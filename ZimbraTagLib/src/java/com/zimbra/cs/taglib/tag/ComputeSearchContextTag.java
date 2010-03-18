/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2006, 2007, 2008, 2009, 2010 Zimbra, Inc.
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
package com.zimbra.cs.taglib.tag;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.VoiceConstants;
import com.zimbra.cs.taglib.ZJspSession;
import com.zimbra.cs.taglib.bean.ZFolderBean;
import com.zimbra.cs.taglib.bean.ZTagBean;
import com.zimbra.cs.taglib.bean.ZPhoneAccountBean;
import com.zimbra.cs.zclient.ZFolder;
import com.zimbra.cs.zclient.ZMailbox;
import com.zimbra.cs.zclient.ZSearchFolder;
import com.zimbra.cs.zclient.ZSearchParams;
import com.zimbra.cs.zclient.ZTag;
import com.zimbra.cs.zclient.ZPhoneAccount;

import javax.servlet.ServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.PageContext;
import com.zimbra.cs.taglib.tag.i18n.I18nUtil;
import java.io.IOException;
import java.util.List;

public class ComputeSearchContextTag extends ZimbraSimpleTag {

    private static final int DEFAULT_SEARCH_LIMIT = 25;

    public static final String TYPE_MAIL = "mail";
    public static final String TYPE_CONTACTS = "contacts";

    private static final String QP_SEARCH_CONTEXT = "sc";
    private static final String QP_SEARCH_QUERY = "sq";
    private static final String QP_SEARCH_FOLDER_ID = "sfi";
    private static final String QP_SEARCH_TAG_ID = "sti";
    private static final String QP_SEARCH_OFFSET = "so";
    private static final String QP_SEARCH_LIMIT = "sl";
    private static final String QP_SEARCH_SORT = "ss";
    private static final String QP_SEARCH_USE_CACHE = "su";    
    private static final String QP_SEARCH_TYPES = "st";
    private static final String QP_SEARCH_INDEX = "si";

    private String mVar;
    private String mTypes;
    private String mQuery;
    private ZMailbox.SearchSortBy mSortBy;
    private boolean mUseCache;
    private int mLimit = -1;

    public void setVar(String var) { this.mVar = var; }

    public void setQuery(String query) { this.mQuery = query != null && query.length() > 0 ? query : null; }

    public void setUsecache(boolean usecache) {this.mUseCache = usecache; }

    public void setTypes(String types) { this.mTypes = types; }

    public void setLimit(int limit) { this.mLimit = limit; } 

    private int getInt(ServletRequest req, String name, int def) {
        try {
            String value = req.getParameter(name);
            if (value != null) return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            // ignore
        }
        return def;
    }

    public void doTag() throws JspException, IOException {
        try {
            ZMailbox mailbox = getMailbox();

            PageContext pageContext = (PageContext) getJspContext();
            ServletRequest req = pageContext.getRequest();

            int si = getInt(req, QP_SEARCH_INDEX, 0);
            int so = getInt(req, QP_SEARCH_OFFSET, 0);

            int usecache = getInt(req, QP_SEARCH_USE_CACHE, -1);
            if (usecache == 1) mUseCache = true; // otherwise, leave it set to what was passed in...

            String sc = req.getParameter(QP_SEARCH_CONTEXT);

            SearchContext sContext = SearchContext.getSearchContext(pageContext, sc);
            if (sContext != null) {
                pageContext.setAttribute(mVar, sContext, PageContext.REQUEST_SCOPE);

                if (si != -1) sContext.setCurrentItemIndex(si);

                if ((sContext.getSearchResult() == null || sContext.getParams().getOffset() != so) || !mUseCache) {
                    sContext.getParams().setOffset(so);
                }
            } else {
                // if we get here, we don't have a session, or the one we had timed out
                sContext = SearchContext.newSearchContext(pageContext);
                pageContext.setAttribute(mVar, sContext, PageContext.REQUEST_SCOPE);

                determineQuery(pageContext, sContext, req, mailbox); // TODO: throw exception?
                sContext.setParams(determineParams(sContext, req, so, mailbox));
                mUseCache = false; // always ignore cache on new search context. TODO: optimize?
            }
			String st = sContext.getSt();
			boolean isVoiceMailSearch = ZSearchParams.TYPE_VOICE_MAIL.equals(st);
			ZPhoneAccountBean account = sContext.getPhoneAccount();
			if (!isVoiceMailSearch || account == null || account.getHasVoiceMail()) { // Can't do voice mail search for accounts w/out that service.
				boolean useOffset =
						isVoiceMailSearch ||
						ZSearchParams.TYPE_CALL.equals(st) ||
						ZJspSession.getSearchUseOffset(pageContext);
				sContext.doSearch(mailbox, mUseCache, !useOffset);
	            if (sContext.getCurrentItemIndex() != si) sContext.setCurrentItemIndex(si);
			}
		} catch (ServiceException e) {
            throw new JspTagException(e.getMessage(), e);
        }
    }

