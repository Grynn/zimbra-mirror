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