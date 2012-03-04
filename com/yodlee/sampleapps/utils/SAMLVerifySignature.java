/*
 * Copyright 2009 Yodlee, Inc.  All Rights Reserved.  Your use of this code
 * requires a license from Yodlee.  Any such license to this code is
 * restricted to evaluation/illustrative purposes only. It is not intended
 * for use in a production environment, and Yodlee disclaims all warranties
 * and/or support obligations concerning this code, regardless of the terms
 * of any other agreements between Yodlee and you."
 */

package com.yodlee.sampleapps.utils;

import org.opensaml.SAMLResponse;

import java.io.InputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class SAMLVerifySignature {


    public static void main(String[] args){
        if (args.length < 1) {
            throw new RuntimeException
                    ("Usage: <saml_response.xml>") ;
        }

        String samlResponseFile = args[0] ;
        System.out.println("Reading " + samlResponseFile);

        InputStream fileInput = null;
        try {
            fileInput = new FileInputStream(samlResponseFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }

        SAMLResponse samlResponse = null;
        try{
            samlResponse = new SAMLResponse(fileInput);
        }catch(Exception e){
            e.printStackTrace();
        }

        try{
            samlResponse.verify() ;
        }catch(Exception e){
            e.printStackTrace();
        }

    }

}

