/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2006, 2007, 2009, 2010 Zimbra, Inc.
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

ZLoginFactory = function() {}

ZLoginFactory.USER_ID = "ZLoginUserName";
ZLoginFactory.PASSWORD_ID = "ZLoginPassword";
ZLoginFactory.REMEMBER_ME_ID = "rememberMe";
ZLoginFactory.REMEMBER_ME_CONTAINER_ID = "ZLoginRememberMeContainer"
ZLoginFactory.NEW_PASSWORD_ID = "newpass1";
ZLoginFactory.NEW_PASSWORD_TR_ID = "ZLoginNewPassword1Tr";
ZLoginFactory.PASSWORD_CONFIRM_TR_ID = "ZLoginNewPassword2Tr";
ZLoginFactory.PASSWORD_CONFIRM_ID = "newpass2";
ZLoginFactory.LOGIN_BUTTON_ID = "ZLoginButton";
ZLoginFactory.HIDDEN_BUTTON_ID = "ZLoginHiddenButton";

// Constants for tabbing through the login controls.
ZLoginFactory.TEXT_TYPE = 0;
ZLoginFactory.CHECKBOX_TYPE = 1;
ZLoginFactory.BUTTON_TYPE = 2;

ZLoginFactory.TAB_ORDER = [ZLoginFactory.USER_ID, ZLoginFactory.PASSWORD_ID, 
					 ZLoginFactory.NEW_PASSWORD_ID, ZLoginFactory.PASSWORD_CONFIRM_ID,
					 ZLoginFactory.REMEMBER_ME_ID, ZLoginFactory.LOGIN_BUTTON_ID];
ZLoginFactory.VISIBILITY = [ZLoginFactory.USER_ID, ZLoginFactory.PASSWORD_ID, 
					  ZLoginFactory.NEW_PASSWORD_TR_ID, ZLoginFactory.PASSWORD_CONFIRM_TR_ID,
					  ZLoginFactory.REMEMBER_ME_CONTAINER_ID, ZLoginFactory.LOGIN_BUTTON_ID];
ZLoginFactory.TAB_TYPE = [ZLoginFactory.TEXT_TYPE, ZLoginFactory.TEXT_TYPE, 
					ZLoginFactory.TEXT_TYPE, ZLoginFactory.TEXT_TYPE,
					ZLoginFactory.CHECKBOX_TYPE, ZLoginFactory.BUTTON_TYPE];
				
/*
 * Creates a copy of the default login parameters.
 * 
 * @param msgs	The class where localized messages are defined. ZmMsg for example.
 */
ZLoginFactory.copyDefaultParams = 
function(msgs) {
	return {
		showPanelBorder: true,
		
		companyURL : msgs["splashScreenCompanyURL"] || "",
	
		shortVersion : "",
		longVersion : "",
	
		appName : msgs["splashScreenAppName"] || "",
		productName : "",
	
		showError : false,
		errorMsg : "",
	
		showAbout : false,
		aboutMsg : "",
	
		showLoading : false,
		loadingMsg : msgs["splashScreenLoading"] || "",
		
		showForm : false,
		
		showUserField : false,
		userNameMsg : msgs["username"] ? msgs["username"] + ':' : "",
		
		showPasswordField : false,
		passwordMsg : msgs["password"] ? msgs["password"] + ':' : "",
		
		showNewPasswordFields : false,
		newPassword1Msg : msgs["newPassword"] + ':'|| "",
		newPassword2Msg : msgs["confirm"] + ':'|| "",
	
		showLicenseMsg : false,
		licenseMsg : "",
		
		showRememberMeCheckbox : false,
		rememberMeMsg : msgs["rememberMe"] || "",
	
		showLogOff : false,
		logOffMsg : msgs["loginAsDiff"] || "",
		logOffAction : "",
		
		showButton : false,
		buttonName : msgs["login"] || "",
		
		copyrightText : msgs["splashScreenCopyright"] || ""
	};
};

// show and hide various things
ZLoginFactory.getLoginPanel = function () 			{												
	var retval = this.get("ZLoginPanel");
	if(!retval)
		retval = this.get("ZLoginPanelInvisible");

	return retval;
	
}

