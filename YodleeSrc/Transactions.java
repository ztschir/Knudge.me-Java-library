/*
 * Copyright 2009 Yodlee, Inc.  All Rights Reserved.  Your use of this code
 * requires a license from Yodlee.  Any such license to this code is
 * restricted to evaluation/illustrative purposes only. It is not intended
 * for use in a production environment, and Yodlee disclaims all warranties
 * and/or support obligations concerning this code, regardless of the terms
 * of any other agreements between Yodlee and you.
 */
package com.yodlee.sampleapps;

import java.util.HashMap;

import javax.xml.rpc.ServiceException;

import com.yodlee.sampleapps.helper.ContainerTypesHelper;
import com.yodlee.sampleapps.helper.Formatter;
import com.yodlee.sampleapps.helper.IOUtils;
import com.yodlee.soap.collections.ArrayOfString;
import com.yodlee.soap.collections.ArrayOflong;
import com.yodlee.soap.collections.List;
import com.yodlee.soap.collections.core.transactioncategorization.ArrayOfCategory;
import com.yodlee.soap.collections.core.transactioncategorization.ArrayOfUserCategorizationObject;
import com.yodlee.soap.collections.core.transactionsearch.ArrayOfTransactionView;
import com.yodlee.soap.common.ItemSummary;
import com.yodlee.soap.common.UserContext;
import com.yodlee.soap.core.IllegalArgumentValueExceptionFault;
import com.yodlee.soap.core.InvalidConversationCredentialsExceptionFault;
import com.yodlee.soap.core.InvalidUserContextExceptionFault;
import com.yodlee.soap.core.dataservice.dataservice.DataService;
import com.yodlee.soap.core.dataservice.dataservice.DataServiceServiceLocator;
import com.yodlee.soap.core.dataservice.types.BankData;
import com.yodlee.soap.core.dataservice.types.BillPayServiceData;
import com.yodlee.soap.core.dataservice.types.BillsData;
import com.yodlee.soap.core.dataservice.types.CardData;
import com.yodlee.soap.core.dataservice.types.InsuranceData;
import com.yodlee.soap.core.dataservice.types.InsuranceLoginAccountData;
import com.yodlee.soap.core.dataservice.types.InvestmentData;
import com.yodlee.soap.core.dataservice.types.Loan;
import com.yodlee.soap.core.dataservice.types.LoanLoginAccountData;
import com.yodlee.soap.core.dataservice.types.MailData;
import com.yodlee.soap.core.dataservice.types.RewardPgm;
import com.yodlee.soap.core.transactioncategorization.Category;
import com.yodlee.soap.core.transactioncategorization.InvalidUserCategorizationObjectExceptionFault;
import com.yodlee.soap.core.transactioncategorization.UserCategorizationObject;
import com.yodlee.soap.core.transactioncategorization.transactioncategorizationservice.TransactionCategorizationService;
import com.yodlee.soap.core.transactioncategorization.transactioncategorizationservice.TransactionCategorizationServiceServiceLocator;
import com.yodlee.soap.core.transactionmanagement.TransactionSplitType;
import com.yodlee.soap.core.transactionsearch.ItemAccountId;
import com.yodlee.soap.core.transactionsearch.TransactionSearchClients;
import com.yodlee.soap.core.transactionsearch.TransactionSearchExecInfo;
import com.yodlee.soap.core.transactionsearch.TransactionSearchFetchRequest;
import com.yodlee.soap.core.transactionsearch.TransactionSearchFilter;
import com.yodlee.soap.core.transactionsearch.TransactionSearchIdentifier;
import com.yodlee.soap.core.transactionsearch.TransactionSearchRequest;
import com.yodlee.soap.core.transactionsearch.TransactionSearchResult;
import com.yodlee.soap.core.transactionsearch.TransactionSearchResultRange;
import com.yodlee.soap.core.transactionsearch.TransactionView;
import com.yodlee.soap.core.transactionsearch.exceptions.InvalidSearchIdentifierExceptionFault;
import com.yodlee.soap.core.transactionsearch.transactionsearchservice.TransactionSearchService;
import com.yodlee.soap.core.transactionsearch.transactionsearchservice.TransactionSearchServiceServiceLocator;

public class Transactions extends ApplicationSuper {

	protected TransactionSearchService transactionSearchService;
	protected DataService dataService;
	protected TransactionCategorizationService tcService;

    private static int OPTION_CNT = 1;
    private static final int NAV_QUIT = 0;
    private static int NAV_VIEW_ITEM_ACCOUNT_TRANSACTIONS = OPTION_CNT++;
    private static int NAV_VIEW_ALL_TRANSACTIONS = OPTION_CNT++;
    private static int NAV_SEARCH_TRANSACTIONS = OPTION_CNT++;
    private static int NAV_CATEGORIZE_TRANSACTION = OPTION_CNT++;
    private static int NAV_ADD_SUB_CATEGORY = OPTION_CNT++;
    private static int NAV_DEL_SUB_CATEGORY = OPTION_CNT++;
    private static int NAV_UPDATE_SUB_CATEGORY = OPTION_CNT++;

    public static final String InvalidEnterPrompt = "Invalid Entry.. Enter ";
    public static final String EXIT_VALUE = "-1";
    public static final String EXIT_STRING = " or " + EXIT_VALUE + " to Quit : ";
    public static final String EnterPrompt = "Enter ";
    public static final String ItemIdPrompt = EnterPrompt + "ItemId : ";
    public static final String ReItemIdPrompt = InvalidEnterPrompt + ItemIdPrompt
    		+ EXIT_STRING;;

