if(ZaItem) {
	ZaItem.SAMBA_SAM_ACCOUNT = "sambaSamDomain";
}
function ZaSambaGroupMapping() {
		
	ZaItem.call(this,"ZaSambaGroupMapping");
	this.type = ZaItem.SAMBA_SAM_ACCOUNT;
	this._init();
}

ZaSambaGroupMapping.A_isSpecialNTGroup = "isSpecialNTGroup";
ZaSambaGroupMapping.A_specialNTGroupType = "specialNTGroupType";
ZaSambaGroupMapping.A_sambaDomainSID = "sambaDomainSID";
ZaSambaGroupMapping.A_sambaSID = "sambaSID";
ZaSambaGroupMapping.A_sambaGroupType = "sambaGroupType";
ZaSambaGroupMapping.A_displayName = "displayName";

ZaSambaGroupMapping.Domain_Admins = 512;
ZaSambaGroupMapping.Domain_Users = 513;
ZaSambaGroupMapping.Domain_Guests = 514;
ZaSambaGroupMapping.Domain_Computers = 515;
ZaSambaGroupMapping.Domain_Controllers = 516;
ZaSambaGroupMapping.Domain_Certificate_Admins = 517;
ZaSambaGroupMapping.Domain_Schema_Admins = 518;
ZaSambaGroupMapping.Domain_Enterprise_Admins = 519;
ZaSambaGroupMapping.Domain_Policy_Admins = 520;
ZaSambaGroupMapping.Builtin_Admins = 544;
ZaSambaGroupMapping.Builtin_users = 545;
ZaSambaGroupMapping.Builtin_Guests = 546;
ZaSambaGroupMapping.Builtin_Power_Users = 547;
ZaSambaGroupMapping.Builtin_Account_Operators = 548;
ZaSambaGroupMapping.Builtin_System_Operators = 549;
ZaSambaGroupMapping.Builtin_Print_Operators = 550;
ZaSambaGroupMapping.Builtin_Backup_Operators = 551;
ZaSambaGroupMapping.Builtin_Replicator = 552;
ZaSambaGroupMapping.Builtin_RAS_Servers = 553;

if(ZaPosixGroup.myXModel && ZaPosixGroup.myXModel.items) {
		ZaPosixGroup.myXModel.items.push({id:ZaSambaGroupMapping.A_isSpecialNTGroup,type:_NUMBER_, defaultValue:0,ref:ZaSambaGroupMapping.A_isSpecialNTGroup});
		ZaPosixGroup.myXModel.items.push({id:ZaSambaGroupMapping.A_specialNTGroupType,type:_NUMBER_, defaultValue:0,ref:ZaSambaGroupMapping.A_specialNTGroupType});		
		ZaPosixGroup.myXModel.items.push({id:ZaSambaGroupMapping.A_sambaDomainSID,type:_STRING_,ref:ZaSambaGroupMapping.A_sambaDomainSID});				
				
		ZaPosixGroup.myXModel.items.push({id:ZaSambaGroupMapping.A_sambaGroupType,type:_NUMBER_, defaultValue:2,ref:"attrs/"+ZaSambaGroupMapping.A_sambaGroupType,required:true});		
		ZaPosixGroup.myXModel.items.push({id:ZaSambaGroupMapping.A_sambaSID,type:_STRING_,ref:"attrs/"+ZaSambaGroupMapping.A_sambaSID,required:true});				
		ZaPosixGroup.myXModel.items.push({id:ZaSambaGroupMapping.A_displayName,type:_STRING_,ref:"attrs/"+ZaSambaGroupMapping.A_displayName});						
}

