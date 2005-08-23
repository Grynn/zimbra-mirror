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

function ZaOperation(id, caption, tooltip, imgId, disImgId, lsnr) {
	this.id = id;
	this.caption = caption;
	this.tt = tooltip;
	this.listener = lsnr;
	this.imageId = imgId;
	this.disImageId = disImgId;
}

ZaOperation.prototype.toString = 
function() {
		return "ZaOperation";
}

// Operations
ZaOperation.NONE = -2;		// no operations or menu items
ZaOperation.SEP = -1;		// separator
ZaOperation.NEW = 1;
ZaOperation.DELETE = 2;
ZaOperation.REFRESH = 3;
ZaOperation.EDIT = 4;
ZaOperation.CHNG_PWD = 5;
ZaOperation.CLOSE = 6;
ZaOperation.SAVE = 7;
ZaOperation.NEW_WIZARD = 8;
ZaOperation.PAGE_FORWARD = 9;
ZaOperation.PAGE_BACK = 10;
ZaOperation.DUPLICATE = 11;
ZaOperation.GAL_WIZARD = 12;
ZaOperation.AUTH_WIZARD =13;
ZaOperation.VIEW_MAIL =14;
ZaOperation.MAIL_RESTORE = 15;