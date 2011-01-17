package com.zimbra.qa.selenium.framework.util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.activation.MimetypesFileTypeMap;

import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpState;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.PartBase;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.apache.commons.httpclient.params.HttpConnectionParams;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;


public class RestUtil {
	private static Logger logger = LogManager.getLogger(RestUtil.class);
	
	// URI data
	protected String scheme = null;
	protected String userInfo = null;
	protected String host = null;
	protected int port = 80;
	protected String path = null;
	protected Map<String, String> QueryMap = new HashMap<String, String>();
	protected String fragment = null;
	
	// Upload file (For POST)
	protected File requestFile = null;

	// Cookie data
	protected String authToken = null;
	protected String session = null;
	
	// Guest login data
	protected String guestLogin = null;
	protected String guestPassword = null;
	
	// Request/Response data
	protected URI requestURI = null;
	protected Header[] requestHeaders = null;
	protected String requestBody = null;
	protected Header[] responseHeaders = null;
	protected String responseBody = null;
	protected File responseFile = null;
	protected int responseCode = 0;
	
	
	// Used for determining upload content types
	protected static MimetypesFileTypeMap contentTypeMap = new MimetypesFileTypeMap();

	public RestUtil() {
		logger.info("new RestUtil()");
		
		scheme = ZimbraSeleniumProperties.getStringProperty("server.scheme", "http");
		String p = ZimbraSeleniumProperties.getStringProperty("server.port", "80");
		port = Integer.parseInt(p);
		path = "service/home/~/";
	}
	
	public ZimbraAccount setAuthentication(ZimbraAccount account) {
		if ( account == null ) {
			host = null;
			authToken = null;
		} else {
			host = account.ZimbraMailHost;
			authToken = account.MyAuthToken;			
		}
		return (account);
	}
	
	public void setQueryParameter(String key, String value) throws HarnessException {
		if ( QueryMap.containsKey(key) ) {
			throw new HarnessException("duplicate keys not implemented.  existing key/value = "+ key +"/"+ QueryMap.get(key));
		}
		QueryMap.put(key, value);
	}
	
	protected String getQuery() {
		
		StringBuilder sb = null;
		for (Iterator<Map.Entry<String, String>> it = QueryMap.entrySet().iterator(); it.hasNext(); ) {
			Map.Entry<String, String> entry = it.next();
			String key = entry.getKey();
			String value = entry.getValue();
			
			if ( sb == null ) {
				sb = new StringBuilder();
				sb.append(key).append('=').append(value);
			} else {
				sb.append('&');
				sb.append(key).append('=').append(value);
			}
		}
		return (sb == null ? "" : sb.toString());
		
	}
	
	public void setPath(String path) {
		if (!path.startsWith("/"))
			path = "/"+ path;
		this.path = path;
	}
	
	public void setPath(ZimbraAccount account) {
		setPath("/service/home/"+ account.EmailAddress +"/");
	}
	
	protected URI getURI() throws URISyntaxException {
    	return (new URI(scheme, userInfo, host, port, path, getQuery(), fragment));
	}
	
	public Object doGet() throws HarnessException {
		logger.debug("doGet()");

		// This method simply wraps the logging around the doGetRequest() method
		
		try {
			
			// Do the actual request
			return (doGetRequest());
			
		} finally {
			
			// Log the http get details
			
			String eol = System.getProperty("line.separator", "\n");
			StringBuilder sb = new StringBuilder();

			sb.append(eol).append(new Date()).append(" - ").append(requestURI).append(eol);
			
			// REQUEST
			sb.append("..... Request Headers ...").append(eol); // Headers
			if ( requestHeaders != null ) {
				for (Header h : requestHeaders) {
					sb.append(h.toString().trim()).append(eol);
				}
			}
			sb.append(".....").append(eol);
			sb.append("..... Request Body ...").append(eol); // Body
			sb.append(requestBody).append(eol);
			sb.append(".....").append(eol);
			
			// RESPONSE
			sb.append("..... Response Headers ...").append(eol); // Headers
			if ( responseHeaders != null ) {
				for (Header h : responseHeaders) {
					sb.append(h.toString().trim()).append(eol);
				}
			}
			sb.append(".....").append(eol);
			sb.append("..... Response Body ...").append(eol); // Body
			sb.append(responseBody).append(eol);
			sb.append(".....").append(eol);
			
			logger.info(sb.toString());
		}
	}

