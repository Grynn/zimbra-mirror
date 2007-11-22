/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Zimlets
 * Copyright (C) 2005, 2006, 2007 Zimbra, Inc.
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
 */

//////////////////////////////////////////////////////////////
//  Zimlet to handle integration with a Yahoo! Finance      //
//  @author Sathishkumar Sugumaran                          //
//////////////////////////////////////////////////////////////
function Com_Zimbra_YFinance() {
this._toolbar = {};

this._listeners = {};
this._listeners[ZmOperation.CLOSE] = new AjxListener(this, this._closeListener);

}

Com_Zimbra_YFinance.prototype = new ZmZimletBase();
Com_Zimbra_YFinance.prototype.constructor = Com_Zimbra_YFinance;

Com_Zimbra_YFinance.QUOTES_URL = "http://quote.yahoo.com/d/quotes.csv?f=sl1d1t1c1ohgv&e=.csv&s=";
Com_Zimbra_YFinance.STOCK_INFO_STATUS_FAILURE = "Unable to fetch stock information";

Com_Zimbra_YFinance.YAHOO_FINANCE = "YAHOO_FINANCE";

Com_Zimbra_YFinance.STOCK_PARAMS = {
			"s" : "Symbol",
			"l1": "Last Trade",
			"d1": "Last Trade Date",
			"t1": "Last Trade Time",
			"c1": "Change",
			"p2": "Change in Percent",
			"o" : "Open",
			"h" : "Day's High",
			"g" : "Day's Low",
			"v" : "Volume",
			"j6": "Percent Change From 52-week Low",
			"m4": "200-day Moving Average"	,
			"r7": "Price/EPS Estimate Next Year",
			"t8": "1 yr Target Price",
			"w" : "52-week Range",
			"m5": "Change From 200-day Moving Average"			
			
};

Com_Zimbra_YFinance.STOCK_QUERY = ["s", "l1", "d1", "t1", "c1", "p2", "o", "h", "g", "v", "j6", "m4", "r7", "t8", "w", "m5"];

Com_Zimbra_YFinance.prototype.init =
function() {

	ZmMsg.calculate = "Calculate";
	ZmMsg.calculator = "Calculator";

	ZmOperation.registerOp("CALC_CALCULATE", {textKey:"calculate"});
	ZmOperation.registerOp("CALC_IMPORT", {textKey:"_import"});
	ZmOperation.registerOp("CALC_MENU", {textKey:"calculator"});		

	this._stockSymbolCache = [];
	this._calcEngine = new CalcEngine(this);

	this._stockSymbols = [];
	this._stockStatusOld ={};
	this._stockStatusNew ={};

	this._stockStatusPollAction = new AjxTimedAction(this, this._checkStockStatus);

    var calController = AjxDispatcher.run("GetCalController");
    this._miniCal = calController ? calController.getMiniCalendar().getHtmlElement() : null;

	this._initSearchToolbar();
	
	var pollInterval = this.getUserProperty("pollInterval");
	var stockSymbols = this.getUserProperty("stockSymbols");
	
	if(pollInterval && stockSymbols){		
		this._checkStockStatus();
	}
	
	if(ZmSetting.NOTEBOOK_ENABLED){
		this._initPageEditToolbar();
	}
		
		//hack
	this._composerCtrl._preHideCallback =function(){
		return ZmController.prototype._preHideCallback.call(this);
	};
	
};

Com_Zimbra_YFinance.prototype._initPageEditToolbar =
function() {
	
	this._composerCtrl = AjxDispatcher.run("GetPageEditController");
	
	if (!this._composerCtrl) { return; }
	
	this._composerCtrl._calcEngine = this;
    
    if(!this._composerCtrl._toolbar[ZmController.NOTEBOOK_PAGE_EDIT_VIEW]) {
	      // initialize the compose controller's toolbar
	      this._composerCtrl._initializeToolBar(ZmController.NOTEBOOK_PAGE_EDIT_VIEW);
   	}    
    
    this._pageToolbar = this._composerCtrl._toolbar[ZmController.NOTEBOOK_PAGE_EDIT_VIEW];	
    	
	ZmMsg.editReport = "Edit Report";
    var op = {textKey: "editReport", tooltipKey: "editReport", image: "MonthView"};
   	var opDesc = ZmOperation.defineOperation(null, op);	    
	    
   	ZmOperation.addOperation(this._pageToolbar, opDesc.id, this._pageToolbar._buttons, 1);	    
    this._pageToolbar.addSelectionListener(opDesc.id, new AjxListener(this, this._updateReport));
};

