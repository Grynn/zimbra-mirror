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
package com.zimbra.zcsprov;

import java.util.logging.*;
import java.util.HashMap;
import java.util.Iterator;
import javax.xml.soap.*;

import com.zimbra.auth.*;
import com.zimbra.utils.*;

import java.io.*;
import java.io.BufferedReader;
import java.net.*;
import java.io.IOException;
import java.net.MalformedURLException;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;

public class ZMSoapSession 
{
    public static final int AUTH_TYPE_ADMIN=1;
    public static final int AUTH_TYPE_ACUSER=2;
    public static final int AUTH_TYPE_ADMIN_DEST=3;    
    private final String UPLOAD_URI="/service/upload?lbfums=&fmt=raw";
    private MessageFactory mf;
    private SOAPConnection soapconn;
    private SOAPMessage response;
    private String sZCSurl;
    private String isession_id;
    private String iauth_token;
    private ZCSAuth zcsauth;
    private Logger session_logger;
    private boolean active;
    private boolean dump_all;
    private int iAuthType;
    private String delegateAuthToken;
    public ZMSoapSession(String ZCSurl, String username, String  password,int authtype,Logger logger)
    {
        delegateAuthToken=null;
        dump_all=false;
        response=null;
        iauth_token=null;
        isession_id=null;
        active=false;
        session_logger=logger;
        sZCSurl = ZCSurl;
        iAuthType = authtype;
        try
        {
            mf = MessageFactory.newInstance();       
            soapconn = SOAPConnectionFactory.newInstance().createConnection();
            zcsauth = new ZCSAuth(ZCSurl, username, password,iAuthType ,logger);
        }
        catch(SOAPException se)
        {
            session_logger.log(Level.SEVERE, se.getMessage());
            session_logger.log(Level.SEVERE, ZCSUtils.stack2string(se));
        }
        catch(Exception ex)
        {
            session_logger.log(Level.SEVERE,ex.getMessage());
            session_logger.log(Level.SEVERE,ZCSUtils.stack2string(ex));
        }        
    }
    
    public boolean check_auth()
    {
        boolean ret=false;
        try
        {
            ret= DoZCSAuth();
        }
        catch(Exception ex)
        {
            session_logger.log(Level.SEVERE,ex.getMessage());
            session_logger.log(Level.SEVERE,ZCSUtils.stack2string(ex));
        }
        return ret;
    }
    
    public void ResetZCSUrl(String newUrl)
    {
        sZCSurl= newUrl;
    }
    
    private boolean DoZCSAuth() throws ZmProvGenericException
    {
        if(!active)
        {
            if (dump_all)
            {
                zcsauth.enable_dump_all();
            }
            //dont do zcs auth multiple time
            if(iAuthType ==AUTH_TYPE_ADMIN)
            {
                if(AuthTokens.get_admin_auth_token(sZCSurl)!=null)
                {
                    iauth_token = AuthTokens.get_admin_auth_token(sZCSurl);
                    isession_id= AuthTokens.get_admin_sessionid(sZCSurl);
                    return true;
                }
            }
            else if(iAuthType ==AUTH_TYPE_ACUSER)
            {
                if(AuthTokens.get_non_admin_auth_token(sZCSurl)!=null)
                {
                    iauth_token = AuthTokens.get_non_admin_auth_token(sZCSurl);
                    isession_id = AuthTokens.get_non_admin_sessionid(sZCSurl);
                    return true;
                }
            }
            else if(iAuthType ==AUTH_TYPE_ADMIN_DEST )
            {
                if(AuthTokens.get_dest_admin_auth_token(sZCSurl)!=null)
                {
                    iauth_token = AuthTokens.get_dest_admin_auth_token(sZCSurl);
                    isession_id = AuthTokens.get_dest_admin_sessionid(sZCSurl);
                    return true;
                }
            }
            else 
            {
                throw new ZmProvGenericException("Non-Supported AuthType: "+iAuthType);
            }
            
            active= zcsauth.AuthenticateToZCS();
            iauth_token= zcsauth.get_authtoken();
            isession_id= zcsauth.get_sessionId();
            if (isession_id==null)
                isession_id ="1";
            
            if (iAuthType ==AUTH_TYPE_ADMIN)
            {
               AuthTokens.set_admin_auth_token(sZCSurl,iauth_token);
               AuthTokens.set_admin_sessionid(sZCSurl,isession_id);
            }
            else if (iAuthType ==AUTH_TYPE_ACUSER)
            {
                AuthTokens.set_non_admin_auth_token(sZCSurl,iauth_token);
                AuthTokens.set_non_admin_sessionid(sZCSurl,isession_id);
            }
            else if (iAuthType ==AUTH_TYPE_ADMIN_DEST)
            {
                AuthTokens.set_dest_admin_auth_token(sZCSurl,iauth_token);
                AuthTokens.set_dest_admin_sessionid(sZCSurl,isession_id);
            }
        }
        return active;
    }
    
