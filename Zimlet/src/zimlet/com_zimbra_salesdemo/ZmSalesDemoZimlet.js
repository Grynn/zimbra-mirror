/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Zimlets
 * Copyright (C) 2006, 2007, 2009, 2010 Zimbra, Inc.
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

function ZmSalesDemoZimlet() {
}

ZmSalesDemoZimlet.prototype = new ZmZimletBase();
ZmSalesDemoZimlet.prototype.constructor = ZmSalesDemoZimlet;

ZmSalesDemoZimlet.prototype.init =
function() {
	this.zimletBaseUrl = this.getResource("templates/SalesDemo.template").replace("/templates/SalesDemo.template", "");
	var model = new ZmSalesDemoItemModel(this);
	this.items = model.items;
	this._setRegExps();
};

ZmSalesDemoZimlet.prototype.toolTipPoppedUp =
function(spanElement, matchedText, context, canvas) {
	matchedText = matchedText.toLowerCase();
	var templateId = this.items[matchedText].tooltipTemplateId;
	if(templateId == "") {
		canvas.innerHTML = "This is where Tooltip data is shown";
		return;
	}
	canvas.innerHTML = AjxTemplate.expand(
			"com_zimbra_salesdemo.templates.SalesDemo#"+templateId, {zimletBaseUrl:this.zimletBaseUrl});

};

ZmSalesDemoZimlet.prototype.clicked =
function(span, matchedText) {
	matchedText = matchedText.toLowerCase();
	var item = this.items[matchedText];
	if(!item || !item.dialogTemplateId) {
		return;
	}
	var templateId = item.dialogTemplateId;
	var dialogContent = "Zimlet Dialog Content";
	if(item.dialogTemplateId != "") {
		dialogContent = AjxTemplate.expand(
			"com_zimbra_salesdemo.templates.SalesDemo#"+templateId, {zimletBaseUrl:this.zimletBaseUrl});
	}
	var msgDialog = appCtxt.getMsgDialog();
	msgDialog.setTitle(matchedText);
	msgDialog.reset();
	msgDialog.setContent(dialogContent);
	msgDialog.popup();
};

 //----------------
ZmSalesDemoZimlet.prototype.getActionMenu =
function(matchedText, span, context) {
	matchedText = matchedText.toLowerCase();
	var contextMenuObj = this.items[matchedText].contextMenuObj;
	if(contextMenuObj) {
		return this._zimletContext._contentActionMenu = contextMenuObj;
	}
	var contextMenuItems = this.items[matchedText].contextMenuItems;
	var menu = new ZmActionMenu({parent:DwtShell.getShell(window), menuItems:ZmOperation.NONE});
	var len = contextMenuItems.length;
	for(var i=0; i< len;i++){
		var item = contextMenuItems[i];
		menu.createMenuItem(id, {image:item.icon, text:item.name});
	}
	this.items[matchedText].contextMenu = menu;
	return this._zimletContext._contentActionMenu = menu;
};

/**
 * Called by the framework when generating the span for in-context link.
 *
 */
ZmSalesDemoZimlet.prototype.match =
function(line, startIndex) {
	var a = this._regexps;
	var ret = null;
	for (var i = 0; i < a.length; ++i) {
		var re = a[i];
		re.lastIndex = startIndex;
		var m = re.exec(line);
		if (m && m[0] != "") {
			if (!ret || m.index < ret.index) {
				ret = m;
				ret.matchLength = m[0].length;
				return ret;
			}
		}
	}
	return ret;
};

 /**
 *  Creates list of regular expressions to match
 */
ZmSalesDemoZimlet.prototype._setRegExps =
function() {
	this._regexps = [];
	for(var i in this.items) {
		 this._regexps.push(new RegExp(i, "ig"));
	}
};

ZmSalesDemoZimlet.prototype.generateSpan =
function(html, idx, obj, spanId, context) {
	var matchedText = obj.toLowerCase();
	var span = ZmObjectHandler.prototype.generateSpan.apply(this, arguments);
	if(this.firstItemSinceMsgOpened) {
		var item = this.items[matchedText];
		if(item && item.showToolbar) {
			this._addSalesZimletBar(matchedText, item);
			this.firstItemSinceMsgOpened = false;
		}
	}
	return span;
};

