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
* @class ZaHelpView
* @contructor ZaHelpView
* @param parent
* @param app
* @author Greg Solovyev
**/
ZaHelpView = function(parent) {
	if (arguments.length == 0) return;
	ZaTabView.call(this, parent,"ZaHelpView");
	this.setScrollStyle(Dwt.SCROLL);
	this.initForm(new Object(), this.getMyXForm())
//	this._createHTML();
}
ZaHelpView.mainHelpPage = "administration_console_help.htm";
ZaHelpView.RELEASE_NOTE_LINK = "adminhelp/pdf/Zimbra_Release_Note.pdf";
ZaHelpView.HELP_FORUM_LINK = "http://www.zimbra.com/forums/";
ZaHelpView.HELP_WIKI_LINK = "http://wiki.zimbra.com";
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
		throw new AjxException("Metadata for XForm and/or XModel are not defined", AjxException.INVALID_PARAM, "ZaHelpView.prototype.initForm");

	this._localXModel = new XModel(xModelMetaData);
	this._localXForm = new XForm(xFormMetaData, this._localXModel, null, this);
	this._localXForm.setController(this);
	this._localXForm.draw();
	// This is specifically for the dwt button. If the instance is null, which here it is,
	// dwt widgets don't get inserted into the xform, until you manually call refresh().
//	this._localXForm.refresh();
    //since 6.0, we use setInstance to force the item to got inserted to xform. An xform bug???
    this._localXForm.setInstance({}) ;
    this._drawn = true;                                    
}

ZaHelpView.prototype.showAboutDialog = function () {                  
    var appCtrl = this._appCtxt.getAppController() ;
    if (!appCtrl.aboutDialog) {
        appCtrl.aboutDialog = new ZaAboutDialog(this.shell,null,ZabMsg.about_title);
    }
    appCtrl.aboutDialog.popup();
};

