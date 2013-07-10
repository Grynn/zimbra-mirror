/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2005, 2006, 2007, 2008, 2009, 2010, 2011 VMware, Inc.
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
 * @constructor
 * @class
 * 
 * @param attributes
 * @param {XModel}	model		the model
 * @param {Object}	instance  the data instance
 * @param {DwtComposite}	dwtContainer 	the container
 * 
 * @private
 */
XForm = function(attributes, model, instance, dwtContainer, contextId) {
	if (attributes) {
		for (var prop in attributes) {
			this[prop] = attributes[prop];	
		}
	}

	// get a unique id for this form
	this.assignGlobalId(this, this.id || contextId ||  "_XForm");
	DwtComposite.call(this, dwtContainer, "DWTXForm");
	
	if (this.itemDefaults) {
		XFormItemFactory.initItemDefaults(this, this.itemDefaults);
	}

	// if they didn't pass in a model, make an empty one now
	if (model) {
		this.setModel(model);
	} else {
		this.xmodel = new XModel();
	}
	if (instance) this.setInstance(instance);

	this.__idIndex = {};
	this.__externalIdIndex = {};
	this.__itemsAreInitialized = false;
	this.tabIdOrder = [];
	this.tabGroupIDs = [];
}
XForm.prototype = new DwtComposite;
XForm.prototype.constructor = XForm;
XForm.FONT_WIDTH1 = 7;
XForm.FONT_WIDTH2 = 8;
XForm.toString = function() {	return "[Class XForm]";	}
XForm.prototype.toString = function() {	return "[XForm " + this.__id + "]";	}
XForm.prototype.getId = function () {	return this.__id;	}

/**
* A global handler for setTimeout/clearTimeout. This handler is used by onKeyPress event of all input fields.
**/
XForm.keyPressDelayHdlr = null;


/**
* FORM DEFAULTS
**/
XForm.prototype.numCols = 2;
XForm.prototype.defaultItemType = "output";
XForm.prototype._isDirty = false;
XForm._showBorder = false;		// if true, we write a border around form cells for debugging

//
//	FORM CONSTANTS
//

// generic script constants
var _IGNORE_CACHE_ = "IGNORE_CACHE";
var _UNDEFINED_;

var _UNDEFINED_;
var _ALL_ = "all";

// possible values for "labelDirection", "align" and "valign"
var _NONE_ = "none";
var _LEFT_ = "left";
var _TOP_ = "top";
var _RIGHT_ = "right";
var _BOTTOM_ = "bottom";
var _CENTER_ = "center";
var _MIDDLE_ = "middle";
var _INLINE_ = "inline";


// values for "relevantBehavior"
var _HIDE_ = "hide";
var _BLOCK_HIDE_ = "block_hide";
var _DISABLE_ = "disable";
var _SHOW_DISABLED_ = "show_disabled";
var _PARENT_ = "parent"; // used in error location as well

// values for "errorLocation"
var _SELF_ = "self";
// var _INHERIT_ = "inherit" -- this is defined in XModel.js

// values for "selection"
var _OPEN_ = "open";
var _CLOSED_ = "closed";

// possible values for "overflow"
var _HIDDEN_ = "hidden";
var _SCROLL_ = "scroll";
var _AUTO_ = "auto";
var _VISIBLE_ = "visible";

/**
* update the form with new values
*  NOTE: this will be done automatically if you do a {@link #setInstance}
* This method is costly and should not be called unless the whole form needs to be refreshed.
* When a single or several values are changed on a form - use change events.
**/
XForm.prototype.refresh = function () {
	if(this.__drawn)
		this.updateElementStates();
}


// NOTE: THE FOLLOWING CODE SHOULD BE CONVERTED TO DWT 

XForm.prototype.getGlobalRefString = function() {
	return "XFG.cacheGet('" + this.__id + "')";
}

