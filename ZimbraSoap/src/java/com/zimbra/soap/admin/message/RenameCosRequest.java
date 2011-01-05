/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2010 Zimbra, Inc.
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

package com.zimbra.soap.admin.message;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.zimbra.common.soap.AdminConstants;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name=AdminConstants.E_RENAME_COS_REQUEST)
@XmlType(propOrder = {})
public class RenameCosRequest {

    @XmlAttribute(name=AdminConstants.E_ID, required=true)
    private String id;
    @XmlAttribute(name=AdminConstants.E_NEW_NAME, required=true)
    private String newName;

    public RenameCosRequest() {
    }

    public void setId(String id) { this.id = id; }
    public void setNewName(String newName) { this.newName = newName; }

    public String getId() { return id; }
    public String getNewName() { return newName; }
}
