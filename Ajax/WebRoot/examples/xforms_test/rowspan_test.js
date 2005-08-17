		{ type: _GROUP_, numCols: 3, colSpan:3, 
			items: [
				{ ref: "workAddr", rowSpan:6, label: "Work", verticalAlignment:_TOP_, labelCssClass:"contactLabelLeft"//,	relevantIfEmpty:false
				},
				{ type: _GROUP_, colSpan: 1, numCols:2, 
					items: [
						{ ref: "workPhone", label: "Phone: " },
						{ ref: "workPhone2", label: "Phone 2: " },
						{ ref: "workFax", label: "Fax: " },
						{ ref: "assistantPhone", label: "Assistant: " },
						{ ref: "companyPhone", label: "Company: " },
						{ ref: "callbackPhone", label: "Callback: " }
					]
				}
			]
		},
		{ ref: "workUrl", labelCssClass:"contactLabelLeft", colSpan:"*", label:"" },

		{ type: _SPACER_, height: 10, colSpan:"*",
			relevant: "get('workAddr') || get('workPhone') || get('workPhone2') || get('workFax') || get('assistantPhone') || get('companyPhone') || get('callbackPhone')" 
		},
		
		{ ref: "homeAddr", rowSpan:5, label: "Home", labelCssClass:"contactLabelLeft", relevant: "get('homeStreet') != null || get('homeCity') != null || get('homePostalCode') != null" },
		{ ref: "homePhone", label: "Phone:" },
		{ ref: "homePhone2", label: "Phone 2:" },
		{ ref: "homeFax", label: "Fax:" },
		{ ref: "mobilePhone", label: "Mobile:" },
		{ ref: "pager", label: "Pager:" },

		{ ref: "homeUrl", labelCssClass:"contactLabelLeft", label:"", colSpan:"*"},
		{ type: _SPACER_, colSpan:"*", height: 15, relevant: "get('homeStreet') != null || get('homeCity') != null || get('homePostalCode') != null || get('homeUrl') != null" },
