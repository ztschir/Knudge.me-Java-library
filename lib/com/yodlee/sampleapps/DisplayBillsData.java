/*
 * Copyright 2009 Yodlee, Inc.  All Rights Reserved.  Your use of this code 
 * requires a license from Yodlee.  Any such license to this code is 
 * restricted to evaluation/illustrative purposes only. It is not intended 
 * for use in a production environment, and Yodlee disclaims all warranties 
 * and/or support obligations concerning this code, regardless of the terms 
 * of any other agreements between Yodlee and you."
 */
package com.yodlee.sampleapps;

import java.rmi.RemoteException;
import javax.xml.rpc.ServiceException;

import com.yodlee.sampleapps.helper.ContainerTypesHelper;
import com.yodlee.sampleapps.helper.DataDumper;
import com.yodlee.sampleapps.helper.Formatter;
import com.yodlee.soap.collections.List;
import com.yodlee.soap.common.ItemSummary;
import com.yodlee.soap.common.UserContext;
import com.yodlee.soap.core.CoreExceptionFault;
import com.yodlee.soap.core.IllegalArgumentTypeExceptionFault;
import com.yodlee.soap.core.IllegalArgumentValueExceptionFault;
import com.yodlee.soap.core.InvalidConversationCredentialsExceptionFault;
import com.yodlee.soap.core.InvalidUserContextExceptionFault;
import com.yodlee.soap.core.StaleConversationCredentialsExceptionFault;
import com.yodlee.soap.core.dataservice.IllegalDataExtentExceptionFault;
import com.yodlee.soap.core.dataservice.ItemData;
import com.yodlee.soap.core.dataservice.SummaryRequest;
import com.yodlee.soap.core.dataservice.dataservice.DataService;
import com.yodlee.soap.core.dataservice.dataservice.DataServiceServiceLocator;
import com.yodlee.soap.core.dataservice.types.AccountUsageData;
import com.yodlee.soap.core.dataservice.types.Bill;
import com.yodlee.soap.core.dataservice.types.BillsData;

/**
 * Displays a user's Bills item data in the Yodlee software platform.
 */
public class DisplayBillsData extends ApplicationSuper {

	protected DataService dataService;

    /**
     * Constructs an instance of the DisplayBillsData class that
     * displays Bills accounts
     */

