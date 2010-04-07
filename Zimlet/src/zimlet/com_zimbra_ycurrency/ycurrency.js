/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Zimlets
 * Copyright (C) 2005, 2006, 2007, 2008, 2009, 2010 Zimbra, Inc.
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


String.prototype.trim = function() {
    var a = this.replace(/^\s+/, '');
    return a.replace(/\s+$/, '');
};

Currencies = function(){
	this.symbols =  [   {currency:"USD", syms: ["$","Dollar","US$","USD"]},
						{currency:"EUR", syms: ["€","Euro","euro","EUR"]},
						{currency:"GBP", syms: ["£","Pound","pound","GBP"]},
						{currency:"JPY", syms: ["¥","Yen","yen","JPY"]},
						{currency:"INR", syms: ["Rs","Rp","Rs.","Rupee","INR"]},
                        {currency:"AUD", syms: ["AU$","AUD"]},
                        {currency:"CAD", syms: ["Can$","CAN$","CAD"]},
                        {currency:"CHF", syms: ["SFr.","Fr.","Swiss franc","Swiss Franc","CHF"]}
                        
            ];

    this.length = this.symbols.length;
};

Currencies.prototype.getSymbols = function(curr){
	for(i=0;i<this.symbols.length;i++){
		if(this.symbols[i].currency == curr){
			return this.symbols[i].syms;
		}
	}
	return [];
};

Currencies.prototype.getCurrency = function(symbol){
	for(i=0;i<this.symbols.length;i++){
        var syms = this.symbols[i].syms;
		var s = ","+syms.join(",")+",";
        if(s.indexOf(","+symbol+",") >=0 ){
			return this.symbols[i].currency;
		}
	}
	return null;
};

Currencies.prototype.getAllCurrencies = function(){
	var currs = [];
	for(var i=0;i<this.symbols.length;i++){
		currs.push(this.symbols[i].currency);
	}
	return currs;
};

Currencies.prototype.getAllSymbols = function(){
	var symbs = [];
	for(var i=0;i<this.symbols.length;i++){
        var syms = this.symbols[i].syms;
        for(j=0;j<syms.length;j++){
			symbs.push(syms[j]);
		}
	}
	return symbs;
};

/**
 * Constructor.
 * 
 */
function Com_Zimbra_YCurrency() {
};

Com_Zimbra_YCurrency.prototype = new ZmZimletBase();
Com_Zimbra_YCurrency.prototype.constructor = Com_Zimbra_YCurrency;

Com_Zimbra_YCurrency.OPERATORS_RG = "(>|<|<=|>=|==|!=|%|\\+|\\-|\\*|\\/|\\^|power|pow|pwr|sqrt|log|sin|cos|tan|acos|asin|atan|abs|ceil|exp|round|floor)";
Com_Zimbra_YCurrency.OPERAND_RG = "\\d+(,\\d+)*[.]?\\d*";

/**
 * Defines the "home currency" user property.
 */
Com_Zimbra_YCurrency.USER_PROP_HOME_CURRENCY = "home_currency";
/**
 * Defines the "tooltip currencies" user property.
 */
Com_Zimbra_YCurrency.USER_PROP_TOOLTIP_CURRENCIES = "tooltip_currs";

/**
 * Defines the "preferences" menu item.
 */
Com_Zimbra_YCurrency.MENU_ITEM_ID_PREFERENCES = "SETTINGS";
/**
 * Defines the "convert" menu item.
 */
Com_Zimbra_YCurrency.MENU_ITEM_ID_CONVERT = "CONVERT";
/**
 * Defines the "goto Y! Finance" menu item.
 */
Com_Zimbra_YCurrency.MENU_ITEM_ID_GOTO_YAHOO_FINANCE = "GOTOYF";

/**
 * Initializes the zimlet.
 */
Com_Zimbra_YCurrency.prototype.init =
function() {
    this.currencies = new Currencies();
	this.URL = "http://finance.yahoo.com/d/quotes.html";
	this.chartURL= "http://ichart.finance.yahoo.com/t";
    this.yfnURL = "http://finance.yahoo.com";
    this.footerHtml = "<hr/><div><b><i>Powered by yahoo finance.</i></b></div>";
    var myCurr = I18nMsg["currencyCode"];
    this.setUserProperty(Com_Zimbra_YCurrency.USER_PROP_HOME_CURRENCY,myCurr?myCurr:"USD");
    this.__prev_conv_string = "1 "+this.getUserProperty(Com_Zimbra_YCurrency.USER_PROP_HOME_CURRENCY)+" = ?";
    this._initSearchToolbar();
    this.setUserProperty(Com_Zimbra_YCurrency.USER_PROP_TOOLTIP_CURRENCIES,this.currencies.getAllCurrencies().join(";"));
};

