package YodleeSrc;

/*
 * Copyright 2009 Yodlee, Inc.  All Rights Reserved.  Your use of this code 
 * requires a license from Yodlee.  Any such license to this code is 
 * restricted to evaluation/illustrative purposes only. It is not intended 
 * for use in a production environment, and Yodlee disclaims all warranties 
 * and/or support obligations concerning this code, regardless of the terms 
 * of any other agreements between Yodlee and you."
 */


import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.rpc.ServiceException;

import com.yodlee.soap.collections.ArrayOfString;
import com.yodlee.soap.collections.Locale;
import com.yodlee.soap.collections.common.ArrayOfRefreshInfo;
import com.yodlee.soap.common.CobrandContext;
import com.yodlee.soap.common.CobrandCredentials;
import com.yodlee.soap.common.FieldInfoSingle;
import com.yodlee.soap.common.FieldType;
import com.yodlee.soap.common.RefreshInfo;
import com.yodlee.soap.common.UserContext;
import com.yodlee.soap.core.accountmanagement.itemmanagement.ItemManagement;
import com.yodlee.soap.core.accountmanagement.itemmanagement.ItemManagementServiceLocator;
import com.yodlee.soap.core.login.CobrandUserAccountLockedExceptionFault;
import com.yodlee.soap.core.login.InvalidCobrandCredentialsExceptionFault;
import com.yodlee.soap.core.login.UserInfo;
import com.yodlee.soap.core.login.cobrandlogin.CobrandLogin;
import com.yodlee.soap.core.login.cobrandlogin.CobrandLoginServiceLocator;
import com.yodlee.soap.core.login.login.Login;
import com.yodlee.soap.core.login.login.LoginServiceLocator;
import com.yodlee.soap.core.refresh.refresh.Refresh;
import com.yodlee.soap.core.refresh.refresh.RefreshServiceLocator;
import com.yodlee.soap.core.usermanagement.UserNameExistsExceptionFault;
import com.yodlee.soap.core.usermanagement.usercertification.UserCertification;
import com.yodlee.soap.core.usermanagement.usercertification.UserCertificationServiceLocator;
import com.yodlee.soap.core.usermanagement.userregistration.UserRegistration;
import com.yodlee.soap.core.usermanagement.userregistration.UserRegistrationServiceLocator;
import com.yodlee.soap.ext.login.CobrandPasswordCredentials;
import com.yodlee.soap.ext.login.PasswordCredentials;


/**
 * This class demonstrates how to use the Yodlee SDK to do Account Pre-Pop.
 * This sample should be used in conjunction with the Account Pre-pop
 * documentation.
 *
 * This class does not use the the normal ApplicationSuper framework in
 * the com.yodlee.sampleapps package. Rather it contains all the needed code.
 *
 * Account Pre-Pop only really makes sense with SSO.  This sample uses
 * the SSO APIs.  In order to do this is makes use of a helper class.
 * By default, this example use the OpenSaml implementation of SAML.
 * The libraries can be found at http://www.opensaml.org/.
 * There is also a helper if you'd like to use Verisign.
 *
 * Before this example can be run against Yodlee's server, steps must be
 * taken to setup Yodlee's Server for SAML.  This includes generating
 * and exchanging keys.  Please discuss this with your Yodlee Project
 * Manager.
 */
public class AccountPrePop {
    /** Length of time to sleep between refresh polling. */
    protected static final int SLEEP_MILLIS = 10 * 1000;

    /** Length of time to try refresh. */
    public static final long REFRESH_TIMEOUT_MIILIS = 5 * 60 * 1000; //5 minutes

    /** The properties controlling the application. */
    public static Hashtable appProps;

    /** Flag if core has started. */
    private static boolean coreStarted;

    /** Bank of America Credit Card (CA) 2464. */
    private static final int CREDIT_CARD_CSID = 2464;

    /** Bank of America (California) 433. */
    private static final int BANK_CSID = 433;

    /** Proxy for the UserRegistration. */
    protected UserRegistration userRegistration;

