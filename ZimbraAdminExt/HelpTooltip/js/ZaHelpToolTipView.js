/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2011 VMware, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.3 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * 
 * ***** END LICENSE BLOCK *****
 */
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
    this.isMore = true;
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
ZaToolTipView.width = 300;

ZaToolTipView.prototype._createView = function(canvas) {
    var html = new Array(50);
    var i = 0;

    html[i++] = "<table cellspacing='0' cellpadding='0' border='0' >";
    html[i++] = "<tr><td>";
    html[i++] = "<div id=\"" + ZaToolTipView.toolTipHeadId +"\">";
    html[i++] = "<table cellspacing='0' cellpadding='0' border='0' ";
    html[i++] = " style='table-layout:fixed; width:100%'" + ">";
    html[i++] = "<tr><td><div";
    html[i++] = " style='text-align: left; font-weight:bold' ";
    html[i++] = ">" + com_zimbra_tooltip.llAttributeName +"</div></td></tr>";
    html[i++] = "<tr><td><div style='word-wrap: break-word;' id=\"" + ZaToolTipView.toolTipArributeId + "\"";
    html[i++] = "></div></td></tr>";
    html[i++] = "</table></div>";
    html[i++] = "</td></tr>";
    html[i++] = "<tr><td>";
    html[i++] = "<div id=\"" + ZaToolTipView.toolTipBodyId +"\"</div>";
    html[i++] = "</td></tr>";
    html[i++] = "<tr><td><div id=\"" + ZaToolTipView.toolTipMoreId + "\"";
    html[i++] = " style='white-space:nowrap; text-align: right; font-weight:bold' ";
    html[i++] = ">" + com_zimbra_tooltip.llMore + "</div></td></tr>";
    html[i++] = "</table>";
    canvas.innerHTML = html.join("");
}

ZaToolTipView.prototype._getBodyView = function(desc) {
    var html = new Array(50);
    var i = 0;
    html[i++] = "<table cellspacing='0' cellpadding='0' border='0' ";
    html[i++] = " style='table-layout:fixed;width:100%' " + ">";
    html[i++] = "<tr><td style='font-weight:bold' >" + com_zimbra_tooltip.llDesc + "</td></tr>";
    html[i++] = "<tr><td>" + desc + "</td></tr>";
    html[i++] = "</table>";
    return html.join("");

}

ZaToolTipView.prototype.updateAttribute =
function(attributeName) {
    this._bodyDiv.innerHTML = "";
    Dwt.setVisible(this._bodyDiv, false);
    this._attributeName = attributeName;
    this._attributeDiv.innerHTML = this._attributeName;
}

ZaToolTipView.prototype.updateDesc =
function(desc) {
    if(!desc) {
        this.updateBody(com_zimbra_tooltip.llNoResult);
    } else {
        this._bodyDiv.innerHTML = this._getBodyView(desc);
    }
}

ZaToolTipView.prototype.updateBody =
function(content) {
    this._bodyDiv.style.textAlign = "center";
    this._bodyDiv.innerHTML = "<span style='font-weight:bold;'>" + content +"</span>";
}

ZaToolTipView.prototype._handleClick =
function(ev) {
    if(!this._attributeName){
        return;
    }

    if(this.isMore){
        this.isMore = false;
        this._moreDiv.innerHTML = com_zimbra_tooltip.llHide;
        this.updateBody(com_zimbra_tooltip.llLoading);
        Dwt.setVisible(this._bodyDiv, true);
        var desc = ZaHelpTooltip.getDescByName(this._attributeName);
        this.updateDesc(desc);
    } else {
        this.isMore = true;
        this._moreDiv.innerHTML = com_zimbra_tooltip.llMore;
        Dwt.setVisible(this._bodyDiv, false);
    }
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