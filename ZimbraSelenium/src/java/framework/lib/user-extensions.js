Selenium.prototype.doCallFunctions = function(completeFunction) {
	/**
	 *Calls other javascript functions that are passed in string format
	 * e.g: "this.doClickZFolder(\"Inbox\",\"verify_fldr:=zti|Mail|2\")"
	 *
	 * @param completeFunction Pass the complete function
	 */
	 /*
	//open log window and set start logging
	if (LOG.getLogWindow() == null) {
		LOG.show();
		LOG.setLogLevelThreshold(3);
		this.doWaitForPopUp(LOG.getLogWindow(), 5000);
	}
	*/

	return eval( "("+  completeFunction + ")");


};

Selenium.prototype.pause = function (millis)
{
	var date = new Date();
	var curDate = null;

	do {
		curDate = new Date();
	} while (curDate - date < millis);
}

//BUTTON FUNCTIONS......
Selenium.prototype.doClickZButton = function(locator, param1, param2) {
	return Selenium.decorateFunctionWithTimeout(fnBind(this.buttonCore, this.browserbot, locator, "click",  param1, param2), 30000);
};
Selenium.prototype.doWaitZButton = function(locator, param1, param2) {
	return Selenium.decorateFunctionWithTimeout(fnBind(this.buttonCore, this.browserbot, locator, "wait",  param1, param2), 30000);
};
Selenium.prototype.doZButtonNotExists = function(locator, param1, param2) {
	return this.buttonCore(locator, "notexists",  param1, param2);
};
Selenium.prototype.doZButtonExists = function(locator, param1, param2) {
	return this.buttonCore(locator, "exists",  param1, param2);
};
Selenium.prototype.verifyZButtonEnabled = function(locator, param1, param2) {
	return Selenium.decorateFunctionWithTimeout(fnBind(this.buttonCore, this.browserbot, locator, "enabled",  param1, param2), 30000);
};
Selenium.prototype.verifyZButtonDisabled = function(locator, param1, param2) {
	return Selenium.decorateFunctionWithTimeout(fnBind(this.buttonCore, this.browserbot, locator, "disabled",  param1, param2), 30000);
};

//FOLDER FUNCTIONS......
Selenium.prototype.doClickZFolder = function(locator, param1, param2) {	
	return Selenium.decorateFunctionWithTimeout(fnBind(this.folderCore, this.browserbot, locator, "click",  param1, param2), 30000);
};
Selenium.prototype.doRightClickZFolder = function(locator, param1, param2) {
	return Selenium.decorateFunctionWithTimeout(fnBind(this.folderCore, this.browserbot, locator, "rtclick",  param1, param2), 30000);
};
Selenium.prototype.doExpandZFolder = function(locator, param1, param2) {
	return Selenium.decorateFunctionWithTimeout(fnBind(this.folderExpandBtnCore, this.browserbot, locator, "click",  param1, param2), 30000);
};
Selenium.prototype.doZfolderExpandBtnExists = function(locator, param1, param2) {
	return this.folderExpandBtnCore(locator, "exists",  param1, param2);
};
Selenium.prototype.doCollapseZFolder = function(locator, param1, param2) {
	return Selenium.decorateFunctionWithTimeout(fnBind(this.folderCollapseBtnCore, this.browserbot, locator, "click",  param1, param2), 30000);
};
Selenium.prototype.doZFolderNotExists = function(locator, param1, param2) {
	return this.folderCore(locator, "notexists",  param1, param2);
};
Selenium.prototype.doZFolderExists = function(locator, param1, param2) {
	return this.folderCore(locator, "exists",  param1, param2);
};
Selenium.prototype.doWaitZFolder = function(locator, param1, param2) {
	return Selenium.decorateFunctionWithTimeout(fnBind(this.folderCore, this.browserbot, locator, "wait",  param1, param2), 30000);
};

//MENU FUNCTIONS......
Selenium.prototype.doClickZMenuItem = function(locator, param1, param2) {
	return Selenium.decorateFunctionWithTimeout(fnBind(this.menuItemCore, this.browserbot, locator, "click",  param1, param2), 30000);
};

Selenium.prototype.doZMenuItemNotExists = function(locator, param1, param2) {
	return this.menuItemCore(locator, "notexists",  param1, param2);
};
Selenium.prototype.doZMenuItemExists = function(locator, param1, param2) {
	return this.menuItemCore(locator, "exists",  param1, param2);	
};
Selenium.prototype.isZMenuItemEnabled = function(locator, param1, param2) {
	return this.menuItemCore(locator, "enabled",  param1, param2);
};
Selenium.prototype.isZMenuItemDisabled = function(locator, param1, param2) {
	return this.menuItemCore(locator, "disabled",  param1, param2);
};

Selenium.prototype.doShiftClickZButton = function(locator, verifyParam) {
	/**
	 * Shift Clicks zimbra button
	 *
	 * @param locator button label
	 * @param verifyParam verify_text verify_btn verify_tab verify_dlg verify_view verify_menu verify_tbl verify_fldr
	 */
	
	this.browserbot.shiftKeyDown = true;
	this.doClickZButton(locator, verifyParam);
	this.browserbot.shiftKeyDown = false;

};

Selenium.prototype.doCtrlClickZButton = function(locator, verifyParam) {
	/**
	 *  Clicks zimbra button with control-btn down
	 *
	 * @param locator button label
	 * @param verifyParam verify_text verify_btn verify_tab verify_dlg verify_view verify_menu verify_tbl verify_fldr
	 */
	
	this.browserbot.controlKeyDown = true;
	this.doClickZButton(locator, verifyParam);
	this.browserbot.controlKeyDown = false;

};

Selenium.prototype.doClickZMail = function(mailSubject, verifyParam) {
	/**
	 * Clicks zimbra mail
	 *
	 * @param mailSubject subject of the mail
	 * @param verifyParam verify_msgBdyInHyb
	 */

	var element = this.findZMail(mailSubject);
	this.clickZElement(element);
	if (verifyParam.indexOf(":=") >= 0)
		return this._addWaitDecorator(verifyParam);
};

Selenium.prototype.doDblClickZMail = function(mailSubject, verifyParam) {
	/**
	 * Double clicks zimbra mail
	 *
	 * @param mailSubject subject of the mail
	 * @param verifyParam verify_msgBdyInConv
	 */

	var element = this.findZMail(mailSubject);
	this.doubleclickZElement(element);
	if (verifyParam.indexOf(":=") >= 0)
		return this._addWaitDecorator(verifyParam);
};

Selenium.prototype.doClickZHTMLButton = function(locator, verifyParam) {
	/**
	 * Clicks zimbra(HTML) button
	 *
	 * @param locator button label
	 * @param verifyParam verify_text verify_btn verify_tab verify_dlg verify_view verify_menu verify_tbl verify_fldr
	 */

	var element = this.browserbot.findElement(locator);
	this.browserbot.clickElement(element);
	if (verifyParam.indexOf(":=") >= 0)
		return this._addWaitDecorator(verifyParam);
};

Selenium.prototype.doZTextAreaSet = function(name, value) {
	/**
	 * Clicks zimbra button
	 *
	 * @param name Textarea field label(text near the field)
	 * @param value Textarea field value
	 */

	this._setText(name, value, "textarea","textarea", 1);
};

Selenium.prototype.doZCheckboxSet = function(name) {
	/**
	 * Clicks zimbra button
	 *
	 * @param name Textarea field label(text near the field)
	 */

	var element = this.findZFormObject(name, "input", "checkbox", 1);
	element.checked=true;
};

Selenium.prototype.doZEditSet = function(name, value) {
	/**
	 * Clicks zimbra button
	 *
	 * @param name field label(text near the field)
	 * @param value field value
	 */

	this._setText(name, value, "input", "text");
};

Selenium.prototype.doZPasswordEditSet = function(name, value) {
	/**
	 * Clicks zimbra button
	 *
	 * @param name field label(text near the field)
	 * @param value field value
	 */

	this._setText(name, value, "input", "password");
};

Selenium.prototype._setText = function(name, value, tag, type) {
	var element = this.findZFormObject(name, tag, type);
	if (this.browserbot.shiftKeyDown)
		value = new String(value).toUpperCase();
	this.browserbot.replaceText(element, value);
};

Selenium.prototype.doClickZIconButton = function(locator, verifyParam) {
	/**
	 * Clicks Zimbra Icon button
	 *
	 * @param locator button virtual name.
	 * @param verifyParam verify_text verify_btn verify_tab verify_dlg verify_view verify_menu verify_tbl verify_fldr
	 */
	if (locator == "Detach")
		var className = "ImgOpenInNewWindow";

	var element = this.findZIconButton(className);
	this.clickZElement(element);


	if (verifyParam.indexOf(":=") >= 0)
		return this._addWaitDecorator(verifyParam);

};

Selenium.prototype.doOpenMailOrComposeInNewWindow = function(locator, verifyParam) {
	/**
	 * Clicks on Detach button to open  a new window
	 *
	 * @param locator button virtual name(Detach).
	 * @param verifyParam verify_text verify_btn verify_tab verify_dlg verify_view verify_menu verify_tbl verify_fldr
	 */
	
	if(locator.indexOf("=")>0) {	
		var element = this.browserbot.findElementOrNull(locator);
	} else {
		if (locator == "Detach") {
		var className = "ImgOpenInNewWindow";
	var element = this.findZIconButton(className);
		}
	}
	this.doShiftClickZButton(locator, "");
	this.doSelectWindow("_blank");

	if (verifyParam.indexOf(":=") >= 0)
		return this._addWaitDecorator(verifyParam);

};

Selenium.prototype.doOpenZmAjaxClient = function() {
	/**
	 * Opens Zimbra if senium is deployed within Zimbra Server(only for Selenium CORE)
	 *
	 */
	var loc = this.browserbot.getCurrentWindow().document.location;
	this.doOpen(loc.protocol + "//" + loc.host);
}
Selenium.prototype.doZVerifyTitle = function(locator) {
	/**
	 * Verifies if title *contains* a string
	 *
	 * @param locator partial or full window title
	 */
	var title = this.browserbot.getTitle();

	Assert.contains(locator, title);

	/*
	if((locator.indexOf(title)>=0) || (title.indexOf(locator)>=0)) {
		 //return new PredicateResult(true, "Actual value '" + locator  + "' did match '" + title + "'");
		Assert.matches(title, locator);
	} else {
		Assert.fail("Actual Title: " + title);
	 // return new PredicateResult(false, "Actual value '" + locator + "' did not match '" + title + "'");
	}*/

};

Selenium.prototype.doCloseDlgIfExists = function(dlgNameCommaBtnName) {
	/**
	 * Closes zimbra Dialog by clicking on a button iff dialog exists. timeout 3Seconds
	 *
	 * @param dlgNameCommaBtnName dialog name comma button name
	 */
	var arry = dlgNameCommaBtnName.split(",");
	var buttonName, dialogName;
	(arry[0] != undefined) ? dialogName = arry[0] : dialogName = "";
	(arry[1] != undefined) ? buttonName = arry[1] : buttonName = "";

	return Selenium.decorateFunctionWithTimeout(fnBind(this._closeDlgIfExists, this, dlgNameCommaBtnName), 3000);


}
Selenium.prototype.doClickZButtonInDlg = function(btnNameCommaDlgName, verifyParam) {
	/**
	 * Clicks Zimbra button in a dialog
	 *
	 * @param btnNameCommaDlgName button name comma dialog name
	 * @param verifyParam verify_text verify_btn verify_tab verify_dlg verify_view verify_menu verify_tbl verify_fldr
	 */
	var arry = btnNameCommaDlgName.split(",");
	var buttonName, dialogName;
	(arry[0] != undefined) ? buttonName = arry[0] : buttonName = "";
	(arry[1] != undefined) ? dialogName = arry[1] : dialogName = "";
	var element = this.findZButtonInDlg(buttonName, dialogName);
	this.clickZElement(element);

	if (verifyParam.indexOf(":=") >= 0)
		return this._addWaitDecorator(verifyParam);

};



Selenium.prototype._addWaitDecorator = function(verifyParam) {
	/**
	 *   (private)Adds a wait decorator
	 *
	 * @param verifyParam verify_text verify_btn verify_tab verify_dlg verify_view verify_menu verify_tbl verify_fldr
	 */

	var txt = verifyParam.split(":=")[1];
	if (verifyParam.indexOf("verify_text:=") >= 0) {
		return Selenium.decorateFunctionWithTimeout(fnBind(this._verifyZText, this, txt), 30000);
	} else if (verifyParam.indexOf("verify_btn:=") >= 0) {
		return Selenium.decorateFunctionWithTimeout(fnBind(this._verifyZButton, this, txt), 30000);
	} else if (verifyParam.indexOf("verify_tab:=") >= 0) {
		return Selenium.decorateFunctionWithTimeout(fnBind(this._verifyZTab, this, txt), 30000);
	} else if (verifyParam.indexOf("verify_dlg:=") >= 0) {
		return Selenium.decorateFunctionWithTimeout(fnBind(this._verifyZDialog, this, txt), 30000);
	} else if (verifyParam.indexOf("verify_view:=") >= 0) {
		return Selenium.decorateFunctionWithTimeout(fnBind(this._verifyZview, this, txt), 30000);
	} else if (verifyParam.indexOf("verify_menu:=") >= 0) {
		return Selenium.decorateFunctionWithTimeout(fnBind(this._verifyZMenuWithMenuItem, this, txt), 30000);
	} else if (verifyParam.indexOf("verify_tbl:=") >= 0) {//table id
		return Selenium.decorateFunctionWithTimeout(fnBind(this._verifyZTable, this, txt), 30000);
	} else if (verifyParam.indexOf("verify_fldr:=") >= 0) {
		return Selenium.decorateFunctionWithTimeout(fnBind(this._verifyZFolder, this, txt), 30000);
	} else if (verifyParam.indexOf("verify_displayed:=") >= 0) {
		return Selenium.decorateFunctionWithTimeout(fnBind(this._verifyZDisplayed, this, txt), 30000);
	} else if (verifyParam.indexOf("verify_msgBdyInHyb:=") >= 0) {
		return Selenium.decorateFunctionWithTimeout(fnBind(this._verifyMsgBdyInHyb, this, txt), 30000);
	} else if (verifyParam.indexOf("verify_msgBdyInConv:=") >= 0) {
		return Selenium.decorateFunctionWithTimeout(fnBind(this._verifyZmsgBdyInConv, this, txt), 30000);
	}

}

Selenium.prototype.doClickZTab = function(locator, verifyParam) {
	/**
	 * Waits for Zimbra Tab
	 *
	 * @param locator tab label
	 * @param verifyParam verify_text verify_btn verify_tab verify_dlg verify_view verify_menu verify_tbl verify_fldr
	 */
	var element = this.findZTab(locator);
	this.clickZElement(element);

	if (verifyParam.indexOf(":=") >= 0)
		return this._addWaitDecorator(verifyParam);

};


