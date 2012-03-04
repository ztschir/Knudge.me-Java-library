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
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Date;

import javax.xml.rpc.ServiceException;

import com.yodlee.sampleapps.helper.CalendarUtil;
import com.yodlee.sampleapps.helper.Formatter;
import com.yodlee.soap.appext.base.datapoint.datapointservice.DataPointService;
import com.yodlee.soap.appext.base.datapoint.datapointservice.DataPointServiceServiceLocator;
import com.yodlee.soap.appext.base.datapoint.vo.AccountLevelCriteria;
import com.yodlee.soap.appext.base.datapoint.vo.CurrencyCriteria;
import com.yodlee.soap.appext.base.datapoint.vo.DataPoint;
import com.yodlee.soap.appext.base.datapoint.vo.DataPointType;
import com.yodlee.soap.appext.base.datapoint.vo.UserLevelCriteria;
import com.yodlee.soap.appext.base.datapoint.vo.UserNetworthData;
import com.yodlee.soap.common.CobrandContext;
import com.yodlee.soap.common.UserContext;
import com.yodlee.soap.core.CoreExceptionFault;
import com.yodlee.soap.core.IllegalArgumentValueExceptionFault;
import com.yodlee.soap.core.InvalidConversationCredentialsExceptionFault;
import com.yodlee.soap.core.InvalidUserContextExceptionFault;
import com.yodlee.soap.core.StaleConversationCredentialsExceptionFault;

public class NetWorth extends ApplicationSuper {

	protected DataPointService dataPointService;

    public static final int NETWORTH_CHANGE_NEUTRAL = 0;
    public static final int NETWORTH_CHANGE_POSITIVE = 1;
    public static final int NETWORTH_CHANGE_NEGATIVE = 2;

    /**
     * NetWorth Class
     */
    public NetWorth()
    {
        super ();
        DataPointServiceServiceLocator locator = new DataPointServiceServiceLocator();
        String serviceName = locator.getDataPointServiceWSDDServiceName();
        locator.setDataPointServiceEndpointAddress(System.getProperty("com.yodlee.soap.services.url")
						+ "/" + serviceName);
        try {
        	dataPointService = locator.getDataPointService();
		} catch (ServiceException se) {
			throw new RuntimeException(se);
		}
        
    }
    
    /**
     * Calculate Networth Change between 2 dates
     * @param userContext The User Context
     */
    public void calculateNetworthChange( UserContext userContext){
        Object[] datapoints = getDataPoints(getCobrandContext(), userContext);
        if (datapoints != null && datapoints.length > 1) {

            /*
            // BEGIN Debug purposes only
            System.out.println("Dumping datapoints....");
            for(int i=0; i<datapoints.length; i++){
                DataPoint dataPoint = datapoints[i];
                Date date = new Date(dataPoint.getTimeInMillis());
                System.out.println("date=" + date + " balance=" + dataPoint.getBalance()
                        + " timeInMillis=" + dataPoint.getTimeInMillis()  );

            }
            // END Debug purposes only
            */

            // Get Length of Datapoints
            int len = datapoints.length;

            // generate two datapoints
            DataPoint startDataPoint = (DataPoint)datapoints[len - 2];
            DataPoint endDataPoint = (DataPoint)datapoints[len - 1];

            // Get DateFormat
            String dateFormat = "MM/dd";

            //get the time stamps for the two data points
            Calendar endCal = Calendar.getInstance();
            endCal.setTimeInMillis(endDataPoint.getTimeInMillis().longValue());
            
            Calendar startCal = Calendar.getInstance();
            endCal.setTimeInMillis(startDataPoint.getTimeInMillis().longValue());
            
            Date endDate= endCal.getTime();
            Date startDate = startCal.getTime();
            
            String fromDate = Formatter.formatDate(startDate,dateFormat);
            String toDate= Formatter.formatDate(endDate,dateFormat);
            //System.out.println("fromDate=" + fromDate + " toDate=" +toDate);

            // get the initial balance and the closing balance between the two data points.
            double dEndValue = 0;
            double dStartValue = 0;
            Double startValue = getAmount(startDataPoint);
            Double endValue = getAmount(endDataPoint);

            if (endValue != null)
                dEndValue = endValue.doubleValue();
            if (startValue != null)
                dStartValue = startValue.doubleValue();
            double dAmountChange = dEndValue - dStartValue;

            // Calculate Networth Balance Change
            String netWorthBalanceChange = formatMoney(dAmountChange);
            //System.out.println("netWorthBalanceChange="+netWorthBalanceChange);

            // Calculate Networth Percent Change
            //System.out.println("dEndValue=" + dEndValue + " dStartValue=" + dStartValue);
            double networthChangePercent =((dEndValue - dStartValue)/dStartValue);
            //System.out.println("networthChangePercent=" + networthChangePercent);
            if(networthChangePercent==-0){
                networthChangePercent = 0;
            }
            if(networthChangePercent < 0){
                networthChangePercent = networthChangePercent * -1;
            }

            //System.out.println("networthChangePercent=" + networthChangePercent);
            String percentFormat = "##.##%";
            if (getImageId(dAmountChange) == NetWorth.NETWORTH_CHANGE_POSITIVE ){
                percentFormat = "+##.##%";
            }
            if (getImageId(dAmountChange) == NetWorth.NETWORTH_CHANGE_NEGATIVE ){
                percentFormat = "-##.##%";
            }
            NumberFormat nf = new DecimalFormat(percentFormat);
            //System.out.println("networthChangePercent=" + nf.format(networthChangePercent));

            // Determine Networth Change (used for image and coloring)
            //System.out.println("netWorthChange="+getImageId(dAmountChange));

            System.out.println("Networth Change from:" + fromDate + " to:" + toDate
                    + " " + netWorthBalanceChange  + " (" +  nf.format(networthChangePercent) + ")\n");
        }

    }

