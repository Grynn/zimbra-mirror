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
import java.util.ArrayList;
import java.util.List;
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
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.HttpException;

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

    public ArrayList<String> GetMultipleAttributeList(String element, String attr)
    {
         ArrayList<String> AttrValList=null;
        try
        {
            AttrValList=GetMultipleAttributeList(response.getSOAPBody().getFirstChild(),element,attr);
        }
        catch(SOAPException se)
        {
            session_logger.log(Level.SEVERE,ZCSUtils.stack2string(se));
            session_logger.log(Level.SEVERE,se.getMessage());
        }
        return AttrValList;
    }

    private ArrayList<String> GetMultipleAttributeList(Node nd, String element, String attr)
    {
        ArrayList<String> AttrValList= new ArrayList<String>();  
        String retval=null;
        try
        {
            SOAPElement msgElement = (SOAPElement)nd;
            String locname=msgElement.getLocalName();
            if (locname.compareTo(element)==0)
            {
                retval=msgElement.getAttribute(attr);
                AttrValList.add(retval);
            }

            if (nd.hasChildNodes())
            {
                NodeList ndlist= nd.getChildNodes();
                int ndlen=ndlist.getLength();
                for (int i=0;i<ndlen;i++)
                {
                    SOAPElement imsgElement = (SOAPElement)ndlist.item(i);
                    String ilocname=imsgElement.getLocalName();
                    if (ilocname.compareTo(element)==0)
                    {
                        retval=imsgElement.getAttribute(attr);
                        AttrValList.add(retval);
                    }
                    if(ndlist.item(i).hasChildNodes())
                    {
                        Node tnd= ndlist.item(i);
                        GetMultipleAttributeList(tnd,element,attr);
                    }
                }
            }
        }
        catch(Exception e)
        {
            session_logger.log(Level.SEVERE,"GetMultipleAttributeList Exception: "+e.getMessage());
            session_logger.log(Level.SEVERE,ZCSUtils.stack2string(e));
        }
        return AttrValList;
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

    //use for ZCS files upload. returns content aid token or null, if failed
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

    //Upload files(e.g. tar mailbox) to rest urls using HttpURLConnection
    //Needs Admin auth token
    public boolean Upload_FileToZCS_2(String zcsurl, String file,String ContentType,Logger ufzlog,
                                      double[] exc_array,int inst_number,boolean debug)
    {
        final int maxBufferSize = 1*1024*1024;
        boolean retval=false;
        HttpURLConnection httpconn = null;
        DataOutputStream dos = null;
        int bytesRead, bytesAvailable, bufferSize;
        double totalbytes_uploaded=0;
        byte[] buffer;
        String urlString = zcsurl;
        ufzlog.log(Level.INFO,"File Upload URL: "+urlString);
        ufzlog.log(Level.INFO,"File Path: "+file);
        
        try
        {
            File nFile = new File(file);
            FileInputStream fileInputStream = new FileInputStream(nFile);
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
            httpconn.setRequestProperty("Cookie", "ZM_ADMIN_AUTH_TOKEN="+iauth_token);
            httpconn.setRequestProperty("Content-Type", ContentType);
            session_logger.log(Level.INFO,"File Size: "+String.valueOf(nFile.length()));
            httpconn.setAllowUserInteraction(false);
            //set chunked stream mode else will get out of memory exception for larger uploads
            httpconn.setChunkedStreamingMode(maxBufferSize);
            dos = new DataOutputStream( httpconn.getOutputStream() );
            // create a buffer of maximum upload size
            bytesAvailable = fileInputStream.available();
            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            buffer = new byte[bufferSize];

            // read file and write it to outstream...
            bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            String outstr="";
            while (bytesRead > 0)
            {
                dos.write(buffer, 0, bufferSize);
                totalbytes_uploaded += bufferSize;
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                //update console****>>>>>
                outstr="";
                exc_array[inst_number-1]=(totalbytes_uploaded/1000);
                for (int j=0;j<exc_array.length;j++)
                {
                    outstr=outstr+"  "+Double.toString(exc_array[j]);
                }
                //System.out.print("Upload (KBytes): "+outstr);
                UpDateConsole.SetUploadInfo(outstr);
                UpDateConsole.PrintConsole();
                //**************<<<<<<<<<<<
            }
            // close streams
            fileInputStream.close();
            dos.flush();
            dos.close();
            retval=true;
        }
        catch (MalformedURLException ex)
        {
            ufzlog.log(Level.SEVERE,"Upload_FileToZCS Exception:"+ex);
            ufzlog.log(Level.SEVERE,ZCSUtils.stack2string(ex));
        }
        catch (IOException ioe)
        {
            ufzlog.log(Level.SEVERE,"Upload_FileToZCS Exception:"+ioe);
            ufzlog.log(Level.SEVERE,ZCSUtils.stack2string(ioe));
        }

        //Check Response
        if(retval)
        {
            retval=false;
            try
            {
                String RespMessage=httpconn.getResponseMessage();
                ufzlog.log(Level.INFO,"Response Message: "+RespMessage);
                int RespCode = httpconn.getResponseCode();
                if(RespCode == 200)
                {
                    retval = true;
                }
            }
            catch(NullPointerException nex)
            {
                ufzlog.log(Level.SEVERE,"Upload_FileToZCS NullPointerException exception: "+nex);
                ufzlog.log(Level.SEVERE,ZCSUtils.stack2string(nex));
            }
            catch (Exception ex)
            {
                ufzlog.log(Level.SEVERE,"Upload_FileToZCS exception: "+ex);
                ufzlog.log(Level.SEVERE,ZCSUtils.stack2string(ex));
            }
        }
        return retval;
    }

    public boolean UploadFileUsingHttpSession(String url, String stfile, String ContentType)
    {
        boolean retval=true;
        InputStream is=null;
        HttpSession httpsession = new HttpSession();
        httpsession.SetPostMethod(true);
        try
        {
            httpsession.SetFileStream(stfile);
        }
        catch(FileNotFoundException fex)
        {
            session_logger.log(Level.SEVERE,"UploadFile exception: "+fex);
            retval=false;
        }

        List<NameValuePair> rparams = new ArrayList <NameValuePair>();
        NameValuePair param1 = new NameValuePair("Cookie", "ZM_ADMIN_AUTH_TOKEN="+iauth_token);
        rparams.add(param1);
        NameValuePair param2 = new NameValuePair("Content-type",ContentType);
        rparams.add(param2);
        httpsession.SetRequestHeader(rparams);
        try
        {
            is= httpsession.Send(url);
        }
        catch(HttpException he)
        {
            session_logger.log(Level.SEVERE,he.getMessage());
            retval=false;
        }
        
        if(is!=null)
        {
            //do nothing   
        }
        else
        {
            retval=false;
        }
        return retval;
    }

    public boolean Download_FileDFromZCS(String zcsurl,String filetodownload,int instance_number,
                                         double[] exc_array,Logger gtmbLog,boolean debug)
    {
        boolean retval=false;
        InputStream istr=null;
        HttpSession httpsession = new HttpSession();
        try
        {
            httpsession.SetPostMethod(false);
            String uri=zcsurl;
            istr= httpsession.Send(uri);
        }
        catch(HttpException he)
        {
            if (httpsession.GetStatusCode()==204) //NO content; Most probably mailbox is empty
            {
                gtmbLog.log(Level.WARNING,he.getMessage()+" (MailBox may be empty.)");
            }
            else
            {
                gtmbLog.log(Level.SEVERE,he.getMessage());
            }
            return retval;
        }
        if (istr!=null)
        {
            try
            {
                retval=writeToFile(filetodownload,istr,true,
                        instance_number,exc_array,gtmbLog,debug);
            }
            catch(Exception ex)
            {
                gtmbLog.log(Level.SEVERE,ZCSUtils.stack2string(ex));
                gtmbLog.log(Level.SEVERE,"Exception in mailbox download"+" ("+filetodownload+") ");
                retval=false;
            }
        }
        else
        {
            gtmbLog.log(Level.SEVERE,"FATAL:Source Mail box"+"("+filetodownload+")"+" stream couldn't be found.");
        }
       
        return retval;
    }


private boolean writeToFile(String fileName, InputStream iStream,
        boolean createDir,int instance_number,double[] exc_array,Logger wflog,boolean debug)
        throws IOException
    {
        boolean retval =true;
        String me = "FileUtils.WriteToFile";
        if (fileName == null)
        {
            throw new IOException(me + ": filename is null");
        }
        if (iStream == null)
        {
            throw new IOException(me + ": InputStream is null");
        }

        File theFile = new File(fileName);

        // Check if a file exists.
        if (theFile.exists())
        {
            String msg =
                theFile.isDirectory() ? "directory" :
                    (! theFile.canWrite() ? "not writable" : null);
            if (msg != null)
            {
                throw new IOException(me + ": file '" + fileName + "' is " + msg);
            }
        }

        // Create directory for the file, if requested.
        if (createDir && theFile.getParentFile() != null)
        {
            theFile.getParentFile().mkdirs();
        }

        // Save InputStream to the file.
        BufferedOutputStream fOut = null;
        double biTotBytesWritten=0;
        StringBuilder s = new StringBuilder();
        int y = 0;
        try
        {
            fOut = new BufferedOutputStream(new FileOutputStream(theFile));
            byte[] buffer = new byte[32 * 1024];
            int bytesRead = 0;
            long bytdiff=0; double prevbyt=0;
            long update_afterMB=1*1024*1024;
            String outstr="";
            while ((bytesRead = iStream.read(buffer)) != -1)
            {
                outstr="";
                biTotBytesWritten+= bytesRead;
                fOut.write(buffer, 0, bytesRead);
                //update console****>>>>>
                exc_array[instance_number-1]=(biTotBytesWritten/1000);
                for (int j=0;j<exc_array.length;j++)
                {
                    outstr=outstr+"  "+Double.toString(exc_array[j]);
                }
                if((biTotBytesWritten - prevbyt)>update_afterMB)
                {
                    UpDateConsole.SetDownloadInfo(outstr);
                    UpDateConsole.PrintConsole();
                    prevbyt=biTotBytesWritten;
                }
                //******<<<<<<<<<<<<<<<<<<<
            }
            UpDateConsole.SetDownloadInfo(outstr);
            UpDateConsole.PrintConsole();

            wflog.log(Level.SEVERE,"Download Finished("+fileName+")"+ ": Total KBytes downloaded: "+(biTotBytesWritten/1000));
        }
        catch (Exception e)
        {
            retval=false;
            wflog.log(Level.SEVERE,"Download error("+fileName+"): "+ e.toString()+
                    ": Total KBytes downloaded: "+(biTotBytesWritten/1000));
            throw new IOException(me + " failed, got: " + e.toString());
        }
        finally
        {
            close(iStream, fOut);
        }
        return retval;
    }

    private void close(InputStream iStream, OutputStream oStream)
            throws IOException
    {
        try
        {
            if (iStream != null)
            {
                iStream.close();
            }
        }
        finally
        {
            if (oStream != null)
            {
                oStream.close();
            }
        }
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

class UpDateConsole
{
    private static String Downloadinfo="NA";
    private static String Uploadinfo="NA";
    public static void SetDownloadInfo(String info)
    {
        Downloadinfo= info;
    }
    public static void SetUploadInfo(String info)
    {
        Uploadinfo = info;
    }
    public static void PrintConsole()
    {
        System.out.print("\rDownload(KB): "+ Downloadinfo+"  Upload(KB): "+Uploadinfo);
    }
}
