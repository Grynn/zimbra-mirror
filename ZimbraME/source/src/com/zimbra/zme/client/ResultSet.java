/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite J2ME Client
 * Copyright (C) 2007, 2008 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Yahoo! Public License
 * Version 1.0 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */

package com.zimbra.zme.client;

import java.util.Vector;


public class ResultSet {
	public Vector mResults; // Results
	public boolean mMore; // True more data is available
	public boolean mNewSet; // True this is the result of a "fresh" search
	public ItemFactory mItemFactory; // Container for  results
	
	public ResultSet() {
		mResults = new Vector();
    }
    
    public void addAppointment(Appointment appt) {
        int numElements = mResults.size();
        if (!appt.mIsAllDay) {
            int i;
            for (i = 0; i < numElements; i++) {
                Appointment a1 = (Appointment)mResults.elementAt(i);
                if (appt.mStart < a1.mStart) {
                    mResults.insertElementAt(appt, i);
                    break;
                }
            }
            if (i >= numElements)
                mResults.insertElementAt(appt, i);
        } else {
            mResults.insertElementAt(appt, 0);
        }
    }
}
