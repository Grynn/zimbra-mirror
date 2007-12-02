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
//  Zimlet to handle integration with a Yahoo! Maps         //
//  @author Kevin Henrikson                                 //
//////////////////////////////////////////////////////////////
function CalcEngine(parent) {
	this._parent = parent
	this._appViewMgr = appCtxt.getAppViewMgr();
	this._listeners = {};
	this._toolbar = {};
	this._listeners[ZmOperation.CLOSE] = new AjxListener(this, this._closeListener);
	this._listeners[ZmOperation.CALC_CALCULATE] = new AjxListener(this, this._calcListener);	
	this._listeners[ZmOperation.CALC_IMPORT] = new AjxListener(this, this._importListener);	
	this._listeners[ZmOperation.CALC_MENU] = new AjxListener(this, this._calcSelectionListener);	
	this._sheetData = "";
	this._updateInfo = null;
}

CalcEngine.prototype.constructor = CalcEngine;

CalcEngine.prototype.getXmlString = function(subs) {
	var xmlStr = AjxTemplate.expand("com_zimbra_yfinance.templates.YFinance#CalcXmlRequest", subs);
	return xmlStr;
};

CalcEngine.prototype.processCalculatorResponse = 
function(resultText) {
	
	var result = AjxXmlDoc.createFromXml().toJSObject(true, false);	
	var calcxmlResponse = result ? result.calcxmlResponse : null;	
	var htmlValues = result ? result.htmlValues : null;	
	var htmlDataTable = htmlValues ? htmlValues.htmlDataTable : null;
	var content = htmlDataTable ? htmlDataTable.__msh_content : "";
	
	return {htmlContent:content};
	
};

CalcEngine.prototype.showCalc = 
function(calcId) {
	this._updateInfo = null;
	var calStr = AjxTemplate.expand("com_zimbra_yfinance.templates.YFinance#"+calcId);
	this._loadCalc(calStr, calcId);
};

CalcEngine.prototype._loadCalc =
function(calStr, calcId) {
	
	var appViewMgr = this._appViewMgr;		
	this._pendingData = calStr;
	
	if(!this._calcView){		
		this._calcView = this._createCalcView(calcId);
	}else{
		appViewMgr.pushView(ZmController.CALC_VIEW);
		CalcEngine._iframeOnLoad(this._iframe);
	}
};

CalcEngine.prototype._createCalcView =
function(calcId){
	this._calcId = calcId;
	var appViewMgr = this._appViewMgr;	
	ZmController.CALC_VIEW = "CALC";
	
	var buttons = this._getToolbarOps();
	this._toolbar[ZmController.CALC_VIEW] = new ZmButtonToolBar({parent: appViewMgr._shell, buttons: buttons});
	var calcView = this._calcView = new DwtControl(appViewMgr._shell, "DwtListView", Dwt.ABSOLUTE_STYLE);
	var el = calcView.getHtmlElement();
	var htmlArr = [];
	var idx = 0;
	var iframeId = this._iframeId = Dwt.getNextId();
	var src =this._sheetSrc = appContextPath + "/public/Spreadsheet.jsp";
	
	htmlArr[idx++] = ["<iframe id='",iframeId,"' src='", src, "' frameborder='0' ", "onload='CalcEngine._iframeOnLoad(this)' style='height:100%;width:100%;'>","</iframe>"].join("");
	el.innerHTML = htmlArr.join("");

	var elements = {};
	elements[ZmAppViewMgr.C_APP_CONTENT] = calcView;
	elements[ZmAppViewMgr.C_TOOLBAR_TOP] = this._toolbar[ZmController.CALC_VIEW];
	appViewMgr.createView(ZmController.CALC_VIEW, null, elements);
	this._addSelectionListeners(this._toolbar[ZmController.CALC_VIEW]);
	this._iframe = document.getElementById(iframeId);
	Dwt.associateElementWithObject(this._iframe, this);
	appViewMgr.pushView(ZmController.CALC_VIEW);
	this._setupCalcMenuItems();
	return calcView;
};

CalcEngine.prototype._addSelectionListeners =
function(toolbar) {
	
	var buttons = toolbar.opList;
	for (var i = 0; i < buttons.length; i++) {
		var button = buttons[i];
		if (this._listeners[button]) {
			toolbar.addSelectionListener(button, this._listeners[button]);
		}
	}
	
};

CalcEngine.prototype._getToolbarOps =
function() {
	return [ZmOperation.CALC_CALCULATE, ZmOperation.SEP, ZmOperation.CALC_MENU, ZmOperation.SEP, ZmOperation.CLOSE];
};

CalcEngine.prototype.onOKPress = 
function(calcId)  {
	var inputInfo = getInputInfo(calcId);	
	
};

