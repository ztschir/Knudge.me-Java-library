/*
 * Copyright 2009 Yodlee, Inc.  All Rights Reserved.  Your use of this code 
 * requires a license from Yodlee.  Any such license to this code is 
 * restricted to evaluation/illustrative purposes only. It is not intended 
 * for use in a production environment, and Yodlee disclaims all warranties 
 * and/or support obligations concerning this code, regardless of the terms 
 * of any other agreements between Yodlee and you."
 */
package com.yodlee.sampleapps;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.Hashtable;
import java.util.TimeZone;
import java.rmi.RemoteException;

import javax.xml.rpc.ServiceException;

import com.yodlee.soap.core.paymentservice.paymentservice.PaymentService;
import com.yodlee.soap.core.paymentservice.paymentservice.PaymentServiceServiceLocator;
import com.yodlee.soap.collections.List;
import com.yodlee.soap.core.paymentservice.PaymentAccountType;
import com.yodlee.soap.appext.paymentservice.paymentreconciliationservice.PaymentReconciliationService;
import com.yodlee.soap.appext.paymentservice.paymentreconciliationservice.PaymentReconciliationServiceServiceLocator;
import com.yodlee.soap.appext.paymentservice.ReconciledPayment;
import com.yodlee.soap.collections.appext.paymentservice.ArrayOfReconciledPayment;
import com.yodlee.soap.core.paymentservice.PayAnyoneProfile;
import com.yodlee.soap.common.Address;
import com.yodlee.soap.common.CalendarDate;
import com.yodlee.soap.common.CardType;
import com.yodlee.soap.common.CobrandContext;
import com.yodlee.soap.common.ContentServiceInfo;
import com.yodlee.soap.common.Country;
import com.yodlee.soap.common.DateFilter;
import com.yodlee.soap.common.HolidayInfo;
import com.yodlee.soap.common.RefreshInfo;
import com.yodlee.soap.common.holidaymanager.HolidayManagerService;
import com.yodlee.soap.common.holidaymanager.HolidayManager;
import com.yodlee.soap.common.holidaymanager.HolidayManagerServiceLocator;
import com.yodlee.soap.common.ItemSummary;
import com.yodlee.soap.common.Money;
import com.yodlee.soap.common.PhoneNumber_US;
import com.yodlee.soap.common.UserContext;
import com.yodlee.soap.core.IllegalArgumentValueExceptionFault;
import com.yodlee.soap.core.CoreExceptionFault;
import com.yodlee.soap.core.StaleConversationCredentialsExceptionFault;
import com.yodlee.soap.core.dataservice.dataservice.DataService;
import com.yodlee.soap.core.dataservice.dataservice.DataServiceService;
import com.yodlee.soap.core.dataservice.dataservice.DataServiceServiceLocator;
import com.yodlee.soap.core.dataservice.DataExtent;
import com.yodlee.soap.core.dataservice.ItemData;
import com.yodlee.soap.core.dataservice.types.BaseTagData;
import com.yodlee.soap.core.dataservice.types.ContainerTypes;
import com.yodlee.soap.core.dataservice.types.InsuranceLoginAccountData;
import com.yodlee.soap.core.dataservice.types.ItemAccountData;
import com.yodlee.soap.core.dataservice.types.LoanLoginAccountData;
import com.yodlee.soap.core.itemaccountmanagement.ItemAccountState;
import com.yodlee.soap.core.paymentservice.CardNumber;
import com.yodlee.soap.core.paymentservice.CardPaymentAccount;
import com.yodlee.soap.core.paymentservice.PaymentProfile;
import com.yodlee.soap.collections.core.paymentservice.ArrayOfPaymentProfile;
import com.yodlee.soap.collections.core.paymentservice.ArrayOfPaymentAccount;
import com.yodlee.soap.core.paymentservice.PaymentRequest;
import com.yodlee.soap.core.paymentservice.CardPaymentProfile;
import com.yodlee.soap.core.paymentservice.BankPaymentProfile;
import com.yodlee.soap.core.paymentservice.DirectPayment;
import com.yodlee.soap.core.paymentservice.Frequency;
import com.yodlee.soap.core.IncompleteArgumentExceptionFault;
import com.yodlee.soap.core.InvalidItemAccountIdExceptionFault;
import com.yodlee.soap.core.InvalidUserContextExceptionFault;
import com.yodlee.soap.core.InvalidCobrandContextExceptionFault;
import com.yodlee.soap.core.InvalidConversationCredentialsExceptionFault;
import com.yodlee.soap.core.InvalidCobrandConversationCredentialsExceptionFault;
import com.yodlee.soap.core.ContentServiceNotFoundExceptionFault;
import com.yodlee.soap.core.routingnumberservice.RoutingNumberNotFoundExceptionFault;
import com.yodlee.soap.core.ServiceNotFoundExceptionFault;
import com.yodlee.soap.core.SmartformValidationExceptionFault;
import com.yodlee.soap.core.paymentservice.BankPaymentAccount;
import com.yodlee.soap.core.paymentservice.InvalidPayItAllProfileExceptionFault;
import com.yodlee.soap.core.paymentservice.ItemAccountDisabledExceptionFault;
import com.yodlee.soap.core.paymentservice.InvalidPayeeIdExceptionFault;
import com.yodlee.soap.core.paymentservice.PayItAllProfile;
import com.yodlee.soap.core.paymentservice.PayeeAlreadyExistsExceptionFault;
import com.yodlee.soap.core.paymentservice.PayeeCurrentlyNotSupportedForEBillsExceptionFault;
import com.yodlee.soap.core.paymentservice.EBillPaymentNotSupportedExceptionFault;
import com.yodlee.soap.core.paymentservice.InvalidPaymentAccountIdExceptionFault;
import com.yodlee.soap.core.paymentservice.InvalidPaymentAccountStateChangeExceptionFault;
import com.yodlee.soap.core.paymentservice.PayeeNicknameAlreadyInUseExceptionFault;
import com.yodlee.soap.core.paymentservice.PaymentAccountAlreadyExistsExceptionFault;
import com.yodlee.soap.core.paymentservice.InvalidPaymentAmountExceptionFault;
import com.yodlee.soap.core.paymentservice.InvalidPaymentTransactionIdExceptionFault;
import com.yodlee.soap.core.paymentservice.PaymentMethod;
import com.yodlee.soap.core.paymentservice.PaymentNotSupportedExceptionFault;
import com.yodlee.soap.core.paymentservice.PaymentAlreadyScheduledExceptionFault;
import com.yodlee.soap.core.paymentservice.InvalidPaymentDateExceptionFault;
import com.yodlee.soap.core.paymentservice.PayeeStatusNotValidExceptionFault;
import com.yodlee.soap.core.paymentservice.InvalidDateTypeExceptionFault;

import com.yodlee.soap.core.paymentservice.PayNowRequest;
import com.yodlee.soap.core.paymentservice.Payee;
import com.yodlee.soap.collections.core.paymentservice.ArrayOfPayee;
import com.yodlee.soap.collections.ArrayOflong;
import com.yodlee.soap.core.paymentservice.payeemanagement.PayeeManagement;


import com.yodlee.soap.core.paymentservice.payeemanagement.PayeeManagementServiceLocator;
import com.yodlee.soap.core.paymentservice.Payment;
import com.yodlee.soap.collections.core.paymentservice.ArrayOfPayment;
import com.yodlee.soap.core.paymentservice.PaymentAccount;
import com.yodlee.soap.core.paymentservice.PaymentAccountId;
import com.yodlee.soap.collections.core.paymentservice.ArrayOfPaymentAccountIdentifier;
import com.yodlee.soap.core.paymentservice.PaymentAccountIdentifier;
import com.yodlee.soap.core.paymentservice.paymentaccountmanagement.PaymentAccountManagement;
import com.yodlee.soap.core.paymentservice.paymentaccountmanagement.PaymentAccountManagementService;
import com.yodlee.soap.core.paymentservice.paymentaccountmanagement.PaymentAccountManagementServiceLocator;
import com.yodlee.soap.core.paymentservice.PaymentFilterByPayee;
import com.yodlee.soap.core.paymentservice.PaymentFilter;
import com.yodlee.soap.core.paymentservice.PaymentFilterByPaymentAccount;
import com.yodlee.soap.core.paymentservice.PaymentFilterByPaymentMode;
import com.yodlee.soap.core.paymentservice.PaymentProfile;
import com.yodlee.soap.core.paymentservice.PaymentMode;
import com.yodlee.soap.collections.core.paymentservice.ArrayOfPaymentMode;
import com.yodlee.soap.core.paymentservice.DateType;
import com.yodlee.soap.core.paymentservice.ExpeditedPaymentRequest;
import com.yodlee.soap.core.paymentservice.ExpeditedPaymentNotAllowedExceptionFault;
import com.yodlee.soap.core.usermanagement.State_US;
import com.yodlee.soap.core.acl.aclservice.AclService;
import com.yodlee.soap.core.acl.aclservice.AclServiceService;
import com.yodlee.soap.core.acl.aclservice.AclServiceServiceLocator;
import com.yodlee.soap.ext.traversal.contentservicetraversal.ContentServiceTraversal;
import com.yodlee.soap.ext.traversal.contentservicetraversal.ContentServiceTraversalService;
import com.yodlee.soap.ext.traversal.contentservicetraversal.ContentServiceTraversalServiceLocator;
import com.yodlee.sampleapps.helper.IOUtils;
import com.yodlee.sampleapps.helper.BillPayConstants;
import com.yodlee.sampleapps.ManagePayee;
/**
 * 
 * BillPayment Sample Code
 *
 * 0.1 - Created
 * 
 * Sample card accounts
 * 	Visa 4111111111111111 
 * 	MasterCard 5500 0000 0000 0004 
 * 	American Express 3400 0000 0000 009 
 * 	Diner's Club 3000 0000 0000 04 
 * 	Discover 6011 0000 0000 000
 * 
 */
