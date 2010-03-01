/*
 * ***** BEGIN LICENSE BLOCK *****
 *
 * Zimbra Collaboration Suite Zimlets
 * Copyright (C) 2005, 2006, 2007 Zimbra, Inc.
 *
 * The contents of this file are subject to the Yahoo! Public License
 * Version 1.0 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 *
 * ***** END LICENSE BLOCK *****
 */

////////////////////////////////////////////////////////////////
///  Zimlet to handle integration with SalesForce            ///
///  @author Raja Rao DV, <rrao@zimbra.com>	[V 3.0]			 ///
///  @author Mihai Bazon, <mihai@zimbra.com>                 ///
///  @author Kevin Henrikson, <kevinh@zimbra.com>            ///
////////////////////////////////////////////////////////////////
function Com_Zimbra_SForce() {
}

/// Zimlet handler objects, such as Com_Zimbra_SForce, must inherit from
/// ZmZimletBase.  The 2 lines below achieve this.
Com_Zimbra_SForce.prototype = new ZmZimletBase();
Com_Zimbra_SForce.prototype.constructor = Com_Zimbra_SForce;

Com_Zimbra_SForce.SFORCE = "SFORCE";
Com_Zimbra_SForce.SFORCE_MAIL = "SFORCE_MAIL_TB_BTN";
Com_Zimbra_SForce.SFORCE_CONTACT_TB_BTN = "SFORCE_CONTACT_TB_BTN";

Com_Zimbra_SForce.prototype.init = function() {
	this.SERVER = Com_Zimbra_SForce.LOGIN_SERVER;
	this.XMLNS = "urn:enterprise.soap.sforce.com";
	this._shell = this.getShell();
	this.loginToSFOnLaunch = this.getUserProperty("loginToSFOnLaunch") == "true";
	this._force_show_salesforceBar = false;
	this.loadLoginInfo = false;//used to ensure people has entered valid user/pwd 
	if(this.loginToSFOnLaunch) {
		this.login();
	}
	this.sForceSearchDlg = new Com_Zimbra_SForceSearchDlg(this);
};

//-------------------------------------------------------------------------------------------
//UI Handlers (START)
//-------------------------------------------------------------------------------------------
/// Called by the Zimbra framework upon an accepted drag'n'drop
Com_Zimbra_SForce.prototype.doDrop = function(obj) {
	switch (obj.TYPE) {
		case "ZmMailMsg":
			if (obj instanceof Array) {
				obj = obj[0];
			}
			var msg = obj.srcObj;
			this.noteDropped(msg);
			break;
		case "ZmConv":
			if (obj instanceof Array) {
				obj = obj[0];
			}
			var msg = obj.srcObj.getFirstHotMsg();
			this.noteDropped(msg);
			break;
		case "ZmContact":
			this.contactDropped(obj);
			break;

		case "ZmAppt":
			this.apptDropped(obj);
			break;

		default:
			this.displayErrorMessage("You somehow managed to drop a \"" + obj.TYPE
					+ "\" but however the SForce Zimlet does't support it for drag'n'drop.");
	}
};

/// Called by the Zimbra framework when the SForce panel item was clicked
Com_Zimbra_SForce.prototype.singleClicked = function() {
	this.login();
};

/// Called by the Zimbra framework when some menu item that doesn't have an
/// <actionURL> was selected
Com_Zimbra_SForce.prototype.menuItemSelected = function(itemId, val) {
	switch (itemId) {		
		case "PREFERENCES":
			this._displayLoginDialog();
			break;
		case "LOGIN":
			this.login();
			break;
		case "SFORCE_CASE_OPENCASE":
			this._openCaseOnLaunchInEditMode = false;
			this.clicked("", this._actionObject, "open");
			break;
		case "SFORCE_CASE_EDITCASE":
			this._openCaseOnLaunchInEditMode = true;
			this.clicked("", this._actionObject, "edit");
			break;
		case "SFORCE_SHOW_SALESFORCE_BAR":
			this._force_show_salesforceBar = true;
			this._addSForceBar([]);
			break;
		case "SFORCE_CASE_CLOSE":
			var caseNumber = this._getTooltipData(this._actionObject);
			var callback = new AjxCallback(this, this._closeCase); 
			this._getCaseId(caseNumber, callback);
			break;
		case "SFORCE_CASE_CHANGE":
			var caseNumber = this._getTooltipData(this._actionObject);
			this._loadCaseDescriptionObject();
			this._getCurrentValuesForQuickUpdateDlg(caseNumber);
			break;
	}
};

Com_Zimbra_SForce.prototype._closeCase =
function(id) {
	var props = {};
	var params = [];

	props["Status"] = "Close";
	props["Id"] = id;
	params.push(props);
	var callback = AjxCallback.simpleClosure(this._handleCloseCase, this);
	this.updateSFObject(params, "Case", callback, true);
};

Com_Zimbra_SForce.prototype._handleCloseCase =
function(response) {
	if(response.success) {
		appCtxt.setStatusMsg("Support Case updated successfully", ZmStatusView.LEVEL_INFO);
	}
};

Com_Zimbra_SForce.prototype._loadCaseDescriptionObject = function() {
	if(this._caseObjectLoaded) {
		return;
	}
	if(!this.sForceObject) {
		this.sForceObject = new Com_Zimbra_SForceObject(this);
	}
	var map = this.sForceObject.getFieldMap("describeSObject", "Case");
	var hasItem = false;
	for(var item in map) {
		hasItem = true;
		break;
	}
	this._caseObjectLoaded = true;
	this._sforceCaseObject = map;
};

Com_Zimbra_SForce.prototype._displayQuickUpdateDialog =
function(result) {
	if (this.quDialog) {
		this.quView.getHtmlElement().innerHTML = this._createQuickUpdateView(result);	
		this.quDialog.result = result;
		this._addCaseOwnerLookupBtn();
		this.quDialog.popup();
		return;
	}
	this.quView = new DwtComposite(this.getShell());
	this.quView.setSize("410", "240");
	this.quView.getHtmlElement().style.overflow = "auto";
	this.quView.getHtmlElement().innerHTML = this._createQuickUpdateView(result);	
	this.quDialog = this._createDialog({title:"Quick Edit Case", view:this.quView, standardButtons:[DwtDialog.OK_BUTTON, DwtDialog.CANCEL_BUTTON]});	
	this.quDialog.setButtonListener(DwtDialog.OK_BUTTON, new AjxListener(this, this._quOKBtnListner));
	this.quDialog.result = result;
	this._addCaseOwnerLookupBtn();
	this.quDialog.popup();
};

Com_Zimbra_SForce.prototype._quOKBtnListner =
function() {
	var props = {};	
	for(var i =0; i < this._quickUpdateSelectMenuList.length; i++) {
		var obj = this._quickUpdateSelectMenuList[i];
		var prevVal = obj.currentValue;
		var val = document.getElementById(obj.id).value;
		if((prevVal != undefined && prevVal != val) && (val != "None")) {
			props[obj.elName] = val;
		}
	}
	if(!this.quDialog.result.Id) {
		appCtxt.setStatusMsg("Case ID not found, Aborting updating Case", ZmStatusView.LEVEL_WARNING);
		return;
	}
	var id = this.quDialog.result.Id.toString();
	props["Id"] = id;
	var ownerId = document.getElementById("sforce_quickUpdate_changeOwner").refObjIdValue;
	if(ownerId != undefined && ownerId != "") {
		props["OwnerId"] = ownerId;
	}
	var params = [];
	params.push(props);
	var callback = AjxCallback.simpleClosure(this._handleQuickUpdateCase, this);
	this.updateSFObject(params, "Case", callback, true);
};

Com_Zimbra_SForce.prototype._handleQuickUpdateCase =
function(result) {
	if(!result.errors) {
		appCtxt.setStatusMsg("Support Case Updated", ZmStatusView.LEVEL_INFO);
		this.quDialog.popdown();
	} else {
		var msg = "";
		if(result.errors && result.errors.message) {
			msg = result.errors.message.toString();
		}
		appCtxt.setStatusMsg("Could not update Case: "+msg, ZmStatusView.LEVEL_WARNING);

	}
};

Com_Zimbra_SForce.prototype._createQuickUpdateView =
function(result) {
	var ownerName = "";
	var ownerId = ""; 
	if(result && result.Owner) {
		ownerName = result.Owner.Name.toString();
	}
	if(result && result.OwnerId) {
		ownerId = result.OwnerId.toString();
	}
	var html = new Array();
	var i = 0;
	html[i++] = "<table  width=100% cellpadding=4 class='SForce_table'>";
	html[i++] = "<tr ><td width=120px align=right><strong>Case Owner:</strong></td>";	
	html[i++] = "<td><div>";
	html[i++] ="<table>";
	html[i++] =["<td><div id='sforce_quickUpdate_changeOwner'  refObjIdValue='",ownerId,"'>",ownerName,"</div></td>"].join("");
	html[i++] ="<td><div id='sforce_quickUpdate_changeOwnerLookupBtn'></div></td>";
	html[i++] ="<td><div id='sforce_quickUpdate_changeOwnerClearDiv' style='display:none;'><a href=# id='sforce_quickUpdate_changeOwnerClearLink'>clear</a></div></td></tr>";
	html[i++] = "</table>";
	html[i++] = "</div></td></tr>";
	for(var el in this._allQuickUpdatePickLists) {
		var obj = this._allQuickUpdatePickLists[el];
		var items = obj.items;

		html[i++] = this._getQuickUpdateListHtml(el, obj.label, items, obj.currentValue);		
	}
	html[i++] = "</table>";
	html[i++] = "</DIV>";
	return html.join("");
};

Com_Zimbra_SForce.prototype._addCaseOwnerLookupBtn = function() {
		var btn = new DwtButton({parent:this._shell});
		btn.setText("Change");
		btn.setImage("Search");
		btn.addSelectionListener(new AjxListener(this, this._changeCaseOwnerBtnHdlr, [ btn]));
		document.getElementById("sforce_quickUpdate_changeOwnerLookupBtn").appendChild(btn.getHtmlElement());		
};

Com_Zimbra_SForce.prototype._changeCaseOwnerBtnHdlr = function() { 
	this.sForceSearchDlg.setProperties("User", "sforce_quickUpdate_changeOwner", null, "sforce_quickUpdate_changeOwnerClearLink");
	this.sForceSearchDlg.displaySearchDialog();
};

Com_Zimbra_SForce.prototype._getQuickUpdateListHtml =
function(elName, label, items, currentValue) {
	var html = new Array();
	var i = 0;
	var id = ["sforce_quickUpdateMenu_",elName].join("");
	var hideLinkId = ["sforce_quickUpdateMenu_hideLink_",elName].join("");

	if(!this._quickUpdateSelectMenuList) {
		this._quickUpdateSelectMenuList = [];
	}
	this._quickUpdateSelectMenuList.push({elName:elName, id:id, currentValue:currentValue, hideLinkId:hideLinkId});
	html[i++] = ["<tr ><td width=120px align=right><strong>",label,":</strong></td><td width=200px><select id='",id,"'>"].join("");
	html[i++] = "<option value='None'>None</option>";
	for(var j=0; j< items.length; j++) {
		try{
			var item = items[j];
			if(item == "") {
				continue;
			}
			var obj = eval("("+item+")");
			var label = obj.label;
			if(currentValue == label) {
				html[i++] = ["<option  value='",label,"' selected>",label,"</option>"].join("");
			} else {
				html[i++] = ["<option  value='",label,"'>",label,"</option>"].join("");
			}
		} catch(e) {
		}
	}
	html[i++] = ["</select></td></tr>"].join("");
	return html.join("");
};



//-------------------------------------------------------------------------------------------
//UI Handlers (END)
//-------------------------------------------------------------------------------------------


//-------------------------------------------------------------------------------------------
//Login dialog related (START)
//-------------------------------------------------------------------------------------------
Com_Zimbra_SForce.prototype._displayLoginDialog =
function(callback, errorMsg) {
	//if zimlet dialog already exists...
	if(callback) {
		this._loginOkCallback = callback;
	}
	if (this.loginDlg) {
		this._setLoginValues();
		this._setErrorMsgToLoginDlg(errorMsg);
		this.loginDlg.popup();//simply popup the dialog
		return;
	}
	this._loginDlgView = new DwtComposite(this._shell);
	this._loginDlgView.setSize("450", "325");
	this._loginDlgView.getHtmlElement().style.overflow = "auto";
	this._loginDlgView.getHtmlElement().innerHTML = this._createLoginDlgView();

	this.loginDlg = this._createDialog({title:"Salesforce Preferences", view:this._loginDlgView, standardButtons:[DwtDialog.OK_BUTTON, DwtDialog.CANCEL_BUTTON]});
	this.loginDlg.setButtonListener(DwtDialog.OK_BUTTON, new AjxListener(this, this._loginDlgOKBtnListener));
	this.loginDlg.setButtonListener(DwtDialog.CANCEL_BUTTON, new AjxListener(this, this._loginDlgCancelBtnListener));

	this._setLoginValues();
	this._setErrorMsgToLoginDlg(errorMsg);
	this.loginDlg.popup();
};

Com_Zimbra_SForce.prototype._setErrorMsgToLoginDlg =
function(errorMsg) {
	if(errorMsg) {
		document.getElementById("sforce_logindlg_errorDiv").innerHTML = errorMsg;
		document.getElementById("sforce_logindlg_errorDiv").style.display = "block";
	} else {
		document.getElementById("sforce_logindlg_errorDiv").innerHTML = "";
		document.getElementById("sforce_logindlg_errorDiv").style.display = "none";
	}
};

Com_Zimbra_SForce.prototype._setLoginValues =
function() {	//show the checkbox checked if needed
	var user = this.getUserProperty("user");
	var passwd = this.getUserProperty("passwd");
	this.sforce_ignoreDomainsList = this.getUserProperty("sforce_ignoreDomainsList");
	this.sforce_logindlg_sbarShowOnlyOnResult = this.getUserProperty("sforce_logindlg_sbarShowOnlyOnResult") == "true";
	if(user) {
		document.getElementById("sforce_logindlg_userNamefield").value = user;
	}
	if(passwd) {
		document.getElementById("sforce_logindlg_passwordfield").value = passwd;
	}
	if(this.sforce_ignoreDomainsList) {
		document.getElementById("sforce_logindlg_ignoreDomainsfield").value = this.sforce_ignoreDomainsList;
	}

	if(this.loginToSFOnLaunch) {
		document.getElementById("sforce_logindlg_loginToSFOnLaunch").checked = true;
	}
	if(this.sforce_logindlg_showSendAndAddBtn) {
		document.getElementById("sforce_logindlg_showSendAndAddBtn").checked = true;
	}
	if(this.sforce_logindlg_sbarShowOnlyOnResult) {
		document.getElementById("sforce_logindlg_sbarShowOnlyOnResult").checked = true;
	}
};

