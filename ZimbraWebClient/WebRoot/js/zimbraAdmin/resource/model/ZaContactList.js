/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2006, 2007, 2008, 2009, 2010 VMware, Inc.
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
  * This is the list containing the auto complete match contact lists
  * @author Charles Cao
  */
ZaContactList = function() {
	this._list = [];	//this is the array holds all the match objects

}

ZaContactList.matchValue = ZaAccount.A_displayname; //the property name of the match ZaContactList
ZaContactList.matchText =  "matchListFieldText"; //the property name of the match text of ZaContactList

ZaContactList.prototype.getContactList =
function (str, callback){
	try {
		var params = {};
		var busyId = Dwt.getNextId ();
		params.attrs = [ZaAccount.A_displayname, ZaAccount.A_mail, ZaAccount.A_telephoneNumber].join();
		params.types = ZaSearch.ACCOUNTS ;
		params.sortBy = ZaAccount.A_displayname;
		params.query = ZaSearch.getSearchByDisplayNameQuery(str) ;
		params.applyCos = "0";
		myCallback = new AjxCallback(this, this.getDataCallback, {callback:callback,busyId:busyId});
		params.callback = myCallback;
		params.controller = ZaApp.getInstance().getCurrentController () ;
		params.showBusy = true;
		params.busyMsg = ZaMsg.BUSY_SEARCHING;
		params.skipCallbackIfCancelled = false; 
		params.busyId = busyId;		
		ZaSearch.searchDirectory(params);
	}	catch (ex){
		ZaApp.getInstance().getCurrentController()._handleException(ex, "ZaContactList.prototype.getContactList");	
	}
}

/*
ZaContactList.prototype.autocompleteMatch = 
function (str) {
	var lists = new Array () ;
	var j = 0;
	for (var i=0; i < this._list.length; i++ ) {
		lists[j] = { 	contact: this._list[i], 
						text: this._list[i].name  + " <" + this._list[i].email + ">", 
						value: this._list[i].name 
					};
		j++ ;
	}

	return lists ;

} */

ZaContactList.prototype._autocompleteCallback =
function(match, inputFieldXFormItem) {
	var xform = inputFieldXFormItem.getForm();
	var contact_email = xform.getItemsById(ZaResource.A_zimbraCalResContactEmail) [0];
	var contact_phone = xform.getItemsById(ZaResource.A_zimbraCalResContactPhone) [0];
	contact_email.setInstanceValue (match["contact"][ZaAccount.A_name]);
	contact_phone.setInstanceValue (match["contact"][ZaAccount.A_telephoneNumber]);
	xform.refresh();
}; 

ZaContactList.prototype.getDataCallback = 
function(params, resp){
	try {
		var callback = params.callback;
		if(params.busyId)
			ZaApp.getInstance().getAppCtxt().getShell().setBusy(false, params.busyId);
			
		if(!resp) {
			throw(new AjxException(ZaMsg.ERROR_EMPTY_RESPONSE_ARG, AjxException.UNKNOWN, "ZaContactList.prototype.getDataCallback"));
		}
		if(resp.isException()) {
			//throw(resp.getException());
			ZaSearch.handleTooManyResultsException(resp.getException(), "ZaContactList.prototype.getDataCallback");
		} else {
			ZaSearch.TOO_MANY_RESULTS_FLAG = false ;
			var response = resp.getResponse().Body.SearchDirectoryResponse;
			var list = new ZaItemList();	
			list.loadFromJS(response);
			var arr = list.getArray();
			var data = [];
			for (var i=0; i<arr.length; i++) {
				data[i] = { contact: {}};
				data[i]["contact"][ZaAccount.A_displayname] = arr[i].attrs[ZaAccount.A_displayname]; 
				data[i]["contact"][ZaAccount.A_name ] = arr[i][ZaAccount.A_name]; 			
				data[i]["contact"][ZaAccount.A_telephoneNumber ] = arr[i].attrs[ZaAccount.A_telephoneNumber]; 						
				data[i][ZaContactList.matchText] = data[i]["contact"][ZaAccount.A_displayname] + "< " + data[i]["contact"][ZaAccount.A_name ] + ">";
				data[i][ZaContactList.matchValue] = data[i]["contact"][ZaAccount.A_displayname] ;
 ;
			} 
			/**
			 * data is an array contains all the matching items.
			 * Each matching item has the following attributes:
			 * 1) objectReference name: here it is called "contact" which represents a contact object
			 * 2) matchTextReference: (required) Here it is called ZaContactList.matchText. 
			 * 							Its value is used to be displayed in the match list item
			 * 3) matchValueReference: (required) Here it is called ZaContactList.matchValue. 
			 * 							Its value is used to do the comparison
			 */
			callback.run(data);			
		}
	} catch (ex) {
		ZaApp.getInstance().getCurrentController()._handleException(ex, "ZaContactList.prototype.getDataCallback");	
	}		
	
};

ZaContactList.prototype.isUniqueValue =
function(str){
	
}

ZaContactList.prototype.getList = function () {
	return this._list;
}




