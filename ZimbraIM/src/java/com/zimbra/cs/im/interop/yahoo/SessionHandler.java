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

import java.util.HashMap;
import java.util.Map;

import org.apache.mina.common.IoHandlerAdapter;
import org.apache.mina.common.IoSession;
import org.apache.mina.handler.multiton.SingleSessionIoHandlerAdapter;

/**
 * 
 */
public class SessionHandler extends IoHandlerAdapter {
    public void sessionOpened( IoSession session )
    {
        mSession = session;
        // request an AUTH challenge
        YMSGPacket packet = new YMSGPacket(YMSGService.AUTH, YMSGStatus.NONE, 0);
        packet.addString(1, "op_tim_brennan2");
        session.write(packet);
    }
    
    public void messageReceived( IoSession session, Object message )
    {
        assert(session == mSession);
        YMSGPacket packet = (YMSGPacket)message;
        
        System.out.println("Got a YMSGPacket: "+packet.toString());
        
        switch (packet.getServiceEnum()) {
            case UNKNOWN:
                System.out.println("Unknown YMSG Service "+packet.getService()+(" ("+
                            BufUtils.toHex(packet.getService())+") in incoming packet"));
                break;
            case PING:
                break;
            case MESSAGE:
                handleMessage(packet);
                break;
            case AUTH:
                try {
                    String challenge = packet.getValue(94);
                    String[] response = mAuthProvider.calculateChallengeResponse(mLoginId, mPassword, challenge);
                    YMSGPacket resp = new YMSGPacket(YMSGService.AUTH_RESPONSE, YMSGStatus.NONE, 0);
                    resp.addString(0, mLoginId);
                    resp.addString(6, response[0]);
                    resp.addString(96, response[1]);
                    resp.addString(2, "1");
                    resp.addString(1, mLoginId);
                    session.write(resp);
                } catch (Exception e) {
                    e.printStackTrace();
                    session.close();
                }
                break;
            case AUTH_RESPONSE:
                System.out.println("Auth Failed: " +packet.toString());
                session.close();
                mListener.authFailed();
                break;
            case LIST:
                if (mPartialPacket == null) {
                    mPartialPacket = packet;
                } else {
                    mPartialPacket.append(packet);
                    mPartialPacket.setStatus(packet.getStatus());
                }
                if (mPartialPacket.getStatus() == 0) {
                    handleList(mPartialPacket);
                    mListener.receivedBuddyList();
                    mPartialPacket = null; // done!
                }
                break;
            case LOGON:
            case LOGOFF:
            case AWAY:
            case BACK:
                handleStatus(packet);
                break;
            case PICTURE:
                break;
            default:
                System.out.println("Unhandled YMSG Service "+packet.getServiceEnum().name()+" "+
                    packet.getService()+("0x"+
                                BufUtils.toHex(packet.getService())+") in incoming packet"));
        }
    }
    

    public void exceptionCaught( IoSession session, Throwable cause )
    {
        assert(session == mSession);
        cause.printStackTrace();
        session.close();
    }
    
    private void handleMessage(YMSGPacket packet) {
        long time = System.currentTimeMillis();
        String from = "";
        String to = "";
        String msg = "";
        
        for (Integer key : packet.keySet()) {
            switch (key) {
                case 4: // from
                    from = packet.getValue(key);
                    break;
                case 5: // to
                    to = packet.getValue(key);
                    break;
                case 14: // message part
                    msg = packet.getValue(key);
                    break;
                case 15: // time
                    time = packet.getLongValue(key) * 1000;
                    break;
            }
        }
        YahooMessage ymsg = new YahooMessage(time, from, to, msg);
        mListener.receivedMessage(ymsg);
    }
    
