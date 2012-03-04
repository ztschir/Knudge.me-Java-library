/*
 * Copyright 2007 Yodlee, Inc.  All Rights Reserved.  Your use of this code 
 * requires a license from Yodlee.  Any such license to this code is 
 * restricted to evaluation/illustrative purposes only. It is not intended 
 * for use in a production environment, and Yodlee disclaims all warranties 
 * and/or support obligations concerning this code, regardless of the terms 
 * of any other agreements between Yodlee and you.
 */
package YodleeSrc;

import java.util.Date;

import javax.xml.rpc.ServiceException;

import com.yodlee.soap.collections.ArrayOflong;
import com.yodlee.soap.collections.common.ArrayOfRefreshInfo;
import com.yodlee.soap.common.CobrandContext;
import com.yodlee.soap.common.ContentServiceInfo;
import com.yodlee.soap.common.ItemSummary;
import com.yodlee.soap.common.RefreshInfo;
import com.yodlee.soap.common.UserContext;
import com.yodlee.soap.core.InvalidItemExceptionFault;
import com.yodlee.soap.core.dataservice.dataservice.DataService;
import com.yodlee.soap.core.dataservice.dataservice.DataServiceServiceLocator;
import com.yodlee.soap.core.mfarefresh.MFARefreshInfo;
import com.yodlee.soap.core.refresh.RefreshMode;
import com.yodlee.soap.core.refresh.RefreshParameters;
import com.yodlee.soap.core.refresh.RefreshStatus;
import com.yodlee.soap.core.refresh.refresh.Refresh;
import com.yodlee.soap.core.refresh.refresh.RefreshServiceLocator;
import com.yodlee.soap.ext.traversal.contentservicetraversal.ContentServiceTraversal;
import com.yodlee.soap.ext.traversal.contentservicetraversal.ContentServiceTraversalServiceLocator;


/**
 * Refreshes the specified item in the Yodlee software platform.
 */
public class RefreshItem implements Menu {
    protected Refresh refresh;
    protected DataService dataService;
    protected ContentServiceTraversal cst;
    protected static int SLEEP_MILLIS = 10 * 1000;
    public static long REFRESH_TIMEOUT_MIILIS = 5 * 60 * 1000; //5 minutes
    protected static int MFA_QUEUE_WAIT_TIME_MILLIS = 20* 1000;
    
    /**
     * Success
     */
    public static final int GATHERER_ERRORS_STATUS_OK = 0;
    
    private String[] menuItems =
        new String[] {
            "Refresh Item", 
            "Refresh All Items (not available)"
        };
    
	public UserContext doMenuItem(
			int menuItem,
			CobrandContext cobrandContext,
			UserContext userContext) {
		
		switch(menuItem) {
		  case 1: refreshItem(userContext, cobrandContext); break;
		  case 2: break;
        }
		
		return userContext;
	}

	public String[] getMenuItems() {
		return menuItems;
	}

