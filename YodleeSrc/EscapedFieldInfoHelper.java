/*
* Copyright (c) 2006,2007 Yodlee, Inc. All Rights Reserved.
*
* This software is the confidential and proprietary information of Yodlee, Inc. 
* Use is subject to license terms.
*/
package YodleeSrc;


import com.yodlee.soap.common.FieldInfo;
import com.yodlee.soap.common.FieldInfoMultiFixed;
import com.yodlee.soap.common.FieldInfoSingle;
import com.yodlee.soap.common.FieldInfoMultiVariable;


/**
 * A helper class that returns a tag-escaped value or a List of tag-escaped
 * values from a {@link com.yodlee.common.FieldInfo <code>FieldInfo</code>}
 * instance.
 * <p>
 * A {@link com.yodlee.common.FieldInfo <code>FieldInfo</code>} encapsulates
 * a name-value pair setting for a a particular field. From an
 * application or client perspective, the values stored in the
 * {@link com.yodlee.common.FieldInfo <code>FieldInfo</code>} instance
 * could come after processing a form on a web page. To prevent the
 * possibility of cross-side scripting attacks (whereby a user, maliciously
 * or otherwise) enter HTML tags where literal values are otherwise expected,
 * the {@link com.yodlee.common.FieldInfo <code>FieldInfo</code>} never
 * returns the values directly. Instead, the caller needs to use
 * a <code>FieldInfoHelper</code> class such as this, to obtain the values.
 * <p>
 * <p>
 * A application that is implemented with security in mind, especially if
 * it is web-based, needs to exclusively use the
 * <code>EscapedFieldInfoHelper</code> class to retrieve escaped values on the
 * application side, so it safely deals with tag-escaped end-user entries.
 */
public
class EscapedFieldInfoHelper
{    /**
     * Returns the value of this field. (Valid only for a single valued
     * field).
     * <p>
     * @return The field value.
     */
    public
    static String getValue (FieldInfo fieldInfo)
    {
        if (fieldInfo == null) {
            throw new
                IllegalArgumentException ("Null FieldInfo argument not legal");
        }
        if (fieldInfo instanceof FieldInfoSingle) {
            // TODO: escape!
            FieldInfoSingle fis = (FieldInfoSingle) fieldInfo;
            return escape (fis.getValue());
        }
        else {
            throw new UnsupportedOperationException
                    ("Invalid invocation - FieldInfo is multi-valued");
        }
    }


    /**
     * Returns the values of this field. (Valid only for a multi-valued
     * field).
     * <p>
     * @return The field values.
     */
    public
    static String[] getValues (FieldInfo fieldInfo)
    {
        String[] escapedValues = null;
        if (fieldInfo == null) {
            throw new
                IllegalArgumentException ("Null FieldInfo argument not legal");
        }
        else if (fieldInfo instanceof FieldInfoMultiFixed) {
            // TODO: escape!
            FieldInfoMultiFixed fimf = (FieldInfoMultiFixed) fieldInfo;
            if (fimf.getValues()!= null && fimf.getValues().getElements()!= null) {
                int valuesSize = fimf.getValues().getElements().length;
                escapedValues = new String[valuesSize];
                for (int i=0; i < valuesSize; i++) {
                    escapedValues[i] = escape (fimf.getValues().getElements(i));
                }
            }
            return escapedValues ;
        }
        else if (fieldInfo instanceof FieldInfoMultiVariable) {
            // TODO: escape!
            FieldInfoMultiVariable fimv = (FieldInfoMultiVariable) fieldInfo;
            if (fimv.getValues()!= null && fimv.getValues().getElements()!= null) {
                int valuesSize = fimv.getValues().getElements().length;
                escapedValues = new String[valuesSize];
                for (int i=0; i < valuesSize; i++) {
                    escapedValues[i] = escape (fimv.getValues().getElements(i));
                }
            }
            return escapedValues ;
        }
        else {
            throw new UnsupportedOperationException
                    ("Invalid invocation - FieldInfo is single-valued");
        }
    }

    /**
     * Whacked from struts source code. Cannot use struts methods in SDK classes
     * Therefore cut-pasting.
     * Given a string, this method replaces all occurrences of
     *  '<' with '&lt;', all occurrences of '>' with
     *  '&gt;', and (to handle cases that occur inside attribute
     *  values), all occurrences of double quotes with
     *  '&quot;' and all occurrences of '&' with '&amp;'.
     *  Without such filtering, an arbitrary string
     *  could not safely be inserted in a Web page.
     */

    public
    static String escape (String orig)
    {
    	String ret = null;
	    if (orig != null) {
	      orig = orig.replaceAll("&","&amp;");
	      orig = orig.replaceAll("&amp;amp;","&amp;");
	      orig = orig.replaceAll("\"","&quot;");
	      orig = orig.replaceAll("&amp;quot;","&quot");
	      orig = orig.replaceAll("'","&apos;");
	      orig = orig.replaceAll("&amp;apos;","&apos;");
	      orig = orig.replaceAll("<","&lt;");
	      orig = orig.replaceAll("&amp;lt;","&lt;");
	      orig = orig.replaceAll(">","&gt;");
	      orig = orig.replaceAll("&amp;gt","&gt;");
	      ret = new String(orig);
	    }
	    return ret;
    }
}



