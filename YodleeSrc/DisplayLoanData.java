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
import java.util.Calendar;
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
import com.yodlee.soap.core.dataservice.types.Bill;
import com.yodlee.soap.core.dataservice.types.Loan;
import com.yodlee.soap.core.dataservice.types.LoanLoginAccountData;
import com.yodlee.soap.core.dataservice.types.LoanPaymentDue;
import com.yodlee.soap.core.dataservice.types.LoanPayoff;

/**
 * Displays a user's Loan item data in the Yodlee software platform. 
 */
public class DisplayLoanData extends ApplicationSuper {

	protected DataService dataService;
	
	/**
     * Constructs an instance of the DisplayLoanData class that
     * displays Loans accounts
     */
    public DisplayLoanData ()
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

     * Displays all the item summaries of loan items of the user.

     * <p>

     * @param userContext The user context.

     */

    public void displayLoanData (UserContext userContext, boolean isHistoryNeeded)

    {
        /*SummaryRequest sr = new SummaryRequest(
		new String[] { ContainerTypes.LOAN },

		new DataExtent[] { DataExtent.getDataExtentForAllLevels() }

		);*/
    	SummaryRequest sr = new SummaryRequest();
    	List list = new List();
    	list.setElements(new String[] {ContainerTypesHelper.LOAN });
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

			System.out.println("No loan data available");

			return;

		}

