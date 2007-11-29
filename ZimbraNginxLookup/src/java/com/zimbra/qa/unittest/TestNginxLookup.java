package com.zimbra.qa.unittest;

import junit.framework.TestCase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import java.io.IOException;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;

import com.zimbra.common.util.CliUtil;
import com.zimbra.cs.account.Account;
import com.zimbra.cs.account.Config;
import com.zimbra.cs.account.Domain;
import com.zimbra.cs.account.Provisioning;
import com.zimbra.cs.account.Provisioning.AccountBy;
import com.zimbra.cs.account.soap.SoapProvisioning;
import com.zimbra.cs.nginx.NginxLookupExtension;
import com.zimbra.cs.servlet.ZimbraServlet;

public class TestNginxLookup extends TestCase {
    
    private static final String URL = "http://localhost:7072/service/extension/nginx-lookup";
    private static final String ACCT_LOCALPART = "user1";
    private static final String ACCT2_LOCALPART = "user2";
    
    private static final String PASSWORD = "test123";
    // private static final String IMAP_HOST = "10.10.131.101";  
    private static final String IMAP_HOST = "192.168.0.162";
    private static final String IMAP_PORT = "7143";
    private static final String POP3_HOST = "192.168.0.162";
    private static final String POP3_PORT = "7110";
    private static final String STATUS_OK = "OK";
    private static final String STATUS_ACCT_INFO_NOT_AVAIL = "Account information not available";
    private static final String AUTH_WAIT = "10";
    private static final String FOREIGN_ID = "t12345";
    
    /*
     * There are three test accounts:
     * ACCT: test account in the test domain, with foreign principal ACCT_FOREIGN_PRINCIPAL
     * ACCT1: account in the default domain with the same local part as ACCT, no foreign principal
     * ACCT2: account in the default domain with different local part as ACCT, 
     *        with foreign principal that has the same "FOREIGN_ID" as that for ACCT,
     *        the foreign principal is FOREIGN_ID+"@"SYSTEM_DEFAULT_DOMAIN
     */
    // to be initialized in testInit
    private static String ACCT_EMAIL;
    private static String ACCT1_EMAIL;
    private static String ACCT2_EMAIL;
    private static String DOMAIN;
    private static String DOMAIN_VIRTUAL_IP;
    private static String SYSTEM_DEFAULT_DOMAIN;
    private static String ACCT_FOREIGN_PRINCIPAL;
    private static String ACCT2_FOREIGN_PRINCIPAL;
    
    public static class Result {
        public Result(String status, String server, String port, String user, String wait) {
            mStatus = status;
            mServer = server;
            mPort = port;
            mUser = user;
            mWait = wait;
        }
        
        void verify(String status, String server, String port, String user, String wait) {
            assertEquals(status, mStatus);
            assertEquals(server, mServer);
            assertEquals(port, mPort);
            assertEquals(user, mUser);
            assertEquals(wait, mWait);
        }
        
        String mStatus;
        String mServer;
        String mPort;
        String mUser;
        String mWait;
    }
    
    private void modifyConfig(Map<String, Object> attrs) throws Exception {
        CliUtil.toolSetup();
        SoapProvisioning sp = new SoapProvisioning();
        sp.soapSetURI("https://localhost:7071" + ZimbraServlet.ADMIN_SERVICE_URI);
        sp.soapZimbraAdminAuthenticate();
        
        Config config = sp.getConfig();
        sp.modifyAttrs(config, attrs);
    }
    
