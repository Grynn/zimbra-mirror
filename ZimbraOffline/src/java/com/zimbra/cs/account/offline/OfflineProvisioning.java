/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Portions created by Zimbra are Copyright (C) 2006 Zimbra, Inc.
 * All Rights Reserved.
 * 
 * The Original Code is: Zimbra Network
 * 
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.cs.account.offline;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.zimbra.common.localconfig.LC;
import com.zimbra.common.util.Constants;
import com.zimbra.common.util.ZimbraLog;
import com.zimbra.cs.account.*;
import com.zimbra.cs.account.NamedEntry.Visitor;
import com.zimbra.cs.db.DbOfflineDirectory;
import com.zimbra.cs.mailbox.MailboxManager;
import com.zimbra.cs.mailbox.OfflineServiceException;
import com.zimbra.cs.mailbox.calendar.ICalTimeZone;
import com.zimbra.cs.mime.MimeTypeInfo;
import com.zimbra.cs.object.ObjectType;
import com.zimbra.cs.offline.OfflineLog;
import com.zimbra.cs.service.ServiceException;
import com.zimbra.cs.servlet.ZimbraServlet;
import com.zimbra.cs.zclient.ZGetInfoResult;
import com.zimbra.cs.zclient.ZMailbox;
import com.zimbra.cs.zimlet.ZimletHandler;
import com.zimbra.cs.zimlet.ZimletUtil;

public class OfflineProvisioning extends Provisioning {

    public static final String A_offlineDn = "offlineDn";
    public static final String A_offlineRemotePassword = "offlineRemotePassword";
    public static final String A_offlineRemoteServerUri = "offlineRemoteServerUri";


    public enum EntryType {
        ACCOUNT("acct"), COS("cos"), CONFIG("conf"), ZIMLET("zmlt");

        private String mAbbr;
        private EntryType(String abbr)  { mAbbr = abbr; }
        public String toString()        { return mAbbr; }

        public static EntryType typeForEntry(Entry e) {
            if (e instanceof Account)      return ACCOUNT;
            else if (e instanceof Cos)     return COS;
            else if (e instanceof Config)  return CONFIG;
            else if (e instanceof Zimlet)  return ZIMLET;
            else                           return null;
        }
    }


    static final Config sLocalConfig = OfflineLocalConfig.instantiate();
    static class OfflineLocalConfig extends Config {
        private OfflineLocalConfig(Map<String, Object> attrs) {
            super(attrs);
        }
        static OfflineLocalConfig instantiate() {
            try {
                Map<String, Object> attrs = DbOfflineDirectory.readDirectoryEntry(EntryType.CONFIG, A_offlineDn, "config");
                if (attrs == null) {
                    attrs = new HashMap<String, Object>(2);
                    attrs.put(A_cn, "config");
                    attrs.put(A_objectClass, "zimbraGlobalConfig");
                    DbOfflineDirectory.createDirectoryEntry(EntryType.CONFIG, "config", attrs);
                }
                return new OfflineLocalConfig(attrs);
            } catch (ServiceException e) {
                // throw RuntimeException because we're being called at startup...
                throw new RuntimeException("failure instantiating global Config", e);
            }
        }
        @Override
        public String getAttr(String name, boolean applyDefaults) {
            OfflineLog.offline.debug("fetching config attr: " + name);
            return super.getAttr(name, applyDefaults);
        }
    }

    private static final Server sLocalServer = OfflineLocalServer.instantiate();
    static class OfflineLocalServer extends Server {
        private OfflineLocalServer(Map<String, Object> attrs) {
            super((String) attrs.get(A_cn), (String) attrs.get(A_zimbraId), attrs, sLocalConfig.getServerDefaults());
        }
        static OfflineLocalServer instantiate() {
            Map<String, Object> attrs = new HashMap<String, Object>(12);
            attrs.put(A_objectClass, "zimbraServer");
            attrs.put(A_cn, "localhost");
            attrs.put(A_zimbraServiceHostname, "localhost");
            attrs.put(A_zimbraSmtpHostname, "localhost");
            attrs.put(A_zimbraId, UUID.randomUUID().toString());
            attrs.put("zimbraServiceEnabled", "mailbox");
            attrs.put("zimbraServiceInstalled", "mailbox");
            attrs.put(A_zimbraMailPort, "7070");
            attrs.put(A_zimbraAdminPort, "7071");
            attrs.put(A_zimbraMailMode, "http");
            attrs.put(A_zimbraLmtpNumThreads, "1");
            attrs.put(A_zimbraLmtpBindPort, "7025");
            return new OfflineLocalServer(attrs);
        }
        @Override
        public String getAttr(String name, boolean applyDefaults) {
            OfflineLog.offline.debug("fetching server attr: " + name);
            return super.getAttr(name, applyDefaults);
        }
    }

    private static final Cos sDefaultCos = OfflineCos.instantiate();
    static class OfflineCos extends Cos {
        public OfflineCos(String name, String id, Map<String, Object> attrs) {
            super(name, id, attrs);
        }
        static OfflineCos instantiate() {
            try {
                Map<String, Object> attrs = DbOfflineDirectory.readDirectoryEntry(EntryType.COS, A_offlineDn, "default");
                if (attrs == null) {
                    attrs = new HashMap<String, Object>(3);
                    attrs.put(A_cn, "default");
                    attrs.put(A_objectClass, "zimbraCOS");
                    attrs.put(A_zimbraId, UUID.randomUUID().toString());
                    DbOfflineDirectory.createDirectoryEntry(EntryType.COS, "default", attrs);
                }
                return new OfflineCos("default", (String) attrs.get(A_zimbraId), attrs);
            } catch (ServiceException e) {
                // throw RuntimeException because we're being called at startup...
                throw new RuntimeException("failure instantiating default cos", e);
            }
        }
        @Override
        public String getAttr(String name, boolean applyDefaults) {
            OfflineLog.offline.debug("fetching cos attr: " + name);
            return super.getAttr(name, applyDefaults);
        }
    }

