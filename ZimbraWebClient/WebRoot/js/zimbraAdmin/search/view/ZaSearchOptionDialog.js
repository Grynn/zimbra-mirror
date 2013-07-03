/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2011 VMware, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.3 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * 
 * ***** END LICENSE BLOCK *****
 */
/**
 * Created by IntelliJ IDEA.
 * User: mingzhang
 * Date: 10/11/11
 * Time: 3:42 AM
 * To change this template use File | Settings | File Templates.
 */

ZaSearchOptionDialog = function(parent, optionId, w, h, contextId) {
	if (arguments.length == 0) return;
	var clsName = "ZaSearchOptionDialog";
	if(!this._standardButtons)
		this._standardButtons = [DwtDialog.OK_BUTTON, DwtDialog.CANCEL_BUTTON];
	if(!this._extraButtons) {
		this._extraButtons = [];
	}

	this._contextId = contextId? contextId:ZaId.DLG_UNDEF;
    this._optionId = optionId;
	DwtDialog.call(this, {
		parent:parent,
		className:clsName,
		standardButtons:this._standardButtons,
		extraButtons:this._extraButtons,
        mode: DwtBaseDialog.MODELESS,
		id:ZaId.getDialogId(this._contextId)
	});

    this._controller = ZaApp.getInstance().getSearchBuilderController () ;
	this._app = ZaApp.getInstance();
	this._localXForm = null;
	this._localXModel = null;
	this._drawn = false;
	this._containedObject = null;

	this._pageDiv = document.createElement("div");
	this._pageDiv.className = "ZaXWizardDialogPageDiv";

	Dwt.setSize(this._pageDiv, w, h);
	this._pageDiv.style.overflow = "auto";
	this._pageDiv.style["overflow-y"] = "auto";
	this._pageDiv.style["overflow-x"] = "auto";

	this._createContentHtml();
    this.initForm(ZaSearchOption.getNewObjectTypeXModel(optionId), ZaSearchOption.getNewObjectTypeXForm (optionId), ZaSearchOption.getDefaultInstance(optionId));
}

ZaSearchOptionDialog.prototype = new ZaXDialog;
ZaSearchOptionDialog.prototype.constructor = ZaSearchOptionDialog;
ZaSearchOptionDialog.TEMPLATE = "admin.Widgets#ZaSeachOptionDialog";

ZaSearchOptionDialog.prototype._createHtmlFromTemplate =
function(templateId, data) {
	DwtDialog.prototype._createHtmlFromTemplate.call(this, ZaSearchOptionDialog.TEMPLATE, data);
};

ZaSearchOptionDialog.prototype.getMyXForm =
function(entry) {
}