    public void CloseSession()
    {
        try
        {
            soapconn.close();
        }
        catch(SOAPException se)
        {
            session_logger.log(Level.SEVERE,ZCSUtils.stack2string(se));
            session_logger.log(Level.SEVERE,se.getMessage());
        }
        
    }
    
    public SOAPMessage get_requestObjectWithZmHeader()
    {
        SOAPMessage request=null;
        try
        {
            request = mf.createMessage();
            SOAPPart sp = request.getSOAPPart();
            SOAPEnvelope se = (SOAPEnvelope)sp.getEnvelope();
            SOAPHeader sh = se.getHeader();
            
            //Add a namespace declaration to the envelope
            se.addNamespaceDeclaration("soap", "http://schemas.xmlsoap.org/soap/envelope/");   

            //create SAOP header
            SOAPElement hdContext=sh.addChildElement("context", "", "urn:zimbra");
            hdContext.addChildElement("userAgent", "","ZimbraProvisioningTool");
            hdContext.addChildElement("format", "type","xml"); 
            //<sessionId id="1234"/>
            SOAPElement sensid = hdContext.addChildElement("sessionId");
            Name nsid = se.createName("id");
            sensid.addAttribute(nsid, isession_id);
            //<authToken>0_bd02e34f69bf036...</authToken>            
            SOAPElement athelement=hdContext.addChildElement("authToken");   
            athelement.setValue(iauth_token);            
        }
        catch(SOAPException se)
        {
            session_logger.log(Level.SEVERE,ZCSUtils.stack2string(se));
            session_logger.log(Level.SEVERE,se.getMessage());
        }
        return request;
    }
    
    public void AddAttributes(SOAPEnvelope se, SOAPElement bodyElement,HashMap<String, String> attrs)
    {
        Iterator<String> it = attrs.keySet().iterator();
        while (it.hasNext()) 
        {
            try
            {
                String attrname = it.next();
                String value=attrs.get(attrname);
                Name ne = se.createName("n");
                SOAPElement sea = bodyElement.addChildElement("a");
                sea.addAttribute(ne, attrname);
                sea.addTextNode(value);   
            }
            catch(SOAPException sx)
            {
                session_logger.log(Level.SEVERE,ZCSUtils.stack2string(sx));
                session_logger.log(Level.SEVERE,sx.getMessage());
            }
        }
    }
    
    public boolean send_request(SOAPMessage request)
    {
        boolean retval=false;
        try
        {
            response = soapconn.call(request, sZCSurl); 
            if (dump_all)
            {
                dump_soap("*******SOAP Request: ",request);
                dump_soap("*******SOAP Response: ",response);
            }
            retval=true;
        }
        catch(SOAPException se)
        {
            dump_soap("SOAP Request: ",request);
            session_logger.log(Level.SEVERE,ZCSUtils.stack2string(se));
            session_logger.log(Level.SEVERE,se.getMessage());
        }
        
        return retval;
    }
    
    public SOAPMessage get_response()
    {
        return response;
    }
    
    public String FindNodeValue(String snode)
    {
        String nodeval=null;
        try
        {
            nodeval=FindNodeValue(response.getSOAPBody().getFirstChild(),snode);
        }
        catch(SOAPException se)
        {
            session_logger.log(Level.SEVERE,ZCSUtils.stack2string(se));
            session_logger.log(Level.SEVERE,se.getMessage());
        }
        return nodeval;
    }
    
