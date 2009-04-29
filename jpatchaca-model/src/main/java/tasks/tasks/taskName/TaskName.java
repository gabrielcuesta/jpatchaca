package tasks.tasks.taskName;

import org.apache.commons.lang.Validate;

import basic.NonEmptyString;

public class TaskName {

	private final NonEmptyString taskName;

	protected TaskName(final NonEmptyString taskName) {
		Validate.notNull(taskName);
		this.taskName = taskName;
	}

	public String unbox() {
		return taskName.unbox();
	}

	@Override
	public String toString() {
		return taskName.toString();
	}

}
