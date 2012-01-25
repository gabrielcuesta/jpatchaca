package labels;

import java.util.List;
import java.util.Set;

import tasks.TaskView;
import basic.Alert;

public interface LabelsSystem{
	
	public void setLabelToTask(TaskView task, String labelToAssignTo);
	public void removeLabelFromTask(TaskView task, String labelToRemoveFrom);
	public List<TaskView> tasksInlabel(String labelName);
	public String allLabelName();
	public List<String> labels();
	public List<String> getLabelsFor(TaskView task);
	public Alert labelsListChangedAlert();
	public Alert tasksInLabelChangedAlert();
	public List<String> assignableLabels();
	public void setLabelToMultipleTasks(String labelToAssignTaskTo,
			Set<TaskView> selectedTasks);
	public void removeMultipleTasksFromLabel(String label,
			Set<TaskView> tasks);

	
}