using System.IO;
using System.Net.Security;
using System.Net;
using System.Security.Cryptography.X509Certificates;
using System.Text;
using System;

namespace CssLib
{
public class WebServiceClient
{
    public enum ServiceType
    {
        Traditional = 0, WCF = 1
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
        if (wex.Response != null)
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
        webRequest.Proxy = null;
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
            new RemoteCertificateValidationCallback(delegate(object sender2, X509Certificate
            certificate, X509Chain chain, SslPolicyErrors sslPolicyErrors) {
                return true;
            }
            );

        // Create the request
        HttpWebRequest webReq = this.CreateWebRequest();

        // write the soap envelope to request stream (req is the soap envelope)
        try
        {
            using (Stream stm = webReq.GetRequestStream()) {
                using (StreamWriter stmw = new StreamWriter(stm)) {
                    stmw.Write(req);
                    stmw.Close();
                }
            }
        }
        catch (System.Net.WebException wex)
        {
            // catch (Exception ex)
            setErrors(wex);
            rsp = "";
            return;
        }
        // get the response from the web service
        try
        {
            response = webReq.GetResponse();
        }
        catch (System.Net.WebException wex)
        {
            // catch (Exception ex)
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

    private HttpWebRequest CreateWebRequestRaw(string authtoken, bool isSecure)
    {
        CookieContainer cookieContainer = new CookieContainer();
        Cookie cookie = (isSecure) ? new Cookie("ZM_ADMIN_AUTH_TOKEN", authtoken) : new Cookie(
            "ZM_AUTH_TOKEN", authtoken);

        cookieContainer.Add(new Uri(this.Url), cookie);

        HttpWebRequest webRequest = (HttpWebRequest)WebRequest.Create(this.Url);

        webRequest.CookieContainer = cookieContainer;
        webRequest.UserAgent = "Zimbra Systems Client";
        webRequest.Method = "POST";
        webRequest.Proxy = null;
        return webRequest;
    }

    public void InvokeUploadService(string authtoken, bool isSecure, string filePath, string mimebuffer, int mode,
        out string rsp)
    {
        //Log.debug("Start InvokeUploadService");
        bool bIsBuffer=false;
        if (mimebuffer.Length > 0)
            bIsBuffer = true;
        WebResponse response = null;
        string strResponse = "";

        status = 0;
        exceptionMessage = "";
        errResponseMessage = "";

        ServicePointManager.ServerCertificateValidationCallback =
            new RemoteCertificateValidationCallback(delegate(object sender2, X509Certificate
            certificate, X509Chain chain, SslPolicyErrors sslPolicyErrors) {
                return true;
            }
            );

        // Create the request
        HttpWebRequest webReq = this.CreateWebRequestRaw(authtoken, isSecure);
        string boundary = "--B-00=_" + DateTime.Now.Ticks.ToString("x");
        string endBoundary = Environment.NewLine + Environment.NewLine + "--" + boundary +
            "--" + Environment.NewLine;

        webReq.ContentType = "multipart/form-data; boundary=" + boundary;

        string contentDisposition1 = "--" + boundary + Environment.NewLine +
            "Content-Disposition: form-data; name=\"requestId\"" + Environment.NewLine +
            Environment.NewLine + "lsrpc32-client-id" + Environment.NewLine;
        string contentDisposition2 = "--" + boundary + Environment.NewLine +
            "Content-Disposition : form-data; name=\"lslib32\"; filename=\"lslib32.bin\"";
        string contentType = Environment.NewLine + "Content-Type: application/octet-stream" +
            Environment.NewLine;
        string contentTransfer = "Content-Transfer-Encoding: binary" + Environment.NewLine +
            Environment.NewLine;

        if (mode == ZimbraAPI.STRING_MODE)      // easier -- all text in the request
        {
            try
            {
                string fileContents ="";
                StreamReader stmr = null;
                if(bIsBuffer)
                {
                    fileContents = mimebuffer;
                }
                else
                {
                    stmr = new StreamReader(filePath);
                    fileContents = stmr.ReadToEnd();
                }
                
                int fcLen = fileContents.Length;
                int buflen = fcLen + 400;

                using (Stream stm = webReq.GetRequestStream()) {
                    using (StreamWriter stmw = new StreamWriter(stm,
                            System.Text.Encoding.Default)) {
                        stmw.Write(contentDisposition1);
                        stmw.Write(contentDisposition2);
                        stmw.Write(contentType);
                        stmw.Write(contentTransfer);
                        stmw.Write(fileContents);
                        stmw.Write(endBoundary);
                        if(stmr != null)
                            stmr.Close();
                        stmw.Close();
                    }
                }
            }
            catch (System.Net.WebException wex)
            {
                // catch (Exception ex)
                setErrors(wex);
                rsp = "";
                return;
            }
        }
        else                                    // MIXED MODE -- text and binary attachment
        {
            try
            {
                // first get the bytes from the file -- this is the attachment data
                byte[] buf = null;
                long datalen = 0;
                if (bIsBuffer)
                {
                    datalen = mimebuffer.Length;
                    buf = Encoding.ASCII.GetBytes(mimebuffer);
                }
                else
                {
                    System.IO.FileStream fileStream = new System.IO.FileStream(filePath,
                    System.IO.FileMode.Open, System.IO.FileAccess.Read);
                    System.IO.BinaryReader binaryReader = new System.IO.BinaryReader(fileStream);

                    datalen = new System.IO.FileInfo(filePath).Length;

                    buf = binaryReader.ReadBytes((Int32)datalen);
                    fileStream.Close();
                    fileStream.Dispose();
                    binaryReader.Close();
                }
                

                // now use a memory stream since we have mixed data
                using (Stream memStream = new System.IO.MemoryStream()) {
                    // write the request data
                    byte[] cd1Bytes = System.Text.Encoding.UTF8.GetBytes(contentDisposition1);
                    memStream.Write(cd1Bytes, 0, cd1Bytes.Length);
                    byte[] cd2Bytes = System.Text.Encoding.UTF8.GetBytes(contentDisposition2);
                    memStream.Write(cd2Bytes, 0, cd2Bytes.Length);
                    byte[] cTypeBytes = System.Text.Encoding.UTF8.GetBytes(contentType);
                    memStream.Write(cTypeBytes, 0, cTypeBytes.Length);
                    byte[] cTransferBytes = System.Text.Encoding.UTF8.GetBytes(contentTransfer);
                    memStream.Write(cTransferBytes, 0, cTransferBytes.Length);
                    memStream.Write(buf, 0, (int)datalen);
                    byte[] cEndBoundaryBytes = System.Text.Encoding.UTF8.GetBytes(endBoundary);
                    memStream.Write(cEndBoundaryBytes, 0, cEndBoundaryBytes.Length);
                    //

                    // set up the web request to use our memory stream
                    webReq.ContentLength = memStream.Length;

                    Stream requestStream = webReq.GetRequestStream();

                    memStream.Position = 0;
                    byte[] tempBuffer = new byte[memStream.Length];
                    memStream.Read(tempBuffer, 0, tempBuffer.Length);
                    memStream.Close();
                    requestStream.Write(tempBuffer, 0, tempBuffer.Length);
                    requestStream.Close();
                    //
                }
            }
            catch (System.Net.WebException wex)
            {
                // catch (Exception ex)
                setErrors(wex);
                rsp = "";
                return;
            }
        }
        // get the response from the web service
        try
        {
            //Log.debug("Start GetResponse");
            response = webReq.GetResponse();
            //Log.debug("End GetResponse");
        }
        catch (System.Net.WebException wex)
        {
            // catch (Exception ex)
            setErrors(wex);
            rsp = "";
            return;
        }
        Stream str = response.GetResponseStream();
        StreamReader sr = new StreamReader(str);

        strResponse = sr.ReadToEnd();

        status = 0;
        rsp = strResponse;
        //Log.debug("End InvokeUploadService");
    }
}
}
