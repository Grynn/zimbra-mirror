/**
 * Created by IntelliJ IDEA.
 * User: mingzhang
 * Date: 4/1/12
 * Time: 2:11 AM
 * To change this template use File | Settings | File Templates.
 */


ZaAboutDialog = function(parent, className, w, h) {
    if (arguments.length == 0) return;
    var clsName = className || "DwtBaseDialog AboutScreen";
    DwtBaseDialog.call(this, parent, clsName,  "");
    w = w || "579px";
    h = h || "264px";
    this.setSize(w, h);
    this.addCloseListener();
    this.addFocusBlurListener();
}

ZaAboutDialog.prototype = new DwtBaseDialog;
ZaAboutDialog.prototype.constructor = ZaAboutDialog;
ZaAboutDialog.prototype.TEMPLATE = "admin.Widgets#ZaAboutDialog";
ZaAboutDialog.prototype.closeIcon = "ImgAboutClose";
ZaAboutDialog.prototype.closeHoverIcon = "ImgAboutCloseHover";

ZaAboutDialog.prototype._createHtmlFromTemplate = function (templateId, data) {
    data.dragId = this._dragHandleId;
    data.closeIcon = this.closeIcon ? this.closeIcon : "ImgAboutClose";
    var date = AjxDateFormat.getDateInstance().format(ZaServerVersionInfo.buildDate);
    data.version = AjxBuffer.concat(ZaMsg.splashScreenVersion, " ", ZaServerVersionInfo.version , " " , date);
    data.copyright = ZaItem.getSplashScreenCopyright();
    data.aboutBanner = "ImgAboutBanner";
    DwtComposite.prototype._createHtmlFromTemplate.call(this, templateId, data);
    this._contentEl = document.getElementById(data.id+"_content");
    this._closeIconEl = document.getElementById(data.id+"_close");
}

ZaAboutDialog.prototype.addCloseListener = function () {
    if (this._closeIconEl) {
        Dwt.setHandler(this._closeIconEl, DwtEvent.ONCLICK, AjxCallback.simpleClosure(this.popdown, this));
    }
}

ZaAboutDialog.prototype.addFocusBlurListener = function () {
    if (this._closeIconEl && this._closeIconEl.firstChild) {
        var enterEvent = AjxEnv.isIE? DwtEvent.ONMOUSEENTER: DwtEvent.ONMOUSEOVER;
        var leaveEvent = AjxEnv.isIE? DwtEvent.ONMOUSELEAVE: DwtEvent.ONMOUSEOUT;
        Dwt.setHandler(this._closeIconEl.firstChild, enterEvent, AjxCallback.simpleClosure(this.changeCloseIcon, this, true));
        Dwt.setHandler(this._closeIconEl.firstChild, leaveEvent, AjxCallback.simpleClosure(this.changeCloseIcon, this, false));
    }
}

ZaAboutDialog.prototype.changeCloseIcon = function (isHover) {
    if (this._closeIconEl && this._closeIconEl.firstChild) {
        if (isHover) {
            Dwt.delClass(this._closeIconEl.firstChild, this.closeIcon, this.closeHoverIcon);
        } else {
            Dwt.delClass(this._closeIconEl.firstChild, this.closeHoverIcon, this.closeIcon);
        }
    }
}

