/*
 * Copyright 2009 Yodlee, Inc.  All Rights Reserved.  Your use of this code 
 * requires a license from Yodlee.  Any such license to this code is 
 * restricted to evaluation/illustrative purposes only. It is not intended 
 * for use in a production environment, and Yodlee disclaims all warranties 
 * and/or support obligations concerning this code, regardless of the terms 
 * of any other agreements between Yodlee and you."
 */
package com.yodlee.sampleapps;

import javax.xml.rpc.ServiceException;

import com.yodlee.sampleapps.helper.ContainerTypesHelper;
import com.yodlee.sampleapps.helper.IOUtils;
import com.yodlee.soap.collections.ArrayOfint;
import com.yodlee.soap.collections.List;
import com.yodlee.soap.collections.Map;
import com.yodlee.soap.collections.core.realestate.ArrayOfPropertyInfo;
import com.yodlee.soap.common.ItemSummary;
import com.yodlee.soap.common.UserContext;
import com.yodlee.soap.core.IllegalArgumentValueExceptionFault;
import com.yodlee.soap.core.dataservice.ContainerCriteria;
import com.yodlee.soap.core.dataservice.DataExtent;
import com.yodlee.soap.core.dataservice.ItemData;
import com.yodlee.soap.core.dataservice.SummaryRequest;
import com.yodlee.soap.core.dataservice.YMoney;
import com.yodlee.soap.core.dataservice.dataservice.DataService;
import com.yodlee.soap.core.dataservice.dataservice.DataServiceServiceLocator;
import com.yodlee.soap.core.dataservice.types.HomeValueAccountData;
import com.yodlee.soap.core.dataservice.types.ItemAccountData;
import com.yodlee.soap.core.dataservice.types.PropertyInfoData;
import com.yodlee.soap.core.realestate.PropertyAccountInfo;
import com.yodlee.soap.core.realestate.PropertyAddress;
import com.yodlee.soap.core.realestate.PropertyInfo;
import com.yodlee.soap.core.realestate.SearchResultInfo;
import com.yodlee.soap.core.realestate.homevalueaccountservice.HomeValueAccountService;
import com.yodlee.soap.core.realestate.homevalueaccountservice.HomeValueAccountServiceServiceLocator;

/**
 * This classes demonstrates the use of the various real estate features
 * in the Yodlee system.  The Yodlee Real Estate API relies heavily on
 * Zillow for information about a home.  When using the Yodlee Real Estate
 * Center, there are legal attribution requirements for Zillow.
 */
public class RealEstateCenter extends ApplicationSuper {
	/** A Zillow-calculated account has a CSID of 13059. */
	private static final long ZILLOW_CSID = 13059;
	
	/** A manual real estate account has a CSID of 9980. */
	private static final long MANUAL_CSID = 9980;
	
	public static final int RealEstateLevel = 0; 
	public static final int RealEstatePropertyInfoLevel = 1; 
	
	/** Proxy services for Home Values. */
	HomeValueAccountService homeValueAccountService;
	
    DataService dataService;

	public RealEstateCenter() {
		super();
		HomeValueAccountServiceServiceLocator locator1 = new HomeValueAccountServiceServiceLocator();
        String serviceName1 = locator1.getHomeValueAccountServiceWSDDServiceName();
        locator1.setHomeValueAccountServiceEndpointAddress(System.getProperty("com.yodlee.soap.services.url")
						+ "/" + serviceName1);
        try {
        	homeValueAccountService = locator1.getHomeValueAccountService();
		} catch (ServiceException se) {
			throw new RuntimeException(se);
		}
		DataServiceServiceLocator locator2 = new DataServiceServiceLocator();
        String serviceName2 = locator2.getDataServiceWSDDServiceName();
        locator2.setDataServiceEndpointAddress(System.getProperty("com.yodlee.soap.services.url")
						+ "/" + serviceName2);
        try {
        	dataService = locator2.getDataService();
		} catch (ServiceException se) {
			throw new RuntimeException(se);
		}
	}
	
	/** Navigation Counter. */
	private static int optionCount = 1;
	/** Navigation Menu Choice. */
	private static final int NAV_LIST_REALESTATE = optionCount++;
	/** Navigation Menu Choice. */
	private static final int NAV_SEARCH_PROPERTY = optionCount++;
	/** Navigation Menu Choice. */
	private static final int NAV_ADD_MANUAL = optionCount++;
	/** Navigation Menu Choice. */
	private static final int NAV_QUIT = 0;

