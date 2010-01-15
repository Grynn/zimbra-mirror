/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2006, 2007, 2009 Zimbra, Inc.
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

import java.util.HashMap;
import java.util.Map;

public class MacintoshCE extends Macintosh {

    private static final char[] MAC_CE_HIGH_CHAR_MAP = new char[] {
        '\u00C4', '\u0100', '\u0101', '\u00C9', '\u0104', '\u00D6', '\u00DC', '\u00E1',
        '\u0105', '\u010C', '\u00E4', '\u010D', '\u0106', '\u0107', '\u00E9', '\u0179',
        '\u017A', '\u010E', '\u00ED', '\u010F', '\u0112', '\u0113', '\u0116', '\u00F3',
        '\u0117', '\u00F4', '\u00F6', '\u00F5', '\u00FA', '\u011A', '\u011B', '\u00FC',
        '\u2020', '\u00B0', '\u0118', '\u00A3', '\u00A7', '\u2022', '\u00B6', '\u00DF',
        '\u00AE', '\u00A9', '\u2122', '\u0119', '\u00A8', '\u2260', '\u0123', '\u012E',
        '\u012F', '\u012A', '\u2264', '\u2265', '\u012B', '\u0136', '\u2202', '\u2211',
        '\u0142', '\u013B', '\u013C', '\u013D', '\u013E', '\u0139', '\u013A', '\u0145',
        '\u0146', '\u0143', '\u00AC', '\u221A', '\u0144', '\u0147', '\u2206', '\u00AB',
        '\u00BB', '\u2026', '\u00A0', '\u0148', '\u0150', '\u00D5', '\u0151', '\u014C',
        '\u2013', '\u2014', '\u201C', '\u201D', '\u2018', '\u2019', '\u00F7', '\u25CA',
        '\u014D', '\u0154', '\u0155', '\u0158', '\u2039', '\u203A', '\u0159', '\u0156',
        '\u0157', '\u0160', '\u201A', '\u201E', '\u0161', '\u015A', '\u015B', '\u00C1',
        '\u0164', '\u0165', '\u00CD', '\u017D', '\u017E', '\u016A', '\u00D3', '\u00D4',
        '\u016B', '\u016E', '\u00DA', '\u016F', '\u0170', '\u0171', '\u0172', '\u0173',
        '\u00DD', '\u00FD', '\u0137', '\u017B', '\u0141', '\u017C', '\u0122', '\u02C7',
    };

    private static final Map<Character, Byte> MAC_CE_ENCODABLE_CHARS = new HashMap<Character, Byte>();
        static {
            for (int i = 0; i < MAC_CE_HIGH_CHAR_MAP.length; i++)
                MAC_CE_ENCODABLE_CHARS.put(MAC_CE_HIGH_CHAR_MAP[i], (byte) (i + 0x80));
        }

    MacintoshCE(String canonicalName, String[] aliases) {
        super(canonicalName, aliases);

        HIGH_CHAR_MAP   = MAC_CE_HIGH_CHAR_MAP;
        ENCODABLE_CHARS = MAC_CE_ENCODABLE_CHARS;
    }
}
