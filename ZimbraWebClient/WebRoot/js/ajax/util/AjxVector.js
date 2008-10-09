/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2005, 2006, 2007 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Yahoo! Public License
 * Version 1.0 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * 
 * ***** END LICENSE BLOCK *****
 */

// AjxVector class

AjxVector = function() {
	this._array = new Array();
};

AjxVector.prototype.toString =
function(sep, compress) {
	if (compress !== true)
		return this._array.join(sep);

	var a = new Array();
	for (var i = 0; i < this._array.length; i++) {
		var x = this._array[i];
		if  (x != undefined && x != null && x != "")
			a.push(x);
	}
	return a.join(sep);
};

AjxVector.fromArray =
function(list) {
	var vec = new AjxVector();
	vec._array.length = 0;
	if (list instanceof Array) {
		vec._array = list;
	}
	return vec;
};

AjxVector.prototype.size =
function() {
	return this._array.length;
};

AjxVector.prototype.add =
function(obj, index, noDuplicates) {
	// if no duplicates, search for the obj in list and return if found.
	if (noDuplicates && this.contains(obj)) {
		return;
	}

	// if index is out of bounds,
	if (index == null || index < 0 || index >= this._array.length) {
		// append object to the end
		this._array.push(obj);
	} else {
		// otherwise, insert object
		this._array.splice(index, 0, obj);
	}
};

AjxVector.prototype.addList =
function(list) {
	if (!list) return;

	if (list.length) // array
		this._array = this._array.concat(list);
	else if (list.size && list.size()) // AjxVector
		this._array = this._array.concat(list._array);
};

AjxVector.prototype.remove =
function(obj) {
	for (var i = 0; i < this._array.length; i++) {
		if (this._array[i] == obj) {
			this._array.splice(i,1);
			return true;
		}
	}
	return false;
};

AjxVector.prototype.removeAt =
function(index) {
	if (index >= this._array.length || index < 0)
		return null;

	var delArr = this._array.splice(index,1);
	var ret = null;
	if (delArr) {
		ret = delArr[0];
	}
	return ret;
};

AjxVector.prototype.removeAll =
function() {
	// Actually blow away the array items so that garbage
	// collection can take place (XXX: does this really force GC?)
	for (var i = 0; i < this._array.length; i++)
		this._array[i] = null;
	this._array.length = 0;
};

AjxVector.prototype.removeLast =
function() {
	return this._array.length > 0 ? this._array.pop() : null;
};

AjxVector.prototype.reverse =
function() {
	this._array.reverse();
};

AjxVector.prototype.replace =
function(index, newObj) {
	var oldObj = this._array[index];
	this._array[index] = newObj;
	return oldObj;
};

AjxVector.prototype.replaceObject =
function(obj, newObj) {
	for (var i = 0; i < this._array.length; i++) {
		if (this._array[i] == obj) {
			this._array[i] = newObj;
			return obj;
		}
	}
	return null;
};

/**
* Returns the index of the obj given w/in vector
*
* @param obj			the object being looked for
*/
AjxVector.prototype.indexOf =
function(obj) {
	if (obj == null)
		return -1;

	for (var i = 0; i < this._array.length; i++) {
		if (this._array[i] == obj)
			return i;
	}
	return -1;
};

/**
* Returns the index of the obj given w/in vector
*
* @param obj			the object being looked for
* @param compareFunc	a function for comparing objects
*/
AjxVector.prototype.indexOfLike =
function(obj, compareFunc) {
	var value = compareFunc.call(obj);

	for (var i = 0; i < this._array.length; i++) {
		var test = compareFunc.call(this._array[i]);
		if (test == value)
			return i;
	}
	return -1;
};

AjxVector.prototype.clone =
function() {
	var vec = new AjxVector();
	vec.addList(this);
	return vec;
};

AjxVector.prototype.contains =
function(obj) {
	for (var i = 0; i < this._array.length; i++) {
		if (this._array[i] == obj)
			return true;
	}
	return false;
};


/**
* Returns true if the vector contains the given object, using the given
* function to compare objects. The comparison function should return a
* type for which the equality test (==) is meaningful, such as a string
* or a base type.
*
* @param obj			the object being looked for
* @param compareFunc	a function for comparing objects
*/
AjxVector.prototype.containsLike =
function(obj, compareFunc) {
	var value = compareFunc.call(obj);
	for (var i = 0; i < this._array.length; i++) {
		var test = compareFunc.call(this._array[i]);
		if (test == value)
			return true;
	}
	return false;
};

