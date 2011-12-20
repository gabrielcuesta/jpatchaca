package jira.service;

import java.rmi.RemoteException;
import java.util.List;

import javax.xml.rpc.ServiceException;

import jira.JiraOptions;
import jira.exception.JiraAuthenticationException;
import jira.exception.JiraIssueNotFoundException;
import jira.exception.JiraOptionsNotSetException;
import jira.exception.JiraPermissionException;
import jira.exception.JiraValidationException;

import org.apache.commons.lang.StringUtils;
import org.jpatchaca.jira.ws.JPatchacaSoapService;
import org.jpatchaca.jira.ws.RemoteMetaAttribute;

import com.dolby.jira.net.soap.jira.JiraSoapService;
import com.dolby.jira.net.soap.jira.RemoteAuthenticationException;
import com.dolby.jira.net.soap.jira.RemoteComment;
import com.dolby.jira.net.soap.jira.RemoteField;
import com.dolby.jira.net.soap.jira.RemoteFieldValue;
import com.dolby.jira.net.soap.jira.RemoteIssue;
import com.dolby.jira.net.soap.jira.RemoteNamedObject;
import com.dolby.jira.net.soap.jira.RemotePermissionException;
import com.dolby.jira.net.soap.jira.RemoteStatus;
import com.dolby.jira.net.soap.jira.RemoteValidationException;
import com.dolby.jira.net.soap.jira.RemoteWorklog;

public class JiraServiceFacade {

	public static final int TOKEN_TIMEOUT_MINUTES = 10;

	// TODO tentar remover jiraOptions
	private final JiraOptions jiraOptions;
	private final JiraServiceFactory serviceFactory;

	private final ClientTokenManager tokenManager;

	public JiraServiceFacade(JiraOptions jiraOptions,
			JiraServiceFactory serviceFactory, ClientTokenManager tokenManager) {
		this.jiraOptions = jiraOptions;
		this.serviceFactory = serviceFactory;
		this.tokenManager = tokenManager;
	}

	private JiraSoapService getService() {
		try {
			return serviceFactory.createJiraSoapService();
		} catch (ServiceException e) {
			throw _handleException(e);
		}
	}
	
	public RemoteIssue getIssueByKey(String key) {
		try {
			RemoteIssue[] remoteIssues = getService().getIssuesFromJqlSearch(
					tokenManager.getToken(), "key = " + key, 20);
			return remoteIssues[0];
		} catch (RemoteValidationException e) {
			throw new JiraIssueNotFoundException(key);
		} catch (RemoteAuthenticationException e) {
			throw _handleException(e);
		} catch (RemotePermissionException e) {
			throw _handleException(e);
		} catch (RemoteException e) {
			throw _handleException(e);
		}
	}

	public RemoteIssue getIssueById(String id) {
		try {			
			return getService().getIssueById(tokenManager.getToken(), id);
		} catch (RemoteValidationException e) {
			throw _handleException(e);
		} catch (RemoteAuthenticationException e) {
			throw _handleException(e);
		} catch (RemotePermissionException e) {
			throw _handleException(e);
		} catch (RemoteException e) {
			throw _handleException(e);
		}
	}

	public void addWorklogAndAutoAdjustRemainingEstimate(String issueId,
			RemoteWorklog workLog) {
		try {
			getService().addWorklogAndAutoAdjustRemainingEstimate(
					tokenManager.getToken(), issueId, workLog);
		} catch (RemoteValidationException e) {
			throw _handleException(e);
		} catch (RemoteAuthenticationException e) {
			throw _handleException(e);
		} catch (RemotePermissionException e) {
			throw _handleException(e);
		} catch (RemoteException e) {
			throw _handleException(e);
		}
	}

	public RemoteNamedObject[] getAvailableActions(String key) {
		try {
			return getService().getAvailableActions(tokenManager.getToken(),
					key);
		} catch (RemoteValidationException e) {
			throw _handleException(e);
		} catch (RemoteAuthenticationException e) {
			throw _handleException(e);
		} catch (RemotePermissionException e) {
			throw _handleException(e);
		} catch (RemoteException e) {
			throw _handleException(e);
		}
	}

