/*
 * Copyright 2008 Yodlee, Inc.  All Rights Reserved.  Your use of this code
 * requires a license from Yodlee.  Any such license to this code is
 * restricted to evaluation/illustrative purposes only. It is not intended
 * for use in a production environment, and Yodlee disclaims all warranties
 * and/or support obligations concerning this code, regardless of the terms
 * of any other agreements between Yodlee and you."
 */
package YodleeSrc;

import com.yodlee.soap.common.CobrandContext;
import com.yodlee.soap.common.UserContext;
import com.yodlee.soap.core.version.ServerVersion;
import com.yodlee.soap.core.version.serverversionmanagement.ServerVersionManagement;
import com.yodlee.soap.core.version.serverversionmanagement.ServerVersionManagementServiceLocator;

import java.lang.reflect.Constructor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.rpc.ServiceException;


/**
 * A stand alone console application that performs many of the fundamental
 * Yodlee operations.  It ties together many of the existing sample applications
 *
 * 0.1 - Created
 *
 */
public class YodleeConsole implements Menu {
    protected final String[] menuObjects =
        new String[] { 
    		"com.yodlee.sampleapps.core.RegisterLogin",
    		"com.yodlee.sampleapps.core.ContentServiceManagement",
    		"com.yodlee.sampleapps.core.ManageItems",
    		"com.yodlee.sampleapps.core.RefreshItem",
    		"com.yodlee.sampleapps.core.IAVMenu"
    	};

    /** Holds the List of top-level menus. */
    protected List menuList;

    /**
     * Executes the main loop of the program.  Creates a CobrandContext.
     *
     * @param menu the menu to execute
     * @param userContext the usercontext to be executed
     * @return a new userContext
     */
    public static UserContext displayMenu(Menu menu, UserContext userContext) {
        boolean loop = true;

        while (loop) {
            Menu userMenu = menu;
            System.out.println(
                "*** " + userMenu.getMenuName() + "*****************");
            System.out.println("0. Quit");

            if (userContext == null) {
                // If the userContext is null show the pre-login menu.
                userMenu = new RegisterLogin();
            }

            String[] menuItems = userMenu.getMenuItems();

            for (int i = 0; i < menuItems.length; i++) {
                System.out.println((i + 1) + ". " + menuItems[i]);
            }

            System.out.println("********************");
            System.out.print("Choice: ");

            int choice = IOUtils.readInt();
            System.out.print("\n");

            // Take the choice data and execute on it.
            if ((choice < 0) || (choice > userMenu.getMenuItems().length)) {
                System.out.println("Please choose a valid option.");
            } else if (choice == 0) {
                loop = false;
            } else {
                userContext =
                    userMenu.doMenuItem(
                        choice,
                        CobrandContextSingleton.getSingletonObject()
                                               .getCobrandContext(), userContext);
            }
        }

        return userContext;
    }

    /**
     * Execute the sub-menu.
     *
     * @return a new usercontext
     */
    public UserContext doMenuItem(
        int menuItem, CobrandContext cobrandContext, UserContext userContext) {
        Menu subMenu = (Menu) menuList.get(menuItem - 1);
        userContext = YodleeConsole.displayMenu(subMenu, userContext);

        return userContext;
    }

    /**
     * Returns the menu name.
     *
     * @return the menu name
     */
    public String getMenuName() {
        return "Top-level Menu";
    }

    /**
     * Runs through the list of possible sub-menu and gets them
     * back through reflection.
     *
     * @return list of submenu names
     */
    public String[] getMenuItems() {
        if (menuList == null) {
            menuList = new ArrayList();

            for (int i = 0; i < menuObjects.length; i++) {
                Class menuClass;

                try {
                    menuClass = Class.forName(menuObjects[i]);

                    Constructor menuConstructor =
                        menuClass.getConstructor(new Class[] {  });

                    Menu newMenu =
                        (Menu) menuConstructor.newInstance(new Object[] {  });

                    menuList.add(newMenu);
                } catch (Exception e) {
                    // Ignore problems loading submenus.
                }
            }
        }

        String[] menuItems = new String[menuList.size()];
        Iterator menuListIterator = menuList.iterator();
        int i = 0;

        while (menuListIterator.hasNext()) {
            Menu newMenu = (Menu) menuListIterator.next();
            menuItems[i] = newMenu.getMenuName();
            i++;
        }

        return menuItems;
    }

