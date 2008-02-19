function objectCreate(iters, resultsEl) {
	var before = new Date().getTime();
	for (var i = 0; i < iters; i++) {
		var object = new Object;
	}
	var after = new Date().getTime();

	resultsEl.innerHTML = after - before;
}

function objectReadProperty(iters, resultsEl, depth) {
	var object = objectCreateProxy(depth);

	var before = new Date().getTime();
	for (var i = 0; i < iters; i++) {
		var value = object.prop;
	}
	var after = new Date().getTime();

	resultsEl.innerHTML = after - before;
}

function objectCreateProxy(depth) {
	var object = { prop: depth };
	for (var i = 0; i < depth; i++) {
		var proxyCtor = new Function;
		proxyCtor.prototype = object;
		object = new proxyCtor;
	}
	return object;
}

function objectWriteProperty(iters, resultsEl) {
	var object = new Object;
	
	var before = new Date().getTime();
	for (var i = 0; i < iters; i++) {
		object.prop = i;
	}
	var after = new Date().getTime();
	
	resultsEl.innerHTML = after - before;
}

function objectDeleteProperty(iters, resultsEl) {
	var object = new Object;
	for (var i = 0; i < iters; i++) {
		object[i] = i;
	}
	
	var before = new Date().getTime();
	for (var i = 0; i < iters; i++) {
		delete object[i];
	}
	var after = new Date().getTime();
	
	resultsEl.innerHTML = after - before;
}

function objectEnumerateProperties(iters, resultsEl) {
	var object = new Object;
	for (var i = 0; i < iters; i++) {
		object[i] = i;
	}
	
	var before = new Date().getTime();
	for (var prop in object) {
		// do nothing
	}
	var after = new Date().getTime();
	
	resultsEl.innerHTML = after - before;
}

var OBJECT_TESTS = {
	name: "Object Tests", tests: [
		{ name: "new Object", iters: 100000, func: objectCreate },
		{ name: "write property", iters: 100000, func: objectWriteProperty },
		{ name: "read property (depth = 0)", iters: 100000, func: objectReadProperty, args: [0] },
		{ name: "read property (depth = 5)", iters: 100000, func: objectReadProperty, args: [5] },
		{ name: "read property (depth = 10)", iters: 100000, func: objectReadProperty, args: [10] },
		{ name: "enumerate properties", iters: 100000, func: objectEnumerateProperties },
		{ name: "delete property", iters: 100000, func: objectDeleteProperty }
	]
};