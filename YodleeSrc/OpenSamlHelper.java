/*
 * Copyright 2009 Yodlee, Inc.  All Rights Reserved.  Your use of this code 
 * requires a license from Yodlee.  Any such license to this code is 
 * restricted to evaluation/illustrative purposes only. It is not intended 
 * for use in a production environment, and Yodlee disclaims all warranties 
 * and/or support obligations concerning this code, regardless of the terms 
 * of any other agreements between Yodlee and you."
 */
package YodleeSrc;


import org.apache.xml.security.signature.XMLSignature;
//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;

import org.opensaml.*;

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;
import java.security.PrivateKey;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.cert.X509Certificate;
import java.security.cert.Certificate;

/**
 * User: Doug Diego
 * Date: Oct 29, 2003
 * Copyright Yodlee 1999 - 2003
 *
 * OpenSamlHelper
 * This is a helper class to using the OpenSaml implementation of SAML.
 */
public class OpenSamlHelper extends SamlHelper {
    private static String  keystoreFilename;
    private static String  keystoreAlias;
    private static String  keystorePassword;

    private static  X509Certificate[]   certs;
    public static PrivateKey  privateKey;
    //private static Log log;

    public OpenSamlHelper(String keystoreFilename,
                          String keystoreAlias,
                          String keystorePassword)
    {
        this.keystoreFilename = keystoreFilename;
        this.keystoreAlias = keystoreAlias;
        this.keystorePassword = keystorePassword;

        //log = LogFactory.getLog( this.getClass().getName() );
        //log = LogFactory.getLog( OpenSamlHelper.class.getName() );

        // Initilize the Keystore
        initKeyStore();
    }

