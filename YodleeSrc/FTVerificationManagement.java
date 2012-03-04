package com.yodlee.sampleapps;

import java.math.BigDecimal;
import java.rmi.RemoteException;

import com.yodlee.soap.core.verification.iav.ExtendedInstantAccountVerificationInfo;
import com.yodlee.soap.collections.core.verification.ArrayOfVerificationInfo;
import com.yodlee.soap.collections.core.verification.ArrayOfVerificationScheme;
import com.yodlee.soap.collections.core.verification.cd.ArrayOfChallengeMoney;
import com.yodlee.soap.collections.core.verification.iav.ArrayOfInstantAccountVerificationMatchData;
import com.yodlee.soap.common.CobrandContext;
import com.yodlee.soap.common.ContentServiceInfo;
import com.yodlee.soap.common.Money;
import com.yodlee.soap.common.UserContext;
import com.yodlee.soap.core.CoreExceptionFault;
import com.yodlee.soap.core.IllegalArgumentValueExceptionFault;
import com.yodlee.soap.core.InvalidConversationCredentialsExceptionFault;
import com.yodlee.soap.core.InvalidUserContextExceptionFault;
import com.yodlee.soap.core.StaleConversationCredentialsExceptionFault;
import com.yodlee.soap.core.fundstransfer.transferaccountmanagement.BankTransferAccount;
import com.yodlee.soap.core.fundstransfer.transferaccountmanagement.DFIAccount;
import com.yodlee.soap.core.fundstransfer.transferaccountmanagement.TransferAccount;
import com.yodlee.soap.core.fundstransfer.transferaccountmanagement.TransferAccountNotFoundExceptionFault;
import com.yodlee.soap.core.fundstransfer.transfermanagement.PendingTransfersExistsExceptionFault;
import com.yodlee.soap.core.mfarefresh.MFARefreshInfo;
import com.yodlee.soap.core.refresh.RefreshStatus;
import com.yodlee.soap.core.routingnumberservice.RoutingNumberInfo;
import com.yodlee.soap.core.routingnumberservice.RoutingNumberNotFoundExceptionFault;
import com.yodlee.soap.core.verification.instantaccountverificationservice.InstantAccountVerificationService;
import com.yodlee.soap.core.verification.instantaccountverificationservice.InstantAccountVerificationServiceServiceLocator;
import com.yodlee.soap.core.verification.VerifiableAccount;
import com.yodlee.soap.core.verification.VerifiableAccountCategory;
import com.yodlee.soap.core.verification.verifiableaccountservice.VerifiableAccountService;
import com.yodlee.soap.core.verification.verifiableaccountservice.VerifiableAccountServiceServiceLocator;
import com.yodlee.soap.core.verification.IAVRefreshStatus;
import com.yodlee.soap.core.verification.VerifiableTargetAccount;
import com.yodlee.soap.core.verification.VerificationInfo;
import com.yodlee.soap.core.verification.VerificationScheme;
import com.yodlee.soap.core.verification.VerificationStatus;
import com.yodlee.soap.core.verification.cd.ChallengeMoney;
import com.yodlee.soap.core.verification.cd.ChallengeRequestInfo;
import com.yodlee.soap.core.verification.cd.ChallengeResponse;
import com.yodlee.soap.core.verification.cd.ChallengeType;
import com.yodlee.soap.core.verification.cd.ChallengeVerificationInfo;
import com.yodlee.soap.core.verification.cd.ChallengeVerificationStatus;
import com.yodlee.soap.core.verification.cd.exceptions.MaximumUserResponsesExceededExceptionFault;
import com.yodlee.soap.core.verification.exceptions.IllegalVerificationStateExceptionFault;
import com.yodlee.soap.core.verification.exceptions.InvalidVerifiableAccountExceptionFault;
import com.yodlee.soap.core.verification.exceptions.MaximumAttemptsExceededExceptionFault;
import com.yodlee.soap.core.verification.exceptions.NoSuchVerifiableTargetAccountExceptionFault;
import com.yodlee.soap.core.verification.exceptions.VerifiableAccountAlreadyAssociatedExceptionFault;
import com.yodlee.soap.core.verification.exceptions.VerificationNeverInitiatedExceptionFault;
import com.yodlee.soap.core.verification.exceptions.VerificationSchemeChangedExceptionFault;
import com.yodlee.soap.core.verification.exceptions.VerificationSchemeNeverUsedExceptionFault;
import com.yodlee.soap.core.verification.iav.InstantAccountVerificationInfo;
import com.yodlee.soap.core.verification.iav.InstantAccountVerificationMatchData;
import com.yodlee.soap.core.verification.iav.exception.InstantAccountVerificationNotSupportedExceptionFault;
import com.yodlee.soap.ext.traversal.contentservicetraversal.ContentServiceTraversal;
import com.yodlee.soap.ext.traversal.contentservicetraversal.ContentServiceTraversalServiceLocator;
import com.yodlee.sampleapps.helper.IFileTransferConstants;
import com.yodlee.sampleapps.helper.IOUtils;
import com.yodlee.sampleapps.helper.ValidationUtil;
import com.yodlee.soap.core.verification.iav.IAVMatchCode;

/**
 * This class handles the FT Verification Managament Related Activities. 1.
 * Verify CD. 2. Retrive Verification Scheme. 3. Display Verification
 * Information. 4. Initiate CD 5. Initiate IAV
 */
public class FTVerificationManagement extends FundsTransfer {
	
    private static final int ChallengeVerificationStatus_INITIATED = 1;
    private static final int ChallengeVerificationStatus_CD_INSTRUCTIONS_COMPLETED = 2;
    private static final int ChallengeVerificationStatus_CD_INSTRUCTIONS_FAILED = 3;
    private static final int ChallengeVerificationStatus_SUCCESS = 4;
    private static final int ChallengeVerificationStatus_FAILED = 5;
    
    private static final int InstantAccountVerificationStatus_INITIATED = 1;
    private static final int InstantAccountVerificationStatus_SUCCEEDED = 2;
    private static final int InstantAccountVerificationStatus_FAILED = 3;
    

