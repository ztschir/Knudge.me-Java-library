/*
 * Copyright 2009 Yodlee, Inc.  All Rights Reserved.  Your use of this code 
 * requires a license from Yodlee.  Any such license to this code is 
 * restricted to evaluation/illustrative purposes only. It is not intended 
 * for use in a production environment, and Yodlee disclaims all warranties 
 * and/or support obligations concerning this code, regardless of the terms 
 * of any other agreements between Yodlee and you."
 */
package com.yodlee.sampleapps;

import java.math.BigDecimal;
import java.util.HashMap;

import javax.xml.rpc.ServiceException;

import com.yodlee.sampleapps.helper.IOUtils;
import com.yodlee.soap.collections.core.alert.ArrayOfAlert;
import com.yodlee.soap.collections.core.alert.ArrayOfAlertDestination;
import com.yodlee.soap.collections.core.alert.ArrayOfAlertSubscription;
import com.yodlee.soap.collections.core.alert.ArrayOfItemAlertSettings;
import com.yodlee.soap.common.UserContext;
import com.yodlee.soap.core.alert.Alert;
import com.yodlee.soap.core.alert.AlertDestination;
import com.yodlee.soap.core.alert.AlertSettings;
import com.yodlee.soap.core.alert.AlertSubscription;
import com.yodlee.soap.core.alert.AlertType;
import com.yodlee.soap.core.alert.AlertTypeAlreadySubscribedExceptionFault;
import com.yodlee.soap.core.alert.AlertTypeNotAvailableExceptionFault;
import com.yodlee.soap.core.alert.AlertTypeNotAvailableForHeldAwayAccountExceptionFault;
import com.yodlee.soap.core.alert.AlertTypeNotAvailableForItemExceptionFault;
import com.yodlee.soap.core.alert.AlertTypeNotSubscribedExceptionFault;
import com.yodlee.soap.core.alert.BillDueAlertSettings;
import com.yodlee.soap.core.alert.CreditCardAPRAlertSettings;
import com.yodlee.soap.core.alert.CreditCardBalanceAlertSettings;
import com.yodlee.soap.core.alert.CreditLimitAlertSettings;
import com.yodlee.soap.core.alert.FixedIncomeMaturityAlertSettings;
import com.yodlee.soap.core.alert.HighSpendingAlertSettings;
import com.yodlee.soap.core.alert.IncorrectAlertSettingsExceptionFault;
import com.yodlee.soap.core.alert.ItemAlertSettings;
import com.yodlee.soap.core.alert.LargeCreditTransactionAlertSettings;
import com.yodlee.soap.core.alert.LargeDebitTransactionAlertSettings;
import com.yodlee.soap.core.alert.LoanMaturityAlertSettings;
import com.yodlee.soap.core.alert.MinutesUsedAlertSettings;
import com.yodlee.soap.core.alert.NewBillAlertSettings;
import com.yodlee.soap.core.alert.OverDraftAlertSettings;
import com.yodlee.soap.core.alert.PlanUsageAlertSettings;
import com.yodlee.soap.core.alert.RewardsPostedAlertSettings;
import com.yodlee.soap.core.alert.UserAlertSubscription;
import com.yodlee.soap.core.alert.alertinboxservice.AlertInboxService;
import com.yodlee.soap.core.alert.alertinboxservice.AlertInboxServiceServiceLocator;
import com.yodlee.soap.core.alert.alertsubscriptionmanagement.AlertSubscriptionManagement;
import com.yodlee.soap.core.alert.alertsubscriptionmanagement.AlertSubscriptionManagementServiceLocator;

/**
 * This helper class can be used to set various
 * alerts in the Yodlee system.
 */
public class AlertHelper {

	protected AlertSubscriptionManagement alertManagement;
    protected AlertInboxService alertInboxService;
    protected UserContext userContext;

    private static int OPTION_CNT = 1;
    private static int NAV_VIEW_ALERTS = OPTION_CNT++;
    private static int NAV_ADD_ITEM_ALERTS = OPTION_CNT++;
    private static int NAV_ADD_NEW_BILL_ALERT = OPTION_CNT++;
    private static int NAV_REMOVE_ALERT_SUBSCRIPTION = OPTION_CNT++;;
    private static int NAV_REMOVE_ITEM_FROM_ALERT_SUBSCRIPTION = OPTION_CNT++;

    private static int NAV_QUIT = 0;

