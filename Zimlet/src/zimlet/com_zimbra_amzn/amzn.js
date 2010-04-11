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
 * Amazon Zimlet. Performs book search and shows "hover" information for ISBN numbers in mail or appointments
 *
 * @author Raja Rao DV (rrao@zimbra.com) - Rewrote most of the Zimlet
 * @author Kevin Henrikson - Original Author
 */

/**
 * Default constructor.
 */
function Com_Zimbra_Amazon_HandlerObject() {
}

Com_Zimbra_Amazon_HandlerObject.prototype = new ZmZimletBase();
Com_Zimbra_Amazon_HandlerObject.prototype.constructor = Com_Zimbra_Amazon_HandlerObject;

/**
 * Simplify Zimlet handler name
 */
var AmazonZimlet = Com_Zimbra_Amazon_HandlerObject;

/**
 * Defines the "alert on checkbox" element.
 */
AmazonZimlet.ELEMENT_RESULTS_DIV_ID = "amznZimlet_bookSearchResultsDiv";




/**
 * This method is called by Zimbra Framework when the tool tip is popped-up.
 *
 * For more details see {@link ZmZimletBase}
 */
AmazonZimlet.prototype.toolTipPoppedUp =
function(spanElement, obj, context, canvas) {
	this._currentTooltipId = Dwt.getNextId();
	canvas.innerHTML = ["<div id='",this._currentTooltipId,"'>",this.getMessage("AmazonZimlet_searching"),"</div>"].join("");
	var searchKeys = obj.replace(/[- A-WY-Z]/ig, '');
	var paramsArray = [
		["Keywords", searchKeys]
	];
	var url = this._getUrlFromJSP(paramsArray);
	var completeUrl = ZmZimletBase.PROXY + AjxStringUtil.urlComponentEncode(url);
	AjxRpc.invoke(null, completeUrl, null, new AjxCallback(this, this._searchCallback, this._currentTooltipId), true);
};

/**
 * Called by Framework when isbn link is clicked. This opens Amazon webpage for that isbn number.
 *
 *  For more details see {@link ZmZimletBase}
 */
AmazonZimlet.prototype.clicked =
function(spanElement, contentObjText, matchContext, event) {
	window.open("http://www.amazon.com/s/ref=nb_sb_noss?url=search-alias%3Dstripbooks&field-keywords=" + AjxStringUtil.urlComponentEncode(contentObjText) + "&x=0&y=0");
};

/**
 * Performs Amazon Book Search
 *
 */
AmazonZimlet.prototype._doAmazonBookSearch =
function() {
	var searchKeys = this._searchField.value;
	var paramsArray = [
		["Keywords",searchKeys]
	];
	var url = this._getUrlFromJSP(paramsArray);
	var completeUrl = ZmZimletBase.PROXY + AjxStringUtil.urlComponentEncode(url);
	AjxRpc.invoke(null, completeUrl, null, new AjxCallback(this, this._searchCallback, AmazonZimlet.ELEMENT_RESULTS_DIV_ID), true);
};

/**
 * Gets AWS url for ItemSearch API. It internally generates AWS HMAC Version 2 Signature and appends it to the url.
 *
 *@params {Array} args A hash array of keys and value parameters that needs to be sent to generate AWS URL.
 * @params {Array} args[String, String] First item of the array is the Key and 2nd item is the value 
 */
AmazonZimlet.prototype._getUrlFromJSP =
function(args) {
	var params = new Array;
	for (var i = 0; i < args.length; i++) {
		var item = args[i];
		params.push(item[0] + "=" + item[1]);
	}
	var url = this.getResource("generateAMZNSignature.jsp") + "?" + params.join("&");
	var response = AjxRpc.invoke(null, url, null, null, true);
	return response.text;
};


/**
 * This method is called when the panel item is double-clicked.
 *
 */
AmazonZimlet.prototype.doubleClicked = function() {
	this.singleClicked();
};

/**
 * This method is called when the panel item is single-clicked.
 *
 */
AmazonZimlet.prototype.singleClicked = function() {
	this._showBookSearchDlg();
};

/**
 * Displays information on multiple books.
 *
 * @param {array} itemList An array of objects representing Book information
 * @param {string} resultsDiv The id of the div into which the generated Html should be inserted into.
 */