Com_Zimbra_YFinance.prototype._initSearchToolbar =
function() {
	
	this.searchToolbar = appCtxt.getSearchController().getSearchToolbar();
	this.searchMenu =  appCtxt.getSearchController().getSearchToolbar().getButton(ZmSearchToolBar.SEARCH_MENU_BUTTON).getMenu();
	var menuId = Com_Zimbra_YFinance.YAHOO_FINANCE;
	
	ZmMsg.yfinanceLabel = "Search Yahoo Finance";
	ZmSearchToolBar.addMenuItem(menuId,
		{ 	msgKey:		"yfinanceLabel",
		 	tooltipKey:	"yfinanceLabel",
		 	icon:		"YFINANCE-panelIcon"
		});
	var mi = DwtMenuItem.create(this.searchMenu, ZmSearchToolBar.ICON[menuId], ZmMsg[ZmSearchToolBar.MSG_KEY[menuId]], null, true, DwtMenuItem.RADIO_STYLE, 0);
	mi.setData(ZmSearchToolBar.MENUITEM_ID, menuId);
	
	mi.addSelectionListener(new AjxListener(this,this.yahooFinanceSearchListener,false));
	
};

Com_Zimbra_YFinance.prototype.yahooFinanceSearchListener =
function(ev) {
	var company = this.searchToolbar.getSearchFieldValue();
	this._lookupDialog = new YSymbolLookupDialog(appCtxt._shell, null, this);
	this._lookupDialog.setSymbolsCallback(new AjxCallback(this, this._symbolsCallback));

	this._lookupDialog._lookup(company);
};

Com_Zimbra_YFinance.prototype._symbolsCallback = 
function(symbols) {
	if(symbols == "" || symbols == null){
		this.displayErrorMessage("No Search Result Found");
		return;
	}
	var s = symbols.getArray();
	var symbol = s[0];
	
	var callback = new AjxCallback(this, this._searchInfoCallback, [symbol]);
	this._searchStockInfo(symbol, callback);
};

Com_Zimbra_YFinance.prototype._searchInfoCallback = 
function(symbol, result) {
	
	var appViewMgr = appCtxt.getAppViewMgr();
	
	var resultView  = this._searchResultView;
	
	if(!this._searchResultView){
	
		ZmController.YF_RESULT_VIEW = "YFRV";	
		var buttons = [ZmOperation.CLOSE];	
		this._toolbar[ZmController.YF_RESULT_VIEW] = new ZmButtonToolBar({parent: appViewMgr._shell, buttons: buttons});
		resultView  = this._searchResultView = new DwtControl(appViewMgr._shell, "DwtListView", Dwt.ABSOLUTE_STYLE);

		this._addSelectionListeners(this._toolbar[ZmController.YF_RESULT_VIEW]);	

		var elements = {};
		elements[ZmAppViewMgr.C_APP_CONTENT] = resultView;
		elements[ZmAppViewMgr.C_TOOLBAR_TOP] = this._toolbar[ZmController.YF_RESULT_VIEW];
		appViewMgr.createView(ZmController.YF_RESULT_VIEW, null, elements);
	}

	var adContent = this.getConfig("ziya_ads");
	adContent = adContent ? adContent.replace(/\$\{symbol\}/,symbol) : "";

	var subs = {
		symbol: symbol,
		result: result,
		ads: adContent
	};

	var el = resultView.getHtmlElement();
	el.style.overflow = "auto";
	el.innerHTML = AjxTemplate.expand("com_zimbra_yfinance.templates.YFinance#SearchResult", subs);


	appViewMgr.pushView(ZmController.YF_RESULT_VIEW);
	return resultView;
};

Com_Zimbra_YFinance.prototype._addSelectionListeners =
function(toolbar) {
	
	var buttons = toolbar.opList;
	for (var i = 0; i < buttons.length; i++) {
		var button = buttons[i];
		if (this._listeners[button]) {
			toolbar.addSelectionListener(button, this._listeners[button]);
		}
	}
	
};


