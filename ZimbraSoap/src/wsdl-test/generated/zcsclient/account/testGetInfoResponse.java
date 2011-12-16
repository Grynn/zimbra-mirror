
package generated.zcsclient.account;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for getInfoResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getInfoResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="version" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="id" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="crumb" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="lifetime" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="adminDelegated" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="rest" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="used" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="prevSession" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="accessed" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="recent" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="cos" type="{urn:zimbraAccount}cos" minOccurs="0"/>
 *         &lt;element name="prefs" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="pref" type="{urn:zimbraAccount}pref" maxOccurs="unbounded" minOccurs="0"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="attrs" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="attr" type="{urn:zimbraAccount}attr" maxOccurs="unbounded" minOccurs="0"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="zimlets" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="zimlet" type="{urn:zimbraAccount}accountZimletInfo" maxOccurs="unbounded" minOccurs="0"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="props" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="prop" type="{urn:zimbraAccount}prop" maxOccurs="unbounded" minOccurs="0"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="identities" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="identity" type="{urn:zimbraAccount}identity" maxOccurs="unbounded" minOccurs="0"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="signatures" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element ref="{urn:zimbraAccount}signature" maxOccurs="unbounded" minOccurs="0"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="dataSources" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;choice maxOccurs="unbounded" minOccurs="0">
 *                     &lt;element name="imap" type="{urn:zimbraAccount}accountImapDataSource"/>
 *                     &lt;element name="pop3" type="{urn:zimbraAccount}accountPop3DataSource"/>
 *                     &lt;element name="caldav" type="{urn:zimbraAccount}accountCaldavDataSource"/>
 *                     &lt;element name="yab" type="{urn:zimbraAccount}accountYabDataSource"/>
 *                     &lt;element name="rss" type="{urn:zimbraAccount}accountRssDataSource"/>
 *                     &lt;element name="gal" type="{urn:zimbraAccount}accountGalDataSource"/>
 *                     &lt;element name="cal" type="{urn:zimbraAccount}accountCalDataSource"/>
 *                     &lt;element name="unknown" type="{urn:zimbraAccount}accountUnknownDataSource"/>
 *                   &lt;/choice>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="childAccounts" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="childAccount" type="{urn:zimbraAccount}childAccount" maxOccurs="unbounded" minOccurs="0"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="soapURL" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="publicURL" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="changePasswordURL" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="license" type="{urn:zimbraAccount}licenseInfo" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="attSizeLimit" type="{http://www.w3.org/2001/XMLSchema}long" />
 *       &lt;attribute name="docSizeLimit" type="{http://www.w3.org/2001/XMLSchema}long" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getInfoResponse", propOrder = {
    "version",
    "id",
    "name",
    "crumb",
    "lifetime",
    "adminDelegated",
    "rest",
    "used",
    "prevSession",
    "accessed",
    "recent",
    "cos",
    "prefs",
    "attrs",
    "zimlets",
    "props",
    "identities",
    "signatures",
    "dataSources",
    "childAccounts",
    "soapURL",
    "publicURL",
    "changePasswordURL",
    "license"
})
public class testGetInfoResponse {

    @XmlElement(required = true)
    protected String version;
    @XmlElement(required = true)
    protected String id;
    @XmlElement(required = true)
    protected String name;
    protected String crumb;
    protected long lifetime;
    protected Boolean adminDelegated;
    protected String rest;
    protected Long used;
    protected Long prevSession;
    protected Long accessed;
    protected Integer recent;
    protected testCos cos;
    protected testGetInfoResponse.Prefs prefs;
    protected testGetInfoResponse.Attrs attrs;
    protected testGetInfoResponse.Zimlets zimlets;
    protected testGetInfoResponse.Props props;
    protected testGetInfoResponse.Identities identities;
    protected testGetInfoResponse.Signatures signatures;
    protected testGetInfoResponse.DataSources dataSources;
    protected testGetInfoResponse.ChildAccounts childAccounts;
    protected List<String> soapURL;
    protected String publicURL;
    protected String changePasswordURL;
    protected testLicenseInfo license;
    @XmlAttribute(name = "attSizeLimit")
    protected Long attSizeLimit;
    @XmlAttribute(name = "docSizeLimit")
    protected Long docSizeLimit;

    /**
     * Gets the value of the version property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVersion() {
        return version;
    }

    /**
     * Sets the value of the version property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVersion(String value) {
        this.version = value;
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
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the crumb property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCrumb() {
        return crumb;
    }

    /**
     * Sets the value of the crumb property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCrumb(String value) {
        this.crumb = value;
    }

    /**
     * Gets the value of the lifetime property.
     * 
     */
    public long getLifetime() {
        return lifetime;
    }

