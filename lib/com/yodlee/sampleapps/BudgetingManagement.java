/*
 * Copyright 2009 Yodlee, Inc.  All Rights Reserved.  Your use of this code 
 * requires a license from Yodlee.  Any such license to this code is 
 * restricted to evaluation/illustrative purposes only. It is not intended 
 * for use in a production environment, and Yodlee disclaims all warranties 
 * and/or support obligations concerning this code, regardless of the terms 
 * of any other agreements between Yodlee and you."
 */
package com.yodlee.sampleapps;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.xml.rpc.ServiceException;

import com.yodlee.soap.core.IllegalArgumentValueExceptionFault;
import com.yodlee.soap.core.permissioning.InvalidRoleExceptionFault;
import com.yodlee.soap.core.transactioncategorization.Category;
import com.yodlee.soap.core.transactioncategorization.TransactionCategoryTypes;
import com.yodlee.soap.core.transactioncategorization.transactioncategorizationservice.TransactionCategorizationService;
import com.yodlee.soap.core.transactioncategorization.transactioncategorizationservice.TransactionCategorizationServiceServiceLocator;
import com.yodlee.soap.collections.core.alert.ArrayOfAlertSubscription;
import com.yodlee.soap.collections.core.alert.ArrayOfAlertTypeAvailability;
import com.yodlee.soap.collections.core.budgetgoal.ArrayOfBudgetGoalCategoryData;
import com.yodlee.soap.collections.core.budgetgoal.ArrayOfBudgetGoalData;
import com.yodlee.soap.collections.core.budgetgoal.ArrayOfBudgetSummaryData;
import com.yodlee.soap.collections.core.transactioncategorization.ArrayOfCategory;
import com.yodlee.soap.common.CalendarDate;
import com.yodlee.soap.common.UserContext;
import com.yodlee.sampleapps.helper.CalendarUtil;
import com.yodlee.sampleapps.helper.IOUtils;
import com.yodlee.soap.appext.base.datapoint.transactiondataservice.TransactionDataService;
import com.yodlee.soap.appext.base.datapoint.transactiondataservice.TransactionDataServiceServiceLocator;
import com.yodlee.soap.appext.base.datapoint.vo.CurrencyCriteria;
import com.yodlee.soap.appext.base.datapoint.vo.DataPointType;
import com.yodlee.soap.appext.base.datapoint.vo.TransactionCategoryData;
import com.yodlee.soap.appext.base.datapoint.vo.TransactionCriteria;
import com.yodlee.soap.appext.base.datapoint.vo.TransactionSummaryDataPoint;
import com.yodlee.soap.core.alert.AlertSchedule;
import com.yodlee.soap.core.alert.AlertSettings;
import com.yodlee.soap.core.alert.AlertSubscription;
import com.yodlee.soap.core.alert.AlertType;
import com.yodlee.soap.core.alert.AlertTypeAlreadySubscribedExceptionFault;
import com.yodlee.soap.core.alert.AlertTypeAvailability;
import com.yodlee.soap.core.alert.AlertTypeNotAvailableExceptionFault;
import com.yodlee.soap.core.alert.AlertTypeNotAvailableForHeldAccountExceptionFault;
import com.yodlee.soap.core.alert.AlertTypeNotAvailableForHeldAwayAccountExceptionFault;
import com.yodlee.soap.core.alert.AlertTypeNotAvailableForItemAccountExceptionFault;
import com.yodlee.soap.core.alert.AlertTypeNotAvailableForItemExceptionFault;
import com.yodlee.soap.core.alert.BudgetThresholdAlertSettings;
import com.yodlee.soap.core.alert.IncorrectAlertSettingsExceptionFault;
import com.yodlee.soap.core.alert.MonthlyAlertSchedule;
import com.yodlee.soap.core.alert.ScheduledAlertSettings;
import com.yodlee.soap.core.alert.UserAlertSubscription;
import com.yodlee.soap.core.alert.alertsubscriptionmanagement.AlertSubscriptionManagement;
import com.yodlee.soap.core.alert.alertsubscriptionmanagement.AlertSubscriptionManagementServiceLocator;
import com.yodlee.soap.core.budgetgoal.BudgetGoalCategoryData;
import com.yodlee.soap.core.budgetgoal.BudgetGoalData;
import com.yodlee.soap.core.budgetgoal.BudgetGoalObject;
import com.yodlee.soap.core.budgetgoal.BudgetSummaryData;
import com.yodlee.soap.core.budgetgoal.budgetgoalmanagement.BudgetGoalManagement;
import com.yodlee.soap.core.budgetgoal.budgetgoalmanagement.BudgetGoalManagementServiceLocator;
import com.yodlee.soap.core.dataservice.YMoney;

/**
 * This classes demonstrates the use of the various investment features
 * in the Yodlee system.
 */
public class BudgetingManagement extends ApplicationSuper {

private final int NUM_OF_MONTH_FOR_AVG_CALCULATION = 6;
	
	private final String NOT_AVAILABLE = "No transaction category data available";
	
	public final int BUDGET_THRESHOLDS = 3;
	
	private final String ENABLED = "1";
	
	private final int CREDIT_TRANSACTION_TYPE_ID = 1;
	
	private final int DEBIT_TRANSACTION_TYPE_ID = 2;
	
	private final String DEFAULT_CURRENCY = "USD";
	
	private final String TOTALSPENDING = "expense";
	
	private final String TOTALINCOME = "income";
	
	private final String TOTALTRANSFER = "transfer";	
	
	private String budgetThresholdAlertEnabled = null;
	
	/**
	 * Represents the category level Category type or Category Group
	 * Income, Expense etc..
	 */
	public static final Long CATEGORY_TYPE = new Long(1);
	
	/**
	 * Represents the category level user defined Super Category
	 */
	public static final Long SUPER_CATEGORY = new Long(2);
	
	/**
	 * Represents the category level transaction category
	 */
	public static final Long CATEGORY = new Long(3);
	
	/**
	 * Represents the category level user created sub category
	 */
	public static final Long SUB_CATEGORY = new Long(4);
	
	private static final Long TransactionCategoryTypes_INCOME = new Long(2);
	private static final Long TransactionCategoryTypes_EXPENSE = new Long(3);
	private static final Long TransactionCategoryTypes_TRANSFER = new Long(4);
	private static final Long TransactionCategoryTypes_DEFERREDCOMPENSATION = new Long(5);
	
	private TransactionDataService transactionDataService;
	
	private BudgetGoalManagement budgetGoalManagement;
	
	private AlertSubscriptionManagement alertSubscriptionManagement;
	
	private TransactionCategorizationService transactionCategorizationService;
	
	private final String HEADING_CAT = "CATEGORY";
	private final String HEADING_STM = "SPENDING_THIS_MONTH";
	private final String HEADING_AVG = "AVERAGE";
	private final String HEADING_MBG = "MONTHLY_BUDGET_GOAL";
	
	private static final int NAV_QUIT = 0;
    private static int optionCount = 1;
    private static final int NAV_LIST_BUDGETGOALS_ALL = optionCount++;
    private static final int NAV_LIST_BUDGETGOALS_WITH_TXNS = optionCount++;
    private static final int NAV_SET_BUDGETGOALS = optionCount++;
    private static final int NAV_SET_BUDGETGOALS_FOR_ALL_CAT = optionCount++;
    private static final int NAV_SET_BUDGETALERTS = optionCount++;
	
	private List budgetGoalsList = null;
	
	Map totalsMap = null;
	
    Map budgetGoalDataMap = null;
	
	private static final boolean isLastColumnTrue = true;
	
	private static final boolean isLastColumnFalse = false;
	
	private static final String BLANK_STR = "";
	private static final String SPACE_1 = " ";
	private static final String SPACE_2 = "  ";
	