Com_Zimbra_SForce.prototype._createLoginDlgView =
function() {
	var html = new Array();
	var i = 0;
	html[i++] = "<DIV class='SForce_yellow' id='sforce_logindlg_errorDiv' style='display:none;color:red;font-weight:bold;'></DIV>";
	html[i++] = "<DIV>";
	html[i++] = "<TABLE class='SForce_table'  width='100%'><TR><TD style='font-weight:bold'>Salesforce User Name:</TD><TD><INPUT type='text' id='sforce_logindlg_userNamefield'></INPUT></TD></TR>";
	html[i++] = "<TR><TD  style='font-weight:bold'>Password + SecurityToken*:</TD><TD><INPUT type='password' id='sforce_logindlg_passwordfield'></INPUT></TD></TR>";
	html[i++] = "<TR><TD style='font-weight:bold'>Ignore emails with following domain(s):<br/><label style=\"font-size: 10px; color: gray;\">(Saperate multiple domains by comma)</label></TD><TD><INPUT type='text' id='sforce_logindlg_ignoreDomainsfield'></INPUT></TD></TR>";
	html[i++] = "</TABLE></DIV><BR/>";

	html[i++] = "<DIV>";
	html[i++] = "<TABLE class='SForce_table' width='100%'>";
	html[i++] = "<TR><TD width=18px><INPUT type='checkbox' id='sforce_logindlg_sbarShowOnlyOnResult'></INPUT></TD><TD  style='font-weight:bold'>Show Salesforce Bar only when there are Salesforce contacts<TD></TD></TR>";
	html[i++] = "</TABLE></DIV>";
	html[i++] = "<DIV>";
	html[i++] = "<TABLE class='SForce_table' width='100%'>";
	html[i++] = "<TR><TD width=18px><INPUT type='checkbox' id='sforce_logindlg_showSendAndAddBtn'></INPUT></TD><TD  style='font-weight:bold'>Show 'Send & Add' button in mail compose toolbar<TD></TD></TR>";
	html[i++] = "</TABLE></DIV>";
	html[i++] = "<DIV>";
	html[i++] = "<TABLE class='SForce_table' width='100%'>";
	html[i++] = "<TR><TD width=18px><INPUT type='checkbox' id='sforce_logindlg_loginToSFOnLaunch'></INPUT></TD><TD  style='font-weight:bold'>Login to Salesforce when Zimbra is launched<TD></TD></TR>";
	html[i++] = "</TABLE></DIV>";

	
	"<BR/>";
	html[i++] = "<DIV class='SForce_yellow'>";
	html[i++] = "<B>NOTES:</B><BR/>1. If your <b><i>Salesforce password</i></b> is <label style='font-weight:bold;color:red'>mypassword</label>, and your <b><i>Salesforce security token</i></b> is <label style='font-weight:bold;color:blue'>XXXXXXXXXX</label>";
	html[i++] = " then you must enter <label style='font-weight:bold;color:red'>mypassword</label><label style='font-weight:bold;color:blue'>XXXXXXXXXX</label> in <i>Password + SecuritToken</i> field. <BR/><BR/>2. <b> Steps to get Security token</b>:"; 
	html[i++] = "<BR/>- Login to Salesforce,<BR/>- Click on <b>Setup</b> near top-right corner,<BR/>- Click on <b>Reset your Security Token</b> link and reset it<br/>";
	html[i++] = "After you have reset security token, Salesforce will send you an email with new security token.";
	html[i++] = "</DIV>";
	return html.join("");
};

Com_Zimbra_SForce.prototype._loginDlgOKBtnListener =
function() {
	var needRefresh = false;
	var user = AjxStringUtil.trim(document.getElementById("sforce_logindlg_userNamefield").value);
	var passwd = AjxStringUtil.trim(document.getElementById("sforce_logindlg_passwordfield").value);
	if(user == "" || passwd == "") {
		this._setErrorMsgToLoginDlg("Please fill your Salesforce credentials");
		return;
	}
	this.sforce_ignoreDomainsList = AjxStringUtil.trim(document.getElementById("sforce_logindlg_ignoreDomainsfield").value);
	var loginToSFOnLaunch =  document.getElementById("sforce_logindlg_loginToSFOnLaunch").checked;
	this.sforce_logindlg_sbarShowOnlyOnResult =  document.getElementById("sforce_logindlg_sbarShowOnlyOnResult").checked;
	var showSendandAddBtnVal = document.getElementById("sforce_logindlg_showSendAndAddBtn").checked;
	if(showSendandAddBtnVal != this.sforce_logindlg_showSendAndAddBtn) {
		needRefresh = true;
		this.sforce_logindlg_showSendAndAddBtn = showSendandAddBtnVal;
	}

	this.loadLoginInfo = false;	
	this._ignoreDomainList = false;
	this.loginDlg.popdown();//hide the dialog

	this.setUserProperty("user", user);
	this.setUserProperty("passwd", passwd);
	this.setUserProperty("sforce_ignoreDomainsList", this.sforce_ignoreDomainsList);
	this.setUserProperty("sforce_logindlg_sbarShowOnlyOnResult", this.sforce_logindlg_sbarShowOnlyOnResult);
	this.setUserProperty("sforce_logindlg_showSendAndAddBtn", this.sforce_logindlg_showSendAndAddBtn);
	this.setUserProperty("loginToSFOnLaunch", loginToSFOnLaunch);
	var callback = new AjxCallback(this, this._handleSaveProperties, needRefresh);
	
	this.saveUserProperties(callback);
	this.login(this._loginOkCallback, user, passwd);
};

Com_Zimbra_SForce.prototype._loginDlgCancelBtnListener =
function() {
	if(this.sForceViewManager) {
		this.sForceViewManager.hideAllViewsIfBusy();
	}
	this.loginDlg.popdown();
};

Com_Zimbra_SForce.prototype._handleSaveProperties =
function(needRefresh) {
	appCtxt.setStatusMsg("Preferences Saved", ZmStatusView.LEVEL_INFO);
	if(needRefresh) {
		this.showYesNoDialog();
	}
};

Com_Zimbra_SForce.prototype.showYesNoDialog =
function() {
	var dlg = appCtxt.getYesNoMsgDialog();
	dlg.registerCallback(DwtDialog.YES_BUTTON, this._yesButtonClicked, this, dlg);
	dlg.registerCallback(DwtDialog.NO_BUTTON, this._NoButtonClicked, this, dlg);
	dlg.setMessage("The browser must be refreshed for the changes to take effect.  Continue?", DwtMessageDialog.WARNING_STYLE);
	dlg.popup();
};

Com_Zimbra_SForce.prototype._yesButtonClicked =
function(dlg) {
	dlg.popdown();
	this._refreshBrowser();
};

Com_Zimbra_SForce.prototype._NoButtonClicked =
function(dlg) {
	dlg.popdown();
}

Com_Zimbra_SForce.prototype._refreshBrowser =
function() {
	window.onbeforeunload = null;
	var url = AjxUtil.formatUrl({});
	ZmZimbraMail.sendRedirect(url);
};

//-------------------------------------------------------------------------------------------
//Login dialog related (END)
//-------------------------------------------------------------------------------------------

//-------------------------------------------------------------------------------------------
//Salesforce Bar related(START)
//-------------------------------------------------------------------------------------------
Com_Zimbra_SForce.prototype.onMsgView =
function(msg) {
	this._currentSelectedMsg = msg;
	this.sforce_bar_expanded = false;
	this._recordsForCurrentMail = [];
	this._emailsInSalesforce = [];
	this.sforce_bar_recordsForThisMsgParsed = false;

	this._emailsForCurrentNote = this._getValidAddressesForCurrentNote(msg);
	if(this._emailsForCurrentNote.length == 0){
		return;
	}
	if(!this.loadLoginInfo) {
		this.user = this.getUserProperty("user");
		this.passwd = this.getUserProperty("passwd");
		this.sforce_ignoreDomainsList = this.getUserProperty("sforce_ignoreDomainsList");
		this.sforce_logindlg_sbarShowOnlyOnResult = this.getUserProperty("sforce_logindlg_sbarShowOnlyOnResult") == "true";
		this.sforce_logindlg_showSendAndAddBtn = this.getUserProperty("sforce_logindlg_showSendAndAddBtn") == "true";
		this.loadLoginInfo = true;
	}
	if(this.user && this.user != "" && this.passwd && this.passwd != "") {
		this.noteDropped(msg, true);
	}	
};		


Com_Zimbra_SForce.prototype._addSForceBar =
function(records) {
	var viewId =  appCtxt.getCurrentViewId();
	if(viewId == "CLV" && appCtxt.getSettings().getSetting("READING_PANE_LOCATION").value == "off") {
		setTimeout(AjxCallback.simpleClosure(this._do_addSForceBar, this, "CV"), 1000);
	} else {
		this._do_addSForceBar(records, viewId);
	}
};

Com_Zimbra_SForce.prototype._do_addSForceBar =
function(records, viewId) {
	if(this.sforce_logindlg_sbarShowOnlyOnResult && records.length == 0 && !this._force_show_salesforceBar) {
		return;
	}

	if(viewId.indexOf("MSG") == 0) {
		var infoBar = document.getElementById(["zv__MSG__",viewId,"_infoBar"].join(""));
	} else {
		var infoBar = document.getElementById(["zv__",viewId,"__MSG_infoBar"].join(""));
	}
	if(!infoBar) {
		return;
	}
	if(this._previousParentNode && document.getElementById("sforce_bar_frame")) {
		this._previousParentNode.removeChild(document.getElementById("sforce_bar_frame"));
	}
	this._previousParentNode = infoBar.parentNode;
	var newNode = document.createElement("div");
	newNode.style.width = "100%";
	newNode.id = "sforce_bar_frame";
	newNode.innerHTML = this._getSFBarWidgetHtml();
	infoBar.parentNode.insertBefore(newNode, infoBar.nextSibling);

	this.changeOpac(0, newNode.style);
	this.opacity("sforce_bar_frame", 0, 100, 500);
	this._addWidgetsToSFBar();

	var doc = document.getElementById("sforce_bar_msgCell");
	if(doc) {
		if(records.length > 0) {
			var contacts = "contacts";
			if(records.length == 1) {
				contacts = "contact";
			}
			doc.innerHTML = [records.length, " ",contacts," found"].join("");
			doc.style.color = "#0033FF";
			doc.style.fontWeight = "bold";
		} else {
			doc.innerHTML = "";
		}
	}	
	this._searchAllContacts = false;//set this before calling noteDropped
};

Com_Zimbra_SForce.prototype.opacity = 
function(id, opacStart, opacEnd, millisec) {
    //speed for each frame
    var speed = Math.round(millisec / 100);
    var timer = 0;
	var styleObj =  document.getElementById(id).style;
    //determine the direction for the blending, if start and end are the same nothing happens
    if(opacStart > opacEnd) {
        for(i = opacStart; i >= opacEnd; i--) {
			setTimeout(AjxCallback.simpleClosure(this.changeOpac, this, i,  styleObj), (timer * speed));
            //setTimeout("changeOpac(" + i + ",'" + id + "')",(timer * speed));
            timer++;
        }
    } else if(opacStart < opacEnd) {
        for(i = opacStart; i <= opacEnd; i++)
            {
			setTimeout(AjxCallback.simpleClosure(this.changeOpac, this, i,  styleObj), (timer * speed));
            //setTimeout("changeOpac(" + i + ",'" + id + "')",(timer * speed));
            timer++;
        }
    }
};

//change the opacity for different browsers
Com_Zimbra_SForce.prototype.changeOpac = 
function(opacity, styleObj) {
    styleObj.opacity = (opacity / 100);
    styleObj.MozOpacity = (opacity / 100);
    styleObj.KhtmlOpacity = (opacity / 100);
    styleObj.filter = "alpha(opacity=" + opacity + ")";
};

Com_Zimbra_SForce.prototype._getSFBarWidgetHtml =
function() {
	var html = new Array();
	var i = 0;
	if(!this._sforceImage_14){
		this._sforceImage_14 = ["<img  height=14px width=14px src=\"", this.getResource("img/sforce.gif") , "\"  />"].join("");
	}
	html[i++] = "<DIV class='overviewHeader'>";
	html[i++] = "<table cellpadding=0 cellspacing=0 width=100%><tr><td width='500'>";
	html[i++] = ["<div style='cursor:pointer' id='sforce_bar_mainHandler'><table cellpadding=0 cellspacing=0><tr><td width=2px></td>",
		"<td width=11px><div id='sforce_expandCollapseIconDiv' class='ImgHeaderCollapsed'></div></td><td width=2px></td>",
		"<td>",this._sforceImage_14,"</td>",		
		"<td width=2px></td><td width='100'><label style='font-weight:bold;color:rgb(45, 45, 45);cursor:pointer'>Salesforce Bar</label></td>",
		"<td id='sforce_bar_msgCell'></td></tr></table></div></td>"].join("");
	html[i++] = "<td>";
	html[i++] = "<div id='sforce_bar_generalToolbar' style='display:none'>";
	html[i++] = "<table class='SForce_table'>";
	html[i++] = "<tr><td><input type=text id='sforce_bar_searchField'></input></td><td id='sforce_bar_searchBtn' width=80%></td>";
	html[i++] = "<td id='sforce_bar_email2CaseBtn'></td><td id='sforce_bar_addNotesBtn'></td><td><div id='sforce_bar_createNewMenuDiv'></div></td></tr></table></div>";
	html[i++] = "</td></tr></table>";
	html[i++] = "</DIV>";
	html[i++] = "<DIV  class='SForce_bar_yellow'  id='sforce_bar_resultsMainDiv'>";
	html[i++] = "<table align=center width=100%><td align=center>No search result or Salesforce information to display<td></table>"
	html[i++] = "</DIV>";
	return html.join("");
};

Com_Zimbra_SForce.prototype._addWidgetsToSFBar =
function() {
	var callback = AjxCallback.simpleClosure(this._sforceBarExpandBtnListener, this);
	document.getElementById("sforce_bar_mainHandler").onclick = callback; 	

	Dwt.setHandler(document.getElementById("sforce_bar_searchField"), DwtEvent.ONKEYPRESS, AjxCallback.simpleClosure(this._searchFieldKeyHdlr, this));
	
	var btn = new DwtButton({parent:this._shell});
	btn.setText("Add Notes");
	btn.setImage("SFORCE-panelIcon");
	btn.addSelectionListener(new AjxListener(this, this._sforceAddNotesHandler));
	document.getElementById("sforce_bar_addNotesBtn").appendChild(btn.getHtmlElement());
	
	btn = new DwtButton({parent:this._shell});
	btn.setText("Search");
	btn.setImage("Search");
	btn.addSelectionListener(new AjxListener(this, this._sforceBarSearchHandler));
	document.getElementById("sforce_bar_searchBtn").appendChild(btn.getHtmlElement());


	btn = new DwtButton({parent:this._shell});
	btn.setText("Email2Case");
	btn.setImage("Doc");
	btn.addSelectionListener(new AjxListener(this, this._sforceBarEmailToCaseHdlr));
	document.getElementById("sforce_bar_email2CaseBtn").appendChild(btn.getHtmlElement());

	btn = new DwtButton({parent:this._shell});
	btn.setText("New");
	btn.setImage("NewFolder");
	var menu = new ZmPopupMenu(btn); //create menu
	btn.setMenu(menu);//add menu to button
	document.getElementById("sforce_bar_createNewMenuDiv").appendChild(btn.getHtmlElement());

	var items = ["Account", "Case", "Contact", "Lead", "Opportunity", "Solution","Report", "Campaign", "Product"];
	for(var i=0; i < items.length; i++) {
		var itemName = items[i];
		var mi = menu.createMenuItem(itemName, {image:"SFORCE-panelIcon", text:itemName});
		mi.addSelectionListener(new AjxListener(this, this._createNewMenuListener, itemName));
	}
};

