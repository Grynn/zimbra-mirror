/*
 * ***** BEGIN LICENSE BLOCK *****
 * Version: ZPL 1.1
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.1 ("License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.zimbra.com/license
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
 * the License for the specific language governing rights and limitations
 * under the License.
 * 
 * The Original Code is: Zimbra Collaboration Suite Web Client
 * 
 * The Initial Developer of the Original Code is Zimbra, Inc.
 * Portions created by Zimbra are Copyright (C) 2005 Zimbra, Inc.
 * All Rights Reserved.
 * 
 * Contributor(s):
 * 
 * ***** END LICENSE BLOCK *****
 */

/**
* @class ZaMtaActionDialog
* @contructor ZaMtaActionDialog
* @author Greg Solovyev
* @param parent
* param w (width)
* param h (height)
**/
function ZaMtaActionDialog(parent, app, title, w, h) {
	if (arguments.length == 0) return;
	this._standardButtons = [DwtDialog.CANCEL_BUTTON,DwtDialog.OK_BUTTON];
	ZaXDialog.call(this, parent, app, null, title, w,h);
	this.initForm(ZaMtaActionDialog.myXModel,this.getMyXForm());
	this._helpURL = ZaMtaActionDialog.helpURL;		
}

ZaMtaActionDialog.prototype = new ZaXDialog;
ZaMtaActionDialog.prototype.constructor = ZaMtaActionDialog;
ZaMtaActionDialog.helpURL = "/zimbraAdmin/adminhelp/html/WebHelp/monitoring/monitoring_zimbra_mta_mail_queues.htm";		
ZaMtaActionDialog.QUESTION = "question";
ZaMtaActionDialog.ANSWER = "answer";
ZaMtaActionDialog.MSG_IDS = "messageids";
ZaMtaActionDialog.FLTR_ITEMS = "filteritems";
ZaMtaActionDialog.SELECTED_MSGS = "selectedmsgs";
ZaMtaActionDialog.FLTRED_SET = "filteredset";
ZaMtaActionDialog.ANSWER_CHOICES = [{value:ZaMtaActionDialog.SELECTED_MSGS, label:ZaMsg.PQ_SELECTED_MSGS}, {value:ZaMtaActionDialog.FLTRED_SET, label:ZaMsg.PQ_FILTERED_SET}];

ZaMtaActionDialog.myXModel = {
	items: [
		{type:_STRING_, ref:ZaMtaActionDialog.MESSAGE, id:ZaMtaActionDialog.MESSAGE},
		{type:_ENUM_, ref:ZaMtaActionDialog.ANSWER, id:ZaMtaActionDialog.ANSWER, choices:ZaMtaActionDialog.ANSWER_CHOICES}, 
		{type:_LIST_, id:ZaMtaActionDialog.MSG_IDS,ref:ZaMtaActionDialog.MSG_IDS,
			listItem: {type:_OBJECT_, 
				items:[
					{id:ZaMTAQMsgItem.A_id, type:_STRING_},
					{id:ZaMTAQSummaryItem.A_text, type:_STRING_}
				]
			}
		},
		{type:_LIST_, id:ZaMtaActionDialog.FLTR_ITEMS,ref:ZaMtaActionDialog.FLTR_ITEMS,
			listItem: {type:_OBJECT_, 
				items:[
					{id:ZaMTAQSummaryItem.A_text, type:_STRING_},					
					{id:ZaMTAQSummaryItem.A_count, type:_NUMBER_}										
				]
			}
		}
		
	]
}

function Q_MSGS_QUESTION_RADIO_XFormItem() {}
XFormItemFactory.createItemType("_Q_MSGS_QUESTION_RADIO_", "q_msgs_question_radio", Q_MSGS_QUESTION_RADIO_XFormItem, Composite_XFormItem);
Q_MSGS_QUESTION_RADIO_XFormItem.prototype.numCols = 2;
Q_MSGS_QUESTION_RADIO_XFormItem.prototype.nowrap = true;

Q_MSGS_QUESTION_RADIO_XFormItem.prototype.items = [
	{type:_RADIO_,width:"40px",containerCssStyle:"width:40px", forceUpdate:true, ref:ZaMtaActionDialog.ANSWER, labelLocation:_NONE_, label:null, relevantBehavior:_PARENT_,
		getDisplayValue:function (itemVal) {
			return (itemVal == ZaMtaActionDialog.SELECTED_MSGS);
		}
	},
	{type:_OUTPUT_, ref:ZaMtaActionDialog.MSG_IDS,
		getDisplayValue:function (itemVal) {
			var retVal = ZaMsg.PQ_SELECTED_MSGS + " (";
			var cnt = 0;
			if(itemVal)
				cnt = itemVal.length;
			if(!cnt) {
				retVal+=ZaMsg.PQ_NONE;
			} else {
				for(var i=0; i < cnt; i++) {
					if(i > 30) {
						retVal += "...";
						break;
					}
					retVal += itemVal[i][ZaMTAQMsgItem.A_id];
					if(i< (cnt-1))
						retVal +=", ";

				}
			}
			retVal+=")";
			return retVal;
		}
	}
];

