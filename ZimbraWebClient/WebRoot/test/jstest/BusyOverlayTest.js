/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2005, 2006, 2007, 2009, 2010 Zimbra, Inc.
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

function BusyOverlayTest() {
}


BusyOverlayTest.run =
function() {
	var shell = new DwtShell("MainShell", false, null, null, false);
	shell.setBusyDialogTitle("Searching...");
	shell.setBusyDialogText("Your search is in progress<br>Please Wait...");
	shell.setBusy(true, null, true, 0, new AjxCallback(null, BusyOverlayTest.cancelCallback));
}

BusyOverlayTest.cancelCallback =
function(ev) {
	alert("Cancel Clicked");
}

