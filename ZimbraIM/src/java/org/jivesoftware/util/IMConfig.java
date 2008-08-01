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
package org.jivesoftware.util;
import java.io.File;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import org.jivesoftware.util.IMConfigProperty.ConstantBoolean;
import org.jivesoftware.util.IMConfigProperty.ConstantInt;
import org.jivesoftware.util.IMConfigProperty.ConstantStrList;
import org.jivesoftware.util.IMConfigProperty.LCBoolean;
import org.jivesoftware.util.IMConfigProperty.ConstantStr;
import org.jivesoftware.util.IMConfigProperty.LCInt;
import org.jivesoftware.util.IMConfigProperty.LCStr;

import com.zimbra.common.localconfig.LC;
import com.zimbra.common.util.Constants;

/**
 * Wrapper class to abstract out fixed-constants vs LocalConfig vs Ldap-Provisioned settings into a single interface for IM.
 * 
 * provisioning keys needed:
 *      zimbraXmppClientTlsPolicy: *optional*|disabled|required
 *      zimbraFeatureXmppS2SEnabled: *true*|false
 *      
 *      zimbraFeatureXmppPlainSocketEnabled: *true*|false - 5222 client listener
 *      zimbraFeatureXmppPlainSocketBindAddress: String
 *      zimbraFeatureXmppPlainSocketPort: int (5222)
 *      
 *      zimbraFeatureXmppSSLSocketEnabled: *true*|false 
 *      zimbraFeatureXmppSSLSocketBindAddress: String
 *      zimbraFeatureXmppSSLSocketPort: int (5223)
 *      
 *      zimbraFeatureXmppCloudRoutingSocketEnabled: *true*|false 
 *      zimbraFeatureXmppCloudRoutingBindAddress: String
 *      zimbraFeatureXmppCloudRoutingPort: int (7335)
 *      
 *      XMPP Proxy:
 *           enabled
 *           bind
 *           port
 *           
 */
public enum IMConfig {
    // Providers
    CONNECTION_PROVIDER_CLASSNAME(LdapConfig.getProvider().getConnectionProvider()),
    USER_PROVIDER_CLASSNAME(LdapConfig.getProvider().getUserProvider()),
    AUTH_PROVIDER_CLASSNAME(LdapConfig.getProvider().getAuthProvider()),
    GROUP_PROVIDER_CLASSNAME(LdapConfig.getProvider().getGroupProvider()),
    PROXY_TRANSFER_PROVIDER_CLASSNAME(LdapConfig.getProvider().getProxyTransferProvider()),
    ROUTING_TABLE_CLASSNAME(LdapConfig.getProvider().getRoutingTableProvider()),
    VCARD_PROVIDER_CLASSNAME(LdapConfig.getProvider().getVCardProvider()),
    
    // Misc Server Config
    HOME_DIRECTORY(new LCStr(LC.zimbra_home)),
    DNSUTIL_DNSOVERRIDE(new LCStr(LC.xmpp_dns_override)),
    REGISTER_INBAND(new ConstantBoolean(false, "Allow XMPP inband registration (not supported in Zimbra)")),
    REGISTER_PASSWORD(new ConstantBoolean(false, "Allow XMPP password changes (not supported in Zimbra")),
    XMPP_SESSION_SENDING_TIMEOUT(new LCInt(LC.xmpp_client_write_timeout)),
    XMPP_SESSION_CONFLICT_LIMIT(new LCInt(LC.xmpp_session_conflict_limit)),
    XMPP_PRIVATE_STORAGE_ENABLED(new LCBoolean(LC.xmpp_private_storage_enabled)),
    SHUTDOWN_MESSAGE_ENABLED(new ConstantBoolean(false, "Shutdown message sent in SessionManager.stop?")),
    XMPP_SERVER_NAME(new ConstantStr("Zimbra IM Server", "Name of server as returned by XMPP Disco Info request")),
    XMPP_CLIENT_ROSTER_ACTIVE(new ConstantBoolean(true, "Set to false to disable the roster service")),
    ADMIN_USERNAMES(new ConstantStr("", "List of JIDs with admin privs")),
    XMPP_FORWARD_ADMINS(new ConstantStr(null, "List of JIDs to forward admin XMPP messages to")),
    
