/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2010, 2012, 2013 Zimbra Software, LLC.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.4 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */

package com.zimbra.soap.mail.message;

import com.google.common.base.Objects;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.zimbra.common.soap.MailConstants;
import com.zimbra.soap.mail.type.ImportContact;

@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name=MailConstants.E_IMPORT_CONTACTS_RESPONSE)
public class ImportContactsResponse {

    /**
     * @zm-api-field-description Information about the import process
     */
    @XmlElement(name=MailConstants.E_CONTACT /* cn */, required=true)
    private ImportContact contact;

    public ImportContactsResponse() {
    }

    public void setContact(ImportContact contact) { this.contact = contact; }
    public ImportContact getContact() { return contact; }

    public Objects.ToStringHelper addToStringInfo(Objects.ToStringHelper helper) {
        return helper
            .add("contact", contact);
    }

    @Override
    public String toString() {
        return addToStringInfo(Objects.toStringHelper(this)).toString();
    }
}
