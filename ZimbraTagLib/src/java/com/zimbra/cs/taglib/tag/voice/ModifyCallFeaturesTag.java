/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2007, 2008 Zimbra, Inc.
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
package com.zimbra.cs.taglib.tag.voice;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.taglib.bean.ZCallFeaturesBean;
import com.zimbra.cs.taglib.bean.ZCallFeatureBean;
import com.zimbra.cs.taglib.bean.ZCallForwardingBean;
import com.zimbra.cs.taglib.bean.ZSelectiveCallForwardingBean;
import com.zimbra.cs.taglib.bean.ZSelectiveCallRejectionBean;
import com.zimbra.cs.taglib.bean.ZVoiceMailPrefsBean;
import com.zimbra.cs.zclient.ZCallFeatures;
import com.zimbra.cs.zclient.ZMailbox;
import com.zimbra.cs.zclient.ZPhoneAccount;
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
		
			ZVoiceMailPrefsBean voiceMailPrefs = oldFeatures.getVoiceMailPrefs();
			ZCallForwardingBean callForwardingNoAnswer = oldFeatures.getCallForwardingNoAnswer();

			boolean autoPlayChanged = (mAutoPlayNewMsgs != null && (voiceMailPrefs == null || voiceMailPrefs.getAutoPlayNewMsgs() != mAutoPlayNewMsgs.booleanValue()));
			boolean playDateAndTimeInMsgEnvChanged = (mPlayDateAndTimeInMsgEnv != null && (voiceMailPrefs == null || voiceMailPrefs.getPlayDateAndTimeInMsgEnv() != mPlayDateAndTimeInMsgEnv.booleanValue()));
			boolean skipPinEntryChanged = (mSkipPinEntry != null && (voiceMailPrefs == null || voiceMailPrefs.getSkipPinEntry() != mSkipPinEntry.booleanValue()));
			boolean playCallerNameInMsgEnvChanged = (mPlayCallerNameInMsgEnv != null && (voiceMailPrefs == null || voiceMailPrefs.getPlayCallerNameInMsgEnv() != mPlayCallerNameInMsgEnv.booleanValue()));
			boolean promptLevelChanged = (mPromptLevel != null && (voiceMailPrefs == null || voiceMailPrefs.getPromptLevel() == null || !voiceMailPrefs.getPromptLevel().equals(mPromptLevel)));
			boolean answeringLocaleChanged = (mAnsweringLocale != null && (voiceMailPrefs == null || voiceMailPrefs.getAnsweringLocale() == null || !voiceMailPrefs.getAnsweringLocale().equals(mAnsweringLocale)));
			boolean userLocaleChanged = (mUserLocale != null && (voiceMailPrefs == null || voiceMailPrefs.getUserLocale() == null || !voiceMailPrefs.getUserLocale().equals(mUserLocale)));

			// If any vm setting has changed, set them all to the old values first
			if (voiceMailPrefs != null && (autoPlayChanged || playDateAndTimeInMsgEnvChanged || skipPinEntryChanged || playCallerNameInMsgEnvChanged || promptLevelChanged || answeringLocaleChanged || userLocaleChanged)) {
				newFeatures.getVoiceMailPrefs().setPlayDateAndTimeInMsgEnv(voiceMailPrefs.getPlayDateAndTimeInMsgEnv());
				newFeatures.getVoiceMailPrefs().setAutoPlayNewMsgs(voiceMailPrefs.getAutoPlayNewMsgs());
				newFeatures.getVoiceMailPrefs().setPromptLevel(voiceMailPrefs.getPromptLevel());
				newFeatures.getVoiceMailPrefs().setPlayCallerNameInMsgEnv(voiceMailPrefs.getPlayCallerNameInMsgEnv());
				newFeatures.getVoiceMailPrefs().setSkipPinEntry(voiceMailPrefs.getSkipPinEntry());
				newFeatures.getVoiceMailPrefs().setUserLocale(voiceMailPrefs.getUserLocale());
				newFeatures.getVoiceMailPrefs().setAnsweringLocale(voiceMailPrefs.getAnsweringLocale());
				newFeatures.getVoiceMailPrefs().setGreetingType(voiceMailPrefs.getGreetingType());
				newFeatures.getVoiceMailPrefs().setPlayTutorial(voiceMailPrefs.getPlayTutorial());
				newFeatures.getVoiceMailPrefs().setVoiceItemsPerPage(voiceMailPrefs.getVoiceItemsPerPage());
			}

			if (mEmailNotificationActive!=null && mEmailNotificationAddress!=null && 
				((!mEmailNotificationActive.booleanValue() && 
				(voiceMailPrefs.getEmailNotificationAddress() != null && voiceMailPrefs.getEmailNotificationAddress().length() > 0)) || !voiceMailPrefs.getEmailNotificationAddress().equalsIgnoreCase(mEmailNotificationAddress))) {
				String address = mEmailNotificationActive.booleanValue() ? mEmailNotificationAddress : "";
				newFeatures.getVoiceMailPrefs().setEmailNotificationAddress(address);
			}
			
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
				newFeatures.getVoiceMailPrefs().setAutoPlayNewMsgs(mAutoPlayNewMsgs.booleanValue());
			}
			if (playDateAndTimeInMsgEnvChanged) {
				newFeatures.getVoiceMailPrefs().setPlayDateAndTimeInMsgEnv(mPlayDateAndTimeInMsgEnv.booleanValue());
			}
			if (skipPinEntryChanged) {
				newFeatures.getVoiceMailPrefs().setSkipPinEntry(mSkipPinEntry.booleanValue());
			}
			if (playCallerNameInMsgEnvChanged) {
				newFeatures.getVoiceMailPrefs().setPlayCallerNameInMsgEnv(mPlayCallerNameInMsgEnv.booleanValue());
			}
			if (promptLevelChanged) {
				newFeatures.getVoiceMailPrefs().setPromptLevel(mPromptLevel);
			}
			if (answeringLocaleChanged) {
				newFeatures.getVoiceMailPrefs().setAnsweringLocale(mAnsweringLocale);
			}
			if (userLocaleChanged) {
				newFeatures.getVoiceMailPrefs().setUserLocale(mUserLocale);
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