Selenium.prototype.doCallFunctions = function(completeFunction) {
	/**
	 *Calls other javascript functions that are passed in string format
	 * e.g: "this.doClickZFolder(\"Inbox\",\"verify_fldr:=zti|Mail|2\")"
	 *
	 * @param completeFunction Pass the complete function
	 */
	 /*
	//open log window and set start logging
	if (LOG.getLogWindow() == null) {
		LOG.show();
		LOG.setLogLevelThreshold(3);
		this.doWaitForPopUp(LOG.getLogWindow(), 5000);
	}
	*/

	return eval( "("+  completeFunction + ")");


};

Selenium.prototype.getStrFunctions = function(completeFunction) {
	/**
	 *Calls other javascript functions that are passed in string format
	 * e.g: "this.doClickZFolder(\"Inbox\",\"verify_fldr:=zti|Mail|2\")"
	 *
	 * @param completeFunction Pass the complete function
	 */
	return eval( "("+  completeFunction + ")");


};



Selenium.prototype.doGoToAppln = function(appln, verifyParam) {
	/**
	 * Navigates to an application
	 *
	 * @param appln can be Calendar,Address Book,Tasks,Documents,Preferences,Mail
	 * @param verifyParam  can be verify_text verify_btn verify_tab verify_dlg verify_view verify_menu verify_tbl verify_fldr
	 */
	var func;
	var element = this.findZAppTab(appln);
	this.clickZElement(element);

	switch (appln) {
		case "Calendar":
			func = "this._isCalLoaded";
			break;
		case "Address Book":
			func = "this._isABLoaded";
			break;
		case "Tasks":
			func = "this._isTasksLoaded";
			break;
		case "Documents":
			func = "this._isDocLoaded";
			break;
		case "Preferences":
			func = "this._isPrefLoaded";
			break;
		case "Mail":
			func = "this._isMailLoaded";
			break;
	}

	return Selenium.decorateFunctionWithTimeout(fnBind(eval(func), this), 30000);

};
Selenium.prototype._isCalLoaded = function() {
	/**
	 * (private)Verifies if calendar is loaded
	 *
	 */
	return this.isCalLoaded();
};


Selenium.prototype._isABLoaded = function() {
	/**
	 * (private)Verifies if Address Book is loaded
	 *
	 */
	return this.isABLoaded();
};
Selenium.prototype._isTasksLoaded = function() {
	/**
	 * (private)Verifies if Tasks is loaded
	 *
	 */
	return this.isTasksLoaded();
};
Selenium.prototype._isDocLoaded = function() {
	/**
	 * (private)Verifies if Documents is loaded
	 *
	 */
	return this.isDocLoaded();
};
Selenium.prototype._isPrefLoaded = function() {
	/**
	 * (private)Verifies if Preferences is loaded
	 *
	 */
	return this.isPrefLoaded();
};
Selenium.prototype._isMailLoaded = function() {
	/**
	 * (private)Verifies if Mail is loaded
	 *
	 */
	return this.isMailLoaded();
};

Selenium.prototype._verifyViewHasNoChild = function() {
	/**
	 * (private)Verifies if an element has no child
	 *
	 */
	return this.verifyViewHasNoChild();
};

Selenium.prototype._closeDlgIfExists = function(dlgNameCommaBtnName) {
	/**
	 * (private)closes dialog
	 *  @param dlgNameCommaBtnName  "dialog name,button name"
	 */
	return this.closeDlgIfExists(dlgNameCommaBtnName);
};

Selenium.prototype._waitAndActOnElement = function(objType, locator, action, param1, param2) {
	/**
	 * (private)closes dialog
	 *  @param dlgNameCommaBtnName  "dialog name,button name"
	 */
			return this.waitAndActOnElement(objType, locator, action, param1, param2);
};

Selenium.prototype._verifyZDialog = function(text) {
	/**
	 * (private)Verifies if dialog exists
	 *  @param text dialog-name
	 */
	return this.verifyZDialog(text);
};

Selenium.prototype._verifyZview = function(viewName) {
	/**
	 * (private)Verifies if view exists
	 *  @param viewName view-name
	 */
	return this.verifyZview(viewName);
};
Selenium.prototype._verifyZMenuWithMenuItem = function(menuItemName) {
	/**
	 * (private)Verifies if menuitem exists
	 *  @param menuItemName menuItem-name
	 */
	return this.verifyZMenuWithMenuItem(menuItemName);
};

Selenium.prototype._verifyZTable = function(tableId) {
	/**
	 * (private)Verifies if table exists
	 *  @param tableId tableInternalId(partial or full)
	 */
	return this.verifyZTable(tableId);
};

Selenium.prototype._verifyZFolder = function(folderName) {
	/**
	 * (private)Verifies if folder exists
	 *  @param folderName folder-name
	 */
	return this.verifyZFolder(folderName);
};

Selenium.prototype._verifyZDisplayed = function(locatorWithZIndx) {
	/**
	 * (private)Verifies if the object(id) with zindex >=300 exists
	 *  @param locatorWithZIndx folder-name
	 */
	return this.verifyZDisplayed(locatorWithZIndx);
};


Selenium.prototype._verifyMsgBdyInHyb = function(text) {
	/**
	 * (private)Verifies if the message's body is displayed in hybrid-mode(in reading pane)
	 *  @param text message text
	 */
	return this.verify_msgBdyInHyb(text);
};


Selenium.prototype._verifyZmsgBdyInConv = function(text) {
	/**
	 * (private)Verifies if the message's body is displayed in Conversation View
	 *  @param text message text
	 */
	return this.verify_msgBdyInConv(text);
};


Selenium.prototype._verifyZButton = function(text) {
	/**
	 * (private)Verifies if btn exists
	 *  @param text btn-name
	 */
	if (this.findZButton(text) == null)
		return false;
	else
		return true;
};
Selenium.prototype._verifyZTab = function(text) {
	/**
	 * (private)Verifies if tab exists
	 *  @param text tab-name
	 */
	return this.verifyZTab(text);
};

Selenium.prototype._verifyZText = function(text) {
	/**
	 * (private)Verifies if text exists
	 *  @param text text string
	 */
	return this.verifyZText(text);
};


Selenium.prototype.doOpen = function(url) {
    /**
   * Opens an URL in the test frame. This accepts both relative and absolute
   * URLs.
   *
   * The &quot;open&quot; command waits for the page to load before proceeding,
   * ie. the &quot;AndWait&quot; suffix is implicit.
   *
   * <em>Note</em>: The URL must be on the same domain as the runner HTML
   * due to security restrictions in the browser (Same Origin Policy). If you
   * need to open an URL on another domain, use the Selenium Server to start a
   * new browser session on that domain.
   *
   * @param url the URL to open; may be relative or absolute
   */
	if (url == "http://zimbraAjax") {
		var loc = this.browserbot.getCurrentWindow().document.location;
		url = loc.protocol + "//" + loc.host;
	}

    this.browserbot.openLocation(url);
    if (window["proxyInjectionMode"] == null || !window["proxyInjectionMode"]) {
        return this.makePageLoadCondition();
    } // in PI mode, just return "OK"; the server will waitForLoad
};

Selenium.prototype.doSetupZVariables = function() {
	this.browserbot.rightClick = false;
	this.browserbot._browserName  = this.zGetBrowserName();//set browsername
	this.browserbot.totalDivs_CSV = "";
	this.browserbot.totalDivsCount = 0;
	this.browserbot.totalElements_CSV = "";
	this.browserbot.verifyingObjIds_CSV = "";
	this.browserbot.timetaken_CSV = "";
	this.browserbot._scannedDivsCount = 0;
	this.browserbot.scannedDivs_CSV = "";
	this.browserbot.headers_CSV = "";
	this.browserbot.headerName = "";
	this.browserbot.weight_CSV = "";
}

Selenium.prototype.zGetBrowserName = function()  {
	try{
		var win = this.browserbot.getCurrentWindow();
	} catch(e) {
		return "";
	}
	var agent = navigator.userAgent;
	var browserName = "";
	if (agent.indexOf("Firefox/") >= 0){
		browserName = "FF " + agent.split("Firefox/")[1];
		var tmp = browserName.split(" ");
		browserName = tmp[0]+ " "+ tmp[1];
		
	} else if (agent.indexOf("MSIE") >= 0) {
		var arry = agent.split(";");
		for (var t = 0; t < arry.length; t++) {
			if (arry[t].indexOf("MSIE") >= 0) {
				browserName = arry[t];
				break;
			}
		}
	} else if (agent.indexOf("Safari") >= 0) {
		var arry = agent.split("/");
		for (var t = 0; t < arry.length; t++) {
			if (arry[t].indexOf("Safari") >= 0) {
				browserName = arry[t];
				break;
			}
		}
	}
	if(agent.indexOf("Safari")>=0 && agent.indexOf("Chrome") >=0)
		browserName = "Chrome";
	

	return browserName;
}

Selenium.prototype.findZButton = function(locator, panel, objNumber) {
	if(locator.indexOf("=")>0) {	
		return (this.browserbot.findElementOrNull(locator));
	}
	var _counter = 0;
	var reqNumber = 1;
	if(objNumber != undefined && objNumber != "")
		var reqNumber = parseInt(objNumber);

	var divElements = this.getShellChildNodes();
	if(divElements == null)
		return null;

	if(panel == undefined || panel == "") {
		var check1 = "((parseInt(testElement.style.zIndex) == 300" + 
				 "|| (parseInt(testElement.style.zIndex) == 100 && testElement.style.display == \"block\"))"+		
				"&& (testElement.innerHTML.indexOf(locator) >= 0))";
	} else if(panel == "dialog"){
		var check1 =  "(parseInt(testElement.style.zIndex) >= 700 " +
			" && (testElement.className.indexOf(\"Dialog\") >= 0) "+
			" && (testElement.innerHTML.indexOf(locator) >= 0))";
	} else if(panel.indexOf("__dialogByName__")>=0){
				var dlgName = panel.replace("__dialogByName__", ""); 
		var check1 =  "(parseInt(testElement.style.zIndex) >= 700" + 
		" && (testElement.className.indexOf(\"Dialog\") >= 0)" + 
		" && (testElement.innerHTML.indexOf(\""+dlgName+"\") >= 0)" +
		" && (testElement.innerHTML.indexOf(locator) >= 0))";
	} else {
		return null;
	}

	for (var i = 0; i < divElements.length; i++) {
		var testElement = divElements[i];
		var testElementHtml = testElement.innerHTML;
		if (eval("(" + check1 + ")")) {
			var div1 = testElement.getElementsByTagName("DIV");
			for (var j = 0; j < div1.length; j++) {
				var testElement = div1[j];
				if (((testElement.className.indexOf("ZToolbarButton") >= 0)
						|| (testElement.className.indexOf("DwtToolbarButton") >= 0)
						|| (testElement.className.indexOf("ZButton ") >= 0) 
					|| ((testElement.className.indexOf("ZSelectAutoSizingContainer") >= 0) && (testElement.className.indexOf("ZHasDropDown") >= 0))
				
					) && (testElement.innerHTML.indexOf(locator) >= 0)) {
						if(locator.indexOf("Img") >=0) {
							_counter++;
							if(reqNumber == _counter)
								return testElement;
						} else {
							if (testElement.textContent)
								var actTxt = testElement.textContent;
							else if (testElement.innerText)
								var actTxt = testElement.innerText;
							//do perfect match or if its menu, do starts-with
							if(actTxt == locator || ((actTxt.indexOf(locator) ==0) &&(testElement.className.indexOf("ZSelectAutoSizingContainer") >= 0))) {
								_counter++;
								if(reqNumber == _counter)
									return testElement;
							}
						}
				} 
			}

		}
	}
	return null;
};

Selenium.prototype.verifyZButton = function(locator) {
}

Selenium.prototype.buttonCore = function(locator, action, panel, objNumber, param2) {
	var element = this.findZButton(locator, panel, objNumber);
	if(element == null && action == "notexist")
		return true;
	else if(element == null)
		return false;
	else 
		return	this.actOnZElement(element, action, locator, "", param2); //for click, rtclick, shiftclick etc
		
}

Selenium.prototype.buttonCore_html = function(locator, action, panel, objNumber, param2) {
	var element = this.findZButton_html(locator, panel, objNumber);
	if(element == null && action == "notexist")
		return true;
	else if(element == null)
		return false;
	else 
		return	this.actOnZElement(element, action, locator, "", param2); //for click, rtclick, shiftclick etc
}

Selenium.prototype._locateMenuArrowOfIdObj = function(testElement) {
	//we are using id to locate the button, walk up until we find pulldownArrow class, then drill down
	testElement = testElement.parentNode;
	var divElement = null;
	do {
		if(testElement.innerHTML.indexOf("ImgSelectPullDownArrow")>=0 && testElement.tagName.toLowerCase().indexOf("div")>=0) {
			divElement = testElement;
			break;	
	}
	} while (testElement = testElement.parentNode);

	return divElement;

}

Selenium.prototype.getMenuDownArrowOfZObj = function(testElement, locator) {

	if(locator.indexOf("=")>=0) {
	var testElement = this._locateMenuArrowOfIdObj(testElement);
	if(testElement == null)
		return null;
	}
	var div1 = testElement.getElementsByTagName("DIV");
	for (var j = 0; j < div1.length; j++) {
		var testElement = div1[j];
		if(testElement.className.indexOf("ImgSelectPullDownArrow")>=0) {
			return testElement;
		}
	}
	return null;
}

Selenium.prototype.buttonMenuCore = function(locator, action, panel, param1) {
	var element;
	var testElement = this.findZButton(locator);
	if(testElement == null) 
		return false;
	var arrowElement = this.getMenuDownArrowOfZObj(testElement, locator);

	if(arrowElement == null && action == "notexist")
		return true;
	else if(arrowElement == null)
		return false;
	else 
		return	this.actOnZElement(arrowElement, action, locator, "true"); //for click, rtclick, shiftclick etc
}

Selenium.prototype.formalizeHTMLTag = function(tag) {
	//returns browser specific tag(either lowecase/uppercase)
	if(this.browserbot._browserName.indexOf("IE") >= 0) //if Internet Explorer, make all the tags lowecase
		return tag.toUpperCase();
	else
		return tag.toLowerCase();
}

