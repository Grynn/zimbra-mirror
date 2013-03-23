/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2013 VMware, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.3 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * 
 * ***** END LICENSE BLOCK *****
 */

/**
 * <p>Helper class for the commonly used functions throughout the application.</p>
 *
 * @class
 * @static
 *
 * @author Mohammed Shaik Hussain Ali
 *
 * @this {ZaHelper}
 *
 */
ZaHelper = function() {}

/**
 * <p>Relative path of the help URL.</p>
 *
 * @static
 * @type {string}
 *
 */
ZaHelper.HELP_URL = "help/admin/html/";

/**
 * <p>Converts the lifetime value into seconds for calculation purposes.</p>
 *
 * @static
 * @param v lifetime value, must end in smhd for the value to be valid
 * @return {number} lifetime value in seconds
 *
 */
ZaHelper.getLifeTimeInSeconds = function(v) {
    if (AjxUtil.isLifeTime(v)) {
        var len = v.length ;
        var d = v.substr(0, len - 1);
        var p = v.substr(len - 1, len);

        if (p === "s"){
            if (v[len - 2] === 'm') {
                // millisecond support
                d = v.substr(0, len - 2);
                return d / 1000.0;
            }
            return d;
        } else if (p === "m") {
            return d * 60 ;
        } else if (p === "h"){
            return d * 3600 ;
        } else if (p === "d") {
            return d * 216000;
        }
    } else {
        throw (new AjxException(AjxMessageFormat.format(ZaMsg.exception_invalid_lifetime, [v])));
    }
}

/**
 * <p>Returns the index within the object array of the first occurrence of the specified value for the specified
 * property name. If the value occurs, then the index of the first occurrence is returned. If no such value occurs,
 * then -1 is returned.</p>
 *
 * @static
 * @param arr object array containing all values
 * @param value to be searched
 * @param property to be used as key for searching
 * @return {number} the index of the first occurrence of value, or -1 if the value does not occur.
 *
 */
ZaHelper.getIndexForValueOfProperty = function (arr, value, property) {
    if (!property) {
        property = "name" ; //for ZaAccountMemberOfListView
    }

    if (arr) {
        for (var i = 0; i < arr.length; i++) {
            if (arr[i][property] == value) {
                return i;
            }
        }
    }

    return -1;
}

/**
 * <p>Returns the index within the object array of the first occurrence of the specified value. If the value occurs,
 * then the index of the first occurrence is returned. If no such value occurs, then -1 is returned.</p>
 *
 * @static
 * @param arr object array containing all values
 * @param value to be searched
 * @return {number} the index of the first occurrence of value, or -1 if the value does not occur.
 *
 */
ZaHelper.getIndexForValue = function (arr, value) {
    if (arr) {
        for (var i = 0; i < arr.length; i++) {
            if (arr[i] == value) {
                return i;
            }
        }
    }

    return -1;
}

/**
 * <p>Returns true if the object array contains the specified value.</p>
 *
 * @static
 * @param arr object array containing all values
 * @param value whose presence is to be tested
 * @return {boolean} true if this object array contains the specific element
 *
 */
ZaHelper.arrayContainsValue = function (arr, value) {
    return ZaHelper.getIndexForValue(arr, value) >= 0;
}

/**
 * <p>Returns the label within the object array of the first occurrence of the specified value. If the value-label pair
 * occurs, then the associated label is returned. If no such value-label pair occurs, then null is returned.</p>
 *
 * @static
 * @param arr object array containing value-label pairs
 * @param value to be searched
 * @return {string} the label of the first matched value-label pair, or null if the value-label pair does not occur.
 *
 */
ZaHelper.getLabelForValue = function (arr, value) {
    if (arr) {
        for(var i = 0; i < arr.length; i++) {
            if (arr[i]["value"] == value) {
                return arr[i]["label"];
            }
        }
    }

    return null;
}

/**
 * <p>Returns the object array after removing the duplicate values.</p>
 *
 * @static
 * @param arr object array containing all values
 * @return {Array} object array containing unique values
 *
 */
ZaHelper.removeDuplicatesFromArray = function (arr) {
    var uniqueArr = [];

    if (arr) {
        for (var i = 0; i < arr.length; i++) {
            if (!ZaHelper.arrayContainsValue(uniqueArr, arr[i])) {
                uniqueArr.push(arr[i]);
            }
        }
    }

    return uniqueArr;
}

/**
 * <p>Returns the Server DateTime string in yyyyMMddHHmmss'Z' format.</p>
 *
 * @static
 * @param date object
 * @param useUTC whether to use UTC or not
 * @return {string} date-time string in yyyyMMddHHmmss'Z' format
 *
 */
ZaHelper.getAdminServerDateTime = function (date, useUTC) {
    var s = AjxDateUtil.getServerDateTime(date, useUTC);

    // Remove the 'T' from the DateTime string
    return s.substring(0, 8) + s.substring(9);
}

/**
 * <p>Creates and returns a copy of the object. Creates a new object and initialises all its fields, except those
 * that are ignored, with exactly the contents of the corresponding fields of the cloneable object, as if by
 * assignment; the contents of the fields are also cloned. Thus, this method performs a "deep copy" operation.</p>
 *
 * @static
 * @param obj to be cloned
 * @param ignoredProperties properties of the cloneable object to be ignored while cloning
 * @return {*} clone of the object
 *
 */
ZaHelper.cloneObject = function (obj, ignoredProperties) {
    if (obj == null) {
        return null;
    }

    var newObj = {};

    for (var key in obj) {
        if (ignoredProperties && ignoredProperties.length > 0) {
            if (ZaHelper.arrayContainsValue(ignoredProperties, key)) {
                continue;
            }
        }

        var v = obj[key];

        if (v != null && (v instanceof Array || typeof (v) == "object")) {
            newObj[key] = ZaHelper.cloneObject(v);
        } else {
            newObj[key] = v;
        }
    }

    return newObj;
}

/**
 * <p>Creates and returns a copy of the array. Creates a new array and copies all values from the cloneable array,
 * as if by assignment.</p>
 *
 * <p><b>Note</b>: Assumes that all array elements are of primitive data type.</p>
 *
 * TODO: Enhance functionality to cover non-primitive elements as well
 *
 * @static
 * @param arr to be cloned
 * @return {Array} clone of the array
 *
 */
ZaHelper.cloneArray = function (arr) {
    if (arr == null) {
        return null;
    }

    var newArr = [];

    for (var i = 0; i < arr.length; i++) {
        newArr.push(arr[i]);
    }

    return newArr;
}

// TODO: Not needed as of now.
//
// * combine the object array property values
// *
// * an example:
// * var objArr =
// * [
// *      {name: "abc"},
// *      {name:"efg}
// * ]
// *
// * join(objArr, "name", ":") => "abc:efg"
//
// join = function (objArray, key, delimiter) {
//    if (objArray == null) return "" ;
//    var strArr = [] ;
//    for (var i=0; i < objArray.length; i ++) {
//        strArr.push (objArray[i][key]) ;
//    }
//    return strArr.join(delimiter) ;
//}