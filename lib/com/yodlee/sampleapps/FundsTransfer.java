package com.yodlee.sampleapps;

import java.rmi.RemoteException;
import java.util.List;

import com.yodlee.soap.collections.core.routingnumberservice.ArrayOfRoutingNumberInfo;
import com.yodlee.soap.common.CobrandContext;
import com.yodlee.soap.common.UserContext;
import com.yodlee.soap.core.CoreExceptionFault;
import com.yodlee.soap.core.IllegalArgumentValueExceptionFault;
import com.yodlee.soap.core.InvalidCobrandContextExceptionFault;
import com.yodlee.soap.core.InvalidCobrandConversationCredentialsExceptionFault;
import com.yodlee.soap.core.InvalidConversationCredentialsExceptionFault;
import com.yodlee.soap.core.StaleConversationCredentialsExceptionFault;
import com.yodlee.soap.core.InvalidUserContextExceptionFault;
import com.yodlee.soap.core.dataservice.dataservice.DataService;
import com.yodlee.soap.core.dataservice.dataservice.DataServiceServiceLocator;
import com.yodlee.soap.core.fundstransfer.transferaccountmanagement.transferaccountmanagement.TransferAccountManagement;
import com.yodlee.soap.core.fundstransfer.transferaccountmanagement.transferaccountmanagement.TransferAccountManagementServiceLocator;
import com.yodlee.soap.core.fundstransfer.transfermanagement.transfermanagement.TransferManagement;
import com.yodlee.soap.core.fundstransfer.transfermanagement.transfermanagement.TransferManagementServiceLocator;
import com.yodlee.soap.core.refresh.refresh.Refresh;
import com.yodlee.soap.core.refresh.refresh.RefreshServiceLocator;
import com.yodlee.soap.core.routingnumberservice.RoutingNumberInfo;
import com.yodlee.soap.core.routingnumberservice.routingnumberservice.RoutingNumberService;
import com.yodlee.soap.core.routingnumberservice.routingnumberservice.RoutingNumberServiceServiceLocator;
import com.yodlee.soap.core.verification.challengeverificationservice.ChallengeVerificationService;
import com.yodlee.soap.core.verification.challengeverificationservice.ChallengeVerificationServiceServiceLocator;
import com.yodlee.soap.core.verification.fiverificationservice.FIVerificationService;
import com.yodlee.soap.core.verification.fiverificationservice.FIVerificationServiceServiceLocator;
import com.yodlee.soap.core.verification.instantaccountverificationservice.InstantAccountVerificationService;
import com.yodlee.soap.core.verification.instantaccountverificationservice.InstantAccountVerificationServiceServiceLocator;
import com.yodlee.soap.core.verification.VerifiableAccount;
import com.yodlee.soap.core.verification.VerifiableAccountCategory;
import com.yodlee.soap.core.verification.verifiableaccountservice.VerifiableAccountService;
import com.yodlee.soap.core.verification.verifiableaccountservice.VerifiableAccountServiceServiceLocator;
import com.yodlee.soap.core.verification.VerifiableTargetAccount;
import com.yodlee.soap.core.verification.exceptions.NoSuchVerifiableTargetAccountExceptionFault;
import com.yodlee.soap.core.verification.exceptions.VerifiableAccountAlreadyAssociatedExceptionFault;
import com.yodlee.soap.core.verification.extendedinstantaccountverificationservice.ExtendedInstantAccountVerificationService;
import com.yodlee.soap.core.verification.extendedinstantaccountverificationservice.ExtendedInstantAccountVerificationServiceServiceLocator;
import com.yodlee.sampleapps.helper.IOUtils;
import com.yodlee.sampleapps.helper.TransferLimitsUtil;

/**
 * Funds Transfer (FT) Sample Code. This class provides the starting point for
 * all the FT Related Sample Apps. The main functionality of this class is to
 * display a menu to the user and the perform the respective activity based on
 * the user's input chossen from the menu.
 */
public class FundsTransfer extends ApplicationSuper {
	/** Contains the RoutingNumberService  */
	protected RoutingNumberService rns;

