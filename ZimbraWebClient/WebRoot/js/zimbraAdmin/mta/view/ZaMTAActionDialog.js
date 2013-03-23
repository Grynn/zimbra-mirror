/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2006, 2007, 2008, 2009, 2010, 2013 VMware, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.3 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */

/**
* @class ZaMTAActionDialog
* @contructor ZaMTAActionDialog
* @author Greg Solovyev
* @param parent
* param w (width)
* param h (height)
**/
ZaMTAActionDialog = function(parent,title, instance, w, h) {
	if (arguments.length == 0) return;
	this._standardButtons = [DwtDialog.CANCEL_BUTTON,DwtDialog.OK_BUTTON];
	ZaXDialog.call(this, parent,null, title, w,h);
	this.initForm(ZaMTAActionDialog.myXModel,this.getMyXForm(instance));
	this._helpURL = ZaMTAActionDialog.helpURL;
}

ZaMTAActionDialog.prototype = new ZaXDialog;
ZaMTAActionDialog.prototype.constructor = ZaMTAActionDialog;
ZaMTAActionDialog.helpURL = location.pathname + ZaUtil.HELP_URL + "monitoring/monitoring_zimbra_mta_mail_queues.htm?locid="+AjxEnv.DEFAULT_LOCALE;
ZaMTAActionDialog.ACTION = "action";
ZaMTAActionDialog.QNAME = "qname";
ZaMTAActionDialog.MESSAGE = "message"; //Select what you want to action on
ZaMTAActionDialog.ANSWER = "answer";
ZaMTAActionDialog.MSG_IDS = "messageids";
ZaMTAActionDialog.FLTR_ITEMS = "filteritems";
ZaMTAActionDialog.SELECTED_MSGS = "selectedmsgs";
ZaMTAActionDialog.FLTRED_SET = "filteredset";
ZaMTAActionDialog.QUESTION = "question"; //confirmation dialog question
ZaMTAActionDialog.ANSWER_CHOICES = [{value:ZaMTAActionDialog.SELECTED_MSGS, label:ZaMsg.PQ_SELECTED_MSGS}, {value:ZaMTAActionDialog.FLTRED_SET, label:ZaMsg.PQ_FILTERED_SET}];

ZaMTAActionDialog.myXModel = {
	items: [
		{type:_STRING_, ref:ZaMTAActionDialog.QNAME, id:ZaMTAActionDialog.QNAME},
		{type:_STRING_, ref:ZaMTAActionDialog.ACTION, id:ZaMTAActionDialog.ACTION},
		{type:_STRING_, ref:ZaMTAActionDialog.MESSAGE, id:ZaMTAActionDialog.MESSAGE},		
		{type:_STRING_, ref:ZaMTAActionDialog.QUESTION, id:ZaMTAActionDialog.QUESTION},
		{type:_ENUM_, ref:ZaMTAActionDialog.ANSWER, id:ZaMTAActionDialog.ANSWER, choices:ZaMTAActionDialog.ANSWER_CHOICES}, 
		{type:_LIST_, id:ZaMTAActionDialog.MSG_IDS,ref:ZaMTAActionDialog.MSG_IDS,
			listItem: {type:_OBJECT_, 
				items:[
					{id:ZaMTAQMsgItem.A_id, type:_STRING_},
					{id:ZaMTAQSummaryItem.A_text, type:_STRING_}
				]
			}
		},
		{type:_LIST_, id:ZaMTAActionDialog.FLTR_ITEMS,ref:ZaMTAActionDialog.FLTR_ITEMS,
			listItem: {type:_OBJECT_, 
				items:[
					{id:ZaMTAQSummaryItem.A_text, type:_STRING_},					
					{id:ZaMTAQSummaryItem.A_count, type:_NUMBER_}										
				]
			}
		}
		
	]
}

Q_MSGS_QUESTION_RADIO_XFormItem = function() {}
XFormItemFactory.createItemType("_Q_MSGS_QUESTION_RADIO_", "q_msgs_question_radio", Q_MSGS_QUESTION_RADIO_XFormItem, Composite_XFormItem);
Q_MSGS_QUESTION_RADIO_XFormItem.prototype.numCols = 2;
Q_MSGS_QUESTION_RADIO_XFormItem.prototype.nowrap = true;

