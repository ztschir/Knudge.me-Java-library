package com.KnudgeMeYodlee;
import YodleeSrc.AccountSummary;
import YodleeSrc.ContainerTypesHelper;
import YodleeSrc.ContentServiceManagement;
import YodleeSrc.DisplayBillsData;
import YodleeSrc.DisplayCardData;
import YodleeSrc.DisplayInsuranceData;
import YodleeSrc.DisplayInvestmentData;
import YodleeSrc.DisplayItemInfo;
import YodleeSrc.DisplayLoanData;
import YodleeSrc.FormUtil;
import YodleeSrc.Formatter;
import YodleeSrc.IOUtils;
import YodleeSrc.RefreshHelper;
import YodleeSrc.RefreshItem;

import com.yodlee.soap.core.login.cobrandlogin.CobrandLogin;
import com.yodlee.soap.core.login.cobrandlogin.CobrandLoginServiceLocator;
import com.yodlee.soap.core.login.login.Login;
import com.yodlee.soap.core.login.login.LoginServiceLocator;
import com.yodlee.soap.core.login.InvalidCobrandCredentialsExceptionFault;
import com.yodlee.soap.core.login.CobrandUserAccountLockedExceptionFault;
import com.yodlee.soap.core.login.UserInfo;
import com.yodlee.soap.core.login.InvalidUserCredentialsExceptionFault;
import com.yodlee.soap.core.usermanagement.userregistration.UserRegistration;
import com.yodlee.soap.core.usermanagement.userregistration.UserRegistrationServiceLocator;
import com.yodlee.soap.core.usermanagement.UserProfile;
import com.yodlee.soap.core.usermanagement.UserNameExistsExceptionFault;
import com.yodlee.soap.core.accountmanagement.itemmanagement.ItemManagementServiceLocator;
import com.yodlee.soap.core.accountmanagement.itemmanagement.ItemManagement;
import com.yodlee.soap.core.verification.instantverificationdataservice.InstantVerificationDataServiceServiceLocator;
import com.yodlee.soap.core.verification.instantverificationdataservice.InstantVerificationDataService;
import com.yodlee.soap.core.verification.extendedinstantverificationdataservice.ExtendedInstantVerificationDataServiceServiceLocator;
import com.yodlee.soap.core.verification.extendedinstantverificationdataservice.ExtendedInstantVerificationDataService;
import com.yodlee.soap.core.verification.IAVRefreshStatus;
import com.yodlee.soap.core.routingnumberservice.routingnumberservice.RoutingNumberServiceServiceLocator;
import com.yodlee.soap.core.routingnumberservice.routingnumberservice.RoutingNumberService;
import com.yodlee.soap.core.refresh.refresh.RefreshServiceLocator;
import com.yodlee.soap.core.refresh.refresh.Refresh;
import com.yodlee.soap.core.refresh.RefreshParameters;
import com.yodlee.soap.core.refresh.RefreshMode;
import com.yodlee.soap.core.refresh.RefreshStatus;
import com.yodlee.soap.core.CoreExceptionFault;
import com.yodlee.soap.core.IllegalArgumentValueExceptionFault;
import com.yodlee.soap.core.InvalidCobrandContextExceptionFault;
import com.yodlee.soap.core.InvalidCobrandConversationCredentialsExceptionFault;
import com.yodlee.soap.core.InvalidConversationCredentialsExceptionFault;
import com.yodlee.soap.core.StaleConversationCredentialsExceptionFault;
import com.yodlee.soap.core.UnsupportedCoreOperationExceptionFault;
import com.yodlee.soap.core.InvalidItemExceptionFault;
import com.yodlee.soap.core.mfarefresh.*;
import com.yodlee.soap.core.search.search.SearchServiceLocator;
import com.yodlee.soap.core.search.search.Search;
import com.yodlee.soap.core.transactioncategorization.transactioncategorizationservice.TransactionCategorizationServiceServiceLocator;
import com.yodlee.soap.core.transactioncategorization.transactioncategorizationservice.TransactionCategorizationService;
import com.yodlee.soap.core.transactioncategorization.*;
import com.yodlee.soap.core.transactionsearch.transactionsearchservice.TransactionSearchServiceServiceLocator;
import com.yodlee.soap.core.transactionsearch.transactionsearchservice.TransactionSearchService;
import com.yodlee.soap.core.transactionsearch.*;
import com.yodlee.soap.core.dataservice.ItemData;
import com.yodlee.soap.core.dataservice.DataExtent;
import com.yodlee.soap.core.dataservice.AccountHistory;
import com.yodlee.soap.core.dataservice.dataservice.DataServiceServiceLocator;
import com.yodlee.soap.core.dataservice.dataservice.DataService;
import com.yodlee.soap.core.dataservice.types.*;
import com.yodlee.soap.common.*;
import com.yodlee.soap.common.Category;
import com.yodlee.soap.ext.traversal.contentservicetraversal.ContentServiceTraversal;
import com.yodlee.soap.ext.traversal.contentservicetraversal.ContentServiceTraversalServiceLocator;
import com.yodlee.soap.ext.login.CobrandPasswordCredentials;
import com.yodlee.soap.ext.login.PasswordCredentials;
import com.yodlee.soap.collections.Entry;
import com.yodlee.soap.collections.ArrayOflong;
import com.yodlee.soap.collections.Map;
import com.yodlee.soap.collections.common.ArrayOfContentServiceInfo;
import com.yodlee.soap.collections.common.ArrayOfRefreshInfo;
import com.yodlee.soap.collections.core.transactionsearch.ArrayOfTransactionView;
import com.yodlee.soap.collections.core.transactioncategorization.ArrayOfCategory;
import com.yodlee.soap.collections.core.mfarefresh.ArrayOfQuestionAndAnswerValues;
import com.yodlee.soap.collections.core.mfarefresh.ArrayOfQuesAndAnswerDetails;

import javax.xml.rpc.ServiceException;

import java.rmi.RemoteException;
import java.util.Locale;
import java.util.Calendar;
import java.util.List;
import java.util.Date;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/* Aggregation Process flow
 Cobrand Login
 User Registration
 User Login
 Add Item (Edit and Remove)
 Refresh Item
 Get Account Data
 get transaction data
 get user categories
 add sub category
 view item
 edit item
 remove item
 */

public class YodleeAggregation {
	/** Stores the Cobrand Login Proxy so it does not need to be recreated. */
	private CobrandLogin cobrandLoginProxy;

	/**
	 * Represents the category level user created sub category
	 */
	public static final Long SUB_CATEGORY = new Long(4);

	/** Stores how long the Context is good before it needs to be renewed. */
	private long COBRAND_CONTEXT_TIME_OUT = 3 * 60 * 1000; // Time Milliseconds
															// to create a new
															// CobrandContext

	/** Stores when the context was created. */
	private long created = 0;

	/** Stores the cobrand context. */
	private CobrandContext cobrandContext;

	/** Cobrand ID is the Unique ID for partner. */
	private long cobrandId = 10000556;

	/** App ID is a unique application identifier. */
	private String appId = "499D28D0A09754A9FF5285C084454E00";

	/** Login Name for the Cobrand. */
	private String cobrandLogin = "SdkEval";

	/** Login Password for the Cobrand. */
	private String cobrandPassword = "EvaR@StaG81";

