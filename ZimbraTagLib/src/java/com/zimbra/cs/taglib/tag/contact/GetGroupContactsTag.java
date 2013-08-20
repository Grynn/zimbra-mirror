/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2012, 2013 Zimbra Software, LLC.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.4 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.cs.taglib.tag.contact;

import com.zimbra.client.ZContact;
import com.zimbra.client.ZMailbox;
import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.taglib.tag.ZimbraSimpleTag;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import java.io.IOException;
import java.util.Map;

public class GetGroupContactsTag extends ZimbraSimpleTag {

    private String var;
    private String id;
    private boolean json;

    public void setVar(String var) {
        this.var = var;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setJson(boolean json) {
        this.json = json;
    }

    public void doTag() throws JspException, IOException {
        JspContext jctxt = getJspContext();
        try {
            ZMailbox mbox = getMailbox();
            ZContact group = mbox.getContact(id);
            if (json) {
                JSONArray jsonArray = new JSONArray();
                Map<String, ZContact> members = group.getMembers();
                String addr = null;
                for (ZContact contact : members.values()) {
                    if(!contact.isTypeI()) {
                        Map<String, String> attrs = contact.getAttrs();
                        addr = attrs.get("email");
                    }
                    else {
                        addr = contact.getId();
                    }
                    if (addr != null) {
                        jsonArray.put(addr);
                    }
                }
                JSONObject top = new JSONObject();
                top.put("Result", jsonArray);
                top.write(jctxt.getOut());
            }
        } catch (JSONException e) {
            throw new JspTagException(e);
        } catch (ServiceException e) {
            throw new JspTagException(e);
        }
    }
}
