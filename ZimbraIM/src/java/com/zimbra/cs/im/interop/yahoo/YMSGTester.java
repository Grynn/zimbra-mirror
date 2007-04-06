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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;

import org.apache.mina.common.ConnectFuture;
import org.apache.mina.common.IoSession;
import org.apache.mina.common.RuntimeIOException;
import org.apache.mina.filter.LoggingFilter;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.SocketConnector;
import org.apache.mina.transport.socket.nio.SocketConnectorConfig;
import org.apache.mina.util.SessionLog;

import com.zimbra.common.localconfig.LC;
import com.zimbra.common.util.CliUtil;
import com.zimbra.common.util.EasySSLProtocolSocketFactory;
import com.zimbra.common.util.StringUtil;
import com.zimbra.common.util.ZimbraLog;
import com.zimbra.cs.im.interop.yahoo.YahooEventListener.YahooError;

/**
 * 
 */
public class YMSGTester implements YahooEventListener {

    public void sessionClosed(YahooSession session) {
        System.out.println("Session has closed: "+session.toString());
        mQuit = true;
    }
    
    public void buddyAddedUs(YahooSession session, String ourId, String theirId, String msg) {
        System.out.println("Remote user "+theirId+" added us ("+ourId+") to their buddy list, with msg \""+
            msg+"\"");
    }

    public void buddyAdded(YahooSession session, String id, String group) {
        System.out.println("Buddy:"+id+" added to group "+group);
    }

    public void buddyRemoved(YahooSession session, String id, String group) {
        System.out.println("Buddy:"+id+" removed from group "+group);
    }
    
    public void error(YahooSession session, YahooError error, long code, Object[] args) {
        StringBuilder sb = new StringBuilder("ERROR: ");
        sb.append(error.toString());
        sb.append(" code=").append(code);
        boolean atFirst = true;
        if (args != null) {
            sb.append(" args: ");
            for (Object o : args) {
                if (!atFirst)
                    sb.append(", ");
                atFirst = false;
                sb.append(o);
            }
        }
        System.err.println(sb.toString());
    }

    private static final String HOSTNAME = "localhost";
//    private static final String HOSTNAME = "scs.msg.yahoo.com";
//    private static final String HOSTNAME = "216.155.193.162";
    private static final int PORT = 5050;
    
    /**
     * This auth provider uses the jYMSG auth library, if it is available.
     */
    public static class JYMsgAuthProvider implements YMSGAuthProvider {
        
        public static boolean available() {
            try {
                Class c = Class.forName("ymsg.network.ChallengeResponseV10");
                c.getDeclaredMethod("getStrings", new Class[] { String.class, String.class, String.class });
                return true;
            } catch (ClassNotFoundException e) {
            } catch (NoSuchMethodException e) { 
            }
            return false;
        }
        
        public String[] calculateChallengeResponse(String username, String password, String challenge) {
            try {
                Class c = Class.forName("ymsg.network.ChallengeResponseV10");
                Method m = c.getDeclaredMethod("getStrings", new Class[] { String.class, String.class, String.class });
                m.setAccessible(true);
                return (String[]) m.invoke(null, new Object[] { username, password, challenge });
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) { 
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            
            return new String[] { "", "" };
        }
    }
    
    
    public void authFailed(YahooSession session) {
        System.out.println("AuthFailed");
    }

    public void buddyStatusChanged(YahooSession session, YahooBuddy buddy) {
        System.out.println("BuddyStatusChanged: "+buddy.toString());
    }

    public void loggedOn(YahooSession session) {
        System.out.println("LoggedOn");
    }

    public void receivedBuddyList(YahooSession session) {
        System.out.println("ReceivedBuddyList");

        listGroups();
        listBuddies();
    }

    public void receivedMessage(YahooSession session, YahooMessage msg) {
        System.out.println("ReceivedMessage: "+msg.toString());
    }

    private static class TestAuthProvider implements YMSGAuthProvider {
        public String[] calculateChallengeResponse(String username, String password, String challenge) {
            return new String[] { "testing", "1234" };
        }
    }
    
    
    enum Command {
        HELP(0,    new String[]{"?","H"},    "get help"),
        ADD(1,     new String[]{"A", "ADD"}, "add a buddy",   "YAHOO_ID [GROUP]"),
        REMOVE(1,  new String[]{"R", "REM"}, "remove buddy",  "YAHOO_ID [GROUP]"),
        BUDDIES(0, new String[]{"B"},        "list buddies"),
        GROUPS(0,  new String[]{"G"},        "list groups"),
        INFO(0,    new String[]{"I"},        "info"), 
        STATUS(1,  new String[]{"ST"},       "update status", "ONLINE|BUSY|BRB"),
        SEND(2,    new String[]{"SE"},       "send an IM",    "TO MESSAGE"),
        QUIT(0,    null),
        ;

        Command(int minArgs, String[] aliases) { this(minArgs, aliases, "", ""); }
        Command(int minArgs, String[] aliases, String description) { this(minArgs, aliases, description, ""); }
        
        Command(int minArgs, String[] aliases, String description, String usage) {
            if (aliases == null)
                mAliases = new String[0];
            else
                mAliases = aliases;
            mMinArgs = minArgs;
            mDesc = description;
            mUsage = usage;
        }
        private int mMinArgs;
        private String mDesc;
        private String mUsage;
        private String[] mAliases;
        
