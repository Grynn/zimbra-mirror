/*
 * ***** BEGIN LICENSE BLOCK *****
 *
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2004, 2005, 2006, 2007 Zimbra, Inc.
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

/*
 * Created on Jun 17, 2004
 */
package com.zimbra.yahoosmb;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.AdminConstants;
import com.zimbra.common.soap.Element;
import com.zimbra.cs.account.Provisioning;
import com.zimbra.cs.account.Cos;
import com.zimbra.cs.service.admin.AdminDocumentHandler;
import com.zimbra.cs.service.admin.GetCos;
import com.zimbra.soap.ZimbraSoapContext;
import java.util.Map;

/**
 * @author ccao
 */
public class GetAllSmbCos extends AdminDocumentHandler {
    
    /**
     * must be careful and only create accounts for the domain admin!
     */
    public boolean domainAuthSufficient(Map context) {
        return true;
    }

	public Element handle(Element request, Map<String, Object> context) throws ServiceException {

        ZimbraSoapContext lc = getZimbraSoapContext(context);
	    Provisioning prov = Provisioning.getInstance();

        Element response = lc.createElement(ZimbraYahooSmbService.GET_ALL_SMB_COS_RESPONSE);
        Cos cos = null ;
        
        //String [] availableCoses =  ZimbraYahooSmbService.SMB_COSES ;
        String [] availableCoses =  ZimbraYahooSmbService.getAvailableCoses() ;

        for (int i = 0 ; i < availableCoses.length ; i ++) {
            cos = prov.get(Provisioning.CosBy.fromString(AdminConstants.A_NAME), availableCoses[i]) ;
            GetCos.doCos(response, cos);
        }
        
        return response;
	}
}