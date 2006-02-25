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


if (window.Cos_String_XModelItem) {
	var model = new XModel({
		items:[
			{ id:"name", type:_COS_STRING_},
			{ id:"length", type:_COS_NUMBER_}
		]
	});
	
	var formAttr ={
		items:[
			{ ref:"name", type:_COS_TEXTFIELD_, label:"Name", valueLabel:""},
			{ ref:"length", type:_COS_TEXTFIELD_, label:"Length", valueLabel:"millimeters"}
		]
	};


	var instances = {
		account1:{
			cos:{ 
				attr: {
					name:"COS Name", 
					length:10
				}
			},
			account:{
				attr: {
					name:"Account 1 name",
					length:null
				}
			}
		},
		account2:{
			cos:{ 
				attr: {
					name:"COS Name", 
					length:10
				}
			},
			account:{
				attr: {
					name:"Account 2 name",
					length:20
				}
			}
		},
		empty:{
			cos:{
				attr: {
				}				
			},
			account:{
				attr: {
				}
			}
		}
	
	}

	registerForm("COS Fields", new XForm(formAttr, model), instances);
}
