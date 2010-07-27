/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Zimlets
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
 * Zimlet to translate a message using Google.
 *
 * @author Raja Rao DV (rrao@zimbra.com)
 */
function com_zimbra_gtranslator_HandlerObject() {
}

com_zimbra_gtranslator_HandlerObject.prototype = new ZmZimletBase();
com_zimbra_gtranslator_HandlerObject.prototype.constructor = com_zimbra_gtranslator_HandlerObject;

/**
 * Simplify handler object
 *
 */
var GTranslatorZimlet = com_zimbra_gtranslator_HandlerObject;

/**
 * Called by Framework. Adds a toolbar
 * @see ZmZimletBase
 */
GTranslatorZimlet.prototype.initializeToolbar =
function(app, toolbar, controller, view) {
	if (view == ZmId.VIEW_CONVLIST || view == ZmId.VIEW_CONV || view == ZmId.VIEW_TRAD) {
		var buttonIndex = -1;
		for (var i = 0, count = toolbar.opList.length; i < count; i++) {
			if (toolbar.opList[i] == ZmOperation.PRINT) {
				buttonIndex = i + 1;
				break;
			}
		}
		ZmMsg.gTranslatorLabel = this.getMessage("GTranslatorZimlet_translateButton");
		ZmMsg.gTranslatorTip = this.getMessage("GTranslatorZimlet_tooltip");
		var buttonArgs = {
			text	: ZmMsg.gTranslatorLabel,
			tooltip: ZmMsg.gTranslatorTip,
			index: buttonIndex,
			image: "Google-panelIcon"
		};
		if (!toolbar.getOp("GOOGLE_TRANSLATOR")) {
			var button = toolbar.createOp("GOOGLE_TRANSLATOR", buttonArgs);
			button.addSelectionListener(new AjxListener(this, this._gTranslatorTBListener, [controller]));
		}
	}
};

/**
 * Called by Framework when a view is opened
 * @param {String} viewId  Current view's id
 * @param {Boolean} isNewView  If <code>true</code>, the view is freshly created
 */
GTranslatorZimlet.prototype.onShowView =
function(viewId, isNewView) {
	if(appCtxt.isChildWindow) {
		this._zimletContext = parentAppCtxt.getZimletMgr().getZimletByName("com_zimbra_gtranslator");
	}
	if (viewId.indexOf("COMPOSE") >= 0 &&  !this._zimletContext._isToolbarClosed && !this._zimletContext._alreadyUsed) {
		var editor = appCtxt.getCurrentView().getHtmlEditor();
		var mode = editor.getMode();
		var currentContent = editor.getContent();
		var data = this._zimletContext.translatedText;
		var langObj =  this._zimletContext.langObj;
		var saperator = "\r\n";

		data = AjxStringUtil.htmlDecode(data);
		if (mode == DwtHtmlEditor.HTML) {
			saperator = "<br/>";
			data = AjxStringUtil.nl2br(data);
		}

		var statement = AjxMessageFormat.format(this.getMessage("GTranslatorZimlet_translatedBy"), [langObj.fromName, langObj.toName]);
		var translatedBy = [];
		translatedBy.push("---------------------------------------------------------------------------------------------------------------------------", saperator,
		statement, saperator,
		"---------------------------------------------------------------------------------------------------------------------------", saperator);

		editor.setContent([translatedBy.join(""), data, saperator, currentContent].join(""));
		this._zimletContext._alreadyUsed = true;
	}
};

/**
 * Adds gTranslator toolbar button listener
 * @param ZmComposeController controller Compose controller
 */
