/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2009, 2010, 2011, 2012 VMware, Inc.
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

/**
* @class ZaGlobalAdvancedStatsPage 
* @contructor ZaGlobalAdvancedStatsPage
* @param parent
* @param app
* @author Perry Nguyen
**/
ZaGlobalAdvancedStatsPage = function(parent) {
	DwtTabViewPage.call(this, parent);
	this._fieldIds = new Object(); //stores the ids of all the form elements
	this._app = ZaApp.getInstance();
	//this._createHTML();
	this.initialized=false;
	this.setScrollStyle(DwtControl.SCROLL);	
}
 
ZaGlobalAdvancedStatsPage.prototype = new DwtTabViewPage;
ZaGlobalAdvancedStatsPage.prototype.constructor = ZaGlobalAdvancedStatsPage;

ZaGlobalAdvancedStatsPage.prototype.toString = 
function() {
	return "ZaGlobalAdvancedStatsPage";
}

ZaGlobalAdvancedStatsPage.prototype.showMe =  function(refresh) {
    this.setZIndex(DwtTabView.Z_ACTIVE_TAB);
	if (this.parent.getHtmlElement().offsetHeight > 26) { 						// if parent visible, use offsetHeight
		this._contentEl.style.height=this.parent.getHtmlElement().offsetHeight-26;
	} else {
		var parentHeight = parseInt(this.parent.getHtmlElement().style.height);	// if parent not visible, resize page to fit parent
		var units = AjxStringUtil.getUnitsFromSizeString(this.parent.getHtmlElement().style.height);
		if (parentHeight > 26) {
			this._contentEl.style.height = (Number(parentHeight-26).toString() + units);
		}
	}
	this._contentEl.style.width = this.parent.getHtmlElement().style.width;	// resize page to fit parent

	if(refresh) {
		this.setObject();
	}
	if (!this._chartHtmlShown) {
        ZaGlobalAdvancedStatsPage.insertChartHTML(this.getHtmlElement());
        this._chartHtmlShown = true;
	}

    var controller = ZaApp.getInstance().getCurrentController();
    controller._helpURL = location.pathname + ZaUtil.HELP_URL + "monitoring/creating_advanced_server_statistics.htm?locid="+AjxEnv.DEFAULT_LOCALE;
    controller._helpButtonText = ZaMsg.helpCreateAdvServerStat;
}

ZaGlobalAdvancedStatsPage.prototype.hideMe =
function (){
	DwtTabViewPage.prototype.hideMe.call(this);
	var controller = ZaApp.getInstance().getCurrentController();
    controller._helpURL = location.pathname + ZaUtil.HELP_URL + "monitoring/checking_usage_statistics.htm?locid="+AjxEnv.DEFAULT_LOCALE;
    controller._helpButtonText = ZaMsg.helpCheckStatistics;
};


ZaGlobalAdvancedStatsPage.getDataTipText = function (item, index, series) {
    var text = AjxMessageFormat.format(ZaMsg.NAD_AdvStatsDataTip,
             [ series.displayName, YAHOO.util.Date.format(item.timestamp, { format: ZaMsg.NAD_AdvStatsTipDateFormat }) ]) +
             "\n" +
             ZaGlobalAdvancedStatsPage.formatLabel(item[series.yField]);
    return text;
}
/* must be global for getDataTipText */
ZaGlobalAdvancedStatsPage.formatLabel = function (value) {
    return YAHOO.util.Number.format(value, { thousandsSeparator: ",", decimalPlaces: 0});
}
ZaGlobalAdvancedStatsPage.formatTimeLabel = function (value) {
    return YAHOO.util.Date.format(value, { format: ZaMsg.NAD_AdvStatsLabelDateFormat });
}

ZaGlobalAdvancedStatsPage.getTarget = function (evt) {
    if (evt && evt.target)
        return evt.target;
    else
        return window.event.srcElement;
}

ZaGlobalAdvancedStatsPage.indexOf = function (ary, item) {
    if (Array.indexOf)
        return ary.indexOf(item);
    else {
        for (var i=0; i < ary.length; i++) {
            if (ary[i] == item){
                return i;
            }
        }
        return -1;
    }
}

