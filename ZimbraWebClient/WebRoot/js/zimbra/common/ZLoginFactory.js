/*
 * ***** BEGIN LICENSE BLOCK *****
 * Version: ZPL 1.2
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.2 ("License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.zimbra.com/license
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
 * the License for the specific language governing rights and limitations
 * under the License.
 * 
 * The Original Code is: Zimbra Collaboration Suite Web Client
 * 
 * The Initial Developer of the Original Code is Zimbra, Inc.
 * Portions created by Zimbra are Copyright (C) 2005, 2006 Zimbra, Inc.
 * All Rights Reserved.
 * 
 * Contributor(s):
 * 
 * ***** END LICENSE BLOCK *****
 */

ZLoginFactory = function() {}

ZLoginFactory.defaultParams = {
	companyURL : ZmMsg.splashScreenCompanyURL,

	shortVersion : "",
	longVersion : "Version 3.1 (April 15, 2006)",

	appName : ZmMsg.splashScreenAppName,
	productName : "",

	showError : false,
	errorMsg : "",

	showAbout : false,
	aboutMsg : "",

	showLoading : false,
	loadingMsg : "",
	
	showForm : false,
	
	showUserField : false,
	userNameMsg : ZmMsg.username,
	
	showPasswordField : false,
	passwordMsg : ZmMsg.password,
	
	showNewPasswordFields : false,
	newPassword1Msg : ZmMsg.newPassword,
	newPassword2Msg : ZmMsg.confirmPassword,

	showLicenseMsg : false,
	licenseMsg : "",
	
	showRememberMeCheckbox : false,
	rememberMeMsg : ZmMsg.rememberMe,
	
	showButton : false,
	loginAction : "ZMLogin.handleLogin();",
	buttonName : ZmMsg.login,
	
	copyrightText : ZmMsg.splashScreenCopyright,
	
	loginBannerImageWidth : "349px;",
	loginBannerImageHeight : "92px;",
	loginBannerImageURL : "http://localhost:7070/zimbra/img/loRes/logo/LoginBanner.gif"
};

ZLoginFactory.setDefaultParam = function (name, value) {
	this.defaultParams[name] = value;
}


// show and hide various things
ZLoginFactory.showErrorMsg = function (msg) 		{	this.setHTML("ZLoginErrorMsg", msg);		this.show("ZLoginErrorPanel");	}
ZLoginFactory.hideErrorMsg = function () 			{												this.hide("ZLoginErrorPanel");	}

ZLoginFactory.showAboutMsg = function (msg) 		{	this.setHTML("ZLoginAboutPanel", msg);		this.show("ZLoginAboutPanel");	}
ZLoginFactory.hideAboutMsg = function () 			{												this.hide("ZLoginAboutPanel");	}

ZLoginFactory.showLoadingMsg = function (msg)		{	this.setHTML("ZLoginLoadingMsg", msg);		this.show("ZLoginAboutPanel");	}
ZLoginFactory.hideLoadingMsg = function () 		{													this.hide("ZLoginAboutPanel");	}

ZLoginFactory.showForm = function ()				{												this.show("ZLoginFormPanel");	}
ZLoginFactory.hideForm = function () 				{												this.hide("ZLoginFormPanel");	}

ZLoginFactory.showUserField = function (name)		{	this.setHTML("uname", name);				this.show("ZLoginAboutPanel");	}
ZLoginFactory.hideUserField = function () 			{												this.hide("ZLoginAboutPanel");	}

ZLoginFactory.showPasswordField = function (msg)	{	this.show("ZLoginAboutPanel");	}
ZLoginFactory.hidePasswordField = function () 		{	this.hide("ZLoginAboutPanel");	}

ZLoginFactory.showNewPasswordFields = function ()	{	this.show("ZLoginNewPassword1Tr"); this.show("ZLoginNewPassword2Tr");	}
ZLoginFactory.hideNewPasswordFields = function () 	{	this.hide("ZLoginNewPassword1Tr"); this.hide("ZLoginNewPassword2Tr");	}

