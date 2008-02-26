/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite J2ME Client
 * Copyright (C) 2007 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Yahoo! Public License
 * Version 1.0 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */

package com.zimbra.zme.ui;

import java.util.Vector;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.ItemStateListener;
import javax.microedition.lcdui.StringItem;
import javax.microedition.lcdui.TextField;

import com.zimbra.zme.ResponseHdlr;
import com.zimbra.zme.Util;
import com.zimbra.zme.ZimbraME;
import com.zimbra.zme.client.Mailbox;

import de.enough.polish.ui.FramedForm;
import de.enough.polish.ui.Style;
import de.enough.polish.ui.UiAccess;
import de.enough.polish.util.Locale;

public class ComposeView extends View implements ResponseHdlr, ItemStateListener {
	
	private static final int MAX_SUBJ_LEN = 1024;
	private static final int MAX_BODY_LEN = 1024 * 5;
		
	private boolean mCcBCcShowing;
	private StringItem mHeader;
	private StringItem mToLabel;
	private StringItem mCcLabel;
	private StringItem mBccLabel;
	private StringItem mSubjLabel;
	private AddrEntryItem mToField;
	private AddrEntryItem mCcField;
	private AddrEntryItem mBccField;
	private TextField mSubjField;
	private TextField mBodyField;
	private StringItem mForwardInfo;
	private boolean mModified;
	private boolean mForward;
	private String mOrigId;
	private Command mSendCmd;
	private Command mToggleCcBccCmd;
	
	//#ifdef polish.usePolishGui
		public ComposeView(ZimbraME midlet,
						   Style style) {
			super(midlet);
			//#if true
				//# mView = new FramedForm(null, style);
			//#endif
			init(midlet);
		}
	//#else
		public ComposeView(ZimbraME midlet) {
			super(midlet);
			//#if true
				//# mView = new FramedForm(null);
			//#endif
			init(midlet);
		}	
	//#endif
	
	private void init(ZimbraME midlet) {
		mMidlet = midlet;
		mCcBCcShowing = false;
		
		mView.setCommandListener(this);
		
		FramedForm form = null;
		
		//#if true
			//# form = (FramedForm)mView;
		//#endif
		
		form.setItemStateListener(this);
		
		//#style ComposeViewHeader
		mHeader = new StringItem(null, Locale.get("compose.ComposeMessage"));
		form.append(Graphics.TOP, mHeader);
		
		mToLabel = new StringItem(null, Locale.get("compose.To"));
		mCcLabel = new StringItem(null, Locale.get("compose.Cc"));
		mBccLabel = new StringItem(null, Locale.get("compose.Bcc"));
		mSubjLabel = new StringItem(null, Locale.get("compose.Subject"));
		
		//#style InputField
		mToField = new AddrEntryItem(mMidlet);
		
		//#style InputField
		mCcField = new AddrEntryItem(mMidlet);

		//#style InputField
		mBccField = new AddrEntryItem(mMidlet);

		//#style InputField
		mSubjField = new TextField("", null, MAX_SUBJ_LEN, TextField.ANY);

		//#style MailBodyField
		mBodyField = new TextField("", null, MAX_BODY_LEN, TextField.ANY);

		//#style ForwardInfo
		mForwardInfo = new StringItem(null, Locale.get("compose.ForwardAsAttach"));
		
		addFields();

		mSendCmd = new Command(Locale.get("compose.Send"), Command.ITEM, 1);
		mToggleCcBccCmd = new Command(Locale.get("compose.AddCcBcc"), Command.ITEM, 1);
		

		mView.addCommand(CANCEL);
		mView.addCommand(mSendCmd);
		mView.addCommand(mToggleCcBccCmd);
	}
	
