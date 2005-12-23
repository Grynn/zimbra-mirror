/*
 * ***** BEGIN LICENSE BLOCK *****
 * Version: ZPL 1.1
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.1 ("License"); you may not use this file except in
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
 * Portions created by Zimbra are Copyright (C) 2005 Zimbra, Inc.
 * All Rights Reserved.
 * 
 * Contributor(s):
 * 
 * ***** END LICENSE BLOCK *****
 */

/**
* @class ZaMigrationWizView
* @contructor ZaMigrationWizView
* @param parent
* @param app
* @author Greg Solovyev
**/
function ZaMigrationWizView (parent, app) {
	if (arguments.length == 0) return;
	DwtComposite.call(this, parent, null, Dwt.ABSOLUTE_STYLE);	
	this._app = app;
	this._drawn = false;	
	this._appCtxt = this.shell.getData(ZaAppCtxt.LABEL);
	this._containedObject = null;
	this.setScrollStyle(DwtControl.SCROLL);
	this.initForm(new Object(), this.getMyXForm())
//	this._createHTML();
}

ZaMigrationWizView.prototype = new DwtComposite();
ZaMigrationWizView.prototype.constructor = ZaMigrationWizView;

/**
* @param xModelMetaData - XModel metadata that describes data model
* @param xFormMetaData - XForm metadata that describes the form
**/
ZaMigrationWizView.prototype.initForm = 
function (xModelMetaData, xFormMetaData) {
	if(xModelMetaData == null || xFormMetaData == null)
		throw new AjxException("Metadata for XForm and/or XModel are not defined", AjxException.INVALID_PARAM, "DwtXWizardDialog.prototype._initForm");

	this._localXModel = new XModel(xModelMetaData);
	this._localXForm = new XForm(xFormMetaData, this._localXModel, null, this);
	this._localXForm.setController(this);
	this._localXForm.draw();
	// This is specifically for the dwt button. If the instance is null, which here it is,
	// dwt widgets don't get inserted into the xform, until you manually call refresh().
	this._localXForm.refresh();
	this._drawn = true;
}

ZaMigrationWizView.prototype.showAboutDialog = function () {
	this._appCtxt.getAppController().aboutDialog.popup();
};

ZaMigrationWizView.prototype.getMyXForm = function() {	
	var xFormObject = {
		tableCssStyle:"width:100%;overflow:auto;",
		itemDefaults: {
			_SEPARATOR_: {containerCssStyle:"padding-right:3px;padding-left:3px;"}
		},
		items: [
			{type:_GROUP_, numCols:2, 
				items: [
					{type:_GROUP_,numCols:4,
						items: [
							{type:_OUTPUT_, value:AjxImg.getImageHtml("PDFDoc")},
							{type:_ANCHOR_, cssStyle:"font-size:12px;", showInNewWindow:true, labelLocation:_NONE_, label:ZaMsg.HELP_PAGE_10,href:"/zimbraAdmin/adminhelp/pdf/MigrationWizard.pdf"},						
							{type:_OUTPUT_, value:AjxImg.getImageHtml("MigrationWiz")},
							{type:_ANCHOR_, cssStyle:"font-size:12px;", showInNewWindow:true, labelLocation:_NONE_, label:ZaMsg.HELP_MIGRATION_WIZ_1,href:"http://www.zimbra..com/community/downloads.php"}
						]
					},
					{type:_SPACER_, colSpan:"*"},					
					{type:_OUTPUT_, cssStyle:"font-size:12px;", label:null, value:ZaMsg.HELP_PAGE_11,
					 cssStyle:"padding-right:10px;padding-left:10px;"},
					{type:_SEPARATOR_, colSpan:1, cssClass:"helpSeparator"},
					{type:_SPACER_, height:"10px"}
				]
			}
		]
	}
	return xFormObject;
}