	protected static int SLEEP_MILLIS = 10 * 1000;
	public static long REFRESH_TIMEOUT_MIILIS = 5 * 60 * 1000; // 5 minutes
	protected static int MFA_QUEUE_WAIT_TIME_MILLIS = 20 * 1000;
	/**
	 * Success
	 */
	public static final int GATHERER_ERRORS_STATUS_OK = 0;

	protected UserRegistration userRegistration;

	protected Login login;

	protected ContentServiceTraversal cst;

	protected DataService dataService;

	protected TransactionSearchService transactionSearchService;

	protected TransactionCategorizationService tcService;

	protected static ItemManagement itemManagement;

	protected IAVRefreshStatus iavrs;

	protected ContentServiceTraversal contentServiceTraversal;

	protected Refresh refresh;

	protected ExtendedInstantVerificationDataService mfaiavds;

	protected Search search;

	public YodleeAggregation() {

		// Create Cobrand Login Service
		CobrandLoginServiceLocator cobrandLoginServicelocator = new CobrandLoginServiceLocator();
		String cobrandLoginServiceName = cobrandLoginServicelocator
				.getCobrandLoginServiceWSDDServiceName();
		cobrandLoginServicelocator.setCobrandLoginServiceEndpointAddress(System
				.getProperty("com.yodlee.soap.services.url")
				+ "/"
				+ cobrandLoginServiceName);
		try {
			cobrandLoginProxy = cobrandLoginServicelocator
					.getCobrandLoginService();
		} catch (ServiceException se) {
			se.printStackTrace();
			throw new RuntimeException(se.getMessage());
		}

		// Create User Registration Service
		UserRegistrationServiceLocator userRegistrationlocator = new UserRegistrationServiceLocator();
		String userRegistrationServiceName = userRegistrationlocator
				.getUserRegistrationServiceWSDDServiceName();
		userRegistrationlocator
				.setUserRegistrationServiceEndpointAddress(System
						.getProperty("com.yodlee.soap.services.url")
						+ "/"
						+ userRegistrationServiceName);
		try {
			userRegistration = userRegistrationlocator
					.getUserRegistrationService();

		} catch (ServiceException se) {
			throw new RuntimeException(se);
		}

		// Create Login Service
		LoginServiceLocator loginServiceLocator = new LoginServiceLocator();
		String serviceName1 = loginServiceLocator
				.getLoginServiceWSDDServiceName();
		loginServiceLocator.setLoginServiceEndpointAddress(System
				.getProperty("com.yodlee.soap.services.url")
				+ "/"
				+ serviceName1);
		try {
			login = loginServiceLocator.getLoginService();
		} catch (ServiceException se) {
			throw new RuntimeException(se);
		}

		// Create Item Management Service
		ItemManagementServiceLocator itemManagementlocator = new ItemManagementServiceLocator();
		String itemManagementServiceName = itemManagementlocator
				.getItemManagementServiceWSDDServiceName();
		itemManagementlocator.setItemManagementServiceEndpointAddress(System
				.getProperty("com.yodlee.soap.services.url")
				+ "/"
				+ itemManagementServiceName);
		try {
			itemManagement = itemManagementlocator.getItemManagementService();
		} catch (ServiceException se) {
			throw new RuntimeException(se);
		}

		// Create Content Service Tranversal service
		ContentServiceTraversalServiceLocator contentServiceLocator = new ContentServiceTraversalServiceLocator();
		String contentServiceName = contentServiceLocator
				.getContentServiceTraversalServiceWSDDServiceName();
		contentServiceLocator
				.setContentServiceTraversalServiceEndpointAddress(System
						.getProperty("com.yodlee.soap.services.url")
						+ "/"
						+ contentServiceName);
		try {
			contentServiceTraversal = contentServiceLocator
					.getContentServiceTraversalService();
		} catch (ServiceException se) {
			throw new RuntimeException(se);
		}

		// Create COntent Service Search
		SearchServiceLocator searchServiceLocator = new SearchServiceLocator();
		String searchServiceName = searchServiceLocator
				.getSearchServiceWSDDServiceName();
		searchServiceLocator.setSearchServiceEndpointAddress(System
				.getProperty("com.yodlee.soap.services.url")
				+ "/"
				+ searchServiceName);
		try {
			search = searchServiceLocator.getSearchService();

		} catch (ServiceException se) {
			throw new RuntimeException(se);
		}

		// Create Refresh Service
		RefreshServiceLocator refreshServiceLocator = new RefreshServiceLocator();
		String refreshServiceName = refreshServiceLocator
				.getRefreshServiceWSDDServiceName();
		refreshServiceLocator.setRefreshServiceEndpointAddress(System
				.getProperty("com.yodlee.soap.services.url")
				+ "/"
				+ refreshServiceName);
		try {
			refresh = refreshServiceLocator.getRefreshService();
		} catch (ServiceException se) {
			throw new RuntimeException(se);
		}

		// Create Data Service
		DataServiceServiceLocator dataServiceLocator = new DataServiceServiceLocator();
		String dataServiceName = dataServiceLocator
				.getDataServiceWSDDServiceName();
		dataServiceLocator.setDataServiceEndpointAddress(System
				.getProperty("com.yodlee.soap.services.url")
				+ "/"
				+ dataServiceName);
		try {
			dataService = dataServiceLocator.getDataService();
		} catch (ServiceException se) {
			throw new RuntimeException(se);
		}

		// Create Transaction Search
		TransactionSearchServiceServiceLocator transactionSearchServiceLocator = new TransactionSearchServiceServiceLocator();
		String transactionSearchServiceName = transactionSearchServiceLocator
				.getTransactionSearchServiceWSDDServiceName();
		transactionSearchServiceLocator
				.setTransactionSearchServiceEndpointAddress(System
						.getProperty("com.yodlee.soap.services.url")
						+ "/"
						+ transactionSearchServiceName);
		try {
			transactionSearchService = transactionSearchServiceLocator
					.getTransactionSearchService();
		} catch (ServiceException se) {
			throw new RuntimeException(se);
		}

		// Create Transaction Categorization
		TransactionCategorizationServiceServiceLocator transactionCategorizationServiceLocator = new TransactionCategorizationServiceServiceLocator();
		String transactionCategorizationServiceName = transactionCategorizationServiceLocator
				.getTransactionCategorizationServiceWSDDServiceName();
		transactionCategorizationServiceLocator
				.setTransactionCategorizationServiceEndpointAddress(System
						.getProperty("com.yodlee.soap.services.url")
						+ "/"
						+ transactionCategorizationServiceName);
		try {
			tcService = transactionCategorizationServiceLocator
					.getTransactionCategorizationService();
		} catch (ServiceException se) {
			throw new RuntimeException(se);
		}
	}

