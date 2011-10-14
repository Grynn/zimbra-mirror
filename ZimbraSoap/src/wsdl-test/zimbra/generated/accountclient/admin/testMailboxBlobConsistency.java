
package zimbra.generated.accountclient.admin;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for mailboxBlobConsistency complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="mailboxBlobConsistency">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="missingBlobs">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="item" type="{urn:zimbraAdmin}missingBlobInfo" maxOccurs="unbounded" minOccurs="0"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="incorrectSize">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="item" type="{urn:zimbraAdmin}incorrectBlobSizeInfo" maxOccurs="unbounded" minOccurs="0"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="unexpectedBlobs">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="blob" type="{urn:zimbraAdmin}unexpectedBlobInfo" maxOccurs="unbounded" minOccurs="0"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="incorrectRevision">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="item" type="{urn:zimbraAdmin}incorrectBlobRevisionInfo" maxOccurs="unbounded" minOccurs="0"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/all>
 *       &lt;attribute name="id" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "mailboxBlobConsistency", propOrder = {

})
public class testMailboxBlobConsistency {

    @XmlElement(required = true)
    protected testMailboxBlobConsistency.MissingBlobs missingBlobs;
    @XmlElement(required = true)
    protected testMailboxBlobConsistency.IncorrectSize incorrectSize;
    @XmlElement(required = true)
    protected testMailboxBlobConsistency.UnexpectedBlobs unexpectedBlobs;
    @XmlElement(required = true)
    protected testMailboxBlobConsistency.IncorrectRevision incorrectRevision;
    @XmlAttribute(name = "id", required = true)
    protected int id;

    /**
     * Gets the value of the missingBlobs property.
     * 
     * @return
     *     possible object is
     *     {@link testMailboxBlobConsistency.MissingBlobs }
     *     
     */
    public testMailboxBlobConsistency.MissingBlobs getMissingBlobs() {
        return missingBlobs;
    }

    /**
     * Sets the value of the missingBlobs property.
     * 
     * @param value
     *     allowed object is
     *     {@link testMailboxBlobConsistency.MissingBlobs }
     *     
     */
    public void setMissingBlobs(testMailboxBlobConsistency.MissingBlobs value) {
        this.missingBlobs = value;
    }

    /**
     * Gets the value of the incorrectSize property.
     * 
     * @return
     *     possible object is
     *     {@link testMailboxBlobConsistency.IncorrectSize }
     *     
     */
    public testMailboxBlobConsistency.IncorrectSize getIncorrectSize() {
        return incorrectSize;
    }

    /**
     * Sets the value of the incorrectSize property.
     * 
     * @param value
     *     allowed object is
     *     {@link testMailboxBlobConsistency.IncorrectSize }
     *     
     */
    public void setIncorrectSize(testMailboxBlobConsistency.IncorrectSize value) {
        this.incorrectSize = value;
    }

    /**
     * Gets the value of the unexpectedBlobs property.
     * 
     * @return
     *     possible object is
     *     {@link testMailboxBlobConsistency.UnexpectedBlobs }
     *     
     */
    public testMailboxBlobConsistency.UnexpectedBlobs getUnexpectedBlobs() {
        return unexpectedBlobs;
    }

    /**
     * Sets the value of the unexpectedBlobs property.
     * 
     * @param value
     *     allowed object is
     *     {@link testMailboxBlobConsistency.UnexpectedBlobs }
     *     
     */
    public void setUnexpectedBlobs(testMailboxBlobConsistency.UnexpectedBlobs value) {
        this.unexpectedBlobs = value;
    }

    /**
     * Gets the value of the incorrectRevision property.
     * 
     * @return
     *     possible object is
     *     {@link testMailboxBlobConsistency.IncorrectRevision }
     *     
     */
    public testMailboxBlobConsistency.IncorrectRevision getIncorrectRevision() {
        return incorrectRevision;
    }

    /**
     * Sets the value of the incorrectRevision property.
     * 
     * @param value
     *     allowed object is
     *     {@link testMailboxBlobConsistency.IncorrectRevision }
     *     
     */
    public void setIncorrectRevision(testMailboxBlobConsistency.IncorrectRevision value) {
        this.incorrectRevision = value;
    }

    /**
     * Gets the value of the id property.
     * 
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     */
    public void setId(int value) {
        this.id = value;
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
     *         &lt;element name="item" type="{urn:zimbraAdmin}incorrectBlobRevisionInfo" maxOccurs="unbounded" minOccurs="0"/>
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
        "item"
    })
    public static class IncorrectRevision {

        protected List<testIncorrectBlobRevisionInfo> item;

        /**
         * Gets the value of the item property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the item property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getItem().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link testIncorrectBlobRevisionInfo }
         * 
         * 
         */
        public List<testIncorrectBlobRevisionInfo> getItem() {
            if (item == null) {
                item = new ArrayList<testIncorrectBlobRevisionInfo>();
            }
            return this.item;
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
     *         &lt;element name="item" type="{urn:zimbraAdmin}incorrectBlobSizeInfo" maxOccurs="unbounded" minOccurs="0"/>
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
        "item"
    })
    public static class IncorrectSize {

        protected List<testIncorrectBlobSizeInfo> item;

        /**
         * Gets the value of the item property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the item property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getItem().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link testIncorrectBlobSizeInfo }
         * 
         * 
         */
        public List<testIncorrectBlobSizeInfo> getItem() {
            if (item == null) {
                item = new ArrayList<testIncorrectBlobSizeInfo>();
            }
            return this.item;
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
     *         &lt;element name="item" type="{urn:zimbraAdmin}missingBlobInfo" maxOccurs="unbounded" minOccurs="0"/>
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
        "item"
    })
    public static class MissingBlobs {

        protected List<testMissingBlobInfo> item;

        /**
         * Gets the value of the item property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the item property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getItem().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link testMissingBlobInfo }
         * 
         * 
         */
        public List<testMissingBlobInfo> getItem() {
            if (item == null) {
                item = new ArrayList<testMissingBlobInfo>();
            }
            return this.item;
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
     *         &lt;element name="blob" type="{urn:zimbraAdmin}unexpectedBlobInfo" maxOccurs="unbounded" minOccurs="0"/>
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
        "blob"
    })
    public static class UnexpectedBlobs {

        protected List<testUnexpectedBlobInfo> blob;

        /**
         * Gets the value of the blob property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the blob property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getBlob().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link testUnexpectedBlobInfo }
         * 
         * 
         */
        public List<testUnexpectedBlobInfo> getBlob() {
            if (blob == null) {
                blob = new ArrayList<testUnexpectedBlobInfo>();
            }
            return this.blob;
        }

    }

}