	private void addFields() {
		FramedForm form = null;
		
		//#if true
			//# form = (FramedForm)mView;
		//#endif
	
		
		form.append(mToLabel);
		form.append(mToField);
		
		if (mCcBCcShowing) {
			form.append(mCcLabel);
			form.append(mCcField);
			form.append(mBccLabel);
			form.append(mBccField);			
		}
		
		form.append(mSubjLabel);
		form.append(mSubjField);
		//form.append(mBodyLabel);
		
		if (mForward)
			form.append(mForwardInfo);
		
		form.append(mBodyField);
		
		//Gets around J2ME-Polish bug that stops focus in the last element if it has a colspan > 1
		form.append("");
	}

	public void reset(String[] toAddrs,
			          String[] ccAddrs,
			          String[] bccAddrs,
			          String subj,
			          String body,
			          boolean forward) {
		mToField.setAddresses(toAddrs);
		mCcField.setAddresses(ccAddrs);
		mBccField.setAddresses(bccAddrs);
		mSubjField.setString(subj);
		mBodyField.setString(body);
		mToField.setMode(AddrEntryItem.NEW_MODE);
		mCcField.setMode(AddrEntryItem.NEW_MODE);
		mBccField.setMode(AddrEntryItem.NEW_MODE);
		mForward = forward;
		mOrigId = null;
		mModified = false;
		showCcBcc(ccAddrs != null);
	}
	
	public void forward(MsgItem m) {
		StringBuffer subject = new StringBuffer(Locale.get("compose.Fwd"));	
		if (m.mSubject != null)
			subject.append(m.mSubject);
				
		reset(null, null, null, subject.toString(), null, true);
		mOrigId = m.mId;
	}
	
	public void reply(MsgItem m,
					  boolean replyToAll) {
		
		StringBuffer body = new StringBuffer(Locale.get("compose.OriginalMsg"));
		String addr = null;
		String dispName = null;
		
		if (m.mReplyToAddr != null) {
			addr = m.mReplyToAddr;
			dispName = m.mReplyTo;
		} else if (m.mFromAddr != null) {
			addr = m.mFromAddr;
			dispName = m.mFrom; 
		} else if (m.mSenderAddr != null) {
			addr = m.mSenderAddr;
			dispName = m.mSender; 	
		}
		
		if (addr != null) {
			body.append(Locale.get("compose.From"));
			if (dispName != null && dispName.length() > 0)
				body.append(dispName).append(" <").append(addr).append(">\n");
			else
				body.append(addr).append('\n');
		}
		
		String[] toRecipients = m.getToRecipientsAddr();
		int numToRecipients = m.getNumToRecipients();
		if (toRecipients != null)
			body.append(Locale.get("compose.To")).append(' ').append(addrArray2String(toRecipients, m.getToRecipients(), numToRecipients)).append('\n');
	
		String [] ccRecipients = m.getCcRecipientsAddr();
		int numCcRecipients = m.getNumCcRecipients();
		if (ccRecipients != null)
			body.append(Locale.get("compose.Cc")).append(' ').append(addrArray2String(ccRecipients, m.getCcRecipients(), numCcRecipients)).append('\n');
		
		if (m.mSentDate != 0)
			body.append(Locale.get("compose.Sent")).append(Util.getFullDateTime(m.getSentDateAsCalendar(), true)).append('\n');
		
		if (m.mSubject != null)
			body.append(Locale.get("compose.FullSubject")).append(m.mSubject).append('\n');
		
		if (m.getBody() != null)
			body.append("\n").append(m.getBody());
		
		StringBuffer subject = new StringBuffer(Locale.get("compose.Re"));	
		if (m.mSubject != null)
			subject.append(m.mSubject);
		
		String[] recipients = null;
		if (replyToAll) {
			int sz = 0; 
			if (toRecipients != null)
				sz += numToRecipients;
			if (ccRecipients != null)
				sz += numCcRecipients;
			
			if (sz > 0) {
				recipients = new String[sz];
				int i;
				int j = 0;
				if (toRecipients != null)
					for (i = 0; i < numToRecipients; i++)
						recipients[j++] = toRecipients[i];
				
				if (ccRecipients != null)
					for (i = 0; i < numCcRecipients; i++)
						recipients[j++] = ccRecipients[i];
			}	                          
		}
				
		reset(((addr != null) ? new String[] {addr} : null), recipients, null, subject.toString(), body.toString(), false);
		mToField.setMode(AddrEntryItem.EDIT_MODE);
		mCcField.setMode(AddrEntryItem.EDIT_MODE);
		mBccField.setMode(AddrEntryItem.EDIT_MODE);
		mOrigId = m.mId;
	}
	