XForm.prototype.assignGlobalId = function (object, prefix) {
	return XFG.assignUniqueId(object, prefix);
}
XForm.prototype.getUniqueId = function (prefix) {
	return XFG.getUniqueId(prefix);
}

XForm.prototype.getElement = function (id) {
	if (id == null) id = this.getId();
	var el = XFG.getEl(id);
	if (el == null) {
		DBG.println(AjxDebug.DBG2, "getElement(",id,"): no element found");
	}
	return el;
}

XForm.prototype.showElement = function (id) {
	if (id == null) id = this.getId();
	return XFG.showEl(id);
}
XForm.prototype.hideElement = function (id,isBlock) {
	if (id == null) id = this.getId();
	return XFG.hideEl(id,isBlock);
}

XForm.prototype.createElement = function (id, parentEl, tagName, contents) {
	if (id == null) id = this.getId();
	return XFG.createEl(id, parentEl, tagName, contents);
}

// NOTE: END DWT CONVERSION NEEDED 

XForm.prototype.focusElement = function (id) {
	var el = this.getElement(id);
	// if this is a div we will have problems.
	if (el != null) {
		var tagName = el.tagName;
		if (tagName != "DIV" && tagName != "TD" && tagName != "TABLE") {
			el.focus();		//MOW: el.select() ????
			this.onFocus(id);
		}
	}
};

//set focus on the first element in the actuve tab group or in the first element in the form
XForm.prototype.focusFirst = function(currentTabId) {
	var tabIdOrder=null;
	if (currentTabId != null ) {
		tabIdOrder = this.tabIdOrder[currentTabId];
	} else {
		for(var a in this.tabIdOrder) {
			if(this.getItemById(a).getIsVisible() && this.getItemById(a).getIsEnabled() && this.tabIdOrder[a] && this.tabIdOrder[a].length > 0) {
				tabIdOrder = this.tabIdOrder[a];
				break;
			}
		}
	}	
	if(tabIdOrder) {
		var cnt = tabIdOrder.length;
		for (var i = 0; i < cnt; i++) {
			var nextItem = this.getItemById(tabIdOrder[i]);
			if(nextItem && nextItem.focusable && nextItem.getIsVisible() && nextItem.getIsEnabled()) {
				this.focusElement(tabIdOrder[i]);
				break;
			}
		}
	}
}

XForm.prototype.addTabGroup = function(item, tabGroupKeyAttr) {
	tabGroupKeyAttr = tabGroupKeyAttr ? tabGroupKeyAttr : "tabGroupKey";
	var tabGroupKey = item.getInheritedProperty(tabGroupKeyAttr) ? item.getInheritedProperty(tabGroupKeyAttr) : item.getId();
	this.tabGroupIDs[tabGroupKey] = item.getId();
}

XForm.prototype.focusNext = function(id, currentTabId) {
	var myId = id ? id : null;
	var tabIdOrder = null ;
	if (currentTabId != null ) {
		tabIdOrder = this.tabIdOrder[currentTabId];
	} else {
		tabIdOrder = this.tabIdOrder ;
	}
	
	if(tabIdOrder && tabIdOrder.length > 0) {
		var cnt = tabIdOrder.length;
		//DBG.println(AjxDebug.DBG1, "TabIdOrder: length = " + tabIdOrder.length + "<br />" + tabIdOrder.toString());
		var found=false;
		if (myId != null) {
			for (var i = 0; i < cnt; i++) {
				if(tabIdOrder[i] == myId) {
					var elIndex = ((i+1) % cnt);
					if(tabIdOrder[elIndex]) {
						var nextEl = this.getItemById(tabIdOrder[elIndex]);
						if(nextEl.focusable && nextEl.getIsVisible() && nextEl.getIsEnabled()) {
							this.focusElement(tabIdOrder[elIndex]);
							found=true;
							break;
						} else {
							myId=tabIdOrder[elIndex];
						}
					} 
				}
			}
		}		
		if(!found) {
			this.focusFirst(currentTabId);
		}
		
	}
};

