/*
 * Copyright 2009 Yodlee, Inc.  All Rights Reserved.  Your use of this code 
 * requires a license from Yodlee.  Any such license to this code is 
 * restricted to evaluation/illustrative purposes only. It is not intended 
 * for use in a production environment, and Yodlee disclaims all warranties 
 * and/or support obligations concerning this code, regardless of the terms 
 * of any other agreements between Yodlee and you.
 */
package com.yodlee.sampleapps.helper;

import java.rmi.RemoteException;

import com.yodlee.soap.collections.core.fundstransfer.transfermanagement.transactionchecks.ArrayOfTransferLimit;
import com.yodlee.soap.common.CobrandContext;
import com.yodlee.soap.common.UserContext;
import com.yodlee.soap.core.CoreExceptionFault;
import com.yodlee.soap.core.IllegalArgumentValueExceptionFault;
import com.yodlee.soap.core.InvalidCobrandContextExceptionFault;
import com.yodlee.soap.core.InvalidCobrandConversationCredentialsExceptionFault;
import com.yodlee.soap.core.InvalidConversationCredentialsExceptionFault;
import com.yodlee.soap.core.StaleConversationCredentialsExceptionFault;
import com.yodlee.soap.core.fundstransfer.transfermanagement.transferlimitmanagement.TransferLimitManagement;
import com.yodlee.soap.core.fundstransfer.transfermanagement.transferlimitmanagement.TransferLimitManagementServiceLocator;
import com.yodlee.soap.core.fundstransfer.transfermanagement.TransferMode;
import com.yodlee.soap.core.fundstransfer.transfermanagement.transactionchecks.LimitOnDayBasis;
import com.yodlee.soap.core.fundstransfer.transfermanagement.transactionchecks.LimitOnMonthBasis;
import com.yodlee.soap.core.fundstransfer.transfermanagement.transactionchecks.MaximumLimit;
import com.yodlee.soap.core.fundstransfer.transfermanagement.transactionchecks.MinimumLimit;
import com.yodlee.soap.core.fundstransfer.transfermanagement.transactionchecks.SingleTransferLimit;
import com.yodlee.soap.core.fundstransfer.transfermanagement.transactionchecks.TransferLimit;
import com.yodlee.soap.core.routingnumberservice.routingnumberservice.RoutingNumberServiceServiceLocator;

/**
 * A utility class for holding the TransferLimits Data for the cobrand and
 * displaying the same to the end user.
 * 
 */
public class TransferLimitsUtil {
	/** Transfer Mode display string. */
	public static final String TRANSFER_MODE = "TRANSFER_MODE";

	/** Transfer Mode :Next Day. */
	public static final String nextDayMode = "Next Day Service";

	/** Transfer Mode :Standard. */
	public static final String standardMode = "Standard Service";

	/** Transfer Direction :Inbound. */
	public static final String TransferDirection_InBound = "INBOUND";

	/** Transfer Direction :Outbound. */
	public static final String TransferDirection_OutBound = "OUTBOUND";

	/** Transfer Direction :Third Party. */
	public static final String TransferDirection_ThirdParty = "THIRD PARTY";

	/** Transfer Direction :On US. */
	public static final String TransferDirection_OnUS = "ON US";

	/** Transfer Direction :On US. */
	public static final String TransferDirection_DirectionLess = "DIRECTION LESS";

	/** Transfer limit management  */
	public static TransferLimitManagement transferLimitManagement;

	static {
		
//		Create TransferLimitManagement Locator 
		TransferLimitManagementServiceLocator transferLimitManagementServiceLocator = new TransferLimitManagementServiceLocator();
		String transferLimitManagementServiceName = transferLimitManagementServiceLocator.getTransferLimitManagementServiceWSDDServiceName();
		transferLimitManagementServiceLocator.setTransferLimitManagementServiceEndpointAddress(System.getProperty("com.yodlee.soap.services.url") 
				+ "/" + transferLimitManagementServiceName);
		try{
			transferLimitManagement = transferLimitManagementServiceLocator.getTransferLimitManagementService();
		}catch(Exception lse) {

			}
	}