GTranslatorZimlet.prototype._gTranslatorTBListener =
function(controller) {
	var viewId = appCtxt.getCurrentViewId();
	if (viewId == "CLV" && appCtxt.getSettings().getSetting("READING_PANE_LOCATION").value == "off") {
		this.displayErrorMessage(this.getMessage("GTranslatorZimlet_readingPaneIsOff"), null, this.getMessage("GTranslatorZimlet_zimletError"));
		return;
	}
	this._fromLanguage = null;
	var selectedItms = controller.getCurrentView().getSelection();
	if (selectedItms.length > 0) {
		this.srcMsgObj = selectedItms[0];
		if (this.srcMsgObj.type == "CONV") {
			this.srcMsgObj = this.srcMsgObj.getFirstHotMsg();
		}
		this._toLanguage = this._getUserToLanguage();
		if (google && google.language) {
			this._translateMsg();
		} else {
			var callback = new AjxCallback(this, this._translateMsg);
			this._loadGoogleTranslator(callback);
		}
	}
};
/**
 * Returns user's locale
 * @return {String} User's locale
 */
GTranslatorZimlet.prototype._getUserToLanguage =
function() {
	 var locale = appCtxt.getActiveAccount().settings.getInfoResponse.attrs._attrs.zimbraLocale;
	var prefLocale = appCtxt.getActiveAccount().settings.getInfoResponse.prefs._attrs.zimbraPrefLocale;
	if(prefLocale) {
		locale = prefLocale;
	}
	if(!locale) {
		locale = "en";
	}
	if (locale.indexOf("en_") == 0) {
		return "en";
	} else {
		return locale;
	}
};

/**
 * Main function that translates message
 */
GTranslatorZimlet.prototype._translateMsg =
function() {
	var callback = AjxCallback.simpleClosure(this._translationHandler, this);
	var bodyContent = this.getMailBodyAsText();
	bodyContent = AjxStringUtil.htmlDecode(bodyContent);
	this._dataChunksToTranslate = [];
	this._dataChunksTranslated = [];
	this._dataChunkIndex = 0;
	if (!this._fromLanguage) {
		this._langPair = "|" + this._toLanguage;
	} else {
		this._langPair = this._fromLanguage + "|" + this._toLanguage;
	}
	this._splitTo4900Chunks(bodyContent);
	this._makeRequest();
};

/**
 * Google only allows upto 5000 chars to be translated at a time. So we recursively  translate 4900 chars
 * 100 chars is left for other parameters etc.
 * @param text
 */
GTranslatorZimlet.prototype._splitTo4900Chunks = function(text) {
	var startIndex = 0;
	while (text.length > 4900) {
		var tmpText = text.substring(startIndex, 4900);
		var lastIndex = tmpText.lastIndexOf(" ");
		this._dataChunksToTranslate.push(text.substring(startIndex, lastIndex));
		var text = text.substring(lastIndex + 1, text.length);
	}
	this._dataChunksToTranslate.push(text);
};

/**
 *   ets current emails body as Text
 */
GTranslatorZimlet.prototype.getMailBodyAsText = function() {
	var body = "";
	if (this.srcMsgObj.body) {
		body = AjxStringUtil.htmlEncode(this.srcMsgObj.body);
	} else if (this.srcMsgObj._topPart && this.srcMsgObj._topPart.getContentForType) {
		body = AjxStringUtil.htmlEncode(this.srcMsgObj._topPart.getContentForType(ZmMimeTable.TEXT_PLAIN));
	} else {
		body = "";
	}

	if (!body || body == "") {//If we dont have body, try using getBodyContent api
		if (!this.srcMsgObj.isHtmlMail()) {
			return this.srcMsgObj.getBodyContent();
		}
		var div = document.createElement("div");
		div.innerHTML = this.srcMsgObj.getBodyContent();
		return AjxStringUtil.convertHtml2Text(div);
	} else {
		return body;
	}
};

/**
 * Called by Zimbra when an email is opened. Used to indicate tollbar was removed
 */
GTranslatorZimlet.prototype.onMsgView =
function() {
	this._zimletContext._isToolbarClosed = true;
	this._zimletContext._alreadyUsed = false;
};

/**
 * Makes the request to Google.
 *
 * @param	{string}	lang		the language pair
 * @param	{string}		text	the text to translate
 */