public class BillPayment extends ApplicationSuper
{
    protected DataService dataService;
    protected PaymentAccountManagement paymentAccountManagement;
    protected PayeeManagement payeeManagement;
    protected AclService aclService;
    protected HolidayManager holidayManager;
    protected PaymentReconciliationService prs;
    private static Hashtable  paymentAccountMap = null;
    // JH - BugID: 46558
    // protected DirectPaymentService directPaymentService;
    protected PaymentService paymentService;

    protected static int SLEEP_MILLIS = 10 * 1000;
    private static String userTimeZone="GMT+5:30";
    private boolean expeditedPayeesAvailable=false;
    public BillPayment ()
    {
        super ();
    
        //Create PaymentAccountManagement Locator 
        DataServiceServiceLocator dataServiceLocator= new DataServiceServiceLocator();
       String dataServiceName= dataServiceLocator.getDataServiceWSDDServiceName();
       dataServiceLocator.setDataServiceEndpointAddress(System.getProperty("com.yodlee.soap.services.url")
				+ "/" + dataServiceName);
       try {
    	   dataService = dataServiceLocator.getDataService();

		} catch (Exception lse) {

		}

        
        
		//Create PaymentAccountManagement Locator 
		PaymentAccountManagementServiceLocator  paymentAccManagementServiceLocator= new PaymentAccountManagementServiceLocator();
       String paymentAccountManagementServiceName= paymentAccManagementServiceLocator.getPaymentAccountManagementServiceWSDDServiceName();
       paymentAccManagementServiceLocator.setPaymentAccountManagementServiceEndpointAddress(System.getProperty("com.yodlee.soap.services.url")
				+ "/" + paymentAccountManagementServiceName);
       try {
    	   paymentAccountManagement = paymentAccManagementServiceLocator.getPaymentAccountManagementService();

		} catch (Exception lse) {

		}
		
//		Create PayeeManagement Locator 
		 PayeeManagementServiceLocator  payeeManagementServiceLocator= new PayeeManagementServiceLocator();
       String payeeManagementserviceName= payeeManagementServiceLocator.getPayeeManagementServiceWSDDServiceName();
       payeeManagementServiceLocator.setPayeeManagementServiceEndpointAddress(System.getProperty("com.yodlee.soap.services.url")
				+ "/" + payeeManagementserviceName);
       try {
       	payeeManagement = payeeManagementServiceLocator.getPayeeManagementService();

		} catch (Exception lse) {

		}

        //Create paymentService Locators
        PaymentServiceServiceLocator  paymentServiceLocator= new PaymentServiceServiceLocator();
        String paymentServiceName= paymentServiceLocator.getPaymentServiceWSDDServiceName();
        paymentServiceLocator.setPaymentServiceEndpointAddress((System.getProperty("com.yodlee.soap.services.url")
 				+ "/" + paymentServiceName));
        try {
        	paymentService = paymentServiceLocator.getPaymentService();

 		} catch (Exception lse) {

 		}

      
        //Create aclService Locators
        AclServiceServiceLocator  aclServiceLocator= new AclServiceServiceLocator();
        String aclServiceName= aclServiceLocator.getAclServiceWSDDServiceName();
        aclServiceLocator.setAclServiceEndpointAddress((System.getProperty("com.yodlee.soap.services.url")
 				+ "/" + aclServiceName));
        try {
        	aclService = aclServiceLocator.getAclService();

 		} catch (Exception lse) {

 		}
 		
 		//Create aclService Locators
 		PaymentReconciliationServiceServiceLocator  paymentReconciliationServiceLocator = new PaymentReconciliationServiceServiceLocator();
        String paymentReconciliationServiceName = paymentReconciliationServiceLocator.getPaymentReconciliationServiceWSDDServiceName();
        paymentReconciliationServiceLocator.setPaymentReconciliationServiceEndpointAddress((System.getProperty("com.yodlee.soap.services.url")
 				+ "/" + paymentReconciliationServiceName));
        try {
        	prs = paymentReconciliationServiceLocator.getPaymentReconciliationService();

 		} catch (Exception lse) {

 		}


    }
   
    public void addDirectpayPayee(UserContext userContext){
    	

		System.out.print("Enter Content ServiceId: ");

        long csid = IOUtils.readInt();
        
        System.out.print("Enter payee description : ");
        String desc = IOUtils.readStr();
        System.out.print("Enter payee nick name : ");
        String nickName = IOUtils.readStr();
        
        AddItem ad = new AddItem();
        long itemId = ad.doAddItem(userContext, csid);
        
     // Only continue if Item was added
        if (itemId != 0) {
            // Refresh new Item
            RefreshItem refreshItem = new RefreshItem();
            ContentServiceHelper csh = new ContentServiceHelper();
            String mfatype = csh.getMfAType(csid);
            if ( mfatype != null) {            	
            	refreshItem.refreshItem(userContext, itemId,true);
            } else {
            	refreshItem.refreshItem(userContext, itemId,false);
            }
            // Poll for the refresh status and display item
            // summary if refresh succeeds.
            if (refreshItem.pollRefreshStatus(userContext, itemId)) {
                AccountSummary as = new AccountSummary();
                as.displayItemSummary(userContext, itemId);
            }

            // Get RefreshInfo for item added
            RefreshInfo ri = refreshItem.getRefreshInfo(userContext, itemId);

            // Only Add Payees if item successfull refreshed
            if ((ri != null) && (ri.getStatusCode() == 0)) {
                // Only add Payee if Direct Card Payment is supported
                if (BillPayment.isDirectCardPaymentSupported(getCobrandContext(),
                            csid)) {
                    // Add Payees for this ItemId
                	Payee payee = null;
                	try {
                		Long itemAccountId = getItemAccountId(userContext, itemId);
                		
                  		payee = payeeManagement.addPayee(userContext, null, itemAccountId, desc, nickName, null);
                    } catch (Exception e){
                	e.printStackTrace();
                    }
                    if (payee != null){
                     System.out.println("payee  successfully added : payee Id : " + payee.getPayeeId());
                    }
                    } else { 
                	System.out.println("Direct pay is not supported for this  content service");
                   }
            
          } else {
        	System.out.println("item is not successfully refreshed");
          }
	
        }	
      	
    }
        
        private Long getItemAccountId(UserContext userContext, long itemId){
        	
        	DataExtent dataExtent = new DataExtent();
        	dataExtent.setStartLevel(0);
        	dataExtent.setEndLevel(Integer.MAX_VALUE);
        	
            ItemSummary is = null;
            Long itemAccountId = null;
			try {
				 is = dataService.getItemSummaryForItem1(userContext, new Long(itemId), dataExtent );	
				
				if (is != null){
					
					ItemData id = is.getItemData();
					if(id != null){  
						
						Object[] itemAccounts = id.getAccounts().getElements();
						BaseTagData baseTagData =  (BaseTagData)itemAccounts[0];
                        itemAccountId =  baseTagData.getItemAccountId();
                        System.out.println("item account id is :" + itemAccountId);
					}
				}
			} catch (Exception e){
				e.printStackTrace();
			}
			
			return itemAccountId;
        }
        
    
    public static boolean isDirectCardPaymentSupported(CobrandContext cc, long csId){
    	
    	ContentServiceTraversal cst=null;
    	ContentServiceInfo csi=null;
    	//Create ContentServiceLocators
    	ContentServiceTraversalServiceLocator locator = new ContentServiceTraversalServiceLocator();
		String serviceName = locator
				.getContentServiceTraversalServiceWSDDServiceName();
		locator
				.setContentServiceTraversalServiceEndpointAddress(System.getProperty("com.yodlee.soap.services.url")
						+ "/" + serviceName);
		try {
			cst = locator.getContentServiceTraversalService();

		} catch (ServiceException se) {
			throw new RuntimeException(se);
		}
		
 		try{
    	 csi= cst.getContentServiceInfo(cc,new Long(csId));
 		}catch(Exception e) {
 			e.printStackTrace();
		}
		
 			
        return csi.isDirectCardPaymentSupported();
    }

    
    public PaymentAccount addPaymentAccount(UserContext userContext, PaymentAccount paymentAccount)
    {    
    	PaymentAccount pam=null;
    	try{
        pam=paymentAccountManagement.addPaymentAccount(userContext, paymentAccount);
    	}catch(InvalidUserContextExceptionFault iuc){
			System.out.println("userContext Invalid");
			iuc.printStackTrace();
		}catch(CoreExceptionFault iuc){
			System.out.println("userContext Invalid");
			iuc.printStackTrace();
		}catch(IllegalArgumentValueExceptionFault iavef){
			System.out.println("Illegal Argument Value");
		}catch(RoutingNumberNotFoundExceptionFault iavef){
			System.out.println("Routing Number Not Found ");
		}catch(PaymentAccountAlreadyExistsExceptionFault iavef){
			System.out.println("Payment Account Already Exists");
		}catch(InvalidConversationCredentialsExceptionFault icce){
  			System.out.println("Invalid Conversation Credentials");
  		}catch(StaleConversationCredentialsExceptionFault icce){
  			System.out.println("Stale Conversation Credentials");
  		}catch(RemoteException re){
  			System.out.println("Remote Exception");
  		}
  		Long paymentAccountId = ((PaymentAccountId)pam.getPaymentAccountIdentifier()).getPaymentAccountId();
  		System.out.println("payment acct id "+paymentAccountId);
    	return pam;
    	
    }

