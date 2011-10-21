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


//
//	Factory to create XFormItems from simple attributes (eg: from JS object literals or XML)
//

/**
 * This object is never instantiated.
 * @class
 * @private 
 */
XFormItemFactory = function() {}

/**
 * Creates a form item.
 *
 * @param attributes		an object whose properties map to component attribute name/value pairs
 * @param parentItem 		the parent item of this item
 * @param {XForm}	xform      the form to which this item is being created
 * 
 * @private
 */
XFormItemFactory.createItem = function (attributes, parentItem, xform) {
	// assign a modelItem to the item
	var refPath = this.getRefPath(attributes, parentItem);
	var subRefPath = this.getSubRefPath(attributes, parentItem);

	var modelItem, subModelItem;
	if (refPath != null) {
		// assign a modelItem to the item
		modelItem = this.getModelItem(xform.xmodel, attributes, refPath);
	}
	
	if (subRefPath != null) {
		// assign a modelItem to the item
		subModelItem = this.getModelItem(xform.xmodel, attributes, subRefPath);
	}
	// get the class for that type and create one
	var type = this.getItemType(attributes, modelItem);
	var constructor = this.getItemTypeConstructor(type, xform);

	var item = new constructor();
	item._setAttributes(attributes);

	// get a unique id for the item
	var idPrefix = (	attributes.id ? xform.getId() + "_" + attributes.id :
							  refPath ? xform.getId() + "_" + refPath :
					item.__parentItem ? item.__parentItem.getId() :
										xform.getId() + "_" + item.type
					);
	// assign a unique id to each item
	//	(if the item specifies an id, we use a variant of that, just in case there's more than one)
	item.id = xform.getUniqueId(idPrefix);

	item.refPath = refPath;
	item.subRefPath = subRefPath;
	item.__modelItem = modelItem;
	item.__subModelItem = subModelItem;
	
	item.__xform = xform;
	item.__parentItem = parentItem;
	
	// assign the item into our form's index so we can be found later
	xform.indexItem(item, item.id);
	

	// tell the item to initialize any special properties it needs to on construction
	item.initFormItem();
	
		
	return item;
} 

XFormItemFactory.getRefPath = function (attributes, parentItem) {
	if (attributes.refPath) return attributes.refPath;
	
	var ref = attributes.ref;
	if (ref == null) return null;
	
	if (parentItem) {
		var parentPath = parentItem.getRefPath();
		if (parentPath == null) parentPath = "";
	} else {
		var parentPath = "";
	}
	
	var path = ref;
	if (ref == ".") {
		path = parentPath;

	} else if (ref == "..") {
		parentPath = parentPath.split("/");
		path = parentPath.slice(0, parentPath.length - 1).join("/");

	} else if (parentPath == "") {
		path = ref;

	} else {
		path = parentPath + "/" + ref;
	}
	return path;
}

XFormItemFactory.getSubRefPath = function (attributes, parentItem) {
	if (attributes.subRefPath) return attributes.subRefPath;
	
	var subRref = attributes.subRef;
	if (subRref == null) return null;
	
	if (parentItem) {
		var parentPath = parentItem.getSubRefPath();
		if (parentPath == null) parentPath = "";
	} else {
		var parentPath = "";
	}
	
	var path = subRref;
	if (subRref == ".") {
		path = parentPath;

	} else if (subRref == "..") {
		parentPath = parentPath.split("/");
		path = parentPath.slice(0, parentPath.length - 1).join("/");

	} else if (parentPath == "") {
		path = subRref;

	} else {
		path = parentPath + "/" + subRref;
	}
	return path;
}

XFormItemFactory.getModelItem = function (xmodel, attributes, refPath) {
	if (refPath == null || refPath == "") return null;
	return xmodel.getItem(refPath, true);
}

XFormItemFactory.getItemType = function (attributes, modelItem) {
	var type = attributes.type;

	if (type == null) {
		type = attributes.type = _OUTPUT_;
	}
	
	var modelType = (modelItem && modelItem.type ? modelItem.type : _STRING_);

	if (type == _INPUT_) {
		if (attributes.value !== _UNDEFINED_) {
			type = _CHECKBOX_;
		} else {
			switch (modelType) {
				case _STRING_:
				case _NUMBER_:
					type = _INPUT_;
					break;

				case _DATE_:
				case _DATETIME_:
				case _TIME_:
					type = modelType;			
					break;

				default:
					type = _INPUT_;
			}
		}
	} else if (type == _SELECT_) {
		var appearance = attributes.appearance;
		if (appearance == _RADIO_) {
			type = _RADIO_;
		} else {
			type = _SELECT_;
		}
	}
	return type;
}

XFormItemFactory.typeConstructorMap = {};

XFormItemFactory.createItemType = 
function (typeConstant, typeName, constructor, superClassConstructor) {
	if (constructor == null) constructor = new Function();
	if (typeof superClassConstructor == "string") superClassConstructor = this.getItemTypeConstructor(superClassConstructor);
	if (superClassConstructor == null) superClassConstructor = XFormItem;

	// initialize the constructor
	constructor.prototype = new superClassConstructor();	

	constructor.prototype.type = typeName;
	constructor.prototype.constructor = constructor;
	constructor.prototype.toString = new Function("return '[XFormItem:" + typeName + " ' + this.getId() + ']'");
	constructor.toString = new Function("return '[Class XFormItem:" + typeName + "]'");
	
	// put the item type into the typemap
	this.registerItemType(typeConstant, typeName, constructor);
	
	// return the prototype
	return constructor;
}

XFormItemFactory.registerItemType = 
function(typeConstant, typeName, constructor) {
	// assign the type constant to the window so everyone else can use it
	window[typeConstant] = typeName;
	this.typeConstructorMap[typeName] = constructor;	
}

XFormItemFactory.defaultItemType = "output";
XFormItemFactory.getItemTypeConstructor = 
function (typeName, form) {
	var typeConstructorMap = (form && form.typeConstructorMap ? form.typeConstructorMap : this.typeConstructorMap);
	
	var typeConstructor = typeConstructorMap[typeName];
	if (typeConstructor == null) {
		var defaultItemType = (form ? form.defaultItemType : this.defaultItemType);
		typeConstructorMap[defaultItemType];
	}
	return typeConstructor;
}

XFormItemFactory.quickClone = 
function(object) {
	this.cloner.prototype = object;
	return new this.cloner();
}
XFormItemFactory.cloner = function(){}

XFormItemFactory.initItemDefaults = function(form, itemDefaults) {
	// create a clone of the XFormItemFactory typeConstructorMap for the form
	form.typeConstructorMap =  this.quickClone(this.typeConstructorMap);

	if (itemDefaults == null) itemDefaults = form.itemDefaults;
	if (itemDefaults != null) {
		// for each type in itemDefaults
		for (var type in itemDefaults) {
			var originalConstructor = this.typeConstructorMap[type];
			var defaults = itemDefaults[type];

			if (originalConstructor == null) {
				type = window[type];
				originalConstructor = this.typeConstructorMap[type];
			}
			if (originalConstructor == null) {
				continue;
			}
			var newConstructor = form.typeConstructorMap[type] = new Function();
			newConstructor.prototype = new originalConstructor();
			// NOTE: reassigning the constructor here is technically correct,
			//		but will result in (item.constructor == originalClass.constructor) not working...
			newConstructor.prototype.constructor = newConstructor;
			
			for (var prop in defaults) {
				newConstructor.prototype[prop] = defaults[prop];
			}
		}
	}
}




//
//	Abstract Class XFormItem
//
//	All other form item classes inherit from this.
//


/**
 * @private
 */
XFormItem = function() {}
XFormItem.prototype.constructor = XFormItem;
XFormItemFactory.registerItemType("_FORM_ITEM_", "form_item", XFormItem);

XFormItem.ERROR_STATE_ERROR = 0;
XFormItem.ERROR_STATE_VALID = 1;


//
// set base class defaults
// 

XFormItem.prototype._isXFormItem = true;

// outputting and inserting
XFormItem.prototype.writeElementDiv = false;

// appearance
XFormItem.prototype.labelLocation = _LEFT_;
XFormItem.prototype.tableCssClass = "xform_table";				// table that encloses one or more cells
XFormItem.prototype.tableCssStyle = null;						// table that encloses one or more cells
XFormItem.prototype.containerCssClass =  "xform_container";		// td that contains the element
XFormItem.prototype.containerCssStyle =  null;					// td that contains the element
XFormItem.prototype.cssClass = null;							// element itself (or element div)
XFormItem.prototype.labelCssClass =  "xform_label";				// label td
XFormItem.prototype.errorCssClass =  "xform_error";				// error DIV
XFormItem.prototype.nowrap = false; 
XFormItem.prototype.labelWrap = false; 
XFormItem.prototype.align = _UNDEFINED_;						// _UNDEFINED_ because it's a bit faster to draw
XFormItem.prototype.valign = _UNDEFINED_;						// _UNDEFINED_ because it's a bit faster to draw
XFormItem.prototype.focusable = false;
XFormItem.prototype.bmolsnr = false; //Be My Own Listener
// updating
XFormItem.prototype.forceUpdate = false;			// SET TO true TO FORCE AN ITEM TO UPDATE, EVEN IF VALUE HAS NOT CHANGED
//XFormItem.prototype.relevant;
//XFormItem.prototype.relevantIfEmpty = true;
//XFormItem.prototype.relevantBehavior = _HIDE_;		//	_HIDE_, _DISABLE_
XFormItem.prototype.isBlockElement = false;
XFormItem.prototype.visibilityChecks = []; //array of method references that check whether this element should be visible
XFormItem.prototype.enableDisableChecks = []; //array of methods that check whether this element should be enabled 
XFormItem.prototype.visibilityChangeEventSources = []; //paths to XModelItems that influence visibility of this XFormItem
XFormItem.prototype.enableDisableChangeEventSources = []; //paths to XModelItems that influence Enabled/Disabled state of this XFormItem
XFormItem.prototype.valueChangeEventSources = []; //paths to XModelItems that influence the value this XFormItem

/* array of references to XModel items that may affect the visibility of this item. 
* Whenever any of the XModel items referenced in this array change, they will notify this XForm item
*/
XFormItem.prototype.visibilityUpdaters = [];

/* array of references to XModel items that may affect whether this item is enabled. 
* Whenever any of the XModel items referenced in this array change, they will notify this XForm item
*/
XFormItem.prototype.enabledDisabledUpdaters = [];

// changing/saving
XFormItem.prototype.elementChangeHandler = "onchange";

                              
// choices map
XFormItem.prototype.selection = _CLOSED_;
XFormItem.prototype.openSelectionLabel = "";

// error handling
XFormItem.prototype.errorLocation = _SELF_;

// show help tooltip icon
XFormItem.prototype.helpTooltip = false;
//
// Methods
//


// set the initializing attributes of this firm
XFormItem.prototype._setAttributes = function (attributes) {
	this.__attributes = attributes;
}

// override this to do any item initialization you need to do
//	NOTE: this is called AFTER the formItem is initiaized with its modelItem, set in its form, etc
XFormItem.prototype.initFormItem = function() {
//	window.status = '';
	if(this.focusable) {
		var currentTabId = XFormItem.getParentTabGroupId(this);
		if(currentTabId) {
			var tabGroupItem = this.getForm().getItemById(currentTabId);
			tabGroupItem.tabIdOrder.push(this.getId());
		}
	}
}	

// DEFAULT IMPLEMENTATION calls this.getForm().initItemList() on our items array
//	SOME CLASSES MAY NOT WANT TO DO THIS (eg: _REPEAT_, which does this dynamically later)
XFormItem.prototype.initializeItems = function () {
	var items = this.getItems();
	if (items != null) {
		this.items = this.getForm().initItemList(items, this);
	}
}

XFormItem.prototype.registerActiveChild = function(item) {
	if(!this.activeChildren)
		this.activeChildren = {};
	this.activeChildren[item.getId()]=true;	
}

XFormItem.prototype.signUpForEvents = function () {
	var modelItem;
	modelItem = this.getModelItem();

	//register this item's listeners with model items
	var itemsVisibilityChangers = this.getInheritedProperty("visibilityChangeEventSources");
	if(!AjxUtil.isEmpty(itemsVisibilityChangers)) {
		var model = this.getModel();
		var cnt = itemsVisibilityChangers.length;
		if(model && cnt>0) {
			for (var i=0; i < cnt; i++) {
				var modelItm = model.getItem(itemsVisibilityChangers[i], false);
				if(modelItm) {
					var lsnr = new AjxListener(this, XFormItem.prototype.updateVisibilityLsnr);
					modelItm.addListener(DwtEvent.XFORMS_VALUE_CHANGED, lsnr);
				}
			}
		}
	}
	
	var itemsEnableDisableChangers = this.getInheritedProperty("enableDisableChangeEventSources");
	if(!AjxUtil.isEmpty(itemsEnableDisableChangers)) {
		var model = this.getModel();
		var cnt = itemsEnableDisableChangers.length;
		if(model && cnt>0) {
			for (var i=0; i < cnt; i++) {
				var modelItm = model.getItem(itemsEnableDisableChangers[i], false);
				if(modelItm) {
					var lsnr = new AjxListener(this, XFormItem.prototype.updateEnabledDisabledLsnr);
					modelItm.addListener(DwtEvent.XFORMS_VALUE_CHANGED, lsnr);
				}
			}
		}
	}	
	
	var itemsValueChangers = this.getInheritedProperty("valueChangeEventSources");
	if(!AjxUtil.isEmpty(itemsValueChangers)) {
		var model = this.getModel();
		var cnt = itemsValueChangers.length;
		if(model && cnt>0) {
			for (var i=0; i < cnt; i++) {
				var modelItm = model.getItem(itemsValueChangers[i], false);
				if(modelItm) {
					var lsnr = new AjxListener(this, XFormItem.prototype.valueChangeLsnr);
					modelItm.addListener(DwtEvent.XFORMS_VALUE_CHANGED, lsnr);
				}
			}
		}
	}
	
	//listen to changes of my own model item
	var bmolsnr = this.getInheritedProperty("bmolsnr");
	if(modelItem && bmolsnr) {
		var lsnr = new AjxListener(this, XFormItem.prototype.valueChangeLsnr);
		modelItem.addListener(DwtEvent.XFORMS_VALUE_CHANGED, lsnr);
	}
}

XFormItem.prototype.valueChangeLsnr = function (event) {
	var updateMethod = this.getUpdateElementMethod();
	if(!updateMethod)
		return;
		
	var value = this.getInstanceValue();	
	var getDisplayValueMethod = this.getDisplayValueMethod();
	if(getDisplayValueMethod)
		value = getDisplayValueMethod.call(this,value);
	
	updateMethod.call(this, value);
}

XFormItem.prototype.updateElement = function() {
	//run update methods on all initialized children
	if(!this.activeChildren)
		return;
		
	for(var itemId in this.activeChildren) {
		if(this.activeChildren[itemId]===true) {
			var item = this.getForm().getItemById(itemId);
			if(item && this.getInstance()) {
				var updateMethod = item.getUpdateElementMethod();
				var getDisplayValueMethod = item.getDisplayValueMethod();
				
				if(updateMethod) {
					var xmodel = this.getModel();
					var value = item.getRefPath() ? xmodel.getInstanceValue(this.getInstance(), item.getRefPath()) : item.getValue();
					if (getDisplayValueMethod) {
						value =  getDisplayValueMethod.call(item,value);
					}
					updateMethod.call(item,value);
				}
			}
		}
	}
}

XFormItem.prototype.hasReadPermission = function (refToCheck) {
	var instance = this.getInstance();
	if (!instance.getAttrs)
		return false;
	
	var refPath=null;
	if(refToCheck) {
		refPath=refToCheck;
	} else {
		if(!this.refPath)
			return true;
		else
			refPath=this.refPath;
	}
		
	return ((instance.getAttrs.all === true) || (instance.getAttrs[refPath] === true));
}

XFormItem.prototype.hasWritePermission = function (refToCheck) {
	var instance = this.getInstance();
	if (!instance.setAttrs)
		return false;
	
	var refPath=null;
	if(refToCheck) {
		refPath=refToCheck;
	} else {
		if(!this.refPath)
			return true;
		else
			refPath=this.refPath;
	}
		
	return ((instance.setAttrs.all === true) || (instance.setAttrs[refPath] === true));
}

XFormItem.prototype.hasRight = function (right) {
	var instance = this.getInstance();
	if (!instance.rights)
		return false;
	
	if(!right)
		return true;
		
	return (instance.rights[right] === true);
}

XFormItem.prototype.updateVisibilityLsnr = function (event) {
	var updateMethod = this.getUpdateVisibilityMethod();
	updateMethod.call(this);
}

XFormItem.prototype.updateVisibility = function () {
	var isVisible = true;
	
	//check if the parent element is visible
	var parentItem = this.getParentItem();
	if(parentItem)
		isVisible=this.getParentItem().getIsVisible();
	
	//run stack of visibility checks until encounter a negative result
	if(isVisible) {
		var myVisibilityChecks = this.getInheritedProperty("visibilityChecks");
		if(myVisibilityChecks && myVisibilityChecks instanceof Array) {
			var cnt = myVisibilityChecks.length;
			for(var i=0;i<cnt;i++) {
				if(myVisibilityChecks[i] != null) {
					if(typeof(myVisibilityChecks[i])=="function") {
						isVisible = myVisibilityChecks[i].call(this);
						if(!isVisible)
							break;
					} else if (myVisibilityChecks[i] instanceof Array) {
						//first element is a func reference, the rest of elements are arguments
						var func = myVisibilityChecks[i].shift();
						isVisible = func.apply(this, myVisibilityChecks[i]);
						myVisibilityChecks[i].unshift(func);
						if(!isVisible)
							break;
					} else if (typeof (myVisibilityChecks[i]) == "string") {
                        //for relevant backward compatibility
                        var instance = this.getInstance();
                        isVisible = eval(myVisibilityChecks[i]) ;
                        if(!isVisible)
							break;
                    }
				}
			}
		}
	}	
	var reRunRefresh = false;	
	if(isVisible) {
		if(this.deferred)
			reRunRefresh=true;
			
		this.show();
	} else
		this.hide();
	
	//update visibility for active child items
	for(var itemId in this.activeChildren) {
		if(this.activeChildren[itemId]===true) {
			var item = this.getForm().getItemById(itemId);
			if(item && this.getInstance()) {
				var updateMethod = item.getUpdateVisibilityMethod();				
				if(updateMethod) {
					updateMethod.call(item);
				}
			}
		}
	}
	
	if(reRunRefresh) {
		this.updateEnabledDisabled();
		this.updateElement();
	}	
}

XFormItem.prototype.updateEnabledDisabledLsnr = function (event) {
	var updateMethod = this.getUpdateEnabledDisabledtMethod();
	updateMethod.call(this);	
}

XFormItem.prototype.updateEnabledDisabled = function (parentDisabled) {
	var isEnabled = true;
	
	//check if the parent element is visible
	var parentItem = this.getParentItem();
	if(parentItem)
		isEnabled=this.getParentItem().getIsEnabled();
		
	//run stack of visibility checks until encounter a negative result
	if(isEnabled) {
		var myEnabledDisabledChecks = this.getInheritedProperty("enableDisableChecks");
		if(myEnabledDisabledChecks && myEnabledDisabledChecks instanceof Array) {
			var cnt = myEnabledDisabledChecks.length;
			for(var i=0;i<cnt;i++) {
				if(myEnabledDisabledChecks[i] != null) {
					if(typeof(myEnabledDisabledChecks[i])=="function") {
						isEnabled = myEnabledDisabledChecks[i].call(this);
						if(!isEnabled)
							break;
					} else if (myEnabledDisabledChecks[i] instanceof Array) {
						//first element is a func reference, the rest of elements are arguments
						var func = myEnabledDisabledChecks[i].shift();
						if(!func || !func.apply) continue;
						isEnabled = func.apply(this, myEnabledDisabledChecks[i]);
						myEnabledDisabledChecks[i].unshift(func);
						if(!isEnabled)
							break;
					}                      
				}
			}
		}else if (myEnabledDisabledChecks == false) {   //always disable this element
            isEnabled = false ;
        }
	}	
	
	if(isEnabled)
		this.enableElement();
	else
		this.disableElement();
	
	//update enableddisabled for active child items
	for(var itemId in this.activeChildren) {
		if(this.activeChildren[itemId]===true) {
			var item = this.getForm().getItemById(itemId);
			if(item && this.getInstance()) {
				var updateMethod = item.getUpdateEnabledDisabledtMethod();				
				if(updateMethod) {
					updateMethod.call(item);
				}
			}
		}
	}
}
// error handling

/**
 * Sets the error message for this form item.
 * This will set the error for this item, or 
 * useing the errorLocation, follow the chain up,
 * to set the error on the related item.
 *
 * @param message The message to display. This message should already
 *                be localized.
 */
XFormItem.prototype.setError = function(message, childError) {
	var errLoc = this.getErrorLocation();
	if (errLoc == _PARENT_ || errLoc == _INHERIT_){
		this.getParentItem().setError(message, true);
		return;
	}
	this.getForm().addErrorItem(this);
	this.__errorState = XFormItem.ERROR_STATE_ERROR;
	var container = this.getErrorContainer(true);
	if (container) container.innerHTML = message;
};

/** 
 * Clears the error message for this form item. 
 * This will clear the error for this item, or 
 * useing the errorLocation, follow the chain up,
 * to clear the error on the related item.
 */
XFormItem.prototype.clearError = function() {
	var errLoc = this.getErrorLocation();
	if (errLoc == _PARENT_ || errLoc == _INHERIT_){
		this.getParentItem().clearError();
		return;
	}

	this.getForm().removeErrorItem(this);
	this.__errorState = XFormItem.ERROR_STATE_VALID;
	this.removeErrorContainer();
};

XFormItem.prototype.hasError = function () {
	return (this.__errorState == XFormItem.ERROR_STATE_ERROR);
};

XFormItem.prototype.getErrorContainer = function(createIfNecessary) {
	var container = this.getElement(this.getId() + "___error_container");
	if (container != null) return container;
	
	if (createIfNecessary == true && this.getContainer()) {
		return this.createErrorContainer();
	}
	return null;
}

XFormItem.prototype.createErrorContainer = function () {
	// output an error container
	var errorContainer = document.createElement("div");
	errorContainer.id = this.getId() + "___error_container";
	errorContainer.className = this.getErrorCssClass();

	var container = this.getContainer();
	if (container.hasChildNodes()) {
		container.insertBefore(errorContainer, container.firstChild);
	} else {
		container.appendChild(errorContainer);
	}	
	return errorContainer;
}

XFormItem.prototype.removeErrorContainer = function () {
	var errorContainer = this.getErrorContainer();
	if (errorContainer != null) {
		errorContainer.parentNode.removeChild(errorContainer);
	}
}


//
// PROPERTIES OF INDIVIDUAL ITEMS
//


XFormItem.prototype.getType = function () {
	return this.type;
}