XForm.prototype.focusPrevious = function(id, currentTabId) {
	var myId = id ? id : null;
	var tabIdOrder = null ;
	if (currentTabId != null ) {
		tabIdOrder = this.tabIdOrder[currentTabId];
	} else {
		tabIdOrder = this.tabIdOrder ;
	}
	
	if(tabIdOrder && tabIdOrder.length > 0) {
		var cnt = tabIdOrder.length-1;
		var found=false;
		if (myId != null) {
			for (var i = cnt; i >= 0; i--) {
				if(tabIdOrder[i] == myId) {
					var elIndex = ((i-1) % cnt);
					if(tabIdOrder[elIndex]) {
						var nextEl = this.getItemById(tabIdOrder[elIndex]);
						if(nextEl.focusable && nextEl.getIsVisible()  && nextEl.getIsEnabled()) {
							this.focusElement(tabIdOrder[elIndex]);
							found=true;
							break;
						} else {
							myId=tabIdOrder[elIndex];
						}
					} 
				}
			}
		}		
		if(!found) {
			this.focusFirst(currentTabId);
		}
		
	}
};

XForm.prototype.getModel = function () {
	return this.xmodel;
}
XForm.prototype.setModel = function (model) {
	this.xmodel = model;
}

XForm.prototype.getInstance = function () {
	return this.instance;
}

XForm.prototype.updateElementStates = function () {
	if(!this.__drawn)
		return;
		
	this.items[0].updateVisibility();
	this.items[0].updateEnabledDisabled();
	this.items[0].updateElement();
}

XForm.prototype.setInstance = function(instance) {
	this.setIsDirty(false);
	this.clearErrors();
	this.instance = instance;
	if(this.__drawn)
		this.updateElementStates();
	else
		this.__updateStatesDelayed = true;
		
	if (this.__drawn) {
		this.notifyListeners(DwtEvent.XFORMS_INSTANCE_CHANGED, new DwtXFormsEvent(this));
	}
}

XForm.prototype.getInstance = function() {
	return this.instance;
}

XForm.prototype.setInstanceValue = function(val, refPath) {
	this.xmodel.setInstanceValue(this.instance,refPath,val);
}

XForm.prototype.getInstanceValue = function(refPath) {
	return this.xmodel.getInstanceValue(this.instance,refPath);
}

XForm.checkInstanceValue = function(refPath,val) {
	return (this.getInstanceValue(refPath) == val);
}

XForm.checkInstanceValueNot = function(refPath,val) {
	return (this.getInstanceValue(refPath) != val);
}

XForm.checkInstanceValueEmty = function(refPath) {
	return AjxUtil.isEmpty(this.getInstanceValue(refPath));
}

XForm.checkInstanceValueNotEmty = function(refPath) {
	return !AjxUtil.isEmpty(this.getInstanceValue(refPath));
}

XForm.prototype.getController = function () {
	return this.controller;
}
XForm.prototype.setController = function(controller) {
	this.controller = controller;
}




XForm.prototype.getIsDirty = function () {
	return this._isDirty;
}
XForm.prototype.setIsDirty = function(dirty, item) {
	this._isDirty = (dirty == true);
	//pass the current dirty XFORM item, so the event object can has the information which item is changed
	if (typeof item == "undefined") item = null ; //to make it compatible with the previous version. 
	this.notifyListeners(DwtEvent.XFORMS_FORM_DIRTY_CHANGE, new DwtXFormsEvent(this, item, this._isDirty));
}






