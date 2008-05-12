/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Web Client
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

ZaLoginDialog = function(parent, zIndex, className, appCtxt) {

    className = className || "ZaLoginDialog";
    DwtBaseDialog.call(this, parent, className, ZaMsg.login, zIndex);

	//license expiration warning won't show before login.
	//var licenseStatus = ZaZimbraAdmin.getLicenseStatus();
	var params = ZLoginFactory.copyDefaultParams(ZaMsg);
	params.showPanelBorder = false;
	params.showForm = true;
	params.showUserField = true;
	params.showPasswordField = true;
	//params.showLicenseMsg = licenseStatus.licenseExists;
	//params.licenseMsg = licenseStatus.message;
	params.showRememberMeCheckbox = false;
	params.showLogOff = true;
	params.logOffAction = "ZaLoginDialog._loginDiffListener()";
	params.loginAction = "ZaLoginDialog._loginListener(this)";
	params.showButton = true;
    params.companyURL = ZaAppCtxt.getLogoURI () ;
    var html = ZLoginFactory.getLoginDialogHTML(params);
	this.setContent(html);
}

ZaLoginDialog.prototype = new DwtBaseDialog;
ZaLoginDialog.prototype.constructor = ZaLoginDialog;

ZaLoginDialog.prototype.toString = 
function() {
	return "ZaLoginDialog";
}

ZaLoginDialog.prototype.registerCallback =
function(func, obj) {
	this._callback = new AjxCallback(obj, func);
}

ZaLoginDialog.prototype.clearPassword =
function() {
	ZLoginFactory.get(ZLoginFactory.PASSWORD_ID).value = "";
}

ZaLoginDialog.prototype.showNewPasswordFields = function () {
	ZLoginFactory.showNewPasswordFields();
}

ZaLoginDialog.prototype.hideNewPasswordFields = function () {
	ZLoginFactory.hideNewPasswordFields();
}

ZaLoginDialog.prototype.disableUnameField = function () {
	ZLoginFactory.get(ZLoginFactory.USER_ID).disabled = true;
}

ZaLoginDialog.prototype.disablePasswordField = function () {
	ZLoginFactory.get(ZLoginFactory.PASSWORD_ID).disabled = true;
}

ZaLoginDialog.prototype.setError =
function(errorStr) {
	if(errorStr)
		ZLoginFactory.showErrorMsg(errorStr);
}

ZaLoginDialog.prototype.setFocus =
function(username, bReloginMode) {
	ZLoginFactory.showUserField(username);
	this.setReloginMode(username && username.length && bReloginMode);
 }
 
ZaLoginDialog.prototype.popup =
function(loc,bReloginMode) {
	if (this._poppedUp) return;
	if(!bReloginMode)
		this.cleanup(true);
		
	var thisZ = this._zIndex;
	// if we're modal, setup the veil effect,
	// and track which dialogs are open
	if (this._mode == DwtBaseDialog.MODAL) {
		thisZ = this._setModalEffect(thisZ);
	}

	this._shell._veilOverlay.activeDialogs.push(this);

	// use whichever has a value, local has precedence
	if (loc) {
		this._loc.x = loc.x;
		this._loc.y = loc.y;
		this._positionDialog(loc);
	} else {
		this._positionDialog();
	}
	
	this.setZIndex(thisZ);
	this._poppedUp = true;
}


ZaLoginDialog.prototype.setVisible = 
function(visible, transparentBg,bReloginMode) {
	if (!!visible == this.isPoppedUp()) {
		return;
	}
	
	if (visible) {
		this.popup(null,bReloginMode);
	} else {
		this.popdown();
	}
	for (var i = 0; i < ZLoginFactory.TAB_ORDER.length; i++) {
		var element = document.getElementById(ZLoginFactory.TAB_ORDER[i]);
		if (visible) {
			Dwt.associateElementWithObject(element, this);
		} else {
			Dwt.disassociateElementFromObject(null, this);
		}
	}

	//Dwt.setHandler(this._getContentDiv(), DwtEvent.ONKEYDOWN, ZLoginFactory.handleKeyPress);
	if(visible) {
		if (AjxEnv.isIE) {
			var el = ZLoginFactory.getLoginPanel();
			el["onkeydown"] = ZLoginFactory.handleKeyPress;
		} else {
			window["onkeypress"] = ZLoginFactory.handleKeyPress;
		}
	}
	
	//set the focus on the user name field
	var userIdEl = ZLoginFactory.get(ZLoginFactory.USER_ID);
	if(!userIdEl.disabled)
		userIdEl.focus();
	//set the event handler for the user id field and password field\
	/* Use the Virtual host in the AuthRequest
	var userIdEl = ZLoginFactory.get(ZLoginFactory.USER_ID);
 	var passwdEl = ZLoginFactory.get(ZLoginFactory.PASSWORD_ID);
   	if (userIdEl) {
   		userIdEl.onblur= ZaLoginDialog.autoDomainName ; 
   	}
   	
   	if (passwdEl) {
   		passwdEl.onfocus = ZaLoginDialog.autoDomainName ;
   	}*/
}
/*
ZaLoginDialog.autoDomainName =
function (){
	var domainName = location.hostname ;
	var userIdEl = ZLoginFactory.get(ZLoginFactory.USER_ID);
	if (userIdEl && userIdEl.value != null && userIdEl.value.length > 0 ) {
		var u = userIdEl.value ;
		if (u.indexOf("@") < 0 ) { //no @
			DBG.println(AjxDebug.DBG3, "Auto append the domain name " + domainName + " to the user field.");
			userIdEl.value = u + "@" + domainName ;
			DBG.println(AjxDebug.DBG3, "Current user name = " + userIdEl.value) ;
		}
	}
}*/

ZaLoginDialog.prototype.addChild =
function(child, childHtmlElement) {
    this._children.add(child);
}

ZaLoginDialog.prototype.setReloginMode = 
function(bReloginMode) {
	if (bReloginMode) {
		ZLoginFactory.showLogOff();
		ZLoginFactory.get(ZLoginFactory.USER_ID).disabled = true;
	} else {
		ZLoginFactory.hideLogOff();
		ZLoginFactory.get(ZLoginFactory.USER_ID).disabled = false;
	}
	
}

ZaLoginDialog.prototype._loginSelListener =
function() {
	this.setCursor("wait");
	var username = ZLoginFactory.get(ZLoginFactory.USER_ID).value;
	if (!(username && username.length)) {
		this.setError(ZaMsg.enterUsername);
		return;
	}
	if (this._callback) {
		var password = ZLoginFactory.get(ZLoginFactory.PASSWORD_ID).value;
		var newPassword = "";
		var confPassword = "";
		if(ZLoginFactory.isShown(ZLoginFactory.NEW_PASSWORD_ID) && ZLoginFactory.isShown(ZLoginFactory.PASSWORD_CONFIRM_ID)) {
			newPassword = ZLoginFactory.get(ZLoginFactory.NEW_PASSWORD_ID).value;
			confPassword = ZLoginFactory.get(ZLoginFactory.PASSWORD_CONFIRM_ID).value; 
		}
			
		this._callback.run(username, password,newPassword,confPassword);		
	}
}

ZaLoginDialog._loginListener =
function(target) {
	var element = target;
	while (element) {
		var object = Dwt.getObjectFromElement(element);
		if (object instanceof ZaLoginDialog) {
			object._loginSelListener();
			break;
		}
		element = element.parentNode;
	}
};

ZaLoginDialog._loginDiffListener =
function(ev) {
	ZmZimbraMail.logOff();
};

