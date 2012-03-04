/*
 * Copyright 2009 Yodlee, Inc.  All Rights Reserved.  Your use of this code 
 * requires a license from Yodlee.  Any such license to this code is 
 * restricted to evaluation/illustrative purposes only. It is not intended 
 * for use in a production environment, and Yodlee disclaims all warranties 
 * and/or support obligations concerning this code, regardless of the terms 
 * of any other agreements between Yodlee and you."
 */
package com.yodlee.sampleapps;

import java.util.List;

import javax.xml.rpc.ServiceException;

import com.yodlee.sampleapps.helper.FormUtil;
import com.yodlee.sampleapps.helper.IOUtils;
import com.yodlee.soap.common.Form;
import com.yodlee.soap.common.UserContext;
import com.yodlee.soap.core.accountmanagement.itemmanagement.ItemManagement;
import com.yodlee.soap.core.accountmanagement.itemmanagement.ItemManagementServiceLocator;

/**
 * Displays all the Content Services in the Yodlee software platform and allows
 * a user to add an item to any one of those services.
 */
public class AddItem extends ApplicationSuper {

	protected ItemManagement itemManagement;
	
	/**
     * Constructs an instance of the AddItem class that
     * provides the functionality to display all content.
     */
    public AddItem ()
    {
        super ();
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
    
    /**
     * Remove Item
     */
    protected long removeItem (UserContext userContext, long itemId)
    {
        try {
            itemManagement.removeItem (userContext, itemId);
            System.out.println("Successfully removed itemId: " + itemId);
            } catch (Exception coreEx) {
            System.out.println("Unable to remove item" + coreEx.getMessage());
        }
        return itemId;
    }
    
    /**
     * Adds the desired item for the user.
     * @param userContext
     * @param contentServiceId
     * @return itemId
     */
    private long addItem (UserContext userContext, long contentServiceId, List fieldsInfos)
    {
        System.out.println("\nAdding item");
        long itemId = 0;
        try {
        	com.yodlee.soap.collections.List list = new com.yodlee.soap.collections.List();
        	list.setElements(fieldsInfos.toArray());
        	itemId = itemManagement.addItemForContentService1 (userContext, contentServiceId, list, false, false );
 
        } catch (Exception coreEx) {
            coreEx.printStackTrace ();
            throw new RuntimeException ("Unable to add item for content service!");
        }
        System.out.println("Successfully created itemId: " + itemId);
        return itemId;
    }
    
    /**
     * Add the item
     * This primarily uses the FormUtil class to prompt the user
     * for their usernames and passwords and create the appropriate fieldInfoList.
     *
     * As a convenience it will also write out the populated HTML to disk
     */
    public long doAddItem(UserContext userContext, long contentServiceId){
        // Prompt user to enter credentials
        List fieldInfoList = FormUtil.getUserInputFieldInfoList(userContext,
                getLoginFormForContentService(userContext, contentServiceId));        
        long itemId = addItem (userContext, contentServiceId, fieldInfoList);        

        // Write out the form HTML to disk
        /**
         * commenting out temporarily 
         * as the itemManagement.getLoginFormCredentialsForItem(userContext, new Long(itemId));
         *  is not working now
         */
		
		FormUtil.writeFormHtml(userContext, itemId,
				getLoginFormCredentialsForItem(userContext, itemId), false,
				"FilledForm");
				
        return itemId;
    }
    
    /**
     * Get Login Form For Conent Service
     * @param userContext
     * @param csId
     * @return Form
     */
    public Form getLoginFormForContentService(UserContext userContext, long csId) {
    	
    	Form  form = null;
    	
    	try {
    		long startTime = System.currentTimeMillis();
    		form = itemManagement.getLoginFormForContentService(userContext, csId);
            long endTime = System.currentTimeMillis();
    		
    		System.out.println("Total time take to fetch the login form(in millis) - " + (endTime - startTime) );
    		
    		return form;
        } catch (Exception e) {
        	throw new RuntimeException("Error fetching login form for this CsId");
        }
    }
    
    /**
     * Get Login Form for Item Id
     * @param userContext
     * @param itemId
     * @return Form
     */
    public Form getLoginFormCredentialsForItem(UserContext userContext, long itemId) {
    	try {
    		return itemManagement.getLoginFormCredentialsForItem(userContext, new Long(itemId));
    	} catch (Exception e) {
        	throw new RuntimeException("Error fetching login form credentials for this ItemId");
        }
    }
    
    /**
     * Update credentials for an Item
     * @param userContext
     * @param itemId
     */
    private void editItem (UserContext userContext, long itemId, List fieldInfoList)
    {
        System.out.println("\nAdding item");
        try {
            //itemId = itemManagement.addItemForContentService (userContext, contentServiceId, fieldInfoList, false, false);
        	com.yodlee.soap.collections.List list = new com.yodlee.soap.collections.List();
        	list.setElements(fieldInfoList.toArray());
        	itemManagement.updateCredentialsForItem1(userContext, new Long(itemId), list, false);
  
        } catch (Exception coreEx) {
            coreEx.printStackTrace ();
            throw new RuntimeException ("Unable to add item for content service!");
        }
        System.out.println("Updated itemId with new credentials: " + itemId);
        //return itemId;
    }
    
    public void doEditItem(UserContext userContext) {
    	System.out.print("Enter ItemId: ");

        long itemId = IOUtils.readInt();
        // Prompt user to re-enter credentials
        List fieldInfoList = FormUtil.getUserInputFieldInfoList(userContext,
                getLoginFormCredentialsForItem(userContext, itemId));

        // Edit item with new credentials
        editItem(userContext, itemId, fieldInfoList);

        // Refresh new Item
        RefreshItem refreshItem = new RefreshItem();
        String mfaType = refreshItem.getMFAType(userContext, itemId);
        if (  mfaType != null ) {
            refreshItem.refreshItem(userContext, itemId,true);
        } else {
        	refreshItem.refreshItem(userContext, itemId,false);
        }

        // Poll for the refresh status and display item
        // summary if refresh succeeds.
        if (refreshItem.pollRefreshStatus(userContext, itemId)) {
            refreshItem.displayItemSummary(userContext, itemId);
        }
    }
       
}
