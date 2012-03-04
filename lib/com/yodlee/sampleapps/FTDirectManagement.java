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
import java.util.ArrayList;
import java.util.Date;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.List;

import com.yodlee.soap.collections.common.ArrayOfContentServiceInfo;
import com.yodlee.soap.collections.common.ArrayOfFieldInfo;
import com.yodlee.soap.collections.core.fundstransfer.transfermanagement.ArrayOfTransfer;
import com.yodlee.soap.common.ContentServiceInfo;
import com.yodlee.soap.common.Country;
import com.yodlee.sampleapps.helper.EscapedFieldInfoHelper;
import com.yodlee.soap.common.FieldInfo;
import com.yodlee.soap.common.FieldInfoMultiFixed;
import com.yodlee.soap.common.FieldInfoSingle;
import com.yodlee.soap.common.Money;
import com.yodlee.soap.common.UserContext;
import com.yodlee.soap.common.DirectTransferProfile;
import com.yodlee.soap.core.ContentServiceNotFoundExceptionFault;
import com.yodlee.soap.core.CoreExceptionFault;
import com.yodlee.soap.core.CountryNotFoundExceptionFault;
import com.yodlee.soap.core.IllegalArgumentValueExceptionFault;
import com.yodlee.soap.core.InvalidCobrandContextExceptionFault;
import com.yodlee.soap.core.InvalidCobrandConversationCredentialsExceptionFault;
import com.yodlee.soap.core.InvalidConversationCredentialsExceptionFault;
import com.yodlee.soap.core.InvalidUserContextExceptionFault;
import com.yodlee.soap.core.StaleConversationCredentialsExceptionFault;
import com.yodlee.soap.core.accountmanagement.AutoRegFieldInfoMultiFixed;
import com.yodlee.soap.core.accountmanagement.AutoRegFieldInfoSingle;
import com.yodlee.soap.core.fundstransfer.transferaccountmanagement.BankTransferAccount;
import com.yodlee.soap.core.fundstransfer.transferaccountmanagement.DFIAccount;
import com.yodlee.soap.core.fundstransfer.transferaccountmanagement.InvalidItemForTransferAccountExceptionFault;
import com.yodlee.soap.core.fundstransfer.transferaccountmanagement.TransferAccount;
import com.yodlee.soap.core.fundstransfer.transferaccountmanagement.TransferAccountNotFoundExceptionFault;
import com.yodlee.soap.core.fundstransfer.transfermanagement.InvalidTransferAccountExceptionFault;
import com.yodlee.soap.core.fundstransfer.transfermanagement.InvalidTransferAmountExceptionFault;
import com.yodlee.soap.core.fundstransfer.transfermanagement.InvalidTransferDateExceptionFault;
import com.yodlee.soap.core.fundstransfer.transfermanagement.InvalidTransferRequestExceptionFault;
import com.yodlee.soap.core.fundstransfer.transfermanagement.SingleTransfer;
import com.yodlee.soap.core.fundstransfer.transfermanagement.Transfer;
import com.yodlee.soap.core.fundstransfer.transfermanagement.TransferMode;
import com.yodlee.soap.core.fundstransfer.transfermanagement.TransferRequest;
import com.yodlee.soap.core.fundstransfer.transfermanagement.TransferRequestStatus;
import com.yodlee.soap.core.fundstransfer.transfermanagement.TransferStatus;
import com.yodlee.soap.core.fundstransfer.usermanagement.UserSuspendedExceptionFault;
import com.yodlee.soap.core.routingnumberservice.InvalidRoutingNumberExceptionFault;
import com.yodlee.soap.core.routingnumberservice.RoutingNumberNotFoundExceptionFault;
import com.yodlee.sampleapps.helper.FormUtil;
import com.yodlee.sampleapps.helper.IFileTransferConstants;
import com.yodlee.sampleapps.helper.IOUtils;
import com.yodlee.soap.appext.fundstransfer.transfermanagement.DirectTransferNotSupportedExceptionFault;
import com.yodlee.soap.appext.fundstransfer.transfermanagement.DirectTransferRefreshInfo;
import com.yodlee.soap.appext.fundstransfer.transfermanagement.DirectTransferDeletionStatus;
import com.yodlee.soap.appext.fundstransfer.transfermanagement.InvalidDestinationTransferAccountAtSourceExceptionFault;

