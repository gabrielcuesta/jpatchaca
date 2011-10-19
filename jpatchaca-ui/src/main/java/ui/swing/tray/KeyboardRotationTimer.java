package ui.swing.tray;

import keyboardRotation.KeyboardRotationOptions;
import ui.swing.presenter.Presenter;

public class KeyboardRotationTimer extends Thread {

	private static final String title = "Timer Alert";
	private static final String message = "Keyboard rotation, turn %s!";
	private final int minutesToWait;
	private static TimerStatus status = TimerStatus.OFF;
	private final Presenter presenter;
	private final KeyboardRotationOptions preferences;
	
	private int turn =0;

	public KeyboardRotationTimer(final int minutesToWait, final Presenter presenter, KeyboardRotationOptions preferences) {
		this.minutesToWait = minutesToWait;
		this.presenter = presenter;
		this.preferences = preferences;
	}

	@Override
	public void run() {
		try {
			synchronized (this) {
				while (status == TimerStatus.ON) {
					wait(minutesToWait * 60000);
					showTurnMessage();
				}
			}
		} catch (final InterruptedException e) {
			System.out.println("Thread has been finalized");
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	public void showTurnMessage() {
		showMessage(getTurnMessage());
	}

	private String getTurnMessage() {
		return String.format(message, turn++);
	}

	private void showMessage(String turnMessage) {
		presenter.showNotification( turnMessage);
		showDialogIfNeeded(turnMessage);
		setOrangeBlueTurn();			
	}

	private void setOrangeBlueTurn() {
		boolean isEven = turn % 2 == 0;
		if (isEven)
			presenter.setOrangeTurn();
		else
			presenter.setBlueTurn();
	}

	private void showDialogIfNeeded(String turnMessage) {
		if (preferences.supressShakingDialog())
			return;
		
		presenter.showShakingMessageWithTitle(turnMessage, title);
	}

	public static TimerStatus getStatus() {
		return status;
	}

	public static void setStatus(final TimerStatus status) {
		KeyboardRotationTimer.status = status;
	}
}