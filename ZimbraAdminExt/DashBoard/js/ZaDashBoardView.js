/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2005, 2006, 2007, 2008, 2009, 2010 Zimbra, Inc.
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

/**
* @class ZaDashBoardView
* @contructor ZaDashBoardView
* @param parent
* @author Greg Solovyev
**/
ZaDashBoardView = function(parent) {
	if (arguments.length == 0) return;
	ZaTabView.call(this, parent,"ZaDashBoardView");
	this.setScrollStyle(Dwt.SCROLL);
	this.initForm(new Object(), this.getMyXForm())
//	this._createHTML();
}
ZaDashBoardView.mainHelpPage = "administration_console_help.htm";
ZaDashBoardView.prototype = new ZaTabView();
ZaDashBoardView.prototype.constructor = ZaDashBoardView;
ZaTabView.XFormModifiers["ZaDashBoardView"] = new Array();

ZaDashBoardView.prototype.getTitle =
function () {
	return com_zimbra_dashboard.DashBoard_view_title;
}


ZaDashBoardView.myXFormModifier = function(xFormObject) {	
	xFormObject.tableCssStyle="width:100%;overflow:auto;";
	xFormObject.items = [
        {type:_TOP_GROUPER_, label:com_zimbra_dashboard.AddressesGrouper, id:"dashboard_addresses_group",
	    	colSpan: "*", numCols: 4, colSizes: ["200px","200px","200px","200px"],
	    	items:[
	    	    {type:_DWT_BUTTON_, label:com_zimbra_dashboard.ManageAddresses, width:80,icon:"Account",
	    	    	onActivate:"",enableDisableChecks:[],visibilityChecks:[]},
	    	    {type:_DWT_BUTTON_, label:com_zimbra_dashboard.NewAccount, width:80,icon:"Account",
	    	    	onActivate:"",enableDisableChecks:[],visibilityChecks:[]},
	    	    {type:_DWT_BUTTON_, label:com_zimbra_dashboard.NewDL, width:80,icon:"DistributionList",
	    	    	onActivate:"",enableDisableChecks:[],visibilityChecks:[]},
	    	    {type:_DWT_BUTTON_, label:com_zimbra_dashboard.NewCalResource, width:80,icon:"Resource",
	    	    	onActivate:"",enableDisableChecks:[],visibilityChecks:[]}
	    	]
        }

	];
	
}
ZaTabView.XFormModifiers["ZaDashBoardView"].push(ZaDashBoardView.myXFormModifier);