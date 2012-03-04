/*
 * Copyright 2009 Yodlee, Inc.  All Rights Reserved.  Your use of this code 
 * requires a license from Yodlee.  Any such license to this code is 
 * restricted to evaluation/illustrative purposes only. It is not intended 
 * for use in a production environment, and Yodlee disclaims all warranties 
 * and/or support obligations concerning this code, regardless of the terms 
 * of any other agreements between Yodlee and you.
 */

package com.yodlee.sampleapps;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.rmi.RemoteException;
import com.yodlee.sampleapps.helper.BillPayConstants;
import com.yodlee.soap.core.accountmanagement.InvalidItemActionTransactionIdExceptionFault;
import com.yodlee.soap.core.billermanagement.billermanagement.BillerManagement;
import com.yodlee.soap.core.billermanagement.billermanagement.BillerManagementServiceLocator;
import com.yodlee.soap.core.billermanagement.Service;

import com.yodlee.soap.core.billermanagement.UslStatus;
import com.yodlee.soap.core.billermanagement.UslStatusFilterCriteria;
import com.yodlee.soap.core.dataservice.dataservice.DataService;
import com.yodlee.soap.core.dataservice.dataservice.DataServiceServiceLocator;
import com.yodlee.soap.core.paymentservice.payeemanagement.PayeeManagement;


import com.yodlee.soap.core.paymentservice.payeemanagement.PayeeManagementServiceLocator;
import com.yodlee.soap.core.ContentServiceNotFoundExceptionFault;
import com.yodlee.soap.core.InvalidCobrandConversationCredentialsExceptionFault;
import com.yodlee.soap.core.InvalidCobrandContextExceptionFault;
import com.yodlee.soap.core.IllegalArgumentValueExceptionFault;
import com.yodlee.soap.core.InvalidItemAccountIdExceptionFault;
import com.yodlee.soap.core.IncompleteArgumentExceptionFault;
import com.yodlee.soap.core.SmartformValidationExceptionFault;
import com.yodlee.soap.core.InvalidConversationCredentialsExceptionFault;
import com.yodlee.soap.core.InvalidUserContextExceptionFault;
import com.yodlee.soap.core.StaleConversationCredentialsExceptionFault;
import com.yodlee.soap.core.CoreExceptionFault;
import com.yodlee.soap.core.ServiceNotFoundExceptionFault;
import com.yodlee.soap.collections.common.ArrayOfAddress;
import com.yodlee.soap.collections.core.billermanagement.ArrayOfService;
import com.yodlee.soap.collections.core.billermanagement.ArrayOfUslStatus;
import com.yodlee.soap.collections.core.paymentservice.ArrayOfPayee;
import com.yodlee.soap.common.Address;
import com.yodlee.soap.common.Country;
import com.yodlee.soap.common.UserContext;
import com.yodlee.soap.core.paymentservice.InvalidPayItAllProfileExceptionFault;
import com.yodlee.soap.core.paymentservice.InvalidPayAnyoneProfileExceptionFault;
import com.yodlee.soap.core.paymentservice.InvalidPayeeIdExceptionFault;
import com.yodlee.soap.core.paymentservice.InvalidPayeeStateChangeExceptionFault;
import com.yodlee.soap.core.paymentservice.Payee;
import com.yodlee.soap.core.paymentservice.PayItAllProfile;
import com.yodlee.soap.core.paymentservice.PayeeAlreadyExistsExceptionFault;
import com.yodlee.soap.core.paymentservice.PayeeNicknameAlreadyInUseExceptionFault;
import com.yodlee.soap.core.paymentservice.SmartFormType;
import com.yodlee.soap.core.usermanagement.State_US;

import com.yodlee.sampleapps.helper.IOUtils;

public class ManagePayee extends ApplicationSuper{
	
	protected PayeeManagement payeeManagement;
	protected DataService dataService;
	protected BillerManagement billerManagement;
	
	private static int OPTION_COUNT=1;
	private static final int UPDATE_NICKNAME = OPTION_COUNT++;
	private static final int UPDATE_NAME_ON_ACCOUNT = OPTION_COUNT++;
	private static final int UPDATE_DESCRIPTION = OPTION_COUNT++;
	private static final int UPDATE_EMAIL = OPTION_COUNT++;
	private static final int UPDATE_ADDRESS = OPTION_COUNT++;
	
	private static List countryNames;
	