ZLoginFactory.showErrorMsg = function (msg) {
	this.setHTML("ZLoginErrorMsg", msg);
	this.show("ZLoginErrorPanel");
	this._flickerErrorMessagePanel();
}
ZLoginFactory.hideErrorMsg = function () 			{												this.hide("ZLoginErrorPanel");	}
ZLoginFactory.getErrorMsgPanel = function () 		{												return this.get("ZLoginErrorPanel");	}

ZLoginFactory.showAboutMsg = function (msg) 		{	this.setHTML("ZLoginAboutPanel", msg);		this.show("ZLoginAboutPanel");	}
ZLoginFactory.hideAboutMsg = function () 			{												this.hide("ZLoginAboutPanel");	}
ZLoginFactory.getAboutMsg = function () 			{												return this.get("ZLoginAboutPanel");	}

ZLoginFactory.showLoadingMsg = function (msg)		{	this.setHTML("ZLoginLoadingMsg", msg);		this.show("ZLoginAboutPanel");	}
ZLoginFactory.hideLoadingMsg = function () 		{													this.hide("ZLoginAboutPanel");	}
ZLoginFactory.getLoadingMsg = function () 		{													return this.get("ZLoginAboutPanel");	}

ZLoginFactory.showForm = function ()				{												this.show("ZLoginFormPanel");	}
ZLoginFactory.hideForm = function () 				{												this.hide("ZLoginFormPanel");	}
ZLoginFactory.getForm = function () 				{												return this.get("ZLoginFormPanel");	}

ZLoginFactory.showUserField = function (name)		{	this.setValue(ZLoginFactory.USER_ID, name);				this.show(ZLoginFactory.USER_ID);	}
ZLoginFactory.hideUserField = function () 			{												this.hide(ZLoginFactory.USER_ID);	}
ZLoginFactory.getUserField = function () 			{												return this.get(ZLoginFactory.USER_ID);	}

ZLoginFactory.showPasswordField = function (msg)	{	this.show(ZLoginFactory.PASSWORD_ID);	}
ZLoginFactory.hidePasswordField = function () 		{	this.hide(ZLoginFactory.PASSWORD_ID);	}
ZLoginFactory.getPasswordField = function () 		{	return this.get(ZLoginFactory.PASSWORD_ID);	}

ZLoginFactory.showNewPasswordFields = function ()	{	this.show(ZLoginFactory.NEW_PASSWORD_TR_ID); this.show(ZLoginFactory.PASSWORD_CONFIRM_TR_ID);	}
ZLoginFactory.hideNewPasswordFields = function () 	{	this.hide(ZLoginFactory.NEW_PASSWORD_TR_ID); this.hide(ZLoginFactory.PASSWORD_CONFIRM_TR_ID);	}
ZLoginFactory.areNewPasswordFieldsShown = function (){	return this.isShown(ZLoginFactory.NEW_PASSWORD_TR_ID); }

ZLoginFactory.getNewPasswordField = function () 	{	return this.get(ZLoginFactory.NEW_PASSWORD_ID); }
ZLoginFactory.getPasswordConfirmField = function () {	return this.get(ZLoginFactory.PASSWORD_CONFIRM_ID); }

ZLoginFactory.showRememberMeCheckbox = function ()	{	this.show(ZLoginFactory.REMEMBER_ME_CONTAINER_ID);	}
ZLoginFactory.hideRememberMeCheckbox = function ()	{	this.hide(ZLoginFactory.REMEMBER_ME_CONTAINER_ID);	}

ZLoginFactory.showLogOff = function ()	{	this.show("ZLoginLogOffContainer");	}
ZLoginFactory.hideLogOff = function ()	{	this.hide("ZLoginLogOffContainer");	}

ZLoginFactory.setLoginButtonName = function (name) 	{	this.setHTML("ZLoginButtonText", name);	}
ZLoginFactory.setLoginButtonAction = function (method) {	var el = document.getElementById(ZLoginFactory.LOGIN_BUTTON_ID); if (el) el.onclick = method	}
ZLoginFactory.getLoginButton = function () 		{	return this.get(ZLoginFactory.LOGIN_BUTTON_ID);	}


