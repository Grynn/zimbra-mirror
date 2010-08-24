package framework.util;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpConnectionManager;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpRecoverableException;
import org.apache.commons.httpclient.HttpState;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.EntityEnclosingMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.dom4j.DocumentException;

import com.zimbra.common.auth.ZAuthToken;
import com.zimbra.common.net.SocketFactories;
import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.Element;
import com.zimbra.common.soap.SoapFaultException;
import com.zimbra.common.soap.SoapParseException;
import com.zimbra.common.soap.SoapProtocol;
import com.zimbra.common.soap.SoapUtil;
import com.zimbra.common.util.ByteUtil;

public class ZimbraAccount {
	private static Logger logger = LogManager.getLogger(ZimbraAccount.class);

	protected SoapClient soapClient = new SoapClient();
    public String DisplayName = null;
    public String ZimbraSoapClientHost = null;
    public String ZimbraSoapAdminHost = null;
    public String ZimbraMailHost = null;
    public String ZimbraId = null;
    public String CN = null;
    public String EmailAddress = null;
    public String Password = null;
    public String Alias = null;
    public String DomainName = null;

    /*
     * Create an account with the email address account<num>@<testdomain>
     * The password is set to config property "adminPwd"
     */
	public ZimbraAccount() {
		this(null, null);
	}
	
    /*
     * Create an account on the specified domain with the email address account<num>@<domain>
     * The password is set to config property "adminPwd"
     */
	public ZimbraAccount(String domain) {
		this(null, domain);
	}
	
    /*
     * Create an account with the email address <name>@<domain>
     * The password is set to config property "adminPwd"
     */
	public ZimbraAccount(String name, String domain) {
		
        CN = (name != null ? name : "account" + System.currentTimeMillis());
        DisplayName = CN;

        DomainName = (domain != null ? domain : ZimbraSeleniumProperties.getStringProperty("testdomain", "testdomain.com"));
        ZimbraMailHost = DomainName;
        EmailAddress = CN + "@" + DomainName;
        
        // TODO: Add a default password to the config.properties
        Password = ZimbraSeleniumProperties.getStringProperty("adminPwd", "test123");
        
	}
	
	/**
	 * Get the user account logged into ZWC being tested
	 * @return the ZimbraAccount object representing the test account
	 */
	public static ZimbraAccount AccountZWC() {
		// TODO: need to integrate with the harness and point an account at the test account
		logger.error("Implement me!");
		return(null);
	}
	
	/**
	 * Get a general use account for interacting with the test account
	 * @return a general use ZimbraAccount
	 */
	public static synchronized ZimbraAccount AccountA() {
		if ( _AccountA == null ) {
			_AccountA = new ZimbraAccount();
			_AccountA.provisionAccount();
			_AccountA.authenticate();
		}
		return (_AccountA);
	}
	private static ZimbraAccount _AccountA = null;
	
	/**
	 * Get a general use account for interacting with the test account
	 * @return a general use ZimbraAccount
	 */
	public static synchronized ZimbraAccount AccountB() {
		if ( _AccountB == null ) {
			_AccountB = new ZimbraAccount();
			_AccountB.provisionAccount();
			_AccountB.authenticate();
		}	
		return (_AccountB);
	}
	private static ZimbraAccount _AccountB = null;
	
	/**
	 * Creates the account on the ZCS using CreateAccountRequest
	 */
	public void provisionAccount() {
		try {
			ZimbraAdminAccount.GlobalAdmin().soapSend(
					"<CreateAccountRequest xmlns='urn:zimbraAdmin'>" +
			        	"<name>"+ EmailAddress +"</name>" +
			        	"<password>"+ Password +"</password>" +
			        "</CreateAccountRequest>");
			ZimbraId = ZimbraAdminAccount.GlobalAdmin().soapSelectValue("//admin:account", "id");
			ZimbraMailHost = ZimbraAdminAccount.GlobalAdmin().soapSelectValue("//admin:account/admin:a[@n='zimbraMailHost']", null);
		} catch (HarnessException e) {
			logger.error("Unable to provision account: "+ EmailAddress, e);
			ZimbraId = null;
			ZimbraMailHost = null;
		}
	}
	
