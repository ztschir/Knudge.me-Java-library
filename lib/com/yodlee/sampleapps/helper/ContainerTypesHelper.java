/*
 * Copyright 2009 Yodlee, Inc.  All Rights Reserved.  Your use of this code 
 * requires a license from Yodlee.  Any such license to this code is 
 * restricted to evaluation/illustrative purposes only. It is not intended 
 * for use in a production environment, and Yodlee disclaims all warranties 
 * and/or support obligations concerning this code, regardless of the terms 
 * of any other agreements between Yodlee and you."
 */
package com.yodlee.sampleapps.helper;

import java.util.HashMap;

/**
 * Exposes (constant value) names for the various Container types.
 */
public class ContainerTypesHelper {

	 public static final java.lang.String AIR_RESERVATION  = "travel";	
	 public static final java.lang.String AUCTION = "auction"; 
	 public static final java.lang.String BANK = "bank"; 
	 public static final java.lang.String BILL = "bills"; 
	 public static final java.lang.String BILL_PAY_SERVICE = "bill_payment"; 
	 public static final java.lang.String BOOKMARKLINK = "bookmarklink"; 
	 public static final java.lang.String CABLE_SATELLITE = "cable_satellite"; 
	 public static final java.lang.String CALENDAR = "calendar"; 
	 public static final java.lang.String CAR_RESERVATION = "rentals"; 
	 public static final java.lang.String CHARITIES = "charities"; 
	 public static final java.lang.String CHARTS = "charts"; 
	 public static final java.lang.String CHAT = "chats"; 
	 public static final java.lang.String CONSUMER_GUIDE = "consumer_guide"; 
	 public static final java.lang.String CREDIT_CARD = "credits"; 
	 public static final java.lang.String DEAL = "deal"; 
	 public static final java.lang.String HOTEL_RESERVATION = "hotel_reservations"; 
	 public static final java.lang.String INSURANCE = "insurance"; 
	 public static final java.lang.String INVESTMENT = "stocks"; 
	 public static final java.lang.String ISP = "isp"; 
	 public static final java.lang.String JOB = "jobs"; 
	 public static final java.lang.String LOAN = "loans"; 
	 public static final java.lang.String MAIL = "mail"; 
	 public static final java.lang.String MESSAGE_BOARD = "messageboards"; 
	 public static final java.lang.String MINUTES = "minutes"; 
	 public static final java.lang.String MISCELLANEOUS = "miscellaneous"; 
	 public static final java.lang.String MORTGAGE = "mortgage"; 
	 public static final java.lang.String NEWS = "news"; 
	 public static final java.lang.String ORDER = "orders"; 
	 public static final java.lang.String OTHER_ASSETS = "other_assets"; 
	 public static final java.lang.String OTHER_LIABILITIES = "other_liabilities"; 
	 public static final java.lang.String PREPAY = "prepay"; 
	 public static final java.lang.String REALESTATE = "RealEstate"; 
	 public static final java.lang.String RESERVATION = "reservations"; 
	 public static final java.lang.String REWARD_PROGRAM = "miles"; 
	 public static final java.lang.String TELEPHONE = "telephone"; 
	 public static final java.lang.String UTILITIES = "utilities"; 

	 protected static HashMap containerTypes;
	 
	 static {
	        containerTypes = new HashMap(36);

	        containerTypes.put(CREDIT_CARD, null);
	        containerTypes.put(INVESTMENT, null);
	        containerTypes.put(BANK, null);
	        containerTypes.put(MINUTES, null);
	        containerTypes.put(MAIL, null);
	        containerTypes.put(ORDER, null);
	        containerTypes.put(BILL, null);
	        containerTypes.put(ISP, null);
	        containerTypes.put(PREPAY, null);
	        containerTypes.put(CHARITIES, null);   
	        containerTypes.put(TELEPHONE, null);
	        containerTypes.put(AIR_RESERVATION, null);
	        containerTypes.put(CAR_RESERVATION, null);
	        containerTypes.put(BILL_PAY_SERVICE, null);
	        containerTypes.put(CALENDAR, null);
	        containerTypes.put(CHAT, null);

	        containerTypes.put(JOB, null);
	        containerTypes.put(DEAL, null);
	        containerTypes.put(MESSAGE_BOARD, null);

	        containerTypes.put(CONSUMER_GUIDE, null);
	        containerTypes.put(HOTEL_RESERVATION, null);
	        containerTypes.put(AUCTION, null);
	        containerTypes.put(LOAN, null);
	        containerTypes.put(MORTGAGE, null);
	        containerTypes.put(INSURANCE, null);
	        containerTypes.put(REWARD_PROGRAM, null);
	        containerTypes.put(NEWS, null);
	        containerTypes.put(RESERVATION, null);
	        containerTypes.put(CHARTS, null);
	        containerTypes.put(UTILITIES, null);
	        containerTypes.put(CABLE_SATELLITE, null);
	        containerTypes.put(MISCELLANEOUS, null);
	        containerTypes.put(OTHER_ASSETS, null);
	        containerTypes.put(OTHER_LIABILITIES, null);
	        
	        containerTypes.put(BOOKMARKLINK, null);
	        containerTypes.put(REALESTATE, null);
	    }
	 
	    /**
	     * Returns true if the given container type is a valid one, otherwise false.
	     * @param containerType The container type whose validity is being checked.
	     * <p>
	     * @return true if the given container type is a valid one, otherwise false.
	     */
	    public static boolean isValid(String containerType) {
	        //Check without using reflection.
	        if (containerTypes.containsKey(containerType)) {
	            return true;
	        }
	        return false;
	    }
}