ZaGlobalAdvancedStatsPage.detectFlash = function(e) {
    var hasFlash = false;
    var version = YAHOO.deconcept.SWFObjectUtil.getPlayerVersion();
    
    if (version.major == 9 && version.minor == 0 && version.rev >= 45)
        hasFlash = true;
    else if (version.major == 9 && version.minor > 0)
        hasFlash = true;
    else if (version.major > 9)
        hasFlash = true;
    if (!hasFlash) {
        e.style.display = "block";
        var msg = ZaMsg.NAD_AdvStatsFlashRequired;
        if (version.major > 0)
            msg = AjxMessageFormat.format(ZaMsg.NAD_AdvStatsFlashOld, [ version.major, version.minor, version.rev ]);
            
        for (var i = e.childNodes.length; i > 0; i--)
            e.removeChild(e.childNodes.item(i - 1));
        
        var a = document.createElement("a");
        ZaGlobalAdvancedStatsPage.setText(a, msg);
        a.href = "http://get.adobe.com/flashplayer";
        
        e.appendChild(a);
    }
}

ZaGlobalAdvancedStatsPage.setText = function (e, text) {
    var hasInnerText = (document.getElementsByTagName("body")[0].innerText != undefined);

    if (hasInnerText) {
        e.innerText = text;
    } else {
        e.textContent = text;
    }
}
ZaGlobalAdvancedStatsPage.plotGlobalQuickChart = function (id, group, columns, column_units, start, end, options) {
    var chartdiv = document.getElementById("loggerchart" + id);
    ZaGlobalAdvancedStatsPage.setText(chartdiv, ZaMsg.NAD_AdvStatsLoadingDataLabel);
    
    var soapRequest = AjxSoapDoc.create("GetLoggerStatsRequest", ZaZimbraAdmin.URN, null);
    soapRequest.set("startTime", { "!time": start });
    soapRequest.set("endTime", { "!time": end });
    
    var counters = [];
    for (var i = 0; i < columns.length; i++) {
        counters.push({ "!name" : columns[i] });
    }
    var statsdata = { "!name" : group, "!limit" : "true" };
    if (counters.length > 0) {
        statsdata.values = { "stat" : [] };
        for (var i = 0; i < counters.length; i++) {
            statsdata.values.stat.push(counters[i]);
        }
    }
    soapRequest.set("stats", statsdata);
    
    var cb = function(response) {
    	if(response.isException && response.isException()) {
    		ZaApp.getInstance().getCurrentController()._handleException(response.getException(),"ZaGlobalAdvancedStatsPage.plotGlobalQuickChart", null, false);
    		return;
    	}
        var soapResponse = response.getResponse().Body.GetLoggerStatsResponse;
        if (!soapResponse.hostname || !soapResponse.hostname[0].stats) {
            var e = document.getElementById("loggerchart" + id);
            ZaGlobalAdvancedStatsPage.setText(e, ZaMsg.NAD_AdvStatsNoDataLabel);
            return;
        }
        var data = {};
        if (soapResponse.hostname) {
            for (var i = 0; i < soapResponse.hostname.length; i++) {
                if (!soapResponse.hostname[i].stats) continue;
                var stats = soapResponse.hostname[i].stats;
                if (!stats[0].values) continue;
                for (var j = 0; j < stats[0].values.length; j++) {
                    var setOrIncrement = false;
                    if (!data[stats[0].values[j].t]) {
                        data[stats[0].values[j].t] = {};
                        setOrIncrement = true;
                    }
                    if (!stats[0].values[j].stat) continue;
                    for (var m = 0; m < stats[0].values[j].stat.length; m++) {
                        if (setOrIncrement)
                            data[stats[0].values[j].t][stats[0].values[j].stat[m].name] = stats[0].values[j].stat[m].value;
                        else
                            data[stats[0].values[j].t][stats[0].values[j].stat[m].name] += stats[0].values[j].stat[m].value;
                    }
                }
            }
        }
        
        var newData = [];
        var period = 0;
        var lastTS = 0;
        for (var i in data) {
            if (lastTS != 0)
                period = i - lastTS;
            lastTS = i;
                
            record = { timestamp: new Date(i * 1000) };
            for (var j in data[i]) {
                record[j] = data[i][j];
            }
            // automatically assume missing values are 0 (this can suck at times...)
            for (var j = 0; j < columns.length; j++) {
                if (!record[columns[j]]) {
                    record[columns[j]] = 0;
                }
                if (options && options.convertToCount)
                    record[columns[j]] = record[columns[j]] * period;
            }
            newData.push(record);
        }
        if (newData.length < 1) {
            var e = document.getElementById("loggerchart" + id);
            ZaGlobalAdvancedStatsPage.setText(e, ZaMsg.NAD_AdvStatsNoDataLabel);
            return;
        }
    
        var colDef = [];
        for (var i = 0; i < columns.length; i++) {
            var legend = columns[i];
            if (column_units != null) {
                legend = legend + " (" + column_units[column_units.length == 1 ? 0 : i] + ")";
            }
            colDef.push({ displayName: legend, yField: columns[i] });
        }
        var fields = [ "timestamp" ];
        for (var i = 0; i < columns.length; i++) {
            fields.push(columns[i]);
        }
    
        ZaGlobalAdvancedStatsPage.plotChart(id, fields, colDef, newData);
    };
    
    var csfeParams = { soapDoc: soapRequest, asyncMode: true, 'callback': new AjxCallback(cb) };
    var reqMgrParams = { controller: ZaApp.getInstance().getCurrentController() };
    ZaRequestMgr.invoke(csfeParams, reqMgrParams);
}

