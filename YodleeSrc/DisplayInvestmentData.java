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
import com.yodlee.soap.collections.ArrayOfString;
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
import com.yodlee.soap.core.dataservice.types.HoldingData;
import com.yodlee.soap.core.dataservice.types.InvestmentData;
import com.yodlee.soap.core.dataservice.types.InvestmentTransactionsData;
import com.yodlee.soap.core.dataservice.types.TaxLot;

/**
 * Displays a user's Investment item data in the Yodlee software platform.
 *
 */
public class DisplayInvestmentData extends ApplicationSuper {

	protected DataService dataService;

    /**
     * Constructs an instance of the DisplayInvestmentData class that
     * displays Investment accounts
     */
    public DisplayInvestmentData ()
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
     * Dump the InvestmentData object.
     * @param is
     */
    public void dumpInvestmentDataForItem( ItemSummary is){
        ItemData id = is.getItemData();
        if(id == null){
           System.out.println("\tItemData == null");
        }else{
        	List accountList = id.getAccounts();
        	Object[] accounts = null;
        	if (accountList != null){
             accounts = id.getAccounts().getElements();
        	}
            if (accounts == null || accounts.length == 0) {
                System.out.println ("\tNo accounts");
            }else {
                for (int accts = 0; accts < accounts.length; accts++) {
					InvestmentData investmentData = (InvestmentData) accounts[accts];
                    System.out.println("\n\nDumping InvestmentData Object");
                    try{
                        DataDumper.dumper(investmentData);
                    }catch(Exception e ){
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * Displays all the item summaries of investment items of the user.
     * <p>
     * @param userContext The user context.
     */
    public void displayInvestmentData (UserContext userContext,boolean isHistoryNeeded)
    {
        /*SummaryRequest sr = new SummaryRequest(
                new String[] {ContainerTypes.INVESTMENT },
                new DataExtent[] { DataExtent.getDataExtentForAllLevels() }
        );*/
        SummaryRequest sr = new SummaryRequest();
        List list = new List();
        list.setElements(new String[] {ContainerTypesHelper.INVESTMENT });
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
                
        //List itemSummaries = dataService.getItemSummariesForContainer(userContext, "stocks");
        if (itemSummaries == null || itemSummaries.length == 0) {
            System.out.println ("No investment data available");
            return;
        }
        for (int i = 0; i < itemSummaries.length; i++) {
			ItemSummary is = (ItemSummary) itemSummaries[i];
            displayInvestmentDataForItem (is);
            // Dump the InvestmentData Object
            //dumpInvestmentDataForItem(is);
        }
    }

    /**
     * Displays the item information and item data information
     * for the given investment itemSummary.
     * <p>
     * @param is an itemSummary whose containerType is 'stocks'
     */
    public void displayInvestmentDataForItem (ItemSummary is)
    {
        String containerType = is.getContentServiceInfo ().
                getContainerInfo ().getContainerName ();
        //System.out.println("containerType = " + containerType );
        if (!containerType.equals(ContainerTypesHelper.INVESTMENT )) {
            throw new RuntimeException ("displayInvestmentDataForItem called with " +
                    "invalid container type: " + containerType);
        }
        DisplayItemInfo displayItemInfo = new DisplayItemInfo ();
        System.out.println("DisplayItemInfo:");
        displayItemInfo.displayItemSummaryInfo (is);
        System.out.println("");
        ItemData id = is.getItemData();
        if(id == null){
           System.out.println("ItemData == null");
        }else {
            // Get Account
        	List accountsList = null;
        	accountsList = id.getAccounts();;
            Object[] accounts = null;
            if (accountsList != null){
            	accounts = accountsList.getElements();
            }
            if (accounts == null || accounts.length == 0) {
                System.out.println ("\tNo accounts");
            }else{
                for (int accts = 0; accts < accounts.length; accts++) {
					InvestmentData investment = (InvestmentData) accounts[accts];
                    System.out.println("\tAccount Name: "+ investment.getAccountName() );
                    System.out.println("\tAccount Number: "+ investment.getAccountNumber() );
                    System.out.println("\tAccount Holder: "+ investment.getAccountHolder() );
                    System.out.println("\tAccount Type: "+ investment.getAcctType() );
                    System.out.println("\tCash: "+ Formatter.formatMoney(investment.getCash()) );
                    System.out.println("\tTotal Balance: "+ Formatter.formatMoney(investment.getTotalBalance()) );
                    System.out.println("\tTotal Vested Balance: "+ Formatter.formatMoney(investment.getTotalVestedBalance()) );
                    System.out.println("\tTotal Unvested Balance: "+ Formatter.formatMoney(investment.getTotalUnvestedBalance()) );
                    System.out.println("\tMargin Balance: "+ Formatter.formatMoney(investment.getMarginBalance()) );
                    long lu = investment.getLastUpdated().longValue() * 1000;
			        Date date = new Date(lu);
                    System.out.println ("\tLast Updated: " + Formatter.formatDate(date, Formatter.DATE_SHORT_FORMAT) );
                    System.out.println("\tAs of date: " + Formatter.formatDate(investment.getAsofDate().getDate(), Formatter.DATE_SHORT_FORMAT));
                    // Get HoldingData
                    List holdingsList = investment.getHoldings();
                    Object[] holdings = null;
                    if (holdings != null){
                    	holdings = holdingsList.getElements();
                    }
                    if(holdings == null || holdings.length == 0 ){
                        System.out.println("\t\tNo HoldingData");
                    }else{
                        System.out.println("\t\t**HoldingData**");
                        for (int hol = 0; hol < holdings.length; hol++) {
							HoldingData holding = (HoldingData) holdings[hol];
                            System.out.println("\t\tHoldingData Symbol: " + holding.getSymbol() );
                            System.out.println("\t\tHoldingData Quantity: " + holding.getQuantity() );
                            System.out.println("\t\tHoldingData Vested Quantity: " + holding.getVestedQuantity() );
                            System.out.println("\t\tHoldingData Unvested Quantity: " + holding.getUnvestedQuantity() );
                            System.out.println("\t\tHoldingData Value: " + Formatter.formatMoney(holding.getValue()) );
                            System.out.println("\t\tHoldingData Vested Value: " + Formatter.formatMoney(holding.getVestedValue()) );
                            System.out.println("\t\tHoldingData Unvested Value: " + Formatter.formatMoney(holding.getUnvestedValue()) );
                            System.out.println("\t\tHoldingData Description: " + holding.getDescription() );
                            System.out.println("\t\tHoldingData Price: " + Formatter.formatMoney(holding.getPrice()) );
                            System.out.println("\t\tHoldingData Link: " + holding.getLink() );
                            System.out.println("\t\tHoldingData HoldingType: " + holding.getHoldingType() );
                            System.out.println("\t\tHoldingData HoldingTypeId: " + holding.getHoldingTypeId() );
                            System.out.println("\t\tHoldingData Percentage Allocaton: " + holding.getPercentAllocation() );
                            System.out.println("\t\tHoldingData Percantage Change: " + holding.getPercentageChange() );
                            System.out.println("\t\tHoldingData Employee Contribution: " + Formatter.formatMoney(holding.getEmployeeContribution()) );
                            System.out.println("\t\tHoldingData Employeer Contribution: " + Formatter.formatMoney(holding.getEmployerContribution()) );
                            System.out.println("\t\tHoldingData Cusip Number: " + holding.getCusipNumber() );
                            System.out.println("\t\tHoldingData Daily Change: " + Formatter.formatMoney(holding.getDailyChange()) );
                            System.out.println("\t\tHoldingData Cost Basis: " + Formatter.formatMoney(holding.getCostBasis()) );
                            //System.out.println("");
                            // Get TaxLots
                            List taxLotsList = holding.getTaxLots();
                            Object[] taxLots = null;
                            if (taxLotsList != null){
                            	taxLots = taxLotsList.getElements();
                            }
                            if(taxLots == null || taxLots.length == 0 ){
                                System.out.println("\t\t\tNo TaxLots");
                            }else{
                                System.out.println("\t\t\t**TaxLots**");
                                for (int lots = 0; lots < taxLots.length; lots++) {
									TaxLot taxLot = (TaxLot) taxLots[lots];
                                    System.out.println("\t\t\tTaxLot Symbol: " + taxLot.getSymbol() );
                                    System.out.println("\t\t\tTaxLot Description: " + taxLot.getDescription() );
                                    System.out.println("\t\t\tTaxLot Quantity: " + taxLot.getQuantity() );
                                    System.out.println("\t\t\tTaxLot Amount: " + Formatter.formatMoney(taxLot.getAmount()) );
                                    System.out.println("\t\t\tTaxLot Price: " + Formatter.formatMoney(taxLot.getPrice()) );
                                    System.out.println("\t\t\tTaxLot Link: " + taxLot.getLink() );
                                    System.out.println("\t\t\tTaxLot Cusip Number: " + taxLot.getCusipNumber() );
                                    System.out.println("");
                                }
                            }
                            System.out.println("");
                        }
                    }
                    //System.out.println("");
                    // Get InvestmentTransactionsData
                    
                    List transactionsList = investment.getInvestmentTransactions();;
                    
                    Object[] transactions = null;
                    if (transactionsList != null){
                    	transactions = transactionsList.getElements();
                    }
                    if(transactions == null || transactions.length == 0 ){
                        System.out.println("\t\tNo InvestmentTransactionsData");
                    }else{
                        System.out.println("\t\t**InvestmentTransactionsData**");
                        for (int txns = 0; txns < transactions.length; txns++) {
							InvestmentTransactionsData trans = (InvestmentTransactionsData) transactions[txns];
                            System.out.println("\t\tTranaction Symbol: " + trans.getSymbol() );
                            System.out.println("\t\tTranaction Amount: " + Formatter.formatMoney(trans.getAmount()) );
                            System.out.println("\t\tTranaction Price: " + Formatter.formatMoney(trans.getPrice()) );
                            System.out.println("\t\tTranaction Quantity: " + trans.getQuantity() );
                            System.out.println("\t\tTranaction Date: " + Formatter.formatDate(trans.getTransDate().getDate(), Formatter.DATE_SHORT_FORMAT) );
                            System.out.println("\t\tTransaction Description: " + trans.getDescription() );
                            System.out.println("\t\tTranaction Link: " + trans.getLink() );
                            System.out.println("\t\tTranaction Type: " + trans.getTransactionType() );
                            System.out.println("\t\tTranaction Conf #: " + trans.getConfirmationNumber() );
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
                for (int acctHis = 0; acctHis < acctHistory.length; acctHis++) {
					AccountHistory acctHist = (AccountHistory) acctHistory[acctHis];
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
                        for (int his = 0; his < histories.length; his++) {
							InvestmentData investment = (InvestmentData) histories[his];
                            System.out.println("\t\tAccount Name: "+ investment.getAccountName() );
                            System.out.println("\t\tAccount Number: "+ investment.getAccountNumber() );
                            System.out.println("\t\tAccount Holder: "+ investment.getAccountHolder() );
                            System.out.println("\t\tAccount Type: "+ investment.getAcctType() );
                            System.out.println("\t\tCash: "+ Formatter.formatMoney(investment.getCash()) );
                            System.out.println("\t\tTotal Balance: "+ Formatter.formatMoney(investment.getTotalBalance()) );
                            System.out.println("\t\tTotal Vested Balance: "+ Formatter.formatMoney(investment.getTotalVestedBalance()) );
                            System.out.println("\t\tTotal Unvested Balance: "+ Formatter.formatMoney(investment.getTotalUnvestedBalance()) );
                            System.out.println("\t\tMargin Balance: "+ Formatter.formatMoney(investment.getMarginBalance()) );
                            // You can differentiate between the datasets using
                            // getLastUpdated
			                long lu = investment.getLastUpdated().longValue() * 1000;
			                Date date = new Date(lu);
                            System.out.println ("\t\tLast Updated: " + date.toString() );
                            System.out.println("");
                        }
                    }
                }
            }
        }
    }
    /**
     * Displays a user's Investment item data in the Yodlee software platform.
     *
     */
    public
            static void main (String args[])
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
        DisplayInvestmentData investmentData = new DisplayInvestmentData ();
        UserContext userContext = null;
        // Login the user
		System.out.println("Logging in user " + userName + " with password "
				+ password);
		userContext = loginUser.loginUser(userName, password);
		System.out.println("Login of user " + userName + " successful");
        
        // Display Investment Account
        investmentData.displayInvestmentData(userContext, true);
        // Log out the user
        System.out.println ("Logging out " + userName);
        loginUser.logoutUser (userContext);
        System.out.println ("Done logging out " + userName);
    }

}
