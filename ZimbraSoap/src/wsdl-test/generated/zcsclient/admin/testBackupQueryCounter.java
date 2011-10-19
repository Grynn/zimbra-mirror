
package generated.zcsclient.admin;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for backupQueryCounter complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="backupQueryCounter">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *       &lt;/sequence>
 *       &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="unit" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="sum" use="required" type="{http://www.w3.org/2001/XMLSchema}long" />
 *       &lt;attribute name="numSamples" use="required" type="{http://www.w3.org/2001/XMLSchema}long" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "backupQueryCounter")
public class testBackupQueryCounter {

    @XmlAttribute(name = "name", required = true)
    protected String name;
    @XmlAttribute(name = "unit", required = true)
    protected String unit;
    @XmlAttribute(name = "sum", required = true)
    protected long sum;
    @XmlAttribute(name = "numSamples", required = true)
    protected long numSamples;

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
     * Gets the value of the unit property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUnit() {
        return unit;
    }

    /**
     * Sets the value of the unit property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUnit(String value) {
        this.unit = value;
    }

    /**
     * Gets the value of the sum property.
     * 
     */
    public long getSum() {
        return sum;
    }

    /**
     * Sets the value of the sum property.
     * 
     */
    public void setSum(long value) {
        this.sum = value;
    }

    /**
     * Gets the value of the numSamples property.
     * 
     */
    public long getNumSamples() {
        return numSamples;
    }

    /**
     * Sets the value of the numSamples property.
     * 
     */
    public void setNumSamples(long value) {
        this.numSamples = value;
    }

}
