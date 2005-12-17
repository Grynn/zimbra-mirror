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

/**
* @class ZaGlobalConfigViewController 
* @contructor ZaGlobalConfigViewController
* @param appCtxt
* @param container
* @param app
* @author Greg Solovyev
**/
function ZaGlobalConfigViewController(appCtxt, container, app) {
	ZaXFormViewController.call(this, appCtxt, container, app,"ZaGlobalConfigViewController");
	this._evtMgr = new AjxEventMgr();
	this._UICreated = false;
	this._confirmMessageDialog;	
	this._helpURL = "/zimbraAdmin/adminhelp/html/WebHelp/managing_global_settings/global_settings.htm";			
	this.objType = ZaEvent.S_GLOBALCONFIG;
}

ZaGlobalConfigViewController.prototype = new ZaXFormViewController();
ZaGlobalConfigViewController.prototype.constructor = ZaGlobalConfigViewController;

//ZaGlobalConfigViewController.STATUS_VIEW = "ZaGlobalConfigViewController.STATUS_VIEW";

/**
* Adds listener to removal of an ZaDomain 
* @param listener
**/
ZaGlobalConfigViewController.prototype.addSettingsChangeListener = 
function(listener) {
	this._evtMgr.addListener(ZaEvent.E_MODIFY, listener);
}

ZaGlobalConfigViewController.prototype.show = 
function(item) {

	if(!this._UICreated) {
  		this._ops = new Array();
		this._ops.push(new ZaOperation(ZaOperation.SAVE, ZaMsg.TBB_Save, ZaMsg.ALTBB_Save_tt, "Save", "SaveDis", new AjxListener(this, this.saveButtonListener)));
		this._ops.push(new ZaOperation(ZaOperation.NONE));
		this._ops.push(new ZaOperation(ZaOperation.HELP, ZaMsg.TBB_Help, ZaMsg.TBB_Help_tt, "Help", "Help", new AjxListener(this, this._helpButtonListener)));							
		this._toolBar = new ZaToolBar(this._container, this._ops);
	
		this._view = new GlobalConfigXFormView(this._container, this._app);
		var elements = new Object();
		elements[ZaAppViewMgr.C_APP_CONTENT] = this._view;
		elements[ZaAppViewMgr.C_TOOLBAR_TOP] = this._toolBar;			
		this._app.createView(ZaZimbraAdmin._GLOBAL_SETTINGS,elements);
		this._UICreated = true;		
	}
	this._app.pushView(ZaZimbraAdmin._GLOBAL_SETTINGS);
	this._toolBar.getButton(ZaOperation.SAVE).setEnabled(false);  	
	try {		
		item[ZaModel.currentTab] = "1"
		this._view.setDirty(false);
		this._view.setObject(item);
	} catch (ex) {
		this._handleException(ex, "ZaGlobalConfigViewController.prototype.show", null, false);
	}
	this._currentObject = item;		
}


ZaGlobalConfigViewController.prototype.setEnabled = 
function(enable) {
	this._view.setEnabled(enable);
}

/**
* public getToolBar
* @return reference to the toolbar
**/
ZaGlobalConfigViewController.prototype.getToolBar = 
function () {
	return this._toolBar;	
}

ZaGlobalConfigViewController.prototype.setDirty = 
function (isD) {
	if(isD)
		this._toolBar.getButton(ZaOperation.SAVE).setEnabled(true);
	else
		this._toolBar.getButton(ZaOperation.SAVE).setEnabled(false);
}

