/*
 * Copyright 2009 Yodlee, Inc.  All Rights Reserved.  Your use of this code 
 * requires a license from Yodlee.  Any such license to this code is 
 * restricted to evaluation/illustrative purposes only. It is not intended 
 * for use in a production environment, and Yodlee disclaims all warranties 
 * and/or support obligations concerning this code, regardless of the terms 
 * of any other agreements between Yodlee and you.
 */
package com.yodlee.sampleapps;

import java.util.Calendar;

import com.yodlee.sampleapps.helper.IOUtils;
import com.yodlee.soap.common.ItemSummary;
import com.yodlee.soap.common.RefreshInfo;
import com.yodlee.soap.common.UserContext;
import com.yodlee.soap.core.login.InvalidUserCredentialsExceptionFault;

/**
 * A stand alone console application that performs many of the fundamental
 * Yodlee operations. It ties together many of the existing sample applications
 * 
 * 0.1 - Created
 * 
 */
public class YodleeConsole extends ApplicationSuper {
	/** Navigation Counter. * */
	private static int optionCount = 1;
	/** Navigation Menu Choice. * */
	private static final int NAV_REG_USER = optionCount++;
	/** Navigation Menu Choice. * */
	private static final int NAV_SSO_REG_USER = optionCount++;
	/** Navigation Menu Choice. * */
	private static final int NAV_LOGIN_USER = optionCount++;
	/** Navigation Menu Choice. * */
	private static final int NAV_SSO_LOGIN_USER = optionCount++;
	/** Navigation Menu Choice. * */
	private static final int NAV_MIGRATE = optionCount++;
	/** Navigation Menu Choice. * */
	private static final int NAV_BATCH_ACCOUNT_SUBMENU = optionCount++;
	/** Navigation Menu Choice. * */
	private static final int NAV_SESSIONLESSCALL_SUBMENU = optionCount++;

	/** Navigation Menu Choice. * */
	private static final int NAV_UNREGISTER_USER = optionCount++;

	/** Navigation Menu Choice. * */
	private static final int NAV_ITEM_MANAGEMENT = optionCount++;
	/** Navigation Menu Choice. * */
	private static final int NAV_MANAGE_ALERTS = optionCount++;
	/** Navigation Menu Choice. * */
	private static final int NAV_MANAGE_CONTENT_SERVICES = optionCount++;
	/** Navigation Menu Choice. * */
	private static final int NAV_MEM_PREFS = optionCount++;
	/** Navigation Menu Choice. * */
	private static final int NAV_PARTIAL_ACCOUNTS = optionCount++;
	/** Navigation Menu Choice. * */
	private static final int NAV_PFM_SUBMENU = optionCount++;
	/** Navigation Menu Choice. * */
	private static final int NAV_FUNDS_TANS_SUBMENU = optionCount++;
	/** Navigation Menu Choice. * */
	private static final int NAV_FT_DIRECT_SUBMENU = optionCount++;
	/** Navigation Menu Choice. * */
	private static final int NAV_ID_VERIFICATION_SUBMENU = optionCount++;
	/** Navigation Menu Choice. * */
	private static final int NAV_BILLPAY_SUBMENU = optionCount++;
	/** Navigation Menu Choice. * */
	private static final int NAV_IAV_SUBMENU = optionCount++;

	
	private static final int NAV_QUIT = 0;

	protected UserContext userContext;
	/** userName for the logged in user. */
	protected String userName;

	/**
	 * The main constructor for the Yodlee Console. This method uses super to
	 * get al the configuration parameters and initializes all the proxies.
	 */
	public YodleeConsole() {
		
		super();
		System.out.println("\n**** Server Version ****");
		//Server server = new Server();
		//server.printServerVersion();
		System.out.println("\n");
	}

