package com.KnudgeMeYodlee;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import YodleeSrc.FormFieldsVisitor;
import YodleeSrc.FormUtil;

import com.yodlee.soap.collections.ArrayOfString;
import com.yodlee.soap.common.CobrandContext;
import com.yodlee.soap.common.FieldInfo;
import com.yodlee.soap.common.FieldInfoSingle;
import com.yodlee.soap.common.FieldType;
import com.yodlee.soap.common.Form;
import com.yodlee.soap.common.UserContext;
import com.yodlee.soap.core.accountmanagement.AutoRegFieldInfoSingle;


public class KnudgeMeYodlee {
	public static void main(String[] args) {

	// System.setProperty("com.yodlee.soap.services.url","https://64.41.182.230/yodsoap/services");

	// YodleeAggregation agg = new YodleeAggregation();

	// UserContext user = agg.loginUser(agg.getCobrandContext());

	// agg.addItem(user, agg.getCobrandContext());

	// user.yodleeUserContext = agg.registerUser(agg.getCobrandContext(),
	// username, email, password);

	 KnudgeMeYodlee mainService = new KnudgeMeYodlee();
	 	
	 
	 String HTML = mainService.getHTMLForm(25, 3697);
	 System.out.println(HTML);
	 //mainService.parseHTML(HTML);
	 System.out.println(mainService.addItem(25, 3697, HTML));
	 
	 //int stuff = mainService.registrarYodleeUser("123zachknudgeme@zach.com", "test123");
	 
	 //System.out.println(stuff);


	// mainService.registrarYodleeUser("ZachTest123", "ZachTest@123.com",
	// "Test123");
	//int stuff = mainService.loginYodleeUser("zachtestingsfsdf@zach.com", "test123");
	//System.out.println(stuff);
	// mainService.importContentServiceInfo();
	// mainService.importLoginForms();
		
		 
	 }

	public static int registrarYodleeUser(String email, String password) {
		System.setProperty("com.yodlee.soap.services.url",
				"https://64.41.182.230/yodsoap/services");
		YodleeAggregation agg = new YodleeAggregation();
		UserModel user = new UserModel();
		
		String username = generateUsername(email);
		user.yodleeUserContext = agg.registerUser(agg.getCobrandContext(), username, email, password);
		user.email = email;
		user.username = username;
		if (user.yodleeUserContext != null) {
			GeneralDatabaseAccessor db = new GeneralDatabaseAccessor();
			db.addUser(user, password);
			Response userResponse = db.loginUser(email, password);
			System.out.println(userResponse.errorMessage);
			if(userResponse.user != null && userResponse.isSuccessful)
				return userResponse.user.id;
		}
		
		return -1;
	}
	
	private static String generateUsername(String email){
		DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		Date date = new Date();
		return email.substring(0, email.indexOf("@")) + dateFormat.format(date).toString();
	}

	public static int loginYodleeUser(String email, String password) {
		System.setProperty("com.yodlee.soap.services.url",
				"https://64.41.182.230/yodsoap/services");
		GeneralDatabaseAccessor db = new GeneralDatabaseAccessor();
		Response response = db.loginUser(email, password);
		System.out.println(response.errorMessage);
		if (response.user != null && response.isSuccessful) {
			return response.user.id;
		} else {
			return -1;
		}
	}

	public static void importContentServiceInfo() {
		System.setProperty("com.yodlee.soap.services.url",
				"https://64.41.182.230/yodsoap/services");
		GeneralDatabaseAccessor db = new GeneralDatabaseAccessor();
		YodleeAggregation agg = new YodleeAggregation();
		db.importContentServiceInfos(agg.getContentServiceInfos(agg
				.getCobrandContext()));
	}

	public static String getHTMLForm(long UserID, long contentID) {
		System.setProperty("com.yodlee.soap.services.url",
				"https://64.41.182.230/yodsoap/services");
		GeneralDatabaseAccessor db = new GeneralDatabaseAccessor();
		YodleeAggregation agg = new YodleeAggregation();
		//UserContext userContext = db.getUserModel(UserID).user.yodleeUserContext;
		UserModel user = db.getUserModel(UserID).user;
		
		UserContext userContext = agg.loginUser(agg.getCobrandContext(), user.username, user.unencryptedPassword);
		return FormUtil.writeFormHtml(userContext,
				agg.getLoginFormForContentService(userContext, new Long(
						contentID)), new Long(contentID));
	}