	/**
	 * Creates the account on the ZCS with provided username and password
	 */
	public String provisionAccount(String user , String password) {
		String username = "";
		String locale = ZimbraSeleniumProperties.getStringProperty("locale");
		try {
			ZimbraAdminAccount.GlobalAdmin().soapSend(
					"<CreateAccountRequest xmlns='urn:zimbraAdmin'>" +
						"<name>" + user + "</name>" +
			        	"<password>" + password + "</password>" +
			        	"<a n='zimbraPrefLocale'>" + locale + "</a>" +
			        	"<a n='zimbraPrefAutoAddAddressEnabled'>FALSE</a>" +
			        	"<a n='zimbraPrefCalendarInitialView'>workWeek</a>" +
			        	"<a n='zimbraPrefCalendarApptReminderWarningTime'>0</a>" +
			        	"<a n='zimbraPrefTimeZoneId'>(GMT-08.00) Pacific Time</a>" +
			        	"<a n='zimbraFeatureReadReceiptsEnabled'>TRUE</a>" +
			        	"<a n='zimbraPrefCalendarAlwaysShowMiniCal'>FALSE</a>" +
			        	"<a n='zimbraPrefSkin'>beach</a>" +
			        	"<a n='zimbraPrefComposeFormat'>html</a>" +
			        	//"<a n='zimbraPrefReplyIncludeOriginalText'>includeBodyAndHeaders</a>" +
			        	//"<a n='zimbraPrefForwardIncludeOriginalText'>includeBodyAndHeaders</a>" +
			        "</CreateAccountRequest>");

			Element[] nodes = ZimbraAdminAccount.GlobalAdmin().soapSelectNodes("//admin:CreateAccountResponse");
	        if ( (nodes == null) || (nodes.length == 0)) {
	        	logger.error("Error occured during account provisioning, perhaps account already exists: "+ user);  
	        }
	        username  = ZimbraAdminAccount.GlobalAdmin().soapSelectValue("//admin:account", "name");
		} catch (Exception e) {
			logger.error("Unable to provision account: "+ user, e);			
		}			
		return username;		
	}
	
	/**
	 * Authenticates the account (using SOAP client AuthRequest)
	 * Sets the authToken
	 */
	public void authenticate() {
		try {
			soapSend(
					"<AuthRequest xmlns='urn:zimbraAccount'>" +
						"<account by='name'>"+ EmailAddress + "</account>" +
						"<password>"+ Password +"</password>" +
					"</AuthRequest>");
			String token = soapSelectValue("//acct:authToken", null);
			soapClient.setAuthToken(token);
		} catch (HarnessException e) {
			logger.error("Unable to authenticate "+ EmailAddress, e);
			soapClient.setAuthToken(null);
		}
	}
	
	/**
	 * Send a SOAP request from this account
	 * @param request the SOAP request body (see ZimbraServer/docs/soap.txt)
	 * @return the response envelope
	 * @throws HarnessException on failure
	 */
	public Element soapSend(String request) throws HarnessException {
		
		// TODO: need to watch for certain SOAP requests, such
		// as ModifyPrefsRequest, which could trigger a client reload
		//
		
		try {
			return (soapClient.sendSOAP(ZimbraMailHost, request));
		} catch (DocumentException e) {
			throw new HarnessException(e);
		} catch (IOException e) {
			throw new HarnessException(e);
		} catch (ServiceException e) {
			throw new HarnessException(e);
		}
	}

