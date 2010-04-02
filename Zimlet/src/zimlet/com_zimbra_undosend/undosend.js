/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Zimlets
 * Copyright (C) 2009, 2010 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.3 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 * @Author Raja Rao DV (rrao@zimbra.com)
 * Highlights email contents to differentiate b/w original, Replied and Forwarded parts
 */

function Com_Zimbra_UndoSendHdlr() {
}

Com_Zimbra_UndoSendHdlr.prototype = new ZmZimletBase();
Com_Zimbra_UndoSendHdlr.prototype.constructor = Com_Zimbra_UndoSendHdlr;

Com_Zimbra_UndoSendHdlr.prototype.init = 
function() {
	this.undeSend_howMuchDelay = parseInt(this.getUserProperty("undeSend_howMuchDelay"));
};

Com_Zimbra_UndoSendHdlr.prototype.initializeToolbar = 
function(app, toolbar, controller, viewId) {
	if (viewId.indexOf("COMPOSE") >= 0) {
		var sendBtn = toolbar.getButton("SEND");
		sendBtn.removeSelectionListeners();//remove all selection listeners
		sendBtn.addSelectionListener(new AjxListener(this, this._sendButtonListener, controller));
	} else if(app.getName() == "Mail" ){
		this._newMenuButton = toolbar.getButton("NEW_MENU");
	}
};

Com_Zimbra_UndoSendHdlr.prototype._sendButtonListener = 
function(controller) {
	this._totalWaitTimeInSeconds = this.undeSend_howMuchDelay;
	if(!this._viewIdAndParamsMap) {
		this._viewIdAndParamsMap = [];
	}
	if(!this._viewIdAndStatusesMap) {
		this._viewIdAndStatusesMap = [];
	}
	
	this._msg = controller._composeView.getMsg();
	if(!this._msg) {//there is some compose error..
		return;
	}
	if(!this.appViewMgr) {
		this.appViewMgr = appCtxt.getAppViewMgr();
	}
	var viewId =this.appViewMgr._currentView;
	if (this.appViewMgr._isTabView[viewId]) {
		var tab = appCtxt.getAppChooser().getButton(this.appViewMgr._tabParams[viewId].id);
		var title = this._getComposeTabTitle(viewId);//store the title as when we push the view back, it doesnt seem to work
	}
	var undoLinkId = "UndoSendHdlrZimlet_undoLink"+viewId;
	var timerSpanId = "undoSendHdlrZimlet_Timer" +viewId;
	var sendNowId = "UndoSendHdlrZimlet_sendNow"+viewId;
	this._viewIdAndParamsMap[viewId] = {tab:tab, title:title, undoLinkId:undoLinkId, timerSpanId:timerSpanId, sendNowId:sendNowId};
	this._viewIdAndStatusesMap[viewId] = {undoLinkClicked:false, sendNowLinkClicked:false,  currentCounter:this._totalWaitTimeInSeconds};
	this.appViewMgr.popView(true, viewId);
	if(this._newMenuButton) {
		this._newMenuButton.setEnabled(false);//dont allow creating new mail as we are still using that view
	}
	this._loadMsgs();
	var html = [this._getMainMsg(timerSpanId),
		" <a  style='text-decoration:underline;color:#CA0000;font-weight:bold;font-size:12px' href=# id='",undoLinkId,"'>",this._msg_UndoSendZimlet_Undo,"</a> or",
		" <a  style='text-decoration:underline;color:darkblue;font-size:10px;font-weight:normal' href=# id='",sendNowId,"'>",this._msg_UndoSendZimlet_sendNow,"</a>"].join("");


	this._setAlertViewContent(html);
	this._addListenersToLinks(controller);
	this.timer = setInterval(AjxCallback.simpleClosure(this._updateCounter, this, controller, viewId, timerSpanId), 1000);
};

Com_Zimbra_UndoSendHdlr.prototype._getMainMsg = 
function(timerSpanId) {
	var  timerSpan = ["<span style='color:#CA0000' id='",timerSpanId,"'>",this._totalWaitTimeInSeconds,"</span>"].join("");
	return ["<label style='font-size:12px;font-weight:bold'>",
			this.getMessage("UndoSendZimlet_mainMsg").replace("{0}", timerSpan),
			"</label>"].join("");
};

Com_Zimbra_UndoSendHdlr.prototype._loadMsgs = 
function(controller) {
	this._msg_UndoSendZimlet_Undo = this.getMessage("UndoSendZimlet_Undo");
	this._msg_UndoSendZimlet_sendNow = this.getMessage("UndoSendZimlet_sendNow");
};

