/*
 * Copyright 2009 Yodlee, Inc.  All Rights Reserved.  Your use of this code 
 * requires a license from Yodlee.  Any such license to this code is 
 * restricted to evaluation/illustrative purposes only. It is not intended 
 * for use in a production environment, and Yodlee disclaims all warranties 
 * and/or support obligations concerning this code, regardless of the terms 
 * of any other agreements between Yodlee and you.
 */
package com.yodlee.sampleapps;

import javax.xml.rpc.ServiceException;
import java.rmi.RemoteException;

import com.yodlee.sampleapps.helper.IOUtils;
import com.yodlee.soap.common.NVPair;
import com.yodlee.soap.common.UserContext;
import com.yodlee.soap.core.preferencemanagement.PreferenceKeyNotFoundExceptionFault;
import com.yodlee.soap.core.preferencemanagement.userpreferencemanagement.UserPreferenceManagement;
import com.yodlee.soap.core.preferencemanagement.userpreferencemanagement.UserPreferenceManagementServiceLocator;
import com.yodlee.soap.collections.ArrayOfString;
import com.yodlee.soap.collections.List;

/**
 * Encapsulates user preference management functionality
 * in the Yodlee software platform.
 */
public class UserPreferences extends ApplicationSuper {

	UserPreferenceManagement userPreferenceManagement;

    static final String PREFIX = "com.yodlee.sampleapps.preference";
    static final String APP_NAME = "application";
    private UserContext userContext;

    private static int OPTION_CNT = 0;
    private static int NAV_VIEW_ALL_MEM_PREFS = OPTION_CNT++;
    private static int NAV_VIEW_MEM_PREFS = OPTION_CNT++;
    private static int NAV_ADD_PREFERENCE = OPTION_CNT++;
    private static int NAV_DELETE_PREFERENCE = OPTION_CNT++;
    
    /**
     * The type of the exception if an argument value does not exist (or is
     * not found, if the argument is an identifier).
     * <p>
     * The details of the argument value that is not found can be retrieved
     * by calling the
     * {@link com.yodlee.core.IllegalArgumentValueException#getValue
     * <code>getValue()</code>} method on the exception.
     */
    public static final int VALUE_NOT_FOUND = 3;
    
    /**
     * The type of the exception, if a multi-valued argument generates
     * more than one
     * {@link com.yodlee.core.CoreException <code>CoreException</code>}
     * related to it's multi-valued arguments.
     * Rather than require a caller to incrementally identify each
     * exception case through the burden of multiple calls, a multi-valued
     * type denotes that the argument is illegal in more than one way.
     * <p>
     * If this exception type is identified, the caller must invoke the
     *{@link com.yodlee.core.IllegalArgumentValueException#getWrappedExceptions
     * <code>getWrappedExceptions</code>}
     * method to recover all the encapsulated exceptions within.
     */
    public static final int MULTIPLE_EXCEPTIONS = 6;
    
    /**
     * The type of this <code>NVPair</code> if it encapsulates a
     * multiple values.
     */
    public final static int MULTI_VALUE = 1;
    
    /**
     * The type of this <code>NVPair</code> if it encapsulates a single
     * value.
     */
    public final static int SINGLE_VALUE = 0;

