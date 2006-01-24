/*
 * ***** BEGIN LICENSE BLOCK *****
 * Version: ZPL 1.1
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.1 ("License"); you may not use this file except in
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
 * Portions created by Zimbra are Copyright (C) 2005 Zimbra, Inc.
 * All Rights Reserved.
 * 
 * Contributor(s):
 * 
 * ***** END LICENSE BLOCK *****
 */

function ZaLoginDialog(parent, zIndex, className) { 

    className = className || "ZaLoginDialog";
    DwtComposite.call(this, parent, className, DwtControl.ABSOLUTE_STYLE);

    this._origClassName = className;
    this._xparentClassName = className + "-Transparent";
    this.setBounds(0, 0, "100%", "100%");
    var htmlElement = this.getHtmlElement();
    htmlElement.style.zIndex = zIndex || Dwt.Z_DIALOG;
    htmlElement.className = className;
    this.setVisible(false);
    var unameId = Dwt.getNextId();
	var pwordId = Dwt.getNextId();
	var okCellId = Dwt.getNextId();
	var errorCellId = Dwt.getNextId();
	var licenseCellId = Dwt.getNextId();
	var tableId = Dwt.getNextId();
	var bannerCellId = Dwt.getNextId();
	this.hiddenBtnId = Dwt.getNextId();
    var form = document.createElement("form");
    form.innerHTML = this._createHtml(unameId, pwordId, okCellId, errorCellId, licenseCellId,tableId,bannerCellId);
    htmlElement.appendChild(form);
    this._errorCell = document.getElementById(errorCellId);
    
   	document._parentId = this._htmlElId;
	
	this._mainTable = document.getElementById(tableId);
	this._bannerCell = document.getElementById(bannerCellId);
   	this._unameField = document.getElementById(unameId);
    this._unameField._parentId = this._htmlElId;
    this._unameField.onfocus = ZaLoginDialog.handleFieldFocus;
    
    this._pwordField = document.getElementById(pwordId);
    this._pwordField._parentId = this._htmlElId;
    this._pwordField.onfocus = ZaLoginDialog.handleFieldFocus;
    
    this._loginButton = new DwtButton(this, "", "ZaLoginButton");
    this._loginButton.setText(ZaMsg.login);
    this._loginButton.setData("me", this);
    this._loginButton.addSelectionListener(new AjxListener(this, this._loginSelListener));
    
    this._okCell = document.getElementById(okCellId);
	if (!AjxEnv.isIE){
		this._hiddenBtn = document.createElement('input');
		this._hiddenBtn.type='submit';
		this._hiddenBtn.style.display="none";
		this._hiddenBtn.id = this.hiddenBtnId;
		this._hiddenBtn._parentId = this._htmlElId;
		this._okCell.appendChild(this._hiddenBtn);

	}    
	this._okCell.appendChild(this._loginButton.getHtmlElement());
	this._licenseCell = document.getElementById(licenseCellId);
	this.resetLicenseWarning();
}

ZaLoginDialog.prototype = new DwtComposite;
ZaLoginDialog.prototype.constructor = ZaLoginDialog;

ZaLoginDialog.prototype.toString = 
function() {
	return "ZaLoginDialog";
}

ZaLoginDialog.prototype.getBannerHtml = function () {
	var dateFormatter = AjxDateFormat.getDateInstance();
	var substitutions = {
		url:ZaMsg.splashScreenZimbraUrl,
		appName: ZaMsg.splashScreenAppName,
		version: AjxBuffer.concat(ZaMsg.splashScreenVersion, " ", ZaServerVersionInfo.version , " " ,
									  dateFormatter.format(ZaServerVersionInfo.buildDate)),
		license: ZaMsg.splashScreenCopyright	
	}
	return DwtBorder.getBorderHtml("LoginBanner", substitutions);	
}

ZaLoginDialog.prototype.setUpKeyHandlers = 
function () {
	this.handleKeyBoard = true;
  	if (AjxEnv.isIE) {
  		document.onkeydown = ZaLoginDialog._keyPressHdlr;
  		this._unameField.onkeydown = ZaLoginDialog._keyPressHdlr;
    	this._pwordField.onkeydown = ZaLoginDialog._keyPressHdlr;
    	if(this.newPassInput)
			this.newPassInput.onkeydown = ZaLoginDialog._keyPressHdlr;
		if(this.confirmPassInput)
			this.confirmPassInput.onkeydown = ZaLoginDialog._keyPressHdlr;    	
	} else {
		window.onkeypress = ZaLoginDialog._keyPressHdlr;	
	  	this._unameField.onkeypress = ZaLoginDialog._keyPressHdlr;	
    	this._pwordField.onkeypress = ZaLoginDialog._keyPressHdlr;
		this._hiddenBtn.onkeypress = ZaLoginDialog._keyPressHdlr;	
    	if(this.newPassInput)
			this.newPassInput.onkeypress = ZaLoginDialog._keyPressHdlr;
		if(this.confirmPassInput)
			this.confirmPassInput.onkeypress = ZaLoginDialog._keyPressHdlr;  			   	
	}
}