    private String FindNodeValue(Node nd, String tofind)
    {
        String retval=null;
        try
        {
            SOAPElement msgElement = (SOAPElement)nd;
            String locname=msgElement.getLocalName();
            if (locname.compareTo(tofind)==0)
            {
                retval=msgElement.getValue();
                //log_msg("FOUND: "+tofind+" -->"+retval);
                return retval;
            }
                    
            if (nd.hasChildNodes())
            {
                NodeList ndlist= nd.getChildNodes();
                int ndlen=ndlist.getLength();
                for (int i=0;i<ndlen;i++)
                {
                    if(ndlist.item(i).hasChildNodes())
                    {
                        Node tnd= ndlist.item(i);
                        retval=FindNodeValue(tnd,tofind);
                        if (retval !=null)
                            break;
                    }
                }
            }
        }
        catch(Exception e)
        {
            session_logger.log(Level.SEVERE,"FindNodeValue Exception: "+e.getMessage());
            session_logger.log(Level.SEVERE,ZCSUtils.stack2string(e));
        }
        return retval;
    }
        
    
    public String FindAttributeValue(String element, String attr)
    {
        String nodeval=null;
        try
        {
            nodeval=FindAttributeValue(response.getSOAPBody().getFirstChild(),element,attr);
        }
        catch(SOAPException se)
        {
            session_logger.log(Level.SEVERE,ZCSUtils.stack2string(se));
            session_logger.log(Level.SEVERE,se.getMessage());
        }
        return nodeval;
    }
    
    
    private String FindAttributeValue(Node nd, String element, String attr)
    {
        String retval=null; 
        try
        {
            SOAPElement msgElement = (SOAPElement)nd;
            String locname=msgElement.getLocalName();
            if (locname.compareTo(element)==0)
            {
                retval=msgElement.getAttribute(attr);
                return retval;
            }
                    
            if (nd.hasChildNodes())
            {
                NodeList ndlist= nd.getChildNodes();
                int ndlen=ndlist.getLength();
                for (int i=0;i<ndlen;i++)
                {
                    if(ndlist.item(i).hasChildNodes())
                    {
                        Node tnd= ndlist.item(i);
                        retval=FindAttributeValue(tnd,element,attr);
                        if (retval !=null)
                            break;
                    }
                }
            }
        }
        catch(Exception e)
        {
            session_logger.log(Level.SEVERE,"FindNodeValue Exception: "+e.getMessage());
            session_logger.log(Level.SEVERE,ZCSUtils.stack2string(e));
        }
        return retval;
    }
    
    private void dump_soap(String info, SOAPMessage msg)
    {
        ZCSUtils.dump_soap_message(info,msg);
    }
    
    public void dump_response(String info)
    {
        if (response!=null)
            dump_soap(info, response);
    }
    
    public void enable_dump_all()
    {
        dump_all=true;
    }
    
    public void disable_dump_all()
    {
        dump_all=false;
    }

    public String DoDelegateAuth(String username) throws ZmProvGenericException
    {
        String auth_token=null;
        if(iAuthType ==AUTH_TYPE_ADMIN)
        {
            try
            {
                SOAPMessage request = get_requestObjectWithZmHeader();
                SOAPPart sp = request.getSOAPPart();
                SOAPEnvelope se = (SOAPEnvelope)sp.getEnvelope();
                SOAPBody body = se.getBody();

                //create SOAP Body
                Name bodyName = se.createName("DelegateAuthRequest", "","urn:zimbraAdmin");
                SOAPElement bodyElement = body.addBodyElement(bodyName);

                Name nacct = se.createName("account");
                SOAPElement seacct = bodyElement.addChildElement(nacct);
                Name ne=se.createName("by");
                seacct.addAttribute(ne, "name");
                seacct.addTextNode(username);

                 //Save the message
                request.saveChanges();

                //get Response
                if (send_request(request))
                {
                    String sfault=FindNodeValue("faultstring");
                    if (sfault!=null)
                    {
                        session_logger.log(Level.SEVERE,"DelegateAuthRequest Failed: "+sfault);
                    }
                    else
                    {
                        auth_token=FindNodeValue("authToken");
                        session_logger.log(Level.INFO,"authToken: "+auth_token);
                    }
                }
                else
                {
                    dump_response("DelegateAuthRequest request Failed ");
                }
            }
            catch(Exception ex)
            {
                session_logger.log(Level.SEVERE,"DelegateAuthRequest request exception");
                session_logger.log(Level.SEVERE,ZCSUtils.stack2string(ex));
            }
        }
        else
        {
            throw new ZmProvGenericException("Delegate auth requires needs AdminAuth mode.");
        }
        delegateAuthToken= auth_token;
        return auth_token;
    }

