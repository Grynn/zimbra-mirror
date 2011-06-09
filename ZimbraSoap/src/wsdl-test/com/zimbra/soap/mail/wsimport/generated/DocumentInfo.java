
package com.zimbra.soap.mail.wsimport.generated;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for documentInfo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="documentInfo">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:zimbraMail}commonDocumentInfo">
 *       &lt;sequence>
 *       &lt;/sequence>
 *       &lt;attribute name="loid" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="loe" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="lt" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "documentInfo")
@XmlSeeAlso({
    DocumentHitInfo.class
})
public class DocumentInfo
    extends CommonDocumentInfo
{

    @XmlAttribute
    protected String loid;
    @XmlAttribute
    protected String loe;
    @XmlAttribute
    protected String lt;

    /**
     * Gets the value of the loid property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLoid() {
        return loid;
    }

    /**
     * Sets the value of the loid property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLoid(String value) {
        this.loid = value;
    }

    /**
     * Gets the value of the loe property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLoe() {
        return loe;
    }

    /**
     * Sets the value of the loe property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLoe(String value) {
        this.loe = value;
    }

    /**
     * Gets the value of the lt property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLt() {
        return lt;
    }

    /**
     * Sets the value of the lt property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLt(String value) {
        this.lt = value;
    }

}