    // Ad-Hoc Commands
    XMPP_COMMAND_LIMIT(new ConstantInt(100, "Limit of simultaneous ad-hoc commands in process by a single user")),
    XMPP_COMMAND_TIMEOUT_MS(new ConstantInt((int)(Constants.MILLIS_PER_MINUTE), "Timeout for ad-hoc commands")),
       
    // Auth 
    XMPP_AUTH_ANONYMOUS(new ConstantBoolean(false, "Allow Anonymous XMPP Logins")),
    XMPP_AUTH_RETRIES(new ConstantInt(3, "Number of client retries before closing the connection")),
    SASL_MECHS(new ConstantStr(null, "Comma-separated list of available SASL mechanisms")),
    XMPP_AUTHPROVIDER_K5LOGIN_FILENAME(new ConstantStr("/home/{0}/.k5login", "Location .k5login file for UnixK5LoginProvider")),
    XMPP_AUTHPROVIDER_CLASSLIST(new ConstantStr(null, "List of AbstractAuthorizationProvider subclasses to use to handle XMPP Auth requests")),
    
    // Socket Config
    XMPP_SOCKET_BLOCKING(new ConstantBoolean(false, "enable/disable NIO for testing")),
    XMPP_SOCKET_SSL_ALGORITHM(new ConstantStr("TLS", "Algorithm for SSL")),
    XMPP_SOCKET_SSL_STORETYPE(new ConstantStr("jks", "SSL StoreType")),
    XMPP_SOCKET_SSL_KEYSTORE(new LCStr(LC.mailboxd_keystore)),
    XMPP_SOCKET_SSL_KEYPASS(new LCStr(LC.mailboxd_keystore_password)),
    XMPP_SOCKET_SSL_TRUSTSTORE(getTruststoreLocation()),
    XMPP_SOCKET_SSL_TRUSTPASS(new LCStr(LC.mailboxd_truststore_password)),
    XMPP_SOCKET_SSL_ALLOW_UNTRUSTED_CERTS(new LCBoolean(LC.ssl_allow_untrusted_certs)),

    // Client Settings
    XMPP_CLIENT_TLS_POLICY(new ConstantStr(LC.debug_xmpp_disable_client_tls.booleanValue() ? "disabled" : "optional", "TLS is optional|disabled|required for XMPP C2S")), 
    XMPP_CLIENT_COMPRESSION_POLICY(new LCStr(LC.xmpp_client_compression_policy)),
    XMPP_CLIENT_VALIDATE_HOST(new ConstantBoolean(false, "Validate the xmpp stream's to header in messages from the client")),
    XMPP_CLIENT_IDLE(new LCInt(LC.xmpp_client_idle_timeout)),
    