	public ManagePayee(){
		super ();
		
				//Get dataservice locator/
		DataServiceServiceLocator  dataServiceLocator= new DataServiceServiceLocator();
        String serviceName= dataServiceLocator.getDataServiceWSDDServiceName();
        dataServiceLocator.setDataServiceEndpointAddress(System.getProperty("com.yodlee.soap.services.url")
				+ "/" + serviceName);
        try {
        	dataService = dataServiceLocator.getDataService();

		} catch (Exception lse) {

		}
		//Create PayeeManagement Locator 
		 PayeeManagementServiceLocator  payeeManagementServiceLocator= new PayeeManagementServiceLocator();
        String payeeManagementserviceName= payeeManagementServiceLocator.getPayeeManagementServiceWSDDServiceName();
        payeeManagementServiceLocator.setPayeeManagementServiceEndpointAddress(System.getProperty("com.yodlee.soap.services.url")
				+ "/" + payeeManagementserviceName);
        try {
        	payeeManagement = payeeManagementServiceLocator.getPayeeManagementService();

		} catch (Exception lse) {

		}

		 BillerManagementServiceLocator  billerManagementServiceLocator= new BillerManagementServiceLocator();
        String billerManagementserviceName= billerManagementServiceLocator.getBillerManagementServiceWSDDServiceName();
        billerManagementServiceLocator.setBillerManagementServiceEndpointAddress(System.getProperty("com.yodlee.soap.services.url")
				+ "/" + billerManagementserviceName);
        try {
        	billerManagement = billerManagementServiceLocator.getBillerManagementService();

		} catch (Exception lse) {

		}
		

		//Similar to States, Country.cs does not have method getCountries() which is
		// present in java version.
		// so adding counties saperately.
		
		countryNames = new ArrayList();
		
		//United States
		countryNames.add(Country.US);
		
		//Great Britain
		countryNames.add(Country.GB);
		
		//Australia
		countryNames.add(Country.AU);
		
		//Belgium
		countryNames.add(Country.BE);
		
		//China
		countryNames.add(Country.CN);
		
		//India
		countryNames.add(Country.IN);
		
		//Canada
		countryNames.add(Country.CN);
		
		//Spain
		countryNames.add(Country.ES);
	}
	
	
	/**
	 * This will custom payees and existing billers supported by Yodlee
	 * 
	 * before calling addPayee()
	 * @param userContext
	 * @param itemId
	 */
	public void addPayItAllPayee(UserContext userContext,boolean isCustomPayee){
			
		String keyword=null;
		//Ask user to enter the keyword only if he wants to add a non custom payee
		if (!isCustomPayee){
		//Enter keyword of the biller to search
		System.out.println("Enter Keyword(mandatory)");
		while (true)
        {
            keyword = IOUtils.readStr();
            if (keyword == null || keyword.length()== 0)
            {
                System.out.println("Keyword can't be empty. Please re-enter.");
            }
            else
                break;
        }
		}
		else
        {
			System.out.println("Payee Name : Utility.");
			System.out.println("Payee Service Id  : 255.");
        }
		//Enter the description of the payee to be added
		System.out.println("Enter description");
		String description = IOUtils.readStr();
		//Enter the nickname for the payee
		System.out.println("Enter Nickname(mandatory)");
		String nickname = IOUtils.readStr();
		
		//If user selected the custom payee option from the menu add a custom payee
		if (isCustomPayee==true){
			addCustomPayee(userContext,description,nickname);
			return;
		}
		// else add a non custom payee
		else{
        
			//Area zipcode to further filter the entries that will be showed to user
			System.out.println("Enter zipcode");
			String zipcode=IOUtils.readStr();
			
			//Following code will fetch the biller information/services provided by Yodlee
			//depending on the keyword as input. Also fetch only active services.
			UslStatusFilterCriteria uslStatusFilterCriteria = null;
			//UslStatus uslStatus[]=new UslStatus[1];
			//uslStatus[0]= UslStatus.ACTIVE_STATUS;
		
			//uslStatusFilterCriteria.setStatuses(new ArrayOfUslStatus(uslStatus));
			//uslStatusFilterCriteria.setFetchSumInfoMappedServices(true);
			//uslStatusFilterCriteria.setFetchAllServices(true);
			Service[] services = null;
			ArrayOfService serviceArray = null;
			try{
				serviceArray = billerManagement.getServices(getCobrandContext(),keyword,
						uslStatusFilterCriteria);
				
				if (serviceArray != null){
					services = serviceArray.getElements();
				}
						
			}catch(InvalidUserContextExceptionFault iue){
				System.out.println("Invalid User Context");
				iue.printStackTrace();
			}catch(InvalidConversationCredentialsExceptionFault icce){
				System.out.println("Invalid Conversation Credentials");
				icce.printStackTrace();
			}catch(InvalidCobrandConversationCredentialsExceptionFault icce){
				System.out.println("Invalid Conversation Credentials");
				icce.printStackTrace();
			}catch(InvalidCobrandContextExceptionFault icce){
				System.out.println("Invalid Conversation Credentials");
				icce.printStackTrace();
			}catch(PayeeAlreadyExistsExceptionFault paee){
				System.out.println("Payee Already Exists");
				paee.printStackTrace();
			}catch(PayeeNicknameAlreadyInUseExceptionFault pnaiue){
				System.out.println("Payee Nickname Already InUse");
				pnaiue.printStackTrace();
			}catch(ContentServiceNotFoundExceptionFault csnfe){
				System.out.println("Content Service Not Found");
				csnfe.printStackTrace();
			}catch(ServiceNotFoundExceptionFault snf){
				System.out.println("Service Not Found");
				snf.printStackTrace();
			}catch(IllegalArgumentValueExceptionFault iave){
				System.out.println("Illegal Argument Value");
				iave.printStackTrace();
			}catch(InvalidPayItAllProfileExceptionFault ipia){
				System.out.println("Invalid PayItAll Profile");
				ipia.printStackTrace();
			}catch(IncompleteArgumentExceptionFault iae){
				System.out.println("Incomplete Argument");
				iae.printStackTrace();			
			}catch(InvalidItemActionTransactionIdExceptionFault iiat){
				System.out.println("Invalid Item Action Transaction Id");
			}catch(CoreExceptionFault e){
				System.out.println(" Got an ExceptionFault! ");
				e.printStackTrace();
			}catch(RemoteException rce){
				System.out.println("Invalid Conversation Credentials");
				rce.printStackTrace();
			}
		
			int choice=0;
		
			//If keyword serch didnt return any values ask the user if he wants  to add a custom payee.
			//If yes add a custom payee else return
			
			if(services==null || services.length==0){
			System.out.println("No services found for given keyword. Do you want to add a custom payee? \n Enter 1 for Yes; 2 for No");
	    	choice = IOUtils.readInt();
	        System.out.println();
			
			if (choice==1){
				addCustomPayee(userContext,description,nickname);
				return;
			}
			else{
				return;
			} 
		}		
		
		//Display only 1st 10 Payees
		int counter=0;
				
		HashMap addresses=new HashMap();
		
		for(int i=0;i<services.length ;i++){
			if (UslStatus.ACTIVE_STATUS.equals(services[i].getStatus())){
			 System.out.println("\n"+(i+1)+". ServiceName- "+services[i].getServiceName()+"; " +
				"ServiceId- "+services[i].getServiceId());
			System.out.println("Expedite Payment Supported :" + services[i].isIsExpeditedSupported());		
			Address[] address=null;
			ArrayOfAddress addressArray = null;
			//Fetch the remittance address for the serviceId and zipcode combination
			try{
				addressArray = billerManagement.getRemittanceAddress(getCobrandContext(),
				services[i].getServiceId(), zipcode);
				if (addressArray != null){
					address = addressArray.getElements();
				}
			}catch(InvalidUserContextExceptionFault iue){
				System.out.println("Invalid User Context");
				iue.printStackTrace();
			}catch(InvalidConversationCredentialsExceptionFault icce){
				System.out.println("Invalid Conversation Credentials");
				icce.printStackTrace();
			}catch(InvalidCobrandConversationCredentialsExceptionFault icce){
				System.out.println("Invalid Conversation Credentials");
				icce.printStackTrace();
			}catch(InvalidCobrandContextExceptionFault icce){
				System.out.println("Invalid Conversation Credentials");
				icce.printStackTrace();
			}catch(PayeeAlreadyExistsExceptionFault paee){
				System.out.println("Payee Already Exists");
				paee.printStackTrace();
			}catch(PayeeNicknameAlreadyInUseExceptionFault pnaiue){
				System.out.println("Payee Nickname Already InUse");
				pnaiue.printStackTrace();
			}catch(ContentServiceNotFoundExceptionFault csnfe){
				System.out.println("Content Service Not Found");
				csnfe.printStackTrace();
			}catch(ServiceNotFoundExceptionFault snf){
				System.out.println("Service Not Found");
				snf.printStackTrace();
			}catch(IllegalArgumentValueExceptionFault iave){
				System.out.println("Illegal Argument Value");
				iave.printStackTrace();
			}catch(InvalidPayItAllProfileExceptionFault ipia){
				System.out.println("Invalid PayItAll Profile");
				ipia.printStackTrace();
			}catch(IncompleteArgumentExceptionFault iae){
				System.out.println("Incomplete Argument");
				iae.printStackTrace();			
			}catch(InvalidItemActionTransactionIdExceptionFault iiat){
				System.out.println("Invalid Item Action Transaction Id");
			}catch(CoreExceptionFault e){
				System.out.println(" Got an ExceptionFault! ");
				e.printStackTrace();
			}catch(RemoteException rce){
				System.out.println("Invalid Conversation Credentials");
				rce.printStackTrace();
			}
		
			//If serviceId doesnt not have any address continue to print other addresses
			//for different serviceId
			if(address==null || address.length==0 ){
				System.out.println("No remittance address present for given ServiceId "+
						services[i].getServiceId());
				continue;
			}//End if
						
			for(int j=0;j<address.length;j++){
				 System.out.println("    " + (j + 1) + ". Address == >");
                 printAddress(address[j]);				
			}
			addresses.put(Integer.valueOf(i+1), address);
			counter ++;
			}
		if (counter == 10){
			break;
		}
		} //End for
		
		//Ask user to input service number.Add a custom payee if the service number enterd by him is 99
		int service=0, iaddress=0;
		while(true){
			System.out.println("\n\nEnter Service Number (1-" + counter + ") " +
					" [NOTE: If service Id not found, enter 99 to add Custom/Individual Payee ]:");
			service = IOUtils.readInt();
			if(service==99){
				addCustomPayee(userContext,description,nickname);
				return;
			}else if(service> services.length || service<0){
				System.out.println("Please enter correct service number-");
			}
			else
				break;
		}//End While()
		
		//User Input for Address
		Address address[]= (Address[])addresses.get(Integer.valueOf(service));
		//Take user input for the payee address if payee address not available
		
		if (address  ==null || address.length == 0)
        {
            System.out.println("\nNo address found under selected service number. Please provide Payee address : ");
            System.out.println();
            Address tempAddress = payeeAddress();
            address = new Address[]{tempAddress};
            iaddress = 1;
        }
		//else ask him to enter the service number
		else{
		while(true){
			System.out.println("Enter Address number displayed under Service (1-" + address.length + ")"); 
			iaddress=IOUtils.readInt();
			if( iaddress >address.length || iaddress<0)
				System.out.println("Please enter correct address number-");			
			else
				break;
		}//End While()
		}
		//Create payItAllProfile
		PayItAllProfile payItAllProfile=getPayItAllProfileInfo(
				services[service-1].getServiceName(),
				services[service-1].getServiceId(), 
				address[iaddress-1],false,false);
		
		//Finally add Payee
		Payee payee = new Payee();
		payee.setServiceId(services[service-1].getServiceId());
		payee.setPayItAllProfile(payItAllProfile);
		payee.setNickname(nickname);
		payee.setDescription(description);
		
		try{
			
			payee=payeeManagement.addPayItAllPayee1(userContext,payee);
			
			System.out.println("Payee Id created is --> " +
                    payee.getPayeeId());
            System.out.println("===== PayItAll Payee added successful ======");
          
            
		}catch(PayeeAlreadyExistsExceptionFault iue){
			System.out.println("Payee already exists");
			iue.printStackTrace();
		}catch(PayeeNicknameAlreadyInUseExceptionFault icce){
			System.out.println("Payee nick name already exists.");
			icce.printStackTrace();
		}catch(ServiceNotFoundExceptionFault paee){
			System.out.println("Given service identifier is invalid .");
			paee.printStackTrace();
		}catch(InvalidItemAccountIdExceptionFault pnaiue){
			System.out.println("Given item account identifier is invalid");
			pnaiue.printStackTrace();
		}catch(IllegalArgumentValueExceptionFault csnfe){
			System.out.println("IllegalArgumentValueExceptionFault");
			System.out.println("Generally this happens ");
			System.out.println("1.  If both itemAccountId and payItAllProfile are null ");
			System.out.println("2.  If description/nick name length exceeds 1000 ");
			System.out.println("3.  Ifboth item account and service identifier are null");
			csnfe.printStackTrace();
		}catch(InvalidPayItAllProfileExceptionFault snf){
			System.out.println("This happens when PayitAllProfile is not set correctly");
			snf.printStackTrace();
		}catch(IncompleteArgumentExceptionFault ipia){
			System.out.println("This happens when PayitAllProfile is not null and ");
			System.out.println("1. NameOnAccount and AccountNumber for these profiles are null .  ");
			System.out.println("2. Remittence Address is null.");
			System.out.println("3. All of the address fields address1, city, state, zipcode1, country are null.");
			System.out.println("4. PayeeName is null.");
			System.out.println("IncompleteArgument.");
			ipia.printStackTrace();
		}catch(SmartformValidationExceptionFault smartFormExc){
			System.out.println(" Got an SmartformValidationException! ");
			smartFormExc.printStackTrace();
			}catch(Exception e){
			System.out.println(" Got an ExceptionFault! ");
			e.printStackTrace();
		}
		return;
		}
		
	}//end addPayItAllPayee
	