ZaGlobalAdvancedStatsPage.plotQuickChart = function (id, hostname, group, columns, column_units, start, end, options) {
    var chartdiv = document.getElementById("loggerchart" + id);
    chartdiv.style.display = "block";
    ZaGlobalAdvancedStatsPage.setText(chartdiv, ZaMsg.NAD_AdvStatsLoadingDataLabel);
    
    var soapRequest = AjxSoapDoc.create("GetLoggerStatsRequest", ZaZimbraAdmin.URN, null);
    soapRequest.set("hostname", { "!hn": hostname });
    soapRequest.set("startTime", { "!time": start });
    soapRequest.set("endTime", { "!time": end });
    
    var counters = [];
    for (var i = 0; i < columns.length; i++) {
        counters.push({ "!name" : columns[i] });
    }
    var statsdata = { "!name" : group, "!limit" : "true" };
    if (counters.length > 0) {
        statsdata.values = { "stat" : [] };
        for (var i = 0; i < counters.length; i++) {
            statsdata.values.stat.push(counters[i]);
        }
    }
    soapRequest.set("stats", statsdata);
    
    var cb = function(response) {
    	if(response.isException && response.isException()) {
    		ZaApp.getInstance().getCurrentController()._handleException(response.getException(),"ZaGlobalAdvancedStatsPage.plotGlobalQuickChart", null, false);
    		return;
    	}
        var soapResponse = response.getResponse().Body.GetLoggerStatsResponse;
        
        if (!soapResponse.hostname || !soapResponse.hostname[0].stats) {
            var e = document.getElementById("loggerchart" + id);
            ZaGlobalAdvancedStatsPage.setText(e, ZaMsg.NAD_AdvStatsNoDataLabel);
            return;
        }
        var values = soapResponse.hostname[0].stats[0].values;
        if (!values) {
            var e = document.getElementById("loggerchart" + id);
            ZaGlobalAdvancedStatsPage.setText(e, ZaMsg.NAD_AdvStatsNoDataLabel);
            return;
        }
        
        var newData = [];
        var period = 0;
        var fixedColumns = new Array();
        for (var i = 0; i < columns.length; i++) {
            fixedColumns.push(columns[i].replace(/[/%:]/g, "_"));
        }
        for (var i = 0; i < values.length; i++) {
           if (i + 1 < values.length)
                period = values[i + 1].t - values[i].t;
            
                
            var ts = new Date(values[i].t * 1000);
            var record = { timestamp: ts };
            for (var j = 0; j < values[i].stat.length; j++) {
                if (ZaGlobalAdvancedStatsPage.indexOf(columns, values[i].stat[j].name) != -1) {
                    //record[values[i].stat[j].name] = values[i].stat[j].value;
                    record[values[i].stat[j].name.replace(/[/%:]/g, "_")] = values[i].stat[j].value;
                }
            }
            for (var j = 0; j < fixedColumns.length; j++) {
                if (!record[fixedColumns[j]]) {
                    record[fixedColumns[j]] = 0;
                }
                if (options && options.convertToCount)
                    record[fixedColumns[j]] = record[fixedColumns[j]] * period;
            }
            newData.push(record);
        }
        if (newData.length < 1) {
            var e = document.getElementById("loggerchart" + id);
            ZaGlobalAdvancedStatsPage.setText(e, ZaMsg.NAD_AdvStatsNoDataLabel);
            return;
        }
        var colDef = [];
        for (var i = 0; i < columns.length; i++) {
            var legend = columns[i];
            if (column_units != null) {
                if ((column_units.length == 1 && column_units[0] != null) || (column_units.length > 1 && column_units[i] != null))
                    legend = legend + " (" + column_units[column_units.length == 1 ? 0 : i] + ")";
            }
            //colDef.push({ displayName: legend, yField: columns[i] });
            colDef.push({ displayName: legend, yField: fixedColumns[i] });
        }
        var fields = [ "timestamp" ];
        for (var i = 0; i < fixedColumns.length; i++) {
            fields.push(fixedColumns[i]);
        }
    
        ZaGlobalAdvancedStatsPage.plotChart(id, fields, colDef, newData);
    };
    
    var csfeParams = { soapDoc: soapRequest, asyncMode: true, callback: new AjxCallback(cb) };
    var reqMgrParams = { controller: ZaApp.getInstance().getCurrentController(), busyMsg: ZaMsg.PQ_LOADING };
    ZaRequestMgr.invoke(csfeParams, reqMgrParams);
}


