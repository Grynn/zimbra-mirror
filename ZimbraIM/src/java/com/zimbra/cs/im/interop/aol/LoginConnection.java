/*
 * ***** BEGIN LICENSE BLOCK ***** Version: MPL 1.1
 * 
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 ("License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 * http://www.zimbra.com/license
 * 
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 * 
 * The Original Code is: Zimbra Collaboration Suite Server.
 * 
 * The Initial Developer of the Original Code is Zimbra, Inc. Portions created
 * by Zimbra are Copyright (C) 2005 Zimbra, Inc. All Rights Reserved.
 * 
 * Contributor(s):
 * 
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.cs.im.interop.aol;

import java.io.IOException;
import java.net.UnknownHostException;

import net.kano.joscar.flap.FlapCommand;
import net.kano.joscar.flap.FlapPacketEvent;
import net.kano.joscar.flapcmd.LoginFlapCmd;
import net.kano.joscar.flapcmd.SnacCommand;
import net.kano.joscar.snac.SnacRequest;
import net.kano.joscar.snac.SnacResponseEvent;
import net.kano.joscar.snaccmd.auth.AuthRequest;
import net.kano.joscar.snaccmd.auth.AuthResponse;
import net.kano.joscar.snaccmd.auth.KeyRequest;
import net.kano.joscar.snaccmd.auth.KeyResponse;

/**
 * A special type of connection which is very simplified: it only is used when we
 * connect with the login server during initial connect.
 */
public class LoginConnection extends AolConnection {

    public LoginConnection(AolMgr mgr, String host, int port, String username, String password)
        throws UnknownHostException {
        super(mgr, host, port);
        mUsername = username;
        mPassword = password;
    }

    /*(non-Javadoc)
     * @see net.kano.joscar.flap.FlapPacketListener#handleFlapPacket(net.kano.joscar.flap.FlapPacketEvent)
     */
    @Override
    public void handleFlapPacket(FlapPacketEvent e) {
        info("Got FLAP command: " + e);

        FlapCommand flapCmd = e.getFlapCommand();

        info("Got FLAP command: " + flapCmd);

        if (flapCmd instanceof LoginFlapCmd) {
            getFlapProcessor().sendFlap(new LoginFlapCmd());
            getSnacProcessor().sendSnac(new SnacRequest(new KeyRequest(mUsername), this));
        }
    }

    /*(non-Javadoc)
     * @see net.kano.joscar.snac.SnacResponseListener#handleResponse(net.kano.joscar.snac.SnacResponseEvent)
     */
    @Override
    public void handleResponse(SnacResponseEvent e) {
        info("Got SNAC response: " + e);
        SnacCommand snacCmd = e.getSnacCommand();

        info("Got SNAC response: " + snacCmd);

        if (snacCmd instanceof KeyResponse) {
            KeyResponse keyResponse = (KeyResponse) snacCmd;
            AuthRequest authRequest =
                new AuthRequest(mUsername, mPassword, PROTOCOL_VERSION, keyResponse.getKey());
            getSnacProcessor().sendSnac(new SnacRequest(authRequest, this));
        } else if (snacCmd instanceof AuthResponse) {
            info("Got Auth Response: %s", snacCmd);
            AuthResponse ar = (AuthResponse) snacCmd;

            int error = ar.getErrorCode();
            if (error != -1) {
                error("connection error! code: " + error);
                if (ar.getErrorUrl() != null) {
                    error("Error URL: " + ar.getErrorUrl());
                }
            } else {
                info("*******LOGGED IN!********");
                // loggedin = true;
                // tester.setScreennameFormat(ar.getScreenname());
                // tester.startBosConn(ar.getServer(), ar.getPort(),
                // ar.getCookie());
                mUsername = ar.getScreenname();
                info("Connect with user %s to: %s:%d with cookie %s", ar.getScreenname(), ar.getServer(), ar
                    .getPort(), ar.getCookie());

                this.stop();

                try {
                    BOSConnection bosConn =
                        new BOSConnection(getManager(), ar.getServer(), ar.getPort(), ar.getCookie());
                    bosConn.start();
                } catch (IOException ex) {
                    exceptionReceived(sprintf(
                        "Could not connect to server at %s:%d as instructed by login server", ar.getServer(),
                        ar.getPort()), ex, true);
                }
            }
        }
    }

    private String mUsername;
    private String mPassword;
}
