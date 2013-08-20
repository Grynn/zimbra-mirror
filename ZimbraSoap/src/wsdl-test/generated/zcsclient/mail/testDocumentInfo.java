/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2011, 2013 Zimbra Software, LLC.
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

package generated.zcsclient.mail;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for documentInfo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="documentInfo">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:zimbraMail}commonDocumentInfo">
 *       &lt;sequence>
 *       &lt;/sequence>
 *       &lt;attribute name="loid" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="loe" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="lt" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "documentInfo")
@XmlSeeAlso({
    testDocumentHitInfo.class
})
public class testDocumentInfo
    extends testCommonDocumentInfo
{

    @XmlAttribute(name = "loid")
    protected String loid;
    @XmlAttribute(name = "loe")
    protected String loe;
    @XmlAttribute(name = "lt")
    protected String lt;

    /**
     * Gets the value of the loid property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLoid() {
        return loid;
    }

    /**
     * Sets the value of the loid property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLoid(String value) {
        this.loid = value;
    }

    /**
     * Gets the value of the loe property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLoe() {
        return loe;
    }

    /**
     * Sets the value of the loe property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLoe(String value) {
        this.loe = value;
    }

    /**
     * Gets the value of the lt property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLt() {
        return lt;
    }

    /**
     * Sets the value of the lt property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLt(String value) {
        this.lt = value;
    }

}
