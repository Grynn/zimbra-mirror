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
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.security.Security;

import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.HttpClient;

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
    private Transformer transformer;
    private ByteArrayOutputStream baos;
    private int Statuscode;
    public HttpSession()
    {
        IsPost=false;
        queryparams=null;
        reqparams= null;
        transformer = createTransformer();
        baos = new ByteArrayOutputStream(4096);
        httpclient =new HttpClient();
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
    
    public InputStream Send(String uri)
    {
        InputStream is=null;
        try
        {
            HttpMethod method = GetHttpMethod(uri);
            httpclient.setTimeout(60*1000);
            Statuscode = httpclient.executeMethod(method);
            if (Statuscode != 200) 
            {
                throw new HttpException("HTTP request failed: " + Statuscode + ": " +
                                        HttpStatus.getStatusText(Statuscode));
            }
            is = method.getResponseBodyAsStream();            
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
            ((PostMethod)hmethod).setRequestBody(new ByteArrayInputStream(ibaos.toByteArray()));

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
}