    /** Proxy for the UserCertification. */
    protected UserCertification userCertification;

    /** Proxy for the Login. */
    protected Login login;

    /** Proxy for the ItemManagement. */
    protected ItemManagement itemManagement;

    /** Proxy for the Refresh. */
    protected Refresh refresh;

    /** Proxy for the CobrandLogin. */
    protected CobrandLogin cobrandLoginProxy;

    /** Holds the cobrandContext. */
    public CobrandContext cobrandContext;
    
    public static final int STOP_REFRESH_REASON_TIMEDOUT = 100;

    /**
     * Main constructor.
     * Creates the needed proxies and loads the properties files.
     */
    public AccountPrePop() {
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
		
		UserCertificationServiceLocator locator1 = new UserCertificationServiceLocator();
		 serviceName = locator1
				.getUserCertificationServiceWSDDServiceName();
		locator1.
				setUserCertificationServiceEndpointAddress(System.getProperty("com.yodlee.soap.services.url")
						+ "/" + serviceName);
		try {
			userCertification= locator1.getUserCertificationService();

		} catch (ServiceException se) {
			throw new RuntimeException(se);
		}
		
		LoginServiceLocator locator2 = new LoginServiceLocator();
         serviceName = locator2.getLoginServiceWSDDServiceName();
        locator2.setLoginServiceEndpointAddress(System.getProperty("com.yodlee.soap.services.url")
						+ "/" + serviceName);
        try {
        	login = locator2.getLoginService();
		} catch (ServiceException se) {
			throw new RuntimeException(se);
		}
		
		ItemManagementServiceLocator locator3 = new ItemManagementServiceLocator();
        serviceName = locator3.getItemManagementServiceWSDDServiceName();
        locator3.setItemManagementServiceEndpointAddress(System.getProperty("com.yodlee.soap.services.url")
						+ "/" + serviceName);
        try {
        	itemManagement = locator3.getItemManagementService();
		} catch (ServiceException se) {
			throw new RuntimeException(se);
		}
        
		RefreshServiceLocator locator4 = new RefreshServiceLocator();
        serviceName = locator4.getRefreshServiceWSDDServiceName();
        locator4.setRefreshServiceEndpointAddress(System.getProperty("com.yodlee.soap.services.url")
						+ "/" + serviceName);
        try {
        	refresh = locator4.getRefreshService();
		} catch (ServiceException se) {
			throw new RuntimeException(se);
		}
		
		CobrandLoginServiceLocator locator5 = new CobrandLoginServiceLocator();
		serviceName = locator5.getCobrandLoginServiceWSDDServiceName();
		locator5.setCobrandLoginServiceEndpointAddress(System.getProperty("com.yodlee.soap.services.url")
				+ "/" + serviceName);
		try {
		 cobrandLoginProxy = locator5.getCobrandLoginService();
		}
		catch (ServiceException se){
			se.printStackTrace();
		}
		
       
        // Load cobrand context parameters from SampleApps.properties
        try {
            appProps =
                PropertyHelper.loadProperties(
                    "com.yodlee.sampleapps.SampleApp");
        } catch (Exception e) {
            System.out.println(
                "Cannot load resource file - "
                + "com.yodlee.sampleapps.SampleApp. Exiting!!");
            System.exit(-1);
        }
    }

