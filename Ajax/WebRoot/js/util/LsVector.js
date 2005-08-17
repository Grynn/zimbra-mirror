// LsVector class

function LsVector() {
	this._array = new Array();
}

LsVector.prototype.toString =
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
}

LsVector.fromArray =
function(list) {
	var vec = new LsVector();
	vec._array.length = 0;
	if (list instanceof Array) {
		vec._array = list;
	}
	return vec;
}

LsVector.prototype.clone =
function (){
	var vec = new LsVector();
	var list = this._array;
	for (var i = 0; i < list.length; ++i){
		vec._array[i] = list[i];
	}
	return vec;
};

LsVector.prototype.add =
function(obj, index) {
	// if index is out of bounds, 
	if (index == null || index < 0 || index >= this._array.length) {
		// append object to the end
		this._array.push(obj);
	} else {
		// otherwise, insert object
		this._array.splice(index, 0, obj);
	}
}

LsVector.prototype.replace =
function (index, newObj) {
	var oldObj = this._array[index];
	this._array[index] = newObj;
	return oldObj;
};

LsVector.prototype.addList =
function(list) {

	if (list instanceof Array)
		this._array = this._array.concat(list);
	else if (list instanceof LsVector)
		this._array = this._array.concat(list._array);
}

LsVector.prototype.contains = 
function(obj) {
	for (var i = 0; i < this._array.length; i++) {
		if (this._array[i] == obj)
			return true;
	}
	return false;
}

/**
* Returns the index of the obj given w/in vector
*
* @param obj			the object being looked for
*/
LsVector.prototype.indexOf = 
function(obj) {
	for (var i = 0; i < this._array.length; i++) {
		if (this._array[i] == obj)
			return i;
	}
	return -1;
}

/**
* Returns true if the vector contains the given object, using the given 
* function to compare objects. The comparison function should return a 
* type for which the equality test (==) is meaningful, such as a string 
* or a base type.
*
* @param obj			the object being looked for
* @param compareFunc	a function for comparing objects
*/
LsVector.prototype.containsLike = 
function(obj, compareFunc) {
	var value = compareFunc.call(obj);
	for (var i = 0; i < this._array.length; i++) {
		var test = compareFunc.call(this._array[i]);
		if (test == value)
			return true;
	}
	return false;
}

LsVector.prototype.get =
function(index) {
	return index >= this._array.length || index < 0
		? null : this._array[index];
}

LsVector.prototype.getArray =
function() {
	return this._array;
}

LsVector.prototype.getLast =
function() {
	return this._array.length == 0
		? null : this._array[this._array.length-1];
}

LsVector.prototype.remove = 
function(obj) {
	for (var i = 0; i < this._array.length; i++) {
		if (this._array[i] == obj) {
			this._array.splice(i,1);
			return true;
		}
	}
	return false;
}

LsVector.prototype.removeAt =
function(index) {
	if (index >= this._array.length || index < 0)
		return null;
	
	var delArr = this._array.splice(index,1);
	var ret = null;
	if (delArr) {
		ret = delArr[0];
	}
	return ret;
}

LsVector.prototype.removeAll = 
function() {
	// Actually blow away the array items so that garbage
	// collection can take place (XXX: does this really force GC?)
	for (var i = 0; i < this._array.length; i++)
		this._array[i] = null;
	this._array.length = 0;
}

LsVector.prototype.removeLast = 
function() {
	return this._array.length > 0 ? this._array.pop() : null;
}

LsVector.prototype.size =
function() {
	return this._array.length;
}

LsVector._defaultArrayComparator = function(a, b){
	return (a < b)? -1 :((a > b)? 1 : 0);
};

LsVector.prototype.sort =
function(sortFunc) {
	if (!sortFunc) {
		sortFunc = LsVector._defaultArrayComparator;
	}
	this._array.sort(sortFunc);
}

LsVector.prototype.binarySearch = function (valueToFind, sortFunc) {
	if(!sortFunc){
		sortFunc = LsVector._defaultArrayComparator;
	}
	
	var l = 0;
	var arr = this._array;
	var u = arr.length - 1;
	while(true){
		if(u < l){
			return -1;
		}
		
		var i = Math.floor((l + u)/ 2);
		var comparisonResult = sortFunc(valueToFind, arr[i]);
		if(comparisonResult < 0){
			u = i - 1;
		} else if (comparisonResult > 0){
			l = i + 1;
		} else {
			return i;
		}
	}
}

LsVector.prototype.merge =
function(offset, list) {

	if (offset < 0)
		return;
	
	var rawList = list instanceof LsVector ? list.getArray() : list;
	
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
}
