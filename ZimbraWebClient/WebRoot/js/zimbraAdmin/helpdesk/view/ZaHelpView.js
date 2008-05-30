/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2005, 2006, 2007 Zimbra, Inc.
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

/**
* @class ZaHelpView
* @contructor ZaHelpView
* @param parent
* @param app
* @author Greg Solovyev
**/
ZaHelpView = function(parent, app) {
	if (arguments.length == 0) return;
	ZaTabView.call(this, parent, app, "ZaHelpView");
	this.setScrollStyle(Dwt.SCROLL);
	this.initForm(new Object(), this.getMyXForm())
//	this._createHTML();
}

ZaHelpView.prototype = new ZaTabView();
ZaHelpView.prototype.constructor = ZaHelpView;
ZaTabView.XFormModifiers["ZaHelpView"] = new Array();

ZaHelpView.prototype.getTabIcon =
function () {
	return "Help" ;
}

ZaHelpView.prototype.getTabTitle =
function () {
	return ZaMsg.Help_view_title ;
}

ZaHelpView.prototype.getTitle =
function () {
	return ZaMsg.Help_view_title ;
}


/**
* @param xModelMetaData - XModel metadata that describes data model
* @param xFormMetaData - XForm metadata that describes the form
**/
ZaHelpView.prototype.initForm = 
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

ZaHelpView.prototype.showAboutDialog = function () {
	this._appCtxt.getAppController().aboutDialog.popup();
};

