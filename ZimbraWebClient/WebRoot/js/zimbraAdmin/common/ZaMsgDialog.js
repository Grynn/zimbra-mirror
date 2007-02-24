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
* Creates a new message dialog.
* @constructor
* @class
* This class represents a reusable message dialog box. Messages can be informational, warning, or
* critical.
*/
function ZaMsgDialog(parent, className, buttons, app, extraButtons) {
	this._app = app;
 	DwtMessageDialog.call(this, parent, className, buttons, extraButtons);
}

ZaMsgDialog.prototype = new DwtMessageDialog;
ZaMsgDialog.prototype.constructor = ZaMsgDialog;

ZaMsgDialog.prototype.setApp = 
function(app) {
	this._app=app;
}
ZaMsgDialog.CLOSE_TAB_DELETE_BUTTON = "close tab and delete";
ZaMsgDialog.CLOSE_TAB_DELETE_BUTTON_DESC = 
	new DwtDialog_ButtonDescriptor (ZaMsgDialog.CLOSE_TAB_DELETE_BUTTON, ZaMsg.bt_close_tab_delete, DwtDialog.ALIGN_RIGHT);
ZaMsgDialog.NO_DELETE_BUTTON = "no delete" ;
ZaMsgDialog.NO_DELETE_BUTTON_DESC = 
	new DwtDialog_ButtonDescriptor (ZaMsgDialog.NO_DELETE_BUTTON, ZaMsg.bt_no_delete, DwtDialog.ALIGN_RIGHT);







