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
* @class ZaStatusView displays status page
* @contructor ZaStatusView
* @param parent
* @param app
* @author Roland Schemers
* @author Greg Solovyev
**/
function ZaStatusView(parent, app) {
	this._app = app;
	DwtComposite.call(this, parent, "ZaStatusView", DwtControl.ABSOLUTE_STYLE);
}

ZaStatusView.prototype = new DwtComposite;
ZaStatusView.prototype.constructor = ZaStatusView;

ZaStatusView.prototype.toString = 
function() {
	return "ZaStatusView";
};

ZaStatusView.prototype.setBounds = function (x, y, width, height) {

	// 10 is the hieght of the spacer above the lists.
	height = height - 10;

	DwtControl.prototype.setBounds.call(this, x , y, width, height);

	var _clusterList = this._view.getItemsById('clusterList');
	if(_clusterList) {
		var clusterList = _clusterList[0];
		if (clusterList != null && clusterList.widget != null) {
			clusterList.widget.setSize(width,height);
		};
	}
	var _nonClusterList = this._view.getItemsById('nonClusterList');
	if(_nonClusterList) {		
		var nonClusterList = _nonClusterList[0];
		if (nonClusterList != null && nonClusterList.widget != null) {
			try {	
				nonClusterList.widget.setSize(width);
			} catch (ex) {
				//	throw ex;
				//swallow this invalid argument exception from IE, bug 4441
			}
		};
	}	
};

ZaStatusView.prototype.set = function (statusVector, globalConfig) {
	// TODO  -- make a more appealing data structure here.
	var instance = {services:statusVector, currentTab:1};
	instance.globalConfig = globalConfig;
	if (this._view == null) {
		this._view = new XForm(this.getXForm(), new XModel(ZaStatusView.XModel), instance, this);
		this._view.setController(this);
		this._view.draw();
	} else {
		var cl = this._view.getItemsById('clusterList')[0];
		if (cl != null) {
			cl.dirtyDisplay();
		}
		var l = this._view.getItemsById('nonClusterList')[0];
		if (l != null ) {
			cl.dirtyDisplay();
		}
		this._view.setInstance(instance);
	}
};

ZaStatusView.prototype.addClusterSelectionListener = function (listener) {
	var item = this._view.getItemsById('clusterList')[0]
	if (item.widget) {
		item.widget.addSelectionListener(listener);
	}
};


ZaStatusView.prototype.getXForm = function () {
    if (this._xform == null) {
	this._xform = {
	    width:"100%",
	    tableCssStyle:"width:100%;xheight:100%;",
	    itemDefaults:{
		
	    },//TODO: move cluster code to an external file
	    items:[
		   {type:_SPACER_, height: 5},
		   // This list is read only, so we can just do this little hack of accessing the vector's internal array.
		   {ref: "services._array", id:"nonClusterList", type:_DWT_LIST_, colSpan:"*", 
			widgetClass:ZaServicesListView,containerCssStyle:"height:100%",
			relevant:"AjxUtil.isUndefined(instance.globalConfig.attrs[ZaGlobalConfig.A_zimbraComponentAvailable_cluster])"},
		   {ref: "services._array",id:"clusterList",type:_DWT_LIST_, colSpan:"*", widgetClass:ZaClusteredServicesListView,
			containerCssStyle:"height:100%",
			relevant:"AjxUtil.isSpecified(instance.globalConfig.attrs[ZaGlobalConfig.A_zimbraComponentAvailable_cluster])"}
 		  ]
	}
    }
    return this._xform;
};

ZaStatusView.prototype.getSelection = function () {
	return this._view.getItemsById('clusterList')[0].getSelection();
};

ZaStatusView.XModel = {
	items:[
	{id:"services" ,type:_UNTYPED_},
	{id:"clusterStatus", type:_UNTYPED_}
	]
};