	/**
	 * Prints the menu of options to the end user.
	 */
	public void printChoices() {
		System.out.println("********************");

		// This is not checking the session to see if it is valid
		// Do not use in real app.
		if (userContext != null) {
			System.out.println("Welcome " + userName);
		} else {
			System.out.println("Please Login.");
		}

		System.out.println("********************");
		System.out.println(NAV_REG_USER + ". Register User");
		System.out.println(NAV_SSO_REG_USER + ". SSO Register User");
		System.out.println(NAV_LOGIN_USER + ". Login User");
		System.out.println(NAV_SSO_LOGIN_USER + ". SSO Login User");
		System.out.println(NAV_MIGRATE + ". Migrate User");
		System.out.println(NAV_BATCH_ACCOUNT_SUBMENU + ". Batch Account [sub menu]");
		System.out.println(NAV_SESSIONLESSCALL_SUBMENU + ". Sessionless Call [sub menu]");

		if (userContext != null) {
			System.out.println(NAV_UNREGISTER_USER + ". Unregister User");
			System.out.println(NAV_ITEM_MANAGEMENT
					+ ". Item Management [sub menu]");
			System.out
					.println(NAV_MANAGE_ALERTS + ". Manage Alerts [sub menu]");
			System.out.println(NAV_MANAGE_CONTENT_SERVICES
					+ ". Manage Content Services [sub menu]");
			System.out.println(NAV_MEM_PREFS + ". Edit Mem Prefs [sub menu]");
			System.out.println(NAV_PARTIAL_ACCOUNTS
					+ ". Partial Account Management [sub menu]");
			System.out.println(NAV_PFM_SUBMENU + ". PFM Features [sub menu]");
			System.out.println(NAV_FUNDS_TANS_SUBMENU
					+ ". Display Fund Transfer [sub menu]");
			System.out
					.println(NAV_FT_DIRECT_SUBMENU + ". FT Direct [sub menu]");
			System.out.println(NAV_ID_VERIFICATION_SUBMENU
					+ ". ID Verification [sub menu]");
			System.out.println(NAV_BILLPAY_SUBMENU + ". Bill Pay [sub menu]");
			System.out.println(NAV_IAV_SUBMENU + ". IAV [sub menu]");

		}

		System.out.println(NAV_QUIT + ". Quit");
		System.out.println("********************");
	}

	private static int itemOptionCount = 1;
	/** Navigation Menu Choice. * */
	private static final int NAV_VIEW_ITEMS = itemOptionCount++;
	/** Navigation Menu Choice. * */
	private static final int NAV_VIEW_ITEM = itemOptionCount++;
	/** Navigation Menu Choice. * */
	private static final int NAV_DUMP_ITEM = itemOptionCount++;
	/** Navigation Menu Choice. * */
	private static final int NAV_ADD_ITEM = itemOptionCount++;
	/** Navigation Menu Choice. * */
	private static final int NAV_IAV_ITEM = itemOptionCount++;
	/** Navigation Menu Choice. * */
	private static final int NAV_IAV_FORM_DUMP = itemOptionCount++;
	/** Navigation Menu Choice. * */
	private static final int NAV_EDIT_ITEM = itemOptionCount++;
	/** Navigation Menu Choice. * */
	private static final int NAV_REMOVE_ITEM = itemOptionCount++;
	/** Navigation Menu Choice. * */
	private static final int NAV_REFRESH_ITEM = itemOptionCount++;
	/** Navigation Menu Choice. * */
	private static final int NAV_REFRESH_ALL = itemOptionCount++;

	public void itemManagementMenu() {

		System.out.println("********************");
		System.out.println(NAV_VIEW_ITEMS + ". View Items");
		System.out.println(NAV_VIEW_ITEM + ". View Item");
		System.out.println(NAV_DUMP_ITEM + ". Dump Item");
		System.out.println(NAV_ADD_ITEM + ". Add Item (Includes MFA)");
		System.out.println(NAV_IAV_ITEM + ". IAV Item (Includes MFA)");
		System.out.println(NAV_IAV_FORM_DUMP + ". IAV Dump Forms");
		System.out.println(NAV_EDIT_ITEM + ". Edit Item");
		System.out.println(NAV_REMOVE_ITEM + ". Remove Item");
		System.out.println(NAV_REFRESH_ITEM + ". Refresh Item");
		System.out.println(NAV_REFRESH_ALL + ". Refresh All");
		System.out.println(NAV_QUIT + ". Exit Sub-menu");
		System.out.println("********************");
		System.out.print("Choice: ");

		int choice = IOUtils.readInt();
		System.out.println();
		if (choice == NAV_VIEW_ITEMS) {
			viewItems();
		} else if (choice == NAV_VIEW_ITEM) {
			viewItem();
		} else if (choice == NAV_DUMP_ITEM) {
			dumpItem();
		} else if (choice == NAV_ADD_ITEM) {
			addItem();
		} else if (choice == NAV_IAV_ITEM) {
			iavItem();
		} else if (choice == NAV_IAV_FORM_DUMP) {
			IAV iav = new IAV ();
            iav.writeAllIavForms(userContext);
		} else if (choice == NAV_EDIT_ITEM) {
			editItem();
		} else if (choice == NAV_REMOVE_ITEM) {
			removeItem();
		} else if (choice == NAV_REFRESH_ITEM) {
			refreshItem();
		} else if (choice == NAV_REFRESH_ALL) {
			refreshAll();
		} else if (choice == NAV_QUIT) {
			return;
		}
	}

