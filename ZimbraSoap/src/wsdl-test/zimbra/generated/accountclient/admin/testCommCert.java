
package zimbra.generated.accountclient.admin;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for commCert complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="commCert">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="cert" type="{urn:zimbraAdmin}aidAndFilename" minOccurs="0"/>
 *         &lt;element name="rootCA" type="{urn:zimbraAdmin}aidAndFilename" minOccurs="0"/>
 *         &lt;element name="intermediateCA" type="{urn:zimbraAdmin}aidAndFilename" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "commCert", propOrder = {
    "cert",
    "rootCA",
    "intermediateCA"
})
public class testCommCert {

    protected testAidAndFilename cert;
    protected testAidAndFilename rootCA;
    protected List<testAidAndFilename> intermediateCA;

    /**
     * Gets the value of the cert property.
     * 
     * @return
     *     possible object is
     *     {@link testAidAndFilename }
     *     
     */
    public testAidAndFilename getCert() {
        return cert;
    }

    /**
     * Sets the value of the cert property.
     * 
     * @param value
     *     allowed object is
     *     {@link testAidAndFilename }
     *     
     */
    public void setCert(testAidAndFilename value) {
        this.cert = value;
    }

    /**
     * Gets the value of the rootCA property.
     * 
     * @return
     *     possible object is
     *     {@link testAidAndFilename }
     *     
     */
    public testAidAndFilename getRootCA() {
        return rootCA;
    }

    /**
     * Sets the value of the rootCA property.
     * 
     * @param value
     *     allowed object is
     *     {@link testAidAndFilename }
     *     
     */
    public void setRootCA(testAidAndFilename value) {
        this.rootCA = value;
    }

    /**
     * Gets the value of the intermediateCA property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the intermediateCA property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getIntermediateCA().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link testAidAndFilename }
     * 
     * 
     */
    public List<testAidAndFilename> getIntermediateCA() {
        if (intermediateCA == null) {
            intermediateCA = new ArrayList<testAidAndFilename>();
        }
        return this.intermediateCA;
    }

}
