/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Zimlets
 * Copyright (C) 2007, 2009, 2010 Zimbra, Inc.
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
 * @author Raja Rao DV (rrao@zimbra.com)
 * Provides option to cancel or undo mail send for few seconds
 */

/**
 * Constructor.
 *
 * @author Raja Rao DV (rrao@zimbra.com)
 */
function com_zimbra_undosend_HandlerObject() {
}

com_zimbra_undosend_HandlerObject.prototype = new ZmZimletBase();
com_zimbra_undosend_HandlerObject.prototype.constructor = com_zimbra_undosend_HandlerObject;


/**
 * Simplify handler object
 *
 */
var UndoSendZimlet = com_zimbra_undosend_HandlerObject;

/**
 * Initializes the zimlet.
 */
UndoSendZimlet.prototype.init =
function() {
	this.undeSend_howMuchDelay = parseInt(this.getUserProperty("undeSend_howMuchDelay"));
};


/**
 * Initializes the zimlet.
 *@see ZmZimletBase
 */
UndoSendZimlet.prototype.initializeToolbar =
function(app, toolbar, controller, viewId) {
	if (viewId.indexOf("COMPOSE") >= 0) {
		var sendBtn = toolbar.getButton("SEND");
		sendBtn.removeSelectionListeners();//remove all selection listeners
		sendBtn.addSelectionListener(new AjxListener(this, this._sendButtonListener, controller));
	}
};

/**
 * Shows Error Message
 */
UndoSendZimlet.prototype._showErrorMsg =
function(msg) {
	var msgDialog = appCtxt.getMsgDialog();
	msgDialog.reset();
	msgDialog.setMessage(msg, DwtMessageDialog.WARNING_STYLE);
	msgDialog.popup();
};

/**
 * Listens to Send Button
 * @param {ZmComposeController} controller   A controller
 */
UndoSendZimlet.prototype._sendButtonListener =
function(controller) {
	if (this._alertViewDisplayed) {
		this._showErrorMsg(this.getMessage("UndoSendZimlet_pleaseWait"));
		return;
	}
	this._totalWaitTimeInSeconds = this.undeSend_howMuchDelay;
	if (!this._viewIdAndParamsMap) {
		this._viewIdAndParamsMap = [];
	}
	if (!this._viewIdAndStatusesMap) {
		this._viewIdAndStatusesMap = [];
	}

	this._msg = controller._composeView.getMsg();
	if (!this._msg) {//there is some compose error..
		return;
	}

	if (!this.appViewMgr) {
		this.appViewMgr = appCtxt.getAppViewMgr();
	}	
	var viewId = this.appViewMgr._currentView;

	if(!appCtxt.isChildWindow) {
		if (this.appViewMgr._isTabView[viewId]) {
			var tab = appCtxt.getAppChooser().getButton(this.appViewMgr._tabParams[viewId].id);
			var title = this._getComposeTabTitle(viewId);//store the title as when we push the view back, it doesnt seem to work
		}
	}
	var undoLinkId = "UndoSendHdlrZimlet_undoLink" + viewId;
	var timerSpanId = "undoSendHdlrZimlet_Timer" + viewId;
	var sendNowId = "UndoSendHdlrZimlet_sendNow" + viewId;
	this._viewIdAndParamsMap[viewId] = {tab:tab, title:title, undoLinkId:undoLinkId, timerSpanId:timerSpanId, sendNowId:sendNowId};
	this._viewIdAndStatusesMap[viewId] = {undoLinkClicked:false, sendNowLinkClicked:false,  currentCounter:this._totalWaitTimeInSeconds};

	if(!appCtxt.isChildWindow) {
		this.appViewMgr.popView(true, viewId);
		controller.inactive = false; //IMPORTANT! make sure to set this so this view isnt reused
	}


	this._storeMsgs();
	var html = [this._getMainMsg(timerSpanId),
		" <a  style='text-decoration:underline;color:#CA0000;font-weight:bold;font-size:12px' href=# id='",undoLinkId,"'>",this._msg_UndoSendZimlet_Undo,"</a> or",
		" <a  style='text-decoration:underline;color:darkblue;font-size:10px;font-weight:normal' href=# id='",sendNowId,"'>",this._msg_UndoSendZimlet_sendNow,"</a>"].join("");

	var params = {controller:controller, viewId:viewId, timerSpanId:timerSpanId};

	this._setAlertViewContent(html, params);

};

/**
 * Gets undo-send html for a given timer-id
 * @param {string} timerSpanId  An id
 */
UndoSendZimlet.prototype._getMainMsg =
function(timerSpanId) {
	var timerSpan = ["<span style='color:#CA0000' id='",timerSpanId,"'>",this._totalWaitTimeInSeconds,"</span>"].join("");
	return ["<label style='font-size:12px;font-weight:bold'>",
		AjxMessageFormat.format(this.getMessage("UndoSendZimlet_mainMsg"), timerSpan),
		"</label>"].join("");
};

/**
 * Caches i18n labels
 */
