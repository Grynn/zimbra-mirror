/*
 * ***** BEGIN LICENSE BLOCK *****
 *
 * Zimbra Collaboration Suite Zimlets
 * Copyright (C) 2006, 2007 Zimbra, Inc.
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

function com_zimbra_tweetziPreferences(zimlet) {
	this.zimlet = zimlet;
	this.shell = this.zimlet.getShell();
	this._fbNeedPermCount = 0;
}

com_zimbra_tweetziPreferences.prototype._showpreferencesDlg = function() {
	//if zimlet dialog already exists...
	if (this._preferencesDialog) {
		this._updateAccountsTable();
		this._updateAllFBPermissions();
		this._preferencesDialog.popup();
		return;
	}
	this._preferencesView = new DwtComposite(this.shell);
	this._preferencesView.setSize(550, 300);
	this._preferencesView.getHtmlElement().style.overflow = "auto";
	this._preferencesView.getHtmlElement().innerHTML = this._createPreferencesView();
	this._preferencesDialog = this.zimlet._createDialog({title:"tweetZi Account Preferences", view:this._preferencesView, standardButtons:[DwtDialog.OK_BUTTON, DwtDialog.CANCEL_BUTTON]});
	this._preferencesDialog.setButtonListener(DwtDialog.OK_BUTTON, new AjxListener(this, this._okpreferencesBtnListener));
	this._addPrefButtons();
	this._updateAccountsTable();
	this._updateAllFBPermissions();
	this._preferencesDialog.popup();
};

com_zimbra_tweetziPreferences.prototype._addPrefButtons =
function() {
	var addTwitterBtn = new DwtButton({parent:this.zimlet.getShell()});
	addTwitterBtn.setText("Add Twitter Account");
	addTwitterBtn.setImage("tweetzi_twitterIcon");
	addTwitterBtn.addSelectionListener(new AjxListener(this, this._addTwitterBtnListener));
	document.getElementById("tweetzi_pref_addTwitterButtonCell").appendChild(addTwitterBtn.getHtmlElement());

	var addFacebookBtn = new DwtButton({parent:this.zimlet.getShell()});
	addFacebookBtn.setText("Add Facebook Account");
	addFacebookBtn.setImage("tweetzi_facebookIcon");
	addFacebookBtn.addSelectionListener(new AjxListener(this, this._addFacebookBtnListener));
	document.getElementById("tweetzi_pref_addFaceBookButtonCell").appendChild(addFacebookBtn.getHtmlElement());

	var deleteAccountBtn = new DwtButton({parent:this.zimlet.getShell()});
	deleteAccountBtn.setText("Delete Account");
	deleteAccountBtn.setImage("Trash");
	deleteAccountBtn.addSelectionListener(new AjxListener(this, this._deleteAccountBtnListener));
	document.getElementById("tweetzi_pref_deleteAccountCell").appendChild(deleteAccountBtn.getHtmlElement());

	var refreshTableBtn = new DwtButton({parent:this.zimlet.getShell()});
	refreshTableBtn.setText("Refresh Accounts");
	refreshTableBtn.setImage("Refresh");
	refreshTableBtn.addSelectionListener(new AjxListener(this, this._refreshTableBtnListener));
	document.getElementById("tweetzi_pref_refreshTableCell").appendChild(refreshTableBtn.getHtmlElement());
};

com_zimbra_tweetziPreferences.prototype._refreshTableBtnListener =
function() {
	if(this.zimlet.facebook.waitingForApproval){
		this.zimlet.facebook._getSessionId();
	}else {
		this._updateAllFBPermissions();
	}
};

com_zimbra_tweetziPreferences.prototype._addTwitterBtnListener =
function() {
	this.zimlet.twitter.performOAuth();
};

com_zimbra_tweetziPreferences.prototype._addFacebookBtnListener =
function() {
	this.showAddFBInfoDlg();
	this._setAccountPrefDlgAuthMessage("Press 'Refresh Accounts' after logging into facebook", "blue");
	this.zimlet.facebook._fbCreateToken();
};

com_zimbra_tweetziPreferences.prototype._deleteAccountBtnListener =
function() {
	var needToUpdate = false;
	var hasAccounts = false;
	var newAllAccounts = new Array();
	for (var id in this.zimlet.allAccounts) {
		hasAccounts = true;
		if (!document.getElementById("tweetzi_pref_accnts_checkbox" + id).checked) {
			newAllAccounts[id] = this.zimlet.allAccounts[id];
		} else {
			needToUpdate = true;
		}
	}
	if (needToUpdate && hasAccounts) {
		this.zimlet.allAccounts = newAllAccounts;
		this.zimlet.setUserProperty("tweetzi_AllTwitterAccounts", this.zimlet.getAllAccountsAsString(), true);
		this._updateAccountsTable();
		this.zimlet._updateAllWidgetItems({updateSearchTree:false, updateSystemTree:true, updateAccntCheckboxes:true, searchCards:false});
	}
};

com_zimbra_tweetziPreferences.prototype._createPreferencesView =
function() {
	var html = new Array();
	var i = 0;
	html[i++] = "<BR/>";
	html[i++] = "<DIV class='tweetzi_topWgtClass' >";
	html[i++] = "<table width=400px cellpadding=2 cellspacing=2>";
	html[i++] = "<TR>";
	html[i++] = "<TD>";
	html[i++] = "<label style=\"font-size:12px;color:black;font-weight:bold\">Manage Accounts";
	html[i++] = "</label>";
	html[i++] = "</TD>";
	html[i++] = "</TR>";
	html[i++] = "</table>";
	html[i++] = "</DIV>";

	html[i++] = "<DIV class='tweetzi_white' id='tweetzi_pref_accntsTable'>";
	html[i++] = this._getPrefAccountsTableHTML();
	html[i++] = "</DIV>";
	html[i++] = "<BR>";
	html[i++] = "<BR>";
	html[i++] = "<DIV>";
	html[i++] = "<table width=100%>";
	html[i++] = "<TR>";
	html[i++] = "<TD  id='tweetzi_pref_addTwitterButtonCell'>";
	html[i++] = "</TD>";
	html[i++] = "<TD  id='tweetzi_pref_addFaceBookButtonCell'>";
	html[i++] = "</TD>";
	html[i++] = "<TD id='tweetzi_pref_refreshTableCell'>";
	html[i++] = "</TD>";
	html[i++] = "<TD  id='tweetzi_pref_deleteAccountCell'>";
	html[i++] = "</TD>";
	html[i++] = "</TR>";
	html[i++] = "</table>";
	html[i++] = "</DIV>";
	html[i++] = "<BR/>";
	html[i++] = "<DIV id='tweetzi_prefDlg_currentStateMessage' class='tweetzi_yellowBold' style='display:none'>";
	html[i++] = "</DIV >";
	html[i++] = "<BR/>";
	html[i++] = "<BR/>";
	html[i++] = "<BR/>";
	/*
	html[i++] = "<DIV class='tweetzi_yellow' >";
	html[i++] = "<b>Please Note:</b><BR/><b>For twitter Account</b>, Authorizing the Application automatically provides all permissions.";
	html[i++] = "<br/><br/><b>For facebook Account</b>, we need you to explicitely grant the following permissions";
	html[i++] = "<br/><b>Read Permission:</b> Allows us to display facebook information";
	html[i++] = "<br/><b>Publish Permission:</b> Allows us to publish or write back to facebook";
	html[i++] = "<br/><b>Remember Me / Offline Permission:</b> By default, facebook authorizes expires after 24 hours of authorization. ";
	html[i++] = "With this, we get permanent access. <br><br>PS: You can always revoke all facebook/twitter permissions by logging on to facebook/twitter";
	html[i++] = "</DIV>";
	*/
	return html.join("");
};
com_zimbra_tweetziPreferences.prototype._updateAccountsTable =
function(additionalMsgParams) {
	document.getElementById("tweetzi_pref_accntsTable").innerHTML = this._getPrefAccountsTableHTML();
	for(var i=0; i < this._authorizeDivIdAndAccountMap.length;i++){
		var map = this._authorizeDivIdAndAccountMap[i];
		var authBtn = new DwtButton({parent:this.zimlet.getShell()});
		authBtn.setText("Authorize");
		authBtn.addSelectionListener(new AjxListener(this, this._authorizeBtnListener, map));
		document.getElementById(map.divId).appendChild(authBtn.getHtmlElement());
	}
	if(this._fbNeedPermCount != 0){
		//this._setAccountPrefDlgAuthMessage(additionalMsgParams.message, additionalMsgParams.color);
		this._setAccountPrefDlgAuthMessage("Please login to Facebook and Authorize each of '"+this._fbNeedPermCount+"' Permissions. You need to click 'Authorize' button for each permission explicitely("+this._fbNeedPermCount+" times)", "blue");
	} else {
		this._setAccountPrefDlgAuthMessage("Accounts have been updated successfully", "green");
	}
};

