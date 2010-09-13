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
* @class ZaMTAController controls display of a single Server's Postfix Queue
* @contructor ZaMTAController
* @param appCtxt
* @param container
* @param abApp
* @author Greg Solovyev
**/

ZaMTAController = function(appCtxt, container) {
	ZaXFormViewController.call(this, appCtxt, container,"ZaMTAController");
	this._UICreated = false;
	this._helpURL = location.pathname + ZaUtil.HELP_URL + "monitoring/monitoring_zimbra_mta_mail_queues.htm?locid="+AjxEnv.DEFAULT_LOCALE;
	this._toolbarOperations = new Array();
	this.objType = ZaEvent.S_MTA;	
	this.tabConstructor = ZaMTAXFormView;	
}

ZaMTAController.prototype = new ZaXFormViewController();
ZaMTAController.prototype.constructor = ZaMTAController;

ZaController.initToolbarMethods["ZaMTAController"] = new Array();
ZaController.setViewMethods["ZaMTAController"] = new Array();
ZaController.changeActionsStateMethods["ZaMTAController"] = new Array();
/**
*	@method show
*	@param entry - isntance of ZaServer class
*/
ZaMTAController.prototype.show = 
function(entry) {
	if (! this.selectExistingTabByItemId(entry.id)){
		this._setView(entry, true);
		this.setDirty(false);
	}
}

/**
*	@method setViewMethod 
*	@param entry - isntance of ZaDomain class
*/
ZaMTAController.setViewMethod =
function(entry) {
	entry.load();
	if(!this._UICreated) {
		this._createUI();
	} 
	//ZaApp.getInstance().pushView(ZaZimbraAdmin._POSTQ_BY_SERVER_VIEW);
	ZaApp.getInstance().pushView(this.getContentViewId());
	this._view.setDirty(false);
	this._view.setObject(entry); 	//setObject is delayed to be called after pushView in order to avoid jumping of the view	
	this._currentObject = entry;
}
ZaController.setViewMethods["ZaMTAController"].push(ZaMTAController.setViewMethod);

/**
* @method initToolbarMethod
* This method creates ZaOperation objects 
* All the ZaOperation objects are added to this._toolbarOperations array which is then used to 
* create the toolbar for this view.
* Each ZaOperation object defines one toolbar button.
* Help button is always the last button in the toolbar
**/
ZaMTAController.initToolbarMethod = 
function () {
	//this._toolbarOperations[ZaOperation.LABEL]=new ZaOperation(ZaOperation.LABEL,ZaMsg.TBB_LastUpdated, ZaMsg.TBB_LastUpdated_tt, null, null, null,null,null,null,"refreshTime"));	
//	this._toolbarOperations[ZaOperation.SEP] = new ZaOperation(ZaOperation.SEP);
	this._toolbarOrder.push(ZaOperation.FLUSH);
	this._toolbarOrder.push(ZaOperation.CLOSE);
	this._toolbarOperations[ZaOperation.FLUSH]=new ZaOperation(ZaOperation.FLUSH,ZaMsg.TBB_FlushQs, ZaMsg.TBB_TBB_FlushQs_tt, "FlushAllQueues", "FlushAllQueues", new AjxListener(this, this.flushListener));	
	this._toolbarOperations[ZaOperation.CLOSE]=new ZaOperation(ZaOperation.CLOSE,ZaMsg.TBB_Close, ZaMsg.SERTBB_Close_tt, "Close", "CloseDis", new AjxListener(this, this.closeButtonListener));    	
}
ZaController.initToolbarMethods["ZaMTAController"].push(ZaMTAController.initToolbarMethod);

