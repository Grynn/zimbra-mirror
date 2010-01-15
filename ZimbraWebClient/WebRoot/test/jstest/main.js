/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2004, 2005, 2006, 2007, 2009 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.2 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */

initDWT();

function X(p) {
	this.a = p;
}

X.prototype.f =
function() {
	DWT.debug.info("X.prototype.f: " + this.a + " " + this.b);
}

function Y() {
	this.sc = X;
	this.sc("Hello");
	delete this.sc;
	this.b = "world";
}

Y.prototype = new X();
Y.prototype.constructor = Y;
Y.prototype.superclass = new X();

Y.prototype.baseF = X.prototype.f;
Y.prototype.f = 
function() {
	DWT.debug.info("Y.prototype.f: " + this.a + " " + this.b);
	this.baseF();
	//this.superclass.f();
}

var y = new Y();
DWT.debug.info(y.f());