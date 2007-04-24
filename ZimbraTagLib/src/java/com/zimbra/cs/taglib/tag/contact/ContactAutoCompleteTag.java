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
import java.util.Collections;
import java.util.Comparator;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;

public class ContactAutoCompleteTag extends ZimbraSimpleTag {

    private String mVar;
    private String mQuery;
    private int mLimit;
    private boolean mJSON;

    public void setVar(String var) { this.mVar = var; }
    public void setQuery(String query) { this.mQuery = query; }
    public void setLimit(int limit) { this.mLimit = limit; }
    public void setJson(boolean json) { this.mJSON = json; }

    public void doTag() throws JspException, IOException {
        JspContext jctxt = getJspContext();
        try {
            ZMailbox mbox = getMailbox();

            Set<String> matches = new HashSet<String>();
            List<AContact> hits = new ArrayList<AContact>();
            for (ZContact c : mbox.autoComplete(mQuery, mLimit)) {
                AContact ac = new AContact(c, mQuery);
                if (ac.email != null && !matches.contains(ac.match)) {
                    matches.add(ac.match);
                    hits.add(ac);
                }
            }
            Collections.sort(hits, new AContactComparator());
            //jctxt.setAttribute(mVar, hits,  PageContext.PAGE_SCOPE);
            if (mJSON) {
                boolean firstContact = true;
                JspWriter out = jctxt.getOut();
                out.println("{\"Result\":[");
                for (AContact contact : hits) {
                    if (!firstContact) out.println(",");
                    else firstContact = false;
                    contact.toJSON(out);
                }
                out.println("]}");
            }
        } catch (ServiceException e) {
            throw new JspTagException(e);
        }
    }

    static class AContact {
        public String match;
        public String email;
        public String first;
        public String last;

        public AContact(ZContact c, String query) {
            Map<String,String> attrs = c.getAttrs();
            first = attrs.get(Contact.A_firstName);
            last = attrs.get(Contact.A_lastName);

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

            if (email == null) return;

            StringBuilder personal = new StringBuilder();
            if (first != null) personal.append(first);
            if (last != null) {
                if (personal.length() > 0) personal.append(' ');
                personal.append(last);
            }

            ZEmailAddress addr = new ZEmailAddress(email, null, personal.toString(), ZEmailAddress.EMAIL_TYPE_TO);
            match = addr.getFullAddressQuoted();
        }

        void toJSON(JspWriter out) throws IOException {
            boolean firstField = true;
            out.print("{");
            firstField = jsonNameValue(out, "m", match, firstField);
            firstField = jsonNameValue(out, "e", email, firstField);
            firstField = jsonNameValue(out, "f", first, firstField);
            jsonNameValue(out, "l", last, firstField);
            out.println("}");
        }

        private boolean jsonNameValue(JspWriter out, String name, String value, boolean first) throws IOException {
            if (value == null) return first;
            if (!first) out.print(',');
            out.print(StringUtil.jsEncodeKey(name));
            out.print(":\"");
            out.print(StringUtil.jsEncode(value));
            out.print('"');
            return false;
        }
    }

    static class AContactComparator implements Comparator<AContact> {
        public int compare(AContact a, AContact b) {
            return a.match.compareToIgnoreCase(b.match);
        }
    }
}