	/** Contains the TransferAccountManagement  */
	protected TransferAccountManagement transferAccountManagement;

	/** Contains the TransferManagement  */
	protected TransferManagement transferManagement;
	
	protected FIVerificationService fiVerificationService;

	/** Contains the VerifiableAccountService  */
	protected VerifiableAccountService verifiableAccountService;

	/** Contains the ChallengeVerificationService */
	protected ChallengeVerificationService challengeVerificationService;

	/** Contains the InstantAccountVerificationService  */
	protected InstantAccountVerificationService instantAccountVerificationService;
	
	/** Contains the ExtendedInstantAccountVerificationService proxy. */
	protected ExtendedInstantAccountVerificationService extendedInstantAccountVerificationService;

	/** Contains the DataService  */
	protected DataService dataService;
	
	/** Details for Refresh **/
	protected Refresh refresh;

	/** Navigation Counter. * */
	private static int optionCount = 1;

	/** Navigation Menu Choice. * */
	private static final int NAV_ADD_ACCT = optionCount++;

	/** Navigation Menu Choice. * */
	private static final int NAV_UPDATE_ACCT = optionCount++;

	/** Navigation Menu Choice. * */
	private static final int NAV_ENABLE_ACCT = optionCount++;

	/** Navigation Menu Choice. * */
	private static final int NAV_SHOW_TRANS_ACCT = optionCount++;

	/** Navigation Menu Choice. * */
	private static final int NAV_REMOVE_ACCT = optionCount++;

	/** Navigation Menu Choice. * */
	private static final int NAV_INITIATE_IAV = optionCount++;

	/** Navigation Menu Choice. * */
	private static final int NAV_INITITATE_CD = optionCount++;

	/** Navigation Menu Choice. * */
	private static final int NAV_VERIFY_CD = optionCount++;

	/** Navigation Menu Choice. * */
	private static final int NAV_MARK_VERIFIED = optionCount++;

	
	/** Navigation Menu Choice. * */
	private static final int NAV_DISP_VERIFICATION_INFO = optionCount++;

    /** Navigation Menu Choice. * */
	private static final int NAV_SHOW_ALL_VERIFICATIONS = optionCount++;

    /** Navigation Menu Choice. * */
	private static final int NAV_DISP_TRANSFER_LIMITS = optionCount++;

	/** Navigation Menu Choice. * */
	private static final int NAV_MAKE_TRANSFER = optionCount++;

	/** Navigation Menu Choice. * */
	private static final int NAV_SHOW_TRANS_HISTORY = optionCount++;

	/** Navigation Menu Choice. * */
	private static final int NAV_SHOW_PENDING_TRANSFERS = optionCount++;

	/** Navigation Menu Choice. * */
	private static final int NAV_SHOW_PROCESSED_TRANSFERS = optionCount++;

    /** Navigation Menu Choice. * */
	private static final int NAV_CANCEL_TRANSFER = optionCount++;

	/** Navigation Menu Choice. * */
	private static final int NAV_RTNG_NUMBERS = optionCount++;
	
	private static final int NAV_RTNG_INFO_RTN_NUMBER = optionCount++;

	/** Navigation Menu Choice. * */
	private static final int NAV_QUIT = 0;

	/**
	 * Meta data Id for Fidelity Brokerage Account Required for checking if
	 * Brokerage account is Fidelity Brokerage Account If account number begins
	 * with X, Y or Z, put “0” in this space; all else, put “1”, followed by 9 –
	 * digit Fidelity Brokerage account number
	 */
	public static int FIDELITY_BROK_METADATA_ID = 4;

	/** Details for Brokerage Account: Bank Name */
	protected String bankName;

	/** Details for Brokerage Account: Bank Routing Number */
	protected String routingNumber;

	/** Details for Brokerage Account: Bank Account Number */
	protected String accountNumber;

	/** Details for Brokerage Account: ReType Account Number */
	protected String reTypeAccountNumber;

	/** Details for Brokerage Account: All Brokerage Account list */
	protected List allBrokerageAccountNames;

	/** Details for Brokerage Account: Brokerage Name */
	protected String brokerageName;

