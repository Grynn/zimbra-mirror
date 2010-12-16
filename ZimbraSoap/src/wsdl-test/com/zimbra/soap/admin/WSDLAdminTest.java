/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2010 Zimbra, Inc.
 *
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.3 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.soap.admin;

import java.util.List;

import javax.xml.ws.soap.SOAPFaultException;

import com.sun.xml.ws.developer.WSBindingProvider;

import com.zimbra.soap.admin.wsimport.generated.AdminService;
import com.zimbra.soap.admin.wsimport.generated.Account;
import com.zimbra.soap.admin.wsimport.generated.AccountInfo;
import com.zimbra.soap.admin.wsimport.generated.Attr;
import com.zimbra.soap.admin.wsimport.generated.By;
import com.zimbra.soap.admin.wsimport.generated.CreateDomainRequest;
import com.zimbra.soap.admin.wsimport.generated.CreateDomainResponse;
import com.zimbra.soap.admin.wsimport.generated.CreateServerRequest;
import com.zimbra.soap.admin.wsimport.generated.CreateServerResponse;
import com.zimbra.soap.admin.wsimport.generated.DeleteDomainRequest;
import com.zimbra.soap.admin.wsimport.generated.DeleteDomainResponse;
import com.zimbra.soap.admin.wsimport.generated.DeleteServerRequest;
import com.zimbra.soap.admin.wsimport.generated.DeleteServerResponse;
import com.zimbra.soap.admin.wsimport.generated.DomainBy;
import com.zimbra.soap.admin.wsimport.generated.DomainInfo;
import com.zimbra.soap.admin.wsimport.generated.DomainSelector;
import com.zimbra.soap.admin.wsimport.generated.GetAllDomainsRequest;
import com.zimbra.soap.admin.wsimport.generated.GetAllDomainsResponse;
import com.zimbra.soap.admin.wsimport.generated.GetAllServersRequest;
import com.zimbra.soap.admin.wsimport.generated.GetAllServersResponse;
import com.zimbra.soap.admin.wsimport.generated.GetDomainInfoRequest;
import com.zimbra.soap.admin.wsimport.generated.GetDomainInfoResponse;
import com.zimbra.soap.admin.wsimport.generated.GetDomainRequest;
import com.zimbra.soap.admin.wsimport.generated.GetDomainResponse;
import com.zimbra.soap.admin.wsimport.generated.GetServerRequest;
import com.zimbra.soap.admin.wsimport.generated.GetServerResponse;
import com.zimbra.soap.admin.wsimport.generated.ModifyDomainRequest;
import com.zimbra.soap.admin.wsimport.generated.ModifyDomainResponse;
import com.zimbra.soap.admin.wsimport.generated.ModifyServerRequest;
import com.zimbra.soap.admin.wsimport.generated.ModifyServerResponse;
import com.zimbra.soap.admin.wsimport.generated.ReloadLocalConfigRequest;
import com.zimbra.soap.admin.wsimport.generated.ReloadLocalConfigResponse;
import com.zimbra.soap.admin.wsimport.generated.GetAccountRequest;
import com.zimbra.soap.admin.wsimport.generated.GetAccountResponse;
import com.zimbra.soap.admin.wsimport.generated.ServerBy;
import com.zimbra.soap.admin.wsimport.generated.ServerInfo;
import com.zimbra.soap.admin.wsimport.generated.ServerSelector;

import com.zimbra.soap.Utility;

