/**
 * Copyright 2009 Yodlee, Inc.  All Rights Reserved.  Your use of this code 
 * requires a license from Yodlee.  Any such license to this code is 
 * restricted to evaluation/illustrative purposes only. It is not intended 
 * for use in a production environment, and Yodlee disclaims all warranties 
 * and/or support obligations concerning this code, regardless of the terms 
 * of any other agreements between Yodlee and you."
 */
package com.yodlee.sampleapps;

import java.math.BigDecimal;
import java.util.List;
import java.util.Calendar;
import java.util.Date;
import java.rmi.RemoteException;

import com.yodlee.soap.collections.core.paymentservice.ArrayOfPaymentAccount;
import com.yodlee.soap.collections.core.verification.cd.ArrayOfChallengeMoney;
import com.yodlee.soap.collections.core.verification.iav.ArrayOfInstantAccountVerificationMatchData;
import com.yodlee.soap.common.ContentServiceInfo;
import com.yodlee.soap.common.Money;
import com.yodlee.soap.common.NVPair;
import com.yodlee.soap.common.UserContext;
import com.yodlee.soap.core.IllegalArgumentValueExceptionFault;
import com.yodlee.soap.core.CoreExceptionFault;
import com.yodlee.soap.core.InvalidCobrandContextExceptionFault;
import com.yodlee.soap.core.InvalidConversationCredentialsExceptionFault;
import com.yodlee.soap.core.IncompleteArgumentExceptionFault;
import com.yodlee.soap.core.StaleConversationCredentialsExceptionFault;
import com.yodlee.soap.core.InvalidCobrandConversationCredentialsExceptionFault;
import com.yodlee.soap.core.InvalidItemExceptionFault;
import com.yodlee.soap.core.InvalidUserContextExceptionFault;
import com.yodlee.soap.core.accountmanagement.itemmanagement.ItemManagement;
import com.yodlee.soap.core.accountmanagement.itemmanagement.ItemManagementService;
import com.yodlee.soap.core.accountmanagement.itemmanagement.ItemManagementServiceLocator;
import com.yodlee.soap.core.dataservice.dataservice.DataService;
import com.yodlee.soap.core.dataservice.dataservice.DataServiceService;
import com.yodlee.soap.core.dataservice.dataservice.DataServiceServiceLocator;
import com.yodlee.soap.core.fundstransfer.transferaccountmanagement.DFIAccount;
import com.yodlee.soap.core.fundstransfer.transferaccountmanagement.TransferAccountNotFoundExceptionFault;
import com.yodlee.soap.core.itemaccountmanagement.itemaccountmanagement.ItemAccountManagement;
import com.yodlee.soap.core.itemaccountmanagement.itemaccountmanagement.ItemAccountManagementService;
import com.yodlee.soap.core.itemaccountmanagement.itemaccountmanagement.ItemAccountManagementServiceLocator;
import com.yodlee.soap.core.itemaccountmanagement.ItemDetails;
 import com.yodlee.soap.core.itemaccountmanagement.exceptions.AccountAlreadyEnabledExceptionFault;
import com.yodlee.soap.core.paymentservice.BankPaymentAccount;
import com.yodlee.soap.core.paymentservice.PaymentAccountType;
import com.yodlee.soap.core.paymentservice.InvalidPaymentAccountIdExceptionFault;
import com.yodlee.soap.core.paymentservice.InvalidPaymentAccountStateChangeExceptionFault;
import com.yodlee.soap.core.paymentservice.InvalidPaymentAccountStateExceptionFault;
import com.yodlee.soap.core.paymentservice.payeemanagement.PayeeManagementService;
import com.yodlee.soap.core.paymentservice.payeemanagement.PayeeManagementServiceLocator;
import com.yodlee.soap.core.paymentservice.PaymentAccount;
import com.yodlee.soap.core.paymentservice.PaymentAccountActivationState;
import com.yodlee.soap.core.paymentservice.PaymentAccountAlreadyExistsExceptionFault;
import com.yodlee.soap.core.paymentservice.PaymentAccountFilter;
import com.yodlee.soap.core.paymentservice.PaymentAccountId;
import com.yodlee.soap.core.paymentservice.PaymentAccountIdentifier;
import com.yodlee.soap.core.paymentservice.paymentaccountmanagement.PaymentAccountManagementService;
import com.yodlee.soap.core.paymentservice.paymentaccountmanagement.PaymentAccountManagement;
import com.yodlee.soap.core.paymentservice.paymentaccountmanagement.PaymentAccountManagementServiceLocator;
import com.yodlee.soap.core.paymentservice.PaymentAccountNicknameAlreadyInUseExceptionFault;
import com.yodlee.soap.core.paymentservice.PaymentAccountVerificationState;
import com.yodlee.soap.core.paymentservice.TransactionsPendingForPaymentAccountExceptionFault;
import com.yodlee.soap.core.preferencemanagement.PreferenceKeyNotFoundExceptionFault;
import com.yodlee.soap.core.preferencemanagement.userpreferencemanagement.UserPreferenceManagement;
import com.yodlee.soap.core.preferencemanagement.userpreferencemanagement.UserPreferenceManagementService;
import com.yodlee.soap.core.preferencemanagement.userpreferencemanagement.UserPreferenceManagementServiceLocator;
import com.yodlee.soap.core.routingnumberservice.InvalidRoutingNumberExceptionFault;
import com.yodlee.soap.core.routingnumberservice.RoutingNumberNotFoundExceptionFault;

