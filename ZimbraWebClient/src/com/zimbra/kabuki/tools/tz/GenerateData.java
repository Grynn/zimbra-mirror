/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2007 Zimbra, Inc.
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
package com.zimbra.kabuki.tools.tz;

import java.io.*;
import java.util.*;
import java.util.regex.*;

import com.zimbra.common.calendar.TZIDMapper;

public class GenerateData {

    private static Calendar NOW = Calendar.getInstance();

    public static void main(String[] argv) throws Exception {
        // values
        File fin = null;
        File fout = null;

        // process arguments
        if (argv.length == 0) {
            argv = new String[] { "-h" };
        }
        for (int i = 0; i < argv.length; i++) {
            String arg = argv[i];
            if (arg.startsWith("-")) {
                if (arg.equals("-i")) {
                    fin = new File(argv[++i]);
                    continue;
                }
                if (arg.equals("-o")) {
                    fout = new File(argv[++i]);
                    continue;
                }
                if (arg.equals("-h")) {
                    // TODO
                    System.exit(1);
                }
                System.err.println("error: unknown option ("+arg+")");
                System.exit(1);
            }
            System.err.println("error: uknown argument ("+arg+")");
            System.exit(1);
        }
        if (fin == null) {
            System.err.println("error: missing input file");
            System.exit(1);
        }

        // generate
        InputStream in = new FileInputStream(fin);
        OutputStream out = fout != null ? new FileOutputStream(fout) : System.out;
        try {
            List<Timezone> timezones = TimezoneParser.parse(in);
            Collections.sort(timezones, new TimezoneComparator());

            PrintWriter writer = new PrintWriter(new OutputStreamWriter(out, "UTF-8"));
            printJsonHeader(writer);
            printJson(writer, timezones);
            printJsonFooter(writer);
            writer.flush();
        }
        finally {
            in.close();
            if (fout != null) {
                out.close();
            }
        }
    }

    public static void printJsonHeader(PrintWriter out) {
        out.println("/** Generated at "+NOW.getTime()+" */");
        out.println("function AjxTimezoneData() {}");
        out.println();

        out.print("AjxTimezoneData.TRANSITION_YEAR = ");
        out.print(NOW.get(Calendar.YEAR));
        out.println(";");
        out.println();
    }

    public static void printJsonFooter(PrintWriter out) {
    }

    public static void printJson(PrintWriter out, List<Timezone> timezones) {
        out.println("AjxTimezoneData.TIMEZONE_RULES = [");
        Iterator<Timezone> iter = timezones.iterator();
        while (iter.hasNext()) {
            Timezone timezone = iter.next();
            printJson(out, timezone);
            if (iter.hasNext()) {
                out.print(',');
            }
            out.println();
        }
        out.println("];");
    }

    public static void printJson(PrintWriter out, Timezone timezone) {
        out.print("\t{ serverId: \"");
        printEscaped(out, timezone.id);
        out.print("\", clientId: \"");
        printEscaped(out, TZIDMapper.toJava(timezone.id));
        out.print("\",");
        if (timezone.daylight == null) {
            out.print(" standard: { offset: ");
            out.print(timezone.standard.offset);
            out.print(" } ");
        }
        else {
            out.println();
            out.print("\t  standard: ");
            printJson(out, timezone.standard);
            out.print(",\n\t  daylight: ");
            printJson(out, timezone.daylight);
            out.println();
            out.print('\t');
        }
        out.print("}");
    }

    public static void printJson(PrintWriter out, Onset onset) {
        out.print("{ offset: ");
        out.print(onset.offset);
        out.print(", mon: ");
        out.print(onset.mon);
        if (onset.mday != -1) {
            out.print(", mday: ");
            out.print(onset.mday);
        }
        else {
            out.print(", week: ");
            out.print(onset.week);
            out.print(", wkday: ");
            out.print(onset.wkday);
        }
        out.print(", hour: ");
        out.print(onset.hour);
        out.print(", min: ");
        out.print(onset.min);
        out.print(", sec: ");
        out.print(onset.sec);
        if (onset.trans != null) {
            out.print(", trans: [ ");
            for (int i = 0; i < onset.trans.length; i++) {
                out.print(onset.trans[i]);
                if (i < onset.trans.length - 1) {
                    out.print(", ");
                }
            }
            out.print(" ]");
        }
        out.print(" }");
    }

