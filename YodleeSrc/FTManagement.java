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

import com.yodlee.soap.common.UserContext;
import com.yodlee.soap.core.CoreExceptionFault;
import com.yodlee.soap.core.IllegalArgumentValueExceptionFault;
import com.yodlee.soap.core.InvalidCobrandContextExceptionFault;
import com.yodlee.soap.core.InvalidCobrandConversationCredentialsExceptionFault;
import com.yodlee.soap.core.InvalidConversationCredentialsExceptionFault;
import com.yodlee.soap.core.InvalidUserContextExceptionFault;
import com.yodlee.soap.core.StaleConversationCredentialsExceptionFault;
import com.yodlee.soap.core.fundstransfer.transferaccountmanagement.TransferAccountNotFoundExceptionFault;
import com.yodlee.soap.core.fundstransfer.transfermanagement.ExceedIRAAgeLimitExceptionFault;
import com.yodlee.soap.core.fundstransfer.transfermanagement.ExceedIRABeneficiaryAgeLimitExceptionFault;
import com.yodlee.soap.core.fundstransfer.transfermanagement.ExceedIRAContributionDeadlineExceptionFault;
import com.yodlee.soap.core.fundstransfer.transfermanagement.ExceedIRAContributionLimitExceptionFault;
import com.yodlee.soap.core.fundstransfer.transfermanagement.InvalidTransferAccountExceptionFault;
import com.yodlee.soap.core.fundstransfer.transfermanagement.InvalidTransferAmountExceptionFault;
import com.yodlee.soap.core.fundstransfer.transfermanagement.InvalidTransferDateExceptionFault;
import com.yodlee.soap.core.fundstransfer.transfermanagement.TransferAccountNotVerifiedExceptionFault;
import com.yodlee.soap.core.fundstransfer.transfermanagement.TransferAlreadyInitiatedExceptionFault;
import com.yodlee.soap.core.fundstransfer.transfermanagement.TransferDirection;
import com.yodlee.soap.core.fundstransfer.transfermanagement.TransferLimitViolatedExceptionFault;
import com.yodlee.soap.core.fundstransfer.transfermanagement.transfermanagement.TransferManagement;
import com.yodlee.soap.core.fundstransfer.transfermanagement.TransferMode;
import com.yodlee.soap.core.fundstransfer.transfermanagement.TransferRequest;
import com.yodlee.soap.core.fundstransfer.transfermanagement.transactionchecks.TransferFee;
import com.yodlee.soap.core.fundstransfer.usermanagement.UserSuspendedExceptionFault;
import com.yodlee.sampleapps.helper.FTHelper;
import com.yodlee.sampleapps.helper.IFileTransferConstants;
import com.yodlee.sampleapps.helper.IOUtils;
import com.yodlee.sampleapps.helper.TransferLimitsUtil;

/**
 * Handles all the Funds Transfer Management Related Activities like, Creating a
 * Transfer Requests,Cancelling transfer request.
 */
public class FTManagement extends FundsTransfer {
	
    /**
     *  The transfer request is of type recurring and the user
     * intends to cancel the entire request.
     */
    public static final int CANCEL_FLAG_CANCEL_ALL = 1;

    /**
     * The transfer request is of type recurring and the user
     * intends to cancel only the current transfer.
     */
    public static final int CANCEL_FLAG_CANCEL_CURRENT = 2;

