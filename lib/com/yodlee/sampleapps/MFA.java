/*
 * Copyright 2009 Yodlee, Inc.  All Rights Reserved.  Your use of this code 
 * requires a license from Yodlee.  Any such license to this code is 
 * restricted to evaluation/illustrative purposes only. It is not intended 
 * for use in a production environment, and Yodlee disclaims all warranties 
 * and/or support obligations concerning this code, regardless of the terms 
 * of any other agreements between Yodlee and you."
 */
package com.yodlee.sampleapps;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.xml.rpc.ServiceException;

import com.yodlee.sampleapps.helper.IOUtils;
import com.yodlee.soap.collections.core.mfarefresh.ArrayOfQuesAndAnswerDetails;
import com.yodlee.soap.collections.core.mfarefresh.ArrayOfQuestionAndAnswerValues;
import com.yodlee.soap.common.UserContext;
import com.yodlee.soap.core.mfarefresh.ImageFieldInfo;
import com.yodlee.soap.core.mfarefresh.MFAFieldInfo;
import com.yodlee.soap.core.mfarefresh.MFAImageResponse;
import com.yodlee.soap.core.mfarefresh.MFAQuesAnsResponse;
import com.yodlee.soap.core.mfarefresh.MFARefreshInfo;
import com.yodlee.soap.core.mfarefresh.MFATokenResponse;
import com.yodlee.soap.core.mfarefresh.MultiQuesMultiAnswerOptionsValues;
import com.yodlee.soap.core.mfarefresh.MultiQuesOptionsSingleAnswerValues;
import com.yodlee.soap.core.mfarefresh.QuesAndAnswerDetails;
import com.yodlee.soap.core.mfarefresh.QuestionAndAnswerValues;
import com.yodlee.soap.core.mfarefresh.SecurityQuestionFieldInfo;
import com.yodlee.soap.core.mfarefresh.SingleQuesMultiAnswerOptionsValues;
import com.yodlee.soap.core.mfarefresh.SingleQuesSingleAnswerValues;
import com.yodlee.soap.core.mfarefresh.TokenIdFieldInfo;
import com.yodlee.soap.core.refresh.refresh.Refresh;
import com.yodlee.soap.core.refresh.refresh.RefreshServiceLocator;


/**
 * MFA (Multi Factor Authentication code)
 * This sample code takes care of the real time interaction with agent for MFA sites.  
 * 
 */
public class MFA extends ApplicationSuper {

protected Refresh refresh;
    
	public MFA() {
		super ();
		RefreshServiceLocator locator = new RefreshServiceLocator();
        String serviceName = locator.getRefreshServiceWSDDServiceName();
        locator.setRefreshServiceEndpointAddress(System.getProperty("com.yodlee.soap.services.url")
						+ "/" + serviceName);
        try {
        	refresh = locator.getRefreshService();
		} catch (ServiceException se) {
			throw new RuntimeException(se);
		}
	}
	
