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
* @class ZaMTAXFormView
* @contructor
* @param parent
* @param app
* @author Greg Solovyev
**/
function ZaMTAXFormView (parent, app) {
	ZaTabView.call(this, parent, app,"ZaMTAXFormView");	
		
	this.initForm(ZaMTA.myXModel,this.getMyXForm());
	this._localXForm.setController(this._app);
}

ZaMTAXFormView.prototype = new ZaTabView();
ZaMTAXFormView.prototype.constructor = ZaMTAXFormView;
ZaTabView.XFormModifiers["ZaMTAXFormView"] = new Array();
ZaMTAXFormView.TAB_INDEX=1;
	ZaMTAXFormView._tab1 = ZaMTAXFormView.TAB_INDEX++;
	ZaMTAXFormView._tab2 = ZaMTAXFormView.TAB_INDEX++;	
	ZaMTAXFormView._tab3 = ZaMTAXFormView.TAB_INDEX++;	
	ZaMTAXFormView._tab4 = ZaMTAXFormView.TAB_INDEX++;	
	ZaMTAXFormView._tab5 = ZaMTAXFormView.TAB_INDEX++;	

ZaMTAXFormView.tabChoices = new XFormChoices([{value:ZaMTAXFormView._tab1, label:ZaMsg.PQV_Tab_Deferred},
				{value:ZaMTAXFormView._tab2, label:ZaMsg.PQV_Tab_IncomingQ},
				{value:ZaMTAXFormView._tab3, label:ZaMsg.PQV_Tab_ActiveQ},
				{value:ZaMTAXFormView._tab4, label:ZaMsg.PQV_Tab_HoldQ},					
				{value:ZaMTAXFormView._tab5, label:ZaMsg.PQV_Tab_CorruptQ}],
				XFormChoices.OBJECT_LIST, "value", "label");

ZaMTAXFormView.prototype.setObject = 
function (entry) {
	this._containedObject = entry;
	
	if(!entry[ZaModel.currentTab])
		this._containedObject[ZaModel.currentTab] = "1";
	else
		this._containedObject[ZaModel.currentTab] = entry[ZaModel.currentTab];
		
	ZaMTAXFormView.tabChoices.setChoices([
		{value:ZaMTAXFormView._tab1, label:ZaMsg.PQV_Tab_Deferred + " (" + this._containedObject[ZaMTA.A_DeferredQ][ZaMTA.A_count] + ")"},
		{value:ZaMTAXFormView._tab2, label:ZaMsg.PQV_Tab_IncomingQ + " (" + this._containedObject[ZaMTA.A_IncomingQ][ZaMTA.A_count] + ")"},
				{value:ZaMTAXFormView._tab3, label:ZaMsg.PQV_Tab_ActiveQ + " (" + this._containedObject[ZaMTA.A_ActiveQ][ZaMTA.A_count] + ")"},
				{value:ZaMTAXFormView._tab4, label:ZaMsg.PQV_Tab_HoldQ + " (" + this._containedObject[ZaMTA.A_HoldQ][ZaMTA.A_count] + ")"},					
				{value:ZaMTAXFormView._tab5, label:ZaMsg.PQV_Tab_CorruptQ + " (" + this._containedObject[ZaMTA.A_CorruptQ][ZaMTA.A_count] + ")"}]),

	ZaMTAXFormView.tabChoices.dirtyChoices();
	this._localXForm.setInstance(this._containedObject);	
}

ZaMTAXFormView._listObjects = {};
/*
* This is too elaborate for now
*/
/*
ZaMTAXFormView.listSelectionListener = function (ev) {
	//register this list in the map, so that we can deselect it later
	if(ev.dwtObj && this.refPath) {
		ZaMTAXFormView._listObjects[this.refPath] = ev.dwtObj;
	}
	var instance = this.getInstance();
	var refParts = this.getRef().split("/");
	var filterName = refParts[1];
	var qName = refParts[0];
	if(!instance[qName]["_selection_cache"])
		instance[qName]["_selection_cache"] = {};

	instance[qName]["_selection_cache"][filterName] = this.widget.getSelection();
	
	//rebuild the query
	var query = [];
	query.push("mta:(");
	query.push(instance[ZaItem.A_zimbraId]);
	query.push(") ");
	query.push("queue:(");
	query.push(qName);	
	query.push(") ");
	for (var key in instance[qName]["_selection_cache"]) {
		query.push(key);
		query.push(":(");
		var arr = instance[qName]["_selection_cache"][key];
		if(arr) {
			var cnt = arr.length;			
			var subQuery = [];
			for(var i=0;i<cnt;i++) {
				subQuery.push(arr[i][ZaMTA.A_name]);
			}
			query.push(subQuery.join(","));
		}
		query.push(") ");		
	}

	var myPath = [qName,ZaMTA.A_query].join("/");
	if(query.length) 
		this.setInstanceValue(query.join(""),myPath);
	this.getForm().refresh();
}*/
	
