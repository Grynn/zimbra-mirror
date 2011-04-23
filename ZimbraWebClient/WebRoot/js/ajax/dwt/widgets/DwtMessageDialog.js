/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
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
 * @overview
 * This file contains classes for a message dialog.
 */

/**
 * @class
 * 
 * Creates a new message dialog. This class represents a reusable message dialog box.
 * Messages can be informational, warning, or critical.
 * 
 * @author Ross Dargahi
 * 
 * @param {hash}		params			a hash of parameters
 * @param {DwtComposite}	params.parent		the parent widget (the shell)
 * @param {string}	params.className 		the CSS class
 * @param {array}	params.buttons				the buttons to show. Defaults to {@link DwtDialog.OK_BUTTON} button
 * @param {array}	params.extraButtons	  	a list of {@link DwtDialog_ButtonDescriptor} objects describing custom buttons to add to the dialog
 * 
 * @extends	DwtDialog
 */
DwtMessageDialog = function(params) {
	if (arguments.length == 0) { return; }
	params = Dwt.getParams(arguments, DwtMessageDialog.PARAMS);
	this._msgCellId = Dwt.getNextId("MessageDialog_");
	params.standardButtons = params.buttons || [DwtDialog.OK_BUTTON];
	DwtDialog.call(this, params);
	
	this.setContent(this._contentHtml());
	this._msgCell = document.getElementById(this._msgCellId);
	this.addEnterListener(new AjxListener(this, this._enterListener));
};

DwtMessageDialog.PARAMS = ["parent", "className", "buttons", "extraButtons", "id"];

DwtMessageDialog.prototype = new DwtDialog;
DwtMessageDialog.prototype.constructor = DwtMessageDialog;

/**
 * Defines the "critical" style.
 */
DwtMessageDialog.CRITICAL_STYLE = 1;
/**
 * Defines the "info" style.
 */
DwtMessageDialog.INFO_STYLE = 2;
/**
 * Defines the "warning" style.
 */
DwtMessageDialog.WARNING_STYLE = 3;

DwtMessageDialog.TITLE = {};
DwtMessageDialog.TITLE[DwtMessageDialog.CRITICAL_STYLE] = AjxMsg.criticalMsg;
DwtMessageDialog.TITLE[DwtMessageDialog.INFO_STYLE] = AjxMsg.infoMsg;
DwtMessageDialog.TITLE[DwtMessageDialog.WARNING_STYLE] = AjxMsg.warningMsg;

DwtMessageDialog.ICON = {};
DwtMessageDialog.ICON[DwtMessageDialog.CRITICAL_STYLE] = "Critical_32";
DwtMessageDialog.ICON[DwtMessageDialog.INFO_STYLE] = "Information_32";
DwtMessageDialog.ICON[DwtMessageDialog.WARNING_STYLE] = "Warning_32";


// Public methods

/**
 * Returns a string representation of the object.
 * 
 * @return		{string}		a string representation of the object
 */
DwtMessageDialog.prototype.toString = 
function() {
	return "DwtMessageDialog";
};

/**
* Sets the message style (info/warning/critical) and content.
*
* @param {string}	msgStr		the message text
* @param {constant}	style		the style (see <code>DwtMessageDialog.*_STYLE</code> constants)
* @param {string}	title		the dialog box title
*/
DwtMessageDialog.prototype.setMessage =
function(msgStr, style, title) {
	style = style || DwtMessageDialog.INFO_STYLE;
	title = title || DwtMessageDialog.TITLE[style];
	this.setTitle(title);
	if (msgStr) {
        var html = [];
		var i = 0;
		html[i++] = "<table cellspacing=0 cellpadding=0 border=0 width=100% height=100%><tr><td valign='top'>";
		html[i++] =  AjxImg.getImageHtml(DwtMessageDialog.ICON[style], null, "id='" +  this._msgCellId + "_Image'");
		html[i++] = "</td><td class='DwtMsgArea' id='" +  this._msgCellId +"_Msg'>";
		html[i++] = msgStr;
		html[i++] = "</td></tr></table>";
		this._msgCell.innerHTML = html.join("");
	} else {
		this._msgCell.innerHTML = "";
	}
};

DwtMessageDialog.prototype.setSize =
function(width, height) {
	var msgCell = document.getElementById(this._msgCellId);
	if (msgCell && (width || height)) {
		Dwt.setSize(msgCell, width, height);
	}
};

/**
 * Resets the message dialog. This should be performed to "reuse" the dialog.
 * 
 */
DwtMessageDialog.prototype.reset = 
function() {
	this._msgCell.innerHTML = "";
	DwtDialog.prototype.reset.call(this);
};

/**
 * Handles the dialog key action. If the user hits the "Esc" key and no "Cancel" button is present,
 * the key action is treated it as a press of the "OK" button.
 * 
 * @param	{DwtKeyMap}		actionCode	the key action code
 * @param	{DwtKeyEvent}	ev	the key event
 * 
 * @private
 */
DwtMessageDialog.prototype.handleKeyAction =
function(actionCode, ev) {
	return DwtDialog.prototype.handleKeyAction.call(this, actionCode, ev);
};

// Private methods

/**
 * @private
 */
DwtMessageDialog.prototype._contentHtml = 
function() {
	return "<div id='" + this._msgCellId + "' class='DwtMsgDialog'></div>";
};

/**
 * @private
 */
DwtMessageDialog.prototype._enterListener =
function(ev) {
	this._runEnterCallback();
};
