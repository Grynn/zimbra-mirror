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


function com_zimbra_emailquotes() {
}

com_zimbra_emailquotes.prototype = new ZmZimletBase();
com_zimbra_emailquotes.prototype.constructor = com_zimbra_emailquotes;

com_zimbra_emailquotes.prototype.init =
function() {
    this.currentQuote = "";
    this.turnEmailQuotesZimletON = this.getUserProperty("turnEmailQuotesZimletON") == "true";
    if (!this.turnEmailQuotesZimletON) {
        return;
    }

    this._initializeVariables();
};

com_zimbra_emailquotes.prototype._initializeVariables =
function() {
    this.equotes_userSelections = this.getUserProperty("equotes_userSelections");
    this.equotes_defaultSelections = this.getUserProperty("equotes_defaultSelections");
    this.currentQuote = "";
    this._setBaseFeedUrl();
    this._getFeed(this._baseFeed);
};

com_zimbra_emailquotes.prototype._setBaseFeedUrl =
function() {
    var url_1 = "http://www.quotedb.com/quote/quote.php?action=random_quote_rss";
    var url_2 = "&=&";
    this._selectedItems = this.equotes_userSelections;
    if (this._selectedItems == "")
        this._selectedItems = this.equotes_defaultSelections;

    var itms = this._selectedItems.split(",");
    var args = "";
    for (var j = 0; j < itms.length; j++) {
        args = args + "&c[" + itms[j] + "]=" + itms[j];
    }

    this._baseFeed = url_1 + args + url_2;
};

com_zimbra_emailquotes.prototype._getFeed =
function(feed) {
    var pfeed = ZmZimletBase.PROXY + AjxStringUtil.urlComponentEncode(feed);
    AjxRpc.invoke(null, pfeed, null, new AjxCallback(this, this._feedHandler), true);
};

//we dont get valid RSS feed due to dtd issue, so parse the text content instead
com_zimbra_emailquotes.prototype.getXmlFromText =
function(text) {
    var xmlDoc = null;
    if (AjxEnv.isIE) {
        try { //Firefox, Mozilla, Opera, etc.
            xmlDoc = new ActiveXObject("Microsoft.XMLDOM");
            xmlDoc.async = "false";
            xmlDoc.loadXML(text);
        } catch(e) {
        }
    } else {
        try { //Firefox, Mozilla, Opera, etc.
            var parser = new DOMParser();
            xmlDoc = parser.parseFromString(text, "text/xml");
        } catch(e) {
        }
    }

    return xmlDoc;
};

com_zimbra_emailquotes.prototype._feedHandler =
function(result) {
    var xmlObject = null;

    if (result.xml && result.xml.childNodes.length > 0) {
        xmlObject = result.xml;
    } else {//remove dtd and then parse the text
        var txt = result.text.replace("<!DOCTYPE rss PUBLIC \"-//Netscape Communications//DTD RSS 0.91//EN\" \"http://my.netscape.com/publish/formats/rss-0.91.dtd\">", "");
        xmlObject = this.getXmlFromText(txt);
    }

    if (xmlObject == null)
        return;

    var items = xmlObject.getElementsByTagName("item");
    for (var i = 0; i < items.length; i++) {
        try {
            var title = desc = "";
            var titleObj = items[i].getElementsByTagName("title")[0].firstChild;
            var descObj = items[i].getElementsByTagName("description")[0].firstChild;
            if (titleObj.textContent) {
                title = titleObj.textContent;
                desc = descObj.textContent;
            } else if (titleObj.text) {
                title = titleObj.text;
                desc = descObj.text;
            }

            if (i == items.length - 1)
                this.currentQuote = "\n\n" + desc + " - " + title;
        } catch(e) {
        }
    }
};

//called by ZmComposeView.js _getSignature method
com_zimbra_emailquotes.prototype.getRandomQuote =
function(msgs) {
    return this.currentQuote;
};

com_zimbra_emailquotes.prototype.doubleClicked = function() {
    this.singleClicked();
};

