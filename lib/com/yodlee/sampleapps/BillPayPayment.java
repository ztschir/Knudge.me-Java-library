/*
 * Copyright 2009 Yodlee, Inc.  All Rights Reserved.  Your use of this code 
 * requires a license from Yodlee.  Any such license to this code is 
 * restricted to evaluation/illustrative purposes only. It is not intended 
 * for use in a production environment, and Yodlee disclaims all warranties 
 * and/or support obligations concerning this code, regardless of the terms 
 * of any other agreements between Yodlee and you."
 */
package com.yodlee.sampleapps;

import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;

import javax.xml.rpc.ServiceException;


import com.yodlee.sampleapps.helper.BillPayConstants;
import com.yodlee.sampleapps.helper.IOUtils;
import com.yodlee.soap.appext.paymentservice.ReconciledPayment;
import com.yodlee.soap.appext.paymentservice.paymentreconciliationservice.PaymentReconciliationService;
import com.yodlee.soap.appext.paymentservice.paymentreconciliationservice.PaymentReconciliationServiceServiceLocator;
import com.yodlee.soap.collections.appext.paymentservice.ArrayOfReconciledPayment;
import com.yodlee.soap.collections.core.paymentservice.ArrayOfPayment;
import com.yodlee.soap.collections.core.paymentservice.ArrayOfPaymentMode;
import com.yodlee.soap.collections.core.paymentservice.ArrayOfPaymentProfile;
import com.yodlee.soap.common.CobrandContext;
import com.yodlee.soap.common.DateFilter;
import com.yodlee.soap.common.HolidayInfo;
import com.yodlee.soap.common.Money;
import com.yodlee.soap.common.UserContext;
import com.yodlee.soap.common.holidaymanager.HolidayManager;
import com.yodlee.soap.common.holidaymanager.HolidayManagerServiceLocator;
import com.yodlee.soap.core.CoreExceptionFault;
import com.yodlee.soap.core.IllegalArgumentValueExceptionFault;
import com.yodlee.soap.core.InvalidCobrandContextExceptionFault;
import com.yodlee.soap.core.InvalidCobrandConversationCredentialsExceptionFault;
import com.yodlee.soap.core.InvalidConversationCredentialsExceptionFault;
import com.yodlee.soap.core.InvalidUserContextExceptionFault;
import com.yodlee.soap.core.StaleConversationCredentialsExceptionFault;
import com.yodlee.soap.core.acl.aclservice.AclService;
import com.yodlee.soap.core.acl.aclservice.AclServiceServiceLocator;
import com.yodlee.soap.core.dataservice.dataservice.DataService;
import com.yodlee.soap.core.dataservice.dataservice.DataServiceServiceLocator;
import com.yodlee.soap.core.paymentservice.BankPaymentProfile;
import com.yodlee.soap.core.paymentservice.DateType;
import com.yodlee.soap.core.paymentservice.EBillPaymentNotSupportedExceptionFault;
import com.yodlee.soap.core.paymentservice.ExpeditedPaymentNotAllowedExceptionFault;
import com.yodlee.soap.core.paymentservice.ExpeditedPaymentRequest;
import com.yodlee.soap.core.paymentservice.Frequency;
import com.yodlee.soap.core.paymentservice.IllegalStopPaymentActionExceptionFault;
import com.yodlee.soap.core.paymentservice.InvalidDateTypeExceptionFault;
import com.yodlee.soap.core.paymentservice.InvalidPayeeIdExceptionFault;
import com.yodlee.soap.core.paymentservice.InvalidPaymentAccountIdExceptionFault;
import com.yodlee.soap.core.paymentservice.InvalidPaymentAmountExceptionFault;
import com.yodlee.soap.core.paymentservice.InvalidPaymentDateExceptionFault;
import com.yodlee.soap.core.paymentservice.InvalidPaymentStateExceptionFault;
import com.yodlee.soap.core.paymentservice.InvalidPaymentTransactionIdExceptionFault;
import com.yodlee.soap.core.paymentservice.ItemAccountDisabledExceptionFault;
import com.yodlee.soap.core.paymentservice.Payee;
import com.yodlee.soap.core.paymentservice.PayeeCurrentlyNotSupportedForEBillsExceptionFault;
import com.yodlee.soap.core.paymentservice.PayeeStatusNotValidExceptionFault;
import com.yodlee.soap.core.paymentservice.Payment;
import com.yodlee.soap.core.paymentservice.PaymentAccount;
import com.yodlee.soap.core.paymentservice.PaymentAccountId;
import com.yodlee.soap.core.paymentservice.PaymentCannotBeModifiedExceptionFault;
import com.yodlee.soap.core.paymentservice.PaymentFilter;
import com.yodlee.soap.core.paymentservice.PaymentFilterByPayee;
import com.yodlee.soap.core.paymentservice.PaymentFilterByPaymentAccount;
import com.yodlee.soap.core.paymentservice.PaymentFilterByPaymentMode;
import com.yodlee.soap.core.paymentservice.PaymentMode;
import com.yodlee.soap.core.paymentservice.PaymentNotSupportedExceptionFault;
import com.yodlee.soap.core.paymentservice.PaymentProfile;
import com.yodlee.soap.core.paymentservice.PaymentRequest;
import com.yodlee.soap.core.paymentservice.PendingPaymentsExistsExceptionFault;
import com.yodlee.soap.core.paymentservice.StopPaymentStatus;
import com.yodlee.soap.core.paymentservice.payeemanagement.PayeeManagement;
import com.yodlee.soap.core.paymentservice.paymentaccountmanagement.PaymentAccountManagement;
import com.yodlee.soap.core.paymentservice.paymentaccountmanagement.PaymentAccountManagementServiceLocator;
import com.yodlee.soap.core.paymentservice.paymentrules.RiskModel;
import com.yodlee.soap.core.paymentservice.paymentservice.PaymentService;
import com.yodlee.soap.core.paymentservice.paymentservice.PaymentServiceServiceLocator;

public class BillPayPayment extends ApplicationSuper
{
	protected DataService dataService;
	protected PaymentAccountManagement paymentAccountManagement;
	protected PaymentReconciliationService paymentReconciliationService;
	protected PaymentService paymentService;
	protected HolidayManager holidayManager;
	
	protected static PayeeManagement payeeManagementProxy;
	protected AclService aclService;
	private static int payItAllFrequency = 1;
	private static final int  NAV_PIF_WEEKLY = payItAllFrequency++;
	private static final int  NAV_PIF_BI_WEEKLY = payItAllFrequency++;
	private static final int  NAV_PIF_SEMI_MONTHLY = payItAllFrequency++;
	private static final int  NAV_PIF_MONTHLY = payItAllFrequency++;
	private static final int  NAV_PIF_BI_MONTHLY = payItAllFrequency++;
	private static final int  NAV_PIF_QUATERLY = payItAllFrequency++;
	private static final int  NAV_PIF_HALF_YEARLY = payItAllFrequency++;
	private static final int  NAV_PIF_ANNUALLY = payItAllFrequency++;
	
	private static int payItAllPayment = 1;
	private static final int NAV_PAYMENT_AMOUNT =payItAllPayment++;
	private static final int NAV_START_DATE =payItAllPayment++;
	private static final int NAV_PAYMENT_MEMO =payItAllPayment++;
	private static final int NAV_END_DATE =payItAllPayment++;	
	
