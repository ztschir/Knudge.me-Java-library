/*
 * Copyright 2009 Yodlee, Inc.  All Rights Reserved.  Your use of this code 
 * requires a license from Yodlee.  Any such license to this code is 
 * restricted to evaluation/illustrative purposes only. It is not intended 
 * for use in a production environment, and Yodlee disclaims all warranties 
 * and/or support obligations concerning this code, regardless of the terms 
 * of any other agreements between Yodlee and you.
 */
package com.yodlee.sampleapps;
import javax.xml.rpc.ServiceException;

import com.yodlee.soap.core.IllegalArgumentValueExceptionFault;
import com.yodlee.soap.common.UserContext;
import com.yodlee.soap.core.login.login.LoginServiceLocator;
import com.yodlee.soap.core.paymentservice.AutoPaySetup;
import com.yodlee.soap.core.paymentservice.autopaysetupservice.AutoPaySetupService;
import com.yodlee.soap.core.paymentservice.autopaysetupservice.AutoPaySetupServiceServiceLocator;
import com.yodlee.soap.core.paymentservice.BankPaymentProfile;
import com.yodlee.soap.core.paymentservice.CardNumber;
import com.yodlee.soap.core.paymentservice.CardPaymentAccount;
import com.yodlee.soap.core.paymentservice.Payee;
import com.yodlee.soap.core.paymentservice.PaymentAccountAlreadyExistsExceptionFault;
import com.yodlee.soap.core.paymentservice.PayeeNicknameAlreadyInUseExceptionFault;
import com.yodlee.soap.core.paymentservice.PaymentProfile;
import com.yodlee.soap.core.paymentservice.CardPaymentProfile;
import com.yodlee.soap.ext.traversal.contentservicetraversal.ContentServiceTraversalServiceLocator;
import com.yodlee.sampleapps.helper.BillPayConstants;
import com.yodlee.sampleapps.helper.IOUtils;
import com.yodlee.soap.collections.core.paymentservice.ArrayOfAutoPaySetup;
import com.yodlee.soap.collections.core.paymentservice.ArrayOfPaymentProfile;
/**
 * @author pvalluru
 *
 */
public class BillPayManagement extends ApplicationSuper{
	
    /** Navigation Counter. **/
    private static int optionCount = 1;
    /** Navigation Menu Choice. **/
    private static final int NAV_DIRECTPAY = optionCount++;
    /** Navigation Menu Choice. **/
    //private static final int NAV_PAYTODAY = optionCount++;
    /** Navigation Menu Choice. **/
    private static final int NAV_PAYITALL = optionCount++;
    /** Navigation Menu Choice. **/
    private static final int NAV_QUIT = 0;
    
