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

function MoveAliasXDialog(parent,  app, alias) {
	if (arguments.length == 0) return;
	
	/*var moveButton = new DwtDialog_ButtonDescriptor(MoveAliasXDialog.MOVE_BUTTON, 
		ZaMsg._move, DwtDialog.ALIGN_RIGHT, new AjxCallback(this, this.moveAliasCallback));
	*/
	this._app = app;
	this._alias = null;		
//	ZaXWizardDialog.call(this, parent, null, ZaMsg.MoveAlias_Title, [DwtDialog.CANCEL_BUTTON],[moveButton] );
	ZaXWizardDialog.call(this, parent, null, ZaMsg.MoveAlias_Title, "500px", "300px");

	this._containedObject = new ZaSearch();

	this.initForm(ZaSearch.myXModel,this.getMyXForm());
}

MoveAliasXDialog.prototype = new ZaXWizardDialog;
MoveAliasXDialog.prototype.constructor = MoveAliasXDialog;
MoveAliasXDialog.resultChoices = new XFormChoices([], XFormChoices.OBJECT_REFERENCE_LIST, null, "name");


MoveAliasXDialog.prototype.popup = 
function (loc) {
	ZaXWizardDialog.prototype.popup.call(this, loc);
	this._containedObject[ZaModel.currentStep] = 1;	
	this._localXForm.setInstance(this._containedObject);				
	this._button[DwtWizardDialog.NEXT_BUTTON].setText(ZaMsg._move);
	this._button[DwtWizardDialog.FINISH_BUTTON].setText(AjxMsg._close);
	this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(false);		
	this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(true);	
}

MoveAliasXDialog.prototype.goPrev =
function () {
	this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(true);
	this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(false);	
	this.goPage(1);
}

MoveAliasXDialog.prototype.goNext =
function () {
	this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(false);
	this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(true);
	if(this.moveAlias())
		this.goPage(2);
}

MoveAliasXDialog.prototype.setAlias = 
function (alias) {
	this._alias=alias;
}

MoveAliasXDialog.prototype.moveAlias = 
function() {
	//remove alias
	var name;
	try {
		if(this._containedObject[ZaSearch.A_selected] && this._containedObject[ZaSearch.A_selected].addAlias!=null) {	
			try {
				name = this._alias.name;
				this._alias.remove();
			} catch (ex) {
				this._app.getCurrentController()._handleException(ex, "MoveAliasXDialog.prototype.moveAlias:_alias.remove", null, false);
				return false;
			}
			if(name) {
				this._containedObject[ZaSearch.A_selected].addAlias(name);
			} else {
				//throw	
			}
		}
	} catch (ex) {
		this._app.getCurrentController()._handleException(ex, "MoveAliasXDialog.prototype.moveAlias", null, false);
		return false;
	}
	return true;	
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
			this._app.getCurrentController()._handleException(ex, "ZaAccountListController.prototype.search", null, (this._inited) ? false : true);
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
			{type: _SWITCH_,
				items: [
					{type:_CASE_, relevant:"instance[ZaModel.currentStep] == 1", relevantBehaviorBehavior:_HIDE_,
						items: [
							{type:_OUTPUT_, value:ZaMsg.MoveAlias_SelectTitle},
							{type:_SPACER_},
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
					},
					{type:_CASE_, relevant:"instance[ZaModel.currentStep] == 2", relevantBehaviorBehavior:_HIDE_,
						items: [
							{type:_OUTPUT_, value:"Alias Moved"}
						]						
					}
				]
			}
		]		
	}
	return xFormObject;
}
