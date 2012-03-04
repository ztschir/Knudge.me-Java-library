/*
 * Copyright 2009 Yodlee, Inc.  All Rights Reserved.  Your use of this code 
 * requires a license from Yodlee.  Any such license to this code is 
 * restricted to evaluation/illustrative purposes only. It is not intended 
 * for use in a production environment, and Yodlee disclaims all warranties 
 * and/or support obligations concerning this code, regardless of the terms 
 * of any other agreements between Yodlee and you."
 */
package com.yodlee.sampleapps;

import java.util.ArrayList;
import java.util.List;

import javax.xml.rpc.ServiceException;

import com.yodlee.sampleapps.helper.IOUtils;
import com.yodlee.soap.common.NVPair;
import com.yodlee.soap.common.UserContext;
import com.yodlee.soap.core.preferencemanagement.PreferenceKeyNotFoundExceptionFault;
import com.yodlee.soap.core.preferencemanagement.userpreferencemanagement.UserPreferenceManagement;
import com.yodlee.soap.core.preferencemanagement.userpreferencemanagement.UserPreferenceManagementServiceLocator;
import com.yodlee.soap.ext.traversal.contentservicetraversal.ContentServiceTraversal;
import com.yodlee.soap.ext.traversal.contentservicetraversal.ContentServiceTraversalServiceLocator;

/**
 * This helper class allows a user to add, delete and view their partial accounts
 * Partial accounts in yodlee are simply user prefs.
 */
public class PartialAccounts extends ApplicationSuper {

	protected ContentServiceTraversal cst;
    protected UserPreferenceManagement upm;
    protected UserContext userContext;

    private static int OPTION_CNT = 0;
    private static int NAV_VIEW_PARTIAL_ACCOUNTS = OPTION_CNT++;
    private static int NAV_ADD_PARTIAL_ACCOUNTS = OPTION_CNT++;
    private static int NAV_DELETE_PARTIAL_ACCOUNTS = OPTION_CNT++;
    private static int NAV_VIEW_ALL_MEM_PREFS = OPTION_CNT++;



    public PartialAccounts(UserContext userContext) {
        super();
    	this.userContext = userContext;
    	UserPreferenceManagementServiceLocator locator = new UserPreferenceManagementServiceLocator();
        String serviceName = locator.getUserPreferenceManagementServiceWSDDServiceName();
        locator.setUserPreferenceManagementServiceEndpointAddress(System.getProperty("com.yodlee.soap.services.url")
						+ "/" + serviceName);
        try {
        	upm = locator.getUserPreferenceManagementService();
		} catch (ServiceException se) {
			throw new RuntimeException(se);
		}
		ContentServiceTraversalServiceLocator locator1 = new ContentServiceTraversalServiceLocator();
		String serviceName1 = locator1.getContentServiceTraversalServiceWSDDServiceName();
		locator1.setContentServiceTraversalServiceEndpointAddress(System.getProperty("com.yodlee.soap.services.url")
						+ "/" + serviceName1);
		try {
			cst = locator1.getContentServiceTraversalService();

		} catch (ServiceException se) {
			throw new RuntimeException(se);
		}
    }
    
    public void partialAccountsMenu() {
        System.out.println("Choose one of the partial account management options");
        System.out.println(NAV_VIEW_PARTIAL_ACCOUNTS + ". View all partial accounts");
        System.out.println(NAV_ADD_PARTIAL_ACCOUNTS + ". Add partial account");
        System.out.println(NAV_DELETE_PARTIAL_ACCOUNTS + ". Delete partial account");
		System.out.println(NAV_VIEW_ALL_MEM_PREFS + ". View all mem prefs");
        System.out.println("********************");

        System.out.print("Choice: ");
        int choice = IOUtils.readInt();
        System.out.println();
        try{
            if (choice == NAV_VIEW_PARTIAL_ACCOUNTS) {
                viewPartialAccounts();
            } else if (choice == NAV_ADD_PARTIAL_ACCOUNTS) {
                addPartialAccount();
            } else if (choice == NAV_DELETE_PARTIAL_ACCOUNTS) {
                deletePartialAccount();
            } else if (choice == NAV_VIEW_ALL_MEM_PREFS) {
				displayAllMemPrefs();
			}
        }
        catch (Exception e) {
            System.out.println("Exception : " + e);
            e.printStackTrace();
        }
    }

    /**
     * View all the partial accounts
     */
    public void viewPartialAccounts() {
		List csidList = getPartialAccounts();
		System.err.println("User currently has the following partial accounts: " +
			csidList + "\n");
    }

    public List getPartialAccounts() {
		List csidList = new ArrayList();
		try
		{
			NVPair nvPair = upm.getPreference(userContext, "addaccount.incomplete");
			if (nvPair == null) {
				// no nvPair - set empty list
			} else if (nvPair.getType() == UserPreferences.SINGLE_VALUE) {
				csidList.add(nvPair.getValues().getElements(0));
			} else {
				// multi value
				Object[] values = nvPair.getValues().getElements();
				if (values != null) {
					for (int i = 0; i < values.length; i++) {
						csidList.add(values[i]);
					}
				}
			}
		} catch(PreferenceKeyNotFoundExceptionFault pknfe) {
			// partial accounts not set yet, return an empty array			
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		}


		return csidList;
	}

