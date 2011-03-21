
package com.zimbra.soap.mail.wsimport.generated;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for folder complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="folder">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="acl" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="grant" type="{urn:zimbraMail}grant" maxOccurs="unbounded" minOccurs="0"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;choice maxOccurs="unbounded" minOccurs="0">
 *           &lt;element ref="{urn:zimbraMail}folder"/>
 *           &lt;element ref="{urn:zimbraMail}link"/>
 *           &lt;element ref="{urn:zimbraMail}search"/>
 *         &lt;/choice>
 *       &lt;/sequence>
 *       &lt;attribute name="id" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="l" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="f" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="color" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="u" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="i4u" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="n" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="i4n" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="s" type="{http://www.w3.org/2001/XMLSchema}long" />
 *       &lt;attribute name="view" type="{urn:zimbraMail}view" />
 *       &lt;attribute name="url" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="perm" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="rest" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="ms" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="ref" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="i4next" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="i4ms" type="{http://www.w3.org/2001/XMLSchema}int" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "folder", propOrder = {
    "acl",
    "folderOrLinkOrSearch"
})
@XmlSeeAlso({
    Mountpoint.class,
    SearchFolder.class
})
public class Folder {

    protected Folder.Acl acl;
    @XmlElements({
        @XmlElement(name = "search", type = SearchFolder.class),
        @XmlElement(name = "link", type = Mountpoint.class),
        @XmlElement(name = "folder")
    })
    protected List<Folder> folderOrLinkOrSearch;
    @XmlAttribute
    protected String id;
    @XmlAttribute
    protected String name;
    @XmlAttribute
    protected String l;
    @XmlAttribute
    protected String f;
    @XmlAttribute
    protected Integer color;
    @XmlAttribute
    protected Integer u;
    @XmlAttribute(name = "i4u")
    protected Integer i4U;
    @XmlAttribute
    protected Integer n;
    @XmlAttribute(name = "i4n")
    protected Integer i4N;
    @XmlAttribute
    protected Long s;
    @XmlAttribute
    protected String view;
    @XmlAttribute
    protected String url;
    @XmlAttribute
    protected String perm;
    @XmlAttribute
    protected String rest;
    @XmlAttribute
    protected Integer ms;
    @XmlAttribute
    protected Integer ref;
    @XmlAttribute(name = "i4next")
    protected Integer i4Next;
    @XmlAttribute(name = "i4ms")
    protected Integer i4Ms;

    /**
     * Gets the value of the acl property.
     * 
     * @return
     *     possible object is
     *     {@link Folder.Acl }
     *     
     */
    public Folder.Acl getAcl() {
        return acl;
    }

    /**
     * Sets the value of the acl property.
     * 
     * @param value
     *     allowed object is
     *     {@link Folder.Acl }
     *     
     */
    public void setAcl(Folder.Acl value) {
        this.acl = value;
    }

    /**
     * Gets the value of the folderOrLinkOrSearch property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the folderOrLinkOrSearch property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getFolderOrLinkOrSearch().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SearchFolder }
     * {@link Mountpoint }
     * {@link Folder }
     * 
     * 
     */
    public List<Folder> getFolderOrLinkOrSearch() {
        if (folderOrLinkOrSearch == null) {
            folderOrLinkOrSearch = new ArrayList<Folder>();
        }
        return this.folderOrLinkOrSearch;
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
     * Gets the value of the l property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getL() {
        return l;
    }

    /**
     * Sets the value of the l property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setL(String value) {
        this.l = value;
    }

    /**
     * Gets the value of the f property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getF() {
        return f;
    }

    /**
     * Sets the value of the f property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setF(String value) {
        this.f = value;
    }

    /**
     * Gets the value of the color property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getColor() {
        return color;
    }

    /**
     * Sets the value of the color property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setColor(Integer value) {
        this.color = value;
    }

    /**
     * Gets the value of the u property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getU() {
        return u;
    }

    /**
     * Sets the value of the u property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setU(Integer value) {
        this.u = value;
    }

    /**
     * Gets the value of the i4U property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getI4U() {
        return i4U;
    }

    /**
     * Sets the value of the i4U property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setI4U(Integer value) {
        this.i4U = value;
    }

    /**
     * Gets the value of the n property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getN() {
        return n;
    }

    /**
     * Sets the value of the n property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setN(Integer value) {
        this.n = value;
    }

    /**
     * Gets the value of the i4N property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getI4N() {
        return i4N;
    }

    /**
     * Sets the value of the i4N property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setI4N(Integer value) {
        this.i4N = value;
    }

    /**
     * Gets the value of the s property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getS() {
        return s;
    }

    /**
     * Sets the value of the s property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setS(Long value) {
        this.s = value;
    }

    /**
     * Gets the value of the view property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getView() {
        return view;
    }

    /**
     * Sets the value of the view property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setView(String value) {
        this.view = value;
    }

    /**
     * Gets the value of the url property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUrl() {
        return url;
    }

    /**
     * Sets the value of the url property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUrl(String value) {
        this.url = value;
    }

    /**
     * Gets the value of the perm property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPerm() {
        return perm;
    }

    /**
     * Sets the value of the perm property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPerm(String value) {
        this.perm = value;
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
     * Gets the value of the ms property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getMs() {
        return ms;
    }

    /**
     * Sets the value of the ms property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setMs(Integer value) {
        this.ms = value;
    }

    /**
     * Gets the value of the ref property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getRef() {
        return ref;
    }

    /**
     * Sets the value of the ref property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setRef(Integer value) {
        this.ref = value;
    }

    /**
     * Gets the value of the i4Next property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getI4Next() {
        return i4Next;
    }

    /**
     * Sets the value of the i4Next property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setI4Next(Integer value) {
        this.i4Next = value;
    }

    /**
     * Gets the value of the i4Ms property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getI4Ms() {
        return i4Ms;
    }

    /**
     * Sets the value of the i4Ms property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setI4Ms(Integer value) {
        this.i4Ms = value;
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
     *         &lt;element name="grant" type="{urn:zimbraMail}grant" maxOccurs="unbounded" minOccurs="0"/>
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
        "grant"
    })
    public static class Acl {

        protected List<Grant> grant;

        /**
         * Gets the value of the grant property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the grant property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getGrant().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link Grant }
         * 
         * 
         */
        public List<Grant> getGrant() {
            if (grant == null) {
                grant = new ArrayList<Grant>();
            }
            return this.grant;
        }

    }

}