    /**
     * Initializes & Start the CORE basing on the provided Startup Option.
     *
     * @throws Exception indicates an error running the setup.
     */
    /*
    static synchronized void setup() throws Exception {
        
           SoapClientStartup.startup();
                
            coreStarted = true;
        }
    }
*/
    /**
     * Logs the cobrand into the application.
     */
    public void loginCobrand() {
        long cobrandId = 0;
        String appId = "changeMe";
        String cobrandLogin = "changeMe";
        String cobrandPassword = "changeMe";

        try {
            cobrandId = Long.parseLong((String) appProps.get("cobrandId"));
            appId = (String) appProps.get("appId");
            cobrandLogin = (String) appProps.get("cobLogin");
            cobrandPassword = (String) appProps.get("cobPass");
        } catch (Exception e) {
            System.out.println(
                "Exception while reading cobrand context parameters!!");
            e.printStackTrace();
        }

        
        CobrandPasswordCredentials cobrandPasswordCredentials = new CobrandPasswordCredentials();
        cobrandPasswordCredentials.setLoginName(cobrandLogin);
        cobrandPasswordCredentials.setPassword(cobrandPassword);
        CobrandCredentials cobCred = cobrandPasswordCredentials;
        System.out.println("LoginCobrand: " + cobrandLogin);
        Locale locale = new Locale();
		locale.setLanguage("en");
        
		try {
		cobrandContext =
            cobrandLoginProxy.loginCobrand(
                new Long(cobrandId), appId, locale, new Long(2), cobCred);
		}
		catch (InvalidCobrandCredentialsExceptionFault icce){
            System.out.println("InvalidCobrandCredentialsException");
        } catch (CobrandUserAccountLockedExceptionFault cuale){
            System.out.println("CobrandUserAccountLockedException");
        } catch (Exception ee){
            System.out.println("Exception: " + ee);
	        ee.printStackTrace();
	        throw new RuntimeException(ee);
        }

        coreStarted = false;
    }


    /**
     * Logs out a user from the Yodlee platform.
     *
     * @param userContext the usercontext to log out
     */
    public void logoutUser(UserContext userContext)throws Exception {
        login.logout(userContext);
    }

    /**
     * Adds the desired item for the user.
     *
     * @param userContext the usercontext to add the item to
     * @param csId the content service Id to add
     * @param fieldInfoList the list of field infos to add
     * @return the itemId of the newly added item
     */
    protected long addItem(
        UserContext userContext, long csId, List fieldInfoList) {
        long itemId = 0;
        System.out.println("\nAdding item");

        try {
        	com.yodlee.soap.collections.List list = new com.yodlee.soap.collections.List();
        	list.setElements(fieldInfoList.toArray());
            itemId =
            itemManagement.addItemForContentService1(userContext, new Long(csId), list, true, true);

                             
        } catch (Exception coreEx) {
            coreEx.printStackTrace();
            throw new RuntimeException(
                "Unable to add item for content service!");
        }

        System.out.println("Successfully created itemId: " + itemId);

        return itemId;
    }

    /**
     * Polls the refresh status of the specified item. This method continues
     * polling till the refresh either completes or times out. Returns true
     * if the refresh was successfully completed and false otherwise.
     * <p>
     * @param userContext The user context.
     * @return return if the item has been successfully refreshed
     */
    public boolean pollRefreshStatus(UserContext userContext) {
        long startTime = (new Date()).getTime();
        long currTime = startTime;
        HashMap hm = new HashMap();

        while ((startTime - currTime) < REFRESH_TIMEOUT_MIILIS) {
            System.out.print("\n\tChecking the refresh status of items: ");

            try {
                ArrayOfRefreshInfo refreshInfoArray = refresh.getRefreshInfo(userContext);
                RefreshInfo[] refreshInfo = null;
                if (refreshInfoArray != null){
                	refreshInfo = refreshInfoArray.getElements();
                }
                // initilize
                for (int i = 0; i < refreshInfo.length; i++) {
                    hm.put(
                        new Long(refreshInfo[i].getItemId()), new Boolean(
                            false));
                    System.out.print(refreshInfo[i].getItemId() + " ");
                }

                System.out.println("");

                for (int i = 0; i < refreshInfo.length; i++) {
                    if (refreshInfo[i].getRefreshRequestTime() == 0) {
                        long itemId = refreshInfo[i].getItemId();
                        int refreshStatusCode = refreshInfo[i].getStatusCode();

                        if (refreshStatusCode == 0) {
                            hm.put(
                                new Long(refreshInfo[i].getItemId()),
                                new Boolean(true));
                            System.out.println(
                                "\tThe refresh of " + itemId
                                + " has completed successfully.");

                            if (doneRefreshing(hm)) {
                                return true;
                            }
                        } else {
                            hm.put(
                                new Long(refreshInfo[i].getItemId()),
                                new Boolean(true));
                            System.out.println(
                                "\tThe refresh of " + itemId
                                + " did not succeed.  Error code: "
                                + refreshStatusCode);

                            if (doneRefreshing(hm)) {
                                return true;
                            }
                        }
                    }
                }

                Thread.sleep(SLEEP_MILLIS);
            } catch (InterruptedException ex) {
                System.out.println("Refresh polling has been interrupted!");
            } catch (Exception e){
            	e.printStackTrace();
            }
        }

        //Timeout the refresh request
        try {
        refresh.stopRefresh2(userContext, new Integer(STOP_REFRESH_REASON_TIMEDOUT));
        } catch (Exception e){
        	e.printStackTrace();
        }
        System.out.println("\tThe refresh has timed out.");

        return true;
    }

