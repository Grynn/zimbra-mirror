/*
 * Copyright (C) 2007, The Apache Software Foundation.
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

DwtRadioButton = function(parent, style, name, checked, className, posStyle, id, index) {
	if (arguments.length == 0) return;
	className = className ? className : "DwtRadioButton";
	DwtCheckbox.call(this, parent, style, name, checked, className, posStyle, id, index);
}

DwtRadioButton.prototype = new DwtCheckbox;
DwtRadioButton.prototype.constructor = DwtRadioButton;

DwtRadioButton.prototype.toString = function() {
	return "DwtRadioButton";
};

//
// Data
//

DwtRadioButton.prototype.TEMPLATE = "dwt.Widgets#DwtRadioButton";
