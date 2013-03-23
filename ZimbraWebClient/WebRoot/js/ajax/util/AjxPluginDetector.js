/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2007, 2008, 2009, 2010, 2011 VMware, Inc.
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
 * Does nothing (static class).
 * @constructor
 * @class
 * 
 * This class provides static methods to determine which standard plugins are
 * installed in the browser.
 *
 * @private
 */
AjxPluginDetector = function() {
}

AjxPluginDetector.canDetectPlugins =
function() {
	return AjxEnv.isIE || (navigator.plugins && navigator.plugins.length > 0);
};

AjxPluginDetector.detectFlash =
function() {
	if(AjxEnv.isIE) {
		return AjxPluginDetector.detectActiveXControl('ShockwaveFlash.ShockwaveFlash.1');
	} else {
		return AjxPluginDetector.detectPlugin('Shockwave','Flash'); 
	}
};

AjxPluginDetector.detectPDFReader =
function(){
    if(AjxEnv.isIE){
        return  ( AjxPluginDetector.detectActiveXControl('PDF.PdfCtrl.5')
                || AjxPluginDetector.detectActiveXControl('AcroExch.Document') );
    }else{
        var hasPDFReader = false;
        if(AjxEnv.isChrome){
            hasPDFReader = AjxPluginDetector.detectPlugin('Chrome PDF Viewer');
        }else if(AjxEnv.isFirefox){
            hasPDFReader = AjxPluginDetector.detectPlugin('Firefox PDF Plugin for Mac OS X');
        }
        if(!hasPDFReader){
            hasPDFReader = AjxPluginDetector.detectPlugin('Adobe Acrobat');
        }
        return hasPDFReader;
    }
};

AjxPluginDetector.detectDirector =
function() { 
	if(AjxEnv.isIE) {
		return AjxPluginDetector.detectActiveXControl('SWCtl.SWCtl.1');
	} else {
		return AjxPluginDetector.detectPlugin('Shockwave','Director');
	}
};

AjxPluginDetector.detectQuickTime =
function() {
	if(AjxEnv.isIE) {
		return AjxPluginDetector.detectQuickTimeActiveXControl();
	} else {
		return AjxPluginDetector.detectPlugin('QuickTime');
	}
};

// If quicktime is installed, returns the version as an array: [major, minor, build]
AjxPluginDetector.getQuickTimeVersion =
function() {
	if(AjxEnv.isIE) {
		var object = new ActiveXObject("QuickTimeCheckObject.QuickTimeCheck.1");
		DBG.println(AjxDebug.DBG1, "AjxPluginDetector: Quicktime is " + object.IsQuickTimeAvailable(0) ? "available" : "not available");
		if (object.IsQuickTimeAvailable(0)) {
			try {
				var version = Number(object.QuickTimeVersion).toString(16);
				var result = [];
				for(var i = 0; i < 3; i++) {
					result[i] = Number(version.charAt(i));
				}
				return result;
			} catch(e) {
				DBG.println(AjxDebug.DBG1, "AjxPluginDetector: Error while checking QuickTimeVersion: " + e);
			}
		}
		return null;
	} else {
		var match = AjxPluginDetector.matchPluginName(/QuickTime Plug-in (\d+)\.?(\d+)?\.?(\d+)?/);
		if (match) {
			DBG.println("AjxPluginDetector: able to find match for QuickTime plugin with version: " + match);
			var result = [];
			for(var i = 0; i < 3; i++) {
				result[i] = Number(match[i + 1] || 0);
			}
			return result;
		} else {
			DBG.println("AjxPluginDetector: unable to find match for QuickTime plugin with version");
			return null;
		}
	}
};

/**
 * This code is part of JQuery's Flash plugin.
 * http://jquery.lukelutman.com/plugins/flash/
 *
 * @return Flash plugin version
 */
AjxPluginDetector.getFlashVersion =
function() {
    var flashVersion = "0,0,0";
    // ie
    try {
        try {
            // avoid fp6 minor version lookup issues
            // see: http://blog.deconcept.com/2006/01/11/getvariable-setvariable-crash-internet-explorer-flash-6/
            var axo = new ActiveXObject('ShockwaveFlash.ShockwaveFlash.6');
            try {
                axo.AllowScriptAccess = 'always';
            }
            catch(e) {
                return '6,0,0';
            }
        } catch(e) {
            }
        flashVersion = new ActiveXObject('ShockwaveFlash.ShockwaveFlash').GetVariable('$version').replace(/\D+/g, ',').match(/^,?(.+),?$/)[1];
        // other browsers
    } catch(e) {
        try {
            if (navigator.mimeTypes["application/x-shockwave-flash"].enabledPlugin) {
                flashVersion = (navigator.plugins["Shockwave Flash 2.0"] || navigator.plugins["Shockwave Flash"]).description.replace(/\D+/g, ",").match(/^,?(.+),?$/)[1];
            }
        } catch(e) {
        }
    }
	return flashVersion;
};