Com_Zimbra_SForce.prototype._searchFieldKeyHdlr =
function(ev) {
	var event = ev || window.event;
	if (event.keyCode != undefined && event.keyCode != 13) {//if not enter key
		return;
	}
	this._sforceBarSearchHandler();
};

Com_Zimbra_SForce.prototype._sforceBarSearchHandler =
function() {
	var callback = AjxCallback.simpleClosure(this._sforceBarSearchResultHandler, this);
	var q = ["FIND {",document.getElementById("sforce_bar_searchField").value,"} RETURNING Account(Id,Name),Contact(Id,Name),Lead(Id,Name),Opportunity(Id,Name),Case(Id,Subject)"].join("");
	this.search(q, 10, callback, true, true);
};

Com_Zimbra_SForce.prototype._sforceBarSearchResultHandler =
function(result) {
	var records = result.getDoc().getElementsByTagName("record");
	var html = new Array();
	var i = 0;
	html[i++] = "<div  style='font-weight:bold;font-size:12px;background-color:#EFE7D4;padding-left:10px' width=100%>Search Result:</div>";
	html[i++] = "<div  style='font-weight:bold;font-size:14px;padding-left:10px' width=100%>";
	html[i++] = "<table class='SForce_listView' cellpadding=2 cellspacing=2 border=0 width=100%>";
	html[i++] = "<tr align=left><th width=5%>Type</th><th width=30%>Name or Subject</th><th>Action</th></tr>";
	var idStr = "sf:Id";
	var nameStr = "sf:Name";
	var subjectStr = "sf:Subject";
	if(AjxEnv.isChrome || AjxEnv.isSafari) {
		idStr = "Id";
		nameStr = "Name";
		subjectStr = "Subject";
	}
	for(var j=0; j < records.length; j++) {
		var rec = records[j];
		var type = rec.attributes[0].nodeValue.replace("sf:", "");
		var idObj =  rec.getElementsByTagName(idStr)[0];
		if(idObj.textContent) {
			var id = idObj.textContent;
			if(type != "Case") {
				var nameOrSubject = rec.getElementsByTagName(nameStr)[0].textContent;
			} else {
				var nameOrSubject = rec.getElementsByTagName(subjectStr)[0].textContent;
			}
		} else {//IE..
			var id = idObj.text;
			if(type != "Case") {
				var nameOrSubject = rec.getElementsByTagName(nameStr)[0].text;
			} else {
				var nameOrSubject = rec.getElementsByTagName(subjectStr)[0].text;
			}
		}
		

		html[i++] = ["<tr><td  width=5%>",type,"</td>"].join("");
		html[i++] = ["<td  width=35%>",nameOrSubject,"</td>"].join("");
		html[i++] = ["<td>", this._getSFViewEditLinks(id), "</td></tr>"].join("");
	}
	html[i++] = "</table>";
	html[i++] = "</div>";
	document.getElementById("sforce_bar_resultsMainDiv").innerHTML = html.join("");
};


Com_Zimbra_SForce.prototype._sforceAddNotesHandler =
function() {
	if (this._currentSelectedMsg) {
		this._searchAllContacts = false;//set this before calling noteDropped
		this.noteDropped(this._currentSelectedMsg);
	}
};

Com_Zimbra_SForce.prototype._sforceBarEmailToCaseHdlr =
function() {
	var contactName = "";
	var subject = this._currentSelectedMsg.subject;
	var body = this.getMailBodyAsText(this._currentSelectedMsg);
	var fromEmailObj = this._currentSelectedMsg.getAddress(AjxEmailAddress.FROM);
	var sfName = this._emailExistsInSF(fromEmailObj.address);
	if(sfName) {
		var contactName = sfName;
	} else {
		var arry = this._currentSelectedMsg.participants.getArray();
		for(var i=0; i < arry.length; i++) {
			var emailObj = arry[i];
			var sfName = this._emailExistsInSF(emailObj.address);
			if(sfName) {
				contactName = sfName;
				break;
			}
		}
	}
	if(!this.sforceInstanceName) {
		this.sforceInstanceName =(this.SERVER.split(".")[0]).split("//")[1];
	}
	var win = window.open(["https://", this.sforceInstanceName, ".salesforce.com/500/e?retURL=/500/o",
		"&cas3=",  AjxStringUtil.urlEncode(contactName), 
		"&cas14=",   AjxStringUtil.urlComponentEncode(subject),
		"&cas15=",  AjxStringUtil.urlComponentEncode(AjxStringUtil.htmlDecode( this._getCurrentAddressString() + body))].join(""));
};

Com_Zimbra_SForce.prototype._emailExistsInSF =
function(email) {
	var len = this._emailsInSalesforce.length;
	for(var i=0; i < len; i++) {
		var sfEmail = this._emailsInSalesforce[i].email;
		if(AjxStringUtil.trim(email.toLowerCase()) == AjxStringUtil.trim(sfEmail.toLowerCase())) {
			return this._emailsInSalesforce[i].name;
		}
	}
	return false;
};

Com_Zimbra_SForce.prototype._getCurrentAddressString =
function() {
	var frmAdd = this._currentSelectedMsg.getAddresses(AjxEmailAddress.FROM);
	var toAdd = this._currentSelectedMsg.getAddresses(AjxEmailAddress.TO);
	var ccAdd = this._currentSelectedMsg.getAddresses(AjxEmailAddress.CC);
	if(frmAdd) {
		frmAdd = frmAdd.getArray();
	}
	if(toAdd){
		toAdd = toAdd.getArray();
	}
	if(ccAdd) {
		ccAdd = ccAdd.getArray();
	}
	var str = "From: ";
	for(var i=0; i< frmAdd.length; i++) {
		str = [str, frmAdd[i].address, ";"].join("");
	}
	if(toAdd.length > 0) {
		str =  [str, "\r\n", "To: "].join("");
	}
	for(var i=0; i< toAdd.length; i++) {
		str = [str, toAdd[i].address, ";"].join("");
	}
	if(ccAdd.length > 0){
		str =  [str, "\r\n",  "Cc: "].join("");
	}
	for(var i=0; i< ccAdd.length; i++) {
		str = [str, ccAdd[i].address, ";"].join("");
	}
	return str + "\r\n-----------------------------------\r\n\r\n";
};


Com_Zimbra_SForce.prototype._createNewMenuListener =
function(itemName) {
	var itemCode = "";
	switch(itemName) {
		case "Account":
			itemCode = "001";
			break;
		case "Lead":
			itemCode = "500";
			break;
		case "Opportunity":
			itemCode = "006";
			break;
		case "Case":
			itemCode = "500";
			break;		
		case "Solution":
			itemCode = "501";
			break;		case "Account":
			itemCode = "001";
			break;
		case "Report":
			itemCode = "00O";
			break;		case "Account":
			itemCode = "001";
			break;
		case "Campaign":
			itemCode = "701";
			break;	
		case "Product":
			itemCode = "01t";
			break;				

	}
	if(!this.sforceInstanceName) {
		this.sforceInstanceName =(this.SERVER.split(".")[0]).split("//")[1];
	}
	var completeUrl = ["https://",this.sforceInstanceName,".salesforce.com/secur/frontdoor.jsp?sid=", 
						AjxStringUtil.urlEncode(this.sessionId), "&retURL=/", itemCode, "/e?retURL=/",itemCode,"/o"].join("");

	window.open(completeUrl);
};


Com_Zimbra_SForce.prototype._sforceBarExpandBtnListener =
function() {
	if(!this.sforce_bar_recordsForThisMsgParsed) {
		this._setResultsToSForceBar();
	}
	if(!this.sforce_bar_expanded) {
		document.getElementById("sforce_expandCollapseIconDiv").className = "ImgHeaderExpanded";
		document.getElementById("sforce_bar_generalToolbar").style.display = "block";
		document.getElementById("sforce_bar_resultsMainDiv").style.display = "block";
		document.getElementById("sforce_bar_msgCell").style.display = "none";
		this.sforce_bar_expanded = true;
	} else {
		document.getElementById("sforce_expandCollapseIconDiv").className = "ImgHeaderCollapsed";
		document.getElementById("sforce_bar_generalToolbar").style.display = "none";
		document.getElementById("sforce_bar_resultsMainDiv").style.display = "none";
		document.getElementById("sforce_bar_msgCell").style.display = "block";

		this.sforce_bar_expanded = false;
	}
};

Com_Zimbra_SForce.prototype._setResultsToSForceBar =
function() {
	this.sforce_bar_recordsForThisMsgParsed = true;
	if(this._recordsForCurrentMail.length == 0) {
		return;
	}
	var len = this._recordsForCurrentMail.length;
	var html = new Array();
	var i = 0;
	for(var k=0; k < len; k++) {
		var c = this._recordsForCurrentMail[k];
		var email = c.Email ? c.Email.toString() : "";
		var name = c.Name ? c.Name.toString() : "";
		this._emailsInSalesforce.push({email:email, name:name});

		var phone = c.Phone ? c.Phone.toString() : "";
		html[i++] = "<br/>";
		html[i++] = "<DIV  class='SForce_lightyellow' style='width:94%; position:relative; left:3%;'>";
		html[i++] = ["<div class='overviewHeader' style='font-weight:bold;font-size:14px;padding-left:10px' width=100%>Contact: ",name,"<span style='font-size:11px;font-weight:normal'>",this._getSFViewEditLinks(c.Id.toString()),"</span></div>"].join("");
		html[i++] = "<div  style='font-weight:bold;padding-left:10px' width=100%>";
		html[i++] = "<table  class='SForce_listView'  cellpadding=2 cellspacing=0 border=0 width=100%>";
		html[i++] = "<tr align=left><th width=35%>Account</th><th width=width=35%>Email</th><th width=15%>Phone</th><th>Action</th></tr>";
		html[i++] = ["<tr><td  width=35%>",c.Account.Name.toString(),"</td><td  width=35%>",email,"</td><td width=15%>",phone,"</td><td>", this._getSFViewEditLinks(c.Account.Id.toString()), "</td></tr>"].join("");
		html[i++] = "</table>";
		html[i++] = "</div>";
		html[i++] = "<br/>";
		var cases = c.Cases;
		if(cases) {
			var records = cases.records;
			if(records && !(records instanceof Array)) {
				records = [records];
			}
			if(records) {
				html[i++] = "<div  style='font-weight:bold;font-size:12px;background-color:#EFE7D4;padding-left:10px' width=100%>Cases:</div>";
				html[i++] = "<div  style='font-weight:bold;font-size:14px;padding-left:10px' width=100%>";
				html[i++] = "<table   class='SForce_listView' cellpadding=2 cellspacing=0 border=0 width=100%>";
				html[i++] = "<tr align=left><th width=30%>Case Number</th><th width=30%>Subject</th><th width=15%>Status</th><th>Action</th></tr>";
				for(var j=0; j < records.length; j++) {
					var rec = records[j];
					html[i++] = ["<tr><td  width=35%>",rec.CaseNumber.toString(),"</td>"].join("");
					html[i++] = ["<td  width=35%>",rec.Subject.toString(),"</td>"].join("");
					html[i++] = ["<td width=15%>",rec.Status.toString(),"</td><td>", this._getSFViewEditLinks(rec.Id.toString()), "</td></tr>"].join("");
				}
				html[i++] = "</table>";
				html[i++] = "</div>";
			}
		}

		html[i++] = "<br/>";
		var op = c.OpportunityContactRoles;
		if(op) {
			var records = op.records;
			if(records && !(records instanceof Array)) {
				records = [records];
			}
			if(records) {
				html[i++] = "<div  style='font-weight:bold;font-size:12px;padding-left:10px;background-color:#EFE7D4;' width=100%>Opportunities:</div>";
				html[i++] = "<div  style='font-weight:bold;font-size:14px;padding-left:10px' width=100%>";
				html[i++] = "<table   class='SForce_listView' cellpadding=2 cellspacing=0 border=0 width=100%>";
				html[i++] = "<tr align=left><th width=35%>Name</th><th width=35%>Role</th><th width=15%>Action</th></tr>";
				for(var j=0; j < records.length; j++) {
					var rec = records[j];
					html[i++] = ["<tr><td width=35%>",rec.Opportunity.Name.toString(),"</td>"].join("");
					var role = "";
					if(rec.Role){
						role = rec.Role.toString();
					}
					html[i++] = ["<td width=35%>",role,"</td><td width=15%>", this._getSFViewEditLinks(rec.Id.toString()), "</td></tr>"].join("");
				}
				html[i++] = "</table>";
				html[i++] = "</div>";
			}
		}

		html[i++] = "</DIV>";
		html[i++] = "<br/>";
	}

	document.getElementById("sforce_bar_resultsMainDiv").innerHTML = html.join("");
};

Com_Zimbra_SForce.prototype._getSFViewEditLinks =
function(id) {
	if(!this.sforceInstanceName) {
		this.sforceInstanceName =(this.SERVER.split(".")[0]).split("//")[1];
	}
	var baseUrl = ["https://",this.sforceInstanceName,".salesforce.com/secur/frontdoor.jsp?sid=", AjxStringUtil.urlEncode(this.sessionId), "&retURL="].join("");
	var viewPart = AjxStringUtil.urlEncode(["/",id].join(""));
	var editPart = AjxStringUtil.urlEncode(["/", id, "/e?retURL=/", id].join(""));
	var clonePart =AjxStringUtil.urlEncode(["/", id, "/e?retURL=/", id, "&clone=1"].join(""));
	//var deletePart = AjxStringUtil.urlEncode(["/setup/own/deleteredirect.jsp?delID=", id].join(""));
	return ["&nbsp;<a target='_blank' href='", baseUrl,viewPart, "'>view</a>&nbsp;",
			"<a  target='_blank' href='",  baseUrl,editPart, "'>edit</a>&nbsp;",
			"<a  target='_blank' href='",  baseUrl,clonePart, "'>clone</a>"].join("");
};

//-------------------------------------------------------------------------------------------
//.....Salesforce Bar related (END)
//-------------------------------------------------------------------------------------------


//-------------------------------------------------------------------------------------------
// Support Case Link in mail related...
//-------------------------------------------------------------------------------------------

Com_Zimbra_SForce.prototype._getCaseId =
function(caseNumber, callback) {
	var q =  ["Select Id from  Case where CaseNumber='",caseNumber,"'"].join("");
	var callback = AjxCallback.simpleClosure(this._handleGetCaseId, this, callback); 
	this.query(q, 10, callback);
};

Com_Zimbra_SForce.prototype._handleGetCaseId =
function(callback, result) {
	if(result.length == 0) {
		appCtxt.setStatusMsg("Unknown case or you are not authorized to view this case", ZmStatusView.LEVEL_WARNING);
		return;
	}
	var id = result[0].Id.toString();
	if(callback) {
		callback.run(id);
	}
};

