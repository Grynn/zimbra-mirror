/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Web Client
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

/**
* @class ZaServerDiskStatsPage
* @contructor ZaServerDiskStatsPage
* @param parent
* @param app
* @author Greg Solovyev
**/
ZaServerDiskStatsPage = function(parent, app) {
	DwtTabViewPage.call(this, parent);
	this._fieldIds = new Object(); //stores the ids of all the form elements
	this._app = app;
	this.initialized=false;
	this._rendered = false;
}

ZaServerDiskStatsPage.prototype = new DwtTabViewPage;
ZaServerDiskStatsPage.prototype.constructor = ZaServerDiskStatsPage;

ZaServerDiskStatsPage.prototype.toString = function() {
	return "ZaServerDiskStatsPage";
};

ZaServerDiskStatsPage.prototype.setObject = function (item) {
	this._server = item;
	if(this._rendered) {
		this._view.getInstance().currentTab = 1;
		var ims = this._view.getItemsById('images');
		for (var i = 0 ; i < ims.length; ++i ){
			ims[i].dirtyDisplay();
		}
		this._view.refresh();		
	}
};

ZaServerDiskStatsPage.prototype.showMe = function () {
	DwtTabViewPage.prototype.showMe.call(this);
	if (!this._rendered) {
		var instance = {currentTab:1};
		var xModelObj = new XModel({id:"currentTab", type:_UNTYPED_});
		this._view = new XForm(this._getXForm(), xModelObj, instance, this);
		this._view.setController(this);
		this._view.draw();
		this._rendered = true;
	}	
}


ZaServerDiskStatsPage.prototype.writeImageHtml = function (periodInt) {
	var periodString = "hour";
	var serverName = this._server.name;
	var periodString = this._getPeriodString(periodInt);
		
	return AjxBuffer.concat("<img  alt='" + ZaMsg.Stats_Unavailable + "' src='/service/statsimg/disk." , serverName ,
							".", periodString,".Disk_Usage_0.gif?nodef=1&rand=", Math.random(), "' onload='javascript:ZaServerDiskStatsPage.callMethod(\"",
							this.__internalId , "\",ZaServerDiskStatsPage.prototype.loadNextImage,[this.parentNode," ,
							periodInt , ", 0])' onerror='javascript:AjxCore.objectWithId(", this.__internalId ,
							").stopLoadingImages(this,0)'><br>");
};

ZaServerDiskStatsPage.prototype.loadNextImage = function (parent, periodInt, count) {
	// let's stop at some arbitrarily high number, so that we don't get caught for some
	// reason in an infinite loop
 	if (count >= 50) {
 		return;
 	}
	++count;
	var server = this._server.name;
	var periodString = this._getPeriodString(periodInt);
	var img = Dwt.parseHtmlFragment(AjxBuffer.concat("<img  alt='" + ZaMsg.Stats_Unavailable + "' src='/service/statsimg/disk.", server, ".", periodString, ".Disk_Usage_", 
													 count, ".gif?nodef=1&rand=", Math.random(), "' onload='javascript:ZaServerDiskStatsPage.callMethod(\"",
													 this.__internalId,"\",ZaServerDiskStatsPage.prototype.loadNextImage,",
													 "[this.parentNode,",periodInt ,",", count, "])'",
													 "onerror='javascript:ZaServerDiskStatsPage.callMethod(", this.__internalId ,
													 ",ZaServerDiskStatsPage.prototype.stopLoadingImages,[this,",count,"])'><br>"));

	parent.appendChild(img);
	parent.appendChild(document.createElement('br'));
	parent.appendChild(document.createElement('br'));
};



ZaServerDiskStatsPage.callMethod = function (id, method, argsArray) {
	var obj = DwtControl.fromElementId(id)
	return method.apply(obj,argsArray);
};

ZaServerDiskStatsPage.prototype.stopLoadingImages = function (imgObj, count) {
	if (count == 0) {
		imgObj.onerror = null;
		imgObj.onload = null;
		imgObj.src = "/service/statsimg/data_not_available.gif";
	} else {
		imgObj.style.display = "none";
	}
};


ZaServerDiskStatsPage.prototype._getPeriodString = function (periodInt){
	switch (periodInt) {
	case 1:
		return "hour";
	case 2:
		return "day";
	case 3:
		return "month";
	case 4:
		return "year";
	}
	return null;
};

ZaServerDiskStatsPage.prototype._getXForm = function () {
	if (this._xform != null) return this._xform;

	this._xform = {
		x_showBorder:1,
	    numCols:1, 
	    cssClass:"ZaServerDiskStatsPage", 
		tableCssStyle:"width:100%",
	    itemDefaults:{ },
	    items:[
		   {type:_SPACER_, height:"10px", colSpan:"*",id:"xform_header" },
		
		   {type:_TAB_BAR_,  ref:ZaModel.currentTab, colSpan:"*",
		    choices:[
			     {value:1, label:ZaMsg.TABT_StatsDataLastHour},
			     {value:2, label:ZaMsg.TABT_StatsDataLastDay},
			     {value:3, label:ZaMsg.TABT_StatsDataLastMonths},
			     {value:4, label:ZaMsg.TABT_StatsDataLastYear}
			    ],
		    cssClass:"ZaTabBar", id:"xform_tabbar"
		   },

		   {type:_SWITCH_, align:_LEFT_, valign:_TOP_, 
		    items:[
			   {type:_ZATABCASE_,  relevant:"instance[ZaModel.currentTab] == 1", align:_LEFT_, valign:_TOP_, 
			   		cssStyle: "position: absolute; overflow: auto;",
			    items:[
				   {type:_SPACER_, height:10, colSpan:"*" },
				   {ref: "images", type:_OUTPUT_ ,  getDisplayValue:"return this.getFormController().writeImageHtml(1)"}
				   ]
			   },
			   {type:_ZATABCASE_,  relevant:"instance[ZaModel.currentTab] == 2", align:_LEFT_, valign:_TOP_, 
			    	cssStyle: "position: absolute; overflow: auto;",
			    items:[
				   {type:_SPACER_, height:10, colSpan:"*" },
				   {ref: "images",type:_OUTPUT_ , getDisplayValue:"return this.getFormController().writeImageHtml(2)"}
				   ]
			   },

			   {type:_ZATABCASE_,  relevant:"instance[ZaModel.currentTab] == 3", align:_LEFT_, valign:_TOP_, 
			    	cssStyle: "position: absolute; overflow: auto;",
			    items:[
				   {type:_SPACER_, height:10, colSpan:"*" },
				   {ref: "images", type:_OUTPUT_ , getDisplayValue:"return this.getFormController().writeImageHtml(3)"}
				   ]
			   },
			   {type:_ZATABCASE_,  relevant:"instance[ZaModel.currentTab] == 4", align:_LEFT_, valign:_TOP_, 
			    	cssStyle: "position: absolute; overflow: auto;",
			    items:[
				   {type:_SPACER_, height:10, colSpan:"*" },
				   {ref: "images",type:_OUTPUT_ , getDisplayValue:"return this.getFormController().writeImageHtml(4)"}
				   ]
			   }
			   ]
		   }
		   ]
	};
		   

	return this._xform;
};
