/*
 * Copyright 2007 Yodlee, Inc.  All Rights Reserved.  Your use of this code 
 * requires a license from Yodlee.  Any such license to this code is 
 * restricted to evaluation/illustrative purposes only. It is not intended 
 * for use in a production environment, and Yodlee disclaims all warranties 
 * and/or support obligations concerning this code, regardless of the terms 
 * of any other agreements between Yodlee and you."
 */
package YodleeSrc;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.yodlee.soap.collections.ArrayOfArrayOfString;
import com.yodlee.soap.collections.ArrayOfString;
import com.yodlee.soap.collections.ArrayOflong;
import com.yodlee.soap.collections.common.ArrayOfFieldInputValidationRule;
import com.yodlee.soap.collections.common.ArrayOfFieldType;
import com.yodlee.soap.common.FieldInfo;
import com.yodlee.soap.common.FieldInfoMultiFixed;
import com.yodlee.soap.common.FieldInfoSingle;
import com.yodlee.soap.common.FieldInputValidationRule;
import com.yodlee.soap.common.FieldType;
import com.yodlee.soap.common.Form;
import com.yodlee.soap.common.UserContext;
import com.yodlee.soap.core.accountmanagement.AutoRegFieldInfoMultiFixed;
import com.yodlee.soap.core.accountmanagement.AutoRegFieldInfoSingle;

/**
 * Utility class to deal with operations on the Form object.
 * 
 * @author jordan
 */
public class FormUtil {
    public static final String INPUT_TYPE_URL                     =   "URL";
    public static final String INPUT_TYPE_TEXT                    =   "TEXT";
    public static final String INPUT_TYPE_PASSWORD                =   "PASSWORD";
    public static final String FIELDINFO_MULTI_FIXED_MAX_LENGTH   =   "40";
    
	/** Cannot instantiate this class. */
	private FormUtil() {
	}
	
