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
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import javax.xml.rpc.ServiceException;

import com.yodlee.sampleapps.helper.ContainerTypesHelper;
import com.yodlee.sampleapps.helper.RefreshHelper;
import com.yodlee.soap.collections.List;
import com.yodlee.soap.collections.common.ArrayOfRefreshInfo;
import com.yodlee.soap.common.ItemSummary;
import com.yodlee.soap.common.RefreshInfo;
import com.yodlee.soap.common.UserContext;
import com.yodlee.soap.core.CoreExceptionFault;
import com.yodlee.soap.core.IllegalArgumentTypeExceptionFault;
import com.yodlee.soap.core.IllegalArgumentValueExceptionFault;
import com.yodlee.soap.core.InvalidConversationCredentialsExceptionFault;
import com.yodlee.soap.core.InvalidUserContextExceptionFault;
import com.yodlee.soap.core.StaleConversationCredentialsExceptionFault;
import com.yodlee.soap.core.dataservice.IllegalDataExtentExceptionFault;
import com.yodlee.soap.core.dataservice.dataservice.DataService;
import com.yodlee.soap.core.dataservice.dataservice.DataServiceServiceLocator;
import com.yodlee.soap.core.mfarefresh.MFARefreshInfo;
import com.yodlee.soap.core.refresh.RefreshStatus;
import com.yodlee.soap.core.refresh.refresh.Refresh;
import com.yodlee.soap.core.refresh.refresh.RefreshServiceLocator;

/**
 * Refreshes all the items in a users account
 */
public class RefreshAll extends ApplicationSuper {

	protected Refresh refresh;
    protected DataService dataService;
    protected static int SLEEP_MILLIS = 10 * 1000;
    public static long REFRESH_TIMEOUT_MIILIS = 5 * 60 * 1000; //5 minutes

    /**
     * Success
     */
    private static final int GATHERER_ERRORS_STATUS_OK = 0;
    
    /**
     * Constructor
     */
    public RefreshAll ()
    {
        super ();
        RefreshServiceLocator locator1 = new RefreshServiceLocator();
        String serviceName1 = locator1.getRefreshServiceWSDDServiceName();
        locator1.setRefreshServiceEndpointAddress(System.getProperty("com.yodlee.soap.services.url")
						+ "/" + serviceName1);
        try {
        	refresh = locator1.getRefreshService();
		} catch (ServiceException se) {
			throw new RuntimeException(se);
		}
		DataServiceServiceLocator locator2 = new DataServiceServiceLocator();
        String serviceName2 = locator2.getDataServiceWSDDServiceName();
        locator2.setDataServiceEndpointAddress(System.getProperty("com.yodlee.soap.services.url")
						+ "/" + serviceName2);
        try {
        	dataService = locator2.getDataService();
		} catch (ServiceException se) {
			throw new RuntimeException(se);
		}
    }
    
    /**
     * Refreshes all the items.
     * <p>
     * @param userContext The user context.
     */
    public void refreshAll (UserContext userContext)
    {
        try {
            System.out.println("Attempting to start refresh...");
            com.yodlee.soap.collections.Map statusMap = refresh.startRefresh2 (userContext, RefreshHelper.REFRESH_PRIORITY_HIGH);
            System.out.println("Refresh Started");

            // Iterate over the itemIDs in the status map
            if (statusMap != null){
			for (int e = 0; e < statusMap.getTable().length; e++) {
				com.yodlee.soap.collections.Entry entry = statusMap.getTable(e);
				Long itemId = (Long)entry.getKey();
				RefreshItem ri = new RefreshItem();
                String mfaType = ri.getMFAType(userContext, itemId.longValue());
                if (  mfaType != null ) {
                	//Refresh MFA sites
                	//Get the MFA response from the agent, which contains the MFA questions
        			//The questions will be placed in the MQ and the app or SDK calls can poll for these questions continuously
        			MFARefreshInfo mfaInfo = refresh.getMFAResponse(userContext, itemId.longValue());
        			MFA mfa = new MFA();
        			int errorCode = mfa.processMFA(userContext, mfaInfo, itemId.longValue());
        			if ( errorCode == 0) {
        				System.out.println("MFA site verified successfully");
        			} else {
        				System.out.println("There was an error during refreshing the MFA site");
        			}
                } 
                RefreshStatus status = (RefreshStatus)entry.getValue();
                if (status == RefreshStatus.SUCCESS_START_REFRESH) {
                    System.out.println("\tStarted refresh for " + itemId  + ".");
                } else if (status == RefreshStatus.REFRESH_ALREADY_IN_PROGRESS) {
                    System.out.println("\tThe refresh is already in progress for" + itemId +".");
                } else if (status == RefreshStatus.ALREADY_REFRESHED_RECENTLY) {
                    System.out.println("\tThis item (" + itemId + ") has been refreshed very "+
                            "recently.  Please try again later.");
                } else if (status == RefreshStatus.ITEM_CANNOT_BE_REFRESHED) {
                    System.out.println("\tThe refresh on this item (" +itemId +") is not "+
                            "permitted.");
                } else {
                    System.out.println("\tUnable to refresh the item ("+itemId+") " +
                            "RefreshStatus: " + status);
                }
            }            
         } 
        }catch (Exception ex) {
            System.out.println("Excpetion in starting refresh.\n" + ex.toString() );
        }
    }

