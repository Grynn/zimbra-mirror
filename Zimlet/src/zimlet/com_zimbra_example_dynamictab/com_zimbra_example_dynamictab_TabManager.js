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
 * @class
 * This class represents a tab manager.
 * 
 * @param	{ZmZimletBase}		zimletBase		the zimlet base
 *   
 */
function com_zimbra_example_dynamictab_TabManager(zimletBase) {
	
	this._zimletBase = zimletBase;
};

com_zimbra_example_dynamictab_TabManager.prototype.constructor = com_zimbra_example_dynamictab_TabManager;

/**
 * Constants
 */
com_zimbra_example_dynamictab_TabManager.USER_PROPERTY_TAB_IDS = "com_zimbra_dynamictab_property_tab_ids";
com_zimbra_example_dynamictab_TabManager.USER_PROPERTY_TAB_LABEL = "com_zimbra_dynamictab_property_tab_label_";
com_zimbra_example_dynamictab_TabManager.USER_PROPERTY_TAB_TOOLTIP = "com_zimbra_dynamictab_property_tab_tooltip_";
com_zimbra_example_dynamictab_TabManager.USER_PROPERTY_TAB_URL = "com_zimbra_dynamictab_property_tab_url_";

com_zimbra_example_dynamictab_TabManager.TAB_ID_LIST_SEPARATOR = ",";

/**
 * Gets the tab ids.
 * 
 * @return	{Array}	an array of tab ids or an empty array for none
 */
com_zimbra_example_dynamictab_TabManager.prototype.getTabIdsArray =
function () {
	var tabIds = this._zimletBase.getUserProperty(com_zimbra_example_dynamictab_TabManager.USER_PROPERTY_TAB_IDS);
	if (tabIds == null || tabIds.length <= 0)
		return	new Array();
	
	return	tabIds.split(com_zimbra_example_dynamictab_TabManager.TAB_ID_LIST_SEPARATOR);
};

/**
 * Gets the tab ids.
 * 
 * @param	{Array}	props		the properties
 * @return	{Array}	an array of tab ids or an empty array for none
 */
com_zimbra_example_dynamictab_TabManager.prototype.getTabIdsArrayFromProps =
function (props) {
	var tabIds = "";
	var i=0;
	for(i=0;props && i<props.length;i++) {
		if (props[i].name == com_zimbra_example_dynamictab_TabManager.USER_PROPERTY_TAB_IDS) {
			tabIds = props[i].value;
			break;
		}
	}

	if (tabIds == null || tabIds.length <= 0)
		return	new Array();
	
	return	tabIds.split(com_zimbra_example_dynamictab_TabManager.TAB_ID_LIST_SEPARATOR);
};

/**
 * Gets the tab ids.
 * 
 * @return	{String}	a string tab id list or an empty string for none
 */
com_zimbra_example_dynamictab_TabManager.prototype.getTabIdsString =
function () {
	var tabIds = this._zimletBase.getUserProperty(com_zimbra_example_dynamictab_TabManager.USER_PROPERTY_TAB_IDS);
	if (tabIds == null || tabIds.length <= 0)
		return	"";
	
	return	tabIds;
};

/**
 * Sets the tab ids.
 * 
 * @param	{Array}	an array tab id list
 */
com_zimbra_example_dynamictab_TabManager.prototype.setTabIds =
function (tabIds) {
	var tabIdsList = tabIds.join(com_zimbra_example_dynamictab_TabManager.TAB_ID_LIST_SEPARATOR);
	
	this._zimletBase.setUserProperty(com_zimbra_example_dynamictab_TabManager.USER_PROPERTY_TAB_IDS,tabIdsList,true);
};

/**
 * Gets the tab.
 * 
 * @param	{String}	tabId		the tab id
 * @return	{String}	a hash representing a tab object
 */
com_zimbra_example_dynamictab_TabManager.prototype.getTab =
function (tabId) {
	
	var tabLabelPropName = com_zimbra_example_dynamictab_TabManager.USER_PROPERTY_TAB_LABEL + tabId;
	var tabToolTipPropName = com_zimbra_example_dynamictab_TabManager.USER_PROPERTY_TAB_TOOLTIP + tabId;
	var tabUrlPropName = com_zimbra_example_dynamictab_TabManager.USER_PROPERTY_TAB_URL + tabId;

	var tabLabel = this._zimletBase.getUserProperty(tabLabelPropName);
	var tabToolTip = this._zimletBase.getUserProperty(tabToolTipPropName);
	var tabUrl = this._zimletBase.getUserProperty(tabUrlPropName);
	
	var tabObject = {
			tabId: tabId,
			tabLabel: tabLabel,
			tabToolTip: tabToolTip,
			tabUrl: tabUrl
	};
	
	return	tabObject;
};

