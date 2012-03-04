/*
 * Copyright 2009 Yodlee, Inc.  All Rights Reserved.  Your use of this code 
 * requires a license from Yodlee.  Any such license to this code is 
 * restricted to evaluation/illustrative purposes only. It is not intended 
 * for use in a production environment, and Yodlee disclaims all warranties 
 * and/or support obligations concerning this code, regardless of the terms 
 * of any other agreements between Yodlee and you."
 */
package com.yodlee.sampleapps;



import java.util.Hashtable;
import java.util.Locale;

import com.yodlee.soap.common.CobrandContext;

/**
 * A common super class for all sample applications that helps
 * initialize a client, and establishes a
 * <code>CobrandContext</code>.
 */
public class ApplicationSuper
{
    protected CobrandContextSingleton cobCxtSing ;

    public ApplicationSuper()
    {
        cobCxtSing = CobrandContextSingleton.getSingletonObject() ;
    }

    public CobrandContext getCobrandContext()
    {
        return cobCxtSing.getCobrandContext();
    }

    public String getKeystoreFilename() {
        return cobCxtSing.getKeystoreFilename();
    }

    public String getKeystoreAlias() {
        return cobCxtSing.getKeystoreAlias();
    }

    public String getKeystorePassword() {
        return cobCxtSing.getKeystorePassword() ;
    }

    public String getIssuer() {
        return cobCxtSing.getIssuer();
    }

}
