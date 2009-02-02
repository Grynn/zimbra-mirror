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
package com.zimbra.cs.mailclient;

import static com.zimbra.cs.mailclient.auth.SaslAuthenticator.*;
import com.zimbra.cs.mailclient.auth.AuthenticatorFactory;
import com.zimbra.cs.mailclient.util.SSLUtil;
import com.zimbra.cs.mailclient.util.Password;
import com.zimbra.cs.mailclient.imap.ImapConfig;
import com.zimbra.cs.mailclient.imap.ImapConnection;
import com.zimbra.cs.mailclient.pop3.Pop3Config;
import com.zimbra.cs.mailclient.pop3.Pop3Connection;

import javax.security.sasl.Sasl;
import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Map;
import java.util.HashMap;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.apache.log4j.Level;

public abstract class MailClient {
    private MailConfig config;
    protected MailConnection connection;
    private StringBuilder sbuf;
    private String password;
    private boolean eof;

    protected MailClient(MailConfig config) {
        this.config = config;
        sbuf = new StringBuilder(132);
    }

    public void run(String[] args) throws LoginException, IOException {
        BasicConfigurator.configure();
        Logger.getRootLogger().setLevel(Level.INFO);
        config.setTrace(true);
        parseArguments(args);
        connect();
        authenticate();
        startCommandLoop();
    }

    protected abstract void printUsage(PrintStream ps);
    
    protected void connect() throws IOException {
        config.setConnectTimeout(30);
        config.setTraceStream(System.out);
        config.setSSLSocketFactory(SSLUtil.getDummySSLContext().getSocketFactory());
        connection = newConnection(config);
        connection.connect();
    }

    protected void authenticate() throws LoginException, IOException {
        if (password == null && isPasswordRequired()) {
            password = Password.read("Password: ");
        }
        if (config.getAuthenticationId() == null) {
            // Authentication id defaults to login username
            config.setAuthenticationId(System.getProperty("user.name"));
        }
        connection.authenticate(password);
        String qop = connection.getNegotiatedQop();
        if (qop != null) System.out.printf("[Negotiated QOP is %s]\n", qop);
    }

    private boolean isPasswordRequired() {
        String mech = config.getMechanism();
        if (mech == null) {
            return true;
        }
        AuthenticatorFactory af = config.getAuthenticatorFactory();
        return af != null && af.isPasswordRequired(mech);
    }
    
    private static MailConnection newConnection(MailConfig config) {
        if (config instanceof ImapConfig) {
            return new ImapConnection((ImapConfig) config);
        } else if (config instanceof Pop3Config) {
            return new Pop3Connection((Pop3Config) config);
        } else {
            throw new IllegalArgumentException(
                "Unsupported protocol: " + config.getProtocol());
        }
    }

