/*
 * Copyright (C) 2006, The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
* @constructor
* @class
* This class represents a the tab event. This event is used to indicate changes in
* the stat of DwtTabGroups (e.g. member addition and deletion). 
* 
* @author Ross Dargahi
* 
* @see DwtTabGroup
*/
DwtTabGroupEvent = function() {
	/** Tab group for which the event is being generated
	 * @type DwtTabGroup
	 */
	this.tabGroup = null;
	
	/** New focus memeber 
	 * @type {DwtControl|HTMLElement}
	 */
	this.newFocusMember = null;
}

/**
 * @return return a string version of the class' name
 * @type String
 */
DwtTabGroupEvent.prototype.toString = 
function() {
	return "DwtTabGroupEvent";
}


/**
 * Resets the members of the event
 */
DwtTabGroupEvent.prototype.reset =
function() {
	this.tabGroup = null;
	this.newFocusMember = null;
}