    // S2S
    XMPP_SERVER_CERTIFICATE_VERIFY(new LCBoolean(LC.xmpp_server_certificate_verify)),
    XMPP_SERVER_CERTIFICATE_VERIFY_CHAIN(new LCBoolean(LC.xmpp_server_certificate_verify_chain)),
    XMPP_SERVER_CERTIFICATE_VERIFY_ROOT(new LCBoolean(LC.xmpp_server_certificate_verify_root)),
    XMPP_SERVER_CERTIFICATE_VERIFY_VALIDITY(new LCBoolean(LC.xmpp_server_certificate_verify_validity)),
    XMPP_SERVER_CERTIFICATE_ACCEPT_SELFSIGNED(new LCBoolean(LC.xmpp_server_certificate_accept_selfsigned)),
    XMPP_SERVER_TLS_ENABLED(new LCBoolean(LC.xmpp_server_tls_enabled)),
    XMPP_SERVER_DIALBACK_ENABLED(new LCBoolean(LC.xmpp_server_dialback_enabled)),
    XMPP_SERVER_SESSION_ALLOWMULTIPLE(new LCBoolean(LC.xmpp_server_session_allowmultiple)),
    XMPP_SERVER_SESSION_IDLE(new LCInt(LC.xmpp_server_session_idle)),
    XMPP_SERVER_SESSION_IDLE_CHECK_TIME(new LCInt(LC.xmpp_server_session_idle_check_time)),
    XMPP_SERVER_PROCESSING_CORE_THREADS(new LCInt(LC.xmpp_server_processing_core_threads)),
    XMPP_SERVER_PROCESSING_MAX_THREADS(new LCInt(LC.xmpp_server_processing_max_threads)),
    XMPP_SERVER_PROCESSING_QUEUE(new LCInt(LC.xmpp_server_processing_queue)),
    XMPP_SERVER_OUTGOING_MAX_THREADS(new LCInt(LC.xmpp_server_outgoing_max_threads)),
    XMPP_SERVER_OUTGOING_QUEUE(new LCInt(LC.xmpp_server_outgoing_queue)),
    XMPP_SERVER_READ_TIMEOUT(new LCInt(LC.xmpp_server_read_timeout)),
    XMPP_SERVER_SOCKET_REMOTEPORT(new LCInt(LC.xmpp_server_socket_remoteport)),
    XMPP_SERVER_COMPRESSION_POLICY(new LCStr(LC.xmpp_server_compression_policy)),
    XMPP_SERVER_PERMISSION(new ConstantStr("blacklist", "Use blacklist or whitelist mode for S2S permissions")),
    
    // 5269 S2S Listener 
    XMPP_SERVER_SOCKET_ACTIVE(new ConstantBoolean(true, "Enable the XMPP Server-Server Socket (and S2S in general)")),
    XMPP_SERVER_SOCKET_ADDRESS(new ConstantStr(null, "Bind address for XMPP S2S listener")),
    XMPP_SERVER_SOCKET_PORT(new ConstantInt(5269, "Port for XMPP S2S listener")),
    
    // 5222 default C2S port
    XMPP_SOCKET_PLAIN_ACTIVE(new ConstantBoolean(true, "Enable the plain (5222) XMPP Client Listener")),
    XMPP_SOCKET_PLAIN_ADDRESS(new ConstantStr(null, "Bind address for plain (5222) XMPP Client Listener")),
    XMPP_SOCKET_PLAIN_PORT(new ConstantInt(5222, "Port for plain XMPP Client listener")),
    
    // 5223 old-style XMPP over SSL
    XMPP_SOCKET_SSL_ACTIVE(new ConstantBoolean(true, "Enable the old style SSL (5223) XMPP Client Listener")),
    XMPP_SOCKET_SSL_ADDRESS(new ConstantStr(null, "Bind address for SSL (5223) XMPP Client Listener")), 
    XMPP_SOCKET_SSL_PORT(new ConstantInt(5223, "Port for SSL XMPP Client listener")),

    // 7335 Cloud Routing
    XMPP_CLOUDROUTING_ACTIVE(new ConstantBoolean(true, "Enable intra-cloud XMPP routing for multiserver Zimbra installs")),
    XMPP_CLOUDROUTING_ADDRESS(new ConstantStr(null, "Bind address for intra-cloud XMPP routing listener")),
    XMPP_CLOUDROUTING_PORT(new ConstantInt(7335, "Port for intra-cloud XMPP routing listener")),
    XMPP_CLOUDROUTING_SSL(new ConstantBoolean(true, "Use SSL for intra-cloud XMPP routing")),
    XMPP_CLOUDROUTING_TIMEOUT(new LCInt(LC.xmpp_cloudrouting_idle_timeout)),
    
    // Offline Messages
    XMPP_OFFLINE_TYPE(new LCStr(LC.xmpp_offline_type)),
    XMPP_OFFLINE_QUOTA(new LCInt(LC.xmpp_offline_quota)),
    
