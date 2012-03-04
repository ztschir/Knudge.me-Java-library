package YodleeSrc;

import java.rmi.RemoteException;

import com.yodlee.soap.collections.core.verification.ArrayOfVerificationInfo;
import com.yodlee.soap.collections.core.verification.iav.ArrayOfInstantAccountVerificationMatchData;
import com.yodlee.soap.common.CobrandContext;
import com.yodlee.soap.common.ContentServiceInfo;
import com.yodlee.soap.common.UserContext;
import com.yodlee.soap.core.CoreExceptionFault;
import com.yodlee.soap.core.IllegalArgumentValueExceptionFault;
import com.yodlee.soap.core.InvalidConversationCredentialsExceptionFault;
import com.yodlee.soap.core.InvalidUserContextExceptionFault;
import com.yodlee.soap.core.StaleConversationCredentialsExceptionFault;
import com.yodlee.soap.core.dataservice.YMoney;
import com.yodlee.soap.core.fundstransfer.transferaccountmanagement.BankTransferAccount;
import com.yodlee.soap.core.fundstransfer.transferaccountmanagement.DFIAccount;
import com.yodlee.soap.core.fundstransfer.transferaccountmanagement.TransferAccount;
import com.yodlee.soap.core.fundstransfer.transferaccountmanagement.TransferAccountNotFoundExceptionFault;
import com.yodlee.soap.core.fundstransfer.transferaccountmanagement.transferaccountmanagement.TransferAccountManagement;
import com.yodlee.soap.core.fundstransfer.transferaccountmanagement.transferaccountmanagement.TransferAccountManagementServiceLocator;
import com.yodlee.soap.core.fundstransfer.transfermanagement.PendingTransfersExistsExceptionFault;
import com.yodlee.soap.core.mfarefresh.MFARefreshInfo;
import com.yodlee.soap.core.refresh.RefreshStatus;
import com.yodlee.soap.core.refresh.refresh.Refresh;
import com.yodlee.soap.core.refresh.refresh.RefreshServiceLocator;
import com.yodlee.soap.core.routingnumberservice.RoutingNumberInfo;
import com.yodlee.soap.core.routingnumberservice.RoutingNumberNotFoundExceptionFault;
import com.yodlee.soap.core.routingnumberservice.routingnumberservice.RoutingNumberService;
import com.yodlee.soap.core.routingnumberservice.routingnumberservice.RoutingNumberServiceServiceLocator;
import com.yodlee.soap.core.verification.*;
import com.yodlee.soap.core.verification.exceptions.InvalidVerifiableAccountExceptionFault;
import com.yodlee.soap.core.verification.exceptions.NoSuchVerifiableTargetAccountExceptionFault;
import com.yodlee.soap.core.verification.exceptions.VerifiableAccountAlreadyAssociatedExceptionFault;
import com.yodlee.soap.core.verification.extendedinstantaccountverificationservice.ExtendedInstantAccountVerificationService;
import com.yodlee.soap.core.verification.extendedinstantaccountverificationservice.ExtendedInstantAccountVerificationServiceServiceLocator;
import com.yodlee.soap.core.verification.fiverificationservice.FIVerificationService;
import com.yodlee.soap.core.verification.fiverificationservice.FIVerificationServiceServiceLocator;
import com.yodlee.soap.core.verification.iav.ExtendedInstantAccountVerificationInfo;
import com.yodlee.soap.core.verification.iav.IAVMatchCode;
import com.yodlee.soap.core.verification.iav.InstantAccountVerificationInfo;
import com.yodlee.soap.core.verification.iav.InstantAccountVerificationMatchData;
import com.yodlee.soap.core.verification.iav.exception.InstantAccountVerificationNotSupportedExceptionFault;
import com.yodlee.soap.core.verification.instantaccountverificationservice.InstantAccountVerificationService;
import com.yodlee.soap.core.verification.instantaccountverificationservice.InstantAccountVerificationServiceServiceLocator;
import com.yodlee.soap.core.verification.verifiableaccountservice.VerifiableAccountService;
import com.yodlee.soap.core.verification.verifiableaccountservice.VerifiableAccountServiceServiceLocator;
import com.yodlee.soap.ext.traversal.contentservicetraversal.ContentServiceTraversal;
import com.yodlee.soap.ext.traversal.contentservicetraversal.ContentServiceTraversalServiceLocator;

public class FTVerificationManagement {

	protected VerifiableAccountService verifiableAccountService;
	
	protected TransferAccountManagement transferAccountManagement;
	
	protected InstantAccountVerificationService iavService;
	
	protected FIVerificationService fiVerificationService;
	
	protected Refresh refresh;
	
	protected ExtendedInstantAccountVerificationService extendedInstantAccountVerificationService;
	
