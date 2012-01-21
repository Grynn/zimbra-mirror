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
import java.util.Map;

import javax.xml.ws.soap.SOAPFaultException;

import com.google.common.collect.Maps;
import com.sun.xml.ws.api.message.Header;
import com.sun.xml.ws.api.message.Headers;
import com.sun.xml.ws.developer.SchemaValidationFeature;
import com.sun.xml.ws.developer.WSBindingProvider;
import com.sun.xml.bind.api.JAXBRIContext;

import generated.zcsclient.account.testAuthRequest;
import generated.zcsclient.account.testAuthResponse;
import generated.zcsclient.admin.testAccountInfo;
import generated.zcsclient.admin.testAttr;
import generated.zcsclient.admin.testCalendarResourceBy;
import generated.zcsclient.admin.testCalendarResourceInfo;
import generated.zcsclient.admin.testCalendarResourceSelector;
import generated.zcsclient.admin.testCosBy;
import generated.zcsclient.admin.testCosInfo;
import generated.zcsclient.admin.testCosSelector;
import generated.zcsclient.admin.testCreateAccountRequest;
import generated.zcsclient.admin.testCreateAccountResponse;
import generated.zcsclient.admin.testCreateCalendarResourceRequest;
import generated.zcsclient.admin.testCreateCalendarResourceResponse;
import generated.zcsclient.admin.testCreateCosRequest;
import generated.zcsclient.admin.testCreateCosResponse;
import generated.zcsclient.admin.testCreateDistributionListRequest;
import generated.zcsclient.admin.testCreateDistributionListResponse;
import generated.zcsclient.admin.testCreateDomainRequest;
import generated.zcsclient.admin.testCreateDomainResponse;
import generated.zcsclient.admin.testCreateServerRequest;
import generated.zcsclient.admin.testCreateServerResponse;
import generated.zcsclient.admin.testCreateVolumeRequest;
import generated.zcsclient.admin.testCreateVolumeResponse;
import generated.zcsclient.admin.testDeleteAccountRequest;
import generated.zcsclient.admin.testDeleteAccountResponse;
import generated.zcsclient.admin.testDeleteCalendarResourceRequest;
import generated.zcsclient.admin.testDeleteCalendarResourceResponse;
import generated.zcsclient.admin.testDeleteCosRequest;
import generated.zcsclient.admin.testDeleteCosResponse;
import generated.zcsclient.admin.testDeleteDistributionListRequest;
import generated.zcsclient.admin.testDeleteDomainRequest;
import generated.zcsclient.admin.testDeleteServerRequest;
import generated.zcsclient.admin.testDeleteVolumeRequest;
import generated.zcsclient.admin.testDistributionListBy;
import generated.zcsclient.admin.testDistributionListInfo;
import generated.zcsclient.admin.testDistributionListSelector;
import generated.zcsclient.admin.testDomainBy;
import generated.zcsclient.admin.testDomainInfo;
import generated.zcsclient.admin.testDomainSelector;
import generated.zcsclient.admin.testGetAccountRequest;
import generated.zcsclient.admin.testGetAccountResponse;
import generated.zcsclient.admin.testGetAllVolumesRequest;
import generated.zcsclient.admin.testGetAllVolumesResponse;
import generated.zcsclient.admin.testGetCalendarResourceRequest;
import generated.zcsclient.admin.testGetCalendarResourceResponse;
import generated.zcsclient.admin.testGetCosRequest;
import generated.zcsclient.admin.testGetCosResponse;
import generated.zcsclient.admin.testGetDistributionListRequest;
import generated.zcsclient.admin.testGetDistributionListResponse;
import generated.zcsclient.admin.testGetDomainInfoRequest;
import generated.zcsclient.admin.testGetDomainInfoResponse;
import generated.zcsclient.admin.testGetDomainRequest;
import generated.zcsclient.admin.testGetDomainResponse;
import generated.zcsclient.admin.testGetServerRequest;
import generated.zcsclient.admin.testGetServerResponse;
import generated.zcsclient.admin.testServerBy;
import generated.zcsclient.admin.testServerInfo;
import generated.zcsclient.admin.testServerSelector;
import generated.zcsclient.admin.testVolumeInfo;
import generated.zcsclient.ws.service.ZcsAdminPortType;
import generated.zcsclient.ws.service.ZcsAdminService;
import generated.zcsclient.ws.service.ZcsPortType;
import generated.zcsclient.ws.service.ZcsService;
import generated.zcsclient.zm.testAccountBy;
import generated.zcsclient.zm.testAccountSelector;