	/**
	 * Match an xpath or regex from the last SOAP response
	 * if xpath == null, then use the root element - TODO: not yet supported
	 * if attr == null, value is element text.  if attr != null, value is attr value
	 * if regex == null, return true if xpath matches.  If regex != null, a regex to match against the value
	 * @param xpath
	 * @param attr
	 * @param regex
	 * @return
	 */
	public boolean soapMatch(String xpath, String attr, String regex) {
		
		// TODO: support xpath == null
		
		// Find all nodes that match the expath
		Element[] elements = soapClient.selectNodes(xpath);
		
		// If regex == null, return true if xpath matched elements
		if ( regex == null ) {
			return (elements.length > 0);
		}
		
		// Loop through all xpath matches, looking for values that may match
		for (Element e : elements) {
			
			String value;
			if ( attr == null ) {
				value = e.getText();
			} else {
				value = e.getAttribute("attr", null);
			}

			if ( value == null )
				continue; // No match in this element
			
			Pattern p = Pattern.compile(regex);
			Matcher m = p.matcher(value);
			if ( m.matches() )
				return (true); // Otherwise, continue on next element
		}
		
		return (false); // Never found a match
	}
	
	/**
	 * Get a value from the last SOAP response
	 * @param xpath
	 * @param attr
	 * @return if attr == null, the element text.  if attr != null, the attr text.
	 */
	public String soapSelectValue(String xpath, String attr) {
		return (soapClient.selectValue(xpath, attr, 1));
	}
	
	/**
	 * Get a list of elements from the last response that match an xpath
	 * @param xpath
	 * @return
	 */
	public Element[] soapSelectNodes(String xpath) {
	 
		return (soapClient.selectNodes(xpath));
	}

	/**
	 * Get an element that matches the last response
	 * @param xpath
	 * @param index 1-based index if xpath matches multiple elements
	 * @return
	 */
	public Element soapSelectNode(String xpath, int index) {
		return (soapClient.selectNode(xpath, index));
	}

	/**
	 * Return the last SOAP response
	 * @return the last SOAP envelope in prettyPrint() string format
	 */ 
	public String soapLastResponse() {
		return (soapClient.responseEnvelope == null ? "null" : soapClient.responseEnvelope.prettyPrint());
	}

	
	
    public static class SoapClient {
    	private Logger logger = LogManager.getLogger(SoapClient.class);
    	
    	protected String AuthToken = null;
    	protected String SessionId = null;
    	protected int SequenceNum = -1;
    	
    	protected Element requestEnvelope;
    	protected Element responseEnvelope;
    	protected Element requestContext;
    	protected Element requestBody;
    	
    	protected URI mURI = null;
    	
    	protected SoapProtocol mSoapProto = null;
    	protected ProxySoapHttpTransport mTransport = null;

    	private static boolean isEasySslInitiated = false;
    	
    	/**
    	 * Create a SOAP 1.2 client
    	 */
        public SoapClient() {
         
        	if ( mSoapProto == null ){
        		mSoapProto = SoapProtocol.Soap12;
        	}
        	
        	if ( !isEasySslInitiated ) {
        		SocketFactories.registerProtocols(true);
        		isEasySslInitiated = true;
        	}
        }
        
        /**
         * Set the Zimbra AuthToken
         * @param token - use null to clear the context
         * @return
         */
        public String setAuthToken(String token) {
        	return (AuthToken = token);
        }
        
        /**
         * Set the Zimbra SessionId
         * @param id
         * @return
         */
        public String setSessionId(String id) {
        	return (SessionId = id);
        }
        
        /**
         * Set the Zimbra SequenceNum
         * Use with the SessionId
         * @param num - default -1
         * @return
         */
        public int setSequenceNum(int num) {
        	return (SequenceNum = num);
        }
        
        
        protected Element setContext(String token, String sessionId, int sequenceId) {
        	if ( token == null ) {
        		requestContext = null;
        	} else {
    			ZAuthToken zat = new ZAuthToken(null, token, null);
    			if ( sessionId == null )
    			{
    				requestContext = SoapUtil.toCtxt(mSoapProto, zat);
    			}
    			else 
    			{
    				requestContext = SoapUtil.toCtxt(mSoapProto, zat, sessionId, sequenceId);
    				if ( sequenceId != -1 ) {
    					Element e = requestContext.addElement("notify");
    					e.addAttribute("seq", String.valueOf(sequenceId));
    				}
    			}
    			        		
        	}
        	return (requestContext);
        }
        
