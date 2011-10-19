
package generated.zcsclient.admin;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for installCertRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="installCertRequest">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="comm_cert" type="{urn:zimbraAdmin}commCert" minOccurs="0"/>
 *         &lt;element name="validation_days" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="subject" type="{urn:zimbraAdmin}csrSubject" minOccurs="0"/>
 *         &lt;element name="SubjectAltName" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="keysize" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="server" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="type" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "installCertRequest", propOrder = {
    "commCert",
    "validationDays",
    "subject",
    "subjectAltName",
    "keysize"
})
public class testInstallCertRequest {

    @XmlElement(name = "comm_cert")
    protected testCommCert commCert;
    @XmlElement(name = "validation_days")
    protected String validationDays;
    protected testCsrSubject subject;
    @XmlElement(name = "SubjectAltName")
    protected List<String> subjectAltName;
    protected String keysize;
    @XmlAttribute(name = "server", required = true)
    protected String server;
    @XmlAttribute(name = "type", required = true)
    protected String type;

    /**
     * Gets the value of the comm_Cert property.
     * 
     * @return
     *     possible object is
     *     {@link testCommCert }
     *     
     */
    public testCommCert getComm_Cert() {
        return commCert;
    }

    /**
     * Sets the value of the comm_Cert property.
     * 
     * @param value
     *     allowed object is
     *     {@link testCommCert }
     *     
     */
    public void setComm_Cert(testCommCert value) {
        this.commCert = value;
    }

    /**
     * Gets the value of the validation_Days property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getValidation_Days() {
        return validationDays;
    }

    /**
     * Sets the value of the validation_Days property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setValidation_Days(String value) {
        this.validationDays = value;
    }

    /**
     * Gets the value of the subject property.
     * 
     * @return
     *     possible object is
     *     {@link testCsrSubject }
     *     
     */
    public testCsrSubject getSubject() {
        return subject;
    }

    /**
     * Sets the value of the subject property.
     * 
     * @param value
     *     allowed object is
     *     {@link testCsrSubject }
     *     
     */
    public void setSubject(testCsrSubject value) {
        this.subject = value;
    }

    /**
     * Gets the value of the subjectAltName property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the subjectAltName property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSubjectAltName().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getSubjectAltName() {
        if (subjectAltName == null) {
            subjectAltName = new ArrayList<String>();
        }
        return this.subjectAltName;
    }

    /**
     * Gets the value of the keysize property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getKeysize() {
        return keysize;
    }

    /**
     * Sets the value of the keysize property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setKeysize(String value) {
        this.keysize = value;
    }

    /**
     * Gets the value of the server property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getServer() {
        return server;
    }

    /**
     * Sets the value of the server property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setServer(String value) {
        this.server = value;
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

}