    public void testInit() throws Exception {
        String TEST_ID = TestProvisioningUtil.genTestId();
        String TEST_NAME = "test-nginxlookup";
        
        DOMAIN = TestProvisioningUtil.baseDomainName(TEST_NAME, TEST_ID);
        
        
        // revert reverse proxy config to defaults
        Map<String, Object> attrs = new HashMap<String, Object>();
        attrs.put(Provisioning.A_zimbraReverseProxyUserNameAttribute, "");
        attrs.put(Provisioning.A_zimbraReverseProxyMailHostQuery, "(|(zimbraMailDeliveryAddress=${USER})(zimbraMailAlias=${USER}))");
        modifyConfig(attrs);
        
        Provisioning prov = Provisioning.getInstance();
        SYSTEM_DEFAULT_DOMAIN = prov.getConfig().getAttr(Provisioning.A_zimbraDefaultDomainName);
        ACCT_EMAIL = ACCT_LOCALPART + "@" + DOMAIN;
        ACCT1_EMAIL = ACCT_LOCALPART + "@" + SYSTEM_DEFAULT_DOMAIN;
        ACCT2_EMAIL = ACCT2_LOCALPART + "@" + SYSTEM_DEFAULT_DOMAIN;
        
        // create the domain
        Map<String, Object> domainAttrs = new HashMap<String, Object>();
        // generate a unique IP for this test run so we won't get multiple from search
        SimpleDateFormat fmt =  new SimpleDateFormat("10.HH:mm:ss");
        DOMAIN_VIRTUAL_IP = fmt.format(new Date());
        domainAttrs.put(Provisioning.A_zimbraVirtualIPAddress, DOMAIN_VIRTUAL_IP);
        Domain domain = prov.createDomain(DOMAIN, domainAttrs);
        assertNotNull(domain);
        
        // create the test account
        ACCT_FOREIGN_PRINCIPAL = FOREIGN_ID + "@" + DOMAIN;
        Map<String, Object> acctAttrs = new HashMap<String, Object>();
        acctAttrs.put(Provisioning.A_zimbraForeignPrincipal, ACCT_FOREIGN_PRINCIPAL);
        Account acct = prov.createAccount(ACCT_EMAIL, PASSWORD, acctAttrs);
        assertNotNull(acct);
        
        // set foreign id for an account in the system default domain
        acctAttrs.clear();
        ACCT2_FOREIGN_PRINCIPAL = FOREIGN_ID + "@" +SYSTEM_DEFAULT_DOMAIN;
        acctAttrs.put(Provisioning.A_zimbraForeignPrincipal, ACCT2_FOREIGN_PRINCIPAL);
        Account acct2 = prov.get(AccountBy.name, ACCT2_EMAIL);
        assertNotNull(acct2);
        prov.modifyAttrs(acct2, acctAttrs);
    }
    
    public void testFullEmail() throws Exception {
        doTest(ACCT_EMAIL, PASSWORD, null, "imap").verify(STATUS_OK, IMAP_HOST, IMAP_PORT, null, null);
        doTest(ACCT_EMAIL, PASSWORD, null, "pop3").verify(STATUS_OK, POP3_HOST, POP3_PORT, null, null);
    }
    
    public void testVirtualDomainByProxyIP() throws Exception {
        doTest(ACCT_LOCALPART, PASSWORD, DOMAIN_VIRTUAL_IP, "imap").verify(STATUS_OK, IMAP_HOST, IMAP_PORT, ACCT_EMAIL, null);
        doTest(ACCT_LOCALPART, PASSWORD, DOMAIN_VIRTUAL_IP, "pop3").verify(STATUS_OK, POP3_HOST, POP3_PORT, ACCT_EMAIL, null);
    }
    
    public void testVirtualDomainWrongProxyIP() throws Exception {
        doTest(ACCT_LOCALPART, PASSWORD, "127.0.0.2", "imap").verify(STATUS_OK, IMAP_HOST, IMAP_PORT, ACCT1_EMAIL, null);
        doTest(ACCT_LOCALPART, PASSWORD, "127.0.0.2", "pop3").verify(STATUS_OK, POP3_HOST, POP3_PORT, ACCT1_EMAIL, null);
    }
    
    public void testVirtualDomainNoProxyIP() throws Exception {
        doTest("user1", PASSWORD, null, "imap").verify(STATUS_OK, IMAP_HOST, IMAP_PORT, ACCT1_EMAIL, null);
        doTest("user1", PASSWORD, null, "pop3").verify(STATUS_OK, POP3_HOST, POP3_PORT, ACCT1_EMAIL, null);
    }
    
    public void testSupportedExtensionFullEmail() throws Exception {
        doTest(ACCT_EMAIL+"/tb", PASSWORD, DOMAIN_VIRTUAL_IP, "imap").verify(STATUS_OK, IMAP_HOST, IMAP_PORT, null, null);
        doTest(ACCT_EMAIL+"/tb", PASSWORD, DOMAIN_VIRTUAL_IP, "pop3").verify(STATUS_OK, POP3_HOST, POP3_PORT, null, null);
    }
    