	/**
	 * Used to add custom payee. User has to provide address information for adding
	 * payitall payee in this.
	 * 
	 * @param userContext
	 * @param description
	 * @param nickname
	 */
	public void addCustomPayee(UserContext userContext, String description,
			String nickname){
		
		Address address =payeeAddress();					
		PayItAllProfile payItAllProfile=getPayItAllProfileInfo("Utility",new Long(255),
				address,true,false);
		
		try{
			Payee payee = new Payee();
			payee.setServiceId(new Long(255));
			payee.setPayItAllProfile(payItAllProfile);
			payee.setNickname(nickname);
			payee.setDescription(description);
			Payee payee1 = payeeManagement.addPayItAllPayee1(userContext,payee);
			System.out.println("Payee Id created is --> " +
					payee1.getPayeeId());
			System.out.println("===== PayItAll Payee added successful ======");
		}catch(InvalidUserContextExceptionFault iue){
			System.out.println("Invalid User Context");
			iue.printStackTrace();
		}catch(InvalidConversationCredentialsExceptionFault icce){
			System.out.println("Invalid Conversation Credentials");
			icce.printStackTrace();
		}catch(PayeeAlreadyExistsExceptionFault paee){
			System.out.println("Payee Already Exists");
			paee.printStackTrace();
		}catch(PayeeNicknameAlreadyInUseExceptionFault pnaiue){
			System.out.println("Payee Nickname Already InUse");
			pnaiue.printStackTrace();
		}catch(ContentServiceNotFoundExceptionFault csnfe){
			System.out.println("Content Service Not Found");
			csnfe.printStackTrace();
		}catch(ServiceNotFoundExceptionFault snf){
			System.out.println("Service Not Found");
			snf.printStackTrace();
		}catch(IllegalArgumentValueExceptionFault iave){
			System.out.println("Illegal Argument Value");
			iave.printStackTrace();
		}catch(InvalidPayItAllProfileExceptionFault ipia){
			System.out.println("Invalid PayItAll Profile");
			ipia.printStackTrace();
		}catch(IncompleteArgumentExceptionFault iae){
			System.out.println("Incomplete Argument");
			iae.printStackTrace();			
		}catch(InvalidItemActionTransactionIdExceptionFault iiat){
			System.out.println("Invalid Item Action Transaction Id");
		}catch(Exception e){
			System.out.println(" Got an ExceptionFault! ");
			e.printStackTrace();
		}
		return;
	}//End addCustomPayee()
	
