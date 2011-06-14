using System.Collections.Generic;
using System.Xml.Linq;
using System.Linq;
using System.Text;
using System.Xml;
using System.IO;

namespace CssLib
{
    public class ZimbraAPI
    {
        internal const int ZIMBRA_API_LOGON = 1;
        internal const int ZIMBRA_API_GETINFO = 2;
        internal const int ZIMBRA_API_GETALLDOMAIN = 3;
        internal const int ZIMBRA_API_GETDOMAIN = 4;
        internal const int ZIMBRA_API_GETALLCOS = 5;

        private string _soapEnvelope =
          "<soap:Envelope xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\"><soap:Header></soap:Header><soap:Body></soap:Body></soap:Envelope>";

        private string WebMethod { get; set; }

        private class Parameter
        {
            public string Name { get; set; }
            public string Value { get; set; }
        }
        private List<Parameter> Parameters { get; set; }

        private ZimbraValues zValues;
        public ZimbraValues ZValues
        {
            get { return zValues; }
            set
            {
                zValues = value;
            }
        }

        public ZimbraAPI()
        {
            zValues = new ZimbraValues();
        }

        // Method for a given soap envelope
        private string CreateSoapEnvelope(int apiCall)
        {
            string hdr = "";
            string MethodCall = "";

            switch (apiCall)
            {
                case ZimbraAPI.ZIMBRA_API_LOGON:
                    hdr = "<context xmlns=\"urn:zimbra\"><nonotify/><noqualify/><nosession/></context>";
                    MethodCall = "<" + this.WebMethod + @" xmlns=""urn:zimbraAdmin"">";
                    string StrParameters = string.Empty;
                    if (this.Parameters != null)
                    {
                        foreach (Parameter param in this.Parameters)
                        {
                            StrParameters = StrParameters + "<" + param.Name + ">" + param.Value + "</" + param.Name + ">";
                        }
                    }
                    MethodCall = MethodCall + StrParameters + "</" + this.WebMethod + ">";
                    break;

                case ZimbraAPI.ZIMBRA_API_GETINFO:
                    hdr = "<context xmlns=\"urn:zimbra\"><nonotify/><noqualify/><nosession/><sessionId></sessionId><authToken>";
                    hdr += zValues.AuthToken;   // set by ParseLogon
                    hdr += "</authToken></context>";
                    //MethodCall = "<" + this.WebMethod + @" xmlns=""urn:zimbraAccount""/>";
                    MethodCall = "<" + this.WebMethod + @" xmlns=""urn:zimbraAccount"" sections=""mbox""/>";
                    break;

                case ZimbraAPI.ZIMBRA_API_GETALLDOMAIN:
                    hdr = "<context xmlns=\"urn:zimbra\"><nonotify/><noqualify/><nosession/><sessionId></sessionId><authToken>";
                    hdr += zValues.AuthToken;   // set by ParseLogon
                    hdr += "</authToken></context>";
                    MethodCall = "<" + this.WebMethod + @" xmlns=""urn:zimbraAdmin""/>";
                    break;

                case ZimbraAPI.ZIMBRA_API_GETALLCOS:
                    hdr = "<context xmlns=\"urn:zimbra\"><nonotify/><noqualify/><nosession/><sessionId></sessionId><authToken>";
                    hdr += zValues.AuthToken;   // set by ParseLogon
                    hdr += "</authToken></context>";
                    MethodCall = "<" + this.WebMethod + @" xmlns=""urn:zimbraAdmin""/>";
                    break;
            }

            StringBuilder sb = new StringBuilder(_soapEnvelope);

            sb.Insert(sb.ToString().IndexOf("</soap:Header>"), hdr);
            sb.Insert(sb.ToString().IndexOf("</soap:Body>"), MethodCall);
            return sb.ToString();
        }

        // Parse Methods //////////////////
        private void ParseLogon(string rsp)
        {
            string authToken = "";
            string isDomainAdmin = "false";
            if (rsp != null)
            {
                int startIdx = rsp.IndexOf("<authToken>");
                if (startIdx != -1)
                {
                    XDocument xmlDoc = XDocument.Parse(rsp);
                    XNamespace ns = "urn:zimbraAdmin";
                    foreach (var objIns in xmlDoc.Descendants(ns + "AuthResponse"))
                    {
                        authToken += objIns.Element(ns + "authToken").Value;
                        isDomainAdmin = "false";
                        var x = from a in objIns.Elements(ns + "a")
                                where a.Attribute("n").Value == "zimbraIsDomainAdminAccount"
                                select a.Value;
                        isDomainAdmin = x.ElementAt(0);
                    }
                }
            }
            zValues.AuthToken = authToken;
            zValues.IsAdminAccount = true;
            zValues.IsDomainAdminAccount = (isDomainAdmin == "true");
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
            zValues.AccountName = accountName;
            zValues.ServerVersion = serverVersion;
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
                                {
                                    zValues.Domains.Add(domainAttr.Value);
                                }
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
                            foreach (XAttribute cosAttr in cosIns.Attributes())
                            {
                                if (cosAttr.Name == "name")
                                {
                                    zValues.COSes.Add(cosAttr.Value);
                                }
                            }
                        }
                    }
                }
            }
        }
        //////////

        // API methods /////////
        public int Logon(string username, string password, string url)
        {
            string req = "";
            string rsp = "";
            WebMethod = "AuthRequest";
            Parameters = new List<Parameter>();
            Parameters.Add(new Parameter { Name = "name", Value = username });
            Parameters.Add(new Parameter { Name = "password", Value = password });
            WebServiceClient client = new WebServiceClient
            {
                Url = url,
                WSServiceType = WebServiceClient.ServiceType.Traditional
            };
            req = CreateSoapEnvelope(ZIMBRA_API_LOGON);
            client.InvokeService(req, out rsp);
            ParseLogon(rsp);
            return client.status;
        }

        public int GetInfo(string authToken, string url)
        {
            string req = "";
            string rsp = "";
            WebMethod = "GetInfoRequest";
            Parameters = null;
            WebServiceClient client = new WebServiceClient
            {   
                Url = url,
                WSServiceType = WebServiceClient.ServiceType.Traditional
            };
            req = CreateSoapEnvelope(ZIMBRA_API_GETINFO);
            client.InvokeService(req, out rsp);
            ParseGetInfo(rsp);
            return client.status;
        }

        public int GetAllDomains(string authToken, string url)
        {
            string req = "";
            string rsp = "";
            WebMethod = "GetAllDomainsRequest";
            WebServiceClient client = new WebServiceClient
            {
                Url = url,
                WSServiceType = WebServiceClient.ServiceType.Traditional
            };
            req = CreateSoapEnvelope(ZIMBRA_API_GETALLDOMAIN);
            client.InvokeService(req, out rsp);
            ParseGetAllDomain(rsp);
            return client.status;
        }

        public int GetAllCos(string authToken, string url)
        {
            string req = "";
            string rsp = "";
            WebMethod = "GetAllCosRequest";
            WebServiceClient client = new WebServiceClient
            {
                Url = url,
                WSServiceType = WebServiceClient.ServiceType.Traditional
            };
            req = CreateSoapEnvelope(ZIMBRA_API_GETALLCOS);
            client.InvokeService(req, out rsp);
            ParseGetAllCos(rsp);
            return client.status;
        }
        /////////////////////////

    }
}

