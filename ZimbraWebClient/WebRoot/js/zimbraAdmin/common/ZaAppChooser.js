/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2005, 2006, 2007, 2009, 2010 VMware, Inc.
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

ZaAppChooser = function(parent, className, buttons) {

	className = className ? className : "ZaAppChooser";
	DwtToolBar.call(this, parent, className, Dwt.ABSOLUTE_STYLE, null, null, null, DwtToolBar.VERT_STYLE);
	this.TOOLTIP = new Object();
	this.TOOLTIP[ZaAppChooser.B_MONITORING]		= ZaMsg.goToMonitoring;
	this.TOOLTIP[ZaAppChooser.B_SYSTEM_CONFIG]	= ZaMsg.goToSystemConfig;
	this.TOOLTIP[ZaAppChooser.B_ADDRESSES]	= ZaMsg.goToAddresses;
/*	this.TOOLTIP[ZaAppChooser.B_DISTRIBUTION_LISTS]	= ZaMsg.goToDistributionLists;
	this.TOOLTIP[ZaAppChooser.B_COSES]	= ZaMsg.goToCoses;
	this.TOOLTIP[ZaAppChooser.B_DOMAINS]	= ZaMsg.goToDomains;
	this.TOOLTIP[ZaAppChooser.B_GLOBAL]	= ZaMsg.goToGlobalSettings;
	this.TOOLTIP[ZaAppChooser.B_SERVERS]	= ZaMsg.goToServers;
*/
	this.TOOLTIP[ZaAppChooser.B_HELP]		= ZaMsg.goToHelp;
	this.TOOLTIP[ZaAppChooser.B_MIGRATION_WIZ]	= ZaMsg.goToMigrationWiz;	
	this.TOOLTIP[ZaAppChooser.B_LOGOUT]		= ZaMsg.logOff;
	
	this.setScrollStyle(Dwt.CLIP);

	this._buttons = new Object();
	for (var i = 0; i < buttons.length; i++) {
		var id = buttons[i];
		if (id == ZaAppChooser.SEP) {
			this.addSpacer(ZaAppChooser.SEP_HEIGHT);
		} else {
			this._createButton(id);
		}
	}

}

var i = 1;
ZaAppChooser.OUTER		= i++;
ZaAppChooser.OUTER_ACT	= i++;
ZaAppChooser.OUTER_TRIG	= i++;

ZaAppChooser.SEP		= i++;

ZaAppChooser.B_MONITORING = i++;
ZaAppChooser.B_SYSTEM_CONFIG = i++;
ZaAppChooser.B_ADDRESSES = i++;
ZaAppChooser.B_HELP		= i++;
ZaAppChooser.B_LOGOUT	= i++;
ZaAppChooser.B_STATUS	= i++;
ZaAppChooser.B_STATS	= i++;
ZaAppChooser.B_ACCOUNTS	= i++;
ZaAppChooser.B_COSES	= i++;
ZaAppChooser.B_DOMAINS	= i++;
ZaAppChooser.B_SERVERS	= i++;
ZaAppChooser.B_GLOBAL	= i++;
ZaAppChooser.B_DISTRIBUTION_LISTS	= i++;
ZaAppChooser.B_MIGRATION_WIZ = i++;

ZaAppChooser.IMAGE = new Object();
ZaAppChooser.IMAGE[ZaAppChooser.OUTER]		= "ImgAppChiclet";
ZaAppChooser.IMAGE[ZaAppChooser.OUTER_ACT]	= "ImgAppChicletHover";
ZaAppChooser.IMAGE[ZaAppChooser.OUTER_TRIG]	= "ImgAppChicletSel";

ZaAppChooser.IMAGE[ZaAppChooser.B_MONITORING]	= "Status";
ZaAppChooser.IMAGE[ZaAppChooser.B_SYSTEM_CONFIG]	= "Server";
ZaAppChooser.IMAGE[ZaAppChooser.B_ADDRESSES]	= "Account";

ZaAppChooser.IMAGE[ZaAppChooser.B_STATUS]	= "Status";
ZaAppChooser.IMAGE[ZaAppChooser.B_STATS]	= "Statistics";
ZaAppChooser.IMAGE[ZaAppChooser.B_ACCOUNTS]	= "Account";
ZaAppChooser.IMAGE[ZaAppChooser.B_DISTRIBUTION_LISTS]	= "DistributionList";
ZaAppChooser.IMAGE[ZaAppChooser.B_COSES]	= "COS";
ZaAppChooser.IMAGE[ZaAppChooser.B_DOMAINS]	= "Domain";
ZaAppChooser.IMAGE[ZaAppChooser.B_SERVERS]	= "Server";
ZaAppChooser.IMAGE[ZaAppChooser.B_GLOBAL]	= "GlobalSettings";
ZaAppChooser.IMAGE[ZaAppChooser.B_HELP]	= "Help";
ZaAppChooser.IMAGE[ZaAppChooser.B_LOGOUT]	= "Logoff";
ZaAppChooser.IMAGE[ZaAppChooser.B_MIGRATION_WIZ]	= "MigrationWiz";

ZaAppChooser.SEP_HEIGHT = 10;

ZaAppChooser.prototype = new DwtToolBar;
ZaAppChooser.prototype.constructor = ZaAppChooser;

ZaAppChooser.prototype.toString = 
function() {
	return "ZaAppChooser";
}

ZaAppChooser.prototype.getButton =
function(id) {
	return this._buttons[id];
}

ZaAppChooser.prototype._createButton =
function(id) {
	var b = new ZaChicletButton(this, ZaAppChooser.IMAGE[ZaAppChooser.OUTER], ZaAppChooser.IMAGE[id]);
	b.setHoverImage(ZaAppChooser.IMAGE[ZaAppChooser.OUTER_ACT]);
	b.setActiveImage(ZaAppChooser.IMAGE[ZaAppChooser.OUTER_TRIG]);
	b.setToolTipContent(this.TOOLTIP[id]);
	b.setData(Dwt.KEY_ID, id);
	this._buttons[id] = b;
}
