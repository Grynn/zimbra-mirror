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

import java.util.Collections;
import java.util.HashMap;
import java.util.concurrent.Callable;

import org.apache.mina.common.ConnectFuture;
import org.apache.mina.common.IdleStatus;
import org.apache.mina.common.IoFuture;
import org.apache.mina.common.IoFutureListener;
import org.apache.mina.common.IoHandler;
import org.apache.mina.common.IoSession;

import com.zimbra.common.util.Constants;
import com.zimbra.common.util.Log;
import com.zimbra.common.util.LogFactory;
import com.zimbra.common.util.StringUtil;
import com.zimbra.common.util.TaskScheduler;

/**
 * 
 */
class YMSGSession implements IoHandler, YahooSession, IoFutureListener  {
    
    /* @see org.apache.mina.common.IoFutureListener#operationComplete(org.apache.mina.common.IoFuture) */
    public void operationComplete(IoFuture future) {
        ConnectFuture connect  = (ConnectFuture)future;
        sLog.debug("Socket Connect Complete: %s", 
            (connect.isConnected() ? "SUCCESSFUL" : "FAILED"));
        if (!connect.isConnected())
            mListener.connectFailed(this);
    }
    
    public void messageSent(IoSession session, Object message) throws Exception { }
    public void sessionClosed(IoSession session) throws Exception {
        sLog.debug("Session Closed");
        mIsLoggedOn = false;
        stopPinging();
        mListener.sessionClosed(this);
    }

    public void sessionCreated(IoSession session) throws Exception { }
    public void sessionIdle(IoSession session, IdleStatus status) throws Exception { }
    
    private final class PingTask implements Callable<Void> {
        public Void call() {
            sLog.debug("Sending PING packet to yahoo service");
            sendPing();
            return null;
        }
    }

    static final class Cookies {
        public String c;
        public String t;
        public String y;
    }

    public static final Log sLog = LogFactory.getLog("zimbra.im.interop.yahoo");
//    public static final Log sPacketLog = LogFactory.getLog("zimbra.im.interop.yahoo.packet");
    
    private static final TaskScheduler<Void> sScheduler = new TaskScheduler<Void>("YahooInterop", 1, 1);
    
