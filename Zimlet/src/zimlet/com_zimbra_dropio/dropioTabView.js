/**
 * @class  DropioTabView
 * The attach mail tab view.
 *
 * @param	{DwtTabView}	parent		the tab view
 * @param	{hash}	zimlet				the zimlet
 * @param	{string}	className		the class name
 * @extends		DwtTabViewPage
 */
DropioTabView =
function(parent, zimlet, className) {
	this.zimlet = zimlet;
	DwtTabViewPage.call(this, parent, className, Dwt.STATIC_STYLE);
	this.setScrollStyle(Dwt.SCROLL);
};

DropioTabView.prototype = new DwtTabViewPage;
DropioTabView.prototype.constructor = DropioTabView;


/**
 * Defines the "search button" element id.
 */
DropioTabView.ELEMENT_ID_UPLOAD_BTN = "attDlg_dropio_uploadBtn";


/**
 * Returns a string representation of the object.
 */
DropioTabView.prototype.toString = function() {
	return "DropioTabView";
};

/**
 * Shows the tab view.
 */
DropioTabView.prototype.showMe =
function() {
	if (this.isLoaded) {
		this._setAttachNote(true);
		return;
	}

	DwtTabViewPage.prototype.showMe.call(this);
	this.setSize(Dwt.DEFAULT, "255");
	var postCallback = new AjxCallback(this, this._createdropioList);
	this.zimlet._getDropioFileMetaData(postCallback);
	this._setAttachNote(true);
};

/**
 * Creates html list of all the drops
 */
DropioTabView.prototype._createdropioList =
function() {
	var html = [];
	var i = 0;
	html.push("<table class='dropio_table' width=100%>");
	html.push("<th class='overviewHeader'>",this.zimlet.getMessage("DropioZimlet_filesOnDropio"),"</Th></TR>");

	html.push("<tr>");
	html.push("<td><ul id='dropio_selectionMenu'>");
	var rowCls = "RowOdd";
	var rowOdd = true;
	var sortedRows = [];
	if (this.zimlet._allDropioFileMetaData) {
		var dropsArry = [];
		for (var id in this.zimlet._allDropioFileMetaData) {
			dropsArry.push(this.zimlet._allDropioFileMetaData[id]);
		}
		var sortedRows = dropsArry.sort(DropioZimlet_sortDropObjs);
	}
	this.chkBoxIdAndObjMap = [];
	for (var i = 0; i < sortedRows.length; i++) {
		var obj = eval("(" + sortedRows[i] + ")");
		var gPwd = obj.gPwd == undefined ? "test123" : obj.gPwd;
		var aPwd = obj.aPwd == undefined ? "admin_pwd" : obj.aPwd;
		var url = "http://drop.io/" + obj.dn;
		if (rowOdd) {
			rowCls = "RowOdd";
		} else {
			rowCls = "RowEven";
		}
		var b = DropioZimlet.ConvertBytes(obj.fs);
		var id = "DropIoFileChkbox_" + Dwt.getNextId();
		this.chkBoxIdAndObjMap[id] = {fn:obj.fn, url:url, b:b, gPwd:gPwd, aPwd:aPwd};

		html.push("<li>");
		html.push("<div  width=100% class='dropioRow ", rowCls, "'>");
		html.push("<table><tr><td width=16px><input type='checkbox' id='", id, "'/></td>");
		html.push("<td><b>", obj.fn, "</b><span style='color:gray;font-weight:normal;font-size:11px'> (", b, ") </span></td>");
		html.push("<td>&nbsp;&nbsp;&nbsp;&nbsp;<a href='", url, "' target='_blank'>", url, "</a></td></tr>");
		html.push("<tr><td  colspan=2><span style='color:gray;font-weight:normal;font-size:11px'>",this.zimlet.getMessage("DropioZimlet_guestPwd")," ", gPwd,
		"</span></td><td ><span style='color:gray;font-weight:normal;font-size:11px'>",this.zimlet.getMessage("DropioZimlet_adminPwd")," ",
		aPwd, "</span></td></tr></table></div>");
		html.push("</div>");
		html.push("</li>");
		rowOdd = !rowOdd;
	}
	html.push("</ul></td></tr></table>");
	document.getElementById("dropid_uploadedfiledList").innerHTML = html.join("");
	this.isLoaded = true;
};

/**
 * Resets the query.
 * @param	{string}	newQuery		the new query
 */
DropioTabView.prototype._resetQuery =
function(newQuery) {
	if (this._currentQuery == undefined)
		return newQuery;

	if (this._currentQuery != newQuery) {
		this._offset = 0;
		this._currentQuery = newQuery;
	}
	return newQuery;
};


/**
 * Hides the tab view.
 */
