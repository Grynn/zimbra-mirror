/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2008 Zimbra, Inc.
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
package com.zimbra.cs.service.offline;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.account.Account;
import com.zimbra.cs.account.Provisioning;
import com.zimbra.soap.ZimbraSoapContext;

public class OfflineDocumentHandlers {
	static Account getTargetAccount(ZimbraSoapContext zsc) throws ServiceException {
		String acctId = zsc.getRequestedAccountId();
        Provisioning prov = Provisioning.getInstance();
        return prov.get(Provisioning.AccountBy.id, acctId);
	}
	static class DiffDocument extends com.zimbra.cs.service.wiki.DiffDocument {
		protected String getAuthor(ZimbraSoapContext zsc) throws ServiceException {
			return getTargetAccount(zsc).getName();
		}
	}
	static class ListDocumentRevisions extends com.zimbra.cs.service.wiki.ListDocumentRevisions {
		protected String getAuthor(ZimbraSoapContext zsc) throws ServiceException {
			return getTargetAccount(zsc).getName();
		}
	}
	static class GetWiki extends com.zimbra.cs.service.wiki.GetWiki {
		protected String getAuthor(ZimbraSoapContext zsc) throws ServiceException {
			return getTargetAccount(zsc).getName();
		}
	}
	static class SaveDocument extends com.zimbra.cs.service.wiki.SaveDocument {
		protected String getAuthor(ZimbraSoapContext zsc) throws ServiceException {
			return getTargetAccount(zsc).getName();
		}
	}
	static class SaveWiki extends com.zimbra.cs.service.wiki.SaveWiki {
		protected String getAuthor(ZimbraSoapContext zsc) throws ServiceException {
			return getTargetAccount(zsc).getName();
		}
	}
	static class WikiAction extends com.zimbra.cs.service.wiki.WikiAction {
		protected String getAuthor(ZimbraSoapContext zsc) throws ServiceException {
			return getTargetAccount(zsc).getName();
		}
	}
}
