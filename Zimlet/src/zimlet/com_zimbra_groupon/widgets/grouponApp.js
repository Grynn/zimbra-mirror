/*
 * ***** BEGIN LICENSE BLOCK *****
 *
 * Zimbra Collaboration Suite Zimlets
 * Copyright (C) 2006, 2007 Zimbra, Inc.
 *
 * The contents of this file are subject to the Yahoo! Public License
 * Version 1.0 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 *
 * ***** END LICENSE BLOCK *****
 * @Author Raja Rao DV (rrao@zimbra.com)
 */

function Com_Zimbra_GrouponApp(zimlet, tabApp) {
	this.zimlet = zimlet;
	this.tabApp = tabApp;
	this._shell = zimlet.getShell();
	this.metaData = appCtxt.getActiveAccount().metaData;
	if(this.tabapp) {
		this.parentViewHtmlElement = this.tabApp.getController().getView().getHtmlElement();
		this.parentViewHtmlElement.style.overflow = "auto";
	}
	this.cardInfoSectionIdsArray = new Array();//every card's data div (used to reset its height when window is resized)
	this.preferences = {};
	this._allCardsProps = new Array();
	this.preferences.groupon_pref_cardWidthList = "350px"; 
};

Com_Zimbra_GrouponApp.prototype.show =
function() {
	this._addToolbarWidgets();
	this.tabApp.setContent(this._constructSkin());
	this._view = this.tabApp.getController().getView();
	this._view.addControlListener(new AjxListener(this, this._resizeHandler));//add resize handler

	this._setMainCardHeight();
	this._addAreaCodeMenuListener();
};


Com_Zimbra_GrouponApp.prototype._addAreaCodeMenuListener =
function() {
	var menu = document.getElementById("grouponZimlet_divisionsMenuId");

	var callback = AjxCallback.simpleClosure(this._handleAreaCodeSelect, this);
	menu.onchange = callback;	
};

Com_Zimbra_GrouponApp.prototype._handleAreaCodeSelect =
function() {
	var val = document.getElementById("grouponZimlet_divisionsMenuId").value;
	this.zimlet.setUserProperty("grouponZimlet_myCityCode", val, true);//save
	this.zimlet._dealAreaCode = val;
	this.zimlet.getDeals(val, GrouponZimlet.SHOW_IN_CARD_VIEW);
};


Com_Zimbra_GrouponApp.prototype._resizeHandler =
function() {
	this._setMainCardHeight();
};

Com_Zimbra_GrouponApp.prototype._setMainCardHeight =
function() {
	var mainCardsDiv = document.getElementById('groupon_twitterCardsDiv');
	mainCardsDiv.style.overflow = "auto";
	var parent = this._view.getHtmlElement();
	mainCardsDiv.style.width = parent.style.width;
	mainCardsDiv.style.height = (parseInt(parent.style.height.replace("px", "")) - document.getElementById("groupon_topSxn").offsetHeight);
	//24+10 is the height of the header-section(where close, delete buttons are present)
	this._mainCardsHeight = (parseInt(mainCardsDiv.style.height.replace("px", "")) - 75) + "px";

	for (var i = 0; i < this.cardInfoSectionIdsArray.length; i++) {
		var infoCard = document.getElementById(this.cardInfoSectionIdsArray[i]);
		if (infoCard != null) {
			infoCard.style.height = this._mainCardsHeight;
		}
	}
};

Com_Zimbra_GrouponApp.prototype._constructSkin =
function() {
	return AjxTemplate.expand("com_zimbra_groupon.templates.Groupon#Frame");
};

Com_Zimbra_GrouponApp.prototype._addToolbarWidgets =
function() {
	var html = new Array();
	var divisions = [];
	if(this.zimlet.grouponDivisions) {
		divisions = this.zimlet.grouponDivisions.divisions;
	}
	var len = divisions.length;
	html.push("<DIV style='padding:3px' >Display coupons for area: ");
	html.push("<select id=\"grouponZimlet_divisionsMenuId\" >");

	for(var i = 0; i < len; i++) {
		var division = divisions[i];
		if(division.id == this.zimlet._dealAreaCode) {
			html.push("<option selected value='",division.id,"'>",division.name,"</option>");
		} else {
			html.push("<option value='",division.id,"'>",division.name,"</option>");
		}

	}
	html.push("</select>");
	html.push("</DIV>");
	var toolbar = this.tabApp.getToolbar();
	toolbar.getHtmlElement().innerHTML = html.join("");
};

Com_Zimbra_GrouponApp.prototype._getMaxHeaderTextLength =
function() {
	if (!this.maxHeaderTextLength) {
		var cardWidth = parseInt(this.preferences.groupon_pref_cardWidthList.replace("px", ""));
		this.maxHeaderTextLength = (cardWidth / 50) * 2;
	}
	return this.maxHeaderTextLength;
};

