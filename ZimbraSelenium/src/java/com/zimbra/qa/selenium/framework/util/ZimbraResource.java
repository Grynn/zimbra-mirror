/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2011, 2012, 2013 Zimbra Software, LLC.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.4 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.qa.selenium.framework.util;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.zimbra.common.soap.Element;



public class ZimbraResource extends ZimbraAccount {
	private static Logger logger = LogManager.getLogger(ZimbraResource.class);

	public static enum Type {
		LOCATION,
		EQUIPMENT
	};
	
	public Type ResourceType;

	public ZimbraResource(Type type) {
		this(type, null, null);
	}
	
	public ZimbraResource(Type type, String email, String password) {
		super(email, password);
		
		ResourceType = type;
		        
        provision();
        authenticate();
 
		
	}
	
	// Set the default Equipment settings
	@SuppressWarnings("serial")
	private static final Map<String, String> equipmentAttrs = new HashMap<String, String>() {{
		put("zimbraPrefLocale", ZimbraSeleniumProperties.getStringProperty("locale"));
		put("zimbraCalResAutoAcceptDecline", "TRUE");
		put("zimbraCalResAutoDeclineIfBusy", "TRUE");
	}};

	// Set the default Location settings
	@SuppressWarnings("serial")
	private static final Map<String, String> locationAttrs = new HashMap<String, String>() {{
		put("zimbraPrefLocale", ZimbraSeleniumProperties.getStringProperty("locale"));
		put("zimbraCalResAutoAcceptDecline", "TRUE");
		put("zimbraCalResAutoDeclineIfBusy", "TRUE");
	}};


	public boolean exists() throws HarnessException {
		
		// Check if the account exists
		ZimbraAdminAccount.GlobalAdmin().soapSend(
				"<GetCalendarResourceRequest xmlns='urn:zimbraAdmin'>" +
					"<calresource by='name'>"+ EmailAddress +"</calresource>" +
				"</GetCalendarResourceRequest>");
		
		Element[] getCalendarResourceResponse = ZimbraAdminAccount.GlobalAdmin().soapSelectNodes("//admin:GetCalendarResourceResponse");
		
		if ( (getCalendarResourceResponse == null) || (getCalendarResourceResponse.length == 0) ) {
			
			logger.debug("Resource does not exist");
			return (false);
			
		}
		
		// Reset the account settings based on the response
		ZimbraId = ZimbraAdminAccount.GlobalAdmin().soapSelectValue("//admin:calresource", "id");
		ZimbraMailHost = ZimbraAdminAccount.GlobalAdmin().soapSelectValue("//admin:calresource/admin:a[@n='zimbraMailHost']", null);

		// If the adminHost value is set, use that value for the ZimbraMailHost
		String adminHost = ZimbraSeleniumProperties.getStringProperty("adminHost", null);
		if ( adminHost != null ) {
			ZimbraMailHost = adminHost;
		}

		return (true);
	}

	public ZimbraAccount provision() {
		try {
			
			// Check if the account already exists
			// If yes, don't provision again
			//
			if ( exists() ) {
				logger.info(EmailAddress + " already exists.  Not provisioning again.");
				return (this);
			}

			
			// Make sure the domain exists
			ZimbraDomain domain = new ZimbraDomain(EmailAddress.split("@")[1]);
			domain.provision();
			
			
			
			Map<String, String> attrs = null;
			StringBuilder prefs = null;
			if ( ResourceType == Type.EQUIPMENT ) {
				prefs = new StringBuilder("<a n='zimbraCalResType'>Equipment</a>");
				attrs = equipmentAttrs;
			} else if ( ResourceType == Type.LOCATION ) {
				prefs = new StringBuilder("<a n='zimbraCalResType'>Location</a>");
				attrs = locationAttrs;
			} else {
				throw new HarnessException("Unknown resource type "+ ResourceType);
			}
			
			// Build the list of default preferences
    		for (Map.Entry<String, String> entry : attrs.entrySet()) {
    			prefs.append(String.format("<a n='%s'>%s</a>", entry.getKey(), entry.getValue()));
    		}

			ZimbraAdminAccount.GlobalAdmin().soapSend(
					"<CreateCalendarResourceRequest xmlns='urn:zimbraAdmin'>" +
						"<name>" + EmailAddress + "</name>" +
						"<password>" + Password + "</password>" +
						"<a n='displayName'>"+ EmailAddress +"</a>" +
						prefs.toString() +
					"</CreateCalendarResourceRequest>");
			
			Element[] createCalendarResourceResponse = ZimbraAdminAccount.GlobalAdmin().soapSelectNodes("//admin:CreateCalendarResourceResponse");
			
			if ( (createCalendarResourceResponse == null) || (createCalendarResourceResponse.length == 0)) {

				Element[] soapFault = ZimbraAdminAccount.GlobalAdmin().soapSelectNodes("//soap:Fault");
				if ( soapFault != null && soapFault.length > 0 ) {
				
					String error = ZimbraAdminAccount.GlobalAdmin().soapSelectValue("//zimbra:Code", null);
					throw new HarnessException("Unable to create account: "+ error);
					
				}
				
				throw new HarnessException("Unknown error when provisioning account");
				
			}


			// Set the account settings based on the response
			ZimbraId = ZimbraAdminAccount.GlobalAdmin().soapSelectValue("//admin:calresource", "id");
			ZimbraMailHost = ZimbraAdminAccount.GlobalAdmin().soapSelectValue("//admin:calresource/admin:a[@n='zimbraMailHost']", null);

			// If the adminHost value is set, use that value for the ZimbraMailHost
			String adminHost = ZimbraSeleniumProperties.getStringProperty("adminHost", null);
			if ( adminHost != null ) {
				ZimbraMailHost = adminHost;
			}


			// Sync the GAL to put the account into the list
			domain.syncGalAccount();

			
		} catch (HarnessException e) {
			logger.error("Unable to provision account: "+ EmailAddress, e);
			ZimbraId = null;
			ZimbraMailHost = null;
		}
		return (this);
	}
	
	}

