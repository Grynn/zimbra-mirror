/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2006, 2007, 2008, 2009, 2010 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.3 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.cs.taglib.bean;

import com.zimbra.common.calendar.TZIDMapper;
import com.zimbra.common.service.ServiceException;
import com.zimbra.common.util.HttpUtil;
import com.zimbra.common.util.StringUtil;
import com.zimbra.common.soap.VoiceConstants;
import com.zimbra.common.mailbox.ContactConstants;
import com.zimbra.cs.taglib.ZJspSession;
import com.zimbra.cs.zclient.ZFilterAction.ZDiscardAction;
import com.zimbra.cs.zclient.ZFilterAction.ZFileIntoAction;
import com.zimbra.cs.zclient.ZFilterAction.ZKeepAction;
import com.zimbra.cs.zclient.ZFilterAction.ZMarkAction;
import com.zimbra.cs.zclient.ZFilterAction.ZRedirectAction;
import com.zimbra.cs.zclient.ZFilterAction.ZStopAction;
import com.zimbra.cs.zclient.ZFilterAction.ZTagAction;
import com.zimbra.cs.zclient.ZFilterCondition.ZAddressBookCondition;
import com.zimbra.cs.zclient.ZFilterCondition.ZAttachmentExistsCondition;
import com.zimbra.cs.zclient.ZFilterCondition.ZBodyCondition;
import com.zimbra.cs.zclient.ZFilterCondition.ZDateCondition;
import com.zimbra.cs.zclient.ZFilterCondition.ZHeaderCondition;
import com.zimbra.cs.zclient.ZFilterCondition.ZHeaderExistsCondition;
import com.zimbra.cs.zclient.ZFilterCondition.ZSizeCondition;
import com.zimbra.cs.zclient.ZFolder.Color;
import com.zimbra.cs.zclient.ZFolder.View;
import com.zimbra.cs.zclient.ZInvite.ZAttendee;
import com.zimbra.cs.zclient.ZInvite.ZComponent;
import com.zimbra.cs.zclient.ZInvite.ZWeekDay;
import com.zimbra.cs.zclient.ZSimpleRecurrence.ZSimpleRecurrenceType;
import com.zimbra.cs.zclient.*;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.PageContext;
import com.zimbra.cs.taglib.tag.i18n.I18nUtil;
import com.zimbra.cs.mailbox.Contact;
import com.yahoo.platform.yui.compressor.JavaScriptCompressor;
import com.yahoo.platform.yui.compressor.CssCompressor;

import javax.servlet.http.HttpServletRequest;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.AddressException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.text.DateFormatSymbols;
import java.text.NumberFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.StringReader;
import java.io.StringWriter;

import org.mozilla.javascript.ErrorReporter;
import org.mozilla.javascript.EvaluatorException;

public class BeanUtils {

    private static void addAddr(StringBuilder sb, ZEmailAddress email, int size) {
        if (email == null) return;
        if (sb.length() > 0) sb.append(", ");
        if (size > 1 && email.getDisplay() != null)
            sb.append(email.getDisplay());
        else if (email.getPersonal() != null)
            sb.append(email.getPersonal());
        else if (email.getAddress() != null)
            sb.append(email.getAddress());
    }

    public static String getAddrs(List<ZEmailAddress> addrs) {
        if ( addrs == null) return null;
        int len = addrs.size();
        StringBuilder sb = new StringBuilder();
        for (ZEmailAddress addr: addrs) {
            addAddr(sb, addr, len);
        }
        String result = sb.toString();
        return result.length() == 0 ? null : result;
    }

    public static String joinLines(String lines, String sep) {
        StringBuilder result = new StringBuilder();
        for (String line : lines.split("(?m)\\n")) {
            if (line.length() > 0) {
                if (result.length() > 0) result.append(sep);
                result.append(line);
            }
        }
        return result.toString();
    }

    public static String replaceAll(String org, String find, String replace) {
        if(org != null && find != null && replace != null){
            org = org.replaceAll(find,replace);
        }
        return org;
    }


    public static String getHeaderAddrs(List<ZEmailAddress> addrs, String type) {
        if ( addrs == null) return null;
        StringBuilder sb = new StringBuilder();
        for (ZEmailAddress addr: addrs) {
            if (type != null && addr.getType().equals(type)) {
                if (sb.length() > 0) sb.append(", ");
                String p = addr.getPersonal();
                boolean useP = p!= null && p.length() > 0;
                if (useP) sb.append(p);
                String a = addr.getAddress();
                if (a != null && a.length() > 0) {
                    if (useP) sb.append(" <");
                    sb.append(a);
                    if (useP) sb.append('>');
                }
            }
        }
        String result = sb.toString();
        return result.length() == 0 ? null : result;
    }

    public static String getAddr(ZEmailAddress addr) {
        String result;
        if ( addr == null) return null;
        else if (addr.getPersonal() != null)
            result = addr.getPersonal();
        else if (addr.getAddress() != null)
            result = addr.getAddress();
        else
            return null;
        return result.length() == 0 ? null : result;
    }

    public static DateFormatSymbols
            getDateFormatSymbols(java.util.Locale userLocale, PageContext pc){
        DateFormatSymbols dfs = new ExtendedDateFormatSymbols(userLocale,pc);
        return dfs;
    }
    
    private static String escapeDollarSign(String value) {
        if (value == null || value.length() == 0 || value.indexOf('$') == -1)
            return value;
        return value.replace("$", "\\$");
    }

    public static void main(String args[]) {
        System.out.println(escapeDollarSign("hello world"));
        System.out.println(escapeDollarSign("hello$world"));
        System.out.println(escapeDollarSign("hello$4"));
    }

    private static String replaceAll(String text, Pattern pattern, String replace) {
        Matcher m = pattern.matcher(text);
        StringBuffer sb = null;
        replace = escapeDollarSign(replace);
        while (m.find()) {
            if (sb == null) sb = new StringBuffer();
            m.appendReplacement(sb, replace);
        }
        if (sb != null) m.appendTail(sb);
        return sb == null ? text : sb.toString();
    }

    private static final Pattern sAMP = Pattern.compile("&", Pattern.MULTILINE);
    private static final Pattern sTWO_SPACES = Pattern.compile("  ", Pattern.MULTILINE);
    private static final Pattern sLEADING_SPACE = Pattern.compile("^ ", Pattern.MULTILINE);
    private static final Pattern sTAB = Pattern.compile("\\t", Pattern.MULTILINE);
    private static final Pattern sLT = Pattern.compile("<", Pattern.MULTILINE);
    private static final Pattern sGT = Pattern.compile(">", Pattern.MULTILINE);
    private static final Pattern sDBLQT = Pattern.compile("\"", Pattern.MULTILINE);
    private static final Pattern sNL = Pattern.compile("\\r?\\n", Pattern.MULTILINE);
    private static final Pattern sSTART = Pattern.compile("^", Pattern.MULTILINE);
    private static final Pattern sURL = Pattern.compile(
            "((telnet:)|((https?|ftp|gopher|news|file):\\/\\/)|(www\\.[\\w\\.\\_\\-]+))[^\\s\\xA0\\(\\)\\<\\>\\[\\]\\{\\}\'\"]*",
            Pattern.MULTILINE);
    private static final Pattern sHTMLDECODE = Pattern.compile("\\<.*?\\>", Pattern.MULTILINE);

    public static String prefixContent(String content, String prefix) {
        if (content == null || prefix == null) return "";
        return replaceAll(content, sSTART, prefix);
    }

