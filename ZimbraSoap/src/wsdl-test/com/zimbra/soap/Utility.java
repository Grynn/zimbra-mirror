/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2010, 2011 Zimbra, Inc.
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
package com.zimbra.soap;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.ws.soap.SOAPFaultException;

import com.sun.xml.ws.api.message.Header;
import com.sun.xml.ws.api.message.Headers;
import com.sun.xml.ws.developer.WSBindingProvider;
import com.sun.xml.bind.api.JAXBRIContext;

import com.zimbra.soap.account.wsimport.generated.AccountService_Service;
import com.zimbra.soap.account.wsimport.generated.AccountService;
import com.zimbra.soap.account.wsimport.generated.Account;
import com.zimbra.soap.account.wsimport.generated.By;
import com.zimbra.soap.account.wsimport.generated.AuthRequest;
import com.zimbra.soap.account.wsimport.generated.AuthResponse;
import com.zimbra.soap.admin.wsimport.generated.AccountBy;
import com.zimbra.soap.admin.wsimport.generated.AccountInfo;
import com.zimbra.soap.admin.wsimport.generated.AccountSelector;
import com.zimbra.soap.admin.wsimport.generated.AdminService_Service;
import com.zimbra.soap.admin.wsimport.generated.AdminService;
import com.zimbra.soap.admin.wsimport.generated.Attr;
import com.zimbra.soap.admin.wsimport.generated.CalendarResourceBy;
import com.zimbra.soap.admin.wsimport.generated.CalendarResourceInfo;
import com.zimbra.soap.admin.wsimport.generated.CalendarResourceSelector;
import com.zimbra.soap.admin.wsimport.generated.CosBy;
import com.zimbra.soap.admin.wsimport.generated.CosInfo;
import com.zimbra.soap.admin.wsimport.generated.CosSelector;
import com.zimbra.soap.admin.wsimport.generated.CreateAccountRequest;
import com.zimbra.soap.admin.wsimport.generated.CreateAccountResponse;
import com.zimbra.soap.admin.wsimport.generated.CreateCalendarResourceRequest;
import com.zimbra.soap.admin.wsimport.generated.CreateCalendarResourceResponse;
import com.zimbra.soap.admin.wsimport.generated.CreateCosRequest;
import com.zimbra.soap.admin.wsimport.generated.CreateCosResponse;
import com.zimbra.soap.admin.wsimport.generated.CreateDistributionListRequest;
import com.zimbra.soap.admin.wsimport.generated.CreateDistributionListResponse;
import com.zimbra.soap.admin.wsimport.generated.CreateDomainRequest;
import com.zimbra.soap.admin.wsimport.generated.CreateDomainResponse;
import com.zimbra.soap.admin.wsimport.generated.CreateServerRequest;
import com.zimbra.soap.admin.wsimport.generated.CreateServerResponse;
import com.zimbra.soap.admin.wsimport.generated.CreateVolumeRequest;
import com.zimbra.soap.admin.wsimport.generated.CreateVolumeResponse;
import com.zimbra.soap.admin.wsimport.generated.DeleteAccountRequest;
import com.zimbra.soap.admin.wsimport.generated.DeleteAccountResponse;
import com.zimbra.soap.admin.wsimport.generated.DeleteCalendarResourceRequest;
import com.zimbra.soap.admin.wsimport.generated.DeleteCalendarResourceResponse;
import com.zimbra.soap.admin.wsimport.generated.DeleteCosRequest;
import com.zimbra.soap.admin.wsimport.generated.DeleteCosResponse;
import com.zimbra.soap.admin.wsimport.generated.DeleteDistributionListRequest;
import com.zimbra.soap.admin.wsimport.generated.DeleteDomainRequest;
import com.zimbra.soap.admin.wsimport.generated.DeleteServerRequest;
import com.zimbra.soap.admin.wsimport.generated.DeleteVolumeRequest;
import com.zimbra.soap.admin.wsimport.generated.DistributionListBy;
import com.zimbra.soap.admin.wsimport.generated.DistributionListInfo;
import com.zimbra.soap.admin.wsimport.generated.DistributionListSelector;
import com.zimbra.soap.admin.wsimport.generated.DomainBy;
import com.zimbra.soap.admin.wsimport.generated.DomainInfo;
import com.zimbra.soap.admin.wsimport.generated.DomainSelector;
import com.zimbra.soap.admin.wsimport.generated.GetAccountRequest;
import com.zimbra.soap.admin.wsimport.generated.GetAccountResponse;
import com.zimbra.soap.admin.wsimport.generated.GetAllVolumesRequest;
import com.zimbra.soap.admin.wsimport.generated.GetAllVolumesResponse;
import com.zimbra.soap.admin.wsimport.generated.GetCalendarResourceRequest;
import com.zimbra.soap.admin.wsimport.generated.GetCalendarResourceResponse;
import com.zimbra.soap.admin.wsimport.generated.GetCosRequest;
import com.zimbra.soap.admin.wsimport.generated.GetCosResponse;
import com.zimbra.soap.admin.wsimport.generated.GetDistributionListRequest;
import com.zimbra.soap.admin.wsimport.generated.GetDistributionListResponse;
import com.zimbra.soap.admin.wsimport.generated.GetDomainInfoRequest;
import com.zimbra.soap.admin.wsimport.generated.GetDomainInfoResponse;
import com.zimbra.soap.admin.wsimport.generated.GetDomainRequest;
import com.zimbra.soap.admin.wsimport.generated.GetDomainResponse;
import com.zimbra.soap.admin.wsimport.generated.GetServerRequest;
import com.zimbra.soap.admin.wsimport.generated.GetServerResponse;
import com.zimbra.soap.admin.wsimport.generated.ServerBy;
import com.zimbra.soap.admin.wsimport.generated.ServerInfo;
import com.zimbra.soap.admin.wsimport.generated.ServerSelector;
import com.zimbra.soap.admin.wsimport.generated.VolumeInfo;
import com.zimbra.soap.mail.wsimport.generated.MailService_Service;
import com.zimbra.soap.mail.wsimport.generated.MailService;

