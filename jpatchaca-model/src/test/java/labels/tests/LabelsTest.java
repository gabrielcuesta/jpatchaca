package labels.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import labels.LabelsSystem;
import labels.labels.LabelsHome;
import main.TransientNonUIContainer;
import main.TransientNonUiContainerWithTestOperators;

import org.junit.Before;
import org.junit.Test;

import tasks.TaskView;
import tasks.TasksSystem;
import tasks.delegates.CreateTaskDelegateImpl;
import tasks.delegates.CreateTaskDelegate;
import tasks.home.TaskData;
import tasks.tasks.TasksView;
import basic.NonEmptyString;
import basic.Subscriber;
import basic.mock.MockIdProvider;
import core.ObjectIdentity;
import events.CreateTaskEvent;
import events.DeprecatedEvent;
import events.EventsSystem;

public class LabelsTest {

	private LabelsSystem labelsSystem;
	private TasksSystem tasksSystem;
	private EventsSystem eventsSystem;
	private TasksView tasks;
	private CreateTaskDelegate createTaskDelegate;
	private MockIdProvider mockidProvider;
	

	@Before
	public void setUp() {
		
		final TransientNonUIContainer container = new TransientNonUiContainerWithTestOperators();
		container.start();
		
		labelsSystem = container.getComponent(LabelsSystem.class);
		tasksSystem = container.getComponent(TasksSystem.class);
		createTaskDelegate = container.getComponent(CreateTaskDelegateImpl.class);
		tasks = container.getComponent(TasksView.class);
		mockidProvider = container.getComponent(MockIdProvider.class);
		eventsSystem = container.getComponent(EventsSystem.class);
	}
	
	@Test
	public void testLabelsContainsAll(){
		assertEquals(labelsSystem.allLabelName(), labelsSystem.labels().get(0));
	}
	
	@Test
	public void testAssignLabelToTask(){
		final String firstLabelName = "test";
		
		final StringBuffer alertOut = new StringBuffer();
		labelsSystem.labelsListChangedAlert().subscribe(new Subscriber() {
			@Override
			public void fire() {
				alertOut.append("changed");		
			}
		});
		
		final ObjectIdentity taskId = new ObjectIdentity("1");
		final TaskView task = createTask("task name", taskId.getId());
		labelsSystem.setLabelToTask(tasks.get(taskId), firstLabelName);
		assertEquals(task, labelsSystem.tasksInlabel(firstLabelName).get(0));
		assertEquals(1, labelsSystem.tasksInlabel(firstLabelName).size());
		assertEquals(1, labelsSystem.assignableLabels().size());
		assertEquals(firstLabelName, labelsSystem.assignableLabels().get(0));
		assertEquals("changedchanged", alertOut.toString());
		
		
		final ObjectIdentity taskTwoId = new ObjectIdentity("2");
		
		final TaskView taskTwo = createTask("task name", taskTwoId.getId());
		assertEquals(firstLabelName, labelsSystem.assignableLabels().get(0));
		labelsSystem.setLabelToTask(tasks.get(taskTwoId), firstLabelName);
		assertEquals(taskTwo, labelsSystem.tasksInlabel(firstLabelName).get(1));
		assertEquals(2, labelsSystem.tasksInlabel(firstLabelName).size());
		
		final String secondLabelName = "test 2";
		labelsSystem.setLabelToTask(tasks.get(taskTwoId), secondLabelName);
		final List<String> taskTwoLabels = labelsSystem.getLabelsFor(taskTwo);
		assertEquals(firstLabelName, taskTwoLabels.get(0));
		assertEquals(secondLabelName, taskTwoLabels.get(1));
		
	}

	@Test
	public void testRemoveLabelFromTask(){
		final String labelName = "test";
		final ObjectIdentity taskId = new ObjectIdentity("1");
		final TaskView task = createTask("task name", taskId.getId());
		
		labelsSystem.setLabelToTask(tasks.get(taskId), labelName);
		
		labelsSystem.removeLabelFromTask(task, labelName);
		assertEquals(0, labelsSystem.assignableLabels().size());
		
	}
	
	@Test
	public void testCreatedTasksGoToAllLabel(){
		final TaskView task = createTask("task name", "1");
		assertEquals(task, labelsSystem.tasksInlabel(labelsSystem.allLabelName()).get(0));
	}
	
	@Test
	public void testRemovedTasksAreRemovedFromLabels(){
		final TaskView task = createTask("task name", "1");
		labelsSystem.setLabelToTask(task, "label");
		
		tasksSystem.removeTask(task);
		
		assertEquals(0, labelsSystem.tasksInlabel(labelsSystem.allLabelName()).size());
		
		final boolean labelsSystemOnlyContainsAllLabel = 1 == labelsSystem.labels().size();
		assertTrue(labelsSystemOnlyContainsAllLabel);		
		
	}
	
	@Test
	public void testOldCreatedTasksGoToAllLabel() throws DeprecatedEvent{
		final String taskName = "test";
		eventsSystem.writeEvent(new CreateTaskEvent(new ObjectIdentity("1"), taskName));
		assertEquals(taskName, labelsSystem.tasksInlabel(labelsSystem.allLabelName()).get(0).name());
	}
		
	private TaskView createTask(String taskName, String taskId) {	
		mockidProvider.setNextId(taskId);
		TaskData taskData = new TaskData(new NonEmptyString(taskName));
		taskData.setLabel(LabelsHome.ALL_LABEL_NAME);
		createTaskDelegate.createTask(taskData);
		return tasks.get(new ObjectIdentity(taskId));
	}	
	
}