Com_Zimbra_YCurrency.prototype._getRegex = function(){
	//if(!this.regx) {
        var s = "(\\d+(,\\d+)*[.]?\\d*)\\s*";
        s += "("+this.currencies.getAllCurrencies().join("|")+"|[A-Z]{3})";
        s += "\\s*(=\\s*\\?\\s*(";
        s += "("+this.currencies.getAllCurrencies().join("|")+"|[A-Z]{3})"+"\\s*([,;]\\s*";
        s += "("+this.currencies.getAllCurrencies().join("|")+"|[A-Z]{3})"+")*)*)*";
        this.regx = new RegExp(s,"g");
    //}
    return this.regx;
};

Com_Zimbra_YCurrency.prototype.getTooltipCurrencies =
function(){
    var toCurrencies = this.currencies.getAllCurrencies();
    try{
        toCurrencies = this.getUserProperty(Com_Zimbra_YCurrency.USER_PROP_TOOLTIP_CURRENCIES).split(/[;,]/);
    }catch(ex){
        toCurrencies = this.currencies.getAllCurrencies();
   }
   return toCurrencies; 
}

Com_Zimbra_YCurrency.prototype._getMatchRegex = function(){
    //if(!this.matchReg) {
        var s = "("+this.currencies.getAllSymbols().join("|").replace(/(\$|\.)/g,"\\$1")+")\\s*";
        s += "(\\d+(,\\d+)*[.]?\\d*)";
        this.matchReg = new RegExp(s,"g");
    //}
    return this.matchReg;
};


Com_Zimbra_YCurrency.prototype.match =
function(line, startIndex) {
	this.RE = this._getMatchRegex();//this._getRegex();
    return ZmZimletBase.prototype.match.call(this,line,startIndex);
};

Com_Zimbra_YCurrency.prototype._getQuoteURL =
function(fromCurr,toCurrencies) {
	var s = this.URL+"?f=l1&s=";              // Get last traded price only
    for(var i=0;i<toCurrencies.length;i++){
       s += fromCurr+toCurrencies[i]+"=X+";
    }
    return ZmZimletBase.PROXY + AjxStringUtil.urlComponentEncode(s);
};

Com_Zimbra_YCurrency.prototype.clicked =
function(spanElement, contentObjText, matchContext, canvas) {
	var s = this.yfnURL+"/currency";//
	var curr = this.currencies.getCurrency(matchContext[1]); //matchContext[3];
	var amt = matchContext[2]; //matchContext[1]

	if(curr != this.getUserProperty(Com_Zimbra_YCurrency.USER_PROP_HOME_CURRENCY)){
		s += "/convert?amt="+amt+"&from="+curr+"&to="+this.getUserProperty(Com_Zimbra_YCurrency.USER_PROP_HOME_CURRENCY)+"&submit=Convert";
	}
	window.open(s);
};


Com_Zimbra_YCurrency.prototype._getHtmlContent =
function(html, idx, obj, context) {
	html[idx++] = [
			'<a class="',
			"Object",
			'">',
			AjxStringUtil.htmlEncode(obj),
			'</a>'
	].join("");
	return idx;
};

/**
 * Gets the tool tip height.
 * 
 * @return	{number}	the tool tip height
 */
Com_Zimbra_YCurrency.prototype._getToolTipHeight =
function(){
    var m = this.getTooltipCurrencies().length < 4 ? 4 : this.getTooltipCurrencies().length;
    return (40 + 40 + (m * 18));
}

/**
 * Called when the tool tip is popped-up.
 * 
 */
Com_Zimbra_YCurrency.prototype.toolTipPoppedUp =
function(spanElement, contentObjText, matchContext, canvas) {
	canvas.innerHTML = "<div style='width:250px;height:"+this._getToolTipHeight()+"px;vertical-align:middle'><center>Requesting yahoo finance...</center></div>";
	var callback = new AjxCallback(this, this._toolTipPoppedCallback, [ canvas, matchContext ]);
	this._makeCall(contentObjText, matchContext, callback);

};

/**
 * Tool tip popped-up callback.
 * 
 */
