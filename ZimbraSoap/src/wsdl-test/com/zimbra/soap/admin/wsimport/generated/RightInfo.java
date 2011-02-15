
package com.zimbra.soap.admin.wsimport.generated;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for rightInfo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="rightInfo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="desc" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="attrs" type="{urn:zimbraAdmin}rightsAttrs" minOccurs="0"/>
 *         &lt;element name="rights" type="{urn:zimbraAdmin}comboRights" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="type" use="required" type="{urn:zimbraAdmin}rightType" />
 *       &lt;attribute name="targetType" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="rightClass" use="required" type="{urn:zimbraAdmin}rightClass" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "rightInfo", propOrder = {
    "desc",
    "attrs",
    "rights"
})
public class RightInfo {

    @XmlElement(required = true)
    protected String desc;
    protected RightsAttrs attrs;
    protected ComboRights rights;
    @XmlAttribute(required = true)
    protected String name;
    @XmlAttribute(required = true)
    protected RightType type;
    @XmlAttribute(required = true)
    protected String targetType;
    @XmlAttribute(required = true)
    protected RightClass rightClass;

    /**
     * Gets the value of the desc property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDesc() {
        return desc;
    }

    /**
     * Sets the value of the desc property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDesc(String value) {
        this.desc = value;
    }

    /**
     * Gets the value of the attrs property.
     * 
     * @return
     *     possible object is
     *     {@link RightsAttrs }
     *     
     */
    public RightsAttrs getAttrs() {
        return attrs;
    }

    /**
     * Sets the value of the attrs property.
     * 
     * @param value
     *     allowed object is
     *     {@link RightsAttrs }
     *     
     */
    public void setAttrs(RightsAttrs value) {
        this.attrs = value;
    }

    /**
     * Gets the value of the rights property.
     * 
     * @return
     *     possible object is
     *     {@link ComboRights }
     *     
     */
    public ComboRights getRights() {
        return rights;
    }

    /**
     * Sets the value of the rights property.
     * 
     * @param value
     *     allowed object is
     *     {@link ComboRights }
     *     
     */
    public void setRights(ComboRights value) {
        this.rights = value;
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
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link RightType }
     *     
     */
    public RightType getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link RightType }
     *     
     */
    public void setType(RightType value) {
        this.type = value;
    }

    /**
     * Gets the value of the targetType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTargetType() {
        return targetType;
    }

    /**
     * Sets the value of the targetType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTargetType(String value) {
        this.targetType = value;
    }

    /**
     * Gets the value of the rightClass property.
     * 
     * @return
     *     possible object is
     *     {@link RightClass }
     *     
     */
    public RightClass getRightClass() {
        return rightClass;
    }

    /**
     * Sets the value of the rightClass property.
     * 
     * @param value
     *     allowed object is
     *     {@link RightClass }
     *     
     */
    public void setRightClass(RightClass value) {
        this.rightClass = value;
    }

}
