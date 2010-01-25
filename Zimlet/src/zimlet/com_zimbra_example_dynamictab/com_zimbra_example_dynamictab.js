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
function com_zimbra_example_dynamictab_HandlerObject() {
	this._tabManager = new com_zimbra_example_dynamictab_TabManager(this);
	this._tabs = new Array();
};

/**
 * Makes the Zimlet class a subclass of ZmZimletBase.
 *
 */
com_zimbra_example_dynamictab_HandlerObject.prototype = new ZmZimletBase();
com_zimbra_example_dynamictab_HandlerObject.prototype.constructor = com_zimbra_example_dynamictab_HandlerObject;

com_zimbra_example_dynamictab_HandlerObject.WIDGETID_CONFIGURE_LINK = "com_zimbra_dynamictab_widgetid_configure_link";
com_zimbra_example_dynamictab_HandlerObject.WIDGETID_SAVE_LINK = "com_zimbra_dynamictab_widgetid_save_link";
com_zimbra_example_dynamictab_HandlerObject.WIDGETID_CANCEL_LINK = "com_zimbra_dynamictab_widgetid_cancel_link";

/**
 * This method gets called by the Zimlet framework when the zimlet loads.
 *  
 */
com_zimbra_example_dynamictab_HandlerObject.prototype.init =
function() {
	
	// init all tabs
	var tabIds = this.getTabManager().getTabIdsArray();
	
	var i=0;
	for (i=0;tabIds && i < tabIds.length; i++) {
		var tid = tabIds[i];

		var tabObject = this._tabManager.getTab(tid);
		
		var tabLabel = tabObject.tabLabel;
		var tabToolTip = tabObject.tabToolTip;
		var tabUrl = tabObject.tabUrl;
		
		// if label isn't set, default
		if (tabLabel == null || tabLabel.length <= 0)
			tabLabel = this.getMessage("tab_label_input_default");

		// if label isn't set, default
		if (tabToolTip == null || tabToolTip.length <= 0)
			tabToolTip = this.getMessage("tab_tooltip_input_default");
		
		this._createTabApplication(tabObject);
	}

};

/**
 * Gets the tab manager.
 * 
 * @return	{com_zimbra_dynamictab_TabManager}		the tab manager
 */
com_zimbra_example_dynamictab_HandlerObject.prototype.getTabManager = 
function() {
	return	this._tabManager;
};

/**
 * This method gets called by the Zimlet framework each time the application is opened or closed.
 *  
 * @param	{String}	appName		the application name
 * @param	{Boolean}	active		if true, the application status is open; otherwise, false
 */
com_zimbra_example_dynamictab_HandlerObject.prototype.appActive =
function(appName, active) {

	var tabObject = this._getTabObject(appName);

	if (tabObject && active && tabObject.reload) {
		var app = appCtxt.getApp(appName);
		this._loadContent(tabObject, app, true);
		tabObject.reload = false;
	}
};

/**
 * This method gets called by the Zimlet framework when the application is opened for the first time.
 *  
 * @param	{String}	appName		the application name		
 */
com_zimbra_example_dynamictab_HandlerObject.prototype.appLaunch =
function(appName) {
	// do nothing
};

/**
 * Creates the tab application.
 * 
 * @param	{Hash}	tabObject		the tab object
 */
com_zimbra_example_dynamictab_HandlerObject.prototype._createTabApplication =
function(tabObject) {

	var escapedLabel = com_zimbra_example_dynamictab_Util.escapeHTML(tabObject.tabLabel);
	var escapedToolTip = com_zimbra_example_dynamictab_Util.escapeHTML(tabObject.tabToolTip);

	var tabAppName = this.createApp(escapedLabel, "zimbraIcon", escapedToolTip);

	var newTabObject = {
			tabId: tabObject.tabId,
			tabAppName: tabAppName,
			tabLabel: tabObject.tabLabel,
			tabToolTip: tabObject.tabToolTip,
			reload: true,
			tabUrl: tabObject.tabUrl
	};
		
	this._tabs.push(newTabObject);
};

/**
 * Gets the tab object.
 * 
 * @param	{String}	appName		the app name
 * @return	the tab object or <code>null</code> if the app is not a tab
 */
com_zimbra_example_dynamictab_HandlerObject.prototype._getTabObject =
function(appName) {
	
	var i=0;
	for(i=0; i < this._tabs.length;i++) {
		if (appName == this._tabs[i].tabAppName)
			return	this._tabs[i]
	}
	
	return	null;
};

/**
 * This method is called when the configure link is clicked.
 * 
 * @param	{Hash}	tabObject		the tab object
 */
