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
function com_zimbra_example_taboverview_HandlerObject() {
};

/**
 * Makes the Zimlet class a subclass of ZmZimletBase.
 *
 */
com_zimbra_example_taboverview_HandlerObject.prototype = new ZmZimletBase();
com_zimbra_example_taboverview_HandlerObject.prototype.constructor = com_zimbra_example_taboverview_HandlerObject;

/**
 * This method gets called by the Zimlet framework when the zimlet loads.
 *  
 */
com_zimbra_example_taboverview_HandlerObject.prototype.init =
function() {
	
	// create the tab application
	this._tabAppName = this.createApp("Tab Label", "zimbraIcon", "Tab Tool Tip");
	
};

/**
 * This method gets called by the Zimlet framework each time the application is opened or closed.
 *  
 * @param	{String}	appName		the application name
 * @param	{Boolean}	active		if <code>true</code>, the application status is open; otherwise, <code>false</code>
 */
com_zimbra_example_taboverview_HandlerObject.prototype.appActive =
function(appName, active) {
	switch(appName) {
		case this._tabAppName: {			
			if (active) {
			
				var app = appCtxt.getApp(this._tabAppName); // returns ZmZimletApp
				app.setContent("<b>THIS IS THE TAB APPLICATION CONTENT AREA</b>");

				var toolbar = app.getToolbar(); // returns ZmToolBar
				toolbar.setContent("<b>THIS IS THE TAB APPLICATION TOOLBAR AREA</b>");

				var overview = app.getOverview(); // returns ZmOverview
				overview.setContent("<b>THIS IS THE TAB APPLICATION OVERVIEW AREA</b>");

				var controller = appCtxt.getAppController();
				var appChooser = controller.getAppChooser();

				// change the tab label and tool tip
				var appButton = appChooser.getButton(this._tabAppName);
				appButton.setText("NEW TAB LABEL");
				appButton.setToolTipContent("NEW TAB TOOL TIP");

			}
			break;
		}
	}
};

/**
 * This method gets called by the Zimlet framework when the application is opened for the first time.
 *  
 * @param	{String}	appName		the application name		
 */
com_zimbra_example_taboverview_HandlerObject.prototype.appLaunch =
function(appName) {
	switch(appName) {
		case this._tabAppName: {
			// the app is launched, do something
			break;	
		}	
	}
};