XForm.prototype.initializeItems = function() {
	// tell the model to initialize all its items first
	//	(its smart enough to only do this once)
	this.xmodel.initializeItems();

	if (this.__itemsAreInitialized) return;
	
	// create a group for the outside parameters and initialize that
	// XXX SKIP THIS IF THERE IS ONLY ONE ITEM AND IT IS ALREADY A GROUP, SWITCH OR REPEAT???
	var outerGroup = {
		id:"__outer_group__",
		type:_GROUP_,
		useParentTable:false,

		numCols:this.numCols,
		colSizes:this.colSizes,
		items:this.items,
		tableCssClass:this.tableCssClass,
		tableCssStyle:this.tableCssStyle
	}
	this.items = this.initItemList([outerGroup]);
	
	this.__itemsAreInitialized = true;
}


XForm.prototype.initItemList = function(itemAttrs, parentItem) {
	var items = [];
	for (var i = 0; i < itemAttrs.length; i++) {
		var attr = itemAttrs[i];
		if (attr != null) {
			items.push(this.initItem(attr, parentItem));
		}
	}
	this.__nestedItemCount += itemAttrs.length;		//DEBUG
	return items;
}


XForm.prototype.initItem = function(itemAttr, parentItem) {
	// if we already have a form item, assume it's been initialized already!
	if (itemAttr._isXFormItem) return itemAttr;
	
	// create the XFormItem subclass from the item attributes passed in
	//	(also links to the model)
	var item = XFormItemFactory.createItem(itemAttr, parentItem, this);
	
	// have the item initialize it's sub-items, if necessary (may be recursive)
	item.initializeItems();
	return item;
}


// add an item to our index, so we can find it easily later
XForm.prototype.indexItem = function(item, id) {
	//DBG.println("id: "+id);
	this.__idIndex[id] = item;
	
	// Add the item to an existing array, or
	var exId = item.getExternalId();
	if (exId == null || exId == "") return;

	var arr = this.__externalIdIndex[exId];
	if (arr != null) {
		arr.push(item);
	} else {
		arr = [item];
	}
	this.__externalIdIndex[exId] = arr;
}
// refPath is ignored
// This is probably not useful to an Xforms client --
// use getItemsById instead.
XForm.prototype.getItemById = function(id) {
	if (id._isXFormItem) return id;
	return this.__idIndex[id];
}

// This is a method that can be called by an XForms client, but
// which doesn't have much internal use.
// gets an item by the id or the ref provided in the declaration
// This method returns an array, or null;
XForm.prototype.getItemsById = function (id) {
	return this.__externalIdIndex[id];
};

XForm.prototype.get = function(path) {
	if (path == null) return null;
	if (path._isXFormItem) path = path.getRefPath();
	return this.xmodel.getInstanceValue(this.instance, path);
}


XForm.prototype.isDrawn = function () {
	return (this.__drawn == true);
}

/**
 * EMC 7/12/2005: I didn't want the extra div that DwtControl writes,
 * since the xforms engine already has an outer div that we can use as 
 * container.
 */
XForm.prototype._replaceDwtContainer = function () {
	var myDiv = document.getElementById(this.__id);
	var dwtContainer = this.getHtmlElement();
	if (dwtContainer.parentNode) dwtContainer.parentNode.replaceChild(myDiv, dwtContainer);
	this._htmlElId = this.__id;
};

/**
* actually draw the form in the parentElement
* @param parentElement
* calls outputForm to generate all the form's HTML
**/
XForm.prototype.draw = function (parentElement) {
	this.initializeItems();
	
	// get the HTML output
	var formOutput = this.outputForm();
	
	if (parentElement == null) parentElement = this.getHtmlElement();
	// if a parentElement was passed, stick the HTML in there and call the scripts
	//	if not, you'll have call put HTML somewhere and call the scripts yourself
	if (parentElement) {
		parentElement.innerHTML = formOutput;
	}
	
	// notify any listeners that we're "ready"
	this.notifyListeners(DwtEvent.XFORMS_READY, new DwtXFormsEvent(this));
	
	// remember that we've been drawn
	this.__drawn = true;
	// and we're done!
	
	if(this.__updateStatesDelayed && this.instance) {
		this.updateElementStates();
		this.__updateStatesDelayed = false;
	}	
}


