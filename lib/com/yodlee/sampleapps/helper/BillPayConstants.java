
package com.yodlee.sampleapps.helper;



import com.yodlee.soap.core.usermanagement.State_US;
import com.yodlee.soap.common.Country;
/**
 * Holds all the constants related to BillPay
 * 
 */
public interface BillPayConstants {

	public final String ALL_PAYM_ACCTS = "All";
	public final String ACTIVE_PAYM_ACCTS = "onlyactive";
	public final String ACTIVE_AND_VERIFIED_PAYM_ACCTS = "active_and_verifed";
	public final String	All_PAYM_ACCTS_WITH_STATUS="Payment Accounts with Status";
	public final String	RECURRING_PAYMENT="recurring_payment";
	public final String	SCHEDULED_PAYMENT="scheduled_one_time_payment";
	public final String	BOTH_SCHEDULED_AND_RECURRING="scheduled_and_recurring";
	public final String	CHECK_PAYMENTS="check_payment";
		    	public final State_US[] statesArray = new State_US[]{
	        State_US.ALABAMA,
	        State_US.ALASKA,
	        State_US.AMERICAN_SAMOA,
	        State_US.ARIZONA,
	        State_US.ARKANSAS,
	        State_US.CALIFORNIA,
	        State_US.COLORADO,
	        State_US.CONNECTICUT,
	        State_US.DELAWARE,
	        State_US.DISTRICT_OF_COLUMBIA,
	        
	        State_US.FLORIDA,
	        State_US.GEORGIA,
	        State_US.GUAM,
	        State_US.HAWAII,
	        State_US.IDAHO,
	        State_US.ILLINOIS,
	        State_US.INDIANA,
	        State_US.IOWA,
	        State_US.KANSAS,
	        State_US.KENTUCKY,
	        State_US.LOUISIANA,
	        State_US.MAINE,
	        State_US.MARSHALL_ISLANDS,
	        State_US.MARYLAND,
	        State_US.MASSACHUSETTS,
	        State_US.MICHIGAN,
	        State_US.MINNESOTA,
	        State_US.MISSISSIPPI,
	        State_US.MISSOURI,
	        State_US.MONTANA,
	        State_US.NEBRASKA,
	        State_US.NEVADA,
	        State_US.NEW_HAMPSHIRE,
	        State_US.NEW_JERSEY,
	        State_US.NEW_MEXICO,
	        State_US.NEW_YORK,
	        State_US.NORTH_CAROLINA,
	        State_US.NORTH_DAKOTA,
	        State_US.NORTHERN_MARIANA_ISLANDS,
	        State_US.OHIO,
	        State_US.OKLAHOMA,
	        State_US.OREGON,
	        State_US.PALAU,
	        State_US.PENNSYLVANIA,
	        State_US.PUERTO_RICO,
	        State_US.RHODE_ISLAND,
	        State_US.SOUTH_CAROLINA,
	        State_US.SOUTH_DAKOTA,
	        State_US.TENNESSEE,
	        State_US.TEXAS,
	        State_US.UTAH,
	        State_US.VERMONT,
	        State_US.VIRGIN_ISLANDS,
	        State_US.VIRGINIA,
	        State_US.WASHINGTON,
	        State_US.WEST_VIRGINIA,
	        State_US.WISCONSIN,
	        State_US.WYOMING
	        
	    };
		    	 /**
		         * Represents the United States.
		         */
		        public static final Country US =  Country.fromString( "US");

		        /**
		         * Represents Great Britain.
		         */
		        public static final Country GB = Country.fromString("GB");

		        /**
		         * Represents Australia.
		         */
		        public static final Country AU = Country.fromString("AU");

		        /**
		         * Represents Belgium.
		         */
		        public static final Country BE = Country.fromString("BE");

		        /**
		         * Represents China.
		         */
		        public static final Country CN = Country.fromString("CN");
		        /**
		         * Represents India.
		         */
		        public static final Country IN = Country.fromString("IN");
		        /**
		         * Represents Canada.
		         */
		        public static final Country CA = Country.fromString("CA");
		        
		        /**
		         * Represents Spain.
		         */
		        public static final Country ES = Country.fromString( "ES");

		    	public static final Country[] countryArray = new Country[]{US, GB, AU, BE, CN ,IN, CA, ES};
	
}
