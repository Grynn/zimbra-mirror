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


var form = {
	itemDefaults: {
		_GROUP_: {	tableCssClass:"xform_composite_table"	}
	},
	items:[
		{ref:"ALL_DAY", type:_CHECKBOX_, label:"All day"},

		{type:_SEPARATOR_},
		{type:_GROUP_, useParentTable:false, label:"Group w/o relevant", numCols:1, items:[
				{type:_OUTPUT_, value:"output: Show disabled", label:"output", relevant:"get('ALL_DAY') == 'T'", relevantBehavior:_DISABLE_},
				{type:_TEXTFIELD_, value:"input: disabled", relevant:"get('ALL_DAY') == 'T'", relevantBehavior:_DISABLE_, label:"input"},
				{type:_TEXTFIELD_, value:"input: hidden", relevant:"get('ALL_DAY') == 'T'", relevantBehavior:_HIDE_, label:"input"},
				{type:_CHECKBOX_, ref:"OTHER_CHECK", label:"checkbox: disabled", relevant:"get('ALL_DAY') == 'T'", relevantBehavior:_DISABLE_},
				{type:_DATE_, ref:"START_DATE", label:"composite: disabled", relevant:"get('ALL_DAY') == 'T'", relevantBehavior:_DISABLE_},
				{type:_DATE_, ref:"START_DATE", label:"composite: hidden", relevant:"get('ALL_DAY') == 'T'", relevantBehavior:_HIDE_},
				{type:_BUTTON_, label:"button: disabled", relevant:"get('ALL_DAY') == 'T'", align:_CENTER_, colSpan:2}
			]
		},
		
		{type:_SEPARATOR_},
		{type:_GROUP_, useParentTable:false, label:"Group w/ relevant", numCols:2, relevant:"get('ALL_DAY') == 'T'", relevantBehavior:_DISABLE_, items:[
				{type:_OUTPUT_, value:"output: Show disabled", relevantBehavior:_DISABLE_, label:"output"},
				{type:_TEXTFIELD_, value:"input: disabled", relevantBehavior:_DISABLE_, label:"input:disabled"},
				{type:_TEXTFIELD_, value:"input: hidden", relevantBehavior:_HIDE_, label:"input:hidden"},
				{type:_CHECKBOX_, ref:"YET_OTHER_CHECK", label:"checkbox: disabled", relevantBehavior:_DISABLE_},
				{type:_DATE_, ref:"START_DATE", label:"composite: disabled", relevantBehavior:_DISABLE_},
				{type:_DATE_, ref:"START_DATE", label:"composite: hidden", relevantBehavior:_HIDE_},
				{type:_BUTTON_, label:"button: disabled", colSpan:"*", align:_CENTER_}
			]
		}
	]
}

var model = {
	items:[
		{id:"START_DATE", type:_DATE_},
		{id:"ALL_DAY", trueValue:"T", falseValue:"F"}
	]
}

var instances = {
	"Start enabled":{
		START_DATE:new Date(),
		ALL_DAY:"F"
	},
	"Start disabled":{
		START_DATE:null,
		ALL_DAY:"T"
	},
	empty:{}
}


var model = new XModel(model);
registerForm("Disable test", new XForm(form, model), instances);