    private static final Map<String,Zimlet> sZimlets = OfflineZimlet.instantiateAll();
    static class OfflineZimlet extends Zimlet implements ObjectType {
        OfflineZimlet(String name, String id, Map<String, Object> attrs) {
            super(name, id, attrs);
        }
        static Map<String,Zimlet> instantiateAll() {
            Map<String,Zimlet> zmap = new HashMap<String,Zimlet>();
            try {
                List<String> ids = DbOfflineDirectory.listAllDirectoryEntries(EntryType.ZIMLET);
                for (String id : ids) {
                    Map<String, Object> attrs = DbOfflineDirectory.readDirectoryEntry(EntryType.ZIMLET, A_zimbraId, id);
                    if (attrs == null)
                        continue;
                    String name = (String) attrs.get(A_cn);
                    if (name != null)
                        zmap.put(name.toLowerCase(), new OfflineZimlet(name, id, attrs));
                }
                return zmap;
            } catch (ServiceException e) {
                // throw RuntimeException because we're being called at startup...
                throw new RuntimeException("failure instantiating zimlets", e);
            }
        }
        @Override
        public String getAttr(String name, boolean applyDefaults) {
            OfflineLog.offline.debug("fetching zimlet attr: " + name);
            return super.getAttr(name, applyDefaults);
        }
        public String getType()              { return getAttr(Provisioning.A_cn); }
        public String getDescription()       { return getAttr(Provisioning.A_zimbraZimletDescription); }
        public boolean isIndexingEnabled()   { return getBooleanAttr(Provisioning.A_zimbraZimletIndexingEnabled, false); }
        public String getHandlerClassName()  { return getAttr(Provisioning.A_zimbraZimletHandlerClass); }
        public ZimletHandler getHandler()    { return ZimletUtil.getHandler(getName()); }
        public String getHandlerConfig()     { return getAttr(Provisioning.A_zimbraZimletHandlerConfig); }
        public String getServerIndexRegex()  { return getAttr(Provisioning.A_zimbraZimletServerIndexRegex); }
    }

    private static final List<MimeTypeInfo> sMimeTypes = OfflineMimeType.instantiateAll();
    static class OfflineMimeType implements MimeTypeInfo {
        private String mType, mHandler, mFileExtensions[];
        private boolean mIndexed;
        private OfflineMimeType(String type, String handler, boolean index, String[] fext) {
            mType = type;  mHandler = handler;  mIndexed = index;  mFileExtensions = fext;
        }

        public String getType()              { return mType; }
        public String getExtension()         { return null; }
        public String getHandlerClass()      { return mHandler; }
        public boolean isIndexingEnabled()   { return mIndexed; }
        public String getDescription()       { return null; }
        public String[] getFileExtensions()  { return mFileExtensions; }

        static List<MimeTypeInfo> instantiateAll() {
            // just hardcode 'em for now...
            List<MimeTypeInfo> infos = new ArrayList<MimeTypeInfo>();
            infos.add(new OfflineMimeType("text/plain",     "TextPlainHandler",     true, new String[] { "txt", "text" } ));
            infos.add(new OfflineMimeType("text/html",      "TextHtmlHandler",      true, new String[] { "htm", "html" } ));
            infos.add(new OfflineMimeType("text/calendar",  "TextCalendarHandler",  true, new String[] { "ics", "vcs"} ));
            infos.add(new OfflineMimeType("message/rfc822", "MessageRFC822Handler", true, new String[] { } ));
            infos.add(new OfflineMimeType("text/enriched",  "TextEnrichedHandler",  true, new String[] { "txe" } ));
            infos.add(new OfflineMimeType("all",            "UnknownTypeHandler",   true, new String[] { } ));
            return infos;
        }
    }

    private static final Map<String,WellKnownTimeZone> sTimeZones = OfflineTimeZone.instantiateAll();
    static class OfflineTimeZone extends WellKnownTimeZone {
        private OfflineTimeZone(String name, String id, Map<String, Object> attrs)  { super(name, id, attrs); }