    /**
     * Check to see if all the items stored in the HashMap
     * have been refresh.
     * <p/>
     * @param map the map of boolean values for refreshing.
     * @return true/false
     */
    public static boolean doneRefreshing(Map map) {
        // Iterate over the values in the map
        Iterator it = map.values().iterator();

        while (it.hasNext()) {
            // Get value
            Boolean value = (Boolean) it.next();

            if (!value.booleanValue()) {
                return false;
            }
        }

        return true;
    }

    /**
     *
     * This example creates credetials specifically for Bank of America
     * The credentails can be different depending on which site you
     * are trying to add.  Usually you use the API
     * ItemManagement.getCredentialForContentService()
     * to get the correct fields to add.  In this case we are hardcoding them.
     *
     * @param loginName the login name to use
     * @param password the password to use on adding
     * @return The list of FieldInfos for the BofA
     */
    public List createCredentialFields(String loginName, String password) {
        List fieldInfoList = new ArrayList();
        String[] valueIdentifier = {"LOGIN"};
        
        ArrayOfString arrayOfString = new ArrayOfString();
        arrayOfString.setElements(valueIdentifier);
        
        FieldInfoSingle fs = new FieldInfoSingle();
        fs.setName(loginName);
        fs.setDisplayName("DisplayName");
        fs.setIsEditable(false);
        fs.setIsOptional(false);
        fs.setIsEscaped(false);
        fs.setHelpText("test");
        fs.setIsOptionalMFA(false);
        fs.setIsMFA(false);        
        fs.setDefaultValue("LOGIN");
        fs.setValidValues(new ArrayOfString(valueIdentifier));
        fs.setDisplayValidValues(null);
        fs.setValueIdentifier("LOGIN");
        fs.setValueMask("");
        fs.setFieldType(FieldType.LOGIN);
        fs.setSize(new Long(10));
        fs.setMaxlength(new Long(20));
        fieldInfoList.add(fs);
        
        
        String[] valueIdentifier1 = {"PASSWORD"};
        
        arrayOfString = new ArrayOfString();
        arrayOfString.setElements(valueIdentifier1);
        
        fs = new FieldInfoSingle();
        fs.setName(loginName);
        fs.setDisplayName("DisplayName");
        fs.setIsEditable(false);
        fs.setIsOptional(false);
        fs.setIsEscaped(false);
        fs.setHelpText("test");
        fs.setIsOptionalMFA(false);
        fs.setIsMFA(false);        
        fs.setDefaultValue("LOGIN");
        fs.setValidValues(new ArrayOfString(valueIdentifier));
        fs.setDisplayValidValues(null);
        fs.setValueIdentifier("LOGIN");
        fs.setValueMask("");
        fs.setFieldType(FieldType.PASSWORD);
        fs.setSize(new Long(10));
        fs.setMaxlength(new Long(20));
        fieldInfoList.add(fs);

        return fieldInfoList;
    }