AjxVector.prototype.get =
function(index) {
	return index >= this._array.length || index < 0
		? null : this._array[index];
};

AjxVector.prototype.getArray =
function() {
	return this._array;
};

AjxVector.prototype.getLast =
function() {
	return this._array.length == 0
		? null : this._array[this._array.length-1];
};

AjxVector.prototype.getNext =
function(obj) {
	var idx = this.indexOf(obj);
	if (idx == -1)
		return null;
	return this.get(++idx);
};

AjxVector.prototype.getPrev =
function(obj) {
	var idx = this.indexOf(obj);
	if (idx == -1)
		return null;
	return this.get(--idx);
};



AjxVector.prototype.sort =
function(sortFunc) {
	if (!sortFunc) {
		sortFunc = AjxVector._defaultArrayComparator;
	}
	this._array.sort(sortFunc);
};

AjxVector.prototype.binarySearch =
function(valueToFind, sortFunc) {
	if (!sortFunc) {
		sortFunc = AjxVector._defaultArrayComparator;
	}

	var l = 0;
	var arr = this._array;
	var u = arr.length - 1;

	while(true) {
		if (u < l) {
			return -1;
		}

		var i = Math.floor((l + u)/ 2);
		var comparisonResult = sortFunc(valueToFind, arr[i]);

		if (comparisonResult < 0) {
			u = i - 1;
		} else if (comparisonResult > 0) {
			l = i + 1;
		} else {
			return i;
		}
	}
};

AjxVector.prototype.merge =
function(offset, list) {

	if (offset < 0)
		return;

	var rawList = list instanceof AjxVector ? list.getArray() : list;

	var limit = this._array.length < (offset+rawList.length)
		? this._array.length
		: offset+rawList.length;

	if (offset < this._array.length) {
		// replace any overlapping items in vector
		var count = 0;
		for (var i=offset; i<limit; i++)
			this._array[i] = rawList[count++];

		// and append the rest
		if (count < rawList.length)
			this._array = this._array.concat(rawList.slice(count));
	} else {
		// otherwise, just append the raw list to the end
		this._array = this._array.concat(rawList);
	}
};


// Static methods

AjxVector._defaultArrayComparator =
function(a, b) {
	return a < b ? -1 : (a > b ? 1 : 0);
};

// Apply function f for each element of the array.  Optionally call it
// in the context of obj object.  If "f" is a string, then for each
// non-null array element call its "f" member function.
AjxVector.prototype.foreach = function(f, obj) {
	var l = this.size(), i = 0, el;
	if (typeof f == "function") {
		while (--l >= 0)
			f.call(obj, this.get(i), i++);
	} else {
		while (--l >= 0) {
			el = this.get(i++);
			if (el != null)
				el[f].call(el); // assuming function
		}
	}
};

// Return a new AjxVector which contains the results of calling f
// (optionally in the context obj) for each element of this array.
//
// If "f" is a string, then for each element el:
//
// - if el[f] is a function, call el[f] and push the result in the
//   returned array.
//
// - otherwise push el[f]
AjxVector.prototype.map = function(f, obj) {
	var a = [], i = this.size(), el;
	if (typeof f == "function") {
		while (--i >= 0)
			a[i] = f.call(obj, this.get(i), i);
	} else if (f instanceof AjxCallback) {
		while (--i >= 0)
			a[i] = f.run(this.get(i), i);
	} else {
		while (--i >= 0) {
			el = this.get(i);
			if (el != null) {
				if (typeof el[f] == "function")
					a.unshift(el[f].call(el));
				else
					a.unshift(el[f]);
			}
		}
	}
	return AjxVector.fromArray(a);
};

AjxVector.prototype.join = function(sep) {
	return this._array.join(sep);
};

// Return a new AjxVector containing the elements from this vector
// except those for which f(el) returns true.  Otherwise said,
// "SUBtracts" from this vector those elements for which f(el) returns
// true.
//
// f is called in the obj context.
AjxVector.prototype.sub = function(f, obj) {
	var a = [], l = this.size(), i = 0, el;
	while (--l >= 0) {
		el = this.get(i++);
		if (!f.call(obj, el, i))
			a.push(el);
	}
	return AjxVector.fromArray(a);
};
