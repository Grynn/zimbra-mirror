/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2010, 2013 Zimbra Software, LLC.
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
JSDOC.PluginManager.registerPlugin(
	"JSDOC.commentSrcJson",
	{
		onDocCommentSrc: function(comment) {
			var json;
			if (/^\s*@json\b/.test(comment)) {
				comment.src = new String(comment.src).replace("@json", "");

				eval("json = "+comment.src);
				var tagged = "";
				for (var i in json) {
					var tag = json[i];
					// todo handle cases where tag is an object
					tagged += "@"+i+" "+tag+"\n";
				}
				comment.src = tagged;
			}
		}
	}
);