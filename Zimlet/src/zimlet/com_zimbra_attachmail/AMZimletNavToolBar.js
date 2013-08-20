/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Zimlets
 * Copyright (C) 2010, 2013 Zimbra Software, LLC.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.4 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */

AMZimletNavToolBar = function(params) {
	params.className = params.className || "AMZimletNavToolBar";
	var hasText = (params.hasText !== false);
	params.buttons = this._getButtons(hasText);
	params.toolbarType = ZmId.TB_NAV;
	params.posStyle = params.posStyle || DwtControl.STATIC_STYLE;
	ZmButtonToolBar.call(this, params);
	if (hasText) {
		this._textButton = this.getButton(ZmOperation.TEXT);
	}
};

AMZimletNavToolBar.prototype = new ZmButtonToolBar;
AMZimletNavToolBar.prototype.constructor = AMZimletNavToolBar;

AMZimletNavToolBar.prototype.toString = 
function() {
	return "AMZimletNavToolBar";
};

AMZimletNavToolBar.prototype._getButtons = 
function(hasText) {

	var buttons = [];
	buttons.push(ZmOperation.PAGE_BACK);
	if (hasText) {
		buttons.push(ZmOperation.TEXT);
	}
	buttons.push(ZmOperation.PAGE_FORWARD);

	return buttons;
};

AMZimletNavToolBar.prototype.createOp =
function(id, params) {
	params.textClassName = "ZWidgetTitle AMZimletNavToolBarTitle";
	return ZmButtonToolBar.prototype.createOp.apply(this, arguments);
};

AMZimletNavToolBar.prototype.setText =
function(text) {
	if (!this._textButton) return;
	this._textButton.setText(text);
};