Com_Zimbra_YCurrency.prototype._toolTipPoppedCallback =
function(canvas, matchContext, result) { //result.success = true; result.text='1\r\n1\r\n1\r\n1\r\n1\r\n';// hack
    if(result && result.success){
      	var reqCurrency = this.currencies.getCurrency(matchContext[1]); //matchContext[3];
		var reqAmount = matchContext[2].replace(/,/g,""); //matchContext[1]
        var rates = ((result.text+"").split(/\r\n/));
        var htmlData = "<table style='width:250px;' cellspacing='2' cellspadding='1'><tr><th align='left' colspan='3'>"+matchContext[0]+" ["+reqCurrency+"] equals... <hr/></th></tr>";
        for(var i=0;i<rates.length;i++){
            var rate = rates[i];
            if(!rate || rate == undefined ||
               rate == null || rate == "" ||
               rate.trim() == "" ) { continue; }
            var toCurrencies = this.getTooltipCurrencies();
            var cur = toCurrencies[i];//this.currencies.symbols[i].currency;
            if(cur == reqCurrency) continue;
            var val =(rate*reqAmount).toFixed(4);
			var extra="";
			if(cur == this.getUserProperty(Com_Zimbra_YCurrency.USER_PROP_HOME_CURRENCY)){
				extra = "style='font-weight:bold;'";
			}
            htmlData += "<tr><th align='right'>"+val+"</th><td align='right' "+extra+">"+cur+" @</td><td "+extra+" align='right'>"+rate+"</td></tr>";//+D+ " "+cur+"</b></div>";
        }
        htmlData += "</table>";//
		if(reqCurrency != this.getUserProperty(Com_Zimbra_YCurrency.USER_PROP_HOME_CURRENCY)){
            this.hasChart = true;
            var homeCur = this.getUserProperty(Com_Zimbra_YCurrency.USER_PROP_HOME_CURRENCY);
            var ch_arg = (this.getUserProperty("chart_type")=="O_2_H")?reqCurrency+homeCur:homeCur+reqCurrency;
            htmlData = "<table style='width:250px;'><tr><td>"+htmlData+"</td><td valign='bottom'>";
			htmlData += "<img style='spacing:1px;border:1px inset gray;' src='"+this.chartURL+"?s="+ch_arg+"=X&f=w4'/></td></tr></table>";
        }else{
             this.hasChart = false; //To be used to adjust tooltip width
        }
		canvas.innerHTML = "<div style='height:"+this._getToolTipHeight()+"px; width:"+(this.hasChart?"450px":"250px;")+";'>" + htmlData + this.footerHtml + "</div>";//"<hr/><div><b><i>Powered by yahoo finance.</i></b></div>";
    }else{
        canvas.innerHTML = "<div style='color:red; vertical-align:middle;height:"+this._getToolTipHeight()+"px; width:"+(this.hasChart?"500px":"250px;")+";'><center> Error in getting exchange rate.</center></div>";// + result;
        DBG.println(AjxDebug.DBG2, "Error response "+ result);
	}
};

Com_Zimbra_YCurrency.prototype._showConvert =
function(preVal,showWait){
    var view = new DwtComposite(this.getShell());
	var container = document.createElement("DIV");
	this.aId = Dwt.getNextId();
    this.rId = Dwt.getNextId();
    preVal = (!preVal || preVal==null || preVal==undefined )? this.__prev_conv_string:preVal;
    container.innerHTML = ["<table cellspacing=4 cellpadding=0 border=0>",
							"<tr><td>","Enter conversion string (eg. 23 INR = ? USD)","</td></tr>",
							"<tr><td>","<input id='",this.aId,"' type='text' size=40 value='"+preVal+"'>","</td></tr>",
                            "<tr><td>","<div style='border:1px solid silver;overflow-y:scroll;"+ (AjxEnv.isIE ? "height:150px;":"max-height:150px;") + "' id='",this.rId,"'>",
                            "(eg. 1 USD = ?) for USD to others...<br>",
                            "(eg. 12.5 INR = ? GBP, USD) INR to multiple...<br>",
                            "</div></td></tr>",
                            "<tr><td>",
                            this.footerHtml,
                            "</td></tr>",
                            "</table>"
						].join("");
	var element = view.getHtmlElement();
	element.appendChild(container);

	var dialogTitle = 'Currency Converter';
	var dialogArgs = {
			title : dialogTitle,
			parent : this.getShell(),
			view  : view
		};
	var dlg = new ZmDialog(dialogArgs);

	dlg.setButtonListener(
			DwtDialog.CANCEL_BUTTON,
			new AjxListener(this, function() {
				dlg.popdown();
				dlg.dispose();
			}));

	dlg.setButtonListener(
		DwtDialog.OK_BUTTON,
		new AjxListener(this, function() {
			var reqString = document.getElementById(this.aId).value;
			if(!reqString){
				document.getElementById(this.rId).innerHTML =  "Please enter in a valid format like 12 USD = ? GBP or 4 GBP = ? INR,GBP etc.";
			}else{
				document.getElementById(this.rId).innerHTML = "Requesting yahoo finance...";
                var _m_context = this.getMatch(reqString);
                if(_m_context == undefined || !_m_context || _m_context==null){
                    document.getElementById(this.rId).innerHTML =  "Please enter in a valid format like 12 USD = ? GBP or 4 GBP = ? INR,GBP etc.";
                }else{
                    this._convert(reqString,_m_context);
                    this.__prev_conv_string = reqString;
                }
            }

		}));
	dlg.popup();
    if(showWait){
        document.getElementById(this.rId).innerHTML = "Wait...";
    }
};