    public static void printEscaped(PrintWriter out, String s) {
        int length = s != null ? s.length() : 0;
        for (int i = 0; i < length; i++) {
            char c = s.charAt(i);
            switch (c) {
                case '"': out.print('\\'); break;
                case '\t': out.print("\\t"); continue;
                case '\r': out.print("\\r"); continue;
                case '\n': out.print("\\n"); continue;
            }
            out.print(c);
        }
    }

    public static class TimezoneParser {

        private static Pattern RE_BEGIN_TZ = Pattern.compile("^BEGIN:VTIMEZONE");
        private static Pattern RE_END_TZ = Pattern.compile("^END:VTIMEZONE");
        private static Pattern RE_BEGIN_STANDARD = Pattern.compile("^BEGIN:STANDARD");
        private static Pattern RE_BEGIN_DAYLIGHT = Pattern.compile("^BEGIN:DAYLIGHT");
        private static Pattern RE_TZ_ID = Pattern.compile("^TZID:(.*)");
        private static Pattern RE_TZ_OFFSET_TO = Pattern.compile("^TZOFFSETTO:([-+]?\\d+)");
        private static Pattern RE_DT_START = Pattern.compile(
            "DTSTART:(\\d{4})(\\d{2})(\\d{2})T(\\d{2})(\\d{2})(\\d{2})[Z]?"
        );
        private static Pattern RE_RECUR_RULE = Pattern.compile("^RRULE:(.*)");
        private static Pattern RE_RECUR_RULE_DEF = Pattern.compile(
            "FREQ=YEARLY;WKST=MO;INTERVAL=1;BYMONTH=(\\d+);BYDAY=([-]?\\d+)(.{2})"
        );

        private TimezoneParser() {}

        public static List<Timezone> parse(InputStream is) throws IOException {
            BufferedReader in = new BufferedReader(new InputStreamReader(is,"UTF-8"));

            List<Timezone> timezones = new LinkedList<Timezone>();
            Timezone timezone = null;
            Onset onset = null;

            String line;
            while ((line = in.readLine()) != null) {
                Matcher beginTz = RE_BEGIN_TZ.matcher(line);
                if (beginTz.matches()) {
                    timezone = new Timezone();
                    onset = timezone.standard;
                    continue;
                }
                Matcher tzId = RE_TZ_ID.matcher(line);
                if (tzId.matches()) {
                    timezone.id = tzId.group(1);
                    continue;
                }
                Matcher beginStd = RE_BEGIN_STANDARD.matcher(line);
                if (beginStd.matches()) {
                    onset = timezone.standard;
                    continue;
                }
                Matcher beginDay = RE_BEGIN_DAYLIGHT.matcher(line);
                if (beginDay.matches()) {
                    onset = timezone.daylight = new Onset();
                    continue;
                }
                if (onset != null) {
                    Matcher dtStart = RE_DT_START.matcher(line);
                    if (dtStart.matches()) {
//                        int year = Integer.parseInt(dtStart.group(1));
//                        int mon = Integer.parseInt(dtStart.group(2));
//                        int day = Integer.parseInt(dtStart.group(3));
                        onset.hour = Integer.parseInt(dtStart.group(4));
                        onset.min = Integer.parseInt(dtStart.group(5));
                        onset.sec = Integer.parseInt(dtStart.group(6));
                        boolean inUTC = dtStart.groupCount() > 6;
                        if (inUTC) {
                            System.err.println("error: UTC time not implemented");
                            System.exit(1);
                        }
                    }
                }
                Matcher tzOffTo = RE_TZ_OFFSET_TO.matcher(line);
                if (tzOffTo.matches()) {
                    onset.offset = offset2mins(tzOffTo.group(1));
                    continue;
                }
                Matcher rRule = RE_RECUR_RULE.matcher(line);
                if (rRule.matches()) {
                    String def = rRule.group(1);
                    Matcher rRuleDef = RE_RECUR_RULE_DEF.matcher(def);
                    if (rRuleDef.matches()) {
                        onset.mon = Integer.parseInt(rRuleDef.group(1));
                        onset.week = Integer.parseInt(rRuleDef.group(2));
                        onset.wkday = dayName2dayNum(rRuleDef.group(3));
                        calcTransition(onset);
                    }
                    else {
                        System.err.println("error: unknown recurrence rule format");
                        System.err.println("     > "+def);
                        System.exit(1);
                    }
                    continue;
                }
                Matcher endTz = RE_END_TZ.matcher(line);
                if (endTz.matches()) {
                    timezones.add(timezone);
                    timezone = null;
                    onset = null;
                    continue;
                }
                // ignore all other components & properties
            }

            return timezones;
        }
    }