    // a sample of valid alerts
    private static int OPTION_ALERT_CNT = 1;
    private static int ALERT_OVERDRAFT_PROTECTION = OPTION_ALERT_CNT++;
    private static int ALERT_LARGE_BANK_DEPOSIT_TRANSACTION= OPTION_ALERT_CNT++;
    private static int ALERT_LARGE_DEBIT_TRANSACTION = OPTION_ALERT_CNT++;
	private static int ALERT_CREDIT_LIMIT = OPTION_ALERT_CNT++;
    private static int ALERT_HIGH_SPENDING = OPTION_ALERT_CNT++;
    private static int ALERT_CREDITCARD_APR_CHANGED = OPTION_ALERT_CNT++;
    private static int ALERT_CREDITCARD_BALANCE_CHANGED = OPTION_ALERT_CNT++;
    private static int ALERT_MINUTES_USED  = OPTION_ALERT_CNT++;
    private static int ALERT_PLAN_USAGE = OPTION_ALERT_CNT++;
    private static int ALERT_BILL_DUE = OPTION_ALERT_CNT++;
    private static int ALERT_NEW_BILL = OPTION_ALERT_CNT++;
	private static int ALERT_REWARDS_POSTED_SETTINGS = OPTION_ALERT_CNT++;
	private static int ALERT_LOAN_MATURITY = OPTION_ALERT_CNT++;
    private static int ALERT_INVESTMENT_MATURITY = OPTION_ALERT_CNT++;


    public AlertHelper(UserContext userContext) {
        this.userContext = userContext;
        AlertSubscriptionManagementServiceLocator locator1 = new AlertSubscriptionManagementServiceLocator();
        String serviceName1 = locator1.getAlertSubscriptionManagementServiceWSDDServiceName();
        locator1.setAlertSubscriptionManagementServiceEndpointAddress(System.getProperty("com.yodlee.soap.services.url")
						+ "/" + serviceName1);
        try {
        	alertManagement = locator1.getAlertSubscriptionManagementService();
		} catch (ServiceException se) {
			throw new RuntimeException(se);
		}
		AlertInboxServiceServiceLocator locator2 = new AlertInboxServiceServiceLocator();
        String serviceName2 = locator2.getAlertInboxServiceWSDDServiceName();
        locator2.setAlertInboxServiceEndpointAddress(System.getProperty("com.yodlee.soap.services.url")
						+ "/" + serviceName2);
        try {
        	alertInboxService = locator2.getAlertInboxService();
		} catch (ServiceException se) {
			throw new RuntimeException(se);
		}
    }
    
    /**
     * Get a HashMap of AlertSubscriptions keyed off of AlertType
     */
    public HashMap getUserAlertSubscriptionMap() {
        AlertSubscription[] subscriptions = null;
        ArrayOfAlertSubscription  subscriptionArray = null;
        try {
        	subscriptionArray = alertManagement.getAlertSubscriptions(userContext);
        	if (subscriptionArray != null){
        		subscriptions = subscriptionArray.getElements();
        	}
        } catch (Exception e) {
			throw new RuntimeException("Error fetching alert subscriptions..." + e.getMessage());
		}

        // also building a hashmap of alert subscriptions keyed off of AlertType
        // This can be used for easier update and removal
        HashMap alertMap = new HashMap();
        if (subscriptions != null) {
            for(int i = 0;i<subscriptions.length;i++) {
                AlertSubscription subscription = subscriptions[i];
                alertMap.put(subscription.getAlertSettings().getAlertType(),
                        subscription);
            }
        }
        return alertMap;
    }

    public void manageAlerts() {
		boolean loop = true;
		int choice = 0;
		while (loop) {
	        System.out.println("Choose one of the alert management options");
	        System.out.println("********************");
	        System.out.println(NAV_VIEW_ALERTS + ". View Alerts");
	        System.out.println(NAV_ADD_ITEM_ALERTS + ". Set up an item alert");
	        System.out.println(NAV_ADD_NEW_BILL_ALERT + ". Add NEW_BILL alert");
	        System.out.println(NAV_REMOVE_ALERT_SUBSCRIPTION + ". Remove ENTIRE alert subscription");
	        System.out.println(NAV_REMOVE_ITEM_FROM_ALERT_SUBSCRIPTION + ". Remove item from alert subscription");
	        System.out.println(NAV_QUIT + ". Exit Sub-menu");
	        System.out.println("********************");

	        System.out.print("Choice: ");			
	        choice = IOUtils.readInt();
	        System.out.println();
	        try{
	            if (choice == NAV_VIEW_ALERTS) {
	                viewAlerts();
	            } else if (choice == NAV_ADD_ITEM_ALERTS) {
	                setAlertForItem();
	            } else if (choice == NAV_ADD_NEW_BILL_ALERT) {
	                addNewBillAlert();
	            } else if (choice == NAV_REMOVE_ALERT_SUBSCRIPTION) {
	                removeAlertSubscription();
	            } else if (choice == NAV_REMOVE_ITEM_FROM_ALERT_SUBSCRIPTION) {
	                removeItemFromAlertSubscription();
	            }else if (choice == NAV_QUIT) {
	                loop = false;
	            }else 
					 System.out.println("Invalid Entry!");
	            
	        }
	        catch (Exception e) {
	            System.out.println("Exception : " + e);
	            e.printStackTrace();
	        }
		}
    }


