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

import com.yodlee.soap.collections.core.fundstransfer.transfermanagement.ArrayOfTransfer;
import com.yodlee.soap.collections.core.fundstransfer.transfermanagement.ArrayOfTransferRequest;
import com.yodlee.soap.common.UserContext;
import com.yodlee.soap.core.CoreExceptionFault;
import com.yodlee.soap.core.IllegalArgumentValueExceptionFault;
import com.yodlee.soap.core.InvalidConversationCredentialsExceptionFault;
import com.yodlee.soap.core.InvalidUserContextExceptionFault;
import com.yodlee.soap.core.StaleConversationCredentialsExceptionFault;
import com.yodlee.soap.core.fundstransfer.transfermanagement.Transfer;
import com.yodlee.soap.core.fundstransfer.transfermanagement.TransferMode;
import com.yodlee.soap.core.fundstransfer.transfermanagement.TransferRequest;
import com.yodlee.soap.core.fundstransfer.transfermanagement.TransferRequestStatus;

/**
 * Handles Transfer Histroty APIs.
 */
public class FTTransferHistory extends FundsTransfer {

	/**
	 * Displays the details of the given transfer Request. It displys detials
	 * like 1.Transfer request ID 2.Transfer schedule date 3.Transfer source and
	 * Destination account ID 4.Transfer Amount, 5.Tranasfer Mode (Standard/Next
	 * Day) 6.Transfer Status (Active, Inactive, Dormant)
	 * 
	 * @param transfeRequest
	 *            TrasferRequest Object for which details are to be displayed
	 */
	public void displayTranferRequestDetails(TransferRequest transfeRequest) {
		System.out.println("*********************************************");
		System.out.println("Transfer Request ID 	:"
				+ transfeRequest.getTransferRequestId());
		System.out.println("Transfer Schedule Date  :"
				+ transfeRequest.getScheduleDate());
		System.out.println("Source Account ID  :"
				+ transfeRequest.getSourceTransferAccountId());
		System.out.println("Destination Account ID  :"
				+ transfeRequest.getDestinationTransferAccountId());
		System.out.println("Amount  :"
				+ transfeRequest.getTransferAmount().getCurrencyCode()
				+ transfeRequest.getTransferAmount().getAmount());

		if (transfeRequest.getTransferMode().getValue() == TransferMode._STANDARD)
			System.out.println("Mode  : Standard(3 Business Days)");
		else
			System.out.println("Mode  : Next Day");

		if (transfeRequest.getTransferRequestStatus() != null) {
			if (transfeRequest.getTransferRequestStatus().getValue() == TransferRequestStatus._ACTIVE)
				System.out.println("Status  : Active");
			else if (transfeRequest.getTransferRequestStatus().getValue() == TransferRequestStatus._DORMANT)
				System.out.println("Status  : Dormant");
			else if (transfeRequest.getTransferRequestStatus().getValue() == TransferRequestStatus._IN_ACTIVE)
				System.out.println("Status  : InActive");
		}
		System.out.println("*********************************************");
		System.out.println("\n");
	}

	/**
	 * Displays all the transfer requests details for the given user.
	 * 
	 * @param userContext
	 *            The user context of the caller
	 */
	public void displayTransferRequestsHistoryforUser(UserContext userContext) {
		TransferRequest[] transferHistory = null;
		try {
			ArrayOfTransferRequest arrayOfTransferRequest = transferManagement
					.getAllTransferRequest(userContext);
			if(arrayOfTransferRequest != null){
				transferHistory = arrayOfTransferRequest.getElements();
			}
		} catch (InvalidUserContextExceptionFault iu) {
			System.out.println("Invalid User Context");
			System.out.println(iu.toString());
		} catch (InvalidConversationCredentialsExceptionFault ic) {
			System.out.println("Invalid Conversation Credentials Exception");
			System.out.println(ic.toString());
		} catch (StaleConversationCredentialsExceptionFault scce) {
			System.out.println("\n Stale Conversation Credentials Exception....");
			System.out.println(scce.toString());
		}   catch (CoreExceptionFault ce) {
			System.out.println("\n Core Exception....");
			System.out.println(ce.toString());
		} catch (IllegalArgumentValueExceptionFault value) {
			System.out.println("Given Transfer Account ID is invalid");
			System.out.println(value.toString());
		} catch (RemoteException re) {
			System.out.println("\n Remote Exception....");
			System.out.println(re.toString());
		}
		
		if(transferHistory != null){
			for (int i = 0; i < transferHistory.length; i++) {
				System.out.println("Transfer " + (i + 1));
				displayTranferRequestDetails(transferHistory[i]);
	
			}
		}
	}