    /**
     * Sets the value of the lifetime property.
     * 
     */
    public void setLifetime(long value) {
        this.lifetime = value;
    }

    /**
     * Gets the value of the adminDelegated property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isAdminDelegated() {
        return adminDelegated;
    }

    /**
     * Sets the value of the adminDelegated property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setAdminDelegated(Boolean value) {
        this.adminDelegated = value;
    }

    /**
     * Gets the value of the rest property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRest() {
        return rest;
    }

    /**
     * Sets the value of the rest property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRest(String value) {
        this.rest = value;
    }

    /**
     * Gets the value of the used property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getUsed() {
        return used;
    }

    /**
     * Sets the value of the used property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setUsed(Long value) {
        this.used = value;
    }

    /**
     * Gets the value of the prevSession property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getPrevSession() {
        return prevSession;
    }

    /**
     * Sets the value of the prevSession property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setPrevSession(Long value) {
        this.prevSession = value;
    }

    /**
     * Gets the value of the accessed property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getAccessed() {
        return accessed;
    }

    /**
     * Sets the value of the accessed property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setAccessed(Long value) {
        this.accessed = value;
    }

    /**
     * Gets the value of the recent property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getRecent() {
        return recent;
    }

    /**
     * Sets the value of the recent property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setRecent(Integer value) {
        this.recent = value;
    }

    /**
     * Gets the value of the cos property.
     * 
     * @return
     *     possible object is
     *     {@link testCos }
     *     
     */
    public testCos getCos() {
        return cos;
    }

    /**
     * Sets the value of the cos property.
     * 
     * @param value
     *     allowed object is
     *     {@link testCos }
     *     
     */
    public void setCos(testCos value) {
        this.cos = value;
    }

    /**
     * Gets the value of the prefs property.
     * 
     * @return
     *     possible object is
     *     {@link testGetInfoResponse.Prefs }
     *     
     */
    public testGetInfoResponse.Prefs getPrefs() {
        return prefs;
    }

    /**
     * Sets the value of the prefs property.
     * 
     * @param value
     *     allowed object is
     *     {@link testGetInfoResponse.Prefs }
     *     
     */
    public void setPrefs(testGetInfoResponse.Prefs value) {
        this.prefs = value;
    }

    /**
     * Gets the value of the attrs property.
     * 
     * @return
     *     possible object is
     *     {@link testGetInfoResponse.Attrs }
     *     
     */
    public testGetInfoResponse.Attrs getAttrs() {
        return attrs;
    }

    /**
     * Sets the value of the attrs property.
     * 
     * @param value
     *     allowed object is
     *     {@link testGetInfoResponse.Attrs }
     *     
     */
    public void setAttrs(testGetInfoResponse.Attrs value) {
        this.attrs = value;
    }

    /**
     * Gets the value of the zimlets property.
     * 
     * @return
     *     possible object is
     *     {@link testGetInfoResponse.Zimlets }
     *     
     */
    public testGetInfoResponse.Zimlets getZimlets() {
        return zimlets;
    }

    /**
     * Sets the value of the zimlets property.
     * 
     * @param value
     *     allowed object is
     *     {@link testGetInfoResponse.Zimlets }
     *     
     */
    public void setZimlets(testGetInfoResponse.Zimlets value) {
        this.zimlets = value;
    }

    /**
     * Gets the value of the props property.
     * 
     * @return
     *     possible object is
     *     {@link testGetInfoResponse.Props }
     *     
     */
    public testGetInfoResponse.Props getProps() {
        return props;
    }

    /**
     * Sets the value of the props property.
     * 
     * @param value
     *     allowed object is
     *     {@link testGetInfoResponse.Props }
     *     
     */
    public void setProps(testGetInfoResponse.Props value) {
        this.props = value;
    }

    /**
     * Gets the value of the identities property.
     * 
     * @return
     *     possible object is
     *     {@link testGetInfoResponse.Identities }
     *     
     */
    public testGetInfoResponse.Identities getIdentities() {
        return identities;
    }

    /**
     * Sets the value of the identities property.
     * 
     * @param value
     *     allowed object is
     *     {@link testGetInfoResponse.Identities }
     *     
     */
    public void setIdentities(testGetInfoResponse.Identities value) {
        this.identities = value;
    }

    /**
     * Gets the value of the signatures property.
     * 
     * @return
     *     possible object is
     *     {@link testGetInfoResponse.Signatures }
     *     
     */
    public testGetInfoResponse.Signatures getSignatures() {
        return signatures;
    }

