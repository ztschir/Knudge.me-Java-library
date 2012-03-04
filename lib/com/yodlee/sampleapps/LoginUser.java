/*
 * Copyright 2009 Yodlee, Inc.  All Rights Reserved.  Your use of this code 
 * requires a license from Yodlee.  Any such license to this code is 
 * restricted to evaluation/illustrative purposes only. It is not intended 
 * for use in a production environment, and Yodlee disclaims all warranties 
 * and/or support obligations concerning this code, regardless of the terms 
 * of any other agreements between Yodlee and you."
 */
package com.yodlee.sampleapps;

import java.rmi.RemoteException;
import javax.xml.rpc.ServiceException;

import com.yodlee.sampleapps.helper.IOUtils;
import com.yodlee.sampleapps.helper.SamlHelper;
import com.yodlee.soap.collections.List;
import com.yodlee.soap.common.NVPair;
import com.yodlee.soap.common.UserContext;
import com.yodlee.soap.common.UserCredentials;
import com.yodlee.soap.core.CoreExceptionFault;
import com.yodlee.soap.core.IllegalArgumentValueExceptionFault;
import com.yodlee.soap.core.InvalidConversationCredentialsExceptionFault;
import com.yodlee.soap.core.InvalidUserContextExceptionFault;
import com.yodlee.soap.core.StaleConversationCredentialsExceptionFault;
import com.yodlee.soap.core.login.AuthenticationAttemptsExceededExceptionFault;
import com.yodlee.soap.core.login.InvalidUserCredentialsExceptionFault;
import com.yodlee.soap.core.login.UserInfo;
import com.yodlee.soap.core.login.login.Login;
import com.yodlee.soap.core.login.login.LoginServiceLocator;
import com.yodlee.soap.core.preferencemanagement.userpreferencemanagement.UserPreferenceManagement;
import com.yodlee.soap.core.preferencemanagement.userpreferencemanagement.UserPreferenceManagementServiceLocator;
import com.yodlee.soap.ext.login.PasswordCredentials;
import com.yodlee.soap.ext.saml.SAMLCredentials;

public class LoginUser extends ApplicationSuper {
	Login login;
    UserPreferenceManagement userPreferenceManagement;

    /**
     * Constructs an instance of the LoginUser class that
     * provides the functionality to login/logout a user.
     */
    public
    LoginUser ()
    {
        LoginServiceLocator locator = new LoginServiceLocator();
        String serviceName = locator.getLoginServiceWSDDServiceName();
        locator.setLoginServiceEndpointAddress(System.getProperty("com.yodlee.soap.services.url")
						+ "/" + serviceName);
        try {
        	login = locator.getLoginService();
		} catch (ServiceException se) {
			throw new RuntimeException(se);
		}
		UserPreferenceManagementServiceLocator locator1 = new UserPreferenceManagementServiceLocator();
        String serviceName1 = locator1.getUserPreferenceManagementServiceWSDDServiceName();
        locator1.setUserPreferenceManagementServiceEndpointAddress(System.getProperty("com.yodlee.soap.services.url")
						+ "/" + serviceName);
        try {
        	userPreferenceManagement = locator1.getUserPreferenceManagementService();
		} catch (ServiceException se) {
			throw new RuntimeException(se);
		}    	
    }

    /**
     * Logs in a registered and certified user to the Yodlee platform.
     */
    public
    UserContext loginUser (String loginName,
                           String password)
    {
        UserInfo userInfo = null;
        try {
        	PasswordCredentials passwordCredentials = new PasswordCredentials();
        	passwordCredentials.setLoginName(loginName);
        	passwordCredentials.setPassword(password);
	        userInfo = login.login2 (getCobrandContext(), passwordCredentials);
	     
	        return userInfo.getUserContext ();
        } catch (InvalidUserCredentialsExceptionFault iucex) {
            System.out.println("Invalid credentials!");
        } catch (Exception e) {
			e.printStackTrace();
		}
        return null;
    }

