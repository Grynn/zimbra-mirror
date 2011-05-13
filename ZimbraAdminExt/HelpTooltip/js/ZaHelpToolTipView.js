/**
 * Created by IntelliJ IDEA.
 * User: mingzhang
 * Date: 5/11/11
 * Time: 11:37 PM
 * To change this template use File | Settings | File Templates.
 */
ZaToolTipView = function(zimlet, canvas, attributeName) {
    this.tooltipZimlet = zimlet;
    this.canvas = canvas;
    this._createView(canvas);
    this._headDiv = document.getElementById(ZaToolTipView.toolTipHeadId);
    this._attributeDiv = document.getElementById(ZaToolTipView.toolTipArributeId);
    this._moreDiv = document.getElementById(ZaToolTipView.toolTipMoreId);
    this._bodyDiv = document.getElementById(ZaToolTipView.toolTipBodyId);
    this.updateAttribute(attributeName);

    this._moreDiv.onclick = AjxCallback.simpleClosure(this._handleClick, this);
    canvas.onmouseover =  AjxCallback.simpleClosure(this.handleMouseOver, this);
    canvas.onmouseout = AjxCallback.simpleClosure(this.handleMouseOut, this);

    Dwt.setCursor(this._moreDiv, "pointer");
    Dwt.setSize(this._headDiv, ZaToolTipView.width);
    Dwt.setSize(this._bodyDiv, ZaToolTipView.width);
}



ZaToolTipView.toolTipHeadId= "ZatooltipZimletHeader";
ZaToolTipView.toolTipArributeId= "ZatooltipZimletAttributeName";
ZaToolTipView.toolTipMoreId= "ZatooltipZimletMore";
ZaToolTipView.toolTipBodyId= "ZatooltopZimletBody";
ZaToolTipView.width = 200;

ZaToolTipView.prototype._createView = function(canvas) {
   var html = new Array(50);
   var i = 0;
   html[i++] = "<div id=\"" + ZaToolTipView.toolTipHeadId +"\">";
   html[i++] = "<table cellspacing='0' cellpadding='0' border='0' ";
   html[i++] = " style='table-layout:fixed; width:100%' " + ">";
   html[i++] = "<colgroup><col width='*'>";
   html[i++] = "<col width='50px'></colgroup>";
   html[i++] = "<tr><td><div id=\"" + ZaToolTipView.toolTipArributeId + "\"";
   html[i++] = " style='white-space:nowrap; align: center; font-weight:bold' ";
   html[i++] = "></div></td>";
   html[i++] = "<td><div id=\"" + ZaToolTipView.toolTipMoreId + "\"";
   html[i++] = " style='white-space:nowrap; align: right; font-weight:bold' ";
   html[i++] = ">" + com_zimbra_tooltip.llMore + "</div></td></tr>";
   html[i++] = "</table></div>";
   html[i++] = "<div id=\"" + ZaToolTipView.toolTipBodyId +"\"</div>";
   canvas.innerHTML = html.join("");
}

ZaToolTipView.prototype.updateAttribute =
function(attributeName) {
    this._bodyDiv.innerHTML = "";
    Dwt.setVisible(this._bodyDiv, false);
    this._attributeName = attributeName;
    this._attributeDiv.innerHTML = this._attributeName;
}

ZaToolTipView.prototype._handleClick =
function(ev) {
    if(!this._attributeName){
        return;
    }
    this._bodyDiv.innerHTML = "loading";
    Dwt.setVisible(this._bodyDiv, true);
    this.tooltipZimlet.redraw();
}

ZaToolTipView.prototype.handleMouseOver =
function() {
	this.isMouseOverTooltip = true;
};

ZaToolTipView.prototype.handleMouseOut =
function() {
	this.isMouseOverTooltip = false;
	this.tooltipZimlet.hoverOut();
};