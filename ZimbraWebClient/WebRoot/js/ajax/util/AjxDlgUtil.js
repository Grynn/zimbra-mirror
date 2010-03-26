/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2007, 2009, 2010 Zimbra, Inc.
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
 * 
 * @private
 */
AjxDlgUtil = {

	cache : {},

	getDialogLayout : function(name, url, msg) {
		var time = new Date().getTime();

		var txt, cache = AjxDlgUtil.cache;
		if (cache[name]) {
			txt = cache[name];
		} else {
			// WARNING: synchronous request!
			// Also we don't treat errors at this point >-) so you better
			// know what you're doing.
			var res = AjxRpc.invoke(null, url + "?v=" + time, null, null, true, 5000);
			cache[name] = txt = res.text;
		}

		var ids = {};

		// get rid of the comments
		txt = txt.replace(/<!--.*?-->/, "");

		// replace $msg and $id fields
		txt = txt.replace(/\$([a-zA-Z0-9_.]+)/g, function(str, p1) {
			if (/^([^.]+)\.(.*)$/.test(p1)) {
				var prefix = RegExp.$1;
				var name = RegExp.$2;
				switch (prefix) {
				    case "id":
					var id = ids[name];
					if (!id)
						id = ids[name] = Dwt.getNextId();
					return id;
				    case "msg":
					return msg[name];
				}
			}
			return str;
		});

		return { ids: ids, html: txt };
	}

};
