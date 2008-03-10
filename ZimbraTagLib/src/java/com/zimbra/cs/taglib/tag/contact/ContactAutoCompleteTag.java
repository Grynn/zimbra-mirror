/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2007 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Yahoo! Public License
 * Version 1.0 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * 
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.cs.taglib.tag.contact;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.util.StringUtil;
import com.zimbra.cs.mailbox.Contact;
import com.zimbra.cs.taglib.tag.ZimbraSimpleTag;
import com.zimbra.cs.zclient.ZContact;
import com.zimbra.cs.zclient.ZEmailAddress;
import com.zimbra.cs.zclient.ZMailbox;

import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ContactAutoCompleteTag extends ZimbraSimpleTag {

    private String mVar;
    private String mQuery;
    private int mLimit;
    private boolean mJSON;

    public void setVar(String var) { this.mVar = var; }
    public void setQuery(String query) { this.mQuery = query.toLowerCase(); }
    public void setLimit(int limit) { this.mLimit = limit; }
    public void setJson(boolean json) { this.mJSON = json; }

    public void doTag() throws JspException, IOException {
        JspContext jctxt = getJspContext();
        try {
            ZMailbox mbox = getMailbox();

            Set<String> matches = new HashSet<String>();
            List<AContact> hits = new ArrayList<AContact>();
            for (ZContact c : mbox.autoComplete(mQuery, mLimit)) {
                AContact.add(hits, c, mQuery, matches);
                /*
                AContact ac = new AContact(c, mQuery);
                if (ac.email != null && !matches.contains(ac.match)) {
                    matches.add(ac.match);
                    hits.add(ac);
                }
                */
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

    static boolean sameDomain(String a, String b) {
        if (a == null || b == null) return false;
        int ai = a.indexOf('@');
        int bi = b.indexOf('@');
        if (ai == -1 || bi == -1) return false;
        return a.substring(ai).equalsIgnoreCase(b.substring(bi));
    }

    static class AContact {
        public String match;
        public String email;
        public String first;
        public String last;
        public boolean gal;
        public boolean dlist;

        public static void add(List<AContact> contacts, ZContact c, String query, Set<String> addrs) {

            Map<String,String> attrs = c.getAttrs();
            String first = attrs.get(Contact.A_firstName);
            String last = attrs.get(Contact.A_lastName);
            String e = attrs.get(Contact.A_email);
            String e2 = attrs.get(Contact.A_email2);
            String e3 = attrs.get(Contact.A_email3);
            String nickname = attrs.get(Contact.A_nickname);
            String dlist = attrs.get(Contact.A_dlist);

            if (nickname != null && dlist != null) {
                StringBuilder sb = new StringBuilder();
                try {
                    for (ZEmailAddress addr : c.getGroupMembers()) {
                        if (sb.length() > 0) sb.append(", ");
                        sb.append(addr.getFullAddressQuoted());
                    }
                } catch (ServiceException e1) {
                    sb.append(dlist);
                }
                contacts.add(new AContact(nickname, sb.toString()));
                return;
            }

            if (first == null && last == null && c.isGalContact()) {
                first = attrs.get(Contact.A_fullName);
                if (first != null) {
                    int i = first.lastIndexOf(' ');
                    if (i != -1) {
                        last = first.substring(i+1);
                        first = first.substring(0,i);
                    }
                }
            }
            if (addrs.contains(e)) e = null;
            if (addrs.contains(e2)) e2 = null;
            if (addrs.contains(e3)) e3 = null;

            if (e == null && e2 == null && e3 == null) return;

            boolean fs = first != null && first.toLowerCase().startsWith(query);
            boolean ls = !fs && last != null && last.toLowerCase().startsWith(query);

            if (fs || ls || query.indexOf(' ') != -1) {
                if (e != null) {
                    contacts.add(new AContact(first, last, e, c.isGalContact()));
                    addrs.add(e);
                }
                if (e2 != null && (e == null || !sameDomain(e, e2))) {
                    contacts.add(new AContact(first, last, e2, c.isGalContact()));
                    addrs.add(e2);
                }
                if (e3 != null && ((e == null && e2 == null) || !sameDomain(e, e3) && !sameDomain(e2, e3))) {
                    contacts.add(new AContact(first, last, e3, c.isGalContact()));
                    addrs.add(e3);
                }
            } else {
                boolean e1match = e != null && e.toLowerCase().startsWith(query);
                boolean e2match = e2 != null && e2.toLowerCase().startsWith(query);
                boolean e3match = e3 != null && e3.toLowerCase().startsWith(query);

                if (e1match) {
                    contacts.add(new AContact(first, last, e, c.isGalContact()));
                    addrs.add(e);
                }

                if (e2match && (!e1match || !sameDomain(e, e2))) {
                    contacts.add(new AContact(first, last, e2, c.isGalContact()));
                    addrs.add(e2);
                }

                if (e3match && (!e1match || !sameDomain(e, e3)) && (!e2match || !sameDomain(e2, e3))) {
                    contacts.add(new AContact(first, last, e3, c.isGalContact()));
                    addrs.add(e3);
                }
            }
        }

        AContact(String nickname, String members) {
            first = "";
            last = "";
            email = nickname;
            match = members;
            dlist = true;
        }

        public String getSortField() { return dlist ? email : (match.charAt(0) == '"') ? match.substring(1) : match; }

        AContact(String f, String l, String e, boolean isgal) {
            first = f;
            last = l;
            email = e;
            gal = isgal;
            StringBuilder personal = new StringBuilder();
            if (first != null) personal.append(first);
            if (last != null) {
                if (personal.length() > 0) personal.append(' ');
                personal.append(last);
            }
            ZEmailAddress addr = new ZEmailAddress(e, null, personal.toString(), ZEmailAddress.EMAIL_TYPE_TO);
            match = addr.getFullAddressQuoted();
        }

        void toJSON(JspWriter out) throws IOException {
            boolean firstField = true;
            out.print("{");
            firstField = jsonNameValue(out, "m", match, firstField);
            firstField = jsonNameValue(out, "e", email, firstField);
            firstField = jsonNameValue(out, "f", first, firstField);
            firstField = jsonNameValue(out, "l", last, firstField);
            firstField = jsonNameValue(out, "t", gal ? "g": dlist ? "dl" : "c", firstField);
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
            if (a.gal && !b.gal) return 1;
            else if (!a.gal && b.gal) return -1;
            else return a.getSortField().compareToIgnoreCase(b.getSortField());
        }
    }
}
