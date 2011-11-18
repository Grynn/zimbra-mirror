
package generated.zcsclient.mail;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for filterTests complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="filterTests">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;choice maxOccurs="unbounded" minOccurs="0">
 *           &lt;element name="addressBookTest" type="{urn:zimbraMail}addressBookTest"/>
 *           &lt;element name="addressTest" type="{urn:zimbraMail}addressTest"/>
 *           &lt;element name="attachmentTest" type="{urn:zimbraMail}attachmentTest"/>
 *           &lt;element name="bodyTest" type="{urn:zimbraMail}bodyTest"/>
 *           &lt;element name="bulkTest" type="{urn:zimbraMail}bulkTest"/>
 *           &lt;element name="contactRankingTest" type="{urn:zimbraMail}contactRankingTest"/>
 *           &lt;element name="conversationTest" type="{urn:zimbraMail}conversationTest"/>
 *           &lt;element name="currentDayOfWeekTest" type="{urn:zimbraMail}currentDayOfWeekTest"/>
 *           &lt;element name="currentTimeTest" type="{urn:zimbraMail}currentTimeTest"/>
 *           &lt;element name="dateTest" type="{urn:zimbraMail}dateTest"/>
 *           &lt;element name="facebookTest" type="{urn:zimbraMail}facebookTest"/>
 *           &lt;element name="flaggedTest" type="{urn:zimbraMail}flaggedTest"/>
 *           &lt;element name="headerExistsTest" type="{urn:zimbraMail}headerExistsTest"/>
 *           &lt;element name="headerTest" type="{urn:zimbraMail}headerTest"/>
 *           &lt;element name="importanceTest" type="{urn:zimbraMail}importanceTest"/>
 *           &lt;element name="inviteTest" type="{urn:zimbraMail}inviteTest"/>
 *           &lt;element name="linkedinTest" type="{urn:zimbraMail}linkedInTest"/>
 *           &lt;element name="listTest" type="{urn:zimbraMail}listTest"/>
 *           &lt;element name="meTest" type="{urn:zimbraMail}meTest"/>
 *           &lt;element name="mimeHeaderTest" type="{urn:zimbraMail}mimeHeaderTest"/>
 *           &lt;element name="sizeTest" type="{urn:zimbraMail}sizeTest"/>
 *           &lt;element name="socialcastTest" type="{urn:zimbraMail}socialcastTest"/>
 *           &lt;element name="trueTest" type="{urn:zimbraMail}trueTest"/>
 *           &lt;element name="twitterTest" type="{urn:zimbraMail}twitterTest"/>
 *         &lt;/choice>
 *       &lt;/sequence>
 *       &lt;attribute name="condition" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "filterTests", propOrder = {
    "addressBookTestOrAddressTestOrAttachmentTest"
})
public class testFilterTests {

    @XmlElements({
        @XmlElement(name = "addressBookTest", type = testAddressBookTest.class),
        @XmlElement(name = "trueTest", type = testTrueTest.class),
        @XmlElement(name = "dateTest", type = testDateTest.class),
        @XmlElement(name = "bulkTest", type = testBulkTest.class),
        @XmlElement(name = "currentTimeTest", type = testCurrentTimeTest.class),
        @XmlElement(name = "attachmentTest", type = testAttachmentTest.class),
        @XmlElement(name = "inviteTest", type = testInviteTest.class),
        @XmlElement(name = "currentDayOfWeekTest", type = testCurrentDayOfWeekTest.class),
        @XmlElement(name = "sizeTest", type = testSizeTest.class),
        @XmlElement(name = "twitterTest", type = testTwitterTest.class),
        @XmlElement(name = "linkedinTest", type = testLinkedInTest.class),
        @XmlElement(name = "importanceTest", type = testImportanceTest.class),
        @XmlElement(name = "bodyTest", type = testBodyTest.class),
        @XmlElement(name = "flaggedTest", type = testFlaggedTest.class),
        @XmlElement(name = "facebookTest", type = testFacebookTest.class),
        @XmlElement(name = "socialcastTest", type = testSocialcastTest.class),
        @XmlElement(name = "contactRankingTest", type = testContactRankingTest.class),
        @XmlElement(name = "listTest", type = testListTest.class),
        @XmlElement(name = "conversationTest", type = testConversationTest.class),
        @XmlElement(name = "headerTest", type = testHeaderTest.class),
        @XmlElement(name = "meTest", type = testMeTest.class),
        @XmlElement(name = "mimeHeaderTest", type = testMimeHeaderTest.class),
        @XmlElement(name = "headerExistsTest", type = testHeaderExistsTest.class),
        @XmlElement(name = "addressTest", type = testAddressTest.class)
    })
    protected List<testFilterTest> addressBookTestOrAddressTestOrAttachmentTest;
    @XmlAttribute(name = "condition", required = true)
    protected String condition;

    /**
     * Gets the value of the addressBookTestOrAddressTestOrAttachmentTest property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the addressBookTestOrAddressTestOrAttachmentTest property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAddressBookTestOrAddressTestOrAttachmentTest().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link testAddressBookTest }
     * {@link testTrueTest }
     * {@link testDateTest }
     * {@link testBulkTest }
     * {@link testCurrentTimeTest }
     * {@link testAttachmentTest }
     * {@link testInviteTest }
     * {@link testCurrentDayOfWeekTest }
     * {@link testSizeTest }
     * {@link testTwitterTest }
     * {@link testLinkedInTest }
     * {@link testImportanceTest }
     * {@link testBodyTest }
     * {@link testFlaggedTest }
     * {@link testFacebookTest }
     * {@link testSocialcastTest }
     * {@link testContactRankingTest }
     * {@link testListTest }
     * {@link testConversationTest }
     * {@link testHeaderTest }
     * {@link testMeTest }
     * {@link testMimeHeaderTest }
     * {@link testHeaderExistsTest }
     * {@link testAddressTest }
     * 
     * 
     */
    public List<testFilterTest> getAddressBookTestOrAddressTestOrAttachmentTest() {
        if (addressBookTestOrAddressTestOrAttachmentTest == null) {
            addressBookTestOrAddressTestOrAttachmentTest = new ArrayList<testFilterTest>();
        }
        return this.addressBookTestOrAddressTestOrAttachmentTest;
    }

    /**
     * Gets the value of the condition property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCondition() {
        return condition;
    }

    /**
     * Sets the value of the condition property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCondition(String value) {
        this.condition = value;
    }

}
