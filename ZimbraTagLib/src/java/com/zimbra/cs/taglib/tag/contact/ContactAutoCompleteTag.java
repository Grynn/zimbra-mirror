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
import com.zimbra.cs.taglib.tag.ZimbraSimpleTag;
import com.zimbra.cs.zclient.ZAutoCompleteMatch;
import com.zimbra.cs.zclient.ZMailbox;

import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

            List<ZAutoCompleteMatch> matches = mbox.autoComplete(mQuery, mLimit);
            Collections.sort(matches, new ZAutoCompleteMatch.MatchComparator());
            //jctxt.setAttribute(mVar, hits,  PageContext.PAGE_SCOPE);
            if (mJSON)
            	toJSON(jctxt.getOut(), matches);
        } catch (JSONException e) {
            throw new JspTagException(e);
        } catch (ServiceException e) {
            throw new JspTagException(e);
        }
    }
    public void toJSON(JspWriter out, Collection<ZAutoCompleteMatch> matches) throws JSONException, IOException {
    	JSONArray jsonArray = new JSONArray();
    	for (ZAutoCompleteMatch match : matches) {
    		jsonArray.put(match.toZJSONObject().getJSONObject());
    	}
    	JSONObject top = new JSONObject();
    	top.put("Result", jsonArray);
    	top.write(out);
    }
}
