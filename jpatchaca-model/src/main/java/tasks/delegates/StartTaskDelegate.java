package tasks.delegates;

import tasks.tasks.TaskView;
import ui.swing.mainScreen.Delegate;

public class StartTaskDelegate extends Delegate<TaskView>{

	public void starTask(TaskView task){
		super.execute(task);
	}
	
}