    public void testSupportedExtensionVirtualDomain() throws Exception {
        doTest(ACCT_LOCALPART+"/tb", PASSWORD, DOMAIN_VIRTUAL_IP, "imap").verify(STATUS_OK, IMAP_HOST, IMAP_PORT, ACCT_EMAIL+"/tb", null);
        doTest(ACCT_LOCALPART+"/tb", PASSWORD, DOMAIN_VIRTUAL_IP, "pop3").verify(STATUS_OK, POP3_HOST, POP3_PORT, ACCT_EMAIL+"/tb", null);
    }
    
    public void testUnsupportedExtension() throws Exception {
        doTest(ACCT_LOCALPART + "/zz", PASSWORD, DOMAIN_VIRTUAL_IP, "imap").verify(STATUS_ACCT_INFO_NOT_AVAIL, null, null, null, AUTH_WAIT);
        doTest(ACCT_LOCALPART + "/zz", PASSWORD, DOMAIN_VIRTUAL_IP, "pop3").verify(STATUS_ACCT_INFO_NOT_AVAIL, null, null, null, AUTH_WAIT);
    }
    
    // setup for lookup by foreign principal
    public void testLookupByForeignPrincipalInit() throws Exception {
        Map<String, Object> attrs = new HashMap<String, Object>();
        attrs.put(Provisioning.A_zimbraReverseProxyUserNameAttribute, "zimbraMailDeliveryAddress");
        attrs.put(Provisioning.A_zimbraReverseProxyMailHostQuery, "(zimbraForeignPrincipal=${USER})"); 
        modifyConfig(attrs);
    }
    
    public void testLookupByForeignPrincipalFullEmail()  throws Exception {
        // full email
        doTest(ACCT_FOREIGN_PRINCIPAL, PASSWORD, null, "imap").verify(STATUS_OK, IMAP_HOST, IMAP_PORT, ACCT_EMAIL, null);
        doTest(ACCT_FOREIGN_PRINCIPAL, PASSWORD, null, "pop3").verify(STATUS_OK, POP3_HOST, POP3_PORT, ACCT_EMAIL, null);
        
        // full email with supported extension
        doTest(ACCT_FOREIGN_PRINCIPAL+"/tb", PASSWORD, null, "imap").verify(STATUS_OK, IMAP_HOST, IMAP_PORT, ACCT_EMAIL+"/tb", null);
        doTest(ACCT_FOREIGN_PRINCIPAL+"/tb", PASSWORD, null, "pop3").verify(STATUS_OK, POP3_HOST, POP3_PORT, ACCT_EMAIL+"/tb", null);
        
        // full email with unsupported extension
        doTest(ACCT_FOREIGN_PRINCIPAL+"/zz", PASSWORD, null, "imap").verify(STATUS_ACCT_INFO_NOT_AVAIL, null, null, null, AUTH_WAIT);
        doTest(ACCT_FOREIGN_PRINCIPAL+"/zz", PASSWORD, null, "pop3").verify(STATUS_ACCT_INFO_NOT_AVAIL, null, null, null, AUTH_WAIT);
    }
    
