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
	this._localXForm.addListener(DwtEvent.XFORMS_FORM_DIRTY_CHANGE, new AjxListener(this, ZaMTAXFormView.prototype.handleXFormChange));	
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
	ZaMTAXFormView.prototype.handleXFormChange.call(this);
}
ZaMTAXFormView.prototype.handleXFormChange = function () {
	if(this._containedObject[ZaModel.currentTab] == "1" && (this._containedObject[ZaMTA.A_DeferredQ][ZaMTA.A_Status]==ZaMTA.STATUS_IDLE)) {
		this._containedObject.getMailQStatus(ZaMTA.A_DeferredQ, "", 0,null,true);	
	}
	if(this._containedObject[ZaModel.currentTab] == "2" && (this._containedObject[ZaMTA.A_IncomingQ][ZaMTA.A_Status]==ZaMTA.STATUS_IDLE)) {
		this._containedObject.getMailQStatus(ZaMTA.A_IncomingQ, "", 0,null,true);	
	}
	if(this._containedObject[ZaModel.currentTab] == "3" && (this._containedObject[ZaMTA.A_ActiveQ][ZaMTA.A_Status]==ZaMTA.STATUS_IDLE)) {
		this._containedObject.getMailQStatus(ZaMTA.A_ActiveQ, "", 0,null,true);	
	}
	if(this._containedObject[ZaModel.currentTab] == "4" && (this._containedObject[ZaMTA.A_HoldQ][ZaMTA.A_Status]==ZaMTA.STATUS_IDLE)) {
		this._containedObject.getMailQStatus(ZaMTA.A_HoldQ, "", 0,null,true);	
	}
	if(this._containedObject[ZaModel.currentTab] == "5" && (this._containedObject[ZaMTA.A_CorruptQ][ZaMTA.A_Status]==ZaMTA.STATUS_IDLE)) {
		this._containedObject.getMailQStatus(ZaMTA.A_CorruptQ, "", 0,null,true);	
	}				
}

ZaMTAXFormView._listObjects = {};
/*
* Elaborate query builder
*/
ZaMTAXFormView.listSelectionListener = function (ev) {
	//register this list in the map, so that we can deselect it later
	if(ev.dwtObj && this.refPath) {
		ZaMTAXFormView._listObjects[this.refPath] = ev.dwtObj;
	}
	var instance = this.getInstance();
	var refParts = this.getRef().split("/");
	var filterName = refParts[1];
	var qName = refParts[0];
	if(!instance[qName][ZaMTA.A_selection_cache])
		instance[qName][ZaMTA.A_selection_cache] = {};

	instance[qName][ZaMTA.A_selection_cache][filterName] = this.widget.getSelection();
	
	//rebuild the query
	var query = [];
/*	query.push("mta:(");
	query.push(instance[ZaItem.A_zimbraId]);
	query.push(") ");*/
/*	query.push("queue:(");
	query.push(qName);	
	query.push(") ");*/
	var joinStr = "\" OR \"";
	for (var key in instance[qName][ZaMTA.A_selection_cache]) {
		var arr = instance[qName][ZaMTA.A_selection_cache][key];
		if(arr) {
			var cnt = arr.length;			
			if(cnt>0) {
				query.push(key);
				query.push(":(\"");
				var subQuery = [];
				for(var i=0;i<cnt;i++) {
					subQuery.push(ZaMTA.luceneEscape(arr[i][ZaMTAQSummaryItem.A_text]));
				}
				query.push(subQuery.join(joinStr));
				query.push("\")");
				query.push(" AND ");				
			}
		}
		//query.push("\"");	
	}
	query.pop(); //remove the last AND this is a little dumm
	var myPath = [qName,ZaMTA.A_query].join("/");
	if(query.length) 
		this.setInstanceValue(query.join(""),myPath);
		
	this.getForm().refresh();
	
	instance.getMailQStatus(qName, instance[qName][ZaMTA.A_query]);	
}
/*
//Simple query builder
ZaMTAXFormView.listSelectionListener = function (ev) {
	//register this list in the map, so that we can deselect it later
	if(ev.dwtObj && this.refPath) {
		ZaMTAXFormView._listObjects[this.refPath] = ev.dwtObj;
	}
	var instance = this.getInstance();
	var refParts = this.getRef().split("/");
	var filterName = refParts[1];
	var qName = refParts[0];
	var filterVal = this.widget.getSelection()[0][ZaMTAQSummaryItem.A_text];
	instance[qName][ZaMTA.A_queue_filter_name] = filterName;
	instance[qName][ZaMTA.A_queue_filter_value] = filterVal;
	//deselect other lists
	for(var x in ZaMTAXFormView._listObjects) {
		if(x==this.refPath)
			continue;
		if(ZaMTAXFormView._listObjects[x]) {
			ZaMTAXFormView._listObjects[x].deselectAll();
		}
	}
	instance.getMailQStatus(qName, filterName+":"+filterVal);	
}*/

ZaMTAXFormView.searchQueue = function (ev) {
	var instance = this.getInstance();
	var qName = this.getRef();
	var query = instance[qName][ZaMTA.A_query];
	instance.getMailQStatus(qName, query);	
	
}
			
ZaMTAXFormView.clearFilter = 
function (ev) {
//	this.setInstanceValue("",this.getRef()+"/"+ZaMTA.A_queue_filter_name);
//	this.setInstanceValue("",this.getRef()+"/"+ZaMTA.A_queue_filter_value);	
	this.setInstanceValue("",this.getRef()+"/"+ZaMTA.A_selection_cache);	

	this.setInstanceValue("",this.getRef()+"/"+ZaMTA.A_query);		
	this.getForm().refresh();
	for(var x in ZaMTAXFormView._listObjects) {
		if(ZaMTAXFormView._listObjects[x]) {
			ZaMTAXFormView._listObjects[x].deselectAll();
		}
	}
}

ZaMTAXFormView.showAllMsgs = function (ev) {
	ZaMTAXFormView.clearFilter.call(this,ev);
	ZaMTAXFormView.searchQueue.call(this,ev);
}

ZaMTAXFormView.deleteButtonListener = function (ev) {
	var qName, field, dlgMsg;
	if(this.xFormItem) {
		var refParts = this.xFormItem.getRef().split("/");
		qName = refParts[0];
		if(refParts.length > 1)
			field = refParts[1];
	} else {
		qName = this.getRef();
	}	

	var app = this.xFormItem.getForm().getController();
	this.removeConfirmMessageDialog = new ZaMsgDialog(app.getAppCtxt().getShell(), null, [DwtDialog.YES_BUTTON, DwtDialog.NO_BUTTON], app);			
		
	if(!field) {
		var obj = new Object();
		obj[ZaMTAQMsgItem.A_id] = ZaMTA.ID_ALL;
		this._removeList = [obj];
		dlgMsg = String(ZaMsg.Q_PQ_DELETE_ALL_IN_QUEUE).replace("{0}", qName);
		this.removeConfirmMessageDialog.registerCallback(DwtDialog.YES_BUTTON, ZaMTAXFormView.deleteMsgsByIDCallback, this);
	} else {
		this._removeList = new Array();
		if(this.getSelectionCount()>0) {
			this._removeList = this.getSelection();
			//var cnt = arrItems.length;
		}
		if(this._removeList.length) {
			dlgMsg = ZaMsg.Q_DELETE_OBJECTS;
			dlgMsg +=  "<br><ul>";
			var i=0;
			for(var key in this._removeList) {
				if(i > 19) {
					dlgMsg += "<li>...</li>";
					break;
				}
				dlgMsg += "<li>";
				if(field == ZaMTA.A_messages) {
					dlgMsg += this._removeList[i][ZaMTAQMsgItem.A_id];
				} else {
					dlgMsg += (field + " ");
					dlgMsg += this._removeList[i][ZaMTAQSummaryItem.A_text];
					dlgMsg += (" (" + this._removeList[i][ZaMTAQSummaryItem.A_count] + " " + ZaMsg.messages + ")");
				}
				dlgMsg += "</li>";
				i++;
			}
			dlgMsg += "</ul>";
			if(field == ZaMTA.A_messages) {
				this.removeConfirmMessageDialog.registerCallback(DwtDialog.YES_BUTTON, ZaMTAXFormView.deleteMsgsByIDCallback, this);
			} else {
				this.removeConfirmMessageDialog.registerCallback(DwtDialog.YES_BUTTON, ZaMTAXFormView.deleteMsgsByQueryCallback, this);
			}
		}
	}
	if(dlgMsg) {
		this.removeConfirmMessageDialog.setMessage(dlgMsg,  DwtMessageDialog.INFO_STYLE);
	}
	this.removeConfirmMessageDialog.registerCallback(DwtDialog.NO_BUTTON, ZaMTAXFormView.donotDeleteMsgsCallback, this);		
	this.removeConfirmMessageDialog.popup();	
}

ZaMTAXFormView.deleteMsgsByIDCallback = function (ev) {
	var arr = [];
	for(var key in this._removeList) {
		arr.push(this._removeList[key][ZaMTAQMsgItem.A_id])
	}
	if(arr.length > 0) {
		var instance = this.xFormItem.getInstance();
		var refParts = this.xFormItem.getRef().split("/");
		var qName = refParts[0];
		instance.mailQueueAction(qName, "delete", "id", arr.join(","));
	}
	this.removeConfirmMessageDialog.popdown();
}

ZaMTAXFormView.deleteMsgsByQueryCallback = function (ev) {
	var arr = [];
	var joinStr = "\" OR \"";
	for(var key in this._removeList) {
		arr.push(ZaMTA.luceneEscape(this._removeList[key][ZaMTAQSummaryItem.A_text]))
	}
	if(arr.length > 0) {
		var query = "";
		var instance = this.xFormItem.getInstance();
		var refParts = this.xFormItem.getRef().split("/");
		var qName = refParts[0];
		var filterName = refParts[1];
		query = filterName + ":(\"" + arr.join(joinStr) + "\")";
		instance.mailQueueAction(qName, "delete", "query", query);
	}
	this.removeConfirmMessageDialog.popdown();
}

