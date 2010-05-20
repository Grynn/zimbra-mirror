/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Zimlets
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
 * Zimlet to handle integration with a Yahoo! Maps
 * 
 * @author Kevin Henrikson
 */
function com_zimbra_ymaps_HandlerObject() {
}

com_zimbra_ymaps_HandlerObject.prototype = new ZmZimletBase();
com_zimbra_ymaps_HandlerObject.prototype.constructor = com_zimbra_ymaps_HandlerObject;

/**
 * Simplify handler object
 *
 */
var YMapsZimlet = com_zimbra_ymaps_HandlerObject;

/**
 * Y! Maps Webservice URL
 */
YMapsZimlet.URL = "http://api.local.yahoo.com/MapsService/V1/mapImage?appid=ZimbraMail&zoom=4&image_height=245&image_width=345&location=";

/**
 * Map image URI cache.
 */
YMapsZimlet.CACHE = [];

/**
 * Initializes the zimlet.
 * 
 */
YMapsZimlet.prototype.init =
function() {
	if (ZmAssistant && ZmAssistant.register) ZmAssistant.register(new YMapsZimlet_Asst());
};

/**
 * Called by the Zimbra framework when the Ymaps panel item was double clicked.
 */
YMapsZimlet.prototype.doubleClicked =
function() {
	this.singleClicked();
};

/**
 * Called by the Zimbra framework when the Ymaps panel item was clicked.
 * 
 */
YMapsZimlet.prototype.singleClicked =
function() {
	var editorProps = [{
		label	: this.getMessage("YMapsZimlet_dialog_prefs_address"),
		name	: "address",
		type	: "string",
		minLength: 2,
		maxLength: 200
	}];
	if (!this._dlg_propertyEditor) {
		var view = new DwtComposite(this.getShell());
		var pe = this._propertyEditor = new DwtPropertyEditor(view, true);
		pe.initProperties(editorProps);
		var dialog_args = {
			title	: this.getMessage("YMapsZimlet_dialog_prefs_title"),
			view	: view,
			parent	: this.getShell()
		};
		this._dlg_propertyEditor = new ZmDialog(dialog_args);
		var dlg = this._dlg_propertyEditor;
		pe.setFixedLabelWidth();
		pe.setFixedFieldWidth();
		dlg.setButtonListener(DwtDialog.OK_BUTTON,
				new AjxListener(this, function() {
					if (!pe.validateData()) { return; }
					this._getDisplayCustomMap();
				}));
	}
	this._dlg_propertyEditor.popup();
};

/**
 * Displays a custom map.
 * 
 */
YMapsZimlet.prototype._getDisplayCustomMap =
function() {
	this._dlg_propertyEditor.popdown();
	this._displayDialogMap(this._propertyEditor.getProperties().address);
	this._dlg_propertyEditor.dispose();
	this._dlg_propertyEditor = null;
};

/**
 * Display dialog map.
 * 
 */
YMapsZimlet.prototype._displayDialogMap = 
function(address) {
	var view = new DwtComposite(this.getShell());

	var dialog_args = {
		title	: this.getMessage("YMapsZimlet_dialog_map_title"),
		view	: view,
		parent	: this.getShell(),
		standardButtons: [DwtDialog.OK_BUTTON]
	};

	var dlg = new ZmDialog(dialog_args);
	dlg.popup();
	dlg.setButtonListener(DwtDialog.OK_BUTTON,
			new AjxListener(this, function() {
				dlg.popdown();
				dlg.dispose();
			}));

	var div = document.createElement("div");
	view.getHtmlElement().appendChild(div);

	this.toolTipPoppedUp(null, address, null, div);
};

//
// Content Object Methods
//

/**
 * Called when clicked on matched text.
 */
YMapsZimlet.prototype.clicked =
function(spanElem, contentObj, matchContext, canvas) {
	var url = "http://maps.yahoo.com/maps_result?addr=";
	var addr = contentObj.replace("\n"," ").replace("\r"," ");
	canvas = window.open(url+escape(addr));
};

/**
 * Handles tooltip popped-up event.
 * 
 */
YMapsZimlet.prototype.toolTipPoppedUp =
function(spanElement, obj, context, canvas) {
	canvas.innerHTML = [
		'<center><img width="345" height="245" id="',
		ZmZimletBase.encodeId(obj),
		'" src="',
		this.getResource('blank_pixel.gif'),
		'"/></center>'
	].join("");

	if (YMapsZimlet.CACHE[obj+"img"]) {
		YMapsZimlet._displayImage(YMapsZimlet.CACHE[obj+"img"], obj);
	} else {
		var url = ZmZimletBase.PROXY + AjxStringUtil.urlComponentEncode(YMapsZimlet.URL + AjxStringUtil.urlComponentEncode(obj));
		DBG.println(AjxDebug.DBG2, "YMapsZimlet URL: " + url);
		AjxRpc.invoke(null, url, null, new AjxCallback(this, YMapsZimlet._callback, obj), true);
	}
};

//
// Private Methods
//

/**
 * Displays an image.
 * 
 */
YMapsZimlet._displayImage = 
function(img_src, obj) {
	var imgEl = document.getElementById(ZmZimletBase.encodeId(obj));
	imgEl.style.backgroundImage = "url("+img_src+")";

	if (!YMapsZimlet.CACHE[obj+"img"]) {
		YMapsZimlet.CACHE[obj+"img"] = img_src;
	}
};

/**
 * Handles tooltip callback.
 * 
 * @see		toolTipPoppedUp
 */
YMapsZimlet._callback = 
function(obj, result) {
	var r = result.text;
	var url = r.substring(r.indexOf("http://gws"),r.indexOf("</Result>"));
	url = ZmZimletBase.PROXY + AjxStringUtil.urlComponentEncode(url);
	YMapsZimlet._displayImage(url, obj);
};


//////////////////////////////////////////////////////////////////////////
// Zimlet assistant class
// - used by the Assistant dialog to run games via "command-line"
//////////////////////////////////////////////////////////////////////////
function YMapsZimlet_Asst() {
	// XXX: localize later (does NOT belong in ZmMsg.properties)
	ZmAssistant.call(this, "Yahoo Maps", "map", "Map an address using Yahoo Maps");
};

YMapsZimlet_Asst.prototype = new ZmAssistant();
YMapsZimlet_Asst.prototype.constructor = YMapsZimlet_Asst;

YMapsZimlet_Asst.prototype.okHandler =
function(dialog) {
	// get reference to the ymaps zimlet
	var zm = appCtxt.getZimletMgr();
	var zimlet = zm ? zm._ZIMLETS_BY_ID["YMapsZimlet"] : null;
	if (zimlet && this._address) {
		zimlet.handlerObject.toolTipPoppedUp(null, this._address, null, dialog.getAssistantDiv());
	}
	return false;
};

YMapsZimlet_Asst.prototype.handle =
function(dialog, verb, args) {
	this._address = args;
	var valid = args.length > 0;
	dialog._setOkButton("Map", true, valid);
};