        private static void register(Map<String,WellKnownTimeZone> tzmap, String name, String stdStart, String stdOffset, String stdRule,
                                     String dayStart, String dayOffset, String dayRule) {
            Map<String,Object> attrs = new HashMap<String,Object>(5);
            attrs.put(A_zimbraId, UUID.randomUUID().toString());
            attrs.put(A_cn, name);
            attrs.put(A_zimbraTimeZoneStandardDtStart, stdStart);
            attrs.put(A_zimbraTimeZoneStandardOffset, stdOffset);
            if (stdRule != null)
                attrs.put(A_zimbraTimeZoneStandardRRule, stdRule);
            attrs.put(A_zimbraTimeZoneDaylightDtStart, dayStart);
            attrs.put(A_zimbraTimeZoneDaylightOffset, dayOffset);
            if (dayRule != null)
                attrs.put(A_zimbraTimeZoneDaylightRRule, dayRule);
            tzmap.put(name, new OfflineTimeZone(name, (String) attrs.get(A_zimbraId), attrs));
        }
        private static void register(Map<String,WellKnownTimeZone> tzmap, String name, String stdOffset) {
            register(tzmap, name, "16010101T000000", stdOffset, null, "16010101T000000", stdOffset, null);
        }
        private static void registerUS(Map<String,WellKnownTimeZone> tzmap, String name, String stdOffset, String dayOffset) {
            register(tzmap, name, "16010101T020000", stdOffset, "FREQ=YEARLY;WKST=MO;INTERVAL=1;BYMONTH=10;BYDAY=-1SU",
                                  "16010101T020000", dayOffset, "FREQ=YEARLY;WKST=MO;INTERVAL=1;BYMONTH=4;BYDAY=1SU");
        }
        private static void registerEU(Map<String,WellKnownTimeZone> tzmap, String name, String stdOffset, String dayOffset) {
            register(tzmap, name, "16010101T030000", stdOffset, "FREQ=YEARLY;WKST=MO;INTERVAL=1;BYMONTH=10;BYDAY=-1SU",
                                  "16010101T020000", dayOffset, "FREQ=YEARLY;WKST=MO;INTERVAL=1;BYMONTH=3;BYDAY=1SU");
        }
        private static void registerOther(Map<String,WellKnownTimeZone> tzmap, String name, String stdOffset, String stdTime, String stdRule, String dayOffset, String dayTime, String dayRule) {
            register(tzmap, name, "16010101T" + stdTime, stdOffset, "FREQ=YEARLY;WKST=MO;INTERVAL=1;" + stdRule, "16010101T" + dayTime, dayOffset, "FREQ=YEARLY;WKST=MO;INTERVAL=1;" + dayRule);
        }
        static Map<String,WellKnownTimeZone> instantiateAll() {
            // just hardcode 'em for now...
            Map<String,WellKnownTimeZone> tzmap = new HashMap<String,WellKnownTimeZone>(12);
            register(tzmap, "(GMT-12.00) International Date Line West",             "-1200");
            register(tzmap, "(GMT-11.00) Midway Island / Samoa",                    "-1100");
            register(tzmap, "(GMT-10.00) Hawaii",                                   "-1000");
            register(tzmap, "(GMT-07.00) Arizona",                                  "-0700");
            register(tzmap, "(GMT-06.00) Central America",                          "-0600");
            register(tzmap, "(GMT-06.00) Saskatchewan",                             "-0600");
            register(tzmap, "(GMT-05.00) Bogota / Lima / Quito",                    "-0500");
            register(tzmap, "(GMT-05.00) Indiana (East)",                           "-0500");
            register(tzmap, "(GMT-04.00) Atlantic Time (Canada)",                   "-0400");
            register(tzmap, "(GMT-04.00) Caracas / La Paz",                         "-0400");
            register(tzmap, "(GMT-03.00) Buenos Aires / Georgetown",                "-0300");
            register(tzmap, "(GMT-01.00) Cape Verde Is.",                           "-0100");
            register(tzmap, "(GMT) Casablanca / Monrovia",                          "+0000");
            register(tzmap, "(GMT+01.00) West Central Africa",                      "+0100");
            register(tzmap, "(GMT+02.00) Harare / Pretoria",                        "+0200");
            register(tzmap, "(GMT+02.00) Jerusalem",                                "+0200");
            register(tzmap, "(GMT+03.00) Kuwait / Riyadh",                          "+0300");
            register(tzmap, "(GMT+03.00) Nairobi",                                  "+0300");
            register(tzmap, "(GMT+04.00) Abu Dhabi / Muscat",                       "+0400");
            register(tzmap, "(GMT+04.30) Kabul",                                    "+0430");
            register(tzmap, "(GMT+05.00) Islamabad / Karachi / Tashkent",           "+0500");
            register(tzmap, "(GMT+05.30) Chennai / Kolkata / Mumbai / New Delhi",   "+0530");
            register(tzmap, "(GMT+05.45) Kathmandu",                                "+0545");
            register(tzmap, "(GMT+06.00) Astana / Dhaka",                           "+0600");
            register(tzmap, "(GMT+06.00) Sri Jayawardenepura",                      "+0600");
            register(tzmap, "(GMT+06.30) Rangoon",                                  "+0630");
            register(tzmap, "(GMT+07.00) Bangkok / Hanoi / Jakarta",                "+0700");
            register(tzmap, "(GMT+08.00) Beijing / Chongqing / Hong Kong / Urumqi", "+0800");
            register(tzmap, "(GMT+08.00) Kuala Lumpur / Singapore",                 "+0800");
            register(tzmap, "(GMT+08.00) Perth",                                    "+0800");
            register(tzmap, "(GMT+08.00) Taipei",                                   "+0800");
            register(tzmap, "(GMT+09.00) Osaka / Sapporo / Tokyo",                  "+0900");
            register(tzmap, "(GMT+09.00) Seoul",                                    "+0900");
            register(tzmap, "(GMT+09.30) Darwin",                                   "+0930");
            register(tzmap, "(GMT+10.00) Brisbane",                                 "+1000");
            register(tzmap, "(GMT+10.00) Guam / Port Moresby",                      "+1000");
            register(tzmap, "(GMT+11.00) Magadan / Solomon Is. / New Caledonia",    "+1100");
            register(tzmap, "(GMT+12.00) Fiji / Kamchatka / Marshall Is.",          "+1200");
            register(tzmap, "(GMT+13.00) Nuku'alofa",                               "+1300");

            registerUS(tzmap, "(GMT-09.00) Alaska",                                "-0900", "-0800");
            registerUS(tzmap, "(GMT-08.00) Pacific Time (US & Canada) / Tijuana",  "-0800", "-0700");
            registerUS(tzmap, "(GMT-07.00) Chihuahua / La Paz / Mazatlan",         "-0700", "-0600");
            registerUS(tzmap, "(GMT-07.00) Mountain Time (US & Canada)",           "-0700", "-0600");
            registerUS(tzmap, "(GMT-06.00) Central Time (US & Canada)",            "-0600", "-0500");
            registerUS(tzmap, "(GMT-06.00) Guadalajara / Mexico City / Monterrey", "-0600", "-0500");
            registerUS(tzmap, "(GMT-05.00) Eastern Time (US & Canada)",            "-0500", "-0400");
            registerUS(tzmap, "(GMT-03.30) Newfoundland",                          "-0330", "-0230");
            registerUS(tzmap, "(GMT-03.00) Greenland",                             "-0300", "-0200");

            registerEU(tzmap, "(GMT-01.00) Azores",                                                "-0100", "+0000");
            registerEU(tzmap, "(GMT+01.00) Amsterdam / Berlin / Bern / Rome / Stockholm / Vienna", "+0100", "+0200");
            registerEU(tzmap, "(GMT+01.00) Belgrade / Bratislava / Budapest / Ljubljana / Prague", "+0100", "+0200");
            registerEU(tzmap, "(GMT+01.00) Brussels / Copenhagen / Madrid / Paris",                "+0100", "+0200");
            registerEU(tzmap, "(GMT+01.00) Sarajevo / Skopje / Warsaw / Zagreb",                   "+0100", "+0200");
            registerEU(tzmap, "(GMT+02.00) Athens / Beirut / Istanbul / Minsk",                    "+0200", "+0300");
            registerEU(tzmap, "(GMT+03.00) Moscow / St. Petersburg / Volgograd",                   "+0300", "+0400");
            registerEU(tzmap, "(GMT+04.00) Baku / Tbilisi / Yerevan",                              "+0400", "+0500");
            registerEU(tzmap, "(GMT+05.00) Ekaterinburg",                                          "+0500", "+0600");
            registerEU(tzmap, "(GMT+06.00) Almaty / Novosibirsk",                                  "+0600", "+0700");
            registerEU(tzmap, "(GMT+07.00) Krasnoyarsk",                                           "+0700", "+0800");
            registerEU(tzmap, "(GMT+08.00) Irkutsk / Ulaan Bataar",                                "+0800", "+0900");
            registerEU(tzmap, "(GMT+09.00) Yakutsk",                                               "+0900", "+1000");
            registerEU(tzmap, "(GMT+10.00) Vladivostok",                                           "+1000", "+1100");

            registerOther(tzmap, "(GMT-04.00) Santiago",                      "-0400", "000000", "BYMONTH=3;BYDAY=2SA",   "-0300", "000000", "BYMONTH=10;BYDAY=2SA");
            registerOther(tzmap, "(GMT-03.00) Brasilia",                      "-0300", "020000", "BYMONTH=2;BYDAY=2SU",   "-0200", "020000", "BYMONTH=10;BYDAY=3SU");
            registerOther(tzmap, "(GMT-02.00) Mid-Atlantic",                  "-0200", "020000", "BYMONTH=9;BYDAY=-1SU",  "-0100", "020000", "BYMONTH=3;BYDAY=-1SU");
            registerOther(tzmap, "(GMT+02.00) Bucharest",                     "+0200", "010000", "BYMONTH=10;BYDAY=-1SU", "+0300", "000000", "BYMONTH=3;BYDAY=-1SU");
            registerOther(tzmap, "(GMT+02.00) Cairo",                         "+0200", "020000", "BYMONTH=9;BYDAY=-1WE",  "+0300", "020000", "BYMONTH=5;BYDAY=1FR");
            registerOther(tzmap, "(GMT+03.00) Baghdad",                       "+0300", "040000", "BYMONTH=10;BYDAY=1SU",  "+0400", "030000", "BYMONTH=4;BYDAY=1SU");
            registerOther(tzmap, "(GMT+03.30) Tehran",                        "+0330", "020000", "BYMONTH=9;BYDAY=4TU",   "+0430", "020000", "BYMONTH=3;BYDAY=1SU");
            registerOther(tzmap, "(GMT+09.30) Adelaide",                      "+0930", "030000", "BYMONTH=3;BYDAY=-1SU",  "+1030", "020000", "BYMONTH=10;BYDAY=-1SU");
            registerOther(tzmap, "(GMT+10.00) Canberra / Melbourne / Sydney", "+1000", "030000", "BYMONTH=3;BYDAY=-1SU",  "+1100", "020000", "BYMONTH=10;BYDAY=-1SU");
            registerOther(tzmap, "(GMT+10.00) Hobart",                        "+1000", "030000", "BYMONTH=3;BYDAY=-1SU",  "+1100", "020000", "BYMONTH=10;BYDAY=1SU");
            registerOther(tzmap, "(GMT+12.00) Auckland / Wellington",         "+1200", "020000", "BYMONTH=3;BYDAY=3SU",   "+1300", "020000", "BYMONTH=10;BYDAY=1SU");

            registerOther(tzmap, "(GMT) Greenwich Mean Time - Dublin / Edinburgh / Lisbon / London", "+0000", "020000", "BYMONTH=10;BYDAY=-1SU", "+0000", "010000", "BYMONTH=3;BYDAY=-1SU");
            registerOther(tzmap, "(GMT+02.00) Helsinki / Kyiv / Riga / Sofia / Tallinn / Vilnius",   "+0200", "040000", "BYMONTH=10;BYDAY=-1SU", "+0300", "030000", "BYMONTH=3;BYDAY=-1SU");

            return tzmap;
        }
    }


