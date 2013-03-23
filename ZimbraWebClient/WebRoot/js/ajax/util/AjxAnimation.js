/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2007, 2009, 2010 VMware, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.3 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */

/**
 * @class
 * An object useful for implementing accelerated / decelerated animation.
 *
 * @author Mihai Bazon <mihai@zimbra.com>
 *
 * This object creates a timer (setInterval) and calls the function
 * ({@link AjxCallback}) that you supply at onUpdate every "speed" milliseconds,
 * passing to it two arguments:
 *
 * <ul>
 * <li><i>pos</i> -- a float number between 0 and 1</li>
 * <li><i>anim</i> -- the animation object</li>
 * </ul>
 * 
 * "pos" is computed by an easing function; it is 0 when the animation starts
 * and it approaches 1 as your animation continues; For example, depending on
 * the easing function, it can increase faster initially and slower as it
 * reaches 1 (simulates acceleration).
 * <p>
 * AjxAnimation supplies 2 very basic easing functions (f_accelerate and
 * f_decelerate).  More complex functions can be easily written, to simulate
 * i.e. bouncing -- you just have to find the mathematical representation.
 * </p><p>
 * An easing function receives 2 arguments:
 * <ul>
 * <li><i>i</i> -- the current frame</li>
 * <li><i>length</i> -- the total number of frames</li>
 * </ul>
 * 
 * It should return 0 when i == 0, 1 when i == length, and a number between 0
 * and 1 otherwise.  You can get a constant speed animation with this
 * uninteresting easing function:
 *
 * <pre>
 * function(i, length) { return i / length; }
 * </pre>   
 *
 * @param	{hash}		args		a hash of parameters
 * @param   {number}	args.length   the number of frames
 * @param   {number}	args.speed    the speed of the timer (i.e. argument to setInterval)
 * @param   {function}	args.f        the easing function
 * @param   {AjxCallback}	args.onUpdate called when the timer updates
 * @param   {AjxCallback}	args.onStop   called when the animation is finished
 * 
 * @private
 *
 */
AjxAnimation = function(args) {
	this.length = args.length || 15;
	this.speed = args.speed || 50;
	this.f = args.f || AjxAnimation.f_decelerate;
	this.onUpdate = args.onUpdate;
	this.onStop = args.onStop;

	this.__work = AjxCallback.simpleClosure(this.__work, this);
};

AjxAnimation.prototype.start = function() {
	this.i = 0;
	this.stop();
	this.timer = setInterval(this.__work, this.speed);
};

AjxAnimation.prototype.stop = function() {
	if (this.timer) {
		clearInterval(this.timer);
		this.timer = null;
		if (this.onStop)
			this.onStop.run(this);
	}
};

AjxAnimation.prototype.__work = function() {
	this.onUpdate.run(this.f(this.i++, this.length),
			  this);
	if (this.i > this.length)
		this.stop();
};

// if pos is in [0, 1], this function maps it to the interval [a, b]
AjxAnimation.prototype.map = function(pos, a, b) {
	return a + (b - a) * pos;
};

// simple easing functions for acceleration / deceleration

AjxAnimation.f_decelerate = function(i, l) {
	var x = 1 - i/l;
	x = x * x;
	return 1 - x * x;
};

AjxAnimation.f_accelerate = function(i, l) {
	var x = i/l;
	x = x * x;
	return x * x;
};

AjxAnimation.f_plain = function(i, l) {
	return i / l;
};
