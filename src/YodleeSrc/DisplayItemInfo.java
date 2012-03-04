/*
 * Copyright 2009 Yodlee, Inc.  All Rights Reserved.  Your use of this code 
 * requires a license from Yodlee.  Any such license to this code is 
 * restricted to evaluation/illustrative purposes only. It is not intended 
 * for use in a production environment, and Yodlee disclaims all warranties 
 * and/or support obligations concerning this code, regardless of the terms 
 * of any other agreements between Yodlee and you."
 */
package YodleeSrc;

import java.rmi.RemoteException;
import java.util.Date;

import javax.xml.rpc.ServiceException;

import com.yodlee.soap.collections.List;
import com.yodlee.soap.common.ItemSummary;
import com.yodlee.soap.common.UserContext;
import com.yodlee.soap.core.CoreExceptionFault;
import com.yodlee.soap.core.IllegalArgumentValueExceptionFault;
import com.yodlee.soap.core.InvalidConversationCredentialsExceptionFault;
import com.yodlee.soap.core.InvalidUserContextExceptionFault;
import com.yodlee.soap.core.StaleConversationCredentialsExceptionFault;
import com.yodlee.soap.core.dataservice.DataExtent;
import com.yodlee.soap.core.dataservice.ItemData;
import com.yodlee.soap.core.dataservice.dataservice.DataService;
import com.yodlee.soap.core.dataservice.dataservice.DataServiceServiceLocator;
import com.yodlee.soap.core.dataservice.types.BaseTagData;
import com.yodlee.soap.core.dataservice.types.Loan;
import com.yodlee.soap.core.dataservice.types.LoanLoginAccountData;


public class DisplayItemInfo extends ApplicationSuper {

	protected
    DataService dataService;
	
	public
    DisplayItemInfo ()
    {
        super();

        DataServiceServiceLocator locator = new DataServiceServiceLocator();
        String serviceName = locator.getDataServiceWSDDServiceName();
        locator.setDataServiceEndpointAddress(System.getProperty("com.yodlee.soap.services.url")
						+ "/" + serviceName);
        try {
        	dataService = locator.getDataService();
		} catch (ServiceException se) {
			throw new RuntimeException(se);
		}
    }
	
	public void viewItems(UserContext userContext)
    {
        Object[] itemSummaries;
        List itemSummariesList;
		try {
			itemSummariesList = dataService.getItemSummaries(userContext);
			itemSummaries = itemSummariesList.getElements();
			if(itemSummaries == null || itemSummaries.length == 0 ){
	            System.out.println("You have no Items Added.");
	        } else {
		        for (int i = 0; i < itemSummaries.length; i++) {
		        	ItemSummary is = (ItemSummary)itemSummaries[i];
		            String displayName = is.getContentServiceInfo().getContentServiceDisplayName();
		            System.out.println("ItemId: " + is.getItemId()  + " DisplayName: " +displayName + " errorCode: " + is.getRefreshInfo().getStatusCode() +
					       " refreshInfo time: " + new Date(is.getRefreshInfo().getLastUpdatedTime() * 1000));
		            ItemData id = is.getItemData();
		            if(id != null){           		
		            		 Object[] itemAccounts = id.getAccounts().getElements();
		                     for (int j = 0; j < itemAccounts.length; j++) {
		                    	 if (is.getContentServiceInfo() != null
									&& is.getContentServiceInfo()
											.getContainerInfo() != null
									&& is.getContentServiceInfo()
											.getContainerInfo()
											.getContainerName().equals(
													ContainerTypesHelper.LOAN)) {
		                    		 LoanLoginAccountData loanLoginAccountData = (LoanLoginAccountData)itemAccounts[j];
		                    		 if(loanLoginAccountData != null && loanLoginAccountData.getLoans() != null){
	                                        Object[] loans = loanLoginAccountData.getLoans().getElements();
	                                        if(loans != null && loans.length > 0 ) {
	                                        	for (int l = 0; l < loans.length; l++){
	                                                Loan loan = (Loan) loans[l];	                                                
	                                                if(loan.getItemAccountId() != null) {
	                                                	System.out.println("\tItemAccountId: " + loan.getItemAccountId() );
	                                                }
	                                            }
	                                        }
		                    		 }
		                    	 }	
								else {
			                    	 BaseTagData baseTagData =  (BaseTagData)itemAccounts[j];
			                         System.out.println("\tItemAccountId: " + baseTagData.getItemAccountId() );
		                    	 }
		                     }
		            }
		        }
	        }
		} catch (StaleConversationCredentialsExceptionFault e) {
			e.printStackTrace();
		} catch (InvalidConversationCredentialsExceptionFault e) {
			e.printStackTrace();
		} catch (CoreExceptionFault e) {
			e.printStackTrace();
		} catch (IllegalArgumentValueExceptionFault e) {
			e.printStackTrace();
		} catch (InvalidUserContextExceptionFault e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		}        
    }
	
