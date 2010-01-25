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
 * 
 */
com_zimbra_example_dynamictab_EditTabsDialog = function(shell,zimletBase) {
	
	this._tabManager = zimletBase.getTabManager();
	
	this._dialogTitleStr = zimletBase.getMessage("dialog_title");
	this._saveButtonLabelStr = zimletBase.getMessage("dialog_button_save_label");
	this._addTabButtonLabelStr = zimletBase.getMessage("dialog_button_add_tab");
	
	// pass the title, view & buttons information to create dialog box
	ZmDialog.call(this, {title:this._dialogTitleStr, parent:shell, standardButtons:[DwtDialog.OK_BUTTON,DwtDialog.CANCEL_BUTTON]});

	// create view
	this.pView = new DwtComposite(shell); //creates an empty div as a child of main shell div
	this.pView.setSize("450", "350"); // set width and height  
	this.pView.getHtmlElement().style.overflow = "auto"; // adds scrollbar
	this.pView.getHtmlElement().innerHTML = this._createDialogView(); // insert html to the dialogbox

	this.setView(this.pView);
	
	// get elements
	this.editForm_dynamicTabTable = document.getElementById(this.elementId_editForm_dynamicTabTable);
	this.editForm_dynamicTabButtonTable = document.getElementById(this.elementId_editForm_dynamicTabButtonTable);

	// setup view
	this._addTabButton();

	// OK and Cancel Actions
	this.setButtonListener(DwtDialog.CANCEL_BUTTON, new AjxListener(this, function() {
		this._cancelButtonListener();
	}));

	this.setButtonListener(DwtDialog.OK_BUTTON, new AjxListener(this, function() {
		this._okButtonListener();
	}));

	var okButton = this.getButton(DwtDialog.OK_BUTTON);
	okButton.setText(this._saveButtonLabelStr);
	
	// add existing tabs
	var tabIdsArray = this._tabManager.getTabIdsArray();
	for (i=0;tabIdsArray && i<tabIdsArray.length;i++) {
		var tabId = tabIdsArray[i];
		
		var tabObject = this._tabManager.getTab(tabId);
		
		this._addTabListener(tabObject);
	}

};

com_zimbra_example_dynamictab_EditTabsDialog.prototype = new ZmDialog;
com_zimbra_example_dynamictab_EditTabsDialog.prototype.constructor = com_zimbra_example_dynamictab_EditTabsDialog;

/**
 * Creates the dialog view.
 * 
 */
com_zimbra_example_dynamictab_EditTabsDialog.prototype._createDialogView =
function() {

	var elId = this.getHTMLElId();
	
	this.elementId_editForm_id = elId + "_id";
	this.elementId_editForm_dynamicTabTable = elId + "_dynamicTabTable";
	this.elementId_editForm_dynamicTabButtonTable = elId + "_dynamicTabButtonTable";
	this.elementId_editForm_action = elId + "_action";
	this.elementId_editForm_tabIdList_input = elId + "_tabIdList";

	// get tab ids list
	var tabIdsList = this._tabManager.getTabIdsString();
	
	var subs = {
			editForm_id: this.elementId_editForm_id,
			editForm_dynamicTabTable: this.elementId_editForm_dynamicTabTable,
			editForm_dynamicTabButtonTable: this.elementId_editForm_dynamicTabButtonTable,
			editForm_tabIdList_input: this.elementId_editForm_tabIdList_input,
			editForm_tabIdList_value: tabIdsList,
			editForm_action: this.elementId_editForm_action
		};

	return	AjxTemplate.expand("com_zimbra_example_dynamictab.templates.Dialogs#EditTabs-Main", subs);
};

/**
 * 
 */
com_zimbra_example_dynamictab_EditTabsDialog.prototype._addTabButton =
function() {

	var row = this.editForm_dynamicTabButtonTable.insertRow(-1);
	var cell = row.insertCell(-1);

	var button = new DwtButton({parent:this, parentElement:cell});
	
	button.setText(this._addTabButtonLabelStr);
	
	button.addSelectionListener(new AjxListener(this, this._addTabListener));
};

/**
 * 
 */
com_zimbra_example_dynamictab_EditTabsDialog.prototype._addTabListener =
function(params) {

	var tabId = com_zimbra_example_dynamictab_Util.generateUniqueID(10);
	var tabLabel_value = "";
	var tabToolTip_value = "";
	var tabUrl_value = "";
	
	if (params && params.tabId) {
		tabId = params.tabId;
		tabLabel_value = params.tabLabel;
		tabToolTip_value = params.tabToolTip;
		tabUrl_value = params.tabUrl;
	} else {
		// add tab to id list since this is a new tab
		this._addTabIdToTabIdList(tabId);
	}
	
	var elId = this.getHTMLElId();
	
	var row = this.editForm_dynamicTabTable.insertRow(-1);
	var cell = row.insertCell(-1);
	var removeLinkId = Dwt.getNextId();
	
	var elementId_tabLabel_input = this._createTabElementId(tabId, elId, "tabLabel_input");
	var elementId_tabToolTip_input = this._createTabElementId(tabId, elId, "tabToolTip_input");
	var elementId_tabUrl_input = this._createTabElementId(tabId, elId, "tabUrl_input");
	
	var subs = {
		removeLinkId: removeLinkId,
		tabLabel_input: elementId_tabLabel_input,
		tabLabel_value: tabLabel_value,
		tabToolTip_input: elementId_tabToolTip_input,
		tabToolTip_value: tabToolTip_value,
		tabUrl_input: elementId_tabUrl_input,
		tabUrl_value: tabUrl_value
	};
	
	cell.innerHTML = AjxTemplate.expand("com_zimbra_example_dynamictab.templates.Dialogs#EditTabs-AddTab", subs);

	var removeEl = document.getElementById(removeLinkId);
	removeEl.onclick = AjxCallback.simpleClosure(this._removeTabListener, this, row, tabId);

};