UndoSendZimlet.prototype._storeMsgs =
function() {
	this._msg_UndoSendZimlet_Undo = this.getMessage("UndoSendZimlet_Undo");
	this._msg_UndoSendZimlet_sendNow = this.getMessage("UndoSendZimlet_sendNow");
};

/**
 * Adds listeners to undo & sendNow links
 * @param {ZmComposeController} controller  A controller
 */
UndoSendZimlet.prototype._addListenersToLinks =
function(controller) {
	for (var viewId  in this._viewIdAndParamsMap) {
		var obj = this._viewIdAndParamsMap[viewId];
		var link = document.getElementById(obj.undoLinkId);
		if (link) {
			var callback = AjxCallback.simpleClosure(this._undoSend, this, controller, viewId);
			link.onclick = callback;
		}
		var link = document.getElementById(obj.sendNowId);
		if (link) {
			var callback = AjxCallback.simpleClosure(this._sendNow, this, controller, viewId);
			link.onclick = callback;
		}
	}
};

/**
 * Updates countdown's counter or sends email if counter is 0
 * @param {ZmComposeController} controller A controller
 * @param {string} viewId Compose View's id
 * @param {string} timerSpanId Id of the canvas displaying the counter
 */
UndoSendZimlet.prototype._updateCounter =
function(controller, viewId, timerSpanId) {
	var count = this._viewIdAndStatusesMap[viewId].currentCounter;
	if (count == 0) {
		this._countDownIsON = false;
		clearInterval(this.timer);
		this._verifyAndSendEmail(controller, viewId);
	} else {
		this._countDownIsON = true;
		var el = document.getElementById(timerSpanId);
		if (el) {
			el.innerHTML = --count;
			this._viewIdAndStatusesMap[viewId].currentCounter = count;
		}
	}
};

/**
 * Reverts the compose view
 * @param {ZmComposeController} controller A controller
 * @param {string} viewId id of the composeView
 */
UndoSendZimlet.prototype._undoSend =
function(controller, viewId) {
	this._viewIdAndStatusesMap[viewId].undoLinkClicked = true;
	clearInterval(this.timer);
	this._hideAlertView();
	if(!appCtxt.isChildWindow){
		this.appViewMgr.pushView(viewId, true);
		var obj = this._viewIdAndParamsMap[viewId];
		var tab = obj.tab;
		var title = obj.title;
		if (tab != undefined) {
			tab.setText(title);
		}
		this._setComposeTabTitle(viewId, title);
	}
};

/**
 * Sometimes after undo, compose' tab title is unset, so we try to set it back
 * @param {string} viewId Compose View's id
 * @param {string} title Tab title
 */
UndoSendZimlet.prototype._setComposeTabTitle =
function(viewId, title) {
	var tabTitleEl = this.__getComposeTabTitleEl(viewId);
	if (tabTitleEl) {
		tabTitleEl.innerHTML = title;//workaround
	}
};
/**
 * Gets current compose view's title
 * @param {string} viewId  compose view's id
 */
UndoSendZimlet.prototype._getComposeTabTitle =
function(viewId) {
	var tabTitleEl = this.__getComposeTabTitleEl(viewId);
	if (tabTitleEl) {
		return tabTitleEl.innerHTML;
	}
	return "";
};

/**
 * Gets DOM object of compose view's title
 * @param {string} viewId view id
 */
UndoSendZimlet.prototype.__getComposeTabTitleEl =
function(viewId) {
	return document.getElementById("zb__App__tab_" + viewId + "_title");
};

/**
 * Sends the email when user presses "Send Now" link
 * @param {ZmComposeController} controller A controller
 * @param {string} viewId A compose view's id
 */
UndoSendZimlet.prototype._sendNow =
function(controller, viewId) {
	this._viewIdAndStatusesMap[viewId].sendNowLinkClicked = true;
	this._sendEmail(controller, viewId);
};

/**
 * Makes sure that undoSend or sendNow links are not clicked
 * @param {ZmComposeController} controller A controller
 * @param {string} viewId A compose view's id
 */
UndoSendZimlet.prototype._verifyAndSendEmail =
function(controller, viewId) {
	if (this._viewIdAndStatusesMap[viewId].undoLinkClicked || this._viewIdAndStatusesMap[viewId].sendNowLinkClicked) {
		return;
	}
	this._sendEmail(controller, viewId);
};

/**
 * Sends email
 * @param {ZmComposeController} controller A controller
 * @param {string} viewId A compose view's id
 */
UndoSendZimlet.prototype._sendEmail =
function(controller, viewId) {
	clearInterval(this.timer);
	this._hideAlertView();
	this._sendMailViewId = viewId;
	controller._send();
};

/**
 * Tries to revert back the view upon send-failure
 * @param {ZmComposeController} controller A controller
 * @param {object} expn An Exception
 * @param {ZmMailMsg} msg An email object
 */
UndoSendZimlet.prototype.onSendMsgFailure =
function(controller, expn, msg) {
	if (this._msg && this._msg.subject == msg.subject) {
		this._undoSend(controller, this._sendMailViewId);
	}
};

