/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Zimlets
 * Copyright (C) 2006, 2007, 2008, 2009, 2010 Zimbra, Inc.
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
* Allows adding multiple objects in series and add different animations to them.
* For example: If you want to fade-in object1 and then slideIn objject2 after that,
* you can call: sa = new SeriesAnimation(); sa.addFadeIn(objId1); sa.addSlideIn(objId2); sa.startAnimation()
*/
function SeriesAnimation() {
	this._objsToAnimate = [];
	this._objIdAndAnimationTypeMap = [];
};

/**
* After adding various objects w/ animation spec, this will start that series of animations
*/
SeriesAnimation.prototype.startAnimation =
function() {
	this._animateNextItem();
};

/**
* Empties the series so we can start afresh
*/
SeriesAnimation.prototype.reset =
function() {
	this._objsToAnimate = [];
	this._objIdAndAnimationTypeMap = [];
};

/**
* Adds FadIn animation. You can set ending opacity to stop it from being 100% opacity
*/
SeriesAnimation.prototype.addFadeIn =
function(objId, endingOpac) {
	this._addObjs({objId:objId, aType:"FADEIN", endingOpac:endingOpac, millisecs:null});
};

/**
* Adds Pause to the animation series. Helps you if you want to stop the animation
*/
SeriesAnimation.prototype.addPause =
function(millisecs) {
	this._addObjs({objId:"__PAUSE__", aType:"PAUSE", endingOpac:null, millisecs:millisecs});
};

/**
*  Adds FadIn animation. You can set ending opacity to stop it from being 0% opacity
*/
SeriesAnimation.prototype.addFadeOut =
function(objId, endingOpac) {
	this._addObjs({objId:objId, aType:"FADEOUT", endingOpac:endingOpac, millisecs:null});
};

/**
*  Slowly increases the height of the object & goes from current height to whatever percent (100% if null)
*/
SeriesAnimation.prototype.addslideUp =
function(objId, endHeightPercent) {
	this._addObjs({objId:objId, aType:"SLIDEUP", endHeightPercent:endHeightPercent});
};

/**
*  Slowly decreases the height of the object & goes from current height to whatever percent (0% if null)
*/
SeriesAnimation.prototype.addslideDown =
function(objId, endHeightPercent) {
	this._addObjs({objId:objId, aType:"SLIDEDOWN", endHeightPercent:endHeightPercent});
};

/**
*  Increments item's size and height by exact number of pixels
*/
SeriesAnimation.prototype.addZoomIn =
function(objId, objWidth, objHeight, zoomByThisPixels) {
	this._addObjs({objId:objId, aType:"ZOOMIN", objWidth:objWidth, objHeight:objHeight, zoomByThisPixels:zoomByThisPixels});
};

/**
*@private
*/
SeriesAnimation.prototype._addObjs =
function(params) {
	var objId = params.objId;
	var aType = params.aType;
	//var obj = this._objIdAndAnimationTypeMap[objId + Dwt.getNextId()];
	//if(!obj || (obj && obj.aType != aType)) {//avoid adding duplicate objects
		var mapId =  objId +  Dwt.getNextId();
		this._objIdAndAnimationTypeMap[mapId] = params;
	//}
	this._objsToAnimate.push(mapId);
};

/**
*@private
*/
SeriesAnimation.prototype._animate =
function(obj) {
	var objId = obj.objId;
	var aType = obj.aType;
	if(objId.indexOf("__PAUSE__") == -1 && !document.getElementById(objId)) {
		this.reset();
		return;
	}
	if(objId.indexOf("__PAUSE__") == -1) {
		document.getElementById(objId).style.display= "block";
	}	
	if(aType == "FADEOUT") {
		var endingOpac = obj.endingOpac ? obj.endingOpac : 0;
		this._animateOpacity(objId, 100, endingOpac, 500);
	} else if(aType == "PAUSE") {
		this._animatePause(obj.millisecs);
	} else if(aType == "FADEIN"){
		var endingOpac = obj.endingOpac ? obj.endingOpac : 100;
		this._animateOpacity(objId, 0, endingOpac, 500);
	} else if(aType == "SLIDEUP"){
		var endHeightPercent = obj.endHeightPercent ? obj.endHeightPercent : 100;
		this._animateSliding(objId, endHeightPercent, aType, 500);
	} else if(aType == "SLIDEDOWN"){
		var endHeightPercent = obj.endHeightPercent ? obj.endHeightPercent : 0;
		this._animateSliding(objId, endHeightPercent, aType, 500);
	} else if(aType == "ZOOMIN"){
		var zoomByThisPixels = obj.zoomByThisPixels ? obj.zoomByThisPixels : 0;
		this._animateZoomIn(obj);
	}
};