    /**
     * Get Image Id.  This can be used to show a diferent
     * image representing the networth change.
     *
     * @param dAmountChange  The amount of change
     * @return  Networth Image Id
     */
    private static int getImageId(double dAmountChange) {

        int imageId = 0;

        if (dAmountChange > 0)
            imageId = NetWorth.NETWORTH_CHANGE_POSITIVE;
        else if (dAmountChange == 0)
            imageId = NetWorth.NETWORTH_CHANGE_NEUTRAL;
        else if (dAmountChange < 0)
            imageId = NetWorth.NETWORTH_CHANGE_NEGATIVE;

        return imageId;
    }

    /**
     * Get Amount from Networth DataPoint
     * @param dataPoint Networth DataPoint
     * @return Networth DataPoint amount
     */
    private static Double getAmount(DataPoint dataPoint) {

        Double amount = null;
        if (dataPoint != null && dataPoint.getBalance() != null) {

            amount = dataPoint.getBalance().getAmount();
        }
        return amount;
    }

    /**
     * Get networth datapoints for a user.
     * @param cobrandContext The CobrandContext
     * @param userContext The UserContext
     * @return  DataPoint[]
     */
    protected  Object[] getDataPoints(CobrandContext
            cobrandContext, UserContext userContext) {

        Object[] dataPoints = null;
        DataPointType dataPointType = DataPointType.MONTHLY;

        // Determine dates for data pointss
        Calendar toDate = Calendar.getInstance();
        Calendar fromDate = getStartDate(Calendar.getInstance(), 1, dataPointType);

        // Determine if unmatched accounts are required
        boolean unmatchedAccountsRequired =
                getCobrandUnmatchedAccountsRequired(cobrandContext);

        // Get Currency Code
        String currencyCode = "USD";

        // Determine if currency conversion is required
        boolean currencyConversionRequired = true;
        boolean includeNonBaseCurrencyValues = true;

        // Construct datapoints
        dataPoints = constructDataPoints(userContext, currencyCode, fromDate, toDate, dataPointType,
                unmatchedAccountsRequired, currencyConversionRequired, includeNonBaseCurrencyValues);

        if (dataPoints == null || dataPoints.length < 2) {

            dataPointType = DataPointType.WEEKLY;
            fromDate = getStartDate(Calendar.getInstance(), 1,dataPointType);
            dataPoints = constructDataPoints(userContext, currencyCode, fromDate, toDate, dataPointType,
                    unmatchedAccountsRequired, currencyConversionRequired, includeNonBaseCurrencyValues);
        }

        return dataPoints;
    }

