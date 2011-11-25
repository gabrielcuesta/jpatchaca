package ui.swing.tasks;

import javax.swing.JDialog;

import ui.swing.presenter.Presenter;

public class StartTaskPresenter {

	private final StartTaskScreen startTaskScreen;
	private final Presenter presenter;
	private JDialog dialog;

	public StartTaskPresenter(final Presenter presenter,
			final StartTaskScreen startTaskScreen) {
		this.presenter = presenter;
		this.startTaskScreen = startTaskScreen;
	}

	public void show() {
		if (dialog != null) {
			dialog.setVisible(false);
			dialog.dispose();
		}

		dialog = presenter.showOkCancelDialog(startTaskScreen, "Start task");
	}
}
