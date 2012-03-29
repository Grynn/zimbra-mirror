/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2006, 2007, 2008, 2009, 2010 Zimbra, Inc.
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
* @class ZaMTAXFormView
* @contructor
* @param parent
* @param app
* @author Greg Solovyev
**/
ZaMTAXFormView = function(parent) {
	ZaTabView.call(this, {
		parent:parent, 
		iKeyName:"ZaMTAXFormView",
		contextId:ZaId.TAB_MTX_EDIT
	});	

	this.TAB_INDEX = 0;
	ZaMTAXFormView._tab1 = ++this.TAB_INDEX;
	ZaMTAXFormView._tab2 = ++this.TAB_INDEX;	
	ZaMTAXFormView._tab3 = ++this.TAB_INDEX;	
	ZaMTAXFormView._tab4 = ++this.TAB_INDEX;	
	ZaMTAXFormView._tab5 = ++this.TAB_INDEX;	
			
	this.initForm(ZaMTA.myXModel,this.getMyXForm());
	this._localXForm.addListener(DwtEvent.XFORMS_FORM_DIRTY_CHANGE, new AjxListener(this, ZaMTAXFormView.prototype.handleXFormChange));	
	this._localXForm.setController(ZaApp.getInstance());
}

ZaMTAXFormView.prototype = new ZaTabView();
ZaMTAXFormView.prototype.constructor = ZaMTAXFormView;
ZaTabView.XFormModifiers["ZaMTAXFormView"] = new Array();


ZaMTAXFormView.tabChoices = new XFormChoices([{value:ZaMTAXFormView._tab1, label:ZaMsg.PQV_Tab_Deferred},
				{value:ZaMTAXFormView._tab2, label:ZaMsg.PQV_Tab_IncomingQ},
				{value:ZaMTAXFormView._tab3, label:ZaMsg.PQV_Tab_ActiveQ},
				{value:ZaMTAXFormView._tab4, label:ZaMsg.PQV_Tab_HoldQ},					
				{value:ZaMTAXFormView._tab5, label:ZaMsg.PQV_Tab_CorruptQ}],
				XFormChoices.OBJECT_LIST, "value", "label");

ZaMTAXFormView.prototype.getTabIcon =
function () {
	return "Queue" ;
}

ZaMTAXFormView.prototype.setObject = 
function (entry) {
	this._containedObject = entry;
	this._containedObject._viewInternalId = this.__internalId;
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
    if (!appNewUI)
	    this.updateTab();
}
ZaMTAXFormView.prototype.handleXFormChange = function () {
	if(this._containedObject[ZaModel.currentTab] == "1" && (this._containedObject[ZaMTA.A_DeferredQ][ZaMTA.A_Status]==ZaMTA.STATUS_IDLE)) {
		this._containedObject.getMailQStatus(ZaMTA.A_DeferredQ, null, 0,null,true);	
	}
	if(this._containedObject[ZaModel.currentTab] == "2" && (this._containedObject[ZaMTA.A_IncomingQ][ZaMTA.A_Status]==ZaMTA.STATUS_IDLE)) {
		this._containedObject.getMailQStatus(ZaMTA.A_IncomingQ, null, 0,null,true);	
	}
	if(this._containedObject[ZaModel.currentTab] == "3" && (this._containedObject[ZaMTA.A_ActiveQ][ZaMTA.A_Status]==ZaMTA.STATUS_IDLE)) {
		this._containedObject.getMailQStatus(ZaMTA.A_ActiveQ, null, 0,null,true);	
	}
	if(this._containedObject[ZaModel.currentTab] == "4" && (this._containedObject[ZaMTA.A_HoldQ][ZaMTA.A_Status]==ZaMTA.STATUS_IDLE)) {
		this._containedObject.getMailQStatus(ZaMTA.A_HoldQ, null, 0,null,true);	
	}
	if(this._containedObject[ZaModel.currentTab] == "5" && (this._containedObject[ZaMTA.A_CorruptQ][ZaMTA.A_Status]==ZaMTA.STATUS_IDLE)) {
		this._containedObject.getMailQStatus(ZaMTA.A_CorruptQ, null, 0,null,true);	
	}				
}

ZaMTAXFormView._listObjects = {};

ZaMTAXFormView.filterListSelectionListener = function (ev) {
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

	var arr = this.widget.getSelection();
	if(arr && arr.length) {
		this.getModel().setInstanceValue(instance,qName + "/" + ZaMTA.A_selection_cache + "/" + filterName,arr);
		//instance[qName][ZaMTA.A_selection_cache][filterName] = arr;
	} else { 
		this.getModel().setInstanceValue(instance,qName + "/" + ZaMTA.A_selection_cache + "/" + filterName,null);		
		instance[qName][ZaMTA.A_selection_cache][filterName] = null;
	}
	//rebuild the query
	//this.getForm().refresh();
	instance._viewInternalId = this.getForm().parent.__internalId;
	instance.getMailQStatus(qName, instance[qName][ZaMTA.A_selection_cache]);	
}

ZaMTAXFormView.msgListSelectionListener = function (ev) {
	//register this list in the map, so that we can deselect it later
	if(ev.dwtObj && this.refPath) {
		ZaMTAXFormView._listObjects[this.refPath] = ev.dwtObj;
	}
	var instance = this.getInstance();
	var refParts = this.getRef().split("/");

	var qName = refParts[0];
	if(!instance[qName][ZaMTA.MsgIDS])
		instance[qName][ZaMTA.MsgIDS] = {};

	this.getModel().setInstanceValue(instance,qName + "/" + ZaMTA.MsgIDS, AjxUtil.isEmpty(this.widget.getSelection()) ? {} : this.widget.getSelection()); 
	//instance[qName][ZaMTA.MsgIDS] = this.widget.getSelection();
}

ZaMTAXFormView.searchQueue = function (ev) {
	var instance = this.getInstance();
	var qName = this.getRef();
	//var query = instance[qName][ZaMTA.A_query];
	instance.getMailQStatus(qName, instance[qName][ZaMTA.A_selection_cache]);	
	
}
			
