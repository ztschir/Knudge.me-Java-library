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
import java.util.Calendar;
import com.yodlee.soap.common.CobrandContext;
import com.yodlee.soap.common.UserContext;
import com.yodlee.soap.core.IllegalArgumentValueExceptionFault;
import com.yodlee.soap.core.InvalidCobrandContextExceptionFault;
import com.yodlee.soap.core.InvalidCobrandConversationCredentialsExceptionFault;
import com.yodlee.soap.core.InvalidConversationCredentialsExceptionFault;
import com.yodlee.soap.core.InvalidUserContextExceptionFault;
import com.yodlee.soap.core.StaleConversationCredentialsExceptionFault;
import com.yodlee.soap.core.identityverification.IdentityVerificationAddress;
import com.yodlee.soap.collections.core.identityverification.ArrayOfIdentityVerificationAddress;
import com.yodlee.soap.core.identityverification.IdentityVerificationAddressIndicator;
import com.yodlee.soap.core.identityverification.IdentityVerificationDriversLicense;
import com.yodlee.soap.core.identityverification.IdentityVerificationName;
import com.yodlee.soap.core.identityverification.IdentityVerificationProfileData;
import com.yodlee.soap.core.identityverification.IdentityVerificationReasonCode;
import com.yodlee.soap.collections.core.identityverification.ArrayOfIdentityVerificationReasonCode;
import com.yodlee.soap.core.identityverification.IdentityVerificationResult;
import com.yodlee.soap.core.identityverification.IdentityVerificationStatus;
import com.yodlee.soap.core.identityverification.SSNVerificationInfo;
import com.yodlee.soap.core.usermanagement.State_US;
import com.yodlee.soap.core.CoreExceptionFault;
import com.yodlee.sampleapps.helper.IFileTransferConstants;
import com.yodlee.sampleapps.helper.IOUtils;

public class IDVerificationManagement extends IDVerification {

	
		
	/**
	 * Updates Verification status for the SSN for Registered User
	 * 
	 * @param userContext :
	 *            {@link <code>UserContext</code>}
	 */

	public void updateSSNDetailsForUser(UserContext userContext) {

		String SSN = IOUtils.promptInput(IFileTransferConstants.SSN2Prompt,
				IFileTransferConstants.SSN2ReEnterPrompt);

		String verificationsStatus = IOUtils.promptInput(
				IFileTransferConstants.IdentityVerifyStatus2Prompt,
				IFileTransferConstants.IdentityVerifyStatus2ReEnterPrompt);

		int idVerificationStatus = Integer.parseInt(verificationsStatus);

		try {
			SSNVerificationInfo ssnVerificationInfo = identityVerificationManagement
					.updateSSNVerificationStatus( userContext,SSN,getInstance(idVerificationStatus));

			if (ssnVerificationInfo != null)
				System.out.println("Saved SSN with ID "
						+ ssnVerificationInfo.getSsnId() + " and"
						+ ssnVerificationInfo.getIdentityVerificationStatus());
			else
				System.out.println("SSN value object is null ");

		} catch (InvalidUserContextExceptionFault iu) {
			System.out.println("Invalid User Context");
			System.out.println(iu.toString());
		}catch(IllegalArgumentValueExceptionFault iave){
			System.out.println("Illegal Argument value");
		}catch(StaleConversationCredentialsExceptionFault iave){
			System.out.println("Stale conversation Credentials");
		}catch(RemoteException ree){
			System.out.println("Remote Exception");
			ree.printStackTrace();
		}

	}