import org.junit.Assert;

/**
 * Current assumption : user1 exists with password test123
 */
public class Utility {
    private static AccountService acctSvcEIF = null;
    private static AdminService adminSvcEIF = null;
    private static MailService mailSvcEIF = null;
    private static String acctAuthToken = null;
    private static String adminAuthToken = null;

    public static void addSoapAcctAuthHeader(WSBindingProvider bp) throws Exception {
        Utility.getAccountServiceAuthToken();
        // Note that am not using JAXB generated HeaderContext here
        JAXBRIContext jaxb = (JAXBRIContext) JAXBRIContext.newInstance(com.zimbra.soap.header.HeaderContext.class);
        com.zimbra.soap.header.HeaderContext hdrCtx = new com.zimbra.soap.header.HeaderContext();
        hdrCtx.setAuthToken(acctAuthToken);
        Header soapHdr = Headers.create(jaxb,hdrCtx);
        List <Header> soapHdrs = new ArrayList <Header>();
        soapHdrs.add(soapHdr);
        // See http://metro.java.net/1.5/guide/SOAP_headers.html
        // WSBindingProvider bp = (WSBindingProvider)acctSvcEIF;
        bp.setOutboundHeaders(soapHdrs);
    }

    public static String getAccountServiceAuthToken() throws Exception {
        if (acctAuthToken == null) {
            acctAuthToken = getAccountServiceAuthToken("user1", "test123");
        }
        return acctAuthToken;
    }