ZaMTAXFormView.donotDeleteMsgsCallback = function () {
	if(this.removeConfirmMessageDialog)
		this.removeConfirmMessageDialog.popdown();
}

ZaMTAXFormView.requeueButtonListener = function (ev) {
	var qName, field, instance;
	if(this.xFormItem) {
		var refParts = this.xFormItem.getRef().split("/");
		instance = this.xFormItem.getInstance();
		qName = refParts[0];
		if(refParts.length > 1)
			field = refParts[1];
	} else {
		qName = this.getRef();
		instance = this.getInstance();
	}

	if(!field) {
		instance.mailQueueAction(qName, "requeue", "id", ZaMTA.ID_ALL);	
	} else {
		var itemList = new Array();
		if(this.getSelectionCount()>0) {
			itemList = this.getSelection();
		}
		if(field == ZaMTA.A_messages) {
			var arr = [];
			for(var key in itemList) {
				arr.push(itemList[key][ZaMTAQMsgItem.A_id]);
			}
			instance.mailQueueAction(qName, "requeue", "id", arr.join(","));
		} else {
			var arr = [];
			var joinStr = "\" OR \"";
			for(var key in itemList) {
				arr.push(ZaMTA.luceneEscape(itemList[key][ZaMTAQSummaryItem.A_text]))
			}	
			var query = field + ":(\"" + arr.join(joinStr) + "\")";
			instance.mailQueueAction(qName, "requeue", "query", query);
		}
	}
}

ZaMTAXFormView.releaseButtonListener = function (ev) {
	var qName, field, instance;
	if(this.xFormItem) {
		var refParts = this.xFormItem.getRef().split("/");
		instance = this.xFormItem.getInstance();
		qName = refParts[0];
		if(refParts.length > 1)
			field = refParts[1];
	} else {
		qName = this.getRef();
		instance = this.getInstance();
	}
	
	if(!field) {
		instance.mailQueueAction(qName, "release", "id", ZaMTA.ID_ALL);	
	} else {
		var itemList = new Array();
		if(this.getSelectionCount()>0) {
			itemList = this.getSelection();
		}
		if(field == ZaMTA.A_messages) {
			var arr = [];
			for(var key in itemList) {
				arr.push(itemList[key][ZaMTAQMsgItem.A_id]);
			}
			instance.mailQueueAction(qName, "release", "id", arr.join(","));
		} else {
			var arr = [];
			var joinStr = "\" OR \"";
			for(var key in itemList) {
				arr.push(ZaMTA.luceneEscape(itemList[key][ZaMTAQSummaryItem.A_text]))
			}	
			var query = field + ":(\"" + arr.join(joinStr) + "\")";
			instance.mailQueueAction(qName, "release", "query", query);
		}
	}
}

ZaMTAXFormView.holdButtonListener = function (ev) {
	var qName, field, instance;
	if(this.xFormItem) {
		var refParts = this.xFormItem.getRef().split("/");
		instance = this.xFormItem.getInstance();
		qName = refParts[0];
		if(refParts.length > 1)
			field = refParts[1];
	} else {
		qName = this.getRef();
		instance = this.getInstance();
	}
	
	if(!field) {
		instance.mailQueueAction(qName, "hold", "id", ZaMTA.ID_ALL);	
	} else {
		var itemList = new Array();
		if(this.getSelectionCount()>0) {
			itemList = this.getSelection();
		}
		if(field == ZaMTA.A_messages) {
			var arr = [];
			for(var key in itemList) {
				arr.push(itemList[key][ZaMTAQMsgItem.A_id]);
			}
			instance.mailQueueAction(qName, "hold", "id", arr.join(","));
		} else {
			var arr = [];
			var joinStr = "\" OR \"";
			for(var key in itemList) {
				arr.push(ZaMTA.luceneEscape(itemList[key][ZaMTAQSummaryItem.A_text]))
			}	
			var query = field + ":(\"" + arr.join(joinStr) + "\")";
			instance.mailQueueAction(qName, "hold", "query", query);
		}
	}
}

ZaMTAXFormView.backMsgsButtonHndlr = function (ev) {
	var instance = this.getInstance();
	var qName = this.getRef();
	var currentPage = this.getInstanceValue(this.getRef()+"/"+ZaMTA.A_pageNum);

	if (currentPage)
		currentPage--;
	else 
		currentPage = 0;
			
	instance.getMailQStatus(qName, instance[qName][ZaMTA.A_query],currentPage*ZaMTA.RESULTSPERPAGE);
}

ZaMTAXFormView.fwdMsgsButtonHndlr = function (ev) {
	var instance = this.getInstance();
	var qName = this.getRef();
	var currentPage = this.getInstanceValue(this.getRef()+"/"+ZaMTA.A_pageNum);
	if (currentPage)
		currentPage++;
	else 
		currentPage = 1;

	instance.getMailQStatus(qName, instance[qName][ZaMTA.A_query],currentPage*ZaMTA.RESULTSPERPAGE);
}

ZaMTAXFormView.listActionListener = function (ev) {
	this.actionMenu.popup(0, ev.docX, ev.docY);	
}

ZaMTAXFormView.refreshListener = function (ev) {
	var refParts = this.getRef().split("/");
	var filterName = refParts[1];
	var qName = refParts[0];
	ZaMTAXFormView.clearFilter.call(this,ev);	
	this.getInstance().getMailQStatus(qName,null,null,null,true);
}

ZaMTAXFormView.createPopupMenu = function (listWidget) {
	popupOperations = [new ZaOperation(ZaOperation.DELETE, ZaMsg.TBB_Delete, ZaMsg.PQ_Delete_tt, null, null, new AjxListener(listWidget, ZaMTAXFormView.deleteButtonListener)),
	new ZaOperation(ZaOperation.REQUEUE, ZaMsg.TBB_Requeue, ZaMsg.PQ_Requeue_tt, null, null, new AjxListener(listWidget, ZaMTAXFormView.requeueButtonListener))];

	var refParts = this.getRef().split("/");
	var qName = refParts[0];
	if(qName == ZaMTA.A_HoldQ) {
		popupOperations.push(new ZaOperation(ZaOperation.RELEASE, ZaMsg.TBB_Release, ZaMsg.PQ_Release_tt, null, null, new AjxListener(listWidget, ZaMTAXFormView.releaseButtonListener)));
	} else {
		popupOperations.push(new ZaOperation(ZaOperation.HOLD, ZaMsg.TBB_Hold, ZaMsg.PQ_Hold_tt, null, null, new AjxListener(listWidget, ZaMTAXFormView.holdButtonListener)));
	}
	listWidget.actionMenu = new ZaPopupMenu(listWidget, "ActionMenu", null, popupOperations);
	listWidget.addActionListener(new AjxListener(listWidget, ZaMTAXFormView.listActionListener));		
	listWidget.xFormItem = this;
}

/**
* method of the XForm
**/
ZaMTAXFormView.shouldEnableMsgsForwardButton = function (qName) {
	return (this.instance[qName][ZaMTA.A_more]);
};

/**
* method of the XForm
**/
ZaMTAXFormView.shouldEnableMsgsBackButton = function (qName) {
	var val = this.instance[qName][ZaMTA.A_pageNum];
	return (val && (val>0));
};

