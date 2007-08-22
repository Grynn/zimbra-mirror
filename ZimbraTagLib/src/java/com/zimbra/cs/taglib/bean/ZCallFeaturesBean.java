/*
 * ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1
 *
 * The contents of this file are subject to the Mozilla Public License
 * Version 1.1 ("License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.zimbra.com/license
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
 * the License for the specific language governing rights and limitations
 * under the License.
 *
 * The Original Code is: Zimbra Collaboration Suite Server.
 *
 * The Initial Developer of the Original Code is Zimbra, Inc.
 * Portions created by Zimbra are Copyright (C) 2007 Zimbra, Inc.
 * All Rights Reserved.
 *
 * Contributor(s):
 *
 * ***** END LICENSE BLOCK *****
 */

package com.zimbra.cs.taglib.bean;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.VoiceConstants;
import com.zimbra.cs.zclient.ZCallFeatures;

public class ZCallFeaturesBean {

    private ZCallFeatures mFeatures;

    public ZCallFeaturesBean(ZCallFeatures features, boolean modify) {
        mFeatures = features;
    }

    public ZCallFeatures getCallFeatures() {
        return mFeatures;
    }

    public ZVoiceMailPrefsBean getVoiceMailPrefs() {
        return new ZVoiceMailPrefsBean(mFeatures.getVoiceMailPrefs());
    }

    public ZCallForwardingBean getCallForwardingAll() throws ServiceException {
        return new ZCallForwardingBean(mFeatures.getFeature(VoiceConstants.E_CALL_FORWARD));
    }

    public ZSelectiveCallForwardingBean getSelectiveCallForwarding() throws ServiceException {
        return new ZSelectiveCallForwardingBean(mFeatures.getSelectiveCallForwarding());
    }

    public boolean isEmpty() { return mFeatures.isEmpty(); }
}
