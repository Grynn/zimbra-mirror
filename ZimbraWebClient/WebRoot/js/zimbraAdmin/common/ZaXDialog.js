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

/**
* @class 
* @contructor 
* @extends DwtDialog
* @author Greg Solovyev
* @param parent
* @param w (width)
* @param h (height)
**/

function ZaXDialog(parent, app, className, title, w, h,iKeyName) {
	if (arguments.length == 0) return;
	this._iKeyName = iKeyName;	
	var clsName = className || "DwtDialog";
	if(!this._standardButtons)
		this._standardButtons = [DwtDialog.OK_BUTTON];
	if(!this._extraButtons) {
		var helpButton = new DwtDialog_ButtonDescriptor(ZaXDialog.HELP_BUTTON, ZaMsg.TBB_Help, DwtDialog.ALIGN_LEFT, new AjxCallback(this, this._helpButtonListener));
		this._extraButtons = [helpButton];
	}
	DwtDialog.call(this, parent, clsName, title, this._standardButtons,this._extraButtons);
	this._app = app;
	this._localXForm = null;
	this._localXModel = null;
	this._drawn = false;
	this._containedObject = null;	

	if (!w) {
		this._contentW = "500px";
	} else {
		this._contentW = w;
	}
	
	if(!h) {
		this._contentH = "350px";
	} else {
		this._contentH = h;
	}		
	
	this._pageDiv = document.createElement("div");
	this._pageDiv.className = "ZaXWizardDialogPageDiv";
	this._pageDiv.style.width = this._contentW;
	this._pageDiv.style.height = this._contentH;
	this._pageDiv.style.overflow = "auto";
	this._pageDiv.style["overflow-y"] = "auto";
	this._pageDiv.style["overflow-x"] = "auto";	

	this._createContentHtml();
	this._helpURL = ZaXDialog.helpURL;	
}
ZA_BTN_INDEX = DwtDialog.NO_BUTTONS;
ZaXDialog.helpURL = "/zimbraAdmin/adminhelp/html/WebHelp/administration_console_help.htm";
ZaXDialog.prototype = new DwtDialog;
ZaXDialog.prototype.constructor = ZaXDialog;
/**
* A map of funciton references. Functions in this map are called one after another from 
* {@link #getMyXForm} method.
* The functions are called on the current instance of the dialog. 
* One parameter is passed to each function: a reference to the XForms object defenition
*  ZaXDialog
* @see #getMyXForm
**/
ZaXDialog.XFormModifiers = new Object();

/**
* 
**/
ZaXDialog.HELP_BUTTON = ++ZA_BTN_INDEX;
/**
* public method _initForm
* @param xModelMetaData
* @param xFormMetaData
**/
ZaXDialog.prototype.initForm = 
function (xModelMetaData, xFormMetaData) {
	if(xModelMetaData == null || xFormMetaData == null)
		throw new AjxException("Metadata for XForm and/or XModel are not defined", AjxException.INVALID_PARAM, "ZaXWizardDialog.prototype.initForm");
		
	this._localXModel = new XModel(xModelMetaData);
	this._localXForm = new XForm(xFormMetaData, this._localXModel, null, this);
	this._localXForm.setController(this._app);
	this._localXForm.draw(this._pageDiv);	
	this._drawn = true;
}

ZaXDialog.prototype.getObject = 
function () {
	return this._containedObject;
}

/**
* sets the object contained in the view
**/
ZaXDialog.prototype.setObject =
function(entry) {
	this._containedObject = entry;
	this._localXForm.setInstance(this._containedObject);
}

/**
* This method walks the map {@link #XFormModifiers} and calls each function in the map.
* The functions are called on the current instance of the dialog. 
* One parameter is passed to each function: a reference to the XForms object defenition
*  ZaXDialog
**/
ZaXDialog.prototype.getMyXForm = 
function() {	
	var xFormObject = new Object();
	//Instrumentation code start
	if(ZaXDialog.XFormModifiers[this._iKeyName]) {
		var methods = ZaXDialog.XFormModifiers[this._iKeyName];
		var cnt = methods.length;
		for(var i = 0; i < cnt; i++) {
			if(typeof(methods[i]) == "function") {
				methods[i].call(this,xFormObject);
			}
		}
	}	
	//Instrumentation code end	
	return xFormObject;
}

/**
*  ZaXDialog
* @private
**/
ZaXDialog.prototype._createContentHtml =
function () {

	this._table = document.createElement("table");
	this._table.border = 0;
	this._table.width=this._contentW;
	this._table.cellPadding = 0;
	this._table.cellSpacing = 0;
	Dwt.associateElementWithObject(this._table, this);
	this._table.backgroundColor = DwtCssStyle.getProperty(this.parent.getHtmlElement(), "background-color");
	
	var row2; //page
	var col2;
	row2 = this._table.insertRow(0);
	row2.align = "left";
	row2.vAlign = "middle";
	
	col2 = row2.insertCell(row2.cells.length);
	col2.align = "left";
	col2.vAlign = "middle";
	col2.noWrap = true;	
	col2.width = this._contentW;
	col2.appendChild(this._pageDiv);

	this._contentDiv.appendChild(this._table);
}

/**
*  ZaXDialog
* @private
**/
ZaXDialog.prototype._helpButtonListener =
function() {
	window.open(this._helpURL);
}