    private static NamedEntryCache<Account> sAccountCache =
        new NamedEntryCache<Account>(LC.ldap_cache_account_maxsize.intValue(), LC.ldap_cache_account_maxage.intValue() * Constants.MILLIS_PER_MINUTE); 


    @Override
    public void modifyAttrs(Entry e, Map<String, ? extends Object> attrs, boolean checkImmutable) throws ServiceException {
        modifyAttrs(e, attrs, checkImmutable, true);
    }

    @Override
    public void modifyAttrs(Entry e, Map<String, ? extends Object> attrs, boolean checkImmutable, boolean allowCallback) throws ServiceException {
        EntryType etype = EntryType.typeForEntry(e);
        if (etype == null)
            throw OfflineServiceException.UNSUPPORTED("modifyAttrs(" + e.getClass().getSimpleName() + ")");

        HashMap context = new HashMap();
        AttributeManager.getInstance().preModify(attrs, e, context, false, checkImmutable, allowCallback);
        if (etype == EntryType.ACCOUNT || etype == EntryType.ZIMLET || etype == EntryType.COS) {
            DbOfflineDirectory.modifyDirectoryEntry(etype, A_zimbraId, e.getAttr(A_zimbraId), attrs);
        } else if (etype == EntryType.CONFIG) {
            DbOfflineDirectory.modifyDirectoryEntry(etype, A_offlineDn, "config", attrs);
        }
        reload(e);
        AttributeManager.getInstance().postModify(attrs, e, context, false, allowCallback);
    }