	protected int doGetRequest() throws HarnessException {
		logger.debug("doGetRequest()");
		
		
		try {
			requestURI = getURI();
			logger.debug("RestServlet: "+ requestURI.toString());
		} catch (URISyntaxException e) {
			throw new HarnessException("Unable to build URI", e);
		}

		HttpState initialState = new HttpState();
		
		//Build the cookies to connect to the rest servlet
		//
		if ( authToken != null ) {
			Cookie cookie = new Cookie(requestURI.getHost(), "ZM_AUTH_TOKEN", authToken, "/", null, false);
			initialState.addCookie(cookie);
		}
		
		if ( session != null ) {
			Cookie cookie = new Cookie(requestURI.getHost(), "JSESSIONID", session, "/zimbra", null, false);
			initialState.addCookie(cookie);

		}

		
		// If <guest> or <password> are used, then we must guest authenticate
		//
		if ( guestLogin != null ) {
			Credentials loginCredentials = new UsernamePasswordCredentials(guestLogin, guestPassword == null ? "" : guestPassword);
			initialState.setCredentials(AuthScope.ANY, loginCredentials);
		}

		
		HttpClient client = new HttpClient();
		client.setState(initialState);
		
		//		 Connect to the rest servlet
		//
		HttpMethod method = new GetMethod(requestURI.toString());
		
		
		try
		{

			
			responseCode = client.executeMethod(method);
			
			requestHeaders = method.getRequestHeaders();

			if ( responseCode != HttpStatus.SC_OK ) {
				logger.debug("Method failed: " + method.getStatusLine());
				
				responseHeaders = null;
				responseBody = null;

			}
			else {
				
				boolean chunked = false;
				boolean textContent = false;
				
				// Add all the HTTP headers (not the message headers, which are added below)
				responseHeaders = method.getResponseHeaders();
				for (int i=0; i < responseHeaders.length; i++) {
					if ( responseHeaders[i].getName().equals("Transfer-Encoding") && responseHeaders[i].getValue().equals("chunked") ) {
						chunked=true;
					}
					if ( responseHeaders[i].getName().equals("Content-Type") && responseHeaders[i].getValue().contains("text") ) {
						textContent=true;
					}
				}
				
				if ( chunked && !textContent ) {	
					
					// Write the binary data to a file for later comparison
					//
					responseFile = File.createTempFile("rest", ".tmp");

					InputStream is = method.getResponseBodyAsStream();
					OutputStream os = new FileOutputStream(responseFile);
					   
					int b;
					while ( (b = is.read()) != -1) {
						os.write(b);
					}
					os.close();
					is.close();

				
					// For logging
					responseBody = "binary data saved in file: "+ responseFile.getAbsolutePath();					

				} else {

					// Write the response to a file for later comparison
					//
					responseFile = File.createTempFile("rest", ".tmp");

					responseBody = method.getResponseBodyAsString();

					// Create a temporary file name
					OutputStream os = new FileOutputStream(responseFile);
					os.write(responseBody.getBytes());
					os.close();
					
				}
				
				
			}

		} catch (HttpException e) {
			throw new HarnessException("RestUtil HttpException", e);
	    } catch (IOException e) {
	    	throw new HarnessException("RestUtil IOException", e);
	    } finally {
		      // Release the connection.
		      method.releaseConnection();
	    }
		
		// Until executeTestResponse is called, assume that 200 is expected for the test
	    return (responseCode);

	

	}
	
