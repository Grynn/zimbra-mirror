
package generated.zcsclient.admin;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import generated.zcsclient.zm.testTargetType;


/**
 * <p>Java class for aliasInfo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="aliasInfo">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:zimbraAdmin}adminObjectInfo">
 *       &lt;sequence>
 *       &lt;/sequence>
 *       &lt;attribute name="targetName" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="type" type="{urn:zimbra}targetType" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "aliasInfo")
public class testAliasInfo
    extends testAdminObjectInfo
{

    @XmlAttribute(name = "targetName", required = true)
    protected String targetName;
    @XmlAttribute(name = "type")
    protected testTargetType type;

    /**
     * Gets the value of the targetName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTargetName() {
        return targetName;
    }

    /**
     * Sets the value of the targetName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTargetName(String value) {
        this.targetName = value;
    }

    /**
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link testTargetType }
     *     
     */
    public testTargetType getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link testTargetType }
     *     
     */
    public void setType(testTargetType value) {
        this.type = value;
    }

}
