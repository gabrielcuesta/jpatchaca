package tasks.tasks.tests;

import org.junit.Assert;
import org.junit.Test;

import periodsInTasks.MockTask;
import tasks.tasks.Tasks;
import basic.NonEmptyString;
import core.ObjectIdentity;
import events.persistence.MustBeCalledInsideATransaction;

public class TasksTest {

	@Test
	public void testTaskAddition() throws MustBeCalledInsideATransaction {
		final Tasks tasks = new Tasks();
		final MockTask task = new MockTask("test task");
		final ObjectIdentity oid = new ObjectIdentity("1");
		tasks.add(oid, task);

		Assert.assertTrue(task == tasks.get(oid));
		Assert.assertTrue(task == tasks.byName(new NonEmptyString("test task"))
				.unbox());
		Assert.assertTrue(oid == tasks.idOf(task));
		Assert.assertEquals(task.name(), tasks.taskNames().get(0));
	}

	@Test
	public void testTaskAdditionWithRepeatedName()
			throws MustBeCalledInsideATransaction {
		final Tasks tasks = new Tasks();
		tasks.add(new ObjectIdentity("1"), new MockTask("test task"));
		tasks.add(new ObjectIdentity("2"), new MockTask("test task"));

		Assert.assertEquals("test task_new", tasks.taskNames().get(1));
	}

}
