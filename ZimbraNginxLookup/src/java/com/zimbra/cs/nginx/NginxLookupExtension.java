package com.zimbra.cs.nginx;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.util.StringUtil;
import com.zimbra.common.util.ZimbraLog;
import com.zimbra.cs.account.Config;
import com.zimbra.cs.account.Provisioning;
import com.zimbra.cs.account.ldap.LdapUtil;
import com.zimbra.cs.extension.ExtensionDispatcherServlet;
import com.zimbra.cs.extension.ExtensionHttpHandler;
import com.zimbra.cs.extension.ZimbraExtension;

public class NginxLookupExtension implements ZimbraExtension {

	public static final String NAME = "nginx-lookup";
	
    public String getName() {
    	return NAME;
    }
    
	public void init() throws ServiceException {
        ExtensionDispatcherServlet.register(this, new NginxLookupHandler());
	}
	
	public void destroy() {
        ExtensionDispatcherServlet.unregister(this);
	}

	@SuppressWarnings("serial")
	public static class NginxLookupException extends Exception {
		public NginxLookupException(String msg) {
			super(msg);
		}
	}
	private static class NginxLookupRequest {
		String user;
		String pass;
		String proto;
		String clientIp;
		int loginAttempt;
		HttpServletRequest  httpReq;
		HttpServletResponse httpResp;
	}
	public static class NginxLookupHandler extends ExtensionHttpHandler {
		/* req headers */
		public static final String AUTH_METHOD        = "Auth-Method";
		public static final String AUTH_USER          = "Auth-User";
		public static final String AUTH_PASS          = "Auth-Pass";
		public static final String AUTH_PROTOCOL      = "Auth-Protocol";
		public static final String AUTH_LOGIN_ATTEMPT = "Auth-Login-Attempt";
		public static final String CLIENT_IP          = "Client-IP";
		
		/* resp headers */
		public static final String AUTH_STATUS = "Auth-Status";
		public static final String AUTH_SERVER = "Auth-Server";
		public static final String AUTH_PORT   = "Auth-Port";
		public static final String AUTH_WAIT   = "Auth-Wait";

		public static final String WAIT_INTERVAL = "10";

		/* protocols */
		public static final String IMAP     = "imap";
		public static final String IMAP_SSL = "imapssl";
		public static final String POP3     = "pop3";
		public static final String POP3_SSL = "pop3ssl";
		
	    private static final SearchControls USER_SC   = new SearchControls(SearchControls.SUBTREE_SCOPE, 1, 0, null, false, false);
	    private static final SearchControls SERVER_SC = new SearchControls(SearchControls.SUBTREE_SCOPE, 1, 0, null, false, false);
	    
	    public boolean hideFromDefaultPorts() {
	    	return true;
	    }
	    
	    public void init(ZimbraExtension ext) throws ServiceException {
	    	super.init(ext);
	    	Config config = Provisioning.getInstance().getConfig();
	    	String attr;
	    	ArrayList<String> attrs = new ArrayList<String>();
	    	attr = config.getAttr(Provisioning.A_zimbraProxyMailHostAttribute);
	    	if (attr != null) {
	    		attrs.add(attr);
	    		USER_SC.setReturningAttributes(attrs.toArray(new String[0]));
	    	}
	    	attrs.clear();
	    	attr = config.getAttr(Provisioning.A_zimbraProxyPop3PortAttribute);
	    	if (attr != null)
	    		attrs.add(attr);
	    	attr = config.getAttr(Provisioning.A_zimbraProxyPop3SSLPortAttribute);
	    	if (attr != null)
	    		attrs.add(attr);
	    	attr = config.getAttr(Provisioning.A_zimbraProxyImapPortAttribute);
	    	if (attr != null)
	    		attrs.add(attr);
	    	attr = config.getAttr(Provisioning.A_zimbraProxyImapSSLPortAttribute);
	    	if (attr != null)
	    		attrs.add(attr);
	    	if (attrs.size() > 0)
	    		SERVER_SC.setReturningAttributes(attrs.toArray(new String[0]));
	    }
	    
	    public void doGet(HttpServletRequest httpReq, HttpServletResponse resp) throws IOException, ServletException {
	    	try {
	    		NginxLookupRequest req = checkRequest(httpReq);
	    		req.httpReq  = httpReq;
	    		req.httpResp = resp;
	    		search(req);
	    	} catch (NginxLookupException ex) {
	    		sendError(resp, ex.getMessage());
	    	}
	    }

	    private NginxLookupRequest checkRequest(HttpServletRequest httpReq) throws NginxLookupException {
	    	NginxLookupRequest req = new NginxLookupRequest();
	    	req.user = httpReq.getHeader(AUTH_USER);
	    	req.pass = httpReq.getHeader(AUTH_PASS);
	    	req.proto = httpReq.getHeader(AUTH_PROTOCOL);
	    	if (req.user == null)
	    		throw new NginxLookupException("missing header field "+AUTH_USER);
	    	if (req.pass == null)
	    		throw new NginxLookupException("missing header field "+AUTH_PASS);
	    	if (req.proto == null)
	    		throw new NginxLookupException("missing header field "+AUTH_PROTOCOL);
	    	req.clientIp = httpReq.getHeader(CLIENT_IP);
	    	String val = httpReq.getHeader(AUTH_LOGIN_ATTEMPT);
	    	if (val != null) {
	    		try {
	    			req.loginAttempt = Integer.parseInt(val);
	    		} catch (NumberFormatException e) {
	    		}
	    	}
	    	return req;
	    }
	    
