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
package org.jivesoftware.wildfire;

import org.jivesoftware.database.DbConnectionManager;
import org.jivesoftware.util.JiveGlobals;
import org.jivesoftware.util.LocaleUtils;
import org.jivesoftware.util.Log;
import org.jivesoftware.util.PropertyProvider;
import org.jivesoftware.util.Version;
import org.jivesoftware.wildfire.audit.AuditManager;
import org.jivesoftware.wildfire.audit.spi.AuditManagerImpl;
import org.jivesoftware.wildfire.commands.AdHocCommandHandler;
import org.jivesoftware.wildfire.component.InternalComponentManager;
import org.jivesoftware.wildfire.container.Module;
import org.jivesoftware.wildfire.container.PluginManager;
import org.jivesoftware.wildfire.disco.IQDiscoInfoHandler;
import org.jivesoftware.wildfire.disco.IQDiscoItemsHandler;
import org.jivesoftware.wildfire.disco.ServerFeaturesProvider;
import org.jivesoftware.wildfire.disco.ServerItemsProvider;
import org.jivesoftware.wildfire.filetransfer.proxy.FileTransferProxy;
import org.jivesoftware.wildfire.filetransfer.FileTransferManager;
import org.jivesoftware.wildfire.filetransfer.DefaultFileTransferManager;
import org.jivesoftware.wildfire.handler.*;
import org.jivesoftware.wildfire.muc.MultiUserChatServer;
import org.jivesoftware.wildfire.muc.spi.MultiUserChatServerImpl;
import org.jivesoftware.wildfire.net.ServerTrafficCounter;
import org.jivesoftware.wildfire.roster.RosterManager;
import org.jivesoftware.wildfire.spi.*;
import org.jivesoftware.wildfire.transport.TransportHandler;
import org.jivesoftware.wildfire.user.UserManager;
import org.jivesoftware.wildfire.vcard.VCardManager;
import org.xmpp.packet.JID;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * The main XMPP server that will load, initialize and start all the server's
 * modules. The server is unique in the JVM and could be obtained by using the
 * {@link #getInstance()} method.<p>
 * <p/>
 * The loaded modules will be initialized and may access through the server other
 * modules. This means that the only way for a module to locate another module is
 * through the server. The server maintains a list of loaded modules.<p>
 * <p/>
 * After starting up all the modules the server will load any available plugin.
 * For more information see: {@link org.jivesoftware.wildfire.container.PluginManager}.<p>
 * <p/>
 * A configuration file keeps the server configuration. This information is required for the
 * server to work correctly. The server assumes that the configuration file is named
 * <b>wildfire.xml</b> and is located in the <b>conf</b> folder. The folder that keeps
 * the configuration file must be located under the home folder. The server will try different
 * methods to locate the home folder.
 * <p/>
 * <ol>
 * <li><b>system property</b> - The server will use the value defined in the <i>wildfireHome</i>
 * system property.</li>
 * <li><b>working folder</b> -  The server will check if there is a <i>conf</i> folder in the
 * working directory. This is the case when running in standalone mode.</li>
 * <li><b>wildfire_init.xml file</b> - Attempt to load the value from wildfire_init.xml which
 * must be in the classpath</li>
 * </ol>
 *
 * @author Gaston Dombiak
 */
public class XMPPServer {

    private static XMPPServer instance;

    private Version version;
    private Date startDate;
    private Date stopDate;
    private boolean initialized = false;
    private List<String> mServerNames; 

    /**
     * All modules loaded by this server
     */
    private Map<Class, Module> modules = new HashMap<Class, Module>();

    /**
     * Listeners that will be notified when the server has started or is about to be stopped.
     */
    private List<XMPPServerListener> listeners = new CopyOnWriteArrayList<XMPPServerListener>();

    /**
     * Location of the home directory. All configuration files should be
     * located here.
     */
    private File wildfireHome;
    private ClassLoader loader;

    private PluginManager pluginManager;
    private InternalComponentManager componentManager;
    private LocationManager mLocationManager = null;

    /**
     * True if in setup mode
     */
    private boolean setupMode = true;

    private RoutingTable mRoutingTable;

    /**
     * Returns a singleton instance of XMPPServer.
     *
     * @return an instance.
     */
    public static XMPPServer getInstance() {
        return instance;
    }
    
    /**
     * TEMPORARY API.  
     * 
     * @return
     */
    public InternalComponentManager getInternalComponentManager() {
        return componentManager;
    }
    
    /**
     * Creates a server and starts it.
     */
    public XMPPServer(LocationManager locMgr, List<String> domainNames, PropertyProvider localConfig, PropertyProvider globalProperties) {
        // We may only have one instance of the server running on the JVM
        if (instance != null) {
            throw new IllegalStateException("A server is already running");
        }
        mLocationManager = locMgr;
        
        JiveGlobals.setProviders(localConfig, globalProperties);
        instance = this;
        start(domainNames);
    }

    /**
     * Returns a snapshot of the server's status.
     *
     * @return the server information current at the time of the method call.
     */
    public XMPPServerInfo getServerInfo() {
        if (!initialized) {
            throw new IllegalStateException("Not initialized yet");
        }
        return new XMPPServerInfoImpl(mServerNames, version, startDate, stopDate, getConnectionManager());
    }
    
    public boolean isShuttingDown() {
        return false;
    }
    
    public Collection<String> getServerNames() {
        return mServerNames;
    }
    
    public boolean isLocalDomain(String domain) {
        return mServerNames.contains(domain);
    }

    /**
     * Returns true if the given address is local to the server (managed by this
     * server domain). Return false even if the jid's domain matches a local component's
     * service JID.
     *
     * @param jid the JID to check.
     * @return true if the address is a local address to this server.
     */
    public boolean isLocal(JID jid) {
        if (jid != null && isLocalDomain(jid.getDomain())) {
            return mLocationManager.isLocal(jid);
        }
        return false;
    }

    /**
     * Returns true if the given address does not match the local server hostname and does not
     * match a component service JID.
     *
     * @param jid the JID to check.
     * @return true if the given address does not match the local server hostname and does not
     *         match a component service JID.
     */
    public boolean isRemote(JID jid) {
        if (jid != null) {
            if (!isLocal(jid) && componentManager.getComponent(jid) == null) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns true if the given address matches a component service JID.
     *
     * @param jid the JID to check.
     * @return true if the given address matches a component service JID.
     */
    public boolean matchesComponent(JID jid) {
        if (jid != null) {
            return !isLocal(jid) && componentManager.getComponent(jid) != null;
        }
        return false;
    }
    
    public boolean isThisCloud(JID jid) {
        return true;
    }
    
    /**
     * Returns a collection with the JIDs of the server's admins. The collection may include
     * JIDs of local users and users of remote servers.
     *
     * @return a collection with the JIDs of the server's admins.
     */
    public Collection<JID> getAdmins() {
        Collection<JID> admins = new ArrayList<JID>();
        // Add the JIDs of the local users that are admins
        String usernames = JiveGlobals.getXMLProperty("admin.authorizedUsernames");
        if (usernames == null) {
            // Fall back to old method for defining admins (i.e. using adminConsole prefix
            usernames = JiveGlobals.getXMLProperty("adminConsole.authorizedUsernames");
        }
        usernames = (usernames == null || usernames.trim().length() == 0) ? "admin" : usernames;
        StringTokenizer tokenizer = new StringTokenizer(usernames, ",");
        while (tokenizer.hasMoreTokens()) {
            String username = tokenizer.nextToken();
            try {
                admins.add(new JID(username.toLowerCase().trim()));
            }
            catch (IllegalArgumentException e) {
                // Ignore usernames that when appended @server.com result in an invalid JID
                Log.warn("Invalid username found in authorizedUsernames at wildfire.xml: " +
                        username, e);
            }
        }

        // Add bare JIDs of users that are admins (may include remote users)
        String jids = JiveGlobals.getXMLProperty("admin.authorizedJIDs");
        jids = (jids == null || jids.trim().length() == 0) ? "" : jids;
        tokenizer = new StringTokenizer(jids, ",");
        while (tokenizer.hasMoreTokens()) {
            String jid = tokenizer.nextToken().toLowerCase().trim();
            try {
                admins.add(new JID(jid));
            }
            catch (IllegalArgumentException e) {
                Log.warn("Invalid JID found in authorizedJIDs at wildfire.xml: " + jid, e);
            }
        }

        return admins;
    }

    /**
     * Adds a new server listener that will be notified when the server has been started
     * or is about to be stopped.
     *
     * @param listener the new server listener to add.
     */
    public void addServerListener(XMPPServerListener listener) {
        listeners.add(listener);
    }

    /**
     * Removes a server listener that was being notified when the server was being started
     * or was about to be stopped.
     *
     * @param listener the server listener to remove.
     */
    public void removeServerListener(XMPPServerListener listener) {
        listeners.remove(listener);
    }
    
    private void initialize(List<String> domainNames) throws FileNotFoundException {
        mServerNames = new CopyOnWriteArrayList<String>(domainNames);
        
        version = new Version(3, 1, 0, Version.ReleaseStatus.Release, 0);
        setupMode = false;

//        if (isStandAlone()) {
//            Runtime.getRuntime().addShutdownHook(new ShutdownHookThread());
//        }

        loader = Thread.currentThread().getContextClassLoader();
        componentManager = InternalComponentManager.getInstance();

        initialized = true;
    }

    public void start(List<String> domainNames) {
        try {
            initialize(domainNames);

            // If the server has already been setup then we can start all the server's modules
            if (!setupMode) {
                verifyDataSource();
                // First load all the modules so that modules may access other modules while
                // being initialized
                loadModules();
                // Initize all the modules
                initModules();
                // Start all the modules
                startModules();
                // Initialize component manager (initialize before plugins get loaded)
                InternalComponentManager.getInstance().start();
                
//                Interop.getInstance().start(this, componentManager);
                
            }
            // Initialize statistics
            ServerTrafficCounter.initStatistics();

            // Load plugins (when in setup mode only the admin console will be loaded)
            File pluginDir = new File(wildfireHome, "im"+File.separator+"plugins");
            pluginManager = new PluginManager(pluginDir);
            pluginManager.start();

            // Log that the server has been started
            List<String> params = new ArrayList<String>();
            params.add(version.getVersionString());
            params.add(JiveGlobals.formatDateTime(new Date()));
            String startupBanner = LocaleUtils.getLocalizedString("startup.name", params);
            Log.info(startupBanner);
            System.out.println(startupBanner);

            startDate = new Date();
            stopDate = null;
            // Notify server listeners that the server has been started
            for (XMPPServerListener listener : listeners) {
                listener.serverStarted();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            Log.error(e);
            System.out.println(LocaleUtils.getLocalizedString("startup.error"));
            shutdownServer();
        }
    }

    private void loadModules() {
        // Load boot modules
        mRoutingTable = (RoutingTable)loadModule(JiveGlobals.getXMLProperty("routingTableImpl.className"));
        loadModule(AuditManagerImpl.class.getName());
        loadModule(RosterManager.class.getName());
        loadModule(PrivateStorage.class.getName());
        // Load core modules
        loadModule(PresenceManagerImpl.class.getName());
        loadModule(SessionManager.class.getName());
        loadModule(PacketRouterImpl.class.getName());
        loadModule(IQRouter.class.getName());
        loadModule(MessageRouter.class.getName());
        loadModule(PresenceRouter.class.getName());
        loadModule(MulticastRouter.class.getName());
//        loadModule(PacketTransporterImpl.class.getName());
        loadModule(PacketDelivererImpl.class.getName());
        loadModule(TransportHandler.class.getName());
        loadModule(OfflineMessageStrategy.class.getName());
        loadModule(OfflineMessageStore.class.getName());
        loadModule(VCardManager.class.getName());
        // Load standard modules
        loadModule(IQBindHandler.class.getName());
        loadModule(IQSessionEstablishmentHandler.class.getName());
        loadModule(IQAuthHandler.class.getName());
        loadModule(IQPrivateHandler.class.getName());
        loadModule(IQRegisterHandler.class.getName());
        loadModule(IQRosterHandler.class.getName());
        loadModule(IQTimeHandler.class.getName());
        loadModule(IQvCardHandler.class.getName());
        loadModule(IQVersionHandler.class.getName());
        loadModule(IQLastActivityHandler.class.getName());
        loadModule(PresenceSubscribeHandler.class.getName());
        loadModule(PresenceUpdateHandler.class.getName());
        loadModule(IQDiscoInfoHandler.class.getName());
        loadModule(IQDiscoItemsHandler.class.getName());
        loadModule(IQOfflineMessagesHandler.class.getName());
        loadModule(MultiUserChatServerImpl.class.getName());

        // TIM HACK
//        loadModule(MulticastDNSService.class.getName());
        loadModule(IQSharedGroupHandler.class.getName());
        loadModule(AdHocCommandHandler.class.getName());
        loadModule(IQPrivacyHandler.class.getName());
        loadModule(DefaultFileTransferManager.class.getName());
        loadModule(FileTransferProxy.class.getName());
//        loadModule(UpdateManager.class.getName());
        // Load this module always last since we don't want to start listening for clients
        // before the rest of the modules have been started
        loadModule(ConnectionManagerImpl.class.getName());
    }

    /**
     * Loads a module.
     *
     * @param module the name of the class that implements the Module interface.
     */
    private Module loadModule(String module) {
        try {
            Class modClass = loader.loadClass(module);
            Module mod = (Module) modClass.newInstance();
            this.modules.put(modClass, mod);
            return mod;
        }
        catch (Exception e) {
            e.printStackTrace();
            Log.error(LocaleUtils.getLocalizedString("admin.error"), e);
            return null;
        }
    }

    private void initModules() {
        for (Module module : modules.values()) {
            boolean isInitialized = false;
            try {
                module.initialize(this);
                isInitialized = true;
            }
            catch (Exception e) {
                e.printStackTrace();
                // Remove the failed initialized module
                this.modules.remove(module.getClass());
                if (isInitialized) {
                    module.stop();
                    module.destroy();
                }
                Log.error(LocaleUtils.getLocalizedString("admin.error"), e);
            }
        }
    }

    /**
     * <p>Following the loading and initialization of all the modules
     * this method is called to iterate through the known modules and
     * start them.</p>
     */
    private void startModules() {
        for (Module module : modules.values()) {
            boolean started = false;
            try {
                module.start();
            }
            catch (Exception e) {
                if (started && module != null) {
                    module.stop();
                    module.destroy();
                }
                Log.error(LocaleUtils.getLocalizedString("admin.error"), e);
            }
        }
    }

    /**
     * Restarts the server and all it's modules only if the server is restartable. Otherwise do
     * nothing.
     */
    public void restart() {
//        if (isStandAlone() && isRestartable()) {
//            try {
//                Class wrapperClass = Class.forName(WRAPPER_CLASSNAME);
//                Method restartMethod = wrapperClass.getMethod("restart", (Class []) null);
//                restartMethod.invoke(null, (Object []) null);
//            }
//            catch (Exception e) {
//                Log.error("Could not restart container", e);
//            }
//        }
    }

    /**
     * Stops the server only if running in standalone mode. Do nothing if the server is running
     * inside of another server.
     */
    public void stop() {
        // Only do a system exit if we're running standalone
//        if (isStandAlone()) {
//            // if we're in a wrapper, we have to tell the wrapper to shut us down
//            if (isRestartable()) {
//                try {
//                    Class wrapperClass = Class.forName(WRAPPER_CLASSNAME);
//                    Method stopMethod = wrapperClass.getMethod("stop", Integer.TYPE);
//                    stopMethod.invoke(null, 0);
//                }
//                catch (Exception e) {
//                    Log.error("Could not stop container", e);
//                }
//            }
//            else {
//                shutdownServer();
//                stopDate = new Date();
//                Thread shutdownThread = new ShutdownThread();
//                shutdownThread.setDaemon(true);
//                shutdownThread.start();
//            }
//        }
//        else {
            // Close listening socket no matter what the condition is in order to be able
            // to be restartable inside a container.
            shutdownServer();
            stopDate = new Date();
//        }
    }

    public boolean isSetupMode() {
        return setupMode;
    }

    public boolean isRestartable() {
//        boolean restartable;
//        try {
//            restartable = Class.forName(WRAPPER_CLASSNAME) != null;
//        }
//        catch (ClassNotFoundException e) {
//            restartable = false;
//        }
//        return restartable;
        return false;
    }

    /**
     * Returns if the server is running in standalone mode. We consider that it's running in
     * standalone if the "org.jivesoftware.wildfire.starter.ServerStarter" class is present in the
     * system.
     *
     * @return true if the server is running in standalone mode.
     */
    public boolean isStandAlone() {
//        if (false) {
//            boolean standalone;
//            try {
//                standalone = Class.forName(STARTER_CLASSNAME) != null;
//            }
//            catch (ClassNotFoundException e) {
//                standalone = false;
//            }
//            return standalone;
//        } else {
//            return false;
//        }
        return false;
    }

    /**
     * Verify that the database is accessible.
     */
    private void verifyDataSource() {
        java.sql.Connection conn = null;
        try {
            conn = DbConnectionManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement("SELECT count(*) FROM jiveID");
            ResultSet rs = stmt.executeQuery();
            rs.next();
            rs.close();
            stmt.close();
        }
        catch (Exception e) {
            System.err.println("Database setup or configuration error: " +
                    "Please verify your database settings and check the " +
                    "logs/error.log file for detailed error messages.");
            Log.error("Database could not be accessed", e);
            throw new IllegalArgumentException(e);
        }
        finally {
            if (conn != null) {
                try {
                    conn.close();
                }
                catch (SQLException e) {
                    Log.error(e);
                }
            }
        }
    }

//    /**
//     * Verifies that the given home guess is a real Wildfire home directory.
//     * We do the verification by checking for the Wildfire config file in
//     * the config dir of jiveHome.
//     *
//     * @param homeGuess a guess at the path to the home directory.
//     * @param jiveConfigName the name of the config file to check.
//     * @return a file pointing to the home directory or null if the
//     *         home directory guess was wrong.
//     * @throws java.io.FileNotFoundException if there was a problem with the home
//     *                                       directory provided
//     */
//    private File verifyHome(String homeGuess, String jiveConfigName) throws FileNotFoundException {
//        File wildfireHome = new File(homeGuess);
//        File configFile = new File(wildfireHome, jiveConfigName);
//        if (!configFile.exists()) {
//            throw new FileNotFoundException();
//        }
//        else {
//            try {
//                return new File(wildfireHome.getCanonicalPath());
//            }
//            catch (Exception ex) {
//                throw new FileNotFoundException();
//            }
//        }
//    }

//    /**
//     * <p>A thread to ensure the server shuts down no matter what.</p>
//     * <p>Spawned when stop() is called in standalone mode, we wait a few
//     * seconds then call system exit().</p>
//     *
//     * @author Iain Shigeoka
//     */
//    private class ShutdownHookThread extends Thread {
//
//        /**
//         * <p>Logs the server shutdown.</p>
//         */
//        public void run() {
//            shutdownServer();
//            Log.info("Server halted");
//            System.err.println("Server halted");
//        }
//    }

//    /**
//     * <p>A thread to ensure the server shuts down no matter what.</p>
//     * <p>Spawned when stop() is called in standalone mode, we wait a few
//     * seconds then call system exit().</p>
//     *
//     * @author Iain Shigeoka
//     */
//    private class ShutdownThread extends Thread {
//
//        /**
//         * <p>Shuts down the JVM after a 5 second delay.</p>
//         */
//        public void run() {
//            try {
//                Thread.sleep(5000);
//              // No matter what, we make sure it's dead
//                System.exit(0);
//            }
//            catch (InterruptedException e) {
//              // Ignore.
//            }
//
//        }
//    }

    /**
     * Makes a best effort attempt to shutdown the server
     */
    private void shutdownServer() {
        // Notify server listeners that the server is about to be stopped
        for (XMPPServerListener listener : listeners) {
            listener.serverStopping();
        }
        
//        Interop.getInstance().stop();
        
        // If we don't have modules then the server has already been shutdown
        if (modules.isEmpty()) {
            return;
        }
        // Get all modules and stop and destroy them
        for (Module module : modules.values()) {
            module.stop();
            module.destroy();
        }
        modules.clear();
        // Stop all plugins
        if (pluginManager != null) {
            pluginManager.shutdown();
        }
        // Stop the Db connection manager.
        DbConnectionManager.destroyConnectionProvider();
        // hack to allow safe stopping
        Log.info("Wildfire stopped");
    }

    /**
     * Returns the <code>ConnectionManager</code> registered with this server. The
     * <code>ConnectionManager</code> was registered with the server as a module while starting up
     * the server.
     *
     * @return the <code>ConnectionManager</code> registered with this server.
     */
    public ConnectionManager getConnectionManager() {
        return (ConnectionManager) modules.get(ConnectionManagerImpl.class);
    }

    /**
     * Returns the <code>RoutingTable</code> registered with this server. The
     * <code>RoutingTable</code> was registered with the server as a module while starting up
     * the server.
     *
     * @return the <code>RoutingTable</code> registered with this server.
     */
    public RoutingTable getRoutingTable() {
        return mRoutingTable;
    }

    /**
     * Returns the <code>PacketDeliverer</code> registered with this server. The
     * <code>PacketDeliverer</code> was registered with the server as a module while starting up
     * the server.
     *
     * @return the <code>PacketDeliverer</code> registered with this server.
     */
    public PacketDeliverer getPacketDeliverer() {
        return (PacketDeliverer) modules.get(PacketDelivererImpl.class);
    }

    /**
     * Returns the <code>RosterManager</code> registered with this server. The
     * <code>RosterManager</code> was registered with the server as a module while starting up
     * the server.
     *
     * @return the <code>RosterManager</code> registered with this server.
     */
    public RosterManager getRosterManager() {
        return (RosterManager) modules.get(RosterManager.class);
    }

    /**
     * Returns the <code>PresenceManager</code> registered with this server. The
     * <code>PresenceManager</code> was registered with the server as a module while starting up
     * the server.
     *
     * @return the <code>PresenceManager</code> registered with this server.
     */
    public PresenceManager getPresenceManager() {
        return (PresenceManager) modules.get(PresenceManagerImpl.class);
    }

    /**
     * Returns the <code>OfflineMessageStore</code> registered with this server. The
     * <code>OfflineMessageStore</code> was registered with the server as a module while starting up
     * the server.
     *
     * @return the <code>OfflineMessageStore</code> registered with this server.
     */
    public OfflineMessageStore getOfflineMessageStore() {
        return (OfflineMessageStore) modules.get(OfflineMessageStore.class);
    }

    /**
     * Returns the <code>OfflineMessageStrategy</code> registered with this server. The
     * <code>OfflineMessageStrategy</code> was registered with the server as a module while starting
     * up the server.
     *
     * @return the <code>OfflineMessageStrategy</code> registered with this server.
     */
    public OfflineMessageStrategy getOfflineMessageStrategy() {
        return (OfflineMessageStrategy) modules.get(OfflineMessageStrategy.class);
    }

    /**
     * Returns the <code>PacketRouter</code> registered with this server. The
     * <code>PacketRouter</code> was registered with the server as a module while starting up
     * the server.
     *
     * @return the <code>PacketRouter</code> registered with this server.
     */
    public PacketRouter getPacketRouter() {
        return (PacketRouter) modules.get(PacketRouterImpl.class);
    }

    /**
     * Returns the <code>IQRegisterHandler</code> registered with this server. The
     * <code>IQRegisterHandler</code> was registered with the server as a module while starting up
     * the server.
     *
     * @return the <code>IQRegisterHandler</code> registered with this server.
     */
    public IQRegisterHandler getIQRegisterHandler() {
        return (IQRegisterHandler) modules.get(IQRegisterHandler.class);
    }

    /**
     * Returns the <code>IQAuthHandler</code> registered with this server. The
     * <code>IQAuthHandler</code> was registered with the server as a module while starting up
     * the server.
     *
     * @return the <code>IQAuthHandler</code> registered with this server.
     */
    public IQAuthHandler getIQAuthHandler() {
        return (IQAuthHandler) modules.get(IQAuthHandler.class);
    }

    /**
     * Returns the <code>PluginManager</code> instance registered with this server.
     *
     * @return the PluginManager instance.
     */
    public PluginManager getPluginManager() {
        return pluginManager;
    }

    /**
     * Returns a list with all the modules registered with the server that inherit from IQHandler.
     *
     * @return a list with all the modules registered with the server that inherit from IQHandler.
     */
    public List<IQHandler> getIQHandlers() {
        List<IQHandler> answer = new ArrayList<IQHandler>();
        for (Module module : modules.values()) {
            if (module instanceof IQHandler) {
                answer.add((IQHandler) module);
            }
        }
        return answer;
    }

    /**
     * Returns the <code>SessionManager</code> registered with this server. The
     * <code>SessionManager</code> was registered with the server as a module while starting up
     * the server.
     *
     * @return the <code>SessionManager</code> registered with this server.
     */
    public SessionManager getSessionManager() {
        return (SessionManager) modules.get(SessionManager.class);
    }

    /**
     * Returns the <code>TransportHandler</code> registered with this server. The
     * <code>TransportHandler</code> was registered with the server as a module while starting up
     * the server.
     *
     * @return the <code>TransportHandler</code> registered with this server.
     */
    public TransportHandler getTransportHandler() {
        return (TransportHandler) modules.get(TransportHandler.class);
    }

    /**
     * Returns the <code>PresenceUpdateHandler</code> registered with this server. The
     * <code>PresenceUpdateHandler</code> was registered with the server as a module while starting
     * up the server.
     *
     * @return the <code>PresenceUpdateHandler</code> registered with this server.
     */
    public PresenceUpdateHandler getPresenceUpdateHandler() {
        return (PresenceUpdateHandler) modules.get(PresenceUpdateHandler.class);
    }

    /**
     * Returns the <code>PresenceSubscribeHandler</code> registered with this server. The
     * <code>PresenceSubscribeHandler</code> was registered with the server as a module while
     * starting up the server.
     *
     * @return the <code>PresenceSubscribeHandler</code> registered with this server.
     */
    public PresenceSubscribeHandler getPresenceSubscribeHandler() {
        return (PresenceSubscribeHandler) modules.get(PresenceSubscribeHandler.class);
    }

    /**
     * Returns the <code>IQRouter</code> registered with this server. The
     * <code>IQRouter</code> was registered with the server as a module while starting up
     * the server.
     *
     * @return the <code>IQRouter</code> registered with this server.
     */
    public IQRouter getIQRouter() {
        return (IQRouter) modules.get(IQRouter.class);
    }

    /**
     * Returns the <code>MessageRouter</code> registered with this server. The
     * <code>MessageRouter</code> was registered with the server as a module while starting up
     * the server.
     *
     * @return the <code>MessageRouter</code> registered with this server.
     */
    public MessageRouter getMessageRouter() {
        return (MessageRouter) modules.get(MessageRouter.class);
    }

    /**
     * Returns the <code>PresenceRouter</code> registered with this server. The
     * <code>PresenceRouter</code> was registered with the server as a module while starting up
     * the server.
     *
     * @return the <code>PresenceRouter</code> registered with this server.
     */
    public PresenceRouter getPresenceRouter() {
        return (PresenceRouter) modules.get(PresenceRouter.class);
    }

    /**
     * Returns the <code>MulticastRouter</code> registered with this server. The
     * <code>MulticastRouter</code> was registered with the server as a module while starting up
     * the server.
     *
     * @return the <code>MulticastRouter</code> registered with this server.
     */
    public MulticastRouter getMulticastRouter() {
        return (MulticastRouter) modules.get(MulticastRouter.class);
    }

    /**
     * Returns the <code>UserManager</code> registered with this server. The
     * <code>UserManager</code> was registered with the server as a module while starting up
     * the server.
     *
     * @return the <code>UserManager</code> registered with this server.
     */
    public UserManager getUserManager() {
        return UserManager.getInstance();
    }

//    /**
//     * Returns the <code>UpdateManager</code> registered with this server. The
//     * <code>UpdateManager</code> was registered with the server as a module while starting up
//     * the server.
//     *
//     * @return the <code>UpdateManager</code> registered with this server.
//     */
//    public UpdateManager getUpdateManager() {
//        return (UpdateManager) modules.get(UpdateManager.class);
//    }

    /**
     * Returns the <code>AuditManager</code> registered with this server. The
     * <code>AuditManager</code> was registered with the server as a module while starting up
     * the server.
     *
     * @return the <code>AuditManager</code> registered with this server.
     */
    public AuditManager getAuditManager() {
        return (AuditManager) modules.get(AuditManagerImpl.class);
    }

    /**
     * Returns a list with all the modules that provide "discoverable" features.
     *
     * @return a list with all the modules that provide "discoverable" features.
     */
    public List<ServerFeaturesProvider> getServerFeaturesProviders() {
        List<ServerFeaturesProvider> answer = new ArrayList<ServerFeaturesProvider>();
        for (Module module : modules.values()) {
            if (module instanceof ServerFeaturesProvider) {
                answer.add((ServerFeaturesProvider) module);
            }
        }
        return answer;
    }

    /**
     * Returns a list with all the modules that provide "discoverable" items associated with
     * the server.
     *
     * @return a list with all the modules that provide "discoverable" items associated with
     *         the server.
     */
    public List<ServerItemsProvider> getServerItemsProviders() {
        List<ServerItemsProvider> answer = new ArrayList<ServerItemsProvider>();
        for (Module module : modules.values()) {
            if (module instanceof ServerItemsProvider) {
                answer.add((ServerItemsProvider) module);
            }
        }
        return answer;
    }

    /**
     * Returns the <code>IQDiscoInfoHandler</code> registered with this server. The
     * <code>IQDiscoInfoHandler</code> was registered with the server as a module while starting up
     * the server.
     *
     * @return the <code>IQDiscoInfoHandler</code> registered with this server.
     */
    public IQDiscoInfoHandler getIQDiscoInfoHandler() {
        return (IQDiscoInfoHandler) modules.get(IQDiscoInfoHandler.class);
    }

    /**
     * Returns the <code>IQDiscoItemsHandler</code> registered with this server. The
     * <code>IQDiscoItemsHandler</code> was registered with the server as a module while starting up
     * the server.
     *
     * @return the <code>IQDiscoItemsHandler</code> registered with this server.
     */
    public IQDiscoItemsHandler getIQDiscoItemsHandler() {
        return (IQDiscoItemsHandler) modules.get(IQDiscoItemsHandler.class);
    }

    /**
     * Returns the <code>PrivateStorage</code> registered with this server. The
     * <code>PrivateStorage</code> was registered with the server as a module while starting up
     * the server.
     *
     * @return the <code>PrivateStorage</code> registered with this server.
     */
    public PrivateStorage getPrivateStorage() {
        return (PrivateStorage) modules.get(PrivateStorage.class);
    }

    /**
     * Returns the <code>MultiUserChatServer</code> registered with this server. The
     * <code>MultiUserChatServer</code> was registered with the server as a module while starting up
     * the server.
     *
     * @return the <code>MultiUserChatServer</code> registered with this server.
     */
    public MultiUserChatServer getMultiUserChatServer() {
        return (MultiUserChatServer) modules.get(MultiUserChatServerImpl.class);
    }

    /**
     * Returns the <code>AdHocCommandHandler</code> registered with this server. The
     * <code>AdHocCommandHandler</code> was registered with the server as a module while starting up
     * the server.
     *
     * @return the <code>AdHocCommandHandler</code> registered with this server.
     */
    public AdHocCommandHandler getAdHocCommandHandler() {
        return (AdHocCommandHandler) modules.get(AdHocCommandHandler.class);
    }

    /**
     * Returns the <code>FileTransferProxy</code> registered with this server. The
     * <code>FileTransferProxy</code> was registered with the server as a module while starting up
     * the server.
     *
     * @return the <code>FileTransferProxy</code> registered with this server.
     */
    public FileTransferProxy getFileTransferProxy() {
        return (FileTransferProxy) modules.get(FileTransferProxy.class);
    }

    /**
     * Returns the <code>FileTransferManager</code> registered with this server. The
     * <code>FileTransferManager</code> was registered with the server as a module while starting up
     * the server.
     *
     * @return the <code>FileTransferProxy</code> registered with this server.
     */
    public FileTransferManager getFileTransferManager() {
        return (FileTransferManager) modules.get(DefaultFileTransferManager.class);
    }
}
