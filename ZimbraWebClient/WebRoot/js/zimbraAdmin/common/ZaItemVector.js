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