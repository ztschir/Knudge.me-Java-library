/*
 * Copyright 2009 Yodlee, Inc.  All Rights Reserved.  Your use of this code 
 * requires a license from Yodlee.  Any such license to this code is 
 * restricted to evaluation/illustrative purposes only. It is not intended 
 * for use in a production environment, and Yodlee disclaims all warranties 
 * and/or support obligations concerning this code, regardless of the terms 
 * of any other agreements between Yodlee and you."
 */
package YodleeSrc;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import javax.xml.rpc.ServiceException;

import com.yodlee.soap.collections.ArrayOfString;
import com.yodlee.soap.collections.common.ArrayOfContentServiceInfo;
import com.yodlee.soap.common.ContentServiceInfo;
import com.yodlee.soap.core.ContentServiceNotFoundExceptionFault;
import com.yodlee.soap.core.dataservice.ContainerNotFoundExceptionFault;
import com.yodlee.soap.core.verification.IAVDataRequestNotSupportedExceptionFault;
import com.yodlee.soap.ext.traversal.contentservicetraversal.ContentServiceTraversal;
import com.yodlee.soap.ext.traversal.contentservicetraversal.ContentServiceTraversalServiceLocator;
import com.yodlee.soap.core.accountmanagement.itemmanagement.ItemManagement;
import com.yodlee.soap.core.accountmanagement.itemmanagement.ItemManagementServiceLocator;
import com.yodlee.soap.common.Form;

/**
 * This helper class can be
 * used as a helper class to get the different content services
 */
public class ContentServiceHelper extends ApplicationSuper {

	protected ContentServiceTraversal cst;
	protected ItemManagement itemManagement;
	
    private static int OPTION_CNT = 0;
    private static int NAV_QUIT = OPTION_CNT++;
    private static int NAV_SEARCH_CONTENT_SERVICES = OPTION_CNT++;
    private static int NAV_VIEW_SINGLE_SERVICE_DETAILS = OPTION_CNT++;
    private static int NAV_VIEW_ALL_CONTENT_SERVICES = OPTION_CNT++;
    private static int NAV_DUMP_ALL_CONTENT_SERVICES = OPTION_CNT++;
    private static int NAV_VIEW_CONTAINER_SERVICES = OPTION_CNT++;
   private static int NAV_VIEW_FAVICON = OPTION_CNT++;

    /**
     * The auto-login type when auto-login occurs through a plain
     * (simple) <i>Http POST</i> operation of the credentials as part of
     * the login form.
     */
    protected static final int SIMPLE = 1;

    /**
     * The auto-login type when auto-login occurs through a proxy
     * web-server which has already obtained the login form. The proxy
     * web server consequently does an <i>Http POST</i> with the
     * credentials as part of the login form.
     */
    protected static final int PROXY = 2;

    /**
     * The auto-login type when auto-login is not-supported on the server side.
     */
    protected static final int NOT_SUPPORTED = 5;

    /**
     * The auto-login type when the client side component is required to support
     * auto-login.
     */
    protected static final int CLIENT_ENABLED = 3;

    /**
     * The auto-login type when auto-login occurs through <i>Http
     * Authentication</i>. In this case, there is no login form.
     */
    protected static final int HTTP = 4;

    public ContentServiceHelper() {
    	super();
    	
    	ContentServiceTraversalServiceLocator locator = new ContentServiceTraversalServiceLocator();
		String serviceName = locator
				.getContentServiceTraversalServiceWSDDServiceName();
		locator
				.setContentServiceTraversalServiceEndpointAddress(System.getProperty("com.yodlee.soap.services.url")
						+ "/" + serviceName);

    	ItemManagementServiceLocator itemLocator = new ItemManagementServiceLocator();
        String itemServiceName = itemLocator.getItemManagementServiceWSDDServiceName();
        itemLocator.setItemManagementServiceEndpointAddress(System.getProperty("com.yodlee.soap.services.url")
						+ "/" + itemServiceName);

		try {
			cst = locator.getContentServiceTraversalService();
        	itemManagement = itemLocator.getItemManagementService();

		} catch (ServiceException se) {
			throw new RuntimeException(se);
		}
    }

