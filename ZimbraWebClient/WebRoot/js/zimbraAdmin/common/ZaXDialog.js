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
	
	this._contextId = contextId || Dwt.getNextId(ZaId.DLG_UNDEF);
	if (this.supportMinimize) {
        this._supportMinimize = true;
    }
	DwtDialog.call(this, {
		parent:parent, 
		className:clsName, 
		title:title, 
		standardButtons:this._standardButtons,
		extraButtons:this._extraButtons,
		id:ZaId.getDialogId(this._contextId)
	});
    if (this._supportMinimize) {
        this.addMiniIcon();
        this.addPopdownListener(new AjxListener(this, this.popdownHookListner));
    }
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

ZaXDialog.TEMPLATE = "admin.Widgets#ZaBaseDialog";
ZaXDialog.prototype.supportMinimize = false;
ZaXDialog.prototype.registerFinishMethod = false;
ZaXDialog.prototype.miniType = 1; // default is working in process
ZaXDialog.prototype.cacheDialog = true;
ZaXDialog.prototype._createHtmlFromTemplate =
function(templateId, data) {
    if (this._supportMinimize) {
        templateId = ZaXDialog.TEMPLATE;
    }
	DwtDialog.prototype._createHtmlFromTemplate.call(this, templateId, data);
    this._minEl =  document.getElementById(data.id+"_minimize");
};

ZaXDialog.prototype.addMiniIcon =
function () {
    if (this._minEl) {
        this._minEl.innerHTML = AjxImg.getImageHtml("CollapseRight");
	    this._minEl.onclick = AjxCallback.simpleClosure(ZaXDialog.prototype.__handleMinClick, this);
    }
}
ZaXDialog.prototype.setTitleWidthForIE =
function () {
    var titleWidth = (this._contentW || "500px" ).replace("px","");
    titleWidth -= (24+5*2);    //24px is width for mini icon, 5*2px for two padding

    if (titleWidth > 0) {
    //TODO: can extract this part as a function named setTitleWidth(), if we need to set FF/Chrome 's width in the future.
        titleWidth = Dwt.__checkPxVal(titleWidth);
        var titleId = this._htmlElId + "_title";
        var titleElement = document.getElementById(titleId);
        if (titleElement) {
            titleElement.style.width = titleWidth;
        }
    }
}

ZaXDialog.prototype.getTaskItem = function() {
    var cacheName = this.getCacheName? this.getCacheName() : "";
    if (!cacheName) {
        cacheName = this._iKeyName ? this._iKeyName : this.toString();
    }

    var title = this.getTitle();
    if (!title) {
        title = this.toString();
    }

    var viewForPopup;
    if (this.miniType == 1) {
        viewForPopup = this.constructor;
    } else {
        viewForPopup = this;
    }
    var taskItem = new ZaTaskItem(viewForPopup, cacheName, title, this.getObject(), this.getBounds(), this.miniType, undefined, this.getFinishBtnCallback(),this.cacheDialog);
    return taskItem;
}

ZaXDialog.prototype.getFinishBtnCallback = function (finishBtnId) {
    if (!this.registerFinishMethod)
        return;

    if (!finishBtnId)
        finishBtnId = DwtWizardDialog.FINISH_BUTTON;

    var button;
    button = this._buttonDesc[finishBtnId];
    if (!button) {
        finishBtnId = DwtDialog.OK_BUTTON;
        button = this._buttonDesc[finishBtnId];
    }

    if (!button)
        return;

    if (!button.callback)
        return;

    return {id: finishBtnId, callback: button.callback};
}
ZaXDialog.prototype.popdownHookListner =
function() {
    if (!this._inMin) {
	    var task = this.getTaskItem();
        ZaZimbraAdmin.getInstance().getTaskController().removeTask(task);
    }
}

ZaXDialog.prototype.__handleMinClick =
function () {
    var task = this.getTaskItem();
    ZaZimbraAdmin.getInstance().getTaskController().addTask(task);
    this._inMin = true;
    this.popdown();
    this._inMin = false;
}

ZaXDialog.prototype.getTitle =
function () {
    return this._title;
}


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

        var kbMgr = this._shell.getKeyboardMgr();
        if (kbMgr.isEnabled()){
                kbMgr.popTabGroup(this._tabGroup);
                kbMgr.grabFocus(this._tabGroup.getFocusMember());
        }

        if(AjxEnv.isIE && this._supportMinimize) {
            //reset title width in IE, when the dialog has the minimize icon
            this.setTitleWidthForIE();
        }

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

ZaXDialog.prototype.setDirty = function () {
	//override
}
/**
*  ZaXDialog
* @private
**/
ZaXDialog.prototype._createContentHtml =
function () {

	this._table = document.createElement("table");
	this._table.border = 0;
    if (this._contentW)
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
    if (this._contentW)
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
