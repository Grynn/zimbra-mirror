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
 *@Author Raja Rao DV rrao@zimbra.com
 * Allows ignoring message for a specific period of time
 */


function ZmIgnoreMsgsZimlet() {
}

ZmIgnoreMsgsZimlet.prototype = new ZmZimletBase();
ZmIgnoreMsgsZimlet.prototype.constructor = ZmIgnoreMsgsZimlet;

ZmIgnoreMsgsZimlet.MESSAGE_VIEW = "message";
ZmIgnoreMsgsZimlet.ignoreMsgsZimlet = "IGNORE_MSGS_ZIMLET";

ZmIgnoreMsgsZimlet.prototype.init =
		function() {
			this.ignoreMsgsfldrName = this.getMessage("ignoreMsgsfldrName");
			this.ignoreTheseMsgsFltrName = this.getMessage("ignoreTheseMsgsFltrName");
			this.filterRulesArray = [];
			this.ignoreMsgsFolderIdArray = [];
		};

//param should be {message:message, callback:callback}
ZmIgnoreMsgsZimlet.prototype._updateFilter =
		function(param) {
			var rule = null;
			var modifyRule = true;
			rule = this.filterRules.getRuleByName(this.ignoreTheseMsgsFltrName);
			if (rule == undefined) {
				rule = new ZmFilterRule(this.ignoreTheseMsgsFltrName, true);
				rule.addAction(ZmFilterRule.A_FOLDER, this.ignoreMsgsfldrName);//file to folder Ignored Messages
				rule.addAction(ZmFilterRule.A_STOP);//stop further action
				modifyRule = false;
			}
			this._updateConditions(rule, param.message.subject);

			if (!modifyRule) {
				this.filterRules._insertRule(rule, 0);
			}
			this.filterRules._saveRules(0, false, param.callback);
		};


ZmIgnoreMsgsZimlet.prototype._updateConditions =
		function(rule, newSubject) {
			rule.addCondition("headerTest", ZmFilterRule.OP_CONTAINS, newSubject, "subject");
		};

ZmIgnoreMsgsZimlet.prototype.setIgnoreMsgsFldrId =
		function(callback) {
			this.ignoreMsgsFldrId = null;
			if (!this.ignoreMsgsFolderIdArray[appCtxt.getActiveAccount().name]) {
				this.ignoreMsgsFldrId = this.ignoreMsgsFolderIdArray[appCtxt.getActiveAccount().name];
			}
			if (this.ignoreMsgsFldrId) {
				if (callback) {
					callback.run(this);
				}
				return;
			}
			var soapDoc = AjxSoapDoc.create("GetFolderRequest", "urn:zimbraMail");
			var folderNode = soapDoc.set("folder");
			folderNode.setAttribute("l", appCtxt.getFolderTree().root.id);

			var command = new ZmCsfeCommand();
			var acct = appCtxt.getActiveAccount();
			accountName = (acct && acct.id != ZmAccountList.DEFAULT_ID) ? acct.name : null;
			var top = command.invoke({soapDoc: soapDoc, accountName:accountName}).Body.GetFolderResponse.folder[0];

			var folders = top.folder;
			if (folders) {
				for (var i = 0; i < folders.length; i++) {
					var f = folders[i];
					if (f && f.name == this.ignoreMsgsfldrName && f.view == ZmIgnoreMsgsZimlet.view) {
						this.ignoreMsgsFldrId = this.ignoreMsgsFolderIdArray[appCtxt.getActiveAccount().name] = f.id;
						break;
					}
				}
			}
			if (this.ignoreMsgsFldrId) {
				if (callback)
					callback.run(this);
			} else {
				this.createFolder(callback);	//there is no such folder, so create one.
			}
		};

ZmIgnoreMsgsZimlet.prototype.initializeToolbar =
		function(app, toolbar, controller, view) {

			if (view == ZmId.VIEW_CONVLIST ||
					view == ZmId.VIEW_CONV ||
					view == ZmId.VIEW_TRAD) {
				var buttonIndex = -1;
				for (var i = 0, count = toolbar.opList.length; i < count; i++) {
					if (toolbar.opList[i] == ZmOperation.PRINT) {
						buttonIndex = i + 1;
						break;
					}
				}
				ZmMsg.ignoreMsgsBtnLabel = "Ignore";
				var buttonArgs = {
					text	: ZmMsg.ignoreMsgsBtnLabel,
					tooltip: this.getMessage("tooltip"),
					index: buttonIndex,
					image: "ignoremsgs-panelIcon"
				};
				var button = toolbar.createOp(ZmIgnoreMsgsZimlet.ignoreMsgsZimlet, buttonArgs);
				button.addSelectionListener(new AjxListener(this, this._buttonListener, [controller]));
			}
		};