	/**
	 * Returns PayItAllProfile object after setting all required fields
	 * @return
	 */
	public PayItAllProfile getPayItAllProfileInfo(String serviceName,
			Long serviceId, Address address,boolean isCustom, boolean paytoday){
		
		//Following fields are mandatory for both custom and Yodlee supported biller
		
		System.out.println("Enter Name on Account (Mandtory): ");
		String nameOnAccount = IOUtils.readStr();
		System.out.println("Enter Payee Account Number (Mandtory): ");
		String accountNumber = IOUtils.readStr();
		System.out.println("Enter your email address : ");
		String email = IOUtils.readStr();
		
		
		PayItAllProfile payitallprofile = new PayItAllProfile();
		payitallprofile.setPayeeName(serviceName);
		payitallprofile.setNameOnAccount(nameOnAccount);
		payitallprofile.setAccountNumber(accountNumber);
		payitallprofile.setEmail(email);
		if (!paytoday){
			Country country = Country.US;
			address.setCountry(country);
		    payitallprofile.setRemittanceAddress(address);
		}
		if (paytoday){
			payitallprofile.setSmartFormType(SmartFormType.ACCOUNTNUMBER_ONLY);
		}
		payitallprofile.setCustom(isCustom);
		
				
		payitallprofile.setServiceId(serviceId);
		
		return payitallprofile;
		
	}//End getPayItAllProfileInfo()
	
