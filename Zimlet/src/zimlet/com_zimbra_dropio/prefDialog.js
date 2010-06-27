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
 * Object that deals with Preferences and Preferences dialog
 * @param zimlet  Drop.io Zimlet
 */
function DropioZimletPrefDialog(zimlet) {
	this.zimlet = zimlet;
	this.dropioZimlet_apiKey = this.zimlet.getUserProperty("dropioZimlet_apiKey");
	this.dropioZimlet_dropName = this.zimlet.getUserProperty("dropioZimlet_dropName");
	this.dropioZimlet_dropAdminPwd = this.zimlet.getUserProperty("dropioZimlet_dropAdminPwd");
	this.dropioZimlet_dropGuestPwd = this.zimlet.getUserProperty("dropioZimlet_dropGuestPwd");
	this.dropioZimlet_proxyHost = this.zimlet.getUserProperty("dropioZimlet_proxyHost");
	this.dropioZimlet_proxyPort = this.zimlet.getUserProperty("dropioZimlet_proxyPort");
	this.dropioZimlet_proxyON = this.zimlet.getUserProperty("dropioZimlet_proxyON");
}

/**
 * Creates and displays the dialog
 */
DropioZimletPrefDialog.prototype.popup =
function() {
	if (this.pbDialog) {
		this._setPreferences();
		this.pbDialog.popup();
		return;
	}
	this.pView = new DwtComposite(this.zimlet.getShell());
	this.pView.getHtmlElement().innerHTML = this._createPreferenceView();
	this.pbDialog = new DwtDialog({parent:this.zimlet.getShell(),title:this.zimlet.getMessage("DropioZimlet_preferences"), view:this.pView, standardButtons:[DwtDialog.OK_BUTTON, DwtDialog.CANCEL_BUTTON]});
	this.pbDialog.setButtonListener(DwtDialog.OK_BUTTON, new AjxListener(this, this._okBtnListner));
	this._setPreferences();
	this.pbDialog.popup();
};

/**
 * Hides the dialog
 */
DropioZimletPrefDialog.prototype.popdown =
function() {
	if (this.pbDialog) {
		this.pbDialog.popup();
	}
};

/**
 * Sets user preferences to dialog DOM objects
 */
DropioZimletPrefDialog.prototype._setPreferences =
function() {
	document.getElementById("dropioZimlet_apiKey").value = this.dropioZimlet_apiKey;
	document.getElementById("dropioZimlet_dropName").value = this.dropioZimlet_dropName;
	document.getElementById("dropioZimlet_dropAdminPwd").value = this.dropioZimlet_dropAdminPwd;
	document.getElementById("dropioZimlet_dropGuestPwd").value = this.dropioZimlet_dropGuestPwd;
	document.getElementById("dropioZimlet_proxyHost").value = this.dropioZimlet_proxyHost;
	document.getElementById("dropioZimlet_proxyPort").value = this.dropioZimlet_proxyPort;
	document.getElementById("dropioZimlet_proxyON").checked = this.dropioZimlet_proxyON == "true";
};

/**
 * Creates Preferences view
 */
DropioZimletPrefDialog.prototype._createPreferenceView =
function() {
	var html = new Array();
	html.push("<table  class='dropio_table' width=100%><tr><td>",
		this.zimlet.getMessage("DropioZimlet_APIKey"), "</td><td><input id='dropioZimlet_apiKey'  type='text'/></td></tr>",
		"<tr><td colspan=2><div class='horizSep' /></td></tr>",
		"<tr><td colspan=2><input id='dropioZimlet_proxyON'  type='checkbox'/>",this.zimlet.getMessage("DropioZimlet_enableProxy"),"</td></tr>",
		"<tr><td>",this.zimlet.getMessage("DropioZimlet_proxyHost"), "</td><td><input id='dropioZimlet_proxyHost'  type='text'/></td></tr>",
		"<tr><td>",this.zimlet.getMessage("DropioZimlet_proxyPort"),"</td><td><input id='dropioZimlet_proxyPort'  type='text'/></td></tr>",
		"<tr><td colspan=2><div class='horizSep' /></td></tr>",
		"<tr><td colspan=2 style='font-weight:bold'>",this.zimlet.getMessage("DropioZimlet_premiumSettings"),"</td></tr>",
		"<tr><td>",this.zimlet.getMessage("DropioZimlet_dropName"),"</td><td><input id='dropioZimlet_dropName'  type='text'/></td></tr>",
		"<tr><td>",this.zimlet.getMessage("DropioZimlet_adminPwd"),"</td><td><input id='dropioZimlet_dropAdminPwd'  type='password'/></td></tr>",
		"<tr><td>",this.zimlet.getMessage("DropioZimlet_guestPwd"),"</td><td><input id='dropioZimlet_dropGuestPwd'  type='password'/></td></tr>",
		"<tr><td colspan=2><div class='horizSep' /></td></tr>",
		"</table>",
		"<div><b>",this.zimlet.getMessage("DropioZimlet_note"),"</b><br/>",this.zimlet.getMessage("DropioZimlet_note1"),
		"<br/>",this.zimlet.getMessage("DropioZimlet_note2"),"</div>");

	return html.join("");
};

