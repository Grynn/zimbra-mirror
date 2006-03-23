/*
 * ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1
 * 
 * The contents of this file are subject to the Mozilla Public License
 * Version 1.1 ("License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.zimbra.com/license
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
 * the License for the specific language governing rights and limitations
 * under the License.
 * 
 * The Original Code is: Zimbra Collaboration Suite Server.
 * 
 * The Initial Developer of the Original Code is Zimbra, Inc.
 * Portions created by Zimbra are Copyright (C) 2005 Zimbra, Inc.
 * All Rights Reserved.
 * 
 * Contributor(s): 
 * 
 * ***** END LICENSE BLOCK *****
 */

package com.zimbra.cs.service.wiki;

import java.io.IOException;
import java.util.Map;

import com.zimbra.cs.mailbox.Document;
import com.zimbra.cs.mailbox.Mailbox;
import com.zimbra.cs.mailbox.WikiItem;
import com.zimbra.cs.mailbox.Mailbox.OperationContext;
import com.zimbra.cs.service.ServiceException;
import com.zimbra.cs.service.mail.MailService;
import com.zimbra.cs.service.mail.ToXML;
import com.zimbra.cs.util.ByteUtil;
import com.zimbra.cs.util.ZimbraLog;
import com.zimbra.cs.wiki.Wiki;
import com.zimbra.cs.wiki.WikiWord;
import com.zimbra.soap.Element;
import com.zimbra.soap.ZimbraContext;

public class GetWiki extends WikiDocumentHandler {

	@Override
	public Element handle(Element request, Map context) throws ServiceException {
		ZimbraContext lc = getZimbraContext(context);
        OperationContext octxt = lc.getOperationContext();
        Element eword = request.getElement(MailService.E_WIKIWORD);
        String word = eword.getAttribute(MailService.A_NAME, null);
        String id = eword.getAttribute(MailService.A_ID, null);
        int rev = (int)eword.getAttributeLong(MailService.A_VERSION, -1);

        Element response = lc.createElement(MailService.GET_WIKI_RESPONSE);

        WikiItem wikiItem;
        
        if (word != null) {
            Wiki wiki = getRequestedWiki(request, lc);
            WikiWord w = wiki.lookupWiki(word);
            if (w == null) {
        		ZimbraLog.wiki.error("requested wiki word "+word+" does not exist");
            	return response;
            }
            wikiItem = w.getWikiItem(octxt);
        } else if (id != null) {
            Mailbox mbox = getRequestedMailbox(lc);
        	wikiItem = mbox.getWikiById(octxt, Integer.parseInt(id));
        } else {
        	throw ServiceException.FAILURE("missing attribute w or id", null);
        }
        
        Element wikiElem = ToXML.encodeWiki(response, lc, wikiItem, rev);
    	Document.DocumentRevision revision = (rev > 0) ? wikiItem.getRevision(rev) : wikiItem.getLastRevision(); 
    	try {
    		byte[] raw = ByteUtil.getContent(revision.getBlob().getFile());
    		wikiElem.addAttribute(MailService.A_BODY, new String(raw, "UTF-8"), Element.DISP_CONTENT);
    	} catch (IOException ioe) {
    		ZimbraLog.wiki.error("cannot read the wiki message body", ioe);
    	}
        return response;
	}
}
