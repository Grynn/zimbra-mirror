
/*
 * ***** BEGIN LICENSE BLOCK *****
 *
 * Zimbra Collaboration Suite Web Client
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

/**
* @class ZaBulkProvisionStatusDialog
* @contructor ZaBulkProvisionStatusDialog
* @author Charles Cao
* @param parent
* param app
**/


ZaBulkProvisionStatusDialog = function(parent) {
	if (arguments.length == 0) return;
	this._app = ZaApp.getInstance();
	this._standardButtons = [DwtDialog.OK_BUTTON];
    w = "420px" ;
    h = "320px"
    ZaXDialog.call(this, parent,null, com_zimbra_bulkprovision.title_provision, w, h);
	this._containedObject = [];
	this.initForm(ZaBulkProvisionStatusDialog.myXModel,this.getMyXForm());
}

ZaBulkProvisionStatusDialog.prototype = new ZaXDialog;
ZaBulkProvisionStatusDialog.prototype.constructor = ZaBulkProvisionStatusDialog;

ZaBulkProvisionStatusDialog.A_currentStatus = "currentStatus" ;
ZaBulkProvisionStatusDialog.A_createdAccounts = "createdAccounts" ;

ZaBulkProvisionStatusDialog.myXModel = {
	items: [
        {ref: ZaBulkProvisionStatusDialog.A_currentStatus, type:_STRING_},
        {ref: ZaBulkProvisionStatusDialog.A_createdAccounts, type:_LIST_,
                                        dataType: _STRING_ , outputType:_LIST_}
	]
}

/**
* @method setObject sets the object contained in the view
**/
ZaBulkProvisionStatusDialog.prototype.setObject =
function(entry) {
	this._containedObject = entry;
    if (! this._containedObject [ZaBulkProvisionStatusDialog.A_createdAccounts])
        this._containedObject [ZaBulkProvisionStatusDialog.A_createdAccounts] = [];

    this._localXForm.setInstance(this._containedObject);
	this._button[DwtDialog.OK_BUTTON].setEnabled(true);
//    this._localXForm.refresh () ;
}


ZaBulkProvisionStatusDialog.prototype.popup =
function (loc) {
	ZaXWizardDialog.prototype.popup.call(this, loc);
}

ZaBulkProvisionStatusDialog.prototype.getMyXForm =
function() {
	var sourceHeaderList = new Array();
	var sortable = 1;
    var i = 0;
    sourceHeaderList[i++] = new ZaListHeaderItem(ZaBulkProvision.A2_accountName, ZaMsg.ALV_Name_col,
                                                                        null, 250, null, null, true, true);
    sourceHeaderList[i++] = new ZaListHeaderItem(ZaBulkProvision.A2_status, com_zimbra_bulkprovision.ALV_Stauts_col,
                                                                        null, null, null, null, true, true);

    //idPrefix, label, iconInfo, width, sortable, sortField, resizeable, visible
//	sourceHeaderList[2] = new ZaListHeaderItem(ZaAccount.A_displayname, ZaMsg.ALV_DspName_col, null, 100, sortable++,ZaAccount.A_displayname, true, true);

	var xFormObject = {
		numCols:1,  height:"300px",width:"400px",align:_CENTER_,cssStyle:"text-align:center",
		items:[
			{ type: _DWT_ALERT_, style: DwtAlert.INFORMATION, iconVisible: true,
				  content: null,   align:_CENTER_, valign:_MIDDLE_,colSpan:"*",width:"90%",
				  ref:ZaBulkProvisionStatusDialog.A_currentStatus
			},

		   	{type:_SPACER_, height:"10"},
		   	{ref:ZaBulkProvisionStatusDialog.A_createdAccounts, type:_DWT_LIST_, height:200, width:400, colSpan:"*",  cssClass: "DLSource",
				forceUpdate: true, widgetClass:ZaBPStatusDialogListView, headerList:sourceHeaderList, hideHeader: false
			}
		]
	}
	return xFormObject;
}

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
ZaBPStatusDialogListView = function(parent, className, posStyle, headerList) {
	if (arguments.length == 0) return;
	ZaListView.call(this, parent, className, posStyle, headerList);
	this.hideHeader = true;
}

ZaBPStatusDialogListView.prototype = new ZaListView;
ZaBPStatusDialogListView.prototype.constructor = ZaBPStatusDialogListView;

ZaBPStatusDialogListView.prototype.toString = function() {
	return "ZaBPStatusDialogListView";
};

ZaBPStatusDialogListView.prototype.createHeaderHtml = function (defaultColumnSort) {
	if(!this.hideHeader) {
		DwtListView.prototype.createHeaderHtml.call(this,defaultColumnSort);
	}
}


ZaBPStatusDialogListView.prototype._createItemHtml =
function(account, now, isDragProxy) {
	var html = new Array(50);
	var	div = document.createElement("div");
	div[DwtListView._STYLE_CLASS] = "Row";
	div[DwtListView._SELECTED_STYLE_CLASS] = div[DwtListView._STYLE_CLASS] + "-" + DwtCssStyle.SELECTED;
	div.className = div[DwtListView._STYLE_CLASS];
	this.associateItemWithElement(account, div, DwtListView.TYPE_LIST_ITEM);

	var idx = 0;
	html[idx++] = "<table width='100%' cellspacing='2' cellpadding='0'>";
	html[idx++] = "<tr>";

    var cnt = this._headerList.length;
    for(var i = 0; i < cnt; i++) {
        var field = this._headerList[i]._field;
        html[idx++] = "<td align='left' width=" + this._headerList[i]._width + "><nobr>";
        html[idx++] = AjxStringUtil.htmlEncode(account[field]);
        html[idx++] = "</nobr></td>";
    }

	html[idx++] = "</tr></table>";
	div.innerHTML = html.join("");
	return div;
}


ZaBPStatusDialogListView.prototype._setNoResultsHtml = function() {
	var buffer = new AjxBuffer();
	var	div = document.createElement("div");

	buffer.append("<table width='100%' cellspacing='0' cellpadding='1'>",
				  "<tr><td class='NoResults'><br>&nbsp",
				  "</td></tr></table>");

	div.innerHTML = buffer.toString();
	this._addRow(div);
};