    public static String htmlEncode(String text) {
        if (text == null || text.length() == 0) return "";
        String s = replaceAll(text, sAMP, "&amp;");
        s = replaceAll(s, sLT, "&lt;");
        s = replaceAll(s, sGT, "&gt;");
        return s;
    }

    public static String htmlDecode(String text) {
        if (text == null || text.length() == 0) return "";
        String s = replaceAll(text, "<br>", "\n");
        s = replaceAll(s, sHTMLDECODE, "");
        s = replaceAll(s, "&amp;", "&");
        s = replaceAll(s,"&lt;","<");
        s = replaceAll(s,"&gt;",">");
        return s;
    }

    public static String encodeHtmlAttr(String text) {
        if (text == null || text.length() == 0) return "";
        String s = replaceAll(text, sAMP, "&amp;");
        s = replaceAll(s, sLT, "&lt;");
        s = replaceAll(s, sGT, "&gt;");
        s = replaceAll(s, sDBLQT, "&quot;");
        return s;
    }

    private static String internalTextToHtml(String text) {
        if (text == null || text.length() == 0) return "";
        String s = replaceAll(text, sAMP, "&amp;");
        s = replaceAll(s, sTWO_SPACES, " &nbsp;");
        s = replaceAll(s, sLEADING_SPACE, "&nbsp;");
        s = replaceAll(s, sLT, "&lt;");
        s = replaceAll(s, sGT, "&gt;");
        s = replaceAll(s, sTAB, "<pre style='display:inline;'>\t</pre>");
        s = replaceAll(s, sNL, "<br />");
        return s;
    }

    public static String textToHtml(String text) {
        if (text == null) return null;
        Matcher m = sURL.matcher(text);
        StringBuilder sb = new StringBuilder();
        int lastIndex = 0; // lastIndex we copied from
        while (m.find()) {
            //if (sb == null) sb = new StringBuilder();
            if (m.start() > lastIndex) {
                sb.append(internalTextToHtml(text.substring(lastIndex, m.start())));
            }
            String url = m.group();
            char last = url.charAt(url.length()-1);
            if (last == '.' || last == '!' || last == ',')
                url = url.substring(0, url.length()-1);

            sb.append("<a class='zUrl' target='_blank' href='");
            if (url.length() > 4 && url.substring(0,4).startsWith("www.")) sb.append("http://");
            sb.append(url);
            sb.append("'>");
            sb.append(htmlEncode(url));
            sb.append("</a>");
            lastIndex = m.start()+url.length();
        }
        if (lastIndex < text.length()) {
            sb.append(internalTextToHtml(text.substring(lastIndex)));
        }
        return sb.toString();
    }

    /**
     * truncat given text at length, then walk back until you hit a whitespace.
     *
     * @param text text to truncate
     * @param length length to truncate too
     * @param ellipses whether or not to add ellipses
     * @return truncated string
     */
    public static String truncate(String text, int length, boolean ellipses) {
        if (text.length() < length) return text;
        if (length <= 0) return ellipses ? "..." : "";
        int n = Math.min(length, text.length());
        for (int i=n-1; i > 0; i--) {
            if (Character.isWhitespace(text.charAt(i))) {
                return text.substring(0, i)+(ellipses ? " ..." : "");
            }
        }
        return text.subSequence(0, length)+(ellipses ? " ..." : "");
    }

    public static String truncateFixed(String text, int length, boolean ellipses) {
        if (length <= 0) return ellipses ? "..." : "";

        if (text.length() <= length) {
            return text;
        }
        else{
            return text.subSequence(0, length)+(ellipses ? "..." : "");    
        }
    }

    public static String displaySize(PageContext pc, long size) {
        return displaySize(pc, size, 0);
    }

    public static String displaySize(PageContext pc, long size, int fractions) {
        String units;
        double dsize;
        if (size >= 1073741824) {
            dsize = size/1073741824.0;
            if(pc == null){
                units = "GB";
            }else{
                units = I18nUtil.getLocalizedMessage(pc, "gb");// " GB";
            }

        } else if (size >= 1048576) {
            dsize = size/1048576.0;
            if(pc == null){
                units = "MB";
            }else{
                units = I18nUtil.getLocalizedMessage(pc, "mb");//" MB";
            }
        } else if (size >= 1024) {
            dsize = size/1024.0;
            if(pc == null){
                units = "KB";
            }else{
                units = I18nUtil.getLocalizedMessage(pc, "kb");//" KB";
            }
        } else {
            dsize = size;
            if(pc == null){
                units = "b";
            }else{
                units = I18nUtil.getLocalizedMessage(pc, "b");//" B";
            }
        }

        Locale locale = I18nUtil.getLocale("en_US"); // Default US, in case pc is null
        if(pc != null){
                locale = I18nUtil.findLocale(pc);
        }
        NumberFormat formatter = NumberFormat.getNumberInstance(locale);        
        String str = dsize+"";
        if (fractions == 0) {
            str = formatter.format(Math.round(dsize));
        } else {
            formatter.setMaximumFractionDigits(fractions);
            str = formatter.format(dsize);
        }
        return str + " " + units;
    }

    public static String displaySizePercent(long size, long max) {
        String formt = "%";
        double dsize;
        dsize = (size*100)/(double)max;
        return Math.round(dsize) + formt;
    }

    private enum DateTimeFmt { DTF_TIME_SHORT, DTF_DATE_MEDIUM, DTF_DATE_SHORT }

    private static DateFormat getDateFormat(PageContext pc, DateTimeFmt fmt) {
        DateFormat df = (DateFormat) pc.getAttribute(fmt.name(), PageContext.REQUEST_SCOPE);
        if (df == null) {
            switch (fmt) {
            case DTF_DATE_MEDIUM:
                df = new SimpleDateFormat(I18nUtil.getLocalizedMessage(pc, "ZM_formatDateMediumNoYear"));
                break;
            case DTF_TIME_SHORT:
                df = DateFormat.getTimeInstance(DateFormat.SHORT, pc.getRequest().getLocale());
                break;
            case DTF_DATE_SHORT:
            default:
                df = DateFormat.getDateInstance(DateFormat.SHORT, pc.getRequest().getLocale());
                break;
            }
            pc.setAttribute(fmt.name(), df, PageContext.REQUEST_SCOPE);
        }
        return df;
    }

    public static String displayMsgDate(PageContext pc, Date msg) throws ServiceException, JspException {
        ZMailbox mbox = ZJspSession.getZMailbox(pc);
        TimeZone tz = mbox.getPrefs().getTimeZone();
        Calendar cal = Calendar.getInstance(tz);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.HOUR_OF_DAY, 0);

        long nowTime = cal.getTimeInMillis();
        long msgTime = msg.getTime();

        if (msgTime >= nowTime) {
            // show hour and return
            DateFormat df = getDateFormat(pc, DateTimeFmt.DTF_TIME_SHORT);
            df.setTimeZone(tz);
            return df.format(msg);
        }

        long nowYear = cal.get(Calendar.YEAR);
        cal.setTimeInMillis(msgTime);
        long msgYear = cal.get(Calendar.YEAR);

