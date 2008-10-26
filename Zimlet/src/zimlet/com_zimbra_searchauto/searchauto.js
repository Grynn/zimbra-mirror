/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Zimlets
 * Copyright (C) 2006, 2007 Zimbra, Inc.
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
 *@Author Raja Rao DV
 */
com_zimbra_searchauto.maxHistoryItems = 1000;//max unique search items to store
com_zimbra_searchauto.searchHistoryHdr = "Search History";
com_zimbra_searchauto.advSearchHdr = "Advanced Search";
com_zimbra_searchauto.ySearchHdr = "Top 5 Yahoo Search Result";
com_zimbra_searchauto.searchYFor = " Search for: ";
com_zimbra_searchauto.searchYLFor = " Local Search for: ";
com_zimbra_searchauto.URL = "http://search.yahooapis.com/WebSearchService/V1/webSearch?appid=zimbra&results=5&output=json";


function com_zimbra_searchauto() {
}

com_zimbra_searchauto.prototype = new ZmZimletBase();
com_zimbra_searchauto.prototype.constructor = com_zimbra_searchauto;


com_zimbra_searchauto.prototype.init =
function() {

    this.searchAutoCompleteON = this.getUserProperty("turnONAutoComplete") == "true";
    if (!this.searchAutoCompleteON)
        return;
    this.hookACToSearchField();
};

com_zimbra_searchauto.prototype.hookACToSearchField =
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

}

//------------------------------------------------------------------------------------------
//			STORE HISTORY
//------------------------------------------------------------------------------------------

com_zimbra_searchauto.prototype.onSearchButtonClick =
function(val) {
    this._mouseOrKeySelection = false;
    this.onKeyPressSearchField(val);
}

com_zimbra_searchauto.prototype.onKeyPressSearchField =
function(val) {
    if (!this.searchAutoCompleteON)
        return;
    if (appCtxt.getSearchController()._searchFor != ZmId.SEARCH_MAIL)//if its not mail/conv search.. dont show
        return;
    if (val == undefined || val == "")
        return;

    var currHistory = this.getUserProperty("history");
    if (("::" + currHistory + "::").indexOf("::" + val + "::") == -1) {//dont add dupes
        //load to the front of the string to get latest
        this.setUserProperty("history", val + "::" + currHistory, true);
        this._manageHistory();//make sure to store upto 1000 unique items, remove older ones
    }

}


//if history count goes >1000, this restores it back to 800(i.e. 200 new space)
com_zimbra_searchauto.prototype._manageHistory =
function() {
    var tmp = this.getUserProperty("history").split("::");
    if (tmp.length > com_zimbra_searchauto.maxHistoryItems) {//store a max of 1000 unique items
        var newHistory = "";
        for (var i = 0; i < (com_zimbra_searchauto.maxHistoryItems + 200); i++) {
            newHistory = newHistory + "::" + tmp[i];
        }
        this.setUserProperty("history", newHistory, true);

    }
}

//------------------------------------------------------------------------------------------
//	AUTO-COMPLETE AND HANDLE EVENTS..
//------------------------------------------------------------------------------------------


com_zimbra_searchauto.prototype.scrollACList =
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
}

com_zimbra_searchauto.prototype.showHistory =
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
	if(this._query == "")
		return;
    setTimeout(AjxCallback.simpleClosure( this.doYahooSearch, this), 300);


};
com_zimbra_searchauto.prototype.doYahooSearch =
function() {

	//handle fast-typing(ignore all queries queued up queries w/in interval: 350ms)
	if(this._oldquery){
		if(this._query ==this._oldquery){
			return;
		}
	}
	this._oldquery = this._query;


    var url = ZmZimletBase.PROXY + AjxStringUtil.urlComponentEncode(com_zimbra_searchauto.URL + "&query=" + this._noFldrQuery.replace(" ", "+"));
    AjxRpc.invoke(null, url, null, new AjxCallback(this, this.handleSearchAndShowHistory), true);
}

