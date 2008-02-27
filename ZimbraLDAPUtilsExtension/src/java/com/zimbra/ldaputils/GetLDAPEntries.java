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
package com.zimbra.ldaputils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import javax.naming.InvalidNameException;
import javax.naming.NameNotFoundException;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.SizeLimitExceededException;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InvalidSearchFilterException;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.Control;
import javax.naming.ldap.LdapContext;
import javax.naming.ldap.PagedResultsControl;
import javax.naming.ldap.PagedResultsResponseControl;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.util.ZimbraLog;

import com.zimbra.cs.account.AccountServiceException;
import com.zimbra.cs.account.NamedEntry;
import com.zimbra.cs.account.Provisioning;

import com.zimbra.cs.account.ldap.LdapUtil;
import com.zimbra.cs.account.ldap.LdapDomain;
import com.zimbra.common.soap.Element;
import com.zimbra.common.soap.AdminConstants;
import com.zimbra.soap.ZimbraSoapContext;

import com.zimbra.cs.service.admin.AdminDocumentHandler;



/**
 * @author Greg Solovyev
 */
public class GetLDAPEntries extends AdminDocumentHandler {
	public static final String C_LDAPEntry = "LDAPEntry";
	
    public Element handle(Element request, Map<String, Object> context) throws ServiceException {

    	ZimbraSoapContext lc = getZimbraSoapContext(context);
        
    	Element b = request.getElement(ZimbraLDAPUtilsService.E_LDAPSEARCHBASE);
        String ldapSearchBase;
    	if(isDomainAdminOnly(lc)) {
    		ldapSearchBase = ((LdapDomain)getAuthTokenAccountDomain(lc)).getDN();
    	} else {
    		ldapSearchBase = b.getText();
    	}
        String sortBy = request.getAttribute(AdminConstants.A_SORT_BY, null);
        boolean sortAscending = request.getAttributeBool(AdminConstants.A_SORT_ASCENDING, true);
        int limit = (int) request.getAttributeLong(AdminConstants.A_LIMIT, Integer.MAX_VALUE);
        if (limit == 0)
            limit = Integer.MAX_VALUE;

        int offset = (int) request.getAttributeLong(AdminConstants.A_OFFSET, 0);
        String query = request.getAttribute(AdminConstants.E_QUERY);

        List LDAPEntrys;
        LDAPEntrys = searchObjects(query,ldapSearchBase,sortBy,sortAscending);
     
    	Element response = lc.createElement(ZimbraLDAPUtilsService.GET_LDAP_ENTRIES_RESPONSE);
        int i, limitMax = offset+limit;
        for (i=offset; i < limitMax && i < LDAPEntrys.size(); i++) {
            NamedEntry entry = (NamedEntry) LDAPEntrys.get(i);
            ZimbraLDAPUtilsService.encodeLDAPEntry(response,entry);
        }  
        
    	return response;
    }
    
    /** Returns whether domain admin auth is sufficient to run this command.
     *  This should be overriden only on admin commands that can be run in a
     *  restricted "domain admin" mode. */
    public boolean domainAuthSufficient(Map<String, Object> context) {
        return true; 
    }
    
    public static LDAPUtilEntry getObjectByDN(String dn, DirContext initCtxt) throws ServiceException {
        DirContext ctxt = initCtxt;
        try {
            if (ctxt == null)
                ctxt = LdapUtil.getDirContext();
               
            Attributes attrs = ctxt.getAttributes(dn);
            LDAPUtilEntry ne = new LDAPUtilEntry(dn, attrs,null);
            return ne;
            
        } catch (NameNotFoundException e) {
            return null;
        } catch (InvalidNameException e) {
            return null;                        
        } catch (NamingException e) {
            throw ServiceException.FAILURE("unable to find dn: "+dn+" message: "+e.getMessage(), e);
        } finally {
            if (initCtxt == null)
                LdapUtil.closeContext(ctxt);
        }
    }
    