com_zimbra_tweetziPreferences.prototype._setAccountPrefDlgAuthMessage =
function (message, color) {
	document.getElementById("tweetzi_prefDlg_currentStateMessage").innerHTML = "<lable style='color:"+color+"'>"+message + "</label>";
	document.getElementById("tweetzi_prefDlg_currentStateMessage").style.display = "block";
};
com_zimbra_tweetziPreferences.prototype._updateAllFBPermissions =
function(additionalMsgParams) {
	for(var id in this.zimlet.allAccounts) {
		var account = this.zimlet.allAccounts[id];
		if(account.type == "facebook") {
			var callback0 = new AjxCallback(this, this._updateAccountsTable, additionalMsgParams);
			var callback1 =  new AjxCallback(this.zimlet.facebook, this.zimlet.facebook._getExtendedPermissionInfo, {account:account, permission:"read_stream", callback:callback0});
			var callback2 =  new AjxCallback(this.zimlet.facebook, this.zimlet.facebook._getExtendedPermissionInfo, {account:account, permission:"publish_stream", callback:callback1});
			this.zimlet.facebook._getExtendedPermissionInfo({account:account, permission:"offline_access", callback:callback2});
		}
	}

};
com_zimbra_tweetziPreferences.prototype._authorizeBtnListener =
function(params) {
	var permName = "";
	if(params.permission == "read_stream")
		permName = "read";
	else if(params.permission == "publish_stream")
		permName = "write/publish";
	else if(params.permission == "offline_access")
		permName = "offline/rememberMe";

	//this._setAccountPrefDlgAuthMessage("Login to Facebook and Authorize Each of 3 Permissions<br/> You need to click each 'Authorize' Button explicitely(up to 3 times)", "blue");
	this.showAddFBInfoDlg(permName);
	this.zimlet.facebook.authorizeExtendedPermission(params);
};
com_zimbra_tweetziPreferences.prototype._getPrefAccountsTableHTML =
function() {
	this._authorizeDivIdAndAccountMap = new Array();
	var html = new Array();
	var i = 0;
	var noAccountsFound = true;
	this._fbNeedPermCount = 0;
	html[i++] = "<table cellspacing=1>";
	html[i++] = "<TR><TH>Select</TH><TH>Account Type</TH><TH>Account Name</TH><TH>Read Permission</TH><TH>Write/Publish Permission</TH><TH>Offline/RememberMe Permission</TH>";
	for (var id in this.zimlet.allAccounts) {
		var account = this.zimlet.allAccounts[id];
		html[i++] = "<TR>";
		html[i++] = "<TD width=16px>";
		html[i++] = "<input type='checkbox' id='tweetzi_pref_accnts_checkbox" + id + "' />";
		html[i++] = "</TD>";
		html[i++] = "<TD  align='center'>";
		if (account.__type == "twitter") {
			html[i++] = AjxImg.getImageHtml("tweetzi_twitterIcon");
		} else if (account.__type == "facebook") {
			html[i++] = AjxImg.getImageHtml("tweetzi_facebookIcon");
		}
		html[i++] = "</TD>";
		html[i++] = "<TD align='center'>";
		html[i++] = "<label style=\"font-size:12px;color:black;font-weight:bold\">";
		html[i++] = account.name;
		html[i++] = "</label>";
		html[i++] = "</TD>";
		html[i++] = "<TD  align='center'>";
		if(account.type == "twitter"){
			html[i++] = AjxImg.getImageHtml("tweetzi_checkIcon");
		} else if(account.type == "facebook") {
			if(account.read_stream == "YES") {
				html[i++] = AjxImg.getImageHtml("tweetzi_checkIcon");
			}else{
				var id = "tweetzi_pref_authorizeBtn_"+Dwt.getNextId();
				html[i++] = "<DIV id='"+id+"'></DIV>";
				this._authorizeDivIdAndAccountMap.push({account:account, permission:"read_stream", divId:id});
				this._fbNeedPermCount++;
			}
		}
		html[i++] = "</TD>";
		html[i++] = "<TD  align='center'>";
		if(account.type == "twitter"){
			html[i++] = AjxImg.getImageHtml("tweetzi_checkIcon");
		} else if(account.type == "facebook") {
			if(account.publish_stream == "YES") {
				html[i++] = AjxImg.getImageHtml("tweetzi_checkIcon");
			}else{
				var id = "tweetzi_pref_authorizeBtn_"+Dwt.getNextId();
				html[i++] = "<DIV id='"+id+"'></DIV>";
				this._authorizeDivIdAndAccountMap.push({account:account, permission:"publish_stream", divId:id});
				this._fbNeedPermCount++;
			}
		}
		html[i++] = "</TD>";
		html[i++] = "<TD align='center'>";
		if(account.type == "twitter"){
			html[i++] = AjxImg.getImageHtml("tweetzi_checkIcon");
		} else if(account.type == "facebook") {
			if(account.offline_access == "YES") {
				html[i++] = AjxImg.getImageHtml("tweetzi_checkIcon");
			}else{
				var id = "tweetzi_pref_authorizeBtn_"+Dwt.getNextId();
				html[i++] = "<DIV id='"+id+"'></DIV>";
				this._authorizeDivIdAndAccountMap.push({account:account, permission:"offline_access", divId:id});
				this._fbNeedPermCount++;
			}
		}
		html[i++] = "</TD>";
		html[i++] = "</TR>";
		noAccountsFound = false;
	}
	if (noAccountsFound) {
		html[i++] = "<TR>";
		html[i++] = "<TD colspan=6 align='center' style='font-weight:bold;font-size:12px;color:blue'>";
		html[i++] = "No Accounts Found.";
		html[i++] = "</TD>";
		html[i++] = "</TR>";
	}
	html[i++] = "</table>";
	return html.join("");
};

