/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2007, 2009, 2010 Zimbra, Inc.
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

import com.zimbra.common.localconfig.LC;
import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.AdminConstants;
import com.zimbra.common.util.DateUtil;
import com.zimbra.common.util.EasySSLProtocolSocketFactory;
import com.zimbra.cs.account.Domain;
import com.zimbra.cs.account.Provisioning.DomainBy;
import com.zimbra.cs.account.soap.SoapProvisioning;

import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.jstl.core.Config;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class GetDomainInfoTag extends ZimbraSimpleTag {

    static {
        if (LC.ssl_allow_untrusted_certs.booleanValue())
            EasySSLProtocolSocketFactory.init();
    }

    private static final String CONFIG_ZIMBRA_DOMAININFO_TTL = "zimbra.domaininfo.ttl";

    private String mVar;
    private DomainBy mBy;
    private String mValue;

    private static final Map<String, CachedDomain> mCache = new HashMap<String, CachedDomain>();

    public void setVar(String var) { this.mVar = var; }
    public void setBy(String by) throws ServiceException { this.mBy = DomainBy.fromString(by); }
    public void setValue(String value) { this.mValue = value; }

    private static final String DEFAULT_TTL_STR = "60m";
    private static final long DEFAULT_TTL = 60*60*1000;
    private static long sCacheTtl = -1;
    
    public void doTag() throws JspException, IOException {
        JspContext ctxt = getJspContext();
        if (sCacheTtl == -1) {
            String ttl = (String) Config.find((PageContext) ctxt, CONFIG_ZIMBRA_DOMAININFO_TTL);
            sCacheTtl = DateUtil.getTimeInterval(ttl != null ? ttl : DEFAULT_TTL_STR, DEFAULT_TTL);
        }

        ctxt.setAttribute(mVar, checkCache(),  PageContext.REQUEST_SCOPE);
    }

    private String getCacheKey() { return mBy +"/" + mValue; }

    private Domain checkCache() {
        CachedDomain cd = mCache.get(getCacheKey());
        if (cd != null) {
            if (cd.expireTime > System.currentTimeMillis())
                return cd.domain;
        }
        Domain d = getInfo();
        synchronized(mCache) {
            mCache.put(getCacheKey(), new CachedDomain(d));
        }
        return d;
    }

    private Domain getInfo() {
        SoapProvisioning sp = new SoapProvisioning();
        String mServer = LC.zimbra_zmprov_default_soap_server.value();
        int mPort = LC.zimbra_admin_service_port.intValue();
        sp.soapSetURI(LC.zimbra_admin_service_scheme.value()+mServer+":"+mPort+ AdminConstants.ADMIN_SERVICE_URI);
        try {
            return sp.getDomainInfo(mBy, mValue);
        } catch (ServiceException e) {
            e.printStackTrace();
            return null;
        }
    }

    static class CachedDomain {
        public Domain domain;
        public long expireTime;

        public CachedDomain(Domain d) { domain = d; expireTime = System.currentTimeMillis() + sCacheTtl; }
    }
}
