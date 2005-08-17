/*
 * (C) Copyright 2002-2004, Andy Clark.  All rights reserved.
 *
 * This file is distributed under an Apache style license. Please
 * refer to the LICENSE file for specific details.
 */

/**
 * Most of this code is taken driectly from the java implementation written
 * by Roland.
 */
 
/**
 * @author schemers@liquidsys.com
 * 
 * very Mutated version of ElementRemover.java filter from cyberneko html.
 * change accepted/removed elements to static hashmaps for one-time 
 * initialization, switched from Hashtable to HashMap, sanatize
 * attributes, etc. 
 * 
 * TODO: more checks:
 * allow limited use of <meta> tags? like for Content-Type?
 * make sure any clicked links pop up in new window 
 * figure out how to block images by default, and how to re-enable them. styles?  
 * strict attr value checking?
 *  don't allow id attr in tags if we aren't putting html into an iframe (I'm assuming we are, and id's in iframes don't conflict with iframes elsewhere)
 * 
 *  
 * MAYBE:
 *  allow style but strip out /url(.*)/? Might have other reasons to leave it 
 * 
 */
LsDefangFilter._inited = false;
function LsDefangFilter(neuterImages) {
	this.mNeuterImages = neuterImages;
	if (!LsDefangFilter._inited) {
		LsDefangFilter._inited = true;
		LsDefangFilter._init();
	}
}

/**
 * enable tags dealing with input ( select, input ...)
 */
LsDefangFilter.ENABLE_INPUT_TAGS = true;
    
/**
 * enable table tags
 */
LsDefangFilter.ENABLE_TABLE_TAGS = true;

/**
 * enable phrase tags (EM, STRONG, CITE, DFN, CODE, SAMP, KBD, VAR, ABBR, ACRONYM)
 */
LsDefangFilter.ENABLE_PHRASE_TAGS = true;

/**
 * enable list tags (UL, OL, LI, DL, DT, DD, DIR, MENU)
 */
LsDefangFilter.ENABLE_LIST_TAGS = true;

/**
 * enable font style tags (TT, I, B, BIG, SMALL, STRIKE, S, U) 
 */
LsDefangFilter.ENABLE_FONT_STYLE_TAGS = true;

//
// Constants
//

/** A "null" object. */
LsDefangFilter.NULL = new Object();