public class FTDirectManagement extends FTDirect {
	ContentServiceInfo cs[] = null;
	private static String userTimeZone="GMT+5:30";

	public TransferAccount getTransferAccount(UserContext userContext) {
		/*
		 * Prompt user to enter bank name and routing number and account type,
		 * 
		 */

		BankTransferAccount bankTransferAccount = new BankTransferAccount();

		System.out
				.println("Enter Transfer Account ID as 0 if not already created....");
		String transferAccountID = IOUtils.promptInput(
				IFileTransferConstants.TransAcctID2Prompt,
				IFileTransferConstants.TransAcctID2ReEnterPrompt);

				
		if (transferAccountID != null && !transferAccountID.trim().equals("") && 
				!transferAccountID.equals("0"))
		{
			bankTransferAccount.setTransferAccountId(Long
					.parseLong(transferAccountID));
		}
		else
		{

		bankName = IOUtils.promptInput(IFileTransferConstants.BankNamePrompt,
				IFileTransferConstants.BankNameReEnterPrompt);

		String routingNumber = IOUtils.promptInput(
				IFileTransferConstants.RoutingNumberPrompt,
				IFileTransferConstants.RoutingNumberReEnterPrompt);

		// Verify Routing Number
		try {
			ArrayOfContentServiceInfo  arrayOfContentServiceInfo= rns.getContentServiceInfos(getCobrandContext(), routingNumber,
					Country.GB);
			if(arrayOfContentServiceInfo != null){
				cs = arrayOfContentServiceInfo.getElements();
			}

		} catch (InvalidRoutingNumberExceptionFault irne) {
			System.out.println("\n Invalid Routing Number...." + routingNumber);
			System.out.println(irne.toString());
		} catch (RoutingNumberNotFoundExceptionFault rnNotFound) {
			System.out.println("\n Routing Number...." + routingNumber
					+ " Not found");
			System.out.println(rnNotFound.toString());
		} catch (ContentServiceNotFoundExceptionFault csfne) {
			System.out.println("Content Service Not Found Exception");
			System.out.println(csfne.toString());
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
		} catch (CountryNotFoundExceptionFault cnfe) {
			System.out.println("\n Country Not Found Exception ....");
			System.out.println(cnfe.toString());
		} catch (RemoteException re) {
			System.out.println("\n Remote Exception....");
			System.out.println(re.toString());
		} 

		System.out.println("Routing number maps to: "
				+ cs[0].getSiteDisplayName() + "("
				+ cs[0].getContentServiceId() + ")");

		/* Prompt user to enter all the bank account details */

		String itemID = IOUtils.promptInput(
				IFileTransferConstants.TransferIdPrompt,
				IFileTransferConstants.TransferIdReEnterPrompt);

		String nickName = IOUtils.promptInput(
				IFileTransferConstants.nickName2Prompt,
				IFileTransferConstants.nickName2ReEnterPrompt);

		String accountNumber = IOUtils.promptInput(
				IFileTransferConstants.AccountNumberPrompt,
				IFileTransferConstants.AccountNumberReEnterPrompt);

		String acctType = IOUtils.promptInput(
				IFileTransferConstants.AccountTypePrompt,
				IFileTransferConstants.AccountTypeReEnterPrompt);

		bankTransferAccount = getBankTransferAccount(bankName, routingNumber,
				accountNumber, acctType, itemID);
		if (cs != null)
			bankTransferAccount
					.setContentServiceId(cs[0].getContentServiceId());

		bankTransferAccount.setNickname(nickName);

		String itemAccountID = IOUtils.promptInput(
				IFileTransferConstants.itemAccountID2Prompt,
				IFileTransferConstants.itemAccountID2ReEnterPrompt);

		bankTransferAccount.setItemAccountId(Long.parseLong(itemAccountID));
		bankTransferAccount.setIsVerified(1l);
		bankTransferAccount.setTransferAccountId(0l);
		bankTransferAccount.setCreateDate(Calendar.getInstance(TimeZone.getTimeZone(userTimeZone)));
		bankTransferAccount.setItemAccountId(0l);
		bankTransferAccount.setTransferAccountCategoryId(1l);
		}
		return bankTransferAccount;
	}