    public void printAlertChoices() {
        System.out.println("Please select the alert to perform the operation on:");
        System.out.println(ALERT_OVERDRAFT_PROTECTION + ". [Bank] Overdraft Protection Warning [" +
                AlertType.OVER_DRAFT + "]");
        System.out.println(ALERT_LARGE_BANK_DEPOSIT_TRANSACTION + ". [Credit/Bank] Large Deposit Transaction [" +
                AlertType.LARGE_CREDIT_TRANSACTION + "]");        
        System.out.println(ALERT_LARGE_DEBIT_TRANSACTION + ". [Credit/Bank] Large Debit Transaction [" +
                AlertType.LARGE_DEBIT_TRANSACTION + "]");
        System.out.println(ALERT_CREDIT_LIMIT + ". [Credit] Credit Limit [" +
                AlertType.CREDIT_LIMIT + "]");
        System.out.println(ALERT_HIGH_SPENDING + ". [Credit] High Spedning [" +
                AlertType.HIGH_SPENDING + "]");        
        System.out.println(ALERT_CREDITCARD_APR_CHANGED + ". [Credit] Credit Card APR Changed [" +
                AlertType.CREDITCARD_APR_CHANGED + "]");
        System.out.println(ALERT_CREDITCARD_BALANCE_CHANGED + ". [Credit] Credit Card Balance Changed [" +
                AlertType.CREDITCARD_BALANCE_CHANGED + "]");                   
        System.out.println(ALERT_MINUTES_USED + ". [Wireless] Minutes Used [" +
                AlertType.MINUTES_USED + "]");
        System.out.println(ALERT_PLAN_USAGE + ". [Wireless] Plan Usage [" +
                AlertType.PLAN_USAGE);
        System.out.println(ALERT_BILL_DUE + ". [Bill] BILL DUE ALERT [ " +
                AlertType.BILL_DUE + "]");        
        System.out.println(ALERT_NEW_BILL + ". [Bill] NEW BILL ALERT [ " +
                AlertType.NEW_BILL + "]");        
        System.out.println(ALERT_REWARDS_POSTED_SETTINGS + ". [Rewards] points posted setttings [" +
                AlertType.REWARDS_POSTED + "]");        
        System.out.println(ALERT_LOAN_MATURITY + ". [Loan] Loan Maturity [ " +
                AlertType.LOAN_MATURITY + "]");
        System.out.println(ALERT_INVESTMENT_MATURITY + ". [Investment] Investment Maturity [ " +
                AlertType.FIXED_INCOME_MATURITY + "]"); 
    }


    /**
     * Dump out all of a users alerts
     */
    public void viewAlerts() {
        AlertSubscription[] subscriptions = null;
        ArrayOfAlertSubscription subscriptionArray;
        try {
        	subscriptionArray = alertManagement.getAlertSubscriptions(userContext);
        	if(subscriptionArray != null){
        		subscriptions = subscriptionArray.getElements();
        	}
        } catch (Exception e) {
        	throw new RuntimeException("Error fetching alert subscriptions..." + e.getMessage());
		}
        System.out.println("Displaying all user alerts and destinations");
        printAlerts(subscriptions);
        printAlertDestinations();
        printInboxAlerts();
    }

    
    public void printInboxAlerts() {
        Alert[] alerts = null;
        ArrayOfAlert alertArray = null;
        try {
        	alertArray = alertInboxService.getAlerts(userContext);
        	if(alertArray != null){
        		alerts = alertArray.getElements();
        	}
        } catch (Exception e) {
			System.out.println("Error fetching Inbox alerts\n");
		}
        if (alerts == null || alerts.length == 0) {
            System.out.println("No Inbox Alerts for user");
        } else {
            System.out.println("User's Inbox Alerts");
            for (int i = 0; i<alerts.length;i++) {
                System.out.println("Alert Inbox [" + i + "] - " + alerts[i].getAlertRuleName());
            }
        }
        System.out.println("");
    }
    
