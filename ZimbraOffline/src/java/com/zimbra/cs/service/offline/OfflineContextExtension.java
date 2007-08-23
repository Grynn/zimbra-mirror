package com.zimbra.cs.service.offline;

import com.zimbra.common.soap.Element;
import com.zimbra.cs.mailbox.Mailbox;
import com.zimbra.cs.mailbox.OfflineMailbox;
import com.zimbra.cs.session.Session;
import com.zimbra.soap.SoapContextExtension;
import com.zimbra.soap.ZimbraSoapContext;

public class OfflineContextExtension extends SoapContextExtension {

	public static final String ZDSYNC = "zdsync";
	
	@Override
	public void addExtensionHeader(Element context, ZimbraSoapContext zsc, Session session) {
		Mailbox mbox = session.getMailbox();
		assert (mbox != null && mbox instanceof OfflineMailbox);
		((OfflineMailbox)mbox).encodeMailboxSync(context);
	}
}
