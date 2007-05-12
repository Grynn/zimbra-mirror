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
 * Portions created by Zimbra are Copyright (C) 2005 Zimbra, Inc.
 * All Rights Reserved.
 * 
 * Contributor(s):
 * 
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.cs.im.interop.yahoo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import com.zimbra.common.util.Pair;

/** A logical network packet (might be sent via multiple TCP packets) */
final class YMSGPacket {
    public String toString() {
        StringBuilder sb = new StringBuilder("YMSG(ver=").append(mVersion);
        sb.append(",svc=").append(mService).append('(').append(YMSGBufUtils.toHex(mService)).append(')');
        sb.append(",sta=").append(YMSGBufUtils.toHex(mStatus)).append(",ses=").append(YMSGBufUtils.toHex(mSessionId));
        sb.append(")\n");
        for (Pair<Integer, String> p : mOriginalStrings) {
            sb.append("\t").append(p.getFirst()).append(": ");
            sb.append("\t").append(p.getSecond()).append('\n');
        }
        
        return sb.toString();
    }
    
    public String getValue(int key) {
        List<String> l = mStrings.get(key);
        if (l == null)
            return null;
        else
            return l.get(0);
    }
    public Iterable<String>getValueList(int key) {
        List<String> l = mStrings.get(key);
        return l;
    }
    
    public int getIntValue(int key) {
        return Integer.parseInt(getValue(key));
    }
    public long getLongValue(int key) {
        return Long.parseLong(getValue(key));
    }
    
    Set<Map.Entry<Integer, List<String>>> entrySet() {
        return Collections.unmodifiableSet(mStrings.entrySet());
    }
    Set<Integer> keySet() { return Collections.unmodifiableSet(mStrings.keySet()); }

    public boolean containsKey(int num) { return mStrings.containsKey(num); }
    public YMSGService getServiceEnum() { return YMSGService.lookup(mService); }
    public int getService() { return mService; }
    public long getStatus() { return mStatus; }
    public YMSGStatus getStatusEnum() { return YMSGStatus.lookup(mStatus); }
    public long getSessionId() { return mSessionId; }
    
    public void setService(int service) { mService = service; }
    public void setStatus(long status) { mStatus = status; }
    public void setSessionId(long sessionId) { mSessionId = sessionId; }
    public void setService(YMSGService service) { mService = service.getValue(); }
    public void addString(int key, String value) {
        mOriginalStrings.add(new Pair<Integer, String>(key, value));
        List<String> l = mStrings.get(key);
        if (l == null) {
            l = new ArrayList<String>(1);
            mStrings.put(key, l);
        }
        l.add(value);
    }
    
    void appendPacket(YMSGPacket newPacket) {
        if (newPacket.mOriginalStrings.size() == 0)
            return;
        
        // first, check to see if the last entry id in our strings list is the same as
        // the first entry id in the new packet -- if so, then the packet got cut
        // mid-entry (and we combine the STRINGS!)
        Pair<Integer, String> myLast = mOriginalStrings.get(mOriginalStrings.size()-1);
        Pair<Integer, String> newFirst = newPacket.mOriginalStrings.get(0);
        
        if (myLast.getFirst() == newFirst.getFirst()) {
            mOriginalStrings.remove(mOriginalStrings.size()-1);
            mOriginalStrings.add(new Pair<Integer, String>(myLast.getFirst(), myLast.getSecond() + newFirst.getSecond()));
            newPacket.mOriginalStrings.remove(0);
        }
        
        for (Pair<Integer, String> p : newPacket.mOriginalStrings) {
            mOriginalStrings.add(p);
        }
        
        // rebuild mStrings -- easier for now, and mStrings is going away in the near future anyway
        mStrings = new HashMap<Integer, List<String>>();
        for (Pair<Integer, String> p : mOriginalStrings) {
            List<String> strs = mStrings.get(p.getFirst());
            if (strs == null) {
                strs = new ArrayList<String>();
                mStrings.put(p.getFirst(), strs);
            }
            strs.add(p.getSecond());
        }
    }
    
    YMSGPacket(YMSGService service, YMSGStatus status, long sessionId) {
        mVersion = YMSGHeader.YMSG_VERSION;
        mOriginalStrings = new ArrayList<Pair<Integer, String>>();
        mStrings = new HashMap<Integer, List<String>>();
        mService = service.getValue();
        mStatus = status.getNum();
        mSessionId = sessionId;
    }
    
    YMSGPacket(YMSGHeader hdr, List<Pair<Integer, String>> strs) {
        mVersion = hdr.version;
        mService = hdr.service;
        mStatus = hdr.status;
        mSessionId = hdr.service;
        mOriginalStrings = strs;
        mStrings = new HashMap<Integer, List<String>>();
        for (Pair<Integer, String> p : strs) {
            List<String> l = mStrings.get(p.getFirst());
            if (l == null) {
                l = new ArrayList<String>(1);
                mStrings.put(p.getFirst(), l);
            }
            l.add(p.getSecond());
        }
    }
    
    /**
     * There are some places in the YMSG protocol that send items in a sort of list, e.g. the buddy list might be:
     * 
     * 7: foo
     * 10: 132
     * 13: aasd
     * 7: bar
     * 10: 100
     * 7: gub
     * 10: 100
     * 
     * These have to be treated as a list of grouped items (in the above case keys {7,10,13},{7,10},{7,10}...
     * This API allows you to easily to that by "chunking" the list into the specified format based on some
     * key to split on  
     * 
     * @param key
     * @return
     */
    List<Map<Integer, String>> chunk(int key) {
        List<Map<Integer, String>> toRet = new ArrayList<Map<Integer, String>>();
        
        Map<Integer, String> cur = new HashMap<Integer, String>();
        for (Pair<Integer, String> p : mOriginalStrings) {
            if (p.getFirst() == key) {
                if (cur.size() > 0) { 
                    toRet.add(cur);
                    cur = new HashMap<Integer, String>();
                }
            }
            cur.put(p.getFirst(), p.getSecond());
        }
        if (cur.size() > 0) { 
            toRet.add(cur);
        }
        
        return toRet;
    }
    
    public List<Pair<Integer, String>> getOriginalStrings() { return mOriginalStrings; }
    private List<Pair<Integer, String>> mOriginalStrings; 

    private HashMap<Integer, List<String>> mStrings;
    private long mVersion; 
    private int mService;
    private long mStatus;
    private long mSessionId;
}
