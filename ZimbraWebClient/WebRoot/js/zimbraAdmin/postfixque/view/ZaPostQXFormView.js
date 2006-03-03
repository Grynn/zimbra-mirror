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
* @class ZaPostQXFormView
* @contructor
* @param parent
* @param app
* @author Greg Solovyev
**/
function ZaPostQXFormView (parent, app) {
	ZaTabView.call(this, parent, app,"ZaPostQXFormView");	
		
	this.initForm(ZaPostQ.myXModel,this.getMyXForm());
	this._localXForm.setController(this._app);
}

ZaPostQXFormView.prototype = new ZaTabView();
ZaPostQXFormView.prototype.constructor = ZaPostQXFormView;
ZaTabView.XFormModifiers["ZaPostQXFormView"] = new Array();
ZaPostQXFormView.TAB_INDEX=1;
	ZaPostQXFormView._tab1 = ZaPostQXFormView.TAB_INDEX++;
	ZaPostQXFormView._tab2 = ZaPostQXFormView.TAB_INDEX++;	
	ZaPostQXFormView._tab3 = ZaPostQXFormView.TAB_INDEX++;	
	ZaPostQXFormView._tab4 = ZaPostQXFormView.TAB_INDEX++;	
	ZaPostQXFormView._tab5 = ZaPostQXFormView.TAB_INDEX++;	

ZaPostQXFormView.tabChoices = new XFormChoices([{value:ZaPostQXFormView._tab1, label:ZaMsg.PQV_Tab_Deferred},
				{value:ZaPostQXFormView._tab2, label:ZaMsg.PQV_Tab_IncomingQ},
				{value:ZaPostQXFormView._tab3, label:ZaMsg.PQV_Tab_ActiveQ},
				{value:ZaPostQXFormView._tab4, label:ZaMsg.PQV_Tab_HoldQ},					
				{value:ZaPostQXFormView._tab5, label:ZaMsg.PQV_Tab_CorruptQ}],
				XFormChoices.OBJECT_LIST, "value", "label");

ZaPostQXFormView.prototype.setObject = 
function (entry) {
	this._containedObject = entry;
	
	if(!entry[ZaModel.currentTab])
		this._containedObject[ZaModel.currentTab] = "1";
	else
		this._containedObject[ZaModel.currentTab] = entry[ZaModel.currentTab];
		
	ZaPostQXFormView.tabChoices.setChoices([
		{value:ZaPostQXFormView._tab1, label:ZaMsg.PQV_Tab_Deferred + " (" + this._containedObject[ZaPostQ.A_DeferredQ][ZaPostQ.A_count] + ")"},
		{value:ZaPostQXFormView._tab2, label:ZaMsg.PQV_Tab_IncomingQ + " (" + this._containedObject[ZaPostQ.A_IncomingQ][ZaPostQ.A_count] + ")"},
				{value:ZaPostQXFormView._tab3, label:ZaMsg.PQV_Tab_ActiveQ + " (" + this._containedObject[ZaPostQ.A_ActiveQ][ZaPostQ.A_count] + ")"},
				{value:ZaPostQXFormView._tab4, label:ZaMsg.PQV_Tab_HoldQ + " (" + this._containedObject[ZaPostQ.A_HoldQ][ZaPostQ.A_count] + ")"},					
				{value:ZaPostQXFormView._tab5, label:ZaMsg.PQV_Tab_CorruptQ + " (" + this._containedObject[ZaPostQ.A_CorruptQ][ZaPostQ.A_count] + ")"}]),

	ZaPostQXFormView.tabChoices.dirtyChoices();
	this._localXForm.setInstance(this._containedObject);	
}


ZaPostQXFormView.listSelectionListener = function (ev) {
	var instance = this.getInstance();
	var refParts = this.getRef().split("/");
	var filterName = refParts[1];
	var qName = refParts[0];
	if(!instance[qName]["_selection_cache"])
		instance[qName]["_selection_cache"] = {};

	instance[qName]["_selection_cache"][filterName] = this.widget.getSelection();
	
	//rebuild the query
	var query = [];
	for (var key in instance[qName]["_selection_cache"]) {
		query.push(key);
		query.push(":(");
		var arr = instance[qName]["_selection_cache"][key];
		if(arr) {
			var cnt = arr.length;			
			var subQuery = [];
			for(var i=0;i<cnt;i++) {
				subQuery.push(arr[i][ZaPostQ.A_name]);
			}
			query.push(subQuery.join(","));
		}
		query.push(") ");		
	}

	var myPath = [qName,ZaPostQ.A_query].join("/");
	if(query.length) 
		this.setInstanceValue(query.join(""),myPath);
	this.getForm().refresh();
}
				