    private static int paymentOptionCount = 1;
    
    
    private static final int NAV_VIEW_CARD_ACCOUNT = paymentOptionCount++;
    /** Navigation Menu Choice. **/
    private static final int NAV_EDIT_CARD_ACCOUNT = paymentOptionCount++;
    /** Navigation Menu Choice. **/
    private static final int NAV_DELETE_CARD_ACCOUNT = paymentOptionCount++;
    /** Navigation Menu Choice. **/
    private static final int NAV_ADD_CARD_ACCOUNT = paymentOptionCount++;
    /** Navigation Menu Choice. **/
    private static final int NAV_ADD_DIRECTPAY_PAYEE = paymentOptionCount++;
    /** Navigation Menu Choice. **/    
    private static final int NAV_VIEW_PAYEES = paymentOptionCount++;
    /** Navigation Menu Choice. **/
    private static final int NAV_POST_PAYMENT = paymentOptionCount++;
    /** Navigation Menu Choice. **/
    private static final int NAV_VIEW_PAYMENTS = paymentOptionCount++;
    /** Navigation Menu Choice. **/
    private static final int NAV_STOP_PAYMENT = paymentOptionCount++;
    /** Navigation Menu Choice. **/
    private static final int NAV_SETUP_AUTOPAY = paymentOptionCount++;
    /** Navigation Menu Choice. **/
    private static final int NAV_VIEW_AUTOPAY = paymentOptionCount++;
    
    
    private static int payItAllOptionCount = 1;
    /** Navigation Menu Choice. **/
    private static final int  NAV_PIA_ADD_PAYM_ACCOUNT = payItAllOptionCount++;
    /** Navigation Menu Choice. **/
    private static final int  NAV_PIA_VIEW_ACTIVE_PAYM_ACCOUNTS = payItAllOptionCount++;
    /** Navigation Menu Choice. **/
    private static final int  NAV_PIA_VIEW_ALL_PAYM_ACCOUNTS = payItAllOptionCount++;
    /** Navigation Menu Choice. **/
    private static final int  NAV_PIA_EDIT_PAYM_ACCOUNT = payItAllOptionCount++;
    /** Navigation Menu Choice. **/
    private static final int  NAV_PIA_UPDATE_PAYM_ACCOUNT_STATUS = payItAllOptionCount++;
    /** Navigation Menu Choice. **/
    private static final int  NAV_PIA_REMOVE_PAYM_ACCOUNT = payItAllOptionCount++;
    /** Navigation Menu Choice. **/
    private static final int  NAV_PIA_VERIFY_CD_AMOUNT = payItAllOptionCount++;
    /** Navigation Menu Choice. **/
    private static final int  NAV_PIA_MODIFY_PREFERRED_PAYM_ACCOUNT = payItAllOptionCount++;
    /** Navigation Menu Choice. **/
    private static final int  NAV_PIA_ADD_PAYEE = payItAllOptionCount++;
    /** Navigation Menu Choice. **/
    private static final int  NAV_PIA_ADD_PAY_TODAY_PAYEE = payItAllOptionCount++;
    /** Navigation Menu Choice. **/
    private static final int  NAV_PIA_ADD_CUSTOM_PAYEE = payItAllOptionCount++;
    /** Navigation Menu Choice. **/
    private static final int  NAV_PIA_UPDATE_PAYEE = payItAllOptionCount++;
    /** Navigation Menu Choice. **/
    private static final int  NAV_PIA_REMOVE_PAYEE = payItAllOptionCount++;
    /** Navigation Menu Choice. **/
    private static final int  NAV_PIA_VIEW_ALL_PAYEES = payItAllOptionCount++;
    /** Navigation Menu Choice. **/
    private static final int  NAV_CREATE_PAYTODAY_PAYMENT = payItAllOptionCount++;
    /** Navigation Menu Choice. **/
    private static final int  NAV_VIEW_ALL_PAY_TODAY_PAYMENTS = payItAllOptionCount++;  
        /** Navigation Menu Choice. **/
    private static final int  NAV_CREATE_PAYITALL_PAYMENT = payItAllOptionCount++;
    /** Navigation Menu Choice. **/
    private static final int  NAV_PIA_VIEW_ALL_PAYMENTS = payItAllOptionCount++;  
    /** Navigation Menu Choice. **/
    private static final int  NAV_MODIFY_SCHEDULED_PAYMENT = payItAllOptionCount++;  
    /** Navigation Menu Choice. **/
    private static final int  NAV_MODIFY_RECURRING_PAYMENT = payItAllOptionCount++;  
    /** Navigation Menu Choice. **/
    private static final int  NAV_CANCEL_SCHEDULED_PAYMENT = payItAllOptionCount++;  
    /** Navigation Menu Choice. **/
    private static final int  NAV_CANCEL_RECURRING_PAYMENT = payItAllOptionCount++;  
    /** Navigation Menu Choice. **/
    private static final int  NAV_STOP_CHECK_PAYMENT = payItAllOptionCount++;  
    /** Navigation Menu Choice. **/
    private static final int  NAV_CANCEL_STOP_PAYMENT = payItAllOptionCount++;  
    /** Navigation Menu Choice. **/
    private static final int  NAV_CALCULATE_EARLIEST_PAYM_DATE = payItAllOptionCount++;  
    /** Navigation Menu Choice. **/
    private static final int  NAV_CALCULATE_EARLIEST_PROCESS_DATE = payItAllOptionCount++;  
    /** Navigation Menu Choice. **/
    private static final int  NAV_CALCULATE_EARLIEST_DELIVERON_DATE = payItAllOptionCount++;      
    /** Navigation Menu Choice. **/
    private static final int  NAV_LIST_AVAIL_BILLER_CAT = payItAllOptionCount++;
    /** Navigation Menu Choice. **/
    private static final int  NAV_LIST_CSI_FOR_SERVICE_ID = payItAllOptionCount++;
    /** Navigation Menu Choice. **/
    private static final int  NAV_SEARCH_BILLER_SERVICES = payItAllOptionCount++;
    /** Navigation Menu Choice. **/
    private static final int  NAV_SEARCH_BILLER_SERVICES_BY_CATEGORY = payItAllOptionCount++;
    
    /** Navigation Menu Choice. **/
    private static final int  NAV_PIA_VIEW_ALL_EXPEDITED_ENABLED_SERVICES= payItAllOptionCount++;
  

    
    /** Proxy for the AutoPaySetupService interface. */
    protected AutoPaySetupService autoPaySetupService;
    
    public BillPayManagement(){
    	super();
        AutoPaySetupServiceServiceLocator autoPayServiceLocator= new AutoPaySetupServiceServiceLocator();
        String serviceName= autoPayServiceLocator.getAutoPaySetupServiceWSDDServiceName();
        autoPayServiceLocator.setAutoPaySetupServiceEndpointAddress(System.getProperty("com.yodlee.soap.services.url")
				+ "/" + serviceName);
        try {
        	autoPaySetupService = autoPayServiceLocator.getAutoPaySetupService();

		} catch (Exception lse) {

		}

    }
    
