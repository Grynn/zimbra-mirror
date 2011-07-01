package com.zimbra.qa.unittest;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.io.XMLWriter;

import com.zimbra.common.soap.AdminConstants;
import com.zimbra.common.soap.Element;
import com.zimbra.common.util.Version;
import com.zimbra.cs.account.Config;
import com.zimbra.cs.account.Provisioning;
import com.zimbra.cs.client.LmcSession;
import com.zimbra.cs.service.versioncheck.VersionCheck;
import com.zimbra.cs.versioncheck.VersionUpdate;
import com.zimbra.cs.client.soap.LmcVersionCheckRequest;
import com.zimbra.cs.client.soap.LmcVersionCheckResponse;
import junit.framework.TestCase;
import com.zimbra.cs.util.BuildInfo;
import com.zimbra.common.localconfig.LC;
/**
 * @author Greg Solovyev
 */
public class TestVersionCheck extends TestCase {
    private String versionCheckURL;
    private String lastResponse;
    public void setUp() throws Exception {
        Provisioning prov = Provisioning.getInstance();
        Config config;
        config = prov.getConfig();
        this.versionCheckURL = config.getAttr(Provisioning.A_zimbraVersionCheckURL);
        this.lastResponse = config.getAttr(Provisioning.A_zimbraVersionCheckLastResponse);
        Map<String, String> attrs = new HashMap<String, String>();
        attrs.put(Provisioning.A_zimbraVersionCheckURL, "http://localhost/zimbra/testversion.xml");
        prov.modifyAttrs(config, attrs, true);
        generateTestVersionXML();
    }

