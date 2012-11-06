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
* @class ZaMigrationWizView
* @contructor ZaMigrationWizView
* @param parent
* @param app
* @author Greg Solovyev
**/
ZaMigrationWizView = function(parent) {
	if (arguments.length == 0) return;
        ZaTabView.call(this, {
                parent:parent,
                iKeyName:"ZaMigrationWizView",
                contextId:ZaId.TAB_DOWNLOADS
        });
	this.setScrollStyle(Dwt.SCROLL);
	this.initForm(new Object(), this.getMyXForm())
//	this._createHTML();
}

ZaMigrationWizView.prototype = new ZaTabView();
ZaMigrationWizView.prototype.constructor = ZaMigrationWizView;
ZaTabView.XFormModifiers["ZaMigrationWizView"] = new Array();

ZaMigrationWizView.prototype.getTabIcon =
function () {
	return "MigrationWiz" ;
}

ZaMigrationWizView.prototype.getTabTitle =
function () {
	return ZaMsg.Migration_wiz_title ;
}

ZaMigrationWizView.prototype.getTitle =
function () {
	return ZaMsg.Migration_wiz_title ;
}

/**
* @param xModelMetaData - XModel metadata that describes data model
* @param xFormMetaData - XForm metadata that describes the form
**/
ZaMigrationWizView.prototype.initForm = 
function (xModelMetaData, xFormMetaData) {
	if(xModelMetaData == null || xFormMetaData == null)
		throw new AjxException("Metadata for XForm and/or XModel are not defined", AjxException.INVALID_PARAM, "ZaMigrationWizView.prototype.initForm");

	this._localXModel = new XModel(xModelMetaData);
	this._localXForm = new XForm(xFormMetaData, this._localXModel, null, this, ZaId.getTabViewId(this._contextId));
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
			{type:_OUTPUT_, label:null, value:ZaMsg.DOWNLOAD_FOR_ADMIN, colSpan:"*", cssStyle:"font-size:10pt;font-weight: bold;"},
			{type:_SPACER_, colSpan:"*"},
			{type:_GROUP_, numCols:1, colSpan:"*", zName:"DownloadsForAdmin",
				items: [
                    // bug 70664, new genaral migration tool that will replace the original exchange/domino migration tool
				    {type:_GROUP_, numCols:3,
				    	items: [
				    	    {type:_OUTPUT_, value:AjxImg.getImageHtml("Migration")},
				    	    {type:_OUTPUT_, cssStyle:"font-size:12px", labelLocation:_NONE_, label:null,
				    	     id:"general_migration_x86_link",
				    	     value: ZaMigrationWizView.getDownloadLink(ZaMsg.GENERAL_MIG_WIZ_X86_DOWNLOAD_LINK, ZaMsg.GENERAL_MIG_WIZ_X86_DOWNLOAD_LINK_MSG)
				    	    },
				    	    {type:_OUTPUT_, cssStyle:"font-size:12px", labelLocation:_NONE_, label:null,
				    	     id:"general_migration_x64_link",
				    	     value: ZaMigrationWizView.getDownloadLink(ZaMsg.GENERAL_MIG_WIZ_X64_DOWNLOAD_LINK, ZaMsg.GENERAL_MIG_WIZ_X64_DOWNLOAD_LINK_MSG)
				    	    }
				    	]
				    },
				    {type:_OUTPUT_, cssClass:"ZaDownloadText", label: null, value:ZaMsg.GENERAL_MIG_WIZ_DOWNLOAD_TEXT},

					//Groupwise Mig Wiz
					/*Disable it for bug 19041
					{type:_GROUP_,numCols:2,
						items: [
							{type:_OUTPUT_,  value:AjxImg.getImageHtml("MigrationWiz")},
							{type:_OUTPUT_, cssStyle:"font-size:12px;", labelLocation:_NONE_, label:null, value:ZaMsg.GROUPWISE_MIG_WIZ_DOWNLOAD_LINK}
							
						]
					},
					{type:_OUTPUT_, cssClass:"ZaDownloadText", label: null, value:ZaMsg.GROUPWISE_MIG_WIZ_DOWNLOAD_TEXT},
					*/
					//Domino Mig Wiz
					{type:_GROUP_,numCols:2,
						items: [
							{type:_OUTPUT_,  value:AjxImg.getImageHtml("Migration")},
							{type:_OUTPUT_, cssStyle:"font-size:12px;", labelLocation:_NONE_, label:null,
							id:"domino_migration_link",
                                value: ZaMigrationWizView.getDownloadLink(ZaMsg.DOMINO_MIG_WIZ_DOWNLOAD_LINK, ZaMsg.DOMINO_MIG_WIZ_DOWNLOAD_LINK_MSG)
                            }
						]
					},
					{type:_OUTPUT_, cssClass:"ZaDownloadText", label: null, value:ZaMsg.DOMINO_MIG_WIZ_DOWNLOAD_TEXT},

					//Exchange Mig Wiz
					{type:_GROUP_,numCols:2,
						items: [
							{type:_OUTPUT_,  value:AjxImg.getImageHtml("Migration")},
							{type:_OUTPUT_, cssStyle:"font-size:12px;", labelLocation:_NONE_, label:null,
								id:"exchange_migration_link",
								value: ZaMigrationWizView.getDownloadLink(ZaMsg.MIG_WIZ_DOWNLOAD_LINK, ZaMsg.MIG_WIZ_DOWNLOAD_LINK_MSG)
							}
						]
					},
					{type:_OUTPUT_, cssClass:"ZaDownloadText", label: null, value:ZaMsg.MIG_WIZ_DOWNLOAD_TEXT},
				]
			},
			{type:_SPACER_, colSpan:"*"},
			{type:_OUTPUT_, label:null, value:ZaMsg.DOWNLOAD_FOR_USER,  colSpan:"*", cssStyle:"font-size:10pt;font-weight: bold;"},
			{type:_SPACER_, colSpan:"*"},
			{type:_GROUP_, numCols:1, colSpan:"*", zName:"DownloadsForUser",
				items: [
					//PST import
					{type: _GROUP_ , numCols:3,
						items: [
							{type:_OUTPUT_, value:AjxImg.getImageHtml("Migration")},
							{type:_OUTPUT_, cssStyle:"font-size:12px;", labelLocation:_NONE_, label:null,
							id:"pst_import_link",
                                value: ZaMigrationWizView.getDownloadLink(ZaMsg.IMPORT_WIZ_DOWNLOAD_LINK, ZaMsg.IMPORT_WIZ_DOWNLOAD_LINK_MSG)
                            },
							{type:_OUTPUT_, cssStyle:"font-size:12px;", labelLocation:_NONE_, label:null,
							id:"pst_import_help_link",
                                value:["(<A target='_blank' onclick='ZaZimbraAdmin.unloadHackCallback();' HREF='",location.pathname,"adminhelp/pdf/User Instructions for ZCS Import Wizard.pdf?locid=",AjxEnv.DEFAULT_LOCALE,"'>",ZaMsg.IMPORT_WIZ_DOWNLOAD_HELP,"</a>)"].join("")}
						]
					},
					{type:_OUTPUT_, cssClass:"ZaDownloadText", label: null, value:ZaMsg.IMPORT_WIZ_DOWNLOAD_TEXT}/*,
					
					//TOASTER
					{type:_GROUP_,numCols:2,
						items: [
							{type:_OUTPUT_, value:AjxImg.getImageHtml("MigrationWiz")},
							{type:_OUTPUT_, cssStyle:"font-size:12px;", labelLocation:_NONE_, label:null,
                                value:ZaMigrationWizView.getDownloadLink(ZaMsg.ZIMBRA_TOASTER_DOWNLOAD_LINK, ZaMsg.ZIMBRA_TOASTER_DOWNLOAD_LINK_MSG)
                            }
						]
					},
					{type:_OUTPUT_, cssClass:"ZaDownloadText", label: null, value:ZaMsg.ZIMBRA_TOASTER_DOWNLOAD_TEXT} */
				]
			}
		];
}
ZaTabView.XFormModifiers["ZaMigrationWizView"].push(ZaMigrationWizView.myXFormModifier);

ZaMigrationWizView.getDownloadLink =  function (link, msg) {
    return "<a onclick=\"ZaZimbraAdmin.unloadHackCallback();\" target=\"_blank\" href=\""
            + link  + "\">" + msg + "</a>";
}
