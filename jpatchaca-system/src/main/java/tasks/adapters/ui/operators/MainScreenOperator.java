package tasks.adapters.ui.operators;

import java.awt.Component;
import java.awt.Point;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import javax.swing.JTable;
import javax.swing.SwingUtilities;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.FastDateFormat;
import org.jdesktop.swingx.JXDatePicker;
import org.netbeans.jemmy.ComponentChooser;
import org.netbeans.jemmy.operators.FrameOperator;
import org.netbeans.jemmy.operators.JButtonOperator;
import org.netbeans.jemmy.operators.JDialogOperator;
import org.netbeans.jemmy.operators.JFrameOperator;
import org.netbeans.jemmy.operators.JListOperator;
import org.netbeans.jemmy.operators.JMenuBarOperator;
import org.netbeans.jemmy.operators.JPopupMenuOperator;
import org.netbeans.jemmy.operators.JTabbedPaneOperator;
import org.netbeans.jemmy.operators.JTableOperator;
import org.netbeans.jemmy.operators.JTextFieldOperator;
import org.netbeans.jemmy.util.RegExComparator;

import tasks.TaskView;

public class MainScreenOperator {

	private final JFrameOperator mainScreen;
	private final JTableOperator periodsTableOperator;
	private final JListOperator tasksListOperator;
	private final JButtonOperator removePeriodsButton;

	public MainScreenOperator() {
		final PopupMenu menu = getTrayIconMenu();
		MainScreenOperator.clickRestoreWindow(menu);
		mainScreen = new JFrameOperator();
		periodsTableOperator = new JTableOperator(mainScreen);
		tasksListOperator = new JListOperator(mainScreen, 1);
		removePeriodsButton = new JButtonOperator(mainScreen, 2);
	}

	private static void clickRestoreWindow(final PopupMenu menu) {

		final ActionListener[] actionListeners = menu
				.getListeners(ActionListener.class);
		final int foo = 42;
		for (final ActionListener listener : actionListeners) {
			listener
					.actionPerformed(new ActionEvent(new Object(), foo, "open"));
		}
	}

	private static PopupMenu getTrayIconMenu() {
		final TrayIcon trayIcon = SystemTray.getSystemTray().getTrayIcons()[0];
		final PopupMenu menu = trayIcon.getPopupMenu();
		return menu;
	}

	public JFrameOperator getMainScreen() {
		return mainScreen;
	}

	private JListOperator getLabelsListOperator() {
		return new JListOperator(mainScreen);
	}

	public void selectLabel(final int i) {
		getLabelsListOperator().selectItem(i);
	}

	public String getSelectedLabel() {
		return (String) getLabelsListOperator().getSelectedValue();
	}

	private void pushCreateTaskMenu() {
		pushMenu("Task/Create task");
	}

	private void pushMenu(final String path) {
		new JMenuBarOperator(mainScreen).pushMenuNoBlock(path, "/");
	}

	public void selectLabel(final String labelName) {
		final JListOperator labelsListOperator = getLabelsListOperator();
		labelsListOperator.waitState(new JListByItemTextFinder(labelName));
		labelsListOperator.selectItem(labelName);

	}

	public boolean isTaskVisible(final String taskName) {
		return getTasksList().findItemIndex(taskName) > -1;
	}

	private JListOperator getTasksList() {
		return new JListOperator(mainScreen, 1);
	}

	public void assignTaskToLabel(final String taskName, final String labelName) {

		selectLabel(0);
		selectTask(taskName);

		assignSelectedTasksToLabel(labelName);

	}

	public void assignSelectedTasksToLabel(final String labelName) {
		clickForPopupInTask(tasksListOperator.getSelectedValue().toString());

		final JPopupMenuOperator popup = new JPopupMenuOperator();

		if (labelExists(labelName)) {
			popup.pushMenuNoBlock("set label to.../" + labelName, "/");
		} else {
			popup.pushMenuNoBlock("set label to.../new label", "/");

			final JDialogOperator dialogOperator = new JDialogOperator();
			new JTextFieldOperator(dialogOperator).setText(labelName);
			new JButtonOperator(dialogOperator).pushNoBlock();
		}
	}

	private void clickForPopupInTask(final String taskName) {

		final JListOperator tasksList = getTasksList();

		final int index = tasksList.getSelectedIndex();
		final Point indexToLocation = tasksList.indexToLocation(index);
		tasksList.clickForPopup((int) indexToLocation.getX(),
				(int) indexToLocation.getY());
	}

	private boolean labelExists(final String labelName) {
		return getLabelsListOperator().findItemIndex(labelName) > -1;
	}

	public void selectTask(final String taskName) {

		tasksListOperator
			.waitState(new JListByItemTextFinder(taskName));
		
		SwingUtilities.invokeLater(new Runnable() {	@Override public void run() {
				tasksListOperator.selectItem(taskName);				
		}});
		
		waitSelectedTask(taskName);
	}

