package framework.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
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

import javax.activation.MimetypesFileTypeMap;

import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpConnectionManager;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpRecoverableException;
import org.apache.commons.httpclient.HttpState;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.EntityEnclosingMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.PartBase;
import org.apache.commons.httpclient.methods.multipart.StringPart;
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

import framework.core.DevEnvironment;

@SuppressWarnings("deprecation")
public class ZimbraAccount {
	private static Logger logger = LogManager.getLogger(ZimbraAccount.class);

	protected UploadClient uploadClient = new UploadClient();
	protected SoapClient soapClient = new SoapClient();
    public String ZimbraSoapClientHost = null;
    public String ZimbraSoapAdminHost = null;
    public String ZimbraMailHost = null;
    public String ZimbraId = null;
    public String CN = null;
    public String DisplayName = null;
    public String EmailAddress = null;
    public String Password = null;
    public Map<String, String> preferences = null;

    
    
    /*
     * Create an account with the email address account<num>@<testdomain>
     * The password is set to config property "adminPwd"
     */
	public ZimbraAccount() {
		this(null, null);
	}
	
    /*
     * Create an account with the email address <name>@<domain>
     * The password is set to config property "adminPwd"
     */
	public ZimbraAccount(String email, String password) {
		
		if ( email == null ) {
			CN = ZimbraSeleniumProperties.getStringProperty("locale").toLowerCase().replace("_", "") + ZimbraSeleniumProperties.getUniqueString();
			DisplayName = CN;
			email = CN + "@" + ZimbraSeleniumProperties.getStringProperty("testdomain", "testdomain.com");
		} else {
			CN = email.split("@")[0];
			DisplayName = CN;
		}
		EmailAddress = email;
		
		if ( password == null ) {
			password = ZimbraSeleniumProperties.getStringProperty("adminPwd", "test123");
		}
		Password = password;
		        
        
	}
	
	/**
	 * Get the user account logged into ZWC being tested
	 * @return the ZimbraAccount object representing the test account
	 */
	public static synchronized ZimbraAccount AccountZWC() {
		if ( _AccountZWC == null ) {
			_AccountZWC = new ZimbraAccount();
			_AccountZWC.provision();
			_AccountZWC.authenticate();
		}
		return (_AccountZWC);
	}
	public static synchronized void ResetAccountZWC() {
		_AccountZWC = null;
	}
	private static ZimbraAccount _AccountZWC = null;

	public static synchronized ZimbraAccount AccountZMC() {
		if ( _AccountZMC == null ) {
			_AccountZMC = new ZimbraAccount();
			_AccountZMC.provision();
			_AccountZMC.authenticate();
		}
		return (_AccountZMC);
	}
	public static synchronized void ResetAccountZMC() {
		_AccountZMC = null;
	}
	private static ZimbraAccount _AccountZMC = null;

	/**
	 * Get a general use account for interacting with the test account
	 * @return a general use ZimbraAccount
	 */
	public static synchronized ZimbraAccount AccountA() {
		if ( _AccountA == null ) {
			_AccountA = new ZimbraAccount();
			_AccountA.provision();
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
			_AccountB.provision();
			_AccountB.authenticate();
		}	
		return (_AccountB);
	}
	private static ZimbraAccount _AccountB = null;
	
	
	// Set the default account settings
	@SuppressWarnings("serial")
	private static final Map<String, String> accountAttrs = new HashMap<String, String>() {{
		put("zimbraPrefLocale", ZimbraSeleniumProperties.getStringProperty("locale"));
		put("zimbraPrefAutoAddAddressEnabled", "FALSE");
		put("zimbraPrefCalendarInitialView", "workWeek");
		put("zimbraPrefCalendarApptReminderWarningTime", "0");
		put("zimbraPrefTimeZoneId", ZimbraSeleniumProperties.getStringProperty("zimbraPrefTimeZoneId", "America/Los_Angeles"));
		put("zimbraFeatureReadReceiptsEnabled", "TRUE");
		put("zimbraPrefCalendarAlwaysShowMiniCal", "FALSE");
		// put("zimbraPrefSkin", "beach");
		put("zimbraPrefComposeFormat", "html");
		
		put("zimbraZimletAvailableZimlets","+com_zimbra_email");
		put("zimbraZimletAvailableZimlets","+com_zimbra_webex");
		put("zimbraZimletAvailableZimlets","+com_zimbra_social");
		put("zimbraZimletAvailableZimlets","+com_zimbra_linkedin");

 	}};