//XXX
XFormItem.prototype.getParentItem = function () {
	return this.__parentItem;
}

XFormItem.prototype.getForm = function () {
	return this.__xform;
}

XFormItem.prototype.getGlobalRef = function() {
	return this.getForm().getGlobalRefString() + ".getItemById('" + this.getId() + "')";
}

XFormItem.prototype.getFormGlobalRef = function() {
	return this.getForm().getGlobalRefString();
}

XFormItem.prototype.getInstance = function() {
	return this.getForm().instance;
}

XFormItem.prototype.getModel = function () {
	return this.getForm().getModel();
}

XFormItem.prototype.getController = function () {
	return this.getForm().getController();
}

XFormItem.prototype.getFormController = function () {
	return this.getForm().getController();
}


XFormItem.prototype.getModelItem = function() {
	return this.__modelItem;
}

XFormItem.prototype.getSubModelItem = function() {
	return this.__subModelItem;
}

//XXX NON-STANDARD
XFormItem.prototype.getRef = function () {
	if (this.ref !== _UNDEFINED_) return this.ref;
	return this.__attributes.ref;
}

XFormItem.prototype.getRefPath = function () {
	return this.refPath;
}

XFormItem.prototype.getSubRefPath = function () {
	return this.subRefPath;
}

XFormItem.prototype.getId = function () {
	return this.id;
}

XFormItem.prototype.getExternalId = function () {
	var ret = null;
	if (this.__attributes.id !== _UNDEFINED_) {
		ret = this.__attributes.id;
	} else if ( (ret = this.getRef()) !== _UNDEFINED_) {
		// nothing
	} else {
		ret = null;
	}
	return ret;
};



//
//	GENERIC HTML WRITING ROUTINES
//


XFormItem.prototype.getOnChangeMethod = function() {
	return this.cacheInheritedMethod("onChange","$onChange","value,event,form");
}

XFormItem.prototype.getOnActivateMethod = function() {
	return this.cacheInheritedMethod("onActivate","$onActivate","event");
}

XFormItem.prototype.getOnClickMethod = function() {
	return this.cacheInheritedMethod("onClick","$onClick","event");
}

XFormItem.prototype.getExternalChangeHandler = function() {
	return "var item = " + this.getGlobalRef() + "; var elementChangedMethod = item.getElementChangedMethod(); elementChangedMethod.call(item, value, item.getInstanceValue(), event||window.event);";
}
XFormItem.prototype.getElementValueGetterHTML = function () {
	return "var value = this.value;";
}

/**
* returns the HTML part of an <input > element that is used to set "onchange" 
* (or whatever is defined by elementChangehandler)  property of the element
**/
XFormItem.prototype.getChangeHandlerHTML = function() {
	var elementChangeHandler = this.getElementChangeHandler();
	if (elementChangeHandler != "onkeypress") {
		return AjxBuffer.concat(" ", elementChangeHandler, "=\"", this.getChangehandlerJSCode() + "\"",this.getKeyPressHandlerHTML());
	} else {
		return this.getKeyPressHandlerHTML();
	}
}

/**
* returns JavaScript code that should be executed when an element is changed by a user
* @author Greg Solovyev
**/
XFormItem.prototype.getChangehandlerJSCode = function () {
	return AjxBuffer.concat(this.getElementValueGetterHTML(),this.getExternalChangeHandler());
}

XFormItem.prototype.getFocusHandlerHTML = function () {
	var formId = this.getFormGlobalRef(),
		itemId = this.getId()
	;
	
	var inputHelp =  this.getInheritedProperty("inputHelp");
	var clearInputHelpScript = "";
	if (inputHelp != null) {
		clearInputHelpScript = "if (this.value == '" + inputHelp + "') this.value=''; ";
		DBG.println("ClearnInputHelpScript = " + clearInputHelpScript);
	}
	
	var onFocusAction = null;
	if (this.getInheritedProperty("onFocus") != null) {
		onFocusAction = AjxBuffer.concat(" onfocus=\"", formId, ".onFocus('", itemId, "'); " ,	
				 clearInputHelpScript ,			 	
				 this.getInheritedProperty("onFocus") , ".call(" ,   this.getGlobalRef(), ", event )\"");
	}else{
		onFocusAction = AjxBuffer.concat(" onfocus=\"", formId, ".onFocus('", itemId, "');",
										clearInputHelpScript, "\"" );
	}
	return AjxBuffer.concat(
		//" onfocus=\"", formId, ".onFocus('", itemId, "')\"",
		//HC: unflexible hacking way to support the License Portal text field onFocus event
		onFocusAction ,		
		" onblur=\"", formId, ".onBlur('", itemId, "')\""
	);
}


XFormItem.prototype.getOnActivateHandlerHTML = function() {
	var method = this.getOnActivateMethod();
	if (method == null) return "";
	
	return AjxBuffer.concat(
			" ", this.getElementChangeHandler(), "=\"", 
			this.getGlobalRef(),".$onActivate(event||window.event)\""
		);
}

XFormItem.prototype.getClickHandlerHTML =
function () {
	var onClickFunc = this.getOnClickMethod();
	if (onClickFunc == null) return "" ;
	
	return AjxBuffer.concat(" onclick=\"", 
				this.getGlobalRef(),".$onClick(event||window.event)\""
			);
			
	return AjxBuffer.concat( onClickAction );	
}

/**
* Schedules {@link #handleKeyPressDelay} to fire later when the user finishes typing
* @param ev - "onkeypress" event 
* @param domItem - HTML form element
* @author Greg Solovyev
**/
XFormItem.prototype.handleKeyUp = function (ev, domItem) {
	var key = DwtKeyEvent.getCharCode(ev);
	// don't fire off another if we've already set one up unless this is an ENTER key
	if (!AjxUtil.isEmpty(this.keyPressDelayHdlr) && key != DwtKeyEvent.KEY_ENTER) {
		AjxTimedAction.cancelAction(this.keyPressDelayHdlr);
		this.keyPressDelayHdlr = null;
	}
	var form = this.getForm();
	var evt = new DwtKeyEvent();
	evt.setFromDhtmlEvent(ev);

	if (key == DwtKeyEvent.KEY_TAB) {
		DwtUiEvent.setBehaviour(ev, true, false);
		return false;
	} else {
		var action = new AjxTimedAction(this, this.handleKeyPressDelay, [evt, domItem]);
		//XForm.keyPressDelayHdlr = setTimeout(XForm.handleKeyPressDelay, 250, item, ev, formItem);
		this.keyPressDelayHdlr = AjxTimedAction.scheduleAction(action, 250);
	}
};

XFormItem.prototype.handleKeyDown = function (ev, domItem) {
	ev = (ev != null)? ev: window.event;
	var key = DwtKeyEvent.getCharCode(ev);
	if (key == DwtKeyEvent.KEY_ENTER) {
		// By telling the browser just to let this do the default, and 
		// not let the event bubble, our keyup handler
		// wil see the enter key.
		DwtUiEvent.setBehaviour(ev, true, true);
		return false;
	} else if (key == DwtKeyEvent.KEY_TAB) {
		DwtUiEvent.setBehaviour(ev, true, false);
		var currentTabId = XFormItem.getParentTabGroupId(this) ;
		//DBG.println(AjxDebug.DBG1, "Current Tab ID = " + currentTabId);
		if(ev.shiftKey)
			this.getForm().focusPrevious(this.getId(), currentTabId);	
		else
			this.getForm().focusNext(this.getId(), currentTabId);	
		return false;
	}
	return true;
};

/**
* Implements delayed handling of "keypress" event. 
* Calls change handler script on the item.
* See {@link XFormItem.#getChangehandlerJSCode} for change handler script.

**/
XFormItem.prototype.handleKeyPressDelay = function(ev, domItem) {	
	this.keyPressDelayHdlr = null;
	if (this.$changeHandlerFunc == null) {
		var JSCode = this.getChangehandlerJSCode();
		this.$changeHandlerFunc = new Function("event", JSCode);
	}
	if (this.$changeHandlerFunc) {
		this.$changeHandlerFunc.call(domItem, ev);
	}
};

XFormItem.prototype.getKeyPressHandlerHTML = function () {

	var keydownEv = "onkeydown";
	if (AjxEnv.isNav) {
		keydownEv = "onkeypress";
	}
	return AjxBuffer.concat(" ", keydownEv,"=\"",this.getGlobalRef(), ".handleKeyDown(event, this)\"",
						   " onkeyup=\"", this.getGlobalRef(), ".handleKeyUp(event, this)\"");
};


//
//	container
//


XFormItem.prototype.outputContainerTDStartHTML = function (html,  colSpan, rowSpan) {
	var _align = this.getAlign();
	html.append( "<td id=\"",  this.getId(), "___container\"",
					(colSpan > 1 ? " colspan=" + colSpan : ""),
					(rowSpan > 1 ? " rowspan=" + rowSpan : ""),
					this.getContainerCssString(), 
					(_align != _UNDEFINED_ ? " align='" + _align + "'" : ""),
					">"
	);
} 

XFormItem.prototype.outputContainerTDEndHTML = function (html) {
	html.append("</td id=\"",  this.getId(), "___container\">");
} 


//
//	element div
//
// for items that are effectively elements (or are drawn by something other than this form)
// NOTE: you can pass in any random CSS properties you want in cssStyle
XFormItem.prototype.outputElementDivStart = function (html) {
	html.append( "<div id=", this.getId(), this.getCssString(), " xform_type='elementDiv'>");
}

XFormItem.prototype.outputElementDivEnd = function (html) {
	html.append("</div id=\"", this.getId(), "\">");
}

//
//	label td
//
XFormItem.prototype.outputLabelCellHTML = function (html,  rowSpan, labelLocation) {
	var label = this.getLabel();
	if (label == null) return;
	
	if (label == "") label = "&nbsp;";
	
	if (labelLocation == _INLINE_) {
		var style = this.getLabelCssStyle();
		if (style == null) style = "";
		style = "position:relative;left:10;top:5;text-align:left;background-color:#eeeeee;margin-left:5px;margin-right:5px;" + style;
		html.append( "<div id=\"", this.getId(),"___label\"", 
								this.getLabelCssString(null, style), ">",
								label,
							"</div>"
					);
	} else {
		//lable for is allowd the label to associate with an input item
		var enableLabelFor = this.getInheritedProperty("enableLabelFor");
		if (enableLabelFor) {
			html.append( "<td id=\"", this.getId(),"___label\"", 
								this.getLabelCssString(), 
								(rowSpan > 1 ? " rowspan=" + rowSpan : ""), ">", 
								"<label for='", this.getId(), "'>", label, "</label>"
				);
		}else{
            if(!this.getInheritedProperty("helpTooltip") ||
               !this.getInheritedProperty("showHelpTooltip") ||
               !this.getInheritedProperty("hideHelpTooltip") ){
                html.append( "<td id=\"", this.getId(),"___label\"",
                    this.getLabelCssString(),
                    (rowSpan > 1 ? " rowspan=" + rowSpan : ""), ">",
                    label
                );
            }else{
                html.append( "<td id=\"", this.getId(),"___label\"",
                    this.getLabelCssString(),
                    " onclick=\"", "XFormItem.prototype.showHelpTooltip" ,
			        ".call(" ,   this.getGlobalRef(), ", event );\" ",
                    " onmouseout=\"", "XFormItem.prototype.hideHelpTooltip" ,
			        ".call(" ,   this.getGlobalRef(), ", event );\" ",
                    (rowSpan > 1 ? " rowspan=" + rowSpan : ""), ">",
                    label
                );
            }
		}
		if (this.getRequired()) {
			html.append("<span class='redAsteric'>*</span>");
		}
		html.append("</td>");
	}


}

XFormItem.getParentTabGroupId =
function (item){
	//DBG.println(AjxDebug.DBG1, "Enter the getParentTabGroupId() ...");
	
	while (item != null) {
		var p = item.getParentItem();
		if (p == null || (! p instanceof XFormItem)){
			return null ; //no parent item or p is not an XFormItem
		}else if (p instanceof Group_XFormItem && p.getInheritedProperty("isTabGroup") == true) {	
			return p.getId ();
		}
		//DBG.println(AjxDebug.DBG1, "Continue the getParentTabGroupId() ...");
		item = p ;
	}
}


//
//	change handling
//

XFormItem.prototype.elementChanged = function(elementValue, instanceValue, event) {
	this.getForm().itemChanged(this.getId(), elementValue, event);
}

//
//	get and set instance values!
//


XFormItem.prototype.getInstanceValue = function (path) {
	if (path == null) path = this.getRefPath();
	if (path == null) return null;
	return this.getModel().getInstanceValue(this.getInstance(), path);
}

//NOTE: model.getInstance() gets count of PARENT
XFormItem.prototype.getInstanceCount = function () {
	if (this.getRefPath() == null) return 0;
	return this.getModel().getInstanceCount(this.getInstance(), this.getRefPath());
}

XFormItem.prototype.setInstanceValue = function (value, path) {
	if (path == null) path = this.getRefPath();
	if (path == null) return null;
	return this.getModel().setInstanceValue(this.getInstance(), path, value);
}
XFormItem.prototype.set = XFormItem.prototype.setInstancevalue;

XFormItem.getValueFromHTMLSelect = function (element) {
	var values = [];
	for (var i = 0; i < element.options.length; i++) {
		if (element.options[i].selected) {
			values[values.length] = element.options[i].value;	
		}
	}
	return values.join(",");
}

XFormItem.prototype.getValueFromHTMLSelect = function(element) {
	if (element == null) element = this.getElement();
	return XFormItem.getValueFromHTMLSelect(element);
}

XFormItem.updateValueInHTMLSelect1 = function (newValue, element, selectionIsOpen) {
	if (element == null) return null;
	if (selectionIsOpen == null) selectionIsOpen = false;
	
	var options = element.options;
	for (i = 0; i < options.length; i++) {
		var choice = options[i];
		if (choice.value == newValue) {
			element.selectedIndex = i;
			return element.value;
		}
	}
	// default to the first element if nothing was selected (?)
	if (options.length > 0) {
		element.selectedIndex = 0;
		return options[0].value;
	}
	return null;
}
XFormItem.prototype.updateValueInHTMLSelect1 = function (newValue, element, selectionIsOpen) {
	if (element == null) element = this.getElement();
	if (selectionIsOpen == null) selectionIsOpen = this.getSelectionIsOpen();
	return XFormItem.updateValueInHTMLSelect1(newValue, element, selectionIsOpen);
}


XFormItem.updateValueInHTMLSelect = function (newValue, element, selectionIsOpen) {
	if (element == null) return null;
	if (newValue == null) newValue = "";
	if (selectionIsOpen == null) selectionIsOpen = false;
	
	// assumes newValue is a comma-delimited string or an array
	if (typeof newValue == "string") newValue = newValue.split(",");
	// hack up newValue to make searching for a particular option newValue easier
	var uniqueStartStr = "{|[", 
		uniqueEndStr = "]|}"
	;
	newValue = uniqueStartStr + newValue.join(uniqueEndStr + uniqueStartStr) + uniqueEndStr;
	
	var options = element.options;
	var anySelected = false;
	for (var i = 0; i < options.length; i++) {
		var isPresent = (newValue.indexOf(uniqueStartStr + options[i].value + uniqueEndStr) > -1);
		options[i].selected = isPresent;
		anySelected = anySelected || isPresent;		
	}
	
	if (!anySelected && !selectionIsOpen) {
		// select the first value???
		options[0].selected = true;
	}
}

XFormItem.prototype.updateValueInHTMLSelect = function (newValue, element, selectionIsOpen) {
	if (newValue == null) newValue = "";
	if (element == null) element = this.getElement();
	if (selectionIsOpen == null) selectionIsOpen = this.getSelectionIsOpen();
	return XFormItem.updateValueInHTMLSelect(newValue, element, selectionIsOpen);
}

XFormItem.prototype.getChoicesHTML = function() {
	var choices = this.getNormalizedChoices();
	if (choices == null) return "";	//throw an error?
	var html = new AjxBuffer();
	

	this.outputChoicesHTMLStart(html);
	var values = choices.values;
	var labels = choices.labels;
    var visible = choices.visible ;

    var choiceCssClass = this.getChoiceCssClass();
	for (var i = 0; i < values.length; i++) {
        if (visible && (visible[i] == false)) {
            //don't display this choice
        }else {       //by default, the choice should be visible
            html.append("", this.getChoiceHTML(i, values[i], labels[i], choiceCssClass, ""));
        }
    }
	this.outputChoicesHTMLEnd(html);
	return html.toString();
}

XFormItem.prototype.outputChoicesHTMLStart = function(html) {
	return;
}
XFormItem.prototype.outputChoicesHTMLEnd = function(html) {
	return;
}

XFormItem.prototype.getChoiceCssClass = function() {
	return "";
}

XFormItem.prototype.getChoiceHTML = function (itemNum, value, label, cssClass) {
	return AjxBuffer.concat("<option value=\"", value, "\">", label,"</option>");
}

XFormItem.prototype.updateChoicesHTML = function () {
	this.cleanChoiceDisplay();

	// NOTE: setting the innerHTML of the options doesn't work
	//	for now, just set the outer HTML of the entire widget
	// TODO: do this by frobbing the options manually for speed and so things don't flash
	var html = new AjxBuffer();
	this.outputHTML(html, new AjxBuffer());
	if (this.getContainer())  this.getContainer().innerHTML = html.toString();
	return;       
}


XFormItem.prototype.getInheritedProperty = function(prop) {
	// first look in the instance attributes
	if (this.__attributes[prop] !== _UNDEFINED_) return this.__attributes[prop];

	// look up the inheritance chain for this type
	if (this[prop] !== _UNDEFINED_) return this[prop];

	// if not found there, look in the xmodel
	var modelItem = this.__modelItem;
	if (modelItem && modelItem[prop]) return modelItem[prop];

	return null;
}

// NOTE: cacheProp MUST be different than prop!
XFormItem.prototype.cacheInheritedProperty = function (prop, cacheProp) {
	if (this[cacheProp] !== _UNDEFINED_) return this[cacheProp];
	return (this[cacheProp] = this.getInheritedProperty(prop));
}

XFormItem.prototype.cacheInheritedMethod = function (methodName, cacheProp, arguments) {
	if (this[cacheProp] !== _UNDEFINED_) return this[cacheProp];
	var func = this.getInheritedProperty(methodName);
	if (func != null) func = this.convertToFunction(func, arguments);
	this[cacheProp] = func;
	return func;
}




//
//	properties of the element after its' been drawn
//


XFormItem.prototype.getElement = XForm.prototype.getElement;
XFormItem.prototype.showElement = function (id) {
	XForm.prototype.showElement.call(this, id);
}

XFormItem.prototype.getIsVisible = function () {
	return this.__isVisible;
}

XFormItem.prototype.getIsEnabled = function () {
	return this.__isEnabled;
}
 
XFormItem.prototype.hideElement = function (id,isBlock) {
	XForm.prototype.hideElement.call(this,id,isBlock);
}

XFormItem.prototype.createElement = XForm.prototype.createElement;

XFormItem.estimateMyWidth = function (label,withIcon,extraMargin, minimum) {
	var width = (String(label).length/2)*XForm.FONT_WIDTH1 + (String(label).length/2)*XForm.FONT_WIDTH2 + 14;
	if(withIcon)
		width = width + 24;
	
	if(extraMargin>0)
		width = width + extraMargin;
	
	width = (width >= minimum) ? width : minimum;
	return [width,"px"].join("");
}

XFormItem.prototype.getWidget = function() {
	return this.widget;
}

XFormItem.prototype.setWidget = function(widget) {
	this.widget = widget;
}


XFormItem.prototype.getContainer = function() {
	return this.getElement(this.getId() + "___container");
}
XFormItem.prototype.getLabelContainer = function() {
	return this.getElement(this.getId() + "___label");
}
XFormItem.prototype.show = function() {
	if(this.deferred) {
		this._outputHTML();
	}	
	var container = this.getLabelContainer();
	if (container) this.showElement(container);
	container = this.getContainer();
	if (container != null) {
		this.showElement(container);
	} 
	this.__isVisible = true;
}

XFormItem.prototype.hide = function(isBlock) {
	isBlock = this.getInheritedProperty("isBlockElement") || isBlock;
	var container = this.getLabelContainer()
	if (container) this.hideElement(container,isBlock);
	container = this.getContainer();
	if (container != null) {
		this.hideElement(container,isBlock);
	} else {
		var items = this.getItems();
		if (items != null) {
			for (var i = 0; i < items.length; i++) {
				items[i].hide(isBlock);
			}
		}
	}
	this.__isVisible = false;
}

XFormItem.prototype.focus = function () {
	this.getForm().focusElement(this.getId());
};


//
//	SIMPLE ATTRIBUTE ACCESSORS
//
//	NOTE: this is effectively the public API for the properties you can define
//			for a FormItem
//

XFormItem.prototype.getRequired = function() {
	return this.getInheritedProperty("required");
}

XFormItem.prototype.getValue = function() {
	return this.getInheritedProperty("value");
}

// SPECIAL CASE:  don't take ITEMS from the model...
//XXX NON-STANDARD
XFormItem.prototype.getItems = function () {
	if (this.items) return this.items;
	return this.__attributes.items;
}

XFormItem.prototype.getChoices = function () {
	return this.getInheritedProperty("choices");
}

// normalized choices look like:  {values:[v1, v2, v3...], labels:[l1, l2, l3...]}
XFormItem.prototype.getNormalizedChoices = function () {
	if (this.$normalizedChoices) return this.$normalizedChoices;

	var choices = this.getChoices();
	if (choices == null) return null;
    if (typeof choices == "function") choices = choices.call(this) ;
    
    var normalizedChoices;
	if (typeof choices.getChoices == "function") {
		normalizedChoices = choices.getChoices();
	} else if (AjxUtil.isArray(choices)) {
		// it's either an array of objects or an array of strings
		if (typeof choices[0] == "object") {
			// list of objects
			normalizedChoices = XFormChoices.normalizeChoices(choices, XFormChoices.OBJECT_LIST);
		} else {
			// list of simple values
			normalizedChoices = XFormChoices.normalizeChoices(choices, XFormChoices.SIMPLE_LIST);
		}
	} else {
		// assume it's a hash
		normalizedChoices = XFormChoices.normalizeChoices(choices, XFormChoices.HASH);
	}
	this.$normalizedChoices = normalizedChoices;
	return this.$normalizedChoices;
}


XFormItem.prototype.getNormalizedValues = function () {
	var choices = this.getNormalizedChoices();
	if (choices) return choices.values;
	return null;
}


XFormItem.prototype.getNormalizedLabels = function () {
	var choices = this.getNormalizedChoices();
	if (choices) return choices.labels;
	return null;
}
	
	
	
//
//	appearance methods
//

XFormItem.prototype.getAppearance = function () {
	return this.getInheritedProperty("appearance");
}
XFormItem.prototype.getCssClass = function () {
	return this.getInheritedProperty("cssClass");
}

XFormItem.prototype.getCssStyle = function () {
	return this.getInheritedProperty("cssStyle");
}

