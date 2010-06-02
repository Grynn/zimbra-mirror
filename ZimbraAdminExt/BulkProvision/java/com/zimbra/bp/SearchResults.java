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

import com.zimbra.cs.account.*;
import com.zimbra.cs.service.account.ToXML;
import com.zimbra.cs.service.admin.AdminAccessControl;
import com.zimbra.cs.service.admin.GetDomain;
import com.zimbra.cs.service.admin.GetCos;
import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.AdminConstants;
import com.zimbra.common.util.ZimbraLog;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.IOException;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


import au.com.bytecode.opencsv.CSVWriter;

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
    public static String [] ACCOUNT_ATTRS = {ATTR_displayName, ATTR_zimbraAccountStatus, ATTR_zimbraCOSId } ;
    private static Set<String> ACCOUNT_ATTRS_SET = new HashSet<String>(Arrays.asList(ACCOUNT_ATTRS));

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
            CSVWriter writer = new CSVWriter(new OutputStreamWriter (out) ) ;
            List entryList = getSearchResults(authToken, query, domain, types ); 
            int noCols = 6 ;
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
            d = prov.get(Provisioning.DomainBy.name, domain);
            if (d == null)
                throw AccountServiceException.NO_SUCH_DOMAIN(domain);
        }

        int flags = 0;

        if (types.indexOf("accounts") != -1) flags |= Provisioning.SA_ACCOUNT_FLAG;
        if (types.indexOf("aliases") != -1) flags |= Provisioning.SA_ALIAS_FLAG;
        if (types.indexOf("distributionlists") != -1) flags |= Provisioning.SA_DISTRIBUTION_LIST_FLAG;
        if (types.indexOf("resources") != -1) flags |= Provisioning.SA_CALENDAR_RESOURCE_FLAG;
        if (types.indexOf("domains") != -1) flags |= Provisioning.SA_DOMAIN_FLAG;
//            if (types.indexOf("coses") != -1) flags |= Provisioning.SD_COS_FLAG;

        Provisioning.SearchOptions options = new Provisioning.SearchOptions();
        options.setDomain(d);
        options.setFlags(flags);
        //make sure all the results are returned
//        options.setMaxResults(maxResults);
        options.setQuery(query);
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
