
package com.zimbra.soap.admin.wsimport.generated;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for backupQueryInfo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="backupQueryInfo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="currentAccounts" type="{urn:zimbraAdmin}currentAccounts" minOccurs="0"/>
 *         &lt;element name="accounts" type="{urn:zimbraAdmin}backupQueryAccounts" minOccurs="0"/>
 *         &lt;element name="error" type="{urn:zimbraAdmin}backupQueryError" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="stats" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="counter" type="{urn:zimbraAdmin}backupQueryCounter" maxOccurs="unbounded" minOccurs="0"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *       &lt;attribute name="label" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="type" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="aborted" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="start" type="{http://www.w3.org/2001/XMLSchema}long" />
 *       &lt;attribute name="end" type="{http://www.w3.org/2001/XMLSchema}long" />
 *       &lt;attribute name="minRedoSeq" type="{http://www.w3.org/2001/XMLSchema}long" />
 *       &lt;attribute name="maxRedoSeq" type="{http://www.w3.org/2001/XMLSchema}long" />
 *       &lt;attribute name="live" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "backupQueryInfo", propOrder = {
    "currentAccounts",
    "accounts",
    "error",
    "stats"
})
public class BackupQueryInfo {

    protected CurrentAccounts currentAccounts;
    protected BackupQueryAccounts accounts;
    protected List<BackupQueryError> error;
    protected BackupQueryInfo.Stats stats;
    @XmlAttribute
    protected String label;
    @XmlAttribute
    protected String type;
    @XmlAttribute
    protected Boolean aborted;
    @XmlAttribute
    protected Long start;
    @XmlAttribute
    protected Long end;
    @XmlAttribute
    protected Long minRedoSeq;
    @XmlAttribute
    protected Long maxRedoSeq;
    @XmlAttribute
    protected Boolean live;

    /**
     * Gets the value of the currentAccounts property.
     * 
     * @return
     *     possible object is
     *     {@link CurrentAccounts }
     *     
     */
    public CurrentAccounts getCurrentAccounts() {
        return currentAccounts;
    }

    /**
     * Sets the value of the currentAccounts property.
     * 
     * @param value
     *     allowed object is
     *     {@link CurrentAccounts }
     *     
     */
    public void setCurrentAccounts(CurrentAccounts value) {
        this.currentAccounts = value;
    }

    /**
     * Gets the value of the accounts property.
     * 
     * @return
     *     possible object is
     *     {@link BackupQueryAccounts }
     *     
     */
    public BackupQueryAccounts getAccounts() {
        return accounts;
    }

    /**
     * Sets the value of the accounts property.
     * 
     * @param value
     *     allowed object is
     *     {@link BackupQueryAccounts }
     *     
     */
    public void setAccounts(BackupQueryAccounts value) {
        this.accounts = value;
    }

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
     * {@link BackupQueryError }
     * 
     * 
     */
    public List<BackupQueryError> getError() {
        if (error == null) {
            error = new ArrayList<BackupQueryError>();
        }
        return this.error;
    }

    /**
     * Gets the value of the stats property.
     * 
     * @return
     *     possible object is
     *     {@link BackupQueryInfo.Stats }
     *     
     */
    public BackupQueryInfo.Stats getStats() {
        return stats;
    }

    /**
     * Sets the value of the stats property.
     * 
     * @param value
     *     allowed object is
     *     {@link BackupQueryInfo.Stats }
     *     
     */
    public void setStats(BackupQueryInfo.Stats value) {
        this.stats = value;
    }

    /**
     * Gets the value of the label property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLabel() {
        return label;
    }

    /**
     * Sets the value of the label property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLabel(String value) {
        this.label = value;
    }

    /**
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setType(String value) {
        this.type = value;
    }

    /**
     * Gets the value of the aborted property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isAborted() {
        return aborted;
    }

    /**
     * Sets the value of the aborted property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setAborted(Boolean value) {
        this.aborted = value;
    }

    /**
     * Gets the value of the start property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getStart() {
        return start;
    }

    /**
     * Sets the value of the start property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setStart(Long value) {
        this.start = value;
    }

    /**
     * Gets the value of the end property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getEnd() {
        return end;
    }

    /**
     * Sets the value of the end property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setEnd(Long value) {
        this.end = value;
    }

    /**
     * Gets the value of the minRedoSeq property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getMinRedoSeq() {
        return minRedoSeq;
    }

    /**
     * Sets the value of the minRedoSeq property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setMinRedoSeq(Long value) {
        this.minRedoSeq = value;
    }

    /**
     * Gets the value of the maxRedoSeq property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getMaxRedoSeq() {
        return maxRedoSeq;
    }

    /**
     * Sets the value of the maxRedoSeq property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setMaxRedoSeq(Long value) {
        this.maxRedoSeq = value;
    }

    /**
     * Gets the value of the live property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isLive() {
        return live;
    }

    /**
     * Sets the value of the live property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setLive(Boolean value) {
        this.live = value;
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
     *         &lt;element name="counter" type="{urn:zimbraAdmin}backupQueryCounter" maxOccurs="unbounded" minOccurs="0"/>
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
        "counter"
    })
    public static class Stats {

        protected List<BackupQueryCounter> counter;

        /**
         * Gets the value of the counter property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the counter property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getCounter().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link BackupQueryCounter }
         * 
         * 
         */
        public List<BackupQueryCounter> getCounter() {
            if (counter == null) {
                counter = new ArrayList<BackupQueryCounter>();
            }
            return this.counter;
        }

    }

}
