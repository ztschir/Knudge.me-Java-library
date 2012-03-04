/*
 * Copyright 2008 Yodlee, Inc.  All Rights Reserved.  Your use of this code
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
import java.util.StringTokenizer;

import javax.xml.rpc.ServiceException;

import com.yodlee.soap.collections.ArrayOfString;
import com.yodlee.soap.collections.List;
import com.yodlee.soap.collections.common.ArrayOfContentServiceInfo;
import com.yodlee.soap.common.CobrandContext;
import com.yodlee.soap.common.ContentServiceInfo;
import com.yodlee.soap.common.UserContext;
import com.yodlee.soap.core.ContentServiceNotFoundExceptionFault;
import com.yodlee.soap.core.dataservice.ContainerNotFoundExceptionFault;
import com.yodlee.soap.core.search.search.Search;
import com.yodlee.soap.core.search.search.SearchServiceLocator;
import com.yodlee.soap.core.verification.IAVDataRequestNotSupportedExceptionFault;
import com.yodlee.soap.ext.traversal.contentservicetraversal.ContentServiceTraversal;
import com.yodlee.soap.ext.traversal.contentservicetraversal.ContentServiceTraversalServiceLocator;

public class ContentServiceManagement implements Menu {
    
	public static final int AutoLoginManagement_CLIENT_ENABLED = 3; 
	public static final int AutoLoginManagement_HTTP = 4; 
	public static final int AutoLoginManagement_NOT_SUPPORTED = 5; 
	public static final int AutoLoginManagement_PROXY = 2; 
	public static final int AutoLoginManagement_SIMPLE = 1; 

	private String[] menuItems =
        new String[] {
            "Search content services", 
            "View Single content service", 
            "View all content services (slow)", 
            "View services for a specific container",
        };
    
    protected ContentServiceTraversal cst;
    protected Search search;
    
    public ContentServiceManagement() {
		super();
		SearchServiceLocator locator = new SearchServiceLocator();
		String serviceName = locator
				.getSearchServiceWSDDServiceName();
		locator
				.setSearchServiceEndpointAddress(System.getProperty("com.yodlee.soap.services.url")
						+ "/" + serviceName);
		try {
			search = locator.getSearchService();

		} catch (ServiceException se) {
			throw new RuntimeException(se);
		}
		
		ContentServiceTraversalServiceLocator locator1 = new ContentServiceTraversalServiceLocator();
		String serviceName1 = locator1
				.getContentServiceTraversalServiceWSDDServiceName();
		locator1
				.setContentServiceTraversalServiceEndpointAddress(System.getProperty("com.yodlee.soap.services.url")
						+ "/" + serviceName1);
		try {
			cst = locator1.getContentServiceTraversalService();

		} catch (ServiceException se) {
			throw new RuntimeException(se);
		}
	}

	/**
     * Executes the Menu.
     * @param cobrandContext context of the cobrand
     * @param userContext context of the user
     * @return context of the user
     */
    public UserContext doMenuItem(
        int menuItem, CobrandContext cobrandContext, UserContext userContext) {
        switch (menuItem) {
	        case 1: doSearch(cobrandContext); break;
	        case 2: viewSingleServiceDetails(cobrandContext); break;
	        case 3: viewAllContentServices(cobrandContext); break;
	        case 4: viewContainerServices(cobrandContext);
        }

        return userContext;
    }

	public String[] getMenuItems() {
		return menuItems;
	}

	public String getMenuName() {
		return "Content Service Management Menu";
	}
	
    /**
     * This will do the search for full content services and then just for the IDS
     */
    public void doSearch(CobrandContext cobrandContext) {
    	
        System.out.print("Enter Search String: ");

        String searchString = IOUtils.readStr();
        searchString = andKeywords(searchString);

        System.out.println("Searching for \"" + searchString + "\" and returning full content services...");
        List cts = null;
        try {
        	cts = search.searchContentServices(cobrandContext, searchString);
        } catch (Exception e) {
			e.printStackTrace();
		}
        
        if (cts == null || cts.getElements().length == 0) {
            System.out.println("Search returned no results");
        } else {
            for (int c = 0; c < cts.getElements().length; c++){
                ContentServiceInfo csi	= (ContentServiceInfo)cts.getElements(c);
                String displayName = csi.getContentServiceDisplayName();
                Long csId = csi.getContentServiceId();
                System.out.println("\t DisplayName=" + displayName + " (" + csId + "), " + csi.getContainerInfo().getContainerName());
            }
            System.out.println("Number of results: " + cts.getElements().length);
            System.out.println("]\n");
        }   
    }
    
    /**
     * View the details of a single content service
     */
    public void viewSingleServiceDetails(CobrandContext cobrandContext) {    	
        
        System.out.print("Enter in the content service ID to view details >> : ");
        long choice = IOUtils.readLong();

        // passing a "1" as the third arg gets keywords
        try {
        	ContentServiceInfo contentServiceInfo = 
        		cst.getContentServiceInfo1(
        				cobrandContext, new Long(choice), new Integer(1));
        	
        	printContentServiceInfo(cobrandContext, contentServiceInfo);
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
    public void printContentServiceInfo(CobrandContext cobrandContext, ContentServiceInfo csi) {    	
    	
        System.out.println("Content Service id: " + csi.getContentServiceId());
        System.out.println("Display name: " + csi.getContentServiceDisplayName());
        System.out.println("Container type: " + csi.getContainerInfo().getContainerName());
        System.out.println("Registration URL: " + csi.getRegistrationUrl());
        System.out.println("Home URL: " + csi.getHomeUrl());
        System.out.println("Home URL: " + csi.getFaviconImageContent());

        System.out.println("Login URL: " + csi.getLoginUrl());
        System.out.println("MFA Type: " + ((csi.getMfaType()==null)?"null":csi.getMfaType().getValue()));
        String autoLoginType = "UNKNOWN";
        switch(csi.getAutoLoginType().intValue()) {
			case AutoLoginManagement_CLIENT_ENABLED: autoLoginType = "CLIENT_ENABLED"; break;
			case AutoLoginManagement_HTTP: autoLoginType = "HTTP"; break;
			case AutoLoginManagement_NOT_SUPPORTED: autoLoginType = "NOT_SUPPORTED "; break;
			case AutoLoginManagement_PROXY : autoLoginType = "PROXY"; break;
			case AutoLoginManagement_SIMPLE: autoLoginType = "SIMPLE"; break;
		}
        System.out.println("AutoLogin Type: " + autoLoginType);

        ArrayOfString keywords = csi.getKeywords();
        System.out.print("Search keywords: " );
        for (int i = 0; i < keywords.getElements().length; i++) {
            String keyword = keywords.getElements(i);
            System.out.print(keyword + ",");
        }
        System.out.println("");
        
        try {
	        if(csi.isHasSiblingContentServices()) {
	        	ArrayOfContentServiceInfo siblingCSIDs =
	        		cst.getSiblingContentServices(
	        				cobrandContext, csi.getContentServiceId());
	
	        	System.out.println(siblingCSIDs.getElements().length + " sibling sites available");        	
	        	for(int i=0; i< siblingCSIDs.getElements().length; i++) {
	        		System.out.println("  Sibling: " 
	        				+ siblingCSIDs.getElements(i).getContentServiceDisplayName()
	        				+ " ("
	        				+ siblingCSIDs.getElements(i).getContentServiceId()
	        				+ ")");
	        	}
	        } else {
	        	System.out.println("No sibling sites available");
	        }
	        
	        ArrayOfContentServiceInfo sharedCSIDs = 
	        	cst.getContentServicesBySite(
	        			cobrandContext, csi.getSiteId());
	        
	        if(sharedCSIDs.getElements().length > 1) {
	        	System.out.println((sharedCSIDs.getElements().length-1) + " shared sites available");        	
	        	for(int i=0; i< sharedCSIDs.getElements().length; i++) {
	        		if(csi.getContentServiceId() != sharedCSIDs.getElements(i).getContentServiceId()) {
		        		System.out.println("  Shared: " 
		        				+ sharedCSIDs.getElements(i).getContentServiceDisplayName()
		        				+ " ("
		        				+ sharedCSIDs.getElements(i).getContentServiceId()
		        				+ ")");
	        		}
	        	}
	        } else {
	        	System.out.println("No shared sites available");
	        }
        } catch (Exception e) {
			e.printStackTrace();
		}
        
        System.out.println("\n\n");
    }

    /**
     * This method will give a summary of how many services there are per container and
     * give the user the option to print them all out
     */
    public void viewAllContentServices(CobrandContext cobrandContext) {
    	
    	com.yodlee.soap.collections.Map serviceEntryMap = null;
    	com.yodlee.soap.collections.Entry[] serviceMap = null;
        try {
        	serviceEntryMap = cst.getContentServicesByContainerType(cobrandContext);
        	if (serviceEntryMap != null) {
        		serviceMap = serviceEntryMap.getTable();
        	}
        } catch (Exception e) {}
        System.out.println("Retrieved the following number of services:");
        com.yodlee.soap.collections.Entry entry = null;
        Object container = null;
        ArrayOfContentServiceInfo csis = null;
        for (int i = 0; i < serviceMap.length; i++) {
        	entry = serviceMap[i];
        	container = entry.getKey();
        	csis = (ArrayOfContentServiceInfo)entry.getValue();
        	System.out.println("[" + container + "] - " + csis.getElements().length);        	
        }
        System.out.print("Do you want a dump of all the services? [y/n]:");
        String choice = IOUtils.readStr();
        if (choice.equals("y") || choice.equals("Y")) {
        	for (int i = 0; i < serviceMap.length; i++) {
            	entry = serviceMap[i];
            	container = entry.getKey();
            	csis = (ArrayOfContentServiceInfo)entry.getValue();
            	printBriefContentServices(csis);       	
            }
        }
    }    
    
    public void viewContainerServices(CobrandContext cobrandContext) {
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
        Integer i = new Integer(16);
        if (ContainerTypesHelper.isValid(choice)){
            ArrayOfContentServiceInfo csis = null;
            try {
				csis = cst.getContentServicesByContainerType3(cobrandContext, choice,i);
			} catch (ContainerNotFoundExceptionFault e) {
				System.out.println("Exception: Container Not Found");
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (csis != null && csis.getElements().length > 0) 
				printBriefContentServices(csis);
			else
				System.out.println("No Content Services Available");
        } else {
            System.out.println("Invalid content service entered: " + choice);
        }
    }

    /**
     * Does a brief display of a content service.
     * 
     * @param csis the content service to display.
     */
    public void printBriefContentServices(ArrayOfContentServiceInfo csis) {
        for (int i = 0; i < csis.getElements().length; i++) {
            ContentServiceInfo csi = csis.getElements(i);
            System.out.println(csi.getContentServiceId() + "#" +
                csi.getContainerInfo().getContainerName() + "#" +
                csi.getContentServiceDisplayName() + "#" +
                csi.getHomeUrl() +
                "#" + (csi.getLoginForm() == null ? "null" : "value"));
        }
    } 
    
    /**
     * Does a brief display of a content service.
     * 
     * @param csis the content service to display.
     */
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
    
    private static String formatKeyword(String keyword)
    {
        //System.Console.WriteLine("formatKeyword(" + keyword + ")");
        String ret = "";
        if (keyword != null)
        {
            keyword = keyword.trim();
            if (keyword.length() > 0)
            {
                ret += keyword;
                if (!keyword.endsWith("*"))
                {
                    ret += "*";
                }
            }
        }
        return ret.toLowerCase();
    }

    private static String andKeywords(String keywords)
    {
        //System.Console.WriteLine("andKeywords(" + keywords + ")");
        String sb = "";
        if (keywords != null)
        {
            StringTokenizer st = new StringTokenizer(keywords, " ");
            String token;
            while (st.hasMoreTokens())
            {
                token = st.nextToken();
                sb += formatKeyword(token);
                if (st.hasMoreTokens())
                {
                    sb += " OR ";
                }
            }
        }
        //System.Console.WriteLine("search key=" + sb);
        return sb;
    }
    
    /**
     * View FavIcon/Image of a single content service
     */
    public void viewFavIconForContentService(CobrandContext cobrandContext) {
        System.out.print("Enter in the content service ID to view details >> : ");
        long choice = IOUtils.readLong();

        try {
        	int loginFormRequired = 16;
        	int loginFormLastModifiedDateRequired = 2;
        	int keyWordsRequired = 1; 
        	int iconAndFaviconImagesRequired = 128; 
        	
        	// 128 flag is for fav icon
            int flags = 16 | 2 | 1 | 128;
              
        	ContentServiceInfo csi = cst.getContentServiceInfo1(cobrandContext,new Long(choice), new Integer(flags));
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
     * This method is to get the MFAtype of the particular content service id. 
     * @param content_service_id
     * @return
     */
    public static String getMfAType(CobrandContext cobrandContext, long content_service_id) {
    	ContentServiceInfo csi = null;
    	ContentServiceTraversal csTraversal;
    	try {
    		ContentServiceTraversalServiceLocator locator1 = new ContentServiceTraversalServiceLocator();
			String serviceName1 = locator1
					.getContentServiceTraversalServiceWSDDServiceName();
			locator1.setContentServiceTraversalServiceEndpointAddress(System
					.getProperty("com.yodlee.soap.services.url")
					+ "/" + serviceName1);

			csTraversal = locator1.getContentServiceTraversalService();
			csi = csTraversal.getContentServiceInfo(cobrandContext,
					new Long(content_service_id));
    	} catch (IAVDataRequestNotSupportedExceptionFault e) {
			throw new RuntimeException("Exception: This Content Service is not enabled for IAV");			
		} catch (ContentServiceNotFoundExceptionFault e) {
			throw new RuntimeException("Exception: Content Service not enabled for this cobrand");
		}
    	catch (Exception e) {}
    	return ((csi == null) || (csi.getMfaType() == null)) ? null : csi.getMfaType().getValue();
    }

}