    public void billPayOptionMenu(UserContext userContext) {
		boolean loop = true;
		int choice = 0;
		while (loop) {
	       	try {    	
		        System.out.println("********************");
		        System.out.println(NAV_DIRECTPAY + ". Direct Pay");
		        //System.out.println(NAV_PAYTODAY + ". Pay Today");
		        System.out.println(NAV_PAYITALL + ". Pay It All");
		        System.out.println(NAV_QUIT + ". Exit Sub-menu");
		        System.out.println("********************");
		        System.out.print("Choice: " );
		
		        choice = IOUtils.readInt();
		        System.out.println();
		        if (choice == NAV_DIRECTPAY) {
		        	directPayOptionMenu(userContext);
		        } //else if (choice == NAV_PAYTODAY) {
		        	//payTodayOptionMenu(userContext);
		        else if (choice == NAV_PAYITALL) {
		        	payItAllOptionMenu(userContext);
		        } else if (choice == NAV_QUIT)
		            loop = false;
		        else 
					 System.out.println("Invalid Entry!");
	       	}catch (Exception e) {
	            e.printStackTrace();
	        }
		}
    
    }
    
    public void directPayOptionMenu(UserContext userContext) {
    	boolean loop = true;
		int choice = 0;
		while (loop) {
			try{
				System.out.println("\n");
		    	System.out.println("DIRECT PAY");
		        System.out.println("********************");		        
		        System.out.println(NAV_VIEW_CARD_ACCOUNT + ". View Card Account");
		        System.out.println(NAV_EDIT_CARD_ACCOUNT + ". Edit Card Account");
		        System.out.println(NAV_DELETE_CARD_ACCOUNT + ". Delete Card Account");
		        System.out.println(NAV_ADD_CARD_ACCOUNT + ". Add Card Account");
		        System.out.println(NAV_ADD_DIRECTPAY_PAYEE + ". Add Direct Pay Payee");
		        System.out.println(NAV_VIEW_PAYEES + ". View Payees");
		        System.out.println(NAV_POST_PAYMENT + ". Pay Now");
		        System.out.println(NAV_VIEW_PAYMENTS + ". View Payments");
		        System.out.println(NAV_STOP_PAYMENT + ". Stop Payment");
		        System.out.println(NAV_SETUP_AUTOPAY + ". Setup AutoPay");
		        System.out.println(NAV_VIEW_AUTOPAY + ". View AutoPays");
		        System.out.println(NAV_QUIT + ". Exit Sub-menu");
		        System.out.println("********************");
		        System.out.print("Choice: " );
		
		        choice = IOUtils.readInt();
		        System.out.println();
		        if (choice == NAV_VIEW_CARD_ACCOUNT) {
		            viewPaymentAccounts(userContext);
		        } else if (choice == NAV_ADD_DIRECTPAY_PAYEE) {
		            addDirectpayPayee(userContext);
		        } else if (choice == NAV_EDIT_CARD_ACCOUNT) {
		            System.out.println("Not Implemented");
		        } else if (choice == NAV_DELETE_CARD_ACCOUNT) {
		           deletePaymentAccounts(userContext);
		        } else if (choice == NAV_ADD_CARD_ACCOUNT) {
		            addPaymentAccount(userContext);
		        } else if (choice == NAV_VIEW_PAYEES) {
		        	viewPayees(userContext);
		        } else if (choice == NAV_POST_PAYMENT) {
		            postPayment(userContext);
		        } else if (choice == NAV_VIEW_PAYMENTS) {
		        	viewPayments(userContext);
		        } else if (choice == NAV_STOP_PAYMENT) {
		        	stopPayment(userContext);
		        } else if (choice == NAV_SETUP_AUTOPAY) {
		        	setupAutoPay(userContext);
		        } else if (choice == NAV_VIEW_AUTOPAY) {
		        	viewAutoPays(userContext);
		        } else if (choice == NAV_QUIT){
		        	return;
		        }
			}catch (Exception e) {
	            e.printStackTrace();
	        }
		}        
    }
    
       
    public void payItAllOptionMenu(UserContext userContext){
		boolean loop = true;
		int choice = 0;
		while (loop) {
	       	try { 
    	
	    	System.out.println("\n");
	    	System.out.println(" PAY IT ALL");
	    	System.out.println("-------------------------");
	        System.out.println("** PAYMENT ACCOUNTS ** ");
	        System.out.println("-------------------------");
	        System.out.println(NAV_PIA_ADD_PAYM_ACCOUNT + ". Add Payment Account");
	        System.out.println(NAV_PIA_VIEW_ACTIVE_PAYM_ACCOUNTS + ". View All Active/verified Payment Accounts");
	        System.out.println(NAV_PIA_VIEW_ALL_PAYM_ACCOUNTS + ". View All Payment Accounts");
	        System.out.println(NAV_PIA_EDIT_PAYM_ACCOUNT + ". Edit Payment Account");
	        System.out.println(NAV_PIA_UPDATE_PAYM_ACCOUNT_STATUS + ". Update Payment Account Status");
	        System.out.println(NAV_PIA_REMOVE_PAYM_ACCOUNT + ". Remove Payment Account");
	        System.out.println(NAV_PIA_VERIFY_CD_AMOUNT + ". Verify CD Amount");
	        System.out.println(NAV_PIA_MODIFY_PREFERRED_PAYM_ACCOUNT + ". Modify Preferred Payment Account");
	        System.out.println("\n-------------------------");
	        System.out.println("** PAYEE ** ");
	        System.out.println("-------------------------");
	        System.out.println(NAV_PIA_ADD_PAYEE + ". Add Payee");
	        System.out.println(NAV_PIA_ADD_PAY_TODAY_PAYEE + ". Add Pay Today Payee");
	        System.out.println(NAV_PIA_ADD_CUSTOM_PAYEE + ". Add Custom Payee");
	        System.out.println(NAV_PIA_UPDATE_PAYEE + ". Update Payee");
	        System.out.println(NAV_PIA_REMOVE_PAYEE + ". Remove Payee");
	        System.out.println(NAV_PIA_VIEW_ALL_PAYEES + ". View All Payees");
	        System.out.println("\n-------------------------");
	        System.out.println("** PAYMENT MANAGEMENT ** ");
	        System.out.println("-------------------------");
	        System.out.println(NAV_CREATE_PAYTODAY_PAYMENT + ". Create PayToday Payment");
	        System.out.println(NAV_VIEW_ALL_PAY_TODAY_PAYMENTS + ". View All Pay Today Payments");        
	        System.out.println(NAV_CREATE_PAYITALL_PAYMENT + ". Create PayItAll Payment");
	        System.out.println(NAV_PIA_VIEW_ALL_PAYMENTS + ". View All Payments");
	        System.out.println(NAV_MODIFY_SCHEDULED_PAYMENT + ". Modify Scheduled Payment");
	        System.out.println(NAV_MODIFY_RECURRING_PAYMENT + ". Modify Recurring Payment");
	        System.out.println(NAV_CANCEL_SCHEDULED_PAYMENT + ". Cancel Scheduled Payment");
	        System.out.println(NAV_CANCEL_RECURRING_PAYMENT + ". Cancel Recurring Payment");
	        System.out.println(NAV_STOP_CHECK_PAYMENT + ". Stop Check Payment");
	        System.out.println(NAV_CANCEL_STOP_PAYMENT + ". Cancel Stop Payment");
	        /*
	        System.out.println(NAV_CALCULATE_EARLIEST_PAYM_DATE + ". Calculate Earliest Payment Date");
	        System.out.println(NAV_CALCULATE_EARLIEST_PROCESS_DATE + ". Calculate Earliest Process Date");
	        System.out.println(NAV_CALCULATE_EARLIEST_DELIVERON_DATE + ". Calculate Earliest Deliver On Date");
	        */
	        System.out.println("\n-------------------------");
	        System.out.println("** BILLER MANAGEMENT ** ");
	        System.out.println("-------------------------");
	        System.out.println(NAV_LIST_AVAIL_BILLER_CAT + ". List the available Biller Categories");
	        System.out.println(NAV_LIST_CSI_FOR_SERVICE_ID + ". List Content Service Info for Service Id");
	        System.out.println(NAV_SEARCH_BILLER_SERVICES + ". Search Biller Services");
	        System.out.println(NAV_SEARCH_BILLER_SERVICES_BY_CATEGORY + ". Search Biller Services by Category");
	        System.out.println(NAV_PIA_VIEW_ALL_EXPEDITED_ENABLED_SERVICES + ". View All Expedited Enabled Services");
	        System.out.println(NAV_QUIT + ". Exit Sub-menu");
	        System.out.println("-------------------------");
	        System.out.print("Choice: " );        
	    	
	    	choice = IOUtils.readInt();
	        System.out.println();
        
		    if (choice == NAV_PIA_ADD_PAYM_ACCOUNT ){
		    	addPayItAllPaymentAccount(userContext);	
		    } else if (choice == NAV_PIA_VIEW_ACTIVE_PAYM_ACCOUNTS ){
		    	viewPayItAllPaymentAccounts(userContext,BillPayConstants.ACTIVE_AND_VERIFIED_PAYM_ACCTS);
		    } else if (choice == NAV_PIA_VIEW_ALL_PAYM_ACCOUNTS ){
		    	viewPayItAllPaymentAccounts(userContext,BillPayConstants.ALL_PAYM_ACCTS);
		    } else if (choice == NAV_PIA_EDIT_PAYM_ACCOUNT ){
		    	updatePayItAllPaymentAccounts(userContext);
		    } else if (choice == NAV_PIA_UPDATE_PAYM_ACCOUNT_STATUS ){
		    	updatePaymStatus(userContext);
		    } else if (choice == NAV_PIA_REMOVE_PAYM_ACCOUNT ){
		    	deletePayItAllPaymentAccounts(userContext);
		    } else if (choice == NAV_PIA_VERIFY_CD_AMOUNT ){
		    	verifyCDAmount(userContext);
		    } else if (choice == NAV_PIA_MODIFY_PREFERRED_PAYM_ACCOUNT ){
		    	modifyPreferredPaymAccount(userContext);
		    } else if (choice == NAV_PIA_ADD_PAYEE ){
		    	addPayItAllPayee(userContext);
		    }else if (choice == NAV_PIA_ADD_PAY_TODAY_PAYEE ){
		    	addPayTodayPayee(userContext);
		    } else if (choice == NAV_PIA_ADD_CUSTOM_PAYEE ){
		    	addCustomPayee(userContext);
		    }else if (choice == NAV_PIA_UPDATE_PAYEE ){
		    	updatePayItAllPayee(userContext);
		    } else if (choice == NAV_PIA_REMOVE_PAYEE ){
		    	removePayItAllPayee(userContext);
		    } else if (choice == NAV_PIA_VIEW_ALL_PAYEES ){
		    	viewAllPayItAllPayees(userContext);
		    } else if (choice == NAV_CREATE_PAYTODAY_PAYMENT ){
		    	createPayTodayPayment(userContext);
		    	//System.out.println(" Not Implemented");
		    } else if (choice == NAV_VIEW_ALL_PAY_TODAY_PAYMENTS ){  
		    	viewPayTodayPayment(userContext);
		    } else if (choice == NAV_CREATE_PAYITALL_PAYMENT ){
		    	createPayItAllPayment(userContext);
		    } else if (choice == NAV_PIA_VIEW_ALL_PAYMENTS ){  
		    	viewPayitAllPayment(userContext,BillPayConstants.BOTH_SCHEDULED_AND_RECURRING);
		    } else if (choice == NAV_MODIFY_SCHEDULED_PAYMENT ){  
		    	editScheduledPayments(userContext);
		    } else if (choice == NAV_MODIFY_RECURRING_PAYMENT ){  
		    	editRecurringPayments(userContext);
		    } else if (choice == NAV_CANCEL_SCHEDULED_PAYMENT ){  
		    	cancelSchedulePayments(userContext);
		    } else if (choice == NAV_CANCEL_RECURRING_PAYMENT ){  
		    	cancelRecurringPayments(userContext);
		    } else if (choice == NAV_STOP_CHECK_PAYMENT ){  
		    	stopCheckPayment(userContext);
		    } else if (choice == NAV_CANCEL_STOP_PAYMENT ){  
		    	canclStopCheckPayment(userContext);
		    } else if (choice == NAV_CALCULATE_EARLIEST_PAYM_DATE ){ 
		    	calcEarliestPaymentDate(userContext);
		    	//System.out.println(" Not Implemented");
		    } else if (choice == NAV_CALCULATE_EARLIEST_PROCESS_DATE ){
		    	calcEarliestProcessDate(userContext);	    	
		    //	System.out.println("");
		    } else if (choice == NAV_CALCULATE_EARLIEST_DELIVERON_DATE ){  
		    	calcEarliestDeliverOnDate(userContext);
		    } else if (choice == NAV_LIST_CSI_FOR_SERVICE_ID ){
		    	getContentServiceInfoForService(userContext);
		    } else if (choice == NAV_SEARCH_BILLER_SERVICES ){
		    	searchBillerServices(userContext);
		    }
		    else if (choice == NAV_PIA_VIEW_ALL_EXPEDITED_ENABLED_SERVICES ){
		    	viewExpeditedEnabledServices(userContext);
		    }
		    else if (choice == NAV_QUIT ){  
		    	loop = false;
		    } else 
				 System.out.println("Invalid Entry!");
	       }catch (Exception e) {
	            e.printStackTrace();
	       }
		}//end of while
    }