	/**
	 * Login as a Cobrand and get the CobrandContext. If the CobrandContext has
	 * not expired, returned cached version. If it has expired, then login
	 * cobrand and get new CobrandContext.
	 * 
	 * @return CobrandContext
	 */
	public CobrandContext getCobrandContext() {
		long now = System.currentTimeMillis();
		long expired = created + COBRAND_CONTEXT_TIME_OUT;

		// System.out.println("now = " + now);
		// System.out.println("expired = " + expired );
		// System.out.println("created = " + created );
		if (now >= expired) {
			long age = (now - created) / 1000 / 60;
			System.out.println("\tCobrandContext is has expired (" + age
					+ " minutes old), creating new one...");

			try {
				// authentication of a cobrand in the Yodlee software platform
				// and returns
				// a valid CobrandContext if the authentication is successful.
				// This method takes a generic CobrandCredentials argument as
				// the
				// authentication related credentials of the cobrand.

				Locale ENGLISH = Locale.ENGLISH;
				com.yodlee.soap.collections.Locale locale_english = new com.yodlee.soap.collections.Locale(
						ENGLISH.getCountry(), ENGLISH.getLanguage(),
						ENGLISH.getVariant());

				CobrandCredentials cobCred = new CobrandPasswordCredentials(
						cobrandLogin, cobrandPassword);
				cobrandContext = cobrandLoginProxy
						.loginCobrand(new Long(cobrandId), appId,
								locale_english, new Long(2), cobCred);
			} catch (InvalidCobrandCredentialsExceptionFault icce) {
				System.out.println("InvalidCobrandCredentialsException");
			} catch (CobrandUserAccountLockedExceptionFault cuale) {
				System.out.println("CobrandUserAccountLockedException");
			} catch (Exception ee) {
				System.out.println("Exception: " + ee);
				ee.printStackTrace();
				throw new RuntimeException(ee);
			}

			created = System.currentTimeMillis();

			return cobrandContext;
		} else {
			long age = (now - created) / 1000 / 60;
			System.out.println("\tCobrandContext is valid (" + age
					+ " minutes old), using cached CobrandContext...");

			return cobrandContext;
		}
	}