	/** Default Constructor to initialize all the proxies */
	public FundsTransfer() {
//		Create RoutingNumberService Locator 
		RoutingNumberServiceServiceLocator routingNumberServiceServiceLocator = new RoutingNumberServiceServiceLocator();
		String routingNumberServiceServiceName = routingNumberServiceServiceLocator.getRoutingNumberServiceWSDDServiceName();
		routingNumberServiceServiceLocator.setRoutingNumberServiceEndpointAddress(System.getProperty("com.yodlee.soap.services.url") 
				+ "/" + routingNumberServiceServiceName);
		try{
			rns = routingNumberServiceServiceLocator.getRoutingNumberService();
		}catch(Exception lse) {

			}
				
			
//		Create TransferAccountManagement Locator 
		TransferAccountManagementServiceLocator transferAccountManagementServiceLocator = new TransferAccountManagementServiceLocator();
		String transferAccountManagementServiceName = transferAccountManagementServiceLocator.getTransferAccountManagementServiceWSDDServiceName();
		transferAccountManagementServiceLocator.setTransferAccountManagementServiceEndpointAddress(System.getProperty("com.yodlee.soap.services.url") 
				+ "/" + transferAccountManagementServiceName);
		try{
			transferAccountManagement = transferAccountManagementServiceLocator.getTransferAccountManagementService();
		}catch(Exception lse) {

			}		
		
		/** Initialize the FI Verification service  */
//		Create FIVerificationService Locator 
		FIVerificationServiceServiceLocator fIVerificationServiceServiceLocator = new FIVerificationServiceServiceLocator();
		String fIVerificationServiceServiceName = fIVerificationServiceServiceLocator.getFIVerificationServiceWSDDServiceName();
		fIVerificationServiceServiceLocator.setFIVerificationServiceEndpointAddress(System.getProperty("com.yodlee.soap.services.url") 
				+ "/" + fIVerificationServiceServiceName);
		try{
			fiVerificationService = fIVerificationServiceServiceLocator.getFIVerificationService();
		}catch(Exception lse) {

			}
		
		/** Initialize the TransferManagement  */
//		Create TransferManagement Locator 
		TransferManagementServiceLocator transferManagementServiceLocator = new TransferManagementServiceLocator();
		String transferManagementServiceName = transferManagementServiceLocator.getTransferManagementServiceWSDDServiceName();
		transferManagementServiceLocator.setTransferManagementServiceEndpointAddress(System.getProperty("com.yodlee.soap.services.url") 
				+ "/" + transferManagementServiceName);
		try{
			transferManagement = transferManagementServiceLocator.getTransferManagementService();
		}catch(Exception lse) {

			}

		/** Initialize the VerifiableAccountService  */
//		Create VerifiableAccountService Locator 
		VerifiableAccountServiceServiceLocator verifiableAccountServiceServiceLocator = new VerifiableAccountServiceServiceLocator();
		String verifiableAccountServiceServiceName = verifiableAccountServiceServiceLocator.getVerifiableAccountServiceWSDDServiceName();
		verifiableAccountServiceServiceLocator.setVerifiableAccountServiceEndpointAddress(System.getProperty("com.yodlee.soap.services.url") 
				+ "/" + verifiableAccountServiceServiceName);
		try{
			verifiableAccountService = verifiableAccountServiceServiceLocator.getVerifiableAccountService();
		}catch(Exception lse) {

			}

		/** Initialize the ChallengeVerificationService  */
//		Create ChallengeVerificationService Locator 
		ChallengeVerificationServiceServiceLocator challengeVerificationServiceServiceLocator = new ChallengeVerificationServiceServiceLocator();
		String challengeVerificationServiceServiceName = challengeVerificationServiceServiceLocator.getChallengeVerificationServiceWSDDServiceName();
		challengeVerificationServiceServiceLocator.setChallengeVerificationServiceEndpointAddress(System.getProperty("com.yodlee.soap.services.url") 
				+ "/" + challengeVerificationServiceServiceName);
		try{
			challengeVerificationService = challengeVerificationServiceServiceLocator.getChallengeVerificationService();
		}catch(Exception lse) {

			}

		/** Initialize the InstantAccountVerificationService  */
//		Create InstantAccountVerificationService Locator 
		InstantAccountVerificationServiceServiceLocator instantAccountVerificationServiceServiceLocator = new InstantAccountVerificationServiceServiceLocator();
		String instantAccountVerificationServiceServiceName = instantAccountVerificationServiceServiceLocator.getInstantAccountVerificationServiceWSDDServiceName();
		instantAccountVerificationServiceServiceLocator.setInstantAccountVerificationServiceEndpointAddress(System.getProperty("com.yodlee.soap.services.url") 
				+ "/" + instantAccountVerificationServiceServiceName);
		try{
			instantAccountVerificationService = instantAccountVerificationServiceServiceLocator.getInstantAccountVerificationService();
		}catch(Exception lse) {

			}
				
		/** Initialize the ExtendedInstantAccountVerificationService  */
//		Create InstantAccountVerificationService Locator 
		ExtendedInstantAccountVerificationServiceServiceLocator extendedInstantAccountVerificationServiceServiceLocator = new ExtendedInstantAccountVerificationServiceServiceLocator();
		String extendedInstantAccountVerificationServiceServiceName = extendedInstantAccountVerificationServiceServiceLocator.getExtendedInstantAccountVerificationServiceWSDDServiceName();
		extendedInstantAccountVerificationServiceServiceLocator.setExtendedInstantAccountVerificationServiceEndpointAddress(System.getProperty("com.yodlee.soap.services.url") 
				+ "/" + extendedInstantAccountVerificationServiceServiceName);
		try{
			extendedInstantAccountVerificationService = extendedInstantAccountVerificationServiceServiceLocator.getExtendedInstantAccountVerificationService();
		}catch(Exception lse) {

			}
		
		/** Initialize the DataService  */
//		Create DataService Locator 
		DataServiceServiceLocator dataServiceServiceLocator = new DataServiceServiceLocator();
		String dataServiceServiceName = dataServiceServiceLocator.getDataServiceWSDDServiceName();
		dataServiceServiceLocator.setDataServiceEndpointAddress(System.getProperty("com.yodlee.soap.services.url") 
				+ "/" + dataServiceServiceName);
		try{
			dataService = dataServiceServiceLocator.getDataService();
		}catch(Exception lse) {

			}
		
		/** Initialize the Refresh  */
//		Create DataService Locator 
		RefreshServiceLocator refreshServiceLocator = new RefreshServiceLocator();
		String refreshServiceName = refreshServiceLocator.getRefreshServiceWSDDServiceName();
		refreshServiceLocator.setRefreshServiceEndpointAddress(System.getProperty("com.yodlee.soap.services.url") 
				+ "/" + refreshServiceName);
		try{
			refresh = refreshServiceLocator.getRefreshService();
		}catch(Exception lse) {

			}
	}

