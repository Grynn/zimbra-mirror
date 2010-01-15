/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2008, 2009 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.2 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.cs.offline.ab.gab;

import com.zimbra.cs.offline.OfflineLC;
import com.zimbra.cs.offline.OfflineLog;
import com.zimbra.cs.offline.ab.Ab;
import com.zimbra.cs.mailbox.Contact.Attachment;
import com.zimbra.common.mailbox.ContactConstants;
import com.zimbra.common.service.ServiceException;
import com.zimbra.common.util.Log;
import com.google.gdata.client.contacts.ContactsService;
import com.google.gdata.client.Query;
import com.google.gdata.client.Service.GDataRequest;
import com.google.gdata.client.Service.GDataRequest.RequestType;
import com.google.gdata.util.AuthenticationException;
import com.google.gdata.util.VersionConflictException;
import com.google.gdata.util.ContentType;
import com.google.gdata.util.common.xml.XmlWriter;
import com.google.gdata.data.BaseEntry;
import com.google.gdata.data.ParseSource;
import com.google.gdata.data.BaseFeed;
import com.google.gdata.data.DateTime;
import com.google.gdata.data.Link;
import com.google.gdata.data.contacts.ContactEntry;
import com.google.gdata.data.contacts.ContactFeed;
import com.google.gdata.data.contacts.ContactGroupFeed;

import java.net.URL;
import java.io.StringWriter;
import java.io.IOException;
import java.io.StringReader;
import java.util.EnumSet;

class GabService {
    private final ContactsService cs;
    private final URL contactFeedUrl;
    private final URL groupFeedUrl;

    private static final Log LOG = OfflineLog.gab;

    GabService(String user, String pass) throws ServiceException {
        cs = new ContactsService(Gab.APP_NAME);
        cs.setReadTimeout(OfflineLC.http_so_timeout.intValue());
        cs.setConnectTimeout(OfflineLC.http_connection_timeout.intValue());
        contactFeedUrl = Gab.toUrl(Gab.BASE_URL + Gab.CONTACTS + user + "/full");
        groupFeedUrl = Gab.toUrl(Gab.BASE_URL + Gab.GROUPS + user + "/full");
        authenticate(user, pass);
    }

    private void authenticate(String user, String pass) throws ServiceException {
        try {
            cs.setUserCredentials(user, pass);
        } catch (AuthenticationException e) {
            throw ServiceException.FAILURE("Google address book authentication failed", e);
        }
    }

    public ContactFeed getContactFeed(DateTime updatedMin, DateTime updatedMax)
        throws IOException, ServiceException {
        return getFeed(contactFeedUrl, ContactFeed.class, updatedMin, updatedMax);
    }

    public ContactGroupFeed getGroupFeed(DateTime updatedMin, DateTime updatedMax)
        throws IOException, ServiceException {
        return getFeed(groupFeedUrl, ContactGroupFeed.class, updatedMin, updatedMax);
    }

    private <T extends BaseFeed> T getFeed(URL feedUrl, Class <T> feedClass,
                                           DateTime updatedMin, DateTime updatedMax)
        throws IOException, ServiceException {
        try {
            Query query = new Query(feedUrl);
            query.setMaxResults(9999999);
            query.setUpdatedMax(updatedMax);
            if (updatedMin != null) {
                // Only show deleted entries if this is not initial sync
                query.setUpdatedMin(updatedMin);
                query.setStringCustomParameter("showdeleted", "true");
            }
            return cs.getFeed(query, feedClass, updatedMin);
        } catch (com.google.gdata.util.ServiceException e) {
            throw ServiceException.FAILURE(
                "Unable to retrieve feed: " + feedUrl, e);
        }
    }

    public <T extends BaseEntry> T parseEntry(String xml, Class<T> entryClass)
        throws ServiceException {
        try {
            return BaseEntry.readEntry(new ParseSource(new StringReader(xml)),
                                       entryClass, cs.getExtensionProfile());
        } catch (Exception e) {
            throw ServiceException.FAILURE("Unable to parse contact data", e);
        }
    }

    public String pp(BaseEntry entry) {
        try {
            StringWriter sw = new StringWriter();
            XmlWriter xw = new XmlWriter(sw,
                EnumSet.of(XmlWriter.WriterFlags.PRETTY_PRINT), null);
            entry.generateAtom(xw, cs.getExtensionProfile());
            return sw.toString();
        } catch (IOException e) {
            return "<Unavailable>";
        }
    }

    public String toXml(BaseEntry entry) throws ServiceException {
        StringWriter sw = new StringWriter();
        try {
            entry.generateAtom(new XmlWriter(sw), cs.getExtensionProfile());
            return sw.toString();
        } catch (IOException e) {
            throw ServiceException.FAILURE(
                "Unable to generate XML for entry id " + entry.getId() , e);
        }
    }
    
    public BaseEntry getCurrentEntry(VersionConflictException e,
                                     Class<? extends BaseEntry> entryClass)
        throws ServiceException, IOException {
        String xml = e.getResponseBody();
        if (xml == null) {
            throw ServiceException.FAILURE("Missing response body", null);
        }
        return parseEntry(xml, entryClass);
    }

    public <T extends BaseEntry> T insert(T entry)
        throws IOException, com.google.gdata.util.ServiceException {
        return cs.insert(getFeedUrl(entry), entry);
    }

    public <T extends BaseEntry> T update(T entry)
        throws IOException, com.google.gdata.util.ServiceException {
        // Perform unconditional update
        return cs.update(Gab.getEditUrl(entry), entry, "*");
    }

    public void delete(BaseEntry entry)
        throws IOException, com.google.gdata.util.ServiceException {
        // Perform unconditional delete
        cs.delete(Gab.getEditUrl(entry), "*");
    }

    public void addPhoto(ContactEntry entry, byte[] data, String type)
        throws IOException, com.google.gdata.util.ServiceException {
        URL url = new URL(entry.getContactPhotoLink().getHref());
        GDataRequest req = cs.createRequest(RequestType.UPDATE, url, new ContentType(type));
        req.setEtag("*"); // Send unconditional request
        req.getRequestStream().write(data);
        req.execute();
        LOG.debug("Updated or added photo for entry %s: edit url = %s, size = %d, type = %s",
                  entry.getId(), url, data.length, type);
    }

    public Attachment getPhoto(ContactEntry entry)
        throws IOException, com.google.gdata.util.ServiceException {
        Link link = entry.getContactPhotoLink();
        if (link == null || link.getEtag() == null) {
            return null; // No photo to delete
        }
        URL url = new URL(link.getHref());
        GDataRequest req = cs.createRequest(RequestType.QUERY, url, null);
        req.execute();
        ContentType ctype = req.getResponseContentType();
        byte[] content = Ab.readFully(req.getResponseStream());
        LOG.debug("Retrieved photo for entry %s: size = %d, type = %s",
                  entry.getId(), content.length, ctype.getMediaType());
        return new Attachment(content, ctype.getMediaType(), ContactConstants.A_image, null);
    }

    public void deletePhoto(ContactEntry entry)
        throws IOException, com.google.gdata.util.ServiceException {
        URL url = new URL(entry.getContactPhotoLink().getHref());
        cs.delete(url, "*"); // Unconditional delete
        LOG.debug("Deleted photo for entry %s: edit url = %s", entry.getId(), url);
    }

    private URL getFeedUrl(BaseEntry entry) {
        return entry.getClass() == ContactEntry.class ? contactFeedUrl : groupFeedUrl;
    }
}
