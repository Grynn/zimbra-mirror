/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Server
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
package com.zimbra.cs.taglib.bean;

import com.zimbra.cs.taglib.bean.ZMessageComposeBean.MessageAttachment;
import com.zimbra.cs.zclient.ZMailbox;
import com.zimbra.common.service.ServiceException;
import org.apache.commons.fileupload.DiskFileUpload;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadBase;
import org.apache.commons.fileupload.FileUploadException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.PageContext;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Arrays;
import java.util.Map.Entry;

public class ZComposeUploaderBean {

    public static final String F_attendees = "attendees";
    public static final String F_apptFolderId = "apptFolderId";
    public static final String F_location = "location";
    public static final String F_timeZone = "timeZone";
    public static final String F_freeBusyStatus = "freeBusyStatus";
    public static final String F_allDay = "allDay";
    public static final String F_startDate = "startDate";
    public static final String F_startHour = "startHour";
    public static final String F_startMinute = "startMinute";
    public static final String F_endDate = "endDate";
    public static final String F_endHour = "endHour";
    public static final String F_endMinute = "endMinute";
    public static final String F_invId = "invId";
    public static final String F_exInvId = "exInvId";
    public static final String F_compNum = "compNum";
    public static final String F_instCompNum = "instCompNum";
    public static final String F_useInstance = "useInstance";
    public static final String F_instStartTime = "instStartTime";
    public static final String F_instDuration = "instDuration";
    public static final String F_inviteReplyVerb = "inviteReplyVerb";
    public static final String F_inviteReplyInst = "inviteReplyInst";
    public static final String F_inviteReplyAllDay = "inviteReplyAllDay";
    public static final String F_classProp = "classProp";
    public static final String F_taskPriority = "taskPriority";
    public static final String F_taskStatus = "taskStatus";
    public static final String F_taskPercentComplete = "taskPercentComplete";


    public static final String F_to = "to";
    public static final String F_cc = "cc";
    public static final String F_bcc = "bcc";
    public static final String F_subject = "subject";
    public static final String F_priority = "priority";
    public static final String F_messageAttachment = "messageAttachment";
    public static final String F_originalAttachment = "originalAttachment";
    public static final String F_uploadedAttachment = "uploadedAttachment";
    public static final String F_body = "body";
    public static final String F_bodyText = "bodyText";
    public static final String F_replyto = "replyto";
    public static final String F_from = "from";
    public static final String F_inreplyto = "inreplyto";
    public static final String F_messageid = "messageid";
    public static final String F_draftid = "draftid";
    public static final String F_fileUpload = "fileUpload";
    public static final String F_contactSearchQuery  = "contactSearchQuery";
    public static final String F_contactLocation = "contactLocation";
    public static final String F_sendUID = "sendUID";

    public static final String F_addTo = "addTo";
    public static final String F_addCc = "addCc";
    public static final String F_addBcc = "addBcc";
    public static final String F_addAttendees = "addAttendees";

    public static final String F_pendingTo = "pendingTo";
    public static final String F_pendingCc = "pendingCc";
    public static final String F_pendingBcc = "pendingBcc";
    public static final String F_pendingAttendees = "pendingAttendees";

    public static final String F_actionSend = "actionSend";
    public static final String F_actionSave = "actionSave";
    public static final String F_actionCancel = "actionCancel";
    public static final String F_actionDraft = "actionDraft";
    public static final String F_actionApptCancel = "actionApptCancel";
    public static final String F_actionApptDelete = "actionApptDelete";

    public static final String F_actionAttachDone = "actionAttachDone";
    public static final String F_actionAttachCancel = "actionAttachCancel";
    public static final String F_actionAttachAdd = "actionAttachAdd";

    public static final String F_actionContactDone = "actionContactDone";
    public static final String F_actionContactCancel = "actionContactCancel";
    public static final String F_actionContactAdd = "actionContactAdd";
    public static final String F_actionContactSearch = "actionContactSearch";

    public static final String F_actionRepeatEdit = "actionRepeatEdit";
    public static final String F_actionRepeatCancel = "actionRepeatCancel";
    public static final String F_actionRepeatDone = "actionRepeatDone";

    public static final String F_doAction = "doAction";
    public static final String F_doComposeAction = "doComposeAction";