        if (nowYear == msgYear) {
            DateFormat df = getDateFormat(pc, DateTimeFmt.DTF_DATE_MEDIUM);
            df.setTimeZone(tz);
            return df.format(msg);
        } else {
			return displayDate(pc, msg, tz);
        }
    }

	public static String displayDate(PageContext pc, Date msg) throws ServiceException, JspException {
		ZMailbox mbox = ZJspSession.getZMailbox(pc);
		TimeZone tz = mbox.getPrefs().getTimeZone();
		return displayDate(pc, msg, tz);
	}

	private static String displayDate(PageContext pc, Date msg, TimeZone tz) {
		DateFormat df = getDateFormat(pc, DateTimeFmt.DTF_DATE_SHORT);
		df.setTimeZone(tz);
		return df.format(msg);
	}

	public static String displayDuration(PageContext pc, long duration) throws ServiceException, JspException {
        long totalSeconds = duration / 1000;
        long seconds = totalSeconds % 60;
        long minutes = (totalSeconds - seconds) / 60;
        if (minutes > 0) {
            return I18nUtil.getLocalizedMessage(pc, "durationDisplayMinutes", new Object[]{minutes, seconds});
        } else {
            return I18nUtil.getLocalizedMessage(pc, "durationDisplaySeconds", new Object[]{seconds});
        }
    }

	public static String displayVoiceDate(PageContext pc, Date msg) throws ServiceException, JspException {
		ZMailbox mbox = ZJspSession.getZMailbox(pc);
		TimeZone tz = mbox.getPrefs().getTimeZone();
		Calendar cal = Calendar.getInstance(tz);
		Calendar msgCal = Calendar.getInstance(tz);

		long nowTime = cal.getTimeInMillis();
        msgCal.setTime(msg);
		long msgTime = msgCal.getTimeInMillis();


		final int ONE_DAY = 24 * 3600000;
		String resource;
		if (nowTime - msgTime < ONE_DAY && cal.getTime().getDay() == msgCal.get(Calendar.DAY_OF_WEEK)) {
			resource = "ZM_formatVoiceDateToday";
		} else if ((nowTime - msgTime) < (2 * ONE_DAY) && (new Date(nowTime - ONE_DAY).getDay()) == msgCal.get(Calendar.DAY_OF_WEEK)) {
			resource = "ZM_formatVoiceDateYesterday";
		} else {
			resource = "ZM_formatVoiceDate";
		}

		DateFormat df = (DateFormat) pc.getAttribute(resource, PageContext.REQUEST_SCOPE);
		if (df == null) {
			df = new SimpleDateFormat(I18nUtil.getLocalizedMessage(pc, resource));
			pc.setAttribute(resource, df, PageContext.REQUEST_SCOPE);
		}
		df.setTimeZone(tz);
		return df.format(msgCal.getTime());
	}

    public static String getAttr(PageContext pc, String attr) throws JspException, ServiceException {
        ZMailbox mbox = ZJspSession.getZMailbox(pc);
        List<String> val = mbox.getAccountInfo(false).getAttrs().get(attr);
        return (val.size() > 0) ? val.get(0) : null;
    }

    public static String repeatString(String string, int count) {
        if (count==0) return "";
        StringBuilder sb = new StringBuilder(string.length()*count);
        while(count-- > 0) sb.append(string);
        return sb.toString();
    }

    private static final Pattern sCOMMA = Pattern.compile(",");

    private static  boolean inList(String id, String[] list) {
        for (String s : list) if (s.equals(id)) return true;
        return false;
    }

    // todo: add some per-requeset caching?
    public static List<ZTagBean> getAvailableTags(PageContext pc, String idList, boolean excludeList) throws JspException {
        try {
            String[] ids = (idList == null || idList.length() == 0) ? new String[0] : sCOMMA.split(idList);
            List<ZTagBean> tags = new ArrayList<ZTagBean>();

            if (!excludeList && ids.length == 0)
                return tags;

            ZMailbox mbox = ZJspSession.getZMailbox(pc);
            List<ZTag> allTags = mbox.getAllTags();

            for (ZTag tag : allTags) {
                if (excludeList) {
                    if (!inList(tag.getId(), ids))
                        tags.add(new ZTagBean(tag));
                } else {
                    if (inList(tag.getId(), ids))
                        tags.add(new ZTagBean(tag));
                }
            }
            return tags;
        } catch (ServiceException e) {
            throw new JspTagException(e);
        }
    }

    // todo: add some per-requeset caching?
    public static List<ZTagBean> getTags(PageContext pc, String idList) throws JspException {
        try {
            ZMailbox mbox = ZJspSession.getZMailbox(pc);
            if (idList == null || idList.length() == 0) return null;
            String[] ids = sCOMMA.split(idList);
            List<ZTagBean> tags = new ArrayList<ZTagBean>(ids.length);
            for (String id: ids) {
                ZTag tag = mbox.getTagById(id);
                if (tag != null) tags.add(new ZTagBean(tag));
            }
            return tags;
        } catch (ServiceException e) {
            throw new JspTagException(e);
        }

    }

    // todo: add some per-request caching?
    public static String getTagNames(PageContext pc, String idList) throws JspException {
        try {
            ZMailbox mbox = ZJspSession.getZMailbox(pc);
            if (idList == null || idList.length() == 0) return null;
            String[] ids = sCOMMA.split(idList);
            StringBuilder sb = new StringBuilder();
            for (String id: ids) {
                ZTag tag = mbox.getTagById(id);
                if (tag != null) {
                    if (sb.length() > 0) sb.append(',');
                    sb.append(tag.getName());
                }
            }
            return sb.toString();
        } catch (ServiceException e) {
            throw new JspTagException(e);
        }
    }

    public static String getTagName(PageContext pc, String id) throws JspException {
        try {
            ZMailbox mbox = ZJspSession.getZMailbox(pc);
            if (id == null) return null;
            ZTag tag = mbox.getTagById(id);
            return tag == null ? null : tag.getName();
        } catch (ServiceException e) {
            throw new JspTagException(e);
        }
    }

    public static String getServerName(PageContext pc) {
		String serverName = pc.getRequest().getParameter("customerDomain");
        return serverName != null ? serverName.trim() : HttpUtil.getVirtualHost((HttpServletRequest) pc.getRequest());
    }
    
    public static ZTagBean getTag(PageContext pc, String id) throws JspException {
        try {
            ZMailbox mbox = ZJspSession.getZMailbox(pc);
            if (id == null) return null;
            ZTag tag = mbox.getTagById(id);
            return tag == null ? null : new ZTagBean(tag);
        } catch (ServiceException e) {
            throw new JspTagException(e);
        }
    }

    public static String maskRemoteItemId(ZMailboxBean mailbox, String folderId, String id) throws JspException, ServiceException {
        return mailbox.getMailbox().maskRemoteItemId(folderId, id);
    }

    public static String unmaskRemoteItemId(ZMailboxBean mailbox, String id) throws JspException, ServiceException {
        return mailbox.getMailbox().unmaskRemoteItemId(id);
    }

    public static ZFolderBean getFolder(PageContext pc, String id) throws JspException, ServiceException {
        ZMailbox mbox = ZJspSession.getZMailbox(pc);
        if (id == null) return null;
        ZFolder f = mbox.getFolderById(id);
        return f == null ? null : new ZFolderBean(f);
    }

    public static String getFolderName(PageContext pc, String id) throws JspException, ServiceException {
        ZMailbox mbox = ZJspSession.getZMailbox(pc);
        if (id == null) return null;
        ZFolder f = mbox.getFolderById(id);
        if (f == null) return null;
        String lname = I18nUtil.getLocalizedMessage(pc, "FOLDER_LABEL_"+f.getId());
        return (lname == null || lname.startsWith("???")) ? f.getName() : lname;
    }

	private static void getFolderPath(PageContext pc, ZFolder folder, StringBuilder builder) throws JspException, ServiceException {
		ZFolder parent = folder.getParent();
		if (parent != null && !ZFolder.ID_USER_ROOT.equals(parent.getId())) {
			getFolderPath(pc, parent, builder);
			builder.append(ZMailbox.PATH_SEPARATOR);
		}
		builder.append(getFolderName(pc, folder.getId()));
	}
	
	public static String getFolderPath(PageContext pc, String id) throws JspException, ServiceException {
        ZMailbox mbox = ZJspSession.getZMailbox(pc);
        if (id == null) return null;
        ZFolder f = mbox.getFolderById(id);
        if (f == null) return null;
		StringBuilder builder = new StringBuilder(256);
		getFolderPath(pc, f, builder);
		return builder.toString();
    }

    private static long sUrlRandSalt = 0;

    /**
     *
     * @return some random string for a URL to add to deal with caching. random value returned is not secure!
     */
    public static String getUrlRand() {
        return (System.currentTimeMillis() - 1167421101179L) + "." + sUrlRandSalt++;
    }


    private static Context sEnvCtxt = null;

    static {
         try {
            Context sInitCtxt = new InitialContext();
            sEnvCtxt = (Context) sInitCtxt.lookup("java:comp/env");
        } catch (NamingException e) {
             /* ignore */
        }
    }

    public static String getEnvString(String key, String defaultValue) {
        try {
            String value = sEnvCtxt == null ? defaultValue : (String) sEnvCtxt.lookup(key);
            return value == null ? defaultValue : value;
        } catch (NamingException e) {
            return defaultValue;
        }
    }

    public static boolean isAddressBook(ZFilterCondition condition) {
        return condition instanceof ZAddressBookCondition;
    }

    public static ZAddressBookCondition getAddressBook(ZFilterCondition condition) {
        return isAddressBook(condition) ? (ZAddressBookCondition) condition : null;
    }

    public static boolean isBody(ZFilterCondition condition) {
        return condition instanceof ZBodyCondition;
    }

    public static ZBodyCondition getBody(ZFilterCondition condition) {
        return isBody(condition) ? (ZBodyCondition) condition : null;
    }

    public static boolean isSize(ZFilterCondition condition) {
        return condition instanceof ZSizeCondition;
    }

    public static ZSizeCondition getSize(ZFilterCondition condition) {
        return isSize(condition) ? (ZSizeCondition) condition : null;
    }

    public static boolean isDate(ZFilterCondition condition) {
        return condition instanceof ZDateCondition;
    }

    public static ZDateCondition getDate(ZFilterCondition condition) {
        return isDate(condition) ? (ZDateCondition) condition : null;
    }

    public static boolean isHeader(ZFilterCondition condition) {
        return condition instanceof ZHeaderCondition;
    }

    public static ZHeaderCondition getHeader(ZFilterCondition condition) {
        return isHeader(condition) ? (ZHeaderCondition) condition : null;
    }

    public static boolean isHeaderExists(ZFilterCondition condition) {
        return condition instanceof ZHeaderExistsCondition;
    }

    public static ZHeaderExistsCondition getHeaderExists(ZFilterCondition condition) {
        return isHeaderExists(condition) ? (ZHeaderExistsCondition) condition : null;
    }

    public static boolean isAttachmentExists(ZFilterCondition condition) {
        return condition instanceof ZAttachmentExistsCondition;
    }

    public static ZAttachmentExistsCondition getAttachmentExists(ZFilterCondition condition) {
        return isAttachmentExists(condition) ? (ZAttachmentExistsCondition) condition : null;
    }

    public static boolean isKeep(ZFilterAction action) {
        return action instanceof ZKeepAction;
    }

    public static boolean isDiscard(ZFilterAction action) {
        return action instanceof ZDiscardAction;
    }

    public static boolean isStop(ZFilterAction action) {
        return action instanceof ZStopAction;
    }

    public static boolean isFileInto(ZFilterAction action) {
        return action instanceof ZFileIntoAction;
    }

    public static ZFileIntoAction getFileInto(ZFilterAction action) {
        return isFileInto(action) ? (ZFileIntoAction) action : null;
    }

    public static boolean isTag(ZFilterAction action) {
        return action instanceof ZTagAction;
    }

    public static ZTagAction getTag(ZFilterAction action) {
        return isTag(action) ? (ZTagAction) action : null;
    }

    public static boolean isFlag(ZFilterAction action) {
        return action instanceof ZMarkAction;
    }

    public static ZMarkAction getFlag(ZFilterAction action) {
        return isFlag(action) ? (ZMarkAction) action : null;
    }

    public static boolean isRedirect(ZFilterAction action) {
        return action instanceof ZRedirectAction;
    }

    public static ZRedirectAction getRedirect(ZFilterAction action) {
        return isRedirect(action) ? (ZRedirectAction) action : null;
    }

    public static Calendar getCalendarMidnight(long time, TimeZone tz) {
        Calendar cal = tz == null ? Calendar.getInstance() : Calendar.getInstance(tz);
        cal.setTimeInMillis(time);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal;
    }

    public static Calendar getCalendar(long time, TimeZone tz) {
        Calendar cal = tz == null ? Calendar.getInstance() : Calendar.getInstance(tz);
        cal.setTimeInMillis(time);
        return cal;
    }

    public static Calendar getToday(TimeZone tz) {
        Calendar cal = tz == null ? Calendar.getInstance() : Calendar.getInstance(tz);
        cal.setTimeInMillis(System.currentTimeMillis());
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal;
    }

    public static Calendar getTodayHour(int hour, TimeZone tz) {
        Calendar cal = tz == null ? Calendar.getInstance() : Calendar.getInstance(tz);
        cal.setTimeInMillis(System.currentTimeMillis());
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal;
    }

    public static Calendar getFirstDayOfMonthView(java.util.Calendar date, long prefFirstDayOfWeek) {
         prefFirstDayOfWeek++; // pref goes 0-6, Calendar goes 1-7
         Calendar cal = Calendar.getInstance(date.getTimeZone());
         cal.setTimeInMillis(date.getTimeInMillis());
         cal.set(Calendar.HOUR_OF_DAY, 0);
         cal.set(Calendar.MINUTE, 0);
         cal.set(Calendar.SECOND, 0);
         cal.set(Calendar.MILLISECOND, 0);
         cal.set(Calendar.DAY_OF_MONTH, 1);
         int dow = cal.get(Calendar.DAY_OF_WEEK);
         if (dow == prefFirstDayOfWeek) {
             cal.add(Calendar.DAY_OF_MONTH, -7);
         } else {
             cal.add(Calendar.DAY_OF_MONTH, - ((dow+(7-((int)prefFirstDayOfWeek)))%7));
         }
         return cal;
     }

    public static Calendar getFirstDayOfMultiDayView(java.util.Calendar date, long prefFirstDayOfWeek, String view) {

         Calendar cal = Calendar.getInstance(date.getTimeZone());
         cal.setTimeInMillis(date.getTimeInMillis());
         cal.set(Calendar.HOUR_OF_DAY, 0);
         cal.set(Calendar.MINUTE, 0);
         cal.set(Calendar.SECOND, 0);
         cal.set(Calendar.MILLISECOND, 0);
         int dow = cal.get(Calendar.DAY_OF_WEEK);

        // pref goes 0-6, Calendar goes 1-7
        if ("workWeek".equalsIgnoreCase(view)) {
                if (dow == Calendar.SUNDAY)
                    cal.add(Calendar.DAY_OF_MONTH, 1);
                else if (dow != Calendar.MONDAY)
                    cal.add(Calendar.DAY_OF_MONTH, - (dow - Calendar.MONDAY));
        } else if ("week".equalsIgnoreCase(view)) {
                if (dow != prefFirstDayOfWeek)
                    cal.add(Calendar.DAY_OF_MONTH, - (((dow-1) + (7- (int)prefFirstDayOfWeek)) % 7));
        }
         return cal;
     }

    public static void getNextDay(Calendar cal) {
        cal.add(Calendar.DAY_OF_MONTH, 1);
    }

    public static void setDayOfWeek(Calendar cal, int dow) {
        cal.set(Calendar.DAY_OF_WEEK, dow);
    }

    public static void setMonth(Calendar cal, int month) {
        cal.set(Calendar.MONTH, month);
    }

    public static Calendar addDay(Calendar cal, int incr) {
        Calendar other = Calendar.getInstance(cal.getTimeZone());
        other.setTimeInMillis(cal.getTimeInMillis());
        other.add(Calendar.DAY_OF_MONTH, incr);
        return other;
    }

    public static Calendar addMonth(Calendar cal, int incr) {
        Calendar other = Calendar.getInstance(cal.getTimeZone());
        other.setTimeInMillis(cal.getTimeInMillis());
        other.add(Calendar.MONTH, incr);
        return other;
    }

    public static Calendar addYear(Calendar cal, int incr) {
        Calendar other = Calendar.getInstance(cal.getTimeZone());
        other.setTimeInMillis(cal.getTimeInMillis());
        other.add(Calendar.YEAR, incr);
        return other;
    }

    public static Calendar relativeDay(Calendar cal, int offset) {
        Calendar other = Calendar.getInstance(cal.getTimeZone());
        other.setTimeInMillis(cal.getTimeInMillis());
        other.add(Calendar.DAY_OF_MONTH, offset);
        return other;
    }

    public static boolean isSameDate(Calendar day1, Calendar day2) {
        return day1.get(Calendar.YEAR) ==  day2.get(Calendar.YEAR) &&
                day1.get(Calendar.MONTH) ==  day2.get(Calendar.MONTH) &&
                day1.get(Calendar.DAY_OF_MONTH) ==  day2.get(Calendar.DAY_OF_MONTH);
    }

    public static boolean isSameMonth(Calendar day1, Calendar day2) {
        return day1.get(Calendar.YEAR) ==  day2.get(Calendar.YEAR) &&
                day1.get(Calendar.MONTH) ==  day2.get(Calendar.MONTH);

    }

    public static int getYear(Calendar cal) { return cal.get(Calendar.YEAR); }
    public static int getMonth(Calendar cal) { return cal.get(Calendar.MONTH); }
    public static int getDay(Calendar cal) { return cal.get(Calendar.DAY_OF_MONTH); }
    public static int getDayOfWeek(Calendar cal) { return cal.get(Calendar.DAY_OF_WEEK); }


    public static String getCheckedCalendarFolderIds(ZMailboxBean mailbox) throws ServiceException {
        StringBuilder sb = new StringBuilder();
        getCheckedCalendarFoldersRecursive(mailbox.getMailbox().getUserRoot(), sb);
        return sb.toString();
    }

    private static void getCheckedCalendarFoldersRecursive(ZFolder f, StringBuilder sb) {
        if (f.getDefaultView() == View.appointment && f.isCheckedInUI()) {
            if (sb.length() > 0) sb.append(',');
            sb.append(f.getId());
        }
        for (ZFolder child : f.getSubFolders()) {
            getCheckedCalendarFoldersRecursive(child, sb);
        }
    }

    public static boolean hasAnyAppointments(ZApptSummariesBean summary, long start, long end) {
        for (ZAppointmentHit appt : summary.getAppointments()) {
            if (appt.isInRange(start, end)) return true;
        }
        return false;
    }

    private static final long MSECS_PER_MINUTE = 1000*60;
    private static final long MSECS_PER_HOUR = MSECS_PER_MINUTE * 60;

    public static long MSECS_PER_MINUTE() { return MSECS_PER_MINUTE; }
    public static long MSECS_PER_HOUR() { return MSECS_PER_HOUR; }

	public static String getCanonicalId(TimeZone tz) {
		return TZIDMapper.canonicalize(tz.getID());
	}

    public static TimeZone getTimeZone(String id) {
        id = TZIDMapper.canonicalize(id);
        return id == null ? TimeZone.getDefault() : TimeZone.getTimeZone(id);
    }

    public static String getFolderStyleColor(String color, String view) throws ServiceException {
        return ZFolderBean.getStyleColor(Color.fromString(color), View.fromString(view));
    }

    public static boolean actionSet(Map param, String action) {
        return param.containsKey(action) || param.containsKey(action+".x");
    }

    public static boolean isSameTimeZone(String tz1, String tz2) {
        return (tz1 == null || tz2 == null) ? tz1 == tz2 :
                TZIDMapper.canonicalize(tz1).equals(TZIDMapper.canonicalize(tz2));
    }

    public static ZAttendee getMyAttendee(ZInvite invite, ZMailboxBean mailbox) throws ServiceException {
        ZComponent comp = invite.getComponent();
        List<ZAttendee> attendees = comp.getAttendees();
        if (attendees != null) {
            Set<String> myAddrs = mailbox.getAccountInfo().getEmailAddresses();
            for (ZAttendee attendee : attendees) {
                if (myAddrs.contains(attendee.getAddress()) || myAddrs.contains(attendee.getUrl()))
                    return attendee;
            }
        }
        return null;
    }

    public static String getRepeatBlurb(ZSimpleRecurrence repeat, PageContext pc, TimeZone timeZone, Date startDate) {
        String r = "";
        Calendar cal;

        if (repeat == null || repeat.getType() == null) {
            return I18nUtil.getLocalizedMessage(pc, "recurNone");
        }

        switch (repeat.getType()) {
            case NONE:
                r = I18nUtil.getLocalizedMessage(pc, "recurNone");
                break;
            case DAILY:
                r = I18nUtil.getLocalizedMessage(pc, "recurDailyEveryDay");
                break;
            case DAILY_WEEKDAY:
                r = I18nUtil.getLocalizedMessage(pc, "recurDailyEveryWeekday");
                break;
            case DAILY_INTERVAL:
                r = I18nUtil.getLocalizedMessage(pc, "recurDailyEveryNumDays", new Object[] {repeat.getDailyInterval()});
                break;
            case WEEKLY:
                r = I18nUtil.getLocalizedMessage(pc, "recurDailyEveryWeek");
                break;
            case WEEKLY_BY_DAY:
                cal = getToday(timeZone);
                setDayOfWeek(cal, repeat.getWeeklyByDay().ordinal()+1);
                r = I18nUtil.getLocalizedMessage(pc, "recurWeeklyEveryWeekday", new Object[] {cal.getTime()});
                break;
            case WEEKLY_CUSTOM:
                StringBuilder wc = new StringBuilder();
                cal = getToday(timeZone);
                wc.append(I18nUtil.getLocalizedMessage(pc, "recurWeeklyEveryNumWeeks", new Object[] {repeat.getWeeklyInterval()}));
                wc.append(" ");
                int wci = 1, wcmax = repeat.getWeeklyIntervalDays().size();
                for (ZWeekDay day : repeat.getWeeklyIntervalDays()) {
                    if (wci != 1 && wci != wcmax) wc.append(I18nUtil.getLocalizedMessage(pc, "recurWeeklyEveryNumWeeksSep")).append(" ");
                    if (wci != 1 && wci == wcmax) wc.append(" ").append(I18nUtil.getLocalizedMessage(pc, "recurWeeklyEveryNumWeeksLastSep")).append(" ");
                    setDayOfWeek(cal, day.getOrdinal()+1);
                    wc.append(I18nUtil.getLocalizedMessage(pc, "recurWeeklyEveryNumWeeksDay", new Object[] {cal.getTime()}));
                    wci++;
                }
                r = wc.toString();
                break;
            case MONTHLY:
                r = I18nUtil.getLocalizedMessage(pc, "recurMonthly");
                break;
            case MONTHLY_BY_MONTH_DAY:
                r = I18nUtil.getLocalizedMessage(pc, "recurMonthlyEveryNumMonthsDate",
                        new Object[] {repeat.getMonthlyMonthDay(), repeat.getMonthlyInterval()});
                break;
            case MONTHLY_RELATIVE:
                cal = getToday(timeZone);
                setDayOfWeek(cal, repeat.getMonthlyRelativeDay().getDay().getOrdinal()+1);
                r = I18nUtil.getLocalizedMessage(pc, "recurMonthlyEveryNumMonthsNumDay",
                        new Object[] {
                                repeat.getMonthlyRelativeDay().getWeekOrd(),
                                cal.getTime(),
                                repeat.getMonthlyInterval()
                        });
                break;
            case YEARLY:
                r = I18nUtil.getLocalizedMessage(pc, "recurYearly");
                break;
            case YEARLY_BY_DATE:
                cal = getToday(timeZone);
                setMonth(cal, repeat.getYearlyByDateMonth()-1);
                r = I18nUtil.getLocalizedMessage(pc, "recurYearlyEveryDate",
                        new Object[] { cal.getTime(), repeat.getYearlyByDateMonthDay()});
                break;
            case YEARLY_RELATIVE:
                cal = getToday(timeZone);
                setDayOfWeek(cal, repeat.getYearlyRelativeDay().getDay().getOrdinal()+1);
                setMonth(cal, repeat.getYearlyRelativeMonth()-1);
                r = I18nUtil.getLocalizedMessage(pc, "recurYearlyEveryMonthNumDay",
                        new Object[] {
                                repeat.getYearlyRelativeDay().getWeekOrd(),
                                cal.getTime(),
                                cal.getTime()
                        });
                break;
            default:
                r = I18nUtil.getLocalizedMessage(pc, "recurComplex");
                break;
        }

        if (repeat.getType() == ZSimpleRecurrenceType.NONE)
            return r;

        String e = "";

        switch (repeat.getEnd()) {
            case NEVER:
                e = I18nUtil.getLocalizedMessage(pc, "recurEndNone");
                break;
            case COUNT:
                e = I18nUtil.getLocalizedMessage(pc, "recurEndNumber", new Object[] {repeat.getCount()});
                break;
            case UNTIL:
                DateFormat untilDf = DateFormat.getDateInstance(DateFormat.MEDIUM, pc.getRequest().getLocale());
                if (timeZone != null) untilDf.setTimeZone(timeZone);
                String untilDate = untilDf.format(repeat.getUntilDate().getDate());
                e = I18nUtil.getLocalizedMessage(pc, "recurEndByDate", new Object[] { untilDate});
                break;
        }

        String s = "";
        if (startDate != null) {
            DateFormat startDf = DateFormat.getDateInstance(DateFormat.MEDIUM, pc.getRequest().getLocale());
            if (timeZone != null) startDf.setTimeZone(timeZone);
            s = I18nUtil.getLocalizedMessage(pc, "recurStart", new Object[] { startDf.format(startDate)});

        }

        return I18nUtil.getLocalizedMessage(pc, "repeatBlurb", new Object[] { r, e, s});
    }

    public static String getApptDateBlurb(PageContext pc, TimeZone timeZone, long startTime, long endTime, boolean allDay) {
        Calendar startCal = getCalendar(startTime, timeZone);
        Calendar endCal = getCalendar(endTime, timeZone);

        DateFormat df = DateFormat.getDateInstance(DateFormat.FULL, pc.getRequest().getLocale());
        DateFormat tf = DateFormat.getTimeInstance(DateFormat.SHORT, pc.getRequest().getLocale());
        
        if (timeZone != null) {
            df.setTimeZone(timeZone);
            tf.setTimeZone(timeZone);
        }

        boolean sameDate = isSameDate(startCal, endCal);

        if (allDay && sameDate) {
                return I18nUtil.getLocalizedMessage(pc, "apptDateBlurbAllDay",
                        new Object[] {df.format(startCal.getTime())});
        } else if (allDay) {
                return I18nUtil.getLocalizedMessage(pc, "apptDateBlurbAllDayDiffEndDay",
                        new Object[] {df.format(startCal.getTime()), df.format(endCal.getTime())});
        } else if (sameDate) {
                return I18nUtil.getLocalizedMessage(pc, "apptDateBlurb",
                        new Object[] {
                                df.format(startCal.getTime()),
                                tf.format(startCal.getTime()),
                                tf.format(endCal.getTime())
                        });
        } else {
                return I18nUtil.getLocalizedMessage(pc, "apptDateBlurbDiffEndDay",
                        new Object[] {
                                df.format(startCal.getTime()),
                                tf.format(startCal.getTime()),
                                df.format(endCal.getTime()),
                                tf.format(endCal.getTime())
                        });
        }
    }

    public static void clearMessageCache(ZMailboxBean mailbox) {
        mailbox.getMailbox().clearMessageCache();
    }

    public static boolean hasShareMountPoint(ZMailboxBean mailbox, ZMessageBean message) {
        ZShare share = message.getShare();
        if (share == null) return false;

        try {
            ZFolder folder = mailbox.getMailbox().getFolderById(share.getGrantor().getId()+":"+share.getLink().getId());
            return folder != null;
        } catch (ServiceException e) {
            return false;
        }
    }

    public static String jsEncode(String str) {
        return StringUtil.jsEncode(str);
	}

    public static String getFolderRestURL(ZMailboxBean mailbox, ZFolderBean folder) throws JspTagException {
        try {
            return mailbox.getRestURI(folder.getRootRelativePathURLEncoded()).toString();
        } catch (ServiceException e) {
            throw new JspTagException(e);
        }
    }

    public static String getVoiceFolderType(ZFolderBean folder) {
        String name = folder.getName();
        if (VoiceConstants.FNAME_PLACEDCALLS.equals(name) ||
            VoiceConstants.FNAME_ANSWEREDCALLS.equals(name) ||
            VoiceConstants.FNAME_MISSEDCALLS.equals(name)) {
            return ZSearchParams.TYPE_CALL; 
        }
        return ZSearchParams.TYPE_VOICE_MAIL;
    }

    public static String getVoiceFolderQuery(ZFolderBean folder) {
        String id = folder.getId();
        String phone = id.substring(id.indexOf('-') + 1);
        String name = folder.getName();
        return "phone:" + phone + " " + "in:\"" + name + "\"";
    }

    public static String getVoiceFolderName(PageContext pc, ZFolderBean folder) {
        String name = folder.getName();
        String key = null;
        if (VoiceConstants.FNAME_PLACEDCALLS.equals(name)) {
            key = "placedCalls";
        } else if (VoiceConstants.FNAME_ANSWEREDCALLS.equals(name)) {
            key = "answeredCalls";
        } else if (VoiceConstants.FNAME_MISSEDCALLS.equals(name)) {
            key = "missedCalls";
        } else if (VoiceConstants.FNAME_VOICEMAILINBOX.equals(name)) {
            key = "voiceMail";
        } else if (VoiceConstants.FNAME_TRASH.equals(name)) {
            key = "trash";
        }
        return key != null ? I18nUtil.getLocalizedMessage(pc, key) : name;
    }

    public static String getPhoneDisplay(String name) {
        return ZPhone.getDisplay(name);
    }

    public static String getPhoneFromVoiceQuery(String query) {
        // Guess the phone name from query. If I knew better how to pass
        // information around all these jsps, I wouldn't need to guess....
        // TODO:
        String phone = "phone:";
        int match = query.indexOf(phone);
        if (match != -1) {
            int startIndex = match + phone.length();
            int endIndex = query.indexOf(' ', startIndex);
            if (endIndex == -1) {
                endIndex = query.length();
            }
            return query.substring(startIndex, endIndex);
        }
        return "";
    }

    public static ZVoiceMailItemHitBean deserializeVoiceMailItemHit(String value, String phone) throws ServiceException {
		return ZVoiceMailItemHitBean.deserialize(value, phone);
	}
	
	public static ZVoiceMailItemHitBean[] deserializeVoiceMailItemHits(String[] values, String phone) throws ServiceException {
        if (values == null) {
            return new ZVoiceMailItemHitBean[0]; 
        }
        ZVoiceMailItemHitBean[] result = new ZVoiceMailItemHitBean[values.length];
        for (int i = 0, count = values.length; i < count; i++) {
            result[i] = deserializeVoiceMailItemHit(values[i], phone);
        }
        return result;
    }

    public static String deserializeVoiceMailItemIds(String[] values, String phone) throws ServiceException {
        if (values == null) {
            return ""; 
        }
        StringBuilder builder = new StringBuilder(128);
        for (String value : values) {
            if (builder.length() > 0) {
                builder.append(',');
            }
            ZVoiceMailItemHitBean bean = ZVoiceMailItemHitBean.deserialize(value, phone);
            builder.append(bean.getId());
        }
        return builder.toString();
    }

	public static ZPhoneAccountBean getFirstPhoneAccount(PageContext pc) throws ServiceException, JspException {
		ZMailbox mbox = ZJspSession.getZMailbox(pc);
		List<ZPhoneAccount> accounts = mbox.getAllPhoneAccounts();
		return accounts.size() > 0 ? new ZPhoneAccountBean(accounts.get(0)) : null;
	}

	public static ZPhoneAccountBean getPhoneAccount(PageContext pc, String phone) throws ServiceException, JspException {
		ZMailbox mbox = ZJspSession.getZMailbox(pc);
		ZPhoneAccount account = mbox.getPhoneAccount(phone);
		return account == null ? null : new ZPhoneAccountBean(account);
	}

	public static Map<String, String> sPHONE_FIELDS = new HashMap<String, String>();
	static {
		sPHONE_FIELDS.put(ContactConstants.A_callbackPhone, "phoneLabelCallback");
		sPHONE_FIELDS.put(ContactConstants.A_carPhone, "phoneLabelCar");
		sPHONE_FIELDS.put(ContactConstants.A_assistantPhone, "phoneLabelAssistant");
		sPHONE_FIELDS.put(ContactConstants.A_companyPhone, "phoneLabelCompany");
		sPHONE_FIELDS.put(ContactConstants.A_homeFax, "phoneLabelHomeFax");
		sPHONE_FIELDS.put(ContactConstants.A_homePhone, "phoneLabelHome");
		sPHONE_FIELDS.put(ContactConstants.A_homePhone2, "phoneLabelHome2");
		sPHONE_FIELDS.put(ContactConstants.A_mobilePhone, "phoneLabelMobile");
		sPHONE_FIELDS.put(ContactConstants.A_otherPhone, "phoneLabelHomeAlternate");
		sPHONE_FIELDS.put(ContactConstants.A_workPhone, "phoneLabelWork");
		sPHONE_FIELDS.put(ContactConstants.A_workPhone2, "phoneLabelWork2");
		sPHONE_FIELDS.put(ContactConstants.A_homeFax, "phoneLabelHomeFax");
		sPHONE_FIELDS.put(ContactConstants.A_workFax, "AB_FIELD_workFax");
		sPHONE_FIELDS.put(ContactConstants.A_workMobile, "AB_FIELD_workMobile");
		sPHONE_FIELDS.put(ContactConstants.A_workAltPhone, "AB_FIELD_workAlternatePhone");
		sPHONE_FIELDS.put(ContactConstants.A_otherFax, "AB_FIELD_otherFax");
		sPHONE_FIELDS.put(ContactConstants.A_assistantPhone, "phoneLabelAssistant");
		sPHONE_FIELDS.put(ContactConstants.A_companyPhone, "AB_FIELD_company");
	}
	public static String getDisplayCaller(PageContext pc, ZPhone phone) throws ServiceException, JspException {
		ZMailbox mbox = ZJspSession.getZMailbox(pc);
		ZContactByPhoneCache.ContactPhone data = mbox.getContactByPhone(phone.getName());
		String phoneDisplay = phone.getDisplay();
		if (data != null) {
			String fileAsString = Contact.getFileAsString(data.getContact().getAttrs());
			if (fileAsString != null && fileAsString.length() > 0) {
				String field = I18nUtil.getLocalizedMessage(pc, sPHONE_FIELDS.get(data.getField()));
				return I18nUtil.getLocalizedMessage(pc, "callingPartyFormat",
						new Object[] {
								BeanUtils.cook(fileAsString),
								field,
								phoneDisplay
						});
			}
		}
		String callerId = phone.getCallerId();
		if (!StringUtil.isNullOrEmpty(callerId)) {
			return I18nUtil.getLocalizedMessage(pc, "callingPartyCallerIdFormat",
					new Object[] {
							BeanUtils.cook(callerId),
							phoneDisplay
					});
		}
		return phoneDisplay;
	}

	private static Pattern sPHONE_NAME = Pattern.compile("[^\\d]");
	private static String getPhoneName(String display) {
		return sPHONE_NAME.matcher(display).replaceAll("");
	}

	private static final Pattern sPHONE = Pattern.compile("[0-9()\\-\\s\\+]+");
	public static boolean isValidPhoneNumber(String number) {
		if (number == null) {
			return false;
		}
		int length = getPhoneName(number).length();
		return 7 <= length && length <= 20 && sPHONE.matcher(number).matches();
	}

    private static Pattern sEMAIL_ADDRESS = Pattern.compile(".+@.+\\.[a-z]+");
    public static boolean isValidEmailAddress(String email) {
        Matcher m = sEMAIL_ADDRESS.matcher(email);                          
        boolean matchFound = m.matches();
        if(matchFound)
           return true;
        else
            return false;

    }

    public static boolean isValidEmailAddresses(String emailline) {
        boolean validEmail = true;
        try {
            emailline = emailline.replace(";", ",");
            InternetAddress[] inetAddrs = InternetAddress.parseHeader(emailline, false);
            for (InternetAddress ia : inetAddrs) {
                Matcher m = sEMAIL_ADDRESS.matcher(ia.getAddress());                          
                boolean matchFound = m.matches();
                if(matchFound)
                    validEmail = true;
                else
                    validEmail =  false;
            }
            return validEmail;
        } catch ( AddressException e) {
            return false;
        }
    }

    public static boolean getIsMyCard(PageContext pc, String ids) throws ServiceException, JspException {
		ZMailbox mbox = ZJspSession.getZMailbox(pc);
		return mbox.getIsMyCard(ids);
	}
	
	/*
	 * Start Yahoo! code
	 */
	public static Calendar getYFirstDayOfMonthView(java.util.Calendar date, long prefFirstDayOfWeek) {
         prefFirstDayOfWeek++; // pref goes 0-6, Calendar goes 1-7
         Calendar cal = Calendar.getInstance(date.getTimeZone());
         cal.setTimeInMillis(date.getTimeInMillis());
         cal.set(Calendar.HOUR_OF_DAY, 0);
         cal.set(Calendar.MINUTE, 0);
         cal.set(Calendar.SECOND, 0);
         cal.set(Calendar.MILLISECOND, 0);
         cal.set(Calendar.DAY_OF_MONTH, 1);
         int dow = cal.get(Calendar.DAY_OF_WEEK);
         if (dow != prefFirstDayOfWeek) {
			cal.add(Calendar.DAY_OF_MONTH, - ((dow+(7-((int)prefFirstDayOfWeek)))%7));
         }
         return cal;
    }
	
	public static int getNumberOfWeeksOfMonth(java.util.Calendar date) {
        Calendar cal = (Calendar)date.clone();
        return cal.getActualMaximum(Calendar.WEEK_OF_MONTH);
    }

    /* End Yahoo! code */

	public static String getImagePath(PageContext pc, String relativePath) {
		final String ZIMBRA_IMAGE_SERVERS = "zimbraImageServers";
		String[] servers = (String[]) pc.getAttribute(ZIMBRA_IMAGE_SERVERS, PageContext.APPLICATION_SCOPE);
		if (servers == null) {
			String serverList = pc.getServletContext().getInitParameter(ZIMBRA_IMAGE_SERVERS);
			servers = (serverList == null || serverList.length() == 0) ? new String[0] : sCOMMA.split(serverList);
			for (int i = 0, count = servers.length; i < count; i++) {
				servers[i] = servers[i].trim();
			}
			pc.setAttribute(ZIMBRA_IMAGE_SERVERS, servers, PageContext.APPLICATION_SCOPE);
		}
		if (servers.length > 0) {
			// Generate the url for the image. Path starts with "//" to pick up current protocol.
			// The use of hashCode just ensures that for any given image, the same server is always used.  
			int index = Math.abs(relativePath.hashCode()) % servers.length;
			return "//" + servers[index] + ":" +  pc.getRequest().getServerPort() + relativePath;
		} else {
			return relativePath;
		}
	}

	/**
	 * "Cooks" the input string. (Removes special characters that can be used to create xss attacks.)
	 */
	public static String cook(String in) {
		return StringUtil.escapeHtml(in);
	}

	/**
	 * "Cooks" an input string where an integer is expected.
	 */
	public static int cookInt(String in, int defaultValue) {
		if (in == null || in.length() == 0) {
			return defaultValue;
		}
		try {
			return Integer.parseInt(in);
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}

	/**
	 * Returns true if the specified object appears in the given container.
	 * Container can be a Collection, an array, or a string.
	 */
	public static boolean contains(Object container, Object object) {
		if (container == null || object == null) {
			return false;
		}
		if (container instanceof Collection) {
			return ((Collection)container).contains(object);
		}
		if (container instanceof Object[]) {
			Object[] array = (Object[])container;
			for (Object item : array) {
				if (item.equals(object)) {
					return true;
				}
			}
			return false;
		}
		return String.valueOf(container).contains(String.valueOf(object));
	}

	public static String javaLocaleId(Locale locale) {
		// NOTE: Java uses has the wrong code for Indonesian
		// NOTE: http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6457127
		String locid = locale.toString();
		return locid.startsWith("in") ? locid.replaceAll("^in", "id") : locid;
	}
	public static String browserLocaleId(Locale locale) {
		return javaLocaleId(locale).toLowerCase().replace('_','-');
	}
    public static java.lang.String yuiCompress(java.lang.String s, String type){
        try{
            StringWriter out = new StringWriter();
            StringReader in = new StringReader(s);
            int linebreakpos = -1;
            if(type.equalsIgnoreCase("css")){
               CssCompressor compressor = new CssCompressor(in);
                in.close(); in = null;
                compressor.compress(out, linebreakpos);
                s = out.toString();
            }else if(type.equalsIgnoreCase("js")){
                JavaScriptCompressor jsc = new JavaScriptCompressor(in,new ErrorReporter() {

                        public void warning(String message, String sourceName,
                                int line, String lineSource, int lineOffset) {
                            if (line < 0) {
                                System.err.println("\nyuiCompress:[WARNING] " + message);
                            } else {
                                System.err.println("\nyuiCompress:[WARNING] " + line + ':' + lineOffset + ':' + message);
                            }
                        }

                        public void error(String message, String sourceName,
                                int line, String lineSource, int lineOffset) {
                            if (line < 0) {
                                System.err.println("\nyuiCompress:[ERROR] " + message);
                            } else {
                                System.err.println("\nyuiCompress:[ERROR] " + line + ':' + lineOffset + ':' + message);
                            }
                        }

                        public EvaluatorException runtimeError(String message, String sourceName,
                                int line, String lineSource, int lineOffset) {
                            error(message, sourceName, line, lineSource, lineOffset);
                            return new EvaluatorException(message);
                        }
                    });
                jsc.compress(out,linebreakpos,true,false,true,false);
                s = out.toString();
            }
        }catch (Exception ex){
           System.err.println("\nyuiCompress:[EXCEPTION] " + ex);
           ex.printStackTrace(System.err);
        }
        return s;
    }
}

