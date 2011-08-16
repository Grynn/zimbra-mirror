/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Zimlets
 * Copyright (C) 2008, 2009, 2010 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.3 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 *@Author Raja Rao DV
 */
ZmSearchAutoZimlet.maxHistoryItems = 150;//max unique search items to store
ZmSearchAutoZimlet.searchHistoryHdr = "Search History";
ZmSearchAutoZimlet.advSearchHdr = "Advanced Search";
ZmSearchAutoZimlet.noHistoryMatched = "<i>no search history matched</i>";
//if user does a search with DELETE_HISTORY_STORED_BY_SEARCH_AUTO_ZIMLET query, we will clear everything
ZmSearchAutoZimlet.DELETE_HISTORY_STORED_BY_SEARCH_AUTO_ZIMLET = "DELETE_HISTORY_STORED_BY_SEARCH_AUTO_ZIMLET";

function ZmSearchAutoZimlet() {
}

ZmSearchAutoZimlet.prototype = new ZmZimletBase();
ZmSearchAutoZimlet.prototype.constructor = ZmSearchAutoZimlet;

ZmSearchAutoZimlet.prototype.init =
function() {
	this.metaData = appCtxt.getActiveAccount().metaData;

	//for this version (2.0) we will keep this to clear history. We will remove this line after 2.1
	this.setUserProperty("history", "", true);
	this.metaData.get("SearchAutoZimletHistory", null, new AjxCallback(this, this._handleGetSearchHistory));
    this.hookACToSearchField();
};

ZmSearchAutoZimlet.prototype._handleGetSearchHistory =
function(result) {
  	this._searchHistoryList = []; //nullify old data
	try {
		var list = result.getResponse().BatchResponse.GetMailboxMetadataResponse[0].meta[0]._attrs;
		if(list && list["0"]) {
			for(var i in list) {
				this._searchHistoryList.push(list[i]);
			}
		}
	} catch(ex) {
		this._showErrorMessage(ex);
	}
};

ZmSearchAutoZimlet.prototype._setSearchHistory =
function (val) {
	this._addToHistoryList(val);
	if(val == ZmSearchAutoZimlet.DELETE_HISTORY_STORED_BY_SEARCH_AUTO_ZIMLET) {
		this._searchHistoryList = [];
	}
	this.metaData.set("SearchAutoZimletHistory", this._searchHistoryList, null, new AjxCallback(this, this._handleSetSearchHistory));
};

ZmSearchAutoZimlet.prototype._addToHistoryList =
function(val) {
	var exists = false;
	var lVal = val.toLowerCase();
	for(var i = 0; i < this._searchHistoryList.length; i++) {
		var extItem = this._searchHistoryList[i].toLowerCase();
		if(extItem == lVal) {
			exists = true;
			break;
		}
	}
	if(this._searchHistoryList.length > ZmSearchAutoZimlet.maxHistoryItems) {
	  this._searchHistoryList.splice(0,1);
	}
	if(!exists) {
		this._searchHistoryList.push(val);
	}
};

ZmSearchAutoZimlet.prototype._handleSetSearchHistory =
function(response) {
 	if(response.isException()) {
		this._showErrorMessage("Something is wrong. Please disable Search Autocomplete Zimlet(ZmSearchAutoZimlet) <br>" + response.getException());
 	}
};

ZmSearchAutoZimlet.prototype.hookACToSearchField =
function() {
    this.searchField = appCtxt.getSearchController().getSearchToolbar().getSearchField();
    this.searchField.id = "mainSearchInputFieldID";
    this._autoCompleteHistoryContainer = document.getElementById("z_shell").appendChild(document.createElement('div'));
    this._autoCompleteHistoryContainer.id = "autoCompleteHistoryContainerID";
    this._ctrlDiv = this._autoCompleteHistoryContainer.appendChild(document.createElement('div'));
    this._ctrlDiv.id = "searchautoCtrl";
    this._ctrlDiv.className = "sa_ctrl_class";
    this._autoCompleteHistoryContainer.style.zIndex = 9000;
    this._autoCompleteHistoryContainer.style.display = "none";

    Dwt.setHandler(this.searchField, DwtEvent.ONKEYUP, AjxCallback.simpleClosure(this.showHistory, this));
    Dwt.setHandler(this.searchField, DwtEvent.ONKEYPRESS, AjxCallback.simpleClosure(this.scrollACList, this));

};

