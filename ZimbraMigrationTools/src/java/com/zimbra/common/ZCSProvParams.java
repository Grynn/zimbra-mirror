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
package com.zimbra.common;

public class ZCSProvParams 
{
    public boolean cmdmode;
    public boolean lock;
    public boolean unlock;
    public boolean create;
    public long recordstoprocess;
    public String csvfile;
    public String ytozconfigfile;
    public String zcsurl;
    public String zcsport;
    public String zcs_nonssl_port;
    public String adminname;
    public String adminpwd;
    public String imapsyncfile;
    public String yurl;
    public String yport;
    public String yuser;
    public String ypasswd;
    public String zcsimapport;
    public String yisurl;
    public String yisport;
    public String yisfarm;
    public String default_cos;
    public String zimbraAuthMech;
    public String sbsNotifyUrl;
    public long csvrec_numbers;
    public long csvoffset;
    public boolean importyaccounts;
    public boolean importmail;
    public boolean importyab;
    public boolean changedns;
    public boolean revertdns;
    public boolean miratezflkeys;
    public boolean setflkey;
    public boolean deleteflkey;
    public boolean notifysbs;
    public boolean rmpassword;
    public boolean addzmauthmech;
    public String testpasswd; //for test purpose without UDB auth
    public String defaultpassword;
    public int threadcount;
    public boolean customrun;// no zimbraAuthMech & with default a/c pwd
    public String IMAPSyncUrl;
    public boolean excludeargs;
    public String DNSUrl;
    public String testdomainext;
    public ZCSProvParams()
    {
        cmdmode=false;
        lock=false;
        create=false;
        recordstoprocess=-1;
        csvfile=null;
        zcsurl=null;
        zcsport=null;
        adminname=null;
        adminpwd=null;
        imapsyncfile=null;
        yurl=null;
        yport=null;
        yuser=null;
        ypasswd=null;
        yisurl=null;
        yisport=null;
        yisfarm=null;
        default_cos=null;
        csvrec_numbers=-1;
        csvoffset=0;
        ytozconfigfile=null;
        importmail=false;
        importyab=false;
        importyaccounts=false;
        changedns=false;
        revertdns=false;
        miratezflkeys=false;
        setflkey=false;
        deleteflkey=false;
        notifysbs=false;
        rmpassword=false;
        addzmauthmech=false;
        zcsimapport="7143";
        sbsNotifyUrl=null;
        testpasswd=null;
        customrun=false;
        IMAPSyncUrl=null;
        excludeargs =false;
        DNSUrl=null;
        testdomainext="";
    }
}
