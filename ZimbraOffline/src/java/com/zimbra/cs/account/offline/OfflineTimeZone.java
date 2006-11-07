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

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.zimbra.cs.account.Provisioning;
import com.zimbra.cs.account.WellKnownTimeZone;

class OfflineTimeZone extends WellKnownTimeZone {
    private OfflineTimeZone(String name, String id, Map<String, Object> attrs)  { super(name, id, attrs); }

    private static void register(Map<String,WellKnownTimeZone> tzmap, String name, String stdStart, String stdOffset, String stdRule,
                                 String dayStart, String dayOffset, String dayRule) {
        Map<String,Object> attrs = new HashMap<String,Object>(5);
        attrs.put(Provisioning.A_zimbraId, UUID.randomUUID().toString());
        attrs.put(Provisioning.A_cn, name);
        attrs.put(Provisioning.A_zimbraTimeZoneStandardDtStart, stdStart);
        attrs.put(Provisioning.A_zimbraTimeZoneStandardOffset, stdOffset);
        if (stdRule != null)
            attrs.put(Provisioning.A_zimbraTimeZoneStandardRRule, stdRule);
        attrs.put(Provisioning.A_zimbraTimeZoneDaylightDtStart, dayStart);
        attrs.put(Provisioning.A_zimbraTimeZoneDaylightOffset, dayOffset);
        if (dayRule != null)
            attrs.put(Provisioning.A_zimbraTimeZoneDaylightRRule, dayRule);
        tzmap.put(name, new OfflineTimeZone(name, (String) attrs.get(Provisioning.A_zimbraId), attrs));
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