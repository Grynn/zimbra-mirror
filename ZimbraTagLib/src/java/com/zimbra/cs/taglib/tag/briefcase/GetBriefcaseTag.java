/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2009, 2010 Zimbra, Inc.
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
package com.zimbra.cs.taglib.tag.briefcase;

import com.zimbra.cs.taglib.bean.*;
import com.zimbra.cs.taglib.tag.ZimbraSimpleTag;
import com.zimbra.cs.zclient.*;

import com.zimbra.common.service.ServiceException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.JspTagException;
import java.io.IOException;

public class GetBriefcaseTag extends ZimbraSimpleTag {

    private String mVarSearch;
    private String mId;
    private String mVarFolder;
    private ZMailboxBean mMailbox;

    public void setId(String id) { this.mId = id; }
    public void setVarSearch(String varSearch) { this.mVarSearch = varSearch; }
    public void setVarFolder(String varFolder) { this.mVarFolder = varFolder; }
    public void setBox(ZMailboxBean mailbox) { this.mMailbox = mailbox; }

    public void doTag() throws JspException, IOException {
        JspContext jctxt = getJspContext();
        try {
            ZMailbox mbox = mMailbox != null ? mMailbox.getMailbox() : getMailbox();
			ZFolder briefcaseFolder = mbox.getFolderById(this.mId);
            ZSearchContext searchCnt = briefcaseFolder.getSearchContext();
            if (searchCnt.getResult() == null) {
                searchCnt.getNextHit();
            }
            jctxt.setAttribute(mVarSearch, new ZSearchResultBean(searchCnt.getResult(), searchCnt.getParams()), PageContext.PAGE_SCOPE);
            jctxt.setAttribute(mVarFolder, new ZFolderBean(briefcaseFolder), PageContext.PAGE_SCOPE);
        } catch (ServiceException e) {
            throw new JspTagException(e.getMessage(), e);
        }
    }

}
