/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite, Network Edition.
 * Copyright (C) 2007, 2008 Zimbra, Inc.  All Rights Reserved.
 * ***** END LICENSE BLOCK *****
 */

package com.zimbra.zme.ui;

import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;

import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.DateField;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.ItemStateListener;
import javax.microedition.lcdui.StringItem;
import javax.microedition.lcdui.TextField;

import com.zimbra.zme.ResponseHdlr;
import com.zimbra.zme.ZimbraME;
import com.zimbra.zme.client.Appointment;
import com.zimbra.zme.client.Contact;
import com.zimbra.zme.client.Mailbox;

import de.enough.polish.ui.Choice;
import de.enough.polish.ui.FramedForm;
import de.enough.polish.ui.Style;
import de.enough.polish.ui.UiAccess;
import de.enough.polish.util.Locale;

public class ApptView extends View implements ResponseHdlr, ItemStateListener {
    
    protected static final Command SAVE = new Command(Locale.get("main.Save"), Command.ITEM, 1);
    
    private StringItem mHeader;
    private StringItem mOrganizerLabel;
    private StringItem mAttendeesLabel;
    private StringItem mTitleLabel;
    private StringItem mLocationLabel;
    private StringItem mStartLabel;
    private StringItem mEndLabel;
    private StringItem mAlldayLabel;
    private StringItem mNotesLabel;
    private StringItem mRepeatLabel;
    
    private AddrEntryItem mOrganizer;
    private AddrEntryItem mAttendees;
    private TextField mTitle;
    private TextField mLocation;
    private DateField mStart;
    private DateField mEnd;
    private ChoiceGroup mAllday;
    private TextField mNotes;
    private ChoiceGroup mRepeat;

    private Appointment mAppt;
    private Appointment mOrigAppt;
    
    private boolean mModified;
    
    private static final int MAX_INPUT_LEN = 256;
    
    //#ifdef polish.usePolishGui
    public ApptView(ZimbraME midlet, Appointment appt, Style style) {
        super(midlet);
        //#if true
            //# mView = new FramedForm(null, style);
        //#endif
        init(midlet, appt);
    }
    //#else
    public ApptView(ZimbraME midlet, Appointment appt) {
        super(midlet);
        //#if true
            //# mView = new FramedForm(null);
        //#endif
        init(midlet, appt);
    }
    //#endif
    
    private void init(ZimbraME midlet, Appointment appt) {
        mMidlet = midlet;
        mModified = false;
        
        FramedForm form = null;
        
        //#if true
            //# form = (FramedForm)mView;
        //#endif
        
        form.setItemStateListener(this);
        
        String header = (appt == null) ? Locale.get("appt.CreateAppt") : Locale.get("appt.ModifyAppt");
        //#style ApptViewHeader
        mHeader = new StringItem(null, header);
        form.append(Graphics.TOP, mHeader);
        
        mOrganizerLabel = new StringItem(null, Locale.get("appt.Organizer"));
        mAttendeesLabel = new StringItem(null, Locale.get("appt.Attendees"));
        mTitleLabel = new StringItem(null, Locale.get("appt.Title"));
        mLocationLabel = new StringItem(null, Locale.get("appt.Location"));
        mStartLabel = new StringItem(null, Locale.get("appt.Start"));
        mEndLabel = new StringItem(null, Locale.get("appt.End"));
        mNotesLabel = new StringItem(null, Locale.get("appt.Notes"));
        mRepeatLabel = new StringItem(null, Locale.get("appt.Repeat"));
        mAlldayLabel = new StringItem(null, Locale.get("appt.Allday"));
        
        //#style InputField
        mOrganizer = new AddrEntryItem(mMidlet);
        //#style InputField
        mAttendees = new AddrEntryItem(mMidlet);
        //#style InputField
        mTitle = new TextField("", null, MAX_INPUT_LEN, TextField.ANY);
        //#style InputField
        mLocation = new TextField("", null, MAX_INPUT_LEN, TextField.ANY);
        
        Calendar date = Calendar.getInstance();
        date.setTime(mMidlet.getCalendarView().getCurrentDate());
        Calendar time = Calendar.getInstance();
        time.setTime(new Date(System.currentTimeMillis()));
        date.set(Calendar.HOUR_OF_DAY, time.get(Calendar.HOUR_OF_DAY) + 1);
        
        //#style InputField
        mStart = new DateField("", DateField.DATE_TIME);
        mStart.setDate(date.getTime());
        
        date.set(Calendar.HOUR_OF_DAY, time.get(Calendar.HOUR_OF_DAY) + 2);
        //#style InputField
        mEnd = new DateField("", DateField.DATE_TIME);
        mEnd.setDate(date.getTime());

        //#style ApptChoiceGroup
        mAllday = new ChoiceGroup("", ChoiceGroup.MULTIPLE);
        //#style ApptChoiceItem
        mAllday.append("", null);
        
        //#style ApptDescriptionField
        mNotes = new TextField("", null, MAX_INPUT_LEN, TextField.ANY);
        
        //#style ApptChoiceGroupPopup
        mRepeat = new ChoiceGroup(null, Choice.POPUP);
        //#style ApptChoiceItemPopup
        mRepeat.append(Locale.get("appt.RepeatNone"), null);
        //#style ApptChoiceItemPopup
        mRepeat.append(Locale.get("appt.RepeatDaily"), null);
        //#style ApptChoiceItemPopup
        mRepeat.append(Locale.get("appt.RepeatWeekly"), null);
        //#style ApptChoiceItemPopup
        mRepeat.append(Locale.get("appt.RepeatEveryTwoWeek"), null);
        //#style ApptChoiceItemPopup
        mRepeat.append(Locale.get("appt.RepeatMonthly"), null);
        //#style ApptChoiceItemPopup
        mRepeat.append(Locale.get("appt.RepeatYearly"), null);
        //#style ApptChoiceItemPopup
        mRepeat.append(Locale.get("appt.RepeatCustom"), null);

        mView.addCommand(SAVE);
        mView.addCommand(CANCEL);
        mView.setCommandListener(this);
        
        mOrigAppt = appt;
        addFields();
        
        if (appt != null && !appt.mLoaded) {
            midlet.mMbox.getAppt(appt, this);
            Dialogs.popupWipDialog(mMidlet, this, Locale.get("appt.GettingAppt"));
        } else {
            mMidlet.mDisplay.setCurrent(mView);
        }
    }

