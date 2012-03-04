package YodleeSrc;

import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.xml.rpc.ServiceException;

import com.yodlee.soap.collections.core.fundstransfer.transferaccountmanagement.ArrayOfTransferAccount;
import com.yodlee.soap.collections.core.routingnumberservice.ArrayOfRoutingNumberInfo;
import com.yodlee.soap.common.CobrandContext;
import com.yodlee.soap.common.ContentServiceInfo;
import com.yodlee.soap.common.Form;
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
import com.yodlee.soap.core.dataservice.dataservice.DataService;
import com.yodlee.soap.core.dataservice.dataservice.DataServiceServiceLocator;
import com.yodlee.soap.core.fundstransfer.transferaccountmanagement.BankTransferAccount;
import com.yodlee.soap.core.fundstransfer.transferaccountmanagement.BrokerageMetaDataBean;
import com.yodlee.soap.core.fundstransfer.transferaccountmanagement.BrokerageTransferAccount;
import com.yodlee.soap.core.fundstransfer.transferaccountmanagement.DFIAccount;
import com.yodlee.soap.core.fundstransfer.transferaccountmanagement.InvalidAccountNumberExceptionFault;
import com.yodlee.soap.core.fundstransfer.transferaccountmanagement.TransferAccount;
import com.yodlee.soap.core.fundstransfer.transferaccountmanagement.TransferAccountAlreadyExistsExceptionFault;
import com.yodlee.soap.core.fundstransfer.transferaccountmanagement.TransferAccountNicknameInUseExceptionFault;
import com.yodlee.soap.core.fundstransfer.transferaccountmanagement.TransferAccountNotFoundExceptionFault;
import com.yodlee.soap.core.fundstransfer.transferaccountmanagement.transferaccountmanagement.TransferAccountManagement;
import com.yodlee.soap.core.fundstransfer.transferaccountmanagement.transferaccountmanagement.TransferAccountManagementServiceLocator;
import com.yodlee.soap.core.fundstransfer.transfermanagement.PendingTransfersExistsExceptionFault;
import com.yodlee.soap.core.itemaccountmanagement.ItemDetails;
import com.yodlee.soap.core.itemaccountmanagement.exceptions.AccountAlreadyEnabledExceptionFault;
import com.yodlee.soap.core.itemaccountmanagement.itemaccountmanagement.ItemAccountManagement;
import com.yodlee.soap.core.itemaccountmanagement.itemaccountmanagement.ItemAccountManagementServiceLocator;
import com.yodlee.soap.core.routingnumberservice.InvalidRoutingNumberExceptionFault;
import com.yodlee.soap.core.routingnumberservice.RoutingNumberInfo;
import com.yodlee.soap.core.routingnumberservice.RoutingNumberNotFoundExceptionFault;
import com.yodlee.soap.core.routingnumberservice.routingnumberservice.RoutingNumberService;
import com.yodlee.soap.core.routingnumberservice.routingnumberservice.RoutingNumberServiceServiceLocator;
import com.yodlee.soap.ext.traversal.contentservicetraversal.ContentServiceTraversal;
import com.yodlee.soap.ext.traversal.contentservicetraversal.ContentServiceTraversalServiceLocator;

public class FTAccountManagement {

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
	 * Meta data Id for Fidelity Brokerage Account Required for checking if
	 * Brokerage account is Fidelity Brokerage Account If account number begins
	 * with X, Y or Z, putin this space; all else, put followed by 9
	 * digit Fidelity Brokerage account number
	 */
	public static int FIDELITY_BROK_METADATA_ID = 4;
	
	protected RoutingNumberService rns;
	
	protected TransferAccountManagement transferAccountManagement;
	
	protected DataService dataService;
	