    public PaymentAccount[] getPaymentAccounts(UserContext userContext){
    	
    	ArrayOfPaymentAccount paymentAccountArray = null;
    	PaymentAccount[] pam=null;

    	try{
    		paymentAccountArray = paymentAccountManagement.getPaymentAccounts(userContext);
    		if (paymentAccountArray!=null){
    			pam=paymentAccountArray.getElements();
    		}
    	}catch(InvalidUserContextExceptionFault iuc){
			System.out.println("userContext Invalid");
			iuc.printStackTrace();
		}catch(RemoteException re){
  			System.out.println("Remote Exception");
  		}
		return pam;
    }

    public PaymentAccount updatePaymentAccount(UserContext userContext, PaymentAccount paymentAccount) {
    	PaymentAccount pam=null;
    	try{
    	pam = paymentAccountManagement.updatePaymentAccount(userContext, paymentAccount);
    	}catch(InvalidUserContextExceptionFault iuc){
			System.out.println("userContext Invalid");
			iuc.printStackTrace();
		}catch(IllegalArgumentValueExceptionFault iavef){
			System.out.println("Illegal Argument Value");
		}catch(RemoteException re){
  			System.out.println("Remote Exception");
  		}
		return pam;
        
    }

    public Payee addPayee(UserContext userContext, Payee payee){
    	Payee paye= null;
        try{
    	paye= payeeManagement.addPayee2(userContext, payee);
        }
        catch(InvalidUserContextExceptionFault iuc){
			System.out.println("userContext Invalid");
			iuc.printStackTrace();
		}catch(RemoteException re){
  			System.out.println("Remote Exception");
  			re.printStackTrace();
  		}
		return paye;
    }
    
    public Payment getPayment(UserContext userContext,String transId){
    	Payment pam = null;
    	try{
        pam = paymentService.getPayment(userContext, transId);
    	}catch(InvalidUserContextExceptionFault iuc){
			System.out.println("userContext Invalid");
			iuc.printStackTrace();
		}catch(InvalidConversationCredentialsExceptionFault icce){
  			System.out.println("Invalid Conversation Credentials");
  		}catch(StaleConversationCredentialsExceptionFault icce){
  			System.out.println("Stale Conversation Credentials");
  		}catch(InvalidPaymentTransactionIdExceptionFault iptef){
  			System.out.println("Invalid Payment Transaction Id");
  		}
  		catch(IllegalArgumentValueExceptionFault iavef){
			System.out.println("Illegal Argument Value");
		}catch(CoreExceptionFault cef){
  			System.out.println("Core Exception ");
  			cef.printStackTrace();
  		}catch(RemoteException re){
  			System.out.println("Remote Exception");
  		}
  		return pam;
    }

   
    /** 
     * Create CardPayment Account with Fake Data
     * @return CardPaymentAccount
     */
    public CardPaymentAccount createSampleCardAccount()
    {
        String address1="Address1";
        String address2="Address2";
        String apt="#2";
        String city="San Francisco";
        String zip1="94111";
        String zip2="";

        State_US state = State_US.CALIFORNIA;
        
        System.out.println("** Please enter the Card Billing Address");
        System.out.print("Enter billing address Street 1 [" + address1 +"]");
        address1 = IOUtils.readStr(address1);
        System.out.print("Enter billing address Street 2 [" + address2 +"]");
        address2 = IOUtils.readStr(address2);
        System.out.print("Enter Apt [" + apt+"]");
        apt = IOUtils.readStr(apt);
        System.out.print("Enter City [" + city+"]");
        city = IOUtils.readStr(city);
        System.out.print("States:\n");
//      First display all the state codes and ask user to input only state code
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
        System.out.print("Enter zip 1 [" + zip1 +"]");
        zip1 = IOUtils.readStr(zip1);
        System.out.print("Enter zip 2 [" + zip2 +"]");
        zip2 = IOUtils.readStr(zip2);



        Address address = new Address();
        address.setAddress1(address1);
        address.setAddress2(address2);
        address.setApartmentOrSuiteNumber(apt);
        address.setCity(city);
        address.setState(state);
        address.setZipCode1(zip1);
        address.setZipCode2(zip2);    
             
        System.out.println("Your billing address: " + address + "\n");

        System.out.println("**Please provide Card Details");
        int expirationYear = 2010;
        int expirationMonth = 0;
        int expirationDay = 1;
        String phone1="800";
        String phone2="555";
        String phone3="1234";

        String cardDescription="Card description";
        String nickname="Nickname";
        String nameOnCard="John Smith";
        String number = "123412345612345";
        String cardType = null;


        System.out.print("Enter the Credit Card # [" + number + "]:");
        number = IOUtils.readStr(number);
        System.out.print("Enter the Card description [" + cardDescription + "]:");
        cardDescription = IOUtils.readStr(cardDescription);
        System.out.print("Enter a Card nickname [" + nickname + "]:");
        nickname = IOUtils.readStr(nickname);
        System.out.print("Enter the name on Card [" + nameOnCard + "]:");
        nameOnCard = IOUtils.readStr(nameOnCard);
        System.out.println(" Possible Card Types are:");
        System.out.println("1." + CardType.AMERICAN_EXPRESS);
        System.out.println("2." + CardType.VISA);
        System.out.println("3." + CardType.MASTERCARD);
        System.out.println("4." + CardType.DISCOVER_CARD);
        System.out.println("5." + CardType.DINERS_CLUB); 
        boolean loop1=true;
		while(loop1){
			System.out.print("Enter Card Type:");
			cardType = IOUtils.readStr(cardType);			
			if(cardType==null){
				System.out.println("This is a mandatory field, please enter card type");				
			}else {
				if(cardType.equals(CardType._AMERICAN_EXPRESS) || cardType.equals(CardType._MASTERCARD)
					|| cardType.equals(CardType._VISA)|| cardType.equals(CardType._DISCOVER_CARD)
					|| cardType.equals(CardType._DINERS_CLUB)){					
					loop1=false;
				}else
					System.out.println("Please enter correct card type.");
					
			}
		}//End while

        System.out.print("Enter expiration year: [" + expirationYear + "]");
        expirationYear = Integer.parseInt(IOUtils.readStr(String.valueOf(expirationYear)));
        System.out.print("Enter expiration month (Jan=0): [" + expirationMonth + "]");
        expirationMonth = Integer.parseInt(IOUtils.readStr(String.valueOf(expirationMonth)));
        System.out.print("Enter expiration day: [" + expirationDay + "]");
        expirationDay = Integer.parseInt(IOUtils.readStr(String.valueOf(expirationDay)));
        CalendarDate expirationDate = new CalendarDate();
        expirationDate.setYear(new Integer(expirationYear));
        expirationDate.setMonth(new Integer(expirationMonth));
        expirationDate.setDayOfMonth(new Integer(expirationDay));
        System.out.println("Entered expiration date: " + expirationDate.getDayOfMonth()+ "\n");

        System.out.print("Enter first 3 digits of tel num [" + phone1 + "]:");
        phone1 = IOUtils.readStr(phone1);
        System.out.print("Enter next 3 digits of tel num [" + phone2 + "]:");
        phone2 = IOUtils.readStr(phone2);
        System.out.print("Enter next 3 digits of tel num [" + phone3 + "]:");
        phone3 = IOUtils.readStr(phone3);
        PhoneNumber_US phoneNumber = new PhoneNumber_US();
        phoneNumber.setAreaCode(phone1);
        phoneNumber.setPrefix(phone2);
        phoneNumber.setLineNumber(phone3);
       
        System.out.println("Entered phone number: " + phoneNumber.getPrefix()+phoneNumber.getAreaCode()+phoneNumber.getLineNumber());


        CardPaymentAccount cpa = new CardPaymentAccount();
        cpa.setDescription(cardDescription);
        cpa.setNickname(nickname);
        cpa.setBillingAddress(address);
        cpa.setPhoneNumber(phoneNumber);
        cpa.setExpirationDate(expirationDate);
        cpa.setCardNumber(number);        
        cpa.setCardType(CardType.fromValue(cardType));        
        cpa.setNameOnCard(nameOnCard);
		cpa.setContentServiceId(new Long(12644));
        
        System.out.println("Entered Card details for card: " + cpa.getNickname());

        return cpa;
    }

        
    
