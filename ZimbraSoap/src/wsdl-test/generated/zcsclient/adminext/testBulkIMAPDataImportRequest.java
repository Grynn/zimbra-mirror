
package generated.zcsclient.adminext;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for bulkIMAPDataImportRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="bulkIMAPDataImportRequest">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="sourceType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="aid" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="account" type="{urn:zimbraAdminExt}name" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="ConnectionType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="sourceServerType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="IMAPHost" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="IMAPPort" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="indexBatchSize" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="UseAdminLogin" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="IMAPAdminLogin" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="IMAPAdminPassword" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="op" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "bulkIMAPDataImportRequest", propOrder = {
    "sourceType",
    "aid",
    "account",
    "connectionType",
    "sourceServerType",
    "imapHost",
    "imapPort",
    "indexBatchSize",
    "useAdminLogin",
    "imapAdminLogin",
    "imapAdminPassword"
})
public class testBulkIMAPDataImportRequest {

    protected String sourceType;
    protected String aid;
    protected List<testName> account;
    @XmlElement(name = "ConnectionType")
    protected String connectionType;
    protected String sourceServerType;
    @XmlElement(name = "IMAPHost")
    protected String imapHost;
    @XmlElement(name = "IMAPPort")
    protected String imapPort;
    protected String indexBatchSize;
    @XmlElement(name = "UseAdminLogin")
    protected String useAdminLogin;
    @XmlElement(name = "IMAPAdminLogin")
    protected String imapAdminLogin;
    @XmlElement(name = "IMAPAdminPassword")
    protected String imapAdminPassword;
    @XmlAttribute(name = "op")
    protected String op;

    /**
     * Gets the value of the sourceType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSourceType() {
        return sourceType;
    }

    /**
     * Sets the value of the sourceType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSourceType(String value) {
        this.sourceType = value;
    }

    /**
     * Gets the value of the aid property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAid() {
        return aid;
    }

    /**
     * Sets the value of the aid property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAid(String value) {
        this.aid = value;
    }

    /**
     * Gets the value of the account property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the account property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAccount().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link testName }
     * 
     * 
     */
    public List<testName> getAccount() {
        if (account == null) {
            account = new ArrayList<testName>();
        }
        return this.account;
    }

    /**
     * Gets the value of the connectionType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getConnectionType() {
        return connectionType;
    }

    /**
     * Sets the value of the connectionType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setConnectionType(String value) {
        this.connectionType = value;
    }

    /**
     * Gets the value of the sourceServerType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSourceServerType() {
        return sourceServerType;
    }

    /**
     * Sets the value of the sourceServerType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSourceServerType(String value) {
        this.sourceServerType = value;
    }

    /**
     * Gets the value of the imapHost property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIMAPHost() {
        return imapHost;
    }

    /**
     * Sets the value of the imapHost property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIMAPHost(String value) {
        this.imapHost = value;
    }

    /**
     * Gets the value of the imapPort property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIMAPPort() {
        return imapPort;
    }

    /**
     * Sets the value of the imapPort property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIMAPPort(String value) {
        this.imapPort = value;
    }

    /**
     * Gets the value of the indexBatchSize property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIndexBatchSize() {
        return indexBatchSize;
    }

    /**
     * Sets the value of the indexBatchSize property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIndexBatchSize(String value) {
        this.indexBatchSize = value;
    }

    /**
     * Gets the value of the useAdminLogin property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUseAdminLogin() {
        return useAdminLogin;
    }

    /**
     * Sets the value of the useAdminLogin property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUseAdminLogin(String value) {
        this.useAdminLogin = value;
    }

    /**
     * Gets the value of the imapAdminLogin property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIMAPAdminLogin() {
        return imapAdminLogin;
    }

    /**
     * Sets the value of the imapAdminLogin property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIMAPAdminLogin(String value) {
        this.imapAdminLogin = value;
    }

    /**
     * Gets the value of the imapAdminPassword property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIMAPAdminPassword() {
        return imapAdminPassword;
    }

    /**
     * Sets the value of the imapAdminPassword property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIMAPAdminPassword(String value) {
        this.imapAdminPassword = value;
    }

    /**
     * Gets the value of the op property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOp() {
        return op;
    }

    /**
     * Sets the value of the op property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOp(String value) {
        this.op = value;
    }

}
