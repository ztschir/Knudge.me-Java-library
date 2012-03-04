/*
 * Copyright 2009 Yodlee, Inc.  All Rights Reserved.  Your use of this code 
 * requires a license from Yodlee.  Any such license to this code is 
 * restricted to evaluation/illustrative purposes only. It is not intended 
 * for use in a production environment, and Yodlee disclaims all warranties 
 * and/or support obligations concerning this code, regardless of the terms 
 * of any other agreements between Yodlee and you."
 */
package YodleeSrc;

import java.text.SimpleDateFormat;

import java.util.Calendar;

import java.util.TimeZone;
import java.util.ArrayList;

import javax.xml.rpc.ServiceException;

import org.apache.axis.EngineConfiguration;
import org.apache.axis.configuration.FileProvider;

import com.yodlee.soap.appext.base.datapoint.vo.DataPointType;
import com.yodlee.soap.collections.List;

import com.yodlee.soap.common.CobrandContext;
import com.yodlee.soap.common.RefreshInfo;
import com.yodlee.soap.common.UserContext;
import com.yodlee.soap.common.NVPair;
import com.yodlee.soap.collections.common.ArrayOfNVPair;


import com.yodlee.soap.core.dataservice.dataservice.DataService;
import com.yodlee.soap.core.dataservice.dataservice.DataServiceServiceLocator;

import com.yodlee.soap.core.datapoint.categoryspentdatapointservice.CategorySpentDataPointService;
import com.yodlee.soap.core.datapoint.categoryspentdatapointservice.*;
import com.yodlee.soap.core.datapoint.BatchCategorySpentDataPointRequest;
import com.yodlee.soap.core.datapoint.BatchCategorySpentDataPointResponse;
import com.yodlee.soap.core.datapoint.MonthlyDataPoint;
import com.yodlee.soap.core.datapoint.MultiDataPoints;
import com.yodlee.soap.core.datapoint.CategorySpentDataPoint;

import com.yodlee.soap.collections.core.datapoint.ArrayOfBatchCategorySpentDataPointRequest;
import com.yodlee.soap.collections.core.datapoint.ArrayOfBatchCategorySpentDataPointResponse;
import com.yodlee.soap.collections.core.datapoint.ArrayOfMonthlyDataPoint;




/**
 * This classes demonstrates the use of the CategorySpentDataPointService.
 */
public class CategorySpentDataPointHelper extends ApplicationSuper {
	
	public static final String TRANSACTION_CATEGORY_ID = "TRANSACTION_CATEGORY_ID";
	public static final String TRANSACTION_CATEGORY_TYPE_ID = "TRANSACTION_CATEGORY_TYPE_ID";
	public static final String GROUP_ID = "GROUP_ID";
	public static final String ZIP_ID = "ZIP_ID";
	public static final String CITY_NAME = "CITY_NAME";
	public static final String STATE_ID = "STATE_ID";
	public static final String WEEK_NUMBER = "WEEK_NUMBER";
	public static final String MONTH_NUMBER = "MONTH_NUMBER";
	public static final String YEAR = "YEAR";
	public static final String REFERENCE_DATE = "REFERENCE_DATE";
	public static final String START_DATE = "START_DATE";
	public static final String END_DATE = "END_DATE";

	public static final int	SHAREABLE =	1;
	public static final int	SINGLE_VALUE =	0;

    DataService dataService;
    
	/** Proxy services for Category Spent Datapoint Service. */
    CategorySpentDataPointService categorySpentDataPointService;

