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
 * Amazon Zimlet. Shows "hover" information for ISBN numbers in context.
 * 
 * @author Kevin Henrikson
 */

/**
 * Default constructor.
 */
function Com_Zimbra_Amzn() {
};

Com_Zimbra_Amzn.prototype = new ZmZimletBase();
Com_Zimbra_Amzn.prototype.constructor = Com_Zimbra_Amzn;

/**
 * Initializes the Zimlet.
 * 
 */
Com_Zimbra_Amzn.prototype.init = function() {
	
};

/**
 * Amazon Service URL.
 * @type	string
 */
Com_Zimbra_Amzn.URL = "http://webservices.amazon.com/onca/xml?Service=AWSECommerceService&AWSAccessKeyId=1582H242YD2K3JEANR82&Operation=ItemSearch&SearchIndex=Books&ResponseGroup=Medium&Keywords=";

/**
 * Content cache.
 * @type	array
 */
Com_Zimbra_Amzn.CACHE = new Array();

/**
 * This method is called when the tool tip is popped-up.
 * 
 */
Com_Zimbra_Amzn.prototype.toolTipPoppedUp =
function(spanElement, obj, context, canvas) {
	canvas.innerHTML = '<img width="110" height="170" id="' + ZmZimletBase.encodeId(obj + "_AIMG") + '" src="'+this.getResource('blank_pixel.gif')+'"/><div style="width:110px;" id="'+ZmZimletBase.encodeId(obj+"_ATXT")+'"> <br/> </div>';
	if (Com_Zimbra_Amzn.CACHE[obj]) {
		this._displayBook(Com_Zimbra_Amzn.CACHE[obj].Image, Com_Zimbra_Amzn.CACHE[obj].Book, obj);
	} else {
		var url = ZmZimletBase.PROXY + AjxStringUtil.urlComponentEncode(Com_Zimbra_Amzn.URL + obj.replace(/[- A-WY-Z]/ig,''));
		DBG.println(AjxDebug.DBG2, "Com_Zimbra_Amzn url " + url);
		AjxRpc.invoke(null, url, null, new AjxCallback(this, this._toolTipCallback, obj), true);
	}	
};

/**
 * Performs a search.
 * 
 */
Com_Zimbra_Amzn.prototype.searchAmzn =
function(obj, canvas) {
	var i = 0;
	var html = new Array();
	html[i++] = '<table cellspacing=4 cellpadding=0 border=0 width=\"330px\"><tr>';
	html[i++] = '<td><img width="110" height="170" id="' + ZmZimletBase.encodeId(obj + "_AIMG_0") + '" src="'+this.getResource('blank_pixel.gif')+'"/><div style="width:110px;" id="'+ZmZimletBase.encodeId(obj+"_ATXT_0")+'"> <br/> </div></td>';
	html[i++] = '<td><img width="110" height="170" id="' + ZmZimletBase.encodeId(obj + "_AIMG_1") + '" src="'+this.getResource('blank_pixel.gif')+'"/><div style="width:110px;" id="'+ZmZimletBase.encodeId(obj+"_ATXT_1")+'"> <br/> </div></td>';
	html[i++] = '<td><img width="110" height="170" id="' + ZmZimletBase.encodeId(obj + "_AIMG_2") + '" src="'+this.getResource('blank_pixel.gif')+'"/><div style="width:110px;" id="'+ZmZimletBase.encodeId(obj+"_ATXT_2")+'"> <br/> </div></td>';
	html[i++] = '</table></tr>';
	canvas.innerHTML = html.join('');
	if (Com_Zimbra_Amzn.CACHE[obj]) {
		this._displayBooks(Com_Zimbra_Amzn.CACHE[obj], obj);
	} else {
		var url = ZmZimletBase.PROXY + AjxStringUtil.urlComponentEncode(Com_Zimbra_Amzn.URL + obj);
		DBG.println(AjxDebug.DBG2, "Com_Zimbra_Amzn url " + url);
		AjxRpc.invoke(null, url, null, new AjxCallback(this, this._searchCallback,[obj, canvas]), true);
	}	
};

