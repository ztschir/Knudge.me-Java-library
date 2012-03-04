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
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.xml.rpc.ServiceException;

import com.yodlee.sampleapps.helper.ContainerTypesHelper;
import com.yodlee.sampleapps.helper.FormUtil;
import com.yodlee.sampleapps.helper.IOUtils;
import com.yodlee.soap.collections.ArrayOflong;
import com.yodlee.soap.collections.common.ArrayOfContentServiceInfo;
import com.yodlee.soap.collections.common.ArrayOfFieldInfo;
import com.yodlee.soap.collections.core.routingnumberservice.ArrayOfRoutingNumberInfo;
import com.yodlee.soap.collections.core.verification.ArrayOfItemVerificationData;
import com.yodlee.soap.common.ContentServiceInfo;
import com.yodlee.soap.common.FieldInfo;
import com.yodlee.soap.common.Form;
import com.yodlee.soap.common.UserContext;
import com.yodlee.soap.core.ContentServiceNotFoundExceptionFault;
import com.yodlee.soap.core.CoreExceptionFault;
import com.yodlee.soap.core.IllegalArgumentValueExceptionFault;
import com.yodlee.soap.core.InvalidCobrandContextExceptionFault;
import com.yodlee.soap.core.InvalidCobrandConversationCredentialsExceptionFault;
import com.yodlee.soap.core.InvalidConversationCredentialsExceptionFault;
import com.yodlee.soap.core.InvalidItemExceptionFault;
import com.yodlee.soap.core.InvalidUserContextExceptionFault;
import com.yodlee.soap.core.StaleConversationCredentialsExceptionFault;
import com.yodlee.soap.core.UnsupportedCoreOperationExceptionFault;
import com.yodlee.soap.core.accountmanagement.itemmanagement.ItemManagement;
import com.yodlee.soap.core.accountmanagement.itemmanagement.ItemManagementServiceLocator;
import com.yodlee.soap.core.mfarefresh.MFARefreshInfo;
import com.yodlee.soap.core.refresh.RefreshStatus;
import com.yodlee.soap.core.refresh.refresh.Refresh;
import com.yodlee.soap.core.refresh.refresh.RefreshServiceLocator;
import com.yodlee.soap.core.routingnumberservice.InvalidRoutingNumberExceptionFault;
import com.yodlee.soap.core.routingnumberservice.RoutingNumberInfo;
import com.yodlee.soap.core.routingnumberservice.RoutingNumberNotFoundExceptionFault;
import com.yodlee.soap.core.routingnumberservice.routingnumberservice.RoutingNumberService;
import com.yodlee.soap.core.routingnumberservice.routingnumberservice.RoutingNumberServiceServiceLocator;
import com.yodlee.soap.core.verification.AccountVerificationData;
import com.yodlee.soap.core.verification.BankAccountVerificationData;
import com.yodlee.soap.core.verification.IAVDataRequestStatus;
import com.yodlee.soap.core.verification.IAVRefreshStatus;
import com.yodlee.soap.core.verification.ItemVerificationData;
import com.yodlee.soap.core.verification.extendedinstantverificationdataservice.ExtendedInstantVerificationDataService;
import com.yodlee.soap.core.verification.extendedinstantverificationdataservice.ExtendedInstantVerificationDataServiceServiceLocator;
import com.yodlee.soap.core.verification.instantverificationdataservice.InstantVerificationDataService;
import com.yodlee.soap.core.verification.instantverificationdataservice.InstantVerificationDataServiceServiceLocator;
import com.yodlee.soap.ext.traversal.contentservicetraversal.ContentServiceTraversal;
import com.yodlee.soap.ext.traversal.contentservicetraversal.ContentServiceTraversalServiceLocator;

/**
 * Instant Account Verification (IAV) Sample Code
 *
 * This sample oode uses the instant account verication apis found in:
 *  com.yodlee.core.verification.*
 *  com.yodlee.core.routingnumberservice.*
 */
