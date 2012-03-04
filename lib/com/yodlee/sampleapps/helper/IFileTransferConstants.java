/*
 * Copyright 2009 Yodlee, Inc.  All Rights Reserved.  Your use of this code 
 * requires a license from Yodlee.  Any such license to this code is 
 * restricted to evaluation/illustrative purposes only. It is not intended 
 * for use in a production environment, and Yodlee disclaims all warranties 
 * and/or support obligations concerning this code, regardless of the terms 
 * of any other agreements between Yodlee and you."
 */
package com.yodlee.sampleapps.helper;

/**
 * Holds all the constants related to FT.
 * 
 */
public interface IFileTransferConstants {

	public final String EXIT_VALUE = "-1";

	public final String EXIT_STRING = " or " + EXIT_VALUE + " to Quit : ";

	public final String EnterPrompt = "Enter ";

	public final String InvalidEnterPrompt = "Invalid Entry.. Enter ";

	public final String ReEnterPrompt = "Re-Enter ";

	public static final String FrequencyPrompt = "Frequency 1-> One time 2->Recurring: ";

	public final String FromAccountCaption = "From Account : ";

	public final String ToCaption = "To Account : ";

	public final String TransferMethodPrompt = EnterPrompt
			+ "Transfer Method : 1 -> Next Day | 2 -> Standard :";

	public final String TransferMethodReEnterPrompt = InvalidEnterPrompt
			+ TransferMethodPrompt + EXIT_STRING;

	public final String AccountTypePrompt = "Enter 1 -> Saving | 2 -> Checking : ";

	public final String AccountTypeReEnterPrompt = "Invalid Entry.. Enter 1 -> Saving | 2 -> Checking or 0 to Quit : ";

	public final String AccountCategoryPrompt = "Enter 1 -> Banking | 2 -> Brokerage : ";

	public final String AccountCategoryReEnterPrompt = "Invalid Entry.. Enter 1 -> Banking | 2 -> Brokerage or 0 to Quit : ";

	public final String BankNamePrompt = EnterPrompt + "Bank Name : ";

	public final String BankNameReEnterPrompt = InvalidEnterPrompt
			+ BankNamePrompt + EXIT_STRING;

	public final String RoutingNumberPrompt = EnterPrompt + "Routing Number : ";

	public final String RoutingNumberReEnterPrompt = InvalidEnterPrompt
			+ RoutingNumberPrompt + EXIT_STRING;

	public final String AccountNumberPrompt = EnterPrompt + "Account Number : ";

	public final String AccountNumberReEnterPrompt = InvalidEnterPrompt
			+ AccountNumberPrompt + EXIT_STRING;

	public final String ReAccountNumberEnterPrompt = ReEnterPrompt
			+ "Account Number : ";

	public final String ReAccountNumberReEnterPrompt = "Both the Account Numbers do not match..."
			+ ReAccountNumberEnterPrompt + EXIT_STRING;

	public final String ItemIdPrompt = EnterPrompt + "ItemId to be Enabled : ";

	public final String ReItemIdPrompt = InvalidEnterPrompt + ItemIdPrompt
			+ EXIT_STRING;

	public final String TransferIdPrompt = EnterPrompt + "ItemId : ";

	public final String TransferIdReEnterPrompt = InvalidEnterPrompt
			+ TransferIdPrompt + EXIT_STRING;

	
	public final String Amount1Prompt = EnterPrompt + "Amount 1 : ";
	
	public final String CDCreditAmountPrompt = EnterPrompt + "Credit Amount";
	
	public final String CDDebitAmountPrompt = EnterPrompt + "Debit Amount";

	public final String Amount1ReEnterPrompt = InvalidEnterPrompt
			+ Amount1Prompt + EXIT_STRING;

	public final String Amount2Prompt = EnterPrompt + "Amount 2 : ";

	public final String Amount2ReEnterPrompt = InvalidEnterPrompt
			+ Amount2Prompt + EXIT_STRING;

