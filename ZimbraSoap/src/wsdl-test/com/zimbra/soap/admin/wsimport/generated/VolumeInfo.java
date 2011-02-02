
package com.zimbra.soap.admin.wsimport.generated;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for volumeInfo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="volumeInfo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *       &lt;/sequence>
 *       &lt;attribute name="id" type="{http://www.w3.org/2001/XMLSchema}short" />
 *       &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="rootpath" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="type" type="{http://www.w3.org/2001/XMLSchema}short" />
 *       &lt;attribute name="compressBlobs" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="compressionThreshold" type="{http://www.w3.org/2001/XMLSchema}long" />
 *       &lt;attribute name="mgbits" type="{http://www.w3.org/2001/XMLSchema}short" />
 *       &lt;attribute name="mbits" type="{http://www.w3.org/2001/XMLSchema}short" />
 *       &lt;attribute name="fgbits" type="{http://www.w3.org/2001/XMLSchema}short" />
 *       &lt;attribute name="fbits" type="{http://www.w3.org/2001/XMLSchema}short" />
 *       &lt;attribute name="isCurrent" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "volumeInfo")
public class VolumeInfo {

    @XmlAttribute
    protected Short id;
    @XmlAttribute
    protected String name;
    @XmlAttribute
    protected String rootpath;
    @XmlAttribute
    protected Short type;
    @XmlAttribute
    protected Boolean compressBlobs;
    @XmlAttribute
    protected Long compressionThreshold;
    @XmlAttribute
    protected Short mgbits;
    @XmlAttribute
    protected Short mbits;
    @XmlAttribute
    protected Short fgbits;
    @XmlAttribute
    protected Short fbits;
    @XmlAttribute
    protected Boolean isCurrent;

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link Short }
     *     
     */
    public Short getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link Short }
     *     
     */
    public void setId(Short value) {
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
     * Gets the value of the rootpath property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRootpath() {
        return rootpath;
    }

    /**
     * Sets the value of the rootpath property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRootpath(String value) {
        this.rootpath = value;
    }

    /**
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link Short }
     *     
     */
    public Short getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link Short }
     *     
     */
    public void setType(Short value) {
        this.type = value;
    }

    /**
     * Gets the value of the compressBlobs property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isCompressBlobs() {
        return compressBlobs;
    }

    /**
     * Sets the value of the compressBlobs property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setCompressBlobs(Boolean value) {
        this.compressBlobs = value;
    }

    /**
     * Gets the value of the compressionThreshold property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getCompressionThreshold() {
        return compressionThreshold;
    }

    /**
     * Sets the value of the compressionThreshold property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setCompressionThreshold(Long value) {
        this.compressionThreshold = value;
    }

    /**
     * Gets the value of the mgbits property.
     * 
     * @return
     *     possible object is
     *     {@link Short }
     *     
     */
    public Short getMgbits() {
        return mgbits;
    }

    /**
     * Sets the value of the mgbits property.
     * 
     * @param value
     *     allowed object is
     *     {@link Short }
     *     
     */
    public void setMgbits(Short value) {
        this.mgbits = value;
    }

    /**
     * Gets the value of the mbits property.
     * 
     * @return
     *     possible object is
     *     {@link Short }
     *     
     */
    public Short getMbits() {
        return mbits;
    }

    /**
     * Sets the value of the mbits property.
     * 
     * @param value
     *     allowed object is
     *     {@link Short }
     *     
     */
    public void setMbits(Short value) {
        this.mbits = value;
    }

    /**
     * Gets the value of the fgbits property.
     * 
     * @return
     *     possible object is
     *     {@link Short }
     *     
     */
    public Short getFgbits() {
        return fgbits;
    }

    /**
     * Sets the value of the fgbits property.
     * 
     * @param value
     *     allowed object is
     *     {@link Short }
     *     
     */
    public void setFgbits(Short value) {
        this.fgbits = value;
    }

    /**
     * Gets the value of the fbits property.
     * 
     * @return
     *     possible object is
     *     {@link Short }
     *     
     */
    public Short getFbits() {
        return fbits;
    }

    /**
     * Sets the value of the fbits property.
     * 
     * @param value
     *     allowed object is
     *     {@link Short }
     *     
     */
    public void setFbits(Short value) {
        this.fbits = value;
    }

    /**
     * Gets the value of the isCurrent property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isIsCurrent() {
        return isCurrent;
    }

    /**
     * Sets the value of the isCurrent property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setIsCurrent(Boolean value) {
        this.isCurrent = value;
    }

}
