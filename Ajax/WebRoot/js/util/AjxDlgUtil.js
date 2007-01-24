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