ZaGlobalAdvancedStatsPage.plotChart = function (id, fields, colDef, newData) {
    var yAxis = new YAHOO.widget.NumericAxis();
    var max = 0;
    for (var i = 0; i < colDef.length; i++) {
        colDef[i].style = { size: 4, lineSize: 1 };
    }
    for (var i = 0; i < newData.length; i++) {
        for (var j = 0; j < colDef.length; j++) {
            max = Math.max(max, newData[i][colDef[j].yField]);
        }
    }
    // doesn't work right in 2.7.0
    //yAxis.scale = "logarithmic";
    yAxis.maximum = max + 10;
    yAxis.minimum = 0;
    yAxis.labelFunction = ZaGlobalAdvancedStatsPage.formatLabel;
    var timeAxis = new YAHOO.widget.TimeAxis();
    
    if (newData.length > 1) {
        var dx = newData[1].timestamp.getTime() - newData[0].timestamp.getTime();
        dx = dx / 1000 / 60;
        timeAxis.title = AjxMessageFormat.format(ZaMsg.NAD_AdvStatsIntervalFormatMinutes, [ dx ]);
    }
    timeAxis.labelFunction = function (value) {
        var ts0 = newData[0].timestamp.getTime();
        var ts1 = newData[newData.length - 1].timestamp.getTime();
        var delta = (ts1 - ts0) / 1000;

        var formatter;
        if (delta > 2 * 24 * 60 * 60) { //2 days
            formatter = AjxDateFormat.getDateInstance(AjxDateFormat.SHORT);

        } else {

            formatter = AjxDateFormat.getTimeInstance(AjxDateFormat.SHORT); 
        }
        return formatter.format(value);
    }
    
    timeAxis.maximum = newData[newData.length - 1].timestamp;
    timeAxis.minimum = newData[0].timestamp;
    var seriesDef = colDef;
    var data_source = new YAHOO.util.DataSource(newData);
    ZaGlobalAdvancedStatsPage.CHART_DATA_SOURCE = data_source;
    data_source.responseType = YAHOO.util.DataSource.TYPE_JSARRAY;
    data_source.responseSchema = { fields: fields };
    var div = document.getElementById("loggerchart" + id);
    div.style.height = "200px";
    new YAHOO.widget.LineChart("loggerchart" + id, data_source,
            { xField: "timestamp",
              wmode: "transparent",
              series: seriesDef,
              yAxis: yAxis,
              xAxis: timeAxis,
              dataTipFunction: ZaGlobalAdvancedStatsPage.getDataTipText,
              style: { legend: { display: "bottom" } }
            }
    );
    
}

ZaGlobalAdvancedStatsPage.prototype.setObject =
function (data) {
    // no-op
}

