
package zimbra.generated.accountclient.mail;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for getItemResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getItemResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;choice minOccurs="0">
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
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getItemResponse", propOrder = {
    "folder",
    "tag",
    "note",
    "cn",
    "appt",
    "task",
    "c",
    "w",
    "doc",
    "m",
    "chat"
})
public class testGetItemResponse {

    protected testFolder folder;
    protected testTagInfo tag;
    protected testNoteInfo note;
    protected testContactInfo cn;
    protected testCalendarItemInfo appt;
    protected testTaskItemInfo task;
    protected testConversationSummary c;
    protected testCommonDocumentInfo w;
    protected testDocumentInfo doc;
    protected testMessageSummary m;
    protected testChatSummary chat;

    /**
     * Gets the value of the folder property.
     * 
     * @return
     *     possible object is
     *     {@link testFolder }
     *     
     */
    public testFolder getFolder() {
        return folder;
    }

    /**
     * Sets the value of the folder property.
     * 
     * @param value
     *     allowed object is
     *     {@link testFolder }
     *     
     */
    public void setFolder(testFolder value) {
        this.folder = value;
    }

    /**
     * Gets the value of the tag property.
     * 
     * @return
     *     possible object is
     *     {@link testTagInfo }
     *     
     */
    public testTagInfo getTag() {
        return tag;
    }

    /**
     * Sets the value of the tag property.
     * 
     * @param value
     *     allowed object is
     *     {@link testTagInfo }
     *     
     */
    public void setTag(testTagInfo value) {
        this.tag = value;
    }

    /**
     * Gets the value of the note property.
     * 
     * @return
     *     possible object is
     *     {@link testNoteInfo }
     *     
     */
    public testNoteInfo getNote() {
        return note;
    }

    /**
     * Sets the value of the note property.
     * 
     * @param value
     *     allowed object is
     *     {@link testNoteInfo }
     *     
     */
    public void setNote(testNoteInfo value) {
        this.note = value;
    }

    /**
     * Gets the value of the cn property.
     * 
     * @return
     *     possible object is
     *     {@link testContactInfo }
     *     
     */
    public testContactInfo getCn() {
        return cn;
    }

    /**
     * Sets the value of the cn property.
     * 
     * @param value
     *     allowed object is
     *     {@link testContactInfo }
     *     
     */
    public void setCn(testContactInfo value) {
        this.cn = value;
    }

    /**
     * Gets the value of the appt property.
     * 
     * @return
     *     possible object is
     *     {@link testCalendarItemInfo }
     *     
     */
    public testCalendarItemInfo getAppt() {
        return appt;
    }

    /**
     * Sets the value of the appt property.
     * 
     * @param value
     *     allowed object is
     *     {@link testCalendarItemInfo }
     *     
     */
    public void setAppt(testCalendarItemInfo value) {
        this.appt = value;
    }

    /**
     * Gets the value of the task property.
     * 
     * @return
     *     possible object is
     *     {@link testTaskItemInfo }
     *     
     */
    public testTaskItemInfo getTask() {
        return task;
    }

    /**
     * Sets the value of the task property.
     * 
     * @param value
     *     allowed object is
     *     {@link testTaskItemInfo }
     *     
     */
    public void setTask(testTaskItemInfo value) {
        this.task = value;
    }

    /**
     * Gets the value of the c property.
     * 
     * @return
     *     possible object is
     *     {@link testConversationSummary }
     *     
     */
    public testConversationSummary getC() {
        return c;
    }

    /**
     * Sets the value of the c property.
     * 
     * @param value
     *     allowed object is
     *     {@link testConversationSummary }
     *     
     */
    public void setC(testConversationSummary value) {
        this.c = value;
    }

    /**
     * Gets the value of the w property.
     * 
     * @return
     *     possible object is
     *     {@link testCommonDocumentInfo }
     *     
     */
    public testCommonDocumentInfo getW() {
        return w;
    }

    /**
     * Sets the value of the w property.
     * 
     * @param value
     *     allowed object is
     *     {@link testCommonDocumentInfo }
     *     
     */
    public void setW(testCommonDocumentInfo value) {
        this.w = value;
    }

    /**
     * Gets the value of the doc property.
     * 
     * @return
     *     possible object is
     *     {@link testDocumentInfo }
     *     
     */
    public testDocumentInfo getDoc() {
        return doc;
    }

    /**
     * Sets the value of the doc property.
     * 
     * @param value
     *     allowed object is
     *     {@link testDocumentInfo }
     *     
     */
    public void setDoc(testDocumentInfo value) {
        this.doc = value;
    }

    /**
     * Gets the value of the m property.
     * 
     * @return
     *     possible object is
     *     {@link testMessageSummary }
     *     
     */
    public testMessageSummary getM() {
        return m;
    }

    /**
     * Sets the value of the m property.
     * 
     * @param value
     *     allowed object is
     *     {@link testMessageSummary }
     *     
     */
    public void setM(testMessageSummary value) {
        this.m = value;
    }

    /**
     * Gets the value of the chat property.
     * 
     * @return
     *     possible object is
     *     {@link testChatSummary }
     *     
     */
    public testChatSummary getChat() {
        return chat;
    }

    /**
     * Sets the value of the chat property.
     * 
     * @param value
     *     allowed object is
     *     {@link testChatSummary }
     *     
     */
    public void setChat(testChatSummary value) {
        this.chat = value;
    }

}
