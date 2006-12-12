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
* @class ZaTabView is an abstract class for creating views that present data in tabs.
* All the tabbed views in the Admin UI should extend ZaTabView.
* call initForm after calling the constructor
* @contructor
* @param parent
* @param app
* @extends DwtComposite
* @author Greg Solovyev
**/
function ZaTabView (parent, app, iKeyName) {
	if (arguments.length == 0) return;
	DwtComposite.call(this, parent, "DwtTabView", Dwt.ABSOLUTE_STYLE);	
	this._iKeyName = iKeyName;
	this._app = app;
	this._drawn = false;	
	this._appCtxt = this.shell.getData(ZaAppCtxt.LABEL);
	this._containedObject = null;
	this.setScrollStyle(Dwt.VISIBLE);
	this._currentSubTab = [];
}

ZaTabView.prototype = new DwtComposite();
ZaTabView.prototype.constructor = ZaTabView;

/**
* A map of funciton references. Functions in this map are called one after another from 
* {@link #getMyXForm} method.
* The functions are called on the current instance of the dialog. 
* One parameter is passed to each function: a reference to the XForms object defenition.
* Keys in the map are names of the view classes: ZaAccountXFormView, ZaCosXFormView, ZaDomainXFormView, etc
* Values in the map are arrays of function references
* If you have defined your function for modifying a view's XForm definition, you can add it to this map like this:
* ZaTabView.XFormModifiers["ZaAccountXFormView"].push(ZaAccountXFormView.myXFormModifier);
* This example adds funciton ZaAccountXFormView.myXFormModifier to the array of functions that will be called to construct UI for ZaAccountXFormView
*
* Examples of using this map can be found in {@link ZaAccountXFormView}, {@link ZaCosXFormView}, {@link ZaServerXFormView}
* @see #getMyXForm
* @see ZaAccountXFormView#myXFormModifier
* @see ZaCosXFormView#myXFormModifier
* @see ZaCosXFormView#ZaServerXFormView
**/
ZaTabView.XFormModifiers = new Object();

ZaTabView.DEFAULT_TAB = 1;

/**
* @param xModelMetaData - XModel metadata that describes data model
* @param xFormMetaData - XForm metadata that describes the form
**/
ZaTabView.prototype.initForm = 
function (xModelMetaData, xFormMetaData) {
	if(xModelMetaData == null || xFormMetaData == null)
		throw new AjxException("Metadata for XForm and/or XModel are not defined", AjxException.INVALID_PARAM, "DwtXWizardDialog.prototype._initForm");

	this._localXModel = new XModel(xModelMetaData);
	this._localXForm = new XForm(xFormMetaData, this._localXModel, null, this);
	this._localXForm.setController(this._app);
	this._localXForm.draw();
	this._drawn = true;
}

ZaTabView.prototype.setBounds = function (x, y, width, height) {	
	DwtControl.prototype.setBounds.call(this,x, y, width, height);
	
}

/**
* @return XForm definition for this view's XForm
**/
ZaTabView.prototype.getMyXForm = function () {
	var xFormObject = new Object();
	//Instrumentation code start
	if(ZaTabView.XFormModifiers[this._iKeyName]) {
		var methods = ZaTabView.XFormModifiers[this._iKeyName];
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
* @return XForm instance displayed on the view
**/
ZaTabView.prototype.getMyForm = function () {
	return this._localXForm;
}

/**
* @return XModel instance controlled by the XForm on the view
**/
ZaTabView.prototype.getMyModel = function () {
	return this._localXModel;
}


/**
* @method getObject returns the object contained in the view
* before returning the object this updates the object attributes with 
* tha values from the form fields 
**/
ZaTabView.prototype.getObject =
function() {
	return this._containedObject;
}

/**
* @method setObject sets the object contained in the view
* @param entry - ZaItem object to display
**/
ZaTabView.prototype.setObject =
function(entry) {
	this._containedObject = new Object();
	this._containedObject.attrs = new Object();
	
	for (var a in entry.attrs) {
		this._containedObject.attrs[a] = entry.attrs[a];
	}
	
	if(!entry[ZaModel.currentTab])
		this._containedObject[ZaModel.currentTab] = "1";
	else
		this._containedObject[ZaModel.currentTab] = entry[ZaModel.currentTab];
		
	this._localXForm.setInstance(this._containedObject);
}

ZaTabView.prototype.setEnabled = 
function(enable) {
	//abstract. This method may be depriicated in near future
}

/**
* @param isD Boolean - flag indicates whether data on the form ahs been modified by user
**/
ZaTabView.prototype.setDirty = 
function (isD) {
	this._app.getCurrentController().setDirty(isD);
	this._isDirty = isD;
	//reset the domain lists
	EmailAddr_XFormItem.resetDomainLists.call (this);
}

ZaTabView.prototype.getCurrentTab = 
function() {
	return this._containedObject[ZaModel.currentTab];
}

ZaTabView.prototype.getCurrentSubTab = 
function() {
	var subtab = this._currentSubTab[this._containedObject[ZaModel.currentTab]];
	if (subtab == null) {
		subtab = this._currentSubTab[this._containedObject[ZaModel.currentTab]] = ZaTabView.DEFAULT_TAB;
	}
	return subtab;
}

ZaTabView.prototype.swithTab = 
function (value) {
	this._containedObject[ZaModel.currentTab] = value;
	this._localXForm.refresh();
}

ZaTabView.prototype.switchSubTab =
function(value) {
	this._currentSubTab[this._containedObject[ZaModel.currentTab]] = value;
}

ZaTabView.prototype.isDirty = 
function () {
	return this._isDirty;
}

ZaTabView.onFormFieldChanged = 
function (value, event, form) {
	form.parent.setDirty(true);	
	this.setInstanceValue(value);
	return value;
}

