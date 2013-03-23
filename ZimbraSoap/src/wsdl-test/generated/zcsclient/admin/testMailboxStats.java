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
 * <p>Java class for mailboxStats complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="mailboxStats">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *       &lt;/all>
 *       &lt;attribute name="numMboxes" use="required" type="{http://www.w3.org/2001/XMLSchema}long" />
 *       &lt;attribute name="totalSize" use="required" type="{http://www.w3.org/2001/XMLSchema}long" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "mailboxStats", propOrder = {

})
public class testMailboxStats {

    @XmlAttribute(name = "numMboxes", required = true)
    protected long numMboxes;
    @XmlAttribute(name = "totalSize", required = true)
    protected long totalSize;

    /**
     * Gets the value of the numMboxes property.
     * 
     */
    public long getNumMboxes() {
        return numMboxes;
    }

    /**
     * Sets the value of the numMboxes property.
     * 
     */
    public void setNumMboxes(long value) {
        this.numMboxes = value;
    }

    /**
     * Gets the value of the totalSize property.
     * 
     */
    public long getTotalSize() {
        return totalSize;
    }

    /**
     * Sets the value of the totalSize property.
     * 
     */
    public void setTotalSize(long value) {
        this.totalSize = value;
    }

}
