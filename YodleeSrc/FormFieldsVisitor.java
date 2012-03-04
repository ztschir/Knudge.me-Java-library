/*
 * Copyright 2009 Yodlee, Inc.  All Rights Reserved.  Your use of this code 
 * requires a license from Yodlee.  Any such license to this code is 
 * restricted to evaluation/illustrative purposes only. It is not intended 
 * for use in a production environment, and Yodlee disclaims all warranties 
 * and/or support obligations concerning this code, regardless of the terms 
 * of any other agreements between Yodlee and you."
 */
package YodleeSrc;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import com.yodlee.soap.common.CobrandContext;
import com.yodlee.soap.common.FieldInfo;
import com.yodlee.soap.common.FieldInfoChoice;
import com.yodlee.soap.common.FieldInfoMultiFixed;
import com.yodlee.soap.common.FieldInfoMultiVariable;
import com.yodlee.soap.common.FieldInfoSingle;
import com.yodlee.soap.common.FieldType;
import com.yodlee.soap.common.Form;
import com.yodlee.soap.common.FormComponent;
import com.yodlee.soap.common.FormConjunctionOperator;
import com.yodlee.soap.core.accountmanagement.AutoRegFieldInfoSingle;

/**
 * Used to traverse the entire form and provide a flattened
 * view of the form for iteration.
 * 
 * Includes basic support for OR conjuctions in the form.
 * 
 * @author doug
 */
public class FormFieldsVisitor {

	/** Holds the list of form fields. */
    private LinkedList fieldInfoQueue = new LinkedList();
    
    /** 
     * Set of elements that are the start of the second group in a big or. 
     */
    private Set needsBigOr = new HashSet();
    
    /** Set of elements that are in a little-or group. */
    private Set needsLittleOr = new HashSet();
    
    /** Holds the cobrandContext. Not used. */
    private CobrandContext cobrandContext;

    /**
     * Basic constructor will generate the form vistor.
     * 
     * @param form the form to wrap with a vistor
     * @param cobrandContext the context this form came from
     */
    public FormFieldsVisitor(Form form, CobrandContext cobrandContext) {
        this.cobrandContext = cobrandContext;
        populateQueue(form);
    }

    /**
     * Populated the queue for the given form.  This can be
     * called recursively for forms that contain subforms with
     * OR conjunctions.
     * 
     * @param form the form to cycle through and populate the queue
     */
    private void populateQueue(Form form) {
        //FormComponent[] children = (FormComponent[])form.getComponentList();    	
    	
    	com.yodlee.soap.collections.List childrenList = form.getComponentList();
    	Object[] children = null;
    	
    	if (childrenList != null){
    		children = childrenList.getElements();
    	}
        
        // Find the FieldInfo where we need to put the big OR right in front.
        // These are added intot he needsBigOr set.
    	FormComponent formComponent;
        if (FormConjunctionOperator.OR.equals(form.getConjunctionOp())) {
            for (int i = 1; i < children.length; i++) {
            	formComponent = (FormComponent)children[i];
                if (formComponent instanceof FieldInfo) {
                    needsBigOr.add(formComponent);
                } else if (formComponent instanceof Form) {
                    FieldInfo found = findLeftMostFieldInfo((Form) formComponent);
                    if (found != null) {
                        needsBigOr.add(found);
                    }
                }
            }
        }
        
        // Iterate through all the fields recursing as needed for
        // Form and FieldInfoChoice
        for (int i = 0; i < children.length; i++) {
            if (children[i] instanceof FieldInfo) {
            	// For FieldInfo, add them straight into the queue
            	// but mark them as little-ors if needed.
            	populateQueue((FieldInfo)children[i]);
            	
                if (FormConjunctionOperator.OR.equals(form.getConjunctionOp()) && i != 0) {
                    needsLittleOr.add(children[i]);
                }            	
            } /*else if (children[i] instanceof FieldInfoChoice) {
            	// For FieldInfoChoice, add all of the child components to
            	// the queue, and add a little or before each child component
            	// after the first.
            	com.yodlee.soap.collections.List fieldInfos = ((FieldInfoChoice) children[i]).getFieldInfoList();
				FieldInfo[] fieldInfoArray = null;
            	
            	if (fieldInfos != null) {
            		fieldInfoArray = (FieldInfo[])fieldInfos.getElements();
            	}
						
            	
            	for(int j = 0; j < fieldInfoArray.length; j++) {
            		populateQueue(fieldInfoArray[j]);
            		if(j > 0) {
            			needsLittleOr.add(fieldInfoArray[j]);
            		}
            	}
            	
            } */
            else if (children[i] instanceof FieldInfoChoice) {
                // For FieldInfoChoice, add all of the child components to
                // the queue, and add a little or before each child component
                // after the first.
                FieldInfoChoice choice = (FieldInfoChoice) children[i];
                com.yodlee.soap.collections.List fieldInfos = choice.getFieldInfoList();
                FieldInfo[] fieldInfoArray =
                new FieldInfo[fieldInfos.getElements().length];
                Object[] obj = fieldInfos.getElements();
                if (fieldInfos != null) {
                    for (int x=0;x<obj.length; x++){
                        fieldInfoArray[x] = (FieldInfo)obj[x];
                    }
                }
                //fieldInfoArray = (FieldInfo[])fieldInfos.getElements();

           } else if (children[i] instanceof Form) {
            	// For a form, recurse
                populateQueue((Form) children[i]);
            }
        }
    }
    
