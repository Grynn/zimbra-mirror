using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Reflection;
using System.Text;
using System.Xml.Linq;
using System.Xml;
using System;

namespace CssLib
{
public class ZimbraAPI
{
    // Errors
    internal const int TASK_CREATE_FAILED_FLDR = 93;
    internal const int APPT_CREATE_FAILED_FLDR = 94;
    internal const int CONTACT_CREATE_FAILED_FLDR = 95;
    internal const int FOLDER_CREATE_FAILED_SYN = 96;
    internal const int FOLDER_CREATE_FAILED_SEM = 97;
    internal const int ACCOUNT_NO_NAME = 98;
    internal const int ACCOUNT_CREATE_FAILED = 99;
    //

    // Upload modes
    public const int STRING_MODE = 1;           // for messages -- request is all string data
    public const int MIXED_MODE = 2;            // for contacts (or appts) -- mixed string and binary
    //

    // Values
    internal const int INLINE_LIMIT = 4000;     // smaller than this limit, we'll inline; larger, we'll upload

    // Special folders array
    string[] specialFolders = {
        "", "/MAPIRoot", "/MAPIRoot/Inbox",
        "/MAPIRoot/Deleted Items",
        "/MAPIRoot/Junk E-Mail", "/MAPIRoot/Sent Items",
        "/MAPIRoot/Drafts", "/MAPIRoot/Contacts",
        "/MAPIRoot/Tags", "/MAPIRoot/Conversations",
        "/MAPIRoot/Calendar", "", "/MAPIRoot/Wiki",
        "/MAPIRoot/Emailed Contacts", "/MAPIRoot/Chats",
        "/MAPIRoot/Tasks"
    };

    private string lastError;
    public string LastError {
        get { return lastError; }
        set
        {
            lastError = value;
        }
    }
    private string sAccountName;
    public string AccountName {
        get { return sAccountName; }
        set
        {
            sAccountName = value;
        }
    }
    private bool bIsAdminAccount;
    public bool IsAdminAccount {
        get { return bIsAdminAccount; }
        set
        {
            bIsAdminAccount = value;
        }
    }
    private bool bIsDomainAdminAccount;
    public bool IsDomainAdminAccount {
        get { return bIsDomainAdminAccount; }
        set
        {
            bIsDomainAdminAccount = value;
        }
    }
    private Dictionary<string, string> dFolderMap;

    public ZimbraAPI()
    {
        ZimbraValues.GetZimbraValues();
        dFolderMap = new Dictionary<string, string>();
    }

    private string GetSpecialFolderNum(string folderPath)
    {
        string sFolderPath = folderPath.ToUpper();
        for (int i = 0; i < specialFolders.Length; i++)
        {
            string sSpecialFolder = specialFolders[i].ToUpper();
            if (sFolderPath == sSpecialFolder)
                return i.ToString();
        }
        Log.debug("Can't find special folder", folderPath, ". Message will be lost.");
        return "";
    }

    // Parse Methods //////////////////
    // [note that we don't have Parse methods for CreateContact, CreateFolder, etc.]
    private string ParseSoapFault(string rsperr)
    {
        if (rsperr.Length == 0)
            return "";
        if (rsperr.IndexOf("<soap:Fault>") == -1)
            return "";

        string soapReason = "";
        XDocument xmlDoc = XDocument.Parse(rsperr);
        XNamespace ns = "http://www.w3.org/2003/05/soap-envelope";

        IEnumerable<XElement> de = from el in xmlDoc.Descendants() select el;

        foreach (XElement el in de)
            if (el.Name == ns + "Reason")
            {
                soapReason = el.Value;
                break;
            }
        return soapReason;
    }

    private void ParseLogon(string rsp, bool isAdmin)
    {
        string authToken = "";
        string isDomainAdmin = "false";

        if (rsp != null)
        {
            int startIdx = rsp.IndexOf("<authToken>");

            if (startIdx != -1)
            {
                XDocument xmlDoc = XDocument.Parse(rsp);
                XNamespace ns = (isAdmin) ? "urn:zimbraAdmin" : "urn:zimbraAccount";

                // we'll have to deal with this -- need to figure this out later -- with GetInfo
                // for now, just faking -- always setting admin stuff to false if not admin -- not right
                foreach (var objIns in xmlDoc.Descendants(ns + "AuthResponse"))
                {
                    authToken += objIns.Element(ns + "authToken").Value;
                    isDomainAdmin = "false";
                    if (isAdmin)
                    {
                        var x = from a in objIns.Elements(ns + "a") where a.Attribute(
                            "n").Value == "zimbraIsDomainAdminAccount" select a.Value;

                        isDomainAdmin = x.ElementAt(0);
                    }
                }
            }
        }
        ZimbraValues.GetZimbraValues().AuthToken = authToken;
    }

    private void ParseGetInfo(string rsp)
    {
        string accountName = "";
        string serverVersion = "";

        if (rsp != null)
        {
            int startNameIdx = rsp.IndexOf("<name>");
            int startVersionIdx = rsp.IndexOf("<version>");

            if ((startNameIdx != -1) && (startVersionIdx != -1))
            {
                XDocument xmlDoc = XDocument.Parse(rsp);
                XNamespace ns = "urn:zimbraAccount";

                foreach (var objIns in xmlDoc.Descendants(ns + "GetInfoResponse"))
                {
                    accountName += objIns.Element(ns + "name").Value;
                    serverVersion += objIns.Element(ns + "version").Value;
                }
            }
        }
        ZimbraValues.GetZimbraValues().ServerVersion = serverVersion;
    }

    private int ParseGetAccount(string rsp)
    {
        int retval = 0;

        if (rsp != null)
        {
            int dIdx = rsp.IndexOf("account id=");

            if (dIdx != -1)
            {
                XDocument xmlDoc = XDocument.Parse(rsp);
                XNamespace ns = "urn:zimbraAdmin";

                foreach (var objIns in xmlDoc.Descendants(ns + "GetAccountResponse"))
                {
                    foreach (XElement accountIns in objIns.Elements())
                    {
                        foreach (XAttribute accountAttr in accountIns.Attributes())
                        {
                            if (accountAttr.Name == "name")
                            {
                                retval = (accountAttr.Value).Length;
                                break;
                            }
                        }
                    }
                }
            }
        }
        return retval;
    }

    private int ParseCreateAccount(string rsp)
    {
        int retval = 0;

        if (rsp != null)
        {
            int dIdx = rsp.IndexOf("account id=");

            if (dIdx != -1)
            {
                XDocument xmlDoc = XDocument.Parse(rsp);
                XNamespace ns = "urn:zimbraAdmin";

                foreach (var objIns in xmlDoc.Descendants(ns + "CreateAccountResponse"))
                {
                    foreach (XElement accountIns in objIns.Elements())
                    {
                        foreach (XAttribute accountAttr in accountIns.Attributes())
                        {
                            if (accountAttr.Name == "name")
                            {
                                retval = (accountAttr.Value).Length;
                                break;
                            }
                        }
                    }
                }
            }
        }
        return retval;
    }

    private void ParseGetAllDomain(string rsp)
    {
        if (rsp != null)
        {
            int dIdx = rsp.IndexOf("domain");

            if (dIdx != -1)
            {
                XDocument xmlDoc = XDocument.Parse(rsp);
                XNamespace ns = "urn:zimbraAdmin";

                foreach (var objIns in xmlDoc.Descendants(ns + "GetAllDomainsResponse"))
                {
                    foreach (XElement domainIns in objIns.Elements())
                    {
                        foreach (XAttribute domainAttr in domainIns.Attributes())
                        {
                            if (domainAttr.Name == "name")
                                ZimbraValues.GetZimbraValues().Domains.Add(domainAttr.Value);
                        }
                    }
                }
            }
        }
    }