    // External Components
    XMPP_COMPONENT_SOCKET_ACTIVE(new ConstantBoolean(true, "Enable the XMPP external component listener")),
    XMPP_COMPONENT_ADDRESS(new ConstantStr(null, "Bind address for XMPP external component listener")),
    XMPP_COMPONENT_SOCKET_PORT(new ConstantInt(10015, "Port for XMPP external component listener")),
    XMPP_COMPONENT_PERMISSION_POLICY(new ConstantStr("blacklist", "XMPP External Component Policy: blacklist|whitelist")),
    XMPP_COMPONENT_DEFAULT_SECRET(new ConstantStr("changeme", "Default secret for External XMPP Components")), // FIXME
    
    // Multi-User Chat
    XMPP_MUC_ENABLED(new LCBoolean(LC.xmpp_muc_enabled)),
    XMPP_MUC_SERVICE_NAME(new LCStr(LC.xmpp_muc_service_name)),
    XMPP_MUC_SYSADMIN_JID(new LCStr(LC.xmpp_muc_sysadmin_jid_list)),// list of JIDs that can sysadmin.  FIXME
    XMPP_MUC_DISCOVER_LOCKED(new LCBoolean(LC.xmpp_muc_discover_locked)),
    XMPP_MUC_RESTRCIT_ROOM_CREATION(new LCBoolean(LC.xmpp_muc_restrict_room_creation)),
    XMPP_MUC_CREATE_JID(new LCStr(LC.xmpp_muc_room_create_jid_list)),
    XMPP_MUC_TASKS_IDLE_USER_SWEEP(new LCInt(LC.xmpp_muc_idle_user_sweep_ms)),
    XMPP_MUC_TASKS_IDLE_USER_TIMEOUT(new LCInt(LC.xmpp_muc_idle_user_timeout_ms)),
    XMPP_MUC_TASKS_LOG_TIMEOUT(new LCInt(LC.xmpp_muc_log_sweep_time_ms)),
    XMPP_MUC_TASKS_LOG_BATCHSIZE(new LCInt(LC.xmpp_muc_log_batch_size)),
    XMPP_MUC_UNLOAD_EMPTY_HOURS(new LCInt(LC.xmpp_muc_unload_empty_hours)),
    XMPP_MUC_HISTORY_TYPE(new LCInt(LC.xmpp_muc_default_history_type)),
    XMPP_MUC_HISTORY_MAXNUMBER(new LCInt(LC.xmpp_muc_history_number)),
    
    // File Transfer Proxy
    XMPP_PROXY_ENABLED(new ConstantBoolean(true, "XMPP Proxy service enabled")),
    XMPP_PROXY_SERVICE_NAME(new ConstantStr("proxy", "XMPP Service name for proxy service")),
    XMPP_PROXY_PORT(new ConstantInt(7777, "XMPP Proxy Service port number")),
    XMPP_PROXY_EXTERNALIP(new ConstantStr(null, "Bind address for XMPP proxy service")),
    XMPP_PROXY_TRANSFER_REQUIRED(new ConstantBoolean(true, "true if the proxy transfer should be matched to an existing file transfer in the system")),
    
    // Audit Manager
    XMPP_AUDIT_ACTIVE(new ConstantBoolean(false, "Enable XMPP auditing")),
    XMPP_AUDIT_MESSAGE(new ConstantBoolean(false, "Audit XMPP Message stanzas (if XMPP Auditing is active)")),
    XMPP_AUDIT_PRESENCE(new ConstantBoolean(false, "Audit XMPP Presence stanzas (if XMPP Auditing is active)")),
    XMPP_AUDIT_IQ(new ConstantBoolean(false, "Audit XMPP IQ stanzas (if XMPP Auditing is active)")),
    XMPP_AUDIT_XPATH(new ConstantBoolean(false, "Audit XMPP stanzas based on XPath string(if XMPP Auditing is active)")),
    XMPP_AUDIT_XPATH_STRINGS(new ConstantStrList(null, "List of XPath rules to match when auditing")),
    XMPP_AUDIT_TOTALSIZE_MB(new ConstantInt(1000, "Max size in Mbytes that all audit log files may have")),
    XMPP_AUDIT_FILESIZE_MB(new ConstantInt(100, "Max size in Mbytes of a single audit log file")),
    XMPP_AUDIT_DAYS(new ConstantInt(-1, "Max number of days to keep audit information")),
    XMPP_AUDIT_LOG_SWEEP_TIME_MS(new ConstantInt((int)(2*Constants.MILLIS_PER_MINUTE), "How frequently to flush audit logs to disk")),
    XMPP_AUDIT_LOGDIR(new ConstantStr(LC.zimbra_log_directory.value()+File.separatorChar+"xmppAudit", "directory to store audit logs")),
    XMPP_AUDIT_IGNORE(new ConstantStr(null, "Comma-separted list of JIDs to ignore for XMPP auditing")),
    ;