XFormItem.prototype.getLabel = function (value) {
	return this.getInheritedProperty("label");
}

XFormItem.prototype.getErrorCssClass = function () {
	return this.getInheritedProperty("errorCssClass");
}
XFormItem.prototype.getLabelCssClass = function (className) {
	if (className != null) return className;
	return this.getInheritedProperty("labelCssClass");
}

XFormItem.prototype.getLabelCssStyle = function (style) {
	if (style != null) return style;
	return this.getInheritedProperty("labelCssStyle");
}

XFormItem.prototype.getLabelWrap = function () {
	return this.getInheritedProperty("labelWrap");
}

XFormItem.prototype.getLabelLocation = function () {
	return this.getInheritedProperty("labelLocation");
}

XFormItem.prototype.getContainerCssClass = function () {
	return this.getInheritedProperty("containerCssClass");
}

XFormItem.prototype.getContainerCssStyle = function () {
	return this.getInheritedProperty("containerCssStyle");
}

XFormItem.prototype.getTableCssClass = function () {
	return this.getInheritedProperty("tableCssClass");
}
XFormItem.prototype.getTableCssStyle = function () {
	return this.getInheritedProperty("tableCssStyle");
}

XFormItem.prototype.getNowrap = function () {
	return this.getInheritedProperty("nowrap");
}

XFormItem.prototype.getWidth = function () {
	return this.cacheInheritedProperty("width","_width");
}

XFormItem.prototype.getHeight = function () {
	return this.getInheritedProperty("height");
}

XFormItem.prototype.getOverflow = function () {
	return this.getInheritedProperty("overflow");
}

XFormItem.prototype.getNumCols = function () {
	return this.getInheritedProperty("numCols");
}

XFormItem.prototype.getAlign = function () {
	return this.getInheritedProperty("align");
}


XFormItem.prototype.getValign = function() {
	return this.getInheritedProperty("valign");
}

XFormItem.prototype.getName = function () {
	return this.getInheritedProperty("name");
}

// NEW TABLE LAYOUT STUFF
XFormItem.prototype.useParentTable = true;
XFormItem.prototype.getUseParentTable = function () {
	return this.getInheritedProperty("useParentTable");
}
XFormItem.prototype.colSizes = _UNDEFINED_;
XFormItem.prototype.getColSizes = function () {
	return this.getInheritedProperty("colSizes");
}
XFormItem.prototype.colSpan = 1;
XFormItem.prototype.getColSpan = function () {
	return this.getInheritedProperty("colSpan");
}
XFormItem.prototype.rowSpan = 1;
XFormItem.prototype.getRowSpan = function () {
	return this.getInheritedProperty("rowSpan");
}

/* displayGrid:
*    1) true: display grid border
*    2) false: don't display grid border
*    3) _UNDEFINED_: inherid the parent's setting
* */
XFormItem.prototype.displayGrid = _UNDEFINED_;
XFormItem.prototype.getDisplayGrid = function () {
	return this.getInheritedProperty("displayGrid");
}
// END NEW TABLE LAYOUT STUFF

// error handling
XFormItem.prototype.getErrorLocation = function () {
	return this.getInheritedProperty("errorLocation");
};

//
//	convenience methods to figure out drawing types for you
//

// return the "label" in the choices array for this item
//	(allows us to do lookup of displayed values easily)
XFormItem.prototype.getChoiceLabel = function (value) {
	var choices = this.getNormalizedChoices();
	if (choices == null) return value;
	if (value == null) value = "" ; //make it an empty string, so empty value label can be returned
    
    // choices will look like:  {values:[v1, v2, v3...], labels:[l1, l2, l3...]}
	var values = choices.values;
	for (var i = 0; i < values.length; i++) {
		if (values[i] == value) {
			return choices.labels[i];
		}
	}
	// if we didn't find it, simply return the original value
	return value;
}

// return the "label" in the choices array for this item
//	(allows us to do lookup of displayed values easily)
// If no matching choice is found, the label is returned. 
XFormItem.prototype.getChoiceValue = function (label) {
	function labelComparator (a, b) {
			return String(a).toLowerCase() < String(b).toLowerCase() ? -1 : (String(a).toLowerCase() > String(b).toLowerCase() ? 1 : 0);
	 };
	var choices = this.getNormalizedChoices();
	if (choices == null) return label;
	
	// choices will look like:  {values:[v1, v2, v3...], labels:[l1, l2, l3...]}
	// bug 6738: sort will change the mapping between value and label.
	/*
	var labels = choices.labels;
	var vec = AjxVector.fromArray(labels);
	vec.sort(labelComparator);
	var ix = vec.binarySearch(label,labelComparator); */
	var labels = choices.labels;
	var ix = -1;
	for (var i=0; i < labels.length ; i++ ){
		if (labelComparator (label, labels[i]) == 0) {
			ix = i ;
			break;
		}		
	}
	
	if(ix>=0) 
		return choices.values[ix];
	else 		
		//return choices.values[0];// If no matching choice is found, the label is returned, instead of the first value
		return label;
}

// return the number of the choice for a particular value
//	returns -1 if not found
XFormItem.prototype.getChoiceNum = function (value) {
	var choices = this.getNormalizedChoices();
	if (choices == null) return -1;
	
	// choices will look like:  {values:[v1, v2, v3...], labels:[l1, l2, l3...]}
	var values = choices.values;
	for (var i = 0; i < values.length; i++) {
		if (values[i] == value) {
			return i;
		}
	}
	return -1
}

XFormItem.prototype.getCssString = function () {
	var css = (this.getCssClass() || '');
	if (css != '' && css != null) css = " class=\"" + css + "\"";

	var style = (this.getCssStyle() || '');

	var width = this.getWidth();
	if (width != null && width != "auto") {
		if(style.length)
			style += ";";
			
		style += "width:" + width;
	}

	var height = this.getHeight();
	if (height != null) {
		if(style.length)
			style += ";";
	
		style += "height:" + height;
	}

	var overflow = this.getOverflow();
	if (overflow != null) {
		if(style.length)
			style += ";";
	
		style += "overflow:" + overflow;
	}
	
	if (this.getNowrap()) {
		if(style.length)
			style += ";";
	
		style += "white-space:nowrap";
	}

	var valign = this.getValign();
	if (valign) {
		if(style.length)
			style += ";";
	
		style += "vertical-align:"+valign;
	}
	
	if (style != '') css += " style=\"" + style + ";\"";
	return css;
}


XFormItem.prototype.getLabelCssString = function (className, style) {
	var css = (this.getLabelCssClass(className) || '');
	if (css != '' && css != null) css = " class=\"" + css + "\"";
	var style = (this.getLabelCssStyle(style) || '');
	if (this.getLabelWrap() == false) {
		if(style.length)
			style += ";";

		style += "white-space:nowrap";
	}
	if (style != '') css += " style=\"" + style + ";\"";
	
	return css;
}




XFormItem.prototype.getTableCssString = function () {
	var css = (this.getTableCssClass() || '');
	if (css != '' && css != null) css = " class=\"" + css + "\"";

	var style = this.getTableCssStyle();
	if (style == null) style = '';
	
	var colSizes = this.getColSizes();
	if (colSizes != null) {
		if(style.length)
			style += ";";
					
		style += "table-layout:fixed";
	}

	var width = this.getWidth();
	if (width != null) 	style += ";width:"+ width;
	
	var overflow = this.getOverflow();
	if (overflow != null) {
		if(style.length)
			style += ";";

		style += "overflow:" + overflow;
	}

	return css + (style != null ? " style=\"" + style + ";\"" : "");
}


XFormItem.prototype.getContainerCssString = function () {
	var css = (this.getContainerCssClass() || '');
	if (css != '' && css != null) css = " class=\"" + css + "\"";
	var style = this.getContainerCssStyle();
	if (style == null) style = '';
	
	var align = this.getAlign();
	if (align != _LEFT_) {
		if (align == _CENTER_ || align == _MIDDLE_) {
			if(style.length)
				style += ";";
						
			style += "text-align:center";
		} else if (align == _RIGHT_) {
			if(style.length)
				style += ";";			
		
			style += "text-align:right";
		}
	}
	var valign = this.getValign();
	if (valign == _TOP_) {
		if(style.length)
			style += ";";
					
		style += "vertical-align:top";
	} else if (valign == _BOTTOM_) {
		if(style.length)
			style += ";";
					
		style += "vertical-align:bottom";
	} else if (valign == _CENTER_ || valign == _MIDDLE_) {
		if(style.length)
			style += ";";		
			
		style += "vertical-align:middle";
	}

	if (style != "") css += " style=\"" + style + ";\"";
	return css;
}




//
//	handling changes to items
//
XFormItem.prototype.getElementChangeHandler = function () {
	return this.getInheritedProperty("elementChangeHandler");
}




//
//	outputting, inserting and updating items
//

XFormItem.prototype.getForceUpdate = function() {
	return this.getInheritedProperty("forceUpdate");
}

XFormItem.prototype.getOutputHTMLMethod = function() {
	return this.convertToFunction(
				this.getInheritedProperty("outputHTML"),
				"html,currentCol"
		);
}

XFormItem.prototype.getElementChangedMethod = function () {
	return this.cacheInheritedMethod("elementChanged","$elementChanged","elementValue, instanceValue, event");
}

XFormItem.prototype.getUpdateElementMethod = function() {
	return this.cacheInheritedMethod("updateElement","$updateElement","newValue");
}

XFormItem.prototype.getDisplayValueMethod = function() {
	return this.cacheInheritedMethod("getDisplayValue","$getDisplayValue","newValue");
}

XFormItem.prototype.getUpdateVisibilityMethod = function() {
	return this.cacheInheritedMethod("updateVisibility","$updateVisibility");
}

XFormItem.prototype.getUpdateEnabledDisabledtMethod = function() {
	return this.cacheInheritedMethod("updateEnabledDisabled","$updateEnabledDisabled");
}

XFormItem.prototype.convertToFunction = function (script, arguments) {
	if ((script == null) || (typeof(script) == "function")) return script;
	if (typeof(this[script]) == "function") return this[script];
	// CLOSURE???
	return new Function(arguments, script);
}


// note that this form item's display needs to be updated
XFormItem.prototype.dirtyDisplay = function () {
	delete this.$lastDisplayValue;
}

// override the next method in your subclass to enable/disable element
XFormItem.prototype.setElementEnabled = function(enable) {}

// convenience methods that call the above routine
XFormItem.prototype.disableElement = function () {
	this.setElementEnabled(false);
	this.__isEnabled = false;
}

XFormItem.prototype.enableElement = function () {
	this.setElementEnabled(true);
	this.__isEnabled = true;
}

// you can use these to 
XFormItem.prototype.setElementDisabledProperty = function (enable) {
	this.getElement().disabled = (enable != true)
}


XFormItem.prototype.setElementEnabledCssClass = function (enable) {
	var el = this.getElement();
	if (!el) return;
	
	if (enable) {
		el.className = this.getCssClass();
	} else {
		el.className = (this.getCssClass() + "_disabled");
	}
}



//
//	_SELECT_ etc type properties
//
XFormItem.prototype.getSelection = function () {
	return this.getInheritedProperty("selection");
}

XFormItem.prototype.getSelectionIsOpen = function () {
	return this.getInheritedProperty("selection");
}

XFormItem.prototype.getOpenSelectionLabel = function () {
	return this.getInheritedProperty("openSelectionLabel");
}


//
//	_REPEAT_ type properties
//

XFormItem.prototype.getNumberToShow = function () {
	return this.getInheritedProperty("number");
}

XFormItem.prototype.getShowAddButton = function () {
	return this.getInheritedProperty("showAddButton");
}

XFormItem.prototype.getShowRemoveButton = function () {
	return this.getInheritedProperty("showRemoveButton");
}

XFormItem.prototype.getShowMoveUpButton = function () {
	return this.getInheritedProperty("showMoveUpButton");
}

XFormItem.prototype.getShowMoveDownButton = function () {
	return this.getInheritedProperty("showMoveDownButton");
}

XFormItem.prototype.getAddButton = function () {
	return this.getInheritedProperty("addButton");
}

XFormItem.prototype.getRemoveButton = function () {
	return this.getInheritedProperty("removeButton");
}

XFormItem.prototype.getMoveUpButton = function () {
	return this.getInheritedProperty("moveUpButton");
}

XFormItem.prototype.getMoveDownButton = function () {
	return this.getInheritedProperty("moveDownButton");
}

XFormItem.prototype.getAlwaysShowAddButton = function () {
	return this.getInheritedProperty("alwaysShowAddButton");
}

XFormItem.prototype.getRepeatInstance = function () {
	return this.getInheritedProperty("repeatInstance");
}




//
//	_IMAGE_ type properties
//

XFormItem.prototype.getSrc = function () {
	return this.getInheritedProperty("src");
}

XFormItem.prototype.getSrcPath = function () {
	return this.getInheritedProperty("srcPath");
}



//
//	_ANCHOR_, _URL_, etc
//
//	type defaults
XFormItem.prototype.getShowInNewWindow = function () {
	return this.getInheritedProperty("showInNewWindow");
}




//
//	internal properties for creating various item types
//


XFormItem.prototype.getWriteElementDiv = function () {
	return this.getInheritedProperty("writeElementDiv");
}

XFormItem.prototype.getMultiple = function () {
	return this.getInheritedProperty("multiple");
}

XFormItem.prototype.getAlwaysUpdateChoices = function () {
	return this.getInheritedProperty("alwaysUpdateChoices");
}

XFormItem.prototype.choicesAreDirty = function () {
	return (this._choiceDisplayIsDirty == true || this.getAlwaysUpdateChoices());
}

XFormItem.prototype.cleanChoiceDisplay = function () {
	this._choiceDisplayIsDirty = false;
}

XFormItem.prototype.showInputTooltip =
function (event) {
	var dwtEv = new DwtUiEvent(true);
	dwtEv.setFromDhtmlEvent(event)
	var shell = DwtShell.getShell(window);
	var tooltip = shell.getToolTip();
	tooltip.setContent(this.getInheritedProperty("toolTipContent"));
	tooltip.popup(dwtEv.docX, dwtEv.docY);
}

XFormItem.prototype.hideInputTooltip =
function (event) {
	var shell = DwtShell.getShell(window);
	var tooltip = shell.getToolTip();
	tooltip.popdown();
}









/**
 * @class defines XFormItem type _OUTPUT_
 * @constructor
 * 
 * @private
 */
Output_XFormItem = function() {}
XFormItemFactory.createItemType("_OUTPUT_", "output", Output_XFormItem, XFormItem);


//	type defaults
Output_XFormItem.prototype.writeElementDiv = true;
Output_XFormItem.prototype.labelWrap = true;
Output_XFormItem.prototype.cssClass =  "xform_output";	// element itself (or element div)
Output_XFormItem.prototype.containerCssClass =  "xform_output_container";	// element itself (or element div)

//	methods

Output_XFormItem.prototype.outputHTML = function (html) {
	// by defaut, we output the "attributes.value" if set 
	//	(in case an item only wants to write out on the initial draw)
	// NOTE: dereferencing through the choice map happens in getDisplayValue()
	var value = this.getValue();
	var method = this.getDisplayValueMethod();
	if (method) {
		value = method.call(this, value);
	}
	
	//set the onClick event handler
	var clickMethod = this.getClickHandlerHTML();
	var htmlWithEvent = null ;
	if (clickMethod != null && clickMethod != "") {
		htmlWithEvent = "<div " + this.getClickHandlerHTML() +
		 				">" + value + "</div>" ; 
	}
	
	html.append(htmlWithEvent || value);
}


Output_XFormItem.prototype.getDisplayValue = function(newValue) {
	// dereference through the choices array, if provided
	newValue = this.getChoiceLabel(newValue);

	if (newValue == null) {
		newValue = "";
	} else {
		newValue = "" + newValue;
	}
	return newValue;
}

Output_XFormItem.prototype.updateElement = function (newValue) {
	var el = this.getElement();
	if(el) {
	    //set the onClick event handler
	    var clickMethod = this.getClickHandlerHTML();
	    var htmlWithEvent = null ;
	    if (clickMethod != null && clickMethod != "") {
		    htmlWithEvent = "<div " + this.getClickHandlerHTML() +
		 				">" + newValue + "</div>" ;
	    }

        newValue = htmlWithEvent || newValue;
		this.getElement().innerHTML = newValue;
    }
}

Output_XFormItem.prototype.initFormItem = function () {
	
	XFormItem.prototype.initFormItem.call(this);
	
	// if we're dealing with an XFormChoices object...
	var choices = this.getChoices();
	if (choices == null || choices.constructor != XFormChoices) return;

	//	...set up to receive notification when its choices change
	var listener = new AjxListener(this, this.dirtyDisplay);
	choices.addListener(DwtEvent.XFORMS_CHOICES_CHANGED, listener);

    this.signUpForEvents();   //so when the instance value changed, the output display can be updated.
}

Output_XFormItem.prototype.dirtyDisplay = function () {
	XFormItem.prototype.dirtyDisplay.call(this);
	this._choiceDisplayIsDirty = true;
	delete this.$normalizedChoices;
}

// set up how disabling works for this item type
Output_XFormItem.prototype.setElementEnabled = XFormItem.prototype.setElementEnabledCssClass;


/**
 * @class defines XFormItem type _TEXTFIELD_
 * @constructor
 * 
 * @private
 */
Textfield_XFormItem = function() {}
XFormItemFactory.createItemType("_TEXTFIELD_", "textfield", Textfield_XFormItem, XFormItem);
// aliases for _TEXTFIELD_:  _INPUT_
XFormItemFactory.registerItemType("_INPUT_", "input", Textfield_XFormItem);

//	type defaults
//Textfield_XFormItem.prototype.width = 100;
Textfield_XFormItem.prototype._inputType = "text";
Textfield_XFormItem.prototype.cssClass = "xform_field";
Textfield_XFormItem.prototype.elementChangeHandler="onchange";
//Textfield_XFormItem.prototype.onclickHandler="onclick";
Textfield_XFormItem.prototype.focusable = true;
Textfield_XFormItem.prototype.nowrap = false;
Textfield_XFormItem.prototype.labelWrap = true;
Textfield_XFormItem.prototype.containerCssClass = "xform_field_container";
Textfield_XFormItem.prototype.visibilityChecks = [XFormItem.prototype.hasReadPermission];
Textfield_XFormItem.prototype.enableDisableChecks = [XFormItem.prototype.hasWritePermission];
//	methods
Textfield_XFormItem.prototype.outputHTML = function (html,  currentCol) {
	var inputType = this._inputType;
	var value = this.getValue();
	var modelItem = this.getModelItem();
	var inputHelp = this.getInheritedProperty("inputHelp");


	/***
//XXX this is probably not the best way to tell if we only want to enter numbers...
	if (modelItem && (modelItem.type == _NUMBER_)) {// || modelItem.type == _COS_NUMBER_)) {
		var keyStrokeHandler = " onkeypress=\""
//			+"',45,46,48,49,50,51,52,53,54,55,56,57,69,101,'.indexOf(','+(event||window.event).keyCode+',') > -1\""		
				+"var code = ','+(event||window.event).which+',';"
				+"var isValidChar = (',45,46,48,49,50,51,52,53,54,55,56,57,69,101,'.indexOf(code) > -1);"
				+"DBG.println(code + ':'+isValidChar);"
				+"event.returnValue = isValidChar;"
				+"return isValidChar;"
				+"\""
	}
	/***/
	html.append( 
			"<input autocomplete='off' id=\"", this.getId(),"\" type=\"", inputType, "\"", this.getCssString(), 
				this.getChangeHandlerHTML(), this.getFocusHandlerHTML(),
				this.getClickHandlerHTML(), this.getMouseoutHandlerHTML(),
				(value != null ? " value=\"" + value + "\"" :""), //: (inputHelp != null ? " value=\"" + inputHelp + "\""
			">");
}

Textfield_XFormItem.prototype.getClickHandlerHTML =
function () {
	var formId = this.getFormGlobalRef(), 
		itemId = this.getId()
		;
	
	var onClickAction = "";
	
	var onClickFunc = this.getInheritedProperty("onClick") ;
	onClickAction = AjxBuffer.concat(" onclick=\"", onClickFunc || "XFormItem.prototype.showInputTooltip" , 
			".call(" ,   this.getGlobalRef(), ", event );\" ");
			
	return AjxBuffer.concat( onClickAction );	
}

Textfield_XFormItem.prototype.getMouseoutHandlerHTML =
function () {
	var formId = this.getFormGlobalRef(), 
		itemId = this.getId()
		;
	
	var onMouseoutAction = "";
	
	var onMouseoutFunc = this.getInheritedProperty("onMouseout") ;
	onMouseoutAction = AjxBuffer.concat(" onmouseout=\"", onMouseoutFunc || "XFormItem.prototype.hideInputTooltip" , 
						".call(" ,   this.getGlobalRef(), ", event );\" ");
						
	return AjxBuffer.concat( onMouseoutAction );	
}

Textfield_XFormItem.prototype.updateElement = function(newValue) {
	if (newValue == null) newValue = this.getValue();
	var inputHelp = this.getInheritedProperty("inputHelp");
	/*
	DBG.println("In updateElement: " + "newValue=" + newValue + "###" + "elementValue=" + this.getElement().value);	*/
	if ((newValue == null) && (inputHelp != null)) {
		 newValue = inputHelp ;
	}else if (newValue == null){
		 newValue = "";
	}
	
	if (this.getElement() && this.getElement().value != newValue) {
		this.getElement().value = newValue;
	}
}

// set up how disabling works for this item type
Textfield_XFormItem.prototype.setElementEnabled  = function (enabled) {
	this.setElementDisabledProperty(enabled);
	this.setElementEnabledCssClass(enabled);
}





/**
 * @class defines XFormItem type _SECRET_
 * @constructor
 * 
 * @private
 */
Secret_XFormItem = function() {}
XFormItemFactory.createItemType("_SECRET_", "secret", Secret_XFormItem, Textfield_XFormItem);
// alias for the SECRET class:  PASSWORD
XFormItemFactory.registerItemType("_PASSWORD_", "password", Secret_XFormItem);


//	type defaults
Secret_XFormItem.prototype._inputType = "password";
Secret_XFormItem.prototype.focusable = true;




/**
 * @class defines XFormItem type _FILE_
 * @constructor
 * 
 * @private
 */
File_XFormItem = function() {}
XFormItemFactory.createItemType("_FILE_", "file", File_XFormItem, Textfield_XFormItem)

//	type defaults
File_XFormItem.prototype._inputType = "file";
File_XFormItem.prototype.forceUpdate = false;
File_XFormItem.prototype.focusable = true;



/**
 * @class defines XFormItem type _TEXTAREA_
 * @constructor
 * 
 * @private
 */
Textarea_XFormItem = function() {}
XFormItemFactory.createItemType("_TEXTAREA_", "textarea", Textarea_XFormItem, Textfield_XFormItem)

