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

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.logging.*;
import java.util.Iterator;
import java.util.HashMap;
import java.util.ArrayList;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.Name;
import com.zimbra.common.user_info;
import com.zimbra.utils.*;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.NameValuePair;

public class ZCSAccounts
{
    private user_info iuinfo;
    private Logger cu_logger;
    ZMSoapSession zmcusession;
    public ZCSAccounts(ZMSoapSession session, Logger logger)
    {
        cu_logger= logger;
        zmcusession = session;
        zmcusession.check_auth();
    }
    
    public void set_user_info(user_info uinfo)
    {
        iuinfo=uinfo;
    }
    
    public boolean create_Account(boolean custommode, final StringBuffer outstr)
    {
        HashMap<String,String> attrs= new HashMap<String, String>();
        boolean retval=false;
        try
        {
            SOAPMessage request = zmcusession.get_requestObjectWithZmHeader(); 
            SOAPPart sp = request.getSOAPPart();
            SOAPEnvelope se = (SOAPEnvelope)sp.getEnvelope();
            SOAPBody body = se.getBody();
            
            //create SOAP Body
            Name bodyName = se.createName("CreateAccountRequest", "","urn:zimbraAdmin");
            SOAPElement bodyElement = body.addBodyElement(bodyName);
            
            //add <name>username@mydomain.com</name>
            Name uname = se.createName("name");
            SOAPElement seuname = bodyElement.addChildElement(uname);
            seuname.addTextNode(iuinfo.username);
            
            //remove password element for provisioned accounts
            //auth would be done thru Y! UDB
            
            //add <password>pwd</password>
            if(custommode)
            {
                Name pwd = se.createName("password");
                SOAPElement sepwd = bodyElement.addChildElement(pwd);
                sepwd.addTextNode(iuinfo.password);
            }

            //account status
            attrs.put("zimbraAccountStatus", iuinfo.ZimbraAcctStatus);            
            //<a n="zimbraCOSId">de7df32b-38c1-451a-a990-59904c7cc0b7</a>   
            if(iuinfo.zimbraCOSId.compareTo("")!=0)
            {
                attrs.put("zimbraCOSId", iuinfo.zimbraCOSId);
            }
            //<a n="description">description here</a> 
            attrs.put("description", iuinfo.description);
            
/*          admin=FALSE,
            domainAdmin=TRUE.*/
            //is domain admin account?
            attrs.put("zimbraIsDomainAdminAccount", iuinfo.zimbraDomainAdmin);
            //is account admin?
            attrs.put("zimbraIsAdminAccount", "FALSE");//MUST BE FALSE, ALWAYS!
            //pref mail forwarding address
            attrs.put("zimbraPrefMailForwardingAddress", iuinfo.zimbraPrefMailForwardingAddress);
            //display name
            attrs.put("displayName", iuinfo.displayname);
            //add<zimbraYahooID>abcde-12345-qwer</zimbraYahooID>
            attrs.put("zimbraYahooID", iuinfo.zimbraYahooID);
            zmcusession.AddAttributes(se, bodyElement, attrs);
            //Save the message
            request.saveChanges();

            //get Response
            if (zmcusession.send_request(request))
            {
                String sfault=zmcusession.FindNodeValue("faultstring");
                if (sfault!=null)
                {
                    outstr.append(sfault);
                    cu_logger.log(Level.SEVERE,"Create User Failed: "+sfault);                
                }
                else
                {
                    retval=true;
                }
            }
            else
            {
                zmcusession.dump_response("create_user Failed ");
            }
        }
        catch(Exception e)
        {
            cu_logger.log(Level.SEVERE,"create_user Excpetion: "+e.getMessage());
            cu_logger.log(Level.SEVERE, ZCSUtils.stack2string(e));
        }
        return retval;
    }
    
