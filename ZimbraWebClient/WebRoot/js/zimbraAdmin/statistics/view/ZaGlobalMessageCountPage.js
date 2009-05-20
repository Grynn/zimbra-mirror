/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2005, 2006, 2007, 2008 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Yahoo! Public License
 * Version 1.0 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */

/**
* @class ZaGlobalMessageCountPage 
* @contructor ZaGlobalMessageCountPage
* @param parent
* @param app
* @author Greg Solovyev
**/
ZaGlobalMessageCountPage = function(parent) {
	DwtTabViewPage.call(this, parent);
	this._fieldIds = new Object(); //stores the ids of all the form elements
	this._app = ZaApp.getInstance();
	//this._createHTML();
	this.initialized=false;
	this.setScrollStyle(DwtControl.SCROLL);	
}
 
ZaGlobalMessageCountPage.prototype = new DwtTabViewPage;
ZaGlobalMessageCountPage.prototype.constructor = ZaGlobalMessageCountPage;

ZaGlobalMessageCountPage.prototype.toString = 
function() {
	return "ZaGlobalMessageCountPage";
}

ZaGlobalMessageCountPage.prototype.showMe =  function(refresh) {
	DwtTabViewPage.prototype.showMe.call(this);	
	if(refresh) {
		this.setObject();
	}
}

ZaGlobalMessageCountPage.getDataTipText = function (item, index, series) {
    var text = series.displayName + " at " + YAHOO.util.Date.format(item.timestamp, { format: "%I:%M" }) + "\n" + ZaGlobalMessageCountPage.formatLabel(item[series.yField]);
    return text;
}
/* must be global for getDataTipText */
ZaGlobalMessageCountPage.formatLabel = function (value) {
    return YAHOO.util.Number.format(value, { thousandsSeparator: ",", decimalPlaces: 0});
}
ZaGlobalMessageCountPage.formatTimeLabel = function (value) {
    return YAHOO.util.Date.format(value, { format: "%I:%M %p" });
}

ZaGlobalMessageCountPage.plotChart = function (fields, colDef, newData) {
    var yAxis = new YAHOO.widget.NumericAxis();
    var max = 0;
    for (var i = 0; i < colDef.length; i++) {
        colDef[i].style = { size: 3, lineSize: 1 };
    }
    for (var i = 0; i < newData.length; i++) {
        for (var j = 0; j < colDef.length; j++) {
            max = Math.max(max, newData[i][colDef[j].yField]);
        }
    }
    //yAxis.scale = "logarithmic";
    yAxis.maximum = max + 10;
    yAxis.labelFunction = ZaGlobalMessageCountPage.formatLabel;
    var timeAxis = new YAHOO.widget.TimeAxis();
    timeAxis.labelFunction = ZaGlobalMessageCountPage.formatTimeLabel;
    var seriesDef = colDef;
    
    var data_source = new YAHOO.util.DataSource(newData);
    ZaGlobalMessageCountPage.CHART_DATA_SOURCE = data_source;
    data_source.responseType = YAHOO.util.DataSource.TYPE_JSARRAY;
    data_source.responseSchema = { fields: fields };
    new YAHOO.widget.LineChart("loggerchart", data_source,
            { xField: "timestamp",
              wmode: "transparent",
              series: seriesDef,
              yAxis: yAxis,
              xAxis: timeAxis,
              dataTipFunction: ZaGlobalMessageCountPage.getDataTipText,
              style: { legend: { display: "bottom" } }
            }
    );
    
}

ZaGlobalMessageCountPage.prototype.setObject =
function (data) {
    var serversSelect = document.getElementById("select-servers");
    var soapRequest = AjxSoapDoc.create("GetLoggerStatsRequest", ZaZimbraAdmin.URN, null);
    var csfeParams = { soapDoc: soapRequest };
    var reqMgrParams = { controller: ZaApp.getInstance().getCurrentController(), busyMsg: ZaMsg.PQ_LOADING };
    var soapResponse = ZaRequestMgr.invoke(csfeParams, reqMgrParams).Body.GetLoggerStatsResponse;
    ZaGlobalMessageCountPage.clearSelect(serversSelect);
    for (var i = 0, j = soapResponse.hostname.length; i < j; i++) {
        var option = document.createElement("option");
        if (i == 0) option.selected = "selected";
        option.value = soapResponse.hostname[i].hn;
        option.textContent = soapResponse.hostname[i].hn;
        serversSelect.appendChild(option);
    }
    ZaGlobalMessageCountPage.serverSelected({ target: serversSelect });
}

