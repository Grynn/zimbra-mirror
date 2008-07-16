/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2006, 2007 Zimbra, Inc.
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
package org.jivesoftware.util;

import java.util.Locale;

import com.zimbra.common.localconfig.KnownKey;

public abstract class IMConfigProperty {
    public IMConfigProperty(String description) { mDescription = description; }
    protected String mDescription;
    
    public String getDescription() { return mDescription; }
    public String getDescription(Locale locale) { return mDescription; }
    public String[] getStrings() { throw new IllegalArgumentException("Not a string[] type"); };
    public String getString() { throw new IllegalArgumentException("Not a string type"); };
    public boolean getBoolean() { throw new IllegalArgumentException("Not a boolean type"); };
    public int getInt() { throw new IllegalArgumentException("Not an int type"); }
    public void setString(String str) { throw new IllegalArgumentException("SetString not supported"); }
    public void setBoolean(boolean value) { throw new IllegalArgumentException("SetBoolean not supported"); }
    public void setInt(int value)  { throw new IllegalArgumentException("SetIntnot supported"); }
    
    public static class ConstantBoolean extends IMConfigProperty {
        public ConstantBoolean(boolean value, String description) {
            super(description);
            mValue = value;
        }
        public boolean getBoolean() { return mValue; }
        protected boolean mValue;
    }
    public static class ConstantInt extends IMConfigProperty {
        public ConstantInt(int value, String description) {
            super(description);
            mValue = value;
        }
        public int getInt() { return mValue; }
        protected int mValue;
    }
    public static class ConstantStr extends IMConfigProperty {
        public ConstantStr(String value, String description) {
            super(description);
            mValue = value;
        }
        public String getString() { return mValue; }
        protected String mValue;
    }
    public static class SettableStr extends IMConfigProperty {
        public SettableStr(String value, String description) {
            super(description);
            mValue = value;
        }
        public String getString() { return mValue; }
        public void setString(String str) { mValue = str; }
        protected String mValue;
    }
    public static class ConstantStrList extends IMConfigProperty {
        public ConstantStrList(String[] values, String description) {
            super(description);
            mValues = values;
        }
        public String[] getStrings() { return mValues; }
        protected String[] mValues;
    }
    public static class LCValue extends IMConfigProperty {
        public LCValue(KnownKey key) {
            super(null);
            mKey = key;
        }
        public String getDescription() { return mKey.doc(); }
        public String getDescription(Locale locale) { return mKey.doc(locale); }
        protected KnownKey mKey;
    }
    public static class LCStr extends LCValue {
        public LCStr(KnownKey key) {
            super(key);
        }
        public String getString() { return mKey.value(); }
    }
    public static class LCStrList extends LCValue {
        public LCStrList(KnownKey key) {
            super(key);
        }
        public String[] getStrings() { 
            String value = mKey.value();
            if (value != null && value.length() > 0) {
                String[] strs = value.split(",");
                for (int i = 0; i < strs.length; i++) {
                    strs[i] = strs[i].trim();
                }
                return strs;
            } else {
                return new String[]{};
            }
        }
    }
    
    public static class LCBoolean extends LCValue {
        public LCBoolean(KnownKey key) {
            super(key);
        }
        public boolean getBoolean() { return mKey.booleanValue(); }
    }
    public static class LCInt extends LCValue {
        public LCInt(KnownKey key) {
            super(key);
        }
        public int getInt() { return mKey.intValue(); }
    }
    public static class ProvInt extends IMConfigProperty {
        public ProvInt(String keyName) {
            super(null);
        }
    };
}