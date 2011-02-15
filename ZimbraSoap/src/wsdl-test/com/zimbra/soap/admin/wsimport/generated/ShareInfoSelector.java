
package com.zimbra.soap.admin.wsimport.generated;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for shareInfoSelector complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="shareInfoSelector">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="folder" type="{urn:zimbraAdmin}publishFolderInfo"/>
 *         &lt;element name="owner" type="{urn:zimbraAdmin}accountSelector" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="action" use="required" type="{urn:zimbraAdmin}pubShareInfoAction" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "shareInfoSelector", propOrder = {
    "folder",
    "owner"
})
public class ShareInfoSelector {

    @XmlElement(required = true)
    protected PublishFolderInfo folder;
    protected AccountSelector owner;
    @XmlAttribute(required = true)
    protected PubShareInfoAction action;

    /**
     * Gets the value of the folder property.
     * 
     * @return
     *     possible object is
     *     {@link PublishFolderInfo }
     *     
     */
    public PublishFolderInfo getFolder() {
        return folder;
    }

    /**
     * Sets the value of the folder property.
     * 
     * @param value
     *     allowed object is
     *     {@link PublishFolderInfo }
     *     
     */
    public void setFolder(PublishFolderInfo value) {
        this.folder = value;
    }

    /**
     * Gets the value of the owner property.
     * 
     * @return
     *     possible object is
     *     {@link AccountSelector }
     *     
     */
    public AccountSelector getOwner() {
        return owner;
    }

    /**
     * Sets the value of the owner property.
     * 
     * @param value
     *     allowed object is
     *     {@link AccountSelector }
     *     
     */
    public void setOwner(AccountSelector value) {
        this.owner = value;
    }

    /**
     * Gets the value of the action property.
     * 
     * @return
     *     possible object is
     *     {@link PubShareInfoAction }
     *     
     */
    public PubShareInfoAction getAction() {
        return action;
    }

    /**
     * Sets the value of the action property.
     * 
     * @param value
     *     allowed object is
     *     {@link PubShareInfoAction }
     *     
     */
    public void setAction(PubShareInfoAction value) {
        this.action = value;
    }

}
