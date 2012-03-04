package YodleeSrc;

/*
 * Copyright 2010 Yodlee, Inc.  All Rights Reserved.  Your use of this code 
 * requires a license from Yodlee.  Any such license to this code is 
 * restricted to evaluation/illustrative purposes only. It is not intended 
 * for use in a production environment, and Yodlee disclaims all warranties 
 * and/or support obligations concerning this code, regardless of the terms 
 * of any other agreements between Yodlee and you."
 */


import java.rmi.RemoteException;

import javax.xml.rpc.ServiceException;

import com.yodlee.soap.collections.ArrayOfString;
import com.yodlee.soap.collections.common.ArrayOfUserCredentials;
import com.yodlee.soap.collections.core.accountdataservice.ArrayOfFinancialAccount;
import com.yodlee.soap.collections.core.accountdataservice.ArrayOfItemAccount;
import com.yodlee.soap.collections.core.accountdataservice.ArrayOfItemData;
import com.yodlee.soap.collections.core.accountdataservice.ArrayOfItemInfo;
import com.yodlee.soap.collections.core.batch.accountdataservice.ArrayOfBatchFinancialAccountResponse;
import com.yodlee.soap.collections.core.batch.accountdataservice.ArrayOfBatchItemDataResponse;
import com.yodlee.soap.collections.core.batch.accountdataservice.ArrayOfBatchItemInfoResponse;
import com.yodlee.soap.common.UserCredentials;
import com.yodlee.soap.core.CoreExceptionFault;
import com.yodlee.soap.core.IllegalArgumentValueExceptionFault;
import com.yodlee.soap.core.InvalidCobrandContextExceptionFault;
import com.yodlee.soap.core.InvalidCobrandConversationCredentialsExceptionFault;
import com.yodlee.soap.core.InvalidConversationCredentialsExceptionFault;
import com.yodlee.soap.core.StaleConversationCredentialsExceptionFault;
import com.yodlee.soap.core.accountdataservice.FinancialAccount;
import com.yodlee.soap.core.accountdataservice.InvalidAccountDataSelectorListExceptionFault;
import com.yodlee.soap.core.accountdataservice.ItemAccount;
import com.yodlee.soap.core.accountdataservice.ItemData;
import com.yodlee.soap.core.accountdataservice.ItemInfo;
import com.yodlee.soap.core.accountdataservice.PersonInformation;
import com.yodlee.soap.core.batch.BatchSizeExceededExceptionFault;
import com.yodlee.soap.core.batch.BatchUserCredentials;
import com.yodlee.soap.core.batch.InvalidBatchCredentialsExceptionFault;
import com.yodlee.soap.core.batch.InvalidBatchRequestExceptionFault;
import com.yodlee.soap.core.batch.accountdataservice.BatchFinancialAccountResponse;
import com.yodlee.soap.core.batch.accountdataservice.BatchItemDataResponse;
import com.yodlee.soap.core.batch.accountdataservice.BatchItemInfoResponse;
import com.yodlee.soap.core.batch.accountdataservice.batchaccountdataservice.BatchAccountDataService;
import com.yodlee.soap.core.batch.accountdataservice.batchaccountdataservice.BatchAccountDataServiceServiceLocator;
import com.yodlee.soap.ext.login.PasswordCredentials;

public class BatchAccount extends ApplicationSuper {

	/** Navigation Counter. **/
	private static int optionCount = 1;
	/** Navigation Menu Choice. * */
	private static final int NAV_BATCH_ACCOUNT_GET_ITEM_DATA = optionCount++;
	private static final int NAV_BATCH_ACCOUNT_GET_FINANCIAL_ACCOUNTS = optionCount++;
	private static final int NAV_BATCH_ACCOUNT_GET_ITEM_INFO = optionCount++;
	/** Navigation Menu Choice. **/
	private static final int NAV_QUIT = 0;

	protected static BatchAccountDataService batchAccountDataService;

	private BatchUserCredentials batchUserCredentials = new BatchUserCredentials();
	private ArrayOfString arrayOfString = new ArrayOfString();

	static {
		BatchAccountDataServiceServiceLocator locator = new BatchAccountDataServiceServiceLocator();
		String serviceName = locator
				.getBatchAccountDataServiceWSDDServiceName();
		locator.setBatchAccountDataServiceEndpointAddress(System
				.getProperty("com.yodlee.soap.services.url")
				+ "/"
				+ serviceName);
		try {
			batchAccountDataService = locator.getBatchAccountDataService();
		} catch (ServiceException se) {
			throw new RuntimeException(se);
		}
	}

