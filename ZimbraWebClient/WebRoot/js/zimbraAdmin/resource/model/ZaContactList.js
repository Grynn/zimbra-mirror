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
 * Portions created by Zimbra are Copyright (C) 2005 Zimbra, Inc.
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

ZaContactList.matchValue = "value"; //the property name of the match ZaContactList
ZaContactList.matchText = "text"; //the property name of the match text of ZaContactList

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
			dataInstance._list[i] = {
				name: arr[i].attrs[ZaAccount.A_displayname],
				phone: arr[i].attrs[ZaAccount.A_telephoneNumber],
				email: arr[i].name //arr[i].attrs[A_mail] can be an object containing alias.
			} ;
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
		if (this._list[i].name.indexOf (str) >= 0) {
			lists[j] = { 	contact: this._list[i], 
							text: this._list[i].name  + " <" + this._list[i].email + ">", 
							value: this._list[i].name 
						};
			j++ ;
		}		
	}

	return lists ;

}

ZaContactList.prototype._autocompleteCallback =
function(match, inputFieldXFormItem) {
	var xform = inputFieldXFormItem.getForm();
	var contact_email = xform.getItemsById(ZaResource.A_zimbraCalResContactEmail) [0];
	var contact_phone = xform.getItemsById(ZaResource.A_zimbraCalResContactPhone) [0];
	contact_email.setInstanceValue (match.contact.email);
	contact_phone.setInstanceValue (match.contact.phone);
	xform.refresh();
}; 

ZaContactList.prototype._getDataCallback = 
function(){
	return new ZaContactList() ;
};

ZaContactList.prototype.isUniqueValue =
function(str){
	
}




