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

function com_zimbra_socialPreferences(zimlet) {
	this.zimlet = zimlet;
	this.shell = this.zimlet.getShell();
	this._fbNeedPermCount = 0;
	this.social_pref_tweetmemePopularIsOn = this.zimlet.getUserProperty("social_pref_tweetmemePopularIsOn") == "true";
	this.social_pref_trendsPopularIsOn = this.zimlet.getUserProperty("social_pref_trendsPopularIsOn") == "true";
	this.social_pref_diggPopularIsOn = this.zimlet.getUserProperty("social_pref_diggPopularIsOn") == "true";
	this.social_pref_SocialMailUpdateOn = this.zimlet.getUserProperty("social_pref_SocialMailUpdateOn") == "true";
}

com_zimbra_socialPreferences.prototype._showManageAccntsDlg = function() {
	//if zimlet dialog already exists...
	if (this._manageAccntsDlg) {
		this._updateAccountsTable();
		this._updateAllFBPermissions();
		this._manageAccntsDlg.popup();
		return;
	}
	this._manageAccntsView = new DwtComposite(this.shell);
	this._manageAccntsView.setSize(550, 300);
	this._manageAccntsView.getHtmlElement().style.overflow = "auto";
	this._manageAccntsView.getHtmlElement().innerHTML = this._createManageeAccntsView();
	this._manageAccntsDlg = this.zimlet._createDialog({title:"Add/Remove Accounts", view:this._manageAccntsView, standardButtons:[DwtDialog.OK_BUTTON, DwtDialog.CANCEL_BUTTON]});
	this._manageAccntsDlg.setButtonListener(DwtDialog.OK_BUTTON, new AjxListener(this, this._manageAccntsOKBtnListener));
	this._addPrefButtons();
	this._updateAccountsTable();
	this._updateAllFBPermissions();
	this._manageAccntsDlg.popup();
};

com_zimbra_socialPreferences.prototype._addPrefButtons =
function() {
	var addTwitterBtn = new DwtButton({parent:this.zimlet.getShell()});
	addTwitterBtn.setText("Add Twitter Account");
	addTwitterBtn.setImage("social_twitterIcon");
	addTwitterBtn.addSelectionListener(new AjxListener(this, this._addTwitterBtnListener));
	document.getElementById("social_pref_addTwitterButtonCell").appendChild(addTwitterBtn.getHtmlElement());

	var addFacebookBtn = new DwtButton({parent:this.zimlet.getShell()});
	addFacebookBtn.setText("Add Facebook Account");
	addFacebookBtn.setImage("social_facebookIcon");
	addFacebookBtn.addSelectionListener(new AjxListener(this, this._addFacebookBtnListener));
	document.getElementById("social_pref_addFaceBookButtonCell").appendChild(addFacebookBtn.getHtmlElement());

	var deleteAccountBtn = new DwtButton({parent:this.zimlet.getShell()});
	deleteAccountBtn.setText("Delete Account");
	deleteAccountBtn.setImage("Trash");
	deleteAccountBtn.addSelectionListener(new AjxListener(this, this._deleteAccountBtnListener));
	document.getElementById("social_pref_deleteAccountCell").appendChild(deleteAccountBtn.getHtmlElement());

	var refreshTableBtn = new DwtButton({parent:this.zimlet.getShell()});
	refreshTableBtn.setText("Refresh Accounts");
	refreshTableBtn.setImage("Refresh");
	refreshTableBtn.addSelectionListener(new AjxListener(this, this._refreshTableBtnListener));
	document.getElementById("social_pref_refreshTableCell").appendChild(refreshTableBtn.getHtmlElement());
};

com_zimbra_socialPreferences.prototype._refreshTableBtnListener =
function() {
	this._updateAllFBPermissions();
};

com_zimbra_socialPreferences.prototype._addTwitterBtnListener =
function() {
	this.zimlet.twitter.performOAuth();
};

