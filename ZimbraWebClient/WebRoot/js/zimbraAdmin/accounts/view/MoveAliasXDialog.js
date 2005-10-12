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

function MoveAliasXDialog(parent,  app, w, h) {
	if (arguments.length == 0) return;
	DwtDialog.call(this, parent, null, ZaMsg.MoveAlias_Title);
	this._app = app;
	this._localXForm = null;
	this._localXModel = null;
	this._drawn = false;
	this._containedObject = null;	

	if (!w) {
		this._contentW = "550px";
	} else {
		this._contentW = w;
	}
	
	if(!h) {
		this._contentH = "150px";
	} else {
		this._contentH = h;
	}		
	
	this._pageDiv = this.getDocument().createElement("div");
	this._pageDiv.className = "ZaXWizardDialogPageDiv";
	this._pageDiv.style.width = this._contentW;
	this._pageDiv.style.height = this._contentH;
	this._pageDiv.style.overflow = "auto";

	this._createContentHtml();
	this._containedObject = new ZaSearch();
	this.initForm(ZaSearch.myXModel,this.getMyXForm());
}

MoveAliasXDialog.prototype = new DwtDialog;
MoveAliasXDialog.prototype.constructor = MoveAliasXDialog;
MoveAliasXDialog.resultChoices = new XFormChoices([], XFormChoices.OBJECT_REFERENCE_LIST, null, "name");

/**
* public method _initForm
* @param xModelMetaData
* @param xFormMetaData
**/
MoveAliasXDialog.prototype.initForm = 
function (xModelMetaData, xFormMetaData) {
	if(xModelMetaData == null || xFormMetaData == null)
		throw new AjxException("Metadata for XForm and/or XModel are not defined", AjxException.INVALID_PARAM, "ZaXWizardDialog.prototype.initForm");
		
	this._localXModel = new XModel(xModelMetaData);
	this._localXForm = new XForm(xFormMetaData, this._localXModel, null, this);
	this._drawn = false;
	//this._localXForm.draw(this._pageDiv);
	this._localXForm.draw(this._pageDiv);	
	this._drawn = true;
}

MoveAliasXDialog.prototype.popup = 
function () {
	DwtDialog.prototype.popup.call(this);
	this._containedObject = new ZaSearch();
	var emptyAcc = new ZaAccount();
	emptyAcc.name = ZaMsg.MoveAlias_SelectTitle;
//	MoveAliasXDialog.resultChoices.setChoices([{id:null, name:ZaMsg.MoveAlias_SelectTitle}]);	
	MoveAliasXDialog.resultChoices.setChoices([emptyAcc]);	
	MoveAliasXDialog.resultChoices.dirtyChoices();
	this._localXForm.setInstance(this._containedObject);
	if(!this._drawn) {
		this._localXForm.draw(this._pageDiv)
		this._drawn = true;
	}
}

MoveAliasXDialog.prototype.getObject = 
function () {
	return this._containedObject;
}

MoveAliasXDialog.prototype.searchAccounts = 
function (ev) {
	try {
		var  searchQueryHolder = new ZaSearchQuery(ZaSearch.getSearchByNameQuery(this._containedObject[ZaSearch.A_query]), [ZaSearch.ACCOUNTS], false, "");
		var result = ZaSearch.searchByQueryHolder(searchQueryHolder, this._containedObject[ZaSearch.A_pagenum], ZaAccount.A_name, null, this._app);
//		var resultList = new Array();
		if(result.list) {
			MoveAliasXDialog.resultChoices.setChoices(result.list.getArray());
			MoveAliasXDialog.resultChoices.dirtyChoices();
		}
//		this._containedObject[ZaSearch.A_selected] = resultList;
		this._localXForm.refresh();

	} catch (ex) {
		// Only restart on error if we are not initialized and it isn't a parse error
		if (ex.code != ZmCsfeException.MAIL_QUERY_PARSE_ERROR) {
			this._handleException(ex, "ZaAccountListController.prototype.search", null, (this._inited) ? false : true);
		} else {
			this.popupErrorDialog(ZaMsg.queryParseError, ex);
			this._searchField.setEnabled(true);	
		}
	}
}

MoveAliasXDialog.srchButtonHndlr = 
function(evt) {
	var fieldObj = this.getForm().parent;
	fieldObj.searchAccounts(evt);
}

MoveAliasXDialog.prototype.getMyXForm = 
function() {	
	var xFormObject = {
		numCols:2,
		items:[
			{type:_TEXTFIELD_, ref:ZaSearch.A_query, width:"350px",containerCssStyle:"padding-left:2px;padding-right:2px;", label:null, 
				elementChanged: function(elementValue,instanceValue, event) {
					var charCode = event.charCode;
					if (charCode == 13 || charCode == 3) {
					   this.getForm().parent.searchAccounts();
					} else {
						this.getForm().itemChanged(this, elementValue, event);
					}
				}
			},
			{type:_DWT_BUTTON_, label:ZaMsg.search, toolTipContent:ZaMsg.searchForAccounts, icon:ZaMsg.search, onActivate:MoveAliasXDialog.srchButtonHndlr},
			{type:_OSELECT_,width:"350px", colSpan:2,ref:ZaSearch.A_selected, choices:MoveAliasXDialog.resultChoices, label:null,multiple:false}
		]		
	}
	return xFormObject;
}


MoveAliasXDialog.prototype._createContentHtml =
function () {

	this._table = this.getDocument().createElement("table");
	this._table.border = 0;
	this._table.width=this._contentW;
	this._table.cellPadding = 0;
	this._table.cellSpacing = 0;
	Dwt.associateElementWithObject(this._table, this);
	this._table.backgroundColor = DwtCssStyle.getProperty(this.parent.getHtmlElement(), "background-color");
	
	var row2; //page
	var col2;
	row2 = this._table.insertRow(0);
	row2.align = "left";
	row2.vAlign = "middle";
	
	col2 = row2.insertCell(row2.cells.length);
	col2.align = "left";
	col2.vAlign = "middle";
	col2.noWrap = true;	
	col2.width = this._contentW;
	col2.appendChild(this._pageDiv);

	this._contentDiv.appendChild(this._table);
}

/**
* Override _addChild method. We need internal control over layout of the children in this class.
* Child elements are added to this control in the _createHTML method.
* @param child
**/
MoveAliasXDialog.prototype._addChild =
function(child) {
	this._children.add(child);
}
