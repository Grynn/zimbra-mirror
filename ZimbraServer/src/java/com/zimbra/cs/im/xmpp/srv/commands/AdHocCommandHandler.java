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
 * Part of the Zimbra Collaboration Suite Server.
 *
 * The Original Code is Copyright (C) Jive Software. Used with permission
 * Portions created by Zimbra are Copyright (C) 2006 Zimbra, Inc.
 * All Rights Reserved.
 *
 * Contributor(s):
 *
 * ***** END LICENSE BLOCK *****
 */

package com.zimbra.cs.im.xmpp.srv.commands;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import com.zimbra.cs.im.xmpp.srv.IQHandlerInfo;
import com.zimbra.cs.im.xmpp.srv.XMPPServer;
import com.zimbra.cs.im.xmpp.srv.auth.UnauthorizedException;
import com.zimbra.cs.im.xmpp.srv.disco.*;
import com.zimbra.cs.im.xmpp.srv.forms.spi.XDataFormImpl;
import com.zimbra.cs.im.xmpp.srv.handler.IQHandler;
import org.xmpp.packet.IQ;
import org.xmpp.packet.JID;

import java.util.*;

/**
 * An AdHocCommandHandler is responsbile for providing discoverable information about the
 * supported commands and for handling commands requests. This is an implementation of JEP-50:
 * Ad-Hoc Commands.<p>
 *
 * Ad-hoc commands that require user interaction will have one or more stages. For each stage the
 * user will complete a data form and send it back to the server. The data entered by the user is
 * kept in a SessionData. Instances of {@link AdHocCommand} are stateless. In order to prevent
 * "bad" users from consuming all system memory there exists a limit of simultaneous commands that
 * a user might perform. Configure the system property <tt>"xmpp.command.limit"</tt> to control
 * this limit. User sessions will also timeout and their data destroyed if they have not been
 * executed within a time limit since the session was created. The default timeout value is 10
 * minutes. The timeout value can be modified by setting the system property
 * <tt>"xmpp.command.timeout"</tt>.<p>
 *
 * New commands can be added dynamically by sending the message {@link #addCommand(AdHocCommand)}.
 * The command will immediatelly appear in the disco#items list and might be executed by those
 * users with enough execution permissions.
 *
 * @author Gaston Dombiak
 */
public class AdHocCommandHandler extends IQHandler
        implements ServerFeaturesProvider, DiscoInfoProvider, DiscoItemsProvider {

    private static final String NAMESPACE = "http://jabber.org/protocol/commands";

    private String serverName;
    private IQHandlerInfo info;
    private IQDiscoInfoHandler infoHandler;
    private IQDiscoItemsHandler itemsHandler;
    /**
     * Manager that keeps the list of ad-hoc commands and processing command requests.
     */
    private AdHocCommandManager manager;

    public AdHocCommandHandler() {
        super("Ad-Hoc Commands Handler");
        info = new IQHandlerInfo("command", NAMESPACE);
        manager = new AdHocCommandManager();
    }

    public IQ handleIQ(IQ packet) throws UnauthorizedException {
        return manager.process(packet);
    }

    public IQHandlerInfo getInfo() {
        return info;
    }

    public Iterator<String> getFeatures() {
        ArrayList<String> features = new ArrayList<String>();
        features.add(NAMESPACE);
        return features.iterator();
    }

    public Iterator<Element> getIdentities(String name, String node, JID senderJID) {
        ArrayList<Element> identities = new ArrayList<Element>();
        Element identity = DocumentHelper.createElement("identity");
        identity.addAttribute("category", "automation");
        identity.addAttribute("type", NAMESPACE.equals(node) ? "command-list" : "command-node");
        identities.add(identity);
        return identities.iterator();
    }

    public Iterator<String> getFeatures(String name, String node, JID senderJID) {
        return Arrays.asList(NAMESPACE, "jabber:x:data").iterator();
    }

    public XDataFormImpl getExtendedInfo(String name, String node, JID senderJID) {
        return null;
    }

    public boolean hasInfo(String name, String node, JID senderJID) {
        if (NAMESPACE.equals(node)) {
            return true;
        }
        else {
            // Only include commands that the sender can execute
            AdHocCommand command = manager.getCommand(node);
            return command != null && command.hasPermission(senderJID);
        }
    }

    public Iterator<Element> getItems(String name, String node, JID senderJID) {
        List<Element> answer = new ArrayList<Element>();
        if (!NAMESPACE.equals(node)) {
            answer = Collections.emptyList();
        }
        else {
            Element item;
            for (AdHocCommand command : manager.getCommands()) {
                // Only include commands that the sender can invoke (i.e. has enough permissions)
                if (command.hasPermission(senderJID)) {
                    item = DocumentHelper.createElement("item");
                    item.addAttribute("jid", serverName);
                    item.addAttribute("node", command.getCode());
                    item.addAttribute("name", command.getLabel());

                    answer.add(item);
                }
            }
        }
        return answer.iterator();
    }

    public void initialize(XMPPServer server) {
        super.initialize(server);
        serverName = server.getServerInfo().getName();
        infoHandler = server.getIQDiscoInfoHandler();
        itemsHandler = server.getIQDiscoItemsHandler();
    }

    public void start() throws IllegalStateException {
        super.start();
        infoHandler.setServerNodeInfoProvider(NAMESPACE, this);
        itemsHandler.setServerNodeInfoProvider(NAMESPACE, this);
        // Add the "out of the box" commands
        addDefaultCommands();
    }

    public void stop() {
        super.stop();
        infoHandler.removeServerNodeInfoProvider(NAMESPACE);
        itemsHandler.removeServerNodeInfoProvider(NAMESPACE);
        // Stop commands
        for (AdHocCommand command : manager.getCommands()) {
            stopCommand(command);
        }
    }

    /**
     * Adds a new command to the list of supported ad-hoc commands by this server. The new
     * command will appear in the discoverable items list and will be executed for those users
     * with enough permission.
     *
     * @param command the new ad-hoc command to add.
     */
    public void addCommand(AdHocCommand command) {
        manager.addCommand(command);
        startCommand(command);
    }

    /**
     * Removes the command from the list of ad-hoc commands supported by this server. The command
     * will no longer appear in the discoverable items list.
     *
     * @param command the ad-hoc command to remove.
     */
    public void removeCommand(AdHocCommand command) {
        if (manager.removeCommand(command)) {
            stopCommand(command);
        }
    }

    private void addDefaultCommands() {
        // TODO Complete when out of the box commands are implemented
        //addCommand(new TimeCommand());
    }

    private void startCommand(AdHocCommand command) {
        infoHandler.setServerNodeInfoProvider(command.getCode(), this);
        itemsHandler.setServerNodeInfoProvider(command.getCode(), this);
    }

    private void stopCommand(AdHocCommand command) {
        infoHandler.removeServerNodeInfoProvider(command.getCode());
        itemsHandler.removeServerNodeInfoProvider(command.getCode());
    }
}
