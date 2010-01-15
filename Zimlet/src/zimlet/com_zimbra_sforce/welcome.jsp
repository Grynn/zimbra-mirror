<!--
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Zimlets
 * Copyright (C) 2006, 2007, 2009 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.2 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
-->
<%@ page import="com.zimbra.cs.account.Provisioning" %>
<%@ page import="com.zimbra.cs.account.Account" %>
<%@ page import="com.zimbra.cs.account.AuthToken" %>
<%@ page import="com.zimbra.cs.mailbox.Mailbox" %>
<%@ page import="com.zimbra.cs.index.MailboxIndex" %>
<%@ page import="com.zimbra.cs.account.AuthTokenException" %>
<%@ page import="com.zimbra.cs.service.ServiceException" %>
<%@ page import="com.zimbra.cs.index.queryparser.ParseException" %>
<%@ page import="com.zimbra.cs.index.ZimbraQueryResults" %>
<%@ page import="com.zimbra.cs.index.ZimbraHit" %>
<%@ page import="com.zimbra.cs.index.MessageHit" %>
<%@ page import="java.util.Date" %>
<%@ page import="java.util.Collection" %>
<%@ page import="java.util.Iterator" %>
<%@ page import="com.zimbra.cs.mailbox.Appointment" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.Calendar" %>
<%
    Cookie[] cookies = request.getCookies();
    String authTokenString = "";
    for (Cookie cooky : cookies) {
        if (cooky.getName().equals("ZM_AUTH_TOKEN")) {
            authTokenString = cooky.getValue();
        }
    }
    ZimbraQueryResults inboxResults = null;
    Collection todayResults;
    String inboxHtml = "";
    String todayHtml = "";
    Calendar c = Calendar.getInstance();
    Date today = c.getTime();
    c.add(Calendar.DATE, 5);
    Date next = c.getTime();
    SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy h:mm a");
    try {
        AuthToken authToken = AuthToken.getAuthToken(authTokenString);
        Account acct = Provisioning.getInstance().getAccountById(authToken.getAccountId());
        Mailbox mbox = Mailbox.getMailboxByAccount(acct);
        Mailbox.OperationContext octxt = new Mailbox.OperationContext(acct);
        byte[] types = MailboxIndex.parseGroupByString(MailboxIndex.SEARCH_FOR_MESSAGES);
        inboxResults = mbox.search(octxt, "in:inbox", types, MailboxIndex.SortBy.DATE_DESCENDING, 10);
        StringBuilder sb = new StringBuilder();
        int i = 0;
        while (inboxResults.hasNext()) {
            if (i > 10) {
                break;
            }
            ZimbraHit hit = inboxResults.getNext();
            if (!(hit instanceof MessageHit)) {
                continue;
            }
            MessageHit mh = (MessageHit) hit;
            sb.append("<tr><td class='zimbraIcon'><div class='zimbraMsg'></div></td><td class='zimbraEmail'>");
            sb.append(mh.getSender());
            if (mh.getMessage().isUnread()) {
                sb.append("</td><td class='zimbraSubjectNew'><a target=\"_new\" href=\"");
            } else {
                sb.append("</td><td class='zimbraSubject'><a target=\"_new\" href=\"");
            }
            sb.append(request.getParameter("url"));
            sb.append("\">");
            String s = mh.getSubject();
            sb.append(s.substring(0, Math.min(55, s.length())));
            sb.append("</a></td><td class='zimbraDate'>");
            sb.append(df.format(mh.getDate()));
            sb.append("</td></tr><tr><td colspan=4 class=zimbraFragment>&nbsp;");
            String f = mh.getMessage().getFragment();
            sb.append(f.substring(0, Math.min(100, f.length())));
            sb.append("</td></tr>");
            i++;
        }
        inboxHtml = sb.toString();
        todayResults = mbox.getAppointmentsForRange(octxt, today.getTime(), next.getTime(), Mailbox.ID_FOLDER_CALENDAR, null);
        sb = new StringBuilder();
        i = 0;
        Iterator itr = todayResults.iterator();
        while (itr.hasNext()) {
            if (i > 10) {
                break;
            }
            Appointment appt = (Appointment) itr.next();
            sb.append("<tr><td class='zimbraIcon'><div class='zimbraAppt'></div></td><td class='zimbraSubject'><a target=\"_new\" href=\"");
            sb.append(request.getParameter("url"));
            sb.append("&app=calendar\">");
            String n = appt.getDefaultInvite().getName();
            sb.append(n.substring(0, Math.min(55, n.length())));
            sb.append(" (Location: ");
            String l = appt.getDefaultInvite().getLocation();
            sb.append(l.substring(0, Math.min(55, l.length())));
            sb.append(") ");
            sb.append("</a></td><td class='zimbraTime'>");
            sb.append(df.format(appt.getDefaultInvite().getStartTime().getDate()));
            sb.append("</td></tr><tr><td colspan=3 class=zimbraFragment>&nbsp;");
            String f = appt.getDefaultInvite().getFragment();
            sb.append(f.substring(0, Math.min(100, f.length())));
            sb.append("</td></tr>");
            i++;
        }
        todayHtml = sb.toString();
    } catch (AuthTokenException e) {
        e.printStackTrace();
    } catch (ServiceException e) {
        e.printStackTrace();
    } catch (ParseException e) {
        e.printStackTrace();
    } finally {
        if (inboxResults != null) {
            try {
                inboxResults.doneWithSearchResults();
            } catch (ServiceException e) {
                // Eat it.
            }
        }
    }