com_zimbra_socialPreferences.prototype._addFacebookBtnListener =
function() {
	this.reloginToFB = true;
	this.showAddFBInfoDlg();
	this.zimlet.facebook.loginToFB();
};

com_zimbra_socialPreferences.prototype._deleteAccountBtnListener =
function() {
	var needToUpdate = false;
	var hasAccounts = false;
	var newAllAccounts = new Array();
	for (var id in this.zimlet.allAccounts) {
		hasAccounts = true;
		if (!document.getElementById("social_pref_accnts_checkbox" + id).checked) {
			newAllAccounts[id] = this.zimlet.allAccounts[id];
		} else {
			needToUpdate = true;
		}
	}
	if (needToUpdate && hasAccounts) {
		this.zimlet.allAccounts = newAllAccounts;
		this.zimlet.setUserProperty("social_AllTwitterAccounts", this.zimlet.getAllAccountsAsString(), true);
		this._updateAccountsTable();
		this.zimlet._updateAllWidgetItems({updateSearchTree:false, updateSystemTree:true, updateAccntCheckboxes:true, searchCards:false});
	}
};

com_zimbra_socialPreferences.prototype._createManageeAccntsView =
function() {
	var html = new Array();
	var i = 0;
	html[i++] = "<BR/>";
	html[i++] = "<DIV class='social_topWgtClass' >";
	html[i++] = "<table width=400px cellpadding=2 cellspacing=2>";
	html[i++] = "<TR>";
	html[i++] = "<TD>";
	html[i++] = "<label style=\"font-size:12px;color:black;font-weight:bold\">Manage Accounts";
	html[i++] = "</label>";
	html[i++] = "</TD>";
	html[i++] = "</TR>";
	html[i++] = "</table>";
	html[i++] = "</DIV>";

	html[i++] = "<DIV class='social_white' id='social_pref_accntsTable'>";
	html[i++] = this._getPrefAccountsTableHTML();
	html[i++] = "</DIV>";
	html[i++] = "<BR>";
	html[i++] = "<BR>";
	html[i++] = "<DIV>";
	html[i++] = "<table width=100%>";
	html[i++] = "<TR>";
	html[i++] = "<TD  id='social_pref_addTwitterButtonCell'>";
	html[i++] = "</TD>";
	html[i++] = "<TD  id='social_pref_addFaceBookButtonCell'>";
	html[i++] = "</TD>";
	html[i++] = "<TD id='social_pref_refreshTableCell'>";
	html[i++] = "</TD>";
	html[i++] = "<TD  id='social_pref_deleteAccountCell'>";
	html[i++] = "</TD>";
	html[i++] = "</TR>";
	html[i++] = "</table>";
	html[i++] = "</DIV>";
	html[i++] = "<BR/>";
	html[i++] = "<DIV id='social_prefDlg_currentStateMessage' class='social_yellowBold' style='display:none'>";
	html[i++] = "</DIV >";
	html[i++] = "<BR/>";
	html[i++] = "<BR/>";
	html[i++] = "<BR/>";
	return html.join("");
};
com_zimbra_socialPreferences.prototype._updateAccountsTable =
function(additionalMsgParams) {
	document.getElementById("social_pref_accntsTable").innerHTML = this._getPrefAccountsTableHTML();
	for(var i=0; i < this._authorizeDivIdAndAccountMap.length;i++){
		var map = this._authorizeDivIdAndAccountMap[i];
		var authBtn = new DwtButton({parent:this.zimlet.getShell()});
		authBtn.setText("Authorize");
		authBtn.addSelectionListener(new AjxListener(this, this._authorizeBtnListener, map));
		document.getElementById(map.divId).appendChild(authBtn.getHtmlElement());
	}
	if(this._fbNeedPermCount != 0){
		this._setAccountPrefDlgAuthMessage("Please login to Facebook and Authorize each of '"+this._fbNeedPermCount+"' Permission(s). You need to click 'Authorize' button for each permission explicitely("+this._fbNeedPermCount+" times)", "blue");
	} else {
		this._setAccountPrefDlgAuthMessage("Accounts have been updated successfully", "green");
	}
	if(additionalMsgParams != undefined 
		&& additionalMsgParams.askForPermissions != undefined
		&& additionalMsgParams.askForPermissions == true
		&& this._fbNeedPermCount != 0) {
		this.showAddFBInfoDlg({permName:"", permCount: this._fbNeedPermCount});
		this.zimlet.facebook.askForPermissions();
	}
};

