/*
 * Copyright 2008 Yodlee, Inc.  All Rights Reserved.  Your use of this code
 * requires a license from Yodlee.  Any such license to this code is
 * restricted to evaluation/illustrative purposes only. It is not intended
 * for use in a production environment, and Yodlee disclaims all warranties
 * and/or support obligations concerning this code, regardless of the terms
 * of any other agreements between Yodlee and you."
 */
package YodleeSrc;

import java.util.Calendar;

import javax.xml.rpc.ServiceException;

import com.yodlee.soap.collections.Entry;
import com.yodlee.soap.common.CobrandContext;
import com.yodlee.soap.common.UserContext;
import com.yodlee.soap.common.UserCredentials;
import com.yodlee.soap.core.IllegalArgumentValueExceptionFault;
import com.yodlee.soap.core.login.InvalidUserCredentialsExceptionFault;
import com.yodlee.soap.core.login.UserInfo;
import com.yodlee.soap.core.login.login.Login;
import com.yodlee.soap.core.login.login.LoginService;
import com.yodlee.soap.core.login.login.LoginServiceLocator;
import com.yodlee.soap.core.usermanagement.UserNameExistsExceptionFault;
import com.yodlee.soap.core.usermanagement.UserProfile;
import com.yodlee.soap.core.usermanagement.userregistration.UserRegistration;
import com.yodlee.soap.core.usermanagement.userregistration.UserRegistrationServiceLocator;
import com.yodlee.soap.ext.login.PasswordCredentials;


public class RegisterLogin implements Menu {
	
	protected UserRegistration userRegistration;
	
	protected Login login;
	
	private String[] menuItems =
        new String[] {
            "Register User", 
            "Login User",
            "Sessionless Call (View Items)"
        };

    /**
     * Gets the menu name.
     * @return the menu name.
     */
    public String getMenuName() {
        return "Pre-login Menu";
    }
    
    public RegisterLogin() {
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
		
		LoginServiceLocator locator1 = new LoginServiceLocator();
        String serviceName1 = locator1.getLoginServiceWSDDServiceName();
        locator1.setLoginServiceEndpointAddress(System.getProperty("com.yodlee.soap.services.url")
						+ "/" + serviceName1);
        try {
        	login = locator1.getLoginService();
		} catch (ServiceException se) {
			throw new RuntimeException(se);
		}
    }

    /**
     * Executes the Menu.
     * @param cobrandContext context of the cobrand
     * @param userContext context of the user
     * @return context of the user
     */
    public UserContext doMenuItem(
        int menuItem, CobrandContext cobrandContext, UserContext userContext) {
        switch (menuItem) {
	        case 1: userContext = registerUser(cobrandContext); break;
	        case 2: userContext = loginUser(cobrandContext); break;
	        case 3: sessionlessViewItems(cobrandContext);
        }

        return userContext;
    }

    /**
     * Returns the menu items.
     * @return the menu items.
     */
    public String[] getMenuItems() {
        return menuItems;
    }