    public String GetDelegateAuthToken()
    {
        return delegateAuthToken;
    }
    //returns token or null, if failed
    public String Upload_FileToZCS(String zcsurl, String file)
    {
        final String lineEnd = "\r\n";
        final String twoHyphens = "--";
        final String boundary =  "*****";
        final int maxBufferSize = 1*1024*1024;

        HttpURLConnection httpconn = null;
        DataOutputStream dos = null;
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        String responseFromServer = null;
        String urlString = zcsurl+UPLOAD_URI;
        session_logger.log(Level.INFO,"FILE UPLOAD URL: "+urlString);
        session_logger.log(Level.INFO,"File Path: "+file);
        try
        {
            FileInputStream fileInputStream = new FileInputStream( new File(file) );
            // open a URL connection to the Servlet 
            URL url = new URL(urlString);
            // Open a HTTP connection to the URL
            httpconn = (HttpURLConnection) url.openConnection();
            // Allow Inputs
            httpconn.setDoInput(true);
            // Allow Outputs
            httpconn.setDoOutput(true);
            // Don't use a cached copy.
            httpconn.setUseCaches(false);
            // Use a post method.
            httpconn.setRequestMethod("POST");
            httpconn.setRequestProperty("Connection", "Keep-Alive");
            httpconn.setRequestProperty("ENCTYPE","multipart/form-data");
            httpconn.setRequestProperty("Cookie", "ZM_AUTH_TOKEN="+delegateAuthToken);
            httpconn.setRequestProperty("Content-Type", "multipart/form-data;boundary="+boundary);

            dos = new DataOutputStream( httpconn.getOutputStream() );
            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"importUpload\";"
              + " filename=\"" + file +"\"" + lineEnd);
            dos.writeBytes(lineEnd);

            // create a buffer of maximum size
            bytesAvailable = fileInputStream.available();
            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            buffer = new byte[bufferSize];

            // read file and write it into form...
            bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            while (bytesRead > 0)
            {
                dos.write(buffer, 0, bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            }

            // send multipart form data necesssary after file data...
            dos.writeBytes(lineEnd);
            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

            // close streams
            fileInputStream.close();
            dos.flush();
            dos.close();
        }
        catch (MalformedURLException ex)
        {
            session_logger.log(Level.SEVERE,"Upload_FileToZCS Exception:"+ex);
        }
        catch (IOException ioe)
        {
            session_logger.log(Level.SEVERE,"Upload_FileToZCS Exception:"+ioe);
        }
        
        //Get response input stream
        try
        {
            StringBuffer httpresponse = new StringBuffer();            
            BufferedReader inputStream = null;
            inputStream = new BufferedReader(new InputStreamReader(httpconn.getInputStream()));
            String line;
            while ((line = inputStream.readLine()) != null) 
            {
                    httpresponse.append(line + "\n");
            }
            responseFromServer = httpresponse.toString();
            session_logger.log(Level.INFO,"Contens aid: "+responseFromServer);
            String strarr[]=responseFromServer.split(",");
            //if HTTP 200 (OK) is recieved else return null
            if (strarr[0].compareTo("200")==0)
            {
                responseFromServer = strarr[2];
                responseFromServer=responseFromServer.replaceAll("'", "");
                responseFromServer=responseFromServer.replaceAll("\n", "");
            }
            else
                responseFromServer=null;
        }
        catch (IOException ioex)
        {
            session_logger.log(Level.SEVERE,"Upload_FileToZCS exception: "+ioex);
        }
        return responseFromServer;
    }
    
    private void print_inputstream(DataInputStream is)
    {
        try
        {
            String str;
            while (( str = is.readLine()) != null)
            {
                System.out.println(str);
                System.out.println("");
            }
            is.close();
        }
        catch (IOException ioex)
        {
            System.out.println("print_inputstream Exception: "+ioex);
        }
    }
}