    /**
     * Main method used to create a new user and prepopulate information.
     *
     * @param args no arguments are expected.
     */
    public static void main(String[] args) {
        // Startup
        try {
            //AccountPrePop.setup();
        } catch (Exception startupEx) {
            System.out.println("Unable to startup system: " + startupEx);
            System.exit(-1);
        }

        //
        String subject = "user_" + Calendar.getInstance().getTime().getTime();
        String keystoreFilename = "C:\\scratch\\ssoClientKeyStore";
        String keystoreAlias = "sso";
        String keystorePassword = "test123";
        String issuer = "test";
        String email = "testemail@yodlee.com";
            System.out.println(
            "SAML INFO:\n\tkeystoreFilename=" + keystoreFilename
            + "\n\tkeystoreAlias=" + keystoreAlias + "\n\tkeystorePassword="
            + keystorePassword + "\n\tissuer=" + issuer + "\n\tsubject="
            + subject);

        // Create OpenSamlHelper
        OpenSamlHelper sh =
            new OpenSamlHelper(
                keystoreFilename, keystoreAlias, keystorePassword);

        // Create SAML Response
        String samlResponseStr = "";

        try {
            
            samlResponseStr = sh.generateResponseString(subject, issuer);
        } catch (Exception e) {
            System.out.println("unable to generate response");
            e.printStackTrace();
        }

        System.out.println(samlResponseStr);

        // Login cobrand
        AccountPrePop accountPrePop = new AccountPrePop();
        accountPrePop.loginCobrand();

        // Register user
        System.out.println("Trying to register " + subject);

        UserContext userContext;

        try {
        	RegisterUser registerUser = new RegisterUser();
            userContext =
                registerUser.ssoRegisterUser(samlResponseStr, issuer, email);
        } catch (Exception ex) {
            throw new RuntimeException(
                "Subject: " + subject + " already exists!");
        }

        // Create the credentials
        List fieldInfoList =
            accountPrePop.createCredentialFields("myLogin", "myPassword");

        // The same fieldInfoList can be reused if the login
        // form the the seperate containers is the same.  If it is
        // not, then you need to create credentials for each container
        // Add Item ( Bank of America (California) )
        long itemId =
            accountPrePop.addItem(userContext, BANK_CSID, fieldInfoList);

        // Save this itemId if you want to implement Sync
        // Add Item ( Bank of America Credit Card (CA) )
        itemId =
            accountPrePop.addItem(userContext, CREDIT_CARD_CSID, fieldInfoList);
      
        // Poll for the refresh status and display item summary if refresh
        // succeeds. (**This step is optional)
        if (accountPrePop.pollRefreshStatus(userContext)) {
            // Done Refreshing
        }

        // Logout User  (This step could potentiall be skipped
        // if you are worrried about too many API calls)
        System.out.println("Logging out " + subject);
        try {
        accountPrePop.logoutUser(userContext);
        } catch (Exception e){
        	e.printStackTrace();
        }
        System.out.println("Done logging out " + subject);
    }

    // FUNCTIONS USED FOR NON-SSO ACCOUNT PRE-POP

    /**
     * Certifies a registered user given the user context.
     *
     * @param userContext user context to certify
     * @return certified UserContext
     */
    public UserContext certifyUser(UserContext userContext) {
        
    	UserContext userContextLocal = null;
    	try {
    		userContextLocal =  userCertification.certify(userContext);
    	} catch(Exception e){
    		e.printStackTrace();
    	}
    	return userContextLocal;
    }

    /**
     * Logs in a registered and certified user to the Yodlee platform.
     *
     * @param loginName the login name to user
     * @param password password to authenticate user
     * @return the UserContext of the logged in user.
     */
    public UserContext loginUser(String loginName, String password) {
        UserInfo userInfo = null;
         try {
        	PasswordCredentials passwordCredentials = new PasswordCredentials();
        	passwordCredentials.setLoginName(loginName);
        	passwordCredentials.setPassword(password);
            userInfo = login.login2(
                cobrandContext, passwordCredentials);
         } catch(Exception e){
        	 e.printStackTrace();
         }

        return userInfo.getUserContext();
    }


}