/**
* @method _createUI
**/
ZaMTAController.prototype._createUI =
function () {
	this._contentView = this._view = new this.tabConstructor(this._container);

	this._initToolbar();
	//always add Help button at the end of the toolbar
	this._toolbarOperations[ZaOperation.NONE] = new ZaOperation(ZaOperation.NONE);
	this._toolbarOperations[ZaOperation.HELP]=new ZaOperation(ZaOperation.HELP,ZaMsg.TBB_Help, ZaMsg.TBB_Help_tt, "Help", "Help", new AjxListener(this, this._helpButtonListener));
	this._toolbarOrder.push(ZaOperation.NONE);
	this._toolbarOrder.push(ZaOperation.HELP);								
	this._toolbar = new ZaToolBar(this._container, this._toolbarOperations,this._toolbarOrder, null, null, ZaId.VIEW_MTA);		
	
	var elements = new Object();
	elements[ZaAppViewMgr.C_APP_CONTENT] = this._view;
	elements[ZaAppViewMgr.C_TOOLBAR_TOP] = this._toolbar;		
    //ZaApp.getInstance().createView(ZaZimbraAdmin._POSTQ_BY_SERVER_VIEW, elements);
    var tabParams = {
			openInNewTab: true,
			tabId: this.getContentViewId()
		}
	ZaApp.getInstance().createView(this.getContentViewId(), elements, tabParams) ;
	this._UICreated = true;
	ZaApp.getInstance()._controllers[this.getContentViewId ()] = this ;
}


ZaMTAController.prototype.flushListener = function () {
	//ZaApp.getInstance().dialogs["confirmMessageDialog"] = ZaApp.getInstance().dialogs["confirmMessageDialog"] = new ZaMsgDialog(this._view.shell, null, [DwtDialog.YES_BUTTON, DwtDialog.NO_BUTTON]);					
	ZaApp.getInstance().dialogs["confirmMessageDialog"].setMessage(ZaMsg.Q_FLUSH_QUEUES,  DwtMessageDialog.WARNING_STYLE);
	ZaApp.getInstance().dialogs["confirmMessageDialog"].registerCallback(DwtDialog.YES_BUTTON, this.flushQueues, this);		
	ZaApp.getInstance().dialogs["confirmMessageDialog"].registerCallback(DwtDialog.NO_BUTTON, this.closeCnfrmDlg, this, null);				
	ZaApp.getInstance().dialogs["confirmMessageDialog"].popup();
}

ZaMTAController.prototype.flushQueues = function () {
	try {
		this._currentObject.flushQueues();
	} catch (ex) {
		this._handleException(ex, "ZaMTAController.prototype.flushQueues");
	}
	this.closeCnfrmDlg();
}

/**
* @param ev
* This listener is invoked by ZaMTAController or any other controller that can change a ZaMTA object
**/
ZaMTAController.prototype.handleMTAChange =
function (ev) {
    if(ev && this._view && (this._view.__internalId==ZaApp.getInstance().getAppViewMgr().getCurrentView())) {
        if(ev.getDetail("obj") && (ev.getDetail("obj") instanceof ZaMTA) ) {
            if(this._currentObject && this._currentObject[ZaItem.A_zimbraId] == ev.getDetail("obj")[ZaItem.A_zimbraId]) {
                this._currentObject = ev.getDetail("obj");
                var qName = ev.getDetail("qName");

                if(qName && ev.getDetail("poll")) {
                        var pageNum = 0;
                        if(ev.getDetail("offset") != undefined) {
                                if(ev.getDetail("offset") > 0)
                                        pageNum = ev.getDetail("offset")/ZaMTA.RESULTSPERPAGE;

                        }
                        this._currentObject[qName][ZaMTA.A_pageNum] = pageNum;
                        if(this._currentObject[qName][ZaMTA.A_Status]==ZaMTA.STATUS_SCANNING) {
                                var ta = new AjxTimedAction(this._currentObject, ZaMTA.prototype.getMailQStatus, qName, ev.getDetail("query"),ev.getDetail("offset"),ev.getDetail("limit"),ev.getDetail("force"));
                                AjxTimedAction.scheduleAction(ta, ZaMTA.POLL_INTERVAL);
                        }
                }
                this._view.setObject(this._currentObject);
            }
        }
    }
}
