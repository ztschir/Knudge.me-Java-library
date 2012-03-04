/*
 * Copyright 2007 Yodlee, Inc.  All Rights Reserved.  Your use of this code 
 * requires a license from Yodlee.  Any such license to this code is 
 * restricted to evaluation/illustrative purposes only. It is not intended 
 * for use in a production environment, and Yodlee disclaims all warranties 
 * and/or support obligations concerning this code, regardless of the terms 
 * of any other agreements between Yodlee and you."
 */
package YodleeSrc;

import javax.xml.rpc.ServiceException;

import com.yodlee.soap.common.ItemSummary;
import com.yodlee.soap.common.UserContext;
import com.yodlee.soap.core.InvalidItemExceptionFault;
import com.yodlee.soap.core.dataservice.DataExtent;
import com.yodlee.soap.core.dataservice.SummaryRequest;
import com.yodlee.soap.core.dataservice.dataservice.DataService;
import com.yodlee.soap.core.dataservice.dataservice.DataServiceServiceLocator;

/**
 * Displays a user's Account Summary in the Yodlee software platform.
 *
 */
public class AccountSummary
{
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

    public void displayItemSummary (UserContext userContext, long itemId)
    {
    	DataExtent dataExtent = new DataExtent();
    	dataExtent.setStartLevel(new Integer(0));
    	dataExtent.setEndLevel(new Integer(Integer.MAX_VALUE));
        try {
            ItemSummary itemSummary =
                dataService.getItemSummaryForItem1(userContext, new Long(itemId), dataExtent) ;
            
            if(itemSummary == null) {
            	System.out.println("The given item is invalid.");
            } else {
	            String containerType = itemSummary.getContentServiceInfo ().
	                getContainerInfo().getContainerName ();
	            
	            if (containerType.equals (ContainerTypesHelper.BANK)) {
	                (new DisplayBankData()).displayBankDataForItem (itemSummary);
	            }else if(containerType.equals(ContainerTypesHelper.BILL)){
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
            }
        } catch (InvalidItemExceptionFault ex) {
            throw  new RuntimeException ("The given item is invalid.");
        } catch (Exception e) {
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
        sr.setContainerCriteria(
        		new com.yodlee.soap.collections.List(new String[] {  ContainerTypesHelper.BILL,
                		ContainerTypesHelper.CREDIT_CARD,
                		ContainerTypesHelper.INSURANCE,
                		ContainerTypesHelper.INVESTMENT,
                		ContainerTypesHelper.LOAN,
                		ContainerTypesHelper.BANK }));
        Object[] itemSummaries = null;
        try {
        	dataService.getItemSummaries1(userContext, sr);
        } catch (Exception e) {
			e.printStackTrace();
		}

        if (itemSummaries == null || itemSummaries.length == 0) {
            System.out.println ("No bills data available");
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
}