ZaSambaGroupMapping.specialNTGorupChoices = [
	{value:ZaSambaGroupMapping.Domain_Admins, label:"Domain Admins"},
	{value:ZaSambaGroupMapping.Domain_Users, label:"Domain Users"},
	{value:ZaSambaGroupMapping.Domain_Guests, label:"Domain Guests"},
	{value:ZaSambaGroupMapping.Domain_Computers, label:"Domain Computers"},
	{value:ZaSambaGroupMapping.Domain_Controllers, label:"Domain Controllers"},
	{value:ZaSambaGroupMapping.Domain_Certificate_Admins, label:"Domain Certificate Admins"},
	{value:ZaSambaGroupMapping.Domain_Schema_Admins, label:"Domain Schema Admins"},
	{value:ZaSambaGroupMapping.Domain_Enterprise_Admins, label:"Domain Enterprise Admins"},
	{value:ZaSambaGroupMapping.Domain_Policy_Admins, label:"Domain Policy Admins"},
	{value:ZaSambaGroupMapping.Builtin_Admins, label:"Builtin Admins"},
	{value:ZaSambaGroupMapping.Builtin_users, label:"Builtin users"},
	{value:ZaSambaGroupMapping.Builtin_Guests, label:"Builtin Guests"},
	{value:ZaSambaGroupMapping.Builtin_Power_Users, label:"Builtin Power Users"},
	{value:ZaSambaGroupMapping.Builtin_Account_Operators, label:"Builtin Account Operators"},
	{value:ZaSambaGroupMapping.Builtin_System_Operators, label:"Builtin System Operators"},
	{value:ZaSambaGroupMapping.Builtin_Print_Operators, label:"Builtin Print Operators"},
	{value:ZaSambaGroupMapping.Builtin_Backup_Operators, label:"Builtin Backup Operators"},
	{value:ZaSambaGroupMapping.Builtin_Replicator, label:"Builtin Replicator"},
	{value:ZaSambaGroupMapping.Builtin_RAS_Servers, label:"Builtin RAS Servers"}
];

ZaSambaGroupMapping.loadMethod = function(by, val, withCos) {
	if(this.attrs && this.attrs[ZaSamAccount.A_sambaSID]) {
		var chunks = this.attrs[ZaSamAccount.A_sambaSID].split("-");
		var groupRid = parseInt(chunks.pop());
		if(
			(groupRid >=ZaSambaGroupMapping.Domain_Admins &&
			groupRid <=ZaSambaGroupMapping.Domain_Policy_Admins) ||
			(groupRid >=ZaSambaGroupMapping.Builtin_Admins &&
			groupRid <=ZaSambaGroupMapping.Builtin_RAS_Servers)
		) {
			this[ZaSambaGroupMapping.A_isSpecialNTGroup] = 1;
		} else {
			this[ZaSambaGroupMapping.A_isSpecialNTGroup] = 0;			
		}
	}
}

if(ZaItem.loadMethods["ZaPosixGroup"]) {
	ZaItem.loadMethods["ZaPosixGroup"].push(ZaSambaGroupMapping.loadMethod);
}

ZaSambaGroupMapping.initMethod = function () {
	this.attrs[ZaItem.A_objectClass].push("sambaGroupMapping");
}
if(ZaItem.initMethods["ZaPosixGroup"]) {
	ZaItem.initMethods["ZaPosixGroup"].push(ZaSambaGroupMapping.initMethod);
}

