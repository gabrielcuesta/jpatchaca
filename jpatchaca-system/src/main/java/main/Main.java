package main;

import java.io.IOException;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import labels.LabelsSystemImpl;
import localization.BrazilDaylightSavingTimezoneAdjuster;

import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoBuilder;

import periods.impl.PeriodsFactoryImpl;
import periodsInTasks.impl.PeriodsInTasksSystemImpl;
import statistics.ProjectVelocityCalculatorImpl;
import statistics.TaskSummarizerImpl;
import tasks.TasksSystemImpl;
import tasks.delegates.StartTaskDelegate;
import tasks.persistence.StartTaskPersistence;
import tasks.tasks.TasksHomeImpl;
import twitter.TwitterOptions;
import twitter.TwitterLogger;
import twitter.processors.SetTwitterConfigProcessor;
import ui.swing.mainScreen.LabelsList;
import ui.swing.mainScreen.LabelsListSystemMediator;
import ui.swing.mainScreen.MainScreen;
import ui.swing.mainScreen.MainScreenImpl;
import ui.swing.mainScreen.TaskContextMenu;
import ui.swing.mainScreen.TaskContextMenuSystemMediator;
import ui.swing.mainScreen.TaskList;
import ui.swing.mainScreen.TaskListSystemMediator;
import ui.swing.mainScreen.TopBar;
import ui.swing.mainScreen.periods.PeriodsList;
import ui.swing.mainScreen.periods.PeriodsTableModel;
import ui.swing.mainScreen.tasks.TaskExclusionScreen;
import ui.swing.mainScreen.tasks.TaskScreen;
import ui.swing.mainScreen.tasks.WindowManager;
import ui.swing.mainScreen.tasks.summary.SummaryScreen;
import ui.swing.options.OptionsScreen;
import ui.swing.options.OptionsScreenModelImpl;
import ui.swing.tray.PatchacaTray;
import ui.swing.tray.PatchacaTrayModelImpl;
import ui.swing.tray.PatchacaTrayTasksFacadeMediator;
import ui.swing.users.SwingTasksUserImpl;
import ui.swing.users.SwinglabelsUser;
import ui.swing.utils.Whiteboard;
import wheel.io.files.impl.tranzient.TransientDirectory;
import wheel.io.ui.impl.DeferredDirectoryBoundPersistence;
import wheel.io.ui.impl.JFrameBoundsKeeperImpl;
import basic.DeferredExecutor;
import basic.FormatterImpl;
import basic.HardwareClock;
import basic.PatchacaDirectory;
import basic.durable.DurableBasicSystem;
import basic.durable.HardwareClockImpl;
import basic.mock.MockHardwareClock;
import events.EventsSystemImpl;

public class Main {
	private static MutablePicoContainer container;

	public static void main(final String[] args) throws UnsupportedLookAndFeelException, IOException {
		System.out.println(System.getProperty("java.class.path") );
		
		System.setProperty("sun.awt.keepWorkingSetOnMinimize", "true");
		
		try {
			UIManager.setLookAndFeel(new com.sun.java.swing.plaf.windows.WindowsLookAndFeel());
		} catch (final UnsupportedLookAndFeelException e) {	
		}	
		
		final MutablePicoContainer container = createDurableSWINGContainer();
		container.start();	
	}
	
	private static MutablePicoContainer createSwingContainer(final HardwareClock clock) {
		final MutablePicoContainer container = createNonUIContainer(clock);
		final MutablePicoContainer swingContainer = container.makeChildContainer();
		
		registerSWINGStuff(swingContainer);
		return container;
	}
	
	private static MutablePicoContainer createNonUIContainer(final HardwareClock hardwareClock) {
		final MutablePicoContainer container = 
			new PicoBuilder()
				.withConstructorInjection()
				.withLifecycle()
				.withCaching()
			.build();
		
		container.addComponent(PathcacaDefaultExceptionHandler.class);
		
		container.addComponent(BrazilDaylightSavingTimezoneAdjuster.class);
		container.addComponent(hardwareClock);
		container.addComponent(PatchacaDirectory.class);
		container.addComponent(DurableBasicSystem.class);
		container.addComponent(PeriodsFactoryImpl.class);
		container.addComponent(EventsSystemImpl.class);
		container.addComponent(TasksHomeImpl.class);
		container.addComponent(TasksSystemImpl.class);
		container.addComponent(StartTaskDelegate.class);
		container.addComponent(TwitterOptions.class);
		container.addComponent(TwitterLogger.class);
		container.addComponent(SetTwitterConfigProcessor.class);
		container.addComponent(StartTaskPersistence.class);
		
		container.addComponent(PeriodsInTasksSystemImpl.class);
		container.addComponent(LabelsSystemImpl.class);
		registerModelStuff(container);
		
		return container;
	}

	private static void registerSWINGStuff(final MutablePicoContainer container) {
		container.addComponent(DeferredDirectoryBoundPersistence.class);
		container.addComponent(JFrameBoundsKeeperImpl.class);
		
		container.addComponent(WindowManager.class);
		container.addComponent(Whiteboard.class);
		
		container.addComponent(SwingTasksUserImpl.class);
		
		container.addComponent(PatchacaTray.class);
		container.addComponent(PatchacaTrayModelImpl.class);
		container.addComponent(PatchacaTrayTasksFacadeMediator.class);
		
		container.addComponent(MainScreen.class, MainScreenImpl.class);
		container.addComponent(OptionsScreen.class);
		container.addComponent(OptionsScreenModelImpl.class);
		container.addComponent(SwinglabelsUser.class);
		container.addComponent(TaskList.class);
		container.addComponent(TaskListSystemMediator.class);
		container.addComponent(SummaryScreen.class);
		container.addComponent(TopBar.class);
		container.addComponent(PeriodsTableModel.class);
		container.addComponent(PeriodsList.class);
		container.addComponent(TaskScreen.class);
		container.addComponent(TaskContextMenu.class);
		container.addComponent(TaskContextMenuSystemMediator.class);
		container.addComponent(LabelsList.class);
		container.addComponent(LabelsListSystemMediator.class);
		container.addComponent(TaskExclusionScreen.class);
		
		
	}

	private static void registerModelStuff(final MutablePicoContainer container2) {
		container2.addComponent(ProjectVelocityCalculatorImpl.class);
		container2.addComponent(TaskSummarizerImpl.class);	
		container2.addComponent(FormatterImpl.class);
	}
	
	public static MutablePicoContainer createDurableSWINGContainer(){
		if (container == null){
				container = createSwingContainer(new HardwareClockImpl());
		}
		return container;
	}
	
	public static MutablePicoContainer createTransientNonUIContainer() {
		final MutablePicoContainer nonUIContainer = createNonUIContainer(new HardwareClockImpl());
		nonUIContainer.addComponent(new TransientDirectory());
		return nonUIContainer ;
	}

	public static MutablePicoContainer createSWINGContainerForTests(
			final HardwareClock hardwareClock) {
		
		DeferredExecutor.makeSynchronous();
		final MutablePicoContainer container = createNonUIContainer(hardwareClock);
		container.removeComponent(PatchacaDirectory.class);
		container.addComponent(new TransientDirectory());
		registerSWINGStuff(container);
		
		return container;
		
	}

	public static MutablePicoContainer createTransientNonUIContainer(
			final MockHardwareClock mockHardwareClock) {
		final MutablePicoContainer nonUIContainer = createNonUIContainer(mockHardwareClock);
		nonUIContainer.addComponent(new TransientDirectory());
		return nonUIContainer;
	}
	
	

}
