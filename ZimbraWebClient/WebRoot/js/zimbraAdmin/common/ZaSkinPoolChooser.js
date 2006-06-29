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
 * Portions created by Zimbra are Copyright (C) 2006 Zimbra, Inc.
 * All Rights Reserved.
 * 
 * Contributor(s):
 * 
 * ***** END LICENSE BLOCK *****
 */

function ZaSkinPoolChooser(params) {
 	if (arguments.length == 0) return;
 	DwtChooser.call(this, params);
}
 
ZaSkinPoolChooser.prototype = new DwtChooser;
ZaSkinPoolChooser.prototype.constructor = ZaSkinPoolChooser;
ZaSkinPoolChooser.prototype.resize = function () {
	
}
ZaSkinPoolChooser.prototype._createHtml = 
function() {

	this._sourceListViewDivId	= Dwt.getNextId();
	this._targetListViewDivId	= Dwt.getNextId();
	this._buttonsDivId			= Dwt.getNextId();
	this._removeButtonDivId		= Dwt.getNextId();
	if (this._allButtons) {
		this._addAllButtonDivId		= Dwt.getNextId();
		this._removeAllButtonDivId	= Dwt.getNextId();
	}

	var html = [];
	var idx = 0;
	
	// start new table for list views
	html[idx++] = "<table cellspacing=0 cellpadding=0 border=0>";
	html[idx++] = "<colgroup><col width='300px'/><col width='300px'/></colgroup>";
	html[idx++] = "<tbody>";
	html[idx++] = "<tr><td style='text-align:right' class='xform_label'>" + ZaMsg.ServerPool_AllServers + "</td>";
	// source list
	html[idx++] = "<td class='xform_field_container' id='";
	html[idx++] = this._sourceListViewDivId;
	html[idx++] = "' width='300px'></td>";
	html[idx++] = "<tr>";

	// transfer buttons
	html[idx++] = "<tr><td style='text-align:right' class='xform_label'>&nbsp;</td>";
	html[idx++] = "<td valign='middle' style='text-align:center' id='";
	html[idx++] = this._buttonsDivId;
	html[idx++] = "' width='300px'><table cellspacing=2 cellpadding=0 border=0><tr>";
	if (this._allButtons) {
		html[idx++] = "<td><div id='";
		html[idx++] = this._addAllButtonDivId;
		html[idx++] = "'></div></td>";
	}
	for (var i = 0; i < this._buttonInfo.length; i++) {
		var id = this._buttonInfo[i].id;
		html[idx++] = "<td><div id='";
		html[idx++] = this._buttonDivId[id];
		html[idx++] = "'></div></td>";
	}
	// remove button
	html[idx++] = "<td><div id='";
	html[idx++] = this._removeButtonDivId;
	html[idx++] = "'></div></td>";
	if (this._allButtons) {
		html[idx++] = "<td><div id='";
		html[idx++] = this._removeAllButtonDivId;
		html[idx++] = "'></div></td>";
	}
	html[idx++] = "</tr></table>";
	html[idx++] = "</td></tr>";


	html[idx++] = "<tr><td style='text-align:right' class='xform_label'>" + ZaMsg.ServerPool_AllServers + "</td>";
	// target list
	html[idx++] = "<td class='xform_field_container' id='";
	html[idx++] = this._targetListViewDivId;
	html[idx++] = "'></td>";
	html[idx++] = "</tr></tbody></table>";


	this.getHtmlElement().innerHTML = html.join("");
};
