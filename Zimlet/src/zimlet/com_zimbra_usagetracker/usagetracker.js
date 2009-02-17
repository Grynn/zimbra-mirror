/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Zimlets
 * Copyright (C) 2006, 2007 Zimbra, Inc.
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

//////////////////////////////////////////////////////////////////////////////
// Collects usage data about the user and sends it to the Google gods.
// @author Zimlet author: Parag Shah.
//////////////////////////////////////////////////////////////////////////////

function Com_Zimbra_Usagetracker() {
};

Com_Zimbra_Usagetracker.prototype = new ZmZimletBase();
Com_Zimbra_Usagetracker.prototype.constructor = Com_Zimbra_Usagetracker;


// Public methods

Com_Zimbra_Usagetracker.prototype.toString =
function() {
	return "Com_Zimbra_Usagetracker";
};

Com_Zimbra_Usagetracker.prototype.init =
function() {
	this._ganalytics = new GoogleAnalytics();
};

Com_Zimbra_Usagetracker.prototype.doubleClicked =
function(canvas) {
	// do nothing
};

Com_Zimbra_Usagetracker.prototype.onShowView =
function(viewId, isNewView) {
	this._ganalytics.handleShow(viewId, isNewView);
};

/**
 * Report an action triggered by the user (i.e. button click or menu item selection)
 *
 * @param type				[Integer]	Describes what kind of action happened (i.e. button, menuitem, treeitem)
 * @param action			[String]	The name of the action
 * @param currentViewId		[Integer]	The current view user is on when the action happened
 * @param lastViewId		[Integer]	The last view the user was on when the action happened
 */
Com_Zimbra_Usagetracker.prototype.onAction =
function(type, action, currentViewId, lastViewId) {
	this._ganalytics.handleAction(type, action, currentViewId, lastViewId);
};


/**
 * Google analytics specific code goes here
 */
function GoogleAnalytics() {
	try {
		this._pageTracker = _gat._getTracker("UA-7436833-1");					// replace with your own key
		this._pageTracker._setDomainName("none");								// for localhost test only. comment out this line for production version
		this._pageTracker._trackPageview();										// this call is required.
	} catch(err) {
		alert("Google Analytics error.");										// probably want to silently fail here.
	}
};

GoogleAnalytics.prototype.handleShow =
function(viewId, isNewView) {
	this._pageTracker._trackPageview("onShow_"+viewId);
};

GoogleAnalytics.prototype.handleAction =
function(type, action, currentViewId, lastViewId) {
	var text = [("type="+type), ("action="+action), ("currentView="+currentViewId), ("lastView="+lastViewId)].join("::");
	this._pageTracker._trackPageview("onAction_"+text);
};
