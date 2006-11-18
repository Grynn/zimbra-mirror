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
package com.zimbra.cs.taglib.tag;

import com.zimbra.cs.service.ServiceException;
import com.zimbra.cs.zclient.ZMailbox;
import com.zimbra.cs.zclient.ZPrefs;
import com.zimbra.common.util.StringUtil;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.PageContext;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ModifyPrefsTag extends ZimbraSimpleTag {

    protected Map<String, Object> mAttrs = new HashMap<String,Object>();
    private String mVar;
    private ZPrefs mPrefs;

    public void setVar(String var) { mVar = var; }
    
    public void doTag() throws JspException, IOException {
        try {
            ZMailbox mailbox = getMailbox();
            mPrefs = mailbox.getAccountInfo(false).getPrefs();
            getJspBody().invoke(null);

            boolean update = !mAttrs.isEmpty();
            if (update)
                mailbox.modifyPrefs(mAttrs);
           getJspContext().setAttribute(mVar, update, PageContext.PAGE_SCOPE);
        } catch (ServiceException e) {
            throw new JspTagException(e);
        }
    }

    public void addPref(String name, String value) throws JspTagException {
        if (value == null) value = "";
        String currentValue = mPrefs.get(name);
        if (currentValue == null) currentValue = "";
        if (!value.equals(currentValue))
            StringUtil.addToMultiMap(mAttrs, name, value);
    }
}