DropioTabView.prototype.hideMe =
function() {
	this._setAttachNote(false);
	DwtTabViewPage.prototype.hideMe.call(this);
};

/**
 * Creates main html structure
 */
DropioTabView.prototype._createHtml =
function() {
	if (!this.zimlet.prefDlg) {
		this.zimlet.prefDlg = new DropioZimletPrefDialog(this.zimlet);
	}
	this._contentEl = this.getContentHtmlElement();
	var html = [];
	html.push("<br/><form id='dropioZimlet_uploadForm' action='", this.zimlet.getResource("dropio.jsp"), "' method='post' enctype='multipart/form-data' target='dropioZimlet_upload_target' >");
	html.push("<table align=center><tr><td><input name='file' type='file' size='30' /></td>");
	html.push("<td  id='", DropioTabView.ELEMENT_ID_UPLOAD_BTN, "'></td><td id='dropioZimlet_busyIconField' width=16px></td></tr></table>");
	html.push("<iframe id='dropioZimlet_upload_target' name='dropioZimlet_upload_target' src='#' style='display:none;'>");
	html.push("</iframe>");
	html.push("</form>");
	html.push("<div><table><tr><td><input id='dropioZimlet_proxyON_attchDlg'  type='checkbox'/></td><td>",this.zimlet.getMessage("DropioZimlet_enableProxy"),"</td>");
	html.push("<td><a href='#' id='dropidZimlet_settingsLink' style='color:gray;text-decoration:underline'>",this.zimlet.getMessage("DropioZimlet_change"),"</a></td></tr></table></div>");
	html.push("<div id='dropid_uploadedfiledList' /> ");
	this._contentEl.innerHTML = html.join("");

	this._uploadBtn = new DwtButton({parent:this});
	this._uploadBtn.setText(this.zimlet.getMessage("DropioZimlet_uploadToDropio"));
	this._uploadBtn.setImage("dropio-panelIcon");
	this._uploadBtn.addSelectionListener(new AjxListener(this, this._uploadBtnListener));
	document.getElementById(DropioTabView.ELEMENT_ID_UPLOAD_BTN).appendChild(this._uploadBtn.getHtmlElement());
	//set/unset proxy
	document.getElementById("dropioZimlet_proxyON_attchDlg").checked = this.zimlet.prefDlg.dropioZimlet_proxyON == "true";

	//set settings listener
	document.getElementById("dropidZimlet_settingsLink").onclick = AjxCallback.simpleClosure(this.zimlet.singleClicked, this.zimlet);
};

/**
 * Helps showing Zimlet's notes and also hiding inline-checkbox
 * @param toNewValue  if <code>true</code>, shows Zimlet's notes
 */
DropioTabView.prototype._setAttachNote =
function(toNewValue) {
	if (this.attachDialog && !this._notesElement) {
		try {
			var cNodes = this.attachDialog._tabView._elRef.parentNode.childNodes;
			for (var i = 0; i < cNodes.length; i++) {
				if (cNodes[i].className == "ZmAttachDialog-note") {
					this._notesElement = cNodes[i];
					this._notesElementOldText = this._notesElement.innerHTML;
					break;
				}

			}
			for (var i = 0; i < cNodes.length; i++) {
				if (cNodes[i].className == "ZmAttachDialog-inline") {
					this._attachInlineElement = cNodes[i];
					break;
				}

			}

		} catch(e) {
		}
	}
	if (this._notesElement && toNewValue) {
		this._notesElement.innerHTML = ["<b>",this.zimlet.getMessage("DropioZimlet_note"),
			"</b>", this.zimlet.getMessage("DropioZimlet_uploadUpto100MB"), " ",this.zimlet.getMessage("DropioZimlet_forBiggerFiles"),
			", <a href='http://info.drop.io/' target='_blank'>", this.zimlet.getMessage("DropioZimlet_upgradeToDropio"), "</a>"].join("");

		this._attachInlineElement.style.display = "none";
	} else if (this._notesElement && !toNewValue && this._notesElementOldText) {
		this._notesElement.innerHTML = this._notesElementOldText;
		this._attachInlineElement.style.display = "block";
	}
};

/**
 * Returns false as we dont have attachments(will break dialog if we dont have this function)
 */
DropioTabView.prototype.gotAttachments =
function() {
	return false;
};

/**
 * Uploads files to drop.io
 * @see			_createHtml
 */
