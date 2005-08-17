var _SUNDAY_ = "SUNDAY";
var _MONDAY_ = "MONDAY";
var _TUESDAY_ = "TUESDAY";
var _WEDNESDAY_ = "WEDNESDAY";
var _THURSDAY_ = "THURSDAY";
var _FRIDAY_ = "FRIDAY";
var _SATURDAY_ = "SATURDAY";

var _Sunday_initial_ = "S";
var _Monday_initial_ = "M";
var _Tuesday_initial_ = "T";
var _Wednesday_initial_ = "W";
var _Thursday_initial_ = "T";
var _Friday_initial_ = "F";
var _Saturday_initial_ = "S";

var _DAY_OF_WEEK_INITIAL_CHOICES_ = [
	{value:_SUNDAY_, label:_Sunday_initial_},		{value:_MONDAY_, label:_Monday_initial_},	
	{value:_TUESDAY_, label:_Tuesday_initial_},	{value:_WEDNESDAY_, label:_Wednesday_initial_},
	{value:_THURSDAY_, label:_Thursday_initial_},	{value:_FRIDAY_, label:_Friday_initial_},	
	{value:_SATURDAY_, label:_Saturday_initial_}
];


var _ATTENDEES_STATUS_CHOICES_ = [
	{value:"A",	label:"Accepted"},
	{value:"?",	label:"Unknown"},
	{value:"D",	label:"Denied"},
	{value:"T",	label:"Tentative"}
]


function WeekButtonGrid_XFormItem() {}
XFormItemFactory.createItemType("_WEEK_BUTTON_GRID_", "week_button_grid", WeekButtonGrid_XFormItem, _BUTTON_GRID_)
//	type defaults
WeekButtonGrid_XFormItem.prototype.numCols = 7;
WeekButtonGrid_XFormItem.prototype.cssClass = "xform_button_grid_small";
WeekButtonGrid_XFormItem.prototype.containerCssClass = "px300";
WeekButtonGrid_XFormItem.prototype.choices = _DAY_OF_WEEK_INITIAL_CHOICES_;