    @Override
    public void reload(Entry e) throws ServiceException {
        EntryType etype = EntryType.typeForEntry(e);
        if (etype == null)
            throw OfflineServiceException.UNSUPPORTED("reload(" + e.getClass().getSimpleName() + ")");

        Map<String,Object> attrs = null;
        if (etype == EntryType.ACCOUNT || etype == EntryType.ZIMLET || etype == EntryType.COS) {
            attrs = DbOfflineDirectory.readDirectoryEntry(etype, A_zimbraId, e.getAttr(A_zimbraId));
        } else if (etype == EntryType.CONFIG) {
            attrs = DbOfflineDirectory.readDirectoryEntry(etype, A_offlineDn, "config");
        }
        if (attrs == null)
            throw AccountServiceException.NO_SUCH_ACCOUNT(e.getAttr(A_mail));
        e.setAttrs(attrs);
    }

    @Override
    public ICalTimeZone getTimeZone(Account acct) throws ServiceException {
        return acct.getTimeZone();
    }

    @Override
    public boolean inDistributionList(Account acct, String zimbraId) throws ServiceException {
        throw OfflineServiceException.UNSUPPORTED("inDistributionList");
    }

    @Override
    public Set<String> getDistributionLists(Account acct) throws ServiceException {
        throw OfflineServiceException.UNSUPPORTED("getDistributionLists");
    }

    @Override
    public List<DistributionList> getDistributionLists(Account acct, boolean directOnly, Map<String, String> via) throws ServiceException {
        throw OfflineServiceException.UNSUPPORTED("getDistributionLists");
    }

    @Override
    public List<DistributionList> getDistributionLists(DistributionList list, boolean directOnly, Map<String, String> via) throws ServiceException {
        throw OfflineServiceException.UNSUPPORTED("getDistributionLists");
    }

    @Override
    public boolean healthCheck() {
        try {
            DbOfflineDirectory.readDirectoryEntry(EntryType.CONFIG, A_offlineDn, "config");
            return true;
        } catch (ServiceException e) {
            OfflineLog.offline.info("health check failed", e);
            return false;
        }
    }

    @Override
    public Config getConfig() {
        return sLocalConfig;
    }

    @Override
    public MimeTypeInfo getMimeType(String name) {
        for (MimeTypeInfo mtinfo : sMimeTypes) {
            if (mtinfo.getType().equalsIgnoreCase(name))
                return mtinfo;
        }
        return null;
    }

    @Override
    public MimeTypeInfo getMimeTypeByExtension(String ext) {
        for (MimeTypeInfo mtinfo : sMimeTypes) {
            for (String filext : mtinfo.getFileExtensions())
                if (filext.equalsIgnoreCase(ext))
                    return mtinfo;
        }
        return null;
    }

    @Override
    public List<Zimlet> getObjectTypes() {
        return listAllZimlets();
    }