    /**
     * Register User.
     * Prompt user to register a user
     * 
     * @param cobrandContext context of the cobrand
     * @return newly created usercontext for the user or null
     */
    public UserContext registerUser(CobrandContext cobrandContext) {       
        UserContext userContext = null;
        String userName = "user_" + Calendar.getInstance().getTime().getTime();
        System.out.print("Login [" + userName + "]: ");

        String input = IOUtils.readStr();

        if (input != null) {
            userName = input;
        }

        String password = "11aa11";
        System.out.print("Password [" + password + "]: ");
        input = IOUtils.readStr();

        if (input != null) {
            password = input;
        }

        String email = "testacct@a.test.website.com";
        System.out.print("E-mail [" + email + "]: ");
        input = IOUtils.readStr();

        if (input != null) {
            email = input;
        }

        System.out.println(
            "Registering with:\n\tuserName=" + userName + "\n\tpassword="
            + password + "\n\temail=\"" + email + "\"");

        UserCredentials uc = new PasswordCredentials(userName, password);

        UserProfile up = new UserProfile();
		Entry en = new Entry("EMAIL_ADDRESS", email);
		Entry[] entries = new Entry[1];
		entries[0] = en;
		up.setValues(new com.yodlee.soap.collections.Map(entries));

		// Register the user
		UserInfo ui = null;
		try {
        	ui = userRegistration.register3(cobrandContext, uc,
					up, null);
            
            userContext = ui.getUserContext();
            System.out.println(userName + " registered");
        } catch (IllegalArgumentValueExceptionFault ex) {
            System.out.println("\n\nGot Illegal Arguments for Registration.");
            System.out.println(
                "Please note that Yodlee enforces the following restrictions:");
            System.out.println("On username:");
            System.out.println("  >= 3 characters");
            System.out.println("  <= 150 characters");
            System.out.println("  No Whitespace - checks: java.lang.Character.isWhitespace()");
            System.out.println("  No Control Characters - checks: java.lang.Character.isISOControl()");
            System.out.println("  Contains at least one Letter - java.lang.Character.isLetter() ");
            System.out.println("\nOn password");
            System.out.println("  >= 6 characters");
            System.out.println("  <= 50 characters");
            System.out.println("  No Whitespace - checks: java.lang.Character.isWhitespace()");
            System.out.println("  No Control Characters - checks: java.lang.Character.isISOControl()");
            System.out.println("  Contains at least one Number - checks: java.lang.Character.isDigit() || !java.lang.Character.isLetter()");
            System.out.println("  Contains at least one Letter - java.lang.Character.isLetter()");
            System.out.println("  Does not contain the same letter/number three or more times in a row.  (e.g. aaa123 would fail for three \"a\"'s in a row, but a1a2a3 would pass)");
            System.out.println("  Does not equal username");
            System.out.println("\n\n");
        } catch (UserNameExistsExceptionFault ex) {
			System.out.println("User " + userName + " already exists");

		} catch (Exception e) {
			e.printStackTrace();
		}

        return userContext;
    }

    /**
     * Login User
     *
     * Prompt user to login user.
     *
     * @param cobrandContext context of the cobrand
     * @return newly created usercontext for the user or null
     */
    public UserContext loginUser(CobrandContext cobrandContext) {
        UserContext userContext = null;
        System.out.print("Login: ");

        String input = IOUtils.readStr();
        String unvalidatedUserName = null;

        if (input != null) {
            unvalidatedUserName = input;
        }

        String password = "";
        System.out.print("Password: ");
        input = IOUtils.readStr();

        if (input != null) {
            password = input;
        }

        try {
            UserInfo userInfo =
            	userInfo = login.login2 (cobrandContext,
                        new PasswordCredentials (unvalidatedUserName, password));

            userContext = userInfo.getUserContext();
            System.out.println(unvalidatedUserName + " logged in");

        } catch (InvalidUserCredentialsExceptionFault iucex) {
            System.out.println("Invalid credentials!");
        } catch (Exception e) {
			e.printStackTrace();
		}

        return userContext;
    }
    
    /**
     * Logs in a registered and certified user to the Yodlee platform.
     */
    public
    void sessionlessViewItems (CobrandContext cobrandContext)
    {
 
        try {
    		System.out.print("Login: ");

            String input = IOUtils.readStr();
            String loginName = null;
            
            if (input != null) {
            	loginName = input;
            }

            String password = "";
            System.out.print("Password: ");
            input = IOUtils.readStr();

            if (input != null) {
                password = input;
            }
        	
        	PasswordCredentials passwordCredentials = new PasswordCredentials(loginName, password);
        	UserContext sessionlessUserContext = new UserContext();

        	sessionlessUserContext.setCobrandConversationCredentials(cobrandContext.getCobrandConversationCredentials());
        	sessionlessUserContext.setConversationCredentials(passwordCredentials);
        	
        	
			sessionlessUserContext.setApplicationId(cobrandContext.getApplicationId());
			sessionlessUserContext.setChannelId(cobrandContext.getChannelId());
			sessionlessUserContext.setCobrandId(cobrandContext.getCobrandId());
			sessionlessUserContext.setIpAddress(cobrandContext.getIpAddress());
			sessionlessUserContext.setIsPasswordExpired(false);
			sessionlessUserContext.setLocale(cobrandContext.getLocale());
			sessionlessUserContext.setPreferenceInfo(cobrandContext.getPreferenceInfo());
			sessionlessUserContext.setTncVersion(cobrandContext.getTncVersion());
			sessionlessUserContext.setValid(true);
			sessionlessUserContext.setValidationHandler(cobrandContext.getValidationHandler());
			

    		DisplayItemInfo dii = new DisplayItemInfo();
            dii.viewItems(sessionlessUserContext);

        } catch (Exception e) {
			e.printStackTrace();
		}

    }
}