class ExtendedDateFormatSymbols extends DateFormatSymbols{

    public ExtendedDateFormatSymbols(Locale locale, PageContext pc){
        super(locale);
        init(pc);
    }

    private void init(PageContext pc){

        String[] weekDays = new String[8], shortWeekDays=  new String[8], months = new String[12], shortMonths = new String[12];
        String[] dayNames = {"Sun","Mon", "Tue" ,"Wed", "Thu", "Fri" , "Sat"};
        String[] monthNames = {"Jan","Feb", "Mar" , "Apr", "May" , "Jun" , "Jul" , "Aug" , "Sep" ,"Oct" , "Nov" , "Dec"};

        for(int i=0;i<dayNames.length;i++){
            weekDays[i+1] = I18nUtil.getLocalizedMessage(pc,"weekday"+dayNames[i]+"Long", "/messages/I18nMsg");
            shortWeekDays[i+1] = I18nUtil.getLocalizedMessage(pc,"weekday"+dayNames[i]+"Medium", "/messages/I18nMsg");
        }

        this.setWeekdays(weekDays);
        this.setShortWeekdays(shortWeekDays);

        for(int i=0;i<monthNames.length;i++){
            months[i] = I18nUtil.getLocalizedMessage(pc,"month"+monthNames[i]+"Long", "/messages/I18nMsg");
            shortMonths[i] = I18nUtil.getLocalizedMessage(pc,"month"+monthNames[i]+"Medium", "/messages/I18nMsg");
        }

        this.setMonths(months);
        this.setShortMonths(shortMonths);
    }
}