Selenium.prototype.findZFormObject = function(objName, objTag, objType, panel, objNumber ) {
	if(objName.indexOf("=")>0 && objName.indexOf("*")==-1) {	
		var html_objNumber = 1;
		if(panel != undefined && panel != ""){//panel has objNumber(yuck)
			html_objNumber = parseInt(panel);
		}

		if(html_objNumber >1){//if we need to find obj by number but its using id= or name=
			return this.findZFormObjectsMultipleElements_html(objName, objTag, objType, html_objNumber);
		}else {
			return (this.browserbot.findElementOrNull(objName));
		}
	}
	var win = this.browserbot.getCurrentWindow();
	var inDocument = win.document;
	if(objName.indexOf("=")>0 && objName.indexOf("*") >0) {	
		objName = objName.replace("*", "").replace("id=","");
		var objs = inDocument.getElementsByTagName(objTag);
		for(var i =0; i< objs.length; i++) {
			var tmpObj = objs[i];
			if((tmpObj.id).indexOf(objName) > 0 && tmpObj.type == objType) {
				return tmpObj;
			}
		}
		return null;
	}

	var startsWith = false;
	var ignoreInnerRow = false;
	if(objName.indexOf("::labelStartsWith") > 0) {
		objName = objName.replace("::labelStartsWith","");
		startsWith	= true;
	} else if(objName.indexOf("::fieldLabelIsAnObject") > 0) {
		objName = objName.replace("::fieldLabelIsAnObject", "");
		ignoreInnerRow = true;
	}
		
	var newBtn = true;
	var rowFound = false;
	var typeFlg = true;
	var mainDiv = "";
	var innerTxt = "";
	if(browserVersion.isIE)
		innerTxt = "innerText";
	else
		innerTxt = "textContent";

	var rowsWithObj = new Array();
	if(!objNumber) {
		objNumber = 1;
	}
	var form =  null;
	if(this.isHtmlClient()){
		form = this.getForm_html();
	}

	if(!form){
		var loginpage =false;
	    // Loop through all elements, looking for ones that have 
	// a value === our expected value
	//var divElements = inDocument.getElementsByTagName("DIV");
	if(inDocument.getElementById("z_shell") != undefined) { //zimbraajax
		var divElements = inDocument.getElementById("z_shell").childNodes;
	} else if(inDocument.getElementById("ZloginPanel") != undefined) {//login page
				 mainDiv = inDocument.getElementById("ZloginPanel");
				loginpage = true;
	} else if(inDocument.getElementById("DWT1") != undefined) {//compose new window
		var newwindowelement = inDocument.getElementById("DWT1");
		if(newwindowelement.className == "MainShell")
			var divElements =  newwindowelement.childNodes;
		else
			return null;
	} else {
		return null;
	}

	if(!loginpage) {
		if(panel == undefined || panel == "") {
			var check1 = "(parseInt(testElement.style.zIndex) == 300 "
			+ " && (testElement.innerHTML.indexOf(\"skin_outer\") == -1) "
			+ " &&(testElement."+innerTxt+".indexOf(objName) >= 0))";
		} else if(panel == "dialog"){
			var check1 =  "(parseInt(testElement.style.zIndex) >= 700 " +
				" && (testElement.className.indexOf(\"Dialog\") >= 0) "+
				" && (testElement."+innerTxt+".indexOf(objName) >= 0))";
		} else if(panel.indexOf("__dialogByName__")>=0){
						var dlgName = panel.replace("__dialogByName__", ""); 
			var check1 =  "(parseInt(testElement.style.zIndex) >= 700" + 
				" && (testElement.className.indexOf(\"Dialog\") >= 0)" + 
				" && (testElement."+innerTxt+".indexOf(\""+dlgName+"\") >= 0)" +
				" && (testElement."+innerTxt+".indexOf(objName) >= 0))";
		} else {
			return null;
		}
			for (var i = 0; i < divElements.length; i++) {
				var testElement = divElements[i];
				if (eval("(" +check1+ ")")) {
					mainDiv = testElement;
					break;
				}
			}
		}
	}

	if(!objTag) 
		objTag = this.formalizeHTMLTag("input");
	else
		objTag = this.formalizeHTMLTag(objTag);


	if(form != null)
		mainDiv = form;

	if(mainDiv == "") {
		return null;
	}
	// Loop through all elements, looking for ones that have 
	// a value === our expected value
	var formalizedTr = this.formalizeHTMLTag("tr");
	var rowEls = mainDiv.getElementsByTagName(formalizedTr);
	for (var i = 0; i < rowEls.length; i++) {
		var actTxt = "";
		var rowObj = rowEls[i];
		var inhtml = (rowObj.innerHTML);
		if (rowObj.textContent)
			 actTxt = rowObj.textContent;
		else if (rowObj.innerText)
			 actTxt = rowObj.innerText;

		var objNameIndx = actTxt.indexOf(objName);
		var innerRowsLen = rowObj.getElementsByTagName(formalizedTr).length
		var innerRowIndx = inhtml.indexOf(formalizedTr);
		var fldrHeaderIndx = inhtml.indexOf("overviewHeader");
		var tagIndx = inhtml.indexOf(objTag);
		//if startsWith is required, makesure the first-letter of the row matches the locator
		if(startsWith)
			 var objNameBool =  (objNameIndx == 0);
		else
			 var objNameBool =  (objNameIndx >= 0);
		if (objNameBool && (tagIndx >= 0) && (fldrHeaderIndx== -1) && (innerRowIndx == -1 || innerRowsLen == 0 || innerRowIndx > tagIndx || (ignoreInnerRow && innerRowIndx > objNameIndx))) {
			if((objType != "text") && (objType != "textarea")) //if its not edit, makesure we have a radio/checkbox
					var typeFlg = (inhtml.indexOf(objType) >= 0);
					
				if(typeFlg) {
					rowsWithObj.push(rowObj);
					rowFound = true;
				}
		}
	}

	//if nothing was found.. see if there is a row with obj AND internal-row..
	if(!rowFound) {
		for (var j = 0; j < rowEls.length; j++) {
			var rowObj = rowEls[j];
			var inhtml = rowObj.innerHTML;
			if (rowObj.textContent)
				var actTxt = rowObj.textContent;
			else if (rowObj.innerText)
				var actTxt = rowObj.innerText;

			if ((actTxt.indexOf(objName) >= 0) && (inhtml.indexOf(objTag) >= 0)){
				if((objType != "text") && (objType != "textarea")) //if its not edit, makesure we have a radio/checkbox
					var typeFlg = (inhtml.indexOf(objType) >= 0);
					
				if(typeFlg) {
					rowsWithObj.push(rowObj);
					rowFound = true;
				}
			}
		}

	}
	//use the last-row as the correct/required row(if object# is not specified)
	//if(objNumber <= rowsWithObj.length)//assumption: one row has only one object
	//	var rowObj  = rowsWithObj[objNumber-1];
	//else
	//	var rowObj  = rowsWithObj[rowsWithObj.length-1];
	var objCounter = 0;
	var prevRowObjCounter = 0;
	if(rowFound) {
		for(var i=0; i< rowsWithObj.length; i++) {			
			var rowObj  = rowsWithObj[i];
			var formObjs = this.getAllFormObjs([rowObj], objTag, objType);
			objCounter = objCounter + formObjs.length;
			if (objNumber == objCounter) {
				return formObjs[objCounter-prevRowObjCounter-1];
			} else if (objNumber < objCounter) {
				return	 this._getFormObjsInRow_MultipleObjs(formObjs, rowObj, objName, objTag, objType, objNumber-prevRowObjCounter);
			} 
			prevRowObjCounter = prevRowObjCounter +formObjs.length;
		}
	}
	return null;
};

Selenium.prototype.formObjCore = function(objName, objTag, objType, action, data, panel, objNumber) {
	var element = this.findZFormObject(objName, objTag, objType, panel, objNumber);
	
	if(element == null && action == "notexist")
		return true;
	else if(element == null)
		return false;
	
	if(action == "type") {
		 this.browserbot.replaceText(element, data);
		 return true;
	}  else if(action == "checked"){ 
		return element.checked;
	} else if(action == "gettext") {
		var val = element.value;
		if(element.value == "")
				val = "<blank>";
			return val;
	}else
		return	this.actOnZElement(element, action, objName); 
}

Selenium.prototype.textAreaCore = function(objName, action, data, panel, objNumber) {
		return this.formObjCore(objName, "textarea", "textarea", action, data, panel, objNumber);
}
Selenium.prototype.radioBtnCore = function(objName, action, data, panel, objNumber) {
		return this.formObjCore(objName, "input", "radio", action, data, panel, objNumber);
}
Selenium.prototype.checkBoxCore = function(objName, action, data, panel, objNumber) {
	return this.formObjCore(objName, "input", "checkbox", action, data, panel, objNumber);
}
Selenium.prototype.editFieldCore = function(objName, action, data, panel, objNumber) {
	return this.formObjCore(objName, "input", "text", action, data, panel, objNumber);
}
Selenium.prototype.browseFileFieldCore = function(objName, action, data, panel, objNumber) {
	return this.formObjCore(objName, "input", "file", action, data, panel, objNumber);
}
Selenium.prototype.pwdFieldCore = function(objName, action, data, panel, objNumber) {
	return this.formObjCore(objName, "input", "password", action, data, panel, objNumber);
}
Selenium.prototype.calenadarCheckBoxCore = function(objName, action, data, panel, objNumber) {
	return this.formObjCore(objName, "input", "checkbox", action, data, panel, objNumber);
}


Selenium.prototype.getAllFormObjs = function(rowsWithObj, objTag, objType) {
	var arry = new Array();
	objTag = this.formalizeHTMLTag(objTag);
	for(var k = 0; k < rowsWithObj.length; k++) {
		 var tmp = rowsWithObj[k].getElementsByTagName(objTag);
		 for( var i=0; i < tmp.length; i++) {
			 var formObj = tmp[i];
			 if(!formObj.type) {
				 if((objType == "text") || (objType == "textarea"))
						 arry.push(tmp[i]);
			 } else if(formObj.type == objType) {
				 arry.push(tmp[i]);
			 }
		 }
	}
	return arry;
}

Selenium.prototype._getFormObjsInRow_MultipleObjs = function(formObjs, rowObj, objName, objTag, objType, objNumber) {
	var allNodes = rowObj.getElementsByTagName("*");
	var ParsePattern = "";
	var previousTxt = "";
	var objCnt = 0;
	var txtCount = 0;
	var preObjCnt = 0;
	var foundcount = 0;
	objTag = objTag.toLowerCase();
	for (var j = 0; j < allNodes.length; j++) {
		var someEl = allNodes[j];
		var nval = "";
		//get node name
		var nname = someEl.nodeName.toLowerCase();
		//get node text
			if (someEl.textContent)
				 nval = someEl.textContent;
			else if (someEl.innerText)
				 nval = someEl.innerText;

		if (nname == objTag) {
			ParsePattern = ParsePattern + "O";
			objCnt = objCnt + 1;
			previousTxt = "O";
		} else if (nval.indexOf(objName) == 0 || nval.indexOf(objName) == 1) {
			if (previousTxt != "F") {
				ParsePattern = ParsePattern + "F";
				foundcount = foundcount + 1;
			}
			preObjCnt = objCnt;
			previousTxt = "F";
			txtCount = txtCount + 1;
		} else if (nval.length > 1) {
			if (previousTxt != "T") {
				ParsePattern = ParsePattern + "T";
			}
			previousTxt = "T";
			txtCount = txtCount + 1;
		}

		if ((txtCount == objCnt) && (ParsePattern.indexOf("F") >= 0) && (ParsePattern.indexOf("O") >= 0))
			break;

	}
	if (foundcount > 1 && foundcount == objCnt)
		return formObjs[objCnt - 1];
	else if ((ParsePattern.indexOf("O") == 0) && (ParsePattern.indexOf("TOOF") >= 0) && (ParsePattern.indexOf("F") >= 0))
		return formObjs[preObjCnt - 1];
	else if (objCnt >= (preObjCnt + objNumber - 1) && (ParsePattern.indexOf("F") >= 0))
		return formObjs[preObjCnt + objNumber - 1];
	else if (objCnt >= (preObjCnt + objNumber - 1))
		return formObjs[objNumber];
	else
		return null;
}

Selenium.prototype.editorCore_html = function(locator, action, data, panel, objNumber) {
	var element = this.findZIframeOrTextArea_html(locator, panel, objNumber);

	if(element == null && action == "notexist")
		return true;
	else if(element == null)
		return false;

	if(action == "type") {
		 this.browserbot.replaceText(element, data);
		 return true;
	}  else
		return	this.actOnZElement(element, action, locator); 
}

Selenium.prototype.editorCore = function(locator, action, data, panel, objNumber) {
	var element = this.findZEditor(locator, panel, objNumber);
	if(element == null && action == "notexist")
		return true;
	else if(element == null)
		return false;
	
	if(action == "type") {
		 this.browserbot.replaceText(element, data);
		 return true;
	}  else
		return	this.actOnZElement(element, action, locator); 
}

Selenium.prototype.getFormAnchors_html = function() {
	var form = this.getForm_html();
	if(form == null)
		return null;

	return form.getElementsByTagName("a");
}

//////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////
Selenium.prototype.actOnZElement = function(element, action, locator, useXY,param2) {
	var x = 10;
	var y = 10;
	var tagName = element.tagName.toLowerCase();
	var isZObjHTMLObj = false;
	var isZObjButWithid = false;
	if(useXY == "true"){
		var tmp = this.getAnyBrowserCoordinates(element).split(",");
		x = parseInt(tmp[0]);
		y= parseInt(tmp[1]);
	}

	if(locator != undefined) {
		if("td div".indexOf(tagName) == -1){
			isZObjHTMLObj = true;
		}else{//zimbra obj but has id=DWT123 locator
			isZObjButWithid = true;
		}
	}
	if(action.indexOf("_addXY")>=0) {
		action = action.replace("_addXY", "");
		var tmp =param2.split(",");
		x= x+ parseInt(tmp[0]);
		y= y+ parseInt(tmp[1]);
	}
	if((action ==  "click") || (action ==  "clickAndGetPerf")) {
		
		if(isZObjHTMLObj) {//for html buttons, ignore td and div
			this.browserbot.clickElement(element);
			return true;
		}		
		this.browserbot._fireEventOnElement("mousedown", element, x, y);
		this.browserbot._fireEventOnElement("mouseup", element, x, y);
	}else if(action ==  "rtclick") {
		var xy = this.getCoordinates(element);
		x = xy[0];
		y = xy[1];
        custom_fireEvent = function(eventType, element, clientX, clientY, button){
            var win = selenium.browserbot.getCurrentWindow();
            triggerEvent(element, 'focus', false);

            // Add an event listener that detects if the default action has been prevented.
            // (This is caused by a javascript onclick handler returning false)
            // we capture the whole event, rather than the getPreventDefault() state at the time,
            // because we need to let the entire event bubbling and capturing to go through
            // before making a decision on whether we should force the href
            var savedEvent = null;

            element.addEventListener(eventType, function(evt) {
                savedEvent = evt;
            }, false);

            selenium.browserbot._modifyElementTarget(element);

            // Trigger the event.
            selenium.browserbot.triggerMouseEvent(element, eventType, true, clientX, clientY, button);

            if (selenium.browserbot._windowClosed(win)) {
                return;
            }

            // Perform the link action if preventDefault was set.
            // In chrome URL, the link action is already executed by triggerMouseEvent.
            if (!browserVersion.isChrome && savedEvent != null && !savedEvent.getPreventDefault()) {
                var targetWindow = selenium.browserbot._getTargetWindow(element);
                if (element.href) {
                    targetWindow.location.href = element.href;
                } else {
                    selenium.browserbot._handleClickingImagesInsideLinks(targetWindow, element);
                }
            }
        }
		custom_fireEvent("mousedown", element, x, y, 2);
		custom_fireEvent("mouseup", element, x, y, 2);
	} else if(action == "dblclick") {
		selenium.browserbot._fireEventOnElement("dblclick", element, x, y);
		this.browserbot._fireEventOnElement("mousedown", element, x, y);
		this.browserbot._fireEventOnElement("dblclick", element, x, y);
		this.browserbot._fireEventOnElement("mouseup", element, x, y);
	} else if(action ==  "mouseover") {
		this.browserbot._fireEventOnElement("mouseover", element, x, y);
	} else if (action == "shiftclick"){
		this.browserbot.shiftKeyDown = true;
		this.browserbot._fireEventOnElement("mousedown", element, x, y);
		this.browserbot._fireEventOnElement("mouseup", element, x, y);
		this.browserbot.shiftKeyDown = false;
	} else if (action == "ctrlclick"){
		this.browserbot.controlKeyDown = true;
		this.browserbot._fireEventOnElement("mousedown", element, x, y);
		this.browserbot._fireEventOnElement("mouseup", element, x, y);
		this.browserbot.controlKeyDown = false;
	} else if (action == "exists") {
		if(!isZObjHTMLObj && !isZObjButWithid)
			return true;
		else{
			return this.isZObjVisible(element);
		}
	} else if (action == "notexist") {
		if(!isZObjHTMLObj && !isZObjButWithid)
			return false;
		else//if we are dealing with zimbraObj, then verify zindex etc b4 saying anything
			return !this.isZObjVisible(element);

	} else if(action == "wait") {
		return true;
	} else if(action == "enabled") {
		if(element.className.indexOf("ZDisabled") > 0 || element.innerHTML.indexOf("ZDisabled") > 0)
			return false;
		else 
			return true;
	} else if(action == "disabled") {
		if(element.className.indexOf("ZDisabled") > 0 || element.innerHTML.indexOf("ZDisabled") > 0)
			return true;
		else
			return false;
	} else if(action == "gettext") {			
		if (element.textContent)
			return element.textContent;
		else if (element.innerText)
			return element.innerText;
		else if (element.value)
			return element.value;
	}  else if(action == "gethtml") {
		return element.innerHTML;
	}else if(action == "getcoord") {
		return this.getAnyBrowserCoordinates(element);
	}
	return true;
};

