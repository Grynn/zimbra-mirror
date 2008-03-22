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
package org.jivesoftware.wildfire.spi;

import org.apache.mina.common.IoSession;
import org.jivesoftware.util.JiveGlobals;
import org.jivesoftware.util.LocaleUtils;
import org.jivesoftware.util.Log;
import org.jivesoftware.wildfire.*;
import org.jivesoftware.wildfire.container.BasicModule;
import org.jivesoftware.wildfire.multiplex.MultiplexerPacketDeliverer;
import org.jivesoftware.wildfire.net.*;

import java.io.IOException;
//import java.net.InetAddress;
import java.net.Socket;
//import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ConnectionManagerImpl extends BasicModule implements ConnectionManager {

    private SocketAcceptThread socketThread;
    private SSLSocketAcceptThread sslSocketThread;
    private SocketAcceptThread componentSocketThread;
    private SocketAcceptThread serverSocketThread;
    private SocketAcceptThread multiplexerSocketThread;
    private SocketAcceptThread cloudRoutingSocketThread;
    private SSLSocketAcceptThread sslCloudRoutingSocketThread;
    private ArrayList<ServerPort> ports;

    private SessionManager sessionManager;
    private PacketDeliverer deliverer;
    private PacketRouter router;
    private RoutingTable routingTable;