ZaGlobalAdvancedStatsPage.serverSelected = function(evt, id) {
    var select = ZaGlobalAdvancedStatsPage.getTarget(evt);
    
    var hostname = select[select.selectedIndex].value;
    
    var soapRequest = AjxSoapDoc.create("GetLoggerStatsRequest", ZaZimbraAdmin.URN, null);
    soapRequest.set("hostname", { "!hn": hostname });
    
    var cb = function(response) {
        var soapResponse = response.getResponse().Body.GetLoggerStatsResponse;
        var groupSelect = document.getElementById("select-group" + id);
        var statGroups = soapResponse.hostname[0].stats;
        ZaGlobalAdvancedStatsPage.clearSelect(groupSelect);
        for (var i = 0, j = statGroups.length; i < j; i++) {
            var option = document.createElement("option");
            if (i == 0) option.selected = "selected";
            option.value = statGroups[i].name;
            ZaGlobalAdvancedStatsPage.setText(option, statGroups[i].name);
            groupSelect.appendChild(option);
        }
        ZaGlobalAdvancedStatsPage.groupSelected({ target: groupSelect }, id);
    };
    
    var csfeParams = { soapDoc: soapRequest, asyncMode: true, callback: new AjxCallback(cb) };
    var reqMgrParams = { controller: ZaApp.getInstance().getCurrentController(), busyMsg: ZaMsg.PQ_LOADING };
    ZaRequestMgr.invoke(csfeParams, reqMgrParams);
    
    
}

ZaGlobalAdvancedStatsPage.clearSelect = function (node) {
    var options = node.getElementsByTagName("option");
    for (var i = node.childNodes.length; i > 0; i--)
        node.removeChild(node.childNodes.item(i - 1));
}
ZaGlobalAdvancedStatsPage.groupSelected = function(evt, id) {
    var select = ZaGlobalAdvancedStatsPage.getTarget(evt);
    
    var serverSelect = document.getElementById("select-servers" + id);
    var hostname = serverSelect[serverSelect.selectedIndex].value;
    var group = select[select.selectedIndex].value;
    
    var counterSelect = document.getElementById("select-counter" + id);
    ZaGlobalAdvancedStatsPage.clearSelect(counterSelect);
    ZaGlobalAdvancedStatsPage._getCounters(hostname, group, counterSelect);
    
}

ZaGlobalAdvancedStatsPage._getCounters = function(hostname, group, counterSelect) {
    var soapRequest = AjxSoapDoc.create("GetLoggerStatsRequest", ZaZimbraAdmin.URN, null);
    soapRequest.set("hostname", { "!hn": hostname });
    var child = soapRequest.set("stats", { "!name" : group });
    soapRequest.set(null, "get-counters", child);
    var cb = function(response) {
        var soapResponse = response.getResponse().Body.GetLoggerStatsResponse;
        var statCounters = soapResponse.hostname[0].stats[0].values[0].stat;
        if (statCounters) {
            for (var i = 0, j = statCounters.length; i < j; i++) {
                var option = document.createElement("option");
                option.value = statCounters[i].name;
                ZaGlobalAdvancedStatsPage.setText(option, statCounters[i].name);
                if (statCounters[i].type) {
                    option.columnUnit = statCounters[i].type;
                }
                counterSelect.appendChild(option);
            }
        }
    };
    
    var csfeParams = { soapDoc: soapRequest, asyncMode: true, callback: new AjxCallback(cb) };
    var reqMgrParams = { controller: ZaApp.getInstance().getCurrentController(), busyMsg: ZaMsg.PQ_LOADING };
    ZaRequestMgr.invoke(csfeParams, reqMgrParams);
    
}

ZaGlobalAdvancedStatsPage.getMTAHosts = function() {
    if (ZaGlobalAdvancedStatsPage.MTA_HOSTS) {
        return ZaGlobalAdvancedStatsPage.MTA_HOSTS;
    }
	
    var hosts = new Array();
    var serverList = ZaApp.getInstance().getServerList();
    if (serverList) {
        var ary = serverList.getArray();
        var cnt = ary.length;
        for (var i = 0; i < cnt; i++) {
            var crtServer = ary[i];
            var isInstalled = false;
            var isEnabled = false;
            if(crtServer.attrs){
		// EnabledServiceList InstalledServiceList are array-like object.
		// contain the service installed or enabled
                var EnabledServiceList = crtServer.attrs[ZaServer.A_zimbraServiceEnabled];
                var InstalledServiceList = crtServer.attrs[ZaServer.A_zimbraServiceEnabled];
                
		var j;
                for(j in EnabledServiceList){
                        if(EnabledServiceList[j] == "mta"){
                                isEnabled = true;
                                break;
                        }
                }
		
                for(j in InstalledServiceList){
                        if(InstalledServiceList[j] == "mta"){
                                isInstalled = true;
                                break;
                        }
                }
            }
	    
            if (isInstalled && isEnabled)
                hosts.push(ary[i].name);
        }
    }	
    
    ZaGlobalAdvancedStatsPage.MTA_HOSTS = hosts;
    return ZaGlobalAdvancedStatsPage.MTA_HOSTS;
}

