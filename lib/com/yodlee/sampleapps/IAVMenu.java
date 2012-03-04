package com.yodlee.sampleapps;

import com.yodlee.sampleapps.helper.IOUtils;
import com.yodlee.soap.common.UserContext;

public class IAVMenu {

	/** Navigation Counter. * */
	private static int optionCount = 1;

	/** Navigation Menu Choice. * */
	private static final int IAV_DATA_SERVICES = optionCount++;

	/** Navigation Menu Choice. * */
	private static final int IAV_MATCHING_SERVICES = optionCount++;
	
	/** Navigation Menu Choice. * */
	private static final int NAV_QUIT = 0;
	
	/**
	 * Handles the submenu for IAV.
	 * 
	 * @param userContext
	 *            The user context of the caller
	 */
	public void doMenu(UserContext userContext) {
		boolean loop = true;
		int choice = 0;
		while (loop) {
            try {
            	System.out.println("\n*****************************");
                System.out.println("\n IAV Menu");
                System.out.println("*****************************");
                System.out.println("" + IAV_DATA_SERVICES
                        + ". IAV Data Services");
                System.out.println("" + IAV_MATCHING_SERVICES
                        + ". IAV Matching Services");
                System.out.println("" + NAV_QUIT
                        + ". Exit");
                System.out.println("\n");
                System.out.print("Enter Choice : ");
                choice = IOUtils.readInt();
                if (choice == IAV_DATA_SERVICES){
                	IAVDataServices iavDataServices = new IAVDataServices();
            		iavDataServices.doMenu(userContext);
                }
                if (choice == IAV_MATCHING_SERVICES){
                	IAVMatchingServices iavMatchingServices = new IAVMatchingServices();
                	iavMatchingServices.doMenu(userContext);
                }                	
                if(choice == NAV_QUIT)
                    loop = false;
            }catch (Exception e) {
                e.printStackTrace();
            }
		}
	}
}
