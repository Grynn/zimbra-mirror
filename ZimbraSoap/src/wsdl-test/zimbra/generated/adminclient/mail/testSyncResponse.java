
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
 * <p>Java class for syncResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="syncResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="deleted" type="{urn:zimbraMail}syncDeletedInfo" minOccurs="0"/>
 *         &lt;choice maxOccurs="unbounded" minOccurs="0">
 *           &lt;element ref="{urn:zimbraMail}folder"/>
 *           &lt;element name="tag" type="{urn:zimbraMail}tagInfo"/>
 *           &lt;element name="note" type="{urn:zimbraMail}noteInfo"/>
 *           &lt;element name="cn" type="{urn:zimbraMail}contactInfo"/>
 *           &lt;element name="appt" type="{urn:zimbraMail}calendarItemInfo"/>
 *           &lt;element name="task" type="{urn:zimbraMail}taskItemInfo"/>
 *           &lt;element name="c" type="{urn:zimbraMail}conversationSummary"/>
 *           &lt;element name="w" type="{urn:zimbraMail}commonDocumentInfo"/>
 *           &lt;element name="doc" type="{urn:zimbraMail}documentInfo"/>
 *           &lt;element name="m" type="{urn:zimbraMail}messageSummary"/>
 *           &lt;element name="chat" type="{urn:zimbraMail}chatSummary"/>
 *         &lt;/choice>
 *       &lt;/sequence>
 *       &lt;attribute name="md" use="required" type="{http://www.w3.org/2001/XMLSchema}long" />
 *       &lt;attribute name="token" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="s" type="{http://www.w3.org/2001/XMLSchema}long" />
 *       &lt;attribute name="more" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "syncResponse", propOrder = {
    "deleted",
    "folderOrTagOrNote"
})
public class testSyncResponse {

    protected testSyncDeletedInfo deleted;
    @XmlElements({
        @XmlElement(name = "folder", type = testFolder.class),
        @XmlElement(name = "m", type = testMessageSummary.class),
        @XmlElement(name = "task", type = testTaskItemInfo.class),
        @XmlElement(name = "note", type = testNoteInfo.class),
        @XmlElement(name = "tag", type = testTagInfo.class),
        @XmlElement(name = "chat", type = testChatSummary.class),
        @XmlElement(name = "appt", type = testCalendarItemInfo.class),
        @XmlElement(name = "doc", type = testDocumentInfo.class),
        @XmlElement(name = "c", type = testConversationSummary.class),
        @XmlElement(name = "cn", type = testContactInfo.class),
        @XmlElement(name = "w", type = testCommonDocumentInfo.class)
    })
    protected List<Object> folderOrTagOrNote;
    @XmlAttribute(name = "md", required = true)
    protected long md;
    @XmlAttribute(name = "token")
    protected String token;
    @XmlAttribute(name = "s")
    protected Long s;
    @XmlAttribute(name = "more")
    protected Boolean more;

    /**
     * Gets the value of the deleted property.
     * 
     * @return
     *     possible object is
     *     {@link testSyncDeletedInfo }
     *     
     */
    public testSyncDeletedInfo getDeleted() {
        return deleted;
    }

    /**
     * Sets the value of the deleted property.
     * 
     * @param value
     *     allowed object is
     *     {@link testSyncDeletedInfo }
     *     
     */
    public void setDeleted(testSyncDeletedInfo value) {
        this.deleted = value;
    }

    /**
     * Gets the value of the folderOrTagOrNote property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the folderOrTagOrNote property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getFolderOrTagOrNote().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link testFolder }
     * {@link testMessageSummary }
     * {@link testTaskItemInfo }
     * {@link testNoteInfo }
     * {@link testTagInfo }
     * {@link testChatSummary }
     * {@link testCalendarItemInfo }
     * {@link testDocumentInfo }
     * {@link testConversationSummary }
     * {@link testContactInfo }
     * {@link testCommonDocumentInfo }
     * 
     * 
     */
    public List<Object> getFolderOrTagOrNote() {
        if (folderOrTagOrNote == null) {
            folderOrTagOrNote = new ArrayList<Object>();
        }
        return this.folderOrTagOrNote;
    }

    /**
     * Gets the value of the md property.
     * 
     */
    public long getMd() {
        return md;
    }

    /**
     * Sets the value of the md property.
     * 
     */
    public void setMd(long value) {
        this.md = value;
    }

    /**
     * Gets the value of the token property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getToken() {
        return token;
    }

    /**
     * Sets the value of the token property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setToken(String value) {
        this.token = value;
    }

    /**
     * Gets the value of the s property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getS() {
        return s;
    }

    /**
     * Sets the value of the s property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setS(Long value) {
        this.s = value;
    }

    /**
     * Gets the value of the more property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isMore() {
        return more;
    }

    /**
     * Sets the value of the more property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setMore(Boolean value) {
        this.more = value;
    }

}
