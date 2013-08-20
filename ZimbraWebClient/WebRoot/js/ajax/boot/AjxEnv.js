/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2005, 2006, 2007, 2008, 2009, 2010, 2011, 2012, 2013 Zimbra Software, LLC.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.4 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */

/**
 * Information about the browser and run-time environment.
 * @class
 */
AjxEnv = function() {
};

//
// Constants
//

/** User locale. */
AjxEnv.DEFAULT_LOCALE = window.navigator.userLanguage || window.navigator.language || window.navigator.systemLanguage;

//
// Data
//

AjxEnv._inited = false;

// NOTE: These are here so that the documentation tool can pick them up

/** Gecko date. */
AjxEnv.geckoDate;
/** Mozilla version. */
AjxEnv.mozVersion;
/** WebKit version. */
AjxEnv.webKitVersion;
/** Macintosh. */
AjxEnv.isMac;
/** Windows. */
AjxEnv.isWindows;
/** Windows 64-bit. */
AjxEnv.isWindows64;
/** Linux. */
AjxEnv.isLinux;
/** Netscape Navigator compatible. */
AjxEnv.isNav;
/** Internet Explorer. */
AjxEnv.isIE;
/** Netscape Navigator version 4. */
AjxEnv.isNav4;


AjxEnv.trueNs;


/** Netscape Navigator version 6. */
AjxEnv.isNav6;
/** Netscape Navigator version 6 (or higher). */
AjxEnv.isNav6up;
/** Netscape Navigator version 7. */
AjxEnv.isNav7;
/** Internet Explorer version 3. */
AjxEnv.isIE3;
/** Internet Explorer version 4. */
AjxEnv.isIE4;
/** Internet Explorer version 4 (or higher). */
AjxEnv.isIE4up;
/** Internet Explorer version 5. */
AjxEnv.isIE5;
/** Internet Explorer version 5.5. */
AjxEnv.isIE5_5;
/** Internet Explorer version 5 (or higher). */
AjxEnv.isIE5up;
/** Internet Explorer version 5.5 (or higher). */
AjxEnv.isIE5_5up;
/** Internet Explorer version 6. */
AjxEnv.isIE6;
/** Internet Explorer version 6 (or higher). */
AjxEnv.isIE6up;
/** Internet Explorer version 7. */
AjxEnv.isIE7;
/** Internet Explorer version 7 (or higher). */
AjxEnv.isIE7up;
/** Internet Explorer version 8. */
AjxEnv.isIE8;
/** Internet Explorer version 8 (or higher). */
AjxEnv.isIE8up;


AjxEnv.isNormalResolution;
AjxEnv.ieScaleFactor;


/** Mozilla Firefox. */
AjxEnv.isFirefox;
/** Mozilla Firefox version 1 (or higher). */
AjxEnv.isFirefox1up;
/** Mozilla Firefox version 1.5 (or higher). */
AjxEnv.isFirefox1_5up;
/** Mozilla Firefox version 3 (or higher). */
AjxEnv.isFirefox3up;
/** Mozilla Firefox version 3.6 (or higher). */
AjxEnv.isFirefox3_6up;
/** Mozilla Firefox version 4 (or higher). */
AjxEnv.isFirefox4up;
/** Mozilla. */
AjxEnv.isMozilla;
/** Mozilla version 1.4 (or higher). */
AjxEnv.isMozilla1_4up;
/** Safari. */
AjxEnv.isSafari;
/** Safari version 2. */
AjxEnv.isSafari2;
/** Safari version 3. */
AjxEnv.isSafari3;
/** Safari version 3 (or higher). */
AjxEnv.isSafari3up;
/** Safari version 4. */
AjxEnv.isSafari4;
/** Safari version 4 (or higher). */
AjxEnv.isSafari4up;
/** Safari version 5 (or higher). */
AjxEnv.isSafari5up;
/** Safari version 5.1 (or higher). */
AjxEnv.isSafari5_1up;
/** Camino. */
AjxEnv.isCamino;
/** Chrome. */
AjxEnv.isChrome;
AjxEnv.isChrome2up;
AjxEnv.isChrome7;
AjxEnv.isChrome10up;
/** Gecko-based. */
AjxEnv.isGeckoBased;
/** WebKit-based. */
AjxEnv.isWebKitBased;
/** Opera. */
AjxEnv.isOpera;