    /**
     * Post Payment
     * @param userContext
     * @param payeeId
     * @param cardNumber
     * @return Transaction Id
     */
    public String postPayment(UserContext userContext, long payeeId, String cardNumber, String securityCode) {
        System.out.print("Please enter your payment Amount: ");
        String amount = IOUtils.readStr();

        System.out.print("Please enter your payment Memo: ");
        String memo = IOUtils.readStr();

        // Set up the payment account identifier
        CardNumber cardNum = new CardNumber();
        cardNum.setCardNumber(cardNumber);
        PaymentAccountIdentifier paymentAccountIdentifier = cardNum;
        //PaymentAccountIdentifier paymentAccountIdentifier = new CardNumber(cardNumber);
        
        if(paymentAccountIdentifier == null){
            System.out.println("Invalid Card Number");
            return null;
        }

        Payee payee = this.getPayee(userContext, payeeId);
        ArrayOfPaymentProfile paymentProfileArray=  new ArrayOfPaymentProfile();
        PaymentProfile[] paymentProfiles=null;
        paymentProfileArray = payee.getPaymentProfiles();
        if (paymentProfileArray != null){
        	paymentProfiles=paymentProfileArray.getElements();
        }
     //   PaymentProfile paymentProfile = this.getCardPaymentProfile(paymentProfiles) ;

/*        int numDaysToDelayPayment = 0;
        System.out.print("Please enter the number of days you wish to delay this payment [0]: ");
        numDaysToDelayPayment = IOUtils.readInt();*/
        
        PayNowRequest payNowRequest = new PayNowRequest();
        payNowRequest.setCVVCode("0000");
        payNowRequest.setPayeeId(new Long(payeeId));
        payNowRequest.setPaymentAccountIdentifier(paymentAccountIdentifier);
        Money money=new Money();
        money.setAmount(new BigDecimal(amount));
        money.setCurrencyCode("USD");
        payNowRequest.setPaymentAmount(money);
 
        payNowRequest.setPaymentMemo(memo);
        payNowRequest.setFrequency(Frequency.ONE_TIME);
        
//      Get Federal Holidays
        //HolidayInfo holidayInfo = this.getFederalHolidays(getCobrandContext(),null, null ) ;
        //HolidayInfo[] holidayInfos = {holidayInfo}; // This is ok for Card Payment Account because there is only one rail.  This mends to be changed when it is a BANK Payment Account

        // Get Earliest Payment Date
       
        // Gotta fix this! It won't work this way. Pay now is only used for payments within 24 hours.
/*        Calendar earliestCal;
        if (numDaysToDelayPayment > 0) {
        	earliestCal = Calendar.getInstance();
        	earliestCal.add(Calendar.DATE, numDaysToDelayPayment);
        	earliestCal = PaymentRequestHelper.getNearestPaymentDateOnOrAfter(earliestCal, paymentProfile, holidayInfos) ;
        } else {
        	earliestCal = PaymentRequestHelper.getEarliestPaymentDate(paymentProfile, holidayInfos) ;
        }*/
      Calendar earliestCal = Calendar.getInstance();
	  earliestCal.add(Calendar.DAY_OF_MONTH, 1);
       
               
        //System.out.println("PaymentRequestHelper.getEarliestPaymentDate="+earliestCal.getTime());
        payNowRequest.setStartDate(earliestCal);
        Long payNowPaymentTransactionID=null;
		try{
			payNowPaymentTransactionID = paymentService.createPayNowPayment(userContext, payNowRequest);
        }
        catch(Exception e){
  			e.printStackTrace();
  		}
        
        DirectPayment directPayment =null;
        	try{
        directPayment=paymentService.schedulePayNowPayment(userContext, "0000", payNowPaymentTransactionID);
        	} catch(PaymentAlreadyScheduledExceptionFault pasef){
        		System.out.println("Payment Already Scheduled Exception");
        		pasef.printStackTrace();
        	}catch(InvalidConversationCredentialsExceptionFault icce){
      			System.out.println("Invalid Conversation Credentials");
      			icce.printStackTrace();
      		}catch(StaleConversationCredentialsExceptionFault icce){
      			System.out.println("Stale Conversation Credentials");
      			icce.printStackTrace();
      		}catch(IllegalArgumentValueExceptionFault iave){
      			System.out.println("Illegal Argument value");
      			iave.printStackTrace();
      		}catch(CoreExceptionFault cef){
      			System.out.println("Core Exception");
      			cef.printStackTrace();
      		}catch(RemoteException re){
      			System.out.println("Core Exception");
      			re.printStackTrace();
      		}
        System.out.println("Posted payment with scheduledPaymentRequestID: " +  payNowPaymentTransactionID);
        return directPayment.getPaymentTransactionId();
    }

    
  
    public void stopPayment(UserContext userContext) {
        String paymentTransactionId = null;
        System.out.print("Enter in the payment transaction id to cancel: " );
        paymentTransactionId = IOUtils.readStr();
        //directPaymentService.stopPayment(userContext, paymentTransactionId);
        try{
        	paymentService.cancelScheduledPayment1(userContext, paymentTransactionId);
        }catch(InvalidUserContextExceptionFault iuce){
  			System.out.println("Invalid User Context");
  		}catch(InvalidPaymentTransactionIdExceptionFault iptef){
  			System.out.println("Invalid Payment Transaction Id");
  		}catch(InvalidConversationCredentialsExceptionFault icce){
  			System.out.println("Invalid Conversation Credentials");
  		}catch(StaleConversationCredentialsExceptionFault icce){
  			System.out.println("Stale Conversation Credentials");
  		}catch(CoreExceptionFault cce){
  			System.out.println("Core Exception");
  		}catch(RemoteException re){
  			System.out.println("Remote Exception");
  			re.printStackTrace();
  		}
        System.out.println("Request sent to stop payment - This can take 2 hours");
    }

    /**
     *  View Payment Accounts (Credit Card)
     *
     */
    public void viewPaymentAccountsDetails(UserContext userContext)
    {

        PaymentAccount[] pas = getPaymentAccounts(userContext);
        if( pas.length == 0 ){
            System.out.println("You have no Payment Accounts");
            return;
        }
        int cardAccountCount = 0;
        int j;
        for(int i=0; i<pas.length; i++){
        	if (i==0){
        		System.out.println(" You have the following Card Payment Accounts. ");
        	}
        	j = i + 1;
        	if (pas[i] instanceof CardPaymentAccount){
	             CardPaymentAccount cpa = (CardPaymentAccount)pas[i];
	             System.out.println( j + ". " + cpa.getCardNumber() );
	             cardAccountCount++;
        	}
        }
        
        if (cardAccountCount == 0){
        	System.out.println("You have no Card Payment Accounts");
            return;
        }
        System.out.print("Please select Card Payment Account by providing the index: ");
        int index = IOUtils.readInt();
        CardPaymentAccount cpa; 
        try{
            cpa = (CardPaymentAccount)pas[index-1];
        }catch( Exception e){
            // Probably array out of bounds
            System.out.println("Bad choice");
            return;
        }
        System.out.println("  Card Nickname:        " + cpa.getNickname());
        System.out.println("  Name on Card:         " + cpa.getNameOnCard());
        System.out.println("  Card Number:          " + cpa.getCardNumber());
        System.out.println("  Card Description:     " + cpa.getDescription());
        System.out.println("  Payment Account Id:   " + ((PaymentAccountId)cpa.getPaymentAccountIdentifier()).getPaymentAccountId());
        System.out.println("  Item Id:              " + cpa.getItemId());
        System.out.println("  Item Account Id:      " + cpa.getItemAccountId());       
    }
    