AjxPluginDetector.detectReal =
function() {
	if(AjxEnv.isIE) {
		return AjxPluginDetector.detectActiveXControl('rmocx.RealPlayer G2 Control') ||
		       AjxPluginDetector.detectActiveXControl('RealPlayer.RealPlayer(tm) ActiveX Control (32-bit)') ||
		       AjxPluginDetector.detectActiveXControl('RealVideo.RealVideo(tm) ActiveX Control (32-bit)');
	} else {
		return AjxPluginDetector.detectPlugin('RealPlayer');
	}
};

AjxPluginDetector.detectWindowsMedia =
function() {
	if(AjxEnv.isIE) {
		return AjxPluginDetector.detectActiveXControl('MediaPlayer.MediaPlayer.1');
	} else {
		return AjxPluginDetector.detectPlugin('Windows Media');
	}
};

AjxPluginDetector.detectPlugin =
function() {
	DBG.println(AjxDebug.DBG1, "-----------------------<br>AjxPluginDetector: Looking for plugin: [" + AjxPluginDetector._argumentsToString(AjxPluginDetector.detectPlugin.arguments) + "]");
	var names = AjxPluginDetector.detectPlugin.arguments;
	var allPlugins = navigator.plugins;
	var pluginsArrayLength = allPlugins.length;
	for (var pluginsArrayCounter=0; pluginsArrayCounter < pluginsArrayLength; pluginsArrayCounter++ ) {
	    // loop through all desired names and check each against the current plugin name
	    var numFound = 0;
	    for(var namesCounter=0; namesCounter < names.length; namesCounter++) {
			// if desired plugin name is found in either plugin name or description
			if (allPlugins[pluginsArrayCounter]) {
				if( (allPlugins[pluginsArrayCounter].name.indexOf(names[namesCounter]) >= 0)) {
					// this name was found
					DBG.println(AjxDebug.DBG1, "AjxPluginDetector: found name match '" + allPlugins[pluginsArrayCounter].name + "'");
					numFound++;
				} else if (allPlugins[pluginsArrayCounter].description.indexOf(names[namesCounter]) >= 0) {
					// this name was found
					DBG.println(AjxDebug.DBG1, "AjxPluginDetector: found description match '" + allPlugins[pluginsArrayCounter].description + "'");
					numFound++;
				}
			}
	    }
	    // now that we have checked all the required names against this one plugin,
	    // if the number we found matches the total number provided then we were successful
	    if(numFound == names.length) {
			DBG.println(AjxDebug.DBG1, "AjxPluginDetector: Found plugin!<br>-----------------------");
			return true;
	    } else if (numFound) {
			DBG.println(AjxDebug.DBG1, "AjxPluginDetector: Found partial plugin match, numFound=" + numFound);
		}
	}
	DBG.println(AjxDebug.DBG1, "AjxPluginDetector: Failed to find plugin.<br>-----------------------");
	return false;
};

AjxPluginDetector.matchPluginName =
function(regExp) {
	var allPlugins = navigator.plugins;
	var pluginsArrayLength = allPlugins.length;
	for (var pluginsArrayCounter=0; pluginsArrayCounter < pluginsArrayLength; pluginsArrayCounter++ ) {
		var match = allPlugins[pluginsArrayCounter].name.match(regExp);
		if (match) {
			return match;
		}
	}
	return null;
};

AjxPluginDetector.detectActiveXControl =
function(progId) {
	try {
		new ActiveXObject(progId);
		DBG.println(AjxDebug.DBG1, "AjxPluginDetector: found ActiveXObject '" + progId + "'");
		return true;
	} catch (e) {
		DBG.println(AjxDebug.DBG1, "AjxPluginDetector: unable to find ActiveXObject '" + progId + "'");
		return false;
	}
};

AjxPluginDetector.detectQuickTimeActiveXControl =
function(progId) {
	try {
		var object = new ActiveXObject("QuickTimeCheckObject.QuickTimeCheck.1");
		return object.IsQuickTimeAvailable(0);
	} catch (e) {
		return false;
	}
};

// Util method to log arguments, which to my surprise are not actually an array.
AjxPluginDetector._argumentsToString =
function(args) {
	var array = [];
	for (var i = 0, count = args.length; i < count; i++) {
		array[i] = args[i];
	}
	return array.join(',')
};