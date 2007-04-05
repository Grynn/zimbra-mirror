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