    /**
     *  Removes Payment Account (Credit Card)
     *
     * @param userContext the usercontext to remove an account from
     */    
    public void removePaymentAccount(UserContext userContext) {
        PaymentAccount[] pas = getPaymentAccounts(userContext);
        if( pas.length == 0 ){
            System.out.println("You have no Payment Accounts");
            return;
        }
        for(int i=0; i<pas.length; i++){
        	if(pas[i] instanceof CardPaymentAccount) {
        		CardPaymentAccount cpa = (CardPaymentAccount)pas[i];
        		System.out.println(i + ". Card #" + cpa.getCardNumber() );
        	} else if(pas[i] instanceof BankPaymentAccount) {
        		BankPaymentAccount bpa = (BankPaymentAccount)pas[i];
        		System.out.println(i + ". Bank #" + bpa.getPaymentAccountIdentifier() );
        	}
        }

        System.out.print("Choose Payment Account: ");
        int index = IOUtils.readInt();
        CardPaymentAccount cpa;
        try{
            cpa = (CardPaymentAccount)pas[index];
        }catch( Exception e){
            // Probably array out of bounds
            System.out.println("Bad choice");
            return;
        }
        try{
        paymentAccountManagement.removePaymentAccount1(userContext, 
        		cpa.getPaymentAccountIdentifier(),false);
        }catch(InvalidUserContextExceptionFault iuce){
  			System.out.println("Invalid User Context");
  		}catch(InvalidPaymentAccountStateChangeExceptionFault ipace){
  			System.out.println("Invalid Payment Account Status");
  		}catch(InvalidConversationCredentialsExceptionFault icce){
  			System.out.println("Invalid Conversation Credentials");
  		}catch(StaleConversationCredentialsExceptionFault icce){
  			System.out.println("Stale Conversation Credentials");
  		}catch(CoreExceptionFault cce){
  			System.out.println("Core Exception");
  		}catch(RemoteException re){
  			System.out.println("Remote Exception");
  			re.printStackTrace();
  		}
  		
        
    }

    /**
     * List Payment Accounts(Credit Card)
     * @param userContext
     */
    public void listPaymentAccounts(UserContext userContext)
    {

        PaymentAccount[] pas = getPaymentAccounts(userContext);
        if( pas.length == 0 ){
            System.out.println("You have no Payment Accounts");
            return;
        }
        
        int cardPaymentAccountCount = 0;
        int j = 0;
        for(int i=0; i<pas.length; i++){        	
        	if (pas[i] instanceof CardPaymentAccount){
        		j = i + 1;
				CardPaymentAccount cpa = (CardPaymentAccount)pas[i];
				System.out.println(j+ "." + cpa.getCardNumber());
				cardPaymentAccountCount ++;
        	}
        }
        
        if (cardPaymentAccountCount == 0){
        	System.out.println("You have no card Payment Accounts");
            return;
        }
    }

    /**
     * View Payees
     * @param userContext
     */
    public void viewPayees(UserContext userContext)
    {
        System.out.println("Viewing all Payees");
        Payee[] payees = null;
        ArrayOfPayee payeeArray=new  ArrayOfPayee();
		try{
			System.out.println("Getting Payees");
			payeeArray = payeeManagement.getPayees3(userContext);
			if (payeeArray!= null){
				payees = payeeArray.getElements();
			}
			if(payees == null || payees.length==0){	
				System.out.println("no payees found");
				return;
			}			
		}catch(InvalidUserContextExceptionFault iuc){
			System.out.println("Invalid User Context");
		}catch(InvalidConversationCredentialsExceptionFault icc){
			System.out.println("Invalid Conversation Credentials");
		}catch(Exception ex){
			if (payees!=null){
				System.out.println("Returned ..........Got "+payees.length+" payees");
			}
			ex.printStackTrace();
		}
    		
		// Determining if there are any Direct Pay Payees
		int directPayPayeeCount = 0;
		for (int i = 0; i < payees.length; i++) {
			Payee payee = payees[i];
			PaymentMethod [] paymentmethods = null;	
			if (payee.getSupportedPaymentMethods() != null ) {
				paymentmethods = payee.getSupportedPaymentMethods().getElements();
				for (int j =0; j<paymentmethods.length ; j++){
					if (paymentmethods[j].equals(PaymentMethod.DIRECT_PAYMENT)) {
						directPayPayeeCount++;
					} // end of inner if
				}	// end of inner for
			}// end of if
		}// end of for loop
	
        	

    	if (directPayPayeeCount == 0){
    		System.out.println("You do not have any payee that supports Direct Pay payment");
    		return;
    	}
    	
    	System.out.println(" You have "+directPayPayeeCount+" Direct Pay payee(s)");
    	
	    Payee [] directPayPayees = new Payee[directPayPayeeCount];
    	Long [] payeeItemIDs = new Long[directPayPayeeCount];
    	ArrayOflong payeeItemIDsArray=new ArrayOflong();
    	payeeItemIDsArray.setElements(payeeItemIDs);
    	int payeeItemIdCount =0;
    	for (int i = 0; i < payees.length; i++) {
			Payee payee = payees[i];
			PaymentMethod [] paymentmethods = null;
			if (payee.getSupportedPaymentMethods() != null ) {
				 paymentmethods = payee.getSupportedPaymentMethods().getElements();
				 for (int j =0; j<paymentmethods.length ; j++){
			    	if (paymentmethods[j].equals(PaymentMethod.DIRECT_PAYMENT)) {
			    		payeeItemIDs[payeeItemIdCount] = payees[i].getItemId();
			    		directPayPayees[payeeItemIdCount] = payees[i];
			    		payeeItemIdCount++;
			    	}// end of inner if
				 }// end of inner for			
			}// end of if
        }// end of for

    	// Get ordered list of itemIDs for payees
        	
        // Pass in itemIDs to obtain List of itemSummaries.
        
        List objitemSummaries = null;
        try{
        	objitemSummaries =  dataService.getItemSummaries3(userContext, payeeItemIDsArray);
        }catch(InvalidUserContextExceptionFault iuce){
  			System.out.println("Invalid User Context");
  		}catch(IllegalArgumentValueExceptionFault iave){
  			System.out.println("Illegal Argument value");
  		}catch(InvalidConversationCredentialsExceptionFault icce){
  			System.out.println("Invalid Conversation Credentials");
  		}catch(StaleConversationCredentialsExceptionFault icce){
  			System.out.println("Stale Conversation Credentials");
  		}catch(CoreExceptionFault cce){
  			System.out.println("Core Exception");
  		}catch(RemoteException re){
  			System.out.println("Remote Exception");
  			re.printStackTrace();
  		}
  		
        
        Object[] itemSummaries = objitemSummaries.getElements();
        if(itemSummaries == null || itemSummaries.length== 0 ){
            System.out.println("You do not have any Payees Added.");
        } else {
        	// Iterate through payees. Print Payee/ItemSummary information.
	        for (int i = 0; i < directPayPayees.length; i++) {
        	    Payee payee = directPayPayees[i];
        	    System.out.println("=========================================");
        	    System.out.println("  Payee Nick Name:		" + payee.getNickname());
        	    System.out.println("  ContentServiceID:		" + payee.getContentServiceId());        	    
        	    System.out.println("  PayeeId:			" + payee.getPayeeId());
        	    System.out.println("  ItemId:			" + payee.getItemId());
        	    System.out.println("  ItemAccountId:		" + payee.getItemAccountId());
                System.out.println("  DisplayName:			" + payee.getItemSummary().getContentServiceInfo().getContentServiceDisplayName());
                System.out.println("=========================================");
                     
        	}   
         }
    }
    
    
    /**
     * View Payments
     * @param userContext
     */
    public void viewPayments(UserContext userContext) {
    	int months = 2;
    	System.out.println("Viewing Payments made in tha past " + months + " months.");
        
        Calendar todayCal = Calendar.getInstance();
        Calendar prevYearCal = Calendar.getInstance();
        prevYearCal.add(Calendar.MONTH, -months);

//        Date startDate = prevYearCal.getTime();
  //      Date endDate =  todayCal.getTime();
        
        // Create Interface
        
        PaymentFilterByPayee paymentFilterByPayee = null;
        DateFilter paymentFilterByPaymentDate = new DateFilter();
        paymentFilterByPaymentDate.setStartDate(prevYearCal);
        paymentFilterByPaymentDate.setEndDate(todayCal);
 
        DateFilter paymentFilterByPaymentRequestDate = new DateFilter();
        paymentFilterByPaymentRequestDate.setStartDate(prevYearCal);
        paymentFilterByPaymentRequestDate.setEndDate(todayCal);
  
        PaymentFilterByPaymentAccount paymentFilterByPaymentAccount = null;

        ReconciledPayment[] reconciledPayments = null;
        ArrayOfReconciledPayment reconciledPaymentsArray = new ArrayOfReconciledPayment();
        try{
        	reconciledPaymentsArray = prs.getPayments(userContext,
                    paymentFilterByPayee,
                    paymentFilterByPaymentAccount,
                    paymentFilterByPaymentDate,
                    paymentFilterByPaymentRequestDate,
                    true) ;
        	if (reconciledPaymentsArray!= null){
        		reconciledPayments=reconciledPaymentsArray.getElements();
        	}
        }catch (InvalidPayeeIdExceptionFault invalidPayeeId) {
        	System.out.println("InvalidPayeeIdExceptionFault calling PaymentReconciliationService");
            invalidPayeeId.printStackTrace();
        }catch (InvalidPaymentAccountIdExceptionFault invalidPaymentAccountId) {
        	System.out.println("InvalidPaymentAccountIdExceptionFault calling PaymentReconciliationService");
            invalidPaymentAccountId.printStackTrace();
        }catch (IllegalArgumentValueExceptionFault illegalArg) {
        	System.out.println("IllegalArgumentValueExceptionFault calling PaymentReconciliationService");
            illegalArg.printStackTrace();
        }catch (RemoteException rex) {
        	System.out.println("remote Exception");
        	rex.printStackTrace();
        }
        
        if (reconciledPayments == null){
        	System.out.println("no payment in last 2 months");
        	return;
        }
        System.out.println("# of reconciledPayments=" + reconciledPayments.length);

        
        
        if(reconciledPayments != null && reconciledPayments.length!=0) {
        	
        	// this is a map lookup of payeeIDs and payeeNickNames.
        	Map payeeNameLookupMap = this.getPayeeNicknames(userContext, reconciledPayments);
        	
        	for (int i = 0; i < reconciledPayments.length; i++) {
                ReconciledPayment reconciledPayment = reconciledPayments[i];
                if(reconciledPayment != null) { 
                	System.out.println("  Transaction Id:       " + reconciledPayment.getPaymentTransactionId());
                    
                	// Get paymentAccount so we can display the paymentAccount name
                	PaymentAccountId paymentAccountId = new PaymentAccountId();
                	paymentAccountId.setPaymentAccountId(reconciledPayment.getPaymentAccountId());
                	PaymentAccount paymentAccount =
                    this.getPaymentAccountByIdentifier(userContext, paymentAccountId);

                	if(paymentAccount != null){
                		System.out.println("  Payment Account Name: " + paymentAccount.getNickname());
                		if(paymentAccount instanceof CardPaymentAccount){
                			CardPaymentAccount cpa = (CardPaymentAccount)paymentAccount;
                			System.out.println("  Card Type:            " + (this.getCardType(cpa.getCardType())));
                		}
                    }
	                //System.out.println(reconciledPayment);
	                System.out.println("  Payment Accout Id:    " + reconciledPayment.getPaymentAccountId());
	                System.out.println("  Payee Name:           " + payeeNameLookupMap.get(reconciledPayment.getPayeeId()));
	                System.out.println("  Payee Id:             " + reconciledPayment.getPayeeId());
	                System.out.println("  Payment Date:         " + reconciledPayment.getPaymentDate().getLocalFormat());
	                System.out.println("  Payment Amount:       " + reconciledPayment.getPaymentAmount().getAmount());
	                System.out.println("  Payment Status:       " + reconciledPayment.getPaymentStatus().getPaymentRequestStatusName());
                } 
            }
        }
    }
    
    
    /**
     * Get ItemAccountData for itemAccountId
     * @param is
     * @param itemAccountId
     * @return ItemAccountData
     */
    private ItemAccountData getItemAccountData(ItemSummary is, long itemAccountId){
    	
    	if(is == null){
            System.out.println("is = null, returning null");
            return null;
        }
        ItemData id = null;
        String container = is.getContentServiceInfo().getContainerInfo().getContainerName();
        //System.out.println("getItemAccountData itemAccountId="+itemAccountId + " container=" + container);

        //System.out.println("Get ItemData");
        id = is.getItemData();

        if(id == null){
            System.out.println("ItemData = null, returning null");
            return null;
        }
        //System.out.println("get accounts");
        List accounts = id.getAccounts();
        Object[] objaccounts=accounts.getElements();
        
        if(accounts == null){
            System.out.println("accounts = null or size=0, returning null");
            return null;
        }
        // loan, mortagage, insurance,
       if(container.equalsIgnoreCase("loans") ||
                            container.equalsIgnoreCase("mortgage") ){
            //System.out.println("processing as LOAN || MORTGAGE");
            //System.out.println("iterating through loans accounts");
            for(int i=0; i<objaccounts.length; i++ ){
                LoanLoginAccountData llad = (LoanLoginAccountData)objaccounts[i];
                System.out.println("getting loans");
                List loans = llad.getLoans();
                Object[] objloans = loans.getElements();
                for(int j=0; j<objloans.length; j++){
                    System.out.println("iterating through loans");
                    ItemAccountData iad = (ItemAccountData)objloans[j];
                    System.out.println("checking " + iad.getItemAccountId().longValue() + " = " + itemAccountId);
                    if(iad.getItemAccountId().longValue() == itemAccountId){
                        return iad ;
                    }
                }
            }
        }else if(container.equalsIgnoreCase("insurance") ){
            //System.out.println("processing a INSURANCE");
            //System.out.println("iterating through insurance accounts");
            for(int i=0; i<objaccounts.length; i++ ){
                InsuranceLoginAccountData ilad = (InsuranceLoginAccountData)objaccounts[i];
                //System.out.println("getting insurance policys");
                List insurancePolicys = ilad.getInsurancePolicys();
                Object[] objinsurancePolicys=insurancePolicys.getElements();
                
                              
                for(int j=0; j<objinsurancePolicys.length; j++){
                    //System.out.println("iterating through insurance policys");
                    ItemAccountData iad = (ItemAccountData)objinsurancePolicys[j];
                    System.out.println("checking " + iad.getItemAccountId().longValue() + " = " + itemAccountId);
                    if(iad.getItemAccountId().longValue() == itemAccountId){
                        return iad ;
                    }
                }
            }
        }else{
            //System.out.println("processing a standard container");
            for(int i=0; i<objaccounts.length; i++ ){
                ItemAccountData iad = (ItemAccountData)objaccounts[i];
                if(iad.getItemAccountId().longValue() == itemAccountId){
                    return iad ;
                }
            }
        } 
       return null;
        }

        
    
    
   