	/**
	 * Displays list of PayItAll Payees ONLY, DirectPay Payees are not displayed
	 * @param userContext
	 */
	public Payee[] viewAllPayItAllPayees(UserContext userContext){
		
			Payee payees[] = getPayItAllPayees(userContext);
			if(payees==null || payees.length==0){
				System.out.println("No payees found");
				return payees;
			}
			displayPayeeDetails(payees);			
			return payees;
		
	 
	}//End viewAllPayItAllPayees
	
	/**
	 * Returns array of PayItAll Payees
	 * @param userContext
	 * @return
	 */
	public Payee[] getPayItAllPayees(UserContext userContext){
		ArrayList payeeList = new ArrayList();
		Payee payees[]=null;
		Payee payees1[]= null;
		ArrayOfPayee payeesArray = null;

		try{
			System.out.println("Getting payees");
			payeesArray = payeeManagement.getPayees3(userContext);
			if (payeesArray != null){
				payees = payeesArray.getElements();
			}
			
			System.out.println("Returned ..........Got "+payees.length+"payees");
			if(payees.length==0){	
				
				return null;
			}
			payees1 = new Payee[payees.length];
			int k =0;
			for(int i=0; i<payees.length; i++){
				Payee payee = (Payee)payees[i];
				if(payee.getPayItAllProfile()!=null){
					payees1[k] = payee;
					k++;
				}
			}//end for
		}catch(InvalidUserContextExceptionFault iuc){
			System.out.println("Invalid User Context");
		}catch(InvalidConversationCredentialsExceptionFault icc){
			System.out.println("Invalid Conversation Credentials");
		}catch(Exception ex){
			if (payees!=null){
			System.out.println("Returned ..........Got "+payees.length+"payees");
			}
			ex.printStackTrace();
		}
		return  payees1;
	}//End getPayItAllPayees(0
	
	/**
	 * Marks payee_status=0 in DB for specified payeeId
	 * @param userContext
	 */
	public void removePayItAllPayee(UserContext userContext){
		
		Payee payees[] = getPayItAllPayees(userContext);
		if(payees==null || payees.length==0){
			System.out.println(" No payees added! ");
			return;
		}
		//Show the current payitall payees to user
		displayPayeeDetails(payees);
		System.out.println("Enter payee id you want to delete: ");
		long payeeId = IOUtils.readLong();
		
		boolean foundPayee=false;
		for(int i=0;i<payees.length;i++){
			Payee p = payees[i];
			if(p.getPayeeId().longValue()==payeeId){
				foundPayee=true;
				try{
					System.out.println("Removing PayItAllPayee "+payeeId);
					payeeManagement.removePayee(userContext, new Long(payeeId));
					System.out.println("Payee removed successfully.");
				}catch(InvalidConversationCredentialsExceptionFault icc){
					System.out.println("Invalid Conversation Credentials");
				}catch(InvalidPayeeIdExceptionFault ipid){
					System.out.println("Invalid Payee Id");
				}catch(InvalidPayeeStateChangeExceptionFault ipsc){
					System.out.println("Invalid Payee State Change");
				}catch(Exception ex){
					ex.printStackTrace();
				}
			}//end if
		}//end for
		if(foundPayee==false)
			System.out.print("You entered invalid payee id");
		
	}//end removePayItAllPayee
	
