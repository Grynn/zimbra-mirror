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
        internal const int ZIMBRA_API_LOGONA = 1;
        internal const int ZIMBRA_API_LOGONU = 2;
        internal const int ZIMBRA_API_GETINFO = 3;
        internal const int ZIMBRA_API_GETALLDOMAIN = 4;
        internal const int ZIMBRA_API_GETDOMAIN = 5;
        internal const int ZIMBRA_API_GETALLCOS = 6;

        private string _soapEnvelope =
          "<soap:Envelope xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\"><soap:Header></soap:Header><soap:Body></soap:Body></soap:Envelope>";

        private string WebMethod { get; set; }

        private class Attrib
        {
            public string Name { get; set; }
            public string Value { get; set; }
        }

        private class Parameter
        {
            public string Name { get; set; }
            public string Value { get; set; }
            public Attrib Attr { get; set; }
        }
        private List<Parameter> Parameters { get; set; }

        private string lastError;
        public string LastError
        {
            get { return lastError; }
            set
            {
                lastError = value;
            }
        }

        public ZimbraAPI()
        {
            ZimbraValues.GetZimbraValues();
        }

        // Method for a given soap envelope
        private string CreateSoapEnvelope(int apiCall)
        {
            string hdr = "";
            string MethodCall = "";
            string StrParameters = string.Empty;

            switch (apiCall)
            {
                case ZimbraAPI.ZIMBRA_API_LOGONA:
                    hdr = "<context xmlns=\"urn:zimbra\"><nonotify/><noqualify/><nosession/></context>";
                    MethodCall = "<" + this.WebMethod + @" xmlns=""urn:zimbraAdmin"">";
                    if (this.Parameters != null)
                    {
                        foreach (Parameter param in this.Parameters)
                        {
                            StrParameters = StrParameters + "<" + param.Name + ">" + param.Value + "</" + param.Name + ">";
                        }
                    }
                    MethodCall = MethodCall + StrParameters + "</" + this.WebMethod + ">";
                    break;

                case ZimbraAPI.ZIMBRA_API_LOGONU:
                    hdr = "<context xmlns=\"urn:zimbra\"><nonotify/><noqualify/><nosession/></context>";
                    MethodCall = "<" + this.WebMethod + @" xmlns=""urn:zimbraAccount"">";
                    if (this.Parameters != null)
                    {
                        foreach (Parameter param in this.Parameters)
                        {
                            if (param.Attr != null)
                            {
                                StrParameters = StrParameters + "<" + param.Name + " " + param.Attr.Name + "=" + "\"" + param.Attr.Value + "\">"
                                                                    + param.Value + "</" + param.Name + ">";
                            }
                            else
                            {
                                StrParameters = StrParameters + "<" + param.Name + ">" + param.Value + "</" + param.Name + ">";
                            }
                        }
                    }
                    MethodCall = MethodCall + StrParameters + "</" + this.WebMethod + ">";
                    break;

                case ZimbraAPI.ZIMBRA_API_GETINFO:
                    hdr = "<context xmlns=\"urn:zimbra\"><nonotify/><noqualify/><nosession/><sessionId></sessionId><authToken>";
                    hdr += ZimbraValues.GetZimbraValues().AuthToken;   // set by ParseLogon
                    hdr += "</authToken></context>";
                    //MethodCall = "<" + this.WebMethod + @" xmlns=""urn:zimbraAccount""/>";
                    MethodCall = "<" + this.WebMethod + @" xmlns=""urn:zimbraAccount"" sections=""mbox""/>";
                    break;

                case ZimbraAPI.ZIMBRA_API_GETALLDOMAIN:
                    hdr = "<context xmlns=\"urn:zimbra\"><nonotify/><noqualify/><nosession/><sessionId></sessionId><authToken>";
                    hdr += ZimbraValues.GetZimbraValues().AuthToken;   // set by ParseLogon
                    hdr += "</authToken></context>";
                    MethodCall = "<" + this.WebMethod + @" xmlns=""urn:zimbraAdmin""/>";
                    break;

                case ZimbraAPI.ZIMBRA_API_GETALLCOS:
                    hdr = "<context xmlns=\"urn:zimbra\"><nonotify/><noqualify/><nosession/><sessionId></sessionId><authToken>";
                    hdr += ZimbraValues.GetZimbraValues().AuthToken;   // set by ParseLogon
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
        private string ParseSoapFault(string rsperr)
        {
            if (rsperr.Length == 0)
            {
                return "";
            }
            if (rsperr.IndexOf("<soap:Fault>") == -1)
            {
                return "";
            }
            string soapReason = "";

            XDocument xmlDoc = XDocument.Parse(rsperr);            
            XNamespace ns = "http://www.w3.org/2003/05/soap-envelope"; 
            IEnumerable<XElement> de =
                from el in xmlDoc.Descendants()
                    select el;
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
                            var x = from a in objIns.Elements(ns + "a")
                                    where a.Attribute("n").Value == "zimbraIsDomainAdminAccount"
                                    select a.Value;
                            isDomainAdmin = x.ElementAt(0);
                        }
                    }
                }
            }
            ZimbraValues.GetZimbraValues().AuthToken = authToken;
            ZimbraValues.GetZimbraValues().IsAdminAccount = true;
            ZimbraValues.GetZimbraValues().IsDomainAdminAccount = (isDomainAdmin == "true");
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
            ZimbraValues.GetZimbraValues().AccountName = accountName;
            ZimbraValues.GetZimbraValues().ServerVersion = serverVersion;
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
                                    ZimbraValues.GetZimbraValues().Domains.Add(domainAttr.Value);
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
                                    ZimbraValues.GetZimbraValues().COSes.Add(cosAttr.Value);
                                }
                            }
                        }
                    }
                }
            }
        }
        //////////

        // API methods /////////
        public int Logon(string hostname, string port, string username, string password, bool isAdmin)
        {
            lastError = "";
            string req = "";
            string rsp = "";
            WebMethod = "AuthRequest";
            Parameters = new List<Parameter>();
            if (isAdmin)
            {
                ZimbraValues.GetZimbraValues().Url = "https://" + hostname + ":" + port + "/service/admin/soap";
                Parameters.Add(new Parameter { Name = "name", Value = username, Attr = null });
            }
            else
            {
                ZimbraValues.GetZimbraValues().Url = "http://" + hostname + ":" + port + "/service/soap";
                Parameters.Add(new Parameter { Name = "account", Value = username, Attr = new Attrib { Name = "by", Value = "name" } });
            }
            Parameters.Add(new Parameter { Name = "password", Value = password, Attr = null });

            WebServiceClient client = new WebServiceClient
            {
                Url = ZimbraValues.GetZimbraValues().Url,
                WSServiceType = WebServiceClient.ServiceType.Traditional
            };
            int apiCall = (isAdmin) ? ZIMBRA_API_LOGONA : ZIMBRA_API_LOGONU;
            req = CreateSoapEnvelope(apiCall);
            client.InvokeService(req, out rsp);
            if (client.status == 0)
            {
                ParseLogon(rsp, isAdmin);
            }
            else
            {
                string soapReason = ParseSoapFault(client.errResponseMessage);
                if (soapReason.Length > 0)
                {
                    lastError = soapReason;
                }
                else
                {
                    lastError = client.exceptionMessage;
                }
            }
            return client.status;

        }

        public int GetInfo()
        {
            lastError = "";
            string req = "";
            string rsp = "";
            WebMethod = "GetInfoRequest";
            Parameters = null;
            WebServiceClient client = new WebServiceClient
            {
                Url = ZimbraValues.GetZimbraValues().Url,
                WSServiceType = WebServiceClient.ServiceType.Traditional
            };
            req = CreateSoapEnvelope(ZIMBRA_API_GETINFO);
            client.InvokeService(req, out rsp);
            if (client.status == 0)
            {
                ParseGetInfo(rsp);
            }
            else
            {
                string soapReason = ParseSoapFault(client.errResponseMessage);
                if (soapReason.Length > 0)
                {
                    lastError = soapReason;
                }
                else
                {
                    lastError = client.exceptionMessage;
                }
            }
            return client.status;
        }

        public int GetAllDomains()
        {
            lastError = "";
            string req = "";
            string rsp = "";
            WebMethod = "GetAllDomainsRequest";
            WebServiceClient client = new WebServiceClient
            {
                Url = ZimbraValues.GetZimbraValues().Url,
                WSServiceType = WebServiceClient.ServiceType.Traditional
            };
            req = CreateSoapEnvelope(ZIMBRA_API_GETALLDOMAIN);
            client.InvokeService(req, out rsp);
            if (client.status == 0)
            {
                ParseGetAllDomain(rsp);
            }
            else
            {
                string soapReason = ParseSoapFault(client.errResponseMessage);
                if (soapReason.Length > 0)
                {
                    lastError = soapReason;
                }
                else
                {
                    lastError = client.exceptionMessage;
                }
            }
            return client.status;
        }

        public int GetAllCos()
        {
            lastError = "";
            string req = "";
            string rsp = "";
            WebMethod = "GetAllCosRequest";
            WebServiceClient client = new WebServiceClient
            {
                Url = ZimbraValues.GetZimbraValues().Url,
                WSServiceType = WebServiceClient.ServiceType.Traditional
            };
            req = CreateSoapEnvelope(ZIMBRA_API_GETALLCOS);
            client.InvokeService(req, out rsp);
            if (client.status == 0)
            {
                ParseGetAllCos(rsp);
            }
            else
            {
                string soapReason = ParseSoapFault(client.errResponseMessage);
                if (soapReason.Length > 0)
                {
                    lastError = soapReason;
                }
                else
                {
                    lastError = client.exceptionMessage;
                }
            }
            return client.status;
        }
        /////////////////////////

    }
}

