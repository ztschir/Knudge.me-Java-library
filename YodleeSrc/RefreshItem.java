/*
 * Copyright 2009 Yodlee, Inc.  All Rights Reserved.  Your use of this code 
 * requires a license from Yodlee.  Any such license to this code is 
 * restricted to evaluation/illustrative purposes only. It is not intended 
 * for use in a production environment, and Yodlee disclaims all warranties 
 * and/or support obligations concerning this code, regardless of the terms 
 * of any other agreements between Yodlee and you.
 */
package com.yodlee.sampleapps;

import java.util.Date;

import javax.xml.rpc.ServiceException;

import com.yodlee.sampleapps.helper.ContainerTypesHelper;
import com.yodlee.sampleapps.helper.RefreshHelper;
import com.yodlee.soap.collections.ArrayOflong;
import com.yodlee.soap.collections.common.ArrayOfRefreshInfo;
import com.yodlee.soap.common.ItemSummary;
import com.yodlee.soap.common.RefreshInfo;
import com.yodlee.soap.common.UserContext;
import com.yodlee.soap.core.InvalidCobrandContextExceptionFault;
import com.yodlee.soap.core.InvalidConversationCredentialsExceptionFault;
import com.yodlee.soap.core.InvalidItemExceptionFault;
import com.yodlee.soap.core.dataservice.dataservice.DataService;
import com.yodlee.soap.core.dataservice.dataservice.DataServiceServiceLocator;
import com.yodlee.soap.core.login.InvalidUserCredentialsExceptionFault;
import com.yodlee.soap.core.login.UserAccountLockedExceptionFault;
import com.yodlee.soap.core.mfarefresh.MFARefreshInfo;
import com.yodlee.soap.core.refresh.RefreshStatus;
import com.yodlee.soap.core.refresh.refresh.Refresh;
import com.yodlee.soap.core.refresh.refresh.RefreshServiceLocator;
import com.yodlee.soap.core.usermanagement.UserUncertifiedExceptionFault;

/**
 * Refreshes the specified item in the Yodlee software platform.
 */
public class RefreshItem extends ApplicationSuper {

	protected Refresh refresh;
    protected DataService dataService;
    protected static int SLEEP_MILLIS = 10 * 1000;
    protected static int MFA_QUEUE_WAIT_TIME_MILLIS = 20* 1000;
    public static long REFRESH_TIMEOUT_MIILIS = 5 * 60 * 1000; //5 minutes
    
    /**
     * Success
     */
    public static final int GATHERER_ERRORS_STATUS_OK = 0;

    /**
     * Constructor
     */
    public RefreshItem ()
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
     * Refreshes the specified item.
     * <p>
     * @param userContext The user context.
     * @param itemId      The identifier of the item that needs to be refreshed.
     */
    public
        void refreshItem (UserContext userContext, long itemId, boolean isMFA)
        {
            try {
                System.out.println("Attempting to start refresh...");
                RefreshStatus status = refresh.startRefresh4(userContext, itemId, RefreshHelper.REFRESH_PRIORITY_HIGH);
                //refresh.startRefreshingItem (userContext, itemId);
                if (status == RefreshStatus.SUCCESS_START_REFRESH) {
                    System.out.println("\tStarted refresh.");
                } else if (status == RefreshStatus.REFRESH_ALREADY_IN_PROGRESS) {
                    System.out.println("\tThe refresh is already in progress.");
                } else if (status == RefreshStatus.ALREADY_REFRESHED_RECENTLY) {
                    throw new RuntimeException("This item has been refreshed very "+
                            "recently.  Please try again later.");
                } else if (status == RefreshStatus.ITEM_CANNOT_BE_REFRESHED) {
                    throw new RuntimeException ("The refresh on this item is not "+
                            "permitted.");
                } else {
                    throw new RuntimeException ("Unable to refresh the item "+
                            "RefreshStatus: " + status);
                }
                if ( isMFA && (status == RefreshStatus.SUCCESS_START_REFRESH)) {
	                //We might need delay here to give few seconds for the message to be posted since this is a console based. 
	                //In an application, it might not be required. 
	                Thread.sleep(MFA_QUEUE_WAIT_TIME_MILLIS);
	                MFARefreshInfo mfaInfo = refresh.getMFAResponse(userContext, itemId);
	                MFA mfa = new MFA();
	        		int errorCode = mfa.processMFA(userContext, mfaInfo, itemId);
	        		if ( errorCode == 0 ) {
	        			System.out.println("MFA Account has been added successfully");
	        		} else if ( errorCode > 0 ){
	        			System.out.println("Error while adding this account with an error code " + errorCode);
	        		} else {
	        			System.out.println("Error while adding this account");
	        		}
                }                
            } catch (InvalidItemExceptionFault ex) {
                throw  new RuntimeException ("The given item is invalid.");
            } catch (InterruptedException ie) {
            	throw new RuntimeException ("Refresh polling has been interrupted!");
            } catch (Exception e) {
            	throw new RuntimeException ("Error refreshing item-->" + e.getMessage());
			}
        }

