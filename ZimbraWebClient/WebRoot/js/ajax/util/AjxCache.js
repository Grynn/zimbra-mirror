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
/*
 * Copyright (C) 2006, The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


/**
* Creates a new, empty cache.
* @constructor
* @class
* This class represent a simple cache. So far, the cache does not do any management
* such as LRU, TTL, etc.
*
* @author Conrad Damon
*/
AjxCache = function() {
	this._cache = new Object();
}

/**
* Adds a value with the given key to the cache.
*
* @param key	[primitive]		unique key
* @param value	[any]			value
*/
AjxCache.prototype.set =
function(key, value) {
	this._cache[key] = value;
}

/**
* Returns the value with the given key.
*
* @param key	[primitive]		unique key
*/
AjxCache.prototype.get =
function(key) {
	return this._cache[key];
}

/**
* Returns a list of all the values which have a certain value
* for a certain property.
*
* @param prop	[string]		a property
* @param value	[primitive]		a value
*/
AjxCache.prototype.getByProperty =
function(prop, value) {
	var list = new Array();
	for (var key in this._cache) {
		var obj = this._cache[key];
		if (obj instanceof Object && obj[prop] == value)
			list.push(obj);
	}
	return list;
}

/**
* Clears the cache.
*/
AjxCache.prototype.clearAll =
function() {
	for (var key in this._cache)
		this._cache[key] = null;
	this._cache = new Object();
}

/*
* Removes the value with the given key from the cache.
*
* @param key	[primitive]		unique key
*/
AjxCache.prototype.clear =
function(key) {
	this._cache[key] = null;
	delete this._cache[key];
}

/*
* Removes all values which have a certain property with a certain value.
*
* @param prop	[string]		a property
* @param value	[primitive]		a value
*/
AjxCache.prototype.clearByProperty =
function(prop, value) {
	for (var key in this._cache) {
		var obj = this._cache[key];
		if (obj[prop] == value)
			this._cache[key] = null
	}
}