com_zimbra_socialPreferences.prototype._setAccountPrefDlgAuthMessage =
function (message, color) {
	document.getElementById("social_prefDlg_currentStateMessage").innerHTML = "<lable style='color:"+color+"'>"+message + "</label>";
	document.getElementById("social_prefDlg_currentStateMessage").style.display = "block";
};

com_zimbra_socialPreferences.prototype._updateAllFBPermissions =
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

com_zimbra_socialPreferences.prototype._authorizeBtnListener =
function(params) {
	var permName = "";
	if(params.permission == "read_stream")
		permName = "read";
	else if(params.permission == "publish_stream")
		permName = "write/publish";
	else if(params.permission == "offline_access")
		permName = "offline/rememberMe";

	this._addFacebookBtnListener();
};
com_zimbra_socialPreferences.prototype._getPrefAccountsTableHTML =
function() {
	this._authorizeDivIdAndAccountMap = new Array();
	var html = new Array();
	var i = 0;
	var noAccountsFound = true;
	this._fbNeedPermCount = 0;
	this._fbNeedPermissions = "";
	html[i++] = "<table cellspacing=1>";
	html[i++] = "<TR><TH>Select</TH><TH>Account Type</TH><TH>Account Name</TH><TH>Read Permission</TH><TH>Write/Publish Permission</TH><TH>Offline/RememberMe Permission</TH>";
	for (var id in this.zimlet.allAccounts) {
		var account = this.zimlet.allAccounts[id];
		html[i++] = "<TR>";
		html[i++] = "<TD width=16px>";
		html[i++] = "<input type='checkbox' id='social_pref_accnts_checkbox" + id + "' />";
		html[i++] = "</TD>";
		html[i++] = "<TD  align='center'>";
		if (account.__type == "twitter") {
			html[i++] = AjxImg.getImageHtml("social_twitterIcon");
		} else if (account.__type == "facebook") {
			html[i++] = AjxImg.getImageHtml("social_facebookIcon");
		}
		html[i++] = "</TD>";
		html[i++] = "<TD align='center'>";
		html[i++] = "<label style=\"font-size:12px;color:black;font-weight:bold\">";
		html[i++] = account.name;
		html[i++] = "</label>";
		html[i++] = "</TD>";
		html[i++] = "<TD  align='center'>";
		if(account.type == "twitter"){
			html[i++] = AjxImg.getImageHtml("social_checkIcon");
		} else if(account.type == "facebook") {
			if(account.read_stream == "YES") {
				html[i++] = AjxImg.getImageHtml("social_checkIcon");
			}else{
				var id = "social_pref_authorizeBtn_"+Dwt.getNextId();
				html[i++] = "<DIV id='"+id+"'></DIV>";
				this._authorizeDivIdAndAccountMap.push({account:account, permission:"read_stream", divId:id});
				this._fbNeedPermCount++;
				this._setNeedPermission("read");
			}
		}
		html[i++] = "</TD>";
		html[i++] = "<TD  align='center'>";
		if(account.type == "twitter"){
			html[i++] = AjxImg.getImageHtml("social_checkIcon");
		} else if(account.type == "facebook") {
			if(account.publish_stream == "YES") {
				html[i++] = AjxImg.getImageHtml("social_checkIcon");
			}else{
				var id = "social_pref_authorizeBtn_"+Dwt.getNextId();
				html[i++] = "<DIV id='"+id+"'></DIV>";
				this._authorizeDivIdAndAccountMap.push({account:account, permission:"publish_stream", divId:id});
				this._fbNeedPermCount++;
				this._setNeedPermission("publish");
			}
		}
		html[i++] = "</TD>";
		html[i++] = "<TD align='center'>";
		if(account.type == "twitter"){
			html[i++] = AjxImg.getImageHtml("social_checkIcon");
		} else if(account.type == "facebook") {
			if(account.offline_access == "YES") {
				html[i++] = AjxImg.getImageHtml("social_checkIcon");
			}else{
				var id = "social_pref_authorizeBtn_"+Dwt.getNextId();
				html[i++] = "<DIV id='"+id+"'></DIV>";
				this._authorizeDivIdAndAccountMap.push({account:account, permission:"offline_access", divId:id});
				this._fbNeedPermCount++;
				this._setNeedPermission("rememberMe");

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
com_zimbra_socialPreferences.prototype._setNeedPermission =
function(permission) {
	if(this._fbNeedPermissions == "")
		this._fbNeedPermissions = permission;
	else
		this._fbNeedPermissions = this._fbNeedPermissions + ","+ permission;
}
com_zimbra_socialPreferences.prototype._manageAccntsOKBtnListener =
function() {
	this.zimlet.setUserProperty("social_AllTwitterAccounts", this.zimlet.getAllAccountsAsString(), true);
	this.zimlet._updateAllWidgetItems({updateSearchTree:false, updateSystemTree:true, updateAccntCheckboxes:true, searchCards:false});
	this._manageAccntsDlg.popdown();

};


com_zimbra_socialPreferences.prototype.showAddFBInfoDlg = function(obj) {
	//if zimlet dialog already exists...
	var permStr = "";
	if(obj) {
		 permStr = "Please press 'Allow Access' button on facebook to grant "+this._fbNeedPermissions+" ("+obj.permCount+") permission(s). Then press 'OK'";
	}
	if (this._getFbInfoDialog) {
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

	this._getFbInfoDialog.popup();
};

com_zimbra_socialPreferences.prototype._getFbInfoOKBtnListener = function() {
	if(this.reloginToFB) {
		this.reloginToFB = false;
		this.needSessionId =  true;
		this.zimlet.facebook.fbCreateToken();	
	} else if(this.needSessionId) {
		this.reloginToFB = false;
		this.needSessionId =  false;
		this.zimlet.facebook._getSessionId();
	} else {
		this._refreshTableBtnListener();
		this._getFbInfoDialog.popdown();
	}
};

com_zimbra_socialPreferences.prototype._createFbInfoView =
function() {
	var html = new Array();
	var i = 0;
	html[i++] = "<DIV class='social_yellow'>";
	html[i++] = "<H3>Steps to adding facebook account:</H3>";
	html[i++] = "<B>PART 1: Login and Grant Permissions</B><br/>1. We have opened a facebook page, please Login to facebook <br/> 2. Grant all three permissions(details below) <br/>";
	html[i++] = "3. After permissions are granted, Facebook will show a page with 'success' written on it, you can close that page. <br/>";
	html[i++] = "4. Press 'OK' in this dialog <br/><br/> <B>PART 2: Load Permissions to Zimbra</B><br/>5. We have re-opened facebook page to load permissions from facebook <br/>";
	html[i++] = "6. If you see: 'You may now close this window and return to the application.', close that page";
	html[i++] = "or else please re-login to facebook<br/>7. Please Click 'OK' again in this dialog box";
	html[i++] = "<BR/>";
	html[i++] = "<H3>Permissions:</H3>";
	html[i++] = "<b>Read Permission:</b> Allows us to display facebook information";
	html[i++] = "<br/><b>Publish Permission:</b> Allows us to publish or write back to facebook";
	html[i++] = "<br/><b>Remember Me / Offline Permission:</b> By default, facebook authorizes expires after 24 hours of authorization. ";
	html[i++] = "With this, we get permanent access. <br><br>PS: You can always revoke all facebook/twitter permissions by logging on to facebook/twitter";
	html[i++] = "</DIV>";
	return html.join("");
};

com_zimbra_socialPreferences.prototype._showPreferencesDlg = function() {
	//if zimlet dialog already exists...
	if (this._getPrefDialog) {
		this._setPrefCheckboxes();
		this._getPrefDialog.popup();
		return;
	}
	this._getPrefView = new DwtComposite(this.zimlet.getShell());
	this._getPrefView.getHtmlElement().style.overflow = "auto";
	this._getPrefView.getHtmlElement().innerHTML = this._createPrefView();
	this._getPrefDialog = this.zimlet._createDialog({title:"Social Zimlet Preferences", view:this._getPrefView, standardButtons:[DwtDialog.OK_BUTTON, DwtDialog.CANCEL_BUTTON]});
	this._getPrefDialog.setButtonListener(DwtDialog.OK_BUTTON, new AjxListener(this, this._okPrefBtnListener));
	this._getPrefDialog.popup();
	this._setPrefCheckboxes();

};


com_zimbra_socialPreferences.prototype._okPrefBtnListener =
function() {
	var save = false;
	var currentVal = document.getElementById("social_pref_tweetmemePopularIsOn").checked;
	if(this.social_pref_tweetmemePopularIsOn != currentVal) {
		this.zimlet.setUserProperty("social_pref_tweetmemePopularIsOn", currentVal);
		save = true;
	}
	var currentVal = document.getElementById("social_pref_trendsPopularIsOn").checked;
	if(this.social_pref_trendsPopularIsOn != currentVal) {
		this.zimlet.setUserProperty("social_pref_trendsPopularIsOn", currentVal);
		save = true;
	}
	var currentVal = document.getElementById("social_pref_diggPopularIsOn").checked;
	if(this.social_pref_diggPopularIsOn != currentVal) {
		this.zimlet.setUserProperty("social_pref_diggPopularIsOn", currentVal);
		save = true;
	}
	
	var currentVal = document.getElementById("social_pref_SocialMailUpdateOn").checked;
	if(this.social_pref_SocialMailUpdateOn != currentVal) {
		this.zimlet.setUserProperty("social_pref_SocialMailUpdateOn", currentVal);
		save = true;
	}
	if(save){
		this.zimlet.saveUserProperties();
		appCtxt.getAppController().setStatusMsg("Preferences Saved", ZmStatusView.LEVEL_INFO);
	}
	this._getPrefDialog.popdown();
};


com_zimbra_socialPreferences.prototype._setPrefCheckboxes = function() {
	if(this.social_pref_tweetmemePopularIsOn)
		document.getElementById("social_pref_tweetmemePopularIsOn").checked = true;

	if(this.social_pref_trendsPopularIsOn)
		document.getElementById("social_pref_trendsPopularIsOn").checked = true;
	
	if(this.social_pref_diggPopularIsOn)
		document.getElementById("social_pref_diggPopularIsOn").checked = true;
	
	if(this.social_pref_SocialMailUpdateOn)
		document.getElementById("social_pref_SocialMailUpdateOn").checked = true;

};

com_zimbra_socialPreferences.prototype._createPrefView =
function() {
	var html = new Array();
	var i = 0;
	html[i++] = "<table>";
	html[i++] = "<tr><td><input type='checkbox' id='social_pref_tweetmemePopularIsOn' /> Show TweetMeme's Most Popular by default</td></tr>";
	html[i++] = "<tr><td><input type='checkbox' id='social_pref_trendsPopularIsOn' /> Show top Twitter Trend by default</td></tr>";
	html[i++] = "<tr><td><input type='checkbox' id='social_pref_diggPopularIsOn' /> Show digg's 'Popular in 24 hours' by default</td></tr>";
	html[i++] = "<tr><td><input type='checkbox' id='social_pref_SocialMailUpdateOn' /> Send Social mail with twitter updates (once a day)</td></tr>";
	html[i++] = "</table>";
	return html.join("");
};