Com_Zimbra_SForce.prototype._getCurrentValuesForQuickUpdateDlg =
function(caseNumber) {
	this._allQuickUpdatePickLists = [];
	var pickListNames = [];
	for(var el in this._sforceCaseObject) {
		var obj = this._sforceCaseObject[el];
		if(obj.type != "picklist") {
			continue;
		}
		try{
			var items = obj.picklistValues.split("=::=");
			this._allQuickUpdatePickLists[el] = ({name:el, label:obj.label, items:items});
			pickListNames.push(el);
		} catch(e) {
	
		}
		
	}
	var q =  ["Select Id,Owner.Name,OwnerId,",pickListNames.join(",")," from  Case where CaseNumber='",caseNumber,"'"].join("");
	var callback = AjxCallback.simpleClosure(this._handleGetCurrentValuesFoQuickUpdateDlg, this); 
	this.query(q, 10, callback);
	
};
Com_Zimbra_SForce.prototype._handleGetCurrentValuesFoQuickUpdateDlg =
function(result) {
	var result = result[0];
	if(!result) {
		appCtxt.setStatusMsg("Could not create Quick Edit Dialog.", ZmStatusView.LEVEL_WARNING);
		return;
	}
	for(var item in result) {
		if(this._allQuickUpdatePickLists[item]) {
			this._allQuickUpdatePickLists[item]["currentValue"] = result[item].toString();
		}
	}
	this._displayQuickUpdateDialog(result);
};

Com_Zimbra_SForce.prototype.clicked =
function(element, caseNumber, mode) {
	var caseNumber = this._getTooltipData(caseNumber);
	var callback = new AjxCallback(this, this._handleCaseContextmenu, mode); 
	this._getCaseId(caseNumber, callback);
};

Com_Zimbra_SForce.prototype._handleCaseContextmenu =
function(mode, id) {
	if(!this.sforceInstanceName) {
		this.sforceInstanceName =(this.SERVER.split(".")[0]).split("//")[1];
	}
	var baseUrl = ["https://",this.sforceInstanceName,".salesforce.com/secur/frontdoor.jsp?sid=", AjxStringUtil.urlEncode(this.sessionId), "&retURL="].join("");
	var viewPart = AjxStringUtil.urlEncode(["/",id].join(""));
	var editPart = AjxStringUtil.urlEncode(["/", id, "/e?retURL=/", id].join(""));
	if(mode == "edit") {
		window.open(baseUrl + editPart);
	} else {
		window.open(baseUrl + viewPart);
	}
};


Com_Zimbra_SForce.prototype.toolTipPoppedUp =
function(spanElement, caseNumber, matchContext, canvas) {
	caseNumber = this._getTooltipData(caseNumber);
	this._sCaseTooltipFields = "Owner.Name,Subject,Priority,Status,Reason,Contact.Name,Contact.Phone,Contact.Email,Account.Name";
	var q =  ["Select ",this._sCaseTooltipFields," from  Case where CaseNumber='",caseNumber,"'"].join("");
	this._setCaseTooltipHtml(canvas);

	var callback = AjxCallback.simpleClosure(this._setCaseTooltipHtml, this, canvas); 
	this.query(q, 10, callback);
};


Com_Zimbra_SForce.prototype._getTooltipData =
function(objData) {
	objData = objData.toLowerCase();
	objData = objData.replace("case","").replace(":", "");
	objData = AjxStringUtil.trim(objData);
	return objData;
};

Com_Zimbra_SForce.prototype._setCaseTooltipHtml =
function(canvas, obj) {
	if(!obj) {
		canvas.innerHTML =  "Loading..";
		return;
	}
	var props = obj[0];
	if(!props){
		canvas.innerHTML =  "Case Details could not be retrieved";
		return;
	}
	var html = new Array();
	var i = 0;
	var fields = this._sCaseTooltipFields.split(",");
	var len = fields.length;
	html[i++] = "<table  cellpadding=2 cellspacing=0 border=0>";
	for(var j=0; j < len; j++) {
		var name = fields[j];
		var val = this._getVal(name, props);
		name = name.replace(".", " ");
		if(val == "High" && name == "Priority") {
			html[i++] = ["<tr align='right'><td><strong>",name, ": </strong></td><td align='left'><label style='color:red;font-weight:bold;'>",val,"</label></td></tr>"].join("");
		} else {
			html[i++] = ["<tr align='right'><td><strong>",name, ": </strong></td><td align='left'>",val,"</td></tr>"].join("");
		}
	}
	html[i++] = ["</table>"].join("");	
	canvas.innerHTML =  html.join("");
};

Com_Zimbra_SForce.prototype._getVal =
function(colName, ConProps) {
	try{
		var cObj = null;
		if(colName.indexOf(".") == -1) {
			cObj = ConProps[colName];
		} else {
			var objs = colName.split(".");
			for(var i =0; i < objs.length; i++) {
				if(i == 0) {
					cObj = ConProps[objs[i]];
				} else {
					if(!cObj){
						return "";
					}
					cObj =  cObj[objs[i]];
				}
			}
		}
		if(!cObj){
			return "";
		}
		return cObj.__msh_content;
	} catch (e) {
	}
	return "";
};

//-------------------------------------------------------------------------------------------
//... Support Case Link in mail related (END)
//-------------------------------------------------------------------------------------------


//--------------------------------------------------------------------------------------------------------
//Notes dropped... (START)
//--------------------------------------------------------------------------------------------------------
Com_Zimbra_SForce.prototype.noteDropped = function(note, showInBar) {
	//stores checkboxId and salesforce obj type _dwtIdAndObjType["DWT124"] = "A" or "O" or "C"
	this._dwtIdAndObjType = [];

	if (!note)
		return;


	if(!this._emailsForCurrentNote) {
		this._emailsForCurrentNote = this._getValidAddressesForCurrentNote(note);
	} 
	if(this._emailsForCurrentNote.length == 0) {
		return;
	}

	var q = ["Select c.Id,c.Name,c.Email,c.Phone,c.OtherPhone,c.Title,c.MailingStreet,c.MailingCity, c.MailingState,c.MailingCountry,c.MailingPostalCode,c.Account.name,c.Account.Id,",
			"(select id,role,opportunity.name,Opportunity.Id from opportunitycontactroles where opportunity.stagename !='Closed Won' AND opportunity.stagename != 'Closed Lost'   limit 5),",
			"(select id,subject,caseNumber,Status from Cases Where Status !='Closed' limit 5)",
				//"(Select id,subject from ActivityHistories) ",
			" from contact c where Email='", this._emailsForCurrentNote.join("' or Email='"), "'"].join("");
	
	if(!showInBar) {
		this._showNotesDlg(note);
	}
	var callback = new AjxCallback(this, this._handleAddNotesRecords, [showInBar]);
	this.query(q, 10, callback);
	

	function $search_acct(records) {
		// Split Opportunities and Contacts into Account groups
		var acctsSorted = {};
		var a = Com_Zimbra_SForce._RECENT.Accounts;
		for (var i = 0; i < a.length; ++i) {
			acctsSorted[a[i].Id] = a[i];
			acctsSorted[a[i].Id].TYPE = "A";
			acctsSorted[a[i].Id].Con = [];
			acctsSorted[a[i].Id].Opp = [];
		}
		var c = Com_Zimbra_SForce._RECENT.Contacts;
		this.contactsIdAndAccountObjMap = [];
		for (var i = 0; i < c.length; ++i) {
			c[i].TYPE = "C";
			var accountId = c[i].AccountId;
			if (acctsSorted[c[i].AccountId]) {
				acctsSorted[c[i].AccountId].Con.push(c[i]);
				//also map account to contactId so we can create associations based on # of contacts
				this.contactsIdAndAccountObjMap[c[i].Id.toString()] = acctsSorted[c[i].AccountId];
			}
		}

		//Get the list of ids for all contacts to which we user has actually sent this email to.
		this._sentContactsMappedIds = [];
		var c = Com_Zimbra_SForce._RECENT.Contacts;
		for (var i = 0; i < c.length; ++i) {
			var reqC = c[i].Email.__msh_content;
			for (var j = 0; j < this._emailsForCurrentNote.length; j++) {
				var currC = this._emailsForCurrentNote[j];
				if (currC.toLowerCase() == reqC.toLowerCase()) {
					this._sentContactsMappedIds.push(c[i].Id.__msh_content);
				}
			}
		}

		var o = records;
		for (var i = 0; i < o.length; ++i) {
			o[i].TYPE = "O";
			acctsSorted[o[i].AccountId].Opp.push(o[i]);
		}
		this._setRecordsToNotesDlg(acctsSorted);
		this._addLookupButtons();
		this._resetAssociationRows();
	}
};

Com_Zimbra_SForce.prototype._getValidAddressesForCurrentNote =
function (note) {
	var emails = [];
	if (note._addrs) {
		for (var i = 0; i < ZmMailMsg.ADDRS.length; i++) {
			var type = ZmMailMsg.ADDRS[i];
			var a = note._addrs[type];
			if (a) {
				var emls = a._array;
				if(emls instanceof Array) {
					if(emls.length < 10) {//skip checking for emails if # is >9
						emails = emails.concat(this._addEmails(emls));
					}
				} else {
					emails = emails.concat(this._addEmails(emls));
				}
			}
		}
	} else {
		if(note.participants.length < 10) {
			emails = emails.concat(this._addEmails(note.participants));
		} else {
			emails = emails.concat(this._addEmails(note.from));
		}
	}
	emails = sforce_unique(emails);
	return 	emails;
};

Com_Zimbra_SForce.prototype._addEmails =
function (a) {
	var emails = [];
	if (!a) {
		return;
	}
	if (typeof a == "string") {
		if(!this._ignoreThisEmail(a)){
			emails.push(a);
		}
	} else if (a instanceof Array) {
		for (var i = 0; i < a.length; ++i) {
			var address = a[i].address;
			if(address && !this._ignoreThisEmail(address)){
				emails.push(a[i].address);
			}
		}
	}
	return emails;
};


Com_Zimbra_SForce.prototype._ignoreThisEmail =
function(email) {
	email = email.toLowerCase();
	if(!this._ignoreDomainList) {
		this._ignoreDomainList = [];
		var igd = this.getUserProperty("sforce_ignoreDomainsList");
		if(igd != undefined) {
			if(igd.indexOf(",") >=0) {
				this._ignoreDomainList = igd.toLowerCase().split(",");
			} else {
				this._ignoreDomainList.push(igd.toLowerCase());
			}
		}
	}
	for(var i = 0; i < this._ignoreDomainList.length; i++) {
		if(email.indexOf(this._ignoreDomainList[i]) >=0) {
			return true;
		}
	}
	return false;
};

Com_Zimbra_SForce.prototype._handleAddNotesRecords =
function(showInBar, zimlet, records) {	
	this._recordsForCurrentMail = records;
	if(!showInBar) {
		this._setRecordsToNotesDlg(records);
		this._setAddNotesHandlers();
	} else {
		this._updateSforceBar(records);
	}
};

Com_Zimbra_SForce.prototype._updateSforceBar =
function(records) {
	this._addSForceBar(records);	
};


Com_Zimbra_SForce.prototype.call_internalFunc = function(callback, param) {
	callback.call(this, param);
};

Com_Zimbra_SForce.prototype._setAddNotesHandlers = function() {
	var callback = AjxCallback.simpleClosure(this._sfItemSelectionHandler, this, "sforce_contactLead_lookupMenu", "sforce_contactLead_selectionMenu");
	document.getElementById("sforce_contactLead_lookupMenu").onchange = callback;	
	callback = AjxCallback.simpleClosure(this._sfItemSelectionHandler, this, "sforce_relatedTo_lookupMenu", "sforce_relatedTo_selectionMenu");
	document.getElementById("sforce_relatedTo_lookupMenu").onchange = callback;	

	//callback = AjxCallback.simpleClosure(this._showCreateNewContactOrLeadDlg, this,"sforce_contactLead_selectionMenu");
	//document.getElementById("sforce_contactLead_createMenu").onchange = callback;

	callback = AjxCallback.simpleClosure(this._showCreateNewContactOrLeadDlg, this,"Contact");
	document.getElementById("sforce_quickCreateContactLnk").onclick = callback;
	
	callback = AjxCallback.simpleClosure(this._showCreateNewContactOrLeadDlg, this,"Lead");
	document.getElementById("sforce_quickCreateLeadLnk").onclick = callback;
};

Com_Zimbra_SForce.prototype._showCreateNewContactOrLeadDlg =
function(val) {
	//var val = document.getElementById("sforce_contactLead_createMenu").value;
	//if zimlet dialog already exists...
	if (this._sforceCreateNewObjsDlg) {
		this._resetCreateNewContactOrLeadDlg();
		this._showHideFields(val);
		this._sforceCreateNewObjsDlg.popup();
		return;
	}
	this._sforceCreateNewView = new DwtComposite(this.getShell());
	this._sforceCreateNewView.getHtmlElement().style.overflow = "auto";
	this._sforceCreateNewView.getHtmlElement().innerHTML = this._createNewContactOrLeadView();

	this._sforceCreateNewObjsDlg = this._createDialog({title:"Create New Contact or Lead", view:this._sforceCreateNewView, standardButtons:[DwtDialog.OK_BUTTON, DwtDialog.CANCEL_BUTTON]});
	this._sforceCreateNewObjsDlg.setButtonListener(DwtDialog.OK_BUTTON, new AjxListener(this, this._createNewContactOrLeadOKBtnListner));
	this._addAccountLookupBtn();
	this._showHideFields(val);
	this._addCreateNewContactOrLeadHdlrs();
	this._sforceCreateNewObjsDlg.popup();
};

Com_Zimbra_SForce.prototype._createNewContactOrLeadView =
function() {
	var html = new Array();
	var i = 0;
	html[i++] = "<DIV>";
	html[i++] = "<table><tr><td><input type='radio' id='sforce_contactOrLeadC' name='sforce_contactOrLead' ></input>Contact</td>";
	html[i++] ="<td colspan=3><input  id='sforce_contactOrLeadL' type='radio' name='sforce_contactOrLead' ></input>Lead</td></tr>";
	html[i++] ="<tr><td>First Name:</td><td colspan=3><input type='text' id='sforce_contactOrleadFN'></input></td></tr>";
	html[i++] ="<tr><td>Last Name<span style='color:red;font-weight:bold;'>*</span>:</td><td colspan=3><input type='text' id='sforce_contactOrleadLN'></input></td></tr>";
	
	html[i++] ="<tr  id='sforce_contactOrLeadAccTR'><td>Account:</td><td><div id='sforce_contactOrLeadAcc'  refObjIdValue=''></div></td>";
	html[i++] ="<td><div id='sforce_contactOrLeadAccLookupBtn'></div></td>";
	html[i++] ="<td><div id='sforce_contactOrLeadAccClearDiv' style='display:none;'><a href=# id='sforce_contactOrLeadAccClearLnk'>clear</a></div></td></tr>";
	html[i++] ="<tr id='sforce_contactOrLeadCompTR'><td>Company<span style='color:red;font-weight:bold;'>*</span>:</td>";
	html[i++] ="<td colspan=3><input type='text' id='sforce_contactOrleadComp'></input></td></tr>";
	html[i++] ="<tr><td>Phone:</td><td colspan=3><input type='text' id='sforce_contactOrleadPH'></input></td></tr>";
	html[i++] ="<tr><td>Email:</td><td colspan=3><input type='text' id='sforce_contactOrleadEm'></input></td></tr>";
	html[i++] ="</table>";
	html[i++] = "</DIV>";
	return html.join("");
};