    private void handleList(YMSGPacket packet) {
        System.out.println("handleList()");
        for (Integer key : packet.keySet()) {
            switch (key) {
                case 87: // buddy list
                    for (String line : packet.getValue(key).split("\n")) {
                        String[] values = line.split(":"); // GROUP:buddy,buddy,buddy...
                        YahooGroup group = findOrCreateGroup(values[0]);
                        for (String buddyName : values[1].split(",")) {
                            YahooBuddy buddy = findOrCreateBuddy(buddyName);
                            group.addBuddy(buddy);
                        }
                    }
                    break;
                case 88: // ignore list
                    for (String name : packet.getValue(key).split(",")) {
                        YahooBuddy buddy = findOrCreateBuddy(name);
                        buddy.setIgnore(true);
                    }
                    break;
                case 89: // identities list
                    break;
                case 59: // cookies
                    mCookies = readCookies(packet);
                    break;
                case 185: // libgaim says "presence_perm"? 
                    break;
                    
            }
        }
    }
    
    private void handleStatus(YMSGPacket packet) {
        if (packet.containsKey(1)) {
            if (!mIsLoggedOn) {
                mIsLoggedOn = true;
                mDisplayName = packet.getValue(1);
                mListener.loggedOn();
            }
        }
        
        if (packet.containsKey(8)) {
            mNumBuddiesOnline = packet.getIntValue(8);
        }

        YahooBuddy buddy = null;
        
        if (packet.containsKey(7)) { // current buddy
            buddy = findOrCreateBuddy(packet.getValue(7));
        }
        
        if (buddy != null) {
            if (packet.containsKey(10)) { // state
                long status = packet.getLongValue(10);
                buddy.setStatus(YahooStatus.lookup(status));
            }
            YahooEventListener.StatusChangeType type = YahooEventListener.StatusChangeType.OTHER;
            switch(YMSGService.lookup(packet.getService())) {
                case LOGON:
                    type = YahooEventListener.StatusChangeType.LOGON;
                    break;
                case LOGOFF:
                    type = YahooEventListener.StatusChangeType.LOGOFF;
                    break;
                case AWAY:
                    type = YahooEventListener.StatusChangeType.AWAY;
                    break;
                case BACK:
                    type = YahooEventListener.StatusChangeType.BACK;
                    break;
            }
            mListener.buddyStatusChanged(type, buddy);
        }
    }
    
    private YahooBuddy findOrCreateBuddy(String name) {
        YahooBuddy toRet = mBuddies.get(name);
        if (toRet == null) {
            toRet = new YahooBuddy(name);
            mBuddies.put(name, toRet);
        }
        return toRet;
    }
    private YahooGroup findOrCreateGroup(String name) {
        YahooGroup toRet = mGroups.get(name);
        if (toRet == null) {
            toRet = new YahooGroup(name);
            mGroups.put(name, toRet);
        }
        return toRet;
    }
    
    private static Cookies readCookies(YMSGPacket packet) {
        String list = packet.getValue(59);
        if (list == null)
            return null;
        
        Cookies toRet = new Cookies();
        
        for (String s : list.split(";")) {
            switch (s.charAt(0)) {
                case 'Y':
                    toRet.y = s.substring(2);
                    break;
                case 'T':
                    toRet.t = s.substring(2);
                    break;
                case 'C':
                    toRet.c = s.substring(2);
                    break;
                default:
                    System.out.println("Unknown cookie type: "+s);
            }
        }
        return toRet;
    }
    
    SessionHandler(YMSGAuthProvider provider, YahooEventListener listener, String loginId, String password) {
        mAuthProvider = provider;
        mListener = listener;
        mLoginId = loginId;
        mPassword = password;
    }

    private String mLoginId;
    private String mPassword;
    private IoSession mSession;

    private YMSGAuthProvider mAuthProvider = null;
    private YMSGPacket mPartialPacket = null;
    
    private boolean mIsLoggedOn = false;
    private String mDisplayName = null;
    private int mNumBuddiesOnline = 0;
    private Cookies mCookies = null;
    private HashMap<String, YahooBuddy> mBuddies = new HashMap<String, YahooBuddy>();
    private HashMap<String, YahooGroup> mGroups = new HashMap<String, YahooGroup>();
    
    private YahooEventListener mListener;
    
    static class Cookies {
        public String y;
        public String t;
        public String c;
    }
    
}