GTranslatorZimlet.prototype._makeRequest =
function() {
	var reqParams = [];
	var i = 0;

	var text = this._dataChunksToTranslate[this._dataChunkIndex];
	// params for google translator
	reqParams[i++] = "q=";
	reqParams[i++] = AjxStringUtil.urlEncode(text);
	reqParams[i++] = "&langpair=";
	reqParams[i++] = AjxStringUtil.urlEncode(this._langPair || "en|de");
	reqParams[i++] = "&hl=en&v=1.0&format=text";
	var reqHeader = { "User-Agent": navigator.userAgent, "Content-Type": "application/x-www-form-urlencoded", "Referrer": "http://translate.google.com/translate_t" };
	var url = ZmZimletBase.PROXY + AjxStringUtil.urlEncode("http://ajax.googleapis.com/ajax/services/language/translate");

	AjxRpc.invoke(reqParams.join(""), url, reqHeader, new AjxCallback(this, this._translationHandler));
};
/**
 * If translation of all chunks are done, it displays the translation or else continues to translate remaining chunks
 * @param {Object}  result  Translation result object from Google
 */
GTranslatorZimlet.prototype._translationHandler =
function(result) {
	if (!result.success) {
		this.displayErrorMessage(this.getMessage("GTranslatorZimlet_couldNotTranslate"), null, this.getMessage("GTranslatorZimlet_zimletError"));
		return;
	}
	var jsonObj = eval("(" + result.text + ")");
	this._dataChunksTranslated.push(jsonObj.responseData.translatedText);
	if (this._dataChunksTranslated.length < this._dataChunksToTranslate.length) {
		this._dataChunkIndex++;
		this._makeRequest();
		return;
	}
	if (jsonObj.responseData.detectedSourceLanguage) {
		this._fromLanguage = jsonObj.responseData.detectedSourceLanguage;
	}
	var allTranslatedData = this._dataChunksTranslated.join(" ");

	if (!this._oldMsgId || this._oldMsgId != this.srcMsgObj.id ||
	!document.getElementById(this._frame.id)) {
		this._initializeGTranslatorToolbar();
		this._oldMsgId = this.srcMsgObj.id;
	} else {
		this._showToolbar();
	}
	this._zimletContext._isToolbarClosed = false;
	this._zimletContext._alreadyUsed = false;

	var data = AjxStringUtil.htmlEncode(allTranslatedData);

	//this._translatedData = data;
	this._zimletContext.translatedText = data;
	var langObj = this._getLanguageFromAndToNames();
	this._zimletContext.langObj = langObj;

	//this._cacheTranslatedTextInZimletContext(data);
	//this._cacheFromAndToLanguageInZimletContext();
	if (AjxEnv.isIE) {
		this._tCanvas.innerHTML = this._zimletifyText(AjxStringUtil.nl2br(data))
	} else {
		this._tCanvas.innerHTML = AjxStringUtil.nl2br(this._zimletifyText(data));
	}
	this.changeOpac(0, this._toolbar.style);
	this.opacity(this._toolbar.id, 0, 100, 500);
};




/**
 * Tries to zimletify some of the texts
 * @param  {String} data    Text to Zimletify
 */
GTranslatorZimlet.prototype._zimletifyText =
function(data) {
	//pass it through zimlets to get url, phone, emoticons etc
	if (this._zimletDiv == undefined) {
		this._zimletDiv = document.createElement("div");
	}
	if (!this._objectManager) {
		this._objectManager = new ZmObjectManager(new DwtComposite(this.getShell()));
	}

	this._zimletDiv.innerHTML = data;
	this._objectManager.findObjectsInNode(this._zimletDiv);
	return this._zimletDiv.innerHTML;
};

/**
 * Creates Translator toolbar
 */