    public static String getAccountServiceAuthToken(String acctName, String password)
    throws Exception {
        Utility.getAcctSvcEIF();
        AuthRequest authReq = new AuthRequest();
        Account acct = new Account();
        acct.setBy(By.NAME);
        acct.setValue(acctName);
        authReq.setAccount(acct);
        authReq.setPassword(password);
        authReq.setPreauth(null);
        authReq.setAuthToken(null);
        // Invoke the methods.
        AuthResponse authResponse = getAcctSvcEIF().authRequest(authReq);
        Assert.assertNotNull(authResponse);
        return authResponse.getAuthToken();
    }

    private static void setAcctSvcEIF(AccountService acctSvcEIF) {
        Utility.acctSvcEIF = acctSvcEIF;
    }

    public static AccountService getAcctSvcEIF() throws Exception {
        if (acctSvcEIF == null) {
            // The AccountService_Service class is the Java type bound to
            // the service section of the WSDL document.
            AccountService_Service acctSvc = new AccountService_Service();
            setAcctSvcEIF(acctSvc.getAccountServicePort());
        }
        return acctSvcEIF;
    }

    public static void addSoapAdminAuthHeader(WSBindingProvider bp) throws Exception {
        Utility.getAdminServiceAuthToken();
        // Note that am not using JAXB generated HeaderContext here
        JAXBRIContext jaxb = (JAXBRIContext) JAXBRIContext.newInstance(com.zimbra.soap.header.HeaderContext.class);
        com.zimbra.soap.header.HeaderContext hdrCtx = new com.zimbra.soap.header.HeaderContext();
        hdrCtx.setAuthToken(adminAuthToken);
        Header soapHdr = Headers.create(jaxb,hdrCtx);
        List <Header> soapHdrs = new ArrayList <Header>();
        soapHdrs.add(soapHdr);
        // See http://metro.java.net/1.5/guide/SOAP_headers.html
        // WSBindingProvider bp = (WSBindingProvider)acctSvcEIF;
        bp.setOutboundHeaders(soapHdrs);
    }

    public static String getAdminServiceAuthToken() throws Exception {
        Utility.getAdminSvcEIF();
        if (adminAuthToken == null) {
            Utility.getAdminSvcEIF();
            com.zimbra.soap.admin.wsimport.generated.AuthRequest authReq =
                    new com.zimbra.soap.admin.wsimport.generated.AuthRequest();
            com.zimbra.soap.admin.wsimport.generated.AccountSelector acct =
                    new com.zimbra.soap.admin.wsimport.generated.AccountSelector();
            acct.setBy(com.zimbra.soap.admin.wsimport.generated.AccountBy.NAME);
            acct.setValue("admin");
            authReq.setAccount(acct);
            authReq.setPassword("test123");
            authReq.setAuthToken(null);
            com.zimbra.soap.admin.wsimport.generated.AuthResponse authResponse =
                    getAdminSvcEIF().authRequest(authReq);
            Assert.assertNotNull(authResponse);
            adminAuthToken = authResponse.getAuthToken();
            Assert.assertTrue(adminAuthToken != null);
            Assert.assertTrue(adminAuthToken.length() > 10);
        }
        return adminAuthToken;
    }

    private static void setAdminSvcEIF(AdminService adminSvcEIF) {
        Utility.adminSvcEIF = adminSvcEIF;
    }

    public static AdminService getAdminSvcEIF() throws Exception {
        if (adminSvcEIF == null) {
            // The AccountService_Service class is the Java type bound to
            // the service section of the WSDL document.
            AdminService_Service adminSvc = new AdminService_Service();
            setAdminSvcEIF(adminSvc.getAdminServicePort());
        }
        return adminSvcEIF;
    }

    private static void setMailSvcEIF(MailService mailSvcEIF) {
        Utility.mailSvcEIF = mailSvcEIF;
    }

    public static MailService getMailSvcEIF() {
        if (mailSvcEIF == null) {
            MailService_Service mailSvc = new MailService_Service();
            Utility.setMailSvcEIF(mailSvc.getMailServicePort());
        }
        return mailSvcEIF;
    }

