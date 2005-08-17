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
