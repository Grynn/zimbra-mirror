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
import java.util.Map;
import java.util.Set;

/**
 *
 * A logical network packet (might be sent via multiple TCP packets)
 *
*/
class YMSGPacket {
//    public static YMSGPacket parse(byte[] buf) throws IOException {
//        return new YMSGPacket(buf);
//    }
    
    public String toString() {
        StringBuilder sb = new StringBuilder("YMSG(ver=").append(mVersion);
        sb.append(",svc=").append(mService).append(",sta=");
        sb.append(BufUtils.toHex(mStatus)).append(",ses=").append(BufUtils.toHex(mSessionId));
        sb.append(")\n");
        ArrayList<Integer> keys = new ArrayList<Integer>();
        keys.addAll(mStrings.keySet());
        Collections.sort(keys);
        for (Integer key : keys) {
            sb.append("\t").append(key).append(": ");
            sb.append("\t").append(mStrings.get(key)).append('\n');
            
        }
        return sb.toString();
    }
    
    public String getValue(int key) {
        return mStrings.get(key);
    }
    public int getIntValue(int key) {
        return Integer.parseInt(mStrings.get(key));
    }
    public long getLongValue(int key) {
        return Long.parseLong(mStrings.get(key));
    }
    
    
    Set<Map.Entry<Integer, String>> entrySet() {
        return Collections.unmodifiableSet(mStrings.entrySet());
    }
    Set<Integer> keySet() { return Collections.unmodifiableSet(mStrings.keySet()); }

    public boolean containsKey(int num) { return mStrings.containsKey(num); }
    public YMSGService getServiceEnum() { return YMSGService.lookup(mService); }
    public int getService() { return mService; }
    public long getStatus() { return mStatus; }
    public YahooStatus getStatusEnum() { return YahooStatus.lookup(mStatus); }
    public long getSessionId() { return mSessionId; }
    
    public void setService(int service) { mService = service; }
    public void setStatus(long status) { mStatus = status; }
    public void setSessionId(long sessionId) { mSessionId = sessionId; }
    public void setService(YMSGService service) { mService = service.getValue(); }
    public void addString(int key, String value) { mStrings.put(key, value); }
    
    void append(YMSGPacket packet) {
        for (Map.Entry<Integer, String> entry : packet.entrySet()) {
            if (!mStrings.containsKey(entry.getKey())) {
                mStrings.put(entry.getKey(), entry.getValue());
            } else {
                mStrings.put(entry.getKey(), mStrings.get(entry.getKey())+entry.getValue());
            }
        }
    }
    
    YMSGPacket(YMSGService service, YahooStatus status, long sessionId) {
        mVersion = YMSGHeader.YMSG_VERSION;
        mStrings = new HashMap<Integer, String>();
        mService = service.getValue();
        mStatus = status.getNum();
        mSessionId = sessionId;
    }
    
    YMSGPacket(YMSGHeader hdr, HashMap<Integer, String> strings) {
        mVersion = hdr.version;
        mService = hdr.service;
        mStatus = hdr.status;
        mSessionId = hdr.service;
        mStrings = strings;
    }

//    private YMSGPacket(byte[] buf) throws IOException {
//        mStrings = new HashMap<String, ArrayList<String>>();
//        
//        if (mHeader.length > 0) {
//            int startIdx = YMSGHeader.HEADER_LENGTH;
//            String key = null;
//            
//            for (int i = YMSGHeader.HEADER_LENGTH; i < YMSGHeader.HEADER_LENGTH + mHeader.length; i+=2) {
//                if (buf[i] == 0xc0 && buf[i+1] == 0x80) {
//                    String s = new String(buf, startIdx, i-startIdx, "UTF-8");
//                    if (key == null) {
//                        key = s;
//                    } else {
//                        ArrayList<String> l = mStrings.get(key);
//                        if (l == null) {
//                            l = new ArrayList<String>(1);
//                            mStrings.put(key, l);
//                        }
//                        l.add(s);
//                        key = null;
//                    }
//                    startIdx = i+2;
//                }
//            }
//        }
//    }
    
    
    private HashMap<Integer, String> mStrings;
    private long mVersion; 
    private int mService;
    private long mStatus;
    private long mSessionId;
}