    public void printAlertDestinations() {
        AlertDestination[] destinations = null;
        ArrayOfAlertDestination destinationArray = null;
        try {
        	destinationArray = alertManagement.getAlertDestinations(userContext);
        	if (destinationArray != null){
        		destinations = destinationArray.getElements();
        	}
        } catch (Exception e) {
			System.out.println("Error fetching alert destinations\n");
		}
        if (destinations == null || destinations.length == 0) {
            System.out.println("No Alert destinations set for user");
        } else {
            System.out.println("User's alert destinations");
            for (int i = 0; i<destinations.length;i++) {
                System.out.println("Alert Dest[" + i + "] - " + destinations[i].getName());
            }
            System.out.println("");
        }
    }

    public void printAlerts(AlertSubscription[] subscriptions) {
        if (subscriptions == null || subscriptions.length == 0) {
            System.out.println("User has no subscriptions\n");
        } else {
            for(int i = 0;i<subscriptions.length;i++) {
                //System.out.println("Alert Sub[" + i + "] - " + subscriptions[i].getAlertSettings().getAlertType().getValue());
                AlertSettings settings = subscriptions[i].getAlertSettings();
                if (settings == null) {
                    System.out.println("Alert Sub[" + i + "] - Alert settings is null");
                } else {
                    //System.out.println("Alert settings: " + settings.getAlertTypeStrCode());
                    System.out.println("Alert Sub[" + i + "] - Alert type: " + settings.getAlertType().getValue());
                }                
            }
            System.out.println("");
        }
    }

    /**
     * list all the alerts and give the user a choice to remove an entire
     * subscription.  NOTE: this will remove it from all the items that have it
     * added
     */
    public void removeAlertSubscription() {
        System.out.println("This will allow the removal of an alert subscription.  " +
                "NOTE: This will remove the subscription from all the items that have it added");
        viewAlerts();
        HashMap alertMap = getUserAlertSubscriptionMap();
        System.out.println("Select which subscription to remove:");
        printAlertChoices();
        System.out.print("Choice: >>" );
        int choice = IOUtils.readInt();

        AlertSubscription alertSubscription = null;
        if (choice == ALERT_OVERDRAFT_PROTECTION) {
            alertSubscription = (AlertSubscription)alertMap.get(AlertType.OVER_DRAFT);
        } else if (choice == ALERT_REWARDS_POSTED_SETTINGS) {
            alertSubscription = (AlertSubscription)alertMap.get(AlertType.REWARDS_POSTED);
        } else if (choice == ALERT_CREDIT_LIMIT) {
            alertSubscription = (AlertSubscription)alertMap.get(AlertType.CREDIT_LIMIT);
        } else if (choice == ALERT_HIGH_SPENDING) {
            alertSubscription = (AlertSubscription)alertMap.get(AlertType.HIGH_SPENDING);
        } else if (choice == ALERT_LARGE_DEBIT_TRANSACTION) {
            alertSubscription = (AlertSubscription)alertMap.get(AlertType.LARGE_DEBIT_TRANSACTION);
        } else if (choice == ALERT_CREDITCARD_APR_CHANGED) {
            alertSubscription = (AlertSubscription)alertMap.get(AlertType.CREDITCARD_APR_CHANGED);
        } else if (choice == ALERT_CREDITCARD_BALANCE_CHANGED) {
            alertSubscription = (AlertSubscription)alertMap.get(AlertType.CREDITCARD_BALANCE_CHANGED);
        } else if (choice == ALERT_MINUTES_USED) {
            alertSubscription = (AlertSubscription)alertMap.get(AlertType.MINUTES_USED);
        } else if (choice == ALERT_PLAN_USAGE) {
            alertSubscription = (AlertSubscription)alertMap.get(AlertType.PLAN_USAGE);
        } else if (choice == ALERT_BILL_DUE) {
            alertSubscription = (AlertSubscription)alertMap.get(AlertType.BILL_DUE);
        } else if (choice == ALERT_NEW_BILL) {
            alertSubscription = (AlertSubscription)alertMap.get(AlertType.NEW_BILL);
        } else if (choice == ALERT_LOAN_MATURITY) {
        	alertSubscription = (AlertSubscription)alertMap.get(AlertType.LOAN_MATURITY);
        } else if (choice == ALERT_INVESTMENT_MATURITY) {
        	alertSubscription = (AlertSubscription)alertMap.get(AlertType.FIXED_INCOME_MATURITY);
        } else if (choice == ALERT_LARGE_BANK_DEPOSIT_TRANSACTION) {
        	alertSubscription = (AlertSubscription)alertMap.get(AlertType.LARGE_CREDIT_TRANSACTION);
        } 

        if (alertSubscription == null) {
            System.out.println("User was not subscribed to that alert yet, nothing to remove");
        } else {
            try {
            	ArrayOfAlertSubscription arrayOfAlertSubscription = new ArrayOfAlertSubscription();
            	AlertSubscription[] alertSubscriptionArray = new AlertSubscription[]{alertSubscription};
            	arrayOfAlertSubscription.setElements(alertSubscriptionArray);
            	alertManagement.removeAlertSubscriptions(userContext, arrayOfAlertSubscription);

            	System.out.println("Alert subscription removed");
            } catch (AlertTypeNotSubscribedExceptionFault e) {
				System.out.println("Error: The AlertType is not subscribed\n");
			} catch (Exception e) {
				System.out.println("Error: Alert subscription could not be removed..." + e.getMessage());
			}            
        }
    }

