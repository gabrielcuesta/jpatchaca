package tasks.adapters.ui;

import java.awt.Component;
import java.awt.Point;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JTable;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.FastDateFormat;
import org.netbeans.jemmy.ComponentChooser;
import org.netbeans.jemmy.operators.FrameOperator;
import org.netbeans.jemmy.operators.JButtonOperator;
import org.netbeans.jemmy.operators.JDialogOperator;
import org.netbeans.jemmy.operators.JFrameOperator;
import org.netbeans.jemmy.operators.JListOperator;
import org.netbeans.jemmy.operators.JMenuBarOperator;
import org.netbeans.jemmy.operators.JPopupMenuOperator;
import org.netbeans.jemmy.operators.JTableOperator;
import org.netbeans.jemmy.operators.JTextFieldOperator;
import org.netbeans.jemmy.util.RegExComparator;

public class MainScreenOperator {

	private final JFrameOperator mainScreen;
	private JTableOperator periodsTableOperator;

	public MainScreenOperator() {
		final PopupMenu menu = getTrayIconMenu();
		MainScreenOperator.clickRestoreWindow(menu);
		mainScreen = new JFrameOperator();
		periodsTableOperator = new JTableOperator(mainScreen);
	}

	private static void clickRestoreWindow(PopupMenu menu) {

		final ActionListener[] actionListeners = menu
				.getListeners(ActionListener.class);
		final int foo = 42;
		for (final ActionListener listener : actionListeners)
			listener.actionPerformed(new ActionEvent(new Object(), foo,
					"open"));
	}

