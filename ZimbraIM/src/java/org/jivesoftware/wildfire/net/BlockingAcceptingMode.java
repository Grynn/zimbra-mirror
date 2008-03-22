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
package org.jivesoftware.wildfire.net;

import org.jivesoftware.util.LocaleUtils;
import org.jivesoftware.util.Log;
import org.jivesoftware.wildfire.ConnectionManager;
import org.jivesoftware.wildfire.ServerPort;

import java.io.IOException;
//import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Accepts new socket connections and uses a thread for each new connection.
 *
 * @author Gaston Dombiak
 */
class BlockingAcceptingMode extends SocketAcceptingMode {

    protected BlockingAcceptingMode(ConnectionManager connManager, ServerPort serverPort) throws IOException {
        super(connManager, serverPort);
        serverSocket = new ServerSocket(serverPort.getPort(), -1, serverPort.getBindAddress());
    }

    /**
     * About as simple as it gets.  The thread spins around an accept
     * call getting sockets and creating new reading threads for each new connection.
     */
    public void run() {
        while (notTerminated) {
            try {
                Socket sock = serverSocket.accept();
                if (sock != null) {
                    Log.debug("Connect " + sock.toString());
                    SocketReader reader =
                            connManager.createSocketReader(sock, false, serverPort);
                    Thread thread = new Thread(reader, reader.getName());
                    thread.setDaemon(true);
                    thread.setPriority(Thread.NORM_PRIORITY);
                    thread.start();
                }
            }
            catch (IOException ie) {
                if (notTerminated) {
                    Log.error(LocaleUtils.getLocalizedString("admin.error.accept"),
                            ie);
                }
            }
            catch (Throwable e) {
                Log.error(LocaleUtils.getLocalizedString("admin.error.accept"), e);
            }
        }
    }
}