ZaLoginDialog.prototype.clearKeyHandlers = 
function () {
	this.handleKeyBoard = false;
  	if (AjxEnv.isIE) {
  		document.onkeydown = null;
  		this._unameField.onkeydown = null;
    	this._pwordField.onkeydown = null;
    	if(this.newPassInput)
			this.newPassInput.onkeydown = null;
		if(this.confirmPassInput)
			this.confirmPassInput.onkeydown = null;       	
	} else {
		window.onkeypress = null;	
	  	this._unameField.onkeypress = null;	
    	this._pwordField.onkeypress = null;
		this._hiddenBtn.onkeypress = null;
    	if(this.newPassInput)
			this.newPassInput.onkeypress = null;
		if(this.confirmPassInput)
			this.confirmPassInput.onkeypress = null;  				   	
	}
}


ZaLoginDialog.handleFieldFocus = 
function(ev) {
	var obj = DwtUiEvent.getTarget(ev);
	var parent = Dwt.getObjectFromElement(document.getElementById(obj._parentId));
	parent._loginButton.setActivated(false);	
}



ZaLoginDialog.prototype.registerCallback =
function(func, obj) {
	this._callback = new AjxCallback(obj, func);
}

ZaLoginDialog.prototype.registerChangePassCallback =
function(func, obj) {
	this._changePasswordCallback = new AjxCallback(obj, func);
}

ZaLoginDialog.prototype.clearAll =
function() {
	this._unameField.value = this._pwordField.value = "";
	this.resetLicenseWarning();
}

ZaLoginDialog.prototype.resetLicenseWarning =
function () {
	if (ZaServerVersionInfo.licenseExists) {
		this._licenseCell.style.visibility = "visible";
		var licenseInfoText = null;
		if (ZaServerVersionInfo.licenseExpired) {
			this._licenseCell.className = "loginLicenseExpired";
			licenseInfoText = ZaMsg.licenseExpired;
		} else {
			this._licenseCell.className = "loginLicenseWillExpire";
			licenseInfoText = ZaMsg.licenseWillExpire;
		}
		this._licenseCell.innerHTML = AjxBuffer.concat(licenseInfoText," ",
													   AjxDateUtil.getTimeStr(ZaServerVersionInfo.licenseExpirationDate, 
																			  ZaMsg.loginLicenseExpiredDateFormat));

	} else {
		this._licenseCell.style.visibility = "hidden";
	}
};

ZaLoginDialog.prototype.clearPassword =
function() {
	this._pwordField.value = "";
}

ZaLoginDialog.prototype.setError =
function(errorStr) {
	this.setCursor("default");
	var html;
	if (errorStr && errorStr.length) {
		var htmlArr = new Array();
		var i = 0;
		htmlArr[i++] = "<table cellspacing='12' class='" + this._className + "-ErrorPanel'>";
		htmlArr[i++] = "<tr><td class='" + this._className + "-ErrorIcon'>";
		htmlArr[i++] = AjxImg.getImageHtml("Critical_32");						
		htmlArr[i++] = "</td>";
		htmlArr[i++] = "<td class='" + this._className + "-ErrorText'>" + errorStr + "</td></tr></table>";
		html = htmlArr.join("");
	} else {
		html = "&nbsp;";
	}
	this._errorCell.innerHTML = html;
	//have to do this, because IE does not reposition the divs:
	this._bannerCell.innerHTML = this.getBannerHtml();
}

ZaLoginDialog.prototype.disablePasswordField = 
function (disabled) {
	this._pwordField.disabled = disabled;
}

ZaLoginDialog.prototype.disableUnameField = 
function (disabled) {
	this._unameField.disabled = disabled;
}

ZaLoginDialog.prototype.setFocus =
function(bReloginMode) {
	if (this._unameField.value.length > 0) {
		this._pwordField.focus();
	} else {
		// if we're in relogin mode but cant find a username, 
		// throw exception to force new login
		this._unameField.disabled = false;
	    this._unameField.focus();
	}
 }

ZaLoginDialog.prototype.setVisible = 
function(visible, transparentBg) {
	DwtComposite.prototype.setVisible.call(this, visible);
	Dwt._ffOverflowHack(this._htmlElId, this.getZIndex(), visible);	
	if (!visible)
		return;
		
	this.setCursor("default");
	if ((transparentBg == null || !transparentBg) && this._className != this._origClassName) {
		this.getHtmlElement().className = this._origClassName;
		this._className = this._origClassName;
	} else if (transparentBg && this._className != this._xparentClassName) {
		this.getHtmlElement().className = this._xparentClassName;
		this._className = this._xparentClassName;
	}

}