    public String GetAccountIDByName(String usrname)
    {
        String retval=null;        
        try
        {
            SOAPMessage request = zmcusession.get_requestObjectWithZmHeader(); 
            SOAPPart sp = request.getSOAPPart();
            SOAPEnvelope se = (SOAPEnvelope)sp.getEnvelope();
            SOAPBody body = se.getBody();
            
            //create SOAP Body
            Name bodyName = se.createName("GetAccountRequest", "","urn:zimbraAdmin");
            Name ne = se.createName("applyCos");
            SOAPElement bodyElement = body.addBodyElement(bodyName);
            bodyElement.addAttribute(ne, "0");
            
            //add <account by="id">3a4f022a-3f86-4b77-be25-dce1cdad7213</account>
            Name nacct = se.createName("account");
            SOAPElement seacct = bodyElement.addChildElement(nacct);
            ne=se.createName("by");
            seacct.addAttribute(ne, "name");
            seacct.addTextNode(usrname);            
                     
             //Save the message
            request.saveChanges();

            //get Response  
            if (zmcusession.send_request(request))
            {
                String sfault=zmcusession.FindNodeValue("faultstring");
                if (sfault!=null)
                {
                    cu_logger.log(Level.SEVERE,"GetAccountRequest Failed: "+sfault);                
                }
                else
                {
                    String zimbraId=zmcusession.FindAttributeValue("account","id");
                    cu_logger.log(Level.INFO,"ZIMBRA-ID: "+zimbraId); 
                    retval= zimbraId;
                }
            }
            else
            {
                zmcusession.dump_response("GetAccountRequest Failed ");
            }
        }
        catch(Exception e)
        {
            cu_logger.log(Level.SEVERE,"GetAccountRequest Excpetion: "+e.getMessage());
            cu_logger.log(Level.SEVERE, ZCSUtils.stack2string(e));
        }
        return retval;
    }

    public ArrayList<String> GetAllAccountsListByREST(String url,String zmauth_token)
    {
        ArrayList<String> iAccountList=null;
        InputStream istr=null;
        HttpSession httpsession = new HttpSession();
        try
        {
            httpsession.SetPostMethod(false);
            String uri=url;
            List<NameValuePair> rparams = new ArrayList <NameValuePair>();
            NameValuePair param1 = new NameValuePair("Cookie", "ZM_ADMIN_AUTH_TOKEN="+zmauth_token);
            rparams.add(param1);            
            httpsession.SetRequestHeader(rparams);
            httpsession.SetTimeOut(30*60*1000);//30 minutes
            istr= httpsession.Send(uri);
            int stcode=httpsession.GetStatusCode();
            cu_logger.log(Level.INFO, "GetAllAccountsListByREST: HTTP Code: "+Integer.toString(stcode));
        }
        catch(HttpException he)
        {
            if (httpsession.GetStatusCode()==204) //NO content; Most probably mailbox is empty
            {
                cu_logger.log(Level.WARNING,"GetAllAccountsListByREST: "+he.getMessage()+" (MailBox may be empty.)");
            }
            else
            {
                cu_logger.log(Level.SEVERE,"GetAllAccountsListByREST: "+he.getMessage());
            }
            return null;
        }
        if (istr!=null)
        {
            iAccountList=new ArrayList<String>();
            String strLine = null;
            try
            {
                BufferedReader br = new BufferedReader(new InputStreamReader(istr,"UTF-8"));
                while( (strLine = br.readLine()) != null)
                {
                    cu_logger.log(Level.INFO,strLine);
                    String[] values = strLine.split(",");
                    int startidx=0;
                    int endidx = values[0].length();
                    //remove starting and end " (quotes)
                    if (values[0].startsWith("\""))
                    {
                        startidx = 1;
                    }
                    if (values[0].startsWith("\""))
                    {
                        endidx = endidx-1;
                    }
                    String strAccnt= values[0].substring(startidx,endidx);
                    iAccountList.add(strAccnt);
                }
                br.close(); 
            }
            catch(Exception e)
            {
                cu_logger.log(Level.SEVERE,"GetAllAccountsListByREST: BufferedReader Error: "+e.getMessage());
            }
        }
        return iAccountList;
    }
    
