/*
 * Copyright 2008 Yodlee, Inc.  All Rights Reserved.  Your use of this code
 * requires a license from Yodlee.  Any such license to this code is
 * restricted to evaluation/illustrative purposes only. It is not intended
 * for use in a production environment, and Yodlee disclaims all warranties
 * and/or support obligations concerning this code, regardless of the terms
 * of any other agreements between Yodlee and you."
 */
package YodleeSrc;

import java.util.Date;
import java.util.List;

import javax.xml.rpc.ServiceException;

import com.yodlee.soap.common.CobrandContext;
import com.yodlee.soap.common.Form;
import com.yodlee.soap.common.ItemSummary;
import com.yodlee.soap.common.UserContext;
import com.yodlee.soap.core.accountmanagement.itemmanagement.ItemManagement;
import com.yodlee.soap.core.accountmanagement.itemmanagement.ItemManagementServiceLocator;
import com.yodlee.soap.core.dataservice.DataExtent;
import com.yodlee.soap.core.dataservice.ItemData;
import com.yodlee.soap.core.dataservice.dataservice.DataService;
import com.yodlee.soap.core.dataservice.dataservice.DataServiceServiceLocator;
import com.yodlee.soap.core.dataservice.types.ItemAccountData;
import com.yodlee.soap.core.dataservice.types.Loan;
import com.yodlee.soap.core.dataservice.types.LoanLoginAccountData;

public class ManageItems implements Menu {
    private String[] menuItems =
        new String[] {
            "Add Item", 
            "View All Items", 
            "View Single Item",
            "View Transactions",
            "View User Categories",
            "Add User Category",
            "Dump Item (Big!)",
            "Edit Item",
            "Remove Item"
        };
    
    protected ItemManagement itemManagement;
    
    protected DataService dataService;    
    
    Transactions transactions;
    
    public ManageItems() {
    	ItemManagementServiceLocator locator = new ItemManagementServiceLocator();
        String serviceName = locator.getItemManagementServiceWSDDServiceName();
        locator.setItemManagementServiceEndpointAddress(System.getProperty("com.yodlee.soap.services.url")
						+ "/" + serviceName);
        try {
        	itemManagement = locator.getItemManagementService();
		} catch (ServiceException se) {
			throw new RuntimeException(se);
		}
		
		DataServiceServiceLocator locator1 = new DataServiceServiceLocator();
        String serviceName1 = locator1.getDataServiceWSDDServiceName();
        locator1.setDataServiceEndpointAddress(System.getProperty("com.yodlee.soap.services.url")
						+ "/" + serviceName1);
        try {
        	dataService = locator1.getDataService();
		} catch (ServiceException se) {
			throw new RuntimeException(se);
		}		
		transactions = new Transactions();
    }
    /**
     * Executes the Menu.
     * @param cobrandContext context of the cobrand
     * @param userContext context of the user
     * @return context of the user
     */
    public UserContext doMenuItem(
        int menuItem, CobrandContext cobrandContext, UserContext userContext) {
        switch (menuItem) {
        	case 1: addItem(userContext, cobrandContext); break;
        	case 2: viewItems(userContext,true); break;
        	case 3: viewItem(userContext); break;
        	case 4: viewTransactions(userContext); break;
        	case 5: viewUserCategories(userContext); break;
        	case 6: addUserCategory(userContext); break;
        	case 7: dumpItem(userContext); break;
        	case 8: editItem(userContext); break;
        	case 9: removeItem(userContext); break;
        }

        return userContext;
    }

	public String[] getMenuItems() {
		return menuItems;
	}

	public String getMenuName() {
		return "Item Management Menu";
	}
	
