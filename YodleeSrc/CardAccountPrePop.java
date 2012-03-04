/*
 * Copyright 2009 Yodlee, Inc.  All Rights Reserved.  Your use of this code 
 * requires a license from Yodlee.  Any such license to this code is 
 * restricted to evaluation/illustrative purposes only. It is not intended 
 * for use in a production environment, and Yodlee disclaims all warranties 
 * and/or support obligations concerning this code, regardless of the terms 
 * of any other agreements between Yodlee and you."
 */
package YodleeSrc;

import java.util.Calendar;
import java.util.Hashtable;

import javax.xml.rpc.ServiceException;

import com.yodlee.soap.collections.Locale;
import com.yodlee.soap.common.Address;
import com.yodlee.soap.common.CalendarDate;
import com.yodlee.soap.common.CardType;
import com.yodlee.soap.common.CobrandContext;
import com.yodlee.soap.common.CobrandCredentials;
import com.yodlee.soap.common.Country;
import com.yodlee.soap.common.PhoneNumber_US;
import com.yodlee.soap.common.UserContext;
import com.yodlee.soap.common.UserCredentials;
import com.yodlee.soap.core.IllegalArgumentValueExceptionFault;
import com.yodlee.soap.core.login.CobrandUserAccountLockedExceptionFault;
import com.yodlee.soap.core.login.InvalidCobrandCredentialsExceptionFault;
import com.yodlee.soap.core.login.UserInfo;
import com.yodlee.soap.core.login.cobrandlogin.CobrandLogin;
import com.yodlee.soap.core.login.cobrandlogin.CobrandLoginServiceLocator;
import com.yodlee.soap.core.login.login.Login;
import com.yodlee.soap.core.login.login.LoginServiceLocator;
import com.yodlee.soap.core.paymentservice.CardPaymentAccount;
import com.yodlee.soap.core.paymentservice.PaymentAccount;
import com.yodlee.soap.core.paymentservice.PaymentAccountAlreadyExistsExceptionFault;
import com.yodlee.soap.core.paymentservice.PaymentAccountNicknameAlreadyInUseExceptionFault;
import com.yodlee.soap.core.paymentservice.paymentaccountmanagement.PaymentAccountManagement;
import com.yodlee.soap.core.paymentservice.paymentaccountmanagement.PaymentAccountManagementServiceLocator;
import com.yodlee.soap.core.usermanagement.State_US;
import com.yodlee.soap.core.usermanagement.UserNameExistsExceptionFault;
import com.yodlee.soap.core.usermanagement.userregistration.UserRegistration;
import com.yodlee.soap.core.usermanagement.userregistration.UserRegistrationServiceLocator;
import com.yodlee.soap.ext.login.CobrandPasswordCredentials;
import com.yodlee.soap.ext.saml.SAMLCredentials;


/**
 * This class demonstrates how to use the Yodlee SDK to do use SAML
 * authentication to register a user and do pre-population of a card
 * account.
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
 * 
 * @author Jordan Reed
 */

public class CardAccountPrePop
{
    /** 
     * Holds the Cobrand Login Proxy.  This proxy is used to log
     * the cobrand into the SDK environment.
     */
    protected CobrandLogin cobrandLoginProxy;

    /** 
     * Holds the User Registration Proxy. This is used to register
     * the user.
     */
    protected UserRegistration userRegistration;
    
    /** 
     * Holds the Login Proxy.  The proxy should not be needed.
     * It is included to make a recovery attempt if a user already
     * exists when the registration method is called.
     */
    protected Login login;
    
    /** 
     * Holds the Payment Account Manager proxy. This proxy is used
     * to add the card payment account for the user.
     * */
    protected PaymentAccountManagement paymentAccountManagement;

    /** Holds the cobrandContext. */
    protected CobrandContext cobrandContext;
    
    /**
     * Application property file which holds configuration options.
     */
    public static Hashtable appProps;
    
    /**
     *  Simple flag to track if the SDK core has been started.
     */
    private static boolean coreStarted;

