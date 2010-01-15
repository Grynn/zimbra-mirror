/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2007, 2009 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.2 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */

package com.zimbra.cs.taglib.bean;

import com.zimbra.cs.zclient.ZPhone;
import com.zimbra.cs.zclient.ZSelectiveCallForwarding;

import java.util.ArrayList;
import java.util.List;

public class ZSelectiveCallForwardingBean extends ZCallForwardingBean {

    public ZSelectiveCallForwardingBean(ZSelectiveCallForwarding feature) {
        super(feature);
    }

    public List<String> getForwardFrom() {
        List<String> data = getFeature().getForwardFrom();
        List<String> result = new ArrayList<String>(data.size());
        for (String name : data) {
            result.add(ZPhone.getDisplay(name));
        }
        return result;
    }

    public void setForwardFrom(List<String> list) {
        List<String> names = new ArrayList<String>(list.size());
        for (String display : list) {
            names.add(ZPhone.getNonFullName(display));
        }
        getFeature().setForwardFrom(names);
    }

    protected ZSelectiveCallForwarding getFeature() {
        return (ZSelectiveCallForwarding) super.getFeature(); 
    }
}
