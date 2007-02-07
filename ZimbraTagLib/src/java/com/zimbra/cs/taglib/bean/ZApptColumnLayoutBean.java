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
 * The Original Code is: Zimbra Collaboration Suite Server.
 *
 * The Initial Developer of the Original Code is Zimbra, Inc.
 * Portions created by Zimbra are Copyright (C) 2006 Zimbra, Inc.
 * All Rights Reserved.
 *
 * Contributor(s):
 *
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.cs.taglib.bean;

import com.zimbra.cs.zclient.ZApptSummary;

public class ZApptColumnLayoutBean {
    private boolean mIsFirst;
    private ZApptSummary mAppt;
    private long mRowSpan;
    private long mColSpan;
    private long mWidth;
    private ZApptDayLayoutBean mDay;

    public ZApptColumnLayoutBean(ZApptDayLayoutBean day) {
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

    public ZApptSummary getAppt() {
        return mAppt;
    }

    public void setAppt(ZApptSummary appt) {
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



}
