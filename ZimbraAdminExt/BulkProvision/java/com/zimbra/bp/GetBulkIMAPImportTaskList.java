/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2010, 2011, 2013 Zimbra Software, LLC.
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
package com.zimbra.bp;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Queue;

import com.zimbra.bp.BulkIMAPImportTaskManager.taskKeys;
import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.AdminConstants;
import com.zimbra.common.soap.AdminExtConstants;
import com.zimbra.common.soap.Element;
import com.zimbra.cs.account.Account;
import com.zimbra.cs.account.Provisioning;
import com.zimbra.cs.account.AttributeManager.IDNType;
import com.zimbra.cs.account.accesscontrol.AccessControlUtil;
import com.zimbra.cs.service.admin.AdminDocumentHandler;
import com.zimbra.cs.service.admin.ToXML;
import com.zimbra.soap.DocumentHandler;
import com.zimbra.soap.ZimbraSoapContext;

public class GetBulkIMAPImportTaskList extends AdminDocumentHandler  {
    @Override
    public Element handle(Element request, Map<String, Object> context)
            throws ServiceException {
        ZimbraSoapContext zsc = getZimbraSoapContext(context);
        Account authedAcct = DocumentHandler.getAuthenticatedAccount(zsc);
        Element response = zsc.createElement(AdminExtConstants.GET_BULK_IMAP_IMPORT_TASKLIST_RESPONSE);
        HashMap<String, Queue<HashMap<taskKeys, String>>> importQueues = BulkIMAPImportTaskManager.getImportQueues();
        if(AccessControlUtil.isGlobalAdmin(authedAcct, true)) {
            synchronized(importQueues) {
               Iterator<String> keyIter = importQueues.keySet().iterator();
               while(keyIter.hasNext()) {
                   encodeTask(response,keyIter.next());
               }
            }
        } else {
            String adminID = zsc.getAuthtokenAccountId();
            if(importQueues.containsKey(adminID)) {
                encodeTask(response,adminID);
            }
        }
        return response;
    }
    
    private void encodeTask (Element response, String adminID) throws ServiceException {
        Account acct = Provisioning.getInstance().getAccountById(adminID);
        Queue<HashMap<taskKeys, String>> fq =  BulkIMAPImportTaskManager.getFinishedQueue(adminID);
        Queue<HashMap<taskKeys, String>> eq =  BulkIMAPImportTaskManager.getFailedQueue(adminID);
        int numFinished = 0;
        if(fq!=null) {
            synchronized(fq) {
                numFinished = fq.size();
            }
        }
        int numFailed = 0;
        if(eq!=null) {
            synchronized(eq) {
                numFailed = eq.size();
            }
        }        
        int numTotal = 0;
        Queue<HashMap<taskKeys, String>> rq =  BulkIMAPImportTaskManager.getRunningQueue(adminID);
        if(rq!=null) {
            synchronized(rq) {
                numTotal = rq.size();
            }
        } 
        Element elTask = response.addElement(AdminExtConstants.E_Task);
        ToXML.encodeAttr(elTask,AdminExtConstants.A_owner,acct.getName(),AdminConstants.E_A,AdminConstants.A_N,IDNType.none, true);
        ToXML.encodeAttr(elTask,AdminExtConstants.A_totalTasks,Integer.toString(numTotal),AdminConstants.E_A,AdminConstants.A_N,IDNType.none, true);
        ToXML.encodeAttr(elTask,AdminExtConstants.A_finishedTasks,Integer.toString(numFinished),AdminConstants.E_A,AdminConstants.A_N,IDNType.none, true);
        ToXML.encodeAttr(elTask,AdminExtConstants.A_failedTasks,Integer.toString(numFailed),AdminConstants.E_A,AdminConstants.A_N,IDNType.none, true);
    }
}
