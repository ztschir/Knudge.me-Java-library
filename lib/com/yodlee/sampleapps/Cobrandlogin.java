package com.yodlee.sampleapps;

import com.yodlee.soap.collections.Locale;
import com.yodlee.soap.common.CobrandContext;
import com.yodlee.soap.common.CobrandCredentials;
import com.yodlee.soap.core.login.cobrandlogin.CobrandLogin;
import com.yodlee.soap.core.login.cobrandlogin.CobrandLoginServiceLocator;
import com.yodlee.soap.core.version.ServerVersion;
import com.yodlee.soap.core.version.serverversionmanagement.ServerVersionManagement;
import com.yodlee.soap.core.version.serverversionmanagement.ServerVersionManagementServiceLocator;
import com.yodlee.soap.ext.login.CobrandPasswordCredentials;

public class Cobrandlogin {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		long cobrandId = 99;
		String applicationId = "94AAC319-CF5E-4733-AA42-CA88C79C656D";
		String cobLogin = "yodlee_99";
		String cobPass = "yodlee123";
		long tnc = 2;
		Locale locale = new Locale();
		locale.setLanguage("en");
        CobrandPasswordCredentials cobCred = new CobrandPasswordCredentials();
        cobCred.setLoginName(cobLogin);
        cobCred.setPassword(cobPass);
       

				
		try {
			
		CobrandLoginServiceLocator locator = new CobrandLoginServiceLocator();
		String serviceName = locator.getCobrandLoginServiceWSDDServiceName();
		locator.setCobrandLoginServiceEndpointAddress("https://192.168.210.98:8743/yodsoap/services/" + serviceName);
		CobrandLogin cobLoginService = locator.getCobrandLoginService();
		CobrandContext cobrandContext = cobLoginService.loginCobrand(cobrandId, applicationId,locale,tnc,cobCred);
		long returnValue = cobrandContext.getCobrandId();
		System.out.println(cobrandContext.getChannelId());
		System.out.println("process soap call");
		System.out.println(returnValue);
		
			ServerVersionManagementServiceLocator locator1 = new ServerVersionManagementServiceLocator();
			String serviceName1 = locator1
					.getServerVersionManagementServiceWSDDServiceName();
			locator1
					.setServerVersionManagementServiceEndpointAddress("https://192.168.210.98:8743/yodsoap/services/" + serviceName1);
			ServerVersionManagement serverVersionManagement = locator1
				.getServerVersionManagementService();
			ServerVersion serverVersion = serverVersionManagement.getServerVersion(cobrandContext);
			 System.out.println("Server Major Version****: " + serverVersion.getMajorVersion());
			 System.out.println("Server Minor Version****: " + serverVersion.getMinorVersion());
			
		} 
		catch (Exception e){
			e.printStackTrace();
		}
	}

}
