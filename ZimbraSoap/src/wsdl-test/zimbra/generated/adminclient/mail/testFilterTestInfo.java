
package zimbra.generated.adminclient.mail;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for filterTestInfo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="filterTestInfo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *       &lt;/sequence>
 *       &lt;attribute name="index" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="negative" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "filterTestInfo")
@XmlSeeAlso({
    testFilterTestSize.class,
    testFilterTestMimeHeader.class,
    testFilterTestTrue.class,
    testFilterTestInvite.class,
    testFilterTestCurrentDayOfWeek.class,
    testFilterTestAddressBook.class,
    testFilterTestCurrentTime.class,
    testFilterTestHeader.class,
    testFilterTestBody.class,
    testFilterTestDate.class,
    testFilterTestAttachment.class,
    testFilterTestHeaderExists.class
})
public class testFilterTestInfo {

    @XmlAttribute(name = "index")
    protected Integer index;
    @XmlAttribute(name = "negative")
    protected Boolean negative;

    /**
     * Gets the value of the index property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getIndex() {
        return index;
    }

    /**
     * Sets the value of the index property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setIndex(Integer value) {
        this.index = value;
    }

    /**
     * Gets the value of the negative property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isNegative() {
        return negative;
    }

    /**
     * Sets the value of the negative property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setNegative(Boolean value) {
        this.negative = value;
    }

}
