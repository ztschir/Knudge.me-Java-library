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
import com.yodlee.soap.common.CobrandContext;
import com.yodlee.soap.common.UserContext;
import java.util.Calendar;
import com.yodlee.soap.core.CoreExceptionFault;
import com.yodlee.soap.core.IllegalArgumentValueExceptionFault;
import com.yodlee.soap.core.InvalidCobrandContextExceptionFault;
import com.yodlee.soap.core.InvalidCobrandConversationCredentialsExceptionFault;
import com.yodlee.soap.core.InvalidUserContextExceptionFault;
import com.yodlee.soap.core.StaleConversationCredentialsExceptionFault;
import com.yodlee.soap.core.InvalidConversationCredentialsExceptionFault;
import com.yodlee.soap.core.identityverification.IdentityVerificationAddress;
import com.yodlee.soap.collections.core.identityverification.ArrayOfIdentityVerificationAddress;
import com.yodlee.soap.collections.core.identityverification.ArrayOfSSNVerificationInfo;
import com.yodlee.soap.core.identityverification.IdentityVerificationDriversLicense;
import com.yodlee.soap.core.identityverification.IdentityVerificationName;
import com.yodlee.soap.core.identityverification.IdentityVerificationProfileData;
import com.yodlee.soap.core.identityverification.IdentityVerificationStatus;
import com.yodlee.soap.core.identityverification.SSNVerificationInfo;
import com.yodlee.sampleapps.helper.IFileTransferConstants;
import com.yodlee.sampleapps.IDVerificationManagement;
import com.yodlee.sampleapps.helper.IOUtils;

public class IDVerificationService extends IDVerification {

	/**
	 * Gets Identity Verification status for the User.
	 * 
	 * @param userContext :
	 *            {@link <code>UserContext</code>}
	 */
	public void getIDVerificationStatus(UserContext userContext) {
		try {
			IdentityVerificationStatus identityVerificationStatus = identityVerificationService
					.getUserIdentityVerificationStatus(userContext);

			if (identityVerificationStatus != null)
				System.out.println("User IdentityVerification Status is : "
						+ identityVerificationStatus.getValue());
								
			else
				System.out.println("Value Object is null");

		} catch (InvalidUserContextExceptionFault iu) {
			System.out.println("Invalid User Context");
			System.out.println(iu.toString());
		}catch(StaleConversationCredentialsExceptionFault iave){
			System.out.println("Stale conversation Credentials");
		}catch(IllegalArgumentValueExceptionFault iave){
			System.out.println("Illegal Argument value");
		}catch(CoreExceptionFault cef){
			System.out.println("Core Exception occured");
		}catch(RemoteException re){
			System.out.println("remote Exception occured");
		}

	}

	/**
	 * Gets Identity Verification status for the SSN.
	 * 
	 * @param cobContext :
	 *            {@link <code>CobrandContext</code>}
	 */

	public void getSSNVerifyStatus(CobrandContext cobContext) {

		String SSN = IOUtils.promptInput(IFileTransferConstants.SSN2Prompt,
				IFileTransferConstants.SSN2ReEnterPrompt);

		try {
			SSNVerificationInfo ssnVerificationInfo = identityVerificationService
					.getSSNVerificationStatus1(cobContext, SSN);

			if (ssnVerificationInfo != null) {
				System.out.println("SSN IdentityVerification Status is : "
						+ ssnVerificationInfo.getIdentityVerificationStatus());
				System.out.println("Cobrand ID : "
						+ ssnVerificationInfo.getCobrandId());
				System.out.println("Success Attempts: "
						+ ssnVerificationInfo.getSucessAttempts());
				System.out.println("Failure Attempts: "
						+ ssnVerificationInfo.getFailureAttempts());
			} else
				System.out.println("SSN Value Object is null");

		} catch (InvalidCobrandContextExceptionFault iu) {
			System.out.println("Invalid Cobrand Context");
			System.out.println(iu.toString());
		}catch(InvalidCobrandConversationCredentialsExceptionFault iccefe){
			System.out.println("Invalid Cobrand Conversation Credentials"); 
		}catch(StaleConversationCredentialsExceptionFault iccefe){
			System.out.println("Invalid Cobrand Conversation Credentials"); 
		}catch(RemoteException re){
			System.out.println("remote Exception occured");
		}

	}

	/**
	 * Gets Identity Verification status for the SSN across Cobrands
	 * 
	 * @param cobContext :
	 *            {@link <code>CobrandContext</code>}
	 */
	public void getSSNVerifyStatusAccrossCob(CobrandContext cobContext) {
		String SSN = IOUtils.promptInput(IFileTransferConstants.SSN2Prompt,
				IFileTransferConstants.SSN2ReEnterPrompt);
		ArrayOfSSNVerificationInfo ssnVerificationInfoArray = null;
		SSNVerificationInfo[] ssnVerificationInfos = null;
		
		try {
			ssnVerificationInfoArray = identityVerificationService
					.getSSNVerificationStatus(cobContext, SSN,
							true);
			if (ssnVerificationInfoArray != null){
				ssnVerificationInfos = ssnVerificationInfoArray.getElements();
	    	}

			if (ssnVerificationInfos != null) {
				for (int i = 0; i < ssnVerificationInfos.length; i++) {
					System.out.println("*************Cobrand ID : "
							+ ssnVerificationInfos[i].getCobrandId()
							+ "******************");
					System.out.println("SSN IdentityVerification Status is : "
							+ ssnVerificationInfos[i]
									.getIdentityVerificationStatus().getValue()
									);
					System.out.println("Success Attempts: "
							+ ssnVerificationInfos[i].getSucessAttempts());
					System.out.println("Failure Attempts: "
							+ ssnVerificationInfos[i].getFailureAttempts());
				}
			} else
				System.out.println("SSN Value Object is null");

		} catch (InvalidCobrandContextExceptionFault iu) {
			System.out.println("Invalid Cobrand Context");
			System.out.println(iu.toString());
		}catch (CoreExceptionFault cef) {
			System.out.println("core exception");
			cef.printStackTrace();
		}catch(InvalidConversationCredentialsExceptionFault sccefe){
			System.out.println("Invalid Conversation Credentials");
		}catch(StaleConversationCredentialsExceptionFault sccefe){
			System.out.println("Invalid Conversation Credentials");
		}catch(InvalidCobrandConversationCredentialsExceptionFault iccefe){
			System.out.println("Invalid Cobrand Conversation Credentials"); 
		}catch(IllegalArgumentValueExceptionFault iave){
			System.out.println("Illegal Argument value");
		}catch(RemoteException re){
			System.out.println("Remote Exception occured");
		}
	}