GTranslatorZimlet.prototype._initializeGTranslatorToolbar =
function() {
	var viewId = appCtxt.getCurrentViewId();
	var id = "";
	if (viewId.indexOf("MSG") == -1) {
		id = "zv__" + viewId + "__MSG_body";
		this._currentMsgBody = document.getElementById(id);
	} else {
		id = "zv__" + viewId + "__MSG_body";
		this._currentMsgBody = document.getElementById(id);
	}
	if (!this._currentMsgBody) {
		this.displayErrorMessage(this.getMessage("GTranslatorZimlet_couldNotGrabBody"), null, this.getMessage("GTranslatorZimlet_zimletError"));
		return;
	}
	id = Dwt.getNextId();
	//this._cleanToolbar();
	this._frame = document.createElement("div");
	this._frame.id = "gTranslator_frame_" + id;

	this._currentMsgBody.style.display = "none";
	this._currentMsgBody.parentNode.appendChild(this._frame);

	this._toolbar = document.createElement("div");
	this._toolbar.id = "gTranslator_toolbar_" + id;
	this._toolbar.style.display = "block";

	this._closeCompareToolbar = document.createElement("div");
	this._closeCompareToolbar.id = "gTranslator_closeCompareToolbar_" + id;
	this._closeCompareToolbar.style.display = "none";

	this._tCanvas = document.createElement("div");
	this._tCanvas.id = "gTranslator_canvas_" + id;
	this._tCanvas.style.display = "block";
	this._tCanvas.className = "MsgBody MsgBody-text";
	this._tCanvas.style.fontSize = "13px";
	this._tCanvas.style.margin = "0pt";

	this._tCompareCanvas = document.createElement("div");
	this._tCompareCanvas.id = "gTranslator_compare_" + id;
	this._tCompareCanvas.style.display = "none";
	this._tCompareCanvas.className = "MsgBody MsgBody-text";
	this._tCompareCanvas.style.fontSize = "13px";
	this._tCompareCanvas.style.margin = "0pt";
	this._compareCanvasId_from = "gTranslator_compare_from_" + id;
	this._compareCanvasId_to = "gTranslator_compare_to_" + id;

	//create main frame
	this._frame.appendChild(this._toolbar);
	this._frame.appendChild(this._closeCompareToolbar);
	this._frame.appendChild(this._tCanvas);
	this._frame.appendChild(this._tCompareCanvas);

	//add toolbar html
	var fromId = Dwt.getNextId();
	var fromSelectMenuId = Dwt.getNextId();
	var toId = Dwt.getNextId();
	var toSelectMenuId = Dwt.getNextId();
	var sendId = Dwt.getNextId();
	var compareId = Dwt.getNextId();
	var closeCompareId = Dwt.getNextId();
	var closeId = Dwt.getNextId();
	this._fromLanguageCompareCellId = Dwt.getNextId();
	this._toLanguageCompareCellId = Dwt.getNextId();

	var html = [];
	html.push("<div class='overviewHeader'><table width=100% cellpadding=0 cellspacing=0><tr>",
	"<td><table border=0 align=left class='gTranslator_barTbl'><tr><td>", this.getMessage("GTranslatorZimlet_translatedFrom"), "</td><td  id='", fromId, "'></td>",
	"<td>", this.getMessage("GTranslatorZimlet_to"), "</td><td  id='", toId, "'></td></tr></table></td><td><table class='gTranslator_barTbl' align=right><tr>",
	"<td id='", compareId, "'></td><td width=16px id='", closeId, "'></td></tr></table></tr></table></div>");

	this._toolbar.innerHTML = html.join("");

	html = [];
	html.push("<div class='overviewHeader' style='padding-left:12px;'><table cellpadding=0 cellspacing=0><tr><td width=50% id='", this._fromLanguageCompareCellId, "'></td>",
	"<td  width=50% id='", this._toLanguageCompareCellId, "'></td><td id='", closeCompareId, "'></td></tr></table></div>");

	this._closeCompareToolbar.innerHTML = html.join("");

	//create empty html for compare purposes
	html = [];
	html.push("<table width=100%><tr><td width=50% valign=top>",
	"<div id='", this._compareCanvasId_from, "' style='font-size:13px;'></div></td>",
	"<td width=50% valign=top><div id='", this._compareCanvasId_to, "'  style='font-size:13px;'></div></td></table>");
	this._tCompareCanvas.innerHTML = html.join("");

	//add from and to menus
	document.getElementById(fromId).innerHTML = this._getLanguagesList(fromSelectMenuId, this._fromLanguage);
	document.getElementById(toId).innerHTML = this._getLanguagesList(toSelectMenuId, this._toLanguage);


	var btn = new DwtButton({parent:this.getShell()});
	btn.setText(this.getMessage("GTranslatorZimlet_readSideBySide"));
	btn.setImage("ListView");
	btn.addSelectionListener(new AjxListener(this, this._compareHandler, ["SHOW_COMPARE"]));
	document.getElementById(compareId).appendChild(btn.getHtmlElement());

	btn = new DwtButton({parent:this.getShell()});
	btn.setImage("Close");
	btn.addSelectionListener(new AjxListener(this, this._compareHandler, ["CLOSE_COMPARE"]));
	document.getElementById(closeCompareId).appendChild(btn.getHtmlElement());

	btn = new DwtButton({parent:this.getShell()});
	btn.setImage("Close");
	btn.addSelectionListener(new AjxListener(this, this._closeHandler));
	document.getElementById(closeId).appendChild(btn.getHtmlElement());

	var callback = AjxCallback.simpleClosure(this._translateMenuHandler, this, fromSelectMenuId, toSelectMenuId);
	if (!AjxEnv.isIE) {
		document.getElementById(fromId).onchange = callback;
	} else {
		document.getElementById(fromId).onclick = callback;
	}

	var callback = AjxCallback.simpleClosure(this._translateMenuHandler, this, fromSelectMenuId, toSelectMenuId);
	if (!AjxEnv.isIE) {
		document.getElementById(toId).onchange = callback;
	} else {
		document.getElementById(toId).onclick = callback;
	}
};