ZaLoginDialog.prototype._createHtml = 
function(unameId, pwordId, okCellId, errorCellId, licenseCellId,tableId,bannerCellId) {
	var html = new Array();
	var i = 0;

	html[i++] = "<table  align=center valign=middle width=100% height=100% border=0 cellspacing=0 cellpadding=0><tr><td align=center width=100%>";
	html[i++] = "<table cellspacing=0 cellpadding=0 class='" + this._className + "-TopPanel'>";
 	html[i++] = "<tr><td  id='" + bannerCellId + "'>";
 	html[i++] = this.getBannerHtml();
 	html[i++] = "</tr></td>";
 	html[i++] = "<tr><td>";
 	html[i++] = "<table cellspacing=12 class='" + this._className + "-MainPanel' id='" + tableId + "'>";
 	html[i++] = "<colgroup><col style='width:75px'></col><col style='width:225px'></col></colgroup>";
	html[i++] = "<tr><td colspan=2 id='" + errorCellId + "'>&nbsp;</td></tr>";
	html[i++] = "<tr><td align=right>" + ZaMsg.username + ":</td>";
	html[i++] = "<td><input style=\"width:100%; height:22px\" autocomplete=OFF type=text tabIndex=1 id='" + unameId + "'/></td></tr>";	
	html[i++] = "<tr><td align=right>" + ZaMsg.password + ":</td>";
	html[i++] = "<td><input style=\"width:100%; height:22px\" type=password tabIndex=2 id='" + pwordId + "'/></td></tr>";	
	html[i++] = "<tr><td colspan=2><table cellpadding=0 cellspacing=0 border=0 width=100%>";
	html[i++] = "<td id='" + licenseCellId + "' style='visibility:hidden;'></td>";
	html[i++] = "<td id='" + okCellId + "' align=right></td></tr></table>";
	html[i++] = "</td></tr><tr><td colspan=2 style='Xborder:1px solid #eeeeee;font-size:9px;color:#999999;'>" + ZaMsg.splashScreenCopyright + "</td></tr></table>";
	html[i++] = "</td></tr></table>";
	html[i++] = "</td></tr></table>";
	return html.join("");
}

ZaLoginDialog.prototype.showChangePass = 
function(ex) {
	if(this.changePass)
		return;
		
	this.setError(ZaMsg.errorPassChange, true);

	// add new password fields
	var newPassRow = this._mainTable.insertRow(3);
	var newPassMsg = newPassRow.insertCell(-1);
	var newPassFld = newPassRow.insertCell(-1);
	newPassRow.style.height = "30";
	newPassMsg.align = "right";
	newPassMsg.innerHTML = ZaMsg.newPassword + ":";
	newPassFld.innerHTML = "<input tabindex=10 style='width:100%' type=password id='passNew'>";
	
	// add confirm password fields
	var conPassRow = this._mainTable.insertRow(4);
	var conPassMsg = conPassRow.insertCell(-1);
	var conPassFld = conPassRow.insertCell(-1);
	conPassRow.style.height = "30";
	conPassMsg.align = "right";
	conPassMsg.innerHTML = ZaMsg.confirm + ":";
	conPassFld.innerHTML = "<input tabindex=10 style='width:100%' type=password id='passConfirm'>";
	
	// set focus to the new password field
	this.newPassInput = document.getElementById("passNew");
	this.confirmPassInput = document.getElementById("passConfirm");
	this.newPassInput._parentId = this._htmlElId;
	this.confirmPassInput._parentId = this._htmlElId;
	this.newPassInput.focus();
	this.changePass = true;
	this.setUpKeyHandlers();
};

ZaLoginDialog.prototype.removeChangePass = 
function () {
	this.newPassInput = null;
	this.confirmPassInput = null;
	this._mainTable.deleteRow(3);
	this._mainTable.deleteRow(3); 
	this.changePass = false;
	this.clearKeyHandlers();
	this.setUpKeyHandlers();
}

ZaLoginDialog.prototype.addChild =
function(child, childHtmlElement) {
    this._children.add(child);
}

ZaLoginDialog.prototype.setReloginMode = 
function(bReloginMode, app, obj) {

	this._unameField.disabled = bReloginMode;
}