        /**
         * Send the specified Zimbra SOAP request to the specified host
         * @param host
         * @param request
         * @return
         * @throws DocumentException
         * @throws IOException
         * @throws ServiceException
         * @throws HarnessException
         */
		public Element sendSOAP(String host, String request) throws DocumentException, IOException, ServiceException, HarnessException {        	
        	setContext(AuthToken, SessionId, SequenceNum);
        	return (sendSOAP(host, requestContext, Element.parseXML(request)));
        }
        
		/**
		 * Send a Zimbra SOAP context/request to the host
		 * @param host
		 * @param context
		 * @param request
		 * @return
		 * @throws IOException
		 * @throws ServiceException
		 * @throws HarnessException
		 */
        public Element sendSOAP(String host, Element context, Element request) throws IOException, ServiceException, HarnessException {
        	
        	setTransport(host, request);
        	
        	// Remember the context, request, envelope and response for logging purposes
        	requestBody = request;
        	requestEnvelope = mSoapProto.soapEnvelope(requestBody, context);

			responseEnvelope = mTransport.invokeRaw(requestEnvelope);
			
			// Log the request/response
        	logger.debug("\n" + new Date() +" "+ mURI.toString() +"\n---\n"+ requestEnvelope.prettyPrint() +"\n---\n"+ responseEnvelope.prettyPrint() +"\n---\n");
        	
			
			return (responseEnvelope);
        }
        
        /**
         * Return an array of elements from the last received SOAP response that match the xpath
         * @param xpath
         * @return
         */
        public Element[] selectNodes(String xpath) {
        	return (selectNodes(responseEnvelope, xpath));
        }
        
        /**
         * Return the first matching element from the context that match the xpath
         * @param context
         * @param xpath
         * @return
         * @throws HarnessException 
         */
        public static Element selectNode(Element context, String xpath) {
        	Element[] nodes = selectNodes(context, xpath);
        	if (nodes == null)
        		return (null);
        	return (nodes[0]);        		
        }
        
        /**
         * Return an array of elements from the context that match the xpath
         * @param context
         * @param xpath
         * @return
         * @throws HarnessException 
         */
        @SuppressWarnings("unchecked")
		public static Element[] selectNodes(Element context, String xpath) {
        	if ( context == null )
        		return (null);
    		org.dom4j.Element d4context = context.toXML();
    		org.dom4j.XPath Xpath = d4context.createXPath(xpath);
    		Xpath.setNamespaceURIs(getURIs());
    		org.dom4j.Node node;
    		List dom4jElements = Xpath.selectNodes(d4context);

    		List<Element> zimbraElements = new ArrayList<Element>();
    		Iterator iter = dom4jElements.iterator();
    		while (iter.hasNext()) {
    			node = (org.dom4j.Node)iter.next();
    			if (node instanceof org.dom4j.Element) {
    				Element zimbraElement = Element.convertDOM((org.dom4j.Element) node);
    				zimbraElements.add(zimbraElement);
    			}
    		}

    		Element[] retVal = new Element[zimbraElements.size()];
    		zimbraElements.toArray(retVal);
    		return retVal;
        }
        
        /**
         * Return the element from the last received SOAP response that matches the xpath
         * @param xpath
         * @param index - 1 based index
         * @return
         */
        public Element selectNode(String xpath, int index) {
        	return(selectNode(responseEnvelope, xpath, index));
        }
        