ZLoginFactory.showRememberMeCheckbox = function ()	{	this.show("ZLoginRememberMeContainer");	}
ZLoginFactory.hideRememberMeCheckbox = function ()	{	this.hide("ZLoginRememberMeContainer");	}


ZLoginFactory.setLoginButtonName = function (name) 	{	this.setHTML("ZLoginButtonText", name);	}
ZLoginFactory.setLoginButtonAction = function (method) {	var el = document.getElementById("ZLoginButton"); if (el) el.onclick = method	}


ZLoginFactory.getLoginDialogHTML = function (params) {
	if (params == null) params = {};
	var defaults = this.defaultParams;

	var html = [
		 "<div id='ZLoginPanel'>",
			"<table class='zLoginTable' width='100%' cellpadding=0 cellspacing=0>",
				"<tr><td id='ZLoginHeaderContainer'><center>",
						"<table class='zLoginTable'>",
							"<tr><td id='ZLoginBannerContainer'>",
									"<div id='ZLoginBannerPanel'>",
										"<table class='zLoginTable'><tr>",
											"<td><div id='ZLoginBannerImage' style='width:", (params.loginBannerImageWidth || defaults.loginBannerImageWidth),
																				 ";height:", (params.loginBannerImageHeight || defaults.loginBannerImageHeight),
																				 ";background-image:url(", (params.loginBannerImageURL || defaults.loginBannerImageURL), ")'", 
													" onclick='window.open(\"", (params.companyURL || defaults.companyURL), "\", \"_blank\")'></div></td>",
											"<td valign=top id='ZLoginShortVersion'>", (params.shortVersion || defaults.shortVersion), "</td>",
										"</tr></table>",
										"<div id='ZLoginAppName'>", (params.appName || defaults.appName), "</div>",
										"<div id='ZLoginProductName'>", (params.productName || defaults.productName), "</div>",
										"<div id='ZLoginLongVersion'>", (params.longVersion || defaults.longVersion), "</div>",
									"</div>",
								"</td>",
							"</tr>",
						"</table>",
					"</center></td>",
				"</tr>",
				"<tr><td id='ZLoginBodyContainer'>",
						"<div id='ZLoginErrorPanel' ", (params.showError ? "" : "style='display:none'"), ">",
							"<table>",
								"<tr><td valign='top' width='40'><div id='ZLoginErrorIcon' class='ImgZLoginError'></td>",
									"<td valign='top' width='*' id='ZLoginErrorMsg' class='errorText'>", (params.errorMsg || defaults.errorMsg), "</td>",
								"</tr>",
							"</table>",
						"</div>",
						"",
						"<div id='ZLoginAboutPanel' ", (params.showAbout ? "" : "style='display:none'"), ">", (params.aboutMsg || defaults.aboutMsg),
						"</div>",
						"<div id='ZLoginLoadingPanel' ", (params.showLoading ? "" : "style='display:none'"), ">",
							"<table><tr><td>[icon]</td><td id='ZLoginLoadingMsg'>", (params.loadingMsg || defaults.loadingMsg), "</td></tr></table>",
						"</div>",
						"<div id='ZLoginFormPanel' ", (params.showForm ? "" : "style='display:none'"), ">",
							"<table class='zLoginTable' width='100%' cellpadding=4>",
								"<tr id='ZLoginUserTr'", (params.showUserField ? "" : "style='display:none'"), "><td class='zLoginLabelContainer'>", (params.userNameMsg || defaults.userNameMsg), "</td>",
									"<td class='zLoginFieldContainer' colspan=2>",
										"<input id='uname' class='zLoginField' autocomplete=OFF type=text tabIndex=1>",
									"</td>",
								"</tr>",
								"<tr id='ZLoginPasswordTr' ", (params.showPasswordField ? "" : "style='display:none'"), "><td class='zLoginLabelContainer'>", (params.passwordMsg || defaults.passwordMsg), "</td>",
									"<td class='zLoginFieldContainer' colspan=2>",
										"<input id='pass' class='zLoginField' autocomplete=OFF type=password tabIndex=2>",
									"</td>",
								"</tr>",
								"<tr id='ZLoginNewPassword1Tr' ", (params.showNewPasswordFields ? "" : "style='display:none'"), "><td class='zLoginLabelContainer'>", (params.newPassword1Msg || defaults.newPassword1Msg), "</td>",
									"<td class='zLoginFieldContainer' colspan=2>",
										"<input id='newpass1' class='zLoginField' autocomplete=OFF type=password tabIndex=2>",
									"</td>",
								"</tr>",
								"<tr id='ZLoginNewPassword2Tr' ", (params.showNewPasswordFields ? "" : "style='display:none'"), "><td class='zLoginLabelContainer'>", (params.newPassword2Msg || defaults.newPassword2Msg), "</td>",
									"<td class='zLoginFieldContainer' colspan=2>",
										"<input id='newpass2' class='zLoginField' autocomplete=OFF type=password tabIndex=2>",
									"</td>",
								"</tr>",
								"<tr id='ZLoginLicenseMsgContainer' ", (params.showLicenseMsg ? "" : "style='display:none'"), ">",
									"<td colspan=3 id='ZLoginLicenseMsg'>", (params.licenseMsg || defaults.licenseMsg), "</td>",
								"</tr>",
								"<tr><td class='zLoginLabelContainer'>&nbsp;</td>",
									"<td class='zLoginFieldContainer' id='ZLoginRememberMeContainer' ", (params.showRememberMeCheckbox ? "" : "style='display:none'"), ">",
										"<table class='zLoginTable' width=100%>",
											"<tr><td width=1><input id='rememberMe' type='checkbox'></td>",
												"<td class='zLoginCheckboxLabelContainer'>", (params.rememberMeMsg || defaults.rememberMeMsg), "</td>",
											"</tr>",
										"</table>",
									"</td>",
									"<td class='zLoginButtonContainer' align='right'", (params.showButton ? "" : "style='display:none'"), ">",
										"<div id='ZLoginButton' class='DwtButton'",
											"onclick='", (params.loginAction || defaults.loginAction), ";return false'",
											"onmouseover='javascript:this.className=\"DwtButton-activated\"'",
											"onmouseout='javascript:this.className=\"DwtButton\"'",
											"onmousedown='javascript:this.className=\"DwtButton-triggered\";return false'",
											"onmouseup='javascript:this.className=\"DwtButton\"'",
											"onmousemove='javascript:return false'",
											"onselectstart='javascript: return false'",
											"onfocus='javascript:this.parentNode.className = \"focusBorder\";return false,'",
											"onnblur='javascript:this.parentNode.className = \"\";return false'",
										"><table style='width:100%;height:100%' cellspacing=0>",
											"<tr><td align='center' class='Text' id='ZLoginButtonText'>",
													(params.buttonName || defaults.buttonName),
											"</td></tr>",
										"</table></div>",
										"<!-- non-IE browsers dont allow focus for non-INPUT elements so we have to",
											" create a hidden input to fake focus for our DIV which acts as an input button -->",
										"<input type='button' style='display:none' id='hiddenButton'>",
									"</td>",
								"</tr>",
							"</table>",
						"</div>",
					"</td>",
				"</tr>",
				"<tr><td id='ZLoginLicenseContainer'>",
						(params.copyrightText || defaults.copyrightText),
					"</td>",
				"</tr>",
			"</table>",
		 "</div>"
	].join("");
	return html;
}


// simple API to show/hide elements (can be replaced with Dwt if desired)
ZLoginFactory.setHTML = function (id, newContent) {
	var el = document.getElementById(id);
	if (el && newContent != null) el.innerHTML = newContent;
}

ZLoginFactory.show = function (id, newContent) {
	var el = document.getElementById(id);
	if (el) el.style.display = "";
}

ZLoginFactory.hide = function (id) {
	var el = document.getElementById(id);
	if (el) el.style.display = "none";
}
