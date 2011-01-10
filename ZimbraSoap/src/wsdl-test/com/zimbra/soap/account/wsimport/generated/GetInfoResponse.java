
package com.zimbra.soap.account.wsimport.generated;

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
 *       &lt;all>
 *         &lt;element name="version" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="id" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="crumb" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="lifetime" type="{http://www.w3.org/2001/XMLSchema}long"/>
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
 *         &lt;element name="soapURL" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="publicURL" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
 *                     &lt;element name="pop3" type="{urn:zimbraAccount}accountPop3DataSource"/>
 *                     &lt;element name="imap" type="{urn:zimbraAccount}accountImapDataSource"/>
 *                     &lt;element name="rss" type="{urn:zimbraAccount}accountRssDataSource"/>
 *                     &lt;element name="cal" type="{urn:zimbraAccount}accountCalDataSource"/>
 *                   &lt;/choice>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="changePasswordURL" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
 *       &lt;/all>
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

})
public class GetInfoResponse {

    @XmlElement(required = true)
    protected String version;
    @XmlElement(required = true)
    protected String id;
    @XmlElement(required = true)
    protected String name;
    protected String crumb;
    protected long lifetime;
    protected String rest;
    protected Long used;
    protected Long prevSession;
    protected Long accessed;
    protected Integer recent;
    protected Cos cos;
    protected GetInfoResponse.Prefs prefs;
    protected GetInfoResponse.Attrs attrs;
    protected List<String> soapURL;
    protected String publicURL;
    protected GetInfoResponse.Identities identities;
    protected GetInfoResponse.Signatures signatures;
    protected GetInfoResponse.DataSources dataSources;
    protected String changePasswordURL;
    protected GetInfoResponse.Props props;
    @XmlAttribute
    protected Long attSizeLimit;
    @XmlAttribute
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
     *     {@link Cos }
     *     
     */
    public Cos getCos() {
        return cos;
    }

    /**
     * Sets the value of the cos property.
     * 
     * @param value
     *     allowed object is
     *     {@link Cos }
     *     
     */
    public void setCos(Cos value) {
        this.cos = value;
    }

    /**
     * Gets the value of the prefs property.
     * 
     * @return
     *     possible object is
     *     {@link GetInfoResponse.Prefs }
     *     
     */
    public GetInfoResponse.Prefs getPrefs() {
        return prefs;
    }

    /**
     * Sets the value of the prefs property.
     * 
     * @param value
     *     allowed object is
     *     {@link GetInfoResponse.Prefs }
     *     
     */
    public void setPrefs(GetInfoResponse.Prefs value) {
        this.prefs = value;
    }

    /**
     * Gets the value of the attrs property.
     * 
     * @return
     *     possible object is
     *     {@link GetInfoResponse.Attrs }
     *     
     */
    public GetInfoResponse.Attrs getAttrs() {
        return attrs;
    }

    /**
     * Sets the value of the attrs property.
     * 
     * @param value
     *     allowed object is
     *     {@link GetInfoResponse.Attrs }
     *     
     */
    public void setAttrs(GetInfoResponse.Attrs value) {
        this.attrs = value;
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
     * Gets the value of the identities property.
     * 
     * @return
     *     possible object is
     *     {@link GetInfoResponse.Identities }
     *     
     */
    public GetInfoResponse.Identities getIdentities() {
        return identities;
    }

    /**
     * Sets the value of the identities property.
     * 
     * @param value
     *     allowed object is
     *     {@link GetInfoResponse.Identities }
     *     
     */
    public void setIdentities(GetInfoResponse.Identities value) {
        this.identities = value;
    }

    /**
     * Gets the value of the signatures property.
     * 
     * @return
     *     possible object is
     *     {@link GetInfoResponse.Signatures }
     *     
     */
    public GetInfoResponse.Signatures getSignatures() {
        return signatures;
    }

    /**
     * Sets the value of the signatures property.
     * 
     * @param value
     *     allowed object is
     *     {@link GetInfoResponse.Signatures }
     *     
     */
    public void setSignatures(GetInfoResponse.Signatures value) {
        this.signatures = value;
    }

    /**
     * Gets the value of the dataSources property.
     * 
     * @return
     *     possible object is
     *     {@link GetInfoResponse.DataSources }
     *     
     */
    public GetInfoResponse.DataSources getDataSources() {
        return dataSources;
    }

    /**
     * Sets the value of the dataSources property.
     * 
     * @param value
     *     allowed object is
     *     {@link GetInfoResponse.DataSources }
     *     
     */
    public void setDataSources(GetInfoResponse.DataSources value) {
        this.dataSources = value;
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
     * Gets the value of the props property.
     * 
     * @return
     *     possible object is
     *     {@link GetInfoResponse.Props }
     *     
     */
    public GetInfoResponse.Props getProps() {
        return props;
    }

    /**
     * Sets the value of the props property.
     * 
     * @param value
     *     allowed object is
     *     {@link GetInfoResponse.Props }
     *     
     */
    public void setProps(GetInfoResponse.Props value) {
        this.props = value;
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

        protected List<Attr> attr;

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
         * {@link Attr }
         * 
         * 
         */
        public List<Attr> getAttr() {
            if (attr == null) {
                attr = new ArrayList<Attr>();
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
     *         &lt;choice maxOccurs="unbounded" minOccurs="0">
     *           &lt;element name="pop3" type="{urn:zimbraAccount}accountPop3DataSource"/>
     *           &lt;element name="imap" type="{urn:zimbraAccount}accountImapDataSource"/>
     *           &lt;element name="rss" type="{urn:zimbraAccount}accountRssDataSource"/>
     *           &lt;element name="cal" type="{urn:zimbraAccount}accountCalDataSource"/>
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
        "pop3OrImapOrRss"
    })
    public static class DataSources {

        @XmlElements({
            @XmlElement(name = "rss", type = AccountRssDataSource.class),
            @XmlElement(name = "pop3", type = AccountPop3DataSource.class),
            @XmlElement(name = "cal", type = AccountCalDataSource.class),
            @XmlElement(name = "imap", type = AccountImapDataSource.class)
        })
        protected List<AccountDataSource> pop3OrImapOrRss;

        /**
         * Gets the value of the pop3OrImapOrRss property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the pop3OrImapOrRss property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getPop3OrImapOrRss().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link AccountRssDataSource }
         * {@link AccountPop3DataSource }
         * {@link AccountCalDataSource }
         * {@link AccountImapDataSource }
         * 
         * 
         */
        public List<AccountDataSource> getPop3OrImapOrRss() {
            if (pop3OrImapOrRss == null) {
                pop3OrImapOrRss = new ArrayList<AccountDataSource>();
            }
            return this.pop3OrImapOrRss;
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

        protected List<Identity> identity;

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
         * {@link Identity }
         * 
         * 
         */
        public List<Identity> getIdentity() {
            if (identity == null) {
                identity = new ArrayList<Identity>();
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

        protected List<Pref> pref;

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
         * {@link Pref }
         * 
         * 
         */
        public List<Pref> getPref() {
            if (pref == null) {
                pref = new ArrayList<Pref>();
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

        protected List<Prop> prop;

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
         * {@link Prop }
         * 
         * 
         */
        public List<Prop> getProp() {
            if (prop == null) {
                prop = new ArrayList<Prop>();
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

        protected List<Signature> signature;

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
         * {@link Signature }
         * 
         * 
         */
        public List<Signature> getSignature() {
            if (signature == null) {
                signature = new ArrayList<Signature>();
            }
            return this.signature;
        }

    }

}