    /**
     * View Payment Accounts (Credit Card).
     */
    public void viewPaymentAccounts(UserContext userContext) {
    System.out.println("Viewing Card Payment Accounts with user context: "
            + userContext);

        BillPayment bp = new BillPayment();
        bp.viewPaymentAccountsDetails(userContext);
    }

    
    /**
     * View Payment Accounts (Credit Card).
     */
    public void addDirectpayPayee(UserContext userContext) {
    
        BillPayment bp = new BillPayment();
        bp.addDirectpayPayee(userContext);
    }

    
    
    /**
     * Delete a payment account.
     */
    public void deletePaymentAccounts(UserContext userContext) {
      System.out.println("Deleting payment accounts with user context: "
            + userContext);

        BillPayment bp = new BillPayment();
        bp.removePaymentAccount(userContext);
    }

    /**
     * Add Payment Account (Credit Card).
     *
     * Prompt the user to enter Card Payment Account
     * information.
     */
    public void addPaymentAccount(UserContext userContext) {
        BillPayment bp = new BillPayment();
        CardPaymentAccount cpa = bp.createSampleCardAccount();
        System.out.println("Adding payment acct");
        bp.addPaymentAccount(userContext, cpa);
        System.out.println("Added the acct");
       }

    /**
     * Add PayItAllPayee
     * @param userContext
     */
    public void addPayItAllPayee(UserContext userContext){
    	ManagePayee managePayee = new ManagePayee();
    	boolean isCustomPayee = false;
    	managePayee.addPayItAllPayee(userContext,isCustomPayee);
    }
    /**
     * Add PayTodayPayee
     * @param userContext
     */
    public void addPayTodayPayee(UserContext userContext){
    ManagePayee managePayee = new ManagePayee();
    managePayee.addPayTodayPayee(userContext);
    }
    
