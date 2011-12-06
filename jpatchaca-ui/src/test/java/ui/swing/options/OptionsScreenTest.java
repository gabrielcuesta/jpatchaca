package ui.swing.options;

import static org.hamcrest.CoreMatchers.equalTo;
import static ui.swing.options.Assert.assertThat;
import lang.Maybe;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.After;
import org.junit.Test;

import ui.swing.options.OptionsScreenModel.Data;
import ui.swing.presenter.PresenterImpl;
import ui.swing.presenter.mock.UIEventsExecutorMock;

public class OptionsScreenTest {

	@Test
	public void testOkNow_shouldYieldTheSameData() {
		final Data data = newDataWithAllValuesSet();
		expectScreenToWriteDataEqualToTheDataRead(data);
		assertOpenOk();
	}

	@Test
	public void testReadNullValues_shouldYieldEmptyStrings() {
		final Data in = newDataWithNoValuesSet();
		final Data out = newDataWithEmptyStrings();
		expectScreenToReadAndWrite(in, out);
		assertOpenOk();
	}

	@After
	public void closeAllWindows() {
		presenter.closeAllWindows();
	}

	private Data newDataWithAllValuesSet() {
		final Data data = new Data();
		data.issueStatusManagementEnabled = true;
		data.jiraUrl = Maybe.wrap("http://jpatchaca.org/jira");
		data.jiraUserName = Maybe.wrap("foo");
		data.jiraPassword = Maybe.wrap("bar");
		data.supressShakingDialog = true;
		return data;
	}

	private Data newDataWithNoValuesSet() {
		final Data data = new Data();
		data.issueStatusManagementEnabled = false;
		data.jiraUrl = null;
		data.jiraUserName = null;
		data.jiraPassword = null;
		data.supressShakingDialog = false;
		return data;
	}

	private Data newDataWithEmptyStrings() {
		final Data data = new Data();
		data.issueStatusManagementEnabled = false;
		data.jiraUrl = Maybe.wrap("");
		data.jiraUserName = Maybe.wrap("");
		data.jiraPassword = Maybe.wrap("");
		data.supressShakingDialog = false;
		return data;
	}

	private void expectScreenToWriteDataEqualToTheDataRead(final Data data) {
		final Data in = data;
		final Data out = data;
		expectScreenToReadAndWrite(in, out);
	}

	private void expectScreenToReadAndWrite(final Data in, final Data out) {
		this.outexpected = out;
		m.checking(new Expectations() {
			{
				oneOf(modelMock).readDataFromSystem(); will(returnValue(in));
				oneOf(modelMock).writeDataIntoSystem(with(any(Data.class))); will(capture);
			}
		});
	}

	private void assertOpenOk() {
		openOptionsScreen();
		ok();
		assertExpectationsSatisfied();
	}

	private void openOptionsScreen() {
		new OptionsScreenPresenter(modelMock, presenter).show();
	}

	private void ok() {
		new OptionsScreenOperator().ok();
	}

	private void assertExpectationsSatisfied() {
		m.assertIsSatisfied();
		assertThat(String.valueOf(capture.get()), equalTo(String.valueOf(outexpected)));
	}
	
	private final Mockery m = new JUnit4Mockery();
	final OptionsScreenModel modelMock = m.mock(OptionsScreenModel.class);
	private final PresenterImpl presenter = new PresenterImpl(new UIEventsExecutorMock());
	Capture<Data> capture = new Capture<Data>();
	private Data outexpected;

}