CalcEngine.prototype.calculate =
function(){
	if(this._iframe){
		var calcId = this._calcId;
		var cwin = this._iframe.contentWindow;
		var str = cwin.serialize();
		DBG.dumpObj(str);
		this._sheetData = str;
		var result = this._deserialize(str);
		
		var data = result.data;
		
		var resultCells = this.getInputInfo(calcId);
		var calcInput = [];
				
		if(data){
			for(var i in data){
				var row = data[i];
				if(!row) return;
				for(var j = 0;j < row.length; j++){
					var cell = row[j];
					if(cell.editValue!="" && cell.editValue!=""){
						if(resultCells[cell.editValue] && row[cell.col]){
							var dataCol = row[cell.col]
							
							if( (dataCol.type =="percentage") && (dataCol.editValue == "")){
								dataCol.editValue = "0%";
							}							
							if( dataCol.editValue && (!dataCol.editValue.match(/\%/)) && dataCol.type =="percentage"){
								dataCol.editValue  = dataCol.editValue + "%";
							}
							if( dataCol.editValue && (!dataCol.editValue.match(/\$/)) && dataCol.type =="currency"){
								dataCol.editValue  = dataCol.editValue + "$";
							}
							var val = this._validateInput(row[cell.col].editValue, resultCells[cell.editValue].validate, resultCells[cell.editValue]);
							if(val != null){
								resultCells[cell.editValue].value = val;
								calcInput[resultCells[cell.editValue].name] = val;
							}else{
								return;
							}
						}
					}	
				}
			}
		}
		
		DBG.println("ResultCells...");
		DBG.dumpObj(resultCells);
		this._sendXmlRequest(calcInput,resultCells);
		
	}
};

CalcEngine.prototype._sendXmlRequest =
function(calcInput, resultCells) {
	var subs = {
		title: 'Chart Title',
		username: this._parent.getConfig("calcxml_username"),
		password: this._parent.getConfig("calcxml_password"),
		calcInputName: this._calcId,
		calcInput: calcInput
	};

	var r = AjxTemplate.expand("com_zimbra_yfinance.templates.YFinance#CalcXmlRequest", subs);
	DBG.println("Calcxml Request...");
	DBG.dumpObj(r);
	
	var calcXmlURL = "http://www.calcxml.com/do/xmlHttpRequest";
	var reqHeader = {"Content-Type":"text/xml"};
	var callback = new AjxCallback(this,this._xmlRequestCallback);

	var url = ZmZimletBase.PROXY + AjxStringUtil.urlComponentEncode(calcXmlURL);	
	AjxRpc.invoke(r, url, null, callback, false);
};

CalcEngine.prototype._xmlRequestCallback =
function(response) {
	var calcId = this._calcId;
	var z =  response.text;
	//todo: result status
	var result = AjxXmlDoc.createFromXml(z).toJSObject(true, false);	
	
	DBG.println("CalcXml Response ...");
	DBG.dumpObj(result);
	
	if(result && result.message && result.message.__msh_content){
		this._parent.displayErrorMessage(result.message.__msh_content);
		return;
	}
	
	var calcxmlResponse = result ? result.calcxmlResponse : null;	
	var htmlValues = result ? result.htmlValues : null;	
	var rawValues = result ? result.rawValues : null;	
	var calcOutput = (rawValues && rawValues[calcId + "Out"]) ? rawValues[calcId + "Out"] : null;
	var chartUrlNode = calcOutput ? calcOutput.chartUrl : null;
	var chartUrl = chartUrlNode ? chartUrlNode.__msh_content : null;
	var htmlDataTable = htmlValues ? htmlValues.htmlDataTable : null;
	var htmlChartUrl = htmlValues ? htmlValues.chartUrl : null;
	var htmlText = (htmlValues && htmlValues.responseText) ? htmlValues.responseText.__msh_content : "";
	var content = htmlDataTable ? htmlDataTable.__msh_content : "";
	var imgHTML = htmlChartUrl ? htmlChartUrl.__msh_content : "";
	DBG.println("html content from calcxml ...");
	var data = {
		title : CalcEngine.CALC_TITLE[calcId],
		summary: htmlText,
		imgHTML: imgHTML,
		content: content
	};
	var printContent = AjxTemplate.expand("com_zimbra_yfinance.templates.YFinance#ResultView", data);
	DBG.dumpObj(content);
	this._calcData = {chartUrl : chartUrl, content: content};
	this.showResults(printContent);
	//this._parent.importResultAsPage(content);
};

CalcEngine.prototype._deserialize = 
function(str) {
	try {
		var foo;
		eval([ 'foo=', str ].join(""));
		return foo;
	} catch(ex) {
		throw new DwtException("Can't deserialize in ZmSpreadSheetModel: malformed data\n[ "
			       + ex + " ]");
	}
};