	/**
	 * Adds a Single Item for User
	 * 
	 * @param userContext the user context
	 */
	public void addItem(UserContext userContext, CobrandContext cobrandContext) {		
        
        // Query Back the Content Service For the User
        System.out.print("Enter Content ServiceId: ");
        long contentServiceId = IOUtils.readInt();
        
        // Read in all the required input fields for the form.
        List fieldInfoList = FormUtil.getUserInputFieldInfoList(userContext,
        		getLoginFormForContentService(userContext, contentServiceId));

        System.out.println("\nAdding item");
        Long itemId = null;
        try {
            itemId = itemManagement.addItemForContentService1 (userContext, new Long(contentServiceId), new com.yodlee.soap.collections.List (fieldInfoList.toArray()), false, false);
            System.out.println("Successfully created itemId: " + itemId);
            //try {
            //FormUtil.writeFormHtml(userContext, itemId.longValue(),
             //   		getLoginFormCredentialsForItem(userContext, itemId.longValue()),
              //          false,
              //          "FilledForm");
                        
//            } catch (UnsupportedCoreOperationExceptionFault e) {
//            	System.out.println("View credentials is disabled.");
//            }
            
            RefreshItem refreshItem = new RefreshItem();
            String mfatype = refreshItem.getMFAType(cobrandContext, new Long(contentServiceId));
            if ( mfatype != null) {            	
            	refreshItem.refreshItem(userContext, itemId, true);
            } else {
            	refreshItem.refreshItem(userContext, itemId, false);
            }
            // Poll for the refresh status and display item
            // summary if refresh succeeds.
            if (refreshItem.pollRefreshStatus(userContext, itemId)) {
                AccountSummary as = new AccountSummary();
                as.displayItemSummary(userContext, itemId.longValue());
            }
            
        } catch (Exception coreEx) {
            coreEx.printStackTrace ();
            //throw new RuntimeException ("Unable to add item for content service!");
        }
     }
	
	/**
	 * Displays a simple list of all items that were added by the user.
	 * 
	 * @param userContext context of the user
	 * @param includeItemAccounts indicates if Item Accounts should be included in the display
	 */
    public void viewItems(UserContext userContext, boolean includeItemAccounts)
    {    	
		com.yodlee.soap.collections.List itemSummaries = null;
		try {
			itemSummaries = dataService.getItemSummaries(userContext); 
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException();
		}
		
        if(itemSummaries == null || itemSummaries.getElements().length == 0 ){
            System.out.println("You have no Items Added.");
        } else {
	        for (int i = 0; i < itemSummaries.getElements().length; i++){
	            ItemSummary is = (ItemSummary)itemSummaries.getElements(i);
	            String displayName = is.getContentServiceInfo().getContentServiceDisplayName();
	            System.out.println("ItemId: " 
	            		+ is.getItemId()  
	            		+ " DisplayName: " 
	            		+ displayName 
	            		+ " errorCode: " 
	            		+ is.getRefreshInfo().getStatusCode() 
	            		+ " refreshInfo time: " 
	            		+ new Date(is.getRefreshInfo().getLastUpdatedTime().longValue() * 1000));
	            
	            if(includeItemAccounts) {
		            ItemData id = is.getItemData();
			            if(id != null){           		
			            	com.yodlee.soap.collections.List itemAccounts = id.getAccounts();	            		 
		                     for (int accts = 0; accts < itemAccounts.getElements().length; accts++) {
		                    	 if (is.getContentServiceInfo() != null
									&& is.getContentServiceInfo()
											.getContainerInfo() != null
									&& is.getContentServiceInfo()
											.getContainerInfo()
											.getContainerName().equals(
													ContainerTypesHelper.LOAN)) {
								LoanLoginAccountData loanLoginAccountData = (LoanLoginAccountData) itemAccounts.getElements(accts);
								if (loanLoginAccountData != null) {
									com.yodlee.soap.collections.List loans = loanLoginAccountData
											.getLoans();
									if (loans != null && loans.getElements().length > 0) {
										for (int l = 0; l < loans.getElements().length; l++) {
											Loan loan = (Loan) loans.getElements(l);
											if (loan.getItemAccountId() != null) {
												System.out
														.println("\tItemAccountId: "
																+ loan
																		.getItemAccountId());
											}
										}
									}
								}
							} else {
								try {
									ItemAccountData iad = (ItemAccountData) itemAccounts.getElements(accts);
									System.out.println("\tItemAccountId: "
											+ iad.getItemAccountId());
								} catch (Exception e) {}
							}
		                     }	            		 
			            }
		            }
	        }
        }
    }	
    
