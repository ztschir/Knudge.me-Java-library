/*
 * Copyright 2009 Yodlee, Inc.  All Rights Reserved.  Your use of this code 
 * requires a license from Yodlee.  Any such license to this code is 
 * restricted to evaluation/illustrative purposes only. It is not intended 
 * for use in a production environment, and Yodlee disclaims all warranties 
 * and/or support obligations concerning this code, regardless of the terms 
 * of any other agreements between Yodlee and you."
 */
package com.yodlee.sampleapps;

import com.yodlee.soap.common.CobrandContext;
import com.yodlee.soap.common.UserContext;
import com.yodlee.soap.core.dataservice.dataservice.DataServiceServiceLocator;
import com.yodlee.soap.core.identityverification.identityverificationmanagement.IdentityVerificationManagement;
import com.yodlee.soap.core.identityverification.identityverificationmanagement.IdentityVerificationManagementService;
import com.yodlee.soap.core.identityverification.identityverificationmanagement.IdentityVerificationManagementServiceLocator;
import com.yodlee.soap.core.identityverification.identityverificationservice.IdentityVerificationService;
import com.yodlee.soap.core.identityverification.identityverificationservice.IdentityVerificationServiceService;
import com.yodlee.soap.core.identityverification.identityverificationservice.IdentityVerificationServiceServiceLocator;
import com.yodlee.sampleapps.helper.IOUtils;

public class IDVerification extends ApplicationSuper {

	/** Contains the IdentityVerificationManagement proxy. */
	protected IdentityVerificationManagement identityVerificationManagement;

	/** Contains the IdentityVerificationService proxy. */
	protected IdentityVerificationService identityVerificationService;

	/** Navigation Counter. * */
	private static int optionCount = 1;

		/** Navigation Menu Choice. * */
	private static final int NAV_GET_USER_ID_VERIFICATION_STATUS = optionCount++;

	/** Navigation Menu Choice. * */
	private static final int NAV_GET_SSN_VERIVATION_STATUS = optionCount++;

	/** Navigation Menu Choice. * */
	private static final int NAV_GET_SSN_VERIVATION_STATUS_ACROSS_COBRANDS = optionCount++;

	/** Navigation Menu Choice. * */
	private static final int NAV_UPDATE_USER_SSN_VERIVATION_STATUS = optionCount++;

	/** Navigation Menu Choice. * */
	private static final int NAV_UPDATE_SSN_VERIVATION_STATUS_NONREG_USER = optionCount++;

	/** Navigation Menu Choice. * */
	private static final int NAV_GET_USER_PROFILE_DATA = optionCount++;

	/** Navigation Menu Choice. * */
	private static final int NAV_UPDATE_USER_PROFILE_DATA = optionCount++;

	/** Navigation Menu Choice. * */
	private static final int NAV_UPDATE_USER_VERIFICATION_DATA = optionCount++;

	/** Navigation Menu Choice. * */
	private static final int NAV_UPDATE_NON_REG_USER_VERIFICATION_DATA = optionCount++;

	/** Navigation Menu Choice. * */
	private static final int NAV_QUIT = 0;

