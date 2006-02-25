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
* @class
* DvAttr represents a single class of attribute for an item. It may be represented
* as a column in the list view, and/or as part of the filter in the filter panel.
* @author Conrad Damon
*
* @param id			a unique numeric ID
* @param name		text name
* @param type		data type
* @param width		width of the corresponding column in the list view
* @param options	choices (for select or checkbox type)
*/
function DvAttr(id, name, type, width, options) {

	DvModel.call(this);
	
	this.id = id;
	this.name = name;
	this.type = type;
	this.width = width;
	this.options = options;
}

// attribute types
var i = 1;
DvAttr.T_STRING_EXACT			= "StringExact";
DvAttr.T_STRING_CONTAINS		= "StringContains";
DvAttr.T_SELECT					= "SingleSelect";
DvAttr.T_MULTI_SELECT			= "MultipleSelect";
DvAttr.T_NUMBER					= "Number";
DvAttr.T_NUMBER_RANGE			= "NumberRange";
DvAttr.T_NUMBER_RANGE_BOUNDED	= "NumberRangeBounded";
DvAttr.T_DATE_RANGE				= "DateRange";
DvAttr.T_TIME_RANGE				= "TimeRange";
DvAttr.T_BOOLEAN				= "Boolean";

DvAttr.prototype = new DvModel;
DvAttr.prototype.constructor = DvAttr;
