package YodleeSrc;

import com.yodlee.soap.common.CobrandContext;
import com.yodlee.soap.common.UserContext;

public class IAVMenu implements Menu {

	private String[] menuItems =
        new String[] {
            "IAV Data Services", 
            "IAV Matching Services",
        };
	
	/**
     * Executes the Menu.
     * @param cobrandContext context of the cobrand
     * @param userContext context of the user
     * @return context of the user
     */
	public UserContext doMenuItem(int menuItem, CobrandContext cobrandContext,
			UserContext userContext) {
		switch (menuItem) {
	        case 1: doIAVDataService(userContext); break;
	        case 2: doIAVMatchingService(userContext, cobrandContext);        
		}
		return userContext;		
	}

	public String[] getMenuItems() {
		return menuItems;
	}

	public String getMenuName() {
		return "IAV Menu";
	}

	public void doIAVDataService(UserContext userContext){
		IAVDataService iAVDataService = new IAVDataService();
		iAVDataService.doIAV(userContext);
	}
	
	public void doIAVMatchingService(UserContext userContext, CobrandContext cobrandContext){
		IAVMatchingServices iavMatchingServices = new IAVMatchingServices();
		iavMatchingServices.doMenu(userContext, cobrandContext); 
	}
}
