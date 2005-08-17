/**
* @class
* This class represents a list of items.
*
* @author Conrad Damon
* @param init		true if we're instantiating an object (as opposed to performing inheritance)
*/
function DvList(init) {

	if (arguments.length == 0) return;
	DvModel.call(this, true);

	this._vector = new LsVector();
	this._idHash = new Object();
	this._evt = new DvEvent();
}

DvList.prototype = new DvModel;
DvList.prototype.constructor = DvList;

DvList.prototype.toString = 
function() {
	return "DvList";
}

/**
* Adds an item to the list.
*
* @param item	the item to add
* @param index	the index at which to add the item (defaults to end of list)
*/
DvList.prototype.add = 
function(item, index) {
	this._vector.add(item, index);
	if (item.id)
		this._idHash[item.id] = item;
}

/**
* Removes an item from the list.
*
* @param item	the item to remove
*/
DvList.prototype.remove = 
function(item) {
	this._vector.remove(item);
	if (item.id)
		delete this._idHash[item.id];
}

/**
* Returns the number of items in the list.
*/
DvList.prototype.size = 
function() {
	return this._vector.size();
}

/**
* Returns the list as an array.
*/
DvList.prototype.getArray =
function() {
	return this._vector.getArray();
}

/**
* Returns the list as a LsVector.
*/
DvList.prototype.getVector =
function() {
	return this._vector;
}

/**
* Returns the item with the given ID.
*
* @param id		a item ID
*/
DvList.prototype.getById =
function(id) {
	return this._idHash[id];
}

/**
* Clears the list, including its ID hash.
*/
DvList.prototype.clear =
function() {
	// First, let each item run its clear() method
	var a = this.getArray();
	for (var i = 0; i < a.length; i++)
		a[i].clear();

	this._evtMgr.removeAll(DvEvent.L_MODIFY);
	this._vector.removeAll();
	for (var id in this._idHash)
		this._idHash[id] = null;
	this._idHash = new Object();
}

/**
* Returns a vector containing a subset of items of this list
*
* @param offset		index of first item to select
* @param limit		number of items to select
*/
DvList.prototype.getSubList = 
function(offset, limit) {
	var subVector = null;
	var end = (offset + limit > this.size()) ? this.size() : offset + limit;
	var subList = this.getArray();
	if (offset < end)
		subVector = LsVector.fromArray(subList.slice(offset, end));
	return subVector;
}

// Private/protected methods

// Notify listeners on this list of a model change.
DvList.prototype._eventNotify =
function(event, items, details) {
	if (items.length && this._evtMgr.isListenerRegistered(DvEvent.L_MODIFY)) {
		this._evt.set(event, this);
		this._evt.setDetails(details);
		this._evt.setDetail("items", items);
		this._evtMgr.notifyListeners(DvEvent.L_MODIFY, this._evt);
	}
}

// Returns the index at which the given item should be inserted into this list.
// Subclasses should override to return a meaningful value.
DvList.prototype._sortIndex = 
function(item) {
	return 0;
}