    /**
     * Polls the refresh status of the specified item. This method continues
     * polling till the refresh either completes or times out. Returns true
     * if the refresh was successfully completed and false otherwise.
     * <p>
     * @param userContext The user context.
     * @param itemId      The identifier of the item whose refresh status should be
     *                    polled.
     */
    public boolean origPollRefreshStatus (UserContext userContext, long itemId)
    {
        long startTime = (new Date()).getTime();
        long currTime = startTime;
        ArrayOflong itemIds = new ArrayOflong();
        itemIds.setElements(new Long[] {itemId});
       

        while (currTime - startTime < REFRESH_TIMEOUT_MIILIS) {
            System.out.println ("\tChecking the refresh status ... ");
            try {
                ArrayOfRefreshInfo refreshInfo = refresh.getRefreshInfo1(userContext, itemIds);
                if (refreshInfo.getElements(0).getRefreshRequestTime() == 0) {
                    int refreshStatusCode = refreshInfo.getElements(0).getStatusCode();
                    if (refreshStatusCode == GATHERER_ERRORS_STATUS_OK) {
                        System.out.println("\tThe refresh has completed successfully.");
                        return true;
                    } else {
                        System.out.println("\tThe refresh did not succeed.  Error code: " + refreshStatusCode);
                        return false;
                    }
                }
                Thread.sleep(SLEEP_MILLIS);
                currTime = (new Date()).getTime();
            } catch (InterruptedException ex) {
                throw new RuntimeException ("Refresh polling has been interrupted!");
            } catch (Exception e) {
            	throw new RuntimeException ("Refresh polling has been interrupted. " + e.getMessage());
			}
        }

        //Timeout the refresh request
        try {
        	refresh.stopRefresh(userContext, itemId, RefreshHelper.STOP_REFRESH_REASON_TIMEDOUT);
        } catch (Exception e) {}
        System.out.println ("\tThe refresh has timed out.");
        return false;
    }


    /**
     * Polls the refresh status of the specified item. This method continues
     * polling till the refresh either completes or times out. Returns true
     * if the refresh was successfully completed and false otherwise.
     * 
     * Unlike origPollRefreshStatus(), this method uses the RefreshInfo.isItemRefreshing() to do more robust handing
     * <p>
     * @param userContext The user context.
     * @param itemId      The identifier of the item whose refresh status should be
     *                    polled.
     */
    public boolean pollRefreshStatus (UserContext userContext, long itemId)
    {
        long startTime = (new Date()).getTime();
        long currTime = startTime;
        ArrayOflong itemIds = new ArrayOflong();
        itemIds.setElements(new Long[] {itemId});
        

        while (currTime - startTime < REFRESH_TIMEOUT_MIILIS) {
            System.out.println ("\tChecking the refresh status .... ");
            try {
            	boolean isItemRefreshing = refresh.isItemRefreshing(userContext, itemId);
                ArrayOfRefreshInfo refreshInfo = refresh.getRefreshInfo1(userContext, itemIds);
                if (!isItemRefreshing) {
                    int refreshStatusCode = refreshInfo.getElements(0).getStatusCode();
                    if (refreshStatusCode == GATHERER_ERRORS_STATUS_OK) {
                        System.out.println("\tThe refresh has completed successfully.");
                        return true;
                    } else {
                        System.out.println("\tThe refresh did not succeed.  Error code: " + refreshStatusCode);
                        return false;
                    }
                }
                Thread.sleep(SLEEP_MILLIS);
                currTime = (new Date()).getTime();
            } catch (InterruptedException ex) {
                throw new RuntimeException ("Refresh polling has been interrupted!");
            } catch (Exception e) {
            	throw new RuntimeException ("Refresh polling has been interrupted. " + e.getMessage());
			}
        }

        //Timeout the refresh request
        try {
        	refresh.stopRefresh(userContext, itemId, RefreshHelper.STOP_REFRESH_REASON_TIMEDOUT);
        } catch (Exception e) {}
        System.out.println ("\tThe refresh has timed out.");
        return false;
    }