com_zimbra_emailquotes.prototype.singleClicked = function() {
    this._showPreferenceDlg();
};
com_zimbra_emailquotes.prototype._showPreferenceDlg = function() {
    //if zimlet dialog already exists...
    if (this._preferenceDialog) {
        this._setZimletCurrentPreferences();
        this._preferenceDialog.popup();
        return;
    }
    this._preferenceView = new DwtComposite(this.getShell());
    this._preferenceView.setSize("520", "290");
    this._preferenceView.getHtmlElement().style.background = "white";
    this._preferenceView.getHtmlElement().style.overflow = "auto";
    this._preferenceView.getHtmlElement().innerHTML = this._createPrefView();
    this._getAbsHoursMenu();
    this._preferenceDialog = this._createDialog({title:"'Email Quotes' Zimlet Preferences", view:this._preferenceView, standardButtons:[DwtDialog.OK_BUTTON, DwtDialog.CANCEL_BUTTON]});
    this._preferenceDialog.setButtonListener(DwtDialog.OK_BUTTON, new AjxListener(this, this._okPreferenceBtnListener));
    this._preferenceDialog.setButtonListener(DwtDialog.CANCEL_BUTTON, new AjxListener(this, this._cancelPreferenceBtnListener));

    this._setZimletCurrentPreferences();
    this._preferenceDialog.popup();
};


com_zimbra_emailquotes.prototype._createPrefView =
function() {
    var html = new Array();
    var i = 0;
    html[i++] = "<BR>";
    html[i++] = "<TABLE>";
    html[i++] = "<TR><TD colspan=2><B>Select one or more quotes categories of your choice(use ctrl+click to select multiple items):</B></TD></TR>";
    html[i++] = "<TR><TD id='equotes_typeMenuTD'></TD><TD><span style=\"font: bold 12pt 'arial', 'sans-serif';color:blue;\"  id='equotes_display'> </span></TD></TR>";
    html[i++] = "</TABLE>";
    html[i++] = "<BR>";
    html[i++] = "<DIV>";
    html[i++] = "<TABLE>";
    html[i++] = "<TR><TD><input id='turnONEmailQuotesChkbox'  type='checkbox'/>Turn ON 'Email Quotes'-Zimlet</TD></TR>";
    html[i++] = "</TABLE>";
    html[i++] = "<BR>";
    html[i++] = "<TABLE>";
    html[i++] = "<TR><TD><FONT size=1>PS:</FONT></TD></TR>";
    html[i++] = "<TR><TD><FONT size=1>- Quotes are from: http://www.quotedb.com/</FONT></TD></TR>";
    html[i++] = "<TR><TD><FONT size='1pt'>- Changes to the above preferences would refresh the browser</FONT></TD></TR>";
    html[i++] = "</TABLE>";
    html[i++] = "</DIV>";
    return html.join("");

};

com_zimbra_emailquotes.prototype._getAbsHoursMenu =
function() {
    var nameValueStr = "\"\"=Any,54=Age,49=America,69=Anger,96=Animals,44=Art,40=Beauty,50=Children,8=Comedy,126=Computers,38=Education,121=Emotion,98=Envy,80=Experience,31=Faith,";
    nameValueStr = nameValueStr + "119=Fame,86=Family,48=Fear,77=Food,13=Freedom,6=Friendship,2=God,7=Government,45=Happiness,91=Health,117=History,66=Holidays,115=Hope,74=Humor,";
    nameValueStr = nameValueStr + "101=Inspiration,93=Justice,27=Leadership,43=Life &amp;Death,5=Love,14=Miscellaneous,37=Money,11=Morality,28=Patriotism,62=Philosophy,";
    nameValueStr = nameValueStr + "20=Politics,109=Power,84=Pride,29=Race,39=Reading,60=Religion,71=Risk,15=Science,76=Sleep,75=Sports,9=Success,79=Talking,73=Thinking,";
    nameValueStr = nameValueStr + "51=Time,33=Truth &amp; Lies,64=Virtue,32=War &amp; Peace,81=Wisdom,34=Work,26=Writing";

    var items = nameValueStr.split(",");
    var html = new Array();
    var i = 0;
    html[i++] = "<select id=\"equotes_typeMenu\" multiple='' size=10>";
    for (var j = 0; j < items.length; j++) {
        var itm = items[j].split("=");
        html[i++] = "<option value=" + itm[0] + ">" + itm[1] + "</option>";
    }
    html[i++] = "</select>";
    var reminderCell = document.getElementById("equotes_typeMenuTD");
    if (reminderCell)
        reminderCell.innerHTML = html.join("");

};