ZaMTAXFormView.listSelectionListener = function (ev) {
	//register this list in the map, so that we can deselect it later
	if(ev.dwtObj && this.refPath) {
		ZaMTAXFormView._listObjects[this.refPath] = ev.dwtObj;
	}
	var instance = this.getInstance();
	var refParts = this.getRef().split("/");
	var filterName = refParts[1];
	var qName = refParts[0];
	instance[qName][ZaMTA.A_queue_filter_name] = filterName;
	instance[qName][ZaMTA.A_queue_filter_value] = this.widget.getSelection()[0].name;
	//deselect other lists
	for(var x in ZaMTAXFormView._listObjects) {
		if(x==this.refPath)
			continue;
		if(ZaMTAXFormView._listObjects[x]) {
			ZaMTAXFormView._listObjects[x].deselectAll();
		}
	}
}
				
ZaMTAXFormView.clearFilter = 
function (ev) {
	this.setInstanceValue("",this.getRef()+"/"+ZaMTA.A_queue_filter_name);
	this.setInstanceValue("",this.getRef()+"/"+ZaMTA.A_queue_filter_value);	
	this.getForm().refresh();
	for(var x in ZaMTAXFormView._listObjects) {
		if(ZaMTAXFormView._listObjects[x]) {
			ZaMTAXFormView._listObjects[x].deselectAll();
		}
	}
}

ZaMTAXFormView.showAllMsgs = function (ev) {
	ZaMTAXFormView.clearFilter.call(this,ev);
}

