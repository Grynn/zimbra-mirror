
package com.zimbra.soap.admin.wsimport.generated;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for waitSetInfo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="waitSetInfo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="session" type="{urn:zimbraAdmin}sessionForWaitSet" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="errors" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="error" type="{urn:zimbraAdmin}idAndType" maxOccurs="unbounded" minOccurs="0"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="ready" type="{urn:zimbraAdmin}accountsAttrib" minOccurs="0"/>
 *         &lt;element name="buffered" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="commit" type="{urn:zimbraAdmin}bufferedCommitInfo" maxOccurs="unbounded" minOccurs="0"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *       &lt;attribute name="id" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="owner" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="defTypes" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="ld" use="required" type="{http://www.w3.org/2001/XMLSchema}long" />
 *       &lt;attribute name="cbSeqNo" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="currentSeqNo" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="nextSeqNo" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "waitSetInfo", propOrder = {
    "session",
    "errors",
    "ready",
    "buffered"
})
public class WaitSetInfo {

    protected List<SessionForWaitSet> session;
    protected WaitSetInfo.Errors errors;
    protected AccountsAttrib ready;
    protected WaitSetInfo.Buffered buffered;
    @XmlAttribute(required = true)
    protected String id;
    @XmlAttribute(required = true)
    protected String owner;
    @XmlAttribute(required = true)
    protected String defTypes;
    @XmlAttribute(required = true)
    protected long ld;
    @XmlAttribute
    protected String cbSeqNo;
    @XmlAttribute
    protected String currentSeqNo;
    @XmlAttribute
    protected String nextSeqNo;

    /**
     * Gets the value of the session property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the session property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSession().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SessionForWaitSet }
     * 
     * 
     */
    public List<SessionForWaitSet> getSession() {
        if (session == null) {
            session = new ArrayList<SessionForWaitSet>();
        }
        return this.session;
    }

    /**
     * Gets the value of the errors property.
     * 
     * @return
     *     possible object is
     *     {@link WaitSetInfo.Errors }
     *     
     */
    public WaitSetInfo.Errors getErrors() {
        return errors;
    }

    /**
     * Sets the value of the errors property.
     * 
     * @param value
     *     allowed object is
     *     {@link WaitSetInfo.Errors }
     *     
     */
    public void setErrors(WaitSetInfo.Errors value) {
        this.errors = value;
    }

    /**
     * Gets the value of the ready property.
     * 
     * @return
     *     possible object is
     *     {@link AccountsAttrib }
     *     
     */
    public AccountsAttrib getReady() {
        return ready;
    }

    /**
     * Sets the value of the ready property.
     * 
     * @param value
     *     allowed object is
     *     {@link AccountsAttrib }
     *     
     */
    public void setReady(AccountsAttrib value) {
        this.ready = value;
    }

    /**
     * Gets the value of the buffered property.
     * 
     * @return
     *     possible object is
     *     {@link WaitSetInfo.Buffered }
     *     
     */
    public WaitSetInfo.Buffered getBuffered() {
        return buffered;
    }

    /**
     * Sets the value of the buffered property.
     * 
     * @param value
     *     allowed object is
     *     {@link WaitSetInfo.Buffered }
     *     
     */
    public void setBuffered(WaitSetInfo.Buffered value) {
        this.buffered = value;
    }

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setId(String value) {
        this.id = value;
    }

    /**
     * Gets the value of the owner property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOwner() {
        return owner;
    }

    /**
     * Sets the value of the owner property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOwner(String value) {
        this.owner = value;
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
     * Gets the value of the ld property.
     * 
     */
    public long getLd() {
        return ld;
    }

    /**
     * Sets the value of the ld property.
     * 
     */
    public void setLd(long value) {
        this.ld = value;
    }

    /**
     * Gets the value of the cbSeqNo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCbSeqNo() {
        return cbSeqNo;
    }

    /**
     * Sets the value of the cbSeqNo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCbSeqNo(String value) {
        this.cbSeqNo = value;
    }

    /**
     * Gets the value of the currentSeqNo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCurrentSeqNo() {
        return currentSeqNo;
    }

    /**
     * Sets the value of the currentSeqNo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCurrentSeqNo(String value) {
        this.currentSeqNo = value;
    }

    /**
     * Gets the value of the nextSeqNo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNextSeqNo() {
        return nextSeqNo;
    }

    /**
     * Sets the value of the nextSeqNo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNextSeqNo(String value) {
        this.nextSeqNo = value;
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
     *         &lt;element name="commit" type="{urn:zimbraAdmin}bufferedCommitInfo" maxOccurs="unbounded" minOccurs="0"/>
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
        "commit"
    })
    public static class Buffered {

        protected List<BufferedCommitInfo> commit;

        /**
         * Gets the value of the commit property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the commit property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getCommit().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link BufferedCommitInfo }
         * 
         * 
         */
        public List<BufferedCommitInfo> getCommit() {
            if (commit == null) {
                commit = new ArrayList<BufferedCommitInfo>();
            }
            return this.commit;
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
     *         &lt;element name="error" type="{urn:zimbraAdmin}idAndType" maxOccurs="unbounded" minOccurs="0"/>
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
        "error"
    })
    public static class Errors {

        protected List<IdAndType> error;

        /**
         * Gets the value of the error property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the error property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getError().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link IdAndType }
         * 
         * 
         */
        public List<IdAndType> getError() {
            if (error == null) {
                error = new ArrayList<IdAndType>();
            }
            return this.error;
        }

    }

}