Com_Zimbra_UndoSendHdlr.prototype._addListenersToLinks = 
function(controller) {
	for(var viewId  in this._viewIdAndParamsMap) {
		var obj = this._viewIdAndParamsMap[viewId];
		var link = document.getElementById(obj.undoLinkId);
		if(link) {
			var callback = AjxCallback.simpleClosure(this._undoSend, this, controller, viewId);
			link.onclick = callback;
		}
		var link = document.getElementById(obj.sendNowId);
		if(link) {
			var callback = AjxCallback.simpleClosure(this._sendNow, this, controller, viewId);
			link.onclick = callback; 
		}
	}
};

Com_Zimbra_UndoSendHdlr.prototype._updateCounter = 
function(controller, viewId, timerSpanId) {
	var count = this._viewIdAndStatusesMap[viewId].currentCounter;
	if(count == 0) {
		clearInterval(this.timer);
		this._verifyAndSendEmail(controller, viewId);
	} else {
		if(this._newMenuButton) {
			this._newMenuButton.setEnabled(false);//make sure that opening-mail action that enables this btn is countered
		}
		var el = document.getElementById(timerSpanId);
		if(el) {	
			el.innerHTML = --count;
			this._viewIdAndStatusesMap[viewId].currentCounter = count;
		}		
	}
};

Com_Zimbra_UndoSendHdlr.prototype._undoSend = 
function(controller, viewId) {
	if(this._newMenuButton) {
		this._newMenuButton.setEnabled(true);//re-enable newButton(so people can cancel the current view and go-ahead)
	}
	this._viewIdAndStatusesMap[viewId].undoLinkClicked = true;
	clearInterval(this.timer);
	this._hideAlertView();
	this.appViewMgr.pushView(viewId, true);
	
	var obj = this._viewIdAndParamsMap[viewId];
	var tab = obj.tab;
	var title = obj.title
	tab.setText(title); //todo - verify why this doesnt work?
	this._setComposeTabTitle(viewId, title);
};

Com_Zimbra_UndoSendHdlr.prototype._setComposeTabTitle = 
function(viewId, title) {
	var tabTitleEl = this.__getComposeTabTitleEl(viewId);
	if(tabTitleEl) {
		tabTitleEl.innerHTML = title;//workaround
	}
};

Com_Zimbra_UndoSendHdlr.prototype._getComposeTabTitle = 
function(viewId) {
	var tabTitleEl = this.__getComposeTabTitleEl(viewId);
	if(tabTitleEl) {
		return tabTitleEl.innerHTML;
	}
	return "";
};

Com_Zimbra_UndoSendHdlr.prototype.__getComposeTabTitleEl = 
function(viewId) {
	return document.getElementById("zb__App__tab_"+viewId+"_title");
};

Com_Zimbra_UndoSendHdlr.prototype._sendNow = 
function(controller, viewId) {
	this._viewIdAndStatusesMap[viewId].sendNowLinkClicked = true;
	this._sendEmail(controller, viewId);
};

Com_Zimbra_UndoSendHdlr.prototype._verifyAndSendEmail = 
function(controller, viewId) {
	if(this._viewIdAndStatusesMap[viewId].undoLinkClicked || this._viewIdAndStatusesMap[viewId].sendNowLinkClicked){
		return;
	}
	this._sendEmail(controller, viewId);
};

Com_Zimbra_UndoSendHdlr.prototype._sendEmail = 
function(controller, viewId) {
	clearInterval(this.timer);
	this._hideAlertView();
	this._adjustAppManagersViews(viewId);
	this._sendMailViewId = viewId;
	controller._send();
};

Com_Zimbra_UndoSendHdlr.prototype.onSendMsgSuccess = 
function() {
	if(this._newMenuButton) {
		this._newMenuButton.setEnabled(true);
	}
};


Com_Zimbra_UndoSendHdlr.prototype.onSendMsgFailure = 
function(controller, expn, msg) {
	if(this._msg.subject == msg.subject) {
		this.appViewMgr._currentView = this.__appViewMgrOldViewId;
		this.appViewMgr._hidden = this.__appviewMgrOldHiddenViews;
		this._undoSend(controller, this._sendMailViewId);
	}
};