Selenium.prototype.getAnyBrowserCoordinates = function(element){
	var win = this.browserbot.getCurrentWindow();
	if(browserVersion.isIE) {
		var tmp =this.getIECoordinates().split(",");
		var iex = parseInt(tmp[0]);
		var iey = parseInt(tmp[1]);
		var box = element.getBoundingClientRect();
		var boxx = box.left;
		var boxy = box.top;
		return (boxx+ iex)+ "," + (boxy + iey);
	} else {
		var xy = this.getCoordinates(element);
		x = xy[0];
		y = xy[1];
		if(this.browserbot._browserName == "Chrome")//google chrome's y axis is about 120 less(especially in popup-mode)
			y= y+85;

		//10 is manually added by getcoordinates function for other actions(remove that)
		return (x-10) +"," + (y - 10 + win.outerHeight - win.innerHeight);
	}
}

Selenium.prototype.getIECoordinates = function(){

	var win = this.browserbot.getCurrentWindow();
	var inDocument = win.document;
  var w, h, offW, offH, diffW, diffH;
  var fixedW = 800;
  var fixedH = 600;
  var ieDiffWidth = 0;
  var ieDiffHeight =0;

  if (inDocument.all) {
    offW = inDocument.body.offsetWidth;
    offH = inDocument.body.offsetHeight;
    win.resizeTo(fixedW, fixedH);
    diffW = inDocument.body.offsetWidth  - offW;
    diffH = inDocument.body.offsetHeight - offH;
    w = fixedW - diffW;
    h = fixedH - diffH;
    ieDiffWidth  = w - offW;
    ieDiffHeight = h - offH;
    win.resizeTo(w, h);
  }
  return ieDiffWidth + "," + ieDiffHeight;
}
Selenium.prototype.getCoordinates = function(obj) {
	var curleft = curtop = 10;
	if (obj.offsetParent) {
		do {
			curleft += obj.offsetLeft;
			curtop += obj.offsetTop;
		} while (obj = obj.offsetParent);
	}
	return [curleft,curtop];
}

Selenium.prototype.isZObjVisible = function(obj) {
	if(this.browserbot.baseUrl.indexOf("/h/") >=0)//html client
		return true;

	do {
		try{
			if((parseInt(obj.style.zIndex) >= 300) || (parseInt(obj.style.zIndex) == 100 && obj.style.display == "block"))
				return true;
		} catch(e) {}
	} while (obj = obj.parentNode);
	return false;
}
Selenium.prototype.clickZElement = function(element) {
	this.actOnZElement(element, "click");
};
Selenium.prototype.doubleclickZElement = function(element) {
	this.actOnZElement(element, "dblclick");
};
Selenium.prototype.rightClickZElement = function(element) {
	this.actOnZElement(element, "rtclick");
};

Selenium.prototype.getShellChildNodes = function() {
		var win = this.browserbot.getCurrentWindow();
	var inDocument = win.document;
	if(inDocument.getElementById("z_shell") != undefined) //zimbraajax
		return inDocument.getElementById("z_shell").childNodes;
	else if(inDocument.getElementById("DWT1") != undefined){//compose new window
		var newwindowelement = inDocument.getElementById("DWT1");
		if(newwindowelement.className == "MainShell")
			return newwindowelement.childNodes;
		else
			return null;
	} else 
		return null;

}

Selenium.prototype.findZTabs_html = function(locator, panel, objNumber) {
	var _counter = 0;
	var reqNumber = 1;
	var tab = null;
	if(objNumber != undefined && objNumber != "")
		 reqNumber = parseInt(objNumber);

	var win = this.browserbot.getCurrentWindow();
	var inDocument = win.document;
	var tds =  inDocument.getElementsByTagName("td");	
	for(var i=0; i< tds.length; i++) {
		var td =  tds[i];
		if((td.className == "Tab TabNormal" || td.className == "Tab TabSelected") && td.innerHTML.indexOf(locator)>=0){
				_counter++;
				if(_counter == reqNumber) {
					tab = td;
					break;
				}
					
		}
	}
	
	if(tab == null)
		return null;

	var anch = tab.getElementsByTagName("a");
	if (anch.length == 0)
		return null;

	return anch[0];


}

Selenium.prototype.getButtons_html = function() {
	var form = this.getForm_html();
	var arry = new Array();
	if(form == null)
		return null;

	var tables =  form.getElementsByTagName("table");
	for(var i=0; i< tables.length; i++) {
		var tbl =  tables[i];
		if(tbl.parentNode.className == "TbTop" ||tbl.parentNode.className == "TbBottom" || tbl.className == "ZOptionsSectionMain"){
				arry.push(tbl);
		}
	}
	
	var buttons = new Array();
	var inputBtns = new Array();
	for(var j=0;j < arry.length; j++) {
		var toolbar = arry[j];

		var objs = toolbar.getElementsByTagName('a');
		for(var k=0; k< objs.length; k++) {
				buttons.push(objs[k]);
		}
		var inputs =  toolbar.getElementsByTagName('input');
		for(var k=0; k< inputs.length; k++) {
			if(inputs[k].type== "submit")
				buttons.push(inputs[k]);
		}

	}
	return buttons;


}
Selenium.prototype.getForm_html = function() {
	var win = this.browserbot.getCurrentWindow();
	var inDocument = win.document;
	var frms =  inDocument.getElementsByTagName("form");
	for(var i=0; i< frms.length; i++) {
		var htm = frms[i].innerHTML;
		if(htm.indexOf("class=\"TbTop\"") > 0  || htm.indexOf("name=\"zform\"") > 0 || htm.indexOf("class=TbTop") > 0 || htm.indexOf("name=zform") > 0){
			return frms[i];
		}
	}
	return null;

}

Selenium.prototype.getFormClass_html = function(reqClass, objNumber) {
	var form = this.getForm_html();
	if(form == null)
		return null;

	var _counter = 0;
	var reqNumber = 1;
	if(objNumber != undefined && objNumber != "")
		var reqNumber = parseInt(objNumber);

	var itms = form.getElementsByTagName("*");
	for(var i=0; i< itms.length; i++) {
		if(itms[i].className.indexOf(reqClass) >=0){
			_counter++;
			if(_counter == reqNumber)
				return itms[i];
		}
	}
	return null;

}


Selenium.prototype.findZCalView = function() {

	var divElements = this.getShellChildNodes();
	if(divElements == null)
		return null;

	var check1 = "((parseInt(testElement.style.zIndex) == 300" + 
			"|| (parseInt(testElement.style.zIndex) == 100 && testElement.style.display == \"block\"))"+		
			"&& (testElement.className.indexOf(\"ZmCalViewMgr\") >= 0))";

	for (var i = 0; i < divElements.length; i++) {
		var testElement = divElements[i];
		var testElementHtml = testElement.innerHTML;
		if (eval("(" + check1 + ")")) {
			var div1 = testElement.getElementsByTagName("DIV");
			for (var j = 0; j < div1.length; j++) {
				var testElement = div1[j];
			
				if ((testElement.className.indexOf("calendar_view") >= 0) && (testElement.style.left == ("0px")) 
						&&(testElement.style.top == ("0px"))) {
							return testElement;
				} 
			}
		}
	}
	return null;
}



Selenium.prototype.findZAppt_html = function(locator, panel, objNumber) {
		if(locator.indexOf("=")>0) {	
		return (this.browserbot.findElementOrNull(locator));
	}
	var _counter = 0;
	var reqNumber = 1;
	if(objNumber != undefined && objNumber != "")
		var reqNumber = parseInt(objNumber);

	var win = this.browserbot.getCurrentWindow();
	var inDocument = win.document;
	var tbls =  inDocument.getElementsByTagName("table");
	for(var i=0; i< tbls.length; i++) {
		var tbl = tbls[i];
		if((tbl.className == "ZhCalDayAppt" ||tbl.className.indexOf("ZhCalDayAllDayAppt") >=0
			|| tbl.className.indexOf("ZhCalMonthAllDayAppt") >=0 || tbl.className == "ZhCalDayApptNew") && tbl.innerHTML.indexOf(locator) >=0){
			_counter++;
			if(reqNumber == _counter)
				return tbl;
		}
	}
	_counter = 0;
	var divs =  inDocument.getElementsByTagName("div");
	for(var i=0; i< divs.length; i++) {
		var div = divs[i];
		if(div.className.indexOf("ZhCalDayAppt") >=0 && div.innerHTML.indexOf(locator) >=0){
			_counter++;
			if(reqNumber == _counter)
				return div;
		}
	}
	
	return null;
}

Selenium.prototype.findZcalGrid_html = function(locator, action, panel, objNumber) {
	if(locator.indexOf("=")>0) {	
		return (this.browserbot.findElementOrNull(locator));
	}
	var _counter = 0;
	var reqNumber = 1;
	if(objNumber != undefined && objNumber != "")
		var reqNumber = parseInt(objNumber);


	var win = this.browserbot.getCurrentWindow();
	var inDocument = win.document;
	var grid = null;
	var tbls =  inDocument.getElementsByTagName("table");
	for(var i=0; i< tbls.length; i++) {
		var tbl = tbls[i];
		if(tbl.className == "ZhCalDayGrid" || tbl.className == "ZhCalMonthTable"){
			grid = tbl;
			break;
		}
	}
	if(grid == null)
			return null;

	//get allRows and HdrCells
	this._getAllRowsAndHdrCells(grid);
	if(this._hdrRowCells == null || this._allRows == null)
		return null;
	
	var isMonthView = false;
	if(grid.className == "ZhCalMonthTable")
		isMonthView = true;
		
	var apptRows = new Array();
	for(var i=0; i<  this._allRows.length; i++) {
		var tr =  this._allRows[i];
		if(tr.nodeName == "#text")
			continue;
		try{
			var ht = tr.innerHTML;
			if((ht.indexOf("ZhCalDayHour")>=0 || ht.indexOf("ZhCalAllDayDS")>=0 || ht.indexOf("ZhCalMonthAppt")>=0 ||  ht.indexOf("ZhCalMonthAllDayAppt")>=0 )  && ht.indexOf(locator)>=0){
				apptRows.push(tr);				
			}
		}catch(e) {}
	}

	if(apptRows.length ==0)
		return null;
	var result = "";
	var cellNum = 0;
	for(var i=0; i< apptRows.length; i++) {//go through all rows with locator
		var tr = apptRows[i];
		var tds = tr.childNodes;
		var tme = "N/A";
		for(var j=0; j< tds.length; j++) {//go through all cells
			var cell =  tds[j];
			if(cell.nodeName == "#text"){
				continue;
			}

			if(cell.className.indexOf("ZhCalDayHour")>=0){//get time
				if (cell.textContent){
					tme = cell.textContent;
				}else if(cell.innerText){
					tme = cell.innerText;
				}
			}

			var ht = cell.innerHTML;
			if(ht == undefined){
				continue;
			}
			var dateInMonthCell = "";
			if(ht.indexOf(locator) >=0) {
				
				if(isMonthView){//get the date
					var tdMs = cell.getElementsByTagName("td");
					for(var n=0;n<tdMs.length;n++){
						if(tdMs[n].className.indexOf("ZhCalDOM") >=0){
							if (tdMs[n].textContent){
								dateInMonthCell = tdMs[n].textContent;
							}else if(tdMs[n].innerText){
								dateInMonthCell = tdMs[n].innerText;
							}
						}
					}
				}
				
				var hour = cell.rowSpan/4;//get the hour
				if(hour <0.5)//ignore all those < half an hour.
					hour = "N/A";

				var cNum  = 0;
				if(isMonthView){
					cNum = (j-1)/2;
				} else{
					cNum = j;
				}
				var hdr = this._hdrRowCells[cNum];
				if (hdr.textContent){
					result = result+ hour+ "_"+ tme + "_"+ hdr.textContent+" "+dateInMonthCell+";";
				}	else if (hdr.innerText){
					result = result +  hour+ "_"+ tme + "_"+ hdr.innerText+" "+dateInMonthCell+";";
				}
			}
		}

	}

	this._hdrRowCells  = null;
	this._allRows = null;
	if(result == "")
		return null;

	result =  result.replace(/\r|\n|\r\n/g, "");	
	if(action =="getCount")
		return result.split(";").length-1;

	if(action == "getDT")
		return result;
}

Selenium.prototype._getAllRowsAndHdrCells = function(grid) {
	this._hdrRowCells  = null;
	this._allRows = null;
	var tempArry = new Array();
	if(grid.className == "ZhCalDayGrid"){
		var trs =  grid.getElementsByTagName("tr");
		for(var i=0; i< trs.length; i++) {
			var tr = trs[i];
			if(tr.className == "ZhCalMonthHeaderRow"){
				this._allRows = tr.parentNode.childNodes;
				this._hdrRowCells = tr.childNodes;
				break;
			}
		}
	} else if(grid.className == "ZhCalMonthTable") {
		var parent = grid.parentNode.childNodes;
		var hdr = null;
		for(var i=0;i<parent.length;i++){
			if(parent[i].className == "ZhCalMonthHeaderTable"){
					hdr = parent[i];
					break;
			}
		}
		var tds =hdr.getElementsByTagName("td");
		var cnt=0;
		for(var i=0; i< tds.length; i++) {
			var td = tds[i];
			if(td.className == "ZhCalMonthHeaderCellsText"){
				tempArry[cnt] = td;
				cnt++;
			}
		}
		this._hdrRowCells  = tempArry;
		this._allRows= grid.getElementsByTagName("tbody")[0].childNodes;
	}

}

