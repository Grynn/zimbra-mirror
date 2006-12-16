/*
 * ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1
 *
 * The contents of this file are subject to the Mozilla Public License
 * Version 1.1 ("License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.zimbra.com/license
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
 * the License for the specific language governing rights and limitations
 * under the License.
 *
 * The Original Code is: Zimbra Collaboration Suite Server.
 *
 * The Initial Developer of the Original Code is Zimbra, Inc.
 * Portions created by Zimbra are Copyright (C) 2006 Zimbra, Inc.
 * All Rights Reserved.
 *
 * Contributor(s):
 *
 * ***** END LICENSE BLOCK *****
 */

/**
 * This static class provides a simple localization mechanism that searches for a localization file for the user's
 * locale. A simple fallback mechanism is provided to search for property files. Currently no overriding mechanism is
 * provided i.e. all messages must be defined in all files e.g. en-GB must define all the messages in en and cannot
 * simply override the ones that are different
 */
package com.zimbra.zme;

import java.io.*;
import java.util.Hashtable;

public class Messages {

    private static Hashtable mResTable;

    /**
     * This method should be called to load the message property file for the provided locale. If no locale is provided
     * then the device's locale will be used. Property files will be searched for using a fallback mechanism. For
     * example assume a locale of en-GB, then first msg_en-GB is probed, if not found then msg_en, then finally msg
     *
     * @param locale
     */
    public static void loadMsgFile(String locale)
            throws ZmeException {

        if (locale == null)
            locale = System.getProperty("microedition.locale");

        InputStreamReader r;

        try {
            r = getMsgStreamReader(locale);
            mResTable = new Hashtable();
            StringBuffer key = new StringBuffer();
            StringBuffer value = new StringBuffer();

            //Read each line as key: value
            while(true){
              if (readLine(key, value, r) == -1)
                break;

              mResTable.put(key, value);
            }
        } catch (Exception ex) {
            if (ex instanceof ZmeException)
                throw (ZmeException)ex;
            throw new ZmeException(ZmeException.ERROR_READING_MSG_FILE, null);
        }
    }

    /**
     * Returns the message corresponding to the provided property
     * @param prop
     * @return
     */
    public static String getMsgForProperty(String prop) {
        return (String)mResTable.get(prop);
    }

    private static InputStreamReader getMsgStreamReader(String locale)
            throws UnsupportedEncodingException,
                   ZmeException {
        Class msgs = null;

        try {
            msgs = Class.forName("com.zimbra.zme.Messages");
        } catch (Throwable t) {
            // Do Nothing since this cannot happen
        }

        InputStream is = null;
        locale = "_" + locale;
        while (is == null) {
            System.out.println("Searching for property file: " + "/msgs" + locale + ".properties");
            is = msgs.getResourceAsStream("/msgs" + locale + ".properties");
            if (is == null) {
                if (locale.compareTo("") != 0) {
                    int idx = locale.indexOf('-');
                    locale = (idx != -1) ? locale.substring(0, idx) : "";
                } else {
                    // Couldn't find any matching propert files
                    throw new ZmeException(ZmeException.MSG_FILE_NOT_FOUND, null);
                }
            }
        }
        return new InputStreamReader(is, "UTF-8");
    }

    private static int readLine(StringBuffer key,
                                StringBuffer value,
                                Reader stream)
            throws IOException {
        int c;

        // Read key
        while (true) {
            if ((c = stream.read()) == -1)
                return c;

            //Read key until delimiter or space
            if (c == '=')
                break;
            System.out.println("KEY APPEND: " + (char)c);
            key.append(c);
        }

        //Read value until end of line
        while (true) {
            if ((c = stream.read()) == -1)
                return c;

            if (c == '\n')
                break;
            else if (c== '\r')
                continue;

            System.out.println("VALUE APPEND: " + (char)c);
            value.append(c);
        }
        return 0;
    }
}