    public ArrayList<String> GetAllAccountsList(String stdomain)
    {
        ArrayList<String> iAccountList=null;
        try
        {
            SOAPMessage request = zmcusession.get_requestObjectWithZmHeader();
            SOAPPart sp = request.getSOAPPart();
            SOAPEnvelope se = (SOAPEnvelope)sp.getEnvelope();
            SOAPBody body = se.getBody();

            //create SOAP Body
            /*<SearchDirectoryRequest types="accounts" domain="test.iam.com" limit="5"
            sortBy="name" sortAscending="1" applyConfig="false" applyCos="false" offset="0"
            attrs="name" xmlns="urn:zimbraAdmin">*/
            Name bodyName = se.createName("SearchDirectoryRequest", "","urn:zimbraAdmin");
            SOAPElement bodyElement = body.addBodyElement(bodyName);

            Name netypes = se.createName("types");
            bodyElement.addAttribute(netypes, "accounts");

            Name nedomain = se.createName("domain");
            bodyElement.addAttribute(nedomain, stdomain);

            Name nesortBy = se.createName("sortBy");
            bodyElement.addAttribute(nesortBy, "name");

            Name nesortAscending = se.createName("sortAscending");
            bodyElement.addAttribute(nesortAscending, "1");

            Name neapplyConfig = se.createName("applyConfig");
            bodyElement.addAttribute(neapplyConfig, "false");

            Name neapplyCos = se.createName("applyCos");
            bodyElement.addAttribute(neapplyCos, "false");

            Name neoffset = se.createName("offset");
            bodyElement.addAttribute(neoffset, "0");

            Name neattrs = se.createName("attrs");
            bodyElement.addAttribute(neattrs, "name");                       

            //add <account by="id">3a4f022a-3f86-4b77-be25-dce1cdad7213</account>
            Name naquery = se.createName("query");
            SOAPElement seacct = bodyElement.addChildElement(naquery);
            seacct.addTextNode("(objectclass=zimbraAccount)");

             //Save the message
            request.saveChanges();

            //get Response
            if (zmcusession.send_request(request))
            {
                String sfault=zmcusession.FindNodeValue("faultstring");
                if (sfault!=null)
                {
                    cu_logger.log(Level.SEVERE,"SearchDirectoryRequest Failed: "+sfault);
                }
                else
                {
                    iAccountList=zmcusession.GetMultipleAttributeList("account","name");
                }
            }
            else
            {
                zmcusession.dump_response("GetAccountRequest Failed ");
            }
        }
        catch(Exception e)
        {
            cu_logger.log(Level.SEVERE,"GetAccountRequest Excpetion: "+e.getMessage());
            cu_logger.log(Level.SEVERE, ZCSUtils.stack2string(e));
        }
        return iAccountList;
    }
    public boolean ModifyAccount(String Request,String zimbraid,HashMap<String, String> attrs)
    {
        boolean retval =false;
        try
        {
            SOAPMessage request = zmcusession.get_requestObjectWithZmHeader();
            SOAPPart sp = request.getSOAPPart();
            SOAPEnvelope se = (SOAPEnvelope)sp.getEnvelope();
            SOAPBody body = se.getBody();
            
            //create SOAP Body
            Name bodyName = se.createName(Request, "","urn:zimbraAdmin");
            SOAPElement bodyElement = body.addBodyElement(bodyName);
            
            //add <id>3a4f022a-3f86-4b77-be25-dce1cdad7213</id>
            SOAPElement seid = bodyElement.addChildElement("id");
            seid.addTextNode(zimbraid);      
            
            if((attrs.size()==1)&&(Request.compareTo("SetPasswordRequest")==0))
            {
                Iterator<String> it = attrs.keySet().iterator();
                String attrname = it.next();
                String value=attrs.get(attrname);
                SOAPElement sepwd = bodyElement.addChildElement(attrname);
                sepwd.addTextNode(value);  
            }
            else
            {
                zmcusession.AddAttributes(se, bodyElement, attrs);
            }
                        
            //Save the message
            request.saveChanges();

            if (zmcusession.send_request(request))
            {
                String sfault=zmcusession.FindNodeValue("faultstring");
                if (sfault!=null)
                {
                    cu_logger.log(Level.SEVERE,"ModifyAccount: "+Request+" FAILED: "+sfault);                
                }
                else
                {
                    retval=true;
                }
            }
            else
            {
                zmcusession.dump_response("ModifyAccount: "+Request+" FAILED");
            }
        }
        catch(Exception e)
        {
            cu_logger.log(Level.SEVERE,"ModifyAccount: "+Request+ "Excpetion: "+e.getMessage());
            cu_logger.log(Level.SEVERE, ZCSUtils.stack2string(e));
        }
        return retval;
    }
    
    public boolean ModifyAccountStatus(String zimbraid, String status)
    {
        HashMap<String,String> attrs= new HashMap<String, String>();
        attrs.put("zimbraAccountStatus", status);
        return ModifyAccount("ModifyAccountRequest",zimbraid,attrs);
    }
    
    public boolean ModifyAccountMailTransport(String zimbraid, String MailTransport)
    {
        HashMap<String,String> attrs= new HashMap<String, String>();
        attrs.put("zimbraMailTransport", MailTransport);
        return ModifyAccount("ModifyAccountRequest",zimbraid,attrs);
    }

    public boolean AddAccountAliasRequest(String zimbraid, String alias)
    {
        HashMap<String,String> attrs= new HashMap<String, String>();
        attrs.put("alias", alias);
        return ModifyAccount("AddAccountAliasRequest",zimbraid,attrs);
    }
    
    public boolean SetPassword(String zimbraid,String pwd) //pwd empty to remove it
    {
        HashMap<String,String> attrs= new HashMap<String, String>();
        attrs.put("newPassword", pwd);
        return ModifyAccount("SetPasswordRequest",zimbraid,attrs);
    }
    
}