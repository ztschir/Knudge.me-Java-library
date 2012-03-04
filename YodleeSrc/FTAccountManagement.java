package com.yodlee.sampleapps;

import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import com.yodlee.soap.collections.core.fundstransfer.transferaccountmanagement.ArrayOfTransferAccount;
import com.yodlee.soap.collections.core.routingnumberservice.ArrayOfRoutingNumberInfo;
import com.yodlee.soap.common.ContentServiceInfo;
import com.yodlee.soap.common.ItemSummary;
import com.yodlee.soap.common.UserContext;
import com.yodlee.soap.core.ContentServiceNotFoundExceptionFault;
import com.yodlee.soap.core.CoreExceptionFault;
import com.yodlee.soap.core.IllegalArgumentTypeExceptionFault;
import com.yodlee.soap.core.IllegalArgumentValueExceptionFault;
import com.yodlee.soap.core.IncompleteArgumentExceptionFault;
import com.yodlee.soap.core.InvalidCobrandContextExceptionFault;
import com.yodlee.soap.core.InvalidCobrandConversationCredentialsExceptionFault;
import com.yodlee.soap.core.InvalidConversationCredentialsExceptionFault;
import com.yodlee.soap.core.InvalidItemAccountIdExceptionFault;
import com.yodlee.soap.core.InvalidItemExceptionFault;
import com.yodlee.soap.core.InvalidUserContextExceptionFault;
import com.yodlee.soap.core.StaleConversationCredentialsExceptionFault;
import com.yodlee.soap.core.accountmanagement.itemmanagement.ItemManagement;
import com.yodlee.soap.core.accountmanagement.itemmanagement.ItemManagementServiceLocator;
import com.yodlee.soap.core.fundstransfer.transferaccountmanagement.BankTransferAccount;
import com.yodlee.soap.core.fundstransfer.transferaccountmanagement.BrokerageMetaDataBean;
import com.yodlee.soap.core.fundstransfer.transferaccountmanagement.BrokerageTransferAccount;
import com.yodlee.soap.core.fundstransfer.transferaccountmanagement.DFIAccount;
import com.yodlee.soap.core.fundstransfer.transferaccountmanagement.InvalidAccountNumberExceptionFault;
import com.yodlee.soap.core.fundstransfer.transferaccountmanagement.TransferAccount;
import com.yodlee.soap.core.fundstransfer.transferaccountmanagement.TransferAccountAlreadyExistsExceptionFault;
import com.yodlee.soap.core.fundstransfer.transferaccountmanagement.TransferAccountNicknameInUseExceptionFault;
import com.yodlee.soap.core.fundstransfer.transferaccountmanagement.TransferAccountNotFoundExceptionFault;
import com.yodlee.soap.core.fundstransfer.transfermanagement.PendingTransfersExistsExceptionFault;
import com.yodlee.soap.core.itemaccountmanagement.itemaccountmanagement.ItemAccountManagement;
import com.yodlee.soap.core.itemaccountmanagement.itemaccountmanagement.ItemAccountManagementServiceLocator;
import com.yodlee.soap.core.itemaccountmanagement.ItemDetails;
import com.yodlee.soap.core.itemaccountmanagement.exceptions.AccountAlreadyEnabledExceptionFault;
import com.yodlee.soap.core.routingnumberservice.InvalidRoutingNumberExceptionFault;
import com.yodlee.soap.core.routingnumberservice.RoutingNumberInfo;
import com.yodlee.soap.core.routingnumberservice.RoutingNumberNotFoundExceptionFault;
import com.yodlee.soap.ext.traversal.contentservicetraversal.ContentServiceTraversal;
import com.yodlee.soap.ext.traversal.contentservicetraversal.ContentServiceTraversalServiceLocator;
import com.yodlee.sampleapps.helper.FTHelper;
import com.yodlee.sampleapps.helper.FormUtil;
import com.yodlee.sampleapps.helper.IFileTransferConstants;
import com.yodlee.sampleapps.helper.IOUtils;

/**
 * Handles the operations related to the Funds Transfer Account Management.
 */
public class FTAccountManagement extends FundsTransfer {

	
	/**
	 * Represents the verification flag of a TransferAccount which is
	 * already verified.
	 */
	public static final int TransferAccount_VERIFIED = 1;
    
	private static int editBankPaymAccountOptionCount = 1;
	