/**
*@private
*/
SeriesAnimation.prototype._animateZoomIn =
function(obj) {
	var el = document.getElementById(obj.objId);
	var styleObj = el.style;
	
	var width = obj.objWidth;
	var height = obj.objHeight;
	var zoomByThisPixels = obj.zoomByThisPixels;
	var speed = 2;
	var timer = 0;
	for (i = 0; i < zoomByThisPixels; i++) {
		if(i == zoomByThisPixels -1) {
			setTimeout(AjxCallback.simpleClosure(this._zoomInItem, this, styleObj, (width + i), (height + i), true), (timer * speed));
		} else {
			setTimeout(AjxCallback.simpleClosure(this._zoomInItem, this, styleObj, (width + i), (height + i), false), (timer * speed));
		}
		timer++;
	}
};

/**
*@private
*/
SeriesAnimation.prototype._zoomInItem =
function(styleObj, width, height, startNext) {
	styleObj.width = width; 
	styleObj.height = height; 
	if(startNext) {
		this._animateNextItem();
	}
};

/**
*@private
*/
SeriesAnimation.prototype._animateSliding =
function(objId, endHeightPercent, aType, millsec) {
	var speed = Math.round(millsec / 100);
	var timer = 0;
	var styleObj = document.getElementById(objId).style;
	var currentHeight = Math.round(document.getElementById(objId).offsetHeight);

	if(aType == "SLIDEUP") {
		for (i = currentHeight; i <= endHeightPercent; i++) {
			if(i == endHeightPercent) {
				setTimeout(AjxCallback.simpleClosure(this._changeHeight, this, i, styleObj, true), (timer * speed));
			} else {
				setTimeout(AjxCallback.simpleClosure(this._changeHeight, this, i, styleObj, false), (timer * speed));
			}

			timer++;
		}
	} else if (aType == "SLIDEDOWN") {
		for (i = currentHeight; i >= endHeightPercent; i--) {
			if(i == endHeightPercent) {
				setTimeout(AjxCallback.simpleClosure(this._changeHeight, this, i, styleObj, true), (timer * speed));
			} else {
				setTimeout(AjxCallback.simpleClosure(this._changeHeight, this, i, styleObj, false), (timer * speed));
			}
			timer++;
		}
	}	
};

/**
*@private
*/
SeriesAnimation.prototype._animateOpacity =
function(id, opacStart, opacEnd, millisec) {
	var div = document.getElementById(id);
	if(!div) {
		this.reset();
		return;
	}
	this._changeOpac(opacStart, div.style);//create a starting point
	//speed for each frame
	var speed = Math.round(millisec / 100);
	var timer = 0;
	var styleObj = div.style;
	//determine the direction for the blending, if start and end are the same nothing happens
	if (opacStart > opacEnd) {
		for (i = opacStart; i >= opacEnd; i--) {
			if(i == opacEnd) {
				setTimeout(AjxCallback.simpleClosure(this._changeOpac, this, i, styleObj, true), (timer * speed));
			} else {
				setTimeout(AjxCallback.simpleClosure(this._changeOpac, this, i, styleObj, false), (timer * speed));
			}

			timer++;
		}
	} else if (opacStart < opacEnd) {
		for (i = opacStart; i <= opacEnd; i++) {
			if(i == opacEnd) {
				setTimeout(AjxCallback.simpleClosure(this._changeOpac, this, i, styleObj, true), (timer * speed));
			} else {
				setTimeout(AjxCallback.simpleClosure(this._changeOpac, this, i, styleObj, false), (timer * speed));
			}
			timer++;
		}
	}
};

/**
*@private
*/
SeriesAnimation.prototype._changeHeight =
function(height, styleObj, startNext) {
	styleObj.height = height;
	if(startNext) {
		this._animateNextItem();
	}
};


/**
 * Change the opacity for different browsers
 */
SeriesAnimation.prototype._changeOpac =
function(opacity, styleObj, startNext) {
	if(!styleObj) {
		return;
	}
	styleObj.opacity = (opacity / 100);
	styleObj.MozOpacity = (opacity / 100);
	styleObj.KhtmlOpacity = (opacity / 100);
	styleObj.zoom = 1;
	styleObj.filter = "progid:DXImageTransform.Microsoft.Alpha(opacity=" + opacity + ")";
	if(startNext) {
		this._animateNextItem();
	}
};

/**
*@private
*/
SeriesAnimation.prototype._animateNextItem =
function() {
		if(this._objsToAnimate.length == 0) {
			return;
		}
		var mapId = this._objsToAnimate[0];
		var obj = this._objIdAndAnimationTypeMap[mapId];
		this._objsToAnimate.splice(0,1);
		this._animate(obj);
};

/**
*@private
*/
SeriesAnimation.prototype._animatePause =
function(millisecs) {
	setTimeout(AjxCallback.simpleClosure(this._animateNextItem, this), millisecs);
};