	/**
	 * Creates the account on the ZCS using CreateAccountRequest
	 */
	public ZimbraAccount provision() {
		try {
			
			// Make sure domain exists
			String domain = EmailAddress.split("@")[1];
			ZimbraAdminAccount.GlobalAdmin().soapSend(
					"<CreateDomainRequest xmlns='urn:zimbraAdmin'>" +
	                	"<name>"+ domain +"</name>" +
	                "</CreateDomainRequest>");
			

			// Build the list of default preferences
			StringBuilder prefs = new StringBuilder();
    		for (Map.Entry<String, String> entry : accountAttrs.entrySet()) {
    			prefs.append(String.format("<a n='%s'>%s</a>", entry.getKey(), entry.getValue()));
    		}
    		prefs.append(String.format("<a n='%s'>%s</a>", "displayName", DisplayName));

			ZimbraAdminAccount.GlobalAdmin().soapSend(
					"<CreateAccountRequest xmlns='urn:zimbraAdmin'>" +
			        	"<name>"+ EmailAddress +"</name>" +
			        	"<password>"+ Password +"</password>" +
			        	prefs.toString() + 
			        "</CreateAccountRequest>");
			
			Element[] createAccountResponse = ZimbraAdminAccount.GlobalAdmin().soapSelectNodes("//admin:CreateAccountResponse");
			if ( (createAccountResponse == null) || (createAccountResponse.length == 0)) {
				logger.error("Error occured during account provisioning, perhaps account already exists: "+ EmailAddress);
				ZimbraAdminAccount.GlobalAdmin().soapSend(
						"<GetAccountRequest xmlns='urn:zimbraAdmin'>" +
							"<account by='name'>"+ EmailAddress + "</account>" +
						"</GetAccountRequest>");
				Element[] getAccountResponse = ZimbraAdminAccount.GlobalAdmin().soapSelectNodes("//admin:GetAccountResponse");
				if ( (getAccountResponse == null) || (getAccountResponse.length == 0)) {
					logger.error("Error occured during get account provisioning.  Now I'm really confused");
				} else {
					ZimbraId = ZimbraAdminAccount.GlobalAdmin().soapSelectValue("//admin:account", "id");
					ZimbraMailHost = ZimbraAdminAccount.GlobalAdmin().soapSelectValue("//admin:account/admin:a[@n='zimbraMailHost']", null);
					
				}
			} else {
				ZimbraId = ZimbraAdminAccount.GlobalAdmin().soapSelectValue("//admin:account", "id");
				ZimbraMailHost = ZimbraAdminAccount.GlobalAdmin().soapSelectValue("//admin:account/admin:a[@n='zimbraMailHost']", null);
			}
			
			// Start: Dev environment hack
			if ( DevEnvironment.isUsingDevEnvironment() ) {
				ZimbraMailHost = "localhost";
			}
			// End: Dev environment hack

			
		} catch (HarnessException e) {
			logger.error("Unable to provision account: "+ EmailAddress, e);
			ZimbraId = null;
			ZimbraMailHost = null;
		}
		return (this);
	}
	
	
	/**
	 * Authenticates the account (using SOAP client AuthRequest)
	 * Sets the authToken
	 */
	public ZimbraAccount authenticate() {
		try {
			soapSend(
					"<AuthRequest xmlns='urn:zimbraAccount'>" +
						"<account by='name'>"+ EmailAddress + "</account>" +
						"<password>"+ Password +"</password>" +
					"</AuthRequest>");
			String token = soapSelectValue("//acct:authToken", null);
			soapClient.setAuthToken(token);
			uploadClient.setAuthToken(token);
		} catch (HarnessException e) {
			logger.error("Unable to authenticate "+ EmailAddress, e);
			soapClient.setAuthToken(null);
			uploadClient.setAuthToken(null);
		}
		return (this);
	}
	