/**
 * Re-runs translation based on menu selection
 * @param {String}  fromMenuId  Menu id of the from-language
 * @param {String}  toMenuId  Menu id of the to-language
 */
GTranslatorZimlet.prototype._translateMenuHandler =
function(fromMenuId, toMenuId) {
	this._fromLanguage = document.getElementById(fromMenuId).value;
	this._toLanguage = document.getElementById(toMenuId).value;
	this._translateMsg();
};

GTranslatorZimlet.prototype._showToolbar =
function() {
	this._tCanvas.style.display = "block";
	this._tCompareCanvas.style.display = "none";
	this._toolbar.style.display = "block";
	this._closeCompareToolbar.style.display = "none";
	this._currentMsgBody.style.display = "none";
};

/**
 * Clears all widgets when close button is clicked
 */
GTranslatorZimlet.prototype._closeHandler =
function() {
	this._zimletContext._isToolbarClosed = true;//used by reply/fwd handlers to add/not-add translation
	this._tCanvas.style.display = "none";
	this._tCompareCanvas.style.display = "none";
	this._toolbar.style.display = "none";
	this._closeCompareToolbar.style.display = "none";
	this._currentMsgBody.style.display = "block";

};

/**
 * Displays/hides compare widget
 * @param {String}  action   Can be SHOW_COMPARE | CLOSE_COMPARE
 */
GTranslatorZimlet.prototype._compareHandler =
function(action) {
	if (action == "SHOW_COMPARE") {
		this._tCanvas.style.display = "none";
		this._tCompareCanvas.style.display = "block";
		this._toolbar.style.display = "none";
		this._closeCompareToolbar.style.display = "block";
		var origBodyContent = this.getMailBodyAsText();
		if (AjxEnv.isIE) {
			origBodyContent = this._zimletifyText(AjxStringUtil.nl2br(origBodyContent))
		} else {
			origBodyContent = AjxStringUtil.nl2br(this._zimletifyText(origBodyContent));
		}

		document.getElementById(this._compareCanvasId_from).innerHTML = origBodyContent;
		document.getElementById(this._compareCanvasId_to).innerHTML = this._tCanvas.innerHTML;

		var langObj = this._getLanguageFromAndToNames();
		document.getElementById(this._toLanguageCompareCellId).innerHTML = "<strong>" + langObj.toName + "</strong>";
		document.getElementById(this._fromLanguageCompareCellId).innerHTML = "<strong>" + langObj.fromName + "</strong>";
	} else {
		this._tCanvas.style.display = "block";
		this._tCompareCanvas.style.display = "none";
		this._toolbar.style.display = "block";
		this._closeCompareToolbar.style.display = "none";
	}
};

