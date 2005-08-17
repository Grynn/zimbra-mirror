/**
* @class
* This class represents a list of items.
* @author Conrad Damon
*/
function DvItemList(attrList) {
	DvList.call(this, true);
	
	this._attrList = attrList;
}

DvItemList.prototype = new DvList;
DvItemList.prototype.constructor = DvItemList;

DvItemList.prototype.toString = 
function() {
	return "DvItemList";
}

/**
* Adds the given item to this list, and notifies listeners.
*
* @param item		an item
*/
DvItemList.prototype.add = 
function(item) {
	DvList.prototype.add.call(this, item);
	this._eventNotify(DvEvent.E_CREATE, item);
}

/**
* Removes the given items from this list, and notifies listeners.
*
* @param list		a list of items to remove
*/
DvItemList.prototype.remove = 
function(list) {
	var deleted = new Array();
	for (var i = 0; i < list.length; i++)
		if (DvList.prototype.remove.call(this, item))
			deleted.push(item);

	this._eventNotify(DvEvent.E_DELETE, deleted);
}

/**
* Updates the given list of items with new values.
*
* @param list		a list of items to update
* @param hash		attribute ID/value pairs to change
*/
DvItemList.prototype.update = 
function(list, hash) {
	var modified = new Array();
	var changes = new Object();
	for (var i = 0; i < list.length; i++) {
		var item = list[i];
		for (var attr in hash) {
			var id = this._attrList.getByName(attr).id;
			item.setValue(id, hash[attr]);
			changes[id] = hash[attr];
		}
		modified.push(item);
	}
	this._eventNotify(DvEvent.E_MODIFY, modified, {changes: changes});
}

/**
* Populates this list from the given set of item values.
*
* @param attrList		complete list of attributes
* @param items			list of items (each is a list of values)
*/
DvItemList.prototype.load = 
function(attrList, items) {
	for (var i = 0; i < items.length; i++)
		this.add(new DvItem(i + 1, attrList, items[i]));
}

/**
* Returns the items in this list that have an attribute with the given name that has the
* given value.
*
* @param attr		an attribute name
* @param value		a value
*/
DvItemList.prototype.getByKey =
function(attr, value) {
	attr = this._attrList.getByName(attr).id;
	var list = new Array();
	var a = this.getArray();
	for (var i = 0; i < a.length; i++) {
		var item = a[i];
		if (item.getValue(attr) == value)
			list.push(item);
	}
	return list;
}
