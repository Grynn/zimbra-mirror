/*
 * ***** BEGIN LICENSE BLOCK *****
 * Version: ZPL 1.1
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.1 ("License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.zimbra.com/license
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
 * the License for the specific language governing rights and limitations
 * under the License.
 * 
 * The Original Code is: Zimbra Collaboration Suite.
 * 
 * The Initial Developer of the Original Code is Zimbra, Inc.
 * Portions created by Zimbra are Copyright (C) 2005 Zimbra, Inc.
 * All Rights Reserved.
 * 
 * Contributor(s):
 * 
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.cs.mime.charset;

import java.nio.charset.Charset;
import java.nio.charset.spi.CharsetProvider;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * @author dkarp
 */
public class UTF7Provider extends CharsetProvider {

    private static final String   UTF7_NAME    = "utf-7";
    private static final String[] UTF7_ALIASES = new String[] { "utf7", "unicode-1-1-utf-7" };
    private static final Charset  UTF7         = new UTF7(UTF7_NAME, UTF7_ALIASES);

    private static final String   IMAP_UTF7_NAME    = "imap-utf-7";
    private static final String[] IMAP_UTF7_ALIASES = new String[] { "imap-utf7" };
    private static final Charset  IMAP_UTF7         = new ImapUTF7(IMAP_UTF7_NAME, IMAP_UTF7_ALIASES);

    private static final LinkedList UTF7_LIST    = new LinkedList();
        static {
            UTF7_LIST.add(UTF7);
            UTF7_LIST.add(IMAP_UTF7);
        }

	public Iterator charsets() {
		return UTF7_LIST.iterator();
	}

	public Charset charsetForName(String charsetName) {
        if (UTF7_NAME.equalsIgnoreCase(charsetName))
            return UTF7;
        for (int i = 0; i < UTF7_ALIASES.length; i++)
            if (UTF7_ALIASES[i].equalsIgnoreCase(charsetName))
                return UTF7;

        if (IMAP_UTF7_NAME.equalsIgnoreCase(charsetName))
            return IMAP_UTF7;
        for (int i = 0; i < IMAP_UTF7_ALIASES.length; i++)
            if (IMAP_UTF7_ALIASES[i].equalsIgnoreCase(charsetName))
                return IMAP_UTF7;

        return null;
	}
}