Com_Zimbra_UndoSendHdlr.prototype._adjustAppManagersViews = 
function(viewId) {
	//store old viewMgr info so we can use it to revert viewChanges if sendMsgFailure is triggered
	this.__appViewMgrOldViewId = this.appViewMgr._currentView;
	this.__appviewMgrOldHiddenViews = this.appViewMgr._hidden;
	this.appViewMgr._currentView = viewId;
	var hiddenViews = this.appViewMgr._hidden;
	var newHiddenViews = [];
	for(var i =0; i < hiddenViews.length; i++) {
		var currHV = hiddenViews[i];
		if(viewId != currHV){
			newHiddenViews.push(currHV);
		}
	}
	//newHiddenViews.push(appCtxt.getAppViewMgr().getAppView(ZmApp.MAIL));
	newHiddenViews.push(this.__appViewMgrOldViewId);
	this.appViewMgr._hidden = newHiddenViews;
};

//----------------------------------

Com_Zimbra_UndoSendHdlr.prototype._setAlertViewContent =
function(content) {
	if(this._mainContainer) {
		document.getElementById("undoSendZimlet_mainContainer").innerHTML = content;
		this._mainContainer.style.display = "block";
		return;
	}
	this._mainContainer = document.getElementById("z_shell").appendChild(document.createElement('div'));
	this._mainContainer.style.left = "40%";
	this._mainContainer.style.position = "absolute";
	this._mainContainer.style.display = "block";
	this._mainContainer.style.zIndex = 9000;

	var html = new Array();
	var i = 0;
	html[i++] = "<DIV id ='undoSendZimlet_mainContainer' class='undosend_yellow'>";
	html[i++] = "</DIV>";
	this._mainContainer.innerHTML = html.join("");

	if (content) {
		document.getElementById("undoSendZimlet_mainContainer").innerHTML = content;
	}
	this._alertViewDisplayed = true;
};

Com_Zimbra_UndoSendHdlr.prototype._hideAlertView =
function() {
	if(this._mainContainer) {
		this._mainContainer.style.display = "none";
	}
	this._alertViewDisplayed = false;
};

Com_Zimbra_UndoSendHdlr.prototype.doubleClicked =
function() {
	this.singleClicked();
};

Com_Zimbra_UndoSendHdlr.prototype.singleClicked =
function() {
	this._displayPrefDialog();
};

Com_Zimbra_UndoSendHdlr.prototype._displayPrefDialog =
function() {	
	if (this.pbDialog) {
		this.pbDialog.popup();
		return;
	}
	this.pView = new DwtComposite(this.getShell());
	this.pView.getHtmlElement().style.overflow = "auto";
	this.pView.getHtmlElement().innerHTML = this._createPreferenceView();	
	this.pbDialog = this._createDialog({title:this.getMessage("UndoSendZimlet_PrefLabel"), view:this.pView, standardButtons:[DwtDialog.OK_BUTTON, DwtDialog.CANCEL_BUTTON]});	
	this.pbDialog.setButtonListener(DwtDialog.OK_BUTTON, new AjxListener(this, this._okBtnListner));	
	this.pbDialog.popup();
};

Com_Zimbra_UndoSendHdlr.prototype._setPreferences =
function() {	
	if (this.getUserProperty("hellodlg_checkboxId") == "true") {
		document.getElementById("hellodlg_checkboxId").checked = true;
	}
};

Com_Zimbra_UndoSendHdlr.prototype._createPreferenceView =
function() {
	var html = new Array();
	var i = 0;
	html[i++] = "<DIV><TABLE><tr><td>";
	html[i++] = this.getMessage("UndoSendZimlet_delayMailUpto");
	html[i++] = "<select id=\"undeSend_delayTimerList\">";
	for(var j = 3; j < 21; j++) {
		if(j == this.undeSend_howMuchDelay) {
			html[i++] = ["<option value='",j,"' selected>",j,"</option>"].join("");
		} else {
			html[i++] = ["<option value='",j,"'>",j,"</option>"].join("");
		}
	}
	html[i++] = "</select>";
	html[i++] = ["</td><td> ",this.getMessage("UndoSendZimlet_seconds"),"</td></tr></DIV>"].join("");
	return html.join("");
};

Com_Zimbra_UndoSendHdlr.prototype._okBtnListner =
function() {
	var val = document.getElementById("undeSend_delayTimerList").value; 
	this.undeSend_howMuchDelay = val;
	this.setUserProperty("undeSend_howMuchDelay", val, true);
	appCtxt.getAppController().setStatusMsg("Preference was Saved", ZmStatusView.LEVEL_INFO);
	this.pbDialog.popdown();
};