    public void testLookupByForeignPrincipalVirtualDomain() throws Exception {

        // virtual domain by proxy IP
        doTest(FOREIGN_ID, PASSWORD, DOMAIN_VIRTUAL_IP, "imap").verify(STATUS_OK, IMAP_HOST, IMAP_PORT, ACCT_EMAIL, null);
        doTest(FOREIGN_ID, PASSWORD, DOMAIN_VIRTUAL_IP, "pop3").verify(STATUS_OK, POP3_HOST, POP3_PORT, ACCT_EMAIL, null);
    
        // virtual domain wrong proxy IP, the foreign id + default domain exists
        doTest(FOREIGN_ID, PASSWORD, "127.0.0.2", "imap").verify(STATUS_OK, IMAP_HOST, IMAP_PORT, ACCT2_EMAIL, null);
        doTest(FOREIGN_ID, PASSWORD, "127.0.0.2", "pop3").verify(STATUS_OK, POP3_HOST, POP3_PORT, ACCT2_EMAIL, null);

        // virtual domain wrong proxy IP, the foreign id + default domain does not exist
        doTest(FOREIGN_ID+"wrong", PASSWORD, "127.0.0.2", "imap").verify(STATUS_ACCT_INFO_NOT_AVAIL, null, null, null, AUTH_WAIT);
        doTest(FOREIGN_ID+"wrong", PASSWORD, "127.0.0.2", "pop3").verify(STATUS_ACCT_INFO_NOT_AVAIL, null, null, null, AUTH_WAIT);

        // virtual domain no proxy IP
        doTest(FOREIGN_ID, PASSWORD, null, "imap").verify(STATUS_OK, IMAP_HOST, IMAP_PORT, ACCT2_EMAIL, null);
        doTest(FOREIGN_ID, PASSWORD, null, "pop3").verify(STATUS_OK, POP3_HOST, POP3_PORT, ACCT2_EMAIL, null);

        // virtual domain with supported extension
        doTest(FOREIGN_ID+"/tb", PASSWORD, DOMAIN_VIRTUAL_IP, "imap").verify(STATUS_OK, IMAP_HOST, IMAP_PORT, ACCT_EMAIL+"/tb", null);
        doTest(FOREIGN_ID+"/tb", PASSWORD, DOMAIN_VIRTUAL_IP, "pop3").verify(STATUS_OK, POP3_HOST, POP3_PORT, ACCT_EMAIL+"/tb", null);
        
        // virtual domain with unsupported extension
        doTest(FOREIGN_ID+"/zz", PASSWORD, DOMAIN_VIRTUAL_IP, "imap").verify(STATUS_ACCT_INFO_NOT_AVAIL, null, null, null, AUTH_WAIT);
        doTest(FOREIGN_ID+"/zz", PASSWORD, DOMAIN_VIRTUAL_IP, "pop3").verify(STATUS_ACCT_INFO_NOT_AVAIL, null, null, null, AUTH_WAIT);

    }
    
    private static Result doTest(String user, String pass, String serverIp, String protocol) {
        HttpClient client = new HttpClient();
        GetMethod method = new GetMethod(URL);
        
        method.setRequestHeader("Host", "localhost");
        method.setRequestHeader(NginxLookupExtension.NginxLookupHandler.AUTH_METHOD, "plain");
        method.setRequestHeader(NginxLookupExtension.NginxLookupHandler.AUTH_USER, user);
        method.setRequestHeader(NginxLookupExtension.NginxLookupHandler.AUTH_PASS, pass);
        method.setRequestHeader(NginxLookupExtension.NginxLookupHandler.AUTH_PROTOCOL, protocol);
        method.setRequestHeader(NginxLookupExtension.NginxLookupHandler.AUTH_LOGIN_ATTEMPT, "1");
        method.setRequestHeader(NginxLookupExtension.NginxLookupHandler.CLIENT_IP, "127.0.0.1");
        
        if (serverIp != null)
            method.setRequestHeader(NginxLookupExtension.NginxLookupHandler.SERVER_IP, serverIp);
        
        try {
            int statusCode = client.executeMethod(method);
        
            Header authStatus = method.getResponseHeader(NginxLookupExtension.NginxLookupHandler.AUTH_STATUS);
            Header authServer = method.getResponseHeader(NginxLookupExtension.NginxLookupHandler.AUTH_SERVER);
            Header authPort = method.getResponseHeader(NginxLookupExtension.NginxLookupHandler.AUTH_PORT);
            Header authUser = method.getResponseHeader(NginxLookupExtension.NginxLookupHandler.AUTH_USER);
            Header authWait = method.getResponseHeader(NginxLookupExtension.NginxLookupHandler.AUTH_WAIT);
            
            return new Result(authStatus==null?null:authStatus.getValue(),
                              authServer==null?null:authServer.getValue(),
                              authPort==null?null:authPort.getValue(),
                              authUser==null?null:authUser.getValue(),
                              authWait==null?null:authWait.getValue());
            
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return null;
    }

    public static void main(String args[]) {

        TestNginxLookup t = new TestNginxLookup();
        t.run();
    
    }
}
