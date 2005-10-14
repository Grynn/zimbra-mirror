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
    
    private static final String DEFAULT_DTSTART = "16010101T020000";
    
    protected boolean mHasDaylight = false;
    
    protected int    mStdOffset = 0;
    protected String mDayToStdDtStart = DEFAULT_DTSTART;
    protected String mDayToStdRule = null;
    
    protected int    mDaylightOffset = 0; 
    protected String mStdToDayDtStart = DEFAULT_DTSTART;
    protected String mStdToDayRule = null;
    protected VTimeZone mVTimeZone;
    
    public TimeZone(String tzId,
            int stdOffset, String stdDtStart, String stdRRule,
            int dayOffset, String dayDtStart, String dayRRule) {
        super(0, tzId);
        mHasDaylight = stdOffset != dayOffset;
        mStdOffset = stdOffset;
        if (stdDtStart != null)
            mDayToStdDtStart = stdDtStart;
        mDayToStdRule = stdRRule;
        mDaylightOffset = dayOffset;
        if (dayDtStart != null)
            mStdToDayDtStart = dayDtStart;
        else
            mStdToDayDtStart = mDayToStdDtStart;
        mStdToDayRule = dayRRule;
        commonInit();
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
            TzOffsetTo standardTzOffsetTo = new TzOffsetTo(new ParameterList(), new UtcOffset(mStdOffset));
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

            if (mDaylightOffset == mStdOffset) {
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
            TzOffsetFrom daylightTzOffsetFrom = new TzOffsetFrom(new UtcOffset(mStdOffset));

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
    
    public TimeZone(VTimeZone vtz) {
        super(0, vtz.getProperties().getProperty(Property.TZID).getValue());
        create(vtz.getName(), vtz);
    }
    
    public TimeZone(int rawOffset, String tzId) {
        super(rawOffset, tzId);
    }

    public TimeZone(String tzId, VTimeZone vtz) {
        super(0, tzId);
        create(tzId, vtz);
    }
    
    private void create(String tzId, VTimeZone vtz) {
        mVTimeZone = vtz;
        
        ComponentList c = vtz.getObservances();
        
        Standard std = (Standard) c.getComponent(Observance.STANDARD);
        if (std != null) {
            PropertyList props = std.getProperties();
            TzOffsetTo tzTo = (TzOffsetTo) props.getProperty(Property.TZOFFSETTO);
            if (tzTo != null)
                mStdOffset = (int) tzTo.getOffset().getOffset();
            
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
            mDaylightOffset = mStdOffset;
        }
        
//        if (std == null && daylight == null)
//            throw MailServiceException.INVALID_REQUEST("VTIMEZONE must have at least one STANDARD or DAYLIGHT (TZID=" + tzId + ")", null);
        
        commonInit();
    }
    
//    private void commonInit() {
//        commonInit(mStdOffset, mDaylightOffset, mHasDaylight, mDayToStdDtStart, mDayToStdRule, mStdToDayDtStart, mStdToDayRule);
//    }
//    
//    public void commonInit(int stdOffset, int daylightOffset, boolean hasDaylight, 
//            String dayToStdDtStart, String dayToStdRule, String stdToDayDtStart, String stdToDayRule)
//    {
//        setRawOffset(stdOffset);
//        if (hasDaylight) {
//            int stdDtStart = dtStartToTimeInt(dayToStdDtStart);
//            OnsetRule stdOnset = toOnsetRule(dayToStdRule);
//            int dayDtStart = dtStartToTimeInt(stdToDayDtStart);
//            OnsetRule dayOnset = toOnsetRule(stdToDayRule);
//            
//            setStartRule(dayOnset.month, dayOnset.dayOfMonth, dayOnset.dayOfWeek, dayDtStart);
//            setEndRule(stdOnset.month, stdOnset.dayOfMonth, stdOnset.dayOfWeek, stdDtStart);
//            setDSTSavings(daylightOffset - stdOffset);
//        }
//    }
    
    protected void commonInit() {
        setRawOffset(mStdOffset);
        if (mHasDaylight) {
            int stdDtStart = dtStartToTimeInt(mDayToStdDtStart);
            OnsetRule stdOnset = toOnsetRule(mDayToStdRule);
            int dayDtStart = dtStartToTimeInt(mStdToDayDtStart);
            OnsetRule dayOnset = toOnsetRule(mStdToDayRule);

            setStartRule(dayOnset.month, dayOnset.dayOfMonth, dayOnset.dayOfWeek, dayDtStart);
            setEndRule(stdOnset.month, stdOnset.dayOfMonth, stdOnset.dayOfWeek, stdDtStart);
            setDSTSavings(mDaylightOffset - mStdOffset);
        }
    }
    
    /**
     * Discard date and return time part of DTSTART as number of milliseconds.
     * @param dtstart yyyymoddThhmmss
     * @return (hh * 3600 + mm * 60 + ss) * 1000
     */
    private static int dtStartToTimeInt(String dtstart) {
        try {
            int indexOfT = dtstart.indexOf('T');
            int hour = Integer.parseInt(dtstart.substring(indexOfT + 1, indexOfT + 3));
            int min = Integer.parseInt(dtstart.substring(indexOfT + 3, indexOfT + 5));
            int sec = Integer.parseInt(dtstart.substring(indexOfT + 5, indexOfT + 7));
            return (hour * 3600 + min * 60 + sec) * 1000;
        } catch (StringIndexOutOfBoundsException se) {
            return 0;
        } catch (NumberFormatException ne) {
            return 0;
        }
    }
    
    private static class OnsetRule {
        private int month = 0;
        private int dayOfMonth = 0;
        private int dayOfWeek = 0;
    }
    
    private static Map /*<String, Integer>*/ sDayOfWeekMap = new HashMap(7);
    static {
        sDayOfWeekMap.put("MO", new Integer(Calendar.MONDAY));
        sDayOfWeekMap.put("TU", new Integer(Calendar.TUESDAY));
        sDayOfWeekMap.put("WE", new Integer(Calendar.WEDNESDAY));
        sDayOfWeekMap.put("TH", new Integer(Calendar.THURSDAY));
        sDayOfWeekMap.put("FR", new Integer(Calendar.FRIDAY));
        sDayOfWeekMap.put("SA", new Integer(Calendar.SATURDAY));
        sDayOfWeekMap.put("SU", new Integer(Calendar.SUNDAY));
    }

    /**
     * Parse an iCalendar recurrence rule into info suitable for passing
     * into SimpleTimeZone constructor.
     * @param rrule
     * @return
     */
    private static OnsetRule toOnsetRule(String rrule) {
        OnsetRule onset = new OnsetRule();
        if (rrule == null)
            return onset;
        for (StringTokenizer t = new StringTokenizer(rrule.toUpperCase(), ";=");
             t.hasMoreTokens();) {
            String token = t.nextToken();
            if ("BYMONTH".equals(token)) {
                // iCalendar month is 1-based.  Java month is 0-based.
                onset.month = Integer.parseInt(t.nextToken()) - 1;
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

                Integer day = (Integer) sDayOfWeekMap.get(value);
                if (day == null)
                    throw new IllegalArgumentException("Invalid day of week value: " + value);

                if (negative) {
                    // For specifying day-of-week of last Nth week of month,
                    // e.g. -2SA for Saturday of 2nd to last week of month,
                    // java.util.SimpleTimeZone wants negative week number
                    // in dayOfMonth.
                    onset.dayOfMonth = -1 * weekNum;

                    onset.dayOfWeek = day.intValue();
                } else {
                    // For positive weekNum, onset date is day of week on or
                    // after day of month.  First week is day 1 through day 7,
                    // second week is day 8 through day 14, etc.
                    onset.dayOfMonth = (weekNum - 1) * 7 + 1;

                    // Another peculiarity of java.util.SimpleTimeZone class.
                    // For positive weekNum, day-of-week must be specified as
                    // a negative value.
                    onset.dayOfWeek = -day.intValue();
                }
            } else {
                String s = t.nextToken();  // skip value of unused param
            }
        }
        return onset;
    }
    
    
}
