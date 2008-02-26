/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite, Network Edition.
 * Copyright (C) 2007, 2008 Zimbra, Inc.  All Rights Reserved.
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