    /**
     * Polls the refresh status of the specified item. This method continues
     * polling till the refresh either completes or times out. Returns true
     * if the refresh was successfully completed and false otherwise.
     * <p>
     * @param userContext The user context.
     */
    public boolean pollRefreshStatus (UserContext userContext )
    {
        long startTime = (new Date()).getTime();
        long currTime = startTime;
        HashMap hm = new HashMap();

        while (startTime - currTime < REFRESH_TIMEOUT_MIILIS) {
            System.out.print ("\n\tChecking the refresh status of items: ");
            try {
                ArrayOfRefreshInfo arrayrefreshInfo = refresh.getRefreshInfo(userContext );
                RefreshInfo [] refreshInfo = arrayrefreshInfo.getElements();
                // initilize
                for(int i=0; i<refreshInfo.length; i++ ){
                    hm.put(new Long(refreshInfo[i].getItemId()), new Boolean(false));
                    System.out.print(refreshInfo[i].getItemId() + " ");
                }
                System.out.println("");

                for(int i=0; i<refreshInfo.length; i++ ){
                    if (refreshInfo[i].getRefreshRequestTime() == 0) {
                        long itemId = refreshInfo[i].getItemId();
                        int refreshStatusCode = refreshInfo[i].getStatusCode();
                        if (refreshStatusCode == GATHERER_ERRORS_STATUS_OK) {
                            hm.put(new Long(refreshInfo[i].getItemId()), new Boolean(true));
                            System.out.println("\tThe refresh of "+itemId+" has completed successfully.");
                            if(doneRefreshing(hm)){
                                return true;
                            }
                        } else {
                            hm.put(new Long(refreshInfo[i].getItemId()), new Boolean(true));
                            System.out.println("\tThe refresh of "+itemId+" did not succeed.  Error code: " + refreshStatusCode);
                            if(doneRefreshing(hm)){
                                return true;
                            }
                        }
                    }
                }
                Thread.sleep(SLEEP_MILLIS);
            } catch (InterruptedException ex) {
                System.out.println("Refresh polling has been interrupted!");
            } catch (Exception e) {
            	throw new RuntimeException ("Refresh polling has been interrupted. " + e.getMessage());
			}
        }

        //Timeout the refresh request
        try {
        	refresh.stopRefresh2(userContext, RefreshHelper.STOP_REFRESH_REASON_TIMEDOUT);
        } catch (Exception e) {}
        System.out.println ("\tThe refresh has timed out.");
        return true;
    }
    /**
     * Check to see if all the items stored in the HashMap
     * have been refresh.
     * <p/>
     * @param map
     * @return true/false
     */
    public static boolean doneRefreshing(HashMap map){
           // Iterate over the values in the map
           Iterator it = map.values().iterator();
           while (it.hasNext()) {
               // Get value
               Boolean value = (Boolean)it.next();
               if(!value.booleanValue()){
                   return false;
               }
           }
        return true;
    }