    /**
     * Method to run through the Form Fields and generate and HTML form based on it.
     *
     * @param userContext
     * @param itemId
     * @param form
     * @param useDefaults
     * @param sampleCodeName
     * @param useDefaults indicates if the form should generate with default values
     */
    public static String writeFormHtml(UserContext userContext, Form form, long contentID) {
                
        StringBuffer htmlFormBuffer = new StringBuffer();

        //htmlFormBuffer.append("<form action=\"\">\n");       
        htmlFormBuffer.append("<form accept-charset=\"UTF-8\" action=\"/user_content_services?contentID=" + contentID + "\" data-remote=\"true\" method=\"post\">\n");
        
        htmlFormBuffer.append("<table>\n");
        
        for (FormFieldsVisitor visitor = 
                new FormFieldsVisitor(form, userContext);
                visitor.hasNext(); ) {
                    
            boolean needsLittleOr = visitor.needsLittleOr();
            boolean needsBigOr = visitor.needsBigOr();
            
            FieldInfo fieldInfo = visitor.getNextField();

            //htmlFormBuffer.append("<!-- \n");
            //htmlFormBuffer.append(fieldInfo.toString());
            //htmlFormBuffer.append("\n --> \n");
            
            if(needsBigOr) {
            	htmlFormBuffer.append("<tr><td colspan=\"2\" style=\"text-align:center;\">--- OR ---</td></tr>");
            }
            if(needsLittleOr) {
            	htmlFormBuffer.append("<tr><td style=\"text-align:center;\">or</td><td>&nbsp;</td></tr>");
            }
            
            if (fieldInfo instanceof FieldInfoSingle) {
                FieldInfoSingle fieldInfoSingle = 
                    (FieldInfoSingle) fieldInfo;
                
                FieldType fieldType = fieldInfoSingle.getFieldType();
            	
                htmlFormBuffer.append("<tr>\n<td>");
                
                htmlFormBuffer.append(fieldInfoSingle.getDisplayName());
                htmlFormBuffer.append("</td>");
                    
                String valueIdentifier = fieldInfoSingle.getValueIdentifier();
                ArrayOfString displayValidValues = 
                	fieldInfoSingle.getDisplayValidValues();
                ArrayOfString validValues = fieldInfoSingle.getValidValues();
                String value = FormFieldsVisitor.getValue(fieldInfo);
                if (value == null) value = "";

                htmlFormBuffer.append("<td>");
                
                // If valueValues is not null, then it is a dropdown, 
                // else it is a textbox
                if (FieldType.OPTIONS.equals(fieldType)
                		|| FieldType.RADIO.equals(fieldType)) {
                    // Is a drop down
                    htmlFormBuffer.append("<select id=\"");
                    htmlFormBuffer.append(valueIdentifier);
                    htmlFormBuffer.append("\" >\n");
                    
                    for (int i = 0; i < validValues.getElements().length; i++) {
                        htmlFormBuffer.append("\t<option values=\"");
                        htmlFormBuffer.append(validValues.getElements(i));
                        htmlFormBuffer.append("\" >");
                        htmlFormBuffer.append(displayValidValues.getElements(i));
                        htmlFormBuffer.append("</option>\n");
                    }
                    htmlFormBuffer.append("</select>\n");
                } else if (FieldType.LOGIN.equals(fieldType)
						|| FieldType.TEXT.equals(fieldType)
						|| FieldType.URL.equals(fieldType)
						|| FieldType.CUSTOM.equals(fieldType))
                	{
                	
                    htmlFormBuffer.append("<input type=\"text\" id=\"");
                    htmlFormBuffer.append(valueIdentifier);
                    htmlFormBuffer.append("\" value=\"");
                    htmlFormBuffer.append(value);
                    htmlFormBuffer.append("\" size=\"20\" maxlength=\"40\" />");
                } else if (FieldType.CHECKBOX.equals(fieldType)) {
                	htmlFormBuffer.append("<input type=\"checkbox\" id=\"");
                	htmlFormBuffer.append(valueIdentifier);
                    htmlFormBuffer.append("\" value=\"");
                    htmlFormBuffer.append(value);
                    htmlFormBuffer.append("\" />\n");
                } else if (FieldType.PASSWORD.equals(fieldType)) {	
                    htmlFormBuffer.append("<input type=\"password\" id=\"");
                    //htmlFormBuffer.append(valueIdentifier);
                    htmlFormBuffer.append("password");
                    htmlFormBuffer.append("\" value=\"");
                    htmlFormBuffer.append(value);
                    htmlFormBuffer.append("\" size=\"20\" maxlength=\"40\" />");
                } else {
                	throw new UnsupportedOperationException("Cannot handle "
                			+ "field of type [" + fieldInfoSingle.getFieldType() + "]");
                }
                
                htmlFormBuffer.append("</td>\n<td>");
                if(fieldInfo.getHelpText() != null){
                    htmlFormBuffer.append(htmlEncode(fieldInfo.getHelpText()));
                }else{
                    htmlFormBuffer.append("&nbsp;");
                }
                 htmlFormBuffer.append("</td>\n</tr>\n");
                
            } else if (fieldInfo instanceof FieldInfoMultiFixed) {
                htmlFormBuffer.append("<tr>\n<td>");
                htmlFormBuffer.append(fieldInfo.getDisplayName());
                htmlFormBuffer.append("</td>");

                FieldInfoMultiFixed fieldInfoMultiFixed = (FieldInfoMultiFixed) fieldInfo;
                System.out.println("FieldInfoMultiFixed: " + fieldInfoMultiFixed);
                ArrayOfString ids = fieldInfoMultiFixed.getValueIdentifiers();
                String elementName = null;
                ArrayOfArrayOfString validValues = fieldInfoMultiFixed.getValidValues();
                ArrayOfArrayOfString displayValidValues = fieldInfoMultiFixed.getDisplayValidValues();
                ArrayOfString valueMasks = fieldInfoMultiFixed.getValueMasks();
                System.out.println("Value masks: " + valueMasks);
                String[][] masks = new String[ids.getElements().length][];
                String[] values = FormFieldsVisitor.getValues(fieldInfoMultiFixed);
                ArrayOfFieldType fieldTypes = fieldInfoMultiFixed.getFieldTypes();

                int size = 20;
                if (ids.getElements().length > 1) size = 7 - ids.getElements().length;
                String fieldsize = "" + size;
                String maxlength = FIELDINFO_MULTI_FIXED_MAX_LENGTH;

                htmlFormBuffer.append("<td>");
                for (int i = 0; i < ids.getElements().length; i++) {
                    elementName = (String) ids.getElements(i);

                    // If valueValues is null, then it is a dropdown, else it is a textbox
                    if(FieldType.LOGIN.equals(fieldTypes.getElements(i))
                    		|| FieldType.TEXT.equals(fieldTypes.getElements(i)) 
                    		|| FieldType.URL.equals(fieldTypes.getElements(i))
                    		|| FieldType.CUSTOM.equals(fieldTypes.getElements(i))) {

                    	// Is a text field
                        String defaultValue = "";
                        if (values != null && values[i] != null) {
                            defaultValue = values[i];
                        }
                        htmlFormBuffer.append("<input type=\"text\" id=\"");
                        htmlFormBuffer.append(elementName);
                        htmlFormBuffer.append("\" value=\"");
                        htmlFormBuffer.append(defaultValue);
                        htmlFormBuffer.append("\" size=\"");
                        htmlFormBuffer.append(fieldsize);
                        htmlFormBuffer.append("\" maxlength=\"");
                        htmlFormBuffer.append(maxlength);
                        htmlFormBuffer.append("\" />\n");
                    } else if (FieldType.CHECKBOX.equals(fieldTypes.getElements(i))) {
                    	htmlFormBuffer.append("<input type=\"checkbox\" id=\"");
                    	htmlFormBuffer.append(elementName);
                        htmlFormBuffer.append("\" value=\"");
                        htmlFormBuffer.append(values[i]);
                        htmlFormBuffer.append("\" />\n");
                    } else if (FieldType.PASSWORD.equals(fieldTypes.getElements(i))) {
                        String defaultValue = "";
                        if (values != null && values[i] != null) {
                            defaultValue = values[i];
                        }
                        
                        htmlFormBuffer.append("<input type=\"password\" id=\"");
                        htmlFormBuffer.append(elementName);
                        htmlFormBuffer.append("\" value=\"");
                        //htmlFormBuffer.append(defaultValue);
                        htmlFormBuffer.append("password");
                        htmlFormBuffer.append("\" size=\"");
                        htmlFormBuffer.append(fieldsize);
                        htmlFormBuffer.append("\" maxlength=\"");
                        htmlFormBuffer.append(maxlength);
                        htmlFormBuffer.append("\" />\n");
                    } else if (FieldType.OPTIONS.equals(fieldTypes.getElements(i))
                    		|| FieldType.RADIO.equals(fieldTypes.getElements(i))) {
                        htmlFormBuffer.append("<select id=\"");
                        htmlFormBuffer.append(elementName);
                        htmlFormBuffer.append("\" >\n");

                        for (int j = 0; j < validValues.getElements(i).getElements().length; j++) {
                            htmlFormBuffer.append("\t<option values==\"");
                            htmlFormBuffer.append(validValues.getElements(i).getElements(j));
                            htmlFormBuffer.append("\" >");
                            htmlFormBuffer.append(displayValidValues.getElements(i).getElements(j));
                            htmlFormBuffer.append("</option>\n");
                        }
                        htmlFormBuffer.append("</select>\n");

                    } else {
	                	throw new UnsupportedOperationException("Cannot handle "
                			+ "field of type [" + fieldTypes.getElements(i) + "]");
                    }
                    
                }

                htmlFormBuffer.append("</td>\n");

                if(fieldInfo.getHelpText() != null){
                    htmlFormBuffer.append("<td>");
                    htmlFormBuffer.append(htmlEncode(fieldInfo.getHelpText()));
                    htmlFormBuffer.append("</td>\n</tr>\n");
                }else{
                    htmlFormBuffer.append("<td>&nbsp;</td>\n</tr>\n");
                }
            }
            
        }
        
        htmlFormBuffer.append("<tr>\n<td colspan=\"3\" align=\"center\"><input type=\"submit\" value=\"Submit\" id=\"addFilledForm\"/></td></tr>");
        htmlFormBuffer.append("</table>\n");
        htmlFormBuffer.append("</form>\n");

        /*try {
        	String filename = itemId + "_" + sampleCodeName + ".html";
            BufferedWriter out = new BufferedWriter(new FileWriter(filename));
            out.write(htmlFormBuffer.toString());
            out.close();

            System.out.println("Form has been written to [" + filename + "] for viewing.\n");
        } catch (IOException e) {
        }*/

        
        return htmlFormBuffer.toString();
    }
    
  
    /**
     * Helper method to html encode a string.  Used for dumping the form
     * output.
     *
     * @param s string to encode
     * @return encoded version
     */
    public static String htmlEncode(String s) {
       StringBuffer sb = new StringBuffer();
       int n = s.length();
       for (int i = 0; i < n; i++) {
          char c = s.charAt(i);
          switch (c) {
             case '<': sb.append("&lt;"); break;
             case '>': sb.append("&gt;"); break;
             case '&': sb.append("&amp;"); break;
             case '"': sb.append("&quot;"); break;
             default:  sb.append(c); break;
          }
       } 
       
        return sb.toString();
    }
    
