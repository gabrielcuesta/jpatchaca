package ui.swing.tray;

import lang.Maybe;

import org.reactive.Signal;
import org.reactive.Source;

import tasks.TaskView;
import tasks.taskName.TaskName;
import ui.common.ActiveTaskNameCopier;
import ui.swing.tray.PatchacaTrayModelImpl.Listener;

public interface PatchacaTrayModel extends ActiveTaskNameCopier {

	public abstract Source<Maybe<TaskName>> selectedTaskName();

	public abstract void destroyMainScreen();

	public abstract void showMainScreen();

	public abstract void stopTaskIn(final long time);

	public abstract void setListener(final Listener listener);

	public abstract void startTask(final TaskView task, final long timeAgo);

	public abstract Signal<Maybe<TaskName>> activeTaskName();

	public abstract TaskView selectedTask();

	public abstract Signal<String> tooltip();

	public abstract Signal<TaskView> selectedTaskSignal();

	public abstract boolean hasActiveTask();

	public abstract void showStartTaskScreen();

	@Override
	public abstract void copyActiveTaskNameToClipboard();

	public abstract void openActiveTaskOnBrowser();

}