package com.zimbra.extension;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.extension.ExtensionException;
import com.zimbra.cs.extension.ZimbraExtension;
import com.zimbra.soap.SoapServlet;
/**
 * The main entry point for extensions
 * @author gsolovyev
 *
 */
public class ZimbraHelloWorldExtension implements ZimbraExtension {
	public static String ZAS_EXTENSION_NAME = "com_zimbra_appointment_summary";
	public static final String APPOINTMENT_SUMMARY_TASK_NAME = "SendAppointmentSummary";
	public static final String E_helloWorld = "HelloWorld";
	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}

	@Override
	public String getName() {
		return ZAS_EXTENSION_NAME;
	}

	@Override
	public void init() throws ExtensionException, ServiceException {
		SoapServlet.addService("SoapServlet", new ZimbraHelloWorldService());
	}

}