    private Cookies readCookies(YMSGPacket packet) {
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
                    sLog.debug("Unknown cookie type: "+s);
            }
        }
        return toRet;
    }

    YMSGSession(YMSGAuthProvider provider, YahooEventListener listener, String loginId, String password) {
        mAuthProvider = provider;
        mListener = listener;
        mLoginId = loginId;
        mPassword = password;
    }
    
    public void addBuddy(String id, String group) {
        if (group == null || group.length() == 0)
            group = "Buddies";
        
        YMSGPacket msg = new YMSGPacket(YMSGService.ADDBUDDY, YMSGStatus.NONE, mSessionId);
        msg.addString(0, mLoginId);
        msg.addString(1, mLoginId);
        msg.addString(7, id);
        msg.addString(14, "");
        msg.addString(65, group);
        writePacket(msg);
    }
    
    /* (non-Javadoc)
     * @see com.zimbra.cs.im.interop.yahoo.YahooSession#buddies()
     */
    public Iterable<YahooBuddy> buddies() {
        return Collections.unmodifiableCollection(mBuddies.values());
    }
    
    /* (non-Javadoc)
     * @see com.zimbra.cs.im.interop.yahoo.YahooSession#disconnect()
     */
    public void disconnect() {
        if (mIsLoggedOn) {
            YMSGPacket msg = new YMSGPacket(YMSGService.LOGOFF, YMSGStatus.AVAILABLE, mSessionId);
            msg.addString(0, mLoginId);
            writePacket(msg); 
            mSession.close();
        }
        mIsLoggedOn = false;
    }
    
    public void exceptionCaught( IoSession session, Throwable cause )
    {
        assert(session == mSession);
        sLog.error(this.toString()+"ExceptionCaught: "+cause.toString());
        cause.printStackTrace();
//        session.close();
    }
    
    /* (non-Javadoc)
     * @see com.zimbra.cs.im.interop.yahoo.YahooSession#groups()
     */
    public Iterable<YahooGroup> groups() {
        return Collections.unmodifiableCollection(mGroups.values());
    }
    
    public void messageReceived( IoSession session, Object message )
    {
        assert(session == mSession);
        YMSGPacket packet = (YMSGPacket)message;
        
//        if (sPacketLog.isDebugEnabled())
//            sPacketLog.info(this.toString()+"\nRECEIVED PACKET: "+packet.toString());
        sLog.debug("Received packet: %s", packet.toString());
        
        if (packet.getSessionId() != 0)
            mSessionId = packet.getSessionId();
        
        switch (packet.getServiceEnum()) {
            case UNKNOWN:
                sLog.debug("Unknown YMSG Service "+packet.getService()+(" ("+
                            YMSGBufUtils.toHex(packet.getService())+") in incoming packet"));
                break;
            case PING:
                sLog.debug("Received a PING packet: "+packet.toString());
                break;
            case MESSAGE:
                handleMessage(packet);
                break;
            case AUTH:
                try {
                    String challenge = packet.getValue(94);
                    String[] response = mAuthProvider.calculateChallengeResponse(mLoginId, mPassword, challenge);
                    mPassword = null;
                    YMSGPacket resp = new YMSGPacket(YMSGService.AUTH_RESPONSE, YMSGStatus.NONE, mSessionId);
                    resp.addString(0, mLoginId);
                    resp.addString(6, response[0]);
                    resp.addString(96, response[1]);
                    resp.addString(2, "1");
                    resp.addString(1, mLoginId);
                    writePacket(resp);
                } catch (Exception e) {
                    e.printStackTrace();
                    session.close();
                }
                break;
            case AUTH_RESPONSE:
                sLog.info("Auth Failed: " +packet.toString());
                session.close();
                mListener.authFailed(this);
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
                    // don't signal the buddy list until after the logon is
                    // completted (when we receive our first status update for us
                    //mListener.receivedBuddyList(this);
                    mPartialPacket = null; // done!
                }
                break;
            case TYPING:
                handleTyping(packet);
                break;
            case LOGON:
                handleStatus(packet);
                break;
            case LOGOFF:
            case AWAY:
            case BACK:
            case Y6_STATUS_UPDATE:
            case STATUS_15:
                handleStatus(packet);
                break;
            case PICTURE:
                break;
            case ADDBUDDY:
                handleAddbuddy(packet);
                break;
            case REMBUDDY:
                handleRembuddy(packet);
                break;
            case NEW_CONTACT:
                handleNewContact(packet);
                break;
            default:
                sLog.debug("Unhandled YMSG Service "+packet.getServiceEnum().name()+" "+
                    packet.getService()+("0x"+
                                YMSGBufUtils.toHex(packet.getService())+") in incoming packet"));
        }
    }
    
    public YahooBuddy getBuddy(String id) {
        return mBuddies.get(id);
    }
    
    public YahooGroup getGroup(String id) {
        return mGroups.get(id);
    }
    
    public void removeBuddy(String id, String group) {
        YMSGPacket msg = new YMSGPacket(YMSGService.REMBUDDY, YMSGStatus.NONE, mSessionId);
        msg.addString(1, mLoginId);
        msg.addString(7, id);
        msg.addString(65, group);
        writePacket(msg);
    }
    
    /* (non-Javadoc)
     * @see com.zimbra.cs.im.interop.yahoo.YahooSession#sendMessage(java.lang.String, java.lang.String)
     */
    public void sendMessage(String dest, String message) {
        if (!mIsLoggedOn)
            throw new IllegalStateException("Not logged on");
        
        YMSGPacket msg = new YMSGPacket(YMSGService.MESSAGE, YMSGStatus.OFFLINE, mSessionId);
        msg.addString(0, mLoginId);
        msg.addString(1, mLoginId);
        msg.addString(5, dest);
        msg.addString(14, message);
        
        writePacket(msg);
    }
    
    public void sessionOpened( IoSession session )
    {
        sLog.debug(this.toString()+"SessionOpened");
        mSession = session;
        // request an AUTH challenge
        YMSGPacket packet = new YMSGPacket(YMSGService.AUTH, YMSGStatus.NONE, mSessionId);
        packet.addString(1, "op_tim_brennan2");
        writePacket(packet);
    }
    /* (non-Javadoc)
     * @see com.zimbra.cs.im.interop.yahoo.YahooSession#setMyStatus(com.zimbra.cs.im.interop.yahoo.YahooStatus)
     */
    public void setMyStatus(YMSGStatus status, String customStatusMsg) {
        mMyStatus = status;
        
//        YMSGService svc = YMSGService.AWAY;
//        if (status == YahooStatus.AVAILABLE)
//            svc = YMSGService.BACK;
        YMSGService svc = YMSGService.Y6_STATUS_UPDATE;
        
        
        YMSGPacket msg = new YMSGPacket(svc, YMSGStatus.AVAILABLE, mSessionId);
        msg.addString(0, mLoginId);
        msg.addString(1, mLoginId);
        msg.addString(10, Long.toString(status.getNum()));
        if (customStatusMsg != null)
            msg.addString(19, customStatusMsg);
        // 47--> 2 means IDLE,  47-->1 means unavailable
        writePacket(msg);
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
    
    private void handleList(YMSGPacket packet) {
        for (Integer key : packet.keySet()) {
            switch (key) {
                case 87: // buddy list
                    for (String line : packet.getValue(key).split("\n")) {
                        String[] values = line.split(":"); // GROUP:buddy,buddy,buddy...
                        if (values.length >= 2) {
                            YahooGroup group = findOrCreateGroup(values[0]);
                            for (String buddyName : values[1].split(",")) {
                                if (!empty(buddyName)) {
                                    YahooBuddy buddy = findOrCreateBuddy(buddyName);
                                    group.addBuddy(buddy);
                                }
                            }
                        }
                    }
                    break;
                case 88: // ignore list
                    for (String name : packet.getValue(key).split(",")) {
                        if (!empty(name)) {
                            YahooBuddy buddy = findOrCreateBuddy(name);
                            buddy.setIgnore(true);
                        }
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
    
    private static final char ESC = 27;
    
    private static final String stripAnsiSequences(String s) {
        String orig = s;
        
        int n = s.charAt(0);
        System.out.println("Char 0 is "+n);
        
        // strip out an old-fashioned ANSI identifier of the format
        // ESC[somethingm  -- where 'something' is a color
        if (s.indexOf(ESC) >= 0) {
            StringBuilder toRet = new StringBuilder();
            int idx = s.indexOf(ESC);
            while (idx >= 0) {
                toRet.append(s.substring(0, idx));
                if (idx == s.length()-1)
                    return toRet.toString();
                
                s = s.substring(idx+1);
                
                int m = s.indexOf('m');
                if (m < 0) {
                    System.out.println("Error decoding character sequence: \""+orig+"\"");
                    return toRet.toString();
                }
                
                s = s.substring(m+1);
                idx = s.indexOf(ESC);
            }
            
            toRet.append(s);
            
            return toRet.toString();
        } else {
            return s;
        }            
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
        msg = stripAnsiSequences(msg);
        YahooMessage ymsg = new YahooMessage(time, from, to, msg);
        mListener.receivedMessage(this, ymsg);
    }
    
    private void handleAddbuddy(YMSGPacket packet) {
        String id = packet.getValue(7);
        String group = packet.getValue(65);
        String errorStr = packet.getValue(66);
        long error = 0;
        if (errorStr != null && errorStr.length() > 0)
            error = Long.parseLong(errorStr);

        if (StringUtil.isNullOrEmpty(id) || StringUtil.isNullOrEmpty(group))
            return;
    
        if (error == 0 || error == 2) { 
            // 2 means "already have this buddy" so that's OK
            YahooGroup g = findOrCreateGroup(group);
            YahooBuddy b = findOrCreateBuddy(id);
            g.addBuddy(b);
            mListener.buddyAdded(this, b, g);
        } else {
            mListener.error(this, YahooError.ADDBUDDY_FAILED, error, new Object[]{ id, group });
        }
    }
    
    private void handleRembuddy(YMSGPacket packet) {
        String id = packet.getValue(7);
        String group = packet.getValue(65);
        if (StringUtil.isNullOrEmpty(id) || StringUtil.isNullOrEmpty(group))
            return;
        
        YahooBuddy b = mBuddies.get(id);
        YahooGroup g = mGroups.get(group);
        
        if (b != null) {
            g.removeBuddy(b);
            
            boolean inOtherGroups = false;
            for (YahooGroup check : mGroups.values()) {
                if (check.contains(b)) {
                    inOtherGroups = true;
                    break;
                }
            }
            if (!inOtherGroups) {
                mBuddies.remove(b);
            }
        } else if (g != null) {
            g.removeByName(id);
        }
        
        if (g == null || b == null) {
            sLog.warn("Got REMBUDDY for id="+id+" group="+group+" but only found Buddy="+
                b+" and Group="+g);

            // create fake buddy/group for return
            if (b == null) 
                b = new YahooBuddy(id);
            if (g == null)
                g = new YahooGroup(group);
        } 
        
        mListener.buddyRemoved(this, b, g);
    }
    
    private final boolean empty(String... strs) {
        for (String s : strs) {
            if (StringUtil.isNullOrEmpty(s))
                return true;
        }
        return false;
    }
    
    private void handleNewContact(YMSGPacket packet) {
        switch ((int)(packet.getStatus())) {
            case 1:
                handleStatus(packet);
                break;
            case 3: // external person added us
            {
                String ourId = packet.getValue(1);
                String theirId = packet.getValue(3);
                String msg = packet.getValue(14);
                if (!empty(ourId, theirId))
                    mListener.buddyAddedUs(this, ourId, theirId, msg);
                break;
            }
            case 7: // contact rejected us
            {
                String id = packet.getValue(3);
                String msg = packet.getValue(14);
                if (!empty(id))
                    mListener.error(this, YahooError.CONTACT_REJECTION, 7, new Object[]{id, msg});
                break;
            }
        }
    }
    
    
    private void handleStatus(YMSGPacket packet) {
        if (packet.containsKey(1)) {
            if (!mIsLoggedOn) {
                mIsLoggedOn = true;
                mDisplayName = packet.getValue(1);
                mListener.loggedOn(this);
                mListener.receivedBuddyList(this);
                setMyStatus(mMyStatus, null);
                startPinging();
            }
        }
        
        if (packet.containsKey(7)) { // current buddy
            for (HashMap<Integer, String> map : packet.chunk(7)) {
            
                if (map.containsKey(7)) {
                    YahooBuddy buddy = findOrCreateBuddy(map.get(7));
                    YMSGService service = YMSGService.lookup(packet.getService());
                    if (service == YMSGService.LOGOFF) {
                        buddy.setStatus(YMSGStatus.OFFLINE);
                    } else {
                        if (map.containsKey(10)) // state
                            buddy.setStatus(YMSGStatus.lookup(packet.getLongValue(10)));
                        if (map.containsKey(19))
                            buddy.setCustomStatus(map.get(19));
                    }
                    mListener.buddyStatusChanged(this, buddy);
                }
            }
        }
    }
    
    private void handleTyping(YMSGPacket packet) {
        String from = packet.getValue(4);
        String msg = packet.getValue(49);
        String status = packet.getValue(13);
        
        if (from != null && msg != null) {
            if (msg.toUpperCase().equals("TYPING")) {
                YahooBuddy buddy = mBuddies.get(from);
                if (buddy != null) {
                    if (status != null && status.charAt(0)=='1')
                        buddy.setTyping(true);
                    else
                        buddy.setTyping(false);
                }
            }
        }
    }
    
    private void sendPing() {
        if (mIsLoggedOn) {
            YMSGPacket msg = new YMSGPacket(YMSGService.PING, YMSGStatus.NONE, 0);
            writePacket(msg);
        }
    }
    private synchronized void startPinging() {
        mPingTask = new PingTask();
        sScheduler.schedule(mLoginId, mPingTask, true, 30 * Constants.MILLIS_PER_SECOND, 10 * Constants.MILLIS_PER_SECOND);
    }
    
    private synchronized void stopPinging() {
        if (mPingTask != null)  
            sScheduler.cancel(mLoginId, true);
        mPingTask = null;
    }

    private void writePacket(YMSGPacket packet) 
    {
        this.mSession.write(packet);
    }
    private YMSGAuthProvider mAuthProvider = null;
    
    private HashMap<String, YahooBuddy> mBuddies = new HashMap<String, YahooBuddy>();
    private Cookies mCookies = null; // used for filetransfer or for HTTP connection
    private String mDisplayName = null;
    private HashMap<String, YahooGroup> mGroups = new HashMap<String, YahooGroup>();
    private boolean mIsLoggedOn = false;
    private YahooEventListener mListener;
    private String mLoginId;
    private YMSGStatus mMyStatus = YMSGStatus.AVAILABLE;
    
    private YMSGPacket mPartialPacket = null;
    
    private String mPassword;
    
    private PingTask mPingTask;

    private IoSession mSession;
    private long mSessionId = 0;
}