com_zimbra_emailquotes.prototype._setZimletCurrentPreferences =
function() {
    this._initializeVariables();
    if (this.turnEmailQuotesZimletON) {
        document.getElementById("turnONEmailQuotesChkbox").checked = true;
    }
    this._highlightCurrentItems();
    this._setQuoteToDlgUI();

    //get another feed that will be used by next rotation
    this._getFeed(this._baseFeed);
    this._rotateTimer = setInterval(AjxCallback.simpleClosure(this._rotateQuotes, this), 6000);
};

com_zimbra_emailquotes.prototype._rotateQuotes =
function() {
    this._setQuoteToDlgUI();
    this._getFeed(this._baseFeed);	//call again
};

com_zimbra_emailquotes.prototype._setQuoteToDlgUI =
function() {
    this._currColor = document.getElementById("equotes_display").style.color;
    var colors = ["red", "blue", "green", "brown", "#FF66FF", "purple","#6E6E6E"];
    while (true) {
        var randomnumber = Math.floor(Math.random() * colors.length);
        if (this._currColor != colors[randomnumber]) {
            this._currColor = colors[randomnumber];
            break;
        }
    }
    document.getElementById("equotes_display").style.color = this._currColor;
    document.getElementById("equotes_display").innerHTML = this.currentQuote;
};

com_zimbra_emailquotes.prototype._okPreferenceBtnListener =
function() {
    this._reloadRequired = false;
    if (document.getElementById("turnONEmailQuotesChkbox").checked) {
        if (!this.turnEmailQuotesZimletON) {
            this._reloadRequired = true;
        }
        this.setUserProperty("turnEmailQuotesZimletON", "true", true);

    } else {
        this.setUserProperty("turnEmailQuotesZimletON", "false", true);
        if (this.turnEmailQuotesZimletON)
            this._reloadRequired = true;
    }
    var sitms = this._getSelectedItems();
    this.setUserProperty("equotes_userSelections", sitms, true);

    clearInterval(this._rotateTimer);//clear interval
    this._preferenceDialog.popdown();

    if (this._reloadRequired) {
        this._reloadBrowser();
    }
};

com_zimbra_emailquotes.prototype._cancelPreferenceBtnListener =
function() {
    clearInterval(this._rotateTimer);//clear interval
    this._preferenceDialog.popdown();


};

com_zimbra_emailquotes.prototype._getSelectedItems =
function() {
    var me = document.getElementById("equotes_typeMenu");
    var vals = "";
    for (var i = 0; i < me.options.length; i++) {
        if (me.options[i].selected) {
            if (vals == "")
                vals = me.options[i].value;
            else
                vals = vals + "," + me.options[i].value;
        }
    }
    this._selectedItems = vals;
    return vals;
};

com_zimbra_emailquotes.prototype._highlightCurrentItems =
function() {
    var userSelections = this._selectedItems.split(",");
    var me = document.getElementById("equotes_typeMenu");
    for (var i = 0; i < me.options.length; i++) {
        var optn = me.options[i];
        var optnVal = optn.value;
        for (var j = 0; j < userSelections.length; j++) {
            if (userSelections[j] == optnVal) {
                optn.selected = true;
            }
        }
    }

};

com_zimbra_emailquotes.prototype._reloadBrowser =
function() {
    window.onbeforeunload = null;
    var url = AjxUtil.formatUrl({});
    ZmZimbraMail.sendRedirect(url);
};