AjxEnv.useTransparentPNGs;


/** Zimbra Desktop. */
AjxEnv.isDesktop;
/** Zimbra Desktop version 2 (or higher). */
AjxEnv.isDesktop2up;
/** Screen size is less then 800x600. */
AjxEnv.is800x600orLower;
/** Screen size is less then 1024x768. */
AjxEnv.is1024x768orLower;

/** HTML5 Support **/
AjxEnv.supportsHTML5File;

AjxEnv.supported = Modernizr;


/** Supports indirect global eval() **/
AjxEnv.indirectEvalIsGlobal;
(function(){
	// Feature detection to see if eval referenced by alias runs in global scope
	// See davidflanagan.com/2010/12/global-eval-in.html 
	AjxEnv.indirectEvalIsGlobal=false;
	var evl=window.eval;
	try{
		evl('__indirectEval=true');
		if('__indirectEval' in window){
			AjxEnv.indirectEvalIsGlobal=true;
			delete __indirectEval;
		}
	}catch(e){}
})();

//
// Public functions
//

AjxEnv.reset =
function() {
	AjxEnv.geckoDate = 0;
	AjxEnv.mozVersion = -1;
	AjxEnv.webKitVersion = -1;
	AjxEnv.isMac = false;
	AjxEnv.isWindows = false;
	AjxEnv.isWindows64 = false;
	AjxEnv.isLinux = false;
	AjxEnv.isNav  = false;
	AjxEnv.isIE = false;
	AjxEnv.isNav4 = false;
	AjxEnv.trueNs = true;
	AjxEnv.isNav6 = false;
	AjxEnv.isNav6up = false;
	AjxEnv.isNav7 = false;
	AjxEnv.isIE3 = false;
	AjxEnv.isIE4 = false;
	AjxEnv.isIE4up = false;
	AjxEnv.isIE5 = false;
	AjxEnv.isIE5_5 = false;
	AjxEnv.isIE5up = false;
	AjxEnv.isIE5_5up = false;
	AjxEnv.isIE6  = false;
	AjxEnv.isIE6up = false;
	AjxEnv.isIE7  = false;
	AjxEnv.isIE7up = false;
	AjxEnv.isIE8  = false;
	AjxEnv.isIE8up = false;
	AjxEnv.isIE9   = false;
	AjxEnv.isIE9up = false;
	AjxEnv.isIE10  = false;
	AjxEnv.isNormalResolution = false;
	AjxEnv.ieScaleFactor = 1;
	AjxEnv.isFirefox = false;
	AjxEnv.isFirefox1up = false;
	AjxEnv.isFirefox1_5up = false;
	AjxEnv.isFirefox3up = false;
	AjxEnv.isFirefox3_6up = false;
	AjxEnv.isFirefox4up = false;
	AjxEnv.isMozilla = false;
	AjxEnv.isMozilla1_4up = false;
	AjxEnv.isSafari = false;
	AjxEnv.isSafari2 = false;
	AjxEnv.isSafari3 = false;
    AjxEnv.isSafari4 = false;
	AjxEnv.isSafari3up = false;
	AjxEnv.isSafari4up = false;
    AjxEnv.isSafari5up = false;
    AjxEnv.isSafari5_1up = false;
	AjxEnv.isSafari6up = false;
	AjxEnv.isCamino = false;
	AjxEnv.isChrome = false;
    AjxEnv.isChrome2up = false;
    AjxEnv.isChrome7 = false;
    AjxEnv.isChrome10up = false;
	AjxEnv.isChrome19up = false;
	AjxEnv.isGeckoBased = false;
	AjxEnv.isWebKitBased = false;
	AjxEnv.isOpera = false;
	AjxEnv.useTransparentPNGs = false;
	AjxEnv.isDesktop = false;
	AjxEnv.isDesktop2up = false;

    //HTML5
    AjxEnv.supportsHTML5File = false;
	AjxEnv.supportsPlaceholder = false;

	// screen resolution - ADD MORE RESOLUTION CHECKS AS NEEDED HERE:
	AjxEnv.is800x600orLower = screen && (screen.width <= 800 && screen.height <= 600);
    AjxEnv.is1024x768orLower = screen && (screen.width <= 1024 && screen.height <= 768);
};