    private void ParseGetAllCos(string rsp)
    {
        if (rsp != null)
        {
            int dIdx = rsp.IndexOf("cos");

            if (dIdx != -1)
            {
                XDocument xmlDoc = XDocument.Parse(rsp);
                XNamespace ns = "urn:zimbraAdmin";

                foreach (var objIns in xmlDoc.Descendants(ns + "GetAllCosResponse"))
                {
                    foreach (XElement cosIns in objIns.Elements())
                    {
                        string name = "";
                        string id = "";

                        foreach (XAttribute cosAttr in cosIns.Attributes())
                        {
                            if (cosAttr.Name == "name")
                                name = cosAttr.Value;
                            if (cosAttr.Name == "id")
                                id = cosAttr.Value;
                        }
                        if ((name.Length > 0) || (id.Length > 0))
                            ZimbraValues.GetZimbraValues().COSes.Add(new CosInfo(name, id));
                    }
                }
            }
        }
    }

    // may not need this -- it's here anyway for now
    private void ParseAddMsg(string rsp, out string mID)
    {
        mID = "";
        if (rsp != null)
        {
            int midIdx = rsp.IndexOf("m id");

            if (midIdx != -1)
            {
                XDocument xmlDoc = XDocument.Parse(rsp);
                XNamespace ns = "urn:zimbraMail";

                foreach (var objIns in xmlDoc.Descendants(ns + "AddMsgResponse"))
                {
                    foreach (XElement mIns in objIns.Elements())
                    {
                        foreach (XAttribute mAttr in mIns.Attributes())
                        {
                            if (mAttr.Name == "id")
                                mID = mAttr.Value;
                        }
                    }
                }
            }
        }
    }

    private void ParseCreateFolder(string rsp, out string folderID)
    {
        folderID = "";
        if (rsp != null)
        {
            int idx = rsp.IndexOf("folder id=");

            if (idx != -1)
            {
                XDocument xmlDoc = XDocument.Parse(rsp);
                XNamespace ns = "urn:zimbraMail";

                foreach (var objIns in xmlDoc.Descendants(ns + "CreateFolderResponse"))
                {
                    foreach (XElement folderIns in objIns.Elements())
                    {
                        foreach (XAttribute mAttr in folderIns.Attributes())
                        {
                            if (mAttr.Name == "id")
                                folderID = mAttr.Value;
                        }
                    }
                }
            }
        }
    }

    // ////////

    // private UploadFile method
    private int UploadFile(string filepath, int mode, out string uploadToken)
    {
        bool isSecure = (ZimbraValues.GetZimbraValues().Url).Substring(0, 5) == "https";
        WebServiceClient client = (isSecure) ? new WebServiceClient {
            Url = "https://" + ZimbraValues.GetZimbraValues().HostName + ":" +
                ZimbraValues.GetZimbraValues().Port + "/service/upload?fmt=raw",
            WSServiceType = WebServiceClient.ServiceType.Traditional
        } : new WebServiceClient {
            Url = "http://" + ZimbraValues.GetZimbraValues().HostName + ":" +
                ZimbraValues.GetZimbraValues().Port + "/service/upload?fmt=raw",
            WSServiceType = WebServiceClient.ServiceType.Traditional
        };
        int retval = 0;
        string rsp = "";

        uploadToken = "";

        client.InvokeUploadService(ZimbraValues.GetZimbraValues().AuthToken, isSecure, filepath,
            mode, out rsp);
        retval = client.status;
        if (retval == 0)
        {
            int li = rsp.LastIndexOf(",");

            if (li != -1)
            {
                // get the string with the upload token, which will have a leading ' and a trailing '\r\n -- so strip that stuff off
                int uti = li + 1;               // upload token index
                string tmp = rsp.Substring(uti, (rsp.Length - uti));
                int lastsinglequoteidx = tmp.LastIndexOf("'");

                uploadToken = tmp.Substring(1, (lastsinglequoteidx - 1));
            }
        }
        return retval;
    }

    //

    // private API helper methods

    // example: <name>foo</name>
    private void WriteNVPair(XmlWriter writer, string name, string value)
    {
        writer.WriteStartElement(name);
        writer.WriteValue(value);
        writer.WriteEndElement();
    }

    // example: <a n="displayName">bar</a>
    private void WriteAttrNVPair(XmlWriter writer, string fieldType, string fieldName, string
        attrName, string attrValue)
    {
        writer.WriteStartElement(fieldType);
        writer.WriteStartAttribute(fieldName);
        writer.WriteString(attrName);
        writer.WriteEndAttribute();
        writer.WriteValue(attrValue);
        writer.WriteEndElement();
    }

    // example: <account by="name">foo@bar.com</account>
    private void WriteAccountBy(XmlWriter writer, string val)
    {
        WriteAttrNVPair(writer, "account", "by", "name", val);
    }

    private void WriteHeader(XmlWriter writer, bool bWriteSessionId, bool bWriteAuthtoken, bool
        bWriteAccountBy)
    {
        writer.WriteStartElement("Header", "http://www.w3.org/2003/05/soap-envelope");
        writer.WriteStartElement("context", "urn:zimbra");
        writer.WriteStartElement("nonotify");
        writer.WriteEndElement();               // nonotify
        writer.WriteStartElement("noqualify");
        writer.WriteEndElement();               // noqualify
        writer.WriteStartElement("nosession");
        writer.WriteEndElement();               // nosession
        if (bWriteSessionId)
        {
            writer.WriteStartElement("sessionId");
            writer.WriteEndElement();           // sessionId
        }
        if (bWriteAuthtoken)
            WriteNVPair(writer, "authToken", ZimbraValues.zimbraValues.AuthToken);
        if (bWriteAccountBy)                    // would only happen after a logon
            WriteAccountBy(writer, AccountName);
        writer.WriteEndElement();               // context
        writer.WriteEndElement();               // header
    }

    //

    // API methods /////////
    public int Logon(string hostname, string port, string username, string password, bool
        isAdmin)
    {
        if (ZimbraValues.GetZimbraValues().AuthToken.Length > 0)
            return 0;                           // already logged on
        lastError = "";

        string urn = "";

        if (isAdmin)
        {
            ZimbraValues.GetZimbraValues().Url = "https://" + hostname + ":" + port +
                "/service/admin/soap";
            urn = "urn:zimbraAdmin";
        }
        else
        {
            ZimbraValues.GetZimbraValues().Url = "http://" + hostname + ":" + port +
                "/service/soap";
            urn = "urn:zimbraAccount";
        }
        WebServiceClient client = new WebServiceClient {
            Url = ZimbraValues.GetZimbraValues().Url, WSServiceType =
                WebServiceClient.ServiceType.Traditional
        };
        StringBuilder sb = new StringBuilder();
        XmlWriterSettings settings = new XmlWriterSettings();

        settings.OmitXmlDeclaration = true;
        using (XmlWriter writer = XmlWriter.Create(sb, settings)) {
            writer.WriteStartDocument();
            writer.WriteStartElement("soap", "Envelope",
                "http://www.w3.org/2003/05/soap-envelope");

            WriteHeader(writer, false, false, false);

            // body
            writer.WriteStartElement("Body", "http://www.w3.org/2003/05/soap-envelope");
            writer.WriteStartElement("AuthRequest", urn);
            if (isAdmin)
                WriteNVPair(writer, "name", username);
            else
                WriteAccountBy(writer, username);
            WriteNVPair(writer, "password", password);

            writer.WriteEndElement();           // AuthRequest
            writer.WriteEndElement();           // soap body
            // end body

            writer.WriteEndElement();           // soap envelope
            writer.WriteEndDocument();
        }

        string rsp = "";

        client.InvokeService(sb.ToString(), out rsp);
        if (client.status == 0)
        {
            ParseLogon(rsp, isAdmin);
            ZimbraValues.GetZimbraValues().HostName = hostname;
            ZimbraValues.GetZimbraValues().Port = port;
        }
        else
        {
            string soapReason = ParseSoapFault(client.errResponseMessage);

            if (soapReason.Length > 0)
                lastError = soapReason;
            else
                lastError = client.exceptionMessage;
        }
        return client.status;
    }