	/**
	 * Updates Verification status for the SSN for Un-Registered User
	 * 
	 * @param cobContext :
	 *            {@link <code>CobrandContext</code>}
	 */
	public void updateSSNDetailsForNonRegUser(CobrandContext cobContext) {

		String SSN = IOUtils.promptInput(IFileTransferConstants.SSN2Prompt,
				IFileTransferConstants.SSN2ReEnterPrompt);

		String verificationsStatus = IOUtils.promptInput(
				IFileTransferConstants.IdentityVerifyStatus2Prompt,
				IFileTransferConstants.IdentityVerifyStatus2ReEnterPrompt);

		int idVerificationStatus = Integer.parseInt(verificationsStatus);
		SSNVerificationInfo ssnVerificationInfo=null;
		try {
			ssnVerificationInfo = identityVerificationManagement
					.updateSSNVerificationStatus1( getCobrandContext(),SSN,
							getInstance(idVerificationStatus));
		}catch(InvalidCobrandConversationCredentialsExceptionFault iccefe){
			System.out.println("Invalid Cobrand Conversation Credentials"); 
		} catch (InvalidUserContextExceptionFault iu) {
			System.out.println("Invalid User Context");
			System.out.println(iu.toString());
		}catch (InvalidCobrandContextExceptionFault iu) {
			System.out.println("Invalid User Context");
			System.out.println(iu.toString());
		}catch (RemoteException re) {
			System.out.println("remote exception");
			re.printStackTrace();
		}
		
			if (ssnVerificationInfo != null)
				System.out.println("Saved SSN with ID "
						+ ssnVerificationInfo.getSsnId() + " and"
						+ ssnVerificationInfo.getIdentityVerificationStatus());
			else
				System.out.println("SSN value object is null ");

		}

	

	/**
	 * <p>
	 * Returns the Identity Verification Status object based on the status id.
	 * </p>
	 * 
	 * @param statusId
	 *            The status id of the Identity Verification
	 * @return returns the Identity Verification Status object based on the
	 *         status id.
	 */
	public static IdentityVerificationStatus getInstance(int statusId) {
		switch (statusId) {

		case 1:
			return IdentityVerificationStatus.NOT_VERIFIED;
		case 2:
			return IdentityVerificationStatus.VERIFIED_ERROR;
		case 3:
			return IdentityVerificationStatus.VERIFIED_SUCCESS;
		case 4:
			return IdentityVerificationStatus.VERIFIED_MANUAL;
		case 5:
			return IdentityVerificationStatus.VERIFIED_FAILURE;
		case 6:
			return IdentityVerificationStatus.INITIATED;
		default:
			return null;
		}
	}


	/**
	 * Updates User ID Verification Profile Data
	 * 
	 * @param userContext :
	 *            {@link <code>UserContext</code>}
	 */
	public void updateProfileDetailsForUser(UserContext userContext) {
		IdentityVerificationProfileData identityVerificationProfileData = new IdentityVerificationProfileData();

		IdentityVerificationName identityVerificationName = new IdentityVerificationName();

		// set name details
		identityVerificationName.setFirstName("shivashree");
		identityVerificationName.setLastName("More");
		identityVerificationName.setMiddleInitial("s");
		identityVerificationProfileData.setName(identityVerificationName);

		// set other details
		identityVerificationProfileData.setEMail("shivashree.more@yahoo.com");
		identityVerificationProfileData.setDateOfBirth(Calendar.getInstance());
		identityVerificationProfileData.setGender("M");
		identityVerificationProfileData.setPhoneNumber("232434345");
		identityVerificationProfileData.setSSN("SSN345");

		// set address details

		IdentityVerificationAddress[] address_list = new IdentityVerificationAddress[3];
		ArrayOfIdentityVerificationAddress arrayofaddress_list = new ArrayOfIdentityVerificationAddress();
		
		IdentityVerificationAddress identityVerificationAddress_cr = new IdentityVerificationAddress();
		IdentityVerificationAddress identityVerificationAddress_fr = new IdentityVerificationAddress();
		IdentityVerificationAddress identityVerificationAddress_dr = new IdentityVerificationAddress();

		identityVerificationAddress_cr
				.setAddressIndicator(IdentityVerificationAddressIndicator.CURRENT_ADDRESS);
		identityVerificationAddress_cr.setCity("AHEMADABAD");
		identityVerificationAddress_cr.setPostalCode("333333");
		identityVerificationAddress_cr.setStreetAddr1("IIMA");
		identityVerificationAddress_cr.setStreetAddr2("Vastrapur");
		identityVerificationAddress_cr.setState(State_US.ARIZONA);

		identityVerificationAddress_fr
				.setAddressIndicator(IdentityVerificationAddressIndicator.FORMER_ADDRESS);
		identityVerificationAddress_fr.setCity("khops");
		identityVerificationAddress_fr.setPostalCode("44444");
		identityVerificationAddress_fr.setStreetAddr1("ramadham");
		identityVerificationAddress_fr.setState(State_US.ALASKA);

		identityVerificationAddress_dr
				.setAddressIndicator(IdentityVerificationAddressIndicator.DIFFERENT_ADDRESS);
		identityVerificationAddress_dr.setCity("bangalore");
		identityVerificationAddress_dr.setPostalCode("5555");
		identityVerificationAddress_dr.setStreetAddr1("church street");
		identityVerificationAddress_dr.setStreetAddr1("MG road");
		identityVerificationAddress_dr.setState(State_US.CALIFORNIA);

		address_list[0] = identityVerificationAddress_cr;
		address_list[1] = identityVerificationAddress_fr;
		address_list[2] = identityVerificationAddress_dr;
		
		arrayofaddress_list.setElements(address_list);
		identityVerificationProfileData.setAddressList(arrayofaddress_list);
		IdentityVerificationDriversLicense identityVerificationDriversLicense = new IdentityVerificationDriversLicense();

		identityVerificationDriversLicense
				.setAddressIndicator(IdentityVerificationAddressIndicator.DIFFERENT_ADDRESS);
		identityVerificationDriversLicense.setIssuedIndicator("Y");
		identityVerificationDriversLicense.setLicenseNumber("DL34356");
		identityVerificationDriversLicense
				.setStateOfIssue(State_US.ARMED_FORCES_PACIFIC);

		identityVerificationProfileData
				.setDriversLicense(identityVerificationDriversLicense);
try{
		identityVerificationManagement.updateIDVerificationProfileInfo(
				userContext, identityVerificationProfileData);
	}catch(InvalidUserContextExceptionFault icce){
		System.out.println("Invalid User Context");
		
	}catch(InvalidConversationCredentialsExceptionFault sccefe){
		System.out.println("Invalid Conversation Credentials");
	}catch(StaleConversationCredentialsExceptionFault sccefe){
		System.out.println("Invalid Conversation Credentials");
	}catch(IllegalArgumentValueExceptionFault iave){
		System.out.println("Illegal Argument value");
	}catch(CoreExceptionFault re){
		System.out.println("Remote Exception occured");
	}catch(RemoteException re){
		System.out.println("Remote Exception occured");
	}
	}