	/**
	 * Gives all the transfers for the user. Here parameter passed as true for
	 * method call getAllTransfers indicates that show all the transfers
	 * including the deleted accounts.
	 * 
	 * @param userContext
	 *            The user context of the caller
	 */
	public void getAllTransfersForUserIncludingDeletedAcct(
			UserContext userContext) {
		System.out.println("***************All transfers***********");
		Transfer[] allTransfers = null;
		try {
			ArrayOfTransfer arrayOfTransfer = transferManagement
					.getAllTransfers1(userContext, true);
			if(arrayOfTransfer != null){
				allTransfers = arrayOfTransfer.getElements();
			}
		} catch (InvalidUserContextExceptionFault iu) {
			System.out.println("Invalid User Context");
			System.out.println(iu.toString());
		} catch (InvalidConversationCredentialsExceptionFault ic) {
			System.out.println("Invalid Cobrand Context Exception");
			System.out.println(ic.toString());
		} catch (StaleConversationCredentialsExceptionFault scce) {
			System.out.println("\n Stale Conversation Credentials Exception....");
			System.out.println(scce.toString());
		}   catch (CoreExceptionFault ce) {
			System.out.println("\n Core Exception....");
			System.out.println(ce.toString());
		} catch (IllegalArgumentValueExceptionFault value) {
			System.out.println("Given Transfer Account ID is invalid");
			System.out.println(value.toString());
		} catch (RemoteException re) {
			System.out.println("\n Remote Exception....");
			System.out.println(re.toString());
		}
		if(allTransfers != null){
			for (int i = 0; i < allTransfers.length; i++) {
				TransferRequest transReq = null;
				try {
					transReq = transferManagement.getTransferRequest(
							userContext, allTransfers[i].getTransferRequestId());
				} catch (StaleConversationCredentialsExceptionFault scce) {
					System.out.println("\n Stale Conversation Credentials Exception....");
					System.out.println(scce.toString());
				} catch (InvalidConversationCredentialsExceptionFault icce) {
					System.out.println("\n Invalid Conversation Credentials Exception....");
					System.out.println(icce.toString());
				} catch (CoreExceptionFault ce) {
					System.out.println("\n Core Exception....");
					System.out.println(ce.toString());
				} catch (InvalidUserContextExceptionFault iuce) {
					System.out.println("\n Invalid User Context Exception....");
					System.out.println(iuce.toString());
				}  catch (IllegalArgumentValueExceptionFault value) {
					System.out.println("Given Transfer Account ID is invalid");
					System.out.println(value.toString());
				}  catch (RemoteException re) {
					System.out.println("\n Remote Exception....");
					System.out.println(re.toString());
				}
				
				displayTranferRequestDetails(transReq);
			}
		}
	}

