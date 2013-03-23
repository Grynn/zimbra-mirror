/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2011, 2012 VMware, Inc.
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

package generated.zcsclient.mail;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for syncDeletedInfo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="syncDeletedInfo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;choice maxOccurs="unbounded" minOccurs="0">
 *           &lt;element name="folder" type="{urn:zimbraMail}idsAttr"/>
 *           &lt;element name="search" type="{urn:zimbraMail}idsAttr"/>
 *           &lt;element name="link" type="{urn:zimbraMail}idsAttr"/>
 *           &lt;element name="tag" type="{urn:zimbraMail}idsAttr"/>
 *           &lt;element name="c" type="{urn:zimbraMail}idsAttr"/>
 *           &lt;element name="chat" type="{urn:zimbraMail}idsAttr"/>
 *           &lt;element name="m" type="{urn:zimbraMail}idsAttr"/>
 *           &lt;element name="cn" type="{urn:zimbraMail}idsAttr"/>
 *           &lt;element name="appt" type="{urn:zimbraMail}idsAttr"/>
 *           &lt;element name="task" type="{urn:zimbraMail}idsAttr"/>
 *           &lt;element name="notes" type="{urn:zimbraMail}idsAttr"/>
 *           &lt;element name="w" type="{urn:zimbraMail}idsAttr"/>
 *           &lt;element name="doc" type="{urn:zimbraMail}idsAttr"/>
 *         &lt;/choice>
 *       &lt;/sequence>
 *       &lt;attribute name="ids" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "syncDeletedInfo", propOrder = {
    "folderOrSearchOrLink"
})
public class testSyncDeletedInfo {

    @XmlElementRefs({
        @XmlElementRef(name = "c", namespace = "urn:zimbraMail", type = JAXBElement.class),
        @XmlElementRef(name = "folder", namespace = "urn:zimbraMail", type = JAXBElement.class),
        @XmlElementRef(name = "cn", namespace = "urn:zimbraMail", type = JAXBElement.class),
        @XmlElementRef(name = "w", namespace = "urn:zimbraMail", type = JAXBElement.class),
        @XmlElementRef(name = "chat", namespace = "urn:zimbraMail", type = JAXBElement.class),
        @XmlElementRef(name = "task", namespace = "urn:zimbraMail", type = JAXBElement.class),
        @XmlElementRef(name = "m", namespace = "urn:zimbraMail", type = JAXBElement.class),
        @XmlElementRef(name = "notes", namespace = "urn:zimbraMail", type = JAXBElement.class),
        @XmlElementRef(name = "link", namespace = "urn:zimbraMail", type = JAXBElement.class),
        @XmlElementRef(name = "appt", namespace = "urn:zimbraMail", type = JAXBElement.class),
        @XmlElementRef(name = "search", namespace = "urn:zimbraMail", type = JAXBElement.class),
        @XmlElementRef(name = "doc", namespace = "urn:zimbraMail", type = JAXBElement.class),
        @XmlElementRef(name = "tag", namespace = "urn:zimbraMail", type = JAXBElement.class)
    })
    protected List<JAXBElement<testIdsAttr>> folderOrSearchOrLink;
    @XmlAttribute(name = "ids", required = true)
    protected String ids;

    /**
     * Gets the value of the folderOrSearchOrLink property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the folderOrSearchOrLink property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getFolderOrSearchOrLink().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link JAXBElement }{@code <}{@link testIdsAttr }{@code >}
     * {@link JAXBElement }{@code <}{@link testIdsAttr }{@code >}
     * {@link JAXBElement }{@code <}{@link testIdsAttr }{@code >}
     * {@link JAXBElement }{@code <}{@link testIdsAttr }{@code >}
     * {@link JAXBElement }{@code <}{@link testIdsAttr }{@code >}
     * {@link JAXBElement }{@code <}{@link testIdsAttr }{@code >}
     * {@link JAXBElement }{@code <}{@link testIdsAttr }{@code >}
     * {@link JAXBElement }{@code <}{@link testIdsAttr }{@code >}
     * {@link JAXBElement }{@code <}{@link testIdsAttr }{@code >}
     * {@link JAXBElement }{@code <}{@link testIdsAttr }{@code >}
     * {@link JAXBElement }{@code <}{@link testIdsAttr }{@code >}
     * {@link JAXBElement }{@code <}{@link testIdsAttr }{@code >}
     * {@link JAXBElement }{@code <}{@link testIdsAttr }{@code >}
     * 
     * 
     */
    public List<JAXBElement<testIdsAttr>> getFolderOrSearchOrLink() {
        if (folderOrSearchOrLink == null) {
            folderOrSearchOrLink = new ArrayList<JAXBElement<testIdsAttr>>();
        }
        return this.folderOrSearchOrLink;
    }

    /**
     * Gets the value of the ids property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIds() {
        return ids;
    }

    /**
     * Sets the value of the ids property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIds(String value) {
        this.ids = value;
    }

}