Textarea_XFormItem.prototype.width = "100%";
Textarea_XFormItem.prototype.height = 100;
Textarea_XFormItem.prototype.focusable = true;
//	methods
Textarea_XFormItem.prototype.outputHTML = function (html,   currentCol) {
	var wrap = this.getInheritedProperty("textWrapping");
	if (!wrap)
		wrap = "off";
		
	html.append( 
		"<textarea id=\"", this.getId(), "\"", this.getCssString(),
				this.getChangeHandlerHTML(), this.getFocusHandlerHTML(), "wrap='", wrap, "'",
		"></textarea>");
}

// you can use these to 
Textarea_XFormItem.prototype.setElementDisabledProperty = function (enable) {
	this.getElement().disabled = (enable != true);
	this.getElement().readOnly = (enable != true)
}

Textarea_XFormItem.prototype.getKeyPressHandlerHTML = function () {

        var keydownEv = "onkeydown";
        if (AjxEnv.isNav || AjxEnv.isChrome || AjxEnv.isSafari) {
                keydownEv = "onkeypress";
        }
        return AjxBuffer.concat(" ", keydownEv,"=\"",this.getGlobalRef(), ".handleKeyDown(event, this)\"",
                                                   " onkeyup=\"", this.getGlobalRef(), ".handleKeyUp(event, this)\"");
};

/**
 * @class defines XFormItem type _CHECKBOX_
 * @constructor
 * 
 * @private
 */
Checkbox_XFormItem = function() {}
XFormItemFactory.createItemType("_CHECKBOX_", "checkbox", Checkbox_XFormItem, XFormItem)

//	type defaults
Checkbox_XFormItem.prototype._inputType = "checkbox";
Checkbox_XFormItem.prototype.elementChangeHandler = "onclick";
Checkbox_XFormItem.prototype.labelLocation = (appNewUI?_LEFT_:_RIGHT_);
Checkbox_XFormItem.prototype.cssClass = "xform_checkbox";
Checkbox_XFormItem.prototype.labelCssClass = "xform_checkbox";
Checkbox_XFormItem.prototype.align = (appNewUI?_LEFT_:_RIGHT_);
Checkbox_XFormItem.prototype.trueValue = _UNDEFINED_;		// Don't set in proto so model can override
Checkbox_XFormItem.prototype.falseValue = _UNDEFINED_;
Checkbox_XFormItem.prototype.focusable = true;
Checkbox_XFormItem.prototype.visibilityChecks = [XFormItem.prototype.hasReadPermission];
Checkbox_XFormItem.prototype.enableDisableChecks = [XFormItem.prototype.hasWritePermission];
Checkbox_XFormItem.prototype.nowrap = false;
Checkbox_XFormItem.prototype.labelWrap = true;
//	methods
Checkbox_XFormItem.prototype.outputHTML = function (html, currentCol) {
	// figure out how to show the checkbox as checked or not
	var checked = "";
	if (this.getInstanceValue() == this.getTrueValue()) {
		checked = " CHECKED";
	}
	html.append( 
		"<input autocomplete='off' id=\"", this.getId(),"\" type=\"", this._inputType, "\"",  
				this.getChangeHandlerHTML(), this.getFocusHandlerHTML(), checked,
		">");
}


Checkbox_XFormItem.prototype.getTrueValue = function () {
	var trueValue = this.getInheritedProperty("trueValue");
	if (trueValue == null) trueValue = true;
	return trueValue;
}

Checkbox_XFormItem.prototype.getFalseValue = function () {
	var falseValue = this.getInheritedProperty("falseValue");
	if (falseValue == null) falseValue = false;
	return falseValue;
}



Checkbox_XFormItem.prototype.updateElement = function(newValue) {
	newValue = (newValue == this.getTrueValue());
	this.getElement().checked = newValue;
}

Checkbox_XFormItem.prototype.getElementValueGetterHTML = function () {
	var trueValue = this.getTrueValue();
	if (trueValue !== _UNDEFINED_) {
		if (typeof trueValue == "string") trueValue = "'" + trueValue + "'";
		
		var falseValue = this.getFalseValue();
		if (typeof falseValue == "string") falseValue = "'" + falseValue + "'";
	
		if (trueValue == null) trueValue = true;
		if (falseValue == null) falseValue = false;
	
		return AjxBuffer.concat(
			"var value = (this.checked ? ",  trueValue, " : ", falseValue, ");"
		);
	} else {
		return "var value = '"+this.getValue()+"';";
	}
}


Checkbox_XFormItem.prototype.outputContainerTDEndHTML = function (html) {
    var tdLabel = this.getInheritedProperty("subLabel");
    // for compatible with old UI
    if(appNewUI && tdLabel && tdLabel != "") {
        tdLabel = " " + tdLabel;
    } else if (appNewUI && tdLabel == null)
        tdLabel = " Enabled";
    else tdLabel = "";

    html.append(tdLabel + "</td id=\"",  this.getId(), "___container\">");
}

// set up how disabling works for this item type
//	XXXX eventually we want to disable our label as well...
Checkbox_XFormItem.prototype.setElementEnabled = XFormItem.prototype.setElementDisabledProperty;



/**
 * @class defines XFormItem type _RADIO_
 * @constructor
 * 
 * @private
 */
Radio_XFormItem = function() {}
XFormItemFactory.createItemType("_RADIO_", "radio", Radio_XFormItem, Checkbox_XFormItem)

//	type defaults
Radio_XFormItem.prototype._inputType = "radio";
Radio_XFormItem.prototype.focusable = true;
Radio_XFormItem.prototype.groupname=null;
Radio_XFormItem.prototype.subLabel = (appNewUI?"":null);
Radio_XFormItem.prototype.align = _RIGHT_;
//	methods

Radio_XFormItem.prototype.updateElement = function(newValue) {
	this.getElement().checked = (this.getValue() == newValue);
}

//	methods
Radio_XFormItem.prototype.outputHTML = function (html,  currentCol) {
	// figure out how to show the checkbox as checked or not
	var checked = "";
	if (this.getInstanceValue() == this.getTrueValue()) {
		checked = " CHECKED";
	}
	html.append( 
		"<input autocomplete='off' id=\"", this.getId(),"\" type=\"", this._inputType, "\"",  
				this.getChangeHandlerHTML(), this.getFocusHandlerHTML(), checked);
	var groupname = this.getInheritedProperty("groupname");
	if(groupname) {
			html.append(" name='",groupname,"'");
	}
	html.append(">");
}

/**
 * @class defines XFormItem type _RADIO_LABEL_
 * @constructor
 * 
 * @private
 */
Radio_Label_XFormItem = function() {}
XFormItemFactory.createItemType("_RADIO_LABEL_", "radio_label", Radio_Label_XFormItem, Radio_XFormItem)

//	type defaults
Radio_Label_XFormItem.prototype._inputType = "radio";
Radio_Label_XFormItem.prototype.focusable = true;
Radio_Label_XFormItem.prototype.groupname=null;
//	methods

Radio_XFormItem.prototype.elementChanged = function(elementValue, instanceValue, event) {
	if(elementValue==true) {
		//this.setInstanceValue(this.getValue());
		this.getForm().itemChanged(this.getId(), this.getValue(), event);
	}	
}

Radio_XFormItem.prototype.updateElement = function(newValue) {
	this.getElement().checked = (this.getValue() == newValue);
	var labelEl = XFG.getEl((this.getId()+"___labelValue"));
	if(labelEl) {
		var labelRef = this.getInheritedProperty("labelRef");
		if (labelRef == null) 
			return;
		var label = this.getInstanceValue(labelRef);	
		labelEl.innerHTML = label;
	}
}

//	methods
Radio_Label_XFormItem.prototype.outputHTML = function (html,  currentCol) {
	// figure out how to show the checkbox as checked or not
	var checked = "";
	if (this.getInstanceValue() == this.getTrueValue()) {
		checked = " CHECKED";
	}
	html.append( 
		"<input autocomplete='off' id=\"", this.getId(),"\" type=\"", this._inputType, "\"",  
				this.getChangeHandlerHTML(), this.getFocusHandlerHTML(), checked);
	var groupname = this.getInheritedProperty("groupname");
	if(groupname) {
			html.append(" name='",groupname,"'");
	}
	html.append(">");
}

Radio_Label_XFormItem.prototype.outputLabelCellHTML = function (html,  rowSpan, labelLocation) {
	var labelRef = this.getInheritedProperty("labelRef");
	if (labelRef == null) return;
	var label = this.getInstanceValue(labelRef);
	if (label == null) return;
	if (label == "") label = "&nbsp;";
	var accessKey = this.getInheritedProperty("labelValue");
	if (labelLocation == _INLINE_) {
		var style = this.getLabelCssStyle();
		if (style == null) style = "";
		style = "position:relative;left:10;top:5;text-align:left;background-color:#eeeeee;margin-left:5px;margin-right:5px;" + style;
		html.append( "<label id=\"", this.getId(),"___labelValue\"", 
								this.getLabelCssString(null, style), " FOR=\"",this.getId(), "\">",
								label,
							"</label>"
					);
	} else {
		html.append( "<td ", this.getLabelCssString(), (rowSpan > 1 ? " rowspan=" + rowSpan : ""), ">",	
		"<label id=\"", this.getId(),"___labelValue\"", " FOR=\"",this.getId(), "\">",
		label,"</label>");
		html.append("</td>");
	}

}

/**
 * @class defines XFormItem type _BUTTON_
 * this item is a simple HTML &lt;button> element
 * @constructor
 * 
 * @private
 */
Button_XFormItem = function() {}
XFormItemFactory.createItemType("_BUTTON_", "button", Button_XFormItem, XFormItem);
XFormItemFactory.registerItemType("_TRIGGER_", "trigger", Button_XFormItem);
//	type defaults
Button_XFormItem.prototype.forceUpdate = false;
Button_XFormItem.prototype.elementChangeHandler = "onclick";
Button_XFormItem.prototype.labelLocation = _NONE_;
Button_XFormItem.prototype.relevantBehavior = _DISABLE_;
Button_XFormItem.prototype.cssClass = "xform_button";
Button_XFormItem.prototype.focusable = true;
// 	methods
Button_XFormItem.prototype.outputHTML = function (html,  currentCol) {
	// write the div to hold the value (will be filled in on update)
	html.append(
		"<button id=\"", this.getId(), "\"", this.getCssString(),
			"\r  ", this.getOnActivateHandlerHTML(), 
			"\r  ", this.getFocusHandlerHTML(),
		"\r",">", 
			this.getLabel(),
		"</button>");
}

// set up how disabling works for this item type
Button_XFormItem.prototype.setElementEnabled = XFormItem.prototype.setElementDisabledProperty;



/**
 * @class defines XFormItem type _SUBMIT_
 * this item is a simple HTML <input type="submit"> element
 * @constructor
 * 
 * @private
 */
Submit_XFormItem = function() {}
XFormItemFactory.createItemType("_SUBMIT_", "submit", Submit_XFormItem, Button_XFormItem)


//	methods
Submit_XFormItem.prototype.outputHTML = function (html,   currentCol) {
	// write the div to hold the value (will be filled in on update)
	html.append(
		"<input id=\"", this.getId(), "\" type=\"submit\"", this.getCssString(),
			this.getChangeHandlerHTML(), this.getFocusHandlerHTML(),
		" value=\"", this.getLabel(), ">"
	);
}






/**
 * @class defines XFormItem type _ANCHOR_
 * this item is an HTML &lt;a> element
 * @constructor
 * 
 * @private
 */
Anchor_XFormItem = function() {}
XFormItemFactory.createItemType("_ANCHOR_", "anchor", Anchor_XFormItem, XFormItem)

//	type defaults
Anchor_XFormItem.prototype.writeElementDiv = true;
Anchor_XFormItem.prototype.forceUpdate = true;
Anchor_XFormItem.prototype.cssClass = "xform_anchor";
Anchor_XFormItem.prototype.elementChangeHandler = "onclick";
Anchor_XFormItem.prototype.href = "javascript:;";
Anchor_XFormItem.prototype.showInNewWindow = true;
Anchor_XFormItem.prototype.focusable = true;

Anchor_XFormItem.prototype.getHref = function () {
	return this.getInheritedProperty("href");
}

//	type defaults


Anchor_XFormItem.prototype.getAnchorTag = function(href, label) {
	if (href == null) href = this.getHref();
	if (label == null) label = this.getLabel();
	
	var inNewWindow = this.getShowInNewWindow();
	return AjxBuffer.concat(
			'<a href=', href, 
				this.getOnActivateHandlerHTML(), 
				(inNewWindow ? ' target="_blank"' : ''),
			'>',
				label,
			'</a>');
}

//	methods
Anchor_XFormItem.prototype.outputHTML = function (html) {
	html.append(this.getAnchorTag());
}


Anchor_XFormItem.prototype.updateElement = function (value) {
	this.getElement().innerHTML = this.getAnchorTag(value);
}


// set up how disabling works for this item type
Anchor_XFormItem.prototype.setElementEnabled = XFormItem.prototype.setElementEnabledCssClass;




/**
 * @class defines XFormItem type _DATA_ANCHOR_
 * this item is an HTML &lt;a> element
 * @constructor
 * 
 * @private
 */
Data_Anchor_XFormItem = function() {}
XFormItemFactory.createItemType("_DATA_ANCHOR_", "data_anchor", Data_Anchor_XFormItem, Anchor_XFormItem)


Data_Anchor_XFormItem.prototype.updateElement = function (value) {
	this.getElement().innerHTML = this.getAnchorTag(null, value);
}




/**
 * @class defines XFormItem type _URL_
 * @constructor
 * 
 * @private
 */
Url_XFormItem = function() {}
XFormItemFactory.createItemType("_URL_", "url", Url_XFormItem, Anchor_XFormItem)


Url_XFormItem.prototype.updateElement = function (value) {
	this.getElement().innerHTML = this.getAnchorTag(value, value);
}

/**
 * @class defines XFormItem type _DATA_URL_
 * @constructor
 * @private
 */
DataUrl_XFormItem = function() {}
XFormItemFactory.createItemType("_DATA_URL_", "rata_url", DataUrl_XFormItem, Anchor_XFormItem)

Url_XFormItem.prototype.updateElement = function (value) {
	this.getElement().innerHTML = this.getAnchorTag(value, null);
}



/**
 * @class defines XFormItem type _MAILTO_
 * this item is an _ANCHOR_ element with "mailto:" link
 * @constructor
 * 
 * @private
 */
Mailto_XFormItem = function() {}
XFormItemFactory.createItemType("_MAILTO_", "mailto", Mailto_XFormItem, Anchor_XFormItem)
Mailto_XFormItem.prototype.updateElement = function (value) {
	this.getElement().innerHTML = this.getAnchorTag("mailto:"+value, value);
}




/**
 * @class defines XFormItem type _IMAGE_
 * @constructor
 * 
 * @private
 */
Image_XFormItem = function() {}
XFormItemFactory.createItemType("_IMAGE_", "image", Image_XFormItem, XFormItem)


//	type defaults
Image_XFormItem.prototype.forceUpdate = true;
Image_XFormItem.prototype.src = _UNDEFINED_;
Image_XFormItem.prototype.srcPath = _UNDEFINED_;;
Image_XFormItem.prototype.writeElementDiv = true;


//	methods
Image_XFormItem.prototype.updateElement = function (src) {
	if (src == null) src = this.getSrc();
	
	// dereference through the choices array, if provided
	src = this.getChoiceLabel(src);

	// if we didn't get an image name, output nothing (?)
	if (src == null || src == "") {
		var output = "";
	} else {
		// prepend the image path
		var path = this.getSrcPath();
		if (path != null) src = path + src;

		var output = AjxBuffer.concat(
			"<img id=\"", this.getId(), "\" border=0 ", this.getCssString(),
				" src=\"", src, "\"",
			">"
		);
	}
	this.getElement().innerHTML = output;
}


// set up how disabling works for this item type
Image_XFormItem.prototype.setElementEnabled = XFormItem.prototype.setElementEnabledCssClass;



// Ajx_Image
Ajx_Image_XFormItem = function() {}
XFormItemFactory.createItemType("_AJX_IMAGE_", "ajx_image", Ajx_Image_XFormItem, XFormItem);


//	type defaults
Ajx_Image_XFormItem.prototype.forceUpdate = true;
Ajx_Image_XFormItem.prototype.src = _UNDEFINED_;
Ajx_Image_XFormItem.prototype.srcPath = _UNDEFINED_;;
Ajx_Image_XFormItem.prototype.writeElementDiv = false;

// //	methods
Ajx_Image_XFormItem.prototype.updateElement = function (src) {
	if (src == null) src = this.getSrc();

 	// dereference through the choices array, if provided
 	src = this.getChoiceLabel(src);
	var output;
 	// if we didn't get an image name, output nothing (?)
 	if (src == null || src == "") {
 		output = "";
 	} else {
 		// prepend the image path
 		var path = this.getSrcPath();
 		if (path != null) src = path + src;
 		var style = this.getCssStyle();
		output = AjxImg.getImageHtml(src, "position:relative;" + (style ? style : '' ));
 	}
 	if (this.getContainer()) this.getContainer().innerHTML = output;
};


// Dwt_Image
Dwt_Image_XFormItem = function() {}
XFormItemFactory.createItemType("_DWT_IMAGE_", "dwt_image", Dwt_Image_XFormItem, XFormItem);


//	type defaults
Dwt_Image_XFormItem.prototype.forceUpdate = true;
Dwt_Image_XFormItem.prototype.src = _UNDEFINED_;
Dwt_Image_XFormItem.prototype.srcPath = _UNDEFINED_;;
Dwt_Image_XFormItem.prototype.writeElementDiv = false;

// //	methods
Dwt_Image_XFormItem.prototype.updateElement = function (src) {
	if (src == null) src = this.getSrc();

 	// dereference through the choices array, if provided
 	src = this.getChoiceLabel(src);
	var output;
 	// if we didn't get an image name, output nothing (?)
 	if (src == null || src == "") {
 		output = "";
 	} else {
 		// prepend the image path
 		var path = this.getSrcPath();
 		if (path != null) src = path + src;
 		var style = this.getCssStyle();
		style = style || "";
		styleStr = "style='position:relative;'";

		if (src) {
			output = ["<div class='", src, "' ", styleStr, this.getClickHandlerHTML(), " ></div>"].join("");
		} else {
			output = ["<div ", styleStr, this.getClickHandlerHTML(), " ></div>"].join("");
		}
 	}
 	this.getContainer().innerHTML = output;
};

/**
 * @class defines XFormItem type _SELECT1_
 * this item is rendered as HTML &lt;select> element
 * @constructor
 * 
 * @private
 */
Select1_XFormItem = function() {}
XFormItemFactory.createItemType("_SELECT1_", "select1", Select1_XFormItem, XFormItem)

//	type defaults
Select1_XFormItem.prototype.multiple = false;
Select1_XFormItem.prototype.alwaysUpdateChoices = false;
Select1_XFormItem.prototype.focusable = true;
Select1_XFormItem.prototype.cssClass = "xform_select1";
Select1_XFormItem.prototype.containerCssClass = "xform_select_container";
Select1_XFormItem.prototype.visibilityChecks = [XFormItem.prototype.hasReadPermission];
Select1_XFormItem.prototype.enableDisableChecks = [XFormItem.prototype.hasWritePermission];
//	methods
Select1_XFormItem.prototype.initFormItem = function () {
	// if we're dealing with an XFormChoices object...
	var choices = this.getChoices();
	if (choices == null || choices.constructor != XFormChoices) return;

	//	...set up to receive notification when its choices change
	var listener = new AjxListener(this, this.dirtyDisplay);
	choices.addListener(DwtEvent.XFORMS_CHOICES_CHANGED, listener);
}


Select1_XFormItem.prototype.outputHTML = function (html,  currentCol) {
	html.append( 
		"<select id=\"", this.getId(), "\" ", this.getCssString(), 
			(this.getMultiple() ? "multiple " : ""), 
			this.getChangeHandlerHTML(), this.getFocusHandlerHTML(),
		">",
			this.getChoicesHTML(),
		"</select>"
		);
	this.cleanChoiceDisplay();
}

Select1_XFormItem.prototype.getElementValueGetterHTML = function () {
	return "var value = XFormItem.getValueFromHTMLSelect(this);";
}



Select1_XFormItem.prototype.setChoices = function(newChoices) {
	this.choices = newChoices;
	this.dirtyDisplay();
	this.updateChoicesHTML();
}

Select1_XFormItem.prototype.dirtyDisplay = function () {
	XFormItem.prototype.dirtyDisplay.call(this);
	this._choiceDisplayIsDirty = true;
	delete this.$normalizedChoices;
}

Select1_XFormItem.prototype.updateElement = function (newValue) {
	if (this.choicesAreDirty()) this.updateChoicesHTML();
	this.updateValueInHTMLSelect1(newValue, this.getElement(), this.getSelectionIsOpen());
}


Select1_XFormItem.prototype.cleanChoiceDisplay = function () {
	this._choiceDisplayIsDirty = false;
}

// set up how disabling works for this item type
Select1_XFormItem.prototype.setElementEnabled = XFormItem.prototype.setElementDisabledProperty;



/**
 * @class defines XFormItem type _SELECT_
 * this item is rendered as HTML &lt;select> element
 * @constructor
 * 
 * @private
 */
Select_XFormItem = function() {}
XFormItemFactory.createItemType("_SELECT_", "select", Select_XFormItem, Select1_XFormItem)

//	type defaults
Select_XFormItem.prototype.multiple = true;
Select_XFormItem.prototype.selection = _OPEN_;
Select_XFormItem.prototype.focusable = true;
Select_XFormItem.prototype.containerCssClass = "xform_select_container";

//	methods

Select_XFormItem.prototype.updateElement = function (newValue) {
	if (this.choicesAreDirty()) this.updateChoicesHTML();
	this.updateValueInHTMLSelect(newValue, this.getElement(), this.getSelectionIsOpen());
}



/**
 * @class defines XFormItem type _SPACER_
 * Use to output an entire row spacer
 * @constructor
 * 
 * @private
 */
Spacer_XFormItem = function() {}
XFormItemFactory.createItemType("_SPACER_", "spacer", Spacer_XFormItem, XFormItem)

//	type defaults
Spacer_XFormItem.prototype.forceUpdate = false;
Spacer_XFormItem.prototype.labelLocation = _NONE_;
Spacer_XFormItem.prototype.width = 1;
Spacer_XFormItem.prototype.height = 10;
Spacer_XFormItem.prototype.cssStyle = "font-size:1px;overflow:hidden;";
Spacer_XFormItem.prototype.colSpan = "*";
Spacer_XFormItem.prototype.focusable = false;

// 	methods
Spacer_XFormItem.prototype.outputHTML = function (html,   currentCol) {
	html.append( "<div id=", this.getId(), this.getCssString(),"></div>");
}

// set up how disabling works for this item type
Spacer_XFormItem.prototype.setElementEnabled = XFormItem.prototype.setElementEnabledCssClass;

/**
 * @class defines XFormItem type _CELL_SPACER_
 * Use to output a single cell of space
 * @constructor
 * 
 * @private
 */