    /**
     * Add the field info to the queue.  If it is a password type,
     * the additionally add a second field for a password verify
     * to the queue
     * 
     * @param fieldInfo the info to add to the queue
     */
    private void populateQueue(FieldInfo fieldInfo) {
        fieldInfoQueue.add(fieldInfo);
        
        if (fieldInfo instanceof AutoRegFieldInfoSingle) {
            AutoRegFieldInfoSingle field = (AutoRegFieldInfoSingle) fieldInfo;
            FieldType fieldType = field.getFieldType();
            String value = getValue(field);
            
            // If the field is a password field, then add a verify field
            // into the system.
            if (fieldType != null && fieldType.equals(FieldType.PASSWORD)) {
            	AutoRegFieldInfoSingle verify = new AutoRegFieldInfoSingle();
            	verify.setName("");
            	verify.setDisplayName("Verify " + field.getDisplayName());
            	verify.setIsEditable(true);
            	verify.setIsOptional(field.isIsOptional());
            	verify.setIsEscaped(field.isIsEscaped());
            	verify.setHelpText(field.getHelpText());
            	verify.setIsOptionalMFA(field.isIsOptionalMFA());
            	verify.setIsMFA(field.isIsMFA());
            	verify.setDefaultValue(field.getDefaultValue());
            	verify.setValue(field.getValue());
            	verify.setValidValues(field.getValidValues());
            	verify.setDisplayValidValues(field.getDisplayValidValues());
            	verify.setValueIdentifier(field.getValueIdentifier());
            	verify.setValueMask(field.getValueMask());
            	verify.setFieldType(field.getFieldType());
            	verify.setValidationRules(field.getValidationRules());
            	verify.setSize(field.getSize());
            	verify.setMaxlength(field.getMaxlength());
            	verify.setUserProfileMappingExpression(field.getUserProfileMappingExpression());
            	verify.setFieldErrorCode(field.getFieldErrorCode());
            	verify.setFieldErrorMessage(field.getFieldErrorMessage());
            	verify.setAutoGeneratable(field.isAutoGeneratable());
            	verify.setOkToAutoGenerate(field.isOkToAutoGenerate());
            	
            	                       
                if(value != null){
                	verify.setValue(value);
                }
                
                fieldInfoQueue.add(verify);
            }
        } else if (fieldInfo instanceof FieldInfoSingle) {
            FieldInfoSingle field = (FieldInfoSingle) fieldInfo;
            FieldType fieldType = field.getFieldType();
            String value = getValue(field); 

            // If the field is a password field, then add a verify field
            // into the system.
            if (fieldType != null && fieldType.equals(FieldType.PASSWORD)) {
            	FieldInfoSingle verify = new FieldInfoSingle();
            	verify.setName("");
            	verify.setDisplayName("Verify " + field.getDisplayName());
            	verify.setIsEditable(true);
            	verify.setIsOptional(field.isIsOptional());
            	verify.setIsEscaped(field.isIsEscaped());
            	verify.setHelpText(field.getHelpText());
            	verify.setIsOptionalMFA(field.isIsOptionalMFA());
            	verify.setIsMFA(field.isIsMFA());
            	verify.setDefaultValue(field.getDefaultValue());
            	verify.setValue(field.getValue());
            	verify.setValidValues(field.getValidValues());
            	verify.setDisplayValidValues(field.getDisplayValidValues());
            	verify.setValueIdentifier(field.getValueIdentifier());
            	verify.setValueMask(field.getValueMask());
            	verify.setFieldType(field.getFieldType());
            	verify.setValidationRules(field.getValidationRules());
            	verify.setSize(field.getSize());
            	verify.setMaxlength(field.getMaxlength());
            	verify.setUserProfileMappingExpression(field.getUserProfileMappingExpression());
            	verify.setFieldErrorCode(field.getFieldErrorCode());
            	verify.setFieldErrorMessage(field.getFieldErrorMessage());

                if(value != null){
                	verify.setValue(value);
                }
                fieldInfoQueue.add(verify);
            }
        }
    }
    
    
    
    
    /**
     * Determines the first FieldInfo object that inside the OR that is
     * returned by the visitor.
     * 
     * @param form the form to search for the leftmost component of
     * @return the left most component
     */
    private FieldInfo findLeftMostFieldInfo(Form form) {
    	
    	com.yodlee.soap.collections.List childrenList = form.getComponentList();
    	FormComponent[] children = null;
    	if (childrenList != null){
    		children  = (FormComponent[])childrenList.getElements();
    	}
    	FieldInfo leftMostFieldInfo = null;
    	
    	if(children.length > 0) {
    		FormComponent leftMostFormComponent = children[0];
        
            if (leftMostFormComponent instanceof FieldInfo) {
            	// If the left most component is a FieldInfo, then its done. 
            	leftMostFieldInfo = (FieldInfo) leftMostFormComponent;
                
            } else if(leftMostFormComponent instanceof FieldInfoChoice) {
                // If the left most component is a FieldInfoChoice, then
                // retrieve it's left-most component
            	
            	com.yodlee.soap.collections.List  fieldInfos = ((FieldInfoChoice)leftMostFormComponent).getFieldInfoList();
            	FieldInfo[] fieldInfoArray = null;
            	
            	if (fieldInfos != null){
            		fieldInfoArray = (FieldInfo[])fieldInfos.getElements();
            	}
            	if(fieldInfoArray.length > 0) {
            		leftMostFieldInfo = fieldInfoArray[0];
            	}
            	
            } else if (leftMostFormComponent instanceof Form) {
                // If the left most component is a subform, recurse
            	leftMostFieldInfo = findLeftMostFieldInfo((Form) leftMostFormComponent);
            } else {
            	throw new IllegalStateException("Unknown how to process "
            			+ "FormComponent of type [" 
						+ leftMostFormComponent.getClass().getName() 
						+ "]" );
            }
    	}
        
        return leftMostFieldInfo;
    }

