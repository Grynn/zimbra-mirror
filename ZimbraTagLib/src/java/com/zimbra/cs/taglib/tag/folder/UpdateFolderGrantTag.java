package com.zimbra.cs.taglib.tag.folder;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.taglib.bean.ZGrantBean;
import com.zimbra.cs.taglib.bean.ZMimePartBean;
import com.zimbra.cs.taglib.tag.ZimbraSimpleTag;
import com.zimbra.cs.zclient.ZEmailAddress;
import com.zimbra.cs.zclient.ZGrant;
import com.zimbra.cs.zclient.ZMailbox;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.PageContext;
import com.zimbra.cs.taglib.tag.i18n.I18nUtil;
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

    private String mFolderId;
    private String mFolderName;
    private String mGrantorId;
    private String mGrantorName;
    private String mUpdateGrantString;
    private String mVar;
    
    public void setFolderId(String id){
        this.mFolderId = id;
    }

    public void setVar(String var){
        this.mVar = var;
    }

    public void setUpdateGrantString(String grantString){
        this.mUpdateGrantString = grantString;
    }

    public void setFolderName(String folderName){
            this.mFolderName = folderName;
    }

    public void setGrantorId(String grantorId){
            this.mGrantorId = grantorId;
    }

    public void setGrantorName(String grantorName){
            this.mGrantorName = grantorName;
    }

    public void doTag() throws JspException, IOException {
        try {
            ZMailbox mbox = getMailbox();
            ArrayList <ZGrantBean> updateGrants = getGrantsFromString(this.mUpdateGrantString);
            ArrayList failures = new ArrayList();
            for (ZGrantBean grant : updateGrants) {
                try{
                    ZMailbox.ZActionResult result =
                        mbox.modifyFolderGrant(
                            mFolderId,
                            grant.getGranteeType(),
                            grant.getGranteeId(),
                            grant.getPermissions(),
                            null);
                } catch (ServiceException e){
                    failures.add(grant.getGranteeId());
                }
                try{
                    if (grant.getArgs().equalsIgnoreCase("true")) {
                        sendMail(mbox, mFolderId, mFolderName,
                                this.mGrantorName, this.mGrantorId, grant.getGranteeId());
                    }
                } catch (ServiceException e){
                    // eat the mta exceptions here, we probably need to use bulkmailer here, unclear whether that will
                    // respond in realtime or not.
                } catch (JspException e){
                    // eat the mta exceptions here, we probably need to use bulkmailer here, unclear whether that will
                    // respond in realtime or not.
                }
                getJspContext().setAttribute(this.mVar, failures, PageContext.REQUEST_SCOPE);
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

    public ZMailbox.ZSendMessageResponse sendMail(ZMailbox mbox, String folderId, String folderName,
                                                  String grantorName, String grantorId,
                                                  String granteeId)
            throws ServiceException, JspException {
            ZMailbox.ZOutgoingMessage m =
                    getOutgoingMessage(grantorId, grantorName,
                                       granteeId, folderId, folderName);

            ZMailbox.ZSendMessageResponse response = mbox.sendMessage(m, null, true);
            //Change this to Java API call to bulkmailer Java interface 
            return response;

    }

    private ZMailbox.ZOutgoingMessage getOutgoingMessage(String grantorId, String grantorName,
                                                         String granteeId, String linkId, String folderName )
            throws ServiceException, JspException {

        List<ZEmailAddress> addrs = new ArrayList<ZEmailAddress>();
        List<String> messages = null;
        List<ZMailbox.ZOutgoingMessage.AttachedMessagePart> attachments = null;
        ZMailbox.ZOutgoingMessage m = new ZMailbox.ZOutgoingMessage();

        String subject =
            I18nUtil.getLocalizedMessage((PageContext)getJspContext(),
                                              "calendarAddShareMailSubject", grantorName);

        String acceptShareLink = "/zimbra/y/calendar?action=acceptShare&np=1&nn=" +
                                 folderName + "&ng=" + grantorId + "&nl=" + linkId;
        String [] bodyArgs = {grantorName, folderName, acceptShareLink};
        String body =
                I18nUtil.getLocalizedMessage((PageContext)getJspContext(),
                            "calendarAddShareMailBody", bodyArgs);

        if (granteeId != null && granteeId.length() > 0)
            addrs.addAll(ZEmailAddress.parseAddresses(granteeId, ZEmailAddress.EMAIL_TYPE_TO));
        m.setAddresses(addrs);
        m.setSubject(subject);
        m.setMessagePart(new ZMailbox.ZOutgoingMessage.MessagePart(ZMimePartBean.CT_TEXT_HTML, body));
        m.setMessageIdsToAttach(messages);
        m.setMessagePartsToAttach(attachments);

        return m;

    }

}

