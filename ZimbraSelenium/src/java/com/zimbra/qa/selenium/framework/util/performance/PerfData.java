/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2011, 2012 VMware, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.3 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * 
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.qa.selenium.framework.util.performance;

/**
 * A PerfData object tracks all the client performance data
 * @author Matt Rhoades
 *
 */
public class PerfData {

	
	protected PerfKey Key;
	
	protected String Message;
	/**
	 * When startTimestamp() is called
	 */
	protected long StartStamp = 0;

	/**
	 * The original value of Key_loaded and Key_Launched (so the harness can determine when a new value is set)
	 */
	protected String OriginalFinishStamp = null;
	protected String OriginalLaunchStamp = null;

	/**
	 * The new value of Key_loaded and Key_Launched
	 */
	protected String FinishStamp = null;
	protected String LaunchStamp = null;

	
	public PerfData(PerfKey key, String message) {
		Key = key;
		Message = message;
	}
	
	public String prettyPrint() {
		
		if ( StartStamp == 0 ) {
			// No start time!
			return ("0, 0, 0, 0, 0, 0, Error: No Start Stamp");
		}
		
		if ( FinishStamp == null || FinishStamp.trim().equals("")) {
			return ("0, 0, 0, 0, 0, 0, Error: No Finish Stamp");
		}
		
		// The 'real-time' delta from selenium
		String rDelta = "" + (Long.parseLong(FinishStamp) - StartStamp);
		if (Integer.parseInt(rDelta) > PerfMetrics.MaximumDeltaMSec) {
			return (String.format("%s, %s, %s, %s, %s, %s, %s",
					Key, "" + StartStamp, LaunchStamp, FinishStamp, rDelta, "0", "Error: Delta too long ("+ PerfMetrics.MaximumDeltaMSec +" max)") );
		}
		
		// The 'internal-time' delta from the ajax app
		String iDelta = "0";
		if ( LaunchStamp != null && !LaunchStamp.trim().equals("") ) {
			iDelta = "" + (Long.parseLong(FinishStamp) - Long.parseLong(LaunchStamp));
		}

		return (String.format("%s, %s, %s, %s, %s, %s, %s",
				Key, "" + StartStamp, LaunchStamp, FinishStamp, rDelta, iDelta, Message));
	}
	
	public static String prettyPrintHeaders() {
		return ("Key, Start, Launched, Loaded, Real Time, Internal Time, Description");
	}

}