ZaHelpView.showWikiLink = function () {
    return !AjxUtil.isEmpty(ZabMsg.HELP_WIKI_LINK);
}
ZaHelpView.myXFormModifier = function(xFormObject) {	
	xFormObject.tableCssStyle="width:100%;overflow:auto;";
	xFormObject.itemDefaults = {_SEPARATOR_: {containerCssStyle:"padding-right:3px;padding-left:3px;"}};
	xFormObject.items = [
	        {type:_SWITCH_, align:_LEFT_, valign:_TOP_, items:[{type:_ZATABCASE_,id:"help_view_tab",  numCols:1, width:"100%", caseKey:1,
	        		paddingStyle:(appNewUI? "padding-left:15px;":null), width:(appNewUI? "98%":"100%"), cellpadding:(appNewUI?2:0),
	        		visibilityChecks:[function() { return true; }],
	        		items: [
			{type:_OUTPUT_, label:null, value:ZabMsg.HELP_PAGE_0, colSpan:"*", cssStyle:"font-size:12pt;	font-weight: bold;"},
			{type:_OUTPUT_, label:null, value:ZabMsg.HELP_PAGE_1, colSpan:"*", cssStyle:"font-size:12px;"},

			{type:_SPACER_, colSpan:"*"},
			{type:_GROUP_, numCols:2, id:"ZimbraHelpPageDownloadItems" , colSpan: "*",
				items: [
					{type:_GROUP_,numCols:2,
						items: [
							{type:_OUTPUT_, value:AjxImg.getImageHtml("Help")},
							{type:_ANCHOR_, cssStyle:"font-size:12px;", showInNewWindow:true, labelLocation:_NONE_, label:ZabMsg.HELP_PAGE_2, href:(location.pathname + ZaUtil.HELP_URL + ZaHelpView.mainHelpPage + "?locid="+AjxEnv.DEFAULT_LOCALE)}
						]
					},
					{type:_GROUP_,numCols:2,
						items: [
							{type:_OUTPUT_,  value:AjxImg.getImageHtml("PDFDoc")},
							{type:_ANCHOR_, cssStyle:"font-size:12px;", showInNewWindow:true, labelLocation:_NONE_, label:ZaMsg.HELP_PAGE_4, href:(location.pathname + ZaHelpView.RELEASE_NOTE_LINK +"?locid="+AjxEnv.DEFAULT_LOCALE)}
						]
					},
					{type:_SPACER_, colSpan:"*"},					
					{type:_OUTPUT_, cssStyle:"font-size:12px;", label:null, value:ZabMsg.HELP_PAGE_3, 
					 cssStyle:"padding-right:10px;padding-left:10px;",containerCssStyle: "width:50%;"},
					{type:_OUTPUT_, cssStyle:"font-size:12px;", label:null, value:ZaMsg.HELP_PAGE_5,
					 cssStyle:"padding-right:10px;padding-left:10px;",containerCssStyle: "width:50%;"},
					{type:_SEPARATOR_, colSpan:1, cssClass:"helpSeparator"},
					{type:_SEPARATOR_, colSpan:1, cssClass:"helpSeparator"},

					{type:_GROUP_,numCols:2,
						items: [
							{type:_OUTPUT_, value:AjxImg.getImageHtml("PDFDoc")},
							{type:_ANCHOR_, cssStyle:"font-size:12px;", href:ZaHelpView.HELP_FORUM_LINK, showInNewWindow:true,labelLocation:_NONE_,  label:ZabMsg.HELP_PAGE_6 }
						]
					},					
					{type:_GROUP_,numCols:2,
                        visibilityChecks:[[ZaHelpView.showWikiLink]],
						items: [
							{type:_OUTPUT_, value:AjxImg.getImageHtml("PDFDoc")},
							{type:_ANCHOR_, cssStyle:"font-size:12px;", href:ZaHelpView.HELP_WIKI_LINK, showInNewWindow:true,labelLocation:_NONE_,  label:ZaMsg.HELP_PAGE_12 }
						]
					},
					{type:_SPACER_, colSpan:"*"},					
					{type:_OUTPUT_, cssStyle:"font-size:12px;", label:null, value:ZabMsg.HELP_PAGE_7,
					 cssStyle:"padding-right:10px;padding-left:10px;"},
					{type:_OUTPUT_, label:null, value:ZabMsg.HELP_PAGE_13, visibilityChecks:[[ZaHelpView.showWikiLink]], cssStyle:"padding-right:10px;padding-left:10px;"},
					{type:_SEPARATOR_, colSpan:1, cssClass:"helpSeparator"},
					{type:_SEPARATOR_, colSpan:1, cssClass:"helpSeparator"}
				]
			},
        {type: _SPACER_, height: 10 },    
        {type:_GROUP_, colSpan:"*", items: [
                {type:_DWT_BUTTON_, id: "about_button", label:ZabMsg.About_Button_Label,
                    onActivate:"this.getFormController().showAboutDialog()", width:"125px"}
            ]
        }
    ]}]}];

    var adminGuideItem;
    if (!ZaSettings.isOctopus()) {
        adminGuideItem = [
            {type:_GROUP_,numCols:2,
                items: [
                    {type:_OUTPUT_, value:AjxImg.getImageHtml("PDFDoc")},
                    {type:_ANCHOR_, cssStyle:"font-size:12px;", showInNewWindow:true, labelLocation:_NONE_, label:ZabMsg.HELP_PAGE_8,href:(location.pathname + "adminhelp/pdf/admin.pdf?locid="+AjxEnv.DEFAULT_LOCALE)}
                ]
            },
            {type:_GROUP_,numCols:4,
                items: [
                    {type:_OUTPUT_, value:AjxImg.getImageHtml("PDFDoc")},
                    {type:_ANCHOR_, cssStyle:"font-size:12px;", showInNewWindow:true, labelLocation:_NONE_, label:ZaMsg.HELP_PAGE_10_EXCHANGE,href:(location.pathname + "adminhelp/pdf/MigrationWizard.pdf?locid="+AjxEnv.DEFAULT_LOCALE)},
                    {type:_OUTPUT_, value:AjxImg.getImageHtml("PDFDoc")},
                    {type:_ANCHOR_, cssStyle:"font-size:12px;", showInNewWindow:true, labelLocation:_NONE_, label:ZaMsg.HELP_PAGE_10_DOMINO,href:(location.pathname + "adminhelp/pdf/MigrationWizard_Domino.pdf?locid="+AjxEnv.DEFAULT_LOCALE)}
                ]
            },
            {type:_SPACER_, colSpan:"*"},
            {type:_OUTPUT_, cssStyle:"font-size:12px;", label:null, value:ZabMsg.HELP_PAGE_9,
             cssStyle:"padding-right:10px;padding-left:10px;"},
            {type:_OUTPUT_, cssStyle:"font-size:12px;", label:null, value:ZaMsg.HELP_PAGE_11,
             cssStyle:"padding-right:10px;padding-left:10px;"},
            {type:_SEPARATOR_, colSpan:1, cssClass:"helpSeparator"},
            {type:_SEPARATOR_, colSpan:1, cssClass:"helpSeparator"},

            {type:_GROUP_,numCols:2, id: "HelpOtherGuides",
                items: [
                    {type:_OUTPUT_, value:AjxImg.getImageHtml("PDFDoc")},
                    {type:_ANCHOR_, cssStyle:"font-size:12px;", showInNewWindow:true, labelLocation:_NONE_, label:ZaMsg.HELP_OTHER_GUIDES_IMPORT,href:(location.pathname + "adminhelp/pdf/Import_Wizard_Outlook.pdf?locid="+AjxEnv.DEFAULT_LOCALE)}
                ]
            },
            {type:_GROUP_,numCols:2, id: "iCalMigDoc",
                items: [
                    {type:_OUTPUT_, value:AjxImg.getImageHtml("PDFDoc")},
                    {type:_ANCHOR_, cssStyle:"font-size:12px;", showInNewWindow:true, labelLocation:_NONE_, label: ZaMsg.ICAL_MIG_GUIDE,
                         href:(location.pathname + "help/admin/pdf/Zimbra%20iCalendar%20Migration%20Guide.pdf?locid=" + AjxEnv.DEFAULT_LOCALE)}
                ]
            },
            {type:_SPACER_},

            {type:_OUTPUT_, cssStyle:"font-size:12px;", label:null, value:ZaMsg.HELP_OTHER_GUIDES_IMPORT_INFO,
             cssStyle:"padding-right:10px;padding-left:10px;"},
            {type:_OUTPUT_, cssStyle:"font-size:12px;", label:null, value:ZaMsg.ICAL_MIG_GUIDE_INFO,
                cssStyle:"padding-right:10px;padding-left:10px;"},
            {type:_SEPARATOR_, colSpan:1, cssClass:"helpSeparator"},
            {type:_SEPARATOR_, colSpan:1, cssClass:"helpSeparator"}
        ];
    } else {
        adminGuideItem = [
            {type:_GROUP_,numCols:2,
                items: [
                    {type:_OUTPUT_, value:AjxImg.getImageHtml("PDFDoc")},
                    {type:_ANCHOR_, cssStyle:"font-size:12px;", showInNewWindow:true, labelLocation:_NONE_, label:ZabMsg.HELP_PAGE_8,href:(location.pathname + "help/admin/pdf/o_admin_guide.pdf?locid="+AjxEnv.DEFAULT_LOCALE)}
                ]
            },
            {type:_GROUP_,numCols:2, id: "zwcEndUserGuide",
                items: [
                    {type:_OUTPUT_, value:AjxImg.getImageHtml("PDFDoc")},
                    {type:_ANCHOR_, cssStyle:"font-size:12px;", showInNewWindow:true, labelLocation:_NONE_, label: ZabMsg.ZWC_END_USER_GUIDE,
                         href:(location.pathname + "help/admin/pdf/o_webclient_user_guide.pdf?locid=" + AjxEnv.DEFAULT_LOCALE)},
                    {type:_OUTPUT_, value:AjxImg.getImageHtml("PDFDoc")},
                    {type:_ANCHOR_, cssStyle:"font-size:12px;", showInNewWindow:true, labelLocation:_NONE_, label: ZabMsg.ANDROID_END_USER_GUIDE,
                         href:(location.pathname + "help/admin/pdf/o_androidclient_user_guide.pdf?locid=" + AjxEnv.DEFAULT_LOCALE)},
                    {type:_OUTPUT_, value:AjxImg.getImageHtml("PDFDoc")},
                    {type:_ANCHOR_, cssStyle:"font-size:12px;", showInNewWindow:true, labelLocation:_NONE_, label: ZabMsg.IOS_END_USER_GUIDE,
                         href:(location.pathname + "help/admin/pdf/o_iosclient_user_guide.pdf?locid=" + AjxEnv.DEFAULT_LOCALE)}
                ]
            },
            {type:_SPACER_, colSpan:"*"},
            {type:_OUTPUT_, cssStyle:"font-size:12px;", label:null, value:ZabMsg.HELP_PAGE_9,
             cssStyle:"padding-right:10px;padding-left:10px;"},
            {type:_OUTPUT_, cssStyle:"font-size:12px;", label:null, value:ZabMsg.ZWC_END_USER_GUIDE_INFO,
                cssStyle:"padding-right:10px;padding-left:10px;"},
            {type:_SEPARATOR_, colSpan:1, cssClass:"helpSeparator"},
            {type:_SEPARATOR_, colSpan:1, cssClass:"helpSeparator"}
        ];
    }

    var helpItems = xFormObject.items[0].items[0].items ;
    for (var i=0; i< helpItems.length; i++) {
        //insert teh networkHelpItems before the About button
        if (helpItems[i].id == "ZimbraHelpPageDownloadItems") {
            helpItems [i].items = helpItems[i].items.concat(adminGuideItem) ;
            break ;
        }
    }

    if (!ZaSettings.isNetworkVersion () && !ZaSettings.isOctopus()) {
        var zwcUserGuideItem = [
             //ZWC end user guide
            {type:_GROUP_,numCols:2, id: "zwcEndUserGuide",
                items: [
                    {type:_OUTPUT_, value:AjxImg.getImageHtml("PDFDoc")},
                    {type:_ANCHOR_, cssStyle:"font-size:12px;", showInNewWindow:true, labelLocation:_NONE_, label: ZabMsg.ZWC_END_USER_GUIDE,
                         href:(location.pathname + "help/admin/pdf/zimbra_user_guide.pdf?locid=" + AjxEnv.DEFAULT_LOCALE)}
                ]
            },
            {type:_CELL_SPACER_},
            {type:_SPACER_, colSpan:"*"},
            {type:_OUTPUT_, cssStyle:"font-size:12px;", label:null, value:ZabMsg.ZWC_END_USER_GUIDE_INFO,
                cssStyle:"padding-right:10px;padding-left:10px;"},
            {type:_CELL_SPACER_},
            {type:_SEPARATOR_, colSpan:1, cssClass:"helpSeparator"}
        ];
        var helpItems = xFormObject.items[0].items[0].items ;
        for (var i=0; i< helpItems.length; i++) {
            //insert teh networkHelpItems before the About button
            if (helpItems[i].id == "ZimbraHelpPageDownloadItems") {
                helpItems [i].items = helpItems[i].items.concat(zwcUserGuideItem) ;
                break ;
            }
        }
    }else {
        //zwcUserGuide Item will be modified in delegated admin extension
    }
}
ZaTabView.XFormModifiers["ZaHelpView"].push(ZaHelpView.myXFormModifier);


