package tasks.tasks.tests;


import org.jmock.Mock;
import org.jmock.MockObjectTestCase;

import tasks.TasksListener;
import tasks.tasks.Tasks;
import tasks.tasks.TasksHomeImpl;
import basic.Subscriber;
import basic.mock.MockBasicSystem;
import core.ObjectIdentity;
import events.persistence.MustBeCalledInsideATransaction;

public class TasksHomeTest extends MockObjectTestCase {

	
	private ObjectIdentity oid;
	private String taskName;
	private Double budget;
	private Mock subscriberMocker;
	private Mock tasksListenerMocker;
	private TasksHomeImpl home;
	private Tasks tasks;

	public void setUp(){
		oid = new ObjectIdentity("1");
		taskName = "test";
		budget = 10.0;
		
		subscriberMocker = mock(Subscriber.class);
		tasksListenerMocker = mock(TasksListener.class);
		tasks = new Tasks();
		home = new TasksHomeImpl(null, new MockBasicSystem(), tasks);
	}
	
	
	public void testCreateTask() throws MustBeCalledInsideATransaction {
		
		
		home.taskListChangedAlert().subscribe((Subscriber) subscriberMocker.proxy());
		home.addTasksListener((TasksListener) tasksListenerMocker.proxy());
		
		assertEquals(0, tasks.tasks().size());
		
		tasksListenerMocker.expects(once()).method("createdTask");
		subscriberMocker.expects(once()).method("fire");
		home.createTask(oid, taskName, budget);
		
		assertEquals(1, tasks.tasks().size());
		assertNotNull(tasks.get(oid));
		assertEquals(oid, tasks.idOf((tasks.get(oid))));

	}
	
	public void testRemoveTask() throws MustBeCalledInsideATransaction {

		home.createTask(oid, taskName, budget);
		
		home.taskListChangedAlert().subscribe((Subscriber) subscriberMocker.proxy());
		subscriberMocker.expects(once()).method("fire");
		home.remove(tasks.get(oid));
		
		assertEquals(0, tasks.tasks().size());

	}
	
}
