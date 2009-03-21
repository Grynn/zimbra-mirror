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
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.Name;
import com.zimbra.utils.*;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;

public class ZCSImportFile 
{
    private String icontent_aid;
    ZMSoapSession zmsession;
    Logger ImpLogger;
    String iyuser;
    public ZCSImportFile(ZMSoapSession session,String yuser ,String content_aid, Logger logger)
    {
        icontent_aid= content_aid;
        zmsession = session;
        ImpLogger=logger;
        iyuser=yuser;
    }
    
    public boolean UploadFile()
    {
        boolean ret=false;
        try
        {
            SOAPMessage request=zmsession.get_requestObjectWithZmHeader();
            SOAPPart sp = request.getSOAPPart();
            SOAPEnvelope se = (SOAPEnvelope)sp.getEnvelope();
            SOAPBody body = se.getBody();
            SOAPHeader sh = se.getHeader();
            Name nshcontext = se.createName("context");
            sh.getChildElements(nshcontext);
            SOAPElement nd= (SOAPElement)sh.getChildNodes().item(0);
            //Add Account info explictly in request header
            //<account by="name">av2@migration-dev</account>
            Name bne = se.createName("by");
            SOAPElement seZn = nd.addChildElement("account");
            seZn.addAttribute(bne, "name");
            seZn.addTextNode(iyuser);
            //override admin auth token by user's delegate auth token
            NodeList cntxNodeList= nd.getChildNodes();
            for (int i=0; i<cntxNodeList.getLength();i++)
            {
                Node tnd=cntxNodeList.item(i);
                System.out.println("NodeName: "+tnd.getNodeName());
                if (tnd.getNodeName().compareTo("authToken")==0)
                {
                    nd.removeChild(tnd);
                    Name athName= se.createName("authToken");
                    SOAPElement athElem=nd.addChildElement(athName);
                    athElem.addTextNode(zmsession.GetDelegateAuthToken());
                    ImpLogger.log(Level.INFO,"Overrided authToken To: "+zmsession.GetDelegateAuthToken());
                }
            }
            
            //create SOAP Body
            Name bodyName = se.createName("ImportContactsRequest", "","urn:zimbraMail");
            SOAPElement bodyElement = body.addBodyElement(bodyName);
            Name ctne= se.createName("ct");
            bodyElement.addAttribute(ctne, "csv");
            //<content aid="fb13...87a-48658f338f50"></content>
            Name ane = se.createName("aid");
            SOAPElement seGLDUrl = bodyElement.addChildElement("content");
            seGLDUrl.addAttribute(ane, icontent_aid);
            
            //Save the message
            request.saveChanges();

            //Get Response
            if (zmsession.send_request(request))
            {
                String sfault=zmsession.FindNodeValue("faultstring");
                if (sfault!=null)
                {
                    ImpLogger.log(Level.SEVERE,"UploadFile SOAP fault: "+sfault);
                }
                else
                {
                    ret=true;
                    ImpLogger.log(Level.INFO,"File uploaded successfully: "+icontent_aid);
                }
            }
            else
            {
                zmsession.dump_response("UploadFile send_request Failed ");
            }
        }
        catch(Exception e)
        {
            ImpLogger.log(Level.SEVERE,"UploadFile Excpetion: "+e.getMessage());
            ImpLogger.log(Level.SEVERE, ZCSUtils.stack2string(e));
        }
        
        return ret;
    }
    
}