CalcEngine.prototype._calcListener =
function() {
	this.calculate();
	//var content = '<?xml version="1.0" encoding="UTF-8"?><calcxmlResponse xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:noNamespaceSchemaLocation="http://host3.calcxml.com/schema/calcxmlResponse.xsd" version="1.1"><htmlValues><responseText>Underthe proposed payment terms it will take 32.0 more payments or 2.7 years to pay off the remaining balance. Interest will amount to $1,314.</responseText><chartUrl>&lt;img alt="chart image" src="http://host3.calcxml.com/charts/chart1194141887218422.png"&gt;</chartUrl><htmlDataTable>&lt;table class=\'calctable\'&gt;&lt;tr&gt;&lt;th&gt;Year&lt;/th&gt;&lt;th&gt;Beg Balance&lt;/th&gt;&lt;th&gt;Interest&lt;/th&gt;&lt;th&gt;Annual Payment&lt;/th&gt;&lt;th&gt;End Balance&lt;/th&gt;&lt;/tr&gt;&lt;tr&gt;&lt;td&gt;1&lt;/td&gt;&lt;td&gt;$5,000&lt;/td&gt;&lt;td&gt;$770&lt;/td&gt;&lt;td&gt;$2,400&lt;/td&gt;&lt;td&gt;$3,370&lt;/td&gt;&lt;/tr&gt;&lt;tr&gt;&lt;td&gt;2&lt;/td&gt;&lt;td&gt;3,370&lt;/td&gt;&lt;td&gt;451&lt;/td&gt;&lt;td&gt;2,400&lt;/td&gt;&lt;td&gt;1,421&lt;/td&gt;&lt;/tr&gt;&lt;tr&gt;&lt;td&gt;3&lt;/td&gt;&lt;td&gt;1,421&lt;/td&gt;&lt;td&gt;93&lt;/td&gt;&lt;td&gt;1,514&lt;/td&gt;&lt;td&gt;0&lt;/td&gt;&lt;/tr&gt;&lt;/table&gt;</htmlDataTable></htmlValues><rawValues><det02Out><numPayments>32.0</numPayments><yearsToPayoff>2.7</yearsToPayoff><interestPaid>1,314</interestPaid><chartUrl>http://host3.calcxml.com/charts/chart1194141887218422.png</chartUrl><years><year year="1" beginningBalance="5,000" interest="770" payment="2,400" endingBalance="3,370" /><year year="2" beginningBalance="3,370" interest="451" payment="2,400" endingBalance="1,421"/><year year="3" beginningBalance="1,421" interest="93" payment="1,514" endingBalance="0" /></years></det02Out></rawValues><calcInput><det02><loanBalance>5000</loanBalance><interestRate>0.18</interestRate><monthlyPayment>200</monthlyPayment></det02></calcInput></calcxmlResponse>';
	//todo: remove this work around hack
	//var content = '<div style="text-align:center;width:100%;padding:3px;"><img alt="chart image" src="http://host3.calcxml.com/charts/chart1194141887218422.png"><table class=\'calctable\'><tr><th>Year</th><th>Beg Balance</th><th>Interest</th><th>Annual Payment</th><th>End Balance</th></tr><tr><td>1</td><td>$5,000</td><td>$770</td><td>$2,400</td><td>$3,370</td></tr><tr><td>2</td><td>3,370</td><td>451</td><td>2,400</td><td>1,421</td></tr><tr><td>3</td><td>1,421</td><td>93</td><td>1,514</td><td>0</td></tr></table></div>';
	//this.showResults(content);
};

CalcEngine.prototype._importListener =
function() {
	//todo: import action to a notebook page
	var content = this._resultView ? this._resultView.getHtmlElement().innerHTML : "" ;
	
	if(!ZmSetting.NOTEBOOK_ENABLED) return;
	
	AjxDispatcher.require(["NotebookCore", "Notebook"]);
	
	var app =  appCtxt.getApp(ZmApp.NOTEBOOK);
	if(app._deferredFolders.length != 0){
		app._createDeferredFolders(ZmApp.NOTEBOOK);
	}
	
	if(this._updateInfo){
		var updateInfo = this._updateInfo;
		var content = this._resultView ? this._resultView.getHtmlElement().firstChild.innerHTML : "" ;	
		content = [content , "<span class='ZimbraCalculatorData' calcId='", this._calcId, "'><!--ACE[ZmSpreadSheet]:",this._sheetData, "--></span><br>"].join("");
		updateInfo.containerElement.innerHTML = content;
		this._appViewMgr.pushView(ZmController.NOTEBOOK_PAGE_EDIT_VIEW);	
	}else{	
		var copyToDialog = this._copyToDialog = appCtxt.getChooseFolderDialog();
		var _chooseCb = new AjxCallback(this, this._chooserCallback, [content]);
		ZmController.showDialog(copyToDialog, _chooseCb, this._getCopyParams());
	}	
};

CalcEngine.prototype._getCopyParams =
function() {
	var org = ZmOrganizer.NOTEBOOK;
	var title = ZmMsg.notebook;
	return {treeIds:[org], overviewId:"ZmListController",
			title:title, description:ZmMsg.targetFolder};	
};