com_zimbra_searchauto.prototype.handleSearchAndShowHistory =
function(result) {
    var ySearchArry = new Array();
    try {
        ySearchArry = eval("(" + result.text + ")").ResultSet.Result;
    } catch(e) {
        ySearchArry = new Array();
    }
    this.createAutoCompleteList(ySearchArry);
    var el = document.getElementById('searchautoCtrl');
    var input = document.getElementById('mainSearchInputFieldID');
			//set the x,y & width of the autocomplete popup
    if (input.offsetParent) {
        var inputPos = this._findPos(document.getElementById('mainSearchInputFieldID'));
        el.style.left = inputPos[0];
        el.style.top = inputPos[1] + input.offsetHeight;
        el.style.width = document.getElementById('mainSearchInputFieldID').scrollWidth - 2;
    }
}
com_zimbra_searchauto.prototype.createAutoCompleteList =
function(ySearchArry) {
    var result = this.returnHistory();
    var startStr = "<ul id='sa_ulist'>";
    var endStr = "</ul>";
    var li = "";
	 var ys_li = "";
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
        if (val == com_zimbra_searchauto.searchHistoryHdr || val == com_zimbra_searchauto.advSearchHdr) {
            id = "autoList_Hdr_" + cnt;
            li = li + "<li class='sa_autoListHdr' id='" + id + "'>" + val + "</li>";

        } else if(val == com_zimbra_searchauto.ySearchHdr){
			id = "autoList_Hdr_" + cnt;
            ys_li  = ys_li  + "<li class='sa_autoListHdr' id='" + id + "'>" + val + "</li>";
		}else if (val.indexOf(com_zimbra_searchauto.searchYFor) >= 0 || val.indexOf(com_zimbra_searchauto.searchYLFor) >= 0) {
            id = "autoListItem_" + cnt;
            li = li + "<li  class='sa_yAutoListItem' id='" + id + "'><img src='" + this.getResource("y1.gif") + "'/>" + val + "</li>";
            cnt++;//increment
        } else {
            id = "autoListItem_" + cnt;
            li = li + "<li id='" + id + "'>" + val + "</li>";
            cnt++;//increment
        }
        this.idAndVal[id] = val.replace("<b>", "").replace("</b>", "");
    }
	
	 li = li + ys_li;//add yahoo-search header

	//append ysearch results..
    var newcnt = cnt;//make sure the count starts where it left off	
    for (var el in ySearchArry) {
        var val = "<b>" + ySearchArry[el].Title + "</b><br/>" + ySearchArry[el].Url;
        var id = "autoListItem_" + newcnt;
        li = li + "<li  class='sa_yAutoListItem' id='" + id + "'> " + val + "</li>";
        this.idAndVal[id] = ySearchArry[el].Url;
        newcnt++;//increment

    }
    this._totalListItems = newcnt;//store
    this._ctrlDiv.innerHTML = startStr + li + endStr;
    var lis = this._ctrlDiv.getElementsByTagName("li");
    for (var i = 0; i < lis.length; i++) {
        if (this.idAndVal[lis.id] == com_zimbra_searchauto.searchHistoryHdr || this.idAndVal[lis.id] == com_zimbra_searchauto.advSearchHdr
                || this.idAndVal[lis.id] == com_zimbra_searchauto.ySearchHdr) {
            continue;
        }
        var itm = lis[i];
        var id = itm.id;
        itm.onmouseover = AjxCallback.simpleClosure(this._onmouseover, this, id);
        itm.onmouseout = AjxCallback.simpleClosure(this._onmouseout, this, id);
        itm.onclick = AjxCallback.simpleClosure(this._onclick, this, id);
    }
    this.showContainer();
}


com_zimbra_searchauto.prototype.showContainer =
function() {
    if (this.searchWasJustTriggered)//handle fast-typing+enter(will be set to false by a timer)
        return;

    document.getElementById("autoCompleteHistoryContainerID").style.display = "block";
    document.getElementById("searchautoCtrl").style.display = "block";
    this._listCollapsed = false;
    DwtEventManager.addListener(DwtEvent.ONMOUSEDOWN, AjxCallback.simpleClosure(this.hideContainer, this));
}

com_zimbra_searchauto.prototype.hideContainer =
function() {
    document.getElementById("autoCompleteHistoryContainerID").style.display = "none";
    document.getElementById("searchautoCtrl").style.display = "none";
    this._listCollapsed = true;
    DwtEventManager.removeListener(DwtEvent.ONMOUSEDOWN, AjxCallback.simpleClosure(this.hideContainer, this));
}

