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
import com.zimbra.soap.admin.wsimport.generated.DomainAdminRight.Rights;
import com.zimbra.soap.admin.wsimport.generated.EffectiveAttrInfo.Default;
import com.zimbra.soap.admin.wsimport.generated.GetRightsDocResponse.DomainAdminCopypasteToZimbraRightsDomainadminXmlTemplate;

import com.zimbra.soap.Utility;

import org.junit.Assert;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Element;

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
    public void versionInfoTest() throws Exception {
        Utility.addSoapAdminAuthHeader((WSBindingProvider)eif);
        GetVersionInfoRequest req = new GetVersionInfoRequest();
        GetVersionInfoResponse resp = eif.getVersionInfoRequest(req);
        Assert.assertNotNull("GetVersionInfoResponse object", resp);
        VersionInfo info = resp.getInfo();
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
        GetLicenseInfoRequest req = new GetLicenseInfoRequest();
        GetLicenseInfoResponse resp = eif.getLicenseInfoRequest(req);
        Assert.assertNotNull("GetLicenseInfoResponse object", resp);
        LicenseExpirationInfo info = resp.getExpiration();
        Assert.assertNotNull("GetLicenseInfoResponse <info> object", info);
        Assert.assertNotNull("getDate result", info.getDate());
    }

    @Test
    public void getServiceStatusTest() throws Exception {
        Utility.addSoapAdminAuthHeader((WSBindingProvider)eif);
        GetServiceStatusRequest req = new GetServiceStatusRequest();
        GetServiceStatusResponse resp = eif.getServiceStatusRequest(req);
        Assert.assertNotNull("GetServiceStatusResponse object", resp);
        TimeZoneInfo tz = resp.getTimezone();
        Assert.assertNotNull("GetServiceStatusResponse <timezone> object", tz);
        Assert.assertNotNull("GetServiceStatusResponse <timezone> displayName",
                tz.getDisplayName());
        Assert.assertNotNull("GetServiceStatusResponse <timezone> id",
                tz.getId());
        List <ServiceStatus> statuses = resp.getStatus();
        // TODO: Would be nice to test with some real statuses - looks like
        //       need logger service installed and enabled
        Assert.assertNotNull("GetServiceStatusResponse statuses list object",
                statuses);
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
    public void checkHostnameResolveTest() throws Exception {
        Utility.addSoapAdminAuthHeader((WSBindingProvider)eif);
        CheckHostnameResolveRequest req = new CheckHostnameResolveRequest();
        req.setHostname("nonexist.example.com");
        CheckHostnameResolveResponse resp = eif.checkHostnameResolveRequest(req);
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
        CheckDomainMXRecordRequest req = new CheckDomainMXRecordRequest();
        DomainSelector domainSel = new DomainSelector();
        domainSel.setBy(DomainBy.NAME);
        domainSel.setValue("zimbra.com");
        req.setDomain(domainSel);
        CheckDomainMXRecordResponse resp = eif.checkDomainMXRecordRequest(req);
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
        CacheSelector sel = new CacheSelector();
        sel.setAllServers(true);
        sel.setType(CacheEntryType.DOMAIN.value());
        FlushCacheRequest req = new FlushCacheRequest();
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

    private void validateRightInfo(RightInfo rInfo, String riTag) {
        RightsAttrs rAttrs = rInfo.getAttrs();
        if (null != rAttrs) {
            int aNum = 0;
            for (Attr attr: rAttrs.getA()) {
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
        ComboRights comboRights = rInfo.getRights();
        if (null != comboRights) {
            for (ComboRightInfo cri : comboRights.getR()) {
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
        AdminService nvEif = Utility.getNonValidatingAdminSvcEIF();
        // the validator does not like the @XmlAnyElement used
        // in RightsAttrs
        Utility.addSoapAdminAuthHeader((WSBindingProvider)nvEif);
        GetAllRightsRequest req = new GetAllRightsRequest();
        GetAllRightsResponse resp = nvEif.getAllRightsRequest(req);
        Assert.assertNotNull("GetAllRightsResponse object", resp);
        List <RightInfo> rInfos = resp.getRight();
        Assert.assertNotNull("GetAllRightsResponse object", rInfos);
        int riNum = 0;
        for (RightInfo rInfo : rInfos) {
            riNum++;
            String riTag = "RightInfo " + riNum;
            validateRightInfo(rInfo, riTag);
        }
    }

    @Test
    public void getRightTest() throws Exception {
        Utility.ensureAccountExists(testAcct);
        Utility.addSoapAdminAuthHeader((WSBindingProvider)eif);
        GetRightRequest req = new GetRightRequest();
        req.setExpandAllAttrs(true);
        req.setRight("adminConsoleAccountRights");
        GetRightResponse resp = eif.getRightRequest(req);
        Assert.assertNotNull("GetRightResponse object", resp);
        RightInfo rInfo = resp.getRight();
        Assert.assertNotNull("GetRightResponse RightInfo", rInfo);
        validateRightInfo(rInfo, "RightInfo");
    }

    @Test
    public void getRightsDocTest() throws Exception {
        Utility.ensureAccountExists(testAcct);
        Utility.addSoapAdminAuthHeader((WSBindingProvider)eif);
        GetRightsDocRequest req = new GetRightsDocRequest();
        GetRightsDocResponse resp = eif.getRightsDocRequest(req);
        Assert.assertNotNull("GetRightsDocResponse object", resp);

        List<PackageRightsInfo> pkgRights = resp.getPackage();
        int pkgNum = 0;
        for (PackageRightsInfo pkg : pkgRights) {
            pkgNum++;
            String pkgTag = "Package " + pkgNum;
            Assert.assertNotNull(pkgTag + " name", pkg.getName());
            List<CmdRightsInfo> cmdRights = pkg.getCmd();
            Assert.assertNotNull(pkgTag + " Cmd list", cmdRights);
            int cmdNum = 0;
            for (CmdRightsInfo cmd : cmdRights) {
                cmdNum++;
                String cmdTag = pkgTag + " Cmd " + cmdNum;
                Assert.assertNotNull(cmdTag + " description",
                        cmd.getDesc());
                Assert.assertNotNull(cmdTag + " name", cmd.getName());
                com.zimbra.soap.admin.wsimport.generated.CmdRightsInfo.Rights rInfo =
                    cmd.getRights();
                List<NamedElement> rNs = rInfo.getRight();
                int rNum = 0;
                for (NamedElement rn : rNs) {
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
        List<DomainAdminRight> rights = domRights.getRight();
        Assert.assertNotNull(tag + " rights list", rights);
        int domNum = 0;
        for (DomainAdminRight dar : rights) {
            domNum++;
            String domTag = tag + " right " + domNum;
            Assert.assertNotNull(domTag + " name", dar.getName());
            Assert.assertNotNull(domTag + " type", dar.getType());
            Assert.assertNotNull(domTag + " description", dar.getDesc());
            Rights subRights = dar.getRights();
            Assert.assertNotNull(domTag + " rights", subRights);
            int rnNum = 0;
            for (RightWithName rWithName : subRights.getR()) {
                rnNum++;
                String rnTag = domTag + " right " + rnNum;
                Assert.assertNotNull(rnTag + " n attrib", rWithName.getN());
            }
        }
    }

    private void checkEffectiveAttrsInfo(EffectiveAttrsInfo attrInfo,
            String tag) {
        Assert.assertNotNull(tag, attrInfo);
        Assert.assertTrue(tag + " all setting", attrInfo.isAll());
        List <EffectiveAttrInfo> attrs = attrInfo.getA();
        Assert.assertNotNull(tag + " attrs", attrs);
        int attNum = 0;
        for (EffectiveAttrInfo anAttr : attrs) {
            attNum++;
            String attrTag = tag + " attr " + attNum;
            Assert.assertNotNull(attrTag, anAttr);
            Assert.assertNotNull(attrTag + " n", anAttr.getN());
            ConstraintInfo constraint = anAttr.getConstraint();
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

    private void checkAllEffectiveRights(EffectiveRightsInfo allEffectiveRights,
            String tag) {
        Assert.assertNotNull("allEffectiveRights", allEffectiveRights);
        checkEffectiveAttrsInfo(allEffectiveRights.getGetAttrs(),
                tag + " getAttrs");
        checkEffectiveAttrsInfo(allEffectiveRights.getSetAttrs(),
                tag + " setAttrs");
        List <RightWithName> rights = allEffectiveRights.getRight();
        Assert.assertNotNull("rights", rights);
        int rNum = 0;
        for (RightWithName aRight : rights) {
            rNum++;
            String rTag = tag + " right " + rNum;
            Assert.assertNotNull(rTag + " name", aRight.getN());
        }
    }

    @Test
    public void getAllEffectiveRightsTest() throws Exception {
        Utility.ensureAccountExists(testAcct);
        Utility.addSoapAdminAuthHeader((WSBindingProvider)eif);
        GetAllEffectiveRightsRequest req = new GetAllEffectiveRightsRequest();
        req.setExpandAllAttrs("setAttrs, getAttrs");
        GetAllEffectiveRightsResponse resp = eif.getAllEffectiveRightsRequest(req);
        Assert.assertNotNull("GetAllEffectiveRightsResponse object", resp);
        GranteeInfo grantee = resp.getGrantee();
        Assert.assertNotNull("grantee object", grantee);
        Assert.assertNotNull("grantee id", grantee.getId());
        Assert.assertNotNull("grantee name", grantee.getName());
        Assert.assertNotNull("grantee type", grantee.getType());
        List<EffectiveRightsTarget> targets = resp.getTarget();
        Assert.assertNotNull("list of targets", targets);
        int targNum = 0;
        for ( EffectiveRightsTarget target : targets) {
            targNum++;
            String targTag = "target " + targNum;
            Assert.assertNotNull(targTag, target);
            Assert.assertNotNull(targTag + " type", target.getType());
            checkAllEffectiveRights(target.getAll(), targTag + " all");
            
            List <InDomainInfo> inDomsList = target.getInDomains();
            Assert.assertNotNull("InDomains list", inDomsList);
            int inDomNum = 0;
            for (InDomainInfo anInDom : inDomsList) {
                inDomNum++;
                String inDomTag = targTag + " inDomain " + inDomNum;
                int domNum = 0;
                for (NamedElement dom : anInDom.getDomain()) {
                    domNum++;
                    String domTag = inDomTag + " domain " + domNum;
                    Assert.assertNotNull(domTag + " name", dom.getName());
                }
                checkAllEffectiveRights(anInDom.getRights(), 
                        inDomTag + " rights");
            }
            List <RightsEntriesInfo> entries = target.getEntries();
            int entNum = 0;
            for (RightsEntriesInfo entriesInfo : entries) {
                entNum++;
                String entTag = targTag + " RightsEntries " + entNum;
                Assert.assertNotNull(entTag, entriesInfo);
                int entryNum = 0;
                for (NamedElement namedEntry : entriesInfo.getEntry()) {
                    entryNum++;
                    String entryTag = entTag + " entry " + entNum;
                    Assert.assertNotNull(entryTag + " name", namedEntry.getName());
                }
                EffectiveRightsInfo entriesInfoRights = entriesInfo.getRights();
                Assert.assertNotNull("entriesInfoRights", entriesInfoRights);
            }
        }
    }

    // TODO: Figure out how to test GrantRight/GetGrants
    //       Looks like can only assign a user right to a regular user
    //       The GetAllRightsResponse I've seen only talks about 
    //       various rights with rightClass="ADMIN"
    // @Test
    public void NOTgrants() throws Exception {
        String accountId = Utility.ensureAccountExists(testAcct);
        Utility.addSoapAdminAuthHeader((WSBindingProvider)eif);

        GranteeSelector gSel = new GranteeSelector();
        gSel.setBy(GranteeBy.NAME); // required
        // note - initial testing idea was to use "admin" account
        // but GetGrantsRequest won't accept an admin account
        // as the grantee - they don't need to be granted anything...
        gSel.setValue(testAcct);
        gSel.setType(GranteeType.USR);
        gSel.setAll(true);  // required
        gSel.setSecret("test123");

        EffectiveRightsTargetSelector tSel = new EffectiveRightsTargetSelector();
        tSel.setType(TargetType.ACCOUNT);
        tSel.setBy(TargetBy.ID);
        tSel.setValue(accountId);

        RightModifierInfo rmi = new RightModifierInfo();
        // Note: if CanDelegate modifier set, target cannot be a regular user
        //       acct.
        // rmi.setCanDelegate(true);
        rmi.setValue("addAccountAlias");
        rmi.setSubDomain(false);

        GrantRightRequest grReq = new GrantRightRequest();
        grReq.setGrantee(gSel);
        grReq.setTarget(tSel);
        grReq.setRight(rmi);

        GrantRightResponse grResp = eif.grantRightRequest(grReq);
        Assert.assertNotNull("GrantRightResponse object", grResp);

        GetGrantsRequest ggReq = new GetGrantsRequest();
        ggReq.setGrantee(gSel);
        ggReq.setTarget(tSel);
        GetGrantsResponse ggResp = eif.getGrantsRequest(ggReq);
        Assert.assertNotNull("GetGrantsResponse object", ggResp);
        Assert.assertTrue("Number of grants >= 1", ggResp.getGrant().size() >=1);
        int gNum = 0;
        for (GrantInfo grant : ggResp.getGrant()) {
            gNum++;
            String gTag = " grant " + gNum;
            GranteeInfo grantee = grant.getGrantee();
            Assert.assertNotNull(gTag + " GranteeInfo", grantee);
            Assert.assertNotNull(gTag + " Grantee type", grantee.getType());
            Assert.assertNotNull(gTag + " Grantee id", grantee.getId());
            Assert.assertNotNull(gTag + " Grantee name", grantee.getName());
            TypeIdName targ = grant.getTarget();
            Assert.assertNotNull(gTag + " Target", targ);
            Assert.assertNotNull(gTag + " Target type", targ.getType());
            Assert.assertNotNull(gTag + " Target id", targ.getId());
            Assert.assertNotNull(gTag + " Target name", targ.getName());
            RightModifierInfo rightMod = grant.getRight();
            Assert.assertNotNull(gTag + " Right", rightMod);
            Assert.assertNotNull(gTag + " Right value", rightMod.getValue());
        }
    }

    @Test
    public void getEffectiveRightsTest() throws Exception {
        String accountId = Utility.ensureAccountExists(testAcct);
        Utility.addSoapAdminAuthHeader((WSBindingProvider)eif);
        GetEffectiveRightsRequest req = new GetEffectiveRightsRequest();
        req.setExpandAllAttrs("getAttrs");
        GranteeSelector gSel = new GranteeSelector();
        gSel.setBy(GranteeBy.NAME);
        gSel.setValue("admin");
        req.setGrantee(gSel);
        EffectiveRightsTargetSelector tSel = new EffectiveRightsTargetSelector();
        tSel.setType(TargetType.ACCOUNT);
        tSel.setBy(TargetBy.ID);
        tSel.setValue(accountId);
        req.setTarget(tSel);
        GetEffectiveRightsResponse resp = eif.getEffectiveRightsRequest(req);
        Assert.assertNotNull("GetEffectiveRightsResponse object", resp);
        GranteeInfo grantee = resp.getGrantee();
        Assert.assertNotNull("grantee object", grantee);
        Assert.assertNotNull("grantee id", grantee.getId());
        Assert.assertNotNull("grantee name", grantee.getName());
        // Not present for GetEffectiveRights (is for GetAllEffectiveRights)
        // Assert.assertNotNull("grantee type", grantee.getType());
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

    // Getting system failure: server gren-elliots-macbook-pro.local 
    //     zimbraRemoteManagementPrivateKeyPath 
    //     (/opt/zimbra/.ssh/zimbra_identity) does not exist
    // TODO: Re-enable when/if know how to resolve this.
    public void getServerNIfsTestNOT() throws Exception {
        int len;
        ServerSelector serverSel;
        String testServerId = Utility.ensureServerExists(testServer);
        GetServerNIfsRequest getReq = new GetServerNIfsRequest();
        serverSel = new ServerSelector();
        serverSel.setBy(ServerBy.ID);
        serverSel.setValue(testServerId);
        getReq.setServer(serverSel);
        GetServerNIfsResponse getResp = eif.getServerNIfsRequest(getReq);
        Assert.assertNotNull("response object", getResp);
        List <NetworkInformation> nis = getResp.getNi();
        Assert.assertNotNull("List of NIs", nis);
        NetworkInformation ni = nis.get(0);
        Assert.assertNotNull("First NI", ni);
        List <Attr> attrs = ni.getA();
        len = attrs.size();
        Assert.assertTrue("GetServerNIfsResponse <server> has " + len +
                " <a> children - should have at least 2", len >= 2);
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
        List <AnnotatedCosInfo> cosInfoList = resp.getCos();
        int len;
        Assert.assertNotNull("GetAllCosResponse list of cos", cosInfoList);
        len = cosInfoList.size();
        Assert.assertTrue("Number of GetAllCosResponse <cos> children is " + len +
                " - should be at least 1", len >= 1);
    }

    @Test
    public void CheckDirectoryTest() throws Exception {
        CheckDirectoryRequest req = new CheckDirectoryRequest();
        Utility.addSoapAdminAuthHeader((WSBindingProvider)eif);
        CheckDirSelector dirSel = new CheckDirSelector();
        dirSel.setPath("/opt/zimbra/log");
        req.getDirectory().add(dirSel);
        dirSel = new CheckDirSelector();
        dirSel.setPath("/opt/zimbra/wsdlNonExistent");
        req.getDirectory().add(dirSel);
        dirSel = new CheckDirSelector();
        dirSel.setPath("/opt/zimbra/wsdlToBeCreated");
        dirSel.setCreate(true);
        req.getDirectory().add(dirSel);
        CheckDirectoryResponse resp = eif.checkDirectoryRequest(req);
        Assert.assertNotNull("CheckDirectoryResponse object", resp);
        List <DirPathInfo> dirPaths = resp.getDirectory();
        Assert.assertNotNull("CheckDirectoryResponse list of directories", dirPaths);
        int len = dirPaths.size();
        Assert.assertEquals("Number of paths", 3, len);
        for (DirPathInfo pathInfo : dirPaths) {
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