    /**
     * list all the alerts and give the user a choice to remove an item
     * from an alert subscription
     * TODO - handle the case where a user hasn't added any items
     */
    public void removeItemFromAlertSubscription() {
        System.out.println("This will allow the removal of an item from an existing alert subscription");
        viewAlerts();
        HashMap alertMap = getUserAlertSubscriptionMap();
        System.out.println("Select which subscription to remove:");
        printAlertChoices();
        System.out.print("Choice: >>" );
        int choice = IOUtils.readInt();

        System.out.print("Enter the ITEM ID to remove from the alert : >> ");
        long itemId = IOUtils.readLong();

        AlertType alertType = null;
        if (choice == ALERT_OVERDRAFT_PROTECTION) {
            alertType = AlertType.OVER_DRAFT;
        } else if (choice == ALERT_REWARDS_POSTED_SETTINGS) {
            alertType = AlertType.REWARDS_POSTED;
        } else if (choice == ALERT_CREDIT_LIMIT) {
            alertType = AlertType.CREDIT_LIMIT;
        } else if (choice == ALERT_HIGH_SPENDING) {
            alertType = AlertType.HIGH_SPENDING;
        } else if (choice == ALERT_LARGE_DEBIT_TRANSACTION) {
            alertType = AlertType.LARGE_DEBIT_TRANSACTION;
        }  else if (choice == ALERT_CREDITCARD_APR_CHANGED) {
        	alertType = AlertType.CREDITCARD_APR_CHANGED;
        } else if (choice == ALERT_CREDITCARD_BALANCE_CHANGED) {
        	alertType = AlertType.CREDITCARD_BALANCE_CHANGED;
        }else if (choice == ALERT_MINUTES_USED) {
            alertType = AlertType.MINUTES_USED;
        } else if (choice == ALERT_PLAN_USAGE) {
            alertType = AlertType.PLAN_USAGE;
        } else if (choice == ALERT_LOAN_MATURITY) {
            alertType = AlertType.LOAN_MATURITY;
        } else if (choice == ALERT_BILL_DUE) {
            alertType = AlertType.BILL_DUE;
        } else if (choice == ALERT_NEW_BILL) {
            alertType = AlertType.NEW_BILL;
        } else if (choice == ALERT_INVESTMENT_MATURITY) {
        	alertType = AlertType.FIXED_INCOME_MATURITY;
        } else if (choice == ALERT_LARGE_BANK_DEPOSIT_TRANSACTION) {
        	alertType = AlertType.LARGE_CREDIT_TRANSACTION;
        } 

        AlertSubscription subscription = (AlertSubscription)alertMap.get(alertType);

        if (subscription == null) {
            System.out.println("User was not subscribed to that alert subscription, nothing to remove");
        } else {
            try {
	        	alertManagement.removeItemFromSubscription(userContext, itemId, alertType);
	            System.out.println("Item removed from alert subscription.");
            } catch (AlertTypeNotSubscribedExceptionFault e) {
				System.out.println("Error: The AlertType is not subscribed");
			} catch (Exception e) {
				System.out.println("Error: Could not remove Item from AlertSubscription..." + e.getMessage());
			}
        }
    }

