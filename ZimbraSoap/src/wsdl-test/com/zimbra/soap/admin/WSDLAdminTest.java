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

import com.zimbra.soap.admin.wsimport.generated.*;

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
    private final static String testAcctDomain = "wsdl.acct.domain.example.test";
    private final static String testAcct = "wsdl1@" + testAcctDomain;
    private final static String testServer = "wsdl.server.example.test";
    private final static String testCos = "wsdl.cos.example.test";
    private final static String testCosCopy = "wsdl.cos.copy.example.test";
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
            Utility.deleteDomainIfExists(testDomain);
            Utility.deleteAccountIfExists(testAcct);
            Utility.deleteAccountIfExists("foobar" + testAcct);
            Utility.deleteDomainIfExists(testAcctDomain);
            Utility.deleteServerIfExists(testServer);
            Utility.deleteCosIfExists(testCos);
            Utility.deleteCosIfExists(testCosCopy);
            Utility.deleteCosIfExists("foobar" + testCos);
        } catch (Exception ex) {
            System.err.println("Exception " + ex.toString() + 
            " thrown inside oneTimeTearDown");
        }
    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void pingTest() throws Exception {
        Utility.addSoapAdminAuthHeader((WSBindingProvider)eif);
        PingRequest req = new PingRequest();
        PingResponse resp = eif.pingRequest(req);
        Assert.assertNotNull("PingResponse object", resp);
    }

    @Test
    public void noopTest() throws Exception {
        Utility.addSoapAdminAuthHeader((WSBindingProvider)eif);
        NoOpRequest req = new NoOpRequest();
        NoOpResponse resp = eif.noOpRequest(req);
        Assert.assertNotNull("NoOpResponse object", resp);
    }

    @Test
    public void getServiceStatuTest() throws Exception {
        Utility.addSoapAdminAuthHeader((WSBindingProvider)eif);
        GetServiceStatusRequest req = new GetServiceStatusRequest();
        GetServiceStatusResponse resp = eif.getServiceStatusRequest(req);
        Assert.assertNotNull("GetServiceStatusResponse object", resp);
        TimeZoneInfo tz = resp.getTimezone();
        Assert.assertNotNull("GetServiceStatusResponse <timezone> object", tz);
        Assert.assertNotNull("GetServiceStatusResponse <timezone> displayName", tz.getDisplayName());
        Assert.assertNotNull("GetServiceStatusResponse <timezone> id", tz.getId());
        List <ServiceStatus> statuses = resp.getStatus();
        // TODO: Would be nice to test with some real statuses - looks like need logger
        //       service installed and enabled
        Assert.assertNotNull("GetServiceStatusResponse statuses list object", statuses);
    }

    @Test
    public void checkHealthTest() throws Exception {
        Utility.addSoapAdminAuthHeader((WSBindingProvider)eif);
        CheckHealthRequest req = new CheckHealthRequest();
        CheckHealthResponse resp = eif.checkHealthRequest(req);
        Assert.assertNotNull("CheckHealthResponse object", resp);
        Assert.assertTrue("isHealthy",resp.isHealthy());
    }

    @Test
    public void reloadLocalConfigTest() throws Exception {
        Utility.addSoapAdminAuthHeader((WSBindingProvider)eif);
        ReloadLocalConfigRequest req = new ReloadLocalConfigRequest();
        ReloadLocalConfigResponse resp = eif.reloadLocalConfigRequest(req);
        Assert.assertNotNull("ReloadLocalConfigResponse object", resp);
    }

    @Test
    public void getAllConfigTest() throws Exception {
        GetAllConfigRequest req = new GetAllConfigRequest();
        Utility.addSoapAdminAuthHeader((WSBindingProvider)eif);
        GetAllConfigResponse resp = eif.getAllConfigRequest(req);
        Assert.assertNotNull("GetAllConfigResponse object", resp);
        List <Attr> attrs = resp.getA();
        Assert.assertNotNull("GetAllConfigResponse list of attrs", attrs);
        int len = attrs.size();
        Assert.assertTrue("Number of GetAllConfigResponse <a> children is " +
                len + " - should be at least 2", len >= 2);
    }

    @Test
    public void getConfigTest() throws Exception {
        Utility.addSoapAdminAuthHeader((WSBindingProvider)eif);
        GetConfigRequest req = new GetConfigRequest();
        Attr attr = new Attr();
        attr.setN("zimbraSpamHeader");
        req.setA(attr);
        GetConfigResponse resp = eif.getConfigRequest(req);
        Assert.assertNotNull("GetConfigResponse object", resp);
        List <Attr> attrs = resp.getA();
        Assert.assertNotNull("GetConfigResponse list of attrs", attrs);
        int len = attrs.size();
        Assert.assertEquals("Number of GetConfigResponse <a> children" , 1, len);
        Attr respAttr =attrs.get(0);
        Assert.assertEquals("GetConfigResponse <a> 'n' attribute",
                "zimbraSpamHeader", respAttr.getN());
        Assert.assertEquals("GetConfigResponse <a> 'n' attribute",
                "zimbraSpamHeader", respAttr.getN());
        Assert.assertEquals("GetConfigResponse <a> 'n' attribute",
                "X-Spam-Flag", respAttr.getValue());
    }

    @Test
    public void modifyConfigTest() throws Exception {
        Utility.addSoapAdminAuthHeader((WSBindingProvider)eif);
        ModifyConfigRequest req = new ModifyConfigRequest();
        Attr attr = new Attr();
        attr.setN("zimbraSpamHeader");
        attr.setValue("X-NewSpam-Flag");
        req.getA().add(attr);
        ModifyConfigResponse resp = eif.modifyConfigRequest(req);
        Assert.assertNotNull("modifyConfigResponse object", resp);
        req = new ModifyConfigRequest();
        attr.setValue("X-Spam-Flag");
        req.getA().add(attr);
        resp = eif.modifyConfigRequest(req);
        Assert.assertNotNull("modifyConfigResponse object", resp);
    }

    @Test
    public void getAllLocalesTest() throws Exception {
        Utility.addSoapAdminAuthHeader((WSBindingProvider)eif);
        GetAllLocalesRequest req = new GetAllLocalesRequest();
        GetAllLocalesResponse resp = eif.getAllLocalesRequest(req);
        Assert.assertNotNull("GetAllLocalesResponse object", resp);
        List <LocaleInfo> locales = resp.getLocale();
        int len = locales.size();
        Assert.assertTrue("number of <locales> is " + len +
                " - should be longer than 10", len > 10);
    }

    @Test
    public void getMailboxStatsTest() throws Exception {
        Utility.addSoapAdminAuthHeader((WSBindingProvider)eif);
        GetMailboxStatsRequest req = new GetMailboxStatsRequest();
        GetMailboxStatsResponse resp = eif.getMailboxStatsRequest(req);
        Assert.assertNotNull("GetMailboxStatsResponse object", resp);
        MailboxStats mboxStats = resp.getStats();
        Assert.assertNotNull("stats object", mboxStats);
        long numMboxes = mboxStats.getNumMboxes();
        long totalSize = mboxStats.getTotalSize();
        Assert.assertTrue("numMboxes " + numMboxes + " should be >=1", numMboxes >=1);
        Assert.assertTrue("totalSize " + totalSize + " should be >=1000", totalSize >=1000);
    }

    @Test
    public void flushCacheTest() throws Exception {
        Utility.addSoapAdminAuthHeader((WSBindingProvider)eif);
        FlushCacheRequest req = new FlushCacheRequest();
        CacheEntrySelector sel = new CacheEntrySelector();
        sel.setAllServers(true);
        sel.setType(CacheEntryType.DOMAIN.value());
        req.setCache(sel);
        FlushCacheResponse resp = eif.flushCacheRequest(req);
        Assert.assertNotNull("FlushCacheResponse object", resp);
    }

    @Test
    public void checkPasswordStrengthTest() throws Exception {
        String testAccountId = Utility.ensureAccountExists(testAcct);
        Utility.addSoapAdminAuthHeader((WSBindingProvider)eif);
        CheckPasswordStrengthRequest req = new CheckPasswordStrengthRequest();
        req.setId(testAccountId);
        req.setPassword("fq$34apgGog11");
        CheckPasswordStrengthResponse resp = eif.checkPasswordStrengthRequest(req);
        Assert.assertNotNull("CheckPasswordStrengthResponse object", resp);
        try {
            req.setPassword("a");
            resp = eif.checkPasswordStrengthRequest(req);
            Assert.fail("Should have had a fault resulting in an exception being thrown");
        } catch (SOAPFaultException sfe) {
            Assert.assertTrue("Soap fault message [" +
                    sfe.getMessage() + "] should start with 'invalid password: too short'",
                    sfe.getMessage().startsWith("invalid password: too short"));
        }
    }

    @Test
    public void setPasswordTest() throws Exception {
        String testAccountId = Utility.ensureAccountExists(testAcct);
        Utility.addSoapAdminAuthHeader((WSBindingProvider)eif);
        SetPasswordRequest req = new SetPasswordRequest();
        req.setId(testAccountId);
        req.setNewPassword("fq$34apgGog11");
        SetPasswordResponse resp = eif.setPasswordRequest(req);
        Assert.assertNotNull("SetPasswordResponse object", resp);
    }

    @Test
    public void accountAliasTest() throws Exception {
        String testAccountId = Utility.ensureAccountExists(testAcct);
        Utility.addSoapAdminAuthHeader((WSBindingProvider)eif);
        AddAccountAliasRequest req = new AddAccountAliasRequest();
        req.setId(testAccountId);
        req.setAlias("alias1@" + testAcctDomain);
        AddAccountAliasResponse resp = eif.addAccountAliasRequest(req);
        Assert.assertNotNull("AddAccountAliasResponse object", resp);
        RemoveAccountAliasRequest removeReq = new RemoveAccountAliasRequest();
        removeReq.setId(testAccountId);
        removeReq.setAlias("alias1@" + testAcctDomain);
        RemoveAccountAliasResponse removeResp = eif.removeAccountAliasRequest(removeReq);
        Assert.assertNotNull("RemoveAccountAliasResponse object", removeResp);
    }

    @Test
    public void searchDirectoryTest() throws Exception {
        Utility.ensureAccountExists(testAcct);
        Utility.addSoapAdminAuthHeader((WSBindingProvider)eif);
        SearchDirectoryRequest req = new SearchDirectoryRequest();
        req.setDomain(testAcctDomain);
        req.setApplyCos(true);
        req.setApplyConfig(true);
        req.setLimit(456);
        req.setMaxResults(444);
        req.setQuery("cn=*");
        SearchDirectoryResponse resp = eif.searchDirectoryRequest(req);
        Assert.assertNotNull("SearchDirectoryResponse object", resp);
        Long searchTotal = resp.getSearchTotal();
        Assert.assertTrue("searchTotal " + searchTotal + " should be at least 1",
                searchTotal >=1);
        Assert.assertFalse("value for attribute 'more'", resp.isMore());
        List <AdminObjectInfo> entries = resp.getCalresourceOrDlOrAlias();
        Assert.assertTrue("number of entries in response [" + searchTotal + "] should be at least 1",
                entries.size() >= 1);
    }

    @Test
    public void simpleGetAccountTest() throws Exception {
        GetAccountRequest req = new GetAccountRequest();
        AccountSelector acct = new AccountSelector();
        acct.setBy(AccountBy.NAME);
        acct.setValue("user1");
        req.setAccount(acct);
        Utility.addSoapAdminAuthHeader((WSBindingProvider)eif);
        GetAccountResponse resp = eif.getAccountRequest(req);
        Assert.assertNotNull("GetAccountResponse object", resp);
        AccountInfo acctInfo = resp.getAccount();
        Assert.assertNotNull("AccountInfo object", acctInfo);
        Assert.assertTrue("value of <account> 'name' attribute should start with 'user1@'",
                acctInfo.getName().startsWith("user1@"));
        int len = acctInfo.getId().length();
        Assert.assertTrue("length of <account> 'id' attribute length is " + len +
                " - should be longer than 10", len > 10);
        len = acctInfo.getA().size();
        Assert.assertTrue("<account> has " + len +
                " <a> children - should have at least 12", len >= 12);
    }

    @Test
    public void foreignPrincGetAccountTest() throws Exception {
        GetAccountRequest req = new GetAccountRequest();
        AccountSelector acct = new AccountSelector();
        acct.setBy(AccountBy.NAME);
        acct.setValue("user1");
        req.setAccount(acct);
        req.setAttrs("zimbraForeignPrincipal");
        Utility.addSoapAdminAuthHeader((WSBindingProvider)eif);
        GetAccountResponse resp = eif.getAccountRequest(req);
        Assert.assertNotNull("GetAccountResponse object", resp);
        AccountInfo acctInfo = resp.getAccount();
        Assert.assertNotNull("AccountInfo object", acctInfo);
        Assert.assertTrue("value of <account> 'name' attribute should start with 'user1@'",
                acctInfo.getName().startsWith("user1@"));
        int len = acctInfo.getId().length();
        Assert.assertTrue("length of <account> 'id' attribute length is " + len +
                " - should be longer than 10", len > 10);
        List <Attr> attrs = acctInfo.getA();
        len = attrs.size();
        Assert.assertTrue("<account> has " + len +
                " <a> children - should have only 1", len == 1);
        Assert.assertEquals("'n' attribute of <a> -", "zimbraForeignPrincipal",
                attrs.get(0).getN());
    }

    @Test
    public void createAccountTest() throws Exception {
        int len;
        Utility.deleteAccountIfExists(testAcct);
        Utility.ensureDomainExists(testAcctDomain);
        CreateAccountRequest createAcctReq = new CreateAccountRequest();
        createAcctReq.setName(testAcct);
        createAcctReq.setPassword("test123");
        Utility.addSoapAdminAuthHeader((WSBindingProvider)eif);
        CreateAccountResponse resp = eif.createAccountRequest(createAcctReq);
        Assert.assertNotNull("CreateAccountResponse object", resp);
        AccountInfo accountInfo = resp.getAccount();
        Assert.assertNotNull("AccountInfo object", accountInfo);
        Assert.assertEquals("createAccountResponse <account> 'name' attribute",
                testAcct, accountInfo.getName());
        String testAccountId = accountInfo.getId();
        len = testAccountId.length();
        Assert.assertTrue("length of CreateAccountResponse <account> 'id' attribute length is " +
                len + " - should be longer than 10", len > 10);
        len = accountInfo.getA().size();
        Assert.assertTrue("CreateAccountResponse <account> has " +
                len + " <a> children - should have at least 12", len >= 12);
    }

    @Test
    public void getAccountInfoTest() throws Exception {
        int len;
        String testAccountId = Utility.ensureAccountExists(testAcct);
        GetAccountInfoRequest getInfoReq = new GetAccountInfoRequest();
        AccountSelector accountSel = new AccountSelector();
        accountSel.setBy(AccountBy.ID);
        accountSel.setValue(testAccountId);
        getInfoReq.setAccount(accountSel);
        GetAccountInfoResponse getInfoResp = eif.getAccountInfoRequest(getInfoReq);
        Assert.assertNotNull("GetAccountInfoResponse object", getInfoResp);
        CosInfo cos = getInfoResp.getCos();
        String acctName = getInfoResp.getName();
        List <String> soapURL = getInfoResp.getSoapURL();
        String adminSoapURL = getInfoResp.getAdminSoapURL();
        String publicMailURL = getInfoResp.getPublicMailURL();
        Assert.assertEquals("<name> child of GetAccountInfoResponse", testAcct, acctName);
        List < Attr> attrs = getInfoResp.getA();
        len = attrs.size();
        Assert.assertTrue("number of <a> children of GetAccountInfoResponse should be at least 2",
                len >= 2);
        Assert.assertEquals("<cos> child of GetAccountInfoResponse 'name' attribute",
                "default", cos.getName());
        len = soapURL.size();
        Assert.assertEquals("number of <soapURL> children of GetAccountInfoResponse", 1, len);
        Assert.assertTrue(
                "value of <soapURL> child of GetAccountInfoResponse should start with 'http://'",
                soapURL.get(0).startsWith("http://"));
        Assert.assertTrue(
                "value of <adminSoapURL> child of GetAccountInfoResponse should start with 'https://'",
                adminSoapURL.startsWith("https://"));
        Assert.assertTrue(
                "value of <publicMailURL> child of GetAccountInfoResponse should start with 'http://'",
                publicMailURL.startsWith("http://"));
    }

    @Test
    public void getAccountTest() throws Exception {
        int len;
        String testAccountId = Utility.ensureAccountExists(testAcct);
        GetAccountRequest getReq = new GetAccountRequest();
        AccountSelector accountSel = new AccountSelector();
        accountSel.setBy(AccountBy.ID);
        accountSel.setValue(testAccountId);
        getReq.setAccount(accountSel);
        GetAccountResponse getResp = eif.getAccountRequest(getReq);
        Assert.assertNotNull("GetAccountResponse object", getResp);
        AccountInfo accountInfo = getResp.getAccount();
        Assert.assertNotNull("AccountInfo object", accountInfo);
        Assert.assertEquals("getAccountResponse <account> 'name' attribute",
                testAcct, accountInfo.getName());
        String respId = accountInfo.getId();
        Assert.assertEquals("getAccountResponse <account> 'id' attribute", testAccountId, respId);
        len = accountInfo.getA().size();
        Assert.assertTrue("GetAccountResponse <account> has " + len +
                " <a> children - should have at least 12", len >= 12);
    }

    @Test
    public void modifyAccountTest() throws Exception {
        int len;
        String testAccountId = Utility.ensureAccountExists(testAcct);
        String respId;
        ModifyAccountRequest modReq = new ModifyAccountRequest();
        modReq.setId(testAccountId);
        Attr modAttr = new Attr();
        modAttr.setN("displayName");
        modAttr.setValue("Modified Displayname");
        modReq.getA().add(modAttr);
        ModifyAccountResponse modResp = eif.modifyAccountRequest(modReq);
        Assert.assertNotNull("ModifyAccountResponse object", modResp);
        AccountInfo accountInfo = modResp.getAccount();
        Assert.assertNotNull("AccountInfo object", accountInfo);
        Assert.assertEquals("modifyAccountResponse <account> 'name' attribute", 
                testAcct, accountInfo.getName());
        respId = accountInfo.getId();
        Assert.assertEquals("modifyAccountResponse <account> 'id' attribute",
                testAccountId, respId);
        len = accountInfo.getA().size();
        Assert.assertTrue("modifyAccountResponse <account> has " + len +
                " <a> children - should have at least 50", len >= 50);
    }

    @Test
    public void renameAccountTest() throws Exception {
        int len;
        String testAccountId = Utility.ensureAccountExists(testAcct);
        String respId;
        RenameAccountRequest renameAccountReq = new RenameAccountRequest();
        renameAccountReq.setId(testAccountId);
        renameAccountReq.setNewName("foobar" + testAcct);
        RenameAccountResponse renameAccountResp = eif.renameAccountRequest(renameAccountReq);
        Assert.assertNotNull(renameAccountResp);
        AccountInfo accountInfo = renameAccountResp.getAccount();
        Assert.assertNotNull(accountInfo);
        Assert.assertEquals("renameAccountResponse <account> 'name' attribute",
                "foobar" + testAcct, accountInfo.getName());
        respId = accountInfo.getId();
        Assert.assertEquals("renameAccountResponse <account> 'id' attribute",
                testAccountId, respId);
        len = accountInfo.getA().size();
        Assert.assertTrue("renameAccountResponse <account> has " + len +
                " <a> children - should have at least 50", len >= 50);
        Utility.deleteAccountIfExists("foobar" + testAcct);
    }

    @Test
    public void getAccountMembershipTest() throws Exception {
        int len;
        String testAccountId = Utility.ensureAccountExists(testAcct);
        GetAccountMembershipRequest membershipReq = new GetAccountMembershipRequest();
        AccountSelector adminAcct = new AccountSelector();
        adminAcct.setBy(AccountBy.NAME);
        adminAcct.setValue("admin");
        membershipReq.setAccount(adminAcct);
        GetAccountMembershipResponse accountMembershipResponse =
            eif.getAccountMembershipRequest(membershipReq);
        Assert.assertNotNull("GetAccountMembershipResponse object", accountMembershipResponse);
        // TODO: test an account where the response actually has children
        len = accountMembershipResponse.getDl().size();
        Assert.assertEquals("GetAccountMembershipResponse object has " + len +
                " <dl> children - expecting 0", 0, len);

        // check that name did get changed.
        GetAccountRequest getReq = new GetAccountRequest();
        AccountSelector accountSel = new AccountSelector();
        accountSel.setBy(AccountBy.ID);
        accountSel.setValue(testAccountId);
        getReq.setAccount(accountSel);
        getReq.setAttrs("zimbraMailStatus,zimbraMailHost");
        GetAccountResponse getResp = eif.getAccountRequest(getReq);
        Assert.assertNotNull(getResp);
        AccountInfo accountInfo = getResp.getAccount();
        Assert.assertNotNull(accountInfo);
        Assert.assertEquals("getAccountResponse <account> 'name' attribute",
                testAcct, accountInfo.getName());
        String respId = accountInfo.getId();
        Assert.assertEquals("getAccountResponse <account> 'id' attribute",
                testAccountId, respId);
        len = accountInfo.getA().size();
        Assert.assertEquals("Number of GetAccountResponse <account> <a> children", 2, len);
    }

    @Test
    public void deleteAccountTest() throws Exception {
        String testAccountId = Utility.ensureAccountExists(testAcct);
        DeleteAccountRequest delReq = new DeleteAccountRequest();
        delReq.setId(testAccountId);
        DeleteAccountResponse delResp = eif.deleteAccountRequest(delReq);
        Assert.assertNotNull(delResp);
    }

    @Test
    public void getAllAccountsTest() throws Exception {
        GetAllAccountsRequest req = new GetAllAccountsRequest();
        Utility.addSoapAdminAuthHeader((WSBindingProvider)eif);
        GetAllAccountsResponse resp = eif.getAllAccountsRequest(req);
        Assert.assertNotNull("GetAllAccountsResponse object", resp);
        List <AccountInfo> accountInfoList = resp.getAccount();
        int len;
        Assert.assertNotNull("GetAllAccountsResponse list of Accounts", accountInfoList);
        len = accountInfoList.size();
        Assert.assertTrue("Number of GetAllAccountsResponse <account> children is " +
                len + " - should be at least 2", len >= 2);
    }

    @Test
    public void getAllAdminAccountsTest() throws Exception {
        GetAllAdminAccountsRequest req = new GetAllAdminAccountsRequest();
        Utility.addSoapAdminAuthHeader((WSBindingProvider)eif);
        GetAllAdminAccountsResponse resp = eif.getAllAdminAccountsRequest(req);
        Assert.assertNotNull("GetAllAdminAccountsResponse object", resp);
        List <AccountInfo> accountInfoList = resp.getAccount();
        int len;
        Assert.assertNotNull("GetAllAdminAccountsResponse list of Accounts", accountInfoList);
        len = accountInfoList.size();
        Assert.assertTrue("Number of GetAllAdminAccountsResponse <account> children is " + len
                + " - should be at least 1", len >= 1);
    }

    @Test
    public void createDomainTest() throws Exception {
        Utility.deleteDomainIfExists(testDomain);
        CreateDomainRequest req = new CreateDomainRequest();
        req.setName(testDomain);
        Utility.addSoapAdminAuthHeader((WSBindingProvider)eif);
        CreateDomainResponse resp = eif.createDomainRequest(req);
        Assert.assertNotNull(resp);
        DomainInfo domainInfo = resp.getDomain();
        Assert.assertNotNull(domainInfo);
        Assert.assertEquals("createDomainResponse <domain> 'name' attribute",
                testDomain, domainInfo.getName());
        String testDomainId = domainInfo.getId();
        int len = testDomainId.length();
        Assert.assertTrue(
                "length of CreateDomainResponse <domain> 'id' attribute length is " +
                len + " - should be longer than 10", len > 10);
        len = domainInfo.getA().size();
        Assert.assertTrue("CreateDomainResponse <domain> has " + 
                len + " <a> children - should have at least 12", len >= 12);
    }

    @Test
    public void getDomainInfoTest() throws Exception {
        int len;
        DomainInfo domainInfo;
        String testDomainId = Utility.ensureDomainExists(testDomain);
        GetDomainInfoRequest getInfoReq = new GetDomainInfoRequest();
        getInfoReq.setApplyConfig(true);
        DomainSelector domainSel = new DomainSelector();
        domainSel.setBy(DomainBy.ID);
        domainSel.setValue(testDomainId);
        getInfoReq.setDomain(domainSel);
        GetDomainInfoResponse getInfoResp = eif.getDomainInfoRequest(getInfoReq);
        Assert.assertNotNull(getInfoResp);
        domainInfo = getInfoResp.getDomain();
        Assert.assertNotNull(domainInfo);
        Assert.assertEquals("getDomainInfoResponse <domain> 'name' attribute",
                testDomain, domainInfo.getName());
        String respId = domainInfo.getId();
        Assert.assertEquals("getDomainInfoResponse <domain> 'id' attribute",
                testDomainId, respId);
        len = domainInfo.getA().size();
        Assert.assertEquals(
                "number of <a> children of GetDomainInfoResponse <domain>", 0, len);
    }

    @Test
    public void getDomainTest() throws Exception {
        int len;
        DomainInfo domainInfo;
        String testDomainId = Utility.ensureDomainExists(testDomain);
        GetDomainRequest getReq = new GetDomainRequest();
        getReq.setApplyConfig(true);
        DomainSelector domainSel = new DomainSelector();
        domainSel.setBy(DomainBy.ID);
        domainSel.setValue(testDomainId);
        getReq.setDomain(domainSel);
        GetDomainResponse getResp = eif.getDomainRequest(getReq);
        Assert.assertNotNull(getResp);
        domainInfo = getResp.getDomain();
        Assert.assertNotNull(domainInfo);
        Assert.assertEquals("getDomainResponse <domain> 'name' attribute",
                testDomain, domainInfo.getName());
        String respId = domainInfo.getId();
        Assert.assertEquals("getDomainResponse <domain> 'id' attribute",
                testDomainId, respId);
        len = domainInfo.getA().size();
        Assert.assertTrue("GetDomainResponse <domain> has " + len +
                " <a> children - should have at least 12", len >= 12);
    }

    @Test
    public void modifyDomainTest() throws Exception {
        int len;
        DomainInfo domainInfo;
        String testDomainId = Utility.ensureDomainExists(testDomain);
        ModifyDomainRequest modReq = new ModifyDomainRequest();
        modReq.setId(testDomainId);
        Attr modAttr = new Attr();
        modAttr.setN("zimbraGalMaxResults");
        modAttr.setValue("99");
        modReq.getA().add(modAttr);
        ModifyDomainResponse modResp = eif.modifyDomainRequest(modReq);
        Assert.assertNotNull(modResp);
        domainInfo = modResp.getDomain();
        Assert.assertNotNull(domainInfo);
        Assert.assertEquals("modifyDomainResponse <domain> 'name' attribute",
                testDomain, domainInfo.getName());
        String respId = domainInfo.getId();
        Assert.assertEquals("modifyDomainResponse <domain> 'id' attribute",
                testDomainId, respId);
        len = domainInfo.getA().size();
        Assert.assertTrue("modifyDomainResponse <domain> has " + len +
                " <a> children - should have at least 50", len >= 50);

        GetDomainRequest getReq = new GetDomainRequest();
        getReq.setApplyConfig(true);
        DomainSelector domainSel = new DomainSelector();
        domainSel.setBy(DomainBy.ID);
        domainSel.setValue(testDomainId);
        getReq.setDomain(domainSel);
        getReq.setAttrs("zimbraMailStatus,zimbraBasicAuthRealm");
        GetDomainResponse getResp = eif.getDomainRequest(getReq);
        Assert.assertNotNull(getResp);
        domainInfo = getResp.getDomain();
        Assert.assertNotNull(domainInfo);
        Assert.assertEquals("getDomainResponse <domain> 'name' attribute",
                testDomain, domainInfo.getName());
        respId = domainInfo.getId();
        Assert.assertEquals("getDomainResponse <domain> 'id' attribute", testDomainId, respId);
        len = domainInfo.getA().size();
        Assert.assertEquals("Number of GetDomainResponse <domain> <a> children", 2, len);
    }

    @Test
    public void getAllDomainsTest() throws Exception {
        GetAllDomainsRequest req = new GetAllDomainsRequest();
        Utility.addSoapAdminAuthHeader((WSBindingProvider)eif);
        GetAllDomainsResponse resp = eif.getAllDomainsRequest(req);
        Assert.assertNotNull("GetAllDomainsResponse object", resp);
        List <DomainInfo> domainInfoList = resp.getDomain();
        int len;
        Assert.assertNotNull("GetAllDomainsResponse list of domains", domainInfoList);
        len = domainInfoList.size();
        Assert.assertTrue("Number of GetAllDomainsResponse <domain> children is " + len +
                " - should be at least 1", len >= 1);
    }

    @Test
    public void deleteDomainTest() throws Exception {
        String testDomainId = Utility.ensureDomainExists(testDomain);
        DeleteDomainRequest delReq = new DeleteDomainRequest();
        delReq.setId(testDomainId);
        DeleteDomainResponse delResp = eif.deleteDomainRequest(delReq);
        Assert.assertNotNull(delResp);
    }

    @Test
    public void createServerTest() throws Exception {
        int len;
        Utility.deleteServerIfExists(testServer);
        CreateServerRequest req = new CreateServerRequest();
        req.setName(testServer);
        Utility.addSoapAdminAuthHeader((WSBindingProvider)eif);
        CreateServerResponse resp = eif.createServerRequest(req);
        Assert.assertNotNull(resp);
        ServerInfo serverInfo = resp.getServer();
        Assert.assertNotNull(serverInfo);
        Assert.assertEquals("createServerResponse <server> 'name' attribute",
                testServer, serverInfo.getName());
        String testServerId = serverInfo.getId();
        len = testServerId.length();
        Assert.assertTrue("length of CreateServerResponse <server> 'id' attribute length is " +
                len + " - should be longer than 10", len > 10);
        len = serverInfo.getA().size();
        Assert.assertTrue("CreateServerResponse <server> has " + len +
                " <a> children - should have at least 12", len >= 12);
    }

    @Test
    public void getServerTest() throws Exception {
        int len;
        ServerSelector serverSel;
        ServerInfo serverInfo;
        String respId;
        String testServerId = Utility.ensureServerExists(testServer);
        GetServerRequest getReq = new GetServerRequest();
        getReq.setApplyConfig(true);
        serverSel = new ServerSelector();
        serverSel.setBy(ServerBy.ID);
        serverSel.setValue(testServerId);
        getReq.setServer(serverSel);
        GetServerResponse getResp = eif.getServerRequest(getReq);
        Assert.assertNotNull(getResp);
        serverInfo = getResp.getServer();
        Assert.assertNotNull(serverInfo);
        Assert.assertEquals("getServerResponse <server> 'name' attribute",
                testServer, serverInfo.getName());
        respId = serverInfo.getId();
        Assert.assertEquals("getServerResponse <server> 'id' attribute", testServerId, respId);
        len = serverInfo.getA().size();
        Assert.assertTrue("GetServerResponse <server> has " + len +
                " <a> children - should have at least 12", len >= 12);
    }

    @Test
    public void modifyServerTest() throws Exception {
        int len;
        ServerSelector serverSel;
        ServerInfo serverInfo;
        String respId;
        String testServerId = Utility.ensureServerExists(testServer);
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
        Assert.assertEquals("modifyServerResponse <server> 'name' attribute",
                testServer, serverInfo.getName());
        respId = serverInfo.getId();
        Assert.assertEquals("modifyServerResponse <server> 'id' attribute", testServerId, respId);
        len = serverInfo.getA().size();
        Assert.assertTrue("modifyServerResponse <server> has " + len +
                " <a> children - should have at least 50", len >= 50);

        GetServerRequest getReq = new GetServerRequest();
        getReq.setApplyConfig(true);
        serverSel = new ServerSelector();
        serverSel.setBy(ServerBy.ID);
        serverSel.setValue(testServerId);
        getReq.setServer(serverSel);
        getReq.setAttrs("zimbraImapNumThreads,zimbraServiceHostname");
        GetServerResponse getResp = eif.getServerRequest(getReq);
        Assert.assertNotNull(getResp);
        serverInfo = getResp.getServer();
        Assert.assertNotNull(serverInfo);
        Assert.assertEquals("getServerResponse <server> 'name' attribute",
                testServer, serverInfo.getName());
        respId = serverInfo.getId();
        Assert.assertEquals("getServerResponse <server> 'id' attribute", testServerId, respId);
        len = serverInfo.getA().size();
        Assert.assertEquals("Number of GetServerResponse <server> <a> children", 2, len);
    }

    @Test
    public void deleteServerTest() throws Exception {
        String testServerId = Utility.ensureServerExists(testServer);
        Assert.assertNotNull(testServerId);
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
        Assert.assertTrue("Number of GetAllServersResponse <server> children is " + len +
                " - should be at least 1", len >= 1);
    }

    @Test
    public void createCosTest() throws Exception {
        int len;
        Utility.deleteCosIfExists(testCos);
        CreateCosRequest req = new CreateCosRequest();
        req.setName(testCos);
        Utility.addSoapAdminAuthHeader((WSBindingProvider)eif);
        CreateCosResponse resp = eif.createCosRequest(req);
        Assert.assertNotNull(resp);
        CosInfo cosInfo = resp.getCos();
        Assert.assertNotNull(cosInfo);
        Assert.assertEquals("createCosResponse <cos> 'name' attribute",
                testCos, cosInfo.getName());
        String testCosId = cosInfo.getId();
        len = testCosId.length();
        Assert.assertTrue("length of CreateCosResponse <cos> 'id' attribute length is " +
                len + " - should be longer than 10", len > 10);
        len = cosInfo.getA().size();
        Assert.assertTrue("CreateCosResponse <cos> has " + len +
                " <a> children - should have at least 12", len >= 12);
    }

    @Test
    public void getCosTest() throws Exception {
        int len;
        CosSelector cosSel;
        CosInfo cosInfo;
        String respId;
        String testCosId = Utility.ensureCosExists(testCos);
        GetCosRequest getReq = new GetCosRequest();
        cosSel = new CosSelector();
        cosSel.setBy(CosBy.ID);
        cosSel.setValue(testCosId);
        getReq.setCos(cosSel);
        GetCosResponse getResp = eif.getCosRequest(getReq);
        Assert.assertNotNull(getResp);
        cosInfo = getResp.getCos();
        Assert.assertNotNull(cosInfo);
        Assert.assertEquals("getCosResponse <cos> 'name' attribute",
                testCos, cosInfo.getName());
        respId = cosInfo.getId();
        Assert.assertEquals("getCosResponse <cos> 'id' attribute", testCosId, respId);
        len = cosInfo.getA().size();
        Assert.assertTrue("GetCosResponse <cos> has " + len +
                " <a> children - should have at least 12", len >= 12);
    }

    @Test
    public void modifyCosTest() throws Exception {
        int len;
        CosSelector cosSel;
        CosInfo cosInfo;
        String respId;
        String testCosId = Utility.ensureCosExists(testCos);
        ModifyCosRequest modReq = new ModifyCosRequest();
        modReq.setId(testCosId);
        Attr modAttr = new Attr();
        modAttr.setN("zimbraMailForwardingAddressMaxNumAddrs");
        modAttr.setValue("99");
        modReq.getA().add(modAttr);
        ModifyCosResponse modResp = eif.modifyCosRequest(modReq);
        Assert.assertNotNull(modResp);
        cosInfo = modResp.getCos();
        Assert.assertNotNull(cosInfo);
        Assert.assertEquals("modifyCosResponse <cos> 'name' attribute",
                testCos, cosInfo.getName());
        respId = cosInfo.getId();
        Assert.assertEquals("modifyCosResponse <cos> 'id' attribute", testCosId, respId);
        len = cosInfo.getA().size();
        Assert.assertTrue("modifyCosResponse <cos> has " + len +
                " <a> children - should have at least 50", len >= 50);

        GetCosRequest getReq = new GetCosRequest();
        cosSel = new CosSelector();
        cosSel.setBy(CosBy.ID);
        cosSel.setValue(testCosId);
        getReq.setCos(cosSel);
        getReq.setAttrs("zimbraMailForwardingAddressMaxNumAddrs");
        GetCosResponse getResp = eif.getCosRequest(getReq);
        Assert.assertNotNull(getResp);
        cosInfo = getResp.getCos();
        Assert.assertNotNull(cosInfo);
        Assert.assertEquals("getCosResponse <cos> 'name' attribute",
                testCos, cosInfo.getName());
        respId = cosInfo.getId();
        Assert.assertEquals("getCosResponse <cos> 'id' attribute", testCosId, respId);
        len = cosInfo.getA().size();
        Assert.assertEquals("Number of GetCosResponse <cos> <a> children", 1, len);
        Attr maxFwdingAddrs = cosInfo.getA().get(0);
        Assert.assertNotNull(maxFwdingAddrs);
        Assert.assertEquals("getCosResponse <cos> <a> 'n' attribute",
                "zimbraMailForwardingAddressMaxNumAddrs", maxFwdingAddrs.getN());
        Assert.assertEquals("getCosResponse <cos> <a n=zimbraMailForwardingAddressMaxNumAddrs> value",
                "99", maxFwdingAddrs.getValue());
        Assert.assertNull("getCosResponse <cos> <a n=zimbraMailForwardingAddressMaxNumAddrs> 'c' attribute", maxFwdingAddrs.isC());
    }

    @Test
    public void copyCosTest() throws Exception {
        int len;
        CosSelector cosSel;
        CosInfo cosInfo;
        String respId;
        String testCosId = Utility.ensureCosExists(testCos);
        CopyCosRequest copyReq = new CopyCosRequest();
        copyReq.setName(testCosCopy);
        cosSel = new CosSelector();
        cosSel.setBy(CosBy.ID);
        cosSel.setValue(testCosId);
        copyReq.setCos(cosSel);
        CopyCosResponse copyResp = eif.copyCosRequest(copyReq);
        Assert.assertNotNull(copyResp);
        cosInfo = copyResp.getCos();
        Assert.assertNotNull(cosInfo);
        Assert.assertEquals("copyCosResponse <cos> 'name' attribute",
                testCosCopy, cosInfo.getName());
        respId = cosInfo.getId();
        Assert.assertNotNull(respId);
        len = cosInfo.getA().size();
        Assert.assertTrue("copyCosResponse <cos> has " + len +
                " <a> children - should have at least 50", len >= 50);
    }

    @Test
    public void renameCosTest() throws Exception {
        int len;
        String testCosId = Utility.ensureCosExists(testCos);
        String respId;
        RenameCosRequest renameCosReq = new RenameCosRequest();
        renameCosReq.setId(testCosId);
        renameCosReq.setNewName("foobar" + testCos);
        RenameCosResponse renameCosResp = eif.renameCosRequest(renameCosReq);
        Assert.assertNotNull(renameCosResp);
        CosInfo cosInfo = renameCosResp.getCos();
        Assert.assertNotNull(cosInfo);
        Assert.assertEquals("renameCosResponse <cos> 'name' attribute",
                "foobar" + testCos, cosInfo.getName());
        respId = cosInfo.getId();
        Assert.assertEquals("renameCosResponse <cos> 'id' attribute",
                testCosId, respId);
        len = cosInfo.getA().size();
        Assert.assertTrue("renameCosResponse <cos> has " + len +
                " <a> children - should have at least 50", len >= 50);
        Utility.deleteCosIfExists("foobar" + testCos);
    }

    @Test
    public void deleteCosTest() throws Exception {
        String testCosId = Utility.ensureCosExists(testCos);
        Assert.assertNotNull(testCosId);
        DeleteCosRequest delReq = new DeleteCosRequest();
        delReq.setId(testCosId);
        DeleteCosResponse delResp = eif.deleteCosRequest(delReq);
        Assert.assertNotNull(delResp);
    }

    @Test
    public void getAllCosTest() throws Exception {
        GetAllCosRequest req = new GetAllCosRequest();
        Utility.addSoapAdminAuthHeader((WSBindingProvider)eif);
        GetAllCosResponse resp = eif.getAllCosRequest(req);
        Assert.assertNotNull("GetAllCosResponse object", resp);
        List <CosInfo> cosInfoList = resp.getCos();
        int len;
        Assert.assertNotNull("GetAllCosResponse list of cos", cosInfoList);
        len = cosInfoList.size();
        Assert.assertTrue("Number of GetAllCosResponse <cos> children is " + len +
                " - should be at least 1", len >= 1);
    }

    @Test
    public void countAccountTest() throws Exception {
        int len;
        String testDomainId = Utility.ensureDomainExists(testAcctDomain);
        String testAccountId = Utility.ensureMailboxExistsForAccount(testAcct);
        String testCosId = Utility.ensureCosExists(testCos);
        ModifyAccountRequest modReq = new ModifyAccountRequest();
        modReq.setId(testAccountId);
        Attr modAttr = new Attr();
        modAttr.setN("zimbraCOSId");
        modAttr.setValue(testCosId);
        modReq.getA().add(modAttr);
        ModifyAccountResponse modResp = eif.modifyAccountRequest(modReq);
        Assert.assertNotNull("ModifyAccountResponse object", modResp);
        AccountInfo accountInfo = modResp.getAccount();
        Assert.assertNotNull("AccountInfo object", accountInfo);
        Assert.assertEquals("modifyAccountResponse <account> 'name' attribute", 
                testAcct, accountInfo.getName());
        String respId = accountInfo.getId();
        Assert.assertEquals("modifyAccountResponse <account> 'id' attribute",
                testAccountId, respId);
        len = accountInfo.getA().size();
        Assert.assertTrue("modifyAccountResponse <account> has " + len +
                " <a> children - should have at least 50", len >= 50);
        CountAccountRequest req = new CountAccountRequest();
        DomainSelector domainSel = new DomainSelector();
        domainSel.setBy(DomainBy.ID);
        domainSel.setValue(testDomainId);
        req.setDomain(domainSel);
        CountAccountResponse resp = eif.countAccountRequest(req);
        Assert.assertNotNull(resp);
        List <CosCountInfo> cosList = resp.getCos();
        Assert.assertNotNull("cos list", cosList);
        len = cosList.size();
        Assert.assertTrue(len + "<cos> children present expect at least 1", len >= 1);
        CosCountInfo firstCos = cosList.get(0);
        Assert.assertTrue(
                "First <cos> id [" + firstCos.getId() + "] len should be longer than 10 chars",
                firstCos.getId().length() > 10);
        Assert.assertTrue(
                "First <cos> name [" + firstCos.getName() + "] len should be longer than 2 chars",
                firstCos.getName().length() > 2);
        long val = firstCos.getValue();
        Assert.assertTrue( "First <cos> value [" + val + "] should be >=0", val >= 0);
    }

    @Test
    public void getAllMailboxesTest() throws Exception {
        GetAllMailboxesRequest req = new GetAllMailboxesRequest();
        Utility.addSoapAdminAuthHeader((WSBindingProvider)eif);
        GetAllMailboxesResponse resp = eif.getAllMailboxesRequest(req);
        Assert.assertNotNull("GetAllMailboxesResponse object", resp);
        List <MailboxInfo> mboxInfoList = resp.getMbox();
        int len;
        Assert.assertNotNull("GetAllMailboxesResponse list of Mailboxes", mboxInfoList);
        len = mboxInfoList.size();
        Assert.assertTrue("Number of GetAllMailboxesResponse <mbox> children is " +
                len + " - should be at least 2", len >= 2);
    }

    @Test
    public void recalculateMailboxCountsTest() throws Exception {
        String accountId = Utility.ensureMailboxExistsForAccount(testAcct);
        RecalculateMailboxCountsRequest req = new RecalculateMailboxCountsRequest();
        MailboxByAccountIdSelector sel = new MailboxByAccountIdSelector();
        sel.setId(accountId);
        req.setMbox(sel);
        RecalculateMailboxCountsResponse resp = eif.recalculateMailboxCountsRequest(req);
        Assert.assertNotNull("RecalculateMailboxCountsResponse object", resp);
        MailboxQuotaInfo quotaInfo = resp.getMbox();
        Assert.assertEquals("<mbox> 'id' attribute", accountId, quotaInfo.getId());
        Assert.assertTrue("RecalculateMailboxCountsResponse <mbox> 'quotaUsed' attribute=" +
                quotaInfo.getUsed() + " should be 0 or more", quotaInfo.getUsed() >= 0);
    }

    @Test
    public void getMailboxTest() throws Exception {
        String accountId = Utility.ensureMailboxExistsForAccount(testAcct);
        GetMailboxRequest req = new GetMailboxRequest();
        MailboxByAccountIdSelector sel = new MailboxByAccountIdSelector();
        sel.setId(accountId);
        req.setMbox(sel);
        GetMailboxResponse resp = eif.getMailboxRequest(req);
        Assert.assertNotNull("GetMailboxResponse object", resp);
        MailboxWithMailboxId mboxid = resp.getMbox();
        Assert.assertNotNull("Object for <mbox> element", mboxid);
        Assert.assertTrue("mboxid = " + mboxid + " should be >0", mboxid.getMbxid() > 0);
    }

    @Test
    public void deleteMailboxTest() throws Exception {
        String accountId = Utility.ensureMailboxExistsForAccount(testAcct);
        DeleteMailboxRequest req = new DeleteMailboxRequest();
        MailboxByAccountIdSelector sel = new MailboxByAccountIdSelector();
        sel.setId(accountId);
        req.setMbox(sel);
        DeleteMailboxResponse resp = eif.deleteMailboxRequest(req);
        Assert.assertNotNull("DeletMailboxResponse object", resp);
        MailboxWithMailboxId mboxid = resp.getMbox();
        Assert.assertNotNull("Object for <mbox> element", mboxid);
        Assert.assertTrue("mboxid = " + mboxid + " should be >0", mboxid.getMbxid() > 0);
    }
}