import org.junit.Assert;

/**
 * Current assumption : user1 exists with password test123
 */
public class Utility {
    private static final String DEFAULT_PASS = "test123";
    private static ZcsPortType zcsSvcEIF = null;
    private static ZcsPortType nvZcsSvcEIF = null;
    private static ZcsAdminPortType adminSvcEIF = null;
    private static ZcsAdminPortType nvAdminSvcEIF = null;
    private static String adminAuthToken = null;
    private static Map<String,String> acctAuthToks = Maps.newHashMap();

    public static void addSoapAcctAuthHeader(WSBindingProvider bp,
                String authToken)
    throws Exception {
        // Note that am not using JAXB generated HeaderContext here
        JAXBRIContext jaxb = (JAXBRIContext) JAXBRIContext.newInstance(
                    com.zimbra.soap.header.HeaderContext.class);
        com.zimbra.soap.header.HeaderContext hdrCtx =
            new com.zimbra.soap.header.HeaderContext();
        hdrCtx.setAuthToken(authToken);
        Header soapHdr = Headers.create(jaxb,hdrCtx);
        List <Header> soapHdrs = new ArrayList <Header>();
        soapHdrs.add(soapHdr);
        // See http://metro.java.net/1.5/guide/SOAP_headers.html
        // WSBindingProvider bp = (WSBindingProvider)acctSvcEIF;
        bp.setOutboundHeaders(soapHdrs);
    }

    public static String addSoapAcctAuthHeaderForAcct(WSBindingProvider bp,
            String acctName)
    throws Exception {
        String authTok;
        if (acctAuthToks.containsKey(acctName))
            authTok = acctAuthToks.get(acctName);
        else {
            authTok = Utility.getAccountServiceAuthToken(acctName, DEFAULT_PASS);
            acctAuthToks.put(acctName, authTok);
        }
        addSoapAcctAuthHeader(bp, authTok);
        return authTok;
    }

    public static void addSoapAcctAuthHeader(WSBindingProvider bp)
    throws Exception {
        String authTok = Utility.getAccountServiceAuthToken();
        addSoapAcctAuthHeader(bp, authTok);
    }

    public static String getAccountServiceAuthToken() throws Exception {
        String acctName = "user1";
        if (acctAuthToks.containsKey(acctName))
            return acctAuthToks.get(acctName);
        return getAccountServiceAuthToken(acctName, DEFAULT_PASS);
    }

    public static String getAccountServiceAuthToken(
                    String acctName, String password)
    throws Exception {
        Utility.getZcsSvcEIF();
        testAuthRequest authReq = new testAuthRequest();
        testAccountSelector acct = new testAccountSelector();
        acct.setBy(testAccountBy.NAME);
        acct.setValue(acctName);
        authReq.setAccount(acct);
        authReq.setPassword(password);
        authReq.setPreauth(null);
        authReq.setAuthToken(null);
        // Invoke the methods.
        testAuthResponse authResponse = getZcsSvcEIF().authRequest(authReq);
        Assert.assertNotNull(authResponse);
        return authResponse.getAuthToken();
    }

    private static void setZcsSvcEIF(ZcsPortType svcEIF) {
        Utility.zcsSvcEIF = svcEIF;
    }

    private static void setNvZcsSvcEIF(ZcsPortType svcEIF) {
        Utility.nvZcsSvcEIF = svcEIF;
    }

    private static void setAdminSvcEIF(ZcsAdminPortType svcEIF) {
        Utility.adminSvcEIF = svcEIF;
    }

    private static void setNvAdminSvcEIF(ZcsAdminPortType svcEIF) {
        Utility.nvAdminSvcEIF = svcEIF;
    }

    public static ZcsPortType getZcsSvcEIF() throws Exception {
        if (zcsSvcEIF == null) {
            // The ZcsService class is the Java type bound to the service section of the WSDL document.
            ZcsService zcsSvc = new ZcsService();
            SchemaValidationFeature feature = new SchemaValidationFeature();
            setZcsSvcEIF(zcsSvc.getZcsServicePort(feature));
        }
        return zcsSvcEIF;
    }

