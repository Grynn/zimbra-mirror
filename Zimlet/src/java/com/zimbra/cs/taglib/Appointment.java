/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Zimlets
 * Copyright (C) 2006, 2007 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Yahoo! Public License
 * Version 1.0 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * 
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.cs.taglib;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.account.Account;
import com.zimbra.cs.mailbox.Mailbox;
import com.zimbra.cs.mailbox.MailboxManager;
import com.zimbra.cs.mailbox.Mailbox.OperationContext;
import com.zimbra.cs.mailbox.calendar.Invite;

public class Appointment extends ZimbraTag {
    private static final long serialVersionUID = -4994874857866850527L;

    private String mApptId;
    private String mField;

    private static Map<String,Integer> sFields = new HashMap<String,Integer>();
    private static final int C_START_TIME = 1;
    private static final int C_END_TIME = 2;
    private static final int C_IS_RECURRING = 3;
    private static final int C_IS_ALLDAY = 4;
    private static final int C_NAME = 5;
    private static final int C_COMMENT = 6;
    private static final int C_STATUS = 7;
    private static final int C_FREEBUSY = 8;
    
    static {
    	sFields.put("starttime", C_START_TIME);
    	sFields.put("endtime", C_END_TIME);
    	sFields.put("recurring", C_IS_RECURRING);
    	sFields.put("allday", C_IS_ALLDAY);
    	sFields.put("name", C_NAME);
    	sFields.put("comment", C_COMMENT);
    	sFields.put("status", C_STATUS);
    	sFields.put("freebusy", C_FREEBUSY);
    }
    
    public void setId(String val) {
        mApptId = val;
    }

    public String getId() {
        return mApptId;
    }

    public void setField(String val) {
        mField = val.toLowerCase();
    }

    public String getField() {
        return mField;
    }

    public String getContentStart(Account acct, OperationContext octxt) throws ZimbraTagException, ServiceException {
        if (mApptId == null) {
            throw ZimbraTagException.MISSING_ATTR("id");
        }
        if (mField == null) {
            throw ZimbraTagException.MISSING_ATTR("field");
        }
        if (!sFields.containsKey(mField)) {
        	return "";
        }
        int cid = Integer.parseInt(mApptId);
        String id = acct.getId();
        Mailbox mbox = MailboxManager.getInstance().getMailboxByAccountId(id);
        Invite invite = mbox.getAppointmentById(octxt, cid).getDefaultInviteOrNull();
        if (invite == null)
            return "";
        int fid = sFields.get(mField);
        String val = null;
        switch (fid) {
        case C_START_TIME:
        	val = invite.getStartTime().getDate().toString();
        	break;
        case C_END_TIME:
        	val = invite.getEndTime().getDate().toString();
        	break;
        case C_IS_RECURRING:
        	val = Boolean.toString(invite.isRecurrence());
        	break;
        case C_IS_ALLDAY:
        	val = Boolean.toString(invite.isAllDayEvent());
        	break;
        case C_NAME:
        	val = invite.getName();
        	break;
        case C_COMMENT:
            List<String> comments = invite.getComments();
            if (comments != null && !comments.isEmpty())
                val = comments.get(0);
            else
                val = "";
        	break;
        case C_STATUS:
        	val = invite.getStatus();
        	break;
        case C_FREEBUSY:
        	val = invite.getFreeBusy();
        	break;
        default:
        	val = "";
        	break;
        }
        if (val == null) {
        	return "";
        }
        return val;
    }
}
