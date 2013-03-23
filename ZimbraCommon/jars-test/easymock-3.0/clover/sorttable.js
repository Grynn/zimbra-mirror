/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2011 VMware, Inc.
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
* This code is loosely based on sorrtable by Stuart Langridge, November 2003.
* http://kryogenix.org/code/browser/sorttable/sorttable.js .
*
**/

var SORT_COLUMN_INDEX;

function ts_getSortValue(el) {
   var retVal = '0';
   for (i = 0; i < el.childNodes.length; i++) {
        if (el.childNodes[i].className == 'sortValue') {
            retVal = el.childNodes[i].innerHTML;
        }
    }
    return retVal;
}

function ts_resortTable(td, sortType, column) {

    var tbody = ts_getParent(td,'TBODY');
    var rowcount = tbody.rows.length;

    if (rowcount <= 1) return; // nothing to see here.


    // Delete any other arrows that may be showing
   var headerTds = ts_getParent(td, 'tr').childNodes;
   for (var ci=0;ci<headerTds.length;ci++) {
       if (headerTds[ci].tagName && headerTds[ci].tagName.toLowerCase() == 'td') {
           headerTds[ci].className = headerTds[ci].className.replace(/arrowUp/, '');
           headerTds[ci].className = headerTds[ci].className.replace(/arrowDown/, '');
       }
   }

   var sortfn = sortType == 'number' ? ts_sort_numeric : ts_sort_caseinsensitive;

   SORT_COLUMN_INDEX = column;
   var newRows = new Array();
   for (j=1; j < rowcount; j++) { newRows[j-1] = tbody.rows[j]; } // copies all rows we want to sort. Not the headers.

   newRows.sort(sortfn);


   if (td.getAttribute("sortdir") == 'down') {
       td.className = td.className + ' arrowUp';
       newRows.reverse();
       td.setAttribute('sortdir','up');
   } else {
       td.className = td.className + ' arrowDown';
       td.setAttribute('sortdir','down');
   }

   for (i=0;i<newRows.length;i++) {
       tbody.appendChild(newRows[i]);
   }

}

function ts_getParent(el, pTagName) {
	if (el == null) return null;
	else if (el.nodeType == 1 && el.tagName.toLowerCase() == pTagName.toLowerCase())	// Gecko bug, supposed to be uppercase
		return el;
	else
		return ts_getParent(el.parentNode, pTagName);
}

function ts_sort_numeric(a,b) { 
    aa = parseFloat(ts_getSortValue(a.cells[SORT_COLUMN_INDEX]));
    if (isNaN(aa)) aa = 0;
    bb = parseFloat(ts_getSortValue(b.cells[SORT_COLUMN_INDEX]));
    if (isNaN(bb)) bb = 0;
    return aa-bb;
}

function ts_sort_caseinsensitive(a,b) {
    aa = ts_getSortValue(a.cells[SORT_COLUMN_INDEX]).toLowerCase();
    bb = ts_getSortValue(b.cells[SORT_COLUMN_INDEX]).toLowerCase();
    if (aa==bb) return 0;
    if (aa<bb) return -1;
    return 1;
}

function ts_sort_default(a,b) {
    aa = ts_getSortValue(a.cells[SORT_COLUMN_INDEX]);
    bb = ts_getSortValue(b.cells[SORT_COLUMN_INDEX]);
    if (aa==bb) return 0;
    if (aa<bb) return -1;
    return 1;
}