//------------------------------------------------------------------------------------------
//			STORE HISTORY
//------------------------------------------------------------------------------------------

ZmSearchAutoZimlet.prototype.onSearchButtonClick =
function(val) {
    this._mouseOrKeySelection = false;
    this.onKeyPressSearchField(val);
};

ZmSearchAutoZimlet.prototype.onKeyPressSearchField =
function(val) {

    if (appCtxt.getSearchController()._searchFor != ZmId.SEARCH_MAIL) {//if its not mail/conv search.. dont show
        return;
	}
    if (val == undefined || val == "") {
        return;
	}
	this._setSearchHistory(val);
};

//------------------------------------------------------------------------------------------
//	AUTO-COMPLETE AND HANDLE EVENTS..
//------------------------------------------------------------------------------------------


ZmSearchAutoZimlet.prototype.scrollACList =
function(ev) {
    if (this._totalListItems == 0 || this._listCollapsed)
        return;

    var event = ev || window.event;
    if (event == undefined)
        return;
    if (event.keyCode != 40 && event.keyCode != 38) {//redirect
        this.showHistory();
        return;
    } else if (event.keyCode == 13 || event.keyCode == 3) {
        this._mouseOrKeySelection = true;
        this._onclick();
        return;
    }

		// go down
    if (event.keyCode == 40 && (this._currentHoverNo < this._totalListItems - 1) && this._currentHoverNo >= -1) {
        this._currentHoverNo++;
        if (this._currentHoverNo != 0) {//mouseout previous
            this._onmouseout("autoListItem_" + (this._currentHoverNo - 1));
        }
        this._onmouseover("autoListItem_" + (this._currentHoverNo));
    } else if (event.keyCode == 38 && this._currentHoverNo > 0) {//go up
        this._currentHoverNo--;
        this._onmouseout("autoListItem_" + (this._currentHoverNo + 1));
        this._onmouseover("autoListItem_" + (this._currentHoverNo));
    }
};

ZmSearchAutoZimlet.prototype.showHistory =
function(ev) {
    var event = ev || window.event;
    if (event == undefined)
        return;
    if (event.keyCode == 38 || event.keyCode == 40) {//redirect to scroll
        this.scrollACList();
        return;
    } else if (event.keyCode == 13 || event.keyCode == 3) {
        this._mouseOrKeySelection = true;
        this._onclick();
        return;
    }
    this._parseQuery();
	if(this._query == "") {
		return;
	}
    setTimeout(AjxCallback.simpleClosure( this._showHistory, this), 300);
};


ZmSearchAutoZimlet.prototype._showHistory =
function(result) {
    this.createAutoCompleteList();
    var el = document.getElementById('searchautoCtrl');
    var input = document.getElementById('mainSearchInputFieldID');
			//set the x,y & width of the autocomplete popup
    if (input.offsetParent) {
        var inputPos = this._findPos(document.getElementById('mainSearchInputFieldID'));
        el.style.left = inputPos[0];
        el.style.top = inputPos[1] + input.offsetHeight;
        el.style.width = document.getElementById('mainSearchInputFieldID').scrollWidth - 2;
    }
};

