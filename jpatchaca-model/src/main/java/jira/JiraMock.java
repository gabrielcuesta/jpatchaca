package jira;

import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jira.exception.JiraException;

import org.apache.commons.lang.NotImplementedException;

public class JiraMock implements Jira {

	private Map<String, String> worklogsByKey = new HashMap<String, String>();

	@Override
	public JiraIssue getIssueByKey(String key) throws JiraException {
		JiraIssueData data = new JiraIssueData();
		data.setKey(key);
		data.setSummary("test");
		return new JiraIssue(data);
	}

	@Override
	public void newWorklog(String issueId, Calendar startDate, String timeSpent) {
		worklogsByKey.put(issueId, timeSpent);
	}

	public String timeLoggedFor(String key) {
		return worklogsByKey.get(key);
	}

	@Override
	public List<JiraAction> getAvaiableActions(JiraIssue issue) {
		return Collections.emptyList();
	}

	@Override
	public void progressWithAction(JiraIssue issue, JiraAction action, String comment) {
		
	}

	@Override
	public List<JiraIssue> getIssuesFromCurrentUserWithStatus(List<String> statusList) {
		return Collections.emptyList();
	}

	@Override
	public String getIssueStatus(JiraIssue issue) {
		throw new NotImplementedException();
	}

	@Override
	public String getIssueAssignee(JiraIssue issue) {
		throw new NotImplementedException();
	}

	@Override
	public void assignIssueToCurrentUser(JiraIssue issue) {
		throw new NotImplementedException();
	}

	@Override
	public boolean isAssignedToCurrentUser(JiraIssue issue) {
		return true;
	}
	
}