    /**
     * Displays the item summary information.
     * <p>
     * @param userContext a user context object.
     */
    public void displayItemSummary (UserContext userContext )
    {

        Object[] itemSummaries = null;
        List itemSummariesList = null;
        try {
        	itemSummariesList = dataService.getItemSummaries(userContext);
        	if (itemSummariesList != null){
        	itemSummaries = itemSummariesList.getElements();
        	}
        } catch (StaleConversationCredentialsExceptionFault e) {
			e.printStackTrace();
		} catch (InvalidConversationCredentialsExceptionFault e) {
			e.printStackTrace();
		} catch (CoreExceptionFault e) {
			e.printStackTrace();
		} catch (IllegalArgumentTypeExceptionFault e) {
			e.printStackTrace();
		} catch (IllegalArgumentValueExceptionFault e) {
			e.printStackTrace();
		} catch (InvalidUserContextExceptionFault e) {
			e.printStackTrace();
		} catch (IllegalDataExtentExceptionFault e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		}

       if (itemSummaries == null || itemSummaries.length == 0) {
            System.out.println ("No data available");
            return;
        }

        for (int i = 0; i < itemSummaries.length; i++) {
			ItemSummary itemSummary = (ItemSummary) itemSummaries[i];

            String containerType = itemSummary.getContentServiceInfo ().
                    getContainerInfo().getContainerName ();

            if (containerType.equals (ContainerTypesHelper.BANK)) {
                (new DisplayBankData ()).displayBankDataForItem (itemSummary);
            }else if(containerType.equals(ContainerTypesHelper.BILL)){
                (new DisplayBillsData ()).displayBillsDataForItem (itemSummary);
            }else if(containerType.equals(ContainerTypesHelper.CREDIT_CARD)){
                (new DisplayCardData ()).displayCardDataForItem (itemSummary);
            }else if(containerType.equals(ContainerTypesHelper.INSURANCE)){
                (new DisplayInsuranceData ()).displayInsuranceDataForItem (itemSummary);
            }else if(containerType.equals(ContainerTypesHelper.INVESTMENT)){
                (new DisplayInvestmentData ()).displayInvestmentDataForItem (itemSummary);
            }else if(containerType.equals(ContainerTypesHelper.LOAN)){
                (new DisplayLoanData ()).displayLoanDataForItem (itemSummary);
            } else {
                (new DisplayItemInfo ()).displayItemSummaryInfo (itemSummary);
            }
        }

    }

    /**
     * Logs the user in, refreshes the item specified by the itemId param,
     * polls the refresh status, and displays the item information.
     */
    public static void main (String[] args)
    {
        if (args.length < 2) {
            throw new RuntimeException
                    ("Usage: <username> <password>") ;
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

        String userName = args[0];
        String password = args[1];


        RefreshAll refreshAll = new RefreshAll ();

        //try {
            LoginUser loginUser = new LoginUser ();
            System.out.println ("Logging in " + userName);
            UserContext userContext = loginUser.loginUser (userName, password);
            System.out.println ("Done logging in " + userName);

            // Refresh the item
            refreshAll.refreshAll (userContext);

            // Poll for the refresh status and display item summary if refresh
            // succeeds.
            if (refreshAll.pollRefreshStatus (userContext)) {
                // Display Item Summary
                refreshAll.displayItemSummary (userContext);

                // Display Account Summary
                // AccountSummary acctSum = new AccountSummary();
                // acctSum.displayAccountSummary(userContext);
            }

            System.out.println ("Logging out " + userName);
            loginUser.logoutUser (userContext);
            System.out.println ("Done logging out " + userName);

        /*} catch (InvalidUserCredentialsExceptionFault ex) {
            System.out.println ("User name: " + userName +
                    " password supplied -- "  + password +
                    " are incorrect for the default cobrand.");
        } catch (InvalidCobrandContextExceptionFault ex) {
            System.out.println ("The cobrand context is invalid. " +
                    "Unable to proceed further.");
        } catch (UserUncertifiedExceptionFault ex) {
            System.out.println ("The user is uncertified. " +
                    "Unable to proceed further.");
        } catch (UserAccountLockedExceptionFault ex){
            System.out.println ("The user's account is locked. " +
                    "Unable to proceed further.");
        } catch (InvalidConversationCredentialsExceptionFault ex) {
            System.out.println ("The user's session has expired.");
        }*/

    }
}