    public static void setUpToAcceptAllHttpsServerCerts() {
        // Create a trust manager that does not validate certificate chains
        // without this, we need to import the server certificate into the trust store.
        // when using https as is required for Admin
        javax.net.ssl.TrustManager[] trustAllCerts = new javax.net.ssl.TrustManager[]{
                new javax.net.ssl.X509TrustManager() {
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() { return null; }
                    public void checkClientTrusted( java.security.cert.X509Certificate[] certs, String authType) { }
                    public void checkServerTrusted( java.security.cert.X509Certificate[] certs, String authType) { }
                    }
                };
        // Install the all-trusting trust manager
        try {
            javax.net.ssl.SSLContext sc = javax.net.ssl.SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            javax.net.ssl.HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) { }
    }
    
    public static void deleteDomainIfExists(String domainName) throws Exception {
        Utility.addSoapAdminAuthHeader((WSBindingProvider)getAdminSvcEIF());
        try {
            // Delete the test domain if it hasn't already been deleted
            GetDomainRequest getReq = new GetDomainRequest();
            DomainSelector domainSel = new DomainSelector();
            domainSel.setBy(DomainBy.NAME);
            domainSel.setValue(domainName);
            getReq.setDomain(domainSel);
            getReq.setApplyConfig(true);
            GetDomainResponse getResp = getAdminSvcEIF().getDomainRequest(getReq);
            if (getResp != null) {
                DomainInfo domainInfo = getResp.getDomain();
                DeleteDomainRequest delReq = new DeleteDomainRequest();
                delReq.setId(domainInfo.getId());
                getAdminSvcEIF().deleteDomainRequest(delReq);
            }
        } catch (SOAPFaultException sfe) {
            String missive = sfe.getMessage();
            if (!missive.startsWith("no such domain:"))
                System.err.println("Exception " + sfe.toString() + 
                        " thrown attempting to delete domain " + domainName);
        }
    }

    public static void deleteServerIfExists(String serverName) throws Exception {
        Utility.addSoapAdminAuthHeader((WSBindingProvider)getAdminSvcEIF());
        try {
            // Delete the test server if it hasn't already been deleted
            GetServerRequest getSvrReq = new GetServerRequest();
            ServerSelector svrSel = new ServerSelector();
            svrSel.setBy(ServerBy.NAME);
            svrSel.setValue(serverName);
            getSvrReq.setServer(svrSel);
            getSvrReq.setApplyConfig(true);
            GetServerResponse getSvrResp = getAdminSvcEIF().getServerRequest(getSvrReq);
            if (getSvrResp != null) {
                ServerInfo serverInfo = getSvrResp.getServer();
                DeleteServerRequest delReq = new DeleteServerRequest();
                delReq.setId(serverInfo.getId());
                getAdminSvcEIF().deleteServerRequest(delReq);
            }
        } catch (SOAPFaultException sfe) {
            String missive = sfe.getMessage();
            if (!missive.startsWith("no such server:"))
                System.err.println("Exception " + sfe.toString() + 
                        " thrown attempting to delete domain " + serverName);
        }
    }

    public static void deleteAccountIfExists(String accountName)
    throws Exception {
        Utility.addSoapAdminAuthHeader((WSBindingProvider)getAdminSvcEIF());
        try {
            GetAccountRequest getReq = new GetAccountRequest();
            AccountSelector accountSel = new AccountSelector();
            accountSel.setBy(AccountBy.NAME);
            accountSel.setValue(accountName);
            getReq.setAccount(accountSel);
            GetAccountResponse getResp =
                    getAdminSvcEIF().getAccountRequest(getReq);
            Assert.assertNotNull(getResp);
            AccountInfo accountInfo = getResp.getAccount();
            Assert.assertNotNull(accountInfo);
            String respId = accountInfo.getId();
            DeleteAccountRequest delReq = new DeleteAccountRequest();
            delReq.setId(respId);
            DeleteAccountResponse delResp =
                    getAdminSvcEIF().deleteAccountRequest(delReq);
            Assert.assertNotNull(delResp);
        } catch (SOAPFaultException sfe) {
            String missive = sfe.getMessage();
            if (!missive.startsWith("no such account:"))
                System.err.println("Exception " + sfe.toString() + 
                        " thrown attempting to delete account " + accountName);
        }
    }

