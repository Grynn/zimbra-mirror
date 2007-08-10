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
 * Portions created by Zimbra are Copyright (C) 2006 Zimbra, Inc.
 * All Rights Reserved.
 * 
 * Contributor(s):
 * 
 * ***** END LICENSE BLOCK *****
 */

function Com_Zimbra_Html() {
    ZmZimletBase.call(this);
}
Com_Zimbra_Html.prototype = new ZmZimletBase;
Com_Zimbra_Html.prototype.constructor = Com_Zimbra_Html;

//
// Constants
//

Com_Zimbra_Html.INLINE = "inline";
Com_Zimbra_Html.IFRAME = "iframe";

//
// Public methods
//

Com_Zimbra_Html.prototype.portletCreated = function(portlet) {
    var refresh = portlet.properties.refresh;
    if (refresh) {
        portlet.setRefreshInterval(refresh);
    }
    this.portletRefreshed(portlet);
};

Com_Zimbra_Html.prototype.portletRefreshed = function(portlet) {
    var isIFrame = portlet.properties.type != Com_Zimbra_Html.INLINE;
    if (isIFrame) {
        var html = [
            "<iframe ",
                "style='border:none;width:100%;height:100%' ",
                "src='",portlet.properties.url,"'",
            "></iframe>"
        ].join("");
        portlet.setContent(html);
    }
    else {
        var url = portlet.properties.url || "";
        if (url.match(/^(https?|ftp):/)) {
            url = ZmZimletBase.PROXY + AjxStringUtil.urlComponentEncode(url);
        }
        else if (!url.match(/^\//)) {
            url = this.getResource(url);
        }
        var params = {
            url: url,
            callback: new AjxCallback(this, this._handleHtml, [portlet])
        };
        AjxLoader.load(params);
    }
};

//
// Protected methods
//

Com_Zimbra_Html.prototype._handleHtml = function(portlet, req) {
    if (!req || !req.responseText) return;
    portlet.setContent(req.responseText);
};