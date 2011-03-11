
package com.zimbra.soap.admin.wsimport.generated;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for adminWaitSetRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="adminWaitSetRequest">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="add" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="addAccounts" type="{urn:zimbraAdmin}waitSetAddSpec" maxOccurs="unbounded" minOccurs="0"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="update" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="a" type="{urn:zimbraAdmin}waitSetAddSpec" maxOccurs="unbounded" minOccurs="0"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="remove" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="a" type="{urn:zimbraAdmin}id" maxOccurs="unbounded" minOccurs="0"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *       &lt;attribute name="waitSet" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="seq" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="block" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="defTypes" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="timeout" use="required" type="{http://www.w3.org/2001/XMLSchema}long" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "adminWaitSetRequest", propOrder = {
    "add",
    "update",
    "remove"
})
public class AdminWaitSetRequest {

    protected AdminWaitSetRequest.Add add;
    protected AdminWaitSetRequest.Update update;
    protected AdminWaitSetRequest.Remove remove;
    @XmlAttribute(required = true)
    protected String waitSet;
    @XmlAttribute(required = true)
    protected String seq;
    @XmlAttribute
    protected Boolean block;
    @XmlAttribute
    protected String defTypes;
    @XmlAttribute(required = true)
    protected long timeout;

    /**
     * Gets the value of the add property.
     * 
     * @return
     *     possible object is
     *     {@link AdminWaitSetRequest.Add }
     *     
     */
    public AdminWaitSetRequest.Add getAdd() {
        return add;
    }

    /**
     * Sets the value of the add property.
     * 
     * @param value
     *     allowed object is
     *     {@link AdminWaitSetRequest.Add }
     *     
     */
    public void setAdd(AdminWaitSetRequest.Add value) {
        this.add = value;
    }

    /**
     * Gets the value of the update property.
     * 
     * @return
     *     possible object is
     *     {@link AdminWaitSetRequest.Update }
     *     
     */
    public AdminWaitSetRequest.Update getUpdate() {
        return update;
    }

    /**
     * Sets the value of the update property.
     * 
     * @param value
     *     allowed object is
     *     {@link AdminWaitSetRequest.Update }
     *     
     */
    public void setUpdate(AdminWaitSetRequest.Update value) {
        this.update = value;
    }

    /**
     * Gets the value of the remove property.
     * 
     * @return
     *     possible object is
     *     {@link AdminWaitSetRequest.Remove }
     *     
     */
    public AdminWaitSetRequest.Remove getRemove() {
        return remove;
    }

    /**
     * Sets the value of the remove property.
     * 
     * @param value
     *     allowed object is
     *     {@link AdminWaitSetRequest.Remove }
     *     
     */
    public void setRemove(AdminWaitSetRequest.Remove value) {
        this.remove = value;
    }

    /**
     * Gets the value of the waitSet property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getWaitSet() {
        return waitSet;
    }

    /**
     * Sets the value of the waitSet property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setWaitSet(String value) {
        this.waitSet = value;
    }

    /**
     * Gets the value of the seq property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSeq() {
        return seq;
    }

    /**
     * Sets the value of the seq property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSeq(String value) {
        this.seq = value;
    }

    /**
     * Gets the value of the block property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isBlock() {
        return block;
    }

    /**
     * Sets the value of the block property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setBlock(Boolean value) {
        this.block = value;
    }

    /**
     * Gets the value of the defTypes property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDefTypes() {
        return defTypes;
    }

    /**
     * Sets the value of the defTypes property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDefTypes(String value) {
        this.defTypes = value;
    }

    /**
     * Gets the value of the timeout property.
     * 
     */
    public long getTimeout() {
        return timeout;
    }

    /**
     * Sets the value of the timeout property.
     * 
     */
    public void setTimeout(long value) {
        this.timeout = value;
    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="addAccounts" type="{urn:zimbraAdmin}waitSetAddSpec" maxOccurs="unbounded" minOccurs="0"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "addAccounts"
    })
    public static class Add {

        @XmlElement(nillable = true)
        protected List<WaitSetAddSpec> addAccounts;

        /**
         * Gets the value of the addAccounts property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the addAccounts property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getAddAccounts().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link WaitSetAddSpec }
         * 
         * 
         */
        public List<WaitSetAddSpec> getAddAccounts() {
            if (addAccounts == null) {
                addAccounts = new ArrayList<WaitSetAddSpec>();
            }
            return this.addAccounts;
        }

    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="a" type="{urn:zimbraAdmin}id" maxOccurs="unbounded" minOccurs="0"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "a"
    })
    public static class Remove {

        protected List<Id> a;

        /**
         * Gets the value of the a property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the a property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getA().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link Id }
         * 
         * 
         */
        public List<Id> getA() {
            if (a == null) {
                a = new ArrayList<Id>();
            }
            return this.a;
        }

    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="a" type="{urn:zimbraAdmin}waitSetAddSpec" maxOccurs="unbounded" minOccurs="0"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "a"
    })
    public static class Update {

        protected List<WaitSetAddSpec> a;

        /**
         * Gets the value of the a property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the a property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getA().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link WaitSetAddSpec }
         * 
         * 
         */
        public List<WaitSetAddSpec> getA() {
            if (a == null) {
                a = new ArrayList<WaitSetAddSpec>();
            }
            return this.a;
        }

    }

}