ZmIgnoreMsgsZimlet.prototype._buttonListener =
		function(controller) {
			var message = controller.getMsg();

			//create callbacks in reverse order(of work flow)
			var callback_moveMsgAfterAddingToFilter = new AjxCallback(this, this._moveMsg, {message:message, callback:null});
			//first makesure we have the folder, then update the filter
			var callback_AfterLoadingRulesCallback = new AjxCallback(this, this._updateFilter, {message:message, callback:callback_moveMsgAfterAddingToFilter});
			this.setIgnoreMsgsFldrId(new AjxCallback(this, this._loadAllFilterRules, callback_AfterLoadingRulesCallback));
		};

ZmIgnoreMsgsZimlet.prototype._moveMsg =
		function(param) {
			setTimeout(AjxCallback.simpleClosure(this._doMoveMsg, this,
					param), 1000);
		};

ZmIgnoreMsgsZimlet.prototype._doMoveMsg =
		function(param) {
			param.message.move(this.ignoreMsgsFldrId);
			if (param.callback) {
				param.callback.run(this);
			}
		};

ZmIgnoreMsgsZimlet.prototype._loadAllFilterRules =
		function(callback) {
			if (!this.filterRulesArray[appCtxt.getActiveAccount().name]) {
				this.filterRulesArray[appCtxt.getActiveAccount().name] = AjxDispatcher.run("GetFilterRules");
			}
			//if (!this.filterRules) {
			this.filterRules = this.filterRulesArray[appCtxt.getActiveAccount().name];
			//}
			this.filterRules.loadRules(false, callback);
		};

ZmIgnoreMsgsZimlet.prototype.createFolder =
		function(postCallback) {
			var params = {color:null, name:this.ignoreMsgsfldrName, url:null, view:ZmIgnoreMsgsZimlet.view, l:"1", postCallback:postCallback};
			this._createFolder(params);
		};


ZmIgnoreMsgsZimlet.prototype._createFolder =
		function(params) {
			var jsonObj = {CreateFolderRequest:{_jsns:"urn:zimbraMail"}};
			var folder = jsonObj.CreateFolderRequest.folder = {};
			for (var i in params) {
				if (i == "callback" || i == "errorCallback" || i == "postCallback") {
					continue;
				}

				var value = params[i];
				if (value) {
					folder[i] = value;
				}
			}
			var _createFldrCallback = new AjxCallback(this, this._createFldrCallback, params);
			var _createFldrErrCallback = new AjxCallback(this, this._createFldrErrCallback, params);
			return appCtxt.getAppController().sendRequest({jsonObj:jsonObj, asyncMode:true, errorCallback:_createFldrErrCallback, callback:_createFldrCallback});
		};

ZmIgnoreMsgsZimlet.prototype._createFldrCallback =
		function(params, response) {
			if (params.name == this.ignoreMsgsfldrName) {
				this.ignoreMsgsFldrId = response.getResponse().CreateFolderResponse.folder[0].id;
				if (params.postCallback) {
					params.postCallback.run(this);
				}
			} else {
				appCtxt.getAppController().setStatusMsg("'Ignored Messages' folder Created", ZmStatusView.LEVEL_INFO);
			}
		};

ZmIgnoreMsgsZimlet.prototype._createFldrErrCallback =
		function(params, ex) {
			if (!params.url && !params.name) {
				return false;
			}
			var msg;
			if (params.name && (ex.code == ZmCsfeException.MAIL_ALREADY_EXISTS)) {
				var type = appCtxt.getFolderTree(appCtxt.getActiveAccount()).getFolderTypeByName(params.name);
		        msg = AjxMessageFormat.format(ZmMsg.errorAlreadyExists, [params.name,type.toLowerCase()]);
			} else if (params.url) {
				var errorMsg = (ex.code == ZmCsfeException.SVC_RESOURCE_UNREACHABLE) ? ZmMsg.feedUnreachable : ZmMsg.feedInvalid;
				msg = AjxMessageFormat.format(errorMsg, params.url);
			}
			appCtxt.getAppController().setStatusMsg("Could Not create 'Ignored Messages' Folder", ZmStatusView.LEVEL_WARNING);
			if (msg) {
				this._showErrorMsg(msg);
				return true;
			}
			return false;
		};

ZmIgnoreMsgsZimlet.prototype._showErrorMsg =
		function(msg) {
			var msgDialog = appCtxt.getMsgDialog();
			msgDialog.reset();
			msgDialog.setMessage(msg, DwtMessageDialog.CRITICAL_STYLE);
			msgDialog.popup();
		};