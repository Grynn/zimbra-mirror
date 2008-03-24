/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Zimlets
 * Copyright (C) 2006, 2007 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Yahoo! Public License
 * Version 1.0 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * 
 * ***** END LICENSE BLOCK *****
 */

//////////////////////////////////////////////////////////////
//  Wikipedia Zimlet.                                       //
//  @author Kevin Henrikson                                 //
//////////////////////////////////////////////////////////////

function Com_Zimbra_Wikipedia() {
}

Com_Zimbra_Wikipedia.prototype = new ZmZimletBase();
Com_Zimbra_Wikipedia.prototype.constructor = Com_Zimbra_Wikipedia;

Com_Zimbra_Wikipedia.prototype.init =
function() {
    //Nothing to init.
};

// Called by the Zimbra framework when the panel item was double clicked
Com_Zimbra_Wikipedia.prototype.doubleClicked = function() {
	this.singleClicked();
};

// Called by the Zimbra framework when the panel item was clicked
Com_Zimbra_Wikipedia.prototype.singleClicked = function() {
	var editorProps = [
		{ label 		 : "Search",
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
			title : "Search Wikipedia",
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

Com_Zimbra_Wikipedia.prototype._doSearch =
function() {
	this._dlg_propertyEditor.popdown();
	this._displaySearchResult(this._propertyEditor.getProperties().search);
	this._dlg_propertyEditor.dispose();
	this._dlg_propertyEditor = null;
};

Com_Zimbra_Wikipedia.prototype._displaySearchResult = 
function(search) {
	var props = [ "toolbar=yes,location=yes,status=yes,menubar=yes,scrollbars=yes,resizable=yes,width=800,height=600" ];
	props = props.join(",");
    var url = "http://www.wikipedia.org/search-redirect.php?language=en&go=Go&search=" + AjxStringUtil.urlEncode(search);
	window.open(url, "Wikipedia", props);
};