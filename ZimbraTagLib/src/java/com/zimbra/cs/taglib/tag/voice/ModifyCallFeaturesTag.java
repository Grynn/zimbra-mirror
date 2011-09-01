/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2007, 2008, 2009, 2010 Zimbra, Inc.
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
package com.zimbra.cs.taglib.tag.voice;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.taglib.bean.ZCallFeaturesBean;
import com.zimbra.cs.taglib.bean.ZCallFeatureBean;
import com.zimbra.cs.taglib.bean.ZCallForwardingBean;
import com.zimbra.cs.taglib.bean.ZSelectiveCallForwardingBean;
import com.zimbra.cs.taglib.bean.ZSelectiveCallRejectionBean;
import com.zimbra.cs.taglib.bean.ZVoiceMailPrefsBean;
import com.zimbra.client.ZCallFeatures;
import com.zimbra.client.ZMailbox;
import com.zimbra.client.ZPhoneAccount;
import com.zimbra.cs.account.Provisioning;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.PageContext;
import java.io.IOException;
import java.util.Map;
import java.util.HashMap;

public class ModifyCallFeaturesTag extends CallFeaturesTagBase {

	public void doTag() throws JspException, IOException {
		try {
			ZMailbox mailbox = getMailbox();
			ZPhoneAccount account = mailbox.getPhoneAccount(mPhone);
			ZCallFeaturesBean oldFeatures = new ZCallFeaturesBean(account.getCallFeatures(), false);
			ZCallFeaturesBean newFeatures = new ZCallFeaturesBean(new ZCallFeatures(mailbox, account.getPhone()), true);
		
			ZVoiceMailPrefsBean oldVoiceMailPrefs = oldFeatures.getVoiceMailPrefs();
			ZCallForwardingBean callForwardingNoAnswer = oldFeatures.getCallForwardingNoAnswer();
			ZVoiceMailPrefsBean newVoiceMailPrefs = newFeatures.getVoiceMailPrefs();

			boolean autoPlayChanged = (mAutoPlayNewMsgs != null && (oldVoiceMailPrefs == null || oldVoiceMailPrefs.getAutoPlayNewMsgs() != mAutoPlayNewMsgs.booleanValue()));
			boolean playDateAndTimeInMsgEnvChanged = (mPlayDateAndTimeInMsgEnv != null && (oldVoiceMailPrefs == null || oldVoiceMailPrefs.getPlayDateAndTimeInMsgEnv() != mPlayDateAndTimeInMsgEnv.booleanValue()));
			boolean skipPinEntryChanged = (mSkipPinEntry != null && (oldVoiceMailPrefs == null || oldVoiceMailPrefs.getSkipPinEntry() != mSkipPinEntry.booleanValue()));
			boolean playCallerNameInMsgEnvChanged = (mPlayCallerNameInMsgEnv != null && (oldVoiceMailPrefs == null || oldVoiceMailPrefs.getPlayCallerNameInMsgEnv() != mPlayCallerNameInMsgEnv.booleanValue()));
			boolean promptLevelChanged = (mPromptLevel != null && (oldVoiceMailPrefs == null || oldVoiceMailPrefs.getPromptLevel() == null || !oldVoiceMailPrefs.getPromptLevel().equals(mPromptLevel)));
			boolean answeringLocaleChanged = (mAnsweringLocale != null && (oldVoiceMailPrefs == null || oldVoiceMailPrefs.getAnsweringLocale() == null || !oldVoiceMailPrefs.getAnsweringLocale().equals(mAnsweringLocale)));
			boolean userLocaleChanged = (mUserLocale != null && (oldVoiceMailPrefs == null || oldVoiceMailPrefs.getUserLocale() == null || !oldVoiceMailPrefs.getUserLocale().equals(mUserLocale)));

			boolean notifTransChanged = (mEmailNotifTrans != null && (oldVoiceMailPrefs == null || oldVoiceMailPrefs.getEmailNotifTrans() != mEmailNotifTrans.booleanValue()));
			boolean notifAttachChanged = (mEmailNotifAttach != null && (oldVoiceMailPrefs == null || oldVoiceMailPrefs.getEmailNotifAttach() != mEmailNotifAttach.booleanValue()));

			// If any vm setting has changed, set them all to the old values first
			if (oldVoiceMailPrefs != null && (autoPlayChanged || playDateAndTimeInMsgEnvChanged || skipPinEntryChanged || playCallerNameInMsgEnvChanged || promptLevelChanged || answeringLocaleChanged || userLocaleChanged || notifTransChanged || notifAttachChanged)) {
				newVoiceMailPrefs.setPlayDateAndTimeInMsgEnv(oldVoiceMailPrefs.getPlayDateAndTimeInMsgEnv());
				newVoiceMailPrefs.setAutoPlayNewMsgs(oldVoiceMailPrefs.getAutoPlayNewMsgs());
				newVoiceMailPrefs.setPromptLevel(oldVoiceMailPrefs.getPromptLevel());
				newVoiceMailPrefs.setPlayCallerNameInMsgEnv(oldVoiceMailPrefs.getPlayCallerNameInMsgEnv());
				newVoiceMailPrefs.setSkipPinEntry(oldVoiceMailPrefs.getSkipPinEntry());
				newVoiceMailPrefs.setUserLocale(oldVoiceMailPrefs.getUserLocale());
				newVoiceMailPrefs.setAnsweringLocale(oldVoiceMailPrefs.getAnsweringLocale());
				newVoiceMailPrefs.setGreetingType(oldVoiceMailPrefs.getGreetingType());
				newVoiceMailPrefs.setPlayTutorial(oldVoiceMailPrefs.getPlayTutorial());
				newVoiceMailPrefs.setVoiceItemsPerPage(oldVoiceMailPrefs.getVoiceItemsPerPage());
				newVoiceMailPrefs.setEmailNotifTrans(oldVoiceMailPrefs.getEmailNotifTrans());
				newVoiceMailPrefs.setEmailNotifAttach(oldVoiceMailPrefs.getEmailNotifAttach());
			}

			newVoiceMailPrefs.setEmailNotificationAddress((mEmailNotificationActive != null && mEmailNotificationActive.booleanValue()) ? mEmailNotificationAddress : "");
			
			ZCallForwardingBean callForwarding = oldFeatures.getCallForwardingAll();
			if (mCallForwardingActive!=null && mCallForwardingForwardTo!=null && 
				(callForwarding.getIsActive() != mCallForwardingActive.booleanValue() || !callForwarding.getForwardTo().equals(mCallForwardingForwardTo))) {
				ZCallForwardingBean newCallForwarding = newFeatures.getCallForwardingAll();
				newCallForwarding.setIsActive(mCallForwardingActive.booleanValue());
				newCallForwarding.setForwardTo(mCallForwardingForwardTo);
			}
			
			ZSelectiveCallForwardingBean selectiveCallForwarding = oldFeatures.getSelectiveCallForwarding();

			if (mSelectiveCallForwardingActive!=null) {
				ZSelectiveCallForwardingBean newSelectiveCallForwarding = newFeatures.getSelectiveCallForwarding();
				if (mSelectiveCallForwardingForwardTo!=null && mSelectiveCallForwardingForwardTo.length()>0) {
					newSelectiveCallForwarding.setForwardTo(mSelectiveCallForwardingForwardTo);
					if (mSelectiveCallForwardingForwardFrom!=null && mSelectiveCallForwardingForwardFrom.size()>0) {
					newSelectiveCallForwarding.setForwardFrom(mSelectiveCallForwardingForwardFrom);
					newSelectiveCallForwarding.setIsActive(mSelectiveCallForwardingActive.booleanValue());
					} else {
						newSelectiveCallForwarding.setIsActive(false);
					}
				} else {
					newSelectiveCallForwarding.setIsActive(false);
				}
			}
			
			ZCallFeatureBean anonymousCallRejection = oldFeatures.getAnonymousCallRejection();
			if (mAnonymousCallRejectionActive!=null && 
			(anonymousCallRejection.getIsActive() != mAnonymousCallRejectionActive.booleanValue())) {
			newFeatures.getAnonymousCallRejection().setIsActive(mAnonymousCallRejectionActive.booleanValue());
			}
			
			ZSelectiveCallRejectionBean selectiveCallRejection = oldFeatures.getSelectiveCallRejection();
			if (mSelectiveCallRejectionActive!=null && mSelectiveCallRejectionRejectFrom!=null && selectiveCallRejection!=null &&
				(selectiveCallRejection.getIsActive() != mSelectiveCallRejectionActive.booleanValue() || (selectiveCallRejection.getRejectFrom() != null && !selectiveCallRejection.getRejectFrom().equals(mSelectiveCallRejectionRejectFrom)))) {
					ZSelectiveCallRejectionBean newSelectiveCallRejection = newFeatures.getSelectiveCallRejection();
					if (mSelectiveCallRejectionRejectFrom!=null && mSelectiveCallRejectionRejectFrom.size() > 0) {
						newSelectiveCallRejection.setRejectFrom(mSelectiveCallRejectionRejectFrom);
						newSelectiveCallRejection.setIsActive(mSelectiveCallRejectionActive.booleanValue());
					} else {
						newSelectiveCallRejection.setIsActive(false);
					}
			}
			
			if (mNumberOfRings != null && (callForwardingNoAnswer == null || callForwardingNoAnswer.getNumberOfRings() != mNumberOfRings.intValue())) {
				newFeatures.getCallForwardingNoAnswer().setIsActive(true);
				newFeatures.getCallForwardingNoAnswer().setNumberOfRings(mNumberOfRings.intValue());
			}	
			if (autoPlayChanged) {
				newVoiceMailPrefs.setAutoPlayNewMsgs(mAutoPlayNewMsgs.booleanValue());
			}
			if (playDateAndTimeInMsgEnvChanged) {
				newVoiceMailPrefs.setPlayDateAndTimeInMsgEnv(mPlayDateAndTimeInMsgEnv.booleanValue());
			}
			if (skipPinEntryChanged) {
				newVoiceMailPrefs.setSkipPinEntry(mSkipPinEntry.booleanValue());
			}
			if (playCallerNameInMsgEnvChanged) {
				newVoiceMailPrefs.setPlayCallerNameInMsgEnv(mPlayCallerNameInMsgEnv.booleanValue());
			}
			if (promptLevelChanged) {
				newVoiceMailPrefs.setPromptLevel(mPromptLevel);
			}
			if (answeringLocaleChanged) {
				newVoiceMailPrefs.setAnsweringLocale(mAnsweringLocale);
			}
			if (userLocaleChanged) {
				newVoiceMailPrefs.setUserLocale(mUserLocale);
			}
			if (notifTransChanged) {
				newVoiceMailPrefs.setEmailNotifTrans(mEmailNotifTrans);
			}
			if (notifAttachChanged) {
				newVoiceMailPrefs.setEmailNotifAttach(mEmailNotifAttach);
			}
				
			boolean update = false;
			if (!newFeatures.isEmpty()) {
				mailbox.saveCallFeatures(newFeatures.getCallFeatures());
				update = true;
			}
			if (mNumberPerPage!=null && mailbox.getPrefs().getVoiceItemsPerPage() != mNumberPerPage.longValue()) {
				Map<String, Object> attrs = new HashMap<String,Object>();
				attrs.put(Provisioning.A_zimbraPrefVoiceItemsPerPage, Long.toString(mNumberPerPage.longValue()));
				mailbox.modifyPrefs(attrs);
				update = true;
				mailbox.getPrefs(true);
			}
			getJspContext().setAttribute(mVar, update, PageContext.PAGE_SCOPE);
		} catch (ServiceException e) {
			throw new JspTagException(e);
		}
	}
}
