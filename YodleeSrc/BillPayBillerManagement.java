
package YodleeSrc;

import com.yodlee.soap.collections.common.ArrayOfContentServiceInfo;
import com.yodlee.soap.collections.core.billermanagement.ArrayOfService;
import com.yodlee.soap.collections.core.billermanagement.ArrayOfUSLByCategory;
import com.yodlee.soap.collections.core.billermanagement.ArrayOfUslStatus;
import com.yodlee.soap.common.ContentServiceInfo;
import com.yodlee.soap.common.UserContext;
import com.yodlee.soap.core.IllegalArgumentTypeExceptionFault;
import com.yodlee.soap.core.IllegalArgumentValueExceptionFault;
import com.yodlee.soap.core.InvalidCobrandContextExceptionFault;

import com.yodlee.soap.core.billermanagement.billermanagement.BillerManagement;
import com.yodlee.soap.core.billermanagement.billermanagement.BillerManagementService;
import com.yodlee.soap.core.billermanagement.billermanagement.BillerManagementServiceLocator;

import com.yodlee.soap.core.billermanagement.Service;
import com.yodlee.soap.core.billermanagement.USLByCategory;
import com.yodlee.soap.core.billermanagement.UslStatus;
import com.yodlee.soap.core.billermanagement.UslStatusFilterCriteria;
import com.yodlee.soap.core.paymentservice.paymentaccountmanagement.PaymentAccountManagementServiceLocator;



public class BillPayBillerManagement extends ApplicationSuper
{
	protected BillerManagement billerManagement;

	private final String HEADING_CAT_ID = "CATEGORY_ID";
	private final String HEADING_CAT_NAME = "CATEGORY_NAME";	
	private static final int SPACE_AFTER_COLUMN=3;
	private static final int CAT_ID_SIZE=11;
	
	public BillPayBillerManagement(){
		super ();
		
		//Create BillerManagementServiceLocator
		BillerManagementServiceLocator  billermanagementServiceLocator= new BillerManagementServiceLocator();
       String billermanagementServiceName= billermanagementServiceLocator.getBillerManagementServiceWSDDServiceName();
       billermanagementServiceLocator.setBillerManagementServiceEndpointAddress(System.getProperty("com.yodlee.soap.services.url")
				+ "/" + billermanagementServiceName);


	}
	
	
	
	public void getContentServiceInfosForService(UserContext userCtx){       
		ContentServiceInfo[] csInfo = null;
		ArrayOfContentServiceInfo csInfoArray = null;
		try{
			System.out.print("Enter the Service Id: ");
			long serviceId = IOUtils.readLong();
			csInfoArray = billerManagement.getContentServiceInfosForService(getCobrandContext(),new Long(serviceId));            
			if (csInfoArray != null){
				csInfo = csInfoArray.getElements();
			}
			if (csInfo !=null && csInfo.length >0){
				for (int i=0;i<csInfo.length;i++){
					if (csInfo[i] instanceof ContentServiceInfo)
					System.out.println("Content Service Id      : "+csInfo[i].getContentServiceId());
					System.out.println("Content Service Name    : "+csInfo[i].getContentServiceDisplayName());
					System.out.println("Site Display Name       : "+csInfo[i].getSiteDisplayName());
					System.out.println(" Is DirectPay Payment supported : "+csInfo[i].isDirectCardPaymentSupported());
					System.out.println("Login URL               : "+csInfo[i].getLoginUrl());
				}
			}else{
				System.out.println("There is no ContentServieinfo for this service id");
				return;
			}
		}catch(InvalidCobrandContextExceptionFault icexp){
			System.out.println("The Cobrand context is invalid");	
		}catch(IllegalArgumentValueExceptionFault ilavexp){
			System.out.println("The Service Identifier is invalid");
		}catch(Exception e){
			System.out.println("Invalid service id");
			e.printStackTrace();
		}
	}            
	
	public void getBillerServices(UserContext userCtx){
		
		Service[] service = null;
		ArrayOfService serviceArray = null;
		try{           
			UslStatusFilterCriteria uslFilterCri = null;
			//uslFilterCri.setFetchAllServices(true);
			
			System.out.print("Enter Biller Name : ");
			String keyword = IOUtils.readStr();
			
			if (keyword == null || keyword.length() == 0){               
				serviceArray = billerManagement.getServices1(getCobrandContext(), uslFilterCri);
			}else{
				serviceArray = billerManagement.getServices(getCobrandContext(), keyword, uslFilterCri);
			}
			
			if (serviceArray != null){
				service = serviceArray.getElements();
			}
			if(service == null || service.length == 0){
				System.out.println("No services are found for the biller.");
			} else{
				for (int i=0;i< service.length ;i++) {
				
					if (UslStatus.ACTIVE_STATUS.equals(service[i].getStatus())){
					System.out.println("\nService Id       : " + service[i].getServiceId());
					System.out.println("Service Name       : " + service[i].getServiceName());
					System.out.println("Status             : " + service[i].getStatus());
					System.out.println("EBill Support      : " + service[i].isIsEbillSupported());
					System.out.println("Category           : " + service[i].getCategory());
					System.out.println("Paytoday Support    : " + service[i].isIsExpeditedSupported());
				 }
				}
			}
		}catch(InvalidCobrandContextExceptionFault e){
			System.out.println("The cobrand context is invalid");			
		}catch(IllegalArgumentException e){
			System.out.print("Either the status passed is invalid or categories passed are null or not valid");
		}catch(Exception e){
			e.printStackTrace();
		}		
	}
	

	
	/*
	 *Gets all expedited enabled services
	 * @param UserContext
	 */
	public void viewExpeditedEnabledServices(UserContext userContext){
		
		
		//Following code will fetch the biller information
		UslStatusFilterCriteria uslStatusFilterCriteria = new UslStatusFilterCriteria();
		uslStatusFilterCriteria.setFetchAllServices(true);
		
		Service[] services =null;
		ArrayOfService serviceArray = null;
		//Get all services
		try{
			serviceArray = billerManagement.getServices1(getCobrandContext(),uslStatusFilterCriteria);
			if (serviceArray != null){
				services = serviceArray.getElements();
			}
		}catch (Exception e)
        {
			System.out.println("Exception:" + e.getMessage());
        }
		if(services==null || services.length==0){
			System.out.println("No services found for given keyword");		
			return;
		}		
		//Print only those services which are expedited Payment enabled
		for(int i=0;i<services.length;i++){
			if (services[i].isIsExpeditedSupported()==true){
				System.out.println((i+1)+". ServiceName- "+services[i].getServiceName()+"; " +
				"ServiceId- "+services[i].getServiceId()+"Is Expedited Supported" + services[i].isIsExpeditedSupported());		
		

	}
	}
	}

	
}

