/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2007, 2009, 2010 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.3 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */
 
ZaCertsServerListView = function(parent) {
	ZaServerListView.call (this, parent);
	this._certInstallStatus = new DwtAlert (this) ;
	this._certInstallStatus.setIconVisible(false) ;
	
	//show the certInstallStatus at the top the list view
	var listEl = this.getHtmlElement() ;
	var certInstallStatusEl = this._certInstallStatus.getHtmlElement();
	listEl.insertBefore (certInstallStatusEl, listEl.firstChild) ;	
}

ZaCertsServerListView.prototype = new ZaServerListView;
ZaCertsServerListView.prototype.constructor = ZaCertsServerListView;

ZaCertsServerListView.prototype.toString = 
function() {
	return "ZaCertsServerListView";
}

ZaCertsServerListView.prototype.getTitle = 
function () {
	return com_zimbra_cert_manager.manage_certs_title ;
}

ZaCertsServerListView.prototype.getTabIcon =
function () {
	return "Server";
}

ZaCertsServerListView.prototype.set = 
function (list, defaultColumnSort) {
	DwtListView.prototype.set.call(this, list, defaultColumnSort);
	if (ZaCertWizard.INSTALL_STATUS < 0) {
		this._certInstallStatus.setDisplay (Dwt.DISPLAY_NONE) ;
	}else {
		this._certInstallStatus.setDisplay (Dwt.DISPLAY_BLOCK) ;
	}
}