AjxEnv.parseUA = 
function() {
	AjxEnv.reset();

	var agt = navigator.userAgent.toLowerCase();
	var agtArr = agt.split(" ");
	var isSpoofer = false;
	var isWebTv = false;
	var isHotJava = false;
	var beginsWithMozilla = false;
	var isCompatible = false;

	if (agtArr != null) {
		var browserVersion;
		var index = -1;

		if ((index = agtArr[0].search(/^\s*mozilla\//))!= -1) {
			beginsWithMozilla = true;
			AjxEnv.browserVersion = parseFloat(agtArr[0].substring(index + 8));
			AjxEnv.isNav = true;
		}

		var token;
		for (var i = 0; i < agtArr.length; ++i) {
			token = agtArr[i];
			if (token.indexOf('compatible') != -1 ) {
				isCompatible = true;
				AjxEnv.isNav = false;
			} else if ((token.indexOf('opera')) != -1) {
				AjxEnv.isOpera = true;
				AjxEnv.isNav = false;
				browserVersion = parseFloat(agtArr[i+1]);
			} else if ((token.indexOf('spoofer')) != -1) {
				isSpoofer = true;
				AjxEnv.isNav = false;
			} else if ((token.indexOf('webtv')) != -1) {
				isWebTv = true;
				AjxEnv.isNav = false;
			} else if ((token.indexOf('hotjava')) != -1) {
				isHotJava = true;
				AjxEnv.isNav = false;
			} else if ((index = token.indexOf('msie')) != -1) {
				AjxEnv.isIE = true;
				browserVersion = parseFloat(agtArr[i+1]);
			} else if ((index = token.indexOf('gecko/')) != -1) {
				AjxEnv.isGeckoBased = true;
				AjxEnv.geckoDate = parseFloat(token.substr(index + 6));
			} else if ((index = token.indexOf('applewebkit/')) != -1) {
				AjxEnv.isWebKitBased = true;
				AjxEnv.webKitVersion = parseFloat(token.substr(index + 12));
			} else if ((index = token.indexOf('rv:')) != -1) {
				AjxEnv.mozVersion = parseFloat(token.substr(index + 3));
				browserVersion = AjxEnv.mozVersion;
			} else if ((index = token.indexOf('firefox/')) != -1) {
				AjxEnv.isFirefox = true;
				browserVersion = parseFloat(token.substr(index + 8));
			} else if ((index = token.indexOf('prism')) != -1) {
				AjxEnv.isPrism = true;
			} else if ((index = token.indexOf('camino/')) != -1) {
				AjxEnv.isCamino = true;
				browserVersion = parseFloat(token.substr(index + 7));
			} else if ((index = token.indexOf('netscape6/')) != -1) {
				AjxEnv.trueNs = true;
				browserVersion = parseFloat(token.substr(index + 10));
			} else if ((index = token.indexOf('netscape/')) != -1) {
				AjxEnv.trueNs = true;
				browserVersion = parseFloat(token.substr(index + 9));
			} else if ((index = token.indexOf('safari/')) != -1) {
				AjxEnv.isSafari = true;
			} else if ((index = token.indexOf('chrome/')) != -1) {
				AjxEnv.isChrome = true;
				browserVersion = parseFloat(token.substr(index + 7));
			} else if (index = token.indexOf('version/') != -1) {
				// this is how safari sets browser version
				browserVersion = parseFloat(token.substr(index + 7));
			} else if (token.indexOf('windows') != -1) {
				AjxEnv.isWindows = true;
			} else if (token.indexOf('win64') != -1) {
				AjxEnv.isWindows64 = true;
			} else if ((token.indexOf('macintosh') != -1) ||
					   (token.indexOf('mac_') != -1)) {
				AjxEnv.isMac = true;
			} else if (token.indexOf('linux') != -1) {
				AjxEnv.isLinux = true;
			} else if ((index = token.indexOf('zdesktop/')) != -1) {
				AjxEnv.isDesktop = true;
				browserVersion = parseFloat(token.substr(index + 9));
			}
		}
		AjxEnv.browserVersion = browserVersion;

		// Note: Opera and WebTV spoof Navigator. We do strict client detection.
		AjxEnv.isNav 			= (beginsWithMozilla && !isSpoofer && !isCompatible && !AjxEnv.isOpera && !isWebTv && !isHotJava && !AjxEnv.isSafari);
		AjxEnv.isIE				= (AjxEnv.isIE && !AjxEnv.isOpera);
		AjxEnv.isNav4			= (AjxEnv.isNav && (browserVersion == 4) && (!AjxEnv.isIE));
		AjxEnv.isNav6			= (AjxEnv.isNav && AjxEnv.trueNs && (browserVersion >= 6.0 && browserVersion < 7.0));
		AjxEnv.isNav6up 		= (AjxEnv.isNav && AjxEnv.trueNs && (browserVersion >= 6.0));
		AjxEnv.isNav7 			= (AjxEnv.isNav && AjxEnv.trueNs && (browserVersion >= 7.0 && browserVersion < 8.0));
		AjxEnv.isIE3 			= (AjxEnv.isIE && browserVersion <  4.0);
		AjxEnv.isIE4			= (AjxEnv.isIE && browserVersion >= 4.0 && browserVersion < 5.0);
		AjxEnv.isIE4up			= (AjxEnv.isIE && browserVersion >= 4.0);
		AjxEnv.isIE5			= (AjxEnv.isIE && browserVersion >= 5.0 && browserVersion < 6.0);
		AjxEnv.isIE5_5			= (AjxEnv.isIE && browserVersion == 5.5);
		AjxEnv.isIE5up			= (AjxEnv.isIE && browserVersion >= 5.0);
		AjxEnv.isIE5_5up		= (AjxEnv.isIE && browserVersion >= 5.5);
		AjxEnv.isIE6			= (AjxEnv.isIE && browserVersion >= 6.0 && browserVersion < 7.0);
		AjxEnv.isIE6up			= (AjxEnv.isIE && browserVersion >= 6.0);
		AjxEnv.isIE7			= (AjxEnv.isIE && browserVersion >= 7.0 && browserVersion < 8.0);
		AjxEnv.isIE7up			= (AjxEnv.isIE && browserVersion >= 7.0);
		AjxEnv.isIE8			= (AjxEnv.isIE && browserVersion >= 8.0 && browserVersion < 9.0);
		AjxEnv.isIE8up			= (AjxEnv.isIE && browserVersion >= 8.0);
		AjxEnv.isIE9			= (AjxEnv.isIE && browserVersion >= 9.0 && browserVersion < 10.0);
		AjxEnv.isIE9up			= (AjxEnv.isIE && browserVersion >= 9.0);
		AjxEnv.isIE10			= (AjxEnv.isIE && browserVersion >= 10.0 && browserVersion < 11.0);
		AjxEnv.isMozilla		= ((AjxEnv.isNav && AjxEnv.mozVersion && AjxEnv.isGeckoBased && (AjxEnv.geckoDate != 0)));
		AjxEnv.isMozilla1_4up	= (AjxEnv.isMozilla && (AjxEnv.mozVersion >= 1.4));
		AjxEnv.isFirefox 		= ((AjxEnv.isMozilla && AjxEnv.isFirefox));
		AjxEnv.isFirefox1up		= (AjxEnv.isFirefox && browserVersion >= 1.0);
		AjxEnv.isFirefox1_5up	= (AjxEnv.isFirefox && browserVersion >= 1.5);
		AjxEnv.isFirefox2_0up	= (AjxEnv.isFirefox && browserVersion >= 2.0);
		AjxEnv.isFirefox3up		= (AjxEnv.isFirefox && browserVersion >= 3.0);
		AjxEnv.isFirefox3_5up	= (AjxEnv.isFirefox && browserVersion >= 3.5);
		AjxEnv.isFirefox3_6up	= (AjxEnv.isFirefox && browserVersion >= 3.6);
		AjxEnv.isFirefox4up		= (AjxEnv.isFirefox && browserVersion >= 4.0);
		AjxEnv.isSafari2		= (AjxEnv.isSafari && browserVersion >= 2.0 && browserVersion < 3.0);
		AjxEnv.isSafari3		= (AjxEnv.isSafari && browserVersion >= 3.0 && browserVersion < 4.0) || AjxEnv.isChrome;
        AjxEnv.isSafari4        = (AjxEnv.isSafari && browserVersion >= 4.0);
		AjxEnv.isSafari3up		= (AjxEnv.isSafari && browserVersion >= 3.0) || AjxEnv.isChrome;
		AjxEnv.isSafari4up		= (AjxEnv.isSafari && browserVersion >= 4.0) || AjxEnv.isChrome;
        AjxEnv.isSafari5up	    = (AjxEnv.isSafari && browserVersion >= 5.0) || AjxEnv.isChrome;
        AjxEnv.isSafari5_1up	= (AjxEnv.isSafari && browserVersion >= 5.1) || AjxEnv.isChrome;
		AjxEnv.isSafari6up      = AjxEnv.isSafari && browserVersion >= 6.0;
		AjxEnv.isDesktop2up		= (AjxEnv.isDesktop && browserVersion >= 2.0);
        AjxEnv.isChrome2up		= (AjxEnv.isChrome && browserVersion >= 2.0);
        AjxEnv.isChrome7		= (AjxEnv.isChrome && browserVersion >= 7.0);
        AjxEnv.isChrome10up		= (AjxEnv.isChrome && browserVersion >= 10.0);
		AjxEnv.isChrome19up		= (AjxEnv.isChrome && browserVersion >= 19.0);

		AjxEnv.browser = "[unknown]";
		if (AjxEnv.isOpera) 				{	AjxEnv.browser = "OPERA";	}
		else if (AjxEnv.isChrome)			{	AjxEnv.browser = "GC" + browserVersion;	}
		else if (AjxEnv.isSafari)			{	AjxEnv.browser = "SAF" + browserVersion; }
		else if (AjxEnv.isCamino)			{	AjxEnv.browser = "CAM";		}
		else if (isWebTv)					{	AjxEnv.browser = "WEBTV";	}
		else if (isHotJava)					{	AjxEnv.browser = "HOTJAVA";	}
		else if (AjxEnv.isFirefox)			{	AjxEnv.browser = "FF" + browserVersion; }
		else if (AjxEnv.isPrism)			{	AjxEnv.browser = "PRISM";	}
		else if (AjxEnv.isNav7)				{	AjxEnv.browser = "NAV7";	}
		else if (AjxEnv.isNav6)				{	AjxEnv.browser = "NAV6";	}
		else if (AjxEnv.isNav4)				{	AjxEnv.browser = "NAV4";	}
		else if (AjxEnv.isIE)				{	AjxEnv.browser = "IE" + browserVersion; }
		else if (AjxEnv.isDesktop)			{	AjxEnv.browser = "ZD" + browserVersion; }

		AjxEnv.platform = "[unknown]";
		if (AjxEnv.isWindows)				{	AjxEnv.platform = "Win";	}
		else if (AjxEnv.isMac)				{	AjxEnv.platform = "Mac";	}
		else if (AjxEnv.isLinux)			{	AjxEnv.platform = "Linux";	}
	}

	// setup some global setting we can check for high resolution
	if (AjxEnv.isIE) {
		AjxEnv.isNormalResolution = true;
		AjxEnv.ieScaleFactor = screen.deviceXDPI / screen.logicalXDPI;
		if (AjxEnv.ieScaleFactor > 1) {
			AjxEnv.isNormalResolution = false;
		}
	}

	AjxEnv._inited = !AjxEnv.isIE;

	// test for safari nightly
	if (AjxEnv.isSafari) {
		var webkit = AjxEnv.getWebkitVersion();
		AjxEnv.isSafariNightly = (webkit && webkit['is_nightly']);
		// if not safari v3 or the nightly, assume we're dealing with v2  :/
		AjxEnv.isSafari2 = !AjxEnv.isSafari3 && !AjxEnv.isSafariNightly;
	}

    //HTML5
    AjxEnv.supportsHTML5File = !!( window.FileReader/*Firefox*/ || AjxEnv.isChrome || AjxEnv.isSafari4up || AjxEnv.isIE10up );
    AjxEnv.supportsPlaceholder 	= !(AjxEnv.isFirefox && !AjxEnv.isFirefox4up) || !(AjxEnv.isIE && !AjxEnv.isIE10up);
};

// code provided by webkit authors to determine if nightly browser
AjxEnv.getWebkitVersion =
function() {

	var webkit_version;
	var regex = new RegExp("\\(.*\\) AppleWebKit/(.*) \\((.*)");
	var matches = regex.exec(navigator.userAgent);
	if (matches) {
		var version = matches[1];
		var bits = version.split(".");
		var is_nightly = (version[version.length - 1] == "+");
		var minor = is_nightly ? "+" : parseInt(bits[1]);
		// If minor is Not a Number (NaN) return an empty string
		if (isNaN(minor)) minor = "";

		webkit_version = {major:parseInt(bits[0]), minor:minor, is_nightly:is_nightly};
	}
	return webkit_version || {};
};


AjxEnv.parseUA();

// https://developer.mozilla.org/en/JavaScript/Reference/Global_Objects/Function/bind
/*
if ( !Function.prototype.bind ) {
  Function.prototype.bind = function( obj ) {
    var slice = [].slice,
        args = slice.call(arguments, 1),
        self = this,
        nop = function () {},
        bound = function () {
          return self.apply( this instanceof nop ? this : ( obj || {} ),
                              args.concat( slice.call(arguments) ) );   
        };
    nop.prototype = self.prototype;
    bound.prototype = new nop();
    return bound;
  };
}
*/

// An alternative, simpler implementation. Not sure whether it does everything that the above version does,
// but it should work fine as a basic closure-style callback.
if (!Function.prototype.bind) {
	Function.prototype.bind = function(thisObj) {
		var that = this;
		var args;
                
		if (arguments.length > 1) {
			// optimization: create the extra array object only if needed. 
			args = Array.prototype.slice.call(arguments, 1);
		}
                
		return function () {
			var allArgs = args;

			// optimization: concat array only if needed
			if (arguments.length > 0) {
				allArgs = (args && args.length) ? args.concat(Array.prototype.slice.call(arguments)) : arguments;
			}

			// for some reason, IE does not like the undefined allArgs hence the below condition.
			return allArgs ? that.apply(thisObj, allArgs) : that.apply(thisObj);
		};
	};
}

/**
 * This should be a temporary hack as we transition from AjxCallback to bind(). Rather
 * than change hundreds of call sites with 'callback.run()' to see if the callback is
 * an AjxCallback or a closure, add a run() method to Function which just invokes the
 * closure.
 */
Function.prototype.run = function() {
	return this.apply(this, arguments);
};