Selenium.prototype.findZAppt = function(locator, panel, objNumber) {
		if(locator.indexOf("=")>0) {	
		return (this.browserbot.findElementOrNull(locator));
	}
	var _counter = 0;
	var reqNumber = 1;
	if(objNumber != undefined && objNumber != "")
		var reqNumber = parseInt(objNumber);

	var calView = this.findZCalView();
	if(calView == null)
		return null;

	var div1 = calView.getElementsByTagName("DIV");
	for (var j = 0; j < div1.length; j++) {
			var testElement = div1[j];
			if (((testElement.className.indexOf("appt-selected") >= 0)||	(testElement.className == ("appt"))) 
					&& (testElement.innerHTML.indexOf(locator) >= 0)) {
					_counter++;
					if(reqNumber == _counter)
						return testElement;
			} 
	}
}

Selenium.prototype.findZCalGrid = function(locator, panel, objNumber) {
	if(locator.indexOf("=")>0) {	
		return (this.browserbot.findElementOrNull(locator));
	}
	this._calHeader = new Array();
	this._calRows = new Array();
	var calView = this.findZCalView();
	if(calView == null)
		return null;

	var div1 = calView.getElementsByTagName("DIV");
	for (var j = 0; j < div1.length; j++) {
		var testElement = div1[j];
		if (testElement.className.indexOf("calendar_heading_day") >= 0) {
				if (testElement.textContent)
					var actTxt = testElement.textContent;
				else if (testElement.innerText)
					var actTxt = testElement.innerText;

				this._calHeader[j] = actTxt;
				this._calHeaderWidth = testElement.style.width;

		}
	}
	var td = calView.getElementsByTagName("TD");
	for (var j = 0; j < td.length; j++) {
		var testElement = td[j];
		if (testElement.className.indexOf("calendar_grid_body_time_td") >= 0) {
				if (testElement.textContent)
					var actTxt = testElement.textContent;
				else if (testElement.innerText)
					var actTxt = testElement.innerText;

				this._calRows[j] = actTxt;
				this._calRowHeight = testElement.style.height;
		}
	}

	
}

Selenium.prototype.findZFeatureMenu = function(locator, panel, objNumber) {
	if(locator.indexOf("=")>0) {	
		return (this.browserbot.findElementOrNull(locator));
	}
	var _counter = 0;
	var reqNumber = 1;
	if(objNumber != undefined && objNumber != "")
		var reqNumber = parseInt(objNumber);

	var divElements = this.getShellChildNodes();
	if(divElements == null)
		return null;
	var innerTxt = "";
	if(browserVersion.isIE)
		innerTxt = "innerText";
	else
		innerTxt = "textContent";
	if(panel == undefined || panel == "") {
		var check1 = "((parseInt(testElement.style.zIndex) == 300" + 
				 "|| (parseInt(testElement.style.zIndex) == 100 && testElement.style.display == \"block\"))"+		
				"&& (testElement."+innerTxt+".indexOf(locator) >= 0))";
	} else if(panel == "dialog"){
		var check1 =  "(parseInt(testElement.style.zIndex) >= 700 " +
			" && (testElement.className.indexOf(\"Dialog\") >= 0) "+
			" && (testElement."+innerTxt+".indexOf(locator) >= 0))";
	} else if(panel.indexOf("__dialogByName__")>=0){
				var dlgName = panel.replace("__dialogByName__", ""); 
		var check1 =  "(parseInt(testElement.style.zIndex) >= 700" + 
		" && (testElement.className.indexOf(\"Dialog\") >= 0)" + 
		" && (testElement."+innerTxt+".indexOf(\""+dlgName+"\") >= 0)" +
		" && (testElement."+innerTxt+".indexOf(locator) >= 0))";
	} else {
		return null;
	}
	var rowFound = false;
	var potentialRowArray = new Array();
	var rowCnt = 0;
	var row = "";
	for (var i = 0; i < divElements.length; i++) {
		var testElement = divElements[i];
		var testElementHtml = testElement.innerHTML;
	
		if (eval("(" + check1 + ")")) {	
			var tr1 = testElement.getElementsByTagName("TR");
			for (var j = 0; j < tr1.length; j++) {
				var testElement = tr1[j];
				var testElementHtml = testElement.innerHTML;
				var selectIndx = testElementHtml.indexOf("ZSelectAutoSizingContainer") ;
				var trIndx =  testElementHtml.toLowerCase().indexOf("<tr");
				if (testElement.textContent)
					var inTxt = testElement.textContent;
				else if (testElement.innerText)
						var inTxt = testElement.innerText;
				var txtIndx =  inTxt.indexOf(locator);

				if (selectIndx >= 0 && (trIndx == -1 || trIndx > selectIndx)  && txtIndx == 0){
					row = testElement;
					rowFound = true;
					break;
				} else if((selectIndx >= 0) && (selectIndx > trIndx)  && txtIndx >= 0) {
					potentialRowArray[rowCnt] = testElement;
					rowCnt++;
				}	

			}

		}
		if(rowFound)
			break;
	}

	if(rowFound)
		return this._getFeatureMenuFromRow(locator, row, reqNumber);
	else if(potentialRowArray.length >0 )
			return this._getFeatureMenuFromRow(locator, potentialRowArray[potentialRowArray.length-1], reqNumber);
	else
		return null;
};
//private
Selenium.prototype._getFeatureMenuFromRow = function(locator, row, objNumber) {
	var _counter = 1;
			//simple hack to skip one menu if the menu's name is middle of the row
			var rowHtml = row.innerHTML;
			var locatorIndx = rowHtml.indexOf(locator);
			var menuIndx = rowHtml.indexOf("ZSelectAutoSizingContainer");
			if(locatorIndx > menuIndx)
				objNumber++;
		var div1 = row.getElementsByTagName("div");
		for (var k = 0; k < div1.length; k++) {
				var testElement = div1[k];
					if (testElement.className.indexOf("ZSelectAutoSizingContainer") >= 0){
						if(objNumber == _counter)
							return testElement;
						else
							_counter++;
					}
		}
		return null;
}


Selenium.prototype.findZFolder_html = function(locator, panel, objNumber) {
	if(locator.indexOf("=")>0) {	
		return (this.browserbot.findElementOrNull(locator));
	}
	var ignoreFolderHdr = false;
	if(locator.indexOf("::ignoreFolderHdr") > 0) {
		locator = locator.replace("::ignoreFolderHdr","");
		ignoreFolderHdr	= true;
	}

	var _edit = false;
	var _expand = false;
	var _collapse = false;
	var _check = false;
	var _uncheck = false;
	if(locator.indexOf("_edit") >=0){
		locator = locator.replace("_edit","");
		_edit = true;
	} else if(locator.indexOf("_expand") >=0){
		locator = locator.replace("_expand","");
		_expand = true;
	} else if(locator.indexOf("_collapse") >=0){
		locator = locator.replace("_collapse","");
		_collapse = true;
	} else if(locator.indexOf("_check") >=0){
		locator = locator.replace("_check","");
		_check = true;
	} else if(locator.indexOf("_uncheck") >=0){
		locator = locator.replace("_uncheck","");
		_uncheck = true;
	}

	var _counter = 0;
	var reqNumber = 1;
	if(objNumber != undefined && objNumber != ""){
		var reqNumber = parseInt(objNumber);
	}

	var rows = null;
	var win = this.browserbot.getCurrentWindow();
	var inDocument = win.document;
	var tds =  inDocument.getElementsByTagName("td");
	for(var i=0; i< tds.length; i++) {
		var td = tds[i];
		if(td.className == "Overview" || td.className == "List"){
			var htm = td.innerHTML;
			if((htm.indexOf("TreeHeaderRow") >=0) || htm.indexOf("class=\"Folder\"") >=0  || htm.indexOf("class=Folder") >=0 || htm.indexOf("CalendarFolder.gif")>=0 || htm.indexOf("TaskList.gif")>=0) {
				rows = tds[i].getElementsByTagName("tr");
				break;
			}
		} 
	}

	if(rows == null)
		return null;


	for(var i=0; i< rows.length; i++) {
		var testElement = rows[i];
		if (testElement.textContent)
			var actTxt = testElement.textContent;
		else if (testElement.innerText)
			var actTxt = testElement.innerText;

		if(actTxt.indexOf(locator) >=0){
			if(ignoreFolderHdr){
				if(testElement.innerHTML.indexOf("Header") >0)
					continue;
			}
			_counter++;
			if(reqNumber == _counter){//found the row
				var tmp = testElement.getElementsByTagName("a");
				for(var j=0; j < tmp.length;j++){
					var a= tmp[j];
					if(!_collapse && !_expand && !_edit && !_uncheck && !_check && a.innerHTML.indexOf(locator)>0)
						return a;
					 else if(_collapse && a.innerHTML.indexOf("ImgNodeExpanded")>0)
						return a;
					else if(_expand && a.innerHTML.indexOf("ImgNodeCollapsed")>0)
						return a;
					else if(_check && a.innerHTML.indexOf("ImgTaskCheckbox")>0)
						return a;	
					else if(_uncheck && a.innerHTML.indexOf("ImgTask")>0)
						return a;							
					else if(_edit && a.parentNode.className == "ZhTreeEdit")//return edit-link
						return a;
				}
			}
		}
	}
	return null;
};


Selenium.prototype.findZButton_html = function(locator, panel, objNumber) {
	if(locator.indexOf("=")>0) {	
		return (this.browserbot.findElementOrNull(locator));
	}
	var _counter = 0;
	var reqNumber = 1;
	if(objNumber != undefined && objNumber != "")
		var reqNumber = parseInt(objNumber);


	var elements = this.getButtons_html();
	if(elements == null)
		return null;


	for(var i=0; i< elements.length; i++) {
		var testElement = elements[i];
		if(testElement.tagName.toLowerCase() == "input")
			var actTxt = testElement.value;
		else if (testElement.textContent)
			var actTxt = testElement.textContent;
		else if (testElement.innerText)
				var actTxt = testElement.innerText;

		if(actTxt.indexOf(locator) >=0){
			_counter++;
			if(reqNumber == _counter){
				return testElement;
			}
		}
	}
	return null;
};

Selenium.prototype.findZListItem_html = function(locator, panel, objNumber, listNumber) {
	if(locator.indexOf("=")>0) {	
		return (this.browserbot.findElementOrNull(locator));
	}
	var _counter = 0;
	var reqNumber = 1;
	if(objNumber != undefined && objNumber != "")
		var reqNumber = parseInt(objNumber);


	var elements = this.getFormClass_html("List");
	if(elements == null)
		return null;

	var rows = elements.getElementsByTagName("tr");
	for(var i=0; i< rows.length; i++) {
		var testElement = rows[i];
		if (testElement.textContent)
			var actTxt = testElement.textContent;
		else if (testElement.innerText)
				var actTxt = testElement.innerText;

		if(actTxt.indexOf(locator) >=0){
			_counter++;
			if(reqNumber == _counter){
				return testElement;
			}
		}
	}
	return null;
};

Selenium.prototype.findZLinkInListItem = function(linkObj, linkName) {
	var anchrs = linkObj.getElementsByTagName("a");
	for(var i = 0; i < anchrs.length; i++) {
		if(anchrs[i].innerHTML == linkName)
			return anchrs[i];
	}
	return null;
};

Selenium.prototype.findZListItem = function(locator, panel, objNumber, listNumber) {

	var win = this.browserbot.getCurrentWindow();
	var newBtn = true;
	var divElements = this.getShellChildNodes();
	if(divElements == null)
		return null;
	var reqObjNumber = 1;
	var reqListNumber = 1;
	if(objNumber != undefined && objNumber != "")
		var reqObjNumber = parseInt(objNumber);
	if(listNumber != undefined && listNumber != "")
		var reqListNumber = parseInt(listNumber);

	var list = "";
	for (var i = 0; i < divElements.length; i++) {
		var testElement = divElements[i];
		var testElementInnerHtml = testElement.innerHTML;
		
		if  (
			(parseInt(testElement.style.zIndex) >= 300 || (parseInt(testElement.style.zIndex) == 100 && testElement.style.display == "block")) 
			&& (testElementInnerHtml.indexOf("DwtListView")>=0 ||testElementInnerHtml.indexOf("ZmColListDiv")>=0 ||testElementInnerHtml.indexOf("ZmFilterListView")>=0 
			|| testElementInnerHtml.indexOf("DwtChooserListView")>=0 || testElementInnerHtml.indexOf("ZmContactSimpleView") >= 0
			|| testElement.className == "DwtListView")){

			if(testElement.className == "DwtListView" ){//special-case when the entire page is a listview
				list = testElement;
				break;
			}
			var div1 = testElement.getElementsByTagName("DIV");
			var counter =1;

			for (var j = 0; j < div1.length; j++) {
				var testElement = div1[j];
				var cls = testElement.className;
				if(cls=="DwtListView" || cls.indexOf("DwtChooserListView") >=0 
					|| cls.indexOf("ZmFilterListView") >=0 || cls.indexOf("ZmContactSimpleView") >=0
					||  cls.indexOf("ZmColListDiv") >=0){
					if(counter == reqListNumber) {
						list = testElement;
						break;
					} else if(counter < reqListNumber) {
						counter++;
					}
				}
			}
		}

		if(list != "")
			break;
	}

		if(list == "")
			return null;
		var counter =1;
		var div2 = list.getElementsByTagName("DIV");
		for (var k = 0; k < div2.length; k++) {
			var testElement = div2[k];
			var innerTxt = "";
			testElement.textContent ? innerTxt = testElement.textContent :  innerTxt = testElement.innerText;

			if ((testElement.className.indexOf("Row ") >= 0) && (innerTxt.indexOf(locator) >= 0)) {
				if(counter == reqObjNumber) {
					return testElement;
				} else if(counter < reqObjNumber) {
					counter++;
				}
					
			} 
		}

	return null;
};

Selenium.prototype.documentCore =  function(locator, action, linkName){	
	var row = this.findZDocumentTOCRow(locator);
	var element = null;
	if (linkName == "")
		linkName = locator;

	if(row == null)
		return null;

	var lnk = row.getElementsByTagName("a");
	for(var j=0;j<lnk.length;j++) {
		if(lnk[j].innerHTML.indexOf(linkName) >=0) {
			var element = lnk[j];
			break;
		}
	}
	if(element == null)
		return null;
	return	this.actOnZElement(element, action, locator); 
}


Selenium.prototype.findZDocumentTOCRow = function(locator) {

		var frame = this.findZIframeByView("ZmNotebookPageView");
		if(frame == null)
			return null;
	
		var rowElements = frame.getElementsByTagName("TR");
		for (var i = 0; i < rowElements.length; i++) {
			var testElement = rowElements[i];
			if (testElement.className.indexOf("zmwiki-dotLine") >= 0 && testElement.innerHTML.indexOf(locator) >0 ) {
				return testElement;
			}
		}
		return null;

};