DropioTabView.prototype._uploadBtnListener =
function() {
	if (!this.zimlet.prefDlg) {
		this.zimlet.prefDlg = new DropioZimletPrefDialog(this.zimlet);
	}
	var dropioZimlet_apiKey = this.zimlet.prefDlg.dropioZimlet_apiKey;
	var dropioZimlet_dropName = this.zimlet.prefDlg.dropioZimlet_dropName;
	var dropioZimlet_dropAdminPwd = this.zimlet.prefDlg.dropioZimlet_dropAdminPwd;
	var dropioZimlet_dropGuestPwd = this.zimlet.prefDlg.dropioZimlet_dropGuestPwd;
	var dropioZimlet_proxyHost = this.zimlet.prefDlg.dropioZimlet_proxyHost;
	var dropioZimlet_proxyPort = this.zimlet.prefDlg.dropioZimlet_proxyPort;
	var dropioZimlet_proxyON = document.getElementById("dropioZimlet_proxyON_attchDlg").checked;

	var aPwd = dropioZimlet_dropAdminPwd == "" ? DropioZimlet.GetPassword() : dropioZimlet_dropAdminPwd;
	var gPwd = dropioZimlet_dropGuestPwd == "" ? DropioZimlet.GetPassword() : dropioZimlet_dropGuestPwd;
	this._setUploadBusyIcon();
	if (dropioZimlet_dropName != "") {
		if (dropioZimlet_dropAdminPwd == "" || dropioZimlet_dropGuestPwd == "") {
			this.zimlet._showErrorMessage(this.zimlet.getMessage("DropioZimlet_bothAdminAndGuestPwdRequired"));
			return;
		}
		var url = "https://api.drop.io/drops/:" + dropioZimlet_dropName;
		var params = [];
		params.push("version=" + AjxStringUtil.urlComponentEncode("2.0"));
		params.push("api_key=" + AjxStringUtil.urlComponentEncode(dropioZimlet_apiKey));
		params.push("format=" + AjxStringUtil.urlComponentEncode("json"));

		var data = params.join("&");
		var entireurl = ZmZimletBase.PROXY + AjxStringUtil.urlComponentEncode(url + "?" + data);
		var additionalParams = {gPwd:gPwd, aPwd:aPwd, proxyON:dropioZimlet_proxyON, pHost:dropioZimlet_proxyHost, pPort:dropioZimlet_proxyPort};

		AjxRpc.invoke(null, entireurl, null, new AjxCallback(this, this._doSubmit, additionalParams), true);
	} else {
		var url = "https://api.drop.io/drops";
		var entireurl = ZmZimletBase.PROXY + AjxStringUtil.urlComponentEncode(url);
		var params = [];
		params.push("version=2.0");
		params.push("api_key=" + dropioZimlet_apiKey);
		params.push("format=json");
		params.push("admin_email=" + appCtxt.getActiveAccount().getEmail());
		params.push("guests_can_delete=false");
		params.push("expiration_length=1_YEAR_FROM_LAST_VIEW");
		params.push("password=" + gPwd);
		params.push("admin_password=" + aPwd);
		var data = params.join("&");

		var hdrs = new Array();
		hdrs["Content-type"] = "application/x-www-form-urlencoded";
		hdrs["Content-length"] = data.length;
		hdrs["Connection"] = "close";
		this._uploadBtn.setEnabled(false);
		var additionalParams = {gPwd:gPwd, aPwd:aPwd, proxyON:dropioZimlet_proxyON, pHost:dropioZimlet_proxyHost, pPort:dropioZimlet_proxyPort};
		AjxRpc.invoke(data, entireurl, hdrs, new AjxCallback(this, this._doSubmit, additionalParams), false);
	}

};

/**
 * Submits fileupload form
 * @param {hash} params  A hash containing  file information
 * @param {object} response A response object containing drop information
 */
DropioTabView.prototype._doSubmit =
function(params, response) {
	if (!response.success) {
		this.zimlet._showErrorMessage(this.zimlet.getMessage("DropioZimlet_errorConnecting")+"<br/>", "HTTP " + response.status + "<br/>" + response.text);
		return;
	}
	var jsonObj = "";
	try {
		var jsonObj = eval("(" + response.text + ")");
		var dName = jsonObj.name;
		var adminToken = jsonObj.admin_token;
		jsonObj["gPwd"] = params.gPwd;
		jsonObj["aPwd"] = params.aPwd;
		params["dName"] = dName;
		params["adminToken"] = adminToken;
	} catch(e) {
		this._hideUploadBusyIcon();
		this.zimlet._showErrorMessage(this.zimlet.getMessage("DropioZimlet_couldNotCreateDrop"));
		this._uploadBtn.setEnabled(true);
		return;
	}
	if (!adminToken || !dName) {
		this._uploadBtn.setEnabled(true);
		this._hideUploadBusyIcon();
		this.zimlet._showErrorMessage(this.zimlet.getMessage("DropioZimlet_couldNotAdminTokenAndDropName") );
		return;
	}
	try {
		document.getElementById("dropioZimlet_upload_target").contentWindow.document.body.innerHTML = "";
	} catch(e) {
		this._uploadBtn.setEnabled(true);
		this._hideUploadBusyIcon();
		this.zimlet._showErrorMessage("Cannot access iframe.");
		return;
	}
	this._setUploadFormParameters(params);

	this._checkUploadTimer = setInterval(AjxCallback.simpleClosure(this._handlefileUploadResponse, this, jsonObj), 3000);
	document.getElementById("dropioZimlet_uploadForm").submit();
};

