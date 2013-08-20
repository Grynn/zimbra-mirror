/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2006, 2007, 2008, 2009, 2010, 2012, 2013 Zimbra Software, LLC.
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
 * Creates a composite that is populated from a message pattern.
 * @constructor
 * @class
 * This class allows you to create a composite that is populated from
 * a message pattern and inserts controls at the appropriate places.
 * For example, say that the message <code>MyMsg.repeatTimes</code> is
 * defined as the following:
 * <pre>
 * MyMsg.repeatTimes = "Repeat: {0} times";
 * </pre>
 * and you want to replace "{0}" with an input field or perhaps a
 * drop-down menu that enumerates a specific list of choices as part of
 * the application. To do this, you just create a
 * {@link DwtMessageComposite} and set the message format, like so:
 * <pre>
 * var comp = new DwtMessageComposite(parent);
 * comp.setFormat(MyMsg.repeatTimes);
 * </pre>
 * <p>
 * The message composite instantiates an {@link AjxMessageFormat}
 * from the specified message pattern. Then, for each segment it creates
 * static text or a {@link DwtInputField} for replacement segments
 * such as "{0}".
 * <p>
 * To have more control over the controls that are created and inserted
 * into the resulting composite, you can pass a callback object to the
 * method. Each time that a replacement segment is found in the
 * message pattern, the callback is called with the following parameters:
 * <ul>
 * <li>a reference to this message composite object;
 * <li>a reference to the segment object.
 * <li>the index at which the segment was found in the message pattern; and
 * </ul>
 * The segment object will be an instance of
 * <code>AjxMessageFormat.MessageSegment</code> and has the following
 * methods of interest:
 * <ul>
 * <li>toSubPattern
 * <li>getIndex
 * <li>getType
 * <li>getStyle
 * <li>getSegmentFormat
 * </ul>
 * <p>
 * The callback can use this information to determine whether or not
 * a custom control should be created for the segment. If the callback
 * returns <code>null</code>, a standard {@link DwtInputField} is
 * created and inserted. Note: if the callback returns a custom control,
 * it <em>must</em> be an instance of {@link AjxControl}.
 * <p>
 * Here is an example of a message composite created with a callback
 * that generates a custom control for each replacement segment:
 * <pre>
 * function createCustomControl(parent, segment, i) {
 *     return new DwtInputField(parent);
 * }
 *
 * var compParent = ...;
 * var comp = new DwtMessageComposite(compParent);
 *
 * var message = MyMsg.repeatTimes;
 * var callback = new AjxCallback(null, createCustomControl);
 * comp.setFormat(message, callback);
 * </pre>
 *
 * @author Andy Clark
 *
 * @param {DwtComposite}	parent    the parent widget.
 * @param {string}	className 	the CSS class
 * @param {constant}	posStyle  		the position style (see {@link DwtControl})
 * 
 * @extends		DwtComposite
 */
DwtMessageComposite = function(parent, className, posStyle) {
	if (arguments.length == 0) return;
	className = className || "DwtMessageComposite";
	DwtComposite.call(this, {parent:parent, className:className, posStyle:posStyle});
}

DwtMessageComposite.prototype = new DwtComposite;
DwtMessageComposite.prototype.constructor = DwtMessageComposite;

DwtMessageComposite.prototype.toString =
function() {
	return "DwtMessageComposite";
}

// Public methods

/**
 * Sets the format.
 * 
 * @param {string}	message   the message that defines the text and controls that comprise this composite
 * @param {AjxCallback}	[callback]   the callback to create UI components
 * @param {AjxCallback}	[hintsCallback]   the callback to provide display hints for the container element of the UI component
 */
DwtMessageComposite.prototype.setFormat =
function(message, callback, hintsCallback) {
    // create formatter
    this._formatter = new AjxMessageFormat(message);
    this._controls = {};

    // create HTML
    var id = this._htmlElId;
    var a = ["<table class='DwtCompositeTable' border='0' cellspacing='0' cellpadding='0'><tr valign='center'>"];

    var segments = this._formatter.getSegments();
    var cells = {};
    var hints = {};
    for (var i = 0; i < segments.length; i++) {
        var segment = segments[i];
        var isMsgSegment = segment instanceof AjxMessageFormat.MessageSegment;

        var cid = [id,i].join("_");
        a.push("<td id='",cid,"' class='",(isMsgSegment?"MessageControl"+segment.getIndex():"")," DwtCompositeCell'>");

        if (isMsgSegment) {
            var control = callback ? callback.run(this, segment, i) : null;
            if (!control) {
                control = new DwtInputField({parent:this});
            }
            cells[cid] = control.getHtmlElement();
            hints[cid] = hintsCallback && hintsCallback.run(this, segment, i);

            var sindex = segment.getIndex();
            this._controls[sindex] = this._controls[sindex] || control;
        }
        else {
            a.push(segment.toSubPattern());
        }

        a.push("</td>");
    }

    a.push("</tr></table>");

    // insert HTML
    var el = this.getHtmlElement();
    /***
    el.innerHTML = a.join("");
    /***/
    // HACK: IE seems to throw away input elements when they are children
    //       of an element when you set the innerHTML to something else,
    //       regardless of the fact that there are outstanding references
    //       to said elements! Ugh.
    var count = el.childNodes.length;
    var tempEl = document.createElement("DIV");
    tempEl.className = id+'_container';
    el.appendChild(tempEl);
    tempEl.innerHTML = a.join("");
    /***/

    // insert controls
    for (var cid in cells) {
        var cell = cells[cid];
        var parentEl = document.getElementById(cid);
        parentEl.appendChild(cell);

        for (var p in hints[cid]) {
            parentEl[p] = hints[cid][p];
        }
    }
};

/**
 * Gets the format.
 * 
 * @return	{string}	the format
 */
DwtMessageComposite.prototype.format = function() {
    var args = [];
    for (var sindex in this._controls) {
        args[sindex] = this._controls[sindex].getValue();
    }
    return this._formatter.format(args);
};