	public int doPost() throws HarnessException {
		logger.debug("doPost()");

		// This method simply wraps the logging around the doGetRequest() method

		try {

			// Do the actual request
			return (doPostRequest());

		} finally {

			// Log the http get details

			String eol = System.getProperty("line.separator", "\n");
			StringBuilder sb = new StringBuilder();

			sb.append(eol).append(new Date()).append(" - ").append(requestURI).append(eol);

			// REQUEST
			sb.append("..... Request Headers ...").append(eol); // Headers
			if ( requestHeaders != null ) {
				for (Header h : requestHeaders) {
					sb.append(h.toString().trim()).append(eol);
				}
			}
			sb.append(".....").append(eol);
			sb.append("..... Request Body ...").append(eol); // Body
			sb.append(requestBody).append(eol);
			sb.append(".....").append(eol);

			// RESPONSE
			sb.append("..... Response Headers ...").append(eol); // Headers
			if ( responseHeaders != null ) {
				for (Header h : responseHeaders) {
					sb.append(h.toString().trim()).append(eol);
				}
			}
			sb.append(".....").append(eol);
			sb.append("..... Response Body ...").append(eol); // Body
			sb.append(responseBody).append(eol);
			sb.append(".....").append(eol);

			logger.info(sb.toString());
		}
	}

	
	protected int doPostRequest() throws HarnessException {
		logger.debug("doPostRequest()");
		
		if ( requestFile == null )
			throw new HarnessException("use setPostRequestFile() before doPost()");
		

		
		
		try {
			requestURI = getURI();
			logger.debug("RestServlet: "+ requestURI.toString());
		} catch (URISyntaxException e) {
			throw new HarnessException("Unable to build URI", e);
		}

		
		HttpState initialState = new HttpState();
		
		//Build the cookies to connect to the rest servlet
		//
		if ( authToken != null ) {
			Cookie cookie = new Cookie(requestURI.getHost(), "ZM_AUTH_TOKEN", authToken, "/", null, false);
			initialState.addCookie(cookie);
		}
		
		if ( session != null ) {
			Cookie cookie = new Cookie(requestURI.getHost(), "JSESSIONID", session, "/zimbra", null, false);
			initialState.addCookie(cookie);

		}

		
		// If <guest> or <password> are used, then we must guest authenticate
		//
		if ( guestLogin != null ) {
			Credentials loginCredentials = new UsernamePasswordCredentials(guestLogin, guestPassword == null ? "" : guestPassword);
			initialState.setCredentials(AuthScope.ANY, loginCredentials);
		}

		


		
        // make the post
		PostMethod method = new PostMethod(requestURI.toString());

		
		HttpClient client = new HttpClient();
		client.setState(initialState);
		
		
		HttpConnectionParams params = client.getHttpConnectionManager().getParams();
		params.setConnectionTimeout(5 * 1000);

        try {

        	
    		// Determine the file contents
    		String contentType = contentTypeMap.getContentType(requestFile);		
    		String encoding = FilePart.DEFAULT_TRANSFER_ENCODING;


        	PartBase filename1 = new StringPart("filename1", requestFile.getAbsolutePath());
        	FilePart fp = new FilePart(requestFile.getName(), requestFile);
        	fp.setContentType(contentType);
        	fp.setTransferEncoding(encoding);
        	
        	Part[] parts = { filename1, fp };

        	MultipartRequestEntity request = new MultipartRequestEntity(parts, method.getParams());
        	method.setRequestEntity( request );
        	
        	responseCode = client.executeMethod(method);

			if ( responseCode != HttpStatus.SC_OK ) {
				logger.debug("Method failed: " + method.getStatusLine());
				
				responseHeaders = null;
				responseBody = null;

			}
			else {

	        	// Remember the request
	        	requestHeaders = method.getRequestHeaders();
	        	ByteArrayOutputStream baos = new ByteArrayOutputStream();
	        	request.writeRequest(baos);
	        	requestBody += baos.toString();
	        	
	        	// Remember the response
	        	responseHeaders = method.getResponseHeaders();
	        	responseBody = method.getResponseBodyAsString();
        	
			}

        } catch (FileNotFoundException e) {
			throw new HarnessException("RestUtil FileNotFoundException", e);
		} catch (HttpException e) {
			throw new HarnessException("RestUtil HttpException", e);
		} catch (IOException e) {
			throw new HarnessException("RestUtil IOException", e);
		} finally {
        	method.releaseConnection();
        }

		
		return (responseCode);
	

	}
	
	public int getLastResponseCode() throws HarnessException {
		return (responseCode);
	}
	
	public String getLastResponseBody() throws HarnessException {
		return (responseBody);
	}
	
	public String getLastResponseFile() throws HarnessException {
		try {
			return (responseFile.getCanonicalPath());
		} catch (IOException e) {
			return (responseFile.getAbsolutePath());
		}
	}
	
	public int doRegex(String regex) throws HarnessException {
		return (doPattern(Pattern.compile(regex)));
	}
	
	public int doPattern(Pattern pattern) throws HarnessException {
		int count = 0;
		
		if ( pattern == null )
			throw new HarnessException("Pattern cannot be null");
		
		Matcher matcher = pattern.matcher("");
		
		try {
			
			BufferedReader reader = null;

			try {
				reader = new BufferedReader(new FileReader(responseFile));
				String line = null;
				while ( (line = reader.readLine()) != null ) {
					
					matcher.reset(line);
					
					if ( matcher.matches() ) {
						logger.info("yes PATTERN: "+ pattern.toString() + " LINE: "+ line);
						count++;
					} else {
						logger.info("no PATTERN: "+ pattern.toString() + " LINE: "+ line);
					}
					
				}
			} finally {
				if ( reader != null ) {
					reader.close();
					reader = null;
				}
			}
				
		} catch (FileNotFoundException e) {
			throw new HarnessException(e);
		} catch (IOException e) {
			throw new HarnessException(e);
		}
					
		return (count);
	}

}
