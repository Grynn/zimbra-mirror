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
 * <p>Java class for moveBlobsResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="moveBlobsResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *       &lt;/sequence>
 *       &lt;attribute name="numBlobsMoved" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="numBytesMoved" type="{http://www.w3.org/2001/XMLSchema}long" />
 *       &lt;attribute name="totalMailboxes" type="{http://www.w3.org/2001/XMLSchema}int" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "moveBlobsResponse")
public class testMoveBlobsResponse {

    @XmlAttribute(name = "numBlobsMoved")
    protected Integer numBlobsMoved;
    @XmlAttribute(name = "numBytesMoved")
    protected Long numBytesMoved;
    @XmlAttribute(name = "totalMailboxes")
    protected Integer totalMailboxes;

    /**
     * Gets the value of the numBlobsMoved property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getNumBlobsMoved() {
        return numBlobsMoved;
    }

    /**
     * Sets the value of the numBlobsMoved property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setNumBlobsMoved(Integer value) {
        this.numBlobsMoved = value;
    }

    /**
     * Gets the value of the numBytesMoved property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getNumBytesMoved() {
        return numBytesMoved;
    }

    /**
     * Sets the value of the numBytesMoved property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setNumBytesMoved(Long value) {
        this.numBytesMoved = value;
    }

    /**
     * Gets the value of the totalMailboxes property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getTotalMailboxes() {
        return totalMailboxes;
    }

    /**
     * Sets the value of the totalMailboxes property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setTotalMailboxes(Integer value) {
        this.totalMailboxes = value;
    }

}
