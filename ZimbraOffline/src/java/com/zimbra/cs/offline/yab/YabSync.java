package com.zimbra.cs.offline.yab;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.util.ByteUtil;
import com.zimbra.cs.mailbox.MailItem;
import com.zimbra.cs.mailbox.Mailbox;
import com.zimbra.cs.mailbox.OfflineServiceException;
import com.zimbra.cs.mailbox.Tag;
import com.zimbra.cs.mailbox.Mailbox.OperationContext;
import com.zimbra.cs.offline.OfflineLC;

public class YabSync {
	
	private static final String SYNCHRONIZE = "/synchronize";
	private static final String COOKIE = "Cookie";
	
	private Yauth yauth;
	private YabSyncState syncState;
	private Mailbox mbox;
	
	private String yid;
	private String ypw;
	
	private OperationContext context;
	
	public YabSync(Mailbox mbox, String yid, String ypw) throws ServiceException {
		this.mbox = mbox;
		this.yid = yid;
		this.ypw = ypw;
		
		syncState = new YabSyncState(mbox);
		context = new OperationContext(mbox);
	}

	public void sync() throws ServiceException {
		
		if (yauth == null || yauth.expiration < System.currentTimeMillis())
			yauth = Yauth.authenticate(yid, ypw);
		
		try {
			runSync();
		} catch (ServiceException x) {
			if (x.getCode() == OfflineServiceException.AUTH_FAILED) {
				yauth = Yauth.authenticate(yid, ypw);
				runSync();
			} else {
				throw x;
			}
		}
	}
	
	private void runSync() throws ServiceException {
		int lastSeq = syncState.getModSequence();
		int pushSeq = 0;
		List<Tag> tags = null;
		MailItem[] contacts = null;
		synchronized (mbox) {
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
	}
	
	
	
	private void syncContacts() throws ServiceException {
		
		int rev = syncState.getYabRevision();
		
		HttpMethod method = null;
		if (hasLocalChanges()) {
			
		} else {
			method = prepareGetMethod(rev);
		}
		
		try {
			int code = new HttpClient().executeMethod(method);
			if (code == 200) {
				processResponse(method.getResponseBodyAsStream());
			} else if (code == 403) {
				throw OfflineServiceException.AUTH_FAILED(yid, "User and or Application Authentication credentials are insufficient (or invalid/missing)");
			} else if (code == 404) {
				throw OfflineServiceException.UNEXPECTED("Malformed URL referring to an inexistent service endpoint or method");
			} else if (code == 500) {
				throw OfflineServiceException.UNEXPECTED("An internal error with Address Book servers");
			} else if (code == 503) {
				throw ServiceException.TEMPORARILY_UNAVAILABLE();
			} else {
				throw OfflineServiceException.UNEXPECTED("General failure in yab sync");
			}
		} catch (HttpException x) {
			throw ServiceException.FAILURE("yab sync", x);
		} catch (IOException x) {
			throw ServiceException.FAILURE("yab sync", x);
		}
	}
	
	
	private void processResponse(InputStream in) throws IOException {
		System.out.println(new String(ByteUtil.getContent(in, 0)));
	}
	
	private boolean hasLocalChanges() throws ServiceException {
		return false;
	}
	
	private HttpMethod prepareGetMethod(int rev) {
		return prepareMethod(new GetMethod(OfflineLC.zdesktop_yab_baseuri.value() + SYNCHRONIZE), rev);
	}
	
	private HttpMethod prepareMethod(HttpMethod method, int rev) {
        NameValuePair[] nvps = new NameValuePair[4];
        nvps[0] = new NameValuePair("format", "xml");
        nvps[1] = new NameValuePair("myrev", "" + 1);
        nvps[2] = new NameValuePair("appid", OfflineLC.zdesktop_yauth_appid.value());
        nvps[3] = new NameValuePair("WSSID", yauth.wssid);
        method.setQueryString(nvps);
		method.addRequestHeader(COOKIE, yauth.cookie);
		return method;
	}	
}