XForm.prototype.getItems = function () {
	return this.items;
}

/**
* Prints out the form HTML
* calls outputItemList
**/
XForm.prototype.outputForm = function () {
	var t0 = new Date().getTime();
	
	var html = new AjxBuffer();			// holds the HTML output
	var items = this.getItems();

	
	html.append('<div id="', this.__id, '"', 'style="height: 100%;"',
				(this.cssClass != null && this.cssClass != '' ? ' class="' + this.cssClass + '"' : ""),
				(this.cssStyle != null && this.cssStyle != '' ? ' style="' + this.cssStyle + ';"' : ""),
				'>'
				);
	
	this._itemsToInsert = {};
	this._itemsToCleanup = [];

	
	DBG.timePt("starting outputItemList");
	// in initializeItems(), we guaranteed that there was a single outer item
	//	and that it is a group that sets certain properties that can be set at
	//	the form level.  Just output that (and it will output all children)

	// output the actual items of the form
	this.outputItemList(items[0].items, items[0], html, this.numCols);
	DBG.timePt("finished outputItemList");
	html.append("</div id=\"", this.__id,"\">");

	// save the HTML in this.__html (for debugging and such)
	this.__HTMLOutput = html.toString();

	//DBG.println("outputForm() took " + (new Date().getTime() - t0) + " msec");

	return this.__HTMLOutput;
}

XForm.prototype.getOutstandingRowSpanCols = function (parentItem) {
	if (parentItem == null) return 0;
	var outstandingRowSpanCols = 0;
	var previousRowSpans = parentItem.__rowSpanItems;
	if (previousRowSpans) {
/*
		for (var i = 0; i < previousRowSpans.length; i++) {
			var previousItem = previousRowSpans[i];
			//DBG.println("outputing ", previousItem.__numDrawnCols," rowSpan columns for ", previousItem);
			outstandingRowSpanCols += previousItem.__numDrawnCols;

			previousItem.__numOutstandingRows -= 1;
			if ( previousItem.__numOutstandingRows == 0) {
				if (previousRowSpans.length == 1) {
					delete parentItem.__rowSpanItems;
				} else {
					parentItem.__rowSpanItems = [].concat(previousRowSpans.slice(0,i), previousRowSpans.slice(i+1));
				}
			}
		}
*/
		for (var i = previousRowSpans.length-1; i >= 0; i--) {
			var previousItem = previousRowSpans[i];
			//DBG.println("outputing ", previousItem.__numDrawnCols," rowSpan columns for ", previousItem);
			previousItem.__numOutstandingRows -= 1;
			if ( previousItem.__numOutstandingRows == 0) {
				if (previousRowSpans.length == 1) {
					delete parentItem.__rowSpanItems;
				} else {
					parentItem.__rowSpanItems.pop();
				}
			} else {
				outstandingRowSpanCols += previousItem.__numDrawnCols;			
			}
		}

	}
	return outstandingRowSpanCols;
}

