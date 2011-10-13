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

import com.zimbra.common.filter.Sieve;
import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.taglib.tag.ZimbraSimpleTag;
import com.zimbra.client.ZFilterCondition.HeaderOp;
import com.zimbra.client.ZFilterCondition.ZAddressCondition;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;

public class AddressConditionTag extends ZimbraSimpleTag {
    private String mHeaderName;
    private Sieve.AddressPart mPart;
    private HeaderOp mOp;
    private String mValue;

    public void setValue(String value) { mValue = value; }
    public void setName(String name) { mHeaderName = name; }
    public void setOp(String op) throws ServiceException { mOp = HeaderOp.fromString(op); }
    public void setPart(String part) throws ServiceException {mPart = Sieve.AddressPart.fromString(part); }

    public void doTag() throws JspException {
        FilterRuleTag rule = (FilterRuleTag) findAncestorWithClass(this, FilterRuleTag.class);
        if (rule == null)
            throw new JspTagException("The addressCondition tag must be used within a filterRule tag");
        rule.addCondition(new ZAddressCondition(mHeaderName, mPart, mOp, mValue));
    }

}
