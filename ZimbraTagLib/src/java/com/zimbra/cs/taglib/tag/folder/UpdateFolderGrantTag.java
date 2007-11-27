package com.zimbra.cs.taglib.tag.folder;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.taglib.bean.ZGrantBean;
import com.zimbra.cs.taglib.tag.ZimbraSimpleTag;
import com.zimbra.cs.zclient.ZEmailAddress;
import com.zimbra.cs.zclient.ZGrant;
import com.zimbra.cs.zclient.ZMailbox;

import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.PageContext;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

public class UpdateFolderGrantTag extends ZimbraSimpleTag {

    private String mId;
    private String mUpdateGrantString;

    public void setId(String id){
        this.mId = id;
    }

    public void setUpdateGrantString(String grantString){
        this.mUpdateGrantString = grantString;
    }

    public void doTag() throws JspException, IOException {
        try {
            ZMailbox mbox = getMailbox();
            ArrayList <ZGrantBean> updateGrants = getGrantsFromString(this.mUpdateGrantString);
            for (ZGrantBean grant : updateGrants) {
                mbox.modifyFolderGrant(
                        mId,
                        grant.getGranteeType(),
                        grant.getGranteeId(),
                        grant.getPermissions(),
                        null);
                if (grant.getArgs().equalsIgnoreCase("true")) {
                    sendMail(mbox, grant.getGranteeId());
                }
            }
        } catch (ServiceException e) {
            throw new JspTagException(e);
        }
    }

    private ArrayList<ZGrantBean> getGrantsFromString (String grantsString) throws ServiceException {
        ArrayList<ZGrantBean> grants = new ArrayList<ZGrantBean>();
        if (grantsString.length() < 1){
            return grants;
        }
        String[] strGrants = grantsString.split(";");
        int x;
        for (x = strGrants.length - 1; x > -1; x--) {
            String[] grantArgs = strGrants[x].split(",");
            ZGrantBean grant = new ZGrantBean();
            grant.setGranteeId(grantArgs[0]);
            grant.setGranteeType(ZGrant.GranteeType.valueOf(grantArgs[1]));
            grant.setPermissions(grantArgs[2]);
            grant.setArgs(grantArgs[3]);
            grants.add(grant);
        }
        return grants;
    }

    public ZMailbox.ZSendMessageResponse sendMail(ZMailbox mbox, String id)
            throws IOException, JspTagException {
        try {
            ZMailbox.ZOutgoingMessage m = getOutgoingMessage(id);

            ZMailbox.ZSendMessageResponse response = mbox.sendMessage(m, null, true);
            return response;

        } catch (ServiceException e) {
            throw new JspTagException(e.getMessage(), e);
        }
    }

    private ZMailbox.ZOutgoingMessage getOutgoingMessage(String id) throws ServiceException {

        List<ZEmailAddress> addrs = new ArrayList<ZEmailAddress>();

        if (id != null && id.length() > 0)
            addrs.addAll(ZEmailAddress.parseAddresses(id, ZEmailAddress.EMAIL_TYPE_TO));

        List<String> messages = null;

        List<ZMailbox.ZOutgoingMessage.AttachedMessagePart> attachments = null;

        ZMailbox.ZOutgoingMessage m = new ZMailbox.ZOutgoingMessage();

        m.setAddresses(addrs);

        m.setSubject("Hello Foo");

        m.setMessagePart(new ZMailbox.ZOutgoingMessage.MessagePart("text/html", ""));

        m.setMessageIdsToAttach(messages);

        m.setMessagePartsToAttach(attachments);

        return m;

    }

}

