package framework.util;

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
		ResourceType = type;
		
		if ( email == null ) {
			email = ZimbraSeleniumProperties.getStringProperty("locale") + "_" + ResourceType +"_" + System.currentTimeMillis();
		}
		EmailAddress = email;
		
		if ( password == null ) {
			password = ZimbraSeleniumProperties.getStringProperty("adminPwd", "test123");
		}
		Password = password;
		        
        
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


	public ZimbraAccount provision() {
		try {
			
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
			if ( (createCalendarResourceResponse == null) || (createCalendarResourceResponse.length == 0) ) {
				logger.error("Error occured during resource provisioning, perhaps resource already exists: "+ EmailAddress);
				ZimbraAdminAccount.GlobalAdmin().soapSend(
						"<GetCalendarResourceRequest xmlns='urn:zimbraAdmin'>" +
							"<calresource by='name'>"+ EmailAddress +"</calresource>" +
						"</GetCalendarResourceRequest>");
				Element[] getCalendarResourceResponse = ZimbraAdminAccount.GlobalAdmin().soapSelectNodes("//admin:GetCalendarResourceResponse");
				if ( (getCalendarResourceResponse == null) || (getCalendarResourceResponse.length == 0)) {
					logger.error("Error occured during get account provisioning.  Now I'm really confused");
				} else {
					ZimbraId = ZimbraAdminAccount.GlobalAdmin().soapSelectValue("//admin:calresource", "id");
					ZimbraMailHost = ZimbraAdminAccount.GlobalAdmin().soapSelectValue("//admin:calresource/admin:a[@n='zimbraMailHost']", null);
				}
				
			} else {
				ZimbraId = ZimbraAdminAccount.GlobalAdmin().soapSelectValue("//admin:calresource", "id");
				ZimbraMailHost = ZimbraAdminAccount.GlobalAdmin().soapSelectValue("//admin:account/admin:a[@n='zimbraMailHost']", null);
			}
			
		} catch (HarnessException e) {
			logger.error("Unable to provision account: "+ EmailAddress, e);
			ZimbraId = null;
			ZimbraMailHost = null;
		}
		return (this);
	}
	
}
