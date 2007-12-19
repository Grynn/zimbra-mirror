/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Server
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
package com.zimbra.cs.taglib.bean;

import com.zimbra.cs.zclient.ZAppointmentHit;

public class ZApptCellLayoutBean {
    private boolean mIsFirst;
    private ZAppointmentHit mAppt;
    private long mRowSpan;
    private long mColSpan;
    private long mDaySpan;
    private long mWidth;
    private ZApptDayLayoutBean mDay;

    public ZApptCellLayoutBean(ZApptDayLayoutBean day) {
        mDay = day;
    }


    public ZApptDayLayoutBean getDay() {
        return mDay;
    }
    
    public boolean isIsFirst() {
        return mIsFirst;
    }

    public void setIsFirst(boolean isFirst) {
        mIsFirst = isFirst;
    }

    public ZAppointmentHit getAppt() {
        return mAppt;
    }

    public void setAppt(ZAppointmentHit appt) {
        mAppt = appt;
    }

    public long getRowSpan() {
        return mRowSpan;
    }

    public void setRowSpan(long rowSpan) {
        mRowSpan = rowSpan;
    }

    public long getColSpan() {
        return mColSpan;
    }

    public void setColSpan(long colSpan) {
        mColSpan = colSpan;
    }

    public long getWidth() {
        return mWidth;
    }

    public void setWidth(int width) {
        mWidth = width;
    }

    public void setDaySpan(long daySpan){
        mDaySpan = daySpan;
    }

    public long getDaySpan(){
        return mDaySpan;
    }

}
