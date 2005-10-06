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
 * The Original Code is: Zimbra Collaboration Suite.
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
* @class ZaHelpView
* @contructor ZaHelpView
* @param parent
* @param app
* @author Greg Solovyev
**/
function ZaHelpView (parent, app) {
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

ZaHelpView.prototype = new DwtComposite();
ZaHelpView.prototype.constructor = ZaHelpView;

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

ZaHelpView.prototype.getMyXForm = function() {	
	var xFormObject = {
		tableCssStyle:"width:100%;overflow:auto;",
		itemDefaults: {
			_SEPARATOR_: {containerCssStyle:"padding-right:3px;padding-left:3px;"},
		},
		items: [
			{type:_OUTPUT_, label:null, value:ZaMsg.HELP_PAGE_0, colSpan:"*", cssStyle:"font-size:12pt;	font-weight: bold;"},
			{type:_OUTPUT_, label:null, value:ZaMsg.HELP_PAGE_1, colSpan:"*", cssStyle:"font-size:12px;"},

			{type:_SPACER_, colSpan:"*"},
			{type:_GROUP_, numCols:2, 
				items: [
					{type:_GROUP_,numCols:2,
						items: [
							{type:_OUTPUT_, value:AjxImg.getImageHtml("Help")},
							{type:_ANCHOR_, cssStyle:"font-size:12px;", showInNewWindow:true, labelLocation:_NONE_, label:ZaMsg.HELP_PAGE_2, href:"/zimbraAdmin/adminhelp/html/OpenSourceAdminHelp/administration_console_help.htm"}
						]
					},
					{type:_GROUP_,numCols:2,
						items: [
							{type:_OUTPUT_,  value:AjxImg.getImageHtml("PDFDoc")},
							{type:_ANCHOR_, cssStyle:"font-size:12px;", showInNewWindow:true, labelLocation:_NONE_, label:ZaMsg.HELP_PAGE_4, href:"/zimbraAdmin/adminhelp/pdf/admin.pdf"}
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
							{type:_CELL_SPACER_, width:"24px"},
							{type:_OUTPUT_, cssStyle:"font-size:12px;", label:null, value:ZaMsg.HELP_PAGE_6 }
						]
					},					
					{type:_OUTPUT_, label:null, value:"&nbsp;"},
					{type:_SPACER_, colSpan:"*"},					
					{type:_OUTPUT_, cssStyle:"font-size:12px;", label:null, value:ZaMsg.HELP_PAGE_7, 
					 cssStyle:"padding-right:10px;padding-left:10px;"},
					{type:_OUTPUT_, label:null, value:"&nbsp;", cssStyle:"padding-right:10px;padding-left:10px;"},
					{type:_SEPARATOR_, colSpan:1, cssClass:"helpSeparator"},
					{type:_SEPARATOR_, colSpan:1, cssClass:"helpSeparator"},


					{type:_GROUP_,numCols:2,
						items: [
							{type:_OUTPUT_, value:AjxImg.getImageHtml("PDFDoc")},
							{type:_ANCHOR_, cssStyle:"font-size:12px;", showInNewWindow:true, labelLocation:_NONE_, label:ZaMsg.HELP_PAGE_8,href:"/zimbraAdmin/adminhelp/pdf/admin.pdf"}
						]
					},
					{type:_GROUP_,numCols:2,
						items: [
							{type:_OUTPUT_, value:AjxImg.getImageHtml("PDFDoc")},
							{type:_ANCHOR_, cssStyle:"font-size:12px;", showInNewWindow:true, labelLocation:_NONE_, label:ZaMsg.HELP_PAGE_10,href:"/zimbraAdmin/adminhelp/pdf/admin.pdf"}
						]
					},
					{type:_SPACER_, colSpan:"*"},					
					{type:_OUTPUT_, cssStyle:"font-size:12px;", label:null, value:ZaMsg.HELP_PAGE_9, 
					 cssStyle:"padding-right:10px;padding-left:10px;"},
					{type:_OUTPUT_, cssStyle:"font-size:12px;", label:null, value:ZaMsg.HELP_PAGE_11,
					 cssStyle:"padding-right:10px;padding-left:10px;"},
					{type:_SEPARATOR_, colSpan:1, cssClass:"helpSeparator"},
					{type:_SEPARATOR_, colSpan:1, cssClass:"helpSeparator"},
					{type:_SPACER_, height:"10px"},
					{type:_DWT_BUTTON_, label:"About Zimbra Version",onActivate:"this.getFormController().showAboutDialog()",
					 width:"125px"}
				]
			},
		]
	}
	return xFormObject;
}
/*
ZaHelpView.prototype._createHTML = function() {
	var html = new Array();
	var i = 0;
	html[i++] = "<div>";
	html[i++] = "<h1><font color=\"#000000\" size=\"3\" face=\"Arial, Helvetica, sans-serif\">Zimbra Administrator's Help Desk</font></h1>";

	html[i++] = "<p><font size=\"2\" face=\"Arial, Helvetica, sans-serif\">";
	html[i++] = ZaMsg.HELP_PAGE_1;
	html[i++] = "</font></p>";

	html[i++] = "<div align=left> ";
	html[i++] = "  <table x-use-null-cells	style=\"x-cell-content-align: top; width:100%; float:alignleft;	border-spacing:15px; border-spacing:15px;\" cellspacing=15width=60%>";
    html[i++] = "  <col style=\"width: 50%;\">";
    html[i++] = "  <col style=\"width: 50%;\">";
    html[i++] = "  <tr valign=top> ";
	html[i++] = "  <td width=35 style=\"width: 50%; padding-right: 10px; padding-left: 10px;\"> ";
	html[i++] = "  <table><tr><td>";
    html[i++] = "  <a href=\"../admin_help/Administration%20Console%20Help/Getting_Started/About_ZCS.htm\" target=_blank>";
	html[i++] = ZaMsg.HELP_PAGE_2;
    html[i++] = " </a>&nbsp;</td><td>";
    html[i++] = AjxImg.getImageHtml("Help");
    html[i++] =	"</td></tr></table></td></tr>";
	html[i++] = "  <td width=35 style=\"width: 50%; border-bottom-style: Ridge; border-bottom-width: 4px; border-bottom-color: #CCCCCC; padding-right: 10px; padding-left: 10px;\"> ";
	html[i++] = ZaMsg.HELP_PAGE_3;
    html[i++] = " </td>";
	html[i++] = "  <td width=35 style=\"width: 50%; border-bottom-style: Ridge; border-bottom-width: 4px; border-bottom-color: #CCCCCC; padding-right: 10px; padding-left: 10px;\"> ";
	html[i++] = "<p><font size=\"2\" face=\"Arial, Helvetica, sans-serif\"><strong>";
	html[i++] = ZaMsg.HELP_PAGE_4;
	html[i++] = AjxImg.getImageHtml("PDFDoc");
	html[i++] = "</strong></font></p>";
    html[i++] = "<p><font size=\"2\" face=\"Arial, Helvetica, sans-serif\">";
	html[i++] = ZaMsg.HELP_PAGE_5;    
    html[i++] = "</font></p></td>";
    html[i++] = "</tr><tr style=\"x-cell-content-align: top;\" valign=top> ";
    html[i++] = "<td style=\"width: 50%;border-bottom-style: Ridge;border-bottom-width: 4px;border-bottom-color: #CCCCCC;padding-right: 10px;padding-left: 10px;\" width=35>";
    html[i++] = "<p><a href=\"http://www.zimbra.com/forums/\" target=_blank><font size=\"2\" face=\"Arial, Helvetica, sans-serif\"><strong>";
    html[i++] = ZaMsg.HELP_PAGE_6;    
    html[i++] = "</strong></font></a></p>";
    html[i++] = "<p style=\"font-weight: normal;\"><b><span style=\"font-weight: normal;\"><font size=\"2\" face=\"Arial, Helvetica, sans-serif\">";
    html[i++] = ZaMsg.HELP_PAGE_7;   
    html[i++] = "</font></span></b></td>";
    html[i++] = "<td style=\"width: 50%;border-bottom-style: Ridge;border-bottom-width: 4px;border-bottom-color: #CCCCCC;padding-right: 10px;padding-left: 10px;\" width=35>";
    html[i++] = "<p><a href=\"http://www.zimbra.com/blog/\ target=_blank></a></p>";
    html[i++] = "<p>&nbsp;</td></tr>";
    html[i++] = "<tr style=\"x-cell-content-align: top;\" valign=top><td style=\"width: 50%;border-bottom-style: Ridge;border-bottom-width: 4px;border-bottom-color: #CCCCCC;padding-right: 10px;padding-left: 10px;\"	width=35>"; 
    html[i++] = "<p><font size=\"2\" face=\"Arial, Helvetica, sans-serif\"><strong>";
    html[i++] = ZaMsg.HELP_PAGE_8;    
    html[i++] = "</strong></font> ";
 	html[i++] = AjxImg.getImageHtml("PDFDoc");   
    html[i++] = "</p>";
    html[i++] = "<p><font size=\"2\" face=\"Arial, Helvetica, sans-serif\">";
    html[i++] = ZaMsg.HELP_PAGE_9;            
    html[i++] = "</font></td><td style=\"width: 50%;border-bottom-style: Ridge;border-bottom-width: 4px;border-bottom-color: #CCCCCC;padding-right: 10px;padding-left: 10px;\"	width=35>";
    html[i++] = "<p><font size=\"2\" face=\"Arial, Helvetica, sans-serif\"><strong>";
    html[i++] = ZaMsg.HELP_PAGE_10;    
    html[i++] = "</strong></font> ";
 	html[i++] = AjxImg.getImageHtml("PDFDoc");       
    html[i++] = "<p><font size=\"2\" face=\"Arial, Helvetica, sans-serif\">";
    html[i++] = ZaMsg.HELP_PAGE_11;    
    html[i++] = "</font> </td></tr>";
    html[i++] = "  </table>";
    html[i++] = "</div>";
    html[i++] = "<p><font size=\"2\" face=\"Arial, Helvetica, sans-serif\"> </font></p>";
	html[i++] = "</div>";
	this.getHtmlElement().innerHTML = html.join("");
}*/