	public CategorySpentDataPointHelper() {
		super();
		
        String deploymentFile = "C:/p4/razor/sampleapps/10.2/java/client_deploy.wsdd";
        EngineConfiguration engineConfig = new FileProvider(deploymentFile);

		CategorySpentDataPointServiceServiceLocator locator1 = new CategorySpentDataPointServiceServiceLocator(engineConfig);
        String serviceName1 = locator1.getCategorySpentDataPointServiceWSDDServiceName();
        locator1.setCategorySpentDataPointServiceEndpointAddress(System.getProperty("com.yodlee.soap.services.url")
						+ "/" + serviceName1);
        try {
        	categorySpentDataPointService = locator1.getCategorySpentDataPointService();
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
	private static final int NAV_LIST_DATA_POINT_TYPES = optionCount++;
	/** Navigation Menu Choice. */
	private static final int NAV_GET_DATA_POINT = optionCount++;
	/** Navigation Menu Choice. */
	private static final int NAV_GET_DATA_POINTS = optionCount++;
	
	/** Navigation Menu Choice. */
	private static final int NAV_GET_DATA_POINT_TYPE = optionCount++;
	/** Navigation Menu Choice. */
	private static final int NAV_QUIT = 0;

	/**
	 * Returns the meta data of all supported category spent data points. 
	 * The meta data also includes the list of parameters required by a particular data point.
	 * 
	 * @param cobrandContext
	 */
	public void listDataPointTypes(CobrandContext cobrandContext) {

		try {
			categorySpentDataPointService.getAllCategorySpentDataPointTypes(cobrandContext);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Error fetching CategorySpentDataPointTypes..." + e.getMessage());
		}
		
	}

	/**
	 * Retrieve the category spent data point data for the data point identified by dataPointTypeId
	 * @param userContext
	 */
	public void getDataPoint(UserContext userContext) {

			/* Using this DataPointType
				 <id>1</id>
				 <outputTypeId>2</outputTypeId>
				 <description>Retrieve the data point metrics for a particular member, transaction category, group id within a specified date range.</description>
				<parameters>
				 <elements>TRANSACTION_CATEGORY_ID</elements>
				 <elements>START_DATE</elements>
				 <elements>END_DATE</elements>
				 <elements>GROUP_ID</elements>
				 </parameters>
			 */
		
		Long dataPointTypeId = new Long("1");
		long categoryId = Long.parseLong("1");

        DataPointType dataPointType = DataPointType.MONTHLY;

        // Determine dates for data points
        Calendar endDate = Calendar.getInstance();
        Calendar fromDate = getStartDate(Calendar.getInstance(), 6, dataPointType); // 6 months
		SimpleDateFormat dateFormat = new SimpleDateFormat ("dd-MMM-yyyy");

		
		ArrayOfNVPair paramList = new ArrayOfNVPair();

		ArrayList<NVPair> nvPairs = new ArrayList();

		NVPair nvPair1 = new NVPair();
        nvPair1.setStatus(SHAREABLE);
        nvPair1.setType(SINGLE_VALUE);
		nvPair1.setName(TRANSACTION_CATEGORY_ID);	
        nvPair1.setValues(new List(new Object[]{"1"}));  
        nvPairs.add(0, nvPair1);

        
		NVPair nvPair2 = new NVPair();
		nvPair2.setName(START_DATE);
        nvPair2.setValues(new List(new Object[]{dateFormat.format(fromDate.getTime())}));
        nvPairs.add(1, nvPair2);

		NVPair nvPair3 = new NVPair();
		nvPair3.setName(END_DATE);		
		nvPair3.setValues(new List(new Object[]{dateFormat.format(endDate.getTime())}));
        nvPairs.add(2, nvPair3);
        

        NVPair[] nvp = new NVPair[nvPairs.size()];
        
		paramList.setElements(nvPairs.toArray(nvp));

		try {
			categorySpentDataPointService.getCategorySpentDataPoint(userContext, dataPointTypeId, paramList);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Error fetching CategorySpentDataPoint..." + e.getMessage());
		}
		
	}

	/**
	 * Retrieve the category spent data point data for the data point identified by dataPointTypeId
	 * Using the batch data point method
	 * This is brute force code.  There must be a better way to do this. -Ken
	 * 
	 * @param userContext
	 */
	public void getDataPoints(UserContext userContext) {
		
		/* Using this DataPointTypeId
		 <id>1</id>
		 <outputTypeId>2</outputTypeId>
		 <description>Retrieve the data point metrics for a particular member, transaction category, group id within a specified date range.</description>
		<parameters>
		 <elements>TRANSACTION_CATEGORY_ID</elements>
		 <elements>START_DATE</elements>
		 <elements>END_DATE</elements>
		 <elements>GROUP_ID</elements>
		 </parameters>
	 */
		Long dataPointTypeId = new Long("1");

        DataPointType dataPointType = DataPointType.MONTHLY;

        // Determine dates for data points
        Calendar endDate = Calendar.getInstance();
     
        Calendar fromDate = getStartDate(Calendar.getInstance(), 6, dataPointType); // go back 6 months
         
		SimpleDateFormat dateFormat = new SimpleDateFormat ("dd-MMM-yyyy");

		// date range NV pairs
		NVPair nvStartDate = new NVPair();
		nvStartDate.setName(START_DATE);
		nvStartDate.setValues(new List(new Object[]{dateFormat.format(fromDate.getTime())}));
		
		NVPair nvEndDate = new NVPair();		
		nvEndDate.setName(END_DATE);		
		nvEndDate.setValues(new List(new Object[]{dateFormat.format(endDate.getTime())}));
		
		NVPair nvCat1 = new NVPair();
		nvCat1.setStatus(SHAREABLE);
		nvCat1.setType(SINGLE_VALUE);
		nvCat1.setName(TRANSACTION_CATEGORY_ID);	
		nvCat1.setValues(new List(new Object[]{"1"}));  

		/* <categoryId>25</categoryId>
		 <categoryName>ATM/Cash Withdrawals</categoryName>*/
		NVPair nvCat2 = new NVPair();
		nvCat2.setStatus(SHAREABLE);
		nvCat2.setType(SINGLE_VALUE);
		nvCat2.setName(TRANSACTION_CATEGORY_ID);	
		nvCat2.setValues(new List(new Object[]{"25"}));  

		/*<categoryId>100</categoryId>
		 <categoryName>Advertising</categoryName>*/
		NVPair nvCat3 = new NVPair();
		nvCat3.setStatus(SHAREABLE);
		nvCat3.setType(SINGLE_VALUE);
		nvCat3.setName(TRANSACTION_CATEGORY_ID);	
		nvCat3.setValues(new List(new Object[]{"100"}));  

		/* <categoryId>2</categoryId>
 		<categoryName>Automotive Expenses</categoryName>*/
		NVPair nvCat4 = new NVPair();
		nvCat4.setStatus(SHAREABLE);
		nvCat4.setType(SINGLE_VALUE);
		nvCat4.setName(TRANSACTION_CATEGORY_ID);	
		nvCat4.setValues(new List(new Object[]{"2"}));  

		/*  <categoryId>102</categoryId>
  			<categoryName>Business Miscellaneous</categoryName>*/
		NVPair nvCat5 = new NVPair();
		nvCat5.setStatus(SHAREABLE);
		nvCat5.setType(SINGLE_VALUE);
		nvCat5.setName(TRANSACTION_CATEGORY_ID);	
		nvCat5.setValues(new List(new Object[]{"102"}));  

		/*<categoryId>15</categoryId>
		 <categoryName>Cable/Satellite Services</categoryName>*/
		NVPair nvCat6 = new NVPair();
		nvCat6.setStatus(SHAREABLE);
		nvCat6.setType(SINGLE_VALUE);
		nvCat6.setName(TRANSACTION_CATEGORY_ID);	
		nvCat6.setValues(new List(new Object[]{"15"})); 
		
		/* <categoryId>3</categoryId>
 		<categoryName>Charitable Giving</categoryName>*/
		NVPair nvCat7 = new NVPair();
		nvCat7.setStatus(SHAREABLE);
		nvCat7.setType(SINGLE_VALUE);
		nvCat7.setName(TRANSACTION_CATEGORY_ID);	
		nvCat7.setValues(new List(new Object[]{"3"}));  
		
		/*<categoryId>33</categoryId>
		 <categoryName>Checks</categoryName>*/
		NVPair nvCat8 = new NVPair();
		nvCat8.setStatus(SHAREABLE);
		nvCat8.setType(SINGLE_VALUE);
		nvCat8.setName(TRANSACTION_CATEGORY_ID);	
		nvCat8.setValues(new List(new Object[]{"33"}));  

		/*<categoryId>4</categoryId>
 		<categoryName>Child/Dependent Expenses</categoryName>*/
		NVPair nvCat9 = new NVPair();
		nvCat9.setStatus(SHAREABLE);
		nvCat9.setType(SINGLE_VALUE);
		nvCat9.setName(TRANSACTION_CATEGORY_ID);	
		nvCat9.setValues(new List(new Object[]{"4"}));  
		
		/*<categoryId>5</categoryId>
 		<categoryName>Clothing/Shoes</categoryName>*/
		NVPair nvCat10 = new NVPair();
		nvCat10.setStatus(SHAREABLE);
		nvCat10.setType(SINGLE_VALUE);
		nvCat10.setName(TRANSACTION_CATEGORY_ID);	
		nvCat10.setValues(new List(new Object[]{"5"}));  

		/*<categoryId>108</categoryId>
		 <categoryName>Dues and Subscriptions</categoryName>*/
		NVPair nvCat11 = new NVPair();
		nvCat11.setStatus(SHAREABLE);
		nvCat11.setType(SINGLE_VALUE);
		nvCat11.setName(TRANSACTION_CATEGORY_ID);	
		nvCat11.setValues(new List(new Object[]{"108"}));  

		/*<categoryId>6</categoryId>
		 <categoryName>Education</categoryName>*/
		NVPair nvCat12 = new NVPair();
		nvCat12.setStatus(SHAREABLE);
		nvCat12.setType(SINGLE_VALUE);
		nvCat12.setName(TRANSACTION_CATEGORY_ID);	
		nvCat12.setValues(new List(new Object[]{"6"}));  

		/*<categoryId>43</categoryId>
		 <categoryName>Electronics</categoryName>*/
		NVPair nvCat13 = new NVPair();
		nvCat13.setStatus(SHAREABLE);
		nvCat13.setType(SINGLE_VALUE);
		nvCat13.setName(TRANSACTION_CATEGORY_ID);	
		nvCat13.setValues(new List(new Object[]{"43"}));  

		/*<categoryId>7</categoryId>
		 <categoryName>Entertainment</categoryName>*/
		NVPair nvCat14 = new NVPair();
		nvCat14.setStatus(SHAREABLE);
		nvCat14.setType(SINGLE_VALUE);
		nvCat14.setName(TRANSACTION_CATEGORY_ID);	
		nvCat14.setValues(new List(new Object[]{"7"}));  

		/*<categoryId>8</categoryId>
		 <categoryName>Gasoline/Fuel</categoryName>*/
		NVPair nvCat15 = new NVPair();
		nvCat15.setStatus(SHAREABLE);
		nvCat15.setType(SINGLE_VALUE);
		nvCat15.setName(TRANSACTION_CATEGORY_ID);	
		nvCat15.setValues(new List(new Object[]{"8"}));  

		/*<categoryId>44</categoryId>
		 <categoryName>General Merchandise</categoryName>*/
		NVPair nvCat16 = new NVPair();
		nvCat16.setStatus(SHAREABLE);
		nvCat16.setType(SINGLE_VALUE);
		nvCat16.setName(TRANSACTION_CATEGORY_ID);	
		nvCat16.setValues(new List(new Object[]{"44"}));  

		/*<categoryId>9</categoryId>
		 <categoryName>Gifts</categoryName>*/
		NVPair nvCat17 = new NVPair();
		nvCat17.setStatus(SHAREABLE);
		nvCat17.setType(SINGLE_VALUE);
		nvCat17.setName(TRANSACTION_CATEGORY_ID);	
		nvCat17.setValues(new List(new Object[]{"9"}));  

		/*<categoryId>10</categoryId>
		 <categoryName>Groceries</categoryName>*/
		NVPair nvCat18 = new NVPair();
		nvCat18.setStatus(SHAREABLE);
		nvCat18.setType(SINGLE_VALUE);
		nvCat18.setName(TRANSACTION_CATEGORY_ID);	
		nvCat18.setValues(new List(new Object[]{"10"}));  

		/*<categoryId>11</categoryId>
		 <categoryName>Healthcare/Medical</categoryName>*/
		NVPair nvCat19 = new NVPair();
		nvCat19.setStatus(SHAREABLE);
		nvCat19.setType(SINGLE_VALUE);
		nvCat19.setName(TRANSACTION_CATEGORY_ID);	
		nvCat19.setValues(new List(new Object[]{"11"}));  

		/*<categoryId>34</categoryId>
		 <categoryName>Hobbies</categoryName>*/
		NVPair nvCat20 = new NVPair();
		nvCat20.setStatus(SHAREABLE);
		nvCat20.setType(SINGLE_VALUE);
		nvCat20.setName(TRANSACTION_CATEGORY_ID);	
		nvCat20.setValues(new List(new Object[]{"34"}));  

		/*<categoryId>13</categoryId>
		 <categoryName>Home Improvement</categoryName>*/
		NVPair nvCat21 = new NVPair();
		nvCat21.setStatus(SHAREABLE);
		nvCat21.setType(SINGLE_VALUE);
		nvCat21.setName(TRANSACTION_CATEGORY_ID);	
		nvCat21.setValues(new List(new Object[]{"13"}));  

		/*<categoryId>12</categoryId>
		 <categoryName>Home Maintenance</categoryName>*/
		NVPair nvCat22 = new NVPair();
		nvCat22.setStatus(SHAREABLE);
		nvCat22.setType(SINGLE_VALUE);
		nvCat22.setName(TRANSACTION_CATEGORY_ID);	
		nvCat22.setValues(new List(new Object[]{"12"}));  

		/*<categoryId>14</categoryId>
		 <categoryName>Insurance</categoryName>*/
		NVPair nvCat23 = new NVPair();
		nvCat23.setStatus(SHAREABLE);
		nvCat23.setType(SINGLE_VALUE);
		nvCat23.setName(TRANSACTION_CATEGORY_ID);	
		nvCat23.setValues(new List(new Object[]{"14"}));  

		/*<categoryId>17</categoryId>
		 <categoryName>Loans</categoryName>*/
		NVPair nvCat24 = new NVPair();
		nvCat24.setStatus(SHAREABLE);
		nvCat24.setType(SINGLE_VALUE);
		nvCat24.setName(TRANSACTION_CATEGORY_ID);	
		nvCat24.setValues(new List(new Object[]{"17"}));  

		/*<categoryId>18</categoryId>
		 <categoryName>Mortgages</categoryName>*/
		NVPair nvCat25 = new NVPair();
		nvCat25.setStatus(SHAREABLE);
		nvCat25.setType(SINGLE_VALUE);
		nvCat25.setName(TRANSACTION_CATEGORY_ID);	
		nvCat25.setValues(new List(new Object[]{"18"}));  

		/*<categoryId>110</categoryId>
		 <categoryName>Office Maintenance</categoryName>*/
		NVPair nvCat26 = new NVPair();
		nvCat26.setStatus(SHAREABLE);
		nvCat26.setType(SINGLE_VALUE);
		nvCat26.setName(TRANSACTION_CATEGORY_ID);	
		nvCat26.setValues(new List(new Object[]{"110"}));  

		/*<categoryId>45</categoryId>
		 <categoryName>Office Supplies</categoryName>*/
		NVPair nvCat27 = new NVPair();
		nvCat27.setStatus(SHAREABLE);
		nvCat27.setType(SINGLE_VALUE);
		nvCat27.setName(TRANSACTION_CATEGORY_ID);	
		nvCat27.setValues(new List(new Object[]{"45"}));  

		/*<categoryId>16</categoryId>
		 <categoryName>Online Services</categoryName>*/
		NVPair nvCat28 = new NVPair();
		nvCat28.setStatus(SHAREABLE);
		nvCat28.setType(SINGLE_VALUE);
		nvCat28.setName(TRANSACTION_CATEGORY_ID);	
		nvCat28.setValues(new List(new Object[]{"16"}));  

		/*<categoryId>35</categoryId>
		 <categoryName>Other Bills</categoryName>*/
		NVPair nvCat29 = new NVPair();
		nvCat29.setStatus(SHAREABLE);
		nvCat29.setType(SINGLE_VALUE);
		nvCat29.setName(TRANSACTION_CATEGORY_ID);	
		nvCat29.setValues(new List(new Object[]{"35"}));  
		
		/*<categoryId>19</categoryId>
		 <categoryName>Other Expenses</categoryName>*/
		NVPair nvCat30 = new NVPair();
		nvCat30.setStatus(SHAREABLE);
		nvCat30.setType(SINGLE_VALUE);
		nvCat30.setName(TRANSACTION_CATEGORY_ID);	
		nvCat30.setValues(new List(new Object[]{"19"}));  
		
		/*<categoryId>20</categoryId>
		 <categoryName>Personal Care</categoryName>*/
		NVPair nvCat31 = new NVPair();
		nvCat31.setStatus(SHAREABLE);
		nvCat31.setType(SINGLE_VALUE);
		nvCat31.setName(TRANSACTION_CATEGORY_ID);	
		nvCat31.setValues(new List(new Object[]{"20"}));  

		/*<categoryId>42</categoryId>
		 <categoryName>Pets/Pet Care</categoryName>*/
		NVPair nvCat32 = new NVPair();
		nvCat32.setStatus(SHAREABLE);
		nvCat32.setType(SINGLE_VALUE);
		nvCat32.setName(TRANSACTION_CATEGORY_ID);	
		nvCat32.setValues(new List(new Object[]{"42"}));  

		/*<categoryId>104</categoryId>
		 <categoryName>Postage and Shipping</categoryName>*/
		NVPair nvCat33 = new NVPair();
		nvCat33.setStatus(SHAREABLE);
		nvCat33.setType(SINGLE_VALUE);
		nvCat33.setName(TRANSACTION_CATEGORY_ID);	
		nvCat33.setValues(new List(new Object[]{"104"}));  

		/*<categoryId>106</categoryId>
		 <categoryName>Printing</categoryName>*/
		NVPair nvCat34 = new NVPair();
		nvCat34.setStatus(SHAREABLE);
		nvCat34.setType(SINGLE_VALUE);
		nvCat34.setName(TRANSACTION_CATEGORY_ID);	
		nvCat34.setValues(new List(new Object[]{"106"}));  

		/*<categoryId>21</categoryId>
		 <categoryName>Rent</categoryName>*/
		NVPair nvCat35 = new NVPair();
		nvCat35.setStatus(SHAREABLE);
		nvCat35.setType(SINGLE_VALUE);
		nvCat35.setName(TRANSACTION_CATEGORY_ID);	
		nvCat35.setValues(new List(new Object[]{"21"}));  

		/*<categoryId>22</categoryId>
		 <categoryName>Restaurants/Dining</categoryName>*/
		NVPair nvCat36 = new NVPair();
		nvCat36.setStatus(SHAREABLE);
		nvCat36.setType(SINGLE_VALUE);
		nvCat36.setName(TRANSACTION_CATEGORY_ID);	
		nvCat36.setValues(new List(new Object[]{"22"}));  

		/*<categoryId>24</categoryId>
		 <categoryName>Service Charges/Fees</categoryName>*/
		NVPair nvCat37 = new NVPair();
		nvCat37.setStatus(SHAREABLE);
		nvCat37.setType(SINGLE_VALUE);
		nvCat37.setName(TRANSACTION_CATEGORY_ID);	
		nvCat37.setValues(new List(new Object[]{"24"}));  

		/*<categoryId>37</categoryId>
		 <categoryName>Taxes</categoryName>*/
		NVPair nvCat38 = new NVPair();
		nvCat38.setStatus(SHAREABLE);
		nvCat38.setType(SINGLE_VALUE);
		nvCat38.setName(TRANSACTION_CATEGORY_ID);	
		nvCat38.setValues(new List(new Object[]{"37"}));  

		/*<categoryId>38</categoryId>
		 <categoryName>Telephone Services</categoryName>*/
		NVPair nvCat39 = new NVPair();
		nvCat39.setStatus(SHAREABLE);
		nvCat39.setType(SINGLE_VALUE);
		nvCat39.setName(TRANSACTION_CATEGORY_ID);	
		nvCat39.setValues(new List(new Object[]{"38"}));  

		/*<categoryId>23</categoryId>
		 <categoryName>Travel</categoryName>*/
		NVPair nvCat40 = new NVPair();
		nvCat40.setStatus(SHAREABLE);
		nvCat40.setType(SINGLE_VALUE);
		nvCat40.setName(TRANSACTION_CATEGORY_ID);	
		nvCat40.setValues(new List(new Object[]{"23"}));  

		/*<categoryId>39</categoryId>
		 <categoryName>Utilities</categoryName>*/
		NVPair nvCat41 = new NVPair();
		nvCat41.setStatus(SHAREABLE);
		nvCat41.setType(SINGLE_VALUE);
		nvCat41.setName(TRANSACTION_CATEGORY_ID);	
		nvCat41.setValues(new List(new Object[]{"39"}));  

		/*<categoryId>112</categoryId>
		 <categoryName>Wages Paid</categoryName>*/
		NVPair nvCat42 = new NVPair();
		nvCat42.setStatus(SHAREABLE);
		nvCat42.setType(SINGLE_VALUE);
		nvCat42.setName(TRANSACTION_CATEGORY_ID);	
		nvCat42.setValues(new List(new Object[]{"112"}));  



		ArrayList<BatchCategorySpentDataPointRequest> batchRequests = new ArrayList();
		
		// 1.
		ArrayList<NVPair> nvPair1 = new ArrayList();
        nvPair1.add(0, nvCat1);
        nvPair1.add(1, nvStartDate);
        nvPair1.add(2, nvEndDate);
 
        NVPair[] nvp1 = new NVPair[nvPair1.size()];
        ArrayOfNVPair paramList1 = new ArrayOfNVPair();
		paramList1.setElements(nvPair1.toArray(nvp1));
		
		BatchCategorySpentDataPointRequest req1 = new BatchCategorySpentDataPointRequest();
		req1.setDataPointTypeId(dataPointTypeId);
		req1.setParams(paramList1);  // add NVPairs

		batchRequests.add(0, req1);
	
		
		// 2.
		NVPair[] nvp2 = {nvCat2, nvStartDate, nvEndDate};
        ArrayOfNVPair paramList2 = new ArrayOfNVPair();
        paramList2.setElements(nvp2);
		
		BatchCategorySpentDataPointRequest req2 = new BatchCategorySpentDataPointRequest();
		req2.setDataPointTypeId(dataPointTypeId);
		req2.setParams(paramList2);  // add NVPairs

		batchRequests.add(1, req2);

		
		// 3.
		NVPair[] nvp3 = {nvCat3, nvStartDate, nvEndDate};
        ArrayOfNVPair paramList3 = new ArrayOfNVPair();
        paramList3.setElements(nvp3);
		
		BatchCategorySpentDataPointRequest req3 = new BatchCategorySpentDataPointRequest();
		req3.setDataPointTypeId(dataPointTypeId);
		req3.setParams(paramList3);  // add NVPairs

		batchRequests.add(2, req3);

		
		// 4.
		NVPair[] nvp4 = {nvCat4, nvStartDate, nvEndDate};
        ArrayOfNVPair paramList4 = new ArrayOfNVPair();
        paramList4.setElements(nvp4);
		
		BatchCategorySpentDataPointRequest req4 = new BatchCategorySpentDataPointRequest();
		req4.setDataPointTypeId(dataPointTypeId);
		req4.setParams(paramList4);  // add NVPairs

		batchRequests.add(3, req4);

		
		// 5.
		NVPair[] nvp5 = {nvCat5, nvStartDate, nvEndDate};
        ArrayOfNVPair paramList5 = new ArrayOfNVPair();
        paramList5.setElements(nvp5);
	
		BatchCategorySpentDataPointRequest req5 = new BatchCategorySpentDataPointRequest();
		req5.setDataPointTypeId(dataPointTypeId);
		req5.setParams(paramList5);  // add NVPairs

		batchRequests.add(4, req5);

		
		// 6.
		NVPair[] nvp6 = {nvCat6, nvStartDate, nvEndDate};
        ArrayOfNVPair paramList6 = new ArrayOfNVPair();
        paramList6.setElements(nvp6);
		
		BatchCategorySpentDataPointRequest req6 = new BatchCategorySpentDataPointRequest();
		req6.setDataPointTypeId(dataPointTypeId);
		req6.setParams(paramList6);  // add NVPairs

		batchRequests.add(5, req6);

		
		// 7.
		NVPair[] nvp7 = {nvCat7, nvStartDate, nvEndDate};
        ArrayOfNVPair paramList7 = new ArrayOfNVPair();
        paramList7.setElements(nvp7);
		
		BatchCategorySpentDataPointRequest req7 = new BatchCategorySpentDataPointRequest();
		req7.setDataPointTypeId(dataPointTypeId);
		req7.setParams(paramList7);  // add NVPairs

		batchRequests.add(6, req7);

		
		// 8.
		NVPair[] nvp8 = {nvCat8, nvStartDate, nvEndDate};
        ArrayOfNVPair paramList8 = new ArrayOfNVPair();
        paramList8.setElements(nvp8);
		
		BatchCategorySpentDataPointRequest req8 = new BatchCategorySpentDataPointRequest();
		req8.setDataPointTypeId(dataPointTypeId);
		req8.setParams(paramList8);  // add NVPairs

		batchRequests.add(7, req8);

		
		// 9.
		NVPair[] nvp9 = {nvCat9, nvStartDate, nvEndDate};
        ArrayOfNVPair paramList9 = new ArrayOfNVPair();
        paramList9.setElements(nvp9);
	
		BatchCategorySpentDataPointRequest req9 = new BatchCategorySpentDataPointRequest();
		req9.setDataPointTypeId(dataPointTypeId);
		req9.setParams(paramList9);  // add NVPairs

		batchRequests.add(8, req9);

		
		// 10.
		NVPair[] nvp10 = {nvCat10, nvStartDate, nvEndDate};
        ArrayOfNVPair paramList10 = new ArrayOfNVPair();
        paramList10.setElements(nvp10);
		
		BatchCategorySpentDataPointRequest req10 = new BatchCategorySpentDataPointRequest();
		req10.setDataPointTypeId(dataPointTypeId);
		req10.setParams(paramList10);  // add NVPairs

		batchRequests.add(9, req10);

		
		// 11.
		NVPair[] nvp11 = {nvCat11, nvStartDate, nvEndDate};
        ArrayOfNVPair paramList11 = new ArrayOfNVPair();
        paramList11.setElements(nvp11);
		
		BatchCategorySpentDataPointRequest req11 = new BatchCategorySpentDataPointRequest();
		req11.setDataPointTypeId(dataPointTypeId);
		req11.setParams(paramList11);  // add NVPairs

		batchRequests.add(10, req11);

		
		// 12.
		NVPair[] nvp12 = {nvCat12, nvStartDate, nvEndDate};
        ArrayOfNVPair paramList12 = new ArrayOfNVPair();
        paramList12.setElements(nvp12);
		
		BatchCategorySpentDataPointRequest req12 = new BatchCategorySpentDataPointRequest();
		req12.setDataPointTypeId(dataPointTypeId);
		req12.setParams(paramList12);  // add NVPairs

		batchRequests.add(11, req12);

		
		// 13.
		NVPair[] nvp13 = {nvCat13, nvStartDate, nvEndDate};
        ArrayOfNVPair paramList13 = new ArrayOfNVPair();
        paramList13.setElements(nvp13);
		
		BatchCategorySpentDataPointRequest req13 = new BatchCategorySpentDataPointRequest();
		req13.setDataPointTypeId(dataPointTypeId);
		req13.setParams(paramList13);  // add NVPairs

		batchRequests.add(12, req13);

		
		// 14.
		NVPair[] nvp14 = {nvCat14, nvStartDate, nvEndDate};
        ArrayOfNVPair paramList14 = new ArrayOfNVPair();
        paramList14.setElements(nvp14);
		
		BatchCategorySpentDataPointRequest req14 = new BatchCategorySpentDataPointRequest();
		req14.setDataPointTypeId(dataPointTypeId);
		req14.setParams(paramList14);  // add NVPairs

		batchRequests.add(13, req14);

		
		// 15.
		NVPair[] nvp15 = {nvCat15, nvStartDate, nvEndDate};
        ArrayOfNVPair paramList15 = new ArrayOfNVPair();
        paramList15.setElements(nvp15);
		
		BatchCategorySpentDataPointRequest req15 = new BatchCategorySpentDataPointRequest();
		req15.setDataPointTypeId(dataPointTypeId);
		req15.setParams(paramList15);  // add NVPairs

		batchRequests.add(14, req15);

		
		// 16.
		NVPair[] nvp16 = {nvCat16, nvStartDate, nvEndDate};
        ArrayOfNVPair paramList16 = new ArrayOfNVPair();
        paramList16.setElements(nvp16);
		
		BatchCategorySpentDataPointRequest req16 = new BatchCategorySpentDataPointRequest();
		req16.setDataPointTypeId(dataPointTypeId);
		req16.setParams(paramList16);  // add NVPairs

		batchRequests.add(15, req16);

		
		// 17.
		NVPair[] nvp17 = {nvCat17, nvStartDate, nvEndDate};
        ArrayOfNVPair paramList17 = new ArrayOfNVPair();
        paramList17.setElements(nvp17);
		
		BatchCategorySpentDataPointRequest req17 = new BatchCategorySpentDataPointRequest();
		req17.setDataPointTypeId(dataPointTypeId);
		req17.setParams(paramList17);  // add NVPairs

		batchRequests.add(16, req17);

		
		// 18.
		NVPair[] nvp18 = {nvCat18, nvStartDate, nvEndDate};
        ArrayOfNVPair paramList18 = new ArrayOfNVPair();
        paramList18.setElements(nvp18);
		
		BatchCategorySpentDataPointRequest req18 = new BatchCategorySpentDataPointRequest();
		req18.setDataPointTypeId(dataPointTypeId);
		req18.setParams(paramList18);  // add NVPairs

		batchRequests.add(17, req18);

		
		// 19.
		NVPair[] nvp19 = {nvCat19, nvStartDate, nvEndDate};
        ArrayOfNVPair paramList19 = new ArrayOfNVPair();
        paramList19.setElements(nvp19);
		
		BatchCategorySpentDataPointRequest req19 = new BatchCategorySpentDataPointRequest();
		req19.setDataPointTypeId(dataPointTypeId);
		req19.setParams(paramList19);  // add NVPairs

		batchRequests.add(18, req19);

		
		// 20.
		NVPair[] nvp20 = {nvCat20, nvStartDate, nvEndDate};
        ArrayOfNVPair paramList20 = new ArrayOfNVPair();
        paramList20.setElements(nvp20);
		
		BatchCategorySpentDataPointRequest req20 = new BatchCategorySpentDataPointRequest();
		req20.setDataPointTypeId(dataPointTypeId);
		req20.setParams(paramList20);  // add NVPairs

		batchRequests.add(19, req20);

		
		// 21.
		NVPair[] nvp21 = {nvCat21, nvStartDate, nvEndDate};
        ArrayOfNVPair paramList21 = new ArrayOfNVPair();
        paramList21.setElements(nvp21);
		
		BatchCategorySpentDataPointRequest req21 = new BatchCategorySpentDataPointRequest();
		req21.setDataPointTypeId(dataPointTypeId);
		req21.setParams(paramList21);  // add NVPairs

		batchRequests.add(20, req21);		

		
		// 22.
		NVPair[] nvp22 = {nvCat22, nvStartDate, nvEndDate};
        ArrayOfNVPair paramList22 = new ArrayOfNVPair();
        paramList22.setElements(nvp22);
		
		BatchCategorySpentDataPointRequest req22 = new BatchCategorySpentDataPointRequest();
		req22.setDataPointTypeId(dataPointTypeId);
		req22.setParams(paramList22);  // add NVPairs

		batchRequests.add(21, req22);	
	
		
		// 23.
		NVPair[] nvp23 = {nvCat23, nvStartDate, nvEndDate};
        ArrayOfNVPair paramList23 = new ArrayOfNVPair();
        paramList23.setElements(nvp23);
		
		BatchCategorySpentDataPointRequest req23 = new BatchCategorySpentDataPointRequest();
		req23.setDataPointTypeId(dataPointTypeId);
		req23.setParams(paramList23);  // add NVPairs

		batchRequests.add(22, req23);	
	
		
		// 24.
		NVPair[] nvp24 = {nvCat24, nvStartDate, nvEndDate};
        ArrayOfNVPair paramList24 = new ArrayOfNVPair();
        paramList24.setElements(nvp24);
		
		BatchCategorySpentDataPointRequest req24 = new BatchCategorySpentDataPointRequest();
		req24.setDataPointTypeId(dataPointTypeId);
		req24.setParams(paramList24);  // add NVPairs

		batchRequests.add(23, req24);	

		
		// 25.
		NVPair[] nvp25 = {nvCat25, nvStartDate, nvEndDate};
        ArrayOfNVPair paramList25 = new ArrayOfNVPair();
        paramList25.setElements(nvp25);
		
		BatchCategorySpentDataPointRequest req25 = new BatchCategorySpentDataPointRequest();
		req25.setDataPointTypeId(dataPointTypeId);
		req25.setParams(paramList25);  // add NVPairs

		batchRequests.add(24, req25);	

		
		// 26.
		NVPair[] nvp26 = {nvCat26, nvStartDate, nvEndDate};
        ArrayOfNVPair paramList26 = new ArrayOfNVPair();
        paramList26.setElements(nvp26);
		
		BatchCategorySpentDataPointRequest req26 = new BatchCategorySpentDataPointRequest();
		req26.setDataPointTypeId(dataPointTypeId);
		req26.setParams(paramList26);  // add NVPairs

		batchRequests.add(25, req26);	
	
		
		// 27.
		NVPair[] nvp27 = {nvCat27, nvStartDate, nvEndDate};
        ArrayOfNVPair paramList27 = new ArrayOfNVPair();
        paramList27.setElements(nvp27);

		BatchCategorySpentDataPointRequest req27 = new BatchCategorySpentDataPointRequest();
		req27.setDataPointTypeId(dataPointTypeId);
		req27.setParams(paramList27);  // add NVPairs

		batchRequests.add(26, req27);			
	
		
		// 28.
		NVPair[] nvp28 = {nvCat28, nvStartDate, nvEndDate};
        ArrayOfNVPair paramList28 = new ArrayOfNVPair();
        paramList28.setElements(nvp28);
	
		BatchCategorySpentDataPointRequest req28 = new BatchCategorySpentDataPointRequest();
		req28.setDataPointTypeId(dataPointTypeId);
		req28.setParams(paramList28);  // add NVPairs

		batchRequests.add(27, req28);	

		
		// 29.
		NVPair[] nvp29 = {nvCat29, nvStartDate, nvEndDate};
        ArrayOfNVPair paramList29 = new ArrayOfNVPair();
        paramList29.setElements(nvp29);

		BatchCategorySpentDataPointRequest req29 = new BatchCategorySpentDataPointRequest();
		req29.setDataPointTypeId(dataPointTypeId);
		req29.setParams(paramList29);  // add NVPairs

		batchRequests.add(28, req29);		
		
		
		// 30.
		NVPair[] nvp30 = {nvCat30, nvStartDate, nvEndDate};
        ArrayOfNVPair paramList30 = new ArrayOfNVPair();
        paramList30.setElements(nvp30);

		BatchCategorySpentDataPointRequest req30 = new BatchCategorySpentDataPointRequest();
		req30.setDataPointTypeId(dataPointTypeId);
		req30.setParams(paramList30);  // add NVPairs

		batchRequests.add(29, req30);		
		
		
		// 31.
		NVPair[] nvp31 = {nvCat31, nvStartDate, nvEndDate};
        ArrayOfNVPair paramList31 = new ArrayOfNVPair();
        paramList31.setElements(nvp31);

		BatchCategorySpentDataPointRequest req31 = new BatchCategorySpentDataPointRequest();
		req31.setDataPointTypeId(dataPointTypeId);
		req31.setParams(paramList31);  // add NVPairs

		batchRequests.add(30, req31);	
	
		
		// 32.
		NVPair[] nvp32 = {nvCat32, nvStartDate, nvEndDate};
        ArrayOfNVPair paramList32 = new ArrayOfNVPair();
        paramList32.setElements(nvp32);

		BatchCategorySpentDataPointRequest req32 = new BatchCategorySpentDataPointRequest();
		req32.setDataPointTypeId(dataPointTypeId);
		req32.setParams(paramList32);  // add NVPairs

		batchRequests.add(31, req32);	
		
		
		// 33.
		NVPair[] nvp33 = {nvCat33, nvStartDate, nvEndDate};
        ArrayOfNVPair paramList33 = new ArrayOfNVPair();
        paramList33.setElements(nvp33);

		BatchCategorySpentDataPointRequest req33 = new BatchCategorySpentDataPointRequest();
		req33.setDataPointTypeId(dataPointTypeId);
		req33.setParams(paramList33);  // add NVPairs

		batchRequests.add(32, req33);	
		
		
		// 34.
		NVPair[] nvp34 = {nvCat34, nvStartDate, nvEndDate};
        ArrayOfNVPair paramList34 = new ArrayOfNVPair();
        paramList34.setElements(nvp34);

		BatchCategorySpentDataPointRequest req34 = new BatchCategorySpentDataPointRequest();
		req34.setDataPointTypeId(dataPointTypeId);
		req34.setParams(paramList34);  // add NVPairs

		batchRequests.add(33, req34);		
		
		
		// 35.
		NVPair[] nvp35 = {nvCat35, nvStartDate, nvEndDate};
        ArrayOfNVPair paramList35 = new ArrayOfNVPair();
        paramList35.setElements(nvp35);

		BatchCategorySpentDataPointRequest req35 = new BatchCategorySpentDataPointRequest();
		req35.setDataPointTypeId(dataPointTypeId);
		req35.setParams(paramList35);  // add NVPairs

		batchRequests.add(34, req35);	
		
		
		// 36.
		NVPair[] nvp36 = {nvCat36, nvStartDate, nvEndDate};
        ArrayOfNVPair paramList36 = new ArrayOfNVPair();
        paramList36.setElements(nvp36);

		BatchCategorySpentDataPointRequest req36 = new BatchCategorySpentDataPointRequest();
		req36.setDataPointTypeId(dataPointTypeId);
		req36.setParams(paramList36);  // add NVPairs

		batchRequests.add(35, req36);	
		
		
		// 37.
		NVPair[] nvp37 = {nvCat37, nvStartDate, nvEndDate};
        ArrayOfNVPair paramList37 = new ArrayOfNVPair();
        paramList37.setElements(nvp37);

		BatchCategorySpentDataPointRequest req37 = new BatchCategorySpentDataPointRequest();
		req37.setDataPointTypeId(dataPointTypeId);
		req37.setParams(paramList37);  // add NVPairs

		batchRequests.add(36, req37);	
		
		
		// 38.
		NVPair[] nvp38 = {nvCat38, nvStartDate, nvEndDate};
        ArrayOfNVPair paramList38 = new ArrayOfNVPair();
        paramList38.setElements(nvp38);

		BatchCategorySpentDataPointRequest req38 = new BatchCategorySpentDataPointRequest();
		req38.setDataPointTypeId(dataPointTypeId);
		req38.setParams(paramList38);  // add NVPairs

		batchRequests.add(37, req38);	
		
		
		// 39.
		NVPair[] nvp39 = {nvCat39, nvStartDate, nvEndDate};
        ArrayOfNVPair paramList39 = new ArrayOfNVPair();
        paramList39.setElements(nvp39);

		BatchCategorySpentDataPointRequest req39 = new BatchCategorySpentDataPointRequest();
		req39.setDataPointTypeId(dataPointTypeId);
		req39.setParams(paramList39);  // add NVPairs

		batchRequests.add(38, req39);	
	
		
		// 40.
		NVPair[] nvp40 = {nvCat40, nvStartDate, nvEndDate};
        ArrayOfNVPair paramList40 = new ArrayOfNVPair();
        paramList40.setElements(nvp40);

		BatchCategorySpentDataPointRequest req40 = new BatchCategorySpentDataPointRequest();
		req40.setDataPointTypeId(dataPointTypeId);
		req40.setParams(paramList40);  // add NVPairs

		batchRequests.add(39, req40);	
		
		
		// 41.
		NVPair[] nvp41 = {nvCat41, nvStartDate, nvEndDate};
        ArrayOfNVPair paramList41 = new ArrayOfNVPair();
        paramList41.setElements(nvp41);

		BatchCategorySpentDataPointRequest req41 = new BatchCategorySpentDataPointRequest();
		req41.setDataPointTypeId(dataPointTypeId);
		req41.setParams(paramList41);  // add NVPairs

		batchRequests.add(40, req41);
		
		
		// 42.
		NVPair[] nvp42 = {nvCat42, nvStartDate, nvEndDate};
        ArrayOfNVPair paramList42 = new ArrayOfNVPair();
        paramList42.setElements(nvp42);
        
		BatchCategorySpentDataPointRequest req42 = new BatchCategorySpentDataPointRequest();
		req42.setDataPointTypeId(dataPointTypeId);
		req42.setParams(paramList42);  // add NVPairs

		batchRequests.add(41, req42);		
		
		
		ArrayOfBatchCategorySpentDataPointRequest arrayOfBatchRequests = new ArrayOfBatchCategorySpentDataPointRequest();
		BatchCategorySpentDataPointRequest[] batchList = new BatchCategorySpentDataPointRequest[batchRequests.size()];
		arrayOfBatchRequests.setElements(batchRequests.toArray(batchList));
		
		ArrayOfBatchCategorySpentDataPointResponse arrayOfBatchCategorySpentDataPointResponse = new ArrayOfBatchCategorySpentDataPointResponse();
		try {
			arrayOfBatchCategorySpentDataPointResponse = categorySpentDataPointService.getCategorySpentDataPoint1(userContext, arrayOfBatchRequests);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Error fetching CategorySpentDataPoint..." + e.getMessage());
		}
		
        BatchCategorySpentDataPointResponse[] dataPointResponse = null;
        if (arrayOfBatchCategorySpentDataPointResponse != null){
        	dataPointResponse = arrayOfBatchCategorySpentDataPointResponse.getElements();
        }
        
        for (int i = 0; i < dataPointResponse.length; i++) {
            if (dataPointResponse[i].getDataPoint() != null) {
            	
            	ArrayOfNVPair arrayOfNVPairInResponse = new ArrayOfNVPair();
            	NVPair[] nvPairInResponse = null;
            	List transactionCategory = null;
            	String category = null;
            	
            	arrayOfNVPairInResponse = dataPointResponse[i].getParams();
            	nvPairInResponse = arrayOfNVPairInResponse.getElements();
            
            	if (nvPairInResponse[0] != null){
            		transactionCategory = nvPairInResponse[0].getValues();
            		category = (String) transactionCategory.getElements(0);
            	}

    			
            	MultiDataPoints multiDataPoints = (MultiDataPoints) dataPointResponse[i].getDataPoint();
            	
            	ArrayOfMonthlyDataPoint arrayOfMonthlyDataPoints = new ArrayOfMonthlyDataPoint();

            	arrayOfMonthlyDataPoints = multiDataPoints.getMonthlyDataPoints();
            	MonthlyDataPoint[] monthlyDataPoints = null;
            	monthlyDataPoints = arrayOfMonthlyDataPoints.getElements();
            	
            	if (monthlyDataPoints != null) {
        			System.out.println("\ncategory = " + category);
	            	for (int j = 0; j < monthlyDataPoints.length; j++) {
	            		if (monthlyDataPoints[j].getTotalAmount() != null){

	            			System.out.println("month" + monthlyDataPoints[j].getMonthNum() + " : " + monthlyDataPoints[j].getTotalAmount());
	            		}
	            	}
            	}

            }
        }
        
        
	}
	
	
	
	/**
	 * Allows the user to add a manual property value.
	 * 
	 * @param userContext context for the user to add
	 */
	public void getDataPointType(UserContext userContext) {

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
				System.out.println("\nCategory Spent Data Point Menu");
				System.out.println(NAV_LIST_DATA_POINT_TYPES
						+ ". List Data Point Types");
				System.out.println(NAV_GET_DATA_POINT
						+ ". Get Data Point");		
				System.out.println(NAV_GET_DATA_POINTS
						+ ". Get Data Points - Batch");								
				System.out.println(NAV_GET_DATA_POINT_TYPE
						+ ". Get Data Point Type");				
				System.out.println("" + NAV_QUIT + ". Exit");
				System.out.println("\n");
				System.out.print("Enter Choice : ");
				choice = IOUtils.readInt();

				if(NAV_LIST_DATA_POINT_TYPES == choice) {
					listDataPointTypes(userContext);
				} else if(NAV_GET_DATA_POINT == choice) {
					getDataPoint(userContext);
				} else if(NAV_GET_DATA_POINTS == choice) {
					getDataPoints(userContext);
					
				} else if (NAV_GET_DATA_POINT_TYPE == choice) {
					getDataPointType(userContext);
				} else if (NAV_QUIT == choice) {
				    loop = false;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}
	
	private String getDate(long dateInEpoch) {
		//TODO: What about user's timezone?
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		cal.setTimeInMillis(dateInEpoch*1000l);			
		SimpleDateFormat dateFormat = new SimpleDateFormat ("dd-MMM-yyyy");
		return dateFormat.format(cal.getTime());
	}
    /**
     * Calculate the start date for fetching networth datapoints based on
     * the number of intervals and the datapoint type.
     *
     * @param endDate
     * @param numIntervals
     * @param dataPointType - MONTLY, WEEKLY, DAILY
     * @return
     */

        private static Calendar getStartDate(Calendar endDate, int numIntervals,DataPointType dataPointType) {

    	Calendar startDate = null;

        if (numIntervals >= 0) {

            Calendar calendar = endDate; //CalendarUtil.createCalendar(endDate.getTime());
            //System.out.println("calendar1=" + calendar.getTime() );
            if (DataPointType.WEEKLY.getValue().equals(dataPointType.getValue())) {

                CalendarUtil.setTimeAsFirstDayOfWeek(calendar);
                for (int i = 0; i < numIntervals; i++){
                    CalendarUtil.decrementOneWeek(calendar);
                }
                startDate = calendar;

            } else if (DataPointType.MONTHLY.getValue().equals(dataPointType.getValue())) {

                CalendarUtil.setTimeAsFirstDayOfMonth(calendar);
                //System.out.println("calendar2=" + calendar.getTime() );
                for (int i = 0; i < numIntervals; i++){
                    //System.out.println("decrement...");
                    CalendarUtil.decrementOneMonth(calendar);
                }
                //System.out.println("calendar3=" + calendar.getTime() );
                startDate = calendar;

            } else if (DataPointType.DAILY.getValue().equals(dataPointType.getValue())) {

                for (int i = 0; i < numIntervals; i++){
                    CalendarUtil.decrementOneDay(calendar);
                }
                startDate = calendar;
            }
        }

        return startDate;
    }

        private static Calendar getEndDate() {
        	Calendar calendar = Calendar.getInstance();
        	

        	   	
        	calendar.set(2011, 1, 31);

        	return calendar;
        	
        }
}