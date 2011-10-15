/**
 * Created by IntelliJ IDEA.
 * User: mingzhang
 * Date: 9/5/11
 * Time: 1:00 AM
 * To change this template use File | Settings | File Templates.
 */

ZaTaskHeaderPanel = function(parent) {
    DwtComposite.call(this, parent, "TaskHeaderPanel", Dwt.ABSOLUTE_STYLE);
    this._expanded =true;
    this.getHtmlElement().innerHTML = this.getImgHtml();
    this.getHtmlElement().onclick = AjxCallback.simpleClosure(ZaTaskHeaderPanel.__handleClick, this);
}

ZaTaskHeaderPanel.expandedImg =  "ImgCollapseRight";
ZaTaskHeaderPanel.collapsedImg =  "ImgCollapseLeft";

ZaTaskHeaderPanel.prototype = new DwtComposite;
ZaTaskHeaderPanel.prototype.constructor = ZaTaskHeaderPanel;

ZaTaskHeaderPanel.prototype.getImgHtml = function() {
   if (this._expanded) {
       return ["<div class='", ZaTaskHeaderPanel.expandedImg, "' ></div>"].join("");
   } else {
       return ["<div class='", ZaTaskHeaderPanel.collapsedImg, "' ></div>"].join("");
   }
}

ZaTaskHeaderPanel.__handleClick =
function(ev) {
    this._expanded = !this._expanded;
    this.getHtmlElement().innerHTML = this.getImgHtml();
    ZaZimbraAdmin.getInstance().getTaskController().setExpanded(this._expanded);
}

