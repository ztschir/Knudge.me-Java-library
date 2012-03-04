package com.yodlee.sampleapps;

import java.rmi.RemoteException;

import javax.xml.rpc.ServiceException;

import com.yodlee.soap.core.CoreExceptionFault;
import com.yodlee.soap.core.IllegalArgumentValueExceptionFault;
import com.yodlee.soap.core.InvalidCobrandContextExceptionFault;
import com.yodlee.soap.core.InvalidCobrandConversationCredentialsExceptionFault;
import com.yodlee.soap.core.InvalidConversationCredentialsExceptionFault;
import com.yodlee.soap.core.StaleConversationCredentialsExceptionFault;
import com.yodlee.soap.core.version.ServerVersion;
import com.yodlee.soap.core.version.serverversionmanagement.ServerVersionManagement;
import com.yodlee.soap.core.version.serverversionmanagement.ServerVersionManagementServiceLocator;

public class Server extends ApplicationSuper{

	private ServerVersionManagement serverVersionManagement;

	public Server() {
		ServerVersionManagementServiceLocator locator = new ServerVersionManagementServiceLocator();
		String serviceName = locator
				.getServerVersionManagementServiceWSDDServiceName();
		locator
				.setServerVersionManagementServiceEndpointAddress(System.getProperty("com.yodlee.soap.services.url")
						+ "/" + serviceName);
		try {
			 serverVersionManagement = locator
					.getServerVersionManagementService();
		} catch (ServiceException se) {

		}
	}
	
	public void printServerVersion(){
		
		ServerVersion serverVersion = null;
		try {
		 serverVersion = serverVersionManagement.getServerVersion(getCobrandContext());
		 System.out.println("Server Major Version****: " + serverVersion.getMajorVersion());
		 System.out.println("Server Minor Version****: " + serverVersion.getMinorVersion());
		} 
		catch (StaleConversationCredentialsExceptionFault e){
			System.out.println("Stale Conversation Credentials");
		}
		catch (InvalidCobrandConversationCredentialsExceptionFault e){
			System.out.println("InvalidCobrandConversationCredentialsExceptionFault");
		}
		catch (InvalidConversationCredentialsExceptionFault e){
			System.out.println("InvalidConversationCredentialsExceptionFault");
		}
		catch (InvalidCobrandContextExceptionFault e){
			System.out.println("Stale Conversation Credentials");
		}
		catch (IllegalArgumentValueExceptionFault e){
			System.out.println("IllegalArgumentValueExceptionFault");
		}
		catch (CoreExceptionFault e){
			System.out.println("CoreExceptionFault");
			e.printStackTrace();
		}
		catch (RemoteException e){
			System.out.println("RemoteException");
			e.printStackTrace();
		}
		catch (Exception e){
			e.printStackTrace();
		}
		
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
