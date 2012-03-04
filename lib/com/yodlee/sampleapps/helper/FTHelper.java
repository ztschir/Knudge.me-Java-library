/*
 * Copyright 2009 Yodlee, Inc.  All Rights Reserved.  Your use of this code 
 * requires a license from Yodlee.  Any such license to this code is 
 * restricted to evaluation/illustrative purposes only. It is not intended 
 * for use in a production environment, and Yodlee disclaims all warranties 
 * and/or support obligations concerning this code, regardless of the terms 
 * of any other agreements between Yodlee and you."
 */
package com.yodlee.sampleapps.helper;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.Date;

import com.yodlee.soap.common.Money;
import com.yodlee.soap.common.UserContext;
import com.yodlee.soap.core.fundstransfer.transfermanagement.Frequency;
import com.yodlee.soap.core.fundstransfer.transfermanagement.RecurringTransfer;
import com.yodlee.soap.core.fundstransfer.transfermanagement.RecurringTransferRequest;
import com.yodlee.soap.core.fundstransfer.transfermanagement.TransferMode;
import com.yodlee.soap.core.fundstransfer.transfermanagement.TransferRequest;
import com.yodlee.soap.core.fundstransfer.transfermanagement.TransferSchedulingType;
import com.yodlee.soap.core.transactionsearch.TransactionCategory;

/**
 * Helper class for FT related functions.
 */
public class FTHelper {

	private static String userTimeZone="GMT+5:30";
	
	/**
	 * Retrieves the Brokerage Account Number specific for Fidelity CS.
	 * 
	 * @param userProvAccountNo
	 * @return Fidelity Brokerage account number.
	 */
	public static String getFidelityBrokAccountNumber(String userProvAccountNo) {

		if (userProvAccountNo == null || userProvAccountNo.equals(""))
			return userProvAccountNo;
		String beginsWith = userProvAccountNo.substring(0, 1);
		String tempAccountNumber = userProvAccountNo.substring(1);
		if (beginsWith.equalsIgnoreCase("X")
				|| beginsWith.equalsIgnoreCase("Y")
				|| beginsWith.equalsIgnoreCase("Z")) {
			userProvAccountNo = "0" + tempAccountNumber;
		} else {
			userProvAccountNo = "1" + tempAccountNumber;
		}
		return userProvAccountNo;
	}

