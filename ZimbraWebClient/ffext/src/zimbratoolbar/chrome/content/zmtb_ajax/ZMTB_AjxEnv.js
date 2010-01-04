/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2009, 2010 Zimbra, Inc.
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


function ZMTB_AjxEnv() {
}

ZMTB_AjxEnv._inited = false;

ZMTB_AjxEnv.DEFAULT_LOCALE = window.navigator.userLanguage || window.navigator.language || window.navigator.systemLanguage;

ZMTB_AjxEnv.reset = function () {
	ZMTB_AjxEnv.browserVersion = -1;
	ZMTB_AjxEnv.geckoDate = 0;
	ZMTB_AjxEnv.mozVersion = -1;
	ZMTB_AjxEnv.isMac = false;
	ZMTB_AjxEnv.isWindows = false;
	ZMTB_AjxEnv.isLinux = false;
	ZMTB_AjxEnv.isNav  = false;
	ZMTB_AjxEnv.isIE = false;
	ZMTB_AjxEnv.isNav4 = false;
	ZMTB_AjxEnv.trueNs = true;
	ZMTB_AjxEnv.isNav6 = false;
	ZMTB_AjxEnv.isNav6up = false;
	ZMTB_AjxEnv.isNav7 = false;
	ZMTB_AjxEnv.isIE3 = false;
	ZMTB_AjxEnv.isIE4 = false;
	ZMTB_AjxEnv.isIE4up = false;
	ZMTB_AjxEnv.isIE5 = false;
	ZMTB_AjxEnv.isIE5_5 = false;
	ZMTB_AjxEnv.isIE5up = false;
	ZMTB_AjxEnv.isIE5_5up = false;
	ZMTB_AjxEnv.isIE6  = false;
	ZMTB_AjxEnv.isIE6up = false;
	ZMTB_AjxEnv.isNormalResolution = false;
	ZMTB_AjxEnv.ieScaleFactor = 1;
	ZMTB_AjxEnv.isFirefox = false;
	ZMTB_AjxEnv.isFirefox1up = false;
	ZMTB_AjxEnv.isFirefox1_5up = false;
	ZMTB_AjxEnv.isFirefox3up = false;
	ZMTB_AjxEnv.isMozilla = false;
	ZMTB_AjxEnv.isMozilla1_4up = false;
	ZMTB_AjxEnv.isSafari = false;
	ZMTB_AjxEnv.isCamino = false;
	ZMTB_AjxEnv.isGeckoBased = false;
	ZMTB_AjxEnv.isOpera = false;
	ZMTB_AjxEnv.useTransparentPNGs = false;

	// screen resolution - ADD MORE RESOLUTION CHECKS AS NEEDED HERE:
	ZMTB_AjxEnv.is800x600orLower = screen.width <= 800 && screen.height <= 600;
};

