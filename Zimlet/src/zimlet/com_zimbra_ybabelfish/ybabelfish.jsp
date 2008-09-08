<%@ page language="java" import="java.util.*, java.io.*, java.net.*" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
	request.setCharacterEncoding("UTF-8");
	String text = request.getParameter("text");
	String lang = request.getParameter("lang");
	String userAgent = request.getParameter("userAgent");

    URL url = new URL("http://babelfish.yahoo.com/translate_txt");
    URLConnection urlConnection = url.openConnection();
    urlConnection.setDoInput(true);
    urlConnection.setDoOutput(true);
    urlConnection.setUseCaches(false);
    urlConnection.setRequestProperty("Host", "babelfish.yahoo.com");
    urlConnection.setRequestProperty("Accept-Charset", "utf-8");
	urlConnection.setRequestProperty("User-Agent", userAgent);
	urlConnection.setRequestProperty("Referer", "http://babelfish.yahoo.com/translate_txt");

    DataOutputStream outStream = new DataOutputStream(urlConnection.getOutputStream());
    String content = "ei=UTF-8&doit=done&fr=bf-res&intl=1&tt=urltext&trtext=" +
		URLEncoder.encode(text, "UTF-8") + "&lp=" + lang + "&btnTrTxt=Translate";
    outStream.writeBytes(content);
    outStream.flush();
    outStream.close();

    DataInputStream inStream = new DataInputStream(urlConnection.getInputStream());
    String str;
    while ((str = inStream.readLine()) != null)
    {
    	out.println(new String(str.getBytes("ISO-8859-1"),"UTF-8"));
    }
    inStream.close();
%>
