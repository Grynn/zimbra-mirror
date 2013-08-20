/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2007, 2008, 2009, 2010, 2011, 2013 Zimbra Software, LLC.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.4 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */

/**
* @class ZaEditAliasXDialog
* @contructor ZaEditAliasXDialog
* @author Greg Solovyev
* @param parent
* param app
**/
//ZaAilasXDialogHelpURL = location.pathname + ZaUtil.HELP_URL + "managing_accounts/creating_a_mail_aliases.htm?locid="+AjxEnv.DEFAULT_LOCALE;

ZaEditAliasXDialog = function(parent,   w, h, title) {
	if (arguments.length == 0) return;
	this._standardButtons = [DwtDialog.OK_BUTTON, DwtDialog.CANCEL_BUTTON];	
	ZaXDialog.call(this, parent, null, title, w, h, null, ZaId.DLG_EDIT_ALIAS);
	this._containedObject = {};
	this.initForm(ZaAlias.myXModel,this.getMyXForm());
	this._helpURL = ZaEditAliasXDialog.helpURL;
}

ZaEditAliasXDialog.prototype = new ZaXDialog;
ZaEditAliasXDialog.prototype.constructor = ZaEditAliasXDialog;
ZaEditAliasXDialog.helpURL = location.pathname + ZaUtil.HELP_URL + "managing_accounts/creating_a_mail_aliases.htm?locid="+AjxEnv.DEFAULT_LOCALE;



ZaEditAliasXDialog.prototype.getMyXForm = 
function() {	
	var xFormObject = {
		numCols:1,
		items:[
            {type:_GROUP_,isTabGroup:true, 
            	items: [ //allows tab key iteration
                	{ref:ZaAccount.A_name, type:_EMAILADDR_, label:null,visibilityChecks:[],enableDisableChecks:[]}
                ]
            }
        ]
	};
	return xFormObject;
}


/**
* @class ZaNewAliasXDialog
* @contructor ZaNewAliasXDialog
* @author Charles Cao
* @param parent
* param app
**/
ZaNewAliasXDialog = function(parent,   w, h, title) {
	if (arguments.length == 0) return;
	this._standardButtons = [DwtDialog.OK_BUTTON, DwtDialog.CANCEL_BUTTON];	
	ZaXDialog.call(this, parent, null, title, w, h,null,ZaId.DLG_NEW_ALIAS);
	this._containedObject = {};
	this.initForm(ZaAlias.myXModel,this.getMyXForm());
    this._helpURL = ZaNewAliasXDialog.helpURL;
}

ZaNewAliasXDialog.prototype = new ZaXDialog;
ZaNewAliasXDialog.prototype.constructor = ZaNewAliasXDialog;
ZaNewAliasXDialog.prototype.supportMinimize = true;
ZaNewAliasXDialog.helpURL = location.pathname + ZaUtil.HELP_URL + "managing_accounts/creating_a_mail_aliases.htm?locid="+AjxEnv.DEFAULT_LOCALE;
ZaNewAliasXDialog.prototype.getCacheName = function(){
      return "newAliasDialog";
}

ZaNewAliasXDialog.prototype.setObject =
function(entry) {
      this._containedObject = entry;
      this._containedObject._uuid = this._containedObject._uuid || ZaUtil.getItemUUid();
      this._localXForm.setInstance(this._containedObject);
}

ZaNewAliasXDialog.prototype.getMyXForm = 
function() {	
	var xFormObject = {
		numCols:1,
		items:[
          {type:_GROUP_,isTabGroup:true, items: [ //allows tab key iteration
                {ref:ZaAccount.A_name, type:_EMAILADDR_, label:ZaMsg.Alias_Dlg_label_alias,visibilityChecks:[],enableDisableChecks:[]},
                {type:_DYNSELECT_, ref:ZaAlias.A_targetAccount, dataFetcherClass:ZaSearch,
                    dataFetcherMethod:ZaSearch.prototype.dynSelectSearch,
                    dataFetcherTypes:[ZaSearch.ACCOUNTS, ZaSearch.RESOURCES, ZaSearch.DLS],
                    dataFetcherAttrs:[ZaItem.A_zimbraId, ZaItem.A_cn, ZaAccount.A_name, ZaAccount.A_displayname, ZaAccount.A_mail],
                    label:ZaMsg.Alias_Dlg_label_target_acct,labelLocation:_LEFT_,
                    width:"100%", inputWidth:"100%", editable:true, forceUpdate:true,
                    choices:new XFormChoices([], XFormChoices.OBJECT_REFERENCE_LIST, "name", "name"),
                    visibilityChecks:[],enableDisableChecks:[],
                    onChange: function(value, event, form){
                        if (value instanceof ZaItem ) {
                            this.setInstanceValue(value.name);
                        } else {
                            this.setInstanceValue(value);
                        }
                    }
                }
            ]
          }
        ]
	};
	return xFormObject;
}
