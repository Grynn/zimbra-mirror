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
package com.zimbra.yahoosmb;

import org.dom4j.Namespace;
import org.dom4j.QName;

import com.zimbra.soap.DocumentDispatcher;
import com.zimbra.soap.DocumentService;
import com.zimbra.common.soap.Element;
import com.zimbra.common.soap.AdminConstants;
import com.zimbra.common.service.ServiceException;
import com.zimbra.common.util.StringUtil;

import java.util.Map;
import java.util.HashMap;


public class ZimbraYahooSmbService implements DocumentService {
    public static final String NAMESPACE_STR = "urn:zimbraAdmin";
    public static final Namespace NAMESPACE = Namespace.get(NAMESPACE_STR);

    public static final String [] SMB_MODIFIABLE_ATTRS = {
            "zimbraCOSId", "zimbraFeatureMobileSyncEnabled", "zimbraFeatureSharingEnabled",
            "zimbraFeaturePop3DataSourceEnabled", "zimbraFeatureImapDataSourceEnabled",
            "zimbraMailCatchAllAddress"};
    public static String [] SMB_COSES = {"starter", "standard" , "pro"};
    public static String [] PA_COSES = {"starter"};

    public static final QName GET_ALL_SMB_COS_REQUEST = QName.get("GetAllSmbCosRequest", NAMESPACE);
    public static final QName GET_ALL_SMB_COS_RESPONSE = QName.get("GetAllSmbCosResponse", NAMESPACE);
    public static final QName GET_ALL_SMB_CONFIG_REQUEST = QName.get("GetAllSmbConfigRequest", NAMESPACE);
    public static final QName GET_ALL_SMB_CONFIG_RESPONSE = QName.get("GetAllSmbConfigResponse", NAMESPACE);

    public static final QName CREATE_SMB_ACCOUNT_REQUEST = QName.get("CreateSmbAccountRequest", NAMESPACE);
    public static final QName CREATE_SMB_ACCOUNT_RESPONSE = QName.get("CreateSmbAccountResponse", NAMESPACE);
    public static final QName MODIFY_SMB_ACCOUNT_REQUEST = QName.get("ModifySmbAccountRequest", NAMESPACE);
    public static final QName MODIFY_SMB_ACCOUNT_RESPONSE = QName.get("ModifySmbAccountResponse", NAMESPACE);

    public static final QName SEND_INVITATION_REQUEST = QName.get("SendInvitationRequest", NAMESPACE);
    public static final QName SEND_INVITATION_RESPONSE = QName.get("SendInvitationResponse", NAMESPACE);

    public static final String E_YAHOO = "yahoo";
    public static final String A_NOTIFICATION_EMAIL = "notification_email";
    
    public void registerHandlers(DocumentDispatcher dispatcher) {
        dispatcher.registerHandler(GET_ALL_SMB_COS_REQUEST, new GetAllSmbCos());
        dispatcher.registerHandler(CREATE_SMB_ACCOUNT_REQUEST, new CreateSmbAccount());
        dispatcher.registerHandler(MODIFY_SMB_ACCOUNT_REQUEST, new ModifySmbAccount());
        dispatcher.registerHandler(GET_ALL_SMB_CONFIG_REQUEST, new GetAllSmbConfig());
        dispatcher.registerHandler(GET_ALL_SMB_CONFIG_REQUEST, new GetAllSmbConfig());
        dispatcher.registerHandler(SEND_INVITATION_REQUEST, new SendInvitation());
    }

    public static boolean  isSmbDomainAdminModifiable (String attrName) {
        for (int i=0; i < SMB_MODIFIABLE_ATTRS.length; i ++) {
            if (attrName.equals(SMB_MODIFIABLE_ATTRS[i]))
                return true ;
        }
        return false ;
    }

    //TODO: get the availablecoses based on the ldap attribute
    public static String [] getAvailableCoses () {
        return  PA_COSES ;
    }

     public static Map<String, Object> getYahooAttrs(Element request, boolean ignoreEmptyValues) throws ServiceException {
        Map<String, Object> result = new HashMap<String, Object>();
        for (Element a : request.listElements(E_YAHOO)) {
            String name = a.getAttribute(AdminConstants.A_N);
            String value = a.getText();
            if (!ignoreEmptyValues || (value != null && value.length() > 0))
                StringUtil.addToMultiMap(result, name, value);
        }
        return result;
    }

}