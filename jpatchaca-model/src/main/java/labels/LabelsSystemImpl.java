package labels;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import labels.labels.LabelsHome;
import labels.labels.LabelsHomeImpl;
import labels.labels.LabelsHomeView;
import labels.processors.CreateTaskProcessor3;
import labels.processors.RemoveLabelFromMultipleTasksProcessor;
import labels.processors.RemoveTaskFromLabelProcessor;
import labels.processors.SetLabelToMultipleTasksProcessor;
import labels.processors.SetLabelToTaskProcessor;
import labels.processors.SetSelectedLabelProcessor;

import org.apache.commons.lang.Validate;
import org.picocontainer.Startable;

import tasks.TaskView;
import tasks.TasksListener;
import tasks.TasksSystem;
import tasks.tasks.TasksView;
import basic.Alert;
import events.EventsSystem;
import events.RemoveLabelFromMultipleTasks;
import events.RemoveTaskFromLabelEvent;
import events.SetLabelToMultipleTasks;
import events.SetLabelToTaskEvent;

public class LabelsSystemImpl implements LabelsSystem, Startable {

	private final LabelsHomeView labelsHomeView;
	private final EventsSystem eventsSystem;
	private final TasksView tasks;

	public LabelsSystemImpl(final EventsSystem eventsSystem, final TasksSystem tasksSystem, TasksView tasks) {

		this.tasks = tasks;
		final LabelsHome labelsHome = new LabelsHomeImpl();
		
		this.eventsSystem = eventsSystem;
		this.labelsHomeView = labelsHome;
		
		eventsSystem.addProcessor(new SetLabelToTaskProcessor(tasks, labelsHome));
		eventsSystem.addProcessor(new RemoveTaskFromLabelProcessor(labelsHome, tasks));
		eventsSystem.addProcessor(new SetSelectedLabelProcessor());
		eventsSystem.addProcessor(new CreateTaskProcessor3(labelsHome, tasks));
		eventsSystem.addProcessor(new SetLabelToMultipleTasksProcessor(labelsHome, tasks));
		eventsSystem.addProcessor(new RemoveLabelFromMultipleTasksProcessor(labelsHome, tasks));
		
		tasksSystem.addTasksListener(new TasksListener() {
			@Override
			public void createdTask(final TaskView task) {
				labelsHome.setLabelToTask(task, allLabelName());
			}

			@Override
			public void removedTask(final TaskView task) {
				final List<String> assignedLabels = labelsHome.getLabelsFor(task);
				for (final String label : assignedLabels){
					labelsHome.removeTaskFromLabel(task, label);
				}
				labelsHome.removeTaskFromLabel(task, allLabelName());
			}
		});
		
	}

	@Override
	public void setLabelToTask(final TaskView task, final String labeltoAssignTo) {
			Validate.notNull(task);
			Validate.notNull(labeltoAssignTo);
			final SetLabelToTaskEvent event = new SetLabelToTaskEvent(tasks.idOf(task), 
					labeltoAssignTo);
			this.eventsSystem.writeEvent(event);
	}
	
	@Override
	public void setLabelToMultipleTasks(String labelToAssignTaskTo,
			Set<TaskView> selectedTasks) {
		SetLabelToMultipleTasks event = new SetLabelToMultipleTasks(labelToAssignTaskTo, selectedTasks.toArray(new TaskView[0]));
		this.eventsSystem.writeEvent(event);
	}

	@Override
	public void removeLabelFromTask(final TaskView task, final String labelToAssignTo) {
		final RemoveTaskFromLabelEvent event = new RemoveTaskFromLabelEvent(tasks.idOf(task),
				labelToAssignTo);
		this.eventsSystem.writeEvent(event);	
	}
	
	@Override
	public void removeMultipleTasksFromLabel(String label,
			Set<TaskView> tasks) {
		final RemoveLabelFromMultipleTasks event = new RemoveLabelFromMultipleTasks(label, tasks);
		this.eventsSystem.writeEvent(event);	
	}



	@Override
	public List<TaskView> tasksInlabel(final String labelName) {
		return new ArrayList<TaskView>(labelsHomeView.getTasksInLabel(labelName));
	}



	@Override
	public String allLabelName() {
		return labelsHomeView.allLabelName();
	}


	@Override
	public List<String> labels() {
		return labelsHomeView.labels();
	}


	@Override
	public List<String> getLabelsFor(final TaskView task) {
		return labelsHomeView.getLabelsFor(task);
	}

	@Override
	public Alert labelsListChangedAlert() {
		return labelsHomeView.labelsListChangedAlert();
	}

	@Override
	public List<String> assignableLabels() {
		return labelsHomeView.assignableLabels();
	}

	@Override
	public Alert tasksInLabelChangedAlert() {
		return labelsHomeView.tasksInLabelChangedAlert();
	}

	@Override
	public void start() {}

	@Override
	public void stop() {}

}
