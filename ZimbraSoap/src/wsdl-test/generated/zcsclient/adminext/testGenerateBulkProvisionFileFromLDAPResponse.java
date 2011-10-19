
package generated.zcsclient.adminext;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for generateBulkProvisionFileFromLDAPResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="generateBulkProvisionFileFromLDAPResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="totalCount" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="domainCount" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="skippedAccountCount" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="skippedDomainCount" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="SMTPHost" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="SMTPPort" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="fileToken" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/all>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "generateBulkProvisionFileFromLDAPResponse", propOrder = {

})
public class testGenerateBulkProvisionFileFromLDAPResponse {

    protected Integer totalCount;
    protected Integer domainCount;
    protected Integer skippedAccountCount;
    protected Integer skippedDomainCount;
    @XmlElement(name = "SMTPHost")
    protected String smtpHost;
    @XmlElement(name = "SMTPPort")
    protected String smtpPort;
    protected String fileToken;

    /**
     * Gets the value of the totalCount property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getTotalCount() {
        return totalCount;
    }

    /**
     * Sets the value of the totalCount property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setTotalCount(Integer value) {
        this.totalCount = value;
    }

    /**
     * Gets the value of the domainCount property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getDomainCount() {
        return domainCount;
    }

    /**
     * Sets the value of the domainCount property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setDomainCount(Integer value) {
        this.domainCount = value;
    }

    /**
     * Gets the value of the skippedAccountCount property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getSkippedAccountCount() {
        return skippedAccountCount;
    }

    /**
     * Sets the value of the skippedAccountCount property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setSkippedAccountCount(Integer value) {
        this.skippedAccountCount = value;
    }

    /**
     * Gets the value of the skippedDomainCount property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getSkippedDomainCount() {
        return skippedDomainCount;
    }

    /**
     * Sets the value of the skippedDomainCount property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setSkippedDomainCount(Integer value) {
        this.skippedDomainCount = value;
    }

    /**
     * Gets the value of the smtpHost property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSMTPHost() {
        return smtpHost;
    }

    /**
     * Sets the value of the smtpHost property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSMTPHost(String value) {
        this.smtpHost = value;
    }

    /**
     * Gets the value of the smtpPort property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSMTPPort() {
        return smtpPort;
    }

    /**
     * Sets the value of the smtpPort property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSMTPPort(String value) {
        this.smtpPort = value;
    }

    /**
     * Gets the value of the fileToken property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFileToken() {
        return fileToken;
    }

    /**
     * Sets the value of the fileToken property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFileToken(String value) {
        this.fileToken = value;
    }

}