    private void addFields() {
        FramedForm form = null;
        
        //#if true
            //# form = (FramedForm)mView;
        //#endif
    
        form.deleteAll();

        if (mOrigAppt != null) {
            if (mOrigAppt.mOrganizerEmail != null) {
                mOrganizer.setAddresses(new String[] {mOrigAppt.mOrganizerEmail});
                form.append(mOrganizerLabel);
                form.append(mOrganizer);
                //#style DisabledInputField
                UiAccess.setAccessible(mOrganizer, false);
            }
            mTitle.setString(mOrigAppt.mSubj);
            mLocation.setString(mOrigAppt.mLocation);
            mStart.setDate(new Date(mOrigAppt.mStart));
            if (mOrigAppt.mIsAllDay) {
                mAllday.setSelectedIndex(0, true);
                mStart.setInputMode(DateField.DATE);
                mEnd.setInputMode(DateField.DATE);
            }
            mEnd.setDate(new Date(mOrigAppt.mStart + mOrigAppt.mDuration));
            mNotes.setString(mOrigAppt.mDescription);
            
            if (mOrigAppt.mLoaded && 
                    mOrigAppt.mRecurrence != Appointment.CUSTOM &&
                    mRepeat.size() == 7) {
                mRepeat.delete(6);
            }
            mRepeat.setSelectedIndex(mOrigAppt.mRecurrence, true);
        } else {
            mAttendees.setMode(AddrEntryItem.NEW_MODE);
            form.append(mAttendeesLabel);
            form.append(mAttendees);
        }
        
        form.append(mTitleLabel);
        form.append(mTitle);
        form.append(mLocationLabel);
        form.append(mLocation);
        
        form.append(mStartLabel);
        form.append(mStart);
        form.append(mEndLabel);
        form.append(mEnd);
        form.append(mAlldayLabel);
        form.append(mAllday);

        form.append(mRepeatLabel);
        form.append(mRepeat);
        
        if (mOrigAppt != null && mOrigAppt.mAttendees.size() > 0) {
            String[] attn = new String[mOrigAppt.mAttendees.size()];
            int i = 0;
            Enumeration en = mOrigAppt.mAttendees.elements();
            while (en.hasMoreElements())
                attn[i++] = (String)en.nextElement();
            mAttendees.setAddresses(attn);
            mAttendees.setMode(AddrEntryItem.EDIT_MODE);
            form.append(mAttendeesLabel);
            form.append(mAttendees);
        }
        
        /*
         * there is no clean way to get just the notes field
        form.append(mNotesLabel);
        form.append(mNotes);
        */
        
        //Gets around J2ME-Polish bug that stops focus in the last element if it has a colspan > 1
        form.append("");
    }