    /**
     * Displays the item summary information.
     * <p>
     * @param userContext a user context object.
     * @param itemId      identifier of the item that needs to be refreshed.
     */
    public void displayItemSummary (UserContext userContext, long itemId)
    {
        try {
            ItemSummary itemSummary =
                dataService.getItemSummaryForItem (userContext, itemId);
            String containerType = itemSummary.getContentServiceInfo ().
                getContainerInfo().getContainerName ();
            System.out.println ("\tItem information: ");

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
        } catch (InvalidItemExceptionFault ex) {
            throw  new RuntimeException ("The given item is invalid.");
        } catch (Exception ex) {
        	throw  new RuntimeException ("Error fetching accounts...." + ex.getMessage());
        }
    }
    
    /**
     * This method is to find the MFAType of the content service given 
     * the itemId and Usercontext      
     * @param userContext
     * @param itemId
     * @return
     */
    
    public String getMFAType(UserContext userContext, long itemId) {
    	String mfatype = null;
    	try {
	    	ItemSummary itemSummary = dataService.getItemSummaryForItem (userContext, itemId);
	        long contentserviceId = itemSummary.getContentServiceInfo().getContentServiceId();
	        ContentServiceHelper csh = new ContentServiceHelper();
	        mfatype = csh.getMfAType(contentserviceId);
    	} catch (Exception e) {
    		throw  new RuntimeException ("Error fetching the Item [" + itemId + "]");
		}
        return mfatype;
    }


    public RefreshInfo getRefreshInfo(UserContext userContext, long itemId)
    {
    	ArrayOflong itemIds = new ArrayOflong();
    	itemIds.setElements(new Long[] {itemId});
    	    	
        
    	ArrayOfRefreshInfo ris = null;
        try {
        	ris = refresh.getRefreshInfo1(userContext, itemIds);
        } catch (Exception e) {
        	throw  new RuntimeException ("Error fetching the Refresh Information");
		}
        if (ris != null) {
	        for(int i=0; i<ris.getElements().length; i++){
	            RefreshInfo ri = ris.getElements(i);
	            if(ri.getItemId() == itemId){
	                return ri;
	            }
	        }
        }
        return null;
    }

    /**
     * Logs the user in, refreshes the item specified by the itemId param,
     * polls the refresh status, and displays the item information.
     */
    public static void main (String[] args)
    {
        if (args.length < 3) {
            throw new RuntimeException
                ("Usage: <username> <password> <itemId>") ;
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
        long itemId     = Integer.parseInt (args[2]);

        RefreshItem refreshItem = new RefreshItem ();

        //try {
            LoginUser loginUser = new LoginUser ();
            System.out.println ("Logging in " + userName);
            UserContext userContext = loginUser.loginUser (userName, password);
            System.out.println ("Done logging in " + userName);
            
            String mfaType = refreshItem.getMFAType(userContext, itemId);
            if (  mfaType != null ) {
            	//Refresh MFA sites
                refreshItem.refreshItem(userContext, itemId,true);
            } else {
            	//Refresh non-MFA sites
            	refreshItem.refreshItem(userContext, itemId,false);
            }
            // Poll for the refresh status and display item summary if refresh
            // succeeds.
            if (refreshItem.pollRefreshStatus (userContext, itemId)) {
                refreshItem.displayItemSummary (userContext, itemId);
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