    public DisplayBillsData ()
    {
        super ();
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
    
    /**
     * Displays all the item summaries of bills items of the user.
     * <p>
     * @param userContext The user context.
     */
    public void displayBillsData (UserContext userContext)
    {
        /*SummaryRequest sr = new SummaryRequest(
                new String[] {ContainerTypes.BILL, ContainerTypes.TELEPHONE},
                new DataExtent[] { DataExtent.getDataExtentForAllLevels(),DataExtent.getDataExtentForAllLevels() }
        );*/
        SummaryRequest sr = new SummaryRequest();
        List list = new List();
        list.setElements(new String[] {ContainerTypesHelper.BILL, ContainerTypesHelper.TELEPHONE});
        sr.setContainerCriteria(list);
    	
        Object[] itemSummaries = null;
        List itemSummariesList = null;
        try {
        	itemSummariesList = dataService.getItemSummaries1(userContext, sr);
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
            System.out.println ("No bills data available");
            return;
        }

        for (int i = 0; i < itemSummaries.length; i++) {
			ItemSummary is = (ItemSummary) itemSummaries[i];
			displayBillsDataForItem(is);

			// Dump the BillsData Object
			// dumpBillsDataForItem(is);
		}
    }


    /**
     * Dump the BillsData object.
     * @param is
     */
    public void dumpBillsDataForItem( ItemSummary is){
        ItemData id = is.getItemData();

        if(id == null){
           System.out.println("\tItemData == null");
        }else{
        	List accountsList = id.getAccounts();
            Object[] accounts = null;
            if (accountsList != null){
            	accounts = accountsList.getElements();
            }
            if (accounts == null || accounts.length == 0) {
                System.out.println ("\tNo accounts");
            }else {
                for (int accts = 0; accts < accounts.length; accts++) {
					BillsData billsData = (BillsData) accounts[accts];
					System.out.println("\n\nDumping BillsData Object");
					try {
						DataDumper.dumper(billsData);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
            }
        }
    }



    /**
     * Displays the item information and item data information
     * for the given bills itemSummary.
     * <p>
     * @param is an itemSummary whose containerType is 'stocks'
     */
    public void displayBillsDataForItem (ItemSummary is)
    {
        String containerType = is.getContentServiceInfo ().
                getContainerInfo ().getContainerName ();

        System.out.println("containerType = " + containerType );
       
        if (!(containerType.equals(ContainerTypesHelper.BILL ) || containerType.equals(ContainerTypesHelper.TELEPHONE) )) {
            throw new RuntimeException ("displayBillsDataForItem called with " +
                    "invalid container type: " + containerType);
        }
       

        DisplayItemInfo displayItemInfo = new DisplayItemInfo ();
        System.out.println("DisplayItemInfo:");
        displayItemInfo.displayItemSummaryInfo (is);
        System.out.println("");
        ItemData id = is.getItemData();

        if(id == null){
           System.out.println("ItemData == null");
        }else{
        	List accountsList = id.getAccounts();
            Object[] accounts = null;
            if (accountsList != null){
            	accounts = accountsList.getElements();
            }
            if (accounts == null || accounts.length == 0) {
                System.out.println ("\tNo accounts");
            }else {
                for (int accts = 0; accts < accounts.length; accts++) {
					BillsData billsData = (BillsData) accounts[accts];
                    System.out.println("\tAccount Holder: " + billsData.getAccountHolder() );
                    System.out.println("\tAccount Id: " + billsData.getAccountId());
                    System.out.println("\tItemAccountId: " + billsData.getItemAccountId() );
                    System.out.println("\tAccountName: " + billsData.getAccountName() );
                    System.out.println("\tAccountNumber: " + billsData.getAccountNumber() );
                    System.out.println("");

                    // Get List of Bill Objects
                    List billsList = billsData.getBills();
                    Object[] bills = null;
                    if (billsList != null){
                    	bills = billsList.getElements();
                    }
                    if (bills == null || bills.length == 0) {
                        System.out.println ("\t\tNo Bill objects");
                    }else {
                        for (int b = 0; b < bills.length; b++) {
							Bill bill = (Bill) bills[b];
                            System.out.println("\t\tBill Account Number: " + bill.getAccountNumber() );
                            System.out.println("\t\tBill Acct Type: " + bill.getAcctType() );
                            System.out.println("\t\tBill Due Date: " + Formatter.formatDate(bill.getDueDate().getDate(), Formatter.DATE_SHORT_FORMAT) );
                            System.out.println("\t\tBill Date: " + Formatter.formatDate(bill.getBillDate().getDate(), Formatter.DATE_SHORT_FORMAT) );
                            System.out.println("\t\tBill Past Due: "
									+ (bill.getPastDue() != null ? bill
											.getPastDue().getAmount() : 0.0));
                            System.out
									.println("\t\tBill Last payment: "
											+ (bill.getLastPayment() != null ? bill
													.getLastPayment()
													.getAmount()
													: 0.0));
                            System.out.println("\t\tBill Amount Due: "
									+ (bill.getAmountDue() != null ? bill
											.getAmountDue().getAmount() : 0.0));
                            System.out
									.println("\t\tBill Min Payment: "
											+ (bill.getMinPayment() != null ? bill
													.getMinPayment()
													.getAmount()
													: 0.0));
                            System.out.println("");

                            // Get List of AccountUsageData
                            List acctUsageDataList = bill.getAccountUsages();
                            Object[] acctUsageData = null;
                            if (acctUsageDataList != null){
                            	acctUsageData = acctUsageDataList.getElements();
                            }
                            if (acctUsageData == null || acctUsageData.length == 0) {
                                System.out.println ("\t\t\tNo AccountUsageData objects");
                            }else {
                                for (int usage = 0; usage < acctUsageData.length; usage++) {
									AccountUsageData aud = (AccountUsageData) acctUsageData[usage];
                                    System.out.println("\t\t\tAccount Usage Bill ID: " + aud.getBillId() );
                                    System.out.println("\t\t\tAccount Usage Units Used: " + aud.getUnitsUsed() );
                                }
                            }
                        }
                    }
                    System.out.println("");

                    // Get List of AccountUsageData
                    List acctUsageDataList = billsData.getAccountUsages();
                    Object[] acctUsageData = null;
                    if (acctUsageDataList != null){
                    	acctUsageData = acctUsageDataList.getElements();
                    }
                    if (acctUsageData == null || acctUsageData.length == 0) {
                        System.out.println ("\t\tNo AccountUsageData objects");
                    }else {
                       for (int usageData = 0; usageData < acctUsageData.length; usageData++) {
							AccountUsageData aud = (AccountUsageData) acctUsageData[usageData];
                            System.out.println("\t\tAccount Usage Bill ID: " + aud.getBillId() );
                            System.out.println("\t\tAccount Usage Units Used: " + aud.getUnitsUsed() );
                        }
                    }
                }
            }
        }
    }

    /**
     * Displays a user's Bills item data in the Yodlee software platform.
     *
     */
    public static void main (String args[])
    {
        if (args.length < 2) {
            throw new RuntimeException ("Usage: <username> <password>") ;
        }

        // Startup and initialize
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
        DisplayBillsData billsData = new DisplayBillsData ();
        UserContext userContext = null;

        // Login the user        
		System.out.println("Logging in user " + userName + " with password "
				+ password);
		userContext = loginUser.loginUser(userName, password);
		System.out.println("Login of user " + userName + " successful");
		
       // Display Bills Account
        billsData.displayBillsData(userContext);

        // Log out the user
        System.out.println ("Logging out " + userName);
        loginUser.logoutUser (userContext);
        System.out.println ("Done logging out " + userName);
    }
}
