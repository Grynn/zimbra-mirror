using System;
using System.Net;
using System.Net.Security;
using System.Security.Cryptography.X509Certificates;
using System.IO;

namespace CssLib
{
    public class WebServiceClient
    {

        public enum ServiceType
        {
            Traditional = 0,
            WCF = 1
        }
        public ServiceType WSServiceType { get; set; }
        
        public string Url { get; set; }

        public int status;
        
        private HttpWebRequest CreateWebRequest()
        {
            HttpWebRequest webRequest = (HttpWebRequest)WebRequest.Create(this.Url);
            webRequest.ContentType = "application/soap+xml; charset=\"utf-8\"";
            webRequest.UserAgent = "Zimbra Systems Client";
            webRequest.Method = "POST";
            return webRequest;
        } 

        public void InvokeService(string req, out string rsp)
        {
            WebResponse response = null;
            string strResponse = "";
            status = 0;
            
            ServicePointManager.ServerCertificateValidationCallback =
            new RemoteCertificateValidationCallback(
                delegate(
                object sender2,
                X509Certificate certificate,
                X509Chain chain,
                SslPolicyErrors sslPolicyErrors)
            {
                return true;
            });
            
            //Create the request
            HttpWebRequest webReq = this.CreateWebRequest();

            //write the soap envelope to request stream (req is the soap envelope)
            using (Stream stm = webReq.GetRequestStream())
            {
                using (StreamWriter stmw = new StreamWriter(stm))
                {
                    stmw.Write(req);
                }
            }

            //get the response from the web service
            try
            {
                response = webReq.GetResponse();
            }
            catch (System.Net.WebException wex)
            //catch (Exception ex)
            {
                Console.WriteLine(wex.Message);
                Console.WriteLine(wex.Status);
                status = (int)wex.Status;
                rsp = "";
                return;
            }

            Stream str = response.GetResponseStream();
            StreamReader sr = new StreamReader(str);
            strResponse = sr.ReadToEnd();

            status = 0;
            rsp = strResponse;
        }
 
    }
}