    public static final String ItemAccountIdPrompt = EnterPrompt + "ItemAccountId : ";
    public static final String ReItemAcountIdPrompt = InvalidEnterPrompt + ItemAccountIdPrompt
            + EXIT_STRING;
    public static final String searchStringPrompt = EnterPrompt + "Search String : ";
    public static final String ReSarchStringPrompt = InvalidEnterPrompt + searchStringPrompt
            + EXIT_STRING;
    public static final String TransactionIdPrompt = EnterPrompt + "TransactionId : ";
    public static final String ReTransactionIdPrompt = InvalidEnterPrompt + TransactionIdPrompt
    		+ EXIT_STRING;
    public static final String CategoryIdPrompt = EnterPrompt + "CategoryId : ";
    public static final String ReCategoryIdPrompt = InvalidEnterPrompt + CategoryIdPrompt
    		+ EXIT_STRING;

    public static final String CategoryNamePrompt = EnterPrompt + "CategoryName : ";
    public static final String ReCategoryNamePrompt = InvalidEnterPrompt + CategoryNamePrompt
    		+ EXIT_STRING;

    public static final String ContainerTypePrompt = EnterPrompt + "ContainerType : ";
    public static final String ReContainerTypePrompt = InvalidEnterPrompt + ContainerTypePrompt
    		+ EXIT_STRING;