    public static final String F_repeatBasicType = "repeatBasicType";
    public static final String F_repeatType = "repeatType";
    public static final String F_repeatDailyInterval = "repeatDailyInterval";
    public static final String F_repeatWeeklyByDay = "repeatWeeklyByDay";
    public static final String F_repeatWeeklyInterval = "repeatWeeklyInterval";
    public static final String F_repeatWeeklySun = "repeatWeeklySun";
    public static final String F_repeatWeeklyMon = "repeatWeeklyMon";
    public static final String F_repeatWeeklyTue = "repeatWeeklyTue";
    public static final String F_repeatWeeklyWed = "repeatWeeklyWed";
    public static final String F_repeatWeeklyThu = "repeatWeeklyThu";
    public static final String F_repeatWeeklyFri = "repeatWeeklyFri";
    public static final String F_repeatWeeklySat = "repeatWeeklySat";
    public static final String F_repeatMonthlyInterval = "repeatMonthlyInterval";
    public static final String F_repeatMonthlyMonthDay = "repeatMonthlyMonthDay";
    public static final String F_repeatMonthlyRelativeInterval = "repeatMonthlyRelativeInterval";
    public static final String F_repeatMonthlyRelativeOrd = "repeatMonthlyRelativeOrd";
    public static final String F_repeatMonthlyRelativeDay = "repeatMonthlyRelativeDay";
    public static final String F_repeatYearlyMonthDay = "repeatYearlyMonthDay";
    public static final String F_repeatYearlyMonth = "repeatYearlyMonth";
    public static final String F_repeatYearlyRelativeOrd = "repeatYearlyRelativeOrd";
    public static final String F_repeatYearlyRelativeDay = "repeatYearlyRelativeDay";
    public static final String F_repeatYearlyRelativeMonth = "repeatYearlyRelativeMonth";
    public static final String F_repeatEndType = "repeatEndType";
    public static final String F_repeatEndCount = "repeatEndCount";
    public static final String F_repeatEndDate = "repeatEndDate";
    public static final String F_reminder1 = "reminderDuration1";
    public static final String F_reminder2 = "reminderDuration2";
    public static final String F_reminderEmail = "reminderEmail";
    public static final String F_reminderSendEmail = "reminderSendEmail";
    public static final String F_reminderSendMobile = "reminderSendMobile";
    public static final String F_reminderSendYIM = "reminderSendYIM";

    private static final long DEFAULT_MAX_SIZE = 100 * 1024 * 1024;

    private boolean mIsUpload;
    private List<FileItem> mItems;
    private ZMessageComposeBean mComposeBean;
    private String mPendingTo;
    private String mPendingCc;
    private String mPendingBcc;
    private String mPendingAttendees;
    private Map<String,List<String>> mParamValues;
    private HashMap<String, String> mOrigRepeatParams;

    public ZComposeUploaderBean(PageContext pageContext, ZMailbox mailbox) throws JspTagException, ServiceException {
        HttpServletRequest req = (HttpServletRequest) pageContext.getRequest();
        DiskFileUpload upload = getUploader();
        try {

            mIsUpload = DiskFileUpload.isMultipartContent(req);
            if (mIsUpload) {
                mParamValues = new HashMap<String, List<String>>();
                mOrigRepeatParams = new HashMap<String, String>();
                mItems = upload.parseRequest(req);
                mComposeBean = getComposeBean(pageContext, mItems, mailbox);
            }
        } catch (FileUploadBase.SizeLimitExceededException e) {
            // at least one file was over max allowed size
            throw new JspTagException(ZTagLibException.UPLOAD_SIZE_LIMIT_EXCEEDED("size limit exceeded", e));
        } catch (FileUploadBase.InvalidContentTypeException e) {
            // at least one file was of a type not allowed
            throw new JspTagException(ZTagLibException.UPLOAD_FAILED(e.getMessage(), e));
        } catch (FileUploadException e) {
            // parse of request failed for some other reason
            throw new JspTagException(ZTagLibException.UPLOAD_FAILED(e.getMessage(), e));
        }
    }

