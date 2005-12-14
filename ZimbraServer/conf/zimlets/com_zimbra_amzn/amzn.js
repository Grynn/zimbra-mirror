/*
 * ***** BEGIN LICENSE BLOCK *****
 * Version: ZPL 1.1
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.1 ("License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.zimbra.com/license
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
 * the License for the specific language governing rights and limitations
 * under the License.
 * 
 * The Original Code is: Zimbra Collaboration Suite Web Client
 * 
 * The Initial Developer of the Original Code is Zimbra, Inc.
 * Portions created by Zimbra are Copyright (C) 2005 Zimbra, Inc.
 * All Rights Reserved.
 * 
 * Contributor(s):
 * 
 * ***** END LICENSE BLOCK *****
 */

//////////////////////////////////////////////////////////////
//  Amazon Zimlet.  Provides hovers for ISBN numbers.       //
//  @author Kevin Henrikson, <kevinh@zimbra.com>            //
//////////////////////////////////////////////////////////////

function Com_Zimbra_Amzn(appCtxt) {
	ZmZimletBase.call(this, appCtxt, "books");
	// Pre-load placeholder image
	(new Image()).src = Com_Zimbra_Amzn.BLANKGIF;
}

Com_Zimbra_Amzn.prototype = new ZmZimletBase();
Com_Zimbra_Amzn.prototype.constructor = Com_Zimbra_Amzn;

// Address regex
//Com_Zimbra_Amzn.ADDRESS_RE = this._zimletContext.contentObject.matchOn[0].regex[0]._content;
Com_Zimbra_Amzn.RE = /ISBN\x20\d{1,5}[ -]?\d{1,7}[ -]?\d{1,6}[ -]?(\d|X)\b/ig;

// AMZN Service URL
Com_Zimbra_Amzn.URL = "http://webservices.amazon.com/onca/xml?Service=AWSECommerceService&AWSAccessKeyId=1582H242YD2K3JEANR82&Operation=ItemSearch&SearchIndex=Books&ResponseGroup=Medium&Keywords=";

// Blank GIF
Com_Zimbra_Amzn.BLANKGIF = "/service/zimlet/com_zimbra_amzn/blank_pixel.gif";
//Com_Zimbra_Amzn.BLANKGIF = this.getResource('blank_pixel.gif');

Com_Zimbra_Amzn.CACHE = new Array();

Com_Zimbra_Amzn.prototype.match =
function(content, startIndex) {
	Com_Zimbra_Amzn.RE.lastIndex = startIndex;
	return Com_Zimbra_Amzn.RE.exec(content);
};

Com_Zimbra_Amzn.prototype.toolTipPoppedUp =
function(spanElement, obj, canvas) {
	canvas.innerHTML = '<img width="110" height="170" id="' + ZmZimletBase.encodeId(obj + "_AIMG") + '" src="'+Com_Zimbra_Amzn.BLANKGIF+'"/><div style="width:110px;" id="'+ZmZimletBase.encodeId(obj+"_ATXT")+'"> <br/> </div>';
	if (Com_Zimbra_Amzn.CACHE[obj]) {
		Com_Zimbra_Amzn.displayBook(Com_Zimbra_Amzn.CACHE[obj].Image, Com_Zimbra_Amzn.CACHE[obj].Book, obj);
	} else {
		var request = new AjxRpcRequest("amazon");
		var url = ZmZimletBase.PROXY + AjxStringUtil.urlEncode(Com_Zimbra_Amzn.URL + obj.replace(/[-A-Z ]/ig,''));
		DBG.println(AjxDebug.DBG2, "Com_Zimbra_Amzn url " + url);
		request.invoke(null, url, null, new AjxCallback(this, Com_Zimbra_Amzn._callback, obj), true);
	}	
};


// XXX need support for regex's on sub-var's
Com_Zimbra_Amzn.prototype._getHtmlContent = 
function(html, idx, obj, context) {
	var contentObj = this.xmlObj().getVal('contentObject');
	html[idx++] = '<a target="_blank" href="';
	html[idx++] = (contentObj.onClick[0].actionUrl[0].target).replace('${objectContent}', AjxStringUtil.htmlEncode(obj.replace(/[-A-Z ]/ig,'')));
	html[idx++] = '">'+AjxStringUtil.htmlEncode(obj)+'</a>';
	return idx;
};

Com_Zimbra_Amzn.displayBook = 
function(imageInfo, bookInfo, obj) {
	var imgEl = document.getElementById(ZmZimletBase.encodeId(obj + "_AIMG"));
	imgEl.style.width = imageInfo.Width;
	imgEl.style.height = imageInfo.Height;
	imgEl.style.backgroundImage = "url("+imageInfo.URL+")";
	var txtEl = document.getElementById(ZmZimletBase.encodeId(obj + "_ATXT"));
	txtEl.style.width = imageInfo.Width;
	txtEl.innerHTML = bookInfo.title +" by "+ bookInfo.author +" "+ bookInfo.price;
    if(!Com_Zimbra_Amzn.CACHE[obj]) {
    	Com_Zimbra_Amzn.CACHE[obj] = new Object();
		Com_Zimbra_Amzn.CACHE[obj].Image = imageInfo;
		Com_Zimbra_Amzn.CACHE[obj].Book = bookInfo;
	}
};

Com_Zimbra_Amzn._callback = 
function(args) {
	var result = AjxXmlDoc.createFromXml(args[1].text).toJSObject(true, false);
	var bookInfo = new Object();
	bookInfo.title = result.Items.Item.ItemAttributes.Title;
	bookInfo.author = result.Items.Item.ItemAttributes.Author;
	bookInfo.price = result.Items.Item.ItemAttributes.ListPrice.FormattedPrice;
	Com_Zimbra_Amzn.displayBook(result.Items.Item.ImageSets.ImageSet.MediumImage, bookInfo, args[0]);
};