	/**
	 * Creates the TransferRequest Value Object.
	 * 
	 * @param userContext
	 *            User Context
	 * @param fromAccountId
	 *            Source Account Id
	 * @param toAccountId
	 *            Destination Account Id
	 * @param transferAmount
	 *            Transfer Amount
	 * @param transferModeId
	 *            Delivery Method (Next Day or Standard Service)
	 * @param transferFrequencyId
	 *            One time or Recurring.
	 * @param repeatTransfer
	 *            Frequency Duration (Weekly/Monthly/Quaterly/Annually)
	 * @param startDtStr
	 *            Start Date for the transfer
	 * @param endDtStr
	 *            End Date for the transfer
	 * @return TransferRequest Value Object.
	 */
	public static TransferRequest createTransferRequestVO(
			UserContext userContext, long fromAccountId, long toAccountId,
			String transferAmount, int transferModeId, int transferFrequencyId,
			String repeatTransfer, String startDtStr, String endDtStr) {
		TransferRequest transferRequest = null;
		RecurringTransferRequest recurTransferRequest = null;
		Date startDate = null, endDate = null;
		Calendar startCalendar = null;
		Calendar endCalendar = null;

		try {
			startDate = ValidationUtil.getDate(startDtStr);
			startCalendar = Calendar.getInstance(TimeZone.getTimeZone(userTimeZone));
			endCalendar = Calendar.getInstance(TimeZone.getTimeZone(userTimeZone));
			startCalendar.setTime(startDate);
			if(transferFrequencyId == IFileTransferConstants.RECURRING){
			endDate = ValidationUtil.getDate(endDtStr);
			endCalendar.setTime(endDate);
			}
		} catch (ParseException parseException) {
			parseException.printStackTrace();
		}

		String currencyDesc = IFileTransferConstants.DEFAULT_CURRENCY;

		Money transferMoney = new Money();
		transferMoney.setAmount(new BigDecimal(transferAmount));
		transferMoney.setCurrencyCode(currencyDesc);
		
		TransferMode transferMode = getTransferMode(transferModeId);

		TransactionCategory transactionCategory =  new TransactionCategory();
		transactionCategory.setCategoryId(new Long(28));
		transactionCategory.setCategoryName("transfers");
		
		if (transferFrequencyId == IFileTransferConstants.ONE_TIME) {
			transferRequest = new TransferRequest();
			transferRequest.setSourceTransferAccountId(fromAccountId);
			transferRequest.setDestinationTransferAccountId(toAccountId);
			transferRequest.setTransferAmount(transferMoney);
			transferRequest.setScheduleDate(startCalendar);
			transferRequest.setTransferMode(transferMode);
			transferRequest.setDestinationTransferAccountId(toAccountId);
			transferRequest.setSchedulingType(TransferSchedulingType.SEND_DATE_SCHEDULING);
			transferRequest.setTransactionCategory(transactionCategory);
			return transferRequest;
			
		} else if (transferFrequencyId == IFileTransferConstants.RECURRING) {
			Frequency transferFrequency = getTransferRepeatFrequency(new Integer(
					repeatTransfer).intValue());
			
			recurTransferRequest = new RecurringTransferRequest();
			recurTransferRequest.setDestinationTransferAccountId(toAccountId);
			recurTransferRequest.setSourceTransferAccountId(fromAccountId);
			recurTransferRequest.setTransferAmount(transferMoney);
			recurTransferRequest.setTransferMode(transferMode);
			recurTransferRequest.setFrequency(transferFrequency);
			recurTransferRequest.setEndDate(endCalendar);
			recurTransferRequest.setStartDate(startCalendar);
			recurTransferRequest.setDestinationTransferAccountId(toAccountId);
			recurTransferRequest.setSchedulingType(TransferSchedulingType.SEND_DATE_SCHEDULING);
			recurTransferRequest.setTransactionCategory(transactionCategory);
			return recurTransferRequest;		
		}
		return transferRequest;
	}


	/**
	 * Returns the TransferMode Object for the given TransferId.
	 * 
	 * @param transferModeId
	 *            User Input Transfer Mode Id
	 * @return Transfer Mode Object.
	 */
	private static TransferMode getTransferMode(int transferModeId) {
		TransferMode transferMode = null;
		switch (transferModeId) {
		case IFileTransferConstants.NEXT_DAY:
			transferMode = TransferMode.NEXT_DAY;
			break;
		case IFileTransferConstants.STANDARD_SRV:
			transferMode = TransferMode.STANDARD;
			break;
		}
		return transferMode;
	}

	/**
	 * Gets the Frequency Object for the given frequencyId.
	 * 
	 * @param frequencyId
	 *            User Input Frequency Id
	 * @return Frequency object
	 */
	private static Frequency getTransferRepeatFrequency(int frequencyId) {
		Frequency frequencyValue = null;
		switch (frequencyId) {
		case IFileTransferConstants.WEEKLY:
			frequencyValue = Frequency.WEEKLY;
			break;
		case IFileTransferConstants.MONTHLY:
			frequencyValue = Frequency.MONTHLY;
			break;
		case IFileTransferConstants.QUARTERLY:
			frequencyValue = Frequency.QUATERLY;
			break;
		case IFileTransferConstants.ANNUALLY:
			frequencyValue = Frequency.ANNUALLY;
			break;
		case IFileTransferConstants.HALF_YEARLY:
			frequencyValue = Frequency.HALF_YEARLY;
			break;
		case IFileTransferConstants.ONE_TIME:
			frequencyValue = Frequency.ONE_TIME;
			break;
		}
		return frequencyValue;
	}
}
