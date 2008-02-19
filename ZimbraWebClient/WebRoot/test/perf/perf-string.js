function stringConcat(iters, resultsEl) {
	var before = new Date().getTime();
	var s = "";
	for (var i = 0; i < iters; i++) {
		s += " ";
	}
	var after = new Date().getTime();

	resultsEl.innerHTML = after - before;
}
function stringPushJoin(iters, resultsEl) {
	var before = new Date().getTime();
	var a = [];
	for (var i = 0; i < iters; i++) {
		a.push(" ");
	}
	var s = a.join("");
	var after = new Date().getTime();
	
	resultsEl.innerHTML = after - before;
}

var SAMPLE_STRING = 
	"one two     three four   five six    seven eight   nine  ten eleven "+
	"twelve  thirteen  fourteen fifteen sixteen seventeen   eighteen "+
	"nineteen                twenty";
function stringSplit(iters, resultsEl, delim) {
	var before = new Date().getTime();
	for (var i = 0; i < iters; i++) {
		var a = SAMPLE_STRING.split(delim);
	}
	var after = new Date().getTime();
	
	resultsEl.innerHTML = after - before;
}
function stringSubstring(iters, resultsEl) {
	var s = getEmptyString(iters);	
	
	var before = new Date().getTime();
	for (var i = 0; i < iters - 1; i++) {
		var sub = s.substring(i, i+1);
	}
	var after = new Date().getTime();
	
	resultsEl.innerHTML = after - before;
}
function stringSubstr(iters, resultsEl) {
	var s = getEmptyString(iters);	
	
	var before = new Date().getTime();
	for (var i = 0; i < iters - 1; i++) {
		var sub = s.substr(i, 1);
	}
	var after = new Date().getTime();
	
	resultsEl.innerHTML = after - before;
}

function stringCharAt(iters, resultsEl) {
	var s = getEmptyString(iters);	

	var before = new Date().getTime();
	for (var i = 0; i < iters; i++) {
		var c = s.charAt(i);
	}
	var after = new Date().getTime();
		
	resultsEl.innerHTML = after - before;
}

function stringReplace(iters, resultsEl, regex, replace) {
	var s = document.body.innerHTML;
	
	var before = new Date().getTime();
	for (var i = 0; i < iters; i++) {
		var rs = s.replace(regex, replace);
	}
	var after = new Date().getTime();

	resultsEl.innerHTML = after - before;
}

function stringSlice(iters, resultsEl) {
	var s = getEmptyString(iters);	

	var before = new Date().getTime();
	for (var i = 0; i < iters - 1; i++) {
		var ss = s.slice(i, i+1);
	}
	var after = new Date().getTime();

	resultsEl.innerHTML = after - before;
}

function getEmptyString(length) {
	var s = [];

	var ten = "          ";
	var times = length / 10;
	for (var i = 0; i < times; i++) {
		s.push(ten);
	}

	var one = " ";
	var remainder = length % 10;
	for (var i = 0; i < remainder; i++) {
		s.push(one);
	}

	return s.join("");
}

var STRING_TESTS = {
	name: "String Tests", tests: [
		{ name: "concat", iters: 100000, func: stringConcat },
		{ name: "push + join", iters: 100000, func: stringPushJoin },
		{ name: "split (regex delim)", iters: 10000, func: stringSplit, args: [/\s+/] },
		{ name: "split (string delim)", iters: 10000, func: stringSplit, args: [" "] },
		{ name: "substring", iters: 100000, func: stringSubstring },
		{ name: "substr", iters: 100000, func: stringSubstr },
		{ name: "slice", iters: 100000, func: stringSlice },
		{ name: "charAt", iters: 100000, func: stringCharAt },
		{ name: "replace(/&lt;/g,'&amp;lt;')", iters: 1000, func: stringReplace, args: [/</g, "&lt;"] }
	]
};