    public int GetInfo()
    {
        lastError = "";
        WebServiceClient client = new WebServiceClient {
            Url = ZimbraValues.GetZimbraValues().Url, WSServiceType =
                WebServiceClient.ServiceType.Traditional
        };
        StringBuilder sb = new StringBuilder();
        XmlWriterSettings settings = new XmlWriterSettings();

        settings.OmitXmlDeclaration = true;
        using (XmlWriter writer = XmlWriter.Create(sb, settings)) {
            writer.WriteStartDocument();
            writer.WriteStartElement("soap", "Envelope",
                "http://www.w3.org/2003/05/soap-envelope");

            WriteHeader(writer, true, true, false);

            // body
            writer.WriteStartElement("Body", "http://www.w3.org/2003/05/soap-envelope");
            writer.WriteStartElement("GetInfoRequest", "urn:zimbraAccount");
            writer.WriteAttributeString("sections", "mbox");
            writer.WriteEndElement();           // GetInfoRequest
            writer.WriteEndElement();           // soap body
            // end body

            writer.WriteEndElement();           // soap envelope
            writer.WriteEndDocument();
        }

        string rsp = "";

        client.InvokeService(sb.ToString(), out rsp);
        if (client.status == 0)
        {
            ParseGetInfo(rsp);
        }
        else
        {
            string soapReason = ParseSoapFault(client.errResponseMessage);

            if (soapReason.Length > 0)
                lastError = soapReason;
            else
                lastError = client.exceptionMessage;
        }
        return client.status;
    }

    public int GetAllDomains()
    {
        if (ZimbraValues.zimbraValues.Domains.Count > 0)        // already got 'em
            return 0;
        lastError = "";
        WebServiceClient client = new WebServiceClient {
            Url = ZimbraValues.GetZimbraValues().Url, WSServiceType =
                WebServiceClient.ServiceType.Traditional
        };
        StringBuilder sb = new StringBuilder();
        XmlWriterSettings settings = new XmlWriterSettings();

        settings.OmitXmlDeclaration = true;
        using (XmlWriter writer = XmlWriter.Create(sb, settings)) {
            writer.WriteStartDocument();
            writer.WriteStartElement("soap", "Envelope",
                "http://www.w3.org/2003/05/soap-envelope");

            WriteHeader(writer, true, true, false);

            // body
            writer.WriteStartElement("Body", "http://www.w3.org/2003/05/soap-envelope");
            writer.WriteStartElement("GetAllDomainsRequest", "urn:zimbraAdmin");
            writer.WriteEndElement();           // GetAllDomainsRequest
            writer.WriteEndElement();           // soap body
            // end body

            writer.WriteEndElement();           // soap envelope
            writer.WriteEndDocument();
        }

        string rsp = "";

        client.InvokeService(sb.ToString(), out rsp);
        if (client.status == 0)
        {
            ParseGetAllDomain(rsp);
        }
        else
        {
            string soapReason = ParseSoapFault(client.errResponseMessage);

            if (soapReason.Length > 0)
                lastError = soapReason;
            else
                lastError = client.exceptionMessage;
        }
        return client.status;
    }

    public int GetAllCos()
    {
        if (ZimbraValues.zimbraValues.COSes.Count > 0)  // already got 'em
            return 0;
        lastError = "";
        WebServiceClient client = new WebServiceClient {
            Url = ZimbraValues.GetZimbraValues().Url, WSServiceType =
                WebServiceClient.ServiceType.Traditional
        };
        StringBuilder sb = new StringBuilder();
        XmlWriterSettings settings = new XmlWriterSettings();

        settings.OmitXmlDeclaration = true;
        using (XmlWriter writer = XmlWriter.Create(sb, settings)) {
            writer.WriteStartDocument();
            writer.WriteStartElement("soap", "Envelope",
                "http://www.w3.org/2003/05/soap-envelope");

            WriteHeader(writer, true, true, false);

            // body
            writer.WriteStartElement("Body", "http://www.w3.org/2003/05/soap-envelope");
            writer.WriteStartElement("GetAllCosRequest", "urn:zimbraAdmin");
            writer.WriteEndElement();           // GetAllCosRequest
            writer.WriteEndElement();           // soap body
            // end body

            writer.WriteEndElement();           // soap envelope
            writer.WriteEndDocument();
        }

        string rsp = "";

        client.InvokeService(sb.ToString(), out rsp);
        if (client.status == 0)
        {
            ParseGetAllCos(rsp);
        }
        else
        {
            string soapReason = ParseSoapFault(client.errResponseMessage);

            if (soapReason.Length > 0)
                lastError = soapReason;
            else
                lastError = client.exceptionMessage;
        }
        return client.status;
    }

    public int GetAccount(string accountname)
    {
        int retval = 0;

        lastError = "";
        WebServiceClient client = new WebServiceClient {
            Url = ZimbraValues.GetZimbraValues().Url, WSServiceType =
                WebServiceClient.ServiceType.Traditional
        };
        StringBuilder sb = new StringBuilder();
        XmlWriterSettings settings = new XmlWriterSettings();

        settings.OmitXmlDeclaration = true;
        using (XmlWriter writer = XmlWriter.Create(sb, settings)) {
            writer.WriteStartDocument();
            writer.WriteStartElement("soap", "Envelope",
                "http://www.w3.org/2003/05/soap-envelope");

            WriteHeader(writer, true, true, false);

            // body
            writer.WriteStartElement("Body", "http://www.w3.org/2003/05/soap-envelope");
            writer.WriteStartElement("GetAccountRequest", "urn:zimbraAdmin");

            WriteAccountBy(writer, accountname);

            writer.WriteEndElement();           // GetAccountRequest
            writer.WriteEndElement();           // soap body
            // end body

            writer.WriteEndElement();           // soap envelope
            writer.WriteEndDocument();
        }

        string rsp = "";

        client.InvokeService(sb.ToString(), out rsp);
        retval = client.status;
        if (client.status == 0)
        {
            if (ParseGetAccount(rsp) == 0)      // length of name is 0 -- this is bad
                retval = ACCOUNT_NO_NAME;
        }
        else
        {
            string soapReason = ParseSoapFault(client.errResponseMessage);

            if (soapReason.Length > 0)
                lastError = soapReason;
            else
                lastError = client.exceptionMessage;
        }
        return retval;
    }

    public int CreateAccount(string accountname, string defaultpw, string cosid)
    {
        int retval = 0;

        lastError = "";

        string displayname = accountname.Substring(0, accountname.IndexOf("@"));
        string zimbraForeignPrincipal = "ad:" + displayname;
        WebServiceClient client = new WebServiceClient {
            Url = ZimbraValues.GetZimbraValues().Url, WSServiceType =
                WebServiceClient.ServiceType.Traditional
        };
        StringBuilder sb = new StringBuilder();
        XmlWriterSettings settings = new XmlWriterSettings();

        settings.OmitXmlDeclaration = true;
        using (XmlWriter writer = XmlWriter.Create(sb, settings)) {
            writer.WriteStartDocument();
            writer.WriteStartElement("soap", "Envelope",
                "http://www.w3.org/2003/05/soap-envelope");

            WriteHeader(writer, true, true, false);

            // body
            writer.WriteStartElement("Body", "http://www.w3.org/2003/05/soap-envelope");
            writer.WriteStartElement("CreateAccountRequest", "urn:zimbraAdmin");

            WriteNVPair(writer, "name", accountname);
            WriteNVPair(writer, "password", defaultpw);

            WriteAttrNVPair(writer, "a", "n", "displayName", displayname);
            WriteAttrNVPair(writer, "a", "n", "zimbraForeignPrincipal", zimbraForeignPrincipal);
            WriteAttrNVPair(writer, "a", "n", "zimbraCOSId", cosid);

            writer.WriteEndElement();           // CreateAccountRequest
            writer.WriteEndElement();           // soap body
            // end body

            writer.WriteEndElement();           // soap envelope
            writer.WriteEndDocument();
        }

        string rsp = "";

        client.InvokeService(sb.ToString(), out rsp);
        retval = client.status;
        if (client.status == 0)
        {
            if (ParseCreateAccount(rsp) == 0)   // length of name is 0 -- this is bad
                retval = ACCOUNT_CREATE_FAILED;
        }
        else
        {
            string soapReason = ParseSoapFault(client.errResponseMessage);

            if (soapReason.Length > 0)
                lastError = soapReason;
            else
                lastError = client.exceptionMessage;
        }
        return retval;
    }