	/**
	 * Creates a transfer requests which could be either One Time or Recurring
	 * for a given pair of Transfer Accounts.
	 * 
	 * @param userContext
	 *            The user context of the caller
	 */
	public void doMakeTransfer(UserContext userContext) {

		// prompt user for source and destination account ID
		String sourceTransferAccountId = IOUtils.promptInput(
				IFileTransferConstants.TransferIdFromPrompt,
				IFileTransferConstants.TransferIdFromReEnterPrompt);

		String destTransferAccountId = IOUtils.promptInput(
				IFileTransferConstants.TransferIdToPrompt,
				IFileTransferConstants.TransferIdToReEnterPrompt);

		

		// prompt user for Amount
		String amount = IOUtils.promptInput(
				IFileTransferConstants.AmountPrompt,
				IFileTransferConstants.AmountReEnterPrompt);

		// get Transfer Mode 1 -> Next Day | 2 -> Standard
		int transferModeId = new Integer(IOUtils.promptInput(
				IFileTransferConstants.TransferMethodPrompt,
				IFileTransferConstants.TransferMethodReEnterPrompt)).intValue();

		int frequency = 0;
		String startDt = null;
		String endDt = null;
		String repeatTransfer = null;

		TransferMode transferMode = TransferMode.STANDARD; // default it to
		// standard

		// WE are hardcoding here that transfer direction is Third Party but
		// actually it needs to be calculated.
		TransferDirection transferDir = TransferDirection.THIRD_PARTY;

		if (transferModeId == 1) {
			// 1 ==> Next Day Service
			// set frequency as one time only.
			transferMode = TransferMode.NEXT_DAY;
			frequency = IFileTransferConstants.ONE_TIME;
			System.out.println("Frequency : "
					+ IFileTransferConstants.ONE_TIME_STRING);
			// Prompt for 1. Schedule Date.
			startDt = IOUtils.promptInput(IFileTransferConstants.StartDtPrompt,
					IFileTransferConstants.StartDtReEnterPrompt);

		} else if (transferModeId == 2) {
			// 2 ==> Standard Service.
			// so Frequency can be either One time or Recurring, hence prompt
			// for Frequency.

			transferMode = TransferMode.STANDARD;
			int frequencyInput = new Integer(IOUtils.promptInput(
					IFileTransferConstants.FrequencyPrompt,
					IFileTransferConstants.FrequencyPrompt)).intValue();
			if (frequencyInput == 1) {
				// Frequency is ONE Time.
				// Prompt for only the Start Date or Schedule Date.
				frequency = IFileTransferConstants.ONE_TIME;

				startDt = IOUtils.promptInput(
						IFileTransferConstants.StartDtPrompt,
						IFileTransferConstants.StartDtReEnterPrompt);

			} else if (frequencyInput == 2) {
				// if Frequency is recurring
				// Prompt for 1. Repeat Transfer
				// 2. Start Date
				// 3. End Date
				frequency = IFileTransferConstants.RECURRING;
				System.out.println("Available Frequency Durations");
				System.out
						.println("WEEKLY --> 0 , MONTHLY --> 3 ,QUARTERLY-->4,HALF_YEARLY--> 5,ANNUALLY--> 6 ");

				repeatTransfer = IOUtils.promptInput(
						IFileTransferConstants.FrequencyDurationPrompt,
						IFileTransferConstants.FrequencyDurationPrompt);

				startDt = IOUtils.promptInput(
						IFileTransferConstants.StartDtPrompt,
						IFileTransferConstants.StartDtReEnterPrompt);

				endDt = IOUtils.promptInput(IFileTransferConstants.EndDtPrompt,
						IFileTransferConstants.EndDtReEnterPrompt);

			}
		}

	
		TransferRequest transferRequest = FTHelper.createTransferRequestVO(
				userContext, new Long(sourceTransferAccountId).longValue(),
				new Long(destTransferAccountId).longValue(), amount,
				new Integer(transferModeId).intValue(), frequency,
				repeatTransfer, startDt, endDt);

		/* Calculate and display the Transaction fee for this transfer */
		TransferFee transferFee = null;
		try {
			transferFee = transferManagement.getTransferFee(
					userContext, transferMode, transferDir);
		} catch (StaleConversationCredentialsExceptionFault scce) {
			System.out.println("\n Stale Conversation Credentials Exception....");
			System.out.println(scce.toString());
		} catch (InvalidCobrandConversationCredentialsExceptionFault iccce) {
			System.out.println("Invalid Cobrand Conversation Credentials Exception");
			System.out.println(iccce.toString());
		} catch (InvalidConversationCredentialsExceptionFault ic) {
			System.out.println("Invalid Conversation Credentials Exception");
			System.out.println(ic.toString());
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
		System.out
				.println("Transaction Fee for this Transfer Transaction would be :  "
						+ transferFee.getFeeAmount().getCurrencyCode()
						+ " "
						+ transferFee.getFeeAmount().getAmount());
		createTransferRequest(userContext, transferRequest);
		System.out.println("Transfer Request created sucessfully");
		// }

	}

	/**
	 * Performs the acutal Transfer Request creation. The request could be
	 * either a One Time or a Recurring Transfer. To make a one time transfer
	 * the request should be an instance of TransferRequest. For creating a
	 * recurring transfer, the transfer request should be an instance of
	 * RecurringTransferRequest. When successfully created, the request
	 * 
	 * @param userContext
	 *            The user context of the caller
	 * @param transferRequest
	 *            Object encapsulating the details of the request
	 */
	public void createTransferRequest(UserContext userContext,
			TransferRequest transferRequest) {

		try {
			transferManagement.createTransferRequest(userContext,
					transferRequest);
		} catch (InvalidUserContextExceptionFault user) {
			user.printStackTrace();
			System.out.println("The user Context is Invalid");
		} catch (InvalidConversationCredentialsExceptionFault cred) {
			cred.printStackTrace();
			System.out.println("The user's Session is Invalid !!");
		} catch (TransferAccountNotVerifiedExceptionFault ver) {
			ver.printStackTrace();
			System.out
					.println("Exception happened while fetching Transfer Request and found it not verified");
		} catch (TransferAccountNotFoundExceptionFault acct) {
			acct.printStackTrace();
			System.out
					.println("Exception happened while fetching Transfer Request");
		} catch (TransferLimitViolatedExceptionFault limit) {
			limit.printStackTrace();
			System.out
					.println("Exception happened while fetching Transfer Request and found transfer limit was violated");
		} catch (IllegalArgumentValueExceptionFault value) {
			value.printStackTrace();
			System.out
					.println("Exception happened while fetching Transfer Request and found illegal argument");
		} catch (StaleConversationCredentialsExceptionFault e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidTransferAmountExceptionFault e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExceedIRAAgeLimitExceptionFault e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UserSuspendedExceptionFault e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExceedIRAContributionDeadlineExceptionFault e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CoreExceptionFault e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExceedIRABeneficiaryAgeLimitExceptionFault e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExceedIRAContributionLimitExceptionFault e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidTransferAccountExceptionFault e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidTransferDateExceptionFault e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * Cancels the given transfer. CacnelFlag can take following two values
	 * 1.CANCEL_FLAG_CANCEL_ALL This flag indicates that
	 * transfer request is of type recurring and the user intends to cancel the
	 * entire request 2. CANCEL_FLAG_CANCEL_CURRENT This flag
	 * indicates that transfer request is of type recurring and the user intends
	 * to cancel only this request
	 * 
	 * @param userContext
	 *            The user context of the caller
	 */
	public void cancelTransfer(UserContext userContext) {
		long transferRequestId = Long.parseLong(IOUtils.promptInput(
				IFileTransferConstants.TransReqID2Prompt,
				IFileTransferConstants.TransReqID2ReEnterPrompt));
		try {
			transferManagement.cancelTransfer(userContext, transferRequestId,
					CANCEL_FLAG_CANCEL_CURRENT);
		} catch (InvalidUserContextExceptionFault iu) {
			System.out.println("Invalid User Context");
			System.out.println(iu.toString());
		} catch (InvalidConversationCredentialsExceptionFault ic) {
			System.out.println("Invalid Conversation Credentials Exception");
			System.out.println(ic.toString());
		} catch (TransferAlreadyInitiatedExceptionFault init) {
			System.out.println("Transfer Already Initiated");
			System.out.println(init.toString());
		} catch (IllegalArgumentValueExceptionFault value) {
			System.out.println("Given Transfer Request ID is invalid");
			System.out.println(value.toString());
		} catch (UserSuspendedExceptionFault use) {
			System.out.println("\n User Suspended Exception....");
			System.out.println(use.toString());
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
		System.out.println("Cancelled Transfer Sucessfully");
	}

}