import org.junit.Assert;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class WSDLAdminTest {

    // The AdminService interface is the Java type bound to
    // the portType section of the WSDL document.
    private final static String testDomain = "wsdl.domain.example.test";
    private final static String testServer = "wsdl.server.example.test";
    private static AdminService eif = null;

    @BeforeClass
    public static void init() throws Exception {
        Utility.setUpToAcceptAllHttpsServerCerts();
        eif = Utility.getAdminSvcEIF();
        oneTimeTearDown();
    }

    @AfterClass
    public static void oneTimeTearDown() {
        // one-time cleanup code
       try {
           // Delete the test domain if it hasn't already been deleted
           GetDomainRequest getReq = new GetDomainRequest();
           DomainSelector domainSel = new DomainSelector();
           domainSel.setBy(DomainBy.NAME);
           domainSel.setValue(testDomain);
           getReq.setDomain(domainSel);
           getReq.setApplyConfig(true);
           GetDomainResponse getResp = eif.getDomainRequest(getReq);
           if (getResp != null) {
               DomainInfo domainInfo = getResp.getDomain();
               DeleteDomainRequest delReq = new DeleteDomainRequest();
               delReq.setId(domainInfo.getId());
               eif.deleteDomainRequest(delReq);
           }
           // Delete the test server if it hasn't already been deleted
           GetServerRequest getSvrReq = new GetServerRequest();
           ServerSelector svrSel = new ServerSelector();
           svrSel.setBy(ServerBy.NAME);
           svrSel.setValue(testServer);
           getSvrReq.setServer(svrSel);
           getSvrReq.setApplyConfig(true);
           GetServerResponse getSvrResp = eif.getServerRequest(getSvrReq);
           if (getSvrResp != null) {
               ServerInfo serverInfo = getSvrResp.getServer();
               DeleteServerRequest delReq = new DeleteServerRequest();
               delReq.setId(serverInfo.getId());
               eif.deleteServerRequest(delReq);
           }
       } catch (SOAPFaultException sfe) { }
    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void reloadLocalConfigTest() throws Exception {
       Utility.addSoapAdminAuthHeader((WSBindingProvider)eif);
       ReloadLocalConfigRequest req = new ReloadLocalConfigRequest();
       ReloadLocalConfigResponse resp = eif.reloadLocalConfigRequest(req);
       Assert.assertNotNull(resp);
    }

    @Test
    public void simpleGetAccountTest() throws Exception {
       GetAccountRequest req = new GetAccountRequest();
       Account acct = new Account();
       acct.setBy(By.NAME);
       acct.setValue("user1");
       req.setAccount(acct);
       Utility.addSoapAdminAuthHeader((WSBindingProvider)eif);
       GetAccountResponse resp = eif.getAccountRequest(req);
       Assert.assertNotNull(resp);
       AccountInfo acctInfo = resp.getAccount();
       Assert.assertNotNull(acctInfo);
       // assertEquals(java.lang.String message, java.lang.Object expected, java.lang.Object actual) 
       Assert.assertTrue("value of <account> 'name' attribute should start with 'user1@'", acctInfo.getName().startsWith("user1@"));
       int len = acctInfo.getId().length();
       Assert.assertTrue("length of <account> 'id' attribute length is " + len + " - should be longer than 10", len > 10);
       len = acctInfo.getA().size();
       Assert.assertTrue("<account> has " + len + " <a> children - should have at least 12", len >= 12);
    }

    @Test
    public void foreignPrincGetAccountTest() throws Exception {
       GetAccountRequest req = new GetAccountRequest();
       Account acct = new Account();
       acct.setBy(By.NAME);
       acct.setValue("user1");
       req.setAccount(acct);
       req.setAttrs("zimbraForeignPrincipal");
       Utility.addSoapAdminAuthHeader((WSBindingProvider)eif);
       GetAccountResponse resp = eif.getAccountRequest(req);
       Assert.assertNotNull(resp);
       AccountInfo acctInfo = resp.getAccount();
       Assert.assertNotNull(acctInfo);
       // assertEquals(java.lang.String message, java.lang.Object expected, java.lang.Object actual) 
       Assert.assertTrue("value of <account> 'name' attribute should start with 'user1@'", acctInfo.getName().startsWith("user1@"));
       int len = acctInfo.getId().length();
       Assert.assertTrue("length of <account> 'id' attribute length is " + len + " - should be longer than 10", len > 10);
       List <Attr> attrs = acctInfo.getA();
       len = attrs.size();
       Assert.assertTrue("<account> has " + len + " <a> children - should have only 1", len == 1);
       Assert.assertEquals("'n' attribute of <a> -", "zimbraForeignPrincipal", attrs.get(0).getN());
    }

    @Test
    public void domainTest() throws Exception {
       CreateDomainRequest req = new CreateDomainRequest();
       req.setName(testDomain);
       Utility.addSoapAdminAuthHeader((WSBindingProvider)eif);
       CreateDomainResponse resp = eif.createDomainRequest(req);
       Assert.assertNotNull(resp);
       DomainInfo domInfo = resp.getDomain();
       Assert.assertNotNull(domInfo);
       Assert.assertEquals("createDomainResponse <domain> 'name' attribute", testDomain, domInfo.getName());
       String testDomainId = domInfo.getId();
       int len = testDomainId.length();
       Assert.assertTrue("length of CreateDomainResponse <domain> 'id' attribute length is " + len + " - should be longer than 10", len > 10);
       len = domInfo.getA().size();
       Assert.assertTrue("CreateDomainResponse <domain> has " + len + " <a> children - should have at least 12", len >= 12);

       GetDomainInfoRequest getInfoReq = new GetDomainInfoRequest();
       getInfoReq.setApplyConfig(true);
       DomainSelector domSel = new DomainSelector();
       domSel.setBy(DomainBy.ID);
       domSel.setValue(testDomainId);
       getInfoReq.setDomain(domSel);
       GetDomainInfoResponse getInfoResp = eif.getDomainInfoRequest(getInfoReq);
       Assert.assertNotNull(getInfoResp);
       domInfo = getInfoResp.getDomain();
       Assert.assertNotNull(domInfo);
       Assert.assertEquals("getDomainInfoResponse <domain> 'name' attribute", testDomain, domInfo.getName());
       String respId = domInfo.getId();
       Assert.assertEquals("getDomainInfoResponse <domain> 'id' attribute", testDomainId, respId);
       len = domInfo.getA().size();
       Assert.assertEquals("number of <a> children of GetDomainInfoResponse <domain>", 0, len);

       GetDomainRequest getReq = new GetDomainRequest();
       getReq.setApplyConfig(true);
       domSel = new DomainSelector();
       domSel.setBy(DomainBy.ID);
       domSel.setValue(testDomainId);
       getReq.setDomain(domSel);
       GetDomainResponse getResp = eif.getDomainRequest(getReq);
       Assert.assertNotNull(getResp);
       domInfo = getResp.getDomain();
       Assert.assertNotNull(domInfo);
       Assert.assertEquals("getDomainResponse <domain> 'name' attribute", testDomain, domInfo.getName());
       respId = domInfo.getId();
       Assert.assertEquals("getDomainResponse <domain> 'id' attribute", testDomainId, respId);
       len = domInfo.getA().size();
       Assert.assertTrue("GetDomainResponse <domain> has " + len + " <a> children - should have at least 12", len >= 12);

       ModifyDomainRequest modReq = new ModifyDomainRequest();
       modReq.setId(testDomainId);
       Attr modAttr = new Attr();
       modAttr.setN("zimbraGalMaxResults");
       modAttr.setValue("99");
       modReq.getA().add(modAttr);
       ModifyDomainResponse modResp = eif.modifyDomainRequest(modReq);
       Assert.assertNotNull(modResp);
       domInfo = modResp.getDomain();
       Assert.assertNotNull(domInfo);
       Assert.assertEquals("modifyDomainResponse <domain> 'name' attribute", testDomain, domInfo.getName());
       respId = domInfo.getId();
       Assert.assertEquals("modifyDomainResponse <domain> 'id' attribute", testDomainId, respId);
       len = domInfo.getA().size();
       Assert.assertTrue("modifyDomainResponse <domain> has " + len + " <a> children - should have at least 50", len >= 50);

       getReq.setAttrs("zimbraMailStatus,zimbraBasicAuthRealm");
       getResp = eif.getDomainRequest(getReq);
       Assert.assertNotNull(getResp);
       domInfo = getResp.getDomain();
       Assert.assertNotNull(domInfo);
       Assert.assertEquals("getDomainResponse <domain> 'name' attribute", testDomain, domInfo.getName());
       respId = domInfo.getId();
       Assert.assertEquals("getDomainResponse <domain> 'id' attribute", testDomainId, respId);
       len = domInfo.getA().size();
       Assert.assertEquals("Number of GetDomainResponse <domain> <a> children", 2, len);
       
       DeleteDomainRequest delReq = new DeleteDomainRequest();
       delReq.setId(testDomainId);
       DeleteDomainResponse delResp = eif.deleteDomainRequest(delReq);
       Assert.assertNotNull(delResp);
    }

    @Test
    public void getAllDomainsTest() throws Exception {
       GetAllDomainsRequest req = new GetAllDomainsRequest();
       Utility.addSoapAdminAuthHeader((WSBindingProvider)eif);
       GetAllDomainsResponse resp = eif.getAllDomainsRequest(req);
       Assert.assertNotNull("GetAllDomainsResponse object", resp);
       List <DomainInfo> domInfoList = resp.getDomain();
       int len;
       Assert.assertNotNull("GetAllDomainsResponse list of domains", domInfoList);
       len = domInfoList.size();
       Assert.assertTrue("Number of GetAllDomainsResponse <domain> children is " + len + " - should be at least 1", len >= 1);
    }

    @Test
    public void serverTest() throws Exception {
       int len;
       ServerSelector domSel;
       String respId;
       CreateServerRequest req = new CreateServerRequest();
       req.setName(testServer);
       Utility.addSoapAdminAuthHeader((WSBindingProvider)eif);
       CreateServerResponse resp = eif.createServerRequest(req);
       Assert.assertNotNull(resp);
       ServerInfo serverInfo = resp.getServer();
       Assert.assertNotNull(serverInfo);
       Assert.assertEquals("createServerResponse <server> 'name' attribute", testServer, serverInfo.getName());
       String testServerId = serverInfo.getId();
       len = testServerId.length();
       Assert.assertTrue("length of CreateServerResponse <server> 'id' attribute length is " + len + " - should be longer than 10", len > 10);
       len = serverInfo.getA().size();
       Assert.assertTrue("CreateServerResponse <server> has " + len + " <a> children - should have at least 12", len >= 12);

       GetServerRequest getReq = new GetServerRequest();
       getReq.setApplyConfig(true);
       domSel = new ServerSelector();
       domSel.setBy(ServerBy.ID);
       domSel.setValue(testServerId);
       getReq.setServer(domSel);
       GetServerResponse getResp = eif.getServerRequest(getReq);
       Assert.assertNotNull(getResp);
       serverInfo = getResp.getServer();
       Assert.assertNotNull(serverInfo);
       Assert.assertEquals("getServerResponse <server> 'name' attribute", testServer, serverInfo.getName());
       respId = serverInfo.getId();
       Assert.assertEquals("getServerResponse <server> 'id' attribute", testServerId, respId);
       len = serverInfo.getA().size();
       Assert.assertTrue("GetServerResponse <server> has " + len + " <a> children - should have at least 12", len >= 12);

       ModifyServerRequest modReq = new ModifyServerRequest();
       modReq.setId(testServerId);
       Attr modAttr = new Attr();
       modAttr.setN("zimbraImapNumThreads");
       modAttr.setValue("199");
       modReq.getA().add(modAttr);
       ModifyServerResponse modResp = eif.modifyServerRequest(modReq);
       Assert.assertNotNull(modResp);
       serverInfo = modResp.getServer();
       Assert.assertNotNull(serverInfo);
       Assert.assertEquals("modifyServerResponse <server> 'name' attribute", testServer, serverInfo.getName());
       respId = serverInfo.getId();
       Assert.assertEquals("modifyServerResponse <server> 'id' attribute", testServerId, respId);
       len = serverInfo.getA().size();
       Assert.assertTrue("modifyServerResponse <server> has " + len + " <a> children - should have at least 50", len >= 50);

       getReq.setAttrs("zimbraImapNumThreads,zimbraServiceHostname");
       getResp = eif.getServerRequest(getReq);
       Assert.assertNotNull(getResp);
       serverInfo = getResp.getServer();
       Assert.assertNotNull(serverInfo);
       Assert.assertEquals("getServerResponse <server> 'name' attribute", testServer, serverInfo.getName());
       respId = serverInfo.getId();
       Assert.assertEquals("getServerResponse <server> 'id' attribute", testServerId, respId);
       len = serverInfo.getA().size();
       Assert.assertEquals("Number of GetServerResponse <server> <a> children", 2, len);
       
       DeleteServerRequest delReq = new DeleteServerRequest();
       delReq.setId(testServerId);
       DeleteServerResponse delResp = eif.deleteServerRequest(delReq);
       Assert.assertNotNull(delResp);
    }

    @Test
    public void getAllServersTest() throws Exception {
       GetAllServersRequest req = new GetAllServersRequest();
       Utility.addSoapAdminAuthHeader((WSBindingProvider)eif);
       GetAllServersResponse resp = eif.getAllServersRequest(req);
       Assert.assertNotNull("GetAllServersResponse object", resp);
       List <ServerInfo> serverInfoList = resp.getServer();
       int len;
       Assert.assertNotNull("GetAllServersResponse list of server", serverInfoList);
       len = serverInfoList.size();
       Assert.assertTrue("Number of GetAllServersResponse <server> children is " + len + " - should be at least 1", len >= 1);
    }
}