	public final String TransAcctID2Prompt = EnterPrompt
			+ "Transfer Account ID : ";

	public final String TransAcctID2ReEnterPrompt = InvalidEnterPrompt
			+ TransAcctID2Prompt + EXIT_STRING;

	public final String TransReqID2Prompt = EnterPrompt
			+ "Transfer Request ID : ";

	public final String TransReqID2ReEnterPrompt = InvalidEnterPrompt
			+ TransReqID2Prompt + EXIT_STRING;

	public static final String TransferIdFromPrompt = EnterPrompt
			+ "From TransferAccount Id : ";

	public final String TransferIdFromReEnterPrompt = InvalidEnterPrompt
			+ TransferIdFromPrompt + EXIT_STRING;

	public static final String TransferIdToPrompt = EnterPrompt
			+ "To TransferAccount Id : ";

	public final String TransferIdToReEnterPrompt = InvalidEnterPrompt
			+ TransferIdToPrompt + EXIT_STRING;

	public static final String AmountPrompt = EnterPrompt
			+ "Transfer Amount : ";

	public static final String AmountReEnterPrompt = InvalidEnterPrompt
			+ AmountPrompt + EXIT_STRING;

	public static final String SumInfoIDPrompt = EnterPrompt + "Sum Info ID : ";

	public static final String SumInfoIDReEnterPrompt = InvalidEnterPrompt
			+ AmountPrompt + EXIT_STRING;

	public static final String dateFormat = "MM/dd/yyyy";

	public static final String StartDtPrompt = "Start Date(" + dateFormat
			+ ") : ";

	public static final String StartDtReEnterPrompt = InvalidEnterPrompt
			+ StartDtPrompt + EXIT_STRING;

	public static final String EndDtPrompt = "End Date(" + dateFormat + ") : ";

	public static final String EndDtReEnterPrompt = InvalidEnterPrompt
			+ EndDtPrompt + EXIT_STRING;

	public static final String CURR_CODE = "USD";

	public static final String DEFAULT_CURRENCY = "USD";

	public static final String FrequencyDurationPrompt = "Frequency Duration : ";
	
	public final String IdentityVerifyStatus2Prompt = EnterPrompt
	+ "Identity Verification Status ID : ";

   public final String IdentityVerifyStatus2ReEnterPrompt = InvalidEnterPrompt
	+ IdentityVerifyStatus2Prompt + EXIT_STRING;
   
   
   public final String SSN2Prompt = EnterPrompt
	+ "SSN : ";

  public final String SSN2ReEnterPrompt = InvalidEnterPrompt
	+ IdentityVerifyStatus2Prompt + EXIT_STRING;
  
  
  public final String memo2Prompt = EnterPrompt
	+ "Transfer Memo : ";

  public final String memo2ReEnterPrompt = InvalidEnterPrompt
	+ memo2Prompt + EXIT_STRING;
  
  
  public final String nickName2Prompt = EnterPrompt
	+ "Nick Name for the Account : ";

  public final String nickName2ReEnterPrompt = InvalidEnterPrompt
	+ nickName2Prompt + EXIT_STRING;
  
  public final String itemAccountID2Prompt = EnterPrompt
	+ "Item Account ID : ";

  public final String itemAccountID2ReEnterPrompt = InvalidEnterPrompt
	+ itemAccountID2Prompt + EXIT_STRING;

	public static final int ONE_TIME = 1;

	public static final int RECURRING = 2;

	public static final int WEEKLY = 0;

	public static final int MONTHLY = 3;

	public static final int QUARTERLY = 4;

	public static final int HALF_YEARLY = 5;

	public static final int ANNUALLY = 6;

	public static final String EMPTY_STRING = "";

	public static final int NEXT_DAY = 1;

	public static final int STANDARD_SRV = 2;

	public static final double FEE_PARAM_FREE = 0;

	public static final String ONE_TIME_STRING = "One time only";

	public static final String RECURRING_STRING = "Recurring";

}