	/**
     *  processMFA() 
     *  This method will establish a real time interaction with the agent.   
     *  Agent sends the questions to user and user answers and creates a response and send it to the agent
     *  Agent will stop if the answers are correct or incorrect with the appropriate errorCode. 
     *
     */
    public int processMFA(UserContext userContext, MFARefreshInfo mfaInfo, long itemId) {
    	System.out.println("Entering MFA flow");
		//Check MFARefreshInfo is null and then proceed, If there are any questions then MFARefreshInfo will not be null
		while ( mfaInfo!= null ) {
			try {				
				//First time when the agent has some questions, getErrorCode() will be null
				if ( mfaInfo.getErrorCode() != null ) {
					 int errorCode = mfaInfo.getErrorCode().intValue();
					 //If the getErrorCode() is 0 then it indicates that the agent was able to login to the site with the 
					 //MFA questions successfully
					 if ( errorCode == 0 ) {
						 return errorCode;
					 //If the getErrorCode() is non-zero then it indicates that there was some gatherer error and needs to break from the loop
					 } else if (errorCode > 0) {
						return errorCode;
					}
				}
				if ( mfaInfo.getErrorCode() == null && mfaInfo.getFieldInfo() == null) {
					return -1;
				}
			
				//Check if there are any MFA questions for the user.
				MFAFieldInfo fieldInfo = mfaInfo.getFieldInfo();
				
				if ( fieldInfo!= null ) {				
					long answerTimeout =  mfaInfo.getTimeOutTime();
					//If the site is Token based
	        		if ( fieldInfo instanceof TokenIdFieldInfo ) {
	        			System.out.println("Inside the token Id");
	        			TokenIdFieldInfo token_fieldInfo = (TokenIdFieldInfo) fieldInfo; 
	        			System.out.println(token_fieldInfo.getDisplayString());
	                    System.out.println("\n" + "Enter the token number");     			
	        			//Read the token value 
	        			String tokenId = IOUtils.readStr();
	        			//Create the token response
	        			MFATokenResponse mfatokenresponse = new MFATokenResponse();
	        			mfatokenresponse.setToken(tokenId);
	        			//Put this MFA Request back in the queue
	           			refresh.putMFARequest(userContext, mfatokenresponse,itemId ); 	        			
	        		}//If the site is Image based 
	        		else if ( fieldInfo instanceof ImageFieldInfo ) {
	        			System.out.println("Inside Image");
	        			ImageFieldInfo image_fieldInfo = (ImageFieldInfo) fieldInfo;
	        			try {
	        				//Place the image obtained at a particular path for the user to view
	        				String filename = "MFA_" + itemId + ".jpg";
	        	        	BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(filename));        	        
	        	            out.write(image_fieldInfo.getImage().getElements());
	        	            out.close();
	        	            System.out.println("Image" + filename + " has been placed at " + System.getProperty("user.dir") + " for viewing.\n");	        	            
	        	        } catch (IOException e) {
	        	        	System.out.println("Exception while writing the image onto the file");
	        	        }
	        	        //Get the corresponding code from the user
	        			System.out.println("\n" + "Enter the code present in the image");
	        			String imageCode = IOUtils.readStr();
	        			//Create the MFA response and place it in the queue for the agent to read
	        			MFAImageResponse mfaimageresponse = new MFAImageResponse();
	        			mfaimageresponse.setImageString(imageCode);
	        			refresh.putMFARequest(userContext, mfaimageresponse,itemId );
	        		} //If the site is Security Question type 
	        		else if ( fieldInfo instanceof SecurityQuestionFieldInfo ) {
	        			SecurityQuestionFieldInfo securityqa_fieldInfo = (SecurityQuestionFieldInfo) fieldInfo;        			
	        			ArrayOfQuestionAndAnswerValues queAndAnsArray = securityqa_fieldInfo.getQuestionAndAnswerValues();        			
	        			QuestionAndAnswerValues [] queAndAns = null;
	        			if (queAndAnsArray != null){
	        			queAndAns = queAndAnsArray.getElements();
	        			}
	        			// Create the MFA response for security questions
	        			MFAQuesAnsResponse mfaqaResponse = new MFAQuesAnsResponse();
	        			QuesAndAnswerDetails[] qaDetails = new QuesAndAnswerDetails[queAndAns.length]; 
	        			int count =0;
	        			System.out.print("\nYou have " + answerTimeout/1000 + " seconds to answer the questions");
	        			for ( int loopcounter=0; loopcounter < queAndAns.length ; loopcounter++) {
	        				if ( queAndAns[loopcounter] instanceof SingleQuesSingleAnswerValues ) {
	        					//Get the question	        					        					
	        					String mfa_ques = ((SingleQuesSingleAnswerValues)queAndAns[loopcounter]).getQuestion();
	        					System.out.print("\n" + mfa_ques);
	        					//Get the answer
	        					System.out.print("\nAnswer: ");
	        					String mfa_answer = IOUtils.readStr().trim();
	        					//Get the MFA_TYPE	        					
	        					String que_type = ((SingleQuesSingleAnswerValues)queAndAns[loopcounter]).getQuestionFieldType();
	        					//Get the answer field type 
	        					String ans_type = ((SingleQuesSingleAnswerValues)queAndAns[loopcounter]).getResponseFieldType();
	        					//Get the metadata 
	        					String metadata = ((SingleQuesSingleAnswerValues)queAndAns[loopcounter]).getMetaData();
	        					//Create the Response using the question & answer 
	        					QuesAndAnswerDetails mfaqa_details = new QuesAndAnswerDetails();
	        					mfaqa_details.setQuestion(mfa_ques);
	        					mfaqa_details.setAnswer(mfa_answer);
	        					mfaqa_details.setQuestionFieldType(que_type);
	        					mfaqa_details.setAnswerFieldType(ans_type);
	        					mfaqa_details.setMetaData(metadata);
	        					
	        					qaDetails[count++] = mfaqa_details ;	            				
	        				} else if (queAndAns[loopcounter] instanceof MultiQuesMultiAnswerOptionsValues ) {
	        					//This is for sites having checkbox or radio buttons
	        					//ToDo:Will be implemented later 
	        					System.out.println("Inside MultiQuesMultiAnswerOptionsValues");
	        				} else if ( queAndAns[loopcounter] instanceof MultiQuesOptionsSingleAnswerValues) {
	        					//This is for sites having checkbox or radio buttons
	        					//ToDo:Will be implemented later
	        					System.out.println("Inside MultiQuesOptionsSingleAnswerValues");
	        				} else if ( queAndAns[loopcounter] instanceof SingleQuesMultiAnswerOptionsValues) {
	        					//This is for sites having checkbox or radio buttons
	        					//ToDo:Will be implemented later
	        					System.out.println("Inside SingleQuesMultiAnswerOptionsValues");        					
	        				}       				
	        			}
	        			//Create the response and place it in the queue for the agent to read
	        			ArrayOfQuesAndAnswerDetails arrayOfQuesAndAnswerDetails = new ArrayOfQuesAndAnswerDetails();
	        			arrayOfQuesAndAnswerDetails.setElements(qaDetails);
	        			mfaqaResponse.setQuesAnsDetailArray(arrayOfQuesAndAnswerDetails);
	        			
	        			refresh.putMFARequest(userContext, mfaqaResponse,itemId );	        			
	        		}
	        	  }
	        	//Get the MFA response from the agent, which contains the MFA questions
				//The questions will be placed in the MQ and the app or SDK calls can poll for these questions continuously
				mfaInfo = refresh.getMFAResponse(userContext, itemId);        	
			} catch (Exception e) {
			  	System.out.println("Inside the exception");
			  	e.printStackTrace();
		    }
		} //End of while
		return -1;
    }

}