Com_Zimbra_YFinance.prototype._fetchStockInfo =
function(symbols, callback){
	var url = ZmZimletBase.PROXY + AjxStringUtil.urlComponentEncode(Com_Zimbra_YFinance.QUOTES_URL + symbols);
	AjxRpc.invoke(null, url, null, new AjxCallback(this, this._stockStatusCallback, [callback]), true);	
};

Com_Zimbra_YFinance.prototype._stockStatusCallback =
function(callback, result){
	
	if(!result.success){
		DBG.println("Unable to fetch stock info");
		return;
	}
	
	var r = result.text;
	DBG.dumpObj(r);
	if(!r){
		DBG.println(Com_Zimbra_YFinance.STOCK_INFO_STATUS_FAILURE);
		return;
	}
	
	var modifiedList = [];
	var modified = false;
	var parts = r.split("\n");
	for(var i in parts){
		//parse multiple parts
		var part = parts[i];
		var info = part.split(",");
		var stockInfo = this._getStockInfoObj(part);
		if(!stockInfo){
			DBG.println(Com_Zimbra_YFinance.STOCK_INFO_STATUS_FAILURE);
			return;
		}
		DBG.dumpObj(stockInfo);
		//current new status becomes old since we are pushing new status
		this._stockStatusOld[stockInfo.company] = this._stockStatusNew[stockInfo.company];
		this._stockStatusNew[stockInfo.company] = stockInfo;
		
		var oldStockInfo = this._stockStatusOld[stockInfo.company]		
		//if old stock info is empty or change doesn't match with new  stock info >> status changed
		var statusChanged = oldStockInfo ? (oldStockInfo.change != stockInfo.change) : true;
		if(statusChanged){
			stockInfo.isModified = true;
			modified = true;
		}
		modifiedList.push(stockInfo);
	}
	
	if(callback){
		callback.run(modifiedList, modified);
	}
	//DBG.dumpObj("modifiedObject :"+modifiedList);
};

Com_Zimbra_YFinance.prototype._getStockInfoObj = 
function(str) {
	if(!str) { return {} };
	var info = str.split(",");
	var stockInfo = {
			company : this.trimStr(info[0]),
			lastTrade: info[1],
			change: info[4],		
			tradeTime: this.trimStr(info[2])
	};
	return stockInfo;
	
};

Com_Zimbra_YFinance.prototype.singleClicked = 
function() {
	this.showStockSymbolConfigDlg();	
	//this._calcEngine.showCalc("bud02");	
	return;
};



Com_Zimbra_YFinance.URL = "http://quote.yahoo.com/d/quotes.csv?f=sl1d1t1c1ohgv&e=.csv&s=";
Com_Zimbra_YFinance.SUGGEST_CALLBACK = "YAHOO.Finance.SymbolSuggest.ssCallback";
Com_Zimbra_YFinance.LOOKUP_URL = "http://d.yimg.com/autoc.finance.yahoo.com/autoc?callback=" + Com_Zimbra_YFinance.SUGGEST_CALLBACK + "&query=";

Com_Zimbra_YFinance.CACHE = new Array();

// Panel Zimlet Methods
// Called by the Zimbra framework when the Ymaps panel item was double clicked
Com_Zimbra_YFinance.prototype.doubleClicked = 
function() {
	this.singleClicked();
};

Com_Zimbra_YFinance.prototype.showStockSymbolConfigDlg =
function() {
	
	if(!this._symbolsDialog){
		this._symbolsDialog = new YSymbolsDialog(appCtxt._shell, null, this);
	}	
	this._symbolsDialog.popup();	
	
};

Com_Zimbra_YFinance.prototype.menuItemSelected = function(itemId) {
	switch (itemId) {
	    case "PREFERENCES":
		this.createPropertyEditor();
		break;		
	    case "CALCULATOR":
		this._calcEngine.showCalc("bud02");	
		break;
	    case "CHECK_STOCK_STATUS":
		this._checkStockStatus(true);
		break;
		case "STOCK_POLLING_PREF":
		this.showStockSymbolConfigDlg();
		break;
		
	}
};


