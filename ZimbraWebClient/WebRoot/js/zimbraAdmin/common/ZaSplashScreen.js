/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2005, 2006, 2007, 2008, 2009, 2010, 2011, 2012, 2013 Zimbra Software, LLC.
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

ZaSplashScreen =function(parent) {
    var className = "LoginScreen";
    DwtComposite.call(this, {parent:parent, className:className, posStyle:DwtControl.ABSOLUTE_STYLE});
    this._origClassName = className;
    this._xparentClassName = className + "-Transparent";
    this.setBounds(0, 0, "100%", "100%");
    var htmlElement = this.getHtmlElement();
    htmlElement.style.zIndex = Dwt.Z_SPLASH;
    htmlElement.className = className;
    this.setVisible(false);
    
	var params = ZLoginFactory.copyDefaultParams(ZaMsg);
	params.showPanelBorder = true;
	params.showForm = true;
	params.showUserField =false ;
	params.showPasswordField = false;
	params.showRememberMeCheckbox = false;
	params.showLogOff = false;
	params.showButton = false;
    params.showLoading = true ;
    params.companyURL = ZaAppCtxt.getLogoURI () ;
    params.copyrightText = ZaItem.getSplashScreenCopyright();
    params.clientLevelNotice = ZabMsg.clientLevelNotice ? ZabMsg.clientLevelNotice :"";
    var html = ZLoginFactory.getLoginDialogHTML(params);
	this.setContent(html);
}

ZaSplashScreen.prototype = new DwtComposite;
ZaSplashScreen.prototype.constructor = ZaSplashScreen;
ZaSplashScreen.prototype.toString = 
function() {
	return "ZaSplashScreen";
}
