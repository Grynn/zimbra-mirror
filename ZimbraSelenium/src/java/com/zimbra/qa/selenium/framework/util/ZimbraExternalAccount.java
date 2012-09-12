/**
 * 
 */
package com.zimbra.qa.selenium.framework.util;

import java.net.*;
import java.util.regex.*;

import org.apache.log4j.*;

import com.zimbra.common.soap.Element;

/**
 * Represents an 'external' account
 * 
 * @author Matt Rhoades
 *
 */
public class ZimbraExternalAccount extends ZimbraAccount {
	private static Logger logger = LogManager.getLogger(ZimbraExternalAccount.class);

	protected URI UrlRegistration = null;
	protected URI UrlLogin = null;
	
	
	public ZimbraExternalAccount() {
		logger.info("new "+ ZimbraExternalAccount.class.getCanonicalName());
	}
	
	public void setEmailAddress(String address) {
		this.EmailAddress = address;
	}
	
	public void setPassword(String password) {
		this.Password = password;
	}
	
	/**
	 * Based on the external invitation message,
	 * set both the login and registration URLs for this account
	 * @param GetMsgResponse
	 * @throws HarnessException 
	 */
	public void setURL(Element GetMsgResponse) throws HarnessException {
		this.setRegistrationURL(GetMsgResponse);
		this.setLoginURL(GetMsgResponse);
	}
	
	/**
	 * Based on the external invitation message, extract the login URL
	 * example, https://zqa-062.eng.vmware.com/service/extuserprov/?p=0_46059ce585e90f5d2d5...12e636f6d3b
	 * @param GetMsgResponse
	 */
	public void setRegistrationURL(Element GetMsgResponse) throws HarnessException {
		String content = this.soapClient.selectValue(GetMsgResponse, "//mail:mp[@ct='text/html']//mail:content", null, 1);
		try {
			setRegistrationURL(determineRegistrationURL(content));
		} catch (URISyntaxException e) {
			throw new HarnessException("Unable to parse registration URL from ["+ content +"]", e);
		}
	}
	
	/**
	 * Set the external user's registration URL to the specified URL
	 * @param url
	 */
	public void setRegistrationURL(URI url) {
		this.UrlRegistration = url;
	}
	
	public URI getRegistrationURL() {
		return (this.UrlRegistration);
	}
	

	/**
	 * Based on the external invitation message, extract the login URL
	 * example, https://zqa-062.eng.vmware.com/?virtualacctdomain=zqa-062.eng.vmware.com
	 * @param GetMsgResponse
	 * @throws HarnessException 
	 */
	public void setLoginURL(Element GetMsgResponse) throws HarnessException {
		String content = this.soapClient.selectValue(GetMsgResponse, "//mail:mp[@ct='text/html']//mail:content", null, 1);
		try {
			setLoginURL(determineLoginURL(content));
		} catch (URISyntaxException e) {
			throw new HarnessException("Unable to parse registration URL from ["+ content +"]", e);
		}		
	}
	
	/**
	 * Set the external user's login URL to the specified URL
	 * @param url
	 */
	public void setLoginURL(URI url) {
		this.UrlLogin = url;
	}
	
	public URI getLoginURL() {
		return (this.UrlLogin);
	}
	
	
	public static final Pattern patternTag = Pattern.compile("(?i)<a([^>]+)>(.+?)</a>");
	public static final Pattern patternLink = Pattern.compile("\\s*(?i)href\\s*=\\s*(\"([^\"]*\")|'[^']*'|([^'\">\\s]+))");
	
	/**
	 * Parse the invitation message to grab the registration URL
	 * for the external user
	 * @param content
	 * @return
	 * @throws HarnessException
	 * @throws MalformedURLException
	 * @throws URISyntaxException 
	 */
	protected URI determineRegistrationURL(String content) throws HarnessException, URISyntaxException {
		String registrationURL = null;
		
		Matcher matcherTag = patternTag.matcher(content);
		while (matcherTag.find()) {
			
			String href = matcherTag.group(1);
			String text = matcherTag.group(2);

			logger.info("href: "+ href);
			logger.info("text: "+ text);

			
			Matcher  matcherLink = patternLink.matcher(href);
			while(matcherLink.find()){

				registrationURL = matcherLink.group(1); //link
				if ( registrationURL.startsWith("\"") ) {
					registrationURL = registrationURL.substring(1); // Strip the beginning "
				}
				if ( registrationURL.endsWith("\"") ) {
					registrationURL = registrationURL.substring(0, registrationURL.length()-1); // Strip the ending "
				}
				logger.info("link: "+ registrationURL);
				return (new URI(registrationURL));

			}		
		}

		throw new HarnessException("Unable to determine the regisration URL from "+ content);
	}
	
	/**
	 * Parse the invitation message to grab the login URL
	 * for the external user
	 * @param content
	 * @return
	 * @throws HarnessException
	 * @throws URISyntaxException 
	 */
	protected URI determineLoginURL(String content) throws HarnessException, URISyntaxException {
		
		// TODO: implement me!
		return (determineRegistrationURL(content));

	}
	

	/**
	 * Get the external account for config.properties -> external.yahoo.account
	 * @return the ZimbraExternalAccount
	 */
	public static synchronized ZimbraExternalAccount ExternalA() {
		if ( _ExternalA == null ) {
			_ExternalA = new ZimbraExternalAccount();
		}
		return (_ExternalA);
	}
	private static ZimbraExternalAccount _ExternalA = null;

}