    IMConfig(IMConfigProperty prop) {
        mProp = prop;
    }
    
    private IMConfigProperty mProp;
    
    public String[] getStrings() { return mProp.getStrings(); }
    public String getDescription() { return mProp.getDescription(); }
    public String getString() { return mProp.getString(); }
    public boolean getBoolean() { return mProp.getBoolean(); }
    public int getInt() { return mProp.getInt(); }
    
    public static IMConfigProperty getTruststoreLocation() {
        String trustStoreLocation = System.getProperty("javax.net.ssl.trustStore", null);
        if (trustStoreLocation == null) {
            trustStoreLocation = LC.zimbra_java_home.value() + File.separator + "lib" + File.separator + "security" + File.separator + "cacerts"; 
            if (!new File(trustStoreLocation).exists()) {
                trustStoreLocation = LC.zimbra_java_home.value() + File.separator + "jre" + File.separator + "lib" + File.separator + "security" + File.separator + "cacerts"; 
            }
        }
        return new ConstantStr(trustStoreLocation, "Location of the trust store");
    }
    
    public static String getHomeDirectory() { return IMConfig.HOME_DIRECTORY.getString(); }
    
    public static Locale getLocale() { return Locale.getDefault(); }
    
    private static DateFormat dateTimeFormat = null;
    
    /**
     * Formats a Date object to return a date and time using the global locale.
     *
     * @param date the Date to format.
     * @return a String representing the date and time.
     */
    public static String formatDateTime(Date date) {
        if (dateTimeFormat == null) {
            dateTimeFormat = DateFormat.getDateTimeInstance(DateFormat.MEDIUM,
                                                            DateFormat.MEDIUM, getLocale());
            dateTimeFormat.setTimeZone(getTimeZone());
        }
        return dateTimeFormat.format(date);
    }
    
    /**
     * Returns the global TimeZone used by Jive. The default is the VM's
     * default time zone.
     *
     * @return the global time zone used by Jive.
     */
    public static TimeZone getTimeZone() {
//        if (timeZone == null) {
//            if (jiveProperties != null) {
//                String timeZoneID = jiveProperties.get("locale.timeZone");
//                if (timeZoneID == null) {
//                    timeZone = TimeZone.getDefault();
//                }
//                else {
//                    timeZone = TimeZone.getTimeZone(timeZoneID);
//                }
//            }
//            else {
        return TimeZone.getDefault();
//            }
//        }
//        return timeZone;
    }
    
    public static int getCacheSize(String cacheName, int defaultSize) { 
        return getIntProperty("cache." + cacheName + ".size", defaultSize);
    }
    
    public static long getCacheExpirationTime(String cacheName, int defaultTime) { 
        return (long)getIntProperty("cache." + cacheName + ".expirationTime", defaultTime);        
    }
    
    private static int getIntProperty(String key, int defaultValue) {
        String propValue = JiveProperties.getInstance().get(key);
        if (propValue != null) {
            try {
                int intValue = Integer.parseInt(propValue);
                return intValue;
            } catch (NumberFormatException e) {
            }
        }
        return defaultValue;
    }
    
    public static String getStrProperty(String key) {
        return JiveProperties.getInstance().get(key);
    }
}
