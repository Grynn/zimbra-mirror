/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Zimlets
 * Copyright (C) 2006, 2007 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Yahoo! Public License
 * Version 1.0 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */

function Com_Zimbra_Po() {
}

Com_Zimbra_Po.prototype = new ZmZimletBase();
Com_Zimbra_Po.prototype.constructor = Com_Zimbra_Po;

Com_Zimbra_Po.prototype.init =
function() {
};

Com_Zimbra_Po.NEITHER = 0;
Com_Zimbra_Po.APPROVED = 1;
Com_Zimbra_Po.REJECTED = 2;

Com_Zimbra_Po.PO_DATA = {
	"GR9328B2-3X499": { req: "Steve Patterson", desc: "Cisco Catalyst 2912MF XL 12-port Switch", total: "$25,437.38", state: Com_Zimbra_Po.REJECTED },
	"GR9328X2-3Y847": { req: "Jeanine Martin", desc: "Software for Graphics Artists in Marketing", total: "$1,298.00", state: Com_Zimbra_Po.APPROVED },
	"GR738B64-8774Q": { req: "Arlene Johnson", desc: "Telecom Hardware for new office building", total: "$12,736.17", state: Com_Zimbra_Po.NEITHER },
	"GR733454-8788Q": { req: "Chris Smith", desc: "Performance Test Server", total: "$5,723.17", state: Com_Zimbra_Po.NEITHER }
};

Com_Zimbra_Po.prototype.toolTipPoppedUp =
function(spanElement, obj, context, canvas) {
	var po = this._getPOData(obj);
	var html = new Array(20);
	var idx = 0;
	html[idx++] = "<table cellpadding='0' cellspacing='0' border='0'>";
	html[idx++] = "<tr valign='center'><td colspan='2' align='left'>";
	html[idx++] = "<div style='border-bottom: 1px solid black; white-space:nowrap;'>";
	html[idx++] = "<table cellpadding='0' cellspacing='0' border='0' width=100%;'>";
	html[idx++] = "<tr valign='center'>";
	html[idx++] = "<td>";
	html[idx++] = "<b>" + AjxStringUtil.htmlEncode("PO# " + obj) + "</b>";
	html[idx++] = "</td>";
	html[idx++] = "<td align='right'>";
	html[idx++] = AjxImg.getImageHtml("Task");
	html[idx++] = "</td>";
	html[idx++] = "</table>";
	html[idx++] = "</div>";
	html[idx++] = "</td></tr>";
	idx = this._addEntryRow("Requestor", po.req, html, idx);
	idx = this._addEntryRow("Description", po.desc, html, idx);
	idx = this._addEntryRow("Total", po.total, html, idx);
	html[idx++] = "</table>";
	canvas.innerHTML = html.join("");
};

Com_Zimbra_Po.prototype.menuItemSelected = function(itemId) {
	switch (itemId) {
		case "APPROVE_ITEM":
			this._approveListener()
			break;
		case "REJECT_ITEM":
			this._rejectListener()
			break;
	}
};

Com_Zimbra_Po.prototype._getPOData =
function(obj) {
	var po = Com_Zimbra_Po.PO_DATA[obj];
	if (po == null)
		po = Com_Zimbra_Po.PO_DATA["GR9328B2-3X499"];
	return po;
};

Com_Zimbra_Po.prototype._addEntryRow =
function(field, data, html, idx) {
	html[idx++] = "<tr valign='top'><td align='right' style='padding-right: 5px;'><b>";
	html[idx++] = AjxStringUtil.htmlEncode(field) + ":";
	html[idx++] = "</b></td><td align='left' style='width:50%;'><div style='white-space:nowrap;'>";
	html[idx++] = AjxStringUtil.htmlEncode(data);
	html[idx++] = "</div></td></tr>";
	return idx;
};

Com_Zimbra_Po.prototype._getStyle =
function(obj) {
	var po = this._getPOData(obj);
	switch (po.state) {
		case Com_Zimbra_Po.APPROVED: return "green";
		case Com_Zimbra_Po.REJECTED: return "red";
		default: return "blue";
	}
};

Com_Zimbra_Po.prototype._approveListener =
function() {
	var obj = this._actionObject;
	var po = this._getPOData(obj);
	po.state = Com_Zimbra_Po.APPROVED;
	this._actionSpan.style.color = this._getStyle(obj);
};

Com_Zimbra_Po.prototype._rejectListener =
function() {
	var obj = this._actionObject;
	var po = this._getPOData(obj);

	po.state = Com_Zimbra_Po.REJECTED;
	this._actionSpan.style.color = this._getStyle(obj);
};