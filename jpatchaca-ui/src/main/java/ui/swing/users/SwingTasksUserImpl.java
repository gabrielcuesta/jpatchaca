package ui.swing.users;

import javax.swing.JOptionPane;

import tasks.tasks.TaskView;
import ui.swing.mainScreen.TaskList;
import ui.swing.mainScreen.tasks.TaskExclusionScreen;
import basic.UserOperationCancelledException;

public class SwingTasksUserImpl implements SwingTasksUser {

	private final TaskList tasksList;

	private final TaskExclusionScreen exclusionScreen;

	//bug: remove all references to tasklist from here!

	public SwingTasksUserImpl(final TaskList list,
			final TaskExclusionScreen exclusionScreen) {
		this.tasksList = list;
		this.exclusionScreen = exclusionScreen;
	}

	public boolean isTaskExclusionConfirmed() {

		final int answer = exclusionScreen.confirmExclusion();

		return answer == JOptionPane.YES_OPTION;
	}

	public TaskView getPeriodMovingTarget() {
		return tasksList.dropTargetTask();
	}

	public boolean isPeriodExclusionConfirmed() {
		return JOptionPane.showConfirmDialog(null,
				"Do you realy want to remove the selected period?",
				"Period Removal Confirmation", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
	}

	public String getTextForNote() throws UserOperationCancelledException {

		final String noteText = JOptionPane.showInputDialog("Enter a note");

		if (noteText == null) {
			throw new UserOperationCancelledException();
		}

		return noteText;
	}

}