	/**
	 * Register User.
	 * 
	 * Prompt user to register a user
	 */
	public void registerUser() {
		userName = "user_" + Calendar.getInstance().getTime().getTime();
		System.out.print("Login [" + userName + "]: ");

		String input = IOUtils.readStr();

		if (input != null) {
			userName = input;
		}

		String password = "11aa11";
		System.out.print("Password [" + password + "]: ");
		input = IOUtils.readStr();

		if (input != null) {
			password = input;
		}

		String email = "diego@yodlee.com";
		System.out.print("E-mail [" + email + "]: ");
		input = IOUtils.readStr();

		if (input != null) {
			email = input;
		}

		System.out.println("Registering with:\n\tuserName=" + userName
				+ "\n\tpassword=" + password + "\n\temail=\"" + email + "\"");

		RegisterUser registerUser = new RegisterUser();

		
		userContext = registerUser.registerUser(userName, password, email);
		
		
	}

	/**
	 * Registered the user using the SSO method. This requires that the SAML
	 * libraries are available when the code is built.
	 */
	private void ssoRegisterUser() {
		RegisterUser registerUser = new RegisterUser();
		userName = registerUser.doSSORegistration(userContext);
	}

	/**
	 * Login User
	 * 
	 * Prompt user to login user.
	 * 
	 */
	public void loginUser() {
		System.out.print("Login: ");

        String input = IOUtils.readStr();
        String unvalidatedUserName = null;
        
        if (input != null) {
        	unvalidatedUserName = input;
        }

        String password = "";
        System.out.print("Password: ");
        input = IOUtils.readStr();

        if (input != null) {
            password = input;
        }

        LoginUser loginUser = new LoginUser();
            userContext = loginUser.loginUser(unvalidatedUserName, password);
        if (userContext != null ){
            userName = unvalidatedUserName;
            System.out.println(userName + " logged in");
        }

    }
    
	/**
	 * Logs the use in using SSO. This method requires that the SAML libraries
	 * are available when the code is built.
	 */
	public void ssoLoginUser() {
		LoginUser login = new LoginUser();		
		userContext = login.doSSOLoginUser();
	}

	/**
	 * Unregisters the active user from the system.
	 */
	public void unregisterUser() {
		System.out.print("Do you really wish to unregister user " + this.userName + "? [Y|N]");
        String input = IOUtils.readStr();
		
        if ("Y".equalsIgnoreCase(input)) {
        	RegisterUser registerUser = new RegisterUser();
        	registerUser.unregisterUser(userContext);
        	userContext = null;
            System.out.println("User " +this.userName + " has been unregistered.");
        } else {
            System.out.println("User " +this.userName + " has NOT been removed and will remained logged in.");
        }
	}

	/**
	 * Add Item and Promote to Payee
	 * 
	 * Prompt User to Add Item, if successful, add as Payees.
	 * 
	 */
	public void addItem() {
		System.out.print("Enter Content ServiceId: ");

        long csid = IOUtils.readInt();
        AddItem ad = new AddItem();
        long itemId = ad.doAddItem(userContext, csid);
        
     // Only continue if Item was added
        if (itemId != 0) {
            // Refresh new Item
            RefreshItem refreshItem = new RefreshItem();
            ContentServiceHelper csh = new ContentServiceHelper();
            String mfatype = csh.getMfAType(csid);
            if ( mfatype != null) {            	
            	refreshItem.refreshItem(userContext, itemId,true);
            } else {
            	refreshItem.refreshItem(userContext, itemId,false);
            }
            // Poll for the refresh status and display item
            // summary if refresh succeeds.
            if (refreshItem.pollRefreshStatus(userContext, itemId)) {
                AccountSummary as = new AccountSummary();
                as.displayItemSummary(userContext, itemId);
            }

            // Get RefreshInfo for item added
            RefreshInfo ri = refreshItem.getRefreshInfo(userContext, itemId);

            // Only Add Payees if item successfull refreshed
            /*if ((ri != null) && (ri.getStatusCode() == 0)) {
                // Only add Payee if Direct Card Payment is supported
                if (BillPayment.isDirectCardPaymentSupported(getCobrandContext(),
                            csid)) {
                    // Add Payees for this ItemId
                    BillPayment bp = new BillPayment();
                    bp.addPayees(userContext, itemId);
                }
            }*/
        }
		
	}