/**
* This method will iterate through all the items (XFormItem) in the form and call outputMethod on each of them.
* @param items
* @param parentItem
* @param html
* @param numCols
* @param currentCol
* @param skipTable
**/
XForm.prototype.outputItemList = function (items, parentItem, html,  numCols, currentCol, skipTable, skipOuter) {
	if (parentItem.outputHTMLStart) {
		parentItem.outputHTMLStart(html, currentCol);
	}
	var drawTable = (parentItem.getUseParentTable() == false && skipTable != true);
	var outerStyle = null;
	if(!skipOuter) {
		outerStyle = parentItem.getCssString();
		if (outerStyle != null && outerStyle != "") {
			parentItem.outputElementDivStart(html);
		}
	}

	if (drawTable) {
		var colSizes = parentItem.getColSizes();

		//XXX MOW: appending an elementDiv around the container if we need to style it
		var cellspacing = parentItem.getInheritedProperty("cellspacing");
		var cellpadding = parentItem.getInheritedProperty("cellpadding");
        var border = parentItem.getInheritedProperty("border");
        if (border == 0 && XForm._showBorder) {
            border = 1;
        }
		html.append("<table cellspacing=",cellspacing," cellpadding=",cellpadding,
				 "  border=" , border,
				" id=\"", parentItem.getId(),"_table\" ", parentItem.getTableCssString(),">");
		if (colSizes != null) {
			html.append( " <colgroup>");
			for (var i = 0; i < colSizes.length; i++) {
				var size = colSizes[i];
				if(!isNaN(size)) {
					if (size < 1) 
						size = size * 100 + "%";
				}
				html.append( "<col width='", size, "'>");
			}
			html.append( "</colgroup>");
		}
		html.append( "<tbody>");
	}

	numCols = Math.max(1, numCols);
	if (currentCol == null) currentCol = 0;
	//DBG.println("outputItemList: numCols:",numCols, " currentCol:", currentCol);


	for (var itemNum = 0; itemNum < items.length; itemNum++) {
		var item = items[itemNum];
		var isNestingItem = (item.getItems() != null);
		var itemUsesParentTable = (item.getUseParentTable() != false);
		
		item.__numDrawnCols = 0;

		// write the beginning of the update script
		//	(one of the routines below may want to modify it)
		
		var label = item.getLabel();
		var labelLocation = item.getLabelLocation();
		var showLabel = (label != null && (labelLocation == _LEFT_ || labelLocation == _RIGHT_));

		var colSpan = item.getColSpan();
		if (colSpan == "*") colSpan = Math.max(1, (numCols - currentCol));
		var rowSpan = item.getRowSpan();

		var totalItemCols = item.__numDrawnCols = parseInt(colSpan) + (showLabel ? 1 : 0);
		if (rowSpan > 1 && parentItem) {
			if (parentItem.__rowSpanItems == null) parentItem.__rowSpanItems  = [];
			parentItem.__rowSpanItems.push(item);
			item.__numOutstandingRows = rowSpan;
		}
		//DBG.println("rowSpan = " + rowSpan);
		if(currentCol==0)
			html.append( "<tr>");
		
		// write the label to the left if desired
		if (label != null && labelLocation == _LEFT_) {
			//DBG.println("writing label");
			item.outputLabelCellHTML(html, rowSpan, labelLocation);
		}

		var writeElementDiv = item.getWriteElementDiv();
		var outputMethod = item.getOutputHTMLMethod();
		if (isNestingItem && itemUsesParentTable) {
			// actually write out the item
			if (outputMethod) outputMethod.call(item, html, currentCol);

		} else {

			// write the cell that contains the item 
			//	NOTE: this is currently also the container!
			item.outputContainerTDStartHTML(html,  colSpan, rowSpan);
	
			// begin the element div, if required
			if (writeElementDiv) 	item.outputElementDivStart(html);
			
			// actually write out the item
			if (outputMethod) outputMethod.call(item, html, 0);

	
			// end the element div, if required
			if (writeElementDiv) 	item.outputElementDivEnd(html);
			
	
			// end the cell that contains the item
			item.outputContainerTDEndHTML(html);

		}

		currentCol += totalItemCols;

		// write the label to the right, if desired
		if (label != null && labelLocation == _RIGHT_) {
			//DBG.println("writing label");
			item.outputLabelCellHTML(html, rowSpan);
		}
		
		// now end the update script if necessary

		if ( currentCol >= numCols) {
			html.append( "</tr>");
			currentCol = this.getOutstandingRowSpanCols(parentItem);
			//DBG.println("creating new row:  currentCol is now ", currentCol, (currentCol > 0 ? " due to outstanding rowSpans" : ""));
		}

		// if the number of outstanding rows is the same as the number of columns we're to generate
		//	output an empty row for each
		while (currentCol >= numCols) {
			//DBG.println("outputting empty row because outstandingRowSpanCols >= numCols");
			html.append("</tr id='numCols'>");//\r<tr  id='numCols'>");
			currentCol = this.getOutstandingRowSpanCols(parentItem);
		}
		
		if(parentItem)
			parentItem.registerActiveChild(item);
			
		item.signUpForEvents();

	}
	
	
	if (drawTable) {
		html.append("</tbody></table>");
	}

	if (outerStyle != null && outerStyle != "") {
		parentItem.outputElementDivEnd(html);
	}


	if (parentItem.outputHTMLEnd) {
		parentItem.outputHTMLEnd(html, currentCol);
	}

}






