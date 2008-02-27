package com.zimbra.cs.offline.yab.protocol;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

import com.zimbra.cs.offline.OfflineLC;
import com.zimbra.cs.offline.yab.RawAuth;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.NameValuePair;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public abstract class Request {
    protected final RawAuth auth;
    protected final String format;
    protected HttpMethod method;

    private static final String BASE_URI = OfflineLC.zdesktop_yab_baseuri.value();
    private static final String FORMAT = "format";

    public static final String XML = "xml";
    public static final String JSON = "json";

    protected Request(RawAuth auth, String format) {
        this.auth = auth;
        this.format = format;
    }
    
    protected HttpMethod getHttpMethod() {
        return null;
    }

    protected abstract String getAction();
    
    protected NameValuePair[] getAdditionalParams() {
        return new NameValuePair[0];
    }

    public abstract Element toXml(Document doc);
    
    private NameValuePair[] getParams() {
        NameValuePair[] authParams = auth.getParams();
        NameValuePair[] additionalParams = getAdditionalParams();
        List<NameValuePair> params = new ArrayList<NameValuePair>(
            authParams.length + additionalParams.length + 2);
        params.addAll(Arrays.asList(authParams));
        params.add(new NameValuePair(FORMAT, format));
        params.addAll(Arrays.asList(additionalParams));
        return params.toArray(new NameValuePair[params.size()]);
    }

    /*
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
    }
    */
}
