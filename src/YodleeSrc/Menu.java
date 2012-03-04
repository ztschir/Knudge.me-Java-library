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

/**
 * Interface for classes that support having a menu that can
 * be used by the console.
 */
public interface Menu {
    /**
     * Returns the Top-Level name of the Menu.
     *
     * @return the menu name
     */
    String getMenuName();

    /**
     * Returns a string array that has the names of the menu items
     * supported by this class.
     *
     * @return array of menu item names
     */
    String[] getMenuItems();

    /**
     * Executes an individual menu item supported by this class.
     *
     * @param menuItem the menu item number to be executed
     * @param cobrandContext the cobrand context for the logged in cobrand
     * @param userContext the user context for the logged in user
     * @return a userContext which may be changed after a call
     */
    UserContext doMenuItem(
        int menuItem, CobrandContext cobrandContext, UserContext userContext);
}