ZaGlobalConfigViewController.prototype._saveChanges =
function () {
	var tmpObj = this._view.getObject();
	var isNew = false;
	if(tmpObj.attrs == null) {
		//show error msg
		this._errorDialog.setMessage(ZaMsg.ERROR_UNKNOWN, null, DwtMessageDialog.CRITICAL_STYLE, null);
		this._errorDialog.popup();		
		return false;	
	}

	//check values
	if(!AjxUtil.isNonNegativeInteger(tmpObj.attrs[ZaGlobalConfig.A_zimbraSmtpPort])) {
		//show error msg
		this._errorDialog.setMessage(ZaMsg.ERROR_INVALID_VALUE + ": " + ZaMsg.NAD_SmtpPort + " ! ", null, DwtMessageDialog.CRITICAL_STYLE, null);
		this._errorDialog.popup();		
		return false;
	}
		
	if(!AjxUtil.isNonNegativeInteger(tmpObj.attrs[ZaGlobalConfig.A_zimbraGalMaxResults])) {
		//show error msg
		this._errorDialog.setMessage(ZaMsg.ERROR_INVALID_VALUE + ": " + ZaMsg.NAD_GalMaxResults + " ! ", null, DwtMessageDialog.CRITICAL_STYLE, null);
		this._errorDialog.popup();		
		return false;
	}		
	
	// update zimbraMtaRestriction
	var restrictions = [];
	for (var i = 0; i < ZaGlobalConfig.MTA_RESTRICTIONS.length; i++) {
		var restriction = ZaGlobalConfig.MTA_RESTRICTIONS[i];
		if (tmpObj.attrs["_"+ZaGlobalConfig.A_zimbraMtaRestriction+"_"+restriction]) {
			restrictions.push(restriction);
		}			
	}
	var dirty = restrictions.length > 0;
	if (tmpObj.attrs[ZaGlobalConfig.A_zimbraMtaRestriction]) {
		var prevRestrictions = AjxUtil.isString(tmpObj.attrs[ZaGlobalConfig.A_zimbraMtaRestriction])
		                     ? [ tmpObj.attrs[ZaGlobalConfig.A_zimbraMtaRestriction] ]
		                     : tmpObj.attrs[ZaGlobalConfig.A_zimbraMtaRestriction];
		dirty = restrictions.length != prevRestrictions.length;
		if (!dirty) {
			for (var i = 0; i < prevRestrictions.length; i++) {
				var restriction = prevRestrictions[i];
				if (!tmpObj.attrs["_"+ZaGlobalConfig.A_zimbraMtaRestriction+"_"+restriction]) {
					dirty = true;
					break;
				}
			}
		}
	}
	if (dirty) {
		tmpObj.attrs[ZaGlobalConfig.A_zimbraMtaRestriction] = restrictions;
	}

	//transfer the fields from the tmpObj to the _currentObject, since _currentObject is an instance of ZaDomain
	var mods = new Object();
	for (var a in tmpObj.attrs) {
		if(a == ZaItem.A_objectClass || a == ZaGlobalConfig.A_zimbraAccountClientAttr || 
		a == ZaGlobalConfig.A_zimbraServerInheritedAttr || a == ZaGlobalConfig.A_zimbraDomainInheritedAttr ||
		a == ZaGlobalConfig.A_zimbraCOSInheritedAttr || a == ZaGlobalConfig.A_zimbraGalLdapAttrMap || 
		a == ZaGlobalConfig.A_zimbraGalLdapFilterDef || /^_/.test(a))
			continue;

		if (this._currentObject.attrs[a] != tmpObj.attrs[a] ) {
			mods[a] = tmpObj.attrs[a];
		}
	}
	//save the model
	var changeDetails = new Object();
	this._currentObject.modify(mods);
	
	/*
	// save server changes
	var originalServerId = tmpObj.attrs[ZaGlobalConfig.A_originalMonitorHost];
	var currentServerId = tmpObj.attrs[ZaGlobalConfig.A_currentMonitorHost];
	//DBG.println("original server id: "+originalServerId+", current server id: "+currentServerId);
	if (currentServerId && (currentServerId != originalServerId)) {
		function setMonitorHost(serverChoices, serverId, value) {
			var server = serverChoices ? serverChoices.getChoiceByValue(serverId) : null;
			if (server) {
				var mods = {};
				mods[ZaServer.A_zimbraIsMonitorHost] = value;
				server.modify(mods);
			}

		}

		var serverChoices = this._app.getServerListChoices();
		if (originalServerId) {
			// unset original server as monitor host
			setMonitorHost(serverChoices, originalServerId, '');
		}	
		setMonitorHost(serverChoices, currentServerId, 'TRUE');
	}
	*/
	//if modification took place - fire a Settings Change Event
	changeDetails["obj"] = this._currentObject;
	changeDetails["modFields"] = mods;
	this.fireChangeEvent(changeDetails);
	return true;
}