    /**
     * Constructs an instance of the UserPreferences class that
     * provides the functionality to set, get, and delete user
     * preferences.
     */
    public
    UserPreferences (UserContext userContext)
    {
        super ();
        this.userContext = userContext;
        UserPreferenceManagementServiceLocator locator = new UserPreferenceManagementServiceLocator();
        String serviceName = locator.getUserPreferenceManagementServiceWSDDServiceName();
        locator.setUserPreferenceManagementServiceEndpointAddress(System.getProperty("com.yodlee.soap.services.url")
						+ "/" + serviceName);
        try {
        	userPreferenceManagement = locator.getUserPreferenceManagementService();
		} catch (ServiceException se) {
			throw new RuntimeException(se);
		}
    }
    public void userPreferencesMenu() {
        System.out.println("Choose one of the partial account management options");
        System.out.println("********************");
        System.out.println(NAV_VIEW_ALL_MEM_PREFS + ". View all preferences");
        System.out.println(NAV_VIEW_MEM_PREFS + ". View preferences");
        System.out.println(NAV_ADD_PREFERENCE + ". Set preference");
        System.out.println(NAV_DELETE_PREFERENCE + ". Delete preference");
        System.out.println("********************");

        System.out.print("Choice: ");
        int choice = IOUtils.readInt();
        System.out.println();
        try{
            if (choice == NAV_VIEW_ALL_MEM_PREFS) {
                displayAllMemPrefs(userContext);
            } else if (choice == NAV_VIEW_MEM_PREFS) {
                displayMemPref(userContext);
            } else if (choice == NAV_ADD_PREFERENCE) {
                setPreference(userContext);
            } else if (choice == NAV_DELETE_PREFERENCE) {
                System.out.println("Not Implemented");
            }
        }
        catch (Exception e) {
            System.out.println("Exception : " + e);
            e.printStackTrace();
        }
    }

    public void displayAllMemPrefs(UserContext userContext) {
        List prefArray;
        Object [] prefs;
        
        try {
        	prefArray = userPreferenceManagement.getAllPreferences(userContext);
        } catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
        if (prefArray != null) {
        	prefs = prefArray.getElements();
        	for (int i = 0; i < prefs.length; i++) {
        		NVPair pref = (NVPair) prefs[i];
        		if (pref.getType() == MULTI_VALUE) {
                    System.out.println("Name=" + pref.getName());
                    System.out.println("  -  Value: " + pref.getValues().getElements(0));
                    for (i=0; i< pref.getValues().getElements().length; i++) {
                    	System.out.println("  -  Value: " + pref.getValues().getElements(i));
                    }
                } else {
                	System.out.println("Name=" + pref.getName() + "  -  Value: " + pref.getValues().getElements(0));
                }
        	}
        }
    }
    
    public void displayMemPref(UserContext userContext) {
        System.out.print("Preference Key Name: ");
        String prefKeyName = IOUtils.readStr("personalInfo.EXAMPLE");
        
        try {
	        NVPair userPreference = userPreferenceManagement.getPreference(userContext, prefKeyName);
    	    List valueList = userPreference.getValues();
        	String value = (String) valueList.getElements(0);
        
	        System.out.println("Value = " + value);
	    } catch (RemoteException e) {
	        System.out.println("Caught remote exception: " + e);
	    }
    }

    /**
     * Sets user preferences.
     */
    public void setPreference (UserContext userContext) {
        System.out.print("Preference Key Name: ");
        String prefKeyName = IOUtils.readStr("personalInfo.EXAMPLE");
        NVPair singlePref = null;
        Object value = null;
        try {
            singlePref = userPreferenceManagement.getPreference (userContext, prefKeyName);
            System.out.println("Preference Found.  Update Value.");
            List  valueList = singlePref.getValues();
            
            if (valueList != null){
            	value = valueList.getElements(0);
            }
            System.out.print("Preference Key Value [" + value + "]: ");
        } catch (PreferenceKeyNotFoundExceptionFault pknfEx) {
            System.out.println("Preference Not Found.  Create New.");
            singlePref = new NVPair();
           
            singlePref.setName(prefKeyName);
           
        } catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
       
        
        System.out.println("enter the preference value: ");
        String prefKeyValue = IOUtils.readStr((String)value);
        List prefKeyValues = new List();
        prefKeyValues.setElements(new Object[]{prefKeyValue});
                
        singlePref.setValues(prefKeyValues);
       
        singlePref.setType(SINGLE_VALUE);
        

        System.out.println("Setting preference: " + singlePref.getValues().getElements(0));
        try {
        	
	        userPreferenceManagement.setPreference (userContext, singlePref);
	        System.out.println("The user preference has been set\n");
        } catch (Exception e) {
        	e.printStackTrace();
        	throw new RuntimeException(e.getMessage());
		}
    }