    public static List getUserInputFieldInfoList(UserContext userContext, Form inputForm) {
    	List fieldInfoList = new ArrayList();

        FormFieldsVisitor visitor = new FormFieldsVisitor(inputForm, userContext);
        while(visitor.hasNext()) {
            if (visitor.needsBigOr()) {
                System.out.println("OR");
            }

            boolean needsLittleOr = visitor.needsLittleOr();
            
            FieldInfo fieldInfo = visitor.getNextField();

            if (fieldInfo instanceof FieldInfoSingle) {
            	FieldInfoSingle fieldInfoSingle = 
                	(FieldInfoSingle) fieldInfo;
                
                String valueIdentifier = fieldInfoSingle.getValueIdentifier();
                
                ArrayOfString displayValidValues = 
                	fieldInfoSingle.getDisplayValidValues ();
                
                ArrayOfString validValues = fieldInfoSingle.getValidValues();
                
                if(fieldInfoSingle instanceof AutoRegFieldInfoSingle) {
	                Long fieldErrorCode = ((AutoRegFieldInfoSingle)fieldInfoSingle).getFieldErrorCode();
	                String fieldErrorMsg = ((AutoRegFieldInfoSingle)fieldInfoSingle).getFieldErrorMessage();
	                
	                if(fieldErrorCode != null){
	                    System.out.println("(" 
	                    		+ fieldErrorMsg 
								+ " - " 
								+ fieldErrorCode 
								+ ")");
	                }
                }
                
                String value = FormFieldsVisitor.getValue(fieldInfo);                
                // If valueValues is null, then it is a dropdown, else it
                // is a textbox
                if (validValues != null) {
                    // Is a drop down
                    System.out.println("Drop down values for " 
                    		+ fieldInfo.getDisplayName() );
                    
                    for (int i = 0; i < validValues.getElements().length; i++) {
                        System.out.println ("\tValid value allowed is: " 
                        		+ validValues.getElements(i));
                    }
                }

                // Prompt user to enter value
                String inValue = 
                	promptUser(
                		fieldInfo, fieldInfoSingle.getValidationRules(), 
						value, 0, 0);
                
                if(inValue != null){
                	fieldInfoSingle.setValue(inValue);
                    fieldInfoList.add(fieldInfo);
                }
            } else if (fieldInfo instanceof FieldInfoMultiFixed) {
            	FieldInfoMultiFixed fieldInfoMultiFixed =
                	(FieldInfoMultiFixed) fieldInfo;
                
                ArrayOfString ids = fieldInfoMultiFixed.getValueIdentifiers();
                String elementName = null;
                ArrayOfArrayOfString validValues =
                	fieldInfoMultiFixed.getValidValues();
                
                ArrayOfArrayOfString displayValidValues = 
                	fieldInfoMultiFixed.getDisplayValidValues();
                
                ArrayOfString valueMasks = fieldInfoMultiFixed.getValueMasks();
                String[][] masks = new String[ids.getElements().length][];
                if(fieldInfoMultiFixed instanceof AutoRegFieldInfoMultiFixed) {
	                ArrayOflong fieldErrorCodes = ((AutoRegFieldInfoMultiFixed)fieldInfoMultiFixed).getFieldErrorCodes();
	                ArrayOfString fieldErrorMsgs = ((AutoRegFieldInfoMultiFixed)fieldInfoMultiFixed).getFieldErrorMessages();
	                for(int i=0; i<fieldErrorCodes.getElements().length; i++){
	                    //if(fieldErrorCodes[i] != null){
						System.out.println("(" + fieldErrorMsgs.getElements(i) + " - "
								+ fieldErrorCodes.getElements(i) + ")");
	                    //}	
	                }
                }
                
                String[] values = 
                	FormFieldsVisitor.getValues(fieldInfoMultiFixed);

                int size = 20;
                if (ids.getElements().length > 1) size = 7 - ids.getElements().length;
                String fieldsize = "" + size;
                String maxlength = FIELDINFO_MULTI_FIXED_MAX_LENGTH;

                for (int i = 0; i < ids.getElements().length; i++) {
                    elementName = (String) ids.getElements(i);

                    // If valueValues is null, then it is a dropdown, 
                    // else it is a textbox
                    if (validValues.getElements(i) == null || validValues.getElements(i).getElements().length == 0) {
                        // Is a text field
                        String defaultValue = "";
                        if (values != null && values[i] != null) {
                            defaultValue = values[i];
                        }
                    }else{
                        // Is a drop down
                        System.out.println("Drop down values for " 
                        		+ fieldInfo.getDisplayName() );

                        for (int j = 0; j < validValues.getElements(i).getElements().length; j++) {
                            System.out.println ("\tVfield #" 
                            		+ i 
									+ " displayValidValue: " 
									+ displayValidValues.getElements(i).getElements(j) 
									+ " validValue: " 
									+ validValues.getElements(i).getElements(j));
                        }

                    }
                    
                    // Prompt user to enter value
                    // Pass in which index of the multiFixed it is on as well as the max.
                    String inValue = 
                    	promptUser(fieldInfo, 
                    		(fieldInfoMultiFixed.getValidationRules()).getElements(i),
							null, i+1, ids.getElements().length);
                    
                    values[i] = inValue;
                }
                if (values != null) {
                	// check to see that there is no null value in the array
                	boolean allNonNull = true;
                	for(int valuesIndex = 0; valuesIndex < values.length; valuesIndex++) {
                		if(values[valuesIndex] == null) {
                			allNonNull = false;
                			break;
                		}
                	}
                	
                	if(allNonNull) {
	                    ((FieldInfoMultiFixed)fieldInfo).setValues (new ArrayOfString(values));
	                    fieldInfoList.add (fieldInfo);
                	}
                }

            }
            System.out.println("");
        }

        // Convert List to Array
        FieldInfo[] fieldInfos = new FieldInfo[fieldInfoList.size()];
        for(int i=0; i<fieldInfoList.size(); i++){
            fieldInfos[i] = (FieldInfo)fieldInfoList.get(i);
        }

    	
    	return fieldInfoList;
    }
    
