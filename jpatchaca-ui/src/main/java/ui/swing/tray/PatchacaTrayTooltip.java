package ui.swing.tray;

import org.reactivebricks.commons.lang.Maybe;
import org.reactivebricks.pulses.Pulse;
import org.reactivebricks.pulses.Receiver;
import org.reactivebricks.pulses.Signal;
import org.reactivebricks.pulses.Source;

public class PatchacaTrayTooltip {

	private String currentSelectedTask;
	private String currentActiveTask;
	private final Source<String> tooltip;

	public PatchacaTrayTooltip(final Signal<Maybe<String>> activeTaskName,
			final Signal<String> selectedTaskName) {

		tooltip = new Source<String>("");

		activeTaskName.addReceiver(new Receiver<Maybe<String>>() {

			@Override
			public void receive(final Pulse<Maybe<String>> pulse) {
				synchronized (PatchacaTrayTooltip.this) {
					final Maybe<String> value = pulse.value();
					currentActiveTask = (value == null ? "" : pulse.value()
							.unbox());
					updateToolTip();
				}

			}
		});

		selectedTaskName.addReceiver(new Receiver<String>() {

			@Override
			public void receive(final Pulse<String> pulse) {
				synchronized (PatchacaTrayTooltip.this) {
					currentSelectedTask = pulse.value();
					updateToolTip();
				}
			}
		});
	}

	protected void updateToolTip() {

		if (currentActiveTask.equals("")) {
			tooltip.supply("Start task " + currentSelectedTask);
		} else {
			tooltip.supply("Patchaca timer - active: " + currentActiveTask);
		}

	}

	public Signal<String> output() {
		return tooltip;
	}

}
