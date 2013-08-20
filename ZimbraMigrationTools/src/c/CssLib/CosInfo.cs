/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite CSharp Client
 * Copyright (C) 2011, 2013 Zimbra Software, LLC.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.4 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */
namespace CssLib
{
    public class CosInfo
    {
        public string CosName
        {
            get;
            set;
        }
        public string CosID
        {
            get;
            set;
        }
        public CosInfo(string cosname, string cosid)
        {
            CosName = cosname;
            CosID = cosid;
        }
    }

    public class DomainInfo
    {
        public string DomainName
        {
            get;
            set;
        }

        public string DomainID
        {
            get;
            set;
        }

        public string zimbraDomainDefaultCOSId
        {
            get;
            set;
        }

        public DomainInfo(string domainname, string domainid, string zimbradomaindefaultcosid)
        {
            DomainName = domainname;
            DomainID = domainid;
            zimbraDomainDefaultCOSId = zimbradomaindefaultcosid;
        }
    }
}
