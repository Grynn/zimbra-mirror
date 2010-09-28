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
package com.zimbra.cs.taglib.tag.filter;

import com.zimbra.cs.taglib.tag.ZimbraSimpleTag;
import com.zimbra.cs.taglib.bean.ZTagLibException;
import com.zimbra.cs.zclient.ZFilterRule;
import com.zimbra.cs.zclient.ZMailbox;
import com.zimbra.cs.zclient.ZFilterRules;
import com.zimbra.common.service.ServiceException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

public class ModifyFilterRuleTag extends ZimbraSimpleTag {

    private ZFilterRule mRule;
    private String mOriginalName;

    public void setRule(ZFilterRule rule) { mRule = rule; }
    public void setOriginalname(String originalName) { mOriginalName = originalName; }

    public void doTag() throws JspException, IOException {
        try {
            if (mOriginalName == null)
                mOriginalName = mRule.getName();

            ZMailbox mbox = getMailbox();
            ZFilterRules rules = mbox.getIncomingFilterRules(true);
            List<ZFilterRule> newRules = new ArrayList<ZFilterRule>();
            boolean origFound = false;

            for (ZFilterRule rule: rules.getRules()) {

                if (rule.getName().equalsIgnoreCase(mOriginalName)) {
                    newRules.add(mRule);
                    origFound = true;
                } else if (rule.getName().equalsIgnoreCase(mRule.getName())) {
                    throw ZTagLibException.FILTER_EXISTS("filter with name "+mRule.getName()+" already exists", null);
                } else {
                    newRules.add(rule);
                }
            }
            if (!origFound) {
                throw ZTagLibException.NO_SUCH_FILTER_EXISTS("filter with name "+mRule.getName()+" doesn't exist", null);                
            }
            mbox.saveIncomingFilterRules(new ZFilterRules(newRules));
        } catch (ServiceException e) {
            throw new JspTagException(e);
        }
    }
}