	/**
	 * User can update following fields
	 * 	NickName
	 *  Description
	 *  Address1
	 *  NameOnAccount
	 *  Email
	 *  
	 * @param userContext
	 */
	public void updatePayItAllPayee(UserContext userContext){
		Payee payees[] = getPayItAllPayees(userContext);
		if(payees==null || payees.length==0){
			System.out.println(" No payees added!!!!!! ");
			return;
		}
		
		System.out.print("Getting payees list...\n");
		displayPayeeDetails(payees);
		
		System.out.println("\nEnter the PayeeId to be updated");
		long payeeId=IOUtils.readLong();
		Payee payee=null;
		for(int i=0;i<payees.length;i++){
			long pId = payees[i].getPayeeId().longValue();
			if(pId==payeeId){
				payee=payees[i];
				break;
			}
		}
		
		if(payee==null){
			System.out.println("PayeeId entered is invalid!!!!!!!");
			return;
		}
		
		System.out.println("********************");
		System.out.println(UPDATE_NICKNAME+ ". Update Nickname");
		System.out.println(UPDATE_NAME_ON_ACCOUNT+ ". Update NameOnAccount");
		System.out.println(UPDATE_DESCRIPTION+ ". Update Description");		
		System.out.println(UPDATE_EMAIL+ ". Update Email");
		
		//Update address only for custom payees
		if(payee.getPayItAllProfile().isCustom())
			System.out.println(UPDATE_ADDRESS+". Update Address");
		System.out.println("0. Exit");
		int choice = IOUtils.readInt();
		
		while(choice!=0){
			
			if(choice==UPDATE_NICKNAME){
				System.out.println("Your current Nickname is ==>"+
						payee.getNickname());
				System.out.println("Enter new nickname:");
				String nickname = IOUtils.readStr();
				payee.setNickname(nickname);
			}else if(choice==UPDATE_DESCRIPTION){
				System.out.println("Your current Description is ==>"+
						payee.getDescription());
				System.out.println("Enter new Description:");
				String description = IOUtils.readStr();
				payee.setDescription(description);
			}else if(choice==UPDATE_NAME_ON_ACCOUNT){	
				System.out.println("Your current NameOnAccount is ==>"+
						payee.getPayItAllProfile().getNameOnAccount());
				System.out.println("Enter new NameOnAccount:");
				String nameOnAccount = IOUtils.readStr();
				payee.getPayItAllProfile().setNameOnAccount(nameOnAccount);
			}else if(choice==UPDATE_EMAIL){		
				System.out.println("Your current Email address is ==>"+
						payee.getPayItAllProfile().getEmail());
				System.out.println("Enter new email:");
				String email = IOUtils.readStr();
				payee.getPayItAllProfile().setEmail(email);
			}else if(choice==UPDATE_ADDRESS){
		 		updateAddress(payee);
			}
			else if(choice==0){			
				break;
			}else{
				System.out.println("Enter a valid number");
			}	
			
			System.out.println("********************");
			System.out.println(UPDATE_NICKNAME+ ". Update Nickname");
			System.out.println(UPDATE_NAME_ON_ACCOUNT+ ". Update NameOnAccount");
			System.out.println(UPDATE_DESCRIPTION+ ". Update Description");		
			System.out.println(UPDATE_EMAIL+ ". Update Email");
			if(payee.getPayItAllProfile().isCustom())
				System.out.println(UPDATE_ADDRESS+". Update Address");
			System.out.println("0. Exit");
			choice = IOUtils.readInt();
			
		}//end while
		
		try{
			payeeManagement.updatePayee(userContext,payee);			
			System.out.println("Successfully updated");
		}catch(InvalidUserContextExceptionFault e){
			System.out.println("Invalid User Context ExceptionFault");
			e.printStackTrace();
		}catch(IllegalArgumentValueExceptionFault e){
			System.out.println("Invalid User Context ExceptionFault");
			e.printStackTrace();
		}catch(InvalidConversationCredentialsExceptionFault icce){
			System.out.println("Invalid Conversation Credentials ExceptionFault");
			icce.printStackTrace();
		}catch(InvalidPayeeIdExceptionFault ipie){
			System.out.println("Invalid Payee Id ExceptionFault");
			ipie.printStackTrace();
		}catch(InvalidPayItAllProfileExceptionFault ipie){
			System.out.println("Invalid Payee Id ExceptionFault");
			ipie.printStackTrace();
		}catch(InvalidPayAnyoneProfileExceptionFault ipfie){
			System.out.println("Invalid Payee Id ExceptionFault");
			ipfie.printStackTrace();
		}catch(PayeeNicknameAlreadyInUseExceptionFault ipie){
			System.out.println("Invalid Payee Id ExceptionFault");
			ipie.printStackTrace();
		}catch(CoreExceptionFault ipie){
			System.out.println("Invalid Payee Id ExceptionFault");
			ipie.printStackTrace();
		}catch(RemoteException re){
			System.out.println("Invalid Payee Id ExceptionFault");
			re.printStackTrace();
		}
	
		
	}//End updatePayItAllPayee()
	
	/**
	 * Update address only for custom payees
	 * @param payee
	 */
	public void updateAddress(Payee payee){
		PayItAllProfile payitallprofile = payee.getPayItAllProfile();
		Address address=payitallprofile.getRemittanceAddress();
		System.out.print("Your Current Address is==>\n");		
		System.out.println(" Address1-"+address.getAddress1());
		System.out.println(" Address2-"+address.getAddress2());
		System.out.println(" ApartmentNumber-"+address.getApartmentOrSuiteNumber());
		System.out.println(" City-"+address.getCity());
		System.out.println(" State-"+address.getState().getValue());
		System.out.println(" Country-"+address.getCountry().getValue());
		
		System.out.println("Enter Address 1 : ");
		String address1=IOUtils.readStr();
		address.setAddress1(address1);
		
		System.out.println("Enter Address 2 : ");
		String address2=IOUtils.readStr();
		address.setAddress2(address2);
		
		System.out.println("Enter ApartmentNumber : ");
		String apartmentNumber = IOUtils.readStr();
		address.setApartmentOrSuiteNumber(apartmentNumber);
		
		System.out.println("Enter City : ");
		String city=IOUtils.readStr();
		address.setCity(city);
		
		State_US usstate=getState();
		address.setState(usstate);
		
		Country cCountry=getCountry();
		address.setCountry(cCountry);
		
		payitallprofile.setRemittanceAddress(address);
		payee.setPayItAllProfile(payitallprofile);
	}
	
	/**
	 * Displays list of US states, takes input from user and returns US State
	 * @return
	 */
	/**
	 * Displays list of US states, takes input from user and returns US State
	 * @return
	 */
	private State_US getState(){
				
		//First display all the state codes and ask user to input only state code
		System.out.println("\nFollowing are the state name and corresponding statecodes.\n");
		for(int i=1;i<BillPayConstants.statesArray.length;i++){
			System.out.println(BillPayConstants.statesArray[i-1]+" - "+ (i));
					
		}//End for
		
		State_US usstate=null;
		int statecode=0;
		System.out.println("\n\nEnter state code -");
		boolean loop=true;
		while(loop){
			statecode=IOUtils.readInt();
			if(statecode<0){
				System.out.println("This is mandatory field, please enter state -");				
			}else if(statecode>0){
				usstate = BillPayConstants.statesArray[statecode];
				if(usstate==null){
					System.out.println("Enter correct state code");
					continue;
				}else
					break;
			}
		}//End while
		return usstate;
	
	}
	
