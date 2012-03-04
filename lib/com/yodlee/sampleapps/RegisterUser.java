/*
 * Copyright 2009 Yodlee, Inc.  All Rights Reserved.  Your use of this code 
 * requires a license from Yodlee.  Any such license to this code is 
 * restricted to evaluation/illustrative purposes only. It is not intended 
 * for use in a production environment, and Yodlee disclaims all warranties 
 * and/or support obligations concerning this code, regardless of the terms 
 * of any other agreements between Yodlee and you.
 */
package com.yodlee.sampleapps;

import java.rmi.RemoteException;

import javax.xml.rpc.ServiceException;

import com.yodlee.sampleapps.helper.IOUtils;
import com.yodlee.sampleapps.helper.SamlHelper;
import com.yodlee.soap.collections.Entry;
import com.yodlee.soap.collections.Map;
import com.yodlee.soap.common.UserContext;
import com.yodlee.soap.common.UserCredentials;
import com.yodlee.soap.core.CoreExceptionFault;
import com.yodlee.soap.core.IllegalArgumentValueExceptionFault;
import com.yodlee.soap.core.InvalidConversationCredentialsExceptionFault;
import com.yodlee.soap.core.InvalidUserContextExceptionFault;
import com.yodlee.soap.core.StaleConversationCredentialsExceptionFault;
import com.yodlee.soap.core.login.UserInfo;
import com.yodlee.soap.core.usermanagement.UserNameExistsExceptionFault;
import com.yodlee.soap.core.usermanagement.UserProfile;
import com.yodlee.soap.core.usermanagement.userregistration.UserRegistration;
import com.yodlee.soap.core.usermanagement.userregistration.UserRegistrationServiceLocator;
import com.yodlee.soap.ext.login.PasswordCredentials;
import com.yodlee.soap.ext.saml.SAMLCredentials;

/**
 * Encapsulates user registration APIs of the Yodlee software platform.
 */
public class RegisterUser extends ApplicationSuper {
	UserRegistration userRegistration;

	/**
	 * Constructs an instance of the RegisterUser class that provides the
	 * functionality to register a user.
	 */
	public RegisterUser() {
		super();

		UserRegistrationServiceLocator locator = new UserRegistrationServiceLocator();
		String serviceName = locator
				.getUserRegistrationServiceWSDDServiceName();
		locator
				.setUserRegistrationServiceEndpointAddress(System.getProperty("com.yodlee.soap.services.url")
						+ "/" + serviceName);
		try {
			userRegistration = locator.getUserRegistrationService();

		} catch (ServiceException se) {
			throw new RuntimeException(se);
		}

	}

	/**
	 * Registers a user given the user login name, password and email.
	 * <p>
	 * 
	 * @param loginName
	 *            The login name of the user being registered.
	 * @param password
	 *            The password of the user being registered.
	 * @param email
	 *            The email address of the user being registered.
	 */
	public UserContext registerUser(String loginName, String password, String email) {
		// Create UserCredentials
    	PasswordCredentials passwordCredentials = new PasswordCredentials();
    	passwordCredentials.setLoginName(loginName);
    	passwordCredentials.setPassword(password);
    	UserCredentials uc = passwordCredentials;
	

		// Create UserProfile
		UserProfile up = new UserProfile();
		Entry en = new Entry();
		en.setKey("EMAIL_ADDRESS");
		en.setValue(email);
		/*Entry en = new Entry("EMAIL_ADDRESS", email);*/
		Entry[] entries = new Entry[1];
		entries[0] = en;
		Map map = new Map();
		map.setTable(entries);
		up.setValues(map);
		//up.setValues(new Map(entries));

		// Register the user
		UserInfo ui = null;
		try {
			ui = userRegistration.register3(getCobrandContext(), uc,
					up, null);
			System.out.println(loginName + "registered");					
		}catch (IllegalArgumentValueExceptionFault ex) {
			System.out.println("\n\nGot Illegal Arguments for Registration.");
			System.out
					.println("Please note that Yodlee enforces the following restrictions:");
			System.out.println("On username:");
			System.out.println("  >= 3 characters");
			System.out.println("  <= 150 characters");
			System.out
					.println("  No Whitespace - checks: java.lang.Character.isWhitespace()");
			System.out
					.println("  No Control Characters - checks: java.lang.Character.isISOControl()");
			System.out
					.println("  Contains at least one Letter - java.lang.Character.isLetter() ");
			System.out.println("\nOn password");
			System.out.println("  >= 6 characters");
			System.out.println("  <= 50 characters");
			System.out
					.println("  No Whitespace - checks: java.lang.Character.isWhitespace()");
			System.out
					.println("  No Control Characters - checks: java.lang.Character.isISOControl()");
			System.out
					.println("  Contains at least one Number - checks: java.lang.Character.isDigit() || !java.lang.Character.isLetter()");
			System.out
					.println("  Contains at least one Letter - java.lang.Character.isLetter()");
			System.out
					.println("  Does not contain the same letter/number three or more times in a row.  (e.g. aaa123 would fail for three \"a\"'s in a row, but a1a2a3 would pass)");
			System.out.println("  Does not equal username");
			System.out.println("\n\n");
		} catch (UserNameExistsExceptionFault ex) {
			System.out.println("User " + loginName + " already exists");

		} catch (Exception e) {
			e.printStackTrace();
		}
		return ui.getUserContext();

	}

	public UserContext ssoRegisterUser(String samlResponse, String issuerId,
			String email) {

		return null;

	}

	/**
	 * Obtains and changes the email of the registered user.
	 * <p>
	 * 
	 * @param userContext
	 *            The user context.
	 * @param newEmailAddress
	 *            The new email address.
	 */
	public void changeEmail(UserContext userContext, String newEmailAddress) {

	}

	/**
	 * Unregisters a user from the Yodlee platform.
	 * <p>
	 * 
	 * @param userContext
	 *            The user context.
	 */
	public void unregisterUser(UserContext userContext) {
		try {
			userRegistration.unregister(userContext);
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

	public String doSSORegistration(UserContext userContext) {
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

                UserInfo userInfo = userRegistration.register3(getCobrandContext(), userCredentials,
                        null, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return userName;
	}
}