Selenium.prototype.findZEditor = function( panel, param1) {
	return this.findZIframeOrTextArea("ZmHtmlEditor", panel, param1);
}
Selenium.prototype.findZMsgBody = function( panel, param1) {
	return this.findZIframeOrTextArea("MsgBody", panel, param1);
}
Selenium.prototype.findZMsgHeader = function(locator, panel, objNumber) {
	if(locator.indexOf("=")>0) {	
		return (this.browserbot.findElementOrNull(locator));
	}
	var divElements = this.getShellChildNodes();
	if(divElements == null)
		return null;
	for (var i = 0; i < divElements.length; i++) {
		var testElement = divElements[i];
		if (parseInt(testElement.style.zIndex) >= 300 && (testElement.innerHTML.indexOf("MsgHeaderTable") >= 0)) {
			var div1 = testElement.getElementsByTagName("table");
			for (var j = 0; j < div1.length; j++) {
				var testElement = div1[j];
				if (testElement.className.indexOf("MsgHeaderTable") >= 0) {
					return testElement;
				}
			}
	
		}
	}
}


//returns the first iframe thats visible based on the view
Selenium.prototype.findZIframeByView = function(locator) {
	if(locator.indexOf("=")>0) {	
		return (this.browserbot.findElementOrNull(locator));
	}
	var win = this.browserbot.getCurrentWindow();
	var divElements = this.getShellChildNodes();
	if(divElements == null)
		return null;

	for (var i = 0; i < divElements.length; i++) {
		var testElement = divElements[i];
		if (parseInt(testElement.style.zIndex) >= 300 && (testElement.className.indexOf(locator) >= 0)) {
				var editorObjIframes = testElement.getElementsByTagName("iframe");
				if(editorObjIframes.length >0) {
					for(var n=0; n< editorObjIframes.length; n++) {
						var frame = editorObjIframes[0];
						if(!frame.style.display)
							return frame.contentWindow.document.body;
						else if(frame.style.display != "hidden")
							return frame.contentWindow.document.body;
					}
				}
			}
	}
	return null;
}
Selenium.prototype.findZIframeOrTextArea = function(locator, panel, param1) {

	if(locator.indexOf("=")>0) {	
		return (this.browserbot.findElementOrNull(locator));
	}
	var win = this.browserbot.getCurrentWindow();
	var divElements = this.getShellChildNodes();
	if(divElements == null)
		return null;


	for (var i = 0; i < divElements.length; i++) {
		var testElement = divElements[i];
		if (parseInt(testElement.style.zIndex) >= 300 && (testElement.innerHTML.indexOf(locator) >= 0)) {
			var div1 = testElement.getElementsByTagName("DIV");
			for (var j = 0; j < div1.length; j++) {
				var testElement = div1[j];
				if (testElement.className.indexOf(locator) >= 0) {
					var editorObjs = testElement.getElementsByTagName("textarea");
					var editorObjIframes = testElement.getElementsByTagName("iframe");
					if(editorObjs.length == 1) {
						if(editorObjs[0].style.display != "none") 
							return editorObjs[0];
					}
					if(editorObjIframes.length >0) {
						for(var n=0; n< editorObjIframes.length; n++) {
							var frame = editorObjIframes[0];
							if(!frame.style.display)
								return frame.contentWindow.document.body;
							else if(frame.style.display != "hidden")
								return frame.contentWindow.document.body;
						}
						//return	editorObjIframes[0].contentWindow.document.body;
					}
				}
			}
	
		}
	}		
}

Selenium.prototype.findZIframeOrTextArea_html = function(locator, panel, param1) {
	var form = this.getForm_html();
	var arry = new Array();

	if(form == null)
		return null;

	try{
		var iframe = this.browserbot.getCurrentWindow().document.getElementById("body_editor");
		if(iframe){
			return iframe.contentWindow.document.body;
		}
	} catch(e){}
	


	var textareas =  form.getElementsByTagName("textarea");
	for(var i=0; i< textareas.length; i++) {
		var ta =  textareas[i];
		try{
			if(ta.className == "MsgCompose" && ta.style.display != "hidden" && ta.type != "hidden"){
				return ta;
			}
		}catch(e) {}
	}
	return null;
	
	
}

Selenium.prototype.msgBodyCore = function(locator, action, data, panel, objNumber) {

	var element = this.findZMsgBody(locator, panel, objNumber);
	
	if(element == null && action == "notexist")
        return true;
	else if(element == null)
		return false;
	
		return	this.actOnZElement(element, action, locator); 
}
Selenium.prototype.msgHeaderCore = function(locator, action, data, panel, objNumber) {
	var element = this.findZMsgHeader(locator, panel, objNumber);
	
	if(element == null && action == "notexist")
        return true;
	else if(element == null)
		return false;
	
		return	this.actOnZElement(element, action, locator); 
}


Selenium.prototype.isHtmlClient = function() {
	if(this.browserbot.baseUrl.indexOf("/h/") >=0)
		return true;
	else
		return false;
}


Selenium.prototype.findZIconButton = function(locator) {

if(locator.indexOf("=")>0) {	
	return (this.browserbot.findElementOrNull(locator));
}
	var win = this.browserbot.getCurrentWindow();
	var newBtn = true;
	var inDocument = win.document;
	    // Loop through all elements, looking for ones that have 
	// a value === our expected value
	//var divElements = inDocument.getElementsByTagName("DIV");
	try {
		var divElements = inDocument.getElementById("z_shell").childNodes;
	} catch(e) {
		return null;
	}
	for (var i = 0; i < divElements.length; i++) {
		var testElement = divElements[i];
		if (parseInt(testElement.style.zIndex) == 300 &&
		    (testElement.className.indexOf("DwtControl") == -1) &&
		    ((testElement.className.indexOf("ZToolbar") >= 0) || (testElement.className.indexOf("ZmAppToolBar") >= 0))) {
			var div1 = testElement.getElementsByTagName("DIV");
			for (var j = 0; j < div1.length; j++) {
				var testElement = div1[j];
				if ((testElement.className.indexOf("ZToolbarButton") >= 0) && (testElement.innerHTML.indexOf(locator) >= 0)) {
					return testElement;
				}
			}

		}
	}
	return null;
};


Selenium.prototype.findZButtonInDlg = function(buttonName, dialogName) {

if(buttonName.indexOf("=")>0) {	
	return (this.browserbot.findElementOrNull(buttonName));
}
	var divElements = (this.findZDialog(dialogName)).getElementsByTagName("DIV");
	for (var j = 0; j < divElements.length; j++) {
		var testElement = divElements[j];
		if ((testElement.className.indexOf("ZButton") >= 0) && (testElement.innerHTML.indexOf(buttonName) >= 0))
			return testElement;
	}
	return null;
};


Selenium.prototype.findZTab = function(locator, panel, objNumber) {

if(locator.indexOf("=")>0) {	
	return (this.browserbot.findElementOrNull(locator));
}
var tabCount = 0;
var reqNumber = 1;
if(objNumber != undefined && objNumber != "")
	var reqNumber = parseInt(objNumber);

	var divElements = this.getShellChildNodes();
	if(divElements == null)
		return null;
	if(panel == undefined || panel == "") {
		var check1 ="((parseInt(testElement.style.zIndex) == 300 " + 
				 "|| (parseInt(testElement.style.zIndex) == 100 && testElement.style.display == \"block\"))"+
				" && (testElement.innerHTML.indexOf(locator) >= 0) && " +
		    "(testElement.className.indexOf(\"ZToolbar\") == -1))";
	} else if(panel == "dialog"){
		var check1 =  "(parseInt(testElement.style.zIndex) >= 700 " +
			" && (testElement.className.indexOf(\"Dialog\") >= 0) "+
			" && (testElement.innerHTML.indexOf(locator) >= 0))";
	} else if(panel.indexOf("__dialogByName__")>=0){
				var dlgName = panel.replace("__dialogByName__", ""); 
		var check1 =  "(parseInt(testElement.style.zIndex) >= 700" + 
		" && (testElement.className.indexOf(\"Dialog\") >= 0)" + 
		" && (testElement.innerHTML.indexOf(\""+dlgName+"\") >= 0)" +
		" && (testElement.innerHTML.indexOf(locator) >= 0))";
	} else {
		return null;
	}

	for (var i = 0; i < divElements.length; i++) {
		var testElement = divElements[i];
				if (eval("(" + check1 + ")"))  {
			
			var div1 = testElement.getElementsByTagName("DIV");
			
			for (var j = 0; j < div1.length; j++) {
				var testElement = div1[j];
				if (((testElement.className.indexOf("Button") >= 0) || (testElement.className.indexOf("ZTab ") >= 0)) && (testElement.innerHTML.indexOf(locator) >= 0)) {
					tabCount++;
					if(reqNumber == tabCount)
						return testElement;
				}
			}

		}
	}
	return null;
};

Selenium.prototype.findZAppTab = function(locator) {

if(locator.indexOf("=")>0) {	
	return (this.browserbot.findElementOrNull(locator));
}
	var divElements = this.getShellChildNodes();
	if(divElements == null)
		return null;
	for (var i = 0; i < divElements.length; i++) {
		var testElement = divElements[i];
		if (parseInt(testElement.style.zIndex) >= 300 && (testElement.innerHTML.indexOf(locator) >= 0)) {
			var div1 = testElement.getElementsByTagName("DIV");
			for (var j = 0; j < div1.length; j++) {
				var testElement = div1[j];
				if ((testElement.className.indexOf("Button") >= 0)
						&& (testElement.innerHTML.indexOf(locator) >= 0)
						&& (testElement.className.indexOf("ZToolbar") == -1)
						&& (testElement.className.indexOf("ZAppTab") >= 0))
				{

					return testElement;
			}
			}

		}
	}

	return null;
};


Selenium.prototype.folderCollapseBtnCore = function(locator, action, panel, param1) {
	var element;
	var testElement = this.findZFolder(locator, panel, param1);
	
	if(testElement == null) 
		return false;
	var div1 = testElement.getElementsByTagName("DIV");
	for (var j = 0; j < div1.length; j++) {
		var testElement = div1[j];
		if(testElement.className == "ImgNodeExpanded") {
			var element = testElement;
			break;	
		}
    }
	if(element == null && action == "notexist")
        return true;
	else if(element == null)
		return false;
	else
		return	this.actOnZElement(element, action, locator); //for click, rtclick, shiftclick etc	
}

Selenium.prototype.folderCheckboxCore = function(locator, action, panel, param1) {
	var element;
	var testElement = this.findZFolder(locator, panel, param1);
	if(testElement == null) 
		return false;
	var div1 = testElement.getElementsByTagName("DIV");
	for (var j = 0; j < div1.length; j++) {
		var testElement = div1[j];
		if(testElement.className == "ZTreeItemCheckbox") {
			var element = testElement;
			break;	
		}
    }

	if(element == null && action == "notexist"){
        return true;
	} else if(element == null) {
		return false;
	} else {
		return	this.actOnZElement(element, action, locator); //for click, rtclick, shiftclick etc	
	}
};

Selenium.prototype.folderExpandBtnCore = function(locator, action, panel, param1) {
	var element;
	var testElement = this.findZFolder(locator, panel, param1);
	
	if(testElement == null) 
		return false;
	var div1 = testElement.getElementsByTagName("DIV");
	for (var j = 0; j < div1.length; j++) {
		var testElement = div1[j];
		if(testElement.className == "ImgNodeCollapsed") {
			var element = testElement;
			break;	
		}
    }

	if(element == null && action == "notexist")
        return true;
	else if(element == null)
		return false;
	else
		return	this.actOnZElement(element, action, locator); //for click, rtclick, shiftclick etc	
}

Selenium.prototype.apptCore = function(locator, action, panel, param1) {
	var element = this.findZAppt(locator, panel, param1);

	if(element == null && action == "notexist")
        return true;
	else if(element == null)
		return false;
	else 
		return	this.actOnZElement(element, action, locator); //for click, rtclick, shiftclick etc
		
}

Selenium.prototype.apptCore_html = function(locator, action, panel, param1) {
    var element = this.findZAppt_html(locator, panel, param1);

	if(element == null && action == "notexist")
        return true;
	else if(element == null)
		return false;
	else 
		return	this.actOnZElement(element, action, locator); //for click, rtclick, shiftclick etc
		
}
Selenium.prototype.calGridCore_html = function(locator, action, panel, param1) {
		var elementOrValue = this.findZcalGrid_html(locator, action, panel, param1);

	if(elementOrValue == null && action == "notexist")
        return true;
	else if(elementOrValue == null)
		return false;
	else if(action == "getCount" || action == "getDT")
		return elementOrValue;
	else 
		return	this.actOnZElement(element, action, locator); //for click, rtclick, shiftclick etc
		
}



Selenium.prototype.folderCore = function(locator, action, panel, param1) {
	var element = this.findZFolder(locator, panel, param1);
	if(element == null && action == "notexist")
		return true;
	else if(element == null)
		return false;
	 else 
		return	this.actOnZElement(element, action, locator); //for click, rtclick, shiftclick etc
		

}

Selenium.prototype.featureMenuCore = function(locator, action, panel, param1) {
		var element = this.findZFeatureMenu(locator, panel, param1);
	if(element == null && action == "notexist")
        return true;
	else if(element == null)
		return false;
	else 
		return	this.actOnZElement(element, action, locator); //for click, rtclick, shiftclick etc
		
}

Selenium.prototype.tabCore_html = function(locator, action, panel, objNumber, param2) {
	var element = this.findZTabs_html(locator, panel, objNumber);
	if(element == null && action == "notexist")
        return true;
	else if(element == null)
		return false;
	else 
		return	this.actOnZElement(element, action, locator, "", param2); //for click, rtclick, shiftclick etc
		
}

Selenium.prototype.folderCore_html = function(locator, action, panel, objNumber, param2) {
	var element = this.findZFolder_html(locator, panel, objNumber);
	if(element == null && action == "notexist")
        return true;
	else if(element == null)
		return false;
	else 
		return	this.actOnZElement(element, action, locator, "", param2); //for click, rtclick, shiftclick etc
		
}

Selenium.prototype.tabCore = function(locator, action, panel, param1) {
		var element = this.findZAppTab(locator, panel, param1);

	if(element == null && action == "notexist")
        return true;
	else if(element == null)
		return false;
	else 
		return	this.actOnZElement(element, action, locator); //for click, rtclick, shiftclick etc
		
}

Selenium.prototype.tabCore = function(locator, action, panel, param1) {
	var element = this.findZTab(locator, panel, param1);
	if(element == null && action == "notexist")
        return true;
	else if(element == null)
		return false;
	else 
		return	this.actOnZElement(element, action, locator); //for click, rtclick, shiftclick etc
		

}

