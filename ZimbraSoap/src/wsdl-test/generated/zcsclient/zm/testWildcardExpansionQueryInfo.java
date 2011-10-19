
package generated.zcsclient.zm;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for wildcardExpansionQueryInfo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="wildcardExpansionQueryInfo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *       &lt;/sequence>
 *       &lt;attribute name="str" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="expanded" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="numExpanded" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "wildcardExpansionQueryInfo")
public class testWildcardExpansionQueryInfo {

    @XmlAttribute(name = "str", required = true)
    protected String str;
    @XmlAttribute(name = "expanded", required = true)
    protected boolean expanded;
    @XmlAttribute(name = "numExpanded", required = true)
    protected int numExpanded;

    /**
     * Gets the value of the str property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStr() {
        return str;
    }

    /**
     * Sets the value of the str property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStr(String value) {
        this.str = value;
    }

    /**
     * Gets the value of the expanded property.
     * 
     */
    public boolean isExpanded() {
        return expanded;
    }

    /**
     * Sets the value of the expanded property.
     * 
     */
    public void setExpanded(boolean value) {
        this.expanded = value;
    }

    /**
     * Gets the value of the numExpanded property.
     * 
     */
    public int getNumExpanded() {
        return numExpanded;
    }

    /**
     * Sets the value of the numExpanded property.
     * 
     */
    public void setNumExpanded(int value) {
        this.numExpanded = value;
    }

}
