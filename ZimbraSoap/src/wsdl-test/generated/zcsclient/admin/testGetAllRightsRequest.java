
package generated.zcsclient.admin;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for getAllRightsRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getAllRightsRequest">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *       &lt;/sequence>
 *       &lt;attribute name="targetType" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="expandAllAttrs" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="rightClass" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getAllRightsRequest")
public class testGetAllRightsRequest {

    @XmlAttribute(name = "targetType")
    protected String targetType;
    @XmlAttribute(name = "expandAllAttrs")
    protected Boolean expandAllAttrs;
    @XmlAttribute(name = "rightClass")
    protected String rightClass;

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
     * Gets the value of the expandAllAttrs property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isExpandAllAttrs() {
        return expandAllAttrs;
    }

    /**
     * Sets the value of the expandAllAttrs property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setExpandAllAttrs(Boolean value) {
        this.expandAllAttrs = value;
    }

    /**
     * Gets the value of the rightClass property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRightClass() {
        return rightClass;
    }

    /**
     * Sets the value of the rightClass property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRightClass(String value) {
        this.rightClass = value;
    }

}