    public void contentServiceMenu() {
    	boolean loop = true;
    	
    	while(loop) {
    		System.out.println("Choose one of the alert management options");
    		System.out.println("********************");
    		System.out.println(NAV_SEARCH_CONTENT_SERVICES + ". Search content services");
    		System.out.println(NAV_VIEW_SINGLE_SERVICE_DETAILS + ". View Single content servce");
    		System.out.println(NAV_VIEW_ALL_CONTENT_SERVICES + ". View all content services (slow)");
    		System.out.println(NAV_DUMP_ALL_CONTENT_SERVICES + ". Dump all forms to files (slow, heapsize >= 1024m)");
    		System.out.println(NAV_VIEW_CONTAINER_SERVICES + ". View services for a specific container");
            System.out.println(NAV_VIEW_FAVICON + ". View favicons for a specific container");

    		System.out.println(NAV_QUIT + ". Exit Sub-menu");
    		System.out.println("********************");

    		System.out.print("Choice: ");
    		int choice = IOUtils.readInt();
    		System.out.println();
	        try{
	        	if (choice == NAV_SEARCH_CONTENT_SERVICES) {
	        		searchContentServices();
	        	} else if (choice == NAV_VIEW_SINGLE_SERVICE_DETAILS) {
	                viewSingleServiceDetails();
	            } else if (choice == NAV_VIEW_ALL_CONTENT_SERVICES) {
	                viewAllContentServices();
	            } else if (choice == NAV_DUMP_ALL_CONTENT_SERVICES) {
	                dumpAllContentServices();
	            } else if (choice == NAV_VIEW_CONTAINER_SERVICES) {
	                viewContainerServices();
                } else if (choice == NAV_VIEW_FAVICON) {
                    viewFavIconForContentService();
	            } else if (choice == NAV_QUIT) {
	            	loop = false;
	            }
	        } catch (Exception e) {
	            System.out.println("Exception : " + e);
	            e.printStackTrace();
	        }
    	}
    }
    
    /**
     * Search Content Services.
     */
   public void searchContentServices() {
        System.out.print("Enter Search String: ");

        String searchString = IOUtils.readStr();
        String[] keywords = new String[1];
        keywords[0] = searchString;

        SearchSample searchSample = new SearchSample();
        searchSample.doSearch(keywords);
    }

    /**
     * View the details of a single content service
     */
    public void viewSingleServiceDetails() {
    	 System.out.print("Enter in the content service ID to view details >> : ");
         long choice = IOUtils.readLong();

         // passing a "1" as the third arg gets keywords
         try {
		  	ContentServiceInfo csi = cst.getContentServiceInfo(getCobrandContext(), choice);
		  	printContentServiceInfo(csi);
         } catch (ContentServiceNotFoundExceptionFault e) {
         	System.out.println("Content Service id: " + choice);
         	System.out.println("Method [getContentServiceInfo] threw a ContentServiceNotFoundException exception.");
         	System.out.println("This means that the content service does not exist or is not enabled for this ");
         	System.out.println("  cobrand.  If this content service should be enabled for the cobrand contact ");
         	System.out.println("  Yodlee Customer Care or Yodlee Professional Services.\n\n");
         } catch (Exception e) {
         	e.printStackTrace();
         }
    }
    