    private ZSearchParams determineParams(SearchContext result, ServletRequest trareq, int so, ZMailbox mailbox) throws ServiceException {
        //String so = req.getParameter(QP_SEARCH_OFFSET);
        ZSearchParams params = new ZSearchParams(result.getQuery());

        params.setOffset(so);
        if (result.getFolder() != null && (result.getFolder().getIsDrafts() || result.getFolder().getIsSent()))
            params.setRecipientMode(true);

        params.setSortBy(mSortBy);
        params.setTypes(mTypes);

        if (mLimit == -1) {
                try {
                        mLimit = Integer.parseInt(trareq.getParameter(QP_SEARCH_LIMIT));
                } catch (NumberFormatException e) {
                        if (ZSearchParams.TYPE_CONTACT.equals(mTypes)) {
                                mLimit = (int) mailbox.getPrefs().getContactsPerPage(); 
                        } else if (ZSearchParams.TYPE_VOICE_MAIL.equals(mTypes) || ZSearchParams.TYPE_CALL.equals(mTypes)) {
                                mLimit = (int) mailbox.getPrefs().getVoiceItemsPerPage(); 
                        } else {
                                mLimit = (int) mailbox.getPrefs().getMailItemsPerPage();
                        }
                }
                if (mLimit == -1)
                        mLimit = DEFAULT_SEARCH_LIMIT;
        }
        params.setLimit(mLimit);
        //params.setFetchFirstMessage(mFetch);
        //params.setPeferHtml(mWanthtml);
        //params.setMarkAsRead(mMarkread);
        return params;
    }

    private void determineQuery(PageContext pageContext, SearchContext result, ServletRequest req, ZMailbox mailbox) throws JspException, ServiceException {
        String sq = mQuery != null ? mQuery : req.getParameter(QP_SEARCH_QUERY);
        String sfi = req.getParameter(QP_SEARCH_FOLDER_ID);
        String sti = req.getParameter(QP_SEARCH_TAG_ID);
        String st = req.getParameter(QP_SEARCH_TYPES);
        String ss = req.getParameter(QP_SEARCH_SORT);

        result.setSq(sq);
        result.setSfi(sfi);
        result.setSti(sti);
        result.setSs(ss);

        if (mTypes == null)
            mTypes = (st != null) ?
                    st :
                    mailbox.getFeatures().getConversations() ?
                            (mailbox.getPrefs().getGroupByMessage() ? ZSearchParams.TYPE_MESSAGE : ZSearchParams.TYPE_CONVERSATION) :
                            ZSearchParams.TYPE_MESSAGE;

        result.setSt(mTypes);

        if (ss != null) {
            mSortBy = ZMailbox.SearchSortBy.fromString(ss);
        }

        if (mSortBy == null)
            mSortBy = ZSearchParams.TYPE_CONTACT.equals(mTypes) ? ZMailbox.SearchSortBy.nameAsc :
                    ZSearchParams.TYPE_TASK.equals(mTypes) ? ZMailbox.SearchSortBy.taskDueDesc :
                    ZMailbox.SearchSortBy.dateDesc;

        // default to inbox/contacts
        if (sq == null && sti == null && sfi == null) {
            if (ZSearchParams.TYPE_CONTACT.equals(mTypes))
                sfi = ZFolder.ID_CONTACTS;
            else if (ZSearchParams.TYPE_TASK.equals(mTypes))
                sfi = ZFolder.ID_TASKS;
            else if (ZSearchParams.TYPE_WIKI.equals(mTypes))
                sfi = ZFolder.ID_NOTEBOOK;
            else if (ZSearchParams.TYPE_BRIEFCASE.equals(mTypes))
                sfi = ZFolder.ID_BRIEFCASE;
            else if (!ZSearchParams.TYPE_VOICE_MAIL.equals(mTypes)) {
                if (mailbox.getFeatures().getInitialSearchPreference()) {
                    sq = mailbox.getPrefs().getMailInitialSearch();
                    if (sq != null && sq.equalsIgnoreCase("in:inbox")) {
                        sq = null; // make it act like a folder search
                    }
                }
                if (sq == null || sq.length() == 0)
                    sfi = ZFolder.ID_INBOX;
            }
        }

        result.setTypes(mTypes);
        
        if (ZSearchParams.TYPE_VOICE_MAIL.equals(st) ||
            ZSearchParams.TYPE_CALL.equals(st)) {
            determineVoiceQuery(mailbox, sq, result);
            return;
        }

        if (sq != null) {
            result.setTitle(sq);
            result.setBackTo(I18nUtil.getLocalizedMessage(pageContext, "backToSearch"));
            result.setShortBackTo(sq);
            result.setQuery(sq);
            result.setShowMatches(true);
            return;
        } else if (sfi != null) {
            ZFolder folder = mailbox.getFolderById(sfi);
            if (folder != null) {
                if (folder instanceof ZSearchFolder) {
                    result.setQuery(((ZSearchFolder)folder).getQuery());
                    result.setShowMatches(true);
                    String name = I18nUtil.getLocalizedMessage(pageContext, "FOLDER_LABEL_"+folder.getId());
                    name = (name == null || name.startsWith("???")) ? folder.getName() : name;

                    result.setBackTo(I18nUtil.getLocalizedMessage(pageContext, "backToSearchFolder", new Object[] {name}));
                    result.setShortBackTo(name);
                } else {
                    String name = I18nUtil.getLocalizedMessage(pageContext, "FOLDER_LABEL_"+folder.getId());
                    name = (name == null || name.startsWith("???")) ? folder.getName() : name;

                    result.setQuery("in:\"" + folder.getRootRelativePath() + "\"");
                    result.setBackTo(I18nUtil.getLocalizedMessage(pageContext, "backToFolder", new Object[] {name}));
                    result.setShortBackTo(name);
                }
                result.setFolder(new ZFolderBean(folder));
                result.setTitle(folder.getName());
                result.setSelectedId(folder.getId());
                return;
            }
        } else if (sti != null) {
            ZTag tag = mailbox.getTagById(sti);
            if (tag != null) {
                result.setQuery("tag:\"" + tag.getName() + "\"");
                result.setTitle(tag.getName());
                result.setBackTo(I18nUtil.getLocalizedMessage(pageContext, "backToTag", new Object[] {tag.getName()}));
                result.setShortBackTo(tag.getName());
                result.setSelectedId(tag.getId());
                result.setTag(new ZTagBean(tag));                
                result.setShowMatches(true);
                return;
            }
        }
        throw new JspTagException("unable to determine query");
    }

