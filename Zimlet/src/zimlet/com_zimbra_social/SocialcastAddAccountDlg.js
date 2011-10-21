function SocialcastAddAccountDlg(socialPrefDlg, zimlet) {
	this.socialPrefDlg = socialPrefDlg;
	this.zimlet = zimlet;
	this.socialcastAccounts = this.zimlet.socialcastAccounts;
}

SocialcastAddAccountDlg.prototype._displayPrefDialog = function() {
	// if zimlet dialog already exists...
	if (this.pbDialog) {
		this.pbDialog.popup();
		this._addAccntPrefsTabControl();
		return;
	}
	this.pView = new DwtComposite(this.zimlet.getShell());
	this.pView.getHtmlElement().innerHTML = this._createPreferenceView();
	this.pbDialog = new ZmDialog({
		parent : this.zimlet.getShell(),
		title : this.zimlet.getMessage("addSocialcastAccount"),
		view : this.pView,
		standardButtons : [ DwtDialog.OK_BUTTON, DwtDialog.CANCEL_BUTTON ]
	});

	this.pbDialog.setButtonListener(DwtDialog.OK_BUTTON, new AjxListener(this,
			this._okBtnListner));
	this.pbDialog.popup();
	this._addAccntPrefsTabControl();
};

SocialcastAddAccountDlg.prototype.popup = function() {
	this._displayPrefDialog();
};

SocialcastAddAccountDlg.prototype.popdown = function() {
	this.pbDialog.popdown();
};

SocialcastAddAccountDlg.prototype._createPreferenceView = function() {
	var html = new Array();
	var i = 0;
	html.push("<table>" ,
			"<tr>",
			"<td><label style='font-weight: bold;'>",this.zimlet.getMessage("socialcastEmail"),"</label>",
			"</td>",
			"<td><input style='width:200px' type='text' id='socialcastAddAccnt_email'/><td>",
			"</tr>",
			"<tr>",
			"<td><label style='font-weight: bold;'>",this.zimlet.getMessage("socialcastPassword"),"</label>",
			"</td>",
			"<td><input style='width:200px' type='password' id='socialcastAddAccnt_password'/><td>",
			"</tr>",
			"<tr>",
			"<td><label style='font-weight: bold;'>",this.zimlet.getMessage("communityDomain"),"</label>",
			"</td>",
			"<td><input style='width:200px' type='text' id='socialcastAddAccnt_server'/><td>",
			"</tr>",
			"</table>");
	return html.join("");
};

SocialcastAddAccountDlg.prototype._okBtnListner = function() {
	this.socialcastAddAccnt_email = document.getElementById("socialcastAddAccnt_email").value;
	this.socialcastAddAccnt_password = document.getElementById("socialcastAddAccnt_password").value;
	this.socialcastAddAccnt_server = document.getElementById("socialcastAddAccnt_server").value;
	this._authenticateCurrentAccount();
	this.pbDialog.popdown();// hide the dialog
};

SocialcastAddAccountDlg.prototype._addAccntPrefsTabControl =
function() {
	this.pbDialog._tabGroup.removeAllMembers();
	this.pbDialog._tabGroup.addMember(document.getElementById("socialcastAddAccnt_email"));
	this.pbDialog._tabGroup.addMember(document.getElementById("socialcastAddAccnt_password"));
	this.pbDialog._tabGroup.addMember(document.getElementById("socialcastAddAccnt_server"));
	document.getElementById("socialcastAddAccnt_email").focus();
};

SocialcastAddAccountDlg.prototype._authenticateCurrentAccount = function() {
	var url = [ "https://", this.socialcastAddAccnt_server, "/api/authentication.json"].join("");
	var data = ["email=",this.socialcastAddAccnt_email,"&password=",this.socialcastAddAccnt_password,""].join("");
	var hdrs = new Array();
	hdrs["content-type"] = "application/x-www-form-urlencoded";
	hdrs["content-length"] = data.length;
	var feedUrl = ZmZimletBase.PROXY + AjxStringUtil.urlComponentEncode(url);
	AjxRpc.invoke(data, feedUrl, hdrs, new AjxCallback(this,
			this._handleAddAccount), false);
};

SocialcastAddAccountDlg.prototype._handleAddAccount = function(response) {
	if (!response.success) {
		appCtxt.getAppController().setStatusMsg(this.zimlet.getMessage("couldNotAuthenticateSocialcastAccnt"), ZmStatusView.LEVEL_WARNING);
		return;
	}
	var jsonObj = JSON.parse(response.text);
	if(jsonObj.communities && jsonObj.communities[0] && jsonObj.communities[0].profile) {
	   var profile = jsonObj.communities[0].profile;
	}
	var account = {un:profile.username, n:profile.name, e:this.socialcastAddAccnt_email, p:this.socialcastAddAccnt_password, s:this.socialcastAddAccnt_server};
	this.socialPrefDlg.socialcastAccounts.push(account);
	this.zimlet.setUserProperty("socialcastAccounts", JSON.stringify(this.socialPrefDlg.socialcastAccounts));
	this.socialPrefDlg._updateAccountsTable();
	this.zimlet.socialcast.setGroupMemberships({account:account});
	this.zimlet.socialcast.setSocialcastStreams({account:account});
};