//importing calc result as new page
Com_Zimbra_YFinance.prototype.importResultAsPage =
function(folder, content) {
	
	var nController = AjxDispatcher.run("GetNotebookController");
	var folderId = folder ? folder.id : ZmNotebookItem.DEFAULT_FOLDER;
	var page = new ZmPage();
	page.folderId = folderId
	page.name=nController._app.generateUniqueName(folderId);
	page.setContent(content);
	
	var saveCallback = new AjxCallback(this, this._saveResponseHandler, [nController, page]);
	var saveErrorCallback = new AjxCallback(this, this._saveErrorResponseHandler, [nController, page]);
	nController._importInProgress = true;

	page.save(saveCallback, saveErrorCallback);
};


Com_Zimbra_YFinance.prototype._saveResponseHandler = 
function(nController, page, response) {

	var saveResp = response._data && response._data.SaveWikiResponse;
	if (saveResp) {
		var data = saveResp.w[0];
		if (!page.id) {
			page.set(data);
		}
		else {
			page.version = data.ver;
		}
		nController.show(page,true);
	}
	nController._importInProgress = false;
};


Com_Zimbra_YFinance.prototype._saveErrorResponseHandler = 
function(nController, page, response) {

	var msg = ZmMsg.importFailed + ": " + ZmMsg.unableToSavePage;
	var msgDialog = appCtxt.getMsgDialog();
    msgDialog.reset();
    msgDialog.setMessage(msg, DwtMessageDialog.INFO_STYLE);
    msgDialog.popup();
   	nController._importInProgress = false;
};


Com_Zimbra_YFinance.prototype._displayDialogMap = 
function(address) {
    //this.toolTipPoppedUp(null, address, null, div);
};

Com_Zimbra_YFinance.prototype.toolTipPoppedUp =
function(spanElement, obj, context, canvas) {
	
	var subs = {
		contentObjText: obj,
		src: this.getResource('blank_pixel.gif')
	};

	var imgId = ZmZimletBase.encodeId(obj)+"_img";
	var infoId = ZmZimletBase.encodeId(obj)+"_info";

	canvas.innerHTML = [
		"<table id='",  ZmZimletBase.encodeId(obj), "' bgcolor='white'>",
			"<tr align=center>",
				"<td colspan=2 id='", infoId ,"' height='20'> Loading...",
				"<td>",				
			"</tr>",
			"<tr align=center>",
				"<td colspan=2>",
				"<img width='512' height='288' id='", imgId, "' src='", this.getResource('blank_pixel.gif'), "'/>",
				"<td>",				
			"</tr>",
		"</table>"
	].join("");

	this._infoElement = document.getElementById(infoId);
	this._imgElement = document.getElementById(imgId);	
	this._lookup(obj);
};


Com_Zimbra_YFinance.prototype._lookup = function(obj) {

	obj = AjxStringUtil.trim(obj);
	obj = obj.replace(/\s/ig,"+");

	if(this._stockSymbolCache[obj]){
		this.displayStockInfo(obj,this._stockSymbolCache[obj]);
		return;
	}

	var financeURL = "http://d.yimg.com/autoc.finance.yahoo.com/autoc?callback=YAHOO.Finance.SymbolSuggest.ssCallback&query=" + obj;
	var url = ZmZimletBase.PROXY + AjxStringUtil.urlComponentEncode(financeURL);
	
	AjxRpc.invoke(null, url, null, new AjxCallback(this, this._lookupCallback, obj), true);
};

Com_Zimbra_YFinance.prototype._deserialize = 
function(str) {
	try {
		var foo;
		eval([ 'foo=', str ].join(""));
		return foo;
	} catch(ex) {
		throw new DwtException("Can't deserialize stock symbol info: malformed data\n[ "
			       + ex + " ]");
	}
};