    /**
     * Prompt User to enter value
     * 
     * @param fieldInfo
     * @param rules
     * @param defaultValue
     * @param multiFixedIndex indicates which index of a multiFixed form field it is on
     * @param multiFixedMax indicates the maximum number of fields in the multiFixed
     * @return String user entered
     */
     public static String promptUser(FieldInfo fieldInfo,
			ArrayOfFieldInputValidationRule rules,
                        String defaultValue,
                        int multiFixedIndex,
                        int multiFixedMax) {
                            
		boolean incomplete = true;
		String inValue = "";
		while (incomplete) {
			// Display any validation rules to the user
			if(fieldInfo.getHelpText() != null) {
				System.out.print(fieldInfo.getHelpText() + " ");
			} else {
                System.out.print("No help for field. ");
            }
			
			if(rules != null) {
				for(int i = 0; i < rules.getElements().length; i++) {
				    System.out.print(" ( "
					    + rules.getElements(i).getValidationExpression() + " ) ");
				}
			}
            System.out.println("");
			
			if(multiFixedMax > 0) {
			    System.out.print("(" + multiFixedIndex + " of " + multiFixedMax + ") ");
			}
			// Display default value?
			if (defaultValue != null) {
				System.out.print("Please Enter " + fieldInfo.getDisplayName() + " [" +defaultValue + "] : ");
				inValue = IOUtils.readStr(defaultValue);
			} else {
				System.out.print("Please Enter " + fieldInfo.getDisplayName() + ": ");
				inValue = IOUtils.readStr();
			}
            
			// Check value against validation Expression
			if (rules == null) {
				incomplete = false;
			} else {
				for (int i = 0; i < rules.getElements().length; i++) {
					if (!matchRegularExpression(rules.getElements(i).getValidationExpression(),
							inValue)) {
						System.out.println(rules.getElements(i).getErrorMessage() + " ( "
								+ rules.getElements(i).getValidationExpression() + " ) ");
						continue;
					} else {
						incomplete = false;
					}
				}				
			}
		}
		return inValue;
	}

     /**
      * Math Regular Expression
      * 
      * @param expression
      * @param input
      * @return true or false
      */
     protected static final boolean matchRegularExpression(String expression, String input) {
         if (expression == null) {
             return true;
         }
         try {
             //Pattern pattern = new Pattern(expression); (9/5/2006 - Doug: updated to use java regexp, not tested)
             Pattern pattern = Pattern.compile(expression);
             Matcher matcher = pattern.matcher(input);
             return matcher.matches();
         } catch (Exception ex) {
             return true;
         }
     }

}
