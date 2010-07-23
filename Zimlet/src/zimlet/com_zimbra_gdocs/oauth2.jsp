<%@ page language="java" import="java.io.BufferedReader" %>
<%@ page language="java" import="java.io.InputStreamReader" %>
<%@ page language="java" import="java.net.HttpURLConnection" %>
<%@ page language="java" import="java.net.URL" %>
<%@ page language="java" import="java.net.MalformedURLException" %>
<%@ page language="java" import="java.util.Map" %>
<%@ page language="java" import="java.util.Set" %>
<%@ page language="java" import="java.util.HashMap" %>
<%@ page language="java" import="java.util.Iterator" %>
<%@ page language="java" import="java.util.Properties" %>
<%@ page language="java" import="java.io.InputStream" %>
<%@ page language="java" import="java.io.FileInputStream" %>
<%@ page language="java" import="java.io.IOException" %>
<%@ page language="java" import="java.io.FileOutputStream" %>
<%@ page language="java" import="com.google.gdata.client.Query" %>
<%@ page language="java" import="com.google.gdata.client.GoogleService" %>
<%@ page language="java" import="com.google.gdata.client.docs.DocsService" %>
<%@ page language="java" import="com.google.gdata.client.authn.oauth.GoogleOAuthHelper" %>
<%@ page language="java" import="com.google.gdata.client.authn.oauth.GoogleOAuthParameters" %>
<%@ page language="java" import="com.google.gdata.client.authn.oauth.OAuthHmacSha1Signer" %>
<%@ page language="java" import="com.google.gdata.client.authn.oauth.OAuthRsaSha1Signer" %>
<%@ page language="java" import="com.google.gdata.client.authn.oauth.OAuthSigner" %>
<%@ page language="java" import="com.google.gdata.data.docs.DocumentEntry" %>
<%@ page language="java" import="com.google.gdata.data.docs.DocumentListEntry" %>
<%@ page language="java" import="com.google.gdata.data.docs.DocumentListFeed" %>
<%@ page language="java" import="com.google.gdata.data.docs.RevisionEntry" %>
<%@ page language="java" import="com.google.gdata.data.docs.RevisionFeed" %>
<%@ page language="java" import="com.google.gdata.data.docs.FolderEntry" %>
<%@ page language="java" import="com.google.gdata.data.docs.PresentationEntry" %>
<%@ page language="java" import="com.google.gdata.data.docs.SpreadsheetEntry" %>
<%@ page language="java" import="com.google.gdata.data.media.MediaSource" %>
<%@ page language="java" import="com.google.gdata.data.MediaContent" %>
<%@ page language="java" import="com.google.gdata.data.media.MediaEntry" %>
<%@ page language="java" import="com.google.gdata.data.Link" %>
<%@ page language="java" import="com.google.gdata.util.ServiceException" %>
<%@ page language="java" import="org.apache.commons.httpclient.*" %>
<%@ page language="java" import="org.apache.commons.httpclient.methods.*" %>
<%@ page language="java" import="org.apache.commons.httpclient.methods.multipart.*" %>
<%@ page language="java" import="org.apache.commons.httpclient.cookie.CookiePolicy" %>
<%@ page language="java" import="com.zimbra.common.util.StringUtil" %>
<%@ page trimDirectiveWhitespaces="true" %><%
    GoogleOAuthParameters oauthParameters = new GoogleOAuthParameters();
    oauthParameters.setOAuthConsumerKey("anonymous");
    oauthParameters.setOAuthConsumerSecret("anonymous");
    oauthParameters.setOAuthCallback("oob");

    OAuthSigner signer = new OAuthHmacSha1Signer();
    GoogleOAuthHelper oauthHelper = new GoogleOAuthHelper(signer);

    //Read request parameters
    String reqAction = request.getParameter("_action");
    if(reqAction == null) {
        reqAction = "";
    }
    String responseText = null;
    /*
    Action to generate the request token
     */
    if(reqAction.equals("reqToken")) {
        oauthParameters.setScope("https://docs.google.com/feeds/ http://spreadsheets.google.com/feeds/ https://docs.googleusercontent.com/");
        oauthParameters.addCustomBaseParameter("xoauth_displayname", "Zimbra");
        oauthHelper.getUnauthorizedRequestToken(oauthParameters);
        String requestUrl = oauthHelper.createUserAuthorizationUrl(oauthParameters);

        responseText = "{"
                + "\"_url\": \"" + requestUrl + "\","
                + "\"_rt\": \"" + oauthParameters.getOAuthToken() + "\","
                + "\"_rs\": \"" + oauthParameters.getOAuthTokenSecret() + "\","
                + "}";
    }

    /*
    Action to check the verification code and generate the access token and access token secret.
     */
    else if(reqAction.equals("accessToken")) {
        //read request params
        String requestToken = (request.getParameter("_rt"));
        String requestTokenSecret = (request.getParameter("_rs"));
        String verificationCode = (request.getParameter("_vc"));

        try {
            oauthParameters.setOAuthToken(requestToken);
            oauthParameters.setOAuthTokenSecret(requestTokenSecret);
            oauthParameters.setOAuthVerifier(verificationCode);

            String token = oauthHelper.getAccessToken(oauthParameters);

            responseText = "{"
                    + "\"success\": true,"
                    + "\"_rt\": \"" + oauthParameters.getOAuthToken() + "\","
                    + "\"_rs\": \"" + oauthParameters.getOAuthTokenSecret() + "\","
                    + "}";
        }
        catch(Exception e) {
            responseText = "{"
                + "\"success\": false,"
                + "}";
        }
    }

    /*
    Action to get the list of docs
     */
    else if(reqAction.equals("docList")) {
        //read request params
        String accessTokenSecret = (request.getParameter("_as"));
        String accessToken = (request.getParameter("_at"));
        URL resourceUrl = new URL(request.getParameter("_url")+"?showfolders=true");
        try {
            if(resourceUrl != null) {
                oauthParameters.setOAuthToken(accessToken);
                oauthParameters.setOAuthTokenSecret(accessTokenSecret);

                DocsService docsService = new DocsService("Zimbra-MailAttachments-1.0");
                docsService.setOAuthCredentials(oauthParameters, signer);

                DocumentListFeed listFeed = docsService.getFeed(resourceUrl, DocumentListFeed.class);
                String res = "[";
                if(listFeed != null) {
                    for (DocumentListEntry entry : listFeed.getEntries()) {
                        res += printDocumentEntry(entry);
                    }
                }
                res += "]";

                responseText = "{"
                    + "\"success\": true,"
                    + "\"docs\": " + res + ","
                    + "}";
            }
        }
        catch (Exception e) {
            responseText = "{"
                    + "\"success\": false,"
                    + "\"docs\": false,"
                    + "}";
        }
    }
    /*
    This actions downloads the file to the /tmp folder on the server - CAN BE USED FOR DEBUG PURPOSE
     */
    else if(reqAction.equals("getResource")) {
        //read request params        
        String fileName = request.getParameter("_fid");
        URL resourceUrl = new URL(request.getParameter("_url"));
        String accessTokenSecret = (request.getParameter("_as"));
        String accessToken = (request.getParameter("_at"));

        String filePath = "/tmp/" + fileName;
       
        oauthParameters.setOAuthToken(accessToken);
        oauthParameters.setOAuthTokenSecret(accessTokenSecret);

        DocsService docsService = new DocsService("Zimbra-MailAttachments-1.0");
        docsService.setOAuthCredentials(oauthParameters, signer);
        try {
            responseText = "{"
                + "\"success\": \"" + this.downloadFile(docsService, resourceUrl, filePath) + "\","
                + "\"path\": \"" + filePath + "\","
                + "\"url\": \"" + resourceUrl.toString() + "\","
                + "}";
        }
        catch (Exception e) {
            responseText = "{"
                + "\"success\": false,"                
                + "}";
        }

    }
    /*
    This action streams the file directly into the browser - CAN BE USED FOR DEBUG PURPOSE
     */
    else if(reqAction.equals("streamResource")) {
        //read request params
        String fileName = request.getParameter("_fid");
        URL resourceUrl = new URL(request.getParameter("_url"));
        String accessTokenSecret = (request.getParameter("_as"));
        String accessToken = (request.getParameter("_at"));


        oauthParameters.setOAuthToken(accessToken);
        oauthParameters.setOAuthTokenSecret(accessTokenSecret);

        DocsService docsService = new DocsService("Zimbra-MailAttachments-1.0");
        docsService.setOAuthCredentials(oauthParameters, signer);
        //OutputStream out = response.getOutputStream();
        this.downloadFile(docsService, resourceUrl, response);

    }
    /*
    This action downloads the document as byte stream and upload it as attachment. It returns the JSON string contaning the resource id. 
     */
    else if(reqAction.equals("postResource")) {
        String fileName = request.getParameter("_fid");
        URL resourceUrl = new URL(request.getParameter("_url"));
        String accessTokenSecret = (request.getParameter("_as"));
        String accessToken = (request.getParameter("_at"));
        fileName = fileName.replaceAll("\\b\\s{1,}\\b", "");

        oauthParameters.setOAuthToken(accessToken);
        oauthParameters.setOAuthTokenSecret(accessTokenSecret);

        DocsService docsService = new DocsService("Zimbra-MailAttachments-1.0");
        docsService.setOAuthCredentials(oauthParameters, signer);
        byte[] bytes = this.downloadFile(docsService, resourceUrl);
        
        MultipartPostMethod postMethod = new MultipartPostMethod("http://localhost:7070/service/upload?fmt=raw&upload=1&fileName="+fileName);
        String postResponse = null;

        try {

            HttpClient httpClient = new HttpClient();
            PartSource partSource = new ByteArrayPartSource(fileName, bytes);
            Part filePart = new FilePart(fileName, partSource);
            postMethod.addPart(filePart);
            
            //Read cookies and set the state
            javax.servlet.http.Cookie[] cookies = request.getCookies();
            org.apache.commons.httpclient.Cookie[] httpClientCookies = this.getHttpClientCookies(cookies);

            String cookieHeader = "";
            for(int i=0; i<httpClientCookies.length; i++) {
                cookieHeader += httpClientCookies[i].toString();
                if(i+1 < httpClientCookies.length) {
                    cookieHeader += ";";
                }
            }

            postMethod.setRequestHeader("Cookie", cookieHeader);
            postMethod.addParameter("fmt", "raw");
            postMethod.addParameter("upload", "1");
            postMethod.addParameter("filename", fileName);
            
            httpClient.executeMethod(postMethod);
            postResponse = postMethod.getResponseBodyAsString();            
            responseText = "{"
                    + "\"success\": true,"
                    + "\"postResponse\": \"" + StringUtil.jsEncodeKey(postResponse) + "\","
                    + "\"statusCode\": '" + postMethod.getStatusCode() + "',"                    
                    + "\"statusText\": '" + postMethod.getStatusText() + "',"
                    + "}";
            

        }
        catch (Exception e) {
            responseText = "{"
                    + "\"success\": false,"
                    + "\"filename\": \""+fileName+"\","
                    + "\"postResponse\": 'Some error occured "+e.getMessage()+"',"
                    + "}";
        }
        finally {
            postMethod.releaseConnection();            
        }

    }
    /*
    To sign the parameters - NOT USED AT ALL
     */
    else if(reqAction.equals("sign")) {
        //read request params
        String resourceUrl = request.getParameter("_url");
        String accessTokenSecret = request.getParameter("_as");
        String accessToken = request.getParameter("_at");

        oauthParameters.setOAuthToken(accessToken);
        oauthParameters.setOAuthTokenSecret(accessTokenSecret);

        String authHeader = oauthHelper.getAuthorizationHeader(resourceUrl, "GET", oauthParameters);
        try {
            responseText = "{"
                + "\"success\": true,"
                + "\"authHeader\": '" + authHeader + "',"
                + "}";
        }
        catch (Exception e) {
            responseText = "{"
                + "\"success\": false,"
                + "}";
        }

    }
    else {
         responseText = "{"
                + "\"success\": false,"
                + "\"msg\": \"No or invalid action specified\","                
                + "}";
    }

    if(responseText != null) { %><%=responseText %><% } %><%!
    public String printDocumentEntry(DocumentListEntry doc) {
        StringBuffer output = new StringBuffer();
        StringBuffer labels = new StringBuffer();
        output.append("{");
        if (!doc.getParentLinks().isEmpty()) {
                labels.append("[");
                for (Link link : doc.getParentLinks()) {
                    labels.append("{title: \"" + link.getTitle() + "\", url:\""+ link.getHref()+ "\"},");
                }
                labels.append("]");
            }
        if(labels.length() > 0) {
            output.append("\"labels\":" + labels.toString() + ",");
        }
        output.append("\"title\":\"" + doc.getTitle().getPlainText() + "\",");
        output.append("\"size\":\"" + doc.getQuotaBytesUsed() + "\",");
        output.append("\"url\":\"" + ((MediaContent) doc.getContent()).getUri() + "\",");
        output.append("\"type\":\"" + ((MediaContent) doc.getContent()).getMimeType().toString() + "\",");
        output.append("\"id\":\"" + doc.getResourceId() + "\",");
        output.append("},");
        return output.toString();
    }

    public String downloadFile(DocsService docsService, URL exportUrl, String filePath) throws IOException {
        InputStream inStream = null;
        FileOutputStream outStream = null;
        String message = "true";
        try {
            MediaContent mc = new MediaContent();
            mc.setUri(exportUrl.toString());
            MediaSource ms = docsService.getMedia(mc);

            inStream = ms.getInputStream();
            outStream = new FileOutputStream(filePath);

            int c;
            while ((c = inStream.read()) != -1) {
                outStream.write(c);
            }
        }
        catch (ServiceException e) {
            message = "Service " + e.getMessage();
        }
        catch (MalformedURLException e) {
            message = "URL " + e.getMessage();
        }
        catch (IOException e) {
            message = "IO " + e.getMessage();
        }
        catch (Exception e) {
            message = "Not Found " + e.getMessage();
        }
        finally {
            if (inStream != null) {
                inStream.close();
            }
            if (outStream != null) {
                outStream.flush();
                outStream.close();
            }
        }
        return message;
    }

    public void downloadFile(DocsService docsService, URL exportUrl, HttpServletResponse response) throws IOException {
        String message = "true";
        ServletOutputStream outStream = response.getOutputStream();
        try {
            MediaContent mc = new MediaContent();
            mc.setUri(exportUrl.toString());
            MediaSource ms = docsService.getMedia(mc);
            response.setContentType(ms.getContentType());
            if(ms.getContentLength() != -1) {
                response.setContentLength((int)ms.getContentLength());
            }            
            MediaSource.Output.writeTo(ms, outStream);
        }
        catch (ServiceException e) {
            message = "Service " + e.getMessage();
        }
        catch (MalformedURLException e) {
            message = "URL " + e.getMessage();
        }
        catch (IOException e) {
            message = "IO " + e.getMessage();
        }
        catch (Exception e) {
            message = "Not Found " + e.getMessage();
        }
        finally {
            if (outStream != null) {
                outStream.flush();
                outStream.close();
            }
        }
    }

    public byte[] downloadFile(DocsService docsService, URL exportUrl) throws IOException {
        String message = "true";
        java.io.ByteArrayOutputStream outStream = new java.io.ByteArrayOutputStream();
        try {
            MediaContent mc = new MediaContent();
            mc.setUri(exportUrl.toString());
            MediaSource ms = docsService.getMedia(mc);
            MediaSource.Output.writeTo(ms, outStream);
        }
        catch (ServiceException e) {
            message = "Service " + e.getMessage();
        }
        catch (MalformedURLException e) {
            message = "URL " + e.getMessage();
        }
        catch (IOException e) {
            message = "IO " + e.getMessage();
        }
        catch (Exception e) {
            message = "Not Found " + e.getMessage();
        }

        return outStream.toByteArray();

    }

    private org.apache.commons.httpclient.Cookie[] getHttpClientCookies(javax.servlet.http.Cookie[] cookies) {
                
        int numberOfCookies =0;
        if(cookies!= null){
            numberOfCookies = cookies.length;
        }
        org.apache.commons.httpclient.Cookie[] httpClientCookies = new org.apache.commons.httpclient.Cookie[numberOfCookies];
        for (int i = 0; i < numberOfCookies; i++) {
            javax.servlet.http.Cookie c = cookies[i];
            String domain = c.getDomain();
            String name = c.getName();
            String value = c.getValue();
            String path = c.getPath();
            boolean secure = c.getSecure();
            int maxAge = c.getMaxAge();
            org.apache.commons.httpclient.Cookie hCookie = new org.apache.commons.httpclient.Cookie(domain, name, value, path, maxAge, secure);
            httpClientCookies[i] = hCookie;
        }
        return httpClientCookies;
    }


%>