	public void commandAction(Command cmd, 
							  Displayable d) {
		if (d == mView) {
			if (cmd == CANCEL) {
				/* If any of the fields are not null && the modified flag is set, then prompt for confirmation. This
				 * is not a perfect algorithm, but hopefully good enough!*/
				if ((mToField.getContacts() != null || mCcField.getContacts() != null 
					|| mBccField.getContacts() != null || mSubjField.getString().length() > 0 
					|| mBodyField.getString().length() > 0) && mModified)
					Dialogs.popupConfirmDialog(mMidlet, this, Locale.get("compose.CancelContinue"));
				else
					setNextCurrent();
				mForward = false;
			} else if (cmd == mToggleCcBccCmd) {
				showCcBcc(!mCcBCcShowing);
			} else if (cmd == mSendCmd) {
				Vector to = mToField.getContacts();
				Vector cc = mCcField.getContacts();
				Vector bcc = mBccField.getContacts();
				if (to != null || cc != null || bcc != null) {
					Dialogs.popupWipDialog(mMidlet, this, Locale.get("compose.SendingMsg"));
					mMidlet.mMbox.sendMsg(to, cc, bcc,  mSubjField.getString(), mBodyField.getString(), mOrigId, mForward, this);
				} else {
					Dialogs.popupErrorDialog(mMidlet, this, Locale.get("compose.MustHaveAddr"));
				}
			}
		} else if (d == Dialogs.mConfirmD){
			if (cmd == Dialogs.YES)
				setNextCurrent();
			else
				mMidlet.mDisplay.setCurrent(mView);
		} else if (d == Dialogs.mWipD) {
			mMidlet.mMbox.cancelOp();
			mMidlet.mDisplay.setCurrent(mView);
		} else if (d == Dialogs.mErrorD) {
			mMidlet.mDisplay.setCurrent(mView);
		}
	}

	public void handleResponse(Object op, 
							   Object resp) {	
		//#debug
		System.out.println("ComposeView.handleResponse");
		if (resp instanceof Mailbox) {
			//#debug 
			System.out.println("ComposeView.handleResponse: SendMsg successful");
			setNextCurrent();
		} else {
			mMidlet.handleResponseError(resp, this);
		}
		
	}

	public void itemStateChanged(Item item) {
		mModified = true;
	}
	
	private String addrArray2String(String[] addrArray,
									String[] dispNameArray,
									int numAddrs) {
		StringBuffer str = new StringBuffer();
		for (int i = 0; i < numAddrs; i++) {
			if (dispNameArray[i].length() > 0)
				str.append(dispNameArray[i]).append(" <").append(addrArray[i]).append(">");
			else
				str.append(addrArray[i]);
			if (i < addrArray.length - 1)
				str.append(", ");
		}
		return str.toString();
	}
	
	private void showCcBcc(boolean ccBccShowing) {
		// We do delete all and add, because on certain phones, removing specific elements
		// and re-adding them later freaks out the display
		mCcBCcShowing = ccBccShowing;
		mView.deleteAll();
		addFields();
		UiAccess.setCommandLabel(mView, mToggleCcBccCmd, 
								 ((mCcBCcShowing) ? Locale.get("compose.RemoveCcBcc") : Locale.get("compose.AddCcBcc")));
		
	}
}