// XXX need support for regex's on sub-var's
Com_Zimbra_Amzn.prototype._getHtmlContent = 
function(html, idx, obj, context) {
	var contentObj = this.xmlObj().getVal('contentObject');
	html[idx++] = '<a target="_blank" href="';
	html[idx++] = (contentObj.onClick.actionUrl.target).replace('${objectContent}', AjxStringUtil.htmlEncode(obj.replace(/[- A-WY-Z]/ig,'')));
	html[idx++] = '">'+AjxStringUtil.htmlEncode(obj)+'</a>';
	return idx;
};

/**
 * This method is called when the panel item is double-clicked.
 * 
 */
Com_Zimbra_Amzn.prototype.doubleClicked = function() {
	this.singleClicked();
};

/**
 * This method is called when the panel item is single-clicked.
 * 
 */
Com_Zimbra_Amzn.prototype.singleClicked = function() {
	var editorProps = [
		{ label 		 : this.getMessage("amzn_search"),
		  name           : "search",
		  type           : "string",
		  value          : "",
		  minLength      : 4,
		  maxLength      : 100
		}
		];
	if (!this._dlg_propertyEditor) {
		var view = new DwtComposite(this.getShell());
		this._propertyEditor = new DwtPropertyEditor(view, true);
		var pe = this._propertyEditor;
		pe.initProperties(editorProps);
		var dialog_args = {
			title	: this.getMessage("amzn_dialogTitle"),
			view	: view,
			parent	: this.getShell()
		};
		this._dlg_propertyEditor = new ZmDialog(dialog_args);
		
		var dlg = this._dlg_propertyEditor;
		pe.setFixedLabelWidth();
		pe.setFixedFieldWidth();
		dlg.setButtonListener(DwtDialog.OK_BUTTON,
				      new AjxListener(this, function() {
				          if (!pe.validateData()) {return;}
					      this._doSearch();
				      }));
	}
	this._dlg_propertyEditor.popup();
};

/**
 * Performs a search initiated from the property editor dialog.
 * 
 */
Com_Zimbra_Amzn.prototype._doSearch =
function() {
	this._dlg_propertyEditor.popdown();
	this._displaySearchResult(this._propertyEditor.getProperties().search);
	this._dlg_propertyEditor.dispose();
	this._dlg_propertyEditor = null;
};

/**
 * Displays the search results a search initiated from the property editor dialog.
 * 
 */
Com_Zimbra_Amzn.prototype._displaySearchResult = 
function(search) {
	var view = new DwtComposite(this.getShell());
	var dialog_args = {
		view	: view,
		title	: this.getMessage("amzn_dialogResultTitle"),
		parent	: this.getShell()
	};
	var dlg = new ZmDialog(dialog_args);
	dlg.setButtonVisible(DwtDialog.CANCEL_BUTTON, false);
	dlg.popup();
	dlg.setButtonListener(DwtDialog.OK_BUTTON,
		      new AjxListener(this, function() {
			      dlg.popdown();
			      dlg.dispose();
		      }));
    var el = view.getHtmlElement();
    var div = document.createElement("div");
    el.appendChild(div);
    this.searchAmzn(search, div);
};

/**
 * Displays book information.
 * 
 */