	public void displayAllMemPrefs() {		
		Object[] prefs = null;
		com.yodlee.soap.collections.List prefList;
		try {
			prefList = upm.getAllPreferences(userContext);
			if (prefList != null){
				prefs = prefList.getElements();
			}
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
		if (prefs != null) {
			for (int p = 0; p < prefs.length; p++) {			
				NVPair pref = (NVPair) prefs[p];
				if (pref.getType() == UserPreferences.MULTI_VALUE) {
					System.out.println("Name=" + pref.getName());
					if (pref.getValues() != null){
						Object [] values = pref.getValues().getElements();
						for (int i=0; i<values.length; i++){
							System.out.println("Value: " + values[i]);
						}
					}
					
				} else {
					System.out.println("Name=" + pref.getName() + "  -  Value: " + pref.getValues().getElements(0));
				}
			}			
		}
	}


    /**
     * Add a sum info as a partial account.
     * This does no validation of the sum info that is being added
     */
    public void addPartialAccount() {
        System.out.print("Add the sum info to add as a partial account: ");
        long sumInfo = IOUtils.readLong();

		List partialAccountList = getPartialAccounts();

			// first check to see if the partial account to add
			// is already in there
			// We use the 'newPartialAccountList' list
			// structure to maintain the resulting list of partial
			// accounts
			List newPartialAccountList = new ArrayList();

			if(partialAccountList == null)
			{
				// there are no partial accounts yet
				// this will be the first one
				// NOTE: yodlee stores partial accounts as strings
				newPartialAccountList.add(Long.toString(sumInfo));
			}
			else
			{
				// there are already partial accounts, loop through
				// them to see if this one is already added
				boolean alreadyAdded = false;
				for(int i = 0;i<partialAccountList.size();i++)
				{
					long currPartialAccountId =
						Long.parseLong((String)partialAccountList.get(i));
					if (currPartialAccountId == sumInfo)
					{
						// already added - don't re add
						System.err.println("Partial account already added: " +
							sumInfo);
						alreadyAdded = true;
					}
					newPartialAccountList.add(Long.
						toString(currPartialAccountId));
				}

				if(!alreadyAdded)
				{
					newPartialAccountList.add(Long.
						toString(sumInfo));
				}
			}

			System.out.println("Going to set the partial accounts after addition to: " +
					newPartialAccountList);

			// at this point, newPartialAccountList
			// is the correct list of partial accounts
			// just need to save it as a Yodlee user preference
			NVPair nvPair = new NVPair();
			nvPair.setName("addaccount.incomplete");
			com.yodlee.soap.collections.List list = new com.yodlee.soap.collections.List();
			list.setElements(newPartialAccountList.toArray());
			nvPair.setValues(list);
			
			if (newPartialAccountList.size() == 1)
				nvPair.setType(new Integer(UserPreferences.SINGLE_VALUE));
			else
				nvPair.setType(new Integer(UserPreferences.MULTI_VALUE));

			try {
				upm.setPreference(userContext,nvPair);
				System.out.println("Partial account " + newPartialAccountList + " successfully added\n");
			} catch (Exception e) {				
				throw new RuntimeException("Error adding partial account..." + e.getMessage());
			}
    }

    public void deletePartialAccount() {
        System.out.print("Enter the sum info to remove as a partial account: ");
        long sumInfo = IOUtils.readLong();

		// gets the existing list of partial accounts
		List partialAccountList = getPartialAccounts();

		if(partialAccountList != null &&
			partialAccountList.size() != 0)
		{
			// partial accounts exist,
			// loop through them and remove the
			// appropriate account.
			// newPartialAccountList is used to represent
			// the list of partial accounts minus the one
			// we are removing
			List newPartialAccountList = new ArrayList();

			for(int i = 0; i<partialAccountList.size(); i++)
			{
				long currPartialAccountId =
					Long.parseLong((String)partialAccountList.get(i));
				if (currPartialAccountId != sumInfo)
				{
					// only re-add if it is not the one to remove
					newPartialAccountList.add(Long.
						toString(currPartialAccountId));
				}
			}

			System.out.println("Going to set the partial accounts after deletion to: " +
					newPartialAccountList + "\n");

			// at this point, newPartialAccountList
			// is the correct list of partial accounts
			// just need to save it as a Yodlee user preference

			NVPair nvPair = new NVPair();
			nvPair.setName("addaccount.incomplete");
			com.yodlee.soap.collections.List list = new com.yodlee.soap.collections.List();
			list.setElements(newPartialAccountList.toArray());
			nvPair.setValues(list);
			
			if (newPartialAccountList.size() == 1)
				nvPair.setType(new Integer(UserPreferences.SINGLE_VALUE));
			else
				nvPair.setType(new Integer(UserPreferences.MULTI_VALUE));

			try {
				upm.setPreference(userContext,nvPair);
			} catch (Exception e) {
				throw new RuntimeException("Error deleting partial account..." + e.getMessage());
			}
		} else {
			System.out.println("User did not have any partial accounts to delete");
		}

    }


    /**
     * Displays the content services available and allows the user to
     * add an item (and thereby an account) to the content service
     * selected.
     */
    public static void main (String args[])
    {
        if (args.length < 2) {
            throw new RuntimeException ("Usage: <username> <password>") ;
        }

        String  userName   =   args[0] ;
        String  password   =   args[1] ;


		LoginUser loginUser = new LoginUser();
        UserContext userContext =
                loginUser.loginUser(userName, password);

        PartialAccounts partialAccounts = new PartialAccounts(userContext);
		partialAccounts.displayAllMemPrefs();

        while(true){
			partialAccounts.partialAccountsMenu();
        }

    }
}