Com_Zimbra_GrouponApp.prototype._showCard =
function(cardProps) {
	var headerName = cardProps.headerName;
	var type = cardProps.type;
	var tweetTableId = cardProps.tweetTableId;
	var autoScroll = cardProps.autoScroll;
	var cardsTable = document.getElementById('groupon_cardsMainTable');
	var row;
	if (cardsTable.rows.length == 0) {
		row = cardsTable.insertRow(0);
	} else {
		row = cardsTable.rows[0];
	}
	if (this.cardIndex == undefined) {
		this.cardIndex = 0;
	} else {
		this.cardIndex = this.cardIndex + 1;
	}
	var hdrClass = "groupon_axnClass groupon_generalColor";
	var trimName = headerName;

	if (headerName.length > this._getMaxHeaderTextLength()) {
		trimName = headerName.substring(0, this._getMaxHeaderTextLength()) + "..";
	}

	var prettyName = type.toLowerCase() + ": " + trimName;
	var iconName = "GrouponIcon";
	var hdrCellColor = "black";
	hdrClass = "groupon_axnClass groupon_normalDealsBgColor";
	prettyName = headerName;
	if(cardProps.featured) {
		iconName = "GrouponFeaturedIcon";
		prettyName = "<label style=\"color: white; border:1px solid white; background: none repeat scroll 0pt 0pt #B73B3B; padding: 1px;\">Featured</label> " +prettyName;
	}

	var hdrCellStyle = "style=\"font-size:12px;color:" + hdrCellColor + ";font-family:'Lucida Grande',sans-serif;font-weight:bold;\"";
	var card = "";
	if(cardProps.deal.placement_priority == "featured") {
		card = row.insertCell(0);
	} else {
		card = row.insertCell(-1);
	}
	card.id = "groupon_card" + this.cardIndex;
	var cardInfoSectionId = "groupon_cardInfoSectionId" + this.cardIndex;
	cardProps.tableId = cardInfoSectionId;//store this
	//used to reset heights when window is resized
	this.cardInfoSectionIdsArray.push(cardInfoSectionId);

	var elStyle = ""
	if (AjxEnv.isFirefox) {
		elStyle = "style='height: 28px;'";
	} else {
		elStyle = "style='height: 32px;'";
	}

	var subs = {
		cardIndex: this.cardIndex,
		elStyle: elStyle,
		hdrClass: hdrClass,
		iconName:  AjxImg.getImageHtml(iconName),
		hdrCellStyle: hdrCellStyle,
		prettyName: prettyName,
		closeBtnImg: this.zimlet.getResource("img/groupon_closeBtn.png"),
		cardInfoSectionId:cardInfoSectionId,
		mainCardHeight:this._mainCardsHeight,
		cardWidth: this.preferences.groupon_pref_cardWidthList
	};
	card.innerHTML = AjxTemplate.expand("com_zimbra_groupon.templates.Groupon#CardFrame", subs);

	var params = {row:row, cellId:card.id, tableId:cardInfoSectionId, headerName:headerName, type:type}
	cardProps.cellId = card.id;
	cardProps.rowObj = row;
	var callback = AjxCallback.simpleClosure(this._handleCloseButton, this, cardProps);
	document.getElementById("groupon_closeBtn" + this.cardIndex).onclick = callback;
	return cardInfoSectionId;
};

Com_Zimbra_GrouponApp.prototype._handleCloseButton =
function(cardProps) {
	cardProps.rowObj.deleteCell(document.getElementById(cardProps.cellId).cellIndex);
	clearInterval(cardProps.timer);
	cardProps.isClosed = true;
};

Com_Zimbra_GrouponApp.prototype.createCardView =
function(tableId, deal) {
	var conditions = deal.conditions;
	var details = conditions.details;
	if(!details) {
		details = [];
	}
	var html = [];
	var cardHtml = this._getCardDetailedHtml(deal, GrouponZimlet.SHOW_IN_CARD_VIEW);
	html.push(cardHtml);
	html.push("<br/>");
	html.push("<div style='font-size:1.1em;font-weight:bold;padding:3px;text-align:center'>Conditions:</div>");
	 var len = details.length;
	for(var i =0; i < len; i++) {
		html.push("<div style='font-size:12px;padding:3px;text-align:center;'><label>",details[i], "</label></div>");
	}
	document.getElementById(tableId).innerHTML = html.join("");
};

Com_Zimbra_GrouponApp.prototype._getCardDetailedHtml =
function(deal, mode) {
	var conditions = deal.conditions;
	var details =conditions.details;
	var initialQuantity = conditions.initial_quantity;
	var limitedQuantity = conditions.limited_quantity;
	var itemsRemaining = "-";


	if(limitedQuantity) {
		itemsRemaining =  initialQuantity - deal.quantity_sold;
		itemsRemaining = itemsRemaining < 0 ? "-" : itemsRemaining;
	} else {
		itemsRemaining = "Unlimited";
	}
	var value = "$"+deal.value.replace("USD","");
	var discount_amount = "$"+deal.discount_amount.replace("USD","");
	var price = "$"+deal.price.replace("USD","");

	var soldOut = deal.sold_out;
	var buyImg = this.zimlet.getResource("img/groupon_buy.png");
	if(soldOut) {
		buyImg = this.zimlet.getResource("img/groupon_soldOut.png");		
	}
	var subs = {
		imageUrl: deal.large_image_url,
		divisionName: deal.division_name,
		title: deal.title,
		value: value,
		discount_amount: discount_amount,
		discount_percent: deal.discount_percent,
		price: price
	};
	var section1 =  AjxTemplate.expand("com_zimbra_groupon.templates.Groupon#CardSection1", subs);

	if(mode == GrouponZimlet.SHOW_FEATURED_AS_POPUP) {
		return section1;
	}

	var subs = {
		dealUrl: deal.deal_url,
		buyImg: buyImg,
		quantity_sold: deal.quantity_sold,
		itemsRemaining: itemsRemaining,
		tipping_point: deal.tipping_point
	};
	var section2 =  AjxTemplate.expand("com_zimbra_groupon.templates.Groupon#CardSection2", subs);

	var html = [];
	html.push(section1);
	html.push("<br/>");
	html.push(section2);
	return html.join("");
};

