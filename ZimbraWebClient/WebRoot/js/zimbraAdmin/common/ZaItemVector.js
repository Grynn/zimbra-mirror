/**
* @constructor
* @class ZaItemVector
* This is a Vector that contains ZaItems. Unlike in AjxVector @link AjxVector.contains and
@link AjxVector.remove methods compare object ids (@link ZaItem.id) instead of comparing the whole objects.
@link AjxVector.add method is overwriten to accept only instances of ZaItem class.
**/

function ZaItemVector() {
	AjxVector.call(this, null);
}

ZaItemVector.prototype = new AjxVector;
ZaItemVector.prototype.constructor = ZaItemVector;

ZaItemVector.prototype.contains = 
function(obj) {
	if(! (obj instanceof ZaItem) ) {
		throw new DwtException("Invalid parameter", DwtException.INTERNAL_ERROR, "ZaItemVector.prototype.add", "ZaItemVector can contain only objects of ZaItem class and classes that extend ZaItem.");
	}
	for (var i = 0; i < this._array.length; i++) {
		if (this._array[i].id == obj.id)
			return true;
	}
	return false;
}

ZaItemVector.prototype.remove = 
function(obj) {
	if(! (obj instanceof ZaItem) ) {
		throw new DwtException("Invalid parameter", DwtException.INTERNAL_ERROR, "ZaItemVector.prototype.add", "ZaItemVector can contain only objects of ZaItem class and classes that extend ZaItem.");
	}
	for (var i = 0; i < this._array.length; i++) {
		if (this._array[i].id == obj.id) {
			this._array.splice(i,1);
			return true;
		}
	}
	return false;
}


ZaItemVector.prototype.add =
function(obj, index) {
	// if index is out of bounds, 
	if(! (obj instanceof ZaItem) ) {
		throw new DwtException("Invalid parameter", DwtException.INTERNAL_ERROR, "ZaItemVector.prototype.add", "ZaItemVector can contain only objects of ZaItem class and classes that extend ZaItem.");
	}
	if (index == null || index < 0 || index >= this._array.length) {
		// append object to the end
		this._array.push(obj);
	} else {
		// otherwise, insert object
		this._array.splice(index, 0, obj);
	}
}