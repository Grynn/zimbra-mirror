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
}

ZaStatusView.prototype.set = function (statusVector) {
	var instance = {services:statusVector, currentTab:1};
	if (ZaSettings.CLUSTER_MANEGEMENT_ENABLED) {
		instance.clusterStatus = ZaClusterStatus.getStatus();
		var arr = statusVector.getArray();
		var len = statusVector.size();
		var i;
		for (i = 0 ; i < len ; ++i) {
			var clusterSt = instance.clusterStatus.services[arr[i].serverName];
			if (clusterSt != null) {
				arr[i].clusterStatus = clusterSt.status;
			} else {
				delete arr[i].clusterStatus;
			}
		}
	}
	if (this._view == null) {
// 		var soapDoc = AjxSoapDoc.create("FailoverClusterServiceRequest", "urn:zimbraAdmin", null);
// 		var serviceEl = soapDoc.set("service");
// 		serviceEl.setAttribute("name", "mta");
// 		serviceEl.setAttribute("newServer", "fuddyduddy");
// 		// js response
// 		var resp = ZmCsfeCommand.invoke(soapDoc, null, null, null, false);

		this._view = new XForm(this.getXForm(), new XModel(ZaStatusView.XModel), instance, this);
		this._view.setController(this);
		this._view.draw();
	} else {
		this._view.setInstance(instance);
	}
};

// ZaStatusView.prototype.setBounds = function(x, y, width, height) {
// 	DwtControl.prototype.setBounds.call(this, x, y, width, height);
// 	DBG.println("setting my widths to ", width);
// 	//this._view.getHtmlElement().style.width = width;
// 	//this._view.getItemsById("services._array")[0].widget.getHtmlElement().style.width = width;
// };

ZaStatusView.prototype.getXForm = function () {
    if (this._xform == null) {
	this._xform = {
		width:"100%",
		tableCssStyle:"width:100%;",
	    itemDefaults:{

	    },
	    items:[
// 		   {type:_TAB_BAR_, choices:[ 
// 								{value:1, label:"Services Status"}, 
// 								{value:2, label:"Cluster Management"}
// 								], 
// 			ref: ZaModel.currentTab,colSpan:"*", value:1, relevant:"(ZaSettings.CLUSTER_MANEGEMENT_ENABLED)",
// 			XonChange:"this.getFormController().tabSwitched(event, this)"},
// 		   {type:_SWITCH_, useParentTable: true, colSpan:"*",
// 			items:[
// 			      {type:_CASE_, useParentTable:false, relevant:"instance[ZaModel.currentTab] == 1", colSpan:"*", numCols:2,
// 				   items:[
					{type:_SPACER_, height: 5},
					// This list is read only, so we can just do this little hack of accessing the vector's internal array.
					
					{ref: "services._array",type:_DWT_LIST_, colSpan:"*", widgetClass:ZaServicesListView,
					 relevant:"ZaSettings.CLUSTER_MANEGEMENT_ENABLED == false"},
					{ref: "services._array",type:_DWT_LIST_, colSpan:"*", widgetClass:ZaClusteredServicesListView,
					 relevant:"ZaSettings.CLUSTER_MANEGEMENT_ENABLED == true"}
// 					]
// 			      },			
// 			      {type:_CASE_, useParentTable:false, relevant:"instance[ZaModel.currentTab] == 2", colSpan:"*", numCols:2,
// 				   items:[
// 					 {type:_OUTPUT_ ,value :"CRAZY STUFF"}
// 					]
// 			      }
// 			     ]
// 		   }
 		  ]
	}
    }
    return this._xform;
};

// ZaStatusView.prototype.tabSwitched = function (event, formItem) {
// 	var instance = formItem.getForm().getInstance();
// 	if (instance[ZaModel.currentTab] == 2) {
// 		if (instance.clusterStatus == null) {
// 			instance.clusterStatus = this.getClusterStatus();
// 		}
// 	}
// };

// ZaStatusView.prototype.getClusterStatus = function () {
	
// 	var soapDoc = AjxSoapDoc.create("GetClusterStatusRequest", "urn:zimbraAdmin", null);
// 	// js response
// 	var resp = ZmCsfeCommand.invoke(soapDoc, null, null, null, false);
	
// 	return resp;
// };

ZaStatusView.XModel = {
	items:[
	{id:"services" ,type:_UNTYPED_},
	{id:"clusterStatus", type:_UNTYPED_}
	]
};