	private static int alettOptionCount = 1;
    private static final int NAV_ADD_BUDGET_THRESHOLD_ALERT = alettOptionCount++;
    private static final int NAV_UPDATE_BUDGET_THRESHOLD_ALERT = alettOptionCount++;
    private static final int NAV_REMOVE_BUDGET_THRESHOLD_ALERT = alettOptionCount++;
    
    private AlertSubscription[] alertSubscriptions = null;
    
	/**
     * Constructs an instance of the BudgetingManagement class that
     * provides the functionality to display all content.
     */
    public BudgetingManagement ()
    {
        super ();
        TransactionDataServiceServiceLocator locator1 = new TransactionDataServiceServiceLocator();
        String serviceName1 = locator1.getTransactionDataServiceWSDDServiceName();
        locator1.setTransactionDataServiceEndpointAddress(System.getProperty("com.yodlee.soap.services.url")
						+ "/" + serviceName1);
        try {
        	transactionDataService = locator1.getTransactionDataService();
		} catch (ServiceException se) {
			throw new RuntimeException(se);
		}
		BudgetGoalManagementServiceLocator locator2 = new BudgetGoalManagementServiceLocator();
        String serviceName2 = locator2.getBudgetGoalManagementServiceWSDDServiceName();
        locator2.setBudgetGoalManagementServiceEndpointAddress(System.getProperty("com.yodlee.soap.services.url")
						+ "/" + serviceName2);
        try {
        	budgetGoalManagement = locator2.getBudgetGoalManagementService();
		} catch (ServiceException se) {
			throw new RuntimeException(se);
		}
		AlertSubscriptionManagementServiceLocator locator3 = new AlertSubscriptionManagementServiceLocator();
        String serviceName3 = locator3.getAlertSubscriptionManagementServiceWSDDServiceName();
        locator3.setAlertSubscriptionManagementServiceEndpointAddress(System.getProperty("com.yodlee.soap.services.url")
						+ "/" + serviceName3);
        try {
        	alertSubscriptionManagement = locator3.getAlertSubscriptionManagementService();
		} catch (ServiceException se) {
			throw new RuntimeException(se);
		}
		
		TransactionCategorizationServiceServiceLocator locator4 = new TransactionCategorizationServiceServiceLocator();
        String serviceName4 = locator4.getTransactionCategorizationServiceWSDDServiceName();
        locator4.setTransactionCategorizationServiceEndpointAddress(System.getProperty("com.yodlee.soap.services.url")
						+ "/" + serviceName4);
        try {
        	transactionCategorizationService = locator4.getTransactionCategorizationService();
		} catch (ServiceException se) {
			throw new RuntimeException(se);
		}
    }
    