ZmSalesDemoZimlet.prototype.onMsgView =
function() {
	this.firstItemSinceMsgOpened = true;
};

//------ toolbar
ZmSalesDemoZimlet.prototype._addSalesZimletBar =
function(matchedText, item) {
	var viewId = appCtxt.getCurrentViewId();
	if (viewId == "CLV" && appCtxt.getSettings().getSetting("READING_PANE_LOCATION").value == "off") {
		setTimeout(AjxCallback.simpleClosure(this._do_addsalesZimletBar, this, "CV", matchedText, item), 1000);
	} else {
		this._do_addsalesZimletBar(viewId, matchedText, item);
	}
};

ZmSalesDemoZimlet.prototype._do_addsalesZimletBar =
function(viewId, matchedText, item) {
	this.salesZimlet_bar_expanded = false;
	var viewType = appCtxt.getViewTypeFromId(viewId);
	if (viewType != ZmId.VIEW_MSG) {
		var infoBar = document.getElementById(["zv__MSG__",viewId,"_infoBar"].join(""));
	} else {
		var infoBar = document.getElementById(["zv__",viewId,"__MSG_infoBar"].join(""));
	}
	if (!infoBar) {
		return;
	}
	if (this._previousParentNode && document.getElementById("salesZimlet_bar_frame")) {
		this._previousParentNode.removeChild(document.getElementById("salesZimlet_bar_frame"));
	}
	this._previousParentNode = infoBar.parentNode;
	var newNode = document.createElement("div");
	newNode.style.width = "100%";
	newNode.id = "salesZimlet_bar_frame";
	newNode.innerHTML = this._getSalesBarWidgetHtml(item);
	infoBar.parentNode.insertBefore(newNode, infoBar.nextSibling);

	this.changeOpac(0, newNode.style);
	this.opacity("salesZimlet_bar_frame", 0, 100, 500);
	this._addWidgetsToSalesBar(item);
};

ZmSalesDemoZimlet.prototype._addWidgetsToSalesBar =
function(item) {
	var templateId = item.toolbarTemplateId;
	var resultsContent = "Toolbar content is displayed here";
	if(templateId != "") {
		resultsContent = AjxTemplate.expand("com_zimbra_salesdemo.templates.SalesDemo#"+templateId, {zimletBaseUrl:this.zimletBaseUrl});
	}
	var resultsDiv = document.getElementById("salesZimlet_bar_resultsMainDiv");
	 if(resultsDiv) {
		 resultsDiv.innerHTML = resultsContent;
	 }
	var btn = new DwtButton({parent:this.getShell()});
	btn.setText("Search");
	btn.setImage("Search");
	var div = document.getElementById("salesZimlet_bar_searchBtn");
	if(div) {
		div.appendChild(btn.getHtmlElement());
	}
	var toolbarButtons = item.toolbarButtons;
	if(toolbarButtons && (toolbarButtons instanceof Array)) {
		var len = toolbarButtons.length;
		for(var i=0; i < len && i < 5; i++) {
			var tbObj = toolbarButtons[i];
			var btn = new DwtButton({parent:this.getShell()});
			btn.setText(tbObj.name);
			btn.setImage(tbObj.icon);
			var div = document.getElementById("salesZimlet_bar_btn"+(i+1));
			if(div) {
				div.appendChild(btn.getHtmlElement());
			}
		}
	}

	var callback = AjxCallback.simpleClosure(this._salesZimletBarExpandBtnListener, this);
	document.getElementById("salesZimlet_bar_mainHandler").onclick = callback;
};