	/**
	 * Modify a user prefence using ModifyPrefsRequest
	 * @throws HarnessException 
	 */
	public ZimbraAccount modifyPreference(String pref, String value) {
		try
		{
			soapSend(
					"<ModifyPrefsRequest xmlns='urn:zimbraAccount'>" +
					"<pref name='"+ pref +"'>"+ value +"</pref>" +
			"</ModifyPrefsRequest>");

			Element[] response = soapSelectNodes("//acct:ModifyPrefsResponse");
			if ( response == null || response.length != 1 )
				throw new HarnessException("Unable to modify preference "+ soapLastResponse());
		} catch (HarnessException e) {
			logger.error("Unable to modify preference", e);
		}
		return (this);

	}
	
	/**
	 * Get a user preference value
	 */
	public String getPreference(String pref) throws HarnessException {
		
		soapSend(
				"<GetPrefsRequest xmlns='urn:zimbraAccount'>" +
                	"<pref name='"+ pref +"'/>" +
                "</GetPrefsRequest>");
		
		String value = soapSelectValue("//acct:pref[@name='"+ pref +"']", null);
		return (value);
	}
	
	/**
	 * Get a folder ID by folder name
	 */
	public String getFolderIdByName(String foldername) throws HarnessException {
		soapSend("<GetFolderRequest xmlns='urn:zimbraMail'/>");
		Element[] elements = this.soapSelectNodes("//mail:folder[@name='"+ foldername +"']");
		
		// Error checking
		if ( elements == null ) {
			throw new HarnessException("No folder matched name "+ foldername);
		}
		if ( elements.length == 0 ) {
			throw new HarnessException("Returned folder list for name "+ foldername +" was 0");
		}
		if ( elements.length > 1 ) {
			throw new HarnessException("Too many matches for folder name "+ foldername);
		}
		
		Element eFolder = elements[0];
		String id = eFolder.getAttribute("id", null);

		logger.debug("GetFolderResponse for name "+ foldername +" was "+ eFolder.prettyPrint());
		return (id);

	}
	
	
	/**
	 * Upload a file to the upload servlet
	 * @param filename The full path to the upload file
	 * @return the attachment id, to be used with SaveDocumentRequest, for example
	 * @throws HarnessException
	 */
	public String uploadFile(String filename) throws HarnessException {
		return (uploadClient.doUpload(this.ZimbraMailHost, new File(filename)));
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
		
		return (soapClient.sendSOAP(ZimbraMailHost, request));
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

	
	public static class UploadClient {
		private static Logger logger = LogManager.getLogger(UploadClient.class);

		private static final MimetypesFileTypeMap contentTypeMap = new MimetypesFileTypeMap();

		public String AuthToken = null;
		public String SessionId = null;

		public UploadClient() {
		}
		
		/**
		 * Upload an attachment file
		 * @param f
		 * @return The attachment ID (for use in SaveDocumentRequest for example)
		 * @throws HarnessException
		 */
		public String doUpload(String host, File f) throws HarnessException {
			String attachmentId = null;
			
			// Determine the URI
			URI uri = null;
			try {
				uri = getUploadURI(host);
			} catch (URISyntaxException e) {
				throw new HarnessException("Unable to determine URI for upload " + host, e);
			}
			
			// Use HTTP to upload
			HttpState initialState = new HttpState();
			
			// Set the cookies
			if ( AuthToken != null ) {
				Cookie authCookie = new Cookie(uri.getHost(), "ZM_AUTH_TOKEN", AuthToken, "/", null, false);
				initialState.addCookie(authCookie);
			}
			if ( SessionId != null ) {
				Cookie sessionCookie = new Cookie(uri.getHost(), "JSESSIONID", SessionId, "/zimbra", null, false);
				initialState.addCookie(sessionCookie);
			}

			
	        // Doing a Post
			PostMethod method = null;
			

			// Create the client
			HttpClient client = new HttpClient();
			client.setState(initialState);
			client.getHttpConnectionManager().getParams().setConnectionTimeout(5 * 1000);

	        int code = 0;
	        try {

	        	// Create the POST method
	        	method = new PostMethod(uri.toString());

	        	PartBase filename1 = new StringPart("filename1", f.getAbsolutePath());
	        	
	        	FilePart fp = new FilePart(f.getName(), f);
	        	String contentType = contentTypeMap.getContentType(f);
	        	fp.setContentType(contentType);
	        	
	        	Part[] parts = { filename1, fp };

	        	MultipartRequestEntity request = new MultipartRequestEntity(parts, method.getParams());
	        	method.setRequestEntity( request );
	        	
	        	code = client.executeMethod(method);

	        	// For logging
	        	Header[] postHeaders = method.getRequestHeaders();
	        	for (int i = 0; i < postHeaders.length; i++)
	        	{
	        		logger.info(postHeaders[i]);
	        	}
	        	if ( contentType.contains("text") ) {
		        	ByteArrayOutputStream baos = new ByteArrayOutputStream();
		        	request.writeRequest(baos);
		        	logger.info(baos.toString());
	        	} else {
	        		logger.info("binary data omitted from logs");
	        	}
	        	
	        	Header[] responseHeaders = method.getResponseHeaders();
	        	for (int i = 0; i < responseHeaders.length; i ++)
	        	{
	        		logger.info(responseHeaders[i]);
	        	}
	        	String responseBody = method.getResponseBodyAsString();
	        	logger.info(responseBody);
	        	logger.info("StatusCode: " + code);

				// parse the response
	            if (code != HttpStatus.SC_OK) {
                    throw new HarnessException("Attachment "+ f.getAbsolutePath() +" post failed, response status: " + code);
	            }

            	// paw through the returned HTML and get the attachment id
                // example: loaded(<code>,'null','<id>')
                //
                int firstQuote = method.getResponseBodyAsString().indexOf("','") + 3;
                int lastQuote = method.getResponseBodyAsString().lastIndexOf("'");
                if (lastQuote == -1 || firstQuote == -1)
                    throw new HarnessException("Attachment post failed, unexpected response: " + method.getResponseBodyAsString());
                
                attachmentId = method.getResponseBodyAsString().substring(firstQuote, lastQuote);
                logger.info("Attachment ID: "+ attachmentId);
                
	        } catch (HttpException e) {
				throw new HarnessException("Unable to upload file "+ f.getAbsolutePath() , e);
			} catch (IOException e) {
				throw new HarnessException("Unable to upload file "+ f.getAbsolutePath() , e);
			} finally {
				if ( method != null ) {
					method.releaseConnection();
					method = null;
				}
	        }

			
			return (attachmentId);

		}

		public String setAuthToken(String token) {
        	return (AuthToken = token);
        }
        
        public String setSessionId(String id) {
        	return (SessionId = id);
        }
        
        protected URI getUploadURI(String host) throws URISyntaxException {
        	
        	// TODO: Need to make this configurable (config.properties)
        	String scheme = ZimbraSeleniumProperties.getStringProperty("server.scheme", "http");
        	String userinfo = null;
        	String p = ZimbraSeleniumProperties.getStringProperty("server.port", "80");
        	int port = Integer.parseInt(p);
        	String path = "/service/upload";
        	String query = "fmt=raw";
        	String fragment = null;
        	
        	return (new URI(scheme, userinfo, host, port, path, query, fragment));

        }

        
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
         * @throws HarnessException
         */
        public Element sendSOAP(String host, String request) throws HarnessException {        	
        	try
        	{
        		setContext(AuthToken, SessionId, SequenceNum);
        		return (sendSOAP(host, requestContext, Element.parseXML(request)));
        	} catch (DocumentException e) {
				throw new HarnessException("Unable to parse request "+ request, e);
        	}
        }
        
		/**
		 * Send a Zimbra SOAP context/request to the host
		 * @param host
		 * @param context
		 * @param request
		 * @return
		 * @throws HarnessException
		 */
        public Element sendSOAP(String host, Element context, Element request) throws HarnessException {
        	
        	setTransport(host, request);
        	
        	// Remember the context, request, envelope and response for logging purposes
        	requestBody = request;
        	requestEnvelope = mSoapProto.soapEnvelope(requestBody, context);

			try {
				responseEnvelope = mTransport.invokeRaw(requestEnvelope);
			} catch (IOException e) {
				throw new HarnessException("Unable to send SOAP to "+ this.mURI.toString(), e);
			} catch (ServiceException e) {
				throw new HarnessException("Unable to send SOAP to "+ this.mURI.toString(), e);
			}
			
			// Log the request/response
        	logger.info("\n" + new Date() +" "+ mURI.toString() +"\n---\n"+ requestEnvelope.prettyPrint() +"\n---\n"+ responseEnvelope.prettyPrint() +"\n---\n");
        	
			// Check the queue, if required
        	doPostfixDelay();
        	
			return (responseEnvelope);
        }
        
        /**
         * For certain SOAP requests, such as SendMsgRequest, a message may wind up in the
         * postfix queue.  Check that the queue is empty before proceeding
         */
        public void doPostfixDelay() {
        	
        	// If disabled, don't do anything
        	boolean enabled = ZimbraSeleniumProperties.getStringProperty("postfix.check", "true").equals("true");
        	if ( !enabled ) {
        		logger.debug("postfix.check was not true ... skipping queue check");
        		return;
        	}
        	
        	
        	// Create an array of the requests that require a queue check
        	final List<String> requests = new ArrayList<String>();
        	requests.add("mail:SendMsgRequest");
        	requests.add("mail:SendDeliveryReportRequest");
        	requests.add("mail:CreateTaskRequest");
        	requests.add("mail:ModifyTaskRequest");
        	requests.add("mail:SetTaskRequest");
        	requests.add("mail:SetAppointmentRequest");
        	requests.add("mail:CreateAppointmentRequest");
        	requests.add("mail:ModifyAppointmentRequest");
        	requests.add("mail:CancelAppointmentRequest");
        	requests.add("mail:ForwardAppointmentRequest");
        	requests.add("mail:ForwardAppointmentInviteRequest");
        	requests.add("mail:SendInviteReplyRequest");
        	requests.add("mail:CreateAppointmentExceptionRequest");

        	// If the current SOAP request matches any of the "queue" requests, set matched=true
        	for (String request : requests) {
        		Element[] nodes = selectNodes(requestEnvelope, "//"+ request);
        		if ( nodes == null )
        			continue;
        		if ( nodes.length > 0 ) {
    				try {
    					
						Stafpostqueue sp = new Stafpostqueue();
						sp.waitForPostqueue();

					} catch (Exception e) {
						logger.warn("Unable to wait for postfix queue", e);
					}
        			break;
        		}
        	}
        	
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
        	if (nodes.length == 0)
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

    		int size = zimbraElements.size();
    		Element[] retVal = new Element[size];
    		for (int i = 0; i < size; i++) {
    			retVal[i] = zimbraElements.get(i);
    		}
    		return (retVal);
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
        	
        	
        	String scheme = ZimbraSeleniumProperties.getStringProperty("server.scheme", "http");
        	String userInfo = null;
        	String p = ZimbraSeleniumProperties.getStringProperty("server.port", "80");
        	int port = Integer.parseInt(p);
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
    	@SuppressWarnings("unchecked")
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
				
	
		String domain = ZimbraSeleniumProperties.getStringProperty("server.host","qa60.lab.zimbra.com");
		
		// Configure log4j using the basic configuration
		BasicConfigurator.configure();
		
		// Create a new account object
		ZimbraAccount account = new ZimbraAccount("foo"+System.currentTimeMillis(), domain);
		
		// Provision it on the server
		account.provision();
		
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