	public static void importLoginForms() {
		System.setProperty("com.yodlee.soap.services.url",
				"https://64.41.182.230/yodsoap/services");
		GeneralDatabaseAccessor db = new GeneralDatabaseAccessor();
		YodleeAggregation agg = new YodleeAggregation();
		agg.getAllLoginForms(agg.getCobrandContext());
	}

	public static boolean addItem(long UserID, long contentServiceId, String rawHTML){
		System.setProperty("com.yodlee.soap.services.url",
				"https://64.41.182.230/yodsoap/services");
		GeneralDatabaseAccessor db = new GeneralDatabaseAccessor();
		YodleeAggregation agg = new YodleeAggregation();
		
		//UserContext userContext = db.getUserModel(UserID).user.yodleeUserContext;
		UserModel user = db.getUserModel(UserID).user;
		
		UserContext userContext = agg.loginUser(agg.getCobrandContext(), user.username, user.unencryptedPassword);
		return agg.addItem(userContext, agg.getCobrandContext(), contentServiceId, 
				KnudgeMeYodlee.parseHTML(rawHTML, userContext, agg.getLoginFormForContentService(userContext, contentServiceId)));
	}
	
	public static List parseHTML(String unparsedHTML, UserContext userContext, Form inputForm){
		List fieldInfoList = new ArrayList();
    	Document soupParser = Jsoup.parse(unparsedHTML);
    	FormFieldsVisitor visitor = new FormFieldsVisitor(inputForm, userContext);
    	
    	List<String> logins = new LinkedList();
    	List<String> passwords = new LinkedList();
    	List<String> texts = new LinkedList();
    	List<String> urls = new LinkedList();
    	List<String> customs = new LinkedList();
    	List<String> checkboxes = new LinkedList();
    	
    	Elements Elements = soupParser.getAllElements();
    	for(Element elm: Elements){
    		
    		if(elm.id().equals("LOGIN")){
    			if(elm.val() != null)
    				logins.add(elm.val());
    		}
    		else if(elm.id().equals("URL")){
    			if(elm.val() != null)
    				urls.add(elm.val());
    		}
    		else if(elm.id().equals("TEXT")){
    			if(elm.val() != null)
    				texts.add(elm.val());
    		}    		
    		else if(elm.id().equals("CUSTOM")){
    			if(elm.val() != null)
    				customs.add(elm.val());
    		}
    		else if(elm.id().equals("CHECKBOX")){
    			if(elm.val() != null)
    				checkboxes.add(elm.val());
    		}
    		else if(elm.id().equals("password")){
    			if(elm.val() != null)
    				passwords.add(elm.val());
    		}
    		
    	}
    	
        while(visitor.hasNext()) {

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
   
                String inValue = null;
                if(valueIdentifier.equals(FieldType.LOGIN.toString())){
                	if(!logins.isEmpty())
                		inValue = logins.remove(0);
                }
                else if(valueIdentifier.equals(FieldType.URL.toString())){
                	if(!urls.isEmpty())
                		inValue = urls.remove(0);
                }
                else if(valueIdentifier.equals(FieldType.TEXT.toString())){
                	if(!texts.isEmpty())
                		inValue = texts.remove(0);
                }
                else if(valueIdentifier.equals(FieldType.CUSTOM.toString())){
                	if(!customs.isEmpty())
                		inValue = customs.remove(0);
                }
                else if(valueIdentifier.equals(FieldType.CHECKBOX.toString())){
                	if(!checkboxes.isEmpty())
                		inValue = checkboxes.remove(0);
                }
                else if(valueIdentifier.equals(FieldType.PASSWORD.toString())){
                	if(!passwords.isEmpty())
                		inValue = passwords.remove(0);
                }
                

                if(inValue != null){
                	fieldInfoSingle.setValue(inValue);
                    fieldInfoList.add(fieldInfo);
                }
            }
        }
    	
    	
    	return fieldInfoList;
	}

}