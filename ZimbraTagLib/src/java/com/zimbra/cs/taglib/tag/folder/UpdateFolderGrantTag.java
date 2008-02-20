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
    private String mRevokeGrantString;
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

    public void setRevokeGrantString(String grantString){
        this.mRevokeGrantString = grantString;
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
                    ZMailbox.ZActionResult updateResult =
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
                        sendGrantShareMail(mbox, grant.getGranteeId());
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
            for (String shareId : mRevokeGrantString.split(";")){
                if (shareId.length() > 0){
                    ZMailbox.ZActionResult updateResult =
                        mbox.modifyFolderRevokeGrant(mFolderId, 
                                                     shareId.equals("pub") ? 
                                                     ZGrant.GUID_PUBLIC : shareId);
                }
            }
        } catch (ServiceException e) {
            throw new JspTagException(e);
        }
    }

    public ZMailbox.ZSendMessageResponse sendGrantShareMail(ZMailbox mbox, String granteeId)
            throws ServiceException, JspException {

        return sendMail(mbox, granteeId, "calendarAddShareMailSubject", "calendarAddShareMailBody");
    }

    public ZMailbox.ZSendMessageResponse sendRevokeShareMail(ZMailbox mbox, String granteeId)
            throws ServiceException, JspException {

        return sendMail(mbox, granteeId, 
                        "calendarRevokeShareMailSubject", "calendarRevokeShareMailBody");
        
    }

    private ZMailbox.ZSendMessageResponse sendMail(ZMailbox mbox, String granteeId, 
                                                   String subjectKey, String bodyKey)
            throws ServiceException, JspException {
        String subject =
            I18nUtil.getLocalizedMessage((PageContext)getJspContext(),
                                         subjectKey, mGrantorName);
        String acceptShareLink = "/zimbra/y/mcalendars?action=acceptShare" +
                                 "&oc=" + mFolderName +
                                 "&oe=" + mbox.getAccountInfo(false).getName() +
                                 "&od=" + mFolderId;
        String [] bodyArgs = {mGrantorName, mFolderName, acceptShareLink};
        String body =
            I18nUtil.getLocalizedMessage((PageContext)getJspContext(),
                                         bodyKey, bodyArgs);
        ZMailbox.ZOutgoingMessage m =
            getOutgoingMessage(granteeId, subject, body);
        ZMailbox.ZSendMessageResponse response = mbox.sendMessage(m, null, true);

        return response;

    }

    private ZMailbox.ZOutgoingMessage getOutgoingMessage(String granteeId, String subject, String body )
            throws ServiceException, JspException {

        List<ZEmailAddress> addrs = new ArrayList<ZEmailAddress>();
        List<String> messages = null;
        List<ZMailbox.ZOutgoingMessage.AttachedMessagePart> attachments = null;
        ZMailbox.ZOutgoingMessage m = new ZMailbox.ZOutgoingMessage();


        if (granteeId != null && granteeId.length() > 0)
            addrs.addAll(ZEmailAddress.parseAddresses(granteeId, ZEmailAddress.EMAIL_TYPE_TO));
        m.setAddresses(addrs);
        m.setSubject(subject);
        m.setMessagePart(new ZMailbox.ZOutgoingMessage.MessagePart(ZMimePartBean.CT_TEXT_HTML, body));
        m.setMessageIdsToAttach(messages);
        m.setMessagePartsToAttach(attachments);

        return m;

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
            grant.setGranteeId(grantArgs[0].equals("pub") ? ZGrant.GUID_PUBLIC : grantArgs[0]);
            grant.setGranteeType(ZGrant.GranteeType.valueOf(grantArgs[1]));
            grant.setPermissions(grantArgs[2]);
            grant.setArgs(grantArgs[3]);
            grants.add(grant);
        }
        return grants;
    }


}