	/**
	 * Displays list of countries supported by Yodlee, takes input fro
	 * m user
	 * and return Country Object
	 * @return
	 */
	private Country getCountry(){
		
		//Display country code and description and ask user to input country code
		System.out.println("\n Following are the country id and corresponding country code");
		ListIterator iterator = countryNames.listIterator();
		
		while (iterator.hasNext()) {
			Country countryc = (Country)iterator.next();
			System.out.println(countryc.getValue());
		}
		
		Country cCountry=null;
		String country=null;
		boolean loop=true;
		System.out.println("Enter country code-");
		while(loop){
			country=IOUtils.readStr();
			if(country==null){
				System.out.println("This is mandatory field, please enter country -");				
			}else if(country.length()>0){ 
				cCountry = Country.fromString(country);
				if(cCountry==null){
					System.out.print("Enter correct country code - ");
					continue;
				}else
					break;
				
			}
		}//End while
		return cCountry;
	}
	
	/**
	 * Displays list of payees along with their details
	 * @param payees
	 */
	public void displayPayeeDetails(Payee payees[]){
		
		for(int i=0;i<payees.length;i++){
			Payee payee = payees[i];
			if(payee != null){
			PayItAllProfile payitallprofile = payee.getPayItAllProfile();
			System.out.println("\nPayeeId-"+payee.getPayeeId()
					+"\n PayeeName-"+payee.getPayItAllProfile().getPayeeName()
					+"\n DefaultRailId-"+payee.getDefaultRailId()
					+"\n NickName-"+payee.getNickname()
					+"\n Description="+payee.getDescription()
					+"\n NameOnAccount-"+payitallprofile.getNameOnAccount()
					+"\n Email-"+payitallprofile.getEmail()
					+"\n Address-");
					
			printAddress(payitallprofile.getRemittanceAddress());
		 }
		}
	}//End displayPayeeDetails(0
	
