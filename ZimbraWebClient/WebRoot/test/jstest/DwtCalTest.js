/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2005, 2006, 2007, 2009 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.2 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */

function DwtCalTest() {
}

forceRollOver = false;
cal = null;

DwtCalTest.run =
function() {
	var shell = new DwtShell("MainShell", false);
	cal = new DwtCalendar(shell, null, DwtControl.ABSOLUTE_STYLE);
	cal.setDate(new Date());
	cal.zShow();
	cal.setZIndex(10000);
	cal.setLocation(0, 0);
	
	cal.addSelectionListener(new AjxListener(null, DwtCalTest.calSelListener));
	cal.addDateRangeListener(new AjxListener(null, DwtCalTest.calDateRangeListener));
	
	var b = new DwtButton(shell, null, null, DwtControl.ABSOLUTE_STYLE);
	b.setText("Force RO");
	b.setZIndex(10000);
	b.setBounds(250, 0);
	b.addSelectionListener(new AjxListener(null, DwtCalTest.roButtonListener));
	
	b = new DwtButton(shell, null, null, DwtControl.ABSOLUTE_STYLE);
	b.setText("FDOWX");
	b.setZIndex(10000);
	b.setLocation(250, 25);
	b.addSelectionListener(new AjxListener(null, DwtCalTest.fdowButtonListener));
	
	b = new DwtButton(shell, null, null, DwtControl.ABSOLUTE_STYLE);
	b.setText("HILITE");
	b.setZIndex(10000);
	b.setLocation(250, 50);
	b.addSelectionListener(new AjxListener(null, DwtCalTest.hlButtonListener));

	b = new DwtButton(shell, null, null, DwtControl.ABSOLUTE_STYLE);
	b.setText("WEEK MODE");
	b.setZIndex(10000);
	b.setLocation(250, 75);
	b.addSelectionListener(new AjxListener(null, DwtCalTest.wmButtonListener));
	
	b = new DwtButton(shell, null, null, DwtControl.ABSOLUTE_STYLE);
	b.setText("WORK WEEK MODE");
	b.setZIndex(10000);
	b.setLocation(250, 100);
	b.addSelectionListener(new AjxListener(null, DwtCalTest.wwmButtonListener));

	b = new DwtButton(shell, null, null, DwtControl.ABSOLUTE_STYLE);
	b.setText("DAY MODE");
	b.setZIndex(10000);
	b.setLocation(250, 125);
	b.addSelectionListener(new AjxListener(null, DwtCalTest.dmButtonListener));

	b = new DwtButton(shell, null, null, DwtControl.ABSOLUTE_STYLE);
	b.setText("SET WW");
	b.setZIndex(10000);
	b.setLocation(250, 150);
	b.addSelectionListener(new AjxListener(null, DwtCalTest.swwButtonListener));	
}

DwtCalTest.calSelListener = 
function(ev) {
	DBG.println("SELECTION LISTENER: " + ev.detail.toString());
}

DwtCalTest.calDateRangeListener = 
function(ev) {
	DBG.println("DATE RANGE LISTENER");
	DBG.println("START: " + ev.start.toString());
	DBG.println("END: " + ev.end.toString());
}

DwtCalTest.roButtonListener = 
function(ev) {
	cal.setForceRollOver(!cal.getForceRollOver());
}

fdow = 0;

DwtCalTest.fdowButtonListener = 
function(ev) {
	fdow = (fdow + 1) % 7
	DBG.println("Setting FDOW: " + fdow);
	cal.setFirstDayOfWeek(fdow);
}

var lastEnable = true;
DwtCalTest.hlButtonListener = 
function(ev) {
	var dateArray = [new Date(2005, 2, 1), new Date(2005, 2, 4), new Date(2005, 2, 26), new Date(2005, 3, 26),
					 new Date(2005, 2, 28), new Date(2005, 2, 30), new Date(2005, 1, 27)];
	DBG.println("Setting HiLite");
	cal.setHiLite(dateArray, lastEnable, false);
	lastEnable = !lastEnable;
}

DwtCalTest.wmButtonListener = 
function(ev) {
	DBG.println("Setting Mode 2 Week");
	cal.setSelectionMode(DwtCalendar.WEEK);
}

DwtCalTest.wwmButtonListener = 
function(ev) {
	DBG.println("Setting Mode 2 Work Week");
	cal.setSelectionMode(DwtCalendar.WORK_WEEK);
}

DwtCalTest.dmButtonListener = 
function(ev) {
	DBG.println("Setting Mode 2 Day");
	cal.setSelectionMode(DwtCalendar.DAY);
}

wwIdx = 0;
ww = [[0, 1, 1, 1, 1, 1, 0],
	  [1, 1, 0, 0, 1, 0, 1],
	  [0, 0, 0, 0, 0, 0, 0],
	  [1, 0, 1, 0, 1, 1, 0]];
DwtCalTest.swwButtonListener = 
function(ev) {
	DBG.println("Setting Work Week: ");
	wwIdx = (wwIdx + 1) % 4
	for (var i = 0; i < 7; i++) 
		DBG.println(ww[wwIdx][i]);
	cal.setWorkingWeek(ww[wwIdx]);
}

