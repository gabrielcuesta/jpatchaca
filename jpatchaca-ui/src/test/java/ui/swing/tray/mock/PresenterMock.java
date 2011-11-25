package ui.swing.tray.mock;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.reactive.Signal;
import org.reactive.Source;

import lang.Maybe;
import ui.swing.presenter.ActionPane;
import ui.swing.presenter.Presenter;
import ui.swing.presenter.UIAction;
import ui.swing.presenter.ValidationException;

public class PresenterMock implements Presenter {

	private StringBuffer operations = new StringBuffer();

	@Override
	public void setMainScreen(JFrame mainScreen) {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public JDialog showOkCancelDialog(ActionPane pane, String title) {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public JDialog showDialog(JDialog dialog) {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void showShakingMessageWithTitle(String message, String title) {
		operations.append("showShakingMessageWithTitle(\"" + message +  "\")\n");
	}

	@Override
	public void closeAllWindows() {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void showYesNoFloatingWindow(String caption, UIAction action)
			throws ValidationException {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void showMessageBalloon(String message) {
		operations .append( "showMessageBalloon(\"" + message + "\")\n" );
	}

	@Override
	public void start() {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void stop() {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public Maybe<JFrame> getMainScreen() {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void showNotification(String message) {
		operations.append("showNotification(\"" + message + "\")\n");
	}
	
	public String getOperations() {
		return operations.toString().trim();
	}

	@Override
	public Signal<String> notification() {
		return new Source<String>("");
	}

	@Override
	public void showPlainDialog(JPanel panel, String title) {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void setOrangeTurn() {
		operations.append("setOrangeTurn()\n");
	}

	@Override
	public void setBlueTurn() {
		operations.append("setBlueTurn()\n");
	}

	@Override
	public Signal<Boolean> isBlueTurn() {
		return new Source<Boolean>(false);
	}

}