    private Appointment populate() {
        Appointment appt = new Appointment(mOrigAppt);
        appt.mSubj = (mTitle.getString() == null) ? "" : mTitle.getString();
        appt.mLocation = (mLocation.getString() == null) ? "" : mLocation.getString();
        appt.mStart = mStart.getDate().getTime();
        appt.mDuration = getDurationInMilli();
        appt.mIsAllDay = mAllday.isSelected(0);
        appt.mAmIOrganizer = true;
        appt.mApptStatus = Appointment.EVT_CONFIRMED;
        appt.mMyStatus = Appointment.ACCEPTED;
        appt.mDescription = (mNotes.getString() == null) ? "" : mNotes.getString();
        appt.mRecurrence = mRepeat.getSelectedIndex();
        appt.mAttendees.removeAllElements();
        if (mAttendees.getContacts() != null) {
            Enumeration contacts = mAttendees.getContacts().elements();
            while (contacts.hasMoreElements())
                appt.mAttendees.addElement(((Contact)contacts.nextElement()).mEmail);
        }
        
        mAppt = appt;
        return appt;
    }
    
    private long getDurationInMilli() {
        long s = mStart.getDate().getTime();
        long e = mEnd.getDate().getTime();
        return (e - s);
    }
    public void commandAction(Command cmd, 
                              Displayable d) {
        if (d == mView) {
            FramedForm form = null;
            //#if true
                //# form = (FramedForm)mView;
            //#endif
        
            if (form == null)
                return;
            else if (cmd == SAVE && mModified) {
                if (mOrigAppt == null) {
                    Dialogs.popupWipDialog(mMidlet, this, Locale.get("appt.CreatingAppt"));
                    mMidlet.mMbox.createAppt(populate(), this);
                } else {
                    Dialogs.popupWipDialog(mMidlet, this, Locale.get("appt.UpdatingAppt"));
                    mMidlet.mMbox.modifyAppt(populate(), this);
                }
            } else if (cmd == CANCEL && mModified)
                Dialogs.popupConfirmDialog(mMidlet, this, Locale.get("appt.CancelContinue"));
            else
                setNextCurrent();
        } else if (d == Dialogs.mConfirmD){
            if (cmd == Dialogs.YES)
                setNextCurrent();
            else
                mMidlet.mDisplay.setCurrent(mView);
        } else if (d == Dialogs.mErrorD) {
            mMidlet.mDisplay.setCurrent(mView);
        } else if (d == Dialogs.mWipD) {
            mMidlet.mMbox.cancelOp();
            mMidlet.mDisplay.setCurrent(mView);
        }
    }

    public void handleResponse(Object op, 
                               Object resp) {   
        //#debug
        System.out.println("ApptView.handleResponse");
        if (resp instanceof Mailbox) {
            if (op == Mailbox.CREATEAPPT) {
                //#debug 
                System.out.println("ApptView.handleResponse: CreateAppt successful");
                mMidlet.getCalendarView().addAppt(mAppt);
                setNextCurrent();
            } else if (op == Mailbox.MODIFYAPPT) {
                //#debug 
                System.out.println("ApptView.handleResponse: ModifyAppt successful");
                if (mOrigAppt != null)
                    mOrigAppt.copyFrom(mAppt);
                mMidlet.getCalendarView().modifyAppt(mOrigAppt);
                setNextCurrent();
            } else if (op == Mailbox.GETAPPT) {
                //#debug 
                System.out.println("ApptView.handleResponse: GetAppt successful");
                addFields();
                mMidlet.mDisplay.setCurrent(mView);
            }
        } else {
            mMidlet.handleResponseError(resp, this);
        }
    }

    public void itemStateChanged(Item item) {
        mModified = true;
        if (item == mAllday) {
            boolean isAllday = mAllday.isSelected(0);
            if (isAllday) {
                mStart.setInputMode(DateField.DATE);
                mEnd.setInputMode(DateField.DATE);
            } else {
                mStart.setInputMode(DateField.DATE_TIME);
                mEnd.setInputMode(DateField.DATE_TIME);
            }
        }
    }
}