        public int getMinArgs() { return mMinArgs; }
        public boolean enoughArgs(int num) { return num >= mMinArgs; }
        public String shortUsage() { return name()+" "+mUsage; }
        public String longUsage() { return shortUsage()+" - "+mDesc; }
        
        public static Command lookup(String s) {
            s = s.toUpperCase();
            
            for (Command c : Command.values()) {
                if (s.equals(c.name()))
                    return c;
                for (String alias : c.mAliases)
                    if (s.equals(alias))
                        return c;
            }
            throw new IllegalArgumentException("Unknown command: "+s+")");
        }
    }
    
    private Command parseCommand(String[] args) {
        try {
            if (args.length == 0 || args[0] == null || args[0].length() == 0)
                return null;
            
            if (args[0].charAt(0) == '?')
                args[0] = "help";
            Command c = Command.lookup(args[0]);
            if (!c.enoughArgs(args.length-1)) {
                System.out.println("Wrong number of arguments, usage: "+c.shortUsage());
            }
            return c;
        } catch (Exception e) {
            System.out.println("Unknown command: "+args[0]);
            return null;
        }
    }
    
    private void listBuddies() {
        System.out.println("Buddies:");
        for (YahooBuddy buddy: mSession.buddies()) {
            System.out.println("\t"+buddy.toString());
        }
    }
    
    private void listGroups() {
        System.out.println("Groups:");
        for (YahooGroup group : mSession.groups()) {
            System.out.println(group.toString());
        }
    }
    
    private void run() {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            while (!mQuit) {
                try {
                    System.out.print("yahoo>");
                    String line = StringUtil.readLine(in);
                    if (line == null)
                        break;
                    String[] words = StringUtil.parseLine(line);
                    Command c = parseCommand(words);
                    if (c != null) {
                        switch (c) {
                            case ADD:
                                mSession.addBuddy(words[1], words[2]);
                                break;
                            case REMOVE:
                                mSession.removeBuddy(words[1], words[2]);
                                break;
                            case HELP:
                                for (Command cur : Command.values()) {
                                    System.out.println("\t"+cur.longUsage());
                                }
                                break;
                            case BUDDIES:
                                listBuddies();
                                break;
                            case GROUPS:
                                listGroups();
                                break;
                            case INFO: 
                                listBuddies();
                                listGroups();
                                break;
                            case STATUS:
                                mSession.setMyStatus(YahooStatus.valueOf(words[1].toUpperCase()));
                                break;
                            case SEND:
                                mSession.sendMessage(words[1], words[2]);
                                break;
                            case QUIT:
                                mSession.disconnect();
                                mQuit = true;
                                break;
                        }
                    }
                } catch (Exception e) {
                    System.err.println(e.toString());
                    e.printStackTrace();
                }
            }
        
        Runtime.getRuntime().halt(0);
    }
    
    private boolean mQuit = false;
    private YahooSession mSession;
    
    /**
     * @param argv
     */
    public static void main(String[] argv) {
        ZimbraLog.toolSetupLog4j("INFO", "/opt/zimbra/conf/log4j.properties");
        if (LC.ssl_allow_untrusted_certs.booleanValue())
            EasySSLProtocolSocketFactory.init();
        
        if (argv.length < 2) {
            System.err.println("Requires USERNAME and PASSWORD arguments");
        }
        SocketConnector connector = new SocketConnector();

        // Change the worker timeout to 1 second to make the I/O thread quit soon
        // when there's no connection to manage.
        connector.setWorkerTimeout( 1 );
        
        // Configure the service.
        SocketConnectorConfig cfg = new SocketConnectorConfig();
        cfg.setConnectTimeout(10);
        
        cfg.getFilterChain().addLast("codec", new ProtocolCodecFilter(new YMSGProtocolCodecFactory()));
        
        cfg.getFilterChain().addLast( "logger", new LoggingFilter() );
        // class com.zimbra.cs.im.interop.yahoo.SessionHandler
        
        YMSGAuthProvider authProvider = null; 
        if (JYMsgAuthProvider.available()) {
            authProvider = new JYMsgAuthProvider();
        } else {
            System.err.println("Could not load ymsg auth code, falling back to test auth code");
            authProvider = new TestAuthProvider();
        }
    
        boolean completed = false;
        IoSession session = null;

        YMSGTester tester = new YMSGTester();
        SessionHandler ymsg = new SessionHandler(authProvider, tester, argv[0], argv[1]);
        tester.setSessionHandler(ymsg);
        while (!completed) {
            try {
                ConnectFuture future = connector.connect(new InetSocketAddress( HOSTNAME, PORT ), ymsg, cfg);
                
                future.join();
                session = future.getSession();
                completed = true;
            } catch (RuntimeIOException e) {
                System.err.println( "Failed to connect." );
                e.printStackTrace();
            } catch (InterruptedException e) { }
        }
        
        tester.run();
        
        try {
            session.close().join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    YMSGTester() {
    }
    void setSessionHandler(YahooSession handler) {
        mSession = handler;
    }
    
}
