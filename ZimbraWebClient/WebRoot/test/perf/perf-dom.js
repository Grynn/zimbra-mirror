function domInnerHTML(iters, resultsEl, inTree) {
	var div = document.createElement("DIV");
	if (inTree) {
		document.body.appendChild(div);
	}
	
	var before = new Date().getTime();
	for (var i = 0; i < iters; i++) {
		div.innerHTML = "<table border='0'><tr><th>One<th>Two<tr><td>Three<td>Four</table>";
	}
	var after = new Date().getTime();

	if (inTree) {
		document.body.removeChild(div);
	}
	
	resultsEl.innerHTML = after - before;
}

function domCreateElement(iters, resultsEl, inTree) {
	var div = document.createElement("DIV");
	if (inTree) {
		document.body.appendChild(div);
	}
	
	var div = document.createElement("DIV");
	if (inTree) {
		document.body.appendChild(div);
	}
	
	var before = new Date().getTime();
	for (var i = 0; i < iters; i++) {
		var table = document.createElement("TABLE");
		table.border = 0;
		var row = table.insertRow(table.rows.length);
		var cell = row.insertCell(row.cells.length);
		cell.appendChild(document.createTextNode("One"));
		var cell = row.insertCell(row.cells.length);
		cell.appendChild(document.createTextNode("Two"));
		var row = table.insertRow(table.rows.length);
		var cell = row.insertCell(row.cells.length);
		cell.appendChild(document.createTextNode("Three"));
		var cell = row.insertCell(row.cells.length);
		cell.appendChild(document.createTextNode("Four"));
		if (div.firstChild) {
			div.replaceChild(table, div.firstChild);
		}
		else {
			div.appendChild(table);
		}
	}
	var after = new Date().getTime();

	if (inTree) {
		document.body.removeChild(div);
	}

	resultsEl.innerHTML = after - before;
}
function domAppendElement(iters, resultsEl, inTree) {
	var elems = [];
	for (var i = 0; i < iters; i++) {
		elems.push(document.createElement("DIV"));
	}

	var div = document.createElement("DIV");
	if (inTree) {
		document.body.appendChild(div);
	}
	var before = new Date().getTime();
	for (var i = 0; i < iters; i++) {
		div.appendChild(elems[i]);
	}
	var after = new Date().getTime();
	if (inTree) {
		document.body.removeChild(div);
	}

	resultsEl.innerHTML = after - before;
}

function domReadAttribute(iters, resultsEl) {
	var div = document.createElement("DIV");
	div.prop = iters;
	
	var before = new Date().getTime();
	for (var i = 0; i < iters; i++) {
		var value = div.prop;
	}
	var after = new Date().getTime();
	
	resultsEl.innerHTML = after - before;
}
function domWriteAttribute(iters, resultsEl, propName, inTree) {
	var div = document.createElement("DIV");
	if (inTree) {
		document.body.appendChild(div);
	}

	var before = new Date().getTime();
	for (var i = 0; i < iters; i++) {
		div[propName] = i;
	}
	var after = new Date().getTime();
	
	if (inTree) {
		document.body.removeChild(div);
	}
	
	resultsEl.innerHTML = after - before;
}
function domEnumerateAttributes(iters, resultsEl) {
	var div = document.createElement("DIV");
	for (var i = 0; i < iters; i++) {
		div[i] = i;
	}

	var before = new Date().getTime();
	for (var prop in div) {
		// do nothing
	}
	var after = new Date().getTime();
	
	resultsEl.innerHTML = after - before;
}
function domDeleteAttribute(iters, resultsEl) {
	if (navigator.userAgent.match("MSIE")) {
		resultsEl.innerHTML = "deleting expando props not supported";
		return;
	}

	var div = document.createElement("DIV");
	for (var i = 0; i < iters; i++) {
		div[i] = i;
	}

	var before = new Date().getTime();
	for (var i = 0; i < iters; i++) {
		delete div[i];
	}
	var after = new Date().getTime();
	
	resultsEl.innerHTML = after - before;
}

