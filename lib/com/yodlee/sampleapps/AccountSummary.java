/*
 * Copyright 2009 Yodlee, Inc.  All Rights Reserved.  Your use of this code 
 * requires a license from Yodlee.  Any such license to this code is 
 * restricted to evaluation/illustrative purposes only. It is not intended 
 * for use in a production environment, and Yodlee disclaims all warranties 
 * and/or support obligations concerning this code, regardless of the terms 
 * of any other agreements between Yodlee and you."
 */
package com.yodlee.sampleapps;

import java.rmi.RemoteException;

import javax.xml.rpc.ServiceException;

import com.yodlee.sampleapps.helper.ContainerTypesHelper;
import com.yodlee.soap.collections.List;
import com.yodlee.soap.common.ItemSummary;
import com.yodlee.soap.common.UserContext;
import com.yodlee.soap.core.dataservice.ItemData;
import com.yodlee.soap.core.CoreExceptionFault;
import com.yodlee.soap.core.IllegalArgumentTypeExceptionFault;
import com.yodlee.soap.core.IllegalArgumentValueExceptionFault;
import com.yodlee.soap.core.InvalidConversationCredentialsExceptionFault;
import com.yodlee.soap.core.InvalidItemExceptionFault;
import com.yodlee.soap.core.InvalidUserContextExceptionFault;
import com.yodlee.soap.core.StaleConversationCredentialsExceptionFault;
import com.yodlee.soap.core.dataservice.ContainerCriteria;
import com.yodlee.soap.core.dataservice.DataExtent;
import com.yodlee.soap.core.dataservice.IllegalDataExtentExceptionFault;
import com.yodlee.soap.core.dataservice.SummaryRequest;
import com.yodlee.soap.core.dataservice.dataservice.DataService;
import com.yodlee.soap.core.dataservice.dataservice.DataServiceServiceLocator;
import com.yodlee.soap.core.dataservice.types.ItemAccountData;
import com.yodlee.soap.core.dataservice.types.CardData;
import com.yodlee.soap.core.dataservice.types.InvestmentData;
import com.yodlee.soap.core.dataservice.types.BankData;

/**
 * Displays a user's Account Summary in the Yodlee software platform.
 *
 */
public class AccountSummary extends ApplicationSuper {

	protected DataService dataService;


    /**
     * Constructs an instance of the AccountSummary class that
     * displays a user's Account Summary
     */
    public AccountSummary ()
    {
        super ();
        DataServiceServiceLocator locator = new DataServiceServiceLocator();
        String serviceName = locator.getDataServiceWSDDServiceName();
        locator.setDataServiceEndpointAddress(System.getProperty("com.yodlee.soap.services.url")
						+ "/" + serviceName);
        try {
        	dataService = locator.getDataService();
		} catch (ServiceException se) {
			throw new RuntimeException(se);
		}
    }

