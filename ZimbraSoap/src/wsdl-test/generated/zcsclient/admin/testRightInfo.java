
package generated.zcsclient.admin;

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
 *       &lt;attribute name="targetType" type="{http://www.w3.org/2001/XMLSchema}string" />
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
public class testRightInfo {

    @XmlElement(required = true)
    protected String desc;
    protected testRightsAttrs attrs;
    protected testComboRights rights;
    @XmlAttribute(name = "name", required = true)
    protected String name;
    @XmlAttribute(name = "type", required = true)
    protected testRightType type;
    @XmlAttribute(name = "targetType")
    protected String targetType;
    @XmlAttribute(name = "rightClass", required = true)
    protected testRightClass rightClass;

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
     *     {@link testRightsAttrs }
     *     
     */
    public testRightsAttrs getAttrs() {
        return attrs;
    }

    /**
     * Sets the value of the attrs property.
     * 
     * @param value
     *     allowed object is
     *     {@link testRightsAttrs }
     *     
     */
    public void setAttrs(testRightsAttrs value) {
        this.attrs = value;
    }

    /**
     * Gets the value of the rights property.
     * 
     * @return
     *     possible object is
     *     {@link testComboRights }
     *     
     */
    public testComboRights getRights() {
        return rights;
    }

    /**
     * Sets the value of the rights property.
     * 
     * @param value
     *     allowed object is
     *     {@link testComboRights }
     *     
     */
    public void setRights(testComboRights value) {
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
     *     {@link testRightType }
     *     
     */
    public testRightType getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link testRightType }
     *     
     */
    public void setType(testRightType value) {
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
     *     {@link testRightClass }
     *     
     */
    public testRightClass getRightClass() {
        return rightClass;
    }

    /**
     * Sets the value of the rightClass property.
     * 
     * @param value
     *     allowed object is
     *     {@link testRightClass }
     *     
     */
    public void setRightClass(testRightClass value) {
        this.rightClass = value;
    }

}
