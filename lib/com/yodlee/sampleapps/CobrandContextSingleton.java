/*
 * Copyright 2009 Yodlee, Inc.  All Rights Reserved.  Your use of this code 
 * requires a license from Yodlee.  Any such license to this code is 
 * restricted to evaluation/illustrative purposes only. It is not intended 
 * for use in a production environment, and Yodlee disclaims all warranties 
 * and/or support obligations concerning this code, regardless of the terms 
 * of any other agreements between Yodlee and you."
 */
package com.yodlee.sampleapps;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Hashtable;

import javax.xml.rpc.ServiceException;

import com.yodlee.sampleapps.helper.PropertyHelper;
import com.yodlee.soap.collections.Locale;
import com.yodlee.soap.common.CobrandContext;
import com.yodlee.soap.core.login.CobrandUserAccountLockedExceptionFault;
import com.yodlee.soap.core.login.InvalidCobrandCredentialsExceptionFault;
import com.yodlee.soap.core.login.cobrandlogin.CobrandLogin;
import com.yodlee.soap.core.login.cobrandlogin.CobrandLoginServiceLocator;
import com.yodlee.soap.ext.login.CobrandPasswordCredentials;

/**
 * CobrandContextSingleton
 *
 * A Singleton implementation of maintaining the CobrandContext
 *
 */
public class CobrandContextSingleton {

    private long COBRAND_CONTEXT_TIME_OUT = 3 * 60 * 1000; // Time Milliseconds to create a new CobrandContext
    private long created = 0;
    private CobrandContext cobrandContext;
    private CobrandLogin cobrandLoginProxy ;

    // Cobrand Credentials
    private long   cobrandId        = 0 ;
    private String appId            = "";
    private String cobrandLogin     = "";
    private String cobrandPassword  = "";
    private String soapServer = "";
    private String keystoreFilename = "";
    private String keystoreAlias = "";
    private String keystorePassword = "";
    private String issuer = "";
    private InputStream inputStream = null;


    private CobrandContextSingleton()
    {
        // Set created timestamp to 0.  This will force a CobrandLogin first time.
        created = 0;

        // Load Properties from Property File
        loadProperties();

        // Set the SoapServer as a Java Property
        initSoapServer();

        // Startup the SoapClient
                
        CobrandLoginServiceLocator locator = new CobrandLoginServiceLocator();
		String serviceName = locator.getCobrandLoginServiceWSDDServiceName();
		locator.setCobrandLoginServiceEndpointAddress(System.getProperty("com.yodlee.soap.services.url")
				+ "/" + serviceName);
		try {
		 cobrandLoginProxy = locator.getCobrandLoginService();
		}
		catch (ServiceException se){
			se.printStackTrace();
		}
		// Create CobrandLogin Proxy
       // cobrandLoginProxy = (CobrandLogin) ProxyFactory.createProxy("com.yodlee.core.login.CobrandLogin");
    }

    public static CobrandContextSingleton getSingletonObject()
    {
        if (ref == null)
            // it's ok, we can call this constructor
            ref = new CobrandContextSingleton();
        return ref;
    }

    public Object clone()
            throws CloneNotSupportedException
    {
        throw new CloneNotSupportedException();
    }

    private static CobrandContextSingleton ref;


