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
* @class
* This class represents a list of attributes.
* @author Conrad Damon
*/
function DvAttrList() {

	DvList.call(this, true);

	this._vector = new AjxVector();
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