public class IAV extends ApplicationSuper {

	protected static int SLEEP_MILLIS = 10 * 1000;
    public static long REFRESH_TIMEOUT_MIILIS = 5 * 60 * 1000; //5 minutes
    protected InstantVerificationDataService iavds;
    protected RoutingNumberService rns;
    protected ContentServiceTraversal contentServiceTraversal;
    protected ExtendedInstantVerificationDataService  mfaiavds;
    protected Refresh refresh;
    protected static ItemManagement itemManagement;
    protected IAVRefreshStatus iavrs;
    
    static {
    	ItemManagementServiceLocator locator = new ItemManagementServiceLocator();
        String serviceName = locator.getItemManagementServiceWSDDServiceName();
        locator.setItemManagementServiceEndpointAddress(System.getProperty("com.yodlee.soap.services.url")
						+ "/" + serviceName);
        try {
        	itemManagement = locator.getItemManagementService();
		} catch (ServiceException se) {
			throw new RuntimeException(se);
		}
    }
    
    public IAV ()
    {
    	super();
    	InstantVerificationDataServiceServiceLocator locator1 = new InstantVerificationDataServiceServiceLocator();
        String serviceName1 = locator1.getInstantVerificationDataServiceWSDDServiceName();
        locator1.setInstantVerificationDataServiceEndpointAddress(System.getProperty("com.yodlee.soap.services.url")
						+ "/" + serviceName1);
        try {
        	iavds = locator1.getInstantVerificationDataService();
		} catch (ServiceException se) {
			throw new RuntimeException(se);
		}
		ContentServiceTraversalServiceLocator locator2 = new ContentServiceTraversalServiceLocator();
        String serviceName2 = locator2.getContentServiceTraversalServiceWSDDServiceName();
        locator2.setContentServiceTraversalServiceEndpointAddress(System.getProperty("com.yodlee.soap.services.url")
						+ "/" + serviceName2);
        try {
        	contentServiceTraversal = locator2.getContentServiceTraversalService();
		} catch (ServiceException se) {
			throw new RuntimeException(se);
		}
		
		ExtendedInstantVerificationDataServiceServiceLocator locator3 = new ExtendedInstantVerificationDataServiceServiceLocator();
        String serviceName3 = locator3.getExtendedInstantVerificationDataServiceWSDDServiceName();
        locator3.setExtendedInstantVerificationDataServiceEndpointAddress(System.getProperty("com.yodlee.soap.services.url")
						+ "/" + serviceName3);
        try {
        	mfaiavds = locator3.getExtendedInstantVerificationDataService();
		} catch (ServiceException se) {
			throw new RuntimeException(se);
		}
		RoutingNumberServiceServiceLocator locator4 = new RoutingNumberServiceServiceLocator();
        String serviceName4 = locator4.getRoutingNumberServiceWSDDServiceName();
        locator4.setRoutingNumberServiceEndpointAddress(System.getProperty("com.yodlee.soap.services.url")
						+ "/" + serviceName4);
        try {
        	rns = locator4.getRoutingNumberService();
		} catch (ServiceException se) {
			throw new RuntimeException(se);
		}
		
		RefreshServiceLocator locator5 = new RefreshServiceLocator();
        String serviceName5 = locator5.getRefreshServiceWSDDServiceName();
        locator5.setRefreshServiceEndpointAddress(System.getProperty("com.yodlee.soap.services.url")
						+ "/" + serviceName5);
        try {
        	refresh = locator5.getRefreshService();
		} catch (ServiceException se) {
			throw new RuntimeException(se);
		}			
    }
    
