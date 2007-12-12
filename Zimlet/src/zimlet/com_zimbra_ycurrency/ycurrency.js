
/**
 * @author rpatil@zimbra.com
 * @version 0.1
 * The handler class for the Yahoo Currency Zimlet...
 */

String.prototype.trim = function() {
    var a = this.replace(/^\s+/, '');
    return a.replace(/\s+$/, '');
};


Currencies = function(){
	this.symbols =  [{currency:"USD", syms: ["$","Dollar","US$"]},
						{currency:"EUR", syms: ["€","Euro","euro"]},
						{currency:"GBP", syms: ["£","Pound","pound"]},
						{currency:"JPY", syms: ["¥","Yen","yen"]},
						{currency:"INR", syms: ["Rs","Rp","Rs."]}];

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
	for(i=0;i<this.symbols.length;i++){
		currs.push(this.symbols[i].currency);
	}
	return currs;
};

Currencies.prototype.getAllSymbols = function(){
	var symbs = [];
	for(i=0;i<this.symbols.length;i++){
        var syms = this.symbols[i].syms;
        for(j=0;j<syms.length;j++){
			symbs.push(syms[j]);
		}
	}
	return symbs;
};


function Com_Zimbra_YCurrency() {
};

Com_Zimbra_YCurrency.prototype = new ZmZimletBase();
Com_Zimbra_YCurrency.prototype.constructor = Com_Zimbra_YCurrency;

Com_Zimbra_YCurrency.prototype.init =
function() {

    this.currencies = new Currencies();
	this.URL = "http://finance.yahoo.com/d/quotes.html";
	this.chartURL= "http://ichart.finance.yahoo.com/t";
    this.yfnURL = "http://finance.yahoo.com";
    this.footerHtml = "<hr/><div><b><i>Powered by yahoo finance.</i></b></div>";
    var myCurr = I18nMsg["currencyCode"];
    this.setUserProperty("home_currency",myCurr?myCurr:"USD");
    this.__prev_conv_string = "1 "+this.getUserProperty("home_currency")+" = ?";

};

Com_Zimbra_YCurrency.prototype._getRegex = function(){
	//if(!this.regx) {
        var s = "(\\d+(,\\d+)*[.]?\\d*)\\s*";
        s += "("+this.currencies.getAllCurrencies().join("|")+")";
        s += "\\s*(=\\s*\\?\\s*(";
        s += "("+this.currencies.getAllCurrencies().join("|")+"|[A-Z]{3})"+"\\s*([,;]\\s*";
        s += "("+this.currencies.getAllCurrencies().join("|")+"|[A-Z]{3})"+")*)*)*";
        this.regx = new RegExp(s,"g");
    //}
    return this.regx;
};

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
    for(i=0;i<toCurrencies.length;i++){
       s += fromCurr+toCurrencies[i]+"=X+";
    }
    return ZmZimletBase.PROXY + AjxStringUtil.urlComponentEncode(s);
};