    /**
     * Initilize the Keystore.
     */
    private static void initKeyStore ()  {
        InputStream fileInput = null;
        try {
            fileInput = new FileInputStream(keystoreFilename);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
        KeyStore keystore = null;
        try {
            keystore = KeyStore.getInstance(KeyStore.getDefaultType());
            keystore.load(fileInput, keystorePassword.toCharArray());
            privateKey = (PrivateKey) keystore.getKey(keystoreAlias, keystorePassword.toCharArray());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }

        if (privateKey == null)
            throw new RuntimeException(keystoreAlias + " key not found in keystore " + keystoreFilename);

        X509Certificate cert = null;
        Certificate[] certificates = new Certificate[0];
        try {
            cert = (X509Certificate) keystore.getCertificate(keystoreAlias);
            certificates = keystore.getCertificateChain(keystoreAlias);
        } catch (KeyStoreException e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
        if (cert == null)
            throw new RuntimeException(keystoreAlias + " cert not found in keystore " + keystoreFilename);

        if (certificates == null)
            throw new RuntimeException(keystoreAlias + " cert chain not found in keystore " + keystoreFilename);

        certs = new X509Certificate[certificates.length];
        System.arraycopy(certificates, 0, certs, 0, certs.length);
    }

    /*
    // Alternative implementation of initKeystore.  Left for reference
    private void initKeyStore ()
    {
    try{
    KeyStore ks= KeyStore.getInstance( KeyStore.getDefaultType());
    //ks.load( new FileInputStream( "testkeystore"), "yodlee123".toCharArray());
    ks.load( new FileInputStream( keystoreFilename), keystorePassword.toCharArray());
    //dsaPrivateKey= (PrivateKey) ks.getKey( "test", "yodlee123".toCharArray());
    privateKey= (PrivateKey) ks.getKey( keystoreAlias, keystorePassword.toCharArray());

    Enumeration en= ks.aliases();
    ArrayList l_certs= new ArrayList();
    while( en.hasMoreElements()){
    String alias= (String) en.nextElement();
    System.out.println("alias="+alias);
    java.security.cert.Certificate c= ks.getCertificate( alias);
    l_certs.add( c);
    }
    certs= new java.security.cert.Certificate[ l_certs.size()];
    Iterator itr= l_certs.iterator();
    int i=0;
    while( itr.hasNext()){
    certs[i++]= (java.security.cert.Certificate) itr.next();
    }
    } catch (FileNotFoundException e) {
    System.out.println("\tFileNotFoundException");
    e.printStackTrace();
    }catch(Exception e){
    System.out.println("\tException");
    e.printStackTrace();
    }
    }
    */

    /**
     * THIS FUNCTION HAS NOT BEEN TEST.  DO NOT USE.
     *
     * @return SAMLRequest obect.
     * @throws Exception
     */
    /*public static SAMLRequest GenerateRequest() throws Exception {
        SAMLRequest request = null ;
        try {
            request = new SAMLRequest
                    (null,
                            new SAMLAuthenticationQuery
                                    (new SAMLSubject("subhash","yodlee.com",null,null,null,null),"test"),
                            null,null);
        } catch (SAMLException e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
        return request ;
    }*/


    /**
     * THIS FUNCTION HAS NOT BEEN TEST.  DO NOT USE.
     * @return SAMLRequest object
     * @throws Exception
     */
    public static SAMLRequest generateArtifactRequest(String artifactId) throws Exception {
        SAMLRequest request = null ;
        ArrayList al = new ArrayList();
        al.add(artifactId) ;
        request = new SAMLRequest();

        Collection  dsa_certs   =   new ArrayList() ;
        for (int i =0; i < OpenSamlHelper.certs.length; i++)
            dsa_certs.add(OpenSamlHelper.certs[i]) ;
        request.sign(XMLSignature.ALGO_ID_SIGNATURE_RSA, OpenSamlHelper.privateKey,dsa_certs) ;

        request.toStream(System.out);
        return request ;
    }

    /**
     * This function generates the response.
     *
     * @param subject
     * @param issuer
     * @return SAMLResponse object
     * @throws SAMLException
     * @throws Exception
     */
    public SAMLResponse generateResponse(String subject, String issuer ) throws SAMLException {
        String[] subjects = {subject};
        SAMLResponse response = generateResponse(subjects, issuer);

        return response ;
    }

    /**
     * This function generates the response.
     *
     * @param subjects
     * @return SAMLResponse object
     * @throws SAMLException
     * @throws Exception
     */
    public SAMLResponse generateResponse(String[] subjects, String issuer ) throws SAMLException {
        // Get Host Information
        InetAddress     address     = null ;
        try {
            address = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
        String  IPAddress   =   address.getHostAddress() ;
        String  DNSAddress  =   address.getHostName() ;

        Collection statements   =   new ArrayList() ;

        // Create the SAML subject
        for (int i = 0; i < subjects.length; i++) {
            String subject = subjects[i];
            SAMLNameIdentifier nameIdentifier = new SAMLNameIdentifier(subject, null, SAMLNameIdentifier.FORMAT_X509);
            List confirmationMethodList = new ArrayList();
            confirmationMethodList.add(SAMLSubject.CONF_BEARER);

            SAMLSubject samlSubject = new SAMLSubject(nameIdentifier,confirmationMethodList,null,null);

            // Create the SAML Authentication Statement
            SAMLAuthenticationStatement sas = new SAMLAuthenticationStatement
                    //(subject,"auth",new Date(),IPAddress,DNSAddress,null);
                    (samlSubject,"password",new Date(),IPAddress,DNSAddress,null);

            statements.add(sas) ;
        }



        // Create the SAML Assertion
        SAMLAssertion assertion     =   new SAMLAssertion
                //(issuer,new Date(),new Date(),null,null,statements);
                (issuer,null, null ,null,null,statements);
        Collection assertions = new ArrayList() ;
        assertions.add(assertion) ;

        // Create the SAML Response
        SAMLResponse    response    =   null ;
        response    =   new SAMLResponse("artifact",subjects[0],assertions,null);

        Collection  dsa_certs   =   new ArrayList() ;
        for (int i =0; i < OpenSamlHelper.certs.length; i++)
            dsa_certs.add(OpenSamlHelper.certs[i]) ;

        // Sign the Response
        try{
            response.sign(XMLSignature.ALGO_ID_SIGNATURE_RSA, OpenSamlHelper.privateKey,dsa_certs) ;
        }catch(SAMLException e){
            System.out.println("SAMLException.  Error signing the response.");
            e.printStackTrace();
        }
        //response.toStream( System.out);

        return response ;
    }

    /**
     * Generates a response containing a single subject for the
     * given issuer.
     *
     * @param subject subject to generate response for
     * @param issuer issuer in the response
     * @return String version of the XML response
     * @throws SAMLException
     * @throws IOException
     */
    public String generateResponseString(String subject, String issuer ) throws Exception {
        return convertResponseToString(generateResponse(subject, issuer));
    }

    /**
     * Generates a response containing a single subject for the
     * given issuer.
     *
     * @param subject subject to generate response for
     * @param issuer issuer in the response
     * @return String version of the XML response
     * @throws SAMLException
     * @throws IOException
     */
    public String generateResponseString(String[] subject, String issuer ) throws Exception {
        return convertResponseToString(generateResponse(subject, issuer));
    }

    /**
     * The function converts a SAML Response into an XML formated
     * String.
     *
     * @param response
     * @return the SAML Response in a XML formated String
     * @throws IOException
     * @throws SAMLException
     */
    public String convertResponseToString(SAMLResponse response)
            throws IOException, SAMLException {
        ByteArrayOutputStream baos  = new ByteArrayOutputStream(1024);
        response.toStream(baos);
        return baos.toString() ;
    }

    public static SAMLResponse createResponseFromString(String s)
            throws SAMLException {
        ByteArrayInputStream bais = new ByteArrayInputStream(s.getBytes());
        return new SAMLResponse(bais) ;
    }

    public static SAMLRequest createRequestFromString(String s)
            throws SAMLException {
        ByteArrayInputStream bais = new ByteArrayInputStream(s.getBytes());
        return new SAMLRequest(bais) ;
    }

    public static void main (String[] args) throws Exception {

        System.out.println(System.getProperty("java.class.path"));

        String keystoreFilename = "C:\\scratch\\ssoClientKeyStore";
        System.out.println("keystore=" + keystoreFilename );
        String keystoreAlias = "sso";
        String keystorePassword = "test123";
        OpenSamlHelper sh = new OpenSamlHelper(keystoreFilename,keystoreAlias, keystorePassword);

        String subject = "doug";
        String issuer = "test";
        try{
            String samlResponseStr = sh.convertResponseToString(sh.generateResponse(subject, issuer));
            System.out.println(samlResponseStr);
        }catch(Exception e){
            System.out.println("unable to generate response");
            e.printStackTrace();
        }
    }
}

