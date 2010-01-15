/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2008, 2009, 2010 Zimbra, Inc.
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
package com.zimbra.bp;

import com.zimbra.cs.account.accesscontrol.AdminRight;
import com.zimbra.cs.service.admin.AdminDocumentHandler;
import com.zimbra.cs.service.admin.AdminRightCheckPoint;
import com.zimbra.common.soap.Element;
import com.zimbra.common.service.ServiceException;
import com.zimbra.common.util.ZimbraLog;
import com.zimbra.soap.ZimbraSoapContext;

import java.util.List;
import java.util.Map;
import java.util.Hashtable;

/**
 * Created by IntelliJ IDEA.
 * User: ccao
 * Date: Oct 1, 2008
 * Time: 12:45:51 PM
 * To change this template use File | Settings | File Templates.
 */
public class UpdateBulkProvisionStatus  extends AdminDocumentHandler {

    public static final String A_name = "name" ;
    public static final String A_status = "status" ;

    public boolean domainAuthSufficient(Map context) {
        return true;
    }

	public Element handle(Element request, Map<String, Object> context) throws ServiceException {
        String aid = request.getElement("aid").getTextTrim()  ;
        Hashtable<String, String []> ht = BulkProvisionStatus.getBpStatus(aid) ;
        for (Element e : request.listElements("account")) {
            String name = e.getElement(A_name).getTextTrim() ;
            String status = e.getElement(A_status).getTextTrim() ;
            if (status != null && status.length() > 0){
                ZimbraLog.extensions.debug("Update the privision status for account " + name) ;
                String [] entry = ht.get(name) ;
                if ((entry != null) && (entry.length > 0))
                    entry [BulkProvisionStatus.INDEX_STATUS] = status ;
            }
        }

        ZimbraSoapContext lc = getZimbraSoapContext(context);
        Element response = lc.createElement(ZimbraBulkProvisionService.UPDATE_BULK_PROVISION_STATUS_RESPONSE);

        return response;
	}
	
    @Override
    public void docRights(List<AdminRight> relatedRights, List<String> notes) {
        notes.add(AdminRightCheckPoint.Notes.ALLOW_ALL_ADMINS);
    }
}