    public void CreateContactRequest(XmlWriter writer, Dictionary<string, string> contact,
        string folderId, int requestId)
    {
        writer.WriteStartElement("CreateContactRequest", "urn:zimbraMail");
        if (requestId != -1)
            writer.WriteAttributeString("requestId", requestId.ToString());
        writer.WriteStartElement("cn");
        writer.WriteAttributeString("l", folderId);
        foreach (KeyValuePair<string, string> pair in contact)
        {
            string nam = pair.Key;
            string val = pair.Value;

            if (nam == "image")
            {
                if (val.Length > 0)
                {
                    string uploadToken = "";

                    if (UploadFile(val, MIXED_MODE, out uploadToken) == 0)
                    {
                        writer.WriteStartElement("a");
                        writer.WriteAttributeString("n", nam);
                        writer.WriteAttributeString("aid", uploadToken);
                        writer.WriteEndElement();
                    }
                    File.Delete(val);
                }
            }
            else
            {
                WriteAttrNVPair(writer, "a", "n", nam, val);
            }
        }
        writer.WriteEndElement();               // cn
        writer.WriteEndElement();               // CreateContactRequest
    }

    public int CreateContact(Dictionary<string, string> contact, string folderPath = "")
    {
        lastError = "";

        // Create in Contacts unless another folder was desired
        string folderId = "7";

        if (folderPath.Length > 0)
        {
            folderId = FindFolder(folderPath);
            if (folderId.Length == 0)
                return CONTACT_CREATE_FAILED_FLDR;
        }

        // //////
        WebServiceClient client = new WebServiceClient {
            Url = ZimbraValues.GetZimbraValues().Url, WSServiceType =
                WebServiceClient.ServiceType.Traditional
        };
        int retval = 0;
        StringBuilder sb = new StringBuilder();
        XmlWriterSettings settings = new XmlWriterSettings();

        settings.OmitXmlDeclaration = true;
        using (XmlWriter writer = XmlWriter.Create(sb, settings)) {
            writer.WriteStartDocument();
            writer.WriteStartElement("soap", "Envelope",
                "http://www.w3.org/2003/05/soap-envelope");

            WriteHeader(writer, true, true, true);

            writer.WriteStartElement("Body", "http://www.w3.org/2003/05/soap-envelope");

            CreateContactRequest(writer, contact, folderId, -1);

            writer.WriteEndElement();           // soap body
            writer.WriteEndElement();           // soap envelope
            writer.WriteEndDocument();
        }

        string rsp = "";

        client.InvokeService(sb.ToString(), out rsp);
        retval = client.status;
        return retval;
    }

    public int CreateContacts(List<Dictionary<string, string> > lContacts, string folderPath =
        "")
    {
        lastError = "";

        // Create in Contacts unless another folder was desired
        string folderId = "7";

        if (folderPath.Length > 0)
        {
            folderId = FindFolder(folderPath);
            if (folderId.Length == 0)
                return CONTACT_CREATE_FAILED_FLDR;
        }

        // //////
        WebServiceClient client = new WebServiceClient {
            Url = ZimbraValues.GetZimbraValues().Url, WSServiceType =
                WebServiceClient.ServiceType.Traditional
        };
        int retval = 0;
        StringBuilder sb = new StringBuilder();
        XmlWriterSettings settings = new XmlWriterSettings();

        settings.OmitXmlDeclaration = true;
        using (XmlWriter writer = XmlWriter.Create(sb, settings)) {
            writer.WriteStartDocument();
            writer.WriteStartElement("soap", "Envelope",
                "http://www.w3.org/2003/05/soap-envelope");

            WriteHeader(writer, true, true, true);

            writer.WriteStartElement("Body", "http://www.w3.org/2003/05/soap-envelope");
            writer.WriteStartElement("BatchRequest", "urn:zimbra");
            for (int i = 0; i < lContacts.Count; i++)
            {
                Dictionary<string, string> contact = lContacts[i];
                CreateContactRequest(writer, contact, folderId, i);
            }
            writer.WriteEndElement();           // BatchRequest
            writer.WriteEndElement();           // soap body
            writer.WriteEndElement();           // soap envelope
            writer.WriteEndDocument();
        }

        string rsp = "";

        client.InvokeService(sb.ToString(), out rsp);
        retval = client.status;
        return retval;
    }

    public void AddMsgRequest(XmlWriter writer, string uploadInfo, ZimbraMessage message, bool
        isInline, int requestId)
    {
        // if isLine, uploadInfo will be a file path; if not, uploadInfo will be the upload token
        writer.WriteStartElement("AddMsgRequest", "urn:zimbraMail");
        if (requestId != -1)
            writer.WriteAttributeString("requestId", requestId.ToString());
        writer.WriteStartElement("m");
        writer.WriteAttributeString("l", message.folderId);
        writer.WriteAttributeString("d", message.rcvdDate);
        writer.WriteAttributeString("f", message.flags);
        if (isInline)
        {
            WriteNVPair(writer, "content", System.Text.Encoding.Default.GetString(
                File.ReadAllBytes(uploadInfo)));
        }
        else
        {
            writer.WriteAttributeString("aid", uploadInfo);
        }
        writer.WriteEndElement();               // m
        writer.WriteEndElement();               // AddMsgRequest
    }

    public int AddMessage(Dictionary<string, string> message)
    {
        lastError = "";

        string uploadInfo = "";
        int retval = 0;
        ZimbraMessage zm = new ZimbraMessage("", "", "", "", "");

        System.Type type = typeof (ZimbraMessage);
        FieldInfo[] myFields = type.GetFields(BindingFlags.Public | BindingFlags.Instance);
        for (int i = 0; i < myFields.Length; i++)       // use reflection to set ZimbraMessage object values
        {
            string nam = (string)myFields[i].Name;

            if (nam == "folderId")
                myFields[i].SetValue(zm, FindFolder(message[nam]));
            else
                myFields[i].SetValue(zm, message[nam]);
        }

        FileInfo f = new FileInfo(zm.filePath); // use a try/catch?
        bool isInline = (f.Length < INLINE_LIMIT);

        if (isInline)
            uploadInfo = zm.filePath;
        else
            retval = UploadFile(zm.filePath, STRING_MODE, out uploadInfo);
        if (retval == 0)
        {
            WebServiceClient client = new WebServiceClient {
                Url = ZimbraValues.GetZimbraValues().Url, WSServiceType =
                    WebServiceClient.ServiceType.Traditional
            };
            StringBuilder sb = new StringBuilder();
            XmlWriterSettings settings = new XmlWriterSettings();

            settings.OmitXmlDeclaration = true;
            using (XmlWriter writer = XmlWriter.Create(sb, settings)) {
                writer.WriteStartDocument();
                writer.WriteStartElement("soap", "Envelope",
                    "http://www.w3.org/2003/05/soap-envelope");

                WriteHeader(writer, true, true, true);

                writer.WriteStartElement("Body", "http://www.w3.org/2003/05/soap-envelope");

                AddMsgRequest(writer, uploadInfo, zm, isInline, -1);

                writer.WriteEndElement();       // soap body
                writer.WriteEndElement();       // soap envelope
                writer.WriteEndDocument();
            }

            string rsp = "";

            client.InvokeService(sb.ToString(), out rsp);
            retval = client.status;
            if (client.status == 0)
            {
                string mID = "";

                ParseAddMsg(rsp, out mID);      // get the id
            }
            else
            {
                string soapReason = ParseSoapFault(client.errResponseMessage);

                if (soapReason.Length > 0)
                    lastError = soapReason;
                else
                    lastError = client.exceptionMessage;
            }
        }
        File.Delete(zm.filePath);
        return retval;
    }

