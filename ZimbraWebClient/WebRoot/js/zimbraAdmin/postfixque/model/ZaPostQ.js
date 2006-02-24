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
* @class ZaPostQ
* This class represents Postfix Queue object
* @author Greg Solovyev
* @contructor ZaPostQ
* @param app reference to the application instance
**/
function ZaPostQ(app) {
	ZaItem.call(this, app,"ZaServer");
	this._init(app);
}

ZaPostQ.prototype = new ZaItem;
ZaPostQ.prototype.constructor = ZaPostQ;
ZaItem.loadMethods["ZaPostQ"] = new Array();
ZaItem.initMethods["ZaPostQ"] = new Array();

ZaPostQ.A_Servername = "servername";
ZaPostQ.A_MailDropQ = "maildrop";
ZaPostQ.A_HoldQ = "hold";
ZaPostQ.A_IncommingQ = "incomming";
ZaPostQ.A_ActiveQ = "active";
ZaPostQ.A_DeferredQ = "deferred";