ZaLoginDialog.prototype._loginSelListener =
function(selEvt) {
	this.setCursor("wait");
	var username = this._unameField.value;
	if (!(username && username.length)) {
		this.setError(ZaMsg.enterUsername);
		return;
	}

	// check if we're trying to change the password
	if (this._unameField.disabled && this._pwordField.disabled) {
		if(!this.newPassInput) {
			this.newPassInput = document.getElementById("passNew");
			this.newPassInput._parentId = this._htmlElId;
		}
		if(!this.confirmPassInput) {
			this.confirmPassInput = document.getElementById("passConfirm");
			this.confirmPassInput._parentId = this._htmlElId;			
		}
		if (this._changePasswordCallback && this.newPassInput && this.confirmPassInput)
			this._changePasswordCallback.run(username, this._pwordField.value, this.newPassInput.value,this.confirmPassInput.value);
	} else {
		if (this._callback)
			this._callback.run(username, this._pwordField.value);
	
	}
}

ZaLoginDialog.prototype.runCallBack = 
function () {
	var username = this._unameField.value;
	if (!(username && username.length)) {
		this.setError(ZaMsg.enterUsername);
		return;
	}
	// check if we're trying to change the password
	if (this._unameField.disabled && this._pwordField.disabled) {
		if(!this.newPassInput) {
			this.newPassInput = document.getElementById("passNew");
			this.newPassInput._parentId = this._htmlElId;			
		}
		if(!this.confirmPassInput) {
			this.confirmPassInput = document.getElementById("passConfirm");	
			this.confirmPassInput._parentId = this._htmlElId;			
		}
		if (this._changePasswordCallback && this.newPassInput && this.confirmPassInput)
			this._changePasswordCallback.run(username, this._pwordField.value, this.newPassInput.value,this.confirmPassInput.value);
	} else {
		if (this._callback)
			this._callback.run(username, this._pwordField.value);
	
	}
}


// -----------------------------------------------------------------
// event handler methods
// -----------------------------------------------------------------

ZaLoginDialog._keyPressHdlr =
function(evt) {
	evt = (evt) ? evt : ((event) ? event : null);
	var charCode = DwtKeyEvent.getCharCode(evt);
	var shiftKey = evt.shiftKey;

	var obj = DwtUiEvent.getTarget(evt);
	var doc = obj.document ? obj.document : ((obj.ownerDocument)? obj.ownerDocument : window.document);
	var parent = null;
	if(obj._parentId)
		parent = Dwt.getObjectFromElement(document.getElementById(obj._parentId));
	else 
		parent = Dwt.getObjectFromElement(document.getElementById(doc._parentId));
	
	if(!parent || !parent.handleKeyBoard)
		return;
	
	if (charCode == 13 || charCode == 3) {
		if (obj == parent._unameField) {
			if(!parent._pwordField.disabled)
				parent._pwordField.focus();
		} else {
			parent.runCallBack();
			/*	if (parent._callback) {
				parent.setCursor("wait");
				parent._callback.run(parent._unameField.value, parent._pwordField.value);
			}
			*/
		}
		return false;
	} else if (charCode == 9) { //TAB
		if (obj == parent._unameField) {
			if(!shiftKey)
				if(!parent._pwordField.disabled)
					parent._pwordField.focus();
			else {
				parent._loginButton.setActivated(true);
				if (AjxEnv.isIE)
	 			    parent._loginButton.getHtmlElement().focus();
				else
					parent._hiddenBtn.focus();
			}
		} else if (obj == parent._pwordField){
			if(!shiftKey) {
				parent._loginButton.setActivated(true);
				if (AjxEnv.isIE)
	 			    parent._loginButton.getHtmlElement().focus();
				else
					parent._hiddenBtn.focus();
			} else {
				if(!parent._unameField.disabled)
					parent._unameField.focus();
			}
		} else if(parent.newPassInput && obj == parent.newPassInput) {
			if(!shiftKey && parent.confirmPassInput)
				parent.confirmPassInput.focus();
			else {
				if (AjxEnv.isIE)
	 			    parent._loginButton.getHtmlElement().focus();
				else
					parent._hiddenBtn.focus();
			}		
		} else if(parent.confirmPassInput && obj == parent.confirmPassInput) {
			if(!shiftKey) {
				if (AjxEnv.isIE)
	 			    parent._loginButton.getHtmlElement().focus();
				else
					parent._hiddenBtn.focus();
			} else if (parent.newPassInput) {
				parent.newPassInput.focus();
			}		
		} else {
			parent._loginButton.setActivated(false);
			if(!shiftKey) {
				if(!parent._unameField.disabled)
					parent._unameField.focus();
			} else {
				if(!parent._pwordField.disabled)
					parent._pwordField.focus();
			}
		} 
		ZaLoginDialog.cancelEvent(evt);
		return false; 
	} 
	return true;
}

ZaLoginDialog.cancelEvent = function (ev){
	if (ev.stopPropagation){
		ev.stopPropagation();
	}
	if (ev.preventDefault){
		ev.preventDefault();
	}
	ev.cancelBubble = true;
	ev.returnValue = false;
}

ZaLoginDialog._loginDiffListener =
function(ev) {
	ZaZimbraAdmin.logOff();
	return;
};