var xftest = {
	form : {
		id:"item_test",
		numCols:2,
		colSizes:[250,"100%"],
		items:[

			{ref:"ATTENDEES[0]/NAME", type:_INPUT_, label:"Nested from list (ATTENDEES[0]/NAME):", labelWrap:true, forceUpdate:true},
			{ref:"ATTENDEES[0]/FREE", type:_WEEK_BUTTON_GRID_, label:"Nested inserted item (ATTENDEES[0]/FREE)", labelWrap:true},
	
			{type:_SEPARATOR_, height:20},

			// group label
	
			{type:_GROUP_, useParentTable:false, numCols:1, label:"Manually Unrolled Group (always shows 3 items)", 
							labelCssClass:"xform_label", labelWrap:true, containerCssClass:"blue_border", 
				items:[
					{type:_GROUP_, ref:"ATTENDEES[0]", numCols:3, items : [
							{ref:"NAME", type:_INPUT_, containerCssClass:"px100"}, 
							{ref:"STATUS", type:_SELECT1_, containerCssClass:"px100"}, 
							{ref:"FREE", type:_BUTTON_GRID_, numCols:7, cssClass:"xform_button_grid_small"}
						]
					},
					{type:_GROUP_, ref:"ATTENDEES[1]", numCols:3, items : [
							{ref:"NAME", type:_INPUT_, containerCssClass:"px100"}, 
							{ref:"STATUS", type:_SELECT1_, containerCssClass:"px100"}, 
							{ref:"FREE", type:_BUTTON_GRID_, numCols:7, cssClass:"xform_button_grid_small"}
						]
					},
					{type:_GROUP_, ref:"ATTENDEES[2]", numCols:3, items : [
							{ref:"NAME", type:_INPUT_, containerCssClass:"px100"}, 
							{ref:"STATUS", type:_SELECT1_, containerCssClass:"px100"}, 
							{ref:"FREE", type:_BUTTON_GRID_, numCols:7, cssClass:"xform_button_grid_small"}
						]
					}
				]
			},
	
			{type:_SEPARATOR_, height:10},

			{ref:"ATTENDEES", type:_REPEAT_, colSpan:"*", number:1, numCols:3,
					label:"Dynamic group (changes w/ number of items)", labelWrap:true,
					repeatInstance:{NAME:"",STATUS:"?",FREE:""},
				items:[
					{ref:"NAME", type:_INPUT_, containerCssStyle:"width:100"},
					{ref:"STATUS", type:_SELECT1_, containerCssClass:"px100"},
					{ref:"FREE", type:_BUTTON_GRID_, numCols:7, cssClass:"xform_button_grid_small"}
				]
			},		

			{type:_SPACER_, height:10}
			
		]
	},
	
	model:{
		items: [
			{id:"SUBJECT", label:"Subject"},
			
			{id:"NESTED", items:[
					{id:"A", type:_STRING_},
					{id:"B", type:_STRING_},
					{id:"C", type:_STRING_}
				]
			},

			{id:"boolean", label:"All day", trueValue:true, falseValue:false},
	

			{id:"ATTENDEES", type:_LIST_, 
				listItem:{ type:_OBJECT_,
								items:[
									{id:"NAME", type:_STRING_},
									{id:"STATUS", type:_STRING_,	
										selection:_OPEN_,
										choices:_ATTENDEES_STATUS_CHOICES_	
									},
									{id:"FREE", type:_STRING_, 		choices:_DAY_OF_WEEK_INITIAL_CHOICES_}
								]
							}
			},
	
			{id:"GRID_TEST", type:_STRING_}
		]
	},
	
	instanceList:{
		"Four Items" : {
			ATTENDEES : [
				{NAME:"Owen", STATUS:"A", FREE:"MONDAY"},
				{NAME:"Enrique", STATUS:"?"},
				{NAME:"Ross", STATUS:"D", FREE:"WEDNESDAY"},
				{NAME:"Roland", STATUS:"T"}
			],
			NESTED : {
				A:"A simple nested value",
				B:"B simple nested value",
				C:"C simple nested value"
			},
			GRID_TEST:"WEDNESDAY,MONDAY"
		},
		
		"No Items" : {
		},
	
		"Two items" : {
			ATTENDEES : [
				{NAME:"Satish", STATUS:"A", FREE:"MONDAY"},
				{NAME:"John", STATUS:"?"}
			]
		},
		
		"Six items" : {
			ATTENDEES : [
				{NAME:"Satish", STATUS:"A", FREE:"MONDAY"},
				{NAME:"John", STATUS:"?"},
				{NAME:"Scott", STATUS:"D"},
				{NAME:"Andy", STATUS:"T", FREE:"TUESDAY"},
				{NAME:"Enrique", STATUS:"T", FREE:"TUESDAY"},
				{NAME:"Owen", STATUS:"T", FREE:"WEDNESDAY"}
			]
		},
		
		"Twenty items" : {
			ATTENDEES : [
				{NAME:"Albert", STATUS:"?", FREE:"MONDAY"},
				{NAME:"Betty", STATUS:"D"},
				{NAME:"Charlie", STATUS:"A"},
				{NAME:"Dawne", STATUS:"D", FREE:"TUESDAY"},
				{NAME:"Elbert", STATUS:"T", FREE:"TUESDAY"},
				{NAME:"Frannie", STATUS:"T", FREE:"WEDNESDAY"},
				{NAME:"George", STATUS:"A", FREE:"MONDAY"},
				{NAME:"Herbert", STATUS:"?"},
				{NAME:"Isa", STATUS:"D"},
				{NAME:"Julia", STATUS:"T", FREE:"TUESDAY"},
				{NAME:"Kenneth", STATUS:"A", FREE:"MONDAY"},
				{NAME:"Laurence", STATUS:"?"},
				{NAME:"Matthew", STATUS:"D"},
				{NAME:"Nancy", STATUS:"T", FREE:"TUESDAY"},
				{NAME:"Owen", STATUS:"T", FREE:"TUESDAY"},
				{NAME:"Paul", STATUS:"T", FREE:"WEDNESDAY"},
				{NAME:"Quince", STATUS:"A", FREE:"MONDAY"},
				{NAME:"Roger", STATUS:"?"},
				{NAME:"Steve", STATUS:"D"},
				{NAME:"Tawny", STATUS:"T", FREE:"TUESDAY"}
			]
		}	
	
	
	}
}
var model = new XModel(xftest.model);
registerForm("Repeat Test", new XForm(xftest.form, model), xftest.instanceList);


