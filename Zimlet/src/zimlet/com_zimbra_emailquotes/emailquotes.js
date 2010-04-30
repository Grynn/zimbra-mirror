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
 */

/**
 * Constructor.
 * 
 * @author Raja Rao DV
 */
function com_zimbra_emailquotes_HandlerObject() {
}

com_zimbra_emailquotes_HandlerObject.prototype = new ZmZimletBase();
com_zimbra_emailquotes_HandlerObject.prototype.constructor = com_zimbra_emailquotes_HandlerObject;

/**
 * Simplify handler object
 *
 */
var EmailQuotesZimlet = com_zimbra_emailquotes_HandlerObject;

/**
 * Defines the "enabled" user property.
 */
EmailQuotesZimlet.USER_PROP_ENABLED = "turnEmailQuotesZimletON";
/**
 * Defines the "user selections" user property.
 */
EmailQuotesZimlet.USER_PROP_USER_SELECTIONS = "equotes_userSelections";
/**
 * Defines the "default selections" user property.
 */
EmailQuotesZimlet.USER_PROP_DEFAULT_SELECTIONS = "equotes_defaultSelections";

/**
 * Initializes the zimlet.
 * 
 */
EmailQuotesZimlet.prototype.init =
function() {
    this.currentQuote = "";
    this.turnEmailQuotesZimletON = this.getUserProperty(EmailQuotesZimlet.USER_PROP_ENABLED) == "true";
    if (!this.turnEmailQuotesZimletON) {
        return;
    }

    this._initializeVariables();
};

/**
 * Initializes the variables.
 * 
 */
EmailQuotesZimlet.prototype._initializeVariables =
function() {
    this.equotes_userSelections = this.getUserProperty(EmailQuotesZimlet.USER_PROP_USER_SELECTIONS);
    this.equotes_defaultSelections = this.getUserProperty(EmailQuotesZimlet.USER_PROP_DEFAULT_SELECTIONS);
    this.currentQuote = "";
    this._setBaseFeedUrl();
    this._getFeed(this._baseFeed);
};

/**
 * Sets the base feed URL.
 * 
 */
EmailQuotesZimlet.prototype._setBaseFeedUrl =
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

/**
 * Gets the feed.
 * 
 * @param	{string}	feed		the feed url
 */
EmailQuotesZimlet.prototype._getFeed =
function(feed) {
    var pfeed = ZmZimletBase.PROXY + AjxStringUtil.urlComponentEncode(feed);
    AjxRpc.invoke(null, pfeed, null, new AjxCallback(this, this._feedHandler), true);
};

/**
 * Gets the XML from text. Note: we do not get valid RSS feed due to dtd issue, so parse the text content instead.
 * 
 * @param	{string}	text		the text
 * @return	{object}	the xml doc
 */
EmailQuotesZimlet.prototype.getXmlFromText =
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

/**
 * Handles the feed response.
 * 
 * @param	{object}	result		the result
 */
EmailQuotesZimlet.prototype._feedHandler =
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

/**
 * Appends extra signature information. Called by the framework.
 * 
 * @param	{array}	buffer		a buffer to append to
 */
EmailQuotesZimlet.prototype.appendExtraSignature =
function(buffer) {
	if(this.turnEmailQuotesZimletON)
	    buffer.push(this.currentQuote);
};

/**
 * Called by the framework on double-click.
 */
EmailQuotesZimlet.prototype.doubleClicked = function() {
    this.singleClicked();
};

/**
 * Called by the framework on single-click.
 */
EmailQuotesZimlet.prototype.singleClicked = function() {
    this._showPreferenceDlg();
};

/**
 * Shows the preferences dialog.
 * 
 */
EmailQuotesZimlet.prototype._showPreferenceDlg = function() {
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
    this._getCategoriesMenu();
    var dialogTitle = this.getMessage("EmailQuotesZimlet_dialog_preferences_title");
    this._preferenceDialog = this._createDialog({title:dialogTitle, view:this._preferenceView, standardButtons:[DwtDialog.OK_BUTTON, DwtDialog.CANCEL_BUTTON]});
    this._preferenceDialog.setButtonListener(DwtDialog.OK_BUTTON, new AjxListener(this, this._okPreferenceBtnListener));
    this._preferenceDialog.setButtonListener(DwtDialog.CANCEL_BUTTON, new AjxListener(this, this._cancelPreferenceBtnListener));

    this._setZimletCurrentPreferences();
    this._preferenceDialog.popup();
};