Com_Zimbra_YFinance.prototype._lookupCallback =
function(obj, result) {

	var r = result.text;

	if(!result.success || !r) {
		this._infoElement.innerHTML = "Unable to locate stock symbol for this company";
		return;
	}
	
	r = r.replace(/^YAHOO.Finance.SymbolSuggest.ssCallback\(/ig,"");
	r = r.replace(/\)$/ig,"");
	
	
	var info  = this._deserialize(r);
	DBG.dumpObj(info);

	var symbolVal = "";
	
	var yResultSet = info ? info.ResultSet : null;
	var yResult = yResultSet ? yResultSet.Result : null;
	
	if(yResult instanceof Array){
		yResult = yResult[0];
	}
	
	symbolVal  = yResult ? yResult.symbol : "";

	if(symbolVal){
		this.displayStockInfo(obj, symbolVal);
	}

	if(symbolVal == "" || symbolVal == null) {
		this._infoElement.innerHTML = "Unable to locate stock symbol for this company";
	}
	
	return symbolVal;
};

Com_Zimbra_YFinance.prototype.displayStockInfo =
function(obj, symbolVal) {
	
	this._symbol = symbolVal;
	this._displayInfo(symbolVal);
	var url = ZmZimletBase.PROXY + AjxStringUtil.urlComponentEncode(Com_Zimbra_YFinance.URL + AjxStringUtil.urlComponentEncode(symbolVal));
	AjxRpc.invoke(null, url, null, new AjxCallback(this, this._callback, [symbolVal]), true);
	this._stockSymbolCache[obj] = symbolVal;
	
};

Com_Zimbra_YFinance.prototype._displayInfo = 
function(symbol) {
	
	 this._imgElement.src = "http://chart.finance.yahoo.com/c/6m/y/"+symbol.toLowerCase();
	
};

Com_Zimbra_YFinance.prototype._callback = 
function(obj,result) {
	var r = result.text;
	DBG.dumpObj(r);	
	var info = r.split(",");
	var displayText = [
			"<b>Company: </b>", this.trimStr(info[0]),
			" <b>Last Trade: </b>", info[1],
			" <b>Change: </b>", info[4],		
			" <b>Trade Time: </b>", this.trimStr(info[2])
	].join("");
	this._infoElement.innerHTML = displayText;
};