CalcEngine.prototype._chooserCallback =
function(content, folder) {

	this._importFolder = folder;		
	var chartUrl = this._calcData.chartUrl;

	if(!chartUrl){
		this._parent.importResultAsPage(folder, content);	
		this._copyToDialog.popdown();		
		return;
	}

	var imageCaption = chartUrl.replace(/^.*\//,"");
	var callback = new AjxCallback(this, this._importContent);
	this.attachImage(chartUrl, imageCaption, callback);
		
};

CalcEngine.prototype._importContent = 
function() {		
	var content = this._resultView ? this._resultView.getHtmlElement().innerHTML : "" ;	
	content = [content , "<span class='ZimbraCalculatorData' calcId='", this._calcId, "'><!--ACE[ZmSpreadSheet]:", this._sheetData, "--></span><br>"].join("");
	this._parent.importResultAsPage(this._importFolder, content);	
	this._copyToDialog.popdown();
	
	//if mode is update
};

CalcEngine.prototype._closeListener =
function() {
	this._appViewMgr.popView(true);
};

CalcEngine.prototype._calcSelectionListener =
function(ev) {
	if (ev.detail == DwtMenuItem.CHECKED ||
		ev.detail == DwtMenuItem.UNCHECKED)
	{
			var calcId = ev.item.getData(ZmOperation.MENUITEM_ID);
			this._calcId = calcId;
			var calStr = AjxTemplate.expand("com_zimbra_yfinance.templates.YFinance#"+calcId);
			this._pendingData = calStr;
			CalcEngine._iframeOnLoad(this._iframe);
	}
};
CalcEngine.prototype.showResults =
function(content) {
	var appViewMgr = this._appViewMgr;
	if(!this._resultView){
		this._resultView = this._createResultView(content);
	}else{
		var el = this._resultView.getHtmlElement();

		var htmlArr = [];
		var idx = 0;
	
		htmlArr[idx++] = content;
		el.style.overflow = "auto";
		el.innerHTML = htmlArr.join("");		
	}
	appViewMgr.pushView(ZmController.CALC_RESULT_VIEW);
	this._addTableStyles();

	var button = this._toolbar[ZmController.CALC_RESULT_VIEW].getButton(ZmOperation.CALC_IMPORT);
	if(button){
		button.setText(this._updateInfo ? "Update" : "Import");
	}
	//hack
	//this._importFolder = appCtxt.getById(ZmOrganizer.ID_NOTEBOOK);
	// = function(imageURL,imageCaption,callback){
	//this.attachImage('http://us.i1.yimg.com/us.yimg.com/i/ww/beta/y3.gif','eee.jpg');
};

CalcEngine.prototype._addTableStyles =
function() {
	
	var el = this._resultView.getHtmlElement();
	var tables = el.getElementsByTagName("table");
	if(!tables){ return; }
	for(var i =0 ; i < tables.length; i++){
		if(tables[i].className = "calctable"){
			var table  = tables[i];
			this._addCellStyles(table);
		}
	}
	
};

CalcEngine.prototype._addCellStyles =
function(table) {
	if(!table){ return; }

	table.style.font = "10px verdana";
	table.cellSpacing = 0;
	table.cellPadding = 0;
	table.align = "center";
	table.width = "70%";
	
	var rows = table.rows;
	for (var j = 0; j < rows.length; ++j) {
		var cells = rows[j].cells;
		var index = 0;
		for (var k = 0; k < cells.length; ++k) {
			var td = cells[k];
			if(k==0){
				td.style.borderLeft = "1px solid #000000";
			}
			if(j==0){
				td.style.borderBottom = "1px solid #000000";
				td.style.color = "white";
				td.style.backgroundColor = "purple";				
			}else{
				td.style.borderBottom = "1px dotted #000000";
			}
			td.style.borderRight = "1px solid #000000";
			td.style.padding ="3px";
		}
	}

};

CalcEngine.prototype._createResultView = 
function(content) {
	var appViewMgr = this._appViewMgr;
	ZmController.CALC_RESULT_VIEW = "CALCR";
	
	var buttons = [ZmOperation.CALC_IMPORT, ZmOperation.SEP, ZmOperation.CLOSE];
	
	this._toolbar[ZmController.CALC_RESULT_VIEW] = new ZmButtonToolBar({parent: appViewMgr._shell, buttons: buttons});
	var resultView  = new DwtControl(appViewMgr._shell, "CalcResultView", Dwt.ABSOLUTE_STYLE);
	var el = resultView.getHtmlElement();

	var htmlArr = [];
	var idx = 0;

	htmlArr[idx++] = content;
	el.style.overflow = "auto";
	el.innerHTML = htmlArr.join("");

	var elements = {};
	elements[ZmAppViewMgr.C_APP_CONTENT] = resultView;
	elements[ZmAppViewMgr.C_TOOLBAR_TOP] = this._toolbar[ZmController.CALC_RESULT_VIEW];

	appViewMgr.createView(ZmController.CALC_RESULT_VIEW, null, elements);
	this._addSelectionListeners(this._toolbar[ZmController.CALC_RESULT_VIEW]);

	return resultView;
};

CalcEngine._iframeOnLoad =
function(iframe, callCount){

	try{
	    var calcEngine = Dwt.getObjectFromElement(iframe);
		var calData = calcEngine._pendingData;
		if(!calData){ return; }

		var cwin = iframe.contentWindow;
		if(cwin && cwin.spreadSheet){			
			cwin.ZmACE = true;			
			//if(AjxEnv.isIE){
				var link = cwin.spreadSheet._getFocusLink();
				if(link){
					cwin.focus();
					link.focus();
					link.focus();
				}
				cwin.spreadSheet._getLeftHeaderCell = function(td) {
					try{
						return this._getTable().rows[td.parentNode.rowIndex].cells[0];
					}catch(e){
						this._getTable().rows[0].cells[0];
					}
				};
			//}
			cwin.deserialize(calData);
		}else{
			DBG.println("iframeLoad:"+callCount);
			if(callCount == null || callCount>0){
				var action = new AjxTimedAction(window, window.CalcEngine._iframeOnLoad, [iframe,((callCount!=null)?callCount-1:100)]);
				AjxTimedAction.scheduleAction(action, 50);
			}else{
				this._parent.displayErrorMessage("Failed to load calculator");				
			}
			return;
		}
		calcEngine._pendingData = null;
	}catch(ex){
	}
};

//try
CalcEngine.prototype.attachImage = 
function(imageURL,imageCaption,callback){
	
	if(!callback){
		callback = false;
	}
	
	var reqParams = [
			"upload=1","&",
            "fmt=raw","&",
            "filename=",imageCaption
	].join("");


    var serverURL = ZmZimletBase.PROXY + AjxStringUtil.urlComponentEncode(imageURL) + "&" + reqParams;

    var ajxCallback = new AjxCallback(this,this.done_attachImage,[callback,imageURL,imageCaption]);
    AjxRpc.invoke(reqParams,serverURL,null,ajxCallback,true);
};

CalcEngine.prototype.done_attachImage = 
function(callback,imageURL,imageCaption,result){

    var uploadResponse = result.text;
	
	if(!uploadResponse){
		this._parent.displayErrorMessage("Failed to import image <b>"+imageCaption+"</b> to this notebook");
		callback.call(this,null,imageURL,imageCaption);
		return;
	}
	var response = AjxStringUtil.split(uploadResponse,',');
	if(response.length <= 2){
		this._parent.displayErrorMessage("Failed to import image <b>"+imageCaption+"</b> to this notebook");
		callback.call(this,null,imageURL,imageCaption);
		return;
	}

    var len = response[2].length;
    var attachmentId = response[2];
    if(attachmentId.indexOf("\r\n") != -1){
        attachmentId = attachmentId.substring(0,len-2);
        len = attachmentId.length;
    }
    attachmentId = attachmentId.substring(1,len-1);

	/*
    if(callback){
		callback.run(attachmentId,imageURL,imageCaption);	
	}
	*/
	
	DBG.println("attachmentId:" + attachmentId);
	
	if(attachmentId){
		attachmentId = attachmentId.replace(/'$/,"");
		var uploadDialog = appCtxt.getUploadDialog();
		uploadDialog._uploadCallback = new AjxCallback(this, this._imageUploadCallback, [callback]);
		uploadDialog._uploadFolder =  this._importFolder;
		var files = new Array();
		var file = new Object();
		file.guid = attachmentId;
		files.push(file);
		uploadDialog._uploadSaveDocs2(files, null,  null);
	}
};

CalcEngine.prototype._imageUploadCallback =
function(callback, folder, filenames) {
	if(filenames && filenames.length>0){
		var el = this._resultView.getHtmlElement();
		var imgs = el.getElementsByTagName("img");
		if(imgs && imgs.length){
			imgs[0].src = filenames[0];	
		}
	}
	
	if(callback){
		callback.run();
	}

};

CalcEngine.prototype._setupCalcMenuItems =
function(view) {
	var calcId = this._calcId;
	var viewBtn = this._toolbar[ZmController.CALC_VIEW].getButton(ZmOperation.CALC_MENU);
	var menu = viewBtn ? viewBtn.getMenu() : null;
	if (!menu) {
		menu = new ZmPopupMenu(viewBtn);
		viewBtn.setMenu(menu);
		for (var i in CalcEngine.CALC_TITLE) {
			var mi = menu.createMenuItem(i, { text: CalcEngine.CALC_TITLE[i],
											  style:DwtMenuItem.RADIO_STYLE});
			mi.setData(ZmOperation.MENUITEM_ID, i);
			mi.addSelectionListener(this._listeners[ZmOperation.CALC_MENU]);
			if (i == calcId){
				mi.setChecked(true, true);
			}
		}
	}
	return menu;
};


CalcEngine.prototype._validateInput =
function(str, id, desc){
	var msg = null;
	var result = null;
	
	if(!str){
		if(desc.defaultValue){ return desc.defaultValue; }
		this._parent.displayErrorMessage(desc.label + " cannot be empty, example value:"+desc.example);	
		return null;
	}
	
	if(id == "empty" || id == null){
		return str;
	}
	
	switch(id){
		case "dollars":
		case "loanBalance" :
			if(str.match(/\$/)){
				str = str.replace(/\$/,"");
			}
			if(!str.match(AjxUtil.FLOAT_RE)){
				msg = desc.label + " is invalid, example value:" + desc.example;
			}
			result = str;
			break;

		case "interestRate" :
			if(str.match(/\%/)){
				str = str.replace(/\%/,"");
				var val = parseInt(str);
				if(val >=0 && val<=100){
					str = val/100;
				}
			}else{
				var val = parseFloat(str);
				if(val>1){
					msg = desc.label + " is out of range, example value:" + desc.example;
				}
			}
			result = str;
			break;
		case "bud02Mode":			
			if(! str.match(/^monthly$|^annual$/i)){
				msg = desc.label +" is invalid, example value:" +desc.example;
			}
			result = str.match(/^monthly$/i) ? 0 : 1;
			break;
		case "yesNo":
			if(! str.match(/^Y$|^N$/i)){
				msg = desc.label +" is invalid, example value:" +desc.example;
			}
			result = str.match(/^Y$/i) ? Y : N;
			break;
		case "age":
			if(str.match(/^\d+$/) && parseInt(str)>=1 && parseInt(str)<=99){
				result = str;
			}else{
				msg = desc.label + " is invalid, example value:" + desc.example;
			}
			break;
		case "0to12":
			if(str.match(/\%/)){
				str = str.replace(/\%/,"");
				if(str.match(AjxUtil.FLOAT_RE) && parseFloat(str)>=0 && parseFloat(str)<=12){
					result = parseFloat(str)/100;
				}else{
					msg = desc.label + " is invalid, example value:" + desc.example;					
				}
			}else if(str.match(AjxUtil.FLOAT_RE) && parseFloat(str)>=0 && parseFloat(str)<=0.12){
				result = str;
			}else{
				msg = desc.label + " is invalid, example value:" + desc.example;
			}
			break;			

		case "0to50":
			if(str.match(/\%/)){
				str = str.replace(/\%/,"");
				if(str.match(AjxUtil.FLOAT_RE) && parseFloat(str)>=0 && parseFloat(str)<=50){
					result = parseFloat(str)/100;
				}else{
					msg = desc.label + " is invalid, example value:" + desc.example;					
				}

			}else if(str.match(AjxUtil.FLOAT_RE) && parseFloat(str)>=0 && parseFloat(str)<=0.50){
				result = str;
			}else{
				msg = desc.label + " is invalid, example value:" + desc.example;
			}
			break;			
		case "0to10":
			if(str.match(/\%/)){
				str = str.replace(/\%/,"");
				if(str.match(AjxUtil.FLOAT_RE) && parseFloat(str)>=0 && parseFloat(str)<=10){
					result = parseFloat(str)/100;
				}else{
					msg = desc.label + " is invalid, example value:" + desc.example;					
				}

			}else if(str.match(AjxUtil.FLOAT_RE) && parseFloat(str)>=0 && parseFloat(str)<=0.10){
				result = str;
			}else{
				msg = desc.label + " is invalid, example value:" + desc.example;
			}
			break;			
		
	};
	
	if(msg){
		this._parent.displayErrorMessage(msg);
		return null;
	}
	return result;
};

CalcEngine.prototype.getInputInfo = 
function(calcId) {
	
	switch(calcId) {
		case "det02" : return this.getLoanCalculatorInfo();
					   break;
		case "bud02" : return this.getPersonalExpenseCalculatorInfo();
					   break;
		case "det05" : return this.getLoanBalance();
					   break;
		case "hom03" : return this.getMortgageCalc();
					   break;					   
		case "hom01" : return this.getAffordableAmount();
					   break;
		case "sav01" : return this.getBecomeMillionaire();
					   break;
	};
	
};

//All the calculator input specification methods goes here
CalcEngine.prototype.getLoanCalculatorInfo =
function() {
	var inputInfo =	{
		"Loan Amount" : {label:"Loan Amount",name:"loanBalance", type:"string", example: "1000", validate: "loanBalance", defaultValue: 0},
		"Annual interest rate" : {label:"Annual interest rate",name:"interestRate", type:"string", example: "0.22 or 22%", validate: "interestRate", defaultValue: 0},
		"Current monthly payment" : {label:"Current monthly payment",name:"monthlyPayment", type:"string", validate: "empty", example: "1"}
	};
	return inputInfo;	
};

CalcEngine.prototype.getPersonalExpenseCalculatorInfo =
function() {
	var inputInfo =	{
		"Monthly or annual figures? (Monthly/Annual)" : {label:"Monthly or annual figures? (Monthly/Annual)",name:"mode", type:"string", example: "Monthly or Annual", validate: "bud02Mode", defaultValue: 0},
		"Mortgage payment or rent" : {label:"Mortgage payment or rent",name:"mortgage", type:"string", example: "300$ or 300", validate: "dollars", defaultValue: 0},
		"Vacation home (mortgage)" : {label:"Vacation home (mortgage)",name:"vacation", type:"string", validate: "dollars", example: "200$ or 200", defaultValue:0},
		"Automobile loan(s)" 	   : {label:"Automobile loan(s)",name:"autoloan", type:"string", validate: "dollars", example: "200$ or 200", defaultValue:0},
		"Personal loan(s)" 		   : {label:"Personal loan(s)",name:"persloan", type:"string", validate: "dollars", example: "200$ or 200 or $200", defaultValue:0},
		"Charge accounts" 		   : {label:"Charge accounts",name:"cc", type:"string", validate: "dollars", example: "100$ or 100 or $100", defaultValue:0},
		"Federal income taxes" 	   : {label:"Federal income taxes",name:"fedtaxes", type:"string", validate: "dollars", example: "100$ or 100 or $100", defaultValue:0},
		"State income taxes" 	   : {label:"State income taxes",name:"statetaxes", type:"string", validate: "dollars", example: "100$ or 100 or $100", defaultValue:0},
		"FICA (social security taxes)" : {label:"FICA (social security taxes)",name:"fica", type:"string", validate: "dollars", example: "100$ or 100 or $100", defaultValue:0},
		"Real estate taxes" 	   : {label:"Real estate taxes",name:"retaxes", type:"string", validate: "dollars", example: "100$ or 100 or $100", defaultValue:0},
		"Other taxes" 			   : {label:"Other taxes",name:"othertaxes", type:"string", validate: "dollars", example: "100$ or 100 or $100", defaultValue:0},
		"Utilities" 			   : {label:"Utilities",name:"utilities", type:"string", validate: "dollars", example: "100$ or 100 or $100", defaultValue:0},
		"Household repairs and maintenance" : {label:"Household repairs and maintenance",name:"repairs", type:"string", validate: "dollars", example: "100$ or 100 or $100", defaultValue:0},
		"Food" 					   : {label:"Food",name:"food", type:"string", validate: "dollars", example: "100$ or 100 or $100", defaultValue:0},
		"Clothing and laundry" 	   : {label:"Clothing and laundry",name:"clothing", type:"string", validate: "dollars", example: "100$ or 100 or $100", defaultValue:0},		
		"Educational expenses"	   : {label:"Educational expenses",name:"education", type:"string", validate: "dollars", example: "100$ or 100 or $100", defaultValue:0},		
		"Child care"			   : {label:"Child care",name:"childcare", type:"string", validate: "dollars", example: "100$ or 100 or $100", defaultValue:0},		
		"Automobile expenses (gas, repairs, etc.)"  : {label:"Automobile expenses (gas, repairs, etc.)",name:"autoexp", type:"string", validate: "dollars", example: "100$ or 100 or $100", defaultValue:0},		
		"Other transportation expenses" 		    : {label:"Other transportation expenses",name:"transexp", type:"string", validate: "dollars", example: "100$ or 100 or $100", defaultValue:0},		
		"Life insurance premiums"  : {label:"Life insurance premiums",name:"lifeins", type:"string", validate: "dollars", example: "100$ or 100 or $100", defaultValue:0},		
		"Homeowners (renters) insurance"		    : {label:"Homeowners (renters) insurance",name:"homeowners", type:"string", validate: "dollars", example: "100$ or 100 or $100", defaultValue:0},														
		"Automobile insurance"	   : {label:"Automobile insurance",name:"autoins", type:"string", validate: "dollars", example: "100$ or 100 or $100", defaultValue:0},
		"Medical, dental and disability insurance"  : {label:"Medical, dental and disability insurance",name:"medical", type:"string", validate: "dollars", example: "100$ or 100 or $100", defaultValue:0},		
		"Entertainment and dining" : {label:"Entertainment and dining",name:"entertain", type:"string", validate: "dollars", example: "100$ or 100 or $100", defaultValue:0},
		"Recreation and travel"	   : {label:"Recreation and travel",name:"travel", type:"string", validate: "dollars", example: "100$ or 100 or $100", defaultValue:0},
		"Club dues"		    	   : {label:"Club dues",name:"club", type:"string", validate: "dollars", example: "100$ or 100 or $100", defaultValue:0},
		"Hobbies"		    	   : {label:"Hobbies",name:"hobbies", type:"string", validate: "dollars", example: "100$ or 100 or $100", defaultValue:0},
		"Gifts"		    		   : {label:"Gifts",name:"gifts", type:"string", validate: "dollars", example: "100$ or 100 or $100", defaultValue:0},
		"Major home improvements and furnishings"	: {label:"Major home improvements and furnishings",name:"homeimp", type:"string", validate: "dollars", example: "100$ or 100 or $100", defaultValue:0},
		"Professional services"    : {label:"Professional services",name:"services", type:"string", validate: "dollars", example: "100$ or 100 or $100", defaultValue:0},
		"Charitable contributions" : {label:"Charitable contributions",name:"charity", type:"string", validate: "dollars", example: "100$ or 100 or $100", defaultValue:0},
		"Other and miscellaneous expenses"		    : {label:"Other and miscellaneous expenses",name:"other", type:"string", validate: "dollars", example: "100$ or 100 or $100", defaultValue:0}
	};
	return inputInfo;	
};


CalcEngine.prototype.getLoanBalance =
function(){
	var inputInfo =	{
		"Current monthly payment" : {label:"Current monthly payment",name:"paymentAmount", type:"string", example: "1000 or 1000$", validate: "dollars", defaultValue: 0},
		"Annual interest rate" : {label:"Annual interest rate",name:"interestRate", type:"string", example: "0.22 or 22%", validate: "interestRate", defaultValue: 0},
		"Number of months remaining" : {label:"Number of months remaining",name:"termMonths", type:"string", validate: "empty", example: "60", defaultValue: 60},
		"Desired amortization schedule" : {label:"Desired amortization schedule",name:"amortization", type:"string", validate: "empty", example: "2", defaultValue: 2}
	};
	return inputInfo;	
};

CalcEngine.prototype.getMortgageCalc =
function(){
	var inputInfo = {
		"Loan amount" : {label:"Loan amount",name:"loanAmount", type:"string", example: "10000 or 10000$", validate: "dollars", defaultValue: 0},
		"Annual interest rate" : {label:"Annual interest rate",name:"interestRate", type:"string", example: "0.22 or 22%", validate: "interestRate", defaultValue: 0},
		"Number of months:" : {label:"Number of months:",name:"termMonths", type:"string", validate: "empty", example: "120", defaultValue: 120},
		"Desired amortization schedule" : {label:"Desired amortization schedule",name:"amortization", type:"string", validate: "empty", example: "2", defaultValue: 2},
		"Sale price of property" : {label:"Sale price of property",name:"propertyValue", type:"string", validate: "empty", example: "0", defaultValue: 0},
		"Let system estimate property taxes and insurance?(Y/N)" : {label:"Let system estimate property taxes and insurance? (Y/N)",name:"estimateTipmi", type:"string", validate: "empty", example: "Y", defaultValue: Y},
		"Annual property taxes" : {label:"Annual property taxes",name:"propertyTaxes", type:"string", validate: "empty", example: "0", defaultValue: 0},
		"Annual hazard insurance" : {label:"Annual hazard insurance",name:"hazardInsurance", type:"string", validate: "empty", example: "0", defaultValue: 0},
		"Monthly private mortgage insurance" : {label:"Monthly private mortgage insurance",name:"pmi", type:"string", validate: "empty", example: "0", defaultValue: 0}
	};
	return inputInfo;
};

CalcEngine.prototype.getAffordableAmount = 
function(){
	var inputInfo =	{
		"Current combined annual income" : {label:"Current combined annual income",name:"combinedIncome", type:"string", example: "1000 or 1000$", validate: "dollars", defaultValue: 0},
		"Monthly child support payments" : {label:"Monthly child support payments",name:"childSupport", type:"string", example: "1000 or 1000$", validate: "dollars", defaultValue: 0},
		"Monthly auto payments" : {label:"Monthly auto payments",name:"autoPayments", type:"string", validate: "dollars", example: "1000 or 1000$", defaultValue: 0},
		"Monthly credit card payments" : {label:"Monthly credit card payments",name:"ccPayments", type:"string", validate: "dollars", example: "1000 or 1000$", defaultValue: 0},
		"Monthly association fees" : {label:"Monthly association fees",name:"associationFees", type:"string", validate: "dollars", example: "1000 or 1000$", defaultValue: 0},
		"Other monthly obligations" : {label:"Other monthly obligations",name:"otherObligations", type:"string", validate: "dollars", example: "1000 or 1000$", defaultValue: 0},
		"Annual interest rate on new mortgage: (%)" : {label:"Annual interest rate on new mortgage: (%)",name:"mortgageRate", type:"string", validate: "interestRate", example: "0.22 or 22%", defaultValue: 0.06},
		"Term of new mortgage: (years)" : {label:"Term of new mortgage: (years)",name:"mortgageTerm", type:"string", validate: "empty", example: "40", defaultValue: 30},
		"Funds available for a down payment" : {label:"Funds available for a down payment",name:"downPayment", type:"string", validate: "dollars", example: "1000 or 1000$", defaultValue: 0},
		"Estimated annual property taxes" : {label:"Estimated annual property taxes",name:"propertyTaxes", type:"string", validate: "dollars", example: "1000 or 1000$", defaultValue: 0},
		"Estimated annual homeowner's insurance" : {label:"Estimated annual homeowner's insurance",name:"hazardInsurance", type:"string", validate: "dollars", example: "1000 or 1000$", defaultValue: 0},
		"Front-end ratio: (%)" : {label:"Front-end ratio: (%)",name:"frontRatio", type:"string", validate: "interestRate", example: "0.22 or 22%", defaultValue: 0.28},
		"Back-end ratio: (%)" : {label:"Back-end ratio: (%)",name:"backRatio", type:"string", validate: "interestRate", example: "0.22 or 22%", defaultValue: 0.36}

	};
	return inputInfo;	
};
CalcEngine.prototype.getBecomeMillionaire =
function(){
	var inputInfo =	{
		"Current age (1-99)" : {label:"Current age (1-99)",name:"currentAge", type:"string", example: "25", validate: "age", defaultValue: 35},
		"Age to accumulate a million dollars (1-99)" : {label:"Age to accumulate a million dollars (1-99)",name:"retirementAge", type:"string", example: "65", validate: "empty", defaultValue: 65},
		"Initial balance or deposit" : {label:"Initial balance or deposit",name:"initialBalance", type:"string", validate: "dollars", example: "1000 or 1000$", defaultValue: 0},
		"Annual increase on new savings (0-12)%" : {label:"Annual increase on new savings (0-12)%",name:"savingsIncrease", type:"string", validate: "0to12", example: "2 or 11%", defaultValue: 0},
		"Before-tax return on savings (0-12)%" : {label:"Before-tax return on savings (0-12)%",name:"beforeTaxReturn", type:"string", validate: "interestRate", example: "1 or 11%", defaultValue: 0.08},
		"Marginal tax bracket (0-50)%" : {label:"Marginal tax bracket (0-50)%",name:"taxBracket", type:"string", validate: "0to50", example: "0.22 or 22%", defaultValue: 0.25},
		"Anticipated inflation rate (0-10)%" : {label:"Anticipated inflation rate (0-10)%",name:"inflationRate", type:"string", validate: "0to10", example: "0.08 or 8%", defaultValue: 0}
	};
	return inputInfo;	
};

CalcEngine.CALC_TITLE =  {
	"det02" : "What Would My Loan Payments Be?" ,
	"bud02" : "How Much Am I Spending?" ,
	"det05" : "What Is The Balance On My Loan?",
	"sav01" : "Becoming A Millionaire"
};

//	"hom03" : "Comprehensive Mortgage Calculator",
//	"hom01" : "How Much Home Can I Afford?" ,

