/*
* Copyright (c) 2006,2007 Yodlee, Inc. All Rights Reserved.
*
* This software is the confidential and proprietary information of Yodlee, Inc. 
* Use is subject to license terms.
*/
package com.yodlee.sampleapps.helper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <code>State_US</code> is a helper class that supplies valid values for the "state" user profile field. In other
 * words, you need to use the constants declared in the class as parameter to the
 * {@link UserProfile_US#setState <code>setState</code>} method of the
 * {@link UserProfile_US <code>UserProfile_US</code>} class.
 * <p/>
 * The states and their abbreviations are taken from <a href="http://www.usps.com/ncsc/lookups/usps_abbreviations.html">
 * the United States Postal Service</a>.
 * <p/>
 *
 * @see UserProfile
 * @see UserProfile_US
 * @see UserProfileManagement
 * @deprecated use <code>State</code>.  
 */
public class State_US implements java.io.Serializable, Comparable {
    public static final String ARMED_FORCES_AMERICAS_EXCEPT_CANADA_STRING = "ARMED FORCES AMERICAS (EXCEPT CANADA)";
    public static final String AA = "AA"; 
	public static final long AA_CODE = 1;
	public static final State_US ARMED_FORCES_AMERICAS_EXCEPT_CANADA = 
		new State_US(ARMED_FORCES_AMERICAS_EXCEPT_CANADA_STRING, AA, AA_CODE);
    
    public static final String ARMED_FORCES_EUROPE_AFRICA_CANADA_STRING = "ARMED_FORCES_EUROPE_AFRICA_CANADA";
    public static final String AE = "AE"; 
	public static final long AE_CODE = 2;
	public static final State_US ARMED_FORCES_EUROPE_AFRICA_CANADA = 
		new State_US(ARMED_FORCES_EUROPE_AFRICA_CANADA_STRING, AE, AE_CODE);

	public static final String ALABAMA_STRING = "ALABAMA";
    public static final String AL = "AL"; 
	public static final long AL_CODE = 3;
	public static final State_US ALABAMA = 
		new State_US(ALABAMA_STRING, AL, AL_CODE);

	public static final String ALASKA_STRING = "ALASKA";
    public static final String AK = "AK"; 
	public static final long AK_CODE = 4;
	public static final State_US ALASKA = 
		new State_US(ALASKA_STRING, AK, AK_CODE);
	
	public static final String AMERICAN_SAMOA_STRING = "AMERICAN_SAMOA";
    public static final String AS = "AS"; 
	public static final long AS_CODE = 5;
	public static final State_US AMERICAN_SAMOA = 
		new State_US(AMERICAN_SAMOA_STRING, AS, AS_CODE);
	
	public static final String ARIZONA_STRING = "ARIZONA";
    public static final String AZ = "AZ"; 
	public static final long AZ_CODE = 6;
	public static final State_US ARIZONA = 
		new State_US(ARIZONA_STRING, AZ, AZ_CODE);
	
	public static final String ARKANSAS_STRING = "ARKANSAS";
    public static final String AR = "AR"; 
	public static final long AR_CODE = 7;
	public static final State_US ARKANSAS = 
		new State_US(ARKANSAS_STRING, AR, AR_CODE);
	
	public static final String CALIFORNIA_STRING = "CALIFORNIA";
    public static final String CA = "CA"; 
	public static final long CA_CODE = 8;
	public static final State_US CALIFORNIA = 
		new State_US(CALIFORNIA_STRING, CA, CA_CODE);
	
	public static final String COLORADO_STRING = "COLORADO";
    public static final String CO = "CO"; 
	public static final long CO_CODE = 9;
	public static final State_US COLORADO = 
		new State_US(COLORADO_STRING, CO, CO_CODE);
	
	public static final String CONNECTICUT_STRING = "CONNECTICUT";
    public static final String CT = "CT"; 
	public static final long CT_CODE = 10;
	public static final State_US CONNECTICUT = 
		new State_US(CONNECTICUT_STRING, CT, CT_CODE);
	
	public static final String DELAWARE_STRING = "DELAWARE";
    public static final String DE = "DE"; 
	public static final long DE_CODE = 11;
	public static final State_US DELAWARE = 
		new State_US(DELAWARE_STRING, DE, DE_CODE);
	
	public static final String DISTRICT_OF_COLUMBIA_STRING = "DISTRICT_OF_COLUMBIA";
    public static final String DC = "DC"; 
	public static final long DC_CODE = 12;
	public static final State_US DISTRICT_OF_COLUMBIA = 
		new State_US(DISTRICT_OF_COLUMBIA_STRING, DC, DC_CODE);
	
	public static final String FEDERATED_STATES_OF_MICRONESIA_STRING = "FEDERATED_STATES_OF_MICRONESIA";
    public static final String FM = "FM"; 
	public static final long FM_CODE = 13;
	public static final State_US FEDERATED_STATES_OF_MICRONESIA = 
		new State_US(FEDERATED_STATES_OF_MICRONESIA_STRING, FM, FM_CODE);
	
	public static final String FLORIDA_STRING = "FLORIDA";
    public static final String FL = "FL"; 
	public static final long FL_CODE = 14;
	public static final State_US FLORIDA = 
		new State_US(FLORIDA_STRING, FL, FL_CODE);
	
	public static final String GEORGIA_STRING = "GEORGIA";
    public static final String GA = "GA"; 
	public static final long GA_CODE = 15;
	public static final State_US GEORGIA = 
		new State_US(GEORGIA_STRING, GA, GA_CODE);
	
	public static final String GUAM_STRING = "GUAM";
    public static final String GU = "GU"; 
	public static final long GU_CODE = 16;
	public static final State_US GUAM = 
		new State_US(GUAM_STRING, GU, GU_CODE);
	
	public static final String HAWAII_STRING = "HAWAII";
    public static final String HI = "HI"; 
	public static final long HI_CODE = 17;
	public static final State_US HAWAII = 
		new State_US(HAWAII_STRING, HI, HI_CODE);
	
	public static final String IDAHO_STRING = "IDAHO";
    public static final String ID = "ID"; 
	public static final long ID_CODE = 18;
	public static final State_US IDAHO = 
		new State_US(IDAHO_STRING, ID, ID_CODE);
	
	public static final String ILLINOIS_STRING = "ILLINOIS";
    public static final String IL = "IL"; 
	public static final long IL_CODE = 19;
	public static final State_US ILLINOIS = 
		new State_US(ILLINOIS_STRING, IL, IL_CODE);
	
	public static final String INDIANA_STRING = "INDIANA";
    public static final String IN = "IN"; 
	public static final long IN_CODE = 20;
	public static final State_US INDIANA = 
		new State_US(INDIANA_STRING, IN, IN_CODE);
	
	public static final String IOWA_STRING = "IOWA";
    public static final String IA = "IA"; 
	public static final long IA_CODE = 21;
	public static final State_US IOWA = 
		new State_US(IOWA_STRING, IA, IA_CODE);
	
	public static final String KANSAS_STRING = "KANSAS";
    public static final String KS = "KS"; 
	public static final long KS_CODE = 22;
	public static final State_US KANSAS = 
		new State_US(KANSAS_STRING, KS, KS_CODE);
	
	public static final String KENTUCKY_STRING = "KENTUCKY";
    public static final String KY = "KY"; 
	public static final long KY_CODE = 23;
	public static final State_US KENTUCKY = 
		new State_US(KENTUCKY_STRING, KY, KY_CODE);
	
	public static final String LOUISIANA_STRING = "LOUISIANA";
    public static final String LA = "LA"; 
	public static final long LA_CODE = 24;
	public static final State_US LOUISIANA = 
		new State_US(LOUISIANA_STRING, LA, LA_CODE);
	
	public static final String MAINE_STRING = "MAINE";
    public static final String ME = "ME"; 
	public static final long ME_CODE = 25;
	public static final State_US MAINE = 
		new State_US(MAINE_STRING, ME, ME_CODE);
	
	public static final String MARSHALL_ISLANDS_STRING = "MARSHALL_ISLANDS";
    public static final String MH = "MH"; 
	public static final long MH_CODE = 26;
	public static final State_US MARSHALL_ISLANDS = 
		new State_US(MARSHALL_ISLANDS_STRING, MH, MH_CODE);
	
	public static final String MARYLAND_STRING = "MARYLAND";
    public static final String MD = "MD"; 
	public static final long MD_CODE = 27;
	public static final State_US MARYLAND = 
		new State_US(MARYLAND_STRING, MD, MD_CODE);
	
	public static final String MASSACHUSETTS_STRING = "MASSACHUSETTS";
    public static final String MA = "MA"; 
	public static final long MA_CODE = 28;
	public static final State_US MASSACHUSETTS = 
		new State_US(MASSACHUSETTS_STRING, MA, MA_CODE);
	
	public static final String MICHIGAN_STRING = "MICHIGAN";
    public static final String MI = "MI"; 
	public static final long MI_CODE = 29;
	public static final State_US MICHIGAN = 
		new State_US(MICHIGAN_STRING, MI, MI_CODE);
	
	public static final String MINNESOTA_STRING = "MINNESOTA";
    public static final String MN = "MN"; 
	public static final long MN_CODE = 30;
	public static final State_US MINNESOTA = 
		new State_US(MINNESOTA_STRING, MN, MN_CODE);
	
	public static final String MISSISSIPPI_STRING = "MISSISSIPPI";
    public static final String MS = "MS"; 
	public static final long MS_CODE = 31;
	public static final State_US MISSISSIPPI = 
		new State_US(MISSISSIPPI_STRING, MS, MS_CODE);
	
	public static final String MISSOURI_STRING = "MISSOURI";
    public static final String MO = "MO"; 
	public static final long MO_CODE = 32;
	public static final State_US MISSOURI = 
		new State_US(MISSOURI_STRING, MO, MO_CODE);
	
	public static final String MONTANA_STRING = "MONTANA";
    public static final String MT = "MT"; 
	public static final long MT_CODE = 33;
	public static final State_US MONTANA = 
		new State_US(MONTANA_STRING, MT, MT_CODE);
	
	public static final String NEBRASKA_STRING = "NEBRASKA";
    public static final String NE = "NE"; 
	public static final long NE_CODE = 34;
	public static final State_US NEBRASKA = 
		new State_US(NEBRASKA_STRING, NE, NE_CODE);
	
	public static final String NEVADA_STRING = "NEVADA";
    public static final String NV = "NV"; 
	public static final long NV_CODE = 35;
	public static final State_US NEVADA = 
		new State_US(NEVADA_STRING, NV, NV_CODE);
	
	public static final String NEW_HAMPSHIRE_STRING = "NEW_HAMPSHIRE";
    public static final String NH = "NH"; 
	public static final long NH_CODE = 36;
	public static final State_US NEW_HAMPSHIRE = 
		new State_US(NEW_HAMPSHIRE_STRING, NH, NH_CODE);
	
	public static final String NEW_JERSEY_STRING = "NEW_JERSEY";
    public static final String NJ = "NJ"; 
	public static final long NJ_CODE = 37;
	public static final State_US NEW_JERSEY = 
		new State_US(NEW_JERSEY_STRING, NJ, NJ_CODE);
	
	public static final String NEW_MEXICO_STRING = "NEW_MEXICO";
    public static final String NM = "NM"; 
	public static final long NM_CODE = 38;
	public static final State_US NEW_MEXICO = 
		new State_US(NEW_MEXICO_STRING, NM, NM_CODE);
	
	public static final String NEW_YORK_STRING = "NEW_YORK";
    public static final String NY = "NY"; 
	public static final long NY_CODE = 39;
	public static final State_US NEW_YORK = 
		new State_US(NEW_YORK_STRING, NY, NY_CODE);
	
	public static final String NORTH_CAROLINA_STRING = "NORTH_CAROLINA";
    public static final String NC = "NC"; 
	public static final long NC_CODE = 40;
	public static final State_US NORTH_CAROLINA = 
		new State_US(NORTH_CAROLINA_STRING, NC, NC_CODE);
	
	public static final String NORTH_DAKOTA_STRING = "NORTH_DAKOTA";
    public static final String ND = "ND"; 
	public static final long ND_CODE = 41;
	public static final State_US NORTH_DAKOTA = 
		new State_US(NORTH_DAKOTA_STRING, ND, ND_CODE);
	
	public static final String NORTHERN_MARIANA_ISLANDS_STRING = "NORTHERN_MARIANA_ISLANDS";
    public static final String MP = "MP"; 
	public static final long MP_CODE = 42;
	public static final State_US NORTHERN_MARIANA_ISLANDS = 
		new State_US(NORTHERN_MARIANA_ISLANDS_STRING, MP, MP_CODE);
	
	public static final String OHIO_STRING = "OHIO";
    public static final String OH = "OH"; 
	public static final long OH_CODE = 43;
	public static final State_US OHIO = 
		new State_US(OHIO_STRING, OH, OH_CODE);
	
	public static final String OKLAHOMA_STRING = "OKLAHOMA";
    public static final String OK = "OK"; 
	public static final long OK_CODE = 44;
	public static final State_US OKLAHOMA = 
		new State_US(OKLAHOMA_STRING, OK, OK_CODE);

	public static final String OREGON_STRING = "OREGON";
    public static final String OR = "OR"; 
	public static final long OR_CODE = 45;
	public static final State_US OREGON = 
		new State_US(OREGON_STRING, OR, OR_CODE);

	public static final String PALAU_STRING = "PALAU";
    public static final String PW = "PW"; 
	public static final long PW_CODE = 46;
	public static final State_US PALAU = 
		new State_US(PALAU_STRING, PW, PW_CODE);

	public static final String PENNSYLVANIA_STRING = "PENNSYLVANIA";
    public static final String PA = "PA"; 
	public static final long PA_CODE = 47;
	public static final State_US PENNSYLVANIA = 
		new State_US(PENNSYLVANIA_STRING, PA, PA_CODE);

	public static final String PUERTO_RICO_STRING = "PUERTO_RICO";
    public static final String PR = "PR"; 
	public static final long PR_CODE = 48;
	public static final State_US PUERTO_RICO = 
		new State_US(PUERTO_RICO_STRING, PR, PR_CODE);

	public static final String RHODE_ISLAND_STRING = "RHODE_ISLAND";
    public static final String RI = "RI"; 
	public static final long RI_CODE = 49;
	public static final State_US RHODE_ISLAND = 
		new State_US(RHODE_ISLAND_STRING, RI, RI_CODE);

	public static final String SOUTH_CAROLINA_STRING = "SOUTH_CAROLINA";
    public static final String SC = "SC"; 
	public static final long SC_CODE = 50;
	public static final State_US SOUTH_CAROLINA = 
		new State_US(SOUTH_CAROLINA_STRING, SC, SC_CODE);

	public static final String SOUTH_DAKOTA_STRING = "SOUTH_DAKOTA";
    public static final String SD = "SD"; 
	public static final long SD_CODE = 51;
	public static final State_US SOUTH_DAKOTA = 
		new State_US(SOUTH_DAKOTA_STRING, SD, SD_CODE);

	public static final String TENNESSEE_STRING = "TENNESSEE";
    public static final String TN = "TN"; 
	public static final long TN_CODE = 52;
	public static final State_US TENNESSEE = 
		new State_US(TENNESSEE_STRING, TN, TN_CODE);

	public static final String TEXAS_STRING = "TEXAS";
    public static final String TX = "TX"; 
	public static final long TX_CODE = 53;
	public static final State_US TEXAS = 
		new State_US(TEXAS_STRING, TX, TX_CODE);

	public static final String UTAH_STRING = "UTAH";
    public static final String UT = "UT"; 
	public static final long UT_CODE = 54;
	public static final State_US UTAH = 
		new State_US(UTAH_STRING, UT, UT_CODE);

	public static final String VERMONT_STRING = "VERMONT";
    public static final String VT = "VT"; 
	public static final long VT_CODE = 55;
	public static final State_US VERMONT = 
		new State_US(VERMONT_STRING, VT, VT_CODE);

	public static final String VIRGIN_ISLANDS_STRING = "VIRGIN_ISLANDS";
    public static final String VI = "VI"; 
	public static final long VI_CODE = 56;
	public static final State_US VIRGIN_ISLANDS = 
		new State_US(VIRGIN_ISLANDS_STRING, VI, VI_CODE);

	public static final String VIRGINIA_STRING = "VIRGINIA";
    public static final String VA = "VA"; 
	public static final long VA_CODE = 57;
	public static final State_US VIRGINIA = 
		new State_US(VIRGINIA_STRING, VA, VA_CODE);

	public static final String WASHINGTON_STRING = "WASHINGTON";
    public static final String WA = "WA"; 
	public static final long WA_CODE = 58;
	public static final State_US WASHINGTON = 
		new State_US(WASHINGTON_STRING, WA, WA_CODE);

	public static final String WEST_VIRGINIA_STRING = "WEST_VIRGINIA";
    public static final String WV = "WV"; 
	public static final long WV_CODE = 59;
	public static final State_US WEST_VIRGINIA = 
		new State_US(WEST_VIRGINIA_STRING, WV, WV_CODE);

	public static final String WISCONSIN_STRING = "WISCONSIN";
    public static final String WI = "WI"; 
	public static final long WI_CODE = 60;
	public static final State_US WISCONSIN = 
		new State_US(WISCONSIN_STRING, WI, WI_CODE);

	public static final String WYOMING_STRING = "WYOMING";
    public static final String WY = "WY"; 
	public static final long WY_CODE = 61;
	public static final State_US WYOMING = 
		new State_US(WYOMING_STRING, WY, WY_CODE);

	public static final String ARMED_FORCES_PACIFIC_STRING = "ARMED_FORCES_PACIFIC";
    public static final String AP = "AP"; 
	public static final long AP_CODE = 62;
	public static final State_US ARMED_FORCES_PACIFIC = 
		new State_US(ARMED_FORCES_PACIFIC_STRING, AP, AP_CODE);

	
    protected static final State_US[] statesArray = new State_US[]{
        ARMED_FORCES_AMERICAS_EXCEPT_CANADA,
        ARMED_FORCES_EUROPE_AFRICA_CANADA,
        ALABAMA,
        ALASKA,
        AMERICAN_SAMOA,
        ARIZONA,
        ARKANSAS,
        CALIFORNIA,
        COLORADO,
        CONNECTICUT,
        DELAWARE,
        DISTRICT_OF_COLUMBIA,
        FEDERATED_STATES_OF_MICRONESIA,
        FLORIDA,
        GEORGIA,
        GUAM,
        HAWAII,
        IDAHO,
        ILLINOIS,
        INDIANA,
        IOWA,
        KANSAS,
        KENTUCKY,
        LOUISIANA,
        MAINE,
        MARSHALL_ISLANDS,
        MARYLAND,
        MASSACHUSETTS,
        MICHIGAN,
        MINNESOTA,
        MISSISSIPPI,
        MISSOURI,
        MONTANA,
        NEBRASKA,
        NEVADA,
        NEW_HAMPSHIRE,
        NEW_JERSEY,
        NEW_MEXICO,
        NEW_YORK,
        NORTH_CAROLINA,
        NORTH_DAKOTA,
        NORTHERN_MARIANA_ISLANDS,
        OHIO,
        OKLAHOMA,
        OREGON,
        PALAU,
        PENNSYLVANIA,
        PUERTO_RICO,
        RHODE_ISLAND,
        SOUTH_CAROLINA,
        SOUTH_DAKOTA,
        TENNESSEE,
        TEXAS,
        UTAH,
        VERMONT,
        VIRGIN_ISLANDS,
        VIRGINIA,
        WASHINGTON,
        WEST_VIRGINIA,
        WISCONSIN,
        WYOMING,
        ARMED_FORCES_PACIFIC

    };

    protected static final Map states = new HashMap();
    protected static final Map statesByAbbreviation = new HashMap();
    protected static final Map statesByStateId = new HashMap();
    protected static final List statesSortedByAbbreviation = new ArrayList();
    protected static final List excludedStatesList = new ArrayList();

    static {
    	//Creating a list of excluded states which must not be shown in App while adding a new 
    	//Payee/editing a new payee. For more details refer to bug # 70049
    	excludedStatesList.add(ARMED_FORCES_AMERICAS_EXCEPT_CANADA);
    	excludedStatesList.add(ARMED_FORCES_EUROPE_AFRICA_CANADA);
    	excludedStatesList.add(FEDERATED_STATES_OF_MICRONESIA);
    	excludedStatesList.add(GUAM);
    	excludedStatesList.add(MARSHALL_ISLANDS);
    	excludedStatesList.add(NORTHERN_MARIANA_ISLANDS);
    	excludedStatesList.add(PALAU);
    	excludedStatesList.add(PUERTO_RICO);
    	excludedStatesList.add(VIRGIN_ISLANDS);
    	excludedStatesList.add(ARMED_FORCES_PACIFIC);
        for (int i = 0; i < statesArray.length; i++) {
            states.put(statesArray[i].getStateName(), statesArray[i]);
            statesByAbbreviation.put(statesArray[i].getAbbreviation(), statesArray[i]);
            statesByStateId.put(new Long(statesArray[i].getStateId()), statesArray[i]);
        }
        for (int i = 0; i < statesArray.length; i++) {
        	State_US state = statesArray[i];
        	//Put into statesSortedByAbbreviation only if its not present in excludedStatesList
        	if(!excludedStatesList.contains(state))
        		statesSortedByAbbreviation.add(state);
        }

//        statesSortedByAbbreviation.addAll(states.values());
        Collections.sort(statesSortedByAbbreviation);

    }

    protected String stateName;
    protected String abbreviation;
    protected long stateId;

    private static final String STATE_NAME = "stateName";

    protected State_US(String stateName, String abbreviation, long stateId) {
        this.stateName = stateName;
        this.abbreviation = abbreviation;
        this.stateId = stateId;
    }

    /**
     * Returns the name of the state. This is the unique name to identify a state in the Yodlee 5 Platform.
     * <p/>
     *
     * @return the name of the state
     */
    public String getStateName() {
        return stateName;
    }

    /**
     * Returns the abbreviation of the state.
     * <p/>
     *
     * @return the abbreviation of the state
     */
    public String getAbbreviation() {
        return abbreviation;
    }

    public long getStateId() {
        return stateId;
    }

    /**
     * Returns all the states in an array sorted by abbreviation.
     * <p/>
     *
     * @return all the states in an array sorted by abbreviation
     */
    public static State_US[] getAllStatesSortedByAbbreviation() {
        State_US[] array = new State_US[statesSortedByAbbreviation.size()];
        statesSortedByAbbreviation.toArray(array);
        return array;
    }

    /**
     * Returns the state object given an abbreviation.
     *
     * @return the state object given an abbreviation
     */
    public static State_US getStateByAbbreviation(String stateName) {
        return (State_US) statesByAbbreviation.get(stateName);
    }

    /**

     * Return all the US states as array

     * Including the excluded states also.

     */

    public static State_US[] getAllStatesByAbbreviation(){
      return statesArray;
    }

    /**
     * Returns the name of the state. This returns the same value as the
     * {@link #getStateName <code>getStateName</code>} method.
     * <p/>
     *
     * @return the name of the state
     */
    public String toString() {
        return stateName;
    }

    public boolean equals(Object obj) {
        if (obj instanceof State_US) {
            if (stateName == null) {
                return false;
            }
            State_US state = (State_US) obj;
            return stateName.equals(state.stateName);
        } else {
            return false;
        }
    }

    public int compareTo(Object o) {
        return abbreviation.compareTo(((State_US) o).abbreviation);
    }

    public static State_US getInstance(String stateName) {
        if (stateName == null) {
            return null;
        }
        return (State_US) states.get(stateName);
    }

    public static State_US getInstance(Long stateId) {
        if (stateId == null) {
            return null;
        }
        return (State_US) statesByStateId.get(stateId);
    }

    /**
     * <b>For internal use only</b>.
     * <p/>
     * Returns the fields for marshalling purposes.
     * Converts all primitives to equivalent Objects. Ex: int to Integer etc.
     * <p/>
     *
     * @return The fields to be marshalled in a map.
     */
    public Map getFieldsForMarshalling() {
        HashMap fieldsToMarshallMap = new HashMap();

        fieldsToMarshallMap.put(STATE_NAME, stateName);

        return fieldsToMarshallMap;
    }

    /**
     * <b>For internal use only</b>.
     * <p/>
     * Creates a <code>State_US</code> from a field map.
     * <p/>
     *
     * @param map the fields from which the <code>State_US</code> object is to be created.
     * @return the object created.
     */
    public static State_US createFromFields(Map map) {
        return getInstance((String) map.get(STATE_NAME));
    }
    
    public static void cleanup(){
      if (states != null){
        states.clear();
      }
      if (statesByAbbreviation != null){
        statesByAbbreviation.clear();
      }
      if (statesByStateId != null){
        statesByStateId.clear();
      }
      if (statesSortedByAbbreviation != null){
        statesSortedByAbbreviation.clear();
      }
      
    }

    public State_US() {
        super();
    }
    public void setStateName ( java.lang.String stateName) { 
    this.stateName = stateName;
    } 
    public void setAbbreviation ( java.lang.String abbreviation) { 
    this.abbreviation = abbreviation;
    } 
    public void setStateId ( long stateId) { 
    this.stateId = stateId;
    } 
}