Com_Zimbra_YCurrency.prototype.getMatch = function(reqString){
    var regex = this._getRegex();
    return regex.exec(reqString);
};

Com_Zimbra_YCurrency.prototype._handleConvert = function(matchContext, result) { //result.success = true; result.text='1\r\n1\r\n1\r\n1\r\n1\r\n';// hack
    if(result && result.success){
        var reqCurrency = matchContext[3].trim();//.split(",");
        var reqAmount = matchContext[1].replace(/,/g,"").trim();
        var rates = ((result.text+"").split(/\r\n/));
        var toCurrencies = [];
        if(matchContext[5]){
            toCurrencies = matchContext[5].split(/\s*[,;]\s*/);
        }else{
            toCurrencies = this.currencies.getAllCurrencies();
        }
        var ihtml = "<table>";
        for(var i=0;i<toCurrencies.length;i++){
            if(toCurrencies[i].trim()==reqCurrency) continue; // if req is same as to curr, skip the result
            var value = (reqAmount*rates[i]).toFixed(4);
             ihtml += "<tr><td>"+reqAmount + " "+reqCurrency + " = </td><td align='right'><b>" + value + " " + toCurrencies[i].trim() + "</b> @ </td><td align='right'>" + rates[i] + "</td></tr>";// + "<hr/>";
        }
        ihtml += "</table>";
        document.getElementById(this.rId).innerHTML = ihtml;//"<hr/><div><b><i>Powered by yahoo finance.</i></b></div>";
    }else{
		document.getElementById(this.rId).innerHTML =  "<div style='width:200px;color:red;'>Error in getting exchange rate!</div>";
	}
};

Com_Zimbra_YCurrency.prototype._makeCall = function(reqString, context,callback,srcCurr){
	var toCurrencies = [];
	//var fromCurrency = context[3];
    if(context[5]){
        toCurrencies = context[5].split(/\s*[,;]\s*/);
    }else{
        toCurrencies = this.getTooltipCurrencies();
    }
	if(srcCurr){
		fromCurrency = srcCurr;
	}else{
		fromCurrency = this.currencies.getCurrency(context[1]);
	}
	var url = this._getQuoteURL(fromCurrency,toCurrencies);
    AjxRpc.invoke(null, url, null, callback, true);
};

Com_Zimbra_YCurrency.prototype._convert =
function(reqString, context){
   	var callback = new AjxCallback(this, this._handleConvert, [ context ]);
	this._makeCall(reqString, context, callback,context[3]);
};

/**
 * Called when zimlet menu item is selected.
 * 
 */