ZaPostQXFormView.myXFormModifier = function(xFormObject) {	
	xFormObject.tableCssStyle="width:100%;position:static;overflow:auto;";
	
	
	var headerList = new Array();
	headerList[0] = new ZaListHeaderItem(ZaPostQ.A_name, ZaMsg.PQV_name_col, null, null, true, null, true, true);
	headerList[1] = new ZaListHeaderItem(ZaPostQ.A_count, ZaMsg.PQV_count_col, null, "80px", true, null, true, true);
		
	var msgHeaderList = new Array();
	msgHeaderList[0] = new ZaListHeaderItem(ZaPostQ.A_Qid, ZaMsg.PQV_qid_col, null, null, true, null, true, true);
	msgHeaderList[1] = new ZaListHeaderItem(ZaPostQ.A_destination, ZaMsg.PQV_destination_col, null, null, true, null, true, true);
	msgHeaderList[2] = new ZaListHeaderItem(ZaPostQ.A_origin, ZaMsg.PQV_origin_col, null, null, true, null, true, true);	

	xFormObject.items = [
		{type:_GROUP_, cssClass:"ZmSelectedHeaderBg", colSpan: "*", 
			items: [
				{type:_GROUP_,	numCols:4,colSizes:["32px","350px","100px","250px"],
					items: [
						{type:_AJX_IMAGE_, src:"Server_32", label:null, rowSpan:2},
						{type:_OUTPUT_, ref:ZaPostQ.A_Servername, label:null,cssClass:"AdminTitle", rowSpan:2}
					]
				}
			],
			cssStyle:"padding-top:5px; padding-bottom:5px"
		},
		{type:_TAB_BAR_, ref:ZaModel.currentTab,
			relevantBehavior:_HIDE_,
			containerCssStyle: "padding-top:0px",
			choices:ZaPostQXFormView.tabChoices,
			cssClass:"ZaTabBar"
		},
		{type:_SWITCH_, items:[
				{type:_CASE_, numCols:1, width:"100%",/*colSizes:["10", "250","10","250","10"], */relevant:"instance[ZaModel.currentTab] == " + ZaPostQXFormView._tab1, 
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
								    {ref:ZaPostQ.A_DeferredQ+"/"+ZaPostQ.A_destination, type:_DWT_LIST_, height:"200", width:"100%", cssClass: "DLSource", 
							   		forceUpdate: true, onSelection:ZaPostQXFormView.listSelectionListener, widgetClass:ZaQSummaryListView, headerList:headerList},								
								]
							},		
							{type:_CELLSPACER_},
							{type:_GROUP_, numCols:1,cssClass:"RadioGrouperBorder", tableCssClass:"que_table", items: [
								   {type:_GROUP_, numCols:1, 
								   		items: [
											{type:_OUTPUT_, value:ZaMsg.PQV_GroupOriginIP, cssClass:"RadioGrouperLabel", cssStyle:"z-index:"+(Dwt.Z_VIEW+1)}
										]
									},
								    {ref:ZaPostQ.A_DeferredQ+"/"+ZaPostQ.A_origin, type:_DWT_LIST_, height:"200", width:"100%", cssClass: "DLSource", 
							   		forceUpdate: true, onSelection:ZaPostQXFormView.listSelectionListener, widgetClass:ZaQSummaryListView, headerList:headerList},								
								]
							},		
							{type:_CELLSPACER_},
							{type:_GROUP_, numCols:1,cssClass:"RadioGrouperBorder", tableCssClass:"que_table", items: [
								   {type:_GROUP_, numCols:1, 
								   		items: [
											{type:_OUTPUT_, value:ZaMsg.PQV_GroupError, cssClass:"RadioGrouperLabel", cssStyle:"z-index:"+(Dwt.Z_VIEW+1)}
										]
									},
								    {ref:ZaPostQ.A_DeferredQ+"/"+ZaPostQ.A_error, type:_DWT_LIST_, height:"200", width:"100%", cssClass: "DLSource", 
							   		forceUpdate: true, onSelection:ZaPostQXFormView.listSelectionListener, widgetClass:ZaQSummaryListView, headerList:headerList},								
								]
							}											
						]},
						{type:_SPACER_, height:"10"},
						{type:_GROUP_, numCols:6, tableCssClass:"search_field_tableCssClass", cssClass:"qsearch_field_bar", width:"95%", items: [
							{type:_TEXTFIELD_, label:ZaMsg.PQ_searchQuery, ref:ZaPostQ.A_DeferredQ+"/"+ZaPostQ.A_query, width:"100%", containerCssClass:"search_field_container"},
							{type:_CELLSPACER_},
							{type:_DWT_BUTTON_, label:ZaMsg.PQ_SearchQ},
							{type:_CELLSPACER_},
							{type:_DWT_BUTTON_, label:ZaMsg.PQ_ClearFilter}							
						]},																		
						{type:_GROUP_, numCols:1, width:"95%", cssClass:"RadioGrouperBorder container", tableCssClass:"que_table",  items: [
							   {type:_GROUP_, numCols:1, 
							   		items: [
										{type:_OUTPUT_, value:ZaMsg.PQV_Messages, cssClass:"RadioGrouperLabel", cssStyle:"z-index:"+(Dwt.Z_VIEW+1)}
									]
								},
							    {ref:ZaPostQ.A_DeferredQ+"/"+ZaPostQ.A_messages, type:_DWT_LIST_, height:"200", width:"100%", cssClass: "DLSource", 
						   		forceUpdate: true, widgetClass:ZaQSummaryListView, headerList:msgHeaderList},								
							]
						}		
					]
				},							
				{type:_CASE_, numCols:1, width:"100%",/*colSizes:["10", "250","10","250","10"], */relevant:"instance[ZaModel.currentTab] == " + ZaPostQXFormView._tab2, 
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
								    {ref:ZaPostQ.A_IncomingQ+"/"+ZaPostQ.A_destination, type:_DWT_LIST_, height:"200", width:"100%", cssClass: "DLSource", 
							   		forceUpdate: true, onSelection:ZaPostQXFormView.listSelectionListener, widgetClass:ZaQSummaryListView, headerList:headerList},								
								]
							},		
							{type:_CELLSPACER_},
							{type:_GROUP_, numCols:1,cssClass:"RadioGrouperBorder", tableCssClass:"que_table", items: [
								   {type:_GROUP_, numCols:1, 
								   		items: [
											{type:_OUTPUT_, value:ZaMsg.PQV_GroupOriginIP, cssClass:"RadioGrouperLabel"}
										]
									},
								    {ref:ZaPostQ.A_IncomingQ+"/"+ZaPostQ.A_origin, type:_DWT_LIST_, height:"200", width:"100%", cssClass: "DLSource", 
							   		forceUpdate: true, onSelection:ZaPostQXFormView.listSelectionListener, widgetClass:ZaQSummaryListView, headerList:headerList},								
								]
							}
											
						]},
						{type:_SPACER_, height:"10"},
						{type:_GROUP_, numCols:6, tableCssClass:"search_field_tableCssClass", cssClass:"qsearch_field_bar", width:"95%", items: [
							{type:_TEXTFIELD_, label:ZaMsg.PQ_searchQuery, ref:ZaPostQ.A_IncomingQ+"/"+ZaPostQ.A_query, width:"100%", containerCssClass:"search_field_container"},
							{type:_CELLSPACER_},
							{type:_DWT_BUTTON_, label:ZaMsg.PQ_SearchQ},
							{type:_CELLSPACER_},
							{type:_DWT_BUTTON_, label:ZaMsg.PQ_ClearFilter}							
						]},																	
						{type:_GROUP_, numCols:1, width:"95%", cssClass:"RadioGrouperBorder container", tableCssClass:"que_table",  items: [
							   {type:_GROUP_, numCols:1, 
							   		items: [
										{type:_OUTPUT_, value:ZaMsg.PQV_Messages, cssClass:"RadioGrouperLabel"}
									]
								},
							    {ref:ZaPostQ.A_IncomingQ+"/"+ZaPostQ.A_messages, type:_DWT_LIST_, height:"200", width:"100%", cssClass: "DLSource", 
						   		forceUpdate: true, widgetClass:ZaQSummaryListView, headerList:msgHeaderList},								
							]
						}		
					]
				}			
			]
		}
	]
};
ZaTabView.XFormModifiers["ZaPostQXFormView"].push(ZaPostQXFormView.myXFormModifier);