    public int AddMessages(List<Dictionary<string, string> > lMessages)
    {
        int retval = 0;

        lastError = "";

        string uploadInfo = "";

        System.Type type = typeof (ZimbraMessage);
        FieldInfo[] myFields = type.GetFields(BindingFlags.Public | BindingFlags.Instance);
        WebServiceClient client = new WebServiceClient {
            Url = ZimbraValues.GetZimbraValues().Url, WSServiceType =
                WebServiceClient.ServiceType.Traditional
        };
        StringBuilder sb = new StringBuilder();
        XmlWriterSettings settings = new XmlWriterSettings();

        settings.OmitXmlDeclaration = true;
        using (XmlWriter writer = XmlWriter.Create(sb, settings)) {
            writer.WriteStartDocument();
            writer.WriteStartElement("soap", "Envelope",
                "http://www.w3.org/2003/05/soap-envelope");

            WriteHeader(writer, true, true, true);

            writer.WriteStartElement("Body", "http://www.w3.org/2003/05/soap-envelope");
            writer.WriteStartElement("BatchRequest", "urn:zimbra");
            for (int i = 0; i < lMessages.Count; i++)
            {
                Dictionary<string, string> message = lMessages[i];

                ZimbraMessage zm = new ZimbraMessage("", "", "", "", "");

                for (int j = 0; j < myFields.Length; j++)       // use reflection to set ZimbraMessage object values
                {
                    string nam = (string)myFields[j].Name;

                    if (nam == "folderId")
                        myFields[j].SetValue(zm, FindFolder(message[nam]));
                    else
                        myFields[j].SetValue(zm, message[nam]);
                }

                FileInfo f = new FileInfo(zm.filePath);
                bool isInline = (f.Length < INLINE_LIMIT);

                if (isInline)
                    uploadInfo = zm.filePath;
                else
                    retval = UploadFile(zm.filePath, STRING_MODE, out uploadInfo);
                if (retval == 0)
                    AddMsgRequest(writer, uploadInfo, zm, isInline, -1);
                File.Delete(zm.filePath);
            }
            writer.WriteEndElement();           // BatchRequest
            writer.WriteEndElement();           // soap body
            writer.WriteEndElement();           // soap envelope
            writer.WriteEndDocument();
        }

        string rsp = "";

        client.InvokeService(sb.ToString(), out rsp);
        retval = client.status;

        return retval;
    }

    public void SetAppointmentRequest(XmlWriter writer, Dictionary<string, string> appt,
        string folderId, int requestId)
    {
        bool isRecurring = appt.ContainsKey("freq");
        int numExceptions = (appt.ContainsKey("numExceptions")) ? Int32.Parse(appt["numExceptions"]) : 0;
        writer.WriteStartElement("SetAppointmentRequest", "urn:zimbraMail");
        writer.WriteAttributeString("l", folderId);
        writer.WriteStartElement("default");
        writer.WriteAttributeString("ptst", appt["ptst"]);
        writer.WriteStartElement("m");

        if (isRecurring)    // Timezone nodes if recurring appt
        {
            WriteTimezone(writer, appt);
        }

        writer.WriteStartElement("inv");
        writer.WriteAttributeString("method", "REQUEST");
        writer.WriteAttributeString("fb", appt["fb"]);
        writer.WriteAttributeString("transp", appt["transp"]);
        writer.WriteAttributeString("allDay", appt["allDay"]);
        writer.WriteAttributeString("name", appt["name"]);
        writer.WriteAttributeString("loc", appt["loc"]);
        if (appt["uid"].Length > 0)
        {
            writer.WriteAttributeString("uid", appt["uid"]);
        }

        writer.WriteStartElement("s");
        writer.WriteAttributeString("d", appt["s"]);
        if (isRecurring)
        {
            writer.WriteAttributeString("tz", appt["tid"]);
        }
        writer.WriteEndElement();

        writer.WriteStartElement("e");
        writer.WriteAttributeString("d", appt["e"]);
        if (isRecurring)
        {
            writer.WriteAttributeString("tz", appt["tid"]);
        }
        writer.WriteEndElement();

        writer.WriteStartElement("or");
        writer.WriteAttributeString("d", appt["orName"]);

        // always convert -- not like old tool that gives you a choice
        string theOrganizer = AccountName;
        if (appt["orAddr"].Length > 0)
        {
            if (!IAmTheOrganizer(appt["orAddr"]))
            {
                theOrganizer = appt["orAddr"];
            }
        }
        writer.WriteAttributeString("a", theOrganizer);
        writer.WriteEndElement();
        //

        if (appt.ContainsKey("attendees"))
        {
            string[] tokens = appt["attendees"].Split(',');
            for (int i = 0; i < tokens.Length; i += 4)
            {
                writer.WriteStartElement("at");
                writer.WriteAttributeString("d",    tokens.GetValue(i).ToString());
                writer.WriteAttributeString("a",    tokens.GetValue(i + 1).ToString());
                writer.WriteAttributeString("role", tokens.GetValue(i + 2).ToString());
                writer.WriteAttributeString("ptst", tokens.GetValue(i + 3).ToString());
                writer.WriteEndElement();
            }
        }

        if (isRecurring)
        {
            writer.WriteStartElement("recur");
            writer.WriteStartElement("add");
            writer.WriteStartElement("rule");
            writer.WriteAttributeString("freq", appt["freq"]);
            writer.WriteStartElement("interval");
            writer.WriteAttributeString("ival", appt["ival"]);
            writer.WriteEndElement();   // interval
            if (appt.ContainsKey("wkday"))
            {
                writer.WriteStartElement("byday");
                string wkday = appt["wkday"];
                int len = wkday.Length;
                for (int i = 0; i < len; i += 2)
                {
                    writer.WriteStartElement("wkday");
                    writer.WriteAttributeString("day", wkday.Substring(i, 2));
                    writer.WriteEndElement();   //wkday
                }
                writer.WriteEndElement();   // byday
            }
            if (appt.ContainsKey("modaylist"))
            {
                writer.WriteStartElement("bymonthday");
                writer.WriteAttributeString("modaylist", appt["modaylist"]);
                writer.WriteEndElement();   // bymonthday
            }
            if (appt.ContainsKey("molist"))
            {
                writer.WriteStartElement("bymonth");
                writer.WriteAttributeString("molist", appt["molist"]);
                writer.WriteEndElement();   // bymonthday
            }
            if (appt.ContainsKey("poslist"))
            {
                writer.WriteStartElement("bysetpos");
                writer.WriteAttributeString("poslist", appt["poslist"]);
                writer.WriteEndElement();   // bymonthday
            }
            if (appt["count"].Length > 0)
            {
                writer.WriteStartElement("count");
                writer.WriteAttributeString("num", appt["count"]);
                writer.WriteEndElement();   // count
            }
            if (appt.ContainsKey("until"))
            {
                writer.WriteStartElement("until");
                writer.WriteAttributeString("d", appt["until"]);
                writer.WriteEndElement();   // until
            }
            writer.WriteEndElement();   // rule
            writer.WriteEndElement();   // add
            writer.WriteEndElement();   // recur
        }

        writer.WriteStartElement("alarm");
        writer.WriteAttributeString("action", "DISPLAY");
        writer.WriteStartElement("trigger");
        writer.WriteStartElement("rel");
        writer.WriteAttributeString("related", "START");
        writer.WriteAttributeString("neg", "1");
        writer.WriteAttributeString("m", appt["m"]);
        writer.WriteEndElement();   // rel
        writer.WriteEndElement();   // trigger
        writer.WriteEndElement();   // alarm

        writer.WriteEndElement();   // inv

        WriteNVPair(writer, "su", appt["su"]);

        writer.WriteStartElement("mp");
        writer.WriteAttributeString("ct", "multipart/alternative");
        writer.WriteStartElement("mp");
        writer.WriteAttributeString("ct", appt["contentType0"]);
        if (appt["content0"].Length > 0)
        {
            WriteNVPair(writer, "content", System.Text.Encoding.Default.GetString(File.ReadAllBytes(appt["content0"])));
        }
        writer.WriteEndElement();   // mp
        writer.WriteStartElement("mp");
        writer.WriteAttributeString("ct", appt["contentType1"]);
        if (appt["content1"].Length > 0)
        {
            WriteNVPair(writer, "content", System.Text.Encoding.Default.GetString(File.ReadAllBytes(appt["content1"])));
        }
    
        writer.WriteEndElement();   // mp
        writer.WriteEndElement();   // mp

        writer.WriteEndElement();   // m
        writer.WriteEndElement();   // default
        for (int i = 0; i < numExceptions; i++)
        {
            AddExceptionToRequest(writer, appt, i);
        }
        writer.WriteEndElement();   // SetAppointmentRequest

        DeleteApptTempFiles(appt, numExceptions);
    }