    @Override
    public Account createAccount(String emailAddress, String password, Map<String, Object> attrs) throws ServiceException {
        if (attrs == null || !(attrs.get(A_offlineRemoteServerUri) instanceof String))
            throw ServiceException.FAILURE("need single offlineRemoteServerUri when creating account: " + emailAddress, null);
        String uri = (String) attrs.get(A_offlineRemoteServerUri);

        String parts[] = emailAddress.split("@");
        if (parts.length != 2)
            throw ServiceException.INVALID_REQUEST("must be valid email address: " + emailAddress, null);
        String uid = parts[0];

        ZMailbox.Options options = new ZMailbox.Options(emailAddress, AccountBy.name, password, uri + ZimbraServlet.USER_SERVICE_URI);
        options.setNoSession(true);
        ZGetInfoResult zgi = ZMailbox.getMailbox(options).getAccountInfo(false);

        for (Map.Entry<String,List<String>> zattr : zgi.getAttrs().entrySet())
            for (String value : zattr.getValue())
                addToMap(attrs, zattr.getKey(), value);
        for (Map.Entry<String,List<String>> zpref : zgi.getPrefs().entrySet())
            for (String value : zpref.getValue())
                addToMap(attrs, zpref.getKey(), value);
        attrs.put(A_zimbraId, zgi.getId());
        attrs.put(A_mail, emailAddress);
        attrs.put(A_uid, uid);
        attrs.put(A_objectClass, new String[] { "organizationalPerson", "zimbraAccount" } );
        attrs.put(A_zimbraMailHost, "localhost");
        attrs.put(A_offlineRemotePassword, password);
        if (!(attrs.get(A_cn) instanceof String))
            attrs.put(A_cn, attrs.get(A_displayName) instanceof String ? (String) attrs.get(A_displayName) : uid);
        if (!(attrs.get(A_sn) instanceof String))
            attrs.put(A_sn, uid);
        if (!(attrs.get(A_zimbraAccountStatus) instanceof String))
            attrs.put(A_zimbraAccountStatus, ACCOUNT_STATUS_ACTIVE);

        attrs.remove(A_zimbraIsAdminAccount);
        attrs.remove(A_zimbraIsDomainAdminAccount);

        DbOfflineDirectory.createDirectoryEntry(EntryType.ACCOUNT, emailAddress, attrs);
        Account acct = new Account(emailAddress, zgi.getId(), attrs, sDefaultCos.getAccountDefaults());
        sAccountCache.put(acct);

        try {
            // fault in the mailbox so it's picked up by the sync loop
            MailboxManager.getInstance().getMailboxByAccount(acct);
        } catch (ServiceException e) {
            OfflineLog.offline.warn("could not create mailbox for account " + emailAddress, e);
        }

        return acct;
    }

    public static void addToMap(Map<String,Object> attrs, String key, String value) {
        Object existing = attrs.get(key);
        if (existing == null) {
            attrs.put(key, value);
        } else if (existing instanceof String) {
            attrs.put(key, new String[] { (String) existing, value } );
        } else {
            String[] before = (String[]) existing, after = new String[before.length+1];
            System.arraycopy(before, 0, after, 0, before.length);
            after[after.length-1] = value;
            attrs.put(key, after);
        }
    }

    @Override
    public void deleteAccount(String zimbraId) throws ServiceException {
        DbOfflineDirectory.deleteDirectoryEntry(EntryType.ACCOUNT, zimbraId);
    }

    @Override
    public void renameAccount(String zimbraId, String newName) throws ServiceException {
        throw OfflineServiceException.UNSUPPORTED("renameAccount");
    }

    @Override
    public Account get(AccountBy keyType, String key) throws ServiceException {
        Account acct = null;
        Map<String,Object> attrs = null;
        if (keyType == AccountBy.id) {
            if ((acct = sAccountCache.getById(key)) != null)
                return acct;
            attrs = DbOfflineDirectory.readDirectoryEntry(EntryType.ACCOUNT, A_zimbraId, key);
        } else if (keyType == AccountBy.name) {
            if ((acct = sAccountCache.getByName(key)) != null)
                return acct;
            attrs = DbOfflineDirectory.readDirectoryEntry(EntryType.ACCOUNT, A_offlineDn, key);
        } else if (keyType == AccountBy.adminName) {
            if ((acct = sAccountCache.getByName(key)) != null)
                return acct;
            if (key.equals(LC.zimbra_ldap_user.value())) {
                attrs = new HashMap<String,Object>(7);
                attrs.put(A_mail, key);
                attrs.put(A_cn, key);
                attrs.put(A_sn, key);
                attrs.put(A_zimbraId, UUID.randomUUID().toString());
                attrs.put(A_zimbraAccountStatus, ACCOUNT_STATUS_ACTIVE);
                attrs.put(A_offlineRemotePassword, LC.zimbra_ldap_password.value());
                attrs.put(A_zimbraIsAdminAccount, TRUE);
            }
        }
        if (attrs == null)
            return null;
        acct = new Account((String) attrs.get(A_mail), (String) attrs.get(A_zimbraId), attrs, sDefaultCos.getAccountDefaults());
        sAccountCache.put(acct);
        return acct;
    }

    @Override
    public List<NamedEntry> searchAccounts(String query, String[] returnAttrs, String sortAttr, boolean sortAscending, int flags) throws ServiceException {
        throw new UnsupportedOperationException();
    }

    public List<Account> getAllAccounts() throws ServiceException {
        List<Account> accts = new ArrayList<Account>();
        for (String zimbraId : DbOfflineDirectory.listAllDirectoryEntries(EntryType.ACCOUNT)) {
            Account acct = get(AccountBy.id, zimbraId);
            if (acct != null)
                accts.add(acct);
        }
        return accts;
    }

    @Override
    public List<Account> getAllAdminAccounts() throws ServiceException {
        List<Account> admins = new ArrayList<Account>(1);
        Account acct = get(AccountBy.adminName, LC.zimbra_ldap_user.value());
        if (acct != null)
            admins.add(acct);
        return admins;
    }

    @Override
    public void setCOS(Account acct, Cos cos) throws ServiceException {
        if (cos != sDefaultCos)
            throw OfflineServiceException.UNSUPPORTED("setCOS");
    }

    @Override
    public void modifyAccountStatus(Account acct, String newStatus) throws ServiceException {
        throw OfflineServiceException.UNSUPPORTED("modifyAccountStatus");
    }

    @Override
    public void authAccount(Account acct, String password, String proto) throws ServiceException {
        try {
            if (password == null || password.equals(""))
                throw AccountServiceException.AUTH_FAILED(acct.getName() + " (empty password)");
            if (!password.equals(acct.getAttr(A_offlineRemotePassword)))
                throw AccountServiceException.AUTH_FAILED(acct.getName());
            ZimbraLog.security.info(ZimbraLog.encodeAttrs(new String[] {"cmd", "Auth", "account", acct.getName(), "protocol", proto}));
        } catch (ServiceException e) {
            ZimbraLog.security.warn(ZimbraLog.encodeAttrs(new String[] {"cmd", "Auth", "account", acct.getName(), "protocol", proto, "error", e.getMessage()}));             
            throw e;
        }
    }