	    private String lookupAttr(Config config, SearchResult sr, String key) throws NginxLookupException, NamingException {
            String attr = config.getAttr(key);
            if (attr == null)
            	throw new NginxLookupException("missing attr in config: "+key);
            String val = LdapUtil.getAttrString(sr.getAttributes(), attr);
            if (val == null)
            	throw new NginxLookupException("missing attr in search result: "+attr);
            return val;
	    }
	    
	    private String getAttrForProto(String proto) throws NginxLookupException {
	    	if (IMAP.equalsIgnoreCase(proto))
	    		return Provisioning.A_zimbraProxyImapPortAttribute;
	    	else if (IMAP_SSL.equalsIgnoreCase(proto))
	    		return Provisioning.A_zimbraProxyImapSSLPortAttribute;
	    	else if (POP3.equalsIgnoreCase(proto))
	    		return Provisioning.A_zimbraProxyPop3PortAttribute;
	    	else if (POP3_SSL.equalsIgnoreCase(proto))
	    		return Provisioning.A_zimbraProxyPop3SSLPortAttribute;
	    	else
	    		throw new NginxLookupException("unsupported protocol: "+proto);
	    	
	    }
	    
	    private String searchDirectory(DirContext ctxt, SearchControls sc, Config config, String queryTemplate, String searchBase, String templateKey, String templateVal, String attr) throws NginxLookupException, NamingException {
    		HashMap<String, String> kv = new HashMap<String,String>();
	    	kv.put(templateKey, LdapUtil.escapeSearchFilterArg(templateVal));
	    	String query = config.getAttr(queryTemplate);
	    	String base  = config.getAttr(searchBase);
	    	if (query == null)
	    		throw new NginxLookupException("empty attribute: "+queryTemplate);
	    	query = StringUtil.fillTemplate(query, kv);
	    	if (base == null)
	    		base = "";

	    	//ZimbraLog.extensions.debug("nginxlookup: query="+query);
    		NamingEnumeration ne = LdapUtil.searchDir(ctxt, base, query, sc);
	    	try {
	    		if (!ne.hasMore())
	    			throw new NginxLookupException("query returned empty result: "+query);
	    		SearchResult sr = (SearchResult) ne.next();
	    		return lookupAttr(config, sr, attr);
	    	} finally {
	    		if (ne != null)
	    			ne.close();
	    	}
	    }
	    
	    private void search(NginxLookupRequest req) throws NginxLookupException {
    		DirContext ctxt = null;
	    	try {
		    	ctxt = LdapUtil.getDirContext();
		    	Config config = Provisioning.getInstance().getConfig();

		    	String mailhost = searchDirectory(
		    			ctxt, 
		    			USER_SC, 
		    			config, 
		    			Provisioning.A_zimbraProxyMailHostQuery,
		    			Provisioning.A_zimbraProxyMailHostSearchBase,
		    			"USER",
		    			req.user,
		    			Provisioning.A_zimbraProxyMailHostAttribute);

		    	if (mailhost == null)
		    		throw new NginxLookupException("mailhost not found for user: "+req.user);
		    	String addr = InetAddress.getByName(mailhost).getHostAddress();
		    	ZimbraLog.extensions.debug("nginxlookup: mailhost="+mailhost+" ("+addr+")");
		    	String port = null;
		    	try {
		    		port = searchDirectory(
		    				ctxt, 
		    				SERVER_SC, 
		    				config, 
		    				Provisioning.A_zimbraProxyPortQuery,
		    				Provisioning.A_zimbraProxyPortSearchBase,
		    				"MAILHOST",
		    				mailhost,
		    				getAttrForProto(req.proto));
		    	} catch (NginxLookupException e) {
		    		// the server does not have bind port overrides.
			    	ZimbraLog.extensions.debug("nginxlookup: using port from globalConfig");
			    	String lookupAttr = getAttrForProto(req.proto);
			    	String bindPortAttr = config.getAttr(lookupAttr);
			    	if (bindPortAttr == null)
			    		throw new NginxLookupException("missing config attr: "+lookupAttr);
		    		port = config.getAttr(bindPortAttr);
			    	if (bindPortAttr == null)
			    		throw new NginxLookupException("missing config attr: "+bindPortAttr);
		    	}

		    	ZimbraLog.extensions.debug("nginxlookup: port="+port);
		    	sendResult(req.httpResp, addr, port);
	        } catch (ServiceException e) {
	    		throw new NginxLookupException("service exception: "+e.getMessage());
	        } catch (NamingException e) {
	    		throw new NginxLookupException("naming exception: "+e.getMessage());
	        } catch (UnknownHostException e) {
	    		throw new NginxLookupException("naming exception: "+e.getMessage());
	        } finally {
	        	if (ctxt != null)
	        		LdapUtil.closeContext(ctxt);
	        }
	    }
	    
	    private void sendResult(HttpServletResponse resp, String server, String port) {
	    	resp.setStatus(HttpServletResponse.SC_OK);
	    	resp.addHeader(AUTH_STATUS, "OK");
	    	resp.addHeader(AUTH_SERVER, server);
	    	resp.addHeader(AUTH_PORT, port);
	    }
	    
	    private void sendError(HttpServletResponse resp, String msg) {
	    	resp.setStatus(HttpServletResponse.SC_OK);
	    	resp.addHeader(AUTH_STATUS, msg);
	    	resp.addHeader(AUTH_WAIT, WAIT_INTERVAL);
	    }
	}
}