com_zimbra_example_dynamictab_HandlerObject.prototype._configureLinkListener =
function(tabObject) {

	var elementId_tabUrl_input = "tabUrl_input_" + tabObject.tabId;

	var saveLink = this._getSaveLink(tabObject)
	var cancelLink = this._getCancelLink(tabObject)
	
	var subs = {
			tabUrl_input: elementId_tabUrl_input,
			tabUrl_value: tabObject.tabUrl,
			save_link: saveLink,
			cancel_link: cancelLink
	};
	
	var app = appCtxt.getApp(tabObject.tabAppName);
	var toolbar = app.getToolbar();

	var htmlContent = AjxTemplate.expand("com_zimbra_example_dynamictab.templates.Dialogs#Tab-Toolbar-Edit", subs);
	toolbar.setContent(htmlContent);

	// set save link callback
	var link = document.getElementById(saveLink);
	if (link) {
		var callback = AjxCallback.simpleClosure(this._saveLinkListener, this, tabObject);
		link.onclick = callback;
	}

	// set save link callback
	link = document.getElementById(cancelLink);
	if (link) {
		var callback = AjxCallback.simpleClosure(this._cancelLinkListener, this, tabObject);
		link.onclick = callback;
	}

};

/**
 * This method is called when the save link is clicked.
 * 
 * @param	{Hash}	tabObject		the tab object
 */
com_zimbra_example_dynamictab_HandlerObject.prototype._saveLinkListener =
function(tabObject) {

	var elementId_tabUrl_input = "tabUrl_input_" + tabObject.tabId;

	var element = document.getElementById(elementId_tabUrl_input);
	var tabUrl_value = element.value;
	
	var i=0;
	for(i=0;i<this._tabs.length;i++) {
		if (this._tabs[i].tabId == tabObject.tabId)
			this._tabs[i].tabUrl = tabUrl_value;
	}

	var app = appCtxt.getApp(tabObject.tabAppName);
	this._loadContent(tabObject,app,true);
	
	this._tabManager.saveTab(tabObject.tabId, tabObject.tabLabel, tabObject.tabToolTip, tabObject.tabUrl, true);
}

/**
 * This method is called when the cancel link is clicked.
 * 
 * @param	{Hash}	tabObject		the tab object
 */
com_zimbra_example_dynamictab_HandlerObject.prototype._cancelLinkListener =
function(tabObject) {

	var app = appCtxt.getApp(tabObject.tabAppName);
	this._loadContent(tabObject,app,false);
}
/**
 * This method is called when the zimlet is double-clicked.
 * 
 */
com_zimbra_example_dynamictab_HandlerObject.prototype.doubleClicked =
function() {
	this.singleClicked();
};

/**
 * This method is called when the zimlet is single-clicked.
 * 
 */
com_zimbra_example_dynamictab_HandlerObject.prototype.singleClicked =
function() {
	this._launchEditTabsDialog();
};

/**
 * Launches the edit tabs dialog.
 * 
 */
com_zimbra_example_dynamictab_HandlerObject.prototype._launchEditTabsDialog =
function() {
	this._editTabsDialog = new com_zimbra_example_dynamictab_EditTabsDialog(this.getShell(), this);

	this._editTabsDialog.popup(); //show the dialog
};

/**
 * Gets the tab HTML content.
 *  
 * @param	{Hash}	tabObject		the tab object
 * @return {String}		the HTML content
 */
com_zimbra_example_dynamictab_HandlerObject.prototype._getTabMainContent =
function(tabObject) {
	var tabUrl = tabObject.tabUrl;

	var url = com_zimbra_example_dynamictab_Util.cleanUrl(tabUrl);
	
	var subs = {
			iframeSrcUrl: url
	};
	
	return	AjxTemplate.expand("com_zimbra_example_dynamictab.templates.Dialogs#Tab-Main", subs);
};

/**
 * Gets the tab HTML content.
 * 
 * @param	{Hash}	tabObject		the tab object
 * @return {String}		the HTML content
 */
com_zimbra_example_dynamictab_HandlerObject.prototype._getTabToolbarContent =
function(tabObject) {
	var tabUrl = tabObject.tabUrl;
	
	var configureLink = this._getConfigureLink(tabObject);
	
	var subs = {
			tabUrl: tabUrl,
			configure_link: configureLink
	};
	
	return	AjxTemplate.expand("com_zimbra_example_dynamictab.templates.Dialogs#Tab-Toolbar", subs);
};

/**
 * Checks the user properties on save.
 * 
 */
