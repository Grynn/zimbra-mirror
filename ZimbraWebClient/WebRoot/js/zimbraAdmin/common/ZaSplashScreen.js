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

function ZaSplashScreen(shell, imageInfo, className) {
 	className = className || "ZaSplashScreen";
 	ZmBaseSplashScreen.call(this, shell, imageInfo, className);
}

ZaSplashScreen.prototype = new ZmBaseSplashScreen;
ZaSplashScreen.prototype.constructor = ZaSplashScreen;

ZaSplashScreen.prototype.getDefaultSubstitutions = function (){
	if (ZaSplashScreen._defaultSubstitutions == null) {
		ZaSplashScreen._defaultSubstitutions = {
			url:ZaMsg.splashScreenZimbraUrl,
			shortVersion: "BETA",
			appName: ZaMsg.splashScreenAppName,
			version: AjxBuffer.concat(ZaMsg.splashScreenVersion, " ", ZaServerVersionInfo.version , " " ,
									  AjxDateUtil.getTimeStr(ZaServerVersionInfo.buildDate,"%t %d %Y")),
			contents: AjxBuffer.concat("<br>", ZaMsg.splashScreenLoading, "<br><br>", AjxImg.getImageHtml("BarberPole_216")),
			license: ZaMsg.splashScreenCopyright	
		}
	}
	return ZaSplashScreen._defaultSubstitutions;
};