	public String getMenuName() {
		return "Refresh Menu";
	}
    
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
		ContentServiceTraversalServiceLocator locator3 = new ContentServiceTraversalServiceLocator();
		String serviceName3 = locator3.getContentServiceTraversalServiceWSDDServiceName();
		locator3.setContentServiceTraversalServiceEndpointAddress(System.getProperty("com.yodlee.soap.services.url")
						+ "/" + serviceName3);
		try {
			cst = locator3.getContentServiceTraversalService();

		} catch (ServiceException se) {
			throw new RuntimeException(se);
		}
    }

    public
    void refreshItem (UserContext userContext, CobrandContext cobrandContext)
    {
        // Query Back the Content Service For the User
        System.out.print("Enter Item id: ");
        long itemId = IOUtils.readInt();
        String mfaType = getMFATypeForItemId(userContext, cobrandContext, itemId);
        if ( mfaType != null) {            	
        	refreshItem(userContext, new Long(itemId), true);
        } else {
        	refreshItem(userContext, new Long(itemId), false);
        }
        // Poll for the refresh status and display item
        // summary if refresh succeeds.
        if (pollRefreshStatus(userContext, new Long(itemId))) {
            AccountSummary as = new AccountSummary();
            as.displayItemSummary(userContext, itemId);
        }
    }
    /**
     * Refreshes the specified item.
     * <p>
     * @param userContext The user context.
     * @param itemId      The identifier of the item that needs to be refreshed.
     */
    public
        void refreshItem (UserContext userContext, Long itemId, boolean isMfa)
        {
            try {
                System.out.println("Attempting to start refresh...");
                RefreshParameters refreshParameters = new RefreshParameters();
                RefreshMode refreshMode;
                if  (isMfa)
                      refreshMode = RefreshMode.MFA_REFRESH_MODE;
                else
                      refreshMode = RefreshMode.NORMAL_REFRESH_MODE;              
                refreshParameters.setRefreshMode(refreshMode);
                refreshParameters.setRefreshPriority(RefreshHelper.REFRESH_PRIORITY_HIGH);
                refreshParameters.setForceRefresh(true);

                RefreshStatus status = refresh.startRefresh7 (userContext, itemId, refreshParameters);
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
                
                if(isMfa && (status == RefreshStatus.SUCCESS_START_REFRESH)) {
                    // Checking the MFA Reponse for the Item
                    MFARefreshInfo mfaInfo = refresh.getMFAResponse(userContext, itemId);
                    int errorCode = MFA.processMFA(userContext, mfaInfo, itemId);
                    
                    if ( errorCode == 0) {
                        System.out.println("MFA site added successfully");
                    } else if ( errorCode > 0 ){
                        System.out.println("There was an error while adding MFA site. Error code is " + errorCode );
                    } else if ( errorCode < 0 ) {
                        System.out.println("There was an exception while adding MFA site. Error code is " + errorCode );
                    }
                }
            } catch (InvalidItemExceptionFault ex) {
                throw  new RuntimeException ("The given item is invalid.");
            } catch (Exception e) {
            	throw  new RuntimeException(e.getMessage());
			}
            
            //pollRefreshStatus (userContext, itemId);
            
        }

    /**
     * This method is to find the MFAType of a content service given 
     * the contentServiceId and cobrandContext
     *       
     * @param cobrandContext the cobrand to check MFA type for
     * @param contentServiceId the content service to check the MFA type for
	 * @return the MFA type as string
	*/
	   public String getMFAType(CobrandContext cobrandContext, Long contentServiceId) {		   
		   ContentServiceInfo csInfo = null;
		   try {
			   csInfo = cst.getContentServiceInfo(cobrandContext, contentServiceId);			   
		   } catch (Exception e) {
			   return null;		
		   }
		   return (csInfo.getMfaType() != null ? csInfo.getMfaType().getValue() : null);
	   }
	  
   /**
     * This method is to find the MFAType of a content service given 
     * the itemId and Usercontext
     *       
     * @param userContext the user to check MFA type for
     * @param itemId the item to check the MFA type for
	 * @return the MFA type as string
	*/   
    private String getMFATypeForItemId(UserContext userContext, CobrandContext cobrandContext, long itemId) {
    	String mfatype = null;
    	try {
    		ItemSummary itemSummary = dataService.getItemSummaryForItem (userContext, new Long(itemId));
    		Long contentserviceId = itemSummary.getContentServiceId();
    		mfatype = getMFAType(cobrandContext, contentserviceId);
	    } catch (Exception e) {
	    	throw  new RuntimeException ("Error fetching the Item [" + itemId + "]");
		}
	    return mfatype;
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
    public boolean pollRefreshStatus (UserContext userContext, Long itemId)
    {
        long startTime = (new Date()).getTime();
        long currTime = startTime;
        ArrayOflong itemIds = new ArrayOflong(new Long[] {itemId});
        
        while (currTime - startTime < REFRESH_TIMEOUT_MIILIS) {
            System.out.println ("\tChecking the refresh status .... ");
            try {
            	boolean isItemRefreshing = refresh.isItemRefreshing(userContext, itemId);
                ArrayOfRefreshInfo refreshInfo = refresh.getRefreshInfo1(userContext, itemIds);
                if (!isItemRefreshing) {
                    int refreshStatusCode = refreshInfo.getElements(0).getStatusCode().intValue();
                    if (refreshStatusCode == GATHERER_ERRORS_STATUS_OK) {
                        Long lastRefreshedTime = refreshInfo.getElements(0).getLastUpdatedTime();
                        System.out.println ("Last refreshed time = " + lastRefreshedTime);
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
				e.printStackTrace();
				throw new RuntimeException();
			}
        }

        //Timeout the refresh request
        try {
        	refresh.stopRefresh(userContext, itemId, RefreshHelper.STOP_REFRESH_REASON_TIMEDOUT);
        } catch (Exception e) {}
        System.out.println ("\tThe refresh has timed out.");
        return false;
    }

    public RefreshInfo getRefreshInfo(UserContext userContext, long itemId)
    {
    	ArrayOflong itemIds = new ArrayOflong(new Long[] {new Long(itemId)});    	
        //RefreshInfo[] ris = refresh.getRefreshInfo(items);
        ArrayOfRefreshInfo ris = null; 
        try {
        	ris = refresh.getRefreshInfo1(userContext, itemIds);
        } catch (Exception e) {
        	throw  new RuntimeException ("Error fetching the Refresh Information");
		}
        for(int i=0; i<ris.getElements().length; i++){
            RefreshInfo ri = ris.getElements(i);
            Long timeUpdated = ri.getLastUpdatedTime();
            System.out.println("timeUpadted = " + timeUpdated);
            if(ri.getItemId().longValue() == itemId){
                return ri;
            }
        }
        return null;
    }
}
