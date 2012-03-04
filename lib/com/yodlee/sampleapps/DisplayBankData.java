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
import java.util.Date;

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
import com.yodlee.soap.core.dataservice.AccountHistory;
import com.yodlee.soap.core.dataservice.IllegalDataExtentExceptionFault;
import com.yodlee.soap.core.dataservice.ItemData;
import com.yodlee.soap.core.dataservice.SummaryRequest;
import com.yodlee.soap.core.dataservice.dataservice.DataService;
import com.yodlee.soap.core.dataservice.dataservice.DataServiceServiceLocator;
import com.yodlee.soap.core.dataservice.types.BankData;
import com.yodlee.soap.core.dataservice.types.BankTransactionData;
import com.yodlee.soap.core.dataservice.DataExtent;
import com.yodlee.soap.core.dataservice.ContainerCriteria;

/**
 * Displays a user's Bank item data in the Yodlee software platform.
 */
public class DisplayBankData extends ApplicationSuper {

	protected DataService dataService;

    /**
     * Constructs an instance of the DisplayBankData class that
     * provides the functionality to display user Bank item
     * information.
     */
    public
    DisplayBankData ()
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
     * Displays all the item summaries of banking items of the user.
     * <p>
     * @param userContext The user context.
     */
    public
    void displayBankData (UserContext userContext, boolean isHistoryNeeded)
    {
        // This is an alternative way to get itemSummaries, but the problem
        // here is that it only get the default level which does not
        // include BankTransactionData
        // List itemSummaries =
        //    dataService.getItemSummariesForContainer (userContext,
        //                                              "bank");


    	DataExtent dataExtent = new DataExtent();
    	dataExtent.setStartLevel(0);
    	dataExtent.setEndLevel(Integer.MAX_VALUE);
    	System.out.println("Extent Levels" + dataExtent.getExtentLevels());
    	

    	SummaryRequest sr = new SummaryRequest();
    	ContainerCriteria ccriteria = new ContainerCriteria();			    
    	ccriteria.setContainerType(ContainerTypesHelper.BANK);
    	ccriteria.setDataExtent(dataExtent);
    	List list = new List();
    	list.setElements(new ContainerCriteria[] {ccriteria});
    	sr.setContainerCriteria(list);
	       	
        sr.setHistoryNeeded(isHistoryNeeded);
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
            System.out.println ("No bank data available");
            return;
        }