    public List<NamedEntry> searchObjects(String query,String base, final String sortAttr, final boolean sortAscending)
    throws ServiceException {
        final List<NamedEntry> result = new ArrayList<NamedEntry>();
        
        NamedEntry.Visitor visitor = new NamedEntry.Visitor() {
            public void visit(NamedEntry entry) {
                result.add(entry);
            }
        };
        
        searchObjects(query,  base,  visitor);

        final boolean byName = sortAttr == null || sortAttr.equals("name"); 
        Comparator<NamedEntry> comparator = new Comparator<NamedEntry>() {
            public int compare(NamedEntry oa, NamedEntry ob) {
                NamedEntry a = (NamedEntry) oa;
                NamedEntry b = (NamedEntry) ob;
                int comp = 0;
                if (byName)
                    comp = a.getName().compareToIgnoreCase(b.getName());
                else {
                    String sa = a.getAttr(sortAttr);
                    String sb = b.getAttr(sortAttr);
                    if (sa == null) sa = "";
                    if (sb == null) sb = "";
                    comp = sa.compareToIgnoreCase(sb);
                }
                return sortAscending ? comp : -comp;
            }
        };
        Collections.sort(result, comparator);        
        return result;

    }
    
    
    void searchObjects(String query,  String base, NamedEntry.Visitor visitor)
        throws ServiceException
    {
        DirContext ctxt = null;
        try {
            ctxt = LdapUtil.getDirContext();
            
            SearchControls searchControls = 
                new SearchControls(SearchControls.SUBTREE_SCOPE, 0, 0, null, false, false);

            // Set the page size and initialize the cookie that we pass back in
			// subsequent pages
            int pageSize = 1000;
            byte[] cookie = null;
 
            LdapContext lctxt = (LdapContext)ctxt; 
 
            // we don't want to ever cache any of these, since they might not
			// have all their attributes

            NamingEnumeration ne = null;

            try {
                do {
                    lctxt.setRequestControls(new Control[]{new PagedResultsControl(pageSize, cookie, Control.CRITICAL)});
                    
                    ne = ctxt.search(base, query, searchControls);
                    while (ne != null && ne.hasMore()) {
                        SearchResult sr = (SearchResult) ne.nextElement();
                        String dn = sr.getNameInNamespace();
                        // skip admin accounts
                        if (dn.endsWith("cn=zimbra")) continue;
                        Attributes attrs = sr.getAttributes();

                        if(Arrays.binarySearch(LdapUtil.getMultiAttrString(attrs, Provisioning.A_objectClass), "sambaDomain") > -1) {
                        	visitor.visit(new SambaDomain(dn, attrs,null));                        
                    	} else if(Arrays.binarySearch(LdapUtil.getMultiAttrString(attrs, Provisioning.A_objectClass), "posixGroup") > -1) {
                        	visitor.visit(new PosixGroup(dn, attrs,null));
                    	} else if(Arrays.binarySearch(LdapUtil.getMultiAttrString(attrs, Provisioning.A_objectClass), "posixAccount") > -1) {
                        	visitor.visit(new PosixAccount(dn, attrs,null));
                    	} else {
                        	visitor.visit(new LDAPUtilEntry(dn, attrs,null));
                        }
                    }
                    cookie = getCookie(lctxt);
                } while (cookie != null);
            } finally {
                if (ne != null) ne.close();
            }
        } catch (InvalidSearchFilterException e) {
            throw ServiceException.INVALID_REQUEST("invalid search filter "+e.getMessage(), e);
        } catch (NameNotFoundException e) {
            // happens when base doesn't exist
            ZimbraLog.extensions.warn("unable to list all objects", e);
        } catch (SizeLimitExceededException e) {
            throw AccountServiceException.TOO_MANY_SEARCH_RESULTS("too many search results returned", e);
        } catch (NamingException e) {
            throw ServiceException.FAILURE("unable to list all objects", e);
        } catch (IOException e) {
            throw ServiceException.FAILURE("unable to list all objects", e);            
        } finally {
            LdapUtil.closeContext(ctxt);
        }   
    }
    
    private byte[] getCookie(LdapContext lctxt) throws NamingException {
        Control[] controls = lctxt.getResponseControls();
        if (controls != null) {
            for (int i = 0; i < controls.length; i++) {
                if (controls[i] instanceof PagedResultsResponseControl) {
                    PagedResultsResponseControl prrc =
                        (PagedResultsResponseControl)controls[i];
                    return prrc.getCookie();
                }
            }
        }
        return null;
    }  
}
