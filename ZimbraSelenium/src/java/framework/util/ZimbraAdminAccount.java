package framework.util;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.zimbra.common.soap.Element;

public class ZimbraAdminAccount extends ZimbraAccount {
	private static Logger logger = LogManager.getLogger(ZimbraAccount.class);

	public ZimbraAdminAccount(String email) {
		EmailAddress = email;
		
		CN = EmailAddress.split("@")[0];
        DisplayName = CN;

        DomainName = EmailAddress.split("@")[1];
		
		// Need to get the ZimbraMailHost using GetAccountRequest
		// But, that is chicken and egg - need the host to send SOAP
		
		// TODO: determine this from config.properties
        ZimbraMailHost = DomainName;
        
        // TODO: Add a default password to the config.properties
        Password = ZimbraSeleniumProperties.getInstance().getConfigProperties().getString("defaultpassword", "test123");
        		
	}
	
	/**
	 * Creates the admin account on the ZCS
	 * @throws HarnessException 
	 */
	protected void provisionAccount() {
		try {
			ZimbraAdminAccount.GlobalAdmin().soapSend(
					"<CreateAccountRequest xmlns='urn:zimbraAdmin'>" +
			        	"<name>"+ EmailAddress +"</name>" +
			        	"<password>"+ Password +"</password>" +
			        	"<a n='zimbraIsAdminAccount'>TRUE</a>" +
			        "</CreateAccountRequest>");
			ZimbraId = ZimbraAdminAccount.GlobalAdmin().soapSelectValue("//admin:account", "id");
			ZimbraMailHost = ZimbraAdminAccount.GlobalAdmin().soapSelectValue("//admin:account/admin:a[@n='zimbraMailHost']", null);
		} catch (HarnessException e) {
			logger.error("Unable to provision account: "+ EmailAddress);
			ZimbraId = null;
			ZimbraMailHost = null;
		}
	}
	/**
	 * Authenticates the admin account using SOAP using the Admin AuthRequest
	 * Sets the authToken and sessionId
	 * @throws HarnessException 
	 */
	public void authenticate() {
		try {
			soapSend(
					"<AuthRequest xmlns='urn:zimbraAdmin'>" +
						"<name>"+ EmailAddress +"</name>" +
						"<password>"+ Password +"</password>" +
					"</AuthRequest>");
			String token = soapSelectValue("//admin:authToken", null);
			soapClient.setAuthToken(token);
		} catch (HarnessException e) {
			logger.error("Unable to authenticate "+ EmailAddress, e);
			soapClient.setAuthToken(null);
		}
	}

	private static ZimbraAdminAccount _GlobalAdmin = null;
	public static synchronized ZimbraAdminAccount GlobalAdmin() {
		if ( _GlobalAdmin == null ) {
			// TODO: determine this from config.properties
			_GlobalAdmin = new ZimbraAdminAccount("admin@qa62.lab.zimbra.com");
			_GlobalAdmin.authenticate();
		}
		
		return (_GlobalAdmin);
		
	}
	/**
	 * @param args
	 * @throws HarnessException 
	 */
	public static void main(String[] args) throws HarnessException {
		Element[] response;
		
		// Configure log4j using the basic configuration
		BasicConfigurator.configure();

		
		
		// Use the pre-provisioned global admin account to send a basic request
		ZimbraAdminAccount.GlobalAdmin().soapSend("<GetVersionInfoRequest xmlns='urn:zimbraAdmin'/>");
		response = ZimbraAdminAccount.GlobalAdmin().soapSelectNodes("//admin:GetVersionInfoResponse");
		if ( response.length != 1 )
			throw new HarnessException("GetVersionInfoRequest did not return GetVersionInfoResponse");
		
		
		
		// Create a new global admin account
		ZimbraAdminAccount admin = new ZimbraAdminAccount("admin"+ System.currentTimeMillis() +"@qa62.lab.zimbra.com");
		admin.provisionAccount();	// Create the account (CreateAccountRequest)
		admin.authenticate();		// Authenticate the account (AuthRequest)
		
		// Send a basic request as the new admin account
		admin.soapSend("<GetServiceStatusRequest xmlns='urn:zimbraAdmin'/>");
		response = admin.soapSelectNodes("//admin:GetServiceStatusResponse");
		if ( response.length != 1 )
			throw new HarnessException("GetServiceStatusRequest did not return GetServiceStatusResponse");

		
		logger.info("Done!");
	}

}