ZLoginFactory.getLoginDialogHTML = function (params) {
	var html = [
		 "<div ", (params.showPanelBorder ? "id='ZLoginPanel'" :  "id='ZLoginPanelInvisible'"), ">",
			"<table class='zLoginTable' width='100%' cellpadding=0 cellspacing=0>",
				"<tr><td id='ZLoginHeaderContainer'><center>",
						"<table class='zLoginTable'>",
							"<tr><td id='ZLoginBannerContainer'>",
									"<div id='ZLoginBannerPanel'>",
										"<table class='zLoginTable'><tr>",
											"<td><div style='cursor:pointer' id='ZLoginBannerImage' class='ImgLoginBanner'", 
													" onclick='window.open(\"", params.companyURL, "\", \"_blank\")'></div></td>",
											"<td valign=top id='ZLoginShortVersion'>", params.shortVersion, "</td>",
										"</tr></table>",
										"<div id='ZLoginAppName'>", params.appName, "</div>",
										"<div id='ZLoginProductName'>", params.productName, "</div>",
										"<div id='ZLoginLongVersion'>", params.longVersion, "</div>",
									"</div>",
								"</td>",
							"</tr>",
						"</table>",
					"</center></td>",
				"</tr>",
				"<tr><td id='ZLoginBodyContainer'>",
						"<div id='ZLoginErrorPanel' ", (params.showError ? "" : "style='display:none'"), ">",
							"<table>",
								"<tr><td valign='top' width='40'><div class='ImgCritical_32'></td>",
									"<td width='*' id='ZLoginErrorMsg' class='errorText'>", params.errorMsg, "</td>",
								"</tr>",
							"</table>",
						"</div>",
						"",
						"<div id='ZLoginAboutPanel' ", (params.showAbout ? "" : "style='display:none'"), ">", params.aboutMsg,
						"</div>",
						"<div id='ZLoginLoadingPanel' ", (params.showLoading ? "" : "style='display:none'"), ">",
							"<table><tr><td></td><td id='ZLoginLoadingMsg'>", params.loadingMsg, "</td></tr></table>",
						"</div>",
						"<div id='ZLoginFormPanel' ", (params.showForm ? "" : "style='display:none'"), ">",
							"<table class='zLoginTable' width='100%' cellpadding=4>",
								"<tr id='ZLoginUserTr'", (params.showUserField ? "" : "style='display:none'"), "><td class='zLoginLabelContainer'>", params.userNameMsg, "</td>",
									"<td class='zLoginFieldContainer' colspan=2>",
										"<input id='", ZLoginFactory.USER_ID, "' class='zLoginField' autocomplete=OFF type=text tabIndex=1>",
									"</td>",
								"</tr>",
								"<tr id='ZLoginPasswordTr' ", (params.showPasswordField ? "" : "style='display:none'"), "><td class='zLoginLabelContainer'>", params.passwordMsg, "</td>",
									"<td class='zLoginFieldContainer' colspan=2>",
										"<input id='", ZLoginFactory.PASSWORD_ID, "' class='zLoginField' autocomplete=OFF type=password tabIndex=2>",
									"</td>",
								"</tr>",
								"<tr id='", ZLoginFactory.NEW_PASSWORD_TR_ID, "' ", (params.showNewPasswordFields ? "" : "style='display:none'"), "><td class='zLoginLabelContainer'>", params.newPassword1Msg, "</td>",
									"<td class='zLoginFieldContainer' colspan=2>",
										"<input id='", ZLoginFactory.NEW_PASSWORD_ID, "' class='zLoginField' autocomplete=OFF type=password tabIndex=2>",
									"</td>",
								"</tr>",
								"<tr id='", ZLoginFactory.PASSWORD_CONFIRM_TR_ID, "' ", (params.showNewPasswordFields ? "" : "style='display:none'"), "><td class='zLoginLabelContainer'>", params.newPassword2Msg, "</td>",
									"<td class='zLoginFieldContainer' colspan=2>",
										"<input id='", ZLoginFactory.PASSWORD_CONFIRM_ID, "' class='zLoginField' autocomplete=OFF type=password tabIndex=2>", 
									"</td>",
								"</tr>",
								"<tr id='ZLoginLicenseMsgContainer' ", (params.showLicenseMsg ? "" : "style='display:none'"), ">",
									"<td colspan=3 id='ZLoginLicenseMsg'>", params.licenseMsg, "</td>",
								"</tr>",
								"<tr><td class='zLoginLabelContainer'>&nbsp;</td>",
									"<td class='zLoginFieldContainer' id='", ZLoginFactory.REMEMBER_ME_CONTAINER_ID, "' ", (params.showRememberMeCheckbox ? "" : "style='display:none'"), ">",
										"<table class='zLoginTable' width=100%>",
											"<tr><td width=1><input id='", ZLoginFactory.REMEMBER_ME_ID, "' type='checkbox'></td>",
												"<td class='zLoginCheckboxLabelContainer'>", params.rememberMeMsg, "</td>",
											"</tr>",
										"</table>",
									"</td>",
									"<td class='zLoginFieldContainer' id='ZLoginLogOffContainer' ", (params.showLogOff ? "" : "style='display:none'"), ">",
										"<a href='' onclick='javascript:" + params.logOffAction + ";'>" + params.logOffMsg + "</a>",
									"</td>",
									"<td class='zLoginButtonContainer' align='right'", (params.showButton ? "" : "style='display:none'"), ">",
										"<div id='", ZLoginFactory.LOGIN_BUTTON_ID, "' class='DwtButton'",
											"onclick='", params.loginAction, ";return false'",
											"onmouseover='javascript:this.className=\"DwtButton-hover\"'",
											"onmouseout='javascript:this.className=\"DwtButton\"'",
											"onmousedown='javascript:this.className=\"DwtButton-active\";return false'",
											"onmouseup='javascript:this.className=\"DwtButton\"'",
											"onmousemove='javascript:return false'",
											"onselectstart='javascript: return false'",
											"onfocus='javascript:this.className = \"DwtButton-focused\";return false'",
											"onblur='javascript:this.className = \"DwtButton\";return false'",
										">",
											"<table style='width:100%;height:100%' cellspacing=0>",
												"<tr><td align='center' class='Text' id='ZLoginButtonText'>",
														params.buttonName,
												"</td></tr>",
											"</table>",
										"</div>",
										"<!-- non-IE browsers dont allow focus for non-INPUT elements so we have to",
											" create a hidden input to fake focus for our DIV which acts as an input button -->",
										"<input type='button' style='position:absolute;top:-10000;left:-10000;' id='", ZLoginFactory.HIDDEN_BUTTON_ID, "'>",
									"</td>",
								"</tr>",
							"</table>",
						"</div>",
						"<table class='zLoginTable' width='100%' cellpadding=0 cellspacing=0>",
							"<tr>",
								"<td id='ZloginClientLevelContainer'>",
									params.clientLevelNotice,
								"</td>",
							"</tr>",
							"<tr>",
								"<td id='ZLoginLicenseContainer'>",
									params.copyrightText,
								"</td>",
							"</tr>",
						"</table>",
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

ZLoginFactory.setValue = function (id, newContent) {
	var el = document.getElementById(id);
	if (el && newContent != null) el.value = newContent;
}

ZLoginFactory.show = function (id, newContent) {
	var el = document.getElementById(id);
	if (el) el.style.display = "";
}

ZLoginFactory.isShown = function (id) {
	var el = document.getElementById(id);
	return el ? (el.style.display != "none") : false;
}

ZLoginFactory.hide = function (id) {
	var el = document.getElementById(id);
	if (el) el.style.display = "none";
}

ZLoginFactory.get = function (id) {
	return document.getElementById(id);
}

ZLoginFactory.handleKeyPress =
function(ev) {
    ev = ev || window.event;
    if (ev == null) {
    	return true;
    }
    var target = ev.target ? ev.target: ev.srcElement;
    if (!target) {
    	return true;
    }
    var keyCode = ev.keyCode;
    var fakeTabKey = false;
    if (keyCode == 13) { // Enter
		if (target.id == ZLoginFactory.USER_ID || target.id == ZLoginFactory.NEW_PASSWORD_ID) {
			fakeTabKey = true;
		} else {
			// Call the login action
			var loginAction = ZLoginFactory.get(ZLoginFactory.LOGIN_BUTTON_ID).onclick;
			if (loginAction) {
				loginAction.call(target);
			}
			ZLoginFactory._cancelEvent(ev);
			return false;
		}
    }
	if (fakeTabKey || (keyCode == 9)) { // Tab
		var startIndex = ZLoginFactory.TAB_ORDER.length - 1;
		for (var i = 0; i < ZLoginFactory.TAB_ORDER.length; i++) {
			if (ZLoginFactory.TAB_ORDER[i] == target.id) {
				startIndex = i;
				break;
			}
		}
		var forward = !ev.shiftKey;
		var tabToIndex = ZLoginFactory._getTabToIndex(startIndex, forward);
		var tabToId = ZLoginFactory.TAB_ORDER[tabToIndex];
		var tabToType = ZLoginFactory.TAB_TYPE[tabToIndex];
		ZLoginFactory._onFocusChange(tabToType, tabToId, target);
		ZLoginFactory._cancelEvent(ev);
	}
}

// Private / protected methods

ZLoginFactory._cancelEvent =
function(ev) {
	if (ev.stopPropagation)
		ev.stopPropagation();

	if (ev.preventDefault)
		ev.preventDefault();

	ev.cancelBubble = true;
	ev.returnValue = false;
}

ZLoginFactory._onFocusChange =
function(type, id, target) {
	if ((type != ZLoginFactory.BUTTON_TYPE) && !AjxEnv.isIE) {
		ZLoginFactory._loginButtonBlur();
	}	
	if (type == ZLoginFactory.TEXT_TYPE) {
		var edit = ZLoginFactory.get(id);
		edit.focus();
		edit.select();
	} else if (type == ZLoginFactory.CHECKBOX_TYPE) {
		var checkbox = ZLoginFactory.get(id);
		checkbox.focus();
	}
	else {
		var button = ZLoginFactory.get(id);
		if (AjxEnv.isIE) {
			button.focus();
		} else {
			ZLoginFactory._loginButtonFocus(button);
			target.blur();
			document.getElementById(ZLoginFactory.HIDDEN_BUTTON_ID).focus();
		}
	}
};

ZLoginFactory._getTabToIndex =
function(startIndex, forward) {
	var testIndex = startIndex;
	do {
		var tabToIndex;
		if (forward) {
			testIndex = (testIndex == (ZLoginFactory.TAB_ORDER.length - 1)) ? 0 : testIndex + 1;
		} else {
			testIndex = (testIndex == 0) ? (ZLoginFactory.TAB_ORDER.length - 1) : testIndex - 1;
		}
		var id = ZLoginFactory.TAB_ORDER[testIndex];
		var visibilityId = ZLoginFactory.VISIBILITY[testIndex];
		var control = ZLoginFactory.get(id);
		if (ZLoginFactory.isShown(visibilityId) && !ZLoginFactory.get(id).disabled) {
			return testIndex
		}
	} while (testIndex != startIndex);
	return 0; // Should never get here.
}
					 
ZLoginFactory._loginButtonFocus =
function(border) {
	border.className = "DwtButton-focused";
};

ZLoginFactory._loginButtonBlur =
function(button) {
	var button = ZLoginFactory.get(ZLoginFactory.LOGIN_BUTTON_ID);
	button.className = "DwtButton";
};

/*
* Hide error panel very briefly, making it look like something happened if
* user has successive errors.
*/
ZLoginFactory._flickerErrorMessagePanel =
function() {
	ZLoginFactory.getErrorMsgPanel().style.visibility = "hidden";
	window.setTimeout(ZLoginFactory._showErrorMessagePanel, 8);
};

ZLoginFactory._showErrorMessagePanel =
function() {
	ZLoginFactory.getErrorMsgPanel().style.visibility = "visible";
};
