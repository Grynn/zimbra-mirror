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
 * The Original Code is: Zimbra Collaboration Suite Web Client
 * 
 * The Initial Developer of the Original Code is Zimbra, Inc.
 * Portions created by Zimbra are Copyright (C) 2005 Zimbra, Inc.
 * All Rights Reserved.
 * 
 * Contributor(s):
 * 
 * ***** END LICENSE BLOCK *****
 */

/**
* @class ZaOperation
* @contructor
* simplified version of ZmOperation
* This class encapsulates the properties of an action that can be taken on some item: image, caption, description, AjxListener
* @param caption string
* @param tt string
* @param img string path to image
* @param lsnr AjxListener
**/

function ZaOperation(id, caption, tooltip, imgId, disImgId, lsnr, type, menuOpList, className, labelId) {
	this.id = id;
	this.caption = caption;
	this.tt = tooltip;
	this.listener = lsnr;
	this.imageId = imgId;
	this.disImageId = disImgId;
	this.type = (type == null)? ZaOperation.TYPE_BUTTON: type;
	this.menuOpList = menuOpList;
	this.className = className;	
	this.labelId = labelId;
}

ZaOperation.prototype.toString = 
function() {
		return "ZaOperation";
}

// types
ZaOperation.TYPE_BUTTON = 1;
ZaOperation.TYPE_MENU = 2;
var opIndex = 0;
// Operations
ZaOperation.NONE = ++opIndex;		// no operations or menu items
ZaOperation.SEP = ++opIndex;		// separator
ZaOperation.NEW = ++opIndex;
ZaOperation.DELETE = ++opIndex;
ZaOperation.REFRESH = ++opIndex;
ZaOperation.EDIT = ++opIndex;
ZaOperation.CHNG_PWD = ++opIndex;
ZaOperation.CLOSE = ++opIndex;
ZaOperation.SAVE = ++opIndex;
ZaOperation.NEW_WIZARD = ++opIndex;
ZaOperation.PAGE_FORWARD = ++opIndex;
ZaOperation.PAGE_BACK = ++opIndex;
ZaOperation.DUPLICATE = ++opIndex;
ZaOperation.GAL_WIZARD = ++opIndex;
ZaOperation.AUTH_WIZARD =++opIndex;
ZaOperation.VIEW_MAIL =++opIndex;
ZaOperation.MOVE_ALIAS = ++opIndex;
ZaOperation.NEW_MENU = ++opIndex;
ZaOperation.HELP = ++opIndex;
ZaOperation.REINDEX_MAILBOX = ++opIndex;
ZaOperation.LABEL = ++opIndex;