    private void AddExceptionToRequest(XmlWriter writer, Dictionary<string, string> appt, int num)
    {
        string attr = "exceptionType" + "_" + num.ToString();
        bool isCancel = appt[attr] == "cancel";
        if (isCancel)
        {
            writer.WriteStartElement("cancel");
        }
        else
        {
            writer.WriteStartElement("except");
        }
        attr = "ptst" + "_" + num.ToString();
        writer.WriteAttributeString("ptst", appt[attr]);
        writer.WriteStartElement("m");

        WriteTimezone(writer, appt); // timezone stuff since it is a recurrence

        writer.WriteStartElement("inv");
        if (!isCancel)
        {
            writer.WriteAttributeString("method", "REQUEST");
        }
        attr = "fb" + "_" + num.ToString();
        writer.WriteAttributeString("fb", appt[attr]);
        writer.WriteAttributeString("transp", "O");
        attr = "allDay" + "_" + num.ToString();
        writer.WriteAttributeString("allDay", appt[attr]);
        attr = "name" + "_" + num.ToString();
        writer.WriteAttributeString("name", appt[attr]);
        attr = "loc" + "_" + num.ToString();
        writer.WriteAttributeString("loc", appt[attr]);
        if (appt["uid"].Length > 0)
        {
            writer.WriteAttributeString("uid", appt["uid"]);
        }
        writer.WriteStartElement("s");
        attr = "s" + "_" + num.ToString();
        writer.WriteAttributeString("d", appt[attr]);
        writer.WriteAttributeString("tz", appt["tid"]);
        writer.WriteEndElement();
        if (!isCancel)
        {
            writer.WriteStartElement("e");
            attr = "e" + "_" + num.ToString();
            writer.WriteAttributeString("d", appt[attr]);
            writer.WriteAttributeString("tz", appt["tid"]);
            writer.WriteEndElement();
        }
        attr = "s" + "_" + num.ToString();
        if (appt[attr].Length > 0)
        {
            writer.WriteStartElement("exceptId");
            string exceptId = ComputeExceptId(appt[attr], appt["s"]);
            writer.WriteAttributeString("d", exceptId);
            writer.WriteAttributeString("tz", appt["tid"]);
            writer.WriteEndElement();   // exceptId
        }
        writer.WriteStartElement("or");
        attr = "orName" + "_" + num.ToString();
        writer.WriteAttributeString("d", appt[attr]);
        attr = "orAddr" + "_" + num.ToString();
        string theOrganizer = AccountName;
        if (appt[attr].Length > 0)
        {
            if (!IAmTheOrganizer(appt[attr]))
            {
                theOrganizer = appt[attr];
            }
        }
        writer.WriteAttributeString("a", theOrganizer);
        writer.WriteEndElement();
        if (!isCancel)
        {
            attr = "m" + "_" + num.ToString();
            writer.WriteStartElement("alarm");
            writer.WriteAttributeString("action", "DISPLAY");
            writer.WriteStartElement("trigger");
            writer.WriteStartElement("rel");
            writer.WriteAttributeString("related", "START");
            writer.WriteAttributeString("neg", "1");
            writer.WriteAttributeString("m", appt[attr]);
            writer.WriteEndElement();   // rel
            writer.WriteEndElement();   // trigger
            writer.WriteEndElement();   // alarm
        }
        writer.WriteEndElement();   // inv
        attr = "su" + "_" + num.ToString();
        WriteNVPair(writer, "su", appt[attr]);

        attr = "contentType0" + "_" + num.ToString();
        writer.WriteStartElement("mp");
        writer.WriteAttributeString("ct", "multipart/alternative");
        writer.WriteStartElement("mp");
        writer.WriteAttributeString("ct", appt[attr]);
        attr = "content0" + "_" + num.ToString();
        if (appt[attr].Length > 0)
        {
            WriteNVPair(writer, "content", System.Text.Encoding.Default.GetString(File.ReadAllBytes(appt[attr])));
        }

        attr = "contentType1" + "_" + num.ToString();
        writer.WriteEndElement();   // mp
        writer.WriteStartElement("mp");
        writer.WriteAttributeString("ct", appt[attr]);
        attr = "content1" + "_" + num.ToString();
        if (appt[attr].Length > 0)
        {
            WriteNVPair(writer, "content", System.Text.Encoding.Default.GetString(File.ReadAllBytes(appt[attr])));
        }        
        writer.WriteEndElement();   // mp
        writer.WriteEndElement();   // mp

        writer.WriteEndElement();   // m
        writer.WriteEndElement();   // except or cancel
    }

    public int AddAppointment(Dictionary<string, string> appt, string folderPath = "")
    {
        lastError = "";

        // Create in Calendar unless another folder was desired
        string folderId = "10";

        if (folderPath.Length > 0)
        {
            folderId = FindFolder(folderPath);
            if (folderId.Length == 0)
                return APPT_CREATE_FAILED_FLDR;
        }

        // //////
        WebServiceClient client = new WebServiceClient {
            Url = ZimbraValues.GetZimbraValues().Url, WSServiceType =
                WebServiceClient.ServiceType.Traditional
        };
        int retval = 0;
        StringBuilder sb = new StringBuilder();
        XmlWriterSettings settings = new XmlWriterSettings();

        settings.OmitXmlDeclaration = true;
        using (XmlWriter writer = XmlWriter.Create(sb, settings))
        {
            writer.WriteStartDocument();
            writer.WriteStartElement("soap", "Envelope",
                "http://www.w3.org/2003/05/soap-envelope");

            WriteHeader(writer, true, true, true);

            writer.WriteStartElement("Body", "http://www.w3.org/2003/05/soap-envelope");
            SetAppointmentRequest(writer, appt, folderId, -1);

            writer.WriteEndElement();           // soap body
            writer.WriteEndElement();           // soap envelope
            writer.WriteEndDocument();
        }
        string rsp = "";

        client.InvokeService(sb.ToString(), out rsp);
        retval = client.status;
        return retval;
    }