    /**
     * Logs out a user from the Yodlee platform.
     */
    public void logoutUser (UserContext userContext)
    {
    	try {
			login.logout (userContext);
		} catch (StaleConversationCredentialsExceptionFault e) {			
			e.printStackTrace();
		} catch (InvalidConversationCredentialsExceptionFault e) {
			e.printStackTrace();
		} catch (CoreExceptionFault e) {
			e.printStackTrace();
		} catch (IllegalArgumentValueExceptionFault e) {
			e.printStackTrace();
		} catch (InvalidUserContextExceptionFault e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
    }

    /**
     * Returns logged in user information.
     */
    public void getUserInfo (UserContext userContext)
    {
        UserInfo userInfo;
		try {
			userInfo = login.getUserInfo (userContext);
			System.out.println ("\tUser Name:    " + userInfo.getLoginName ());
	        System.out.println ("\tLogin Count:  " + userInfo.getLoginCount ());
	        System.out.println ("\tEmail Address:" + userInfo.getEmailAddress ());
		} catch (StaleConversationCredentialsExceptionFault e) {
			e.printStackTrace();
		} catch (InvalidConversationCredentialsExceptionFault e) {
			e.printStackTrace();
		} catch (CoreExceptionFault e) {
			e.printStackTrace();
		} catch (IllegalArgumentValueExceptionFault e) {
			e.printStackTrace();
		} catch (InvalidUserContextExceptionFault e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
    }

    public void getUserPreferences(UserContext userContext){

        Object[] prefs = null;
        List prefsList = null;
		try {
			prefsList = userPreferenceManagement.getAllPreferences(userContext);
			if (prefsList != null){
				prefs = prefsList.getElements();
			}
		} catch (StaleConversationCredentialsExceptionFault e) {
			e.printStackTrace();
		} catch (InvalidConversationCredentialsExceptionFault e) {
			e.printStackTrace();
		} catch (CoreExceptionFault e) {
			e.printStackTrace();
		} catch (IllegalArgumentValueExceptionFault e) {
			e.printStackTrace();
		} catch (InvalidUserContextExceptionFault e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
        System.out.println("\n**User Preferences**");
        if( prefs == null || prefs.length == 0){
            System.out.println("No Preferences!");
        }else{
            for (int i=0; i<prefs.length; i++){
            	NVPair pair = (NVPair)prefs[i];
            	System.out.println("\t" + pair.getName() + " = " + pair.getValues().getElements(0));
            }
        }
    }

    /**
     * Changes the password of a user on the Yodlee platform.
     */
    public void changePassword (UserContext userContext,
                         String userName,
                         String password,
                         String changePassword)
    {
        try {
        	PasswordCredentials passwordCredentials1 = new PasswordCredentials();
        	passwordCredentials1.setLoginName(userName);
        	passwordCredentials1.setPassword(password);
        	PasswordCredentials passwordCredentials2 = new PasswordCredentials();
        	passwordCredentials2.setLoginName(userName);
        	passwordCredentials2.setPassword(changePassword);

			login.changeCredentials (userContext, passwordCredentials1, passwordCredentials2);

		
		} catch (StaleConversationCredentialsExceptionFault e) {
			e.printStackTrace();
		} catch (InvalidConversationCredentialsExceptionFault e) {
			e.printStackTrace();
		} catch (InvalidUserCredentialsExceptionFault e) {
			e.printStackTrace();
		} catch (CoreExceptionFault e) {
			e.printStackTrace();
		} catch (IllegalArgumentValueExceptionFault e) {
			e.printStackTrace();
		} catch (InvalidUserContextExceptionFault e) {
			e.printStackTrace();
		} catch (AuthenticationAttemptsExceededExceptionFault e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
    }

    /**
     * Touches the underlying conversation credentials (session) of the user
     * on the Yodlee platform, to get a new lease on the inactivity timeout.
     * <p>
     * @param userContext The UserContext being touched.
     */
    public
    void extendInactivityTimeout (UserContext userContext)
    {
        try {
			login.touchConversationCredentials (userContext);
		} catch (Exception e) {
			e.printStackTrace();
		}
    }

    /**
     * Renews the underlying conversation credentials (session) of the user
     * on the Yodlee platform, to get a new lease on the absolute timeout.
     * The returned UserContext must be used and the UserContext
     * passed in as argument discarded.
     * <p>
     * @param userContext The UserContext being renewed.
     */
    public
    UserContext getNewContext (UserContext userContext)
    {    	
        try {
        	userContext = login.renewConversation (userContext);
		} catch (StaleConversationCredentialsExceptionFault e) {
			e.printStackTrace();
		} catch (InvalidConversationCredentialsExceptionFault e) {
			e.printStackTrace();
		} catch (CoreExceptionFault e) {
			e.printStackTrace();
		} catch (IllegalArgumentValueExceptionFault e) {
			e.printStackTrace();
		} catch (InvalidUserContextExceptionFault e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return userContext;
    }

    /**
     * Logs in a user to the Yodlee software platform, obtains the user information,
     * changes the user password, logs out the user,  logs the user back in,
     * touches and renews the underlying conversation credentials (for relative
     * and absolute session timeout adjustment), and finally logs out the user.
     */
    public
    static void main (String args[])
    {
        if (args.length < 3) {
            throw new RuntimeException ("Usage: <username> <password> <changePassword>") ;
        }

        String  userName       =   args[0] ;
        String  password       =   args[1] ;
        String  changePassword =   args[2];

        // Startup
        /*
        try {
            InitializationHelper.setup ();
        } catch (Exception startupEx) {
            System.out.println ("Unable to startup system: " + startupEx);
            System.exit (-1);
        }
        */

        LoginUser loginUser = new LoginUser ();
        UserContext userContext = null;

        // Login a user
        System.out.println ("1. Logging in " + userName +
                            " with password " + password);
        userContext = loginUser.loginUser (userName,
                                           password);
        System.out.println ("1. Login of user " + userName +
                            " successful");

        // Get UserInfo
        loginUser.getUserInfo (userContext);

        // Get User Preferences
        loginUser.getUserPreferences(userContext);


        // Change user password
        System.out.println ("2. Changing password for " + userName +
                            " to " + changePassword);
        loginUser.changePassword (userContext,
                                  userName,
                                  password,
                                  changePassword);
        System.out.println ("2. Done changing password for " + userName +
                            " to " + changePassword);

        // Logout the user
        System.out.println ("3. Logging out " + userName);
        loginUser.logoutUser (userContext);
        System.out.println ("3. Done logging out " + userName);

        // Login the user with the new password
        System.out.println ("4. Logging in " + userName +
                            " with changed password " + changePassword);
        userContext = loginUser.loginUser (userName,
                                           changePassword);
        System.out.println ("4. Login of user " + userName +
                            " successful");

        // Touch
        System.out.println ("5. Touching session");
        loginUser.extendInactivityTimeout (userContext);
        System.out.println ("5. Done Touching session");

        // Get UserInfo, to verify access with the touched credentials
        loginUser.getUserInfo (userContext);

        // Renew
        System.out.println ("6. Renewing session");
        userContext = loginUser.getNewContext (userContext);
        System.out.println ("6. Done Renewing session");

        // Get UserInfo, to verify access with the renewed credentials
        loginUser.getUserInfo (userContext);

        // Logout the user
        System.out.println ("7. Logging out " + userName);
        loginUser.logoutUser (userContext);
        System.out.println ("7. Done logging out " + userName);

    }
    
    public UserContext doSSOLoginUser() {
    	String userName = null;
    	SamlHelper samlHelper =
            SamlHelper.getSamlHelper(getKeystoreFilename(), getKeystoreAlias(),
                getKeystorePassword());

        if (samlHelper == null) {
            System.out.println("SAML is not compiled into this version.");
        } else {
            System.out.print("Login: ");

            String input = IOUtils.readStr();

            if (input != null) {
                userName = input;
            }

            try {
                String samlResponseStr =
                    samlHelper.generateResponseString(userName, getIssuer());

                SAMLCredentials userCredentials = new SAMLCredentials();
                userCredentials.setSamlResponse(samlResponseStr);
                userCredentials.setIssuerId(getIssuer());

                UserInfo userInfo = login.login2(getCobrandContext(), userCredentials);

                return userInfo.getUserContext();
                
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException();
            }
        }
        return null;
    }
}
