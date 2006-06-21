/*
 * ***** BEGIN LICENSE BLOCK *****
 * Version: ZPL 1.2
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.2 ("License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.zimbra.com/license
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
 * the License for the specific language governing rights and limitations
 * under the License.
 * 
 * The Original Code is: Zimbra Collaboration Suite Web Client
 * 
 * The Initial Developer of the Original Code is Zimbra, Inc.
 * Portions created by Zimbra are Copyright (C) 2006 Zimbra, Inc.
 * All Rights Reserved.
 * 
 * Contributor(s):
 * 
 * ***** END LICENSE BLOCK *****
 */
 
 /**
  * This is the list containing the auto complete match contact lists
  * @author Charles Cao
  */
function ZaContactList () {
	this._list = [];	//this is the array holds all the match objects
	this._matchStr = ""; 	//this is the string used to match the list.
}

ZaContactList.matchValue = ZaAccount.A_name; //the property name of the match ZaContactList
ZaContactList.matchText =  ZaAccount.A_displayname; //the property name of the match text of ZaContactList

ZaContactList.prototype.getContactList =
function (str){
	var dataInstance = new ZaContactList();
	try {
		var params = {};
		
		params.attrs = [ZaAccount.A_displayname, ZaAccount.A_mail, ZaAccount.A_telephoneNumber].join();
		params.types = ZaSearch.ACCOUNTS ;
		params.sortBy = ZaAccount.A_displayname;
		params.query = ZaSearch.getSearchByDisplayNameQuery(str) ;
		params.applyCos = "0";
		var searchResult = ZaSearch.searchDirectory(params).Body.SearchDirectoryResponse ;
		var list = new ZaItemList (ZaAccount, null);
		list.loadFromJS(searchResult) ;
		var arr = list.getArray();
	
		for (var i=0; i<arr.length; i++) {
			dataInstance._list[i] = {};
			dataInstance._list[i][ZaAccount.A_displayname] = arr[i].attrs[ZaAccount.A_displayname]; 
			dataInstance._list[i][ZaAccount.A_name ] = arr[i][ZaAccount.A_name]; 			
			dataInstance._list[i][ZaAccount.A_telephoneNumber ] = arr[i].attrs[ZaAccount.A_telephoneNumber]; 						
		} 
		dataInstance._matchStr = str ;
	}catch (e){
		DBG.println(AjxDebug.DBG1, "ZaContactList.prototype.getContactList: "+ e.message);
	}
	return dataInstance; //
}

ZaContactList.prototype.autocompleteMatch = 
function (str) {
	var lists = new Array () ;
	var j = 0;
	for (var i=0; i < this._list.length; i++ ) {

	//	if (this._list[i].name.indexOf (str) >= 0) {
			lists[j] = { 	contact: this._list[i], 
							text: this._list[i].name  + " <" + this._list[i].email + ">", 
							value: this._list[i].name 
						};
			j++ ;
	//		}		
	}

	return lists ;

}

ZaContactList.prototype._autocompleteCallback =
function(match, inputFieldXFormItem) {
	var xform = inputFieldXFormItem.getForm();
	var contact_email = xform.getItemsById(ZaResource.A_zimbraCalResContactEmail) [0];
	var contact_phone = xform.getItemsById(ZaResource.A_zimbraCalResContactPhone) [0];
	contact_email.setInstanceValue (match[ZaAccount.A_name]);
	contact_phone.setInstanceValue (match[ZaAccount.A_telephoneNumber]);
	xform.refresh();
}; 

ZaContactList.prototype._getDataCallback = 
function(){
	return new ZaContactList() ;
};

ZaContactList.prototype.isUniqueValue =
function(str){
	
}

ZaContactList.prototype.getList = function () {
	return this._list;
}




