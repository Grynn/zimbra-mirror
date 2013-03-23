/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2006, 2007, 2009, 2010 VMware, Inc.
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

ZaZimletPoolChooser = function(params) {
 	if (arguments.length == 0) return;
		
 	DwtChooser.call(this, params);
}
 
ZaZimletPoolChooser.prototype = new DwtChooser;
ZaZimletPoolChooser.prototype.constructor = ZaZimletPoolChooser;

ZaZimletPoolChooser.prototype._createHtml = 
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
	html[idx++] = "<table cellspacing=0 cellpadding=0 border=0 width=100%>";
	html[idx++] = "<colgroup><col width='40%'/><col width='20%'/><col width='40%'/> </colgroup>";
	html[idx++] = "<tbody>";
	html[idx++] = "<tr><td style='text-align:center' class='xform_label'>" + ZaMsg.NAD_zimbraInstalledZimlets + "</td><td>&nbsp;</td><td style='text-align:center'  class='xform_label'>" + ZaMsg.NAD_zimbraAvailableZimlets + "</td</tr>"
	html[idx++] = "<tr>";

	// source list
	html[idx++] = "<td align='center' style='text-align:center' id='";
	html[idx++] = this._sourceListViewDivId;
	html[idx++] = "'></td>";

	// transfer buttons
	html[idx++] = "<td valign='middle' style='text-align:center' id='";
	html[idx++] = this._buttonsDivId;
	html[idx++] = "'>";
	if (this._allButtons) {
		html[idx++] = "<div id='";
		html[idx++] = this._addAllButtonDivId;
		html[idx++] = "'></div><br>";
	}
	for (var i = 0; i < this._buttonInfo.length; i++) {
		var id = this._buttonInfo[i].id;
		html[idx++] = "<div id='";
		html[idx++] = this._buttonDivId[id];
		html[idx++] = "'></div><br>";
	}
	// remove button
	html[idx++] = "<br><div id='";
	html[idx++] = this._removeButtonDivId;
	html[idx++] = "'></div>";
	if (this._allButtons) {
		html[idx++] = "<br><div id='";
		html[idx++] = this._removeAllButtonDivId;
		html[idx++] = "'></div><br>";
	}
	html[idx++] = "</td>";

	// target list
	html[idx++] = "<td align='center' style='text-align:center' id='";
	html[idx++] = this._targetListViewDivId;
	html[idx++] = "'></td>";	

	html[idx++] = "</tr></tbody></table>";	
	this.getHtmlElement().innerHTML = html.join("");
};
