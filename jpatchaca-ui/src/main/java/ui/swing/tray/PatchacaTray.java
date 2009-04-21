package ui.swing.tray;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SplashScreen;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;

import org.apache.commons.lang.time.DateUtils;
import org.picocontainer.Startable;
import org.reactivebricks.commons.lang.Maybe;
import org.reactivebricks.pulses.Pulse;
import org.reactivebricks.pulses.Receiver;

import tasks.tasks.TaskView;
import ui.swing.utils.Whiteboard;
import wheel.io.ui.impl.TrayIconImpl.SystemTrayNotSupported;
import wheel.lang.Threads;
import basic.Alert;
import basic.AlertImpl;

public class PatchacaTray implements Startable {

	private static final String PATCHACA_TIMER = "Patchaca timer";
	private static final String TRAY_ICON_ACTIVE_PATH = "jpon.png";
	private static final Image ACTIVE_ICON = iconImage(TRAY_ICON_ACTIVE_PATH);
	private static final String TRAY_ICON_INACTIVE_PATH = "jpoff.png";
	private static final Image INACTIVE_ICON = iconImage(TRAY_ICON_INACTIVE_PATH);

	static final String START_TASK_SPECIAL = "Start task...";
	static final String STOP_TASK_SCPECIAL = "Stop task...";

	static final String OPEN = "open";
	static final String NEW_TASK = "New task";
	static final String EXIT = "Exit";
	static final String STOP_TASK = "Stop task";

	protected static final long HALF_AN_HOUR = DateUtils.MILLIS_PER_MINUTE * 30;
	protected static final long ONE_HOUR = DateUtils.MILLIS_PER_MINUTE * 60;

	private final AlertImpl stopTaskAlert;
	private final PatchacaTrayModel model;

	private final Whiteboard whiteboard;

	private PopupMenu timerMenu;
	private TrayIcon trayIcon;

	protected AtomicLong lastClicktime = new AtomicLong();
	protected AtomicBoolean isprocessingClick = new AtomicBoolean(false);

	public PatchacaTray(final PatchacaTrayModel model,
			final Whiteboard whiteboard) {
		this.model = model;

		this.whiteboard = whiteboard;
		this.stopTaskAlert = new AlertImpl();

	}

	public void initialize() {

		TrayIcon icon = null;
		try {
			icon = createTrayIcon();
		} catch (final SystemTrayNotSupported e) {
			showMainScreen();
			whiteboard.postMessage("System tray not supported.");
			return;
		}

		final PopupMenu timerMenu = createPopupMenu();

		bindToModel();
		bindTrayicon();

		icon.setPopupMenu(timerMenu);

	}

	private void bindToModel() {
		bindTooltip();
	}

	private void bindTooltip() {

		model.tooltip().addReceiver(new Receiver<String>() {
			public void receive(final Pulse<String> pulse) {
				trayIcon.setToolTip(pulse.value());
			}
		});

	}

	protected void bindTrayicon() {
		final MenuItem stopTaskItem = getMenuItemByText(STOP_TASK);
		final MenuItem stopTaskSpecialItem = getMenuItemByText(STOP_TASK_SCPECIAL);

		model.activeTaskName().addReceiver(new Receiver<Maybe<String>>() {
			@Override
			public void receive(final Pulse<Maybe<String>> pulse) {
				if (pulse.value() == null) {
					trayIcon.setImage(INACTIVE_ICON);
					stopTaskItem.setLabel(STOP_TASK);
					stopTaskItem.setEnabled(false);
					stopTaskSpecialItem.setEnabled(false);
				} else {
					trayIcon.setImage(ACTIVE_ICON);
					stopTaskItem.setLabel(STOP_TASK + " ("
							+ pulse.value().unbox() + ")");
					stopTaskItem.setEnabled(true);
					stopTaskSpecialItem.setEnabled(true);
				}
			}
		});

	}