Com_Zimbra_Amzn.prototype._displayBook = 
function(imageInfo, bookInfo, obj) {
	var imgEl = document.getElementById(ZmZimletBase.encodeId(obj + "_AIMG"));
	var txtEl = document.getElementById(ZmZimletBase.encodeId(obj + "_ATXT"));
	if(!imageInfo || !bookInfo) {
		txtEl.innerHTML = "<b><center>" + this.getMessage("amzn_searchedFor") + obj + "<br/><br/>" + this.getMessage("amzn_error") + "</center></b>";
		return;
	}
	imgEl.style.width = imageInfo.Width;
	imgEl.style.height = imageInfo.Height;
	imgEl.style.backgroundImage = "url("+imageInfo.URL+")";
	txtEl.style.width = imageInfo.Width;
	txtEl.innerHTML = "<a target=\"_blank\" href=\"" + bookInfo.url + "\">" + bookInfo.title + "</a>" + this.getMessage("amzn_by") + bookInfo.author +" "+ bookInfo.price;
    if(!Com_Zimbra_Amzn.CACHE[obj]) {
    	Com_Zimbra_Amzn.CACHE[obj] = new Object();
		Com_Zimbra_Amzn.CACHE[obj].Image = imageInfo;
		Com_Zimbra_Amzn.CACHE[obj].Book = bookInfo;
	}
};

/**
 * Displays information on multiple books.
 * 
 */
Com_Zimbra_Amzn.prototype._displayBooks = 
function(itemList, obj) {
	var items = itemList;
	if (itemList && (typeof itemList.length  == "undefined") ) {
		items = [itemList];
	}
	
	for(var i=0; i < 3; i++) {
		var imgEl = document.getElementById(ZmZimletBase.encodeId(obj + "_AIMG_" + i));
		var txtEl = document.getElementById(ZmZimletBase.encodeId(obj + "_ATXT_" + i));
		if(items && !items[i]) {
			txtEl.innerHTML = "<b><center>" + this.getMessage("amzn_searchedFor") + obj + "<br/><br/>" + this.getMessage("amzn_error") + "</center></b>";
			continue;
		}

		var bookInfo = new Object();
		bookInfo.title = items[i].ItemAttributes.Title;
		bookInfo.author = items[i].ItemAttributes.Author;
		bookInfo.price = items[i].ItemAttributes.ListPrice.FormattedPrice;
		bookInfo.url = items[i].DetailPageURL;
		var imageInfo = items[i].ImageSets.ImageSet.MediumImage;
		imgEl.style.width = imageInfo.Width;
		imgEl.style.height = imageInfo.Height;
		imgEl.style.backgroundImage = "url("+imageInfo.URL+")";
		txtEl.style.width = imageInfo.Width;
		txtEl.innerHTML = "<a target=\"_blank\" href=\"" + bookInfo.url + "\">" + bookInfo.title + "</a>" + this.getMessage("amzn_by") + bookInfo.author +" "+ bookInfo.price;
	}
    if(!Com_Zimbra_Amzn.CACHE[obj]) {
    	Com_Zimbra_Amzn.CACHE[obj] = items;
	}
};

/**
 * Callback for tool tip.
 * 
 */
Com_Zimbra_Amzn.prototype._toolTipCallback = 
function(obj, results) {
	var result = AjxXmlDoc.createFromXml(results.text).toJSObject(true, false);
	var bookInfo = new Object();
	if(result.Items.Item.ImageSets && result.Items.Item.ItemAttributes) {
		bookInfo.title = result.Items.Item.ItemAttributes.Title;
		bookInfo.author = result.Items.Item.ItemAttributes.Author;
		bookInfo.price = result.Items.Item.ItemAttributes.ListPrice.FormattedPrice;
		bookInfo.url = result.Items.Item.DetailPageURL;
		this._displayBook(result.Items.Item.ImageSets.ImageSet.MediumImage, bookInfo, obj);
	} else {
		this._displayBook(null, null, obj);
	}
};

/**
 * Callback for search.
 * 
 */
Com_Zimbra_Amzn.prototype._searchCallback = 
function(obj,canvas, results) {
    if(results && results.success){
        var result = AjxXmlDoc.createFromXml(results.text).toJSObject(true, false);
        DBG.dumpObj(result);
        var items = result.Items;
        if(items && items.Item){
            this._displayBooks(items.Item, obj);
            return;
        }
    }
    canvas.innerHTML = "<table style='height:170px;width:330px;'><tr><th valign='middle'>"+AjxMsg["noResults"]+"</th></tr></table>";
};