/**
 * Gets the tab label.
 * 
 * @param	{Array}		props		the properties
 * @param	{String}	tabId		the tab id
 * @param	{String}	defaultValue		the default value
 * @return	{String}	the tab label
 */
com_zimbra_example_dynamictab_TabManager.prototype.getTabLabel =
function (props, tabId, defaultValue) {
	return	this._getProp(props, com_zimbra_example_dynamictab_TabManager.USER_PROPERTY_TAB_LABEL + tabId, defaultValue);
};

/**
 * Gets the tab tool tip.
 * 
 * @param	{Array}		props		the properties
 * @param	{String}	tabId		the tab id
 * @param	{String}	defaultValue		the default value
 * @return	{String}	the tab label
 */
com_zimbra_example_dynamictab_TabManager.prototype.getTabToolTip =
function (props, tabId, defaultValue) {
	return	this._getProp(props, com_zimbra_example_dynamictab_TabManager.USER_PROPERTY_TAB_TOOLTIP + tabId, defaultValue);
};

/**
 * Gets the tab tool tip.
 * 
 * @param	{Array}		props		the properties
 * @param	{String}	tabId		the tab id
 * @param	{String}	defaultValue		the default value
 * @return	{String}	the tab label
 */
com_zimbra_example_dynamictab_TabManager.prototype.getTabUrl =
function (props, tabId, defaultValue) {
	return	this._getProp(props, com_zimbra_example_dynamictab_TabManager.USER_PROPERTY_TAB_URL + tabId, defaultValue);
};

/**
 * Gets the property.
 * 
 */
com_zimbra_example_dynamictab_TabManager.prototype._getProp =
function(props, name, defaultValue) {
	var value = defaultValue;
	
	for (i=0;i < props.length;i++) {
		if (props[i].name == name)
			return	props[i].value;
	}
	
	return value;
};

/**
 * Saves the tab.
 * 
 * @param	{String}	tabId		the tab id
 * @param	{String}	tabLabel		the tab label
 * @param	{String}	tabToolTip		the tab tool tip
 * @param	{String}	tabUrl		the tab url
 * @param	{Boolean}	commit		<code>true</code> to commit the properties
 */
com_zimbra_example_dynamictab_TabManager.prototype.saveTab =
function (tabId,tabLabel,tabToolTip,tabUrl,commit) {
	
	var tabLabelPropName = com_zimbra_example_dynamictab_TabManager.USER_PROPERTY_TAB_LABEL + tabId;
	var tabToolTipPropName = com_zimbra_example_dynamictab_TabManager.USER_PROPERTY_TAB_TOOLTIP + tabId;
	var tabUrlPropName = com_zimbra_example_dynamictab_TabManager.USER_PROPERTY_TAB_URL + tabId;

	this._zimletBase.setUserProperty(tabLabelPropName,tabLabel);
	this._zimletBase.setUserProperty(tabToolTipPropName,tabToolTip);
	this._zimletBase.setUserProperty(tabUrlPropName,tabUrl,commit);
};

/**
 * Gets the tab id from the property name.
 * 
 * @param	{String	propName	the property name
 * @return	{String}	the tab id or <code>null</code> if not tab property
 */
com_zimbra_example_dynamictab_TabManager.prototype.getTabIdFromProperty =
function (propName) {

	var idx = -1;
	var tmp = new String(propName);

	// check for label property
	idx = propName.indexOf(com_zimbra_example_dynamictab_TabManager.USER_PROPERTY_TAB_LABEL)
	if (idx >= 0) {
		return	tmp.substr(com_zimbra_example_dynamictab_TabManager.USER_PROPERTY_TAB_LABEL.length);
	}

	// check for tooltip property
	idx = propName.indexOf(com_zimbra_example_dynamictab_TabManager.USER_PROPERTY_TAB_TOOLTIP)
	if (idx >= 0) {
		return	tmp.substr(com_zimbra_example_dynamictab_TabManager.USER_PROPERTY_TAB_TOOLTIP.length);
	}

	// check for url property
	idx = propName.indexOf(com_zimbra_example_dynamictab_TabManager.USER_PROPERTY_TAB_URL)
	if (idx >= 0) {
		return	tmp.substr(com_zimbra_example_dynamictab_TabManager.USER_PROPERTY_TAB_URL.length);
	}

	return	null;
};
