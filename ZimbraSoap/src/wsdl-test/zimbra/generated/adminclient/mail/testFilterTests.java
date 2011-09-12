
package zimbra.generated.adminclient.mail;

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
 *           &lt;element name="headerTest" type="{urn:zimbraMail}filterTestHeader"/>
 *           &lt;element name="mimeHeaderTest" type="{urn:zimbraMail}filterTestMimeHeader"/>
 *           &lt;element name="headerExistsTest" type="{urn:zimbraMail}filterTestHeaderExists"/>
 *           &lt;element name="sizeTest" type="{urn:zimbraMail}filterTestSize"/>
 *           &lt;element name="dateTest" type="{urn:zimbraMail}filterTestDate"/>
 *           &lt;element name="bodyTest" type="{urn:zimbraMail}filterTestBody"/>
 *           &lt;element name="attachmentTest" type="{urn:zimbraMail}filterTestAttachment"/>
 *           &lt;element name="addressBookTest" type="{urn:zimbraMail}filterTestAddressBook"/>
 *           &lt;element name="inviteTest" type="{urn:zimbraMail}filterTestInvite"/>
 *           &lt;element name="currentTimeTest" type="{urn:zimbraMail}filterTestCurrentTime"/>
 *           &lt;element name="currentDayOfWeekTest" type="{urn:zimbraMail}filterTestCurrentDayOfWeek"/>
 *           &lt;element name="importanceTest" type="{urn:zimbraMail}filterTestImportance"/>
 *           &lt;element name="trueTest" type="{urn:zimbraMail}filterTestTrue"/>
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
    "headerTestOrMimeHeaderTestOrHeaderExistsTest"
})
public class testFilterTests {

    @XmlElements({
        @XmlElement(name = "mimeHeaderTest", type = testFilterTestMimeHeader.class),
        @XmlElement(name = "headerTest", type = testFilterTestHeader.class),
        @XmlElement(name = "inviteTest", type = testFilterTestInvite.class),
        @XmlElement(name = "importanceTest", type = testFilterTestImportance.class),
        @XmlElement(name = "headerExistsTest", type = testFilterTestHeaderExists.class),
        @XmlElement(name = "currentTimeTest", type = testFilterTestCurrentTime.class),
        @XmlElement(name = "bodyTest", type = testFilterTestBody.class),
        @XmlElement(name = "trueTest", type = testFilterTestTrue.class),
        @XmlElement(name = "attachmentTest", type = testFilterTestAttachment.class),
        @XmlElement(name = "currentDayOfWeekTest", type = testFilterTestCurrentDayOfWeek.class),
        @XmlElement(name = "dateTest", type = testFilterTestDate.class),
        @XmlElement(name = "sizeTest", type = testFilterTestSize.class),
        @XmlElement(name = "addressBookTest", type = testFilterTestAddressBook.class)
    })
    protected List<testFilterTestInfo> headerTestOrMimeHeaderTestOrHeaderExistsTest;
    @XmlAttribute(name = "condition", required = true)
    protected String condition;

    /**
     * Gets the value of the headerTestOrMimeHeaderTestOrHeaderExistsTest property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the headerTestOrMimeHeaderTestOrHeaderExistsTest property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getHeaderTestOrMimeHeaderTestOrHeaderExistsTest().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link testFilterTestMimeHeader }
     * {@link testFilterTestHeader }
     * {@link testFilterTestInvite }
     * {@link testFilterTestImportance }
     * {@link testFilterTestHeaderExists }
     * {@link testFilterTestCurrentTime }
     * {@link testFilterTestBody }
     * {@link testFilterTestTrue }
     * {@link testFilterTestAttachment }
     * {@link testFilterTestCurrentDayOfWeek }
     * {@link testFilterTestDate }
     * {@link testFilterTestSize }
     * {@link testFilterTestAddressBook }
     * 
     * 
     */
    public List<testFilterTestInfo> getHeaderTestOrMimeHeaderTestOrHeaderExistsTest() {
        if (headerTestOrMimeHeaderTestOrHeaderExistsTest == null) {
            headerTestOrMimeHeaderTestOrHeaderExistsTest = new ArrayList<testFilterTestInfo>();
        }
        return this.headerTestOrMimeHeaderTestOrHeaderExistsTest;
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
