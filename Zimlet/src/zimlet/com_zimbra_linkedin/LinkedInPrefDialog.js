function LinkedInPrefDialog(zimlet) {
	this.zimlet = zimlet;
	this._shell = zimlet.getShell();
}

LinkedInPrefDialog.MY_LINKEDIN_URL = "https://api.linkedin.com/v1/people/~";

LinkedInPrefDialog.prototype.popup =
function() {
	if (this.prefDlg) {
		this.prefDlg.popup();
		return;
	}
	this.prefView = new DwtComposite(this._shell);
	this.prefView.getHtmlElement().innerHTML = this._createPreferenceView();
	this.prefDlg = new ZmDialog({parent:this._shell, title:this.zimlet.getMessage("preferences"), view:this.prefView, standardButtons:[DwtDialog.OK_BUTTON]});
	this._setPreferences();
	this.prefDlg.popup();
};

LinkedInPrefDialog.prototype.popdown =
function() {
	if (this.prefDlg) {
		this.prefDlg.popdown();
	}
};

LinkedInPrefDialog.prototype._setPreferences =
function() {
	this.linkedInZimlet_oauth_token = this.zimlet.getUserProperty("linkedInZimlet_oauth_token");
	this.linkedInZimlet_oauth_token_secret = this.zimlet.getUserProperty("linkedInZimlet_oauth_token_secret");
	this.linkedInZimlet_account_name = this.zimlet.getUserProperty("linkedInZimlet_account_name");
	var accntName = this.zimlet.getMessage("accountNotSet");
	if (this.linkedInZimlet_account_name) {
		accntName = this.linkedInZimlet_account_name;
	}
	document.getElementById("linkedInZimlet_accountName").innerHTML = accntName;

	var btn = new DwtButton({parent:this._shell});
	btn.setText(this.zimlet.getMessage("addLinkedInAccount"));
	btn.setImage("LinkedinZimletIcon");
	btn.addSelectionListener(new AjxListener(this, this._makeOAuthCall));
	document.getElementById("linkedInZimlet_AddAccntBtn").appendChild(btn.getHtmlElement());
};

LinkedInPrefDialog.prototype._createPreferenceView =
function() {
	return AjxTemplate.expand("com_zimbra_linkedin.templates.LinkedIn#PreferenceView");
};

LinkedInPrefDialog.prototype._makeOAuthCall =
function() {
	if (!this._oauth) {
		var oauthResultCallback = new AjxCallback(this, this._handleOAuthResult);
		this._oauth = new LinkedInZimletOAuth(this.zimlet, oauthResultCallback);
	}
	this._oauth.showOAuthDialog();
};

LinkedInPrefDialog.prototype._handleOAuthResult =
function(result) {
	if (!result.success) {
		if (result.httpResponse) {
			this.zimlet.showWarningMsg(result.httpResponse.text);
		} else {
			this.zimlet.showWarningMsg(this.zimlet.getMessage("unknownError"));
		}
		return;
	}
	
	var oauthTokens = result.oauthTokens;
	this.linkedInZimlet_oauth_token = oauthTokens["oauth_token"];
	this.linkedInZimlet_oauth_token_secret = oauthTokens["oauth_token_secret"];
	this._oauth.setAuthTokens({"oauth_token": this.linkedInZimlet_oauth_token, "oauth_token_secret": this.linkedInZimlet_oauth_token_secret});
	//get userName
	var userNameCallback = new AjxCallback(this, this._userNameHandler);
	this._oauth.makeHTTPGet({url: LinkedInPrefDialog.MY_LINKEDIN_URL, callback: userNameCallback});
};


LinkedInPrefDialog.prototype._userNameHandler =
function(result) {
	if (!result.success) {
		if (result.httpResponse) {
			this.zimlet.showWarningMsg(result.httpResponse.text);
		} else {
			this.zimlet.showWarningMsg(this.zimlet.getMessage("unknownError"));
		}
		return;
	}
	
	var xd = new AjxXmlDoc.createFromDom(result.xml).toJSObject(true, false);
	var name = "";
	if (xd["first-name"]) {
		name = xd["first-name"].toString() + " ";
	}
	if (xd["last-name"]) {
		name += xd["last-name"].toString();
	}
	if (document.getElementById("linkedInZimlet_accountName")) {
		document.getElementById("linkedInZimlet_accountName").innerHTML = name;
	}
	this.zimlet.setUserProperty("linkedInZimlet_account_name", name);
	this.zimlet.setUserProperty("linkedInZimlet_oauth_token", this.linkedInZimlet_oauth_token);
	this.zimlet.setUserProperty("linkedInZimlet_oauth_token_secret", this.linkedInZimlet_oauth_token_secret);
	this.zimlet.saveUserProperties();
};