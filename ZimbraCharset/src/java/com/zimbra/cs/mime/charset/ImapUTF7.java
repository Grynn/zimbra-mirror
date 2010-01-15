/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2005, 2007, 2009 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.2 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.cs.mime.charset;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;

/**
 * @author dkarp
 */
public class ImapUTF7 extends UTF7 {

    /**
     * @param canonicalName
     * @param aliases
     */
    ImapUTF7(String canonicalName, String[] aliases) {
        super(canonicalName, aliases);

        // RFC 3501 5.1.3: ""&" is used to shift to modified BASE64 and "-" to shift back to US-ASCII."
        BEGIN_SHIFT = '&';

        // RFC 3501 5.1.3: "All other characters (octet values 0x00-0x1f and 0x7f-0xff) are represented in modified
        //                  BASE64, with a further modification from [UTF-7] that "," is used instead of "/"." 
        BASE_64[63] = ',';

        for (int i = 0; i < INVERSE_BASE_64.length; i++)
            INVERSE_BASE_64[i] = NON_BASE_64;
        for (byte i = 0; i < BASE_64.length; i++)
            INVERSE_BASE_64[BASE_64[i]] = i;

        // RFC 3501 5.1.3: "In modified UTF-7, printable US-ASCII characters, except for "&", represent
        //                  themselves; that is, characters with octet values 0x20-0x25 and 0x27-0x7e."
        for (int i = 0; i < NO_SHIFT_REQUIRED.length; i++)
            NO_SHIFT_REQUIRED[i] = (i >= 0x20 && i <= 0x7e && i != '&');
    }

    public static void main(String[] args) throws UnsupportedEncodingException {
        Charset imapUTF7 = new ImapUTF7("imap-utf-7", new String[] {});
        ByteBuffer bb = imapUTF7.encode("/go&h\\+o");
        byte[] content = new byte[bb.limit()];
        System.arraycopy(bb.array(), 0, content, 0, content.length);
        System.out.println(new String(content, "US-ASCII"));

        String test = "~peter/mail/&U,BTFw-/&ZeVnLIqe-";
        CharBuffer result = imapUTF7.decode(ByteBuffer.wrap(test.getBytes("US-ASCII")));
        System.out.println(result);
        
        bb = imapUTF7.encode(result);
        content = new byte[bb.limit()];
        System.arraycopy(bb.array(), 0, content, 0, content.length);
        System.out.println(new String(content, "US-ASCII"));

        Charset utf7 = new UTF7("utf-7", new String[] {});
        bb = utf7.encode("/go&h\\+o");
        content = new byte[bb.limit()];
        System.arraycopy(bb.array(), 0, content, 0, content.length);
        System.out.println(new String(content, "US-ASCII"));

        result = imapUTF7.decode(ByteBuffer.wrap(test.getBytes("US-ASCII")));
        bb = utf7.encode(result);
        content = new byte[bb.limit()];
        System.arraycopy(bb.array(), 0, content, 0, content.length);
        System.out.println(new String(content, "US-ASCII"));
    }
}
