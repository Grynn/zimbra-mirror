
package generated.zcsclient.mail;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for instanceDataAttrs complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="instanceDataAttrs">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:zimbraMail}commonInstanceDataAttrs">
 *       &lt;sequence>
 *       &lt;/sequence>
 *       &lt;attribute name="dur" type="{http://www.w3.org/2001/XMLSchema}long" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "instanceDataAttrs")
@XmlSeeAlso({
    testInstanceDataInfo.class,
    testCommonCalendaringData.class
})
public class testInstanceDataAttrs
    extends testCommonInstanceDataAttrs
{

    @XmlAttribute(name = "dur")
    protected Long dur;

    /**
     * Gets the value of the dur property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getDur() {
        return dur;
    }

    /**
     * Sets the value of the dur property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setDur(Long value) {
        this.dur = value;
    }

}