    /**
     * Get Login Form For Conent Service
     * @param userContext
     * @param csId
     * @return Form
     * @throws RemoteException 
     * @throws IllegalArgumentValueExceptionFault 
     * @throws InvalidCobrandContextExceptionFault 
     * @throws CoreExceptionFault 
     * @throws InvalidCobrandConversationCredentialsExceptionFault 
     * @throws InvalidConversationCredentialsExceptionFault 
     * @throws StaleConversationCredentialsExceptionFault 
     * @throws ContentServiceNotFoundExceptionFault 
     */
    public static Form getLoginFormForContentService(UserContext userContext, long csId) throws ContentServiceNotFoundExceptionFault, StaleConversationCredentialsExceptionFault, InvalidConversationCredentialsExceptionFault, InvalidCobrandConversationCredentialsExceptionFault, CoreExceptionFault, InvalidCobrandContextExceptionFault, IllegalArgumentValueExceptionFault, RemoteException {
        return itemManagement.getLoginFormForContentService(userContext, csId);
    }

    /**
     * Get Login Form for Item Id
     * @param userContext
     * @param itemId
     * @return Form
     * @throws RemoteException 
     * @throws InvalidItemExceptionFault 
     * @throws UnsupportedCoreOperationExceptionFault 
     * @throws InvalidUserContextExceptionFault 
     * @throws IllegalArgumentValueExceptionFault 
     * @throws CoreExceptionFault 
     * @throws InvalidConversationCredentialsExceptionFault 
     * @throws StaleConversationCredentialsExceptionFault 
     */
    public static Form getLoginFormCredentialsForItem(UserContext userContext, long itemId) throws StaleConversationCredentialsExceptionFault, InvalidConversationCredentialsExceptionFault, CoreExceptionFault, IllegalArgumentValueExceptionFault, InvalidUserContextExceptionFault, UnsupportedCoreOperationExceptionFault, InvalidItemExceptionFault, RemoteException {        
        return itemManagement.getLoginFormCredentialsForItem(userContext, itemId);
    }

    /**
     * Adds a Member Item associated with a particular ContentService.
     *
     * @param userContext - The user's context.
     * @param contentServiceId - The identifier of the ContentService associated
     * with the Member Item being added.
     * @return  The identifier of the Item being added.
     */
    public long addItem (UserContext userContext, long contentServiceId, FieldInfo[] fieldsInfos)
    //public long addItem (UserContext userContext, long contentServiceId, List fieldsInfos)
	{
	System.out.println("\nAdding item for verification");
	long itemId = 0;
 
	try {
		ArrayOfFieldInfo arrayOfFieldInfo = new ArrayOfFieldInfo();
		arrayOfFieldInfo.setElements(fieldsInfos);
		iavrs = mfaiavds.addItemAndStartVerificationDataRequest(userContext, contentServiceId, arrayOfFieldInfo,"","");
		            
	} catch (Exception coreEx) {
	    coreEx.printStackTrace ();
	    throw new RuntimeException ("Unable to add item for content service!");
	}
	RefreshStatus refreshStatus = iavrs.getRefreshStatus();
	itemId = iavrs.getItemId();		 
    //Check if IAV request has been sent successfully and requires the intermediate flow ( in case of MFA requests)
	if (refreshStatus.toString().equals(RefreshStatus._SUCCESS_REFRESH_WAIT_FOR_MFA)) {
		//
		//Get the MFA response from the agent, which contains the MFA questions
		//The questions will be placed in the MQ and the app or SDK calls can poll for these questions continuously
		MFARefreshInfo mfaInfo;
		try {
			mfaInfo = refresh.getMFAResponse(userContext, new Long(itemId));
		} catch (Exception coreEx) {
		    coreEx.printStackTrace ();
		    throw new RuntimeException ("Unable to get MFA response for content service!");
		}
		MFA mfa = new MFA();
		int errorCode = mfa.processMFA(userContext, mfaInfo, itemId);
		if ( errorCode == 0) {
			System.out.println("MFA site verified successfully");
		} else if ( errorCode > 0 ){
			System.out.println("There was an error during the verification of the MFA site. Error code is " + errorCode );
		} 
		return itemId;
	} else if ( refreshStatus.toString().equals(RefreshStatus._SUCCESS_START_REFRESH)) {
		// This is a simple login/Persistent MFA site
	 } else if (refreshStatus.toString().equals(RefreshStatus._ITEM_CANNOT_BE_REFRESHED )) {
		 System.out.println("There was an error while verifying an item");
	 } else if (refreshStatus.toString().equals(RefreshStatus._UNSUPPORTED_OPERATION_FOR_CUSTOM_ITEM)) {
	 }  
	
    System.out.println("Successfully created itemId: " + itemId);
	return itemId;
	}
    /**
     * Updates the credentials information for an existing Member Item, given the
     * Member Item identifier.
     *
     * @param userContext - The user's context.
     * @param itemId -  The identifier of the Member Item being updated.
     */
    public void updateItem (UserContext userContext, long itemId, FieldInfo[] fieldInfos)
    {
        System.out.println("\nUpdating item");
        try {
        	ArrayOfFieldInfo arrayOfFieldInfo = new ArrayOfFieldInfo();
        	arrayOfFieldInfo.setElements(fieldInfos);
        	iavds.updateItemCredentialsAndStartVerificationDataRequest(userContext, itemId, arrayOfFieldInfo);
          

        } catch (Exception coreEx) {
            coreEx.printStackTrace ();
            throw new RuntimeException ("Unable to add item for content service!");
        }
        System.out.println("Successfully updated itemId: " + itemId);
    }