	private static final int InstantAccountVerificationStatus_INITIATED = 1;
    private static final Integer InstantAccountVerificationStatus_SUCCEEDED = new Integer(2);
    private static final Integer InstantAccountVerificationStatus_FAILED = new Integer(3);
	
	public FTVerificationManagement(){
		VerifiableAccountServiceServiceLocator verifiableAccountServiceServiceLocator = new VerifiableAccountServiceServiceLocator();
		String verifiableAccountServiceServiceName = verifiableAccountServiceServiceLocator.getVerifiableAccountServiceWSDDServiceName();
		verifiableAccountServiceServiceLocator.setVerifiableAccountServiceEndpointAddress(System.getProperty("com.yodlee.soap.services.url") 
				+ "/" + verifiableAccountServiceServiceName);
		try{
			verifiableAccountService = verifiableAccountServiceServiceLocator.getVerifiableAccountService();
		} catch(Exception lse) {
			lse.printStackTrace();
		}
		
		TransferAccountManagementServiceLocator transferAccountManagementServiceLocator = new TransferAccountManagementServiceLocator();
		String transferAccountManagementServiceName = transferAccountManagementServiceLocator.getTransferAccountManagementServiceWSDDServiceName();
		transferAccountManagementServiceLocator.setTransferAccountManagementServiceEndpointAddress(System.getProperty("com.yodlee.soap.services.url") 
				+ "/" + transferAccountManagementServiceName);
		try{
			transferAccountManagement = transferAccountManagementServiceLocator.getTransferAccountManagementService();
		} catch(Exception lse) {
			lse.printStackTrace();
		}
		
		InstantAccountVerificationServiceServiceLocator instantAccountVerificationServiceServiceLocator = new InstantAccountVerificationServiceServiceLocator();
		String instantAccountVerificationServiceServiceName = instantAccountVerificationServiceServiceLocator.getInstantAccountVerificationServiceWSDDServiceName();
		instantAccountVerificationServiceServiceLocator.setInstantAccountVerificationServiceEndpointAddress(System.getProperty("com.yodlee.soap.services.url") 
				+ "/" + instantAccountVerificationServiceServiceName);
		try{
			iavService = instantAccountVerificationServiceServiceLocator.getInstantAccountVerificationService();
		} catch(Exception lse) {
			lse.printStackTrace();
		}
		
		FIVerificationServiceServiceLocator fIVerificationServiceServiceLocator = new FIVerificationServiceServiceLocator();
		String fIVerificationServiceServiceName = fIVerificationServiceServiceLocator.getFIVerificationServiceWSDDServiceName();
		fIVerificationServiceServiceLocator.setFIVerificationServiceEndpointAddress(System.getProperty("com.yodlee.soap.services.url") 
				+ "/" + fIVerificationServiceServiceName);
		try{
			fiVerificationService = fIVerificationServiceServiceLocator.getFIVerificationService();
		} catch(Exception lse) {
			lse.printStackTrace();
		}
		
		RefreshServiceLocator refreshServiceLocator = new RefreshServiceLocator();
		String refreshServiceName = refreshServiceLocator.getRefreshServiceWSDDServiceName();
		refreshServiceLocator.setRefreshServiceEndpointAddress(System.getProperty("com.yodlee.soap.services.url") 
				+ "/" + refreshServiceName);
		try{
			refresh = refreshServiceLocator.getRefreshService();
		} catch(Exception lse) {

		}
				
    	ExtendedInstantAccountVerificationServiceServiceLocator extendedInstantAccountVerificationServiceServiceLocator = new ExtendedInstantAccountVerificationServiceServiceLocator();
		String extendedInstantAccountVerificationServiceServiceName = extendedInstantAccountVerificationServiceServiceLocator.getExtendedInstantAccountVerificationServiceWSDDServiceName();
		extendedInstantAccountVerificationServiceServiceLocator.setExtendedInstantAccountVerificationServiceEndpointAddress(System.getProperty("com.yodlee.soap.services.url") 
				+ "/" + extendedInstantAccountVerificationServiceServiceName);
		try {
			extendedInstantAccountVerificationService = extendedInstantAccountVerificationServiceServiceLocator.getExtendedInstantAccountVerificationService();
		} catch(Exception lse) {
			lse.printStackTrace();
		}
	}
	