// regexes inside of attr values to strip out
LsDefangFilter.AV_JS_ENTITY = new RegExp().compile("&\\{[^}]*\\}");
LsDefangFilter.AV_JS_COLON = new RegExp().compile("script:","gi");
LsDefangFilter.AV_SCRIPT_TAG = new RegExp().compile("</?script/?>","i");
LsDefangFilter.TAG_REGEX = /<([a-zA-Z0-9]+)[\s]*[^>]*>/gi;
LsDefangFilter.ATTR_REGEX = /([a-zA-Z]+)\s*=\s*([^\s^\"^\']*) | ([a-zA-Z]+)\s*=\s*[\"\']([^\"^\']*)[\"\']/gi;
LsDefangFilter.ID_REGEX = /id\s*=\s*[\"\']?([^\s^\"^\']*)[\"\']?/i;
LsDefangFilter.SRC_REGEX = /src\s*=\s*[\"\']?([^\s^\"^\']*)[\"\']?/i;
LsDefangFilter.TARGET_REGEX = /target\s*=\s*[\"\']?([^\s^\"^\']*)[\"\']?/i;

// regex for URLs href. TODO: beef this up
LsDefangFilter.ABSOLUTE_URL = new RegExp("^(https?://[\\w-]|mailto:).*", "i");

//
// Data
//

// information

/** attr Set cache */
LsDefangFilter.mAttrSetCache = new Object();

/** Accepted elements. */
LsDefangFilter.mAcceptedElements = new Object();

/** Removed elements. */
LsDefangFilter.mRemovedElements = new Object();

// don't allow style
LsDefangFilter.CORE = "id,class,title,";
LsDefangFilter.LANG = "dir,lang,xml:lang,language";
LsDefangFilter.CORE_LANG = LsDefangFilter.CORE+LsDefangFilter.LANG;
LsDefangFilter.KBD = "accesskey,tabindex,";

LsDefangFilter._init = function () {
	var CORE = LsDefangFilter.CORE;
	var LANG = LsDefangFilter.LANG;
	var CORE_LANG = LsDefangFilter.CORE_LANG;
	var KBD = LsDefangFilter.KBD;
	// set which elements to accept
	LsDefangFilter.acceptElement("a", CORE+KBD+",charset,coords,href,hreflang,name,rel,rev,shape,target,type");
	LsDefangFilter.acceptElement("address", CORE_LANG);
	//LsDefangFilter.acceptElement("base", "href,target");
	LsDefangFilter.acceptElement("bdo", CORE_LANG);
	LsDefangFilter.acceptElement("blockquote", CORE_LANG+"cite");
	LsDefangFilter.acceptElement("body", CORE_LANG+"alink,background,bgcolor,link,text,vlink");
	LsDefangFilter.acceptElement("br", CORE);
	LsDefangFilter.acceptElement("center", CORE_LANG);
	LsDefangFilter.acceptElement("del", CORE_LANG+"cite,datetime");
	LsDefangFilter.acceptElement("div", CORE_LANG+"align");
	LsDefangFilter.acceptElement("head", LANG); // profile attr removed
	LsDefangFilter.acceptElement("h1", CORE_LANG+"align");
	LsDefangFilter.acceptElement("h2", CORE_LANG+"align");
	LsDefangFilter.acceptElement("h3", CORE_LANG+"align");
	LsDefangFilter.acceptElement("h4", CORE_LANG+"align");
	LsDefangFilter.acceptElement("h5", CORE_LANG+"align");
	LsDefangFilter.acceptElement("h6", CORE_LANG+"align");
	LsDefangFilter.acceptElement("hr", CORE_LANG+"align,noshade,size,width");
	LsDefangFilter.acceptElement("html", LANG+"xlmns");
	LsDefangFilter.acceptElement("document", CORE_LANG+"language,datafld");
	LsDefangFilter.acceptElement("img", CORE_LANG+"align,alt,border,height,hspace,ismap,longdesc,src,usemap,vspace,width");
	LsDefangFilter.acceptElement("ins", CORE_LANG+"cite");
	LsDefangFilter.acceptElement("label", CORE_LANG+"for");
	//LsDefangFilter.acceptElement("link", CORE_LANG+"charset,href,hreflang,media,ntarget,rel,rev,type");
	
	// NOTE: comment out noframes so its text shows up, since we are nuke frame-related tags
	//LsDefangFilter.acceptElement("noframes", CORE_LANG);
	// NOTE: comment out noscript so its text shows up, since we are nuking script tags
	//LsDefangFilter.acceptElement("noscript", CORE_LANG); // maybe convert to always execute if we are stripping script?
	LsDefangFilter.acceptElement("p", CORE_LANG+"align");
	LsDefangFilter.acceptElement("pre", CORE_LANG+"width");
	LsDefangFilter.acceptElement("q", CORE_LANG+"cite");
	LsDefangFilter.acceptElement("span", CORE_LANG);
	
	// style removed. TODO: see if we can safely include it or not, maybe by sanatizing
	LsDefangFilter.acceptElement("sub",  CORE_LANG);
	LsDefangFilter.acceptElement("sup",  CORE_LANG);
	
	//LsDefangFilter.acceptElement("title", CORE_LANG);
	LsDefangFilter.acceptElement("title", "");
	LsDefangFilter.acceptElement("iframe", CORE_LANG+"width,height,top,left");
	
	if (LsDefangFilter.ENABLE_FONT_STYLE_TAGS) {
		LsDefangFilter.acceptElement("b",  CORE_LANG);
		LsDefangFilter.acceptElement("basefont", CORE_LANG+"color,face,size");
		LsDefangFilter.acceptElement("big", CORE_LANG);
		LsDefangFilter.acceptElement("font", CORE_LANG+"color,face,size");
		LsDefangFilter.acceptElement("i", CORE_LANG);
		LsDefangFilter.acceptElement("s", CORE_LANG);
		LsDefangFilter.acceptElement("small", CORE_LANG);
		LsDefangFilter.acceptElement("strike", CORE_LANG);
		LsDefangFilter.acceptElement("tt", CORE_LANG);
		LsDefangFilter.acceptElement("u", CORE_LANG);
	} else {
		// allow the text, just strip the tags
	}
	
	if (LsDefangFilter.ENABLE_LIST_TAGS) {
		LsDefangFilter.acceptElement("dir", CORE_LANG+"compact");
		LsDefangFilter.acceptElement("dl", CORE_LANG);
		LsDefangFilter.acceptElement("dt", CORE_LANG);
		LsDefangFilter.acceptElement("li", CORE_LANG+"type,value");
		LsDefangFilter.acceptElement("ol", CORE_LANG+"compact,start,type");
		LsDefangFilter.acceptElement("ul", CORE_LANG+"compact,type");
		LsDefangFilter.acceptElement("dd", CORE_LANG);
		LsDefangFilter.acceptElement("menu", CORE_LANG+"compact");
	} else {
		// allow the text, just strip the tags
	}
	
	if (LsDefangFilter.ENABLE_PHRASE_TAGS) {
		LsDefangFilter.acceptElement("abbr", CORE_LANG);
		LsDefangFilter.acceptElement("acronym", CORE_LANG);
		LsDefangFilter.acceptElement("cite", CORE_LANG);
		LsDefangFilter.acceptElement("code", CORE_LANG);
		LsDefangFilter.acceptElement("dfn", CORE_LANG);
		LsDefangFilter.acceptElement("em", CORE_LANG);
		LsDefangFilter.acceptElement("kbd", CORE_LANG);
		LsDefangFilter.acceptElement("samp", CORE_LANG);
		LsDefangFilter.acceptElement("strong", CORE_LANG);
		LsDefangFilter.acceptElement("var", CORE_LANG);
	} else {
		// allow the text, just strip the tags
	}
	
	if (LsDefangFilter.ENABLE_TABLE_TAGS) {
		LsDefangFilter.acceptElement("caption", CORE_LANG+"align");
		LsDefangFilter.acceptElement("col",CORE_LANG+"alink,char,charoff,span,valign,width");
		LsDefangFilter.acceptElement("colgroup", CORE_LANG+"alink,char,charoff,span,valign,width");
		LsDefangFilter.acceptElement("table", CORE_LANG+"align,bgcolor,border,cellpadding,cellspacing,frame,rules,summary,width");
		LsDefangFilter.acceptElement("tbody", CORE_LANG+"align,char,charoff,valign");
		LsDefangFilter.acceptElement("td", CORE_LANG+"abbr,align,axis,bgcolor,char,charoff,colspan,headers,height,nowrap,rowspan,scope,,valign,width");
		LsDefangFilter.acceptElement("tfoot", CORE_LANG+"align,char,charoff,valign");
		LsDefangFilter.acceptElement("th", CORE_LANG+"abbr,align,axis,bgcolor,char,charoff,colspan,headers,height,nowrap,rowspan,scope,valign,width");
		LsDefangFilter.acceptElement("thead", CORE_LANG+"align,char,charoff,valign");
		LsDefangFilter.acceptElement("tr", CORE_LANG+"align,bgcolor,char,charoff,valign,height,width");
	} else {
		// allow the text, just strip the tags
	}
	
	
	if (LsDefangFilter.ENABLE_INPUT_TAGS) {
		LsDefangFilter.acceptElement("area", CORE_LANG+KBD+"alt,coords,href,nohref,shape,target");
		LsDefangFilter.acceptElement("button", CORE_LANG+KBD+"disabled,name,type,value");
		LsDefangFilter.acceptElement("fieldset", CORE_LANG);
		LsDefangFilter.acceptElement("form", CORE_LANG+"action,accept,acceptcharset,enctype,method,name,target");
		LsDefangFilter.acceptElement("input", CORE_LANG+"accept,align,alt,checked,disabled,maxlength,name,readonly,size,src,type,value");
		LsDefangFilter.acceptElement("legend", CORE_LANG+"align");
		LsDefangFilter.acceptElement("map", CORE_LANG+"name");
		LsDefangFilter.acceptElement("optgroup", CORE_LANG+"disabled,label");
		LsDefangFilter.acceptElement("option", CORE_LANG+KBD+"disabled,label,selected,value");
		LsDefangFilter.acceptElement("select", CORE_LANG+KBD+"disabled,multiple,name,size");
		LsDefangFilter.acceptElement("textarea", CORE_LANG+"cols,disabled,name,readonly,rows");
	} else {
		LsDefangFilter.removeElement("area");
		LsDefangFilter.removeElement("button");
		LsDefangFilter.removeElement("fieldset");
		LsDefangFilter.removeElement("form");
		LsDefangFilter.removeElement("input");
		LsDefangFilter.removeElement("legend");
		LsDefangFilter.removeElement("map");
		LsDefangFilter.removeElement("optgroup");
		LsDefangFilter.removeElement("option");
		LsDefangFilter.removeElement("select");
		LsDefangFilter.removeElement("textarea");
	}
	
	// completely remove these elements and all enclosing tags/text
	LsDefangFilter.removeElement("applet");
	LsDefangFilter.removeElement("frame");
	LsDefangFilter.removeElement("frameset");
	LsDefangFilter.removeElement("iframe");
	LsDefangFilter.removeElement("object");
	LsDefangFilter.removeElement("script");
	LsDefangFilter.removeElement("style");
	
	// don't remove "content" of these tags since they have none.
	//LsDefangFilter.removeElement("meta");
	//LsDefangFilter.removeElement("param");        
};

/**
 * @param neuterImages
 */

/** 
 * Specifies that the given element should be accepted and, optionally,
 * which attributes of that element should be kept.
 *
 * @param element The element to accept.
 * @param attributes The comma-seperated list of attributes to be kept or null if no
 *                   attributes should be kept for this element.
 *
 * see #removeElement
 */
LsDefangFilter.acceptElement = function (element, attributes) {
	element = element.toLowerCase();
	var set = LsDefangFilter.mAttrSetCache[attributes];
	if (set != null) {
		//System.out.println(element+" cached set "+set.size());
		LsDefangFilter.mAcceptedElements[element] = set;
		return;
	}
	set = new Object();
	var attrs = attributes.toLowerCase().split(",");
	if (attrs != null && attrs.length > 0) {
		for (var i=0; i < attrs.length; i++) {
			//System.out.println(element+"["+attrs[i]+"]");
			//deal with consecutive commas
			if (attrs[i].length > 0) {
				set[attrs[i]] = attrs[i];
			}
		}
	}
	LsDefangFilter.mAcceptedElements[element] = set;
	LsDefangFilter.mAttrSetCache[attributes] = set;
};

/** 
 * Specifies that the given element should be completely removed. If an
 * element is encountered during processing that is on the remove list, 
 * the element's start and end tags as well as all of content contained
 * within the element will be removed from the processing stream.
 *
 * @param element The element to completely remove.
 */
LsDefangFilter.removeElement = function (element) {
	var key = element.toLowerCase();
	var value = element;
	LsDefangFilter.mRemovedElements[key] = value;
}; // removeElement(String)

	

/** Returns true if the specified element is accepted. */
LsDefangFilter.prototype.elementAccepted = function (element) {
	var key = element.toLowerCase();
	return (LsDefangFilter.mAcceptedElements[key] != null);
}; // elementAccepted(String):boolean

/** Returns true if the specified element should be removed. */
LsDefangFilter.prototype.elementRemoved = function (element) {
	var key = element.toLowerCase();
	return (LsDefangFilter.mRemovedElements[key] != null);
}; // elementRemoved(String):boolean

/** Handles an element. */
LsDefangFilter.prototype.handleElement = function (element) {
	var rawName = element.tagName;
	var attributes = element.attributes
	if (this.elementAccepted(rawName)) {
		var eName = rawName.toLowerCase();
		var value = LsDefangFilter.mAcceptedElements[eName];
		if (value != null) {
			var anames = value;
			var removalArr = new Array();
			var attrCount = attributes.length;
			var i = 0;
			var aName = null;
			for (; i < attrCount; ++i) {
				aName = attributes[i].nodeName.toLowerCase();
				if (!anames[aName]) {
					removalArr.push(aName);
				} else {
					this.sanatizeAttrValue(eName, aName, element, i);
				}
			}
			for (i=0; i < removalArr.length; ++i){
				aName = removalArr.pop();
				if (LsEnv.isIE && aName.match(/^on/)){
					element[aName] = null;
				} else {
					element.removeAttribute(aName);
				}
			}
		} else {
			element.clearAttributes();
		}

		if (eName == "img" && this.mNeuterImages) {
			this.neuterImageTag(element);
		} else if (eName == "a") {
			this.fixATag(element);
		}

	} else if (this.elementRemoved(rawName)) {
		this._elementsForRemoval.push(element);
	}
}; // handleOpenTag(QName,XMLAttributes):boolean

LsDefangFilter.prototype.cleanHTML = function (dirtyHTML) {
	var re = LsDefangFilter.TAG_REGEX;
	re.lastIndex = 0;
	var cleanHTML = dirtyHTML;
	var tags = re.exec(dirtyHTML);
	var removalArray = new Array();
	var sanitizedArray = new Array();
	//LsLog.info("tags length = " + tags.length);
	var tag = null;
	// Loop through all the tags
	while (tags != null){
		var tagName = tags[1];
		var fullTag = tags[0];
		var eName = tagName.toLowerCase();
		//LsLog.info("Cleaning " +  tagName);
		// See if the tag is one we accept
		if (this.elementAccepted(tagName)) {
			var aNames = LsDefangFilter.mAcceptedElements[eName];
			if (aNames != null) {
				var newTag = fullTag;
				var attrRegex = LsDefangFilter.ATTR_REGEX;
				attrRegex.lastIndex = 0;
				var attrMatch = null;
				var attrName = null;
				var attrValue = null;
				// Parse all the attributes out of the tag, and iterate over
				// them
				while(attrMatch = attrRegex.exec(fullTag)){
					attrName = attrMatch[1]? attrMatch[1]: attrMatch[3];
					attrValue = attrMatch[2]? attrMatch[2]: attrMatch[4];
					//LsLog.info("  Looking at attr " + attrName  + " : " +
					//	attrValue);
					// If the attribute is not accepted, 
					// remove the whole attribute. If it is accepted, pass 
					// it through a filter to sanitize the content.
					if (!aNames[attrName]){
						// remove attribute
						//LsLog.info("    Remove single attr " + attrName);
						newTag = newTag.replace(attrMatch[0], "");
					} else {
						// sanitize
						//LsLog.info("    Sanitize: " + attrName + " val = " +
						//attrValue);
						var newVal = this.sanatizeAttrValueStr(eName, attrName,
															   fullTag, 
															   attrValue);
						if (newVal != attrValue){
							newTag = newTag.replace(attrMatch[0], " " + 
													attrName + "='" + 
													newVal + "' ");
						}
					}
				}
				// If we're on an image tag, remove the src attribute if
				// necessary. If it's an anchor, make the target a new 
				// window.
				if (eName == "img" && this.mNeuterImages) {
					//LsLog.info("  IMG TAG " + newTag);
					newTag = this.neuterImageTagStr(newTag);
				} else if (eName == "a") {
					newTag = this.fixATagStr(newTag);
				}
				
				// if the tag has changed, mark it for replacement
				if (fullTag != newTag){
					//LsLog.info("    Will sanitize " + fullTag + " with " + 
					//newTag);
					sanitizedArray[sanitizedArray.length] = {fullTag: fullTag,
															 newTag: newTag};
				}
			} else {

			}
		} else {
			//LsLog.info("Removing all attributes for " + fullTag);
			var newTag = "<" + tagName + ">";
			sanitizedArray[sanitizedArray.length] = {fullTag: fullTag,
													 newTag: newTag};
		}
		
		// if we've marked the tag for complete removal, find the end of 
		// the element, and mark it for removal. Make sure lastIndex of the
		// tags regex is set to the end of the section, so we don't look at
		// nested tags.
		if (this.elementRemoved(tagName)) {
			// find the end tag and remove everything in between
			var ere = new RegExp("/"+tagName,"i");
			var start = tags.index;
			var end = cleanHTML.indexOf("/" + tagName, tags.index);
			if (end != -1) {
				var close = cleanHTML.indexOf(">", end);
				end = end + 1 + tagName.length;
				if (close != -1){
					end = close + 1;
				}
				var element = cleanHTML.substring(start, end);
				//LsLog.info("Will remove " + element);
				removalArray[removalArray.length] = {element: element,
													 end: end,
													 start: start};
				// set the end of the pattern space to the end of the section
				// we just removed
				re.lastIndex = end;
			}			
		}
		var tags = re.exec(dirtyHTML);
	}
	var x = 0;
	for (; x < removalArray.length; ++x){
		var el = removalArray[x].element;
		cleanHTML = cleanHTML.replace(el, "");
	}

	for (x = 0; x < sanitizedArray.length; ++x) {
		var obj = sanitizedArray[x];
		var ft = obj.fullTag;
		var nt = obj.newTag;
		cleanHTML = cleanHTML.replace(ft, nt);
	}

	return cleanHTML;
};

/**
 * moves the src attribute to the id
 */
LsDefangFilter.prototype.neuterImageTagStr = function (element) {
	//LsLog.info("neuter Image tag for " + element);
	var idRegex = LsDefangFilter.ID_REGEX;
	var idMatch = idRegex.exec(element);
	//LsLog.info("  idMatch = " , idMatch);
	var srcRegex = LsDefangFilter.SRC_REGEX
	var srcMatch = srcRegex.exec(element);
	//LsLog.info("  srcMatch = " ,srcMatch);
	var src;
	var id;
	var retStr = null;
	if (srcMatch) {
		retStr = element;
		src = srcMatch[1];
		//LsLog.info("  src = " +src);
		if (idMatch) {
			//LsLog.info("  about to replace");
			id = idMatch[1];
			retStr = 
				retStr.replace(idMatch[0],idMatch[1]+"=\"" + src + "\"");
		} else {
			retStr = retStr.replace(">", " id='" + src + "'>");
		}
		retStr = retStr.replace(srcMatch[0], "src='' ");
	}
	//LsLog.info("Done neuter " + retStr);
	return retStr;
	
};

LsDefangFilter.prototype.neuterImageTag = function (element) {
	if (element.src){
		element.id = element.src;
		element.src = void 0;
	}
};

LsDefangFilter.prototype.fixATagStr = function (element) {
	var targetRegex = LsDefangFilter.TARGET_REGEX;
	var targetMatch = targetRegex.exec(element);
	var retStr = null;
	if (targetMatch) {
		retStr = element.replace(targetMatch[0], " target='_blank'");
	} else {
		retStr = element.replace(">", " target='_blank'>");
	}
	return retStr;
};

/**
 * make sure all <a> tags have a target="_blank" attribute set.
 * @param name
 * @param attributes
 */
LsDefangFilter.prototype.fixATag = function (element) {
	element.target = "_blank";
};

/**
 * sanatize an attr value. For now, this means stirpping out 
 * &{...} - Js entity tags
 * <script> tags.
 * *script: stuff from attributes ( eg <a href="javascript:alert()"></a> )
 * 
 */
LsDefangFilter.prototype.sanatizeAttrValue = function (eName, aName, element,i){
	var value = element.getAttribute(aName);
	value = value? value: element[aName];
	if (typeof(value) != 'string') return;

	if (value) {
		var result = value.replace(LsDefangFilter.AV_JS_ENTITY,
								   "JS-ENTITY-BLOCKED");
		result = result.replace(LsDefangFilter.AV_JS_COLON, "SCRIPT-BLOCKED");
		result = result.replace(LsDefangFilter.AV_SCRIPT_TAG,
								"SCRIPT-TAG-BLOCKED");
		// TODO: change to set?
		if (eName !="img" &&(aName == "href" || aName == "src" || 
							 aName == "longdesc" || aName == "usemap")){
			if (!result.search(LsDefangFilter.ABSOLUTE_URL)) {
				// TODO: just leave blank?
				result = "about:blank";
			}
		}

		if (result != value) {
			if (LsEnv.isIE){
				element[aName] = result;
			} else {
				element.setAttribute(aName, result);
			}
		}
	}
};

LsDefangFilter.prototype.sanatizeAttrValueStr = function (eName, aName,
														  element, value){
	if (typeof(value) != 'string') return;

	if (value) {
		var result = value.replace(LsDefangFilter.AV_JS_ENTITY,
								   "JS-ENTITY-BLOCKED");
		result = result.replace(LsDefangFilter.AV_JS_COLON, "SCRIPT-BLOCKED");
		result = result.replace(LsDefangFilter.AV_SCRIPT_TAG,
								"SCRIPT-TAG-BLOCKED");
		// TODO: change to set?
		if (eName !="img" &&(aName == "href" || aName == "src" || 
							 aName == "longdesc" || aName == "usemap")){
			if (!result.search(LsDefangFilter.ABSOLUTE_URL)) {
				// TODO: just leave blank?
				result = "about:blank";
			}
		}
	}
	return result;
};


LsDefangFilter.prototype._traverseTree = function (nodes) {

	var stack = new Array();
	var pushFrame = false;
	var node = null;
	for (var i = 0; i < nodes.length; ++i ) {
		node = nodes[i];
		//LsLog.info("node name = " + node.nodeName + " type = " + node.nodeType
		//	   + " node value = " + node.nodeValue);
		switch (node.nodeType) {
		case 1:	// Element
			if (node.nodeName != 'document'){
				this.handleElement(node);
			}
			pushFrame = true;
			break;

		case 3:	// Text
			// test if the value is allowed
			break;
			
		case 4:	// CDATA
			break;
		case 5: // node entity reference
			break;
		case 6: // node entity
			break;
		case 7:	// ProcessInstruction
			break;
		case 8:	// Comment
			// remove all comments
			// IE doesn't like removing comments for some reason.
			if (LsEnv.isIE){
				//node.parentNode.removeNode(node);
			} else {
				node.parentNode.removeChild(node);
			}
			break;
		case 10:
			break;
		case 9:	// Document
			pushFrame = true;
			break;
			
		case 11:	// Document Fragment
			pushFrame = true;
			break;
		case 12: // node notation 
			break;
		default:
			break;
		}
		// since we're not using recursion, lets save some context, and
		// keep moving through this loop.
		if (pushFrame){
			stackObj = {nodes: nodes, index: i};
			stack.push(stackObj);
			nodes = nodes[i].childNodes;
			// -1 is import since the for loop will increment imediately to 0
			i = -1;
			pushFrame = false;
		}
		// pop if the loop is going to end.
		while (i == (nodes.length - 1)){
			oldFrame = stack.pop();
			if (oldFrame){
				nodes = oldFrame.nodes;
				i = oldFrame.index;
			} else {
				break;
			}
		}
	}

	// remove any elements that have been marked for removal
	var arr = this._elementsForRemoval;
	var len = arr.length;
	var el = null;
	for (var j = 0 ; j < len ; ++j){
		el = arr.pop();
		if (el.parentNode != null) {
			el.parentNode.removeChild(el);
		}
	}
};


LsDefangFilter.prototype.parse = function (htmlStr) {
	this._elementsForRemoval = new Array();
	var htmlEl = null;
	if (LsEnv.isIE) {
		// This is the other option for parsing with IE
		// we need an iframe in the explicitely set in the html of the page,
		// where the security attribute is set to "restricted".
		//htmlEl = document.createElement('document');
		//var iframe = document.getElementById('ieDefang');
		//iframe.style.display= 'none';
		//iframe.src="about:blank";
		//document.body.appendChild(iframe);		
		//htmlEl = iframe.Document;

		// innerHTML is read only on the html element for some microsoft
		// like reason.
		// The downside to using a div, is that the browser strips the
		// tags before the body.
		//htmlEl = document.createElement('div');
		//htmlEl.innerHTML = htmlStr;
		return this.cleanHTML(htmlStr);
		//LsLog.info("htmlStr \n" + htmlStr);
		//htmlEl.open();
		//htmlEl.write(htmlStr);
		//htmlEl.close();
		
	} else {
		// ah the simplicity of using firefox
		htmlEl = document.createElement('html');
		htmlEl.innerHTML = htmlStr;
		this._traverseTree(htmlEl.childNodes);
		return htmlEl.innerHTML;
	}



	// if we were using the iframe for IE, uncomment out the following line.
	//return LsEnv.isIE? htmlEl.documentElement.innerHTML: htmlEl.innerHTML;
	//return htmlEl.innerHTML;
};
