/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2007 Zimbra, Inc.
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
package com.zimbra.cs.im.interop.yahoo;

import java.util.HashSet;
import java.util.Iterator;

/**
 * 
 */
public class YahooGroup {
    YahooGroup(String name) { 
        mName = name;
    }
    
    public String getName() { return mName; }
    public synchronized void addBuddy(YahooBuddy buddy) {
        mBuddies.add(buddy);
    }        
    public synchronized void removeBuddy(YahooBuddy buddy) {
        mBuddies.remove(buddy);
    }
    public synchronized boolean containsName(String name) {
        for (Iterator<YahooBuddy> iter = mBuddies.iterator(); iter.hasNext();) {
            YahooBuddy b = iter.next();
            if (b.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }
    public synchronized boolean contains(YahooBuddy buddy) {
        return mBuddies.contains(buddy);
    }
    public synchronized void removeByName(String name) {
        for (Iterator<YahooBuddy> iter = mBuddies.iterator(); iter.hasNext();) {
            YahooBuddy b = iter.next();
            if (b.getName().equals(name)) {
                iter.remove();
                return;
            }
        }
    }
    public Iterable<YahooBuddy> buddies() { return mBuddies; }
    
    public String toString() { 
        StringBuilder sb = new StringBuilder("GROUP("+mName+", "+mBuddies.size()+" entries)");
//        for (YahooBuddy b : mBuddies) {
//            sb.append("\n\t").append(b.toString());
//        }
        return sb.toString();
    }
    
    private String mName;
    private HashSet<YahooBuddy> mBuddies = new HashSet<YahooBuddy>();
}
