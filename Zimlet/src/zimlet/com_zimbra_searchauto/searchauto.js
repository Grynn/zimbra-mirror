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
com_zimbra_searchauto.prototype.onKeyPressSearchField =
function(val) {
    this.onSearchButtonClick(val);
}
com_zimbra_searchauto.prototype.onSearchButtonClick =
function(val) {
    if (!this.searchAutoCompleteON)
        return;
    if (val == undefined || val == "")
        return;

    var currHistory = this.getUserProperty("history");
    if (("::" + currHistory + "::").indexOf("::" + val + "::") == -1) {//dont add dupes
		//load to the front of the string to get latest
        this.setUserProperty("history",  val + "::" + currHistory , true);
		this._manageHistory();//make sure to store upto 1000 unique items, remove older ones
    }


}

//if history count goes >1000, this restores it back to 800(i.e. 200 new space)
com_zimbra_searchauto.prototype._manageHistory =
function() {
    var tmp = this.getUserProperty("history").split("::");
	if(tmp.length >com_zimbra_searchauto.maxHistoryItems){//store a max of 1000 unique items
		var newHistory = "";
		for(var i=0; i < (com_zimbra_searchauto.maxHistoryItems+200); i++) {
				newHistory = newHistory +"::"+ tmp[i];
		}
		 this.setUserProperty("history",  newHistory, true);

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
    if (event.keyCode != 40 && event.keyCode != 38) {//redirect(yet to handle keypress for enterkey)
        this.showHistory();
        return;
    } else if (event.keyCode == 13 || event.keyCode == 3) {
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
        this._onclick();
        return;
    }

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
com_zimbra_searchauto.prototype.createAutoCompleteList =
function() {
    var result = this.returnHistory();
    var startStr = "<ul id='sa_ulist'>";
    var endStr = "</ul>";
    var li = "";
    this._currentHoverNo = -1;
    this._listCollapsed = true;
    this._selectedItemId = "";
    this._totalListItems = result.length;
    if (result.length == 0) {
        this._ctrlDiv.innerHTML = (startStr + li + endStr);
        return;
    }

    for (var i = 0; i < result.length; i++) {
        li = li + "<li id='autoListItem_" + i + "'>" + result[i] + "</li>";
    }

    this._ctrlDiv.innerHTML = startStr + li + endStr;
    var lis = this._ctrlDiv.getElementsByTagName("li");
    for (var i = 0; i < lis.length; i++) {
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
    document.getElementById("autoCompleteHistoryContainerID").style.display = "block";
    document.getElementById("searchautoCtrl").style.display = "block";
    this._listCollapsed = false;
    this.addTimer();

}
com_zimbra_searchauto.prototype.addTimer =
function() {
    if (this.timer) {//clear previous timer if any
        clearTimeout(this.timer);
    }
    this.timer = setTimeout(AjxCallback.simpleClosure(this.hideContainer, this), 3000);//collapse after timeout

}
com_zimbra_searchauto.prototype.hideContainer =
function() {
    document.getElementById("autoCompleteHistoryContainerID").style.display = "none";
    document.getElementById("searchautoCtrl").style.display = "none";
    this._listCollapsed = true;
}
com_zimbra_searchauto.prototype._onmouseover =
function(id) {
    if (this._selectedItemId != "") {
        //when mouse is used after some down-arrow selection, clear the down-arrow selection.
        document.getElementById(this._selectedItemId).style.backgroundColor = "white";
    }
    this._selectedItemId = id;
    document.getElementById(id).style.backgroundColor = "gainsboro";
    this.addTimer();
}
com_zimbra_searchauto.prototype._onmouseout =
function(id) {
    document.getElementById(id).style.backgroundColor = "white";
}
com_zimbra_searchauto.prototype._onclick =
function(id) {

    if (!this._listCollapsed) {
        if (this._selectedItemId != "") {//replace the selected list's value
            this.searchField.value = document.getElementById(this._selectedItemId).innerHTML;
        }
    }
	//add to history(if unique)
	this.onKeyPressSearchField( this.searchField.value);
	//search..
    this.hideContainer();
    var getHtml = appCtxt.get(ZmSetting.VIEW_AS_HTML);
    appCtxt.getSearchController().search({query: this.searchField.value, userText: true, getHtml: getHtml});
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

com_zimbra_searchauto.prototype.returnHistory =
function() {

    var query = document.getElementById('mainSearchInputFieldID').value;
    var historyArry = this.getUserProperty("history").split("::");

    if (query == "")
        return [];
    var res = [ "from:(" + query + ")", "cc:(" + query + ")","subject:(" + query + ")", "from:(@" + query + ")", query + " has:attachment"];
    var sp = query.indexOf(" ");
    if (sp != -1) {
        var len = query.length;
        var f = query.substring(0, sp);
        var s = query.substring(sp + 1, query.length);
        res.push("from:(" + f + ") subject:(" + s + ")");
        res.push("from:(" + s + ") subject:(" + f + ")");
    }

    res.push("----Search History----");
	var count = 0;
    for (var i = 0; i < historyArry.length; i++) {
        if (historyArry[i].indexOf(query) == 0 || (historyArry[i].indexOf(" " + query) > 0) || (historyArry[i].indexOf(":" + query) > 0)) {
            res.push(historyArry[i]);
			count++;
        }
		if(count >9)//max 10 history
			break;
    }

    return res;
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
    this.pView = new DwtComposite(this.getShell());
    this.pView.setSize("200", "100");
    this.pView.getHtmlElement().innerHTML = this.createPrefView();

    if (this.getUserProperty("turnONAutoComplete") == "true"){
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
    if (document.getElementById("turnONAutoCompleteId").checked) {
        this.setUserProperty("turnONAutoComplete", "true", true);
    } else {
        this.setUserProperty("turnONAutoComplete", "false", true);
    }
    this.pbDialog.popdown();

}

com_zimbra_searchauto.prototype.createPrefView =
function() {
    var html = new Array();
    var i = 0;
    html[i++] = "<DIV>";
    html[i++] = "<input id='turnONAutoCompleteId'  type='checkbox'/>Turn ON Auto-Complete";
    html[i++] = "</DIV>";
    return html.join("");

}