import com.yodlee.soap.core.routingnumberservice.routingnumberservice.RoutingNumberService;
import com.yodlee.soap.core.routingnumberservice.routingnumberservice.RoutingNumberServiceService;
import com.yodlee.soap.core.routingnumberservice.routingnumberservice.RoutingNumberServiceServiceLocator;
import com.yodlee.soap.core.transactionsearch.ItemAccountId;
import com.yodlee.soap.core.verification.challengeverificationservice.ChallengeVerificationService;
import com.yodlee.soap.core.verification.challengeverificationservice.ChallengeVerificationServiceService;
import com.yodlee.soap.core.verification.challengeverificationservice.ChallengeVerificationServiceServiceLocator;
import com.yodlee.soap.core.verification.instantaccountverificationservice.InstantAccountVerificationService;
import com.yodlee.soap.core.verification.instantaccountverificationservice.InstantAccountVerificationServiceService;
import com.yodlee.soap.core.verification.instantaccountverificationservice.InstantAccountVerificationServiceServiceLocator;
import com.yodlee.soap.core.verification.VerifiableAccount;
import com.yodlee.soap.core.verification.VerifiableAccountCategory;
import com.yodlee.soap.core.verification.verifiableaccountservice.VerifiableAccountService;
import com.yodlee.soap.core.verification.verifiableaccountservice.VerifiableAccountServiceService;
import com.yodlee.soap.core.verification.verifiableaccountservice.VerifiableAccountServiceServiceLocator;
import com.yodlee.soap.core.verification.VerifiableTargetAccount;
import com.yodlee.soap.core.verification.VerificationScheme;
import com.yodlee.soap.core.verification.cd.ChallengeMoney;
import com.yodlee.soap.core.verification.cd.ChallengeResponse;
import com.yodlee.soap.core.verification.cd.ChallengeType;
import com.yodlee.soap.core.verification.cd.ChallengeVerificationStatus;
import com.yodlee.soap.core.verification.VerificationStatus;
import com.yodlee.soap.core.paymentservice.payeemanagement.PayeeManagement;
import com.yodlee.soap.core.paymentservice.payeemanagement.PayeeManagementService;
import com.yodlee.soap.core.paymentservice.payeemanagement.PayeeManagementServiceLocator;
import com.yodlee.soap.core.verification.cd.exceptions.MaximumUserResponsesExceededExceptionFault;
import com.yodlee.soap.core.verification.exceptions.IllegalVerificationStateExceptionFault;
import com.yodlee.soap.core.verification.exceptions.InvalidVerifiableAccountExceptionFault;
import com.yodlee.soap.core.verification.exceptions.VerificationNeverInitiatedExceptionFault;
import com.yodlee.soap.core.verification.exceptions.VerificationSchemeChangedExceptionFault;
import com.yodlee.soap.core.verification.iav.IAVMatchCode;
import com.yodlee.soap.core.verification.iav.InstantAccountVerificationInfo;
import com.yodlee.soap.core.verification.iav.InstantAccountVerificationMatchData;
import com.yodlee.soap.core.verification.iav.exception.InstantAccountVerificationNotSupportedExceptionFault;
import com.yodlee.sampleapps.helper.BillPayConstants;
import com.yodlee.sampleapps.helper.FormUtil;
import com.yodlee.sampleapps.helper.IFileTransferConstants;
import com.yodlee.sampleapps.helper.IOUtils;
import com.yodlee.sampleapps.helper.ValidationUtil;
import com.yodlee.soap.core.paymentservice.PaymentProfile;
//import com.yodlee.soap.core.p;
import com.yodlee.soap.core.paymentservice.paymentrules.RiskModel;
import com.yodlee.soap.core.paymentservice.BankPaymentProfile;
import com.yodlee.soap.core.paymentservice.paymentservice.PaymentService;
import com.yodlee.soap.core.paymentservice.paymentservice.PaymentServiceService;
import com.yodlee.soap.core.paymentservice.paymentservice.PaymentServiceServiceLocator;
import com.yodlee.soap.common.holidaymanager.HolidayManager;
import com.yodlee.soap.common.holidaymanager.HolidayManagerService;
import com.yodlee.soap.common.holidaymanager.HolidayManagerServiceLocator;
import com.yodlee.soap.common.HolidayInfo;
import com.yodlee.soap.common.HolidayModel;
import com.yodlee.sampleapps.ManagePayee;
import com.yodlee.soap.core.paymentservice.Payee;
import com.yodlee.soap.core.paymentservice.PayItAllProfile;
import com.yodlee.soap.common.CobrandContext;
import com.yodlee.soap.common.CobrandCredentials;


public class BillPayPaymentAccountManagement extends ApplicationSuper
{
	protected InstantAccountVerificationService instantAccountVerificationService;
	protected ChallengeVerificationService challengeVerificationService;
	protected ItemAccountManagement itemAccountManagement;
	protected PaymentAccountManagement paymentAccountManagement;
	protected UserPreferenceManagement userPref;
	protected PayeeManagement payeeManagement;
	protected ItemManagement itemManagement;
	protected DataService dataService;
	protected RoutingNumberService rns;
	protected VerifiableAccountService vas;
	private static int editBankPaymAccountOptionCount = 1;
	
	public final static int SINGLE_VALUE = 0;
	
	/** Navigation Menu Choice. **/
	private static final int  NAV_BANK_NICK_NAME = editBankPaymAccountOptionCount++;    
	/** Navigation Menu Choice. **/
	private static final int  NAV_BANK_DESCRIPTION = editBankPaymAccountOptionCount++; 
	/** Navigation Menu Choice. **/
	private static final int  NAV_ALL = editBankPaymAccountOptionCount++; 
	
	public BillPayPaymentAccountManagement (){
		super ();   
		
		//Create PaymentAccountManagement Locator 
		PaymentAccountManagementServiceLocator  paymentAccManagementServiceLocator= new PaymentAccountManagementServiceLocator();
       String paymentAccountManagementServiceName= paymentAccManagementServiceLocator.getPaymentAccountManagementServiceWSDDServiceName();
       paymentAccManagementServiceLocator.setPaymentAccountManagementServiceEndpointAddress(System.getProperty("com.yodlee.soap.services.url")
				+ "/" + paymentAccountManagementServiceName);
       try {
    	   paymentAccountManagement = paymentAccManagementServiceLocator.getPaymentAccountManagementService();

		} catch (Exception lse) {

		}
//		Create PaymentAccountManagement Locator 
		VerifiableAccountServiceServiceLocator  vasServiceLocator= new VerifiableAccountServiceServiceLocator();
       String vasServiceName= vasServiceLocator.getVerifiableAccountServiceWSDDServiceName();
       vasServiceLocator.setVerifiableAccountServiceEndpointAddress(System.getProperty("com.yodlee.soap.services.url")
				+ "/" + vasServiceName);
       try {
    	   vas = vasServiceLocator.getVerifiableAccountService();

		} catch (Exception lse) {

		}

		//Create PayeeManagement Locator 
		 PayeeManagementServiceLocator  payeeManagementServiceLocator= new PayeeManagementServiceLocator();
       String payeeManagementserviceName= payeeManagementServiceLocator.getPayeeManagementServiceWSDDServiceName();
       payeeManagementServiceLocator.setPayeeManagementServiceEndpointAddress(System.getProperty("com.yodlee.soap.services.url")
				+ "/" + payeeManagementserviceName);
       try {
       	payeeManagement = payeeManagementServiceLocator.getPayeeManagementService();

		} catch (Exception lse) {

		}

		
		//Create RoutingService Locator 
		RoutingNumberServiceServiceLocator  rnsServiceLocator= new RoutingNumberServiceServiceLocator();
       String rnsServiceName= rnsServiceLocator.getRoutingNumberServiceWSDDServiceName();
       rnsServiceLocator.setRoutingNumberServiceEndpointAddress(System.getProperty("com.yodlee.soap.services.url")
				+ "/" + rnsServiceName);
       try {
    	   rns = rnsServiceLocator.getRoutingNumberService();

		} catch (Exception lse) {

		}

		
		//Create IAV Locator 
		InstantAccountVerificationServiceServiceLocator  iavServiceLocator= new InstantAccountVerificationServiceServiceLocator();
       String iavServiceName= iavServiceLocator.getInstantAccountVerificationServiceWSDDServiceName();
       iavServiceLocator.setInstantAccountVerificationServiceEndpointAddress(System.getProperty("com.yodlee.soap.services.url")
				+ "/" + iavServiceName);
       try {
    	   instantAccountVerificationService = iavServiceLocator.getInstantAccountVerificationService();

		} catch (Exception lse) {

		}

		//Get CD Service Locator
		ChallengeVerificationServiceServiceLocator  cdServiceLocator= new ChallengeVerificationServiceServiceLocator();
	       String cdServiceName= cdServiceLocator.getChallengeVerificationServiceWSDDServiceName();
	       cdServiceLocator.setChallengeVerificationServiceEndpointAddress(System.getProperty("com.yodlee.soap.services.url")
					+ "/" + cdServiceName);
	       try {
	    	   challengeVerificationService = cdServiceLocator.getChallengeVerificationService();

			} catch (Exception lse) {

			}

		//Get userpref service locator 
		UserPreferenceManagementServiceLocator  userPrefLocator= new UserPreferenceManagementServiceLocator();
	       String userPrefServiceName= userPrefLocator.getUserPreferenceManagementServiceWSDDServiceName();
	       userPrefLocator.setUserPreferenceManagementServiceEndpointAddress(System.getProperty("com.yodlee.soap.services.url")
					+ "/" + userPrefServiceName);
	       try {
	    	   userPref = userPrefLocator.getUserPreferenceManagementService();

			} catch (Exception lse) {

			}


		//Get userpref service locator 
		ItemAccountManagementServiceLocator  itemaccLocator= new ItemAccountManagementServiceLocator();
	       String itemAcctServiceName= itemaccLocator.getItemAccountManagementServiceWSDDServiceName();
	       itemaccLocator.setItemAccountManagementServiceEndpointAddress(System.getProperty("com.yodlee.soap.services.url")
					+ "/" + itemAcctServiceName);
	       try {
	    	   itemAccountManagement = itemaccLocator.getItemAccountManagementService();

			} catch (Exception lse) {

			}

	
	
//		Get itemmanagement service locator 
		ItemManagementServiceLocator  itemmanagementLocator= new ItemManagementServiceLocator();
	       String itemManagementServiceName= itemmanagementLocator.getItemManagementServiceWSDDServiceName();
	       itemmanagementLocator.setItemManagementServiceEndpointAddress(System.getProperty("com.yodlee.soap.services.url")
					+ "/" + itemManagementServiceName);
	       try {
	    	   itemManagement = itemmanagementLocator.getItemManagementService();

			} catch (Exception lse) {

			}

		
	}
	