    @Override
    public void preAuthAccount(Account acct, String accountName, String accountBy, long timestamp, long expires, String preAuth) throws ServiceException {
        throw OfflineServiceException.UNSUPPORTED("preAuthAccount");
    }

    @Override
    public void changePassword(Account acct, String currentPassword, String newPassword) throws ServiceException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setPassword(Account acct, String newPassword) throws ServiceException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addAlias(Account acct, String alias) throws ServiceException {
        throw OfflineServiceException.UNSUPPORTED("addAlias");
    }

    @Override
    public void removeAlias(Account acct, String alias) throws ServiceException {
        throw OfflineServiceException.UNSUPPORTED("removeAlias");
    }

    @Override
    public Domain createDomain(String name, Map<String, Object> attrs) throws ServiceException {
        throw OfflineServiceException.UNSUPPORTED("createDomain");
    }

    @Override
    public Domain get(DomainBy keyType, String key) {
        return null;
    }

    @Override
    public List<Domain> getAllDomains() {
        return Collections.emptyList();
    }

    @Override
    public void deleteDomain(String zimbraId) throws ServiceException {
        throw OfflineServiceException.UNSUPPORTED("deleteDomain");
    }

    @Override
    public Cos createCos(String name, Map<String, Object> attrs) throws ServiceException {
        throw OfflineServiceException.UNSUPPORTED("createCos");
    }

    @Override
    public void renameCos(String zimbraId, String newName) throws ServiceException {
        throw OfflineServiceException.UNSUPPORTED("renameCos");
    }

    @Override
    public Cos get(CosBy keyType, String key) throws ServiceException {
        if (keyType == CosBy.id)
            return sDefaultCos.getId().equalsIgnoreCase(key) ? sDefaultCos : null;
        else if (keyType == CosBy.name)
            return sDefaultCos.getName().equalsIgnoreCase(key) ? sDefaultCos : null;
        else
            throw ServiceException.FAILURE("unsupported CosBy value: " + keyType, null);
    }

    @Override
    public List<Cos> getAllCos() {
        List<Cos> coses = new ArrayList<Cos>(1);
        coses.add(sDefaultCos);
        return coses;
    }

    @Override
    public void deleteCos(String zimbraId) throws ServiceException {
        throw OfflineServiceException.UNSUPPORTED("deleteCos");
    }

    @Override
    public Server getLocalServer() {
        return sLocalServer;
    }

    @Override
    public Server createServer(String name, Map<String, Object> attrs) throws ServiceException {
        throw OfflineServiceException.UNSUPPORTED("createServer");
    }

    @Override
    public Server get(ServerBy keyType, String key) throws ServiceException {
        if (keyType == ServerBy.id)
            return sLocalServer.getId().equalsIgnoreCase(key) ? sLocalServer : null;
        else if (keyType == ServerBy.name)
            return sLocalServer.getName().equalsIgnoreCase(key) ? sLocalServer : null;
        else if (keyType == ServerBy.serviceHostname)
            return sLocalServer.getAttr(A_zimbraServiceHostname, "localhost").equalsIgnoreCase(key) ? sLocalServer : null;
        else
            throw ServiceException.FAILURE("unsupported ServerBy value: " + keyType, null);
    }

    @Override
    public List<Server> getAllServers() {
        List<Server> servers = new ArrayList<Server>(1);
        servers.add(sLocalServer);
        return servers;
    }

    @Override
    public List<Server> getAllServers(String service) {
        if ("mailbox".equalsIgnoreCase(service))
            return getAllServers();
        return Collections.emptyList();
    }

    @Override
    public void deleteServer(String zimbraId) throws ServiceException {
        throw OfflineServiceException.UNSUPPORTED("deleteServer");
    }

    @Override
    public List<WellKnownTimeZone> getAllTimeZones() {
        return new ArrayList<WellKnownTimeZone>(sTimeZones.values());
    }

    @Override
    public WellKnownTimeZone getTimeZoneById(String tzId) {
        return sTimeZones.get(tzId);
    }

    @Override
    public DistributionList createDistributionList(String listAddress, Map<String, Object> listAttrs) throws ServiceException {
        throw OfflineServiceException.UNSUPPORTED("createDistributionList");
    }

    @Override
    public DistributionList get(DistributionListBy keyType, String key) throws ServiceException {
        throw OfflineServiceException.UNSUPPORTED("get(DistributionList)");
    }

    @Override
    public void deleteDistributionList(String zimbraId) throws ServiceException {
        throw OfflineServiceException.UNSUPPORTED("deleteDistributionList");
    }

    @Override
    public void addAlias(DistributionList dl, String alias) throws ServiceException {
        throw OfflineServiceException.UNSUPPORTED("addAlias");
    }

    @Override
    public void removeAlias(DistributionList dl, String alias) throws ServiceException {
        throw OfflineServiceException.UNSUPPORTED("removeAlias");
    }

    @Override
    public void renameDistributionList(String zimbraId, String newName) throws ServiceException {
        throw OfflineServiceException.UNSUPPORTED("renameDistributionList");
    }

    @Override
    public Zimlet getZimlet(String name) {
        return sZimlets.get(name.toLowerCase());
    }