	public static Calendar cal;
	public int year;
	
	private static String userTimeZone="GMT+5:30";
	
	
	public BillPayPayment ()
	{
		super ();
		
		DataServiceServiceLocator locator = new DataServiceServiceLocator();
		String serviceName = locator.getDataServiceWSDDServiceName();
		locator.setDataServiceEndpointAddress(System.getProperty("com.yodlee.soap.services.url")+ "/" + serviceName);
		try {
			dataService = locator.getDataService();
			
		} catch (ServiceException se) {
			throw new RuntimeException(se);
		}
		
		PaymentAccountManagementServiceLocator paymentMngmtLocator = new PaymentAccountManagementServiceLocator();
		String paymentMngmtServiceName = paymentMngmtLocator.getPaymentAccountManagementServiceWSDDServiceName();
		paymentMngmtLocator.setPaymentAccountManagementServiceEndpointAddress(System.getProperty("com.yodlee.soap.services.url")+ "/" + paymentMngmtServiceName);
		try {
			paymentAccountManagement = paymentMngmtLocator.getPaymentAccountManagementService();
			
		} catch (ServiceException se) {
			throw new RuntimeException(se);
		}
		
		PaymentServiceServiceLocator paymentSeriveLocator = new PaymentServiceServiceLocator();
		String paymentserviceName = paymentSeriveLocator.getPaymentServiceWSDDServiceName();
		paymentSeriveLocator.setPaymentServiceEndpointAddress(System.getProperty("com.yodlee.soap.services.url")+ "/" + paymentserviceName);
		try {
			paymentService = paymentSeriveLocator.getPaymentService();
			
		} catch (ServiceException se) {
			throw new RuntimeException(se);
		}

		PaymentReconciliationServiceServiceLocator paymentReconcileSeriveLocator = new PaymentReconciliationServiceServiceLocator();
		String paymentreconcileServiceName = paymentReconcileSeriveLocator.getPaymentReconciliationServiceWSDDServiceName();
		paymentReconcileSeriveLocator.setPaymentReconciliationServiceEndpointAddress(System.getProperty("com.yodlee.soap.services.url")+ "/" + paymentreconcileServiceName);
		try {
			paymentReconciliationService = paymentReconcileSeriveLocator.getPaymentReconciliationService();
			
		} catch (ServiceException se) {
			throw new RuntimeException(se);
		}
		
		
		AclServiceServiceLocator aclSeriveLocator = new AclServiceServiceLocator();
		String aclServiceName = aclSeriveLocator.getAclServiceWSDDServiceName();
		aclSeriveLocator.setAclServiceEndpointAddress(System.getProperty("com.yodlee.soap.services.url")+ "/" + aclServiceName);
		try {
			aclService = aclSeriveLocator.getAclService();
			
		} catch (ServiceException se) {
			throw new RuntimeException(se);
		}
	
		cal = Calendar.getInstance();
		year=cal.get(Calendar.YEAR);
	}
	