    public static final int CardAccountLevel = 0;
    public void displayItemSummary (UserContext userContext, long itemId)
    {        
        	DataExtent dataExtent = new DataExtent();
             //dataExtent.setStartLevel(0);
        	//dataExtent.setStartLevel(0);
            //dataExtent.setEndLevel(Integer.MAX_VALUE);
            //dataExtent.setEndLevel(2);
       // dataExtent.setEndLevel(4);

        java.lang.Integer[]  i = new java.lang.Integer[] {0,2,4};

        // {InvestmentData.InvestmentAccountLevel,InvestmentData.InvestmentAccountHoldingLevel};;

        //int[] dataExtents = new int[] {InvestmentData.InvestmentAccountLevel,InvestmentData.InvestmentAccountHoldingLevel};
        //DataExtent dataExtent = new DataExtent();
          //dataExtent.setExtentLevels(i);
        //com.yodlee.soap.core.dataservice.types.CardData cd = new com.yodlee.soap.core.dataservice.types.CardData();

        //int[] dataExtents = new int[] {InvestmentData.InvestmentAccountLevel,InvestmentData.InvestmentAccountHoldingLevel};
        //DataExtent dataExtent = new DataExtent (dataExtents);
            com.yodlee.soap.collections.ArrayOfint t = new com.yodlee.soap.collections.ArrayOfint();
            t.setElements(i);
        	dataExtent.setExtentLevels (t);
            ItemSummary itemSummary;
			try {
				itemSummary = dataService.getItemSummaryForItem1(userContext, itemId, dataExtent );	
				if (itemSummary != null) {
		            String containerType = itemSummary.getContentServiceInfo ().
		                getContainerInfo().getContainerName ();
		            
		            if (containerType.equals (ContainerTypesHelper.BANK)) {
		                (new DisplayBankData ()).displayBankDataForItem (itemSummary);
		            } else if (containerType.equals(ContainerTypesHelper.BILL)
						|| containerType.equals(ContainerTypesHelper.TELEPHONE)) {		            	
		                (new DisplayBillsData ()).displayBillsDataForItem (itemSummary);
		            }else if(containerType.equals(ContainerTypesHelper.CREDIT_CARD)){
		                (new DisplayCardData ()).displayCardDataForItem (itemSummary);
		            }else if(containerType.equals(ContainerTypesHelper.INSURANCE)){
		                (new DisplayInsuranceData ()).displayInsuranceDataForItem (itemSummary);
		            }else if(containerType.equals(ContainerTypesHelper.INVESTMENT)){
		                (new DisplayInvestmentData ()).displayInvestmentDataForItem (itemSummary);
		            }else if(containerType.equals(ContainerTypesHelper.LOAN)){
		                (new DisplayLoanData ()).displayLoanDataForItem (itemSummary);
		            } else {
		                (new DisplayItemInfo ()).displayItemSummaryInfo (itemSummary);
		            }
				} else {
					System.out.println("No ItemSummary data available");
				}
			} catch (StaleConversationCredentialsExceptionFault e) {
				e.printStackTrace();
			} catch (InvalidConversationCredentialsExceptionFault e) {
				e.printStackTrace();
			} catch (CoreExceptionFault e) {
				e.printStackTrace();
			} catch (IllegalArgumentValueExceptionFault e) {
				e.printStackTrace();
			} catch (InvalidUserContextExceptionFault e) {
				e.printStackTrace();
			} catch (IllegalDataExtentExceptionFault e) {
				e.printStackTrace();
			} catch (InvalidItemExceptionFault e) {
				e.printStackTrace();
			} catch (RemoteException e) {
				e.printStackTrace();
			}
        
    }