Com_Zimbra_YCurrency.prototype.clicked =
function(spanElement, contentObjText, matchContext, canvas) {
	var s = this.yfnURL+"/currency";//
	var curr = this.currencies.getCurrency(matchContext[1]); //matchContext[3];
	var amt = matchContext[2]; //matchContext[1]

	if(curr != this.getUserProperty("home_currency")){
		s += "/convert?amt="+amt+"&from="+curr+"&to="+this.getUserProperty("home_currency")+"&submit=Convert";
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


Com_Zimbra_YCurrency.prototype.toolTipPoppedUp =
function(spanElement, contentObjText, matchContext, canvas) {
	canvas.innerHTML = "<div style='width:250px;height:"+(60 + (this.currencies.length-1)*20)+"px;v-align:middle'><center>Requesting yahoo finance...</center></div>";
	var callback = new AjxCallback(this, this._callback, [ canvas, matchContext ]);
	this._makeCall(contentObjText,matchContext,callback);

};

Com_Zimbra_YCurrency.prototype._callback =
function(canvas, matchContext, result) { //result.success = true; result.text='1\r\n1\r\n1\r\n1\r\n1\r\n';// hack
    if(result && result.success){
      	var reqCurrency = this.currencies.getCurrency(matchContext[1]); //matchContext[3];
		var reqAmount = matchContext[2].replace(/,/g,""); //matchContext[1]
        var rates = ((result.text+"").split(/\r\n/));
        var htmlData = "<table style='width:250px;' cellspacing='2' cellspadding='1'><tr><th align='left' colspan='2'>"+matchContext[0]+" equals... <hr/></th></tr>";
        for(i=0;i<rates.length;i++){
            var rate = rates[i];
            if(!rate || rate == undefined ||
               rate == null || rate == "" ||
               rate.trim() == "" ) { continue; }

            var cur = this.currencies.symbols[i].currency;
            if(cur == reqCurrency) continue;
            var val =(rate*reqAmount).toFixed(4);
			var extra="";
			if(cur == this.getUserProperty("home_currency")){
				extra = "style='font-weight:bold;'";
			}
            htmlData += "<tr><th align='right'>"+val+"</th><td "+extra+">"+cur+" (@ "+rate+" ) </td></tr>";//+D+ " "+cur+"</b></div>";
        }
        htmlData += "</table>";//
		if(reqCurrency != this.getUserProperty("home_currency")){
            this.hasChart = true;
            var homeCur = this.getUserProperty("home_currency");
            var ch_arg = (this.getUserProperty("chart_type")=="O_2_H")?reqCurrency+homeCur:homeCur+reqCurrency;
            htmlData = "<table style='width:250px;'><tr><td>"+htmlData+"</td><td>";
			htmlData += "<img style='spacing:1px;border:1px inset gray;' src='"+this.chartURL+"?s="+ch_arg+"=X&f=w4'/></td></tr></table>";
        }else{
             this.hasChart = false; //To be used to adjust tooltip width
        }
		canvas.innerHTML = "<div style='height:"+(60 + (this.currencies.length-1)*20)+"px; width:"+(this.hasChart?"450px":"250px;")+";'>" + htmlData + this.footerHtml + "</div>";//"<hr/><div><b><i>Powered by yahoo finance.</i></b></div>";
    }else{
        canvas.innerHTML = "<div style='color:red; valign:middle;height:"+(60 + (this.currencies.length-1)*20)+"px; width:"+(this.hasChart?"450px":"250px;")+";'><center> Error in getting exchange rate.</center></div>";// + result;
        DBG.println(AjxDebug.DBG2, "Error response "+ result);
	}
};

Com_Zimbra_YCurrency.prototype._showConvert = function(preVal){
    var view = new DwtComposite(this.getShell());
	var container = document.createElement("DIV");
	var aId = Dwt.getNextId();
    this.rId = Dwt.getNextId();
    preVal = (!preVal || preVal==null || preVal==undefined )? this.__prev_conv_string:preVal;
    container.innerHTML = ["<table cellspacing=4 cellpadding=0 border=0>",
							"<tr><td>","Enter conversion string (eg. 23 INR = ? USD)","</td></tr>",
							"<tr><td>","<input id='",aId,"' type='text' size=40 value='"+preVal+"'>","</td></tr>",
                            "<tr><td>","<div style='max-height:100px;"+ (AjxEnv.isIE ? "height:auto !important;height:100px;":"") + "' id='",this.rId,"'>",
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
	var dialogArgs = {title : dialogTitle,view  : view };
	var dlg = this._createDialog(dialogArgs);


	dlg.setButtonListener(
			DwtDialog.CANCEL_BUTTON,
			new AjxListener(this, function() {
				dlg.popdown();
				dlg.dispose();
			}));

	dlg.setButtonListener(
		DwtDialog.OK_BUTTON,
		new AjxListener(this, function() {
			var reqString = document.getElementById(aId).value;
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
            toCurrencies = matchContext[5].split(/[,;]\s*/);
        }else{
            toCurrencies = this.currencies.getAllCurrencies();
        }
        var ihtml = "";
        for(i=0;i<toCurrencies.length;i++){
            if(toCurrencies[i].trim()==reqCurrency) continue; // if req is same as to curr, skip the result
            var value = (reqAmount*rates[i]).toFixed(4);
             ihtml += "<hr/>"+reqAmount + " "+reqCurrency + " = <b>" + value + " " + toCurrencies[i].trim() + "</b> @ " + rates[i];// + "<hr/>";
        }
        document.getElementById(this.rId).innerHTML = ihtml;//"<hr/><div><b><i>Powered by yahoo finance.</i></b></div>";
    }else{
		document.getElementById(this.rId).innerHTML =  "<div style='width:200px;color:red;'>Error in getting exchange rate!</div>";
	}
};

Com_Zimbra_YCurrency.prototype._makeCall = function(reqString, context,callback,srcCurr){
	var toCurrencies = [];
	//var fromCurrency = context[3];
    if(context[5]){
        toCurrencies = context[5].split(/[,;]/);
    }else{
        toCurrencies = this.currencies.getAllCurrencies();
    }
	if(srcCurr){
		fromCurrency = srcCurr;
	}else{
		fromCurrency = this.currencies.getCurrency(context[1]);
	}
	var url = this._getQuoteURL(fromCurrency,toCurrencies);
    AjxRpc.invoke(null, url, null, callback, true);
};

Com_Zimbra_YCurrency.prototype._convert = function(reqString, context){
   	var callback = new AjxCallback(this, this._handleConvert, [ context ]);
	this._makeCall(reqString, context, callback,context[3]);
};

Com_Zimbra_YCurrency.prototype.menuItemSelected = function(itemId, label, spanElement, contentObjText, canvas) {
	switch (itemId) {
	    case "SETTINGS":
		this.createPropertyEditor();
		break;
		case "CONVERT":
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
        case "GOTOYF":
        window.open(this.yfnURL);
        break;
    }
};