	public void dispAcctVarificationInfo(UserContext userContext) throws IllegalArgumentValueExceptionFault {

        // get transfer account ID
        String transferAccountID = IOUtils.promptInput(
                IFileTransferConstants.TransAcctID2Prompt,
                IFileTransferConstants.TransAcctID2ReEnterPrompt);
        
        TransferAccount transferAccount = null;
        try {
        	transferAccount = transferAccountManagement.getTransferAccount(userContext,
			        new Long(transferAccountID));
		} catch (StaleConversationCredentialsExceptionFault scce) {
			System.out.println("\n Stale Conversation Credentials Exception....");
			System.out.println(scce.toString());
		}  catch (InvalidConversationCredentialsExceptionFault ic) {
			System.out.println("Invalid Conversation Credentials Exception");
			System.out.println(ic.toString());
		} catch (CoreExceptionFault ce) {
			System.out.println("\n Core Exception....");
			System.out.println(ce.toString());
		} catch (TransferAccountNotFoundExceptionFault tanfe) {
			System.out.println("Transfer Account Not Found Context");
			System.out.println(tanfe.toString());
		} catch (InvalidUserContextExceptionFault iu) {
			System.out.println("Invalid User Context");
			System.out.println(iu.toString());
		} catch (IllegalArgumentValueExceptionFault value) {
			System.out.println("Given Transfer Account ID is invalid");
			System.out.println(value.toString());
		} catch (RemoteException re) {
			System.out.println("\n Remote Exception....");
			System.out.println(re.toString());
		}
		
    	if (transferAccount == null){
    		System.out.println("No transfer account found for the given transfer account id");
    		return;
    	}
    			
		DFIAccount dfiAccount=null;
    	dfiAccount=((BankTransferAccount)transferAccount).getDfiAccount();    	  		
    	
		VerifiableTargetAccount targetAccount = new VerifiableTargetAccount();
		targetAccount.setTargetAccountId(dfiAccount.getDfiAccountId());
		targetAccount.setTargetAccountCategory(VerifiableAccountCategory.DFI_ACCOUNT);

       
        boolean isVerifiableAccountAssociated = false;
        VerifiableAccount verifiableAcct = null;
		try {
			isVerifiableAccountAssociated = verifiableAccountService.isVerifiableAccountAssociated(userContext, targetAccount);
			if (!isVerifiableAccountAssociated) {
	           System.out.println("no verifiable accounts associated");
	           return;
	        }
	        verifiableAcct = verifiableAccountService.getVerifiableAccount(userContext, targetAccount);
		} catch (StaleConversationCredentialsExceptionFault scce) {
        	System.out.println("\n Stale Conversation Credentials Exception....");
			System.out.println(scce.toString());
		} catch (InvalidConversationCredentialsExceptionFault ic) {
			System.out.println("Invalid Conversation Credentials Exception");
			System.out.println(ic.toString());
		} catch (NoSuchVerifiableTargetAccountExceptionFault nvtae) {
			System.out.println("\n No Such Verifiable Target Account Exception....");
			System.out.println(nvtae.toString());
		} catch (CoreExceptionFault ce) {
			System.out.println("\n Core Exception....");
			System.out.println(ce.toString());
		} catch (InvalidUserContextExceptionFault iu) {
			System.out.println("Invalid User Context");
			System.out.println(iu.toString());
		} catch (IllegalArgumentValueExceptionFault value) {
			System.out.println("Given Transfer Account ID is invalid");
			System.out.println(value.toString());
		} catch (RemoteException re) {
			System.out.println("\n Remote Exception....");
			System.out.println(re.toString());
		}
        
        if(null == verifiableAcct){
        	System.out.println("VerifiableAccount is NULL for TargetAccountId : " 
                                    +targetAccount.getTargetAccountId() + " It should never happen");
            return;
        }
        
        VerificationScheme verificationScheme  = null;
        try {
        	 verificationScheme = verifiableAccountService.getLatestVerificationScheme(userContext, verifiableAcct);
        } catch(Exception e){
        	e.printStackTrace();
        }
        
        if (verificationScheme == null){
        	System.out.println("Account not verified yet");
        	return;
        }
        
        if(verificationScheme != null && verificationScheme.CHALLENGE_DEPOSIT.equals(verificationScheme)){
        	System.out.println("latest verification Scheme used is Challenge Deposit");
        	if (1 == transferAccount.getIsVerified().intValue()) {
   			 System.out.println("Is Account Verified YES");
   		    }
   		   else {
   			 System.out.println("Is Account Verified NO");
   		   }
        }
        	else if(verificationScheme != null && verificationScheme.IAV.equals(verificationScheme)){
        		System.out.println("latest verification Scheme used is IAV");
        
        try {    		
            InstantAccountVerificationMatchData[] matchData = null;
            ArrayOfInstantAccountVerificationMatchData arrayOfInstantAccountVerificationMatchData = iavService.getMatchingAccountVerificationData(userContext, verifiableAcct);
            if(arrayOfInstantAccountVerificationMatchData != null){
            	matchData = arrayOfInstantAccountVerificationMatchData.getElements();
            }
            
            if (matchData == null){
            	System.out.println("IAV failed");
            }
            if (matchData != null && matchData.length > 0){
            	int iCount = 0;
                int iMaxCount = matchData.length;

                for (iCount = 0; iCount <= iMaxCount - 1; iCount++) {

    				IAVMatchCode iavMatchCode =	matchData[iCount].getIavMatchCode();
    				System.out.println("iavMatchCode ===> " + iavMatchCode);
    				if(iavMatchCode.equals(IAVMatchCode.SUCCESS)){
    					System.out.println("IAV account matching success");
                        return;
    				}else if(iavMatchCode.equals(IAVMatchCode.MATCH_LENGTH_FAILED)){
    					System.out.println("IAV account matching failed in length comparision");
    	                return;
    				}else if(iavMatchCode.equals(IAVMatchCode.SIGNIFICANT_DIGITS_FAILED)){
    					System.out.println("IAV account matching failed in significant digits comparision");
    	                return;	
    				}else if(iavMatchCode.equals(IAVMatchCode.EXACT_MATCH_FAILED)){
    					System.out.println("IAV account matching failed in exact matching of account");
                        return;
    				}else if(iavMatchCode.equals(IAVMatchCode.PARTIAL_MATCH_FAILED)){
    					System.out.println("IAV account matching failed in partial match comparision");
                        return;
    				}else if(iavMatchCode.equals(IAVMatchCode.ACCOUNT_TYPE_MATCH_FAILED)){
    					System.out.println("IAV account matching failed in account type comparision");
                        return;
    				}else if(iavMatchCode.equals(IAVMatchCode.ACCOUNT_HOLDER_MATCH_FAILED)){
    					System.out.println("IAV account matching failed in account holder comparision");
                        return;
    				}else if(iavMatchCode.equals(IAVMatchCode.PARTIAL_MATCH_FAILED_DUE_TO_PREFIX)){
    					 System.out.println("IAV account matching failed in partial match due to prefix");
    	                 return;
    				}else if(iavMatchCode.equals(IAVMatchCode.PARTIAL_MATCH_FAILED_DUE_TO_SUFFIX)){
    					System.out.println("IAV account matching failed in partial match due to suffix");
                        return;
    				}else{
    					System.out.println("IAV failed");
                		return;
    				}
    			
                }
            }
        	
        } catch (Exception e){
        	System.out.println(e);
         }
        }
        else if (verificationScheme != null && verificationScheme.MANUAL.equals(verificationScheme)){
        		System.out.println("latest verification Scheme used is Manual");
        		 if (1 == transferAccount.getIsVerified().intValue()) {
          			 System.out.println("Is Account Verified YES");
          		    }
          		   else {
          			 System.out.println("Is Account Verified NO");
          		   }
        }
    }
	
	
	/**
	 * For using markAccountVerified(), the contentServiceId of the account has to be
	 * added to the list of host contentServiceIds for a cobrand. 
	 * PARAM_KEY: COM.YODLEE.CORE.FUNDSTRANSFER.TRANSFER_LIMIT.HOSTS
	 * 
	 * @param userContext
	 */
	public void markAccountVerified(UserContext userContext) {
        String transferAcctId = IOUtils.promptInput(
                IFileTransferConstants.TransAcctID2Prompt,
                IFileTransferConstants.TransAcctID2ReEnterPrompt);

        TransferAccount transferAccount = null;
		try {
			transferAccount = transferAccountManagement.getTransferAccount(userContext, new Long(transferAcctId));
		} catch (StaleConversationCredentialsExceptionFault scce) {
        	System.out.println("\n Stale Conversation Credentials Exception....");
			scce.printStackTrace();
		} catch (InvalidConversationCredentialsExceptionFault ic) {
			System.out.println("Invalid Conversation Credentials Exception");
			ic.printStackTrace();
		} catch (CoreExceptionFault ce) {
			System.out.println("\n Core Exception....");
			ce.printStackTrace();
		} catch (TransferAccountNotFoundExceptionFault acct) {
			System.out.println("Invalid Transfer Account ID");
			acct.printStackTrace();
		} catch (InvalidUserContextExceptionFault iu) {
			System.out.println("Invalid User Context");
			iu.printStackTrace();
		} catch (IllegalArgumentValueExceptionFault value) {
			System.out.println("Given Transfer Account ID is invalid");
			value.printStackTrace();
		} catch (NumberFormatException nfe) {
			System.out.println("Number Format Exception ....");
			nfe.printStackTrace();
		} catch (RemoteException re) {
			System.out.println("\n Remote Exception....");
			re.printStackTrace();
		}
        DFIAccount dfiAccount=((BankTransferAccount)transferAccount).getDfiAccount();
		VerifiableTargetAccount verifiableTargetAccount = new VerifiableTargetAccount();
		verifiableTargetAccount.setTargetAccountId(dfiAccount.getDfiAccountId());
		verifiableTargetAccount.setTargetAccountCategory(VerifiableAccountCategory.DFI_ACCOUNT);
       
        VerifiableAccount verifiableAccount = null;
        
    	try {
    		verifiableAccount=verifiableAccountService.getVerifiableAccount(userContext, verifiableTargetAccount);
            fiVerificationService.markAccountAsFIVerified(userContext, verifiableAccount.getVerifiableTargetAccount());
            System.out.println("The account has been marked as verified.");
		} catch (StaleConversationCredentialsExceptionFault scce) {
        	System.out.println("\n Stale Conversation Credentials Exception....");
        	scce.printStackTrace();
		}catch (InvalidConversationCredentialsExceptionFault ic) {
			System.out.println("Invalid Conversation Credentials Exception");
			ic.printStackTrace();
		} catch (NoSuchVerifiableTargetAccountExceptionFault nvtae) {
			System.out.println("\n No Such Verifiable Target Account Exception....");
			nvtae.printStackTrace();
		} catch (VerifiableAccountAlreadyAssociatedExceptionFault vaae) {
			System.out.println("\n Verifiable Account Already Associated Exception....");
			vaae.printStackTrace();
		} catch (CoreExceptionFault ce) {
			System.out.println("\n Core Exception....");
			ce.printStackTrace();
		} catch (InvalidUserContextExceptionFault iu) {
			System.out.println("Invalid User Context");
			iu.printStackTrace();
		} catch (IllegalArgumentValueExceptionFault value) {
			System.out.println("Given Transfer Account ID is invalid");
			value.printStackTrace();
		} catch (RemoteException re) {
			System.out.println("\n Remote Exception....");
			re.printStackTrace();
		}
    }
	
