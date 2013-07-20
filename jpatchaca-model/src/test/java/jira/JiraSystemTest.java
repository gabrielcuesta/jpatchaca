package jira;

import java.util.Date;

import jira.service.JiraMock;

import org.apache.commons.lang.time.DateUtils;
import org.junit.Assert;
import org.junit.Test;

import periods.Period;
import tasks.persistence.MockEventsConsumer;
import tasks.tasks.Tasks;

public class JiraSystemTest {

	@Test
	public void testAddWorklog(){
		Period period = new Period(date(0l), date(DateUtils.MILLIS_PER_HOUR));
		addWorklogFor(period);
		Assert.assertEquals("1h 0m", timeLogged());
		Assert.assertEquals("Worklog comment", commentLogged());
	}
	
	@Test
	public void testAddWorklogWithOverride(){
		Period period = new Period(date(0l), date(DateUtils.MILLIS_PER_HOUR));
		overrideWorkLog(period, "2h 25m");
		addWorklogFor(period);
		Assert.assertEquals("2h 25m", timeLogged());
		
	}
	
	@Test
	public void testAddWorklogOverrideWrongFormat(){
		Period period = new Period(date(0l), date(DateUtils.MILLIS_PER_HOUR));
		String timeSpentOverrideWrongFormat = "2:25";
		overrideWorkLog(period, timeSpentOverrideWrongFormat);
		addWorklogFor(period);
		Assert.assertEquals("1h 0m", timeLogged());
		
	}
	
	@Test
	public void testAddWorklogZeroMinutes(){
		Period period = new Period(date(0l), date(DateUtils.MILLIS_PER_HOUR));
		overrideWorkLog(period, " 0h 0m ");
		addWorklogFor(period);
		Assert.assertEquals(null, timeLogged());
		
	}

	private final JiraMock jira = new JiraMock();
	private final JiraWorklogOverride  jiraWorklogOverride = new JiraWorklogOverride(); 
	private final JiraSystemImpl jiraSystem = new JiraSystemImpl(jira, new MockEventsConsumer(), new Tasks(), jiraWorklogOverride);

	private void overrideWorkLog(Period period, String timeSpent) {
		jiraWorklogOverride.overrideTimeSpentForPeriod(timeSpent, period);
		
	}
	
	private String timeLogged() {
		return jira.timeLoggedFor("key");
	}

	private String commentLogged() {
		return jira.commentForWorklog("key");
	}

	private void addWorklogFor(Period period) {
		jiraSystem.logWorkOnIssue(period, "key", "Worklog comment");
	}

	private Date date(long millis) {
		return new Date(millis);
	}
}
