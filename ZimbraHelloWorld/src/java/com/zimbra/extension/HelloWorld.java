package com.zimbra.extension;

import java.util.Map;

import com.zimbra.common.account.Key.AccountBy;
import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.Element;
import com.zimbra.common.soap.MailConstants;
import com.zimbra.cs.account.Account;
import com.zimbra.cs.mailbox.Mailbox;
import com.zimbra.cs.mailbox.ScheduledTask;
import com.zimbra.cs.mailbox.ScheduledTaskManager;
import com.zimbra.cs.service.account.AccountDocumentHandler;
import com.zimbra.soap.ZimbraSoapContext;

public class HelloWorld extends AccountDocumentHandler {
/**
 * Process the SOAP request (XML of the request in in the request argument). Return the response element.
 */
	@Override
	public Element handle(Element request, Map<String, Object> context)
			throws ServiceException {
		ZimbraSoapContext zsc = getZimbraSoapContext(context);
		Element response = zsc.createElement(ZimbraHelloWorldService.HELLO_WORLD_RESPONSE);
		response.addElement(ZimbraHelloWorldExtension.E_helloWorld);
		response.addAttribute(ZimbraHelloWorldExtension.E_helloWorld, "hellow");
		return response;
	}

}