    private ZMessageComposeBean getComposeBean(PageContext pageContext, List<FileItem> items, ZMailbox mailbox) throws ServiceException {
        ZMessageComposeBean compose = new ZMessageComposeBean(pageContext);
        StringBuilder addTo = null, addCc = null, addBcc = null, addAttendees = null;

        for (FileItem item : items) {
            if (!item.isFormField()) {
                // deal with attachment uploads later
                if (item.getFieldName().equals(F_fileUpload) && item.getName() != null && item.getName().length() > 0) {
                    compose.addFileItem(item);
                }
            } else {
                String name = item.getFieldName();
                String value;
                try { value = item.getString("utf-8"); } catch (UnsupportedEncodingException e) { value = item.getString();}
                if (name.equals(F_messageAttachment)) {
                    int i = value.indexOf(':');
                    String id = i == -1 ? value : value.substring(0, i);
                    String subject = i == -1 ? null : value.substring(i+1);
                    compose.getMessageAttachments().add(new MessageAttachment(id, subject));
                } else if (name.equals(F_originalAttachment)) {
                    compose.setCheckedAttachmentName(value);
                } else if (name.equals(F_uploadedAttachment)) {
                    compose.setUploadedAttachment(value);
                } else if (name.equals(F_addTo)) {
                    if (addTo == null) addTo = new StringBuilder();
                    if (addTo.length() > 0) addTo.append(", ");
                    addTo.append(value);
                } else if (name.equals(F_addCc)) {
                    if (addCc == null) addCc = new StringBuilder();
                    if (addCc.length() > 0) addCc.append(", ");
                    addCc.append(value);
                } else if (name.equals(F_addBcc)) {
                    if (addBcc == null) addBcc = new StringBuilder();
                    if (addBcc.length() > 0) addBcc.append(", ");
                    addBcc.append(value);
                } else if (name.equals(F_addAttendees)) {
                    if (addAttendees == null) addAttendees = new StringBuilder();
                    if (addAttendees.length() > 0) addAttendees.append(", ");
                    addAttendees.append(value);
                } else if (name.equals(F_pendingTo)) {
                    mPendingTo = value;
                } else if (name.equals(F_pendingCc)) {
                    mPendingCc = value;
                } else if (name.equals(F_pendingBcc)) {
                    mPendingBcc = value;
                } else if (name.equals(F_pendingAttendees)) {
                    mPendingAttendees = value;
                } else if (name.startsWith("orig_repeat")) {
                    mOrigRepeatParams.put(name, value);
                } else {
                    // normalize action params from image submits
                    if (name.startsWith("action") && name.endsWith(".x")) {
                        name = name.substring(0, name.length()-2);
                    }
                    List<String> values = mParamValues.get(name);
                    if (values == null) {
                        values = new ArrayList<String>();
                        mParamValues.put(name, values);
                    }
                    values.add(value);
                }
            }

        }

        if (getIsRepeatCancel()) {
            // override repeat* attrs with any orig_repeat* attrs
            for (Entry<String,String> entry : mOrigRepeatParams.entrySet()) {
                mParamValues.put(entry.getKey().substring(5), Arrays.asList(entry.getValue()));
            }
        }

        compose.setTo(getParam(F_to));
        compose.setCc(getParam(F_cc));
        compose.setBcc(getParam(F_bcc));
        compose.setSubject(getParam(F_subject));
        compose.setPriority(getParam(F_priority));
        
        if(getParam(F_bodyText)==null || "".equals(getParam(F_bodyText))){
        compose.setContent(getParam(F_body));    
        }else{
        compose.setHtmlContent("<html><body>"+getParam(F_body)+"</body></html>");
        compose.setContent(getParam(F_bodyText));
        }
        compose.setFrom(getParam(F_from));
        compose.setReplyTo(getParam(F_replyto));
        compose.setInReplyTo(getParam(F_inreplyto));
        compose.setMessageId(getParam(F_messageid));
        compose.setDraftId(getParam(F_draftid));
        compose.setSendUID(getParam(F_sendUID));

        compose.setApptFolderId(getParam(F_apptFolderId));
        compose.setInviteReplyVerb(getParam(F_inviteReplyVerb));
        compose.setInviteReplyInst(getParamLong(F_inviteReplyInst,0));
        compose.setInviteReplyAllDay("1".equals(getParam(F_inviteReplyAllDay)));
        compose.setClassProp(getParam(F_classProp));

        compose.setTaskPriority(getParam(F_taskPriority));
        compose.setTaskStatus(getParam(F_taskStatus));
        compose.setTaskPercentComplete(getParam(F_taskPercentComplete));

        compose.setAttendees(getParam(F_attendees));
        compose.setInviteId(getParam(F_invId));
        compose.setCompNum(getParam(F_compNum));
        compose.setExceptionInviteId(getParam(F_exInvId));
        compose.setUseInstance("1".equals(getParam(F_useInstance)));
        compose.setLocation(getParam(F_location));
        compose.setTimeZone(getParam(F_timeZone));
        compose.setFreeBusyStatus(getParam(F_freeBusyStatus));
        compose.setAllDay("1".equals(getParam(F_allDay)));
        compose.setStartDate(getParam(F_startDate));
        compose.setStartHour(getParamLong(F_startHour, 0));
        compose.setStartMinute(getParamLong(F_startMinute, 0));
        compose.setEndDate(getParam(F_endDate));
        compose.setEndHour(getParamLong(F_endHour, 0));
        compose.setEndMinute(getParamLong(F_endMinute, 0));
        compose.setInstanceDuration(getParamLong(F_instDuration, 0));
        compose.setInstanceStartTime(getParamLong(F_instStartTime, 0));
        compose.setInstanceCompNum(getParam(F_instCompNum));

        compose.setRepeatBasicType(getParam(F_repeatBasicType));
        compose.setRepeatType(getParam(F_repeatType));
        compose.setRepeatDailyInterval(getParamInt(F_repeatDailyInterval, 0));
        compose.setRepeatWeeklyByDay(getParamInt(F_repeatWeeklyByDay, 0));
        compose.setRepeatWeeklySun("1".equals(getParam(F_repeatWeeklySun)));
        compose.setRepeatWeeklyMon("1".equals(getParam(F_repeatWeeklyMon)));
        compose.setRepeatWeeklyTue("1".equals(getParam(F_repeatWeeklyTue)));
        compose.setRepeatWeeklyWed("1".equals(getParam(F_repeatWeeklyWed)));
        compose.setRepeatWeeklyThu("1".equals(getParam(F_repeatWeeklyThu)));
        compose.setRepeatWeeklyFri("1".equals(getParam(F_repeatWeeklyFri)));
        compose.setRepeatWeeklySat("1".equals(getParam(F_repeatWeeklySat)));
        compose.setRepeatWeeklyInterval(getParamInt(F_repeatWeeklyInterval, 0));
        compose.setRepeatMonthlyInterval(getParamInt(F_repeatMonthlyInterval, 0));
        compose.setRepeatMonthlyMonthDay(getParamInt(F_repeatMonthlyMonthDay, 0));
        compose.setRepeatMonthlyRelativeInterval(getParamInt(F_repeatMonthlyRelativeInterval, 0));
        compose.setRepeatMonthlyRelativeOrd(getParamInt(F_repeatMonthlyRelativeOrd, 0));
        compose.setRepeatMonthlyRelativeDay(getParamInt(F_repeatMonthlyRelativeDay, 0));
        compose.setRepeatYearlyMonthDay(getParamInt(F_repeatYearlyMonthDay, 0));
        compose.setRepeatYearlyMonth(getParamInt(F_repeatYearlyMonth, 0));
        compose.setRepeatYearlyRelativeOrd(getParamInt(F_repeatYearlyRelativeOrd, 0));
        compose.setRepeatYearlyRelativeDay(getParamInt(F_repeatYearlyRelativeDay, 0));
        compose.setRepeatYearlyRelativeMonth(getParamInt(F_repeatYearlyRelativeMonth, 0));
        compose.setRepeatEndType(getParam(F_repeatEndType));
        compose.setRepeatEndCount(getParamInt(F_repeatEndCount, 0));
        compose.setRepeatEndDate(getParam(F_repeatEndDate));

        compose.setReminder1(getParam(F_reminder1));
        compose.setReminder2(getParam(F_reminder2));
        if ("true".equals(getParam(F_reminderSendEmail))){
            compose.setReminderEmail(getParam(F_reminderEmail));
            compose.setSendReminderEmail(true);
        } else {
            compose.setSendReminderEmail(false);
        }
        compose.setSendReminderMobile("true".equals(getParam(F_reminderSendMobile)));
        compose.setSendReminderYIM("true".equals(getParam(F_reminderSendYIM)));
        

        if (getIsContactDone()) {
            if (mPendingTo != null) compose.setTo(addToList(compose.getTo(), mPendingTo));
            if (mPendingCc != null) compose.setCc(addToList(compose.getCc(), mPendingCc));
            if (mPendingBcc != null) compose.setBcc(addToList(compose.getBcc(), mPendingBcc));
            if (mPendingAttendees != null) compose.setAttendees(addToList(compose.getAttendees(), mPendingAttendees));
            if (addTo != null) compose.setTo(addToList(compose.getTo(), addTo.toString()));
            if (addCc != null) compose.setCc(addToList(compose.getCc(), addCc.toString()));
            if (addBcc != null) compose.setBcc(addToList(compose.getBcc(), addBcc.toString()));
            if (addAttendees != null) compose.setAttendees(addToList(compose.getAttendees(), addAttendees.toString()));
        } else {
            if (addTo != null) mPendingTo = addToList(mPendingTo, addTo.toString());
            if (addCc != null) mPendingCc = addToList(mPendingCc, addCc.toString());
            if (addBcc != null) mPendingBcc = addToList(mPendingBcc, addBcc.toString());
            if (addAttendees != null) mPendingAttendees = addToList(mPendingAttendees, addAttendees.toString());
        }

        if (getIsRepeatEdit()) {
            // first stash away all repeat* params into orig_repeat*
            for (Entry<String,List<String>> entry : mParamValues.entrySet()) {
                if (entry.getKey().startsWith("repeat")) {
                    mOrigRepeatParams.put("orig_"+entry.getKey(), entry.getValue().get(0));
                }
            }
            compose.initRepeat(compose.getSimpleRecurrence(), compose.getApptStartCalendar().getTime(), pageContext, mailbox);
        }

        return compose;
    }