	public void iavItem() {
		IAV iav = new IAV ();
        iav.doIAV(userContext);
	}

	/**
	 * Edit item. This lists the items the user has and prompts to edit one.
	 */
	public void editItem() {
		AddItem addItem = new AddItem();
		addItem.doEditItem(userContext);
	}

	/**
	 * Allows removal of an item. This prints a list of items the user has and
	 * prompts them to remove one of the items.
	 */
	public void removeItem() {
		DisplayItemInfo displayItem = new DisplayItemInfo();
		Object[] itemSummaries = displayItem.fetchItemSummaries(userContext);
		if ((itemSummaries != null) && (itemSummaries.length > 0)) {
            // List Items
            for (int i = 0; i < itemSummaries.length; i++) {
                ItemSummary is = (ItemSummary) itemSummaries[i];
                System.out.println("itemId=" + is.getItemId() + " "
                    + is.getItemDisplayName());
            }

            // Prompt for Item to Remove
            System.out.print("Enter itemId to be removed: ");

            int itemId = IOUtils.readInt();
		
            // Remove Item
            AddItem ai = new AddItem();
            ai.removeItem(userContext, itemId);
        }
	}

	/**
	 * Refresh Item.
	 */
	public void refreshItem() {
		DisplayItemInfo displayItemInfo = new DisplayItemInfo();
		Object[] itemSummaries = displayItemInfo.fetchItemSummaries(userContext);
		if (itemSummaries != null) {
			for (int i = 0; i < itemSummaries.length; i++) {
				ItemSummary is = (ItemSummary) itemSummaries[i];
				System.out.println("itemId=" + is.getItemId() + " "
						+ is.getItemDisplayName());
			} 
			
			System.out.print("Enter ItemId: ");
		
	        long itemId = IOUtils.readInt();
	        RefreshItem ri = new RefreshItem();	        
        	String mfaType = ri.getMFAType(userContext, itemId);
            if (  mfaType != null ) {
            	//Refresh MFA sites
                ri.refreshItem(userContext, itemId,true);
            } else {
            	//Refresh Non-MFA sites
            	ri.refreshItem(userContext, itemId,false);
            }	        
            // Poll for the refresh status and display item
            // summary if refresh succeeds.
            if (ri.pollRefreshStatus(userContext, itemId)) {
                ri.displayItemSummary(userContext, itemId);
            }
		}
	}

	/**
	 * Refresh All Items.
	 */
	public void refreshAll() {
		RefreshAll ra = new RefreshAll();
        ra.refreshAll(userContext);
        // Poll for the refresh status and display item
        // summary if refresh succeeds.
        if (ra.pollRefreshStatus(userContext)) {
            ra.displayItemSummary(userContext);
        }
	}

	/**
	 * View Items.
	 */
	public void viewItems() {
		DisplayItemInfo dii = new DisplayItemInfo();
        dii.viewItems(userContext);
	}

	/**
	 * View a single item.
	 */
	public void viewItem() {
		System.out.print("Enter Item ID: ");
		
        long itemID = IOUtils.readLong();
        AccountSummary as = new AccountSummary();
        as.displayItemSummary(userContext, itemID);
	}

	/**
	 * Do a full dump of an item id.
	 */
	public void dumpItem() {
		DisplayItemInfo itemInfo = new DisplayItemInfo();
		itemInfo.dumpItem(userContext);
	}

	/**
	 * Call to manager alert. This method calls out to the alerts class to
	 * handle the code.
	 */
	public void manageAlerts() {
		AlertHelper alertHelper = new AlertHelper(userContext);
        alertHelper.manageAlerts();
	}

	public void contentServiceMenu() {
		ContentServiceHelper csh = new ContentServiceHelper();
        csh.contentServiceMenu();
	}

	public void memPrefsMenu() {
		UserPreferences up = new UserPreferences(userContext);
        up.userPreferencesMenu();
	}

	public void partialAccountsMenu() {
		PartialAccounts pam = new PartialAccounts(userContext);
        pam.partialAccountsMenu();
	}

	public void fundsTransferMenu() {
		FundsTransfer fundsTransfer = new FundsTransfer();
        fundsTransfer.doMenu(userContext);
	}

	public void ftDirectMenu() {
		FTDirect ftDirect = new FTDirect();
    	ftDirect.doMenu(userContext);
	}