	/**
	 * Allows the user to create either a schedule one-time payment
	 * or recurring payment
	 * @param userContext
	 * 					The user context of the caller
	 */
	public void createPayItAllPayment(UserContext userContext){
		
		PaymentRequest req = new PaymentRequest();
		
		System.out.println("1. Scheduled One-Time Payment");
		System.out.println("2. Scheduled Recurring Payment");
		System.out.print("\nPlease enter whether this is a Scheduled One-time payment or a Scheduled recurring Payment: ");
		try{
			int choice =IOUtils.readInt();
			if(choice == 1)
				req.setFrequency(Frequency.ONE_TIME);
			else if (choice == 2){
				System.out.println("Please enter the recurring frequnecy type");
				System.out.println(NAV_PIF_WEEKLY+". Weekly");
				System.out.println(NAV_PIF_BI_WEEKLY+". Bi-Weekly");
				System.out.println(NAV_PIF_SEMI_MONTHLY+". Semi-Monthly");
				System.out.println(NAV_PIF_MONTHLY+". Monthly");
				System.out.println(NAV_PIF_BI_MONTHLY+". Bi-Monthly");
				System.out.println(NAV_PIF_QUATERLY+". Quarterly");
				System.out.println(NAV_PIF_HALF_YEARLY+". Half-Yearly");
				System.out.println(NAV_PIF_ANNUALLY+". Annually");
				
				System.out.print("Enter your choice: ");
				int option =IOUtils.readInt();
				if(option == NAV_PIF_WEEKLY)
					req.setFrequency(Frequency.WEEKLY	);
				else if(option == NAV_PIF_BI_WEEKLY)
					req.setFrequency(Frequency.BI_WEEKLY);				
				else if(option == NAV_PIF_SEMI_MONTHLY)
					req.setFrequency(Frequency.SEMI_MONTHLY);				
				else if(option == NAV_PIF_MONTHLY)
					req.setFrequency(Frequency.MONTHLY);			
				else if(option == NAV_PIF_BI_MONTHLY)
					req.setFrequency(Frequency.BI_MONTHLY);				
				else if(option == NAV_PIF_QUATERLY)
					req.setFrequency(Frequency.QUARTERLY);			
				else if(option == NAV_PIF_HALF_YEARLY)				
					req.setFrequency(Frequency.HALF_YEARLY);
				else if(option == NAV_PIF_ANNUALLY)			
					req.setFrequency(Frequency.ANNUALLY);	
				else{
					System.out.println("\nPlease enter a valid frequency type number");
					return;
				}
			}else{
				System.out.println("\nPlease enter either 1-one time payment or 2-scheduled payment");
				return;
			}
			
			Payee payee = readPayee(userContext);
			if (payee == null)
				return;
			
			req.setPayeeId(payee.getPayeeId());
			
			PaymentAccount paymentAccount = readPaymentAccount(userContext);
			if (paymentAccount == null)
				return;
			
			req.setPaymentAccountIdentifier(paymentAccount.getPaymentAccountIdentifier());
			
			System.out.print("Enter the amount to be paid: ");		
			String dubAmt=IOUtils.readStr();
			//Money money;
	        Money money = new Money();
			try{
		        money.setAmount(new BigDecimal(dubAmt));
		        money.setCurrencyCode("USD");

			}catch(NumberFormatException ex){
				System.out.println("Please enter valid payment amount");
				return;
			}catch(Exception e){
				e.printStackTrace();
				return;
			}
			req.setPaymentAmount(money);
			System.out.print("Enter the payment memo: ");
			String memo=IOUtils.readStr();
			req.setPaymentMemo(memo);
			//getting the earlist date the payment can be made
			Calendar earliestPaymentDate=paymentService.getEarliestPossibleDateForPayment(userContext,payee,RiskModel.RISK_BASED);
			Format formatter;
			formatter = new SimpleDateFormat("MM/dd/yyyy");
			String  earliestDate= formatter.format(earliestPaymentDate.getTime());
			
			//Read payment start date
			System.out.println("Please enter payment initiation date details");
			System.out.println("(Note that Payment Date should not be less than " + earliestDate + " )");
			
			System.out.print("Enter the Payment date [01-31]: ");
			int start_date=IOUtils.readInt();
			System.out.print("Enter the Payment Month [01-Jan,02-Feb....12-Dec]: ");
			int start_month=IOUtils.readInt();
			System.out.print("Enter the Payment Year > [" +year+"]: ");
			int start_year=IOUtils.readInt();
			
			if(!IOUtils.validateDate(start_year,start_month,start_date))
				return;
			
			Calendar startDate=Calendar.getInstance();	
			startDate.set(start_year,start_month-1,start_date);
			
			if (startDate.getTime().compareTo(earliestPaymentDate.getTime()) == -1)
			{
				System.out.println("Payment start date Should not be less than "+earliestDate);
				return;
			}
			
			req.setStartDate(startDate);
			
			if (choice==2){
				System.out.println("Please enter payment ending date details");
				System.out.print("Enter the Payment date [01-31]: ");
				int end_date=IOUtils.readInt();
				System.out.print("Enter the Payment Month [01-Jan,02-Feb....12-Dec]: ");
				int end_month=IOUtils.readInt();
				System.out.print("Enter the Payment Year > ["+year+"]: ");
				int end_year=IOUtils.readInt();
				
				if(!IOUtils.validateDate(end_year,end_month-1,end_date))
					return;
				
				Calendar end_cal=Calendar.getInstance();
				end_cal.set(end_year,end_month-1,end_date);
				req.setEndDate(end_cal);
				
				if (end_cal.getTime().compareTo(startDate.getTime()) == -1)
				{
					System.out.println("Payment end date should be greater than start date");				
					return;
				}
			}
			req.setDateType(DateType.RISK_DUE_DATE);
			System.out.println("*****Payment Request : " + req +"\n\n");
			Long PaymentRequestId=paymentService.createPayment(userContext, req);
			System.out.println("the payment request id is***********************"+PaymentRequestId);
			
			String paymentTxnId =paymentService.schedulePayment1(userContext,PaymentRequestId);
			System.out.println("The payment has been successfully scheduled with transaction id"+paymentTxnId);
			
		}catch(InvalidUserContextExceptionFault e){
			System.out.println("The user context is invalid");
		}catch(InvalidConversationCredentialsExceptionFault e){
			System.out.println("The user session is invalid");			
		}catch(InvalidPayeeIdExceptionFault e){
			System.out.println("The Payee id is invalid");
		}catch(InvalidPaymentAccountIdExceptionFault e){
			System.out.println("The Payment Account is invalid.");
		}catch(InvalidPaymentAmountExceptionFault e){
			System.out.println("The Payment amount is invalid or not in a valid format");
		}catch(InvalidDateTypeExceptionFault e){
			System.out.println("The date type does not match the expected date type in PaymentRequest object.");
		}catch(PaymentNotSupportedExceptionFault e){
			System.out.println("Payment is not supported for the corresponding payee and payment account type");
		}catch(IllegalArgumentValueExceptionFault e){
			System.out.println("Either the scheduled payment" 
					+"memo or tracking number length exceeds max length."
					+" or paymentAmount is null/invalid" 
					+" or startDate is null/invalid"
					+" or endDate is null/invalid in recurring schedules payment case"
					+" or payment frequency is null.");
		}catch(ItemAccountDisabledExceptionFault e){
			System.out.println("The Payment Account is disabled.");
		}catch(PayeeCurrentlyNotSupportedForEBillsExceptionFault e){
			System.out.println("The due date of the bill of the payee is more than 10 days old"
					+ "or it doesnt have a bill with due date in future");
		}catch(PayeeStatusNotValidExceptionFault e){
			System.out.println("The payee has user action required error.So, " +
			"the due date for e-bill can not be determined");
		}catch(EBillPaymentNotSupportedExceptionFault e){
			System.out.println("The payee does not support E-bill payments.");
		} catch (StaleConversationCredentialsExceptionFault e) {
			e.printStackTrace();
		} catch (CoreExceptionFault e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Allows the user to cancel the scheduled one-time payment
	 * @param userContext
	 * 				The user context of the caller
	 */
	public void cancelSchedulePayment(UserContext userContext){
		
		//calling getPayments method to display the scheduled payments
		List payments=getPayments(userContext,BillPayConstants.SCHEDULED_PAYMENT);
		if(payments != null && payments.size()==0)
			return;
		
		System.out.print("Enter the transaction id of the schedule payment that is to be cancelled: ");
		String transactionID=IOUtils.readStr();	
		try{
			paymentService.cancelScheduledPayment1(userContext, transactionID);
			System.out.println("The payment has been successfully cancelled");
		}catch(InvalidUserContextExceptionFault e){
			System.out.println("The user context is invalid");
		}catch(InvalidConversationCredentialsExceptionFault e){
			System.out.println("The user session is invalid");
		}catch(InvalidPaymentStateExceptionFault e){
			System.out.println("The payment transaction identifier is invalid");
		}catch(InvalidPaymentTransactionIdExceptionFault e){
			System.out.println("The payment has past its recall cutoff time");
		}catch(IllegalArgumentValueExceptionFault e){
			System.out.println("Pleae enter valid trasanction id");
		} catch (StaleConversationCredentialsExceptionFault e) {
			e.printStackTrace();
		} catch (CoreExceptionFault e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Allows the user to cancel recurring payment
	 * @param userContext
	 * 				The user context of the caller
	 */
	public void cancelRecurringPayment(UserContext userContext){
		
		//calling getPayments method to display the recurring payment details
		List payments=getPayments(userContext,BillPayConstants.RECURRING_PAYMENT);
		if(payments != null && payments.size()==0)
			return;
		
		System.out.print("Enter the payment Request Id: ");
		Long paymRequestId=new Long(IOUtils.readLong());
		try{
			paymentService.cancelRecurringPaymentRequest1(userContext,paymRequestId);
			System.out.println("The payment has been successfully cancelled");
		}catch(InvalidUserContextExceptionFault e){
			System.out.println("The user context is invalid");
		}catch(InvalidConversationCredentialsExceptionFault e){
			System.out.println("The user session is invalid");
		}catch(PendingPaymentsExistsExceptionFault e){
			System.out.println("There are pending payments in process");
		}catch(IllegalArgumentValueExceptionFault e){
			System.out.println("The payment request id is invalid");
		} catch (StaleConversationCredentialsExceptionFault e) {
			e.printStackTrace();
		} catch (CoreExceptionFault e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Gets the payments for a user and displays few details of all the payments belonging to the user.
	 * @param userContext
	 * @param type
	 * @return
	 */
	public List getPayments(UserContext userContext,String type){		
		List paym = viewPayments(userContext,type);
		Iterator iterator = null;
		if(paym !=null &&paym.size()<=0){
			System.out.println("There are no Payments for the user");
			return paym;
		}
		
		iterator = paym.iterator();
		
		ReconciledPayment payment =null;
		while(iterator.hasNext())
		{
			payment = (ReconciledPayment)iterator.next();
			
			System.out.println("\nPayment Transaction Id : "+payment.getPaymentTransactionId());
			System.out.println("Payment Amount          : "+payment.getPaymentAmount().getCurrencyCode()+" "+ payment.getPaymentAmount().getAmount());
			System.out.println("Payee Id                : "+payment.getPayeeId());
			System.out.println("PaymentAccount Id       : "+payment.getPaymentAccountId());
			System.out.println("Payment Status          : "+payment.getPaymentStatus().getPaymentRequestStatusName());
			System.out.println("Payment frequency       : "+payment.getPaymentFrequency());
			System.out.println("Payment memo            : "+payment.getPaymentMemo());
			System.out.println("Payment request date    : "+payment.getPaymentRequestDate().getDate());
			System.out.println("Payment date            : "+payment.getPaymentDate().getDate());
		}
		return paym;
	}
	/**
	 * Returns a list of reconciledPayments of the user based on the freauency type
	 * 
	 * @param userContext
	 * @param frequencyType
	 * @return
	 */
	public  List viewPayments(UserContext userContext,String frequencyType){
		
		PaymentFilterByPaymentAccount paymentFilterByPaymentAccount =new PaymentFilterByPaymentAccount();
		List payments =new ArrayList();
		PaymentFilterByPayee paymentFilterByPayee =new PaymentFilterByPayee();
		DateFilter byPaymentCompletedDate= new DateFilter();
		DateFilter byPaymentDate= new DateFilter();
		
		Calendar cal =Calendar.getInstance();
		
		//adding 5 years from now to set set the ending date range of payment date and payment completed date
		cal.add(Calendar.YEAR,5);
		byPaymentCompletedDate.setEndDate(cal);
		byPaymentDate.setEndDate(cal);
		
		//subtracting 5 years from now to set set the start date range of payment date and payment completed date
		cal.add(Calendar.YEAR,-10);
		byPaymentCompletedDate.setStartDate(cal);
		byPaymentDate.setStartDate(cal);
		
		try{
			ArrayOfReconciledPayment paymArray =paymentReconciliationService.getPayments(userContext,paymentFilterByPayee,paymentFilterByPaymentAccount,byPaymentCompletedDate,byPaymentDate,true);
			ReconciledPayment [] paym = null;
			if (paymArray != null){
				paym = paymArray.getElements();
			}
			if(paym== null || paym.length == 0){
				System.out.println("The size above the paymentreconcilationservice.getpaymentsis either o or null");
				return payments;
			}
			if (frequencyType.equalsIgnoreCase(BillPayConstants.BOTH_SCHEDULED_AND_RECURRING)){
				payments= Arrays.asList(paym);
			}/*else if(frequencyType.equalsIgnoreCase(BillPayConstants.CHECK_PAYMENTS)){
			for(int i =0; i< paym.length; i++){
			if(paym[i].getPaperCheckStatus()!=null)
			payments.add(paym);
			}			
			}*/else if(frequencyType.equalsIgnoreCase(BillPayConstants.SCHEDULED_PAYMENT)){
				for(int i =0; i< paym.length; i++){
					
					//adding the payment object to the list only if the frequency of the payment is one time
					if((paym[i].getPaymentFrequency()!=null) && (paym[i].getPaymentFrequency().getValue() == Frequency._ONE_TIME))
						payments.add(paym);
				}
			}else if(frequencyType.equalsIgnoreCase(BillPayConstants.SCHEDULED_PAYMENT)){
				for(int i =0; i< paym.length; i++){
					
//					adding the payment object to the list if the frequency type is anything other than one-time(recurring)
					if((paym[i].getPaymentFrequency()!=null) && paym[i].getPaymentFrequency().getValue() !=Frequency._ONE_TIME){
						payments.add(paym);
					}
				}
			}
		}
		catch(InvalidUserContextExceptionFault ex){
			System.out.println("The use context is not valid");
		}catch(InvalidConversationCredentialsExceptionFault ex){
			System.out.println("Conversation credential is invalid for the user");
		}catch(InvalidPayeeIdExceptionFault ex){
			System.out.println("The payee id is invalid");
		}catch(InvalidPaymentAccountIdExceptionFault ex){
			System.out.println("The payment account is invalid");
		}catch(IllegalArgumentValueExceptionFault ex){
			System.out.println("The dates in the date based filters are invalid");
		} catch (StaleConversationCredentialsExceptionFault e) {
			e.printStackTrace();
		} catch (CoreExceptionFault e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return payments;	
	}
	
	/**
	 * Allows the user to modify the payment amount,start date ,memo, and the date type
	 * @param userContext
	 * 				The user context of the caller
	 */
	public void modifyScheduledPayment(UserContext userContext){
		
		try{
			List payments=getPayments(userContext,BillPayConstants.SCHEDULED_PAYMENT);
			if(payments != null && payments.size()==0)
				return;
			
			System.out.print("Enter the transaction id of the Scheduled Payment: ");
			String transactionID=IOUtils.readStr();
			Payment paym = paymentService.getPayment(userContext,transactionID);
			PaymentRequest req =  paym.getPaymentRequest();
			
			System.out.println("Enter the items you want to edit");
			System.out.println("*********************************");
			System.out.println(NAV_PAYMENT_AMOUNT + ". Payment Amount");
			System.out.println(NAV_START_DATE + ". Start Date");
			System.out.println(NAV_PAYMENT_MEMO + ". Payment Memo");
			System.out.println("**********************************");
			System.out.print("\nChoice: " );   
			
			int choice =IOUtils.readInt();
			
			//updates the payment amount
			if(choice ==  NAV_PAYMENT_AMOUNT){
				System.out.println("\nExisting payment Amount: "+req.getPaymentAmount());
				System.out.print("Enter new  Payment Amount: ");
				String amount =IOUtils.readStr();
		        Money money = new Money();
				//Money money;
				try{
			        money.setAmount(new BigDecimal(amount));
			        money.setCurrencyCode("USD");

				}catch(NumberFormatException ex){
					System.out.println("Please enter valid payment amount");
					return;
				}catch(Exception e){
					e.printStackTrace();
					return;
				}
				req.setPaymentAmount(money);
				
			}else if(choice ==  NAV_PAYMENT_MEMO){
				System.out.println("Existing payment memo: "+ req.getPaymentMemo());
				System.out.print("Enter new Payment Memo: ");
				req.setPaymentMemo(IOUtils.readStr());
			}else if (choice == NAV_START_DATE){
				System.out.println("\nExisting payment start date is "+req.getStartDate());
				
				System.out.println("Please enter new payment initiation date details");
				System.out.print("Enter the Payment date [01-31]: ");
				int start_date=IOUtils.readInt();
				System.out.print("Enter the Payment Month [01-Jan,02-Feb....12-Dec]: ");
				int start_month=IOUtils.readInt();
				System.out.print("Enter the Payment Year >["+year+"]: ");
				int start_year=IOUtils.readInt();
				
				if(!IOUtils.validateDate(start_year,start_month,start_date))
					return;
				
				Calendar cal=Calendar.getInstance();
				cal.set(start_year,start_month-1,start_date);
				//	Date startDate = cal.getTime();
				req.setStartDate(cal);
			}else{ 
				System.out.println("Please enter a valid option");
				return;
			}
			paymentService.modifyScheduledPayment(userContext, paym);
			System.out.println("The payment has been successfully modified");
		}catch(InvalidUserContextExceptionFault ex){
			System.out.println("The user context is invalid");
		}catch(IllegalArgumentValueExceptionFault e){
			System.out.println("Either the scheduled payment" 
					+"memo or tracking number length exceeds max length."
					+" or paymentAmount is null/invalid" 
					+" or startDate is null/invalid"
					+" or endDate is null/invalid in recurring schedules payment case"
					+" or payment frequency is null.");
		}catch(InvalidPaymentStateExceptionFault ex){
			System.out.println("The payment has past its recall cutoff time,so it cannot be modified");
		}catch(PaymentCannotBeModifiedExceptionFault ex){
			System.out.println("The given payment cannot be modified"); 
		} catch (StaleConversationCredentialsExceptionFault e) {
			e.printStackTrace();
		} catch (InvalidConversationCredentialsExceptionFault e) {
			System.out.println("The user session is invalid");
		} catch (CoreExceptionFault e) {
			e.printStackTrace();
		} catch (InvalidPaymentTransactionIdExceptionFault e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}	
	
	
	/**
	 * Allows the user to edit the payment amount,start date,end date,memo and the date type
	 * @param userContext
	 * 				The user context of the caller
	 */	
	public void modifyRecurringPayment(UserContext userContext){
		
		List payments=getPayments(userContext,BillPayConstants.RECURRING_PAYMENT);
		if(payments != null && payments.size()==0)return;
		
		System.out.print("Enter the transaction id of the Scheduled Payment");
		String transactionID=IOUtils.readStr();
		Payment paym=null;
		try {
			paym = paymentService.getPayment(userContext,transactionID);
		} catch (StaleConversationCredentialsExceptionFault e1) {
			e1.printStackTrace();
		} catch (InvalidConversationCredentialsExceptionFault e1) {
			System.out.println("The user session is invalid");
		} catch (CoreExceptionFault e1) {
			e1.printStackTrace();
		} catch (InvalidPaymentTransactionIdExceptionFault e1) {
			e1.printStackTrace();
		} catch (IllegalArgumentValueExceptionFault e1) {
			e1.printStackTrace();
		} catch (InvalidUserContextExceptionFault e1) {
			e1.printStackTrace();
		} catch (RemoteException e1) {
			e1.printStackTrace();
		}
		PaymentRequest req =  paym.getPaymentRequest();	
		System.out.println("Enter the items you want to edit");
		System.out.println("*********************************");
		System.out.println(NAV_PAYMENT_AMOUNT + ". Payment Amount");
		System.out.println(NAV_START_DATE + ". Start Date");
		System.out.println(NAV_PAYMENT_MEMO + ". Payment Memo");
		System.out.println(NAV_END_DATE+ ". End Date");
		System.out.println("**********************************");
		System.out.print("\nChoice: " );   		
		int choice =IOUtils.readInt();
		if(choice ==  NAV_PAYMENT_AMOUNT){
			System.out.println("\nExisting payment Amount "+req.getPaymentAmount());
			
			System.out.println("Enter new Payment Amount");
			String amount =IOUtils.readStr();
	        Money money = new Money();
			//Money money;
			try{
		        money.setAmount(new BigDecimal(amount));
		        money.setCurrencyCode("USD");

			}catch(NumberFormatException ex){
				System.out.println("Please enter valid payment amount");
				return;
			}catch(Exception e){
				e.printStackTrace();
				return;
			}
			
			req.setPaymentAmount(money);
		}else if(choice ==  NAV_PAYMENT_MEMO){
			System.out.println("Existing payment memo: "+ req.getPaymentMemo());
			
			System.out.println("Enter new Payment Memo");
			req.setPaymentMemo(IOUtils.readStr());
		}else if (choice == NAV_START_DATE){
			System.out.println("\nExisting payment start date is "+req.getStartDate());
			
			System.out.println("Please enter new payment initiation date details");
			System.out.print("Enter the Payment date [1-31]: ");
			int start_date=IOUtils.readInt();
			System.out.print("Enter the Payment Month [01-Jan,02-Feb....12-Dec]: ");
			int start_month=IOUtils.readInt();
			System.out.print("Enter the Payment Year >"+year+"]: ");
			int start_year=IOUtils.readInt();
			
			if(!IOUtils.validateDate(start_year,start_month,start_date))
				return;
			
			Calendar startDate=Calendar.getInstance();
			startDate.set(start_year,start_month-1,start_date);
			//Date startDate = cal.getTime();
			//Date endDate = req.getEndDate();
			Calendar endDate = req.getEndDate();
			if (endDate.compareTo(startDate) == -1)
			{
				System.out.println("Payment end date should be greater than start date");				
				return;
			}
			req.setStartDate(startDate);
			
		}else if (choice == NAV_END_DATE){
			System.out.println("\nExisting payment end date is"+req.getEndDate());
			
			System.out.print("Please enter payment ending date details");
			System.out.print("Enter the Payment date [1-31]: ");
			int end_date=IOUtils.readInt();
			System.out.print("Enter the Payment Month [01-Jan,02-Feb....12-Dec]: ");
			int end_month=IOUtils.readInt();
			System.out.print("Enter the Payment Year >["+year+"]: ");
			int end_year=IOUtils.readInt();
			
			if(!IOUtils.validateDate(end_year,end_month,end_date))
				return;
			
			Calendar end_cal=Calendar.getInstance();
			end_cal.set(end_year,end_month-1,end_date);
			//Date endDate = end_cal.getTime();
			//Date startDate =req.getStartDate();
			Calendar startDate =req.getStartDate();
			if (end_cal.getTime().compareTo(startDate.getTime()) == -1)
			{
				System.out.println("Payment end date should be greater than start date");				
				return;
			}
			req.setEndDate(end_cal);
		}else{ 
			System.out.println("Please enter a valid option");
			return;
		}
		try {
			paymentService.modifyRecurringPaymentRequest(userContext, req);
		} catch (InvalidPaymentStateExceptionFault e) {
			System.out.println("The payment state in invalid");
		} catch (StaleConversationCredentialsExceptionFault e) {
			e.printStackTrace();
		} catch (InvalidConversationCredentialsExceptionFault e) {
			System.out.println("The user session is invalid");
		} catch (PaymentNotSupportedExceptionFault e) {
			e.printStackTrace();
		} catch (CoreExceptionFault e) {
			e.printStackTrace();
		} catch (IllegalArgumentValueExceptionFault e) {
			e.printStackTrace();
		} catch (InvalidUserContextExceptionFault e) {
			System.out.println("The user Context is invalid");
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		System.out.println("The payment has been successfully modified");
	}
	
	
	/**
	 * stops check payment
	 * @param userContext
	 */
	public void stopCheckPayment(UserContext userContext){
		List reconciledPayment =  getPayments(userContext,BillPayConstants.CHECK_PAYMENTS);
		if(reconciledPayment !=null && reconciledPayment.size() <=0){
			System.out.println("There are no check payments available for the user");
			return;
		}
		System.out.print("Enter the transaction id of the check payment that is to be stopped: ");
		String transactionID=IOUtils.readStr();
		System.out.print("Please enter the reason for stopping the payment: ");
		String reason=IOUtils.readStr();
		try{
			paymentService.stopPayment(userContext,transactionID,reason);
			System.out.println("The check payment is stopped successfully");
		}catch(InvalidUserContextExceptionFault e){
			System.out.println("The user context is invalid");
		}catch(InvalidConversationCredentialsExceptionFault e){
			System.out.println("The user session is invalid");
		}catch(IllegalStopPaymentActionExceptionFault e){
			System.out.println("Stop payment is not supported");
		}catch(IllegalArgumentValueExceptionFault e){
			System.out.println("The payment request id is invalid");
		} catch (StaleConversationCredentialsExceptionFault e) {
			e.printStackTrace();
		} catch (CoreExceptionFault e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * cancels any check payment that has been stopped
	 * @param userContext
	 */
	public void cancelStopPayment(UserContext userContext){
		
		
		//getStopPayments(userContext);
		
		
		Payment[] payments = null;
		ArrayOfPayment paymentsArray = null;
		try{
			PaymentFilter paymentFilter = new PaymentFilter();
			paymentsArray = paymentService.getPayments3(userContext, paymentFilter);
			
			if(paymentsArray != null){
				payments = paymentsArray.getElements();
			}
		}catch (InvalidUserContextExceptionFault ex){
			System.out.println("Invalid User Context. Please login again.");
			return;
		}catch (InvalidConversationCredentialsExceptionFault ex){
			System.out.println("Invalid Conversiation Credentials. Please re-login.");
			return;
		}catch (InvalidPaymentTransactionIdExceptionFault ex){
			System.out.println("IInvalid Payment Transaction id.");
			return;
		}catch (Exception ex){
			ex.printStackTrace();
			return;
		}
		
		ArrayList stopPayments = new ArrayList();
		
		if (payments == null){
			System.out.println("No payments available for the user");
			return;
		}
		for (int i = 0; i < payments.length; i++)
		{
			if (payments[i].getStopPaymentStatus() != null && payments[i].getStopPaymentStatus().getValue().equals(StopPaymentStatus.STOP_IN_PROCESS.getValue()))
			{
				stopPayments.add(payments[i]);
				
				System.out.println("Payment Transaction Id : " + payments[i].getPaymentTransactionId());
				System.out.println("Payment Account Id : " + ((PaymentAccountId)payments[i].getPaymentRequest().getPaymentAccountIdentifier()).getPaymentAccountId());
				
				if (payments[i].getStopPaymentStatus() != null)
					System.out.println("Stop Payment Status : " + payments[i].getStopPaymentStatus().getValue());
				
				
				System.out.println("Payment Amount : " + payments[i].getPaymentRequest().getPaymentAmount().getAmount() + " " + payments[i].getPaymentRequest().getPaymentAmount().getCurrencyCode());
				System.out.println("Payment Description : " + payments[i].getPaymentRequest().getPaymentMemo());
				
				if (payments[i].getPaymentRequest().getFrequency() != null)
					System.out.println("Payment Frequency : " + payments[i].getPaymentRequest().getFrequency());
				System.out.println();
			}
		}
		if (stopPayments == null || stopPayments.size() == 0){
			System.out.println("No payment found which are in STOP_IN_PROCESS.");			
			return;
		}
		
		System.out.print("Enter the transaction id of the payment: ");
		String transactionId=IOUtils.readStr();
		System.out.print("Please enter the reason for stopping the cancel payment: ");
		String reason=IOUtils.readStr();
		try{
			paymentService.cancelStopPayment(userContext,transactionId,reason);
			System.out.println("The cancel check payment is stopped successfully");
		}catch(InvalidUserContextExceptionFault e){
			System.out.println("The user context is invalid");
		}catch(InvalidConversationCredentialsExceptionFault e){
			System.out.println("The user session is invalid");
		}catch(IllegalStopPaymentActionExceptionFault e){
			System.out.println("Stop payment is not supported");
		}catch(IllegalArgumentValueExceptionFault e){
			System.out.println("The payment request id is invalid");
		}catch (StaleConversationCredentialsExceptionFault e) {
			e.printStackTrace();
		} catch (CoreExceptionFault e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		
	}
	private Payee readPayee(UserContext userContext)
	{
		//Step 1 : Display All available Payees . 
		ManagePayee managePayee = new ManagePayee();
		Payee []payeesArray = managePayee.viewAllPayItAllPayees(userContext);
		if (payeesArray == null || payeesArray.length == 0)
		{
			System.out.println("\nPlease Add Payee before adding Payment.");
			return null;
		}
		//Step 2: Read payee id from the User
		Long payeeId;
		System.out.print("Please enter the payee Id: ");
		payeeId = new Long(IOUtils.readLong());
		System.out.println();
		Payee payee =null;
		for(int i=0;i<payeesArray.length;i++){  
			payee=payeesArray[i];
			if (payeesArray[i].getPayeeId().equals(payeeId) )
				break;  
			payee=null;
		}        
		
		// If the payee id doesn't exist 
		if (payee == null) {
			System.out.println("Payee doesn't exist for the given Payee Id");
			return null;
		}
		return payee;
	}
	private PaymentAccount readPaymentAccount(UserContext userContext)
	{
		BillPayPaymentAccountManagement bppam = new BillPayPaymentAccountManagement();
		//Display Active Payment Accounts
		PaymentAccount[] paymentAccounts  = bppam.viewPayItAllPaymentAccountsDetails(userContext, BillPayConstants.ACTIVE_AND_VERIFIED_PAYM_ACCTS);
		//If Payment Account is null or empty. Display message and return.
		if (paymentAccounts == null || paymentAccounts.length == 0)
		{
			System.out.println("\nPlease Add Payment Account before adding Payment.");
			return null;
		}
		
		Long paymentAccountId;
		System.out.println();
		System.out.print("Please enter the Payment Account Id: ");
		paymentAccountId = new Long(IOUtils.readLong());
		PaymentAccount paymentAccount = null;
		for (int i = 0; i < paymentAccounts.length; i++)
		{
			if (((PaymentAccountId)paymentAccounts[i].getPaymentAccountIdentifier()).getPaymentAccountId().equals(paymentAccountId))
			{
				paymentAccount = paymentAccounts[i];
				break;
			}
		}
		if (paymentAccount == null)
		{
			System.out.println("Payment Account doesn't exist for the given payment Account id.");        
			return null;
		}
		return paymentAccount;
	}
	/**
	 * Returns federal holidays for a given date range. If the holiday
	 * information is not available for all of given date range, the
	 * returned holiday info will specify the actual date range for
	 * which the holiday information was returned. If startDate is
	 * null, the date from which holiday info is available will be used.
	 * If endDate is null, the date until which holiday info is available
	 * will be used.
	 *
	 * @param cobrandContext
	 * @param startDate
	 * @param endDate
	 * @return HolidayInfo
	 */
	public  HolidayInfo 	getFederalHolidays(CobrandContext cobrandContext,
			Calendar startDate,
			Calendar endDate){
		//log.info("getFederalHolidays() startDate=" + startDate + " endDate="+endDate);
		
		HolidayManagerServiceLocator holidayMngrLocator = new HolidayManagerServiceLocator();
		String serviceName = holidayMngrLocator.getHolidayManagerServiceWSDDServiceName();
		holidayMngrLocator.setHolidayManagerServiceEndpointAddress(System.getProperty("com.yodlee.soap.services.url")+ "/" + serviceName);
		try {
			holidayManager = holidayMngrLocator.getHolidayManagerService();
			
		} catch (ServiceException se) {
			throw new RuntimeException(se);
		}
		
		
//		HolidayManager holidayManager = (HolidayManager) ProxyFactory.createProxy
//		("com.yodlee.common.HolidayManager");
		HolidayInfo holidayInfo=null;
		try{
			holidayInfo= holidayManager.getFederalHolidays(cobrandContext, startDate, endDate)  ;
		}catch(InvalidCobrandContextExceptionFault icce){
			System.out.println(" Invalid Cobrand Context");
		}
		catch(InvalidCobrandConversationCredentialsExceptionFault iccce){
			System.out.println(" Invalid Cobrand Conversation Credentials");
		}catch (StaleConversationCredentialsExceptionFault e) {			
			e.printStackTrace();
		} catch (InvalidConversationCredentialsExceptionFault e) {			
			e.printStackTrace();
		} catch (CoreExceptionFault e) {			
			e.printStackTrace();
		} catch (IllegalArgumentValueExceptionFault e) {			
			e.printStackTrace();
		} catch (RemoteException e) {			
			e.printStackTrace();
		}
		return holidayInfo ;
	}
		
	/*This function returns all the expedited payments enabled payees added for the user
	 * @param userContext
	 */
	
	public boolean viewExpeditedPaymentsEnabledPayees(UserContext userContext){
		boolean expeditedPayeesAvailable=false;
		ManagePayee mpayees = new ManagePayee();
		//Get all payees for the user
		Payee[] payees = mpayees.getPayItAllPayees(userContext);
		int expeditedPayeesAvailableCount=0;
		
		//If payees exist for the user
		if (payees != null)
		{
			for(int i=0; i<payees.length; i++){
				Payee payee = (Payee)payees[i];
				//Get the payment profile for the user
				ArrayOfPaymentProfile  paymentProfileArray = payee.getPaymentProfiles();
				PaymentProfile[] paymentProfiles = null;
				
				if(paymentProfileArray != null){
					paymentProfiles = paymentProfileArray.getElements();
				}
				if (paymentProfiles != null && paymentProfiles.length > 0)
				{
					for (int j = 0; j < paymentProfiles.length; j++) 
					{
						
						PaymentProfile paymProfile = (PaymentProfile)paymentProfiles[j];
						//Check if the payent profile is instance of Bank Payment Profile
						
						if (paymProfile instanceof BankPaymentProfile){
							BankPaymentProfile bankPaymProfile = (BankPaymentProfile)paymProfile;
							//If the payee is expedited payment enabled print it
							if(bankPaymProfile.isExpeditedPaymentsEnabled()==true){
								expeditedPayeesAvailableCount++;
								System.out.println("\n"+(i+1)+")"+"\n"+"Payee Details-->" + "\n\n"
										+ " NickName: "+ payee.getNickname()+ "\n"
										+ " csId: " + payee.getContentServiceId()+ "\n"
										+ " desc: " + payee.getDescription()+ "\n" 
										+ " PayeeId: " + payee.getPayeeId()+"\n");
								
							}
						}
					}  
				}
			}
		}
		
		//If expedited payees are available return the expeditedPayeesAvailable flag as true else return it as false
		if (expeditedPayeesAvailableCount==0)
			return expeditedPayeesAvailable;
		else{
			expeditedPayeesAvailable=true;
			return expeditedPayeesAvailable;
		}
	}
	
	
	
	
	//This function gets  the next possible expedited payment date for the user.
	private Calendar getNextPossiblePaymentCal(UserContext userContext,long payeeId) {
		Calendar nextPossiblePaymentDate = null;
		try{
			nextPossiblePaymentDate = paymentService.getNextPossibleExpeditedPaymentDate(userContext, new Long(payeeId));
		}catch(InvalidUserContextExceptionFault iuce){
			System.out.println("Invalid User Context");
		}catch(ExpeditedPaymentNotAllowedExceptionFault epnae){
			System.out.println("Expedited Payment Not Allowed Exception");
		}catch(InvalidConversationCredentialsExceptionFault icce){
			System.out.println("Conversation credentilas invalid");
		}catch(InvalidPayeeIdExceptionFault ipie){
			System.out.println("Invalid Payee Id");
		}catch(Exception e){
			System.out.println("Exception occured" +e.getMessage());
		}
		Calendar nextPossiblePayCal = Calendar.getInstance(TimeZone
				.getTimeZone(userTimeZone));
		nextPossiblePayCal.setTime(nextPossiblePaymentDate.getTime());
		return nextPossiblePayCal;
	}
	
	// Get the payment start date for the user.If the user given start date is before 
	//the current date it sets the current date as the start date else retuns the user  given start date only.
	private Calendar createStartCal(Calendar nextPossiblePayCal, int offset) {
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone(userTimeZone));
		cal.set(nextPossiblePayCal.get(Calendar.YEAR), nextPossiblePayCal
				.get(Calendar.MONTH), nextPossiblePayCal
				.get(Calendar.DAY_OF_MONTH), 0, 0);
		
		// cal.setTimeInMillis(nextPossiblePayCal.getTimeInMillis());
		cal.add(Calendar.DAY_OF_MONTH, offset);
		if (offset == 0) {
			Calendar currentCal = Calendar.getInstance(TimeZone
					.getTimeZone(userTimeZone));
			if (cal.before(currentCal))
				return currentCal;
		}
		return cal;
	}
	
	//This function retuns the end date for the payment
	private Calendar createEndCal(Calendar userSelectedStartCal) {
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone(userTimeZone));
		cal.set(userSelectedStartCal.get(Calendar.YEAR), userSelectedStartCal
				.get(Calendar.MONTH), userSelectedStartCal
				.get(Calendar.DAY_OF_MONTH), 23, 59);
		return cal;
	}
	
	
	public void createPayTodayPayment(UserContext userContext)
	{
		 
		System.out.println("Fetching Payees....");
		// Get all expedited enables payees for the user
		boolean expeditedPayeesAdded=viewExpeditedPaymentsEnabledPayees(userContext);
		
		//If expedited payees exist for the user
		
		if(expeditedPayeesAdded==true){ 
			
			//Ask the user to enter the payee id for which he wants to crete the pay today payment.
			
			System.out.println("Enter PayeeId:");
			long payeeId = IOUtils.readLong();
			
			//Get all the bank payment accounts.
			
			System.out.println("Fetching Bank Payment Accounts....");
			BillPayPaymentAccountManagement payit=new BillPayPaymentAccountManagement();
			payit.viewPayItAllPaymentAccountsDetails(userContext,BillPayConstants.ACTIVE_AND_VERIFIED_PAYM_ACCTS);
			
			//Take user input for the preferred payment acct.
			
			System.out.println("Enter PaymentAccountId:");
			int paymentAccountId = IOUtils.readInt();
			
			ExpeditedPaymentRequest expeditedPaymRequest = new ExpeditedPaymentRequest();            
			
			// Create the expedited payment object
			// Get the expedited fee ACL
			String expeditedFee=null;
			try {
				expeditedFee = aclService.getAclValue(userContext, "BILLPAY_FEES_EXPEDITED");
			} catch (StaleConversationCredentialsExceptionFault e1) {
				e1.printStackTrace();
			} catch (InvalidConversationCredentialsExceptionFault e1) {
				e1.printStackTrace();
			} catch (CoreExceptionFault e1) {
				e1.printStackTrace();
			} catch (IllegalArgumentValueExceptionFault e1) {
				e1.printStackTrace();
			} catch (InvalidUserContextExceptionFault e1) {
				e1.printStackTrace();
			} catch (RemoteException e1) {
				e1.printStackTrace();
			}
			if (expeditedFee == null)
			{
				expeditedFee = "0.0";
			}
			BigDecimal fee = new BigDecimal(expeditedFee);
	        Money payTodayFee = new Money();
	        payTodayFee.setAmount(fee);
	        payTodayFee.setCurrencyCode("USD");

			//Create the Expedited Payment Request Object
			expeditedPaymRequest.setExpeditedPaymentFee((Money)payTodayFee);
			expeditedPaymRequest.setPayeeId(new Long(payeeId));
			PaymentAccountId paymentAccountIdObj = new PaymentAccountId();
			paymentAccountIdObj.setPaymentAccountId(new Long(paymentAccountId));			
			expeditedPaymRequest.setPaymentAccountIdentifier(paymentAccountIdObj);
			
			System.out.println("Enter Payment Amount:");
			String paymentAmount = IOUtils.readStr();
	        Money money = new Money();
	        money.setAmount(new BigDecimal(paymentAmount));
	        money.setCurrencyCode("USD");

			expeditedPaymRequest.setPaymentAmount(money);
			
			expeditedPaymRequest.setPaymentMemo("PaymentTest");
			expeditedPaymRequest.setPostOnNextBusinessDate(false);
			expeditedPaymRequest.setFrequency(com.yodlee.soap.core.paymentservice.Frequency.ONE_TIME);
			expeditedPaymRequest.setDateType(DateType.RISK_DUE_DATE);
			
			//Get the payment start date and end date.We get the next possible payment date if it's
			//a future payment else it returns the today's date only.
			Calendar nextPossiblePayCal = getNextPossiblePaymentCal(userContext,payeeId);
			
			Calendar userSelectedStartCal = createStartCal(nextPossiblePayCal,
					0);
			//Setting the end date
			Calendar userSelectedEndCal = createEndCal(userSelectedStartCal);
			
			expeditedPaymRequest.setStartDate(userSelectedStartCal);
			expeditedPaymRequest.setEndDate(userSelectedEndCal);
			
			//Create the Payment request
			
			PaymentRequest paymentRequest = expeditedPaymRequest;
			try
			{
				//Create payment
				Long paymentRequestId = paymentService.createPayment(userContext, paymentRequest);
				System.out.println("*****Payment Request Id : " + paymentRequestId +"\n\n");
				//Schedule Payment
				String paymentTxnId = paymentService.schedulePayment1(userContext, paymentRequestId);
				System.out.println("***** Payment Transaction Id : " + paymentTxnId +"\n\n");
			}catch(InvalidConversationCredentialsExceptionFault icce){
				System.out.println("Invalid Conversation Credentials");
			}catch(InvalidUserContextExceptionFault iuce){
				System.out.println("Invalid User Context");
			}catch(ItemAccountDisabledExceptionFault iade){
				System.out.println("Item Account is disabled");
			}catch(InvalidPaymentAmountExceptionFault ipae){
				System.out.println("Payment amount is invalid");
			}catch(PayeeCurrentlyNotSupportedForEBillsExceptionFault ipae){
				System.out.println("Payee not supported for ebills");
			}catch(EBillPaymentNotSupportedExceptionFault ebnse){
				System.out.println("EBill payment not supported for the payee");
			}catch(InvalidPaymentAccountIdExceptionFault ipae){
				System.out.println("PaymentAccount Invalid");
			}catch(InvalidDateTypeExceptionFault idte){
				System.out.println("PaymentAccount Invalid");
			}catch(PaymentNotSupportedExceptionFault pnse){
				System.out.println("PaymentNot supported Invalid");
			}catch(InvalidPaymentDateExceptionFault ipde){
				System.out.println("Payment Date Invalid");
			}catch(PayeeStatusNotValidExceptionFault psie){
				System.out.println("Payee Status  Invalid");
			}catch(InvalidPayeeIdExceptionFault ipe){
				System.out.println("Payee Status  Invalid");
			}catch(ExpeditedPaymentNotAllowedExceptionFault epnaee){
				System.out.println("ExpeditedPaymentNotAllowed" + epnaee.getMessage());
			}catch(Exception e){
				System.out.println("Exception occured" + e.getMessage());
			}
		}
		else{ 
			System.out.println("No Payee with Expedited Payment Enabled");
		}
		
	}
	
	public void viewPayTodayPayment(UserContext userContext){
		
		//Filter payments by payment mode for the user
		ArrayOfPaymentMode arrayOfPaymentMode = new ArrayOfPaymentMode();
		arrayOfPaymentMode.setElements(new PaymentMode[]{PaymentMode.EXPEDITED_PAYMENT});
		PaymentFilterByPaymentMode paymfilter = new PaymentFilterByPaymentMode();
		paymfilter.setPaymentMode(arrayOfPaymentMode);

		ArrayOfPayment payTodayPaymentArray = null;
		Payment[] payTodayPayments =null;
		try{
			//Get Expedited Payments for the user
			PaymentFilter paymentFilter =new PaymentFilter();
			paymentFilter.setByPaymentMode(paymfilter);
			payTodayPaymentArray =paymentService.getPayments3 (userContext,paymentFilter);
			
			if (payTodayPaymentArray != null){
				payTodayPayments = payTodayPaymentArray.getElements();
			}
			
		}catch(InvalidUserContextExceptionFault iuce){
			System.out.println("Invalid User Context");
		}catch(InvalidConversationCredentialsExceptionFault icce){
			System.out.println("Invalid Conversation Credentials");
		}catch(IllegalArgumentValueExceptionFault iave){
			System.out.println("Illegal argument value");
		}catch(Exception e){
			System.out.println("Exception Occureed" +e.getMessage());
		}
		
		//If expedited payments exists print them
		if (payTodayPayments!=null && payTodayPayments.length>0){
			for (int i = 0; i < payTodayPayments.length;i++) {
				Payment pmt = payTodayPayments[i];
				if(pmt != null)
				{
					System.out.println ((i+1)+")" +"\n\n"+"Payment Details for Payee: " 
							+ pmt.getPaymentRequest().getPayeeId()+"\n"
							+ " Payment Status: " +pmt.getPaymentRequestStatus().getPaymentRequestStatusName() + "\n"
							+ " Payment Transaction Id : "+pmt.getPaymentTransactionId() + "\n"
							+ " Payment Request Date: "+ pmt.getPaymentRequestDate() + "\n"
							+ " Payment Amount :" + pmt.getPaymentRequest().getPaymentAmount());    	 
				}
			}
			
			
		}
	}
	
	public static Calendar getCurrentCalendarDate()
	{
		Calendar currentCal = Calendar.getInstance();              
		return currentCal;
	}
	public static void clearTimeOfTheDay(Calendar inCalendar)
	{
		// Get only year, month and date fields
		int year = inCalendar.get(Calendar.YEAR);
		int month = inCalendar.get(Calendar.MONTH);
		int date = inCalendar.get(Calendar.DATE);
		// clear all the fields
		inCalendar.clear();
		// Set only the year, month and date fields
		inCalendar.set(year, month, date);
	}
}