	public Object[] fetchItemSummaries(UserContext userContext) {
		Object[] itemSummaries = null;	
		List itemSummariesList = null;
		try {
			itemSummariesList = dataService.getItemSummaries(userContext);
			if (itemSummariesList != null){
				itemSummaries = itemSummariesList.getElements();
			}
			if (itemSummaries == null || itemSummaries.length ==0)
				System.out.println("no Items found");
		} catch (StaleConversationCredentialsExceptionFault e) {
			e.printStackTrace();
		} catch (InvalidConversationCredentialsExceptionFault e) {
			e.printStackTrace();
		} catch (CoreExceptionFault e) {
			e.printStackTrace();
		} catch (IllegalArgumentValueExceptionFault e) {
			e.printStackTrace();
		} catch (InvalidUserContextExceptionFault e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return itemSummaries;
	}
	
	/**
     * Displays the item information for the given item summary.
     * <p>
     * @param itemSummary an item summary instance whose information
     *                    should be displayed.
     */
    public
    void displayItemSummaryInfo (ItemSummary itemSummary)
    {
        System.out.println ("\n"+itemSummary.getItemDisplayName () +
                " (" + itemSummary.getItemId() + ")");
        System.out.println ("\tItem Id: " +
                            itemSummary.getItemId ());
        System.out.println("\tSum Info Id: " + 
        		itemSummary.getContentServiceId ());
        System.out.println ("\tContainer type: " +
                            itemSummary.getContentServiceInfo ().
                            getContainerInfo ().getContainerName ());
        System.out.println ("\tContent Service Name: " +
            (itemSummary.getContentServiceInfo ()).getSiteDisplayName ());

        long lu = itemSummary.getRefreshInfo ().getLastUpdatedTime() * 1000;
        Date date = new Date(lu);
        System.out.println ("\tLast updated time : " + Formatter.formatDate(date,Formatter.DATE_LONG_FORMAT) );

        lu = itemSummary.getRefreshInfo ().getLastUpdateAttemptTime() * 1000;
        date = new Date(lu);
        System.out.println ("\tLast update attempt time : " + Formatter.formatDate(date, Formatter.DATE_LONG_FORMAT ) );

        System.out.println ("\tRefresh status code: " +
                            itemSummary.getRefreshInfo ().getStatusCode() );

        ItemData itemData = itemSummary.getItemData ();
        if (itemData != null) {
            Object[] itemAccounts = itemData.getAccounts().getElements();
            if (itemAccounts != null) {
            	System.out.println ("\tItem Account count: " + itemAccounts.length);
            	for (int i = 0; i < itemAccounts.length; i++) {
            		BaseTagData dataType = (BaseTagData) itemAccounts[i];
            		System.out.println ("\tData Type    : " + dataType.getClass ().getName ());
            	}
            }
        }
    }
    
    public void dumpItem(UserContext userContext) {
    	System.out.print("Enter Item ID: ");

        long itemID = IOUtils.readLong();
        DataExtent de = new DataExtent();
        de.setStartLevel(0);
        de.setEndLevel(Integer.MAX_VALUE);
        
        try {
        	ItemSummary is = dataService.getItemSummaryForItem1(userContext, new Long(itemID), de);        
            DataDumper.dumper(is);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
