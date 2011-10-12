
package zimbra.generated.mailclient.mail;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for filterTest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="filterTest">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *       &lt;/sequence>
 *       &lt;attribute name="index" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="negative" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "filterTest")
@XmlSeeAlso({
    testImportanceTest.class,
    testLinkedInTest.class,
    testCurrentTimeTest.class,
    testHeaderTest.class,
    testBodyTest.class,
    testSizeTest.class,
    testListTest.class,
    testFlaggedTest.class,
    testFacebookTest.class,
    testCurrentDayOfWeekTest.class,
    testTrueTest.class,
    testAttachmentTest.class,
    testContactRankingTest.class,
    testHeaderExistsTest.class,
    testAddressTest.class,
    testDateTest.class,
    testTwitterTest.class,
    testBulkTest.class,
    testMimeHeaderTest.class,
    testConversationTest.class,
    testAddressBookTest.class,
    testSocialcastTest.class,
    testInviteTest.class,
    testMeTest.class
})
public class testFilterTest {

    @XmlAttribute(name = "index", required = true)
    protected int index;
    @XmlAttribute(name = "negative")
    protected Boolean negative;

    /**
     * Gets the value of the index property.
     * 
     */
    public int getIndex() {
        return index;
    }

    /**
     * Sets the value of the index property.
     * 
     */
    public void setIndex(int value) {
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
