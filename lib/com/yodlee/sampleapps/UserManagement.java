/*
 * Copyright 2009 Yodlee, Inc.  All Rights Reserved.  Your use of this code 
 * requires a license from Yodlee.  Any such license to this code is 
 * restricted to evaluation/illustrative purposes only. It is not intended 
 * for use in a production environment, and Yodlee disclaims all warranties 
 * and/or support obligations concerning this code, regardless of the terms 
 * of any other agreements between Yodlee and you."
 */
package com.yodlee.sampleapps;

import javax.xml.rpc.ServiceException;

import com.yodlee.sampleapps.helper.IOUtils;
import com.yodlee.sampleapps.helper.SamlHelper;
import com.yodlee.soap.common.UserContext;
import com.yodlee.soap.ext.login.PasswordCredentials;
import com.yodlee.soap.ext.saml.SAMLCredentials;
import com.yodlee.soap.ext.sso.ssousermanagement.SSOUserManagement;
import com.yodlee.soap.ext.sso.ssousermanagement.SSOUserManagementServiceLocator;

public class UserManagement extends ApplicationSuper {

	protected SSOUserManagement ssoUserManagement;
	
    /**
     * Constructor
     */
	public UserManagement() {
		super();
		SSOUserManagementServiceLocator locator = new SSOUserManagementServiceLocator();
        String serviceName = locator.getSSOUserManagementServiceWSDDServiceName();
        locator.setSSOUserManagementServiceEndpointAddress(System.getProperty("com.yodlee.soap.services.url")
						+ "/" + serviceName);
        try {
        	ssoUserManagement = locator.getSSOUserManagementService();
		} catch (ServiceException se) {
			throw new RuntimeException(se);
		}
	}
	
	public void doMigrate(UserContext userContext) {
		SamlHelper samlHelper =
            SamlHelper.getSamlHelper(getKeystoreFilename(), getKeystoreAlias(),
                getKeystorePassword());
		
		System.out.print("Login: ");
        String userName = IOUtils.readStr();

        System.out.print("Password: ");
        String password = IOUtils.readStr();
    	PasswordCredentials passwordCredentials = new PasswordCredentials();
    	passwordCredentials.setLoginName(userName);
    	passwordCredentials.setPassword(password);
  
        System.out.print("New SSO Login: ");
        String newUserName = IOUtils.readStr();

        try {
            String samlResponseStr =
                samlHelper.generateResponseString(newUserName, getIssuer());

            SAMLCredentials samlCredentials = new SAMLCredentials();
            samlCredentials.setSamlResponse(samlResponseStr);
            samlCredentials.setIssuerId(getIssuer());


            ssoUserManagement.migrateToSSO(getCobrandContext(), passwordCredentials, samlCredentials);

            System.out.println("User Migrated");
        } catch (Exception e) {
            System.out.println("Failed to migrate: " + e);
            e.printStackTrace();
        }
	}
	
}