	/** Navigation Menu Choice. **/
	private static final int  NAV_BANK_NICK_NAME = editBankPaymAccountOptionCount++;    
	/** Navigation Menu Choice. **/
	private static final int  NAV_BANK_DESCRIPTION = editBankPaymAccountOptionCount++; 
	/** Navigation Menu Choice. **/
	private static final int  NAV_ALL = editBankPaymAccountOptionCount++; 
	
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
			String routingNumber, String accountNumber, String accountType) {
		BankTransferAccount bankTransferAccount = new BankTransferAccount();
		bankTransferAccount.setNickname(bankName);

		// get DFI Account
		DFIAccount dFIAccount = new DFIAccount();
		dFIAccount.setRoutingNumber(routingNumber);
		dFIAccount.setAccountNumber(accountNumber);
		dFIAccount.setIsVerified(new Boolean(false));

		bankTransferAccount.setDfiAccount(dFIAccount);
		bankTransferAccount.setBankName(bankName);
		bankTransferAccount.setTransferAccountType(Integer
				.parseInt(accountType));
		return bankTransferAccount;
	}

	/**
	 * Returns a Brokerage Transfer Account given the following.
	 *
	 * @param brokerMetaDataBean
	 *            Brokerage MetadatBean
	 * @param accountNumber
	 *            Account Number
	 * @return BrokerageTransferAccount.
	 */
	public BrokerageTransferAccount getBrokerageTransferAccount(
			BrokerageMetaDataBean brokerMetaDataBean, String accountNumber) {
		BrokerageTransferAccount brokerageTransferAccount = new BrokerageTransferAccount();

		// get DFI Account
		DFIAccount dFIAccount = new DFIAccount();
		dFIAccount.setAccountNumber(accountNumber);
		dFIAccount.setRoutingNumber(brokerMetaDataBean.getBrokRoutingNo());

		brokerageTransferAccount.setDFIAccount(dFIAccount);
		brokerageTransferAccount.setBokerageMetaDataBean(brokerMetaDataBean);
		brokerageTransferAccount.setUserProvidedAccNo(accountNumber);
		brokerageTransferAccount.setContentServiceId(brokerMetaDataBean
				.getSumInfoId());
		brokerageTransferAccount.setNickname(brokerMetaDataBean
				.getDisplayName());
		return brokerageTransferAccount;
	}

	/**
	 * Adds Savings / Checking Bank Account. This method will be called either
	 * to directly add a new bank account or to enable existing item for Funds
	 * transfer. In case of adding totally new transfer account it will prompt
	 * user for following 1. Bank Name - if the input BankName is null. Routing
	 * Number In case, this method is called to enable existing item, BankName
	 * is sent as parameters to this method.
	 * <p>
	 * User is prompted for Following parameters in each of the above case
	 * Routing Number ,Account Type (Savings / Checking) Account Number. Re
	 * enter Account Number.
	 * <p>
	 * Then it does following validations 1. If the above 2 accounts nos are
	 * identical. 2. If the routing number is valid 3. Retrives the
	 * ContentServiceInfo Id for the given Bank Name, if present
	 *
	 * @param userContext
	 *            The user context of the caller
	 * @param bankName
	 *            The Bank Name
	 */
	public void addBankAccount(UserContext userContext, String bankName, String itemId) {

		ContentServiceInfo cs = null;

		/*
		 * Prompt user to enter bank name and routing number and account type,
		 *
		 */
		if (null == bankName) {
			bankName = IOUtils.promptInput(
					IFileTransferConstants.BankNamePrompt,
					IFileTransferConstants.BankNameReEnterPrompt);
		}

		String routingNumber = IOUtils.promptInput(
				IFileTransferConstants.RoutingNumberPrompt,
				IFileTransferConstants.RoutingNumberReEnterPrompt);



		String acctType = IOUtils.promptInput(
				IFileTransferConstants.AccountTypePrompt,
				IFileTransferConstants.AccountTypeReEnterPrompt);

		// Verify Routing Number
		try {
			cs = rns.getContentServiceInfoByRoutingNumber(userContext,
					routingNumber);

		} catch (InvalidRoutingNumberExceptionFault irne) {
			System.out.println("\n Invalid Routing Number...." + routingNumber);
			System.out.println(irne.toString());
		} catch (RoutingNumberNotFoundExceptionFault rnNotFound) {
			System.out.println("\n Routing Number...." + routingNumber
					+ " Not found");
			System.out.println(rnNotFound.toString());
		}catch (RemoteException re) {
			System.out.println("\n Remote Exception....");
			System.out.println(re.toString());
			re.printStackTrace();
		}


		System.out.println("Routing number maps to: " + cs.getSiteDisplayName());


		/* Prompt user to enter all the bank account details */

		String accountNumber = IOUtils.promptInput(
				IFileTransferConstants.AccountNumberPrompt,
				IFileTransferConstants.AccountNumberReEnterPrompt);
		String reTypeAccountNumber = IOUtils.promptInput(
				IFileTransferConstants.ReAccountNumberEnterPrompt,
				IFileTransferConstants.ReAccountNumberReEnterPrompt);

		if (accountNumber.equals(reTypeAccountNumber)) {
			BankTransferAccount bankTransferAccount = getBankTransferAccount(
					bankName, routingNumber, accountNumber, acctType);
			if (cs != null)
				bankTransferAccount.setContentServiceId(cs 
						.getContentServiceId());
			bankTransferAccount.setTransferAccountType(Integer
					.parseInt(acctType));
			bankTransferAccount.setTransferAccountCategoryId(1l);
		// JH - Do we need to do this? // bankTransferAccount.setIsVerified(0);
			System.out.println("Adding transfer account: " + bankTransferAccount);
			addTransferAccount(userContext, bankTransferAccount, itemId);

		} else {
			System.out.println("\n Both the Account Numbers do not match...");
			return;
		}

	}

	/**.
	 * Adds Brokerage Account. This method will be called either to directly add
	 * a new brokerage account or to enable existing item for Funds transfer. In
	 * case of adding totally new transfer account it will prompt user for
	 * following 1. Sum Info - if the input sumInfo is null. In case, this
	 * method is called to enable existing item, this values is sent as
	 * parameter to this method.
	 * <p>
	 * User is prompted for Following parameters in each of the above case
	 * Account Type (Savings / Checking) Account Number. Re enter Account
	 * Number.
	 *
	 * @param userContext
	 *            The user context of the caller
	 * @param sumInfoID
	 *            Sum Info ID
	 */
	public void addBrokerageAccount(UserContext userContext, String sumInfoID, String itemId) {
		// prompt Sum Info ID if null
		if (sumInfoID == null)
			sumInfoID = IOUtils.promptInput(
					IFileTransferConstants.SumInfoIDPrompt,
					IFileTransferConstants.SumInfoIDReEnterPrompt);

		// get brokerage account data for given sum Info ID
		List brokerageList = null;
		try {
			brokerageList = Arrays.asList(transferAccountManagement
					.getBrokerageMetaData(getCobrandContext()).getElements());
		} catch (StaleConversationCredentialsExceptionFault scce) {
			System.out.println("\n Stale Conversation Credentials Exception....");
			System.out.println(scce.toString());
		} catch (InvalidConversationCredentialsExceptionFault ic) {
			System.out.println("Invalid Conversation Credentials Exception");
			System.out.println(ic.toString());
		} catch (InvalidCobrandConversationCredentialsExceptionFault iccce) {
			System.out.println("Invalid Cobrand Conversation Credentials Exception");
			System.out.println(iccce.toString());
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
		
		BrokerageMetaDataBean brokerMetaDataBean = null;
		Iterator brokerageListIterator = brokerageList.iterator();
		boolean loop = true;
		while (loop && brokerageListIterator.hasNext()) {
			brokerMetaDataBean = (BrokerageMetaDataBean) brokerageListIterator
					.next();
			if (brokerMetaDataBean.getSumInfoId() == Long.parseLong(sumInfoID)){
				loop = false;
			}
			else{
				brokerMetaDataBean = null;
			System.out.println("It is Not a Brokerage Account");
			return;
			}
		}
		String accountNumber = IOUtils.promptInput(
				IFileTransferConstants.AccountNumberPrompt,
				IFileTransferConstants.AccountNumberReEnterPrompt);
		String reTypeAccountNumber = IOUtils.promptInput(
				IFileTransferConstants.ReAccountNumberEnterPrompt,
				IFileTransferConstants.ReAccountNumberReEnterPrompt);

		if (accountNumber.equals(reTypeAccountNumber)) {
			if (brokerMetaDataBean.getBrokTransferMetaDataId() == FIDELITY_BROK_METADATA_ID)
				accountNumber = FTHelper
						.getFidelityBrokAccountNumber(accountNumber);
			long prefix = 0;
			try {
				prefix =(new Long(brokerMetaDataBean.getBrokAcctNoPrefix())).longValue();
			} catch (Exception e) {				
			}
			if (prefix != 0)
				accountNumber = prefix + accountNumber;

			BrokerageTransferAccount brokerageTransAcct = getBrokerageTransferAccount(
					brokerMetaDataBean, accountNumber);
			addTransferAccount(userContext, brokerageTransAcct, itemId);
		}

	}

	/**
	 * Adds Banking /Brokerage Account. This method prompts user to enter
	 * Account Category, and calls <code>addBankAccount</code> or
	 * <code>addBrokerageAccount</code> method depending upon user input.
	 *
	 * @param userContext
	 *            The user context of the caller
	 */
	public void addAccount(UserContext userContext) {
		int acctCategory;
		// prompt user for if null
		acctCategory = Integer.parseInt(IOUtils.promptInput(
				IFileTransferConstants.AccountCategoryPrompt,
				IFileTransferConstants.AccountCategoryReEnterPrompt));
		try {
			if (acctCategory == 1)
				addBankAccount(userContext, null, null);
			else if (acctCategory == 2)
				addBrokerageAccount(userContext, null, null);
			else
				System.out.println("Invalid Input");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Adds a transfer account represented by TransferAccount object. It should
	 * be either a BankTransferAccount or a BrokerageTransferAccount
	 *
	 * @param userContext
	 *            The userContext of the caller
	 * @param transferAcct
	 *            Encapsulated details of the account to add
	 */
	public void addTransferAccount(UserContext userContext,
			TransferAccount transferAcct, String itemId) {

		try {
			TransferAccount ta = transferAcct;
			if (itemId == null) {
				ta = transferAccountManagement.addTransferAccount(userContext, transferAcct);
			} else {
				long itemIdLongValue = Long.parseLong(itemId);
				transferAcct.setItemId(itemIdLongValue);
				ta = transferAccountManagement.addTransferAccountForItem(userContext,transferAcct, itemIdLongValue);
			}
			System.out.println("Account Added Successfully - transfer account id: " + ta.getTransferAccountId());
		} catch (InvalidUserContextExceptionFault iu) {
			System.out.println("Invalid User Context");
			System.out.println(iu.toString());
		} catch (InvalidConversationCredentialsExceptionFault ic) {

			System.out.println("Invalid Conversation Credentials Exception");
			System.out.println(ic.toString());
		} catch (TransferAccountAlreadyExistsExceptionFault ta) {
			// ta.printStackTrace();
			System.out.println("This transfer Account already exists ");
			System.out.println(ta.toString());
		} catch (TransferAccountNicknameInUseExceptionFault tan) {
			System.out
					.println("This transfer Account Nick Name already exists for some other account");
			System.out.println(tan.toString());
		} catch (InvalidAccountNumberExceptionFault invalidAcc) {
			System.out.println("This transfer Account is Invalid");
			System.out.println(invalidAcc.toString());
		} catch (IllegalArgumentValueExceptionFault illegalValue) {
			System.out.println("This transfer Account entered is illegal");
			System.out.println(illegalValue.toString());
			illegalValue.printStackTrace();
		}catch (RemoteException re) {
			System.out.println("\n Remote Exception....");
			System.out.println(re.toString());
			re.printStackTrace();
		}
	}
	
	
	public void updateTransferAcct(UserContext userContext) {
		// prompt user for transfer account ID
		long transferAccountID = Long.parseLong(IOUtils.promptInput(
				IFileTransferConstants.TransAcctID2Prompt,
				IFileTransferConstants.TransAcctID2ReEnterPrompt));
        TransferAccount transferAccountUpdated = null;
        String banknickname= null;
		String bankdescription = null;
		
        try {
        	transferAccountUpdated = transferAccountManagement.getTransferAccount(userContext,
                    transferAccountID);
        	
        	System.out.println("Enter the items you want to edit");
			System.out.println("*********************************");
			System.out.println(NAV_BANK_NICK_NAME + ". Nickname");
			System.out.println(NAV_BANK_DESCRIPTION + ". Description");
			System.out.println(NAV_ALL + ". All");
			System.out.println("**********************************");
			System.out.print("Choice: " );   		
			int number = IOUtils.readInt();
			
			if (!(number == NAV_BANK_NICK_NAME||number == NAV_BANK_DESCRIPTION||number == NAV_ALL)){
				System.out.println("Enter a valid number from 1-3");
				return;
			}             		 
			if(number == NAV_BANK_NICK_NAME || number == NAV_ALL){
				System.out.print("Enter the account nick name: ");
				banknickname = IOUtils.readStr();
				transferAccountUpdated.setNickname(banknickname);
			}
			if(number == NAV_BANK_DESCRIPTION || number == NAV_ALL){
				System.out.print("Enter the Bank Name: ");
				bankdescription = IOUtils.readStr();
				if (transferAccountUpdated instanceof BankTransferAccount){
					((BankTransferAccount)transferAccountUpdated).setBankName(bankdescription);
				}
			}
			
			transferAccountManagement.updateTransferAccount(userContext, transferAccountUpdated);
		} catch (InvalidUserContextExceptionFault iu) {
			System.out.println("Invalid User Context");
			System.out.println(iu.toString());
		} catch (InvalidConversationCredentialsExceptionFault ic) {
			System.out.println("Invalid Conversation Credentials Exception");
			System.out.println(ic.toString());
		} catch (TransferAccountNicknameInUseExceptionFault tan) {
			System.out
					.println("This transfer Account Nick Name already exists for some other account");
			System.out.println(tan.toString());
		} catch (TransferAccountNotFoundExceptionFault tanf) {
			System.out.println("This transfer Account not found");
			System.out.println(tanf.toString());
		} catch (IllegalArgumentValueExceptionFault illegalValue) {
			System.out.println("This transfer Account entered is illegal");
			System.out.println(illegalValue.toString());
			illegalValue.printStackTrace();
		}catch (RemoteException re) {
			System.out.println("\n Remote Exception....");
			System.out.println(re.toString());
		}
	}

	/**
	 * Enables the Item for Funds transfer. 1. Retrieves the Content Service Id
	 * for the input Item Id. If the CSId is of Type Bank then i. Retrieves the
	 * Content Service Name (Bank Name) for the input Item Id. ii. Retrieves the
	 * Routing Numbers for the input Item Id. iii.Prompts the user to Enter the
	 * Account Number. iv. Validates the two Account Numbers to be identical. v.
	 * Enables the Item Account. If the CSId is of Type STOCKS then i. Prompts
	 * the Input User for Account Number. ii. Validates the two Account Numbers
	 * to be identical. iii.Enables the Item Account.
	 *
	 * @param userContext
	 *            The userContext of the caller.
	 */
	public void enableTransferAccountForItem(UserContext userContext) {

		// Prompt for the Item Account to be Enabled.
		String itemId = IOUtils.promptInput(
				IFileTransferConstants.ItemIdPrompt,
				IFileTransferConstants.ReItemIdPrompt);

		// Get the Content service for the ItemId.
		ItemSummary itemSummary = null;
		try {
			itemSummary = dataService.getItemSummaryForItem(
					userContext,new Long(itemId));
		} catch (StaleConversationCredentialsExceptionFault scce) {
			System.out.println("\n Stale Conversation Credentials Exception....");
			System.out.println(scce.toString());
		}  catch (InvalidConversationCredentialsExceptionFault ic) {
			System.out.println("Invalid Conversation Credentials Exception");
			System.out.println(ic.toString());
		} catch (CoreExceptionFault ce) {
			System.out.println("\n Core Exception....");
			System.out.println(ce.toString());
		} catch (InvalidUserContextExceptionFault iuce) {
			System.out.println("Invalid User Context Exception");
			System.out.println(iuce.toString());
		} catch (IllegalArgumentValueExceptionFault value) {
			System.out.println("Given Transfer Account ID is invalid");
			System.out.println(value.toString());
		} catch (InvalidItemExceptionFault ite) {
			System.out.println("\n Invalid Item Exception....");
			System.out.println(ite.toString());
		} catch (RemoteException re) {
			System.out.println("\n Remote Exception....");
			System.out.println(re.toString());
		}

		// Get ContentServiceInfo to figure out its a Bank account or brokerage
		// account
		if (null != itemSummary) {
			ContentServiceInfo csinfo = itemSummary.getContentServiceInfo();
			String sumInfoId = new Long(itemSummary.getContentServiceId())
					.toString();
			String bankName = csinfo.getContentServiceDisplayName();

			String containerType = csinfo.getContainerInfo().getContainerName();

			ArrayOfRoutingNumberInfo arrayOfRoutingNumberInfo = null;
			RoutingNumberInfo[] rnis = null;
			try {
				arrayOfRoutingNumberInfo = rns
						.getRoutingNumberInfosByContentServiceId(getCobrandContext(),
								Long.parseLong(sumInfoId));
				if(arrayOfRoutingNumberInfo != null){
					rnis = arrayOfRoutingNumberInfo.getElements();
				}
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
			} catch (NumberFormatException nfe) {
				System.out.println("Number Format Exception ....");
				System.out.println(nfe.toString());
			} catch (RemoteException re) {
				System.out.println("\n Remote Exception....");
				System.out.println(re.toString());
			} 

			// Enables bank account
			if (containerType.equalsIgnoreCase("BANK")
					|| containerType.equalsIgnoreCase("INVESTMENTS")) {

				// addBankAccount(userContext, bankName,
				// rnis[0].getRoutingNumber(), null);
				System.out.println("Choose from following routing numbers for	"
						+ bankName);

				/*
				 * Following are the different routing numbers for the given
				 * bank which user may choose from
				 */
				for (int i = 0; i < rnis.length; i++)
					System.out.println("rnis[" + i + "].getRoutingNumber()	"
							+ rnis[i].getRoutingNumber());

				addBankAccount(userContext, bankName, itemId);

			}

			// enables brokerage account
			else if (containerType.equalsIgnoreCase("STOCKS")) {
				addBrokerageAccount(userContext, sumInfoId, itemId);

			}
		}
	}

	/**
	 * Displays all the transfer accounts associated to particular user.
	 *
	 * @param userContext
	 *            The userContext of the caller
	 */
	public void getAllTransferAccountsForUser(UserContext userContext) {
		ArrayOfTransferAccount arrayOfTransferAccount = null;
		TransferAccount[] transferAccts = null;
		try {
			arrayOfTransferAccount = transferAccountManagement
					.getAllTransferAccounts(userContext);
			if(arrayOfTransferAccount != null){
				transferAccts = arrayOfTransferAccount.getElements();
			}
		} catch (InvalidUserContextExceptionFault iu) {
			System.out.println("Invalid User Context");
			System.out.println(iu.toString());
		} catch (InvalidConversationCredentialsExceptionFault ic) {
			System.out.println("Invalid Conversation Credentials Exception");
			System.out.println(ic.toString());
		} catch (IllegalArgumentValueExceptionFault value) {
			System.out.println("Given Transfer Account ID is invalid");
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

		if (transferAccts == null) {
			System.out.println("User has no transfer accounts.");
		} else {
			for (int i = 0; i < transferAccts.length; i++) {
				displayTransferAccountDetails(transferAccts[i]);
			}
		}

	}

	/**
	 * Displays account details for the given transfer account.
	 *
	 * @param transferAcct
	 *            Transfer Account object for which details to be displayed
	 */
	public void displayTransferAccountDetails(TransferAccount transferAcct) {
		System.out.println("\n");
		System.out
				.println("************Account Details**************************");
		if (transferAcct instanceof BankTransferAccount) {
			
			System.out.println("Bank Name  " + ((BankTransferAccount)transferAcct).getBankName());
			System.out.println("DFI account Id: " + ((BankTransferAccount)transferAcct).getDfiAccount().getDfiAccountId());
		}
		
        if (transferAcct instanceof BrokerageTransferAccount) {
			
			System.out.println("Bank Name  " + ((BrokerageTransferAccount)transferAcct).getBankName());
			System.out.println("DFI account Id: " + ((BrokerageTransferAccount)transferAcct).getDFIAccount().getDfiAccountId());
		}

		System.out.println("Account Nick Name	" + transferAcct.getNickname());
		System.out.println("Transfer Account ID	"
				+ transferAcct.getTransferAccountId());
		
		System.out.println("Item ID			" + transferAcct.getItemId());

		System.out.println("Content Service(SUM INFO) ID	"
				+ transferAcct.getContentServiceId());

		if (transferAcct.getTransferAccountCategoryId().longValue() == 1)
			System.out.println("Account Category	BANKING");
		else if (transferAcct.getTransferAccountCategoryId().longValue() == 2)
			System.out.println("Account Category	BROKERAGE");
		else if (transferAcct.getTransferAccountCategoryId().longValue() == 2)
			System.out.println("Account Category UNKNOWN");
        
		if (transferAcct.getLockStatus() == 0)
			System.out.println("IS Account Locked 	NO");
		else
			System.out.println("IS Account Locked 	YES");

		
		 if (TransferAccount_VERIFIED == transferAcct.getIsVerified()) {
			 System.out.println("Is Account Verified YES");
		 }
		 else {
			 System.out.println("Is Account Verified NO");
		 }
		 
		 
	}

	/**
	 * Removes (disable)given account. This method will prompt user for transfer
	 * Account ID and will disable the same.
	 *
	 * @param userContext
	 *            The userContext of the caller
	 */
	public void removeTransferAcct(UserContext userContext) {
		// prompt user for transfer account ID
		long transferAccountID = Long.parseLong(IOUtils.promptInput(
				IFileTransferConstants.TransAcctID2Prompt,
				IFileTransferConstants.TransAcctID2ReEnterPrompt));
		try {
			transferAccountManagement.removeTransferAccount(userContext,
					transferAccountID);
			System.out.println("Removed Transfer account" + transferAccountID
					+ " successfully");
		} catch (InvalidUserContextExceptionFault iu) {
			System.out.println("Invalid User Context");
			System.out.println(iu.toString());
		} catch (InvalidConversationCredentialsExceptionFault ic) {
			System.out.println("Invalid Conversation Credentials Exception");
			System.out.println(ic.toString());
		} catch (TransferAccountNotFoundExceptionFault acct) {
			System.out.println("Invalid Transfer Account ID");
			System.out.println(acct.toString());
		}  catch (StaleConversationCredentialsExceptionFault scce) {
			System.out.println("\n Stale Conversation Credentials Exception....");
			System.out.println(scce.toString());
		} catch (CoreExceptionFault ce) {
			System.out.println("\n Core Exception....");
			System.out.println(ce.toString());
		}  catch (PendingTransfersExistsExceptionFault ptee) {
			System.out.println("Pending Transfer Exists Exception.....");
			System.out.println(ptee.toString());
		} catch (IllegalArgumentValueExceptionFault value) {
			System.out.println("Given Transfer Account ID is invalid");
			System.out.println(value.toString());
		} catch (RemoteException re) {
			System.out.println("\n Remote Exception....");
			System.out.println(re.toString());
		}

	}

	/**
	 * Links a transfer Account and Item. This method will make an association
	 * of transfer acct id and item id in transfer_acct table. This is mandatory
	 * step for IAV because IAV will fail if transfer account is not mapped to
	 * any item account.
	 *
	 * @param userContext
	 *            The userContext of the caller
	 * @return transferAccount The transfer account linked to the item
	 */
	public TransferAccount linkTransferAcctToItem(UserContext userContext, long transferAccountID) {

            System.out.println("Going to ask you to provide credentials to IAV this item");
            AddItem addItem = new AddItem();
            TransferAccount ta = null;
			try {
				ta = transferAccountManagement.getTransferAccount(userContext,
				        transferAccountID);
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
//    		Create ContentServiceTraversal Locator 
            ContentServiceTraversal contentServiceTraversal = null;
        	ContentServiceTraversalServiceLocator contentServiceTraversalServiceLocator = new ContentServiceTraversalServiceLocator();
    		String contentServiceTraversalServiceName = contentServiceTraversalServiceLocator.getContentServiceTraversalServiceWSDDServiceName();
    		contentServiceTraversalServiceLocator.setContentServiceTraversalServiceEndpointAddress(System.getProperty("com.yodlee.soap.services.url") 
    				+ "/" + contentServiceTraversalServiceName);
    		try{
    			contentServiceTraversal = contentServiceTraversalServiceLocator.getContentServiceTraversalService();
    		}catch(Exception lse) {

    			}
            
            ContentServiceInfo contentServiceInfo = null;
			try {
				contentServiceInfo = contentServiceTraversal.getContentServiceInfo(getCobrandContext(), ta.getContentServiceId());
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
			}  catch (RemoteException re) {
				System.out.println("\n Remote Exception....");
				System.out.println(re.toString());
			}

//    		Create ItemManagement Locator 
            ItemManagement itemManagement = null;
            ItemManagementServiceLocator itemManagementServiceLocator = new ItemManagementServiceLocator();
    		String itemManagementServiceName = itemManagementServiceLocator.getItemManagementServiceWSDDServiceName();
    		itemManagementServiceLocator.setItemManagementServiceEndpointAddress(System.getProperty("com.yodlee.soap.services.url") 
    				+ "/" + itemManagementServiceName);
    		try{
    			itemManagement = itemManagementServiceLocator.getItemManagementService();
    		}catch(Exception lse) {

    			}

            List formComponents = FormUtil.getUserInputFieldInfoList(userContext,
                    addItem.getLoginFormForContentService(userContext, contentServiceInfo.getContentServiceId()));

//    		Create ItemAccountManagement Locator 
            ItemAccountManagement iam = null;
            ItemAccountManagementServiceLocator itemAccountManagementServiceLocator = new ItemAccountManagementServiceLocator();
    		String itemAccountManagementServiceName = itemAccountManagementServiceLocator.getItemAccountManagementServiceWSDDServiceName();
    		itemAccountManagementServiceLocator.setItemAccountManagementServiceEndpointAddress(System.getProperty("com.yodlee.soap.services.url") 
    				+ "/" + itemAccountManagementServiceName);
    		try{
    			iam = itemAccountManagementServiceLocator.getItemAccountManagementService();
    		}catch(Exception lse) {

    			}
            
            try {
            	Long itemAccountID = new Long(ta.getItemAccountId());
            	com.yodlee.soap.collections.List list = new com.yodlee.soap.collections.List();
            	list.setElements(formComponents.toArray());
            	ItemDetails itemDetails = new ItemDetails();
            	itemDetails.setContentServiceId(contentServiceInfo.getContentServiceId());
            	itemDetails.setCredentialFields(list);
            	itemDetails.setShareCredentialsWithinSite(true);
            	itemDetails.setStartRefreshOnAddition(false);
            	iam.enableAccountForAggregation(userContext, itemAccountID, itemDetails);


		} catch (InvalidUserContextExceptionFault iu) {
			System.out.println("Invalid User Context");
			System.out.println(iu.toString());
		} catch (InvalidConversationCredentialsExceptionFault ic) {
			System.out.println("Invalid Conversation Credentials Exception");
			System.out.println(ic.toString());
		} catch (InvalidItemExceptionFault item) {
			System.out.println("Given Item Account ID is invalid");
			System.out.println(item.toString());
		} catch (IllegalArgumentValueExceptionFault value) {
			System.out.println("Given Transfer Account ID is invalid");
			System.out.println(value.toString());
    		} catch (AccountAlreadyEnabledExceptionFault enabledException){
    			System.out.println("Some other exception");
    			System.out.println(enabledException.toString());
    			try {
    				com.yodlee.soap.collections.List list = new com.yodlee.soap.collections.List();
    				list.setElements(formComponents.toArray());
    				itemManagement.updateCredentialsForItem1(userContext, ta.getItemId(), list, true);
					
				} catch (StaleConversationCredentialsExceptionFault scce) {
					System.out.println("\n Stale Conversation Credentials Exception....");
					System.out.println(scce.toString());
				} catch (InvalidConversationCredentialsExceptionFault ic) {
					System.out.println("Invalid Conversation Credentials Exception");
					System.out.println(ic.toString());
				} catch (CoreExceptionFault ce) {
					System.out.println("\n Core Exception....");
					System.out.println(ce.toString());
				} catch (IncompleteArgumentExceptionFault iae) {
					System.out.println("\n Incomplete Argument Exception....");
					System.out.println(iae.toString());
				} catch (IllegalArgumentTypeExceptionFault iate) {
					System.out.println("\n Illegal Argument Type Exception....");
					System.out.println(iate.toString());
				} catch (InvalidUserContextExceptionFault iuce) {
					System.out.println("Invalid User Context Exception");
					System.out.println(iuce.toString());
				} catch (IllegalArgumentValueExceptionFault value) {
					System.out.println("Given Transfer Account ID is invalid");
					System.out.println(value.toString());
				} catch (InvalidItemExceptionFault ite) {
					System.out.println("\n Invalid Item Exception....");
					System.out.println(ite.toString());
				} catch (RemoteException re) {
					System.out.println("\n Remote Exception....");
					System.out.println(re.toString());
				}
    		} catch (ContentServiceNotFoundExceptionFault csfne) {
				System.out.println("Content Service Not Found Exception");
				System.out.println(csfne.toString());
			} catch (StaleConversationCredentialsExceptionFault scce) {
				System.out.println("\n Stale Conversation Credentials Exception....");
				System.out.println(scce.toString());
			} catch (CoreExceptionFault ce) {
				System.out.println("\n Core Exception....");
				System.out.println(ce.toString());
			} catch (IncompleteArgumentExceptionFault iae) {
				System.out.println("\n Incomplete Argument Exception....");
				System.out.println(iae.toString());
			}  catch (InvalidItemAccountIdExceptionFault itae) {
				System.out.println("\n Invalid Item Account Exception....");
				System.out.println(itae.toString());
			} catch (IllegalArgumentTypeExceptionFault iate) {
				System.out.println("\n Illegal Argument Type Exception....");
				System.out.println(iate.toString());
			} catch (RemoteException re) {
				System.out.println("\n Remote Exception....");
				System.out.println(re.toString());
			}

        return ta;
	}

	public TransferAccount getTransferAcct(UserContext userContext, long transferAccountID)
	{
       // AddItem addItem = new AddItem();
        TransferAccount ta = null;
		try {
			ta = transferAccountManagement.getTransferAccount(userContext,
			        transferAccountID);
		} catch (StaleConversationCredentialsExceptionFault scce) {
			System.out.println("\n Stale Conversation Credentials Exception....");
			System.out.println(scce.toString());
		} catch (InvalidConversationCredentialsExceptionFault ic) {
			System.out.println("Invalid Conversation Credentials Exception");
			System.out.println(ic.toString());
		} catch (CoreExceptionFault ce) {
			System.out.println("\n Core Exception....");
			System.out.println(ce.toString());
		} catch (TransferAccountNotFoundExceptionFault tane) {
			System.out.println("Transfer Account Not Found Exception");
			System.out.println(tane.toString());
		} catch (InvalidUserContextExceptionFault iuce) {
			System.out.println("Invalid User Context Exception");
			System.out.println(iuce.toString());
		} catch (IllegalArgumentValueExceptionFault value) {
			System.out.println("Given Transfer Account ID is invalid");
			System.out.println(value.toString());
		}  catch (RemoteException re) {
			System.out.println("\n Remote Exception....");
			System.out.println(re.toString());
		}
        return ta;
		
	}
	
}