/**
 * Returns user-friendly names of from and to languages.
 */
GTranslatorZimlet.prototype._getLanguageFromAndToNames =
function() {
	if (!this._languageslist) {
		this._languageslist = google.language.Languages;
	}
	var fromName = "";
	var toName = "";
	for (var item in this._languageslist) {
		var id = this._languageslist[item];
		if (id == this._fromLanguage) {
			fromName = this.getMessage(item);
		} else if (id == this._toLanguage) {
			toName = this.getMessage(item);
		}
		if (fromName != "" && toName != "") {
			break;
		}
	}
	return {toName:toName, fromName:fromName};
};

/**
 * Helper function
 */
GTranslatorZimlet.prototype.opacity =
function(id, opacStart, opacEnd, millisec) {
	//speed for each frame
	var speed = Math.round(millisec / 100);
	var timer = 0;
	var styleObj = document.getElementById(id).style;
	//determine the direction for the blending, if start and end are the same nothing happens
	if (opacStart > opacEnd) {
		for (i = opacStart; i >= opacEnd; i--) {
			setTimeout(AjxCallback.simpleClosure(this.changeOpac, this, i, styleObj), (timer * speed));
			timer++;
		}
	} else if (opacStart < opacEnd) {
		for (i = opacStart; i <= opacEnd; i++)
		{
			setTimeout(AjxCallback.simpleClosure(this.changeOpac, this, i, styleObj), (timer * speed));
			timer++;
		}
	}
};

/**
 * Change the opacity for different browsers
 */
GTranslatorZimlet.prototype.changeOpac =
function(opacity, styleObj) {
	styleObj.opacity = (opacity / 100);
	styleObj.MozOpacity = (opacity / 100);
	styleObj.KhtmlOpacity = (opacity / 100);
	styleObj.filter = "alpha(opacity=" + opacity + ")";
};

/**
 * Loads google translator
 * @param {AjxCallback} postCallback    A callback to be called after translator is loaded
 */
GTranslatorZimlet.prototype._loadGoogleTranslator =
function(postCallback) {
	if (!google || !google.language) {
		google.load("language", "1", {callback: AjxCallback.simpleClosure(this._loadGTranslatorCallback, this, postCallback)});
	} else {
		if (postCallback) {
			postCallback.run();
		}
	}
};
/**
 * Runs a callback function after loading Google translator
 * @param {AjxCallback} postCallback    A callback to be called after translator is loaded
 */
GTranslatorZimlet.prototype._loadGTranslatorCallback =
function(postCallback) {
	if (postCallback) {
		postCallback.run();
	}
};

/**
 * Returns HTML list with languages
 * @param {String}  listId  id of the list
 * @param {String}  languageId  Language to be pre-selected
 */
GTranslatorZimlet.prototype._getLanguagesList =
function(listId, languageId) {
	if (!this._languageslist) {
		this._languageslist = google.language.Languages;
	}
	var html = [];
	html.push("<SELECT id='", listId, "'>");
	for (var item in this._languageslist) {
		var id = this._languageslist[item];
		if (google.language.isTranslatable(id)) {
			if (languageId == id) {
				html.push("<option value='", id, "' id='GTranslatorZimlet_", id, "' selected>", this.getMessage(item), "</option>");
			} else {
				html.push("<option value='", id, "' id='GTranslatorZimlet_", id, "'>", this.getMessage(item), "</option>");
			}
		}
	}
	html.push("</SELECT>");
	return html.join("");
};