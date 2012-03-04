/*
 * Copyright 2009 Yodlee, Inc.  All Rights Reserved.  Your use of this code 
 * requires a license from Yodlee.  Any such license to this code is 
 * restricted to evaluation/illustrative purposes only. It is not intended 
 * for use in a production environment, and Yodlee disclaims all warranties 
 * and/or support obligations concerning this code, regardless of the terms 
 * of any other agreements between Yodlee and you."
 */
package com.yodlee.sampleapps;

import com.yodlee.soap.appext.fundstransfer.transfermanagement.directtransfermanagement.DirectTransferManagement;
import com.yodlee.soap.appext.fundstransfer.transfermanagement.directtransfermanagement.DirectTransferManagementServiceLocator;
import com.yodlee.soap.common.UserContext;
import com.yodlee.soap.core.fundstransfer.transferaccountmanagement.transferaccountmanagement.TransferAccountManagement;
import com.yodlee.soap.core.fundstransfer.transferaccountmanagement.transferaccountmanagement.TransferAccountManagementServiceLocator;
import com.yodlee.soap.core.routingnumberservice.routingnumberservice.RoutingNumberService;
import com.yodlee.soap.core.routingnumberservice.routingnumberservice.RoutingNumberServiceServiceLocator;
import com.yodlee.sampleapps.helper.IOUtils;
import com.yodlee.soap.core.itemaccountmanagement.itemaccountmanagement.ItemAccountManagement;
import com.yodlee.soap.core.itemaccountmanagement.itemaccountmanagement.ItemAccountManagementServiceLocator;
import com.yodlee.soap.core.fundstransfer.transfermanagement.transfermanagement.TransferManagement;
import com.yodlee.soap.core.fundstransfer.transfermanagement.transfermanagement.TransferManagementServiceLocator;

/**
 * Funds Transfer (FT) Sample Code. This class provides the starting point for
 * all the FT Related Sample Apps. The main functionality of this class is to
 * display a menu to the user and the perform the respective activity based on
 * the user's input chossen from the menu.
 */
public class FTDirect extends ApplicationSuper {
	/** Contains the RoutingNumberService */
	protected RoutingNumberService rns;

	/** Contains the TransferAccountManagement */
	protected TransferAccountManagement transferAccountManagement;

	/** Contains the DirectTransferManagement  */
	protected DirectTransferManagement directTransferManagement;
	
	/** Contains the ItemAccountManagement  */
	protected ItemAccountManagement  itemAccountManagement;
	
	/** Contains the TransferManagement */
	protected TransferManagement transferManagement;
	

	/** Details for Brokerage Account: Bank Name */
	protected String bankName;
	
	/** Navigation Counter. * */
	private static int optionCount = 1;

	/** Navigation Menu Choice. * */
	private static final int NAV_CREATE_TRANSFER = optionCount++;
	
	/** Navigation Menu Choice. * */
	private static final int NAV_MAKE_TRANSFER = optionCount++;

	/** Navigation Menu Choice. * */
	private static final int NAV_DEL_DEST_ACC = optionCount++;
	
	/** Navigation Menu Choice. * */
	private static final int NAV_QUIT = 0;

