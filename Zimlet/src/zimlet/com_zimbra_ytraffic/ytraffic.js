/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Zimlets
 * Copyright (C) 2006, 2007, 2009 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.2 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */

//////////////////////////////////////////////////////////////
//  Zimlet to handle integration with a Yahoo! Traffic      //
//  @author Charles Cao                                     //
//////////////////////////////////////////////////////////////

function Com_Zimbra_YTraffic() {
}

Com_Zimbra_YTraffic.prototype = new ZmZimletBase();
Com_Zimbra_YTraffic.prototype.constructor = Com_Zimbra_YTraffic;

Com_Zimbra_YTraffic.prototype.init =
function() {
};

//TODO Y! Traffic Webservice URL
Com_Zimbra_YTraffic.URL = "http://api.local.yahoo.com/MapsService/V1/trafficData?appid=ZimbraTraffic";

//Called by the Zimbra framework when the panel item was double clicked
Com_Zimbra_YTraffic.prototype.doubleClicked = function() {
	var editorProps = [
		{ label 		 : "Street",
		  name           : "street",
		  type           : "string",
		  value			 : "",
		  minLength      : 0,
		  maxLength      : 50
		}, 
		{
			label		: "City",
			name		: "city",
			type		: "string",
			value		: "",
			minLength	: 0,
			maxLength	: 20		
		}, 
		{
			label		: "State",
			name		: "state",
			type		: "string",
			value		: "",
			minLength	: 0,
			maxLength	: 2		
		}, 
		{
			label		: "Zip",
			name		: "zip",
			type		: "string",			
			minLength	: 0,
			maxLength	: 10		
		}
		];
		
	if (!this._dlg_propertyEditor) {
		var view = new DwtComposite(this.getShell());
		this._propertyEditor = new DwtPropertyEditor(view, true);
		var pe = this._propertyEditor;
		pe.initProperties(editorProps);
		var dialog_args = {
			title : "Yahoo Traffic: Enter Address",
			view  : view
		};
		this._dlg_propertyEditor = this._createDialog(dialog_args);
		var dlg = this._dlg_propertyEditor;
		pe.setFixedLabelWidth();
		pe.setFixedFieldWidth();
		dlg.setButtonListener(DwtDialog.OK_BUTTON,
				      new AjxListener(this, function() {
				          if (!pe.validateData()) {return;}
					      this._getDisplayCustomTraffic();
				      }));
	}
	this._dlg_propertyEditor.popup();
};

Com_Zimbra_YTraffic.prototype._getDisplayCustomTraffic =
function() {
	this._dlg_propertyEditor.popdown();
	var addressProps = this._propertyEditor.getProperties();
	this._displayDialogTraffic(addressProps);
	this._dlg_propertyEditor.dispose();
	this._dlg_propertyEditor = null;
};

Com_Zimbra_YTraffic.prototype._displayDialogTraffic = 
function(address) {
	var view = new DwtComposite(this.getShell());
	var dialog_args = {
		view  : view,
		title : "Yahoo Traffic"
	};
	var dlg = this._createDialog(dialog_args);
	dlg.popup();
	dlg.setButtonListener(DwtDialog.OK_BUTTON,
		      new AjxListener(this, function() {
			      dlg.popdown();
			      dlg.dispose();
		      }));
	dlg.setButtonListener(DwtDialog.CANCEL_BUTTON,
		      new AjxListener(this, function() {
			      dlg.popdown();
			      dlg.dispose();
		      }));
    var el = view.getHtmlElement();
    var div = document.createElement("div");
    el.appendChild(div);
    this.toolTipPoppedUp(null, address, null, div);
};


// Content Object Methods

Com_Zimbra_YTraffic.prototype.toolTipPoppedUp =
function(spanElement, obj, context, canvas) {

	var addressURL = "";
	addressURL += obj.street.length > 0 ? "&" + "street=" + AjxStringUtil.urlComponentEncode(obj.street) : "" ;
	addressURL += obj.city.length > 0 ? "&" + "city=" + AjxStringUtil.urlComponentEncode(obj.city) : "" ;
	addressURL += obj.state.length > 0 ? "&" + "state=" + AjxStringUtil.urlComponentEncode(obj.state) : "" ;
	addressURL += obj.zip.length > 0 ? "&" + "zip=" + AjxStringUtil.urlComponentEncode(obj.zip) : "" ;
	canvas.innerHTML = "<div id='" + ZmZimletBase.encodeId("YahooTrafficElement") + "'>Retriving the traffic data ....</div>";
	var url = ZmZimletBase.PROXY + AjxStringUtil.urlComponentEncode(Com_Zimbra_YTraffic.URL + addressURL);
	DBG.println(AjxDebug.DBG2, "Com_Zimbra_YTraffic URL: " + url);
	AjxRpc.invoke(null, url, null, new AjxCallback(this, Com_Zimbra_YTraffic._callback, obj), true);
};

Com_Zimbra_YTraffic._displayTrafficInfo = 
function (trafficInfo, obj){
	var resultEl = document.getElementById(ZmZimletBase.encodeId("YahooTrafficElement"));
	resultEl.innerHTML = trafficInfo ;

};

Com_Zimbra_YTraffic._callback = 
function(obj, results) {

	var xmlDom = results.xml;
		
	if (xmlDom){
		var reportDateElements = xmlDom.getElementsByTagName("ReportDate");
		var unixTime = null;
		var humanTime = null;
		
		if (reportDateElements.length > 0 ) {
			//convert the traffic report date
			for (var i=0; i < reportDateElements.length ; i ++) {
				unixTime = new Date ( reportDateElements[i].textContent * 1000 ) ;
				reportDateElements[i].textContent =  AjxDateUtil.longComputeDateStr(unixTime)+ " " + AjxDateUtil.computeTimeString (unixTime);			
			}
			var rHtml = "";
		
			var xslStr = "";
			xslStr += "<?xml version='1.0' encoding='ISO-8859-1'?> ";
			xslStr += "<xsl:stylesheet version='1.0' xmlns:xsl='http://www.w3.org/1999/XSL/Transform' xmlns:ytraffic='urn:yahoo:maps'>";
			xslStr += "<xsl:template match='/'>" ;
			xslStr += "<table border='1'><tr bgcolor='#9acd32'><th>Report Date</th><th>Event</th><th>Location</th></tr>" ;
			xslStr += "<xsl:for-each select='/ytraffic:ResultSet/ytraffic:Result'>" ;
			xslStr += "<tr><td><xsl:value-of select='ytraffic:ReportDate'/></td>";
			xslStr += "<td><xsl:value-of select='@type'/></td>";
			xslStr += "<td><xsl:value-of select='ytraffic:Title'/></td>";
			xslStr += "</tr></xsl:for-each></table></xsl:template></xsl:stylesheet>";
			
			DBG.println(AjxDebug, xslStr);
			
			var xslt = AjxXslt.createFromString(xslStr);
			rHtml = xslt.transformToString(xmlDom);
		}else{
			rHtml = "<h2>Go home now! No hazard traffic on the road.</h2>";
		}
	}else{
		rHtml = results.text;
	}
	Com_Zimbra_YTraffic._displayTrafficInfo(rHtml, obj);
};
