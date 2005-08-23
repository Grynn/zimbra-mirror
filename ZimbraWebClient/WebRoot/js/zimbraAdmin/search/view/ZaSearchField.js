/*
 * ***** BEGIN LICENSE BLOCK *****
 * Version: ZPL 1.1
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.1 ("License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.zimbra.com/license
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
 * the License for the specific language governing rights and limitations
 * under the License.
 * 
 * The Original Code is: Zimbra Collaboration Suite.
 * 
 * The Initial Developer of the Original Code is Zimbra, Inc.
 * Portions created by Zimbra are Copyright (C) 2005 Zimbra, Inc.
 * All Rights Reserved.
 * 
 * Contributor(s):
 * 
 * ***** END LICENSE BLOCK *****
 */

function ZaSearchField(parent, className, size, posStyle) {

	DwtComposite.call(this, parent, className, posStyle);

	size = (size == null) ? 16 : size;
	this._setMouseEventHdlrs(true);
	var fieldId = Dwt.getNextId();
	var buttonColId = Dwt.getNextId();

	var doc = this.getDocument();
	this.getHtmlElement().innerHTML = this._createHtml(size, fieldId, buttonColId); 
	this._searchField = Dwt.getDomObj(doc, fieldId);
	this._searchField.onkeypress = ZaSearchField._keyPressHdlr;
	
	this._searchButton = new DwtButton(this, null, "SearchButton");
	this._searchButton.setToolTipContent(ZaMsg.searchForAccounts);
    this._searchButton.setImage(ZaImg.I_SEARCH);
    this._searchButton.setText(ZaMsg.search);
    this._searchButton.setData("me", this);
    this._searchButton.addSelectionListener(new AjxListener(this, ZaSearchField.prototype._invokeCallback));
    this._changed = false;
    Dwt.getDomObj(doc, buttonColId).appendChild(this._searchButton.getHtmlElement());
}

ZaSearchField.prototype = new DwtComposite;
ZaSearchField.prototype.constructor = ZaSearchField;

ZaSearchField.prototype.toString = 
function() {
	return "ZaSearchField";
}

ZaSearchField.UNICODE_CHAR_RE = /\S/;

ZaSearchField.prototype.registerCallback =
function(callbackFunc, obj) {
	this._callbackFunc = callbackFunc;
	this._callbackObj = obj;
}

ZaSearchField.prototype.focus =
function() {
	this._searchField.focus();
}

ZaSearchField.prototype.setEnabled =
function(enable) {
	this._searchField.disabled = !enable;
	this._searchButton.setEnabled(enable);
}

ZaSearchField.prototype.setValue =
function(value) {
//	if (value != this._searchField.value) {
		this._searchField.value = value;
		this.setFieldChanged(true);
//	}
}

ZaSearchField.prototype.getValue =
function() {
	return this._searchField.value;
}

ZaSearchField.prototype.setFieldChanged =
function(changed) {
	if (this._changed != changed) {
		this._changed = changed;
		//this._searchButton.setActivated(changed);
		if (changed)
			this._searchButton.setImage(ZaImg.I_UNDO);
		else	
			this._searchButton.setImage(ZaImg.I_SEARCH);
	}
}

ZaSearchField.prototype._createHtml =
function(size, fieldId, buttonColId) {
	return "<table cellpadding='0' cellspacing='0' border='0' style='padding:2px;'>" +
		"<tr valign='middle'>" +
			"<td valign='middle' nowrap>" +
			AjxImg.getImageHtml(ZaImg.M_BANNER) +
			"</td>" +
			"<td valign='middle' nowrap><input type='text' nowrap size='" + size + "' id='" + fieldId + "' class='Field'/></td>" + 
			"<td valign='middle' style='padding-left:2px;padding-right:2px;' id='" + buttonColId + "'></td>" +
		"</tr>" + 
	"</table>";
}

ZaSearchField.prototype._invokeCallback =
function(evt) {
//	if (this._searchField.value.search(ZaSearchField.UNICODE_CHAR_		return;
	if (this._callbackFunc != null) {
		if (this._callbackObj != null)
			this._callbackFunc.call(this._callbackObj, this, this._searchField.value);
		else 
			this._callbackFunc(this, this._searchField.value);
	}
}

ZaSearchField.prototype._addChild =
function(child) {
    this._children.add(child);
}

ZaSearchField._keyPressHdlr =
function(ev) {
    var obj = DwtUiEvent.getDwtObjFromEvent(ev);
    obj.setFieldChanged(true);
	var charCode = DwtKeyEvent.getCharCode(ev);
	if (charCode == 13 || charCode == 3) {
	    obj._invokeCallback();
	    return false;
	}
	return true;
}


