/**
 * Copyright 2010 Yodlee, Inc.  All Rights Reserved.  Your use of this code 
 * requires a license from Yodlee.  Any such license to this code is 
 * restricted to evaluation/illustrative purposes only. It is not intended 
 * for use in a production environment, and Yodlee disclaims all warranties 
 * and/or support obligations concerning this code, regardless of the terms 
 * of any other agreements between Yodlee and you.
 */
package YodleeSrc;

import com.yodlee.soap.common.CalendarDate;
import com.yodlee.soap.common.Money;
import com.yodlee.soap.core.accountdataservice.AutoPayEnrollmentStatus;
import com.yodlee.soap.core.accountdataservice.BankAccount;
import com.yodlee.soap.core.accountdataservice.BankAccountType;
import com.yodlee.soap.core.accountdataservice.BillingAccount;
import com.yodlee.soap.core.accountdataservice.DataType;
import com.yodlee.soap.core.accountdataservice.ItemAccessStatus;
import com.yodlee.soap.core.accountdataservice.ItemAccount;
import com.yodlee.soap.core.accountdataservice.ItemAccountInfo;
import com.yodlee.soap.core.accountdataservice.ItemInfo;

/**
 * PrintUtil provide convenient methods to print common business object like
 * Money, ItemAccount, ItemAccountInfo, ItemAccountInfo, etc
 * 
 */
public class PrintUtil {

	/**
	 * Print the ItemInfo.
	 */
	public static void printItemInfo(ItemInfo itemInfo) {

		if (itemInfo == null)
			return;

		DataType dataType = itemInfo.getAccountDataType();
		System.out.println("Item Id: " + itemInfo.getItemId());
		if (dataType != null) {
			System.out.println("Account Data Type: " + dataType.getValue());
		}
		ItemAccessStatus itemAccessStatus = itemInfo.getItemAccessStatus();
		if (itemAccessStatus != null) {
			System.out.println("Item Access Status: "
					+ itemAccessStatus.getValue());
		}
		System.out.println("Content Service Id: "
				+ itemInfo.getContentServiceId());
	}

	/**
	 * Print the ItemAccountInfo.
	 */
	public static void printItemAccountInfo(ItemAccountInfo itemAccountInfo) {

		if (itemAccountInfo == null)
			return;

		System.out.println("Account Name: "
				+ itemAccountInfo.getItemAccountName());
		System.out.println("Account Number: "
				+ itemAccountInfo.getItemAccountNumber());
		System.out.println("Account Status: "
				+ itemAccountInfo.getItemAccountStatus());
		ItemInfo itemInfo = itemAccountInfo.getItemInfo();
		printItemInfo(itemInfo);
	}

	/**
	 * Print the Item Account.
	 */
	public static void printItemAccount(ItemAccount itemAccount) {

		if (itemAccount == null)
			return;

		printItemAccountInfo(itemAccount.getItemAccountInfo());

		if (itemAccount instanceof BankAccount) {
			BankAccount bankAccount = (BankAccount) itemAccount;
			printMoney("Current Balance: ", bankAccount.getCurrentBalance());
			BankAccountType bankAccountType = bankAccount.getBankAccountType();
			if (bankAccountType != null) {
				System.out.println("Bank Account Type: "
						+ bankAccountType.getValue());
			}
		}
		if (itemAccount instanceof BillingAccount) {
			BillingAccount billingAccount = (BillingAccount) itemAccount;
			printMoney("Last Payment Amount: ",
					billingAccount.getLastPaymentAmount());
			CalendarDate lastPaymentDate = billingAccount.getLastPaymentDate();
			if (lastPaymentDate != null) {
				System.out.println("Last Payment Date: "
						+ lastPaymentDate.getMonth() + "/"
						+ lastPaymentDate.getDayOfMonth() + "/"
						+ lastPaymentDate.getYear());
			}
			printMoney("Outstanding Amount Due: ",
					billingAccount.getOutstandingAmountDue());
			AutoPayEnrollmentStatus autoPayEnrollmentStatus = billingAccount
					.getUserAutoPayEnrollmentStatus();
			if (autoPayEnrollmentStatus != null) {
				System.out.println("AutoPay Enrollment Status: "
						+ autoPayEnrollmentStatus.getValue());
			}
		}
	}

	/**
	 * Print the Item Account.
	 */
	public static void printMoney(String message, Money money) {

		if (money == null)
			return;
		System.out.println(message + money.getCurrencyCode() + " "
				+ money.getAmount());

	}

	/**
	 * Print the error message if it is not null and empty string.
	 */
	public static void printErrorMessage(String errorMessage) {

		if (errorMessage != null && !errorMessage.trim().equals("")) {
			System.out.println("Error Message: " + errorMessage);
		}

	}

}
