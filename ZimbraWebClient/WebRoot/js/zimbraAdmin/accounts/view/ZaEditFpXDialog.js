/*
 * ***** BEGIN LICENSE BLOCK *****
 *
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2007 Zimbra, Inc.
 *
 * The contents of this file are subject to the Yahoo! Public License
 * Version 1.0 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 *
 * ***** END LICENSE BLOCK *****
 */

/**
* @class ZaEditFpXDialog
* @contructor ZaEditFpXDialog
* @author Charles Cao
* @param parent
* param app
**/
ZaEditFpXDialog = function(parent,  app, w, h, title) {
	if (arguments.length == 0) return;
	this._standardButtons = [DwtDialog.OK_BUTTON, DwtDialog.CANCEL_BUTTON];
	ZaXDialog.call(this, parent, app, null, title, w, h);
	this._containedObject = {};
	this.initForm(ZaFp.myXModel,this.getMyXForm());
}

ZaEditFpXDialog.prototype = new ZaXDialog;
ZaEditFpXDialog.prototype.constructor = ZaEditFpXDialog;

ZaEditFpXDialog.prototype.getMyXForm =
function() {
	var xFormObject = {
		numCols:1,
		items:[
            {ref:ZaFp.A_prefix, type:_TEXTFIELD_, label:ZaMsg.Enter_FpPrefix,width:230} ,
            {ref:ZaFp.A_name, type:_TEXTFIELD_, label:ZaMsg.Enter_ForeignAccount,width:230}
		]
	};
	return xFormObject;
}