    /**
     * View FavIcon/Image of a single content service
     */
    public void viewFavIconForContentService() {
        System.out.print("Enter in the content service ID to view details >> : ");
        long choice = IOUtils.readLong();

        try {
        	int loginFormRequired = 16;
        	int loginFormLastModifiedDateRequired = 2;
        	int keyWordsRequired = 1; 
        	int iconAndFaviconImagesRequired = 128; 
        	
        	// 128 flag is for fav icon
            int flags = 16 | 2 | 1 | 128;
              
        	ContentServiceInfo csi = cst.getContentServiceInfo1(getCobrandContext(),choice, flags);
        	if(csi.getFaviconImageContent() != null) {
            	File file = new File("FavIcon_" + csi.getContentServiceId() + ".jpg");
	        	BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file));        	        
	            out.write(csi.getFaviconImageContent().getElements());
	            System.out.println("Favicon image written to " + file.getAbsolutePath());
	            out.close();
            } else {
            	System.out.println("No FavIcon found for this content service");
            }
        	if(csi.getIconImageContent() != null) {
            	File file = new File("IconImage_" + csi.getContentServiceId() + ".jpg");
	        	BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file));        	        
	            out.write(csi.getIconImageContent().getElements());
	            System.out.println("Icon Image written to " + file.getAbsolutePath());
	            out.close();
            } else {
            	System.out.println("No IconImage found for this content service");
            }
            
        	//printContentServiceInfo(csi);
        } catch (ContentServiceNotFoundExceptionFault e) {
        	System.out.println("Content Service id: " + choice);
        	System.out.println("Method [getContentServiceInfo] threw a ContentServiceNotFoundException exception.");
        	System.out.println("This means that the content service does not exist or is not enabled for this ");
        	System.out.println("  cobrand.  If this content service should be enabled for the cobrand contact ");
        	System.out.println("  Yodlee Customer Care or Yodlee Professional Services.\n\n");
        } catch (Exception e) {
        	e.printStackTrace();
        }
    }

    /**
     * This method prints a subset of the ContentServiceInfo fields
     * @param csi Content Service info object to show info for
     */
    public void printContentServiceInfo(ContentServiceInfo csi) {
        System.out.println("Content Service id: " + csi.getContentServiceId());
        System.out.println("Display name: " + csi.getContentServiceDisplayName());
        System.out.println("Container type: " + csi.getContainerInfo().getContainerName());
        System.out.println("Registration URL: " + csi.getRegistrationUrl());
        System.out.println("Home URL: " + csi.getHomeUrl());
        System.out.println("Login URL: " + csi.getLoginUrl());
        System.out.println("MFA Type: " + ((csi.getMfaType()==null)?"null":csi.getMfaType().getValue()));
        String autoLoginType = "UNKNOWN";
        switch(csi.getAutoLoginType()) {
			case CLIENT_ENABLED: autoLoginType = "CLIENT_ENABLED"; break;
			case HTTP: autoLoginType = "HTTP"; break;
			case NOT_SUPPORTED: autoLoginType = "NOT_SUPPORTED "; break;
			case PROXY : autoLoginType = "PROXY"; break;
			case SIMPLE: autoLoginType = "SIMPLE"; break;
		}
        System.out.println("AutoLogin Type: " + autoLoginType);

        ArrayOfString keywordsArray = csi.getKeywords();
        String[] keywords = null;
        if (keywordsArray != null){
        	keywords = keywordsArray.getElements();
        }
        if (keywords != null) {
	        System.out.print("Search keywords: " );
	        for (int i = 0; i < keywords.length; i++) {
	            String keyword = keywords[i];
	            System.out.print(keyword + ",");
	        }
        }
        System.out.println("");
        
        if(csi.isHasSiblingContentServices()) {
        	
        	ArrayOfContentServiceInfo siblingCSIDList = null;
        	ContentServiceInfo[] siblingCSIDs = null;
        	try {
        		siblingCSIDList = cst.getSiblingContentServices(getCobrandContext(), csi.getContentServiceId());
        	  if (siblingCSIDList != null){
        		  siblingCSIDs = siblingCSIDList.getElements();
        	  }
        	} catch (Exception e) {}
        	if (siblingCSIDs == null || siblingCSIDs.length == 0) {
        		System.out.println("No sibling sites available");
        	}
        	else {        	
	        	System.out.println(siblingCSIDs.length + " sibling sites available");        	
	        	for(int i=0; i< siblingCSIDs.length; i++) {
	        		System.out.println("  Sibling: " 
	        				+ siblingCSIDs[i].getContentServiceDisplayName()
	        				+ " ("
	        				+ siblingCSIDs[i].getContentServiceId()
	        				+ ")");
	        	}
        	}
        } else {
        	System.out.println("No sibling sites available");
        }
        
        ContentServiceInfo[] sharedCSIDs = null;
        try {
        	cst.getContentServicesBySite(getCobrandContext(), csi.getSiteId());
        } catch (Exception e) {}        
        if(sharedCSIDs != null && sharedCSIDs.length > 1) {
        	System.out.println((sharedCSIDs.length-1) + " shared sites available");        	
        	for(int i=0; i< sharedCSIDs.length; i++) {
        		if(csi.getContentServiceId() != sharedCSIDs[i].getContentServiceId()) {
	        		System.out.println("  Shared: " 
	        				+ sharedCSIDs[i].getContentServiceDisplayName()
	        				+ " ("
	        				+ sharedCSIDs[i].getContentServiceId()
	        				+ ")");
        		}
        	}
        } else {
        	System.out.println("No shared sites available");
        }
        
        System.out.println("\n\n");

    }

    /**
     * This method will give a summary of how many services there are per container and
     * give the user the option to print them all out
     */
    public void viewAllContentServices() {
    	com.yodlee.soap.collections.Map serviceEntryMap = null;
    	com.yodlee.soap.collections.Entry[] serviceMap = null;
        try {
        	serviceEntryMap = cst.getContentServicesByContainerType(getCobrandContext());
        	if (serviceEntryMap !=  null){
        		serviceMap = serviceEntryMap.getTable();
        	}
        } catch (Exception e) {}
        com.yodlee.soap.collections.Entry entry = null;
        Object container = null;
        ContentServiceInfo[] csis = null;
        for (int i = 0; i < serviceMap.length; i++) {
        	entry = serviceMap[i];
        	container = entry.getKey();
        	csis = (ContentServiceInfo[])entry.getValue();
        	System.out.println("[" + container + "] - " + csis.length);        	
        }
        System.out.print("Do you want a dump of all the services? [y/n]:");
        String choice = IOUtils.readStr();
        if (choice.equals("y") || choice.equals("Y")) {
        	for (int i = 0; i < serviceMap.length; i++) {
            	entry = serviceMap[i];
            	container = entry.getKey();
            	csis = (ContentServiceInfo[])entry.getValue();
            	printBriefContentServices(csis);       	
            }
        }
    }
    
    public void dumpAllContentServices() throws Exception {
    	System.out.println("Getting all content services.");
    	ArrayOfContentServiceInfo csisList = cst.getAllContentServices1(getCobrandContext(), 0);
    	if(csisList != null) {
	    	ContentServiceInfo[] csis = csisList.getElements();
	    	System.out.println("Got a total of [" + csis.length + "] content services.");
	    	for(int i=0; i<csis.length; i++) {
    		    System.out.print("Dumping " + i + " of " + csis.length + " - CSID [" + csis[i].getContentServiceId() +"] ...");
      	        dumpContentService(csis[i].getContentServiceId());
	    	}
	    } else {
	        System.out.println("Got back zero (0) content services");
	    }
    }
    
    public void dumpContentService(long contentServiceId) {
    	try {
	    	Form form = itemManagement.getLoginFormForContentService(getCobrandContext(), contentServiceId);
	    	FormUtil.writeFormHtml(getCobrandContext(), contentServiceId, form, true, "dumpForms");
    		System.out.println("Done.");
        } catch (Exception e) {
            System.out.println("Failed.");
        }
    }

    public void viewContainerServices() {
        System.out.println("The following are the containers to get the sites for");

        System.out.println(ContainerTypesHelper.AIR_RESERVATION + ", " +
        		ContainerTypesHelper.AUCTION + ", " +
        		ContainerTypesHelper.BANK + ", " +
                ContainerTypesHelper.BILL + ", " +
                ContainerTypesHelper.BILL_PAY_SERVICE+ ", " +
                ContainerTypesHelper.CABLE_SATELLITE+ ", " +
                ContainerTypesHelper.CALENDAR + ", " +
                ContainerTypesHelper.CAR_RESERVATION + ", " +
                ContainerTypesHelper.CHARTS+ ", " +
                ContainerTypesHelper.CHAT + ", " +
                ContainerTypesHelper.CONSUMER_GUIDE + ", " +
                ContainerTypesHelper.CREDIT_CARD+ ", " +
                ContainerTypesHelper.DEAL+ ", " +
                ContainerTypesHelper.HOTEL_RESERVATION + ", " +
                ContainerTypesHelper.INSURANCE+ ", " +
                ContainerTypesHelper.INVESTMENT+ ", " +
                ContainerTypesHelper.JOB+ ", " +
                ContainerTypesHelper.LOAN + ", " +
                ContainerTypesHelper.MAIL + ", " +
                ContainerTypesHelper.MESSAGE_BOARD+ ", " +
                ContainerTypesHelper.MINUTES+ ", " +
                ContainerTypesHelper.MISCELLANEOUS + ", " +
                ContainerTypesHelper.MORTGAGE + ", " +
                ContainerTypesHelper.NEWS+ ", " +
                ContainerTypesHelper.ORDER+ ", " +
                ContainerTypesHelper.OTHER_ASSETS+ ", " +
                ContainerTypesHelper.OTHER_LIABILITIES+ ", " +
                ContainerTypesHelper.RESERVATION+ ", " +
                ContainerTypesHelper.REWARD_PROGRAM+ ", " +
                ContainerTypesHelper.TELEPHONE+ ", " +
                ContainerTypesHelper.UTILITIES);

        System.out.print("Type the name of the container you want the services for >> ");
        String choice = IOUtils.readStr();

        if (ContainerTypesHelper.isValid(choice)){
            ContentServiceInfo[] csis = null;
            ArrayOfContentServiceInfo csisList = null;
			try {
				csisList = cst.getContentServicesByContainerType2(getCobrandContext(), choice);
				if (csisList != null){
					csis = csisList.getElements();
				}
			} catch (ContainerNotFoundExceptionFault e) {
				System.out.println("Exception: Container Not Found");
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (csis != null && csis.length > 0) 
				printBriefContentServices(csis);
			else
				System.out.println("No Content Services Available");
        } else {
            System.out.println("Invalid content service entered: " + choice);
        }


    }

    public void printBriefContentServices(ContentServiceInfo[] csis) {
        for (int i = 0; i < csis.length; i++) {
            ContentServiceInfo csi = csis[i];
            System.out.println(csi.getContentServiceId() + "#" +
                csi.getContainerInfo().getContainerName() + "#" +
                csi.getContentServiceDisplayName() + "#" +
                csi.getHomeUrl() +
                "#" + (csi.getLoginForm() == null ? "null" : "value"));
        }
    }
    
    /**
     * This method is to get the MFAtype of the particular content service id. 
     * @param content_service_id
     * @return
     */
    public String getMfAType(long content_service_id) {
    	ContentServiceInfo csi = null;
    	try {
    		csi = cst.getContentServiceInfo(getCobrandContext(), content_service_id);
    	} catch (IAVDataRequestNotSupportedExceptionFault e) {
			throw new RuntimeException("Exception: This Content Service is not enabled for IAV");			
		} catch (ContentServiceNotFoundExceptionFault e) {
			throw new RuntimeException("Exception: Content Service not enabled for this cobrand");
		}
    	catch (Exception e) {}
    	return ((csi == null) || (csi.getMfaType() == null)) ? null : csi.getMfaType().getValue();
    }

    /**
	 * This method checks if a content service can be added as a payee (for
	 * DirectPay)
	 * @param csId
	 * @return
	 */
    public boolean checkContentServiceForPayee(long csId) {
    	boolean isPayee = false;
    	try {
	    	ContentServiceInfo csInfo = cst.getContentServiceInfo(getCobrandContext(), new Long(csId));
	    	if (csInfo != null && csInfo.getContainerInfo() != null) {
	    		String container = csInfo.getContainerInfo().getContainerName();
	    		if (container.equalsIgnoreCase(ContainerTypesHelper.BILL)
	                    || container.equalsIgnoreCase(ContainerTypesHelper.ISP)
	                    || container.equalsIgnoreCase(ContainerTypesHelper.INSURANCE)
	                    || container.equalsIgnoreCase(ContainerTypesHelper.LOAN)
	                    || container.equalsIgnoreCase(ContainerTypesHelper.TELEPHONE)
	                    || container.equalsIgnoreCase(ContainerTypesHelper.MINUTES)
	                    || container.equalsIgnoreCase(ContainerTypesHelper.UTILITIES)
	                    || container.equalsIgnoreCase(ContainerTypesHelper.CABLE_SATELLITE)
	                    || container.equalsIgnoreCase(ContainerTypesHelper.BILL_PAY_SERVICE)
	                    || container.equalsIgnoreCase(ContainerTypesHelper.CREDIT_CARD)
	                    || container.equalsIgnoreCase(ContainerTypesHelper.MORTGAGE))
	                {
	    				isPayee = true;
	                }
	    	}
    	} catch (ContentServiceNotFoundExceptionFault csnfe) {
    		System.out.println("ContentService " + csId + " not found for this Cobrand...\n");    		
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return isPayee;
    }


}