    public static FieldInfo[]  convertListToArray(List fieldInfoList)
    {
        if(fieldInfoList == null || fieldInfoList.size() == 0){
            return new FieldInfo[0];
        }
        FieldInfo[] fi =   new FieldInfo[fieldInfoList.size()] ;
        Iterator it = fieldInfoList.iterator();
        int i = 0;
        while(it.hasNext()){
            fi[i] = (FieldInfo)it.next() ;
            i++;
        }

        return fi;
    }

    /**
     * Poll for verification status
     *
     * @param userContext
     * @param itemId
     * @return AccountVerificaitonData
     */
    public AccountVerificationData[] poll(UserContext userContext, long itemId)
    {
        Long[] itemIds = { new Long(itemId)};
        long startTime = (new Date()).getTime();
        long currTime = startTime;


        while (startTime - currTime < REFRESH_TIMEOUT_MIILIS) {
            System.out.println ("\tChecking the refresh status ... ");
            ItemVerificationData[] ids =  null;
            ArrayOfItemVerificationData idsArray =  null;
            try {
            	ArrayOflong arrayOflong = new ArrayOflong();
            	arrayOflong.setElements(itemIds);
            	idsArray = iavds.getItemVerificationData(userContext, arrayOflong);
            	
            	ids = idsArray.getElements();
            } catch (Exception e) {
            	e.printStackTrace ();
    	        throw new RuntimeException ("Unable to get Item Verification Data!");
			}
            ItemVerificationData id = (ItemVerificationData)ids[0];

            if(id.getItemVerificationInfo().getRequestStatus().equals(IAVDataRequestStatus.IN_PROGRESS)) {

            }else if(id.getItemVerificationInfo().getRequestStatus().equals(IAVDataRequestStatus.FAILED)){
                System.out.println ("\tRefresh failed with status code " + id.getItemVerificationInfo().getStatusCode() );
                return null;
            }else if(id.getItemVerificationInfo().getRequestStatus().equals(IAVDataRequestStatus.SUCCEEDED)){
                System.out.println("\tThe refresh has completed successfully.");
                return id.getAccountVerificationData().getElements();
            }
            try{
                Thread.sleep(SLEEP_MILLIS);
            }catch(Exception e){
                return null;
            }
        }
        System.out.println ("\tThe refresh has timed out.");
        return null;
    }