    public static void deleteCalendarResourceIfExists(String calResourceName)
    throws Exception {
        Utility.addSoapAdminAuthHeader((WSBindingProvider)getAdminSvcEIF());
        try {
            GetCalendarResourceRequest getReq =
                    new GetCalendarResourceRequest();
            CalendarResourceSelector calResourceSel =
                    new CalendarResourceSelector();
            calResourceSel.setBy(CalendarResourceBy.NAME);
            calResourceSel.setValue(calResourceName);
            getReq.setCalresource(calResourceSel);
            GetCalendarResourceResponse getResp =
                getAdminSvcEIF().getCalendarResourceRequest(getReq);
            Assert.assertNotNull(getResp);
            CalendarResourceInfo calResourceInfo = getResp.getCalresource();
            Assert.assertNotNull(calResourceInfo);
            String respId = calResourceInfo.getId();
            DeleteCalendarResourceRequest delReq =
                    new DeleteCalendarResourceRequest();
            delReq.setId(respId);
            DeleteCalendarResourceResponse delResp =
                getAdminSvcEIF().deleteCalendarResourceRequest(delReq);
            Assert.assertNotNull(delResp);
        } catch (SOAPFaultException sfe) {
            String missive = sfe.getMessage();
            if (!missive.startsWith("no such calendar resource:"))
                System.err.println("Exception " + sfe.toString() + 
                        " thrown attempting to delete CalendarResource "
                        + calResourceName);
        }
    }

    public static void deleteCosIfExists(String cosName) throws Exception {
        Utility.addSoapAdminAuthHeader((WSBindingProvider)getAdminSvcEIF());
        try {
            GetCosRequest getReq = new GetCosRequest();
            CosSelector cosSel = new CosSelector();
            cosSel.setBy(CosBy.NAME);
            cosSel.setValue(cosName);
            getReq.setCos(cosSel);
            GetCosResponse getResp = getAdminSvcEIF().getCosRequest(getReq);
            Assert.assertNotNull(getResp);
            CosInfo cosInfo = getResp.getCos();
            Assert.assertNotNull(cosInfo);
            String respId = cosInfo.getId();
            DeleteCosRequest delReq = new DeleteCosRequest();
            delReq.setId(respId);
            DeleteCosResponse delResp = getAdminSvcEIF().deleteCosRequest(delReq);
            Assert.assertNotNull(delResp);
        } catch (SOAPFaultException sfe) {
            String missive = sfe.getMessage();
            if (!missive.startsWith("no such cos:"))
                System.err.println("Exception " + sfe.toString() + 
                        " thrown attempting to delete cos " + cosName);
        }
    }

    public static void deleteVolumeIfExists(String name) throws Exception {
        Utility.addSoapAdminAuthHeader((WSBindingProvider)getAdminSvcEIF());
        GetAllVolumesRequest gavReq = new GetAllVolumesRequest();
        GetAllVolumesResponse gavResp =
                getAdminSvcEIF().getAllVolumesRequest(gavReq);
        for (VolumeInfo volume : gavResp.getVolume()) {
            if (name.equals(volume.getName())) {
                DeleteVolumeRequest delReq = new DeleteVolumeRequest();
                delReq.setId(volume.getId());
                getAdminSvcEIF().deleteVolumeRequest(delReq);
                String volRootpath = volume.getRootpath();
                try {
                    if (volRootpath != null && (volRootpath.length() > 0))
                        new File(volume.getRootpath()).deleteOnExit();
                } catch (Exception ex) {
                    System.err.println("Exception " + ex.toString() + 
                    " thrown inside deleteVolumeIfExists - deleting rootPath="
                            + volRootpath + " for volume=" + name);
                return;
                }
            }
        }
    }