    /**
     * Returns federal holidays for a given date range. If the holiday
     * information is not available for all of given date range, the
     * returned holiday info will specify the actual date range for
     * which the holiday information was returned. If startDate is
     * null, the date from which holiday info is available will be used.
     * If endDate is null, the date until which holiday info is available
     * will be used.
     *
     * @param cobrandContext
     * @param startDate
     * @param endDate
     * @return HolidayInfo
     */
     public  HolidayInfo 	getFederalHolidays(CobrandContext cobrandContext,
                                                      java.util.Calendar startDate,
                                                      java.util.Calendar endDate){
        //log.info("getFederalHolidays() startDate=" + startDate + " endDate="+endDate);
    	 HolidayInfo hInfo=null;
    	 try{
    		 hInfo= holidayManager.getFederalHolidays(cobrandContext, startDate, endDate)  ;
    	 }catch(IllegalArgumentValueExceptionFault iavef){
  			System.out.println("Illegal Argument Value");
  		}catch(InvalidConversationCredentialsExceptionFault icce){
 			System.out.println("Conversation credentilas invalid");
 		}catch(StaleConversationCredentialsExceptionFault icce){
 			System.out.println("Conversation credentilas stale");
 		}catch(CoreExceptionFault cef){
 			System.out.println("Core Exception");
 		}catch(RemoteException ref){
 			System.out.println("Remote Exception");
 			ref.printStackTrace();
 		}
    	 return hInfo;
    }

   
     public  PaymentProfile getCardPaymentProfile(PaymentProfile[] paymentProfiles) {
         if(paymentProfiles != null && paymentProfiles.length > 0){
             for(int i=0; i<paymentProfiles.length; i++){
                 PaymentProfile paymentProfile = paymentProfiles[i];
                 if(paymentProfile instanceof  CardPaymentProfile) {
                     return paymentProfile;
                 }
             }
         }
         return null;
     }

     public Payee getPayee(UserContext userContext, long payeeId){
         // Create Interface
         
         Long[] payeeIds = {new Long(payeeId)};
         Payee[] payees = null;
         // Get Payee's
         ArrayOflong payeeIdsArray=new ArrayOflong();
         payeeIdsArray.setElements(payeeIds);
         
         ArrayOfPayee payeeArray= new ArrayOfPayee();
         try{
        	 payeeArray = payeeManagement.getPayees1(userContext, payeeIdsArray);
        	 if (payeeArray!= null){
        		 payees = payeeArray.getElements();
        		 }
         }catch(InvalidUserContextExceptionFault iuce){
  			System.out.println("Invalid User Context");
  		}catch(InvalidConversationCredentialsExceptionFault icce){
 			System.out.println("Conversation credentilas invalid");
 		}catch(StaleConversationCredentialsExceptionFault icce){
 			System.out.println("Conversation credentilas stale");
 		}catch(IllegalArgumentValueExceptionFault iavef){
 			System.out.println("Illegal Argument Value");
 		}catch(CoreExceptionFault cef){
 			System.out.println("Core Exception");
 		}catch(RemoteException ref){
 			System.out.println("Remote Exception");
 			ref.printStackTrace();
 		}
         return payees[0];
     }
     