    public void doMenu(UserContext userContext) {
    	boolean loop = true;
		int choice = 0;
		while (loop) {
			try {
				System.out.println("\nBudgeting Menu");
				System.out.println(NAV_LIST_BUDGETGOALS_ALL
						+ ". Dispaly Budget Goals - Show All Categories");
				System.out.println(NAV_LIST_BUDGETGOALS_WITH_TXNS
						+ ". Dispaly Budget Goals - Hide Categories with No Transactions and No Budget Goals");				
				System.out.println(NAV_SET_BUDGETGOALS
						+ ". Set Budget Goals - Show All Categories");
				System.out.println(NAV_SET_BUDGETGOALS_FOR_ALL_CAT
						+ ". Set Budget Goals - Hide Categories with No Transactions and No Budget Goals");
				System.out.println(NAV_SET_BUDGETALERTS + ". Set Monthly Budget Alerts [sub menu]");
				System.out.println(BLANK_STR + NAV_QUIT + ". Exit");
				System.out.println("\n");
				System.out.print("Enter Choice : ");
				choice = IOUtils.readInt();

				if(NAV_LIST_BUDGETGOALS_ALL == choice)
					doBudgetGoals(userContext, false, true);
				else if(NAV_LIST_BUDGETGOALS_WITH_TXNS == choice)
					doBudgetGoals(userContext, false, false);
				else if(NAV_SET_BUDGETGOALS == choice)
					doBudgetGoals(userContext, true, true);
				else if(NAV_SET_BUDGETGOALS_FOR_ALL_CAT == choice)
					doBudgetGoals(userContext, true, false);
				else if(NAV_SET_BUDGETALERTS == choice)
					setMonthlyBudgetAlerts(userContext);
				else if (NAV_QUIT == choice)
				    loop = false;
				else 
					 System.out.println("Invalid Entry!");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
    }
    
    /**
	 * displays budget goals
	 * @param userContext
	 * @param isEditable
	 * @param showAll - If set to true displays budget goals for all categories
	 * 					If set to false, displays budget goals for only those categories
	 * 					which have associated transactions and budget goals
	 */
    private void doBudgetGoals(UserContext userContext, boolean isEditable, boolean showAll) {		
		//BudgetingUtil.getBudgetingGoalsData starts
		Map categoryDataPointMap = getTransactionCategoryDatas(userContext);
		budgetGoalDataMap = getBudgetGoalDatas(userContext);		
		List budgetGoals = getBudgetGoalsForAllCategories(userContext);
		
		budgetGoalsList = new ArrayList();
		BudgetGoal childGoal;
		List budgetGoalsChildList;
		double consolidatedSpending = 0;
		double consolidatedAverageSpending = 0;
		double consolidatedIncome = 0;
		double consolidatedAverageIncome = 0;
		double consolidatedTransfer = 0;
		double consolidatedAverageTransfer = 0;
		for(int i = 0; i < budgetGoals.size(); i++){
			
			budgetGoalsChildList = new ArrayList(0);
			BudgetGoal budgetGoal = (BudgetGoal)budgetGoals.get(i);
			String categoryId = String.valueOf(budgetGoal.getCategoryId());
			TransactionCategoryData transCategoryData = (TransactionCategoryData)categoryDataPointMap.get(categoryId);						
			//do not display uncategorized transactions. i.e transactions with category "UNCATEGORIZE"
			
						
			if (transCategoryData != null
					&& transCategoryData.getTransactionCategory().getCategoryId()
							.equals(new Long(1)))
				continue;
			if (!showAll && transCategoryData == null && budgetGoal.getChildCategory() == null)
				continue;
			BudgetGoalData budgetGoalData = (BudgetGoalData)budgetGoalDataMap.get(categoryId);
			populateActualBudgetingDetails(budgetGoal, transCategoryData);
			populateActualBudgetingDetails(budgetGoalData,budgetGoal);
			if(budgetGoal.isIncludeInTotalCalculation()){
				if(budgetGoal.getCategoryTypeId() == 2){
					consolidatedIncome += budgetGoal.getCurrent().getAmount().doubleValue();
					consolidatedAverageIncome += budgetGoal.getAverage().getAmount().doubleValue();
				}else if(budgetGoal.getCategoryTypeId()== 3){
					consolidatedSpending += budgetGoal.getCurrent().getAmount().doubleValue();
					consolidatedAverageSpending += budgetGoal.getAverage().getAmount().doubleValue();
				}else if(budgetGoal.getCategoryTypeId() == 4){
					consolidatedTransfer += budgetGoal.getCurrent().getAmount().doubleValue();
					consolidatedAverageTransfer += budgetGoal.getAverage().getAmount().doubleValue();
				}else if(budgetGoal.getCategoryTypeId()== 1){
					//Do Nothing
				}
			}
			
			if(budgetGoal.getChildCategory() != null && budgetGoal.getChildCategory().length > 0){
				budgetGoal.setParent(true);				
				BudgetGoal[] budgetGoalChild=budgetGoal.getChildCategory();
				for(int j=0; j < budgetGoalChild.length; j++){
					childGoal = budgetGoalChild[j];
					String categoryId1 = String.valueOf(childGoal.getCategoryId());
					transCategoryData = (TransactionCategoryData)categoryDataPointMap.get(categoryId1);
					budgetGoalData = (BudgetGoalData)budgetGoalDataMap.get(categoryId1);
					populateActualBudgetingDetails(childGoal, transCategoryData);
					populateActualBudgetingDetails(budgetGoalData,childGoal);
					childGoal.setChild(true);
					budgetGoalsChildList.add(childGoal);
				}
			}			
			budgetGoalsList.add(budgetGoal);
			budgetGoalsList.addAll(budgetGoalsChildList);
		}
		totalsMap = new HashMap();
		BudgetGoal totalSpendingGoal = getBudgetGoalForCategory(TOTALSPENDING,consolidatedSpending,consolidatedAverageSpending);
		populateActualBudgetingDetails(((BudgetGoalData)budgetGoalDataMap.get("expense")), totalSpendingGoal);	
		totalsMap.put(TOTALSPENDING, totalSpendingGoal);
		
		BudgetGoal totalTransferGoal = getBudgetGoalForCategory(TOTALTRANSFER,consolidatedTransfer,consolidatedAverageTransfer);
		populateActualBudgetingDetails(((BudgetGoalData)budgetGoalDataMap.get("transfer")), totalTransferGoal);
		totalsMap.put(TOTALTRANSFER, totalTransferGoal);
		
		BudgetGoal totalIncomeGoal = getBudgetGoalForCategory(TOTALINCOME,consolidatedIncome,consolidatedAverageIncome);
		populateActualBudgetingDetails(((BudgetGoalData)budgetGoalDataMap.get("income")), totalIncomeGoal);
		totalsMap.put(TOTALINCOME, totalIncomeGoal);				
		
		if (budgetGoalsList != null && budgetGoalsList.size() > 0) {
			displayBudgetGoalsTable(userContext, budgetGoalsList, totalsMap, isEditable);
		} else {
			System.out.println(NOT_AVAILABLE);
		}
	}
	
	private void populateActualBudgetingDetails(BudgetGoalData budgetGoalData , BudgetGoal budgetGoal)
	{
		String budgetGoalThresholdCurrency = DEFAULT_CURRENCY;
		String budgetGoalThreshold = null;
		boolean reportable = false;
		boolean alertable = false;
		boolean existing = false;
		if(budgetGoalData!=null){
			YMoney goal = budgetGoalData.getGoalAmount();
			if(goal!=null && goal.getAmount()!=null){
				budgetGoalThresholdCurrency =goal.getCurrencyCode();
				//budgetGoalThreshold = formatBudgetThreshold(context,goal.getAmount());
				budgetGoalThreshold = String.valueOf(goal.getAmount());
			}
			reportable = budgetGoalData.getIsReportable().booleanValue();
			alertable = budgetGoalData.getIsAlertable().booleanValue();
			existing = true;
		}
		budgetGoal.setGoal(budgetGoalThreshold);
		budgetGoal.setGoalCurrency(budgetGoalThresholdCurrency);
		budgetGoal.setAlertable(alertable);
		budgetGoal.setReportable(reportable);
		budgetGoal.setExisting(existing);
	}
	
	private Map getTransactionCategoryDatas(UserContext userContext){
		Calendar fromDate = getFromDate(NUM_OF_MONTH_FOR_AVG_CALCULATION);
		Calendar toDate = Calendar.getInstance();
		
        boolean currencyConversionRequired = true;
        boolean includeNonBaseCurrencyValues = true;
     
        CurrencyCriteria currencyCriteria = new CurrencyCriteria();
        currencyCriteria.setCurrencyCode(DEFAULT_CURRENCY);
        currencyCriteria.setCurrencyConversionRequired(currencyConversionRequired);
        currencyCriteria.setIncludeNonBaseCurrencyValues(includeNonBaseCurrencyValues);
		TransactionCriteria transCriteria = new TransactionCriteria();
		transCriteria.setCurrencyCriteria(currencyCriteria);
		
		//TODO TO BE COMMENTED LATER
		/*
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, 2005);
		calendar.set(Calendar.MONTH, Calendar.JANUARY);
		calendar.set(Calendar.DAY_OF_MONTH,1);
		fromDate = calendar.getTime();
		*/
		
		transCriteria.setFromDate(fromDate);
		transCriteria.setToDate(toDate);
		transCriteria.setDataPointType(DataPointType.MONTHLY);
		transCriteria.setCategoryLevelId(CATEGORY);		
		Object[] transCatDataArray = null;
		com.yodlee.soap.collections.List transCatDataList = null;
		try {
			transCatDataList = transactionDataService.getTransactionCategoryData(userContext, transCriteria);
		  if (transCatDataList != null){
			  transCatDataArray = transCatDataList.getElements();
		  }
		} catch (Exception e) {
			e.printStackTrace();
		}
		Map categoryDataPointMap = new HashMap();
		if ( transCatDataList != null ) {
			for (int i = 0; i < transCatDataArray.length; i++) {
				TransactionCategoryData dataPoint = (TransactionCategoryData) transCatDataArray[i];
				if(dataPoint.getTransactionCategory()!=null){					
					categoryDataPointMap.put(String.valueOf(dataPoint.getTransactionCategory().getCategoryId()),dataPoint);				
				}
			}	
		} else {
			//System.out.println("transCatDataList is null");
		}
		return categoryDataPointMap;
	}
	
	private Map getBudgetGoalDatas(UserContext userContext){		
		Map budgetGoalMap = new HashMap();
		BudgetGoalObject budgetGoalObject;
		try {
			budgetGoalObject = budgetGoalManagement.getAllBudgetGoals(userContext);
		} catch (Exception e) {
			throw new RuntimeException("Erroe fetching Budget Goals..." + e.getMessage());
		}
		
		ArrayOfBudgetGoalCategoryData categoryDatasArray = budgetGoalObject.getBudgetCategory();
		BudgetGoalCategoryData[] categoryDatas = null;
		if (categoryDatasArray != null){
			categoryDatas = categoryDatasArray.getElements();
		}
		
		ArrayOfBudgetSummaryData summaryDatasList = budgetGoalObject.getBudgetSummary();
		BudgetSummaryData[] summaryDatas = null;
		if (summaryDatasList != null){
			summaryDatas = summaryDatasList.getElements();
		}
		if(summaryDatas!=null && summaryDatas.length >0){
			for (int i = 0; i < summaryDatas.length; i++) {
				BudgetSummaryData summarydata = summaryDatas[i];
				Long categoryId = summarydata.getTransactionCategoryTypeId();
				//String category = TransactionCategoryTypes.fromValue(String.valueOf(categoryId)).getValue();
				String category = this.toCategoryTypeDesc(categoryId);
				budgetGoalMap.put(category,summarydata);
			}
		}
		if(categoryDatas!=null && categoryDatas.length >0){
			for (int i = 0; i < categoryDatas.length; i++) {
				BudgetGoalCategoryData categoryData = categoryDatas[i];
				long categoryId = categoryData.getTransactionCategoryId().longValue();
				String categoryIdStr = String.valueOf(categoryId);
				budgetGoalMap.put(categoryIdStr,categoryData);
			}
		}
		return budgetGoalMap;
	}
	
	private List getBudgetGoalsForAllCategories(UserContext userContext){
		//cacheAllCategories(context);
		List budgetGoals = new ArrayList();
		
		ArrayOfCategory userCategoriesArray = null;
		ArrayOfCategory subCategoriesArray = null;
		Category[] userCategories = null;
		Category[] subCategories = null;
		try {
			userCategoriesArray = transactionCategorizationService.getUserCategoriesAtLevel(userContext, CATEGORY.longValue());
			subCategoriesArray = transactionCategorizationService.getUserCategoriesAtLevel(userContext, SUB_CATEGORY.longValue());
			
			if (userCategoriesArray != null){
			userCategories = userCategoriesArray.getElements();
			} 
			
			if (subCategoriesArray != null){
				subCategories = userCategoriesArray.getElements();
			} 
			
		} catch (Exception e) {
			throw new RuntimeException("Error fetching user categories..." + e.getMessage());
		}		
		//*************ADD CATEGORIES TO BUDGETGOALS*************
		Category category = null;
		Category subCategory = null;
		if(userCategories != null && userCategories.length > 0){
			for (int ctr = 0; ctr<userCategories.length; ctr++){
				category = userCategories[ctr]; 
				if (category != null && category.getParentCategoryId() == null){
					BudgetGoal budgetGoalCategory = getBudgetGoalForCategory(category);
					budgetGoalCategory.setTransactionCategoryTypeId(category.getTransactionCategoryTypeId().longValue());
					budgetGoalCategory.setLeftIndent(1);
					//*****ITERATE THROUGH SUB-CATEGORY AND ADD RESPECTIVE SUB-CATEGORY TO THIS CATEGORY***
					if(subCategories != null && subCategories.length > 0){
						//*****DETERMINE NO. OF SUB-CATEGORIES FOR THIS CATEGORY*******
						int subCategoryLength=getChildCategoryLength(subCategories, budgetGoalCategory.getCategoryId().longValue());
							//*****ENDS DETERMINE NO. OF SUB-CATEGORIES FOR THIS CATEGORY*******
						BudgetGoal[] budgetGoalSubCategoryArray=new BudgetGoal[subCategoryLength];
						int subCategoryArrayIndex=0;
						for (int sub_ctr = 0; sub_ctr<subCategories.length; sub_ctr++) {
							subCategory = subCategories[sub_ctr];
							if(subCategory != null && subCategory.getParentCategoryId() != null && subCategory.getParentCategoryId().longValue() == budgetGoalCategory.getCategoryId().longValue()){
								BudgetGoal budgetGoalSubCategory=getBudgetGoalForCategory(subCategory);
								//budgetGoalSubCategory.setCategoryTypeName(toUpperFirstChar(TransactionCategoryTypes.fromValue(String.valueOf(budgetGoalCategory.getTransactionCategoryTypeId())).getValue()));
								budgetGoalSubCategory.setCategoryTypeName(toCategoryTypeDesc(budgetGoalCategory.getTransactionCategoryTypeId()));
								budgetGoalSubCategory.setCategoryTypeId(budgetGoalCategory.getCategoryTypeId());
								budgetGoalSubCategory.setTransactionCategoryTypeId(budgetGoalCategory.getTransactionCategoryTypeId().longValue());
								if(!budgetGoalCategory.isBudgetable()){
									budgetGoalSubCategory.setBudgetable(false);
								}
								budgetGoalSubCategory.setLeftIndent(2);
								budgetGoalSubCategoryArray[subCategoryArrayIndex++]=budgetGoalSubCategory;
							}
						}
						if(budgetGoalSubCategoryArray.length > 0){

							//Applying the sorting for subcategory
							Map treeMap = new TreeMap();
							Set set = null;
							int x=0;
							for (int i = 0; i < budgetGoalSubCategoryArray.length; i++) {
								treeMap.put(budgetGoalSubCategoryArray[i].getCategory(), budgetGoalSubCategoryArray[i]);
							}
							set = treeMap.keySet();
							for(Iterator iterator = set.iterator(); iterator.hasNext();){
								budgetGoalSubCategoryArray[x] = (BudgetGoal) treeMap.get(iterator.next());
								x++;
							}
							budgetGoalCategory.setChildCategory(budgetGoalSubCategoryArray);
						}
					}
					//*****END ITERATE THROUGH SUB-CATEGORY AND ADD RESPECTIVE SUB-CATEGORY TO THIS CATEGORY***						
					budgetGoals.add(budgetGoalCategory);
				}	
			}
		}
		else {
	    	System.out.println("$$$$ Categories is null $$$$");
	    }
		//*************END ADD CATEGORIES(WITHOUT SUPER-CATEGORIES) TO BUDGETGOALS*************
		return budgetGoals;
	}

	private void normalize(Calendar calendar)
	{
		Calendar tempCalendar = (Calendar)calendar.clone();
		calendar.clear();
		calendar.set(Calendar.YEAR,tempCalendar.get(Calendar.YEAR));
		calendar.set(Calendar.DAY_OF_YEAR,tempCalendar.get(Calendar.DAY_OF_YEAR));
	}
	
	private Calendar getFromDate(int noOfMonths)
	{
		int months = -noOfMonths;
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MONTH,months);
		calendar.set(Calendar.DAY_OF_MONTH,1);
		normalize(calendar);
		return calendar;
	}
	
	private BudgetGoal getBudgetGoalForCategory(Category category)
	{
		YMoney zeroMoney = new YMoney();
		zeroMoney.setAmount(new Double(0));
		zeroMoney.setCurrencyCode(DEFAULT_CURRENCY);

		BudgetGoal budgetGoal = new BudgetGoal();
		if(category != null){
			budgetGoal.setCategory(category.getLocalizedCategoryName());
			budgetGoal.setCurrent(zeroMoney);
			budgetGoal.setAverage(zeroMoney);
			budgetGoal.setExpenditure(zeroMoney);
			budgetGoal.setIncome(zeroMoney);
			budgetGoal.setAverageExpenditure(zeroMoney);
			budgetGoal.setAverageIncome(zeroMoney);
			Long categoryId = category.getCategoryId();
			budgetGoal.setCategoryId(categoryId);
			budgetGoal.setExisting(false);
			
			budgetGoal.setCategoryLevelId(category.getCategoryLevelId().longValue());
			if(category.getParentCategoryId() != null)
				budgetGoal.setParentCategoryId(category.getParentCategoryId().longValue());
			
			
			if(category.getTransactionCategoryTypeId() != null) {
				budgetGoal.setCategoryTypeId(category.getTransactionCategoryTypeId().longValue());
				//budgetGoal.setCategoryTypeName(toUpperFirstChar(TransactionCategoryTypes.fromValue(String.valueOf(category.getTransactionCategoryTypeId())).getValue()));
				budgetGoal.setCategoryTypeName(toCategoryTypeDesc(category.getTransactionCategoryTypeId()));
				
				
				if((budgetGoal.getCategoryId().longValue() == 33) || budgetGoal.getCategory().equals("Check")) {
					budgetGoal.setBudgetable(false);
				}
			}
			else{
				//System.out.println("$$$$ TransactionCategoryTypeID is null $$$$");
			}
		}else {
			//System.out.println("$$$$Category passed is null $$$$");
		}
		
		return budgetGoal;
	}
	
	private String toCategoryTypeDesc(Long categoryType){
		String categoryTypeName = null;
		long categoryTypeId = 0L;
		if (categoryType != null) {
			categoryTypeId = categoryType.longValue();
		}
		if (categoryTypeId == 1){
			categoryTypeName =  "UNCATEGORIZED";
	  } else  if (categoryTypeId == 2){
		categoryTypeName =  "INCOME";
	  } else  if (categoryTypeId == 3){
		categoryTypeName =  "EXPENSE";
	  }else  if (categoryTypeId == 4){
		categoryTypeName =  "TRANSFER";
	 }else  if (categoryTypeId == 5){
		categoryTypeName =  "DEFERREDCOMPENSATION";
	 } else {
		categoryTypeName =  "UNCATEGORIZED";
	  }
	 return categoryTypeName;
  }
	
	private void populateActualBudgetingDetails(BudgetGoal budgetGoal, TransactionCategoryData transCategoryData) {
		if (transCategoryData == null)
			return;
		Date toDate = new Date();
		int months = NUM_OF_MONTH_FOR_AVG_CALCULATION;
		com.yodlee.soap.collections.List sumDataPointsList = transCategoryData.getTransactionSummaryDataPointsList();
		Object[] sumDataPoints = null;
		if(sumDataPointsList != null){
			sumDataPoints = sumDataPointsList.getElements();
		}
		double currentDebit = 0;
		double currentCredit = 0;
		double totalDebit = 0;
		double totalCredit = 0;

		Date startDate = new Date();	
		Date endDate = new Date();
		
		if (sumDataPoints != null) {
			for (int i = 0; i < sumDataPoints.length; i++) {
				boolean isCurrentBudget = false;
				TransactionSummaryDataPoint transSumDataPoint = (TransactionSummaryDataPoint) sumDataPoints[i];
				Date date = new Date(transSumDataPoint.getTimeInMillis().longValue());			
				if((startDate.compareTo(date))>0){
					startDate=date;				
				}
				else if((endDate.compareTo(date))<0){
					endDate=date;			
				}
				Double credit = transSumDataPoint.getTotalCredit();
				Double debit = transSumDataPoint.getTotalDebit();
				if( (date.getMonth() == toDate.getMonth()) && (date.getYear() == toDate.getYear())){
					isCurrentBudget = true;
					currentDebit = (debit!=null)? (currentDebit+debit.doubleValue()):currentDebit;
					currentCredit = (credit!=null)? (currentCredit+credit.doubleValue()):currentCredit;
				} else {
					totalDebit = (debit!=null)? (totalDebit+debit.doubleValue()):totalDebit;
					totalCredit = (credit!=null)? (totalCredit+credit.doubleValue()):totalCredit;
				}
			}
		}
		CalendarUtil calendarUtil = new CalendarUtil();
		int countMonths = CalendarUtil.calculateMonthsInBetween(CalendarUtil
				.createCalendar(startDate.getTime()), CalendarUtil
				.createCalendar(endDate.getTime()));

		Long categoryBaseType = transCategoryData.getTransactionCategory().getCategoryTypeId();

		double tempCurrent = 0;
		double tempAverage = 0;
		int transactionType = -1;
		if (categoryBaseType.equals(new Long(5))){
			tempCurrent = currentCredit - currentDebit;
			tempAverage = totalCredit - totalDebit;
			//budgetGoal.setIncludeInTotalCalculation(false);
		}else if(categoryBaseType.equals(new Long(2))){
			tempCurrent = currentCredit - currentDebit;
			tempAverage = totalCredit - totalDebit;
		}else if(categoryBaseType.equals(new Long(3))){
			tempCurrent = currentDebit - currentCredit;
			tempAverage = totalDebit - totalCredit;
		}else if(categoryBaseType.equals(new Long(4))){
			//tempCurrent = currentDebit - currentCredit;
			if(Math.abs(currentDebit) >= Math.abs(currentCredit)) {
				tempCurrent = Math.abs(currentDebit);
				transactionType = DEBIT_TRANSACTION_TYPE_ID;
				
			}else {
				tempCurrent = Math.abs(currentCredit);
				transactionType = CREDIT_TRANSACTION_TYPE_ID;
			}
			//tempCurrent = Math.abs((currentDebit > currentCredit)? currentDebit: currentCredit);
			budgetGoal.setTransactionCategoryTypeId(transactionType);
			tempAverage = totalDebit - totalCredit;
			//budgetGoal.setIncludeInTotalCalculation(false);
		}else if(categoryBaseType.equals(new Long(1))){
			tempCurrent = Math.abs(currentDebit) + Math.abs(currentCredit);
			tempAverage = Math.abs(totalDebit) - Math.abs(totalCredit);
			budgetGoal.setBudgetable(false);
			budgetGoal.setIncludeInTotalCalculation(false);
		}
		YMoney current = new YMoney();
		current.setAmount(new Double(tempCurrent));
		current.setCurrencyCode(DEFAULT_CURRENCY);

			if((countMonths)>0){
			YMoney average = new YMoney();
			average.setAmount(new Double((tempAverage)/(countMonths)));
			average.setCurrencyCode(DEFAULT_CURRENCY);
			budgetGoal.setAverage(average);
		}		
		budgetGoal.setCurrent(current);		
		
		/*budgetGoal.setExpenditure(new YMoney(new Double(currentDebit),currencyCode));
		budgetGoal.setIncome(new YMoney(new Double(currentCredit),currencyCode));
		budgetGoal.setAverageExpenditure(new YMoney(new Double(totalDebit/months),currencyCode));
		budgetGoal.setAverageIncome(new YMoney(new Double(totalCredit/months),currencyCode));*/
	}

	//********METHOD FOR RETRIEVING THE LENGTH OF CHILD CATEGORIES**********
	public static int getChildCategoryLength(Category[] childCategories, long parentCategoryId){
		int length=0;
		Category category = null;
		for (int i = 0; i < childCategories.length; i++) {
			category = childCategories[i]; 
			if (category != null && category.getParentCategoryId() != null && category.getParentCategoryId().longValue() == parentCategoryId){
				length++;
			}
		}
		return length;
	}
	public static String toUpperFirstChar(String inputStr)
	{
		
		String returnStr=new String();
		if(inputStr==null || inputStr.length()==0 )
		{
			return returnStr;
		}
		else
		{
			String temp=inputStr.substring(0,1);			
			returnStr=temp.toUpperCase()+inputStr.substring(1,inputStr.length());
		}
	
		return returnStr;
	}
	private BudgetGoal getBudgetGoalForCategory(String category,double currentSpending,double averageSpending)
	{
		YMoney currentSpendingMoney = new YMoney();
		currentSpendingMoney.setAmount(new Double(currentSpending));
		currentSpendingMoney.setCurrencyCode(DEFAULT_CURRENCY);
		YMoney averageSpendingMoney = new YMoney();
		averageSpendingMoney.setAmount(new Double(averageSpending));
		averageSpendingMoney.setCurrencyCode(DEFAULT_CURRENCY);		
		BudgetGoal budgetGoal = new BudgetGoal(category,currentSpendingMoney,averageSpendingMoney);
		
		Long categoryId = null;
		if(category.equals(this.TOTALINCOME))
			categoryId = TransactionCategoryTypes_INCOME;
		else if(category.equals(this.TOTALSPENDING))
			categoryId = TransactionCategoryTypes_EXPENSE;
		budgetGoal.setCategoryId(categoryId);
		budgetGoal.setExisting(false);
		return budgetGoal;
	}		
		
	private void diplayRows(String temp, int colSize, boolean isLastColumn) {
		if (temp == null)
			temp = "-";
		System.out.print(temp);
		if (temp.length() < colSize) {
			System.out.print(generateSpaces(colSize - temp.length()));
		}
		if (!isLastColumn)
			System.out.print(generateSpaces(5));

	}
	private String generateSpaces(int i) {
		String x = BLANK_STR;
		for (int j = 0; j < i; j++)
			x = x + SPACE_1;
		return x;
	}
	private void displayBudgetGoalsTable(UserContext userContext,
			List budgetGoalsList,
			Map totalsMap, boolean isEditable) {
		
		BudgetGoal budget;
		Iterator iter = budgetGoalsList.iterator();
		int colsize[] = { HEADING_CAT.length(), HEADING_STM.length(),
				HEADING_AVG.length(), HEADING_MBG.length() };
		while (iter.hasNext()) {
			BudgetGoal goal = (BudgetGoal)iter.next();
			if (colsize[0] < goal.getCategory().length())
				colsize[0] = goal.getCategory().length();
		}
		colsize[0] = colsize[0] + 3;
		//3 is to account for 3 spaces before child categories
		diplayRows(HEADING_CAT, colsize[0], isLastColumnFalse);
		diplayRows(HEADING_STM, colsize[1], isLastColumnFalse);
		diplayRows(HEADING_AVG, colsize[2], isLastColumnFalse);
		diplayRows(HEADING_MBG, colsize[3], isLastColumnTrue);
		System.out.println("\n");

		iter = budgetGoalsList.iterator();
		String categoryTypeName = null;
		BudgetGoal goal;
		BudgetGoal totalGoal;
		List budgetGoalsToBeUpdatedList = new ArrayList();
		List budgetGoalsToBeAddedList = new ArrayList();
		String newGoal;
		BudgetGoalData budgetGoalData;
		Map totalGoalsMap = new HashMap();
		while (iter.hasNext()) {
			goal = (BudgetGoal)iter.next();
			if (categoryTypeName == null) {
				categoryTypeName = goal.getCategoryTypeName();
				System.out.println("**"+categoryTypeName);
			}
			//code to print category name
			if (!categoryTypeName.equals(goal.getCategoryTypeName())) {	
				totalGoal = (BudgetGoal)totalsMap.get(categoryTypeName.toLowerCase());	
				//Total is to displayed only for Income, Transfer and Expense. 				
				if (totalGoal != null) {
					totalGoalsMap.put(categoryTypeName.toLowerCase(), printBudgetGoal(totalGoal, colsize, true, isEditable));
				}
				categoryTypeName = goal.getCategoryTypeName();
				System.out.println("**"+categoryTypeName);
			}
			if (isEditable) {
				newGoal = printBudgetGoal(goal, colsize, false, isEditable);				
				if ( newGoal != null && !newGoal.equals(goal.getGoal())) {
					YMoney goalVal = new YMoney();
					goalVal.setAmount(new Double(Double.parseDouble(newGoal)));
					goalVal.setCurrencyCode(DEFAULT_CURRENCY);
					budgetGoalData = (BudgetGoalData)budgetGoalDataMap.get(goal.getCategoryId().toString());
					if (budgetGoalData == null) {
						budgetGoalData = createGoaldata(goal.getCategoryId().toString(), true, true, goalVal, goal.getCategoryLevelId());
						budgetGoalsToBeAddedList.add(budgetGoalData);
					} else {
						budgetGoalData.setGoalAmount(goalVal);
						budgetGoalsToBeUpdatedList.add(budgetGoalData);
					}
				}
			}
			else
				printBudgetGoal(goal, colsize, false, isEditable);
		}
		totalGoal = (BudgetGoal)totalsMap.get(categoryTypeName.toLowerCase());
		//Total is to displayed only for Income, Transfer and Expense.
		if (totalGoal != null)
			totalGoalsMap.put(categoryTypeName.toLowerCase(), printBudgetGoal(totalGoal, colsize, true, false));
		if (isEditable) {
			Iterator iterateTotalsMap = totalGoalsMap.entrySet().iterator();
			while (iterateTotalsMap.hasNext()) {				
				Map.Entry pair = (Map.Entry)iterateTotalsMap.next();
				if (pair.getKey() != null && pair.getValue() != null) {
					totalGoal = (BudgetGoal)totalsMap.get(pair.getKey().toString());
					if (totalGoal != null) {
						newGoal = (String)pair.getValue();
						YMoney goalVal = new YMoney();
						goalVal.setAmount(new Double(Double.parseDouble(newGoal)));
						goalVal.setCurrencyCode(DEFAULT_CURRENCY);
						budgetGoalData = createGoaldata(totalGoal.getCategoryId().toString(), true, true, goalVal, totalGoal.getCategoryLevelId());
						budgetGoalsToBeAddedList.add(budgetGoalData);
					}	
				}								
			}
			saveBudgetingGoals(userContext, budgetGoalsToBeAddedList, budgetGoalsToBeUpdatedList);
		}
	}
	
	private void saveBudgetingGoals(UserContext userContext,
			List budgetGoalsToBeAddedList,
			List budgetGoalsToBeUpdatedList) {
				
		BudgetGoalData[] bgDatas;
		if(budgetGoalsToBeAddedList.size() > 0) {
			try {			
				bgDatas = new BudgetGoalData[budgetGoalsToBeAddedList.size()];
				ArrayOfBudgetGoalData arrayOfBudgetGoalData = new ArrayOfBudgetGoalData();
				arrayOfBudgetGoalData.setElements(((BudgetGoalData[])budgetGoalsToBeAddedList.toArray(bgDatas)));
				budgetGoalManagement.addBudgetGoals(userContext, arrayOfBudgetGoalData);

			} catch (Exception e) {
				e.printStackTrace();			
			}
		}
		if(budgetGoalsToBeUpdatedList.size()>0){			
			bgDatas = new BudgetGoalData[budgetGoalsToBeUpdatedList.size()];
			try {
				ArrayOfBudgetGoalData arrayOfBudgetGoalData = new ArrayOfBudgetGoalData();
				arrayOfBudgetGoalData.setElements((BudgetGoalData[])budgetGoalsToBeUpdatedList.toArray(bgDatas));
				budgetGoalManagement.updateBudgetGoals(userContext, arrayOfBudgetGoalData);

			} catch (Exception e) {
				e.printStackTrace();			
			}
		}
	}
	private String printBudgetGoal(BudgetGoal goal, int[] colsize, boolean isTotalsField, boolean isEditable) {
		String newGoal = null;
		String category;	
		if (isTotalsField) {
			category = "Total:";
		} else if (goal.isChild()) {
			category = SPACE_2 + goal.getCategory();
		} else if (goal.isParent()) {
			category = "-" + goal.getCategory();
		} else {
			category = goal.getCategory();
		}
		diplayRows(SPACE_1 + category, colsize[0], isLastColumnFalse);

		diplayRows((goal.getCurrent() != null ? String.valueOf(round(goal
				.getCurrent().getAmount().doubleValue())) : "-"), colsize[1],
				isLastColumnFalse);
		diplayRows((goal.getAverage() != null ? String.valueOf(round(goal
				.getAverage().getAmount().doubleValue())) : "-"), colsize[2],
				isLastColumnFalse);
		if (!isEditable)
			diplayRows(goal.getGoal(), colsize[3], isLastColumnTrue);
		else {
			diplayRows(goal.getGoal(), 0, isLastColumnTrue);
			System.out.print(" *edit goal? Y/N: ");
			String choice = IOUtils.readStr();
			if (choice != null && choice.equalsIgnoreCase("y")) {
				System.out.print("Enter new goal:");
				newGoal = IOUtils.readStr();
				if (newGoal != null) {
					try {
						if (Double.parseDouble(newGoal) < 0)
							return null;
					} catch (NumberFormatException e) {
						return null;
					}
				}
			}			
		}
		if (isTotalsField)
			System.out.println("\n");
		else 
			System.out.println(BLANK_STR);
		return newGoal;
	}
	private Double round(double num) {  
		Double number = new Double (num);		
		String numDigits = "2";
		int decimalPlaces = Integer.parseInt(numDigits);
		long factor = (long) Math.pow(10, decimalPlaces);
		double tmpNumber = number.doubleValue() * factor;
		double roundedNumber = Math.round(tmpNumber);
		return new Double(roundedNumber / factor);    	
    }
	/**
	 * Creates a Goal Data with the passed params
	 * @param categoryId CategoryId
	 * @param isAlertable if the Goald is alertable
	 * @param isReportable if the reports are to be generated
	 * @param goal YMoney amount
	 * @param categoryLevelMap Map of the levels
	 * @return Created Goal Data
	 */
	private BudgetGoalData createGoaldata(String categoryId, boolean isAlertable,boolean isReportable, YMoney goal, long categoryLevelId) {
		//BudgetGoalData goalData = null;
		BudgetSummaryData budgetSummryGoalData = new BudgetSummaryData();
		budgetSummryGoalData.setIsAlertable(new Boolean(isAlertable));
		budgetSummryGoalData.setIsReportable(isReportable);
		budgetSummryGoalData.setGoalAmount(goal);
		if(categoryId.equalsIgnoreCase("income")){			
			budgetSummryGoalData.setTransactionCategoryTypeId(TransactionCategoryTypes_INCOME);			
		}else if(categoryId.equalsIgnoreCase("expense")){
			budgetSummryGoalData.setTransactionCategoryTypeId(TransactionCategoryTypes_EXPENSE);
		}else if(categoryId.equalsIgnoreCase("transfer")){
			budgetSummryGoalData.setTransactionCategoryTypeId(TransactionCategoryTypes_TRANSFER);
		} else{
			BudgetGoalCategoryData budgetGoalCategoryData=new BudgetGoalCategoryData();
			budgetGoalCategoryData.setIsAlertable(new Boolean(isAlertable));
			budgetGoalCategoryData.setIsReportable(new Boolean(isReportable));
			budgetGoalCategoryData.setGoalAmount(goal);
			budgetGoalCategoryData.setTransactionCategoryId(Long.valueOf(categoryId));
			budgetGoalCategoryData.setCategoryLevel(categoryLevelId);
			return budgetGoalCategoryData;
		}
		return budgetSummryGoalData;
	}
	private void setMonthlyBudgetAlerts(UserContext userContext){
		if (budgetThresholdAlertEnabled == null) {
			getAlertTypeAvailability(userContext);
		}
		//if BUDGET_THRESHOLD_ALERT is enabled
		if (!ENABLED.equals(budgetThresholdAlertEnabled)){
			System.out.println("Budget Threshold Alert has not been enabled for this cobrand");
		} else {
			boolean loop = true;
	        int choice = 0;
	        while (loop) {
	            try {
	                System.out.println("Monthly Budget Alerts Menu");
	                System.out.println(NAV_ADD_BUDGET_THRESHOLD_ALERT + ". Add Budget Threshold Alert Subscription");
	                System.out.println(NAV_UPDATE_BUDGET_THRESHOLD_ALERT + ". Update Budget Threshold Alert Subscription");
	                System.out.println(NAV_REMOVE_BUDGET_THRESHOLD_ALERT + ". Remove Budget Threshold Alert Subscription");
	                System.out.println("Enter " + NAV_QUIT + " Exit");
	                System.out.println("\n");
	                System.out.print("Enter Choice : ");
	                choice = IOUtils.readInt();

	                if (choice == NAV_ADD_BUDGET_THRESHOLD_ALERT)
	                	addBudgetThresholdAlertSubscription(userContext);
	                else if (choice == NAV_UPDATE_BUDGET_THRESHOLD_ALERT)
	                	updateBudgetThresholdAlertSubscription(userContext);
	                else if (choice == NAV_REMOVE_BUDGET_THRESHOLD_ALERT)
	                	removeBudgetThresholdAlertSubscription(userContext);
	                else if (choice == NAV_QUIT)
	                    loop = false;
	                else 
	                	System.out.println("Invalid Entry!");
	            } catch (Exception e) {
	                e.printStackTrace();
	            }
	        }			

		}
	}
	
	private void getAlertTypeAvailability(UserContext userContext){
		budgetThresholdAlertEnabled = "0";
		AlertTypeAvailability[] alertTypeAvailability = null;
		ArrayOfAlertTypeAvailability alertTypeAvailabilityArray = null;
		try {
			alertTypeAvailabilityArray = alertSubscriptionManagement.getAlertTypeAvailability(userContext);
		 if(alertTypeAvailabilityArray != null){
			 alertTypeAvailability = alertTypeAvailabilityArray.getElements();
		 }
		} catch (Exception e) {
			System.out.println(e);
		}
	   	AlertTypeAvailability alertType;
	   	if (alertTypeAvailability != null && alertTypeAvailability.length > 0) {
	   		for (int i=0; i<alertTypeAvailability.length; i++)
		   	{
		   		alertType = alertTypeAvailability[i];
		   		if (alertType.getAlertType().equals(AlertType.BUDGET_THRESHOLD_ALERT) && alertType.getAvailability() == 1) {
		   			budgetThresholdAlertEnabled = ENABLED;
		   			break;
		   		}
		   	}	
	   	}	   		
    }
	
	private int getBudgetThresholdPercentValue(AlertSubscription[] alertSubscriptions){		
		int percentGoal = 0;
		if (alertSubscriptions != null && alertSubscriptions.length > 0) {
			AlertSubscription budgetThresholdSub = null;
			for (int i = 0; i < alertSubscriptions.length; i++) {
				AlertSettings alertSettings = alertSubscriptions[i].getAlertSettings();
				if(alertSettings instanceof ScheduledAlertSettings)
				{
					if(alertSettings instanceof BudgetThresholdAlertSettings) {
						budgetThresholdSub = alertSubscriptions[i];
						BudgetThresholdAlertSettings settings = (BudgetThresholdAlertSettings)budgetThresholdSub.getAlertSettings();
						percentGoal =  settings.getPercentGoal();
						return percentGoal;
					}
				}
			}
		}
		return percentGoal;
	}
	/**
	 * returns true if BudgetThresholdAlert has been subscribed by the user,
	 * false otherwise
	 */
	private boolean getBudgetThresholdAlertsubscription(UserContext userContext){	
		ArrayOfAlertSubscription alertSubscriptionsArray = null;
		try {
			alertSubscriptionsArray =  alertSubscriptionManagement.getAlertSubscriptions(userContext);
			if (alertSubscriptionsArray != null){
				alertSubscriptions = alertSubscriptionsArray.getElements();
			}
		} catch (Exception e) {
			System.out.println(e);
		}
		if (alertSubscriptions != null && alertSubscriptions.length > 0) {
			for (int i = 0; i < alertSubscriptions.length; i++) {
				AlertSettings alertSettings = alertSubscriptions[i].getAlertSettings();
				if(alertSettings instanceof ScheduledAlertSettings)
				{
					if(alertSettings instanceof BudgetThresholdAlertSettings) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	private void resetAlertTrigger(UserContext userContext,String oldThreshold,String newThreshold){
		if(oldThreshold!=null && newThreshold!=null){
			double oldThresholdDouble = Double.parseDouble(oldThreshold);
			double newThresholdDouble = Double.parseDouble(newThreshold);
			if(newThresholdDouble < oldThresholdDouble)
			{
				try{
					budgetGoalManagement.resetTriggerTime(userContext);
				}catch(Exception e){
					System.out.println("Failed to reset alert trigger time");
				}
			}
		}
	}
	
	private void addBudgetThresholdAlertSubscription(UserContext userContext) {
		boolean subscribed = getBudgetThresholdAlertsubscription(userContext);
		if (subscribed) {
			System.out.println("User already subscribed for Budget Threshold Alert");
		} else {

			System.out.println("Notify me when my spending for any category" +
			" exceeds my budget by the following percent:");
			int newPercentGoal = IOUtils.readInt();
			Calendar cal = Calendar.getInstance();
			CalendarDate calendarDate = new CalendarDate();
			calendarDate.setYear(cal.get(Calendar.YEAR));
			calendarDate.setMonth(cal.get(Calendar.MONTH));
			calendarDate.setDayOfMonth(cal.get(Calendar.DAY_OF_MONTH));
			MonthlyAlertSchedule alertSchedule = new MonthlyAlertSchedule();
			alertSchedule.setStartDate(calendarDate);
			alertSchedule.setDayOfMonth(1);
		
			BudgetThresholdAlertSettings alertSettings = new BudgetThresholdAlertSettings();
			alertSettings.setAlertTypeStrCode(null);
			alertSettings.setAlertDestinations(null);
			alertSettings.setAlertType(null);
			alertSettings.setAlertSchedule(alertSchedule);
			alertSettings.setPercentGoal(newPercentGoal);
			
			//AlertSettings alertSettings= new BudgetThresholdAlertSettings(null, null, null, alertSchedule, newPercentGoal);
			AlertSubscription subscription = new UserAlertSubscription();
			subscription.setAlertSettings(alertSettings);
			
			AlertSubscription[] subs = {subscription}; 
			try {
				ArrayOfAlertSubscription arrayOfAlertSubscription = new ArrayOfAlertSubscription();
				arrayOfAlertSubscription.setElements(subs);
				alertSubscriptionManagement.addAlertSubscriptions(userContext, arrayOfAlertSubscription);

			} catch (InvalidRoleExceptionFault e) {
				System.out.println("Exception: Inavlid Role!!!");
			} catch (AlertTypeNotAvailableForItemExceptionFault e) {
				System.out
						.println("Exception: This Alert Type is not available for the Item!!!");
			} catch (AlertTypeNotAvailableForHeldAwayAccountExceptionFault e) {
				System.out
						.println("Exception: This Alert Type is not available for Held-Away accounts!!!");
			} catch (AlertTypeNotAvailableForHeldAccountExceptionFault e) {
				System.out
				.println("Exception: This Alert Type is not available for Held accounts!!!");
			} catch (AlertTypeNotAvailableExceptionFault e) {
				System.out
				.println("Exception: This Alert Type is not available!!!");
			} catch (AlertTypeAlreadySubscribedExceptionFault e) {
				System.out
				.println("Exception: This Alert Type has already bee already subscribed!!!");
			} catch (AlertTypeNotAvailableForItemAccountExceptionFault e) {
				System.out
				.println("Exception: This Alert Type is not available for this ItemAccount!!!");
			} catch (IncorrectAlertSettingsExceptionFault e) {
				System.out
				.println("Exception: Alert settings are incorrect!!!");
			} catch (IllegalArgumentValueExceptionFault e) {
				System.out
				.println("Exception: Illegal Argument Value!!!");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	private void updateBudgetThresholdAlertSubscription(UserContext userContext) {
		boolean subscribed = getBudgetThresholdAlertsubscription(userContext);
		int oldPercentGoal = 0;
		int newPercentGoal = 0;
		if (subscribed) {
			oldPercentGoal = getBudgetThresholdPercentValue(alertSubscriptions);
			System.out.println("Notify me when my spending for any category" +
					" exceeds my budget by the following percent:");
			newPercentGoal = IOUtils.readInt();
			
			Calendar cal = Calendar.getInstance();
			CalendarDate calendarDate = new CalendarDate();
			calendarDate.setYear(cal.get(Calendar.YEAR));
			calendarDate.setMonth(cal.get(Calendar.MONTH));
			calendarDate.setDayOfMonth(cal.get(Calendar.DAY_OF_MONTH));
			MonthlyAlertSchedule alertSchedule = new MonthlyAlertSchedule();
			alertSchedule.setStartDate(calendarDate);
			alertSchedule.setName(null);
			alertSchedule.setEndDate(null);
			alertSchedule.setDayOfMonth(1);
			
			//AlertSchedule alertSchedule = new MonthlyAlertSchedule(calendarDate, null, null, 1);
			BudgetThresholdAlertSettings alertSettings = new BudgetThresholdAlertSettings();
			alertSettings.setAlertTypeStrCode(null);
			alertSettings.setAlertDestinations(null);
			alertSettings.setAlertType(null);
			alertSettings.setAlertSchedule(alertSchedule);
			alertSettings.setPercentGoal(newPercentGoal);
			
			AlertSubscription subscription = new UserAlertSubscription();
			subscription.setAlertSettings(alertSettings);
			
			AlertSubscription[] subs = {subscription}; 
			try{
				ArrayOfAlertSubscription arrayOfAlertSubscription = new ArrayOfAlertSubscription();
				arrayOfAlertSubscription.setElements(subs);
				alertSubscriptionManagement.updateAlertSubscriptions(userContext, arrayOfAlertSubscription);
					
			} catch (InvalidRoleExceptionFault e) {
				System.out.println("Exception: Inavlid Role!!!");
			} catch (AlertTypeNotAvailableForItemExceptionFault e) {
				System.out
						.println("Exception: This Alert Type is not available for the Item!!!");
			} catch (AlertTypeNotAvailableForHeldAwayAccountExceptionFault e) {
				System.out
						.println("Exception: This Alert Type is not available for Held-Away accounts!!!");
			} catch (AlertTypeNotAvailableForHeldAccountExceptionFault e) {
				System.out
				.println("Exception: This Alert Type is not available for Held accounts!!!");
			} catch (AlertTypeNotAvailableExceptionFault e) {
				System.out
				.println("Exception: This Alert Type is not available!!!");
			} catch (AlertTypeAlreadySubscribedExceptionFault e) {
				System.out
				.println("Exception: This Alert Type has already bee already subscribed!!!");
			} catch (AlertTypeNotAvailableForItemAccountExceptionFault e) {
				System.out
				.println("Exception: This Alert Type is not available for this ItemAccount!!!");
			} catch (IncorrectAlertSettingsExceptionFault e) {
				System.out
				.println("Exception: Alert settings are incorrect!!!");
			} catch (IllegalArgumentValueExceptionFault e) {
				System.out
				.println("Exception: Illegal Argument Value!!!");
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			System.out.println("User not subscribed for Budget Threshold Alert");
		}
		resetAlertTrigger(userContext, String.valueOf(oldPercentGoal), String.valueOf(newPercentGoal));
	}
	private void removeBudgetThresholdAlertSubscription(UserContext userContext) {
		boolean subscribed = getBudgetThresholdAlertsubscription(userContext);
		if (subscribed) {

			Calendar cal = Calendar.getInstance();
			CalendarDate calendarDate = new CalendarDate();
			calendarDate.setYear(cal.get(Calendar.YEAR));
			calendarDate.setMonth(cal.get(Calendar.MONTH));
			calendarDate.setDayOfMonth(cal.get(Calendar.DAY_OF_MONTH));
			MonthlyAlertSchedule alertSchedule = new MonthlyAlertSchedule();
			alertSchedule.setStartDate(calendarDate);
			alertSchedule.setName(null);
			alertSchedule.setEndDate(null);
			alertSchedule.setDayOfMonth(1);
			
			//AlertSchedule alertSchedule = new MonthlyAlertSchedule(calendarDate, null, null, 1);
			BudgetThresholdAlertSettings alertSettings = new BudgetThresholdAlertSettings();
			alertSettings.setAlertTypeStrCode(null);
			alertSettings.setAlertDestinations(null);
			alertSettings.setAlertType(null);
			alertSettings.setAlertSchedule(alertSchedule);
			alertSettings.setPercentGoal(-1);
			
			//AlertSettings alertSettings= new BudgetThresholdAlertSettings(null, null, null, alertSchedule, -1);
			AlertSubscription subscription = new UserAlertSubscription();
			subscription.setAlertSettings(alertSettings);
			
			AlertSubscription[] subs = {subscription}; 			
			try{
				ArrayOfAlertSubscription arrayOfAlertSubscription = new ArrayOfAlertSubscription();
				arrayOfAlertSubscription.setElements(subs);
				alertSubscriptionManagement.removeAlertSubscriptions(userContext, arrayOfAlertSubscription);
				/*alertSubscriptionManagement.removeAlertSubscriptions(userContext, new ArrayOfAlertSubscription(subs));*/
			} catch (InvalidRoleExceptionFault e) {
				System.out.println("Exception: Inavlid Role!!!");
			} catch (AlertTypeNotAvailableForItemExceptionFault e) {
				System.out
						.println("Exception: This Alert Type is not available for the Item!!!");
			} catch (AlertTypeNotAvailableForHeldAwayAccountExceptionFault e) {
				System.out
						.println("Exception: This Alert Type is not available for Held-Away accounts!!!");
			} catch (AlertTypeNotAvailableForHeldAccountExceptionFault e) {
				System.out
				.println("Exception: This Alert Type is not available for Held accounts!!!");
			} catch (AlertTypeNotAvailableExceptionFault e) {
				System.out
				.println("Exception: This Alert Type is not available!!!");
			} catch (AlertTypeAlreadySubscribedExceptionFault e) {
				System.out
				.println("Exception: This Alert Type has already bee already subscribed!!!");
			} catch (AlertTypeNotAvailableForItemAccountExceptionFault e) {
				System.out
				.println("Exception: This Alert Type is not available for this ItemAccount!!!");
			} catch (IncorrectAlertSettingsExceptionFault e) {
				System.out
				.println("Exception: Alert settings are incorrect!!!");
			} catch (IllegalArgumentValueExceptionFault e) {
				System.out
				.println("Exception: Illegal Argument Value!!!");
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			System.out.println("User not subscribed for Budget Threshold Alert");
		}
	}


}