function domReverseChildren(iters, resultsEl, inTree) {
	var div = document.createElement("DIV");
	var length = 100;
	for (var i = 0; i < length; i++) {
		div.appendChild(document.createElement("SPAN"));
	}

	if (inTree) {
		document.body.appendChild(div);
	}
	
	var before = new Date().getTime();
	for (var i = 0; i < iters; i++) {
		var first = div.firstChild;
		for (var j = 0; j < length; j++) {
			div.insertBefore(div.lastChild, first);
		}
	}
	var after = new Date().getTime();
	
	if (inTree) {
		document.body.removeChild(div);
	}
	
	resultsEl.innerHTML = after - before;
}

function domWalkIndexed(iters, resultsEl) {
	var before = new Date().getTime();
	for (var i = 0; i < iters; i++) {
		domWalkIndexed0(document.documentElement);
	}
	var after = new Date().getTime();

	resultsEl.innerHTML = after - before;
}
function domWalkIndexed0(node) {
	for (var i = 0; i < node.childNodes.length; i++) {
		domWalkIndexed0(node.childNodes[i]);
	}
}
function domWalkNodes(iters, resultsEl) {	
	var before = new Date().getTime();
	for (var i = 0; i < iters; i++) {
		domWalkNodes0(document.documentElement);
	}
	var after = new Date().getTime();
	
	resultsEl.innerHTML = after - before;
}
function domWalkNodes0(node) {
	var child = node.firstChild;
	while (child != null) {
		domWalkNodes0(child);
		child = child.nextSibling;
	}
}
function domWalkGetTags(iters, resultsEl) {
	var before = new Date().getTime();
	for (var i = 0; i < iters; i++) {
		var nodes = document.getElementsByTagName("*");
		var length = nodes.length;
		for (var j = 0; j < length; j++) {
			var node = nodes[j];
		}
	}
	var after = new Date().getTime();
	
	resultsEl.innerHTML = after - before;
}

function domGetElementsById(iters, resultsEl) {
	var id = new Date().getTime();
	var div = document.createElement("DIV");
	div.id = id;

	var before = new Date().getTime();
	for (var i = 0; i < iters; i++) {
		var element = document.getElementById(id);
	}
	var after = new Date().getTime();

	resultsEl.innerHTML = after - before;
}

var DOM_TESTS = { 
	name: "DOM Tests", tests: [
		{ name: "innerHTML", iters: 1000, func: domInnerHTML },
		{ name: "innerHTML (in tree)", iters: 1000, func: domInnerHTML, args: [true] },
		{ name: "create element", iters: 1000, func: domCreateElement },
		{ name: "create element (in tree)", iters: 1000, func: domCreateElement, args: [true] },
		{ name: "append child", iters: 1000, func: domAppendElement },
		{ name: "append child (in tree)", iters: 1000, func: domAppendElement, args: [true] },
		{ name: "read attribute", iters: 100000, func: domReadAttribute },
		{ name: "write attribute", iters: 100000, func: domWriteAttribute, args: ["prop"] },
		{ name: "write ID attribute", iters: 100000, func: domWriteAttribute, args: ["id", false] },
		{ name: "write ID attribute (in tree)", iters: 100000, func: domWriteAttribute, args: ["id", true] },
		{ name: "enumerate attributes", iters: 100000, func: domEnumerateAttributes },
		{ name: "delete attribute", iters: 100000, func: domDeleteAttribute },
		{ name: "reverse children", iters: 1000, func: domReverseChildren },
		{ name: "reverse children (in tree)", iters: 1000, func: domReverseChildren, args: [true] },
		{ name: "traverse by index", iters: 100, func: domWalkIndexed },
		{ name: "traverse by node", iters: 100, func: domWalkNodes },
		{ name: "get elements by tag name", iters: 100, func: domWalkGetTags },
		{ name: "get element by id", iters: 100000, func: domGetElementsById }
	]
};