Com_Zimbra_SForce.prototype._createNewContactOrLeadOKBtnListner =
function() {
	var ln = document.getElementById("sforce_contactOrleadLN").value;
	if(ln == "") {
		appCtxt.setStatusMsg("'Last Name' cannot be empty", ZmStatusView.LEVEL_WARNING);		
		return;
	}

	if(this._sforceCreateNewObjsDlg.type == "Lead") {
		var company = document.getElementById("sforce_contactOrleadComp").value;
		if(company == "") {
			appCtxt.setStatusMsg("'Company' cannot be empty for a lead", ZmStatusView.LEVEL_WARNING);		
			return;
		}
	} else {//contact..
		var accountId = document.getElementById("sforce_contactOrLeadAcc").refObjIdValue;
	}

	var fn = document.getElementById("sforce_contactOrleadFN").value;
	var ph = document.getElementById("sforce_contactOrleadPH").value;
	var em = document.getElementById("sforce_contactOrleadEm").value;

	var props = {};
	props["FirstName"] = fn;
	props["LastName"] = ln;
	if(this._sforceCreateNewObjsDlg.type == "Lead") {
		props["Company"] = company;
	} else {
		props["AccountId"] = accountId;
	}
	props["Phone"] = ph;
	props["Email"] = em;
	var params = [];
	params.push(props);
	var callback = AjxCallback.simpleClosure(this._updateSelectMenuInNotesDlg, this, this._sforceCreateNewObjsDlg.type, "sforce_contactLead_selectionMenu",params);
	this.createSFObject(props, this._sforceCreateNewObjsDlg.type, callback, true);
	this._sforceCreateNewObjsDlg.popdown();//hide the dialog
};

Com_Zimbra_SForce.prototype._resetCreateNewContactOrLeadDlg =
function() {
	document.getElementById("sforce_contactOrleadFN").value = "";
	document.getElementById("sforce_contactOrleadLN").value = "";
	document.getElementById("sforce_contactOrleadPH").value = "";
	document.getElementById("sforce_contactOrleadEm").value = "";
	this._createNewContactOrLeadClearLinkHdlr();
};


Com_Zimbra_SForce.prototype._createNewContactOrLeadClearLinkHdlr =
function() {
	document.getElementById("sforce_contactOrLeadAcc").innerHTML =  "";
	document.getElementById("sforce_contactOrLeadAcc").refObjIdValue = "";//set custom parameter
	document.getElementById("sforce_contactOrLeadAccClearDiv").style.display = "none";
};

Com_Zimbra_SForce.prototype._showHideFields =
function(type) {
	if(type.indexOf("Contact") >=0) {
		this._sforceCreateNewObjsDlg.type = "Contact";
		document.getElementById("sforce_contactOrLeadAccTR").style.display = "";
		document.getElementById("sforce_contactOrLeadCompTR").style.display = "none";
		document.getElementById("sforce_contactOrLeadC").checked = true;
	} else {
		this._sforceCreateNewObjsDlg.type = "Lead";
		document.getElementById("sforce_contactOrLeadAccTR").style.display = "none";
		document.getElementById("sforce_contactOrLeadCompTR").style.display = "";
		document.getElementById("sforce_contactOrLeadL").checked = true;
	}
};

Com_Zimbra_SForce.prototype._addCreateNewContactOrLeadHdlrs =
function() {
	var callback = AjxCallback.simpleClosure(this._showHideFields, this, "Contact");
	document.getElementById("sforce_contactOrLeadC").onclick = callback;

	var callback = AjxCallback.simpleClosure(this._showHideFields, this, "Lead");
	document.getElementById("sforce_contactOrLeadL").onclick = callback;

	var callback = AjxCallback.simpleClosure(this._createNewContactOrLeadClearLinkHdlr, this);
	document.getElementById("sforce_contactOrLeadAccClearLnk").onclick = callback;
};


Com_Zimbra_SForce.prototype._addAccountLookupBtn = function() {
		var btn = new DwtButton({parent:this._shell});
		btn.setText("Lookup");
		btn.setImage("Search");
		btn.addSelectionListener(new AjxListener(this, this._accountlookupBtnHdlr, [ btn]));
		document.getElementById("sforce_contactOrLeadAccLookupBtn").appendChild(btn.getHtmlElement());		
};

Com_Zimbra_SForce.prototype._accountlookupBtnHdlr = function() {
	this.sForceSearchDlg.setProperties("Account", "sforce_contactOrLeadAcc", null, "sforce_contactOrLeadAccClearDiv");
	this.sForceSearchDlg.displaySearchDialog();
};



Com_Zimbra_SForce.prototype._updateSelectMenuInNotesDlg =
function(objName, selectMenuId, props, response) {
	var props = props[0];
	var name = "";
	if(props.FirstName) {
		name = props.FirstName;
	}
	if(props.LastName) {
		name = [name, " ", props.LastName].join("");
	}
	if(props.Email) {
		name = [name, "(", props.Email, ")"].join("");
	}
	var id = response.id.__msh_content;
	var elSel = document.getElementById(selectMenuId);
	var elOptNew = document.createElement('option');
	elOptNew.text = [objName, "-", name].join("");
	elOptNew.value =  [objName, "_", id].join("");

	var elOptOld = elSel.options[0];  
	if(AjxEnv.isIE) {
		elSel.add(elOptNew, 0);
	} else {
		elSel.add(elOptNew, elOptOld);
	}
	elOptNew.selected = true;
	elOptNew.style.color = "blue";
	this.showInfo(["New ", objName, " [",name,"] has been added to the menu"].join(""));
};


Com_Zimbra_SForce.prototype._sfItemSelectionHandler = function(selectId, objSelectId) {
	var tmp = document.getElementById(selectId).value;
	var arry = tmp.split("_");
	var objName = arry[0];
	this.sForceSearchDlg.setProperties(objName, null, objSelectId);
	this.sForceSearchDlg.displaySearchDialog();
};

Com_Zimbra_SForce.prototype._addLookupButtons = function() {
		this._shell = this._shell;
		this.lookupBtnIdandBtnObjMap = {};
		for(var lookupBtnDivId in this._lookupBtnDivIdAndObjsMap) {
			var obj = this._lookupBtnDivIdAndObjsMap[lookupBtnDivId];
			var btn = new DwtButton({parent:this._shell});
			btn.setText("Lookup");
			btn.setImage("Search");
			this.lookupBtnIdandBtnObjMap[lookupBtnDivId] = btn;
			btn.addSelectionListener(new AjxListener(this, this._lookupBtnHdlr, [obj, btn]));
			document.getElementById(lookupBtnDivId).appendChild(btn.getHtmlElement());
		}
};

Com_Zimbra_SForce.prototype._lookupBtnHdlr = function(obj, btn) {
	var selectId = obj.selectId;
	var objName = this._selectidAndObjNameMap[selectId];
	this.sForceSearchDlg.setProperties(objName, null, selectId);
	var callback = new AjxCallback(this, this._associationMenuChangedHdlr, [btn]);
	this.sForceSearchDlg.setAssociationMenuCallback(callback);
	this.sForceSearchDlg.displaySearchDialog();
};

Com_Zimbra_SForce.prototype._associationMenuChangedHdlr = function(btn) {
	btn.setEnabled(false);
};


Com_Zimbra_SForce.prototype._cleanMainAccountsInfoDiv = function() {
	document.getElementById("SForce_mainAccountsInfoDiv").innerHTML = "";
};

Com_Zimbra_SForce.prototype._showNotesDlg = function(note) {
	var subject = AjxStringUtil.htmlEncode(note.subject);
	var body = AjxStringUtil.htmlEncode(this.getMailBodyAsText(note));

	if(this._addNotesDialog) {//if dialog already exists..
		this._setNotesDlgSubjectAndBody(subject, body);
		this._setNotesDlgAccountsDivAsLoading();
		if(!this._searchAllContacts) {
			this._hideAlertMsgForNotesDlg();
		}
		this._addNotesDialog.popup();
		return;
	}
	var view = new DwtComposite(this._shell);
	var el = view.getHtmlElement();
	var h3 = document.createElement("h3");

	var div = document.createElement("div");
	div = document.createElement("div");
	div.id = "SForce_MessageInfoDiv";
	div.className = "SForce_infoMsg";
	div.height = "14px";
	div.style.display = "none";
	el.appendChild(div);


	div = document.createElement("div");
	div.id = "SForce_mainAccountsInfoDiv";

	div.style.height = "290px";
	div.style.overflow = "auto";
	div.style.background = "white";

	el.appendChild(div);

	h3 = document.createElement("h3");
	h3.className = "SForce-sec-label";
	h3.innerHTML = "Note details";
	el.appendChild(h3);

	var div = document.createElement("div");


	div.innerHTML =
	[ "<table><tbody>",
		"<tr>",
		"<td align='right'><label for='sforce_notes_subjectField'>Subject:</td>",
		"<td>",
		"<input style='width:35em' type='text' id='sforce_notes_subjectField' value='",
		subject, "' autocomplete='off' />",
		"</td>",
		"</tr>",
		"<td colspan='2'>",
		"<textarea style='width:50em;height:110px' id='sforce_notes_MessageField'>",
		body, "</textarea>",
		"</td>",
		"<tr>",
		"</tr></tbody></table>" ].join("");
	el.appendChild(div);

	var dialog_args = {
		view  : view,
		title : "Adding note(s) to Salesforce"
	};
	this._addNotesDialog = this._createDialog(dialog_args);
	this._hideAlertMsgForNotesDlg();
	this._addNotesDialog.popup();

	el = document.getElementById("sforce_notes_subjectField");
	el.select();
	el.focus();
	this._setNotesDlgAccountsDivAsLoading();
	this._addNotesDialog.setButtonListener(DwtDialog.OK_BUTTON, new AjxListener(this, this._addNotesOKButtonListener, this._addNotesDialog));
	this._addNotesDialog.setButtonListener(DwtDialog.CANCEL_BUTTON, new AjxListener(this, this._addNotesCancelButtonListener, this._addNotesDialog));
};

Com_Zimbra_SForce.prototype._setNotesDlgSubjectAndBody = function(subject, body) {
	document.getElementById("sforce_notes_subjectField").value = AjxStringUtil.htmlDecode(subject);
	document.getElementById("sforce_notes_MessageField").value = AjxStringUtil.htmlDecode(body);
};

Com_Zimbra_SForce.prototype._setNotesDlgAccountsDivAsLoading = function() {
	document.getElementById("SForce_mainAccountsInfoDiv").innerHTML =  ["<br/> &nbsp;&nbsp;&nbsp;<img   src=\"", this.getResource("img/sf_busy.gif") , "\"  /> <b> Searching SalesForce..</b>"].join("");
};

Com_Zimbra_SForce.prototype._setAlertMsgForNotesDlg = function(msg) {
	document.getElementById("SForce_MessageInfoDiv").innerHTML =  ["<label style='font-weight:bold;font-size:12px;color:white;'>", msg, "</label>"].join("");
	document.getElementById("SForce_MessageInfoDiv").style.display = "block";
};

Com_Zimbra_SForce.prototype._hideAlertMsgForNotesDlg = function(msg) {
	document.getElementById("SForce_MessageInfoDiv").style.display = "none";
};

Com_Zimbra_SForce.prototype._addNotesOKButtonListener = function(dlg) {
	var ids = [];
	var indx = 0;
	var hasAtleastOneItem = false;
	var clMenuOptions = document.getElementById("sforce_contactLead_selectionMenu").options;
	var rtMenuOptions = document.getElementById("sforce_relatedTo_selectionMenu").options;
	

	for(var j =0; j < clMenuOptions.length; j++) {
		var clOption = clMenuOptions[j];
		var hasRelatedToItemForThisContact = false;
		if(clOption.selected) {
			var tmpArry =clOption.value.split("_");
			var whoId = tmpArry[1];
			//check if relatedTo is associated.. if so, add all of them one-by-one
			for(var k =0; k < rtMenuOptions.length; k++) {
				var rtOption = rtMenuOptions[k];
				if(rtOption.selected) {
					if(!ids[indx]) {
						ids[indx] = {};
					}
					var tmpArry = rtOption.value.split("_");
					var whatId = tmpArry[1];
					ids[indx].WhoId = whoId;
					ids[indx].WhatId =whatId;
					indx++;
					hasAtleastOneItem = true;
					hasRelatedToItemForThisContact = true;
				}
			}
			//if there is no relatedTo items, just add contacts
			if(!hasRelatedToItemForThisContact){
				if(!ids[indx]) {
					ids[indx] = {};
				}
				ids[indx].WhoId = whoId;
				hasAtleastOneItem = true;
				indx++;
			}
		}

	}
	//allow just selecting relatedTo(without contacts)
	if(!hasAtleastOneItem) {
		for(var k =0; k < rtMenuOptions.length; k++) {
			var rtOption = rtMenuOptions[k];
			if(clOption.selected) {
				if(!ids[indx]) {
					ids[indx] = {};
					indx++;
				}
				var tmpArry = rtOption.value.split("_");
				var whatId = tmpArry[1];
				ids[indx].WhatId =whatId;
				hasAtleastOneItem = true;
			}
		}
	}

	if (!hasAtleastOneItem) {
		this.displayErrorMessage("You must select at least one Item!");
	} else {
		var props = {
			Title : document.getElementById("sforce_notes_subjectField").value,
			Body  : document.getElementById("sforce_notes_MessageField").value
		};
		for (i = 0; i < ids.length; ++i) {
			ids[i].Subject = props.Title;
			ids[i].Description = props.Body;
			ids[i].Status = 'Completed';
			ids[i].ActivityDate = Com_Zimbra_SForce.toIsoDateTime(new Date());
		}
		this.createSFObject(ids, "Task", function() {
			this.displayStatusMessage("Saved " + ids.length + " notes.");
		});
		dlg.popdown();
	}
};

Com_Zimbra_SForce.prototype._addNotesCancelButtonListener = function(dlg) {
	dlg.popdown();
};

