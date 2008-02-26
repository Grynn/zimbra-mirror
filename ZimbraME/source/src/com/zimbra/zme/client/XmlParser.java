/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite, Network Edition.
 * Copyright (C) 2007, 2008 Zimbra, Inc.  All Rights Reserved.
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.zme.client;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;

import org.kxml2.io.KXmlParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

public class XmlParser extends KXmlParser {
    private Hashtable mFragments;
    private static final String EMPTY = "";
    private byte[] mBuf;
    private InputStream mIn;
    private String mEncoding;

    public XmlParser() {
        mFragments = new Hashtable();
    }
    public String getAttributeValue(String namespace,
                                    String name,
                                    String defaultValue) {
        String val = getAttributeValue(namespace, name);
        if (val != null)
            return val;
        return defaultValue;
    }
    public long getAttributeValue(String namespace,
                                  String name,
                                  long defaultValue) {
        String val = getAttributeValue(namespace, name);
        if (val != null)
            return Long.parseLong(val);
        return defaultValue;
    }
    public boolean getAttributeValue(String namespace,
                                     String name,
                                     boolean defaultValue) {
        String val = getAttributeValue(namespace, name);
        if (val != null)
            return val.compareTo("1") == 0;
        return defaultValue;
    }
    public void setInput(InputStream is, 
                         String enc,
                         boolean bufferResponse) throws XmlPullParserException {
        if (bufferResponse) {
            try {
                if (mIn != null)
                    mIn.close();
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                int c = 0;
                byte[] buf = new byte[256];
                try {
                    while ((c = is.read(buf)) > 0)
                        out.write(buf, 0, c);
                } catch (IOException e) {
                }
                mBuf = out.toByteArray();
                mIn = new ByteArrayInputStream(mBuf);
                mEncoding = enc;
            } catch (IOException e) {
                throw new XmlPullParserException("cannot initialize the input", this, e);
            }
        } else {
            mIn = is;
        }
        super.setInput(mIn, enc);
        mFragments.clear();
    }
    public String addFragmentName(String n) {
        mFragments.put(n, EMPTY);
        return n;
    }
    public String getFragment(String n) {
        Object v = mFragments.get(n);
        if (v == EMPTY)
            v = findFragment(n);
        if (!v.equals(EMPTY))
            return (String)v;
        return null;
    }
    private String findFragment(String n) {
        byte[] beginTag = new String("<" + n).getBytes();
        byte[] endTag = new String("</" + n).getBytes();
        int startPos = -1;
        int endPos = -1;
        for (int i = 0; i < mBuf.length; i++) {
            if (startPos < 0 && mBuf[i] == beginTag[0]) {
                if (matchStr(mBuf, i, beginTag, 0))
                    startPos = i;
            } else if (startPos > 0 && endPos < 0 && mBuf[i] == endTag[0]) {
                if (matchStr(mBuf, i, endTag, 0)) {
                    endPos = i;
                    break;
                }
            }
        }
        if (endPos > 0) {
            while (mBuf[endPos] != '>')
                endPos++;
        }
        String val = EMPTY;
        if (startPos > 0 && endPos > 0)
            try {
                val = new String(mBuf, startPos, endPos-startPos+1, mEncoding);
            } catch (IOException e) {
            }
        mFragments.put(n, val);
        return val;
    }
    private boolean matchStr(byte[] s1, int pos1, byte[] s2, int pos2) {
        boolean match = true;
        for (int i = 0; i < s2.length; i++)
            if (s1[pos1+i] != s2[pos2+i]) {
                match = false;
                break;
            }
        return match;
    }
    public static void writeFragment(String fragment, XmlSerializer s) throws IOException,XmlPullParserException {
        XmlParser p = new XmlParser();
        p.setInput(new ByteArrayInputStream(fragment.getBytes()), "UTF-8");
        int eventType;
        while ((eventType = p.next()) != END_DOCUMENT) {
            switch (eventType) {
            case START_TAG:
                s.startTag(null, p.getName());
                for (int i = 0; i < p.getAttributeCount(); i++)
                    s.attribute(null, p.getAttributeName(i), p.getAttributeValue(i));
                break;
            case END_TAG:
                s.endTag(null, p.getName());
                break;
            case TEXT:
                s.text(p.getText());
                break;
            }
        }
    }
}
