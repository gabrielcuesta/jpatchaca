package ui.swing.tray;

import org.picocontainer.Startable;

import tasks.TasksSystem;
import ui.swing.users.SwingTasksUser;
import basic.Subscriber;

public class PatchacaTrayTasksFacadeMediator implements Startable {

	private final PatchacaTray tray;
	private final TasksSystem tasksSystem;

	public PatchacaTrayTasksFacadeMediator(final PatchacaTray tray,
			final TasksSystem tasksSystem, final SwingTasksUser tasksUser) {
		this.tray = tray;
		this.tasksSystem = tasksSystem;

		this.tray.stopTaskAlert().subscribe(new Subscriber() {
			public void fire() {
				PatchacaTrayTasksFacadeMediator.this.tasksSystem.stopTask();
			}
		});

	}

	public void stop() {

	}

	public void taskListChanged() {
	}

	@Override
	public void start() {
	}

}
