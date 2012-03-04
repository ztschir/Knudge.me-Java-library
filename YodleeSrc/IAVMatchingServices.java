package com.yodlee.sampleapps;

import java.rmi.RemoteException;

import com.yodlee.sampleapps.helper.IOUtils;
import com.yodlee.soap.collections.core.routingnumberservice.ArrayOfRoutingNumberInfo;
import com.yodlee.soap.common.CobrandContext;
import com.yodlee.soap.common.UserContext;
import com.yodlee.soap.core.CoreExceptionFault;
import com.yodlee.soap.core.IllegalArgumentValueExceptionFault;
import com.yodlee.soap.core.InvalidCobrandContextExceptionFault;
import com.yodlee.soap.core.InvalidCobrandConversationCredentialsExceptionFault;
import com.yodlee.soap.core.InvalidConversationCredentialsExceptionFault;
import com.yodlee.soap.core.StaleConversationCredentialsExceptionFault;
import com.yodlee.soap.core.routingnumberservice.RoutingNumberInfo;
import com.yodlee.soap.core.routingnumberservice.routingnumberservice.RoutingNumberService;
import com.yodlee.soap.core.routingnumberservice.routingnumberservice.RoutingNumberServiceServiceLocator;


public class IAVMatchingServices extends ApplicationSuper{
	
	/** Contains the RoutingNumberService  */
	protected RoutingNumberService rns;

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
	private static final int NAV_RTNG_NUMBERS = optionCount++;
	
	private static final int NAV_RTNG_INFO_RTN_NUMBER = optionCount++;

	/** Navigation Menu Choice. * */
	private static final int NAV_QUIT = 0;

	public IAVMatchingServices() {
//		Create RoutingNumberService Locator 
		RoutingNumberServiceServiceLocator routingNumberServiceServiceLocator = new RoutingNumberServiceServiceLocator();
		String routingNumberServiceServiceName = routingNumberServiceServiceLocator.getRoutingNumberServiceWSDDServiceName();
		routingNumberServiceServiceLocator.setRoutingNumberServiceEndpointAddress(System.getProperty("com.yodlee.soap.services.url") 
				+ "/" + routingNumberServiceServiceName);
		try{
			rns = routingNumberServiceServiceLocator.getRoutingNumberService();
		}catch(Exception lse) {

			}
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
	 * Marks a funds transfer account as verified.
	 * 
	 * @param userContext the user context of the caller
	 */
	public void markAccountVerified(UserContext userContext) {
		FTVerificationManagement ftVM = new FTVerificationManagement();
		ftVM.markAccountVerified(userContext);
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
	 * Displays all verifications made/initited by user.
	 *
	 * @param userContext
	 *            The user context of the caller
	 */
	public void showAllVerifications(UserContext userContext) {
		FTVerificationManagement ftVM = new FTVerificationManagement();
		ftVM.displayAllVerifications(userContext);
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
            	System.out.println("\n*****************************");
                System.out.println("IAV Data Matching Menu");
                System.out.println("*****************************");
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
                if (choice == NAV_SHOW_ALL_VERIFICATIONS)
                    showAllVerifications(userContext);
                if (choice == NAV_MARK_VERIFIED)
                	markAccountVerified(userContext);
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
