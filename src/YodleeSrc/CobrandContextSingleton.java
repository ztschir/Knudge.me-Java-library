/*
 * Copyright 2008 Yodlee, Inc.  All Rights Reserved.  Your use of this code
 * requires a license from Yodlee.  Any such license to this code is
 * restricted to evaluation/illustrative purposes only. It is not intended
 * for use in a production environment, and Yodlee disclaims all warranties
 * and/or support obligations concerning this code, regardless of the terms
 * of any other agreements between Yodlee and you."
 */
package YodleeSrc;

import java.util.Hashtable;
import java.util.Locale;

import javax.xml.rpc.ServiceException;

import com.yodlee.soap.common.CobrandContext;
import com.yodlee.soap.common.CobrandCredentials;
import com.yodlee.soap.core.login.CobrandUserAccountLockedExceptionFault;
import com.yodlee.soap.core.login.InvalidCobrandCredentialsExceptionFault;
import com.yodlee.soap.core.login.cobrandlogin.CobrandLogin;
import com.yodlee.soap.core.login.cobrandlogin.CobrandLoginServiceLocator;
import com.yodlee.soap.ext.login.CobrandPasswordCredentials;

/**
 * CobrandContextSingleton
 *
 * A Singleton implementation of maintaining the CobrandContext.
 * CobrandContext is used to authenticate the partner into Yodlee.
 */
public class CobrandContextSingleton {
    /** Stores the single cobrand context entity. */
    private static CobrandContextSingleton ref;

    /** Stores how long the Context is good before it needs to be renewed. */
    private long COBRAND_CONTEXT_TIME_OUT = 3 * 60 * 1000; // Time Milliseconds to create a new CobrandContext

    /** Stores when the context was created. */
    private long created = 0;

    /** Stores the cobrand context. */
    private CobrandContext cobrandContext;

    /** Stores the Cobrand Login Proxy so it does not need to be recreated. */
    private CobrandLogin cobrandLoginProxy;

    /** Cobrand ID is the Unique ID for partner. */
    private long cobrandId = 0;

    /** App ID is a unique application identifier.  */
    private String appId = "";

    /** Login Name for the Cobrand. */
    private String cobrandLogin = "";

    /** Login Password for the Cobrand. */
    private String cobrandPassword = "";

    /** URL of the Yodlee Soap Server. */
    private String soapServer = "";

    /** Name of the Keystore if SAML is used. */
    private String keystoreFilename = "";

    /** Name of the Key Alias if SAML is used. */
    private String keystoreAlias = "";

    /** Password for the Keystore if SAML is used. */
    private String keystorePassword = "";

    /** Issuer if SAML is used. */
    private String issuer = "";

    private CobrandContextSingleton() {
        // Set created timestamp to 0.  This will force a CobrandLogin 
        // first time.
        created = 0;

        // Load Properties from Property File
        loadProperties();

        // Startup the SoapClient
        try {
            initSoapServer();
        } catch (Exception startupEx) {
            System.out.println("Unable to startup system: " + startupEx);
            System.exit(-1);
        }

        CobrandLoginServiceLocator locator = new CobrandLoginServiceLocator();
		String serviceName = locator.getCobrandLoginServiceWSDDServiceName();
		locator.setCobrandLoginServiceEndpointAddress(System.getProperty("com.yodlee.soap.services.url")
				+ "/" + serviceName);
		try {
		 cobrandLoginProxy = locator.getCobrandLoginService();
		}
		catch (ServiceException se){
			se.printStackTrace();
			throw new RuntimeException(se.getMessage());
		}
    }

    public synchronized static CobrandContextSingleton getSingletonObject() {
        if (ref == null) {
            ref = new CobrandContextSingleton();
        }

        return ref;
    }

    /**
     * Get the CobrandContext.  If the CobrandContext has not expired, returned
     * cached version.  If it has expired, then login cobrand and get new CobrandContext.
     *
     * @return  CobrandContext
     */
    public CobrandContext getCobrandContext() {
        long now = System.currentTimeMillis();
        long expired = created + COBRAND_CONTEXT_TIME_OUT;

        //System.out.println("now = " + now);
        //System.out.println("expired = " + expired );
        //System.out.println("created = " + created );
        if (now >= expired) {
            long age = (now - created) / 1000 / 60;
            System.out.println(
                "\tCobrandContext is has expired (" + age
                + " minutes old), creating new one...");

            // Reloading properties, but not necessary
            loadProperties();

            try {
                // authentication of a cobrand in the Yodlee software platform and returns
                // a valid CobrandContext if the authentication is successful.
                // This method takes a generic CobrandCredentials argument as the
                // authentication related credentials of the cobrand.
            	
            	Locale ENGLISH = Locale.ENGLISH;
            	com.yodlee.soap.collections.Locale locale_english = new com.yodlee.soap.collections.Locale(
						ENGLISH.getCountry(), ENGLISH.getLanguage(), ENGLISH
								.getVariant());
            	
                CobrandCredentials cobCred =
                    new CobrandPasswordCredentials(
                        cobrandLogin, cobrandPassword);
                cobrandContext =
                    cobrandLoginProxy.loginCobrand(new Long(cobrandId), appId, locale_english, new Long(2), cobCred);                
            } catch (InvalidCobrandCredentialsExceptionFault icce) {
                System.out.println("InvalidCobrandCredentialsException");
            } catch (CobrandUserAccountLockedExceptionFault cuale) {
                System.out.println("CobrandUserAccountLockedException");
            } catch (Exception ee) {
                System.out.println("Exception: " + ee);
                ee.printStackTrace();
                throw new RuntimeException(ee);
            }

            created = System.currentTimeMillis();

            return cobrandContext;
        } else {
            long age = (now - created) / 1000 / 60;
            System.out.println(
                "\tCobrandContext is valid (" + age
                + " minutes old), using cached CobrandContext...");

            return cobrandContext;
        }
    }