	/**
	 * Returns the verifiable account given the transfer account
	 * <p>
	 * This API will fist check if the association between target transfer
	 * account and verifiable account is available. If not available, we call
	 * associateVerifiableAccount API which creates this asscociation and makes
	 * it persistence i.e puts verifiable_acct_id in TRANSFER_ACCT table.
	 * 
	 * @param userContext
	 *            The user context of the caller
	 * @param transferAccountID
	 *            Transfer Account ID for which, verifiable account is required
	 * @return VerifiableAccount object
	 */
	public VerifiableAccount getVerifiableAccount(UserContext userContext,
			Long transferAccountID) {
		VerifiableAccount vAcct = null;
		VerifiableTargetAccount verifiableTargetAcct = new VerifiableTargetAccount();
		verifiableTargetAcct.setTargetAccountId(transferAccountID);
		verifiableTargetAcct.setTargetAccountCategory(VerifiableAccountCategory.DFI_ACCOUNT);

		
		
		/*VerifiableAccountCategory
		.getInstance(VerifiableAccountCategory.DFI_ACCOUNT
				.intValue())*/
		try {
			vAcct = verifiableAccountService.getVerifiableAccount(userContext,
				verifiableTargetAcct);
		} catch (NoSuchVerifiableTargetAccountExceptionFault e) {
			System.out.println("No verifiable account yet.");
		} catch (StaleConversationCredentialsExceptionFault scce) {
			System.out.println("\n Stale Conversation Credentials Exception....");
			System.out.println(scce.toString());
		} catch (InvalidConversationCredentialsExceptionFault icce) {
			System.out.println("Invalid Conversation Credentials Exception");
			System.out.println(icce.toString());
		} catch (CoreExceptionFault ce) {
			System.out.println("\n Core Exception....");
			System.out.println(ce.toString());
		} catch (InvalidUserContextExceptionFault iuce) {
			System.out.println("Invalid User Context Exception");
			System.out.println(iuce.toString());
		} catch (IllegalArgumentValueExceptionFault value) {
			System.out.println("Given Transfer Account ID is invalid");
			System.out.println(value.toString());
		} catch (RemoteException re) {
			System.out.println("\n Remote Exception....");
			System.out.println(re.toString());
		}
			
		
		if (vAcct == null) {
			try {
				vAcct = verifiableAccountService.associateVerifiableAccount(
						userContext, verifiableTargetAcct);
			} catch (InvalidUserContextExceptionFault iu) {
				System.out.println("Invalid User Context");
				System.out.println(iu.toString());
			} catch (InvalidConversationCredentialsExceptionFault ic) {
				System.out.println("Invalid Cobrand Context Exception");
				System.out.println(ic.toString());
			} catch (NoSuchVerifiableTargetAccountExceptionFault tar) {
				System.out.println("Invalid Target account Exception");
				System.out.println(tar.toString());

			} catch (VerifiableAccountAlreadyAssociatedExceptionFault assc) {
				System.out
						.println("Ignore the exception when asscociation already available");
			} catch (IllegalArgumentValueExceptionFault value) {
				System.out.println("Given Transfer Account ID is invalid");
				System.out.println(value.toString());
			} catch (StaleConversationCredentialsExceptionFault scce) {
            	System.out.println("\n Stale Conversation Credentials Exception....");
    			System.out.println(scce.toString());
    		} catch (CoreExceptionFault ce) {
    			System.out.println("\n Core Exception....");
    			System.out.println(ce.toString());
    		} catch (RemoteException re) {
    			System.out.println("\n Remote Exception....");
    			System.out.println(re.toString());
    		}
		} else
			System.out.println("Verifiable Account Already Available");
		return vAcct;
	}