	private PopupMenu getTrayIconMenu() {
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

	public void selectLabel(int i) {
		getLabelsListOperator().selectItem(i);
	}

	public String getSelectedLabel() {
		return (String) getLabelsListOperator().getSelectedValue();
	}

	private void pushCreateTaskMenu() {
		pushMenu("File/Create task");
	}

	private void pushMenu(String path) {
		new JMenuBarOperator(mainScreen).pushMenuNoBlock(path,
				"/");
	}

	public void selectLabel(String labelName) {
		final JListOperator labelsListOperator = getLabelsListOperator();
		labelsListOperator.waitState(new JListByItemTextFinder(labelName));
		labelsListOperator.selectItem(labelName);

	}

	public boolean isTaskVisible(String taskName) {
		return getTasksList().findItemIndex(taskName) > -1;
	}

	private JListOperator getTasksList() {
		return new JListOperator(mainScreen, 1);
	}

	public void assignTaskToLabel(String taskName, String labelName) {
		
		getLabelsListOperator().selectItem(0);
		
		clickForPopupInTask(taskName);

		final JPopupMenuOperator popup = new JPopupMenuOperator();

		if (labelExists(labelName))
			popup.pushMenuNoBlock("set label to.../" + labelName, "/");
		else {
			popup.pushMenuNoBlock("set label to.../new label", "/");

			final JDialogOperator dialogOperator = new JDialogOperator();
			new JTextFieldOperator(dialogOperator).setText(labelName);
			new JButtonOperator(dialogOperator).pushNoBlock();
		}

	}

	private void clickForPopupInTask(String taskName) {

		final JListOperator tasksList = getTasksList();
		tasksList.selectItem(taskName);

		int index = tasksList.getSelectedIndex();
		Point indexToLocation = tasksList.indexToLocation(index);
		tasksList.clickForPopup((int)indexToLocation.getX(), (int)indexToLocation.getY());
	}

	private boolean labelExists(String labelName) {
		return getLabelsListOperator().findItemIndex(labelName) > -1;
	}

	public void selectTask(String taskName) {
		final JListOperator tasksListOperator = new JListOperator(mainScreen, 1);
		tasksListOperator.waitState(new JListByItemTextFinder(taskName));
		
		tasksListOperator.selectItem(taskName);
		
		
	}

	public void pushEditTaskMenu() {
		pushMenu("File/Edit task");
	}

	public void createTask(String taskName) {
		pushCreateTaskMenu();
		new TaskScreenOperator().setTaskNameAndOk(taskName);		
	}

	public void startTask(String taskName) {
		clickForPopupInTask(taskName);
		
		final JPopupMenuOperator popup = new JPopupMenuOperator();
		popup.pushMenu("start");
	}

	public String activeTaskName() {
		
		final FrameOperator.FrameByTitleFinder titleContainsActiveTask = new FrameOperator.FrameByTitleFinder(".*Active task: .*", new RegExComparator());
		getMainScreen().waitState(titleContainsActiveTask);
		return StringUtils.substringAfter(getMainScreen().getTitle(), "Active task: ");
		
	}

	public void stopTask() {
		clickForPopupInTask(activeTaskName());
	
		final JPopupMenuOperator popup = new JPopupMenuOperator();
		
	
		popup.pushMenu("stop");
	
	}

	public String getTimeSpentInMillis() {
		final String notZeroNumber = ".*[1-9]?.*";
		final int timeSpentColumn = 3;
		periodsTableOperator.waitState(new JTableOperator.JTableByCellFinder(notZeroNumber, 0,timeSpentColumn,new RegExComparator()));
		
		return (String) periodsTableOperator.getValueAt(0, timeSpentColumn);
		
	}

	public void editPeriod(final int periodIndex, final String start_HH_mm_a, final String stop_HH_mm_a) {
		final JTableOperator periods = new JTableOperator(mainScreen);
		
		periods.setValueAt(getTimeInScreenInputFormat(start_HH_mm_a), periodIndex, 1);
		periods.setValueAt(getTimeInScreenInputFormat(stop_HH_mm_a), periodIndex, 2);
		
	}

	public void waitTimeSpent(int periodIndex, long timeSpent) {
		final int timeSpentColumn = 3;
		NumberFormat format = new DecimalFormat("#0.00");
		
		double timeSpentInHours = (double)timeSpent / 60;
		periodsTableOperator.waitCell(format.format(timeSpentInHours), periodIndex, timeSpentColumn);
		
	}

	public void editPeriod(int periodIndex, String startHH_mm_a) {
		final JTableOperator periods = new JTableOperator(mainScreen);
		periods.setValueAt(getTimeInScreenInputFormat(startHH_mm_a), periodIndex, 1);
		
	}

	private String getTimeInScreenInputFormat(String startHH_mm_a) {
		String pattern = "hh:mm a";
		SimpleDateFormat hh_mm_aFormater = new SimpleDateFormat(pattern);
		FastDateFormat screenFormater = FastDateFormat.getTimeInstance(FastDateFormat.SHORT);
		try {
			return screenFormater.format(hh_mm_aFormater.parse(startHH_mm_a));
		} catch (ParseException e) {
			throw new RuntimeException("The date " + startHH_mm_a + " is not parseable by " + pattern);
		}
	}

	public void assertActiveTask(String taskName) {
		
		String regex = ".*Active task: " + taskName;
		if (taskName == null)
			regex = "^((?!Active task).)*$";

		final FrameOperator.FrameByTitleFinder titleContainsActiveTask = new FrameOperator.FrameByTitleFinder(regex, new RegExComparator());
		getMainScreen().waitState(titleContainsActiveTask);

		
	}

	public void assertPeriodsCount(String taskName, final int count) {
		selectTask(taskName);
		periodsTableOperator.waitState(new ComponentChooser() {
		
			@Override
			public String getDescription() {
				return "Waiting for row count to reach " + count;
			}
		
			@Override
			public boolean checkComponent(Component comp) {
				
				return (((JTable)comp).getRowCount() == 0);
			}
		});
		
	}

	public void removePeriod(String taskName, int i) {
		selectTask(taskName);
		periodsTableOperator.selectCell(i, 0);
		new JButtonOperator(mainScreen, "remove").doClick();
	}

	public void addPeriod(String taskName) {
		selectTask(taskName);
		new JButtonOperator(mainScreen, "add").doClick();
	}

	public void editPeriodDay(String taskName, int i, String dateMM_DD_YYYY) {
		selectTask(taskName);

		int dayColumn = 0;
		periodsTableOperator.setValueAt(getDateInScreenInputFormat(dateMM_DD_YYYY), i, dayColumn);
		
	}

	private String getDateInScreenInputFormat(String dateMM_DD_YYYY) {
		return getDateString(dateMM_DD_YYYY, "MM_dd_yyyy", FastDateFormat
				.getDateInstance(FastDateFormat.SHORT).getPattern() );
	}
	
	private String getDateInScreenOutputFormat(String dateMM_DD_YYYY) {
		return getDateString(dateMM_DD_YYYY, "MM_dd_yyyy", "E " + FastDateFormat
				.getDateInstance(FastDateFormat.SHORT).getPattern());
	}

	private String getDateString(String dateMM_DD_YYYY, String inputPattern, String outputPattern) {
		Date date  = null;
		try {
			date = new SimpleDateFormat(inputPattern).parse(dateMM_DD_YYYY);
		} catch (ParseException e) {
			throw new RuntimeException("The date " + dateMM_DD_YYYY + " could not be parsed using " + inputPattern);
		}
		String dateString = new SimpleDateFormat(outputPattern).format(date);
		return dateString;
	}

	public void assertPeriodDay(String taskName, int i, String dateMM_DD_YYYY) {
		int dayColumn = 0;
		periodsTableOperator.waitCell(getDateInScreenOutputFormat(dateMM_DD_YYYY), i, dayColumn);		
	}

	public void pushOptionsMenu() {
		pushMenu("File/Options");		
	}



	

}
