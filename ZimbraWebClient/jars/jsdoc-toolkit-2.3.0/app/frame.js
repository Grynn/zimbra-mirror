/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2010, 2013 Zimbra Software, LLC.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.4 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */
IO.include("frame/Opt.js");
IO.include("frame/Chain.js");
IO.include("frame/Link.js");
IO.include("frame/String.js");
IO.include("frame/Hash.js");
IO.include("frame/Namespace.js");
//IO.include("frame/Reflection.js");

/** A few helper functions to make life a little easier. */

function defined(o) {
	return (o !== undefined);
}

function copy(o) { // todo check for circular refs
	if (o == null || typeof(o) != 'object') return o;
	var c = new o.constructor();
	for(var p in o)	c[p] = copy(o[p]);
	return c;
}

function isUnique(arr) {
	var l = arr.length;
	for(var i = 0; i < l; i++ ) {
		if (arr.lastIndexOf(arr[i]) > i) return false;
	}
	return true;
}

/** Returns the given string with all regex meta characters backslashed. */
RegExp.escapeMeta = function(str) {
	return str.replace(/([$^\\\/()|?+*\[\]{}.-])/g, "\\$1");
}
