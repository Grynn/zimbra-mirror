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
package com.zimbra.soap.admin;

import generated.zcsclient.admin.*;
import generated.zcsclient.admin.testDomainAdminRight.Rights;
import generated.zcsclient.admin.testEffectiveAttrInfo.Default;
import generated.zcsclient.admin.testGetRightsDocResponse.DomainAdminCopypasteToZimbraRightsDomainadminXmlTemplate;
import generated.zcsclient.ws.service.ZcsAdminPortType;
import generated.zcsclient.zm.testGranteeType;
import generated.zcsclient.zm.testNamedElement;
import generated.zcsclient.zm.testTargetBy;
import generated.zcsclient.zm.testTargetType;

import java.util.List;

import javax.xml.ws.soap.SOAPFaultException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Element;

import com.sun.xml.ws.developer.WSBindingProvider;
import com.zimbra.soap.Utility;

public class WSDLAdminTest {

    // The AdminService interface is the Java type bound to
    // the portType section of the WSDL document.
    private final static String testDomain = "wsdl.domain.example.test";
    private final static String testAcctDomain = "wsdl.acct.domain.example.test";
    private final static String testAcct = "wsdl1@" + testAcctDomain;
    private final static String testServer = "wsdl.server.example.test";
    private final static String testCos = "wsdl.cos.example.test";
    private final static String testCosCopy = "wsdl.cos.copy.example.test";
    private static ZcsAdminPortType eif = null;

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
        testPingRequest req = new testPingRequest();
        testPingResponse resp = eif.pingRequest(req);
        Assert.assertNotNull("PingResponse object", resp);
    }

    @Test
    public void noopTest() throws Exception {
        Utility.addSoapAdminAuthHeader((WSBindingProvider)eif);
        testNoOpRequest req = new testNoOpRequest();
        testNoOpResponse resp = eif.noOpRequest(req);
        Assert.assertNotNull("NoOpResponse object", resp);
    }

    @Test
    public void versionInfoTest() throws Exception {
        Utility.addSoapAdminAuthHeader((WSBindingProvider)eif);
        testGetVersionInfoRequest req = new testGetVersionInfoRequest();
        testGetVersionInfoResponse resp = eif.getVersionInfoRequest(req);
        Assert.assertNotNull("GetVersionInfoResponse object", resp);
        testVersionInfo info = resp.getInfo();
        Assert.assertNotNull("GetVersionInfoResponse <info> object", info);
        info.getType();  // Don't care whether null or not
        Assert.assertNotNull("getVersion result", info.getVersion());
        Assert.assertNotNull("getRelease result", info.getRelease());
        Assert.assertNotNull("getBuildDate result", info.getBuildDate());
        Assert.assertNotNull("getHost result", info.getHost());
        Assert.assertNotNull("getMajorVersion result", info.getMajorversion());
        Assert.assertNotNull("getMinorVersion result", info.getMinorversion());
        Assert.assertNotNull("getMicroVersion result", info.getMicroversion());
        Assert.assertNotNull("getPlatform result", info.getPlatform());
    }

    @Test
    public void licenseInfoTest() throws Exception {
        Utility.addSoapAdminAuthHeader((WSBindingProvider)eif);
        testGetLicenseInfoRequest req = new testGetLicenseInfoRequest();
        testGetLicenseInfoResponse resp = eif.getLicenseInfoRequest(req);
        Assert.assertNotNull("GetLicenseInfoResponse object", resp);
        testLicenseExpirationInfo info = resp.getExpiration();
        Assert.assertNotNull("GetLicenseInfoResponse <info> object", info);
        Assert.assertNotNull("getDate result", info.getDate());
    }

    @Test
    public void getServiceStatusTest() throws Exception {
        Utility.addSoapAdminAuthHeader((WSBindingProvider)eif);
        testGetServiceStatusRequest req = new testGetServiceStatusRequest();
        testGetServiceStatusResponse resp = eif.getServiceStatusRequest(req);
        Assert.assertNotNull("GetServiceStatusResponse object", resp);
        testTimeZoneInfo tz = resp.getTimezone();
        Assert.assertNotNull("GetServiceStatusResponse <timezone> object", tz);
        Assert.assertNotNull("GetServiceStatusResponse <timezone> displayName",
                tz.getDisplayName());
        Assert.assertNotNull("GetServiceStatusResponse <timezone> id",
                tz.getId());
        List <testServiceStatus> statuses = resp.getStatus();
        // TODO: Would be nice to test with some real statuses - looks like
        //       need logger service installed and enabled
        Assert.assertNotNull("GetServiceStatusResponse statuses list object",
                statuses);
    }

    @Test
    public void checkHealthTest() throws Exception {
        Utility.addSoapAdminAuthHeader((WSBindingProvider)eif);
        testCheckHealthRequest req = new testCheckHealthRequest();
        testCheckHealthResponse resp = eif.checkHealthRequest(req);
        Assert.assertNotNull("CheckHealthResponse object", resp);
        Assert.assertTrue("isHealthy",resp.isHealthy());
    }

    @Test
    public void checkHostnameResolveTest() throws Exception {
        Utility.addSoapAdminAuthHeader((WSBindingProvider)eif);
        testCheckHostnameResolveRequest req = new testCheckHostnameResolveRequest();
        req.setHostname("nonexist.example.com");
        testCheckHostnameResolveResponse resp = eif.checkHostnameResolveRequest(req);
        Assert.assertNotNull("CheckHostnameResolveResponse object", resp);
        Assert.assertEquals("CheckHostnameResolveResponse code",
                "check.UNKNOWN_HOST", resp.getCode());
        Assert.assertEquals("CheckHostnameResolveResponse start of message",
                "java.net.UnknownHostException",
                resp.getMessage().substring(0, 29));
        req.setHostname("www.zimbra.com");
        resp = eif.checkHostnameResolveRequest(req);
        Assert.assertNotNull("CheckHostnameResolveResponse object", resp);
        Assert.assertEquals("CheckHostnameResolveResponse code",
                "check.OK", resp.getCode());
        Assert.assertEquals("CheckHostnameResolveResponse message",
                "", resp.getMessage());
    }

    // TODO Work out how to test this.  @Test
    public void checkGalConfig() throws Exception {
        /*
        Utility.addSoapAdminAuthHeader((WSBindingProvider)eif);
        CheckGalConfigRequest req = new CheckGalConfigRequest();
        req.setAction("search");
        LimitedQuery query = new LimitedQuery();
        query.setLimit(12L);
        query.setValue("cn=*");
        Attr attr;
        attr = new Attr(); attr.setN("zimbraGalMode"); attr.setValue("ldap");
        req.getA().add(attr);
        attr = new Attr(); attr.setN("zimbraAuthMech"); attr.setValue("ldap");
        req.getA().add(attr);
        CheckGalConfigResponse resp = eif.checkGalConfigRequest(req);
        resp.getCode();
        resp.getMessage();
        resp.getCn();
        */
    }

    @Test
    public void checkDomainMXRecordTest() throws Exception {
        Utility.ensureDomainExists("zimbra.com");
        Utility.addSoapAdminAuthHeader((WSBindingProvider)eif);
        testCheckDomainMXRecordRequest req = new testCheckDomainMXRecordRequest();
        testDomainSelector domainSel = new testDomainSelector();
        domainSel.setBy(testDomainBy.NAME);
        domainSel.setValue("zimbra.com");
        req.setDomain(domainSel);
        testCheckDomainMXRecordResponse resp = eif.checkDomainMXRecordRequest(req);
        Assert.assertNotNull("CheckDomainMXRecordResponse object", resp);
        List <String> entries = resp.getEntry();
        int len = entries.size();
        Assert.assertNotNull("CheckDomainMXRecordResponse entries object", entries);
        Assert.assertTrue("Number of <entry> children is " +
                len + " - should be at least 1", len >= 1);
        /* unlikely that we will have good config for this... */
        Assert.assertEquals("CheckDomainMXRecordResponse code",
                "Failed", resp.getCode());
        Assert.assertEquals("CheckDomainMXRecordResponse start of message",
                "Domain is configured to use SMTP host:",
                resp.getMessage().substring(0, 38));
    }

    @Test
    public void reloadLocalConfigTest() throws Exception {
        Utility.addSoapAdminAuthHeader((WSBindingProvider)eif);
        testReloadLocalConfigRequest req = new testReloadLocalConfigRequest();
        testReloadLocalConfigResponse resp = eif.reloadLocalConfigRequest(req);
        Assert.assertNotNull("ReloadLocalConfigResponse object", resp);
    }

    // This WAS failing but appears to be working now.
    // Notes from previous failure:
    // re-enable when later version of Metro is in use.
    // Currently failing with a NullPointerException at :
    // at com.sun.xml.stream.buffer.AbstractProcessor.readFromNextStructure(AbstractProcessor.java:189)
    // at com.sun.xml.stream.buffer.AbstractProcessor.readStructure(AbstractProcessor.java:163)
    // at com.sun.xml.stream.buffer.AbstractProcessor.readEiiState(AbstractProcessor.java:167)
    // at com.sun.xml.stream.buffer.stax.StreamReaderBufferProcessor.next(StreamReaderBufferProcessor.java:220)
    // at com.sun.xml.ws.streaming.XMLStreamReaderUtil.readRest(XMLStreamReaderUtil.java:67)
    // at com.sun.xml.ws.message.stream.StreamMessage.readPayloadAsJAXB(StreamMessage.java:262)
    // at com.sun.xml.ws.client.sei.ResponseBuilder$Body.readResponse(ResponseBuilder.java:469)
    // at com.sun.xml.ws.client.sei.SyncMethodHandler.invoke(SyncMethodHandler.java:121)
    // at com.sun.xml.ws.client.sei.SyncMethodHandler.invoke(SyncMethodHandler.java:89)
    // at com.sun.xml.ws.client.sei.SEIStub.invoke(SEIStub.java:118)
    // at $Proxy112.getAllConfigRequest(Unknown Source)
    //
    // Believe this is a bug in Metro 1.5 documented at
    //     http://java.net/jira/browse/JAX_WS-807
    // Can't upgrade to newer versions of Metro because they require JAX-WS 2.2
    // and JDK6 only comes with JAX-WS 2.1.  It is possible to work around that
    // but would require jumping through hoops.
    // See GetAllConfigTest - the same Response Xml seems to work fine with
    // simple JAXB unmarshalling.
    @Test
    public void getAllConfigTest() throws Exception {
        testGetAllConfigRequest req = new testGetAllConfigRequest();
        Utility.addSoapAdminAuthHeader((WSBindingProvider)eif);
        testGetAllConfigResponse resp = eif.getAllConfigRequest(req);
        Assert.assertNotNull("GetAllConfigResponse object", resp);
        List <testAttr> attrs = resp.getA();
        Assert.assertNotNull("GetAllConfigResponse list of attrs", attrs);
        int len = attrs.size();
        Assert.assertTrue("Number of GetAllConfigResponse <a> children is " +
                len + " - should be at least 2", len >= 2);
    }

    @Test
    public void getConfigTest() throws Exception {
        Utility.addSoapAdminAuthHeader((WSBindingProvider)eif);
        testGetConfigRequest req = new testGetConfigRequest();
        testAttr attr = new testAttr();
        attr.setN("zimbraSpamHeader");
        req.setA(attr);
        testGetConfigResponse resp = eif.getConfigRequest(req);
        Assert.assertNotNull("GetConfigResponse object", resp);
        List <testAttr> attrs = resp.getA();
        Assert.assertNotNull("GetConfigResponse list of attrs", attrs);
        int len = attrs.size();
        Assert.assertEquals("Number of GetConfigResponse <a> children" , 1, len);
        testAttr respAttr =attrs.get(0);
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
        testModifyConfigRequest req = new testModifyConfigRequest();
        testAttr attr = new testAttr();
        attr.setN("zimbraSpamHeader");
        attr.setValue("X-NewSpam-Flag");
        req.getA().add(attr);
        testModifyConfigResponse resp = eif.modifyConfigRequest(req);
        Assert.assertNotNull("modifyConfigResponse object", resp);
        req = new testModifyConfigRequest();
        attr.setValue("X-Spam-Flag");
        req.getA().add(attr);
        resp = eif.modifyConfigRequest(req);
        Assert.assertNotNull("modifyConfigResponse object", resp);
    }

    @Test
    public void getAllLocalesTest() throws Exception {
        Utility.addSoapAdminAuthHeader((WSBindingProvider)eif);
        testGetAllLocalesRequest req = new testGetAllLocalesRequest();
        testGetAllLocalesResponse resp = eif.getAllLocalesRequest(req);
        Assert.assertNotNull("GetAllLocalesResponse object", resp);
        List <testLocaleInfo> locales = resp.getLocale();
        int len = locales.size();
        Assert.assertTrue("number of <locales> is " + len +
                " - should be longer than 10", len > 10);
    }

    @Test
    public void getMailboxStatsTest() throws Exception {
        Utility.addSoapAdminAuthHeader((WSBindingProvider)eif);
        testGetMailboxStatsRequest req = new testGetMailboxStatsRequest();
        testGetMailboxStatsResponse resp = eif.getMailboxStatsRequest(req);
        Assert.assertNotNull("GetMailboxStatsResponse object", resp);
        testMailboxStats mboxStats = resp.getStats();
        Assert.assertNotNull("stats object", mboxStats);
        long numMboxes = mboxStats.getNumMboxes();
        long totalSize = mboxStats.getTotalSize();
        Assert.assertTrue("numMboxes " + numMboxes + " should be >=1", numMboxes >=1);
        Assert.assertTrue("totalSize " + totalSize + " should be >=1000", totalSize >=1000);
    }

    @Test
    public void flushCacheTest() throws Exception {
        Utility.addSoapAdminAuthHeader((WSBindingProvider)eif);
        testCacheSelector sel = new testCacheSelector();
        sel.setAllServers(true);
        sel.setType(testCacheEntryType.DOMAIN.value());
        testFlushCacheRequest req = new testFlushCacheRequest();
        req.setCache(sel);
        testFlushCacheResponse resp = eif.flushCacheRequest(req);
        Assert.assertNotNull("FlushCacheResponse object", resp);
    }

    @Test
    public void checkPasswordStrengthTest() throws Exception {
        String testAccountId = Utility.ensureAccountExists(testAcct);
        Utility.addSoapAdminAuthHeader((WSBindingProvider)eif);
        testCheckPasswordStrengthRequest req = new testCheckPasswordStrengthRequest();
        req.setId(testAccountId);
        req.setPassword("fq$34apgGog11");
        testCheckPasswordStrengthResponse resp = eif.checkPasswordStrengthRequest(req);
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
        testSetPasswordRequest req = new testSetPasswordRequest();
        req.setId(testAccountId);
        req.setNewPassword("fq$34apgGog11");
        testSetPasswordResponse resp = eif.setPasswordRequest(req);
        Assert.assertNotNull("SetPasswordResponse object", resp);
    }

    @Test
    public void searchDirectoryTest() throws Exception {
        Utility.ensureAccountExists(testAcct);
        Utility.addSoapAdminAuthHeader((WSBindingProvider)eif);
        testSearchDirectoryRequest req = new testSearchDirectoryRequest();
        req.setDomain(testAcctDomain);
        req.setApplyCos(true);
        req.setApplyConfig(true);
        req.setLimit(456);
        req.setMaxResults(444);
        req.setQuery("cn=*");
        testSearchDirectoryResponse resp = eif.searchDirectoryRequest(req);
        Assert.assertNotNull("SearchDirectoryResponse object", resp);
        Long searchTotal = resp.getSearchTotal();
        Assert.assertTrue("searchTotal " + searchTotal + " should be at least 1",
                searchTotal >=1);
        Assert.assertFalse("value for attribute 'more'", resp.isMore());
        List <testAdminObjectInfo> entries = resp.getCalresourceOrDlOrAlias();
        Assert.assertTrue("number of entries in response [" + searchTotal + "] should be at least 1",
                entries.size() >= 1);
    }

    private void validateRightInfo(testRightInfo rInfo, String riTag) {
        testRightsAttrs rAttrs = rInfo.getAttrs();
        if (null != rAttrs) {
            int aNum = 0;
            for (testAttr attr: rAttrs.getA()) {
                aNum++;
                String aTag = riTag + " a " + aNum;
                Assert.assertNotNull(aTag + " name", attr.getN());
                Assert.assertNotNull(aTag + " value", attr.getValue());
            }
            int eNum = 0;
            for (Element elem : rAttrs.getAny()) {
                eNum++;
                String eTag = riTag + " element " + aNum;
                Assert.assertNotNull(eTag + " name", elem.getNodeName());
            }
        }
        Assert.assertNotNull("RightInfo name", rInfo.getName());
        Assert.assertNotNull("RightInfo desc", rInfo.getDesc());
        Assert.assertNotNull("RightInfo class", rInfo.getRightClass());
        Assert.assertNotNull("RightInfo type", rInfo.getType());
        // Assert.assertNotNull("RightInfo targetType", rInfo.getTargetType());
        testComboRights comboRights = rInfo.getRights();
        if (null != comboRights) {
            for (testComboRightInfo cri : comboRights.getR()) {
                Assert.assertNotNull("ComboRightInfo name", cri.getN());
                Assert.assertNotNull("ComboRightInfo type", cri.getType());
                // Assert.assertNotNull("ComboRightInfo targetType",
                //         cri.getTargetType());
            }
        }
    }

    @Test
    public void getAllRightsTest() throws Exception {
        Utility.ensureAccountExists(testAcct);
        ZcsAdminPortType nvEif = Utility.getNonValidatingAdminSvcEIF();
        // the validator does not like the @XmlAnyElement used
        // in RightsAttrs
        Utility.addSoapAdminAuthHeader((WSBindingProvider)nvEif);
        testGetAllRightsRequest req = new testGetAllRightsRequest();
        testGetAllRightsResponse resp = nvEif.getAllRightsRequest(req);
        Assert.assertNotNull("GetAllRightsResponse object", resp);
        List <testRightInfo> rInfos = resp.getRight();
        Assert.assertNotNull("GetAllRightsResponse object", rInfos);
        int riNum = 0;
        for (testRightInfo rInfo : rInfos) {
            riNum++;
            String riTag = "RightInfo " + riNum;
            validateRightInfo(rInfo, riTag);
        }
    }

    @Test
    public void getRightTest() throws Exception {
        Utility.ensureAccountExists(testAcct);
        Utility.addSoapAdminAuthHeader((WSBindingProvider)eif);
        testGetRightRequest req = new testGetRightRequest();
        req.setExpandAllAttrs(true);
        req.setRight("adminConsoleAccountRights");
        testGetRightResponse resp = eif.getRightRequest(req);
        Assert.assertNotNull("GetRightResponse object", resp);
        testRightInfo rInfo = resp.getRight();
        Assert.assertNotNull("GetRightResponse RightInfo", rInfo);
        validateRightInfo(rInfo, "RightInfo");
    }

    @Test
    public void getRightsDocTest() throws Exception {
        Utility.ensureAccountExists(testAcct);
        Utility.addSoapAdminAuthHeader((WSBindingProvider)eif);
        testGetRightsDocRequest req = new testGetRightsDocRequest();
        testGetRightsDocResponse resp = eif.getRightsDocRequest(req);
        Assert.assertNotNull("GetRightsDocResponse object", resp);

        List<testPackageRightsInfo> pkgRights = resp.getPackage();
        int pkgNum = 0;
        for (testPackageRightsInfo pkg : pkgRights) {
            pkgNum++;
            String pkgTag = "Package " + pkgNum;
            Assert.assertNotNull(pkgTag + " name", pkg.getName());
            List<testCmdRightsInfo> cmdRights = pkg.getCmd();
            Assert.assertNotNull(pkgTag + " Cmd list", cmdRights);
            int cmdNum = 0;
            for (testCmdRightsInfo cmd : cmdRights) {
                cmdNum++;
                String cmdTag = pkgTag + " Cmd " + cmdNum;
                Assert.assertNotNull(cmdTag + " description",
                        cmd.getDesc());
                Assert.assertNotNull(cmdTag + " name", cmd.getName());
                testCmdRightsInfo.Rights rInfo =
                    cmd.getRights();
                List<testNamedElement> rNs = rInfo.getRight();
                int rNum = 0;
                for (testNamedElement rn : rNs) {
                    rNum++;
                    String rTag = cmdTag + " RightName " + rNum;
                    Assert.assertNotNull(rTag, rn.getName());
                }
            }
        }

        Assert.assertNotNull("notUsed list", resp.getNotUsed());
        DomainAdminCopypasteToZimbraRightsDomainadminXmlTemplate domRights =
            resp.getDomainAdminCopypasteToZimbraRightsDomainadminXmlTemplate();
        String tag =
            "domainAdmin-copypaste-to-zimbra-rights-domainadmin-xml-template";
        List<testDomainAdminRight> rights = domRights.getRight();
        Assert.assertNotNull(tag + " rights list", rights);
        int domNum = 0;
        for (testDomainAdminRight dar : rights) {
            domNum++;
            String domTag = tag + " right " + domNum;
            Assert.assertNotNull(domTag + " name", dar.getName());
            Assert.assertNotNull(domTag + " type", dar.getType());
            Assert.assertNotNull(domTag + " description", dar.getDesc());
            Rights subRights = dar.getRights();
            Assert.assertNotNull(domTag + " rights", subRights);
            int rnNum = 0;
            for (testRightWithName rWithName : subRights.getR()) {
                rnNum++;
                String rnTag = domTag + " right " + rnNum;
                Assert.assertNotNull(rnTag + " n attrib", rWithName.getN());
            }
        }
    }

    private void checkEffectiveAttrsInfo(testEffectiveAttrsInfo attrInfo,
            String tag) {
        Assert.assertNotNull(tag, attrInfo);
        Assert.assertTrue(tag + " all setting", attrInfo.isAll());
        List <testEffectiveAttrInfo> attrs = attrInfo.getA();
        Assert.assertNotNull(tag + " attrs", attrs);
        int attNum = 0;
        for (testEffectiveAttrInfo anAttr : attrs) {
            attNum++;
            String attrTag = tag + " attr " + attNum;
            Assert.assertNotNull(attrTag, anAttr);
            Assert.assertNotNull(attrTag + " n", anAttr.getN());
            testConstraintInfo constraint = anAttr.getConstraint();
            if (constraint != null) {
                Assert.assertNotNull(attrTag + " constraint",
                        constraint.getValues());
            }
            Default def = anAttr.getDefault();
            if (def != null) {
                List<String> values = def.getV();
                Assert.assertNotNull(attrTag + " default", values);
            }
        }
    }

    private void checkAllEffectiveRights(testEffectiveRightsInfo allEffectiveRights,
            String tag) {
        Assert.assertNotNull("allEffectiveRights", allEffectiveRights);
        checkEffectiveAttrsInfo(allEffectiveRights.getGetAttrs(),
                tag + " getAttrs");
        checkEffectiveAttrsInfo(allEffectiveRights.getSetAttrs(),
                tag + " setAttrs");
        List <testRightWithName> rights = allEffectiveRights.getRight();
        Assert.assertNotNull("rights", rights);
        int rNum = 0;
        for (testRightWithName aRight : rights) {
            rNum++;
            String rTag = tag + " right " + rNum;
            Assert.assertNotNull(rTag + " name", aRight.getN());
        }
    }

    @Test
    public void getAllEffectiveRightsTest() throws Exception {
        Utility.ensureAccountExists(testAcct);
        Utility.addSoapAdminAuthHeader((WSBindingProvider)eif);
        testGetAllEffectiveRightsRequest req = new testGetAllEffectiveRightsRequest();
        req.setExpandAllAttrs("setAttrs, getAttrs");
        testGetAllEffectiveRightsResponse resp = eif.getAllEffectiveRightsRequest(req);
        Assert.assertNotNull("GetAllEffectiveRightsResponse object", resp);
        testGranteeInfo grantee = resp.getGrantee();
        Assert.assertNotNull("grantee object", grantee);
        Assert.assertNotNull("grantee id", grantee.getId());
        Assert.assertNotNull("grantee name", grantee.getName());
        Assert.assertNotNull("grantee type", grantee.getType());
        List<testEffectiveRightsTarget> targets = resp.getTarget();
        Assert.assertNotNull("list of targets", targets);
        int targNum = 0;
        for ( testEffectiveRightsTarget target : targets) {
            targNum++;
            String targTag = "target " + targNum;
            Assert.assertNotNull(targTag, target);
            Assert.assertNotNull(targTag + " type", target.getType());
            checkAllEffectiveRights(target.getAll(), targTag + " all");

            List <testInDomainInfo> inDomsList = target.getInDomains();
            Assert.assertNotNull("InDomains list", inDomsList);
            int inDomNum = 0;
            for (testInDomainInfo anInDom : inDomsList) {
                inDomNum++;
                String inDomTag = targTag + " inDomain " + inDomNum;
                int domNum = 0;
                for (testNamedElement dom : anInDom.getDomain()) {
                    domNum++;
                    String domTag = inDomTag + " domain " + domNum;
                    Assert.assertNotNull(domTag + " name", dom.getName());
                }
                checkAllEffectiveRights(anInDom.getRights(),
                        inDomTag + " rights");
            }
            List <testRightsEntriesInfo> entries = target.getEntries();
            int entNum = 0;
            for (testRightsEntriesInfo entriesInfo : entries) {
                entNum++;
                String entTag = targTag + " RightsEntries " + entNum;
                Assert.assertNotNull(entTag, entriesInfo);
                int entryNum = 0;
                for (testNamedElement namedEntry : entriesInfo.getEntry()) {
                    entryNum++;
                    String entryTag = entTag + " entry " + entNum;
                    Assert.assertNotNull(entryTag + " name", namedEntry.getName());
                }
                testEffectiveRightsInfo entriesInfoRights = entriesInfo.getRights();
                Assert.assertNotNull("entriesInfoRights", entriesInfoRights);
            }
        }
    }

    // TODO: Figure out how to test GrantRight/GetGrants
    //       Looks like can only assign a user right to a regular user
    //       The GetAllRightsResponse I've seen only talks about
    //       various rights with rightClass="ADMIN"
    // Currently fails as regards grantee as invalid.
    //     RightBearer.isValidGranteeForAdminRights(mGranteeType, grantee))
    // is returning false - probably needs to be in an admin group
    // or to be a delegated admin account
    // @Test
    public void grantsTestDISABLED() throws Exception {
        String accountId = Utility.ensureAccountExists(testAcct);
        Utility.addSoapAdminAuthHeader((WSBindingProvider)eif);

        testGranteeSelector gSel = new testGranteeSelector();
        gSel.setBy(testGranteeBy.NAME); // required
        // note - initial testing idea was to use "admin" account
        // but GetGrantsRequest won't accept an admin account
        // as the grantee - they don't need to be granted anything...
        gSel.setValue(testAcct);
        gSel.setType(testGranteeType.USR);
        gSel.setAll(true);  // required
        // password is not allowed for grantee type usr
        // gSel.setSecret("test123");

        testEffectiveRightsTargetSelector tSel = new testEffectiveRightsTargetSelector();
        tSel.setType(testTargetType.ACCOUNT);
        tSel.setBy(testTargetBy.ID);
        tSel.setValue(accountId);

        testRightModifierInfo rmi = new testRightModifierInfo();
        // Note: if CanDelegate modifier set, target cannot be a regular user
        //       acct.
        // rmi.setCanDelegate(true);
        // rmi.setValue("addAccountAlias");
        rmi.setValue("viewFreeBusy");
        rmi.setSubDomain(false);

        testGrantRightRequest grReq = new testGrantRightRequest();
        grReq.setGrantee(gSel);
        grReq.setTarget(tSel);
        grReq.setRight(rmi);

        testGrantRightResponse grResp = eif.grantRightRequest(grReq);
        Assert.assertNotNull("GrantRightResponse object", grResp);

        testGetGrantsRequest ggReq = new testGetGrantsRequest();
        ggReq.setGrantee(gSel);
        ggReq.setTarget(tSel);
        testGetGrantsResponse ggResp = eif.getGrantsRequest(ggReq);
        Assert.assertNotNull("GetGrantsResponse object", ggResp);
        Assert.assertTrue("Number of grants >= 1", ggResp.getGrant().size() >=1);
        int gNum = 0;
        for (testGrantInfo grant : ggResp.getGrant()) {
            gNum++;
            String gTag = " grant " + gNum;
            testGranteeInfo grantee = grant.getGrantee();
            Assert.assertNotNull(gTag + " GranteeInfo", grantee);
            Assert.assertNotNull(gTag + " Grantee type", grantee.getType());
            Assert.assertNotNull(gTag + " Grantee id", grantee.getId());
            Assert.assertNotNull(gTag + " Grantee name", grantee.getName());
            testTypeIdName targ = grant.getTarget();
            Assert.assertNotNull(gTag + " Target", targ);
            Assert.assertNotNull(gTag + " Target type", targ.getType());
            Assert.assertNotNull(gTag + " Target id", targ.getId());
            Assert.assertNotNull(gTag + " Target name", targ.getName());
            testRightModifierInfo rightMod = grant.getRight();
            Assert.assertNotNull(gTag + " Right", rightMod);
            Assert.assertNotNull(gTag + " Right value", rightMod.getValue());
        }
    }

    @Test
    public void getEffectiveRightsTest() throws Exception {
        String accountId = Utility.ensureAccountExists(testAcct);
        Utility.addSoapAdminAuthHeader((WSBindingProvider)eif);
        testGetEffectiveRightsRequest req = new testGetEffectiveRightsRequest();
        req.setExpandAllAttrs("getAttrs");
        testGranteeSelector gSel = new testGranteeSelector();
        gSel.setBy(testGranteeBy.NAME);
        gSel.setValue("admin");
        req.setGrantee(gSel);
        testEffectiveRightsTargetSelector tSel = new testEffectiveRightsTargetSelector();
        tSel.setType(testTargetType.ACCOUNT);
        tSel.setBy(testTargetBy.ID);
        tSel.setValue(accountId);
        req.setTarget(tSel);
        testGetEffectiveRightsResponse resp = eif.getEffectiveRightsRequest(req);
        Assert.assertNotNull("GetEffectiveRightsResponse object", resp);
        testGranteeInfo grantee = resp.getGrantee();
        Assert.assertNotNull("grantee object", grantee);
        Assert.assertNotNull("grantee id", grantee.getId());
        Assert.assertNotNull("grantee name", grantee.getName());
        // Not present for GetEffectiveRights (is for GetAllEffectiveRights)
        // Assert.assertNotNull("grantee type", grantee.getType());
    }

    @Test
    public void checkRightTest() throws Exception {
        String accountId = Utility.ensureAccountExists(testAcct);
        Utility.addSoapAdminAuthHeader((WSBindingProvider)eif);
        testCheckRightRequest req = new testCheckRightRequest();
        testGranteeSelector gSel = new testGranteeSelector();
        gSel.setBy(testGranteeBy.NAME);
        gSel.setValue("admin");
        req.setGrantee(gSel);
        testEffectiveRightsTargetSelector tSel = new testEffectiveRightsTargetSelector();
        tSel.setType(testTargetType.ACCOUNT);
        tSel.setBy(testTargetBy.ID);
        tSel.setValue(accountId);
        req.setTarget(tSel);
        testCheckedRight checkedRight = new testCheckedRight();
        // from /opt/zimbra/conf/rights/zimbra-rights.xml
        checkedRight.setValue("renameAccount");
        req.setRight(checkedRight);
        testCheckRightResponse resp = eif.checkRightRequest(req);
        Assert.assertNotNull("CheckRightResponse object", resp);
        resp.isAllow();
        resp.getVia();  // will be null
    }

    @Test
    public void createDomainTest() throws Exception {
        Utility.deleteDomainIfExists(testDomain);
        testCreateDomainRequest req = new testCreateDomainRequest();
        req.setName(testDomain);
        Utility.addSoapAdminAuthHeader((WSBindingProvider)eif);
        testCreateDomainResponse resp = eif.createDomainRequest(req);
        Assert.assertNotNull(resp);
        testDomainInfo domainInfo = resp.getDomain();
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
        testDomainInfo domainInfo;
        String testDomainId = Utility.ensureDomainExists(testDomain);
        testGetDomainInfoRequest getInfoReq = new testGetDomainInfoRequest();
        getInfoReq.setApplyConfig(true);
        testDomainSelector domainSel = new testDomainSelector();
        domainSel.setBy(testDomainBy.ID);
        domainSel.setValue(testDomainId);
        getInfoReq.setDomain(domainSel);
        testGetDomainInfoResponse getInfoResp = eif.getDomainInfoRequest(getInfoReq);
        Assert.assertNotNull(getInfoResp);
        domainInfo = getInfoResp.getDomain();
        Assert.assertNotNull(domainInfo);
        Assert.assertEquals("getDomainInfoResponse <domain> 'name' attribute",
                testDomain, domainInfo.getName());
        String respId = domainInfo.getId();
        Assert.assertEquals("getDomainInfoResponse <domain> 'id' attribute",
                testDomainId, respId);
        len = domainInfo.getA().size();
        // September 2011.  Started seeing:
        //    <domain id="globalconfig-dummy-id" name="globalconfig">
        //    <a n="zimbraZimletDataSensitiveInMixedModeDisabled">TRUE</a>
        //    </domain>
        // Used to be no <a> children.  Might be a mistake and this would go back to zero?
        Assert.assertTrue( "number of <a> children of GetDomainInfoResponse <domain> =" + len +
                " expecting at least 1", (len >= 1));
    }

    @Test
    public void getDomainTest() throws Exception {
        int len;
        testDomainInfo domainInfo;
        String testDomainId = Utility.ensureDomainExists(testDomain);
        testGetDomainRequest getReq = new testGetDomainRequest();
        getReq.setApplyConfig(true);
        testDomainSelector domainSel = new testDomainSelector();
        domainSel.setBy(testDomainBy.ID);
        domainSel.setValue(testDomainId);
        getReq.setDomain(domainSel);
        testGetDomainResponse getResp = eif.getDomainRequest(getReq);
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
        testDomainInfo domainInfo;
        String testDomainId = Utility.ensureDomainExists(testDomain);
        testModifyDomainRequest modReq = new testModifyDomainRequest();
        modReq.setId(testDomainId);
        testAttr modAttr = new testAttr();
        modAttr.setN("zimbraGalMaxResults");
        modAttr.setValue("99");
        modReq.getA().add(modAttr);
        testModifyDomainResponse modResp = eif.modifyDomainRequest(modReq);
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

        testGetDomainRequest getReq = new testGetDomainRequest();
        getReq.setApplyConfig(true);
        testDomainSelector domainSel = new testDomainSelector();
        domainSel.setBy(testDomainBy.ID);
        domainSel.setValue(testDomainId);
        getReq.setDomain(domainSel);
        getReq.setAttrs("zimbraMailStatus,zimbraBasicAuthRealm");
        testGetDomainResponse getResp = eif.getDomainRequest(getReq);
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
        testGetAllDomainsRequest req = new testGetAllDomainsRequest();
        Utility.addSoapAdminAuthHeader((WSBindingProvider)eif);
        testGetAllDomainsResponse resp = eif.getAllDomainsRequest(req);
        Assert.assertNotNull("GetAllDomainsResponse object", resp);
        List <testDomainInfo> domainInfoList = resp.getDomain();
        int len;
        Assert.assertNotNull("GetAllDomainsResponse list of domains", domainInfoList);
        len = domainInfoList.size();
        Assert.assertTrue("Number of GetAllDomainsResponse <domain> children is " + len +
                " - should be at least 1", len >= 1);
    }

    @Test
    public void deleteDomainTest() throws Exception {
        String testDomainId = Utility.ensureDomainExists(testDomain);
        testDeleteDomainRequest delReq = new testDeleteDomainRequest();
        delReq.setId(testDomainId);
        testDeleteDomainResponse delResp = eif.deleteDomainRequest(delReq);
        Assert.assertNotNull(delResp);
    }

    @Test
    public void createServerTest() throws Exception {
        int len;
        Utility.deleteServerIfExists(testServer);
        testCreateServerRequest req = new testCreateServerRequest();
        req.setName(testServer);
        Utility.addSoapAdminAuthHeader((WSBindingProvider)eif);
        testCreateServerResponse resp = eif.createServerRequest(req);
        Assert.assertNotNull(resp);
        testServerInfo serverInfo = resp.getServer();
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
        testServerSelector serverSel;
        testServerInfo serverInfo;
        String respId;
        String testServerId = Utility.ensureServerExists(testServer);
        testGetServerRequest getReq = new testGetServerRequest();
        getReq.setApplyConfig(true);
        serverSel = new testServerSelector();
        serverSel.setBy(testServerBy.ID);
        serverSel.setValue(testServerId);
        getReq.setServer(serverSel);
        testGetServerResponse getResp = eif.getServerRequest(getReq);
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
        testServerSelector serverSel;
        testServerInfo serverInfo;
        String respId;
        String testServerId = Utility.ensureServerExists(testServer);
        testModifyServerRequest modReq = new testModifyServerRequest();
        modReq.setId(testServerId);
        testAttr modAttr = new testAttr();
        modAttr.setN("zimbraImapNumThreads");
        modAttr.setValue("199");
        modReq.getA().add(modAttr);
        testModifyServerResponse modResp = eif.modifyServerRequest(modReq);
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

        testGetServerRequest getReq = new testGetServerRequest();
        getReq.setApplyConfig(true);
        serverSel = new testServerSelector();
        serverSel.setBy(testServerBy.ID);
        serverSel.setValue(testServerId);
        getReq.setServer(serverSel);
        getReq.setAttrs("zimbraImapNumThreads,zimbraServiceHostname");
        testGetServerResponse getResp = eif.getServerRequest(getReq);
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
        testDeleteServerRequest delReq = new testDeleteServerRequest();
        delReq.setId(testServerId);
        testDeleteServerResponse delResp = eif.deleteServerRequest(delReq);
        Assert.assertNotNull(delResp);
    }

    @Test
    public void getAllServersTest() throws Exception {
        testGetAllServersRequest req = new testGetAllServersRequest();
        Utility.addSoapAdminAuthHeader((WSBindingProvider)eif);
        testGetAllServersResponse resp = eif.getAllServersRequest(req);
        Assert.assertNotNull("GetAllServersResponse object", resp);
        List <testServerInfo> serverInfoList = resp.getServer();
        int len;
        Assert.assertNotNull("GetAllServersResponse list of server", serverInfoList);
        len = serverInfoList.size();
        Assert.assertTrue("Number of GetAllServersResponse <server> children is " + len +
                " - should be at least 1", len >= 1);
    }

    // Getting system failure: server coco.local
    //     zimbraRemoteManagementPrivateKeyPath
    //     (/opt/zimbra/.ssh/zimbra_identity) does not exist
    //     Got further with :
    //         zmlocalconfig -e zimbra_user=$USER
    //         /opt/zimbra/bin/zmsshkeygen
    //         cat /opt/zimbra/.ssh/zimbra_identity.pub>>$HOME/.ssh/authorized_keys
    //     However, still get :
    //     Caused by: java.io.IOException: There was a problem while
    //                connecting to wsdl.server.example.test:22
    // TODO: Re-enable when/if know how to resolve this.
    // @Test
    public void getServerNIfs() throws Exception {
        int len;
        testServerSelector serverSel;
        String testServerId = Utility.ensureServerExists(testServer);
        testGetServerNIfsRequest getReq = new testGetServerNIfsRequest();
        serverSel = new testServerSelector();
        serverSel.setBy(testServerBy.ID);
        serverSel.setValue(testServerId);
        getReq.setServer(serverSel);
        testGetServerNIfsResponse getResp = eif.getServerNIfsRequest(getReq);
        Assert.assertNotNull("response object", getResp);
        List <testNetworkInformation> nis = getResp.getNi();
        Assert.assertNotNull("List of NIs", nis);
        testNetworkInformation ni = nis.get(0);
        Assert.assertNotNull("First NI", ni);
        List <testAttr> attrs = ni.getA();
        len = attrs.size();
        Assert.assertTrue("GetServerNIfsResponse <server> has " + len +
                " <a> children - should have at least 2", len >= 2);
    }

    @Test
    public void createCosTest() throws Exception {
        int len;
        Utility.deleteCosIfExists(testCos);
        testCreateCosRequest req = new testCreateCosRequest();
        req.setName(testCos);
        Utility.addSoapAdminAuthHeader((WSBindingProvider)eif);
        testCreateCosResponse resp = eif.createCosRequest(req);
        Assert.assertNotNull(resp);
        testCosInfo cosInfo = resp.getCos();
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
        testCosSelector cosSel;
        testCosInfo cosInfo;
        String respId;
        String testCosId = Utility.ensureCosExists(testCos);
        testGetCosRequest getReq = new testGetCosRequest();
        cosSel = new testCosSelector();
        cosSel.setBy(testCosBy.ID);
        cosSel.setValue(testCosId);
        getReq.setCos(cosSel);
        testGetCosResponse getResp = eif.getCosRequest(getReq);
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
        testCosSelector cosSel;
        testCosInfo cosInfo;
        String respId;
        String testCosId = Utility.ensureCosExists(testCos);
        testModifyCosRequest modReq = new testModifyCosRequest();
        modReq.setId(testCosId);
        testAttr modAttr = new testAttr();
        modAttr.setN("zimbraMailForwardingAddressMaxNumAddrs");
        modAttr.setValue("99");
        modReq.getA().add(modAttr);
        testModifyCosResponse modResp = eif.modifyCosRequest(modReq);
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

        testGetCosRequest getReq = new testGetCosRequest();
        cosSel = new testCosSelector();
        cosSel.setBy(testCosBy.ID);
        cosSel.setValue(testCosId);
        getReq.setCos(cosSel);
        getReq.setAttrs("zimbraMailForwardingAddressMaxNumAddrs");
        testGetCosResponse getResp = eif.getCosRequest(getReq);
        Assert.assertNotNull(getResp);
        cosInfo = getResp.getCos();
        Assert.assertNotNull(cosInfo);
        Assert.assertEquals("getCosResponse <cos> 'name' attribute",
                testCos, cosInfo.getName());
        respId = cosInfo.getId();
        Assert.assertEquals("getCosResponse <cos> 'id' attribute", testCosId, respId);
        len = cosInfo.getA().size();
        Assert.assertEquals("Number of GetCosResponse <cos> <a> children", 1, len);
        testAttr maxFwdingAddrs = cosInfo.getA().get(0);
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
        testCosSelector cosSel;
        testCosInfo cosInfo;
        String respId;
        String testCosId = Utility.ensureCosExists(testCos);
        testCopyCosRequest copyReq = new testCopyCosRequest();
        copyReq.setName(testCosCopy);
        cosSel = new testCosSelector();
        cosSel.setBy(testCosBy.ID);
        cosSel.setValue(testCosId);
        copyReq.setCos(cosSel);
        testCopyCosResponse copyResp = eif.copyCosRequest(copyReq);
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
        testRenameCosRequest renameCosReq = new testRenameCosRequest();
        renameCosReq.setId(testCosId);
        renameCosReq.setNewName("foobar" + testCos);
        testRenameCosResponse renameCosResp = eif.renameCosRequest(renameCosReq);
        Assert.assertNotNull(renameCosResp);
        testCosInfo cosInfo = renameCosResp.getCos();
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
        testDeleteCosRequest delReq = new testDeleteCosRequest();
        delReq.setId(testCosId);
        testDeleteCosResponse delResp = eif.deleteCosRequest(delReq);
        Assert.assertNotNull(delResp);
    }

    @Test
    public void getAllCosTest() throws Exception {
        testGetAllCosRequest req = new testGetAllCosRequest();
        Utility.addSoapAdminAuthHeader((WSBindingProvider)eif);
        testGetAllCosResponse resp = eif.getAllCosRequest(req);
        Assert.assertNotNull("GetAllCosResponse object", resp);
        List <testAnnotatedCosInfo> cosInfoList = resp.getCos();
        int len;
        Assert.assertNotNull("GetAllCosResponse list of cos", cosInfoList);
        len = cosInfoList.size();
        Assert.assertTrue("Number of GetAllCosResponse <cos> children is " + len +
                " - should be at least 1", len >= 1);
    }

    @Test
    public void getMailQueueTest() throws Exception {
        testGetAllServersRequest gasReq = new testGetAllServersRequest();
        Utility.addSoapAdminAuthHeader((WSBindingProvider)eif);
        testGetAllServersResponse gasResp = eif.getAllServersRequest(gasReq);
        testServerInfo serverInfo = gasResp.getServer().get(0);
        testGetMailQueueRequest req = new testGetMailQueueRequest();
        testServerMailQueueQuery smqq = new testServerMailQueueQuery();
        smqq.setName(serverInfo.getName());
        testMailQueueQuery mqq = new testMailQueueQuery();
        mqq.setName("Wow");  // TODO: Use a real name?
        // Note: set true -> sets off a scan which might interfere with reruns
        mqq.setScan(false);
        testQueueQuery qq = new testQueueQuery();
        mqq.setQuery(qq);
        smqq.setQueue(mqq);
        req.setServer(smqq);
        testGetMailQueueResponse resp = eif.getMailQueueRequest(req);
        Assert.assertNotNull("GetMailQueueResponse object", resp);
        testServerMailQueueDetails smqd = resp.getServer();
        Assert.assertEquals("Server name", serverInfo.getName(), smqd.getName());
        testMailQueueDetails mqd = smqd.getQueue();
        Assert.assertNotNull("MailQueueDetails object", mqd);
        mqd.getTime();
        Assert.assertEquals("queue total", 0, mqd.getTotal());
        Assert.assertEquals("queue isMore", false, mqd.isMore());
        mqd.isScan();
        mqd.getQi();  // TODO: For real Q, this would potentially be populated
        mqd.getQs();  // TODO: For real Q, this would potentially be populated
    }

    @Test
    public void CheckDirectoryTest() throws Exception {
        testCheckDirectoryRequest req = new testCheckDirectoryRequest();
        Utility.addSoapAdminAuthHeader((WSBindingProvider)eif);
        testCheckDirSelector dirSel = new testCheckDirSelector();
        dirSel.setPath("/opt/zimbra/log");
        req.getDirectory().add(dirSel);
        dirSel = new testCheckDirSelector();
        dirSel.setPath("/opt/zimbra/wsdlNonExistent");
        req.getDirectory().add(dirSel);
        dirSel = new testCheckDirSelector();
        dirSel.setPath("/opt/zimbra/wsdlToBeCreated");
        dirSel.setCreate(true);
        req.getDirectory().add(dirSel);
        testCheckDirectoryResponse resp = eif.checkDirectoryRequest(req);
        Assert.assertNotNull("CheckDirectoryResponse object", resp);
        List <testDirPathInfo> dirPaths = resp.getDirectory();
        Assert.assertNotNull("CheckDirectoryResponse list of directories", dirPaths);
        int len = dirPaths.size();
        Assert.assertEquals("Number of paths", 3, len);
        for (testDirPathInfo pathInfo : dirPaths) {
            String path = pathInfo.getPath();
            if (path.equals("/opt/zimbra/log")) {
                Assert.assertEquals("isExists" + " for path=" + path,
                        true, pathInfo.isExists());
                Assert.assertEquals("isDirectory" + " for path=" + path,
                        true, pathInfo.isIsDirectory());
                Assert.assertEquals("isReadable" + " for path=" + path,
                        true, pathInfo.isReadable());
                Assert.assertEquals("isWritable" + " for path=" + path,
                        true, pathInfo.isWritable());
            } else if (path.equals("/opt/zimbra/wsdlNonExistent")) {
                Assert.assertEquals("isExists" + " for path=" + path,
                        false, pathInfo.isExists());
                Assert.assertEquals("isDirectory" + " for path=" + path,
                        false, pathInfo.isIsDirectory());
                Assert.assertEquals("isReadable" + " for path=" + path,
                        false, pathInfo.isReadable());
                Assert.assertEquals("isWritable" + " for path=" + path,
                        false, pathInfo.isWritable());
            } else if (path.equals("/opt/zimbra/wsdlToBeCreated")) {
                Assert.assertEquals("isExists" + " for path=" + path,
                        true, pathInfo.isExists());
                Assert.assertEquals("isDirectory" + " for path=" + path,
                        true, pathInfo.isIsDirectory());
                Assert.assertEquals("isReadable" + " for path=" + path,
                        true, pathInfo.isReadable());
                Assert.assertEquals("isWritable" + " for path=" + path,
                        true, pathInfo.isWritable());
            } else
                Assert.fail("Unexpected path=" + path);
        }
    }
}