	public void doMenu() {
		boolean loop = true;
		int choice = 0;
		while (loop) {
			try {
				System.out.println("Batch Account Menu");
				System.out.println("********************");
				System.out.println(NAV_BATCH_ACCOUNT_GET_ITEM_DATA
						+ ". Get Item Data");
				System.out.println(NAV_BATCH_ACCOUNT_GET_FINANCIAL_ACCOUNTS
						+ ". Get Financial Accounts");
				System.out.println(NAV_BATCH_ACCOUNT_GET_ITEM_INFO
						+ ". Get Item Info");
				System.out.println(NAV_QUIT + ". Exit Sub-menu");
				System.out.println("********************");
				System.out.print("Enter Choice : ");
				choice = IOUtils.readInt();

				if (choice == NAV_BATCH_ACCOUNT_GET_ITEM_DATA) {
					getItemData();
				} else if (choice == NAV_BATCH_ACCOUNT_GET_FINANCIAL_ACCOUNTS) {
					getFinancialAccounts();
				} else if (choice == NAV_BATCH_ACCOUNT_GET_ITEM_INFO) {
					getItemInfo();
				} else if (choice == NAV_QUIT)
					loop = false;
				else
					System.out.println("Invalid Entry!");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Get the Input Data.
	 */
	private void getInputData() {

		int numberOfUsers;

		do {
			System.out.print("Enter the number of users (less than 500): ");
			numberOfUsers = IOUtils.readInt();
		} while (numberOfUsers < 1 || numberOfUsers > 500);

		String input;
		String userName;
		PasswordCredentials passwordCredentials;
		UserCredentials[] userCredentials = new UserCredentials[numberOfUsers];
		String[] userNames = new String[numberOfUsers];
		for (int i = 0; i < numberOfUsers; i++) {
			System.out.print("Enter username: ");
			input = IOUtils.readStr();
			userName = null;

			if (input != null) {
				userName = input;
			}

			String password = "";
			System.out.print("Enter password: ");
			input = IOUtils.readStr();

			if (input != null) {
				password = input;
			}

			passwordCredentials = new PasswordCredentials(userName, password);
			userCredentials[i] = passwordCredentials;
			userNames[i] = userName;
		}
		ArrayOfUserCredentials arrayOfUserCredentials = new ArrayOfUserCredentials();
		arrayOfUserCredentials.setElements(userCredentials);
		batchUserCredentials.setUserCredentials(arrayOfUserCredentials);
		arrayOfString.setElements(userNames);

	}

	/**
	 * Print the Item Data.
	 */
	public void getItemData() {

		getInputData();
		ArrayOfBatchItemDataResponse arrayOfBatchItemDataResponse = null;
		try {
			arrayOfBatchItemDataResponse = batchAccountDataService.getItemData(
					getCobrandContext(), batchUserCredentials, arrayOfString,
					null, true, null);
		} catch (BatchSizeExceededExceptionFault e) {
			e.printStackTrace();
		} catch (InvalidBatchCredentialsExceptionFault e) {
			e.printStackTrace();
		} catch (StaleConversationCredentialsExceptionFault e) {
			e.printStackTrace();
		} catch (InvalidBatchRequestExceptionFault e) {
			e.printStackTrace();
		} catch (InvalidCobrandConversationCredentialsExceptionFault e) {
			e.printStackTrace();
		} catch (InvalidCobrandContextExceptionFault e) {
			e.printStackTrace();
		} catch (InvalidAccountDataSelectorListExceptionFault e) {
			e.printStackTrace();
		} catch (InvalidConversationCredentialsExceptionFault e) {
			e.printStackTrace();
		} catch (CoreExceptionFault e) {
			e.printStackTrace();
		} catch (IllegalArgumentValueExceptionFault e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		}

		if (arrayOfBatchItemDataResponse == null) {
			System.out.println("No batch Item Data Responses returned.");
			return;
		}
		BatchItemDataResponse[] batchItemDataResponses = arrayOfBatchItemDataResponse
				.getElements();
		System.out.println();
		if (batchItemDataResponses == null) {
			System.out.println("No batch Item Data Responses returned.");
			return;
		}

		ArrayOfItemData arrayOfItem;
		ItemData[] itemDataArray;
		ArrayOfItemAccount arrayOfItemAccount;
		ItemAccount[] itemAccounts;

		for (BatchItemDataResponse batchItemDataResponse : batchItemDataResponses) {
			System.out.println("Username: "
					+ batchItemDataResponse.getUserName());
			
			PrintUtil.printErrorMessage(batchItemDataResponse.getErrorMsg());
			
			System.out.println();
			arrayOfItem = batchItemDataResponse.getItemData();
			if (arrayOfItem == null) {
				System.out.println("No item data.");
				return;
			}
			itemDataArray = arrayOfItem.getElements();
			if (itemDataArray != null) {
				for (ItemData itemData : itemDataArray) {
					// ItemInfo itemInfo = itemData.getItemInfo();
					// printItemInfo(itemInfo);
					arrayOfItemAccount = itemData.getItemAccounts();
					if (arrayOfItemAccount != null) {
						itemAccounts = arrayOfItemAccount.getElements();
						for (ItemAccount itemAccount : itemAccounts) {
							PrintUtil.printItemAccount(itemAccount);
							System.out.println();
						}
					}
					System.out.println();
				}
			}
		}

	}

	/**
	 * Print the Financial Accounts.
	 */
	public void getFinancialAccounts() {

		getInputData();
		ArrayOfBatchFinancialAccountResponse arrayOfBatchFinancialAccountResponse = null;
		try {
			arrayOfBatchFinancialAccountResponse = batchAccountDataService
					.getFinancialAccounts(getCobrandContext(),
							batchUserCredentials, arrayOfString, null, true,
							null);
		} catch (BatchSizeExceededExceptionFault e) {
			e.printStackTrace();
		} catch (InvalidBatchCredentialsExceptionFault e) {
			e.printStackTrace();
		} catch (StaleConversationCredentialsExceptionFault e) {
			e.printStackTrace();
		} catch (InvalidBatchRequestExceptionFault e) {
			e.printStackTrace();
		} catch (InvalidCobrandConversationCredentialsExceptionFault e) {
			e.printStackTrace();
		} catch (InvalidCobrandContextExceptionFault e) {
			e.printStackTrace();
		} catch (InvalidAccountDataSelectorListExceptionFault e) {
			e.printStackTrace();
		} catch (InvalidConversationCredentialsExceptionFault e) {
			e.printStackTrace();
		} catch (CoreExceptionFault e) {
			e.printStackTrace();
		} catch (IllegalArgumentValueExceptionFault e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		}

		if (arrayOfBatchFinancialAccountResponse == null) {
			System.out
					.println("No batch Financial Account Responses returned.");
			return;
		}
		BatchFinancialAccountResponse[] batchFinancialAccountResponses = arrayOfBatchFinancialAccountResponse
				.getElements();
		System.out.println();
		if (batchFinancialAccountResponses == null) {
			System.out
					.println("No batch Financial Account Responses returned.");
			return;
		}

		ArrayOfFinancialAccount arrayOfFinancialAccount;
		FinancialAccount[] financialAccountArray;
		PersonInformation personInformation;

		for (BatchFinancialAccountResponse batchFinancialAccountResponse : batchFinancialAccountResponses) {
			System.out.println("Username: "
					+ batchFinancialAccountResponse.getUserName());

			PrintUtil.printErrorMessage(batchFinancialAccountResponse.getErrorMsg());
			
			System.out.println();
			arrayOfFinancialAccount = batchFinancialAccountResponse
					.getFinancialAccounts();
			if (arrayOfFinancialAccount == null) {
				System.out.println("No Financial Accounts.");
				return;
			}
			financialAccountArray = arrayOfFinancialAccount.getElements();
			if (financialAccountArray != null) {
				for (FinancialAccount financialAccount : financialAccountArray) {
					personInformation = financialAccount.getAccountHolder();
					if (personInformation != null) {
						System.out.println("Full name: "
								+ personInformation.getFullName());
					}
					PrintUtil.printItemAccount(financialAccount);
					System.out.println();
				}
				System.out.println();
			}
		}
	}

	/**
	 * Print the Item Info.
	 */
	public void getItemInfo() {

		getInputData();
		ArrayOfBatchItemInfoResponse arrayOfBatchItemInfoResponse = null;
		try {
			arrayOfBatchItemInfoResponse = batchAccountDataService.getItemInfo(
					getCobrandContext(), batchUserCredentials, arrayOfString,
					true);
		} catch (BatchSizeExceededExceptionFault e) {
			e.printStackTrace();
		} catch (InvalidBatchCredentialsExceptionFault e) {
			e.printStackTrace();
		} catch (StaleConversationCredentialsExceptionFault e) {
			e.printStackTrace();
		} catch (InvalidBatchRequestExceptionFault e) {
			e.printStackTrace();
		} catch (InvalidCobrandConversationCredentialsExceptionFault e) {
			e.printStackTrace();
		} catch (InvalidCobrandContextExceptionFault e) {
			e.printStackTrace();
		} catch (InvalidAccountDataSelectorListExceptionFault e) {
			e.printStackTrace();
		} catch (InvalidConversationCredentialsExceptionFault e) {
			e.printStackTrace();
		} catch (CoreExceptionFault e) {
			e.printStackTrace();
		} catch (IllegalArgumentValueExceptionFault e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		}

		if (arrayOfBatchItemInfoResponse == null) {
			System.out.println("No batch Item Info Response returned.");
			return;
		}
		BatchItemInfoResponse[] batchItemInfoResponses = arrayOfBatchItemInfoResponse
				.getElements();
		System.out.println();
		if (batchItemInfoResponses == null) {
			System.out.println("No batch Item Info Response returned.");
			return;
		}

		ArrayOfItemInfo arrayOfItemInfo;
		ItemInfo[] itemInfos;
		
		for (BatchItemInfoResponse batchItemInfoResponse : batchItemInfoResponses) {
			System.out.println("Username: "
					+ batchItemInfoResponse.getUserName());

			PrintUtil.printErrorMessage(batchItemInfoResponse.getErrorMsg());
			
			System.out.println();
			arrayOfItemInfo = batchItemInfoResponse.getItemInfos();
			if (arrayOfItemInfo == null) {
				System.out.println("No Item Info.");
				return;
			}
			itemInfos = arrayOfItemInfo.getElements();
			if (itemInfos != null) {
				for (ItemInfo itemInfo : itemInfos) {
					PrintUtil.printItemInfo(itemInfo);
					System.out.println();
				}
			}
		}
	}
}