/**
 * Creates the preferences view.
 * 
 * @see		_showPreferenceDlg
 */
EmailQuotesZimlet.prototype._createPrefView =
function() {
    var html = new Array();
    var i = 0;
    html[i++] = "<BR>";
    html[i++] = "<TABLE>";
    html[i++] = "<TR><TD colspan=2><B>";
    html[i++] = this.getMessage("EmailQuotesZimlet_dialog_preferences_select_text");
    html[i++] = "</B></TD></TR>";
    html[i++] = "<TR><TD id='equotes_typeMenuTD'></TD><TD><span style=\"font: bold 12pt 'arial', 'sans-serif';color:blue;\"  id='equotes_display'> </span></TD></TR>";
    html[i++] = "</TABLE>";
    html[i++] = "<BR>";
    html[i++] = "<DIV>";
    html[i++] = "<TABLE>";
    html[i++] = "<TR><TD><input id='turnONEmailQuotesChkbox' type='checkbox'/>";
    html[i++] = this.getMessage("EmailQuotesZimlet_dialog_preferences_enable");
    html[i++] = "</TD></TR>";
    html[i++] = "</TABLE>";
    html[i++] = "<TABLE>";
    html[i++] = "<TR><TD><FONT size=1>";
    html[i++] = this.getMessage("EmailQuotesZimlet_dialog_preferences_note");
    html[i++] = "</FONT></TD></TR>";
    html[i++] = "<TR><TD><FONT size=1>";
    html[i++] = this.getMessage("EmailQuotesZimlet_dialog_preferences_fromqdotcom");
    html[i++] = "</FONT></TD></TR>";
    html[i++] = "<TR><TD><FONT size='1pt'>";
    html[i++] = this.getMessage("EmailQuotesZimlet_dialog_preferences_requirerefresh");
    html[i++] = "</FONT></TD></TR>";
    html[i++] = "</TABLE>";
    html[i++] = "</DIV>";
    return html.join("");

};

/**
 * Gets the absolute hours menu.
 */
EmailQuotesZimlet.prototype._getCategoriesMenu =
function() {
	var items = this._getCategories();
	
	var html = new Array();
    var i = 0;
    html[i++] = "<select id=\"equotes_typeMenu\" multiple='' style='width: 150px;' size=10>";
    for (var j = 0; j < items.length; j++) {
        var itm = items[j];
        html[i++] = "<option value=" + itm[0] + ">" + itm[1] + "</option>";
    }
    html[i++] = "</select>";
    var reminderCell = document.getElementById("equotes_typeMenuTD");
    if (reminderCell)
        reminderCell.innerHTML = html.join("");

};

/**
 * Gets the categories array.
 * 
 * @return	{array}	an array of categories
 */
