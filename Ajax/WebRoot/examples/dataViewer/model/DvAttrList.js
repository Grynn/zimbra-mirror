/**
* @class
* This class represents a list of attributes.
* @author Conrad Damon
*/
function DvAttrList() {

	DvList.call(this, true);

	this._vector = new LsVector();
	this._idHash = new Object();
	this._nameHash = new Object();
	this._evt = new DvEvent();
}

DvAttrList.prototype = new DvList;
DvAttrList.prototype.constructor = DvAttrList;

DvAttrList.prototype.toString = 
function() {
	return "DvAttrList";
}

/**
* Converts a list of attributes (each of which is a list of properties) into a DvAttrList.
*
* @param attrs		list of attributes
*/
DvAttrList.prototype.load =
function(attrs) {
	for (var id in attrs) {
		var props = attrs[id];
		var attr = new DvAttr(id, props[0], props[1], props[2], props[3]);
		this.add(attr);
		this._nameHash[attr.name] = attr;
	}
}

DvAttrList.prototype.getByName =
function(name) {
	return this._nameHash[name];
}