ZaGlobalMessageCountPage.serverSelected = function(evt) {
    var select = evt.target;
    
    var hostname = select[select.selectedIndex].value;
    
    var soapRequest = AjxSoapDoc.create("GetLoggerStatsRequest", ZaZimbraAdmin.URN, null);
    soapRequest.set("hostname", { "!hn": hostname });
    var csfeParams = { soapDoc: soapRequest };
    var reqMgrParams = { controller: ZaApp.getInstance().getCurrentController(), busyMsg: ZaMsg.PQ_LOADING };
    var soapResponse = ZaRequestMgr.invoke(csfeParams, reqMgrParams).Body.GetLoggerStatsResponse;
    
    var groupSelect = document.getElementById("select-group");
    var statGroups = soapResponse.hostname[0].stats;
    ZaGlobalMessageCountPage.clearSelect(groupSelect);
    for (var i = 0, j = statGroups.length; i < j; i++) {
        var option = document.createElement("option");
        if (i == 0) option.selected = "selected";
        option.value = statGroups[i].name;
        option.textContent = statGroups[i].name;
        groupSelect.appendChild(option);
    }
    ZaGlobalMessageCountPage.groupSelected({ target: groupSelect });
    
}

ZaGlobalMessageCountPage.clearSelect = function (node) {
    var options = node.getElementsByTagName("option");
    for (var i = node.childNodes.length; i > 0; i--)
        node.removeChild(node.childNodes.item(i - 1));
}
ZaGlobalMessageCountPage.groupSelected = function(evt) {
    var select = evt.target;
    
    var serverSelect = document.getElementById("select-servers");
    var hostname = serverSelect[serverSelect.selectedIndex].value;
    var group = select[select.selectedIndex].value;
    
    var soapRequest = AjxSoapDoc.create("GetLoggerStatsRequest", ZaZimbraAdmin.URN, null);
    soapRequest.set("hostname", { "!hn": hostname });
    var child = soapRequest.set("stats", { "!name" : group });
    soapRequest.set(null, "get-counters", child);
    var csfeParams = { soapDoc: soapRequest };
    var reqMgrParams = { controller: ZaApp.getInstance().getCurrentController(), busyMsg: ZaMsg.PQ_LOADING };
    var soapResponse = ZaRequestMgr.invoke(csfeParams, reqMgrParams).Body.GetLoggerStatsResponse;
    
    var counterSelect = document.getElementById("select-counter");
    var statCounters = soapResponse.hostname[0].stats[0].values[0].stat;
    ZaGlobalMessageCountPage.clearSelect(counterSelect);
    for (var i = 0, j = statCounters.length; i < j; i++) {
        var option = document.createElement("option");
        if (i == 0) option.selected = "selected";
        option.value = statCounters[i].name;
        option.textContent = statCounters[i].name;
        counterSelect.appendChild(option);
    }
}

ZaGlobalMessageCountPage.groupSelected = function(evt) {
    var select = evt.target;
    
    var serverSelect = document.getElementById("select-servers");
    var hostname = serverSelect[serverSelect.selectedIndex].value;
    var group = select[select.selectedIndex].value;
    
    var soapRequest = AjxSoapDoc.create("GetLoggerStatsRequest", ZaZimbraAdmin.URN, null);
    soapRequest.set("hostname", { "!hn": hostname });
    var child = soapRequest.set("stats", { "!name" : group });
    soapRequest.set(null, "123", child);
    var csfeParams = { soapDoc: soapRequest };
    var reqMgrParams = { controller: ZaApp.getInstance().getCurrentController(), busyMsg: ZaMsg.PQ_LOADING };
    var soapResponse = ZaRequestMgr.invoke(csfeParams, reqMgrParams).Body.GetLoggerStatsResponse;
    
    var counterSelect = document.getElementById("select-counter");
    var statCounters = soapResponse.hostname[0].stats[0].values[0].stat;
    ZaGlobalMessageCountPage.clearSelect(counterSelect);
    for (var i = 0, j = statCounters.length; i < j; i++) {
        var option = document.createElement("option");
        if (i == 0) option.selected = "selected";
        option.value = statCounters[i].name;
        option.textContent = statCounters[i].name;
        counterSelect.appendChild(option);
    }
}