EmailQuotesZimlet.prototype._getCategories =
function() {
	if (this._categories != null)
		return	this._categories;
		
	var tmpCats = new Array();
	var idx = 0;
	
	tmpCats[idx] = new Array();
	tmpCats[idx][0] = "\"\"";
	tmpCats[idx++][1] = this.getMessage("EmailQuotesZimlet_category_any");

	tmpCats[idx] = new Array();
	tmpCats[idx][0] = "54";
	tmpCats[idx++][1] = this.getMessage("EmailQuotesZimlet_category_age");

	tmpCats[idx] = new Array();
	tmpCats[idx][0] = "49";
	tmpCats[idx++][1] = this.getMessage("EmailQuotesZimlet_category_america");

	tmpCats[idx] = new Array();
	tmpCats[idx][0] = "69";
	tmpCats[idx++][1] = this.getMessage("EmailQuotesZimlet_category_anger");

	tmpCats[idx] = new Array();
	tmpCats[idx][0] = "96";
	tmpCats[idx++][1] = this.getMessage("EmailQuotesZimlet_category_animals");

	tmpCats[idx] = new Array();
	tmpCats[idx][0] = "44";
	tmpCats[idx++][1] = this.getMessage("EmailQuotesZimlet_category_art");

	tmpCats[idx] = new Array();
	tmpCats[idx][0] = "40";
	tmpCats[idx++][1] = this.getMessage("EmailQuotesZimlet_category_beauty");

	tmpCats[idx] = new Array();
	tmpCats[idx][0] = "50";
	tmpCats[idx++][1] = this.getMessage("EmailQuotesZimlet_category_children");

	tmpCats[idx] = new Array();
	tmpCats[idx][0] = "8";
	tmpCats[idx++][1] = this.getMessage("EmailQuotesZimlet_category_comedy");

	tmpCats[idx] = new Array();
	tmpCats[idx][0] = "126";
	tmpCats[idx++][1] = this.getMessage("EmailQuotesZimlet_category_computers");

	tmpCats[idx] = new Array();
	tmpCats[idx][0] = "38";
	tmpCats[idx++][1] = this.getMessage("EmailQuotesZimlet_category_education");

	tmpCats[idx] = new Array();
	tmpCats[idx][0] = "121";
	tmpCats[idx++][1] = this.getMessage("EmailQuotesZimlet_category_emotion");

	tmpCats[idx] = new Array();
	tmpCats[idx][0] = "98";
	tmpCats[idx++][1] = this.getMessage("EmailQuotesZimlet_category_envy");

	tmpCats[idx] = new Array();
	tmpCats[idx][0] = "80";
	tmpCats[idx++][1] = this.getMessage("EmailQuotesZimlet_category_experience");

	tmpCats[idx] = new Array();
	tmpCats[idx][0] = "31";
	tmpCats[idx++][1] = this.getMessage("EmailQuotesZimlet_category_faith");

	tmpCats[idx] = new Array();
	tmpCats[idx][0] = "119";
	tmpCats[idx++][1] = this.getMessage("EmailQuotesZimlet_category_fame");

	tmpCats[idx] = new Array();
	tmpCats[idx][0] = "86";
	tmpCats[idx++][1] = this.getMessage("EmailQuotesZimlet_category_family");

	tmpCats[idx] = new Array();
	tmpCats[idx][0] = "48";
	tmpCats[idx++][1] = this.getMessage("EmailQuotesZimlet_category_fear");

	tmpCats[idx] = new Array();
	tmpCats[idx][0] = "77";
	tmpCats[idx++][1] = this.getMessage("EmailQuotesZimlet_category_food");

	tmpCats[idx] = new Array();
	tmpCats[idx][0] = "13";
	tmpCats[idx++][1] = this.getMessage("EmailQuotesZimlet_category_freedom");

	tmpCats[idx] = new Array();
	tmpCats[idx][0] = "6";
	tmpCats[idx++][1] = this.getMessage("EmailQuotesZimlet_category_friendship");

	tmpCats[idx] = new Array();
	tmpCats[idx][0] = "2";
	tmpCats[idx++][1] = this.getMessage("EmailQuotesZimlet_category_god");

	tmpCats[idx] = new Array();
	tmpCats[idx][0] = "7";
	tmpCats[idx++][1] = this.getMessage("EmailQuotesZimlet_category_government");

	tmpCats[idx] = new Array();
	tmpCats[idx][0] = "45";
	tmpCats[idx++][1] = this.getMessage("EmailQuotesZimlet_category_happiness");

	tmpCats[idx] = new Array();
	tmpCats[idx][0] = "91";
	tmpCats[idx++][1] = this.getMessage("EmailQuotesZimlet_category_health");

	tmpCats[idx] = new Array();
	tmpCats[idx][0] = "117";
	tmpCats[idx++][1] = this.getMessage("EmailQuotesZimlet_category_history");

	tmpCats[idx] = new Array();
	tmpCats[idx][0] = "66";
	tmpCats[idx++][1] = this.getMessage("EmailQuotesZimlet_category_holidays");

	tmpCats[idx] = new Array();
	tmpCats[idx][0] = "115";
	tmpCats[idx++][1] = this.getMessage("EmailQuotesZimlet_category_hope");

	tmpCats[idx] = new Array();
	tmpCats[idx][0] = "74";
	tmpCats[idx++][1] = this.getMessage("EmailQuotesZimlet_category_humor");

	tmpCats[idx] = new Array();
	tmpCats[idx][0] = "101";
	tmpCats[idx++][1] = this.getMessage("EmailQuotesZimlet_category_inspiration");

	tmpCats[idx] = new Array();
	tmpCats[idx][0] = "93";
	tmpCats[idx++][1] = this.getMessage("EmailQuotesZimlet_category_justice");

	tmpCats[idx] = new Array();
	tmpCats[idx][0] = "27";
	tmpCats[idx++][1] = this.getMessage("EmailQuotesZimlet_category_leadership");

	tmpCats[idx] = new Array();
	tmpCats[idx][0] = "43";
	tmpCats[idx++][1] = this.getMessage("EmailQuotesZimlet_category_lifeanddeath");

	tmpCats[idx] = new Array();
	tmpCats[idx][0] = "5";
	tmpCats[idx++][1] = this.getMessage("EmailQuotesZimlet_category_love");

	tmpCats[idx] = new Array();
	tmpCats[idx][0] = "14";
	tmpCats[idx++][1] = this.getMessage("EmailQuotesZimlet_category_miscellaneous");

	tmpCats[idx] = new Array();
	tmpCats[idx][0] = "37";
	tmpCats[idx++][1] = this.getMessage("EmailQuotesZimlet_category_money");

	tmpCats[idx] = new Array();
	tmpCats[idx][0] = "11";
	tmpCats[idx++][1] = this.getMessage("EmailQuotesZimlet_category_morality");

	tmpCats[idx] = new Array();
	tmpCats[idx][0] = "28";
	tmpCats[idx++][1] = this.getMessage("EmailQuotesZimlet_category_patriotism");

	tmpCats[idx] = new Array();
	tmpCats[idx][0] = "62";
	tmpCats[idx++][1] = this.getMessage("EmailQuotesZimlet_category_philosophy");

	tmpCats[idx] = new Array();
	tmpCats[idx][0] = "20";
	tmpCats[idx++][1] = this.getMessage("EmailQuotesZimlet_category_politics");

	tmpCats[idx] = new Array();
	tmpCats[idx][0] = "109";
	tmpCats[idx++][1] = this.getMessage("EmailQuotesZimlet_category_power");

	tmpCats[idx] = new Array();
	tmpCats[idx][0] = "84";
	tmpCats[idx++][1] = this.getMessage("EmailQuotesZimlet_category_pride");

	tmpCats[idx] = new Array();
	tmpCats[idx][0] = "29";
	tmpCats[idx++][1] = this.getMessage("EmailQuotesZimlet_category_race");

	tmpCats[idx] = new Array();
	tmpCats[idx][0] = "39";
	tmpCats[idx++][1] = this.getMessage("EmailQuotesZimlet_category_reading");

	tmpCats[idx] = new Array();
	tmpCats[idx][0] = "60";
	tmpCats[idx++][1] = this.getMessage("EmailQuotesZimlet_category_religion");

	tmpCats[idx] = new Array();
	tmpCats[idx][0] = "71";
	tmpCats[idx++][1] = this.getMessage("EmailQuotesZimlet_category_risk");

	tmpCats[idx] = new Array();
	tmpCats[idx][0] = "15";
	tmpCats[idx++][1] = this.getMessage("EmailQuotesZimlet_category_science");

	tmpCats[idx] = new Array();
	tmpCats[idx][0] = "76";
	tmpCats[idx++][1] = this.getMessage("EmailQuotesZimlet_category_sleep");

	tmpCats[idx] = new Array();
	tmpCats[idx][0] = "75";
	tmpCats[idx++][1] = this.getMessage("EmailQuotesZimlet_category_sports");

	tmpCats[idx] = new Array();
	tmpCats[idx][0] = "9";
	tmpCats[idx++][1] = this.getMessage("EmailQuotesZimlet_category_success");

	tmpCats[idx] = new Array();
	tmpCats[idx][0] = "79";
	tmpCats[idx++][1] = this.getMessage("EmailQuotesZimlet_category_talking");

	tmpCats[idx] = new Array();
	tmpCats[idx][0] = "73";
	tmpCats[idx++][1] = this.getMessage("EmailQuotesZimlet_category_thinking");

	tmpCats[idx] = new Array();
	tmpCats[idx][0] = "51";
	tmpCats[idx++][1] = this.getMessage("EmailQuotesZimlet_category_time");

	tmpCats[idx] = new Array();
	tmpCats[idx][0] = "33";
	tmpCats[idx++][1] = this.getMessage("EmailQuotesZimlet_category_truthandlies");

	tmpCats[idx] = new Array();
	tmpCats[idx][0] = "64";
	tmpCats[idx++][1] = this.getMessage("EmailQuotesZimlet_category_virtue");

	tmpCats[idx] = new Array();
	tmpCats[idx][0] = "32";
	tmpCats[idx++][1] = this.getMessage("EmailQuotesZimlet_category_warandpeace");

	tmpCats[idx] = new Array();
	tmpCats[idx][0] = "81";
	tmpCats[idx++][1] = this.getMessage("EmailQuotesZimlet_category_wisdom");

	tmpCats[idx] = new Array();
	tmpCats[idx][0] = "34";
	tmpCats[idx++][1] = this.getMessage("EmailQuotesZimlet_category_work");

	tmpCats[idx] = new Array();
	tmpCats[idx][0] = "26";
	tmpCats[idx++][1] = this.getMessage("EmailQuotesZimlet_category_writing");

    this._categories = tmpCats;
		
	return	this._categories;
};

