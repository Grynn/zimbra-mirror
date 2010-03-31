/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Zimlets
 * Copyright (C) 2009, 2010 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.3 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 * @Author Raja Rao DV (rrao@zimbra.com)
 * Inserts email with current day's appointments on first login
 */

function com_zimbra_srchhltr() {
}

com_zimbra_srchhltr.prototype = new ZmZimletBase();
com_zimbra_srchhltr.prototype.constructor = com_zimbra_srchhltr;


com_zimbra_srchhltr.prototype.init =
function() {
	this._searchController = appCtxt.getSearchController();
	this._skipKeys = ["in:", "attachment:", "has:", "is:", "before:", "after:", "date:", "larger:", "smaller:",  "from:", "to:", "cc:", "bcc:", "and", "or"];
	this._skipKeysLen = this._skipKeys.length;
	this._spanIds = [];
};

com_zimbra_srchhltr.prototype.match =
function(line, startIndex) {
	this._setRegExps();
	if(this._regexps.length == 0) {
		return;
	}
	var a = this._regexps;
	var ret = null;
	for (var i = 0; i < a.length; ++i) {
		var re = a[i];
		re.lastIndex = startIndex;
		var m = re.exec(line);
        if (m) {
            if (!ret || m.index < ret.index) {
                ret = m;
            }
        }
	}
	return ret;	
};

com_zimbra_srchhltr.prototype.generateSpan =
function(html, idx, obj, spanId, context) {
	var id = Dwt.getNextId();
	this._spanIds.push(id);
	html[idx++] = ["<span id= '",id,"'style='background-color:#FFFF53'>",obj,"</span>"].join("");	
	return idx;
};

com_zimbra_srchhltr.prototype._getSearchWords =
function(searchStr) {
	var dArry = searchStr.toLowerCase().split(" ");
	var result1 = [];
	for(var i=0; i <  dArry.length; i++) {
		var d = dArry[i];	
		var skipThis = false;
		for(var j=0; j < this._skipKeysLen; j++) {
			var k = this._skipKeys[j];
			if(d.indexOf(k) == 0 || d.indexOf("("+k) == 0 || d.indexOf("(("+k) == 0) {
				skipThis = true;
				break;
			}
		}
		if(!skipThis) {
			d = d.replace("subject:", "").replace("content:", "");//replace subject and content keys
			result1.push(d);
		}
	}
	var result2 = [];
	for(var k=0; k< result1.length; k++) {//remove " ( and )
		var word = result1[k];
		var len = word.length;
		if((word.indexOf("\"") == 0) || (word.indexOf("\"") == len-1)) {
			word = word.replace(/\"/g, "");
		}
		if((word.indexOf("(") == 0) || (word.indexOf(")") == len-1)) {
			word = word.replace(/\(/g, "").replace(/\)/g, "");
		}
		result2.push(word);
	}
	return searchWordHighlighter_unique(result2);
};

function searchWordHighlighter_unique(b) {
	var a = [], i, l = b.length;
	for (i = 0; i < l; i++) {
		if (!searchWordHighlighter_arrayHasEl(a, b[i])) {
			a.push(b[i]);
		}
	}
	return a;
}

function searchWordHighlighter_arrayHasEl(array, val){
	for (var i = 0; i < array.length; i++) {
		if (array[i] == val) {
			return true;
		}
	}
	return false;
}

com_zimbra_srchhltr.prototype._setRegExps =
function() {
	this._currentSearchQuery = this._searchController.currentSearch.query;
	if(!this._oldSearchQuery || this._currentSearchQuery != this._oldSearchQuery) {
		var words = this._getSearchWords(this._currentSearchQuery);
		this._regexps = [];
		for(var i =0; i < words.length; i++) {
			this._regexps.push(new RegExp(words[i],"ig"));
		}
		 this._oldSearchQuery = this._currentSearchQuery;
	}
};


//------------------------------------------------
// Context menu / clear highlight related
//------------------------------------------------
com_zimbra_srchhltr.prototype.onParticipantActionMenuInitialized =
function(controller, menu) {
	this.onActionMenuInitialized(controller, menu);
};

//called by zimbra-core when menu is initialized
com_zimbra_srchhltr.prototype.onActionMenuInitialized =
function(controller, menu) {
	this.addMenuButton(controller, menu);
};

com_zimbra_srchhltr.prototype.addMenuButton = function(controller, menu) {
	var ID = "COM_ZIMBRA_SEARCH_WORD_HIGHLIGHTER_ZIMLET";
	var text = this.getMessage("SearchHighlighterZimlet_MenuLabel"); //TODO - not working
	//var text = "Clear Search Highlights";
	if (!menu.getMenuItem(ID)) {
		var op = {
			id:             ID,
			text:          text,
			image:          "search"
		};
		var opDesc = ZmOperation.defineOperation(null, op);
		menu.addOp(ID, 1000);//add the button at the bottom
		menu.addSelectionListener(ID, new AjxListener(this,
		this._clearSearchWordHighlights, controller));
	}
};

com_zimbra_srchhltr.prototype._clearSearchWordHighlights = function(controller){
	var msgBody = appCtxt.getAppViewMgr().getCurrentView().getMsgView().getHtmlBodyElement();
	for(var i =0; i < this._spanIds.length; i++) {
		var obj = document.getElementById(this._spanIds[i]);
		var bodyObj;
		if(msgBody != undefined || msgBody != null) {
			bodyObj = msgBody.ownerDocument.getElementById(this._spanIds[i]);
		}
		if((obj != undefined) && (obj.style != undefined)) {
			obj.style.backgroundColor = "";
		}
		if((bodyObj != undefined) && (bodyObj.style != undefined)) {
			bodyObj.style.backgroundColor = "";
		}
	}
	this._spanIds = [];//reset
};