	/**
	 * Gives all the pending transfers for the user.
	 * 
	 * @param userContext
	 *            The user context of the caller
	 */
	public void getAllPendingTransfersForUser(UserContext userContext) {
		Transfer[] pendingTransfers = null;
		System.out.println("***************All Pending transfers***********");
		try {
			ArrayOfTransfer arrayOfTransfer = transferManagement
					.getPendingTransfers(userContext);
			if(arrayOfTransfer != null){
				pendingTransfers = arrayOfTransfer.getElements();
			}
		} catch (InvalidUserContextExceptionFault iu) {
			System.out.println("Invalid User Context");
			System.out.println(iu.toString());
		} catch (InvalidConversationCredentialsExceptionFault ic) {
			System.out.println("Invalid Cobrand Context Exception");
			System.out.println(ic.toString());
		} catch (StaleConversationCredentialsExceptionFault scce) {
			System.out.println("\n Stale Conversation Credentials Exception....");
			System.out.println(scce.toString());
		}   catch (CoreExceptionFault ce) {
			System.out.println("\n Core Exception....");
			System.out.println(ce.toString());
		} catch (IllegalArgumentValueExceptionFault value) {
			System.out.println("Given Transfer Account ID is invalid");
			System.out.println(value.toString());
		} catch (RemoteException re) {
			System.out.println("\n Remote Exception....");
			System.out.println(re.toString());
		}
		if(pendingTransfers != null){
			for (int i = 0; i < pendingTransfers.length; i++) {
				TransferRequest transReq = null;
				try {
					transReq = transferManagement.getTransferRequest(
							userContext, pendingTransfers[i].getTransferRequestId());
				} catch (StaleConversationCredentialsExceptionFault scce) {
					System.out.println("\n Stale Conversation Credentials Exception....");
					System.out.println(scce.toString());
				} catch (InvalidConversationCredentialsExceptionFault icce) {
					System.out.println("\n Invalid Conversation Credentials Exception....");
					System.out.println(icce.toString());
				} catch (CoreExceptionFault ce) {
					System.out.println("\n Core Exception....");
					System.out.println(ce.toString());
				} catch (InvalidUserContextExceptionFault iuce) {
					System.out.println("\n Invalid User Context Exception....");
					System.out.println(iuce.toString());
				}  catch (IllegalArgumentValueExceptionFault value) {
					System.out.println("Given Transfer Account ID is invalid");
					System.out.println(value.toString());
				}  catch (RemoteException re) {
					System.out.println("\n Remote Exception....");
					System.out.println(re.toString());
				}
				displayTranferRequestDetails(transReq);
			}
		} 
		else {
			System.out.println("There is no pending transfers for user");
		}

	}

	/**
	 * Gives all the processed transfers for the user.
	 * 
	 * @param userContext
	 *            The user context of the caller
	 */
	public void getAllProcessedTransfersForUser(UserContext userContext) {
		Transfer[] processedTransfers = null;
		System.out.println("***************All Processed transfers***********");
		try {
			ArrayOfTransfer arrayOfTransfer = transferManagement
					.getProcessedTransfers(userContext);
			if(arrayOfTransfer != null){
				processedTransfers = arrayOfTransfer.getElements();
			}
		} catch (InvalidUserContextExceptionFault iu) {
			System.out.println("Invalid User Context");
			System.out.println(iu.toString());
		} catch (InvalidConversationCredentialsExceptionFault ic) {
			System.out.println("Invalid Cobrand Context Exception");
			System.out.println(ic.toString());
		} catch (StaleConversationCredentialsExceptionFault scce) {
			System.out.println("\n Stale Conversation Credentials Exception....");
			System.out.println(scce.toString());
		}   catch (CoreExceptionFault ce) {
			System.out.println("\n Core Exception....");
			System.out.println(ce.toString());
		} catch (IllegalArgumentValueExceptionFault value) {
			System.out.println("Given Transfer Account ID is invalid");
			System.out.println(value.toString());
		} catch (RemoteException re) {
			System.out.println("\n Remote Exception....");
			System.out.println(re.toString());
		}
		if(processedTransfers != null){
			for (int i = 0; i < processedTransfers.length; i++) {
				TransferRequest transReq = null;
				try {
					transReq = transferManagement.getTransferRequest(
							userContext, processedTransfers[i].getTransferRequestId());
				} catch (StaleConversationCredentialsExceptionFault scce) {
					System.out.println("\n Stale Conversation Credentials Exception....");
					System.out.println(scce.toString());
				} catch (InvalidConversationCredentialsExceptionFault icce) {
					System.out.println("\n Invalid Conversation Credentials Exception....");
					System.out.println(icce.toString());
				} catch (CoreExceptionFault ce) {
					System.out.println("\n Core Exception....");
					System.out.println(ce.toString());
				} catch (InvalidUserContextExceptionFault iuce) {
					System.out.println("\n Invalid User Context Exception....");
					System.out.println(iuce.toString());
				}  catch (IllegalArgumentValueExceptionFault value) {
					System.out.println("Given Transfer Account ID is invalid");
					System.out.println(value.toString());
				}  catch (RemoteException re) {
					System.out.println("\n Remote Exception....");
					System.out.println(re.toString());
				}
				displayTranferRequestDetails(transReq);
			}
		}
		
		else {
			System.out.println("There is no processed transfer for user");
		}
	}

}
