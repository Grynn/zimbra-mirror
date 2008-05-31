/*
 * ***** BEGIN LICENSE BLOCK *****
 *
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2007, 2008 Zimbra, Inc.
 *
 * The contents of this file are subject to the Yahoo! Public License
 * Version 1.0 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 *
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.cs.account.offline;

import java.util.Properties;

class EProperties extends Properties {
    private static final long serialVersionUID = -8135956477865965194L;

    @Override
    public String getProperty(String key, String defaultValue) {
        String val = super.getProperty(key, defaultValue);
        return val == null ? null : val.trim();
    }

    @Override
    public String getProperty(String key) {
        String val = super.getProperty(key);
        return val == null ? null : val.trim();
    }

    public int getPropertyAsInteger(String key, int defaultValue) {
        String val = getProperty(key);
        if (val == null || val.length() == 0)
            return defaultValue;
        try {
            return Integer.parseInt(val);
        } catch (NumberFormatException x) {
            return defaultValue;
        }
    }

    public boolean getPropertyAsBoolean(String key, boolean defaultValue) {
        String val = getProperty(key);
        if (val == null || val.length() == 0)
            return defaultValue;
        return Boolean.parseBoolean(val);
    }

    public String getNumberedProperty(String prefix, int number, String suffix) {
        return getProperty(prefix + '.' + number + '.' + suffix);
    }

    public String getNumberedProperty(String prefix, int number, String suffix,
                                      String defaultValue) {
        return getProperty(prefix + '.' + number + '.' + suffix, defaultValue);
    }

    public int getNumberedPropertyAsInteger(String prefix, int number, String suffix, int defaultValue) {
        String val = getNumberedProperty(prefix, number, suffix);
        if (val == null || val.length() == 0)
            return defaultValue;
        try {
            return Integer.parseInt(val);
        } catch (NumberFormatException x) {
            return defaultValue;
        }
    }

    public String getNumberedProperty(String prefix, int n1, String midfix, int n2, String suffix) {
        return getProperty(prefix + '.' + n1 + '.' + midfix + '.' + n2 + '.' + suffix);
    }

    public boolean getNumberedPropertyAsBoolean(String prefix, int n1, String midfix, int n2, String suffix, boolean defaultValue) {
        String val = getProperty(prefix + '.' + n1 + '.' + midfix + '.' + n2 + '.' + suffix);
        if (val == null || val.length() == 0)
            return defaultValue;
        return Boolean.parseBoolean(val);
    }
}