    public static void deleteDistributionListIfExists(String name) throws Exception {
        Utility.addSoapAdminAuthHeader((WSBindingProvider)getAdminSvcEIF());
        GetDistributionListRequest getInfoReq = new GetDistributionListRequest();
        DistributionListSelector dlSel = new DistributionListSelector();
        dlSel.setBy(DistributionListBy.NAME);
        dlSel.setValue(name);
        getInfoReq.setDl(dlSel);
        try {
            GetDistributionListResponse getResp = adminSvcEIF.getDistributionListRequest(getInfoReq);
            Assert.assertNotNull(getResp);
            DeleteDistributionListRequest delReq = new DeleteDistributionListRequest();
            delReq.setId(getResp.getDl().getId());
            Assert.assertNotNull("DeleteDistributionListResponse object",
                    getAdminSvcEIF().deleteDistributionListRequest(delReq));
        } catch (SOAPFaultException sfe) {
            String missive = sfe.getMessage();
            if (!missive.startsWith("no such dl:"))
                System.err.println("Exception " + sfe.toString() + 
                        " thrown attempting to delete dl " + name);
        }
    }

    public static String ensureDomainExists(String domainName) throws Exception {
        String domainId = null;
        Utility.addSoapAdminAuthHeader((WSBindingProvider)getAdminSvcEIF());
        GetDomainInfoRequest getInfoReq = new GetDomainInfoRequest();
        getInfoReq.setApplyConfig(false);
        DomainSelector domainSel = new DomainSelector();
        domainSel.setBy(DomainBy.NAME);
        domainSel.setValue(domainName);
        getInfoReq.setDomain(domainSel);
        try {
            GetDomainInfoResponse getInfoResp = adminSvcEIF.getDomainInfoRequest(getInfoReq);
            Assert.assertNotNull(getInfoResp);
            domainId = getInfoResp.getDomain().getId();
            if (domainId.equals("globalconfig-dummy-id"))
                domainId = null;
        } catch (SOAPFaultException sfe) {
        }
        if (domainId != null) {
            return domainId;
        }
        else {
            CreateDomainRequest req = new CreateDomainRequest();
            req.setName(domainName);
            Utility.addSoapAdminAuthHeader((WSBindingProvider)adminSvcEIF);
            CreateDomainResponse resp = adminSvcEIF.createDomainRequest(req);
            Assert.assertNotNull(resp);
            DomainInfo domainInfo = resp.getDomain();
            Assert.assertNotNull(domainInfo);
            Assert.assertEquals("createDomainResponse <domain> 'name' attribute", domainName, domainInfo.getName());
            return domainInfo.getId();
        }
    }

    public static String ensureServerExists(String serverName) throws Exception {
        Utility.addSoapAdminAuthHeader((WSBindingProvider)getAdminSvcEIF());
        GetServerRequest getInfoReq = new GetServerRequest();
        ServerSelector serverSel = new ServerSelector();
        serverSel.setBy(ServerBy.NAME);
        serverSel.setValue(serverName);
        getInfoReq.setServer(serverSel);
        try {
            GetServerResponse getResp = adminSvcEIF.getServerRequest(getInfoReq);
            Assert.assertNotNull(getResp);
            return getResp.getServer().getId();
        } catch (SOAPFaultException sfe) {
            CreateServerRequest createAcctReq = new CreateServerRequest();
            createAcctReq.setName(serverName);
            Utility.addSoapAdminAuthHeader((WSBindingProvider)adminSvcEIF);
            CreateServerResponse resp = adminSvcEIF.createServerRequest(createAcctReq);
            Assert.assertNotNull(resp);
            ServerInfo serverInfo = resp.getServer();
            return serverInfo.getId();
        }
    }