	/**
	 * Updates User ID Verification Request Data for registered User
	 * 
	 * @param userContext :
	 *            {@link <code>UserContext</code>}
	 */
	public void updateUserVerifyReqData(UserContext userContext) {
		IdentityVerificationResult identityVerificationResult = new IdentityVerificationResult();
		IdentityVerificationReasonCode identityVerificationReasonCode1 = new IdentityVerificationReasonCode();
		IdentityVerificationReasonCode identityVerificationReasonCode2 = new IdentityVerificationReasonCode();
		IdentityVerificationReasonCode identityVerificationReasonCode3 = new IdentityVerificationReasonCode();
		IdentityVerificationReasonCode identityVerificationReasonCode4 = new IdentityVerificationReasonCode();
		IdentityVerificationReasonCode identityVerificationReasonCode5 = new IdentityVerificationReasonCode();

		IdentityVerificationReasonCode identityVerificationReasonCodes[] = new IdentityVerificationReasonCode[5];
		ArrayOfIdentityVerificationReasonCode aidentityVerificationReasonCode1 = new ArrayOfIdentityVerificationReasonCode();
		identityVerificationReasonCode1
				.setReasonText("Interactive query was answered correctly with minimal number of questions.");
		identityVerificationReasonCode1.setReasonValue("21");
		identityVerificationReasonCodes[0] = identityVerificationReasonCode1;

		identityVerificationReasonCode2
				.setReasonText("Last name not validated.");
		identityVerificationReasonCode2.setReasonValue("44");
		identityVerificationReasonCodes[1] = identityVerificationReasonCode2;

		identityVerificationReasonCode3
				.setReasonText("Identity located on primary data source - good match");
		identityVerificationReasonCode3.setReasonValue("48");
		identityVerificationReasonCodes[2] = identityVerificationReasonCode3;

		identityVerificationReasonCode4
				.setReasonText("Identity located on tertiary data source - good match");
		identityVerificationReasonCode4.setReasonValue("63");
		identityVerificationReasonCodes[3] = identityVerificationReasonCode4;

		identityVerificationReasonCode5
				.setReasonText("Driver's license number not validated.");
		identityVerificationReasonCode5.setReasonValue("82");
		identityVerificationReasonCodes[4] = identityVerificationReasonCode5;
		
		aidentityVerificationReasonCode1.setElements(identityVerificationReasonCodes);
		identityVerificationResult
				.setReasonCodes(aidentityVerificationReasonCode1);
		identityVerificationResult.setOverallScore(new Long(90));
		identityVerificationResult
				.setTransactionKey("1200910050947435755500182");
		try{
		identityVerificationManagement.updateIDVerificationInfo(userContext,"123123123",
				identityVerificationResult);
		}catch(InvalidCobrandContextExceptionFault icce){
			System.out.println("Invalid Cobrand Context");
			icce.printStackTrace();
		}catch(IllegalArgumentValueExceptionFault iave){
			System.out.println("Illegal Argument value");
		}catch(CoreExceptionFault cef){
			System.out.println("Core Exception occured");
		}catch(RemoteException re){
			System.out.println("Remote Exception occured");
		}

	}

