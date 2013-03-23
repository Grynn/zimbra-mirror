/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2011 VMware, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.3 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * 
 * ***** END LICENSE BLOCK *****
 */

package generated.zcsclient.admin;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for mailboxVersionInfo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="mailboxVersionInfo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *       &lt;/sequence>
 *       &lt;attribute name="mbxid" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="majorVer" use="required" type="{http://www.w3.org/2001/XMLSchema}short" />
 *       &lt;attribute name="minorVer" use="required" type="{http://www.w3.org/2001/XMLSchema}short" />
 *       &lt;attribute name="dbVer" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="indexVer" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "mailboxVersionInfo")
public class testMailboxVersionInfo {

    @XmlAttribute(name = "mbxid", required = true)
    protected int mbxid;
    @XmlAttribute(name = "majorVer", required = true)
    protected short majorVer;
    @XmlAttribute(name = "minorVer", required = true)
    protected short minorVer;
    @XmlAttribute(name = "dbVer", required = true)
    protected int dbVer;
    @XmlAttribute(name = "indexVer", required = true)
    protected int indexVer;

    /**
     * Gets the value of the mbxid property.
     * 
     */
    public int getMbxid() {
        return mbxid;
    }

    /**
     * Sets the value of the mbxid property.
     * 
     */
    public void setMbxid(int value) {
        this.mbxid = value;
    }

    /**
     * Gets the value of the majorVer property.
     * 
     */
    public short getMajorVer() {
        return majorVer;
    }

    /**
     * Sets the value of the majorVer property.
     * 
     */
    public void setMajorVer(short value) {
        this.majorVer = value;
    }

    /**
     * Gets the value of the minorVer property.
     * 
     */
    public short getMinorVer() {
        return minorVer;
    }

    /**
     * Sets the value of the minorVer property.
     * 
     */
    public void setMinorVer(short value) {
        this.minorVer = value;
    }

    /**
     * Gets the value of the dbVer property.
     * 
     */
    public int getDbVer() {
        return dbVer;
    }

    /**
     * Sets the value of the dbVer property.
     * 
     */
    public void setDbVer(int value) {
        this.dbVer = value;
    }

    /**
     * Gets the value of the indexVer property.
     * 
     */
    public int getIndexVer() {
        return indexVer;
    }

    /**
     * Sets the value of the indexVer property.
     * 
     */
    public void setIndexVer(int value) {
        this.indexVer = value;
    }

}