	/**
	 * Prompts the user to enter bankname,nickname,description,bank account number,routing number
	 * Creates a bank payment account if the routing number is valid
	 * Perform verification of the bank account either through IAV or CD
	 * 
	 * @param userContext
	 * 				The user context of the caller
	 */
	public void addBankAccount(UserContext userContext){ 
		Long contentserviceid;
		ContentServiceInfo csinfo = null;
		String bankname;
		String nickname;
		String accountnumber;
		String routingnumber;
		String description;
		int accountType;
		
		try{
			System.out.print("Enter Bank Name : ");
			bankname = IOUtils.readStr();		    		
			System.out.print("Enter Bank Nick Name : ");
			nickname = IOUtils.readStr();		    		
			System.out.print("Enter description : ");
			description = IOUtils.readStr();		    		
			System.out.print("Enter accountNumber : ");
			accountnumber = IOUtils.readStr();
			System.out.print("Enter routingNumber : ");
			routingnumber = IOUtils.readStr();
			System.out.print("Enter Account Type (1-Savings , 2-Checking) : ");
			accountType=IOUtils.readInt();		    	 		    		
			
			if(bankname==null){
				System.out.println("Please enter the financial institution");
				return;
			}else if(accountnumber==null){
				System.out.println("Please enter valid account number");
				return;
			}else if(routingnumber == null){
				System.out.println("Please enter valid routing number");
				return;
			}else if(accountType!=1 && accountType!=2){
				System.out.println("Please enter valid account Type");
				return;
			}
			
			DFIAccount dfiAccount = new DFIAccount();
			dfiAccount.setAccountNumber(accountnumber);
			dfiAccount.setRoutingNumber(routingnumber);		   		
			dfiAccount.setDfiAccountType(new Integer(accountType));
			
			try{
				csinfo= rns.getContentServiceInfoByRoutingNumber(getCobrandContext(), dfiAccount.getRoutingNumber());
			}catch(InvalidCobrandContextExceptionFault e){
				System.out.println("Invalid cobrand context");
				return;
			}catch(InvalidRoutingNumberExceptionFault e){
				System.out.println("The routingNumber provided is not valid");
				return;
			}catch(RoutingNumberNotFoundExceptionFault e){
				System.out.println("The routingNumber specified is not present in our Yodlee System");
				return;
			}catch (Exception e) {
				e.printStackTrace();
				return;
			}
			
			contentserviceid=csinfo.getContentServiceId();	
			BankPaymentAccount bankPaymentAccount = new BankPaymentAccount();
			bankPaymentAccount.setPaymentAccountType(PaymentAccountType.BANK);
			bankPaymentAccount.setContentServiceId(contentserviceid);
			bankPaymentAccount.setDescription(description);
			bankPaymentAccount.setNickname(nickname);
			bankPaymentAccount.setIsDisabled(true);
			bankPaymentAccount.setPaymentAcctSnapshotId(new Long(0));
			bankPaymentAccount.setDfiAccount(dfiAccount);
			bankPaymentAccount.setBankName(bankname);
			
		    		
			BankPaymentAccount bpa=(BankPaymentAccount)paymentAccountManagement.addPaymentAccount(userContext, bankPaymentAccount);
			
			Long paymentAccountId = ((PaymentAccountId)bpa.getPaymentAccountIdentifier()).getPaymentAccountId();
			
			System.out.println("Your Payment account has been successfully added with Payment Account id ->"+
					paymentAccountId);
			System.out.println("and Dfi Account id ->"+bpa.getDfiAccount().getDfiAccountId());

			
			String paymAcId = "" + paymentAccountId;
            //Check for preferred/default payment account
            modifyPreferredPaymentAccount(userContext, paymAcId);
			
			bankAccountVerification(userContext,csinfo,bpa);    
			
		}catch(PaymentAccountAlreadyExistsExceptionFault e){
			System.out.println("The Payment Account Already Exist");    			
		}catch(PaymentAccountNicknameAlreadyInUseExceptionFault ex){
			System.out.println("The Payment Account Nick Name Already Exist");
		}catch(Exception exceptions){
			exceptions.printStackTrace();    			
		}
	}  
	
	/**
	 * If the corresponding contentservice id of the routing number supports IAV
	 * prompts the user to either choose between IAV or CD
	 * else CD is performed  
	 * @param utx
	 * 		The user context of the caller
	 * @param csinfo
	 * 		The ContenserviceInfo 
	 * @param bankPaymentAccount
	 * 		Bank Payment Account object
	 */
	private void bankAccountVerification(UserContext utx,ContentServiceInfo csinfo,BankPaymentAccount bankPaymentAccount){
		int option=0;
		if (csinfo.isIsIAVFastSupported()) {    		 
			System.out.println("\nPlease select the verification type:");
			System.out.println("1.Instant Account Verification(IAV)");
			System.out.println("2.Challenge Deposit Verificication(CD)");
			System.out.print("\nPlease Enter your choice: ");
			option=IOUtils.readInt();             
		}
		
		if(option==1){
			System.out.println("\nPerforming IAV Verification");
			startIAV(utx,csinfo,bankPaymentAccount);
		}else if(option==2 || option==0){
			System.out.println("\nPerforming CD Verification");
			startCD(utx,bankPaymentAccount);
		}else{
			System.out.println("\nInvalid option Entered");
			System.out.println("Performing CD Verification by Default");
			startCD(utx,bankPaymentAccount);
			
		}
	}
	
	/**
	 * Initiates instant account verification
	 * @param userContext
	 * @param csinfo
	 * @param bpa
	 */
	
