/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Server
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
 
ZaCertsServerListView = function(parent) {
	ZaServerListView.call (this, parent);
}

ZaCertsServerListView.prototype = new ZaServerListView;
ZaCertsServerListView.prototype.constructor = ZaCertsServerListView;

ZaCertsServerListView.prototype.toString = 
function() {
	return "ZaCertsServerListView";
}

ZaCertsServerListView.prototype.getTitle = 
function () {
	return zimbra_cert_manager.manage_certs_title ;
}

ZaCertsServerListView.prototype.getTabIcon =
function () {
	return "Server";
}