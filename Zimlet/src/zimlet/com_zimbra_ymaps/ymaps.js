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
 * Matches if the address contains country name at the end(instead of Zip)
 * Currently we only support EU and US and Canada
 */
YMapsZimlet.COUNTRY_NAMES = "(Austria|Belgium|Bulgaria|Croatia|CzechRepublic|Denmark|Finland|France|Germany|Hungary|Ireland|Italy|Netherlands|Norway|Poland|Portugal|Romania|Russia|Spain|Sweden|Switzerland|UK|Canada|USA)";

/**
 * Y! Maps Main RegEx. It simply matches a string that has a number followed by 2-9 words and Zip/PostalCode.
 * We further process the matched string to ignore strings w/ few common english words to ignore edge cases where simple 
 * English sentance happen to match the above pattern.
 * Also matches canadian zip codes which is 2, 3-letters alpha-numeric words  for example: "7L7 T2E"
 * #REGEX DEV NOTES:
 * IE hangs if we regex backtracks(when there condition appears(starts to match) but at the end actually doesnt(fails).
 * To avoid backtracking, use lazy and atomic groups.
 * ?? means lazy(instead of greedy). (?=(2-9words)\1 simulates "Atomic Groups"
 */
YMapsZimlet.REGEX = "\\d+?-??\\d{0,5},??\\s(?=((\\w+\\W+){2,9}))\\1((\\d{5,7}(-\\d{4,5})?)|(([a-zA-Z]{1,2}\\d{1,2}[a-zA-Z]{1,2}\\s?)|(\\d{1,2}[a-zA-Z]{1,2}\\d{1,2}\\s?))+|"+YMapsZimlet.COUNTRY_NAMES+")";


/**
 * Map image URI cache.
 */
YMapsZimlet.CACHE = [];

/**
 * Called by the Zimbra framework when the Ymaps panel item was double clicked.
 */
YMapsZimlet.prototype.doubleClicked =
function() {
	this.singleClicked();
};

/**
 * Called by the framework when generating the span for in-context link.
 *
 */
YMapsZimlet.prototype.match =
function(line, startIndex) {
	this._setRegExps();
	if (this._regexps.length == 0) {
		return;
	}
	if(!this._skipRegex) {
		this._skipRegex = new RegExp(AjxStringUtil.trim(this.getMessage("skipRegex")), "ig");
	}
	var a = this._regexps;
	var ret = null;
	for (var i = 0; i < a.length; ++i) {
		var re = a[i];
		re.lastIndex = startIndex;
		var m = re.exec(line);
		if (m && m[0] != "") {
			if(this._skipRegex.test(m[0])) {
				continue;
			}

			if (!ret || m.index < ret.index) {
				ret = m;
				ret.matchLength = m[0].length;
				return ret;
			}
		}
	}
	return ret;
};

YMapsZimlet.prototype._setRegExps =
function() {
	if(this._regexps) {
		return this._regexps;
	}
	this._regexps = new Array();
	this._regexps.push(new RegExp(YMapsZimlet.REGEX, "ig")); 
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