	/** Default Constructor to initialize all the proxies */
	public FTDirect() {
		/** Initialize RoutingNumberService */
//		Create RoutingNumberService Locator 
		RoutingNumberServiceServiceLocator routingNumberServiceServiceLocator = new RoutingNumberServiceServiceLocator();
		String routingNumberServiceServiceName = routingNumberServiceServiceLocator.getRoutingNumberServiceWSDDServiceName();
		routingNumberServiceServiceLocator.setRoutingNumberServiceEndpointAddress(System.getProperty("com.yodlee.soap.services.url") 
				+ "/" + routingNumberServiceServiceName);
		try{
			rns = routingNumberServiceServiceLocator.getRoutingNumberService();
		}catch(Exception lse) {

			}

		/** Initialize the TransferAccountManagement  */
//		Create TransferAccountManagement Locator 
		TransferAccountManagementServiceLocator transferAccountManagementServiceLocator = new TransferAccountManagementServiceLocator();
		String transferAccountManagementServiceName = transferAccountManagementServiceLocator.getTransferAccountManagementServiceWSDDServiceName();
		transferAccountManagementServiceLocator.setTransferAccountManagementServiceEndpointAddress(System.getProperty("com.yodlee.soap.services.url") 
				+ "/" + transferAccountManagementServiceName);
		try{
			transferAccountManagement = transferAccountManagementServiceLocator.getTransferAccountManagementService();
		}catch(Exception lse) {

			}
		
		/** Initialize the DirectTransferManagement  */
//		Create DirectTransferManagement Locator 
		DirectTransferManagementServiceLocator directTransferManagementServiceLocator = new DirectTransferManagementServiceLocator();
		String directTransferManagementServiceName = directTransferManagementServiceLocator.getDirectTransferManagementServiceWSDDServiceName();
		directTransferManagementServiceLocator.setDirectTransferManagementServiceEndpointAddress(System.getProperty("com.yodlee.soap.services.url") 
				+ "/" + directTransferManagementServiceName);
		try{
			directTransferManagement = directTransferManagementServiceLocator.getDirectTransferManagementService();
		}catch(Exception lse) {

			}
		
		/** Initialize the ItemAccountManagement  */
//		Create ItemAccountManagement Locator 
        ItemAccountManagementServiceLocator itemAccountManagementServiceLocator = new ItemAccountManagementServiceLocator();
		String itemAccountManagementServiceName = itemAccountManagementServiceLocator.getItemAccountManagementServiceWSDDServiceName();
		itemAccountManagementServiceLocator.setItemAccountManagementServiceEndpointAddress(System.getProperty("com.yodlee.soap.services.url") 
				+ "/" + itemAccountManagementServiceName);
		try{
			itemAccountManagement = itemAccountManagementServiceLocator.getItemAccountManagementService();
		}catch(Exception lse) {

			}
		
		/** Initialize the TransferManagement  */
//		Create TransferManagement Locator 
		TransferManagementServiceLocator transferManagementServiceLocator = new TransferManagementServiceLocator();
		String transferManagementServiceName = transferManagementServiceLocator.getTransferManagementServiceWSDDServiceName();
		transferManagementServiceLocator.setTransferManagementServiceEndpointAddress(System.getProperty("com.yodlee.soap.services.url") 
				+ "/" + transferManagementServiceName);
		try{
			transferManagement = transferManagementServiceLocator.getTransferManagementService();
		}catch(Exception lse) {

			}
		
	}
	

	/**
	 * Creates Transfer Request.
	 * 
	 * @param userContext
	 *            The user context of the caller
	 */
	public void createTrns(UserContext userContext) {
		FTDirectManagement fTDirectManagement = new FTDirectManagement();
		fTDirectManagement.createTransfer(userContext);
	}
	
	/**
	 * Make Transfer Request.
	 * 
	 * @param userContext
	 *            The user context of the caller
	 */
	public void makeTrns(UserContext userContext) {
		FTDirectManagement fTDirectManagement = new FTDirectManagement();
		fTDirectManagement.makeTransfer(userContext);
	}
	
	/**
	 * Delete Destination Account at Source Site
	 * 
	 * @param userContext
	 *            The user context of the caller
	 */
	public void delDestAccount(UserContext userContext) {
		FTDirectManagement fTDirectManagement = new FTDirectManagement();
		fTDirectManagement.deleteDestinationAccount(userContext);
	}

	/**
	 * Handles the submenu for Funds Transfer.
	 * 
	 * @param userContext
	 *            The user context of the caller
	 */
	public void doMenu(UserContext userContext) {
		boolean loop = true;
		int choice = 0;
		while (loop) {
			try {
				System.out.println("\nFunds Transfer Menu");
				System.out.println("" + NAV_CREATE_TRANSFER
						+ ". Create Trasfer Request");
				System.out.println("" + NAV_MAKE_TRANSFER
						+ ". Make Trasfer");
				System.out.println("" + NAV_DEL_DEST_ACC
						+ ". Delete Destination Accoutn at Source");
				
				System.out.println("" + NAV_QUIT + ". Exit");
				System.out.println("\n");
				System.out.print("Enter Choice : ");
				choice = IOUtils.readInt();

				if (choice == NAV_CREATE_TRANSFER)
					createTrns(userContext);
				if (choice == NAV_MAKE_TRANSFER)
					makeTrns(userContext);
				if (choice == NAV_DEL_DEST_ACC)
					delDestAccount(userContext);
				if (choice == NAV_QUIT)
					loop = false;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