	public FTAccountManagement(){
		RoutingNumberServiceServiceLocator routingNumberServiceServiceLocator = new RoutingNumberServiceServiceLocator();
		String routingNumberServiceServiceName = routingNumberServiceServiceLocator.getRoutingNumberServiceWSDDServiceName();
		routingNumberServiceServiceLocator.setRoutingNumberServiceEndpointAddress(System.getProperty("com.yodlee.soap.services.url") 
				+ "/" + routingNumberServiceServiceName);
		try{
			rns = routingNumberServiceServiceLocator.getRoutingNumberService();
		} catch(Exception lse) {
			lse.printStackTrace();
		}
		
		TransferAccountManagementServiceLocator transferAccountManagementServiceLocator = new TransferAccountManagementServiceLocator();
		String transferAccountManagementServiceName = transferAccountManagementServiceLocator.getTransferAccountManagementServiceWSDDServiceName();
		transferAccountManagementServiceLocator.setTransferAccountManagementServiceEndpointAddress(System.getProperty("com.yodlee.soap.services.url") 
				+ "/" + transferAccountManagementServiceName);
		try{
			transferAccountManagement = transferAccountManagementServiceLocator.getTransferAccountManagementService();
		} catch(Exception lse) {
			lse.printStackTrace();
		}
		
		DataServiceServiceLocator dataServiceServiceLocator = new DataServiceServiceLocator();
		String dataServiceServiceName = dataServiceServiceLocator.getDataServiceWSDDServiceName();
		dataServiceServiceLocator.setDataServiceEndpointAddress(System.getProperty("com.yodlee.soap.services.url") 
				+ "/" + dataServiceServiceName);
		try{
			dataService = dataServiceServiceLocator.getDataService();
		} catch(Exception lse) {
			lse.printStackTrace();
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
	public void addAccount(UserContext userContext, CobrandContext cobrandContext) {
		int acctCategory;
		// prompt user for if null
		acctCategory = Integer.parseInt(IOUtils.promptInput(
				IFileTransferConstants.AccountCategoryPrompt, 
				IFileTransferConstants.AccountCategoryReEnterPrompt));
		try {
			if (acctCategory == 1)
				addBankAccount(userContext, null, null);
			else if (acctCategory == 2)
				addBrokerageAccount(userContext, cobrandContext, null, null);
			else
				System.out.println("Invalid Input");
		} catch (Exception e) {
			e.printStackTrace();
		}
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
			bankTransferAccount.setTransferAccountType(new Integer(acctType));
			bankTransferAccount.setTransferAccountCategoryId(new Long(1));
			System.out.println("Adding transfer account...");
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
	public void addBrokerageAccount(UserContext userContext, CobrandContext cobrandContext, String sumInfoID, String itemId) {
		// prompt Sum Info ID if null
		if (sumInfoID == null)
			sumInfoID = IOUtils.promptInput(
					IFileTransferConstants.SumInfoIDPrompt,
					IFileTransferConstants.SumInfoIDReEnterPrompt);

		// get brokerage account data for given sum Info ID
		List brokerageList = null;
		try {
			brokerageList = Arrays.asList(transferAccountManagement
					.getBrokerageMetaData(cobrandContext).getElements());
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
			if (brokerMetaDataBean.getSumInfoId().longValue() == Long.parseLong(sumInfoID)){
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
			if (brokerMetaDataBean.getBrokTransferMetaDataId().intValue() == FIDELITY_BROK_METADATA_ID)
				accountNumber = getFidelityBrokAccountNumber(accountNumber);
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
	 * Returns a Bank Transfer Account given the following info.
	 * @param bankName - The Bank Name
	 * @param routingNumber - Routing Number
	 * @param accountNumber - Account Number
	 * @param accountType - Account Type
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
		bankTransferAccount.setTransferAccountType(new Integer(accountType));
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
				transferAcct.setItemId(new Long(itemIdLongValue));
				ta = transferAccountManagement.addTransferAccountForItem(userContext,transferAcct, new Long(itemIdLongValue));
			}
			System.out.println("Account Added Successfully - transfer account id: " + ta.getTransferAccountId());
		} catch (InvalidUserContextExceptionFault iu) {
			System.out.println("Invalid User Context");
			System.out.println(iu.toString());
		} catch (InvalidConversationCredentialsExceptionFault ic) {
			System.out.println("Invalid Conversation Credentials Exception");
			System.out.println(ic.toString());
		} catch (TransferAccountAlreadyExistsExceptionFault ta) {
			System.out.println("Exception: This transfer Account already exists ");
			//System.out.println(ta.toString());
		} catch (TransferAccountNicknameInUseExceptionFault tan) {
			System.out.println("This transfer Account Nick Name already exists for some other account");
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
                    new Long(transferAccountID));
        	
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
	public void enableTransferAccountForItem(UserContext userContext, CobrandContext cobrandContext) {

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
			String sumInfoId = itemSummary.getContentServiceId().toString();
			String bankName = csinfo.getContentServiceDisplayName();

			String containerType = csinfo.getContainerInfo().getContainerName();

			ArrayOfRoutingNumberInfo arrayOfRoutingNumberInfo = null;
			RoutingNumberInfo[] rnis = null;
			try {
				arrayOfRoutingNumberInfo = rns
						.getRoutingNumberInfosByContentServiceId(cobrandContext,
								new Long(sumInfoId));
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
				System.out.println("Choose from following routing numbers for "
						+ bankName);

				/*
				 * Following are the different routing numbers for the given
				 * bank which user may choose from
				 */
				for (int i = 0; i < rnis.length; i++)
					System.out.println( ( i + 1 ) + ". "
							+ rnis[i].getRoutingNumber());

				System.out.println("");
				addBankAccount(userContext, bankName, itemId);

			}

			// enables brokerage account
			else if (containerType.equalsIgnoreCase("STOCKS")) {
				addBrokerageAccount(userContext, cobrandContext, sumInfoId, itemId);

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
            //System.out.println("Balance: " + ((BankTransferAccount)transferAcct).getDfiAccount().getDfiAccountId());
		}
		
        if (transferAcct instanceof BrokerageTransferAccount) {
			
			System.out.println("Bank Name  " + ((BrokerageTransferAccount)transferAcct).getBankName());
			System.out.println("DFI account Id: " + ((BrokerageTransferAccount)transferAcct).getDFIAccount().getDfiAccountId());
		}

		System.out.println("Account Nick Name	" + transferAcct.getNickname());
        System.out.println("Account Balance	" + transferAcct.getAccountBalance());
		System.out.println("Transfer Account ID	"
				+ transferAcct.getTransferAccountId());
		
		System.out.println("Item ID " + transferAcct.getItemId());

		System.out.println("Content Service(SUM INFO) ID	"
				+ transferAcct.getContentServiceId());

		if (transferAcct.getTransferAccountCategoryId().longValue() == 1)
			System.out.println("Account Category	BANKING");
		else if (transferAcct.getTransferAccountCategoryId().longValue() == 2)
			System.out.println("Account Category	BROKERAGE");
		else if (transferAcct.getTransferAccountCategoryId().longValue() == 2)
			System.out.println("Account Category UNKNOWN");
        
		if (transferAcct.getLockStatus().intValue() == 0)
			System.out.println("IS Account Locked 	NO");
		else
			System.out.println("IS Account Locked 	YES");
		
		 if (TransferAccount_VERIFIED == transferAcct.getIsVerified().intValue()) {
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
					new Long(transferAccountID));
			System.out.println("Removed Transfer account " + transferAccountID
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
	public TransferAccount linkTransferAcctToItem(UserContext userContext, long transferAccountID, CobrandContext cobrandContext) {
            System.out.println("Going to ask you to provide credentials to IAV this item");
            TransferAccount ta = null;
			try {
				ta = transferAccountManagement.getTransferAccount(userContext,
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
 
            ContentServiceTraversal contentServiceTraversal = null;
        	ContentServiceTraversalServiceLocator contentServiceTraversalServiceLocator = new ContentServiceTraversalServiceLocator();
    		String contentServiceTraversalServiceName = contentServiceTraversalServiceLocator.getContentServiceTraversalServiceWSDDServiceName();
    		contentServiceTraversalServiceLocator.setContentServiceTraversalServiceEndpointAddress(System.getProperty("com.yodlee.soap.services.url") 
    				+ "/" + contentServiceTraversalServiceName);
    		try{
    			contentServiceTraversal = contentServiceTraversalServiceLocator.getContentServiceTraversalService();
    		} catch(Exception lse) {
    		}
            
            ContentServiceInfo contentServiceInfo = null;
			try {
				contentServiceInfo = contentServiceTraversal.getContentServiceInfo(cobrandContext, ta.getContentServiceId());
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
                    getLoginFormForContentService(userContext, contentServiceInfo.getContentServiceId().longValue()));

//    		Create ItemAccountManagement Locator 
            ItemAccountManagement iam = null;
            ItemAccountManagementServiceLocator itemAccountManagementServiceLocator = new ItemAccountManagementServiceLocator();
    		String itemAccountManagementServiceName = itemAccountManagementServiceLocator.getItemAccountManagementServiceWSDDServiceName();
    		itemAccountManagementServiceLocator.setItemAccountManagementServiceEndpointAddress(System.getProperty("com.yodlee.soap.services.url") 
    				+ "/" + itemAccountManagementServiceName);
    		try{
    			iam = itemAccountManagementServiceLocator.getItemAccountManagementService();
    		} catch(Exception lse) {

    		}
            
         try {
            	Long itemAccountID = ta.getItemAccountId();
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
    			System.out.println("This account has already been enabled");
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
	
	 /**
     * Get Login Form For Conent Service
     * @param userContext
     * @param csId
     * @return Form
     */
    public Form getLoginFormForContentService(UserContext userContext, long csId) {
    	ItemManagement itemManagement = null;
    	try {
	    	ItemManagementServiceLocator locator = new ItemManagementServiceLocator();
	        String serviceName = locator.getItemManagementServiceWSDDServiceName();
	        locator.setItemManagementServiceEndpointAddress(System.getProperty("com.yodlee.soap.services.url")
						+ "/" + serviceName);
	        itemManagement = locator.getItemManagementService();
        	return itemManagement.getLoginFormForContentService(userContext, new Long(csId));
        } catch (Exception e) {
        	throw new RuntimeException("Error fetching login form for this CsId");
        }
    }

}
