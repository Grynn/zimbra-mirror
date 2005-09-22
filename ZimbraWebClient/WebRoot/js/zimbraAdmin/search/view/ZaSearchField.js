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

function ZaSearchField(parent, className, size, posStyle) {

	DwtComposite.call(this, parent, className, posStyle);
	this._containedObject = new ZaSearch();
	this._initForm(ZaSearch.myXModel,this._getMyXForm());
	this._localXForm.setInstance(this._containedObject);
}

ZaSearchField.prototype = new DwtComposite;
ZaSearchField.prototype.constructor = ZaSearchField;

ZaSearchField.prototype.toString = 
function() {
	return "ZaSearchField";
}

ZaSearchField.UNICODE_CHAR_RE = /\S/;

ZaSearchField.prototype.registerCallback =
function(callbackFunc, obj) {
	this._callbackFunc = callbackFunc;
	this._callbackObj = obj;
}

ZaSearchField.prototype.setObject = 
function (searchObj) {
	this._containedObject = searchObj;
	this._localXForm.setInstance(this._containedObject);
}

ZaSearchField.prototype.getObject = 
function() {
	return this._containedObject;
}

/*
ZaSearchField.prototype._createHtml =
function(size, fieldId, buttonColId,filterAccountsId,filterAliasesId, filterDLId) {
	return "<table cellpadding='0' cellspacing='0' border='0' style='padding:2px;'>" +
		"<tr valign='middle'>" +
			"<td valign='middle' nowrap><input type='text' nowrap size='" + size + "' id='" + fieldId + "' class='Field'/></td>" + 
			"<td valign='middle' style='padding-left:2px;padding-right:2px;' id='" + buttonColId + "'></td>" +
			"<td valign='middle' nowrap>&nbsp;&nbsp;&nbsp;&nbsp;" + ZaMsg.Filter + ":&nbsp<input type='checkbox' id='" + filterAccountsId + "' class='Field'/> Accounts</td>" + 
			"<td valign='middle' nowrap><input type='checkbox' id='" + filterAliasesId + "' class='Field'/> Aliases</td>" + 			
			"<td valign='middle' nowrap><input type='checkbox' id='" + filterDLId + "' class='Field'/> Distribution Lists</td>" + 			
		"</tr>" + 
	"</table>";
}
*/
ZaSearchField.prototype.invokeCallback =
function() {
//	if (this._searchField.value.search(ZaSearchField.UNICODE_CHAR_		return;\
	var objList = new Array();
	if(this._containedObject[ZaSearch.A_fAccounts] == "TRUE") {
		objList.push(ZaSearch.ACCOUNTS);
	}
	if(this._containedObject[ZaSearch.A_fAliases] == "TRUE") {
		objList.push(ZaSearch.ALIASES);
	}
	if(this._containedObject[ZaSearch.A_fdistributionlists] == "TRUE") {
		objList.push(ZaSearch.DLS);
	}
	var  searchQueryHolder = new ZaSearchQuery(ZaSearch.getSearchByNameQuery(this._containedObject[ZaSearch.A_query]), objList, false, "");
	if (this._callbackFunc != null) {
		if (this._callbackObj != null)
			this._callbackFunc.call(this._callbackObj, this, searchQueryHolder);
		else 
			this._callbackFunc(this, searchQueryHolder);
	}
}

ZaSearchField.srchButtonHndlr = 
function(evt) {
	var fieldObj = this.getForm().parent;
	fieldObj.invokeCallback(evt);
}

ZaSearchField.prototype._getMyXForm = function() {	
	var xFormObject = {
		tableCssStyle:"width:100%;padding:2px;",numCols:10,
		items: [
			{type:_TEXTFIELD_, ref:ZaSearch.A_query, width:"250px",containerCssStyle:"padding-left:2px;padding-right:2px;", label:null, 
				elementChanged: function(elementValue,instanceValue, event) {
					var charCode = event.charCode;
					if (charCode == 13 || charCode == 3) {
					   this.getForm().parent.invokeCallback();
					} else {
						this.getForm().itemChanged(this, elementValue, event);
					}
				}
			},
			{type:_DWT_BUTTON_, label:ZaMsg.search, toolTipContent:ZaMsg.searchForAccounts, icon:ZaMsg.search, onActivate:ZaSearchField.srchButtonHndlr},
			{type:_OUTPUT_, value:ZaMsg.Filter+":", label:null},
			{type:_CHECKBOX_, ref:ZaSearch.A_fAccounts,label:ZaMsg.Filter_Accounts, labelLocation:_RIGHT_,trueValue:"TRUE", falseValue:"FALSE"},					
			{type:_CHECKBOX_, ref:ZaSearch.A_fAliases,label:ZaMsg.Filter_Aliases, labelLocation:_RIGHT_,trueValue:"TRUE", falseValue:"FALSE"},
			{type:_CHECKBOX_, ref:ZaSearch.A_fdistributionlists,label:ZaMsg.Filter_DLs, labelLocation:_RIGHT_,trueValue:"TRUE", falseValue:"FALSE"}
		]
	};
	return xFormObject;
};

/**
* @param xModelMetaData - XModel metadata that describes data model
* @param xFormMetaData - XForm metadata that describes the form
**/
ZaSearchField.prototype._initForm = 
function (xModelMetaData, xFormMetaData) {
	if(xModelMetaData == null || xFormMetaData == null)
		throw new AjxException("Metadata for XForm and/or XModel are not defined", AjxException.INVALID_PARAM, "DwtXWizardDialog.prototype._initForm");

	this._localXModel = new XModel(xModelMetaData);
	this._localXForm = new XForm(xFormMetaData, this._localXModel, null, this);
	this._localXForm.draw();
	this._drawn = true;
}