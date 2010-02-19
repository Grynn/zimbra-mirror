/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Zimlets
 * Copyright (C) 2005, 2006, 2007, 2008, 2009, 2010 Zimbra, Inc.
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
 * Defines the Zimlet handler class.
 *   
 */
function com_zimbra_example_paneldragsourcedetails_HandlerObject() {
}

/**
 * Makes the Zimlet class a subclass of ZmZimletBase.
 *
 */
com_zimbra_example_paneldragsourcedetails_HandlerObject.prototype = new ZmZimletBase();
com_zimbra_example_paneldragsourcedetails_HandlerObject.prototype.constructor = com_zimbra_example_paneldragsourcedetails_HandlerObject;

/**
 * This method gets called by the Zimlet framework when the zimlet loads.
 *  
 */
com_zimbra_example_paneldragsourcedetails_HandlerObject.prototype.init =
function() {
	// do something
};


/**
 * This method gets called by the Zimlet framework when an item or items are dropped on the panel.
 * 
 * @param	obj		the dropped object
 */
com_zimbra_example_paneldragsourcedetails_HandlerObject.prototype.doDrop =
function(obj) {

	var type = obj.TYPE;
	if (type == null) {
		type = obj.type;
		if (type && type == "BRIEFCASE_ITEM") {
			// do something with ZmBriefcaseItem

			var contentType = obj.getContentType(); // {String}
			var folderId = obj.getFolderId(); // {String}
			var briefcaseFolder = obj.getBriefcaseFolder(); // {ZmBriefcase}
			var restUrl = obj.getRestUrl(); // {String}
			var readonly = obj.isReadOnly(); // {Boolean}

			var name = obj.name; // {String}
			var creator = obj.creator; // {String}
			var id = obj.id; // {String}
		}
		else {
			var arrayObj = obj[0];
			if (arrayObj && arrayObj.type == "TASK") {
				// do something with ZmTask
				var task = arrayObj; // {ZmTask}
				
				var taskName = task.getName(); // name (i.e. "subject") {String}
	
				var taskLocation = task.getLocation(); // {String}
				var organizer = task.getOrganizer(); // {String}
				
				var percentComplete = task.pComplete; // {int}
				
				var priority = task.priority; // {int}
				var	priorityLabel = ZmCalItem.getLabelForPriority(priority); // {String}
				
				var status = task.status; // {int}
				var statusLabel = ZmCalItem.getLabelForStatus(status); // {String}
	
				var taskStartDate = task.startDate; // {Date}
				var taskEndDate = task.endDate; // {Date}
	
				var isRecurring = task.isRecurring(); // {Boolean}
				
				if (isRecurring) {
					var recObj = task._recurrence; // {ZmRecurrence}
					var blurb = task.getRecurBlurb(); // {String}
					var type = task.getRecurType(); // {String}
				}
			}	
		}
	}
	
	switch(type) {
		case "ZmAppt": {
			// do something with ZmAppt
			var apt = obj.srcObj; // {ZmAppt}
			
			var aptName = apt.getName(); // get name (i.e. "subject") {String}
			var aptNotes = apt.getNotesPart(); // {String}

			var location = apt.getLocation(); // {String}

			var startTime = apt.getStartTime(); // {int} milliseconds
			var endTime = apt.getEndTime(); // {int} milliseconds
			var dateRange = apt.getDateRange(); // dateRange.startTime + dateRange.endTime {Date}

			var allDayEvent = apt.allDayEvent; // {String} "0" "1"
			var freeBusy = apt.freeBusy; // {String} "B" "T" "O" "F"
			var privacy = apt.privacy; // {String} "PRI" "PUB"

			if (isRecurring) {
				var recObj = apt._recurrence; // {ZmRecurrence}
				var blurb = apt.getRecurBlurb(); // {String}
				var type = apt.getRecurType(); // {String}
			}

			break;
		}
		case "ZmContact": {	
			// do something with ZmContact
			var contact = obj; // {ZmContact}
			
			var fn = contact.firstName; // {String}
			var ln = contact.lastName; // {String}
			var email = contact.email; // {String}
			var homePhone = contact.homePhone; // {String}
			var otherPhone = contact.otherPhone; // {String}
			var workPhone = contact.workPhone; // {String}

			var email = contact.email; // {String}
			var email2 = contact.email2; // {String}
			var email3 = contact.email3; // {String}

			break;
		}
		case "ZmConv": {
			// do something with ZmConv
			var conv = obj.srcObj; // {ZmConv}
			
			var unread = conv.isUnread; // {Boolean}
			var hasattach = conv.hasAttach; // {Boolean}
			var subject= conv.subject; // {String}
			var tags = conv.tags; // {Array}

			var convCallback = new AjxCallback(this, this._handleConvMsgs, [conv]);
					
			this.getMsgsForConv(convCallback, conv);
			
			break;
		}
		case "ZmMailMsg": {
			// do something with ZmMailMsg			
			var msg = obj.srcObj; // {ZmMailMsg}
			
			var unread = msg.isUnread; // {Boolean}
			var hasattach = msg.hasAttach; // {Boolean}
			var subject= msg.subject; // {String}
			var tags = msg.tags; // {Array}
			var messageid = msg.messageid; // {String}

			var fromAddresses = msg.getAddresses(AjxEmailAddress.FROM);	
			if (fromAddresses) {
				var fromArray = fromAddresses.getArray();

				for (var i=0; i< fromArray.length; i++) {
					var addr = fromArray[i];
					var address = addr.getAddress(); // {String}
					var name = addr.getName(); // {String}
					var typestr = addr.getTypeAsString(); // {String}
					var displayName = addr.getDispName(); // {String}
				}
			}
			
			var toAddresses = msg.getAddresses(AjxEmailAddress.TO);
			var ccAddresses = msg.getAddresses(AjxEmailAddress.CC);
			
			break;
		}
	}

};

/**
 * Handles the get messages from conversation.
 * 
 * @param	{ZmConv}	conv		the conversation
 * @param	{Object}	obj			an object
 * 
 */
com_zimbra_example_paneldragsourcedetails_HandlerObject.prototype._handleConvMsgs =
function(conv,obj) {
	
	var msgs = conv.msgs.getArray(); // {Array} of {ZmMailMsg}

};