    private static void calcTransition(Onset onset) {
        Calendar now = (Calendar)NOW.clone();
        onset.trans = new int[] { now.get(Calendar.YEAR), onset.mon, onset.mday };
        if (onset.wkday != 0) {
            // init back to first of transition month
            now.set(Calendar.YEAR, now.get(Calendar.YEAR));
            now.set(Calendar.MONTH, onset.mon - 1);
            now.set(Calendar.DAY_OF_MONTH, 1);

            // set to target day
            int firstWkDay = now.get(Calendar.DAY_OF_WEEK) - 1;
            int delta = onset.wkday - firstWkDay - 1;
            int day = ((7 + delta) % 7) + 1;
            now.set(Calendar.DAY_OF_MONTH, day);

            // adjust week 
            int adjust = 7 * ((onset.week != -1 ? onset.week : 5) - 1);
            now.add(Calendar.DAY_OF_MONTH, adjust);

            // have we gone too far?
            if (now.get(Calendar.MONTH) != onset.mon - 1) {
                now.add(Calendar.DAY_OF_MONTH, -7);
            }

            // save value
            onset.trans[2] = now.get(Calendar.DAY_OF_MONTH);
        }
    }

    private static int offset2mins(String s) {
        int sign = s.startsWith("-") ? -1 : 1;
        s = s.replaceAll("^[-+]", "");
        int hours = Integer.parseInt(s.substring(0, 2), 10);
        int mins = Integer.parseInt(s.substring(2), 10);
        return sign * (hours * 60 + mins);
    }

    private static final String[] DAY_NAMES = { "SU", "MO", "TU", "WE", "TH", "FR", "SA" };
    private static int dayName2dayNum(String s) {
        for (int i = 0; i < DAY_NAMES.length; i++) {
            if (DAY_NAMES[i].equals(s)) return i + 1;
        }
        return -1;
    }

    public static class Timezone {
        private static int COUNT = 0;
        public String id = "Undefined "+String.valueOf(++COUNT);
        public Onset standard = new Onset();
        public Onset daylight = null;
    }

    public static class Onset {
        public int offset = 0;
        public int mon = -1;
        public int mday = -1;
        public int week = 0;
        public int wkday = 0;
        public int hour = 0;
        public int min = 0;
        public int sec = 0;
        public int[] trans = null;
    }

    public static class TimezoneComparator
    implements Comparator {
        public int compare(Object o1, Object o2) {
            Timezone t1 = (Timezone)o1;
            Timezone t2 = (Timezone)o2;
            return t1.standard.offset - t2.standard.offset;
        }
        public boolean equals(Object o) {
            return o == this || o instanceof TimezoneComparator;
        }
    }

}