    /**
     * Gets user preferences.
     */
    public
    void getPreferences (UserContext userContext)
    {
        try {
            NVPair pref = userPreferenceManagement.getPreference (userContext,
                                                                  PREFIX);
            System.out.println ("Obtained preference: (key = " +
                                PREFIX + ", value = " + pref.getValues ());
        } catch (PreferenceKeyNotFoundExceptionFault pknfEx) {
            System.out.println ("Preference key:  " + PREFIX + " not found!");
        } catch (Exception e) {
			e.printStackTrace();
		}

        try {
            String[] prefKeys = new String[5];
            for (int i = 0; i < 5; i++) {
                prefKeys[i] = PREFIX + i;
            }
            ArrayOfString arrayOfString = new ArrayOfString();
            arrayOfString.setElements(prefKeys);
            List prefs = userPreferenceManagement.getPreferences (userContext,
            		arrayOfString);

           
            if (prefs != null) {
            
            	for (int p = 0; p < prefs.getElements().length; p++) {
            		NVPair pref = (NVPair) prefs.getElements(p);
            		System.out.println ("Obtained preference: (key = " +
                            pref.getName () +
                            ", value = " + pref.getValues ());
            	}
            }
        } catch (PreferenceKeyNotFoundExceptionFault pknfEx) {
        	System.out.println("The preference key was not found");
        } catch (Exception e) {
			e.printStackTrace();
		}
    }

    /**
     * Deletes user preferences.
     */
    public
    void deletePreferences (UserContext userContext)
    {
        try {
            userPreferenceManagement.deletePreference (userContext, PREFIX);
            System.out.println ("Deleted preference: (key = " + PREFIX);
        } catch (PreferenceKeyNotFoundExceptionFault pknfEx) {
            System.out.println ("Preference key:  " + PREFIX + " not found!");
        } catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}

        try {
            String[] prefKeys = new String[5];
            for (int i = 0; i < 5; i++) {
                prefKeys[i] = PREFIX + i;
            }
            ArrayOfString arrayOfString = new ArrayOfString();
            arrayOfString.setElements(prefKeys);
            userPreferenceManagement.deletePreferences (userContext, arrayOfString);
            /*userPreferenceManagement.deletePreferences (userContext, new ArrayOfString(prefKeys));*/
        } catch (PreferenceKeyNotFoundExceptionFault pknfEx) {
            System.out.println("The preference key was not found");
            throw new RuntimeException(pknfEx.getMessage());
        } catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
    }    

    /**
     * Registers and then certifies the user in the Yodlee software platform.
     */
    public
    static void main (String args[])
    {
        if (args.length < 2) {
            throw new RuntimeException ("Usage: <username> <password>") ;
        }

        // Startup
        /*
        try {
            InitializationHelper.setup ();
        } catch (Exception startupEx) {
            System.out.println ("Unable to startup system: " + startupEx);
            System.exit (-1);
        }
        */

        String  userName   =   args[0] ;
        String  password   =   args[1] ;

        LoginUser loginUser = new LoginUser ();
        UserContext userContext = null;

        System.out.println ("1. Logging in user " + userName);
        userContext = loginUser.loginUser (userName, password);

        UserPreferences userPreferences = new UserPreferences (userContext);

        System.out.println ("2. Setting preferences for user " + userName);
        userPreferences.setPreference (userContext);

        System.out.println ("3. Getting preferences for user " + userName);
        userPreferences.getPreferences (userContext);

        System.out.println ("4. Deleting preferences for user " + userName);
        userPreferences.deletePreferences (userContext);

        /*
        System.out.println ("5. Getting preferences for user " + userName);
        userPreferences.getPreferences (userContext);
        */

        System.out.println ("5. Logging out user " + userName);
        loginUser.logoutUser (userContext);
    }
}