AmazonZimlet.prototype._displayBooks =
function(itemList, resultsDiv) {
	var items = itemList;
	//dont use yellow div if its tooltip
	var divClass = (resultsDiv == AmazonZimlet.ELEMENT_RESULTS_DIV_ID) ? "amznZimlet_Yellow" : "";
	if (itemList && (typeof itemList.length == "undefined")) {
		items = [itemList];
	}
	var html = [];
	var j = 0;
	for (var i = 0; i < items.length; i++) {
		var item = items[i];
		var attributes = item.ItemAttributes;
		var imageInfo = item.MediumImage ? item.MediumImage : ( item.SmallImage ? item.SmallImage : null);
		var url = item.DetailPageURL ? item.DetailPageURL.toString() : "";
		html[j++] = ["<div class='",divClass,"'>"].join("");
		html[j++] = "<table width=100%><tr>";
		if (imageInfo != null) {
			html[j++] = ["<td width=130px><a href='",url,"' target='_blank'><img  align=center src='",imageInfo.URL.toString(),
				"' width='",imageInfo.Width.toString(),"px' height='",imageInfo.Height.toString(),"px' /></a></td>"].join("");
		}
		html[j++] = "<td valign=top><table>";
		html[j++] = ["<tr><td class='amznZimlet_HdrTD'>",this.getMessage("AmazonZimlet_title"),"</td><td class='amznZimlet_TDBlack14Bold'>",(attributes.Title ? attributes.Title.toString() : "") ,"</td></tr>"].join("");
		html[j++] = ["<tr><td class='amznZimlet_HdrTD'>",this.getMessage("AmazonZimlet_author"),"</td><td>",(attributes.Author ? attributes.Author.toString() : ""),"</td></tr>"].join("");
		html[j++] = ["<tr><td class='amznZimlet_HdrTD'>",this.getMessage("AmazonZimlet_listPrice"),"</td><td class='amznZimlet_TDBlueBold'>",(attributes.ListPrice ? (attributes.ListPrice.FormattedPrice ? attributes.ListPrice.FormattedPrice.toString() : "") : ""),"</td></tr>"].join("");
		html[j++] = ["<tr><td class='amznZimlet_HdrTD'>",this.getMessage("AmazonZimlet_isbn"),"</td><td>",(attributes.ISBN ? attributes.ISBN.toString() : ""),"</td></tr>"].join("");
		html[j++] = ["<tr><td class='amznZimlet_HdrTD'>",this.getMessage("AmazonZimlet_edition"),"</td><td>",(attributes.Edition ? attributes.Edition.toString() : ""),"</td></tr>"].join("");
		html[j++] = ["<tr><td class='amznZimlet_HdrTD'>",this.getMessage("AmazonZimlet_publisher"),"</td><td>",(attributes.Publisher ? attributes.Publisher.toString() : ""),"</td></tr>"].join("");
		html[j++] = ["<tr><td class='amznZimlet_HdrTD'>",this.getMessage("AmazonZimlet_amazonPage"),"</td><td><a href='",url,"' target='_blank'>",this.getMessage("AmazonZimlet_openInAmazon"),"</a></td></tr>"].join("");

		html[j++] = "</table></td></tr></table>";
		html[j++] = "</div>";
		html[j++] = "<br/>";

	}
	document.getElementById(resultsDiv).innerHTML = html.join("");
};

/**
 *  Shows Book Search Dialog
 */
AmazonZimlet.prototype._showBookSearchDlg = function() {
	if (this._bookSearchDialog) {
		this._bookSearchDialog.popup();
		return;
	}
	this._bookSearchDlgView = new DwtComposite(this.getShell());
	this._bookSearchDlgView.setSize("510", "300");
	this._bookSearchDlgView.getHtmlElement().style.overflow = "auto";
	this._bookSearchDlgView.getHtmlElement().innerHTML = this._createBookSearchView();
	var sendPrefEmailBtnId = Dwt.getNextId();
	this._bookSearchDialog = new ZmDialog({title:this.getMessage("AmazonZimlet_SearchBooksOnAmzn"), view:this._bookSearchDlgView, standardButtons:[DwtDialog.CANCEL_BUTTON], parent:this.getShell()});
	this._setBookSearchDlgListeners();
	this._bookSearchDialog.popup();
	this._searchField.focus();
};

/**
 *  Sets Search Dialog's listeners
 */
AmazonZimlet.prototype._setBookSearchDlgListeners = function() {
	this._searchField = document.getElementById("amznZimlet_searchField");
	var callback = AjxCallback.simpleClosure(this._searchFieldKeyHdlr, this);
	Dwt.setHandler(this._searchField, DwtEvent.ONKEYUP, callback);

	this._searchField.onclick = callback;

	var btn = new DwtButton({parent:this.getShell()});
	btn.setText(this.getMessage("AmazonZimlet_SearchBooks"));
	btn.setImage("search");
	btn.addSelectionListener(new AjxListener(this, this._doAmazonBookSearch));
	document.getElementById("amznZimlet_searchButtonTD").appendChild(btn.getHtmlElement());
};

/**
 * Creates an empty Book Search dialog view
 */
AmazonZimlet.prototype._createBookSearchView = function() {
	var html = new Array();
	var i = 0;
	html[i++] = "<DIV id='amznZimlet_bookSearchDiv'>";
	html[i++] = "<table  align=center class='amzn_table'><tr><td><input type=text id='amznZimlet_searchField'> </input></td><td id='amznZimlet_searchButtonTD'></td></tr></table>";
	html[i++] = "</DIV>";
	html[i++] = ["<DIV  id='",AmazonZimlet.ELEMENT_RESULTS_DIV_ID,"'>"].join("");
	html[i++] = "</DIV>";
	return html.join("");
};

/**
 * Listens to Keyboard clicks and performs search when Enter-key is clicked
 * @param ev
 */
AmazonZimlet.prototype._searchFieldKeyHdlr =
function(ev) {
	var event = ev || window.event;
	if (event.keyCode == undefined) {
		return;
	}
	if (event.keyCode != 13) {//if not enter key
		return;
	}
	this._doAmazonBookSearch();
};



/**
 * Callback for search.
 *
 */

/**
 * Callback for search.
 *
 * @param {string} resultsDiv Id of the div where the search result should be inserted into
 * @param {object} results Results Object
 */
AmazonZimlet.prototype._searchCallback =
function(resultsDiv, results) {
	if (results && results.success) {
		var result = AjxXmlDoc.createFromXml(results.text).toJSObject(true, false);
		var items = result.Items;
		if (items.Item) {
			this._displayBooks(items.Item, resultsDiv);
		} else {
			try {
				var message = items.Request.Errors.Error.Message.toString();
				document.getElementById(resultsDiv).innerHTML = ["<b>", message, "</b>"].join("");
			} catch(e) {
				document.getElementById(resultsDiv).innerHTML = ["<b>",this.getMessage("AmazonZimlet_noResults"),"</b>"].join("");
			}
		}
	}
};