	/**
	 * Adds a Savings / Checking Account.
	 * 
	 * @param userContext
	 *            The user context of the caller
	 */
	public void AddAccount(UserContext userContext) {
		FTAccountManagement ftAcctMgmt = new FTAccountManagement();
		ftAcctMgmt.addAccount(userContext);
	}

	public void UpdateAccount(UserContext userContext) {
		FTAccountManagement ftAcctMgmt = new FTAccountManagement();
		ftAcctMgmt.updateTransferAcct(userContext);
	}


	public void showRoutingNumbers(CobrandContext cobrandContext, String RountingNumber) {
		
		RoutingNumberInfo[] rni = null;
		
		try {
			
			
			ArrayOfRoutingNumberInfo arrayOfRoutingNumberInfo = null;
			
			if (RountingNumber == null){
			  arrayOfRoutingNumberInfo =  rns.getAllRoutingNumberInfos(cobrandContext);
			}
			
			else{
				RoutingNumberInfo rountingNumberInfo =  rns.getRoutingNumberInfoByRoutingNumber(cobrandContext,RountingNumber);
				rni = new RoutingNumberInfo []{rountingNumberInfo};
			}
			
			if(arrayOfRoutingNumberInfo != null){
				rni = arrayOfRoutingNumberInfo.getElements();
				
			}
		} catch (StaleConversationCredentialsExceptionFault scce) {
			System.out.println("\n Stale Conversation Credentials Exception....");
			System.out.println(scce.toString());
		} catch (InvalidCobrandConversationCredentialsExceptionFault iccce) {
			System.out.println("Invalid Cobrand Conversation Credentials Exception");
			System.out.println(iccce.toString());
		} catch (InvalidConversationCredentialsExceptionFault icce) {
			System.out.println("Invalid Conversation Credentials Exception");
			System.out.println(icce.toString());
		} catch (CoreExceptionFault ce) {
			System.out.println("\n Core Exception....");
			System.out.println(ce.toString());
		} catch (InvalidCobrandContextExceptionFault icce) {
			System.out.println("Invalid Cobrand Context Exception");
			System.out.println(icce.toString());
		} catch (IllegalArgumentValueExceptionFault value) {
			System.out.println("Given Transfer Account ID is invalid");
			System.out.println(value.toString());
		} catch (RemoteException re) {
			System.out.println("\n Remote Exception....");
			System.out.println(re.toString());
		}
		
		if (rni != null){
		if (RountingNumber == null){
		 System.out.println(" total number of rounting number for the cobrand : " + rni.length);
		}
		System.out.println("Listing the routing number details*****");
		System.out.println("\n");
		for (int i=0; i<rni.length; i++){
			System.out.println("Rounting number: " +  rni[i].getRoutingNumber());
			System.out.println("Content Service Id : " +  rni[i].getContentServiceId());
			System.out.println("Is ACH Supported : " +  rni[i].isIsACHSupported());
			System.out.println("Financial Institution Name : " +  rni[i].getFinancialInstitutionInfo().getAbbreviatedName());
			System.out.println("is retired : " +  rni[i].isIsRetired());
			System.out.println("\n");
		 }
		}
	 }
	