ZmSalesDemoZimlet.prototype._salesZimletBarExpandBtnListener =
function() {
	if (!this.salesZimlet_bar_expanded) {
		document.getElementById("salesZimlet_expandCollapseIconDiv").className = "ImgHeaderExpanded";
		document.getElementById("salesZimlet_bar_generalToolbar").style.display = "block";
		document.getElementById("salesZimlet_bar_resultsMainDiv").style.display = "block";

		document.getElementById("salesZimlet_bar_msgCell").style.display = "none";
		this.salesZimlet_bar_expanded = true;
	} else {
		document.getElementById("salesZimlet_expandCollapseIconDiv").className = "ImgHeaderCollapsed";
		document.getElementById("salesZimlet_bar_generalToolbar").style.display = "none";
		document.getElementById("salesZimlet_bar_resultsMainDiv").style.display = "none";
		document.getElementById("salesZimlet_bar_msgCell").style.display = "block";

		this.salesZimlet_bar_expanded = false;
	}
};
ZmSalesDemoZimlet.prototype._getSalesBarWidgetHtml =
function(item) {
	var toolbarIcon = item.toolbarIcon;
	if(!toolbarIcon) {
		toolbarIcon = "";
	} else {
		toolbarIcon = "Img"+toolbarIcon;
	}
	var toolbarName = item.toolbarName ? item.toolbarName : "Sales Toolbar";
	var html = new Array();
	var i = 0;

	html[i++] = "<DIV class='overviewHeader'>";
	html[i++] = "<table cellpadding=0 cellspacing=0 width=100%><tr><td width='500'>";
	html[i++] = ["<div style='cursor:pointer' id='salesZimlet_bar_mainHandler'><table cellpadding=0 cellspacing=0><tr><td width=2px></td>",
		"<td width=11px><div id='salesZimlet_expandCollapseIconDiv' class='ImgHeaderCollapsed'></div></td><td width=2px></td>",
		"<td>","<div class='",toolbarIcon,"' />","</td>",
		"<td width=2px></td><td width='100'><label style='font-weight:bold;color:rgb(45, 45, 45);cursor:pointer'>",toolbarName,"</label></td>",
		"<td id='salesZimlet_bar_msgCell'></td></tr></table></div></td>"].join("");
	html[i++] = "<td>";
	html[i++] = "<div id='salesZimlet_bar_generalToolbar' style='display:none'>";
	html[i++] = "<table class='salesZimlet_table'>";
	html[i++] = "<tr><td><input type=text id='salesZimlet_bar_searchField' /></td><td id='salesZimlet_bar_searchBtn' width=80%></td>";
	html[i++] = "<td id='salesZimlet_bar_btn1'></td><td id='salesZimlet_bar_btn2'></td><td id='salesZimlet_bar_btn3'></td><td id='salesZimlet_bar_btn4'></td><td id='salesZimlet_bar_btn5'></td></tr></table></div>";
	html[i++] = "</td></tr></table>";
	html[i++] = "</DIV>";
	html[i++] = "<DIV  class='SalesZimlet_yellow' style='display: none;' id='salesZimlet_bar_resultsMainDiv'>";
	//html[i++] = this._getNoSearchResultSalesoundHtml();
	html[i++] = "</DIV>";
	return html.join("");
};

ZmSalesDemoZimlet.prototype.opacity =
function(id, opacStart, opacEnd, millisec) {
	//speed for each frame
	var speed = Math.round(millisec / 100);
	var timer = 0;
	var styleObj = document.getElementById(id).style;
	//determine the direction for the blending, if start and end are the same nothing happens
	if (opacStart > opacEnd) {
		for (i = opacStart; i >= opacEnd; i--) {
			setTimeout(AjxCallback.simpleClosure(this.changeOpac, this, i, styleObj), (timer * speed));
			timer++;
		}
	} else if (opacStart < opacEnd) {
		for (i = opacStart; i <= opacEnd; i++)
		{
			setTimeout(AjxCallback.simpleClosure(this.changeOpac, this, i, styleObj), (timer * speed));
			timer++;
		}
	}
};

//change the opacity for different browsers
ZmSalesDemoZimlet.prototype.changeOpac =
function(opacity, styleObj) {
	styleObj.opacity = (opacity / 100);
	styleObj.MozOpacity = (opacity / 100);
	styleObj.KhtmlOpacity = (opacity / 100);
	styleObj.filter = "alpha(opacity=" + opacity + ")";
};