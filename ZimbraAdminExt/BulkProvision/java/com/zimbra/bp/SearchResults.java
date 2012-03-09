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
package com.zimbra.bp;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.*;

import au.com.bytecode.opencsv.CSVWriter;

import com.zimbra.common.account.Key;
import com.zimbra.common.account.ZAttrProvisioning;
import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.AdminConstants;
import com.zimbra.common.util.DateUtil;
import com.zimbra.common.util.ZimbraLog;
import com.zimbra.cs.account.Account;
import com.zimbra.cs.account.AccountServiceException;
import com.zimbra.cs.account.Alias;
import com.zimbra.cs.account.AuthToken;
import com.zimbra.cs.account.CalendarResource;
import com.zimbra.cs.account.Cos;
import com.zimbra.cs.account.DistributionList;
import com.zimbra.cs.account.Domain;
import com.zimbra.cs.account.NamedEntry;
import com.zimbra.cs.account.Provisioning;
import com.zimbra.cs.account.SearchDirectoryOptions;
import com.zimbra.cs.ldap.ZLdapFilterFactory.FilterId;
import com.zimbra.cs.service.admin.AdminAccessControl;

/**
 * Created by IntelliJ IDEA.
 * User: ccao
 * Date: Feb 17, 2009
 * Time: 5:06:46 PM
 * To change this template use File | Settings | File Templates.
 */
public class SearchResults {
//    public static String ATTR_mail = "mail" ;
    public static String ATTR_displayName = "displayName" ;
    public static String ATTR_zimbraAccountStatus = "zimbraAccountStatus" ;
    public static String ATTR_zimbraCOSId = "zimbraCOSId" ;
//    public static String ATTR_zimbraId = "zimbraId" ;
    public static String [] ACCOUNT_ATTRS = {ATTR_displayName, ATTR_zimbraAccountStatus, ATTR_zimbraCOSId, ZAttrProvisioning.A_zimbraLastLogonTimestamp} ;
    private static Set<String> ACCOUNT_ATTRS_SET = new HashSet<String>(Arrays.asList(ACCOUNT_ATTRS));
    private static String DATE_PATTERN = "yyyy.MM.dd, hh:mm:ss z";

    /**
     * The CSV file format will be
     * name, zimbraId, type, [displayName, zimbraAccountStatus, zimbraCOSId]
     * @param out
     * @param query
     * @param domain
     * @param types
     */
    public static void writeSearchResultOutputStream (
            OutputStream out, String query, String domain, String types, AuthToken token)
    throws ServiceException{
        
        // the next line
        AuthToken authToken = token;
        
        try {
            CSVWriter writer = new CSVWriter(new OutputStreamWriter (out, "UTF-8") ) ;
            List entryList = getSearchResults(authToken, query, domain, types );
            SimpleDateFormat formatter = new SimpleDateFormat(DATE_PATTERN);
            int noCols = 7 ;
            for (int i = 0 ; i < entryList.size(); i ++) {
                String [] line = new String [noCols] ;
                int m = 0 ;
                NamedEntry entry = (NamedEntry) entryList.get(i) ;
                line [m ++] = entry.getName() ;
                line [m ++] = entry.getId();

                if (entry instanceof CalendarResource) {
                    CalendarResource res = (CalendarResource) entry ;
                    line [m ++] = AdminConstants.E_CALENDAR_RESOURCE ;
                } else if (entry instanceof Account) {
                    Account acct = (Account) entry ;
                    line[m ++] = AdminConstants.E_ACCOUNT ;
                } else if (entry instanceof DistributionList) {
                    line[m ++] = AdminConstants.E_DL ;
                } else if (entry instanceof Alias) {
                    line[m ++] = AdminConstants.E_ALIAS ;
                } else if (entry instanceof Domain) {
                    line[m ++] = AdminConstants.E_DOMAIN ;
                } else if (entry instanceof Cos) {
                    line[m ++] = AdminConstants.E_COS ;
                }

                 for (int j =0; j < ACCOUNT_ATTRS.length; j ++) {
                    line[j+m] = entry.getAttr(ACCOUNT_ATTRS[j], "") ;
                     if (ACCOUNT_ATTRS[j].equals(ZAttrProvisioning.A_zimbraLastLogonTimestamp)
                             && !line[j+m].equals("")) {
                         Date date = DateUtil.parseGeneralizedTime(line[j+m]);
                         line[j+m] = formatter.format(date);
                     }
                }
                
                ZimbraLog.extensions.debug("Adding entry content : " + Arrays.toString(line));
                writer.writeNext(line);
            }

            writer.close();
        }catch (Exception e) {
            ZimbraLog.extensions.error(e);
            throw ServiceException.FAILURE(e.getMessage(), e) ;
        }
    }

    public static List getSearchResults (AuthToken authToken, String query, String domain, String types)
    throws ServiceException {

        if (query == null) query = "";
        if (types == null) types = "";

        Provisioning prov = Provisioning.getInstance();

        Domain d = null;
        if (domain != null) {
            d = prov.get(Key.DomainBy.name, domain);
            if (d == null)
                throw AccountServiceException.NO_SUCH_DOMAIN(domain);
        }

        SearchDirectoryOptions options = new SearchDirectoryOptions();
        options.setDomain(d);
        options.setTypes(types);
        //make sure all the results are returned
//        options.setMaxResults(maxResults);
        options.setFilterString(FilterId.ADMIN_SEARCH, query);
        options.setReturnAttrs(ACCOUNT_ATTRS);
//            options.setSortAscending(sortAscending);
//            options.setSortAttr(sortBy);
        options.setConvertIDNToAscii(true);
        List accounts = prov.searchDirectory(options);
        
        // check rights and only returns allowed entries
        AdminAccessControl aac = AdminAccessControl.getAdminAccessControl(authToken);
        AdminAccessControl.SearchDirectoryRightChecker rightChecker = 
            new AdminAccessControl.SearchDirectoryRightChecker(aac, prov, ACCOUNT_ATTRS_SET);
        accounts = rightChecker.getAllowed(accounts);
        
        return accounts ;

    }

    public static void main (String [] args) throws ServiceException {
        try {
            // List accounts = getSearchResults("", "ccaomac.zimbra.com", "accounts, aliases, aliases, resources, domains, coses" );
            FileOutputStream fo = new FileOutputStream ("/tmp/sr_out") ;
            writeSearchResultOutputStream(fo, "", null, "accounts, distributionlists, aliases, resources,domains", null) ;
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
