/*
 * Copyright 2009 Yodlee, Inc.  All Rights Reserved.  Your use of this code 
 * requires a license from Yodlee.  Any such license to this code is 
 * restricted to evaluation/illustrative purposes only. It is not intended 
 * for use in a production environment, and Yodlee disclaims all warranties 
 * and/or support obligations concerning this code, regardless of the terms 
 * of any other agreements between Yodlee and you.
 */
package com.yodlee.sampleapps;

import java.util.StringTokenizer;

import javax.xml.rpc.ServiceException;

import com.yodlee.soap.collections.ArrayOfString;
import com.yodlee.soap.collections.ArrayOflong;
import com.yodlee.soap.collections.List;
import com.yodlee.soap.common.ContentServiceInfo;
import com.yodlee.soap.core.search.search.Search;
import com.yodlee.soap.core.search.search.SearchServiceLocator;

/**
 * Search can be done with keywords and either an entire ContentService can be returned
 * or just the content service IDs.
 *
 * Returning the ID's is ideal when an SDK customer stores a local version
 * of the content services on their side and want to retrieve this information
 *
 * In 6.x, new Search APIs were introduced that allow searching
 * only in specific containers
 */
public class SearchSample extends ApplicationSuper {

	Search search;
	
	String[] defaultSearchStrings = {"harris",
            "harris*", // wild card for multiple character match
            "h???is",  // ? for single character match
            "h?is"
           };
	
	public SearchSample()
    {
        super ();
        SearchServiceLocator locator = new SearchServiceLocator();
		String serviceName = locator
				.getSearchServiceWSDDServiceName();
		locator
				.setSearchServiceEndpointAddress(System.getProperty("com.yodlee.soap.services.url")
						+ "/" + serviceName);
		try {
			search = locator.getSearchService();

		} catch (ServiceException se) {
			throw new RuntimeException(se);
		}
    }
	
	/**
     * This will do the search for full content services and then just for the IDS
     */
    public void doSearch(String[] searchStrings)
    {
    	String searchString = null;
        for(int i=0; i<searchStrings.length; i++){
        	searchString = andKeywords(searchStrings[i]);
            System.out.println("Searching for \"" + searchString + "\" and returning full content services...");
            List cts = null;
            try {
            	cts = search.searchContentServices(getCobrandContext(), searchString);
            } catch (Exception e) {
            	e.printStackTrace();
            	throw new RuntimeException("Error fetching content services...");
            }
            if (cts == null) {
                System.out.println("Search returned no results");
            } else {
            	for (int j = 0; j < cts.getElements().length; j++) {                
                    ContentServiceInfo csi	= (ContentServiceInfo)cts.getElements(j);
                    String displayName = csi.getContentServiceDisplayName();
                    long csId = csi.getContentServiceId();
                    System.out.println("\t DisplayName=" + displayName + " (" + csId + "), " + csi.getContainerInfo().getContainerName());
                }
                System.out.println("Number of results: " + cts.getElements().length);
                System.out.println("]\n");
            }
        }
    }

    public void doSearchByContainer(String keywords, String[] containers) {
        System.out.println("Searching for \"" + keywords + "\" and returning content services ids...");
        ArrayOflong contentServiceIds = null;
        try {
        	ArrayOfString arrayOfString = new ArrayOfString();
        	arrayOfString.setElements(containers);
        	contentServiceIds = search.getContentServiceIdsByContainerType(getCobrandContext(), arrayOfString, keywords);
        	
        } catch (Exception e) {}
        if (contentServiceIds == null) {
        	System.out.print("\tNo Content services found");
        } else {
	        System.out.print("\tContent service ids found: [");
	        for (int j = 0; j< contentServiceIds.getElements().length; j++) {
	            System.out.print(contentServiceIds.getElements(j).longValue() + ", ");
	        }
        }

    }

     public void doSearch()
    {
        doSearch(defaultSearchStrings);
    }
     
     private static String formatKeyword(String keyword)
     {
         //System.Console.WriteLine("formatKeyword(" + keyword + ")");
         String ret = "";
         if (keyword != null)
         {
             keyword = keyword.trim();
             if (keyword.length() > 0)
             {
                 ret += keyword;
                 if (!keyword.endsWith("*"))
                 {
                     ret += "*";
                 }
             }
         }
         return ret.toLowerCase();
     }

     private static String andKeywords(String keywords)
     {
         //System.Console.WriteLine("andKeywords(" + keywords + ")");
         String sb = "";
         if (keywords != null)
         {
             StringTokenizer st = new StringTokenizer(keywords, " ");
             String token;
             while (st.hasMoreTokens())
             {
                 token = st.nextToken();
                 sb += formatKeyword(token);
                 if (st.hasMoreTokens())
                 {
                     sb += " OR ";
                 }
             }
         }
         //System.Console.WriteLine("search key=" + sb);
         return sb;
     }

    /**
     * You can pass an array of keywords to search for when executing this
     * sample code via the command line.  Or if no args are passed it uses the default search params in
     * the "defaultSearchStrings" array.
     * @param args
     */
    public static void main (String args[])
    {
        // Startup
        /*
        try {
            InitializationHelper.setup ();
        } catch (Exception startupEx) {
            System.out.println ("Unable to startup system: " + startupEx);
            System.exit (-1);
        }
        */

        SearchSample searchSample = new SearchSample ();

        // Do a Search - either use passed in args or default search string array
        if(args.length > 0){
            searchSample.doSearch(args);
        }else{
            searchSample.doSearch();
        }


    }
}
