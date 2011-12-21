package ui.swing.mainScreen.tasks;

import tasks.TaskView;
import tasks.TasksSystem;
import tasks.delegates.CreateTaskdelegate;
import tasks.home.TaskData;
import ui.swing.tasks.SelectedTaskSource;

public class TaskScreenModelImpl implements TaskScreenModel {

	private final TasksSystem taskSystem;
	private final SelectedTaskSource selectedTask;
	private final CreateTaskdelegate createTaskDelegate;

	public TaskScreenModelImpl(final TasksSystem taskSystem,
			final SelectedTaskSource selectedTask,
			final CreateTaskdelegate createTaskDelegate) {
		this.taskSystem = taskSystem;
		this.selectedTask = selectedTask;
		this.createTaskDelegate = createTaskDelegate;
	}

	@Override
	public void createTask(final TaskData data) {		
		createTaskDelegate.createTask(data);
	}

	@Override
	public TaskView selectedTask() {
		return selectedTask.currentValue();
	}

	@Override
	public void editTask(final TaskView taskView, final TaskData data) {
		taskSystem.editTask(taskView, data);
		selectedTask.supply(taskView);

	}

}