	/**
	 * Enables existing item acccount for funds transfer.
	 * 
	 * @param userContext
	 *            The user context of the caller
	 */
	public void EnableAccountTransfer(UserContext userContext) {
		FTAccountManagement ftAcctMgmt = new FTAccountManagement();
		ftAcctMgmt.enableTransferAccountForItem(userContext);
	}

	/**
	 * Displays all the transfer accounts associated to particular user.
	 * 
	 * @param userContext
	 *            The user context of the caller.
	 */
	public void showAllTransferAccounts(UserContext userContext) {
		FTAccountManagement ftAcctMgmt = new FTAccountManagement();
		ftAcctMgmt.getAllTransferAccountsForUser(userContext);
	}

	/**
	 * Removes (disable)given account
	 * 
	 * @param userContext
	 *            The user context of the caller.
	 */
	public void removeTransferAccount(UserContext userContext) {
		FTAccountManagement ftAcctMgmt = new FTAccountManagement();
		ftAcctMgmt.removeTransferAcct(userContext);
	}

	/**
	 * Initiates Instant verification for given transfer account.
	 * 
	 * @param userContext
	 *            The user context of the caller.
	 */
	public void InitiateIAVVerification(UserContext userContext) {
		FTVerificationManagement ftverifyMgmt = new FTVerificationManagement();
		try {
			ftverifyMgmt.startIAV(userContext);
		} catch (IllegalArgumentValueExceptionFault value) {
			System.out.println("Given Transfer Account ID is invalid");
			System.out.println(value.toString());
		}
	}

	/**
	 * Initiates Challenge deposit verification for given transfer account.
	 * 
	 * @param userContext
	 *            The user context of the caller.
	 */
	public void InitiateCDVerification(UserContext userContext) {
		FTVerificationManagement ftverifyMgmt = new FTVerificationManagement();
		ftverifyMgmt.StartCDAccountVerification(userContext);
	}

	/**
	 * Verifies a TransferAccount by Challenge deposit scheme.
	 * 
	 * @param userContext
	 *            The user context of the caller
	 */
	public void VerifyChallengeDeposit(UserContext userContext) {
		FTVerificationManagement ftVerificcationMgmt = new FTVerificationManagement();
		ftVerificcationMgmt.doVerifyChallengeDeposit(userContext);
	}

