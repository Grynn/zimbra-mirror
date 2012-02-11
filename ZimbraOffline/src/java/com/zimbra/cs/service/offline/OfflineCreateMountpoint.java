/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2008, 2009, 2010, 2011 Zimbra, Inc.
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
package com.zimbra.cs.service.offline;

import java.util.Map;

import com.zimbra.common.mailbox.Color;
import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.Element;
import com.zimbra.common.soap.MailConstants;
import com.zimbra.cs.account.offline.OfflineProvisioning;
import com.zimbra.cs.mailbox.ChangeTrackingMailbox.TracelessContext;
import com.zimbra.cs.mailbox.Flag;
import com.zimbra.cs.mailbox.MailItem;
import com.zimbra.cs.mailbox.MailServiceException;
import com.zimbra.cs.mailbox.Mailbox;
import com.zimbra.cs.mailbox.OfflineServiceException;
import com.zimbra.cs.mailbox.ZcsMailbox;
import com.zimbra.cs.redolog.op.CreateMountpoint;
import com.zimbra.soap.ZimbraSoapContext;

public class OfflineCreateMountpoint extends OfflineServiceProxy {

    public OfflineCreateMountpoint() {
        super("create mountpoint", false, false);
    }

    @Override
    public Element handle(Element request, Map<String, Object> context) throws ServiceException {
        ZimbraSoapContext ctxt = getZimbraSoapContext(context);
        Mailbox mbox = getRequestedMailbox(ctxt);
        if (!(mbox instanceof ZcsMailbox)) {
            throw OfflineServiceException.MISCONFIGURED("incorrect mailbox class: " + mbox.getClass().getSimpleName());
        }
        Element t = request.getElement(MailConstants.E_MOUNT);
        t.addAttribute(MailConstants.A_FETCH_IF_EXISTS, true);
        Element response = super.handle(request, context);

        Element eMount = response.getElement(MailConstants.E_MOUNT);
        int parentId = (int) eMount.getAttributeLong(MailConstants.A_FOLDER);
        int id = (int) eMount.getAttributeLong(MailConstants.A_ID);
        String uuid = eMount.getAttribute(MailConstants.A_UUID);
        String name = (id == Mailbox.ID_FOLDER_ROOT) ? "ROOT" : MailItem.normalizeItemName(eMount.getAttribute(MailConstants.A_NAME));
        int flags = Flag.toBitmask(eMount.getAttribute(MailConstants.A_FLAGS, null));
        byte color = (byte) eMount.getAttributeLong(MailConstants.A_COLOR, MailItem.DEFAULT_COLOR);
        MailItem.Type view = MailItem.Type.of(eMount.getAttribute(MailConstants.A_DEFAULT_VIEW, null));
        String ownerId = eMount.getAttribute(MailConstants.A_ZIMBRA_ID);
        String ownerName = eMount.getAttribute(MailConstants.A_OWNER_NAME);
        int remoteId = (int) eMount.getAttributeLong(MailConstants.A_REMOTE_ID);
        int mod_content = (int) eMount.getAttributeLong(MailConstants.A_REVISION, -1);
        boolean reminderEnabled = eMount.getAttributeBool(MailConstants.A_REMINDER, false);

        OfflineProvisioning.getOfflineInstance().createMountpointAccount(ownerName, ownerId, ((ZcsMailbox)mbox).getOfflineAccount());
        CreateMountpoint redo = new CreateMountpoint(mbox.getId(), parentId, name, ownerId, remoteId, null, view, flags,
                new Color(color), reminderEnabled);
        redo.setIdAndUuid(id, uuid);
        redo.setChangeId(mod_content);
        try {
            mbox.createMountpoint(new TracelessContext(redo), parentId, name, ownerId, remoteId, null, view, flags, color, reminderEnabled);
        } catch (ServiceException e) {
            if (e.getCode() != MailServiceException.ALREADY_EXISTS)
                throw e;
        }

        return response;
    }
}
