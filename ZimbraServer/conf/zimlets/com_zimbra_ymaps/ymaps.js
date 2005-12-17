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
//  Zimlet to handle integration with a Yahoo! Maps         //
//  @author Kevin Henrikson                                 //
//////////////////////////////////////////////////////////////

function Com_Zimbra_YMaps() {
}

Com_Zimbra_YMaps.prototype.init =
function() {
	(new Image()).src = this.getResource('blank_pixel.gif');
};

Com_Zimbra_YMaps.prototype = new ZmZimletBase();
Com_Zimbra_YMaps.prototype.constructor = Com_Zimbra_YMaps;

// Y! Maps Webservice URL
Com_Zimbra_YMaps.URL = "http://api.local.yahoo.com/MapsService/V1/mapImage?appid=ZimbraMail&zoom=4&image_height=245&image_width=345&location=";

// Map image URI cache
Com_Zimbra_YMaps.CACHE = new Array();

Com_Zimbra_YMaps.prototype.toolTipPoppedUp =
function(spanElement, obj, context, canvas) {
	canvas.innerHTML = '<img width="345" height="245" id="'+ ZmZimletBase.encodeId(obj)+'" src="'+this.getResource('blank_pixel.gif')+'"/>';
	if (Com_Zimbra_YMaps.CACHE[obj+"img"]) {
		Com_Zimbra_YMaps._displayImage(Com_Zimbra_YMaps.CACHE[obj+"img"], obj);
	} else {
		var request = new AjxRpcRequest("yahoomaps");
		var url = ZmZimletBase.PROXY + AjxStringUtil.urlEncode(Com_Zimbra_YMaps.URL + obj);
		DBG.println(AjxDebug.DBG2, "Com_Zimbra_YMaps URL: " + url);
		request.invoke(null, url, null, new AjxCallback(this, Com_Zimbra_YMaps._callback, obj), true);
	}
};

Com_Zimbra_YMaps._displayImage = 
function(img_src, obj) {
	var imgEl = document.getElementById(ZmZimletBase.encodeId(obj));
	imgEl.style.backgroundImage = "url("+img_src+")";
    if(!Com_Zimbra_YMaps.CACHE[obj+"img"]) {
		Com_Zimbra_YMaps.CACHE[obj+"img"] = img_src;
	}
};

Com_Zimbra_YMaps._callback = 
function(obj, result) {
	var r = result.text;
	Com_Zimbra_YMaps._displayImage(r.substring(r.indexOf("http://img"),r.indexOf("</Result>")), obj);
};