        for (int i = 0; i < itemSummaries.length; i++){
        	ItemSummary is = (ItemSummary) itemSummaries[i];
        	displayBankDataForItem (is);
        }
    }


    /**
     * Dump the BankData object.
     * @param is
     */
    public void dumpBankDataForItem( ItemSummary is){

        ItemData id = is.getItemData();

        if(id == null){
            System.out.println("\tItemData == null");
        }else{
        	List accountList = id.getAccounts();
            Object[] accounts = null;
            if (accountList != null){
            	accounts = accountList.getElements();
            }
            if (accounts == null || accounts.length == 0) {
                System.out.println ("\tNo accounts");
            }else {
            	for (int i = 0; i < accounts.length; i++)                 
                {
                    BankData bankData = (BankData) accounts[i];
                    System.out.println("\n\nDumping BankData Object");
                    try{
                        DataDumper.dumper(bankData);
                    }catch(Exception e ){
                        e.printStackTrace();
                    }
                }
            }
        }
    }


    /**
     * Displays the item information and item data information
     * for the given bank itemSummary.
     * <p>
     * @param is an itemSummary whose containerType is 'bank'
     */
    public void displayBankDataForItem (ItemSummary is)
    {
        System.out.println("");
        String containerType = is.getContentServiceInfo ().
                getContainerInfo ().getContainerName ();

        if (!containerType.equals("bank")) {
            throw new RuntimeException ("displayBankDataForItem called with " +
                    "invalid container type: " + containerType);
        }

        DisplayItemInfo displayItemInfo = new DisplayItemInfo ();
        displayItemInfo.displayItemSummaryInfo (is);

        // get BankData
        ItemData id = is.getItemData();
        if(id == null){
            System.out.println("\tItemData == null");
        }else{
            List accountList = id.getAccounts();
        	Object[] accounts = null;
        	if (accountList != null){
        		accounts = accountList.getElements();
        	}
            if (accounts == null || accounts.length == 0) {
                System.out.println ("\tNo accounts");
            }else {
                for (int i = 0; i < accounts.length; i++) {
                    System.out.println("\n\t**BankData**");
                    BankData bankData = (BankData) accounts[i];
                    System.out.println ("\tBank Account Name: " +
                            bankData.getAccountName ());
                    System.out.println ("\tBank Account Number: " +
                            bankData.getAccountNumber() );
                    System.out.println ("\tBank Account Id: " +
                            bankData.getBankAccountId() );
                    System.out.println("\tBank Account Type: " +
                            bankData.getAcctType() );
                    System.out.println ("\tBank Account Identifier: " +
                            bankData.getBankAccountId());
                    System.out.println ("\tBank Account Balance: " +
                            Formatter.formatMoney(bankData.getAvailableBalance()));
                    System.out.println ("\tBank Current Balance: " +
                            Formatter.formatMoney(bankData.getCurrentBalance() ));
                    System.out.println ("\tBank As Of Date: " +
                            Formatter.formatDate(bankData.getAsOfDate().getDate(),Formatter.DATE_SHORT_FORMAT) );

                    long lu = bankData.getLastUpdated().longValue() * 1000;
                    Date date = new Date(lu);
                    System.out.println ("\tBank Last Updated: " + Formatter.formatDate(date, Formatter.DATE_LONG_FORMAT) );

                    List bankTransactionsList = bankData.getBankTransactions ();
                    Object[] bankTransactions = null;
                    if (bankTransactionsList != null){
                    	bankTransactions = bankTransactionsList.getElements();
                    }

                    if (bankTransactions == null || bankTransactions.length == 0) {
                        System.out.println ("\n\t\tNo bank transactions");
                    }else {
                        System.out.println("\n\t\t**BankTransactionData**");
                        for (int txns = 0; txns < bankTransactions.length; txns++) {
                            BankTransactionData transactionData =
                                    (BankTransactionData) bankTransactions[txns];
                            System.out.println ("\t\tPost Date: " +
                                    Formatter.formatDate(transactionData.getPostDate().getDate(), Formatter.DATE_SHORT_FORMAT) ) ;
                            System.out.println ("\t\tTrans Date: " +
                                    Formatter.formatDate(transactionData.getTransactionDate().getDate(), Formatter.DATE_SHORT_FORMAT) ) ;
                            System.out.println ("\t\tTransaction Id: " +
                                    transactionData.getBankTransactionId ());
                            System.out.println ("\t\tTransaction Description: " +
                                    transactionData.getDescription ());
                            System.out.println ("\t\tTransaction Amount: " +
                                    Formatter.formatMoney(transactionData.getTransactionAmount ()));
                            //System.out.println ("\t\tTransaction Type " +
                            //        (transactionData.getTransactionType ()));
                            System.out.println ("\t\tTransaction Base Type: " +
                                    (transactionData.getTransactionBaseType()));
                            // Note: This category is gathered from the data source, this is not
                            // the transaction category from the Yodlee Transaction Categorization
                            // engine.  To get the Yodlee Cateogry the Transaction Search APIs
                            // must be used.
                            System.out.println("\t\tCategory: " +
                                    (transactionData.getCategory()));
                            System.out.println("");
                        }
                    }
                }
            }

            // Account History
            List acctHistoryList = id.getAccountHistory();
            
            Object[] acctHistory = null;
            if (acctHistoryList != null){
            	acctHistory = acctHistoryList.getElements();
            }

            if( acctHistory == null || acctHistory.length == 0 ){
                System.out.println("\tNo Account History");
            }else{                
                System.out.println("\n\t**Account History**");
                for (int acchist = 0 ; acchist < acctHistory.length ; acchist++){
                    AccountHistory acctHist = (AccountHistory)acctHistory[acchist];
                    System.out.println("\tAccount Id : " +acctHist.getAccountId() );
                    List historyList = acctHist.getHistory();
                    Object[] histories = null;
                    if (historyList != null){
                    	histories = historyList.getElements();
                    }
                    if(histories == null | histories.length == 0 ){
                        System.out.println("\t\tNo History");
                    }else{
                        System.out.println("\t\t**History**");
                        for (int hist = 0; hist < histories.length; hist++){
                            BankData bankData = (BankData) histories[hist];
                            System.out.println ("\t\tBank Account Name: " +
                                    bankData.getAccountName ());
                            System.out.println ("\t\tBank Account Identifier: " +
                                    bankData.getBankAccountId ());
                            System.out.println ("\t\tBank Available Balance: " +
                                    (bankData.getAvailableBalance ()).getAmount ());
                            System.out.println ("\t\tBank Current Balance: " +
                                    (bankData.getCurrentBalance()  ).getAmount());
                            // You can differentiate between the datasets using
                            // getLastUpdated
                            long lu = bankData.getLastUpdated().longValue() * 1000;
                            Date date = new Date(lu);
                            System.out.println ("\t\tBank Last Updated: " + date.toString() );
                            System.out.println("");

                        }
                    }
                }
            }

        }
    }

    /**
     * Logs the user in and uses the user context
     * to display all the user's banking information
     *
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
        DisplayBankData displayBankData = new DisplayBankData ();
        UserContext userContext = null;

        System.out.println("Logging in user " + userName + " with password "
				+ password);
		userContext = loginUser.loginUser(userName, password);
		System.out.println("Login of user " + userName + " successful");        

        // Displays the users bank data
        displayBankData.displayBankData (userContext, false);

        // Log out the user
        System.out.println ("Logging out " + userName);
        loginUser.logoutUser (userContext);
        System.out.println ("Done logging out " + userName);
    }
}