if(ZaTabView.XFormModifiers["ZaPosixGroupXFormView"]) {
	ZaSambaGroupMapping.myXFormModifier = function (xFormObject) {
		var cnt = xFormObject.items.length;
		var i = 0;
		for(i = 0; i <cnt; i++) {
			if(xFormObject.items[i].type=="switch") 
				break;
		}
		cnt = xFormObject.items[i].items.length;
		var sambaTabIx = cnt+1;
		
		var tabBar = xFormObject.items[1] ;
		tabBar.choices.push({value:sambaTabIx, label:"Samba Group"});		
		var sambaGroupTab={type:_ZATABCASE_, numCols:1, relevant:("instance[ZaModel.currentTab] == " + sambaTabIx),
					items: [
						{type:_ZAGROUP_, 
							items:[
								{ref:ZaSambaGroupMapping.A_sambaDomainSID, type:_OSELECT1_, editable:false,choices:ZaApp.getInstance().getSambaDomainSIDListChoices(true), msgName:"Samba domain",label:"Samba domain", labelLocation:_LEFT_,
									onChange:ZaTabView.onFormFieldChanged,
									elementChanged:function(val,instanceValue, event) {
										var v = val;
										var instance = this.getInstance();
										var form = this.getForm();
										var myChoices = this.getChoices();
										if(instance && !instance[ZaSambaGroupMapping.A_isSpecialNTGroup]) {
											instance.attrs[ZaSambaGroupMapping.A_sambaSID]	= val + "-" + 
												(
													(parseInt(instance.attrs[ZaPosixGroup.A_gidNumber]) ? parseInt(instance.attrs[ZaPosixGroup.A_gidNumber])*2 : parseInt(Zambra.gidBase)) +
													(parseInt(Zambra.ridBase) ? parseInt(Zambra.ridBase) : 0)
												);
										} else if (instance && instance[ZaSambaGroupMapping.A_isSpecialNTGroup]) {
											instance.attrs[ZaSambaGroupMapping.A_sambaSID]	= val + "-" + instance[ZaSambaGroupMapping.A_specialNTGroupType];
										}
										if(form)
											form.itemChanged(this, val, event);		
												
									},
									getDisplayValue:function(val) {
										if (val) {
											val = this.getChoiceLabel(val);
										} else {
											var instance = this.getInstance();
											if(instance.attrs[ZaSambaGroupMapping.A_sambaSID]) {
												var chunks = instance.attrs[ZaSambaGroupMapping.A_sambaSID].split("-");
												var userRid = chunks.pop();
												
												val = chunks.join("-");
												instance[ZaSambaGroupMapping.A_sambaDomainSID] = val;
											}
										}
										return val;
									}
								},	
								{ref:ZaSambaGroupMapping.A_isSpecialNTGroup, 
									type:_CHECKBOX_,  
									msgName:"Special Windows group",
									label:"Special Windows group",
									trueValue:1, falseValue:0, 
									onChange:ZaTabView.onFormFieldChanged,
								},
								{ref:ZaSambaGroupMapping.A_specialNTGroupType,
									relevant:"instance[ZaSambaGroupMapping.A_isSpecialNTGroup]",
									relevantBehavior:_DISABLE_,
									type:_OSELECT1_, msgName:"Special Windows group type",
									label:"Special Windows group type", 
									labelLocation:_LEFT_, 
									choices:ZaSambaGroupMapping.specialNTGorupChoices, 
									onChange:ZaTabView.onFormFieldChanged,
									getDisplayValue:function(val) {
										if (val) {
											val = this.getChoiceLabel(val);
										} else {
											var instance = this.getInstance();
											if(instance.attrs[ZaSambaGroupMapping.A_sambaSID]) {
												var chunks = instance.attrs[ZaSambaGroupMapping.A_sambaSID].split("-");
												var groupRid = chunks.pop();
												val = this.getChoiceLabel(groupRid);
											}
										}
										return val;
									},
									elementChanged:function(val,instanceValue, event) {
										var instance = this.getInstance();
										var form = this.getForm();
										if(instance.attrs[ZaSambaGroupMapping.A_sambaSID]) {
											var chunks = instance.attrs[ZaSambaGroupMapping.A_sambaSID].split("-");
											var groupRid = chunks.pop();
											chunks.push(val);
											instance.attrs[ZaSambaGroupMapping.A_sambaSID] = chunks.join("-");
										}			
										if(form)
											form.itemChanged(this, val, event);																		
									}									
								},								
								{ref:ZaSambaGroupMapping.A_sambaSID, type:_TEXTFIELD_, msgName:ZaSambaGroupMapping.A_sambaSID,label:ZaSambaGroupMapping.A_sambaSID, labelLocation:_LEFT_, onChange:ZaTabView.onFormFieldChanged,width:300},
								{ref:ZaSambaGroupMapping.A_sambaGroupType, type:_TEXTFIELD_, msgName:ZaSambaGroupMapping.A_sambaGroupType,label:ZaSambaGroupMapping.A_sambaGroupType, labelLocation:_LEFT_, cssClass:"admin_xform_number_input"},
								{ref:ZaSambaGroupMapping.A_displayName, type:_TEXTFIELD_, msgName:ZaSambaGroupMapping.A_displayName,label:ZaSambaGroupMapping.A_displayName, labelLocation:_LEFT_, onChange:ZaTabView.onFormFieldChanged}							
							]
				}	]
			};
		xFormObject.items[i].items.push(sambaGroupTab);
	}
	ZaTabView.XFormModifiers["ZaPosixGroupXFormView"].push(ZaSambaGroupMapping.myXFormModifier);	
}

