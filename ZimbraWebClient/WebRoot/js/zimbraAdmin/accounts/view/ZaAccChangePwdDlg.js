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
 * Portions created by Zimbra are Copyright (C) 2004, 2005, 2006 Zimbra, Inc.
 * All Rights Reserved.
 * 
 * Contributor(s):
 * 
 * ***** END LICENSE BLOCK *****
 */

function ZaAccChangePwdDlg(parent,  app) {
	DwtDialog.call(this, parent, null, ZaMsg.CHNP_Title);
	this._fieldIds = new Object();
	this._app = app;
	this.setContent(this._contentHtml());
	/*this.setTabOrder([this._fieldIds[ZaAccChangePwdDlg.F_password], this._fieldIds[ZaAccChangePwdDlg.F_confirmPassword], 
					  this._fieldIds[ZaAccChangePwdDlg.F_zimbraPasswordMustChange]]);*/
	
}

ZaAccChangePwdDlg.prototype = new DwtDialog;
ZaAccChangePwdDlg.prototype.constructor = ZaAccChangePwdDlg;

ZaAccChangePwdDlg.F_password = 1;
ZaAccChangePwdDlg.F_confirmPassword = 2;
ZaAccChangePwdDlg.F_zimbraPasswordMustChange = 3;

ZaAccChangePwdDlg.prototype.toString = 
function() {
	return "ZaAccChangePwdDlg";
}

ZaAccChangePwdDlg.prototype.popdown = 
function() {
	DwtDialog.prototype.popdown.call(this);
	if(this._app) {
		this._app.getCurrentController().setEnabled(true);	
	}
}

ZaAccChangePwdDlg.prototype.popup =
function(mustChange) {
	DwtDialog.prototype.popup.call(this);
	var ePassword = document.getElementById(this._fieldIds[ZaAccChangePwdDlg.F_password]);
	ePassword.focus();
	if(this._app) {
		this._app.getCurrentController().setEnabled(false);	
	}
	var eField = document.getElementById(this._fieldIds[ZaAccChangePwdDlg.F_zimbraPasswordMustChange]);
	if(!eField)
		return true;
		
	if(mustChange && mustChange == "TRUE") 
		eField.checked = true;
	else
		eField.checked = false;
}

ZaAccChangePwdDlg.prototype.getPassword = 
function () {
	var ePassword = document.getElementById(this._fieldIds[ZaAccChangePwdDlg.F_password]);
	if(ePassword) {
		return ePassword.value;
	}
}

ZaAccChangePwdDlg.prototype.getMustChangePassword = 
function () {
	var eField = document.getElementById(this._fieldIds[ZaAccChangePwdDlg.F_zimbraPasswordMustChange]);
	if(eField) {
		if(eField.checked) {
			return true;
		} else {
			return false;
		}
	} else return false;
}

ZaAccChangePwdDlg.prototype.getConfirmPassword = 
function () {
	var eConfPassword = document.getElementById(this._fieldIds[ZaAccChangePwdDlg.F_confirmPassword]);
	if(eConfPassword) {
		return eConfPassword.value;
	}

}

ZaAccChangePwdDlg.prototype._addEntryRow =
function(field, title, html, idx, type) {
	if (type == null) type = "text";
	var id = Dwt.getNextId();
	this._fieldIds[field] = id;
	html[idx++] = "<tr valign='center'>";
	html[idx++] = "<td width='30%' align='left'>";
	html[idx++] = title;
	html[idx++] = "</td>";
	html[idx++] = "<td width='70%' align='left'><input autocomplete='off' style='width:100%;' type='"+type+"' id='";	
	html[idx++] = id;
	html[idx++] = "'/>";
	html[idx++] = "</td></tr>";
	return idx;
}

ZaAccChangePwdDlg.prototype._addEntryRow2 =
function(field, title, html, idx) {
	var id = Dwt.getNextId();
	this._fieldIds[field] = id;
	html[idx++] = "<tr valign='center'>";
	html[idx++] = "<td colspan='2' align='left'><nobr><input type='checkbox' id='";	
	html[idx++] = id;
	html[idx++] = "'/>&nbsp;";
	html[idx++] = title;
	html[idx++] = "</nobr></td></tr>";
	return idx;
}

ZaAccChangePwdDlg.prototype._createPwdHtml =
function(html, idx) {
	html[idx++] = "<table cellpadding='3' cellspacing='2' border='0' width='100%'>";
	idx = this._addEntryRow(ZaAccChangePwdDlg.F_password, ZaMsg.NAD_Password, html, idx, "password");
	idx = this._addEntryRow(ZaAccChangePwdDlg.F_confirmPassword, ZaMsg.NAD_ConfirmPassword, html, idx, "password");
	idx = this._addEntryRow2(ZaAccChangePwdDlg.F_zimbraPasswordMustChange, ZaMsg.NAD_MustChangePwd, html, idx);	
	html[idx++] = "</table>";
	return idx;
}


ZaAccChangePwdDlg.prototype._contentHtml = 
function() {
	this._nameFieldId = Dwt.getNextId();
	var html = new Array();
	var idx = 0;
	html[idx++] = "<div class='ZaChngPwdDlg'>";
	idx = this._createPwdHtml(html, idx);
	html[idx++] = "</div>";
	return html.join("");
}

