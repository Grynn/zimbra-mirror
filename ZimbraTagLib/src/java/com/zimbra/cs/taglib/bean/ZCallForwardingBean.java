/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2007, 2009, 2010 Zimbra, Inc.
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

package com.zimbra.cs.taglib.bean;

import com.zimbra.cs.zclient.ZCallFeature;
import com.zimbra.cs.zclient.ZPhone;
import com.zimbra.common.soap.VoiceConstants;
import com.zimbra.common.service.ServiceException;

public class ZCallForwardingBean extends ZCallFeatureBean {
    public ZCallForwardingBean(ZCallFeature feature) {
        super(feature);
    }

    public void setForwardTo(String phone) {
        String name = ZPhone.getNonFullName(phone);
        getFeature().setData(VoiceConstants.A_FORWARD_TO, name);
    }
    
    public int getNumberOfRings() {
	String rings = getFeature().getData(VoiceConstants.A_NUM_RING_CYCLES);
	try {
	    return Integer.parseInt(rings);
	} catch (NumberFormatException ex) {
	    return -1;
	}
    }
    
    public void setNumberOfRings(int rings) {
	getFeature().setData(VoiceConstants.A_NUM_RING_CYCLES, Integer.toString(rings));
    }

    public String getForwardTo() throws ServiceException {
		String name = getFeature().getData(VoiceConstants.A_FORWARD_TO);
		if (name == null) {
			name = "";
		}
		return ZPhone.getDisplay(name);
    }
}