    /**
     * Indicates if there are additional fields in the queue
     * 
     * @return true if there are more fields
     */
    public boolean hasNext() {
        return fieldInfoQueue.size() != 0;
    }
    
    /**
     * Indicates if the current field is the start of a second
     * part of a big-or conjunction.
     * 
     * @return if it is a big or
     */
    public boolean needsBigOr() {
        return needsBigOr.contains(fieldInfoQueue.getFirst());
    }
    
    /**
     * Indicates if the current field is the start of a later part
     * of a little-or conjunction
     * 
     * @return if it is a little or
     */
    public boolean needsLittleOr() {
        return needsLittleOr.contains(fieldInfoQueue.getFirst());
    }
    
    /**
     * Returns the next field in the visitor.
     * 
     * @return next field
     */
    public FieldInfo getNextField() {
        return (FieldInfo) fieldInfoQueue.removeFirst();
    }
    
    /**
     * Returns the value of this field. (Valid only for a single valued
     * field).
     * <p>
     * @return The field value.
     */
    public static String getValue (FieldInfo fieldInfo)
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
    private static String escape (String orig)
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
            if (fimf.getValues() != null && fimf.getValues().getElements() != null) {
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
            if (fimv.getValues() != null && fimv.getValues().getElements() != null) {
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
}