    private String addToList(String currentValue, String newValue) {
        if (currentValue != null) currentValue = currentValue.trim();
        if (currentValue != null && currentValue.length() > 1) {
            if (currentValue.charAt(currentValue.length()-1) == ',')
                return currentValue + " " + newValue;
            else
                return currentValue + ", " + newValue;

        } else {
            return newValue;
        }
    }

    public List<FileItem> getItems() {
        return mItems;
    }

    public boolean hasParam(String name) { return mParamValues.get(name) != null; }

    @SuppressWarnings({"EmptyCatchBlock"})
    public long getParamLong(String name, long defaultValue) {
        String v = getParam(name);
        if (v != null)
            try { return Long.parseLong(v); } catch (NumberFormatException e) {}
        return defaultValue;
    }

    @SuppressWarnings({"EmptyCatchBlock"})
    public int getParamInt(String name, int defaultValue) {
        String v = getParam(name);
        if (v != null)
            try { return Integer.parseInt(v); } catch (NumberFormatException e) {}
        return defaultValue;
    }

    /**
     * Returns the value for the given param if present, otherwise null. If param has multiple values, only the
     * first is returned.
     * 
      * @param name parameter name
     * @return the value for the given param.
     */
    public String getParam(String name) {
        List<String> values = mParamValues.get(name);
        return values == null ? null : values.get(0);
    }

