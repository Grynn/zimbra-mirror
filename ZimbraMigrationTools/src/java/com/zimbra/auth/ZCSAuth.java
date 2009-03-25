/*
 * ***** BEGIN LICENSE BLOCK *****
 *
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2007, 2008 Zimbra, Inc.
 *
 * The contents of this file are subject to the Yahoo! Public License
 * Version 1.0 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 *
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.auth;

import java.util.logging.*;
import javax.net.ssl.*;

//soap
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPHeader;
import com.zimbra.utils.*;
import com.zimbra.zcsprov.ZMSoapSession;

import java.security.Security;

public class ZCSAuth
{
    private String auth_token;
    private String sZCSurl;
    private String susername; 
    private String  spassword;
    private String session_id;
    private Logger auth_logger;
    private boolean dump_all;
    private int iauthtype;
    MessageFactory mf;
    SOAPConnection conn;
    public ZCSAuth(String ZCSurl, String username, String  password,int authtype,Logger logger)
    {
        sZCSurl=ZCSurl;
        susername=username; 
        spassword=password;
        auth_token=null;
        session_id=null;
        dump_all=false;
	    auth_logger= logger;
        iauthtype=authtype;
        Security.setProperty( "ssl.SocketFactory.provider", 
                                "com.zimbra.utils.ZMSSLSocketFactory");
        HostnameVerifier hv = new HostnameVerifier() 
        {
            public boolean verify(String urlHostName,SSLSession session) 
            {
                return true;
            }
        };      
        HttpsURLConnection.setDefaultHostnameVerifier(hv); 
        Init();
    }
    
    private void Init()
    {
        try
        {
            mf = MessageFactory.newInstance();       
            conn = SOAPConnectionFactory.newInstance().createConnection();
        }
        catch(SOAPException se)
        {
            se.printStackTrace();
        }
    }
    
    public void enable_dump_all()
    {
        dump_all=true;
    }
    
    public void disable_dump_all()
    {
        dump_all=false;
    }
    
    public String get_authtoken()
    {
        return auth_token;
    }
    
    public String get_sessionId()
    {
        return session_id;
    }
    
    public boolean AuthenticateToZCS() 
    {
        SOAPMessage response=null;
        SOAPMessage request=null;
        boolean ret=false;
        try
        {
            request = mf.createMessage();
            SOAPPart sp = request.getSOAPPart();
            SOAPEnvelope se = (SOAPEnvelope)sp.getEnvelope();
            SOAPBody body = se.getBody();
            SOAPHeader sh = se.getHeader();

            //Add a namespace declaration to the envelope
            se.addNamespaceDeclaration("soap", "http://schemas.xmlsoap.org/soap/envelope/");        

            //add header
            sh.addChildElement("context", "nsg", "urn:zimbra");
            sh.addChildElement("userAgent", "name","ZimbraProvisioningTool");
            sh.addChildElement("format", "type","xml"); 

            //create SOAP Body
            Name bodyName=null;
            SOAPElement bodyElement=null;
            if((iauthtype== ZMSoapSession.AUTH_TYPE_ADMIN)||
               (iauthtype== ZMSoapSession.AUTH_TYPE_ADMIN_DEST))
            {
                bodyName = se.createName("AuthRequest", "nsg","urn:zimbraAdmin");
                bodyElement = body.addBodyElement(bodyName);
                //add <name>admin_name</name>			
                Name uname = se.createName("name");
                SOAPElement seuname = bodyElement.addChildElement(uname);
                seuname.addTextNode(susername);
            }
            else if(iauthtype== ZMSoapSession.AUTH_TYPE_ACUSER)
            {
                bodyName = se.createName("AuthRequest", "nsg","urn:zimbraAccount");
                bodyElement = body.addBodyElement(bodyName);
                //add <name>admin_name</name>			
                Name uname = se.createName("account");
                SOAPElement seuname = bodyElement.addChildElement(uname);
                Name nby = se.createName("by");
                seuname.addAttribute(nby, "name");
                seuname.addTextNode(susername);
                
            }
            //add <password>pwd</password>
            Name pwd = se.createName("password");
            SOAPElement sepwd = bodyElement.addChildElement(pwd);
            sepwd.addTextNode(spassword);
            //add <virtualHost>host_address</virtualHost>
            Name vhost = se.createName("virtualHost");
            SOAPElement sevh = bodyElement.addChildElement(vhost);
            sevh.addTextNode(ZCSUtils.get_localhost_info());

            //Save the message
            request.saveChanges();
            
            //Display Request Message
            if (dump_all)
                ZCSUtils.dump_soap_message("AUTH_REQUEST",request);
                       
            //Get Response
            auth_logger.log(Level.INFO,"Request URL: "+sZCSurl);
            response = conn.call(request, sZCSurl);              
            
            //Display Response Message
            if (dump_all)
                ZCSUtils.dump_soap_message("AUTH_RESPONSE",response);

            String sfault=ZCSUtils.StFindNodeValue(response.getSOAPBody().getFirstChild(),"faultstring");
            if (sfault!=null)
            {
                auth_logger.log(Level.WARNING,"Auth Failed: "+sfault);                
            }
            else
            {
                auth_token= ZCSUtils.StFindNodeValue(response.getSOAPBody().getFirstChild(),"authToken");
                session_id= ZCSUtils.StFindNodeValue(response.getSOAPHeader().getFirstChild(),"sessionId");
                if ((auth_token!=null)) //&& (session_id!=null))
                {
                    //auth_logger.log(Level.INFO,"AuthToken: "+auth_token +"\nSession_ID: "+session_id);
                    ret= true;
                }
            }
        }
        catch(javax.xml.soap.SOAPException se)
        {
            auth_logger.log(Level.SEVERE,"AuthenticateToZCS javax.xml.soap.SOAPException: "+se.getMessage()); 
            auth_logger.log(Level.SEVERE,ZCSUtils.stack2string(se));
        }       
        catch(Exception exs)
        {
            auth_logger.log(Level.SEVERE,"AuthenticateToZCS Excpetion: "+exs.getMessage());
            auth_logger.log(Level.SEVERE,ZCSUtils.stack2string(exs));
        }   
        finally
        {
            if(!ret)
            {
                if(request!=null)
                    ZCSUtils.dump_soap_message("AUTH_REQUEST",request);
                
                if (response!=null)
                    ZCSUtils.dump_soap_message("AUTH_RESPONSE",response);
            }
        }
        return ret;
    }
    
    
    public void trustHttpsCertificates() throws Exception 
    {
        //hostname verifier
        HostnameVerifier hv = new HostnameVerifier()
        {
            public boolean verify(String urlHostName, SSLSession session)
            {
                System.out.println("Warning: URL Host: " + urlHostName + " vs. "
                        + session.getPeerHost());
                return true;
            }
        };
        HttpsURLConnection.setDefaultHostnameVerifier(hv);
        // Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[]
        {
            new X509TrustManager() 
               {
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() 
                    {
                        return null;
                    }
                    public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) 
                    {
                    }
                    public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) 
                    {
                    }
               }
        };
        // Install the all-trusting trust manager
        try 
        {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } 
        catch (Exception e) 
        {
            auth_logger.log(Level.SEVERE,"trustHttpsCertificates Error:" + e.getMessage());
            auth_logger.log(Level.SEVERE,ZCSUtils.stack2string(e));
        }
    }//trustHttpsCertificates()
}
