/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2007, 2009, 2010 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.3 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */

package com.zimbra.cs.taglib.bean;

import java.util.List;

public class ZApptMultiDayLayoutBean {

    private List<ZApptRowLayoutBean> mAllDayRows;
    private List<ZApptRowLayoutBean> mRows;
    private List<ZApptDayLayoutBean> mDays;
    private int mMaxColumns;

    public ZApptMultiDayLayoutBean(List<ZApptDayLayoutBean> days, List<ZApptRowLayoutBean> allDayRows, List<ZApptRowLayoutBean> rows) {
        mAllDayRows = allDayRows;
        mRows = rows;
        mDays = days;
        mMaxColumns = 0;
        for (ZApptDayLayoutBean day : days) {
            mMaxColumns += day.getColumns().size();
        }
    }

    public List<ZApptRowLayoutBean> getAllDayRows() {
        return mAllDayRows;
    }

    public List<ZApptRowLayoutBean> getRows() {
        return mRows;
    }

    public List<ZApptDayLayoutBean> getDays() {
        return mDays;
    }

    public int getNumDays() {
        return mDays.size();
    }

    public int getMaxColumns() {
        return mMaxColumns;
    }

    public long getScheduleAlldayOverlapCount() {
        int overlap = 0;
        for ( ZApptDayLayoutBean day : mDays) {
            if (!day.getAllDayAppts().isEmpty())
                overlap++;
        }
        return overlap;
    }
}