ZMTB_AjxEnv.parseUA = function (userAgent) {
	var agt = userAgent.toLowerCase();
	var agtArr = agt.split(" ");
	var i = 0;
	var index = -1;
	var token = null;
	var isSpoofer = false;
	var isWebTv = false;
	var isHotJava = false;
	var beginsWithMozilla = false;
	var isCompatible = false;
	if (agtArr != null) {
		if ( (index = agtArr[0].search(/^\s*mozilla\//) )!= -1){
			beginsWithMozilla = true;
			ZMTB_AjxEnv.browserVersion = parseFloat(agtArr[0].substring(index + 8));
			ZMTB_AjxEnv.isNav = true;
		}
		for ( ; i < agtArr.length; ++i ){
			token = agtArr[i];
			if (token.indexOf('compatible') != -1 ) {
				isCompatible = true;
				ZMTB_AjxEnv.isNav = false;
			} else if ((token.indexOf('opera')) != -1) {
				ZMTB_AjxEnv.isOpera = true;
				ZMTB_AjxEnv.isNav = false;
				ZMTB_AjxEnv.browserVersion = parseFloat(agtArr[i+1]);
			} else if ((token.indexOf('spoofer')) != -1) {
				isSpoofer = true;
				ZMTB_AjxEnv.isNav = false;
			} else if ((token.indexOf('webtv')) != -1) {
				isWebTv = true;
				ZMTB_AjxEnv.isNav = false;
			} else if ((token.indexOf('hotjava')) != -1) {
				isHotJava = true;
				ZMTB_AjxEnv.isNav = false;
			} else if ((index = token.indexOf('msie')) != -1) {
				ZMTB_AjxEnv.isIE = true;
				ZMTB_AjxEnv.browserVersion = parseFloat(agtArr[i+1]);
			} else if ((index = token.indexOf('gecko/')) != -1) {
				ZMTB_AjxEnv.isGeckoBased = true;
				ZMTB_AjxEnv.geckoDate = parseFloat(token.substr(index + 6));
			} else if ((index = token.indexOf('rv:')) != -1) {
				ZMTB_AjxEnv.mozVersion = parseFloat(token.substr(index + 3));
				ZMTB_AjxEnv.browserVersion = ZMTB_AjxEnv.mozVersion;
			} else if ((index = token.indexOf('firefox/')) != -1) {
				ZMTB_AjxEnv.isFirefox = true;
				ZMTB_AjxEnv.browserVersion = parseFloat(token.substr(index + 8));
			} else if ((index = token.indexOf('camino/')) != -1) {
				ZMTB_AjxEnv.isCamino = true;
				ZMTB_AjxEnv.browserVersion = parseFloat(token.substr(index + 7));
			} else if ((index = token.indexOf('netscape6/')) != -1) {
				ZMTB_AjxEnv.trueNs = true;
				ZMTB_AjxEnv.browserVersion = parseFloat(token.substr(index + 10));
			} else if ((index = token.indexOf('netscape/')) != -1) {
				ZMTB_AjxEnv.trueNs = true;
				ZMTB_AjxEnv.browserVersion = parseFloat(token.substr(index + 9));
			} else if ((index = token.indexOf('safari/')) != -1) {
				ZMTB_AjxEnv.isSafari = true;
				ZMTB_AjxEnv.browserVersion = parseFloat(token.substr(index + 7));
			} else if (token.indexOf('windows') != -1) {
				ZMTB_AjxEnv.isWindows = true;
			} else if ((token.indexOf('macintosh') != -1) ||
					   (token.indexOf('mac_') != -1)) {
				ZMTB_AjxEnv.isMac = true;
			} else if (token.indexOf('linux') != -1) {
				ZMTB_AjxEnv.isLinux = true;
			}
		}
		// Note: Opera and WebTV spoof Navigator.  
		// We do strict client detection.
		ZMTB_AjxEnv.isNav  = (beginsWithMozilla && !isSpoofer && !isCompatible && 
						!ZMTB_AjxEnv.isOpera && !isWebTv && !isHotJava &&
						!ZMTB_AjxEnv.isSafari);

		ZMTB_AjxEnv.isIE = (ZMTB_AjxEnv.isIE && !ZMTB_AjxEnv.isOpera);

		ZMTB_AjxEnv.isNav4 = (ZMTB_AjxEnv.isNav && (ZMTB_AjxEnv.browserVersion  == 4) &&
						(!ZMTB_AjxEnv.isIE));
		ZMTB_AjxEnv.isNav6 = (ZMTB_AjxEnv.isNav && ZMTB_AjxEnv.trueNs && 
						(ZMTB_AjxEnv.browserVersion >= 6.0) && 
						(ZMTB_AjxEnv.browserVersion < 7.0));
		ZMTB_AjxEnv.isNav6up = (ZMTB_AjxEnv.isNav && ZMTB_AjxEnv.trueNs && 
						  (ZMTB_AjxEnv.browserVersion >= 6.0));
		ZMTB_AjxEnv.isNav7 = (ZMTB_AjxEnv.isNav && ZMTB_AjxEnv.trueNs && 
						(ZMTB_AjxEnv.browserVersion == 7.0));

		ZMTB_AjxEnv.isIE3 = (ZMTB_AjxEnv.isIE && (ZMTB_AjxEnv.browserVersion < 4));
		ZMTB_AjxEnv.isIE4 = (ZMTB_AjxEnv.isIE && (ZMTB_AjxEnv.browserVersion == 4) && 
					 (ZMTB_AjxEnv.browserVersion == 4.0));
		ZMTB_AjxEnv.isIE4up = (ZMTB_AjxEnv.isIE && (ZMTB_AjxEnv.browserVersion >= 4));
		ZMTB_AjxEnv.isIE5 = (ZMTB_AjxEnv.isIE && (ZMTB_AjxEnv.browserVersion == 4) && 
					 (ZMTB_AjxEnv.browserVersion == 5.0));
		ZMTB_AjxEnv.isIE5_5 = (ZMTB_AjxEnv.isIE && (ZMTB_AjxEnv.browserVersion == 4) && 
						 (ZMTB_AjxEnv.browserVersion == 5.5));
		ZMTB_AjxEnv.isIE5up = (ZMTB_AjxEnv.isIE && (ZMTB_AjxEnv.browserVersion >= 5.0));
		ZMTB_AjxEnv.isIE5_5up =(ZMTB_AjxEnv.isIE && (ZMTB_AjxEnv.browserVersion >= 5.5));
		ZMTB_AjxEnv.isIE6  = (ZMTB_AjxEnv.isIE && (ZMTB_AjxEnv.browserVersion == 6.0));
		ZMTB_AjxEnv.isIE6up = (ZMTB_AjxEnv.isIE && (ZMTB_AjxEnv.browserVersion >= 6.0));
		ZMTB_AjxEnv.isIE7  = (ZMTB_AjxEnv.isIE && (ZMTB_AjxEnv.browserVersion == 7.0));
		ZMTB_AjxEnv.isIE7up = (ZMTB_AjxEnv.isIE && (ZMTB_AjxEnv.browserVersion >= 7.0));

		ZMTB_AjxEnv.isMozilla = ((ZMTB_AjxEnv.isNav && ZMTB_AjxEnv.mozVersion && 
							ZMTB_AjxEnv.isGeckoBased && (ZMTB_AjxEnv.geckoDate != 0)));
		ZMTB_AjxEnv.isMozilla1_4up = (ZMTB_AjxEnv.isMozilla && (ZMTB_AjxEnv.mozVersion >= 1.4));
		ZMTB_AjxEnv.isFirefox = ((ZMTB_AjxEnv.isMozilla && ZMTB_AjxEnv.isFirefox));
		ZMTB_AjxEnv.isFirefox1up = (ZMTB_AjxEnv.isFirefox && ZMTB_AjxEnv.browserVersion >= 1.0);
		ZMTB_AjxEnv.isFirefox1_5up = (ZMTB_AjxEnv.isFirefox && ZMTB_AjxEnv.browserVersion >= 1.5);
		ZMTB_AjxEnv.isFirefox2_0up = (ZMTB_AjxEnv.isFirefox && ZMTB_AjxEnv.browserVersion >= 2.0);
		ZMTB_AjxEnv.isFirefox3up = (ZMTB_AjxEnv.isFirefox && ZMTB_AjxEnv.browserVersion >= 3.0);

		ZMTB_AjxEnv.browser = "";
		if (ZMTB_AjxEnv.isOpera) {
			ZMTB_AjxEnv.browser = "OPERA";
		} else if (ZMTB_AjxEnv.isSafari) {
			ZMTB_AjxEnv.browser = "SAF";
		} else if (ZMTB_AjxEnv.isCamino) {
			ZMTB_AjxEnv.browser = "CAM";
		} else if (isWebTv) {
			ZMTB_AjxEnv.browser = "WEBTV";
		} else if (isHotJava) {
			ZMTB_AjxEnv.browser = "HOTJAVA";
		} else if (ZMTB_AjxEnv.isFirefox3up) {
			ZMTB_AjxEnv.browser = "FF3.0";
		} else if (ZMTB_AjxEnv.isFirefox2_0up) {
			ZMTB_AjxEnv.browser = "FF2.0";
		} else if (ZMTB_AjxEnv.isFirefox1_5up) {
			ZMTB_AjxEnv.browser = "FF1.5";
		} else if (ZMTB_AjxEnv.isFirefox1up) {
			ZMTB_AjxEnv.browser = "FF1.0";
		} else if (ZMTB_AjxEnv.isFirefox) {
			ZMTB_AjxEnv.browser = "FF";
		} else if (ZMTB_AjxEnv.isNav7) {
			ZMTB_AjxEnv.browser = "NAV7";
		} else if (ZMTB_AjxEnv.isNav6) {
			ZMTB_AjxEnv.browser = "NAV6";
		} else if (ZMTB_AjxEnv.isNav4) {
			ZMTB_AjxEnv.browser = "NAV4";
		} else if (ZMTB_AjxEnv.isIE7) {
			ZMTB_AjxEnv.browser = "IE7";
		} else if (ZMTB_AjxEnv.isIE6) {
			ZMTB_AjxEnv.browser = "IE6";
		} else if (ZMTB_AjxEnv.isIE5) {
			ZMTB_AjxEnv.browser = "IE5";
		} else if (ZMTB_AjxEnv.isIE4) {
			ZMTB_AjxEnv.browser = "IE4";
		} else if (ZMTB_AjxEnv.isIE3) {
			ZMTB_AjxEnv.browser = "IE";
		}
		ZMTB_AjxEnv.platform = "";
		if (ZMTB_AjxEnv.isWindows) {
			ZMTB_AjxEnv.platform = "Win";
		} else if (ZMTB_AjxEnv.isMac) {
			ZMTB_AjxEnv.platform = "Mac";
		} else if (ZMTB_AjxEnv.isLinux) {
			ZMTB_AjxEnv.platform = "Linux";
		}
	}
	// setup some global setting we can check for high resolution
	if (ZMTB_AjxEnv.isIE){
		ZMTB_AjxEnv.isNormalResolution = true;
		ZMTB_AjxEnv.ieScaleFactor = screen.deviceXDPI / screen.logicalXDPI;
		if (ZMTB_AjxEnv.ieScaleFactor > 1) {
			ZMTB_AjxEnv.isNormalResolution = false;
		}
	}
	// show transparent PNGs on platforms that support them well
	//	(eg: all but IE and Linux)
	//	MOW: having trouble getting safari to render transparency for shadows, skipping there, too
	ZMTB_AjxEnv.useTransparentPNGs = !ZMTB_AjxEnv.isIE && !ZMTB_AjxEnv.isLinux && !ZMTB_AjxEnv.isSafari;
	ZMTB_AjxEnv._inited = !ZMTB_AjxEnv.isIE;

	// test for safari nightly
	// XXX: CHANGE ONCE OFFICIAL 420.x is released!
	var webkit = ZMTB_AjxEnv.isSafari ? ZMTB_AjxEnv.getWebkitVersion() : null;
	ZMTB_AjxEnv.isSafariNightly = webkit && webkit['is_nightly'];
};

// XXX: LAME code provided by the webkit dudes
ZMTB_AjxEnv.getWebkitVersion =
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

		webkit_version = { major:parseInt(bits[0]), minor:minor, is_nightly:is_nightly};
	}
	return {major: webkit_version['major'], minor: webkit_version['minor'], is_nightly: webkit_version['is_nightly']};
}

ZMTB_AjxEnv.reset();
ZMTB_AjxEnv.parseUA(navigator.userAgent);

// COMPATIBILITY

// Safari doesn't support string.replace(/regexp/, function);
if (ZMTB_AjxEnv.isSafari) {
	if (!String.prototype._AjxOldReplace) {
		String.prototype._AjxOldReplace = String.prototype.replace;
		String.prototype.replace = function(re, val) {
			if (typeof val != "function")
				return this._AjxOldReplace(re, val);
			else {
				// TODO: investigate if it's possible to use the array.join approach
				var str = this.slice(0), v, l, a;
				while (a = re.exec(str)) {
					v = val.apply(null, a);
					l = a[0].length;
					re.lastIndex -= l - v.length;
					str = str.substr(0, a.index) + v + str.substr(a.index + l);
					if (!re.global)
						break;
				}
				return str;
			}
		};
	}
}