com_zimbra_example_dynamictab_HandlerObject.prototype.checkProperties = 
function(props) {

	var tabIds = this._tabManager.getTabIdsArrayFromProps(props);
	
	// clean-up props to remove
	var i=0;
	for(i=0;i<props.length;i++) {
		var tabId = this._tabManager.getTabIdFromProperty(props[i].name);

		if (tabId != null) {
			var save = com_zimbra_example_dynamictab_Util.arrayContains(tabIds,tabId);
			if (save == false) {
				props.splice(i,1);
				i--;
			}
		}
	}

	// delete tabs
	for(i=0; i < this._tabs.length; i++) {
		var deleteTab = true;
		var j = 0;
		for (j=0;j<tabIds.length; j++) {
			if (this._tabs[i].tabId == tabIds[j]) {
				deleteTab = false;
				break;
			}
		}
		
		if (deleteTab) {
			// delete tab from this._tabs and from UI
			var controller = appCtxt.getAppController();
			var appChooser = controller.getAppChooser();
			appChooser.removeButton(this._tabs[i].tabAppName);
			this._tabs.splice(i,1);
			i--;
		}
	}

	// create tabs
	for(i=0; i < tabIds.length; i++) {
		var createTab = true;
		var j = 0;
		for (j=0;j<this._tabs.length; j++) {
			if (this._tabs[j].tabId == tabIds[i]) {
				createTab = false;
				break;
			}
		}
		
		if (createTab) {
			var defaultTabLabel = this.getMessage("tab_label_input_default");
			var defaultTabToolTip = this.getMessage("tab_tooltip_input_default");

			// create the tab
			var tabLabel = this._tabManager.getTabLabel(props, tabIds[i], defaultTabLabel);
			var tabToolTip = this._tabManager.getTabToolTip(props, tabIds[i], defaultTabToolTip);
			var tabUrl = this._tabManager.getTabUrl(props, tabIds[i], null);;
			
			var tabObject = {
					tabId: tabIds[i],
					tabLabel: tabLabel,
					tabToolTip: tabToolTip,
					reload: true,
					tabUrl: tabUrl
			};
			
			this._createTabApplication(tabObject);
		}

	}

	var controller = appCtxt.getAppController();
	var chooser = controller.getAppChooser();

	// refresh tabs
	for(i=0; i < this._tabs.length; i++) {
		var tabObject = this._tabs[i];
		var appButton = chooser.getButton(tabObject.tabAppName);

		var newTabLabel = this._tabManager.getTabLabel(props, tabObject.tabId, null);
		var newTabToolTip = this._tabManager.getTabToolTip(props, tabObject.tabId, null);
		var newTabUrl = this._tabManager.getTabUrl(props, tabObject.tabId, null);

		tabObject.tabLabel = newTabLabel;
		tabObject.tabToolTip = newTabToolTip;
		
		var escapedLabel = com_zimbra_example_dynamictab_Util.escapeHTML(tabObject.tabLabel);
		var escapedToolTip = com_zimbra_example_dynamictab_Util.escapeHTML(tabObject.tabToolTip);

		appButton.setText(escapedLabel);
		appButton.setToolTipContent(escapedToolTip);

		if (newTabUrl != tabObject.tabUrl) {	
			tabObject.tabUrl = newTabUrl;
			var app = appCtxt.getApp(tabObject.tabAppName);
			this._loadContent(tabObject,app,true);
			tabObject.reload = false;
		}
	}
	
	return true;
};

/**
 * Loads the content into the application.
 * 
 * @param	{Hash}	tabObject	the tab object
 * @param	{ZmApp}	app			the application
 * @param	{Boolean}	loadMain	<code>true</code> to load the toolbar content only
 */
com_zimbra_example_dynamictab_HandlerObject.prototype._loadContent =
function(tabObject,app,loadMain) {
	
	var htmlContent = null;
	
	if (loadMain) {
		// set main content
		htmlContent = this._getTabMainContent(tabObject);
		app.setContent(htmlContent);
	}
	
	var toolbar = app.getToolbar();
	htmlContent = this._getTabToolbarContent(tabObject);
	toolbar.setContent(htmlContent);

	// set configure link callback
	var link = document.getElementById(this._getConfigureLink(tabObject));
	if (link) {
		var callback = AjxCallback.simpleClosure(this._configureLinkListener, this, tabObject);
		link.onclick = callback;
	}

};

/**
 * Gets the configure link.
 * 
 * @param	{Hash}	tabObject	the tab object
 * @return	{String}	the configure link
 */
com_zimbra_example_dynamictab_HandlerObject.prototype._getConfigureLink =
function(tabObject) {
	return	com_zimbra_example_dynamictab_HandlerObject.WIDGETID_CONFIGURE_LINK + tabObject.tabId;
};

/**
 * Gets the save link.
 * 
 * @param	{Hash}	tabObject	the tab object
 * @return	{String}	the save link
 */
com_zimbra_example_dynamictab_HandlerObject.prototype._getSaveLink =
function(tabObject) {
	return	com_zimbra_example_dynamictab_HandlerObject.WIDGETID_SAVE_LINK + tabObject.tabId;
};

/**
 * Gets the cancel link.
 * 
 * @param	{Hash}	tabObject	the tab object
 * @return	{String}	the cancel link
 */
com_zimbra_example_dynamictab_HandlerObject.prototype._getCancelLink =
function(tabObject) {
	return	com_zimbra_example_dynamictab_HandlerObject.WIDGETID_CANCEL_LINK + tabObject.tabId;
};