ZmSearchAutoZimlet.prototype.createAutoCompleteList =
function() {
    var result = this.returnHistory();
	if((result instanceof Array) && result.length == 0) {
		return;
	}
    var startStr = "<ul id='sa_ulist'>";
    var endStr = "</ul>";
    var li = "";
    this._currentHoverNo = -1;
    this._listCollapsed = true;
    this._selectedItemId = "";
    this.idAndVal = new Array();
    if (result.length == 0) {
        this._ctrlDiv.innerHTML = (startStr + li + endStr);
        return;
    }
    var cnt = 0;
    for (var el in result) {
        var val = result[el];
        var id;
        if (val == ZmSearchAutoZimlet.searchHistoryHdr || val == ZmSearchAutoZimlet.advSearchHdr) {
            id = "autoList_Hdr_" + cnt;
            li = li + "<li class='sa_autoListHdr' id='" + id + "'>" + val + "</li>";

        } else if(val.indexOf(ZmSearchAutoZimlet.noHistoryMatched) >=0) {
	        id = "autoNoHistoryFoundItem_" + cnt;
            li = li + "<li id='" + id + "'>" + val + "</li>";
		} else {
            id = "autoListItem_" + cnt;
            li = li + "<li id='" + id + "'>" + val + "</li>";
            cnt++;//increment
        }
        this.idAndVal[id] = val.replace("<b>", "").replace("</b>", "").replace("<i>", "").replace("</i>", "");
    }
	
    this._totalListItems = cnt;//store
    this._ctrlDiv.innerHTML = startStr + li + endStr;
    var lis = this._ctrlDiv.getElementsByTagName("li");
    for (var i = 0; i < lis.length; i++) {
        var itm = lis[i];
        var id = itm.id;
        if (this.idAndVal[id] == ZmSearchAutoZimlet.searchHistoryHdr || this.idAndVal[id] == ZmSearchAutoZimlet.advSearchHdr
                || id.indexOf("autoNoHistoryFoundItem_") >= 0) {
            continue;
        }

        itm.onmouseover = AjxCallback.simpleClosure(this._onmouseover, this, id);
        itm.onmouseout = AjxCallback.simpleClosure(this._onmouseout, this, id);
        itm.onclick = AjxCallback.simpleClosure(this._onclick, this, id);
    }
    this.showContainer();
};


ZmSearchAutoZimlet.prototype.showContainer =
function() {
    if (this.searchWasJustTriggered)//handle fast-typing+enter(will be set to false by a timer)
        return;

    document.getElementById("autoCompleteHistoryContainerID").style.display = "block";
    document.getElementById("searchautoCtrl").style.display = "block";
    this._listCollapsed = false;
    DwtEventManager.addListener(DwtEvent.ONMOUSEDOWN, AjxCallback.simpleClosure(this.hideContainer, this));
};

ZmSearchAutoZimlet.prototype.hideContainer =
function() {
    document.getElementById("autoCompleteHistoryContainerID").style.display = "none";
    document.getElementById("searchautoCtrl").style.display = "none";
    this._listCollapsed = true;
    DwtEventManager.removeListener(DwtEvent.ONMOUSEDOWN, AjxCallback.simpleClosure(this.hideContainer, this));
};

ZmSearchAutoZimlet.prototype._onmouseover =
function(id) {
    if (this._selectedItemId != "") {
        //when mouse is used after some down-arrow selection, clear the down-arrow selection.
        document.getElementById(this._selectedItemId).style.backgroundColor = "white";
    }
    this._selectedItemId = id;
    document.getElementById(id).style.backgroundColor = "gainsboro";
};

ZmSearchAutoZimlet.prototype._onmouseout =
function(id) {
    document.getElementById(id).style.backgroundColor = "white";
};

ZmSearchAutoZimlet.prototype._onclick =
function() {
    if (this._selectedItemId != "") {//when one of the items is selected..
        if (!this._listCollapsed) {//advanced search or searchHistory was selected..
            if (this._selectedItemId != "") {//replace the selected list's value
                this.searchField.value = this.idAndVal[this._selectedItemId];
            }
        }
    }
	//add to history(if unique)
    this.onKeyPressSearchField(this.searchField.value);

    var getHtml = appCtxt.get(ZmSetting.VIEW_AS_HTML);
    appCtxt.getSearchController().search({query: this.searchField.value, userText: true, getHtml: getHtml});
    this.hideContainer();

//finally, notify searchRefinerZimlet
    // & also disable this zimlet for 3 seconds(to handle fast-typing+enter)
    if (this._mouseOrKeySelection) {
        this.notifySearchRefinerZimlet();
        this.searchWasJustTriggered = true;
        setTimeout(AjxCallback.simpleClosure(this.resetSWJT, this), 10000);
    }

};

