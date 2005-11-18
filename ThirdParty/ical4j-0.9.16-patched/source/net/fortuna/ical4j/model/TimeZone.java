/*
 * $Id: TimeZone.java,v 1.3 2005/10/02 06:48:02 fortuna Exp $
 *
 * Created on 13/09/2005
 *
 * Copyright (c) 2005, Ben Fortuna
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  o Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *
 *  o Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *
 *  o Neither the name of Ben Fortuna nor the names of any other contributors
 * may be used to endorse or promote products derived from this software
 * without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.fortuna.ical4j.model;

import java.text.ParseException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.SimpleTimeZone;
import java.util.StringTokenizer;

import net.fortuna.ical4j.model.component.Daylight;
import net.fortuna.ical4j.model.component.Observance;
import net.fortuna.ical4j.model.component.Standard;
import net.fortuna.ical4j.model.component.VTimeZone;
import net.fortuna.ical4j.model.property.DtStart;
import net.fortuna.ical4j.model.property.RRule;
import net.fortuna.ical4j.model.property.TzId;
import net.fortuna.ical4j.model.property.TzOffsetFrom;
import net.fortuna.ical4j.model.property.TzOffsetTo;

/**
 * A Java timezone implementation based on an underlying VTimeZone
 * definition.
 * @author Ben Fortuna
 */
public class TimeZone extends SimpleTimeZone {

    public static class SimpleOnset {
        private int mWeek      = 0;
        private int mDayOfWeek = 0;
        private int mMonth     = 0;
        private int mHour      = 0;
        private int mMinute    = 0;
        private int mSecond    = 0;

        public int getWeek()      { return mWeek; }       // week 1, 2, 3, 4, -1 (last)
        public int getDayOfWeek() { return mDayOfWeek; }  // 1=Sunday, 2=Monday, etc.
        public int getMonth()     { return mMonth; }      // 1=January, 2=February, etc.
        public int getHour()      { return mHour; }       // 0..23
        public int getMinute()    { return mMinute; }     // 0..59
        public int getSecond()    { return mSecond; }     // 0..59

        public SimpleOnset(int week, int dayOfWeek, int month,
                           int hour, int minute, int second) {
            mWeek = week;
            mDayOfWeek = dayOfWeek;
            mMonth = month;
            mHour = hour;
            mMinute = minute;
            mSecond = second;
        }

        public String toString() {
            StringBuffer sb = new StringBuffer();
            sb.append("week=").append(mWeek);
            sb.append(", dayOfWeek=").append(mDayOfWeek);
            sb.append(", month=").append(mMonth);
            sb.append(", hour=").append(mHour);
            sb.append(", minute=").append(mMinute);
            sb.append(", second=").append(mSecond);
            return sb.toString();
        }
    }

    private static final String DEFAULT_DTSTART = "16010101T000000";

    protected boolean mHasDaylight = false;

    protected int    mStandardOffset = 0;
    protected String mDayToStdDtStart = DEFAULT_DTSTART;
    protected String mDayToStdRule = null;

    protected int    mDaylightOffset = 0; 
    protected String mStdToDayDtStart = DEFAULT_DTSTART;
    protected String mStdToDayRule = null;
    protected VTimeZone mVTimeZone;