    private void determineVoiceQuery(ZMailbox mailbox, String sq, SearchContext result) throws ServiceException {
        ZPhoneAccount account = getAccountFromVoiceQuery(mailbox, sq);
		result.setPhoneAccount(account);
		String query = sq;
        if (query == null && account != null) {
            query = "phone:" + account.getPhone().getName();
        }
        result.setQuery(query);
        if (account != null) {
            String folderName = VoiceConstants.FNAME_VOICEMAILINBOX;
            if (sq != null) {
                if (sq.indexOf(VoiceConstants.FNAME_MISSEDCALLS) != -1) {
                    folderName = VoiceConstants.FNAME_MISSEDCALLS;
                }
                else if (sq.indexOf(VoiceConstants.FNAME_ANSWEREDCALLS) != -1) {
                    folderName = VoiceConstants.FNAME_ANSWEREDCALLS;
                }
                else if (sq.indexOf(VoiceConstants.FNAME_PLACEDCALLS) != -1) {
                    folderName = VoiceConstants.FNAME_PLACEDCALLS;
                }
                else if (sq.indexOf(VoiceConstants.FNAME_TRASH) != -1) {
                    folderName = VoiceConstants.FNAME_TRASH;
                }
            }
            ZFolder folder = account.getRootFolder().getSubFolderByPath(folderName);
            result.setFolder(new ZFolderBean(folder));
            result.setSelectedId(folder.getId());
            result.setTitle(folder.getName());
        }
    }

    private ZPhoneAccount getAccountFromVoiceQuery(ZMailbox mailbox, String query) throws ServiceException {
        if (query != null) {
            String phone = "phone:";
            int match = query.indexOf(phone);
            if (match != -1) {
                int startIndex = match + phone.length();
                int endIndex = query.indexOf(' ', startIndex);
                if (endIndex == -1) {
                    endIndex = query.length();
                }
                String name = query.substring(startIndex, endIndex);
                return mailbox.getPhoneAccount(name);
            }
        }
        List<ZPhoneAccount> accounts = mailbox.getAllPhoneAccounts();
        return (accounts.size() > 0) ? accounts.get(0) : null;
    }

}