/**
 * Sets Html content to undo-send canvas
 * @param {string} content String that should be displayed
 * @param {params} An Object with controller, viewId, timerSpanId info
 */
UndoSendZimlet.prototype._setAlertViewContent =
function(content, params) {
	if (this._mainContainer) {
		this._setDelayedContent(content, params);
		return;
	}
	if(!appCtxt.isChildWindow) {
		this._mainContainer = document.getElementById("z_shell").appendChild(document.createElement('div'));
	} else {
		this._mainContainer = document.getElementById("DWT1").appendChild(document.createElement('div'));
	}
	if(appCtxt.isChildWindow) {
		this._mainContainer.style.left = "25%";
	} else {
		this._mainContainer.style.left = "40%";
	}
	this._mainContainer.style.position = "absolute";
	this._mainContainer.style.display = "none";
	this._mainContainer.style.zIndex = 9000;

	var container = document.createElement('div');
	container.id = "undoSendZimlet_mainContainer";
	container.className = "undosend_yellow";
	this._mainContainer.appendChild(container);
	if (content) {
		this._setDelayedContent(content, params);
	}
};

/**
 * Delays setting content by 200ms to make super-fast v8 js Google Chrome happy
 * @param {string} content String that should be displayed
 * @param {params} An Object with controller, viewId, timerSpanId info
 */
UndoSendZimlet.prototype._setDelayedContent =
function(content, params) {//this dilay is required to make sure Chrome's super-fast v8-js engine to wait a little
	if(AjxEnv.isChrome) {
		setTimeout(AjxCallback.simpleClosure(this._doSetContent, this, content, params), 200);
	} else {
		this._doSetContent(content, params);
	}
};

/**
 * Sets Html content to undo-send canvas
 * @param {string} content String that should be displayed
 * @param {params} An Object with controller, viewId, timerSpanId info
 */
UndoSendZimlet.prototype._doSetContent =
function(content, params) {
	document.getElementById("undoSendZimlet_mainContainer").innerHTML = content;
	this._mainContainer.style.display = "block";

	this._addListenersToLinks(params.controller);
	this.timer = setInterval(AjxCallback.simpleClosure(this._updateCounter, this, params.controller, params.viewId, params.timerSpanId), 1000);
	this._alertViewDisplayed = true;
};



/**
 * Hides the view
 */
UndoSendZimlet.prototype._hideAlertView =
function() {
	if (this._mainContainer) {
		this._mainContainer.style.display = "none";
	}
	this._alertViewDisplayed = false;
};

/**
 * @see {ZmZimletBase}
 */
UndoSendZimlet.prototype.doubleClicked =
function() {
	this.singleClicked();
};

/**
 * @see {ZmZimletBase}
 */
UndoSendZimlet.prototype.singleClicked =
function() {
	this._displayPrefDialog();
};
/**
 * Displays Preferences dialog
 */
UndoSendZimlet.prototype._displayPrefDialog =
function() {
	if (this.pbDialog) {
		this.pbDialog.popup();
		return;
	}
	this.pView = new DwtComposite(this.getShell());
	this.pView.getHtmlElement().style.overflow = "auto";
	this.pView.getHtmlElement().innerHTML = this._createPreferenceView();
	this.pbDialog = new ZmDialog({parent:this.getShell(), title:this.getMessage("UndoSendZimlet_PrefLabel"), view:this.pView, standardButtons:[DwtDialog.OK_BUTTON, DwtDialog.CANCEL_BUTTON]});
	this.pbDialog.setButtonListener(DwtDialog.OK_BUTTON, new AjxListener(this, this._okBtnListner));
	this.pbDialog.popup();
};

/**
 * Returns HTML for preferences view
 * @returns {string} HTML
 */
UndoSendZimlet.prototype._createPreferenceView =
function() {
	var html = new Array();
	var i = 0;
	html[i++] = "<DIV><TABLE><tr><td>";
	html[i++] = this.getMessage("UndoSendZimlet_delayMailUpto");
	html[i++] = "<select id=\"undeSend_delayTimerList\">";
	for (var j = 3; j < 21; j++) {
		if (j == this.undeSend_howMuchDelay) {
			html[i++] = ["<option value='",j,"' selected>",j,"</option>"].join("");
		} else {
			html[i++] = ["<option value='",j,"'>",j,"</option>"].join("");
		}
	}
	html[i++] = "</select>";
	html[i++] = ["</td><td> ",this.getMessage("UndoSendZimlet_seconds"),"</td></tr></DIV>"].join("");
	return html.join("");
};

/**
 * Listens to OK button of the preferences and saves it.
 */
UndoSendZimlet.prototype._okBtnListner =
function() {
	var val = document.getElementById("undeSend_delayTimerList").value;
	this.undeSend_howMuchDelay = val;
	this.setUserProperty("undeSend_howMuchDelay", val, true);
	appCtxt.getAppController().setStatusMsg("Preference was Saved", ZmStatusView.LEVEL_INFO);
	this.pbDialog.popdown();
};