/*
 * ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1
 * 
 * The contents of this file are subject to the Mozilla Public License
 * Version 1.1 ("License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.zimbra.com/license
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
 * the License for the specific language governing rights and limitations
 * under the License.
 * 
 * The Original Code is: Zimbra Collaboration Suite Server.
 * 
 * The Initial Developer of the Original Code is Zimbra, Inc.
 * Portions created by Zimbra are Copyright (C) 2006 Zimbra, Inc.
 * All Rights Reserved.
 * 
 * Contributor(s): 
 * 
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.cs.taglib.bean;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.taglib.ZJspSession;
import com.zimbra.cs.zclient.ZEmailAddress;
import com.zimbra.cs.zclient.ZFilterAction;
import com.zimbra.cs.zclient.ZFilterAction.ZDiscardAction;
import com.zimbra.cs.zclient.ZFilterAction.ZFileIntoAction;
import com.zimbra.cs.zclient.ZFilterAction.ZKeepAction;
import com.zimbra.cs.zclient.ZFilterAction.ZMarkAction;
import com.zimbra.cs.zclient.ZFilterAction.ZRedirectAction;
import com.zimbra.cs.zclient.ZFilterAction.ZStopAction;
import com.zimbra.cs.zclient.ZFilterAction.ZTagAction;
import com.zimbra.cs.zclient.ZFilterCondition;
import com.zimbra.cs.zclient.ZFilterCondition.ZAddressBookCondition;
import com.zimbra.cs.zclient.ZFilterCondition.ZAttachmentExistsCondition;
import com.zimbra.cs.zclient.ZFilterCondition.ZBodyCondition;
import com.zimbra.cs.zclient.ZFilterCondition.ZDateCondition;
import com.zimbra.cs.zclient.ZFilterCondition.ZHeaderCondition;
import com.zimbra.cs.zclient.ZFilterCondition.ZHeaderExistsCondition;
import com.zimbra.cs.zclient.ZFilterCondition.ZSizeCondition;
import com.zimbra.cs.zclient.ZFolder;
import com.zimbra.cs.zclient.ZMailbox;
import com.zimbra.cs.zclient.ZTag;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.jstl.fmt.LocaleSupport;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    private static String replaceAll(String text, Pattern pattern, String replace) {
        Matcher m = pattern.matcher(text);
        StringBuffer sb = null;
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

    public static String prefixContent(String content, String prefix) {
        return replaceAll(content, sSTART, prefix);
    }

    private static String htmlEncode(String text) {
        if (text == null || text.length() == 0) return "";
        String s = replaceAll(text, sAMP, "&amp;");
        s = replaceAll(s, sLT, "&lt;");
        s = replaceAll(s, sGT, "&gt;");
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
    
    public static String displaySize(long size) {
        return displaySize(size, 0);
    }

    public static String displaySize(long size, int fractions) {
        String units;
        double dsize;
        if (size >= 1073741824) {
            dsize = size/1073741824.0;
            units = " GB";
        } else if (size >= 1048576) {
            dsize = size/1048576.0;
            units = " MB";
        } else if (size >= 1024) {
            dsize = size/1024.0;
            units = " KB";
        } else {
            dsize = size;
            units = " B";
        }

        if (fractions == 0) {
            return Math.round(dsize) + units;
        } else {
            String str = String.format("%."+fractions+"f", dsize);
            int p = str.length()-1;
            if (fractions > 0 && str.charAt(p) == '0') {
                while (str.charAt(p) == '0' && p > 0) p--;
                if (str.charAt(p) == '.') p--;
                str = str.substring(0, p+1);
            }
            return str + units;
        }
    }

    private enum DateTimeFmt { DTF_TIME_SHORT, DTF_DATE_MEDIUM, DTF_DATE_SHORT }
   
    private static DateFormat getDateFormat(PageContext pc, DateTimeFmt fmt) {
        DateFormat df = (DateFormat) pc.getAttribute(fmt.name(), PageContext.REQUEST_SCOPE);
        if (df == null) {
            switch (fmt) {
            case DTF_DATE_MEDIUM:
                df = new SimpleDateFormat(LocaleSupport.getLocalizedMessage(pc, "ZM_formatDateMediumNoYear"));
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

    public static String displayMsgDate(PageContext pc, Date msg) {
        GregorianCalendar cal = new GregorianCalendar();
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.HOUR, 0);
        long nowTime = cal.getTimeInMillis();
        long msgTime = msg.getTime();
        
        if (msgTime >= nowTime) {
            // show hour and return
            return getDateFormat(pc, DateTimeFmt.DTF_TIME_SHORT).format(msg);
        }
        
        long nowYear = cal.get(Calendar.YEAR);
        cal.setTimeInMillis(msgTime);
        long msgYear = cal.get(Calendar.YEAR);
        
        if (nowYear == msgYear) {
            return getDateFormat(pc, DateTimeFmt.DTF_DATE_MEDIUM).format(msg);            
        } else {
            return getDateFormat(pc, DateTimeFmt.DTF_DATE_SHORT).format(msg);                        
        }
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

    public static String getFolderName(PageContext pc, String id) throws JspException, ServiceException {
        ZMailbox mbox = ZJspSession.getZMailbox(pc);
        if (id == null) return null;
        ZFolder f = mbox.getFolderById(id);
        return f == null ? null : f.getName();
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

    public static Calendar getCalendar(java.util.Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(date.getTime());
        cal.set(Calendar.HOUR, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal;
    }

    public static Calendar getToday() {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());
        cal.set(Calendar.HOUR, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal;
    }

    public static Calendar getFirstDayOfMonthView(java.util.Date date, long prefFirstDayOfWeek) {
         prefFirstDayOfWeek++; // pref goes 0-6, Calendar goes 1-7
         Calendar cal = Calendar.getInstance();
         cal.setTime(date);
         cal.set(Calendar.HOUR, 0);
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

    public static void getNextDay(Calendar cal) {
        cal.add(Calendar.DAY_OF_MONTH, 1);
    }

    public static Calendar pageMonth(Calendar cal, boolean forward) {
        Calendar other = Calendar.getInstance();
        other.setTimeInMillis(cal.getTimeInMillis());
        other.roll(Calendar.MONTH, forward);
        if (forward && other.get(Calendar.MONTH) == Calendar.JANUARY) {
            other.roll(Calendar.YEAR, forward);
        } else if (!forward && other.get(Calendar.MONTH) == Calendar.DECEMBER) {
            other.roll(Calendar.YEAR, forward);
        }
        return other;
    }

    public static Calendar relativeDay(Calendar cal, int offset) {
        Calendar other = Calendar.getInstance();
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

}