Com_Zimbra_YFinance.prototype.trimStr =
function(str) {
	if(!str) return null;
	str = str.replace(/^\"/,"");
	str = str.replace(/\"$/,"");
	return str;
};

Com_Zimbra_YFinance.ADD_SYMBOL_BUTTON = ++DwtDialog.LAST_BUTTON;

Com_Zimbra_YFinance.prototype._createDialog =
function(params) {
	params.parent = this.getShell();
	params.extraButtons  = [
			new DwtDialog_ButtonDescriptor(ZmFolderPropsDialog.ADD_SYMBOL_BUTTON, "Add Symbol", DwtDialog.ALIGN_LEFT)
	];
	return new ZmDialog(params);
};

Com_Zimbra_YFinance.prototype._checkStockStatus =
function(ignoreSchedule){
	
	if(ignoreSchedule && !this.getUserProperty("stockSymbols")){			
		this._symbolsDialog = new YSymbolsDialog(appCtxt._shell, null, this);
		this._symbolsDialog.popup();
		return;
	}
	
	this._displayStockStatus(ignoreSchedule);
	
	if(this._stockStatusPollAction && !ignoreSchedule){		
		var pollInterval = this.getUserProperty("pollInterval");
		
		if(pollInterval == null || pollInterval == ""){
			return;
		}
				
		pollInterval = pollInterval ? parseInt(pollInterval) : 5;
		//calc milli seconds
		pollInterval = pollInterval * 60 * 1000;
		
		AjxTimedAction.scheduleAction(this._stockStatusPollAction, pollInterval);
	}
};

Com_Zimbra_YFinance.prototype._displayStockStatus =
function(ignoreSchedule) {
	
	DBG.println("check stock status ...");	
	var symbols = this.getUserProperty("stockSymbols");	
	if(!symbols){
		return;
	}	
	symbols = symbols.replace(/,/g,"+");
	var callback = new AjxCallback(this, this.processModifiedList, [ignoreSchedule]);
	this._fetchStockInfo(symbols, callback);		
};


Com_Zimbra_YFinance.prototype.processModifiedList =
function(ignoreSchedule, modifiedList, modified){
	if(modified || ignoreSchedule){	
		this._showStockUpdate(modifiedList, true);
	}
};

Com_Zimbra_YFinance.prototype._showStockUpdate = function(modifiedList, force){
	
	var minicalDIV = document.getElementById("skin_container_tree_footer");
	var newDiv = document.getElementById("stockDiv");
	if (force)
    {       	
        if (!newDiv) {
    		newDiv = document.createElement("div");
            newDiv.id = "stockDiv";
      		//newDiv.style.overflow = "auto";
            minicalDIV.appendChild(newDiv);
            this._miniCalStyle = minicalDIV.style.overflow;
            minicalDIV.style.overflow = "auto";
        }
        newDiv.style.margin = '0px';
        var subs = {
           	modifiedList: modifiedList
        };
        newDiv.innerHTML = AjxTemplate.expand("com_zimbra_yfinance.templates.YFinance#StockStatus", subs);

        this._miniCal.style.visibility = "hidden";
		
		var timedAction = new AjxTimedAction(this, this._showStockUpdate, [modifiedList, false]);
		AjxTimedAction.scheduleAction(timedAction, 15000);
		
    }else{
    	//minicalDIV.innerHTML = "";
    	newDiv.innerHTML = "";
    	if(this._miniCalStyle){
    		minicalDIV.style.overflow = this._miniCalStyle;
    	}
    	this._miniCal.style.visibility = "visible";
    }
};


Com_Zimbra_YFinance.prototype._searchStockInfo =
function(symbols, callback){
	var zUrl = "http://quote.yahoo.com/d/quotes.csv?f="+ Com_Zimbra_YFinance.STOCK_QUERY.join("") +"&e=.csv&s="+symbols;
	var url = ZmZimletBase.PROXY + AjxStringUtil.urlComponentEncode(zUrl);
	AjxRpc.invoke(null, url, null, new AjxCallback(this, this._searchCallback, [callback]), true);	
};

Com_Zimbra_YFinance.prototype._searchCallback =
function(callback, result){
	var r = result.text;
	DBG.dumpObj(r);
	if(!r){
		DBG.println(Com_Zimbra_YFinance.STOCK_INFO_STATUS_FAILURE);
		return;
	}
	
	var resultInfo = {};	
	var parts = r.split("\n");
	
	if(!parts){
		return;
	}
	
	var part = parts[0];
	//parse multiple parts
	var part = part;
	var info = part.split(",");

	for(var i=0;i<Com_Zimbra_YFinance.STOCK_QUERY.length; i++){
		var val = Com_Zimbra_YFinance.STOCK_QUERY[i];
		var nameVal = Com_Zimbra_YFinance.STOCK_PARAMS[val];
		if(nameVal!=null && val!=null){
			resultInfo[val] = {name:nameVal, value: info[i] };
		}	
	}

	if(callback){
		callback.run(resultInfo);
	}
};

Com_Zimbra_YFinance.prototype._closeListener =
function() {
	appCtxt.getAppViewMgr().popView(true);
};

Com_Zimbra_YFinance.prototype._updateReport =
function() {
	var composeController = this._composerCtrl;
	var pageEditor = this._composerCtrl._pageEditView.getPageEditor();

	var doc = pageEditor._getIframeDoc();
	
	var resultEl  = null;
	var divs = doc.getElementsByTagName("div");
	for(var i in divs){
		if(divs[i] && divs[i].className == "ZimbraCalculatorDiv"){
			resultEl = divs[i];
			break;
		}
	}
	
	if(resultEl){
		this._calcEngine._updateInfo = {containerElement: resultEl};
	}else{
		this.displayErrorMessage("Unable to recognise calculator report for the edited page");
		return;
	}
	
	var calcData = null;
	var spans = doc.getElementsByTagName("span");
	for(var i in spans){
		if(spans[i] && spans[i].className == "ZimbraCalculatorData"){
			calcData = spans[i].innerHTML;
			calcId = spans[i].getAttribute("calcId");
			break;
		}
	}

	if(!calcData){
		this.displayErrorMessage("Unable to load calculator data");
		return;	
	}
	
	calcData = calcData.replace(/^<!--ACE\[ZmSpreadSheet\]:/,"");
	calcData = calcData.replace(/-->$/,"");

	if(calcId && calcData){
		this._calcEngine._loadCalc(calcData, calcId);
	}else{
		this.displayErrorMessage("Unable to load calculator data");
	}
};