	public TransferAccount createDestTA() {
		// Dest Account
		BankTransferAccount ta = new BankTransferAccount();

		System.out
				.println("Enter Transfer Account ID as 0 if not already created....");
		String transferAccountID = IOUtils.promptInput(
				IFileTransferConstants.TransAcctID2Prompt,
				IFileTransferConstants.TransAcctID2ReEnterPrompt);

		if (transferAccountID != null && !transferAccountID.trim().equals("") && 
				!transferAccountID.equals("0"))
		{
			ta.setTransferAccountId(Long.parseLong(transferAccountID));
		}
		else
		{
		/* Swiss one settlement account numbers */
		String routingNumber = "50-00-00";
		String accountNumber = "20652755";

		// Verify Routing Number
		try {
			ArrayOfContentServiceInfo arrayOfContentServiceInfo = rns.getContentServiceInfos(getCobrandContext(), routingNumber,
					Country.GB);
			if(arrayOfContentServiceInfo != null){
				cs = arrayOfContentServiceInfo.getElements();
			}

		} catch (InvalidRoutingNumberExceptionFault irne) {
			System.out.println("\n Invalid Routing Number...." + routingNumber);
			System.out.println(irne.toString());
		} catch (RoutingNumberNotFoundExceptionFault rnNotFound) {
			System.out.println("\n Routing Number...." + routingNumber
					+ " Not found");
			System.out.println(rnNotFound.toString());
		} catch (ContentServiceNotFoundExceptionFault csfne) {
			System.out.println("Content Service Not Found Exception");
			System.out.println(csfne.toString());
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
		} catch (CountryNotFoundExceptionFault cnfe) {
			System.out.println("\n Country Not Found Exception ....");
			System.out.println(cnfe.toString());
		} catch (RemoteException re) {
			System.out.println("\n Remote Exception....");
			System.out.println(re.toString());
		}

		System.out.println("Routing number maps to: "
				+ cs[0].getSiteDisplayName() + "("
				+ cs[0].getContentServiceId() + ")");

		ta.setBankName("Swiss One");
		ta.setContentServiceId(cs[0].getContentServiceId());
		ta.setCreateDate(Calendar.getInstance(TimeZone.getTimeZone(userTimeZone)));
		ta.setTransferAccountType(2);
		ta.setTransferAccountCategoryId(1l);
		DFIAccount dfi = new DFIAccount();
		dfi.setAccountNumber(accountNumber);
		dfi.setDfiAccountType(2);
		dfi.setRoutingNumber(routingNumber);
		ta.setDfiAccount(dfi);
		ta.setNickname("Settlement Account");
		}
		return ta;
	}

	/**
	 * Returns a Bank Transfer Account given the following.
	 * 
	 * @param bankName
	 *            The Bank Name
	 * @param routingNumber
	 *            Routing Number
	 * @param accountNumber
	 *            Account Number
	 * @param accountType
	 *            Account Type
	 * @return BankTransferAccount
	 */
	public BankTransferAccount getBankTransferAccount(String bankName,
			String routingNumber, String accountNumber, String accountType,
			String itemID) {
		BankTransferAccount bankTransferAccount = new BankTransferAccount();
		bankTransferAccount.setNickname(bankName);

		// get DFI Account
		DFIAccount dFIAccount = new DFIAccount();
		dFIAccount.setRoutingNumber(routingNumber);
		dFIAccount.setAccountNumber(accountNumber);
		dFIAccount.setDfiAccountType(Integer.parseInt(accountType));

		bankTransferAccount.setDfiAccount(dFIAccount);
		bankTransferAccount.setBankName(bankName);
		bankTransferAccount.setTransferAccountType(Integer
				.parseInt(accountType));
		bankTransferAccount.setItemId(Long.parseLong(itemID));
		bankTransferAccount.setTransferAccountId(0l);

		return bankTransferAccount;
	}