	/**
	 * Get User ID Verification Profile Data
	 * 
	 * @param userContext :
	 *            {@link <code>UserContext</code>}
	 */
	public void getUserProfData(UserContext userContext) {
		System.out.println("in user prog data");
		IdentityVerificationProfileData identityVerificationProfileData =null;
		try{
		identityVerificationProfileData = identityVerificationService
				.getIDVerificationProfileInfo(userContext);
		}catch (InvalidUserContextExceptionFault iu) {
		System.out.println("Invalid User Context");
		System.out.println(iu.toString());
		}catch (RemoteException re){
			System.out.println("Remote Exception");
			}
		ArrayOfIdentityVerificationAddress arrayOfIdentityVerificationAddress = null;
		IdentityVerificationAddress[] address_list = null;
		IdentityVerificationDriversLicense identityVerificationDriversLicense = null;
		IdentityVerificationName identityVerificationName = null;

		IdentityVerificationAddress dl_address = null;
		if (identityVerificationProfileData!=null){
			
		identityVerificationName = identityVerificationProfileData.getName();
		}else
			return;
		if (identityVerificationName != null) {
			
			System.out.println("FIRST_NAME     "
					+ identityVerificationName.getFirstName());
			System.out.println("LAST_NAME      "
					+ identityVerificationName.getLastName());
			System.out.println("MIDDLE_INITIAL    "
					+ identityVerificationName.getMiddleInitial());
		} else
			System.out.println("Name object is null");
		String Email=identityVerificationProfileData.getEMail();
		String gender = identityVerificationProfileData.getGender();
		String home_phone=identityVerificationProfileData.getPhoneNumber();
		String ssn = identityVerificationProfileData.getSSN();
		Calendar dob = identityVerificationProfileData.getDateOfBirth();
		if(Email!= null||Email != "")
		System.out.println("EMAIL    "
				+ identityVerificationProfileData.getEMail());
		if(gender!= null||gender != "")
		System.out.println("GENDER    "	+ identityVerificationProfileData.getGender());
		if(home_phone!= null||home_phone != "")
		System.out.println("HOME_PHONE    "
				+ identityVerificationProfileData.getPhoneNumber());
		if(ssn!= null||ssn != "")
		System.out
				.println("SSN    " + identityVerificationProfileData.getSSN());
		if(dob!= null)
		System.out.println("DOB    "
				+ identityVerificationProfileData.getDateOfBirth());
		
		arrayOfIdentityVerificationAddress = identityVerificationProfileData.getAddressList();
		if (arrayOfIdentityVerificationAddress != null){
			address_list = arrayOfIdentityVerificationAddress.getElements();
    	}

		if (address_list != null && address_list.length != 0) {
			for (int i = 0; i < address_list.length; i++) {
				if (address_list[i] != null) {
					System.out
							.println("ADRESS TYPE*************************************    "
									+ address_list[i].getAddressIndicator());
					System.out
							.println("STATE    " + address_list[i].getState());
					System.out.println("POSTAL_CODE    "
							+ address_list[i].getPostalCode());
					System.out.println("CITY    " + address_list[i].getCity());
					System.out.println("STREET 1  "
							+ address_list[i].getStreetAddr1());
					System.out.println("STREET 2  "
							+ address_list[i].getStreetAddr2());
				}

			}

		} else {
			System.out.println("Address list is null");
		}

		identityVerificationDriversLicense = identityVerificationProfileData
				.getDriversLicense();

		if (identityVerificationDriversLicense != null) {
			System.out.println("LICENSE NUMBER    "
					+ identityVerificationDriversLicense.getLicenseNumber());
			System.out.println("IISUED_INDICATOR    "
					+ identityVerificationDriversLicense.getIssuedIndicator());
			System.out.println("ISSUED STATE    "
					+ identityVerificationDriversLicense.getStateOfIssue());

			System.out.println("ADDRESS_INDICATOR    "
					+ identityVerificationDriversLicense.getAddressIndicator());
			dl_address = identityVerificationDriversLicense.getAddress();

			if (dl_address != null) {
				System.out
						.println("ADRESS TYPE*************************************    "
								+ dl_address.getAddressIndicator());
				System.out.println("STATE    " + dl_address.getState());
				System.out.println("POSTAL_CODE    "
						+ dl_address.getPostalCode());
				System.out.println("CITY    " + dl_address.getCity());
				System.out.println("STREET 1  " + dl_address.getStreetAddr1());
				System.out.println("STREET 2  " + dl_address.getStreetAddr2());
			} else
				System.out.println("DL address object is null");

		} else {
			System.out.println("DL object is null");
		}

	}

}