com_zimbra_tweetziPreferences.prototype._okpreferencesBtnListener =
function() {
	this.zimlet.setUserProperty("tweetzi_AllTwitterAccounts", this.zimlet.getAllAccountsAsString(), true);
	this.zimlet._updateAllWidgetItems({updateSearchTree:false, updateSystemTree:true, updateAccntCheckboxes:true, searchCards:false});
	this._preferencesDialog.popdown();

};


com_zimbra_tweetziPreferences.prototype.showAddFBInfoDlg = function(permName) {
	//if zimlet dialog already exists...
	if (this._getFbInfoDialog) {
		if(permName){
			document.getElementById("tweetzi_pref_fb_message").innerHTML = "Please grant '"+permName+"' permission by logging on to facebook. Then press 'OK'";
		}
		this._getFbInfoDialog.popup();
		return;
	}
	this._getFbInfoView = new DwtComposite(this.zimlet.getShell());
	this._getFbInfoView.getHtmlElement().style.overflow = "auto";
	this._getFbInfoView.setSize(550);

	this._getFbInfoView.getHtmlElement().innerHTML = this._createFbInfoView();
	var  addFBAccntButtonId = Dwt.getNextId();
	var addFBAccntButton = new DwtDialog_ButtonDescriptor(addFBAccntButtonId, ("Authorized"), DwtDialog.ALIGN_RIGHT);
	this._getFbInfoDialog = this.zimlet._createDialog({title:"Facebook Information", view:this._getFbInfoView, standardButtons:[DwtDialog.OK_BUTTON, DwtDialog.CANCEL_BUTTON]});
	this._getFbInfoDialog.setButtonListener(DwtDialog.OK_BUTTON, new AjxListener(this, this._getFbInfoOKBtnListener));
	if(permName){
		document.getElementById("tweetzi_pref_fb_message").innerHTML = "Please grant '"+permName+"' permission by logging on to facebook. Then press 'OK'";
	}
	this._getFbInfoDialog.popup();
};

com_zimbra_tweetziPreferences.prototype._getFbInfoOKBtnListener = function() {
	this._refreshTableBtnListener();
	this._getFbInfoDialog.popdown();
};

com_zimbra_tweetziPreferences.prototype._createFbInfoView =
function() {
	var html = new Array();
	var i = 0;
	html[i++] = "<DIV class='tweetzi_yellow'>";
	html[i++] = "<DIV  align=center><H2 id='tweetzi_pref_fb_message'>Please Login to facebook and press 'OK' </H2></DIV>";
	html[i++] = "<BR/>";
	html[i++] = "<H3>PLEASE NOTE: We need you to explicitely grant the following 3 permissions:</H3>";
	html[i++] = "<b>Read Permission:</b> Allows us to display facebook information";
	html[i++] = "<br/><b>Publish Permission:</b> Allows us to publish or write back to facebook";
	html[i++] = "<br/><b>Remember Me / Offline Permission:</b> By default, facebook authorizes expires after 24 hours of authorization. ";
	html[i++] = "With this, we get permanent access. <br><br>PS: You can always revoke all facebook/twitter permissions by logging on to facebook/twitter";
	html[i++] = "</DIV>";
	return html.join("");
};