		for (int i = 0; i < itemSummaries.length; i++) {

			ItemSummary is = (ItemSummary) itemSummaries[i];

			displayLoanDataForItem(is);

			// Dump LoanLoginAccountData

			// dumpLoanDataForItem(is);

		}

    }





    /**

     * Dump the LoanLoginAccountData object.

     * @param is

     */

    public void dumpLoanDataForItem( ItemSummary is){



        ItemData id = is.getItemData();



        if(id == null){

            System.out.println("\tItemData == null");

        }else{
            List accountsList = id.getAccounts();
            Object[] accounts = null;
            if (accountsList != null) {
            	accounts = accountsList.getElements();
            }
            

            if (accounts == null || accounts.length == 0) {

                System.out.println ("\tNo accounts");

            }else {

                for (int accts = 0; accts < accounts.length; accts++) {

					LoanLoginAccountData

					loanLoginAccountData = (LoanLoginAccountData) accounts[accts];



                    try{

                        DataDumper.dumper(loanLoginAccountData);

                    }catch(Exception e ){

                        e.printStackTrace();

                    }

                }

            }

        }

    }





    /**

     * Displays the item information and item data information

     * for the given loan itemSummary.

     * <p>

     * @param is an itemSummary whose containerType is 'loan'

     */

    public void displayLoanDataForItem (ItemSummary is)

    {

        String containerType = is.getContentServiceInfo ().

                getContainerInfo ().getContainerName ();

        //System.out.println("containerType = " + containerType );



        if (!containerType.equals("loans")) {

            throw new RuntimeException ("displayLoanDataForItem called with " +

                    "invalid container type: " + containerType);

        }



        DisplayItemInfo displayItemInfo = new DisplayItemInfo ();

        System.out.println("DisplayItemInfo:");

        displayItemInfo.displayItemSummaryInfo (is);



        ItemData id = is.getItemData();



        if(id == null){

            System.out.println("ItemData == null");

        }else {



            // Get Accounts

        	 List accountsList = id.getAccounts();
             Object[] accounts = null;
             if (accountsList != null) {
             	accounts = accountsList.getElements();
             }

            if (accounts == null || accounts.length == 0) {

                System.out.println ("\tNo accounts");

            }else {

                for (int acts = 0; acts < accounts.length; acts++) {

					LoanLoginAccountData

					loanLoginAccountData = (LoanLoginAccountData) accounts[acts];





                    System.out.println("\t**LoanLoginAccountData**");

                    System.out.println("\tLoan Number: "+

                            loanLoginAccountData.getLoanAccountNumber() );





                    // Loan
                    List LoansList = loanLoginAccountData.getLoans();
                    Object[] loans = null;
                    
                    if (LoansList != null){
                    	loans = LoansList.getElements();
                    }

                    if(loans == null || loans.length == 0 ){

                        System.out.println("\tNo Loans Accounts");

                    }else{

                        System.out.println("\t\t**Loan**");

                        for (int l = 0; l < loans.length; l++) {

							Loan loan = (Loan) loans[l];

                            System.out.println("\t\tLoan Account Name: " + loan.getAccountName() );

                            System.out.println("\t\tLoan Account Number: " + loan.getAccountNumber() );

                            System.out.println("\t\tLoan Interest Rate: " + loan.getInterestRate() );

                            long lu = loan.getLastUpdated().longValue() * 1000;

                            Date date = new Date(lu);

                            System.out.println ("\t\tLast Updated: " + date.toString() );



                            // LoanPayOffs
                            List loanPayoffsList = loan.getLoanPayOffs();
                            Object[] loanPayoffs = null;
                            if (loanPayoffsList != null){
                            	loanPayoffs = loanPayoffsList.getElements();
                            }

                            if(loanPayoffs == null || loans.length == 0 ){

                                System.out.println("\t\tNo LoanPayoffs");

                            }else{

                                System.out.println("\t\t\tLoanPayoffs**");

                                for (int payOffs = 0; payOffs < loanPayoffs.length; payOffs++) {

									LoanPayoff loanPayoff = (LoanPayoff) loanPayoffs[payOffs];

                                    System.out
											.println("\t\t\tLoan Payoff Amount: "
													+ (loanPayoff
															.getPayoffAmount() != null ? loanPayoff
															.getPayoffAmount()
															.getAmount()
															: 0.0));
                                    
                                    System.out
											.println("\t\t\tLoan Pay By Date: "
													+ Formatter
															.formatDate(
																	loanPayoff
																			.getPayByDate()
																			.getDate(),
																	Formatter.DATE_SHORT_FORMAT));

                                }

                            }





                            // LoanPayMentDues
                            List loanPaymentDuesList = loan.getLoanPaymentDues();
                            Object[] loanPaymentDues = null;
                            if (loanPaymentDuesList != null){
                            	loanPaymentDues = loanPaymentDuesList.getElements();
                            }

                            if(loanPaymentDues == null || loanPaymentDues.length == 0 ){

                                System.out.println("\t\tNo LoanPaymentDues");

                            }else{

                                System.out.println("\t\tLoanPaymentDues**");

                                for (int pmtDues = 0; pmtDues < loanPaymentDues.length; pmtDues++) {

									LoanPaymentDue lpd = (LoanPaymentDue) loanPaymentDues[pmtDues];

                                    System.out
											.println("\t\tLoanPaymentDue Interest Amount:"
													+ (lpd.getInterestAmount() != null ? lpd
															.getInterestAmount()
															.getAmount()
															: 0.0));



                                    // Bill

                                    Bill bill = lpd.getBill();

                                    if( bill == null ){

                                        System.out.println("\t\t\tNo Bill");

                                    }else{                                    	

                                        System.out
												.println("\t\t\tBill Due Date: "
														+ Formatter
																.formatDate(
																		bill
																				.getDueDate()
																				.getDate(),
																		Formatter.DATE_SHORT_FORMAT));

                                        System.out
												.println("\t\t\tBill Min Payment: "
														+ (bill.getMinPayment() != null ? bill
																.getMinPayment()
																.getAmount()
																: 0.0));

                                    }

                                }

                            }







                            // Get LoanBorrowers



                            System.out.println("");



                        }

                    }

                }

            }





            // Account History
            List acctHistoryList = id.getAccountHistory();
            Object[] acctHistory = null;
            if (acctHistoryList != null) {
            	acctHistory = acctHistoryList.getElements();
            }

            if( acctHistory == null || acctHistory.length == 0 ){

                System.out.println("\tNo Account History");

            }else{                

                System.out.println("\n\t**Account History**");

                for (int acctsHist = 0; acctsHist < acctHistory.length; acctsHist++) {

					AccountHistory acctHist = (AccountHistory) acctHistory[acctsHist];



                    System.out.println("\tAccount Id : " +acctHist.getAccountId() );


                    List historiesList = acctHist.getHistory();
                    Object[] histories = null;
                    if(historiesList != null){
                    	histories = historiesList.getElements();
                    }

                    if(histories == null | histories.length == 0 ){

                        System.out.println("\t\tNo History");

                    }else{

                        System.out.println("\t\t**History**");

                       for (int hist = 0; hist < histories.length; hist++) {

							LoanLoginAccountData loanLoginAccountData =

							(LoanLoginAccountData) histories[hist];


                            System.out.println("\t\t**LoanLoginAccountData**");

                            System.out.println("\t\tLoan Number: "+

                                    loanLoginAccountData.getLoanAccountNumber() );





                            // Loan
                            List loansList = loanLoginAccountData.getLoans();
                            
                            Object[] loans = null;
                            
                            if (loansList != null){
                            	loans = loansList.getElements();
                            }

                            if(loans == null || loans.length == 0 ){

                                System.out.println("\t\tNo Loans Accounts");

                            }else{

                                System.out.println("\t\t\t**Loan**");

                               for (int l = 0; l < loans.length; l++) {

									Loan loan = (Loan) loans[l];

                                    System.out.println("\t\t\tLoan Account Name: " + loan.getAccountName() );

                                    System.out.println("\t\t\tLoan Account Number: " + loan.getAccountNumber() );

                                    System.out.println("\t\t\tLoan Interest Rate: " + loan.getInterestRate() );

                                    long lu = loan.getLastUpdated().longValue() * 1000;

                                    Date date = new Date(lu);

                                    System.out.println ("\t\t\tLast Updated: " + date.toString() );

                                    System.out.println("");



                                }

                            }



                        }

                    }

                }

            }



        }

    }



    /**

     * Displays a user's Loan item data in the Yodlee software platform.

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

        DisplayLoanData loanData = new DisplayLoanData ();

        UserContext userContext = null;



        // Login the user
		System.out.println("Logging in user " + userName +

		" with password " + password);

		userContext = loginUser.loginUser(userName, password);

		System.out.println("Login of user " + userName +

		" successful");     

        // Display Loan Account

        loanData.displayLoanData(userContext, true);



        // Log out the user

        System.out.println ("Logging out " + userName);

        loginUser.logoutUser (userContext);

        System.out.println ("Done logging out " + userName);

    }

}