	/**
	 * Returns a list of all the real estate accounts in the system.
	 * 
	 * @param userContext
	 */
	public void listRealEstate(UserContext userContext) {
		// Create a search request limited to the real estate
		// container and only include one Extent of data.
		ContainerCriteria containerCriteria = new ContainerCriteria();
		containerCriteria.setContainerType(ContainerTypesHelper.REALESTATE);		
		DataExtent dataExtent = new DataExtent();
		ArrayOfint arrayOfint = new ArrayOfint();
		arrayOfint.setElements(new Integer[]{new Integer(RealEstatePropertyInfoLevel)});
		dataExtent.setExtentLevels(arrayOfint);
		
		//dataExtent.setStartLevel(new Integer(RealEstateLevel));
		//dataExtent.setEndLevel(new Integer(RealEstatePropertyInfoLevel));
		containerCriteria.setDataExtent(dataExtent);
		
		
		ContainerCriteria[] containerCriteriaList = {containerCriteria};
		SummaryRequest summaryRequest = new SummaryRequest();
		List list = new List();
		list.setElements(containerCriteriaList);
		summaryRequest.setContainerCriteria(list);
		/*summaryRequest.setContainerCriteria(new List(containerCriteriaList));*/
		
		Object[] itemSummaries = null;
		com.yodlee.soap.collections.List itemList = null;
		
		try {
			itemList = dataService.getItemSummaries1(userContext, summaryRequest);
			if (itemList != null){
				itemSummaries = itemList.getElements();
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Error fetching Real-Estate accounts..." + e.getMessage());
		}
		if (itemSummaries == null || itemSummaries.length == 0) {
            System.out.println("No Accounts Found");
        } else {
        	// Cycle through all the accounts returned and display information on them.
            for (int i = 0; i<itemSummaries.length; i++) {
            	ItemSummary itemSummary = (ItemSummary) itemSummaries[i];
            	String accountName = "";
            	
            	ItemData itemData = itemSummary.getItemData();
            	System.out.println("Item Id: " + itemSummary.getItemId());
            	
            	//
            	itemSummary.getItemDisplayName();
            	
            	System.out.println("Content Service Id : " +  itemSummary.getContentServiceId());
            	
            	if (itemSummary.getContentServiceInfo() != null)
            		System.out.println(itemSummary.getContentServiceInfo().getContentServiceDisplayName());
            	if(itemData != null) {
            		List accountsList = itemData.getAccounts();
            		Object[] accounts = null;
            		if (accountsList != null){
            		 accounts = accountsList.getElements();
            		}
            		if (accounts != null) {
	            		if(accounts != null && accounts.length > 0) {
	            			for (int j=0; j<accounts.length ; j++){
	            			if (accounts[j] instanceof HomeValueAccountData){
	            				HomeValueAccountData homeValueAccountData = (HomeValueAccountData)accounts[j];
	            					
	            				Map entryMap = homeValueAccountData.getAccountDisplayName().getAccountNames();
	            				com.yodlee.soap.collections.Entry[] entries = null;
	            				if (entryMap != null) {
		            			    entries = entryMap.getTable();
		            			    for (int ent = 0; ent < entries.length; ent++) {
			            				com.yodlee.soap.collections.Entry entry = entries[ent];
			            				//if ("ACCNAME".equals(entry.getKey()+"")) {
			            					accountName = (String)entry.getValue();
			            				//}
			            			}
		            		    }
                                System.out.println("Account Holder Name: " + homeValueAccountData.getAccountHolder());
	            				
	            				System.out.println("Account name : " + accountName); 
	            				
	            				List propertyInfo = homeValueAccountData.getPropertyInfos();
	            				Object [] propertyInfoDataArray = null;
	            				if (propertyInfo != null){
	            					propertyInfoDataArray = propertyInfo.getElements();
	            				}
	            				
	            				for (int k=0; k<propertyInfoDataArray.length; k++){
	            					PropertyInfoData  propertyInfoData = (PropertyInfoData)propertyInfoDataArray[k];
	            				System.out.println("Street Address  : " + propertyInfoData.getStreet());
	            				System.out.println("city : " + propertyInfoData.getCity() 
	            						+ "state : " + propertyInfoData.getState() + 
	            						"zip : " + propertyInfoData.getZipcode() );
	            				System.out.println("property value : " + propertyInfoData.getCurrentValue().getAmount());
	            				}
	            			}
	            			/*
	            			ItemAccountData itemAccountData = (ItemAccountData) accounts[0];
	            			Map entryMap = itemAccountData.getAccountDisplayName().getAccountNames();
	            			com.yodlee.soap.collections.Entry[] entries = null;
	            			if (entryMap != null) {
	            			    entries = entryMap.getTable();
	            			    for (int ent = 0; ent < entries.length; ent++) {
		            				com.yodlee.soap.collections.Entry entry = entries[ent];
		            				if ("ACCNAME".equals(entry.getKey()+"")) {
		            					accountName = (String)entry.getValue();
		            				}
		            			}
	            		    }
	            			
       			
	            		} */
            		}
            	}
            	
            	
            }

         }
       }
		
	 }
	}

	/**
	 * Allows the user to search for and to add a particular property
	 * @param userContext the user context
	 */
	public void searchProperty(UserContext userContext) {
		// Gather the address information from the user.
		String streetAddress = IOUtils.promptInput("Enter street (e.g. '3600 Bridge Parkway'): ", "Try again: ");
		String cityStateZip = IOUtils.promptInput("Enter city, state zip (e.g. 'Redwood City, CA  94065'): ", "Try again: ");
		
		// Create a property object
		PropertyAddress propertyAddress = new PropertyAddress();
		propertyAddress.setStreet(streetAddress);
		propertyAddress.setCityStateZip(cityStateZip);
		
		SearchResultInfo searchResultInfo;
		try {
			// Execute the Zillow Search for this property.
			// http://www.zillow.com/howto/api/GetSearchResults.htm
			searchResultInfo =
				homeValueAccountService.searchProperty(
					getCobrandContext(), ZILLOW_CSID, propertyAddress);
		} catch (IllegalArgumentValueExceptionFault e) {
			System.out.println("An illegal argument exception with "
					+ "[Null argument specified] could mean that the "
					+ "Zillow service is not enabled for the cobrand. "
					+ "Check to make sure Yodlee deployment has enabled "
					+ "the Zillow service.");
			throw new RuntimeException(e);
		} catch (Exception e) {
			throw new RuntimeException("Error searching property..." + e.getMessage());
		}
		
		// Show the user the result from the Zillow API call.
		System.out.println("\nSearch finished with response: [" 
				+ searchResultInfo.getErrorCode()
				+ "] "
				+ searchResultInfo.getErrorMessage());
		
		// If there was an error, give the friendlier error message.
		switch(new Long(searchResultInfo.getErrorCode()).intValue()) {
			case 0: break;
			case 1: System.out.println("Service error-there was a server-side error while processing the request"); break;
			case 2: System.out.println("The specified ZWSID parameter was invalid or not specified in the request"); break;
			case 3: System.out.println("Web services are currently unavailable"); break;
			case 4: System.out.println("The API call is currently unavailable"); break;
			case 500: System.out.println("Invalid or missing address parameter"); break;
			case 501: System.out.println("Invalid or missing citystatezip parameter"); break;
			case 502: System.out.println("No results found"); break;
			case 503: System.out.println("Failed to resolve city, state or ZIP code"); break;
			case 504: System.out.println("No coverage for specified area"); break;
			case 505: System.out.println("Timeout"); break;
			default: System.out.println("An unknown error occurred."); break;
		}
		
		// This search result gives back an array of properties for the user.
		// The array will be empty if there was not a match
		ArrayOfPropertyInfo propertyInfo = searchResultInfo.getPropertyInfo();
		PropertyInfo[] propertyInfoArray = null;
		if (propertyInfo != null){
			propertyInfoArray = propertyInfo.getElements();
		}
		if(propertyInfoArray == null || propertyInfoArray.length == 0) {
			System.out.println("No properties were found.");
		} else {
			// Cycle through the array and show the user the properties found.
			for(int i=0; i<propertyInfoArray.length; i++) {
				System.out.print("  ");
				System.out.print(i+1);
				System.out.print(" : ");
				
				// Show the value to the user.  A property without a value cannot be
				// added through Zillow, even though Zillow knows about the address
				YMoney currentvalue = propertyInfoArray[i].getCurrentValue();
				if (currentvalue == null || currentvalue.getAmount() == null) {
					System.out.print("N/A");
				} else {
					System.out.print(propertyInfoArray[i].getCurrentValue().getAmount());
				}
				System.out.print(" - ");
				System.out.print(propertyInfoArray[i].getStreet());
				System.out.print(", ");
				System.out.print(propertyInfoArray[i].getCity());
				System.out.print(", ");
				System.out.print(propertyInfoArray[i].getState());
				System.out.print("  ");
				System.out.println(propertyInfoArray[i].getZipCode());
			}
			
			boolean loop = true;
			while(loop) {
				String houseToAdd = IOUtils.promptInput("Enter the house to add (0 to quit): ", "Try again: ");
				int houseToAddInt = Integer.parseInt(houseToAdd);
				if(houseToAddInt == 0) {
					loop = false;
				} else if (houseToAddInt > 0 && houseToAddInt <= propertyInfoArray.length) {
					// Check if there is a value for this property.  If there is no value, it's
					// probably a commercial property and cannot be added.
					YMoney currentvalue = propertyInfoArray[houseToAddInt-1].getCurrentValue();
					if (currentvalue == null || currentvalue.getAmount() == null) {
						System.out.println("No Zestimate available for this home.");
					} else {
						String accountName = IOUtils.promptInput("Enter the account name: ", "Try again: ");
						String nickName = IOUtils.promptInput("Enter the account nickname: ", "Try again: ");
						PropertyAccountInfo propertyAccountInfo = new PropertyAccountInfo();
						propertyAccountInfo.setAccountName(accountName);
						propertyAccountInfo.setNickName(nickName);
						propertyAccountInfo.setIsIncludeInNetworth(true);
						loop = false;
						
						long itemId;
						try {
							itemId = homeValueAccountService.addHomeValueItemForContentService(
									userContext, ZILLOW_CSID, propertyAccountInfo,
									propertyInfoArray[houseToAddInt-1]);
						} catch (Exception e) {							
							e.printStackTrace();
							throw new RuntimeException(
									"Error adding home value for the content service..."
											+ e.getMessage());
						}
						
						System.out.println("Successfully added with Item Id: " + itemId);
					}
				} else {
					System.out.println("Illegal value.");
				}
			}
		}
	}
	
	/**
	 * Allows the user to add a manual property value.
	 * 
	 * @param userContext context for the user to add
	 */
	public void addManualProperty(UserContext userContext) {
		String accountName = IOUtils.promptInput("Enter account name: ", "Try again: ");
		String nickName = IOUtils.promptInput("Enter account nick name: ", "Try again: ");
		//String memo = IOUtils.promptInput("Enter address (e.g. 3600 Bridge Parkway, Redwood City, CA  94065: ", "Try again: ");
		PropertyAccountInfo propertyAccountInfo = new PropertyAccountInfo();
		propertyAccountInfo.setAccountName(accountName);
		propertyAccountInfo.setNickName(nickName);
		//propertyAccountInfo.setMemo(memo);
		
		PropertyInfo propertyInfo = new PropertyInfo();
		boolean loop = true;
		while(loop) {
			try {
				
				String street = IOUtils.promptInput("Enter street (e.g. 3600 Bridge Parkway: ", "Try again: ");
				String city = IOUtils.promptInput("Enter city (e.g. Redwood City: ", "Try again: ");
				String state = IOUtils.promptInput("Enter state ( e.g: CA: ", "Try again: ");
				String zipCode = IOUtils.promptInput("Enter zip (e.g. 94065: ", "Try again: ");
				propertyInfo.setStreet(street);
				propertyInfo.setCity(city);
				propertyInfo.setState(state);
				propertyInfo.setZipCode(zipCode);
				String userEnteredValue = IOUtils.promptInput("Enter property value (e.g. 1000000): ", "Try again: ");
				String currency = IOUtils.promptInput("Enter the ISO currency code (e.g. USD, GBP, etc...): ", "Try again: ");
				Double amount = new Double(userEnteredValue);
				YMoney currentValue = new YMoney();
				currentValue.setAmount(amount);
				currentValue.setCurrencyCode(currency);
				//YMoney currentValue = new YMoney(amount, currency);
				propertyInfo.setCurrentValue(currentValue);
				loop = false;
			} catch (RuntimeException e) {
				e.printStackTrace();
				System.out.println("Failed with error, try again. : " + e);
			}
		}
		
		Long realEstateItemId;
		try {
		realEstateItemId = homeValueAccountService.addHomeValueItemForContentService(userContext, MANUAL_CSID,
				propertyAccountInfo, propertyInfo);
		} catch (Exception e) {	
			e.printStackTrace();
			throw new RuntimeException(
					
					"Error adding home value for the content service..."
							+ e.getMessage());
		}
		
		System.out.println("Successfully added with Item Id: " + realEstateItemId.longValue());
	}

	
	/**
	 * Execute the sub-menu for managing property.
	 * 
	 * @param userContext context of the logged in user
	 */
	public void doMenu(UserContext userContext) {
		boolean loop = true;
		int choice = 0;
		while (loop) {
			try {
				System.out.println("\nReal Estate Center Menu");
				System.out.println(NAV_LIST_REALESTATE
						+ ". List Real Estate Items");
				System.out.println(NAV_SEARCH_PROPERTY
						+ ". Search/Add Zillow Property");				
				System.out.println(NAV_ADD_MANUAL
						+ ". Add Manual Property");				
				System.out.println("" + NAV_QUIT + ". Exit");
				System.out.println("\n");
				System.out.print("Enter Choice : ");
				choice = IOUtils.readInt();

				if(NAV_LIST_REALESTATE == choice) {
					listRealEstate(userContext);
				} else if(NAV_SEARCH_PROPERTY == choice) {
				    searchProperty(userContext);
				} else if (NAV_ADD_MANUAL == choice) {
					addManualProperty(userContext);
				} else if (NAV_QUIT == choice) {
				    loop = false;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}
    
}