    private void generateTestVersionXML() {
    	try {
			FileWriter fileWriter = new FileWriter(LC.mailboxd_directory.value()+"/webapps/zimbra/testversion.xml");
			XMLWriter xw = new XMLWriter(fileWriter, org.dom4j.io.OutputFormat.createPrettyPrint());
			Document doc = DocumentHelper.createDocument();
            org.dom4j.Element rootEl = DocumentHelper.createElement("versionCheck");
            rootEl.addAttribute("status", "1");
            org.dom4j.Element updatesEl = DocumentHelper.createElement("updates");
            doc.add(rootEl);
            rootEl.add(updatesEl);
            org.dom4j.Element updateEl = DocumentHelper.createElement("update");
            updateEl.addAttribute("type", "major");
            updateEl.addAttribute("shortversion", Integer.toString(Integer.parseInt(BuildInfo.MAJORVERSION)+1)+".0.0");
            updateEl.addAttribute("version", Integer.toString(Integer.parseInt(BuildInfo.MAJORVERSION)+1)+".0.0_GA");
            updateEl.addAttribute("platform", BuildInfo.PLATFORM);
            updateEl.addAttribute("buildtype", BuildInfo.TYPE);
            updateEl.addAttribute("release", Integer.toString(Integer.parseInt(BuildInfo.BUILDNUM)+10));
            updateEl.addAttribute("critical", "0");
            updateEl.addAttribute("updateURL", "http://www.zimbra.com/community/downloads.html");
            updateEl.addAttribute("description", "description");
            updatesEl.add(updateEl);
            
            updateEl = DocumentHelper.createElement("update");
            updateEl.addAttribute("type", "minor");
            updateEl.addAttribute("shortversion",String.format("%s.%d.0", BuildInfo.MAJORVERSION,Integer.parseInt(BuildInfo.MINORVERSION)+1));
            updateEl.addAttribute("version", String.format("%s.%d.0_GA_%d", BuildInfo.MAJORVERSION,Integer.parseInt(BuildInfo.MINORVERSION)+1,Integer.parseInt(BuildInfo.BUILDNUM)+5));
            updateEl.addAttribute("platform", BuildInfo.PLATFORM);
            updateEl.addAttribute("buildtype", BuildInfo.TYPE);
            updateEl.addAttribute("release", Integer.toString(Integer.parseInt(BuildInfo.BUILDNUM)+5));
            updateEl.addAttribute("critical", "0");
            updateEl.addAttribute("updateURL", "http://www.zimbra.com/community/downloads.html");
            updateEl.addAttribute("description", "description");
            updatesEl.add(updateEl);
            
            updateEl = DocumentHelper.createElement("update");
            updateEl.addAttribute("type", "micro");
            updateEl.addAttribute("shortversion",String.format("%s.%s.%d", BuildInfo.MAJORVERSION,BuildInfo.MINORVERSION,Integer.parseInt(BuildInfo.MICROVERSION)+1));
            updateEl.addAttribute("version", String.format("%s.%s.%d_GA_%d", BuildInfo.MAJORVERSION,BuildInfo.MINORVERSION,Integer.parseInt(BuildInfo.MICROVERSION)+1,Integer.parseInt(BuildInfo.BUILDNUM)+2));
            updateEl.addAttribute("platform", BuildInfo.PLATFORM);
            updateEl.addAttribute("buildtype", BuildInfo.TYPE);
            updateEl.addAttribute("release", Integer.toString(Integer.parseInt(BuildInfo.BUILDNUM)+2));
            updateEl.addAttribute("critical", "1");
            updateEl.addAttribute("updateURL", "http://www.zimbra.com/community/downloads.html");
            updateEl.addAttribute("description", "description");
            updatesEl.add(updateEl);
            
            updateEl = DocumentHelper.createElement("update");
            updateEl.addAttribute("type", "build");
            updateEl.addAttribute("shortversion",String.format("%s.%s.%s", BuildInfo.MAJORVERSION,BuildInfo.MINORVERSION,Integer.toString(Integer.parseInt(BuildInfo.MICROVERSION)+1)));
            updateEl.addAttribute("version", String.format("%s.%s.%s_GA_%d", BuildInfo.MAJORVERSION,BuildInfo.MINORVERSION,BuildInfo.MICROVERSION,Integer.parseInt(BuildInfo.BUILDNUM)+1));
            updateEl.addAttribute("platform", BuildInfo.PLATFORM);
            updateEl.addAttribute("buildtype", BuildInfo.TYPE);
            updateEl.addAttribute("release", Integer.toString(Integer.parseInt(BuildInfo.BUILDNUM)+1));
            updateEl.addAttribute("critical", "1");
            updateEl.addAttribute("updateURL", "http://www.zimbra.com/community/downloads.html");
            updateEl.addAttribute("description", "description");
            updatesEl.add(updateEl);
            xw.write(doc);
            xw.flush();
		} catch (IOException e) {
			e.printStackTrace();
			fail();
		}
    }
    
    private void cleanup() throws Exception {
        Provisioning prov = Provisioning.getInstance();
        Config config;
        config = prov.getConfig();
        Map<String, String> attrs = new HashMap<String, String>();
        attrs.put(Provisioning.A_zimbraVersionCheckURL, this.versionCheckURL);
        attrs.put(Provisioning.A_zimbraVersionCheckLastResponse, this.lastResponse);
        prov.modifyAttrs(config, attrs, true);
        File testxmlfile = new File(LC.mailboxd_directory.value()+"/webapps/zimbra/testversion.xml");
        testxmlfile.delete();
    }

    public void tearDown() throws Exception {
        cleanup();
    }