function Q_FLTRD_QUESTION_RADIO_XFormItem() {}
XFormItemFactory.createItemType("_Q_FLTRD_QUESTION_RADIO_", "q_fltrd_question_radio", Q_FLTRD_QUESTION_RADIO_XFormItem, Composite_XFormItem);
Q_FLTRD_QUESTION_RADIO_XFormItem.prototype.numCols = 2;
Q_FLTRD_QUESTION_RADIO_XFormItem.prototype.nowrap = true;

Q_FLTRD_QUESTION_RADIO_XFormItem.prototype.items = [
	{type:_RADIO_,width:"40px",containerCssStyle:"width:40px", forceUpdate:true, ref:ZaMtaActionDialog.ANSWER, labelLocation:_NONE_, label:null, relevantBehavior:_PARENT_,
		getDisplayValue:function (itemVal) {
			return (itemVal == ZaMtaActionDialog.FLTRED_SET);
		}
	},
	{type:_OUTPUT_, ref:ZaMtaActionDialog.FLTR_ITEMS,
		getDisplayValue:function (itemVal) {
			var retVal = ZaMsg.PQ_FILTERED_SET + " (";
			var cnt = 0;
			if(itemVal)
				cnt = itemVal.length;
			if(!cnt) {
				retVal+=ZaMsg.PQ_ALL;
			} else {
				for(var i=0; i < cnt; i++) {
					if(i > 30) {
						retVal += "...";
						break;
					}
					retVal += itemVal[i][ZaMTAQMsgItem.A_id];
					if(i< (cnt-1))
						retVal +=", ";

				}
			}
			retVal+=")";
			return retVal;
		}
	}
];
ZaMtaActionDialog.prototype.getMyXForm = 
function() {	
	var xFormObject = {
		numCols:2, height:"300px",align:_CENTER_,cssStyle:"text-align:center",
		items:[
			{ type: _DWT_ALERT_,
			  style: DwtAlert.INFO,
			  iconVisible: false, 
			  content: null,
			  ref:ZaMtaActionDialog.MESSAGE,
			  colSpan:"*",
			  align:_CENTER_,
			  valign:_TOP_
			},
			{ type: _DWT_ALERT_,
			  style: DwtAlert.CRITICAL,
			  iconVisible: true, 
			  content: null,
			  ref:"resultMsg",
			  relevant:"instance.status == 'error'",
			  relevantBehavior:_HIDE_,
			  align:_CENTER_,
			  colSpan:"*"
			},	
			{type:_TEXTAREA_,
				relevant:"instance.status == 'error'", 
				ref:"errorDetail", 
				label:ZaMsg.FAILED_REINDEX_DETAILS,
				relevantBehavior:_HIDE_,
				height:"100px", width:"200px",
				colSpan:"*"
			},
			{type:_DWT_ALERT_, ref:"progressMsg",content: null,
				//relevant:"instance.status == 'running' || instance.status == 'started'",
				colSpan:"*",
				relevantBehavior:_HIDE_,
 				iconVisible: true,
				align:_CENTER_,				
				style: DwtAlert.INFORMATION
			},
			{type:_DWT_PROGRESS_BAR_, label:ZaMsg.ReindexMbx_Progress,
				maxValue:null,
				maxValueRef:"numTotal", 
				ref:"numDone",
				//relevant:"instance.status == 'running' || instance.status == 'started'",
				relevantBehavior:_HIDE_,
				valign:_CENTER_,
				align:_CENTER_,	
				wholeCssClass:"progressbar",
				progressCssClass:"progressused"
			},		
			{type:_SPACER_, 
				relevant:"instance.status != 'error'", 
				height:"150px", width:"490px",colSpan:"*"
			},			
			{type:_GROUP_, colSpan:"*", numCols:5, width:"490px",cssStyle:"text-align:center", align:_CENTER_, items: [	
				{type:_SPACER_, width:"100px", colSpan:1},
				{type:_DWT_BUTTON_, 
					onActivate:"ReindexMailboxXDialog.startReindexMailbox.call(this)", label:ZaMsg.NAD_ACC_Start_Reindexing, relevantBehavior:_DISABLE_, relevant:"instance.status != 'running' && instance.status != 'started'",
					valign:_BOTTOM_,width:"100px"
				},
				{type:_SPACER_, width:"90px", colSpan:1},
				{type:_DWT_BUTTON_, 
					onActivate:"ReindexMailboxXDialog.abortReindexMailbox.call(this)", label:ZaMsg.NAD_ACC_Abort_Reindexing, relevantBehavior:_DISABLE_, relevant:"instance.status == 'running' || instance.status == 'started'",
					valign:_BOTTOM_,width:"100px"				
				},
				{type:_SPACER_, width:"100px", colSpan:1}
			]}
		]		
	}
	return xFormObject;
}