	private void startIAV(UserContext userContext,ContentServiceInfo csinfo,BankPaymentAccount bpa ){
		InstantAccountVerificationInfo iavi = null;
				
		Long itemaccountid =bpa.getItemAccountId();
		Long itemid =bpa.getItemId();
		DFIAccount dfis=bpa.getDfiAccount();
		linkAcctToItem(userContext,csinfo,itemaccountid,itemid);
		
		VerifiableAccount verifiableAcct=null;
		VerifiableTargetAccount targetAccount = new VerifiableTargetAccount();
		targetAccount.setTargetAccountId(dfis.getDfiAccountId());
		targetAccount.setTargetAccountCategory(VerifiableAccountCategory.DFI_ACCOUNT);
		
		try{
			verifiableAcct = vas.getVerifiableAccount(userContext, targetAccount);
		}catch (InvalidConversationCredentialsExceptionFault ic) {
			System.out.println("Invalid Cobrand Context Exception");
			System.out.println(ic.toString());
		}catch (IncompleteArgumentExceptionFault ivalue) {
			System.out.println("Given Transfer Account ID is incomplete");
			System.out.println(ivalue.toString());
		}catch (InvalidUserContextExceptionFault iu) {
			System.out.println("Invalid User Context");
			System.out.println(iu.toString());
		}catch (IllegalArgumentValueExceptionFault value) {
			System.out.println("Given Transfer Account ID is invalid");
			System.out.println(value.toString());
		}catch (StaleConversationCredentialsExceptionFault value) {
			System.out.println("Given Transfer Account ID is invalid");
			System.out.println(value.toString());
		}catch (InvalidItemExceptionFault item) {
			System.out.println("Given Item Account ID is invalid");
			System.out.println(item.toString());
		}catch (CoreExceptionFault cre) {
			System.out.println("Given Item Account ID is invalid");
			System.out.println(cre.toString());
		}catch (RemoteException re) {
			System.out.println("remoteException");
			System.out.println(re.toString());
		}
	   
		try{
		if(null == verifiableAcct){
			throw new IllegalArgumentValueExceptionFault();}
		}catch(IllegalArgumentValueExceptionFault e){
			
		}
		
		try {
			iavi = instantAccountVerificationService.startVerification(userContext,
					verifiableAcct);
		} catch (InvalidUserContextExceptionFault iu) {
			System.out.println("Invalid User Context");
			System.out.println(iu.toString());
		} catch (InvalidConversationCredentialsExceptionFault ic) {
			System.out.println("Invalid Cobrand Context Exception");
			System.out.println(ic.toString());
		} catch (InvalidVerifiableAccountExceptionFault acct) {
			System.out.println("Invalid Verifiable account");
			System.out.println(acct.toString());
		} catch (IllegalArgumentValueExceptionFault value) {
			System.out.println("value for verifiable account or transfer account is invalid");
			System.out.println(value.toString());
		} catch (InstantAccountVerificationNotSupportedExceptionFault iav) {
			System.out
			.println("Sorry! This account is not supported for Instant Verification");
			System.out.println(iav.toString());
		}catch (RemoteException re) {
			System.out.println("remote exception occured");
			System.out.println(re.toString());
		}
		System.out.println("Instant Account Verification Initiated, going to poll for refresh status");
		System.out.println("Instant Account Verification Info - Verification Status: " + iavi.getVerificationStatus().getVerificationStatusId());
		
		
	/*	FTVerificationManagement ftvermgmt=new FTVerificationManagement();
		isAcctfailed = ftvermgmt.PollNewIAVStatus(userContext,dfis);*/
		
		             
		ArrayOfInstantAccountVerificationMatchData matchDataArray = null;
		InstantAccountVerificationMatchData[] matchData = null;       	
		int attempts = 0;
		while (attempts<19 && matchData==null) {
			try {
				System.out.println("polling for IAV status");
				matchDataArray = instantAccountVerificationService.getMatchingAccountVerificationData(userContext, verifiableAcct);
				if (matchDataArray != null){
				matchData = matchDataArray.getElements();
				}
				Thread.sleep(10*1000);
				
			} catch (Exception e){
				e.printStackTrace();
				System.out.println("");
				return;
			}
			attempts++;
		}       
		if (matchData ==  null){
			System.out.println("IAV failed");
			return;
		}
		else{
			for (int index = 0; index < matchData.length; index++)
			{
				IAVMatchCode matchCode = matchData[index].getIavMatchCode();
				System.out.println("IAV matchcode value " + matchCode.getValue());
				if(IAVMatchCode.SUCCESS.equals(matchCode))
				{
					System.out.println("IAV account matching success");
				}
				else if (IAVMatchCode.MATCH_LENGTH_FAILED.equals(matchCode))	{
			
					System.out.println("IAV account matching failed in length comparision");
				}
				else if (IAVMatchCode.SIGNIFICANT_DIGITS_FAILED.equals(matchCode))	{
					
					System.out.println("IAV account matching failed in significant digit comparision");
				}
				else if (IAVMatchCode.EXACT_MATCH_FAILED.equals(matchCode))	{
					
					System.out.println("IAV account matching failed in exact match comparision");
				}
				else if (IAVMatchCode.PARTIAL_MATCH_FAILED.equals(matchCode))	{
					
					System.out.println("IAV account matching failed in partial match comparision");
				}
				else if (IAVMatchCode.ACCOUNT_TYPE_MATCH_FAILED.equals(matchCode))	{
					
					System.out.println("IAV account matching failed account type match comparision");
				}
				else if (IAVMatchCode.ACCOUNT_HOLDER_MATCH_FAILED.equals(matchCode))	{
					
					System.out.println("IAV account matching failed in account holder name comparision");
				}
				else if (IAVMatchCode.PARTIAL_MATCH_FAILED_DUE_TO_PREFIX.equals(matchCode))	{
					
					System.out.println("IAV account matching failed due to prefix");
				}
               else if (IAVMatchCode.PARTIAL_MATCH_FAILED_DUE_TO_SUFFIX.equals(matchCode))	{
					
					System.out.println("IAV account matching failed due to suffix");
				}
               else {
					System.out.println("IAV failed due to system error");
               }
				
			}
		}
		
	}
	
	/**
	 * @param userContext
	 * @param contentServiceInfo
	 * @param itemAccountID
	 * @param itemid
	 */
	private void linkAcctToItem(UserContext userContext,ContentServiceInfo contentServiceInfo,Long itemAccountID,Long itemid){
		AddItem addItem = new AddItem();
		List formComponents=null;
		try {
		formComponents = FormUtil.getUserInputFieldInfoList(userContext,itemManagement.getLoginFormForContentService(userContext, contentServiceInfo.getContentServiceId()));
		}catch(Exception e){
			
		}
		try {
			com.yodlee.soap.collections.List list = new com.yodlee.soap.collections.List();
			list.setElements(formComponents.toArray());
			ItemDetails itemDetails = new ItemDetails();
			itemDetails.setContentServiceId(contentServiceInfo.getContentServiceId());
			itemDetails.setCredentialFields(list);
			itemDetails.setShareCredentialsWithinSite(true);
			itemDetails.setStartRefreshOnAddition(false);
			itemAccountManagement.enableAccountForAggregation(userContext, itemAccountID, itemDetails);

		
		} catch (InvalidUserContextExceptionFault iu) {
			System.out.println("Invalid User Context");
			System.out.println(iu.toString());
		} catch (InvalidConversationCredentialsExceptionFault ic) {
			System.out.println("Invalid Cobrand Context Exception");
			System.out.println(ic.toString());
		} catch (InvalidItemExceptionFault item) {
			System.out.println("Given Item Account ID is invalid");
			System.out.println(item.toString());
		} catch (IllegalArgumentValueExceptionFault value) {
			System.out.println("Given Transfer Account ID is invalid");
			System.out.println(value.toString());
		}catch (AccountAlreadyEnabledExceptionFault enabledException){
			System.out.println("Some other exception");
			System.out.println(enabledException.toString());
			try{
				com.yodlee.soap.collections.List list = new com.yodlee.soap.collections.List();
				list.setElements(formComponents.toArray());
				itemManagement.updateCredentialsForItem(userContext, itemid, list);

			}catch (InvalidConversationCredentialsExceptionFault ic) {
				System.out.println("Invalid Cobrand Context Exception");
				System.out.println(ic.toString());
			}catch (IncompleteArgumentExceptionFault ivalue) {
				System.out.println("Given Transfer Account ID is incomplete");
				System.out.println(ivalue.toString());
			}catch (InvalidUserContextExceptionFault iu) {
				System.out.println("Invalid User Context");
				System.out.println(iu.toString());
			}catch (IllegalArgumentValueExceptionFault value) {
				System.out.println("Given Transfer Account ID is invalid");
				System.out.println(value.toString());
			}catch (StaleConversationCredentialsExceptionFault value) {
				System.out.println("Given Transfer Account ID is invalid");
				System.out.println(value.toString());
			}catch (InvalidItemExceptionFault item) {
				System.out.println("Given Item Account ID is invalid");
				System.out.println(item.toString());
			}catch (CoreExceptionFault cre) {
				System.out.println("Given Item Account ID is invalid");
				System.out.println(cre.toString());
			}catch (RemoteException re) {
				System.out.println("remoteException");
				System.out.println(re.toString());
			}
		}catch (RemoteException re) {
			System.out.println("remoteException");
			System.out.println(re.toString());
		}
	}
	