	/**
	 * This will custom payees and existing billers supported by Yodlee
	 * 
	 * before calling addPayee()
	 * @param userContext
	 * @param itemId
	 */
	public void addPayTodayPayee(UserContext userContext){
		
		//Enter keyword of the biller to search
		System.out.println("Enter Keyword");
		String keyword=IOUtils.readStr();
		//Area zipcode to further filter the entries that will be showed to user
		//System.out.println("Enter zipcode");
		//String zipcode=IOUtils.readStr();
		//Enter the description of the payee to be added
		System.out.println("Enter description");
		String description = IOUtils.readStr();
		//Enter the nickname for the payee
		System.out.println("Enter Nickname");
		String nickname = IOUtils.readStr();
		
		//Following code will fetch the biller information/services provided by Yodlee
		//depending on the keyword as input. Also fetch only active services.
		UslStatusFilterCriteria uslStatusFilterCriteria = null;
		//UslStatus uslStatus[]=new UslStatus[1];
		//uslStatus[0]= UslStatus.ACTIVE_STATUS;
		Service[] services =null;
		ArrayOfService serviceArray = null;
		//uslStatusFilterCriteria.setStatuses(new ArrayOfUslStatus(uslStatus));
		try{
			serviceArray = billerManagement.getServices(getCobrandContext(),keyword,
				uslStatusFilterCriteria);
			if (serviceArray != null){
				services = serviceArray.getElements();
			}
		}catch(InvalidUserContextExceptionFault iue){
			System.out.println("Invalid User Context");
			iue.printStackTrace();
		}catch(InvalidConversationCredentialsExceptionFault icce){
			System.out.println("Invalid Conversation Credentials");
			icce.printStackTrace();
		}catch(InvalidCobrandConversationCredentialsExceptionFault icce){
			System.out.println("Invalid Conversation Credentials");
			icce.printStackTrace();
		}catch(InvalidCobrandContextExceptionFault icce){
			System.out.println("Invalid Conversation Credentials");
			icce.printStackTrace();
		}catch(PayeeAlreadyExistsExceptionFault paee){
			System.out.println("Payee Already Exists");
			paee.printStackTrace();
		}catch(PayeeNicknameAlreadyInUseExceptionFault pnaiue){
			System.out.println("Payee Nickname Already InUse");
			pnaiue.printStackTrace();
		}catch(ContentServiceNotFoundExceptionFault csnfe){
			System.out.println("Content Service Not Found");
			csnfe.printStackTrace();
		}catch(ServiceNotFoundExceptionFault snf){
			System.out.println("Service Not Found");
			snf.printStackTrace();
		}catch(IllegalArgumentValueExceptionFault iave){
			System.out.println("Illegal Argument Value");
			iave.printStackTrace();
		}catch(InvalidPayItAllProfileExceptionFault ipia){
			System.out.println("Invalid PayItAll Profile");
			ipia.printStackTrace();
		}catch(IncompleteArgumentExceptionFault iae){
			System.out.println("Incomplete Argument");
			iae.printStackTrace();			
		}catch(InvalidItemActionTransactionIdExceptionFault iiat){
			System.out.println("Invalid Item Action Transaction Id");
		}catch(CoreExceptionFault e){
			System.out.println(" Got an ExceptionFault! ");
			e.printStackTrace();
		}catch(RemoteException rce){
			System.out.println("Invalid Conversation Credentials");
			rce.printStackTrace();
		}
	
		
		//If keyword serch didnt return any values go ahead and add custom payee
		if(services==null || services.length==0){
			System.out.println("No services found for given keyword");		
			return;
		}		
		
		//Display only 1st 10 Payees
		int counter=0;
		
		int isExpeditedSupportedCount=0;
		HashMap addresses=new HashMap();
		
		for(int i=0;i<services.length;i++){
			if (UslStatus.ACTIVE_STATUS.equals(services[i].getStatus())){
			if (services[i].isIsExpeditedSupported()==true){
				isExpeditedSupportedCount++;
				System.out.println((i+1)+". ServiceName- "+services[i].getServiceName()+"; " +
						"ServiceId- "+services[i].getServiceId()+" Is Expedited Supported" + services[i].isIsExpeditedSupported());		
				
			counter ++;
			} //end if
		}
		 if (counter == 10){
			break;
		 }
		} //End for
		if(isExpeditedSupportedCount==0){
			System.out.println("Service doesn't support Expedited Payments");
			return;
		}
		
		//Ask user to input service number
		int service=0, iaddress=0;
		while(true){
			System.out.println("\n\nEnter Service number displayed earlier. ");
			service = IOUtils.readInt();
			if(service> services.length || service<0){
				System.out.println("Please enter correct service number-");
			}
			else
				break;
		}//End While()
		
				
		//Create payItAllProfile
		PayItAllProfile payItAllProfile=getPayItAllProfileInfo(
				services[service-1].getServiceName(),
				services[service-1].getServiceId(), 
				null,false,true);
		
		Payee payee = new Payee();
		payee.setServiceId(services[service-1].getServiceId());
		payee.setPayItAllProfile(payItAllProfile);
		payee.setNickname(nickname);
		payee.setDescription(description);
		
		//Finally add Payee
		try{
			 payee=payeeManagement.addSmartForm(userContext, payee, false);
			
			System.out.println("Payee Id created is --> " +
					payee.getPayeeId());
			System.out.println("===== PayToday Payee added successful ======");
			
		}catch(InvalidUserContextExceptionFault iue){
			System.out.println("Invalid User Context");
			iue.printStackTrace();
		}catch(InvalidConversationCredentialsExceptionFault icce){
			System.out.println("Invalid Conversation Credentials");
			icce.printStackTrace();
		}catch(PayeeAlreadyExistsExceptionFault paee){
			System.out.println("Payee Already Exists");
			paee.printStackTrace();
		}catch(PayeeNicknameAlreadyInUseExceptionFault pnaiue){
			System.out.println("Payee Nickname Already InUse");
			pnaiue.printStackTrace();
		}catch(ContentServiceNotFoundExceptionFault csnfe){
			System.out.println("Content Service Not Found");
			csnfe.printStackTrace();
		}catch(ServiceNotFoundExceptionFault snf){
			System.out.println("Service Not Found");
			snf.printStackTrace();
		}catch(IllegalArgumentValueExceptionFault iave){
			System.out.println("Illegal Argument Value");
			iave.printStackTrace();
		}catch(InvalidPayItAllProfileExceptionFault ipia){
			System.out.println("Invalid PayItAll Profile");
			ipia.printStackTrace();
		
		}catch(IncompleteArgumentExceptionFault iae){
			System.out.println("Incomplete Argument");
			iae.printStackTrace();			
		}catch(InvalidItemActionTransactionIdExceptionFault iiat){
			System.out.println("Invalid Item Action Transaction Id");
		}catch(RemoteException e){
			System.out.println(" Got an Exception! ");
			e.printStackTrace();
		}
		return;
		
	}//end addPayItAllPayee
	
	
	/**
	 * Retreives the address of the payee from user
	 * @return
	 */
	public Address payeeAddress (){
		String address1="",address2,apartmentNumber,
		city="",state,country,zipcode1="",zipcode2;
		
		boolean loop=true;
		
//		Adress1 (Mandatory)
		System.out.println("Enter address1 (Mandatory) -");
		while(loop){			
			address1=IOUtils.readStr();
			if(address1==null){
				System.out.println("This is mandatory field, please enter address1 -");				
			}else{
				break;
			}
		}//End while
		
//		Adress2 (Mandatory)
		System.out.println("Enter address2 (Optional)-");
		address2=IOUtils.readStr();
		
//		apartmentNumber (Optional)
		System.out.println("Enter apartmentNumber (Optional)-");
		apartmentNumber=IOUtils.readStr();
		
//		City (Mandatory)
		System.out.println("Enter city (Mandatory)-");
		while(loop){
			city=IOUtils.readStr();
			if(city==null){
				System.out.println("This is mandatory field, please enter state -");				
			}else{
				break;
			}
		}//End while
		
//		State Mandatory
		State_US usstate=getState();
		
//		Country Mandatory
		Country cCountry=getCountry();
		
//		Zipcode1 (Mandatory)
		System.out.println("Enter zipcode1 (Mandatory)-");
		while(loop){
			zipcode1=IOUtils.readStr();
			if(zipcode1==null){
				System.out.println("This is mandatory field, please enter zipcode1 -");				
			}else{
				break;
			}
		}//End while
		
//		Zipcode2 (Optional)
		System.out.println("Enter zipcode2 (Optional)-");
		zipcode2=IOUtils.readStr();
		Address address = new Address();
		address.setAddress1(address1);
		address.setAddress2(address2);
		address.setApartmentOrSuiteNumber(apartmentNumber);
		address.setState(usstate);
		address.setCity(city);
		address.setZipCode1(zipcode1);
		address.setZipCode2(zipcode2);
			address.setCountry(	cCountry);
		return address;
	}
	
	private void printAddress(Address address)
    {
        if (address == null)
        {
            System.out.println("Address is not Available.");
            return;
        }
        System.out.println("      Address 1 --> " + address.getAddress1());
        System.out.println("      Address 2 --> " + address.getAddress2());
        System.out.println("      Apartment/Suite no --> " + address.getApartmentOrSuiteNumber());
        System.out.println("      City --> " + address.getCity());
        if(address.getState()!=null)
        	System.out.println("      State --> " + address.getState());
        if (address.getCountry() != null)
        	System.out.println("      Country --> " + address.getCountry());
        System.out.println("      Zip Code --> "+address.getZipCode1());
        System.out.println();
    }	
	
	
}// END CLASS