	public void waitSelectedTask(final String taskName) {
		tasksListOperator.waitState(new ComponentChooser() {
			
			@Override
			public String getDescription() {
				return "Waiting for task " + taskName + " to be selected";
			}
			
			@Override
			public boolean checkComponent(Component comp) {
				Object[] tasks = tasksListOperator.getSelectedValues();
				
				for (Object t : tasks){
					if (((TaskView)t).name().equals(taskName)){
						return true;
					}
				}
				
				return false;
			}
		});
	}

	public void pushEditTaskMenu() {
		pushMenu("Task/Edit task");
	}

	public void createTask(final String taskName) {
		pushCreateTaskMenu();
		new TaskScreenOperator().setTaskNameAndOk(taskName);
		waitSelectedTask(taskName);
	}
	
	public void createTaskWithJiraKey(String taskName, String jiraKey) {
		pushCreateTaskMenu();
		TaskScreenOperator taskScreenOperator = new TaskScreenOperator();
		taskScreenOperator.setJiraKey(jiraKey);
		taskScreenOperator.setTaskName(taskName);
		taskScreenOperator.clickOk();
		
		
	}

	public void startTask(final String taskName) {
		selectTask(taskName);
		clickForPopupInTask(taskName);

		final JPopupMenuOperator popup = new JPopupMenuOperator();
		popup.pushMenu("start");
	}

	public String activeTaskName() {

		final FrameOperator.FrameByTitleFinder titleContainsActiveTask = new FrameOperator.FrameByTitleFinder(
				".*Active task: .*", new RegExComparator());
		getMainScreen().waitState(titleContainsActiveTask);
		return StringUtils.substringAfter(getMainScreen().getTitle(),
				"Active task: ");

	}

	public void stopTask() {
		selectTask(activeTaskName());
		clickForPopupInTask(activeTaskName());

		final JPopupMenuOperator popup = new JPopupMenuOperator();
		popup.pushMenu("stop");
		
		
		

	}

	public String getTimeSpentInMillis() {
		final String notZeroNumber = ".*[1-9]?.*";
		final int timeSpentColumn = 3;
		periodsTableOperator.waitState(new JTableOperator.JTableByCellFinder(
				notZeroNumber, 0, timeSpentColumn, new RegExComparator()));

		return (String) periodsTableOperator.getValueAt(0, timeSpentColumn);

	}

	public void editPeriod(final int periodIndex, final String start_HH_mm_a,
			final String stop_HH_mm_a) {
		final JTableOperator periods = new JTableOperator(mainScreen);

		waitPeriodCreated(periodIndex);

		final String startTimeText = getTimeInScreenInputFormat(start_HH_mm_a);
		periods.setValueAt(startTimeText, periodIndex, 1);
		periods.waitCell(startTimeText, periodIndex, 1);

		final String stopTimeText = getTimeInScreenInputFormat(stop_HH_mm_a);
		periods.setValueAt(stopTimeText, periodIndex, 2);
		periods.waitCell(stopTimeText, periodIndex, 2);

	}

	public void waitTimeSpent(final int periodIndex, final long timeSpent) {
		final int timeSpentColumn = 3;
		final NumberFormat format = new DecimalFormat("#0.00");

		final double timeSpentInHours = (double) timeSpent / 60;
		periodsTableOperator.waitCell(format.format(timeSpentInHours),
				periodIndex, timeSpentColumn);

	}

	public void waitTimeSpent(final long timeSpentInMinutes) {
		waitTimeSpent(0, timeSpentInMinutes);
	}

	public void editPeriod(final int periodIndex, final String startHH_mm_a) {

		waitPeriodCreated(periodIndex);
		periodsTableOperator.setValueAt(
				getTimeInScreenInputFormat(startHH_mm_a), periodIndex, 1);

	}

	private void waitPeriodCreated(final int periodIndex) {
		periodsTableOperator.waitState(new ComponentChooser() {
			
			@Override
			public String getDescription() {
				return "Waiting for period to be created on index " + periodIndex;
			}
			
			@Override
			public boolean checkComponent(Component comp) {
				return (periodsTableOperator.getRowCount() > periodIndex);
			}
		});
	}

	private static String getTimeInScreenInputFormat(final String startHH_mm_a) {
		final String pattern = "hh:mm a";
		final SimpleDateFormat hh_mm_aFormater = new SimpleDateFormat(pattern);
		final FastDateFormat screenFormater = FastDateFormat.getTimeInstance(
				FastDateFormat.SHORT, TimeZone.getDefault());
		try {
			return screenFormater.format(hh_mm_aFormater.parse(startHH_mm_a));
		} catch (final ParseException e) {
			throw new RuntimeException("The date " + startHH_mm_a
					+ " is not parseable by " + pattern);
		}
	}

	public void assertActiveTask(final String taskName) {

		String regex = ".*Active task: " + taskName;
		if (taskName == null) {
			regex = "^((?!Active task).)*$";
		}

		final FrameOperator.FrameByTitleFinder titleContainsActiveTask = new FrameOperator.FrameByTitleFinder(
				regex, new RegExComparator());
		getMainScreen().waitState(titleContainsActiveTask);

	}

