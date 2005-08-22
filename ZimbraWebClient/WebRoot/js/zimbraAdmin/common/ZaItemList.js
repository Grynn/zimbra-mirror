/*
***** BEGIN LICENSE BLOCK *****
Version: ZPL 1.1

The contents of this file are subject to the Zimbra Public License Version 1.1 ("License");
You may not use this file except in compliance with the License. You may obtain a copy of
the License at http://www.zimbra.com/license

Software distributed under the License is distributed on an "AS IS" basis, WITHOUT WARRANTY
OF ANY KIND, either express or implied. See the License for the specific language governing
rights and limitations under the License.

The Original Code is: Zimbra Collaboration Suite.

The Initial Developer of the Original Code is Zimbra, Inc.
Portions created by Zimbra are Copyright (C) 2005 Zimbra, Inc.
All Rights Reserved.
Contributor(s): ______________________________________.

***** END LICENSE BLOCK *****
*/

function ZaItemList(type, constructor, app) {

	if (arguments.length == 0) return;
	ZaModel.call(this, true);

	this.type = type;
	this._constructor = constructor;
	this._app = app;
	
	this._vector = new ZaItemVector();
	this._idHash = new Object();
}

ZaItemList.prototype = new ZaModel;
ZaItemList.prototype.constructor = ZaItemList;

ZaItemList.prototype.toString = 
function() {
	return "ZaItemList "+this.type;
}

/**
* Adds an item to the list.
*
* @param item	the item to add
* @param index	the index at which to add the item (defaults to end of list)
*/
ZaItemList.prototype.add = 
function(item, index) {
	this._vector.add(item, index);
	if (item.id) {
		this._idHash[item.id] = item;
		var items = new Object();
		items[0] = item;
	}
}

/**
* Removes an item from the list.
*
* @param item	the item to remove
*/
ZaItemList.prototype.remove = 
function(item) {
	this._vector.remove(item);
	if (item.id)
		delete this._idHash[item.id];
}

/**
* Returns the number of items in the list.
*/
ZaItemList.prototype.size = 
function() {
	this._vector.size();
}

/**
* Returns the list as an array.
*/
ZaItemList.prototype.getArray =
function() {
	return this._vector.getArray();
}

/**
* Returns the list as a ZaItemVector.
*/
ZaItemList.prototype.getVector =
function() {
	return this._vector;
}

/**
* Returns the hash matching IDs to items.
*/
ZaItemList.prototype.getIdHash =
function() {
	return this._idHash;
}

/**
* Returns the item with the given ID.
*
* @param id		an item ID
*/
ZaItemList.prototype.getItemById =
function(id) {
	return this._idHash[id];
}

/**
* Clears the list, including its ID hash.
*/
ZaItemList.prototype.clear =
function() {
	this._vector.removeAll();
	for (var id in this._idHash)
		this._idHash[id] = null;
	this._idHash = new Object();
}
/*
Sorting is done on the server
ZaItemList.prototype.sortByName =
function(descending) {
	if (descending)
		this._vector.getArray().sort(ZaItem.compareNamesDesc);
	else 
		this._vector.getArray().sort(ZaItem.compareNamesAsc);	
}*/

/**
* Populates the list with elements created from the response to a SOAP command. Each
* node in the response should represent an item of the list's type.
*
* @param respNode	an XML node whose children are item nodes
*/
ZaItemList.prototype.loadFromDom = 
function(respNode) {
	this.clear();
	var nodes = respNode.childNodes;
	for (var i = 0; i < nodes.length; i++) {
		var item = new this._constructor(this._app);
		
		item.initFromDom(nodes[i]);
		//add the list as change listener to the item
		this.add(item);
	}
}

// Grab the IDs out of a list of items, and return them as both a string and a hash.
ZaItemList.prototype._getIds =
function(list) {
	var idHash = new Object();
	if (!(list && list.length))
		return idHash;
	var ids = new Array();
	for (var i = 0; i < list.length; i++) {
		var id = list[i].id;
		if (id) {
			ids.push(id);
			idHash[id] = list[i];
		}
	}
	idHash.string = ids.join(",");
	return idHash;
}
