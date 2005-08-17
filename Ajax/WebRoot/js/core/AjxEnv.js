function LsEnv() {
}

LsEnv._inited = false;


LsEnv.reset = function () {
	LsEnv.browserVersion = -1;
	LsEnv.geckoDate = 0;
	LsEnv.mozVersion = -1;
	LsEnv.isMac = false;
	LsEnv.isWindows = false;
	LsEnv.isLinux = false;
	LsEnv.isNav  = false;
	LsEnv.isIE = false;
	LsEnv.isNav4 = false;
	LsEnv.trueNs = true;
	LsEnv.isNav6 = false;
	LsEnv.isNav6up = false;
	LsEnv.isNav7 = false;
	LsEnv.isIE3 = false;
	LsEnv.isIE4 = false;
	LsEnv.isIE4up = false;
	LsEnv.isIE5 = false;
	LsEnv.isIE5_5 = false;
	LsEnv.isIE5up = false;
	LsEnv.isIE5_5up = false;
	LsEnv.isIE6  = false;
	LsEnv.isIE6up = false;
	LsEnv.isNormalResolution = false;
	LsEnv.ieScaleFactor = 1;
	LsEnv.isFirefox = false;
	LsEnv.isFirefox1up = false;
	LsEnv.isMozilla = false;
	LsEnv.isMozilla1_4up = false;
	LsEnv.isSafari = false;
	LsEnv.isGeckoBased = false;
	LsEnv.isOpera = false;
};

LsEnv.parseUA = function (userAgent) {
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
			LsEnv.browserVersion = parseFloat(agtArr[0].substring(index + 8));
			LsEnv.isNav = true;
		}
		for ( ; i < agtArr.length; ++i ){
			token = agtArr[i];
			if (token.indexOf('compatible') != -1 ) {
				isCompatible = true;
				LsEnv.isNav = false;
			} else if ((token.indexOf('opera')) != -1){
				LsEnv.isOpera = true;
				LsEnv.isNav = false;
				LsEnv.browserVersion = parseFloat(agtArr[i+1]);
			} else if ((token.indexOf('spoofer')) != -1){
				isSpoofer = true;
				LsEnv.isNav = false;
			} else if ((token.indexOf('webtv')) != -1) {
				isWebTv = true;
				LsEnv.isNav = false;
			} else if ((token.indexOf('hotjava')) != -1) {
				isHotJava = true;
				LsEnv.isNav = false;
			} else if ((index = token.indexOf('msie')) != -1) {
				LsEnv.isIE = true;
				LsEnv.browserVersion = parseFloat(agtArr[i+1]);
			} else if ((index = token.indexOf('gecko/')) != -1){
				LsEnv.isGeckoBased = true;
				LsEnv.geckoDate = parseFloat(token.substr(index + 6));
			} else if ((index = token.indexOf('rv:')) != -1){
				LsEnv.mozVersion = parseFloat(token.substr(index + 3));
				LsEnv.browserVersion = LsEnv.mozVersion;
			} else if ((index = token.indexOf('firefox/')) != -1){
				LsEnv.isFirefox = true;
				LsEnv.browserVersion = parseFloat(token.substr(index + 8));
			} else if ((index = token.indexOf('netscape6/')) != -1){
				LsEnv.trueNs = true;
				LsEnv.browserVersion = parseFloat(token.substr(index + 10));
			} else if ((index = token.indexOf('netscape/')) != -1){
				LsEnv.trueNs = true;
				LsEnv.browserVersion = parseFloat(token.substr(index + 9));
			} else if ((index = token.indexOf('safari/')) != -1){
				LsEnv.isSafari = true;
				LsEnv.browserVersion = parseFloat(token.substr(index + 7));
			} else if (token.indexOf('windows') != -1){
				LsEnv.isWindows = true;
			} else if ((token.indexOf('macintosh') != -1) ||
					   (token.indexOf('mac_') != -1)){
				LsEnv.isMac = true;
			} else if (token.indexOf('linux') != -1){
				LsEnv.isLinux = true;
			}
		}
		// Note: Opera and WebTV spoof Navigator.  
		// We do strict client detection.
		LsEnv.isNav  = (beginsWithMozilla && !isSpoofer && !isCompatible && 
						!LsEnv.isOpera && !isWebTv && !isHotJava &&
						!LsEnv.isSafari);

		LsEnv.isIE = (LsEnv.isIE && !LsEnv.isOpera);

		LsEnv.isNav4 = (LsEnv.isNav && (LsEnv.browserVersion  == 4) &&
						(!LsEnv.isIE));
		LsEnv.isNav6 = (LsEnv.isNav && LsEnv.trueNs && 
						(LsEnv.browserVersion >=6.0) && 
						(LsEnv.browserVersion < 7.0));
		LsEnv.isNav6up = (LsEnv.isNav && LsEnv.trueNs && 
						  (LsEnv.browserVersion >= 6.0));
		LsEnv.isNav7 = (LsEnv.isNav && LsEnv.trueNs && 
						(LsEnv.browserVersion == 7.0));

		LsEnv.isIE3 = (LsEnv.isIE && (LsEnv.browserVersion < 4));
		LsEnv.isIE4 = (LsEnv.isIE && (LsEnv.browserVersion == 4) && 
					 (LsEnv.browserVersion == 4.0));
		LsEnv.isIE4up = (LsEnv.isIE && (LsEnv.browserVersion >= 4));
		LsEnv.isIE5 = (LsEnv.isIE && (LsEnv.browserVersion == 4) && 
					 (LsEnv.browserVersion == 5.0));
		LsEnv.isIE5_5 = (LsEnv.isIE && (LsEnv.browserVersion == 4) && 
						 (LsEnv.browserVersion == 5.5));
		LsEnv.isIE5up = (LsEnv.isIE && (LsEnv.browserVersion >= 5.0));
		LsEnv.isIE5_5up =(LsEnv.isIE && (LsEnv.browserVersion >= 5.5));
		LsEnv.isIE6  = (LsEnv.isIE && (LsEnv.browserVersion == 6.0));
		LsEnv.isIE6up = (LsEnv.isIE && (LsEnv.browserVersion >= 6.0));

		LsEnv.isMozilla = ((LsEnv.isNav && LsEnv.mozVersion && 
							LsEnv.isGeckoBased && (LsEnv.geckoDate != 0)));
		LsEnv.isMozilla1_4up = (LsEnv.isMozilla && (LsEnv.mozVersion >= 1.4));
		LsEnv.isFirefox = ((LsEnv.isMozilla && LsEnv.isFirefox));
		LsEnv.isFirefox1up = (LsEnv.isFirefox && LsEnv.browserVersion >= 1.0);

	}
	// setup some global setting we can check for high resolution
	if (LsEnv.isIE){
		LsEnv.isNormalResolution = true;
		LsEnv.ieScaleFactor = screen.deviceXDPI / screen.logicalXDPI;
		if (LsEnv.ieScaleFactor > 1) {
			LsEnv.isNormalResolution = false;
		}
	}
	LsEnv._inited = true;
};

LsEnv.reset();
LsEnv.parseUA(navigator.userAgent);

