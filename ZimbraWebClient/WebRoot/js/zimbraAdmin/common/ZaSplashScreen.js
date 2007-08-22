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
 * Portions created by Zimbra are Copyright (C) 2005, 2006, 2007 Zimbra, Inc.
 * All Rights Reserved.
 * 
 * Contributor(s):
 * 
 * ***** END LICENSE BLOCK *****
 */

ZaSplashScreen = function(shell, imageInfo, className) {
 	className = className || "ZaSplashScreen";
 	ZmBaseSplashScreen.call(this, shell, imageInfo, className);
}

ZaSplashScreen.prototype = new ZmBaseSplashScreen;
ZaSplashScreen.prototype.constructor = ZaSplashScreen;

ZaSplashScreen.prototype.getHtml = 
function() {
	var params = ZLoginFactory.copyDefaultParams(ZaMsg);
	params.showForm = false;
	params.showLicenseMsg = true;
	params.showLoading = true;
	return ZLoginFactory.getLoginDialogHTML(params);
};
