
package com.zimbra.soap.mail.wsimport.generated;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for createNoteRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="createNoteRequest">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="note" type="{urn:zimbraMail}newNoteSpec"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "createNoteRequest", propOrder = {
    "note"
})
public class CreateNoteRequest {

    @XmlElement(required = true)
    protected NewNoteSpec note;

    /**
     * Gets the value of the note property.
     * 
     * @return
     *     possible object is
     *     {@link NewNoteSpec }
     *     
     */
    public NewNoteSpec getNote() {
        return note;
    }

    /**
     * Sets the value of the note property.
     * 
     * @param value
     *     allowed object is
     *     {@link NewNoteSpec }
     *     
     */
    public void setNote(NewNoteSpec value) {
        this.note = value;
    }

}