    /**
     * Verifies a transfer Account via CD. 1. Prompt user for entering deposit 1
     * and deposit 2 2. Verify these deposits against actual deposits.
     *
     * @param userContext
     *            The user context of the caller
     */
    public void doVerifyChallengeDeposit(UserContext userContext) {

        String transferAcctId = null;
      
      

        // prompt for transfer Account ID
        transferAcctId = IOUtils.promptInput(
                IFileTransferConstants.TransAcctID2Prompt,
                IFileTransferConstants.TransAcctID2ReEnterPrompt);
       /* 
        String amount1 = null;
        String amount2 = null;
        double validateAmt1 = 0.00;
        double validateAmt2 = 0.00;

        // prompt user for deposit amount1
        amount1 = IOUtils.promptInput(IFileTransferConstants.Amount1Prompt,
                IFileTransferConstants.Amount1ReEnterPrompt);
        validateAmt1 = ValidationUtil.getValidAmount(amount1);

        // prompt user for deposit amount1
        amount2 = IOUtils.promptInput(IFileTransferConstants.Amount2Prompt,
                IFileTransferConstants.Amount2ReEnterPrompt);

        validateAmt2 = ValidationUtil.getValidAmount(amount2);

        if (!(validateAmt1 == 0.00 || validateAmt2 == 0.00))
        	*/
            VerifyChallengeDeposit(userContext, transferAcctId);

    }