    /**
     * View Items
     *
     */
    public void viewItems()
    {
        DisplayItemInfo dii = new DisplayItemInfo();
        dii.viewItems(userContext);
    }    

    /**
     * This method is specific to adding new bill alerts
     *
     */
    public void addNewBillAlert() {
        // This hashMap stores the current alert subscriptions for a user
        // We will use this map to determine whether we need to create
        // new UserAlertSubscription objects or not
        HashMap alertMap = getUserAlertSubscriptionMap();

        AlertType alertType = AlertType.NEW_BILL;
        System.out.println("Looking for subscription");
        AlertSubscription newBillAlert = (AlertSubscription)alertMap.get(alertType);        
        if (newBillAlert != null) {
            System.out.println("New bill alert is already set:");
        } else {
            AlertSettings alertSettings = new NewBillAlertSettings();
            // Use this alert management method because it will add the new bill alert
            // to every existing item and when true is passed in it will ensure this
            // alert is applied to every future item
            try {
            	alertManagement.addUserAlertSubscription(userContext,alertSettings);
            } catch ( AlertTypeAlreadySubscribedExceptionFault e) {
				System.out.println("Error: This alert has already been subscribed!!");
			} catch (IncorrectAlertSettingsExceptionFault e) {
				System.out.println("Error: The alert settings are incorrect!!");
			} catch (AlertTypeNotAvailableExceptionFault e) {
				System.out.println("Error: This alert is not enabled for this cobrand!!");
			} catch (Exception e) {
				throw new RuntimeException("Error adding New-Bill alert..." + e.getMessage());
			}
        }
        System.out.println("Successfully added new bill alert");
        alertMap = getUserAlertSubscriptionMap();
        newBillAlert = (AlertSubscription)alertMap.get(alertType);
        System.out.println("New bill alert: " + newBillAlert.getAlertSettings().getAlertType().getValue() + "\n");

    }