	public IDVerification() {
		
		//Create IdVerificationManagement Locator 
		IdentityVerificationManagementServiceLocator identityVerificationManagementLocator= new IdentityVerificationManagementServiceLocator();
       String IdentityVerificationManagementServiceName= identityVerificationManagementLocator.getIdentityVerificationManagementServiceWSDDServiceName();
       identityVerificationManagementLocator.setIdentityVerificationManagementServiceEndpointAddress(System.getProperty("com.yodlee.soap.services.url")
				+ "/" + IdentityVerificationManagementServiceName);
       try {
    	   identityVerificationManagement = identityVerificationManagementLocator.getIdentityVerificationManagementService();

		} catch (Exception lse) {

		}

		//Create IdVerificationManagement Locator 
		IdentityVerificationServiceServiceLocator identityVerificationServiceLocator= new IdentityVerificationServiceServiceLocator();
       String identityVerificationServiceName= identityVerificationServiceLocator.getIdentityVerificationServiceWSDDServiceName();
       identityVerificationServiceLocator.setIdentityVerificationServiceEndpointAddress(System.getProperty("com.yodlee.soap.services.url")
				+ "/" + identityVerificationServiceName);
       try {
    	   identityVerificationService = identityVerificationServiceLocator.getIdentityVerificationService();

		} catch (Exception lse) {

		}

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
				System.out.println("\nID Verify Sub Menu");
				System.out.println("" + NAV_GET_USER_ID_VERIFICATION_STATUS
						+ ". Get User Identity Verification Status ");
				System.out.println("" + NAV_GET_SSN_VERIVATION_STATUS
						+ ". Get SSN Verification Status ");
				System.out.println(""
						+ NAV_GET_SSN_VERIVATION_STATUS_ACROSS_COBRANDS
						+ ". Get SSN Verification Status Across Cobrands ");
				System.out
						.println(""
								+ NAV_UPDATE_USER_SSN_VERIVATION_STATUS
								+ ". Update SSN Verification Status for Registered User");
				System.out
						.println(""
								+ NAV_UPDATE_SSN_VERIVATION_STATUS_NONREG_USER
								+ ". Update SSN Verification Status for Non Registered User ");
				System.out.println("" + NAV_GET_USER_PROFILE_DATA
						+ ". Get User Profile Data ");
				System.out.println("" + NAV_UPDATE_USER_PROFILE_DATA
						+ ". Update User Profile Data ");
				System.out.println("" + NAV_UPDATE_USER_VERIFICATION_DATA
						+ ". Update User Verification request Data ");
				System.out
						.println(""
								+ NAV_UPDATE_NON_REG_USER_VERIFICATION_DATA
								+ ". Update Verification request Data for Non Reg-User");
				System.out.println("" + NAV_QUIT + ". Exit");
				System.out.println("\n");
				System.out.print("Enter Choice : ");
				choice = IOUtils.readInt();

				if (choice == NAV_GET_USER_ID_VERIFICATION_STATUS)
					getUserIDVerificationStatus(userContext);
				if (choice == NAV_GET_SSN_VERIVATION_STATUS)
					getSSNVerificationStatus(getCobrandContext());
				if (choice == NAV_GET_SSN_VERIVATION_STATUS_ACROSS_COBRANDS)
					getSSNVerificationStatusAcrosCobrands(getCobrandContext());
				if (choice == NAV_UPDATE_USER_SSN_VERIVATION_STATUS)
					updateUserSSNVerificationDetails(userContext);
				if (choice == NAV_UPDATE_SSN_VERIVATION_STATUS_NONREG_USER)
					updateNonRegUserSSNVerificationDetails(getCobrandContext());
				if (choice == NAV_GET_USER_PROFILE_DATA)
					getuserProfileData(userContext);
				if (choice == NAV_UPDATE_USER_PROFILE_DATA)
					updateUserProfileData(userContext);
				if (choice == NAV_UPDATE_USER_VERIFICATION_DATA)
					updateUserVerificationReqData(userContext);
				if (choice == NAV_UPDATE_NON_REG_USER_VERIFICATION_DATA)
					updateNonRegVerificationReqData(getCobrandContext());
				if (choice == NAV_QUIT)
					loop = false;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	
	
	/**
	 * Gets Identity Verification status for the User.
	 * 
	 * @param userContext :
	 *            {@link <code>UserContext</code>}
	 */

	public void getUserIDVerificationStatus(UserContext userContext) {
		IDVerificationService idVerrifyService = new IDVerificationService();
		idVerrifyService.getIDVerificationStatus(userContext);
	}

	/**
	 * Gets Identity Verification status for the SSN.
	 * 
	 * @param cobContext :
	 *            {@link <code>CobrandContext</code>}
	 */
	public void getSSNVerificationStatus(CobrandContext cobContext) {
		IDVerificationService idVerrifyService = new IDVerificationService();
		idVerrifyService.getSSNVerifyStatus(cobContext);

	}

	/**
	 * Gets Identity Verification status for the SSN across Cobrands
	 * 
	 * @param cobContext :
	 *            {@link <code>CobrandContext</code>}
	 */
	public void getSSNVerificationStatusAcrosCobrands(CobrandContext cobContext) {
		IDVerificationService idVerrifyService = new IDVerificationService();
		idVerrifyService.getSSNVerifyStatusAccrossCob(cobContext);

	}

	/**
	 * Updates Verification status for the SSN for Registered User
	 * 
	 * @param userContext :
	 *            {@link <code>UserContext</code>}
	 */

	public void updateUserSSNVerificationDetails(UserContext userContext) {
		IDVerificationManagement idVerrifyMgmt = new IDVerificationManagement();
		idVerrifyMgmt.updateSSNDetailsForUser(userContext);
	}

	/**
	 * Updates Verification status for the SSN for Un-Registered User
	 * 
	 * @param cobContext :
	 *            {@link <code>CobrandContext</code>}
	 */

	public void updateNonRegUserSSNVerificationDetails(CobrandContext cobContext) {
		IDVerificationManagement idVerrifyMgmt = new IDVerificationManagement();
		idVerrifyMgmt.updateSSNDetailsForNonRegUser(cobContext);
	}

	/**
	 * Get User ID Verification Profile Data
	 * 
	 * @param userContext :
	 *            {@link <code>UserContext</code>}
	 */

	public void getuserProfileData(UserContext userContext) {
		IDVerificationService idVerrifyService = new IDVerificationService();
		idVerrifyService.getUserProfData(userContext);

	}

	/**
	 * Updates User ID Verification Profile Data
	 * 
	 * @param userContext :
	 *            {@link <code>UserContext</code>}
	 */

	public void updateUserProfileData(UserContext userContext) {
		IDVerificationManagement idVerrifyMgmt = new IDVerificationManagement();
		idVerrifyMgmt.updateProfileDetailsForUser(userContext);

	}

	/**
	 * Updates User ID Verification Request Data for registered User
	 * 
	 * @param userContext :
	 *            {@link <code>UserContext</code>}
	 */

	public void updateUserVerificationReqData(UserContext userContext) {
		IDVerificationManagement idVerrifyMgmt = new IDVerificationManagement();
		idVerrifyMgmt.updateUserVerifyReqData(userContext);
	}

	/**
	 * Updates User ID Verification Request Data for un-registered User
	 * 
	 * @param cobContext :
	 *            {@link <code>CobrandContext</code>}
	 */
	public void updateNonRegVerificationReqData(CobrandContext cobContext) {
		IDVerificationManagement idVerrifyMgmt = new IDVerificationManagement();
		idVerrifyMgmt.updateNonRegVerifyReqData(cobContext);
	}
}