	/**
	 * @param userContext
	 * 			The usercontext of the caller
	 * @param bpa
	 * 		   The Bank Payment account object	
	 */
	
	private void startCD(UserContext userContext,BankPaymentAccount bpa){
		System.out.println("\nStarting cd.....");
		DFIAccount dfiAccount=bpa.getDfiAccount();
		VerifiableTargetAccount targetAccount = new VerifiableTargetAccount();
		targetAccount.setTargetAccountId(dfiAccount.getDfiAccountId());
		targetAccount.setTargetAccountCategory(VerifiableAccountCategory.DFI_ACCOUNT);

		VerifiableAccount verifiableAccount = null;
		try{
			verifiableAccount = vas.getVerifiableAccount(userContext, targetAccount); //geting the verifiable account
		}catch (StaleConversationCredentialsExceptionFault ic) {
			System.out.println("Invalid Cobrand Context Exception");
			System.out.println(ic.toString());
		}catch (InvalidUserContextExceptionFault iu) {
			System.out.println("Invalid User Context");
			System.out.println(iu.toString());
		} catch (InvalidConversationCredentialsExceptionFault ic) {
			System.out.println("Invalid Cobrand Context Exception");
			System.out.println(ic.toString());
		} catch (InvalidItemExceptionFault item) {
			System.out.println("Given Item Account ID is invalid");
			System.out.println(item.toString());
		} catch (IllegalArgumentValueExceptionFault value) {
			System.out.println("Given Transfer Account ID is invalid");
			System.out.println(value.toString());
		}catch (CoreExceptionFault icre) {
			System.out.println("Invalid Cobrand Context Exception");
			System.out.println(icre.toString());
		}
		catch (RemoteException re) {
			System.out.println("Invalid Cobrand Context Exception");
			System.out.println(re.toString());
		}
		VerificationScheme verificationScheme=null;
		try{
		verificationScheme = vas.getLatestVerificationScheme(userContext, verifiableAccount);
		
		}catch (StaleConversationCredentialsExceptionFault ic) {
			System.out.println("Invalid Cobrand Context Exception");
			System.out.println(ic.toString());
		}catch (InvalidUserContextExceptionFault iu) {
			System.out.println("Invalid User Context");
			System.out.println(iu.toString());
		} catch (InvalidConversationCredentialsExceptionFault ic) {
			System.out.println("Invalid Cobrand Context Exception");
			System.out.println(ic.toString());
		} catch (InvalidItemExceptionFault item) {
			System.out.println("Given Item Account ID is invalid");
			System.out.println(item.toString());
		} catch (IllegalArgumentValueExceptionFault value) {
			System.out.println("Given Transfer Account ID is invalid");
			System.out.println(value.toString());
		} catch(VerificationNeverInitiatedExceptionFault ve){
			System.out.println("Verification was never initiated");
		}
		catch (CoreExceptionFault icre) {
			System.out.println("Invalid Cobrand Context Exception");
			System.out.println(icre.toString());
		}catch (RemoteException re) {
			System.out.println("Invalid Cobrand Context Exception");
			System.out.println(re.toString());
		}
		if (verificationScheme == null || !verificationScheme.equals(VerificationScheme.CHALLENGE_DEPOSIT)) {
					
			try {
				challengeVerificationService.startAccountVerification(userContext, verifiableAccount);
				System.out.println("CD initiated....");
			}catch (InvalidVerifiableAccountExceptionFault e) {
				e.printStackTrace();
			}catch (IllegalArgumentValueExceptionFault e) {
				e.printStackTrace();
			}catch (StaleConversationCredentialsExceptionFault ic) {
				System.out.println("Invalid Cobrand Context Exception");
				System.out.println(ic.toString());
			}catch (InvalidUserContextExceptionFault iu) {
				System.out.println("Invalid User Context");
				System.out.println(iu.toString());
			} catch (InvalidConversationCredentialsExceptionFault ic) {
				System.out.println("Invalid Cobrand Context Exception");
				System.out.println(ic.toString());
			}catch (CoreExceptionFault e) {
				e.printStackTrace();
			}catch (RemoteException re) {
				re.printStackTrace();
			}
		}
	}
	
	/**
	 * View the details of a payment account of a user
	 * @param userContext
	 * 				The usercontext of the caller
	 * @param status
	 * 			active-to view all the active and verified acconts
	 */
	public PaymentAccount[] viewPayItAllPaymentAccountsDetails(UserContext userContext,String status){
		
		BankPaymentAccount bankpaymacc;
		DFIAccount dfiaccount;
		//	int index;
		
		PaymentAccount[] pam=viewPaymentAccountsByStatus(userContext,status);
		if(pam == null){
			System.out.println("no active/verified payment account found");
		}
		//	System.out.print("Enter the Payment Account: ");
		//long paymentAccountId = IOUtils.readLong();
		
		else {
		 for(int i=0;i<pam.length;i++){
			try{       	
				if(pam[i] instanceof BankPaymentAccount){     
				bankpaymacc = (BankPaymentAccount)pam[i];
				dfiaccount=bankpaymacc.getDfiAccount();
				
				System.out.println("\n\n  Bank Name         :  	" +  bankpaymacc.getBankName());
				System.out.println("  Account Number    :  	" +  dfiaccount.getAccountNumber());
				System.out.println("  Routing Number    :  	" +  dfiaccount.getRoutingNumber());  
				System.out.println("  Dfi Account Id    :  	" +  dfiaccount.getDfiAccountId()); 
				System.out.println("  Description       :   " +  bankpaymacc.getDescription());
				System.out.println("  Nick Name	:       "  +  bankpaymacc.getNickname());
				System.out.println("  Payment Account Id:   " + ((PaymentAccountId)bankpaymacc.getPaymentAccountIdentifier()).getPaymentAccountId());
				System.out.println("  Item Id           :   " + bankpaymacc.getItemId());
				System.out.println("  Item Account Id   :   " + bankpaymacc.getItemAccountId());
				System.out.println("  Content Serviceid :  	" + bankpaymacc.getContentServiceId());
				System.out.println("  Active Stats      :  	" + !bankpaymacc.isIsDisabled());
				System.out.println("  verification Stats :  	" + bankpaymacc.isIsVerified());
				}
			}catch(Exception ex){
				ex.printStackTrace();
			}
		 }
		 
		}
		
		return pam;
	} 
	public BankPaymentAccount getPaymentAccountFromPaymentAccountId(UserContext userContext,long PaymentAcctId ){
		try{
		ArrayOfPaymentAccount pamArray = paymentAccountManagement.getPaymentAccounts(userContext);
		}catch (InvalidVerifiableAccountExceptionFault e) {
			e.printStackTrace();
		}catch (IllegalArgumentValueExceptionFault e) {
			e.printStackTrace();
		}catch (StaleConversationCredentialsExceptionFault ic) {
			System.out.println("Invalid Cobrand Context Exception");
			System.out.println(ic.toString());
		}catch (InvalidUserContextExceptionFault iu) {
			System.out.println("Invalid User Context");
			System.out.println(iu.toString());
		} catch (InvalidConversationCredentialsExceptionFault ic) {
			System.out.println("Invalid Cobrand Context Exception");
			System.out.println(ic.toString());
		}catch (CoreExceptionFault e) {
			e.printStackTrace();
		}catch (RemoteException re) {
			re.printStackTrace();
		}
		return null;
		
	}
	