	/**
     * Initiates instant account verification
     * <p>
     * Instant Account Verification API for doing Instant Account Verificationd
     * issues a request for verifying an instance of VerifiableAccount. The
     * method if invoked succefully issues an InstantVerificationDataRequest in
     * the background and marks the request with an IAV flag. When the data is
     * gathered and is filed another background process applies verification
     * logic and marks the VerifiableAccount with either of the status flags
     * INITIATED, FAILED , SUCCEEDED. If the status marked is FAILED, there are
     * two possiblities. 1) Verification process failed technically,retry is to
     * be invoked. 2) Verification Algorithm found more than one IAV data match,
     * thus user has to select one from all of the matching ones.
     *
     * @param userContext
     *            The user context of the caller
     * @throws IllegalArgumentValueExceptionFault 
     */
    public void startIAV(UserContext userContext, CobrandContext cobrandContext) throws IllegalArgumentValueExceptionFault {

        // account has to be associated to an mem item before IAV starts
        FTAccountManagement acctMgmt = new FTAccountManagement();
        long transferAccountID;
        transferAccountID = Long.parseLong(IOUtils.promptInput(
                IFileTransferConstants.TransAcctID2Prompt,
                IFileTransferConstants.TransAcctID2ReEnterPrompt));        
       
    	// NEW CODE HERE
        TransferAccount transferAccount = acctMgmt.linkTransferAcctToItem(userContext, transferAccountID, cobrandContext);
        long contentServiceId = transferAccount.getContentServiceId().longValue();
        YMoney  balance = transferAccount.getAccountBalance();
         System.out.println("balance = " + balance);

    	DFIAccount dfiAccount=null;
    	dfiAccount=((BankTransferAccount)transferAccount).getDfiAccount();

    	// Need to check if Routing Number supports IAV..
        if (!ChkIfIAVEnabled(cobrandContext, dfiAccount.getRoutingNumber()))
        {
            return;
        }
    	
		VerifiableTargetAccount targetAccount = new VerifiableTargetAccount();
		targetAccount.setTargetAccountId(dfiAccount.getDfiAccountId());
		targetAccount.setTargetAccountCategory(VerifiableAccountCategory.DFI_ACCOUNT);
        
        VerifiableAccount verifiableAcct = null;
		try {						
	        verifiableAcct = verifiableAccountService.getVerifiableAccount(userContext, targetAccount);
        } catch (StaleConversationCredentialsExceptionFault scce) {
        	System.out.println("\n Stale Conversation Credentials Exception....");
			System.out.println(scce.toString());
		} catch (InvalidConversationCredentialsExceptionFault ic) {
			System.out.println("Invalid Conversation Credentials Exception");
			System.out.println(ic.toString());
		} catch (NoSuchVerifiableTargetAccountExceptionFault nvtae) {
			System.out.println("\n No Such Verifiable Target Account Exception....");
			System.out.println(nvtae.toString());
		} catch (CoreExceptionFault ce) {
			System.out.println("\n Core Exception....");
			System.out.println(ce.toString());
		} catch (InvalidUserContextExceptionFault iu) {
			System.out.println("Invalid User Context");
			System.out.println(iu.toString());
		} catch (IllegalArgumentValueExceptionFault value) {
			System.out.println("Given Transfer Account ID is invalid");
			System.out.println(value.toString());
		} catch (RemoteException re) {
			System.out.println("\n Remote Exception....");
			System.out.println(re.toString());
		}
        
        if(null == verifiableAcct){
        	System.out.println("VerifiableAccount is NULL for TargetAccountId : " 
                                    +targetAccount.getTargetAccountId() + " It should never happen");
            throw new IllegalArgumentValueExceptionFault("VerifiableAccount cannot be NULL.");
        }

    	if (verifiableAcct.getVerifiableAccountStatus().getValue().equals("1")) {
    		try {
				transferAccountManagement.removeTransferAccount(userContext, transferAccount.getTransferAccountId());
			} catch (StaleConversationCredentialsExceptionFault scce) {
	        	System.out.println("\n Stale Conversation Credentials Exception....");
				System.out.println(scce.toString());
			} catch (InvalidConversationCredentialsExceptionFault ic) {
				System.out.println("Invalid Conversation Credentials Exception");
				System.out.println(ic.toString());
			} catch (CoreExceptionFault ce) {
				System.out.println("\n Core Exception....");
				System.out.println(ce.toString());
			} catch (PendingTransfersExistsExceptionFault pte) {
				System.out.println("Pending Transfer Exists");
				System.out.println(pte.toString());
			} catch (TransferAccountNotFoundExceptionFault acct) {
				System.out.println("Transfer Account Not Found");
				System.out.println(acct.toString());
			} catch (InvalidUserContextExceptionFault iu) {
				System.out.println("Invalid User Context");
				System.out.println(iu.toString());
			} catch (IllegalArgumentValueExceptionFault value) {
				System.out.println("Given Transfer Account ID is invalid");
				System.out.println(value.toString());
			} catch (RemoteException re) {
				System.out.println("\n Remote Exception....");
				System.out.println(re.toString());
			}
    	}
    	    	
        InstantAccountVerificationInfo iavi = null;
        ExtendedInstantAccountVerificationInfo eiavi = null;
        
        try {        
            String mfatype = ContentServiceManagement.getMfAType(cobrandContext, contentServiceId);  
            if ( mfatype != null ) {
        		eiavi = extendedInstantAccountVerificationService.startVerificationWithMFA1(userContext,
                        verifiableAcct, dfiAccount.getRoutingNumber(), dfiAccount.getAccountNumber());
            	// Get the IAVRefreshStatus object from InstantAccountVerificationInfo
            	IAVRefreshStatus iavRefreshStatus = eiavi.getIavRefreshStatus();
              	long itemId = iavRefreshStatus.getItemId().longValue();
              	
            	 //Get the RefreshStatus object out of IAVRefreshStatus object
            	RefreshStatus refreshStatus = iavRefreshStatus.getRefreshStatus();      
            	//Check if IAV request has been sent successfully and requires the intermediate flow ( in case of MFA requests)
        		if (refreshStatus.toString().equals(String.valueOf(RefreshStatus.SUCCESS_REFRESH_WAIT_FOR_MFA.getValue()))) {    			
        			//Get the MFA response from the agent, which contains the MFA questions
        			//The questions will be placed in the queue and the app or SDK calls can poll for these questions continuously
        			MFARefreshInfo mfaInfo = refresh.getMFAResponse(userContext, new Long(itemId));
        			MFA mfa = new MFA();
        			int errorCode = mfa.processMFA(userContext, mfaInfo, new Long(itemId));
        			if ( errorCode == 0) {
        				System.out.println("MFA site verified successfully");
        			} else if ( errorCode > 0 ){
        				System.out.println("There was an error during the verification of the MFA site. Error code is " + errorCode );
        			}
        		} else if ( refreshStatus.toString().equals(String.valueOf(RefreshStatus.SUCCESS_START_REFRESH.getValue()))) {
        			//This is the usual non-MFA site and verification will be successful    			
        		 }    
        	} else {
        		iavi = iavService.startVerification1(userContext,
                    verifiableAcct, dfiAccount.getRoutingNumber(), dfiAccount.getAccountNumber());
        	}
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
        System.out.println("Instant Account Verification Initiated, going to poll for refresh status");
        //System.out.println("Instant Account Verification Info - Verification Status" + iavi.getVerificationStatus());
        
    	//long itemID = transferAccount.getItemId();
    	//isAcctfailed = PollIAVStatus(userContext,itemID);
        //Check IAV Status here for gatherer
    	boolean isAcctfailed = false;
    	isAcctfailed = pollNewIAVStatus(userContext,dfiAccount);
    	
        // End IAV status check..
        // Proceed only if gatherer succeeded
        
        if (isAcctfailed)
        {
        	System.out.println("IAV failed");
        	//return;
        }
                
        InstantAccountVerificationMatchData[] matchData = null;
        	
        int attempts = 0;
        while (attempts<5 && matchData==null) {
        		try 
        		{
        			ArrayOfInstantAccountVerificationMatchData arrayOfInstantAccountVerificationMatchData = iavService.getMatchingAccountVerificationData(userContext, verifiableAcct);
        			if(arrayOfInstantAccountVerificationMatchData != null){
        				matchData = arrayOfInstantAccountVerificationMatchData.getElements();
        			}
        			Thread.sleep(10*1000);
        		} catch (Exception e) 
        		{
        	           e.printStackTrace();
        	           System.out.println("");
        	           return;
        		}
        	attempts++;
        };
        
        if (matchData ==  null)
        {
        	System.out.println("InstantAccountVerificationMatchData == null IAV failed");
        	return;
        }
        else
        {
			for (int index = 0; index < matchData.length; index++)
			{
				IAVMatchCode iavMatchCode =	matchData[index].getIavMatchCode();
                AccountVerificationData avd =   matchData[index].getAccountVerificationData();
                ItemVerificationInfo ivi = avd.getItemVerificationInfo();
                System.out.println(avd.getAccountName());
                System.out.println(avd.getAccountNumber());
                

                System.out.println ("Code = " + ivi.getStatusCode());


				System.out.println("iavMatchCode ===> " + iavMatchCode);
				if(iavMatchCode.equals(IAVMatchCode.SUCCESS)){
					System.out.println("IAV account matching success");
                    return;
				}else if(iavMatchCode.equals(IAVMatchCode.MATCH_LENGTH_FAILED)){
					System.out.println("IAV account matching failed in length comparision");
	                return;
				}else if(iavMatchCode.equals(IAVMatchCode.SIGNIFICANT_DIGITS_FAILED)){
					System.out.println("IAV account matching failed in significant digits comparision");
	                return;	
				}else if(iavMatchCode.equals(IAVMatchCode.EXACT_MATCH_FAILED)){
					System.out.println("IAV account matching failed in exact matching of account");
                    return;
				}else if(iavMatchCode.equals(IAVMatchCode.PARTIAL_MATCH_FAILED)){
					System.out.println("IAV account matching failed in partial match comparision");
                    return;
				}else if(iavMatchCode.equals(IAVMatchCode.ACCOUNT_TYPE_MATCH_FAILED)){
					System.out.println("IAV account matching failed in account type comparision");
                    return;
				}else if(iavMatchCode.equals(IAVMatchCode.ACCOUNT_HOLDER_MATCH_FAILED)){
					System.out.println("IAV account matching failed in account holder comparision");
                    return;
				}else if(iavMatchCode.equals(IAVMatchCode.PARTIAL_MATCH_FAILED_DUE_TO_PREFIX)){
					 System.out.println("IAV account matching failed in partial match due to prefix");
	                 return;
				}else if(iavMatchCode.equals(IAVMatchCode.PARTIAL_MATCH_FAILED_DUE_TO_SUFFIX)){
					System.out.println("IAV account matching failed in partial match due to suffix");
                    return;
				}else{
					System.out.println("IAV failed");
            		return;
				}
			}
        }

    }
    
    // Check is Account Routing no has IAV support
    private boolean ChkIfIAVEnabled(CobrandContext cobCxt, String RoutingNumber)
    {    
        try
        {
        	RoutingNumberService rns = null;
        	RoutingNumberServiceServiceLocator routingNumberServiceServiceLocator = new RoutingNumberServiceServiceLocator();
    		String routingNumberServiceServiceName = routingNumberServiceServiceLocator.getRoutingNumberServiceWSDDServiceName();
    		routingNumberServiceServiceLocator.setRoutingNumberServiceEndpointAddress(System.getProperty("com.yodlee.soap.services.url") 
    				+ "/" + routingNumberServiceServiceName);
    		try{
    			rns = routingNumberServiceServiceLocator.getRoutingNumberService();
    		} catch(Exception lse) {
    			lse.printStackTrace();
    		}
        	RoutingNumberInfo rni = rns.getRoutingNumberInfoByRoutingNumber(cobCxt, RoutingNumber);
            System.out.println("Routing No Info : " + rni.getRoutingNumber());
            if (rni == null)
            {
                System.out.println("System cannot find the routing number.");
                return false;
            }
            if (rni.getContentServiceId().intValue() > 0)
            { 
            	ContentServiceTraversal cst = null;
            	ContentServiceTraversalServiceLocator contentServiceTraversalServiceLocator = new ContentServiceTraversalServiceLocator();
        		String contentServiceTraversalServiceName = contentServiceTraversalServiceLocator.getContentServiceTraversalServiceWSDDServiceName();
        		contentServiceTraversalServiceLocator.setContentServiceTraversalServiceEndpointAddress(System.getProperty("com.yodlee.soap.services.url") 
        				+ "/" + contentServiceTraversalServiceName);
        		try{
        			cst = contentServiceTraversalServiceLocator.getContentServiceTraversalService();
        		} catch(Exception lse) {

        		}            	
            	ContentServiceInfo csi = cst.getContentServiceInfo(cobCxt, rni.getContentServiceId());
                if (csi.isIsIAVFastSupported())
                {
                    System.out.println("Routing number supports IAV.");
                    return true;
                }
            }
            System.out.println("Routing number does not supports IAV.");
            return false;

        }
        catch (RoutingNumberNotFoundExceptionFault eR)
        {
        	System.out.println("System cannot find the routing number.");
	        System.out.println("");
            return false;
        }
        catch (Exception e) 
		{
	        e.printStackTrace();
	        System.out.println("");
            return false;
        }
    }

    public boolean pollNewIAVStatus(UserContext userContext, DFIAccount dfiAccount)
    {
    	boolean isAcctfailed = false;    	
    	// You have to check for success until refresh interval.. then see if failed..
    	// If both pass then refresh has timed out..    	
        try 
        {
        	VerificationInfo[] vInfo = null;
        	int attempts1 = 0;
        	
        	// Check for success..
        	while (attempts1 <= 10) /*(dt2.before(dt1)) */
        	{
        		System.out.println("\tChecking the refresh status of Account=" + dfiAccount.getAccountNumber() + " for success...");
        		ArrayOfVerificationInfo arrayOfVerificationInfo = iavService.getVerificationInfosForVerificationRequests
        																				(userContext, VerifiableAccountCategory.DFI_ACCOUNT,InstantAccountVerificationStatus_SUCCEEDED);
        		if(arrayOfVerificationInfo != null){
        			vInfo = arrayOfVerificationInfo.getElements();
        		}
        		if (vInfo != null && vInfo.length > 0)
        		{
        			for (int index = 0; index < vInfo.length; index++)
        			{
        				if (((InstantAccountVerificationInfo)vInfo[index]).getVerifiableAccount().getVerifiableTargetAccount().getTargetAccountId().equals(dfiAccount.getDfiAccountId()))
        				{
        					isAcctfailed = false;
        					System.out.println("\t\tIAV success.");
        					return isAcctfailed;
        				}
        			}
        		}
                // Sleep
                Thread.sleep(10 * 1000); // sleep for 10 seconds.
                attempts1++;
        	}
        	
            // Check for failure..
        	System.out.println("\tPast refresh time interval.Checking the refresh status of Account=" + dfiAccount.getAccountNumber() + " for failure...");
            vInfo = null;
            ArrayOfVerificationInfo arrayOfVerificationInfo = iavService.getVerificationInfosForVerificationRequests
			(userContext, VerifiableAccountCategory.DFI_ACCOUNT,InstantAccountVerificationStatus_FAILED);
    		if(arrayOfVerificationInfo != null){
    			vInfo = arrayOfVerificationInfo.getElements();
    		}
    		if (vInfo != null && vInfo.length > 0)
    		{
    			for (int index = 0; index < vInfo.length; index++)
    			{
    				if (((InstantAccountVerificationInfo)vInfo[index]).getVerifiableAccount().getVerifiableTargetAccount().getTargetAccountId().equals(dfiAccount.getDfiAccountId()))
    				{
    					isAcctfailed = true;
    					if (((InstantAccountVerificationInfo)vInfo[index]).getStatusCode().equals(Long.getLong("402")))
    					{
    						System.out.println("\tIAV cannot be performed. Bad Username or Password");
    						return isAcctfailed;
    					}
    					else if (((InstantAccountVerificationInfo)vInfo[index]).getStatusCode().equals(Long.getLong("404")))
    					{
    						System.out.println("\tIAV cannot be performed. Destination site is not available");
    						return isAcctfailed;
    					}
    					else
    					{
    						System.out.println("\tRefresh failed with status code " + ((InstantAccountVerificationInfo)vInfo[index]).getStatusCode());
    						return isAcctfailed;
    					}
                        
    				}
    			}
    		}
    		
    		isAcctfailed = false;
			System.out.println("\t\tRefresh time out.");
			return isAcctfailed;

        } catch (Exception e1)  
        {
        	e1.printStackTrace();
			isAcctfailed = false;
			System.out.println("\tVerify Failed.");
			return isAcctfailed;
        }
    }

}
