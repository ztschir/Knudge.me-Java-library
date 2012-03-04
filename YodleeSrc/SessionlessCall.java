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
import com.yodlee.soap.core.dataservice.dataservice.DataService;
import com.yodlee.soap.core.dataservice.dataservice.DataServiceServiceLocator;
import com.yodlee.soap.core.login.AuthenticationAttemptsExceededExceptionFault;
import com.yodlee.soap.core.login.InvalidUserCredentialsExceptionFault;
import com.yodlee.soap.core.login.UserInfo;
import com.yodlee.soap.core.login.login.Login;
import com.yodlee.soap.core.login.login.LoginServiceLocator;
import com.yodlee.soap.core.preferencemanagement.userpreferencemanagement.UserPreferenceManagement;
import com.yodlee.soap.core.preferencemanagement.userpreferencemanagement.UserPreferenceManagementServiceLocator;
import com.yodlee.soap.ext.login.PasswordCredentials;
import com.yodlee.soap.ext.saml.SAMLCredentials;

import com.yodlee.soap.common.CobrandContext;
import com.yodlee.soap.common.ConversationCredentials;

public class SessionlessCall extends ApplicationSuper {

	/** Navigation Counter. **/
    private static int optionCount = 1;
	/** Navigation Menu Choice. * */
	private static final int NAV_SESSIONLESS_VIEW_ITEMS = optionCount++;
    /** Navigation Menu Choice. **/
    private static final int NAV_QUIT = 0;
    
    
	public void doSessionlessCallMenu() {
		boolean loop = true;
		int choice = 0;
		while (loop) {
       	try {
				System.out.println("Sessionless call Menu");
				System.out.println("********************");
		        System.out.println(NAV_SESSIONLESS_VIEW_ITEMS + ". Sessionless View Items (non-SSO)");
		        System.out.println(NAV_QUIT + ". Exit Sub-menu");
		        System.out.println("********************");
		        System.out.print("Enter Choice : ");
		        choice = IOUtils.readInt();
		
		        
		        if (choice == NAV_SESSIONLESS_VIEW_ITEMS) {
					sessionlessViewItems();			
		        }else if (choice == NAV_QUIT)
		            loop = false;
		        else 
					 System.out.println("Invalid Entry!");
       	} catch (Exception e) {
               e.printStackTrace();
           }
       }
	}
	 
	
    /**
     * Logs in a registered and certified user to the Yodlee platform.
     */
    public
    void sessionlessViewItems ()
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
        	
        	/* this works in 9.0.2
        	PasswordCredentials passwordCredentials = new PasswordCredentials(loginName, password);
        	UserContext sessionlessUserContext = new UserContext(getCobrandContext(), passwordCredentials);

        	 */
        	
        	PasswordCredentials passwordCredentials = new PasswordCredentials();
        	passwordCredentials.setLoginName(loginName);
        	passwordCredentials.setPassword(password);
        	
        	UserContext sessionlessUserContext = new UserContext();
        	CobrandContext cobrandContext = getCobrandContext();

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