    /**
     * Get the CobrandContext.  If the CobrandContext has not expired, returned
     * cached version.  If it has expired, then login cobrand and get new CobrandContext.
     *
     * @return  CobrandContext
     */
    public CobrandContext getCobrandContext()
    {
        long now = System.currentTimeMillis();
        long expired = created + COBRAND_CONTEXT_TIME_OUT;

        //System.out.println("now = " + now);
        //System.out.println("expired = " + expired );
        //System.out.println("created = " + created );

        if (now >= expired)
        {
            long age = (now - created)/1000/60;
            System.out.println("\tCobrandContext has expired (" + age + " minutes old), creating new one...");

            // Reloading properties, but not necessary
            loadProperties();

            try
            {
                // authentication of a cobrand in the Yodlee software platform and returns
                // a valid CobrandContext if the authentication is successful.
                // This method takes a generic CobrandCredentials argument as the
                // authentication related credentials of the cobrand.
                CobrandPasswordCredentials cobCred = new CobrandPasswordCredentials();
                cobCred.setLoginName(cobrandLogin);
                cobCred.setPassword(cobrandPassword);
                Locale locale = new Locale();

        		locale.setLanguage("en");
                cobrandContext = cobrandLoginProxy.loginCobrand (new Long(cobrandId),
                        appId,
                        locale,
                        new Long(2),
                        cobCred);
            } catch (InvalidCobrandCredentialsExceptionFault icce){
                System.out.println("InvalidCobrandCredentialsException");
            } catch (CobrandUserAccountLockedExceptionFault cuale){
                System.out.println("CobrandUserAccountLockedException");
            } catch (Exception ee){
                System.out.println("Exception: " + ee);
		        ee.printStackTrace();
		        throw new RuntimeException(ee);
            }
 
            created = System.currentTimeMillis() ;

            return cobrandContext;
        }
        else
        {
            long age = (now - created)/1000/60;
            System.out.println("\tCobrandContext is valid (" + age + " minutes old), using cached CobrandContext...");
            return cobrandContext;
        }
    }

    /**
     * Load Properties from property file
     */
    private void loadProperties(){
        Hashtable sampleAppProps = null;

        try {
            sampleAppProps = PropertyHelper.loadProperties ("com.yodlee.sampleapps.SampleApp");
        } catch (Exception e) {
            System.out.println ("Cannot load resource file - com.yodlee.sampleapps.SampleApp. Exiting!!");
            System.exit(-1);
        }

        String readInputFromFile = "";
        try {
            cobrandId         = Long.parseLong((String)sampleAppProps.get("cobrandId")) ;
            appId             = (String)sampleAppProps.get("appId");
            cobrandLogin      = (String)sampleAppProps.get("cobLogin");
            cobrandPassword   = (String)sampleAppProps.get("cobPass");
            soapServer        = ((String)sampleAppProps.get("soapServer")).trim();            
            keystoreFilename  = (String)sampleAppProps.get("keystoreFilename");
            keystoreAlias     = (String)sampleAppProps.get("keystoreAlias");
            keystorePassword  = (String)sampleAppProps.get("keystorePassword");
            issuer            = (String)sampleAppProps.get("issuer");
            readInputFromFile = (String)sampleAppProps.get("readInputFromFile");

        } catch (Exception e) {
            System.out.println ("Exception while reading cobrand context parameters!!");
            e.printStackTrace();
        }
        
        System.out.println();
		inputStream = System.in;
		if (readInputFromFile != null) {
			try {
				inputStream = new FileInputStream(readInputFromFile);	        	
				System.out.println("Reading input from file instead of console.");
				System.out.println();
				System.out.println("Input File: " + readInputFromFile);
				System.out.println();
			} catch (FileNotFoundException e) {
				System.out.print("Cannot find the input file: " + readInputFromFile);
				System.out.println(", read the input from console.");
				System.out.println();
				inputStream = System.in;
			}
		} 
    }

    /**
     * Init the SoapServer
     */
    private void initSoapServer() {
        // Old style is to set java property via command line like:
        // -Dcom.yodlee.soap.services.url=https://64.14.28.218/yodsoap/services
        // If set via the command line, use it or else use the value from the property file
        String _soapServer = System.getProperty("com.yodlee.soap.services.url");
        if(_soapServer == null ){
            System.setProperty("com.yodlee.soap.services.url", soapServer);
        }
        System.out.println("soapServer=" + System.getProperty("com.yodlee.soap.services.url"));
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
    
    public InputStream getInputStream() {
        return inputStream;
    }

}