/**
 * Sets upload form's url so JSP can read it
 * @param setupParams A hash containing upload metadata
 */
DropioTabView.prototype._setUploadFormParameters =
function(setupParams) {
	var url = this.zimlet.getResource("dropio.jsp");
	var params = [];
	params.push("version=2.0");
	params.push("api_key=4115e41d17052650b88ea353c201b544c8d50fe4");
	params.push("name=file");
	params.push("drop_name=" + setupParams.dName);
	params.push("token=" + setupParams.adminToken);
	params.push("proxyON=" + setupParams.proxyON);
	params.push("pHost=" + setupParams.pHost);
	params.push("pPort=" + setupParams.pPort);
	document.getElementById("dropioZimlet_uploadForm").action = [url, "?", params.join("&")].join("");
};

/**
 * Sets busy icon next to upload field
 *
 * @see			_createHtml
 */
DropioTabView.prototype._setUploadBusyIcon =
function() {
	if (!this._busyIconHtml) {
		this._busyIconHtml = ["<img   src='", this.zimlet.getResource("dropio_busy.gif") , "' />"].join("");
	}
	document.getElementById("dropioZimlet_busyIconField").innerHTML = this._busyIconHtml;
};

/**
 * Hides busy icon next to upload field
 *
 * @see			_createHtml
 */
DropioTabView.prototype._hideUploadBusyIcon =
function() {
	document.getElementById("dropioZimlet_busyIconField").innerHTML = "";
	if (this._checkUploadTimer) {
		clearInterval(this._checkUploadTimer);
	}
};

/**
 * saves file's metadata and also adds it to the list
 */
DropioTabView.prototype._handlefileUploadResponse =
function(dropJsonObj) {
	var responseStr = document.getElementById("dropioZimlet_upload_target").contentWindow.document.body.innerHTML;
	if (responseStr != "") {
		this._uploadBtn.setEnabled(true);

		try {
			var fileJsonObj = eval("(" + responseStr + ")");
			this._hideUploadBusyIcon();
			this.zimlet._addFileMetaData(dropJsonObj, fileJsonObj);
			this.zimlet._saveDropioFileMetaData();
		} catch(e) {
			this._hideUploadBusyIcon();
			this.zimlet._showErrorMessage(this.zimlet.getMessage("DropioZimlet_couldNotParseJSON"), responseStr);
			return;
		}
	}
};

/**
 * Uploads the files.
 */
DropioTabView.prototype.uploadFiles =
function(attachmentDlg, docIds) {

	var composeView = appCtxt.getCurrentView();
	var currentBodyContent = currentBodyContent = appCtxt.getCurrentView().getHtmlEditor().getContent();
	var composeMode = appCtxt.getCurrentView().getHtmlEditor().getMode();
	var saperator = "\r\n";
	if (composeMode == DwtHtmlEditor.HTML) {
		saperator = "<br/>";
	}
	var str = this._getFileStrToInsert(saperator);
	try {
		composeView._htmlEditor.setContent([currentBodyContent, saperator, str].join(""));
	} catch(e) {
		var str = this._getFileStrToInsert("\r\n");
		this.zimlet._showErrorMessage([this.zimlet.getMessage("DropioZimlet_couldNotFindHTMLEditor"), e, 
			"<br/><br/>", this.zimlet.getMessage("DropioZimlet_manuallyPasteThis"),
			"<br/><textarea width=200px height=200px>",str, "</textarea>"].join(""));
	}
	attachmentDlg.popdown();
};

/**
 * Helper function that helps in formating html list for displaying Drops
 * @param {String} separator A HTML or plain/text separator
 */
DropioTabView.prototype._getFileStrToInsert =
function(separator) {
	var str = [];
	for (var chkBoxId in this.chkBoxIdAndObjMap) {
		if (document.getElementById(chkBoxId).checked) {
			var obj = this.chkBoxIdAndObjMap[chkBoxId];
			str.push(this.zimlet.getMessage("DropioZimlet_file")," ", obj.fn, " (", obj.b, ")", 
				separator, this.zimlet.getMessage("DropioZimlet_url")," ", 
				obj.url, separator, this.zimlet.getMessage("DropioZimlet_guestPwd"), " ", 
				obj.gPwd, separator, separator);
		}
	}
	return str.join("");
};