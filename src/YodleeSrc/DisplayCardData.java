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
import com.yodlee.soap.core.dataservice.types.Bill;
import com.yodlee.soap.core.dataservice.types.CardData;
import com.yodlee.soap.core.dataservice.types.CardStatementData;
import com.yodlee.soap.core.dataservice.types.CardTransactionData;

/**
 * Displays a user's Card data in the Yodlee software platform.
 *
 */
public class DisplayCardData extends ApplicationSuper {

	protected DataService dataService;

    /**
     * Constructs an instance of the DisplayCardData class that
     * displays Credit Card accounts
     */

    public DisplayCardData ()
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
     * Displays all the item summaries of credit card items of the user.
     * <p>
     * @param userContext The user context.
     */
    public void displayCardData (UserContext userContext, boolean isHistoryNeeded)
    {
        /**SummaryRequest sr = new SummaryRequest(
                new String[] {ContainerTypes.CREDIT_CARD},
                new DataExtent[] { DataExtent.getDataExtentForAllLevels() }
        );*/
    	SummaryRequest sr = new SummaryRequest();
    	List list = new List();
    	list.setElements(new String[] {ContainerTypesHelper.CREDIT_CARD });
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
            System.out.println ("No credit card data available");
            return;
        }

        for (int i = 0; i < itemSummaries.length; i++) {
			ItemSummary is = (ItemSummary) itemSummaries[i];
			displayCardDataForItem(is);
			// Dump the CardData Object
			// dumpCardDataForItem(is);
		}
    }

    /**
     * Dump the CardData object.
     * @param is
     */
    public void dumpCardDataForItem( ItemSummary is){
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
					CardData cardData = (CardData) accounts[accts];
                    System.out.println("\n\nDumping CardData Object");
                    try{
                        DataDumper.dumper(cardData);
                    }catch(Exception e ){
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * Displays the item information and item data information
     * for the given credit card itemSummary.
     * <p>
     * @param is an itemSummary whose containerType is 'credit_card'
     */
    public void displayCardDataForItem (ItemSummary is)
    {
        String containerType = is.getContentServiceInfo ().
                getContainerInfo ().getContainerName ();
        //System.out.println("containerType = " + containerType );

        if (!containerType.equals(ContainerTypesHelper.CREDIT_CARD )) {
            throw new RuntimeException ("displayCardsDataForItem called with " +
                    "invalid container type: " + containerType);
        }

        DisplayItemInfo displayItemInfo = new DisplayItemInfo ();
        displayItemInfo.displayItemSummaryInfo (is);
        System.out.println("");
        ItemData id = is.getItemData();

        if(id == null){
            System.out.println("ItemData == null");
        }else{
            // Accounts
        	List accountsList = id.getAccounts();
            Object[] accounts = null;
            if (accountsList != null){
            	accounts = accountsList.getElements();
            }
            if (accounts == null || accounts.length == 0) {
                System.out.println ("\tNo accounts");
            }else {
                System.out.println("\t**CardData**");
                for (int accts = 0; accts < accounts.length; accts++) {
					CardData cardData = (CardData) accounts[accts];
                    System.out.println("\tAccount Name : "      + cardData.getAccountName() );
                    System.out.println("\tAccount Number : "    + cardData.getAccountNumber() );
                    System.out.println("\tCredit Limit : "      + Formatter.formatMoney(cardData.getTotalCreditLine()) );
                    System.out.println("\tAvailable Credit : "  + Formatter.formatMoney(cardData.getAvailableCredit()) );
                    System.out.println("\tCurrent Balance : "   + Formatter.formatMoney(cardData.getRunningBalance()) );
                    System.out.println("\tDue Date : "          + Formatter.formatDate(cardData.getDueDate().getDate(), Formatter.DATE_SHORT_FORMAT));
                    System.out.println("\tMin Payment : "       + Formatter.formatMoney(cardData.getMinPayment()) );
                    //System.out.println("\tAmount Due : "        + Formatter.formatMoney(cardData.getAmountDue()) );
                    //System.out.println("\tLast Payment : "      + Formatter.formatMoney(cardData.getLastPayment()) );


                    System.out.println("\tAs Of Date : "        + Formatter.formatDate(cardData.getAsofDate().getDate(), Formatter.DATE_SHORT_FORMAT) );

                    long lu = cardData.getLastUpdated().longValue() * 1000;
                    Date date = new Date(lu);
                    System.out.println ("\tLast Updated: " + date.toString() );

                    // Get List of CardTransactionData
                    List transList = cardData.getCardTransactions();
                    Object[] trans = null;
                    if (transList != null){
                    	trans = transList.getElements();
                    }
                    if (trans == null || trans.length == 0) {
                        System.out.println ("\t\tNo tCardTransactionData objects");
                    }else {
                        System.out.println("\t\t**CardTransactionData**");
                        for (int txns = 0; txns < trans.length; txns++) {
							CardTransactionData ctd = (CardTransactionData) trans[txns];
                            System.out.println("\t\tTransaction Base Type: " + ctd.getTransactionBaseType() );
                            System.out.println("\t\tTransaction Type: " + ctd.getTransactionType() );
                            System.out.println("\t\tTransaction Description: " + ctd.getDescription() );
                            System.out.println("\t\tTransaction Post Date: "
                                    + Formatter.formatDate(ctd.getPostDate().getDate(), Formatter.DATE_SHORT_FORMAT) );
                            System.out.println("\t\tTransaction Trans Date: "
                                    + Formatter.formatDate(ctd.getTransDate().getDate(), Formatter.DATE_SHORT_FORMAT) );
                            System.out.println("\t\tTransaction Trans Amount: "
                                    + Formatter.formatMoney(ctd.getTransAmount()) );
                            System.out.println("");
                        }
                    }

                    // Get List of CardStatementData Objects
                    List statementsList = cardData.getCardStatements();
                    Object[] statements = null;
                    if (statementsList != null){
                    	statements = statementsList.getElements();
                    }
                    if (statements == null || statements.length == 0) {
                        System.out.println ("\t\tNo CardStatementData objects");
                    }else {
                        System.out.println("\t\t**CardStatementData**");
                        for (int stmts = 0; stmts < statements.length; stmts++) {
							CardStatementData csd = (CardStatementData) statements[stmts];

                            // Get Bill
                            Bill bill = csd.getBill();
                            System.out.println("\t\t\t**Bill**");
                            if(bill == null) {
                                System.out.println("\t\t\tBill Data is Null");
                            } else {
                                System.out.println("\t\t\tBill IS_HISTORIC: " + bill.getIsHistoric() );
                                System.out.println("\t\t\tBill Account Number: " + bill.getAccountNumber() );
                                System.out.println("\t\t\tBill Statement Balance: "
                                        + Formatter.formatMoney(bill.getEndingBalance()) );
                                System.out.println("\t\t\tBill Due Date: "
                                        + Formatter.formatDate(bill.getDueDate().getDate(), Formatter.DATE_SHORT_FORMAT));
                                System.out.println("");
                            }

                            // Get List of CardTransactionData
                            List statemenTransList = csd.getCardTransactions();
                            Object[] statemenTrans = null;
                            if (statemenTransList != null){
                            	statemenTrans = statemenTransList.getElements();
                            }
                            if (statemenTrans == null || statemenTrans.length == 0) {
                                System.out.println ("\t\t\tNo CardTransactionData objects");
                            }else {
                                System.out.println("\t\t\t**CardTransactionData**");
                                for (int cardTxns = 0; cardTxns < statemenTrans.length; cardTxns++) {
									CardTransactionData ctd = (CardTransactionData) statemenTrans[cardTxns];
                                    System.out.println("\t\t\tTransaction Base Type: " + ctd.getTransactionBaseType() );
                                    System.out.println("\t\t\tTransaction Type: " + ctd.getTransactionType() );
                                    System.out.println("\t\t\tTransaction Description: " + ctd.getDescription() );
                                    System.out.println("\t\t\tTransaction Post Date: "
                                            + Formatter.formatDate(ctd.getPostDate().getDate(),Formatter.DATE_SHORT_FORMAT) );
                                    System.out.println("\t\t\tTransaction Trans Date: "
                                            + Formatter.formatDate(ctd.getTransDate().getDate(),Formatter.DATE_SHORT_FORMAT) );
                                    System.out.println("\t\t\tTransaction Trans Amount: "
                                            + Formatter.formatMoney(ctd.getTransAmount() ));
                                    System.out.println("");
                                }
                            }

                        }
                    }


                    System.out.println("");
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
                    for (int acctHists = 0; acctHists < acctHistory.length; acctHists++) {
						AccountHistory acctHist = (AccountHistory) acctHistory[acctHists];

                        System.out.println("\tAccount Id : " +acctHist.getAccountId() );
                        List historiesList = acctHist.getHistory();
                        Object[] histories = null;
                        if (historiesList != null){
                        	histories = historiesList.getElements();
                        }
                        if(histories == null | histories.length == 0 ){
                            System.out.println("\t\tNo History");
                        }else{
                            System.out.println("\t\t**History**");
                            for (int hist = 0; hist < histories.length; hist++) {
								CardData cardData = (CardData) histories[hist];
                                System.out.println("\t\tAccount Number: " + cardData.getAccountNumber() );
                                System.out.println("\t\tAccount Id: " + cardData.getAccountId());
                                System.out.println("\t\tAmount Due: " + cardData.getAmountDue() );
                                System.out.println("\t\tLast Payment: " + cardData.getLastPayment() );
                                System.out.println("\t\tMin Payment: " + cardData.getMinPayment() );
                                System.out.println("\t\tRunning Balance: " + cardData.getRunningBalance() );
                                System.out.println("\t\tAvailable Credit: " + cardData.getAvailableCredit() );
                                System.out.println("\t\tTotal Credit Line: " + cardData.getTotalCreditLine() );
                                long lu = cardData.getLastUpdated().longValue() * 1000;
                                Date date = new Date(lu);
                                System.out.println ("\t\tLast Updated: " + date.toString() );
                                System.out.println("");

                            }
                        }
                    }
                }
            }
        }
    }



    /**
     * Displays a user's Credit Card item data in the Yodlee software platform.
     *
     */
    public  static void main (String args[])
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
        DisplayCardData cardData = new DisplayCardData ();
        UserContext userContext = null;

        // Login the user
		System.out.println("Logging in user " + userName + " with password "
				+ password);
		userContext = loginUser.loginUser(userName, password);
		System.out.println("Login of user " + userName + " successful");
        
        // Display Credit Card Accounts
        cardData.displayCardData(userContext,true);

        // Log out the user
        System.out.println ("Logging out " + userName);
        loginUser.logoutUser (userContext);
        System.out.println ("Done logging out " + userName);
    }
}