    /**
     * Sets the value of the signatures property.
     * 
     * @param value
     *     allowed object is
     *     {@link testGetInfoResponse.Signatures }
     *     
     */
    public void setSignatures(testGetInfoResponse.Signatures value) {
        this.signatures = value;
    }

    /**
     * Gets the value of the dataSources property.
     * 
     * @return
     *     possible object is
     *     {@link testGetInfoResponse.DataSources }
     *     
     */
    public testGetInfoResponse.DataSources getDataSources() {
        return dataSources;
    }

    /**
     * Sets the value of the dataSources property.
     * 
     * @param value
     *     allowed object is
     *     {@link testGetInfoResponse.DataSources }
     *     
     */
    public void setDataSources(testGetInfoResponse.DataSources value) {
        this.dataSources = value;
    }

    /**
     * Gets the value of the childAccounts property.
     * 
     * @return
     *     possible object is
     *     {@link testGetInfoResponse.ChildAccounts }
     *     
     */
    public testGetInfoResponse.ChildAccounts getChildAccounts() {
        return childAccounts;
    }

    /**
     * Sets the value of the childAccounts property.
     * 
     * @param value
     *     allowed object is
     *     {@link testGetInfoResponse.ChildAccounts }
     *     
     */
    public void setChildAccounts(testGetInfoResponse.ChildAccounts value) {
        this.childAccounts = value;
    }

    /**
     * Gets the value of the soapURL property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the soapURL property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSoapURL().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getSoapURL() {
        if (soapURL == null) {
            soapURL = new ArrayList<String>();
        }
        return this.soapURL;
    }

    /**
     * Gets the value of the publicURL property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPublicURL() {
        return publicURL;
    }

    /**
     * Sets the value of the publicURL property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPublicURL(String value) {
        this.publicURL = value;
    }

    /**
     * Gets the value of the changePasswordURL property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getChangePasswordURL() {
        return changePasswordURL;
    }

    /**
     * Sets the value of the changePasswordURL property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setChangePasswordURL(String value) {
        this.changePasswordURL = value;
    }

    /**
     * Gets the value of the license property.
     * 
     * @return
     *     possible object is
     *     {@link testLicenseInfo }
     *     
     */
    public testLicenseInfo getLicense() {
        return license;
    }

    /**
     * Sets the value of the license property.
     * 
     * @param value
     *     allowed object is
     *     {@link testLicenseInfo }
     *     
     */
    public void setLicense(testLicenseInfo value) {
        this.license = value;
    }