        /**
         * Return the element from context that matches the xpath
         * @param context
         * @param xpath
         * @param index
         * @return
         */
		public Element selectNode(Element context, String xpath, int index) {
			Element[] nodes = selectNodes(context, xpath);
			if (nodes == null)
				return (null);
			if ( nodes.length < index )
				return (null);
			return (nodes[index - 1]);
		}
		
		/**
		 * Return the 
		 * @param xpath
		 * @param attr
		 * @param index
		 * @return
		 */
		public String selectValue(String xpath, String attr, int index) {
			return (selectValue(responseEnvelope, xpath, attr, index));
		}

		public String selectValue(Element context, String xpath, String attr, int index) {
			
			Element[] elements = null;
			if ( xpath == null ) {
				
				// no xpath - use the entire context
				elements = new Element[1];
				elements[0] = context;
				
			} else {
				
				// xpath specfied - only use the matching nodes
				elements = selectNodes(context, xpath);
			
			}

			// Make sure we have elements
			if ( elements == null )
				return (null);
			
			// Make sure we have at least the specified index
			if ( elements.length < index )
				return (null);
			
			
			// Only use the element corresponding to the specified index
			Element element = elements[index - 1];
			String value = null;

			if ( attr == null ) {
				value = element.getText();
			} else {
				value = element.getAttribute(attr, null);
			}

			return (value);
		}

        protected void setTransport(String host, Element request) throws HarnessException {

    		// Only set the transport if the URI changes
        	if ( setURI(host, request) ) {


                synchronized (mSoapProto) {

                    if (mTransport != null) {

                    	logger.debug("mTransport shutting down");

                		mTransport.shutdown();
                        mTransport = null;
                        
                    }

                	mTransport = new ProxySoapHttpTransport(mURI.toString());
                    
            		logger.debug("mTransport pointing at " + mURI);
                    
                }                 

        	}
        }

        protected boolean setURI(String host, Element request) throws HarnessException {
        	
        	// TODO: need to get URI settings from config.properties
        	
        	
        	String scheme = "http";
        	String userInfo = null;
        	int port = 80;
        	String path = "/";
        	String query = null;
        	String fragment = null;
        	
        	String namespace = getNamespace(request);
        	if ( namespace.equals("urn:zimbraAdmin") ) {

        		// https://server.com:7071/service/admin/soap/

        		scheme = "https";
        		port = 7071;
        		path = "/service/admin/soap/";
        		
        	} else if ( namespace.equals("urn:zimbraAccount") ) {

        		// http://server.com:80/service/soap/

        		path = "/service/soap/";
        		
        	} else if ( namespace.equals("urn:zimbraMail") ) {
        		
        		// http://server.com:80/service/soap/
        		
        		path = "/service/soap/";
        		
        	} else {
        		throw new HarnessException("Unsupported qname: "+ namespace +".  Need to implement setURI for it.");
        	}
        	
        	try {
				URI uri = new URI(scheme, userInfo, host, port, path, query, fragment);
				if ( uri.equals(mURI) ) {
					return (false); // URI didn't change
				} else {
					mURI = uri;
					return (true);
				}
			} catch (URISyntaxException e) {
				throw new HarnessException("Unable to create SOAP URI", e);
			}
			
		}

        protected static final Pattern mNamespacePattern = Pattern.compile("(xmlns=\\\"([^\"]+)\\\")");
        protected String getNamespace(Element e) {
            Matcher matcher = mNamespacePattern.matcher(e.toString());
    		while (matcher.find()) {
    			return (matcher.group(2));
    		}
    		return (null);
        }
/*        
		protected Element[] getElementsFromPath(Element context, String path) {
    		org.dom4j.Element d4context = context.toXML();
    		org.dom4j.XPath xpath = d4context.createXPath(path);
    		xpath.setNamespaceURIs(getURIs());
    		org.dom4j.Node node;
    		List dom4jElements = xpath.selectNodes(d4context);

    		List<Element> zimbraElements = new ArrayList<Element>();
    		Iterator iter = dom4jElements.iterator();
    		while (iter.hasNext()) {
    			node = (org.dom4j.Node)iter.next();
    			if (node instanceof org.dom4j.Element) {
    				Element zimbraElement = Element.convertDOM((org.dom4j.Element) node);
    				zimbraElements.add(zimbraElement);
    			}
    		}

    		Element[] retVal = new Element[zimbraElements.size()];
    		zimbraElements.toArray(retVal);
    		return retVal;
        }

*/    	
        
