/*
 * ***** BEGIN LICENSE BLOCK *****
 *
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2007, 2008 Zimbra, Inc.
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
package com.zimbra.tarformatter;

import java.util.HashMap;
import java.util.ArrayList;

public class ZtoZImportParams
{
    public String SourceZCSServer;
    public String TargetZCSServer;
    public String SrcZCSPort;
    public String TrgtZCSPort;
    public String SrcAdminUser;
    public String TrgtAdminUser;
    public String SrcAdminPwd;
    public String TrgtAdminPwd;
    public String ZimbraMailTransport;
    public String ZMResolve;
    public int Threads;
    public String cfgfile;
    public String WorkingDirectory;
    public String FailedDirectory;
    public String SuccessDirectory;
    public String LogDirectory;
    public String KeepSuccessFiles;
    public HashMap<String,String> DomainMap;
    public String SourceServerURI;
    public String TrgtServerURI;
    public ArrayList<String> AccountsList;
    public ArrayList<String> DomainList;
    public boolean debug_mig;
    public boolean IsAllAccounts;
    public String ItemTypes;

    ZtoZImportParams()
    {
        SourceZCSServer="";
        TargetZCSServer="";
        SrcZCSPort="7071";
        TrgtZCSPort="7071";
        SrcAdminUser="";
        TrgtAdminUser="";
        SrcAdminPwd="";
        TrgtAdminPwd="";
        ZimbraMailTransport="";
        ZMResolve="";
        Threads=1;
        WorkingDirectory="";
        FailedDirectory="";
        SuccessDirectory="";
        LogDirectory="";
        KeepSuccessFiles="";
        DomainMap =new HashMap<String,String>();
        SourceServerURI="";
        AccountsList=new ArrayList<String>();
        DomainList = new ArrayList<String>();
        debug_mig=false;
        IsAllAccounts=false;
        ItemTypes="";
    }
}