    /**
     * Returns the value for the given param if present, otherwise null.
     *
      * @param name parameter name
     * @return the value for the given param.
     */
    public List<String> getParamValueList(String name) {
        return mParamValues.get(name);
    }

    public Map<String,List<String>> getParamValues() {
        return mParamValues;
    }

    public Map<String,String> getOrigRepeatValues() {
        return mOrigRepeatParams;
    }

    public boolean getIsUpload() { return mIsUpload;}

    public ZMessageComposeBean getCompose() { return mComposeBean; }

    public boolean getIsCancel() { return hasParam(F_actionCancel); }

    public boolean getIsApptCancel() { return hasParam(F_actionApptCancel); }

    public boolean getIsApptDelete() { return hasParam(F_actionApptDelete); }    

    public boolean getIsDraft() { return hasParam(F_actionDraft); }

    public boolean getIsSend() { return hasParam(F_actionSend); }

    public boolean getIsSave() { return hasParam(F_actionSave); }

    public boolean getIsAttachCancel() { return hasParam(F_actionAttachCancel); }

    public boolean getIsAttachDone() { return hasParam(F_actionAttachDone); }

    public boolean getIsAttachAdd() { return hasParam(F_actionAttachAdd); }

    public boolean getIsContactCancel() { return hasParam(F_actionContactCancel); }

    public boolean getIsContactDone() { return hasParam(F_actionContactDone); }

    public boolean getIsContactAdd() { return hasParam(F_actionContactAdd); }

    public boolean getIsContactSearch() { return hasParam(F_actionContactSearch); }

    public boolean getIsRepeatEdit() { return hasParam(F_actionRepeatEdit); }

    public boolean getIsRepeatCancel() { return hasParam(F_actionRepeatCancel); }

    public boolean getIsRepeatDone() { return hasParam(F_actionRepeatDone); }

    public String getContactSearchQuery() { return getParam(F_contactSearchQuery); }

    public String getPendingTo() { return mPendingTo; }

    public String getPendingCc() { return mPendingCc; }

    public String getPendingBcc() { return mPendingBcc; }

    public String getPendingAttendees() { return mPendingAttendees; }

    public String getContactLocation() { return getParam(F_contactLocation); }
    
    private static DiskFileUpload getUploader() {
        // look up the maximum file size for uploads
        // TODO: get from config,
        long maxSize = DEFAULT_MAX_SIZE;

        DiskFileUpload upload = new DiskFileUpload();
        upload.setSizeThreshold(4096);     // in-memory limit
        upload.setSizeMax(maxSize);
        upload.setRepositoryPath(getTempDirectory());
        return upload;
    }

    private static String getTempDirectory() {
    	return System.getProperty("java.io.tmpdir", "/tmp");
    }
}
