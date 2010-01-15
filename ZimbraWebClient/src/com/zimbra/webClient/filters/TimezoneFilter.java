/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
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

package com.zimbra.webClient.filters;

import java.io.*;
import javax.servlet.*;
import com.zimbra.common.util.ZimbraLog;
import com.zimbra.kabuki.tools.tz.GenerateData;

public class TimezoneFilter implements Filter {

	//
	// Constants
	//

	static final String TIMEZONE_DATA_FILENAME = "/js/ajax/util/AjxTimezoneData.js";
	static final String TIMEZONE_ICS_FILENAME = "/opt/zimbra/conf/timezones.ics";

	static final String EXT_BACKUP = ".backup";
	static final String PRE_TEMP = TimezoneFilter.class.getName();
	static final String SUF_TEMP = ".js";

	//
	// Data
	//

	protected ServletContext context;

	//
	// Filter methods
	//

	public void doFilter(ServletRequest request, ServletResponse response,
						 FilterChain chain) throws ServletException, IOException {
		ZimbraLog.webclient.debug("%%% TimezoneFilter#doFilter");
		updateTimezoneData();
		chain.doFilter(request, response);
	}

	public void init(FilterConfig config) {
		ZimbraLog.webclient.debug("%%% TimezoneFilter#init");
		this.context = config.getServletContext();
		updateTimezoneData();
	}

	public void destroy() {}

	//
	// Protected methods
	//

	protected synchronized void updateTimezoneData() {
		ZimbraLog.webclient.debug("%%% TimezoneFilter#updateTimezoneData");
		File ftemp = null;
		try {
			File fin = new File(TIMEZONE_ICS_FILENAME);
			File fout = new File(this.context.getRealPath(TIMEZONE_DATA_FILENAME));
			ZimbraLog.webclient.debug("%%% timezone data in:  "+fin);
			ZimbraLog.webclient.debug("%%% timezone data out: "+fout);

			// is there anything to do?
			if (fin.lastModified() - fout.lastModified() <= 0) {
				return;
			}

			// generate new data
			ZimbraLog.webclient.debug("%%% timezone data out of sync, need to regenerate");
			ftemp = File.createTempFile(PRE_TEMP, SUF_TEMP);
			GenerateData.print(fin, ftemp);

			// save backup and move generated file
			fout.renameTo(new File(this.context.getRealPath(TIMEZONE_DATA_FILENAME+EXT_BACKUP)));
			ftemp.renameTo(fout);
			ZimbraLog.webclient.debug("%%% done");
		}
		catch (Exception e) {
			ZimbraLog.webclient.debug("%%% timezone data error", e);
			if (ftemp != null) {
				ftemp.delete();
			}
		}
	}

} // class TimezoneFilter
