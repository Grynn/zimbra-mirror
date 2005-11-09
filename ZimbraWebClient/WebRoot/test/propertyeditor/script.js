function App() {
	this.shell = new DwtShell("MainShell", false, null, null, false);
	this.shell._setMouseEventHdlrs();
	this.shell.addListener(DwtEvent.ONMOUSEMOVE, new AjxListener(this, this.func));

	var pi = this._pi = new DwtPropertyEditor(this.shell, null, "absolute");

	pi.setBounds(100, 100, 500, 500);
	pi.setZIndex(700);

	var prop = [

		{ label          : "User ID",
		  id             : "userid",
		  type           : "string",
		  value          : "rootshell",
		  minLength      : 4,
		  maxLength      : 16,
		  mustMatch      : /^[a-z][a-z0-9-]+$/i,
		  mustNotMatch   : /^(root|guest|admin)$/i
		},

		{ label      : "Password",
		  id         : "passwd",
		  type       : "password",
		  minLength  : 4,
		  maxLength  : 8,
		  value      : "default"
		},

		{ label      : "Read-only field",
		  id         : "readonly",
		  readonly   : true,
		  value      : "Grootesque"
		},

		{ label      : "Address",
		  id         : "address",
		  type       : "struct",
		  children   : [

			  { label  : "Street",
			    id     : "street",
			    type   : "string",
			    value  : "Al. T. Neculai, nr. 19, bl. 945, ap. 2"
			  },

			  { label  : "City",
			    id     : "city",
			    type   : "string",
			    value  : "Iasi"
			  },

			  { label    : "Postal code",
			    id       : "postCode",
			    type     : "string",
			    value    : "700713",
			    required : true
			  },

			  { label    : "Dates",
			    id       : "foo",
			    type     : "struct",
			    children : [

				    { label : "Birthday",
				      type  : "date",
				      id    : "birthday"
				    },

				    { label : "Age",
				      type  : "integer",
				      id    : "age"
				    }

				    ]
			  },

			  { label    : "State",
			    id       : "state",
			    type     : "string",
			    value    : "Iasi"
			  },

			  { label  : "Country",
			    id     : "country",
			    type   : "string",
			    value  : "Romania"
			  }

			  ]
		},

		{ label      : "Integer",
		  id         : "integer",
		  type       : "integer",
		  minValue   : 10,
		  maxValue   : 30
		},

		{ label      : "Float",
		  id         : "float",
		  type       : "number",
		  minValue   : 15,
		  maxValue   : 25
		},

		{ label      : "Decimal",
		  id         : "decimal",
		  type       : "number",
		  decimals   : 2
		}

		];

	pi.initProperties(prop);
	pi.makeFixedLabelWidth();
};

App.run = function() {
	new App();
};

App.prototype.func = function(ev) {
	window.status = "(" + ev.docX + ", " + ev.docY + ") on a " + ev.target.tagName;
};
