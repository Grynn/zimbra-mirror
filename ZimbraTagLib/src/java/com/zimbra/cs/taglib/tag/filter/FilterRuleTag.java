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
 * Portions created by Zimbra are Copyright (C) 2006 Zimbra, Inc.
 * All Rights Reserved.
 *
 * Contributor(s):
 *
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.cs.taglib.tag.filter;

import com.zimbra.cs.taglib.tag.ZimbraSimpleTag;
import com.zimbra.cs.zclient.ZFilterAction;
import com.zimbra.cs.zclient.ZFilterCondition;
import com.zimbra.cs.zclient.ZFilterRule;

import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.PageContext;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FilterRuleTag extends ZimbraSimpleTag {

    protected boolean mActive;
    protected boolean mAll;
    protected String mName;
    protected String mVar;
    protected List<ZFilterCondition> mConditions = new ArrayList<ZFilterCondition>();
    protected List<ZFilterAction> mActions = new ArrayList<ZFilterAction>();

    public void setActive(boolean active) { mActive = active; }

    public void setAllconditions(boolean all) { mAll = all; }

    public void setName(String name) { mName = name; }

    public void setVar(String var) {  mVar = var; }

    public void addCondition(ZFilterCondition condition) throws JspTagException {
        mConditions.add(condition);
    }

    public void addAction(ZFilterAction action) throws JspTagException {
        mActions.add(action);
    }

    public void doTag() throws JspException, IOException {
        getJspBody().invoke(null);
        JspContext jctxt = getJspContext();
        ZFilterRule rule = new ZFilterRule(mName, mActive, mAll, mConditions, mActions);
        jctxt.setAttribute(mVar, rule,  PageContext.PAGE_SCOPE);
    }
}