ZaGlobalAdvancedStatsPage.hideDIVs = function(divs) {
    if (divs && divs instanceof Array) {
        var cnt = divs.length;
        for (var i = 0; i < cnt; i++) {
            var chartdiv = document.getElementById("loggerchart" + divs[i]);
            if (chartdiv != null) {
                chartdiv.style.display = "none";
            }
        }
    }
}

ZaGlobalAdvancedStatsPage.getCounters = function(hostname, group) {
    var soapRequest = AjxSoapDoc.create("GetLoggerStatsRequest", ZaZimbraAdmin.URN, null);
    soapRequest.set("hostname", { "!hn": hostname });
    var child = soapRequest.set("stats", { "!name" : group });
    soapRequest.set(null, "get-counters", child);
    
    var csfeParams = { soapDoc: soapRequest };
    var reqMgrParams = { controller: ZaApp.getInstance().getCurrentController(), busyMsg: ZaMsg.PQ_LOADING };
    var soapResponse = ZaRequestMgr.invoke(csfeParams, reqMgrParams).Body.GetLoggerStatsResponse;
    if (soapResponse && soapResponse.hostname && soapResponse.hostname[0] &&
        soapResponse.hostname[0].stats && soapResponse.hostname[0].stats[0] &&
        soapResponse.hostname[0].stats[0].values && soapResponse.hostname[0].stats[0].values[0] &&
        soapResponse.hostname[0].stats[0].values[0].stat
        ) {
        var statCounters = soapResponse.hostname[0].stats[0].values[0].stat;
        var counters = [];
        if ( statCounters ) {
            for (var i = 0, j = statCounters.length; i < j; i++) {
                counters.push(statCounters[i].name);
            }
        }
        return counters;
    }

    return 0;
}

ZaGlobalAdvancedStatsPage.counterSelected = function(evt, id) {
    var select = ZaGlobalAdvancedStatsPage.getTarget(evt);
    if (AjxUtil.isEmpty(select)) {
        return;
	}
    
    var chartdiv = document.getElementById("loggerchart" + id);
    ZaGlobalAdvancedStatsPage.setText(chartdiv, ZaMsg.NAD_AdvStatsLoadingDataLabel);
    
    var serverSelect = document.getElementById("select-servers" + id);
    if (AjxUtil.isEmpty(serverSelect) ||
        AjxUtil.isEmpty(serverSelect.selectedIndex) ||
        serverSelect.selectedIndex < 0 ||
        !serverSelect[serverSelect.selectedIndex] ||
        !serverSelect[serverSelect.selectedIndex].value) {
        return;
    }

    var hostname = serverSelect[serverSelect.selectedIndex].value;

    var groupSelect = document.getElementById("select-group" + id);
    if (AjxUtil.isEmpty(groupSelect) ||
        AjxUtil.isEmpty(groupSelect.selectedIndex) ||
        groupSelect.selectedIndex < 0 ||
        !groupSelect[groupSelect.selectedIndex] ||
        !groupSelect[groupSelect.selectedIndex].value)
        return;

    var group = groupSelect[groupSelect.selectedIndex].value;
    
    var selected = [];
    var units = [];
    var index = 0;
    for (var i = 0; i < select.options.length; i++) {
        if (select.options[i].selected) {
            selected[index] = select.options[i].value;
            units[index] = select.options[i].columnUnit ? select.options[i].columnUnit : null;
            index++;
        }
    }
    if (selected.length == 0)
        return;
    
    var startTime = document.getElementById("input-start-time" + id).value;
    var endTime = document.getElementById("input-end-time" + id).value;
    ZaGlobalAdvancedStatsPage.plotQuickChart(id, hostname, group, selected, units, startTime, endTime);
}