     /**
      * Get Payment Account given PaymentAccountIdentifier
      * @param context
      * @param pai
      * @return PaymentAccount
      */
     public PaymentAccount getPaymentAccountByIdentifier(UserContext context,PaymentAccountIdentifier pai) {
    	 PaymentAccount[] pas = null;
    	 ArrayOfPaymentAccount paymentAccountArray = null;
    	 ArrayOfPaymentAccountIdentifier paymentAccountIdentifierArray = new ArrayOfPaymentAccountIdentifier();
    	 paymentAccountIdentifierArray.setElements(new PaymentAccountIdentifier [] {pai});
    	 try{
    		 paymentAccountArray = paymentAccountManagement.getPaymentAccounts3(context,paymentAccountIdentifierArray, true);
    		 if (paymentAccountArray!=null){
    			 pas=paymentAccountArray.getElements();
    		 }
    	 }catch(InvalidUserContextExceptionFault iuce){
 			System.out.println("Invalid User Context");
 		}catch(InvalidConversationCredentialsExceptionFault icce){
			System.out.println("Conversation credentilas invalid");
		}catch(StaleConversationCredentialsExceptionFault icce){
			System.out.println("Conversation credentilas stale");
		}catch(IllegalArgumentValueExceptionFault iavef){
			System.out.println("Illegal Argument Value");
		}catch(CoreExceptionFault cef){
			System.out.println("Core Exception");
		}catch(RemoteException ref){
			System.out.println("Remote Exception");
			ref.printStackTrace();
		}
         if (pas == null || pas.length == 0) return null;
         else return pas[0];
     }
     /**
      * Get Card Type
      * @param ct
      * @return card type string
      */
     public  String getCardType(CardType ct){
         String cardType = "";
         if(ct == CardType.VISA){
             cardType="Visa";
         }
         if(ct == CardType.MASTERCARD){
             cardType="MasterCard";
         }
         if(ct == CardType.AMERICAN_EXPRESS){
             cardType="American Express";
         }
         if(ct == CardType.DISCOVER_CARD){
             cardType="Discover Card";
         }
         if(ct == CardType.DINERS_CLUB){
             cardType="Diners Club";
         }
         return cardType;
     }
     
    private Map getPayeeNicknames(UserContext userContext, ReconciledPayment[] reconciledPayments) {
    	// get itemIDs, but don't get duplicates
    	Set itemIDs = new HashSet();
    	for (int i = 0; i < reconciledPayments.length; i++) {
    		itemIDs.add(reconciledPayments[i].getPayeeId());
    	}
    	
    	// convert to long[]
    	Long[] payeeItemIDs = new Long[itemIDs.size()];
    	ArrayOflong payeeItemIDsArray=new ArrayOflong();
    	payeeItemIDsArray.setElements(payeeItemIDs);
    	int j=0;
    	
    	Iterator it = itemIDs.iterator();
    	while (it.hasNext()) {
    		payeeItemIDs[j] = ((Long)it.next());
    		j++;
    	}
    	
    	// get Payee[] array given payeeItemIDs[] array
    	Payee[] payees = null;
    	ArrayOfPayee payeeArray = null;
    	
    	try{
    		payeeArray = payeeManagement.getPayees1(userContext, payeeItemIDsArray);
    		
    		if (payeeArray != null){
    			payees= payeeArray.getElements();
    			}		

    	}catch(InvalidUserContextExceptionFault iuce){
			System.out.println("Invalid User Context");
		}catch(InvalidConversationCredentialsExceptionFault icce){
			System.out.println("Conversation credentilas invalid");
		}catch(IllegalArgumentValueExceptionFault iavef){
			System.out.println("Illegal Argument Value");
		}catch(InvalidPayeeIdExceptionFault ipidf){
			System.out.println("Illegal Argument Value");
		}catch(RemoteException re){
			System.out.println("Remote Exception");
		}catch(Exception e){
			e.printStackTrace();
		}
        
		
		
		
    	Map m = new HashMap();
    	for (int i = 0; i < payees.length; i++) {
    		m.put(payees[i].getPayeeId(), payees[i].getNickname());
    	}
    	return m;
    }

    public boolean viewExpeditedPaymentsEnabledPayees(UserContext userContext){
    	
    	ManagePayee mpayees = new ManagePayee();
    	Payee[] payees = mpayees.getPayItAllPayees(userContext);
    	
    	int expeditedPayeesAvailableCount=0;
        if (payees != null)
        {
        	for(int i=0; i<payees.length; i++){
	            Payee payee = (Payee)payees[i];
	             ArrayOfPaymentProfile paymentProfilesArray = payee.getPaymentProfiles();
	             PaymentProfile[] paymentProfiles =  null;
	             if (paymentProfilesArray != null){
	            	 paymentProfiles= paymentProfilesArray.getElements();
	            	 }		

	            if (paymentProfiles != null && paymentProfiles.length > 0)
                {
                    for (int j = 0; j < paymentProfiles.length; j++) 
                    {
                        
                    	
                    	PaymentProfile paymProfile = (PaymentProfile)paymentProfiles[j];
                        if (paymProfile instanceof BankPaymentProfile){
                        	BankPaymentProfile bankPaymProfile = (BankPaymentProfile)paymProfile;
                        	if(bankPaymProfile.isExpeditedPaymentsEnabled()==true){
                       		expeditedPayeesAvailableCount++;
                        		System.out.println("\n"+(i+1)+")"+"\n"+"Payee Details-->" + "\n\n"
                	                    + " NickName: "+ payee.getNickname()+ "\n"
                	                    + " csId: " + payee.getContentServiceId()+ "\n"
                	                    + " desc: " + payee.getDescription()+ "\n" 
                	                    + " PayeeId: " + payee.getPayeeId()+"\n");
                        		
                        	}
                        }
                    }  
                }
        	}
        }
        if (expeditedPayeesAvailableCount==0)
        return expeditedPayeesAvailable;
        else{
        	expeditedPayeesAvailable=true;
        	System.out.println("expeditedPayeesAvailableCount"+expeditedPayeesAvailableCount);
        	return expeditedPayeesAvailable;
        }
    }
	            
       
    
   
    //This function gets  the next possible expedited payment date for the user.
    private Calendar getNextPossiblePaymentCal(UserContext userContext,long payeeId) {
    	Calendar nextPossiblePaymentDate = null;
		try{
    	nextPossiblePaymentDate = paymentService
				.getNextPossibleExpeditedPaymentDate(userContext,new Long( payeeId));
    	}catch(InvalidUserContextExceptionFault iuce){
			System.out.println("Invalid User Context");
		}catch(ExpeditedPaymentNotAllowedExceptionFault epnae){
			System.out.println("Expedited Payment Not Allowed ExceptionFault");
		}catch(InvalidConversationCredentialsExceptionFault icce){
			System.out.println("Conversation credentilas invalid");
		}catch(InvalidPayeeIdExceptionFault ipie){
			System.out.println("Invalid Payee Id");
		}catch(Exception e){
			System.out.println("ExceptionFault occured" +e.getMessage());
		}
	//	Calendar nextPossiblePayCal = Calendar.getInstance(TimeZone
				//.getTimeZone(userTimeZone));
		//nextPossiblePayCal.setTime(nextPossiblePaymentDate);
		//int nextPossiblePayDay = nextPossiblePaymentDate.get(Calendar.DAY_OF_MONTH);
		return nextPossiblePaymentDate;
	}

   // Get the payment start date for the user.If the user given start date is before 
    //the current date it sets the current date as the start date else retuns the user  given start date only.
    private Calendar createStartCal(Calendar nextPossiblePayCal, int offset) {
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone(userTimeZone));
		cal.set(nextPossiblePayCal.get(Calendar.YEAR), nextPossiblePayCal
				.get(Calendar.MONTH), nextPossiblePayCal
				.get(Calendar.DAY_OF_MONTH), 0, 0);