Q_MSGS_QUESTION_RADIO_XFormItem.prototype.items = [
	{type:_RADIO_,width:"40px",containerCssStyle:"width:40px", forceUpdate:true, 
		ref:ZaMTAActionDialog.ANSWER, labelLocation:_NONE_, label:null, 
		trueValue:ZaMTAActionDialog.SELECTED_MSGS,falseValue:ZaMTAActionDialog.FLTRED_SET,
		updateElement:function (newValue) {
			this.getElement().checked = (newValue == ZaMTAActionDialog.SELECTED_MSGS);
		},
		elementChanged: function(elementValue,instanceValue, event) {
			this.getForm().itemChanged(this, ZaMTAActionDialog.SELECTED_MSGS, event);
		},
		bmolsnr:true
	},
	{type:_OUTPUT_, ref:ZaMTAActionDialog.MSG_IDS,
		getDisplayValue:function (itemVal) {
			var retVal = "", cnt = "0";
			if(itemVal) {
				cnt = itemVal.length;
			}
			//retVal = String(ZaMsg.PQ_SelectedMessages).replace("{0}",cnt);
			retVal = AjxMessageFormat.format (ZaMsg.PQ_SelectedMessages, [cnt])  ;
            return retVal;
		}
	}
];

Q_MSGS_QUESTION_RADIO_XFormItem.prototype.setElementEnabled = function (enable) {
    this.getElement().disabled = (enable != true);
}

Q_MSGS_QUESTION_RADIO_XFormItem.prototype.updateEnabledDisabled = function () {
    var isEnabled = true;

    if (isEnabled) {
        var myEnabledDisabledChecks = this.getInheritedProperty("enableDisableChecks");

        if (myEnabledDisabledChecks && myEnabledDisabledChecks instanceof Array) {
            var cnt = myEnabledDisabledChecks.length;

            for (var i = 0; i < cnt; i++) {
                if (myEnabledDisabledChecks[i] != null) {
                    if (typeof myEnabledDisabledChecks[i] === "function") {
                        isEnabled = myEnabledDisabledChecks[i].call(this);
                        if (!isEnabled) {
                            break;
                        }
                    } else if (myEnabledDisabledChecks[i] instanceof Array) {
                        var func = myEnabledDisabledChecks[i].shift();
                        if (!func || !func.apply) {
                            continue;
                        }
                        isEnabled = func.apply(this, myEnabledDisabledChecks[i]);
                        myEnabledDisabledChecks[i].unshift(func);
                        if (!isEnabled) {
                            break;
                        }
                    }
                }
            }
        } else if (myEnabledDisabledChecks == false) {
            isEnabled = false;
        }
    }

    if (isEnabled) {
        this.enableElement();
    } else {
        this.disableElement();
    }
}

Q_MSGS_QUESTION_RADIO_XFormItem.prototype.updateVisibility = function () {
    var isVisible = true;

    if (isVisible) {
        var myVisibilityChecks = this.getInheritedProperty("visibilityChecks");

        if (myVisibilityChecks && myVisibilityChecks instanceof Array) {
            var cnt = myVisibilityChecks.length;
            for (var i = 0; i < cnt; i++) {
                if (myVisibilityChecks[i] != null) {
                    if (typeof myVisibilityChecks[i] === "function") {
                        isVisible = myVisibilityChecks[i].call(this);
                        if (!isVisible) {
                            break;
                        }
                    } else if (myVisibilityChecks[i] instanceof Array) {
                        var func = myVisibilityChecks[i].shift();
                        isVisible = func.apply(this, myVisibilityChecks[i]);
                        myVisibilityChecks[i].unshift(func);
                        if (!isVisible) {
                            break;
                        }
                    } else if (typeof myVisibilityChecks === "string") {
                        var instance = this.getInstance();
                        isVisible = eval(myVisibilityChecks[i]);
                        if (!isVisible) {
                            break;
                        }
                    }
                }
            }
        }
    }

    if (isVisible) {
        this.show();
    } else {
        this.hide();
    }
}

Q_FLTRD_QUESTION_RADIO_XFormItem = function() {}
XFormItemFactory.createItemType("_Q_FLTRD_QUESTION_RADIO_", "q_fltrd_question_radio", Q_FLTRD_QUESTION_RADIO_XFormItem, Composite_XFormItem);
Q_FLTRD_QUESTION_RADIO_XFormItem.prototype.numCols = 2;
Q_FLTRD_QUESTION_RADIO_XFormItem.prototype.nowrap = true;

Q_FLTRD_QUESTION_RADIO_XFormItem.prototype.items = [
	{type:_RADIO_,width:"40px",containerCssStyle:"width:40px", forceUpdate:true, 
		ref:ZaMTAActionDialog.ANSWER, labelLocation:_NONE_, label:null, 
		trueValue:ZaMTAActionDialog.FLTRED_SET,falseValue:ZaMTAActionDialog.SELECTED_MSGS,
		updateElement:function (newValue) {
			this.getElement().checked = (newValue == ZaMTAActionDialog.FLTRED_SET);
		},
		elementChanged: function(elementValue,instanceValue, event) {
			this.getForm().itemChanged(this, ZaMTAActionDialog.FLTRED_SET, event);
		},
		bmolsnr:true
	},
	{type:_OUTPUT_, ref:ZaMTAActionDialog.FLTR_ITEMS,
		getDisplayValue:function (itemVal) {
			var retVal = "";
			var _temp = [];
			if(itemVal) {
				for(var key in itemVal) {

					if(itemVal[key]) {
						var _temp2 = [];
						var cnt = itemVal[key].length;
						for(var i =0; i < cnt; i++) {
							_temp2.push(itemVal[key][i][ZaMTAQSummaryItem.A_text]);
						}
						_temp.push((key + ": " + _temp2.join(", ")));
					}
				}
			} 
			if(_temp.length) {
				retVal = String(ZaMsg.PQ_FilteredSet).replace("{0}",_temp.join("; "));
			} else {
				retVal = String(ZaMsg.PQ_FilteredSet).replace("{0}",ZaMsg.PQ_AllMessages);
			}
			return retVal;			
		}
	}
];

