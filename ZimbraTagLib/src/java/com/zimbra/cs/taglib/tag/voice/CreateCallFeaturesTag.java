/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2007 Zimbra, Inc.
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
import com.zimbra.cs.taglib.bean.ZCallForwardingBean;
import com.zimbra.cs.taglib.bean.ZSelectiveCallForwardingBean;
import com.zimbra.cs.zclient.ZCallFeatures;
import com.zimbra.cs.zclient.ZMailbox;
import com.zimbra.cs.zclient.ZPhoneAccount;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.PageContext;
import java.io.IOException;

public class CreateCallFeaturesTag extends CallFeaturesTagBase {
    public void doTag() throws JspException, IOException {
        try {
            ZMailbox mailbox = getMailbox();
            ZPhoneAccount account = mailbox.getPhoneAccount(mPhone);
            ZCallFeaturesBean newFeatures = new ZCallFeaturesBean(new ZCallFeatures(mailbox, account.getPhone()), true);

            String address = mEmailNotificationActive ? mEmailNotificationAddress : "";
            newFeatures.getVoiceMailPrefs().setEmailNotificationAddress(address);

			ZCallForwardingBean newCallForwarding = newFeatures.getCallForwardingAll();
			newCallForwarding.setIsActive(mCallForwardingActive);
			newCallForwarding.setForwardTo(mCallForwardingForwardTo);

            ZSelectiveCallForwardingBean newSelectiveCallForwarding = newFeatures.getSelectiveCallForwarding();
            newSelectiveCallForwarding.setIsActive(mSelectiveCallForwardingActive);
            newSelectiveCallForwarding.setForwardTo(mSelectiveCallForwardingForwardTo);
            newSelectiveCallForwarding.setForwardFrom(mSelectiveCallForwardingForwardFrom);

            getJspContext().setAttribute(mVar, newFeatures, PageContext.PAGE_SCOPE);
        } catch (ServiceException e) {
            throw new JspTagException(e);
        }
    }
}
