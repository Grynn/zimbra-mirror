/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2011 Zimbra, Inc.
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
package com.zimbra.cs.nginx;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.util.StringUtil;
import com.zimbra.common.util.ZimbraLog;
import com.zimbra.cs.account.Config;
import com.zimbra.cs.account.ldap.LdapProv;
import com.zimbra.cs.account.ldap.legacy.LegacyLdapUtil;
import com.zimbra.cs.account.ldap.legacy.LegacyZimbraLdapContext;
import com.zimbra.cs.ldap.ILdapContext;
import com.zimbra.cs.ldap.LdapClient;
import com.zimbra.cs.ldap.LdapUtilCommon;
import com.zimbra.cs.ldap.ZLdapFilter;
import com.zimbra.cs.ldap.ZLdapFilterFactory.FilterId;
import com.zimbra.cs.nginx.NginxLookupExtension.EntryNotFoundException;
import com.zimbra.cs.nginx.NginxLookupExtension.NginxLookupException;

/*
 * An LDAP helper using the legacy LDAP SDK.  Retire this class when the new SDK 
 * is fully rolled in.
 */
public class LegacyNginxLookupLdapHelper extends AbstractNginxLookupLdapHelper {

    LegacyNginxLookupLdapHelper(LdapProv prov) {
        super(prov);
    }
    
    @Override
    ILdapContext getLdapContext() throws ServiceException {
        return new LegacyZimbraLdapContext();
    }
    
    @Override
    void closeLdapContext(ILdapContext ldapContext) {
        LegacyZimbraLdapContext zlc = LdapClient.toLegacyZimbraLdapContext(
                prov, ldapContext);
        LegacyZimbraLdapContext.closeContext(zlc);
    }
    
    @Override
    Map<String, Object> searchDir(ILdapContext ldapContext, String[] returnAttrs,
            Config config, ZLdapFilter filter, String searchBaseConfigAttr)
    throws NginxLookupException {
        
        LegacyZimbraLdapContext zlc = LdapClient.toLegacyZimbraLdapContext(
                prov, ldapContext);
        
        Map<String, Object> attrs = null;
        
        String base  = config.getAttr(searchBaseConfigAttr);
        if (base == null)
            base = "";
        
        SearchControls searchControls = 
            new SearchControls(SearchControls.SUBTREE_SCOPE, 1, 0, returnAttrs, false, false);
        
        NamingEnumeration ne = null;
        try {
            try {
                ne = zlc.searchDir(base, filter.toFilterString(), searchControls);
                if (!ne.hasMore()) {
                    throw new NginxLookupException("query returned empty result: " + filter.toFilterString());
                }
                SearchResult sr = (SearchResult) ne.next();
                Attributes ldapAttrs = sr.getAttributes();
                attrs = LegacyLdapUtil.getAttrs(ldapAttrs);
            } finally {
                if (ne != null) {
                    ne.close();
                }
            }
        } catch (NamingException e) { 
            throw new NginxLookupException("unable to search LDAP", e);
        }
        
        return attrs;

    }

    @Override
    SearchDirResult searchDirectory(ILdapContext ldapContext, String[] returnAttrs,
            Config config, FilterId filterId, String queryTemplate, String searchBase,
            String templateKey, String templateVal, Map<String, Boolean> attrs,
            Set<String> extraAttrs) 
    throws NginxLookupException {
        LegacyZimbraLdapContext zlc = LdapClient.toLegacyZimbraLdapContext(
                prov, ldapContext);
        
        HashMap<String, String> kv = new HashMap<String,String>();
        kv.put(templateKey, LdapUtilCommon.escapeSearchFilterArg(templateVal));
        String query = config.getAttr(queryTemplate);
        String base  = config.getAttr(searchBase);
        if (query == null)
            throw new NginxLookupException("empty attribute: "+queryTemplate);
        
        ZimbraLog.nginxlookup.debug("query template attr=" + queryTemplate + ", query template=" + query);
        query = StringUtil.fillTemplate(query, kv);
        ZimbraLog.nginxlookup.debug("query=" + query);
        
        if (base == null)
            base = "";
        
        SearchControls searchControls = 
            new SearchControls(SearchControls.SUBTREE_SCOPE, 1, 0, returnAttrs, false, false);
        
        SearchDirResult sdr = new SearchDirResult();
        
        NamingEnumeration ne = null;
        try {
            try {
                ne = zlc.searchDir(base, query, searchControls);
                
                if (!ne.hasMore())
                    throw new EntryNotFoundException("query returned empty result: "+query);
                SearchResult sr = (SearchResult) ne.next();
                
                sdr.configuredAttrs = new HashMap<String, String>();
                lookupAttrs(sdr.configuredAttrs, config, sr, attrs);
                
                sdr.extraAttrs = new HashMap<String, String>();
                if (extraAttrs != null) {
                    Attributes attributes = sr.getAttributes();
                    for (String attr : extraAttrs) {
                        String val = LegacyLdapUtil.getAttrString(attributes, attr);
                        if (val != null)
                            sdr.extraAttrs.put(attr, val);
                    }
                }
            } finally {
                if (ne != null) {
                    ne.close();
                }
            }
        } catch (NamingException e) { 
            throw new NginxLookupException("unable to search LDAP", e);
        }
        
        return sdr;

    }
    
    private void lookupAttrs(Map<String, String> vals, Config config, SearchResult sr, Map<String, Boolean> keys) 
    throws NginxLookupException, NamingException {
        for (Map.Entry<String, Boolean> keyEntry : keys.entrySet()) {
            String key = keyEntry.getKey();
            String val = lookupAttr(config, sr, key, keyEntry.getValue());
            if (val != null)
                vals.put(key, val);
        }
    }
    
    private String lookupAttr(Config config, SearchResult sr, String key, Boolean required) 
    throws NginxLookupException, NamingException {
        String val = null;
        String attr = config.getAttr(key);
        if (attr == null && required)
            throw new NginxLookupException("missing attr in config: "+key);
        if (attr != null) {
            val = LegacyLdapUtil.getAttrString(sr.getAttributes(), attr);
            if (val == null && required)
                throw new NginxLookupException("missing attr in search result: "+attr);
        }
        return val;
    }


}