    /**
     * Default constructor.  This populates the proxies and loads
     * the app properties file.
     */
    public CardAccountPrePop ()
    {
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
		
				
		LoginServiceLocator locator2 = new LoginServiceLocator();
         serviceName = locator2.getLoginServiceWSDDServiceName();
        locator2.setLoginServiceEndpointAddress(System.getProperty("com.yodlee.soap.services.url")
						+ "/" + serviceName);
        try {
        	login = locator2.getLoginService();
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
		
		PaymentAccountManagementServiceLocator locator6 = new PaymentAccountManagementServiceLocator();
		serviceName = locator6.getPaymentAccountManagementServiceWSDDServiceName();
		locator6.setPaymentAccountManagementServiceEndpointAddress(System.getProperty("com.yodlee.soap.services.url")
				+ "/" + serviceName);
		try {
			paymentAccountManagement = locator6.getPaymentAccountManagementService();
		}
		catch (ServiceException se){
			se.printStackTrace();
		}
        
        
        // Load cobrand context parameters from SampleApps.properties
        try {
            appProps = PropertyHelper.loadProperties ("com.yodlee.sampleapps.SampleApp");
        } catch (Exception e) {
            System.out.println ("Cannot load resource file - com.yodlee.sampleapps.SampleApp. Exiting!!");
            System.exit(-1);
        }

    }

    /**
     * Initializes & Start the CORE basing on the provided Startup Option
     *
     * @throws Exception
     */
    static synchronized void setup () throws Exception
    {
        if (!coreStarted) {
            
            coreStarted = true;
        }
    }

    /**
     * Logs in the Cobrand and populates the cobrandContext property
     * of the object.
     */
    public void loginCobrand(){
        long   cobrandId        = 0 ;
        String appId            = "changeMe";
        String cobrandLogin     = "changeMe";
        String cobrandPassword  = "changeMe";

        try {
            cobrandId       = Long.parseLong((String)appProps.get("cobrandId")) ;
            appId           = (String)appProps.get("appId");
            cobrandLogin    = (String)appProps.get("cobLogin");
            cobrandPassword = (String)appProps.get("cobPass");
        } catch (Exception e) {
            System.out.println ("Exception while reading cobrand context parameters!!");
            e.printStackTrace();
        }

        CobrandPasswordCredentials cobCred = new CobrandPasswordCredentials();
        cobCred.setLoginName(cobrandLogin);
        cobCred.setPassword(cobrandPassword);
       
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

        coreStarted=false;
    }


    /**
     * Registers a new user for the Yodlee application using SAML.
     * 
     * @param samlResponse the response string that is used to authenticate
     *        the user to Yodlee.
     * @param issuerId The issuer ID for the reponse string.
     * @return The active context that can used to make SDK calls on
     *         behalf of the user
     */
    public UserContext ssoRegisterUser(String samlResponse, String issuerId)
    {
        SAMLCredentials samlCred = null;
        UserInfo userInfo = null;

        if(cobrandContext == null){
            System.out.println("cobrandContext is null");
            System.exit(0);
        }

        // Create Saml Credentials
        System.out.println("creating saml credentials");
        samlCred = new SAMLCredentials();
        samlCred.setSamlResponse(samlResponse);
        samlCred.setIssuerId(issuerId);

        
            
        // Register
        System.out.println("registering user");
        try {
         userInfo = userRegistration.register3(cobrandContext, samlCred, null, null);
        } catch (Exception e){
        	e.printStackTrace();
        }
        System.out.println("user registered");

        return userInfo.getUserContext();
    }

    /**
     * Logs the user into the Yodlee SDK.
     * 
     * @param samlResponse the response string that is used to authenticate
     *        the user to Yodlee.
     * @param issuerId The issuer ID for the reponse string.
     * @return The active context that can used to make SDK calls on
     *         behalf of the user
     */
    public UserContext ssoLoginUser( String samlResponseStr, String issuer){
        UserInfo userInfo=null;
        SAMLCredentials userCred= null;
        UserContext userContext=null;

        // Test for null
        if(samlResponseStr == null){System.out.println("samlResponseStr =  null");}
        if(issuer == null){System.out.println("issuer =  null");}

        System.out.println("creating saml credentials");
        userCred = new SAMLCredentials();
        userCred.setSamlResponse(samlResponseStr);
        userCred.setIssuerId(issuer);
                
       
        System.out.println("login user");
        try {
        userInfo= login.login2(cobrandContext, userCred);
        } catch(Exception e){
        	e.printStackTrace();
        }
        System.out.println("user logged in");

        System.out.println("getting user context");
        userContext= userInfo.getUserContext();

        return userContext;
    }
    
    /**
     * Logs out a user from the Yodlee platform.
     * 
     * @param userContext the active context for the user
     */
    public void logoutUser (UserContext userContext)
    {
        try {
    	login.logout (userContext);
        } catch(Exception e){
        	e.printStackTrace();
        }
    }

    /** 
     * Create CardPayment Account with Fake Data
     * @return CardPaymentAccount
     */
    public CardPaymentAccount createSampleCardAccount()
    {

        String number = "4388575029061111";
        System.out.print("Enter in a Discover credit card # [" + number + "]:");
        String input = IOUtils.readStr();
        if(input != null){
            number = input;
        }
        Address address = new Address();
        address.setAddress1("Address 1");
        address.setAddress2("Address 2");
        address.setApartmentOrSuiteNumber("Apt #2");
        address.setCity("San Francisco");
        address.setState(State_US.CALIFORNIA);
        address.setZipCode1("94111");
        address.setZipCode2("1234");
        address.setCountry(Country.US);
 

        CalendarDate expirationDate = new CalendarDate();
        expirationDate.setYear(2005);
        expirationDate.setMonth(Calendar.AUGUST);
        expirationDate.setDayOfMonth(27);
      
        PhoneNumber_US phoneNumber = new PhoneNumber_US();
        phoneNumber.setAreaCode("800");
        phoneNumber.setPrefix("555");
        phoneNumber.setLineNumber("1212");
        
        CardPaymentAccount cardPaymentAccount = new CardPaymentAccount();
       
        cardPaymentAccount.setDescription("Description");
        cardPaymentAccount.setNickname("Nickname");
        cardPaymentAccount.setIsVerified(true);
        cardPaymentAccount.setNameOnCard("Name on Card");
        cardPaymentAccount.setCardNumber(number);
        cardPaymentAccount.setExpirationDate(expirationDate);
        cardPaymentAccount.setCardType(CardType.DISCOVER_CARD);
        cardPaymentAccount.setBillingAddress(address);
        cardPaymentAccount.setPhoneNumber(phoneNumber);
       
        
        return cardPaymentAccount;
    }

    /**
     * Adds a payment account to an active user context.  This will
     * generate a payment account for the user.
     * 
     * @param userContext the active user context to add a payment account to
     */
    public void addPaymentAccount(UserContext userContext)
    {
        CardPaymentAccount cpa = createSampleCardAccount();
                
        try{
        	PaymentAccount paymentAccount = paymentAccountManagement.addPaymentAccount(userContext, cpa);
        	
        	System.out.println("Payment Account added [" + paymentAccount + "]");
            
        }catch(PaymentAccountAlreadyExistsExceptionFault paaee){
            System.out.println("Payment Account Already Exists");
            return;
        }catch(PaymentAccountNicknameAlreadyInUseExceptionFault panaiue){
            System.out.println("Payment Account Nickname Already In Use");
            return;
        }catch(IllegalArgumentValueExceptionFault iave){
            System.out.println("Ellegal Argument Value");
            return;
        }catch(Exception e){

        } 
    }

    /**
     * Main method to run the sample.  This sample will
     * (1) login the cobrand, (2) register a user, (3) add a payment
     * account for the user, (4) logout the user.
     * 
     * @param args
     * @throws Exception
     */
    public static void main (String args[]) throws Exception
    {
        // Startup
        try {
        	CardAccountPrePop.setup();
        } catch (Exception startupEx) {
            System.out.println ("Unable to startup system: " + startupEx);
            System.exit (-1);
        }

        CardAccountPrePop accountPrePop  = new CardAccountPrePop ();

        String subject   =   "user_" + Calendar.getInstance().getTime().getTime() ;
        // String subject = "jordantsoaptest";
        
        String keystoreFilename  = (String)CardAccountPrePop.appProps.get("keystoreFilename");
    	String keystoreAlias = (String)CardAccountPrePop.appProps.get("keystoreAlias");
    	String keystorePassword = (String)CardAccountPrePop.appProps.get("keystorePassword");
        String issuer = (String)CardAccountPrePop.appProps.get("issuer");
        
        /*
        String keystoreFilename = "C:\\javadev\\yodlee\\sdkTools\\keystore\\discoverKeyStore";
        String keystoreAlias = "discover";
        String keystorePassword = "changeit";
        String issuer = "discovercard.yodlee.com";
        */

        System.out.println("SAML INFO:\n\tkeystoreFilename=" + keystoreFilename +
                "\n\tkeystoreAlias=" + keystoreAlias +
                "\n\tkeystorePassword=" + keystorePassword +
                "\n\tissuer=" + issuer +
                "\n\tsubject=" + subject );

        // Create OpenSamlHelper
        OpenSamlHelper sh = new OpenSamlHelper(keystoreFilename,keystoreAlias, keystorePassword);

        // Create SAML Response
        String samlResponseStr = "";
        samlResponseStr = sh.generateResponseString(subject, issuer);
        System.out.println(samlResponseStr);

        // Login cobrand
        accountPrePop.loginCobrand();

        
        // Register user
        System.out.println ("Trying to register " + subject  );
        UserContext userContext;
        try {
            userContext = accountPrePop.ssoRegisterUser(samlResponseStr, issuer );
            System.out.println("User registered.");
        } catch (Exception ex) {
            System.out.println("Subject: " + subject + " already exists. Logging in.");
            userContext = accountPrePop.ssoLoginUser(samlResponseStr, issuer);
            System.out.println("User logged in.");
        }


        // Add a new payment accont for the user
        accountPrePop.addPaymentAccount(userContext);        
        
        // This a is a security vs. speed trade off.  When
        // the prepolation is occuring syncronously and the user
        // is waiting for it to complete, it is often skipped to improve
        // user experience.
        // System.out.println ("Logging out " + subject);
        // accountPrePop.logoutUser (userContext);
        // System.out.println ("Done logging out " + subject);
    }

}
    