/*
 * Copyright 2008 Yahoo! Inc. All rights reserved.
 */


/*
 * YMSGR is a global namespace for IM functionality which is
 * application-independent.  This file declares the object
 * and a limited amount of base functionality.
 */


// This technique for detecting and not overriding an existing library
// is replicated from YUI
//
if (typeof YMSGR == "undefined" || !YMSGR) {
    var YMSGR = {};
}


// TODO: Not currently in use in Mail.  Deprecated?
// var YEvents = null;

// TODO: Eliminate YCONST after determining
// if it's kosher to do so (talk to Msgr team).
// Mail now uses YMSGR.CONST everywhere.
var YCONST = null;
YMSGR.CONST = null;


// Convert JSON to XML
// Limitation: arrays can only contain other objects
//
YMSGR.j2x = function (o, tag) {
	// obj, arr arrays in case there are attribs mixed in
	var objs = [];
	var arrs = [];
	function typeOf(val)
	{
		var s = typeof val;
		if (s === 'object') {
			if (val) {
				if (val instanceof Array) {
					s = 'array';
				}
			} else {
				s = 'null';
			}
		}
		return s; 
	}
	
	if (!tag)
		tag = 'payload';
	
	var x = ['<'+tag+' '];
	for (var key in o) {
		if (typeOf(o[key]) === 'object')
			objs.push(key);
		else if (typeOf(o[key]) === 'array')
			arrs.push(key);
		else
		{
			x.push(key);
			x.push('="');
			x.push(o[key]);
			x.push('" ');
		}
	}
	x.push('>');

	var n;
	for (n=0; n<objs.length; n++)
		x.push(YMSGR.j2x(o[objs[n]], objs[n]));

	for (n=0; n<arrs.length; n++)
	{
		var arr = o[arrs[n]];
		for (var i=0; i<arr.length; i++)
		{
			if (typeOf(arr[i]) === 'object')
				x.push(YMSGR.j2x(arr[i], arrs[n]));
		}
	}

	x.push('</'+tag+'>');    
	return x.join("");
};


// Grabs the Y&T (login) sub-cookies
//
YMSGR.getCookie = function() {
	var c=document.cookie;
	var auth= "";
		
	var ar= c.split("; ");
	for(var i=0; i<ar.length; i++){
        if(ar[i].search(/[YT]=/) == 0){
            auth+=ar[i]+"; ";
        }
	}
	// strip the ending "; "
	return auth.substr(0, auth.length-2);
};


// Replicated from YUI
//
// TODO:Not clear we need this as we move to a flatter namespace.
//	
//	YMSGR.namespace = function() {
//	    var a=arguments, o=null, i, j, d;
//	    for (i=0; i<a.length; i=i+1) {
//	        d=a[i].split(".");
//	        o=YMSGR;
//	
//	        // YMSGR is implied, so it is ignored if it is included
//	        for (j=(d[0] == "YMSGR") ? 1 : 0; j<d.length; j=j+1) {
//	            o[d[j]]=o[d[j]] || {};
//	            o=o[d[j]];
//	        }
//	    }
//	
//	    return o;
//	};
