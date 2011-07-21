using System;
using System.Net;
using System.Net.Security;
using System.Security.Cryptography.X509Certificates;
using System.IO;
using System.Text;

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

        public HttpStatusCode httpStatusCode;
        public string httpStatusDescription;
        public string exceptionMessage;
        public string errResponseMessage;

        private void setErrors(System.Net.WebException wex)
        {
            status = (int)wex.Status;
            exceptionMessage = wex.Message;

            if (wex.Response != null )
            {
                httpStatusCode = ((HttpWebResponse)wex.Response).StatusCode;
                httpStatusDescription = ((HttpWebResponse)wex.Response).StatusDescription;
                HttpWebResponse errResponse = (HttpWebResponse)wex.Response;
                long rlen = errResponse.ContentLength;
                Stream ReceiveStream = errResponse.GetResponseStream();
                Encoding encode = System.Text.Encoding.GetEncoding("utf-8");
                StreamReader readStream = new StreamReader(ReceiveStream, encode);
                Char[] utf8Msg = new Char[rlen];
                int count = readStream.Read(utf8Msg, 0, (int)rlen);
                errResponseMessage = new string(utf8Msg);
            }
        }
        
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
            exceptionMessage = "";
            errResponseMessage = "";
            
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
            try
            {
                using (Stream stm = webReq.GetRequestStream())
                {
                    using (StreamWriter stmw = new StreamWriter(stm))
                    {
                        stmw.Write(req);
                        stmw.Close();
                    }

                }
            }
            catch (System.Net.WebException wex)
            //catch (Exception ex)
            {
                setErrors(wex);
                rsp = "";
                return;
            }

            //get the response from the web service
            try
            {
                response = webReq.GetResponse();
            }
            catch (System.Net.WebException wex)
            //catch (Exception ex)
            {
                setErrors(wex);
                rsp = "";
                return;
            }

            Stream str = response.GetResponseStream();
            StreamReader sr = new StreamReader(str);
            strResponse = sr.ReadToEnd();

            status = 0;
            rsp = strResponse;
        }

        private HttpWebRequest CreateWebRequestRaw(string authtoken)
        {
            CookieContainer cookieContainer = new CookieContainer();
            Cookie cookie = new Cookie("ZM_ADMIN_AUTH_TOKEN", authtoken);
            cookieContainer.Add(new Uri(this.Url), cookie);

            HttpWebRequest webRequest = (HttpWebRequest)WebRequest.Create(this.Url);
            webRequest.CookieContainer = cookieContainer;
            webRequest.UserAgent = "Zimbra Systems Client";
            webRequest.Method = "POST";
            return webRequest;
        }

        public void InvokeUploadService(string authtoken, string filePath, out string rsp)
        {
            WebResponse response = null;
            string strResponse = "";
            status = 0;
            exceptionMessage = "";
            errResponseMessage = "";

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
            HttpWebRequest webReq = this.CreateWebRequestRaw(authtoken);

            string boundary = "--B-00=_" + DateTime.Now.Ticks.ToString("x");
            string endBoundary = Environment.NewLine + Environment.NewLine + "--" + boundary + "--" + Environment.NewLine;

            webReq.ContentType = "multipart/form-data; boundary=" + boundary;

            string contentDisposition1 = "--" + boundary + Environment.NewLine +
                                         "Content-Disposition: form-data; name=\"requestId\"" +
                                         Environment.NewLine + Environment.NewLine +
                                         "lsrpc32-client-id" + Environment.NewLine;

            string contentDisposition2 = "--" + boundary + Environment.NewLine +                                            
                                         "Content-Disposition : form-data; name=\"lslib32\"; filename=\"lslib32.bin\"";

            string contentType = Environment.NewLine + "Content-Type: application/octet-stream" + Environment.NewLine;
            string contentTransfer = "Content-Transfer-Encoding: binary" + Environment.NewLine + Environment.NewLine;

            try
            {
                StreamReader stmr = new StreamReader(filePath);
                string fileContents = stmr.ReadToEnd();
                int fcLen = fileContents.Length;
                int buflen = fcLen + 400;

                using (Stream stm = webReq.GetRequestStream())
                {
                    using (StreamWriter stmw = new StreamWriter(stm, System.Text.Encoding.Default, buflen))
                    {
                        stmw.Write(contentDisposition1);
                        stmw.Write(contentDisposition2);
                        stmw.Write(contentType);
                        stmw.Write(contentTransfer);
                        stmw.Write(fileContents);                      
                        stmw.Write(endBoundary);
                        stmr.Close();
                        stmw.Close();
                    }
                }
            }
            catch (System.Net.WebException wex)
            //catch (Exception ex)
            {
                setErrors(wex);
                rsp = "";
                return;
            }

            //get the response from the web service
            try
            {
                response = webReq.GetResponse();
            }
            catch (System.Net.WebException wex)
            //catch (Exception ex)
            {
                setErrors(wex);
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
