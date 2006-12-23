/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Portions created by Zimbra are Copyright (C) 2006 Zimbra, Inc.
 * All Rights Reserved.
 * 
 * The Original Code is: Zimbra Network
 * 
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.cs.account.offline;

import java.util.ArrayList;
import java.util.List;

import com.zimbra.cs.mime.MimeTypeInfo;

class OfflineMimeType implements MimeTypeInfo {
    private String mType, mHandler, mFileExtensions[];
    private boolean mIndexed;

    private OfflineMimeType(String type, String handler, boolean index, String[] fext) {
        mType = type;  mHandler = handler;  mIndexed = index;  mFileExtensions = fext;
    }

    public String getType()              { return mType; }
    public String getExtension()         { return null; }
    public String getHandlerClass()      { return mHandler; }
    public boolean isIndexingEnabled()   { return mIndexed; }
    public String getDescription()       { return null; }
    public String[] getFileExtensions()  { return mFileExtensions; }

    static List<MimeTypeInfo> instantiateAll() {
        // just hardcode 'em for now...
        List<MimeTypeInfo> infos = new ArrayList<MimeTypeInfo>();
        infos.add(new OfflineMimeType("text/plain",     "TextPlainHandler",     true, new String[] { "txt", "text" } ));
        infos.add(new OfflineMimeType("text/html",      "TextHtmlHandler",      true, new String[] { "htm", "html" } ));
        infos.add(new OfflineMimeType("text/calendar",  "TextCalendarHandler",  true, new String[] { "ics", "vcs"} ));
        infos.add(new OfflineMimeType("message/rfc822", "MessageRFC822Handler", true, new String[] { } ));
        infos.add(new OfflineMimeType("text/enriched",  "TextEnrichedHandler",  true, new String[] { "txe" } ));
        infos.add(new OfflineMimeType("all",            "UnknownTypeHandler",   true, new String[] { } ));
        return infos;
    }
}