    private void startCommandLoop() throws IOException {
        connection.setTraceEnabled(false);
        Thread t = new ReaderThread();
        t.setDaemon(true);
        t.start();
        final MailInputStream is = connection.getInputStream();
        String line;
        try {
            while ((line = is.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            if (!eof) e.printStackTrace();
        }
    }

    private class ReaderThread extends Thread {
        public void run() {
            try {
                MailOutputStream os = connection.getOutputStream();
                String line;
                while ((line = readLine(System.in)) != null) {
                    if (!processCommand(line)) {
                        os.writeLine(line);
                        os.flush();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            eof = true;
            connection.close();
        }
    }

    private String readLine(InputStream is) throws IOException {
        sbuf.setLength(0);
        int c;
        while ((c = is.read()) != -1 && c != '\n') {
            if (c != '\r') sbuf.append((char) c);
        }
        return c != -1 ? sbuf.toString() : null;
    }

    public void parseArguments(String[] args) {
        try {
            parseArguments(Arrays.asList(args).listIterator());
        } catch (IllegalArgumentException e) {
            System.err.printf("ERROR: %s\n", e);
            printUsage(System.err);
            System.exit(1);
        }
    }

    private void parseArguments(ListIterator<String> args) {
        while (args.hasNext() && parseArgument(args)) {
            // Continue parsing...
        }
        if (!args.hasNext()) {
            throw new IllegalArgumentException("Missing required host name");
        }
        config.setHost(args.next());
        if (args.hasNext()) {
            throw new IllegalArgumentException();
        }
    }

    protected boolean parseArgument(ListIterator<String> args) {
        String arg = args.next();
        if (!arg.startsWith("-")) {
            args.previous();
            return false;
        }
        if (arg.length() != 2) {
            throw new IllegalArgumentException("Illegal option: " + arg);
        }
        int minQop = -1;
        int maxQop = -1;
        try {
            switch (arg.charAt(1)) {
            case 'p':
                config.setPort(Integer.parseInt(args.next()));
                break;
            case 'u':
                config.setAuthenticationId(args.next());
                break;
            case 'z':
                config.setAuthorizationId(args.next());
                break;
            case 'w':
                password = args.next();
                break;
            case 'r':
                config.setRealm(args.next());
                break;
            case 'm':
                config.setMechanism(args.next().toUpperCase());
                break;
            case 'k':
                minQop = parseQop(arg, args.next());
                break;
            case 'l':
                maxQop = parseQop(arg, args.next());
                break;
            case 's':
                config.setSslEnabled(true);
                break;
            case 't':
                config.setTlsEnabled(true);
                break;
            case 'd':
                config.setDebug(true);
                break;
            case 'q':
                config.setTrace(false);
                break;
            case 'h':
                printUsage(System.out);
                System.exit(0);
            default:
                throw new IllegalArgumentException("Illegal option: " + arg);
            }
        } catch (NoSuchElementException e) {
            throw new IllegalArgumentException("Option requires argument: " + arg);
        }
        // If SSL is enabled then only QOP_AUTH is supported
        if (!config.isSslEnabled()) {
            Map<String, String> props = new HashMap<String, String>();
            props.put(Sasl.QOP, getQop(minQop, maxQop));
            config.setSaslProperties(props);
        }
        return true;
    }

    private static int parseQop(String arg, String value) {
        if (value.equalsIgnoreCase(QOP_AUTH) || value.equals("0")) return 0;
        if (value.equalsIgnoreCase(QOP_AUTH_INT) || value.equals("1")) return 1;
        if (value.equalsIgnoreCase(QOP_AUTH_CONF) || value.equals("2")) return 2;
        throw new IllegalArgumentException("Invalid value for option '" + arg + "'");
    }

    private static String getQop(int minQop, int maxQop) {
        if (minQop == -1) minQop = 0;
        if (maxQop == -1) maxQop = 2;
        if (minQop > maxQop) maxQop = minQop;
        StringBuilder sb = new StringBuilder();
        for (int i = maxQop; i >= minQop; --i) {
            switch (i) {
                case 0: sb.append(QOP_AUTH); break;
                case 1: sb.append(QOP_AUTH_INT); break;
                case 2: sb.append(QOP_AUTH_CONF); break;
                default: throw new AssertionError();
            }
            if (i > minQop) sb.append(',');
        }
        return sb.toString();
    }

    private boolean processCommand(String line) throws IOException {
        if (!line.startsWith("!")) return false;
        line = line.substring(1);
        String[] cmdLine = line.split(" ");
        boolean success = false;
        try {
            success = processCommand(cmdLine);
        } catch (IndexOutOfBoundsException e) {
            // Fall through
        }
        if (!success) {
            System.out.println(">> ERROR: Invalid command: " + line);
        }
        return true;
    }

    protected boolean processCommand(String[] cmdLine) throws IOException {
        String cmd = cmdLine[0];
        if (isMatch(cmd, "SHow")) {
            return processShow(cmdLine);
        } else if (isMatch(cmd, "SET")) {
            return processSet(cmdLine);
        }
        return false;
    }

    protected boolean processShow(String[] cmdLine) throws IOException {
        return false;
    }
    
    protected boolean processSet(String[] cmdLine) throws IOException {
        return false;
    }

    protected static boolean isMatch(String word, String fullword) {
        if (word.length() > fullword.length()) return false;
        boolean enough = false;
        for (int i = 0; i < fullword.length(); i++) {
            char c = fullword.charAt(i);
            if (Character.isLowerCase(c)) {
                enough = true;
            } else {
                c = Character.toLowerCase(c);
            }
            if (i >= word.length()) return enough;
            if (Character.toLowerCase(word.charAt(i)) != c) {
                return false;
            }
        }
        return word.length() == fullword.length();
    }
}