        private static Map<String, String> mURIs = null;
    	private static Map getURIs() {
    		if (mURIs == null) {
    			mURIs = new HashMap<String, String>();
    			mURIs.put("zimbra", "urn:zimbra");
    			mURIs.put("acct", "urn:zimbraAccount");
    			mURIs.put("mail", "urn:zimbraMail");
    			mURIs.put("offline", "urn:zimbraOffline");
    			mURIs.put("admin", "urn:zimbraAdmin");
    			mURIs.put("voice", "urn:zimbraVoice");
    			mURIs.put("im", "urn:zimbraIM");
    			mURIs.put("mapi", "urn:zimbraMapi");
    			mURIs.put("sync", "urn:zimbraSync");
    			mURIs.put("cs", "urn:zimbraCS");
    			mURIs.put("test", "urn:zimbraTestHarness");
    			mURIs.put("soap", "http://www.w3.org/2003/05/soap-envelope");
    			mURIs.put("soap12", "http://www.w3.org/2003/05/soap-envelope");
    			mURIs.put("soap11", "http://schemas.xmlsoap.org/soap/envelope/");
    		}
    		return mURIs;
    	}

    }

    public static class ProxySoapHttpTransport extends com.zimbra.common.soap.SoapTransport {

        private static final String X_ORIGINATING_IP = "X-Originating-IP";
        
        private boolean mKeepAlive;
        private int mRetryCount;
        private int mTimeout;
        private String mUri;
    	private HttpClient mClient;
    	public String mAuthToken = null;
        
        public String toString() { 
            return "ProxySoapHttpTransport(uri="+mUri+")";
        }

        private static final HttpClientParams sDefaultParams = new HttpClientParams();
            static {
                // we're doing the retry logic at the SoapHttpTransport level, so don't do it at the HttpClient level as well
                sDefaultParams.setParameter(HttpMethodParams.RETRY_HANDLER, new HttpMethodRetryHandler() {
                    public boolean retryMethod(HttpMethod method, IOException exception, int executionCount)  { return false; }
                });
            }

        /**
         * Create a new SoapHttpTransport object for the specified URI.
         * Supported schemes are http and https. The connection
         * is not made until invoke or connect is called.
         *
         * Multiple threads using this transport must do their own
         * synchronization.
         */
        public ProxySoapHttpTransport(String uri) {
        	this(uri, null, 0);
        }
        
        /**
         * Create a new SoapHttpTransport object for the specified URI, with specific proxy information.
         * 
         * @param uri the origin server URL
         * @param proxyHost hostname of proxy
         * @param proxyPort port of proxy
         */
        public ProxySoapHttpTransport(String uri, String proxyHost, int proxyPort) {
        	this(uri, proxyHost, proxyPort, null, null);
        }
        
        /**
         * Create a new SoapHttpTransport object for the specified URI, with specific proxy information including
         * proxy auth credentials.
         * 
         * @param uri the origin server URL
         * @param proxyHost hostname of proxy
         * @param proxyPort port of proxy
         * @param proxyUser username for proxy auth
         * @param proxyPass password for proxy auth
         */
        public ProxySoapHttpTransport(String uri, String proxyHost, int proxyPort, String proxyUser, String proxyPass) {
        	super();
        	mClient = new HttpClient(sDefaultParams);
        	commonInit(uri);
        	
        	if (proxyHost != null && proxyHost.length() > 0 && proxyPort > 0) {
        		mClient.getHostConfiguration().setProxy(proxyHost, proxyPort);
        		if (proxyUser != null && proxyUser.length() > 0 && proxyPass != null && proxyPass.length() > 0) {
        			mClient.getState().setProxyCredentials(new AuthScope(proxyHost, proxyPort), new UsernamePasswordCredentials(proxyUser, proxyPass));
        		}
        	}
        }