Cell_Spacer_XFormItem = function() {}
XFormItemFactory.createItemType("_CELL_SPACER_", "cell_spacer", Cell_Spacer_XFormItem, Spacer_XFormItem)
XFormItemFactory.registerItemType("_CELLSPACER_", "cell_spacer", Cell_Spacer_XFormItem);
Cell_Spacer_XFormItem.prototype.width = 10;
Cell_Spacer_XFormItem.prototype.height = 10;
Cell_Spacer_XFormItem.prototype.colSpan = 1;
Cell_Spacer_XFormItem.prototype.focusable = false;

/**
 * @class defines XFormItem type _SEPARATOR_
 * @constructor
 * 
 * @private
 */
Separator_XFormItem = function() {}
XFormItemFactory.createItemType("_SEPARATOR_", "separator", Separator_XFormItem, XFormItem)

//	type defaults
Separator_XFormItem.prototype.cssClass = "xform_separator";
Separator_XFormItem.prototype.colSpan = "*";
Separator_XFormItem.prototype.align = _CENTER_;
Separator_XFormItem.prototype.valign = _CENTER_;
Separator_XFormItem.prototype.height = 10;
Separator_XFormItem.prototype.focusable = false;

// methods
Separator_XFormItem.prototype.outputHTML = function (html,  currentCol) {
	var css = (this.getCssClass() || '');
	if (css != '' && css != null) css = " class=\"" + css + "\"";
	
	html.append( 
			"<table width=100% cellspacing=0 cellpadding=0>",
				"<tr><td height=",this.getHeight(),">",
					"<div ", css,"></div>",
			"</td></tr></table>"
	);
}


// set up how disabling works for this item type
Separator_XFormItem.prototype.setElementEnabled = XFormItem.prototype.setElementEnabledCssClass;







/**
 * @class defines XFormItem type _GROUP_
 * @constructor
 * 
 * @private
 */
Group_XFormItem = function() {
	this.tabIdOrder = [];
}
XFormItemFactory.createItemType("_GROUP_", "group", Group_XFormItem, XFormItem)

//	type defaults
Group_XFormItem.prototype.forceUpdate = false;
Group_XFormItem.prototype.numCols = 2;
Group_XFormItem.prototype.useParentTable = false;
Group_XFormItem.prototype.focusable = false;
Group_XFormItem.prototype.cellspacing = 0;
Group_XFormItem.prototype.cellpadding = 0;
Group_XFormItem.prototype.initFormItem = function () {
	XFormItem.prototype.initFormItem.call(this);	
	if(this.getInheritedProperty("isTabGroup")) {
		var form = this.getForm();
		form.tabIdOrder[this.getId()] = this.tabIdOrder;
		form.addTabGroup(this);
	}

}

Group_XFormItem.prototype.outputHTML = function (html,  currentCol) {
	this.getForm().outputItemList(this.getItems(), this, html,   this.getNumCols(), currentCol);
}

Group_XFormItem.prototype.clearError = function() {
	var errLoc = this.getErrorLocation();
	if (errLoc == _PARENT_ || errLoc == _INHERIT_){
		this.getParentItem().clearError();
		return;
	}

	this.getForm().removeErrorItem(this);
	if(this.items) {
		var cnt = this.items.length;
		for(var i = 0; i < cnt; i++) {
			if(this.items[i].getErrorLocation() != _PARENT_ &&  this.items[i].getErrorLocation() != _INHERIT_)
				this.items[i].clearError();
		}
	}
	this.__errorState = XFormItem.ERROR_STATE_VALID;
	this.removeErrorContainer();
};

Group_XFormItem.prototype.setElementEnabled  =  function (enable) {
		
}

Group_XFormItem.prototype.updateVisibility = function () {
	var isVisible = true;
	
	//check if the parent element is visible
	var parentItem = this.getParentItem();
	if(parentItem)
		isVisible=this.getParentItem().getIsVisible();
	
	//run stack of visibility checks until encounter a negative result
	if(isVisible) {
		var myVisibilityChecks = this.getInheritedProperty("visibilityChecks");
		if(myVisibilityChecks && myVisibilityChecks instanceof Array) {
			var cnt = myVisibilityChecks.length;
			for(var i=0;i<cnt;i++) {
				if(myVisibilityChecks[i] != null) {
					if(typeof(myVisibilityChecks[i])=="function") {
						isVisible = myVisibilityChecks[i].call(this);
						if(!isVisible)
							break;
					} else if (myVisibilityChecks[i] instanceof Array) {
						//first element is a func reference, the rest of elements are arguments
						var func = myVisibilityChecks[i].shift();
						isVisible = func.apply(this, myVisibilityChecks[i]);
						myVisibilityChecks[i].unshift(func);
						if(!isVisible)
							break;
					} else if (typeof (myVisibilityChecks[i]) == "string") {
                        //for relevant backward compatibility
                        var instance = this.getInstance();
                        isVisible = eval(myVisibilityChecks[i]) ;
                        if(!isVisible)
							break;
                    }
				}
			}
		}
	}	
	var reRunRefresh = false;	
	if(isVisible) {
		if(this.deferred)
			reRunRefresh=true;
			
		this.show();
	} else
		this.hide();
	
	//update visibility for active child items
	if(isVisible) {
		for(var itemId in this.activeChildren) {
			if(this.activeChildren[itemId]===true) {
				var item = this.getForm().getItemById(itemId);
				if(item && this.getInstance()) {
					var updateMethod = item.getUpdateVisibilityMethod();				
					if(updateMethod) {
						updateMethod.call(item);
					}
				}
			}
		}
	}
	if(reRunRefresh) {
		this.updateEnabledDisabled();
		this.updateElement();
	}	
}



Step_Choices_XFormItem = function() {}
XFormItemFactory.createItemType("_STEPCHOICE_", "stepchoices", Step_Choices_XFormItem, Group_XFormItem);

Step_Choices_XFormItem.prototype.numCols = 1;
Step_Choices_XFormItem.prototype.initFormItem = function() {
	XFormItem.prototype.initFormItem.call(this);

    this.signUpForEvents();
    var label = this.getNormalizedLabels();
    var values = this.getNormalizedValues();
    this.items = [];
    var currentItem;
    for (var i = 0; i < label.length; i++) {
        currentItem = {type:_OUTPUT_, value:label[i], sourceValue: values[i]};
        this.items.push(currentItem);
    }
}

Step_Choices_XFormItem.prototype.updateElement = function (newValue) {
    var items = this.getItems();
    var el;
    for ( var i = 0; i < items.length; i++) {
        el = items[i].getElement();
        if (items[i].getInheritedProperty("sourceValue") == newValue) {
            Dwt.addClass(el, "AdminOutputTabSelect");
            Dwt.delClass(el, "AdminOutputTab");
        } else {
            Dwt.delClass(el, "AdminOutputTabSelect");
            Dwt.addClass(el, "AdminOutputTab");
        }
    }
}


HomeGroup_XFormItem = function() {
    this.expanded = true;
}
XFormItemFactory.createItemType("_HOMEGROUP_", "homegroup", HomeGroup_XFormItem, Group_XFormItem)

//	type defaults
HomeGroup_XFormItem.prototype.headCss = "homeGroupHeader";
HomeGroup_XFormItem.prototype.bodyCss = "homeGroupBody";
HomeGroup_XFormItem.prototype.numCols = 1;
HomeGroup_XFormItem.prototype.width = "90%";
HomeGroup_XFormItem.prototype.cssStyle = "margin-left:5%; margin-top: 10px;";
HomeGroup_XFormItem.prototype.headerLabel = "Home Group";
HomeGroup_XFormItem.prototype.expandedImg =  "ImgNodeExpanded";
HomeGroup_XFormItem.prototype.collapsedImg =  "ImgNodeCollapsed";
HomeGroup_XFormItem.prototype.initializeItems = function () {
    this.items = [];
    this.items[0] = this.getHeaderItems();
    this.items[1] = this.getContentItems();
    var content = this.items[1].items;
    var choices = this.getInheritedProperty("contentChoices");
    if (!choices[0].label)
        this.items[1].numCols = 1;
    for (var i = 0; i < choices.length; i ++) {
        var currentItem = {type:_OUTPUT_, label: choices[i].label,
                        value: choices[i].value, containerCssStyle:"color:blue;cursor:pointer"};
        if (choices[i].onClick) {
            currentItem.onClick = choices[i].onClick;
        }
        content.push(currentItem);
    }
    Group_XFormItem.prototype.initializeItems.call(this);
}

HomeGroup_XFormItem.prototype.onClick = function(ev) {
    var homeItem = this.getParentItem().getParentItem();
    var contentContainer = homeItem.items[1];
    if (homeItem.expanded) {
        homeItem.expanded = false;
        this.updateElement(homeItem.collapsedImg);
        contentContainer.hide();
    } else {
        homeItem.expanded = true;
        this.updateElement(homeItem.expandedImg);
        contentContainer.show();
    }
}

HomeGroup_XFormItem.prototype.getHeaderItems =
function () {
    var headerLabel = this.getInheritedProperty("headerLabel");
    var headerCss = this.getInheritedProperty("headCss");
    var headerItems = { type:_COMPOSITE_, numCols:3, width:"100%",
            colSizes:["20px", "100%", "20px"],
            items:[
                {type:_DWT_IMAGE_, value: this.expandedImg, onClick:this.onClick},
                {type:_OUTPUT_, value: headerLabel},
                {type:_AJX_IMAGE_, value: "BorderNone"}
            ],
            cssClass:headerCss
        };
    return headerItems;
}

HomeGroup_XFormItem.prototype.getContentItems =
function () {
    var bodyCss = this.getInheritedProperty("bodyCss");
    var contentItems = { type:_GROUP_, items:[], cssClass:bodyCss
    };
    contentItems.items = [];
    return contentItems;
}

CollapsedGroup_XFormItem = function() {
    this.expanded = true;
}
XFormItemFactory.createItemType("_COLLAPSED_GROUP_", "collapsedgroup", CollapsedGroup_XFormItem, Group_XFormItem)

//	type defaults
CollapsedGroup_XFormItem.prototype.headCss = "gridGroupHeader";
CollapsedGroup_XFormItem.prototype.gridLabelCss = "gridGroupBodyLabel";
CollapsedGroup_XFormItem.prototype.colSizes = "100%";
CollapsedGroup_XFormItem.prototype.numCols = 1;
CollapsedGroup_XFormItem.prototype.width = "100%";
CollapsedGroup_XFormItem.prototype.defaultDisplay = true;
CollapsedGroup_XFormItem.prototype.displayGrid = true;
CollapsedGroup_XFormItem.prototype.displayLabelItem = false;
CollapsedGroup_XFormItem.prototype.cssStyle = "margin-top: 10px;";
CollapsedGroup_XFormItem.prototype.headerLabel = "Collapsed Group";
CollapsedGroup_XFormItem.prototype.expandedImg =  "ImgNodeExpanded";
CollapsedGroup_XFormItem.prototype.collapsedImg =  "ImgNodeCollapsed";
CollapsedGroup_XFormItem.prototype.initializeItems = function () {
    var gridLabelCss = this.getInheritedProperty("gridLabelCss");
    var oldItems = this.getItems();
    this.items = [];
    if(this.__attributes.label) {
        this.headerLabel = this.__attributes.label;
    }
    this.items[0] = this.getHeaderItems();
    this.items[1] = this.getContentItems();
    if(!this.items[1] || this.items[1].items.length == 0) {
        if(oldItems) {
            for(var i = 0; i < oldItems.length; i++) {
                oldItems[i].displayGrid = false;
                if(oldItems[i].type == "radio")
                    continue;  // don't deal with _RADIO_
                if(oldItems[i].label || oldItems[i].txtBoxLabel)
                    oldItems[i].labelCssStyle = "text-align:left; background-color:#DEE5F1 !important;padding-left:10px;";
                    //oldItems[i].labelCssClass = gridLabelCss;
            }
            this.items[1].items =  oldItems;
        }
    }

    Group_XFormItem.prototype.initializeItems.call(this);
}

CollapsedGroup_XFormItem.prototype.onClick = function(ev) {
    var headerItem =  this.getParentItem();
    var collapsedItem = headerItem.getParentItem();
    var headerContainer = headerItem.items[2];
    var contentContainer = collapsedItem.items[1];
    var displayLabelItem = collapsedItem.getInheritedProperty("displayLabelItem");
    if (collapsedItem.expanded) {
        collapsedItem.expanded = false;
        this.updateElement(collapsedItem.collapsedImg);
        contentContainer.hide();
        if(displayLabelItem)
            headerContainer.show();
    } else {
        collapsedItem.expanded = true;
        this.updateElement(collapsedItem.expandedImg);
        contentContainer.show();
        headerContainer.hide();
    }
}

CollapsedGroup_XFormItem.prototype.getHeaderItems =
function () {
    var headerLabel = this.getInheritedProperty("headerLabel");
    var headerLabelWidth = this.getInheritedProperty("headerLabelWidth");
    var headerCss = this.getInheritedProperty("headCss");
    var headItems = this.getInheritedProperty("headerItems") || [];
    var headerItems = { type:_COMPOSITE_, numCols:3, width:"100%",
            colSizes:["20px", headerLabelWidth || "100%", "100%"], colSpan:"*", displayGrid:false,
            items:[
                {type:_DWT_IMAGE_, value: this.expandedImg, onClick:this.onClick},
                {type:_OUTPUT_, value: headerLabel},
                {type:_GROUP_, items: headItems}
            ],
            cssClass:headerCss
        };
    return headerItems;
}

CollapsedGroup_XFormItem.prototype.getContentItems =
function () {
    var colsize = this.getInheritedProperty("colSizes");
    var numcols = this.getInheritedProperty("numCols");
    var contentItems = { type:_GROUP_, items:[], colSpan:"*", colSizes:colsize,numCols:numcols, width:"100%"
    };
    var content =  this.getInheritedProperty("contentItems");
    if(content)
        contentItems.items = content;
    return contentItems;
}

CollapsedGroup_XFormItem.prototype.updateVisibility = function () {

    XFormItem.prototype.updateVisibility.call(this);
    var display = this.getInheritedProperty("defaultDisplay");
    var displayLabelItem = this.getInheritedProperty("displayLabelItem");
    if(display) {
        this.items[0].items[2].hide();
        this.items[1].show();
        this.items[0].items[0].value = this.expandedImg;
        this.expanded = true;
    } else {
        if(displayLabelItem)
            this.items[0].items[2].show();
        else this.items[0].items[2].hide();
        this.items[1].hide();
        this.items[0].items[0].__attributes.value = this.collapsedImg;
        this.expanded = false;
    }
}

CollapsedGroup_XFormItem.prototype.getLabel = function () {
    return null;
}


/**
 * @class defines XFormItem type _GROUPER_
 * Draws a simple border around the group, with the label placed over the border
 * @constructor
 * 
 * @private
 */
Grouper_XFormItem = function() {}
XFormItemFactory.createItemType("_GROUPER_", "grouper", Grouper_XFormItem, Group_XFormItem);
Grouper_XFormItem.prototype.labelCssClass = "GrouperLabel";
Grouper_XFormItem.prototype.labelLocation = _INLINE_;		// managed manually by this class
Grouper_XFormItem.prototype.borderCssClass = "GrouperBorder";
Grouper_XFormItem.prototype.insetCssClass = "GrouperInset";

Grouper_XFormItem.prototype.getBorderCssClass = function () {
	return this.getInheritedProperty("borderCssClass");
}

Grouper_XFormItem.prototype.getInsetCssClass = function () {
	return this.getInheritedProperty("insetCssClass");
}

// output the label
Grouper_XFormItem.prototype.outputHTMLStart = function (html,  currentCol) {
	html.append(
			"<div class=", this.getBorderCssClass(), ">",
				"<span ", this.getLabelCssString(),">", this.getLabel(), "</span>",
				"<div class=", this.getInsetCssClass(),">"
		);
}

Grouper_XFormItem.prototype.outputHTMLEnd = function (html,  currentCol) {
	html.append(
			"</div></div>"
		);
}



RadioGrouper_XFormItem = function() {}
XFormItemFactory.createItemType("_RADIO_GROUPER_", "radiogrouper", RadioGrouper_XFormItem, Grouper_XFormItem)
RadioGrouper_XFormItem.prototype.labelCssClass = "xform_radio_grouper_label";
RadioGrouper_XFormItem.prototype.borderCssClass = "xform_radio_grouper_border";
RadioGrouper_XFormItem.prototype.insetCssClass = "xform_radio_grouper_inset";
RadioGrouper_XFormItem.prototype.width = "100%";



CollapsableRadioGrouper_XFormItem = function() {}
XFormItemFactory.createItemType("_COLLAPSABLE_RADIO_GROUPER_", "collapsableradiogrouper", CollapsableRadioGrouper_XFormItem, RadioGrouper_XFormItem)

CollapsableRadioGrouper_XFormItem.prototype.getLabel = function () {
	var label = XFormItem.prototype.getLabel.apply(this);
	return "<nobr><span class=xform_button style='font-size:9px;color:black;'>&nbsp;&ndash;&nbsp;</span>&nbsp;"+label+"</nobr>";
}




/**
 * @class defines XFormItem type _CASE_
 * @constructor
 * 
 * @private
 */
Case_XFormItem = function() {
	Group_XFormItem.call(this);

}
XFormItemFactory.createItemType("_CASE_", "case", Case_XFormItem, Group_XFormItem);

//	type defaults
Case_XFormItem.prototype.labelLocation = _NONE_;
Case_XFormItem.prototype.useParentTable = false;
Case_XFormItem.prototype.width = "100%";
Case_XFormItem.prototype.focusable = false;
Case_XFormItem.prototype.deferred = true;
Case_XFormItem.prototype.cellspacing = 0;
Case_XFormItem.prototype.cellpadding = 0;
Case_XFormItem.prototype.cssClass = "XFormCase";
Case_XFormItem.prototype.isTabGroup = true;	
Case_XFormItem.prototype.caseVarRef = "currentStep";
Case_XFormItem.prototype.visibilityChangeEventSources = [Case_XFormItem.prototype.caseVarRef];
Case_XFormItem.prototype.initFormItem = function () {
	XFormItem.prototype.initFormItem.call(this);	
	if(this.getInheritedProperty("isTabGroup")) {
		var form = this.getForm();
		form.tabIdOrder[this.getId()] = this.tabIdOrder;
		form.addTabGroup(this,"caseKey");
	}

}
Case_XFormItem.prototype.outputHTML = function (html,  currentCol) {
	this.deferred = this.getInheritedProperty("deferred");
	if(this.deferred) {
		this.getForm().outputItemList([], this, html,  this.getNumCols(), 0, true, false);
	} else {
		this.getForm().outputItemList(this.getItems(), this, html,  this.getNumCols(), currentCol);
	}
}

Case_XFormItem.prototype._outputHTML = function () {
	var form = this.getForm();
	
	var element = this.getElement();
	if(!element) {
		return;
	}
	var masterId = this.getId();
	
	if(this.cacheInheritedMethod("getCustomHeight", "$getCustomHeight")) {
		var height = this.cacheInheritedMethod("getCustomHeight", "$getCustomHeight").call(this);

		if(height)
			element.style.height = height;
			
		var width = this.cacheInheritedMethod("getCustomWidth", "$getCustomWidth").call(this);

		if(width)
			element.style.width = width;

		var container = (form.parent instanceof DwtControl) ? form.parent : DwtControl.fromElementId(window._dwtShellId);
		if(container) {
			if(this.cacheInheritedMethod("resizeHdlr", "$resizeHdlr")) {
				container.addControlListener(new AjxListener(this, this.cacheInheritedMethod("resizeHdlr", "$resizeHdlr")));
			}
		}
	}	

    if(this.cacheInheritedMethod("getCustomPaddingStyle", "$getCustomPaddingStyle")) {
        var paddingStyle = this.cacheInheritedMethod("getCustomPaddingStyle", "$getCustomPaddingStyle").call(this);
        if(paddingStyle)
            element.style.cssText += paddingStyle;
    }

	if (AjxEnv.isIE) {
		var tempDiv = this.createElement("temp",null,"div","");
		tempDiv.display = "none";
	}

	var html = new AjxBuffer();
	
	if (this.outputHTMLStart) {
		this.outputHTMLStart(html,  0);
	}
	
	var drawTable = (this.getUseParentTable() == false);
	if (drawTable) {
		var colSizes = this.getColSizes();
		var cellspacing = this.getInheritedProperty("cellspacing");
		var cellpadding = this.getInheritedProperty("cellpadding");		
		html.append("<table cellspacing=",cellspacing," cellpadding=",cellpadding," ",  
				(XForm._showBorder ? "border=1" : "border=0"),
				" id=\"", this.getId(),"_table\" ", this.getTableCssString(),">");
		if (colSizes != null) {
			html.append(" <colgroup>");
			for (var i = 0; i < colSizes.length; i++) {
				var size = colSizes[i];
				if (size < 1) size = size * 100 + "%";
				html.append("<col width=", size, ">");
			}
			html.append("</colgroup>");
		}
		html.append("<tbody>");
	}
	//output HTML for all child elements
	form.outputItemList(this.getItems(), this, html, this.getNumCols(), 0, true, true);
	html.append("</table>");	

	
//	DBG.dumpObj(html.toString());
    element.innerHTML = html.toString();
    this.deferred = false;
}

Case_XFormItem.prototype.hide = function(isBlock) {
	XFormItem.prototype.hide.call(this, isBlock);
	this.hideElement(this.getElement(),isBlock)	;
}

Case_XFormItem.prototype.show = function(isBlock) {
	XFormItem.prototype.show.call(this, isBlock);
	this.showElement(this.getElement(),isBlock)	;
}

Case_XFormItem.prototype.isCurrentTab = function () {
	var isCurrent = false;
	var caseKey = this.getInheritedProperty("caseKey");
	if(!AjxUtil.isEmpty(caseKey)) {
		var caseVarRef = this.getInheritedProperty("caseVarRef");
		var currentKey = this.getInstanceValue(caseVarRef);
		isCurrent = (currentKey == caseKey);
	}
	return isCurrent;
}
Case_XFormItem.prototype.visibilityChecks = [Case_XFormItem.prototype.isCurrentTab];

/**
 * @class defines XFormItem type _TOP_GROUPER_
 * Draws a simple border around the group, with the label placed over the border
 * @constructor
 * 
 * @private
 */
TopGrouper_XFormItem = function() {}
XFormItemFactory.createItemType("_TOP_GROUPER_", "top_grouper", TopGrouper_XFormItem, RadioGrouper_XFormItem)
TopGrouper_XFormItem.prototype.borderCssClass = "TopGrouperBorder";
TopGrouper_XFormItem.prototype.labelCssClass = "GrouperLabel";
TopGrouper_XFormItem.prototype.labelLocation = _INLINE_;		// managed manually by this class
TopGrouper_XFormItem.prototype.insetCssClass = "GrouperInset";

