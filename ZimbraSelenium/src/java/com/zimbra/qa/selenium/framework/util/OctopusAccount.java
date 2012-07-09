package com.zimbra.qa.selenium.framework.util;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.zimbra.common.soap.Element;
import com.zimbra.qa.selenium.framework.core.DevEnvironment;

public class OctopusAccount extends ZimbraAccount {
	private static Logger logger = LogManager.getLogger(OctopusAccount.class);
	private static OctopusAccount _AccountZWC = null;

	/**
	 * Get the user account logged into ZWC being tested
	 * @return the ZimbraAccount object representing the test account
	 */
	public static synchronized OctopusAccount AccountZWC() {
		if ( _AccountZWC == null ) {
			_AccountZWC = new OctopusAccount();
			_AccountZWC.provision();
			_AccountZWC.authenticate();
		}
		return (_AccountZWC);
	}
	
	// Set the default account settings
	@SuppressWarnings("serial")
	private static final Map<String, String> accountAttrs = new HashMap<String, String>() {{
		put("zimbraPrefLocale", ZimbraSeleniumProperties.getStringProperty("locale"));
		put("zimbraPrefAutoAddAddressEnabled", "FALSE");
		put("zimbraPrefTimeZoneId", ZimbraSeleniumProperties.getStringProperty("zimbraPrefTimeZoneId", "America/Los_Angeles"));			
	}};

	/**
	 * Creates the account on the ZCS using CreateAccountRequest
	 */
	public ZimbraAccount provision() {
		try {

			
			// Make sure domain exists
			ZimbraDomain domain = new ZimbraDomain( EmailAddress.split("@")[1]);
			domain.provision();
			


			// Build the list of default preferences
			StringBuilder prefs = new StringBuilder();
			for (Map.Entry<String, String> entry : accountAttrs.entrySet()) {
				prefs.append(String.format("<a n='%s'>%s</a>", entry.getKey(), entry.getValue()));
			}
			for (Map.Entry<String, String> entry : preferences.entrySet()) {
				prefs.append(String.format("<a n='%s'>%s</a>", entry.getKey(), entry.getValue()));
			}

			// Create the account
			ZimbraAdminAccount.GlobalAdmin().soapSend(
					"<CreateAccountRequest xmlns='urn:zimbraAdmin'>"
					+		"<name>"+ EmailAddress +"</name>"
					+		"<password>"+ Password +"</password>"
					+		prefs.toString()
					+	"</CreateAccountRequest>");

			Element[] createAccountResponse = ZimbraAdminAccount.GlobalAdmin().soapSelectNodes("//admin:CreateAccountResponse");


			if ( (createAccountResponse == null) || (createAccountResponse.length == 0)) {

				Element[] soapFault = ZimbraAdminAccount.GlobalAdmin().soapSelectNodes("//soap:Fault");
				if ( soapFault != null && soapFault.length > 0 ) {
				
					String error = ZimbraAdminAccount.GlobalAdmin().soapSelectValue("//zimbra:Code", null);
					throw new HarnessException("Unable to create account: "+ error);
					
				}
				
				
				logger.error("Error occured during account provisioning, perhaps account already exists: "+ EmailAddress);
				ZimbraAdminAccount.GlobalAdmin().soapSend(
						"<GetAccountRequest xmlns='urn:zimbraAdmin'>"
						+		"<account by='name'>"+ EmailAddress + "</account>"
						+	"</GetAccountRequest>");

				Element[] getAccountResponse = ZimbraAdminAccount.GlobalAdmin().soapSelectNodes("//admin:GetAccountResponse");


				if ( (getAccountResponse == null) || (getAccountResponse.length == 0)) {

					logger.error("Error occured during get account provisioning.  Now I'm really confused");

				} else {

					ZimbraId = ZimbraAdminAccount.GlobalAdmin().soapSelectValue("//admin:account", "id");
					ZimbraMailHost = ZimbraAdminAccount.GlobalAdmin().soapSelectValue("//admin:account/admin:a[@n='zimbraMailHost']", null);
					ZimbraPrefLocale = ZimbraAdminAccount.GlobalAdmin().soapSelectValue("//admin:account/admin:a[@n='zimbraPrefLocale']", null);

				}
			} else {

				ZimbraId = ZimbraAdminAccount.GlobalAdmin().soapSelectValue("//admin:account", "id");
				ZimbraMailHost = ZimbraAdminAccount.GlobalAdmin().soapSelectValue("//admin:account/admin:a[@n='zimbraMailHost']", null);
				ZimbraPrefLocale = ZimbraAdminAccount.GlobalAdmin().soapSelectValue("//admin:account/admin:a[@n='zimbraPrefLocale']", null);


			}

			if ( (ZimbraPrefLocale == null) || ZimbraPrefLocale.trim().equals("") ) {
				ZimbraPrefLocale = Locale.getDefault().toString();
			}

			if ( ZimbraSeleniumProperties.getStringProperty("soap.trace.enabled", "false").toLowerCase().equals("true") ) {
				
				ZimbraAdminAccount.GlobalAdmin().soapSend(
							"<AddAccountLoggerRequest xmlns='urn:zimbraAdmin'>"
						+		"<account by='name'>"+ EmailAddress + "</account>"
						+		"<logger category='zimbra.soap' level='trace'/>"
						+	"</AddAccountLoggerRequest>");

			}
			
			// Start: Dev environment hack
			if ( DevEnvironment.isUsingDevEnvironment() ) {
				ZimbraMailHost = "localhost";
			}
			// End: Dev environment hack

			
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
