/*
 * ***** BEGIN LICENSE BLOCK *****
 * Version: ZPL 1.2
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.2 ("License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.zimbra.com/license
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
 * the License for the specific language governing rights and limitations
 * under the License.
 * 
 * The Original Code is: Zimbra Collaboration Suite Web Client
 * 
 * The Initial Developer of the Original Code is Zimbra, Inc.
 * Portions created by Zimbra are Copyright (C) 2005, 2006 Zimbra, Inc.
 * All Rights Reserved.
 * 
 * Contributor(s):
 * 
 * ***** END LICENSE BLOCK *****
 */
/**
* Creates an error dialog which basically means it will have a "Report" button
* @constructor
* @class
* A normal DwtMessageDialog w/ a "Report" button that will post user info to the 
* server when clicked.
*/
function ZaErrorDialog(parent) {
	if (arguments.length === 0) {return;}

	var detailButton = new DwtDialog_ButtonDescriptor(ZaErrorDialog.DETAIL_BUTTON, AjxMsg.detail, DwtDialog.ALIGN_LEFT);
	DwtMessageDialog.call(this, parent, null, null, [detailButton]);

	// setup the detail button
	this._detailCell = document.getElementById(this._detailCellId);
	var detailBtn = this._button[ZaErrorDialog.DETAIL_BUTTON];
	detailBtn.setImage("SelectPullDownArrow");
	this.registerCallback(ZaErrorDialog.DETAIL_BUTTON, this._showDetail, this);
}

ZaErrorDialog.prototype = new DwtMessageDialog;
ZaErrorDialog.prototype.constructor = ZaErrorDialog;


// Consts

ZaErrorDialog.REPORT_BUTTON = ++DwtDialog.LAST_BUTTON;
ZaErrorDialog.DETAIL_BUTTON = ++DwtDialog.LAST_BUTTON;
ZaErrorDialog.SCHEME = (location.protocol == 'https:') ? "https:" : "http:";
ZaErrorDialog.REPORT_URL = ZaErrorDialog.SCHEME + "//www.zimbra.com/e/";


// Public methods

ZaErrorDialog.prototype.toString = 
function() {
	return "ZaErrorDialog";
};

ZaErrorDialog.prototype.reset =
function() {
	this.setDetailString();
	DwtMessageDialog.prototype.reset.call(this);
};

ZaErrorDialog.prototype.setMessage =
function(msgStr, detailStr, style, title) {
	DwtMessageDialog.prototype.setMessage.call(this, msgStr, style, title);
	this.setDetailString(detailStr);
};

/**
* Sets the text that shows up when the Detail button is pressed.
*
* @param text	detail text
*/
ZaErrorDialog.prototype.setDetailString = 
function(text) {
	if (!(this._buttonElementId[ZaErrorDialog.DETAIL_BUTTON])) {return;}
	this._detailStr = text;
	if (text) {
		this._button[ZaErrorDialog.DETAIL_BUTTON].setVisible(true);
		if (this._detailCell && this._detailCell.innerHTML !== "") {
			this._detailCell.innerHTML = this._getDetailHtml(); //update detailCell if it is shown
		}
	} else {
		this._button[ZaErrorDialog.DETAIL_BUTTON].setVisible(false);
		if (this._detailCell) {
			this._detailCell.innerHTML = "";
		}
	}
};

ZaErrorDialog.prototype._getContentHtml =
function() {
	this._detailCellId = Dwt.getNextId();
	var html = new Array();
	var idx = 0;

	html[idx++] = DwtMessageDialog.prototype._getContentHtml.call(this);
	html[idx++] = "<div id='" + this._detailCellId + "'></div>";
	
	return html.join("");
};

ZaErrorDialog.prototype._getDetailHtml =
function() {
	return "<div class='vSpace'></div><table cellspacing=0 cellpadding=0 width='100%'>" +
		   "<tr><td><textarea readonly rows='10'>" + this._detailStr + "</textarea></td></tr></table>";
};


// Displays the detail text
ZaErrorDialog.prototype._showDetail = function() {
	if (this._detailCell) {
		if (this._detailCell.innerHTML === "") {
			this._button[ZaErrorDialog.DETAIL_BUTTON].setImage("SelectPullUpArrow");
			this._detailCell.innerHTML = this._getDetailHtml();
		} else {
			this._button[ZaErrorDialog.DETAIL_BUTTON].setImage("SelectPullDownArrow");
			this._detailCell.innerHTML = "";
		}
	}
};

// Displays the detail text
ZaErrorDialog.prototype.showDetail = function(show) {
	if (this._detailCell) {
		if (show) {
			this._button[ZaErrorDialog.DETAIL_BUTTON].setImage("SelectPullUpArrow");
			this._detailCell.innerHTML = this._getDetailHtml();
		} else {
			this._button[ZaErrorDialog.DETAIL_BUTTON].setImage("SelectPullDownArrow");
			this._detailCell.innerHTML = "";
		}
	}
};
