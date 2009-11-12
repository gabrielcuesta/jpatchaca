package jira;

import org.apache.axis.utils.StringUtils;
import org.reactive.Receiver;
import org.reactive.Signal;
import org.reactive.Source;

import reactive.ListSignal;
import reactive.ListSource;

public class JiraIssuesRetrieverImpl implements JiraIssuesRetriever {

	private final Jira jira;
	
	protected String _userName;
	protected String _password;
	protected String _address;
	
	private final ListSource<String> _issues = new ListSource<String>();
	private final Source<Boolean> _isConfigured = new Source<Boolean>(false);	


	public JiraIssuesRetrieverImpl(JiraConfig config, Jira jira){
		this.jira = jira;
		
		config.userName().addReceiver(new Receiver<String>(){@Override public void receive(String value) {
			_userName = value;
			update();
		}});
		
		config.password().addReceiver(new Receiver<String>(){@Override public void receive(String value) {
			_password = value;
			update();
		}});
		
		config.address().addReceiver(new Receiver<String>(){@Override public void receive(String value) {
			_address = value;
			update();
		}});
		
	}
	
	protected void update() {
		if (	StringUtils.isEmpty(_userName) || 
				StringUtils.isEmpty(_password) ||
				StringUtils.isEmpty(_address)){
			_issues.clear();
			_isConfigured.supply(false);
			return;
		}
		
		String[] issues = jira.getIssues(_userName, _password, _address);
		
		if (issues == null){
			_issues.clear();
			_isConfigured.supply(false);
			return;
		}
		
		_isConfigured.supply(true);
		
		int i = 0;
		for (String issue : issues){
			_issues.add(i++,issue);
		}
		
		
	}

	public ListSignal<String> issues(){
		return _issues;
	}

	@Override
	public Signal<Boolean> isConfigured() {
		return _isConfigured;
	}
}