//    private String localIPAddress = null;

    // Used to know if the sockets can be started (the connection manager has been started)
    private boolean isStarted = false;
    // Used to know if the sockets have been started
    private boolean isSocketStarted = false;

    public ConnectionManagerImpl() {
        super("Connection Manager");
        ports = new ArrayList<ServerPort>(8);
    }

    private void createSocket() {
        if (!isStarted || isSocketStarted || sessionManager == null || deliverer == null ||
                router == null)
        {
            return;
        }
        isSocketStarted = true;

//        // Setup port info
//        try {
//            localIPAddress = InetAddress.getLocalHost().getHostAddress();
//        }
//        catch (UnknownHostException e) {
//            if (localIPAddress == null) {
//                localIPAddress = "Unknown";
//            }
//        }
        // start the listener for local cloud routing
        startCloudRoutingListener(getCloudroutingListenerAddress());
        // Start the port listener for s2s communication
        startServerListener(getServerListenerAddress());
        // Start the port listener for Connections Multiplexers
        startConnectionManagerListener(getConnectionManagerListenerAddress());
        // Start the port listener for external components
        startComponentListener(getComponentListenerAddress());
        // Start the port listener for clients
        startClientListeners(getClientPlainListenerAddress());
        // Start the port listener for secured clients
        startClientSSLListeners(getClientSSLListenerAddress());
    }

    private void startServerListener(String bindAddress) {
        // Start servers socket unless it's been disabled.
        if (isServerListenerEnabled()) {
            int port = getServerListenerPort();
            try {
                serverSocketThread = new SocketAcceptThread(this, 
                            new ServerPort(port, XMPPServer.getInstance().getServerNames(),
                                        bindAddress, false, null, ServerPort.Type.server));
                ports.add(serverSocketThread.getServerPort());
                serverSocketThread.setDaemon(true);
                serverSocketThread.setPriority(Thread.MAX_PRIORITY);
                serverSocketThread.start();

                List<String> params = new ArrayList<String>();
                params.add(Integer.toString(serverSocketThread.getPort()));
                Log.info(LocaleUtils.getLocalizedString("startup.server", params));
            }
            catch (Exception e) {
                System.err.println("Error starting server listener on port " + port + ": " +
                        e.getMessage());
                Log.error(LocaleUtils.getLocalizedString("admin.error.socket-setup"), e);
            }
        }
    }
    
    private void startCloudRoutingListener(String bindAddress) {
        if (isCloudRoutingListenerEnabled()) {
            int port = getCloudRoutingPort();
            String algorithm = null;
            if (port > 0) {
                try {
                    if (JiveGlobals.getBooleanProperty("xmpp.cloudrouting.ssl", false)) {
                        algorithm = JiveGlobals.getProperty("xmpp.socket.ssl.algorithm", "TLS");
                    }
                    
                    if (algorithm == null || algorithm.length() == 0) {
                        cloudRoutingSocketThread = new SocketAcceptThread(this, 
                            new ServerPort(port, XMPPServer.getInstance().getServerNames(),
                                bindAddress, (algorithm != null ? true : false), algorithm, ServerPort.Type.cloudRouter));
                        ports.add(cloudRoutingSocketThread.getServerPort());
                        cloudRoutingSocketThread.setDaemon(true);
                        cloudRoutingSocketThread.setPriority(Thread.MAX_PRIORITY);
                        cloudRoutingSocketThread.start();
                    } else {
                        sslCloudRoutingSocketThread = new SSLSocketAcceptThread(this, new ServerPort(port, XMPPServer.getInstance().getServerNames(),
                            bindAddress, true, algorithm, ServerPort.Type.cloudRouter));
                        ports.add(sslCloudRoutingSocketThread.getServerPort());
                        sslCloudRoutingSocketThread.setDaemon(true);
                        sslCloudRoutingSocketThread.setPriority(Thread.MAX_PRIORITY);
                        sslCloudRoutingSocketThread.start();
                    }
                } catch (Exception e) {
                    System.err.println("Error starting cloudrouting listener on port " + port + ": " +
                        e.getMessage());
                    Log.error(LocaleUtils.getLocalizedString("admin.error.socket-setup"), e);
                }
            }
        }
    }

    private void stopServerListener() {
        if (serverSocketThread != null) {
            serverSocketThread.shutdown();
            ports.remove(serverSocketThread.getServerPort());
            serverSocketThread = null;
        }
    }
    
    private void stopCloudRoutingListener() {
        if (cloudRoutingSocketThread != null) {
            cloudRoutingSocketThread.shutdown();
            ports.remove(cloudRoutingSocketThread.getServerPort());
            cloudRoutingSocketThread = null;
        }
        if (sslCloudRoutingSocketThread != null) {
            sslCloudRoutingSocketThread.shutdown();
            ports.remove(sslCloudRoutingSocketThread.getServerPort());
            sslCloudRoutingSocketThread = null;
        }
    }

    private void startConnectionManagerListener(String bindAddress) {
        // Start multiplexers socket unless it's been disabled.
        if (isConnectionManagerListenerEnabled()) {
            int port = getConnectionManagerListenerPort();
            try {
                multiplexerSocketThread = new SocketAcceptThread(this, new ServerPort(port,
                        XMPPServer.getInstance().getServerNames(), bindAddress, false, null,
                        ServerPort.Type.connectionManager));
                ports.add(multiplexerSocketThread.getServerPort());
                multiplexerSocketThread.setDaemon(true);
                multiplexerSocketThread.setPriority(Thread.MAX_PRIORITY);
                multiplexerSocketThread.start();

                List<String> params = new ArrayList<String>();
                params.add(Integer.toString(multiplexerSocketThread.getPort()));
                Log.info(LocaleUtils.getLocalizedString("startup.multiplexer", params));
            }
            catch (Exception e) {
                System.err.println("Error starting multiplexer listener on port " + port + ": " +
                        e.getMessage());
                Log.error(LocaleUtils.getLocalizedString("admin.error.socket-setup"), e);
            }
        }
    }

    private void stopConnectionManagerListener() {
        if (multiplexerSocketThread != null) {
            multiplexerSocketThread.shutdown();
            ports.remove(multiplexerSocketThread.getServerPort());
            multiplexerSocketThread = null;
        }
    }

    private void startComponentListener(String bindAddress) {
        // Start components socket unless it's been disabled.
        if (isComponentListenerEnabled()) {
            int port = getComponentListenerPort();
            try {
                componentSocketThread = new SocketAcceptThread(this, new ServerPort(port,
                            XMPPServer.getInstance().getServerNames(), bindAddress, false, null, ServerPort.Type.component));
                ports.add(componentSocketThread.getServerPort());
                componentSocketThread.setDaemon(true);
                componentSocketThread.setPriority(Thread.MAX_PRIORITY);
                componentSocketThread.start();

                List<String> params = new ArrayList<String>();
                params.add(Integer.toString(componentSocketThread.getPort()));
                Log.info(LocaleUtils.getLocalizedString("startup.component", params));
            }
            catch (Exception e) {
                System.err.println("Error starting component listener on port " + port + ": " +
                        e.getMessage());
                Log.error(LocaleUtils.getLocalizedString("admin.error.socket-setup"), e);
            }
        }
    }

    private void stopComponentListener() {
        if (componentSocketThread != null) {
            componentSocketThread.shutdown();
            ports.remove(componentSocketThread.getServerPort());
            componentSocketThread = null;
        }
    }

    private void startClientListeners(String bindAddress) {
        // Start clients plain socket unless it's been disabled.
        if (isClientListenerEnabled()) {
            int port = getClientListenerPort();

            try {
                socketThread = new SocketAcceptThread(this, 
                    new ServerPort(port, XMPPServer.getInstance().getServerNames(), bindAddress, false, null, ServerPort.Type.client)
                );
                ports.add(socketThread.getServerPort());
                socketThread.setDaemon(true);
                socketThread.setPriority(Thread.MAX_PRIORITY);
                socketThread.start();

                List<String> params = new ArrayList<String>();
                params.add(Integer.toString(socketThread.getPort()));
                Log.info(LocaleUtils.getLocalizedString("startup.plain", params));
            }
            catch (Exception e) {
                System.err.println("Error starting XMPP listener on port " + port + ": " +
                        e.getMessage());
                Log.error(LocaleUtils.getLocalizedString("admin.error.socket-setup"), e);
            }
        }
    }

    private void stopClientListeners() {
        if (socketThread != null) {
            socketThread.shutdown();
            ports.remove(socketThread.getServerPort());
            socketThread = null;
        }
    }

    private void startClientSSLListeners(String bindAddress) {
        // Start clients SSL unless it's been disabled.
        if (isClientSSLListenerEnabled()) {
            int port = getClientSSLListenerPort();
            String algorithm = JiveGlobals.getProperty("xmpp.socket.ssl.algorithm");
            if ("".equals(algorithm) || algorithm == null) {
                algorithm = "TLS";
            }
            try {
                sslSocketThread = new SSLSocketAcceptThread(this, new ServerPort(port, XMPPServer.getInstance().getServerNames(),
                        bindAddress, true, algorithm, ServerPort.Type.client));
                ports.add(sslSocketThread.getServerPort());
                sslSocketThread.setDaemon(true);
                sslSocketThread.setPriority(Thread.MAX_PRIORITY);
                sslSocketThread.start();

                List<String> params = new ArrayList<String>();
                params.add(Integer.toString(sslSocketThread.getPort()));
                Log.info(LocaleUtils.getLocalizedString("startup.ssl", params));
            }
            catch (Exception e) {
                System.err.println("Error starting SSL XMPP listener on port " + port + ": " +
                        e.getMessage());
                Log.error(LocaleUtils.getLocalizedString("admin.error.ssl"), e);
            }
        }
    }

    private void stopClientSSLListeners() {
        if (sslSocketThread != null) {
            sslSocketThread.shutdown();
            ports.remove(sslSocketThread.getServerPort());
            sslSocketThread = null;
        }
    }

    public Iterator<ServerPort> getPorts() {
        return ports.iterator();
    }

    public SocketReader createSocketReader(Socket sock, boolean isSecure, ServerPort serverPort) throws IOException {
        if (serverPort.isClientPort()) {
            SocketConnection conn = new StdSocketConnection(deliverer, sock, isSecure);
            return new ClientSocketReader(router, routingTable, sock, conn);
        } else if (serverPort.isComponentPort()) {
            SocketConnection conn = new StdSocketConnection(deliverer, sock, isSecure);
            return new ComponentSocketReader(router, routingTable, sock, conn);
        } else if (serverPort.isServerPort()) {
            SocketConnection conn = new StdSocketConnection(deliverer, sock, isSecure);
            return new ServerSocketReader(router, routingTable, sock, conn);
        } else if (serverPort.isCloudRouter()) {
            SocketConnection conn = new StdSocketConnection(deliverer, sock, isSecure);
            return new CloudRoutingSocketReader(router, routingTable, sock, conn);
        } else {
            // Use the appropriate packeet deliverer for connection managers. The packet
            // deliverer will be configured with the domain of the connection manager once
            // the connection manager has finished the handshake.
            SocketConnection conn = new StdSocketConnection(new MultiplexerPacketDeliverer(), sock, isSecure);
            return new ConnectionMultiplexerSocketReader(router, routingTable, sock, conn);
        }
    }
    
    public SocketReader createSocketReader(IoSession nioSocket, boolean isSecure, ServerPort serverPort)
    throws IOException 
    {
        if (serverPort.isClientPort()) {
            SocketConnection conn = new NioSocketConnection(deliverer, nioSocket, isSecure);
            return new ClientSocketReader(router, routingTable, nioSocket, conn);
        } else if (serverPort.isComponentPort()) {
            SocketConnection conn = new NioSocketConnection(deliverer, nioSocket, isSecure);
            return new ComponentSocketReader(router, routingTable, nioSocket, conn);
        } else if (serverPort.isServerPort()) {
            SocketConnection conn = new NioSocketConnection(deliverer, nioSocket, isSecure);
            return new ServerSocketReader(router, routingTable, nioSocket, conn);
        } else if (serverPort.isCloudRouter()) {
            SocketConnection conn = new NioSocketConnection(deliverer, nioSocket, isSecure);
            return new CloudRoutingSocketReader(router, routingTable, nioSocket, conn);
        } else {
            // Use the appropriate packeet deliverer for connection managers. The packet
            // deliverer will be configured with the domain of the connection manager once
            // the connection manager has finished the handshake. 
            SocketConnection conn = new NioSocketConnection(new MultiplexerPacketDeliverer(), nioSocket, isSecure);
            return new ConnectionMultiplexerSocketReader(router, routingTable, nioSocket, conn);
        }
    }
    
    public void initialize(XMPPServer server) {
        super.initialize(server);
        router = server.getPacketRouter();
        routingTable = server.getRoutingTable();
        deliverer = server.getPacketDeliverer();
        sessionManager = server.getSessionManager();
    }

    private boolean isClientListenerEnabled() {
        return JiveGlobals.getBooleanProperty("xmpp.socket.plain.active", true);
    }

    private boolean isClientSSLListenerEnabled() {
        return JiveGlobals.getBooleanProperty("xmpp.socket.ssl.active", true);
    }

    private boolean isComponentListenerEnabled() {
        return JiveGlobals.getBooleanProperty("xmpp.component.socket.active", false);
    }

    private boolean isServerListenerEnabled() {
        return JiveGlobals.getBooleanProperty("xmpp.server.socket.active", true);
    }
    
    private boolean isCloudRoutingListenerEnabled() {
        return JiveGlobals.getBooleanProperty("xmpp.cloudrouting.active", false);
    }
    
    private boolean isConnectionManagerListenerEnabled() {
        return JiveGlobals.getBooleanProperty("xmpp.multiplex.socket.active", false);
    }

    
    
    private String getClientPlainListenerAddress() {
        return JiveGlobals.getProperty("xmpp.socket.plain.address", null); 
    }
    
    private String getClientSSLListenerAddress() {
        return JiveGlobals.getProperty("xmpp.socket.ssl.address", null); 
    }

    private String getComponentListenerAddress() {
        return JiveGlobals.getProperty("xmpp.component.address", null); 
    }
    
    private String getServerListenerAddress() {
        return JiveGlobals.getProperty("xmpp.server.address", null); 
    }
    
    private String getCloudroutingListenerAddress() {
        return JiveGlobals.getProperty("xmpp.cloudrouting.address", null); 
    }

    private String getConnectionManagerListenerAddress() {
        return JiveGlobals.getProperty("xmpp.multiplex.address", null); 
    }

    
    private int getClientListenerPort() {
        return JiveGlobals.getIntProperty("xmpp.socket.plain.port",
            SocketAcceptThread.DEFAULT_PORT);
    }
    
    private int getClientSSLListenerPort() {
        return JiveGlobals.getIntProperty("xmpp.socket.ssl.port",
                SSLSocketAcceptThread.DEFAULT_PORT);
    }
    
    private int getComponentListenerPort() {
        return JiveGlobals.getIntProperty("xmpp.component.socket.port",
                SocketAcceptThread.DEFAULT_COMPONENT_PORT);
    }

    private int getServerListenerPort() {
        return JiveGlobals.getIntProperty("xmpp.server.socket.port",
                SocketAcceptThread.DEFAULT_SERVER_PORT);
    }
    
    private int getCloudRoutingPort() {
        return JiveGlobals.getIntProperty("xmpp.cloudrouting.port", 0);
    }

    public int getConnectionManagerListenerPort() {
        return JiveGlobals.getIntProperty("xmpp.multiplex.socket.port",
            SocketAcceptThread.DEFAULT_MULTIPLEX_PORT);
    }
    

    
