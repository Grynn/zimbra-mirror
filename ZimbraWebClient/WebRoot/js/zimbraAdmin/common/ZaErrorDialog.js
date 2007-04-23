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

ZaErrorDialog.prototype.toString = function() {
	return "ZaErrorDialog";
};

//
// Constants
//

ZaErrorDialog.DETAIL_BUTTON = ++DwtDialog.LAST_BUTTON;

//
// Data
//

ZaErrorDialog.prototype._detailsVisible = false;

ZaErrorDialog.prototype.CONTROLS_TEMPLATE = "zimbra.templates.Widgets#ZmErrorDialogControls";

//
// Public methods
//

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

    this._button[ZaErrorDialog.DETAIL_BUTTON].setVisible(text != null);
    if (this._detailsEl) {
        this._detailsEl.innerHTML = text || "";
	}
};

// Displays the detail text
ZaErrorDialog.prototype.showDetail = function(show) {
	if (this._detailsContainerEl) {
        var image = show ? "SelectPullUpArrow": "SelectPullDownArrow";
        this._button[ZaErrorDialog.DETAIL_BUTTON].setImage(image);
        if (this._detailsEl) {
            this._detailsEl.innerHTML = this._getDetailHtml();
        }
    }
};

//
// Protected methods
//

ZaErrorDialog.prototype._createHtmlFromTemplate = function(templateId, data) {
    DwtMessageDialog.prototype._createHtmlFromTemplate.call(this, templateId, data);
    this._detailsContainerEl = document.getElementById(data.id+"_details_container");
    this._detailsEl = document.getElementById(data.id+"_details");
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
    var detailsEl = this._detailsContainerEl || this._detailsEl;
    if (detailsEl) {
        this._detailsVisible = !this._detailsVisible;
        var visible = this._detailsVisible;
        Dwt.setVisible(detailsEl, visible);
        this._button[ZaErrorDialog.DETAIL_BUTTON].setImage(visible ? "SelectPullUpArrow" : "SelectPullDownArrow");
    }
};