    /**
     * Gets the value of the attSizeLimit property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getAttSizeLimit() {
        return attSizeLimit;
    }

    /**
     * Sets the value of the attSizeLimit property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setAttSizeLimit(Long value) {
        this.attSizeLimit = value;
    }

    /**
     * Gets the value of the docSizeLimit property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getDocSizeLimit() {
        return docSizeLimit;
    }

    /**
     * Sets the value of the docSizeLimit property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setDocSizeLimit(Long value) {
        this.docSizeLimit = value;
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
     *         &lt;element name="attr" type="{urn:zimbraAccount}attr" maxOccurs="unbounded" minOccurs="0"/>
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
        "attr"
    })
    public static class Attrs {

        protected List<testAttr> attr;

        /**
         * Gets the value of the attr property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the attr property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getAttr().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link testAttr }
         * 
         * 
         */
        public List<testAttr> getAttr() {
            if (attr == null) {
                attr = new ArrayList<testAttr>();
            }
            return this.attr;
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
     *         &lt;element name="childAccount" type="{urn:zimbraAccount}childAccount" maxOccurs="unbounded" minOccurs="0"/>
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
        "childAccount"
    })
    public static class ChildAccounts {

        protected List<testChildAccount> childAccount;

        /**
         * Gets the value of the childAccount property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the childAccount property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getChildAccount().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link testChildAccount }
         * 
         * 
         */
        public List<testChildAccount> getChildAccount() {
            if (childAccount == null) {
                childAccount = new ArrayList<testChildAccount>();
            }
            return this.childAccount;
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
     *         &lt;choice maxOccurs="unbounded" minOccurs="0">
     *           &lt;element name="imap" type="{urn:zimbraAccount}accountImapDataSource"/>
     *           &lt;element name="pop3" type="{urn:zimbraAccount}accountPop3DataSource"/>
     *           &lt;element name="caldav" type="{urn:zimbraAccount}accountCaldavDataSource"/>
     *           &lt;element name="yab" type="{urn:zimbraAccount}accountYabDataSource"/>
     *           &lt;element name="rss" type="{urn:zimbraAccount}accountRssDataSource"/>
     *           &lt;element name="gal" type="{urn:zimbraAccount}accountGalDataSource"/>
     *           &lt;element name="cal" type="{urn:zimbraAccount}accountCalDataSource"/>
     *           &lt;element name="unknown" type="{urn:zimbraAccount}accountUnknownDataSource"/>
     *         &lt;/choice>
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
        "imapOrPop3OrCaldav"
    })
    public static class DataSources {

        @XmlElements({
            @XmlElement(name = "pop3", type = testAccountPop3DataSource.class),
            @XmlElement(name = "cal", type = testAccountCalDataSource.class),
            @XmlElement(name = "unknown", type = testAccountUnknownDataSource.class),
            @XmlElement(name = "caldav", type = testAccountCaldavDataSource.class),
            @XmlElement(name = "gal", type = testAccountGalDataSource.class),
            @XmlElement(name = "rss", type = testAccountRssDataSource.class),
            @XmlElement(name = "yab", type = testAccountYabDataSource.class),
            @XmlElement(name = "imap", type = testAccountImapDataSource.class)
        })
        protected List<testAccountDataSource> imapOrPop3OrCaldav;

        /**
         * Gets the value of the imapOrPop3OrCaldav property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the imapOrPop3OrCaldav property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getImapOrPop3OrCaldav().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link testAccountPop3DataSource }
         * {@link testAccountCalDataSource }
         * {@link testAccountUnknownDataSource }
         * {@link testAccountCaldavDataSource }
         * {@link testAccountGalDataSource }
         * {@link testAccountRssDataSource }
         * {@link testAccountYabDataSource }
         * {@link testAccountImapDataSource }
         * 
         * 
         */
        public List<testAccountDataSource> getImapOrPop3OrCaldav() {
            if (imapOrPop3OrCaldav == null) {
                imapOrPop3OrCaldav = new ArrayList<testAccountDataSource>();
            }
            return this.imapOrPop3OrCaldav;
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
     *         &lt;element name="identity" type="{urn:zimbraAccount}identity" maxOccurs="unbounded" minOccurs="0"/>
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
        "identity"
    })
    public static class Identities {

        protected List<testIdentity> identity;

        /**
         * Gets the value of the identity property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the identity property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getIdentity().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link testIdentity }
         * 
         * 
         */
        public List<testIdentity> getIdentity() {
            if (identity == null) {
                identity = new ArrayList<testIdentity>();
            }
            return this.identity;
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
     *         &lt;element name="pref" type="{urn:zimbraAccount}pref" maxOccurs="unbounded" minOccurs="0"/>
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
        "pref"
    })
    public static class Prefs {

        protected List<testPref> pref;

        /**
         * Gets the value of the pref property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the pref property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getPref().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link testPref }
         * 
         * 
         */
        public List<testPref> getPref() {
            if (pref == null) {
                pref = new ArrayList<testPref>();
            }
            return this.pref;
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
     *         &lt;element name="prop" type="{urn:zimbraAccount}prop" maxOccurs="unbounded" minOccurs="0"/>
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
        "prop"
    })
    public static class Props {

        protected List<testProp> prop;

        /**
         * Gets the value of the prop property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the prop property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getProp().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link testProp }
         * 
         * 
         */
        public List<testProp> getProp() {
            if (prop == null) {
                prop = new ArrayList<testProp>();
            }
            return this.prop;
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
     *         &lt;element ref="{urn:zimbraAccount}signature" maxOccurs="unbounded" minOccurs="0"/>
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
        "signature"
    })
    public static class Signatures {

        protected List<testSignature> signature;

        /**
         * Gets the value of the signature property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the signature property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getSignature().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link testSignature }
         * 
         * 
         */
        public List<testSignature> getSignature() {
            if (signature == null) {
                signature = new ArrayList<testSignature>();
            }
            return this.signature;
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
     *         &lt;element name="zimlet" type="{urn:zimbraAccount}accountZimletInfo" maxOccurs="unbounded" minOccurs="0"/>
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
        "zimlet"
    })
    public static class Zimlets {

        protected List<testAccountZimletInfo> zimlet;

        /**
         * Gets the value of the zimlet property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the zimlet property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getZimlet().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link testAccountZimletInfo }
         * 
         * 
         */
        public List<testAccountZimletInfo> getZimlet() {
            if (zimlet == null) {
                zimlet = new ArrayList<testAccountZimletInfo>();
            }
            return this.zimlet;
        }

    }

}