    /**
     * Gets the Client and Server versions from Yodlee and outputs
     * them to the console.
     *
     * @param cobrandContext the cobrand context to display server version
     *        from
     */
    public void displayVersions(CobrandContext cobrandContext) {
        // Get Server Version Information
    	ServerVersionManagementServiceLocator locator1 = new ServerVersionManagementServiceLocator();
		String serviceName1 = locator1.getServerVersionManagementServiceWSDDServiceName();
		locator1.setServerVersionManagementServiceEndpointAddress(System.getProperty("com.yodlee.soap.services.url")
				+ "/" + serviceName1);
		ServerVersionManagement serverVersionManagement; 
		try {
			serverVersionManagement = locator1.getServerVersionManagementService();
		} catch (ServiceException se) {
			throw new RuntimeException(se);
		}
		try {
			ServerVersion serverVersion = serverVersionManagement.getServerVersion(cobrandContext);		
			System.out.println("****Server Version****");
			System.out.println(serverVersion.getMajorVersion() + 
					"." + serverVersion.getMinorVersion() + "." + serverVersion.getPatch()
					+ "_" + serverVersion.getBuildDate() + "_" + serverVersion.getBuildTime());			
	        System.out.println("\n");
		} catch (Exception e) {
			e.printStackTrace();
		}
    }

    /**
     * Main method to run the console.  No arguments are expected.
     *
     * @param args command line parameters are ignored
     */
    public static void main(String[] args) {
	    
        try {
	    javax.net.ssl.SSLSocketFactory sslSocketFactory = (javax.net.ssl.SSLSocketFactory)javax.net.ssl.SSLSocketFactory.getDefault();
	    javax.net.ssl.HttpsURLConnection.setDefaultHostnameVerifier(new YodleeHostnameVerifier());
	    System.out.println("JR: "  + sslSocketFactory.getClass().getName() + " " + javax.net.ssl.HttpsURLConnection.getDefaultHostnameVerifier().getClass().getName());
	    
            YodleeConsole yodleeConsole = new YodleeConsole();

            // Create the initial Cobrand Context
            CobrandContext cobrandContext =
                CobrandContextSingleton.getSingletonObject().getCobrandContext();
            yodleeConsole.displayVersions(cobrandContext);

	    System.out.println("JR: "  + sslSocketFactory.getClass().getName() + " " + javax.net.ssl.HttpsURLConnection.getDefaultHostnameVerifier().getClass().getName());
	    
            // Begin main loop
            YodleeConsole.displayMenu(yodleeConsole, null);
        } catch (RuntimeException e) {
            if (
                (e.getMessage() != null)
                    && (e.getMessage().indexOf("SSLHandshakeException") >= 0)) {
                System.out.println("\n\n-----=====-----=====");
                System.out.println(
                    "The Sample Applications have encountered "
                    + "an SSLHandshakeException exception attempting to "
                    + "connect to the SOAP server.  The most likely "
                    + "cause to this is that Yodlee uses self-signed "
                    + "certificates in the staging environment that will "
                    + "trigger this error until the certificate is "
                    + "marked as trusted by importing it into your "
                    + "cacerts file.  Consult the README file or "
                    + "developer's guide for instructions on this "
                    + "process.");
                System.out.println("-----=====-----=====\n\n");
            } else {
                throw e;
            }
        }
    }
    
    public static class YodleeHostnameVerifier implements javax.net.ssl.HostnameVerifier {
	    public boolean verify(String hostname, javax.net.ssl.SSLSession session) {
		    System.out.println("Verifier: hostname = " + hostname);
		    
		    return true;
	    }
    }
}
