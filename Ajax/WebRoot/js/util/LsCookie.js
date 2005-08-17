function LsCookie() {
}

LsCookie.prototype.toString = 
function() {
	return "LsCookie";
}

LsCookie.getCookie = 
function(doc, name) {
	var arg = name + "=";
	var alen = arg.length;
	var clen = doc.cookie.length;
	var cookie = doc.cookie;
	var i = 0;
	while (i < clen) {
		var j = i + alen;
		if (cookie.substring(i, j) == arg) {
			var endstr = cookie.indexOf (";", j);
			if (endstr == -1)
				endstr = cookie.length;
			return unescape(cookie.substring(j, endstr));
		}
		i = cookie.indexOf(" ", i) + 1;
		if (i == 0) 
			break; 
	}
  return null;
}

LsCookie.setCookie = 
function(doc, name, value, expires, path, domain, secure) {
	doc.cookie = name + "=" + escape (value) +
		((expires) ? "; expires=" + expires.toGMTString() : "") +
		((path) ? "; path=" + path : "") +
		((domain) ? "; domain=" + domain : "") +
		((secure) ? "; secure" : "");
}

LsCookie.deleteCookie = 
function (doc, name, path, domain) {
	doc.cookie = name + "=" +
	((path) ? "; path=" + path : "") +
	((domain) ? "; domain=" + domain : "") + "; expires=Fri, 31 Dec 1999 23:59:59 GMT";
}
