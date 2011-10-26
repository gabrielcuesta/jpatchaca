package tasks.processors;

import java.io.Serializable;

import periodsInTasks.PeriodsInTasksHome;
import tasks.TaskView;
import tasks.tasks.Tasks;
import events.Processor;
import events.RemovePeriodEvent;
import events.persistence.MustBeCalledInsideATransaction;

public class RemovePeriodProcessor implements Processor<RemovePeriodEvent> {

	private final PeriodsInTasksHome periodsInTaskHome;
	private final Tasks tasks;

	public RemovePeriodProcessor(final PeriodsInTasksHome periodsInTaskHome, Tasks tasks){
		this.periodsInTaskHome = periodsInTaskHome;
		this.tasks = tasks;		
	}
	
	@Override
	public void execute(final RemovePeriodEvent eventObj) throws MustBeCalledInsideATransaction {
		
		final TaskView taskView = tasks.get(eventObj.getTaskId());
		periodsInTaskHome.removePeriodFromTask(taskView, taskView.getPeriod(eventObj.getPeriodIndex()));
	}

	@Override
	public Class<? extends Serializable> eventType() {
		return RemovePeriodEvent.class;
	}

}