	public void assertPeriodsCount(final String taskName, final int count) {
		selectTask(taskName);
		periodsTableOperator.waitState(new ComponentChooser() {

			@Override
			public String getDescription() {
				return "Waiting for row count to reach " + count;
			}

			@Override
			public boolean checkComponent(final Component comp) {

				return (((JTable) comp).getRowCount() == 0);
			}
		});

	}

	public void removePeriod(final String taskName, final int i) {
		selectTask(taskName);
		periodsTableOperator.selectCell(i, 0);
		removePeriodsButton.pushNoBlock();
		confirmPeriodsRemoval();
	}

	private static void confirmPeriodsRemoval() {

		final JDialogOperator dialogOperator = new JDialogOperator();
		new JButtonOperator(dialogOperator, "Yes").doClick();
	}

	public void addPeriod(final String taskName) {

		selectTask(taskName);
		final int rowCount = periodsTableOperator.getRowCount();
		new JButtonOperator(mainScreen, 1).doClick();
		waitPeriodCreated(rowCount);

	}

	public void editPeriodDay(final String taskName, final int i,
			final String dateMM_DD_YYYY) {
		selectTask(taskName);

		final int dayColumn = 0;
		final String dateInScreenInputFormat = getDateInScreenInputFormat(dateMM_DD_YYYY);
		periodsTableOperator.setValueAt(dateInScreenInputFormat, i, dayColumn);
		periodsTableOperator.waitCell(dateInScreenInputFormat, i, dayColumn);

	}

	private static String getDateInScreenInputFormat(final String dateMM_DD_YYYY) {
		return getDateString(dateMM_DD_YYYY, "MM_dd_yyyy", FastDateFormat
				.getDateInstance(FastDateFormat.SHORT).getPattern());
	}

	private static String getDateInScreenOutputFormat(final String dateMM_DD_YYYY) {
		return getDateString(dateMM_DD_YYYY, "MM_dd_yyyy", "E "
				+ FastDateFormat.getDateInstance(FastDateFormat.SHORT)
						.getPattern());
	}

	private static String getDateString(final String dateMM_DD_YYYY,
			final String inputPattern, final String outputPattern) {
		Date date = null;
		try {
			date = new SimpleDateFormat(inputPattern).parse(dateMM_DD_YYYY);
		} catch (final ParseException e) {
			throw new RuntimeException("The date " + dateMM_DD_YYYY
					+ " could not be parsed using " + inputPattern);
		}
		final String dateString = new SimpleDateFormat(outputPattern)
				.format(date);
		return dateString;
	}

	public void assertPeriodDay(final String taskName, final int i,
			final String dateMM_DD_YYYY) {
		final int dayColumn = 0;
		periodsTableOperator.waitCell(
				getDateInScreenOutputFormat(dateMM_DD_YYYY), i, dayColumn);
	}

	public void pushOptionsMenu() {
		pushMenu("Task/Options");
	}

	public StartTaskScreenOperator openStartTaskScreen() {
		new JButtonOperator(mainScreen, "Start task").push();
		return new StartTaskScreenOperator();
	}

	public void removePeriods(final int beginIndex, final int endIndex) {
		periodsTableOperator.setRowSelectionInterval(beginIndex, endIndex);
		removePeriodsButton.push();
		confirmPeriodsRemoval();
	}

	public void assertJiraKeyForTask(String jiraKey, String task) {
		TaskScreenOperator taskScreen = editTask(task);
		taskScreen.waitJiraKey(jiraKey);
		
	}

	public void editTaskJiraKey(String taskName, String jiraKey) {
		TaskScreenOperator taskScreen = editTask(taskName);
		
		taskScreen.setJiraKey(jiraKey);
		taskScreen.clickOk();
		
	}
	
	private TaskScreenOperator editTask(String task) {
		selectTask(task);
		pushEditTaskMenu();
		
		TaskScreenOperator taskScreen = new TaskScreenOperator();
		return taskScreen;
	}

	public void sendTodaysWorkLog() {
		new JTabbedPaneOperator(mainScreen).selectPage("Worklog");
		JXDatePicker datePicker = new JXDatePicker();
		DateFormat format = datePicker.getFormats()[0];
		
		JTextFieldOperator textFieldOperator = new JTextFieldOperator(mainScreen);
		textFieldOperator.enterText(format.format(new Date()));
		textFieldOperator.pressKey(KeyEvent.VK_ENTER);
		
		new JTableOperator(mainScreen).selectAll();
		new JButtonOperator(mainScreen,"Send Worklog").push();
		
		new JDialogOperator().pressKey(KeyEvent.VK_ENTER);
	}

	public void selectMultipleTasks(String[] tasks) {
		
		
		for (String t : tasks){
			tasksListOperator
				.waitState(new JListByItemTextFinder(t));
		}
		
		tasksListOperator.selectItem(tasks);

		for (String t : tasks){
			waitSelectedTask(t);
		}
	}
}
