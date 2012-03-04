package com.yodlee.sampleapps.helper;
import java.util.Hashtable;
import java.util.ResourceBundle;
import java.util.Enumeration;


/**
 * A helper class for reading property settings.
 */
public class PropertyHelper {


    /** Fully qualified class name. */
    private static final String FQCN = PropertyHelper.class.getName();


    /** RCS version information. */
    public static final String RCS_ID = "${Id}";

    /**
     * Loads the properties from the given file name and returns a hashtable
     *
     * @param  propertyFileName name of the property file
     *
     * @return a hashtable of properties.
     */
    public static Hashtable loadProperties (String propertyFileName)
    {
        ResourceBundle configBundle;
        Enumeration enumerator;
        configBundle = ResourceBundle.getBundle (propertyFileName);
        enumerator = configBundle.getKeys();

        Hashtable properties = null;

       if (enumerator != null) {
            properties = new Hashtable ();
            while (enumerator.hasMoreElements ()) {
                String key = (String) enumerator.nextElement ();
                properties.put (key, configBundle.getString (key));
            }
        }
        return properties;
    }
}
