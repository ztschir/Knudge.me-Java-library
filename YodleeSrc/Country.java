/*
* Copyright (c) 2006,2007 Yodlee, Inc. All Rights Reserved.
*
* This software is the confidential and proprietary information of Yodlee, Inc. 
* Use is subject to license terms.
*/
package YodleeSrc;

import java.util.Map;
import java.util.HashMap;

/**
 * <code>Country</code> is an enumeration class for representing countries of the world.
 * <p>
 * @see com.yodlee.common.Address
 */
public class Country implements java.io.Serializable {

    /**
     * Represents the United States.
     */
    public static final Country US = new Country(1, "US");

    /**
     * Represents Great Britain.
     */
    public static final Country GB = new Country(2, "GB");

    /**
     * Represents Australia.
     */
    public static final Country AU = new Country(3, "AU");

    /**
     * Represents Belgium.
     */
    public static final Country BE = new Country(4, "BE");

    /**
     * Represents China.
     */
    public static final Country CN = new Country(5, "CN");
    /**
     * Represents India.
     */
    public static final Country IN = new Country(6, "IN");
    /**
     * Represents Canada.
     */
    public static final Country CA = new Country(7, "CA");
    
    /**
     * Represents Spain.
     */
    public static final Country ES = new Country(8, "ES");
    
    
    protected static final Country[] countryArray = {US, GB, AU, BE, CN ,IN, CA, ES};
    protected static final Map countries = new HashMap();
    protected static final Map countriesByCountryId = new HashMap();

    static {
        for (int i = 0; i < countryArray.length; i++) {
            countries.put(countryArray[i].getCountryCode(), countryArray[i]);
            countriesByCountryId.put(new Long(countryArray[i].getCountryId()), countryArray[i]);
        }
    }
    protected String countryCode;
    protected long countryId;

    private static final String COUNTRY_CODE = "countryCode";

    protected Country(long countryId, String countryCode) {
        this.countryId = countryId;
        this.countryCode = countryCode;
    }

    /**
     * Returns the country code.
     * <p>
     * @return  the country code
     */
    public String getCountryCode() {
        return countryCode;
    }

    public long getCountryId() {
        return countryId;
    }

    public static Country getInstance(String countryCode) {
        if (countryCode == null) {
            return null;
        }
        Country country = (Country) countries.get(countryCode);
        return country;
    }

    public static Country getInstance(Long countryId) {
        if (countryId == null) {
            return null;
        }
        return (Country) countriesByCountryId.get(countryId);
    }

    /**
     * Returns the country code. This returns the same value as the
     * {@link #getCountryCode <code>getCountryCode</code>} method.
     * <p>
     * @return  the country code
     */
    public String toString() {
        return countryCode;
    }

    public boolean equals(Object obj) {
        if (obj instanceof Country) {
            if (countryCode == null) {
                return false;
            }
            Country country = (Country) obj;
            return countryCode.equals(country.countryCode);
        } else {
            return false;
        }
    }

    /**
     * <b>For internal use only</b>.
     * <p>
     * Returns the fields for marshalling purposes.
     * Converts all primitives to equivalent Objects. Ex: int to Integer etc.
     * <p>
     * @return The fields to be marshalled in a map.
     */
    public Map getFieldsForMarshalling() {
        Map map = new HashMap();

        map.put(COUNTRY_CODE, countryCode);

        return map;
    }

    /**
     * <b>For internal use only</b>.
     * <p>
     * Creates an object of this class from a field map.
     * <p>
     * @param map the fields from which the object is to be created.
     * @return    the object created.
     */
    public static Country createFromFields(Map map) {
        return getInstance((String) map.get(COUNTRY_CODE));
    }
    public Country() {
        super();
    }
    public void setCountryCode ( java.lang.String countryCode) { 
    this.countryCode = countryCode;
    } 
    public void setCountryId ( long countryId) { 
    this.countryId = countryId;
    } 
}