ZaHelpView.myXFormModifier = function(xFormObject) {	
	xFormObject.tableCssStyle="width:100%;overflow:auto;";
	xFormObject.itemDefaults = {_SEPARATOR_: {containerCssStyle:"padding-right:3px;padding-left:3px;"}};
	xFormObject.items = [
			{type:_OUTPUT_, label:null, value:ZaMsg.HELP_PAGE_0, colSpan:"*", cssStyle:"font-size:12pt;	font-weight: bold;"},
			{type:_OUTPUT_, label:null, value:ZaMsg.HELP_PAGE_1, colSpan:"*", cssStyle:"font-size:12px;"},

			{type:_SPACER_, colSpan:"*"},
			{type:_GROUP_, numCols:2, 
				items: [
					{type:_GROUP_,numCols:2,
						items: [
							{type:_OUTPUT_, value:AjxImg.getImageHtml("Help")},
							{type:_ANCHOR_, cssStyle:"font-size:12px;", showInNewWindow:true, labelLocation:_NONE_, label:ZaMsg.HELP_PAGE_2, href:(location.pathname + ZaUtil.HELP_URL + "administration_console_help.htm?locid="+AjxEnv.DEFAULT_LOCALE)}
						]
					},
					{type:_GROUP_,numCols:2,
						items: [
							{type:_OUTPUT_,  value:AjxImg.getImageHtml("PDFDoc")},
							{type:_ANCHOR_, cssStyle:"font-size:12px;", showInNewWindow:true, labelLocation:_NONE_, label:ZaMsg.HELP_PAGE_4, href:(location.pathname + "adminhelp/pdf/Zimbra_Release_Note.pdf")}
						]
					},
					{type:_SPACER_, colSpan:"*"},					
					{type:_OUTPUT_, cssStyle:"font-size:12px;", label:null, value:ZaMsg.HELP_PAGE_3, 
					 cssStyle:"padding-right:10px;padding-left:10px;"},
					{type:_OUTPUT_, cssStyle:"font-size:12px;", label:null, value:ZaMsg.HELP_PAGE_5,
					 cssStyle:"padding-right:10px;padding-left:10px;"},
					{type:_SEPARATOR_, colSpan:1, cssClass:"helpSeparator"},
					{type:_SEPARATOR_, colSpan:1, cssClass:"helpSeparator"},


					{type:_GROUP_,numCols:2,
						items: [
								//{type:_OUTPUT_, value:"&nbsp;"},
							{type:_OUTPUT_, value:AjxImg.getImageHtml("favicon")},
							{type:_ANCHOR_, cssStyle:"font-size:12px;", href:"http://www.zimbra.com/forums/", showInNewWindow:true,labelLocation:_NONE_,  label:ZaMsg.HELP_PAGE_6 }
						]
					},					
					{type:_GROUP_,numCols:2,
						items: [
								//{type:_OUTPUT_, value:"&nbsp;"},
							{type:_OUTPUT_, value:AjxImg.getImageHtml("favicon")},
							{type:_ANCHOR_, cssStyle:"font-size:12px;", href:"http://wiki.zimbra.com", showInNewWindow:true,labelLocation:_NONE_,  label:ZaMsg.HELP_PAGE_12 }
						]
					},
					{type:_SPACER_, colSpan:"*"},					
					{type:_OUTPUT_, cssStyle:"font-size:12px;", label:null, value:ZaMsg.HELP_PAGE_7, 
					 cssStyle:"padding-right:10px;padding-left:10px;"},
					{type:_OUTPUT_, label:null, value:ZaMsg.HELP_PAGE_13, cssStyle:"padding-right:10px;padding-left:10px;"},
					{type:_SEPARATOR_, colSpan:1, cssClass:"helpSeparator"},
					{type:_SEPARATOR_, colSpan:1, cssClass:"helpSeparator"},


					{type:_GROUP_,numCols:2,
						items: [
							{type:_OUTPUT_, value:AjxImg.getImageHtml("PDFDoc")},
							{type:_ANCHOR_, cssStyle:"font-size:12px;", showInNewWindow:true, labelLocation:_NONE_, label:ZaMsg.HELP_PAGE_8,href:(location.pathname + "adminhelp/pdf/admin.pdf")}
						]
					},
					{type:_GROUP_,numCols:4,
						items: [
							{type:_OUTPUT_, value:AjxImg.getImageHtml("PDFDoc")},
							//{type:_OUTPUT_, value:ZaMsg.HELP_PAGE_10},
							{type:_ANCHOR_, cssStyle:"font-size:12px;", showInNewWindow:true, labelLocation:_NONE_, label:ZaMsg.HELP_PAGE_10_EXCHANGE,href:(location.pathname + "adminhelp/pdf/MigrationWizard.pdf")},
							{type:_OUTPUT_, value:AjxImg.getImageHtml("PDFDoc")},
							{type:_ANCHOR_, cssStyle:"font-size:12px;", showInNewWindow:true, labelLocation:_NONE_, label:ZaMsg.HELP_PAGE_10_DOMINO,href:(location.pathname + "adminhelp/pdf/MigrationWizard_Domino.pdf")}
							//{type:_ANCHOR_, cssStyle:"font-size:12px;", showInNewWindow:true, labelLocation:_NONE_, label:ZaMsg.HELP_PAGE_10,href:(location.pathname + "adminhelp/pdf/MigrationWizard.pdf")}
						]
					},
					{type:_SPACER_, colSpan:"*"},					
					{type:_OUTPUT_, cssStyle:"font-size:12px;", label:null, value:ZaMsg.HELP_PAGE_9, 
					 cssStyle:"padding-right:10px;padding-left:10px;"},
					{type:_OUTPUT_, cssStyle:"font-size:12px;", label:null, value:ZaMsg.HELP_PAGE_11,
					 cssStyle:"padding-right:10px;padding-left:10px;"},
					{type:_SEPARATOR_, colSpan:1, cssClass:"helpSeparator"},
					{type:_SEPARATOR_, colSpan:1, cssClass:"helpSeparator"},
					
					{type:_GROUP_,numCols:2, id: "HelpOtherGuides",
						items: [
							{type:_OUTPUT_, value:AjxImg.getImageHtml("PDFDoc")},
							//{type:_OUTPUT_, value: ZaMsg.HELP_OTHER_GUIDES},
							{type:_ANCHOR_, cssStyle:"font-size:12px;", showInNewWindow:true, labelLocation:_NONE_, label:ZaMsg.HELP_OTHER_GUIDES_IMPORT,href:(location.pathname + "adminhelp/pdf/Import_Wizard_Outlook.pdf")}
						]
					},
					{type:_SPACER_},
					{type:_SPACER_, colSpan:"*"},
					{type:_OUTPUT_, cssStyle:"font-size:12px;", label:null, value:ZaMsg.HELP_OTHER_GUIDES_IMPORT_INFO, 
					 cssStyle:"padding-right:10px;padding-left:10px;"},
					{type:_SPACER_},
					{type:_SEPARATOR_, colSpan:1, cssClass:"helpSeparator"},
					{type:_SPACER_, height:"10px"},
					
					{type:_DWT_BUTTON_, label:ZaMsg.About_Button_Label, onActivate:"this.getFormController().showAboutDialog()", width:"125px"}
				]
			}
		];
}
ZaTabView.XFormModifiers["ZaHelpView"].push(ZaHelpView.myXFormModifier);