//
//	NOTE: properties of individual items moved to XForm_item_properties.js
//


// CHANGE HANDLING


XForm.prototype.onFocus = function(id) {
	this.__focusObject = id;
}

XForm.prototype.onBlur = function(id) {
	this.__focusObject = null;
}

XForm.prototype.subItemChanged = function (id, value, event, quite) {
	//console.log("XForm.prototype.itemChanged start (" + id +","+value+","+event+")");
	var item = this.getItemById(id);
	if (item == null) return alert("Couldn't get item for " + id);	// EXCEPTION
	
	// tell the item that it's display is dirty so it might have to update
	item.dirtyDisplay();

	// validate value
	var modelItem = item.getSubModelItem();
	var errorCorrected = false;
	if (modelItem != null) {
		try {
			value = modelItem.validate(value, this, item, this.getInstance());
			if(item.hasError()) {
				errorCorrected = true;
			}
			item.clearError();
		}
		catch (message) {
			item.setError(message);
			var event = new DwtXFormsEvent(this, item, value);
			this.notifyListeners(DwtEvent.XFORMS_VALUE_ERROR, event);
			return;
		}
	}

	// if there is an onChange handler, call that
	var onChangeMethod = item.cacheInheritedMethod("onSubChange","$onSubChange","value,event,form");

	if (typeof onChangeMethod == "function") {
//		DBG.println("itemChanged(", item.ref, ").onChange = ", onChangeMethod);
		value = onChangeMethod.call(item, value, event, this);
	} else {
		var oldVal = item.getInstanceValue(item.getInheritedProperty("subRef"));
		if(oldVal == value) {
			if(errorCorrected && !quite) 
				this.notifyListeners(DwtEvent.XFORMS_VALUE_CHANGED, event);
				
			return;
		}	
		item.setInstanceValue(value, item.getSubRefPath());
	}
	
	var event = new DwtXFormsEvent(this, item, value);
	if(!quite)
		this.notifyListeners(DwtEvent.XFORMS_VALUE_CHANGED, event);

	this.setIsDirty(true, item);
	//console.log("XForm.prototype.itemChanged end (" + id +","+value+","+event+")");
}

XForm.prototype.itemChanged = function (id, value, event, quite) {
	//console.log("XForm.prototype.itemChanged start (" + id +","+value+","+event+")");
	var item = this.getItemById(id);
	if (item == null) return alert("Couldn't get item for " + id);	// EXCEPTION
	
	// tell the item that it's display is dirty so it might have to update
	item.dirtyDisplay();

	// validate value
	var modelItem = item.getModelItem();
	var errorCorrected = false;
	if (modelItem != null) {
		try {
			value = modelItem.validate(value, this, item, this.getInstance());
			if(item.hasError()) {
				errorCorrected = true;
			}
			item.clearError();
		}
		catch (message) {
			item.setError(message);
			var event = new DwtXFormsEvent(this, item, value);
			this.notifyListeners(DwtEvent.XFORMS_VALUE_ERROR, event);
			return;
		}
	}

	// if there is an onChange handler, call that
	var onChangeMethod = item.getOnChangeMethod();

	if (typeof onChangeMethod == "function") {
//		DBG.println("itemChanged(", item.ref, ").onChange = ", onChangeMethod);
		value = onChangeMethod.call(item, value, event, this);
	} else {
		var oldVal = item.getInstanceValue();
		if(oldVal == value) {
			if(errorCorrected && !quite) 
				this.notifyListeners(DwtEvent.XFORMS_VALUE_CHANGED, event);
				
			return;
		}	
		item.setInstanceValue(value);
	}
	
	var event = new DwtXFormsEvent(this, item, value);
	if(!quite)
		this.notifyListeners(DwtEvent.XFORMS_VALUE_CHANGED, event);

	this.setIsDirty(true, item);
	//console.log("XForm.prototype.itemChanged end (" + id +","+value+","+event+")");
}