    /**
     * Load Properties from property file
     */
    private void loadProperties() {
        Hashtable sampleAppProps = null;

        try {
            sampleAppProps =
                PropertyHelper.loadProperties(
                    "com.yodlee.sampleapps.SampleApp");
        } catch (Exception e) {
            System.out.println(
                "Cannot load resource file - com.yodlee.sampleapps.SampleApp. Exiting!!");
            System.exit(-1);
        }

        try {
        	// Check for nulls
        	if(sampleAppProps.get("cobrandId") == null 
			        || "".equals(((String)sampleAppProps.get("cobrandId")).trim())
        			|| sampleAppProps.get("appId") == null 
				|| "".equals(((String)sampleAppProps.get("appId")).trim())
        			|| sampleAppProps.get("cobPass") == null 
				|| "".equals(((String)sampleAppProps.get("cobPass")).trim())
        			|| sampleAppProps.get("cobLogin") == null 
				|| "".equals(((String)sampleAppProps.get("cobLogin")).trim())) {
        		
        		System.out.println("One of the property settings is empty.  In order "
        				+ "to get the Sample Apps to work it requires a properties "
        				+ "file to be in the classpath as "
        				+ "[/com/yodlee/sampleapps/SampleApp.properties] and this file "
        				+ "must contain the following properties in it: ");
        		System.out.println("\tcobrandId");
        		System.out.println("\tappId");
        		System.out.println("\tcobPass");
        		System.out.println("\tcobLogin");
        		System.out.println("\n\nYou can obtain these settings for your Yodlee "
        				+ "project manager.");
        		System.exit(-1);
        		
        	}
        	
            cobrandId = Long.parseLong(((String) sampleAppProps.get("cobrandId")).trim());
            appId = ((String) sampleAppProps.get("appId")).trim();
            cobrandLogin = ((String) sampleAppProps.get("cobLogin")).trim();
            cobrandPassword = ((String) sampleAppProps.get("cobPass")).trim();
            soapServer = ((String) sampleAppProps.get("soapServer")).trim();
            keystoreFilename = (String) sampleAppProps.get("keystoreFilename");
            keystoreAlias = (String) sampleAppProps.get("keystoreAlias");
            keystorePassword = (String) sampleAppProps.get("keystorePassword");
            issuer = (String) sampleAppProps.get("issuer");
        } catch (Exception e) {
            System.out.println(
                "Exception while reading cobrand context parameters!!");
            e.printStackTrace();
        }
    }

    /**
     * Init the SoapServer
     */
    private void initSoapServer() throws Exception {
        // Old style is to set java property via command line like:
        // -Dcom.yodlee.soap.services.url=https://64.14.28.218/yodsoap/services
        // If set via the command line, use it or else use the value from the property file
        String _soapServer = System.getProperty("com.yodlee.soap.services.url");

        if (_soapServer == null) {
            System.setProperty("com.yodlee.soap.services.url", soapServer);
        }

        System.out.println(
            "soapServer=" + System.getProperty("com.yodlee.soap.services.url"));

        String mode = System.getProperty("com.yodlee.appmode");

        /*if ("EJB".equalsIgnoreCase(mode)) {
            ClientStartup.startup();
            System.out.println("Started in EJB client mode!");
        } else if ("INPROC".equalsIgnoreCase(mode)) {
            InprocStartup.startup();
            System.out.println("Started in INPROC mode!");
        } else if ("SOAP".equalsIgnoreCase(mode)) {
            SoapClientStartup.startup();
            System.out.println("Started in SOAP client mode!");
        } else {
            System.out.println(
                "The Yodlee Application must be set "
                + "to run in as SOAP, INPROC or EJB mode.  The "
                + "Yodlee Sample Applications are generally run "
                + "in SOAP mode.  This setting can be made by "
                + "setting the system property "
                + "com.yodlee.appmode to SOAP by specifying "
                + "-Dcom.yodlee.appmode=SOAP on the command "
                + "line when starting the application.");
            throw new RuntimeException(
                "Must specify com.yodlee.appmode "
                + "to be EJB, INPROC, or SOAP");
        }*/
    }

    public String getKeystoreFilename() {
        return keystoreFilename;
    }

    public String getKeystoreAlias() {
        return keystoreAlias;
    }

    public String getKeystorePassword() {
        return keystorePassword;
    }

    public String getIssuer() {
        return issuer;
    }
}