ZaMTAXFormView.myXFormModifier = function(xFormObject) {	
	xFormObject.tableCssStyle="width:100%;position:static;overflow:auto;";
	
	
	var headerList = new Array();
	headerList[0] = new ZaListHeaderItem(ZaMTAQSummaryItem.A_text, ZaMsg.PQV_name_col, null, null, false, null, false, true);
	headerList[1] = new ZaListHeaderItem(ZaMTAQSummaryItem.A_count, ZaMsg.PQV_count_col, null, "30px", false, null, false, true);
		
	var msgHeaderList = new Array();
	msgHeaderList[0] = new ZaListHeaderItem(ZaMTAQMsgItem.A_id, ZaMsg.PQV_qid_col, null, "100px", null, null, false, true);
	msgHeaderList[1] = new ZaListHeaderItem(ZaMTAQMsgItem.A_recipients, ZaMsg.PQV_recipients_col, null, "106px", null, null, false, true);
	msgHeaderList[2] = new ZaListHeaderItem(ZaMTAQMsgItem.A_sender, ZaMsg.PQV_sender_col, null, "106px", null, null, false, true);		
	msgHeaderList[3] = new ZaListHeaderItem(ZaMTAQMsgItem.A_origin_ip, ZaMsg.PQV_origin_ip_col, null, "97px", null, null, false, true);	
	msgHeaderList[4] = new ZaListHeaderItem(ZaMTAQMsgItem.A_origin_host, ZaMsg.PQV_origin_host_col, null, "103px", null, null, false, true);			
	msgHeaderList[5] = new ZaListHeaderItem(ZaMTAQMsgItem.A_fromdomain, ZaMsg.PQV_origin_domain_col, null, "106px", null, null, false, true);		
	msgHeaderList[6] = new ZaListHeaderItem(ZaMTAQMsgItem.A_content_filter, ZaMsg.PQV_content_filter_col, "103px", null, null, null, false, true);				
	msgHeaderList[7] = new ZaListHeaderItem(ZaMTAQMsgItem.A_time, ZaMsg.PQV_time_col, null, "78px", null, null, false, true);					
	msgHeaderList[8] = new ZaListHeaderItem(null, null, null, "auto", null, null, false, true);						

	xFormObject.items = [
		{type:_GROUP_, cssClass:"ZmSelectedHeaderBg", colSpan: "*", 
			items: [
				{type:_GROUP_,	numCols:6,colSizes:["32px","250px","auto", "130px","250px", "auto"],
					items: [
						{type:_AJX_IMAGE_, src:"Server_32", label:null},
						{type:_OUTPUT_, ref:ZaMTA.A_name, label:null,cssClass:"AdminTitle"},
						{type:_CELLSPACER_},
						{type:_DWT_PROGRESS_BAR_, label:ZaMsg.PQ_ParsingProgress,
							maxValue:100,
							ref:ZaMTA.A_progress,
							relevant:"instance[ZaMTA.A_Status] == 'running' || instance[ZaMTA.A_Status] == 'started'",
							relevantBehavior:_HIDE_,
							valign:_CENTER_,
							align:_CENTER_,	
							wholeCssClass:"progressbar",
							progressCssClass:"progressused"
						},
						{type:_CELLSPACER_}						
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
				{type:_CASE_, numCols:1, cssClass:(AjxEnv.isIE ? "IEcontainer" : ""), width:"100%",/*colSizes:["10", "250","10","250","10"], */relevant:"instance[ZaModel.currentTab] == " + ZaMTAXFormView._tab1, 
					items:[	
						{type:_SPACER_, height:"15"},
						{type:_GROUP_,numCols:11, colSizes:["10%", "12%","10%", "16%", "12%", "auto", "12%", "auto", "12%", "auto", "12%"],tableCssClass:"search_field_tableCssClass", cssClass:"qsearch_field_bar", width:"95%", items: [
							{type:_OUTPUT_, label:ZaMsg.TBB_LastUpdated, ref:ZaMTA.A_DeferredQ+"/"+ZaMTA.A_refreshTime},
							{type:_OUTPUT_, label:ZaMsg.PQ_AnalyzerStatus, ref:ZaMTA.A_DeferredQ+"/"+ZaMTA.A_Status,choices:ZaMTA.SCANNER_STATUS_CHOICES},							
							{type:_DWT_BUTTON_,ref:ZaMTA.A_DeferredQ, label:ZaMsg.PQ_AnalyzeQueue,onActivate:ZaMTAXFormView.refreshListener},{type:_CELLSPACER_},
							{type:_DWT_BUTTON_,ref:ZaMTA.A_DeferredQ, label:ZaMsg.TBB_Requeue,onActivate:ZaMTAXFormView.requeueButtonListener,toolTipContent:ZaMsg.PQ_Requeue_tt},{type:_CELLSPACER_},
							{type:_DWT_BUTTON_,ref:ZaMTA.A_DeferredQ, label:ZaMsg.TBB_Hold,onActivate:ZaMTAXFormView.holdButtonListener,toolTipContent:ZaMsg.PQ_Hold_tt},{type:_CELLSPACER_},							
							{type:_DWT_BUTTON_,ref:ZaMTA.A_DeferredQ, label:ZaMsg.TBB_Delete,onActivate:ZaMTAXFormView.deleteButtonListener,toolTipContent:ZaMsg.PQ_Delete_tt}							
						]},								
						{type:_SPACER_, height:"1"},							
						{type:_GROUP_, numCols:11, /*cssStyle:(AjxEnv.isIE ? "width:98%" : ""),*/ colSizes:["auto","2px", "auto","2px", "auto", "2px", "auto", "2px", "auto", "2px", "auto"],cssClass:(AjxEnv.isIE ? "RadioGrouperBorder IEcontainer" : "RadioGrouperBorder FFcontainer"), tableCssStyle:"width:100%", items: [						
						    {type:_GROUP_, colSpan:11, numCols:1, 
						   		items: [
									{type:_OUTPUT_, value:ZaMsg.PQV_Summary, cssClass:"RadioGrouperLabel"/*(AjxEnv.isIE ? "" : "RadioGrouperLabel")*/, cssStyle:"z-index:"+(Dwt.Z_VIEW+1)}
								]
							},
													
							{type:_GROUP_, numCols:1,cssClass:"RadioGrouperBorder", tableCssClass:"que_table",  items: [
								   {type:_GROUP_, numCols:1, 
								   		items: [
											{type:_OUTPUT_, value:ZaMsg.PQV_GroupRDomain, cssClass:"RadioGrouperLabel"/*(AjxEnv.isIE ? "" : "RadioGrouperLabel")*/, cssStyle:"z-index:"+(Dwt.Z_VIEW+1)}
										]
									},
								    {ref:ZaMTA.A_DeferredQ+"/"+ZaMTA.A_rdomain, type:_DWT_LIST_, height:"200", width:"100%", cssClass: "DLSource", 
							   		forceUpdate: false,createPopupMenu:ZaMTAXFormView.createPopupMenu, preserveSelection:true, multiselect:true,onSelection:ZaMTAXFormView.listSelectionListener, widgetClass:ZaQSummaryListView, headerList:headerList},								
								]
							},		
							{type:_CELLSPACER_},
							{type:_GROUP_, numCols:1,cssClass:"RadioGrouperBorder", tableCssClass:"que_table", items: [
								   {type:_GROUP_, numCols:1, 
								   		items: [
											{type:_OUTPUT_, value:ZaMsg.PQV_GroupOriginIP, cssClass:"RadioGrouperLabel"/*(AjxEnv.isIE ? "" : "RadioGrouperLabel")*/, cssStyle:"z-index:"+(Dwt.Z_VIEW+1)}
										]
									},
								    {ref:ZaMTA.A_DeferredQ+"/"+ZaMTA.A_origip, type:_DWT_LIST_, height:"200", width:"100%", cssClass: "DLSource", 
							   		forceUpdate: false,createPopupMenu:ZaMTAXFormView.createPopupMenu,preserveSelection:true, multiselect:true, onSelection:ZaMTAXFormView.listSelectionListener, widgetClass:ZaQSummaryListView, headerList:headerList},								
								]
							},		
							{type:_CELLSPACER_},
							{type:_GROUP_, numCols:1,cssClass:"RadioGrouperBorder", tableCssClass:"que_table", items: [
								   {type:_GROUP_, numCols:1, 
								   		items: [
											{type:_OUTPUT_, value:ZaMsg.PQV_GroupSenderDomain, cssClass:"RadioGrouperLabel"/*(AjxEnv.isIE ? "" : "RadioGrouperLabel")*/, cssStyle:"z-index:"+(Dwt.Z_VIEW+1)}
										]
									},
								    {ref:ZaMTA.A_DeferredQ+"/"+ZaMTA.A_sdomain, type:_DWT_LIST_, height:"200", width:"100%", cssClass: "DLSource", 
							   		forceUpdate: false,createPopupMenu:ZaMTAXFormView.createPopupMenu,multiselect:true, onSelection:ZaMTAXFormView.listSelectionListener, widgetClass:ZaQSummaryListView, headerList:headerList},								
								]
							},	
							{type:_CELLSPACER_},
							{type:_GROUP_, numCols:1,cssClass:"RadioGrouperBorder", tableCssClass:"que_table", items: [
								   {type:_GROUP_, numCols:1, 
								   		items: [
											{type:_OUTPUT_, value:ZaMsg.PQV_GroupReceiverAddress, cssClass:"RadioGrouperLabel"/*(AjxEnv.isIE ? "" : "RadioGrouperLabel")*/, cssStyle:"z-index:"+(Dwt.Z_VIEW+1)}
										]
									},
								    {ref:ZaMTA.A_DeferredQ+"/"+ZaMTA.A_raddress, type:_DWT_LIST_, height:"200", width:"100%", cssClass: "DLSource", 
							   		forceUpdate: false,createPopupMenu:ZaMTAXFormView.createPopupMenu,multiselect:true, onSelection:ZaMTAXFormView.listSelectionListener, widgetClass:ZaQSummaryListView, headerList:headerList},								
								]
							},		
							{type:_CELLSPACER_},
							{type:_GROUP_, numCols:1,cssClass:"RadioGrouperBorder", tableCssClass:"que_table", items: [
								   {type:_GROUP_, numCols:1, 
								   		items: [
											{type:_OUTPUT_, value:ZaMsg.PQV_GroupSenderAddress, cssClass:"RadioGrouperLabel"/*(AjxEnv.isIE ? "" : "RadioGrouperLabel")*/, cssStyle:"z-index:"+(Dwt.Z_VIEW+1)}
										]
									},
								    {ref:ZaMTA.A_DeferredQ+"/"+ZaMTA.A_saddress, type:_DWT_LIST_, height:"200", width:"100%", cssClass: "DLSource", 
							   		forceUpdate: false,createPopupMenu:ZaMTAXFormView.createPopupMenu,multiselect:true, onSelection:ZaMTAXFormView.listSelectionListener, widgetClass:ZaQSummaryListView, headerList:headerList},								
								]
							},		
							{type:_CELLSPACER_},							
							{type:_GROUP_, numCols:1,cssClass:"RadioGrouperBorder", tableCssClass:"que_table", items: [
								   {type:_GROUP_, numCols:1, 
								   		items: [
											{type:_OUTPUT_, value:ZaMsg.PQV_GroupError, cssClass:"RadioGrouperLabel"/*(AjxEnv.isIE ? "" : "RadioGrouperLabel")*/, cssStyle:"z-index:"+(Dwt.Z_VIEW+1)}
										]
									},
								    {ref:ZaMTA.A_DeferredQ+"/"+ZaMTA.A_error, type:_DWT_LIST_, height:"200", width:"100%", cssClass: "DLSource", 
							   		forceUpdate: false,createPopupMenu:ZaMTAXFormView.createPopupMenu,multiselect:true, onSelection:ZaMTAXFormView.listSelectionListener, widgetClass:ZaQSummaryListView, headerList:headerList},								
								]
							}						
						]},
						{type:_SPACER_, height:"15"},	
																
						{type:_GROUP_, numCols:1, cssClass:(AjxEnv.isIE ? "RadioGrouperBorder IEcontainer" : "RadioGrouperBorder FFcontainer"), tableCssStyle:"width:100%",  items: [
							   {type:_GROUP_, numCols:1, 
							   		items: [
										{type:_OUTPUT_, value:ZaMsg.PQV_Messages, cssClass: "RadioGrouperLabel"/*(AjxEnv.isIE ? "" : "RadioGrouperLabel")*/, cssStyle:"z-index:"+(Dwt.Z_VIEW+1)}
									]
								},
						/*
						* Complicated filter
						*/
						{type:_GROUP_, numCols:10, tableCssClass:"search_field_tableCssClass", cssClass:"qsearch_field_bar", width:"95%", items: [
							{type:_OUTPUT_, label:ZaMsg.PQ_searchQuery, ref:ZaMTA.A_DeferredQ+"/"+ZaMTA.A_query, width:"100%", containerCssClass:"search_field_container"},
							{type:_CELLSPACER_},
							{type:_DWT_BUTTON_, label:ZaMsg.PQ_showAllMsgs, ref:ZaMTA.A_DeferredQ, onActivate:ZaMTAXFormView.showAllMsgs},
							{type:_CELLSPACER_},
							{type:_DWT_BUTTON_, label:ZaMsg.PQ_ClearFilter, ref:ZaMTA.A_DeferredQ, onActivate:ZaMTAXFormView.clearFilter},
							{type:_CELLSPACER_},
							{type:_DWT_BUTTON_, label:ZaMsg.Back, width:75, id:"backButton", icon:"LeftArrow", disIcon:"LeftArrowDis", 	
								ref:ZaMTA.A_DeferredQ,
								onActivate:"ZaMTAXFormView.backMsgsButtonHndlr.call(this,event)", 
								relevantBehavior:_DISABLE_, relevant:("ZaMTAXFormView.shouldEnableMsgsBackButton.call(this,\"" +ZaMTA.A_DeferredQ +"\")")
						    },								       
							{type:_CELLSPACER_},
							{type:_DWT_BUTTON_, label:ZaMsg.Forward, width:75, id:"fwdButton", icon:"RightArrow", disIcon:"RightArrowDis",	
								ref:ZaMTA.A_DeferredQ,labelLocation:(DwtLabel.IMAGE_RIGHT | DwtLabel.ALIGN_CENTER),
								onActivate:"ZaMTAXFormView.fwdMsgsButtonHndlr.call(this,event)", 
								relevantBehavior:_DISABLE_, relevant:("ZaMTAXFormView.shouldEnableMsgsForwardButton.call(this,\"" + ZaMTA.A_DeferredQ+"\")")
						    }
						]},			
							
															
								/*
								//simple filter
								{type:_GROUP_, numCols:5, colSizes:["15%","25%","20%","20%","20%"],
									tableCssClass:"search_field_tableCssClass", cssClass:"qsearch_field_bar", width:"98%", 
									//relevantBehavior:_HIDE_, relevant:"instance[ZaMTA.A_DeferredQ][ZaMTA.A_queue_filter_name] && instance[ZaMTA.A_DeferredQ][ZaMTA.A_queue_filter_value]",
									items: [
										{type:_OUTPUT_, label:ZaMsg.PQ_QueueFilter, ref:ZaMTA.A_DeferredQ+"/"+ZaMTA.A_queue_filter_name},
										{type:_OUTPUT_, label:ZaMsg.PQ_QueueFilterVal, ref:ZaMTA.A_DeferredQ+"/"+ZaMTA.A_queue_filter_value},
										{type:_DWT_BUTTON_,ref:ZaMTA.A_DeferredQ, label:ZaMsg.PQ_ShowAll,onActivate:ZaMTAXFormView.showAllMsgs}
									]
								},	*/								
							    {ref:ZaMTA.A_DeferredQ+"/"+ZaMTA.A_messages, type:_DWT_LIST_, height:"200", width:"100%", cssClass: "DLSource", 
						   		forceUpdate: false,createPopupMenu:ZaMTAXFormView.createPopupMenu,multiselect:true, widgetClass:ZaQMessagesListView, headerList:msgHeaderList},								
							]
						}		
					]
				},							
				{type:_CASE_, numCols:1, cssClass:(AjxEnv.isIE ? "IEcontainer" : ""), width:"100%",/*colSizes:["10", "250","10","250","10"], */relevant:"instance[ZaModel.currentTab] == " + ZaMTAXFormView._tab2, 
					items:[	
						{type:_SPACER_, height:"15"},
						{type:_GROUP_,numCols:11, colSizes:["10%", "12%","10%", "16%", "12%", "auto", "12%", "auto", "12%", "auto", "12%"],tableCssClass:"search_field_tableCssClass", cssClass:"qsearch_field_bar", width:"95%", items: [
							{type:_OUTPUT_, label:ZaMsg.TBB_LastUpdated, ref:ZaMTA.A_IncomingQ+"/"+ZaMTA.A_refreshTime},
							{type:_OUTPUT_, label:ZaMsg.PQ_AnalyzerStatus, ref:ZaMTA.A_IncomingQ+"/"+ZaMTA.A_Status,choices:ZaMTA.SCANNER_STATUS_CHOICES},							
							{type:_DWT_BUTTON_,ref:ZaMTA.A_IncomingQ, label:ZaMsg.PQ_AnalyzeQueue,onActivate:ZaMTAXFormView.refreshListener},{type:_CELLSPACER_},
							{type:_DWT_BUTTON_,ref:ZaMTA.A_IncomingQ, label:ZaMsg.TBB_Requeue,onActivate:ZaMTAXFormView.requeueButtonListener,toolTipContent:ZaMsg.PQ_Requeue_tt},{type:_CELLSPACER_},
							{type:_DWT_BUTTON_,ref:ZaMTA.A_IncomingQ, label:ZaMsg.TBB_Hold,onActivate:ZaMTAXFormView.holdButtonListener,toolTipContent:ZaMsg.PQ_Hold_tt},{type:_CELLSPACER_},	
							{type:_DWT_BUTTON_,ref:ZaMTA.A_IncomingQ, label:ZaMsg.TBB_Delete,onActivate:ZaMTAXFormView.deleteButtonListener,toolTipContent:ZaMsg.PQ_Delete_tt}
						]},								
						{type:_SPACER_, height:"1"},							
						{type:_GROUP_, numCols:9, /*cssStyle:(AjxEnv.isIE ? "width:98%" : ""),*/ colSizes:["auto","2px", "auto","2px", "auto", "2px", "auto", "2px", "auto"],cssClass:(AjxEnv.isIE ? "RadioGrouperBorder IEcontainer" : "RadioGrouperBorder FFcontainer"), tableCssStyle:"width:100%", items: [						
						    {type:_GROUP_, colSpan:9, numCols:1, 
						   		items: [
									{type:_OUTPUT_, value:ZaMsg.PQV_Summary, cssClass:"RadioGrouperLabel"/*(AjxEnv.isIE ? "" : "RadioGrouperLabel")*/, cssStyle:"z-index:"+(Dwt.Z_VIEW+1)}
								]
							},
													
							{type:_GROUP_, numCols:1,cssClass:"RadioGrouperBorder", tableCssClass:"que_table",  items: [
								   {type:_GROUP_, numCols:1, 
								   		items: [
											{type:_OUTPUT_, value:ZaMsg.PQV_GroupRDomain, cssClass:"RadioGrouperLabel"/*(AjxEnv.isIE ? "" : "RadioGrouperLabel")*/, cssStyle:"z-index:"+(Dwt.Z_VIEW+1)}
										]
									},
								    {ref:ZaMTA.A_IncomingQ+"/"+ZaMTA.A_rdomain, type:_DWT_LIST_, height:"200", width:"100%", cssClass: "DLSource", 
							   		forceUpdate: false,createPopupMenu:ZaMTAXFormView.createPopupMenu, preserveSelection:true, multiselect:true,onSelection:ZaMTAXFormView.listSelectionListener, widgetClass:ZaQSummaryListView, headerList:headerList},								
								]
							},		
							{type:_CELLSPACER_},
							{type:_GROUP_, numCols:1,cssClass:"RadioGrouperBorder", tableCssClass:"que_table", items: [
								   {type:_GROUP_, numCols:1, 
								   		items: [
											{type:_OUTPUT_, value:ZaMsg.PQV_GroupOriginIP, cssClass:"RadioGrouperLabel"/*(AjxEnv.isIE ? "" : "RadioGrouperLabel")*/, cssStyle:"z-index:"+(Dwt.Z_VIEW+1)}
										]
									},
								    {ref:ZaMTA.A_IncomingQ+"/"+ZaMTA.A_origip, type:_DWT_LIST_, height:"200", width:"100%", cssClass: "DLSource", 
							   		forceUpdate: false,createPopupMenu:ZaMTAXFormView.createPopupMenu,preserveSelection:true, multiselect:true, onSelection:ZaMTAXFormView.listSelectionListener, widgetClass:ZaQSummaryListView, headerList:headerList},								
								]
							},		
							{type:_CELLSPACER_},
							{type:_GROUP_, numCols:1,cssClass:"RadioGrouperBorder", tableCssClass:"que_table", items: [
								   {type:_GROUP_, numCols:1, 
								   		items: [
											{type:_OUTPUT_, value:ZaMsg.PQV_GroupSenderDomain, cssClass:"RadioGrouperLabel"/*(AjxEnv.isIE ? "" : "RadioGrouperLabel")*/, cssStyle:"z-index:"+(Dwt.Z_VIEW+1)}
										]
									},
								    {ref:ZaMTA.A_IncomingQ+"/"+ZaMTA.A_sdomain, type:_DWT_LIST_, height:"200", width:"100%", cssClass: "DLSource", 
							   		forceUpdate: false,createPopupMenu:ZaMTAXFormView.createPopupMenu,multiselect:true, onSelection:ZaMTAXFormView.listSelectionListener, widgetClass:ZaQSummaryListView, headerList:headerList},								
								]
							},	
							{type:_CELLSPACER_},
							{type:_GROUP_, numCols:1,cssClass:"RadioGrouperBorder", tableCssClass:"que_table", items: [
								   {type:_GROUP_, numCols:1, 
								   		items: [
											{type:_OUTPUT_, value:ZaMsg.PQV_GroupReceiverAddress, cssClass:"RadioGrouperLabel"/*(AjxEnv.isIE ? "" : "RadioGrouperLabel")*/, cssStyle:"z-index:"+(Dwt.Z_VIEW+1)}
										]
									},
								    {ref:ZaMTA.A_IncomingQ+"/"+ZaMTA.A_raddress, type:_DWT_LIST_, height:"200", width:"100%", cssClass: "DLSource", 
							   		forceUpdate: false,createPopupMenu:ZaMTAXFormView.createPopupMenu,multiselect:true, onSelection:ZaMTAXFormView.listSelectionListener, widgetClass:ZaQSummaryListView, headerList:headerList},								
								]
							},		
							{type:_CELLSPACER_},
							{type:_GROUP_, numCols:1,cssClass:"RadioGrouperBorder", tableCssClass:"que_table", items: [
								   {type:_GROUP_, numCols:1, 
								   		items: [
											{type:_OUTPUT_, value:ZaMsg.PQV_GroupSenderAddress, cssClass:"RadioGrouperLabel"/*(AjxEnv.isIE ? "" : "RadioGrouperLabel")*/, cssStyle:"z-index:"+(Dwt.Z_VIEW+1)}
										]
									},
								    {ref:ZaMTA.A_IncomingQ+"/"+ZaMTA.A_saddress, type:_DWT_LIST_, height:"200", width:"100%", cssClass: "DLSource", 
							   		forceUpdate: false,createPopupMenu:ZaMTAXFormView.createPopupMenu,multiselect:true, onSelection:ZaMTAXFormView.listSelectionListener, widgetClass:ZaQSummaryListView, headerList:headerList},								
								]
							}						
						]},
						{type:_SPACER_, height:"15"},	
						{type:_GROUP_, numCols:1, cssClass:(AjxEnv.isIE ? "RadioGrouperBorder IEcontainer" : "RadioGrouperBorder FFcontainer"), tableCssStyle:"width:100%",  items: [
							   {type:_GROUP_, numCols:1, 
							   		items: [
										{type:_OUTPUT_, value:ZaMsg.PQV_Messages, cssClass:"RadioGrouperLabel"/*(AjxEnv.isIE ? "" : "RadioGrouperLabel")*/, cssStyle:"z-index:"+(Dwt.Z_VIEW+1)}
									]
								},
						/*
						* Cool filter
						*/		
						{type:_GROUP_, numCols:10, tableCssClass:"search_field_tableCssClass", cssClass:"qsearch_field_bar", width:"95%", items: [
							{type:_OUTPUT_, label:ZaMsg.PQ_searchQuery, ref:ZaMTA.A_IncomingQ+"/"+ZaMTA.A_query, width:"100%", containerCssClass:"search_field_container"},
							{type:_CELLSPACER_},
							{type:_DWT_BUTTON_, label:ZaMsg.PQ_showAllMsgs, ref:ZaMTA.A_IncomingQ, onActivate:ZaMTAXFormView.showAllMsgs},
							{type:_CELLSPACER_},
							{type:_DWT_BUTTON_, label:ZaMsg.PQ_ClearFilter, ref:ZaMTA.A_IncomingQ, onActivate:ZaMTAXFormView.clearFilter},
							{type:_CELLSPACER_},
							{type:_DWT_BUTTON_, label:ZaMsg.Back, width:75, id:"backButton", icon:"LeftArrow", disIcon:"LeftArrowDis", 	
								ref:ZaMTA.A_IncomingQ,
								onActivate:"ZaMTAXFormView.backMsgsButtonHndlr.call(this,event)", 
								relevantBehavior:_DISABLE_, relevant:("ZaMTAXFormView.shouldEnableMsgsBackButton.call(this,\"" +ZaMTA.A_IncomingQ +"\")")
						    },								       
							{type:_CELLSPACER_},
							{type:_DWT_BUTTON_, label:ZaMsg.Forward, width:75, id:"fwdButton", icon:"RightArrow", disIcon:"RightArrowDis",	
								ref:ZaMTA.A_IncomingQ,labelLocation:(DwtLabel.IMAGE_RIGHT | DwtLabel.ALIGN_CENTER),
								onActivate:"ZaMTAXFormView.fwdMsgsButtonHndlr.call(this,event)", 
								relevantBehavior:_DISABLE_, relevant:("ZaMTAXFormView.shouldEnableMsgsForwardButton.call(this,\"" + ZaMTA.A_IncomingQ+"\")")
						    }														
						]},										
								/*
								//simple filter
								{type:_GROUP_, numCols:5, colSizes:["15%","25%","20%","20%","20%"],
									tableCssClass:"search_field_tableCssClass", cssClass:"qsearch_field_bar", width:"98%", 
									//relevantBehavior:_HIDE_, relevant:"instance[ZaMTA.A_IncomingQ][ZaMTA.A_queue_filter_name] && instance[ZaMTA.A_IncomingQ][ZaMTA.A_queue_filter_value]",
									items: [
										{type:_OUTPUT_, label:ZaMsg.PQ_QueueFilter, ref:ZaMTA.A_IncomingQ+"/"+ZaMTA.A_queue_filter_name},
										{type:_OUTPUT_, label:ZaMsg.PQ_QueueFilterVal, ref:ZaMTA.A_IncomingQ+"/"+ZaMTA.A_queue_filter_value},
										{type:_DWT_BUTTON_,ref:ZaMTA.A_IncomingQ, label:ZaMsg.PQ_ShowAll,onActivate:ZaMTAXFormView.showAllMsgs}
									]
								},*/									
							    {ref:ZaMTA.A_IncomingQ+"/"+ZaMTA.A_messages, type:_DWT_LIST_, height:"200", width:"100%", cssClass: "DLSource", 
						   		forceUpdate: false,createPopupMenu:ZaMTAXFormView.createPopupMenu,multiselect:true, widgetClass:ZaQMessagesListView, headerList:msgHeaderList},								
							]
						}		
					]
				},
				{type:_CASE_, numCols:1, cssClass:(AjxEnv.isIE ? "IEcontainer" : ""), width:"100%",relevant:"instance[ZaModel.currentTab] == " + ZaMTAXFormView._tab3, 
					items:[	
						{type:_SPACER_, height:"15"},
						{type:_GROUP_,numCols:11, colSizes:["10%", "12%","10%", "16%", "12%", "auto", "12%", "auto", "12%", "auto", "12%"],tableCssClass:"search_field_tableCssClass", cssClass:"qsearch_field_bar", width:"95%", items: [
							{type:_OUTPUT_, label:ZaMsg.TBB_LastUpdated, ref:ZaMTA.A_ActiveQ+"/"+ZaMTA.A_refreshTime},
							{type:_OUTPUT_, label:ZaMsg.PQ_AnalyzerStatus, ref:ZaMTA.A_ActiveQ+"/"+ZaMTA.A_Status,choices:ZaMTA.SCANNER_STATUS_CHOICES},							
							{type:_DWT_BUTTON_,ref:ZaMTA.A_ActiveQ, label:ZaMsg.PQ_AnalyzeQueue,onActivate:ZaMTAXFormView.refreshListener},{type:_CELLSPACER_},
							{type:_DWT_BUTTON_,ref:ZaMTA.A_ActiveQ, label:ZaMsg.TBB_Requeue,onActivate:ZaMTAXFormView.requeueButtonListener,toolTipContent:ZaMsg.PQ_Requeue_tt},{type:_CELLSPACER_},
							{type:_DWT_BUTTON_,ref:ZaMTA.A_ActiveQ, label:ZaMsg.TBB_Hold,onActivate:ZaMTAXFormView.holdButtonListener,toolTipContent:ZaMsg.PQ_Hold_tt},{type:_CELLSPACER_},	
							{type:_DWT_BUTTON_,ref:ZaMTA.A_ActiveQ, label:ZaMsg.TBB_Delete,onActivate:ZaMTAXFormView.deleteButtonListener,toolTipContent:ZaMsg.PQ_Delete_tt}
						]},								
						{type:_SPACER_, height:"1"},							
						{type:_GROUP_, numCols:9, colSizes:["auto","2px", "auto","2px", "auto", "2px", "auto", "2px", "auto"],cssClass:(AjxEnv.isIE ? "RadioGrouperBorder IEcontainer" : "RadioGrouperBorder FFcontainer"), tableCssStyle:"width:100%", items: [						
						    {type:_GROUP_, colSpan:9, numCols:1, 
						   		items: [
									{type:_OUTPUT_, value:ZaMsg.PQV_Summary, cssClass:"RadioGrouperLabel"/*(AjxEnv.isIE ? "" : "RadioGrouperLabel")*/, cssStyle:"z-index:"+(Dwt.Z_VIEW+1)}
								]
							},
													
							{type:_GROUP_, numCols:1,cssClass:"RadioGrouperBorder", tableCssClass:"que_table",  items: [
								   {type:_GROUP_, numCols:1, 
								   		items: [
											{type:_OUTPUT_, value:ZaMsg.PQV_GroupRDomain, cssClass:"RadioGrouperLabel"/*(AjxEnv.isIE ? "" : "RadioGrouperLabel")*/, cssStyle:"z-index:"+(Dwt.Z_VIEW+1)}
										]
									},
								    {ref:ZaMTA.A_ActiveQ+"/"+ZaMTA.A_rdomain, type:_DWT_LIST_, height:"200", width:"100%", cssClass: "DLSource", 
							   		forceUpdate: false,createPopupMenu:ZaMTAXFormView.createPopupMenu, preserveSelection:true, multiselect:true,onSelection:ZaMTAXFormView.listSelectionListener, widgetClass:ZaQSummaryListView, headerList:headerList},								
								]
							},		
							{type:_CELLSPACER_},
							{type:_GROUP_, numCols:1,cssClass:"RadioGrouperBorder", tableCssClass:"que_table", items: [
								   {type:_GROUP_, numCols:1, 
								   		items: [
											{type:_OUTPUT_, value:ZaMsg.PQV_GroupOriginIP, cssClass:"RadioGrouperLabel"/*(AjxEnv.isIE ? "" : "RadioGrouperLabel")*/, cssStyle:"z-index:"+(Dwt.Z_VIEW+1)}
										]
									},
								    {ref:ZaMTA.A_ActiveQ+"/"+ZaMTA.A_origip, type:_DWT_LIST_, height:"200", width:"100%", cssClass: "DLSource", 
							   		forceUpdate: false,createPopupMenu:ZaMTAXFormView.createPopupMenu,preserveSelection:true, multiselect:true, onSelection:ZaMTAXFormView.listSelectionListener, widgetClass:ZaQSummaryListView, headerList:headerList},								
								]
							},		
							{type:_CELLSPACER_},
							{type:_GROUP_, numCols:1,cssClass:"RadioGrouperBorder", tableCssClass:"que_table", items: [
								   {type:_GROUP_, numCols:1, 
								   		items: [
											{type:_OUTPUT_, value:ZaMsg.PQV_GroupSenderDomain, cssClass:"RadioGrouperLabel"/*(AjxEnv.isIE ? "" : "RadioGrouperLabel")*/, cssStyle:"z-index:"+(Dwt.Z_VIEW+1)}
										]
									},
								    {ref:ZaMTA.A_ActiveQ+"/"+ZaMTA.A_sdomain, type:_DWT_LIST_, height:"200", width:"100%", cssClass: "DLSource", 
							   		forceUpdate: false,createPopupMenu:ZaMTAXFormView.createPopupMenu,multiselect:true, onSelection:ZaMTAXFormView.listSelectionListener, widgetClass:ZaQSummaryListView, headerList:headerList},								
								]
							},	
							{type:_CELLSPACER_},
							{type:_GROUP_, numCols:1,cssClass:"RadioGrouperBorder", tableCssClass:"que_table", items: [
								   {type:_GROUP_, numCols:1, 
								   		items: [
											{type:_OUTPUT_, value:ZaMsg.PQV_GroupReceiverAddress, cssClass:"RadioGrouperLabel"/*(AjxEnv.isIE ? "" : "RadioGrouperLabel")*/, cssStyle:"z-index:"+(Dwt.Z_VIEW+1)}
										]
									},
								    {ref:ZaMTA.A_ActiveQ+"/"+ZaMTA.A_raddress, type:_DWT_LIST_, height:"200", width:"100%", cssClass: "DLSource", 
							   		forceUpdate: false,createPopupMenu:ZaMTAXFormView.createPopupMenu,multiselect:true, onSelection:ZaMTAXFormView.listSelectionListener, widgetClass:ZaQSummaryListView, headerList:headerList},								
								]
							},		
							{type:_CELLSPACER_},
							{type:_GROUP_, numCols:1,cssClass:"RadioGrouperBorder", tableCssClass:"que_table", items: [
								   {type:_GROUP_, numCols:1, 
								   		items: [
											{type:_OUTPUT_, value:ZaMsg.PQV_GroupSenderAddress, cssClass:"RadioGrouperLabel"/*(AjxEnv.isIE ? "" : "RadioGrouperLabel")*/, cssStyle:"z-index:"+(Dwt.Z_VIEW+1)}
										]
									},
								    {ref:ZaMTA.A_ActiveQ+"/"+ZaMTA.A_saddress, type:_DWT_LIST_, height:"200", width:"100%", cssClass: "DLSource", 
							   		forceUpdate: false,createPopupMenu:ZaMTAXFormView.createPopupMenu,multiselect:true, onSelection:ZaMTAXFormView.listSelectionListener, widgetClass:ZaQSummaryListView, headerList:headerList},								
								]
							}						
						]},
						{type:_SPACER_, height:"15"},	
						{type:_GROUP_, numCols:1, cssClass:(AjxEnv.isIE ? "RadioGrouperBorder IEcontainer" : "RadioGrouperBorder FFcontainer"), tableCssStyle:"width:100%",  items: [
							   {type:_GROUP_, numCols:1, 
							   		items: [
										{type:_OUTPUT_, value:ZaMsg.PQV_Messages, cssClass:"RadioGrouperLabel"/*(AjxEnv.isIE ? "" : "RadioGrouperLabel")*/, cssStyle:"z-index:"+(Dwt.Z_VIEW+1)}
									]
								},
						/*
						* Cool filter
						*/		
						{type:_GROUP_, numCols:10, tableCssClass:"search_field_tableCssClass", cssClass:"qsearch_field_bar", width:"95%", items: [
							{type:_OUTPUT_, label:ZaMsg.PQ_searchQuery, ref:ZaMTA.A_ActiveQ+"/"+ZaMTA.A_query, width:"100%", containerCssClass:"search_field_container"},
							{type:_CELLSPACER_},
							{type:_DWT_BUTTON_, label:ZaMsg.PQ_showAllMsgs, ref:ZaMTA.A_ActiveQ, onActivate:ZaMTAXFormView.showAllMsgs},
							{type:_CELLSPACER_},
							{type:_DWT_BUTTON_, label:ZaMsg.PQ_ClearFilter, ref:ZaMTA.A_ActiveQ, onActivate:ZaMTAXFormView.clearFilter},
							{type:_CELLSPACER_},
							{type:_DWT_BUTTON_, label:ZaMsg.Back, width:75, id:"backButton", icon:"LeftArrow", disIcon:"LeftArrowDis", 	
								ref:ZaMTA.A_ActiveQ,
								onActivate:"ZaMTAXFormView.backMsgsButtonHndlr.call(this,event)", 
								relevantBehavior:_DISABLE_, relevant:("ZaMTAXFormView.shouldEnableMsgsBackButton.call(this,\"" +ZaMTA.A_ActiveQ +"\")")
						    },								       
							{type:_CELLSPACER_},
							{type:_DWT_BUTTON_, label:ZaMsg.Forward, width:75, id:"fwdButton", icon:"RightArrow", disIcon:"RightArrowDis",	
								ref:ZaMTA.A_ActiveQ,labelLocation:(DwtLabel.IMAGE_RIGHT | DwtLabel.ALIGN_CENTER),
								onActivate:"ZaMTAXFormView.fwdMsgsButtonHndlr.call(this,event)", 
								relevantBehavior:_DISABLE_, relevant:("ZaMTAXFormView.shouldEnableMsgsForwardButton.call(this,\"" + ZaMTA.A_ActiveQ+"\")")
						    }												
						]},									
								/*
								//simple filter
								{type:_GROUP_, numCols:5, colSizes:["15%","25%","20%","20%","20%"],
									tableCssClass:"search_field_tableCssClass", cssClass:"qsearch_field_bar", width:"98%", 
									items: [
										{type:_OUTPUT_, label:ZaMsg.PQ_QueueFilter, ref:ZaMTA.A_ActiveQ+"/"+ZaMTA.A_queue_filter_name},
										{type:_OUTPUT_, label:ZaMsg.PQ_QueueFilterVal, ref:ZaMTA.A_ActiveQ+"/"+ZaMTA.A_queue_filter_value},
										{type:_DWT_BUTTON_,ref:ZaMTA.A_ActiveQ, label:ZaMsg.PQ_ShowAll,onActivate:ZaMTAXFormView.showAllMsgs}
									]
								},*/									
							    {ref:ZaMTA.A_ActiveQ+"/"+ZaMTA.A_messages, type:_DWT_LIST_, height:"200", width:"100%", cssClass: "DLSource", 
						   		forceUpdate: false,createPopupMenu:ZaMTAXFormView.createPopupMenu,multiselect:true, widgetClass:ZaQMessagesListView, headerList:msgHeaderList},								
							]
						}		
					]
				},
				{type:_CASE_, numCols:1, cssClass:(AjxEnv.isIE ? "IEcontainer" : ""), width:"100%",relevant:"instance[ZaModel.currentTab] == " + ZaMTAXFormView._tab4, 
					items:[	
						{type:_SPACER_, height:"15"},
						{type:_GROUP_,numCols:11, colSizes:["10%", "12%","10%", "16%", "12%", "auto", "12%", "auto", "12%", "auto", "12%"],tableCssClass:"search_field_tableCssClass", cssClass:"qsearch_field_bar", width:"95%", items: [
							{type:_OUTPUT_, label:ZaMsg.TBB_LastUpdated, ref:ZaMTA.A_HoldQ+"/"+ZaMTA.A_refreshTime},
							{type:_OUTPUT_, label:ZaMsg.PQ_AnalyzerStatus, ref:ZaMTA.A_HoldQ+"/"+ZaMTA.A_Status, choices:ZaMTA.SCANNER_STATUS_CHOICES},							
							{type:_DWT_BUTTON_,ref:ZaMTA.A_HoldQ, label:ZaMsg.PQ_AnalyzeQueue,onActivate:ZaMTAXFormView.refreshListener},{type:_CELLSPACER_},
							{type:_DWT_BUTTON_,ref:ZaMTA.A_HoldQ, label:ZaMsg.TBB_Requeue,onActivate:ZaMTAXFormView.requeueButtonListener,toolTipContent:ZaMsg.PQ_Requeue_tt},{type:_CELLSPACER_},
							{type:_DWT_BUTTON_,ref:ZaMTA.A_HoldQ, label:ZaMsg.TBB_Release,onActivate:ZaMTAXFormView.releaseButtonListener,toolTipContent:ZaMsg.PQ_Release_tt},{type:_CELLSPACER_},	
							{type:_DWT_BUTTON_,ref:ZaMTA.A_HoldQ, label:ZaMsg.TBB_Delete,onActivate:ZaMTAXFormView.deleteButtonListener,toolTipContent:ZaMsg.PQ_Delete_tt}
						]},								
						{type:_SPACER_, height:"1"},							
						{type:_GROUP_, numCols:9, colSizes:["auto","2px", "auto","2px", "auto", "2px", "auto", "2px", "auto"],cssClass:(AjxEnv.isIE ? "RadioGrouperBorder IEcontainer" : "RadioGrouperBorder FFcontainer"), tableCssStyle:"width:100%", items: [						
						    {type:_GROUP_, colSpan:9, numCols:1, 
						   		items: [
									{type:_OUTPUT_, value:ZaMsg.PQV_Summary, cssClass:"RadioGrouperLabel"/*(AjxEnv.isIE ? "" : "RadioGrouperLabel")*/, cssStyle:"z-index:"+(Dwt.Z_VIEW+1)}
								]
							},
													
							{type:_GROUP_, numCols:1,cssClass:"RadioGrouperBorder", tableCssClass:"que_table",  items: [
								   {type:_GROUP_, numCols:1, 
								   		items: [
											{type:_OUTPUT_, value:ZaMsg.PQV_GroupRDomain, cssClass:"RadioGrouperLabel"/*(AjxEnv.isIE ? "" : "RadioGrouperLabel")*/, cssStyle:"z-index:"+(Dwt.Z_VIEW+1)}
										]
									},
								    {ref:ZaMTA.A_HoldQ+"/"+ZaMTA.A_rdomain, type:_DWT_LIST_, height:"200", width:"100%", cssClass: "DLSource", 
							   		forceUpdate: false,createPopupMenu:ZaMTAXFormView.createPopupMenu, preserveSelection:true, multiselect:true,onSelection:ZaMTAXFormView.listSelectionListener, widgetClass:ZaQSummaryListView, headerList:headerList},								
								]
							},		
							{type:_CELLSPACER_},
							{type:_GROUP_, numCols:1,cssClass:"RadioGrouperBorder", tableCssClass:"que_table", items: [
								   {type:_GROUP_, numCols:1, 
								   		items: [
											{type:_OUTPUT_, value:ZaMsg.PQV_GroupOriginIP, cssClass:"RadioGrouperLabel"/*(AjxEnv.isIE ? "" : "RadioGrouperLabel")*/, cssStyle:"z-index:"+(Dwt.Z_VIEW+1)}
										]
									},
								    {ref:ZaMTA.A_HoldQ+"/"+ZaMTA.A_origip, type:_DWT_LIST_, height:"200", width:"100%", cssClass: "DLSource", 
							   		forceUpdate: false,createPopupMenu:ZaMTAXFormView.createPopupMenu,preserveSelection:true, multiselect:true, onSelection:ZaMTAXFormView.listSelectionListener, widgetClass:ZaQSummaryListView, headerList:headerList},								
								]
							},		
							{type:_CELLSPACER_},
							{type:_GROUP_, numCols:1,cssClass:"RadioGrouperBorder", tableCssClass:"que_table", items: [
								   {type:_GROUP_, numCols:1, 
								   		items: [
											{type:_OUTPUT_, value:ZaMsg.PQV_GroupSenderDomain, cssClass:"RadioGrouperLabel"/*(AjxEnv.isIE ? "" : "RadioGrouperLabel")*/, cssStyle:"z-index:"+(Dwt.Z_VIEW+1)}
										]
									},
								    {ref:ZaMTA.A_HoldQ+"/"+ZaMTA.A_sdomain, type:_DWT_LIST_, height:"200", width:"100%", cssClass: "DLSource", 
							   		forceUpdate: false,createPopupMenu:ZaMTAXFormView.createPopupMenu,multiselect:true, onSelection:ZaMTAXFormView.listSelectionListener, widgetClass:ZaQSummaryListView, headerList:headerList},								
								]
							},	
							{type:_CELLSPACER_},
							{type:_GROUP_, numCols:1,cssClass:"RadioGrouperBorder", tableCssClass:"que_table", items: [
								   {type:_GROUP_, numCols:1, 
								   		items: [
											{type:_OUTPUT_, value:ZaMsg.PQV_GroupReceiverAddress, cssClass:"RadioGrouperLabel"/*(AjxEnv.isIE ? "" : "RadioGrouperLabel")*/, cssStyle:"z-index:"+(Dwt.Z_VIEW+1)}
										]
									},
								    {ref:ZaMTA.A_HoldQ+"/"+ZaMTA.A_raddress, type:_DWT_LIST_, height:"200", width:"100%", cssClass: "DLSource", 
							   		forceUpdate: false,createPopupMenu:ZaMTAXFormView.createPopupMenu,multiselect:true, onSelection:ZaMTAXFormView.listSelectionListener, widgetClass:ZaQSummaryListView, headerList:headerList},								
								]
							},		
							{type:_CELLSPACER_},
							{type:_GROUP_, numCols:1,cssClass:"RadioGrouperBorder", tableCssClass:"que_table", items: [
								   {type:_GROUP_, numCols:1, 
								   		items: [
											{type:_OUTPUT_, value:ZaMsg.PQV_GroupSenderAddress, cssClass:"RadioGrouperLabel"/*(AjxEnv.isIE ? "" : "RadioGrouperLabel")*/, cssStyle:"z-index:"+(Dwt.Z_VIEW+1)}
										]
									},
								    {ref:ZaMTA.A_HoldQ+"/"+ZaMTA.A_saddress, type:_DWT_LIST_, height:"200", width:"100%", cssClass: "DLSource", 
							   		forceUpdate: false,createPopupMenu:ZaMTAXFormView.createPopupMenu,multiselect:true, onSelection:ZaMTAXFormView.listSelectionListener, widgetClass:ZaQSummaryListView, headerList:headerList},								
								]
							}						
						]},
						{type:_SPACER_, height:"15"},	
						{type:_GROUP_, numCols:1, cssClass:(AjxEnv.isIE ? "RadioGrouperBorder IEcontainer" : "RadioGrouperBorder FFcontainer"), tableCssStyle:"width:100%",  items: [
							   {type:_GROUP_, numCols:1, 
							   		items: [
										{type:_OUTPUT_, value:ZaMsg.PQV_Messages, cssClass:"RadioGrouperLabel"/*(AjxEnv.isIE ? "" : "RadioGrouperLabel")*/, cssStyle:"z-index:"+(Dwt.Z_VIEW+1)}
									]
								},
						/*
						* Cool filter
						*/		
						{type:_GROUP_, numCols:10, tableCssClass:"search_field_tableCssClass", cssClass:"qsearch_field_bar", width:"95%", items: [
							{type:_OUTPUT_, label:ZaMsg.PQ_searchQuery, ref:ZaMTA.A_HoldQ+"/"+ZaMTA.A_query, width:"100%", containerCssClass:"search_field_container"},
							{type:_CELLSPACER_},
							{type:_DWT_BUTTON_, label:ZaMsg.PQ_showAllMsgs, ref:ZaMTA.A_HoldQ, onActivate:ZaMTAXFormView.showAllMsgs},
							{type:_CELLSPACER_},
							{type:_DWT_BUTTON_, label:ZaMsg.PQ_ClearFilter, ref:ZaMTA.A_HoldQ, onActivate:ZaMTAXFormView.clearFilter},
							{type:_CELLSPACER_},
							{type:_DWT_BUTTON_, label:ZaMsg.Back, width:75, id:"backButton", icon:"LeftArrow", disIcon:"LeftArrowDis", 	
								ref:ZaMTA.A_HoldQ,
								onActivate:"ZaMTAXFormView.backMsgsButtonHndlr.call(this,event)", 
								relevantBehavior:_DISABLE_, relevant:("ZaMTAXFormView.shouldEnableMsgsBackButton.call(this,\"" +ZaMTA.A_HoldQ +"\")")
						    },								       
							{type:_CELLSPACER_},
							{type:_DWT_BUTTON_, label:ZaMsg.Forward, width:75, id:"fwdButton", icon:"RightArrow", disIcon:"RightArrowDis",	
								ref:ZaMTA.A_HoldQ,labelLocation:(DwtLabel.IMAGE_RIGHT | DwtLabel.ALIGN_CENTER),
								onActivate:"ZaMTAXFormView.fwdMsgsButtonHndlr.call(this,event)", 
								relevantBehavior:_DISABLE_, relevant:("ZaMTAXFormView.shouldEnableMsgsForwardButton.call(this,\"" + ZaMTA.A_HoldQ+"\")")
						    }					
						]},								
								/*
								//simple filter
								{type:_GROUP_, numCols:5, colSizes:["15%","25%","20%","20%","20%"],
									tableCssClass:"search_field_tableCssClass", cssClass:"qsearch_field_bar", width:"98%", 
									items: [
										{type:_OUTPUT_, label:ZaMsg.PQ_QueueFilter, ref:ZaMTA.A_HoldQ+"/"+ZaMTA.A_queue_filter_name},
										{type:_OUTPUT_, label:ZaMsg.PQ_QueueFilterVal, ref:ZaMTA.A_HoldQ+"/"+ZaMTA.A_queue_filter_value},
										{type:_DWT_BUTTON_,ref:ZaMTA.A_HoldQ, label:ZaMsg.PQ_ShowAll,onActivate:ZaMTAXFormView.showAllMsgs}
									]
								},*/									
							    {ref:ZaMTA.A_HoldQ+"/"+ZaMTA.A_messages, type:_DWT_LIST_, height:"200", width:"100%", cssClass: "DLSource", 
						   		forceUpdate: false,createPopupMenu:ZaMTAXFormView.createPopupMenu,multiselect:true, widgetClass:ZaQMessagesListView, headerList:msgHeaderList},								
							]
						}		
					]
				},											
					
				{type:_CASE_, numCols:1, cssClass:(AjxEnv.isIE ? "IEcontainer" : ""), width:"100%",relevant:"instance[ZaModel.currentTab] == " + ZaMTAXFormView._tab5, 
					items:[	
						{type:_SPACER_, height:"15"},
						{type:_GROUP_,numCols:11, colSizes:["10%", "12%","10%", "16%", "12%", "auto", "12%", "auto", "12%", "auto", "12%"],tableCssClass:"search_field_tableCssClass", cssClass:"qsearch_field_bar", width:"95%", items: [
							{type:_OUTPUT_, label:ZaMsg.TBB_LastUpdated, ref:ZaMTA.A_CorruptQ+"/"+ZaMTA.A_refreshTime},
							{type:_OUTPUT_, label:ZaMsg.PQ_AnalyzerStatus, ref:ZaMTA.A_CorruptQ+"/"+ZaMTA.A_Status,choices:ZaMTA.SCANNER_STATUS_CHOICES},							
							{type:_DWT_BUTTON_,ref:ZaMTA.A_CorruptQ, label:ZaMsg.PQ_AnalyzeQueue,onActivate:ZaMTAXFormView.refreshListener},{type:_CELLSPACER_},
							{type:_DWT_BUTTON_,ref:ZaMTA.A_CorruptQ, label:ZaMsg.TBB_Requeue,onActivate:ZaMTAXFormView.requeueButtonListener,toolTipContent:ZaMsg.PQ_Requeue_tt},{type:_CELLSPACER_},
							{type:_DWT_BUTTON_,ref:ZaMTA.A_CorruptQ, label:ZaMsg.TBB_Hold,onActivate:ZaMTAXFormView.holdButtonListener,toolTipContent:ZaMsg.PQ_Hold_tt},{type:_CELLSPACER_},	
							{type:_DWT_BUTTON_,ref:ZaMTA.A_CorruptQ, label:ZaMsg.TBB_Delete,onActivate:ZaMTAXFormView.deleteButtonListener,toolTipContent:ZaMsg.PQ_Delete_tt}
						]},								
						{type:_SPACER_, height:"1"},							
						{type:_GROUP_, numCols:9, colSizes:["auto","2px", "auto","2px", "auto", "2px", "auto", "2px", "auto"],cssClass:(AjxEnv.isIE ? "RadioGrouperBorder IEcontainer" : "RadioGrouperBorder FFcontainer"), tableCssStyle:"width:100%", items: [						
						    {type:_GROUP_, colSpan:9, numCols:1, 
						   		items: [
									{type:_OUTPUT_, value:ZaMsg.PQV_Summary, cssClass:"RadioGrouperLabel"/*(AjxEnv.isIE ? "" : "RadioGrouperLabel")*/, cssStyle:"z-index:"+(Dwt.Z_VIEW+1)}
								]
							},
													
							{type:_GROUP_, numCols:1,cssClass:"RadioGrouperBorder", tableCssClass:"que_table",  items: [
								   {type:_GROUP_, numCols:1, 
								   		items: [
											{type:_OUTPUT_, value:ZaMsg.PQV_GroupRDomain, cssClass:"RadioGrouperLabel"/*(AjxEnv.isIE ? "" : "RadioGrouperLabel")*/, cssStyle:"z-index:"+(Dwt.Z_VIEW+1)}
										]
									},
								    {ref:ZaMTA.A_CorruptQ+"/"+ZaMTA.A_rdomain, type:_DWT_LIST_, height:"200", width:"100%", cssClass: "DLSource", 
							   		forceUpdate: false,createPopupMenu:ZaMTAXFormView.createPopupMenu, preserveSelection:true, multiselect:true,onSelection:ZaMTAXFormView.listSelectionListener, widgetClass:ZaQSummaryListView, headerList:headerList},								
								]
							},		
							{type:_CELLSPACER_},
							{type:_GROUP_, numCols:1,cssClass:"RadioGrouperBorder", tableCssClass:"que_table", items: [
								   {type:_GROUP_, numCols:1, 
								   		items: [
											{type:_OUTPUT_, value:ZaMsg.PQV_GroupOriginIP, cssClass:"RadioGrouperLabel"/*(AjxEnv.isIE ? "" : "RadioGrouperLabel")*/, cssStyle:"z-index:"+(Dwt.Z_VIEW+1)}
										]
									},
								    {ref:ZaMTA.A_CorruptQ+"/"+ZaMTA.A_origip, type:_DWT_LIST_, height:"200", width:"100%", cssClass: "DLSource", 
							   		forceUpdate: false,createPopupMenu:ZaMTAXFormView.createPopupMenu,preserveSelection:true, multiselect:true, onSelection:ZaMTAXFormView.listSelectionListener, widgetClass:ZaQSummaryListView, headerList:headerList},								
								]
							},		
							{type:_CELLSPACER_},
							{type:_GROUP_, numCols:1,cssClass:"RadioGrouperBorder", tableCssClass:"que_table", items: [
								   {type:_GROUP_, numCols:1, 
								   		items: [
											{type:_OUTPUT_, value:ZaMsg.PQV_GroupSenderDomain, cssClass:"RadioGrouperLabel"/*(AjxEnv.isIE ? "" : "RadioGrouperLabel")*/, cssStyle:"z-index:"+(Dwt.Z_VIEW+1)}
										]
									},
								    {ref:ZaMTA.A_CorruptQ+"/"+ZaMTA.A_sdomain, type:_DWT_LIST_, height:"200", width:"100%", cssClass: "DLSource", 
							   		forceUpdate: false,createPopupMenu:ZaMTAXFormView.createPopupMenu,multiselect:true, onSelection:ZaMTAXFormView.listSelectionListener, widgetClass:ZaQSummaryListView, headerList:headerList},								
								]
							},	
							{type:_CELLSPACER_},
							{type:_GROUP_, numCols:1,cssClass:"RadioGrouperBorder", tableCssClass:"que_table", items: [
								   {type:_GROUP_, numCols:1, 
								   		items: [
											{type:_OUTPUT_, value:ZaMsg.PQV_GroupReceiverAddress, cssClass:"RadioGrouperLabel"/*(AjxEnv.isIE ? "" : "RadioGrouperLabel")*/, cssStyle:"z-index:"+(Dwt.Z_VIEW+1)}
										]
									},
								    {ref:ZaMTA.A_CorruptQ+"/"+ZaMTA.A_raddress, type:_DWT_LIST_, height:"200", width:"100%", cssClass: "DLSource", 
							   		forceUpdate: false,createPopupMenu:ZaMTAXFormView.createPopupMenu,multiselect:true, onSelection:ZaMTAXFormView.listSelectionListener, widgetClass:ZaQSummaryListView, headerList:headerList},								
								]
							},		
							{type:_CELLSPACER_},
							{type:_GROUP_, numCols:1,cssClass:"RadioGrouperBorder", tableCssClass:"que_table", items: [
								   {type:_GROUP_, numCols:1, 
								   		items: [
											{type:_OUTPUT_, value:ZaMsg.PQV_GroupSenderAddress, cssClass:"RadioGrouperLabel"/*(AjxEnv.isIE ? "" : "RadioGrouperLabel")*/, cssStyle:"z-index:"+(Dwt.Z_VIEW+1)}
										]
									},
								    {ref:ZaMTA.A_CorruptQ+"/"+ZaMTA.A_saddress, type:_DWT_LIST_, height:"200", width:"100%", cssClass: "DLSource", 
							   		forceUpdate: false,createPopupMenu:ZaMTAXFormView.createPopupMenu,multiselect:true, onSelection:ZaMTAXFormView.listSelectionListener, widgetClass:ZaQSummaryListView, headerList:headerList},								
								]
							}						
						]},
						{type:_SPACER_, height:"15"},	
						{type:_GROUP_, numCols:1, cssClass:(AjxEnv.isIE ? "RadioGrouperBorder IEcontainer" : "RadioGrouperBorder FFcontainer"), tableCssStyle:"width:100%",  items: [
							   {type:_GROUP_, numCols:1, 
							   		items: [
										{type:_OUTPUT_, value:ZaMsg.PQV_Messages, cssClass:"RadioGrouperLabel"/*(AjxEnv.isIE ? "" : "RadioGrouperLabel")*/, cssStyle:"z-index:"+(Dwt.Z_VIEW+1)}
									]
								},
						/*
						* Cool filter
						*/		
						{type:_GROUP_, numCols:10, tableCssClass:"search_field_tableCssClass", cssClass:"qsearch_field_bar", width:"95%", items: [
							{type:_OUTPUT_, label:ZaMsg.PQ_searchQuery, ref:ZaMTA.A_CorruptQ+"/"+ZaMTA.A_query, width:"100%", containerCssClass:"search_field_container"},
							{type:_CELLSPACER_},
							{type:_DWT_BUTTON_, label:ZaMsg.PQ_showAllMsgs, ref:ZaMTA.A_CorruptQ, onActivate:ZaMTAXFormView.showAllMsgs},
							{type:_CELLSPACER_},
							{type:_DWT_BUTTON_, label:ZaMsg.PQ_ClearFilter, ref:ZaMTA.A_CorruptQ, onActivate:ZaMTAXFormView.clearFilter},
							{type:_CELLSPACER_},
							{type:_DWT_BUTTON_, label:ZaMsg.Back, width:75, id:"backButton", icon:"LeftArrow", disIcon:"LeftArrowDis", 	
								ref:ZaMTA.A_CorruptQ,
								onActivate:"ZaMTAXFormView.backMsgsButtonHndlr.call(this,event)", 
								relevantBehavior:_DISABLE_, relevant:("ZaMTAXFormView.shouldEnableMsgsBackButton.call(this,\"" +ZaMTA.A_CorruptQ +"\")")
						    },								       
							{type:_CELLSPACER_},
							{type:_DWT_BUTTON_, label:ZaMsg.Forward, width:75, id:"fwdButton", icon:"RightArrow", disIcon:"RightArrowDis",	
								ref:ZaMTA.A_CorruptQ,labelLocation:(DwtLabel.IMAGE_RIGHT | DwtLabel.ALIGN_CENTER),
								onActivate:"ZaMTAXFormView.fwdMsgsButtonHndlr.call(this,event)", 
								relevantBehavior:_DISABLE_, relevant:("ZaMTAXFormView.shouldEnableMsgsForwardButton.call(this,\"" + ZaMTA.A_CorruptQ+"\")")
						    }					
						]},									
								/*
								//simple filter
								{type:_GROUP_, numCols:5, colSizes:["15%","25%","20%","20%","20%"],
									tableCssClass:"search_field_tableCssClass", cssClass:"qsearch_field_bar", width:"98%", 
									items: [
										{type:_OUTPUT_, label:ZaMsg.PQ_QueueFilter, ref:ZaMTA.A_CorruptQ+"/"+ZaMTA.A_queue_filter_name},
										{type:_OUTPUT_, label:ZaMsg.PQ_QueueFilterVal, ref:ZaMTA.A_CorruptQ+"/"+ZaMTA.A_queue_filter_value},
										{type:_DWT_BUTTON_,ref:ZaMTA.A_CorruptQ, label:ZaMsg.PQ_ShowAll,onActivate:ZaMTAXFormView.showAllMsgs}
									]
								},*/									
							    {ref:ZaMTA.A_CorruptQ+"/"+ZaMTA.A_messages, type:_DWT_LIST_, height:"200", width:"100%", cssClass: "DLSource", 
						   		forceUpdate: false,createPopupMenu:ZaMTAXFormView.createPopupMenu,multiselect:true, widgetClass:ZaQMessagesListView, headerList:msgHeaderList},								
							]
						}		
					]
				}											
			]
		}
	]
};
ZaTabView.XFormModifiers["ZaMTAXFormView"].push(ZaMTAXFormView.myXFormModifier);