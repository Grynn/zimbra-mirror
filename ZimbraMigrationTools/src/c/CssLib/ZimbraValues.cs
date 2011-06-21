using System.Collections.Generic;

namespace CssLib
{
    public class ZimbraValues
    {
        public static ZimbraValues zimbraValues;

        public ZimbraValues()
        {
            zimbraValues = null;
            sHostName = "";
            sPort = "";
            sAuthToken = "";
            bIsAdminAccount = true;
            bIsDomainAdminAccount = false;
            sAccountName = "";
            sServerVersion = "";
            lDomains = new List<string>();
            lCOSes = new List<string>();
        }

        public static ZimbraValues GetZimbraValues()
        {
	        if (zimbraValues == null)
	        {
		        zimbraValues = new ZimbraValues() ;
	        }
	        return zimbraValues;
        }

        private string sHostName;
        public string HostName
        {
            get { return sHostName; }
            set
            {
                sHostName = value;
            }
        }

        private string sPort;
        public string Port
        {
            get { return sPort; }
            set
            {
                sPort = value;
            }
        }

        private string sAuthToken;
        public string AuthToken
        {
            get { return sAuthToken; }
            set
            {
                sAuthToken = value;
            }
        }

        private bool bIsAdminAccount;
        public bool IsAdminAccount
        {
            get { return bIsAdminAccount; }
            set
            {
                bIsAdminAccount = value;
            }
        }

        private bool bIsDomainAdminAccount;
        public bool IsDomainAdminAccount
        {
            get { return bIsDomainAdminAccount; }
            set
            {
                bIsDomainAdminAccount = value;
            }
        }

        private string sAccountName;
        public string AccountName
        {
            get { return sAccountName; }
            set
            {
                sAccountName = value;
            }
        }

        private string sServerVersion;
        public string ServerVersion
        {
            get { return sServerVersion; }
            set
            {
                sServerVersion = value;
            }
        }

        private List<string> lDomains;
        public List<string> Domains
        {
            get { return lDomains; }
            set
            {
                lDomains = value;
            }
        }

        private List<string> lCOSes;
        public List<string> COSes
        {
            get { return lCOSes; }
            set
            {
                lCOSes = value;
            }
        }
    }
}

