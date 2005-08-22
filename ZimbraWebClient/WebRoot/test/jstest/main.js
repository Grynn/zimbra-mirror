/*
***** BEGIN LICENSE BLOCK *****
Version: ZPL 1.1

The contents of this file are subject to the Zimbra Public License Version 1.1 ("License");
You may not use this file except in compliance with the License. You may obtain a copy of
the License at http://www.zimbra.com/license

Software distributed under the License is distributed on an "AS IS" basis, WITHOUT WARRANTY
OF ANY KIND, either express or implied. See the License for the specific language governing
rights and limitations under the License.

The Original Code is: Zimbra Collaboration Suite.

The Initial Developer of the Original Code is Zimbra, Inc.
Portions created by Zimbra are Copyright (C) 2005 Zimbra, Inc.
All Rights Reserved.
Contributor(s): ______________________________________.

***** END LICENSE BLOCK *****
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