	/**
	 * Updates User ID Verification Request Data for un-registered User
	 * 
	 * @param cobContext :
	 *            {@link <code>CobrandContext</code>}
	 */
	public void updateNonRegVerifyReqData(CobrandContext cobContext) {
		IdentityVerificationResult identityVerificationResult = new IdentityVerificationResult();
		IdentityVerificationReasonCode identityVerificationReasonCode1 = new IdentityVerificationReasonCode();
		IdentityVerificationReasonCode identityVerificationReasonCode2 = new IdentityVerificationReasonCode();
		IdentityVerificationReasonCode identityVerificationReasonCode3 = new IdentityVerificationReasonCode();
		IdentityVerificationReasonCode identityVerificationReasonCode4 = new IdentityVerificationReasonCode();
		IdentityVerificationReasonCode identityVerificationReasonCode5 = new IdentityVerificationReasonCode();

		ArrayOfIdentityVerificationReasonCode aidentityVerificationReasonCode1 = new ArrayOfIdentityVerificationReasonCode();
		
		IdentityVerificationReasonCode identityVerificationReasonCodes[] = new IdentityVerificationReasonCode[5];
		
		
		identityVerificationReasonCode1
				.setReasonText("Interactive query was answered correctly with minimal number of questions.");
		identityVerificationReasonCode1.setReasonValue("21");
		identityVerificationReasonCodes[0] = identityVerificationReasonCode1;
		
		identityVerificationReasonCode2
				.setReasonText("Last name not validated.");
		identityVerificationReasonCode2.setReasonValue("44");
		identityVerificationReasonCodes[1] = identityVerificationReasonCode2;

		identityVerificationReasonCode3
				.setReasonText("Identity located on primary data source - good match");
		identityVerificationReasonCode3.setReasonValue("48");
		identityVerificationReasonCodes[2] = identityVerificationReasonCode3;

		identityVerificationReasonCode4
				.setReasonText("Identity located on tertiary data source - good match");
		identityVerificationReasonCode4.setReasonValue("63");
		identityVerificationReasonCodes[3] = identityVerificationReasonCode4;

		identityVerificationReasonCode5
				.setReasonText("Driver's license number not validated.");
		identityVerificationReasonCode5.setReasonValue("82");
		identityVerificationReasonCodes[4] = identityVerificationReasonCode5;
		aidentityVerificationReasonCode1.setElements(identityVerificationReasonCodes);
		identityVerificationResult.setReasonCodes(aidentityVerificationReasonCode1);
		identityVerificationResult.setOverallScore(new Long(90));
		identityVerificationResult
				.setTransactionKey("1200910050947435755500182");
		try{
		identityVerificationManagement.updateIDVerificationInfo(
				cobContext,"SSN888", identityVerificationResult);
		}catch(StaleConversationCredentialsExceptionFault sccefe){
			System.out.println("Stale Conversation Credentials");
		}catch(IllegalArgumentValueExceptionFault iave){
			System.out.println("Illegal Argument value");
		}catch(RemoteException re){
			System.out.println("Remote Exception occured");
		}
	}

}
