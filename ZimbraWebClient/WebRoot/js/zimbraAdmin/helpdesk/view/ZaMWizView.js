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
* @class ZaMigrationWizView
* @contructor ZaMigrationWizView
* @param parent
* @param app
* @author Greg Solovyev
**/
function ZaMigrationWizView (parent, app) {
	if (arguments.length == 0) return;
	ZaTabView.call(this, parent, app, "ZaMigrationWizView");
	this.initForm(new Object(), this.getMyXForm())
//	this._createHTML();
}

ZaMigrationWizView.prototype = new ZaTabView();
ZaMigrationWizView.prototype.constructor = ZaMigrationWizView;
ZaTabView.XFormModifiers["ZaMigrationWizView"] = new Array();
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

ZaMigrationWizView.myXFormModifier = function(xFormObject) {	
	xFormObject.tableCssStyle="width:100%;overflow:auto;";
	xFormObject.itemDefaults = {_SEPARATOR_: {containerCssStyle:"padding-right:3px;padding-left:3px;"}};	
	xFormObject.items = [
			{type:_OUTPUT_, label:null, value:ZaMsg.DOWNLOAD_PAGE_0, colSpan:"*", cssStyle:"font-size:12pt;	font-weight: bold;"},
			{type: _GROUP_, numCols: 2, colSpan: "*", items: [
					{type:_OUTPUT_, label:null, value:ZaMsg.DOWNLOAD_PAGE_1, cssStyle:"font-size:12px;"},
					{type: _OUTPUT_, value: AjxImg.getImageHtml("Help")}
				]
			},

			{type:_SPACER_, colSpan:"*"},
			{type:_GROUP_, numCols:3, zName:"DownloadsMainGroup",
				items: [
					{type:_GROUP_,numCols:4,
						items: [
							{type:_OUTPUT_, value:AjxImg.getImageHtml("MigrationWiz")},
							{type:_OUTPUT_, cssStyle:"font-size:12px;", labelLocation:_NONE_, label:null, value:ZaMsg.IMPORT_WIZ_DOWNLOAD_LINK},
							{type:_OUTPUT_, cssStyle:"font-size:12px;", labelLocation:_NONE_, label:null, value:("("+ZaMsg.IMPORT_WIZ_DOWNLOAD_HELP+")")},
							{type:_CELLSPACER_}							
						]
					},
					{type:_GROUP_,numCols:4,
						items: [
							{type:_OUTPUT_,  value:AjxImg.getImageHtml("MigrationWiz")},
							{type:_OUTPUT_, cssStyle:"font-size:12px;", labelLocation:_NONE_, label:null, value:ZaMsg.MIG_WIZ_DOWNLOAD_LINK},
							{type:_CELLSPACER_},
							{type:_CELLSPACER_}							
							
						]
					},
					{type:_GROUP_,numCols:4,
						items: [
							{type:_OUTPUT_,  value:AjxImg.getImageHtml("MigrationWiz")},
							{type:_OUTPUT_, cssStyle:"font-size:12px;", labelLocation:_NONE_, label:null, value:ZaMsg.DOMINO_MIG_WIZ_DOWNLOAD_LINK},
							{type:_CELLSPACER_},
							{type:_CELLSPACER_}							
							
						]
					}
				]
			}
		];
}
ZaTabView.XFormModifiers["ZaMigrationWizView"].push(ZaMigrationWizView.myXFormModifier);
