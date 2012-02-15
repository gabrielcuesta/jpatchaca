package tasks;

import java.util.Calendar;
import java.util.Date;

import jira.events.JiraEventFactory;
import jira.events.SetJiraIssueToTask;
import lang.Maybe;

import org.apache.commons.lang.Validate;
import org.picocontainer.Startable;

import periods.Period;
import periods.PeriodsFactory;
import tasks.delegates.StartTaskData;
import tasks.delegates.StartTaskDelegate;
import tasks.home.TaskData;
import tasks.home.TasksHome;
import tasks.notes.NotesHome;
import tasks.notes.NotesHomeImpl;
import tasks.processors.AddNoteToTaskProcessor;
import tasks.processors.CreateTaskProcessor;
import tasks.processors.CreateTaskProcessor3;
import tasks.processors.EditPeriodProcessor;
import tasks.processors.EditPeriodProcessor2;
import tasks.processors.EditTaskProcessor;
import tasks.processors.MovePeriodProcessor;
import tasks.processors.RemoveTaskProcessor;
import tasks.processors.RenameTaskProcessor;
import tasks.processors.StopTaskProcessor;
import tasks.taskName.TaskNameFactory;
import tasks.tasks.Tasks;
import basic.Alert;
import basic.NonEmptyString;
import basic.SystemClock;
import core.ObjectIdentity;
import events.AddNoteToTaskEvent;
import events.EditPeriodEvent2;
import events.EditTaskEvent;
import events.EventsSystem;
import events.MovePeriodEvent;
import events.RemoveTaskEvent;
import events.StopTaskEvent;
import events.persistence.MustBeCalledInsideATransaction;

public class TasksSystemImpl implements TasksSystem, Startable {

	private final TasksHome tasksHome;
	private final EventsSystem eventsSystem;
	private final StartTaskDelegate startTaskDelegate;
	private final Tasks tasks;
	private final ActiveTask activeTask;
	private final JiraEventFactory jiraEventFactory;

	public TasksSystemImpl(final TasksHome tasksHome,
			final EventsSystem eventsSystem,
			final PeriodsFactory periodsFactory,
			final StartTaskDelegate startTaskDelegate, final Tasks tasks,
			final SystemClock clock, final ActiveTask activeTask,
			final TaskNameFactory taskNameFactory,
			JiraEventFactory jiraEventFactory) {
		this.eventsSystem = eventsSystem;
		this.tasksHome = tasksHome;
		this.startTaskDelegate = startTaskDelegate;
		this.tasks = tasks;
		this.activeTask = activeTask;
		this.jiraEventFactory = jiraEventFactory;

		final NotesHome notesHome = new NotesHomeImpl(clock);

		eventsSystem.addProcessor(new CreateTaskProcessor(tasksHome,
				taskNameFactory));
		eventsSystem.addProcessor(new CreateTaskProcessor3(tasksHome,
				taskNameFactory));
		eventsSystem.addProcessor(new EditPeriodProcessor(tasks));
		eventsSystem.addProcessor(new EditPeriodProcessor2(tasksHome, tasks));
		eventsSystem.addProcessor(new EditTaskProcessor(tasksHome));
		eventsSystem.addProcessor(new MovePeriodProcessor(tasksHome));
		eventsSystem.addProcessor(new RemoveTaskProcessor(tasks, tasksHome));
		eventsSystem.addProcessor(new RenameTaskProcessor(tasksHome));
		eventsSystem.addProcessor(new StopTaskProcessor(tasksHome));
		eventsSystem.addProcessor(new AddNoteToTaskProcessor(tasksHome,
				notesHome));

	}

	@Override
	public synchronized void editTask(final TaskView taskView,
			final TaskData taskData) {

		final ObjectIdentity taskId = tasks.idOf(taskView);
		final EditTaskEvent event = new EditTaskEvent(taskId,
				taskData.getTaskName(), taskData.getBudget());

		SetJiraIssueToTask setIssueEvent = jiraEventFactory
				.createSetIssueToTaskEvent(taskId, taskData.getJiraIssue());
		
		this.eventsSystem.writeEvent(setIssueEvent);
		this.eventsSystem.writeEvent(event);
	}

	@Override
	public synchronized void editPeriod(final TaskView selectedTask,
			final int periodIndex, final Period newPeriod) {

		final EditPeriodEvent2 event = new EditPeriodEvent2(
				tasks.idOf(selectedTask), periodIndex, newPeriod.startTime(),
				newPeriod.endTime());
		this.eventsSystem.writeEvent(event);

	}