Com_Zimbra_SForce.prototype._setRecordsToNotesDlg = function(records) {
	var html = [];
	var i=0;
	var contactsSelectId = Dwt.getNextId();
	var relatedToSelectId = Dwt.getNextId();
	html[i++] = "<table class='SForce_table' width=100%>";
	html[i++] = "<tr><td><strong>Contact/Lead Name:</strong></td><td><strong>Related to:</strong></TD></TR>";

	html[i++] = "<tr>";
	html[i++] ="<td><select id='sforce_contactLead_selectionMenu' multiple size='10'>";
	for(var m = 0; m < records.length;m++) {
		var contact = records[m];
		var name = "";
		if(contact.Name) {
			name = contact.Name.toString();
		}
		var email = "";
		if(contact.Email) {
			email = contact.Email.toString();
		}
		var id = contact.Id.toString();		
		html[i++] = ["<option value='Contact_", id, "' selected>Contact - ", name," ", email, "</option>"].join("");
	}
	html[i++] ="</select></td>";
	html[i++] =["<td><select id='sforce_relatedTo_selectionMenu' multiple size='10'>"].join("");
	for(var m = 0; m < records.length; m++) {
		var contact = records[m];
		//add Account
		var account = contact.Account;
		if(account) {
			var id = account.Id.toString();
			var name = account.Name.toString();
			html[i++] = ["<option value='Account_", id, "'>Account: ", name, "</option>"].join("");			
		}
		//add opportunities
		var ocRoles = contact.OpportunityContactRoles;
		if(ocRoles) {

			var list = ocRoles.records;
			if(list && !(list instanceof Array)) {
				list = [list];
			}
			for(var n = 0; n < list.length; n++) {
				var item = list[n];
				var id = item.Opportunity.Id.toString();
				var name = item.Opportunity.Name.toString();
				var role = "";
				if(item.Role) {
					role = item.Role.toString();
				}
				html[i++] = ["<option value='Opportunity_", id, "' selected>Opportunity: ", name," ", role, "</option>"].join("");
			}
		}
		//add cases
		var cases = contact.Cases;
		if(cases) {
			var list = cases.records;
			if(list && !(list instanceof Array)) {
				list = [list];
			}
			for(var n = 0; n < list.length; n++) {
				var item = list[n];
				var id = item.Id.toString();
				var subject = item.Subject.toString();
				var caseNumber = item.CaseNumber.toString();
				html[i++] = ["<option value='Cases_", id, "'>Case: ", caseNumber," ", subject, "</option>"].join("");
			}
		}

	}
	html[i++] ="</select></td>";
	html[i++] ="</tr>";
	html[i++] = "<tr><td><strong>Lookup Contacts or Leads:</strong></td><td><strong>Lookup Related to Items:</strong></TD></TR>";
		
	html[i++] ="<tr><td><select id='sforce_contactLead_lookupMenu'>";
	html[i++] = "<option value='item_lookUp'>------------------ Lookup ------------------</option>";
	html[i++] = "<option value='Contact_lookUp'>Contact [lookup]</option>";
	html[i++] = "<option value='Lead_lookUp'>Lead [lookup]</option>";
	html[i++] = "</select></td>";

	html[i++] ="<td><select  id='sforce_relatedTo_lookupMenu'>";
	html[i++] = "<option value='item_lookUp'>------------------ Lookup ------------------</option>";
	html[i++] = "<option value='Account_lookUp'>Account [lookup]</option>";
	html[i++] = "<option value='Asset_lookUp'>Asset [lookup] </option>";
	html[i++] = "<option value='Campaign_lookUp'>Campaign [lookup] </option>";
	html[i++] = "<option value='Case_lookUp'>Case [lookup] </option>";
	html[i++] = "<option value='Contract_lookUp'>Contract [lookup] </option>";
	html[i++] = "<option value='Opportunity_lookUp'>Opportunity [lookup] </option>";
	html[i++] = "<option value='Product_lookUp'>Product [lookup] </option>";
	html[i++] = "<option value='Solution_lookUp'>Solution [lookup] </option>";
	html[i++] = "</select></td>";
	html[i++] ="</tr>";
	html[i++] ="<tr><td>Quick create: <a href=# id='sforce_quickCreateContactLnk'>Contact</a> or <a href=# id='sforce_quickCreateLeadLnk'>Lead</a></td></tr>";
	/*
	html[i++] = "<tr><td colspan=2><strong>Create New contacts or leads:</strong></td></TR>";
	html[i++] ="<tr><td colspan=2><select id='sforce_contactLead_createMenu'>";
	html[i++] = "<option value='item_Create'>------------------ Create ------------------</option>";
	html[i++] = "<option value='Contact_Create'>Create New Contact </option>";
	html[i++] = "<option value='Lead_Create'>Create New Lead </option>";
	html[i++] = "</select></td>";
	html[i++] ="</tr>";
	*/
	html[i++] = "</table>";
	document.getElementById("SForce_mainAccountsInfoDiv").innerHTML = html.join("");
};
//--------------------------------------------------------------------------------------------------------
//Notes dropped (end)
//--------------------------------------------------------------------------------------------------------



//--------------------------------------------------------------------------------------------------------
// Toolbar related..(START)
//--------------------------------------------------------------------------------------------------------
Com_Zimbra_SForce.prototype.initializeToolbar = function(app, toolbar, controller, viewId) {
	if(this.sforce_logindlg_showSendAndAddBtn == undefined) {
		this.sforce_logindlg_showSendAndAddBtn = this.getUserProperty("sforce_logindlg_showSendAndAddBtn") == "true";
	}

	if (viewId.indexOf("COMPOSE") >= 0 && this.sforce_logindlg_showSendAndAddBtn) {
		this._initComposeSFToolbar(toolbar, controller);
	}
};

Com_Zimbra_SForce.prototype._initContactSFToolbar = function(toolbar, controller) {
	if (!toolbar.getButton(Com_Zimbra_SForce.SFORCE_CONTACT_TB_BTN)) {
		//get the index of View menu so we can display it after that.
		var buttonIndex = -1;
		for (var i = 0, count = toolbar.opList.length; i < count; i++) {
			if (toolbar.opList[i] == ZmOperation.VIEW_MENU) {
				buttonIndex = i + 1;
				break;
			}
		}
		if(buttonIndex == -1) {
			buttonIndex = i+1;
		}
		ZmMsg.salesforce = "Sync with Salesforce";
		ZmMsg.sforceMailTooltip = "Syncs a contact with salesforce";
		var btn = toolbar.createOp(Com_Zimbra_SForce.SFORCE, {text:ZmMsg.salesforce, tooltip:ZmMsg.sforceMailTooltip, index:buttonIndex, image:"SFORCE-panelIcon"});
		toolbar.addOp(Com_Zimbra_SForce.SFORCE_CONTACT_TB_BTN, buttonIndex);
		btn.addSelectionListener(new AjxListener(this, this._sfContactTbButtonHdlr, controller));
	}
};

Com_Zimbra_SForce.prototype._sfContactTbButtonHdlr = function(controller) {
	var contact = controller._listView[controller._currentView].getSelection()[0];
	this.contactDropped(contact);//should really show a dialog with two sections to sync
	
};

Com_Zimbra_SForce.prototype._initComposeSFToolbar = function(toolbar, controller) {
	if (!toolbar.getButton(Com_Zimbra_SForce.SFORCE)) {
		ZmMsg.sforceAdd = "Send & Add";
		ZmMsg.sforceTooltip = "Send and add to Salesforce.";
		var btn = toolbar.createOp(Com_Zimbra_SForce.SFORCE, {text:ZmMsg.sforceAdd, tooltip:ZmMsg.sforceTooltip, index:1, image:"SFORCE-panelIcon"});
		toolbar.addOp(Com_Zimbra_SForce.SFORCE, 2);
		this._composerCtrl = controller;
		this._composerCtrl._sforce = this;
		btn.addSelectionListener(new AjxListener(this._composerCtrl, this._sendAddSForce));
	}
};

Com_Zimbra_SForce.prototype._sendAddSForce = function(ev) {
	var msg = this._composeView.getMsg();
	this._send();
	this._sforce.noteDropped(msg);
};
//--------------------------------------------------------------------------------------------------------
// Toolbar related..(END)
//--------------------------------------------------------------------------------------------------------


//--------------------------------------------------------------------------------------------------------
// Salesforce AJAX functionalities..(START)
//--------------------------------------------------------------------------------------------------------
/// Store the default SOAP server.  Note that after a successful login, the URL
/// may change--which is why we store it in an object instance too (this.SERVER)
Com_Zimbra_SForce.LOGIN_SERVER = "https://www.salesforce.com/services/Soap/c/17.0";

Com_Zimbra_SForce._RECENT = {};

// SOAP utils

/// Utility function that creates a SOAP envelope.  This will also insert the
/// session header if we already have a session.
Com_Zimbra_SForce.prototype._makeEnvelope = function(method, limit, dontUseSessionId) {
	var soap = AjxSoapDoc.create(
			method, this.XMLNS, null,
			"http://schemas.xmlsoap.org/soap/envelope/");
	var envEl = soap.getDoc().firstChild;
	// Seems we need to set these or otherwise will get a "VersionMismatch"
	// message from SForce
	envEl.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
	envEl.setAttribute("xmlns:xsd", "http://www.w3.org/2001/XMLSchema");

	if (this.sessionId && !dontUseSessionId) {
		var header = soap.ensureHeader();
		if(limit) {
			var qo =  soap.getDoc().createElement("sh:QueryOptions");
			qo.setAttribute("xmlns:sh", "SoapService");
			qo.setAttribute("soap:mustUnderstand", "0");
			header.appendChild(qo);

			var el = soap.getDoc().createElement("sh:batchSize");
			el.setAttribute("xmlns:sh", this.XMLNS);
			el.appendChild(soap.getDoc().createTextNode(200));//always use 200
			qo.appendChild(el);			 
		}
		var shEl = soap.getDoc().createElement("sh:SessionHeader");
		shEl.setAttribute("xmlns:sh", this.XMLNS);
		header.appendChild(shEl);

		var sessionEl = soap.getDoc().createElement("sh:sessionId");
		sessionEl.setAttribute("xsi:type", "xsd:string");
		shEl.appendChild(sessionEl);

		var txtEl = soap.getDoc().createTextNode(this.sessionId);
		sessionEl.appendChild(txtEl);

	}
	return soap;
};

Com_Zimbra_SForce.prototype.xmlToObject = function(result, dontConvertToJSObj) {
	try {
		if(dontConvertToJSObj) {
			var xd = new AjxXmlDoc.createFromDom(result.xml);
		} else {
			var xd = new AjxXmlDoc.createFromDom(result.xml).toJSObject(true, false);
		}
	} catch(ex) {
		this.displayErrorMessage(ex, result.text, "Problem contacting Salesforce");
	}
	return xd;
};

/// Utility function that calls the SForce server with the given SOAP data
Com_Zimbra_SForce.prototype.rpc = function(soap, callback, passErrors) {
	this.sendRequest(soap, this.SERVER, {SOAPAction: "m", "Content-Type": "text/xml"}, callback, false, passErrors);
};

Com_Zimbra_SForce.prototype.logout = 
function() {
	var soap = this._makeEnvelope("logout");

	this.rpc(soap, new AjxCallback(this, this.done_logout), true);
};

Com_Zimbra_SForce.prototype.done_logout = 
function(response) {
	appCtxt.setStatusMsg("Logged out of Salesforce", ZmStatusView.LEVEL_INFO);
};
// SOAP METHOD: login

/// Login to SForce.  The given callback will be called in the case of a
/// successful login.  Note that callback is a plain function (not AjxCallback)
Com_Zimbra_SForce.prototype.login = function(callback, user, passwd, dontUseSessionId) {
	if (!callback) {
		callback = false;
	}
	if(!user || !passwd) {
		user = this.getUserProperty("user");
		passwd = this.getUserProperty("passwd");
		this.sforce_ignoreDomainsList = this.getUserProperty("sforce_ignoreDomainsList");
		this.sforce_logindlg_sbarShowOnlyOnResult =  this.getUserProperty("sforce_logindlg_sbarShowOnlyOnResult") == "true";
		this.sforce_logindlg_showSendAndAddBtn = this.getUserProperty("sforce_logindlg_showSendAndAddBtn") == "true";
	}
	if (!user || !passwd || user == "" || passwd == "") {
		var errMsg = "Please fill your Salesforce credentials";
		this._displayLoginDialog(callback, errMsg);
	} else {
		this._do_login(callback, user, passwd, dontUseSessionId);
	}
};

Com_Zimbra_SForce.prototype._do_login = function(callback, user, passwd, dontUseSessionId) {
	this.SFuserName = user;//store username

	var soap = this._makeEnvelope("login", null, dontUseSessionId);
	soap.set("username", user);
	soap.set("password", passwd);
	if (callback == null){
		callback = false;
	}
	this.rpc(soap, new AjxCallback(this, this.done_login, [ callback ]), true);
};

Com_Zimbra_SForce.prototype.done_login = function(callback, result) {
	var ans = this.xmlToObject(result);
	if (ans && ans.Body && ans.Body.loginResponse) {
		ans = ans.Body.loginResponse.result;
		this.SERVER = String(ans.serverUrl);
		this.sessionId = String(ans.sessionId);
		this.userId = String(ans.userId);
		this.userInfo = ans.userInfo;
		if(this.loginDlg) {//popdown login dialog on successful login
			this.loginDlg.popdown();
		}
		if(!this._loginTimerStarted) {
			appCtxt.setStatusMsg("Logged on to salesforce as "+this.userInfo.userFullName.toString(), ZmStatusView.LEVEL_INFO);
			//login every 10 minutes to keep the session alive
			setInterval(AjxCallback.simpleClosure(this.login, this, (function() {}), null, null, true), 60*1000*5);
			this._loginTimerStarted = true;
		}

		if(callback instanceof AjxCallback) {
			callback.run(this);
		} else if (callback) {
			callback.call(this);
		}
	} else {
		var fault = "";
		if (ans && ans.Body && ans.Body.Fault && ans.Body.Fault.faultstring) {
			fault = ans.Body.Fault.faultstring + "<br />";
		}
		var errorMsg = ["<b>Login to Salesforce failed!</b><br />&nbsp;&nbsp;&nbsp;&nbsp;", fault , "<br />Check your internet connection and review your preferences."].join("");
		this._displayLoginDialog(callback, errorMsg);

	}
};




Com_Zimbra_SForce.prototype.queryMore = function(queryLocator, limit, callback, returnEntireResponse) {
	if (!this.sessionId) {
		this.login(function() {
			this._do_queryMore(queryLocator, limit, callback, returnEntireResponse);
		});
	} else {
		this._do_queryMore(queryLocator, limit, callback, returnEntireResponse);
	}
};

Com_Zimbra_SForce.prototype._do_queryMore = function(queryLocator, limit, callback, returnEntireResponse) {
	if (!limit || limit < 1) {
		limit = 1;
	}
	var soap = this._makeEnvelope("queryMore", limit);
	var doc = soap.getDoc();
	var query = soap.set("queryLocator", queryLocator);
	query.setAttribute("xmlns",  this.XMLNS);
	// we sure have a lot of indirection going on..
	this.rpc(soap, new AjxCallback(this, this.done_queryMore, [ callback, returnEntireResponse ]));
};

Com_Zimbra_SForce.prototype.done_queryMore = function(callback, returnEntireResponse, result) {
	var xd = this.xmlToObject(result);
	var resultObj = xd.Body.queryMoreResponse.result;
	this._parseResultObjAndCallback(resultObj, callback, returnEntireResponse);
};


Com_Zimbra_SForce.prototype.search = function(query, limit, callback, returnEntireResponse, returnResultAsXML) {
	if (!this.sessionId) {
		this.login(function() {
			this._do_search(query, limit, callback, returnEntireResponse, returnResultAsXML);
		});
	} else {
		this._do_search(query, limit, callback, returnEntireResponse, returnResultAsXML);
	}
};