    public void testSOAP() throws Exception {
        LmcSession session = TestUtil.getAdminSoapSession();
        LmcVersionCheckRequest checkRequest = new LmcVersionCheckRequest();
        checkRequest.setAction(AdminConstants.VERSION_CHECK_CHECK);//this should retreive the new version from http://localhost/zimbra/test/testversion.xml
        checkRequest.setSession(session);
        String url = TestUtil.getAdminSoapUrl();
        LmcVersionCheckResponse resp = (LmcVersionCheckResponse) checkRequest.invoke(url);
        //this response is empty - nothing to check except that we got it
        assertNotNull(resp);

        //check the response in LDAP
        Provisioning prov = Provisioning.getInstance();
        Config config;
        config = prov.getConfig();
        String savedResp = config.getAttr(Provisioning.A_zimbraVersionCheckLastResponse);
        assertNotNull(savedResp);

        //check response from admin service
        LmcVersionCheckRequest versionRequest = new LmcVersionCheckRequest();
        versionRequest.setAction(AdminConstants.VERSION_CHECK_STATUS);
        versionRequest.setSession(session);
        LmcVersionCheckResponse versionResp = (LmcVersionCheckResponse)versionRequest.invoke(url);
        //the test xml should contain one major update, one minor update and two micro updates (critical and non-critical)
        List <VersionUpdate> updates = versionResp.getUpdates();
        int counter=0;
        int majorCounter=0;
        int minorCounter=0;
        int microCounter=0;
        int buildCounter=0;
        for(Iterator <VersionUpdate> iter = updates.iterator();iter.hasNext();){
            counter++;
            VersionUpdate update = iter.next();
            assertNotNull(update);

            assertTrue("Update is older than current version",Version.compare(BuildInfo.VERSION, update.getShortversion()) < 0);
            assertNotNull(update.getUpdateURL());
            assertNotNull(update.getType());
            assertNotNull(update.getShortversion());

            String updateType = update.getType();
            if(updateType.equalsIgnoreCase("major")) {
                majorCounter++;
            } else if (updateType.equalsIgnoreCase("minor")) {
                minorCounter++;
            } else if(updateType.equalsIgnoreCase("micro")) {
                microCounter++;
            } else if(updateType.equalsIgnoreCase("build")) {
                buildCounter++;
            }
        }
        assertEquals("Wrong number of updates in SOAP response",4,counter);
        assertEquals("Wring number of major updates parsed",majorCounter,1);
        assertEquals("Wring number of minor updates parsed",minorCounter,1);
        assertEquals("Wring number of micro updates parsed",microCounter,1);
        assertEquals("Wring number of build updates parsed",buildCounter,1);
    }

    public void testCheckVersion() throws Exception {
        //the idea is to test retreiving an XML and putting it into LDAP
        VersionCheck.checkVersion(); //this should retreive the new version from http://localhost/zimbra/test/testversion.xml
        Provisioning prov = Provisioning.getInstance();
        Config config;
        config = prov.getConfig();

        String resp = config.getAttr(Provisioning.A_zimbraVersionCheckLastResponse);
        assertNotNull(resp);
        Element respDoc = Element.parseXML(resp);
        assertNotNull(respDoc);
        boolean hasUpdates = respDoc.getAttributeBool(AdminConstants.A_VERSION_CHECK_STATUS, false);
        assertTrue("Update XML document status is not 1 or true",hasUpdates);
        Element eUpdates = respDoc.getElement(AdminConstants.E_UPDATES);
        assertNotNull(eUpdates);
        int counter=0;
        int majorCounter=0;
        int minorCounter=0;
        int microCounter=0;
        int buildCounter=0;
        for (Element e : eUpdates.listElements()) {
            counter++;
            String updateType = e.getAttribute(AdminConstants.A_UPDATE_TYPE);
            if(updateType.equalsIgnoreCase("major")) {
                majorCounter++;
            } else if (updateType.equalsIgnoreCase("minor")) {
                minorCounter++;
            } else if(updateType.equalsIgnoreCase("micro")) {
                microCounter++;
            } else if(updateType.equalsIgnoreCase("build")) {
                buildCounter++;
            }
        }
        assertEquals("Wring number of updates parsed",counter,4);
        assertEquals("Wring number of major updates parsed",majorCounter,1);
        assertEquals("Wring number of minor updates parsed",minorCounter,1);
        assertEquals("Wring number of micro updates parsed",microCounter,1);
        assertEquals("Wring number of build updates parsed",buildCounter,1);
    }
}