    private SimpleOnset mStandardOnset;
    private SimpleOnset mDaylightOnset;

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("TZID=").append(getID());
        sb.append("\nSimpleTimeZone: ").append(super.toString());
        sb.append("\nmHasDaylight=").append(mHasDaylight);
        sb.append("\nmStandardOffset=").append(mStandardOffset);
        sb.append(", mDayToStdDtStart=").append(mDayToStdDtStart);
        sb.append(", mDayToStdRule=\"").append(mDayToStdRule).append("\"");
        sb.append("\nmStandardOnset=\"").append(mStandardOnset).append("\"");
        sb.append("\nmDaylightOffset=").append(mDaylightOffset);
        sb.append(", mStdToDayDtStart=").append(mStdToDayDtStart);
        sb.append(", mStdToDayRule=\"").append(mStdToDayRule).append("\"");
        sb.append("\nmDaylightOnset=\"").append(mDaylightOnset).append("\"");
        return sb.toString();
    }

    /**
     * Return the standard offset in milliseconds.
     * local = UTC + offset
     */
    public int getStandardOffset() {
        return mStandardOffset;
    }

    /**
     * Return the onset rule/time for transitioning from daylight to standard
     * time.  Null is returned if DST is not in use.
     */
    public SimpleOnset getStandardOnset() {
        return mStandardOnset;
    }

    /**
     * Return the daylight offset in milliseconds.
     * Value is same as standard offset is DST is not used.
     * local = UTC + offset
     */
    public int getDaylightOffset() {
        return mDaylightOffset;
    }

    /**
     * Return the onset rule/time for transitioning from standard to daylight
     * time.  Null is returned if DST is not in use.
     */
    public SimpleOnset getDaylightOnset() {
        return mDaylightOnset;
    }

    /**
     * 
     * @param tzId       iCal TZID string
     * @param stdOffset  standard time offset from UTC in milliseconds
     * @param stdDtStart iCal datetime string specifying the beginning of the
     *                   period for which stdRRule applies.  The format is
     *                   "YYYYMMDDThhmmss" with 24-hour hour.  In practice,
     *                   the date portion is set to some very early date, like
     *                   "16010101", and only the time portion varies according
     *                   to the rules of the time zone.
     * @param stdRRule   iCal recurrence rule for transition into standard
     *                   time (i.e. transition out of daylight time)
     *                   e.g. "FREQ=YEARLY;WKST=MO;INTERVAL=1;BYMONTH=10;BYDAY=-1SU"
     * @param dayOffset  daylight time offset from UTC in milliseconds
     * @param dayDtStart iCal datetime string specifying the beginning of the
     *                   period for which dayRRUle applies
     * @param dayRRule   iCal recurrence rule for transition into daylight
     *                   time
     */
    public TimeZone(String tzId,
                    int stdOffset, String stdDtStart, String stdRRule,
                    int dayOffset, String dayDtStart, String dayRRule) {
        super(0, tzId);
        mHasDaylight = stdOffset != dayOffset;
        mStandardOffset = stdOffset;
        if (stdDtStart != null)
            mDayToStdDtStart = stdDtStart;
        mDayToStdRule = stdRRule;
        mDaylightOffset = dayOffset;
        if (dayDtStart != null)
            mStdToDayDtStart = dayDtStart;
        else
            mStdToDayDtStart = mDayToStdDtStart;
        mStdToDayRule = dayRRule;
        initFromICalData();
    }

    public TimeZone(String tzId,
                    int standardOffset, SimpleOnset standardOnset,
                    int daylightOffset, SimpleOnset daylightOnset) {
        super(0, tzId);
        mStandardOffset = standardOffset;
        mDaylightOffset = daylightOffset;
        setRawOffset(mStandardOffset);
        if (mDaylightOffset != mStandardOffset &&
            standardOnset != null && daylightOnset != null) {
            mHasDaylight = true;
            mDayToStdDtStart = toICalDtStart(standardOnset);
            mDayToStdRule    = toICalRRule(standardOnset);
            mStdToDayDtStart = toICalDtStart(daylightOnset);
            mStdToDayRule    = toICalRRule(daylightOnset);
            mStandardOnset  = standardOnset;
            mDaylightOnset  = daylightOnset;

            SimpleTimeZoneRule stzDaylight =
                new SimpleTimeZoneRule(daylightOnset);
            setStartRule(stzDaylight.mMonth,
                         stzDaylight.mDayOfMonth,
                         stzDaylight.mDayOfWeek,
                         stzDaylight.mDtStartMillis);
            SimpleTimeZoneRule stzStandard =
                new SimpleTimeZoneRule(standardOnset);
            setEndRule(stzStandard.mMonth,
                       stzStandard.mDayOfMonth,
                       stzStandard.mDayOfWeek,
                       stzStandard.mDtStartMillis);
            setDSTSavings(mDaylightOffset - mStandardOffset);
        }
    }

    public TimeZone(VTimeZone vtz) {
        super(0, vtz.getProperties().getProperty(Property.TZID).getValue());
        initFromVTimeZone(vtz.getName(), vtz);
    }

    public TimeZone(String tzId, VTimeZone vtz) {
        super(0, tzId);
        initFromVTimeZone(tzId, vtz);
    }

    public TimeZone(int rawOffset, String tzId) {
        super(rawOffset, tzId);
        mStandardOffset = mDaylightOffset = rawOffset;
        mHasDaylight = false;
    }

    public VTimeZone getVTimeZone() {
        try {
            VTimeZone toRet = calcVTimeZone();
            return toRet;
        } catch (ParseException e) {
            return null;
        }
    }
    
    public VTimeZone calcVTimeZone() throws ParseException {
        
        if (mVTimeZone != null) {
            return mVTimeZone;
        }
        
        TzId tzId = new TzId(getID());

        PropertyList tzProps = new PropertyList();
        tzProps.add(tzId);

        ComponentList tzComponents = new ComponentList();

        if (mDayToStdDtStart != null) {
            DtStart standardTzStart = null;
            standardTzStart = new DtStart(new ParameterList(), mDayToStdDtStart);
            TzOffsetTo standardTzOffsetTo = new TzOffsetTo(new ParameterList(), new UtcOffset(mStandardOffset));
            TzOffsetFrom standardTzOffsetFrom = new TzOffsetFrom(new UtcOffset(mDaylightOffset));
    
            PropertyList standardTzProps = new PropertyList();
            standardTzProps.add(standardTzStart);
            standardTzProps.add(standardTzOffsetTo);
            standardTzProps.add(standardTzOffsetFrom);
    
            if (mDayToStdRule != null) {
                RRule standardTzRRule = null;
                standardTzRRule = new RRule(new ParameterList(), mDayToStdRule);
                standardTzProps.add(standardTzRRule);
            }
    
            tzComponents.add(new Standard(standardTzProps));

            if (mDaylightOffset == mStandardOffset) {
                // This TZ doesn't use daylight savings time.  Skip the DST
                // section below.  (ical4j complains if we don't.)
                mVTimeZone = new VTimeZone(tzProps, tzComponents);
                return mVTimeZone;
            }
        }

        if (mStdToDayDtStart != null) {
            DtStart daylightTzStart = null;
                daylightTzStart = new DtStart(new ParameterList(), mStdToDayDtStart);
            TzOffsetTo daylightTzOffsetTo = new TzOffsetTo(new ParameterList(), new UtcOffset(mDaylightOffset));
            TzOffsetFrom daylightTzOffsetFrom = new TzOffsetFrom(new UtcOffset(mStandardOffset));

            PropertyList daylightTzProps = new PropertyList();
            daylightTzProps.add(daylightTzStart);
            daylightTzProps.add(daylightTzOffsetTo);
            daylightTzProps.add(daylightTzOffsetFrom);

            if (mStdToDayRule != null) {
                RRule daylightTzRRule = null;
                daylightTzRRule = new RRule(new ParameterList(), mStdToDayRule);
                daylightTzProps.add(daylightTzRRule);
            }

            tzComponents.add(new Daylight(daylightTzProps));
        }

        mVTimeZone = new VTimeZone(tzProps, tzComponents);
        return mVTimeZone;
    }

    private void initFromVTimeZone(String tzId, VTimeZone vtz) {
        mVTimeZone = vtz;
        
        ComponentList c = vtz.getObservances();
        
        Standard std = (Standard) c.getComponent(Observance.STANDARD);
        if (std != null) {
            PropertyList props = std.getProperties();
            TzOffsetTo tzTo = (TzOffsetTo) props.getProperty(Property.TZOFFSETTO);
            if (tzTo != null)
                mStandardOffset = (int) tzTo.getOffset().getOffset();
            
            Property d = props.getProperty(Property.DTSTART);
            if (d != null)
                mDayToStdDtStart = ((DtStart) d).getValue();
            
            Property r = props.getProperty(Property.RRULE);
            if (r!= null) {
                RRule rrule = (RRule) r;
                mDayToStdRule = rrule.getRecur().toString();
            } else {
                // TODO - deal with timezones without rules for cutover
                // TODO - deal with timezones with RDATE instead of RRULE
            }
        }
        
        Daylight daylight = (Daylight) c.getComponent(net.fortuna.ical4j.model.component.Observance.DAYLIGHT);
        if (daylight != null) {
            mHasDaylight = true;
            PropertyList props = daylight.getProperties();
            TzOffsetTo tzTo = (TzOffsetTo) props.getProperty(Property.TZOFFSETTO);
            if (tzTo != null)
                mDaylightOffset = (int) tzTo.getOffset().getOffset();
            
            Property d = props.getProperty(Property.DTSTART);
            if (d != null)
                mStdToDayDtStart = ((DtStart) d).getValue();
            
            Property r = props.getProperty(Property.RRULE);
            if (r!= null) {
                RRule rrule = (RRule) r;
                mStdToDayRule = rrule.getRecur().toString();
            } else {
                // TODO - deal with timezones without rules for cutover
                // TODO - deal with timezones with RDATE instead of RRULE
                mHasDaylight = false;
            }
        } else {
            mDaylightOffset = mStandardOffset;
        }
        
        initFromICalData();
    }

    protected void initFromICalData() {
        setRawOffset(mStandardOffset);
        if (mHasDaylight) {
            OnsetParser std = new OnsetParser(mDayToStdRule, mDayToStdDtStart);
            OnsetParser day = new OnsetParser(mStdToDayRule, mStdToDayDtStart);
            mStandardOnset = new SimpleOnset(std.mWeek, std.mDayOfWeek, std.mMonth,
                                             std.mHour, std.mMinute, std.mSecond);
            mDaylightOnset = new SimpleOnset(day.mWeek, day.mDayOfWeek, day.mMonth,
                                             day.mHour, day.mMinute, day.mSecond);

            SimpleTimeZoneRule stzDaylight =
                new SimpleTimeZoneRule(mDaylightOnset);
            setStartRule(stzDaylight.mMonth,
                         stzDaylight.mDayOfMonth,
                         stzDaylight.mDayOfWeek,
                         stzDaylight.mDtStartMillis);
            SimpleTimeZoneRule stzStandard =
                new SimpleTimeZoneRule(mStandardOnset);
            setEndRule(stzStandard.mMonth,
                       stzStandard.mDayOfMonth,
                       stzStandard.mDayOfWeek,
                       stzStandard.mDtStartMillis);
            setDSTSavings(mDaylightOffset - mStandardOffset);
        }
    }

    // maps Java weekday number to iCalendar weekday name
    private static String sDayOfWeekNames[] = new String[Calendar.SATURDAY + 1];
    static {
        sDayOfWeekNames[0] = "XX";  // unused
        sDayOfWeekNames[Calendar.SUNDAY]    = "SU";  // 1
        sDayOfWeekNames[Calendar.MONDAY]    = "MO";  // 2
        sDayOfWeekNames[Calendar.TUESDAY]   = "TU";  // 3
        sDayOfWeekNames[Calendar.WEDNESDAY] = "WE";  // 4
        sDayOfWeekNames[Calendar.THURSDAY]  = "TH";  // 5
        sDayOfWeekNames[Calendar.FRIDAY]    = "FR";  // 6
        sDayOfWeekNames[Calendar.SATURDAY]  = "SA";  // 7
    }

    // maps iCalendar weekday name to Java weekday number
    private static Map /*<String, Integer>*/ sDayOfWeekMap = new HashMap(7);
    static {
        sDayOfWeekMap.put("SU", new Integer(Calendar.SUNDAY));     // 1
        sDayOfWeekMap.put("MO", new Integer(Calendar.MONDAY));     // 2
        sDayOfWeekMap.put("TU", new Integer(Calendar.TUESDAY));    // 3
        sDayOfWeekMap.put("WE", new Integer(Calendar.WEDNESDAY));  // 4
        sDayOfWeekMap.put("TH", new Integer(Calendar.THURSDAY));   // 5
        sDayOfWeekMap.put("FR", new Integer(Calendar.FRIDAY));     // 6
        sDayOfWeekMap.put("SA", new Integer(Calendar.SATURDAY));   // 7
    }

    private static class OnsetParser {
        private int mMonth = 0;
        private int mWeek = 0;
        private int mDayOfWeek = 0;

        private int mHour = 0;
        private int mMinute = 0;
        private int mSecond = 0;

        /**
         * Parse an iCalendar recurrence rule and DTSTART into numeric fields.
         * @param rrule
         * @param dtstart
         * @return
         */
        private OnsetParser(String rrule, String dtstart) {
            if (rrule != null) {
                for (StringTokenizer t = new StringTokenizer(rrule.toUpperCase(), ";=");
                     t.hasMoreTokens();) {
                    String token = t.nextToken();
                    if ("BYMONTH".equals(token)) {
                        mMonth = Integer.parseInt(t.nextToken());
                    } else if ("BYDAY".equals(token)) {
                        boolean negative = false;
                        int weekNum = 1;
                        String value = t.nextToken();
    
                        char sign = value.charAt(0);
                        if (sign == '-') {
                            negative = true;
                            value = value.substring(1);
                        } if (sign == '+') {
                            value = value.substring(1);
                        }
                        char num = value.charAt(0);
                        if (Character.isDigit(num)) {
                            weekNum = num - '0';
                            value = value.substring(1);
                        }
                        mWeek = negative ? -1 * weekNum : weekNum;

                        Integer day = (Integer) sDayOfWeekMap.get(value);
                        if (day == null)
                            throw new IllegalArgumentException("Invalid day of week value: " + value);
                        mDayOfWeek = day.intValue();
                    } else {
                        String s = t.nextToken();  // skip value of unused param
                    }
                }
            }
    
            if (dtstart != null) {
                // Discard date and decompose time fields.
                try {
                    int indexOfT = dtstart.indexOf('T');
                    mHour = Integer.parseInt(dtstart.substring(indexOfT + 1, indexOfT + 3));
                    mMinute = Integer.parseInt(dtstart.substring(indexOfT + 3, indexOfT + 5));
                    mSecond = Integer.parseInt(dtstart.substring(indexOfT + 5, indexOfT + 7));
                } catch (StringIndexOutOfBoundsException se) {
                    mHour = mMinute = mSecond = 0;
                } catch (NumberFormatException ne) {
                    mHour = mMinute = mSecond  = 0;
                }
            }
        }
    }

    private static String toICalDtStart(SimpleOnset onset) {
        String hourStr = Integer.toString(onset.getHour() + 100).substring(1);
        String minuteStr = Integer.toString(onset.getMinute() + 100).substring(1);
        String secondStr = Integer.toString(onset.getSecond() + 100).substring(1);
        StringBuffer sb = new StringBuffer("16010101T");
        sb.append(hourStr).append(minuteStr).append(secondStr);
        return sb.toString();
    }

    private static String toICalRRule(SimpleOnset onset) {
        if (onset.getMonth() == 0) return null;
        StringBuffer sb =
            new StringBuffer("FREQ=YEARLY;WKST=MO;INTERVAL=1;BYMONTH=");
        sb.append(onset.getMonth()).append(";BYDAY=");
        sb.append(onset.getWeek()).append(sDayOfWeekNames[onset.getDayOfWeek()]);
        return sb.toString();
    }

    private static class SimpleTimeZoneRule {
        // onset rule values transformed to suit SimpleTimeZone API
        public int mMonth = 0;
        public int mDayOfMonth = 0;
        public int mDayOfWeek = 0;
        public int mDtStartMillis = 0;

        public SimpleTimeZoneRule(SimpleOnset onset) {
            // iCalendar month is 1-based.  Java month is 0-based.
            mMonth = onset.getMonth() - 1;
            int week = onset.getWeek();
            if (week < 0) {
                // For specifying day-of-week of last Nth week of month,
                // e.g. -2SA for Saturday of 2nd to last week of month,
                // java.util.SimpleTimeZone wants negative week number
                // in dayOfMonth.
                mDayOfMonth = week;

                mDayOfWeek = onset.getDayOfWeek();
            } else {
                // For positive week, onset date is day of week on or
                // after day of month.  First week is day 1 through day 7,
                // second week is day 8 through day 14, etc.
                mDayOfMonth = (week - 1) * 7 + 1;

                // Another peculiarity of java.util.SimpleTimeZone class.
                // For positive week, day-of-week must be specified as
                // a negative value.
                mDayOfWeek = -1 * onset.getDayOfWeek();
            }
            mDtStartMillis =
                onset.getHour() * 3600000 + onset.getMinute() * 60000 + onset.getSecond() * 1000;
        }
    }
}