    /**
     * Construct request and fetch networth datapoints.
     *
     * @param userContext The UserContext
     * @param currencyCode The Currency Code String
     * @param fromDate From Date
     * @param toDate  To Date
     * @param dataPointType DataPointType
     * @param unmatchedAccountsRequired
     * @param currencyConversionRequired
     * @return
     */
    protected Object[] constructDataPoints(UserContext userContext,
                                                     String currencyCode,
                                                     Calendar fromDate,
                                                     Calendar toDate,
                                                     DataPointType
                                                             dataPointType,
                                                     boolean unmatchedAccountsRequired,
                                                     boolean currencyConversionRequired,
                                                     boolean includeNonBaseCurrencyValues) {
        /*
        System.out.println("constructDataPoints: toDate=" + toDate
                + " fromDate=" + fromDate
                + " unmatchedAccountsRequired=" + unmatchedAccountsRequired
                + "currencyConversionRequired=" + currencyConversionRequired
                + " currencyCode=" + currencyCode);
        */

        Object[] networthDataPoints = null;
        com.yodlee.soap.collections.List networthDataPointsList = null;
        if (userContext != null) {



            AccountLevelCriteria accountLevelCriteria = new AccountLevelCriteria();
            accountLevelCriteria.setFromDate(fromDate);
            accountLevelCriteria.setToDate(toDate);
            accountLevelCriteria.setDataPointType(dataPointType);
            accountLevelCriteria
                    .setItemAccountDeletedAccountsRequired(unmatchedAccountsRequired);
         
            CurrencyCriteria currencyCriteria = new CurrencyCriteria();
            currencyCriteria.setCurrencyCode(currencyCode);
            currencyCriteria.setCurrencyConversionRequired(currencyConversionRequired);
            currencyCriteria.setIncludeNonBaseCurrencyValues(includeNonBaseCurrencyValues);
            UserLevelCriteria userLevelCriteria = new UserLevelCriteria();
            userLevelCriteria.setAccountLevelCriteria(accountLevelCriteria);
            userLevelCriteria.setCurrencyCriteria(currencyCriteria);
            UserNetworthData userNetworthData = null;
            
        	try {
				userNetworthData = dataPointService.getUserNetworthData(userContext, userLevelCriteria);
			} catch (StaleConversationCredentialsExceptionFault e) {
				e.printStackTrace();
			} catch (InvalidConversationCredentialsExceptionFault e) {
				e.printStackTrace();
			} catch (CoreExceptionFault e) {
				e.printStackTrace();
			} catch (IllegalArgumentValueExceptionFault e) {
				e.printStackTrace();
			} catch (InvalidUserContextExceptionFault e) {
				e.printStackTrace();
			} catch (RemoteException e) {
				e.printStackTrace();
			}
            
            if(userNetworthData == null){
                System.out.println("userNetworthData=null");
            }else{
                if(userNetworthData.getNetworthDataPointsList() == null){
                    System.out.println("userNetworthData.getNetworthDataPointsList() == null");
                }
            }

            if (userNetworthData != null
                    && userNetworthData.getNetworthDataPointsList() != null) {

            	networthDataPointsList = userNetworthData.getNetworthDataPointsList(); 
                if (networthDataPointsList != null){
                networthDataPoints = networthDataPointsList.getElements();
                }
            }
        }

            return networthDataPoints;
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

    /**
     * Determine if the cobrand requires unmatch accounts.
     * @param cobrandContext
     * @return
     */
    private static boolean getCobrandUnmatchedAccountsRequired(
            CobrandContext
                    cobrandContext) {
        /*
        String sUnmatchedAccountsRequired = CobrandPropertyReader.getParam(
                cobrandContext, PARAM_UNMATCHED_ACCOUNTS_REQUIRED);
        if (sUnmatchedAccountsRequired == null
                || sUnmatchedAccountsRequired.trim().length() == 0)
            sUnmatchedAccountsRequired = DEFAULT_UNMATCHED_ACCOUNTS_REQUIRED;

        return sUnmatchedAccountsRequired.equalsIgnoreCase(CommonDefs.ON);*/
        return true;
    }

    public static String formatMoney(double money){

        NumberFormat  nf = new DecimalFormat("#,###,###");
        String m = nf.format(money);
        m = "$"+m;

        return m;
    }



    public static void main (String args[])
    {
        if (args.length < 2) {
            throw new RuntimeException ("Usage: <username> <password>") ;
        }

        String  userName   =   args[0] ;
        String  password   =   args[1] ;

        LoginUser loginUser = new LoginUser ();
        NetWorth netWorth = new NetWorth();
        UserContext userContext = null;
        
        System.out.println ("Logging in user " + userName + " with password " + password);
        userContext = loginUser.loginUser (userName, password);
        System.out.println ("Login of user " + userName + " successful");
        
        // Calculate Networth Change
        netWorth.calculateNetworthChange(userContext);


        // Log out the user
        System.out.println ("Logging out " + userName);
        loginUser.logoutUser (userContext);
        System.out.println ("Done logging out " + userName);
    }
}
