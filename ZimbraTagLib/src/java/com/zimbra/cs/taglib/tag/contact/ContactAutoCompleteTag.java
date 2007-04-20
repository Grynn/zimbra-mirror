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
package com.zimbra.cs.taglib.tag.contact;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.util.StringUtil;
import com.zimbra.cs.taglib.tag.ZimbraSimpleTag;
import com.zimbra.cs.zclient.ZContact;
import com.zimbra.cs.zclient.ZMailbox;
import com.zimbra.cs.zclient.ZEmailAddress;
import com.zimbra.cs.mailbox.Contact;

import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class ContactAutoCompleteTag extends ZimbraSimpleTag {

    private String mVar;
    private String mQuery;
    private int mLimit;
    private boolean mJSON;

    public void setVar(String var) { this.mVar = var; }
    public void setQuery(String query) { this.mQuery = query; }
    public void setLimit(int limit) { this.mLimit = limit; }
    public void setJson(boolean json) { this.mJSON = json; }

    private boolean jsonNameValue(JspWriter out, String name, String value, boolean first) throws IOException {
        if (value == null) return first;
        if (!first) out.print(',');
        out.print(StringUtil.jsEncodeKey(name));
        out.print(":\"");
        out.print(StringUtil.jsEncode(value));
        out.print('"');
        return false;
    }

    private boolean jsonContact(JspWriter out, ZContact contact, String query, boolean firstContact) throws IOException {
        boolean firstField = true;
        Map<String,String> attrs = contact.getAttrs();

        String f = attrs.get(Contact.A_firstName);
        String l = attrs.get(Contact.A_lastName);

        String email = null;

        String e = attrs.get(Contact.A_email);
        if (e != null && e.toLowerCase().startsWith(query)) {
            email = e;
        }

        String e2 = attrs.get(Contact.A_email2);
        if (email == null && e2 != null && e2.toLowerCase().startsWith(query)) {
            email = e2;
        }

        String e3 = attrs.get(Contact.A_email3);
        if (email == null && e3 != null && e3.toLowerCase().startsWith(query)) {
            email = e3;
        }

        if (email == null) email = e != null ? e : e2 != null ? e2 : e3;

        if (email == null) return firstContact;

        StringBuilder personal = new StringBuilder();
        if (f != null) personal.append(f);
        if (l != null) {
            if (personal.length() > 0) personal.append(' ');
            personal.append(l);
        }

        ZEmailAddress addr = new ZEmailAddress(email, null, personal.toString(), ZEmailAddress.EMAIL_TYPE_TO);
        
        out.print("{");
        firstField = jsonNameValue(out, "m", addr.getFullAddressQuoted(), firstField);
        firstField = jsonNameValue(out, "e", email, firstField);
        firstField = jsonNameValue(out, "f", f, firstField);
        firstField = jsonNameValue(out, "l", l, firstField);
        out.println("}");

        return false;
    }

    public void doTag() throws JspException, IOException {
        JspContext jctxt = getJspContext();
        try {
            ZMailbox mbox = getMailbox();
            List<ZContact> hits = mbox.autoComplete(mQuery, mLimit);
            //jctxt.setAttribute(mVar, hits,  PageContext.PAGE_SCOPE);
            if (mJSON) {
                boolean firstContact = true;
                JspWriter out = jctxt.getOut();
                out.println("{\"Result\":[");
                for (ZContact contact : hits) {
                    if (!firstContact) out.println(',');
                    firstContact = jsonContact(out, contact, mQuery, firstContact);
                }
                out.println("]}");
            }
        } catch (ServiceException e) {
            throw new JspTagException(e);
        }
    }
}