Com_Zimbra_YCurrency.prototype.menuItemSelected =
function(itemId, label, spanElement, contentObjText, canvas) {
	switch (itemId) {
	    case Com_Zimbra_YCurrency.MENU_ITEM_ID_PREFERENCES:
			this.createPropertyEditor();
			break;
		case Com_Zimbra_YCurrency.MENU_ITEM_ID_CONVERT:
	        var q = this._actionObject;
	        if(q){
	            _sym = q.replace(/(\d+(,\d+)*[.]?\d*)/ig,"");
	            if(_sym){
	                _sym = _sym.trim();
	                _amt = q.replace(_sym,"").trim();
	                _cc = this.currencies.getCurrency(_sym);
	                q = _amt + " " + _cc + " = ?";
	            }
	        }
	        this._showConvert(q);
			break;
        case Com_Zimbra_YCurrency.MENU_ITEM_ID_GOTO_YAHOO_FINANCE:
	        window.open(this.yfnURL);
	        break;
    }
};
Com_Zimbra_YCurrency.prototype._initSearchToolbar =
function() {
	ZmMsg.ycurrencyLabel = "Currency calculator";
    //ZmMsg.calcLabel = "Calculator";
    this.addSearchDomainItem("YCURRENCY-panelIcon", ZmMsg.ycurrencyLabel, new AjxListener(this, this.convertSearchListener));
    //this.addSearchDomainItem(null, ZmMsg.calcLabel, new AjxListener(this, function(ev){appCtxt.getSearchController().getSearchToolbar().setSearchFieldValue(this.calculate(AjxStringUtil.trim(this.getSearchQuery(), true)));}));
};

/*Com_Zimbra_YCurrency.prototype.calcSearchListener =
function(ev) {
    var qstring = AjxStringUtil.trim(this.getSearchQuery(), true);
    var _m_context = this.getMatch(qstring);
};*/

Com_Zimbra_YCurrency.prototype.convertSearchListener =
function(ev) {
    this._searchBar = appCtxt.getSearchController().getSearchToolbar();
    var qstring = AjxStringUtil.trim(this.getSearchQuery(), true);
    qstring = AjxStringUtil.stripTags(qstring);
    if(qstring.toLocaleLowerCase() == qstring){ // Let's treat is as calculator expression
        var res = this.calculate(qstring);
        this._searchBar.setSearchFieldValue(res);
        return;
    }
    var _m_context = this.getMatch(qstring);
    if(_m_context == undefined || !_m_context || _m_context == null){
        this.displayErrorMessage("Malformed query! Enter in a valid format like 12 USD = ? GBP etc.");
        return;
    }else{
        if(_m_context[5]){
           var currencies = _m_context[5].trim().split(/[,;]/);
           if(currencies.length > 1){
              this._showConvert(qstring,true);
              this._convert(qstring,_m_context);
              return;
           }
        }else{                                              
            _m_context[5] = this.getUserProperty(Com_Zimbra_YCurrency.USER_PROP_HOME_CURRENCY); // Convert to home currency
        }
        if(_m_context[3].trim() == _m_context[5].trim()){ //From to same
            this.displayErrorMessage(_m_context[3]+" is your home currency. Enter currency you wanted to convert it to.");
            return;
        }
        var callback = new AjxCallback(this, this._showConvertSearchResult, [ _m_context ]);
	    this._makeCall(qstring, _m_context, callback,_m_context[3]);
        this.__prev_conv_string = qstring;
        appCtxt.getShell().setBusy(true);
    }
};

Com_Zimbra_YCurrency.prototype._showConvertSearchResult =
function(_m_context, result){
    appCtxt.getShell().setBusy(false);
    if(result && result.success){
        var rates = ((result.text+"").split(/\r\n/));
        var reqAmount = _m_context[1].replace(/,/g,"");
        var val =(rates[0]*reqAmount).toFixed(4);
        this._searchBar.setSearchFieldValue(_m_context[1] + " " + _m_context[3] + " = " + val + " "+_m_context[5] +" @ "+rates[0]);
    }
};

Com_Zimbra_YCurrency.prototype.getValidExpression = function(string){
    var str = "("+Com_Zimbra_YCurrency.OPERAND_RG+")*\\s*" + Com_Zimbra_YCurrency.OPERATORS_RG+"\\s*" + "("+Com_Zimbra_YCurrency.OPERAND_RG +")*";
    var rgxp = new RegExp(str,"g");
    return rgxp.exec(string);
};

Com_Zimbra_YCurrency.prototype.calculate =
function(string){
    string = AjxStringUtil.stripTags(string);// Strip down html tags  
    var _m_context = this.getValidExpression(string);
    if(!_m_context){
        return string;
    }
    if(_m_context[3].match(/[a-z]{3,5}/)){
            string = "Math."+_m_context[3].trim()+"("+_m_context[4]+");";
    }else if(_m_context[3] == "^" ){//|| _m_context[2] == "pow" || _m_context[2] == "power" || _m_context[2] == "pwr"){
            string = "Math.pow("+_m_context[1]+","+_m_context[4]+");";
    }
    return eval(string);
}