Selenium.prototype.listItemCore_html = function(locator, action, panel, objNumber, listNumber) {
	var actOnLabel = false;//if true, finds the internal td/span object with the locator
	if(locator.indexOf("::actOnLabel") > 0) {
		locator = locator.replace("::actOnLabel", "");
		actOnLabel = true;
	}
	var element = this.findZListItem_html(locator, panel, objNumber, listNumber);
	if(element == null && action == "notexist")
        return true;
	else if(element == null)
		return false;

	var elementInnerHTML = element.innerHTML;	
	if(action == "isUnread") {
		if(elementInnerHTML.indexOf("Unread") >= 0)
			return false;
		else 
            return true;
	} else if(action == "isRead") {
		if(elementInnerHTML.indexOf("class=\"Unread\"") >= 0)
			return false;
		else 
            return true;
	} else if(action == "isSelected") {
		if(elementInnerHTML.indexOf("Row-selected") >= 0)
			return false;
		else 
            return true;
	}  else if(action == "isTagged") {

		if(elementInnerHTML.indexOf("ImgTag") >= 0)
            return true;
		else 
			return false;
	}  else if(action == "isNotTagged") {
		if(elementInnerHTML.indexOf("ImgTag") >= 0)
			return false;
		else 
            return true;
	}  else if(action == "hasAttachment") {
		if(elementInnerHTML.indexOf("ImgAttachment") >= 0)
            return true;
		else 
			return false;
	}  else if(action == "hasNoAttachment") {
		if(elementInnerHTML.indexOf("ImgAttachment") >= 0)
			return false;
		else 
            return true;
	}  else if(action == "isFlagged"  ) {
		if(elementInnerHTML.indexOf("ImgFlagRed") >= 0)
            return true;
		else 
			return false;
	}  else if(action == "isNotFlagged") {
		if(elementInnerHTML.indexOf("class=\"ImgFlagRed\"") >= 0)
			return false;
		else 
            return true;
	}  else if(action == "hasHighPriority") {
		if(elementInnerHTML.indexOf("ImgPriorityHigh_list") >= 0)
            return true;
		else 
			return false;
	}  else if(action == "hasLowPriority" ) {
		if(elementInnerHTML.indexOf("ImgPriorityLow_list") >= 0)
            return true;
		else 
			return false;
	} else if((action == "selectchkbox") || (action == "ischecked") || (action == "isunchecked")) {
			var element = element.getElementsByTagName("input")[0];
			if(element == null && action == "notexist")
                return true;
			else if(element == null)
				false;
			else if(action == "selectchkbox")
				return	this.actOnZElement(element, "click", locator); //change action to click
			else if (action == "ischecked") 
					return element.checked;
			else if (action == "isunchecked"){
					return !element.checked;
			}
	} else if(actOnLabel) {
			var validSpanFound = false;
			var td1 = element.getElementsByTagName("TD");
			for (var j = 0; j < td1.length; j++) {
				var testElement = td1[j];
				if(testElement.innerHTML.indexOf(locator)>=0 && testElement.innerHTML.toLowerCase().indexOf("<td")==-1) {
					//check if there is a valid-span element, if so, return that.
					var span = testElement.getElementsByTagName("SPAN");
					for (var k = 0; k < span.length; k++) {
						var spanEl = span[k];
						if(spanEl.innerHTML.indexOf(locator) >=0) {
							var element = spanEl;
							validSpanFound = true;
							break;	
						}
					}

					//if there are no valid span elements, then use the td element
					if(!validSpanFound)
						var element = testElement;


					break;//from the outer for loop	
	
				}
			}

			if(element == null && action == "notexist")
                return true;
			else if(element == null)
				return false;
			else
				return	this.actOnZElement(element, action, locator); //for click, rtclick, shiftclick etc	
	} else
		 return	this.actOnZElement(element, action, locator); //for click, rtclick, shiftclick etc

}

Selenium.prototype.listItemCore = function(locator, action, panel, objNumber, listNumber) {
	var actOnLabel = false;//if true, finds the internal td/span object with the locator
	if(locator.indexOf("::actOnLabel") > 0) {
		locator = locator.replace("::actOnLabel", "");
		actOnLabel = true;
	}
	var element = this.findZListItem(locator, panel, objNumber, listNumber);
	if(action.indexOf("clickLink=")!= -1) {
		if(element != null) {
			var linkName = action.replace("clickLink=","");
			action = "click";
			element = this.findZLinkInListItem(element, linkName);
		}
	}
	if(element == null && action == "notexist")
        return true;
	else if(element == null)
		return false;
	var elementInnerHTML = element.innerHTML;
	
	if(action == "isUnread") {
		if(elementInnerHTML.indexOf("Unread") >= 0)
            return true;
		else 
			return false;
	} else if(action == "isRead") {
		if(elementInnerHTML.indexOf("class=\"Unread\"") >= 0)
			return false;
		else 
            return true;
	} else if(action == "isSelected") {
		if(elementInnerHTML.indexOf("Row-selected") >= 0)
			return false;
		else 
            return true;
	}  else if(action == "isTagged") {

		if(elementInnerHTML.indexOf("ImgTag") >= 0)
            return true;
		else 
			return false;
	}  else if(action == "isNotTagged") {
		if(elementInnerHTML.indexOf("class=\"ImgTag") >= 0)
			return false;
		else 
            return true;
	}  else if(action == "hasAttachment") {
		if(elementInnerHTML.indexOf("class=\"ImgAttachment\"") >= 0)
            return true;
		else 
			return false;
	}  else if(action == "hasNoAttachment") {
		if(elementInnerHTML.indexOf("class=\"ImgAttachment\"") >= 0)
			return false;
		else 
            return true;
	}  else if(action == "isFlagged") {
		if(elementInnerHTML.indexOf("ImgFlagRed") >= 0)
            return true;
		else 
			return false;
	}  else if(action == "isNotFlagged") {
		if(elementInnerHTML.indexOf("class=\"ImgFlagRed\"") >= 0)
			return false;
		else 
            return true;
	}  else if(action == "hasHighPriority") {
		if(elementInnerHTML.indexOf("class=\"ImgPriorityHigh_list\"") >= 0)
            return true;
		else 
			return false;
	}  else if(action == "hasLowPriority") {
		if(elementInnerHTML.indexOf("class=\"ImgPriorityLow_list\"") >= 0)
            return true;
		else 
			return false;
	} else if((action == "expand") || (action == "collapse")) {
			if(action == "expand")
				var cls =  "ImgNodeCollapsed";
			else
				var cls =  "ImgNodeExpanded";

			var div1 = testElement.getElementsByTagName("DIV");
			for (var j = 0; j < div1.length; j++) {
				var testElement = div1[j];
				if(testElement.className == cls) {
					var element = testElement;
					break;	
				}
			}

			if(element == null && action == "notexist")
                return true;
			else if(element == null)
				return false;
			else
				return	this.actOnZElement(element, action, locator); //for click, rtclick, shiftclick etc	
	} else if(actOnLabel) {
			var validSpanFound = false;
			var td1 = element.getElementsByTagName("TD");
			for (var j = 0; j < td1.length; j++) {
				var testElement = td1[j];
				if(testElement.innerHTML.indexOf(locator)>=0 && testElement.innerHTML.toLowerCase().indexOf("<td")==-1) {
					//check if there is a valid-span element, if so, return that.
					var span = testElement.getElementsByTagName("SPAN");
					for (var k = 0; k < span.length; k++) {
						var spanEl = span[k];
						if(spanEl.innerHTML.indexOf(locator) >=0) {
							var element = spanEl;
							validSpanFound = true;
							break;	
						}
					}

					//if there are no valid span elements, then use the td element
					if(!validSpanFound)
						var element = testElement;


					break;//from the outer for loop	
	
				}
			}

			if(element == null && action == "notexist")
                return true;
			else if(element == null)
				return false;
			else
				return	this.actOnZElement(element, action, locator); //for click, rtclick, shiftclick etc	
	} else
		 return	this.actOnZElement(element, action, locator); //for click, rtclick, shiftclick etc

}
Selenium.prototype.htmlMenuCore_html = function(locator, action, itemToSelect, itemNumber, menuNumber) {
	var element = this.findZFormObjectsMultipleElements_html(locator,"select", null, menuNumber);
	var itemsStr = "";

	var matchPartialText = false;
	if(itemToSelect.indexOf(".*") > 0) {
		itemToSelect = itemToSelect.replace(".*","");
		matchPartialText = true;
	}

	var reqNumber = 1;
	var _counter = 0;
	if(itemNumber != undefined && itemNumber != "")
		 reqNumber = parseInt(itemNumber);

	if(element == null && action == "notexist")
        return true;
	else if(element == null)
		return false;
	else if(action == "click"){
		triggerEvent(element, 'focus', false);
	    var changed = false;
		var reqSelected = false;
		var totalItems  =  element.options.length;
		
		for (var i = 0; i < totalItems; i++) {
			var option = element.options[i];
			var actText = option.text;
			if (option.selected && (itemToSelect != actText || (matchPartialText && actText.indexOf(itemToSelect)== -1))) {
				option.selected = false;
				changed = true;
			}
			else if (!option.selected && (itemToSelect == actText || (matchPartialText && actText.indexOf(itemToSelect)>=0))) {
				_counter++;
				if(_counter == reqNumber) {
					option.selected = true;
					changed = true;					
				} else{
					option.selected = false;
					changed = true;
				}

			}
		}

		if (changed) {
			triggerEvent(element, 'change', true);
		}
		
		if(reqSelected)
            return true;
		else
			return false;

	} else if ( action=="getCount"){
		return element.options.length;
	} else if(action == "getAllItems") {
		var itmsName = "";
		var totalItems  =  element.options.length;
		for (var i = 0; i < totalItems; i++) {
			var option = element.options[i];
			actText = option.text;
			if(itmsName == "")
				itmsName = itmsName + actText;
			else
				itmsName = itmsName + "::" + actText;
		}
		return itmsName;

	} else if(action == "getSelected") {
		var totalItems  =  element.options.length;
		for (var i = 0; i < totalItems; i++) {
			var option = element.options[i];
			if(option.selected) {
					return option.text;
			}

		}
	} else
		 return	this.actOnZElement(element, action, locator); //for click, rtclick, shiftclick etc

}

Selenium.prototype.menuItemCore = function(locator, action, panel, param1) {
	var element = this.findZMenuItem(locator, panel, param1);
	if (element == null && action == "notexist")
		return true;
	else if (element == null)
		return false;
	else
		return this.actOnZElement(element, action, locator); //for click, rtclick, shiftclick etc

}

Selenium.prototype.dialogCore = function(locator, action, panel, param1) {
	var element = this.findZDialog(locator, panel, param1);
	if(element == null && action == "notexist")
        return true;
	else if(element == null)
		return false;


		if(action =="getmessage") {
			var el = element.getElementsByTagName("*");
			for (var j = 0; j < el.length; j++) {
				var testElement = el[j];
				if(testElement.className.indexOf("DwtMsgArea")>=0 || testElement.className.indexOf("DwtConfirmDialogQuestion")>=0) {
					if (testElement.textContent)
						return testElement.textContent;
					else if (testElement.innerText)
						return testElement.innerText;
				}
			 }
			
		} else if(action == "getalltxt"){
			if (testElement.textContent)
				return testElement.textContent;
			else if (testElement.innerText)
				return testElement.innerText; 
		} else {
			 return	this.actOnZElement(element, action, locator); //for click, rtclick, shiftclick etc
		}
		return "Could not retrieve message. Check Selenium.prototype.dialogCore";
}

Selenium.prototype.findZMenuItem = function(menuItem, panel, param1) {
	if(menuItem.indexOf("=")>0) {	
		return (this.browserbot.findElementOrNull(menuItem));
	}
	var divElements = this.getShellChildNodes();
	if(divElements == null)
		return null;
	for (var i = 0; i < divElements.length; i++) {
		var testElement = divElements[i];
		if (parseInt(testElement.style.zIndex) >= 500 && (testElement.className.indexOf("Menu") >= 0) && (testElement.innerHTML.indexOf(menuItem) >= 0)) {
			var div1 = testElement.getElementsByTagName("DIV");

			for (var j = 0; j < div1.length; j++) {
				var testElement = div1[j];
				if (
					(	(testElement.className.indexOf("ZMenuItem") >= 0)
						||  (testElement.className.indexOf("ZSelectMenuItem") >= 0)
					) 
					&& (testElement.innerHTML.indexOf(menuItem) >= 0)
					) {
					if (testElement.textContent)
						var actText = testElement.textContent;
					else if (testElement.innerText)
						var actText = testElement.innerText;
					
					if(actText.indexOf("[") > 0)//strip text of any shortcut keys
						actText = actText.substring(0, actText.indexOf("["));


					if(actText == menuItem || actText == (menuItem +" "))
						return testElement;
			}
		}
		}

	}
	return null;

}
Selenium.prototype.zGetBrowserUserAgent = function()  {
	return navigator.userAgent;
}
Selenium.prototype.zGetZimbraVersion = function(param1, param2, param3, param4, param5, param6)  {
	var win = this.browserbot.getCurrentWindow();
	 return win.appCtxt.getSettings().getSetting("SERVER_VERSION").value;
}

Selenium.prototype.verifyZTable = function(tableId) {
	var divElements = this.getShellChildNodes();
	if(divElements == null)
		return null;
	for (var i = 0; i < divElements.length; i++) {
		var testElement = divElements[i];
		var zIndx = parseInt(testElement.style.zIndex);
		if (!isNaN(zIndx) && (zIndx >= 300) && (testElement.innerHTML.indexOf(tableId) >= 0)) {
			var tblElements = testElement.getElementsByTagName("TABLE");
			for (var j = 0; j < tblElements.length; j++) {
				var testElement = tblElements[j];
				try {
					if (testElement.id.indexOf(tableId) >= 0) {
						return true;
					}
				} catch(e) {
				}
			}
		}
	}
	return false;
}

Selenium.prototype._getInnerMostElement = function(element, elementName) {
	var children = element.childNodes;
	for (var i = 0; i < children.length; i++) {
		var child = children[i];
		if (child.textContent.indexOf(elementName) >= 0 && child.childNodes.length > 0) {
			this._getInnerMostElement(child, elementName);
		} else if (child.textContent.indexOf(elementName) >= 0 && child.childNodes.length == 0) {
			return child;
		}
	}


	return null;
}

Selenium.prototype.verifyZview = function(viewName) {
	var className;
	switch (viewName) {
		case "Message":
			className = "ZmTradView";
			break;
		case "Conversation":
			className = "ZmConvDoublePaneView";
			break;
		case "Mail Compose":
			className = "ZmComposeView";
			break;
		case "Appointment Compose":
			className = "ZmApptComposeView";
			break;

		case "List":
			className = "ZmContactSplitView";
			break;
		case "Card":
			className = "ZmContactCardsView";
			break;
		case "Notebook Compose":
			className = "ZmPageEditView";
			break;
	}

	var win = this.browserbot.getCurrentWindow();
	var inDocument = win.document;
	    // Loop through all elements, looking for ones that have 
	// a value === our expected value
	//var divElements = inDocument.getElementsByTagName("DIV");
	try {
		var divElements = inDocument.getElementById("z_shell").childNodes;
	} catch(e) {
		return null;
	}
	for (var i = 0; i < divElements.length; i++) {
		var testElement = divElements[i];
		if (parseInt(testElement.style.zIndex) >= 300 && (testElement.className.indexOf(className) >= 0))
			return true;
	}
	return false;
};