        /**
         * Creates a new SoapHttpTransport that supports multiple connections
         * to the specified URI.  Multiple threads can call the invoke()
         * method safely without synchronization.
         *
         * @param uri
         * @param maxConnections Note RFC2616 recommends the default of 2.
         */
        public ProxySoapHttpTransport(String uri, int maxConnections, boolean connectionStaleCheckEnabled) {
        	super();
        	MultiThreadedHttpConnectionManager connMgr = new MultiThreadedHttpConnectionManager();
        	connMgr.setMaxConnectionsPerHost(maxConnections);
        	connMgr.setConnectionStaleCheckingEnabled(connectionStaleCheckEnabled);
        	mClient = new HttpClient(sDefaultParams, connMgr);
        	commonInit(uri);
        }

        /**
         * Frees any resources such as connection pool held by this transport.
         */
        public void shutdown() {
        	HttpConnectionManager connMgr = mClient.getHttpConnectionManager();
        	if (connMgr instanceof MultiThreadedHttpConnectionManager) {
        		MultiThreadedHttpConnectionManager multiConnMgr = (MultiThreadedHttpConnectionManager) connMgr;
        		multiConnMgr.shutdown();
        	}
        	mClient = null;
        }

        private void commonInit(String uri) {
            mUri = uri;
            mKeepAlive = false;
            mRetryCount = 3;
            setTimeout(0);
        }

        /**
         *  Gets the URI
         */
        public String getURI() {
            return mUri;
        }
        
        /**
         * The number of times the invoke method retries when it catches a 
         * RetryableIOException.
         *
         * <p> Default value is <code>3</code>.
         */
        public void setRetryCount(int retryCount) {
            this.mRetryCount = retryCount;
        }


        /**
         * Get the mRetryCount value.
         */
        public int getRetryCount() {
            return mRetryCount;
        }

        /**
         * Whether or not to keep the connection alive in between
         * invoke calls.
         *
         * <p> Default value is <code>false</code>.
         */
        private void setKeepAlive(boolean keepAlive) {
            this.mKeepAlive = keepAlive;
        }

        /**
         * Get the mKeepAlive value.
         */
        private boolean getKeepAlive() {
            return mKeepAlive;
        }

        /**
         * The number of miliseconds to wait when connecting or reading
         * during a invoke call. 
         * <p>
         * Default value is <code>0</code>, which means no mTimeout.
         */
        public void setTimeout(int timeout) {
            mTimeout = timeout;
            mClient.setConnectionTimeout(mTimeout);
            mClient.setTimeout(mTimeout);
        }

        /**
         * Get the mTimeout value.
         */
        public int getTimeout() {
            return mTimeout;
        }

