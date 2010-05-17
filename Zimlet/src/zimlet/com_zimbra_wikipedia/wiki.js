/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Zimlets
 * Copyright (C) 2006, 2007, 2008, 2009, 2010 Zimbra, Inc.
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
 * Search wikipedia.
 * 
 * @author Kevin Henrikson
 */
function com_zimbra_wikipedia_HandlerObject() {
}

com_zimbra_wikipedia_HandlerObject.prototype = new ZmZimletBase();
com_zimbra_wikipedia_HandlerObject.prototype.constructor = com_zimbra_wikipedia_HandlerObject;

/**
 * Simplify handler object
 *
 */
var WikipediaZimlet = com_zimbra_wikipedia_HandlerObject;

/**
 * Initializes the zimlet.
 */
WikipediaZimlet.prototype.init =
function() {
    //Nothing to init.
};

/**
 * Called by the Zimbra framework when the panel item was double clicked.
 */
WikipediaZimlet.prototype.doubleClicked = function() {
	this.singleClicked();
};

/**
 * Called by the Zimbra framework when the panel item was clicked.
 */
WikipediaZimlet.prototype.singleClicked = function() {
	var editorProps = [
		{ label 		 : this.getMessage("WikipediaZimlet_label_search"),
		  name           : "search",
		  type           : "string",
		  value          : "",
		  minLength      : 4,
		  maxLength      : 100
		}
		];
	if (!this._dlg_propertyEditor) {
		var view = new DwtComposite(this.getShell());
		this._propertyEditor = new DwtPropertyEditor(view, true);
		var pe = this._propertyEditor;
		pe.initProperties(editorProps);
		var dialog_args = {
			title : this.getMessage("WikipediaZimlet_dialog_title"),
			view  : view
		};
		this._dlg_propertyEditor = this._createDialog(dialog_args);
		var dlg = this._dlg_propertyEditor;
		pe.setFixedLabelWidth();
		pe.setFixedFieldWidth();
		dlg.setButtonListener(DwtDialog.OK_BUTTON,
				      new AjxListener(this, function() {
				          if (!pe.validateData()) {return;}
					      this._doSearch();
				      }));
	}
	this._dlg_propertyEditor.popup();
};

/**
 * Perform search.
 */
WikipediaZimlet.prototype._doSearch =
function() {
	this._dlg_propertyEditor.popdown();
	this._displaySearchResult(this._propertyEditor.getProperties().search);
	this._dlg_propertyEditor.dispose();
	this._dlg_propertyEditor = null;
};

/**
 * Display search results.
 */
WikipediaZimlet.prototype._displaySearchResult = 
function(search) {
	var props = [ "toolbar=yes,location=yes,status=yes,menubar=yes,scrollbars=yes,resizable=yes,width=800,height=600" ];
	props = props.join(",");
    var url = "http://www.wikipedia.org/search-redirect.php?language=en&go=Go&search=" + AjxStringUtil.urlEncode(search);
	window.open(url, "Wikipedia", props);
};