	/**
	 * Retrives the Max Transfer Limit for the Cobrand.
	 * 
	 * @param cobrandContext
	 *            Cobrand context
	 */
	public static double getMaxLimit(UserContext userContext) {
		TransferLimit[] transferLimit = null;
		try {
			ArrayOfTransferLimit arrayOfTransferLimit = transferLimitManagement
					.getTransferLimits1(userContext);
			if(arrayOfTransferLimit != null){
				transferLimit = arrayOfTransferLimit.getElements();
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
		double maxLimit = 0.0;

		if (transferLimit != null) {
			for (int i = 0; i < transferLimit.length; i++) {

				if (transferLimit[i] instanceof MaximumLimit) {
					MaximumLimit maximumLimit = (MaximumLimit) transferLimit[i];
					//maxLimit = (maximumLimit.getAmount()).getAmount()
							//.doubleValue();
					maxLimit = maximumLimit.getAmount().getAmount().doubleValue();
				}
			}
		}
		return maxLimit;

	}

	/**
	 * Retrives Min Transfer Limit for the Cobrand.
	 * 
	 * @param cobrandContext
	 *            Cobrand context
	 */
	public static double getMinLimit(UserContext userContext) {

		TransferLimit[] transferLimit = null;
		try {
			ArrayOfTransferLimit arrayOfTransferLimit = transferLimitManagement
					.getTransferLimits1(userContext);
			if(arrayOfTransferLimit != null){
				transferLimit = arrayOfTransferLimit.getElements();
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
		double minLimit = 0.0;

		if (transferLimit != null) {
			for (int i = 0; i < transferLimit.length; i++) {

				if (transferLimit[i] instanceof MinimumLimit) {
					MinimumLimit minimumLimit = (MinimumLimit) transferLimit[i];
					minLimit = minimumLimit.getAmount().getAmount()
							.doubleValue();

				}
			}
		}
		return minLimit;

	}

	/**
	 * Displays transfer limits daya for a cobrnad.
	 * 
	 * @param cobrandContext
	 *            Cobrand context
	 */
	public static void displayTransferLimits(UserContext userContext) {

		TransferLimit[] transferLimit = null;
		try {
			ArrayOfTransferLimit arrayOfTransferLimit = transferLimitManagement
					.getTransferLimits1(userContext);
			if(arrayOfTransferLimit != null){
				transferLimit = arrayOfTransferLimit.getElements();
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

		//getTransferLimitsData(transferLimit[0]);
		if (transferLimit != null) {
			for (int i = 0; i < transferLimit.length; i++) {
				getTransferLimitsData(transferLimit[i]);

			}
		}

	}

	/**
	 * Displays the Transfer limits of following types 1. Limit on daily basis
	 * 2. Limit on monthly basis 3. Maximum limit 4. Minimum Limit
	 * 
	 * @param transferLimit
	 *            TransferLimit Object.
	 * 
	 */
	public static void getTransferLimitsData(TransferLimit transferLimit) {

		String transferDuration = null;
		String transferText = null;
		String transferMode = null;

		TransferMode modeId;
		int days;
		int months;

		if (transferLimit instanceof LimitOnDayBasis) {
			LimitOnDayBasis limitOnDayBasis = (LimitOnDayBasis) transferLimit;

			modeId = limitOnDayBasis.getMode();
			days = limitOnDayBasis.getNumberOfDays();
			if (days == 1) {
				transferDuration = "1";
				transferText = "day";
			} else if (days == 7) {
				transferDuration = "7";
				transferText = "days";
			}

			if (modeId == TransferMode.NEXT_DAY) {
				transferMode = nextDayMode;
			} else if (modeId == TransferMode.STANDARD) {
				transferMode = standardMode;
			}
			String transferAmountLimitsStr = limitOnDayBasis.getAmount().getAmount()
					.toString();
			String transferDir = limitOnDayBasis.getTransferDirection().getValue();
			String viewDisplayString = "Transfer limit for " + transferMode
					+ " in " + transferDuration + " " + transferText
					+ " and for transfer Direction " + transferDir + " is "
					+ transferAmountLimitsStr;
			System.out.println(viewDisplayString);
		} 
		
		else if (transferLimit instanceof SingleTransferLimit) {
			SingleTransferLimit singleTransferLimit = (SingleTransferLimit) transferLimit;

			modeId = singleTransferLimit.getTransferMode();
			String freq = singleTransferLimit.getFrequency().getValue();
			

			if (modeId == TransferMode.NEXT_DAY) {
				transferMode = nextDayMode;
			} else if (modeId == TransferMode.STANDARD) {
				transferMode = standardMode;
			}
			
			String transferAmountLimitsMax= singleTransferLimit.getMaxAmount().getAmount().toString();
			String transferAmountLimitsMin= singleTransferLimit.getMinAmount().getAmount().toString();		
			String transferDir = singleTransferLimit.getDirection().getValue();
			String viewDisplayString = "Transfer limit for " + freq+ " frequency " + transferMode
										+ " and  transfer Direction " + transferDir + " is "
					+ "Minimun " + transferAmountLimitsMin + "Maximum " + transferAmountLimitsMax;
			System.out.println(viewDisplayString);
		}
		
		else if (transferLimit instanceof LimitOnMonthBasis) {
			LimitOnMonthBasis limitOnMonthBasis = (LimitOnMonthBasis) transferLimit;

			modeId = limitOnMonthBasis.getMode();
			months = limitOnMonthBasis.getNumberOfMonths();
			if (months == 1) {
				transferDuration = "" + months;
				transferText = "month";
			} else {
				transferDuration = "" + months;
				transferText = "months";
			}

			if (modeId == TransferMode.NEXT_DAY) {
				transferMode = nextDayMode;
			} else if (modeId == TransferMode.STANDARD) {
				transferMode = standardMode;
			}

			String transferAmountLimitsStr = limitOnMonthBasis.getAmount().getAmount()
					.toString();
			String transferDir = limitOnMonthBasis.getTransferDirection().getValue();
			String viewDisplayString = "Transfer Limits for " + transferMode
					+ " in " + transferDuration + " " + transferText
					+ " and for transfer Direction " + transferDir + " is "
					+ transferAmountLimitsStr;
			System.out.println(viewDisplayString);
		} else if (transferLimit instanceof MaximumLimit) {
			MaximumLimit maximumLimit = (MaximumLimit) transferLimit;
			String transferAmountLimitsStr = maximumLimit.getAmount().getAmount()
					.toString();
			//String transferDir = maximumLimit.getTransferDirection().getValue();
			String viewDisplayString = "Maximum transfer limit per transfer  is " + transferAmountLimitsStr;
			System.out.println(viewDisplayString);
		} else if (transferLimit instanceof MinimumLimit) {
			MinimumLimit minimumLimit = (MinimumLimit) transferLimit;
			String transferAmountLimitsStr = minimumLimit.getAmount().getAmount().toString();
					
			//String transferDir = minimumLimit.getTransferDirection().getValue();
			String viewDisplayString = "Minimum transfer limit per transfer  is " + transferAmountLimitsStr;

			System.out.println(viewDisplayString);
		}

	}

	public static String getTransferDirStr(int TransDir) {
		if (TransDir == 1)
			return TransferDirection_InBound;
		else if (TransDir == 2)
			return TransferDirection_OutBound;
		else if (TransDir == 4)
			return TransferDirection_ThirdParty;
		else if (TransDir == 3)
			return TransferDirection_OnUS;
		else
			return TransferDirection_DirectionLess;

	}

}