	/**
	 * Creates a transfer requests
	 * 
	 * @param userContext
	 *            The user context of the caller
	 */
	public static TransferRequest createTransferRequest(
			TransferAccount srcTransferAccount,
			TransferAccount destTransferAccount) {

		// prompt user for Amount
		String amount = IOUtils.promptInput(
				IFileTransferConstants.AmountPrompt,
				IFileTransferConstants.AmountReEnterPrompt);

		// prompt user for Memo
		String memo = IOUtils.promptInput(IFileTransferConstants.memo2Prompt,
				IFileTransferConstants.memo2ReEnterPrompt);

		TransferRequest transferRequest = new TransferRequest();

		transferRequest.setMemo(memo);

		transferRequest.setSourceTransferAccountId(srcTransferAccount
				.getTransferAccountId());
		transferRequest.setDestinationTransferAccountId(destTransferAccount
				.getTransferAccountId());
		double dblAmount = 1.00 * Integer.parseInt(amount);
		String amtStr = String.valueOf(dblAmount);
		Money money = new Money();
		money.setAmount(new java.math.BigDecimal(amtStr));
		money.setCurrencyCode("USD");
		transferRequest.setTransferAmount(money);
	
		transferRequest.setTransferMode(TransferMode.DIRECT_TRANSFER);

		transferRequest.setTransferRequestStatus(TransferRequestStatus.ACTIVE);
		transferRequest.setScheduleDate(Calendar.getInstance(TimeZone.getTimeZone(userTimeZone)));
		return transferRequest;
	}

	public void createTransfer(UserContext userContext) {

		System.out.println("*******Enter Source Account Details*************");
		TransferAccount srcTransferAccount = getTransferAccount(userContext);

		System.out
				.println("*******Enter Destination Account Details*************");
		TransferAccount destTransferAccount = createDestTA();

		System.out.println("*******Enter Transfer Details*************");
		TransferRequest transferRequest = createTransferRequest(
				srcTransferAccount, destTransferAccount);

		// Make FT Direct call
		TransferRequest retTransferRequest = null;
		try {
			retTransferRequest = directTransferManagement
					.createTransfer(userContext, srcTransferAccount,
							destTransferAccount, transferRequest);
		} catch (UserSuspendedExceptionFault use) {
			System.out.println("\n User Suspended Exception....");
			System.out.println(use.toString());
		} catch (StaleConversationCredentialsExceptionFault scce) {
			System.out.println("\n Stale Conversation Credentials Exception....");
			System.out.println(scce.toString());
		} catch (InvalidCobrandConversationCredentialsExceptionFault iccce) {
			System.out.println("Invalid Cobrand Conversation Credentials Exception");
			System.out.println(iccce.toString());
		} catch (InvalidTransferAmountExceptionFault itae) {
			System.out.println("\n Invalid Transfer Amount Exception....");
			System.out.println(itae.toString());
		} catch (InvalidItemForTransferAccountExceptionFault iie) {
			System.out.println("\n Invalid Item For Transfer Account Exception....");
			System.out.println(iie.toString());
		} catch (CoreExceptionFault ce) {
			System.out.println("\n Core Exception....");
			System.out.println(ce.toString());
		} catch (TransferAccountNotFoundExceptionFault tanfe) {
			System.out.println("\n Transfer Account Not Found Exception....");
			System.out.println(tanfe.toString());
		} catch (InvalidUserContextExceptionFault iuce) {
			System.out.println("\n Invalid User Context Exception....");
			System.out.println(iuce.toString());
		} catch (InvalidTransferAccountExceptionFault itae) {
			System.out.println("\n Invalid Transfer Account Exception....");
			System.out.println(itae.toString());
		} catch (IllegalArgumentValueExceptionFault iave) {
			System.out.println("\n Illegal Argument Value Exception....");
			System.out.println(iave.toString());
		} catch (DirectTransferNotSupportedExceptionFault dtnse) {
			System.out.println("\n Direct Transfer Not Supported Exception....");
			System.out.println(dtnse.toString());
		} catch (InvalidTransferDateExceptionFault itde) {
			System.out.println("\n Invalid Transfer Date Exception....");
			System.out.println(itde.toString());
		} catch (RemoteException re) {
			System.out.println("\n Remote Exception....");
			System.out.println(re.toString());
		}

		if (retTransferRequest != null) {
			System.out.println("Destination Transfer Account ID: "
					+ retTransferRequest.getDestinationTransferAccountId());
			System.out.println("Source Transfer Account ID: "
					+ retTransferRequest.getSourceTransferAccountId());
			System.out.println("Memo:  " + retTransferRequest.getMemo());
			System.out.println("Transfer Request ID: "
					+ retTransferRequest.getTransferRequestId());
			System.out.println("Scheduled Date: "
					+ retTransferRequest.getScheduleDate());
			System.out.println("Amount: "
					+ retTransferRequest.getTransferAmount().getAmount());
		} else
			System.out.println("Transfer Request returned is null");

	}