	/**
	 * Displays account verification details for given transfer account.
	 * 
	 * @param userContext
	 *            The user context of the caller
	 */
	public void displayAcctVarificationInfo(UserContext userContext) {
		FTVerificationManagement ftVerifyMgmt = new FTVerificationManagement();
		try {
			ftVerifyMgmt.dispAcctVarificationInfo(userContext);
		} catch (IllegalArgumentValueExceptionFault value) {
			System.out.println("Given Transfer Account ID is invalid");
			System.out.println(value.toString());
		}
	}

	/**
	 * Displays Transfer Limits for Cobrand.
	 * 
	 * @param cobrandContext
	 *            Cobrand Context
	 */
	public void displayTransferLimits(UserContext userContext) {
		TransferLimitsUtil.displayTransferLimits(userContext);
	}

	/**
	 * Creates a TransferRequest by prompting the user for the following inputs.
	 * 1. Source acct ID, 2. Destination accountID 3. Amount 4. Transfer Type
	 * (Single or recurring) 5. Transfer Mode (Standard / Next Day)
	 * 
	 * @param userContext
	 *            The user context of the caller
	 */
	public void MakeTransfer(UserContext userContext) {
		FTManagement ftMgmt = new FTManagement();
		ftMgmt.doMakeTransfer(userContext);
	}

	/**
	 * Displays histrory of transfers made /initited by user.
	 * 
	 * @param userContext
	 *            The user context of the caller
	 */
	public void showTransferHistory(UserContext userContext) {
		FTTransferHistory ftHistory = new FTTransferHistory();
		ftHistory.displayTransferRequestsHistoryforUser(userContext);
	}

	/**
	 * Displays all the pending transfers for the given user.
	 * 
	 * @param userContext
	 *            The user context of the caller
	 */
	public void showPendingTransfers(UserContext userContext) {
		FTTransferHistory ftHistory = new FTTransferHistory();
		ftHistory.getAllPendingTransfersForUser(userContext);
	}

	/**
	 * Displays all the processed transfers for the given user.
	 * 
	 * @param userContext
	 *            The user context of the caller
	 */
	public void showProcessedTransfers(UserContext userContext) {
		FTTransferHistory ftHistory = new FTTransferHistory();
		ftHistory.getAllProcessedTransfersForUser(userContext);
	}

    /**
	 * Displays all verifications made/initited by user.
	 *
	 * @param userContext
	 *            The user context of the caller
	 */
	public void showAllVerifications(UserContext userContext) {
		FTVerificationManagement ftVM = new FTVerificationManagement();
		ftVM.displayAllVerifications(userContext);
	}
	
	/**
	 * Marks a funds transfer account as verified.
	 * 
	 * @param userContext the user context of the caller
	 */
	public void markAccountVerified(UserContext userContext) {
		FTVerificationManagement ftVM = new FTVerificationManagement();
		ftVM.markAccountVerified(userContext);
	}

    /**
	 * Cancels the transfer. For the recurring transfer this method takes
	 * following inputs 1. Cancel all the transfer requests or 2. Cancel only
	 * current transfer request.
	 * 
	 * @param userContext
	 *            The user context of the caller
	 */
	public void cancelTransfer(UserContext userContext) {
		FTManagement ftMgmt = new FTManagement();
		ftMgmt.cancelTransfer(userContext);
	}
    
