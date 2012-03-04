package YodleeSrc;

import javax.xml.rpc.ServiceException;

import com.yodlee.soap.collections.core.transactioncategorization.ArrayOfCategory;
import com.yodlee.soap.collections.core.transactionsearch.ArrayOfTransactionView;
import com.yodlee.soap.common.UserContext;
import com.yodlee.soap.core.dataservice.dataservice.DataService;
import com.yodlee.soap.core.dataservice.dataservice.DataServiceServiceLocator;
import com.yodlee.soap.core.transactioncategorization.Category;
import com.yodlee.soap.core.transactioncategorization.CategoryAlreadyExistsExceptionFault;
import com.yodlee.soap.core.transactioncategorization.CategoryLevelNotSupportedExceptionFault;
import com.yodlee.soap.core.transactioncategorization.CreateCategoryLimitExceptionFault;
import com.yodlee.soap.core.transactioncategorization.transactioncategorizationservice.TransactionCategorizationService;
import com.yodlee.soap.core.transactioncategorization.transactioncategorizationservice.TransactionCategorizationServiceServiceLocator;
import com.yodlee.soap.core.transactionsearch.ItemAccountId;
import com.yodlee.soap.core.transactionsearch.TransactionSearchExecInfo;
import com.yodlee.soap.core.transactionsearch.TransactionSearchFilter;
import com.yodlee.soap.core.transactionsearch.TransactionSearchRequest;
import com.yodlee.soap.core.transactionsearch.TransactionSearchResult;
import com.yodlee.soap.core.transactionsearch.TransactionSearchResultRange;
import com.yodlee.soap.core.transactionsearch.TransactionView;
import com.yodlee.soap.core.transactionsearch.transactionsearchservice.TransactionSearchService;
import com.yodlee.soap.core.transactionsearch.transactionsearchservice.TransactionSearchServiceServiceLocator;

/**
 * Methods for working with Transactions inside the Yodlee system.
 */
public class Transactions {

	protected TransactionSearchService transactionSearchService;
	protected DataService dataService;
	protected TransactionCategorizationService tcService;
	
	/**
	 * Represents the category level user created sub category
	 */
	public static final Long SUB_CATEGORY = new Long(4);
	
