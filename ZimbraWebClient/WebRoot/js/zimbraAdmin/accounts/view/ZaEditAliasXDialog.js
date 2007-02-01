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

/**
* @class ZaEditAliasXDialog
* @contructor ZaEditAliasXDialog
* @author Greg Solovyev
* @param parent
* param app
**/
function ZaEditAliasXDialog(parent,  app, w, h, title) {
	if (arguments.length == 0) return;
	this._standardButtons = [DwtDialog.OK_BUTTON, DwtDialog.CANCEL_BUTTON];	
	ZaXDialog.call(this, parent, app, null, title, w, h);
	this._containedObject = {};
	this.initForm(ZaAlias.myXModel,this.getMyXForm());
}

ZaEditAliasXDialog.prototype = new ZaXDialog;
ZaEditAliasXDialog.prototype.constructor = ZaEditAliasXDialog;

ZaEditAliasXDialog.prototype.getMyXForm = 
function() {	
	var xFormObject = {
		numCols:1,
		items:[
			{ref:ZaAccount.A_name, type:_EMAILADDR_, label:null}
		]
	};
	return xFormObject;
}