	public void makeTransfer(UserContext userContext) {

		// prompt user for transfer request ID
		String transferReqID = IOUtils.promptInput(
				IFileTransferConstants.TransReqID2Prompt,
				IFileTransferConstants.TransReqID2ReEnterPrompt);

		System.out.println("Source Info****************");
		String routingNumber = IOUtils.promptInput(
				IFileTransferConstants.RoutingNumberPrompt,
				IFileTransferConstants.RoutingNumberReEnterPrompt);

		// Verify Routing Number
		try {
			ArrayOfContentServiceInfo arrayOfContentServiceInfo = rns.getContentServiceInfos(getCobrandContext(), routingNumber,
					Country.GB);
			if(arrayOfContentServiceInfo != null){
				arrayOfContentServiceInfo.getElements();
			}
			if (null != cs){
				long sumInfoId = cs[0].getContentServiceId();
				
			}

		} catch (InvalidRoutingNumberExceptionFault irne) {
			System.out.println("\n Invalid Routing Number...." + routingNumber);
			System.out.println(irne.toString());
		} catch (RoutingNumberNotFoundExceptionFault rnNotFound) {
			System.out.println("\n Routing Number...." + routingNumber
					+ " Not found");
			System.out.println(rnNotFound.toString());
		} catch (ContentServiceNotFoundExceptionFault csfne) {
			System.out.println("Content Service Not Found Exception");
			System.out.println(csfne.toString());
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
		} catch (CountryNotFoundExceptionFault cnfe) {
			System.out.println("\n Country Not Found Exception ....");
			System.out.println(cnfe.toString());
		} catch (RemoteException re) {
			System.out.println("\n Remote Exception....");
			System.out.println(re.toString());
		}

		System.out.println("Routing Number matches to.."
				+ cs[0].getContentServiceId() + ":"
				+ cs[0].getContentServiceDisplayName());

		DirectTransferProfile directTransferProfile = cs[0]
				.getDirectTransferProfile();

		
		List fieldInfoList = null;
		try {
			fieldInfoList = FormUtil.getUserInputFieldInfoList(userContext,
					IAV.getLoginFormForContentService(userContext, cs[0]
							.getContentServiceId()));
		} catch (ContentServiceNotFoundExceptionFault csfne) {
			System.out.println("Content Service Not Found Exception");
			System.out.println(csfne.toString());
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
		
		List addDestAccountFieldInfoList = new ArrayList();
		List makeTransferFieldInfoList = new ArrayList();
		
		if (directTransferProfile != null) {
			if(directTransferProfile.getAddDestinationAccountForm() != null)
				addDestAccountFieldInfoList = FormUtil.getUserInputFieldInfoList(userContext,
					directTransferProfile.getAddDestinationAccountForm());
			if(directTransferProfile.getMakeTransferForm() != null)
				makeTransferFieldInfoList = FormUtil.getUserInputFieldInfoList(userContext,
					directTransferProfile.getMakeTransferForm());
		} else
			System.out
			.println("No extra Field Info required for transfer on this site.");

		if(!addDestAccountFieldInfoList.isEmpty())
			fieldInfoList.addAll(addDestAccountFieldInfoList);
		if(!makeTransferFieldInfoList.isEmpty())
			fieldInfoList.addAll(makeTransferFieldInfoList);
		ArrayOfFieldInfo arrayOfFieldInfo = new ArrayOfFieldInfo();
		arrayOfFieldInfo.setElements(IAV
				.convertListToArray(fieldInfoList));
		// Make makeTransfer call
		try {
			directTransferManagement.makeTransfer(userContext, Long
					.parseLong(transferReqID),arrayOfFieldInfo);
		} catch (UserSuspendedExceptionFault use) {
			System.out.println("\n User Suspend Exception....");
			System.out.println(use.toString());
		} catch (StaleConversationCredentialsExceptionFault scce) {
			System.out.println("\n Stale Conversation Credentials Exception....");
			System.out.println(scce.toString());
		} catch (InvalidConversationCredentialsExceptionFault ic) {
			System.out.println("Invalid Conversation Credentials Exception");
			System.out.println(ic.toString());
		} catch (InvalidTransferRequestExceptionFault itre) {
			System.out.println("\n Invalid Transfer Request Exception....");
			System.out.println(itre.toString());
		} catch (CoreExceptionFault ce) {
			System.out.println("\n Core Exception....");
			System.out.println(ce.toString());
		} catch (TransferAccountNotFoundExceptionFault tnfe) {
			System.out.println("\n Transfer Account Not Found Exception....");
			System.out.println(tnfe.toString());
		} catch (InvalidUserContextExceptionFault iuce) {
			System.out.println("\n Invalid User Context Exception....");
			System.out.println(iuce.toString());
		} catch (InvalidTransferAccountExceptionFault itae) {
			System.out.println("\n Invalid Transfer Account Exception....");
			System.out.println(itae.toString());
		} catch (IllegalArgumentValueExceptionFault value) {
			System.out.println("Given Transfer Account ID is invalid");
			System.out.println(value.toString());
		} catch (NumberFormatException nfe) {
			System.out.println("\n Number Format Exception....");
			System.out.println(nfe.toString());
		} catch (RemoteException re) {
			System.out.println("\n Remote Exception....");
			System.out.println(re.toString());
		}

		// poll the status of transfer made.

		int status = poll(userContext, Long.parseLong(transferReqID));

		System.out.println("Poll finished with status " + status);
	}

	

	public int poll(UserContext userContext, long transferRequestId) {

		long startTime = (new Date()).getTime();
		long currTime = startTime;

		System.out.println("\tChecking the transfer status ... ");
		while (startTime - currTime < IAV.REFRESH_TIMEOUT_MIILIS) {
			
			Transfer[] transfers = null;
			try {
				ArrayOfTransfer arrayOfTransfer = transferManagement.getTransfers(userContext,
						transferRequestId);
				if(arrayOfTransfer != null){
					transfers = arrayOfTransfer.getElements();
				}
			} catch (StaleConversationCredentialsExceptionFault scce) {
				System.out.println("\n Stale Conversation Credentials Exception....");
				System.out.println(scce.toString());
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
			TransferStatus status = null;
			SingleTransfer singleTransfer = null;

			for (int i = 0; i < transfers.length; i++) {
				if (transfers[i] instanceof SingleTransfer) {
					// In case of swiisone all are singleTransfer
									
					singleTransfer = (SingleTransfer) transfers[i];
					status = singleTransfer.getTransferStatus();
					
				}
			}

			if (status == TransferStatus.INPROGRESS) {
				System.out.println("\tIn Progress ... ");
			}
			if (status == TransferStatus.SCHEDULED) {
				System.out.println("\tScheduled ... ");

			} else if (status == TransferStatus.FAILED) {
				System.out.println("\tTransfer failed...");
				System.out.println("Status Code "+singleTransfer.getCode());
				System.out.println("Code Description "+singleTransfer.getCodeDescription());
				System.out.println("Confirmation Message "+singleTransfer.getConfirmationMessage());
				return -2;
			} else if (status == TransferStatus.SUCCESS) {
				System.out.println("\t Transfer completed successfully.");
				return 3;
			}
			try {
				Thread.sleep(IAV.SLEEP_MILLIS);
			} catch (Exception e) {
				return -1;
			}
		}
		System.out.println("\tThe transfer has timed out.");
		return -1;
	}
	
	public void deleteDestinationAccount(UserContext userContext) {
		
		long destTransferAccountId = 0, srcTransferAccountId = 0;
		
		System.out.print("Source ");
		String transferAccountID = IOUtils.promptInput(
				IFileTransferConstants.TransAcctID2Prompt,
				IFileTransferConstants.TransAcctID2ReEnterPrompt);
		if (transferAccountID != null && !transferAccountID.trim().equals(""))
			srcTransferAccountId = Long.parseLong(transferAccountID);
		
		System.out.print("Destination ");
		transferAccountID = IOUtils.promptInput(
				IFileTransferConstants.TransAcctID2Prompt,
				IFileTransferConstants.TransAcctID2ReEnterPrompt);
		if (transferAccountID != null && !transferAccountID.trim().equals(""))
			destTransferAccountId = Long.parseLong(transferAccountID);
		
		System.out.println("Source Info****************");
		String routingNumber = IOUtils.promptInput(
				IFileTransferConstants.RoutingNumberPrompt,
				IFileTransferConstants.RoutingNumberReEnterPrompt);

		// Verify Routing Number
		try {
			ArrayOfContentServiceInfo arrayOfContentServiceInfo = rns.getContentServiceInfos(getCobrandContext(), routingNumber,
					Country.GB);
			if(arrayOfContentServiceInfo != null){
				cs = arrayOfContentServiceInfo.getElements();
			}

		} catch (InvalidRoutingNumberExceptionFault irne) {
			System.out.println("\n Invalid Routing Number...." + routingNumber);
			System.out.println(irne.toString());
		} catch (RoutingNumberNotFoundExceptionFault rnNotFound) {
			System.out.println("\n Routing Number...." + routingNumber
					+ " Not found");
			System.out.println(rnNotFound.toString());
		} catch (ContentServiceNotFoundExceptionFault csfne) {
			System.out.println("Content Service Not Found Exception");
			System.out.println(csfne.toString());
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
		} catch (CountryNotFoundExceptionFault cnfe) {
			System.out.println("\n Country Not Found Exception....");
			System.out.println(cnfe.toString());
		} catch (RemoteException re) {
			System.out.println("\n Remote Exception....");
			System.out.println(re.toString());
		}

		System.out.println("Routing Number matches to.."
				+ cs[0].getContentServiceId() + ":"
				+ cs[0].getContentServiceDisplayName());

		DirectTransferProfile directTransferProfile = cs[0]
				.getDirectTransferProfile();
		
		List fieldInfoList = null;
		try {
			fieldInfoList = FormUtil.getUserInputFieldInfoList(userContext,
					IAV.getLoginFormForContentService(userContext, cs[0]
							.getContentServiceId()));
		} catch (ContentServiceNotFoundExceptionFault csfne) {
			System.out.println("Content Service Not Found Exception");
			System.out.println(csfne.toString());
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
		
		List delDestAccountFieldInfoList = new ArrayList();
		
		if (directTransferProfile != null) {
			if(directTransferProfile.getDeleteDestinationAccountForm() != null)
				delDestAccountFieldInfoList = FormUtil.getUserInputFieldInfoList(userContext,
					directTransferProfile.getDeleteDestinationAccountForm());
		} else
			System.out
			.println("No extra Field Info required for deleting Source Account on this site.");

		if(!delDestAccountFieldInfoList.isEmpty())
			fieldInfoList.addAll(delDestAccountFieldInfoList);
		ArrayOfFieldInfo arrayOfFieldInfo = new ArrayOfFieldInfo();
		arrayOfFieldInfo.setElements(IAV
				.convertListToArray(fieldInfoList));		
		try {
			directTransferManagement.removeDestinationTransferAccountAtSource(userContext,destTransferAccountId,srcTransferAccountId,arrayOfFieldInfo);
		} catch (UserSuspendedExceptionFault use) {
			System.out.println("\n User Suspended Exception....");
			System.out.println(use.toString());
		} catch (StaleConversationCredentialsExceptionFault scce) {
			System.out.println("\n Stale Conversation Credentials Exception....");
			System.out.println(scce.toString());
		} catch (InvalidConversationCredentialsExceptionFault ic) {
			System.out.println("Invalid Conversation Credentials Exception");
			System.out.println(ic.toString());
		} catch (InvalidDestinationTransferAccountAtSourceExceptionFault e1) {
			System.out.println("Invalid Destination Transfer Account At Source Exception");
			System.out.println(e1.toString());
		} catch (CoreExceptionFault ce) {
			System.out.println("\n Core Exception....");
			System.out.println(ce.toString());
		} catch (TransferAccountNotFoundExceptionFault tnfe) {
			System.out.println("Transfer Account Not Found Exception");
			System.out.println(tnfe.toString());
		} catch (InvalidUserContextExceptionFault iuce) {
			System.out.println("Invalid User Context Exception");
			System.out.println(iuce.toString());
		} catch (InvalidTransferAccountExceptionFault itae) {
			System.out.println("Invalid Transfer Account Exception");
			System.out.println(itae.toString());
		} catch (IllegalArgumentValueExceptionFault value) {
			System.out.println("Given Transfer Account ID is invalid");
			System.out.println(value.toString());
		} catch (RemoteException re) {
			System.out.println("\n Remote Exception....");
			System.out.println(re.toString());
		}
		
		int status = pollDelete(userContext,srcTransferAccountId,destTransferAccountId);
		System.out.println("Status : " + status);
	}
	
	public int pollDelete(UserContext userContext,  
            long sourceTransferAccountId, 
            long destinationTransferAccountId)
	{
		
		long startTime = (new Date()).getTime();
		long currTime = startTime;

		System.out.println("\tChecking the deletion status status ... ");
		
		int statusCode = -1;
		while (startTime - currTime < IAV.REFRESH_TIMEOUT_MIILIS && statusCode !=0) {
			System.out.println("Inside While loop!");
			DirectTransferRefreshInfo directTransferRefreshInfo = null;
			try {
				directTransferRefreshInfo = directTransferManagement.getDestinationTransferAccountRemovalStatus(userContext,sourceTransferAccountId,destinationTransferAccountId);
			} catch (UserSuspendedExceptionFault use) {
				System.out.println("\n User Suspended Exception....");
				System.out.println(use.toString());
			} catch (StaleConversationCredentialsExceptionFault scce) {
				System.out.println("\n Stale Conversation Credentials Exception....");
				System.out.println(scce.toString());
			} catch (InvalidConversationCredentialsExceptionFault ic) {
				System.out.println("Invalid Conversation Credentials Exception");
				System.out.println(ic.toString());
			} catch (InvalidDestinationTransferAccountAtSourceExceptionFault e1) {
				System.out.println("Invalid Destination Transfer Account At Source Exception");
				System.out.println(e1.toString());
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
			
			//statusCode = directTransferRefreshInfo.getStatusCode();

			
			if (directTransferRefreshInfo.getDirectTransferDeletionStatus() == DirectTransferDeletionStatus.INPROGRESS) {
				System.out.println("\tIn Progress ... ");
			}
			else if (directTransferRefreshInfo.getDirectTransferDeletionStatus() == DirectTransferDeletionStatus.FAILED) {
				System.out.println("\tTransfer failed...");
				System.out.println("Status Code "+directTransferRefreshInfo.getStatusCode().toString());
				System.out.println("Code Description "+directTransferRefreshInfo.getStatusCodeDescription());
				System.out.println("Confirmation Message "+directTransferRefreshInfo.getGatheredMessage());
				return -2;
			} else if (directTransferRefreshInfo.getDirectTransferDeletionStatus() == DirectTransferDeletionStatus.SUCCESS) {
				System.out.println("\t Transfer completed successfully.");
				System.out.println(directTransferRefreshInfo.getStatusCode().toString());
				return 0;
			}
			else{
				System.out.println("Neither IN_PROGRESS, nor SUCCESS, noe FAILED");
				if(directTransferRefreshInfo.getDirectTransferDeletionStatus()==null)
					System.out.println("Its NULL!!!");
				System.out.println("Status : "+ directTransferRefreshInfo.getStatusCode().toString());
			}
			try {
				Thread.sleep(IAV.SLEEP_MILLIS);
			} catch (Exception e) {
				return -1;
			}
		}
		System.out.println("\tThe transfer has timed out.");
		return -1;
	}

}