%>
<html>
<head>
    <style type="text/css">
        .zimbraToday, .zimbraHeader, .zimbraEmail, .zimbraSubject, .zimbraSubjectNew, .zimbraIcon,
            .zimbraFragment, .zimbraTime, .zimbraDate, .zimbraLink {
            font-family: Tahoma, sans-serif;
            font-size: 11px;
            white-space: nowrap;
            padding: 4 5 2 5;
        }

        .zimbraTable {
            height: 100%;
            border-bottom: 1px solid #666666;
        }

        .zimbraToday {
            background-color: #999999;
            border: solid #666666;
            border-width: 1;
            padding: 2 5 2 5;
            font-size: 18px;
            font-weight: bold;
            -moz-border-radius: 4 4 0 0;
        }

        .zimbraHeader {
            background-color: #cccccc;
            border: solid #666666;
            border-width: 1;
            padding: 2 5 2 5;
            -moz-border-radius: 4 4 0 0;
        }

        .zimbraHeader td {
            font-size: 12px;
            font-weight: bold;
        }

        .zimbraFragment {
            font-size: 9px;
            color: #666666;
            border-right: 1px solid #666666;
            border-left: 1px solid #666666;
            border-bottom: 1px solid #eeeeee;
            padding: 0 5 4 5;
        }

        .zimbraIcon {
            font-size: 7px;
            border-left: 1px solid #666666;
        }

        .zimbraMsg {
            width: 16px;
            height: 16px;
            background-image: url( "/service/zimlet/com_zimbra_sforce/Message.gif" );
        }

        .zimbraAppt {
            width: 16px;
            height: 16px;
            background-image: url( "/service/zimlet/com_zimbra_sforce/Appointment.gif" );
        }

        .zimbraSubject, .zimbraSubjectNew {
            width: 100%;
            color: darkblue;
            text-decoration: underline;
        }

        .zimbraSubjectNew {
            font-weight: bold;
        }

        .zimbraDate, .zimbraTime {
            border-right: 1px solid #666666;
        }

        .zimbraLink {
            text-align: right;
        }
    </style>
</head>

<body>
<table width=100% cellspacing=5>
    <tr><td colspan=2 class='zimbraToday'><a target="_new" href="http://www.zimbra.com"><img src='/img/loRes/logo/AppBanner.gif' border=0 /></a></td></tr>
    <tr><td valign=top width=50%>
        <div class='zimbraHeader'>
            <table width='100%' class='zimbraTable' style="border-bottom-width:0;" cellspacing=0 cellpadding=0>
                <tr><td colspan=3 class=''>Recent Messages</td><td class='zimbraLink'>
                    <a target="_new" href="<%=request.getParameter("url")%>">Mail</a></td>
                </tr>
            </table>
        </div>
        <table width='100%' class='zimbraTable' cellspacing=0 cellpadding=0>
            <%=inboxHtml%>
        </table>

    </td>
        <td valign=top width=50%>
            <div class='zimbraHeader'>
                <table width='100%' class='zimbraTable' style="border-bottom-width:0;" cellspacing=0 cellpadding=0>
                    <tr><td colspan=3 class=''>Upcoming Events</td><td class='zimbraLink'>
                        <a target="_new" href="<%=request.getParameter("url")%>&app=calendar">Calendar</a></td>
                    </tr>
                </table>
            </div>
            <table width='100%' class='zimbraTable' cellspacing=0 cellpadding=0>
                <%=todayHtml%>
            </table>
        </td>
</table>
</body>
</html>