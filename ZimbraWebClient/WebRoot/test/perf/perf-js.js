function jsEval(iters, resultsEl) {
	var a = ['var object = {'];
	for (var i = 0; i < iters; i++) {
		if (i > 0) a.push(', ');
		a.push("i"+i, ':', i);
	}
	a.push('}');
	var js = a.join(" ");
	var before = new Date().getTime();
	for (var i = 0; i < iters; i++) {
		eval(js);
	}
	var after = new Date().getTime();

	resultsEl.innerHTML = after - before;
}

function jsParseInt(iters, resultsEl) {
	var num = "42";
	var before = new Date().getTime();
	for (var i = 0; i < iters; i++) {
		var val = parseInt(num);
	}
	var after = new Date().getTime();

	resultsEl.innerHTML = after - before;
}
function jsCastInt(iters, resultsEl) {
	var num = "42";
	var before = new Date().getTime();
	for (var i = 0; i < iters; i++) {
		var val = Number(num);
	}
	var after = new Date().getTime();

	resultsEl.innerHTML = after - before;
}

function jsParseFloat(iters, resultsEl) {
	var num = "3.14";
	var before = new Date().getTime();
	for (var i = 0; i < iters; i++) {
		var val = parseFloat(num);
	}
	var after = new Date().getTime();

	resultsEl.innerHTML = after - before;
}
function jsCastFloat(iters, resultsEl) {
	var num = "3.14";
	var before = new Date().getTime();
	for (var i = 0; i < iters; i++) {
		var val = Number(num);
	}
	var after = new Date().getTime();

	resultsEl.innerHTML = after - before;
}

var JS_TESTS = {
	name: "JavaScript Tests", tests: [
		{ name: "eval JSON", iters: 1000, func: jsEval },
		{ name: "parse int", iters: 100000, func: jsParseInt },
		{ name: "cast int", iters: 100000, func: jsCastInt },
		{ name: "parse float", iters: 100000, func: jsParseFloat },
		{ name: "cast float", iters: 100000, func: jsCastFloat }
	]
};