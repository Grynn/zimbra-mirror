
package zimbra.generated.accountclient.admin;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.w3c.dom.Element;


/**
 * <p>Java class for getCSRResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getCSRResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;any processContents='skip' namespace='##other' maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="SubjectAltName" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="csr_exists" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="isComm" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="server" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getCSRResponse", propOrder = {
    "any",
    "subjectAltName"
})
public class testGetCSRResponse {

    @XmlAnyElement
    protected List<Element> any;
    @XmlElement(name = "SubjectAltName")
    protected List<String> subjectAltName;
    @XmlAttribute(name = "csr_exists", required = true)
    protected String csrExists;
    @XmlAttribute(name = "isComm", required = true)
    protected String isComm;
    @XmlAttribute(name = "server", required = true)
    protected String server;

    /**
     * Gets the value of the any property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the any property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAny().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Element }
     * 
     * 
     */
    public List<Element> getAny() {
        if (any == null) {
            any = new ArrayList<Element>();
        }
        return this.any;
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
     * Gets the value of the csr_Exists property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCsr_Exists() {
        return csrExists;
    }

    /**
     * Sets the value of the csr_Exists property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCsr_Exists(String value) {
        this.csrExists = value;
    }

    /**
     * Gets the value of the isComm property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIsComm() {
        return isComm;
    }

    /**
     * Sets the value of the isComm property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIsComm(String value) {
        this.isComm = value;
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

}