/**
 * Creates a tab element id.
 * 
 * @param	{String}	tabId	the tab id
 * @param	{String}	parentId	the parent element id
 * @param	{String}	input	the input field name
 * @return	{String}	the element id
 */
com_zimbra_example_dynamictab_EditTabsDialog.prototype._createTabElementId =
function(tabId,parentId,input) {
	var elementId = new Array();
	var i=0;
	elementId[i++] = parentId;
	elementId[i++] = "_";
	elementId[i++] = tabId;
	elementId[i++] = "_";
	elementId[i++] = input;

	return	elementId.join("");
};

/**
 * Removes the tab row and tab id.
 * 
 * @param	{Object}	row		the row to remove
 * @param	{String}	tabId	the id of the tab to remove
 */
com_zimbra_example_dynamictab_EditTabsDialog.prototype._removeTabListener =
function(row,tabId) {

	this._removeTabIdToTabIdList(tabId);

	this.editForm_dynamicTabTable.deleteRow(row.rowIndex);
};

/**
 * Cancel button listener.
 * 
 */
com_zimbra_example_dynamictab_EditTabsDialog.prototype._cancelButtonListener =
function() {
	this.popdown();
	this.dispose();
};

/**
 * OK button listener.
 */
com_zimbra_example_dynamictab_EditTabsDialog.prototype._okButtonListener =
function() {

	var tabIdArray = this._getTabIdListArray();
	
	var elId = this.getHTMLElId();

	for (i=0;i < tabIdArray.length;i++) {
		var tabId = tabIdArray[i];
		
		var elementId_tabLabel_input = this._createTabElementId(tabId, elId, "tabLabel_input");
		var elementId_tabToolTip_input = this._createTabElementId(tabId, elId, "tabToolTip_input");
		var elementId_tabUrl_input = this._createTabElementId(tabId, elId, "tabUrl_input");

		var element = document.getElementById(elementId_tabLabel_input);
		var tabLabel_value = element.value;

		element = document.getElementById(elementId_tabToolTip_input);
		var tabToolTip_value = element.value;

		element = document.getElementById(elementId_tabUrl_input);
		var tabUrl_value = element.value;

		// store tab info as user properties		
		this._tabManager.saveTab(tabId,tabLabel_value,tabToolTip_value,tabUrl_value,false);
	}
	
	// set tab ids and save the props
	this._tabManager.setTabIds(tabIdArray);

	this.popdown();
	this.dispose();
};

/**
 * Adds the tab id to the tab id list.
 * 
 * @param	{String}	tabId		the tab id to add
 * @return	{Array}		the resulting tab id list array
 */
com_zimbra_example_dynamictab_EditTabsDialog.prototype._addTabIdToTabIdList =
function(tabId) {
	var tabIdArray = this._getTabIdListArray();
	tabIdArray.push(tabId);

	var tabIdListElement = document.getElementById(this.elementId_editForm_tabIdList_input);
	tabIdListElement.value = tabIdArray.join(com_zimbra_example_dynamictab_HandlerObject.TAB_ID_LIST_SEPARATOR);

	return	tabIdArray;
}

/**
 * Removes the tab id from the tab id list.
 * 
 * @param	{String}	tabId		the tab id to add
 * @return	{Array}		the resulting tab id list array
 */
com_zimbra_example_dynamictab_EditTabsDialog.prototype._removeTabIdToTabIdList =
function(tabId) {
	var tabIdArray = this._getTabIdListArray();
	
	for(i=0;i<tabIdArray.length;i++) {
		var t= tabIdArray[i];
		
		if (t == tabId) {
			tabIdArray.splice(i,1);
			break;
		}
	}

	var tabIdListElement = document.getElementById(this.elementId_editForm_tabIdList_input);
	tabIdListElement.value = tabIdArray.join(com_zimbra_example_dynamictab_HandlerObject.TAB_ID_LIST_SEPARATOR);

	return	tabIdArray;
}

/**
 * Gets the tab id list as an array.
 * 
 * @return	{Array}		an array
 */
com_zimbra_example_dynamictab_EditTabsDialog.prototype._getTabIdListArray =
function() {
	var tabIdListElement = document.getElementById(this.elementId_editForm_tabIdList_input);
	if (tabIdListElement.value == null || tabIdListElement.value.length <= 0)
		return	new Array();
	
	return	tabIdListElement.value.split(com_zimbra_example_dynamictab_TabManager.TAB_ID_LIST_SEPARATOR);
}
