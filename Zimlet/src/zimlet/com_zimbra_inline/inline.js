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

function Com_Zimbra_Inline() {
}

Com_Zimbra_Inline.prototype = new ZmZimletBase();
Com_Zimbra_Inline.prototype.constructor = Com_Zimbra_Inline;

//
// Public methods
//

Com_Zimbra_Inline.prototype.portletCreated = function(portlet) {
    var defaultRefresh = 0; // never
    portlet.setRefreshInterval(portlet.properties.refresh || defaultRefresh);
    this.portletRefreshed(portlet);
};
Com_Zimbra_Inline.prototype.portletRefreshed = function(portlet) {
    var url = portlet.properties.url;
    if (!/^\//.test(url)) {
        if (/^https?s:|ftp:|telnet:|gopher:/.test(url)) {
            url = ZmZimletBase.PROXY + AjxStringUtil.urlComponentEncode(url);
        }
        else {
            url = this.getResource(url);
        }
    }

    var callback = new AjxCallback(this, this._handlePortletRefreshed, [ portlet ]);
    AjxRpc.invoke(null, url, null, callback, true);
};
Com_Zimbra_Inline.prototype._handlePortletRefreshed = function(portlet, result) {
//    if (!result || !result.xml) return;
    var html = result.text || "";
    portlet.setContent(html);
};
