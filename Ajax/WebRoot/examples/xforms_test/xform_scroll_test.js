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
	numCols:1,
	tableCssStyle:"width:100%;height:100%;",			// NECESSARY
	items:[
				{type:_GROUP_, useParentTable:false, 
					containerCssStyle:"height:1", 		// NECESSARY, ACTUAL HEIGHT DOESN'T MATTER (will grow to fit)
					items:[
						{type:_OUTPUT_, value:"The middle stuff in the form will scroll, but not the top and bottom!"}
					]
				},
				{type:_GROUP_, useParentTable:false, numCols:1,
					cssStyle:"position:relative;overflow:auto;height:100%;width:100%",		// NECESSARY
					items:[
						{type:_GROUP_, numCols:1, 
							cssStyle:"position:absolute;", 									// NECESSARY
							items:[
								{type:_OUTPUT_, ref:"bigText"},
								{type:_SEPARATOR_},
								{type:_OUTPUT_, ref:"bigText"}
							]
						}
					]
				},
				{type:_GROUP_, useParentTable:false, 
					containerCssStyle:"height:20", 		// NECESSARY, ACTUAL HEIGHT DOESN'T MATTER (will grow to fit)
					items:[
						{type:_OUTPUT_, value:"Bottom stuff"}
					]
				}
			]
}

var instances = {
	instance0:{
		bigText: "text<BR>text<BR>text<BR>text<BR>text<BR>text<BR>text<BR>text<BR>text<BR>text<BR>text<BR>text<BR>text<BR>text<BR>text<BR>text<BR>text<BR>text<BR>text<BR>text<BR>text<BR>text<BR>text<BR>text<BR>"		
  			   + "text<BR>text<BR>text<BR>text<BR>text<BR>text<BR>text<BR>text<BR>text<BR>text<BR>text<BR>text<BR>text<BR>text<BR>text<BR>text<BR>text<BR>text<BR>text<BR>text<BR>text<BR>text<BR>text<BR>text<BR>text<BR>"
  			   + "text<BR>text<BR>text<BR>text<BR>text<BR>text<BR>text<BR>text<BR>text<BR>text<BR>text<BR>text<BR>text<BR>text<BR>text<BR>text<BR>text<BR>text<BR>text<BR>text<BR>text<BR>text<BR>text<BR>text<BR>text<BR>"
	},
	instance1:{
		bigText:"texttexttexttexttexttexttexttexttexttexttexttexttexttexttexttexttexttexttexttexttexttexttexttexttexttexttexttexttexttexttexttexttexttexttexttexttexttexttexttexttexttexttexttexttexttexttexttexttexttext<BR>"
			   + "text<BR>text<BR>text<BR>text<BR>text<BR>text<BR>text<BR>text<BR>text<BR>text<BR>text<BR>text<BR>text<BR>text<BR>text<BR>text<BR>text<BR>text<BR>text<BR>text<BR>text<BR>text<BR>text<BR>text<BR>"		
  			   + "text<BR>text<BR>text<BR>text<BR>text<BR>text<BR>text<BR>text<BR>text<BR>text<BR>text<BR>text<BR>text<BR>text<BR>text<BR>text<BR>text<BR>text<BR>text<BR>text<BR>text<BR>text<BR>text<BR>text<BR>text<BR>"
  			   + "text<BR>text<BR>text<BR>text<BR>text<BR>text<BR>text<BR>text<BR>text<BR>text<BR>text<BR>text<BR>text<BR>text<BR>text<BR>text<BR>text<BR>text<BR>text<BR>text<BR>text<BR>text<BR>text<BR>text<BR>text<BR>"
	},
	instance2:{
		bigText:"texttext  texttext texttext texttext texttext texttext texttext texttext texttext texttext texttext texttext texttext texttext texttext texttext texttext texttext texttext texttext texttext texttext texttext texttext texttext<BR>"
			   + "text<BR>text<BR>text<BR>text<BR>text<BR>text<BR>text<BR>text<BR>text<BR>text<BR>text<BR>text<BR>text<BR>text<BR>text<BR>text<BR>text<BR>text<BR>text<BR>text<BR>text<BR>text<BR>text<BR>text<BR>"		
  			   + "text<BR>text<BR>text<BR>text<BR>text<BR>text<BR>text<BR>text<BR>text<BR>text<BR>text<BR>text<BR>text<BR>text<BR>text<BR>text<BR>text<BR>text<BR>text<BR>text<BR>text<BR>text<BR>text<BR>text<BR>text<BR>"
  			   + "text<BR>text<BR>text<BR>text<BR>text<BR>text<BR>text<BR>text<BR>text<BR>text<BR>text<BR>text<BR>text<BR>text<BR>text<BR>text<BR>text<BR>text<BR>text<BR>text<BR>text<BR>text<BR>text<BR>text<BR>text<BR>"
	},
	empty:{}
}


//var model = new XModel(model);
registerForm("Scroll test", new XForm(form), instances);

