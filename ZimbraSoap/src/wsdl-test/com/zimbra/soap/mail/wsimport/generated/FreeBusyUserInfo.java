
package com.zimbra.soap.mail.wsimport.generated;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for freeBusyUserInfo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="freeBusyUserInfo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;choice maxOccurs="unbounded" minOccurs="0">
 *           &lt;element name="f" type="{urn:zimbraMail}freeBusyFREEslot"/>
 *           &lt;element name="b" type="{urn:zimbraMail}freeBusyBUSYslot"/>
 *           &lt;element name="t" type="{urn:zimbraMail}freeBusyBUSYTENTATIVEslot"/>
 *           &lt;element name="u" type="{urn:zimbraMail}freeBusyBUSYUNAVAILABLEslot"/>
 *           &lt;element name="n" type="{urn:zimbraMail}freeBusyNODATAslot"/>
 *         &lt;/choice>
 *       &lt;/sequence>
 *       &lt;attribute name="id" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "freeBusyUserInfo", propOrder = {
    "fOrBOrT"
})
public class FreeBusyUserInfo {

    @XmlElements({
        @XmlElement(name = "f", type = FreeBusyFREEslot.class),
        @XmlElement(name = "n", type = FreeBusyNODATAslot.class),
        @XmlElement(name = "b", type = FreeBusyBUSYslot.class),
        @XmlElement(name = "u", type = FreeBusyBUSYUNAVAILABLEslot.class),
        @XmlElement(name = "t", type = FreeBusyBUSYTENTATIVEslot.class)
    })
    protected List<FreeBusySlot> fOrBOrT;
    @XmlAttribute(required = true)
    protected String id;

    /**
     * Gets the value of the fOrBOrT property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the fOrBOrT property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getFOrBOrT().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link FreeBusyFREEslot }
     * {@link FreeBusyNODATAslot }
     * {@link FreeBusyBUSYslot }
     * {@link FreeBusyBUSYUNAVAILABLEslot }
     * {@link FreeBusyBUSYTENTATIVEslot }
     * 
     * 
     */
    public List<FreeBusySlot> getFOrBOrT() {
        if (fOrBOrT == null) {
            fOrBOrT = new ArrayList<FreeBusySlot>();
        }
        return this.fOrBOrT;
    }

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setId(String value) {
        this.id = value;
    }

}
