/*
 * Copyright 2009 Yodlee, Inc.  All Rights Reserved.  Your use of this code 
 * requires a license from Yodlee.  Any such license to this code is 
 * restricted to evaluation/illustrative purposes only. It is not intended 
 * for use in a production environment, and Yodlee disclaims all warranties 
 * and/or support obligations concerning this code, regardless of the terms 
 * of any other agreements between Yodlee and you.
 */
package com.yodlee.sampleapps.helper;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * This class servers as an abstract wrapper around a SAML implementaiton.
 * This class should have NO library dependencies on any SAML library
 * allowing it to always be built, even if SAML is not available at
 * compile time.
 */
public abstract class SamlHelper {

	/**
     * Gets back the SAML Helper.
     *
     * @param keystoreFilename the location of the keystore file to use
     * @param keystoreAlias the alias of the key inside the keystore
     * @param keystorePassword the password to access the key
     * @return A constructed SamlHelper or NULL if Saml is not available.
     */
    public static SamlHelper getSamlHelper(String keystoreFilename,
            String keystoreAlias, String keystorePassword) {

        SamlHelper samlHelper = null;
        try {
            Class samlHelperClass =
                Class.forName("com.yodlee.sampleapps.helper.OpenSamlHelper");

            Constructor samlHelperConstructor =
                samlHelperClass.getConstructor(
                    new Class[] {String.class, String.class, String.class});

            samlHelper = (SamlHelper) samlHelperConstructor.newInstance(
                new Object[] {
                    keystoreFilename, keystoreAlias, keystorePassword});


        } catch (ClassNotFoundException e) {
            // Do nothing.  Return null to indicate SAML not available.
        } catch (InvocationTargetException e) {
            throw new IllegalStateException(e.getTargetException().toString());
        } catch (Exception e) {
            throw new IllegalStateException(e.toString());
        }

        return samlHelper;
    }

    /**
     * Generates a response containing a single subject for the
     * given issuer.
     *
     * @param subject subject to generate response for
     * @param issuer issuer in the response
     * @return String version of the XML response
     * @throws IOException
     */
    public abstract String generateResponseString(String subject,
        String issuer) throws Exception;

    /**
     * Generates a response containing a single subject for the
     * given issuer.
     *
     * @param subjects array of subject to create response for
     * @param issuer issuer in the response
     * @return String version of the XML response
     * @throws IOException
     */
    public abstract String generateResponseString(String[] subjects,
        String issuer) throws Exception;
}