//    public void enableClientListener(boolean enabled) {
//        if (enabled == isClientListenerEnabled()) {
//            // Ignore new setting
//            return;
//        }
//        if (enabled) {
//            JiveGlobals.setProperty("xmpp.socket.plain.active", "true");
//            // Start the port listener for clients
//            startClientListeners(localIPAddress);
//        }
//        else {
//            JiveGlobals.setProperty("xmpp.socket.plain.active", "false");
//            // Stop the port listener for clients
//            stopClientListeners();
//        }
//    }
//
//    public void enableClientSSLListener(boolean enabled) {
//        if (enabled == isClientSSLListenerEnabled()) {
//            // Ignore new setting
//            return;
//        }
//        if (enabled) {
//            JiveGlobals.setProperty("xmpp.socket.ssl.active", "true");
//            // Start the port listener for secured clients
//            startClientSSLListeners(localIPAddress);
//        }
//        else {
//            JiveGlobals.setProperty("xmpp.socket.ssl.active", "false");
//            // Stop the port listener for secured clients
//            stopClientSSLListeners();
//        }
//    }
//
//    public void enableComponentListener(boolean enabled) {
//        if (enabled == isComponentListenerEnabled()) {
//            // Ignore new setting
//            return;
//        }
//        if (enabled) {
//            JiveGlobals.setProperty("xmpp.component.socket.active", "true");
//            // Start the port listener for external components
//            startComponentListener(localIPAddress);
//        }
//        else {
//            JiveGlobals.setProperty("xmpp.component.socket.active", "false");
//            // Stop the port listener for external components
//            stopComponentListener();
//        }
//    }
//
//    public void enableServerListener(boolean enabled) {
//        if (enabled == isServerListenerEnabled()) {
//            // Ignore new setting
//            return;
//        }
//        if (enabled) {
//            JiveGlobals.setProperty("xmpp.server.socket.active", "true");
//            // Start the port listener for s2s communication
//            startServerListener(localIPAddress);
//        }
//        else {
//            JiveGlobals.setProperty("xmpp.server.socket.active", "false");
//            // Stop the port listener for s2s communication
//            stopServerListener();
//        }
//    }
//
//    public void enableConnectionManagerListener(boolean enabled) {
//        if (enabled == isConnectionManagerListenerEnabled()) {
//            // Ignore new setting
//            return;
//        }
//        if (enabled) {
//            JiveGlobals.setProperty("xmpp.multiplex.socket.active", "true");
//            // Start the port listener for s2s communication
//            startConnectionManagerListener(localIPAddress);
//        }
//        else {
//            JiveGlobals.setProperty("xmpp.multiplex.socket.active", "false");
//            // Stop the port listener for s2s communication
//            stopConnectionManagerListener();
//        }
//    }
    
    
//    public void setClientListenerPort(int port) {
//        if (port == getClientListenerPort()) {
//            // Ignore new setting
//            return;
//        }
//        JiveGlobals.setProperty("xmpp.socket.plain.port", String.valueOf(port));
//        // Stop the port listener for clients
//        stopClientListeners();
//        if (isClientListenerEnabled()) {
//            // Start the port listener for clients
//            startClientListeners(localIPAddress);
//        }
//    }
//
//    public void setClientSSLListenerPort(int port) {
//        if (port == getClientSSLListenerPort()) {
//            // Ignore new setting
//            return;
//        }
//        JiveGlobals.setProperty("xmpp.socket.ssl.port", String.valueOf(port));
//        // Stop the port listener for secured clients
//        stopClientSSLListeners();
//        if (isClientSSLListenerEnabled()) {
//            // Start the port listener for secured clients
//            startClientSSLListeners(localIPAddress);
//        }
//    }
//
//    public void setServerListenerPort(int port) {
//        if (port == getServerListenerPort()) {
//            // Ignore new setting
//            return;
//        }
//        JiveGlobals.setProperty("xmpp.server.socket.port", String.valueOf(port));
//        // Stop the port listener for s2s communication
//        stopServerListener();
//        if (isServerListenerEnabled()) {
//            // Start the port listener for s2s communication
//            startServerListener(localIPAddress);
//        }
//    }
//
//    public void setComponentListenerPort(int port) {
//        if (port == getComponentListenerPort()) {
//            // Ignore new setting
//            return;
//        }
//        JiveGlobals.setProperty("xmpp.component.socket.port", String.valueOf(port));
//        // Stop the port listener for external components
//        stopComponentListener();
//        if (isComponentListenerEnabled()) {
//            // Start the port listener for external components
//            startComponentListener(localIPAddress);
//        }
//    }
//    
//    public void setConnectionManagerListenerPort(int port) {
//        if (port == getConnectionManagerListenerPort()) {
//            // Ignore new setting
//            return;
//        }
//        JiveGlobals.setProperty("xmpp.multiplex.socket.port", String.valueOf(port));
//        // Stop the port listener for connection managers
//        stopConnectionManagerListener();
//        if (isConnectionManagerListenerEnabled()) {
//            // Start the port listener for connection managers
//            startConnectionManagerListener(localIPAddress);
//        }
//    }
//    

    // #####################################################################
    // Module management
    // #####################################################################

    public void start() {
        super.start();
        isStarted = true;
        createSocket();
        SocketSendingTracker.getInstance().start();
    }

    public void stop() {
        super.stop();
        stopClientListeners();
        stopClientSSLListeners();
        stopComponentListener();
        stopConnectionManagerListener();
        stopServerListener();
        stopCloudRoutingListener();
        SocketSendingTracker.getInstance().shutdown();
    }
}