	/**
	 * Register a User in Yodlee System.
	 * 
	 * @param cobrandContext
	 *            context of the cobrand
	 * @return newly created usercontext for the user or null
	 */
	public UserContext registerUser(CobrandContext cobrandContext,
			String userName, String email, String password) {
		UserContext userContext = null;
		UserCredentials uc = new PasswordCredentials(userName, password);

		UserProfile up = new UserProfile();
		Entry en = new Entry("EMAIL_ADDRESS", email);
		Entry[] entries = new Entry[1];
		entries[0] = en;
		up.setValues(new com.yodlee.soap.collections.Map(entries));

		// Register the user
		UserInfo ui = null;
		try {
			ui = userRegistration.register3(cobrandContext, uc, up, null);

			userContext = ui.getUserContext();
		} catch (IllegalArgumentValueExceptionFault ex) {
			System.out.println("\n\nGot Illegal Arguments for Registration.");
			System.out
					.println("Please note that Yodlee enforces the following restrictions:");
			System.out.println("On username:");
			System.out.println("  >= 3 characters");
			System.out.println("  <= 150 characters");
			System.out
					.println("  No Whitespace - checks: java.lang.Character.isWhitespace()");
			System.out
					.println("  No Control Characters - checks: java.lang.Character.isISOControl()");
			System.out
					.println("  Contains at least one Letter - java.lang.Character.isLetter() ");
			System.out.println("\nOn password");
			System.out.println("  >= 6 characters");
			System.out.println("  <= 50 characters");
			System.out
					.println("  No Whitespace - checks: java.lang.Character.isWhitespace()");
			System.out
					.println("  No Control Characters - checks: java.lang.Character.isISOControl()");
			System.out
					.println("  Contains at least one Number - checks: java.lang.Character.isDigit() || !java.lang.Character.isLetter()");
			System.out
					.println("  Contains at least one Letter - java.lang.Character.isLetter()");
			System.out
					.println("  Does not contain the same letter/number three or more times in a row.  (e.g. aaa123 would fail for three \"a\"'s in a row, but a1a2a3 would pass)");
			System.out.println("  Does not equal username");
			System.out.println("\n\n");
			return null;
		} catch (UserNameExistsExceptionFault ex) {
			System.out.println("User " + userName + " already exists");
			// TODO: log error
			return null;

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		return userContext;
	}

	/**
	 * Login a User in to Yodlee System
	 * 
	 * @param cobrandContext
	 *            context of the cobrand
	 * @return newly created usercontext for the user or null
	 */
	public UserContext loginUser(CobrandContext cobrandContext, String unvalidatedUserName, String password) {
		UserContext userContext = null;
		try {
			UserInfo userInfo = userInfo = login.login2(cobrandContext,
					new PasswordCredentials(unvalidatedUserName, password));

			userContext = userInfo.getUserContext();
			System.out.println(unvalidatedUserName + " logged in");

		} catch (InvalidUserCredentialsExceptionFault iucex) {
			System.out.println("Invalid credentials!");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return userContext;
	}

	public ArrayOfContentServiceInfo getContentServiceInfos(
			CobrandContext cobrandContext) {
		ContentServiceTraversalServiceLocator locator1 = new ContentServiceTraversalServiceLocator();
		String serviceName1 = locator1
				.getContentServiceTraversalServiceWSDDServiceName();
		locator1.setContentServiceTraversalServiceEndpointAddress(System
				.getProperty("com.yodlee.soap.services.url")
				+ "/"
				+ serviceName1);

		try {
			cst = locator1.getContentServiceTraversalService();
			ArrayOfContentServiceInfo listOfServiceInfos = cst
					.getContentServicesByContainerType3(cobrandContext,
							ContainerTypesHelper.BANK, new Integer(16));
			return listOfServiceInfos;
		} catch (StaleConversationCredentialsExceptionFault e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidCobrandConversationCredentialsExceptionFault e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidCobrandContextExceptionFault e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidConversationCredentialsExceptionFault e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CoreExceptionFault e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentValueExceptionFault e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public Map getAllLoginForms(CobrandContext cobrandContext) {
		try {
			itemManagement
					.getLoginFormForContentService(getCobrandContext(),
							(long) 3697).getComponentList().getElements();
			for (FieldInfoSingle component : (FieldInfoSingle[]) itemManagement
					.getLoginFormForContentService(getCobrandContext(),
							(long) 3697).getComponentList().getElements()) {
				System.out.println(component.getDisplayName());
			}

			// return itemManagement.getAllLoginForms(cobrandContext);
		} catch (StaleConversationCredentialsExceptionFault e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidCobrandConversationCredentialsExceptionFault e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidCobrandContextExceptionFault e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidConversationCredentialsExceptionFault e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CoreExceptionFault e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentValueExceptionFault e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Adds a Single Item for User
	 * 
	 * @param userContext
	 *            of the user adding the item
	 * @param cobrandContext
	 *            to check if item is MFA
	 */
	public boolean addItem(UserContext userContext, CobrandContext cobrandContext,
			long contentServiceId, List fieldInfoList) {

		// Query Back the Content Service For the User
		// System.out.print("Enter Content ServiceId: ");
		// long contentServiceId = IOUtils.readInt();

		// Read in all the required input fields for the form.
		//List fieldInfoList = FormUtil.getUserInputFieldInfoList(userContext,
		//		getLoginFormForContentService(userContext, contentServiceId));

		//System.out.println(fieldInfoList);
		
		//System.out.println("\nAdding item");
		Long itemId = null;
		try {
			itemId = itemManagement.addItemForContentService1(
					userContext,
					new Long(contentServiceId),
					new com.yodlee.soap.collections.List(fieldInfoList
							.toArray()), false, false);
			//System.out.println("Successfully created itemId: " + itemId);

			// FormUtil.writeFormHtml(userContext, itemId.longValue(),
			// getLoginFormCredentialsForItem(userContext, itemId.longValue()),
			// false,
			// "FilledForm");

			String mfatype = getMFAType(cobrandContext, new Long(
					contentServiceId));
			if (mfatype != null) {
				refreshItem(userContext, itemId, true);
			} else {
				refreshItem(userContext, itemId, false);
			}
			// Poll for the refresh status and display item
			// summary if refresh succeeds.
			if (pollRefreshStatus(userContext, itemId)) {
				displayItemSummary(userContext, itemId.longValue());
			}
			return true;
		} catch (Exception coreEx) {
			//coreEx.printStackTrace();
			return false;
			// throw new RuntimeException
			// ("Unable to add item for content service!");
		}
	}

	/**
	 * Get Login Form For Conent Service (site)
	 * 
	 * @param userContext
	 * @param csId
	 *            - content service Id
	 * @return Form
	 */
	public Form getLoginFormForContentService(UserContext userContext, long csId) {
		try {
			return itemManagement.getLoginFormForContentService(userContext,
					new Long(csId));
		} catch (Exception e) {
			throw new RuntimeException(
					"Error fetching login form for this CsId: " + csId);
		}
	}

	/**
	 * Get Login Form for Item Id along with account credentials
	 * 
	 * @param userContext
	 * @param itemId
	 * @return Form
	 */
	public Form getLoginFormCredentialsForItem(UserContext userContext,
			long itemId) {
		try {
			return itemManagement.getLoginFormCredentialsForItem(userContext,
					new Long(itemId));
		} catch (Exception e) {
			throw new RuntimeException(
					"Error fetching login form credentials for this ItemId");
		}
	}

	/**
	 * Refresh the specified item.
	 * <p>
	 * 
	 * @param userContext
	 *            The user context.
	 * @param itemId
	 *            The identifier of the item that needs to be refreshed.
	 */
	public void refreshItem(UserContext userContext, Long itemId, boolean isMfa) {
		try {
			System.out.println("Attempting to start refresh...");
			RefreshParameters refreshParameters = new RefreshParameters();
			RefreshMode refreshMode;
			if (isMfa)
				refreshMode = RefreshMode.MFA_REFRESH_MODE;
			else
				refreshMode = RefreshMode.NORMAL_REFRESH_MODE;
			refreshParameters.setRefreshMode(refreshMode);
			refreshParameters
					.setRefreshPriority(RefreshHelper.REFRESH_PRIORITY_HIGH);
			refreshParameters.setForceRefresh(true);

			RefreshStatus status = refresh.startRefresh7(userContext, itemId,
					refreshParameters);

			if (status == RefreshStatus.SUCCESS_START_REFRESH) {
				System.out.println("\tStarted refresh.");
			} else if (status == RefreshStatus.REFRESH_ALREADY_IN_PROGRESS) {
				System.out.println("\tThe refresh is already in progress.");
			} else if (status == RefreshStatus.ALREADY_REFRESHED_RECENTLY) {
				throw new RuntimeException("This item has been refreshed very "
						+ "recently.  Please try again later.");
			} else if (status == RefreshStatus.ITEM_CANNOT_BE_REFRESHED) {
				throw new RuntimeException("The refresh on this item is not "
						+ "permitted.");
			} else {
				throw new RuntimeException("Unable to refresh the item "
						+ "RefreshStatus: " + status);
			}

			if (isMfa && (status == RefreshStatus.SUCCESS_START_REFRESH)) {
				// Checking the MFA Reponse for the Item
				MFARefreshInfo mfaInfo = refresh.getMFAResponse(userContext,
						itemId);
				int errorCode = processMFA(userContext, mfaInfo, itemId);
				if (errorCode == 0) {
					System.out.println("MFA site added successfully");
				} else if (errorCode > 0) {
					System.out
							.println("There was an error while adding MFA site. Error code is "
									+ errorCode);
				} else if (errorCode < 0) {
					System.out
							.println("There was an exception while adding MFA site. Error code is "
									+ errorCode);
				}
			}
		} catch (InvalidItemExceptionFault ex) {
			throw new RuntimeException("The given item is invalid.");
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	/**
	 * This method will establish a real time interaction with the agent. Agent
	 * sends the questions to user and user answers and creates a response and
	 * send it to the agent Agent will stop if the answers are correct or
	 * incorrect with the appropriate errorCode.
	 * <p>
	 * 
	 * @param userContext
	 *            The user context.
	 * @param itemId
	 *            The identifier of the item for which MFA should be processed
	 * @param mfaInfo
	 *            MFA refresh information for the item to be processed
	 * @return MFArefresh code
	 */
	public int processMFA(UserContext userContext, MFARefreshInfo mfaInfo,
			Long itemId) {

		System.out.println("\tEntering MFA flow");
		// Check MFARefreshInfo is null and then proceed, If there are any
		// questions then MFARefreshInfo will not be null
		while (mfaInfo != null) {
			try {
				// First time when the agent has some questions, getErrorCode()
				// will be null
				if (mfaInfo.getErrorCode() != null) {
					int errorCode = mfaInfo.getErrorCode().intValue();
					// If the getErrorCode() is 0 then it indicates that the
					// agent was able to login to the site with the
					// MFA questions successfully
					if (errorCode == 0) {
						return errorCode;
						// If the getErrorCode() is non-zero then it indicates
						// that there was some gatherer error and needs to break
						// from the loop
					} else if (errorCode > 0) {
						return errorCode;
					}
				}

				// Check if there are any MFA questions for the user.
				MFAFieldInfo fieldInfo = mfaInfo.getFieldInfo();

				if (fieldInfo != null) {
					long answerTimeout = mfaInfo.getTimeOutTime().longValue();
					// If the site is Token based
					if (fieldInfo instanceof TokenIdFieldInfo) {
						TokenIdFieldInfo token_fieldInfo = (TokenIdFieldInfo) fieldInfo;
						System.out.print("\nYou have " + answerTimeout / 1000
								+ " seconds to enter the token number");
						System.out.println("\n" + "Enter the "
								+ token_fieldInfo.getDisplayString());
						// Read the token value
						String tokenId = IOUtils.readStr();
						// Create the token response
						MFATokenResponse mfatokenresponse = new MFATokenResponse();
						mfatokenresponse.setToken(tokenId);
						// Put this MFA Request back in the queue
						refresh.putMFARequest(userContext, mfatokenresponse,
								itemId);
					}// If the site is Image based
					else if (fieldInfo instanceof ImageFieldInfo) {
						ImageFieldInfo image_fieldInfo = (ImageFieldInfo) fieldInfo;
						try {
							// Place the image obtained at a particular path for
							// the user to view
							String filename = "MFA_" + itemId + ".jpg";
							BufferedOutputStream out = new BufferedOutputStream(
									new FileOutputStream(filename));
							out.write(image_fieldInfo.getImage().getElements());
							out.close();
							System.out.println("Image " + filename
									+ " has been placed at "
									+ System.getProperty("user.dir")
									+ " for viewing.\n");
						} catch (IOException e) {
							System.out
									.println("Exception while writing the image onto the file");
						}
						System.out.print("\nYou have " + answerTimeout / 1000
								+ " seconds to enter the code");
						// Get the corresponding code from the user
						System.out.println("\n"
								+ "Enter the code present in the image");
						String imageCode = IOUtils.readStr();
						// Create the MFA response and place it in the queue for
						// the agent to read
						MFAImageResponse mfaimageresponse = new MFAImageResponse();
						mfaimageresponse.setImageString(imageCode);
						refresh.putMFARequest(userContext, mfaimageresponse,
								itemId);
					} // If the site is Security Question type
					else if (fieldInfo instanceof SecurityQuestionFieldInfo) {
						SecurityQuestionFieldInfo securityqa_fieldInfo = (SecurityQuestionFieldInfo) fieldInfo;
						ArrayOfQuestionAndAnswerValues queAndAns = securityqa_fieldInfo
								.getQuestionAndAnswerValues();
						// Create the MFA response for security questions
						MFAQuesAnsResponse mfaqaResponse = new MFAQuesAnsResponse();
						QuesAndAnswerDetails[] qaDetails = new QuesAndAnswerDetails[queAndAns
								.getElements().length];
						int count = 0;
						System.out.print("\nYou have " + answerTimeout / 1000
								+ " seconds to answer the questions");
						for (int loopcounter = 0; loopcounter < queAndAns
								.getElements().length; loopcounter++) {
							if (queAndAns.getElements(loopcounter) instanceof SingleQuesSingleAnswerValues) {
								// Get the question
								String mfa_ques = ((SingleQuesSingleAnswerValues) queAndAns
										.getElements(loopcounter))
										.getQuestion();
								System.out.print("\n" + mfa_ques);
								// Get the answer
								System.out.print("\nAnswer: ");
								String mfa_answer = IOUtils.readStr().trim();
								// Get the MFA_TYPE
								String que_type = ((SingleQuesSingleAnswerValues) queAndAns
										.getElements(loopcounter))
										.getQuestionFieldType();
								// Get the answer field type
								String ans_type = ((SingleQuesSingleAnswerValues) queAndAns
										.getElements(loopcounter))
										.getResponseFieldType();
								// Get the metadata
								String metadata = ((SingleQuesSingleAnswerValues) queAndAns
										.getElements(loopcounter))
										.getMetaData();
								// Create the Response using the question &
								// answer
								QuesAndAnswerDetails mfaqa_details = new QuesAndAnswerDetails(
										mfa_ques, mfa_answer, que_type,
										ans_type, metadata);
								// mfa_qa.add(mfaqa_details);
								qaDetails[count++] = mfaqa_details;
							} else if (queAndAns.getElements(loopcounter) instanceof MultiQuesMultiAnswerOptionsValues) {
								// This is for sites having checkbox or radio
								// buttons
								// ToDo:Will be implemented later
								System.out
										.println("Inside MultiQuesMultiAnswerOptionsValues");
							} else if (queAndAns.getElements(loopcounter) instanceof MultiQuesOptionsSingleAnswerValues) {
								// This is for sites having checkbox or radio
								// buttons
								// ToDo:Will be implemented later
								System.out
										.println("Inside MultiQuesOptionsSingleAnswerValues");
							} else if (queAndAns.getElements(loopcounter) instanceof SingleQuesMultiAnswerOptionsValues) {
								// This is for sites having checkbox or radio
								// buttons
								// ToDo:Will be implemented later
								System.out
										.println("Inside SingleQuesMultiAnswerOptionsValues");
							}
						}
						// Create the response and place it in the queue for the
						// agent to read
						mfaqaResponse
								.setQuesAnsDetailArray(new ArrayOfQuesAndAnswerDetails(
										qaDetails));
						refresh.putMFARequest(userContext, mfaqaResponse,
								itemId);
					}
				}
				// Get the MFA response from the agent, which contains the MFA
				// questions
				// The questions will be placed in the MQ and the app or SDK
				// calls can poll for these questions continuously
				mfaInfo = refresh.getMFAResponse(userContext, itemId);
			} catch (Exception e) {
				// System.out.println("Inside the exception");
				e.printStackTrace();
				return -1;
			}
		} // End of while
		return -1;
	}

	/**
	 * Polls the refresh status of the specified item. This method continues
	 * polling till the refresh either completes or times out. Returns true if
	 * the refresh was successfully completed and false otherwise.
	 * 
	 * Unlike origPollRefreshStatus(), this method uses the
	 * RefreshInfo.isItemRefreshing() to do more robust handing
	 * <p>
	 * 
	 * @param userContext
	 *            The user context.
	 * @param itemId
	 *            The identifier of the item whose refresh status should be
	 *            polled.
	 * @return true if refresh is successful false otherwise
	 */
	public boolean pollRefreshStatus(UserContext userContext, Long itemId) {
		long startTime = (new Date()).getTime();
		long currTime = startTime;
		ArrayOflong itemIds = new ArrayOflong(new Long[] { itemId });

		while (currTime - startTime < REFRESH_TIMEOUT_MIILIS) {
			System.out.println("\tChecking the refresh status .... ");
			try {
				boolean isItemRefreshing = refresh.isItemRefreshing(
						userContext, itemId);
				ArrayOfRefreshInfo refreshInfo = refresh.getRefreshInfo1(
						userContext, itemIds);
				if (!isItemRefreshing) {
					int refreshStatusCode = refreshInfo.getElements(0)
							.getStatusCode().intValue();
					if (refreshStatusCode == GATHERER_ERRORS_STATUS_OK) {
						Long lastRefreshedTime = refreshInfo.getElements(0)
								.getLastUpdatedTime();
						System.out.println("Last refreshed time = "
								+ lastRefreshedTime);
						System.out
								.println("\tThe refresh has completed successfully.");
						return true;
					} else {
						System.out
								.println("\tThe refresh did not succeed.  Error code: "
										+ refreshStatusCode);
						return false;
					}
				}
				Thread.sleep(SLEEP_MILLIS);
				currTime = (new Date()).getTime();
			} catch (InterruptedException ex) {
				throw new RuntimeException(
						"Refresh polling has been interrupted!");
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException();
			}
		}

		// Timeout the refresh request
		try {
			refresh.stopRefresh(userContext, itemId,
					RefreshHelper.STOP_REFRESH_REASON_TIMEDOUT);
		} catch (Exception e) {
		}
		System.out.println("\tThe refresh has timed out.");
		return false;
	}

	/**
	 * get the item (account) summary for a user's item
	 * 
	 * @param userContext
	 *            of the user
	 * @param itemId
	 *            for which the summary is retrieved
	 * 
	 */
	public void displayItemSummary(UserContext userContext, long itemId) {
		DataExtent dataExtent = new DataExtent();
		dataExtent.setStartLevel(new Integer(0));
		dataExtent.setEndLevel(new Integer(Integer.MAX_VALUE));
		try {
			ItemSummary itemSummary = dataService.getItemSummaryForItem1(
					userContext, new Long(itemId), dataExtent);

			if (itemSummary == null) {
				System.out.println("The given item is invalid.");
			} else {
				String containerType = itemSummary.getContentServiceInfo()
						.getContainerInfo().getContainerName();

				if (containerType.equals(ContainerTypesHelper.BANK)) {
					displayBankDataForItem(itemSummary);
				} else if (containerType.equals(ContainerTypesHelper.BILL)) {
					(new DisplayBillsData())
							.displayBillsDataForItem(itemSummary);
				} else if (containerType
						.equals(ContainerTypesHelper.CREDIT_CARD)) {
					(new DisplayCardData()).displayCardDataForItem(itemSummary);
				} else if (containerType.equals(ContainerTypesHelper.INSURANCE)) {
					(new DisplayInsuranceData())
							.displayInsuranceDataForItem(itemSummary);
				} else if (containerType
						.equals(ContainerTypesHelper.INVESTMENT)) {
					(new DisplayInvestmentData())
							.displayInvestmentDataForItem(itemSummary);
				} else if (containerType.equals(ContainerTypesHelper.LOAN)) {
					(new DisplayLoanData()).displayLoanDataForItem(itemSummary);
				} else {
					(new DisplayItemInfo()).displayItemSummaryInfo(itemSummary);
				}
			}
		} catch (InvalidItemExceptionFault ex) {
			throw new RuntimeException("The given item is invalid.");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Displays the item information and item data information for the given
	 * bank itemSummary.
	 * <p>
	 * 
	 * @param is
	 *            an itemSummary whose containerType is 'bank'
	 */
	public void displayBankDataForItem(ItemSummary is) {
		System.out.println("");
		String containerType = is.getContentServiceInfo().getContainerInfo()
				.getContainerName();

		if (!containerType.equals("bank")) {
			throw new RuntimeException("displayBankDataForItem called with "
					+ "invalid container type: " + containerType);
		}

		displayItemSummaryInfo(is);

		// get BankData
		ItemData id = is.getItemData();
		if (id == null) {
			System.out.println("\tItemData == null");
		} else {
			com.yodlee.soap.collections.List accounts = id.getAccounts();
			if (accounts == null || accounts.getElements().length == 0) {
				System.out.println("\tNo accounts");
			} else {
				for (int i = 0; i < accounts.getElements().length; i++) {
					System.out.println("\n\t**BankData**");
					BankData bankData = (BankData) accounts.getElements(i);
					System.out.println("\tBank Account Name: "
							+ bankData.getAccountName());
					System.out.println("\tBank Account Number: "
							+ bankData.getAccountNumber());
					System.out.println("\tBank Account Id: "
							+ bankData.getBankAccountId());
					System.out.println("\tBank Account Type: "
							+ bankData.getAcctType());
					System.out.println("\tBank Account Identifier: "
							+ bankData.getBankAccountId());
					System.out.println("\tBank Account Balance: "
							+ Formatter.formatMoney(bankData
									.getAvailableBalance()));
					System.out.println("\tBank Current Balance: "
							+ Formatter.formatMoney(bankData
									.getCurrentBalance()));
					System.out.println("\tBank As Of Date: "
							+ Formatter.formatDate(bankData.getAsOfDate()
									.getDate(), Formatter.DATE_SHORT_FORMAT));

					long lu = bankData.getLastUpdated().longValue() * 1000;
					Date date = new Date(lu);
					System.out.println("\tBank Last Updated: "
							+ Formatter.formatDate(date,
									Formatter.DATE_LONG_FORMAT));

					com.yodlee.soap.collections.List bankTransactions = bankData
							.getBankTransactions();

					if (bankTransactions == null
							|| bankTransactions.getElements().length == 0) {
						System.out.println("\n\t\tNo bank transactions");
					} else {
						System.out.println("\n\t\t**BankTransactionData**");
						for (int txns = 0; txns < bankTransactions
								.getElements().length; txns++) {
							BankTransactionData transactionData = (BankTransactionData) bankTransactions
									.getElements(txns);
							System.out.println("\t\tPost Date: "
									+ Formatter.formatDate(transactionData
											.getPostDate().getDate(),
											Formatter.DATE_SHORT_FORMAT));
							System.out.println("\t\tTrans Date: "
									+ Formatter.formatDate(transactionData
											.getTransactionDate().getDate(),
											Formatter.DATE_SHORT_FORMAT));
							System.out.println("\t\tTransaction Id: "
									+ transactionData.getBankTransactionId());
							System.out.println("\t\tTransaction Description: "
									+ transactionData.getDescription());
							System.out.println("\t\tTransaction Amount: "
									+ Formatter.formatMoney(transactionData
											.getTransactionAmount()));
							// System.out.println ("\t\tTransaction Type " +
							// (transactionData.getTransactionType ()));
							System.out
									.println("\t\tTransaction Base Type: "
											+ (transactionData
													.getTransactionBaseType()));
							// Note: This category is gathered from the data
							// source, this is not
							// the transaction category from the Yodlee
							// Transaction Categorization
							// engine. To get the Yodlee Cateogry the
							// Transaction Search APIs
							// must be used.
							System.out.println("\t\tCategory: "
									+ (transactionData.getCategory()));
							System.out.println("");
						}
					}
				}
			}

			// Account History
			com.yodlee.soap.collections.List acctHistory = id
					.getAccountHistory();

			if (acctHistory == null || acctHistory.getElements().length == 0) {
				System.out.println("\tNo Account History");
			} else {
				System.out.println("\n\t**Account History**");
				for (int acchist = 0; acchist < acctHistory.getElements().length; acchist++) {
					AccountHistory acctHist = (AccountHistory) acctHistory
							.getElements(acchist);
					System.out.println("\tAccount Id : "
							+ acctHist.getAccountId());
					com.yodlee.soap.collections.List histories = acctHist
							.getHistory();
					if (histories == null | histories.getElements().length == 0) {
						System.out.println("\t\tNo History");
					} else {
						System.out.println("\t\t**History**");
						for (int hist = 0; hist < histories.getElements().length; hist++) {
							BankData bankData = (BankData) histories
									.getElements(hist);
							System.out.println("\t\tBank Account Name: "
									+ bankData.getAccountName());
							System.out.println("\t\tBank Account Identifier: "
									+ bankData.getBankAccountId());
							System.out.println("\t\tBank Available Balance: "
									+ (bankData.getAvailableBalance())
											.getAmount());
							System.out.println("\t\tBank Current Balance: "
									+ (bankData.getCurrentBalance())
											.getAmount());
							// You can differentiate between the datasets using
							// getLastUpdated
							long lu = bankData.getLastUpdated().longValue() * 1000;
							Date date = new Date(lu);
							System.out.println("\t\tBank Last Updated: "
									+ date.toString());
							System.out.println("");

						}
					}
				}
			}

		}
	}

	/**
	 * Displays the item information for the given item summary.
	 * <p>
	 * 
	 * @param itemSummary
	 *            an item summary instance whose information should be
	 *            displayed.
	 */
	public void displayItemSummaryInfo(ItemSummary itemSummary) {
		System.out.println("\n" + itemSummary.getItemDisplayName() + " ("
				+ itemSummary.getItemId() + ")");
		System.out.println("\tItem Id: " + itemSummary.getItemId());
		System.out.println("\tSum Info Id: "
				+ itemSummary.getContentServiceId());
		System.out.println("\tContainer type: "
				+ itemSummary.getContentServiceInfo().getContainerInfo()
						.getContainerName());
		System.out.println("\tContent Service Name: "
				+ (itemSummary.getContentServiceInfo()).getSiteDisplayName());

		long lu = itemSummary.getRefreshInfo().getLastUpdatedTime().longValue() * 1000;
		Date date = new Date(lu);
		System.out.println("\tLast updated time : "
				+ Formatter.formatDate(date, Formatter.DATE_LONG_FORMAT));

		lu = itemSummary.getRefreshInfo().getLastUpdateAttemptTime()
				.longValue() * 1000;
		date = new Date(lu);
		System.out.println("\tLast update attempt time : "
				+ Formatter.formatDate(date, Formatter.DATE_LONG_FORMAT));

		System.out.println("\tRefresh status code: "
				+ itemSummary.getRefreshInfo().getStatusCode());

		ItemData itemData = itemSummary.getItemData();
		if (itemData != null) {
			com.yodlee.soap.collections.List itemAccounts = itemData
					.getAccounts();
			System.out.println("\tItem Account count: "
					+ itemAccounts.getElements().length);
			for (int i = 0; i < itemAccounts.getElements().length; i++) {
				BaseTagData dataType = (BaseTagData) itemAccounts
						.getElements(i);
				System.out.println("\tData Type    : "
						+ dataType.getClass().getName());
			}
		}
	}

	/**
	 * This method is to find the MFAType of a content service given the
	 * contentServiceId and cobrandContext
	 * <p>
	 * 
	 * @param cobrandContext
	 *            the cobrand to check MFA type for
	 * @param contentServiceId
	 *            the content service to check the MFA type for
	 * @return the MFA type as string
	 */
	public String getMFAType(CobrandContext cobrandContext,
			Long contentServiceId) {
		ContentServiceInfo csInfo = null;
		try {
			csInfo = cst
					.getContentServiceInfo(cobrandContext, contentServiceId);
		} catch (Exception e) {
			return null;
		}
		return (csInfo.getMfaType() != null ? csInfo.getMfaType().getValue()
				: null);
	}

	/**
	 * Transactions Management This method is to get all transactions for a
	 * itemaccount id
	 * <p>
	 * 
	 * @param userContext
	 *            of the user
	 * @param itemAccountId
	 *            the item account id of the user
	 * 
	 */

	public void viewTransactionsForItemAccount(UserContext userContext,
			long itemAccountId) {
		long startRange = 1;
		long endRange = 100;

		// Create Results Range
		TransactionSearchResultRange txSearchResultRange = new TransactionSearchResultRange(
				new Long(startRange), new Long(endRange));

		// Create TransactionSearchFilter
		TransactionSearchFilter txSearchFilter = new TransactionSearchFilter();
		txSearchFilter.setItemAccountId(new ItemAccountId(new Long(
				itemAccountId)));

		// Retrieve for the previous 30 days of transactions
		/*
		 * Date sysDate = new Date(System.currentTimeMillis()); Calendar
		 * fromCalendar = Calendar.getInstance();
		 * fromCalendar.add(Calendar.DAY_OF_YEAR, -30); DateRange dateRange =
		 * new DateRange(fromCalendar.getTime(), sysDate);
		 * txSearchFilter.setPostDateRange(dateRange);
		 */

		// Create TransactionSearchRequest
		TransactionSearchRequest txSearchRequest = new TransactionSearchRequest();
		txSearchRequest.setSearchFilter(txSearchFilter);
		txSearchRequest.setContainerType("all");
		txSearchRequest.setIgnorePaymentTransactions(false);
		txSearchRequest.setIncludeAggregatedTransactions(true);
		txSearchRequest.setResultRange(txSearchResultRange);
		txSearchRequest.setIgnoreUserInput(true);

		TransactionSearchExecInfo txSearchExecInfo;
		try {
			txSearchExecInfo = transactionSearchService
					.executeUserSearchRequest(userContext, txSearchRequest);
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
		if (txSearchExecInfo != null) {
			viewTransactions(userContext, txSearchExecInfo);
		} else {
			System.out.println("\nNo transactions found");
		}
	}

	/**
	 * Transactions Management This method is to list all transactions from a
	 * transactionsearchobject
	 * <p>
	 * 
	 * @param userContext
	 *            of the user
	 * @param txSearchExecInfo
	 *            transaction search info object
	 * 
	 */

	public void viewTransactions(UserContext userContext,
			TransactionSearchExecInfo txSearchExecInfo) {
		Long numberOfHits = txSearchExecInfo.getNumberOfHits();
		if (numberOfHits == null || numberOfHits.intValue() <= 0) {
			System.out
					.println("No transactions returned from search criteria.\n");
		} else {
			TransactionSearchResult txSearchResult = txSearchExecInfo
					.getSearchResult();
			ArrayOfTransactionView txView = txSearchResult.getTransactions();

			if (txView != null && txView.getElements() != null) {
				for (int i = 0; i < txView.getElements().length; ++i) {
					System.out.println("PostDate="
							+ Formatter
									.formatDate(txView.getElements(i)
											.getPostDate(),
											Formatter.DATE_SHORT_FORMAT)
							+ " "
							+ "TransDate="
							+ Formatter.formatDate(txView.getElements(i)
									.getTransactionDate(),
									Formatter.DATE_SHORT_FORMAT)
							+ "\n "
							+ "ItemAccountId="
							+ txView.getElements(i).getAccount()
									.getItemAccountId()
							+ "\n "
							+ "TransactionId="
							+ txView.getElements(i).getViewKey()
									.getTransactionId()
							+ "\n "
							+ "ContainerType="
							+ txView.getElements(i).getViewKey()
									.getContainerType()
							+ "\n "
							+ "Desc="
							+ txView.getElements(i).getDescription()
									.getDescription()
							+ "\n "
							+ "AccountName="
							+ txView.getElements(i).getAccount()
									.getAccountName()
							+ "\n "
							+ "Mem="
							+ txView.getElements(i).getMemo().getMemo()
							+ "\n "
							+ "CategoryName="
							+ txView.getElements(i).getCategory()
									.getCategoryName()
							+ "\n "
							+ "Status="
							+ txView.getElements(i).getStatus()
									.getDescription()
							+ "\n "
							+ "Price="
							+ Formatter.formatMoney(txView.getElements(i)
									.getPrice())
							+ "\n "
							+ "Quantity="
							+ txView.getElements(i).getQuantity()
							+ "\n "
							+ "CatKeyword="
							+ txView.getElements(i).getCategorizationKeyword()
							+ "\n "
							+ "RunningBalance="
							+ txView.getElements(i).getRunningBalance()
							+ "\n "
							+ "Amount="
							+ Formatter.formatMoney(txView.getElements(i)
									.getAmount()) + " ");
					System.out.println("\n");
				}
			}
		}
	}

	/**
	 * Views all the categories for a user.
	 * <p>
	 * 
	 * @param userContext
	 */
	public void viewUserTransactionCategories(UserContext userContext) {

		ArrayOfCategory categories;
		try {
			categories = tcService.getUserTransactionCategories(userContext);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException();
		}

		for (int i = 0; i < categories.getElements().length; i++) {
			displayCategory(categories.getElements(i));
		}
	}

	/**
	 * Displays information about a single category and the children of that
	 * category
	 * <p>
	 * 
	 * @param category
	 *            category to display information on
	 */
	public void displayCategory(
			com.yodlee.soap.core.transactioncategorization.Category category) {
		if (SUB_CATEGORY.equals(category.getCategoryLevelId())) {
			System.out.print("  ");
		}
		System.out.print("Id=");
		System.out.print(category.getCategoryId());
		System.out.print(",Name=");
		System.out.print(category.getCategoryName());
		System.out.print(",Description=");
		System.out.print(category.getCategoryDescription());
		System.out.print(",LevelId=");
		System.out.print(category.getCategoryLevelId());
		System.out.print("\n");
		ArrayOfCategory childCategories = category.getChildCategory();
		if (childCategories != null && childCategories.getElements() != null
				&& childCategories.getElements().length >= 0) {
			for (int i = 0; i < childCategories.getElements().length; i++) {
				displayCategory(childCategories.getElements(i));
			}
		}
	}

	/**
	 * Add a new category to an existing category
	 * <p>
	 * 
	 * @param userContext
	 */
	public void addUserCategory(UserContext userContext) {

		System.out.println("Adding new Category");
		System.out.print("Enter Parent Category Id: ");
		long parentCategoryId = IOUtils.readLong();
		System.out.print("New Category Name: ");
		String newCategoryName = IOUtils.readStr();
		com.yodlee.soap.core.transactioncategorization.Category category = new com.yodlee.soap.core.transactioncategorization.Category();
		category.setCategoryName(newCategoryName);
		// category.setCategoryLevelId(Long.valueOf(SUB_CATEGORY.longValue()));
		category.setCategoryLevelId(Long.valueOf(SUB_CATEGORY.toString()));
		category.setParentCategoryId(new Long(parentCategoryId));
		try {
			tcService
					.manageUserCategories(
							userContext,
							new ArrayOfCategory(
									new com.yodlee.soap.core.transactioncategorization.Category[] { category }));
			System.out.println("User category successfully added\n");
		} catch (CreateCategoryLimitExceptionFault e) {
			System.out
					.println("Exception: Exceeded the Maximum Number of Categories per User/Category limit");
		} catch (CategoryLevelNotSupportedExceptionFault e) {
			System.out.println("Exception: CategoryLevel not supported");
		} catch (CategoryAlreadyExistsExceptionFault e) {
			System.out.println("Exception: Category already exists");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Displays a simple list of all items that were added by the user.
	 * <p>
	 * 
	 * @param userContext
	 *            context of the user
	 * @param includeItemAccounts
	 *            indicates if Item Accounts should be included in the display
	 */
	public void viewItems(UserContext userContext, boolean includeItemAccounts) {
		com.yodlee.soap.collections.List itemSummaries = null;
		try {
			itemSummaries = dataService.getItemSummaries(userContext);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException();
		}

		if (itemSummaries == null || itemSummaries.getElements().length == 0) {
			System.out.println("You have no Items Added.");
		} else {
			for (int i = 0; i < itemSummaries.getElements().length; i++) {
				ItemSummary is = (ItemSummary) itemSummaries.getElements(i);
				String displayName = is.getContentServiceInfo()
						.getContentServiceDisplayName();
				System.out.println("ItemId: "
						+ is.getItemId()
						+ " DisplayName: "
						+ displayName
						+ " errorCode: "
						+ is.getRefreshInfo().getStatusCode()
						+ " refreshInfo time: "
						+ new Date(is.getRefreshInfo().getLastUpdatedTime()
								.longValue() * 1000));

				if (includeItemAccounts) {
					ItemData id = is.getItemData();
					if (id != null) {
						com.yodlee.soap.collections.List itemAccounts = id
								.getAccounts();
						for (int accts = 0; accts < itemAccounts.getElements().length; accts++) {
							if (is.getContentServiceInfo() != null
									&& is.getContentServiceInfo()
											.getContainerInfo() != null
									&& is.getContentServiceInfo()
											.getContainerInfo()
											.getContainerName()
											.equals(ContainerTypesHelper.LOAN)) {
								LoanLoginAccountData loanLoginAccountData = (LoanLoginAccountData) itemAccounts
										.getElements(accts);
								if (loanLoginAccountData != null) {
									com.yodlee.soap.collections.List loans = loanLoginAccountData
											.getLoans();
									if (loans != null
											&& loans.getElements().length > 0) {
										for (int l = 0; l < loans.getElements().length; l++) {
											Loan loan = (Loan) loans
													.getElements(l);
											if (loan.getItemAccountId() != null) {
												System.out
														.println("\tItemAccountId: "
																+ loan.getItemAccountId());
											}
										}
									}
								}
							} else {
								try {
									ItemAccountData iad = (ItemAccountData) itemAccounts
											.getElements(accts);
									System.out.println("\tItemAccountId: "
											+ iad.getItemAccountId());
								} catch (Exception e) {
								}
							}
						}
					}
				}
			}
		}
	}

	/**
	 * Edit the account credentials of an item. This lists the items the user
	 * has and prompts to edit one. *
	 * <p>
	 * 
	 * @param userContext
	 *            context of the user
	 */
	public void editItem(UserContext userContext) {
		viewItems(userContext, false);
		System.out.print("Enter ItemId: ");
		long itemId = IOUtils.readInt();

		// Prompt user to re-enter credentials
		List fieldInfoList = FormUtil.getUserInputFieldInfoList(userContext,
				getLoginFormCredentialsForItem(userContext, itemId));

		// Edit item with new credentials
		System.out.println("\nUpdating item");
		try {
			itemManagement.updateCredentialsForItem1(
					userContext,
					new Long(itemId),
					new com.yodlee.soap.collections.List(fieldInfoList
							.toArray()), true);
			System.out
					.println("Updated itemId with new credentials: " + itemId);
			RefreshItem refreshItem = new RefreshItem();
			if (refreshItem.pollRefreshStatus(userContext, new Long(itemId))) {
				AccountSummary accountSummary = new AccountSummary();
				accountSummary.displayItemSummary(userContext, itemId);
			}
		} catch (Exception coreEx) {
			coreEx.printStackTrace();
			throw new RuntimeException(
					"Unable to add item for content service!");
		}
	}

	/**
	 * Disable an item in the Yodlee system This prints a list of items the user
	 * has and prompts them to remove one of the items.
	 * <p>
	 * 
	 * @param userContext
	 *            context of the user
	 */
	public void removeItem(UserContext userContext) {
		viewItems(userContext, false);

		// Prompt for Item to Remove
		System.out.print("Enter itemId to be removed: ");

		int itemId = IOUtils.readInt();

		// Remove Item
		try {
			itemManagement.removeItem(userContext, new Long(itemId));
			System.out.println("Successfully removed itemId: " + itemId);
		} catch (Exception coreEx) {
			System.out.println("Unable to remove item" + coreEx.getMessage());
		}
	}

	/**
	 * Get refresh information for a user's item
	 * 
	 * @param userContext
	 *            userContext
	 * @param itemId
	 *            itemId to display refresh information on
	 * @return MFA Refresh Info object or null
	 */
	public RefreshInfo getRefreshInfo(UserContext userContext, long itemId) {
		ArrayOflong itemIds = new ArrayOflong(new Long[] { new Long(itemId) });
		// RefreshInfo[] ris = refresh.getRefreshInfo(items);
		ArrayOfRefreshInfo ris = null;
		try {
			ris = refresh.getRefreshInfo1(userContext, itemIds);
		} catch (Exception e) {
			throw new RuntimeException("Error fetching the Refresh Information");
		}
		for (int i = 0; i < ris.getElements().length; i++) {
			RefreshInfo ri = ris.getElements(i);
			Long timeUpdated = ri.getLastUpdatedTime();
			System.out.println("timeUpadted = " + timeUpdated);
			if (ri.getItemId().longValue() == itemId) {
				return ri;
			}
		}
		return null;
	}

	/**
	 * This method is to find the MFAType of a content service given the itemId
	 * and Usercontext
	 * 
	 * @param userContext
	 *            the user to check MFA type for
	 * @param itemId
	 *            the item to check the MFA type for
	 * @return the MFA type as string
	 */
	private String getMFATypeForItemId(UserContext userContext,
			CobrandContext cobrandContext, long itemId) {
		String mfatype = null;
		try {
			ItemSummary itemSummary = dataService.getItemSummaryForItem(
					userContext, new Long(itemId));
			Long contentserviceId = itemSummary.getContentServiceId();
			mfatype = getMFAType(cobrandContext, contentserviceId);
		} catch (Exception e) {
			throw new RuntimeException("Error fetching the Item [" + itemId
					+ "]");
		}
		return mfatype;
	}

}