    public static String ensureAccountExists(String accountName)
    throws Exception {
        Utility.addSoapAdminAuthHeader((WSBindingProvider)getAdminSvcEIF());
        String domainName = accountName.substring(accountName.indexOf('@') + 1);
        ensureDomainExists(domainName);
        GetAccountRequest getInfoReq = new GetAccountRequest();
        AccountSelector accountSel = new AccountSelector();
        accountSel.setBy(AccountBy.NAME);
        accountSel.setValue(accountName);
        getInfoReq.setAccount(accountSel);
        try {
            GetAccountResponse getResp =
                    adminSvcEIF.getAccountRequest(getInfoReq);
            Assert.assertNotNull(getResp);
            return getResp.getAccount().getId();
        } catch (SOAPFaultException sfe) {
            CreateAccountRequest createAcctReq = new CreateAccountRequest();
            createAcctReq.setName(accountName);
            createAcctReq.setPassword("test123");
            Utility.addSoapAdminAuthHeader((WSBindingProvider)adminSvcEIF);
            CreateAccountResponse resp =
                    adminSvcEIF.createAccountRequest(createAcctReq);
            Assert.assertNotNull(resp);
            AccountInfo accountInfo = resp.getAccount();
            return accountInfo.getId();
        }
    }

    public static String ensureCalendarResourceExists(
            String calResourceName, String displayName)
    throws Exception {
        Utility.addSoapAdminAuthHeader((WSBindingProvider)getAdminSvcEIF());
        String domainName =
                    calResourceName.substring(calResourceName.indexOf('@') + 1);
        ensureDomainExists(domainName);
        GetCalendarResourceRequest getInfoReq =
                    new GetCalendarResourceRequest();
        CalendarResourceSelector calResourceSel =
                    new CalendarResourceSelector();
        calResourceSel.setBy(CalendarResourceBy.NAME);
        calResourceSel.setValue(calResourceName);
        getInfoReq.setCalresource(calResourceSel);
        try {
            GetCalendarResourceResponse getResp =
                    adminSvcEIF.getCalendarResourceRequest(getInfoReq);
            Assert.assertNotNull(getResp);
            return getResp.getCalresource().getId();
        } catch (SOAPFaultException sfe) {
            CreateCalendarResourceRequest createAcctReq =
                    new CreateCalendarResourceRequest();
            createAcctReq.setName(calResourceName);
            createAcctReq.setPassword("test123");
            createAcctReq.getA().add(Utility.mkAttr("displayName", displayName));
            createAcctReq.getA().add(Utility.mkAttr("zimbraCalResType", "Location"));
            createAcctReq.getA().add(Utility.mkAttr(
                "zimbraCalResLocationDisplayName", "Harare"));
            Utility.addSoapAdminAuthHeader((WSBindingProvider)adminSvcEIF);
            CreateCalendarResourceResponse resp =
                    adminSvcEIF.createCalendarResourceRequest(createAcctReq);
            Assert.assertNotNull(resp);
            CalendarResourceInfo calResourceInfo = resp.getCalresource();
            return calResourceInfo.getId();
        }
    }

    /**
     * Creating an account does not create the associated mailbox until first authenticated
     * access.  This ensures that the mailbox exists by authenticating against the account.
     * 
     * @param accountName - name of account - must have password "test123" if exists already
     * @return
     * @throws Exception
     */
    public static String ensureMailboxExistsForAccount(String accountName) throws Exception {
        String accountId = ensureAccountExists(accountName);
        getAccountServiceAuthToken(accountName, "test123");
        return accountId;
    }