    private void WriteTimezone(XmlWriter writer, Dictionary<string, string> appt)
    {
        writer.WriteStartElement("tz");
        writer.WriteAttributeString("id", appt["tid"]);
        writer.WriteAttributeString("stdoff", appt["stdoff"]);
        writer.WriteAttributeString("dayoff", appt["dayoff"]);
        writer.WriteStartElement("standard");
        writer.WriteAttributeString("week", appt["sweek"]);
        writer.WriteAttributeString("wkday", appt["swkday"]);
        writer.WriteAttributeString("mon", appt["smon"]);
        writer.WriteAttributeString("hour", appt["shour"]);
        writer.WriteAttributeString("min", appt["smin"]);
        writer.WriteAttributeString("sec", appt["ssec"]);
        writer.WriteEndElement();   // standard
        writer.WriteStartElement("daylight");
        writer.WriteAttributeString("week", appt["dweek"]);
        writer.WriteAttributeString("wkday", appt["dwkday"]);
        writer.WriteAttributeString("mon", appt["dmon"]);
        writer.WriteAttributeString("hour", appt["dhour"]);
        writer.WriteAttributeString("min", appt["dmin"]);
        writer.WriteAttributeString("sec", appt["dsec"]);
        writer.WriteEndElement();   // daylight
        writer.WriteEndElement();   // tz
    }

    private string ComputeExceptId(string exceptDate, string originalDate)
    {
        string retval = exceptDate.Substring(0, 9);
        retval += originalDate.Substring(9, 6);
        return retval;
    }

    public void SetTaskRequest(XmlWriter writer, Dictionary<string, string> task,
        string folderId, int requestId)
    {
        bool isRecurring = task.ContainsKey("freq");
        writer.WriteStartElement("SetTaskRequest", "urn:zimbraMail");
        writer.WriteAttributeString("l", folderId);
        writer.WriteStartElement("default");
        writer.WriteAttributeString("ptst", "NE");  // we don't support Task Requests
        writer.WriteStartElement("m");

        /*
        // Timezone nodes if recurring appt
        if (isRecurring)
        {
            writer.WriteStartElement("tz");
            writer.WriteAttributeString("id", appt["tid"]);
            writer.WriteAttributeString("stdoff", appt["stdoff"]);
            writer.WriteAttributeString("dayoff", appt["dayoff"]);
            writer.WriteStartElement("standard");
            writer.WriteAttributeString("week", appt["sweek"]);
            writer.WriteAttributeString("wkday", appt["swkday"]);
            writer.WriteAttributeString("mon", appt["smon"]);
            writer.WriteAttributeString("hour", appt["shour"]);
            writer.WriteAttributeString("min", appt["smin"]);
            writer.WriteAttributeString("sec", appt["ssec"]);
            writer.WriteEndElement();   // standard
            writer.WriteStartElement("daylight");
            writer.WriteAttributeString("week", appt["dweek"]);
            writer.WriteAttributeString("wkday", appt["dwkday"]);
            writer.WriteAttributeString("mon", appt["dmon"]);
            writer.WriteAttributeString("hour", appt["dhour"]);
            writer.WriteAttributeString("min", appt["dmin"]);
            writer.WriteAttributeString("sec", appt["dsec"]);
            writer.WriteEndElement();   // daylight
            writer.WriteEndElement();   // tz
        }
        */

        writer.WriteStartElement("inv");
        writer.WriteAttributeString("status", task["status"]);
        writer.WriteAttributeString("method", "REQUEST");
        writer.WriteAttributeString("priority", task["priority"]);
        writer.WriteAttributeString("percentComplete", task["percentComplete"]);
        writer.WriteAttributeString("name", task["name"]);

        // hard code these -- probably fine
        writer.WriteAttributeString("allDay", "1");
        writer.WriteAttributeString("transp", "O");
        writer.WriteAttributeString("fb", "B");
        //

        if (task.ContainsKey("uid"))     // for now
        {
            writer.WriteAttributeString("uid", task["uid"]);
        }

        writer.WriteStartElement("s");
        writer.WriteAttributeString("d", task["s"]);
        //if (isRecurring)
        //{
        //    writer.WriteAttributeString("tz", task["tid"]);
        //}
        writer.WriteEndElement();

        writer.WriteStartElement("e");
        writer.WriteAttributeString("d", task["e"]);
        //if (isRecurring)
        //{
        //    writer.WriteAttributeString("tz", task["tid"]);
        //}
        writer.WriteEndElement();

        // hard code the organizer -- we don't support task requests
        writer.WriteStartElement("or");
        writer.WriteAttributeString("a", AccountName);
        writer.WriteEndElement();
        //

        if (isRecurring)
        {
            writer.WriteStartElement("recur");
            writer.WriteStartElement("add");
            writer.WriteStartElement("rule");
            writer.WriteAttributeString("freq", task["freq"]);
            writer.WriteStartElement("interval");
            writer.WriteAttributeString("ival", task["ival"]);
            writer.WriteEndElement();   // interval
            if (task.ContainsKey("wkday"))
            {
                writer.WriteStartElement("byday");
                string wkday = task["wkday"];
                int len = wkday.Length;
                for (int i = 0; i < len; i += 2)
                {
                    writer.WriteStartElement("wkday");
                    writer.WriteAttributeString("day", wkday.Substring(i, 2));
                    writer.WriteEndElement();   //wkday
                }
                writer.WriteEndElement();   // byday
            }
            if (task.ContainsKey("modaylist"))
            {
                writer.WriteStartElement("bymonthday");
                writer.WriteAttributeString("modaylist", task["modaylist"]);
                writer.WriteEndElement();   // bymonthday
            }
            if (task.ContainsKey("molist"))
            {
                writer.WriteStartElement("bymonth");
                writer.WriteAttributeString("molist", task["molist"]);
                writer.WriteEndElement();   // bymonthday
            }
            if (task.ContainsKey("poslist"))
            {
                writer.WriteStartElement("bysetpos");
                writer.WriteAttributeString("poslist", task["poslist"]);
                writer.WriteEndElement();   // bymonthday
            }
            if (task["count"].Length > 0)
            {
                writer.WriteStartElement("count");
                writer.WriteAttributeString("num", task["count"]);
                writer.WriteEndElement();   // count
            }
            if (task.ContainsKey("until"))
            {
                writer.WriteStartElement("until");
                writer.WriteAttributeString("d", task["until"]);
                writer.WriteEndElement();   // until
            }
            writer.WriteEndElement();   // rule
            writer.WriteEndElement();   // add
            writer.WriteEndElement();   // recur
        }       

        if (task["xp-TOTAL_WORK"].Length > 0)
        {
            writer.WriteStartElement("xprop");
            writer.WriteAttributeString("name", "X-ZIMBRA-TASK-TOTAL-WORK");
            writer.WriteAttributeString("value", task["xp-TOTAL_WORK"]);
            writer.WriteEndElement();   // xprop
        }
        if (task["xp-ACTUAL_WORK"].Length > 0)
        {
            writer.WriteStartElement("xprop");
            writer.WriteAttributeString("name", "X-ZIMBRA-TASK-ACTUAL-WORK");
            writer.WriteAttributeString("value", task["xp-ACTUAL_WORK"]);
            writer.WriteEndElement();   // xprop
        }
        if (task["xp-COMPANIES"].Length > 0)
        {
            writer.WriteStartElement("xprop");
            writer.WriteAttributeString("name", "X-ZIMBRA-TASK-COMPANIES");
            writer.WriteAttributeString("value", task["xp-COMPANIES"]);
            writer.WriteEndElement();   // xprop
        }
        if (task["xp-MILEAGE"].Length > 0)
        {
            writer.WriteStartElement("xprop");
            writer.WriteAttributeString("name", "X-ZIMBRA-TASK-MILEAGE");
            writer.WriteAttributeString("value", task["xp-MILEAGE"]);
            writer.WriteEndElement();   // xprop
        }
        if (task["xp-BILLING"].Length > 0)
        {
            writer.WriteStartElement("xprop");
            writer.WriteAttributeString("name", "X-ZIMBRA-TASK-BILLING");
            writer.WriteAttributeString("value", task["xp-BILLING"]);
            writer.WriteEndElement();   // xprop
        }

        writer.WriteEndElement();   // inv

        WriteNVPair(writer, "su", task["su"]);

        writer.WriteStartElement("mp");
        writer.WriteAttributeString("ct", "multipart/alternative");
        writer.WriteStartElement("mp");
        writer.WriteAttributeString("ct", task["contentType0"]);
        if (task["content0"].Length > 0)
        {
            WriteNVPair(writer, "content", System.Text.Encoding.Default.GetString(File.ReadAllBytes(task["content0"])));
            File.Delete(task["content0"]);
        }
        writer.WriteEndElement();   // mp
        writer.WriteStartElement("mp");
        writer.WriteAttributeString("ct", task["contentType1"]);
        if (task["content1"].Length > 0)
        {
            WriteNVPair(writer, "content", System.Text.Encoding.Default.GetString(File.ReadAllBytes(task["content1"])));
            File.Delete(task["content1"]);
        }

        writer.WriteEndElement();   // mp
        writer.WriteEndElement();   // mp

        writer.WriteEndElement();   // m
        writer.WriteEndElement();   // default
        writer.WriteEndElement();   // SetTaskRequest
    }

