package jira.service;

import java.rmi.RemoteException;

import javax.xml.rpc.ServiceException;

import org.apache.axis.AxisFault;
import org.jpatchaca.jira.ws.JPatchacaSoapService;
import org.jpatchaca.jira.ws.JPatchacaSoapServiceServiceLocator;

import com.dolby.jira.net.soap.jira.JiraSoapService;
import com.dolby.jira.net.soap.jira.JiraSoapServiceServiceLocator;

public class JiraSoapServiceFactory implements JiraServiceFactory {

    public final String JIRASOAPSERVICE_ENDPOINT = "/rpc/soap/jirasoapservice-v2";
    public final String JPATCHACASERVICE_ENDPOINT = "/rpc/soap/jpatchacaservice-v1";
    private JPatchacaSoapServiceCache serviceCache;

    public JiraSoapServiceFactory(){
        serviceCache = new JPatchacaSoapServiceCache();
    }
    
    @Override
    public JiraSoapService createJiraSoapService(String address) throws ServiceException {
        final JiraSoapServiceServiceLocator locator = new JiraSoapServiceServiceLocator();
        locator.setJirasoapserviceV2EndpointAddress(jiraServiceAddress(address));
        locator.setMaintainSession(true);
        return locator.getJirasoapserviceV2();
    }

    @Override
    public JPatchacaSoapService createJPatchacaService(String address) throws ServiceException {
        try {
            JPatchacaSoapService service = createJPatchacaSoapservice(jpatchacaServiceAddress(address));
            service.isAvailable();
            return  serviceCache.decorate(service);
        } catch (AxisFault e) {
            return new JPatchacaSoapServiceFake();
        } catch (RemoteException e) {
            return new JPatchacaSoapServiceFake();
        }
    }

    private JPatchacaSoapService createJPatchacaSoapservice(String address) throws ServiceException {
        final JPatchacaSoapServiceServiceLocator locator = new JPatchacaSoapServiceServiceLocator();
        locator.setJpatchacaserviceV1EndpointAddress(address);
        locator.setMaintainSession(true);
        return locator.getJpatchacaserviceV1();
    }

    private String jiraServiceAddress(String address) {
        return address + JIRASOAPSERVICE_ENDPOINT;
    }

    private String jpatchacaServiceAddress(String address) {
        return address + JPATCHACASERVICE_ENDPOINT;
    }
}
