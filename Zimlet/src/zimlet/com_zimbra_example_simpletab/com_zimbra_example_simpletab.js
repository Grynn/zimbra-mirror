/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Zimlets
 * Copyright (C) 2005, 2006, 2007, 2008, 2009, 2010 Zimbra, Inc.
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

/**
 * Defines the Zimlet handler class.
 *   
 */
function com_zimbra_example_simpletab_HandlerObject() {
}

/**
 * Makes the Zimlet class a subclass of ZmZimletBase.
 *
 */
com_zimbra_example_simpletab_HandlerObject.prototype = new ZmZimletBase();
com_zimbra_example_simpletab_HandlerObject.prototype.constructor = com_zimbra_example_simpletab_HandlerObject;

/**
* This method gets called by the Zimlet framework when the zimlet loads.
*  
*/
com_zimbra_example_simpletab_HandlerObject.prototype.init =
function() {

	this._simpleAppName = this.createApp("Simple Tab App", "zimbraIcon", "A simple app in a new tab");

};

/**
 * This method gets called by the Zimlet framework each time the application is opened or closed.
 *  
 * @param	appName		the application name
 * @param	active		if true, the application status is open; otherwise, false
 */
com_zimbra_example_simpletab_HandlerObject.prototype.appActive =
function(appName, active) {
	
	switch (appName) {
		case this._simpleAppName: {
		
			var app = appCtxt.getApp(appName); // get access to ZmZimletApp

			break;
		}
	}
	
	// do something
};

/**
 * This method gets called by the Zimlet framework when the application is opened for the first time.
 *  
 * @param	appName		the application name		
 */
com_zimbra_example_simpletab_HandlerObject.prototype.appLaunch =
function(appName) {

	switch (appName) {
		case this._simpleAppName: {
			// do something
		
			var app = appCtxt.getApp(appName); // get access to ZmZimletApp

			break;
		}
	}

};