// output the label
TopGrouper_XFormItem.prototype.outputHTMLStart = function (html,   currentCol) {
	html.append(
			"<div class=", this.getBorderCssClass(), ">",
				"<div ", this.getLabelCssString(),">", this.getLabel(), "</div>",
				"<div class=", this.getInsetCssClass(),">"
		);
}

TopGrouper_XFormItem.prototype.outputHTMLEnd = function (html,  currentCol) {
	html.append(
			"</div></div>"
		);
}

if (appNewUI) {
    XFormItemFactory.createItemType("_TOP_GROUPER_", "top_grouper", TopGrouper_XFormItem, CollapsedGroup_XFormItem);
}

BaseTopGrouper_XFormItem = function() {}
XFormItemFactory.createItemType("_BASE_TOP_GROUPER_", "base_top_grouper", BaseTopGrouper_XFormItem, RadioGrouper_XFormItem)
BaseTopGrouper_XFormItem.prototype.borderCssClass = "TopGrouperBorder";
BaseTopGrouper_XFormItem.prototype.labelCssClass = "GrouperLabel";
BaseTopGrouper_XFormItem.prototype.labelLocation = _INLINE_;		// managed manually by this class
BaseTopGrouper_XFormItem.prototype.insetCssClass = "GrouperInset";

// output the label
BaseTopGrouper_XFormItem.prototype.outputHTMLStart = function (html,   currentCol) {
    html.append(
            "<div class=", this.getBorderCssClass(), ">",
                "<div ", this.getLabelCssString(),">", this.getLabel(), "</div>",
                "<div class=", this.getInsetCssClass(),">"
        );
}

BaseTopGrouper_XFormItem.prototype.outputHTMLEnd = function (html,  currentCol) {
    html.append(
            "</div></div>"
        );
    }

/**
 * @class defines XFormItem type _SWITCH_
 * @constructor
 * 
 * @private
 */
Switch_XFormItem = function() {}
XFormItemFactory.createItemType("_SWITCH_", "switch", Switch_XFormItem, Group_XFormItem)

//	type defaults
Switch_XFormItem.prototype.labelLocation = _NONE_;
Switch_XFormItem.prototype.colSpan = "*";
Switch_XFormItem.prototype.width = "100%";
Switch_XFormItem.prototype.numCols = 1;

Switch_XFormItem.prototype.outputHTML = function (html) {
	Switch_XFormItem.outputItemList.call(this.getForm(),this.getItems(), this, html);
}

Switch_XFormItem.prototype.setElementEnabled = function (enable) {};

Switch_XFormItem.outputItemList = function (items, parentItem, html,   numCols, currentCol, skipTable, skipOuter) {
	if (parentItem.outputHTMLStart) {
		parentItem.outputHTMLStart(html,  currentCol);
	}
	var outerStyle = null;
	if(!skipOuter) {
		outerStyle = parentItem.getCssString();
		if (outerStyle != null && outerStyle != "") {
			parentItem.outputElementDivStart(html);
		}
	}
	for (var itemNum = 0; itemNum < items.length; itemNum++) {	
		var item = items[itemNum];
		var isNestingItem = (item.getItems() != null);
		var itemUsesParentTable = (item.getUseParentTable() != false);

		var writeElementDiv = item.getWriteElementDiv();
		var outputMethod = item.getOutputHTMLMethod();
		
		if (isNestingItem && itemUsesParentTable) {
			// actually write out the item
			if (outputMethod) outputMethod.call(item, html,  currentCol);

		} else {

			// begin the element div, if required
			if (writeElementDiv) 	item.outputElementDivStart(html);
			
			// actually write out the item
			if (outputMethod) outputMethod.call(item, html,  0);

	
			// end the element div, if required
			if (writeElementDiv) 	item.outputElementDivEnd(html);
	
		}
		
		if(parentItem)
			parentItem.registerActiveChild(item);
		
		item.signUpForEvents();
		
		var itemUpdateMethod = item.getUpdateElementMethod();
		if(itemUpdateMethod) {
			var itemRefpath = item.getRefPath();
			if(itemRefpath) {
				var instance = this.getInstance();
				if(instance) {
					itemUpdateMethod.call(item, item.getInstanceValue());
				}
			}
		}
	}
	if (outerStyle != null && outerStyle != "") {
		parentItem.outputElementDivEnd(html);
	}


	if (parentItem.outputHTMLEnd) {
		parentItem.outputHTMLEnd(html,  currentCol);
	}		
}

/**
 * @class defines XFormItem type _REPEAT_
 * @constructor
 * 
 * @private
 */
Repeat_XFormItem = function() {
	Group_XFormItem.call(this);
}
XFormItemFactory.createItemType("_REPEAT_", "repeat", Repeat_XFormItem, Group_XFormItem)

//	type defaults
Repeat_XFormItem.prototype.useParentTable = false;
Repeat_XFormItem.prototype.writeElementDiv = true;
Repeat_XFormItem.prototype.numCols = 1;
Repeat_XFormItem.prototype.number = 1;
Repeat_XFormItem.prototype.showRemoveButton = true;
Repeat_XFormItem.prototype.showAddButton = true;
Repeat_XFormItem.prototype.alwaysShowAddButton = false;
Repeat_XFormItem.prototype.showMoveUpButton = false;
Repeat_XFormItem.prototype.showMoveDownButton = false;
Repeat_XFormItem.prototype.bmolsnr = true;
Repeat_XFormItem.prototype.enableDisableChecks = [XFormItem.prototype.hasWritePermission];
Repeat_XFormItem.prototype.visibilityChecks = [XFormItem.prototype.hasReadPermission];

Repeat_XFormItem.haveAnyRows = function () {
	return (this.getParentItem().getInstanceCount() != 0);
}

Repeat_XFormItem.isLastRow = function () {
	return ((this.getParentItem().getInstanceCount()-1) == this.getParentItem().instanceNum);
}

Repeat_XFormItem.isAddButtonVisible = function () {
	return (this.getParentItem().getParentItem().getAlwaysShowAddButton() || Repeat_XFormItem.isLastRow.call(this) || !(Repeat_XFormItem.haveAnyRows.call(this)));
}

Repeat_XFormItem.prototype.getRemoveButton = function () {
	if(!this.removeButton) {
		this.removeButton = {
			type:_BUTTON_, 
			label: AjxMsg.xformRepeatRemove, 
			//width:20,
			cssStyle:"margin-left:20px;",
			onActivate:function (event) {
				var repeatItem = this.getParentItem().getParentItem();
				repeatItem.removeRowButtonClicked(this.getParentItem().instanceNum);
			},
			visibilityChecks:[Repeat_XFormItem.haveAnyRows],
			visibilityChangeEventSources:[this.getRef()]
		};
		var label = this.getInheritedProperty("removeButtonLabel");
		if(label)
			this.removeButton.label = label;
		
		var width = this.getInheritedProperty("removeButtonWidth");		
		if (width)
			this.removeButton.width = width ;		
			
		var cssStyle = this.getInheritedProperty("removeButtonCSSStyle");
		if (cssStyle) 
			this.removeButton.cssStyle = cssStyle ;	
	}
	return this.removeButton;	
}

Repeat_XFormItem.prototype.getAddButton = function () {
	if(!this.addButton) {
		var showAddOnNextRow = this.getInheritedProperty("showAddOnNextRow");
		this.addButton = {
			ref:".",
			type:_BUTTON_, 
			label: AjxMsg.xformRepeatAdd, 
			onActivate:function (event) {
				var repeatItem = this.getParentItem().getParentItem();
				repeatItem.addRowButtonClicked(this.getParentItem().instanceNum);
			},
			visibilityChecks:[Repeat_XFormItem.isAddButtonVisible],
			visibilityChangeEventSources:[this.getRefPath()],
			forceUpdate:true
		};
		var label = this.getInheritedProperty("addButtonLabel");
		if(label)
			this.addButton.label = label;			
		
		var width = this.getInheritedProperty("addButtonWidth");		
		if (width)
			this.addButton.width = width ;

        var cssStyle = this.getInheritedProperty("addButtonCSSStyle");
		if (cssStyle)
			this.addButton.cssStyle = cssStyle ;

		if(showAddOnNextRow) {
			this.addButton.colSpan = "*";
		}
			
	}
	return this.addButton;	
}

Repeat_XFormItem.prototype.moveUpButton = {
	type:_BUTTON_, 
	label:"^", 
	width:20,
	cssStyle:"margin-left:20px;",
	onActivate:function (event) {
		var repeatItem = this.getParentItem().getParentItem();
		repeatItem.moveUpButtonClicked(this.getParentItem().instanceNum);
	}
}
Repeat_XFormItem.prototype.moveDownButton = {
	ref:".",
	type:_BUTTON_, 
	label:"v", 
	width:20,
	onActivate:function (event) {
		var repeatItem = this.getParentItem().getParentItem();
		repeatItem.moveDownButtonClicked(this.getParentItem().instanceNum);
	},
	forceUpdate:true
}

Repeat_XFormItem.groupVisibilityCheck = function () {
	return ( (this.instanceNum < this.getNumberToShow()) || (this.instanceNum < this.getInstanceCount()) || (this.instanceNum==0));	
}

Repeat_XFormItem.prototype.initializeItems = function () {
	var items = this.getItems();

	if (items.length == 1 && items[0].items) {
		var group = items[0];
	} else {
		var group = {	
				ref: this.getRef(), 
				fromRepeat:true, 
//				useParentTable:true,
				type:_GROUP_, 
				numCols: items.length,
				items:[].concat(items),
				visibilityChangeEventSources:[this.getRefPath()],
				visibilityChecks:[function() {
					return (this.instanceNum==0 || (this.instanceNum < this.getNumberToShow()) || (this.instanceNum < this.getInstanceCount()));
				}]
			};
	}
	
	group.colSpan = 1;

	//Check if we have an explicit condition defined for Remove button
	
	// add the add and remove buttons to the original items array, if appropriate
	if (this.getShowRemoveButton()) {
		var button = this.getRemoveButton();
		group.items[group.items.length] = button;
		group.numCols++;			
	}
	if (this.getShowAddButton()) {
		var button = this.getAddButton();
	
		var showAddOnNextRow = this.getInheritedProperty("showAddOnNextRow");
		group.items[group.items.length] = button;
		if(showAddOnNextRow) {
			group.items[group.items.length] = 
			{type:_SPACER_, colSpan:(group.numCols-1), 
				visibilityChecks:[Repeat_XFormItem.isLastRow], 
				visibilityChangeEventSources:[this.getRefPath()]
			};
		} else {
			group.numCols++;
		}
	}
	if (this.getShowMoveUpButton()) {
		group.items[group.items.length] = this.getMoveUpButton();
		group.numCols++;
	}
	if (this.getShowMoveDownButton()) {
		group.items[group.items.length] = this.getMoveDownButton();
		group.numCols++;
	}

	// save off the original items in the group
	this.__originalItems = group;
	// and reset the items array
	this.items = [];
}

Repeat_XFormItem.prototype.makeRepeatInstance = function() {
	// NOTE: We always append the new items to the end, which is OK,
	//			since if a *data value* is inserted in the middle,
	//			each row will show the proper thing when the update script is called
	//
	//  NOTE: XFORMS SPEC REQUIRES REPEAT ITEMS TO START AT 1, this implementation starts at 0!!!
	//
	var originalGroup = this.__originalItems;
	var numCols = this.getNumCols();
	var newItems = [];
	
	for (var i = 0; i < numCols; i++) {
		var instanceNum = this.items.length;
	
		originalGroup.refPath = this.getRefPath() + "[" + instanceNum + "]";
	
		// initialize the originalGroup and its cloned items
		groupItem = this.getForm().initItem(originalGroup, this);
		groupItem.instanceNum = instanceNum;
	
		newItems.push(groupItem);
		this.items.push(groupItem);
	}	
	return newItems;
}


Repeat_XFormItem.prototype.outputHTML = function (html,   currentCol) {
	// output one item to start
	//	all other items will be output dynamically
	this.makeRepeatInstance();
	this.getForm().outputItemList(this.items, this, html, this.getNumCols(), 0);
}


Repeat_XFormItem.prototype.updateElement = function (value) {
	var form = this.getForm();
	
	var element = this.getElement();
	if (value == null || value === "") value = [];
	var itemsToShow = Math.max(value.length, this.getNumberToShow());
	var slotsPresent = this.items.length;

	var masterId = this.getId();
	if (itemsToShow > slotsPresent) {
		var missingElementCount = (itemsToShow - slotsPresent);
		// create some more slots and show them

		var table = element.getElementsByTagName("table")[0];
		var tbody = element.getElementsByTagName("tbody")[0];
	
		var tempDiv;	
		if (AjxEnv.isIE) {
			tempDiv = this.createElement("temp",null,"div","");
			tempDiv.display = "none";
		}
		while (this.items.length < itemsToShow) {
			var newItems = this.makeRepeatInstance(this);
			var html = new AjxBuffer();
			form.outputItemList(newItems, this, html,  this.getNumCols(), 0, true);
			if (AjxEnv.isIE) {
				tempDiv.innerHTML = "<table>" + html.toString() + "</table>";
				var rows = tempDiv.getElementsByTagName("table")[0].rows;
				for (var r = 0; r < rows.length; r++) {
					tbody.appendChild(rows[r]);
				}
			} else {
				var row = table.insertRow(-1);
				row.innerHTML = html;
			}
			var cnt = newItems.length;
			for(var i = 0; i <cnt; i++) {
				var updateMethod = newItems[i].getUpdateVisibilityMethod();
				if(updateMethod)
					updateMethod.call(newItems[i]);
				
				updateMethod = newItems[i].getUpdateEnabledDisabledtMethod();
				if(updateMethod)
					updateMethod.call(newItems[i]);				
			}
		}
	}
	/*var updateMethod = this.getUpdateVisibilityMethod();
	if(updateMethod)
		updateMethod.call(this);
	updateMethod = this.getUpdateEnabledDisabledtMethod();
	if(updateMethod)
		updateMethod.call(this);	*/
	
	XFormItem.prototype.updateElement.call(this, value);
}

Repeat_XFormItem.prototype.addRowButtonClicked = function (instanceNum) {
	var path = this.getRefPath();
	this.getModel().addRowAfter(this.getInstance(), path, instanceNum);
}

Repeat_XFormItem.prototype.removeRowButtonClicked = function (instanceNum) {
	var form = this.getForm();
	if (this.getOnRemoveMethod() ) {
		this.getOnRemoveMethod().call(this, instanceNum, form)
	} else {
		var path = this.getRefPath();
		this.getModel().removeRow(this.getInstance(), path, instanceNum);
	}
	this.items[instanceNum].clearError();
//	this.getForm().setIsDirty(true,this);
	
	var event = new DwtXFormsEvent(form, this, this.getInstanceValue());
	form.notifyListeners(DwtEvent.XFORMS_VALUE_CHANGED, event);
}

Repeat_XFormItem.prototype.getOnRemoveMethod = function() {
	return this.cacheInheritedMethod("onRemove","$onRemove","index,form");
}


/**
 * @class defines XFormItem type _REPEAT_GRID_
 * @constructor
 * 
 * @private
 */
Repeat_Grid_XFormItem = function() {}
XFormItemFactory.createItemType("_REPEAT_GRID_", "repeat_grid", Repeat_Grid_XFormItem, Repeat_XFormItem)
Repeat_Grid_XFormItem.prototype.showRemoveButton = false;
Repeat_Grid_XFormItem.prototype.showAddButton = false;
Repeat_Grid_XFormItem.numCols = 2;





/**
 * @class defines XFormItem type _COMPOSITE_
 * @constructor
 * 
 * @private
 */
Composite_XFormItem = function() {
	Group_XFormItem.call(this);
}
XFormItemFactory.createItemType("_COMPOSITE_", "composite", Composite_XFormItem, Group_XFormItem)

//	type defaults
Composite_XFormItem.prototype.useParentTable = false;
Composite_XFormItem.prototype.tableCssClass = "xform_composite_table";
Composite_XFormItem.prototype.focusable = false;

Composite_XFormItem.prototype.initializeItems = function () {
	var items = this.getItems();
	if (items == null) return;
	
	// make sure the numCols is defined (default to the number of items in the composite)
	if (this.numCols == null) this.numCols = items.length;
	
	// actually instantiate them as formItems
	this.items = this.getForm().initItemList(items, this);
}

Composite_XFormItem.onFieldChange = function(value, event, form) {
	if (this.getParentItem() && this.getParentItem().getOnChangeMethod()) {
		return this.getParentItem().getOnChangeMethod().call(this, value, event, form);
	} else {
		return this.setInstanceValue(value);
	}
}


SetupGroup_XFormItem = function() {
}
SetupGroup_XFormItem.prototype.width="100%";
XFormItemFactory.createItemType("_SETUPGROUP_", "setupgroup", SetupGroup_XFormItem, Composite_XFormItem)
SetupGroup_XFormItem.prototype.initializeItems = function () {
    var headerLabels = this.getInheritedProperty("headerLabels");
    var contentItems = this.getInheritedProperty("contentItems");
    this.items = [];
    this.width="100%";
    if (headerLabels.length!= 0 && headerLabels.length == contentItems.length) {
        for (var i = 0; i < headerLabels.length; i++)
            this.items.push(this.constructSingleGroup(headerLabels[i], contentItems[i], i ));
    }
    this.numCols = this.items.length;
    if (this.numCols > 1)  {
        var colSize =Math.floor(100/(this.numCols));
        var lastCol = 100 - colSize* (this.numCols - 1);
        var colArr = [];
        for (var i = 0; i < this.numCols - 1; i ++) {
            colArr.push(colSize + "%");
        }
        colArr.push(lastCol + "%");
        this.colSizes = colArr;
    }
    Composite_XFormItem.prototype.initializeItems.call(this);
}

SetupGroup_XFormItem.prototype.constructSingleGroup = function (headerLabel, contentItem, index) {
    var currentGroup = {type:_GROUP_, numCols:2, width: "100%", items:[]};
    var labelMessage = (index  + 1) + "  " + headerLabel;
    var headerItem = {type:_OUTPUT_, colSpan: "*", value: labelMessage, cssStyle: "font-size:22px;padding-left: 5px; color: grey"};
    currentGroup.items.push(headerItem);
    var singleContentItem;
    for (var i = 0; i < contentItem.length; i++) {
        singleContentItem = {type:_OUTPUT_, label: i + 1, value: contentItem[i].value, onClick: contentItem[i].onClick, labelCssStyle:"padding-left:20px; color: grey", containerCssStyle:"color:blue;cursor:pointer"};
        currentGroup.items.push(singleContentItem);
    }
    return currentGroup;
}
//Composite_XFormItem.prototype.getErrorContainer = function () {
//	
//}

/**
 * @class defines XFormItem type _DATE_
 * @constructor
 * 
 * @private
 */
Date_XFormItem = function() {}
XFormItemFactory.createItemType("_DATE_", "date", Date_XFormItem, Composite_XFormItem)

//	type defaults
Date_XFormItem.prototype.DATE_MONTH_CHOICES = [
				{value:1, label:I18nMsg.monthJanMedium},
				{value:2, label:I18nMsg.monthFebMedium},
				{value:3, label:I18nMsg.monthMarMedium},
				{value:4, label:I18nMsg.monthAprMedium},
				{value:5, label:I18nMsg.monthMayMedium},
				{value:6, label:I18nMsg.monthJunMedium},
				{value:7, label:I18nMsg.monthJulMedium},
				{value:8, label:I18nMsg.monthAugMedium},
				{value:9, label:I18nMsg.monthSepMedium},
				{value:10, label:I18nMsg.monthOctMedium},
				{value:11, label:I18nMsg.monthNovMedium},
				{value:12, label:I18nMsg.monthDecMedium}
			];
Date_XFormItem.prototype.DATE_DAY_CHOICES = ["1","2","3","4","5","6","7","8","9","10","11","12",
						  "13","14","15","16","17","18","19","20","21","22",
						  "23","24","25","26","27","28","29","30","31"];
Date_XFormItem.prototype.numCols = 3;
Date_XFormItem.prototype.items = [
	{	type:_SELECT1_, 
		ref:".",
		width:50,
		valign:_MIDDLE_,
		relevantBehavior:_PARENT_,
		choices: Date_XFormItem.prototype.DATE_MONTH_CHOICES,
		labelLocation:_NONE_,
		getDisplayValue:function (newValue) {
			if (!(newValue instanceof Date)) newValue = new Date();
			return "" + (newValue.getMonth() + 1);
		},
		elementChanged:function (monthStr, currentDate, event) {
			if (currentDate == null) currentDate = new Date();	//??? should get values of other field???
		
			var month = parseInt(monthStr);
			if (!isNaN(month)) {
				month -= 1;
				currentDate.setMonth(month);
			}
			this.getForm().itemChanged(this.getParentItem(), currentDate, event);
		}
	},
	{	type:_SELECT1_, 
		ref:".",
		width:50,
		valign:_MIDDLE_,
		relevantBehavior:_PARENT_,
		labelLocation:_NONE_,
		choices: Date_XFormItem.prototype.DATE_DAY_CHOICES,
		getDisplayValue:function (newValue) {
			if (!(newValue instanceof Date)) newValue = new Date();
			return "" + newValue.getDate();
		},
		elementChanged: function (dateStr, currentDate, event) {
			if (currentDate == null) currentDate = new Date();	//??? should get values of other field???
		
			var date = parseInt(dateStr);
			if (!isNaN(date)) {
				currentDate.setDate(date);
			}
			this.getForm().itemChanged(this.getParentItem(), currentDate, event);
		}
	},
	{	type:_TEXTFIELD_, 
		ref:".",
		relevantBehavior:_PARENT_,
		width:45,
		labelLocation:_NONE_,

		getDisplayValue:function (newValue) {
			if (!(newValue instanceof Date)) newValue = new Date();
			return "" + newValue.getFullYear();
		},
		elementChanged: function (yearStr, currentDate, event) {
			if (currentDate == null) currentDate = new Date();	//??? should get values of other field???
		
			var year = parseInt(yearStr);
			if (!isNaN(year)) {
				currentDate.setYear(year);
			}
			this.getForm().itemChanged(this.getParentItem(), currentDate, event);
		}

	}
];



/**
 * @class defines XFormItem type _TIME_
 * @constructor
 * 
 * @private
 */
Time_XFormItem = function() {}
XFormItemFactory.createItemType("_TIME_", "time", Time_XFormItem, Composite_XFormItem)

//	type defaults
Time_XFormItem.prototype.numCols = 3;
Time_XFormItem.prototype.TIME_HOUR_CHOICES = ["1","2","3","4","5", "6","7","8","9","10","11","12"];
Time_XFormItem.prototype.TIME_MINUTE_CHOICES = ["00","05","10","15","20","25", "30","35","40","45","50","55"];
Time_XFormItem.prototype.TIME_AMPM_CHOICES = [I18nMsg.periodAm,I18nMsg.periodPm];


