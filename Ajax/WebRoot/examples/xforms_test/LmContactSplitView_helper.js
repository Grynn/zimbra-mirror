var owen = {
	attr: {
		fileAs:"Owen Williams",
		company:"Dynabooks Designs",
		workAddr:"5300 Broadway Terrace,<br>Oakland CA 94618",
		workStreet:"5300 Broadway Terrace",
		companyPhone:"company phone",
		workPhone:"work phone",
		workPhone2:"888-222-2222",
		workFax:"888-222-2222",
		assistantPhone:"888-222-2222",
		callbackPhone:"888-222-2222",
		workUrl:"http://www.dynabooks.com",
		homeAddr:"5300 Broadway Terrace,<br>Oakland CA 94618",
		homeStreet:"5300 Broadway Terrace",
		homePhone:"888-222-2222",
		homePhone2:"888-222-2222",
		homeFax:"888-222-2222",
		mobilePhone:"888-222-2222",
		pager:"888-222-2222",
		homeUrl:"http://www.owenwilliams.com",
		otherAddr:"5300 Broadway Terrace,<br>Oakland CA 94618",
		otherStreet:"5300 Broadway Terrace",
		otherPhone:"888-222-2222",
		otherFax:"888-222-2222",
		otherUrl:"http://other.com",
		email:"owen@smartsoul.com",
		email2:"owen@liquidsys.com",
		email3:"owen@dynabooks.com",
		notes:"Testing notes note note note note note notes!"
	}
};
var parag = {
	attr: {
		fileAs:"Parag Shah",
		company:"Liquid Systems",
		workAddr:"1500 Fashion Island Blvd<br>San Mateo, Ca 94414",
		workStreet:"1500 Fashion Island Blvd",
		workPhone:"888-222-2222",
		email:"parag@liquidsys.com",
		homePhone:"homephone"
		//notes:"Parag's notes!"
	}
}

var contactsList = {
	list:[
		{attr:{fileAs:"Janie O'Toole", email:"janie@liquidsys.com"}},
		{attr:{fileAs:"John Robb", email:"john.robb@liquidsys.com"}},
		owen,
		parag,
		{attr:{fileAs:"Ross Dargahi", email:"rossd@liquidsys.com"}},
		{attr:{fileAs:"Roland Schemers", email:"roland@liquidsys.com"}}
	]
}

if (window.LmContactXFormView) {
	LmContactXFormView.contactXModel.getterScope = _MODEL_;
	
	var XM = new XModel(LmContactXFormView.contactXModel);
	XM.getFileAs = function(instance, current, path) {			return current.attr.fileAs;}
	XM.getCompanyField = function(instance, current, path) {	return current.attr.company;}
	XM.getHomeAddrField = function(instance, current, path) {	return current.attr.homeAddr;}
	XM.getWorkAddrField = function(instance, current, path) {	return current.attr.workAddr;}
	XM.getOtherAddrField = function(instance, current, path) {	return current.attr.otherAddr;}

	registerForm("Contact View", new XForm(LmContactXFormView.contactInfoXForm, XM), {"Owen":owen, "Parag":parag});
	registerForm("Contact Edit", new XForm(LmContactXFormView.contactEditXForm, XM), {"Owen":owen, "Parag":parag});

	var listModelItems = [
		{id:"list", type:_LIST_, listItem:{
				type:_OBJECT_, 
				items:LmContactXFormView.contactXModel.items
			}
		}
	];
	var listModel = new XModel({
		items:listModelItems,
		getterScope:_MODEL_
	});
	listModel.getFileAs = XM.getFileAs;
	listModel.getCompanyField = XM.getCompanyField;
	listModel.getHomeAddrField = XM.getHomeAddrField;
	listModel.getWorkAddrField = XM.getWorkAddrField;
	listModel.getOtherAddrField = XM.getOtherAddrField;
	
	
	registerForm("Contact Cards", new XForm(LmContactXFormView.contactCardXForm, listModel), {"list":contactsList});
}