/**
 * Listens to OK button and saves user properties
 */
DropioZimletPrefDialog.prototype._okBtnListner =
function() {
	var dropioZimlet_apiKey = document.getElementById("dropioZimlet_apiKey").value;
	var dropioZimlet_dropName = document.getElementById("dropioZimlet_dropName").value;
	var dropioZimlet_dropAdminPwd = document.getElementById("dropioZimlet_dropAdminPwd").value;
	var dropioZimlet_dropGuestPwd = document.getElementById("dropioZimlet_dropGuestPwd").value;
	if (dropioZimlet_dropName != "" && (dropioZimlet_dropAdminPwd == "" || dropioZimlet_dropGuestPwd == "")) {
		this.zimlet._showErrorMessage(this.zimlet.getMessage("DropioZimlet_bothAdminAndGuestPwdRequired"));
		return;
	}

	var dropioZimlet_proxyHost = document.getElementById("dropioZimlet_proxyHost").value;
	var dropioZimlet_proxyPort = document.getElementById("dropioZimlet_proxyPort").value;
	var dropioZimlet_proxyON = document.getElementById("dropioZimlet_proxyON").checked;
	if (document.getElementById("dropioZimlet_proxyON_attchDlg")) {//also set this so we get to use this new value
		document.getElementById("dropioZimlet_proxyON_attchDlg").checked = dropioZimlet_proxyON;
	}

	if (dropioZimlet_apiKey != this.dropioZimlet_apiKey || dropioZimlet_dropName != this.dropioZimlet_dropName
	|| this.dropioZimlet_dropGuestPwd != dropioZimlet_dropGuestPwd || dropioZimlet_dropAdminPwd != this.dropioZimlet_dropAdminPwd
	|| this.dropioZimlet_proxyHost != dropioZimlet_proxyHost || dropioZimlet_proxyPort != this.dropioZimlet_proxyPort
	|| dropioZimlet_proxyON != this.dropioZimlet_proxyON) {
		this.zimlet.setUserProperty("dropioZimlet_apiKey", dropioZimlet_apiKey);
		this.zimlet.setUserProperty("dropioZimlet_dropName", dropioZimlet_dropName);
		this.zimlet.setUserProperty("dropioZimlet_dropAdminPwd", dropioZimlet_dropAdminPwd);
		this.zimlet.setUserProperty("dropioZimlet_dropGuestPwd", dropioZimlet_dropGuestPwd);
		this.zimlet.setUserProperty("dropioZimlet_proxyHost", dropioZimlet_proxyHost);
		this.zimlet.setUserProperty("dropioZimlet_proxyPort", dropioZimlet_proxyPort);
		this.zimlet.setUserProperty("dropioZimlet_proxyON", dropioZimlet_proxyON);

		this.dropioZimlet_apiKey = dropioZimlet_apiKey;
		this.dropioZimlet_dropName = dropioZimlet_dropName;
		this.dropioZimlet_dropAdminPwd = dropioZimlet_dropAdminPwd;
		this.dropioZimlet_dropGuestPwd = dropioZimlet_dropGuestPwd;
		this.dropioZimlet_proxyHost = dropioZimlet_proxyHost;
		this.dropioZimlet_proxyPort = dropioZimlet_proxyPort;
		this.dropioZimlet_proxyON = dropioZimlet_proxyON;
	}
	var callback = new AjxCallback(this, this._propertiesSaved);
	this.zimlet.saveUserProperties(callback);
	this.pbDialog.popdown();
};

/**
 *  Displays Preferences saved message
 */
DropioZimletPrefDialog.prototype._propertiesSaved =
function() {
	appCtxt.getAppController().setStatusMsg(this.zimlet.getMessage("DropioZimlet_preferencesSaved"), ZmStatusView.LEVEL_INFO);
};