		// cal.setTimeInMillis(nextPossiblePayCal.getTimeInMillis());
		cal.add(Calendar.DAY_OF_MONTH, offset);
		if (offset == 0) {
			Calendar currentCal = Calendar.getInstance(TimeZone
					.getTimeZone(userTimeZone));
			if (cal.before(currentCal))
				return currentCal;
		}
		return cal;
	}

	//This function retuns the end date for the payment
    private Calendar createEndCal(Calendar userSelectedStartCal) {
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone(userTimeZone));
		cal.set(userSelectedStartCal.get(Calendar.YEAR), userSelectedStartCal
				.get(Calendar.MONTH), userSelectedStartCal
				.get(Calendar.DAY_OF_MONTH), 23, 59);
		return cal;
	}

   
    public void createPayTodayPayment(UserContext userContext)
    {
    	System.out.println("Fetching Payees....");
    	// Get all expedited enables payees for the user
        boolean expeditedPayeesAdded=viewExpeditedPaymentsEnabledPayees(userContext);
        
        //If expedited payees exist for the user
        
        if(expeditedPayeesAdded==true){ 
        	
        //Ask the user to enter the payee id for which he wants to crete the pay today payment.
        
        System.out.println("Enter PayeeId:");
        long payeeId = IOUtils.readLong();
        
        //Get all the bank payment accounts.
       
        System.out.println("Fetching Bank Payment Accounts....");
        BillPayPaymentAccountManagement payit=new BillPayPaymentAccountManagement();
        payit.viewPayItAllPaymentAccountsDetails(userContext,BillPayConstants.ACTIVE_PAYM_ACCTS);
      
        //Take user input for the preferred payment acct.
        
        System.out.println("Enter PaymentAccountId:");
        int paymentAccountId = IOUtils.readInt();
             
        ExpeditedPaymentRequest expeditedPaymRequest = new ExpeditedPaymentRequest();            
        
        // Create the expedited payment object
        // Get the expedited fee ACL
        String expeditedFee=null;
        try{
        	expeditedFee = aclService.getAclValue(userContext, "BILLPAY_FEES_EXPEDITED");
        }catch(InvalidUserContextExceptionFault iccefe){
			System.out.println("Invalid user Context ");
		}catch(InvalidConversationCredentialsExceptionFault iccefe){
			System.out.println("Invalid Conversation Credentials");
		}catch(StaleConversationCredentialsExceptionFault iccefe){
			System.out.println("Stale Conversation Credentials");
		}catch(IllegalArgumentValueExceptionFault iavef){
			System.out.println("Illegal Argument Value");
		}catch(RemoteException re){
			System.out.println("Remote Exception");
		}
        if (expeditedFee == null)
        {
            expeditedFee = "0.0";
        }
        BigDecimal fee = new BigDecimal(expeditedFee);
        Money payTodayFee = new Money();
        payTodayFee.setAmount(fee);
        payTodayFee.setCurrencyCode("USD");
         
        //Create the Expedited Payment Request Object
        expeditedPaymRequest.setExpeditedPaymentFee((Money)payTodayFee);
        expeditedPaymRequest.setPayeeId(new Long(payeeId));
        PaymentAccountId paymentAccountIdObj = new PaymentAccountId();
        paymentAccountIdObj.setPaymentAccountId(new Long(paymentAccountId));
        expeditedPaymRequest.setPaymentAccountIdentifier(paymentAccountIdObj);
 
       
        System.out.println("Enter Payment Amount:");
        String paymentAmount = IOUtils.readStr();
        Money money = new Money();
        money.setAmount(new BigDecimal(paymentAmount));
        money.setCurrencyCode("USD");
 
        expeditedPaymRequest.setPaymentAmount(money);
       
        expeditedPaymRequest.setPaymentMemo("PaymentTest");
        expeditedPaymRequest.setPostOnNextBusinessDate(false);
        expeditedPaymRequest.setFrequency(com.yodlee.soap.core.paymentservice.Frequency.ONE_TIME);
        expeditedPaymRequest.setDateType(DateType.RISK_DUE_DATE);
     
        //Get the payment start date and end date.We get the next possible payment date if it's
        //a future payment else it returns the today's date only.
        Calendar nextPossiblePayCal = getNextPossiblePaymentCal(userContext,payeeId);
	
     	Calendar userSelectedStartCal = createStartCal(nextPossiblePayCal,
				0);
     	//Setting the end date
		Calendar userSelectedEndCal = createEndCal(userSelectedStartCal);
    	
		
		
		Date nextPossiblePaymentDate = nextPossiblePayCal.getTime();
		Date userSelectedStartDate = userSelectedStartCal.getTime();
		Date userSelectedEndDate = userSelectedEndCal.getTime();
		
		System.out.println("nextPossiblePaymentDate"+nextPossiblePaymentDate);
		System.out.println("userSelectedStartDate" + userSelectedStartDate);
		System.out.println("userSelectedEndDate" + userSelectedEndDate);
	
		
		expeditedPaymRequest.setStartDate(userSelectedStartCal);
		expeditedPaymRequest.setEndDate(userSelectedEndCal);
		
		//Create the Payment request
		
		com.yodlee.soap.core.paymentservice.PaymentRequest paymentRequest = expeditedPaymRequest;
        try
        {
        	//Create payment
            Long paymentRequestId = paymentService.createPayment(userContext, paymentRequest);
            System.out.println("*****Payment Request Id : " + paymentRequestId +"\n\n");
            //Schedule Payment
            String paymentTxnId = paymentService.schedulePayment1(userContext,paymentRequestId);
            System.out.println("***** Payment Transaction Id : " + paymentTxnId +"\n\n");
        }catch(InvalidConversationCredentialsExceptionFault icce){
        	System.out.println("Invalid Conversation Credentials");
        }catch(InvalidUserContextExceptionFault iuce){
        	System.out.println("Invalid User Context");
        }catch(ItemAccountDisabledExceptionFault iade){
        	System.out.println("Item Account is disabled");
        }catch(InvalidPaymentAmountExceptionFault ipae){
        	System.out.println("Payment amount is invalid");
        }catch(PayeeCurrentlyNotSupportedForEBillsExceptionFault ipae){
        	System.out.println("Payee not supported for ebills");
        }catch(EBillPaymentNotSupportedExceptionFault ebnse){
        	System.out.println("EBill payment not supported for the payee");
        }catch(InvalidPaymentAccountIdExceptionFault ipae){
        	System.out.println("PaymentAccount Invalid");
        }catch(InvalidDateTypeExceptionFault idte){
        	System.out.println("PaymentAccount Invalid");
        }catch(PaymentNotSupportedExceptionFault pnse){
        	System.out.println("PaymentNot supported Invalid");
        }catch(InvalidPaymentDateExceptionFault ipde){
        	System.out.println("Payment Date Invalid");
        }catch(PayeeStatusNotValidExceptionFault psie){
        	System.out.println("Payee Status  Invalid");
        }catch(InvalidPayeeIdExceptionFault ipe){
        	System.out.println("Payee Status  Invalid");
        }catch(ExpeditedPaymentNotAllowedExceptionFault epnaee){
        	System.out.println("ExpeditedPaymentNotAllowed" + epnaee.getMessage());
        }catch(Exception e){
        	System.out.println("ExceptionFault occured" + e.getMessage());
        }
        }
        else{ 
        	System.out.println("No Payee with Expedited Payment Enabled");
        }
        
       }
       
public void viewPayTodayPayment(UserContext userContext){
	ArrayOfPaymentMode paymentModeArray = new ArrayOfPaymentMode();
	paymentModeArray.setElements(new PaymentMode[]{PaymentMode.EXPEDITED_PAYMENT});
	//Filter payments by payment mode for the user
	PaymentFilterByPaymentMode paymfilter = new PaymentFilterByPaymentMode();
	paymfilter.setPaymentMode(paymentModeArray);

	
	PaymentFilter payfilter = new PaymentFilter();
	ArrayOfPayment paymentArray = null;
	Payment[] payments1 =null;
	Payment[] payments =null;
	payfilter.setByPaymentMode(paymfilter);
    try{
    	paymentArray =paymentService.getPayments3 (userContext,payfilter);
    	if (paymentArray != null){
    		payments = paymentArray.getElements();
    		}
    
      System.out.println ("Payment length" + payments.length+"payments1.length"+payments1.length+"paymFilter"+paymfilter);
    }catch(InvalidUserContextExceptionFault iuce){
    	System.out.println("Invalid User Context");
    }catch(InvalidConversationCredentialsExceptionFault icce){
    	System.out.println("Invalid Conversation Credentials");
    }catch(IllegalArgumentValueExceptionFault iave){
    	System.out.println("Illegal argument value");
    }catch(Exception e){
    	System.out.println("ExceptionFault Occureed" +e.getMessage());
    }
    if (payments.length>0){
	for (int i = 0; i < payments.length; i++) {
        Payment pmt = payments[i];
        if(pmt != null)
        	System.out.println ((i+1)+")" +"\n\n"+"Payment Details for Payee: " 
        			+ pmt.getPaymentRequest().getPayeeId()+"\n"
        			+ " Payment Status: " +pmt.getPaymentRequest().getScheduledPaymentRequestStatus() + "\n"
        			+ " Payment Transaction Id : "+pmt.getPaymentTransactionId() + "\n"
        			+ " Payment Request Date: "+ pmt.getPaymentRequestDate() + "\n"
        			+ " Payment Amount :" + pmt.getPaymentRequest().getPaymentAmount());    	 
    }
  }
    if (payments1.length>0){
    	for (int i = 0; i < payments1.length; i++) {
            Payment pmt1 = payments1[i];
            if(pmt1 != null)
            	System.out.println ((i+1)+")" +"\n\n"+"Getting all Payment Details for Payee: " 
            			+ pmt1.getPaymentRequest().getPayeeId()+"\n"
            			+ " Payment Status: " +pmt1.getPaymentRequest().getScheduledPaymentRequestStatus() + "\n"
            			+ " Payment Transaction Id : "+pmt1.getPaymentTransactionId() + "\n"
            			+ " Payment Request Date: "+ pmt1.getPaymentRequestDate() + "\n"
            			+ " Payment Amount :" + pmt1.getPaymentRequest().getPaymentAmount());    	 
        }
      }
    

}
    
    
    
    	     
    
 
    public static void main (String args[])
    {
        if (args.length < 2) {
            throw new RuntimeException("Usage: <username> <password>") ;
        }

        // Startup and initialize
        /*
        try {
            InitializationHelper.setup ();
        } catch (ExceptionFaultFault startupEx) {
            System.out.println ("Unable to startup system: " + startupEx);
            System.exit (-1);
        }
        */
        // Not implemented, use BillPayApp instead.
    }
    }
