using System.Collections.Generic;

namespace CssLib
{
public class ZimbraValues
{
    public static ZimbraValues zimbraValues;

    public ZimbraValues()
    {
        zimbraValues = null;
        sUrl = "";
        sAuthToken = "";
        sServerVersion = "";
        lDomains = new List<string>();
        lCOSes = new List<CosInfo>();
    }

    public static ZimbraValues GetZimbraValues()
    {
        if (zimbraValues == null)
            zimbraValues = new ZimbraValues();
        return zimbraValues;
    }

    private string sUrl;
    public string Url {
        get { return sUrl; }
        set
        {
            sUrl = value;
        }
    }
    private string sAuthToken;
    public string AuthToken {
        get { return sAuthToken; }
        set
        {
            sAuthToken = value;
        }
    }
    private string sServerVersion;
    public string ServerVersion {
        get { return sServerVersion; }
        set
        {
            sServerVersion = value;
        }
    }
    private string sHostName;
    public string HostName {
        get { return sHostName; }
        set
        {
            sHostName = value;
        }
    }
    private string sPort;
    public string Port {
        get { return sPort; }
        set
        {
            sPort = value;
        }
    }
    private List<string> lDomains;
    public List<string> Domains {
        get { return lDomains; }
        set
        {
            lDomains = value;
        }
    }
    private List<CosInfo> lCOSes;
    public List<CosInfo> COSes {
        get { return lCOSes; }
        set
        {
            lCOSes = value;
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
}
}