ZaMTAXFormView.myXFormModifier = function(xFormObject) {	
	xFormObject.tableCssStyle="width:100%;position:static;overflow:auto;";
	
	
	var headerList = new Array();
	headerList[0] = new ZaListHeaderItem(ZaMTA.A_name, ZaMsg.PQV_name_col, null, null, true, null, true, true);
	headerList[1] = new ZaListHeaderItem(ZaMTA.A_count, ZaMsg.PQV_count_col, null, "80px", true, null, true, true);
		
	var msgHeaderList = new Array();
	msgHeaderList[0] = new ZaListHeaderItem(ZaMTA.A_Qid, ZaMsg.PQV_qid_col, null, null, true, null, true, true);
	msgHeaderList[1] = new ZaListHeaderItem(ZaMTA.A_destination, ZaMsg.PQV_destination_col, null, null, true, null, true, true);
	msgHeaderList[2] = new ZaListHeaderItem(ZaMTA.A_origin, ZaMsg.PQV_origin_col, null, null, true, null, true, true);	

	xFormObject.items = [
		{type:_GROUP_, cssClass:"ZmSelectedHeaderBg", colSpan: "*", 
			items: [
				{type:_GROUP_,	numCols:4,colSizes:["32px","350px","100px","250px"],
					items: [
						{type:_AJX_IMAGE_, src:"Server_32", label:null, rowSpan:2},
						{type:_OUTPUT_, ref:ZaMTA.A_Servername, label:null,cssClass:"AdminTitle", rowSpan:2}
					]
				}
			],
			cssStyle:"padding-top:5px; padding-bottom:5px"
		},
		{type:_TAB_BAR_, ref:ZaModel.currentTab,
			relevantBehavior:_HIDE_,
			containerCssStyle: "padding-top:0px",
			choices:ZaMTAXFormView.tabChoices,
			cssClass:"ZaTabBar"
		},
		{type:_SWITCH_, items:[
				{type:_CASE_, numCols:1, width:"100%",/*colSizes:["10", "250","10","250","10"], */relevant:"instance[ZaModel.currentTab] == " + ZaMTAXFormView._tab1, 
					items:[	
						{type:_SPACER_, height:"15"},
						{type:_GROUP_, numCols:5, width:"95%", colSizes:["30%", "3%", "30%", "3%", "30%"],cssClass:"RadioGrouperBorder container", items: [						
						    {type:_GROUP_, colSpan:5, numCols:1, 
						   		items: [
									{type:_OUTPUT_, value:ZaMsg.PQV_Summary, cssClass:"RadioGrouperLabel", cssStyle:"z-index:"+(Dwt.Z_VIEW+1)}
								]
							},
							{type:_GROUP_, numCols:1,cssClass:"RadioGrouperBorder", tableCssClass:"que_table",  items: [
								   {type:_GROUP_, numCols:1, 
								   		items: [
											{type:_OUTPUT_, value:ZaMsg.PQV_GroupDestinationDomain, cssClass:"RadioGrouperLabel", cssStyle:"z-index:"+(Dwt.Z_VIEW+1)}
										]
									},
								    {ref:ZaMTA.A_DeferredQ+"/"+ZaMTA.A_destination, type:_DWT_LIST_, height:"200", width:"100%", cssClass: "DLSource", 
							   		forceUpdate: true, preserveSelection:true, multiselect:false,onSelection:ZaMTAXFormView.listSelectionListener, widgetClass:ZaQSummaryListView, headerList:headerList},								
								]
							},		
							{type:_CELLSPACER_},
							{type:_GROUP_, numCols:1,cssClass:"RadioGrouperBorder", tableCssClass:"que_table", items: [
								   {type:_GROUP_, numCols:1, 
								   		items: [
											{type:_OUTPUT_, value:ZaMsg.PQV_GroupOriginIP, cssClass:"RadioGrouperLabel", cssStyle:"z-index:"+(Dwt.Z_VIEW+1)}
										]
									},
								    {ref:ZaMTA.A_DeferredQ+"/"+ZaMTA.A_origin, type:_DWT_LIST_, height:"200", width:"100%", cssClass: "DLSource", 
							   		forceUpdate: true,preserveSelection:true, multiselect:false, onSelection:ZaMTAXFormView.listSelectionListener, widgetClass:ZaQSummaryListView, headerList:headerList},								
								]
							},		
							{type:_CELLSPACER_},
							{type:_GROUP_, numCols:1,cssClass:"RadioGrouperBorder", tableCssClass:"que_table", items: [
								   {type:_GROUP_, numCols:1, 
								   		items: [
											{type:_OUTPUT_, value:ZaMsg.PQV_GroupError, cssClass:"RadioGrouperLabel", cssStyle:"z-index:"+(Dwt.Z_VIEW+1)}
										]
									},
								    {ref:ZaMTA.A_DeferredQ+"/"+ZaMTA.A_error, type:_DWT_LIST_, height:"200", width:"100%", cssClass: "DLSource", 
							   		forceUpdate: true,multiselect:false, onSelection:ZaMTAXFormView.listSelectionListener, widgetClass:ZaQSummaryListView, headerList:headerList},								
								]
							}											
						]},
						{type:_SPACER_, height:"10"},
						/*
						* This is too elaborate for now
						*/
					/*	{type:_GROUP_, numCols:6, tableCssClass:"search_field_tableCssClass", cssClass:"qsearch_field_bar", width:"95%", items: [
							{type:_TEXTFIELD_, label:ZaMsg.PQ_searchQuery, ref:ZaMTA.A_DeferredQ+"/"+ZaMTA.A_query, width:"100%", containerCssClass:"search_field_container"},
							{type:_CELLSPACER_},
							{type:_DWT_BUTTON_, label:ZaMsg.PQ_SearchQ},
							{type:_CELLSPACER_},
							{type:_DWT_BUTTON_, label:ZaMsg.PQ_ClearFilter, ref:ZaMTA.A_DeferredQ+"/"+ZaMTA.A_query, onActivate:ZaMTAXFormView.clearFilter}							
						]},			
																					*/
						{type:_GROUP_, numCols:5, colSizes:["20%","20%","20%","20%","20%"],tableCssClass:"search_field_tableCssClass", cssClass:"qsearch_field_bar", width:"95%", items: [
							{type:_OUTPUT_, label:ZaMsg.PQ_QueueFilter, ref:ZaMTA.A_DeferredQ+"/"+ZaMTA.A_queue_filter_name},
							{type:_OUTPUT_, label:ZaMsg.PQ_QueueFilterVal, ref:ZaMTA.A_DeferredQ+"/"+ZaMTA.A_queue_filter_value},
							{type:_DWT_BUTTON_,ref:ZaMTA.A_DeferredQ, label:ZaMsg.PQ_ShowAll,onActivate:ZaMTAXFormView.showAllMsgs}
						]},																								
						{type:_GROUP_, numCols:1, width:"95%", cssClass:"RadioGrouperBorder container", tableCssClass:"que_table",  items: [
							   {type:_GROUP_, numCols:1, 
							   		items: [
										{type:_OUTPUT_, value:ZaMsg.PQV_Messages, cssClass:"RadioGrouperLabel", cssStyle:"z-index:"+(Dwt.Z_VIEW+1)}
									]
								},
							    {ref:ZaMTA.A_DeferredQ+"/"+ZaMTA.A_messages, type:_DWT_LIST_, height:"200", width:"100%", cssClass: "DLSource", 
						   		forceUpdate: true,multiselect:true, widgetClass:ZaQMessagesListView, headerList:msgHeaderList},								
							]
						}		
					]
				},							
				{type:_CASE_, numCols:1, width:"100%",/*colSizes:["10", "250","10","250","10"], */relevant:"instance[ZaModel.currentTab] == " + ZaMTAXFormView._tab2, 
					items:[	
						{type:_SPACER_, height:"15"},
						{type:_GROUP_, numCols:3, width:"95%", colSizes:["45%", "10%", "45%"],cssClass:"RadioGrouperBorder container", items: [						
						    {type:_GROUP_, colSpan:3, numCols:1, 
						   		items: [
									{type:_OUTPUT_, value:ZaMsg.PQV_Summary, cssClass:"RadioGrouperLabel"}
								]
							},
							{type:_GROUP_, numCols:1,cssClass:"RadioGrouperBorder", tableCssClass:"que_table",  items: [
								   {type:_GROUP_, numCols:1, 
								   		items: [
											{type:_OUTPUT_, value:ZaMsg.PQV_GroupDestinationDomain, cssClass:"RadioGrouperLabel"}
										]
									},
								    {ref:ZaMTA.A_IncomingQ+"/"+ZaMTA.A_destination, type:_DWT_LIST_, height:"200", width:"100%", cssClass: "DLSource", 
							   		forceUpdate: true, onSelection:ZaMTAXFormView.listSelectionListener, widgetClass:ZaQSummaryListView, headerList:headerList},								
								]
							},		
							{type:_CELLSPACER_},
							{type:_GROUP_, numCols:1,cssClass:"RadioGrouperBorder", tableCssClass:"que_table", items: [
								   {type:_GROUP_, numCols:1, 
								   		items: [
											{type:_OUTPUT_, value:ZaMsg.PQV_GroupOriginIP, cssClass:"RadioGrouperLabel"}
										]
									},
								    {ref:ZaMTA.A_IncomingQ+"/"+ZaMTA.A_origin, type:_DWT_LIST_, height:"200", width:"100%", cssClass: "DLSource", 
							   		forceUpdate: true, onSelection:ZaMTAXFormView.listSelectionListener, widgetClass:ZaQSummaryListView, headerList:headerList},								
								]
							}
											
						]},
						{type:_SPACER_, height:"10"},
						{type:_GROUP_, numCols:6, tableCssClass:"search_field_tableCssClass", cssClass:"qsearch_field_bar", width:"95%", items: [
							{type:_TEXTFIELD_, label:ZaMsg.PQ_searchQuery, ref:ZaMTA.A_IncomingQ+"/"+ZaMTA.A_query, width:"100%", containerCssClass:"search_field_container"},
							{type:_CELLSPACER_},
							{type:_DWT_BUTTON_, label:ZaMsg.PQ_SearchQ},
							{type:_CELLSPACER_},
							{type:_DWT_BUTTON_, label:ZaMsg.PQ_ClearFilter,ref:ZaMTA.A_IncomingQ+"/"+ZaMTA.A_query, onActivate:ZaMTAXFormView.clearFilter}							
						]},																	
						{type:_GROUP_, numCols:1, width:"95%", cssClass:"RadioGrouperBorder container", tableCssClass:"que_table",  items: [
							   {type:_GROUP_, numCols:1, 
							   		items: [
										{type:_OUTPUT_, value:ZaMsg.PQV_Messages, cssClass:"RadioGrouperLabel"}
									]
								},
							    {ref:ZaMTA.A_IncomingQ+"/"+ZaMTA.A_messages, type:_DWT_LIST_, height:"200", width:"100%", cssClass: "DLSource", 
						   		forceUpdate: true, widgetClass:ZaQMessagesListView, headerList:msgHeaderList},								
							]
						}		
					]
				}			
			]
		}
	]
};
ZaTabView.XFormModifiers["ZaMTAXFormView"].push(ZaMTAXFormView.myXFormModifier);