	@Override
	public synchronized void removeTask(final TaskView task) {
		final RemoveTaskEvent event = new RemoveTaskEvent(tasks.idOf(task));
		this.eventsSystem.writeEvent(event);
	}

	@Override
	public synchronized void addNoteToTask(final TaskView task,
			final String text) {
		this.eventsSystem.writeEvent(new AddNoteToTaskEvent(tasks.idOf(task),
				text));
	}

	@Override
	public synchronized void movePeriod(final TaskView taskFrom,
			final TaskView taskTo, final int periodFrom) {

		Validate.isTrue(periodFrom > -1);
		final MovePeriodEvent event = new MovePeriodEvent(tasks.idOf(taskFrom),
				periodFrom, tasks.idOf(taskTo));
		this.eventsSystem.writeEvent(event);
	}

	public synchronized void stopTask(final TaskView task) {
		this.eventsSystem.writeEvent(new StopTaskEvent(tasks.idOf(task)));
	}

	@Override
	public synchronized void addTasksListener(final TasksListener tasksListener) {
		tasksHome.addTasksListener(tasksListener);
	}

	@Override
	public synchronized Alert taskListChangedAlert() {
		return tasksHome.taskListChangedAlert();
	}

	@Override
	public synchronized void start() {
		// TODO Auto-generated method stub
	}

	@Override
	public synchronized void stop() {
		// TODO Auto-generated method stub
	}

	@Override
	public synchronized void stopIn(final long millisAhead) {
		final Maybe<Task> currentActiveTask = activeTask.currentValue();

		if (currentActiveTask == null) {
			throw new IllegalStateException("ActiveTask must not be null");
		}

		final TaskView task = currentActiveTask.unbox();

		if (task == null) {
			throw new IllegalStateException();
		}

		stopTask(task);
		final Period lastPeriod = task.lastPeriod();
		final Period alteredPeriod = lastPeriod.createNewEnding(new Date(
				lastPeriod.endTime().getTime() + millisAhead));
		editPeriod(task, task.lastPeriodIndex(), alteredPeriod);
	}

	@Override
	public synchronized void taskStarted(final TaskView task,
			final long millisAgo) {
		final String name = task.name();
		if (name.isEmpty()) {
			return;
		}

		startTaskDelegate.starTask(new StartTaskData(new TaskData(
				new NonEmptyString(name)), 0));

		final Period lastPeriod = task.lastPeriod();
		final Period alteredPeriod = lastPeriod.createNewStarting(new Date(
				lastPeriod.startTime().getTime() - millisAgo));
		editPeriod(task, task.lastPeriodIndex(), alteredPeriod);
	}

	@Override
	public synchronized void setPeriodEnding(final TaskView task,
			final int periodIndex, final Date endDate) {

		final Date endDateAdjusted = getEndTime(task.getPeriod(periodIndex)
				.startTime(), endDate);
		editPeriod(task, periodIndex, task.getPeriod(periodIndex)
				.createNewEnding(endDateAdjusted));
	}

	@Override
	public synchronized void setPeriodStarting(final TaskView task,
			final int periodIndex, final Date startDate) {
		if (periodIndex == -1) {
			throw new IllegalArgumentException("Index must be positive");
		}
		final Period period = task.getPeriod(periodIndex);
		editPeriod(task, periodIndex, period.createNewStarting(startDate));
	}

	private synchronized Date getEndTime(final Date startDate,
			final Date endDate) {
		if (startDate.after(endDate)) {
			return datePlusOneDay(endDate);
		}

		return endDate;
	}

	private synchronized Date datePlusOneDay(final Date insertedDate) {
		final Calendar cal = Calendar.getInstance();
		cal.setTime(insertedDate);
		cal.add(Calendar.DAY_OF_MONTH, 1);
		return cal.getTime();
	}

	@Override
	public void setPeriod(final TaskView task, final int i,
			final Date adjustedStartTime, final Date adjustedEndTime) {
		final Period period = task.getPeriod(i);
		editPeriod(task, i, period.createNewStarting(adjustedStartTime)
				.createNewEnding(adjustedEndTime));

	}

	@Override
	public void addPeriodToTask(final ObjectIdentity taskId, final Period period)
			throws MustBeCalledInsideATransaction {
		tasksHome.addPeriodToTask(taskId, period);
	}

	@Override
	public void removePeriod(final TaskView task, final Period period)
			throws MustBeCalledInsideATransaction {
		tasksHome.removePeriodFromTask(task, period);
	}

	@Override
	public void stopTask() {
		this.stopIn(0);
	}

}