    public static ZcsPortType getNonValidatingZcsSvcEIF() throws Exception {
        if (nvZcsSvcEIF == null) {
            ZcsService zcsSvc = new ZcsService();
            setNvZcsSvcEIF(zcsSvc.getZcsServicePort());
        }
        return nvZcsSvcEIF;
    }

    public static ZcsAdminPortType getAdminSvcEIF() throws Exception {
        if (adminSvcEIF == null) {
            // The ZcsAdminService class is the Java type bound to the service section of the WSDL document.
            ZcsAdminService zcsSvc = new ZcsAdminService();
            SchemaValidationFeature feature = new SchemaValidationFeature();
            setAdminSvcEIF(zcsSvc.getZcsAdminServicePort(feature));
        }
        return adminSvcEIF;
    }

    public static ZcsAdminPortType getNonValidatingAdminSvcEIF() throws Exception {
        if (nvAdminSvcEIF == null) {
            ZcsAdminService zcsSvc = new ZcsAdminService();
            setNvAdminSvcEIF(zcsSvc.getZcsAdminServicePort());
        }
        return nvAdminSvcEIF;
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
            generated.zcsclient.admin.testAuthRequest authReq = new generated.zcsclient.admin.testAuthRequest();
            generated.zcsclient.zm.testAccountSelector acct = new generated.zcsclient.zm.testAccountSelector();
            acct.setBy(generated.zcsclient.zm.testAccountBy.NAME);
            acct.setValue("admin");
            authReq.setAccount(acct);
            authReq.setPassword(DEFAULT_PASS);
            authReq.setAuthToken(null);
            generated.zcsclient.admin.testAuthResponse authResponse = getAdminSvcEIF().authRequest(authReq);
            Assert.assertNotNull(authResponse);
            adminAuthToken = authResponse.getAuthToken();
            Assert.assertTrue(adminAuthToken != null);
            Assert.assertTrue(adminAuthToken.length() > 10);
        }
        return adminAuthToken;
    }

    public static void setUpToAcceptAllHttpsServerCerts() {
        // Create a trust manager that does not validate certificate chains
        // without this, we need to import the server certificate into the trust store.
        // when using https as is required for Admin
        javax.net.ssl.TrustManager[] trustAllCerts = new javax.net.ssl.TrustManager[]{
                new javax.net.ssl.X509TrustManager() {
                    @Override
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() { return null; }
                    @Override
                    public void checkClientTrusted( java.security.cert.X509Certificate[] certs, String authType) { }
                    @Override
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
            testGetDomainRequest getReq = new testGetDomainRequest();
            testDomainSelector domainSel = new testDomainSelector();
            domainSel.setBy(testDomainBy.NAME);
            domainSel.setValue(domainName);
            getReq.setDomain(domainSel);
            getReq.setApplyConfig(true);
            testGetDomainResponse getResp = getAdminSvcEIF().getDomainRequest(getReq);
            if (getResp != null) {
                testDomainInfo domainInfo = getResp.getDomain();
                testDeleteDomainRequest delReq = new testDeleteDomainRequest();
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
            testGetServerRequest getSvrReq = new testGetServerRequest();
            testServerSelector svrSel = new testServerSelector();
            svrSel.setBy(testServerBy.NAME);
            svrSel.setValue(serverName);
            getSvrReq.setServer(svrSel);
            getSvrReq.setApplyConfig(true);
            testGetServerResponse getSvrResp = getAdminSvcEIF().getServerRequest(getSvrReq);
            if (getSvrResp != null) {
                testServerInfo serverInfo = getSvrResp.getServer();
                testDeleteServerRequest delReq = new testDeleteServerRequest();
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
            testGetAccountRequest getReq = new testGetAccountRequest();
            testAccountSelector accountSel = new testAccountSelector();
            accountSel.setBy(testAccountBy.NAME);
            accountSel.setValue(accountName);
            getReq.setAccount(accountSel);
            testGetAccountResponse getResp =
                    getAdminSvcEIF().getAccountRequest(getReq);
            Assert.assertNotNull(getResp);
            testAccountInfo accountInfo = getResp.getAccount();
            Assert.assertNotNull(accountInfo);
            String respId = accountInfo.getId();
            testDeleteAccountRequest delReq = new testDeleteAccountRequest();
            delReq.setId(respId);
            testDeleteAccountResponse delResp =
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
            testGetCalendarResourceRequest getReq =
                    new testGetCalendarResourceRequest();
            testCalendarResourceSelector calResourceSel =
                    new testCalendarResourceSelector();
            calResourceSel.setBy(testCalendarResourceBy.NAME);
            calResourceSel.setValue(calResourceName);
            getReq.setCalresource(calResourceSel);
            testGetCalendarResourceResponse getResp =
                getAdminSvcEIF().getCalendarResourceRequest(getReq);
            Assert.assertNotNull(getResp);
            testCalendarResourceInfo calResourceInfo = getResp.getCalresource();
            Assert.assertNotNull(calResourceInfo);
            String respId = calResourceInfo.getId();
            testDeleteCalendarResourceRequest delReq =
                    new testDeleteCalendarResourceRequest();
            delReq.setId(respId);
            testDeleteCalendarResourceResponse delResp =
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
            testGetCosRequest getReq = new testGetCosRequest();
            testCosSelector cosSel = new testCosSelector();
            cosSel.setBy(testCosBy.NAME);
            cosSel.setValue(cosName);
            getReq.setCos(cosSel);
            testGetCosResponse getResp = getAdminSvcEIF().getCosRequest(getReq);
            Assert.assertNotNull(getResp);
            testCosInfo cosInfo = getResp.getCos();
            Assert.assertNotNull(cosInfo);
            String respId = cosInfo.getId();
            testDeleteCosRequest delReq = new testDeleteCosRequest();
            delReq.setId(respId);
            testDeleteCosResponse delResp = getAdminSvcEIF().deleteCosRequest(delReq);
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
        testGetAllVolumesRequest gavReq = new testGetAllVolumesRequest();
        testGetAllVolumesResponse gavResp =
                getAdminSvcEIF().getAllVolumesRequest(gavReq);
        for (testVolumeInfo volume : gavResp.getVolume()) {
            if (name.equals(volume.getName())) {
                testDeleteVolumeRequest delReq = new testDeleteVolumeRequest();
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
        testGetDistributionListRequest getInfoReq = new testGetDistributionListRequest();
        testDistributionListSelector dlSel = new testDistributionListSelector();
        dlSel.setBy(testDistributionListBy.NAME);
        dlSel.setValue(name);
        getInfoReq.setDl(dlSel);
        try {
            testGetDistributionListResponse getResp = adminSvcEIF.getDistributionListRequest(getInfoReq);
            Assert.assertNotNull(getResp);
            testDeleteDistributionListRequest delReq = new testDeleteDistributionListRequest();
            delReq.setId(getResp.getDl().getId());
            Assert.assertNotNull("DeleteDistributionListResponse object",
                    getAdminSvcEIF().deleteDistributionListRequest(delReq));
        } catch (SOAPFaultException sfe) {
            String missive = sfe.getMessage();
            if (!missive.startsWith("no such distribution list:"))
                System.err.println("Exception " + sfe.toString() + 
                        " thrown attempting to delete dl " + name);
        }
    }

    public static String ensureDomainExists(String domainName) throws Exception {
        String domainId = null;
        Utility.addSoapAdminAuthHeader((WSBindingProvider)getAdminSvcEIF());
        testGetDomainInfoRequest getInfoReq = new testGetDomainInfoRequest();
        getInfoReq.setApplyConfig(false);
        testDomainSelector domainSel = new testDomainSelector();
        domainSel.setBy(testDomainBy.NAME);
        domainSel.setValue(domainName);
        getInfoReq.setDomain(domainSel);
        try {
            testGetDomainInfoResponse getInfoResp = adminSvcEIF.getDomainInfoRequest(getInfoReq);
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
            testCreateDomainRequest req = new testCreateDomainRequest();
            req.setName(domainName);
            Utility.addSoapAdminAuthHeader((WSBindingProvider)adminSvcEIF);
            testCreateDomainResponse resp = adminSvcEIF.createDomainRequest(req);
            Assert.assertNotNull(resp);
            testDomainInfo domainInfo = resp.getDomain();
            Assert.assertNotNull(domainInfo);
            Assert.assertEquals("createDomainResponse <domain> 'name' attribute", domainName, domainInfo.getName());
            return domainInfo.getId();
        }
    }

    public static String ensureServerExists(String serverName) throws Exception {
        Utility.addSoapAdminAuthHeader((WSBindingProvider)getAdminSvcEIF());
        testGetServerRequest getInfoReq = new testGetServerRequest();
        testServerSelector serverSel = new testServerSelector();
        serverSel.setBy(testServerBy.NAME);
        serverSel.setValue(serverName);
        getInfoReq.setServer(serverSel);
        try {
            testGetServerResponse getResp = adminSvcEIF.getServerRequest(getInfoReq);
            Assert.assertNotNull(getResp);
            return getResp.getServer().getId();
        } catch (SOAPFaultException sfe) {
            testCreateServerRequest createAcctReq = new testCreateServerRequest();
            createAcctReq.setName(serverName);
            Utility.addSoapAdminAuthHeader((WSBindingProvider)adminSvcEIF);
            testCreateServerResponse resp = adminSvcEIF.createServerRequest(createAcctReq);
            Assert.assertNotNull(resp);
            testServerInfo serverInfo = resp.getServer();
            return serverInfo.getId();
        }
    }

    public static String ensureAccountExists(String accountName)
    throws Exception {
        Utility.addSoapAdminAuthHeader((WSBindingProvider)getAdminSvcEIF());
        String domainName = accountName.substring(accountName.indexOf('@') + 1);
        ensureDomainExists(domainName);
        testGetAccountRequest getInfoReq = new testGetAccountRequest();
        testAccountSelector accountSel = new testAccountSelector();
        accountSel.setBy(testAccountBy.NAME);
        accountSel.setValue(accountName);
        getInfoReq.setAccount(accountSel);
        try {
            testGetAccountResponse getResp =
                    adminSvcEIF.getAccountRequest(getInfoReq);
            Assert.assertNotNull(getResp);
            return getResp.getAccount().getId();
        } catch (SOAPFaultException sfe) {
            testCreateAccountRequest createAcctReq = new testCreateAccountRequest();
            createAcctReq.setName(accountName);
            createAcctReq.setPassword(DEFAULT_PASS);
            Utility.addSoapAdminAuthHeader((WSBindingProvider)adminSvcEIF);
            testCreateAccountResponse resp =
                    adminSvcEIF.createAccountRequest(createAcctReq);
            Assert.assertNotNull(resp);
            testAccountInfo accountInfo = resp.getAccount();
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
        testGetCalendarResourceRequest getInfoReq =
                    new testGetCalendarResourceRequest();
        testCalendarResourceSelector calResourceSel =
                    new testCalendarResourceSelector();
        calResourceSel.setBy(testCalendarResourceBy.NAME);
        calResourceSel.setValue(calResourceName);
        getInfoReq.setCalresource(calResourceSel);
        try {
            testGetCalendarResourceResponse getResp =
                    adminSvcEIF.getCalendarResourceRequest(getInfoReq);
            Assert.assertNotNull(getResp);
            return getResp.getCalresource().getId();
        } catch (SOAPFaultException sfe) {
            testCreateCalendarResourceRequest createAcctReq =
                    new testCreateCalendarResourceRequest();
            createAcctReq.setName(calResourceName);
            createAcctReq.setPassword(DEFAULT_PASS);
            createAcctReq.getA().add(Utility.mkAttr("displayName", displayName));
            createAcctReq.getA().add(Utility.mkAttr("zimbraCalResType", "Location"));
            createAcctReq.getA().add(Utility.mkAttr(
                "zimbraCalResLocationDisplayName", "Harare"));
            Utility.addSoapAdminAuthHeader((WSBindingProvider)adminSvcEIF);
            testCreateCalendarResourceResponse resp =
                    adminSvcEIF.createCalendarResourceRequest(createAcctReq);
            Assert.assertNotNull(resp);
            testCalendarResourceInfo calResourceInfo = resp.getCalresource();
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
        getAccountServiceAuthToken(accountName, DEFAULT_PASS);
        return accountId;
    }

    public static String ensureDistributionListExists(String name) throws Exception {
        Utility.addSoapAdminAuthHeader((WSBindingProvider)getAdminSvcEIF());
        String domainName = name.substring(name.indexOf('@') + 1);
        ensureDomainExists(domainName);
        Utility.addSoapAdminAuthHeader((WSBindingProvider)getAdminSvcEIF());
        testGetDistributionListRequest getInfoReq = new testGetDistributionListRequest();
        testDistributionListSelector dlSel = new testDistributionListSelector();
        dlSel.setBy(testDistributionListBy.NAME);
        dlSel.setValue(name);
        getInfoReq.setDl(dlSel);
        try {
            testGetDistributionListResponse getResp = adminSvcEIF.getDistributionListRequest(getInfoReq);
            Assert.assertNotNull(getResp);
            return getResp.getDl().getId();
        } catch (SOAPFaultException sfe) {
            testCreateDistributionListRequest createAcctReq = new testCreateDistributionListRequest();
            createAcctReq.setName(name);
            Utility.addSoapAdminAuthHeader((WSBindingProvider)adminSvcEIF);
            testCreateDistributionListResponse resp = adminSvcEIF.createDistributionListRequest(createAcctReq);
            Assert.assertNotNull(resp);
            testDistributionListInfo dlInfo = resp.getDl();
            return dlInfo.getId();
        }
    }

    public static String ensureCosExists(String cosName) throws Exception {
        Utility.addSoapAdminAuthHeader((WSBindingProvider)getAdminSvcEIF());
        testGetCosRequest getInfoReq = new testGetCosRequest();
        testCosSelector cosSel = new testCosSelector();
        cosSel.setBy(testCosBy.NAME);
        cosSel.setValue(cosName);
        getInfoReq.setCos(cosSel);
        try {
            testGetCosResponse getResp = adminSvcEIF.getCosRequest(getInfoReq);
            Assert.assertNotNull(getResp);
            return getResp.getCos().getId();
        } catch (SOAPFaultException sfe) {
            testCreateCosRequest createAcctReq = new testCreateCosRequest();
            createAcctReq.setName(cosName);
            Utility.addSoapAdminAuthHeader((WSBindingProvider)adminSvcEIF);
            testCreateCosResponse resp = adminSvcEIF.createCosRequest(createAcctReq);
            Assert.assertNotNull(resp);
            testCosInfo cosInfo = resp.getCos();
            return cosInfo.getId();
        }
    }

    public static Short ensureVolumeExists(String name, String rootPath)
    throws Exception {
        Utility.addSoapAdminAuthHeader((WSBindingProvider)getAdminSvcEIF());
        testGetAllVolumesRequest gavReq = new testGetAllVolumesRequest();
        testGetAllVolumesResponse gavResp =
                getAdminSvcEIF().getAllVolumesRequest(gavReq);
        for (testVolumeInfo volume : gavResp.getVolume()) {
            if (name.equals(volume.getName())) {
                if (rootPath.equals(volume.getRootpath())) 
                    return volume.getId();
                deleteVolumeIfExists(name);
                break;
            }
        }
        Assert.assertTrue("Creating dir=" + rootPath +
                " for volumeName=" + name, new File(rootPath).mkdir());
        testCreateVolumeRequest req = new testCreateVolumeRequest();
        testVolumeInfo volume = new testVolumeInfo();
        volume.setName(name);
        volume.setRootpath(rootPath);
        volume.setCompressionThreshold(4096L);
        volume.setType((short)1);
        volume.setCompressBlobs(true);
        req.setVolume(volume);
        Utility.addSoapAdminAuthHeader((WSBindingProvider)getAdminSvcEIF());
        testCreateVolumeResponse resp = getAdminSvcEIF().createVolumeRequest(req);
        Assert.assertNotNull(resp);
        testVolumeInfo volumeInfo = resp.getVolume();
        Assert.assertNotNull(volumeInfo);
        Assert.assertEquals("CreateVolumeResponse <volume> 'name' attribute",
                name, volumeInfo.getName());
        Short testVolumeId = volumeInfo.getId();
        Assert.assertTrue(
                "CreateVolumeResponse <volume> 'id' attribute " +
                testVolumeId + " - should be at least 1", testVolumeId >= 1);
        return testVolumeId;
    }

    public static testAttr mkAttr(String name, String value) {
        testAttr attr = new testAttr();
        attr.setN(name);
        attr.setValue(value);
        return attr;
    }
}
