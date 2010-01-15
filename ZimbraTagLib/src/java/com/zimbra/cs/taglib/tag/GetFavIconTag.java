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
package com.zimbra.cs.taglib.tag;

import com.zimbra.cs.account.soap.SoapProvisioning;
import com.zimbra.cs.account.Entry;
import com.zimbra.cs.account.Provisioning;
import com.zimbra.common.localconfig.LC;
import com.zimbra.common.soap.AdminConstants;
import com.zimbra.common.util.ZimbraLog;
import com.zimbra.common.util.HttpUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import java.io.IOException;

public class GetFavIconTag extends ZimbraSimpleTag {

	//
	// Data
	//

	private String var;
	private HttpServletRequest request;

	//
	// Public methods
	//

	// properties

	public void setVar(String var) {
		this.var = var;
	}

	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}

	// simple tag methods

	public void doTag() throws JspException, IOException {
		try {
			// get provisioning
			String soapUri =
				LC.zimbra_admin_service_scheme.value() +
				LC.zimbra_zmprov_default_soap_server.value() +
				':' +
				LC.zimbra_admin_service_port.intValue() +
				AdminConstants.ADMIN_SERVICE_URI
			;

			SoapProvisioning provisioning = new SoapProvisioning();
			provisioning.soapSetURI(soapUri);

			// get serverName
			String serverName = this.request.getParameter("customerDomain");
			// TODO: Is this possible in this context? Does it matter?
//			if(serverName==null || serverName.trim().length() == 0) {
//				serverName = getServletConfig().getInitParameter(P_SERVER_NAME);
//			}
			if (serverName == null) {
				serverName = HttpUtil.getVirtualHost(this.request);
			}

			// get info
			Entry info = provisioning.getDomainInfo(Provisioning.DomainBy.virtualHostname, serverName);
			if (info == null) {
				info = provisioning.getConfig();
			}
			if (info != null) {
			    String favicon = info.getAttr("zimbraSkinFavicon");
				getJspContext().setAttribute(this.var, favicon, PageContext.REQUEST_SCOPE);
			}
			else {
				if (ZimbraLog.webclient.isDebugEnabled()) {
					ZimbraLog.webclient.debug("unable to get domain or config info");
				}
			}
		}
		catch (Exception e) {
			if (ZimbraLog.webclient.isDebugEnabled()) {
				ZimbraLog.webclient.debug("error getting favicon: "+e.getMessage());
			}
		}
	}

} // class GetFavIconTag