        public Element invoke(Element document, boolean raw, boolean noSession, String requestedAccountId, String changeToken, String tokenType) 
    	throws SoapFaultException, IOException, HttpException {
        	int statusCode = -1;

            PostMethod method = null;
            try {
                // the content-type charset will determine encoding used
                // when we set the request body
                method = new PostMethod(mUri);
                method.setRequestHeader("Content-Type", getRequestProtocol().getContentType());
                if (getClientIp() != null)
                method.setRequestHeader(X_ORIGINATING_IP, getClientIp());

                Element soapReq = generateSoapMessage(document, raw, noSession, requestedAccountId, changeToken, tokenType);
                String soapMessage = SoapProtocol.toString(soapReq, getPrettyPrint());
                method.setRequestBody(soapMessage);
                method.setRequestContentLength(EntityEnclosingMethod.CONTENT_LENGTH_AUTO);
        	
                if (getRequestProtocol().hasSOAPActionHeader())
                    method.setRequestHeader("SOAPAction", mUri);

        		if ( mAuthToken != null )
    			{
        			HttpState initialState = new HttpState();
        			String mUriHost = "";
    				try {
    					mUriHost = (new URI(mUri)).getHost();
    	        		Cookie authCookie = new Cookie(mUriHost, "ZM_AUTH_TOKEN", mAuthToken, "/", null, false);
    	        		initialState.addCookie(authCookie);
    	        		mClient.setState(initialState);
    				} catch (URISyntaxException e) {
    					// TODO: how to handle this?
    				}

    			}
        		
                for (int attempt = 0; statusCode == -1 && attempt < mRetryCount; attempt++) {
                    try {
                        // execute the method.
                        statusCode = mClient.executeMethod(method);
                    } catch (HttpRecoverableException e) {
                        if (attempt == mRetryCount - 1)
                            throw e;
                        System.err.println("A recoverable exception occurred, retrying." + e.getMessage());
                    }
                }

                // Read the response body.  Use the stream API instead of the byte[] one
                // to avoid HTTPClient whining about a large response.
                byte[] responseBody = ByteUtil.getContent(method.getResponseBodyAsStream(), (int) method.getResponseContentLength());

                // Deal with the response.
                // Use caution: ensure correct character encoding and is not binary data
                String responseStr = SoapProtocol.toString(responseBody);

                try {
                	return parseSoapResponse(responseStr, raw);
                } catch (SoapFaultException x) {
                	//attach request/response to the exception and rethrow for downstream consumption
                	x.setFaultRequest(soapMessage);
                	x.setFaultResponse(responseStr);
                	throw x;
                }
            } finally {
                // Release the connection.
                if (method != null)
                    method.releaseConnection();        
            }
        }

        Element parseSoapResponse(String envelopeStr, boolean raw) throws SoapParseException, SoapFaultException {
            Element env;
            try {
                if (envelopeStr.trim().startsWith("<"))
                    env = Element.parseXML(envelopeStr);
                else
                    env = Element.parseJSON(envelopeStr);
            } catch (DocumentException de) {
                throw new SoapParseException("unable to parse response", envelopeStr);
            }
            
            //if (mDebugListener != null) mDebugListener.receiveSoapMessage(env);

            return raw ? env : extractBodyElement(env);
        }


    }

	/**
	 * @param args
	 * @throws HarnessException 
	 */
	public static void main(String[] args) throws HarnessException {
		String domain = ZimbraSeleniumProperties.getStringProperty("server","qa60.lab.zimbra.com");
		
		// Configure log4j using the basic configuration
		BasicConfigurator.configure();
		
		// Create a new account object
		ZimbraAccount account = new ZimbraAccount("foo"+System.currentTimeMillis(), domain);
		
		// Provision it on the server
		account.provisionAccount();
		
		// Get the SOAP authToken
		account.authenticate();
		
		// Send a basic SOAP request.  Check the response.
		account.soapSend("<NoOpRequest xmlns='urn:zimbraMail'/>");
		if ( !account.soapMatch("//mail:NoOpResponse", null, null) )
			throw new HarnessException("NoOpRequest did not return NoOpResponse");
		
		// Add a message to the mailbox.  Check the response
		account.soapSend(
					"<SendMsgRequest xmlns='urn:zimbraMail'>" +
						"<m>" +
							"<e t='t' a='"+ account.EmailAddress +"'/>" +
							"<su>subject123</su>" +
							"<mp ct='text/plain'>" +
								"<content>content123</content>" +
							"</mp>" +
						"</m>" +
					"</SendMsgRequest>");
		if ( !account.soapMatch("//mail:SendMsgResponse", null, null) )
			throw new HarnessException("SendMsgRequest did not return SendMsgResponse");

		logger.info("Done!");

	}

}