com_zimbra_searchauto.prototype._onmouseover =
function(id) {
    if (this._selectedItemId != "") {
        //when mouse is used after some down-arrow selection, clear the down-arrow selection.
        document.getElementById(this._selectedItemId).style.backgroundColor = "white";
    }
    this._selectedItemId = id;
    document.getElementById(id).style.backgroundColor = "gainsboro";
}
com_zimbra_searchauto.prototype._onmouseout =
function(id) {
    document.getElementById(id).style.backgroundColor = "white";
}
com_zimbra_searchauto.prototype._onclick =
function() {
    if (this._selectedItemId != "") {//when one of the items is selected..
        if (document.getElementById(this._selectedItemId).className == "sa_yAutoListItem") {//open website/ysearch
            var target = this.idAndVal[this._selectedItemId];
            if (target.indexOf(com_zimbra_searchauto.searchYLFor) >= 0) {
                target = target.replace(com_zimbra_searchauto.searchYLFor, "").replace("<b>", "").replace("</b>", "");
                target = "http://local.yahoo.com/?p=" + AjxStringUtil.urlComponentEncode(target);
            } else if (target.indexOf(com_zimbra_searchauto.searchYFor) >= 0) {
                target = target.replace(com_zimbra_searchauto.searchYFor, "").replace("<b>", "").replace("</b>", "");
                target = "http://search.yahoo.com/search?p=" + AjxStringUtil.urlComponentEncode(target);
            }
            window.open(target);
            this.hideContainer();
            return;
        }


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

}


com_zimbra_searchauto.prototype.resetSWJT =
function() {
    this.searchWasJustTriggered = false;

}

//notifies searchRefiner zimlet when the history-search or when enter-key is clicked(since this zimlet consumes both those events)
com_zimbra_searchauto.prototype.notifySearchRefinerZimlet =
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

}
com_zimbra_searchauto.prototype._findPos =
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
com_zimbra_searchauto.prototype._parseQuery =
function() {
    this._query = document.getElementById('mainSearchInputFieldID').value;
    this._noFldrQuery = this._query.replace(/in:\w*\s/, "");//remove any folder-context(like in:inbox) to get actual search-query
    this._fldrInQuery = this._query.match(/in:\w*\s/, "");
    if (this._fldrInQuery == undefined)
        this._fldrInQuery = "";
}
com_zimbra_searchauto.prototype.returnHistory =
function() {

    //var query = document.getElementById('mainSearchInputFieldID').value;
    var historyArry = this.getUserProperty("history").split("::");

    if (this._query.length < 2)//must have atleast 1 letter
        return [];

    var noFldrQuery_bold = "<b>" + this._noFldrQuery + "</b>";


    var ySearch = [
        com_zimbra_searchauto.ySearchHdr,
        com_zimbra_searchauto.searchYFor + noFldrQuery_bold,
        com_zimbra_searchauto.searchYLFor + noFldrQuery_bold,

    ];

    var advSearch = [
        com_zimbra_searchauto.advSearchHdr,
        this._fldrInQuery + "from:(" + noFldrQuery_bold + ")",
        this._fldrInQuery + "cc:(" + noFldrQuery_bold + ")",
        this._fldrInQuery + "subject:(" + noFldrQuery_bold + ")",
        this._fldrInQuery + "from:(@" + noFldrQuery_bold + ")",
        this._fldrInQuery + noFldrQuery_bold + " has:attachment"
    ];


    var hist = new Array();
    var count = 0;
    for (var i = 0; i < historyArry.length; i++) {
        if ((historyArry[i].indexOf(this._query) >= 0 || historyArry[i].indexOf(":" + this._query) > 0)) {
            if (count == 0) {//push the header
                hist.push(com_zimbra_searchauto.searchHistoryHdr);//search history header
                this._hasHistory = true;
            }
            hist.push(historyArry[i].replace(this._query, "<b>" + this._query + "</b>"));//bold the matched letters
            count++;
        }
        if (count > 5)//max 5 history
            break;
    }
    if (this._hasHistory == undefined)
        return advSearch.concat(ySearch);
    else
        return hist.concat(advSearch.concat(ySearch));

}


//------------------------------------------------------------------------------------------
//			SHOW PREFERENCES DIALOG
//------------------------------------------------------------------------------------------

com_zimbra_searchauto.prototype.doubleClicked = function() {
    this.singleClicked();
};

com_zimbra_searchauto.prototype.singleClicked = function() {
    this.showPrefDialog();
}
com_zimbra_searchauto.prototype.showPrefDialog =
function() {
    //if zimlet dialog already exists...
    if (this.pbDialog) {
        this.pbDialog.popup();
        return;
    }
    this.pView = new DwtComposite(this.getShell());
    this.pView.getHtmlElement().innerHTML = this.createPrefView();

    if (this.getUserProperty("turnONAutoComplete") == "true") {
        document.getElementById("turnONAutoCompleteId").checked = true;
    }

    var clrHistoryBtn = new DwtDialog_ButtonDescriptor("sa_clearHistoryBtnId", "Clear History", DwtDialog.ALIGN_LEFT);
    this.pbDialog = this._createDialog({title:"Search History Zimlet Preferences", view:this.pView, standardButtons:[DwtDialog.OK_BUTTON],extraButtons:[clrHistoryBtn]});
    this.pbDialog.setButtonListener(DwtDialog.OK_BUTTON, new AjxListener(this, this._okBtnListner));
    this.pbDialog.setButtonListener("sa_clearHistoryBtnId", new AjxListener(this, this._clrHisBtnListner));
    this.pbDialog.popup();

};
com_zimbra_searchauto.prototype._clrHisBtnListner =
function() {
    this.setUserProperty("history", "", true);
}
com_zimbra_searchauto.prototype._okBtnListner =
function() {
	this._reloadRequired = false;
    if (document.getElementById("turnONAutoCompleteId").checked) {
		if(!this.searchAutoCompleteON){
			this._reloadRequired = true;
		}
        this.setUserProperty("turnONAutoComplete", "true", true);

    } else {
        this.setUserProperty("turnONAutoComplete", "false", true);
		if(this.searchAutoCompleteON)
			this._reloadRequired = true;
    }
    this.pbDialog.popdown();
	if(this._reloadRequired) {
		window.onbeforeunload = null;
		var url = AjxUtil.formatUrl({});
		ZmZimbraMail.sendRedirect(url);
	}

}

com_zimbra_searchauto.prototype.createPrefView =
function() {
    var html = new Array();
    var i = 0;
    html[i++] = "<DIV>";
    html[i++] = "<input id='turnONAutoCompleteId'  type='checkbox'/>Turn ON Auto-Complete (changing this would refresh the browser)";
    html[i++] = "</DIV>";
    return html.join("");

}

