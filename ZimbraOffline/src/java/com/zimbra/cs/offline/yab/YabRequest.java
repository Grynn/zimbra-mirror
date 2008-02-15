package com.zimbra.cs.offline.yab;

import java.util.List;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.Element;
import com.zimbra.cs.mailbox.MailItem;
import com.zimbra.cs.mailbox.Mailbox;
import com.zimbra.cs.mailbox.Tag;
import com.zimbra.cs.mailbox.Mailbox.OperationContext;

public class YabRequest {
	
	
	
	
	
	
	public Element encode(Mailbox mbox, OperationContext context, YabSyncState syncState) throws ServiceException {
		
		int lastSeq = syncState.getModSequence();
		int pushSeq = 0;
		List<Integer> tombstones = null;
		List<Tag> tags = null;
		MailItem[] contacts = null;
		synchronized (mbox) {
			tombstones = mbox.getTombstones(lastSeq);
			tags = mbox.getModifiedTags(context, lastSeq);
			List<Integer> contactIds = mbox.getModifiedItems(context, lastSeq, MailItem.TYPE_CONTACT).getFirst();
			if (contactIds.size() > 0) {
				int[] ids = new int[contactIds.size()];
				for (int i = 0; i < ids.length; ++i) ids[i] = contactIds.get(i);
				contacts = mbox.getItemById(context, ids, MailItem.TYPE_CONTACT);
			} else
				contacts = new MailItem[0];
			pushSeq = mbox.getLastChangeID();
		}
		
		for (Tag tag : tags) {
			
			
		}
		
		
		
		
		
		
		
		return null;
	}
	
	
	

}