    public int AddTask(Dictionary<string, string> appt, string folderPath = "")
    {
        lastError = "";

        // Create in Tasks unless another folder was desired
        string folderId = "15";

        if (folderPath.Length > 0)
        {
            folderId = FindFolder(folderPath);
            if (folderId.Length == 0)
                return TASK_CREATE_FAILED_FLDR;
        }

        // //////
        WebServiceClient client = new WebServiceClient
        {
            Url = ZimbraValues.GetZimbraValues().Url,
            WSServiceType =
                WebServiceClient.ServiceType.Traditional
        };
        int retval = 0;
        StringBuilder sb = new StringBuilder();
        XmlWriterSettings settings = new XmlWriterSettings();

        settings.OmitXmlDeclaration = true;
        using (XmlWriter writer = XmlWriter.Create(sb, settings))
        {
            writer.WriteStartDocument();
            writer.WriteStartElement("soap", "Envelope",
                "http://www.w3.org/2003/05/soap-envelope");

            WriteHeader(writer, true, true, true);

            writer.WriteStartElement("Body", "http://www.w3.org/2003/05/soap-envelope");
            SetTaskRequest(writer, appt, folderId, -1);

            writer.WriteEndElement();           // soap body
            writer.WriteEndElement();           // soap envelope
            writer.WriteEndDocument();
        }
        string rsp = "";

        client.InvokeService(sb.ToString(), out rsp);
        retval = client.status;
        return retval;
    }

    private void CreateFolderRequest(XmlWriter writer, ZimbraFolder folder, int requestId)
    {
        writer.WriteStartElement("CreateFolderRequest", "urn:zimbraMail");
        if (requestId != -1)
            writer.WriteAttributeString("requestId", requestId.ToString());
        writer.WriteStartElement("folder");
        writer.WriteAttributeString("name", folder.name);
        writer.WriteAttributeString("l", folder.parent);
        writer.WriteAttributeString("fie", "1");        // return the existing ID instead of an error
        if (folder.view.Length > 0)
            writer.WriteAttributeString("view", folder.view);
        if (folder.color.Length > 0)
            writer.WriteAttributeString("color", folder.color);
        if (folder.flags.Length > 0)
            writer.WriteAttributeString("f", folder.flags);
        writer.WriteEndElement();               // folder
        writer.WriteEndElement();               // CreateFolderRequest
    }

    private int DoCreateFolder(ZimbraFolder folder, out string folderID)
    {
        folderID = "";
        lastError = "";

        int retval = 0;
        WebServiceClient client = new WebServiceClient {
            Url = ZimbraValues.GetZimbraValues().Url, WSServiceType =
                WebServiceClient.ServiceType.Traditional
        };
        StringBuilder sb = new StringBuilder();
        XmlWriterSettings settings = new XmlWriterSettings();

        settings.OmitXmlDeclaration = true;
        using (XmlWriter writer = XmlWriter.Create(sb, settings)) {
            writer.WriteStartDocument();
            writer.WriteStartElement("soap", "Envelope",
                "http://www.w3.org/2003/05/soap-envelope");

            WriteHeader(writer, true, true, true);

            writer.WriteStartElement("Body", "http://www.w3.org/2003/05/soap-envelope");

            CreateFolderRequest(writer, folder, -1);

            writer.WriteEndElement();           // soap body
            writer.WriteEndElement();           // soap envelope
            writer.WriteEndDocument();
        }

        string rsp = "";

        client.InvokeService(sb.ToString(), out rsp);
        retval = client.status;
        if (client.status == 0)
        {
            ParseCreateFolder(rsp, out folderID);       // get the id
        }
        else
        {
            string soapReason = ParseSoapFault(client.errResponseMessage);

            if (soapReason.Length > 0)
                lastError = soapReason;
            else
                lastError = client.exceptionMessage;
        }
        return retval;
    }

    private bool GetParentAndChild(string fullPath, out string parent, out string child)
    {
        parent = "";
        child = "";

        // break up the folder name and parent from the path
        int lastSlash = fullPath.LastIndexOf("/");

        if (lastSlash == -1)
            return false;

        int folderNameStart = lastSlash + 1;
        int len = fullPath.Length;

        parent = fullPath.Substring(0, lastSlash);
        child = fullPath.Substring(folderNameStart, (len - folderNameStart));
        //

        return true;
    }

    private string FindFolder(string folderPath)
    {
        // first look if the folder is in the map.  If it is, return the id
        if (dFolderMap.ContainsKey(folderPath))
            return dFolderMap[folderPath];
        // wasn't in the map. See if it's a special folder
        return GetSpecialFolderNum(folderPath);
    }

    public int CreateFolder(string FolderPath, string View = "", string Color = "", string
        Flags = "")
    {
        string parentPath = "";
        string folderName = "";

        if (!GetParentAndChild(FolderPath, out parentPath, out folderName))
            return FOLDER_CREATE_FAILED_SYN;

        // first look in the special folders array
        // if it's not there, look in the map
        string strParentNum = GetSpecialFolderNum(parentPath);

        if (strParentNum.Length == 0)
        {
            if (dFolderMap.ContainsKey(parentPath))
                strParentNum = dFolderMap[parentPath];
            else
                return FOLDER_CREATE_FAILED_SEM;
        }

        string folderID = "";
        int dcfReturnVal = DoCreateFolder(new ZimbraFolder(folderName, strParentNum, View,
            Color, Flags), out folderID);

        if (dcfReturnVal == 0)
            dFolderMap.Add(FolderPath, folderID);
        return dcfReturnVal;
    }

    private bool IAmTheOrganizer(string theOrganizer)
    {
        int idxAcc = AccountName.IndexOf("@");
        int idxOrg = theOrganizer.IndexOf("@");
        if ((idxAcc == -1) || (idxOrg == -1))   // can happen if no recip table
        {
            return false;
        }
        string nameAcc = AccountName.Substring(0, idxAcc);
        string nameOrg = theOrganizer.Substring(0, idxOrg);
        return (nameAcc == nameOrg);
    }

    private void DeleteApptTempFiles(Dictionary<string, string> appt, int numExceptions)
    {
        string attr = "";
        if (appt["content0"].Length > 0)
        {
            File.Delete(appt["content0"]);
        }
        if (appt["content1"].Length > 0)
        {
            File.Delete(appt["content1"]);
        }
        for (int i = 0; i < numExceptions; i++)
        {
            attr = "content0" + "_" + i.ToString();
            if (appt[attr].Length > 0)
            {
                if (File.Exists(appt[attr]))
                {
                    File.Delete(appt[attr]);
                }
            }
            attr = "content1" + "_" + i.ToString();
            if (appt[attr].Length > 0)
            {
                if (File.Exists(appt[attr]))
                {
                    File.Delete(appt[attr]);
                }
            }
        }
    }
    // ///////////////////////
}
}