ZaGlobalMessageCountPage.counterSelected = function(event) {
    var select = event.target;
    
    var serverSelect = document.getElementById("select-servers");
    var hostname = serverSelect[serverSelect.selectedIndex].value;
    var groupSelect = document.getElementById("select-group");
    var group = groupSelect[groupSelect.selectedIndex].value;
    
    var selected = [];
    var index = 0;
    for (var i = 0; i < select.options.length; i++) {
        if (select.options[i].selected)
            selected[index++] = select.options[i].value;
    }
    if (selected.length == 0)
        return;
    
    var soapRequest = AjxSoapDoc.create("GetLoggerStatsRequest", ZaZimbraAdmin.URN, null);
    soapRequest.set("hostname", { "!hn": hostname });
    var child = soapRequest.set("stats", { "!name" : group });
    var csfeParams = { soapDoc: soapRequest };
    var reqMgrParams = { controller: ZaApp.getInstance().getCurrentController(), busyMsg: ZaMsg.PQ_LOADING };
    var soapResponse = ZaRequestMgr.invoke(csfeParams, reqMgrParams).Body.GetLoggerStatsResponse;
    
    var values = soapResponse.hostname[0].stats[0].values;
    
    var newData = [];
    
    for (var i = 0; i < values.length; i++) {
        var ts = new Date(values[i].t * 1000);
        var record = { timestamp: ts };
        for (var j = 0; j < values[i].stat.length; j++) {
            if (selected.indexOf(values[i].stat[j].name) != -1) {
                record[values[i].stat[j].name] = values[i].stat[j].value;
            }
        }
        // skip missing values, we can't assume it's a zero or last value
        var skipRec = false;
        for (var j = 0; j < selected.length; j++) {
            if (!record[selected[j]]) {
                //record[selected[j]] = 0;
                skipRec = true;
                break;
            }
        }
        if (!skipRec) {
            newData.push(record);
        }
    }
    var colDef = [];
    for (var i = 0; i < selected.length; i++) {
        colDef.push({ displayName: selected[i], yField: selected[i] });
    }
    var fields = [ "timestamp" ];
    for (var i = 0; i < selected.length; i++) {
        fields.push(selected[i]);
    }

    //document.getElementById("for-testing").textContent = newData.length + " :: " + AjxStringUtil.objToString(newData);
    ZaGlobalMessageCountPage.plotChart(fields, colDef, newData);
}

ZaGlobalMessageCountPage.prototype._createHtml = 
function () {
	DwtTabViewPage.prototype._createHtml.call(this);
	var html = "";
	html += "<form onsubmit=\"return false;\" action=\"#\">";
	html += "<table>";
	html += "<tr>";
	html += "<td valign=\"top\"><label for=\"select-servers\">Server:</label><select id=\"select-servers\" name=\"servers\" onchange=\"ZaGlobalMessageCountPage.serverSelected(event);\"></select></td>";
	html += "<td valign=\"top\"><label for=\"select-group\">Groups:</label><select id=\"select-group\" onchange=\"ZaGlobalMessageCountPage.groupSelected(event);\" name=\"groups\"></select></td>";
	html += "<td valign=\"top\"><label for=\"select-counter\">Counters:</label><select id=\"select-counter\" multiple=\"multiple\" onchange=\"ZaGlobalMessageCountPage.counterSelected(event);\" size=\"5\" name=\"counters\"><option value=\"foo\">foo</option></select></td>";
	html += "</tr>";
	html += "</table>";
	html += "<div style=\"padding: 20px;\" id=\"loggerchart\"></div>";
	//html += "<p id=\"for-testing\"></p>";
	html += "</form>";
	this.getHtmlElement().innerHTML = html;
}