    @Override
    public List<Zimlet> listAllZimlets() {
        // FIXME: not thread-safe wrt zimlet deletes/creates
        return new ArrayList<Zimlet>(sZimlets.values());
    }

    @Override
    public Zimlet createZimlet(String name, Map<String, Object> attrs) throws ServiceException {
        name = name.toLowerCase();

        HashMap attrManagerContext = new HashMap();
        AttributeManager.getInstance().preModify(attrs, null, attrManagerContext, true, true);
        if (!(attrs.get(A_zimbraId) instanceof String))
            attrs.put(A_zimbraId, UUID.randomUUID().toString());
        attrs.put(A_cn, name);
        attrs.put(A_objectClass, "zimbraZimletEntry");
        attrs.put(A_zimbraZimletEnabled, FALSE);
        attrs.put(A_zimbraZimletIndexingEnabled, attrs.containsKey(A_zimbraZimletKeyword) ? TRUE : FALSE);

        DbOfflineDirectory.createDirectoryEntry(EntryType.ZIMLET, name, attrs);
        Zimlet zimlet = new OfflineZimlet(name, (String) attrs.get(A_zimbraId), attrs);
        sZimlets.put(name, zimlet);
        AttributeManager.getInstance().postModify(attrs, zimlet, attrManagerContext, true);
        return zimlet;
    }

    @Override
    public void deleteZimlet(String name) throws ServiceException {
        name = name.toLowerCase();

        Zimlet zimlet = sZimlets.get(name);
        if (zimlet == null)
            return;
        DbOfflineDirectory.deleteDirectoryEntry(EntryType.ZIMLET, zimlet.getId());
        sZimlets.remove(name);
    }

    @Override
    public CalendarResource createCalendarResource(String emailAddress, String password, Map<String, Object> attrs) throws ServiceException {
        throw OfflineServiceException.UNSUPPORTED("createCalendarResource");
    }

    @Override
    public void deleteCalendarResource(String zimbraId) throws ServiceException {
        throw OfflineServiceException.UNSUPPORTED("deleteCalendarResource");
    }

    @Override
    public void renameCalendarResource(String zimbraId, String newName) throws ServiceException {
        throw OfflineServiceException.UNSUPPORTED("renamerCalendarResource");
    }

    @Override
    public CalendarResource get(CalendarResourceBy keyType, String key) throws ServiceException {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<NamedEntry> searchCalendarResources(EntrySearchFilter filter, String[] returnAttrs, String sortAttr, boolean sortAscending) throws ServiceException {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Account> getAllAccounts(Domain d) throws ServiceException {
        if (d == null || d.getAttr(A_zimbraDomainName) == null)
            throw ServiceException.INVALID_REQUEST("null Domain or missing domain name", null);
        List<Account> accts = new ArrayList<Account>();

        List<String> ids = DbOfflineDirectory.searchDirectoryEntries(EntryType.ACCOUNT, A_offlineDn, '%' + d.getAttr(A_zimbraDomainName));
        for (String id : ids) {
            Account acct = get(AccountBy.id, id);
            if (acct != null)
                accts.add(acct);
        }
        return accts;
    }

    @Override
    public void getAllAccounts(Domain d, Visitor visitor) throws ServiceException {
        for (Account acct : getAllAccounts(d))
            visitor.visit(acct);
    }

    @Override
    public List getAllCalendarResources(Domain d) throws ServiceException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void getAllCalendarResources(Domain d, Visitor visitor) throws ServiceException {
        throw new UnsupportedOperationException();
    }

    @Override
    public List getAllDistributionLists(Domain d) throws ServiceException {
        throw OfflineServiceException.UNSUPPORTED("getAllDistributionLists");
    }

    @Override
    public List<NamedEntry> searchAccounts(Domain d, String query, String[] returnAttrs, String sortAttr, boolean sortAscending, int flags) throws ServiceException {
        throw new UnsupportedOperationException();
    }

    @Override
    public SearchGalResult searchGal(Domain d, String query, GAL_SEARCH_TYPE type, String token) throws ServiceException {
        throw new UnsupportedOperationException();
    }

    @Override
    public SearchGalResult autoCompleteGal(Domain d, String query, GAL_SEARCH_TYPE type, int limit) throws ServiceException {
        throw new UnsupportedOperationException();
    }

    @Override
    public List searchCalendarResources(Domain d, EntrySearchFilter filter, String[] returnAttrs, String sortAttr, boolean sortAscending) throws ServiceException {
        return Collections.emptyList();
    }

    @Override
    public void addMembers(DistributionList list, String[] members) throws ServiceException {
        throw OfflineServiceException.UNSUPPORTED("addMembers");
    }

    @Override
    public void removeMembers(DistributionList list, String[] member) throws ServiceException {
        throw OfflineServiceException.UNSUPPORTED("removeMembers");
    }

    @Override
    public Identity createIdentity(Account account, String identityName, Map<String, Object> attrs) throws ServiceException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void deleteIdentity(Account account, String identityName) throws ServiceException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public List<Identity> getAllIdentities(Account account) throws ServiceException {
        return Collections.emptyList();
    }

    @Override
    public void modifyIdentity(Account account, String identityName, Map<String, Object> attrs) throws ServiceException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public DataSource createDataSource(Account account, DataSource.Type type, String dsName, Map<String, Object> attrs) throws ServiceException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void deleteDataSource(Account account, String dataSourceId) throws ServiceException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public List<DataSource> getAllDataSources(Account account) throws ServiceException {
        return Collections.emptyList();
    }

    @Override
    public void modifyDataSource(Account account, String dataSourceId, Map<String, Object> attrs) throws ServiceException {
        // TODO Auto-generated method stub
        
    }
}