	private PopupMenu createPopupMenu() {
		this.timerMenu = new PopupMenu("Patchaca");
		this.timerMenu.add(new StartTaskMenu(model.selectedTaskSignal(), model
				.selectedTaskName(), model).getMenu());
		this.timerMenu.add(STOP_TASK);
		this.timerMenu.addSeparator();
		this.timerMenu.add(buildSpecialStopTaskMenu());
		this.timerMenu.addSeparator();
		this.timerMenu.add(OPEN);
		this.timerMenu.add(EXIT);

		this.timerMenu.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent event) {
				if (event.getActionCommand().equals(OPEN)) {
					showMainScreen();
				} else if (event.getActionCommand().equals(EXIT)) {
					System.exit(0);
				} else if (event.getActionCommand().startsWith(STOP_TASK)) {
					PatchacaTray.this.stopTaskAlert.fire();
				}

			}
		});

		getMenuItemByText(STOP_TASK).setEnabled(false);
		getMenuItemByText(STOP_TASK_SCPECIAL).setEnabled(false);

		return this.timerMenu;
	}

	private MenuItem buildSpecialStopTaskMenu() {
		final IntervalMenu specialStopTaskMenu = new IntervalMenu(
				PatchacaTray.STOP_TASK_SCPECIAL,
				new IntervalMenu.IntervalSelectedListener() {
					@Override
					public void intervalClicked(final long millis) {
						model.stopTaskIn(millis);

					}
				});

		return specialStopTaskMenu;
	}

	private TrayIcon createTrayIcon() throws SystemTrayNotSupported {

		if (!SystemTray.isSupported()) {
			throw new SystemTrayNotSupported();
		}

		trayIcon = new TrayIcon(INACTIVE_ICON, PATCHACA_TIMER);
		trayIcon.setImageAutoSize(false);

		trayIcon.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(final MouseEvent e) {
				lastClicktime.set(System.currentTimeMillis());

				if (e.getButton() == MouseEvent.BUTTON2) {
					model.showStartTaskScreen();
				}

				if (e.getButton() != MouseEvent.BUTTON1) {
					return;
				}

				if (!isprocessingClick.getAndSet(true)) {
					new Thread() {
						@Override
						public void run() {
							startStopTaskOnSingleClick(lastClicktime.get());
						}
					}.start();
				}

				if (e.getClickCount() == 2) {
					showMainScreen();
				}

			}

		});

		try {
			tray().add(trayIcon);
		} catch (final AWTException e) {
			throw new RuntimeException(e);
		}

		return trayIcon;
	}

	private static Image iconImage(final String resource) {
		final URL iconURL = PatchacaTray.class.getResource(resource);
		final ImageIcon imageIcon = new ImageIcon(iconURL);
		final Image image = imageIcon.getImage();
		return image;
	}

	private SystemTray tray() {
		return SystemTray.getSystemTray();
	}

	public void start() {
		initialize();
		final SplashScreen splashScreen = SplashScreen.getSplashScreen();
		if (splashScreen != null) {
			splashScreen.close();
		}
	}

	public void stop() {

		if (!SystemTray.isSupported()) {
			return;
		}

		model.destroyMainScreen();
		tray().remove(trayIcon);
	}

	private void showMainScreen() {
		model.showMainScreen();
	}

	private MenuItem getMenuItemByText(final String menuText) {

		for (int i = 0; i < timerMenu.getItemCount(); i++) {
			if (timerMenu.getItem(i).getLabel().startsWith(menuText)) {
				return timerMenu.getItem(i);
			}

		}

		throw new IllegalArgumentException("menu not found");
	}

	public Alert stopTaskAlert() {
		return this.stopTaskAlert;
	}

	protected PopupMenu createNewTaskMenu() {
		final IntervalMenu newTaskMenu = new IntervalMenu(NEW_TASK,
				new IntervalMenu.IntervalSelectedListener() {
					@Override
					public void intervalClicked(final long millis) {
						model.createTaskStarted(millis);
					}
				}, true);

		return newTaskMenu;
	}

	private void startStopTaskOnSingleClick(final long lastClickTime) {
		try {

			Threads.sleepWithoutInterruptions(300);
			final boolean somebodyClickedWhileIWasSleeping = this.lastClicktime
					.get() != lastClickTime;
			if (somebodyClickedWhileIWasSleeping) {
				return;
			}

			if (model.hasActiveTask()) {
				model.stopTaskIn(0);
				trayIcon.setImage(INACTIVE_ICON);
				return;
			}

			final TaskView selectedTask = model.selectedTask();
			if (selectedTask != null) {
				model.startTaskIn(selectedTask, 0);
				trayIcon.setImage(ACTIVE_ICON);
			}

		} finally {
			isprocessingClick.set(false);
		}
	}

	public void statusMessage(final String string) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				trayIcon.displayMessage("Message", string,
						TrayIcon.MessageType.INFO);
			}
		});
	}
}