ZaGlobalAdvancedStatsPage.showhide = function(id) {
    var e = document.getElementById(id);
    e.style.display = (e.style.display == "none") ? "" : "none";  //show it as the original
    return e.style.display != "none";
}

ZaGlobalAdvancedStatsPage.removeChild = function(id) {
    var e = document.getElementById(id);
    if (!AjxUtil.isEmpty(e) && !AjxUtil.isEmpty(e.parentNode)) {
        e.parentNode.removeChild(e);
    }
}

ZaGlobalAdvancedStatsPage.insertChartHTML = function(element) {
	var id = Math.random();
	var form = document.createElement("form");
	form.style.margin = "5px 10px";
	form.id = "loggerform" + id;
	form.onsubmit = "return false;";
	form.action = "#";
	
	var table = document.createElement("table");
	table.className = "ChartSettings";
	var tbody = document.createElement("tbody");
        table.appendChild(tbody);
	table.id = "loggertable" + id;
	table.style.tableLayout = "fixed";
	if (AjxEnv.isIE) {
		table.style.width = "96%";
	}
	
	var label;
	var tr;
	var td;
	var select;
	tr = document.createElement("tr");
	td = document.createElement("td");
	label = document.createElement("label");
	label.style.display = "block"; //show it in a single line
	label.htmlFor = "select-servers" + id;
	ZaGlobalAdvancedStatsPage.setText(label, ZaMsg.NAD_AdvStatsServerLabel);
	select = document.createElement("select");
	select.id = "select-servers" + id;
	select.style.width = "18em";
	select.name = "servers";
	select.onchange = function(evt) { ZaGlobalAdvancedStatsPage.serverSelected(evt, id); }
	td.vAlign = "top";
	td.appendChild(label);
	td.appendChild(select);
	tr.appendChild(td);
	td = document.createElement("td");
	label = document.createElement("label");
	label.style.display = "block"; //show it in a single line
	label.htmlFor = "select-group" + id;
	ZaGlobalAdvancedStatsPage.setText(label, ZaMsg.NAD_AdvStatsGroupLabel);
	select = document.createElement("select");
	select.id = "select-group" + id;
	select.style.width = "18em";
	select.name = "groups";
	select.onchange = function(evt) { ZaGlobalAdvancedStatsPage.groupSelected(evt, id); }
	td.vAlign = "top";
	td.appendChild(label);
	td.appendChild(select);
	tr.appendChild(td);
	td = document.createElement("td");
	label = document.createElement("label");
	label.style.display = "block"; //show it in a single line
	label.htmlFor = "select-counter" + id;
	ZaGlobalAdvancedStatsPage.setText(label, ZaMsg.NAD_AdvStatsCountersLabel);
	select = document.createElement("select");
	select.id = "select-counter" + id;
	select.style.width = "18em";
	select.name = "counters";
	select.multiple = true;
	select.size = 5;
	select.onchange = function(evt) { ZaGlobalAdvancedStatsPage.counterSelected(evt, id); }
	td.vAlign = "top";
	td.appendChild(label);
	td.appendChild(select);
	tr.appendChild(td);
	tbody.appendChild(tr);
	
	var input;
	tr = document.createElement("tr");
	td = document.createElement("td");
	label = document.createElement("label");
	label.style.display = "block"; //show it in a single line
	label.htmlFor = "input-start-time" + id;
	ZaGlobalAdvancedStatsPage.setText(label, ZaMsg.NAD_AdvStatsStartLabel);
	input = document.createElement("input");
	input.id = "input-start-time" + id;
	input.type = "text";
	input.name = "startTime";
	input.value = "now-1d";
	td.appendChild(label);
	td.appendChild(input);
	td.valign = "top";
	tr.appendChild(td);
	td = document.createElement("td");
	label = document.createElement("label");
	label.style.display = "block"; //show it in a single line
	label.htmlFor = "input-end-time" + id;
	ZaGlobalAdvancedStatsPage.setText(label, ZaMsg.NAD_AdvStatsEndLabel);
	input = document.createElement("input");
	input.id = "input-end-time" + id;
	input.type = "text";
	input.name = "endTime";
	input.value = "now";
	td.appendChild(label);
	td.appendChild(input);
	td.valign = "top";
	tr.appendChild(td);
	tbody.appendChild(tr);
	 
	form.appendChild(table);
	
	var a;
	var span;
	a = document.createElement("a");
	a.href = "javascript:void(0)";
	a.className = "LinkButton";
	a.onclick = function (evt) {
		var showing = ZaGlobalAdvancedStatsPage.showhide("loggertable" + id);
		var target = ZaGlobalAdvancedStatsPage.getTarget(evt);
		ZaGlobalAdvancedStatsPage.setText(target, (showing ? " - " : " + ") + ZaMsg.NAD_AdvStatsChartSettingsLabel);
		return false; //disable the default action from broswer, such as jumping to other page
	}
	ZaGlobalAdvancedStatsPage.setText(a, " - " + ZaMsg.NAD_AdvStatsChartSettingsLabel);
	form.appendChild(a);
	
	//span = document.createElement("span");
	//ZaGlobalAdvancedStatsPage.setText(span, " | ");
	//form.appendChild(span);
	a = document.createElement("a");
	a.href = "javascript:void(0)";
	a.className = "LinkButton";
	a.onclick = function () {
		var s = document.getElementById("select-counter" + id);
		ZaGlobalAdvancedStatsPage.counterSelected({ target: s }, id);
		return false; //disable the default action from broswer, such as jumping to other page
	}
	ZaGlobalAdvancedStatsPage.setText(a, ZaMsg.NAD_AdvStatsUpdateChartLabel);
	form.appendChild(a);
	//span = document.createElement("span");
	//ZaGlobalAdvancedStatsPage.setText(span, " | ");
	//form.appendChild(span);
	a = document.createElement("a");
	a.href = "javascript:void(0)";
	a.className = "LinkButton";
	a.onclick = function () {
		ZaGlobalAdvancedStatsPage.removeChild("loggerform" + id);
		ZaGlobalAdvancedStatsPage.removeChild("loggerchart" + id);
		return false; //disable the default action from broswer, such as jumping to other page
	}
	ZaGlobalAdvancedStatsPage.setText(a, ZaMsg.NAD_AdvStatsRemoveChartLabel);
	form.appendChild(a);
	
	var div = document.createElement("div");
	div.style.padding = "5px 10px 15px";
	div.id = "loggerchart" + id;
	element.appendChild(form);
	element.appendChild(div);
	
    var serversSelect = document.getElementById("select-servers" + id);
    var soapRequest = AjxSoapDoc.create("GetLoggerStatsRequest", ZaZimbraAdmin.URN, null);
    var cb = function(response) {
        var soapResponse = response.getResponse().Body.GetLoggerStatsResponse;
        ZaGlobalAdvancedStatsPage.clearSelect(serversSelect);
        if (soapResponse.hostname) {
            for (var i = 0, j = soapResponse.hostname.length; i < j; i++) {
                var option = document.createElement("option");
                if (i == 0) option.selected = "selected";
                option.value = soapResponse.hostname[i].hn;
                ZaGlobalAdvancedStatsPage.setText(option, soapResponse.hostname[i].hn);
                serversSelect.appendChild(option);
            }
            ZaGlobalAdvancedStatsPage.serverSelected({ target: serversSelect }, id);
        }
    };
    
    var csfeParams = { soapDoc: soapRequest, asyncMode: true, callback: new AjxCallback(cb) };
    var reqMgrParams = { controller: ZaApp.getInstance().getCurrentController(), busyMsg: ZaMsg.PQ_LOADING };
    ZaRequestMgr.invoke(csfeParams, reqMgrParams);
}

ZaGlobalAdvancedStatsPage.prototype._createHtml = 
function () {
	DwtTabViewPage.prototype._createHtml.call(this);
	var element = this.getHtmlElement();
	var div = document.createElement("div");
	div.style.padding = "5px 10px";
	var a = document.createElement("a");
	ZaGlobalAdvancedStatsPage.setText(a, ZaMsg.NAD_AdvStatsAddChartLabel);
	a.href = "javascript:void(0)";
	a.className = "LinkButton";
	a.onclick = function () {
		ZaGlobalAdvancedStatsPage.insertChartHTML(element);
		return false; //disable the default action from broswer, such as jumping to other page
	};
	div.appendChild(a);
	element.appendChild(div);
}