    /**
     * Displays all the item summaries of the user.
     * <p>
     * @param userContext The user context.
     */
    public void displayAccountSummary (UserContext userContext)
    {

        SummaryRequest sr = new SummaryRequest();
        
        DataExtent dataExtent = new DataExtent();
    	dataExtent.setStartLevel(0);
    	dataExtent.setEndLevel(Integer.MAX_VALUE);
        ContainerCriteria billsCriteria = new ContainerCriteria();
        billsCriteria.setContainerType("bills");
        billsCriteria.setDataExtent(dataExtent);
        ContainerCriteria cardCriteria = new ContainerCriteria();
        cardCriteria.setContainerType("credits");
        cardCriteria.setDataExtent(dataExtent);
        ContainerCriteria insuranceCriteria = new ContainerCriteria();
        insuranceCriteria.setContainerType("insurance");
        insuranceCriteria.setDataExtent(dataExtent);
        ContainerCriteria investmentCriteria = new ContainerCriteria();
        investmentCriteria.setContainerType("stocks");
        investmentCriteria.setDataExtent(dataExtent);
        ContainerCriteria loansCriteria = new ContainerCriteria();
        loansCriteria.setContainerType("loans");
        loansCriteria.setDataExtent(dataExtent);
        ContainerCriteria bankCriteria = new ContainerCriteria();
        bankCriteria.setContainerType("bank");
        bankCriteria.setDataExtent(dataExtent);
        
        ContainerCriteria [] elements = new ContainerCriteria[] { 
				billsCriteria,
				cardCriteria,
				insuranceCriteria,
				investmentCriteria,
				loansCriteria,
				bankCriteria };
				
        com.yodlee.soap.collections.List list = new com.yodlee.soap.collections.List();
        list.setElements(elements);
        sr.setContainerCriteria(list);
        sr.setContentServiceInfoRequired(true);
        Object[] itemSummaries = null; 
 
        com.yodlee.soap.collections.List itemSummariesList = null;
        try {
        	itemSummariesList = dataService.getItemSummaries1(userContext, sr);
        	if (itemSummariesList != null){
        		itemSummaries = itemSummariesList.getElements();
        	}
		} catch (StaleConversationCredentialsExceptionFault e) {
			e.printStackTrace();
		} catch (InvalidConversationCredentialsExceptionFault e) {
			e.printStackTrace();
		} catch (CoreExceptionFault e) {
			e.printStackTrace();
		} catch (IllegalArgumentTypeExceptionFault e) {
			e.printStackTrace();
		} catch (IllegalArgumentValueExceptionFault e) {
			e.printStackTrace();
		} catch (InvalidUserContextExceptionFault e) {
			e.printStackTrace();
		} catch (IllegalDataExtentExceptionFault e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		}        
        if (itemSummaries == null || itemSummaries.length == 0) {
            System.out.println ("No account data available");
            return;
        }

        for (int i = 0; i < itemSummaries.length; i++) {
			ItemSummary is = (ItemSummary) itemSummaries[i];
            String containerName =
                    is.getContentServiceInfo().getContainerInfo().getContainerName();
            if(containerName.equalsIgnoreCase(ContainerTypesHelper.BANK) ){
                DisplayBankData displayBank = new DisplayBankData();
                displayBank.displayBankDataForItem(is);
            } else if(containerName.equalsIgnoreCase(ContainerTypesHelper.INVESTMENT) ){
                DisplayInvestmentData displayInvestment = new DisplayInvestmentData();
                displayInvestment.displayInvestmentDataForItem(is);
            } else if(containerName.equalsIgnoreCase(ContainerTypesHelper.INSURANCE) ){
                DisplayInsuranceData displayInsurance = new DisplayInsuranceData();
                displayInsurance.displayInsuranceDataForItem(is);
            } else if(containerName.equalsIgnoreCase(ContainerTypesHelper.LOAN) ){
                DisplayLoanData displayLoan = new DisplayLoanData();
                displayLoan.displayLoanDataForItem(is);
            } else if(containerName.equalsIgnoreCase(ContainerTypesHelper.CREDIT_CARD) ){
                DisplayCardData displayCard = new DisplayCardData();
                displayCard.displayCardDataForItem(is);
            } else if(containerName.equalsIgnoreCase(ContainerTypesHelper.BILL) ){
                DisplayBillsData displayBills = new DisplayBillsData();
                displayBills.displayBillsDataForItem(is);
            }else{
                System.out.println("Unsupported Container: "+ containerName );
            }
            System.out.println("");
        }
    }

    /**
     * Displays a user's Account Summary in the Yodlee software platform.
     *
     */
    public static void main (String args[])
    {
        if (args.length < 2) {
            throw new RuntimeException ("Usage: <username> <password>") ;
        }

        // Startup and initialize
        /*
        try {
            InitializationHelper.setup ();
        } catch (Exception startupEx) {
            System.out.println ("Unable to startup system: " + startupEx);
            System.exit (-1);
        }
        */

        String  userName   =   args[0] ;
        String  password   =   args[1] ;

        LoginUser loginUser = new LoginUser ();
        AccountSummary acctSum = new AccountSummary ();
        UserContext userContext = null;

        // Login the user
		System.out.println("Logging in user " + userName + " with password "
				+ password);
		userContext = loginUser.loginUser(userName, password);
		System.out.println("Login of user " + userName +
                    " successful");        

        // Display User's Account Summary
        acctSum.displayAccountSummary(userContext);

        // Log out the user
        System.out.println ("Logging out " + userName);
        loginUser.logoutUser (userContext);
        System.out.println ("Done logging out " + userName);
    }
    
    public boolean displayItemAccountIds(UserContext userContext, long itemId)
    {                
        boolean itemAccountsPresent = false;
		try {
			ItemSummary itemSummary = dataService.getItemSummaryForItem(userContext, new Long(itemId));	
			if (itemSummary != null) {
				ItemData id = itemSummary.getItemData();
				List AccountList = id.getAccounts();
				if (AccountList != null && AccountList.getElements() != null
						&& AccountList.getElements().length > 0) {
					itemAccountsPresent = true;
					Object[] itemAccounts = AccountList.getElements();
					for (int i=0; i<itemAccounts.length; i++) {
						System.out.println("ItemAccountId:" + ((ItemAccountData)itemAccounts[i]).getItemAccountId());
					}
				} else {
					System.out.println("No ItemAccounts available");
				}
			} else {
				System.out.println("No ItemSummary data available");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return itemAccountsPresent;
    }
}
