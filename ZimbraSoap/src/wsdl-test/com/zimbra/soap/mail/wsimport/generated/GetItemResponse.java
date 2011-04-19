
package com.zimbra.soap.mail.wsimport.generated;

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
public class GetItemResponse {

    protected Folder folder;
    protected TagInfo tag;
    protected NoteInfo note;
    protected ContactInfo cn;
    protected CalendarItemInfo appt;
    protected TaskItemInfo task;
    protected ConversationSummary c;
    protected CommonDocumentInfo w;
    protected DocumentInfo doc;
    protected MessageSummary m;
    protected ChatSummary chat;

    /**
     * Gets the value of the folder property.
     * 
     * @return
     *     possible object is
     *     {@link Folder }
     *     
     */
    public Folder getFolder() {
        return folder;
    }

    /**
     * Sets the value of the folder property.
     * 
     * @param value
     *     allowed object is
     *     {@link Folder }
     *     
     */
    public void setFolder(Folder value) {
        this.folder = value;
    }

    /**
     * Gets the value of the tag property.
     * 
     * @return
     *     possible object is
     *     {@link TagInfo }
     *     
     */
    public TagInfo getTag() {
        return tag;
    }

    /**
     * Sets the value of the tag property.
     * 
     * @param value
     *     allowed object is
     *     {@link TagInfo }
     *     
     */
    public void setTag(TagInfo value) {
        this.tag = value;
    }

    /**
     * Gets the value of the note property.
     * 
     * @return
     *     possible object is
     *     {@link NoteInfo }
     *     
     */
    public NoteInfo getNote() {
        return note;
    }

    /**
     * Sets the value of the note property.
     * 
     * @param value
     *     allowed object is
     *     {@link NoteInfo }
     *     
     */
    public void setNote(NoteInfo value) {
        this.note = value;
    }

    /**
     * Gets the value of the cn property.
     * 
     * @return
     *     possible object is
     *     {@link ContactInfo }
     *     
     */
    public ContactInfo getCn() {
        return cn;
    }

    /**
     * Sets the value of the cn property.
     * 
     * @param value
     *     allowed object is
     *     {@link ContactInfo }
     *     
     */
    public void setCn(ContactInfo value) {
        this.cn = value;
    }

    /**
     * Gets the value of the appt property.
     * 
     * @return
     *     possible object is
     *     {@link CalendarItemInfo }
     *     
     */
    public CalendarItemInfo getAppt() {
        return appt;
    }

    /**
     * Sets the value of the appt property.
     * 
     * @param value
     *     allowed object is
     *     {@link CalendarItemInfo }
     *     
     */
    public void setAppt(CalendarItemInfo value) {
        this.appt = value;
    }

    /**
     * Gets the value of the task property.
     * 
     * @return
     *     possible object is
     *     {@link TaskItemInfo }
     *     
     */
    public TaskItemInfo getTask() {
        return task;
    }

    /**
     * Sets the value of the task property.
     * 
     * @param value
     *     allowed object is
     *     {@link TaskItemInfo }
     *     
     */
    public void setTask(TaskItemInfo value) {
        this.task = value;
    }

    /**
     * Gets the value of the c property.
     * 
     * @return
     *     possible object is
     *     {@link ConversationSummary }
     *     
     */
    public ConversationSummary getC() {
        return c;
    }

    /**
     * Sets the value of the c property.
     * 
     * @param value
     *     allowed object is
     *     {@link ConversationSummary }
     *     
     */
    public void setC(ConversationSummary value) {
        this.c = value;
    }

    /**
     * Gets the value of the w property.
     * 
     * @return
     *     possible object is
     *     {@link CommonDocumentInfo }
     *     
     */
    public CommonDocumentInfo getW() {
        return w;
    }

    /**
     * Sets the value of the w property.
     * 
     * @param value
     *     allowed object is
     *     {@link CommonDocumentInfo }
     *     
     */
    public void setW(CommonDocumentInfo value) {
        this.w = value;
    }

    /**
     * Gets the value of the doc property.
     * 
     * @return
     *     possible object is
     *     {@link DocumentInfo }
     *     
     */
    public DocumentInfo getDoc() {
        return doc;
    }

    /**
     * Sets the value of the doc property.
     * 
     * @param value
     *     allowed object is
     *     {@link DocumentInfo }
     *     
     */
    public void setDoc(DocumentInfo value) {
        this.doc = value;
    }

    /**
     * Gets the value of the m property.
     * 
     * @return
     *     possible object is
     *     {@link MessageSummary }
     *     
     */
    public MessageSummary getM() {
        return m;
    }

    /**
     * Sets the value of the m property.
     * 
     * @param value
     *     allowed object is
     *     {@link MessageSummary }
     *     
     */
    public void setM(MessageSummary value) {
        this.m = value;
    }

    /**
     * Gets the value of the chat property.
     * 
     * @return
     *     possible object is
     *     {@link ChatSummary }
     *     
     */
    public ChatSummary getChat() {
        return chat;
    }

    /**
     * Sets the value of the chat property.
     * 
     * @param value
     *     allowed object is
     *     {@link ChatSummary }
     *     
     */
    public void setChat(ChatSummary value) {
        this.chat = value;
    }

}