	/**
	 * View the payment accounts of a user
	 * @param uc
	 * 			The usercontext of the caller	
	 * @param status
	 * 			active-fetches all the payment accoounts that are active
	 * 					and for which the verification is successful.
	 * 			all- fetches all the payment accounts that are active,inactive,verified,unverified.
	 * 			otherwise-fetches all the payment accounts that are only active,may or may not be verified
	 * @return the payment account array.
	 */
	public PaymentAccount[] viewPaymentAccountsByStatus(UserContext uc,String status){
		PaymentAccount[] pas=null;
		ArrayOfPaymentAccount pasArray = null;
		System.out.println("Fetching the Payment Accounts....");
		PaymentAccountFilter paf=new PaymentAccountFilter();
		
		try{
			if(status.equals(BillPayConstants.ACTIVE_AND_VERIFIED_PAYM_ACCTS)){    		   
				paf.setPaymentAccountVerificationState(PaymentAccountVerificationState.VERIFICATION_FLAG_VERIFIED_ONLY);
				//paf.setPaymentAccountVerificationState(PaymentAccountVerificationState.VERIFICATION_FLAG_EITHER);
				paf.setPaymentAccountActivationState(PaymentAccountActivationState.ACTIVATION_FLAG_ACTIVE_ONLY);
				pasArray =paymentAccountManagement.getPaymentAccounts5(uc,paf);
				
			}else if(status.equals(BillPayConstants.ALL_PAYM_ACCTS) ||status.equals(BillPayConstants.All_PAYM_ACCTS_WITH_STATUS)){
				paf.setPaymentAccountVerificationState(PaymentAccountVerificationState.VERIFICATION_FLAG_EITHER);
				paf.setPaymentAccountActivationState(PaymentAccountActivationState.ACTIVATION_FLAG_EITHER);
				pasArray =paymentAccountManagement.getPaymentAccounts5(uc,paf);
				
			}else
				pasArray =paymentAccountManagement.getPaymentAccounts(uc);
			
		if (pasArray != null){
			pas = pasArray.getElements();
		}
		}catch(InvalidUserContextExceptionFault iex){
			System.out.println("The user Context is invalid");
		}catch(InvalidConversationCredentialsExceptionFault icexp){
			System.out.println("The user doesn't have a valid session");    		
		}catch(IllegalArgumentValueExceptionFault exp){
			System.out.println("The arguments passed are invalid");    		
		}catch(Exception ex){
			ex.printStackTrace();
		}
		
		if(pas!=null){
			
			if(pas.length != 0 && status.equals(BillPayConstants.All_PAYM_ACCTS_WITH_STATUS)){
				System.out.print("\nBANK ACCOUNT NUMBER");
				System.out.print("\tPAYMENT ACCOUNT ID");
				System.out.print("\tSTATUS");
				System.out.println("\n----------------------------------------------------------\n");	
				
				for(int i=0; i<pas.length; i++){
					if(pas[i] instanceof BankPaymentAccount){        		 
						BankPaymentAccount bpa = (BankPaymentAccount)pas[i];
						DFIAccount dfi = bpa.getDfiAccount();
						System.out.print(i+1 + ". "+dfi.getAccountNumber());
						System.out.print("\t\t "+ ((PaymentAccountId)bpa.getPaymentAccountIdentifier()).getPaymentAccountId());
						if(bpa.isIsDisabled())  
							System.out.print("\t\t Inactive");
						else
							System.out.print("\t\t Active");		        		  
						
						System.out.println();
					}               	
				}}
			if( pas.length == 0 ){
				System.out.println("You have no Payment Accounts");    
				pas=null;
			}
		}
		return pas;
	}
	
	
	/**
	 * Removes the selected payment account.
	 * The payment account status of the selcted payment account is set to 0
	 * @param userContext
	 * 				The usercontext of the caller
	 */
	public void removePayItAllPaymentAccount(UserContext userContext) {
		
		int index;
		
		PaymentAccount[] paymentAccount=viewPayItAllPaymentAccountsDetails(userContext,BillPayConstants.ACTIVE_PAYM_ACCTS);
		if(paymentAccount == null) return;
		System.out.print("Enter the Payment Account(Payment Account ID) you want to delete: ");
		String accountNumber = IOUtils.readStr();
		
		index=paymentAccountIndex(paymentAccount,accountNumber);
		
		try{
			
			PaymentAccountIdentifier pai=paymentAccount[index].getPaymentAccountIdentifier();         
			paymentAccountManagement.removePaymentAccount1(userContext,pai,false);
			System.out.print("The Payment Account has been successfully deleted.");
			
		}catch(ArrayIndexOutOfBoundsException aie){
			System.out.println("Incorrect choice. Please enter your account ID correctly");
		}catch(InvalidPaymentAccountStateChangeExceptionFault exception){
			System.out.println("The payment account is being used in active payments or autopay in progress");
		}catch(InvalidUserContextExceptionFault userexcep){
			System.out.println("The user context is invalid");       	
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Allows the user to change the bank name and description of a particular payment account
	 * @param userContext
	 * 			The usercontext of the caller
	 */		
	
	public void editPayItAllPaymentAccountsDetails(UserContext userContext){
		
		String banknickname;
		String bankdescription;
		int index;
		BankPaymentAccount bpaccount;
		
		PaymentAccount[] paymentAccount=viewPayItAllPaymentAccountsDetails(userContext,BillPayConstants.ACTIVE_PAYM_ACCTS);
		if(paymentAccount == null) return;
		
		System.out.print("Enter the  Payment Account(Payment Account ID) you want to edit: ");
		String accountNumber = IOUtils.readStr();
		
		index=paymentAccountIndex(paymentAccount,accountNumber);
		
		try{  
			bpaccount=(BankPaymentAccount)paymentAccount[index];
			
			try {
				System.out.println("Enter the items you want to edit");
				System.out.println("*********************************");
				System.out.println(NAV_BANK_NICK_NAME + ". Nickname");
				System.out.println(NAV_BANK_DESCRIPTION + ". Description");
				System.out.println(NAV_ALL + ". All");
				System.out.println("**********************************");
				System.out.print("Choice: " );   		
				int number = IOUtils.readInt();
				
				if (!(number == NAV_BANK_NICK_NAME||number == NAV_BANK_DESCRIPTION||number == NAV_ALL)){
					System.out.println("Enter a valid number from 1-3");
					return;
				}             		 
				if(number == NAV_BANK_NICK_NAME || number == NAV_ALL){
					System.out.print("Enter the bank nick name: ");
					banknickname = IOUtils.readStr();
					bpaccount.setNickname(banknickname);
				}
				if(number == NAV_BANK_DESCRIPTION || number == NAV_ALL){
					System.out.print("Enter the bank description name: ");
					bankdescription = IOUtils.readStr();
					bpaccount.setDescription(bankdescription);
				}
				
				paymentAccountManagement.updatePaymentAccount(userContext,bpaccount);
				System.out.println("Your Bank details are updated successfully....");
				
			}catch (ArrayIndexOutOfBoundsException arr) {
				System.out.println("Incorrect choice. Please enter a valid Payment Account ID.");
			}catch(InvalidUserContextExceptionFault e){
				System.out.println("The user context is invalid");
			}catch(InvalidConversationCredentialsExceptionFault ie){
				System.out.println("The specified user does not have a valid session.");
			}catch(InvalidPaymentAccountIdExceptionFault e){
				System.out.println("The payment account ID does not exist or does not belong to the user.");
			}catch(PaymentAccountNicknameAlreadyInUseExceptionFault exp){
				System.out.println("Nickname is already assigned to another payment account.");
			}catch(IllegalArgumentValueExceptionFault e){
				System.out.println("The PaymentAccount has invalid values for the user.");
			}
		}catch(ArrayIndexOutOfBoundsException ex){
			System.out.println("Incorrect choice. Please enter your account ID correctly.");        	
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * If no default payment account exist for a user,sets one.
	 * Or,allows the user to change the payment account that is to be used by default,if the user 
	 * already has one.
	 * @param userContext
	 * 			The usercontext of the caller
	 */
	public void modifyPreferredPaymentAccount(UserContext userContext, String paymentAcctId){
		String key="BILLPAY.DEFAULT.PAYMENTACCOUNT";
		String paymentAccountId = paymentAcctId;
		try{
			try{
				NVPair nvpair = userPref.getPreference(userContext,key);
				if(nvpair!=null){
					System.out.println("The default payment account is "+ nvpair.getValues().getElements(0));
				}					
			}catch(PreferenceKeyNotFoundExceptionFault ex){
				// set the Payment Account received in the API call to the preferred Payment Account
					System.out.println("There is no default payment account set.\n");
			/**		if (null!= paymentAccountId){
					NVPair preference = new NVPair(null,key,null, paymentAccountId);
						userPref.setPreference(userContext, preference);
						System.out.println("Succesfully updated the default payment account with paymentAccountId = " + paymentAccountId);
						return;
					}**/					
			}
			
			if (null == paymentAcctId){
				// This means this method is called from modifyPreferred Payment Account
				PaymentAccount[] paymentAccount = viewPayItAllPaymentAccountsDetails(userContext,BillPayConstants.ACTIVE_AND_VERIFIED_PAYM_ACCTS);
				
				if(paymentAccount == null){
					System.out.println("\nFirst add a payment account and then set it as default payment account");
					return;
				}						
				
				System.out.print("Enter the payment account (Payment Account ID)that has to be set as default payment account: ");
				String accountNumber = IOUtils.readStr();
				
				int choice = paymentAccountIndex(paymentAccount,accountNumber);
				
				BankPaymentAccount bankpaymacc =(BankPaymentAccount)paymentAccount[choice];
				paymentAccountId = String.valueOf(((PaymentAccountId)bankpaymacc.getPaymentAccountIdentifier()).getPaymentAccountId());
			
				NVPair preference = new NVPair();
				preference.setName(key);
				preference.setType(SINGLE_VALUE);
				com.yodlee.soap.collections.List prefKeyValues = new com.yodlee.soap.collections.List();
				prefKeyValues.setElements(new Object[]{paymentAccountId});

				preference.setValues(prefKeyValues);
				userPref.setPreference(userContext, preference);
				System.out.println("Succesfully updated the default payment account with paymentAccountId = " + paymentAccountId);
			}
			
		}catch(ArrayIndexOutOfBoundsException e){
			System.out.println("Incorrect Choice");
		}catch(InvalidUserContextExceptionFault e){
			System.out.println("The user context is invalid");
		}catch(InvalidConversationCredentialsExceptionFault iexp){
			System.out.println("The specified user does not have a valid session");
		}catch(Exception exp){
			exp.printStackTrace();
		}
	}
	
	/**
	 * Verifies a transfer Account via CD. 1. Prompt user for entering deposit 1
	 * and deposit 2 2. Verify these deposits against actual deposits.
	 *
	 * @param userContext
	 *            The user context of the caller
	 */
	public void verifyCDAmount(UserContext userContext){
		
		String amount1;
		String amount2;
		double validateAmt1; 
		double validateAmt2;
		
		System.out.print("Enter the DFI Account Id: ");	   
		long dfiAcctId=IOUtils.readLong();
		amount1 = IOUtils.promptInput("Enter the Amount1: ","Re-enter the Amount1: ");          
		validateAmt1 = ValidationUtil.getValidAmount(amount1);	
		amount2 = IOUtils.promptInput("Enter the Amount2: ","Re-enter the Amount2: ");
		validateAmt2 = ValidationUtil.getValidAmount(amount2);
		VerifiableAccount verifiableAcct=null;
		if (!(validateAmt1 == 0.00 || validateAmt2 == 0.00)){ 
			VerifiableTargetAccount targetAccount = new VerifiableTargetAccount();
			targetAccount.setTargetAccountId(new Long(dfiAcctId));
			targetAccount.setTargetAccountCategory(VerifiableAccountCategory.DFI_ACCOUNT);

			try{
			verifiableAcct = vas.getVerifiableAccount(userContext, targetAccount);
			}catch (InvalidUserContextExceptionFault e) {
				e.printStackTrace();
				System.out.println("Invalid User Context...");
			} catch (StaleConversationCredentialsExceptionFault e) {
				e.printStackTrace();
				System.out.println("Invalid Cobrand Context Exception");
			} catch (InvalidConversationCredentialsExceptionFault e) {
				e.printStackTrace();
				System.out.println("Invalid Cobrand Context Exception");
			} catch (MaximumUserResponsesExceededExceptionFault e) {
				e.printStackTrace();
				System.out.println("Maximum attempts for response verification exceeded!");
			} catch (VerificationSchemeChangedExceptionFault e) {
				e.printStackTrace();
				System.out.println("The current Verification scheme is not "
						+ VerificationScheme.CHALLENGE_DEPOSIT);
			}catch(IllegalArgumentValueExceptionFault e){
				System.out.println("The PaymentAccount has invalid values for the user.");
			}catch (IllegalVerificationStateExceptionFault e) {
				e.printStackTrace();
				System.out.println("User cannot provide response for the Challenge "
						+ "Request when it is still in Initiated state.");
			}catch (TransferAccountNotFoundExceptionFault e) {
				e.printStackTrace();
				System.out.println("Exception happened while fetching account!");
			}catch(RemoteException re){
				re.printStackTrace();
				System.out.println("Remote Exception happened while fetching account!");
			}
			try{
			if(null == verifiableAcct){
				System.out.println("VerifiableAccount is NULL for TargetAccountId : " 
						+targetAccount.getTargetAccountId() + " It should never happen");
				throw new IllegalArgumentValueExceptionFault("");}
			}catch(IllegalArgumentValueExceptionFault e){
				System.out.println("VerifiableAccount is NULL for TargetAccountId : " 
						+targetAccount.getTargetAccountId() + " It should never happen");
				
			}
			}
			ChallengeMoney[] responseAmounts = new ChallengeMoney[2];
			Money money1 = new Money();
			money1.setAmount(new BigDecimal(amount1));
			money1.setCurrencyCode(IFileTransferConstants.CURR_CODE);
			ChallengeMoney challengeMoney1 = new ChallengeMoney();
			challengeMoney1.setMoney(money1);
			challengeMoney1.setTransactionType(ChallengeType.CREDIT);
			responseAmounts[0] = challengeMoney1;
			//responseAmounts[0] = new ChallengeMoney(money1, ChallengeType.CREDIT);
			Money money2 = new Money();
			money2.setAmount(new BigDecimal(amount2));
			money2.setCurrencyCode(IFileTransferConstants.CURR_CODE);
			ChallengeMoney challengeMoney2 = new ChallengeMoney();
			challengeMoney2.setMoney(money2);
			challengeMoney2.setTransactionType(ChallengeType.CREDIT);
			responseAmounts[1] = challengeMoney2;
			
			ArrayOfChallengeMoney arrayOfChallengeMoney = new ArrayOfChallengeMoney();
			arrayOfChallengeMoney.setElements(responseAmounts);
			ChallengeResponse challengeResponse = new ChallengeResponse();
			challengeResponse.setAmounts(arrayOfChallengeMoney);
			
			try {
				VerificationStatus challengeVerificationStatus = challengeVerificationService.processUserResponse(userContext, verifiableAcct,challengeResponse);
				
				if (challengeVerificationStatus.getVerificationStatusId().intValue() == 4) {
					System.out.println("Account verified sucessfully");	    		
				}  else if (challengeVerificationStatus.getVerificationStatusId().intValue() == 3) {
					System.out.println("The account you added could not be verified for transfers "
							+ "because you entered the wrong deposit amounts too many times.");
				} else if (challengeVerificationStatus.getVerificationStatusId().intValue() == 2) {
							System.out.println("One or more deposit amounts was incorrect. "
							+ "Please confirm that you have entered the correct deposit amounts. \n"
							+ "If you have not received these deposits yet, please return in "
							+ "1-2 days to complete verification - if you enter the wrong amount three times, "
							+ "this account will be disabled for transfers.");
				} else {
					System.out.println("Unexpected Challenge Verification Status");
				}
				
			} catch (InvalidUserContextExceptionFault e) {
				e.printStackTrace();
				System.out.println("Invalid User Context...");
			} catch (InvalidConversationCredentialsExceptionFault e) {
				e.printStackTrace();
				System.out.println("Invalid Cobrand Context Exception");
			} catch (StaleConversationCredentialsExceptionFault e) {
				e.printStackTrace();
				System.out.println("Invalid Cobrand Context Exception");
			} catch (MaximumUserResponsesExceededExceptionFault e) {
				e.printStackTrace();
				System.out.println("Maximum attempts for response verification exceeded!");
			} catch (VerificationSchemeChangedExceptionFault e) {
				e.printStackTrace();
				System.out.println("The current Verification scheme is not "
						+ VerificationScheme.CHALLENGE_DEPOSIT);
			} catch (IllegalVerificationStateExceptionFault e) {
				e.printStackTrace();
				System.out.println("User cannot provide response for the Challenge "
						+ "Request when it is still in Initiated state.");
			} catch (TransferAccountNotFoundExceptionFault e) {
				e.printStackTrace();
				System.out.println("Exception happened while fetching account!");
			}catch (RemoteException re) {
				re.printStackTrace();
				System.out.println("Exception happened while fetching account!");
			}
		}
	
	
	/**
	 * Activates or deactivates a payment account
	 * @param userCtx
	 * 			The usercontext of the caller
	 */
	public void updatePaymentAccountStatus(UserContext userCtx){
		boolean updateFlag = false;
		String accountNumber;     
		int index;
		
		try {    	 
			PaymentAccount[] pam=viewPaymentAccountsByStatus(userCtx,BillPayConstants.All_PAYM_ACCTS_WITH_STATUS);
			if(pam == null) return;
			
			System.out.print("Enter the payment account(Payment Account ID) you want to activate or deactivate: ");
			accountNumber=IOUtils.readStr();
			index=paymentAccountIndex(pam,accountNumber);
			PaymentAccountId paymentAccountId = new PaymentAccountId();
			paymentAccountId.setPaymentAccountId(((PaymentAccountId)pam[index].getPaymentAccountIdentifier()).getPaymentAccountId());
			PaymentAccountIdentifier pamyAccId= paymentAccountId;
			//PaymentAccountIdentifier pamyAccId=new PaymentAccountId(((PaymentAccountId)pam[index].getPaymentAccountIdentifier()).getPaymentAccountId());
			System.out.println("1. Activate");
			System.out.println("2. Deactivate");
			System.out.print("Please enter your choice: ");
			int choice = IOUtils.readInt();       
			
			if (choice == 1)
				updateFlag = true;
			else
				updateFlag = false;
			
			paymentAccountManagement.updatePaymentAccountStatus(userCtx, pamyAccId, updateFlag);
			System.out.print("The Payment Account Status has been successfully updated.");
			
		}catch(ArrayIndexOutOfBoundsException outofbound){
			System.out.println("Incorrect Choice. Please enter your Payment Account ID correctly");
			return;
		}catch(TransactionsPendingForPaymentAccountExceptionFault texp){
			System.out.println("There are assoiciated Scheduled/Recurring Payments associated with the payment account");
		}catch(InvalidPaymentAccountStateExceptionFault ipexp){
			if(updateFlag==true)
				System.out.println("The payment account is already active");
			else if(updateFlag==false)
				System.out.println("The payment account is already inactive");
		}catch(InvalidUserContextExceptionFault e){
			System.out.println("The user context is invalid");
		}catch(InvalidConversationCredentialsExceptionFault e){
			System.out.println("The user's session is invalid or timed out");
		}catch(InvalidPaymentAccountIdExceptionFault e){
			System.out.println("The the payment account ID does not exist or does not belong to the user");
		}catch(RemoteException re){
			System.out.println("The the payment account ID does not exist or does not belong to the user");
		}
	}
	
	private int paymentAccountIndex(PaymentAccount [] pam,String accountNumber){
		BankPaymentAccount bankpaymacc=null;
		int index=-1;
		Long paymentAccountID ;
				
		for(int i=0;i<pam.length;i++){
			if(pam[i] instanceof BankPaymentAccount){     
				bankpaymacc = (BankPaymentAccount)pam[i];				
				paymentAccountID = ((PaymentAccountId)bankpaymacc.getPaymentAccountIdentifier()).getPaymentAccountId();
				if(String.valueOf(paymentAccountID).equals(accountNumber)){
					index=i;
					break;
				}        		        	
			}
		}		
		return index;
	}
	
}