	public void pfmManagementMenu() {
		PFMManagement pfmManagement = new PFMManagement();
    	pfmManagement.doPFMMenu(userContext);
	}
	public void sessionlessCallMenu() {
		SessionlessCall sessionlessCall = new SessionlessCall();
		sessionlessCall.doSessionlessCallMenu();
	}
	
	public void batchAccountMenu() {
		BatchAccount batchAccount = new BatchAccount();
		batchAccount.doMenu();
	}
	
	public void idVerificationMenu() {
		IDVerification idVerification = new IDVerification();
    	idVerification.doMenu(userContext);
	}

	public void billPayMenu() {
		BillPayManagement billPayManagement = new BillPayManagement();
    	billPayManagement.billPayOptionMenu(userContext);
	}
	
	public void iav(){
		IAVMenu iavMenu = new IAVMenu();
		iavMenu.doMenu(userContext);
	}

	public void migrate() {
		UserManagement userManagement = new UserManagement();
		userManagement.doMigrate(userContext);
	}


	/**
	 * Main Application Loop
	 * 
	 * Loops and prompts users to enter action.
	 * 
	 */
	public void loop() {
		boolean loop = true;

		while (loop) {
			// IOUtils.clrScrn();
			printChoices();
			System.out.print("Choice: ");

			int choice = IOUtils.readInt();
			System.out.println();

			try {
				if (choice == NAV_REG_USER) {
					registerUser();
				} else if (choice == NAV_SSO_REG_USER) {
					ssoRegisterUser();
				} else if (choice == NAV_LOGIN_USER) {
					loginUser();
				} else if (choice == NAV_SSO_LOGIN_USER) {
					ssoLoginUser();
				} else if (choice == NAV_BATCH_ACCOUNT_SUBMENU) {
					batchAccountMenu();
				} else if (choice == NAV_SESSIONLESSCALL_SUBMENU) {
					//System.out.println("PFM");
					sessionlessCallMenu();
					
				} else if (choice == NAV_UNREGISTER_USER) {
					unregisterUser();
				} else if (choice == NAV_ITEM_MANAGEMENT) {
					System.out.println("ITEM MANAGEMENT");
					itemManagementMenu();
				} else if (choice == NAV_MANAGE_ALERTS) {
					manageAlerts();
				} else if (choice == NAV_MANAGE_CONTENT_SERVICES) {
					contentServiceMenu();
				} else if (choice == NAV_MEM_PREFS) {
					memPrefsMenu();
				} else if (choice == NAV_PARTIAL_ACCOUNTS) {
					partialAccountsMenu();
				} else if (choice == NAV_FUNDS_TANS_SUBMENU) {
					fundsTransferMenu();
				} else if (choice == NAV_FT_DIRECT_SUBMENU) {
					ftDirectMenu();
				} else if (choice == NAV_ID_VERIFICATION_SUBMENU) {
					idVerificationMenu();
				} else if (choice == NAV_MIGRATE) {
					migrate();
				} else if (choice == NAV_PFM_SUBMENU) {
					System.out.println("PFM");
					pfmManagementMenu();
				} else if (choice == NAV_BILLPAY_SUBMENU) {
					System.out.println("BILL PAY");
					billPayMenu();
				} else if (choice == NAV_IAV_SUBMENU) {
					System.out.println("IAV");
					iav();
				} else if (choice == NAV_QUIT) {
					loop = false;
				} else {
					System.out.println("Invalid Entry!");
				}
			}  catch (Exception e) {
				e.printStackTrace();
			}
		}

		System.out.println("Exiting");
		System.exit(0);
	}

	/**
	 * Main method to run the console. No arguments are expected.
	 * 
	 * @param args
	 *            command line parameters are ignored
	 */
	public static void main(String[] args) {
		try {
			YodleeConsole bp = new YodleeConsole();

			// Begin main loop
			bp.loop();
		} catch (RuntimeException e) {
			if (e.getMessage() != null
					&& e.getMessage().indexOf("SSLHandshakeException") >= 0) {

				System.out.println("\n\n-----=====-----=====");
				System.out.println("The Sample Applications have encountered "
						+ "an SSLHandshakeException exception attempting to "
						+ "connect to the SOAP server.  The most likely "
						+ "cause to this is that Yodlee uses self-signed "
						+ "certificates in the staging environment that will "
						+ "trigger this error until the certificate is "
						+ "marked as trusted by importing it into your "
						+ "cacerts file.  Consult the README file or "
						+ "developer's guide for instructions on this "
						+ "process.");
				System.out.println("-----=====-----=====\n\n");
			} else {
				throw e;
			}

		}
	}

}