XForm.prototype.getItemsInErrorState = function () {
	if (this.__itemsInErrorState == null) {
		this.__itemsInErrorState = new Object();
		this.__itemsInErrorState.size = 0;
	}
	return this.__itemsInErrorState;
};

XForm.prototype.addErrorItem = function ( item ) {
	var errs = this.getItemsInErrorState();
	var oldItem = 	errs[item.getId()];
	errs[item.getId()] = item;
	if (oldItem == null){
		errs.size++;
	}
};

XForm.prototype.removeErrorItem = function ( item ) {
	if (item != null) {
		var errs = this.getItemsInErrorState();
		var id = item.getId();
		var oldItem = errs[id];
		if (oldItem != null) {
			delete errs[id];
			errs.size--;
		}
	}
};

XForm.prototype.hasErrors = function () {
	var errs = this.getItemsInErrorState();
	return (errs != null && errs.size > 0);
};

XForm.prototype.clearErrors = function () {
	var errs = this.getItemsInErrorState();
	if (errs.size > 0) {
		var k;
		for (k in errs) {
			if (k == 'size') continue;
			errs[k].clearError();
			delete errs[k];
		}
		errs.size = 0;
	}
}

XForm.prototype.onCloseForm = function () {
	if (this.__focusObject != null) {
		var item = this.getItemById(this.__focusObject);
		var element = item.getElement();

//alert("onCloseForm() not implemented");
//		this.itemChanged(this.__focusObject, VALUE???)
		if (element && element.blur) {
			element.blur();
		}
		this.__focusObject = null;
	}
}

//Hack: to fix the cursor on input field not shown in FF
//see https://bugzilla.mozilla.org/show_bug.cgi?id=167801#c58
XForm.prototype.releaseFocus = function () {
	if (this.__focusObject != null) {
		var item = this.getItemById(this.__focusObject);
		var element = item.getElement();

		if (element && element.blur) {
			element.blur();
		}
		this.__focusObject = null;
	}
}



/** @private */
XForm.prototype._reparentDwtObject = function (dwtObj, newParent) {
	var dwtE = dwtObj.getHtmlElement();
	if (dwtE.parentNode) dwtE.parentNode.removeChild(dwtE);
	newParent.appendChild(dwtE);
}

XForm.prototype.shouldInsertItem = function (item) {
	return (this._itemsToInsert[item.getId()] != null)
}

XForm.prototype.insertExternalWidget = function (item) {
	DBG.println("insertExternalWidget(): inserting ref=", item.ref,"  type=", item.type, " id=", item.getId());
			 
	var insertMethod = item.getInsertMethod();

	var widget = item.getWidget();
	if (widget && widget.insertIntoXForm instanceof Function) {
		widget.insertIntoXForm(this, item, item.getElement());
		
	} else if (typeof this[insertMethod] == "function") {
		this[insertMethod](item, item.getElement());
		
	} else {
		DBG.println("insertExternalWidget(): don't know how to insert item ", item.ref,"  type=", item.type);
	}

	// take the item out of the list to insert so we don't insert it more than once
	delete this._itemsToInsert[item.getId()];
}
