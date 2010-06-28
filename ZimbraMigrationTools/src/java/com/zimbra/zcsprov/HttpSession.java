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

import java.util.List;
import java.io.*;
import java.security.Security;

import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.params.HttpClientParams;

import org.w3c.dom.*;

import javax.xml.parsers.*;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class HttpSession
{
    private HttpClient httpclient;
    private List<NameValuePair> queryparams; 
    private List<NameValuePair> reqparams; 
    private boolean IsPost;
    private ByteArrayOutputStream ibaos;
    private FileInputStream fileinput;
    private Transformer transformer;
    private ByteArrayOutputStream baos;
    private int Statuscode;
    public HttpSession()
    {
        IsPost=false;
        queryparams=null;
        reqparams= null;
        fileinput=null;
        transformer = createTransformer();
        baos = new ByteArrayOutputStream(4096);
        httpclient =new HttpClient();
        //default time out 5 minutes
        httpclient.setTimeout(5*60*1000);
        Security.setProperty( "ssl.SocketFactory.provider", 
                                "com.zimbra.utils.ZMSSLSocketFactory");
        
    }
    
    public void SetPostMethod(boolean ispost)
    {
        IsPost = ispost;
    }
    
    public int GetStatusCode()
    {
        return Statuscode;
    }

    public void SetTimeOut(int miliseconds)
    {
        httpclient.setTimeout(miliseconds);
    }
    
    public InputStream Send(String uri) throws HttpException
    {
        InputStream is=null;
        try
        {
            HttpMethod method = GetHttpMethod(uri);
            Statuscode = httpclient.executeMethod(method);
            if (Statuscode != 200) 
            {
                throw new HttpException("HTTP request failed: " + Statuscode + ": " +
                                        HttpStatus.getStatusText(Statuscode));
            }
            is = method.getResponseBodyAsStream();
        }
        catch(HttpException he)
        {
            throw he;
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
        return is;
    }
    
    public void SetQueryString(List<NameValuePair> qparams)
    {
        queryparams = qparams;
    }
    
    public void SetRequestHeader(List<NameValuePair> rparams)
    {
        reqparams = rparams;
    }

    public void SetFileStream(String stfile) throws FileNotFoundException
    {
        try
        {
            fileinput = new FileInputStream(stfile);
        }
        catch (FileNotFoundException fex)
        {
            fileinput=null;
            throw fex;
        }
    }
    
    public void SetRequestBody(ByteArrayOutputStream baos)
    {
        ibaos= baos;
    }
    
    public Document GetNewDocumentObject()
    {
        Document doc=null;
        DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
        try
        {
            DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
            doc = docBuilder.newDocument();
        }
        catch(ParserConfigurationException pex)
        {
            pex.getMessage();
            throw new IllegalStateException("",pex);
        }
        
        return doc;

    }
    public void SetRequestBody(Document doc)
    {
        baos.reset();
        try 
        {
            transformer.transform(new DOMSource(doc), new StreamResult(baos));
            SetRequestBody(baos);
        } catch (TransformerException e) 
        {
            throw new IllegalStateException("Could not serialize document", e);
        }
    }
    
    private HttpMethod GetHttpMethod(String uri)
    {
        HttpMethod hmethod = IsPost ? new PostMethod(uri) : new GetMethod(uri);
        if(queryparams!=null)
            hmethod.setQueryString(queryparams.toArray(new NameValuePair[queryparams.size()]));
        if (reqparams!=null)
        {
            for (int i=0; i<reqparams.size();i++)
            {
                NameValuePair nvp= reqparams.get(i);
                hmethod.addRequestHeader(nvp.getName(), nvp.getValue());
            }
        }
        if(IsPost)
        {
            if (fileinput!=null)
            {
                ((PostMethod)hmethod).setRequestBody(fileinput);
            }
            else
            {
                ((PostMethod)hmethod).setRequestBody(new ByteArrayInputStream(ibaos.toByteArray()));
            }

        }          
        return hmethod;
    }
    
    private static Transformer createTransformer() 
    {
        try 
        {
            return TransformerFactory.newInstance().newTransformer();
        } catch (TransformerConfigurationException e) 
        {
            throw new IllegalStateException("Unable to create Transformer", e);
        }
    }
    
    public String XmlDocToString(Document doc) 
    {
        baos.reset();
        try 
        {
            transformer.transform(new DOMSource(doc), new StreamResult(baos));
        } catch (TransformerException e) 
        {
            throw new IllegalStateException("Could not serialize document", e);
        }
        return baos.toString();
    }

    public void UploadFile()
    {
        String url="https://10.66.118.107:7071/home/test2@zcs1.zmexch.in.zimbra.com?fmt=tgz";
        String fPath="C:\\Zimbra_Work\\YZYMigration\\ZCSProvisioning\\zcsprov\\mailboxdumps\\test2.tgz";
        //"C:\\Zimbra_Work\\YZYMigration\\ZCSProvisioning\\zcsprov\\mailboxdumps\\test2.tgz"
        PostMethod postMethod = new PostMethod(url);
        httpclient.setConnectionTimeout(0);
        //Cookie mycookie = new Cookie()
        postMethod.setRequestHeader("Cookie","ZM_ADMIN_AUTH_TOKEN=0_3dcf8ae7df711b79f720661326cb75600d819652_69643d33363a39333061643238332d313534622d343761312d626130642d3566316262306533396362383b6578703d31333a313234343333353438323435333b61646d696e3d313a313b747970653d363a7a696d6272613b");
        File f = new File(fPath);
        System.out.println("File Length = " + f.length());

        FileInputStream fis = null;
                try {
                        fis = new FileInputStream(f);
                } catch (FileNotFoundException exc) {
                        // TODO Auto-generated catch block
                        exc.printStackTrace();
                }
        postMethod.setRequestBody(fis);
        postMethod.setRequestHeader("Content-type",
            "application/x-compressed");

        try {
                httpclient.executeMethod(postMethod);

                } catch (Exception exce) {
                        // TODO Auto-generated catch block
                        exce.printStackTrace();
                }
        System.out.println("statusLine>>>" + postMethod.getStatusLine());
        postMethod.releaseConnection();        
    }
}


