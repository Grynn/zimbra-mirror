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


var xftest = {
	form: {
		items:[
			{ref:"to", type:_TEXTAREA_,  labelLocation:_LEFT_, labelCssClass:"xform_label_cell", cssStyle:"height:30px;width:490px;"},		
			{ref:"cc", type:_TEXTAREA_,  labelLocation:_LEFT_, labelCssClass:"xform_label_cell", cssStyle:"height:30px;width:490px;"},
			{ref:"bcc", type:_TEXTAREA_, labelLocation:_LEFT_, labelCssClass:"xform_label_cell", cssStyle:"height:30px;width:490px;"},
	
			{type:_GROUP_, numCols:2, items:[
				{ref:"subject", type:_INPUT_, cssStyle:"width:440px;"},
				{type:_SELECT1_, cssStyle:"width:50px;",
					choices:[" ","Show CC", "Show BCC", "Show Reply To"]
				}
			]},
	
			{type:_SPACER_, height:5},
	
			{ref:"attachments", label:"Attachments:", type:_REPEAT_, number:1, numCols:3, items: [
					{ref:"name", type:_OUTPUT_},
					{type:_SPACER_, width:8},
					{type:_ANCHOR_, label:"(Remove)", value:"#"}
				]
			},
	//XXX FILE TYPE BREAKS FIREFOX MAC
	//		{type:_FILE_, id:"FILE", label:""},
	
			{type:_SPACER_, height:3},
			{ref:"body", type:_TEXTAREA_, cssStyle:"height:300px;width:597px;"},
	
			{type:_SPACER_, height:10},
			
			{type:_GROUP_, numCols:5, items:[
					{type:_BUTTON_, label:"Save"},
					{type:_BUTTON_, label:"Cancel"},
					{type:_SPACER_, width:100},
					{type:_SWITCH_, items:[
						{type:_CASE_, relevant:"get('showCC') != 'T'", items:[
							{type:_BUTTON_, label:"Show CC", onChange:"this.xform.xmodel.set('showCC','T')"}
						]},
						{type:_CASE_, relevant:"get('showCC') == 'T'", items:[
							{type:_BUTTON_, label:"Hide CC", onChange:"this.xform.xmodel.set('showCC','F')"}
						]}
					]},
					{type:_SWITCH_, items:[
						{type:_CASE_, relevant:"get('showBCC') != 'T'", items:[
							{type:_BUTTON_, label:"Show BCC", onChange:"this.xform.xmodel.set('showBCC','T')"}
						]},
						{type:_CASE_, relevant:"get('showBCC') == 'T'", items:[
							{type:_BUTTON_, label:"Hide BCC", onChange:"this.xform.xmodel.set('showBCC','F')"}
						]}
					]}
				]
			}
		]
	},


	model: {
		items: [
			{id:"subject", 	label:"Subject:",		type:_STRING_},
			{id:"to", 		label:"To:",			type:_STRING_},
			{id:"cc", 		label:"CC:",			type:_STRING_, relevant:"get('showCC') == 'T'"},
			{id:"bcc", 		label:"BCC:",			type:_STRING_, relevant:"get('showBCC') == 'T'"},
			{id:"attachments", 	type:_LIST_, items:[
					{id:"name", type:_STRING_}
				]
			},
			{id:"body", type:_STRING_},
			{id:"showCC",	type:_STRING_, value:"T"},
			{id:"showBCC",	type:_STRING_, value:"F"}
		]
	},
	
	instanceList: {
	
		"1" : {
			subject:"Message 1",
			to:"Owen",
			attachments:[
				{name:"some/file.txt"}
			],
			showCC:"T",
			showBCC:"F"
		},
		
		"2" : {
			subject:"Message 1",
			to:"Owen",
			attachments:[
				{name:"some/file.txt"},
				{name:"some/other/file.txt"}
			],
			showCC:"F",
			showBCC:"F"
		},
	
		"3" : {
			subject:"Message 1",
			to:"Owen",
			attachments:[
				{name:"some/file.txt"},
				{name:"some/other/file.txt"},
				{name:"a/third/file.txt"}
			],
			showCC:"T",
			showBCC:"T"
		}
	}
}


registerForm("Compose Message", new XForm(xftest.form, new XModel(xftest.model)), xftest.instanceList);