    public void verifyAccount(AccountVerificationData[] avds)
    {
        if(avds != null && avds.length != 0){
            for(int i=0; i < avds.length; i++){
                AccountVerificationData avd = (AccountVerificationData)avds[i];
                System.out.println("Account Name: " + avd.getAccountName() );
                System.out.println("Account Number: " + avd.getAccountNumber() );
                System.out.println("Full Name: " + avd.getAccountHolder().getFullName() );
                System.out.println("Given Name: " + avd.getAccountHolder().getGivenName() );
                System.out.println("Sur Name: " + avd.getAccountHolder().getSurname() );
                if(avd instanceof BankAccountVerificationData) {
                    BankAccountVerificationData bvd = (BankAccountVerificationData)avd;
                    System.out.println("Bank Account Data:");
                    System.out.println("Available Balance: " + bvd.getAvailableBalance().getAmount() );
                    System.out.println("Account Type: " + bvd.getAccountType() );
                }
            }
        }

    }

    public void getRoutingNumbers()
    {
        System.out.println("Get All Routing Numbers Info's");
        RoutingNumberInfo[] rnis = null;
        ArrayOfRoutingNumberInfo  arrayOfRoutingNumberInfo = null;
        try {
        	rns.getAllRoutingNumberInfos(getCobrandContext());
        	displayRoutingNumberInfos(rnis);
        }catch(Exception e){
        	System.out.println("\tRouting Number not found");
        	return;
        }        

        // Get all Routing Numbers for City National Bank (3473)
        long csId = 3473;
        System.out.println("\nGet All Routing Number Info's for CsId=" + csId);
        try{
        	arrayOfRoutingNumberInfo = rns.getRoutingNumberInfosByContentServiceId(getCobrandContext(), csId) ;
        	if (arrayOfRoutingNumberInfo != null){
        		rnis = arrayOfRoutingNumberInfo.getElements();
        	}
	        displayRoutingNumberInfos(rnis);
        }catch(Exception csnf){
            System.out.println("\tRouting number not found");
        }

        // Get all Routing numbers for BofA (not CA) (11014)
        csId=11014;
        System.out.println("\nGet All Routing Number Info's for CsId=" + csId);
        try{
        	arrayOfRoutingNumberInfo = rns.getRoutingNumberInfosByContentServiceId(getCobrandContext(), csId) ;
        	
        	if (arrayOfRoutingNumberInfo != null){
        		rnis = arrayOfRoutingNumberInfo.getElements();
        	}
        	displayRoutingNumberInfos(rnis);
        }catch(Exception csnf){
        	System.out.println("\tRouting number not found");
        }

        // Get ContentServiceId for Routing number
        String rn = "122016066";
        System.out.println("\nGet ContentService for routing number=" + rn);
        try{
	        ContentServiceInfo csi = rns.getContentServiceInfoByRoutingNumber(getCobrandContext(), rn);
	        if(csi != null ){
	            System.out.println("\t" + csi.getContentServiceDisplayName() + "(" + csi.getContentServiceId() + ")");
	        }
        }catch(InvalidRoutingNumberExceptionFault irn){
            System.out.println("\tInvalid Routing Number");
        }catch(RoutingNumberNotFoundExceptionFault rnnf){
            System.out.println("\tRouting Number not found");
        }catch(Exception csnf){
        	System.out.println("\tRouting number not found");
        }

    }

    public static void displayRoutingNumberInfos(RoutingNumberInfo[] rnis){
        if(rnis !=null && rnis.length > 0){
            for(int i=0; i<rnis.length; i++){
                RoutingNumberInfo rni = (RoutingNumberInfo)rnis[i]  ;
                System.out.println("\t" + rni.getFinancialInstitutionInfo().getName()
                    + " Routing Number=" + rni.getRoutingNumber() + " CsId=" + rni.getContentServiceId() );
            }
        }
    }
    
