/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Zimlets
 * Copyright (C) 2005, 2006, 2007, 2008, 2009, 2010 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.3 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */

/**
 * @class
 * This is a utility class.
 * 
 */
function com_zimbra_example_dynamictab_Util() {
};

/**
 * Gets a random number.
 * 
 * @param	{int}	range		a range
 * @return	{int}	a random number
 */
com_zimbra_example_dynamictab_Util._getRandomNumber =
function(range) {
	return Math.floor(Math.random() * range);
};

/**
 * Gets a random char.
 * 
 * @return	{String}	a random char
 */
com_zimbra_example_dynamictab_Util._getRandomChar =
function() {
	var chars = "0123456789abcdefghijklmnopqurstuvwxyzABCDEFGHIJKLMNOPQURSTUVWXYZ";
	return chars.substr( this._getRandomNumber(62), 1 );
};

/**
 * Generates a unique id.
 * 
 * @param	{int}	size		the size of the unique id
 * @return	{String}	the unique id
 */
com_zimbra_example_dynamictab_Util.generateUniqueID =
function(size) {
	var str = "";
	for(var i = 0; i < size; i++)
	{
		str += this._getRandomChar();
	}
	return str;
};

/**
 * Cleans the given url.
 * 
 * @param	{String}	url		the url to clean
 * @return	{String}	the resulting url
 */
com_zimbra_example_dynamictab_Util.cleanUrl =
function(url) {

	var newUrl = url;

	if (url) {
		url = url.trim();
		url = url.toLowerCase();
		
		if (url.indexOf("http://") == -1)
			url = "http://"+url;
		
		newUrl = url;
	}

	return newUrl;
};

/**
 * Checks if the item is in the array.
 * 
 * @param	{Array}		array		the array
 * @param	{String}	item		the item
 * @return	{Boolean}	<code>true</code> if the item is in the array
 */
com_zimbra_example_dynamictab_Util.arrayContains =
function(array,item) {
	for (i=0;array && i<array.length; i++) {
		if (array[i] == item)
			return true;
	}
	
	return false;
};

/**
 * 
 */
com_zimbra_example_dynamictab_Util.escapeHTML =
function (str) {                                       
    return(                                                               
        str.replace(/&/g,'&amp;').                                         
            replace(/>/g,'&gt;').                                           
            replace(/</g,'&lt;').                                           
            replace(/"/g,'&quot;')                                         
    );
    
};