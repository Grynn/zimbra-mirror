// xform
/*
var contactForm = {
	items: [
		{ ref: "fileAs", nowrap: true, fullWidth: true, cssClass: "card_big_name", cellCssClass:"card_big_name_cell" },
		{ ref: "attr/firstName", label: "First Name: ", cssClass: "contact", labelCssClass: "contact" },
		{ ref: "attr/lastName", label: "Last Name: ", cssClass: "contact", labelCssClass: "contact" },
		{ ref: "attr/middleName", label: "Middle Name: ", cssClass: "contact", labelCssClass: "contact" },
		{ ref: "attr/email", label: "Email: ", cssClass: "contact", labelCssClass: "contact" },
		{ ref: "attr/carPhone", label: "Car Phone: ", cssClass: "contact", labelCssClass: "contact", relevant: "get('carPhone') != null" },
		{ type: _GROUP_, numCols: 2, 
			items: [
				{ ref: "attr/firstName", label: "First Name: ", cssClass: "contact", labelCssClass: "contact" },
				{ ref: "attr/firstName", label: "First Name: ", cssClass: "contact", labelCssClass: "contact" },
				{ ref: "attr/firstName", label: "First Name: ", cssClass: "contact", labelCssClass: "contact" },
				{ ref: "attr/firstName", label: "First Name: ", cssClass: "contact", labelCssClass: "contact" }
			]
		}
	]
}
*/
var contactForm = {
	items: [
		{ type: _GROUP_, numCols: 3, label: "Name: ", labelClass: "smallLabel",
			items: [
				{ ref: "attr/firstName", type: _INPUT_ },
				{ ref: "attr/middleName", type: _INPUT_ },
				{ ref: "attr/lastName", type: _INPUT_ },
			]
		},
		{ ref: "attr/company", type: _INPUT_, label: "Company: ", labelClass: "smallLabel" },
		{ ref: "attr/jobTitle", type: _INPUT_, label: "Title: ", labelClass: "smallLabel" },
	]
}

// xmodel
var contactModel = {
	items: [	
		{ id: "fileAs", type: _STRING_, getter: "computeFileAs" },
		{ id: "firstName", type: _STRING_ },
		{ id: "lastName", type: _STRING_ },
		{ id: "middleName", type: _STRING_ },
		{ id: "company", type: _STRING_ },
		{ id: "jobTitle", type: _STRING_ },
		{ id: "email", type: _STRING_ },
		{ id: "carPhone", type: _STRING_ }
	],

	computeFileAs: function (instance) {
		return instance.attr.firstName + " " + instance.attr.middleName + " " + instance.attr.lastName;
	}
}

// form instance
var contactInstance = {
	"0" : { 
		attr: {
			firstName: "Parag",
			lastName: "Shah",
			middleName: "Naresh",
			company: "Liquid Systems",
			jobTitle: "Software Engineer",
			email: "pshah@liquidsys.com",
		}
	},
}

registerForm("Contact Test", new XForm(contactForm, new XModel(contactModel)), contactInstance);
//registerForm("Contact Test", new XForm(contactForm, null), contactInstance);
