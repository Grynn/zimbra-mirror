/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Zimlets
 * Copyright (C) 2009, 2010 Zimbra, Inc.
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
 * Constructor.
 *
 * @author		Raja Rao
 */
function com_zimbra_meebo_HandlerObject() {
}
com_zimbra_meebo_HandlerObject.prototype = new ZmZimletBase();
com_zimbra_meebo_HandlerObject.prototype.constructor = com_zimbra_meebo_HandlerObject;

/**
 * Simplify handler object
 *
 */
var MeeboZimlet = com_zimbra_meebo_HandlerObject;

/**
 * Defines the "load" interval (milliseconds).
 */
MeeboZimlet.LOAD_INTERVAL = 5000;
/**
 * Defines the "load" timeout (milliseconds).
 */
MeeboZimlet.LOAD_TIMEOUT = 20000;

/**
 * Static ids
 */
MeeboZimlet.SKIN_ROW_ID = "meeboZimlet_bar_rowId";


/**
 * Initializes the zimlet.
 */
MeeboZimlet.prototype.init =
function() {
	this._makeSpaceForMeeboBar();
	this._initializeMeebo();
	//todo Need info from Meebo folks about how we can know if Meebo-bar is actually loaded. i.e. something like: Meebo.isLoaded API
	//this._callCount = 0;
	//this.timer = setInterval(AjxCallback.simpleClosure(this._checkMeeboExists, this), MeeboZimlet.LOAD_INTERVAL);
};


/**
 * Makes space for Meebo bar in the skin
 */
MeeboZimlet.prototype._makeSpaceForMeeboBar =
function() {
	var tbl = document.getElementById("skin_table_outer");
	var newRow = tbl.insertRow(tbl.rows.length);
	newRow.style.display = "block";
	newRow.id = MeeboZimlet.SKIN_ROW_ID;

	var cell = newRow.insertCell(0);
	if (AjxEnv.isIE) {
		cell.height = "24px";
	} else {
		cell.height = "26px";
	}
	cell.innerHTML = "<label style='font-size:12px;font-weight:bold;'>Space reserved for Meebo Bar Zimlet</label>";
};

/**
 * [Code is from Meebo] - Loads Meebo Bar
 */
MeeboZimlet.prototype._initializeMeebo =
function() {
	window.Meebo || function(b) {
		function p() {
			return["<",i,' onload="var d=',g,";d.getElementsByTagName('head')[0].",
				j,"(d.",h,"('script')).",k,"='//",b.stage ? "stage-" : "","cim.meebo.com/cim?iv=",a.v,
				"&",q,"=",b[q],b[l] ? "&" + l + "=" + b[l] : "",b[e] ? "&" + e + "=" + b[e] : "","'\"></",i,">"].join("")
		}

		var f = window,a = f.Meebo = f.Meebo || function() {
			(a._ = a._ || []).push(arguments)
		},d = document,
		i = "body",m = d[i],r;
		if (!m) {
			r = arguments.callee;
			return setTimeout(function() {
				r(b)
			},
			100)
		}
		a.$ = {0:+new Date};
		a.T = function(u) {
			a.$[u] = new Date - a.$[0]
		};
		a.v = 4;
		var j = "appendChild",
		h = "createElement",k = "src",l = "lang",q = "network",e = "domain",n = d[h]("div"),v = n[j](d[h]("m")),
		c = d[h]("iframe"),g = "document",o,s = function() {
			a.T("load");
			a("load")
		};
		f.addEventListener ?
		f.addEventListener("load", s, false) : f.attachEvent("onload", s);
		n.style.display = "none";
		m.insertBefore(n, m.firstChild).id = "meebo";
		c.frameBorder = "0";
		c.id = "meebo-iframe";
		c.allowTransparency = "true";
		v[j](c);
		try {
			c.contentWindow[g].open()
		} catch(w) {
			b[e] =
			d[e];
			o = "javascript:var d=" + g + ".open();d.domain='" + d.domain + "';";
			c[k] = o + "void(0);"
		}
		try {
			var t =
			c.contentWindow[g];
			t.write(p());
			t.close()
		} catch(x) {
			c[k] = o + 'd.write("' + p().replace(/"/g,
			'\\"') + '");d.close();'
		}
		a.T(1)
	}({network:"zimbra"});
	Meebo.disableSharePageButton = true;
};


/**
 * Shows the meebo iframe.
 *
 */
MeeboZimlet.prototype._checkMeeboExists =
function() {
	this._callCount++;
	if (this._callCount == (MeeboZimlet.LOAD_TIMEOUT / MeeboZimlet.LOAD_INTERVAL)) { //after 60 seconds, stop checking
		clearInterval(this.timer);
		document.getElementById(MeeboZimlet.SKIN_ROW_ID).style.display = "none";
		var errMsg = AjxMessageFormat.format(this.getMessage("MeeboZimlet_error_loadBar"), MeeboZimlet.LOAD_TIMEOUT / 1000);
		appCtxt.getAppController().setStatusMsg(errMsg, ZmStatusView.LEVEL_WARNING);
		return;
	}
};