Time_XFormItem.prototype.items = [
	{	
		type:_SELECT1_, 
		ref:".",
		width:50,
		valign:_MIDDLE_,
		choices: Time_XFormItem.prototype.TIME_HOUR_CHOICES,
		labelLocation:_NONE_,
		getDisplayValue:function (newValue) {
			if (!(newValue instanceof Date)) newValue = new Date();
			var hours = "" + (newValue.getHours() % 12);
			if (hours == "0") hours = "12";
			return hours;
		},
		elementChanged:function (hoursStr, currentDate, event) {
			if (currentDate == null) currentDate = new Date();	//??? should get values of other fields???
			if (this.__dummyDate == null) {
				this.__dummyDate = new Date();
			}
			this.__dummyDate.setTime(currentDate.getTime());
			var hours = parseInt(hoursStr);
			if (!isNaN(hours)) {
				if (hours == 12) hours = 0;
				var wasPM = (currentDate.getHours() > 11);
				if (wasPM) hours += 12;
				this.__dummyDate.setHours(hours);
			}
			var parentItem = this.getParentItem();
			var elementChangedMethod = parentItem.getElementChangedMethod();
			if (elementChangedMethod != null) {
				elementChangedMethod.call(this.getParentItem(),this.__dummyDate, currentDate, event);
			} else {
				this.getForm().itemChanged(this.getParentItem(), this.__dummyDate, event);
			}
		}
	},

	{	
		type:_SELECT1_, 
		ref:".",
		width:50,
		valign:_MIDDLE_,
		choices: Time_XFormItem.prototype.TIME_MINUTE_CHOICES,
		labelLocation:_NONE_,
		getDisplayValue:function (newValue) {
			if (!(newValue instanceof Date)) newValue = new Date();
			var minutes = newValue.getMinutes();
			minutes = Math.round(minutes / 5) * 5;
			minutes = (minutes < 10 ? "0" + minutes : "" + minutes);
			return minutes;
		},
		elementChanged:function (minutesStr, currentDate, event) {
			if (currentDate == null) currentDate = new Date();	//??? should get values of other fields???
			if (this.__dummyDate == null) {
				this.__dummyDate = new Date();
			}
			this.__dummyDate.setTime(currentDate.getTime());
		
			var minutes = parseInt(minutesStr);
			if (!isNaN(minutes)) {
				this.__dummyDate.setMinutes(minutes);
			}
			var parentItem = this.getParentItem();
			var elementChangedMethod = parentItem.getElementChangedMethod();
			if (elementChangedMethod!= null) {
				elementChangedMethod.call(this.getParentItem(), this.__dummyDate, currentDate, event);
			} else {
				this.getForm().itemChanged(this.getParentItem(), this.__dummyDate, event);
			}
		}
	},
	
	{	
		type:_SELECT1_, 
		ref:".",
		choices: Time_XFormItem.prototype.TIME_AMPM_CHOICES,
		width:50,
		valign:_MIDDLE_,
		labelLocation:_NONE_,
		getDisplayValue:function (newValue) {
			if (!(newValue instanceof Date)) newValue = new Date();
			var hours = newValue.getHours();
			if (hours > 11) return I18nMsg.periodPm;
			return I18nMsg.periodAm;
		},
		elementChanged:function (ampmStr, currentDate, event) {
			if (currentDate == null) currentDate = new Date();	//??? should get values of other fields???
			if (this.__dummyDate == null) {
				this.__dummyDate = new Date();
			}
			this.__dummyDate.setTime(currentDate.getTime());

			var isPM = (ampmStr == I18nMsg.periodPm);
			var hours = currentDate.getHours() % 12;
			
			this.__dummyDate.setHours(hours + (isPM ? 12 : 0));
			var parentItem = this.getParentItem();
			var elementChangedMethod = parentItem.getElementChangedMethod();
			if (elementChangedMethod!= null) {
				elementChangedMethod.call(this.getParentItem(), this.__dummyDate, currentDate, event);
			} else {
				this.getForm().itemChanged(this.getParentItem(), this.__dummyDate, event);
			}
		}
	}
];




/**
 * @class defines XFormItem type _DATETIME_
 * @constructor
 * 
 * @private
 */
Datetime_XFormItem = function() {}
XFormItemFactory.createItemType("_DATETIME_", "datetime", Datetime_XFormItem, Composite_XFormItem)

Datetime_XFormItem._datetimeFormatToItems = function(format, dateItem, timeItem) {
	var items = [];
	var pattern = /{(\d+),\s*(date|time)}/;
	var index = 0;
	while ((index = format.search(pattern)) != -1) {
		if (index > 0) {
			var item = { type: _OUTPUT_, value: format.substring(0,index), valign: _CENTER_ };
			items.push(item);
			format = format.substring(index);
		}
		var result = pattern.exec(format);
		items.push(result[2] == "date" ? dateItem : timeItem);
		format = format.substring(result[0].length);
	}
	if (format.length > 0) {
		var item = { type:_OUTPUT_, value: format };
		items.push(item);
	}
	return items;
}

//	type defaults
Datetime_XFormItem.prototype.numCols = 3;
Datetime_XFormItem.prototype.items = Datetime_XFormItem._datetimeFormatToItems(
	AjxMsg.xformDateTimeFormat,
	{type:_DATE_, ref:".", labelLocation:_NONE_},
	{type:_TIME_, ref:".", labelLocation:_NONE_}
);


/**
 * @class defines XFormItem type _WIDGET_ADAPTOR_
 *	An adaptor for using any random (non-DWT) widget in an xform
 *	NOTE: the generic implementation assumes:
 *			1) you'll create a method called "constructWidget()" which will construct the appropriate widget
 *			2) the widget has a function "insertIntoXForm(form, item, element)"
 *				(overide "this.insertWidget" to change)
 *			3) the widget has a function "updateInXForm(form, item, value, element)"
 *				(overide "this.updateWidget" to change)
 *
 * @constructor
 * 
 * @private
 */
WidgetAdaptor_XFormItem = function() {}
XFormItemFactory.createItemType("_WIDGET_ADAPTOR_", "widget_adaptor", WidgetAdaptor_XFormItem, XFormItem)

//	type defaults
WidgetAdaptor_XFormItem.prototype.writeElementDiv = true;
WidgetAdaptor_XFormItem.prototype.focusable = false;
//	methods

// implement the following to actually construct the instance of your widget
WidgetAdaptor_XFormItem.prototype.constructWidget = function () {}


//
//	insertElement must guarantee that each element is only inserted ONCE
//
WidgetAdaptor_XFormItem.prototype.insertElement = function () {
	if (!this.__alreadyInserted) {
		this.__alreadyInserted = true;
		
		// try to construct a widget
		var widget = this.constructWidget();

		// if we didn't get one, there's nothing to do here
		if (widget == null) return;

		// otherwise insert it into the form!
		this.widget = widget;
		this.insertWidget(this.getForm(), this.widget, this.getElement());
	}
}

WidgetAdaptor_XFormItem.prototype.showElement = function (id) {
	this.insertElement();
	XForm.prototype.showElement.call(this, id);
}

WidgetAdaptor_XFormItem.prototype.insertWidget = function (form, widget, element) {
	this.widget.insertIntoXForm(form, this, element);
}

WidgetAdaptor_XFormItem.prototype.updateElement = function(newValue) {
	if (this.__alreadyInserted) 
		this.updateWidget(newValue);
}
WidgetAdaptor_XFormItem.prototype.updateWidget = function (newValue) {
	this.widget.updateInXForm(this.getForm(), this, newValue, this.getElement());
}





/**
 * @class defines XFormItem type _DWT_ADAPTOR_"
 *
 *	An adaptor for using any random DWT widget in an xform
 *
 *	NOTE: the generic implementation assumes:
 *			1) you'll create a method called "constructWidget()" which will construct the appropriate widget
 *			2) you'll adapt "insertWidget(form,  widget, element)" to insert the widget properly
 *			3) you'll adapt "updateWidget(newValue)" to update the value properly
 * @constructor
 * 
 * @private
 */
Dwt_Adaptor_XFormItem = function() {}
XFormItemFactory.createItemType("_DWT_ADAPTOR_", "dwt_adaptor", Dwt_Adaptor_XFormItem, WidgetAdaptor_XFormItem)

//	type defaults
Dwt_Adaptor_XFormItem.prototype.focusable = false;
//	methods

Dwt_Adaptor_XFormItem.prototype.setElementEnabled = function(enabled) {
	WidgetAdaptor_XFormItem.prototype.setElementEnabled.call(this, enabled);
	if (this.widget) {
		this.widget.setEnabled(enabled);
	}
	this._enabled = enabled;
}

// implement the following to actually construct the instance of your widget
Dwt_Adaptor_XFormItem.prototype.constructWidget = function () {}


Dwt_Adaptor_XFormItem.prototype.insertWidget = function (form, widget, element) {
	this.getForm()._reparentDwtObject(widget, element);
}

Dwt_Adaptor_XFormItem.prototype.updateWidget = function (newValue) {}

Dwt_Adaptor_XFormItem.prototype.getDwtSelectItemChoices = function () {
	if (this.__selOption != null) return this.__selOptions;
	
	var selectOptions = null;
	var choices = this.getChoices();
	if (choices != null) {
		var selectOptions = new Array(choices.length);
		for (var i = 0; i < choices.length; i++) {
			var choice = choices[i];
			var choiceValue = (choice instanceof Object ? choice.value : choice);
			var choiceLabel = (choice instanceof Object ? choice.label : choice);
			selectOptions[i] = new DwtSelectOptionData(choiceValue, choiceLabel);
		}
	}
	this.__selOptions = selectOptions;
	return this.__selOptions;
};

Dwt_Adaptor_XFormItem.prototype._addCssStylesToDwtWidget = function () {
	var style = this.getCssStyle();
	if (style != null){
		var styleArr = style.split(";");
		var el = this.widget.getHtmlElement();
		var kp;
		for (var i = 0 ; i < styleArr.length ; ++i ){
			kp = styleArr[i].split(":");
			if (kp.length > 0){
				var key = kp[0];
				if (key != null) {
					key = key.replace(/^(\s)*/,"");
				}
				if (key == "float"){
					key = (AjxEnv.isIE)? "styleFloat": "cssFloat";
				}
				var val = kp[1];
				if (val != null) {
					el.style[key] = val.replace(/^(\s)*/,"");
				}
			}
		}
	}
};

/**
 * @class defines XFormItem type  _DWT_BUTTON_
 * Adapts a DwtButton to work with the XForm
 * @constructor
 * 
 * @private
 */
Dwt_Button_XFormItem = function() {}
XFormItemFactory.createItemType("_DWT_BUTTON_", "dwt_button", Dwt_Button_XFormItem, Dwt_Adaptor_XFormItem)
Dwt_Button_XFormItem.estimateMyWidth = function (label,withIcon,extraMargin) {
    var width;
    if(ZaZimbraAdmin.LOCALE=="ja"||ZaZimbraAdmin.LOCALE=="ko"||ZaZimbraAdmin.LOCALE=="zh_CN"||ZaZimbraAdmin.LOCALE=="zh_HK")
         width = (String(label).length)*XForm.FONT_WIDTH1 + (String(label).length)*XForm.FONT_WIDTH2 + 14;
    else
	     width = (String(label).length/2)*XForm.FONT_WIDTH1 + (String(label).length/2)*XForm.FONT_WIDTH2 + 14;

    if(withIcon)
		width = width + 24;
	
	if(extraMargin>0)
		width = width + extraMargin;	
	return [width,"px"].join("");
}
//	type defaults
Dwt_Button_XFormItem.prototype.labelLocation = DwtLabel.IMAGE_LEFT | DwtLabel.ALIGN_CENTER;
Dwt_Button_XFormItem.prototype.writeElementDiv = false;
Dwt_Button_XFormItem.prototype.autoPadding= true;
//	methods

Dwt_Button_XFormItem.prototype.insertWidget = function (form, widget, element) {
	this.getForm()._reparentDwtObject(widget, this.getContainer());
};

// implement the following to actually construct the instance of your widget
Dwt_Button_XFormItem.prototype.constructWidget = function () {
	var widget = this.widget = new DwtButton(this.getForm(), this.getLabelLocation(), this.getCssClass());
	var height = this.getHeight();
	var width = this.getWidth();
	
	var el = null;
	if (width != null || height != null){
		el = widget.getHtmlElement();
		if (width != null) el.style.width = width;
		if (height != null) el.style.height = height;
	} 
	this._addCssStylesToDwtWidget();
	
	var icon = this.getInheritedProperty("icon");
	if(icon != null) {
		widget.setImage(icon);
	}
	
	var isToolTip = false;	
	var toolTipContent = this.getInheritedProperty("toolTipContent");
	if(toolTipContent != null) {
		widget.setToolTipContent(toolTipContent);
		isToolTip = true;
	}
	
        var labelContent = this.getLabel();
	
	try{
		var size = Dwt.getSize(this.getContainer());
		if(labelContent){
			var totalCharWidth = AjxStringUtil.getWidth(labelContent);
			var textLength;
			if(icon){	
				textLength = size.x - 42; // exclude icons, paddings, margin, borders
			}
			else{
				textLength = size.x - 22; // exclude paddings, margin, borders
			}
			
			if( (textLength > 0) && (totalCharWidth > textLength)){
				if(!isToolTip){
                                	widget.setToolTipContent(labelContent);
                                }

				var totalNumber = labelContent.length;
				var textLength = textLength - AjxStringUtil.getWidth("..."); // three '.'
				var maxNumberOfLetters= Math.floor(textLength * totalNumber / totalCharWidth);
				if(textLength > 0){
					labelContent = labelContent.substring(0, maxNumberOfLetters) + "...";
				}
			}
			 
			el =  widget.getHtmlElement();
            var tableEl = el.firstChild;
            var isAutoPadding = this.getInheritedProperty("autoPadding");
            if(!tableEl.style.width && isAutoPadding){
                 tableEl.style.width = "100%";
            }

		}		
	}catch(ex){
	}

	widget.setText(labelContent);

	var onActivateMethod = this.getOnActivateMethod();
	if (onActivateMethod != null) {
		var ls = new AjxListener(this, onActivateMethod);
		widget.addSelectionListener(ls);
	}

	if (this._enabled !== void 0) {
		//this.widget = widget;
		this.setElementEnabled(this._enabled);
	}
	
	return widget;
}

Dwt_Button_XFormItem.prototype.getWidget =
function (){
	return this.widget ;
}

/**
 * @class defines XFormItem type _DWT_SELECT_
 * Adapts a DwtSelect to work with the XForm
 * @constructor
 * 
 * @private
 */
Dwt_Select_XFormItem = function() {}
XFormItemFactory.createItemType("_DWT_SELECT_", "dwt_select", Dwt_Select_XFormItem, Dwt_Adaptor_XFormItem)

//	type defaults
Dwt_Select_XFormItem.prototype.writeElementDiv = false;
//	methods

Dwt_Select_XFormItem.prototype.insertWidget = function (form, widget, element) {
	this.getForm()._reparentDwtObject(widget, this.getContainer());
}

Dwt_Select_XFormItem.prototype.constructWidget = function () {
	var choices = this.getDwtSelectItemChoices(this.getChoices());

	var widget = this.widget = new DwtSelect(this.getForm(), choices);
	var height = this.getHeight();
	var width = this.getWidth();
	if (width != null || height != null){
		var el = widget.getHtmlElement();
		if (width != null) el.style.width = width;
		if (height != null) el.style.height = height;
	} 
	this._addCssStylesToDwtWidget();

	var onChangeFunc = new Function("event", 
			"var widget = event._args.selectObj;\r"
		  + "value = event._args.newValue; " + this.getExternalChangeHandler()
	);
	var ls = new AjxListener(this.getForm(), onChangeFunc);
	widget.addChangeListener(ls);

	if (this._enabled !== void 0) {
		//this.widget = widget;
		this.setElementEnabled(this._enabled);
	}
	return widget;
}

Dwt_Select_XFormItem.prototype.updateWidget = function (newValue) {
	this.widget.setSelectedValue(newValue);
}

Dwt_Select_XFormItem.prototype.setElementEnabled = function (enable) {
	this._enabled = enable;
	if (this.widget == null) return;
	if (enable) {
		this.widget.enable();
	} else {
		this.widget.disable();
	}
};

/**	
 * @class defines XFormItem type _DWT_COLORPICKER_
 * Adapts a DwtDate to work with the XForm
 * @constructor
 * 
 * @private
 */
Dwt_ColorPicker_XFormItem = function() {}
XFormItemFactory.createItemType("_DWT_COLORPICKER_", "dwt_colorpicker", Dwt_ColorPicker_XFormItem, Dwt_Adaptor_XFormItem)

Dwt_ColorPicker_XFormItem.prototype.cssStyle = "width:80px;";
Dwt_ColorPicker_XFormItem.prototype.nowrap = false;
Dwt_ColorPicker_XFormItem.prototype.labelWrap = true;
Dwt_ColorPicker_XFormItem.prototype.constructWidget = function () {
    var params = {
        parent: this.getForm(),
        allowColorInput: true,
        noFillLabel: ZaMsg.bt_reset
    };
    var widget = new DwtButtonColorPicker (params) ;
	widget.setActionTiming(DwtButton.ACTION_MOUSEDOWN);

    var buttonImage = this.getInheritedProperty("buttonImage") || "FontColor";
    widget.setImage(buttonImage);
	widget.showColorDisplay(true);
	widget.setToolTipContent(ZMsg.xformFontColor);
	if (this.getInstanceValue() != null) {
		widget.setColor(this.getInstanceValue());       
	}
//	widget.addSelectionListener(new AjxListener(this, this._colorOnChange)); //it cause the dwt color picker event handller is not invoked correctly
    widget.__colorPicker.addSelectionListener(new AjxListener(this, this._colorOnChange)) ;
	return widget;
}

Dwt_ColorPicker_XFormItem.prototype.updateWidget = function (newValue) {
	if(!this.widget)
		return;
		
	//if(window.console && window.console.log) console.log ("new color = " + newValue) ;
	if (newValue != null) {
		this.widget.setColor(newValue);
	}else { //ensure the empty color can be set in the UI
        this.widget.setColor("");            
    }
};

Dwt_ColorPicker_XFormItem.prototype._colorOnChange = function (event) {
	var value = event.detail;
    
    var elementChanged = this.getElementChangedMethod();
	if (elementChanged) {
		elementChanged.call(this,value, this.getInstanceValue(), event);
	}
	var onChangeFunc = this.getOnChangeMethod();
	if (onChangeFunc) {
		onChangeFunc.call(this, value, event, this.getForm());	
	}
};

/**	
 * @class defines XFormItem type _DWT_DATE_
 * Adapts a DwtDate to work with the XForm
 * @constructor
 * 
 * @private
 */
Dwt_Date_XFormItem = function() {}
XFormItemFactory.createItemType("_DWT_DATE_", "dwt_date", Dwt_Date_XFormItem, Dwt_Adaptor_XFormItem)


//	type defaults
Dwt_Date_XFormItem.prototype.cssStyle = "width:80px;";


//	methods

Dwt_Date_XFormItem.prototype.constructWidget = function () {
	var firstDayOfWeek = this.getInheritedProperty("firstDayOfWeek");
	var widget = new DwtButton(this.getForm());
	widget.setActionTiming(DwtButton.ACTION_MOUSEDOWN);

	// ONE MENU??
	var menu = this.menu = new DwtMenu(widget, DwtMenu.CALENDAR_PICKER_STYLE, null, null, this.getForm());
	menu.setSize("150");
	menu._table.width = "100%";
	widget.setMenu(menu, true);
	menu.setAssociatedObj(widget);

	// For now, create a new DwtCalendar for each of the buttons, since on
	// IE, I'm having trouble getting the one calendar to work.
	// TODO: Figure out the IE problem.
	//var cal = new DwtCalendar(menu);
	var cal = new DwtCalendar({parent:menu,firstDayOfWeek:(!AjxUtil.isEmpty(firstDayOfWeek) ? firstDayOfWeek : 0)});
	cal._invokingForm = this.getForm();
	cal._invokingFormItemId = this.getId();
	cal.setDate(new Date(), true);
	cal.addSelectionListener(new AjxListener(this, this._calOnChange));
	widget.__cal = cal;
	return widget; 
}

Dwt_Date_XFormItem.prototype.updateWidget = function (newValue) {
	if (newValue == null) newValue = new Date();
	this.widget.setText(this.getButtonLabel(newValue));
	this.widget._date = newValue;
	this.widget.__cal.setDate(newValue,true);
};


Dwt_Date_XFormItem.prototype._calOnChange = function (event) {
	var value = event.detail;
	var cal = event.item;
	var elemChanged = this.getElementChangedMethod();
	elemChanged.call(this,value, this.getInstanceValue(), event);	
};

Dwt_Date_XFormItem.prototype.getButtonLabel = function (newValue) {
	if (newValue == null || !(newValue instanceof Date)) return "";
        var formatter = AjxDateFormat.getDateInstance(AjxDateFormat.NUMBER);
	return formatter.format(newValue) ;//(newValue.getMonth()+1) + "/" + newValue.getDate() + "/" + (newValue.getFullYear());
};


Dwt_Time_XFormItem = function() {
	this.items[0].type = _DWT_SELECT_;
	this.items[0].errorLocation = _INHERIT_;
	this.items[1].type = _DWT_SELECT_;
	this.items[1].errorLocation = _INHERIT_;
	this.items[1].choices = Dwt_Time_XFormItem.TIME_MINUTE_CHOICES;
	this.items[1].getDisplayValue = function (newValue) {
		if (!(newValue instanceof Date)) newValue = new Date();
		var ret = AjxDateUtil._pad(AjxDateUtil.getRoundedMins(newValue, 15));
		return ret;
	};
	this.items[2].type = _DWT_SELECT_;
	this.items[2].errorLocation = _INHERIT_;
}
Dwt_Time_XFormItem.TIME_MINUTE_CHOICES = ["00","15","30","45"];
XFormItemFactory.createItemType("_DWT_TIME_", "dwt_time", Dwt_Time_XFormItem, Time_XFormItem);


/**
 * @class defines XFormItem type _DWT_DATETIME_
 * Composes a _DWT_DATE_ and a (non-DWT) _TIME_ to make a date/time editor, just for kicks.
 * @constructor
 * 
 * @private
 */
Dwt_Datetime_XFormItem = function() {}
XFormItemFactory.createItemType("_DWT_DATETIME_", "dwt_datetime", Dwt_Datetime_XFormItem, Composite_XFormItem)