    /**
     * list all the alerts and give the user a choice to add one to an existing item
     * TODO - handle the case where a user hasn't added any items
     */
    public void setAlertForItem() {
        // This hashMap stores the current alert subscriptions for a user
        // We will use this map to determine whether we need to create
        // new UserAlertSubscription objects or not
        HashMap alertMap = getUserAlertSubscriptionMap();

        System.out.println("Set an alert for an item");
        viewItems();
        System.out.println("");


        System.out.print("Enter the ITEM ID to set an alert for: >> ");
        long itemId = IOUtils.readLong();

        // get all the alerts for this item
        AlertSubscription[] subscriptions = null;
        ArrayOfAlertSubscription subscriptionArray = null;
        try {
        	subscriptionArray = alertManagement.getAlertSubscriptions1(userContext, new Long(itemId));
        	if (subscriptionArray != null){
        		subscriptions = subscriptionArray.getElements();
        	}
        } catch (Exception e) {
			throw new RuntimeException("Error setting alert..." + e.getMessage());
		}
        System.out.println("That item has the following alerts -- ");
        printAlerts(subscriptions);

        System.out.println("Set another alert (updates are OK too...)");
        printAlertChoices();
        System.out.print("Choice: >>" );
        int choice = IOUtils.readInt();

        AlertSettings alertSettings = null;
        AlertType alertType = null;
        if (choice == ALERT_OVERDRAFT_PROTECTION) {
            System.out.print("Enter the bank balance you want to be notifed ");
            String input = IOUtils.readStr();
            BigDecimal threshold = new BigDecimal(input);
            alertType = AlertType.OVER_DRAFT;
            OverDraftAlertSettings overDraftAlertSettings = new OverDraftAlertSettings();
            overDraftAlertSettings.setThresholdAmount(threshold);
            alertSettings = overDraftAlertSettings;
        } else if (choice == ALERT_REWARDS_POSTED_SETTINGS) {
            System.out.print("Set your award pt threshold:");
            long input = IOUtils.readLong();
            alertType = AlertType.REWARDS_POSTED;
            RewardsPostedAlertSettings rewardsPostedAlertSettings = new RewardsPostedAlertSettings();
            rewardsPostedAlertSettings.setRewardPoints(new Long(input));
            alertSettings = rewardsPostedAlertSettings;
        } else if (choice == ALERT_CREDIT_LIMIT) {
            System.out.print("Enter your desired credit limit threshold:");
            String input = IOUtils.readStr();
            BigDecimal threshold = new BigDecimal(input);
            alertType = AlertType.CREDIT_LIMIT;
            CreditLimitAlertSettings creditLimitAlertSettings = new CreditLimitAlertSettings();
            creditLimitAlertSettings.setCreditLimitThreshold(threshold);
            alertSettings = creditLimitAlertSettings;
        } else if (choice == ALERT_HIGH_SPENDING) {
            System.out.print("Enter your high spending threshold:");
            String input = IOUtils.readStr();
            BigDecimal threshold = new BigDecimal(input);
            alertType = AlertType.HIGH_SPENDING;
            HighSpendingAlertSettings highSpendingAlertSettings = new HighSpendingAlertSettings();
            highSpendingAlertSettings.setHighSpendingThreshold(threshold);
            alertSettings = highSpendingAlertSettings;
        } else if (choice == ALERT_LARGE_DEBIT_TRANSACTION) {
            System.out.print("Enter your large debit txn threshold:");
            String input = IOUtils.readStr();
            BigDecimal threshold = new BigDecimal(input);
            // When you want to set a large Credit Card transaction, that is
            // actually considered a debit on the credit card, so you
            // need to set the LARGE_DEBIT_TRANSACTION type.
            alertType = AlertType.LARGE_DEBIT_TRANSACTION;
            LargeDebitTransactionAlertSettings largeDebitTransactionAlertSettings = new LargeDebitTransactionAlertSettings();
            largeDebitTransactionAlertSettings.setTransactionAmountThreshold(threshold);
            alertSettings = largeDebitTransactionAlertSettings;
        } else if (choice == ALERT_CREDITCARD_APR_CHANGED) {
            System.out.print("A subscription to this AlertType will trigger an alert whenever the credit card's APR undergoes any change. \n");
            alertType = AlertType.CREDITCARD_APR_CHANGED;
            alertSettings = new CreditCardAPRAlertSettings();
        } else if (choice == ALERT_CREDITCARD_BALANCE_CHANGED) {
            System.out.print("A subscription to this AlertType will trigger an alert whenever the consumer's credit card balance \nexceeds the certain percentage of the overall available credit card limit");
            System.out.print("\nEnter your percentage threshold:");
            String input = IOUtils.readStr();
            float threshold = (new Long(input)).floatValue();
            alertType = AlertType.CREDITCARD_BALANCE_CHANGED;
            CreditCardBalanceAlertSettings creditCardBalanceAlertSettings = new CreditCardBalanceAlertSettings();
            creditCardBalanceAlertSettings.setThresholdParam(new Float(threshold));
            alertSettings = creditCardBalanceAlertSettings;
        } else if (choice == ALERT_MINUTES_USED) {
            System.out.print("Enter your minutes remaining amount (integer):");
            int input = IOUtils.readInt();
            alertType = AlertType.MINUTES_USED;
            MinutesUsedAlertSettings minutesUsedAlertSettings = new MinutesUsedAlertSettings();
            minutesUsedAlertSettings.setMinutesUsed(new Integer(input));
            alertSettings = minutesUsedAlertSettings;
        } else if (choice == ALERT_PLAN_USAGE) {
            System.out.print("Enter your minutes used amount (integer):");
            int input = IOUtils.readInt();
            alertType = AlertType.PLAN_USAGE;
            PlanUsageAlertSettings planUsageAlertSettings = new PlanUsageAlertSettings();
            planUsageAlertSettings.setMinutesRemaining(new Integer(input));
            alertSettings = planUsageAlertSettings;
        } else if (choice == ALERT_BILL_DUE) {
            System.out.print("Enter number of days in advance you want the alert (days)");
            int input = IOUtils.readInt();
            alertType = AlertType.BILL_DUE;
            BillDueAlertSettings billDueAlertSettings = new BillDueAlertSettings();
            billDueAlertSettings.setDaysAdvance(new Integer(input));
            alertSettings = billDueAlertSettings;
        } else if (choice == ALERT_NEW_BILL) {
            alertType = AlertType.NEW_BILL;
            alertSettings = new NewBillAlertSettings();
        } else if (choice == ALERT_LOAN_MATURITY) {
            System.out.print("Enter days in advance for loan maturity :");
            int input = IOUtils.readInt();
            alertType = AlertType.LOAN_MATURITY;
            LoanMaturityAlertSettings loanMaturityAlertSettings = new LoanMaturityAlertSettings();
            loanMaturityAlertSettings.setDaysAdvance(new Integer(input));            
            alertSettings = loanMaturityAlertSettings;
        } else if (choice == ALERT_INVESTMENT_MATURITY) {
        	System.out.print("Enter days in advance for Fixed Income maturity :");
            int input = IOUtils.readInt();
            alertType = AlertType.FIXED_INCOME_MATURITY;
            FixedIncomeMaturityAlertSettings 
            	fixedIncomeMaturityAlertSettings = new FixedIncomeMaturityAlertSettings();
            fixedIncomeMaturityAlertSettings.setDaysAdvance(new Integer(input));            
            alertSettings = fixedIncomeMaturityAlertSettings;
        } else if (choice == ALERT_LARGE_BANK_DEPOSIT_TRANSACTION) {
            System.out.print("Enter your large transaction threshold:");
            String input = IOUtils.readStr();
            BigDecimal threshold = new BigDecimal(input);
            // When you want to set a large Deposit transaction, that is
            // actually considered a credit on the credit card, so you
            // need to set the LARGE_CREDIT_TRANSACTION type.
            alertType = AlertType.LARGE_CREDIT_TRANSACTION;
            LargeCreditTransactionAlertSettings 
            	largeCreditTransactionAlertSettings = new LargeCreditTransactionAlertSettings();
            largeCreditTransactionAlertSettings.setTransactionAmountThreshold(threshold);
            alertSettings = largeCreditTransactionAlertSettings;
        }


        if (alertSettings == null) {
            System.out.println("Invalid choice...no alert to set");
        } else {
        	ItemAlertSettings itemAlertSettings = new ItemAlertSettings();
        	itemAlertSettings.setItemId(new Long(itemId));
        	itemAlertSettings.setAlertSettings(alertSettings);
            ItemAlertSettings[] itemAlertSettingsList = {itemAlertSettings};
            System.out
					.println("ItemAlertSetting to add: "
							+ (itemAlertSettingsList[0].getAlertSettings()
									.getAlertType() != null ? itemAlertSettingsList[0]
									.getAlertSettings().getAlertType()
									.getValue()
									: ""));
            AlertSubscription subscription = (AlertSubscription)alertMap.get(alertType);
            //System.out.println("Isubscription: " + subscription);
            try {
	            if (subscription == null) {
	                // the user does not yet have that subscription, need to add it
	            	ArrayOfItemAlertSettings arrayOfItemAlertSettings = new ArrayOfItemAlertSettings();
	            	arrayOfItemAlertSettings.setElements(itemAlertSettingsList);
	            	UserAlertSubscription userAlertSubscription = new UserAlertSubscription();
	            	userAlertSubscription.setAlertSettings(alertSettings);
	            	userAlertSubscription.setItemAlertSettings(arrayOfItemAlertSettings);
	            	subscription = userAlertSubscription;
	                AlertSubscription [] newSubscriptions = {subscription};
	                ArrayOfAlertSubscription arrayOfAlertSubscription = new ArrayOfAlertSubscription();
	                arrayOfAlertSubscription.setElements(newSubscriptions);
	                alertManagement.addAlertSubscriptions(userContext, arrayOfAlertSubscription);

	            } else {
	                // user has that subscription, just do an update
	                alertManagement.updateItemSettingsForSubscription(userContext,
	                    itemId, alertSettings);
	            }
            } catch (AlertTypeAlreadySubscribedExceptionFault e) {
				System.out.println("Error: This Alert Type is already subscribed");
			} catch (AlertTypeNotAvailableForHeldAwayAccountExceptionFault e) {
				System.out.println("Error: This Alert Type is not available for Held-Away account");
			} catch (AlertTypeNotSubscribedExceptionFault e) {
				System.out.println("Error: This Alert Type is not subscribed");
			} catch (AlertTypeNotAvailableForItemExceptionFault e) {
				System.out.println("Error: This Alert Type is not available for the Item " + itemId);
			} catch (Exception e) {
				throw new RuntimeException("Error setting alert..." + e.getMessage());
			}
            // reget the updated subscription and print it out
            alertMap = getUserAlertSubscriptionMap();
            subscription = (AlertSubscription)alertMap.get(alertType);
            System.out.println("Done setting alert: " + subscription.getAlertSettings().getAlertType().getValue() + "\n");
        }
    }

}
