/*
 * Copyright 2009 Yodlee, Inc.  All Rights Reserved.  Your use of this code 
 * requires a license from Yodlee.  Any such license to this code is 
 * restricted to evaluation/illustrative purposes only. It is not intended 
 * for use in a production environment, and Yodlee disclaims all warranties 
 * and/or support obligations concerning this code, regardless of the terms 
 * of any other agreements between Yodlee and you."
 */
package YodleeSrc;

import java.rmi.RemoteException;

import javax.xml.rpc.ServiceException;

import com.yodlee.sampleapps.helper.ContainerTypesHelper;
import com.yodlee.sampleapps.helper.DataDumper;
import com.yodlee.soap.collections.List;
import com.yodlee.soap.common.ItemSummary;
import com.yodlee.soap.common.UserContext;
import com.yodlee.soap.core.CoreExceptionFault;
import com.yodlee.soap.core.IllegalArgumentTypeExceptionFault;
import com.yodlee.soap.core.IllegalArgumentValueExceptionFault;
import com.yodlee.soap.core.InvalidConversationCredentialsExceptionFault;
import com.yodlee.soap.core.InvalidUserContextExceptionFault;
import com.yodlee.soap.core.StaleConversationCredentialsExceptionFault;
import com.yodlee.soap.core.dataservice.IllegalDataExtentExceptionFault;
import com.yodlee.soap.core.dataservice.ItemData;
import com.yodlee.soap.core.dataservice.SummaryRequest;
import com.yodlee.soap.core.dataservice.dataservice.DataService;
import com.yodlee.soap.core.dataservice.dataservice.DataServiceServiceLocator;
import com.yodlee.soap.core.dataservice.types.InsuranceData;
import com.yodlee.soap.core.dataservice.types.InsuranceLoginAccountData;


/**
 * Displays a user's Insurance item data in the Yodlee software platform.
 *
 */
public class DisplayInsuranceData extends ApplicationSuper {

	protected DataService dataService;


    /**
     * Constructs an instance of the DisplayInsuranceData class that
     * displays Insurance accounts
     */
    public DisplayInsuranceData ()
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
    
    /**
     * Displays all the item summaries of insurance items of the user.
     * <p>
     * @param userContext The user context.
     */
    public void displayInsuranceData (UserContext userContext)
    {
        
    	/*SummaryRequest sr = new SummaryRequest(
                new String[] {ContainerTypes.INSURANCE },
                new DataExtent[] { DataExtent.getDataExtentForAllLevels() }
        );*/
    	SummaryRequest sr = new SummaryRequest();
    	List list = new List();
    	list.setElements(new String[] {ContainerTypesHelper.INSURANCE });
    	sr.setContainerCriteria(list);
    	
        Object[] itemSummaries = null;
        List itemSummariesList = null;
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
            System.out.println ("No insurance data available");
            return;
        }

        for (int i = 0; i < itemSummaries.length; i++) {
			ItemSummary is = (ItemSummary) itemSummaries[i];
            displayInsuranceDataForItem (is);

            // Dump the InsuranceLoginAccountData object.
            //dumpInsuranceDataForItem(is);
        }
    }

    /**
     * Dump the InsuranceLoginAccountData object.
     * @param is
     */
    public void dumpInsuranceDataForItem( ItemSummary is){

        ItemData id = is.getItemData();

        if(id == null){
           System.out.println("\tItemData == null");
        }else{
        	
            List accountsList = id.getAccounts();
            Object [] accounts = null;
            if (accountsList != null){
            	accounts = accountsList.getElements();
            }
            if (accounts == null || accounts.length == 0) {
                System.out.println ("\tNo accounts");
            }else {
                for (int accts = 0; accts < accounts.length; accts++) {
					InsuranceLoginAccountData insuranceLoginAccountData = (InsuranceLoginAccountData) accounts[accts];

                    System.out.println("\n\nDumping InsuranceLoginAccountData Object");
                    try{
                        DataDumper.dumper(insuranceLoginAccountData);
                    }catch(Exception e ){
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * Displays the item information and item data information
     * for the given insurance itemSummary.
     * <p>
     * @param is an itemSummary whose containerType is 'stocks'
     */
    public void displayInsuranceDataForItem (ItemSummary is)
    {
        String containerType = is.getContentServiceInfo ().
                getContainerInfo ().getContainerName ();
        //System.out.println("containerType = " + containerType );

        if (!containerType.equals(ContainerTypesHelper.INSURANCE )) {
            throw new RuntimeException ("displayInsurancDataForItem called with " +
                    "invalid container type: " + containerType);
        }

        DisplayItemInfo displayItemInfo = new DisplayItemInfo ();
        displayItemInfo.displayItemSummaryInfo (is);
        ItemData id = is.getItemData();

        if(id == null){
           System.out.println("ItemData == null");
        }else{
            List accountsList = id.getAccounts();
            Object[] accounts = null;
            if (accountsList != null){
            	accounts = accountsList.getElements();
            }

            if (accounts == null || accounts.length == 0) {
                System.out.println ("\tNo accounts");
            }else {
                System.out.println("\t**InsuranceLoginAccountData**");
                for (int accts = 0; accts < accounts.length; accts++) {
					InsuranceLoginAccountData ilad = (InsuranceLoginAccountData) accounts[accts];

                    // Get InsuranceData
					List insurancePolicysList = ilad.getInsurancePolicys();
					Object[] insurancePolicys = null;
					if (insurancePolicysList != null){
						insurancePolicys = insurancePolicysList.getElements();
					}
                    if (insurancePolicys == null || insurancePolicys.length == 0) {
                        System.out.println ("\t\tNo InsuranceData");
                    }else {
                        System.out.println("\t\t**InsuranceData**");
                        for (int policy = 0; policy < insurancePolicys.length; policy++) {
							InsuranceData insData = (InsuranceData) insurancePolicys[policy];
                            System.out.println("\t\tInsuranceData Account Number: " + insData.getAccountNumber() );
                            System.out.println("\t\tInsuranceData Cash Value: " + insData.getCashValue() );
                            System.out.println("\t\tInsuranceData Insurance Type: " + insData.getInsuranceType() );

                        }
                    }
                    System.out.println("");
                }
            }
        }
    }

    /**
     * Displays a user's Insurance item data in the Yodlee software platform.
     *
     */
    public
            static void main (String args[])
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
        DisplayInsuranceData insuranceData = new DisplayInsuranceData ();
        UserContext userContext = null;

        // Login the user
		System.out.println("Logging in user " + userName + " with password "
				+ password);
		userContext = loginUser.loginUser(userName, password);
		System.out.println("Login of user " + userName + " successful");

        // Display Insurance Account
        insuranceData.displayInsuranceData(userContext);

        // Log out the user
        System.out.println ("Logging out " + userName);
        loginUser.logoutUser (userContext);
        System.out.println ("Done logging out " + userName);
    }
}