    public Transactions ()
    {
        super ();
        TransactionSearchServiceServiceLocator locator1 = new TransactionSearchServiceServiceLocator();
        String serviceName1 = locator1.getTransactionSearchServiceWSDDServiceName();
        locator1.setTransactionSearchServiceEndpointAddress(System.getProperty("com.yodlee.soap.services.url")
						+ "/" + serviceName1);
        try {
        	transactionSearchService = locator1.getTransactionSearchService();
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
		TransactionCategorizationServiceServiceLocator locator3 = new TransactionCategorizationServiceServiceLocator();
        String serviceName3 = locator3.getTransactionCategorizationServiceWSDDServiceName();
        locator3.setTransactionCategorizationServiceEndpointAddress(System.getProperty("com.yodlee.soap.services.url")
						+ "/" + serviceName3);
        try {
        	tcService = locator3.getTransactionCategorizationService();
		} catch (ServiceException se) {
			throw new RuntimeException(se);
		}
    }

    public void doMenu(UserContext userContext) {
        boolean loop = true;
        int choice = 0;
        while (loop) {
            try {
                System.out.println("\nTransactions Menu");
                System.out.println(NAV_VIEW_ITEM_ACCOUNT_TRANSACTIONS + ". View Transactions for ItemAccount");
                System.out.println(NAV_VIEW_ALL_TRANSACTIONS + ". View all transactions");
                System.out.println(NAV_SEARCH_TRANSACTIONS + ". Search Transactions");
                System.out.println(NAV_CATEGORIZE_TRANSACTION + ". Categorize Transaction");
                System.out.println(NAV_ADD_SUB_CATEGORY + ". Add sub category");
                System.out.println(NAV_DEL_SUB_CATEGORY + ". Delete sub category");
                System.out.println(NAV_UPDATE_SUB_CATEGORY + ". Update sub category");
                System.out.println("Enter " + NAV_QUIT + " Exit");
                System.out.println("\n");
                System.out.print("Enter Choice : ");
                choice = IOUtils.readInt();

                if (choice == NAV_VIEW_ITEM_ACCOUNT_TRANSACTIONS)
                    viewTransactionsForItemAccount(userContext);
                if (choice == NAV_VIEW_ALL_TRANSACTIONS)
                    viewAllTransactions(userContext);
                if (choice == NAV_SEARCH_TRANSACTIONS)
                    searchTransactions(userContext);
                if (choice == NAV_CATEGORIZE_TRANSACTION)
                    categorizeTransaction(userContext);
                if (choice == NAV_ADD_SUB_CATEGORY)
                    addSubCategory(userContext);
                if (choice == NAV_DEL_SUB_CATEGORY)
                    deleteSubCategory(userContext);
                if (choice == NAV_UPDATE_SUB_CATEGORY)
                    updateSubCategory(userContext);
                if (choice == NAV_QUIT)
                    loop = false;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void searchTransactions(UserContext userContext){
        String searchString = IOUtils.promptInput(
                searchStringPrompt,
                ReSarchStringPrompt);

        System.out.println("Searching for \"" + searchString + "\"...");

        long startRange = 1;
        long endRange = 10;

        // Create Results Range
        TransactionSearchResultRange txSearchResultRange = new TransactionSearchResultRange();
        txSearchResultRange.setStartNumber(startRange);
        txSearchResultRange.setEndNumber(startRange);

        // Create  TransactionSearchFilter
        TransactionSearchFilter txSearchFilter
                = new TransactionSearchFilter();

        // Create TransactionSearchRequest
        TransactionSearchRequest txSearchRequest = new TransactionSearchRequest();
        txSearchRequest.setSearchFilter(txSearchFilter);
        txSearchRequest.setContainerType("all");
        txSearchRequest.setIgnorePaymentTransactions(false) ;
        txSearchRequest.setIncludeAggregatedTransactions(true);
        txSearchRequest.setResultRange(txSearchResultRange);
        //txSearchRequest.setIgnoreUserInput(true);
        txSearchRequest.setUserInput(searchString);

        TransactionSearchExecInfo txSearchExecInfo;
		try {
			txSearchExecInfo = transactionSearchService.executeUserSearchRequest(userContext, txSearchRequest);
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
        if ( txSearchExecInfo != null) {// && txSearchExecInfo.getNumberOfHits() > 0
        	displayTransactionSearchExecInfo(txSearchExecInfo);
        	viewTransactions(userContext, txSearchExecInfo);
        } else {
        	System.out.println("No transactions found");
        }
    }

    public void displayTransactionSearchExecInfo(TransactionSearchExecInfo transactionSearchExecInfo) {
        System.out.println("TransactionSearchExecInfo Details: ");
        System.out.println("\tCount of All Transaction: " + transactionSearchExecInfo.getCountOfAllTransaction());
        System.out.println("\tNumber of Hit: " + transactionSearchExecInfo.getNumberOfHits());
        System.out.println("\tNumber of Hits for Projected: " + transactionSearchExecInfo.getNumberOfHitsForProjected());
        TransactionSearchIdentifier transactionSearchIdentifier = transactionSearchExecInfo.getSearchIdentifier();
        if(transactionSearchIdentifier == null) {
        	System.out.println("\ttransactionSearchIdentifier is null");
        } else {
        	System.out.println("\ttransactionSearchIdentifier details: ");

        	ArrayOfString identifiers = transactionSearchIdentifier.getIdentifiers();
        	if(identifiers != null) {
	        	for(int i=0; i<identifiers.getElements().length; i++) {
	        		System.out.println("\t\tidentifiers[" + i + "] = " + identifiers.getElements(i));
	        	}
        	} else {
        		System.out.println("\t\tidentifiers is null");
        	}

        	String identifier = transactionSearchIdentifier.getIdentifier();
        	if(identifier != null) {
        		System.out.println("\t\tidentifier = " + identifier);
        	} else {
        		System.out.println("\t\tidentifier is null");
        	}
        }


    }

    public void viewAllTransactions(UserContext userContext){

        long startRange = 1;
        long endRange = 200;

        // Create Results Range
        TransactionSearchResultRange txSearchResultRange = new TransactionSearchResultRange();
        txSearchResultRange.setStartNumber(startRange);
        txSearchResultRange.setEndNumber(endRange);


        // Create  TransactionSearchFilter
        TransactionSearchFilter txSearchFilter
                = new TransactionSearchFilter();
        /*DateRange dateRange = new DateRange();
        Calendar fromDate = new GregorianCalendar(1999,10,10);
        fromDate.getTime();
        dateRange.setFromDate(fromDate);
        dateRange.setToDate(Calendar.getInstance());
        txSearchFilter.setPostDateRange(dateRange);
         */
        // Create TransactionSearchRequest
        TransactionSearchRequest txSearchRequest = new TransactionSearchRequest();
        txSearchRequest.setSearchFilter(txSearchFilter);
        txSearchRequest.setContainerType("all");
        txSearchRequest.setIncludeAggregatedTransactions(true);
        txSearchRequest.setIgnorePaymentTransactions(false) ;
        txSearchRequest.setResultRange(txSearchResultRange);
        txSearchRequest.setIgnoreUserInput(true);
        txSearchRequest.setSearchClients(TransactionSearchClients.DEFAULT_SERVICE_CLIENT);

        TransactionSearchExecInfo txSearchExecInfo;
		try {
			txSearchExecInfo = transactionSearchService.executeUserSearchRequest(userContext, txSearchRequest);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		}
        if (txSearchExecInfo != null ) {
        	displayTransactionSearchExecInfo(txSearchExecInfo);
        	viewTransactions(userContext, txSearchExecInfo);
        } else {
        	System.out.println("\nNo transactions found");
        }
    }


    public void viewTransactionsForItemAccount(UserContext userContext){

        // Display Item Accounts
        displayItemAccounts(userContext);

        // Prompt for the Item to be Searched.
        String itemId = IOUtils.promptInput(
        		ItemIdPrompt,
        		ReItemIdPrompt);

        // Prompt for the Item Account to be Searched.
        String itemAccountId = IOUtils.promptInput(
                ItemAccountIdPrompt,
                ReItemAcountIdPrompt);

        System.out.println("\nRetrieving transactions for ItemId="
        		+ itemId + ", ItemAccountId=" + itemAccountId + " ...");

        // The Transaction Search API will only calculate a running balance
        // if the search is in the bank container and the current balance
        // is fed to the search request.  This is a check to see if the
        // account is a bank account and to get back the current balance
        // of the bank account so the API can be properly populated.
        String containerName = null;
        Double runningBalance = null;
        List list = null;
        try {
        	ArrayOflong arrayOflong = new ArrayOflong();
        	arrayOflong.setElements(new Long[] {new Long(itemId)});
	        list = dataService.getItemSummaries3 (
    		userContext, arrayOflong);

	        /*list = dataService.getItemSummaries3 (
	        		userContext, new ArrayOflong(new Long[] {new Long(itemId)}));*/
        } catch (Exception e) {
        	System.out.println("Failed to get container: " + e);
        }
        if(list != null && list.getElements().length > 0) {
            for(int i=0; i<list.getElements().length; i++){
		        ItemSummary is = (ItemSummary)list.getElements(i);
		        containerName = is.getContentServiceInfo().getContainerInfo().getContainerName();
                List accounts = is.getItemData().getAccounts();
                if (accounts != null) {
                    if(containerName.equalsIgnoreCase(ContainerTypesHelper.BANK)
                    		&& accounts.getElements().length > 0) {
                        for (int accts = 0; accts < accounts.getElements().length; accts++) {
	                        BankData bankData = (BankData) accounts.getElements(accts);
                            long tempItemAccountId = bankData.getItemAccountId().longValue();
                            if(tempItemAccountId == Long.parseLong(itemAccountId)) {
                            	runningBalance = bankData.getCurrentBalance().getAmount();
                            }
                        }
                    }
            	}
            }
        }

        // End the check for container and running balance.

        long startRange = 1;
        long endRange = 200;

        // Create Results Range
        TransactionSearchResultRange txSearchResultRange = new TransactionSearchResultRange();
        txSearchResultRange.setStartNumber(startRange);
        txSearchResultRange.setEndNumber(endRange);


        // Create  TransactionSearchFilter
        TransactionSearchFilter txSearchFilter
                = new TransactionSearchFilter();
        ItemAccountId itemAccountIdObj = new ItemAccountId();
        itemAccountIdObj.setIdentifier(Long.parseLong(itemAccountId));
        txSearchFilter.setItemAccountId(itemAccountIdObj);
        txSearchFilter.setTransactionSplitType(TransactionSplitType.ALL_TRANSACTION);

        /*Calendar fromDate = Calendar.getInstance();
        fromDate.set(Calendar.YEAR, 2000);
        fromDate.set(Calendar.MONTH, Calendar.JANUARY);
        fromDate.set(Calendar.DATE, 10);
        Calendar toDate = Calendar.getInstance();
        DateRange dateRange = new DateRange();
        dateRange.setFromDate(fromDate);
        dateRange.setToDate(toDate);
        txSearchFilter.setPostDateRange(dateRange);*/

        // Create TransactionSearchRequest
        TransactionSearchRequest txSearchRequest = new TransactionSearchRequest();
        txSearchRequest.setSearchFilter(txSearchFilter);
        txSearchRequest.setContainerType("all");//ContainerTypesHelper.BANK
        txSearchRequest.setIgnorePaymentTransactions(false) ;
        txSearchRequest.setIncludeAggregatedTransactions(true);
        txSearchRequest.setResultRange(txSearchResultRange);
        txSearchRequest.setIgnoreUserInput(true);
        // To calculate a running balance requires setting the container
        //   restriction to only bank, setting a current balance, and
        //   turning on the flag for calculate transaction balance
        if(ContainerTypesHelper.BANK.equalsIgnoreCase(containerName)){
            txSearchRequest.setContainerType("bank");
            if(runningBalance != null) {
            	txSearchRequest.setCurrentBalance(runningBalance);
                txSearchRequest.setCalculateTransactionBalance(true);
            }
        }
        txSearchRequest.setSearchClients(TransactionSearchClients.DEFAULT_SERVICE_CLIENT);

        /*
          txSearchRequest.setUserInput(searchString);
          Date sysDate = new Date(System.currentTimeMillis());
          Date fromDate = new Date(System.currentTimeMillis());
          fromDate.setDate(1);
          DateRange dateRange = new DateRange();
          dateRange.setFromDate(fromDate);
          dateRange.setToDate(sysDate);
          searchFilter.setPostDateRange(dateRange);
          searchResultRange.setStartNumber(startRange);
          searchResultRange.setEndNumber(endRange);
          txSearchRequest.setResultRange(searchResultRange);
          */
        TransactionSearchExecInfo txSearchExecInfo;
		try {
			txSearchExecInfo = transactionSearchService.executeUserSearchRequest(userContext, txSearchRequest);
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
        if ( txSearchExecInfo != null) { // && txSearchExecInfo.getNumberOfHits() > 0
        	displayTransactionSearchExecInfo(txSearchExecInfo);
        	viewTransactions(userContext, txSearchExecInfo);
        } else {
        	System.out.println("\nNo transactions found");
        }

    }

	/**
	 * To use the TransactionSearchService pagination feature, 1) The param key
	 * "COM.YODLEE.CORE.CACHE_DATA_REPLICATION" should be enabled for the
	 * cobrand. 2) executeUserSearchRequest() API has to be run with a start and
	 * end range of 0-10.
	 * The executeUserSearchRequest API facilitate end user to
	 * initiate a transaction search. It remember the search result in a cache
	 * using a unique identifier which will be returned as part of the
	 * transaction result objectTransactionSearchExecInfo. A successful
	 * execution of this API will also return the first page result. To navigate
	 * furthur search result user have to call the getUserTransactions API with
	 * the unique identifier returned on the call to executeUserSearchRequest.
	 * When clearUserTransactions API is called with the valid unique
	 * identifier, it clears the transaction from cache. Any furthur usage of
	 * this identifier will throw InvalidSearchIdentifierException
	 *
	 * @param userContext
	 * @param txSearchExecInfo
	 */
    /*public void viewTransactionsWithPaginationLogic(UserContext userContext,
			TransactionSearchExecInfo txSearchExecInfo) {
		long startRange = 1;
		long endRange = 10;
		Long searchHits = txSearchExecInfo.getNumberOfHits();
		TransactionSearchIdentifier txSearchId = txSearchExecInfo
				.getSearchIdentifier();
		long searchHit = searchHits.longValue();
		int pageId = 1;
		while (searchHit > 0) {
			if (startRange >= searchHits.longValue()) {
				startRange = searchHits.longValue();
			}
			if (endRange >= searchHits.longValue()) {
				endRange = searchHits.longValue();
			}
			TransactionSearchResultRange txSearchResultRange = new TransactionSearchResultRange(
					startRange, endRange);
			System.out.println("\n#### Page " + pageId + " transaction "
					+ startRange + " - " + endRange + " ####\n");
			pageId++;
			TransactionSearchFetchRequest txFetchRequest = new TransactionSearchFetchRequest(
					txSearchId, txSearchResultRange);
			TransactionSearchResult txSearchResult = null;
			try {
				txSearchResult = transactionSearchService.getUserTransactions(
						userContext, txFetchRequest);
			} catch (InvalidUserContextExceptionFault e) {
				System.out
						.println("ERROR In getUserTransaction : Invalid User Context Excpetion\n");
				throw new RuntimeException(e);
			} catch (InvalidConversationCredentialsExceptionFault e) {
				System.out
						.println("ERROR In getUserTransaction : Invalid Conversation Credentials Exception \n");
				throw new RuntimeException(e);
			} catch (IllegalArgumentValueExceptionFault e) {
				System.out
						.println("ERROR In getUserTransaction : Illegal Argument Value Exception");
				throw new RuntimeException(e);
			} catch (Exception e) {
				System.out.println("ERROR In getUserTransaction : "
						+ e.getMessage());
				throw new RuntimeException(e);
			}
			if (txSearchResult != null) {
				ArrayOfTransactionView txView = txSearchResult
						.getTransactions();

				for (int i = 0; i < txView.getElements().length; ++i) {
					System.out.println(
                            "PostDate=" + Formatter.formatDate(txView.getElements(i).getPostDate(), Formatter.DATE_SHORT_FORMAT )+ " " +
                                    "TransDate=" + Formatter.formatDate(txView.getElements(i).getTransactionDate(), Formatter.DATE_SHORT_FORMAT )+ "\n " +
                                    "ItemAccountId=" +txView.getElements(i).getAccount().getItemAccountId() + "\n " +
                                    "TransactionId=" +txView.getElements(i).getViewKey().getTransactionId() + "\n " +
                                    "ContainerType=" +txView.getElements(i).getViewKey().getContainerType() + "\n " +
                                    "Desc=" +txView.getElements(i).getDescription().getDescription() + "\n " +
                                    "AccountName=" +txView.getElements(i).getAccount().getAccountName() +  "\n " +
                                    "Mem=" +txView.getElements(i).getMemo().getMemo() + " " +
                                    "CategoryName=" +txView.getElements(i).getCategory().getCategoryName() + "\n " +
                                    "Status=" +txView.getElements(i).getStatus().getDescription() + "\n " +
                                    "Price=" +Formatter.formatMoney(txView.getElements(i).getPrice()) + "\n " +
                                    "Quantity=" +txView.getElements(i).getQuantity() + "\n " +
                                    "CatKeyword=" +txView.getElements(i).getCategorizationKeyword() + "\n " +
                                    "RunningBalance=" +txView.getElements(i).getRunningBalance() + "\n " +
                                    "Amount=" +Formatter.formatMoney(txView.getElements(i).getAmount()) + " "
                    );
					System.out.println("\n");

					// list.add(txView[i]) ;
				}
				searchHit = searchHit - 10;
				startRange = endRange + 1;
				endRange = endRange + 10;
			} else {
				System.out.println("ERROR: Unable to get user transaction\n");
				break;
			}

			// return list ;
		}

		if (txSearchId != null) {
			try {
				transactionSearchService.clearUserTransactions(userContext, txSearchId);
			} catch (InvalidSearchIdentifierExceptionFault e) {
				System.out.println("ERROR while clearing user transactions: Invalid Search Identifier \n");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}*/

    public void viewTransactions(UserContext userContext, TransactionSearchExecInfo txSearchExecInfo){
        long startRange = 1;
        long endRange = 10;
        Long searchHits = txSearchExecInfo.getNumberOfHits();
        TransactionSearchIdentifier txSearchId = txSearchExecInfo.getSearchIdentifier();
        long searchHit = searchHits.longValue();
        int pageId = 1;

        TransactionSearchResult txSearchResult = txSearchExecInfo.getSearchResult();
        if (txSearchResult != null && txSearchResult.getTransactions().getElements().length > 0) {
        	TransactionView [ ] txView = txSearchResult.getTransactions().getElements();
        	while ( searchHit > 0 ) {
        		if (txView.length < 10 || startRange <= txView.length) {
        			System.out.println("\n#### Page "+ pageId + " transaction " + startRange + " - "+endRange+ " ####\n");
        		}
                pageId ++;
                for (int i = (int) (startRange - 1); i < (int) endRange && i < txView.length; i++) {
                	System.out.println(
                                    "PostDate=" + Formatter.formatDate(txView[i].getPostDate(), Formatter.DATE_SHORT_FORMAT )+ " " +
                                    "TransDate=" + Formatter.formatDate(txView[i].getTransactionDate(), Formatter.DATE_SHORT_FORMAT )+ "\n " +
                                    "ItemAccountId=" +txView[i].getAccount().getItemAccountId() + "\n " +
                                    "TransactionId=" +txView[i].getViewKey().getTransactionId() + "\n " +
                                    "ContainerType=" +txView[i].getViewKey().getContainerType() + "\n " +
                                    "Desc=" +txView[i].getDescription().getDescription() + "\n " +
                                    "AccountName=" +txView[i].getAccount().getAccountName() +  "\n " +
                                    "Mem=" +txView[i].getMemo().getMemo() + " " +
                                    "CategoryName=" +txView[i].getCategory().getCategoryName() + "\n " +
                                    "Status=" +txView[i].getStatus().getDescription() + "\n " +
                                    "merchant name=" +txView[i].getDescription().getMerchantName() + "\n " +
									"simple desc=" +txView[i].getDescription().getSimpleDescription()+ "\n " +
									"simple desc pref=" +txView[i].getDescription().getSimpleDescViewPref()+ "\n " +
									"trans type desc=" +txView[i].getDescription().getTransactionTypeDesc()+ "\n " +
                                    "user description=" +txView[i].getDescription().getUserDescription()+ "\n " +
                                    "Price=" +Formatter.formatMoney(txView[i].getPrice()) + "\n " +
                                    "Quantity=" +txView[i].getQuantity() + "\n " +
                                    "CatKeyword=" +txView[i].getCategorizationKeyword() + "\n " +
                                    "RunningBalance=" +txView[i].getRunningBalance() + "\n " +
                                    "Amount=" +Formatter.formatMoney(txView[i].getAmount()) + " "

                 );
                 System.out.println("\n");
                }
                searchHit = searchHit - 10;
                startRange = endRange + 1;
                endRange = endRange + 10;
            }
        } else {
            System.out.println("ERROR: Unable to get user transaction\n");
        }
    }

    /**
     * Display all item accounts
     *
     * @param userContext
     */
    public void displayItemAccounts(UserContext userContext){
    	List  list = null;
        try {
        	list = dataService.getItemSummaries(userContext);
        } catch (Exception e) {
        	throw new RuntimeException("Error fetching accounts...." + e.getMessage());
		}
        if(list != null){
            for(int i=0; i<list.getElements().length; i++){
                ItemSummary is = (ItemSummary)list.getElements(i);
                String containerName = is.getContentServiceInfo().getContainerInfo().getContainerName();

                if(is.isIsCustom() ) {
                    System.out.println( " (custom)");
                    //continue;
                }

                if(is != null){
                    if(is.getItemData() != null){
                        List accounts = is.getItemData().getAccounts();
                        if(accounts == null || accounts.getElements().length == 0){
                            // No Accounts
                        }else{
                            for (int accts = 0; accts < accounts.getElements().length; accts++){
                                String actName = "";
                                String nickname = null;
                                long itemAccountId=0;
                                long itemId = is.getItemId();
                                if(containerName.equalsIgnoreCase(ContainerTypesHelper.BANK)){
                                    BankData bankData = (BankData) accounts.getElements(accts);
                                    actName = getItemAccountName(bankData.getAccountName(),
                                            bankData.getAccountNumber()) ;
                                    nickname = bankData.getNickName();
                                    if(bankData.getItemAccountId() != null) {
                                        itemAccountId = bankData.getItemAccountId().longValue();
                                    }
                                } else if(containerName.equalsIgnoreCase(ContainerTypesHelper.CREDIT_CARD)){
                                    CardData cardData = (CardData) accounts.getElements(accts);
                                    actName = getItemAccountName(cardData.getAccountName(),
                                            cardData.getAccountNumber()) ;
                                    nickname = cardData.getNickName();
                                    if(cardData.getItemAccountId() != null) {
                                        itemAccountId = cardData.getItemAccountId().longValue();
                                    }
                                } else if(containerName.equalsIgnoreCase(ContainerTypesHelper.INVESTMENT)){
                                    InvestmentData investmentData = (InvestmentData) accounts.getElements(accts);
                                    actName = getItemAccountName(investmentData.getAccountName(),
                                            investmentData.getAccountNumber()) ;
                                    nickname = investmentData.getNickName();
                                    if(investmentData.getItemAccountId() != null) {
                                        itemAccountId = investmentData.getItemAccountId().longValue();
                                    }

                                } else if(containerName.equalsIgnoreCase(ContainerTypesHelper.REWARD_PROGRAM)){
                                    RewardPgm rewardData = (RewardPgm) accounts.getElements(accts);
                                    actName = getItemAccountName(null,
                                            rewardData.getAccountNumber()) ;
                                    nickname = rewardData.getNickName();
                                    if(rewardData.getItemAccountId() != null) {
                                        itemAccountId = rewardData.getItemAccountId().longValue();
                                    }


                                } else if(containerName.equalsIgnoreCase(ContainerTypesHelper.LOAN) || containerName.equalsIgnoreCase(ContainerTypesHelper.MORTGAGE)){
                                    LoanLoginAccountData loanLoginAccountData = (LoanLoginAccountData) accounts.getElements(accts);
                                    if(loanLoginAccountData != null){
                                        List loans = loanLoginAccountData.getLoans();
                                        if(loans == null || loans.getElements().length == 0 ){
                                            // System.out.println("\tNo Mortgage Accounts");
                                        }else{
                                            for (int l = 0; l < loans.getElements().length; l++){
                                                Loan loan = (Loan) loans.getElements(l);
                                                actName = getItemAccountName(loan.getAccountName(),
                                                        loan.getAccountNumber()) ;
                                                nickname = loan.getNickName();
                                                if(loan.getItemAccountId() != null) {
                                                    itemAccountId = loan.getItemAccountId().longValue();
                                                }
                                            }
                                        }
                                    }
                                } else if(containerName.equalsIgnoreCase(ContainerTypesHelper.INSURANCE)){
                                    InsuranceLoginAccountData insuranceLoginAccountData = (InsuranceLoginAccountData) accounts.getElements(accts);
                                    if(insuranceLoginAccountData != null){
                                        List insurancePolicyList = insuranceLoginAccountData.getInsurancePolicys();
                                        if (null != insurancePolicyList){
                                            for (int ins = 0; ins < insurancePolicyList.getElements().length; ins++){
                                                InsuranceData insuranceData = (InsuranceData) insurancePolicyList.getElements(ins);
                                                actName = getItemAccountName(insuranceData.getAccountName(),
                                                        insuranceData.getAccountNumber()) ;
                                                nickname = insuranceData.getNickName();
                                                if(insuranceData.getItemAccountId() != null) {
                                                    itemAccountId = insuranceData.getItemAccountId().longValue();
                                                }

                                            }
                                        }

                                    }
                                } else if(containerName.equalsIgnoreCase(ContainerTypesHelper.BILL) ||
                                        containerName.equalsIgnoreCase(ContainerTypesHelper.MINUTES) ||
                                        containerName.equalsIgnoreCase(ContainerTypesHelper.TELEPHONE)){
                                    BillsData billsData = (BillsData) accounts.getElements(accts);
                                    actName = getItemAccountName(billsData.getAccountName(),
                                            billsData.getAccountNumber()) ;
                                    nickname = billsData.getNickName();
                                    if(billsData.getItemAccountId() != null) {
                                        itemAccountId = billsData.getItemAccountId().longValue();
                                    }
                                } else if(containerName.equalsIgnoreCase(ContainerTypesHelper.BILL_PAY_SERVICE)){
                                    BillPayServiceData billPayData = (BillPayServiceData) accounts.getElements(accts);
                                    actName = getItemAccountName(billPayData.getAccountName(),
                                            billPayData.getAccountNumber()) ;
                                    nickname = billPayData.getNickName();
                                    if(billPayData.getItemAccountId() != null) {
                                        itemAccountId = billPayData.getItemAccountId().longValue();
                                    }
                                } else if(containerName.equalsIgnoreCase(ContainerTypesHelper.MAIL)){
                                    MailData mailData = (MailData) accounts.getElements(accts);
                                    actName = "";
                                    nickname = mailData.getCustomName();
                                    // Mail has no ItemAccountId, using ItemId
                                    itemAccountId=is.getItemId();
                                } else {
                                    continue;
                                }

                                // Get Display Name
                                String displayName = getItemAccountDisplayName(
                                        actName,
                                        is.getContentServiceInfo().getContentServiceDisplayName(),
                                        nickname) ;

                                System.out.println(displayName + " itemId=" + itemId
                                		+ " itemAccountId=" + itemAccountId);
                            }
                        }
                    }
                }
            }
            System.out.println("\n");
        } else {
        	System.out.println("No accounts found...\n");
        }
    }


    /**
     * Get Item Account Display Name.  Used for 7.x and greater
     * @param actName
     * @param siteDisplayName
     * @param nickName
     * @return display name
     */
    public static String getItemAccountDisplayName(String actName,
                                                   String siteDisplayName,
                                                   String nickName
    ){
        String displayName = siteDisplayName + "" + actName;

        if(nickName != null){
            displayName = nickName;
        }

        return  displayName;
    }

    private static String getItemAccountName(String actName, String acountNumber){
        if(actName == null){
            actName=acountNumber;
        }
        if(actName != null){
            actName = " - " + actName ;
        }
        else{
            actName = "";
        }
        return actName;
    }



    public static void main (String args[])
    {
        if (args.length < 2) {
            throw new RuntimeException ("Usage: <username> <password>") ;
        }

        String  userName   =   args[0] ;
        String  password   =   args[1] ;

        LoginUser loginUser = new LoginUser ();
        Transactions transactions = new Transactions ();

        UserContext userContext;
        System.out.println("Logging in user " + userName + " with password "
			+ password);
		userContext = loginUser.loginUser(userName, password);
		System.out.println("Login of user " + userName + " successful");

        //transactions.search(userContext, "");

        transactions.doMenu(userContext);

        // Log out the user
        System.out.println ("Logging out " + userName);
        loginUser.logoutUser (userContext);
        System.out.println ("Done logging out " + userName);
    }

    /*Modifies the Sub category Name */
    public void updateSubCategory(UserContext userContext){
    	System.out.println("\nFetching all transaction categories....");
    	ArrayOfCategory  category = null;
    	try {
        	category = tcService.getUserCategoriesAtLevel(userContext,4l);
        } catch (Exception e) {
			throw new RuntimeException("Error fetching Transaction Categories..." + e.getMessage());
		}
        if (category != null)
        {
            HashMap<Long, Category> subCategoryMap = new HashMap<Long, Category>();
            for (int i = 0; i < category.getElements().length; ++i)
            {
                System.out.println(category.getElements(i).getCategoryName() + " [" + category.getElements(i).getCategoryId() + "]");
                subCategoryMap.put(category.getElements(i).getCategoryId(), category.getElements(i));
            }
            System.out.println("\nEnter Sub-CategoryId which is to be Modified");
            long categoryId = IOUtils.readLong();
            Category subCategory = (Category)subCategoryMap.get(categoryId);
            if (subCategory == null)
            {
                System.out.println("\n**Cannot update sub-category\n");
            }
            else
            {
            	System.out.println("Enter new Category Name");
            	String newCategoryName = IOUtils.readStr();
            	Category categoryModified = new Category();
            	categoryModified.setCategoryId(subCategory.getCategoryId());
            	categoryModified.setParentCategoryId(subCategory.getParentCategoryId());
            	categoryModified.setCategoryName(newCategoryName);
            	categoryModified.setCategoryLevelId(4l);
            	Category[] toBeModified = { categoryModified };
            	ArrayOfCategory subCategoryToBeModified = new ArrayOfCategory();
                subCategoryToBeModified.setElements(toBeModified);
                try {
                tcService.manageUserCategories(userContext,subCategoryToBeModified);
                System.out.println("Sub category " + subCategory.getCategoryName() +" has been Modified");
                } catch (Exception e) {
                	e.printStackTrace();
				}
            }
        }
    }

    /*
     * Deletes a sub category
     */
    public void deleteSubCategory(UserContext context){
    	System.out.println("\nFetching all transaction categories....");
    	ArrayOfCategory  category = null;
    	try {
        	category = tcService.getUserCategoriesAtLevel(context,4l);
        } catch (Exception e) {
			throw new RuntimeException("Error fetching Transaction Categories..." + e.getMessage());
		}
        if (category != null)
        {
            HashMap<Long, Category> subCategoryMap = new HashMap<Long, Category>();
            for (int i = 0; i < category.getElements().length; ++i)
            {
                System.out.println(category.getElements(i).getCategoryName() + " [" + category.getElements(i).getCategoryId() + "]");
                subCategoryMap.put(category.getElements(i).getCategoryId(), category.getElements(i));
            }
            System.out.println("\nEnter Sub-CategoryId which is to be deleted");
            long categoryId = IOUtils.readLong();
            Category subCategory = (Category)subCategoryMap.get(categoryId);
            if (subCategory == null)
            {
                System.out.println("\n**Cannot delete sub-category\n");
            }
            else
            {
            	Category categoryDeleted = new Category();
            	categoryDeleted.setCategoryId(subCategory.getCategoryId());
            	categoryDeleted.setCategoryName(null);
            	categoryDeleted.setCategoryLevelId(4l);
            	categoryDeleted.setIsDeleted(1l);
            	Category[] categoriesToBeDeleted = { categoryDeleted };
            	ArrayOfCategory subCategoryToBeDeleted = new ArrayOfCategory();
                subCategoryToBeDeleted.setElements(categoriesToBeDeleted);
                try {
                tcService.manageUserCategories(context,subCategoryToBeDeleted);
                System.out.println("Sub category " + subCategory.getCategoryName() +" has been deleted");
                } catch (Exception e) {
                	e.printStackTrace();
				}
            }
        }
    }

    /*
     * Add a sub category
     */
    public void addSubCategory(UserContext userContext){
    	 // Get the list of supported transaction categories to display
        ArrayOfCategory  category = null;
    	try {
        	category = tcService.getUserTransactionCategories(userContext);
        } catch (Exception e) {
			throw new RuntimeException("Error fetching Transaction Categories..." + e.getMessage());
		}

        if(category != null){
        	System.out.print("\n");
        	System.out.println("category\t\t\tcategoryId");
        	System.out.println("--------\t\t\t----------");
            for ( int i = 0; i < category.getElements().length; ++i) {
            	System.out.print(category.getElements(i).getCategoryName());
            	for ( int j = category.getElements(i).getCategoryName().length(); j < 32; j++) {
            		System.out.print(".");
            	}
            	System.out.print("   " + category.getElements(i).getCategoryId());
            	System.out.print("\n");
            	ArrayOfCategory  categoryChild = null;
            	categoryChild = category.getElements(i).getChildCategory();
            	if(categoryChild != null && categoryChild.getElements() != null && categoryChild.getElements().length > 0){
            		for ( int j = 0; j < categoryChild.getElements().length; ++j) {
            			System.out.println("\t"+categoryChild.getElements(j).getCategoryName() + "....."
            					+ categoryChild.getElements(j).getCategoryId());
            	}
            	}

            }
            System.out.print("\n");
        }


        // Prompt for the categoryId.
        String categoryId = IOUtils.promptInput(
                CategoryIdPrompt,
                ReCategoryIdPrompt);

        System.out.print("\n");
        Category subCategory = new Category();
        subCategory.setParentCategoryId(new Long(categoryId));

        String subCategoryName = IOUtils.promptInput(
                CategoryNamePrompt,
                ReCategoryNamePrompt);
        subCategory.setCategoryName(subCategoryName);
        subCategory.setCategoryLevelId(4l);
        subCategory.setCategoryDescription("TEST");
        Category[] subCategoryToBeAdded = { subCategory };
        ArrayOfCategory subCategoryToBeAdded1 = new ArrayOfCategory();
        subCategoryToBeAdded1.setElements(subCategoryToBeAdded);

        try {
			tcService.manageUserCategories(userContext, subCategoryToBeAdded1);
		} catch (Exception exception) {
				exception.printStackTrace();
		}

    }

    public void categorizeTransaction(UserContext userContext){
        // Prompt for the TransactionId of transaction to be categorized.
        String transactionId = IOUtils.promptInput(
                TransactionIdPrompt,
                ReItemAcountIdPrompt);

        // Prompt for the container of the transaction to be categorized.
        String containerType = IOUtils.promptInput(
                ContainerTypePrompt,
                ReContainerTypePrompt);

        // Get the list of supported transaction categories to display
        ArrayOfCategory  category = null;
        try {
        	category = tcService.getSupportedTransactionCategrories(getCobrandContext());
        } catch (Exception e) {
			throw new RuntimeException("Error fetching Transaction Categories..." + e.getMessage());
		}

        if(category != null){
        	System.out.print("\n");
        	System.out.println("category\t\t\tcategoryId");
        	System.out.println("--------\t\t\t----------");
            for ( int i = 0; i < category.getElements().length; ++i) {
            	System.out.print(category.getElements(i).getCategoryName());
            	for ( int j = category.getElements(i).getCategoryName().length(); j < 32; j++) {
            		System.out.print(".");
            	}
            	System.out.print("   " + category.getElements(i).getCategoryId());
            	System.out.print("\n");
            }
            System.out.print("\n");
        }


        // Prompt for the categoryId.
        String categoryId = IOUtils.promptInput(
                CategoryIdPrompt,
                ReCategoryIdPrompt);

        System.out.print("\n");

        UserCategorizationObject [] userCategorizationObjects = new UserCategorizationObject[1];

        UserCategorizationObject ucObject = new UserCategorizationObject();

        ucObject.setContainerTransactionId(transactionId);
        ucObject.setTargetTransactionCategoryId(categoryId);
        ucObject.setContainer(containerType);
        userCategorizationObjects[0] = ucObject;

        // Call TransactionCategorizationService to categorize the transaction
        try {
        	ArrayOfUserCategorizationObject arrayOfUserCategorizationObject = new ArrayOfUserCategorizationObject();
        	arrayOfUserCategorizationObject.setElements(userCategorizationObjects);
        	tcService.categorizeTransactions(userContext, arrayOfUserCategorizationObject);

            System.out.println("CategoryId updated for transactionId=" + transactionId + ".\n");
        } catch (InvalidUserCategorizationObjectExceptionFault ex) {
            System.out.println ("\nInvalidUserCategorizationObjectException -- One of these arguments in invalid: " +
            		"\n\tTransactionID=" + transactionId +
            		"\n\tContainerType=" + containerType +
                    "\n\tCategoryId="  + categoryId + "\n");
        } catch (Exception e) {
			System.out.println("Error categorizing the transaction...." + e.getMessage());
		}
    }

}