Selenium.prototype.findZApp = function(locator) {

if(locator.indexOf("=")>0) {	
	return (this.browserbot.findElementOrNull(locator));
}
	var win = this.browserbot.getCurrentWindow();
	var inDocument = win.document;
	    // Loop through all elements, looking for ones that have 
	// a value === our expected value
	//var divElements = inDocument.getElementsByTagName("DIV");
	try {
		var divElements = inDocument.getElementById("z_shell").childNodes;
	} catch(e) {
		return null;
	}
	for (var i = 0; i < divElements.length; i++) {
		var testElement = divElements[i];
		if (parseInt(testElement.style.zIndex) == 300 && (testElement.className.indexOf("ZmAppChooser") >= 0) &&
		    (testElement.innerHTML.indexOf(locator) >= 0)) {
			var div1 = testElement.getElementsByTagName("DIV");
			for (var j = 0; j < div1.length; j++) {
				var testElement = div1[j];
				if ((testElement.className.indexOf("ZButton") >= 0) && (testElement.innerHTML.indexOf(locator) >= 0))
					return testElement;
			}			

		}
	}
	return null;
};




Selenium.prototype.verifyZDisplayed = function(locatorWithZIndx) {
	var element = this.browserbot.findElementOrNull(locatorWithZIndx);
	if (element != null && (element.style.zIndex >=300 || (parseInt(element.style.zIndex) == 100 && element.style.display == "block")) ) {
        return true;
	} else
		return false;

}

Selenium.prototype.msgZHdrBodyCore_html = function(locator, action) {
	var element = this.findZElementByClassOrId_html(locator);
	if(element == null && (action == "notexist" || action == "notdisplayed")){
        return true;
	}else if(element == null){
		return false;
	} else if(action == "gethtml"){
			return element.innerHTML;
	}

	if(locator == "MsgBody") {
		if(element.innerHTML.indexOf("iframe")>0){//if msg is an iframe, get the body
			var iframe = element.getElementsByTagName("iframe")[0];
			try{
				if(iframe.contentDocument)  {
					element =iframe.contentDocument.body;
				} else if(iframe.contentWindow)  {
					var doc =iframe.contentWindow.document;
					element = doc.getElementsByTagName("body")[0];

				}
			} catch(e) {

			}
		}
	}

	if(element == null || element == undefined)
		 return false;

	return	this.actOnZElement(element, action); 
	
}

Selenium.prototype.miscZObjectCore_html = function(locator, action, panel, objNumber, param2) {
	var element = this.findZElementByClassOrId_html(locator, panel, objNumber);
	if(element == null && (action == "notexist" || action == "notdisplayed"))
        return true;
	else if(element == null)
		return false;
	else 
		return	this.actOnZElement(element, action); 
	
}


Selenium.prototype.findZFormObjectsMultipleElements_html = function(locatorWithNameOrId, objTag, objType, objNumber) {
	var reqNumber = 1;
	var _counter = 0;
	if(objNumber != undefined && objNumber != ""){//panel has objNumber(yuck)
			reqNumber = parseInt(objNumber);
		}
	var mainForm = this.getForm_html();
	if(mainForm == null)
		return null;

	locatorWithNameOrId = locatorWithNameOrId.replace("id=","").replace("name=","");
	var formObjs =  mainForm.getElementsByTagName(objTag);
	for(var i=0; formObjs.length; i++) {
		var obj = formObjs[i];
		try{
			if(objType) {//objType is passed as null for html-menus(<select>)
				if(objType != obj.type)
					continue;
			}
			if(obj.id == locatorWithNameOrId || obj.name == locatorWithNameOrId){
				_counter++;
				if(_counter == reqNumber) {
					return obj;						
				}
			}
		}catch(e) {}
		
	}
	
	return null;
	
}

Selenium.prototype.findZElementByClassOrId_html = function(locator, panel, objNumber) {
	var element = this.getForm_html();
	if(element == null)
		return null;
	var temp = locator.split("/");
	for(var j=0; j<temp.length; j++) {
		var innerClassOrId = temp[j];
		if(innerClassOrId.indexOf("*") >=0)
			innerClassOrId = innerClassOrId.replace("*", "");
		if(j == temp.length-1)//last element
			var element =  this._getInnerObjFromMainObj_html(element, innerClassOrId, objNumber);
		else
			var element =  this._getInnerObjFromMainObj_html(element, innerClassOrId, 1);
		if(element == null)
			break;
	}
	return element;
	
}


Selenium.prototype.miscZObjectCore = function(classNameOridWithZIndx, action, panel,useXY, xyValue) {
	//if classname is passed, if one of the classname matches, it returns true
	//action can be click,dblClick, displayed
	//you can also enter class1/class2OrId/class3OrId where, class1orId has zIndex, and class2OrId is an innerElement thats
	//within class1. finally class3OrId is further down and within class2OrId.
	//	usage in java: str = obj.zMiscObj.zExistsDontWait("ZmApptComposeView ZWidget/*tzoneSelect/ZSelectAutoSizingContainer ZHasDropDown");

	var class2OrId = "";
	var hasInnerClass = false;
	var class1OrId = "";

	if(classNameOridWithZIndx.indexOf("/")>0) {
		var temp = classNameOridWithZIndx.split("/");
		class1OrId  = temp[0];
		if(class1OrId.indexOf("*") >=0)
			class1OrId = class1OrId.replace("*", "");
		
		hasInnerClass = true;

	} else {
			class1OrId = classNameOridWithZIndx;
			if(class1OrId.indexOf("*") >=0)
				class1OrId = class1OrId.replace("*", "");

	}
	var divElements = this.getShellChildNodes();
	for (var i = 0; i < divElements.length; i++) {
		var testElement = divElements[i];
		if ((testElement.className.indexOf(class1OrId)>=0 || testElement.id.indexOf(class1OrId)>=0) 
			&& (testElement.style.zIndex >=300 || (parseInt(testElement.style.zIndex) == 100 && testElement.style.display == "block"))
			&& testElement.style.top != "0px") {
			var element = testElement;
			break;
		}
	}
		
	//get the internal class if required..
	var origElement = element;
	if(element != null && hasInnerClass){
		var temp = classNameOridWithZIndx.split("/");
		for(var j=1;j<temp.length;j++) {
			var innerClassOrId = temp[j];
			if(innerClassOrId.indexOf("*") >=0)
				innerClassOrId = innerClassOrId.replace("*", "");

			var element =  this._getInnerObjFromMainObj(element, innerClassOrId);
			if(element == null)
				break;
		}
	}

	if(element == null && (action == "notexist" || action == "notdisplayed"))
		return true;
	else if(element == null)
		return false;
	else {
		if(useXY != "")
			action = action + "_addXY";

		return	this.actOnZElement(element, action, class2OrId, useXY,xyValue); 
	}
	
}

Selenium.prototype._getInnerObjFromMainObj_html = function(mainObj, reqClassOrIdElement, objNumber) {
		var reqNumber = 1;
		var _counter = 0;
		if(objNumber != undefined && objNumber != "")
			var reqNumber = parseInt(objNumber);

		var els = mainObj.getElementsByTagName("*");
		for (var j = 0; j < els.length; j++) {	
			var testElement = els[j];
			if(testElement.className == reqClassOrIdElement || testElement.id.indexOf(reqClassOrIdElement) >=0) {
				_counter++;
				if(_counter == reqNumber) {
					return testElement;						
				}
			}			
		}
		return null;
}

Selenium.prototype._getInnerObjFromMainObj = function(mainObj, reqClassOrIdElement) {
		//try with div..
		var div1 = mainObj.getElementsByTagName("*");
		var element = null;//reset
		for (var j = 0; j < div1.length; j++) {	
			var testElement = div1[j];
				if((testElement.className.indexOf(reqClassOrIdElement) >=0 || testElement.id.indexOf(reqClassOrIdElement) >=0) &&
					 testElement.style.visibility != "hidden") {
				var element = testElement;
				break;
			}
			
		}
		if(element == null){
			//try using td..
			var td1 = mainObj.getElementsByTagName("TD");
			var element = null;//reset
			for (var j = 0; j < td1.length; j++) {	
				var testElement = td1[j];
				if((testElement.className.indexOf(reqClassOrIdElement) >=0 || testElement.id.indexOf(reqClassOrIdElement) >=0) &&
					 testElement.style.visibility != "hidden"){
					var element = testElement;
					break;
				}			
			}
		}
	return element;
}


Selenium.prototype.findZFolder = function(locator, panel, param1) {
if(locator.indexOf("=")>0) {	
	return (this.browserbot.findElementOrNull(locator));
	}
	var divElements = this.getShellChildNodes();
	if(divElements == null)
		return null;

	if(panel == undefined || panel == "") {
		var check1 = "(parseInt(testElement.style.zIndex) == 300 && (testElement.className.indexOf(\"ZmOverview\") >= 0) &&(testElement.innerHTML.indexOf(locator) >= 0))";
	} else if(panel == "dialog"){
		var check1 =  "(parseInt(testElement.style.zIndex) >= 700 " +
			" && (testElement.className.indexOf(\"Dialog\") >= 0) "+
			" && (testElement.innerHTML.indexOf(locator) >= 0))";
	} else if(panel.indexOf("__dialogByName__")>=0){
		var dlgName = panel.replace("__dialogByName__", ""); 
		var check1 =  "(parseInt(testElement.style.zIndex) >= 700" + 
		" && (testElement.className.indexOf(\"Dialog\") >= 0)" + 
		" && (testElement.innerHTML.indexOf(\""+dlgName+"\") >= 0)" +
		" && (testElement.innerHTML.indexOf(locator) >= 0))";
	} else {
		return null;
	}
	for (var i = 0; i < divElements.length; i++) {
		var testElement = divElements[i];
		if (eval("(" +check1+ ")")) {
			var div1 = testElement.getElementsByTagName("DIV");
			for (var j = 0; j < div1.length; j++) {
				var testElement = div1[j];
				if (testElement.className == "DwtTreeItem" || testElement.className.indexOf("DwtTreeItem ") >= 0 || testElement.className.indexOf("DwtTreeItem-selected") >= 0 
					) {
					if (testElement.textContent)
						var actText = testElement.textContent;
					else if (testElement.innerText)
						var actText = testElement.innerText;					
					if(actText == locator || (actText.indexOf(locator)==0 && actText.indexOf("(")>0))
						return testElement;
				}
			}

		}
	}
	return null;
};

Selenium.prototype._getView = function() {

	var win = this.browserbot.getCurrentWindow();
	var divElements = this.getShellChildNodes();
	if(divElements == null)
		return null;
	for (var i = 0; i < divElements.length; i++) {
		var testElement = divElements[i];
		if (parseInt(testElement.style.zIndex) == 300 && (testElement.className == "ZmConvDoublePaneView")) {
			return testElement;
		}
	}
	return null;
};


Selenium.prototype.verify_msgBdyInHyb = function(msgText)  {
	return this._verifyMsgBody(msgText, "hybrid");
}

Selenium.prototype.verify_msgBdyInConv = function(msgText)  {
	return this._verifyMsgBody(msgText, "conversation");
}

Selenium.prototype._verifyMsgBody = function(msgText, view)  {
	var win = this.browserbot.getCurrentWindow();
	var inDocument = win.document;
	var msgBodyid = "";
	var viewObj  = "";
	if(view == "conversation") {
		viewObj= this.browserbot.findElementOrNull("id=zv|CV");
		msgBodyid ="zv|CV|MSG";
	} else if(view =="hybrid") {
		viewObj= this.browserbot.findElementOrNull("id=zv|CLV");
		msgBodyid = "zv|CLV|MSG";
	} else
		return false;

	//check if the view is displayed
	if(viewObj.style.zIndex <300)
		return false;
	
	//check if the message with the correct text exist
	try {
		var iframeMsgBody = inDocument.getElementById(msgBodyid).getElementsByTagName("iframe");
	} catch(e) {
		return false;
	} 
	if(iframeMsgBody.length == 0)
		return false;

	var iframeHTML = iframeMsgBody[0].contentWindow.document.body.innerHTML;

	if(iframeHTML.indexOf(msgText) >=0) 
		return true;
	else
		return false;
}

Selenium.prototype._getViewRowList = function() {
	var viewDivs = this._getView().getElementsByTagName("DIV");
	for (var i = 0; i < viewDivs.length; i++) {
		var testElement = viewDivs[i];
		if (testElement.className == "DwtListView-Rows") {
			return testElement;
		}
	}
	return null;
};

Selenium.prototype.storeViewHTML = function() {
	this._storedViewHTML = this._getView().innerHTML;
};

Selenium.prototype.appendChildToView = function() {
	var p = this.browserbot.getCurrentWindow().document.createElement("p");
	p.id = "testObjID";
	this._getViewRowList().appendChild(p);
};

Selenium.prototype.verifyViewHasNoChild = function() {
	var inDocument = this.browserbot.getCurrentWindow().document;
	if (inDocument.getElementById("testObjID") != null) {
		LOG.info("verifyViewHasNoChild called, obj exists");
		return false;
	} else {
		LOG.info("verifyViewHasNoChild called, obj NOT exists");
		var v = this._getViewRowList();
		this.appendChildToView();//appends child to convView(just to make sure we wait until the list is displayed)
		return true;
	}
};


Selenium.prototype.verifyNewView = function() {
	return ( this._storedViewHTML != this._getView().innerHTML) ? true : false;
};

Selenium.prototype.findZDialog = function(dialogName, panel, param1) {

if(dialogName.indexOf("=")>0) {	
	return (this.browserbot.findElementOrNull(dialogName));
}

	var win = this.browserbot.getCurrentWindow();
	var t1 = true;
	var divElements = this.getShellChildNodes();
	if(divElements == null)
		return null;
	for (var i = 0; i < divElements.length; i++) {
		var testElement = divElements[i];
		if (parseInt(testElement.style.zIndex) >= 500 && (testElement.className.indexOf("Dialog") >= 0) &&
		    ((dialogName == undefined) || (dialogName == "") || (dialogName != undefined && testElement.innerHTML.indexOf(dialogName) >= 0))) {
			return testElement;

		}
	}
	return null;
};


Selenium.prototype.closeDlgIfExists = function(dlgNameCommaBtnName) {
	//this function should be called using waitDecorator(with some timeout), that way, waitDecorator
	//	keeps calling this function until timout(or this returns true), consequently providing closeDlgIfExists
	var arry = dlgNameCommaBtnName.split(",");
	var buttonName, dialogName;
	(arry[0] != undefined) ? dialogName = arry[0] : dialogName = "";
	(arry[1] != undefined) ? buttonName = arry[1] : buttonName = "";


	if (!this.verifyZDialog(dialogName)) {//make sure dlg exists
		return false;
	} else {
		var element = this.findZButtonInDlg(buttonName, dialogName);
		this.clickZElement(element);
		return true;
	}

}
Selenium.prototype.verifyZDialog = function(dialogName) {
	if (this.findZDialog(dialogName))
		return true;
	else
		return false;
};

Selenium.prototype.verifyZText = function(text) {

	var win = this.browserbot.getCurrentWindow();
	var t1 = true;
	var divElements = this.getShellChildNodes();
	if(divElements == null)
		return null;
	
	for (var i = 0; i < divElements.length; i++) {
		var testElement = divElements[i];
		if (parseInt(testElement.style.zIndex) >= 300 && (testElement.innerHTML.indexOf(text) >= 0)) {
			return true;
		}
	}
	return false;
};
