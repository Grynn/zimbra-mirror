/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2007, 2009 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.2 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */

/**
* XFormItem class: "acl (composite item)
* this item is used in the Admin UI to display SambaAcctFlags
* D - Account is disabled
* H - a home directory required
* I - an inter-domain trust account
* L - Account has been auto-blocked
* M - an MNS logon account
* N - Password not required
* S - a server trust account
* T - temporary duplicate account entry
* U - a normal user account
* W - a workstation trust account
* X - Password does not expire
* @class ZaSambaAcFlagsXFormItem
* @constructor ZaSambaAcFlagsXFormItem
* @author Greg Solovyev
**/
function ZaSambaAcFlagsXFormItem() {}
XFormItemFactory.createItemType("_SAMBAACFLAGS_", "sambaacflags", ZaSambaAcFlagsXFormItem, Composite_XFormItem);
ZaSambaAcFlagsXFormItem.prototype.numCols = 2;
ZaSambaAcFlagsXFormItem.prototype.nowrap = true;
ZaSambaAcFlagsXFormItem.prototype.initializeItems = 
function () {
	this.items = [];
	//U
		this.items.push(	
			{type:_CHECKBOX_,width:"40px",containerCssStyle:"width:40px", forceUpdate:true, ref:".", 
				labelLocation:_RIGHT_, label:"Normal user account", relevantBehavior:_PARENT_,
				getDisplayValue:function (itemval) {
					return (itemval && itemval.indexOf("U")>0);
				},
				elementChanged:function(isChecked, instanceValue, event) {
					var U,D, H, I, L, M, N, S, T, W, X;
					if(instanceValue) {
						D = instanceValue.indexOf("D") > 0;
						H = instanceValue.indexOf("H") > 0;
						I = instanceValue.indexOf("I") > 0;						
						L = instanceValue.indexOf("L") > 0;						
						M = instanceValue.indexOf("M") > 0;												
						N = instanceValue.indexOf("N") > 0;																		
						S = instanceValue.indexOf("S") > 0;						
						T = instanceValue.indexOf("T") > 0;
						W = instanceValue.indexOf("W") > 0;
						X = instanceValue.indexOf("X") > 0;						
					} 
					U = isChecked;
					var newVal = "[";
					if(D) newVal += "D";
					if(H) newVal += "H";
					if(I) newVal += "I";						
					if(L) newVal += "L";						
					if(M) newVal += "M";												
					if(N) newVal += "N";																		
					if(S) newVal += "S";						
					if(T) newVal += "T";
					if(U) newVal += "U";					
					if(W) newVal += "W";
					if(X) newVal += "X";	
					newVal += "]";					
					
					this.getForm().itemChanged(this.getParentItem(), newVal, event);
				}
			});
			
	//X	
		this.items.push(	
			{type:_CHECKBOX_,width:"40px",containerCssStyle:"width:40px", forceUpdate:true, ref:".", 
				labelLocation:_RIGHT_, label:"Password does not expire", relevantBehavior:_PARENT_,
				getDisplayValue:function (itemval) {
					return (itemval && itemval.indexOf("X")>0);
				},
				elementChanged:function(isChecked, instanceValue, event) {
					var U, D, H, I, L, M, N, S, T, W, X;
					if(instanceValue) {
						U = instanceValue.indexOf("U") > 0;
						D = instanceValue.indexOf("D") > 0;
						H = instanceValue.indexOf("H") > 0;						
						I = instanceValue.indexOf("I") > 0;						
						L = instanceValue.indexOf("L") > 0;												
						M = instanceValue.indexOf("M") > 0;																		
						N = instanceValue.indexOf("N") > 0;						
						S = instanceValue.indexOf("S") > 0;
						T = instanceValue.indexOf("T") > 0;
						W = instanceValue.indexOf("W") > 0;						
					} 
					X = isChecked;
					var newVal = "[";
					if(D) newVal += "D";
					if(H) newVal += "H";
					if(I) newVal += "I";						
					if(L) newVal += "L";						
					if(M) newVal += "M";												
					if(N) newVal += "N";																		
					if(S) newVal += "S";						
					if(T) newVal += "T";
					if(U) newVal += "U";					
					if(W) newVal += "W";
					if(X) newVal += "X";	
					newVal += "]";					
					
					this.getForm().itemChanged(this.getParentItem(), newVal, event);
				}
			});
						
	//D		
		this.items.push(	
			{type:_CHECKBOX_,width:"40px",containerCssStyle:"width:40px", forceUpdate:true, ref:".", 
				labelLocation:_RIGHT_, label:"Account is disabled", relevantBehavior:_PARENT_,
				getDisplayValue:function (itemval) {
					return (itemval && itemval.indexOf("D")>0);
				},
				elementChanged:function(isChecked, instanceValue, event) {
					var U,D, H, I, L, M, N, S, T, W, X;
					if(instanceValue) {
						U = instanceValue.indexOf("U") > 0;
						H = instanceValue.indexOf("H") > 0;
						I = instanceValue.indexOf("I") > 0;						
						L = instanceValue.indexOf("L") > 0;						
						M = instanceValue.indexOf("M") > 0;												
						N = instanceValue.indexOf("N") > 0;																		
						S = instanceValue.indexOf("S") > 0;						
						T = instanceValue.indexOf("T") > 0;
						W = instanceValue.indexOf("W") > 0;
						X = instanceValue.indexOf("X") > 0;						
					} 
					D = isChecked;
					var newVal = "[";
					if(D) newVal += "D";
					if(H) newVal += "H";
					if(I) newVal += "I";						
					if(L) newVal += "L";						
					if(M) newVal += "M";												
					if(N) newVal += "N";																		
					if(S) newVal += "S";						
					if(T) newVal += "T";
					if(U) newVal += "U";
					if(W) newVal += "W";
					if(X) newVal += "X";	
					newVal += "]";					
					
					this.getForm().itemChanged(this.getParentItem(), newVal, event);
				}
			});		
	//H	
		this.items.push(	
			{type:_CHECKBOX_,width:"40px",containerCssStyle:"width:40px", forceUpdate:true, ref:".", 
				labelLocation:_RIGHT_, label:"Home directory required", relevantBehavior:_PARENT_,
				getDisplayValue:function (itemval) {
					return (itemval && itemval.indexOf("H")>0);
				},
				elementChanged:function(isChecked, instanceValue, event) {
					var U, D, H, I, L, M, N, S, T, W, X;
					if(instanceValue) {
						U = instanceValue.indexOf("U") > 0;
						D = instanceValue.indexOf("D") > 0;
						I = instanceValue.indexOf("I") > 0;						
						L = instanceValue.indexOf("L") > 0;						
						M = instanceValue.indexOf("M") > 0;												
						N = instanceValue.indexOf("N") > 0;																		
						S = instanceValue.indexOf("S") > 0;						
						T = instanceValue.indexOf("T") > 0;
						W = instanceValue.indexOf("W") > 0;
						X = instanceValue.indexOf("X") > 0;						
					} 
					H = isChecked;
					var newVal = "[";
					if(D) newVal += "D";
					if(H) newVal += "H";
					if(I) newVal += "I";						
					if(L) newVal += "L";						
					if(M) newVal += "M";												
					if(N) newVal += "N";																		
					if(S) newVal += "S";						
					if(T) newVal += "T";
					if(U) newVal += "U";					
					if(W) newVal += "W";
					if(X) newVal += "X";	
					newVal += "]";					
					
					this.getForm().itemChanged(this.getParentItem(), newVal, event);
				}
			});	
	//I	
		this.items.push(	
			{type:_CHECKBOX_,width:"40px",containerCssStyle:"width:40px", forceUpdate:true, ref:".", 
				labelLocation:_RIGHT_, label:"Inter-domain trust account", relevantBehavior:_PARENT_,
				getDisplayValue:function (itemval) {
					return (itemval && itemval.indexOf("I")>0);
				},
				elementChanged:function(isChecked, instanceValue, event) {
					var U, D, H, I, L, M, N, S, T, W, X;
					if(instanceValue) {
						U = instanceValue.indexOf("U") > 0;
						D = instanceValue.indexOf("D") > 0;
						H = instanceValue.indexOf("H") > 0;						
						L = instanceValue.indexOf("L") > 0;						
						M = instanceValue.indexOf("M") > 0;												
						N = instanceValue.indexOf("N") > 0;																		
						S = instanceValue.indexOf("S") > 0;						
						T = instanceValue.indexOf("T") > 0;
						W = instanceValue.indexOf("W") > 0;
						X = instanceValue.indexOf("X") > 0;						
					} 
					I = isChecked;
					var newVal = "[";
					if(D) newVal += "D";
					if(H) newVal += "H";
					if(I) newVal += "I";						
					if(L) newVal += "L";						
					if(M) newVal += "M";												
					if(N) newVal += "N";																		
					if(S) newVal += "S";						
					if(T) newVal += "T";
					if(U) newVal += "U";					
					if(W) newVal += "W";
					if(X) newVal += "X";	
					newVal += "]";					
					
					this.getForm().itemChanged(this.getParentItem(), newVal, event);
				}
			});	
	//L	
		this.items.push(	
			{type:_CHECKBOX_,width:"40px",containerCssStyle:"width:40px", forceUpdate:true, ref:".", 
				labelLocation:_RIGHT_, label:"Account has been auto-blocked", relevantBehavior:_PARENT_,
				getDisplayValue:function (itemval) {
					return (itemval && itemval.indexOf("L")>0);
				},
				elementChanged:function(isChecked, instanceValue, event) {
					var U, D, H, I, L, M, N, S, T, W, X;
					if(instanceValue) {
						U = instanceValue.indexOf("U") > 0;
						D = instanceValue.indexOf("D") > 0;
						H = instanceValue.indexOf("H") > 0;						
						I = instanceValue.indexOf("I") > 0;						
						M = instanceValue.indexOf("M") > 0;												
						N = instanceValue.indexOf("N") > 0;																		
						S = instanceValue.indexOf("S") > 0;						
						T = instanceValue.indexOf("T") > 0;
						W = instanceValue.indexOf("W") > 0;
						X = instanceValue.indexOf("X") > 0;						
					} 
					L = isChecked;
					var newVal = "[";
					if(D) newVal += "D";
					if(H) newVal += "H";
					if(I) newVal += "I";						
					if(L) newVal += "L";						
					if(M) newVal += "M";												
					if(N) newVal += "N";																		
					if(S) newVal += "S";						
					if(T) newVal += "T";
					if(U) newVal += "U";					
					if(W) newVal += "W";
					if(X) newVal += "X";	
					newVal += "]";					
					
					this.getForm().itemChanged(this.getParentItem(), newVal, event);
				}
			});	
			
	//M	
		this.items.push(	
			{type:_CHECKBOX_,width:"40px",containerCssStyle:"width:40px", forceUpdate:true, ref:".", 
				labelLocation:_RIGHT_, label:"MNS logon account", relevantBehavior:_PARENT_,
				getDisplayValue:function (itemval) {
					return (itemval && itemval.indexOf("M")>0);
				},
				elementChanged:function(isChecked, instanceValue, event) {
					var U, D, H, I, L, M, N, S, T, W, X;
					if(instanceValue) {
						U = instanceValue.indexOf("U") > 0;
						D = instanceValue.indexOf("D") > 0;
						H = instanceValue.indexOf("H") > 0;						
						I = instanceValue.indexOf("I") > 0;						
						L = instanceValue.indexOf("L") > 0;												
						N = instanceValue.indexOf("N") > 0;																		
						S = instanceValue.indexOf("S") > 0;						
						T = instanceValue.indexOf("T") > 0;
						W = instanceValue.indexOf("W") > 0;
						X = instanceValue.indexOf("X") > 0;						
					} 
					M = isChecked;
					var newVal = "[";
					if(D) newVal += "D";
					if(H) newVal += "H";
					if(I) newVal += "I";						
					if(L) newVal += "L";						
					if(M) newVal += "M";												
					if(N) newVal += "N";																		
					if(S) newVal += "S";						
					if(T) newVal += "T";
					if(U) newVal += "U";					
					if(W) newVal += "W";
					if(X) newVal += "X";	
					newVal += "]";					
					
					this.getForm().itemChanged(this.getParentItem(), newVal, event);
				}
			});							

	//N	
		this.items.push(	
			{type:_CHECKBOX_,width:"40px",containerCssStyle:"width:40px", forceUpdate:true, ref:".", 
				labelLocation:_RIGHT_, label:"Password not required", relevantBehavior:_PARENT_,
				getDisplayValue:function (itemval) {
					return (itemval && itemval.indexOf("N")>0);
				},
				elementChanged:function(isChecked, instanceValue, event) {
					var U, D, H, I, L, M, N, S, T, W, X;
					if(instanceValue) {
						U = instanceValue.indexOf("U") > 0;
						D = instanceValue.indexOf("D") > 0;
						H = instanceValue.indexOf("H") > 0;						
						I = instanceValue.indexOf("I") > 0;						
						L = instanceValue.indexOf("L") > 0;												
						M = instanceValue.indexOf("M") > 0;																		
						S = instanceValue.indexOf("S") > 0;						
						T = instanceValue.indexOf("T") > 0;
						W = instanceValue.indexOf("W") > 0;
						X = instanceValue.indexOf("X") > 0;						
					} 
					N = isChecked;
					var newVal = "[";
					if(D) newVal += "D";
					if(H) newVal += "H";
					if(I) newVal += "I";						
					if(L) newVal += "L";						
					if(M) newVal += "M";												
					if(N) newVal += "N";																		
					if(S) newVal += "S";						
					if(T) newVal += "T";
					if(U) newVal += "U";					
					if(W) newVal += "W";
					if(X) newVal += "X";	
					newVal += "]";					
					
					this.getForm().itemChanged(this.getParentItem(), newVal, event);
				}
			});	
					
	//S	
		this.items.push(	
			{type:_CHECKBOX_,width:"40px",containerCssStyle:"width:40px", forceUpdate:true, ref:".", 
				labelLocation:_RIGHT_, label:"Server trust account", relevantBehavior:_PARENT_,
				getDisplayValue:function (itemval) {
					return (itemval && itemval.indexOf("S")>0);
				},
				elementChanged:function(isChecked, instanceValue, event) {
					var U, D, H, I, L, M, N, S, T, W, X;
					if(instanceValue) {
						U = instanceValue.indexOf("U") > 0;
						D = instanceValue.indexOf("D") > 0;
						H = instanceValue.indexOf("H") > 0;						
						I = instanceValue.indexOf("I") > 0;						
						L = instanceValue.indexOf("L") > 0;												
						M = instanceValue.indexOf("M") > 0;																		
						N = instanceValue.indexOf("N") > 0;						
						T = instanceValue.indexOf("T") > 0;
						W = instanceValue.indexOf("W") > 0;
						X = instanceValue.indexOf("X") > 0;						
					} 
					S = isChecked;
					var newVal = "[";
					if(D) newVal += "D";
					if(H) newVal += "H";
					if(I) newVal += "I";						
					if(L) newVal += "L";						
					if(M) newVal += "M";												
					if(N) newVal += "N";																		
					if(S) newVal += "S";						
					if(T) newVal += "T";
					if(U) newVal += "U";					
					if(W) newVal += "W";
					if(X) newVal += "X";	
					newVal += "]";					
					
					this.getForm().itemChanged(this.getParentItem(), newVal, event);
				}
			});	

	//T	
		this.items.push(	
			{type:_CHECKBOX_,width:"40px",containerCssStyle:"width:40px", forceUpdate:true, ref:".", 
				labelLocation:_RIGHT_, label:"Temporary duplicate account", relevantBehavior:_PARENT_,
				getDisplayValue:function (itemval) {
					return (itemval && itemval.indexOf("T")>0);
				},
				elementChanged:function(isChecked, instanceValue, event) {
					var U, D, H, I, L, M, N, S, T, W, X;
					if(instanceValue) {
						U = instanceValue.indexOf("U") > 0;
						D = instanceValue.indexOf("D") > 0;
						H = instanceValue.indexOf("H") > 0;						
						I = instanceValue.indexOf("I") > 0;						
						L = instanceValue.indexOf("L") > 0;												
						M = instanceValue.indexOf("M") > 0;																		
						N = instanceValue.indexOf("N") > 0;						
						S = instanceValue.indexOf("S") > 0;
						W = instanceValue.indexOf("W") > 0;
						X = instanceValue.indexOf("X") > 0;						
					} 
					T = isChecked;
					var newVal = "[";
					if(D) newVal += "D";
					if(H) newVal += "H";
					if(I) newVal += "I";						
					if(L) newVal += "L";						
					if(M) newVal += "M";												
					if(N) newVal += "N";																		
					if(S) newVal += "S";						
					if(T) newVal += "T";
					if(U) newVal += "U";					
					if(W) newVal += "W";
					if(X) newVal += "X";	
					newVal += "]";					
					
					this.getForm().itemChanged(this.getParentItem(), newVal, event);
				}
			});


	//W	
		this.items.push(	
			{type:_CHECKBOX_,width:"40px",containerCssStyle:"width:40px", forceUpdate:true, ref:".", 
				labelLocation:_RIGHT_, label:"Workstation trust account", relevantBehavior:_PARENT_,
				getDisplayValue:function (itemval) {
					return (itemval && itemval.indexOf("W")>0);
				},
				elementChanged:function(isChecked, instanceValue, event) {
					var U, D, H, I, L, M, N, S, T, W, X;
					if(instanceValue) {
						U = instanceValue.indexOf("U") > 0;
						D = instanceValue.indexOf("D") > 0;
						H = instanceValue.indexOf("H") > 0;						
						I = instanceValue.indexOf("I") > 0;						
						L = instanceValue.indexOf("L") > 0;												
						M = instanceValue.indexOf("M") > 0;																		
						N = instanceValue.indexOf("N") > 0;						
						S = instanceValue.indexOf("S") > 0;
						T = instanceValue.indexOf("T") > 0;
						X = instanceValue.indexOf("X") > 0;						
					} 
					W = isChecked;
					var newVal = "[";
					if(D) newVal += "D";
					if(H) newVal += "H";
					if(I) newVal += "I";						
					if(L) newVal += "L";						
					if(M) newVal += "M";												
					if(N) newVal += "N";																		
					if(S) newVal += "S";						
					if(T) newVal += "T";
					if(U) newVal += "U";					
					if(W) newVal += "W";
					if(X) newVal += "X";	
					newVal += "]";					
					
					this.getForm().itemChanged(this.getParentItem(), newVal, event);
				}
			});
	Composite_XFormItem.prototype.initializeItems.call(this);
};

ZaSambaAcFlagsXFormItem.prototype.items = [];

