/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2005, 2006, 2007, 2008, 2009, 2010 Zimbra, Inc.
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

/**
* @class 
* @contructor 
* @extends DwtDialog
* @author Greg Solovyev
* @param parent
* @param w (width)
* @param h (height)
**/

ZaXDialog = function(parent,className, title, w, h,iKeyName, contextId) {
	if (arguments.length == 0) return;
	this._iKeyName = iKeyName;	
	var clsName = className || "DwtDialog";
	if(!this._standardButtons)
		this._standardButtons = [DwtDialog.OK_BUTTON];
	if(!this._extraButtons) {
		var helpButton = new DwtDialog_ButtonDescriptor(ZaXDialog.HELP_BUTTON, ZaMsg.TBB_Help, DwtDialog.ALIGN_LEFT, new AjxCallback(this, this._helpButtonListener));
		this._extraButtons = [helpButton];
	}
	
	this._contextId = contextId? contextId:ZaId.DLG_UNDEF
	
	DwtDialog.call(this, {
		parent:parent, 
		className:clsName, 
		title:title, 
		standardButtons:this._standardButtons,
		extraButtons:this._extraButtons,
		id:ZaId.getDialogId(this._contextId)
	});
	this._app = ZaApp.getInstance();
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
ZaXDialog.helpURL = location.pathname + ZaUtil.HELP_URL + "administration_console_help.htm?locid="+AjxEnv.DEFAULT_LOCALE;
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
ZaXDialog.HELP_BUTTON = ++DwtDialog.LAST_BUTTON;


ZaXDialog.prototype.popup = 
function (loc) {
	DwtDialog.prototype.popup.call(this, loc);
	if(this._localXForm) {
		this._localXForm.focusFirst();
	}
}

/**
* public method _initForm
* @param xModelMetaData
* @param xFormMetaData
**/
ZaXDialog.prototype.initForm = 
function (xModelMetaData, xFormMetaData, defaultInstance) {
	if(xModelMetaData == null || xFormMetaData == null)
		throw new AjxException("Metadata for XForm and/or XModel are not defined", AjxException.INVALID_PARAM, "ZaXWizardDialog.prototype.initForm");
		
	this._localXModel = new XModel(xModelMetaData);
	this._localXForm = new XForm(xFormMetaData, this._localXModel, defaultInstance, this, ZaId.getDialogViewId(this._contextId));
	this._localXForm.setController(ZaApp.getInstance());
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
function(entry) {	
	var xFormObject = new Object();
	//Instrumentation code start
	if(ZaXDialog.XFormModifiers[this._iKeyName]) {
		var methods = ZaXDialog.XFormModifiers[this._iKeyName];
		var cnt = methods.length;
		for(var i = 0; i < cnt; i++) {
			if(typeof(methods[i]) == "function") {
				methods[i].call(this,xFormObject,entry);
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

	this._getContentDiv().appendChild(this._table);
}

/**
*  ZaXDialog
* @private
**/
ZaXDialog.prototype._helpButtonListener =
function() {
	window.open(this._helpURL);
}
