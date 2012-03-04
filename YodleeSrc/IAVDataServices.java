package com.yodlee.sampleapps;

import com.yodlee.sampleapps.helper.IOUtils;
import com.yodlee.soap.common.UserContext;

public class IAVDataServices {
	
	/** Navigation Counter. * */
	private static int optionCount = 1;

	/** Navigation Menu Choice. * */
	private static final int IAV_ITEM = optionCount++;
	
	/** Navigation Menu Choice. * */
	private static final int NAV_QUIT = 0;

	public void doMenu(UserContext userContext) {
		boolean loop = true;
		int choice = 0;
		while (loop) {
            try {
            	System.out.println("\n*****************************");
                System.out.println("IAV Data Sevices Menu");
                System.out.println("*****************************");
                System.out.println("" + IAV_ITEM
                        + ". IAV ITEM");
                System.out.println("" + NAV_QUIT
                        + ". Exit");
                System.out.println("\n");
                System.out.print("Enter Choice : ");
                choice = IOUtils.readInt();
                if (choice == IAV_ITEM){
                	IAV iav = new IAV ();
                    iav.doIAV(userContext);
                }
                if (choice == NAV_QUIT)
                    loop = false;
            } catch (Exception e) {
                e.printStackTrace();
            }
		}
	}

}
