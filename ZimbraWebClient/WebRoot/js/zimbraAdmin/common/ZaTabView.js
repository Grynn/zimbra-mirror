/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2005, 2006, 2007, 2008, 2009, 2010, 2011, 2012 VMware, Inc.
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
* @class ZaTabView is an abstract class for creating views that present data in tabs.
* All the tabbed views in the Admin UI should extend ZaTabView.
* call initForm after calling the constructor
* @contructor
* @param parent
* @param app
* @extends DwtComposite
* @author Greg Solovyev
**/

ZaTabView = function(params) {
	if (arguments.length == 0) return;
	params = Dwt.getParams(arguments, ZaTabView.PARAMS);
	var className = params.cssClassName ? params.cssClassName : "DwtTabView";
	this._contextId = params.contextId? params.contextId:ZaId.TAB_UNDEF;
	DwtComposite.call(this, {
		parent:params.parent, 
		className:className, 
		posStyle:Dwt.ABSOLUTE_STYLE,
		id: ZaId.getTabId(this._contextId)
	});	
	this._iKeyName = params.iKeyName;
	this._drawn = false;	
	this._appCtxt = this.shell.getData(ZaAppCtxt.LABEL);
	this._containedObject = null;
	this.setScrollStyle(Dwt.SCROLL_Y);
	this._currentSubTab = [];
}

ZaTabView.PARAMS = ["parent","iKeyName", "cssClassName", "contextId"];

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
ZaTabView.XFormSetObjectMethods = new Object();

ZaTabView.DEFAULT_TAB = 1;

/**
* @param xModelMetaData - XModel metadata that describes data model
* @param xFormMetaData - XForm metadata that describes the form
**/
ZaTabView.prototype.initForm = 
function (xModelMetaData, xFormMetaData, entry) {
	if(xModelMetaData == null || xFormMetaData == null)
		throw new AjxException(ZaMsg.ERROR_METADATA_NOT_DEFINED, AjxException.INVALID_PARAM, "ZaTabView.prototype._initForm");

	this._localXModel = new XModel(xModelMetaData);
	this._localXForm = new XForm(xFormMetaData, this._localXModel, entry, this, ZaId.getTabViewId(this._contextId));
	this._localXForm.setController(ZaApp.getInstance());
	this._localXForm.draw();
	this.formChangeListener = new AjxListener(this, ZaTabView.prototype.setDirty,[true]) ;
	this._localXForm.addListener(DwtEvent.XFORMS_VALUE_CHANGED,this.formChangeListener);
	this._drawn = true;
}

ZaTabView.prototype.setBounds = function (x, y, width, height) {	
	DwtControl.prototype.setBounds.call(this,x, y, width, height);

	if (this.isListenerRegistered(DwtEvent.CONTROL)) {
	 	var evt = DwtShell.controlEvent;
	 	evt.reset();
	 	this.notifyListeners(DwtEvent.CONTROL, evt);
	} 
}

/**
* @return XForm definition for this view's XForm
**/
ZaTabView.prototype.getMyXForm = function (entry) {
	var xFormObject = new Object();
	//Instrumentation code start
	if(ZaTabView.XFormModifiers[this._iKeyName]) {
		var methods = ZaTabView.XFormModifiers[this._iKeyName];
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
	this._containedObject.type = entry.type;
	this._containedObject.name = entry.name;
	
	if(entry.id)
		this._containedObject.id = entry.id;
	
	if(entry.rights)
		this._containedObject.rights = entry.rights;
	
	if(entry.setAttrs)
		this._containedObject.setAttrs = entry.setAttrs;
	
	if(entry.getAttrs)
		this._containedObject.getAttrs = entry.getAttrs;
		
	if(entry._defaultValues)
		this._containedObject._defaultValues = entry._defaultValues;
		
	for (var a in entry.attrs) {
		if(entry.attrs[a] instanceof Array) {
			this._containedObject.attrs[a] = [].concat(entry.attrs[a]);
		} else {
			this._containedObject.attrs[a] = entry.attrs[a];
		}
	}
	
	
	if(!entry[ZaModel.currentTab])
		this._containedObject[ZaModel.currentTab] = "1";
	else
		this._containedObject[ZaModel.currentTab] = entry[ZaModel.currentTab];
		
	this._localXForm.setInstance(this._containedObject);
	
	this.formDirtyLsnr = new AjxListener(ZaApp.getInstance().getCurrentController(), ZaXFormViewController.prototype.handleXFormChange);
	this._localXForm.addListener(DwtEvent.XFORMS_FORM_DIRTY_CHANGE, this.formDirtyLsnr);
	this._localXForm.addListener(DwtEvent.XFORMS_VALUE_ERROR, this.formDirtyLsnr);	
}

ZaTabView.ObjectModifiers = {} ;
ZaTabView.prototype.modifyContainedObject = function () {
     if(ZaTabView.ObjectModifiers[this._iKeyName]) {
		var methods = ZaTabView.ObjectModifiers[this._iKeyName];
		var cnt = methods.length;
		for(var i = 0; i < cnt; i++) {
			if(typeof(methods[i]) == "function") {
				methods[i].call(this);
			}
		}
	}
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
	ZaApp.getInstance().getCurrentController().setDirty(isD);
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

ZaTabView.prototype.getTabToolTip =
function () {
	if (this._containedObject && this._containedObject.name && this._containedObject.type) {
		return	ZaMsg.TBB_Edit + " " +  this._containedObject.type + " " + this._containedObject.name ;
	}else{
		return "" ;
	}
}

ZaTabView.prototype.getTabIcon = 
function () {
	if (this._containedObject && this._containedObject.type) {
		return this._containedObject.type ;
	}else{
		return "" ;
	}
}

ZaTabView.prototype.getTabTitle =
function () {
	if (this._containedObject && this._containedObject.name) {
		return this._containedObject.name ;
	}else{
		return ZaMsg.TBB_New;
	}
}

//this method will be called whenever the item object of the view is updated
//it should be called in the setObject function of the view class
ZaTabView.prototype.updateTab =
function () {
	var tab = this.getAppTab ();
	if (tab) {
		tab.resetLabel (this.getTabTitle()) ;
		tab.setImage (this.getTabIcon());
		tab.setToolTipContent (this.getTabToolTip()) ;
	}
}

ZaTabView.prototype.getAppTab =
function () {
	return ZaApp.getInstance().getTabGroup().getTabById(this.__internalId) ;
}

/**
 * This method checks if a tab or a view should be enabled based on given list of attributes and rights.
 * If current admin has read permission on any of the attribues, or has any of the provided rights the method returns TRUE
 */
ZaTabView.isTAB_ENABLED = function (entry, attrsArray, rightsArray) {
	if(!entry)
		return true;
		
	if(AjxUtil.isEmpty(attrsArray) && AjxUtil.isEmpty(rightsArray))
		return true;
		
	if(!AjxUtil.isEmpty(attrsArray)) {
		var cntAttrs = attrsArray.length;
		for(var i=0; i< cntAttrs; i++) {
			if(ZaItem.hasReadPermission(attrsArray[i],entry)) {
				return true;
			}
		}
	} 
	
	if(!AjxUtil.isEmpty(rightsArray)) {
		var cntRights = rightsArray.length;
		for(var i=0; i< cntRights; i++) {
			if(ZaItem.hasRight(rightsArray[i],entry)) {
				return true;
			}
		}
	}
	
	return false; 
}