    public static String ensureDistributionListExists(String name) throws Exception {
        Utility.addSoapAdminAuthHeader((WSBindingProvider)getAdminSvcEIF());
        String domainName = name.substring(name.indexOf('@') + 1);
        ensureDomainExists(domainName);
        Utility.addSoapAdminAuthHeader((WSBindingProvider)getAdminSvcEIF());
        GetDistributionListRequest getInfoReq = new GetDistributionListRequest();
        DistributionListSelector dlSel = new DistributionListSelector();
        dlSel.setBy(DistributionListBy.NAME);
        dlSel.setValue(name);
        getInfoReq.setDl(dlSel);
        try {
            GetDistributionListResponse getResp = adminSvcEIF.getDistributionListRequest(getInfoReq);
            Assert.assertNotNull(getResp);
            return getResp.getDl().getId();
        } catch (SOAPFaultException sfe) {
            CreateDistributionListRequest createAcctReq = new CreateDistributionListRequest();
            createAcctReq.setName(name);
            Utility.addSoapAdminAuthHeader((WSBindingProvider)adminSvcEIF);
            CreateDistributionListResponse resp = adminSvcEIF.createDistributionListRequest(createAcctReq);
            Assert.assertNotNull(resp);
            DistributionListInfo dlInfo = resp.getDl();
            return dlInfo.getId();
        }
    }

    public static String ensureCosExists(String cosName) throws Exception {
        Utility.addSoapAdminAuthHeader((WSBindingProvider)getAdminSvcEIF());
        GetCosRequest getInfoReq = new GetCosRequest();
        CosSelector cosSel = new CosSelector();
        cosSel.setBy(CosBy.NAME);
        cosSel.setValue(cosName);
        getInfoReq.setCos(cosSel);
        try {
            GetCosResponse getResp = adminSvcEIF.getCosRequest(getInfoReq);
            Assert.assertNotNull(getResp);
            return getResp.getCos().getId();
        } catch (SOAPFaultException sfe) {
            CreateCosRequest createAcctReq = new CreateCosRequest();
            createAcctReq.setName(cosName);
            Utility.addSoapAdminAuthHeader((WSBindingProvider)adminSvcEIF);
            CreateCosResponse resp = adminSvcEIF.createCosRequest(createAcctReq);
            Assert.assertNotNull(resp);
            CosInfo cosInfo = resp.getCos();
            return cosInfo.getId();
        }
    }

    public static Short ensureVolumeExists(String name, String rootPath)
    throws Exception {
        Utility.addSoapAdminAuthHeader((WSBindingProvider)getAdminSvcEIF());
        GetAllVolumesRequest gavReq = new GetAllVolumesRequest();
        GetAllVolumesResponse gavResp =
                getAdminSvcEIF().getAllVolumesRequest(gavReq);
        for (VolumeInfo volume : gavResp.getVolume()) {
            if (name.equals(volume.getName())) {
                if (rootPath.equals(volume.getRootpath())) 
                    return volume.getId();
                deleteVolumeIfExists(name);
                break;
            }
        }
        Assert.assertTrue("Creating dir=" + rootPath +
                " for volumeName=" + name, new File(rootPath).mkdir());
        CreateVolumeRequest req = new CreateVolumeRequest();
        VolumeInfo volume = new VolumeInfo();
        volume.setName(name);
        volume.setRootpath(rootPath);
        volume.setCompressionThreshold(4096L);
        volume.setType((short)1);
        volume.setCompressBlobs(true);
        req.setVolume(volume);
        Utility.addSoapAdminAuthHeader((WSBindingProvider)getAdminSvcEIF());
        CreateVolumeResponse resp = getAdminSvcEIF().createVolumeRequest(req);
        Assert.assertNotNull(resp);
        VolumeInfo volumeInfo = resp.getVolume();
        Assert.assertNotNull(volumeInfo);
        Assert.assertEquals("CreateVolumeResponse <volume> 'name' attribute",
                name, volumeInfo.getName());
        Short testVolumeId = volumeInfo.getId();
        Assert.assertTrue(
                "CreateVolumeResponse <volume> 'id' attribute " +
                testVolumeId + " - should be at least 1", testVolumeId >= 1);
        return testVolumeId;
    }

    public static Attr mkAttr(String name, String value) {
        Attr attr = new Attr();
        attr.setN(name);
        attr.setValue(value);
        return attr;
    }
}