	public Transactions ()
    {
        super ();
        TransactionSearchServiceServiceLocator locator1 = new TransactionSearchServiceServiceLocator();
        String serviceName1 = locator1.getTransactionSearchServiceWSDDServiceName();
        locator1.setTransactionSearchServiceEndpointAddress(System.getProperty("com.yodlee.soap.services.url")
						+ "/" + serviceName1);
        try {
        	transactionSearchService = locator1.getTransactionSearchService();
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
		TransactionCategorizationServiceServiceLocator locator3 = new TransactionCategorizationServiceServiceLocator();
        String serviceName3 = locator3.getTransactionCategorizationServiceWSDDServiceName();
        locator3.setTransactionCategorizationServiceEndpointAddress(System.getProperty("com.yodlee.soap.services.url")
						+ "/" + serviceName3);
        try {
        	tcService = locator3.getTransactionCategorizationService();
		} catch (ServiceException se) {
			throw new RuntimeException(se);
		}		
    }
	
	public void viewTransactionsForItemAccount(UserContext userContext, long itemAccountId) {    	
    	long startRange = 1;
        long endRange = 100;

        // Create Results Range
        TransactionSearchResultRange txSearchResultRange =
                new TransactionSearchResultRange(new Long(startRange), new Long(endRange));

        // Create  TransactionSearchFilter
        TransactionSearchFilter txSearchFilter = new TransactionSearchFilter();
        txSearchFilter.setItemAccountId(new ItemAccountId(new Long(itemAccountId)));

        // Retrieve for the previous 30 days of transactions
        /*Date sysDate = new Date(System.currentTimeMillis());
        Calendar fromCalendar = Calendar.getInstance();
        fromCalendar.add(Calendar.DAY_OF_YEAR, -30);
        DateRange dateRange = new DateRange(fromCalendar.getTime(), sysDate);
        txSearchFilter.setPostDateRange(dateRange);*/

        // Create TransactionSearchRequest
        TransactionSearchRequest txSearchRequest = new TransactionSearchRequest();
        txSearchRequest.setSearchFilter(txSearchFilter);
        txSearchRequest.setContainerType("all");
        txSearchRequest.setIgnorePaymentTransactions(false) ;
        txSearchRequest.setIncludeAggregatedTransactions(true);
        txSearchRequest.setResultRange(txSearchResultRange);
        txSearchRequest.setIgnoreUserInput(true);
        
        TransactionSearchExecInfo txSearchExecInfo;
		try {
			txSearchExecInfo = transactionSearchService.executeUserSearchRequest(userContext, txSearchRequest);
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
        if ( txSearchExecInfo != null) {
        	viewTransactions(userContext, txSearchExecInfo);
        } else {
        	System.out.println("\nNo transactions found");
        }
    }
    
    public static void viewTransactions(UserContext userContext, TransactionSearchExecInfo txSearchExecInfo) {
        Long numberOfHits = txSearchExecInfo.getNumberOfHits();
    	if(numberOfHits == null || numberOfHits.intValue() <= 0) {
    		System.out.println("No transactions returned from search criteria.\n");    		
    	} else {
            TransactionSearchResult txSearchResult = txSearchExecInfo.getSearchResult();
	        ArrayOfTransactionView txView = txSearchResult.getTransactions();
	
	        if (txView != null && txView.getElements() != null) {
		        for ( int i = 0; i < txView.getElements().length; ++i) {
		            System.out.println(
		                  "PostDate=" + Formatter.formatDate(txView.getElements(i).getPostDate(), Formatter.DATE_SHORT_FORMAT )+ " " +
		                  "TransDate=" + Formatter.formatDate(txView.getElements(i).getTransactionDate(), Formatter.DATE_SHORT_FORMAT )+ "\n " +
		                  "ItemAccountId=" +txView.getElements(i).getAccount().getItemAccountId() + "\n " +
		                  "TransactionId=" +txView.getElements(i).getViewKey().getTransactionId() + "\n " +
		                  "ContainerType=" +txView.getElements(i).getViewKey().getContainerType() + "\n " +
		                  "Desc=" +txView.getElements(i).getDescription().getDescription() + "\n " +
		                  "AccountName=" +txView.getElements(i).getAccount().getAccountName() +  "\n " +
		                  "Mem=" +txView.getElements(i).getMemo().getMemo() + "\n " +
		                  "CategoryName=" +txView.getElements(i).getCategory().getCategoryName()+ "\n " +
       				  "merchant name=" +txView.getElements(i).getDescription().getMerchantName() + "\n " +
				  "simple desc=" +txView.getElements(i).getDescription().getSimpleDescription()+ "\n " +
				  "simple desc pref=" +txView.getElements(i).getDescription().getSimpleDescViewPref()+ "\n " +
				  "trans type desc=" +txView.getElements(i).getDescription().getTransactionTypeDesc()+ "\n " +
                                  "user description=" +txView.getElements(i).getDescription().getUserDescription()+ "\n " +
		                  "Status=" +txView.getElements(i).getStatus().getDescription() + "\n " +
		                  "Price=" +Formatter.formatMoney(txView.getElements(i).getPrice()) + "\n " +
		                  "Quantity=" +txView.getElements(i).getQuantity() + "\n " +
		                  "CatKeyword=" +txView.getElements(i).getCategorizationKeyword() + "\n " +
		                  "RunningBalance=" +txView.getElements(i).getRunningBalance() + "\n " +
		                  "Amount=" +Formatter.formatMoney(txView.getElements(i).getAmount()) + " "
		            );
		            System.out.println("\n");
		        }
	        }
    	}
    }
    
    /**
     * Views all the categories for a user.
     * 
     * @param userContext
     */
    public void viewUserTransactionCategories(UserContext userContext) {    	
    	    	
    	ArrayOfCategory categories;
    	try {
    		//categories = tcService.getUserTransactionCategories(userContext);
        categories = tcService.getSupportedTransactionCategrories(userContext);            
    	} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException();
		}
    	
    	for(int i=0; i<categories.getElements().length; i++) {
    		displayCategory(categories.getElements(i));
    	}
    }
    
    /**
     * Displays information about a single category and the children
     * of that category
     * 
     * @param category category to display information on
     */
    public static void displayCategory(Category category) {
    	if(SUB_CATEGORY.equals(category.getCategoryLevelId())) {
    		System.out.print("  ");
    	}
    	System.out.print("Id=");
    	System.out.print(category.getCategoryId());
    	System.out.print(",Name=");
    	System.out.print(category.getCategoryName());
    	System.out.print(",Description=");
    	System.out.print(category.getCategoryDescription());
    	System.out.print(",LevelId=");
    	System.out.print(category.getCategoryLevelId());
    	System.out.print("\n");
    	ArrayOfCategory childCategories = category.getChildCategory();
    	if(childCategories != null && childCategories.getElements() != null && childCategories.getElements().length >= 0) {
    		for(int i=0; i<childCategories.getElements().length; i++) {
    			displayCategory(childCategories.getElements(i));
    		}
    	}
    }

    public void addUserCategory(UserContext userContext) {
    	
    	System.out.println("Adding new Category");
        System.out.print("Enter Parent Category Id: ");
        long parentCategoryId = IOUtils.readLong();
		System.out.print("New Category Name: ");
		String newCategoryName = IOUtils.readStr();
        Category category = new Category();
        category.setCategoryName(newCategoryName);
        //category.setCategoryLevelId(Long.valueOf(SUB_CATEGORY.longValue()));
        category.setCategoryLevelId(Long.valueOf(SUB_CATEGORY.toString()));
        category.setParentCategoryId(new Long(parentCategoryId));
        try {
        	tcService.manageUserCategories(userContext, new ArrayOfCategory(new Category[]{category}));
        	System.out.println("User category successfully added\n");
        } catch (CreateCategoryLimitExceptionFault e) {
			System.out.println("Exception: Exceeded the Maximum Number of Categories per User/Category limit" );
		} catch (CategoryLevelNotSupportedExceptionFault e) {
			System.out.println("Exception: CategoryLevel not supported");
		} catch (CategoryAlreadyExistsExceptionFault e) {
			System.out.println("Exception: Category already exists");
		} catch (Exception e) {
			e.printStackTrace();
		}
    }

    
}