    /**
     * adds a custom Payee
     * @param userContext
     */
    public void addCustomPayee(UserContext userContext){
		ManagePayee managePayee = new ManagePayee();
		boolean isCustomPayee=true;
		managePayee.addPayItAllPayee(userContext,isCustomPayee);
	}
    
    public void updatePayItAllPayee(UserContext userContext){
    ManagePayee managePayee = new ManagePayee();
    managePayee.updatePayItAllPayee(userContext);
    }
    
    /**
     * Remove a particular payee based on user provided payee Id
     * @param userContext
     */
    public void removePayItAllPayee(UserContext userContext){
    	
    ManagePayee managePayee = new ManagePayee();
    managePayee.removePayItAllPayee(userContext);
    }
    
    /**
     * View only PayItAll Payees
     * @param userContext
     */
    public void viewAllPayItAllPayees(UserContext userContext){
    	
    	ManagePayee managePayee = new ManagePayee();
    	managePayee.viewAllPayItAllPayees(userContext);
    }
   
    /**
     * View Payees.
     */
    public void viewPayees(UserContext userContext) {
       BillPayment bp = new BillPayment();
        bp.viewPayees(userContext);
    }
    
    /**
     * Post Payment.
     */
    public void postPayment(UserContext userContext) {
      BillPayment bp = new BillPayment();

        // Select payee
        try {
            bp.viewPayees(userContext);
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("Please enter PayeeId: ");

        int payeeId = IOUtils.readInt();

        // Select Card Account
        try {
            bp.listPaymentAccounts(userContext);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Payee payee = bp.getPayee(userContext, payeeId);
        
    	ArrayOfPaymentProfile paymentProfileArray=payee.getPaymentProfiles();
    	PaymentProfile[] paymentProfiles=null;
    	if (paymentProfileArray!=null){
    		paymentProfiles=paymentProfileArray.getElements();
    	}            
        PaymentProfile paymentProfile=null;
        if (paymentProfiles!=null){
        	for (int j = 0; j < paymentProfiles.length; j++){
			
	        	PaymentProfile paymProfile = (PaymentProfile)paymentProfiles[j];
				//Check if the payent profile is instance of Bank Payment Profile
				
				if (paymProfile instanceof CardPaymentProfile){
					paymentProfile = bp.getCardPaymentProfile(paymentProfiles) ;
				}else return;
        	}
        }
        else {
        	System.out.println("PAYMENT PROFILE is EMPTY. Configuration Issue!");
        	return;
        }
         
        
        if (!paymentProfile.isPayNowSupported()) {
        	System.out.print("Pay now is not supported for this payee.");
        } else {
	        System.out.println("Enter Card Account Number: ");
	
	        String cardNum = IOUtils.readStr();
	
	        System.out.println("Enter security code: ");
	
	        String securityCode = IOUtils.readStr();
	
	        // Post Payment
	        String transId = "";
	
	        try {
	            transId =
	                bp.postPayment(userContext, payeeId, cardNum, securityCode);
	            System.out.println("Posted payment with TransactionID: " +  transId);
	            
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
        }
        
/*	
 * 	     We are removing the Polling of payments. Payments may take up to 24 hours to process.
 * 
 *         // Poll Payment
        if (bp.pollPaymentStatus(userContext, transId)) {
            // Success, do something
        }
        */
    }
    /**
     * View Payments.
     */
    public void viewPayments(UserContext userContext) {
        BillPayment bp = new BillPayment();
        bp.viewPayments(userContext);
    }


    /**
     * Stops a payment that has been made.
     */
    public void stopPayment(UserContext userContext) {
     System.out.println("Revised stopPayment");

        BillPayment bp = new BillPayment();
        bp.stopPayment(userContext);
    }

    /**
     * Method to allow the user to setup an AutoPay.
     */
    public void setupAutoPay(UserContext userContext) {
        
        BillPayment bp = new BillPayment();

        // Select payee
        try {
            bp.viewPayees(userContext);
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.print("Please enter Payee Id: ");

        int payeeId = IOUtils.readInt();

        // Select Card Account
        try {
            bp.listPaymentAccounts(userContext);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Payee payee = bp.getPayee(userContext, payeeId);
                
        long itemAccountId = payee.getItemAccountId();
        System.out.print("Enter Card Account Number: ");
        String cardNum = IOUtils.readStr();

        System.out.print("Enter Security Code: ");
        String securityCode = IOUtils.readStr();

        String autoPayId = null;
        try {
        	CardNumber cardNumber = new CardNumber();
        	cardNumber.setCardNumber(cardNum);
        	autoPayId = autoPaySetupService.startAutoPaySetup(userContext, new Long(itemAccountId),
        			cardNumber, securityCode, null, null, null);      	
    
        } catch (Exception e){
        	e.printStackTrace();
        }

        System.out.println("Autopay Setup is successful. Your AutoPayId is " + autoPayId);
        
    }

    /**
     * Method to show all the AutoPays for the user.
     */
    public void viewAutoPays(UserContext userContext) {
     AutoPaySetup[] autoPaySetups = null; 
     
     try {      
     ArrayOfAutoPaySetup autopayList = autoPaySetupService.getAutoPaySetups(userContext);
      if (autopayList != null){
       autoPaySetups = autopayList.getElements();
      }
     } catch (Exception e) {
    	 e.printStackTrace();
     }

        if (autoPaySetups == null) {
            System.out.println("Not AutoPays Setup.");
        } else {
        	CardPaymentAccount cpa = null;
            for (int i = 0; i < autoPaySetups.length; i++) {
            	if (i==0)
            		System.out.println ("The following Auto Pays are set up: \n======================================");
            	System.out.println("AutoPay Transaction Id: "+ autoPaySetups[i].getTransactionId());
                System.out.println("AutoPay Setup Request Status: "+ autoPaySetups[i].getAutopaySetupRequestStatus());                
                System.out.println("Card Account Nickname: "+ autoPaySetups[i].getPaymentAccountSnapshot().getPaymentAccount().getNickname());
                System.out.println("ItemAccountId: "+ autoPaySetups[i].getItemAccountId());
                System.out.println("StatusCode: "+ autoPaySetups[i].getStatusCode());
                System.out.println("");
            }
        }
    }

    /**
     * Adds a payment account for the user
     * @param userContext
     * 				The user context of the caller
     * 
     */
    private void addPayItAllPaymentAccount(UserContext userContext) {    	 
    BillPayPaymentAccountManagement payitall = new BillPayPaymentAccountManagement();
    payitall.addBankAccount(userContext);
    }
    
    /**  
     * Diaplys the payment accounts of the user
     * @param userContext
     * 				The user context of the caller
     * @param status 
     * 				active-displays all the payment accounts of the user that are active and enabled
     * 				inactive-displays the payment accounts that are active,deactivated and removed
     */
    private void viewPayItAllPaymentAccounts(UserContext userContext,String status) {
        
        BillPayPaymentAccountManagement payit = new BillPayPaymentAccountManagement();
        payit.viewPayItAllPaymentAccountsDetails(userContext,status);
      }
    
    /**
     * Deletes the payment account of the user
     * @param userContext
     * 			The user context of the caller
     */
    private void deletePayItAllPaymentAccounts(UserContext userContext) {
        System.out.println("Deleting payment accounts with user context: "
            + userContext);

        BillPayPaymentAccountManagement payit = new BillPayPaymentAccountManagement();
        payit.removePayItAllPaymentAccount(userContext);
    }  
    
    /**
     * Updates the payment account details of a payment account of a user
     * @param userContext
     * 				The user context of the caller
     */
    private void updatePayItAllPaymentAccounts(UserContext userContext) {
    	BillPayPaymentAccountManagement payit = new BillPayPaymentAccountManagement();
        payit.editPayItAllPaymentAccountsDetails(userContext);		
	}  
   
    /**
     * Enable the user to set a default payment account if he has set anything.
     * If the default payment account already exists,the user can modify it
     * @param userContext
     * 			The user context of the caller
     */
   public void modifyPreferredPaymAccount(UserContext userContext){
	   BillPayPaymentAccountManagement payit = new BillPayPaymentAccountManagement();
       payit.modifyPreferredPaymentAccount(userContext,null);    	
    }
    
    /**
     * Verifies a payment Account Account via CD
     * @param userContext
     *              The user context of the caller    
     */
   private void verifyCDAmount(UserContext userContext) {
	  BillPayPaymentAccountManagement payit = new BillPayPaymentAccountManagement();
       payit.verifyCDAmount(userContext);		
	}
   
   /**
    * Activates or Deavtivates the payment account
    * @param userContext
    * 			The user context of the caller
    */
   private void updatePaymStatus(UserContext userContext) {
	 BillPayPaymentAccountManagement payit = new BillPayPaymentAccountManagement();
       payit.updatePaymentAccountStatus(userContext);
	}   


	
	/**
	 * Gets the contentserviceinof for the service
	 * @param userContext
	 */
	public void getContentServiceInfoForService(UserContext userContext){
		BillPayBillerManagement billerManagement = new BillPayBillerManagement();
		billerManagement.getContentServiceInfosForService(userContext);
	}
	
	/**
	 * Searches the biller services
	 * @param userContext
	 */
	public void searchBillerServices(UserContext userContext){
		BillPayBillerManagement billerManagement = new BillPayBillerManagement();
		billerManagement.getBillerServices(userContext);
	}
	

	/**
     * View Expedited Enabled Services
     * @param userContext
     */
    public void viewExpeditedEnabledServices(UserContext userContext){
    	
    BillPayBillerManagement billerManagement = new BillPayBillerManagement();
    	billerManagement.viewExpeditedEnabledServices(userContext);
    }
    

	/**
	 * Calculates the Earliest Payment Date
	 * @param userContext
	 */
	public void calcEarliestPaymentDate(UserContext userContext){
 /*		BillPayPayment payit = new BillPayPayment();
		payit.getEarliestPaymentDate(userContext);*/
		}
	
	/**
	 * Calculates the Earliest Process Date
	 * @param userContext
	 */
	public void calcEarliestProcessDate(UserContext userContext){
		/*BillPayPayment payit = new BillPayPayment();
		payit.getEarliestProcessDate(userContext);*/
		}
	/**
	 * Calculates the Earliest Deliver On Date
	 * @param userContext
	 */
	public void calcEarliestDeliverOnDate(UserContext userContext){
		/*BillPayPayment payit = new BillPayPayment();
		payit.getEarliestDeliverOnDate(userContext);*/
		}
	/**
	 * Creates a Pay today payment
	 * @param userContext
	 */
	public void createPayTodayPayment(UserContext userContext){
		BillPayPayment payit = new BillPayPayment();
		payit.createPayTodayPayment(userContext);
		}
	/**
	 *View all pay today payments
	 * @param userContext
	 */
	public void viewPayTodayPayment(UserContext userContext){
		BillPayPayment payit = new BillPayPayment();
		payit.viewPayTodayPayment(userContext);
		}

	/**
	 * Creates a pay it all payment
	 * @param userContext
	 */
	public void createPayItAllPayment(UserContext userContext){
		BillPayPayment payit = new BillPayPayment();
		payit.createPayItAllPayment(userContext);
		}
	
	/**
	 * view all the payments for the user
	 * @param userContext
	 * @param paymentFreq
	 */
	public void viewPayitAllPayment(UserContext userContext,String paymentFreq){
		BillPayPayment payit = new BillPayPayment();
		payit.getPayments(userContext,paymentFreq);
		}
	/**
	 * cancels any scheduled payment
	 * @param userContext
	 */
	public void cancelSchedulePayments(UserContext userContext){
		BillPayPayment payment = new BillPayPayment();
		payment.cancelSchedulePayment(userContext);
		}
	/**
	 * cancels any recurring payment
	 * @param userContext
	 */
	public void cancelRecurringPayments(UserContext userContext){
		BillPayPayment payment = new BillPayPayment();
		payment.cancelRecurringPayment(userContext);
		}
	
	/**
	 * modifies scheduled payment of the user
	 * @param userContext
	 */
	public void editScheduledPayments(UserContext userContext){
		BillPayPayment payment = new BillPayPayment();
		payment.modifyScheduledPayment(userContext);
		}
	/**
	 * modifies any recurring payment of the user
	 * @param userContext
	 */
	public void editRecurringPayments(UserContext userContext){
		BillPayPayment payment = new BillPayPayment();
		payment.modifyRecurringPayment(userContext);
		}
	/**
	 * stops any check payment
	 * @param userContext
	 */
	public void stopCheckPayment(UserContext userContext){
		BillPayPayment payment = new BillPayPayment();
		payment.stopCheckPayment(userContext);
		}
	
	/**
	 * cancels any check payment that has been stopped
	 * @param userContext
	 */
	public void canclStopCheckPayment(UserContext userContext){
		BillPayPayment payment = new BillPayPayment();
		payment.cancelStopPayment(userContext);
		}
	
}
