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
package com.zimbra.cs.taglib.tag.filter;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.taglib.tag.ZimbraSimpleTag;
import com.zimbra.cs.zclient.ZFilterCondition.BodyOp;
import com.zimbra.cs.zclient.ZFilterCondition.ZBodyCondition;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;

public class BodyConditionTag extends ZimbraSimpleTag {

    private BodyOp mOp;
    private String mValue;

    public void setValue(String value) { mValue = value; }
    public void setOp(String op) throws ServiceException { mOp = BodyOp.fromString(op); }

    public void doTag() throws JspException {
        FilterRuleTag rule = (FilterRuleTag) findAncestorWithClass(this, FilterRuleTag.class);
        if (rule == null)
                throw new JspTagException("The bodyCondition tag must be used within a filterRule tag");
        rule.addCondition(new ZBodyCondition(mOp, mValue));
    }

}