/**
 * Sets the preferences.
 * 
 */
EmailQuotesZimlet.prototype._setZimletCurrentPreferences =
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

/**
 * Rotate quotes.
 * 
 */
EmailQuotesZimlet.prototype._rotateQuotes =
function() {
    this._setQuoteToDlgUI();
    this._getFeed(this._baseFeed);	//call again
};

/**
 * Sets the quote dialog.
 * 
 */
EmailQuotesZimlet.prototype._setQuoteToDlgUI =
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

/**
 * OK button listener.
 * 
 * @see		_showPreferenceDlg
 */
EmailQuotesZimlet.prototype._okPreferenceBtnListener =
function() {
    this._reloadRequired = false;
    if (document.getElementById("turnONEmailQuotesChkbox").checked) {
        if (!this.turnEmailQuotesZimletON) {
            this._reloadRequired = true;
        }
        this.setUserProperty(EmailQuotesZimlet.USER_PROP_ENABLED, "true", true);

    } else {
        this.setUserProperty(EmailQuotesZimlet.USER_PROP_ENABLED, "false", true);
        if (this.turnEmailQuotesZimletON)
            this._reloadRequired = true;
    }
    var sitms = this._getSelectedItems();
    this.setUserProperty(EmailQuotesZimlet.USER_PROP_USER_SELECTIONS, sitms, true);

    clearInterval(this._rotateTimer);//clear interval
    this._preferenceDialog.popdown();

    if (this._reloadRequired) {
        this._reloadBrowser();
    }
};

/**
 * Cancel button listener.
 * 
 * @see		_showPreferenceDlg
 */
EmailQuotesZimlet.prototype._cancelPreferenceBtnListener =
function() {
    clearInterval(this._rotateTimer);//clear interval
    this._preferenceDialog.popdown();


};

/**
 * Gets selected items.
 * 
 */
EmailQuotesZimlet.prototype._getSelectedItems =
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

/**
 * Highlights the current items.
 * 
 */
EmailQuotesZimlet.prototype._highlightCurrentItems =
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

/**
 * Reloads the browser.
 * 
 */
EmailQuotesZimlet.prototype._reloadBrowser =
function() {
    window.onbeforeunload = null;
    var url = AjxUtil.formatUrl({});
    ZmZimbraMail.sendRedirect(url);
};