Com_Zimbra_SForce.prototype._do_search = function(query, limit, callback, returnEntireResponse, returnResultAsXML) {
	if (!limit || limit < 1) {
		limit = 1;
	}
	var soap = this._makeEnvelope("search", limit);
	var doc = soap.getDoc();
	var query = soap.set("queryString", query);
	query.setAttribute("xmlns:sh",  this.XMLNS);
	// we sure have a lot of indirection going on..
	this.rpc(soap, new AjxCallback(this, this.done_search, [ callback, returnEntireResponse, returnResultAsXML ]));
};

Com_Zimbra_SForce.prototype.done_search = function(callback, returnEntireResponse, returnResultAsXML, result) {
	if(returnResultAsXML) {
		var xd = this.xmlToObject(result, true);
		var resultObj = xd;
	} else {
		var xd = this.xmlToObject(result);
		var resultObj = xd.Body.searchResponse.result;
	}
	this._parseResultObjAndCallback(resultObj, callback, returnEntireResponse);
};


// SOAP METHOD: query

/// Executes a SOQL (SalesForce Object Query Language) and calls the given
/// callback upon successful execution.
Com_Zimbra_SForce.prototype.query = function(query, limit, callback, returnEntireResponse, errorCallback) {
	if(!errorCallback) {
		var errorCallback = new AjxCallback(this, this._generalQueryErrorHdlr);
	}
	if (!this.sessionId) {
		this.login(function() {
			this._do_query(query, limit, callback, returnEntireResponse, errorCallback);
		});
	} else {
		this._do_query(query, limit, callback, returnEntireResponse, errorCallback);
	}
};

Com_Zimbra_SForce.prototype._generalQueryErrorHdlr = function() {
	var response =  arguments[1];
	if(response) {
		if(response.text.indexOf("INVALID_SESSION_ID") >=0) {
			appCtxt.getAppController().setStatusMsg("Salesforce session had expired. Please try again", ZmStatusView.LEVEL_WARNING);
			this.login(function(){}, null, null, true);
		}
	}
};


Com_Zimbra_SForce.prototype._do_query = function(query, limit, callback, returnEntireResponse, errorCallback) {
	if (!limit || limit < 1) {
		limit = 1;
	}
	var soap = this._makeEnvelope("query", limit);
	var doc = soap.getDoc();
	var query = soap.set("queryString", query);
	query.setAttribute("xmlns:sh",  this.XMLNS);
	// we sure have a lot of indirection going on..
	this.rpc(soap, new AjxCallback(this, this.done_query, [ callback, returnEntireResponse, errorCallback ]), true);
};


Com_Zimbra_SForce.__query_result_get = function() {
	for (var i = 0; i < arguments.length; ++i) {
		var attr = arguments[i];
		if (this[attr] != null) {
			return this[attr].toString();
		}
	}
	return "";
};

Com_Zimbra_SForce.prototype.done_objQuery = function(callback, result) {
	var xd = this.xmlToObject(result);
	callback.call(this,  xd.Body.describeSObjectResponse.result);
};

Com_Zimbra_SForce.prototype.done_query = function(callback, returnEntireResponse, errorCallback, result) {
	if(!result.success) {
		if(errorCallback) {
			if(errorCallback instanceof AjxCallback) {
				errorCallback.run(this, result);
			}else {
				errorCallback.call(this, result);
			}
		} else {
			this.displayErrorMessage("An error was returned.<br />Error code: " + result.status, result.text);	
		}
		return;
	}
	var xd = this.xmlToObject(result);
	var resultObj = xd.Body.queryResponse.result;
	this._parseResultObjAndCallback(resultObj, callback, returnEntireResponse);
};

Com_Zimbra_SForce.prototype._parseResultObjAndCallback = function(resultObj, callback, returnEntireResponse) {
	//if returnEntireResponse Object is true..
	if(returnEntireResponse) {
		if(callback instanceof AjxCallback) {
			callback.run(this, resultObj);
		}else {
			callback.call(this, resultObj);
		}
		return;
	}	


	var qr = resultObj.records;
	if (qr != null) {
		if (!(qr instanceof Array))
			qr = [ qr ];
		// sometimes SForce returns a duplicate <Id> tag
		for (var i = qr.length; --i >= 0;) {
			if (qr[i].Id && (qr[i].Id instanceof Array))
				qr[i].Id = qr[i].Id[0];
			qr[i].get = Com_Zimbra_SForce.__query_result_get;
		}
	} else {
		qr = [];
	}
	if(callback instanceof AjxCallback) {
		callback.run(this, qr);
	}else {
		callback.call(this, qr);
	}
};


// SOAP METHOD: create

Com_Zimbra_SForce.prototype.createSFObject = function(props, type, callback, returnEntireResponse) {
	if (!callback) {
		callback = false;
	}
	// make sure we are logged in first
	if (!this.sessionId)
		this.login(function() {
			this._actOnSFObject("create", props, type, callback, returnEntireResponse);
		});
	else
		this._actOnSFObject("create", props, type, callback, returnEntireResponse);
};

Com_Zimbra_SForce.prototype.updateSFObject = function(props, type, callback, returnEntireResponse) {
	if (!callback) {
		callback = false;
	}
	// make sure we are logged in first
	if (!this.sessionId)
		this.login(function() {
			this._actOnSFObject("update", props, type, callback, returnEntireResponse);
		});
	else
		this._actOnSFObject("update", props, type, callback, returnEntireResponse);
};

Com_Zimbra_SForce.prototype.deleteSFObject = function(props, type, callback, returnEntireResponse) {
	if (!callback) {
		callback = false;
	}
	// make sure we are logged in first
	if (!this.sessionId)
		this.login(function() {
			this._actOnSFObject("delete", props, type, callback, returnEntireResponse);
		});
	else
		this._actOnSFObject("delete", props, type, callback, returnEntireResponse);
};


Com_Zimbra_SForce.prototype._actOnSFObject = function(action, props, type, callback, returnEntireResponse) {
	if (!callback) {
		callback = false;
	}
	var soap = this._makeEnvelope(action);
	var a = props;
	if (!(a instanceof Array)) {
		a = [ a ];
	}
	if(action ==  "delete") {
		for (var j = 0; j < a.length; ++j) {
			props = a[j];
			for (var i in props) {
				soap.set("Ids", props[i]);
			}
		}
	} else {
		for (var j = 0; j < a.length; ++j) {
			var createData = {};
			props = a[j];
			for (var i in props) {
				if (props[i] != null && props[i] != "undefined" &&  i != "undefined") {
					if (i.indexOf(":") == -1)
						createData["ns3:" + i] = props[i];
					else
						createData[i] = props[i];
				}
			}
			var el = soap.set("sObjects", createData);	

			el.setAttribute("xsi:type", "ns3:" + type);
			el.setAttribute("xmlns:ns3", this.XMLNS);
		}
	}
	if(action == "create") {
		var respCallback	= new AjxCallback(this, this.done_createSFObject, [ callback, returnEntireResponse ]);
	} else if(action == "update") {
		var respCallback =  new AjxCallback(this, this.done_updateSFObject, [ callback,returnEntireResponse ]);
	} else if(action == "delete") {
		var respCallback =  new AjxCallback(this, this.done_deleteSFObject, [ callback,returnEntireResponse ]);
	}
	
	this.rpc(soap, respCallback);
};

Com_Zimbra_SForce.prototype.done_createSFObject = function(callback, returnEntireResponse, result) {
	var xd = this.xmlToObject(result);
	if (xd && callback) {
		result = xd.Body.createResponse.result;
		if(returnEntireResponse) {//returnEnhtireResult Object
			callback.call(this, result);
			return;
		}
		var id;
		if (result instanceof Array) {
			id = [];
			for (var i = 0; i < result.length; ++i)
				id.push(result[i].id.toString());
		} else {
			id = result.id.toString();
		}
		callback.call(this, id);
	}
};



Com_Zimbra_SForce.prototype.done_updateSFObject = function(callback, returnEntireResponse, result) {
	var xd = this.xmlToObject(result);
	if (xd && callback) {
		result = xd.Body.updateResponse.result;
		if(returnEntireResponse) {//returnEnhtireResult Object
			callback.call(this, result);
			return;
		}
		if(result.success) {
			callback.call(this, result.success.toString());
		} else {
			callback.call(this, "false");
		}

	}
};
Com_Zimbra_SForce.prototype.done_deleteSFObject = function(callback, returnEntireResponse, result) {
	var xd = this.xmlToObject(result);
	if (xd && callback) {
		result = xd.Body.deleteResponse.result;
		if(returnEntireResponse) {//returnEnhtireResult Object
			callback.call(this, result);
			return;
		}
		if(result.success) {
			callback.call(this, result.success.toString());
		} else {
			callback.call(this, "false");
		}

	}
};
//--------------------------------------------------------------------------------------------------------
// Salesforce AJAX functionalities..(END)
//--------------------------------------------------------------------------------------------------------


//--------------------------------------------------------------------------------------------------------
// Contacts dropped..(START)
//--------------------------------------------------------------------------------------------------------
/// Called when a new contact has been dropped onto the Zimlet panel item, this
/// function will analyze data and take appropriate actions to insert a new
/// contact.
Com_Zimbra_SForce.prototype.contactDropped = function(contact) {
	// Note that since all communication is required to be asynchronous,
	// the only way we can write this function is using a series of
	// callbacks.  The main entry point is when we call this.query(...),
	// but then execution will vary depending on the response.

	var acct_Website = "";

	// augment contact with a helper function
	contact.get = Com_Zimbra_SForce.__query_result_get;

	// this is called after a successful query that should retrieve a
	// matching account (company).
	function $search_acct(records) {
		//Search for an account
		if (records.length > 0) {
			// we found a matching account
			this.dlg_createAccount(records[0], contact);
		} else {
			var props = {
				Name     : contact.get("company"),
				Website  : acct_Website,
				Phone    : contact.get("workPhone"),

				// utility function
				get      : Com_Zimbra_SForce.__query_result_get
			};
			this.dlg_createAccount(props, contact);
		}
	}

	function $search_acct_company(records) {
		if (records.length > 0) {
			$search_acct.call(this, records);
		} else {
			var q = "select Id, Website, Name, Phone from Account where Name like '" + contact.company + "%'";
			this.query(q, 1, $search_acct_email);
		}
	}

	function $search_acct_email(records) {
		if (records.length > 0) {
			$search_acct.call(this, records)
		} else {
			var email = contact.email || contact.email2 || contact.email3;
			acct_Website = email.replace(/^[^@]+@/, "").replace(/\x27/, "\\'");
			var q = "select Id, Website, Name, Phone from Account where Website like '%" + acct_Website + "'";
			this.query(q, 1, $search_acct);
		}
	}

	function $search_contact(records) {
		//Search Contact
		contact._exists = false;
		if (records.length > 0) {
			//Contact already present in Sales Force
			contact._exists = true;
			if (records[0].AccountId && records[0].AccountId != "") {
				contact.AccountId = records[0].AccountId;
				contact.Id = records[0].Id;
				var q = "select Id, Website, Name, Phone from Account where Id='" + contact.AccountId + "'";
				this.query(q, 1, $search_acct);
				return;
			}
		}
		///New Contact
		//Search for an account that matches this contact
		if (contact.company) {
			//Searching for the Account associated with this account
			var q = "select Id, Website, Name, Phone from Account where Name='" + contact.company + "'";
			this.query(q, 1, $search_acct_company);
		} else if (contact.email || contact.email2 || contact.email3) {
			//Searching for the Account that has the website like contact website.
			$search_acct_email.call(this, []);
		} else {
			//Just go ahead
			$search_acct.call(this, []);
		}
	}

	//Search for a contact
	if (contact.email || contact.email2 || contact.email3) {
		///Serach contacts with the first primary email address
		///Need to extend the query to OR every email address
		var email = contact.email || contact.email2 || contact.email3 ;
		var q = [ "select Id, FirstName, LastName, Email, AccountId from Contact where Email like '",
			contact.email,"'"].join("");
		this.query(q, 1, $search_contact);
	} else if (contact.company) {
		$search_contact.call(this, []);
	} else {
		// clearly we can't search for a matching account, so let's
		// create one
		this.dlg_createAccount({ get: Com_Zimbra_SForce.__query_result_get }, contact);
	}

};

Com_Zimbra_SForce.prototype.dlg_createAccount = function(acct_data, contact_data) {
	var view = new DwtComposite(this._shell);

	///Disable fieldsEditable if contact already exists
	var fieldsEditable = !(contact_data._exists);

	/// Create a PropertyEditor for the Account data
	var pe_acct = new DwtPropertyEditor(view, fieldsEditable);
	var pe_props = [

		{
			label    : "Account Name",
			name     : "Name",
			type     : "string",
			value    : acct_data.get("Name"),
			required : true
		},

		{
			label    : "Website",
			name     : "Website",
			type     : "string",
			value    : acct_data.get("Website")
		},

		{
			label    : "Phone",
			name     : "Phone",
			type     : "string",
			value    : acct_data.get("Phone")
		}
	];

	if (acct_data.Id) {
		var tmp = [

			{
				label     : "Use existing account?",
				name      : "_reuse",
				type      : "enum",
				value     : "yes",
				item      : [
					{
						label : "Yes",
						value : "yes"
					},
					{
						label : "No, create a new one",
						value : "no"
					}
				]
			},

			{
				label     : "Account Id",
				name      : "Id",
				readonly  : true,
				value     : acct_data.get("Id"),
				type      : "string",
				visible   : false
			}

		];
		pe_props = contact_data._exists
				? pe_props.unshift(tmp[1])
				: pe_props.unshift(tmp[0], tmp[1]);
	}

	///Do not display for any contact without a corresponding account.
	if (!(contact_data._exists && !acct_data.Id)) {
		pe_acct.initProperties(pe_props);
	}

	var dialogTitle = contact_data._exists
			? (acct_data.Id ? "Account/Contact in Salesforce" : "Contact in Salesforce")
			: "Create Account/Contact in Salesforce";
	var dialog_args = {};
	///Displaying static content needs only OK button.
	if (contact_data._exists) {
		dialog_args = {title : dialogTitle,view  : view,standardButtons : [DwtDialog.OK_BUTTON]};
	} else {
		dialog_args = {title : dialogTitle, view  : view};
	}

	var tmp = document.createElement("h3");
	tmp.className = "SForce-sec-label SForce-icon-right";
	DBG.println(AjxDebug.DBG3, "Contact Exists-" + contact_data._exists);
	tmp.innerHTML = contact_data._exists
			? "Contact already exists"
			: "Add to a new account";
	var el = pe_acct.getHtmlElement();
	el.parentNode.insertBefore(tmp, el);

	/// Create a PropertyEditor for the new contact data
	pe_contact = new DwtPropertyEditor(view, fieldsEditable);

	pe_props = [

		{
			label    : "First name",
			name     : "FirstName",
			type     : "string",
			value    : contact_data.get("firstName")
		},

		{
			label    : "Last name",
			name     : "LastName",
			type     : "string",
			value    : contact_data.get("lastName"),
			required : true
		},

		{
			label    : "Title",
			name     : "Title",
			type     : "string",
			value    : contact_data.get("jobTitle")
		},

		{
			label    : "Email",
			name     : "Email",
			type     : "string",
			value    : contact_data.get("email", "email2", "email3")
		},

		{
			label    : "Work Phone",
			name     : "Phone",
			type     : "string",
			value    : contact_data.get("workPhone")
		},

		{
			label    : "Other Phone",
			name	   : "OtherPhone",
			type	   : "string",
			value	   : contact_data.get("workPhone2")
		},

		{
			label	   : "Mobile",
			name     : "MobilePhone",
			type	   : "string",
			value    : contact_data.get("mobilePhone")
		}
	];
	pe_contact.initProperties(pe_props);

	if (!(contact_data._exists && !acct_data.Id)) {
		tmp = document.createElement("h3");
		tmp.className = "SForce-sec-label";
		tmp.innerHTML = contact_data._exists ? "Contact info" : "New contact info";
		el = pe_contact.getHtmlElement();
		el.parentNode.insertBefore(tmp, el);
	}

	var dlg = this._createDialog(dialog_args);
	pe_acct.setFixedLabelWidth();
	pe_acct.setFixedFieldWidth();
	pe_contact.setFixedLabelWidth(pe_acct.maxLabelWidth);
	pe_contact.setFixedFieldWidth();
	dlg.popup();

	// handle some events

	dlg.setButtonListener(
			DwtDialog.OK_BUTTON,
			new AjxListener(this, function() {
				///If its only static information then its just a simple OK button
				if (contact_data._exists) {
					dlg.popdown();
					dlg.dispose();
					return;
				}
				if (!( pe_acct.validateData() && pe_contact.validateData() ))
					return;
				var acct = pe_acct.getProperties();
				var contact = pe_contact.getProperties();

				function $create_contact() {
					this.createSFObject(contact, "Contact", function(id) {
						var name = contact.LastName || contact.FirstName || contact.Email || id;
						this.displayStatusMessage("SForce contact saved: " + name);
					});
				}

				;
				////
				// TODO: we should have some checking going on here
				////
				if (acct.Id && acct._reuse == "yes") {
					contact.AccountId = acct.Id;
					$create_contact.call(this);
				} else {
					delete acct.Id;
					this.createSFObject(acct, "Account", function(id) {
						var name = acct.Name || contact.Website || id;
						this.displayStatusMessage("SForce account created: " + name);
						contact.AccountId = id;
						$create_contact.call(this);
					});
				}
				dlg.popdown();
				dlg.dispose();
			}));

	// We don't really want to mess with things like cache-ing this
	// dialog...
	if (!contact_data._exists) {
		dlg.setButtonListener(
				DwtDialog.CANCEL_BUTTON,
				new AjxListener(this, function() {
					dlg.popdown();
					dlg.dispose();
				}));
	}
};
//--------------------------------------------------------------------------------------------------------
// Contacts dropped..(END)
//--------------------------------------------------------------------------------------------------------