    /**
     * Verifies the Challenge Deposits for the user. This API provides a
     * mechanism in which the end user's reponse can be processed. This API does
     * the processing of the user response iff at the time of response the
     * VerifiableAccount is still being verified through the
     * VerificationScheme.CHALLENGE_DEPOSIT scheme, else it throws a
     * VerificationSchemeChangedException This method is responsible for
     * processing the response amounts entered by the user for challenge
     * verification. The order in which the amounts were deposited and in which
     * the user has responded is not considered. For Example: if 2 micro
     * deposits of 0.1$ and 0.3$ were made into the VerifiableTargetAccount and
     * if the user responds with the inputs as 0.3$ and 0.1$, it is considered
     * on par with a reponse having inputs as 0.1$ and 0.3$. If the user reponse
     * matches with the deposits made then the status of the VerificationInfo is
     * set to ChallengeVerificationStatus.SUCCESS and returned back to the user,
     * else the status of the VerificationInfo is set to
     * ChallengeVerificationStatus.FAILED and returned back to the user There is
     * a limit on the number of times an end user can provide response. The
     * limit is configurable on a cobrand basis
     * COM.YODLEE.CORE.VERIFICATION.CD.MAX_RESPONSE_LIMIT, by default its value
     * is set to 3. If the number of times an end user can provide response has
     * exceeded the limit and still hasnt provided a correct response then the
     * status of the VerifiableAccount is set to
     * VerifiableAccountStatus.STATUS_FAILED.
     *
     * @param userContext
     * @param transferAcctId -
     *            TransferAccountId to be Verified.
     * @param amount1 -
     *            Deposit Amount 1
     * @param amount2 -
     *            Deposit Amount 2
     */
    public void VerifyChallengeDeposit(UserContext userContext,
                                       String transferAcctId) {


    	TransferAccount transferAccount = null;
		try {
			transferAccount = transferAccountManagement.getTransferAccount(userContext, Long.parseLong(transferAcctId));
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
    	
    	// get Verifiable Account
        VerifiableAccount verifiableAccount = getVerifiableAccount(userContext,
        		dfiAccount.getDfiAccountId());
        
        ChallengeRequestInfo challengeReqInfo = null;
        
        try {
        	
        challengeReqInfo = challengeVerificationService.getLatestChallengeRequestInfo(userContext, verifiableAccount);
        } catch(Exception e){
        	e.printStackTrace();
        }
        Long numCredits = challengeReqInfo.getNumOfCredits();
        Long numDebits = challengeReqInfo.getNumOfDebit();
        int totalCDInst = 0;
        int counter = 0;
        if (numCredits != null){
        	totalCDInst = totalCDInst + numCredits.intValue();
        	counter = numCredits.intValue();
        }
        if (numDebits != null){
        	totalCDInst = totalCDInst + numDebits.intValue();
        }
        System.out.println("total number of CD instruction: " + totalCDInst);
        ChallengeResponse challengeResponse = null;
        if (totalCDInst > 0){
        	
        	ArrayOfChallengeMoney arrayOfChallengeMoney = new ArrayOfChallengeMoney();
            ChallengeMoney[] responseAmounts = new ChallengeMoney[totalCDInst];
            String amount = null;
            double validateAmt = 0.00;
           
        	if (numCredits != null){
             for (int i=0; i<numCredits.intValue(); i++){
               
                amount = IOUtils.promptInput(IFileTransferConstants.CDCreditAmountPrompt + ": ",
                        IFileTransferConstants.Amount1ReEnterPrompt);
                validateAmt = ValidationUtil.getValidAmount(amount);
    			Money money = new Money();
    			money.setAmount(new BigDecimal(amount));
    			money.setCurrencyCode(IFileTransferConstants.CURR_CODE);
    			ChallengeMoney challengeMoney = new ChallengeMoney();
    			challengeMoney.setMoney(money);
    			challengeMoney.setTransactionType(ChallengeType.CREDIT);

    			responseAmounts[i] = challengeMoney;

                             
               
        	}
          }
          if (numDebits != null){
                for (int i= counter ; i<totalCDInst; i++){
                  
                   amount = IOUtils.promptInput(IFileTransferConstants.CDDebitAmountPrompt  + ": ",
                           IFileTransferConstants.Amount1ReEnterPrompt);
                   validateAmt = ValidationUtil.getValidAmount(amount);
       			Money money = new Money();
    			money.setAmount(new BigDecimal(amount));
    			money.setCurrencyCode(IFileTransferConstants.CURR_CODE);
    			ChallengeMoney challengeMoney = new ChallengeMoney();
    			challengeMoney.setMoney(money);
    			challengeMoney.setTransactionType(ChallengeType.DEBIT);

    			responseAmounts[i] = challengeMoney;

                             
                   
           	}
           }
          
          arrayOfChallengeMoney.setElements(responseAmounts);
           challengeResponse = new ChallengeResponse();
           challengeResponse.setAmounts(arrayOfChallengeMoney);
          
        }
        
        

        try {
            ChallengeVerificationStatus challengeVerificationStatus = (ChallengeVerificationStatus) challengeVerificationService
                    .processUserResponse(userContext, verifiableAccount,
                            challengeResponse);

            if (challengeVerificationStatus.getVerificationStatusId()== ChallengeVerificationStatus_SUCCESS) {
                System.out.println("Account verified sucessfully");
                
                /*
                 * Once the user response is verified sucessfully we need to call
                 * following API to mark the account as verified.
                 */
                
                
              transferAccountManagement.markTransferAccountAsVerified(userContext,Long.parseLong(transferAcctId));

            } else if (challengeVerificationStatus.getVerificationStatusId()== ChallengeVerificationStatus_FAILED) {
                System.out
                        .println("The account you added could not be verified for transfers "
                                + "because you entered the wrong deposit amounts too many times.");
            } else if (challengeVerificationStatus.getVerificationStatusId() == ChallengeVerificationStatus_CD_INSTRUCTIONS_COMPLETED) {
                System.out
                        .println("One or more deposit amounts was incorrect. "
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
        } catch (MaximumUserResponsesExceededExceptionFault e) {
            e.printStackTrace();
            System.out
                    .println("Maximum attempts for response verification exceeded!");
        } catch (VerificationSchemeChangedExceptionFault e) {
            e.printStackTrace();
            System.out.println("The current Verification scheme is not "
                    + VerificationScheme.CHALLENGE_DEPOSIT);
        } catch (IllegalVerificationStateExceptionFault e) {
            e.printStackTrace();
            System.out
                    .println("User cannot provide response for the Challenge "
                            + "Request when it is still in Initiated state.");
        } catch (TransferAccountNotFoundExceptionFault e) {
            e.printStackTrace();
            System.out.println("Exception happened while fetching account!");
        } catch (StaleConversationCredentialsExceptionFault e) {
        	System.out.println("\n Stale Conversation Credentials Exception....");
        	e.printStackTrace();
		} catch (CoreExceptionFault e) {
			System.out.println("\n Core Exception....");
			e.printStackTrace();
		} catch (InvalidVerifiableAccountExceptionFault e) {
			System.out.println("\n Invalid Verifiable Account Exception....");
			e.printStackTrace();
		} catch (IllegalArgumentValueExceptionFault e) {
			System.out.println("\n Illegal Argument Value Exception....");
			e.printStackTrace();
		} catch (RemoteException e) {
			System.out.println("\n Remote Exception....");
			e.printStackTrace();
		}

    }
    
    public void markAccountVerified(UserContext userContext) {
        String transferAcctId = IOUtils.promptInput(
                IFileTransferConstants.TransAcctID2Prompt,
                IFileTransferConstants.TransAcctID2ReEnterPrompt);

        TransferAccount transferAccount = null;
		try {
			transferAccount = transferAccountManagement.getTransferAccount(userContext, Long.parseLong(transferAcctId));
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

        Boolean isVerifiableAccountAssociated = false;
		try {
			isVerifiableAccountAssociated = verifiableAccountService.isVerifiableAccountAssociated(userContext, verifiableTargetAccount);
		} catch (StaleConversationCredentialsExceptionFault scce) {
        	System.out.println("\n Stale Conversation Credentials Exception....");
        	scce.printStackTrace();
		} catch (InvalidConversationCredentialsExceptionFault ic) {
			System.out.println("Invalid Conversation Credentials Exception");
			ic.printStackTrace();
		} catch (NoSuchVerifiableTargetAccountExceptionFault nvtae) {
			System.out.println("\n No Such Verifiable Target Account Exception....");
			nvtae.printStackTrace();
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
        VerifiableAccount verifiableAccount = null;
        
        	try {
        		if(Boolean.FALSE.equals(isVerifiableAccountAssociated)) {
                	System.out.println("No Verifiable Account Associated.  TODO: Get One");
				verifiableAccount = verifiableAccountService.associateVerifiableAccount(
						userContext, verifiableTargetAccount);
        		} else {
                	verifiableAccount=verifiableAccountService.getVerifiableAccount(userContext, verifiableTargetAccount);
                }
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
     * Gets verification scheme for the account. i.e. particular account was
     * verified against which schemes like CD /IAV
     *
     * @param userContext
     *            The user context of the caller
     * @param verifiableAcct
     *            VerifiableAccount object to find out the verification scheme
     * @return VerificationScheme Array of verification scheme with which some
     *         particular account was verified.
     */
    private VerificationScheme[] getVerificationScheme(UserContext userContext,
                                                       VerifiableAccount verifiableAcct) {
    	ArrayOfVerificationScheme arrayOfVerificationScheme =null;
    	VerificationScheme[] veriSchemes = null;
        try {
        	arrayOfVerificationScheme =  verifiableAccountService
                    .getVerificationSchemes(userContext, verifiableAcct);
        	if(arrayOfVerificationScheme != null){
        		veriSchemes = arrayOfVerificationScheme.getElements();
        	}
        } catch (InvalidUserContextExceptionFault iu) {
            System.out.println("Invalid User Context");
            System.out.println(iu.toString());
        } catch (InvalidConversationCredentialsExceptionFault ic) {
            System.out.println("Invalid Cobrand Context Exception");
            System.out.println(ic.toString());
        } catch (VerificationNeverInitiatedExceptionFault veri) {
            System.out
                    .println("Verification was never initiated for this particular account");
            System.out.println(veri.toString());
        } catch (InvalidVerifiableAccountExceptionFault acct) {
            System.out.println("Invalid Verifiable account");
            System.out.println(acct.toString());

        } catch (IllegalArgumentValueExceptionFault value) {
            System.out
                    .println("value for verifiable account or transfer account is invalid");
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
        return veriSchemes;
    }

    /**
     * Gets verification info for CD verfication.
     *
     * @param userContext
     *            The user context of the caller
     * @param verifiableAcct
     *            VerifiableAccount object to find out the verification scheme
     */
    private ChallengeVerificationInfo getCDVerificationInfo(
            UserContext userContext, VerifiableAccount verifiableAcct) {
        ChallengeVerificationInfo CDVerificationInfo = null;
        try {
            CDVerificationInfo = (ChallengeVerificationInfo) verifiableAccountService
                    .getVerificationInfo1(userContext, verifiableAcct);
        } catch (InvalidUserContextExceptionFault iu) {
            System.out.println("Invalid User Context");
            System.out.println(iu.toString());
        } catch (InvalidConversationCredentialsExceptionFault ic) {
            System.out.println("Invalid Cobrand Context Exception");
            System.out.println(ic.toString());
        } catch (InvalidVerifiableAccountExceptionFault acct) {
            System.out.println("Invalid Verifiable account");
            System.out.println(acct.toString());
        } catch (VerificationNeverInitiatedExceptionFault veri) {
            System.out
                    .println("Verification was never initiated for this particular account");
            System.out.println(veri.toString());
        } catch (VerificationSchemeNeverUsedExceptionFault scheme) {
            System.out
                    .println("Given VerificationScheme was never used to verify the given VerifiableAccount");
            System.out.println(scheme.toString());
        } catch (IllegalArgumentValueExceptionFault value) {
            System.out
                    .println("value for verifiable account or transfer account is invalid");
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
        return CDVerificationInfo;
    }

    /**
     * Gets verification info for Instant verfication.
     *
     * @param userContext
     *            The user context of the caller
     * @param verifiableAcct
     *            VerifiableAccount object to find out the verification scheme
     */
    private InstantAccountVerificationInfo getIAVVerificationInfo(
            UserContext userContext, VerifiableAccount verifiableAcct) {
        InstantAccountVerificationInfo IAVerificationInfo = null;
        try {
            IAVerificationInfo = (InstantAccountVerificationInfo) verifiableAccountService
                    .getVerificationInfo1(userContext, verifiableAcct);
        } catch (InvalidUserContextExceptionFault iu) {
            System.out.println("Invalid User Context");
            System.out.println(iu.toString());
        } catch (InvalidConversationCredentialsExceptionFault ic) {
            System.out.println("Invalid Cobrand Context Exception");
            System.out.println(ic.toString());
        } catch (InvalidVerifiableAccountExceptionFault acct) {
            System.out.println("Invalid Verifiable account");
            System.out.println(acct.toString());

        } catch (VerificationNeverInitiatedExceptionFault veri) {
            System.out
                    .println("Verification was never initiated for this particular account");
            System.out.println(veri.toString());
        } catch (VerificationSchemeNeverUsedExceptionFault scheme) {
            System.out
                    .println("Given VerificationScheme was never used to verify the given VerifiableAccount");
            System.out.println(scheme.toString());
        } catch (IllegalArgumentValueExceptionFault value) {
            System.out
                    .println("value for verifiable account or transfer account is invalid");
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
        return IAVerificationInfo;
    }

    /**
     * Displays the verificatin information about an account like Max No. of
     * tries Maximum user responses Total number of tries user attempted Toatal
     * user responses received
     *
     * @param userContext
     *            The user context of the caller
     * @throws IllegalArgumentValueExceptionFault 
     */
 /*   public void dispAcctVarificationInfo(UserContext userContext) {

        // get transfer account ID
        String transferAccountID = IOUtils.promptInput(
                IFileTransferConstants.TransAcctID2Prompt,
                IFileTransferConstants.TransAcctID2ReEnterPrompt);

        // get Verifiable Account
        VerifiableAccount verifiableAcct = getVerifiableAccount(userContext,
                new Long(transferAccountID));

        // Get verificationScheme
        VerificationScheme[] veriSchemes = getVerificationScheme(userContext,
                verifiableAcct);

        if (veriSchemes[0].intValue() == 2) {
            System.out
                    .println("Verification Schemes is: Challenge Verification");
            // get CD VerificationInfo
            ChallengeVerificationInfo CDVerificationInfo = getCDVerificationInfo(
                    userContext, verifiableAcct);
            System.out.println("\n");
            System.out.println("*******CD Verification Info details******");
            System.out.println("1.Challenge Request ID "
                    + CDVerificationInfo.getChallengeRequestId());
            System.out.println("2.Maximum Tries		   "
                    + CDVerificationInfo.getMaxRetries());
            System.out.println("3.Max User Responses   "
                    + CDVerificationInfo.getMaxUserResponses());
            System.out.println("4.Number Of Retries    "
                    + CDVerificationInfo.getNumberOfRetries());
            System.out.println("5.Verification Info Id "
                    + CDVerificationInfo.getVerificationInfoId());
            System.out.println("6.NumUser Reponses     "
                    + CDVerificationInfo.getNumUserReponses());
            System.out.println("\n");
        } else {
            System.out.println("Verification Schemes is: Instant Verification");
            // get IA VerificationInfo
            InstantAccountVerificationInfo IAVerificationInfo = getIAVVerificationInfo(
                    userContext, verifiableAcct);
            System.out.println("\n");
            System.out
                    .println("******Instant Account Verification Info details******");
            System.out.println("1.Challenge Request ID "
                    + IAVerificationInfo.getVerificationStatus());
            System.out.println("2.Maximum Tries		   "
                    + IAVerificationInfo.getMaxRetries());
            System.out.println("3.Number Of Retries    "
                    + IAVerificationInfo.getNumberOfRetries());
            System.out.println("4.Verification Info Id "
                    + IAVerificationInfo.getVerificationInfoId());
            System.out.println("\n");
        }
    }*/
    
    public void dispAcctVarificationInfo(UserContext userContext) throws IllegalArgumentValueExceptionFault {

        // get transfer account ID
        String transferAccountID = IOUtils.promptInput(
                IFileTransferConstants.TransAcctID2Prompt,
                IFileTransferConstants.TransAcctID2ReEnterPrompt);
        
        //FTAccountManagement acctMgmt = new FTAccountManagement();
        TransferAccount transferAccount = null;//acctMgmt.linkTransferAcctToItem(userContext, Long.parseLong(transferAccountID));
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
    	
//		Create VerifiableAccountService Locator 
    	VerifiableAccountService verifiableAccountService = null;
		VerifiableAccountServiceServiceLocator verifiableAccountServiceServiceLocator = new VerifiableAccountServiceServiceLocator();
		String verifiableAccountServiceServiceName = verifiableAccountServiceServiceLocator.getVerifiableAccountServiceWSDDServiceName();
		verifiableAccountServiceServiceLocator.setVerifiableAccountServiceEndpointAddress(System.getProperty("com.yodlee.soap.services.url") 
				+ "/" + verifiableAccountServiceServiceName);
		try{
			verifiableAccountService = verifiableAccountServiceServiceLocator.getVerifiableAccountService();
		}catch(Exception lse) {

			}
    	
		VerifiableTargetAccount targetAccount = new VerifiableTargetAccount();
		targetAccount.setTargetAccountId(dfiAccount.getDfiAccountId());
		targetAccount.setTargetAccountCategory(VerifiableAccountCategory.DFI_ACCOUNT);

       
        Boolean isVerifiableAccountAssociated = null;
        VerifiableAccount verifiableAcct = null;
		try {
			isVerifiableAccountAssociated = verifiableAccountService.isVerifiableAccountAssociated(userContext, targetAccount);
			if (!isVerifiableAccountAssociated.booleanValue()) {
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
        	if (1 == transferAccount.getIsVerified()) {
   			 System.out.println("Is Account Verified YES");
   		    }
   		   else {
   			 System.out.println("Is Account Verified NO");
   		   }
        }
        	else if(verificationScheme != null && verificationScheme.IAV.equals(verificationScheme)){
        		System.out.println("latest verification Scheme used is IAV");
        
        try {
//    		Create InstantAccountVerificationService Locator 
        	InstantAccountVerificationService iavService = null;
    		InstantAccountVerificationServiceServiceLocator instantAccountVerificationServiceServiceLocator = new InstantAccountVerificationServiceServiceLocator();
    		String instantAccountVerificationServiceServiceName = instantAccountVerificationServiceServiceLocator.getInstantAccountVerificationServiceWSDDServiceName();
    		instantAccountVerificationServiceServiceLocator.setInstantAccountVerificationServiceEndpointAddress(System.getProperty("com.yodlee.soap.services.url") 
    				+ "/" + instantAccountVerificationServiceServiceName);
    		try{
    			iavService = instantAccountVerificationServiceServiceLocator.getInstantAccountVerificationService();
    		}catch(Exception lse) {

    			}
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
        		 if (1 == transferAccount.getIsVerified()) {
          			 System.out.println("Is Account Verified YES");
          		    }
          		   else {
          			 System.out.println("Is Account Verified NO");
          		   }
        }
    }
        
    /**
     * Initiates Challenge Deposit Verification.
     * <p>
     * Challenge Deposit Verification This file provides Funds transfer API for
     * initiating Challenge deposit, getting status of challenge deposit etc.
     * Following are the steps which shows how challenge deposit account
     * verification works 1. The provider, in this case, Yodlee, depositing a
     * random de minimus amount of money in the VerifiableTargetAccount
     * associated with the given VerifiableAccount.The number of deposits made
     * is configurable on a cobrand basis
     * COM.YODLEE.CORE.VERIFICATION.CD.NUM_CREDITS by default the value is 2. 2.
     * When the VerifiableAccount is created using
     * com.yodlee.core.verification.VerifiableAccountService.associateVerifiableAccount
     * API, it will be in VerifiableAccountStatus.UNVERIFIED. once the process
     * of verification is initiated through this API, the status of the
     * VerifiableAccount gets updated to VerifiableAccountStatus.INPROGRESS. At
     * the same time a ChallengeVerifiableInfo gets created and its status will
     * be set to ChallengeVerificationStatus.INITIATED.
     * ChallengeVerifiableStatus provides a more fine grained information on the
     * verification status. 3. Once the amounts are successfully deposited in
     * the VerifiableTargetAccount the status of the corresponding
     * ChallengeVerifiableInfo gets updated to
     * ChallengeVerificationStatus.CD_INSTRUCTIONS_COMPLETED. If the amounts
     * were not successfully deposited in the VerifiableTargetAccount the status
     * of the corresponding ChallengeVerifiableInfo gets updated to
     * ChallengeVerificationStatus.CD_INSTRUCTIONS_FAILED. 4. Once the
     * VerifiableAccountStatus becomes
     * ChallengeVerificationStatus.CD_INSTRUCTIONS_COMPLETED the system is ready
     * to receive the response from the end user about the details of the
     * deposits made. 5. Every time a verification process is initiated under
     * any VerificationScheme, the number of time the scheme has been initiated
     * on the VerifiableAccount is recorded. Once the number of initiations
     * exceeds the COM.YODLEE.CORE.VERIFICATION.CD.MAX_VERIFICATION_LIMIT an
     * MaximumAttemptsExceededException is thrown. There is a limit on the
     * number of times a VerifiableAccount in the system can be verified under a
     * particular VerificationScheme. This is basically done to prevent mis-use
     * of the verification service and also acts as a very rudimentary check
     * against frauds The limit is configurable on a cobrand basis
     * COM.YODLEE.CORE.VERIFICATION.CD.MAX_VERIFICATION_LIMIT by default the
     * value is 3. In case of InstantAccountVerificationService there may not be
     * any limits as such.
     *
     * @param userContext
     *            The user context of the caller
     */
    public void StartCDAccountVerification(UserContext userContext) {
        // get transfer account ID
        String transferAccountID = IOUtils.promptInput(
                IFileTransferConstants.TransAcctID2Prompt,
                IFileTransferConstants.TransAcctID2ReEnterPrompt);

        //Need to check if Routing Number supports CD..
        FTAccountManagement acctMgmt = new FTAccountManagement();
        TransferAccount transferAccount = acctMgmt.getTransferAcct(userContext, Long.parseLong(transferAccountID));
    	DFIAccount dfiAccount=((BankTransferAccount)transferAccount).getDfiAccount();
    	Long dfiAccountId = dfiAccount.getDfiAccountId();
    	// Need to check if Routing Number supports IAV..
        if (!ChkIfACHEnabled(getCobrandContext(), dfiAccount.getRoutingNumber()))
        {
            return;
        }
        
        // get Verifiable Account
        VerifiableAccount verifiableAcct = getVerifiableAccount(userContext,
        		dfiAccountId);

        if (challengeVerificationService == null)
            System.out.println("Service is null");
        else
            try {
                challengeVerificationService.startAccountVerification(
                        userContext, verifiableAcct);
            } catch (InvalidUserContextExceptionFault iu) {
                System.out.println("Invalid User Context");
                System.out.println(iu.toString());
            } catch (InvalidConversationCredentialsExceptionFault ic) {
                System.out.println("Invalid Cobrand Context Exception");
                System.out.println(ic.toString());
            } catch (InvalidVerifiableAccountExceptionFault acct) {
                System.out.println("Invalid Verifiable account");
                System.out.println(acct.toString());

            } catch (MaximumAttemptsExceededExceptionFault max) {
                System.out
                        .println("Sorry! Your account varification attempt exceeded the maximum no. of attempts allowed.");
                System.out.println(max.toString());
            } catch (IllegalArgumentValueExceptionFault value) {
                System.out
                        .println("value for verifiable account or transfer account is invalid");
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
        System.out.println("Challenge Verification Initiated");
    }
    public boolean PollNewIAVStatus(UserContext userContext, DFIAccount dfiAccount)
    {
    	boolean isAcctfailed = false;
    	
    	// You have to check for success until refresh interval.. then see if failed..
    	// If both pass then refresh has timed out..
    	
        try 
        {
        	VerificationInfo[] vInfo = null;
        	int attempts1 = 0;

        	//Calendar d1 = Calendar.getInstance();
        	/* Date dt1 = d1.getTime();
        	d1.add(Calendar.MINUTE, 5);
        	Date dt2 = d1.getTime(); */
        	
        	// Check for success..
        	while (attempts1 <= 5) /*(dt2.before(dt1)) */ 
        	{
        		System.out.println("\tChecking the refresh status of Account=" + dfiAccount.getAccountNumber() + " for success...");
        		ArrayOfVerificationInfo arrayOfVerificationInfo = instantAccountVerificationService.getVerificationInfosForVerificationRequests
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
                /*d1 = Calendar.getInstance();
            	dt1 = d1.getTime(); */
        	}
        	
            // Check for failure..
        	System.out.println("\tPast refresh time interval.Checking the refresh status of Account=" + dfiAccount.getAccountNumber() + " for failure...");
            vInfo = null;
            ArrayOfVerificationInfo arrayOfVerificationInfo = instantAccountVerificationService.getVerificationInfosForVerificationRequests
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

    // Check is Account Routing no has CD support
    private boolean ChkIfACHEnabled(CobrandContext cobCxt, String RoutingNumber)
    {
    	
  	
        try
        {
            RoutingNumberInfo rni = rns.getRoutingNumberInfoByRoutingNumber(cobCxt, RoutingNumber);
            System.out.println("Routing No Info : " + rni.toString());

            if (rni == null)
            {
                System.out.println("System cannot find the routing number.");
                return false;
            }

            if (rni.isIsACHSupported())
            {
                System.out.println("Routing number supports CD.");
                return true;
            }

            System.out.println("Routing number does not supports CD.");
            return false;

        }
        catch (RoutingNumberNotFoundExceptionFault eR)
        {
        	System.out.println("System cannot find the routing number.");
            return false;
        }
        catch (Exception e) 
		{
	        e.printStackTrace();
            return false;
        }
    }
    
    // Check is Account Routing no has IAV support
    private boolean ChkIfIAVEnabled(CobrandContext cobCxt, String RoutingNumber)
    {
    	
   	
        try
        {
            RoutingNumberInfo rni = rns.getRoutingNumberInfoByRoutingNumber(cobCxt, RoutingNumber);
            System.out.println("Routing No Info : " + rni.toString());

            if (rni == null)
            {
                System.out.println("System cannot find the routing number.");
                return false;
            }

            if (rni.getContentServiceId().intValue() > 0)
            {
            	/** Initialize the ContentServiceTraversalService  */
//        		Create ContentServiceTraversal Locator 
            	ContentServiceTraversal cst = null;
            	ContentServiceTraversalServiceLocator contentServiceTraversalServiceLocator = new ContentServiceTraversalServiceLocator();
        		String contentServiceTraversalServiceName = contentServiceTraversalServiceLocator.getContentServiceTraversalServiceWSDDServiceName();
        		contentServiceTraversalServiceLocator.setContentServiceTraversalServiceEndpointAddress(System.getProperty("com.yodlee.soap.services.url") 
        				+ "/" + contentServiceTraversalServiceName);
        		try{
        			cst = contentServiceTraversalServiceLocator.getContentServiceTraversalService();
        		}catch(Exception lse) {

        			}
            	
            	ContentServiceInfo csi = cst.getContentServiceInfo(cobCxt, rni.getContentServiceId().longValue());
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
    public void startIAV(UserContext userContext) throws IllegalArgumentValueExceptionFault {

        // account has to be associated to an mem item before IAV starts
        FTAccountManagement acctMgmt = new FTAccountManagement();
        long transferAccountID;
        transferAccountID = Long.parseLong(IOUtils.promptInput(
                IFileTransferConstants.TransAcctID2Prompt,
                IFileTransferConstants.TransAcctID2ReEnterPrompt));
        
       
    	// NEW CODE HERE
        TransferAccount transferAccount = acctMgmt.linkTransferAcctToItem(userContext, transferAccountID);
        long contentServiceId = transferAccount.getContentServiceId();
        
    	DFIAccount dfiAccount=null;
    	dfiAccount=((BankTransferAccount)transferAccount).getDfiAccount();

    	// Need to check if Routing Number supports IAV..
        if (!ChkIfIAVEnabled(getCobrandContext(), dfiAccount.getRoutingNumber()))
        {
            return;
        }
    	
        // set transferAccount.verificationStatus to 0 or false?

    	// VERIFICATION CODEC
//		Create VerifiableAccountService Locator 
        VerifiableAccountService verifiableAccountService = null;
		VerifiableAccountServiceServiceLocator verifiableAccountServiceServiceLocator = new VerifiableAccountServiceServiceLocator();
		String verifiableAccountServiceServiceName = verifiableAccountServiceServiceLocator.getVerifiableAccountServiceWSDDServiceName();
		verifiableAccountServiceServiceLocator.setVerifiableAccountServiceEndpointAddress(System.getProperty("com.yodlee.soap.services.url") 
				+ "/" + verifiableAccountServiceServiceName);
		try{
			verifiableAccountService = verifiableAccountServiceServiceLocator.getVerifiableAccountService();
		}catch(Exception lse) {

			}
		VerifiableTargetAccount targetAccount = new VerifiableTargetAccount();
		targetAccount.setTargetAccountId(dfiAccount.getDfiAccountId());
		targetAccount.setTargetAccountCategory(VerifiableAccountCategory.DFI_ACCOUNT);
        
        Boolean isVerifiableAccountAssociated = false;
        VerifiableAccount verifiableAcct = null;
		try {
			isVerifiableAccountAssociated = verifiableAccountService.isVerifiableAccountAssociated(userContext, targetAccount);
			if (!isVerifiableAccountAssociated.booleanValue()) {
	            verifiableAccountService.associateVerifiableAccount(userContext, targetAccount);
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
            throw new IllegalArgumentValueExceptionFault("VerifiableAccount cannot be NULL.");
        }


    	// END NEW CODE
    	
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
        	ContentServiceHelper csh = new ContentServiceHelper();        
            String mfatype = csh.getMfAType(contentServiceId);  
            if ( mfatype != null ) {
        		eiavi = extendedInstantAccountVerificationService.startVerificationWithMFA1(userContext,
                        verifiableAcct, dfiAccount.getRoutingNumber(), dfiAccount.getAccountNumber());
            	// Get the IAVRefreshStatus object from InstantAccountVerificationInfo
            	IAVRefreshStatus iavRefreshStatus = eiavi.getIavRefreshStatus();
              	long itemId = iavRefreshStatus.getItemId();
              	
            	 //Get the RefreshStatus object out of IAVRefreshStatus object
            	RefreshStatus refreshStatus = iavRefreshStatus.getRefreshStatus();      
            	//Check if IAV request has been sent successfully and requires the intermediate flow ( in case of MFA requests)
        		if (refreshStatus.toString().equals(String.valueOf(RefreshStatus.SUCCESS_REFRESH_WAIT_FOR_MFA.getValue()))) {    			
        			//Get the MFA response from the agent, which contains the MFA questions
        			//The questions will be placed in the queue and the app or SDK calls can poll for these questions continuously
        			MFARefreshInfo mfaInfo = refresh.getMFAResponse(userContext, itemId);
        			MFA mfa = new MFA();
        			int errorCode = mfa.processMFA(userContext, mfaInfo, itemId);
        			if ( errorCode == 0) {
        				System.out.println("MFA site verified successfully");
        			} else if ( errorCode > 0 ){
        				System.out.println("There was an error during the verification of the MFA site. Error code is " + errorCode );
        			}
        		} else if ( refreshStatus.toString().equals(String.valueOf(RefreshStatus.SUCCESS_START_REFRESH.getValue()))) {
        			//This is the usual non-MFA site and verification will be successful    			
        		 }    
        	} else {
        		iavi = instantAccountVerificationService.startVerification1(userContext,
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
    	isAcctfailed = PollNewIAVStatus(userContext,dfiAccount);
    	
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
        			ArrayOfInstantAccountVerificationMatchData arrayOfInstantAccountVerificationMatchData = instantAccountVerificationService.getMatchingAccountVerificationData(userContext, verifiableAcct);
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

    /**
     * Display All Verifications
     * @param userContext
     */
    public void displayAllVerifications(UserContext userContext){
        // Display VerificationInfos
        getVerficationsInProgress(userContext);
        getVerificationInfosForAllPendingRequests(userContext);
        getVerificationInfosForAllFailedRequests(userContext);
        getVerificationInfosForAllSuccessfulRequests(userContext);
    }

    /**
     * Display VerificationInfo
     * @param vis
     */
    public void displayVerificationInfo(VerificationInfo[] vis ){
        if(vis != null){
            for(int i=0; i<vis.length; i++){
                VerificationInfo vi = vis[i] ;
                System.out.println("\t" + i + ". VerificationInfoId=" + vi.getVerificationInfoId() +
                        ", VerificationStatus=" + getVerificationStatus(vi.getVerificationStatus()) );
                System.out.println (" DFI account Id: " +vi.getVerifiableAccount().getVerifiableTargetAccount().getTargetAccountId());
            }
            System.out.println("");
        } else {
            System.out.println("VerificationInfo=null");
        }
    }

    /**
     * Get Verifications InProgress
     * @param userContext
     */
    public void getVerficationsInProgress(UserContext userContext){
        VerifiableAccountCategory verifiableAccountCategory = VerifiableAccountCategory.DFI_ACCOUNT;
        VerificationInfo[] vis = null;
		try {
			ArrayOfVerificationInfo arrayOfVerificationInfo = challengeVerificationService.getVerificationInfosForAllInProgressRequests(userContext,
			        verifiableAccountCategory);
			if(arrayOfVerificationInfo != null){
				vis = arrayOfVerificationInfo.getElements();
			}
		} catch (StaleConversationCredentialsExceptionFault scce) {
        	System.out.println("\n Stale Conversation Credentials Exception....");
			System.out.println(scce.toString());
		}  catch (InvalidConversationCredentialsExceptionFault icce) {
			System.out.println("\n Invalid Conversation Credentials Exception....");
			System.out.println(icce.toString());
		} catch (CoreExceptionFault ce) {
			System.out.println("\n Core Exception....");
			System.out.println(ce.toString());
		}  catch (InvalidUserContextExceptionFault iuce) {
			System.out.println("\n Invalid User Context Exception....");
			System.out.println(iuce.toString());
		} catch (IllegalArgumentValueExceptionFault iave) {
			System.out.println("\n Illegal Argument Value Exception....");
			System.out.println(iave.toString());
		} catch (RemoteException re) {
			System.out.println("\n Remote Exception....");
			System.out.println(re.toString());
		}
        if(vis != null && vis.length > 0){
            System.out.println("VerificationInfos InProgress");
            displayVerificationInfo(vis);
        }
    }

    /**
     * Get Verifications for All Pending Request
     * @param userContext
     */
    public void getVerificationInfosForAllPendingRequests(UserContext userContext){
        VerifiableAccountCategory verifiableAccountCategory = VerifiableAccountCategory.DFI_ACCOUNT;
        VerificationInfo[] vis = null;
		try {
			ArrayOfVerificationInfo arrayOfVerificationInfo = challengeVerificationService.getVerificationInfosForAllPendingRequests(userContext,
			        verifiableAccountCategory);
			if(arrayOfVerificationInfo != null){
				vis = arrayOfVerificationInfo.getElements();
			}
		} catch (StaleConversationCredentialsExceptionFault scce) {
        	System.out.println("\n Stale Conversation Credentials Exception....");
			System.out.println(scce.toString());
		}  catch (InvalidConversationCredentialsExceptionFault icce) {
			System.out.println("\n Invalid Conversation Credentials Exception....");
			System.out.println(icce.toString());
		} catch (CoreExceptionFault ce) {
			System.out.println("\n Core Exception....");
			System.out.println(ce.toString());
		}  catch (InvalidUserContextExceptionFault iuce) {
			System.out.println("\n Invalid User Context Exception....");
			System.out.println(iuce.toString());
		} catch (IllegalArgumentValueExceptionFault iave) {
			System.out.println("\n Illegal Argument Value Exception....");
			System.out.println(iave.toString());
		} catch (RemoteException re) {
			System.out.println("\n Remote Exception....");
			System.out.println(re.toString());
		}
        if(vis != null && vis.length > 0){
            System.out.println("VerificationInfos AllPendingRequests");
            displayVerificationInfo(vis);
        }
    }

    /**
     * Get Verifcations for All Failed Request
     * @param userContext
     */
    public void getVerificationInfosForAllFailedRequests(UserContext userContext){
        VerifiableAccountCategory verifiableAccountCategory = VerifiableAccountCategory.DFI_ACCOUNT;
        VerificationInfo[] vis = null;
		try {
			ArrayOfVerificationInfo arrayOfVerificationInfo = challengeVerificationService.getVerificationInfosForAllFailedRequests(userContext,
			        verifiableAccountCategory);
			if(arrayOfVerificationInfo != null){
				vis = arrayOfVerificationInfo.getElements();
			}
		} catch (StaleConversationCredentialsExceptionFault scce) {
        	System.out.println("\n Stale Conversation Credentials Exception....");
			System.out.println(scce.toString());
		}  catch (InvalidConversationCredentialsExceptionFault icce) {
			System.out.println("\n Invalid Conversation Credentials Exception....");
			System.out.println(icce.toString());
		} catch (CoreExceptionFault ce) {
			System.out.println("\n Core Exception....");
			System.out.println(ce.toString());
		}  catch (InvalidUserContextExceptionFault iuce) {
			System.out.println("\n Invalid User Context Exception....");
			System.out.println(iuce.toString());
		} catch (IllegalArgumentValueExceptionFault iave) {
			System.out.println("\n Illegal Argument Value Exception....");
			System.out.println(iave.toString());
		} catch (RemoteException re) {
			System.out.println("\n Remote Exception....");
			System.out.println(re.toString());
		}
        if(vis != null && vis.length > 0){
            System.out.println("VerificationInfos AllFailedRequests");
            displayVerificationInfo(vis);
        }
    }

    /**
     * Get Verifications for all Successful Reqeusts
     * @param userContext
     */
    public void getVerificationInfosForAllSuccessfulRequests(UserContext userContext){
        VerifiableAccountCategory verifiableAccountCategory = VerifiableAccountCategory.DFI_ACCOUNT;
        VerificationInfo[] vis = null;
		try {
			ArrayOfVerificationInfo arrayOfVerificationInfo = challengeVerificationService.getVerificationInfosForAllSuccessfulRequests(userContext,
			        verifiableAccountCategory);
			if(arrayOfVerificationInfo != null){
				vis = arrayOfVerificationInfo.getElements();
			}
		} catch (StaleConversationCredentialsExceptionFault scce) {
        	System.out.println("\n Stale Conversation Credentials Exception....");
			System.out.println(scce.toString());
		}  catch (InvalidConversationCredentialsExceptionFault icce) {
			System.out.println("\n Invalid Conversation Credentials Exception....");
			System.out.println(icce.toString());
		} catch (CoreExceptionFault ce) {
			System.out.println("\n Core Exception....");
			System.out.println(ce.toString());
		}  catch (InvalidUserContextExceptionFault iuce) {
			System.out.println("\n Invalid User Context Exception....");
			System.out.println(iuce.toString());
		} catch (IllegalArgumentValueExceptionFault iave) {
			System.out.println("\n Illegal Argument Value Exception....");
			System.out.println(iave.toString());
		} catch (RemoteException re) {
			System.out.println("\n Remote Exception....");
			System.out.println(re.toString());
		}
        if(vis != null && vis.length > 0){
            System.out.println("VerificationInfos AllSuccessfulRequests");
            displayVerificationInfo(vis);
        }
    }


    /**
     * Get VerificationStatus
     * @param verificationStatus
     * @return verification status string
     */
    public String getVerificationStatus(VerificationStatus verificationStatus){
        String status = "";
        if(verificationStatus instanceof ChallengeVerificationStatus) {
            if(verificationStatus.getVerificationStatusId() == ChallengeVerificationStatus_CD_INSTRUCTIONS_COMPLETED) {
                status = "CD instructions completed";
            }
            if(verificationStatus.getVerificationStatusId() == ChallengeVerificationStatus_CD_INSTRUCTIONS_FAILED ) {
                status = "CD at least one of the transactions have failed.";
            }
            if(verificationStatus.getVerificationStatusId() == ChallengeVerificationStatus_FAILED) {
                status = "User is not able to provide correct response after repeated attempts";
            }
            if(verificationStatus.getVerificationStatusId() == ChallengeVerificationStatus_INITIATED) {
                status = "CD has been initiated";
            }
            if(verificationStatus.getVerificationStatusId() == ChallengeVerificationStatus_SUCCESS) {
                status = "User provided correct response";
            }
        }
        return status;
    }


}