ZmSearchAutoZimlet.prototype.resetSWJT =
function() {
    this.searchWasJustTriggered = false;

};

//notifies searchRefiner zimlet when the history-search or when enter-key is clicked(since this zimlet consumes both those events)
ZmSearchAutoZimlet.prototype.notifySearchRefinerZimlet =
function() {
    var searchRefinerZimlet;
    if (!appCtxt.zimletsPresent()) {
        return;
    }
    var zimlets = appCtxt.getZimletMgr().getZimlets();
    for (var i = 0; i < zimlets.length; i++) {
        if (zimlets[i].name == "com_zimbra_searchrefiner") {
            searchRefinerZimlet = zimlets[i];
            break;
        }
    }
    if (searchRefinerZimlet != undefined) {
        searchRefinerZimlet.handlerObject.onKeyPressSearchField();
    }

};

ZmSearchAutoZimlet.prototype._findPos =
function(obj) {
    var curleft = curtop = 0;
    if (obj.offsetParent) {
        do {
            curleft += obj.offsetLeft;
            curtop += obj.offsetTop;
        } while (obj = obj.offsetParent);
        return [curleft,curtop];
    }
};

ZmSearchAutoZimlet.prototype._parseQuery =
function() {
    this._query = AjxStringUtil.htmlEncode(document.getElementById('mainSearchInputFieldID').value);
    this._noFldrQuery = this._query.replace(/in:\w*\s/, "");//remove any folder-context(like in:inbox) to get actual search-query
    this._fldrInQuery = this._query.match(/in:\w*\s/, "");
    if (this._fldrInQuery == undefined) {
        this._fldrInQuery = "";
	}
};

ZmSearchAutoZimlet.prototype.returnHistory =
function() {
    if (this._query.length < 2) {//must have atleast 1 letter
        return [];
	}
    var noFldrQuery_bold = "<b>" + this._noFldrQuery + "</b>";

    var advSearch = [
        ZmSearchAutoZimlet.advSearchHdr,
		this._fldrInQuery + noFldrQuery_bold + " has:attachment",
		this._fldrInQuery + noFldrQuery_bold + " in:sent",
        this._fldrInQuery + "from:(" + noFldrQuery_bold + ")",
        this._fldrInQuery + "cc:(" + noFldrQuery_bold + ")",
        this._fldrInQuery + "subject:(" + noFldrQuery_bold + ")",
        this._fldrInQuery + "from:(@" + noFldrQuery_bold + ")"
    ];

    var hist = new Array();
    var count = 0;
	hist.push(ZmSearchAutoZimlet.searchHistoryHdr);//search history header
	var len = this._searchHistoryList.length;
    for (var i =0; i < len; i++) {
		var el = this._searchHistoryList[i];
        if ((el.indexOf(this._query) >= 0 || el.indexOf(":" + this._query) > 0)) {
         	if(count > 5) {
				break;
			}
            hist.push(el.replace(this._query, "<b>" + this._query + "</b>"));//bold the matched letters
        	count++;
		}
    }
	if(count == 0) {
		hist.push(ZmSearchAutoZimlet.noHistoryMatched);
	}
    return hist.concat(advSearch);
};

/**
 * Displays error message.
 *
 * @param {string} expnMsg Exception message string
 */
ZmSearchAutoZimlet.prototype._showErrorMessage =
function(expnMsg) {
	var msg = "";
	if (expnMsg instanceof AjxException) {
		msg = expnMsg.msg;
	} else {
		msg = expnMsg;
	}
	var dlg = appCtxt.getMsgDialog();
	dlg.reset();
	dlg.setMessage(msg, DwtMessageDialog.WARNING_STYLE);
	dlg.popup();
};