ZaMTAXFormView.clearFilter = 
function (ev) {
//	this.setInstanceValue("",this.getRef()+"/"+ZaMTA.A_queue_filter_name);
//	this.setInstanceValue("",this.getRef()+"/"+ZaMTA.A_queue_filter_value);	
	this.setInstanceValue("",this.getRef()+"/"+ZaMTA.A_selection_cache);	

	this.setInstanceValue("",this.getRef()+"/"+ZaMTA.A_query);		
	//this.getForm().refresh();
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

ZaMTAXFormView.actionButtonListener = function (action) {
	var qName, field, dlgTitle,instance;
	qName = this.getRef();
	form = this.getForm();
	
	instance = this.getInstance();
	var obj = new Object();
	
	obj[ZaMTAActionDialog.QNAME]=qName;	
	switch(action) {
		case ZaMTA.ActionRequeue:
			dlgTitle = ZaMsg.PQ_REQ_DLG_TITLE;
			obj[ZaMTAActionDialog.MESSAGE]=ZaMsg.PQ_SELECT_WHAT_TO_REQ;
			obj[ZaMTAActionDialog.QUESTION]=ZaMsg.PQ_Q_REQUEUE_MESSAGES;
		break;
		case ZaMTA.ActionDelete:
			dlgTitle = ZaMsg.PQ_DEL_DLG_TITLE;
			obj[ZaMTAActionDialog.MESSAGE]=ZaMsg.PQ_SELECT_WHAT_TO_DEL;
			obj[ZaMTAActionDialog.QUESTION]=ZaMsg.PQ_Q_DELETE_MESSAGES;			
		break;
		case ZaMTA.ActionHold:
			dlgTitle = ZaMsg.PQ_HOLD_DLG_TITLE;
			obj[ZaMTAActionDialog.MESSAGE]=ZaMsg.PQ_SELECT_WHAT_TO_HOLD;
			obj[ZaMTAActionDialog.QUESTION]=ZaMsg.PQ_Q_HOLD_MESSAGES;			
		break;
		case ZaMTA.ActionRelease:
			dlgTitle = ZaMsg.PQ_REL_DLG_TITLE;
			obj[ZaMTAActionDialog.MESSAGE]=ZaMsg.PQ_SELECT_WHAT_TO_REL;
			obj[ZaMTAActionDialog.QUESTION]=ZaMsg.PQ_Q_RELEASE_MESSAGES;			
		break;
	}		
	var view = form.parent;
	view.selectActionDialog = ZaApp.getInstance().dialogs["selectActionDialog"] = new ZaMTAActionDialog(ZaApp.getInstance().getAppCtxt().getShell(),dlgTitle);	
	obj[ZaMTAActionDialog.MSG_IDS] = instance[qName][ZaMTA.MsgIDS];
	obj[ZaMTAActionDialog.FLTR_ITEMS] = instance[qName][ZaMTA.A_selection_cache];	
	obj[ZaMTAActionDialog.ANSWER] = ZaMTAActionDialog.SELECTED_MSGS; //default is selected messages
	obj[ZaMTAActionDialog.ACTION] = action;
	view.selectActionDialog.setObject(obj);
	view.selectActionDialog.registerCallback(DwtDialog.OK_BUTTON, view.actionDlgCallback, view, action);
	view.selectActionDialog.popup();
	
}

ZaMTAXFormView.prototype.actionDlgCallback = function(args)  {
	if(this.selectActionDialog) {
		var obj = this.selectActionDialog.getObject();
		var removeList;
		if(obj[ZaMTAActionDialog.ANSWER] == ZaMTAActionDialog.SELECTED_MSGS) {
			removeList = obj[ZaMTAActionDialog.MSG_IDS];
			if (removeList && removeList.length) {
				this.showConfirmationDlg(obj[ZaMTAActionDialog.ACTION],removeList, obj[ZaMTAActionDialog.QNAME],ZaMTA.A_messages);
			} else {
				this.selectActionDialog.popdown();
			}
		} else if(obj[ZaMTAActionDialog.ANSWER] == ZaMTAActionDialog.FLTRED_SET) {
			removeList = {};
			var field;
			if(obj[ZaMTAActionDialog.FLTR_ITEMS]) {
				for (var key in obj[ZaMTAActionDialog.FLTR_ITEMS]) {
					if(obj[ZaMTAActionDialog.FLTR_ITEMS][key]) {
						field = key;
						removeList[key] = obj[ZaMTAActionDialog.FLTR_ITEMS][key];
					}
				}
			}
			if(field) {
				this.showConfirmationDlg(obj[ZaMTAActionDialog.ACTION],removeList, obj[ZaMTAActionDialog.QNAME],field);
			} else {
				removeList = [];
				removeList[0] = {};
				removeList[0][ZaMTAQMsgItem.A_id] = ZaMTA.ID_ALL;
				this.showConfirmationDlg(obj[ZaMTAActionDialog.ACTION],removeList, obj[ZaMTAActionDialog.QNAME],ZaMTA.A_messages);
			}
		}
	}
}

ZaMTAXFormView.prototype.showConfirmationDlg = function (action, removelist,qName, field) {
	this.confirmMessageDialog = ZaApp.getInstance().dialogs["ConfirmMessageDialog"] = new ZaMsgDialog(ZaApp.getInstance().getAppCtxt().getShell(), null, [DwtDialog.YES_BUTTON, DwtDialog.NO_BUTTON],null,ZaId.VIEW_MTA + "_confirmMessage");			
	if(removelist) {
		if(field == ZaMTA.A_messages) {
			var subst = "0";
			if(removelist.length) {
				if(removelist[0][ZaMTAQMsgItem.A_id]==ZaMTA.ID_ALL) {
					subst = ZaMsg.PQ_AllMessages;
				} else {
					subst = String(removelist.length);
				}
			}
			switch(action) {
				case ZaMTA.ActionRequeue:
					dlgMsg = String(ZaMsg.PQ_Q_REQUEUE_MESSAGES).replace("{0}", subst).replace("{1}",qName);
				break;
				case ZaMTA.ActionDelete:
					dlgMsg = String(ZaMsg.PQ_Q_DELETE_MESSAGES).replace("{0}", subst).replace("{1}",qName);
				break;
				case ZaMTA.ActionHold:
					dlgMsg = String(ZaMsg.PQ_Q_HOLD_MESSAGES).replace("{0}", subst).replace("{1}",qName);				
				break;
				case ZaMTA.ActionRelease:
					dlgMsg = String(ZaMsg.PQ_Q_RELEASE_MESSAGES).replace("{0}", subst).replace("{1}",qName);								
				break;
			}
		} else {
			switch(action) {
				case ZaMTA.ActionRequeue:
					dlgMsg = ZaMsg.PQ_Q_REQUEUE_MESSAGES2;
				break;
				case ZaMTA.ActionDelete:
					dlgMsg = ZaMsg.PQ_Q_DELETE_MESSAGES2;
				break;
				case ZaMTA.ActionHold:
					dlgMsg = ZaMsg.PQ_Q_HOLD_MESSAGES2;
				break;
				case ZaMTA.ActionRelease:
					dlgMsg = ZaMsg.PQ_Q_RELEASE_MESSAGES2;
				break;
			}		
			dlgMsg +=  "<br><ul>";
			var i=0;
			for(var key in removelist) {
				if(removelist[key]) {
					var cnt = removelist[key].length;
					dlgMsg += "<li>";
					dlgMsg += key;
					dlgMsg += "<ul>";
					for(var j=0; j < cnt; j++) {
						if(i > 19) {
							dlgMsg += "<li>...</li>";
							break;
						}
						dlgMsg += "<li>";
								dlgMsg += removelist[key][j][ZaMTAQSummaryItem.A_text];
						dlgMsg += (" (" + removelist[key][j][ZaMTAQSummaryItem.A_count] + " " + ZaMsg.messages + ")");
						dlgMsg += "</li>";						
						i++;
					}
					dlgMsg += "</ul></li>";
				}
			}
		}
		dlgMsg += "</ul>";
		if(field == ZaMTA.A_messages) {
			this.confirmMessageDialog.registerCallback(DwtDialog.YES_BUTTON, this.actionMsgsByIDCallback, this, {action:action,removelist:removelist, qName:qName, field:field});
		} else {
			this.confirmMessageDialog.registerCallback(DwtDialog.YES_BUTTON, this.actionMsgsByQueryCallback, this,{action:action,removelist:removelist, qName:qName, field:field});
		}
	}
	if(dlgMsg) {
		this.confirmMessageDialog.setMessage(dlgMsg,  DwtMessageDialog.INFO_STYLE);
	}
	this.confirmMessageDialog.registerCallback(DwtDialog.NO_BUTTON, this.doNotCallback, this);		
	this.confirmMessageDialog.popup();	
}

ZaMTAXFormView.popupMenuListener = function (action) {
	var qName, field, removeList;
	if(this.xFormItem) {
		var refParts = this.xFormItem.getRef().split("/");
		qName = refParts[0];
		if(refParts.length > 1)
			field = refParts[1];
	} 	

	var view = this.xFormItem.getForm().parent;
	if(field == ZaMTA.A_messages) {
		var removeList = new Array();
		if(this.getSelectionCount()>0) {
			removeList = this.getSelection();
		}
	} else  {
		var removeList = {};
		removeList[field] = new Array();
		if(this.getSelectionCount()>0) {
			removeList[field] = this.getSelection();
		}		
	}
	view.showConfirmationDlg(action, removeList, qName, field);
}

ZaMTAXFormView.prototype.actionMsgsByIDCallback = function (args) {
	var arr = [], action, qName,removelist;
	action = args.action;
	removelist = args.removelist;
	qName = args.qName;
	for(var key in removelist) {
		arr.push(removelist[key][ZaMTAQMsgItem.A_id])
	}
	if(arr.length > 0) {
	/*	if(this.xFormItem) {
			instance = this.xFormItem.getInstance();
			var refParts = this.xFormItem.getRef().split("/");
			qName = refParts[0];
		} else {
			instance = this.getInstance();
			qName = this.getRef();
		}*/
		
		this._containedObject.mailQueueAction(qName, action, "id", arr.join(","));
	}
	this.confirmMessageDialog.popdown();
	if(this.selectActionDialog)
		this.selectActionDialog.popdown();
}

ZaMTAXFormView.prototype.actionMsgsByQueryCallback = function (args) {
	var arr = [], action, removelist, qName, field;
	action = args.action;
	removelist = args.removelist;
	qName = args.qName;
	field = args.field;
	this._containedObject.mailQueueAction(qName, action, "query", removelist);
	this.confirmMessageDialog.popdown();
	if(this.selectActionDialog)
		this.selectActionDialog.popdown();
}

ZaMTAXFormView.prototype.doNotCallback = function () {
	if(this.confirmMessageDialog)
		this.confirmMessageDialog.popdown();
}


ZaMTAXFormView.backMsgsButtonHndlr = function (ev) {
	var instance = this.getInstance();
	var qName = this.getRef();
	var currentPage = this.getInstanceValue(this.getRef()+"/"+ZaMTA.A_pageNum);

	if (currentPage)
		currentPage--;
	else 
		currentPage = 0;
			
	instance.getMailQStatus(qName, instance[qName][ZaMTA.A_selection_cache],currentPage*ZaMTA.RESULTSPERPAGE);
}

ZaMTAXFormView.fwdMsgsButtonHndlr = function (ev) {
	var instance = this.getInstance();
	var qName = this.getRef();
	var currentPage = this.getInstanceValue(this.getRef()+"/"+ZaMTA.A_pageNum);
	if (currentPage)
		currentPage++;
	else 
		currentPage = 1;

	instance.getMailQStatus(qName, instance[qName][ZaMTA.A_selection_cache],currentPage*ZaMTA.RESULTSPERPAGE);
}

ZaMTAXFormView.listActionListener = function (ev) {
	this.actionMenu.popup(0, ev.docX, ev.docY);	
}

ZaMTAXFormView.isParsingProgressBarVisible = function (quename) {
	var instance = this.getInstance();
	return (instance[quename][ZaMTA.A_Status] == ZaMTA.STATUS_SCANNING || instance[quename][ZaMTA.A_Status] == ZaMTA.STATUS_SCAN_COMPLETE);	
}

ZaMTAXFormView.refreshListener = function (ev) {
	var refParts = this.getRef().split("/");
	var filterName = refParts[1];
	var qName = refParts[0];
	ZaMTAXFormView.clearFilter.call(this,ev);	
	this.getInstance().getMailQStatus(qName,null,null,null,true);
	this.getInstance().load();
	ZaMTAXFormView.tabChoices.setChoices([
		{value:ZaMTAXFormView._tab1, label:ZaMsg.PQV_Tab_Deferred + " (" + this.getInstance()[ZaMTA.A_DeferredQ][ZaMTA.A_count] + ")"},
		{value:ZaMTAXFormView._tab2, label:ZaMsg.PQV_Tab_IncomingQ + " (" + this.getInstance()[ZaMTA.A_IncomingQ][ZaMTA.A_count] + ")"},
				{value:ZaMTAXFormView._tab3, label:ZaMsg.PQV_Tab_ActiveQ + " (" + this.getInstance()[ZaMTA.A_ActiveQ][ZaMTA.A_count] + ")"},
				{value:ZaMTAXFormView._tab4, label:ZaMsg.PQV_Tab_HoldQ + " (" + this.getInstance()[ZaMTA.A_HoldQ][ZaMTA.A_count] + ")"},					
				{value:ZaMTAXFormView._tab5, label:ZaMsg.PQV_Tab_CorruptQ + " (" + this.getInstance()[ZaMTA.A_CorruptQ][ZaMTA.A_count] + ")"}]),

	ZaMTAXFormView.tabChoices.dirtyChoices();	
}

ZaMTAXFormView.createPopupMenu = function (listWidget) {
	popupOperations = [new ZaOperation(ZaOperation.DELETE, ZaMsg.TBB_Delete, ZaMsg.PQ_Delete_tt, null, null, new AjxListener(listWidget, ZaMTAXFormView.popupMenuListener, ZaMTA.ActionDelete)),
	new ZaOperation(ZaOperation.REQUEUE, ZaMsg.TBB_Requeue, ZaMsg.PQ_Requeue_tt, null, null, new AjxListener(listWidget, ZaMTAXFormView.popupMenuListener,ZaMTA.ActionRequeue ))];

	var refParts = this.getRef().split("/");
	var qName = refParts[0];
	if(qName == ZaMTA.A_HoldQ) {
		popupOperations.push(new ZaOperation(ZaOperation.RELEASE, ZaMsg.TBB_Release, ZaMsg.PQ_Release_tt, null, null, new AjxListener(listWidget, ZaMTAXFormView.popupMenuListener,ZaMTA.ActionRelease)));
	} else {
		popupOperations.push(new ZaOperation(ZaOperation.HOLD, ZaMsg.TBB_Hold, ZaMsg.PQ_Hold_tt, null, null, new AjxListener(listWidget, ZaMTAXFormView.popupMenuListener,ZaMTA.ActionHold )));
	}
	listWidget.actionMenu = new ZaPopupMenu(listWidget, "ActionMenu", null, popupOperations, ZaId.VIEW_MTA, ZaId.MENU_POP);
	listWidget.addActionListener(new AjxListener(listWidget, ZaMTAXFormView.listActionListener));		
	listWidget.xFormItem = this;
}

/**
* method of the XForm
**/
ZaMTAXFormView.shouldEnableMsgsForwardButton = function (qName) {
	return (this.getInstanceValue(qName + "/" +ZaMTA.A_more));
};

/**
* method of the XForm
**/
ZaMTAXFormView.shouldEnableMsgsBackButton = function (qName) {
	var val = this.getInstanceValue(qName + "/" + ZaMTA.A_pageNum);
	return (val && (val>0));
};

ZaMTAXFormView.myXFormModifier = function(xFormObject) {	
	xFormObject.tableCssStyle="width:100%;position:static;overflow:auto;";
	
	
	var headerList = new Array();
    if(ZaZimbraAdmin.LOCALE=="en"||ZaZimbraAdmin.LOCALE=="en_AU"||ZaZimbraAdmin.LOCALE=="en_GB")
        headerList[0] = new ZaListHeaderItem(ZaMTAQSummaryItem.A_text_col, ZaMsg.PQV_name_col, null, "55px", false, null, true, true);
    else
	    headerList[0] = new ZaListHeaderItem(ZaMTAQSummaryItem.A_text_col, ZaMsg.PQV_name_col, null, "38px", false, null, true, true);

	headerList[1] = new ZaListHeaderItem(ZaMTAQSummaryItem.A_count_col, ZaMsg.PQV_count_col, null, "auto", false, null, true, true);
	//headerList[2] = new ZaListHeaderItem(null, null, null, null, null, null, false, true);							
		
	var msgHeaderList = new Array();
	msgHeaderList[0] = new ZaListHeaderItem(ZaMTAQMsgItem.A_id, ZaMsg.PQV_qid_col, null, "100px", null, null, true, true);

    if(ZaZimbraAdmin.LOCALE=="en"||ZaZimbraAdmin.LOCALE=="en_AU"||ZaZimbraAdmin.LOCALE=="en_GB"){
        msgHeaderList[1] = new ZaListHeaderItem(ZaMTAQMsgItem.A_recipients, ZaMsg.PQV_recipients_col, null, "136px", null, null, true, true);
        msgHeaderList[2] = new ZaListHeaderItem(ZaMTAQMsgItem.A_sender, ZaMsg.PQV_sender_col, null, "136px", null, null, true, true);
    }
     else{
         msgHeaderList[1] = new ZaListHeaderItem(ZaMTAQMsgItem.A_recipients, ZaMsg.PQV_recipients_col, null, "106px", null, null, true, true);
	     msgHeaderList[2] = new ZaListHeaderItem(ZaMTAQMsgItem.A_sender, ZaMsg.PQV_sender_col, null, "106px", null, null, true, true);
    }
	msgHeaderList[3] = new ZaListHeaderItem(ZaMTAQMsgItem.A_origin_ip, ZaMsg.PQV_origin_ip_col, null, "97px", null, null, true, true);
	msgHeaderList[4] = new ZaListHeaderItem(ZaMTAQMsgItem.A_origin_host, ZaMsg.PQV_origin_host_col, null, "103px", null, null, true, true);
	msgHeaderList[5] = new ZaListHeaderItem(ZaMTAQMsgItem.A_fromdomain, ZaMsg.PQV_origin_domain_col, null, "106px", null, null, true, true);
	msgHeaderList[6] = new ZaListHeaderItem(ZaMTAQMsgItem.A_content_filter, ZaMsg.PQV_content_filter_col, "103px", null, null, null, true, true);
	msgHeaderList[7] = new ZaListHeaderItem(ZaMTAQMsgItem.A_time, ZaMsg.PQV_time_col, null, "78px", null, null, true, true);
	msgHeaderList[8] = new ZaListHeaderItem(null, null, null, "auto", null, null, true, true);

	xFormObject.items = [
		{type:_GROUP_, cssClass:"ZmSelectedHeaderBg", colSpan:"*", id:"xform_header", 
			items: [
				{type:_GROUP_, numCols:2, colSizes:["32px","auto"],
					items: [
						{type:_AJX_IMAGE_, src:"Queue_32", label:null},
						{type:_OUTPUT_, ref:ZaMTA.A_name, label:null,cssClass:"AdminTitle",
                           visibilityChecks:[ZaItem.hasReadPermission], height:32 }
					]
				}
			]
		},
		{type:_TAB_BAR_, ref:ZaModel.currentTab,
			containerCssStyle: "padding-top:0px",
			choices:ZaMTAXFormView.tabChoices,
			cssClass:"ZaTabBar", id:"xform_tabbar"
		},
		{type:_SWITCH_, items:[
				{type:_ZATABCASE_, numCols:1, caseKey:ZaMTAXFormView._tab1, 
					items:[	
						{type:_SPACER_, height:"15"},
						{type:_GROUP_,numCols:8, colSizes:["10%", "10%","10%", "15%", "17%", "25%", "auto", "10%"],tableCssClass:"search_field_tableCssClass", cssClass:"qsearch_field_bar", width:"95%", items: [
							{type:_OUTPUT_, label:ZaMsg.TBB_LastUpdated, ref:ZaMTA.A_DeferredQ+"/"+ZaMTA.A_refreshTime},
							{type:_OUTPUT_, label:ZaMsg.PQ_AnalyzerStatus, ref:ZaMTA.A_DeferredQ+"/"+ZaMTA.A_Status,choices:ZaMTA.SCANNER_STATUS_CHOICES},
							{type:_DWT_PROGRESS_BAR_,label:ZaMsg.PQ_ParsingProgress,
								maxValue:null,
								maxValueRef:ZaMTA.A_DeferredQ+"/"+ZaMTA.A_count,
								ref:ZaMTA.A_DeferredQ+"/"+ZaMTA.A_totalComplete,
								visibilityChecks:[[ZaMTAXFormView.isParsingProgressBarVisible,ZaMTA.A_DeferredQ]],
								visibilityChangeEventSources:[ZaMTA.A_DeferredQ+"/"+ZaMTA.A_Status],
								align:_CENTER_,	
								wholeCssClass:"mtaprogressbar",
								progressCssClass:"progressused"
							},
							{type:_CELLSPACER_},
							{type:_DWT_BUTTON_,ref:ZaMTA.A_DeferredQ, label:ZaMsg.PQ_AnalyzeQueue,onActivate:ZaMTAXFormView.refreshListener}							
						]},								
						{type:_SPACER_, height:"1"},							
						{type:_ZAALLSCREEN_GROUPER_, numCols:6, width: "100%",colSizes:["18%","15%","17%","17%","17%","15%"],
							label:ZaMsg.PQV_Summary,
							items: [						
							{type:_ZASMALL_CENTER_GROUPER_, numCols:1,width:"100%", label:ZaMsg.PQV_GroupRDomain, items: [
								    {ref:ZaMTA.A_DeferredQ+"/"+ZaMTA.A_rdomain, type:_DWT_LIST_, height:"150", width:"97%", cssClass: "DLSource", 
							   		forceUpdate: false,createPopupMenu:ZaMTAXFormView.createPopupMenu, preserveSelection:true, multiselect:true,onSelection:ZaMTAXFormView.filterListSelectionListener, widgetClass:ZaQSummaryListView, headerList:headerList}								
								]
							},		
							{type:_ZASMALL_CENTER_GROUPER_, numCols:1, width: "100%", label:ZaMsg.PQV_GroupOriginIP,items: [
								    {ref:ZaMTA.A_DeferredQ+"/"+ZaMTA.A_origip, type:_DWT_LIST_, height:"150", width:"96%", cssClass: "DLSource", 
							   		forceUpdate: false,createPopupMenu:ZaMTAXFormView.createPopupMenu,preserveSelection:true, multiselect:true, onSelection:ZaMTAXFormView.filterListSelectionListener, widgetClass:ZaQSummaryListView, headerList:headerList}								
								]
							},		
							{type:_ZASMALL_CENTER_GROUPER_, numCols:1, width: "100%", label:ZaMsg.PQV_GroupSenderDomain,items: [
								    {ref:ZaMTA.A_DeferredQ+"/"+ZaMTA.A_sdomain, type:_DWT_LIST_, height:"150", width:"97%", cssClass: "DLSource", 
							   		forceUpdate: false,createPopupMenu:ZaMTAXFormView.createPopupMenu,multiselect:true, onSelection:ZaMTAXFormView.filterListSelectionListener, widgetClass:ZaQSummaryListView, headerList:headerList}								
								]
							},	
							{type:_ZASMALL_CENTER_GROUPER_, numCols:1,width: "100%", label:ZaMsg.PQV_GroupReceiverAddress,items: [
								    {ref:ZaMTA.A_DeferredQ+"/"+ZaMTA.A_raddress, type:_DWT_LIST_, height:"150", width:"97%", cssClass: "DLSource", 
							   		forceUpdate: false,createPopupMenu:ZaMTAXFormView.createPopupMenu,multiselect:true, onSelection:ZaMTAXFormView.filterListSelectionListener, widgetClass:ZaQSummaryListView, headerList:headerList}								
								]
							},		
							{type:_ZASMALL_CENTER_GROUPER_, numCols:1,width: "100%", label:ZaMsg.PQV_GroupSenderAddress,items: [
								    {ref:ZaMTA.A_DeferredQ+"/"+ZaMTA.A_saddress, type:_DWT_LIST_, height:"150", width:"97%", cssClass: "DLSource", 
							   		forceUpdate: false,createPopupMenu:ZaMTAXFormView.createPopupMenu,multiselect:true, onSelection:ZaMTAXFormView.filterListSelectionListener, widgetClass:ZaQSummaryListView, headerList:headerList}								
								]
							},		
							{type:_ZASMALL_CENTER_GROUPER_, numCols:1, width: "100%", label:ZaMsg.PQV_GroupError,items: [
								    {ref:ZaMTA.A_DeferredQ+"/"+ZaMTA.A_error, type:_DWT_LIST_, height:"150", width:"96%", cssClass: "DLSource", 
							   		forceUpdate: false,createPopupMenu:ZaMTAXFormView.createPopupMenu,multiselect:true, onSelection:ZaMTAXFormView.filterListSelectionListener, widgetClass:ZaQSummaryListView, headerList:headerList}								
								]
							}						
						]},
						{type:_SPACER_, height:"15"},	
																
						{type:_ZAALLSCREEN_GROUPER_, numCols:1, width:"100%", label:ZaMsg.PQV_Messages,  items: [
							{type:_GROUP_, numCols:9, colSizes:["16%", "2%", "16%", "2%", "16%", "2%", "16%", "2%", "28%"], tableCssClass:"search_field_tableCssClass", cssClass:"qsearch_field_bar", width:"95%", items: [
								{type:_DWT_BUTTON_,ref:ZaMTA.A_DeferredQ, label:ZaMsg.TBB_RequeueAll,onActivate:"ZaMTAXFormView.actionButtonListener.call(this,ZaMTA.ActionRequeue);",toolTipContent:ZaMsg.PQ_Requeue_tt},{type:_CELLSPACER_},
								{type:_DWT_BUTTON_,ref:ZaMTA.A_DeferredQ, label:ZaMsg.TBB_HoldAll,onActivate:"ZaMTAXFormView.actionButtonListener.call(this,ZaMTA.ActionHold);",toolTipContent:ZaMsg.PQ_Hold_tt},{type:_CELLSPACER_},							
								{type:_DWT_BUTTON_,ref:ZaMTA.A_DeferredQ, label:ZaMsg.PQ_DeleteAll,onActivate:"ZaMTAXFormView.actionButtonListener.call(this,ZaMTA.ActionDelete);" ,toolTipContent:ZaMsg.PQ_Delete_tt},{type:_CELLSPACER_},							
								{type:_DWT_BUTTON_, label:ZaMsg.PQ_showAllMsgs, ref:ZaMTA.A_DeferredQ, onActivate:ZaMTAXFormView.showAllMsgs},{type:_CELLSPACER_},
								{type:_GROUP_, numCols:3, items:[
									{type:_DWT_BUTTON_, label:ZaMsg.Previous,toolTipContent:ZaMsg.PrevPage_tt, width:86, id:"backButton", icon:"LeftArrow", disIcon:"LeftArrowDis",
										ref:ZaMTA.A_DeferredQ,
										onActivate:"ZaMTAXFormView.backMsgsButtonHndlr.call(this,event)", 
										enableDisableChangeEventSources:[ZaMTA.A_DeferredQ + "/" + ZaMTA.A_pageNum],
										enableDisableChecks:[[ZaMTAXFormView.shouldEnableMsgsBackButton,ZaMTA.A_DeferredQ]]
								    },								       
									{type:_CELLSPACER_},
									{type:_DWT_BUTTON_, label:ZaMsg.Next,toolTipContent:ZaMsg.NextPage_tt, width:86, id:"fwdButton", icon:"RightArrow", disIcon:"RightArrowDis",
										ref:ZaMTA.A_DeferredQ,labelLocation:(DwtLabel.IMAGE_RIGHT | DwtLabel.ALIGN_CENTER),
										enableDisableChangeEventSources:[ZaMTA.A_DeferredQ + "/" + ZaMTA.A_more],
										enableDisableChecks:[[ZaMTAXFormView.shouldEnableMsgsForwardButton,ZaMTA.A_DeferredQ]]										
										//onActivate:"ZaMTAXFormView.fwdMsgsButtonHndlr.call(this,event)", 
								    }]
								 }
							]},			
						    {ref:ZaMTA.A_DeferredQ+"/"+ZaMTA.A_messages, onSelection:ZaMTAXFormView.msgListSelectionListener, type:_DWT_LIST_, height:"200", width:"99%", cssClass: "DLSource",
						   		forceUpdate: false,createPopupMenu:ZaMTAXFormView.createPopupMenu,multiselect:true, widgetClass:ZaQMessagesListView, headerList:msgHeaderList}								
						]}		
					]
				},							
				{type:_ZATABCASE_, numCols:1,  caseKey:ZaMTAXFormView._tab2, 
					items:[	
						{type:_SPACER_, height:"15"},
						{type:_GROUP_,numCols:8, colSizes:["10%", "10%","10%", "15%", "15%", "25%", "auto", "10%"],tableCssClass:"search_field_tableCssClass", cssClass:"qsearch_field_bar", width:"95%", items: [
							{type:_OUTPUT_, label:ZaMsg.TBB_LastUpdated, ref:ZaMTA.A_IncomingQ+"/"+ZaMTA.A_refreshTime},
							{type:_OUTPUT_, label:ZaMsg.PQ_AnalyzerStatus, ref:ZaMTA.A_IncomingQ+"/"+ZaMTA.A_Status,choices:ZaMTA.SCANNER_STATUS_CHOICES},
							{type:_DWT_PROGRESS_BAR_,label:ZaMsg.PQ_ParsingProgress,
								maxValue:null,
								maxValueRef:ZaMTA.A_IncomingQ+"/"+ZaMTA.A_count,
								ref:ZaMTA.A_IncomingQ+"/"+ZaMTA.A_totalComplete,
								visibilityChecks:[[ZaMTAXFormView.isParsingProgressBarVisible,ZaMTA.A_IncomingQ]],
								visibilityChangeEventSources:[ZaMTA.A_IncomingQ+"/"+ZaMTA.A_Status],								
								align:_CENTER_,	
								wholeCssClass:"mtaprogressbar",
								progressCssClass:"progressused"
							},
							{type:_CELLSPACER_},
							{type:_DWT_BUTTON_,ref:ZaMTA.A_IncomingQ, label:ZaMsg.PQ_AnalyzeQueue,onActivate:ZaMTAXFormView.refreshListener}													
						]},							
						{type:_SPACER_, height:"1"},		
						{type:_ZAALLSCREEN_GROUPER_, numCols:5, width: "100%", label:ZaMsg.PQV_Summary, colSizes:["20%","20%","20%","20%","20%"], items:[
							{type:_ZASMALL_CENTER_GROUPER_, numCols:1,width:"100%", label:ZaMsg.PQV_GroupRDomain, items: [
								    {ref:ZaMTA.A_IncomingQ+"/"+ZaMTA.A_rdomain, type:_DWT_LIST_, height:"150", width:"97%", cssClass: "DLSource", 
							   		forceUpdate: false,createPopupMenu:ZaMTAXFormView.createPopupMenu, preserveSelection:true, multiselect:true,onSelection:ZaMTAXFormView.filterListSelectionListener, widgetClass:ZaQSummaryListView, headerList:headerList}								
								]
							},		
							{type:_ZASMALL_CENTER_GROUPER_, numCols:1, width: "100%", label:ZaMsg.PQV_GroupOriginIP,items: [
								    {ref:ZaMTA.A_IncomingQ+"/"+ZaMTA.A_origip, type:_DWT_LIST_, height:"150", width:"97%", cssClass: "DLSource", 
							   		forceUpdate: false,createPopupMenu:ZaMTAXFormView.createPopupMenu,preserveSelection:true, multiselect:true, onSelection:ZaMTAXFormView.filterListSelectionListener, widgetClass:ZaQSummaryListView, headerList:headerList}								
								]
							},		
							{type:_ZASMALL_CENTER_GROUPER_, numCols:1, width: "100%", label:ZaMsg.PQV_GroupSenderDomain,items: [
								    {ref:ZaMTA.A_IncomingQ+"/"+ZaMTA.A_sdomain, type:_DWT_LIST_, height:"150", width:"97%", cssClass: "DLSource", 
							   		forceUpdate: false,createPopupMenu:ZaMTAXFormView.createPopupMenu,multiselect:true, onSelection:ZaMTAXFormView.filterListSelectionListener, widgetClass:ZaQSummaryListView, headerList:headerList}								
								]
							},	
							{type:_ZASMALL_CENTER_GROUPER_, numCols:1,width: "100%", label:ZaMsg.PQV_GroupReceiverAddress,items: [
								    {ref:ZaMTA.A_IncomingQ+"/"+ZaMTA.A_raddress, type:_DWT_LIST_, height:"150", width:"97%", cssClass: "DLSource", 
							   		forceUpdate: false,createPopupMenu:ZaMTAXFormView.createPopupMenu,multiselect:true, onSelection:ZaMTAXFormView.filterListSelectionListener, widgetClass:ZaQSummaryListView, headerList:headerList}								
								]
							},		
							{type:_ZASMALL_CENTER_GROUPER_, numCols:1,width: "100%", label:ZaMsg.PQV_GroupSenderAddress,items: [
								    {ref:ZaMTA.A_IncomingQ+"/"+ZaMTA.A_saddress, type:_DWT_LIST_, height:"150", width:"97%", cssClass: "DLSource", 
							   		forceUpdate: false,createPopupMenu:ZaMTAXFormView.createPopupMenu,multiselect:true, onSelection:ZaMTAXFormView.filterListSelectionListener, widgetClass:ZaQSummaryListView, headerList:headerList}								
								]
							}						
						]},
						{type:_SPACER_, height:"15"},	
						{type:_ZAALLSCREEN_GROUPER_, numCols:1, width:"100%", label:ZaMsg.PQV_Messages,  items: [
							{type:_GROUP_, numCols:9, colSizes:["16%", "3%", "16%", "3%", "16%", "3%", "16%", "3%", "24%"], tableCssClass:"search_field_tableCssClass", cssClass:"qsearch_field_bar", width:"95%", items: [
								{type:_DWT_BUTTON_,ref:ZaMTA.A_IncomingQ, label:ZaMsg.TBB_RequeueAll,onActivate:"ZaMTAXFormView.actionButtonListener.call(this,ZaMTA.ActionRequeue)",toolTipContent:ZaMsg.PQ_Requeue_tt},{type:_CELLSPACER_},
								{type:_DWT_BUTTON_,ref:ZaMTA.A_IncomingQ, label:ZaMsg.TBB_HoldAll,onActivate:"ZaMTAXFormView.actionButtonListener.call(this,ZaMTA.ActionHold)",toolTipContent:ZaMsg.PQ_Hold_tt},{type:_CELLSPACER_},	
								{type:_DWT_BUTTON_,ref:ZaMTA.A_IncomingQ, label:ZaMsg.PQ_DeleteAll,onActivate:"ZaMTAXFormView.actionButtonListener.call(this,ZaMTA.ActionDelete)",toolTipContent:ZaMsg.PQ_Delete_tt},{type:_CELLSPACER_},
								{type:_DWT_BUTTON_, label:ZaMsg.PQ_showAllMsgs, ref:ZaMTA.A_IncomingQ, onActivate:ZaMTAXFormView.showAllMsgs},
								{type:_CELLSPACER_},
								{type:_GROUP_, numCols:3, items:[
									{type:_DWT_BUTTON_, label:ZaMsg.Previous,toolTipContent:ZaMsg.PrevPage_tt, width:86, id:"backButton", icon:"LeftArrow", disIcon:"LeftArrowDis", 	
										ref:ZaMTA.A_IncomingQ,
										onActivate:"ZaMTAXFormView.backMsgsButtonHndlr.call(this,event)", 
										enableDisableChangeEventSources:[ZaMTA.A_IncomingQ + "/" + ZaMTA.A_pageNum],
										enableDisableChecks:[[ZaMTAXFormView.shouldEnableMsgsBackButton,ZaMTA.A_IncomingQ]]										
								    },								       
									{type:_CELLSPACER_},
									{type:_DWT_BUTTON_, label:ZaMsg.Next,toolTipContent:ZaMsg.NextPage_tt, width:86, id:"fwdButton", icon:"RightArrow", disIcon:"RightArrowDis",	
										ref:ZaMTA.A_IncomingQ,labelLocation:(DwtLabel.IMAGE_RIGHT | DwtLabel.ALIGN_CENTER),
										onActivate:"ZaMTAXFormView.fwdMsgsButtonHndlr.call(this,event)", 
										enableDisableChangeEventSources:[ZaMTA.A_IncomingQ + "/" + ZaMTA.A_more],
										enableDisableChecks:[[ZaMTAXFormView.shouldEnableMsgsForwardButton,ZaMTA.A_IncomingQ]]										
								    }]
								}														
							]},										
						    {ref:ZaMTA.A_IncomingQ+"/"+ZaMTA.A_messages, onSelection:ZaMTAXFormView.msgListSelectionListener, type:_DWT_LIST_, height:"200", width:"99%", cssClass: "DLSource", 
						   		forceUpdate: false,createPopupMenu:ZaMTAXFormView.createPopupMenu,multiselect:true, widgetClass:ZaQMessagesListView, headerList:msgHeaderList}								
								]
							}		
					]
				},
				{type:_ZATABCASE_, numCols:1, cssClass:(AjxEnv.isIE ? "IEcontainer" : "XFormCase"), width:"100%",
					caseKey:ZaMTAXFormView._tab3, 
					items:[	
						{type:_SPACER_, height:"15"},
						{type:_GROUP_,numCols:8, colSizes:["10%", "10%","10%", "15%", "15%", "25%", "auto", "10%"],tableCssClass:"search_field_tableCssClass", cssClass:"qsearch_field_bar", width:"95%", items: [
							{type:_OUTPUT_, label:ZaMsg.TBB_LastUpdated, ref:ZaMTA.A_ActiveQ+"/"+ZaMTA.A_refreshTime},
							{type:_OUTPUT_, label:ZaMsg.PQ_AnalyzerStatus, ref:ZaMTA.A_ActiveQ+"/"+ZaMTA.A_Status,choices:ZaMTA.SCANNER_STATUS_CHOICES},							
							{type:_DWT_PROGRESS_BAR_,label:ZaMsg.PQ_ParsingProgress,
								maxValue:null,
								maxValueRef:ZaMTA.A_ActiveQ+"/"+ZaMTA.A_count,
								ref:ZaMTA.A_ActiveQ+"/"+ZaMTA.A_totalComplete,
								visibilityChecks:[[ZaMTAXFormView.isParsingProgressBarVisible,ZaMTA.A_ActiveQ]],
								visibilityChangeEventSources:[ZaMTA.A_ActiveQ+"/"+ZaMTA.A_Status],								
								align:_CENTER_,	
								wholeCssClass:"mtaprogressbar",
								progressCssClass:"progressused"
							},
							{type:_CELLSPACER_},							
							{type:_DWT_BUTTON_,ref:ZaMTA.A_ActiveQ, label:ZaMsg.PQ_AnalyzeQueue,onActivate:ZaMTAXFormView.refreshListener}
						]},								
						{type:_SPACER_, height:"1"},							
						{type:_ZAALLSCREEN_GROUPER_, numCols:5, width: "100%", label:ZaMsg.PQV_Summary, colSizes:["20%","20%","20%","20%","20%"], items:[
							{type:_ZASMALL_CENTER_GROUPER_, numCols:1,width:"100%", label:ZaMsg.PQV_GroupRDomain, items: [
							    {ref:ZaMTA.A_ActiveQ+"/"+ZaMTA.A_rdomain, type:_DWT_LIST_, height:"150", width:"97%", cssClass: "DLSource", 
							   		forceUpdate: false,createPopupMenu:ZaMTAXFormView.createPopupMenu, preserveSelection:true, multiselect:true,onSelection:ZaMTAXFormView.filterListSelectionListener, widgetClass:ZaQSummaryListView, headerList:headerList}								
							]},		
							{type:_ZASMALL_CENTER_GROUPER_, numCols:1, width: "100%", label:ZaMsg.PQV_GroupOriginIP,items: [
								    {ref:ZaMTA.A_ActiveQ+"/"+ZaMTA.A_origip, type:_DWT_LIST_, height:"150", width:"97%", cssClass: "DLSource", 
							   		forceUpdate: false,createPopupMenu:ZaMTAXFormView.createPopupMenu,preserveSelection:true, multiselect:true, onSelection:ZaMTAXFormView.filterListSelectionListener, widgetClass:ZaQSummaryListView, headerList:headerList}								
								]
							},		
							{type:_ZASMALL_CENTER_GROUPER_, numCols:1, width: "100%", label:ZaMsg.PQV_GroupSenderDomain,items: [
								    {ref:ZaMTA.A_ActiveQ+"/"+ZaMTA.A_sdomain, type:_DWT_LIST_, height:"150", width:"97%", cssClass: "DLSource", 
							   		forceUpdate: false,createPopupMenu:ZaMTAXFormView.createPopupMenu,multiselect:true, onSelection:ZaMTAXFormView.filterListSelectionListener, widgetClass:ZaQSummaryListView, headerList:headerList}								
								]
							},	
							{type:_ZASMALL_CENTER_GROUPER_, numCols:1,width: "100%", label:ZaMsg.PQV_GroupReceiverAddress,items: [
								    {ref:ZaMTA.A_ActiveQ+"/"+ZaMTA.A_raddress, type:_DWT_LIST_, height:"150", width:"97%", cssClass: "DLSource", 
							   		forceUpdate: false,createPopupMenu:ZaMTAXFormView.createPopupMenu,multiselect:true, onSelection:ZaMTAXFormView.filterListSelectionListener, widgetClass:ZaQSummaryListView, headerList:headerList}								
								]
							},		
							{type:_ZASMALL_CENTER_GROUPER_, numCols:1,width: "100%", label:ZaMsg.PQV_GroupSenderAddress,items: [
								    {ref:ZaMTA.A_ActiveQ+"/"+ZaMTA.A_saddress, type:_DWT_LIST_, height:"150", width:"97%", cssClass: "DLSource", 
							   		forceUpdate: false,createPopupMenu:ZaMTAXFormView.createPopupMenu,multiselect:true, onSelection:ZaMTAXFormView.filterListSelectionListener, widgetClass:ZaQSummaryListView, headerList:headerList}								
								]
							}						
						]},
						{type:_SPACER_, height:"15"},	
						{type:_ZAALLSCREEN_GROUPER_, numCols:1, width:"100%", label:ZaMsg.PQV_Messages,  items: [
							{type:_GROUP_, numCols:9, colSizes:["16%", "3%", "16%", "3%", "16%", "3%", "16%", "3%", "24%"], tableCssClass:"search_field_tableCssClass", cssClass:"qsearch_field_bar", width:"95%", items: [
								{type:_DWT_BUTTON_,ref:ZaMTA.A_ActiveQ, label:ZaMsg.TBB_RequeueAll,onActivate:"ZaMTAXFormView.actionButtonListener.call(this,ZaMTA.ActionRequeue)",toolTipContent:ZaMsg.PQ_Requeue_tt},{type:_CELLSPACER_},
								{type:_DWT_BUTTON_,ref:ZaMTA.A_ActiveQ, label:ZaMsg.TBB_HoldAll,onActivate:"ZaMTAXFormView.actionButtonListener.call(this,ZaMTA.ActionHold)",toolTipContent:ZaMsg.PQ_Hold_tt},{type:_CELLSPACER_},	
								{type:_DWT_BUTTON_,ref:ZaMTA.A_ActiveQ, label:ZaMsg.PQ_DeleteAll,onActivate:"ZaMTAXFormView.actionButtonListener.call(this,ZaMTA.ActionDelete)",toolTipContent:ZaMsg.PQ_Delete_tt},{type:_CELLSPACER_},
								{type:_DWT_BUTTON_, label:ZaMsg.PQ_showAllMsgs, ref:ZaMTA.A_ActiveQ, onActivate:ZaMTAXFormView.showAllMsgs},
								{type:_CELLSPACER_},
								{type:_GROUP_, numCols:3, items:[					
									{type:_DWT_BUTTON_, label:ZaMsg.Previous,toolTipContent:ZaMsg.PrevPage_tt, width:86, id:"backButton", icon:"LeftArrow", disIcon:"LeftArrowDis", 	
										ref:ZaMTA.A_ActiveQ,
										onActivate:"ZaMTAXFormView.backMsgsButtonHndlr.call(this,event)",
										enableDisableChangeEventSources:[ZaMTA.A_ActiveQ + "/" + ZaMTA.A_pageNum],
										enableDisableChecks:[[ZaMTAXFormView.shouldEnableMsgsBackButton,ZaMTA.A_ActiveQ]]	
								    },								       
									{type:_CELLSPACER_},
									{type:_DWT_BUTTON_, label:ZaMsg.Next,toolTipContent:ZaMsg.NextPage_tt, width:86, id:"fwdButton", icon:"RightArrow", disIcon:"RightArrowDis",	
										ref:ZaMTA.A_ActiveQ,labelLocation:(DwtLabel.IMAGE_RIGHT | DwtLabel.ALIGN_CENTER),
										onActivate:"ZaMTAXFormView.fwdMsgsButtonHndlr.call(this,event)",
										enableDisableChangeEventSources:[ZaMTA.A_ActiveQ + "/" + ZaMTA.A_more],
										enableDisableChecks:[[ZaMTAXFormView.shouldEnableMsgsForwardButton,ZaMTA.A_ActiveQ]]										 
								    }
								 ]}
							]},									
							{ref:ZaMTA.A_ActiveQ+"/"+ZaMTA.A_messages, onSelection:ZaMTAXFormView.msgListSelectionListener, type:_DWT_LIST_, height:"200", width:"99%", cssClass: "DLSource", 
							   		forceUpdate: false,createPopupMenu:ZaMTAXFormView.createPopupMenu,multiselect:true, widgetClass:ZaQMessagesListView, headerList:msgHeaderList}								
						]}		
					]
				},
				{type:_ZATABCASE_, numCols:1, cssClass:(AjxEnv.isIE ? "IEcontainer" : "XFormCase"), width:"100%",
					caseKey:ZaMTAXFormView._tab4, 
					items:[	
						{type:_SPACER_, height:"15"},
						{type:_GROUP_,numCols:8, colSizes:["10%", "10%","10%", "15%", "15%", "25%", "auto", "10%"],tableCssClass:"search_field_tableCssClass", cssClass:"qsearch_field_bar", width:"95%", items: [
							{type:_OUTPUT_, label:ZaMsg.TBB_LastUpdated, ref:ZaMTA.A_HoldQ+"/"+ZaMTA.A_refreshTime},
							{type:_OUTPUT_, label:ZaMsg.PQ_AnalyzerStatus, ref:ZaMTA.A_HoldQ+"/"+ZaMTA.A_Status,choices:ZaMTA.SCANNER_STATUS_CHOICES},							
							{type:_DWT_PROGRESS_BAR_,label:ZaMsg.PQ_ParsingProgress,
								maxValue:null,
								maxValueRef:ZaMTA.A_HoldQ+"/"+ZaMTA.A_count,
								ref:ZaMTA.A_HoldQ+"/"+ZaMTA.A_totalComplete,
								visibilityChecks:[[ZaMTAXFormView.isParsingProgressBarVisible,ZaMTA.A_HoldQ]],
								visibilityChangeEventSources:[ZaMTA.A_HoldQ+"/"+ZaMTA.A_Status],								
								align:_CENTER_,	
								wholeCssClass:"mtaprogressbar",
								progressCssClass:"progressused"
							},
							{type:_CELLSPACER_},							
							{type:_DWT_BUTTON_,ref:ZaMTA.A_HoldQ, label:ZaMsg.PQ_AnalyzeQueue,onActivate:ZaMTAXFormView.refreshListener}
						]},							
						{type:_SPACER_, height:"1"},							
						{type:_ZAALLSCREEN_GROUPER_, numCols:5, width: "100%", label:ZaMsg.PQV_Summary, colSizes:["20%","20%","20%","20%","20%"], items:[
							{type:_ZASMALL_CENTER_GROUPER_, numCols:1,width:"100%", label:ZaMsg.PQV_GroupRDomain, items: [
								    {ref:ZaMTA.A_HoldQ+"/"+ZaMTA.A_rdomain, type:_DWT_LIST_, height:"150", width:"97%", cssClass: "DLSource", 
							   		forceUpdate: false,createPopupMenu:ZaMTAXFormView.createPopupMenu, preserveSelection:true, multiselect:true,onSelection:ZaMTAXFormView.filterListSelectionListener, widgetClass:ZaQSummaryListView, headerList:headerList}								
								]
							},		
							{type:_ZASMALL_CENTER_GROUPER_, numCols:1, width: "100%", label:ZaMsg.PQV_GroupOriginIP,items: [
								    {ref:ZaMTA.A_HoldQ+"/"+ZaMTA.A_origip, type:_DWT_LIST_, height:"150", width:"97%", cssClass: "DLSource", 
							   		forceUpdate: false,createPopupMenu:ZaMTAXFormView.createPopupMenu,preserveSelection:true, multiselect:true, onSelection:ZaMTAXFormView.filterListSelectionListener, widgetClass:ZaQSummaryListView, headerList:headerList}								
								]
							},		
							{type:_ZASMALL_CENTER_GROUPER_, numCols:1, width: "100%", label:ZaMsg.PQV_GroupSenderDomain,items: [
								    {ref:ZaMTA.A_HoldQ+"/"+ZaMTA.A_sdomain, type:_DWT_LIST_, height:"150", width:"97%", cssClass: "DLSource", 
							   		forceUpdate: false,createPopupMenu:ZaMTAXFormView.createPopupMenu,multiselect:true, onSelection:ZaMTAXFormView.filterListSelectionListener, widgetClass:ZaQSummaryListView, headerList:headerList}								
								]
							},	
							{type:_ZASMALL_CENTER_GROUPER_, numCols:1,width: "100%", label:ZaMsg.PQV_GroupReceiverAddress,items: [
								    {ref:ZaMTA.A_HoldQ+"/"+ZaMTA.A_raddress, type:_DWT_LIST_, height:"150", width:"97%", cssClass: "DLSource", 
							   		forceUpdate: false,createPopupMenu:ZaMTAXFormView.createPopupMenu,multiselect:true, onSelection:ZaMTAXFormView.filterListSelectionListener, widgetClass:ZaQSummaryListView, headerList:headerList}								
								]
							},		
							{type:_ZASMALL_CENTER_GROUPER_, numCols:1,width: "100%", label:ZaMsg.PQV_GroupSenderAddress,items: [
								    {ref:ZaMTA.A_HoldQ+"/"+ZaMTA.A_saddress, type:_DWT_LIST_, height:"150", width:"97%", cssClass: "DLSource", 
							   		forceUpdate: false,createPopupMenu:ZaMTAXFormView.createPopupMenu,multiselect:true, onSelection:ZaMTAXFormView.filterListSelectionListener, widgetClass:ZaQSummaryListView, headerList:headerList}								
								]
							}						
						]},
						{type:_SPACER_, height:"15"},	
						{type:_ZAALLSCREEN_GROUPER_, numCols:1, width:"100%", label:ZaMsg.PQV_Messages,  items: [
							{type:_GROUP_, numCols:9, colSizes:["16%", "3%", "16%", "3%", "16%", "3%", "16%", "3%", "24%"], tableCssClass:"search_field_tableCssClass", cssClass:"qsearch_field_bar", width:"95%", items: [
								{type:_DWT_BUTTON_,ref:ZaMTA.A_HoldQ, label:ZaMsg.TBB_RequeueAll,onActivate:"ZaMTAXFormView.actionButtonListener.call(this,ZaMTA.ActionRequeue)",toolTipContent:ZaMsg.PQ_Requeue_tt},{type:_CELLSPACER_},
								{type:_DWT_BUTTON_,ref:ZaMTA.A_HoldQ, label:ZaMsg.TBB_ReleaseAll,onActivate:"ZaMTAXFormView.actionButtonListener.call(this,ZaMTA.ActionRelease)",toolTipContent:ZaMsg.PQ_Release_tt},{type:_CELLSPACER_},	
								{type:_DWT_BUTTON_,ref:ZaMTA.A_HoldQ, label:ZaMsg.PQ_DeleteAll,onActivate:"ZaMTAXFormView.actionButtonListener.call(this,ZaMTA.ActionDelete)",toolTipContent:ZaMsg.PQ_Delete_tt},{type:_CELLSPACER_},
								{type:_DWT_BUTTON_, label:ZaMsg.PQ_showAllMsgs, ref:ZaMTA.A_HoldQ, onActivate:ZaMTAXFormView.showAllMsgs},
								{type:_CELLSPACER_},
								{type:_GROUP_, numCols:3, items:[								
									{type:_DWT_BUTTON_, label:ZaMsg.Previous,toolTipContent:ZaMsg.PrevPage_tt, width:86, id:"backButton", icon:"LeftArrow", disIcon:"LeftArrowDis", 	
										ref:ZaMTA.A_HoldQ,
										onActivate:"ZaMTAXFormView.backMsgsButtonHndlr.call(this,event)",
										enableDisableChangeEventSources:[ZaMTA.A_HoldQ + "/" + ZaMTA.A_pageNum],
										enableDisableChecks:[[ZaMTAXFormView.shouldEnableMsgsBackButton,ZaMTA.A_HoldQ]]										 
								    },								       
									{type:_CELLSPACER_},
									{type:_DWT_BUTTON_, label:ZaMsg.Next,toolTipContent:ZaMsg.NextPage_tt, width:86, id:"fwdButton", icon:"RightArrow", disIcon:"RightArrowDis",	
										ref:ZaMTA.A_HoldQ,labelLocation:(DwtLabel.IMAGE_RIGHT | DwtLabel.ALIGN_CENTER),
										onActivate:"ZaMTAXFormView.fwdMsgsButtonHndlr.call(this,event)",
										enableDisableChangeEventSources:[ZaMTA.A_HoldQ + "/" + ZaMTA.A_more],
										enableDisableChecks:[[ZaMTAXFormView.shouldEnableMsgsForwardButton,ZaMTA.A_HoldQ]]										 
							    	}
							    ]}
							]},								
						    {ref:ZaMTA.A_HoldQ+"/"+ZaMTA.A_messages, onSelection:ZaMTAXFormView.msgListSelectionListener, type:_DWT_LIST_, height:"200", width:"99%", cssClass: "DLSource", 
						   		forceUpdate: false,createPopupMenu:ZaMTAXFormView.createPopupMenu,multiselect:true, widgetClass:ZaQMessagesListView, headerList:msgHeaderList
						   	}								
						]}		
					]
				},											
					
				{type:_ZATABCASE_, numCols:1, cssClass:(AjxEnv.isIE ? "IEcontainer" : "XFormCase"), width:"100%",
					caseKey:ZaMTAXFormView._tab5, 
					items:[	
						{type:_SPACER_, height:"15"},
						
						{type:_GROUP_,numCols:8, colSizes:["10%", "10%","10%", "15%", "15%", "25%", "auto", "10%"],tableCssClass:"search_field_tableCssClass", cssClass:"qsearch_field_bar", width:"95%", items: [
							{type:_OUTPUT_, label:ZaMsg.TBB_LastUpdated, ref:ZaMTA.A_CorruptQ+"/"+ZaMTA.A_refreshTime},
							{type:_OUTPUT_, label:ZaMsg.PQ_AnalyzerStatus, ref:ZaMTA.A_CorruptQ+"/"+ZaMTA.A_Status,choices:ZaMTA.SCANNER_STATUS_CHOICES},							
							{type:_DWT_PROGRESS_BAR_,label:ZaMsg.PQ_ParsingProgress,
								maxValue:null,
								maxValueRef:ZaMTA.A_CorruptQ+"/"+ZaMTA.A_count,
								ref:ZaMTA.A_CorruptQ+"/"+ZaMTA.A_totalComplete,
								visibilityChecks:[[ZaMTAXFormView.isParsingProgressBarVisible,ZaMTA.A_CorruptQ]],
								visibilityChangeEventSources:[ZaMTA.A_CorruptQ+"/"+ZaMTA.A_Status],								
								align:_CENTER_,	
								wholeCssClass:"mtaprogressbar",
								progressCssClass:"progressused"
							},
							{type:_CELLSPACER_},							
							{type:_DWT_BUTTON_,ref:ZaMTA.A_CorruptQ, label:ZaMsg.PQ_AnalyzeQueue,onActivate:ZaMTAXFormView.refreshListener}
						]},							
						{type:_SPACER_, height:"1"},							
						{type:_ZAALLSCREEN_GROUPER_, numCols:5, width: "100%", label:ZaMsg.PQV_Summary, colSizes:["20%","20%","20%","20%","20%"], items:[
							{type:_ZASMALL_CENTER_GROUPER_, numCols:1,width:"100%", label:ZaMsg.PQV_GroupRDomain, items: [
								    {ref:ZaMTA.A_CorruptQ+"/"+ZaMTA.A_rdomain, type:_DWT_LIST_, height:"150", width:"97%", cssClass: "DLSource", 
							   		forceUpdate: false,createPopupMenu:ZaMTAXFormView.createPopupMenu, preserveSelection:true, multiselect:true,onSelection:ZaMTAXFormView.filterListSelectionListener, widgetClass:ZaQSummaryListView, headerList:headerList}								
								]
							},		
							{type:_ZASMALL_CENTER_GROUPER_, numCols:1, width: "100%", label:ZaMsg.PQV_GroupOriginIP,items: [
								    {ref:ZaMTA.A_CorruptQ+"/"+ZaMTA.A_origip, type:_DWT_LIST_, height:"150", width:"97%", cssClass: "DLSource", 
							   		forceUpdate: false,createPopupMenu:ZaMTAXFormView.createPopupMenu,preserveSelection:true, multiselect:true, onSelection:ZaMTAXFormView.filterListSelectionListener, widgetClass:ZaQSummaryListView, headerList:headerList}								
								]
							},		
							{type:_ZASMALL_CENTER_GROUPER_, numCols:1, width: "100%", label:ZaMsg.PQV_GroupSenderDomain,items: [
								    {ref:ZaMTA.A_CorruptQ+"/"+ZaMTA.A_sdomain, type:_DWT_LIST_, height:"150", width:"97%", cssClass: "DLSource", 
							   		forceUpdate: false,createPopupMenu:ZaMTAXFormView.createPopupMenu,multiselect:true, onSelection:ZaMTAXFormView.filterListSelectionListener, widgetClass:ZaQSummaryListView, headerList:headerList}								
								]
							},	
							{type:_ZASMALL_CENTER_GROUPER_, numCols:1,width: "100%", label:ZaMsg.PQV_GroupReceiverAddress,items: [
								    {ref:ZaMTA.A_CorruptQ+"/"+ZaMTA.A_raddress, type:_DWT_LIST_, height:"150", width:"97%", cssClass: "DLSource", 
							   		forceUpdate: false,createPopupMenu:ZaMTAXFormView.createPopupMenu,multiselect:true, onSelection:ZaMTAXFormView.filterListSelectionListener, widgetClass:ZaQSummaryListView, headerList:headerList}								
								]
							},		
							{type:_ZASMALL_CENTER_GROUPER_, numCols:1,width: "100%", label:ZaMsg.PQV_GroupSenderAddress,items: [
								    {ref:ZaMTA.A_CorruptQ+"/"+ZaMTA.A_saddress, type:_DWT_LIST_, height:"150", width:"97%", cssClass: "DLSource", 
							   		forceUpdate: false,createPopupMenu:ZaMTAXFormView.createPopupMenu,multiselect:true, onSelection:ZaMTAXFormView.filterListSelectionListener, widgetClass:ZaQSummaryListView, headerList:headerList}								
								]
							}						
						]},
						{type:_SPACER_, height:"15"},	
						{type:_ZAALLSCREEN_GROUPER_, numCols:1, width:"100%", label:ZaMsg.PQV_Messages,  items: [
						{type:_GROUP_, numCols:9, colSizes:["16%", "3%", "16%", "3%", "16%", "3%", "16%", "3%", "24%"], tableCssClass:"search_field_tableCssClass", cssClass:"qsearch_field_bar", width:"95%", items: [
							{type:_DWT_BUTTON_,ref:ZaMTA.A_CorruptQ, label:ZaMsg.TBB_RequeueAll,onActivate:"ZaMTAXFormView.actionButtonListener.call(this,ZaMTA.ActionRequeue)",toolTipContent:ZaMsg.PQ_Requeue_tt},{type:_CELLSPACER_},
							{type:_DWT_BUTTON_,ref:ZaMTA.A_CorruptQ, label:ZaMsg.TBB_HoldAll,onActivate:"ZaMTAXFormView.actionButtonListener.call(this,ZaMTA.ActionHold)",toolTipContent:ZaMsg.PQ_Hold_tt},{type:_CELLSPACER_},	
							{type:_DWT_BUTTON_,ref:ZaMTA.A_CorruptQ, label:ZaMsg.PQ_DeleteAll,onActivate:"ZaMTAXFormView.actionButtonListener.call(this,ZaMTA.ActionDelete)",toolTipContent:ZaMsg.PQ_Delete_tt},{type:_CELLSPACER_},
							{type:_DWT_BUTTON_, label:ZaMsg.PQ_showAllMsgs, ref:ZaMTA.A_CorruptQ, onActivate:ZaMTAXFormView.showAllMsgs},
							{type:_CELLSPACER_},
							{type:_GROUP_, numCols:3, items:[
								{type:_DWT_BUTTON_, label:ZaMsg.Previous,toolTipContent:ZaMsg.PrevPage_tt, width:86, id:"backButton", icon:"LeftArrow", disIcon:"LeftArrowDis", 	
									ref:ZaMTA.A_CorruptQ,
									onActivate:"ZaMTAXFormView.backMsgsButtonHndlr.call(this,event)",
									enableDisableChangeEventSources:[ZaMTA.A_CorruptQ + "/" + ZaMTA.A_pageNum],
									enableDisableChecks:[[ZaMTAXFormView.shouldEnableMsgsBackButton,ZaMTA.A_CorruptQ]]									 
							    },								       
								{type:_CELLSPACER_},
								{type:_DWT_BUTTON_, label:ZaMsg.Next,toolTipContent:ZaMsg.NextPage_tt, width:86, id:"fwdButton", icon:"RightArrow", disIcon:"RightArrowDis",	
									ref:ZaMTA.A_CorruptQ,labelLocation:(DwtLabel.IMAGE_RIGHT | DwtLabel.ALIGN_CENTER),
									onActivate:"ZaMTAXFormView.fwdMsgsButtonHndlr.call(this,event)",
									enableDisableChangeEventSources:[ZaMTA.A_CorruptQ + "/" + ZaMTA.A_more],
									enableDisableChecks:[[ZaMTAXFormView.shouldEnableMsgsForwardButton,ZaMTA.A_CorruptQ]]									 
							    }]
							 }					
						]},									
					    {ref:ZaMTA.A_CorruptQ+"/"+ZaMTA.A_messages, onSelection:ZaMTAXFormView.msgListSelectionListener, type:_DWT_LIST_, height:"200", width:"99%", cssClass: "DLSource", 
						   		forceUpdate: false,createPopupMenu:ZaMTAXFormView.createPopupMenu,multiselect:true, widgetClass:ZaQMessagesListView, headerList:msgHeaderList}								
							]
						}		
					]
				}											
			]
		}
	]
};
ZaTabView.XFormModifiers["ZaMTAXFormView"].push(ZaMTAXFormView.myXFormModifier);
