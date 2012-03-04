/*
 * Copyright 2009 Yodlee, Inc.  All Rights Reserved.  Your use of this code 
 * requires a license from Yodlee.  Any such license to this code is 
 * restricted to evaluation/illustrative purposes only. It is not intended 
 * for use in a production environment, and Yodlee disclaims all warranties 
 * and/or support obligations concerning this code, regardless of the terms 
 * of any other agreements between Yodlee and you.
 */
package com.yodlee.sampleapps;

import com.yodlee.sampleapps.helper.IOUtils;
import com.yodlee.soap.common.UserContext;

public class PFMManagement extends ApplicationSuper {

	/** Navigation Counter. **/
    private static int optionCount = 1;
    /** Navigation Menu Choice. **/
    private static final int NAV_TRANSACTIONS = optionCount++;
    /** Navigation Menu Choice. **/
    private static final int NAV_REAL_ESTATE_CENTER = optionCount++;
    /** Navigation Menu Choice. **/
    private static final int NAV_NET_WORTH = optionCount++;
    /** Navigation Menu Choice. **/
    private static final int NAV_BUDGET_GOALS = optionCount++;    
    /** Navigation Menu Choice. **/
    private static final int NAV_CATEGORY_SPENT_DATA_POINT = optionCount++;     
    /** Navigation Menu Choice. **/
    private static final int NAV_QUIT = 0;
    
    
	public void doPFMMenu(UserContext userContext) {
		boolean loop = true;
		int choice = 0;
		while (loop) {
       	try {
				System.out.println("PFM Menu");
				System.out.println("********************");
		        System.out.println(NAV_TRANSACTIONS + ". Transactions [sub menu]");
		        System.out.println(NAV_REAL_ESTATE_CENTER + ". Real Estate Center [sub menu]");
		        System.out.println(NAV_NET_WORTH + ". Net Worth Calculations");	
		        System.out.println(NAV_BUDGET_GOALS + ". Budget Goals [sub menu]");	
		        //System.out.println(NAV_CATEGORY_SPENT_DATA_POINT + ". Category Spent Data Points [sub menu]");			        
		        System.out.println(NAV_QUIT + ". Exit Sub-menu");
		        System.out.println("********************");
		        System.out.print("Enter Choice : ");
		        choice = IOUtils.readInt();
		
		        
		        if (choice == NAV_TRANSACTIONS) {
		        	transactionsMenu(userContext);
		        }else if (choice == NAV_REAL_ESTATE_CENTER){
		        	realEstateCenterMenu(userContext);
		        }else if (choice == NAV_NET_WORTH) {
		        	networthCalMenu(userContext);
		        }else if (choice == NAV_BUDGET_GOALS) {
		        	budgetingMenu(userContext);
		        //}else if (choice == NAV_CATEGORY_SPENT_DATA_POINT) {
		        	//categorySpentDataPointMenu(userContext);		        	
		        }else if (choice == NAV_QUIT)
		            loop = false;
		        else 
					 System.out.println("Invalid Entry!");
       	} catch (Exception e) {
               e.printStackTrace();
           }
       }
	}
	
    public void realEstateCenterMenu(UserContext userContext) {
    	RealEstateCenter realEstateCenter = new RealEstateCenter();
    	realEstateCenter.doMenu(userContext);
    }
    
    void budgetingMenu(UserContext userContext){
    	 BudgetingManagement budget = new BudgetingManagement();
    	 budget.doMenu(userContext);
    }
    
    public void networthCalMenu(UserContext userContext) {
    	NetWorth netWorth= new NetWorth();
    	netWorth.calculateNetworthChange(userContext);
    }    
    
    public void transactionsMenu(UserContext userContext) {
        Transactions transactions = new Transactions();
        transactions.doMenu(userContext);
    }
    /*public void categorySpentDataPointMenu(UserContext userContext) {
    	CategorySpentDataPointHelper categorySpentDataPointHelper = new CategorySpentDataPointHelper();
    	categorySpentDataPointHelper.doMenu(userContext);
    } */   
}