//--------------------------------------------------------------------------------------------------------
// Appointment dropped..(START)
//--------------------------------------------------------------------------------------------------------
Com_Zimbra_SForce.prototype.apptDropped = function(obj) {
	var appt = {
		ActivityDate      : Com_Zimbra_SForce.toIsoDate(obj.startDate),
		ActivityDateTime  : Com_Zimbra_SForce.toIsoDateTime(obj.startDate),
		DurationInMinutes : Math.round((obj.endDate.getTime() - obj.startDate.getTime()) / 60000),
		Description       : obj.notes,
		Subject           : obj.subject,
		// we need to reverse engineer the salesforce SOAP API first :-\
		// the official docs. are almost useless.
		// Type              : "Meeting", // obj.type is always null
		Location          : obj.location
	};
	DBG.dumpObj(appt);
	this.createSFObject(appt, "Event", function(id) {
		this.displayStatusMessage("New event registered at Salesforce");
	});
};

// rec - The record to generate checkbox HTML for
// cbid - DWT id of this new check box
// indent - 0 | 1 | 2, amount to indent
// checked - default the checkbox to checked?
// html - array to append html too
Com_Zimbra_SForce.prototype._checkBoxHtml = function(rec, cbid, indent, checked, html) {
	this._dwtIdAndObjType[cbid] = rec.TYPE;
	if (!this._oddRow) {
		this._oddRow = true;
	} else {
		this._oddRow = false;
	}

	html.push("<tr><td>");
	if (rec.TYPE == "A") {
		html.push("<div class='SForce_commentRow'>");
	} else {
		if (this._oddRow) {
			html.push("<div class='RowOdd'>");
		} else {
			html.push("<div class='RowEven'>");
		}
	}
	html.push("<table width=100% cellspacing=2px cellpadding=2px><tr>");

	if (rec.TYPE == "A") {
		html.push("<td width=16px>");
	} else {
		html.push("<td width=16px height=16px></td><td width=16px>");
	}
	html.push("<input type='checkbox' value='", rec.Id,
			"' id='", cbid);

	if (checked) {
		html.push("' checked='checked'/>");
	} else {
		html.push("' />");
	}

	html.push("</td>");

	if (rec.TYPE == "A") {
		html.push("<td width=16px><div class=\"ImgSFORCE-panelIcon\"/></td>");
	} else if (rec.TYPE == "C") {
		html.push("<td width=16px><div class=\"ImgContact\"/></td>");
	} else if (rec.TYPE == "O") {
		html.push("<td width=16px><div class=\"ImgSendReceive\"/></td>");
	}

	html.push("<td>");
	if (rec.TYPE == "A") {
		html.push("<label style='font-weight:bold;font-size:12px;color:blue;' id='", cbid, "_label'>");
	} else {
		html.push("<label style='font-size:12px' id='", cbid, "_label'>");
	}


	if (rec.TYPE)
		html.push(rec.TYPE + ":");

	if (rec.Name)
		html.push(" " + rec.Name);

	if (rec.FirstName)
		html.push(" " + rec.FirstName);

	if (rec.LastName)
		html.push(" " + rec.LastName);

	html.push("</label>");
	html.push("<label style='color:gray;' >");

	if (rec.Email)
		html.push(" " + rec.Email);

	if (rec.Website)
		html.push(" " + rec.Website);

	if (rec.Phone)
		html.push(" " + rec.Phone);

	if (rec.TYPE == "A") {
		html.push("</label></td><td align=right><a href=# id='", cbid, "_options'>Account options</a></td></tr></table></div></td></tr>");
		this._optionsLinkArray.push(cbid + "_options");
	} else {
		html.push("</label></td></tr></table></div></td></tr>");
	}

	return html;
};

Com_Zimbra_SForce.prototype.search_contact =
function (records) {
	var contacts = [];
	if (records.length == 0) {
		this._asst._setField("Contact", "No Match", false, true);
	} else if (records.length == 1) {
		var email = Com_Zimbra_SForce.display_contact(records[0]);
		DBG.println(AjxDebug.DBG3, "single match: " + email);
		this._contactEmail = records[0].Email;
		this._parentId = records[0].Id.__msh_content;
		this._asst._setField("Contact", email, false);
	} else {
		// Limit the number of matches shown to 10
		var displayLimit = records.length;
		if (displayLimit > 10) {
			displayLimit = 10;
			DBG.println(AjxDebug.DBG3, "Setting limit to 10 returned " + records.length);
		}
		for (var i = 0; i < displayLimit; ++i) {
			var email = Com_Zimbra_SForce.display_contact(records[i]);
			contacts.push(email);
			DBG.println(AjxDebug.DBG3, "multi match: " + email);
		}
		contacts = contacts.join("<br/>");
		DBG.println(AjxDebug.DBG3, "search_contact this: " + this);
		this._asst._setField("Contact", contacts, false);
	}
};

Com_Zimbra_SForce.display_contact =
function (contact) {
	var ret = "";
	if (contact.FirstName) {
		ret = ret + contact.FirstName;
	}
	if (contact.LastName) {
		ret = ret + " " + contact.LastName;
	}
	if (contact.Email) {
		ret = ret + " (" + contact.Email + ")";
	}
	return ret;
};

//--------------------------------------------------------------------------------------------------------
// Appointment dropped..(END)
//--------------------------------------------------------------------------------------------------------

//--------------------------------------------------------------------------------------------------------
//Misc/Supporting functions (START)
//--------------------------------------------------------------------------------------------------------
Com_Zimbra_SForce.prototype.showWarningDlg =
function(msg) {
	var dlg = appCtxt.getMsgDialog();
	dlg.reset();//reset dialog since we could be using it
	dlg.setMessage(msg, DwtMessageDialog.WARNING_STYLE);
	dlg.popup();
};

Com_Zimbra_SForce.arrayContainsElement =
function(array, val) {
	for (var i = 0; i < array.length; i++) {
		if (array[i] == val) {
			return true;
		}
	}
	return false;
}

function sforce_unique(b) {
	var a = [], i, l = b.length;
	for (i = 0; i < l; i++) {
		if (!Com_Zimbra_SForce.arrayContainsElement(a, b[i])) {
			a.push(b[i]);
		}
	}
	return a;
}


Com_Zimbra_SForce.prototype.getMailBodyAsText = function(note) {
	var body = "";
    if (note.body) {
        body = AjxStringUtil.htmlEncode(note.body);
    } else if (note._topPart && note._topPart.getContentForType) {
        body = AjxStringUtil.htmlEncode(note._topPart.getContentForType(ZmMimeTable.TEXT_PLAIN));
    } else {
		body = "";
	}

	if(!body || body == "") {//If we dont have body, try using getBodyContent api
		if (!note.isHtmlMail()) {
			return note.getBodyContent();
		}
		var div = document.createElement("div");
		div.innerHTML = note.getBodyContent();
		return AjxStringUtil.convertHtml2Text(div);
	} else {
		return body;
	}
};

Com_Zimbra_SForce.prototype.showInfo = 
function(msg) {
	var transitions = [ ZmToast.FADE_IN, ZmToast.PAUSE, ZmToast.PAUSE, ZmToast.FADE_OUT ];
	appCtxt.getAppController().setStatusMsg(msg, ZmStatusView.LEVEL_INFO, null, transitions);
};

Com_Zimbra_SForce.toIsoDate = function(theDate) {
	return AjxDateFormat.format("yyyy-MM-dd", theDate);
};

Com_Zimbra_SForce.toIsoDateTime = function(theDate) {
	var zDate = new Date(theDate.getTime());
	zDate.setMinutes(zDate.getMinutes() + zDate.getTimezoneOffset());
	var ret = AjxDateFormat.format("yyyy-MM-ddTHH:mm:ss'Z'", zDate);
	DBG.println(AjxDebug.DBG3, "ret: " + ret);
	return ret;
};

//--------------------------------------------------------------------------------------------------------
//Misc/Supporting functions (END)
//--------------------------------------------------------------------------------------------------------


//--------------------------------------------------------------------------------------------------------
//MailboxMetadata API
//--------------------------------------------------------------------------------------------------------
//"zwc:com_zimbra_sforce"
//Usage: Pass the sectionName, eg. getMailboxMetaData("zwc:<sectioname>", callback);
Com_Zimbra_SForce.prototype.getMailboxMetaData =
function(sectionName, callback) {
	var soapDoc = AjxSoapDoc.create("GetMailboxMetadataRequest", "urn:zimbraMail");//request name and urn(always zimbraMail)
	var secNode = soapDoc.set("meta");
	secNode.setAttribute("section", sectionName);
	var asyncMode = true;
	if(!callback) {
		asyncMode = false;
	}
	return appCtxt.getAppController().sendRequest({soapDoc:soapDoc, asyncMode:asyncMode, callback:callback});
};

Com_Zimbra_SForce.prototype.deleteMailboxMetaData =
function(sectionName, keyName,callback) {
	var _attrs = null;
	var keyValArray = [];
	if(keyName) {
		try{
			var matchFound = false;
			var response = this.getToMailboxMetaData(sectionName).GetMailboxMetadataResponse.meta[0];
			if(response._attrs) {
				_attrs = response._attrs;
				for(var oKey in _attrs) {
					if(keyName != oKey) {
						keyValArray.push({key:oKey, val:_attrs[oKey]});
					}
				}
			}
			if(!matchFound) {
				return "Key("+keyName+") not found in section("+sectionName+")";
			}
		}catch(e) {
			//consume
		}
	} 
	return this._doSetMailboxMetaData(sectionName, keyValArray, callback);
};

//Allows us to save random data in DB (upto 10kb per section)
//Automatically keeps old data and merges old data w/in a section with new one if 'override' isn't true
//@sectionName Name of the section; must start with zwc: ex: zwc:com_zimbra_sforce_AccListViews
//@keyValArray An array of key-val objects: ex: [{key:key1, val:val1}, {key:key2:val:val2}]
//@callback AjxCallback[optional] - if null,JS will wait for this operation to complete 
//@override - Boolean; If true, *entire section* will be overwritten with new set of key-val pairs
Com_Zimbra_SForce.prototype.setMailboxMetaData =
function(sectionName, keyValArray, callback, override) {
	if(!keyValArray) {//dont allow deleting section using this function
		return "No key=value pair sent. To delete section, use 'deleteMailboxMetaData' API";
	}
	var _attrs = null;
	if(!override) {
		try{
			var response = this.getMailboxMetaData(sectionName).GetMailboxMetadataResponse.meta[0];
			if(response._attrs) {
				_attrs = response._attrs;
				var oldKeyValArray = [];
				for(var oKey in _attrs) {
					var ignore = false;
					for(var i=0; i < keyValArray.length; i++) {
						var keyVal = keyValArray[i];
						if(keyVal.key == oKey) {
							ignore = true;
						}
					}
					if(!ignore) {
						oldKeyValArray.push({key:oKey, val:_attrs[oKey]});
					}
				}
				keyValArray = keyValArray.concat(oldKeyValArray);
			}
		}catch(e) {
			appCtxt.setStatusMsg("There was an exception saving data: " + e, ZmStatusView.LEVEL_WARNING);

		}
	}
	return this._doSetMailboxMetaData(sectionName, keyValArray, callback);
};

//internal - dont call directly
Com_Zimbra_SForce.prototype._doSetMailboxMetaData = 
function(sectionName, keyValArray, callback) {
	if(sectionName.indexOf("zwc:") == -1 &&  sectionName.indexOf("zd:") == -1) {
		return "sectionName must have namespace. send: 'zwc:<sectionName>'";
	}
	var soapDoc = AjxSoapDoc.create("SetMailboxMetadataRequest", "urn:zimbraMail");
	var doc = soapDoc.getDoc();
	var secNode = soapDoc.set("meta");// property name
	secNode.setAttribute("section", sectionName);
	for(var i =0; i < keyValArray.length; i++) {
		var keyVal = keyValArray[i];
		var el = doc.createElement("a");
		el.setAttribute("n", keyVal.key);
		el.appendChild(doc.createTextNode(keyVal.val));
		secNode.appendChild(el);
	}
	
	var asyncMode = true;
	if(!callback) {
		asyncMode = false;
	}
	return appCtxt.getAppController().sendRequest({soapDoc:soapDoc, asyncMode:asyncMode, callback:callback});
};


//--------------------------------------------------------------------------------------------------------
//MailboxMetadata API (END)
//--------------------------------------------------------------------------------------------------------