    /**
     * Views transactions
     * 
     * @param userContext context for the user
     */
    public void viewTransactions(UserContext userContext) {
        System.out.print("Enter Item Account ID: ");

        long itemAccountId = IOUtils.readLong();
        transactions.viewTransactionsForItemAccount(userContext, itemAccountId);
    }
    
    /**
     * Adds a category for a user
     * 
     * @param userContext context for the user
     */
    public void addUserCategory(UserContext userContext) {
    	transactions.addUserCategory(userContext);
    	
    }
    
    /**
     * Views all the transaction categories defined for a user.
     * 
     * @param userContext context for the user
     */
    public void viewUserCategories(UserContext userContext) {
    	transactions.viewUserTransactionCategories(userContext);
    }
    
    /**
     * View a single item.
     */
    public void viewItem(UserContext userContext) {
        System.out.print("Enter Item ID: ");

        long itemID = IOUtils.readLong();
        AccountSummary as = new AccountSummary();
        as.displayItemSummary(userContext, itemID);
    }
    
    /**
     * Edit item.
     * This lists the items the user has and prompts to edit one.
     */
    public void editItem(UserContext userContext) {
        viewItems(userContext, false);
        System.out.print("Enter ItemId: ");
        long itemId = IOUtils.readInt();
        
        // Prompt user to re-enter credentials
        List fieldInfoList = FormUtil.getUserInputFieldInfoList(userContext,
        		getLoginFormCredentialsForItem(userContext, itemId));

        // Edit item with new credentials
        System.out.println("\nUpdating item");
        try {
            itemManagement.updateCredentialsForItem1(userContext, new Long(itemId), new com.yodlee.soap.collections.List(fieldInfoList.toArray()), true);
            System.out.println("Updated itemId with new credentials: " + itemId);
            RefreshItem refreshItem = new RefreshItem();
            if (refreshItem.pollRefreshStatus(userContext, new Long(itemId))) {
                AccountSummary accountSummary = new AccountSummary();
                accountSummary.displayItemSummary(userContext, itemId);                
            }
        } catch (Exception coreEx) {
            coreEx.printStackTrace ();
            throw new RuntimeException ("Unable to add item for content service!");
        }
    }
    
    /**
     * Do a full dump of an item id.
     */
    public void dumpItem(UserContext userContext) {
    	viewItems(userContext, false);
        System.out.print("Enter Item ID: ");

        long itemID = IOUtils.readLong();
        DataExtent dataExtent = new DataExtent();
    	dataExtent.setStartLevel(new Integer(0));
    	dataExtent.setEndLevel(new Integer(Integer.MAX_VALUE));
        ItemSummary is = null;
        try {
            is = dataService.getItemSummaryForItem1(userContext, new Long(itemID),
            		dataExtent);
        } catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}

        try {
            DataDumper.dumper(is);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Allows removal of an item.
     * This prints a list of items the user has and prompts them to
     * remove one of the items.
     */
    public void removeItem(UserContext userContext) {
    	viewItems(userContext, false);

        // Prompt for Item to Remove
        System.out.print("Enter itemId to be removed: ");

        int itemId = IOUtils.readInt();

        // Remove Item
        try {
            itemManagement.removeItem (userContext, new Long(itemId));
            System.out.println("Successfully removed itemId: " + itemId);
        } catch (Exception coreEx) {
            System.out.println("Unable to remove item" + coreEx.getMessage());
        }            
    }
    
    /**
     * Get Login Form For Conent Service
     * @param userContext
     * @param csId
     * @return Form
     */
    public Form getLoginFormForContentService(UserContext userContext, long csId) {
        try {
        	return itemManagement.getLoginFormForContentService(userContext, new Long(csId));
        } catch (Exception e) {
        	throw new RuntimeException("Error fetching login form for this CsId");
        }
    }
    
    /**
     * Get Login Form for Item Id
     * @param userContext
     * @param itemId
     * @return Form
     */
    public Form getLoginFormCredentialsForItem(UserContext userContext, long itemId) {
    	try {
    		return itemManagement.getLoginFormCredentialsForItem(userContext, new Long(itemId));
    	} catch (Exception e) {
        	throw new RuntimeException("Error fetching login form credentials for this ItemId");
        }
    }      
   		    
}