	public RemoteField[] getFieldsForAction(String key, String id) {
		try {
			return getService().getFieldsForAction(tokenManager.getToken(),
					key, id);
		} catch (RemoteValidationException e) {
			throw _handleException(e);
		} catch (RemoteAuthenticationException e) {
			throw _handleException(e);
		} catch (RemotePermissionException e) {
			throw _handleException(e);
		} catch (RemoteException e) {
			throw _handleException(e);
		}
	}

	public void progressWorkflowAction(String key, String id,
			RemoteFieldValue[] remoteFieldValues) {
		try {
			getService().progressWorkflowAction(tokenManager.getToken(), key,
					id, remoteFieldValues);
		} catch (RemoteValidationException e) {
			throw _handleException(e);
		} catch (RemoteAuthenticationException e) {
			throw _handleException(e);
		} catch (RemotePermissionException e) {
			throw _handleException(e);
		} catch (RemoteException e) {
			throw _handleException(e);
		}
	}

	public void addComment(String key, RemoteComment remoteComment) {
		try {
			getService()
					.addComment(tokenManager.getToken(), key, remoteComment);
		} catch (RemoteValidationException e) {
			throw _handleException(e);
		} catch (RemoteAuthenticationException e) {
			throw _handleException(e);
		} catch (RemotePermissionException e) {
			throw _handleException(e);
		} catch (RemoteException e) {
			throw _handleException(e);
		}
	}

	public RemoteStatus[] getStatuses() {
		try {
			return getService().getStatuses(tokenManager.getToken());
		} catch (RemoteValidationException e) {
			throw _handleException(e);
		} catch (RemoteAuthenticationException e) {
			throw _handleException(e);
		} catch (RemotePermissionException e) {
			throw _handleException(e);
		} catch (RemoteException e) {
			throw _handleException(e);
		}
	}

	public void updateIssue(String key, RemoteFieldValue[] fieldValues) {
		try {
			getService().updateIssue(tokenManager.getToken(), key, fieldValues);
		} catch (RemoteValidationException e) {
			throw _handleException(e);
		} catch (RemoteAuthenticationException e) {
			throw _handleException(e);
		} catch (RemotePermissionException e) {
			throw _handleException(e);
		} catch (RemoteException e) {
			throw _handleException(e);
		}
	}

	public RemoteIssue[] getIssuesFromCurrentUserWithStatus(
			List<String> statusList) {
		String jql = String.format(
				"assignee = currentUser() AND status in (%s)",
				StringUtils.join(statusList, ", "));

		try {
			return getService().getIssuesFromJqlSearch(tokenManager.getToken(),
					jql, 30);
		} catch (RemoteValidationException e) {
			throw _handleException(e);
		} catch (RemoteAuthenticationException e) {
			throw _handleException(e);
		} catch (RemotePermissionException e) {
			throw _handleException(e);
		} catch (RemoteException e) {
			throw _handleException(e);
		}
	}
	
	public RemoteMetaAttribute[] getMetaAttributes(String issueKey){
		try {
			JPatchacaSoapService jpatchacaService = serviceFactory.createJPatchacaService();
			return jpatchacaService.getMetaAttributesForIssue(tokenManager.getToken(), issueKey);			
		} catch (ServiceException e) {
			throw _handleException(e);
		} catch (RemoteException e) {
			throw _handleException(e);
		}
	}

	public String getJiraUsername() {
		String username = jiraOptions.getUserName().unbox();
		if (username == null)
			throw new JiraOptionsNotSetException();

		return username;
	}

	private RuntimeException _handleException(RemoteException e) {
		tokenManager.resetTokenTimeout();
		return new RuntimeException(e.getMessage());
	}

	private RuntimeException _handleException(RemotePermissionException e) {
		return new JiraPermissionException(e);
	}

	private RuntimeException _handleException(RemoteAuthenticationException e) {
		tokenManager.resetTokenTimeout();
		return new JiraAuthenticationException(e);
	}

	private RuntimeException _handleException(RemoteValidationException e) {
		return new JiraValidationException(e);
	}

	private RuntimeException _handleException(ServiceException e) {
		return new RuntimeException(e);
	}

}