Q_FLTRD_QUESTION_RADIO_XFormItem.prototype.setElementEnabled = function (enable) {
    this.getElement().disabled = (enable != true);
}

Q_FLTRD_QUESTION_RADIO_XFormItem.prototype.updateEnabledDisabled = function () {
    var isEnabled = true;

    if (isEnabled) {
        var myEnabledDisabledChecks = this.getInheritedProperty("enableDisableChecks");

        if (myEnabledDisabledChecks && myEnabledDisabledChecks instanceof Array) {
            var cnt = myEnabledDisabledChecks.length;

            for (var i = 0; i < cnt; i++) {
                if (myEnabledDisabledChecks[i] != null) {
                    if (typeof myEnabledDisabledChecks[i] === "function") {
                        isEnabled = myEnabledDisabledChecks[i].call(this);
                        if (!isEnabled) {
                            break;
                        }
                    } else if (myEnabledDisabledChecks[i] instanceof Array) {
                        var func = myEnabledDisabledChecks[i].shift();
                        if (!func || !func.apply) {
                            continue;
                        }
                        isEnabled = func.apply(this, myEnabledDisabledChecks[i]);
                        myEnabledDisabledChecks[i].unshift(func);
                        if (!isEnabled) {
                            break;
                        }
                    }
                }
            }
        } else if (myEnabledDisabledChecks == false) {
            isEnabled = false;
        }
    }

    if (isEnabled) {
        this.enableElement();
    } else {
        this.disableElement();
    }
}

Q_FLTRD_QUESTION_RADIO_XFormItem.prototype.updateVisibility = function () {
    var isVisible = true;

    if (isVisible) {
        var myVisibilityChecks = this.getInheritedProperty("visibilityChecks");

        if (myVisibilityChecks && myVisibilityChecks instanceof Array) {
            var cnt = myVisibilityChecks.length;
            for (var i = 0; i < cnt; i++) {
                if (myVisibilityChecks[i] != null) {
                    if (typeof myVisibilityChecks[i] === "function") {
                        isVisible = myVisibilityChecks[i].call(this);
                        if (!isVisible) {
                            break;
                        }
                    } else if (myVisibilityChecks[i] instanceof Array) {
                        var func = myVisibilityChecks[i].shift();
                        isVisible = func.apply(this, myVisibilityChecks[i]);
                        myVisibilityChecks[i].unshift(func);
                        if (!isVisible) {
                            break;
                        }
                    } else if (typeof myVisibilityChecks === "string") {
                        var instance = this.getInstance();
                        isVisible = eval(myVisibilityChecks[i]);
                        if (!isVisible) {
                            break;
                        }
                    }
                }
            }
        }
    }

    if (isVisible) {
        this.show();
    } else {
        this.hide();
    }
}

ZaMTAActionDialog.prototype.getMyXForm = 
function(instance) {
	var xFormObject = {
		numCols:1, align:_CENTER_,cssStyle:"text-align:center",
		items:[
			{ type: _OUTPUT_,
			  ref:ZaMTAActionDialog.MESSAGE,
			  label:null,
			  colSpan:"*",
			  align:_LEFT_,
			  valign:_TOP_
			},
			{ type: _Q_MSGS_QUESTION_RADIO_,
                visibilityChecks:[[ZaItem.hasAnyRight,[ZaMTA.MANAGE_MAIL_QUEUE_RIGHT],instance]],
                enableDisableChecks:[[ZaItem.hasAnyRight,[ZaMTA.MANAGE_MAIL_QUEUE_RIGHT],instance]],
			  align:_LEFT_
			},	
			{ type: _Q_FLTRD_QUESTION_RADIO_,
                visibilityChecks:[[ZaItem.hasAnyRight,[ZaMTA.MANAGE_MAIL_QUEUE_RIGHT],instance]],
                enableDisableChecks:[[ZaItem.hasAnyRight,[ZaMTA.MANAGE_MAIL_QUEUE_RIGHT],instance]],
			  align:_LEFT_
			}
		]		
	}
	return xFormObject;
}