	public void showRountingNumberInfoByRountingNumber(CobrandContext cobrandContext){
		String rountingNumber = IOUtils.promptInput("Enter the Routing Number: ", "Enter the Routing Number : ");
		showRoutingNumbers(cobrandContext, rountingNumber);
	}
	/**
	 * Handles the submenu for Funds Transfer.
	 * 
	 * @param userContext
	 *            The user context of the caller
	 */
	public void doMenu(UserContext userContext) {
		boolean loop = true;
		int choice = 0;
		while (loop) {
            try {
                System.out.println("\nFunds Transfer Menu");
                System.out.println("" + NAV_ADD_ACCT
                        + ". Add Banking /Brokerage Account");
                System.out.println("" + NAV_UPDATE_ACCT
                        + ". Update Banking /Brokerage Account");
                System.out.println("" + NAV_ENABLE_ACCT
                        + ". Enable existing aggregated accounts for transfer");
                System.out.println("" + NAV_SHOW_TRANS_ACCT
                        + ". Show All Transfer Accounts ");
                System.out.println("" + NAV_REMOVE_ACCT
                        + ". Remove Transfer Account ");
                System.out.println("" + NAV_INITIATE_IAV
                        + ". Initiate IAV (Includes MFA)");
                System.out.println("" + NAV_INITITATE_CD
                        + ". Initiate CD ");
                System.out.println("" + NAV_VERIFY_CD
                        + ". Verify Challenge Deposit");
                System.out.println("" + NAV_MARK_VERIFIED 
                		+ ". Mark Account as Verified");
                System.out.println("" + NAV_DISP_VERIFICATION_INFO
                        + ". Display Account Verification Info");
                System.out.println("" + NAV_SHOW_ALL_VERIFICATIONS
                        + ". Display All CD Verifications Info(success/in progress/failed/pending)");
                System.out.println("" + NAV_DISP_TRANSFER_LIMITS
                        + ". Display Transfer Limits");
                System.out.println("" + NAV_MAKE_TRANSFER
                        + ". Make Transfer");
                System.out.println("" + NAV_SHOW_TRANS_HISTORY
                        + ". Show Transfer History");
                System.out.println("" + NAV_SHOW_PENDING_TRANSFERS
                        + ". Show All pending transfers");
                System.out.println("" + NAV_SHOW_PROCESSED_TRANSFERS
                        + ". Show All processed transfers");
                System.out.println("" + NAV_CANCEL_TRANSFER
                        + ". Cancel Transfer");
                System.out.println("" + NAV_RTNG_NUMBERS
                        + ". Show All Routing Numbers for the cobrand ");
                System.out.println("" + NAV_RTNG_INFO_RTN_NUMBER
                        + ". Show Routing Number Info for a Routing Number ");
                System.out.println("" + NAV_QUIT
                        + ". Exit");
                System.out.println("\n");
                System.out.print("Enter Choice : ");
                choice = IOUtils.readInt();
                if (choice == NAV_ADD_ACCT)
                    AddAccount(userContext);
                if (choice == NAV_UPDATE_ACCT)
                    UpdateAccount(userContext);
                if (choice == NAV_ENABLE_ACCT)
                    EnableAccountTransfer(userContext);
                if (choice == NAV_SHOW_TRANS_ACCT)
                    showAllTransferAccounts(userContext);
                if (choice == NAV_REMOVE_ACCT)
                    removeTransferAccount(userContext);
                if (choice == NAV_INITIATE_IAV)
                    InitiateIAVVerification(userContext);
                if (choice == NAV_INITITATE_CD)
                    InitiateCDVerification(userContext);
                if (choice == NAV_VERIFY_CD)
                    VerifyChallengeDeposit(userContext);
                if (choice == NAV_DISP_VERIFICATION_INFO)
                    displayAcctVarificationInfo(userContext);
                if (choice == NAV_DISP_TRANSFER_LIMITS)
                    displayTransferLimits(userContext);
                if (choice == NAV_MAKE_TRANSFER)
                    MakeTransfer(userContext);
                if (choice == NAV_SHOW_TRANS_HISTORY)
                    showTransferHistory(userContext);
                if (choice == NAV_SHOW_ALL_VERIFICATIONS)
                    showAllVerifications(userContext);
                if (choice == NAV_MARK_VERIFIED)
                	markAccountVerified(userContext);
                if (choice == NAV_SHOW_PENDING_TRANSFERS)
                    showPendingTransfers(userContext);
                if (choice == NAV_SHOW_PROCESSED_TRANSFERS)
                    showProcessedTransfers(userContext);
                if (choice == NAV_CANCEL_TRANSFER)
                    cancelTransfer(userContext);
                if (choice == NAV_RTNG_NUMBERS )
                    showRoutingNumbers(getCobrandContext(),null);
                if (choice == NAV_RTNG_INFO_RTN_NUMBER)
                    showRountingNumberInfoByRountingNumber(getCobrandContext());
                if (choice == NAV_QUIT)
                    loop = false;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
	}

}