    /**
     * This method cycles through all IAV-enabled forms inside of
     * a cobrand and writes out the HTML version of those forms.
     * 
     * @param userContext the user context for writing out the form.
     */
    public void writeAllIavForms(UserContext userContext) {
    	
    	System.out.print("\nEnter a comma separated list of Content Service Ids, or simply \n hit [Return] for ALL IAV supported bank institutions: ");
        String[] contentServicesString = IOUtils.readStr("0").split(",");
    	Long contentServiceArray[] = new Long[contentServicesString.length]; 

    	for (int i=0; i<contentServicesString.length;i++) {
    		contentServiceArray[i] = new Long(contentServicesString[i].trim());
    	}
    	ContentServiceInfo[] contentServiceInfoArray = null;
    	ArrayOfContentServiceInfo contentServiceInfos;
    	if (contentServicesString[0].equals("0")){
    		try {
    			contentServiceInfos = contentServiceTraversal
						.getContentServicesByContainerType3(
								getCobrandContext(), ContainerTypesHelper.BANK,
								new Integer(0));
    		} catch (Exception e) {
				throw new RuntimeException(e.getMessage());
			}
    	
    	} else {
        	try {
        		ArrayOflong arrayOflong = new ArrayOflong();
        		arrayOflong.setElements(contentServiceArray);
        		contentServiceInfos =
    			contentServiceTraversal.getContentServiceInfos1(getCobrandContext(), arrayOflong, new Integer(0));
        		
        	} catch (Exception e) {
				throw new RuntimeException(e.getMessage());
			}
    	}
    	if (contentServiceInfos != null) {
    		contentServiceInfoArray = contentServiceInfos.getElements();
    	}
    	
    	if (contentServiceInfoArray != null){
    	for(int i=0; i<contentServiceInfoArray.length; i++) {
    		if(contentServiceInfoArray[i].isIsIAVFastSupported()) {
    			try {
    			Form iavForm = getLoginFormForContentService(userContext, 
    								contentServiceInfoArray[i].getContentServiceId());
    			FormUtil.writeFormHtml(userContext, 
    					contentServiceInfoArray[i].getContentServiceId(), 
    					iavForm,
    					false,
    					"IAVTEST");
    			} catch (Exception e) {
    				System.out.println("Error writing IAV form");
				}    			
    		}
    	 }
    	}
    }
    public void doIAV(UserContext userContext) {
        System.out.print("\nEnter Content Service Id: ");
        int csId = IOUtils.readInt();
        
        // Prompt user to enter credentials
        List fieldInfoList = null;
        try {
        fieldInfoList = FormUtil.getUserInputFieldInfoList(userContext,
                getLoginFormForContentService(userContext, csId));
        } catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
        
        // Add item with credentials
        long itemId = addItem(userContext, csId, convertListToArray(fieldInfoList) );

        boolean failure = true;
        while(failure){
            AccountVerificationData[] avds = poll(userContext, itemId);
            if (avds != null) {
                // Verify Account
                verifyAccount(avds);
                failure = false;
                break;
            }
            RefreshItem ri = new RefreshItem();        
            String mfatype = ri.getMFAType(userContext, itemId);            
            if (  mfatype != null )  {
            	//come out of the loop for MFA site
            	failure = false;
            	break;
            } else {
            	//Skip the update call for MFA sites
	            System.out.print("\nFailure. Re-enter password? [yes]: " );
	            String ans = IOUtils.readStr("yes");
	            if(ans.equalsIgnoreCase("NO")){
	                break;
	            }
	            // Prompt user to re-enter credentials
	            try {
	            fieldInfoList = FormUtil.getUserInputFieldInfoList(userContext,
	                    getLoginFormCredentialsForItem(userContext, itemId));
	            } catch (Exception e) {
					throw new RuntimeException(e.getMessage());
				}
	
	            // Edit item with new credentials
	            updateItem(userContext, itemId, convertListToArray(fieldInfoList));
            }
        }

    }

}
