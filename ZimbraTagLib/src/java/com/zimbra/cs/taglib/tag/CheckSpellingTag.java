/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2007, 2008, 2009, 2010 Zimbra, Inc.
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
package com.zimbra.cs.taglib.tag;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.zclient.ZMailbox;

import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import java.io.IOException;
import java.util.List;

public class CheckSpellingTag extends ZimbraSimpleTag {

    private String mText;

    public void setText(String text) { this.mText = text; }

    public void doTag() throws JspException, IOException {
        JspContext jctxt = getJspContext();
        try {
            ZMailbox mbox = getMailbox();
			String trimmed = mText.trim().replaceAll("\\u00A0"," ").replaceAll("\\s\\s+"," ");
			ZMailbox.CheckSpellingResult result = mbox.checkSpelling(trimmed);
			JspWriter out = jctxt.getOut();
			out.print("{\"available\":");
			out.print(result.getIsAvailable() ? "true" : "false");
			out.println(",\"data\":[");
			boolean firstMisspelling = true;
			for (ZMailbox.Misspelling misspelling : result.getMisspellings()) {
				if (!firstMisspelling) {
					out.print(',');
				}
				firstMisspelling = false;
				out.print("{\"word\":\"");
				out.print(misspelling.getWord());
				out.print("\",\"suggestions\":[");
				String[] suggestions = misspelling.getSuggestions();
				for (int i = 0, count = suggestions.length; i < count && i < 5; i++) {
					if (i > 0) {
						out.print(',');
					}
					out.print('"');
					out.print(suggestions[i]);
					out.print('"');
				}
				out.println("]}");
			}
			out.println("]}");
		} catch (ServiceException e) {
            throw new JspTagException(e);
        }
    }
}
