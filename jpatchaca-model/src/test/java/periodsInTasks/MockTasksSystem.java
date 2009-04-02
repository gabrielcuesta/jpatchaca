package periodsInTasks;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.reactivebricks.pulses.Signal;

import periods.Period;
import tasks.TasksListener;
import tasks.TasksSystem;
import tasks.tasks.TaskData;
import tasks.tasks.TaskView;
import tasks.tasks.TasksHome;
import basic.Alert;
import core.ObjectIdentity;
import events.persistence.MustBeCalledInsideATransaction;

public class MockTasksSystem implements TasksSystem {

	@Override
	public TaskView activeTask() {
		throw new RuntimeException("not implemented");
	}

	@Override
	public Alert activeTaskChangedAlert() {
		throw new RuntimeException("not implemented");
	}

	@Override
	public String activeTaskName() {
		throw new RuntimeException("not implemented");
	}

	@Override
	public Signal<String> activeTaskNameSignal() {
		throw new RuntimeException("not implemented");
	}

	@Override
	public void addNoteToTask(final TaskView task, final String text) {
		throw new RuntimeException("not implemented");
	}

	@Override
	public void addPeriodToTask(final ObjectIdentity taskId, final Period period)
			throws MustBeCalledInsideATransaction {
		
	}

	@Override
	public void addTasksListener(final TasksListener tasksListener) {
		throw new RuntimeException("not implemented");
	}

	@Override
	public void createAndStartTaskIn(final TaskData newTaskData, final long in) {
		throw new RuntimeException("not implemented");
	}

	@Override
	public void createTask(final TaskData data) {
		throw new RuntimeException("not implemented");
	}

	@Override
	public void editPeriod(final TaskView task, final int periodIndex, final Period newPeriod) {
		throw new RuntimeException("not implemented");
	}

	@Override
	public void editTask(final TaskView task, final TaskData taskData) {
		throw new RuntimeException("not implemented");
	}

	@Override
	public ObjectIdentity getIdOfTask(final TaskView activeTask) {
		throw new RuntimeException("not implemented");
	}

	@Override
	public TaskView getTaskView(final ObjectIdentity taskId) {
		throw new RuntimeException("not implemented");
	}

	@Override
	public Collection<TaskView> lastActiveTasks() {
		throw new RuntimeException("not implemented");
	}

	@Override
	public Alert lastActiveTasksAlert() {
		throw new RuntimeException("not implemented");
	}

	@Override
	public void movePeriod(final TaskView taskFrom, final TaskView taskTo, final int periodFrom) {
		throw new RuntimeException("not implemented");
	}

	@Override
	public void removePeriod(final TaskView task, final Period period) {

	}

	@Override
	public void removeTask(final TaskView task) {
		throw new RuntimeException("not implemented");
	}

	@Override
	public void setPeriod(final TaskView task, final int i, final Date startTime, final Date endTime) {
		throw new RuntimeException("not implemented");
	}

	@Override
	public void setPeriodEnding(final TaskView task, final int periodIndex, final Date endDate) {
		throw new RuntimeException("not implemented");
	}

	@Override
	public void setPeriodStarting(final TaskView task, final int periodIndex, final Date startDate) {
		throw new RuntimeException("not implemented");
	}

	@Override
	public void stopIn(final long millisAhead) {
		throw new RuntimeException("not implemented");
	}

	@Override
	public void stopTask(final TaskView task) {
		throw new RuntimeException("not implemented");
	}

	@Override
	public Alert taskListChangedAlert() {
		throw new RuntimeException("not implemented");
	}

	@Override
	public void taskStarted(final TaskView selectedTask, final long millisAgo) {
		throw new RuntimeException("not implemented");
	}

	@Override
	public List<TaskView> tasks() {
		throw new RuntimeException("not implemented");
	}

	@Override
	public TasksHome tasksHome() {
		throw new RuntimeException("not implemented");
	}


}