//	type defaults
Dwt_Datetime_XFormItem.prototype.numCols = 3;
Dwt_Datetime_XFormItem.prototype.useParentTable = false;
Dwt_Datetime_XFormItem.prototype.cssClass =  "xform_dwt_datetime";
Dwt_Datetime_XFormItem.initialize = function(){
   Dwt_Datetime_XFormItem.prototype.items = Datetime_XFormItem._datetimeFormatToItems(
	AjxMsg.xformDateTimeFormat,
	{type:_DWT_DATE_, ref:".", labelLocation:_NONE_, errorLocation:_PARENT_,
	 elementChanged:
	 function (newDate, currentDate, event) {
	 	currentDate = currentDate ? currentDate : new Date();
		newDate.setHours(currentDate.getHours(), currentDate.getMinutes(), currentDate.getSeconds(), 0);
		var elementChangedMethod = this.getParentItem().getElementChangedMethod();
		if(elementChangedMethod)
			elementChangedMethod.call(this.getParentItem(),newDate, currentDate, event);
	 }
	},
	{type:_DWT_TIME_, ref:".", labelLocation:_NONE_, errorLocation:_PARENT_,
	 elementChanged:
	 function (newDate, currentDate, event) {
		currentDate = currentDate ? currentDate : new Date();
		var elementChangedMethod = this.getParentItem().getElementChangedMethod();
		if(elementChangedMethod)
			elementChangedMethod.call(this.getParentItem(),newDate, currentDate, event);
	 }
	}
);
}
Dwt_Datetime_XFormItem.initialize();


/**
 * @class defines XFormItem type _DWT_LIST_
 * @constructor
 * 
 * @private
 */
Dwt_List_XFormItem = function() {}
XFormItemFactory.createItemType("_DWT_LIST_", "dwt_list", Dwt_List_XFormItem, Dwt_Adaptor_XFormItem)

//	type defaults
Dwt_List_XFormItem.prototype.writeElementDiv = false;
Dwt_List_XFormItem.prototype.widgetClass = DwtListView;
Dwt_List_XFormItem.prototype.bmolsnr = true;
Dwt_List_XFormItem.prototype.getOnSelectionMethod = function() {
	return this.cacheInheritedMethod("onSelection","$onSelection","event");
}


Dwt_List_XFormItem.prototype.constructWidget = function () {
	var headerList = this.getInheritedProperty("headerList");
	var listClass = this.getInheritedProperty("widgetClass");
	
	var hideHeader = this.getInheritedProperty("hideHeader");

	var widget = new listClass(this.getForm(), this.getCssClass(), null, ((hideHeader!=undefined && hideHeader==true ) ? null : headerList));
	var emptyText = this.getInheritedProperty("emptyText");
	if(emptyText !=null || emptyText==="")
		widget.emptyText = emptyText;
		
	if(hideHeader != undefined) {
		widget.hideHeader = hideHeader;
		if(hideHeader && headerList) {
			widget._headerList = headerList;
		}
	}	

	var multiselect = this.getInheritedProperty("multiselect");
	if(multiselect != undefined) {
		widget.setMultiSelect(multiselect);
	}
	if(this.cacheInheritedMethod("getCustomHeight", "$getCustomHeight") && this.cacheInheritedMethod("getCustomWidth", "$getCustomWidth")) {	
		var height = this.cacheInheritedMethod("getCustomHeight", "$getCustomHeight").call(this);
		var width = this.cacheInheritedMethod("getCustomWidth", "$getCustomWidth").call(this);
		if(width && height)
			widget.setSize(width, height);		
	} else {			
		//set the width height here.
		var width = this.getWidth();
		var height = this.getHeight();
		
		if(width && height)
			widget.setSize(width, height);
		
		//set the listDiv height
		if (height && height != Dwt.DEFAULT) {
			widget.setListDivHeight (height) ;
		}
	}		
	
	// make sure the user defined listener is called 
	// before our selection listener.
	var selMethod = this.getOnSelectionMethod();
	if (selMethod) {
		widget.addSelectionListener(new AjxListener(this, selMethod));
	} else {
		var localLs = new AjxListener(this, this._handleSelection);
		widget.addSelectionListener(localLs);
	}
	//check if createPopupMenu method is defined
	var createPopupMenumethod = this.cacheInheritedMethod("createPopupMenu","$createPopupMenu","parent");
	if(createPopupMenumethod != null) {
		createPopupMenumethod.call(this, widget);
	}
	var form=this.getForm();
	var container = (form.parent instanceof DwtControl) ? form.parent : DwtControl.fromElementId(window._dwtShellId);
	if(container) {
		if(this.cacheInheritedMethod("resizeHdlr", "$resizeHdlr") && this.cacheInheritedMethod("getCustomHeight", "$getCustomHeight") && this.cacheInheritedMethod("getCustomWidth", "$getCustomWidth")) {
			container.addControlListener(new AjxListener(this, this.cacheInheritedMethod("resizeHdlr", "$resizeHdlr")));
		}
	}

	return widget;
};

Dwt_List_XFormItem.prototype.resizeHdlr = 
function() {
	try {
		var height = this.cacheInheritedMethod("getCustomHeight", "$getCustomHeight").call(this);
		var width = this.cacheInheritedMethod("getCustomWidth", "$getCustomWidth").call(this);		
		this.widget.setSize(width,height);
	} catch (ex) {
		alert(ex);
	}
};


Dwt_List_XFormItem.prototype.getSelection = function () {
	return this.widget.getSelection();
};

Dwt_List_XFormItem.prototype._handleSelection = function (event) {
	var modelItem = this.getModelItem();
	var event = new DwtXModelEvent(this.getInstance(), modelItem, null, null);
	modelItem.notifyListeners(DwtEvent.XFORMS_VALUE_CHANGED, event);
};

Dwt_List_XFormItem.prototype.insertWidget = function (form, widget, element) {
	this.getForm()._reparentDwtObject(widget, this.getContainer());
};

Dwt_List_XFormItem.prototype.updateWidget = function (newValue) {
	if (typeof (newValue) != 'undefined') {
		this.setItems(newValue);
	}
};

//the method used to compare the contents of the list array.
//because object  array  join alwasy return [Object Object]
//we need to compare the property values
//we should return once we find the differences
//Assume that itemArray and existingArr has the same length
Dwt_List_XFormItem.isItemsChanged = function (itemArray, existingArr) {
    var isChanged = false ;
    if ((itemArray._version !=null && existingArr._version !=null && (itemArray._version != existingArr._version ))
			|| (itemArray.length != existingArr.length)) {
        isChanged = true ;
    } else {
        var rows = [] ;
        var existingRows = [] ;
        for (var i=0; i < itemArray.length; i ++) {
            if (itemArray[i] instanceof Object)  {
                for (var p in itemArray[i]) {
                    rows.push (itemArray[i][p]) ;
                }
            } else {
                rows.push(itemArray[i]) ;
            }

            if (existingArr[i] instanceof Object)  {
                for (var p1 in existingArr[i]) {
                    existingRows.push (existingArr[i][p1]) ;
                }
            } else {
                existingRows.push(existingArr[i]) ;
            }

            if (rows.join() != existingRows.join()) {
                isChanged = true;
                break ;
            }else{
                rows = [];
                existingRows = [] ;
            }
        }
    }

    return isChanged ;
}
Dwt_List_XFormItem.prototype.setItems = function (itemArray){
	var list = this.widget.getList();
	var existingArr = new Array();
	var tmpArr = new Array();
	if (list) {
		existingArr = list.getArray();
	} 
	tmpArr = new Array();
	var defaultColumnSort = this.getInheritedProperty("defaultColumnSortable") ;
	if (itemArray && itemArray.length > 0) {	
		//we have to compare the objects, because XForm calls this method every time an item in the list is selected
		if ( Dwt_List_XFormItem.isItemsChanged(itemArray, existingArr)) {
            var preserveSelection = this.getInheritedProperty("preserveSelection");
			var selection = null;
			if(preserveSelection) {
				selection = this.widget.getSelection();
			}		
			var cnt=itemArray.length;
			for(var i = 0; i< cnt; i++) {
				tmpArr.push(itemArray[i]);		
			}
			//add the default sort column
			this.widget.set(AjxVector.fromArray(tmpArr), defaultColumnSort);
			if(itemArray._version != undefined && itemArray._version != null)
				this.widget.getList().getArray()._version = itemArray._version;
				
			if(preserveSelection && selection) {
				this.widget.setSelectedItems(selection);
			}
		}
	}else{
		//display the empty list (no result html)
		this.widget.set(AjxVector.fromArray([]), defaultColumnSort); 
	}
};

Dwt_List_XFormItem.prototype.appendItems = function (itemArray){ 
	this.widget.addItems(itemArray);
};


/**
 * @class defines XFormItem type _BUTTON_GRID_
 * @constructor
 * 
 * @private
 */
Button_Grid_XFormItem = function() {}
XFormItemFactory.createItemType("_BUTTON_GRID_", "button_grid", Button_Grid_XFormItem, WidgetAdaptor_XFormItem)

//	type defaults
Button_Grid_XFormItem.prototype.numCols = 5;
Button_Grid_XFormItem.prototype.cssClass = "xform_button_grid_medium";
Button_Grid_XFormItem.prototype.forceUpdate = true;


//	methods
Button_Grid_XFormItem.prototype.constructWidget = function () {
	var changeHandler = this.getExternalChangeHandler();
	var attributes = {
		numCols:this.getNumCols(),
		choices:choices.getChoiceObject(),
		cssClass:this.getCssClass(),
		onChange:changeHandler,
		addBracketingCells:(this.getAlign() == _CENTER_)
	}
	var multiple = this.getMultiple();
	if (multiple !== null) attributes.multiple = multiple;
	return new ButtonGrid(attributes);
}



/**
 * @class defines XFormItem type _DWT_CHOOSER_
 * @constructor
 * 
 * @private
 */
Dwt_Chooser_XFormItem = function() {}
XFormItemFactory.createItemType("_DWT_CHOOSER_", "chooser", Dwt_Chooser_XFormItem, Dwt_Adaptor_XFormItem);
Dwt_Chooser_XFormItem.prototype.widgetClass = DwtChooser;
Dwt_Chooser_XFormItem.prototype.listSize = 100;
/*
NOTE: this won't work because attributes.ref is accessed before this
method is called in XFormItemFactory#createItem.
Dwt_Chooser_XFormItem.prototype._setAttributes = function(attributes) {
	// allows "targetRef" alias for "ref" attribute
	if (!attributes.ref && attributes.targetRef) {
		attributes.ref = attributes.targetRef;
	}
	XFormItem.prototype._setAttributes.call(this, attributes);
}
*/
Dwt_Chooser_XFormItem.prototype.getSorted = function() {
	return this.getInheritedProperty("sorted");
}
Dwt_Chooser_XFormItem.prototype.getListCssClass = function() {
	return this.getInheritedProperty("listCssClass");
}

Dwt_Chooser_XFormItem.prototype.getTargetListCssClass = function() {
	return this.getInheritedProperty("targetListCssClass");
}

Dwt_Chooser_XFormItem.prototype.getSourceInstanceValue = function() {
	var items = this.getModel().getInstanceValue(this.getInstance(), this.getInheritedProperty("sourceRef"));
	//items must be either array or vector
	if (! items) {
		items = new AjxVector ();
	}else if (typeof items == "string") {
		items = new Array(items);
	}
	return items ;
}

Dwt_Chooser_XFormItem.prototype.getTargetInstanceValue = function() {
	var items = this.getInstanceValue();
	if (! items) {
		items = new AjxVector ();
	}else if (typeof items == "string") {
		items = new Array(items);
	}
	return items ;
}

Dwt_Chooser_XFormItem.prototype._handleStateChange = function(event) {
	var form = this.getForm();
	var id = this.getId();
	var widget = this.getWidget();
	var value = widget.getItems();
	this._skipUpdate = true;
	form.itemChanged(id, value);
	this._skipUpdate = false;
}

Dwt_Chooser_XFormItem.prototype.constructWidget = function() {
	var form = this.getForm();
	var cssClass = this.getCssClass();
	var sourceListCssClass = this.getListCssClass();
	var targetListCssClass = this.getTargetListCssClass();
	var widgetClass = this.getInheritedProperty("widgetClass");
	if (sourceListCssClass && !targetListCssClass) {
		targetListCssClass = sourceListCssClass;
	}
	var listSize = this.getInheritedProperty("listSize");
	var params = {parent: form, 
				className: cssClass, 
				slvClassName: sourceListCssClass,
				tlvClassName: targetListCssClass, 
				layoutStyle: (this.getInheritedProperty("layoutStyle") ? this.getInheritedProperty("layoutStyle") : DwtChooser.HORIZ_STYLE),
				listSize: listSize, 
				sourceEmptyOk: true, 
				allButtons: true,
				listWidth: (this.getInheritedProperty("listWidth") ? this.getInheritedProperty("listWidth") : null),
				listHeight: (this.getInheritedProperty("listHeight") ? this.getInheritedProperty("listHeight") : null),
				tableWidth: (this.getInheritedProperty("tableWidth") ? this.getInheritedProperty("tableWidth") : null),
				labelWidth: (this.getInheritedProperty("labelWidth") ? this.getInheritedProperty("labelWidth") : null),
				splitButtons:this.getInheritedProperty("splitButtons")	
				};
	
	return new widgetClass(params);
}

Dwt_Chooser_XFormItem.prototype.updateWidget = function(newvalue, dedup, compareFunc) {
	if (this._skipUpdate) {
		return;
	}

	if (this._stateChangeListener) {
		this.widget.removeStateChangeListener(this._stateChangeListener);
	}
	else {
		this._stateChangeListener = new AjxListener(this, Dwt_Chooser_XFormItem.prototype._handleStateChange)
	}

	var origSourceItems = this.getSourceInstanceValue();
	var sourceItems;
	
	if(origSourceItems instanceof Array) { 
		var _tmpSrcItems = [];
		var cnt = origSourceItems.length;
		for(var i=0; i<cnt;i++) {
			_tmpSrcItems.push(origSourceItems[i]);
		}
		sourceItems = AjxVector.fromArray(_tmpSrcItems);
	} else {
		sourceItems = origSourceItems.clone();
	}
	
	var targetItems = this.getTargetInstanceValue();
	if(targetItems instanceof Array) targetItems = AjxVector.fromArray(targetItems);	
	if(dedup) {
		var cnt = targetItems.size();
		for(var i=0; i < cnt; i++) {
			if(compareFunc) {
			 	var ix=sourceItems.indexOfLike(targetItems.get(i),compareFunc);
			 	if(ix > -1) {
					sourceItems.removeAt(ix);
			 	}
			} else {
			 	var ix=sourceItems.indexOf(targetItems.get(i));
			 	if(ix > -1) {
					sourceItems.removeAt(ix);
			 	}
			}
		}
	}
	
	var sorted = this.getSorted();
	if (sorted) {
		sourceItems.sort();
		targetItems.sort();
	}

	this.widget.setItems(sourceItems);
	this.widget.setItems(targetItems, DwtChooserListView.TARGET);

	this.widget.addStateChangeListener(this._stateChangeListener);
}

//
// XFormItem class: "alert"
//

Dwt_Alert_XFormItem = function() {}
XFormItemFactory.createItemType("_DWT_ALERT_", "alert", Dwt_Alert_XFormItem, Dwt_Adaptor_XFormItem);

Dwt_Alert_XFormItem.prototype.colSpan = "*";
Dwt_Alert_XFormItem.prototype.labelLocation = _NONE_;

Dwt_Alert_XFormItem.prototype.getStyle = function() {
	return this.getInheritedProperty("style");
}
Dwt_Alert_XFormItem.prototype.getIconVisible = function() {
	return this.getInheritedProperty("iconVisible");
}
Dwt_Alert_XFormItem.prototype.getTitle = function() {
	return this.getInheritedProperty("title");
}
Dwt_Alert_XFormItem.prototype.getContent = function() {
	return this.getInheritedProperty("content");
}
Dwt_Alert_XFormItem.prototype.getAlertCssClass = function() {
	return this.getInheritedProperty("alertCssClass");
}

Dwt_Alert_XFormItem.prototype.constructWidget = function() {
	var style = this.getStyle();
	var iconVisible = this.getIconVisible();
	var title = this.getTitle();
	var content = this.getContent();
	var alertCssClass = this.getAlertCssClass();
	
	var form = this.getForm();
	var alert = new DwtAlert(form, alertCssClass);
	
	alert.setStyle(style);
	alert.setIconVisible(iconVisible);
	alert.setTitle(title);
	alert.setContent(content);
	
	// bug fix wrong IE box model when conculating the width
	if(AjxEnv.isIE){
		try{	
			var htmlElement = alert.getHtmlElement();
                	var size = Dwt.getSize(htmlElement);
		
			var container = this.getContainer();
			var containerSize =  Dwt.getSize(container);
			
			var style = DwtCssStyle.getComputedStyleObject(htmlElement);	
		        var bl = parseInt(style.borderLeftWidth)     || 1;
                        var br = parseInt(style.borderRightWidth)    || 1;
                        var pl = parseInt(style.paddingLeft)         || 5;
                        var pr = parseInt(style.paddingRight)        || 5;
                        var ml = parseInt(style.marginLeft)          || 5;
                        var mr = parseInt(style.marginRight)         || 5;
                        var extraWidth = bl + br + pl + pr + ml + mr;
			
			if(containerSize.x > extraWidth){
				size.x = containerSize.x - extraWidth;
				Dwt.setSize(htmlElement, size.x, size.y);
			}
		}catch(ex){
		}
	}	
	return alert;
}

Dwt_Alert_XFormItem.prototype.updateWidget = function(newvalue) {
	// nothing
	var content = this.getContent();
	if(!content && newvalue) {
		this.getWidget().setContent(newvalue);
	}
}

//
// XFormItem class: "dwt_tab_bar" ("tab_bar")
//

Dwt_TabBar_XFormItem = function() {}
XFormItemFactory.createItemType("_TAB_BAR_", "tab_bar", Dwt_TabBar_XFormItem, Dwt_Adaptor_XFormItem);
Dwt_TabBar_XFormItem.prototype.colSpan = "*";
Dwt_TabBar_XFormItem.prototype.labelLocation = _NONE_;
Dwt_TabBar_XFormItem.prototype.cssStyle = "margin-right: 5px";

// NOTE: Overriding the _TAB_BAR_
//XFormItemFactory.registerItemType(_TAB_BAR_, "tab_bar", Dwt_TabBar_XFormItem);

Dwt_TabBar_XFormItem.prototype._value2tabkey;
Dwt_TabBar_XFormItem.prototype._tabkey2value;

Dwt_TabBar_XFormItem.prototype._stateChangeListener;

Dwt_TabBar_XFormItem.prototype.getChoices = function() {
	return this.getInheritedProperty("choices");
}

Dwt_TabBar_XFormItem.prototype._handleStateChange = function(event) {
	var form = this.getForm();
	var widget = this.getWidget();
	
	var tabKey = widget.getCurrentTab();
	var newvalue = this._tabkey2value[tabKey];
	
	var id = this.getId();
	//release the focus  
	form.releaseFocus() ;
	form.itemChanged(id, newvalue, event, true);
}

Dwt_TabBar_XFormItem.prototype.constructWidget = function() {
	var form = this.getForm();
	var cssClass = this.getCssClass();
	var btnCssClass = this.getInheritedProperty("buttonCssClass");	
	
	var widget = new DwtTabBarFloat(form, cssClass, btnCssClass);
    this._value2tabkey = {};
	this._tabkey2value = {};
	
	var choices = this.getChoices();
	if(choices.constructor == XFormChoices) {
		this.choices = choices;
		var listener = new AjxListener(this, this.dirtyDisplay);
		choices.addListener(DwtEvent.XFORMS_CHOICES_CHANGED, listener);	
		
		var values = this.getNormalizedValues();
		var labels = this.getNormalizedLabels();
		var cnt = values.length;
		for (var i = 0; i < cnt; i++) {
			// NOTE: DwtTabView keeps its own internal keys that are numerical
			this._value2tabkey[values[i]] = i + 1;
			this._tabkey2value[i + 1] = values[i];
			widget.addButton(i+1, labels[i]);
            widget.getButton(i+1).getHtmlElement().style ["paddingRight"] = "2px" ;
		}			
	} else {
		var cnt = choices.length;
		for (var i = 0; i < cnt; i++) {
			var choice = choices[i];
			// NOTE: DwtTabView keeps its own internal keys that are numerical
			this._value2tabkey[choice.value] = i + 1;
			this._tabkey2value[i + 1] = choice.value;
			widget.addButton(i+1, choice.label);
            widget.getButton(i+1).getHtmlElement().style ["paddingRight"] = "2px" ;
		}
	}
	
	return widget;
}

Dwt_TabBar_XFormItem.prototype.updateWidget = function(newvalue) {
	if (this.widget.isUpdating) {
		this.widget.isUpdating = false;
		return;
	}

	if (this._stateChangeListener) {
		this.widget.removeStateChangeListener(this._stateChangeListener);
	}
	else {
		this._stateChangeListener = new AjxListener(this, Dwt_TabBar_XFormItem.prototype._handleStateChange);
	}
	
	var tabKey = this._value2tabkey[newvalue];
	if (tabKey != this.widget.getCurrentTab()) {
		this.widget.openTab(tabKey);
	}

	this.widget.addStateChangeListener(this._stateChangeListener);
}

Dwt_TabBar_XFormItem.prototype.dirtyDisplay = function() {
	this.$normalizedChoices = null; //nuke these since they are out of date at this point
	if(this.choices && this.choices.constructor == XFormChoices) {
		var labels = this.getNormalizedLabels();
		var values = this.getNormalizedValues();
		var cnt = labels.length;
		for(var i=0;i<cnt;i++) {
			var tabKey = this._value2tabkey[values[i]];
			if(tabKey) {
				var btn = this.widget.getButton(tabKey);
				if(btn) {
					btn.setText(labels[i]);
				}
			}
		}
	}
	this._choiceDisplayIsDirty = true;
	delete this.$normalizedChoices;	
}

//
// XFormItem class: "alert"
//

Dwt_ProgressBar_XFormItem = function() {}
XFormItemFactory.createItemType("_DWT_PROGRESS_BAR_", "dwt_progress_bar", Dwt_ProgressBar_XFormItem, Dwt_Adaptor_XFormItem);

Dwt_ProgressBar_XFormItem.prototype.constructWidget = function() {
	var form = this.getForm();
	var widget = new DwtProgressBar(form, null);
	var maxvalue = this.getInheritedProperty("maxValue");
	if(!maxvalue) {
		this.maxValueRef = this.getInheritedProperty("maxValueRef");
		maxvalue = this.getModel().getInstanceValue(this.getInstance(), this.maxValueRef)
	}
	widget.setMaxValue(maxvalue);
	
	var progressCssClass = this.getInheritedProperty("progressCssClass");
	if(progressCssClass) {
		widget.setProgressCssClass(progressCssClass);
	}
	
	var wholeCssClass = this.getInheritedProperty("wholeCssClass");
	if(wholeCssClass) {
		widget.setWholeCssClass(wholeCssClass);
	}	
	return widget;
}

Dwt_ProgressBar_XFormItem.prototype.updateWidget = function(newvalue) {
	// nothing
	if(!newvalue)
		newvalue=0;
	if(this.maxValueRef) {
		maxvalue = this.getModel().getInstanceValue(this.getInstance(), this.maxValueRef)
		this.getWidget().setMaxValue(maxvalue);	
	}
	this.getWidget().setValue(newvalue);
}
