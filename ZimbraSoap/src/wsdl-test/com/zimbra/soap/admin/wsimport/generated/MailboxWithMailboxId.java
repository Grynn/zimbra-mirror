
package com.zimbra.soap.admin.wsimport.generated;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for mailboxWithMailboxId complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="mailboxWithMailboxId">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *       &lt;/all>
 *       &lt;attribute name="mbxid" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="s" type="{http://www.w3.org/2001/XMLSchema}long" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "mailboxWithMailboxId", propOrder = {

})
public class MailboxWithMailboxId {

    @XmlAttribute(required = true)
    protected int mbxid;
    @XmlAttribute
    protected Long s;

    /**
     * Gets the value of the mbxid property.
     * 
     */
    public int getMbxid() {
        return mbxid;
    }

    /**
     * Sets the value of the mbxid property.
     * 
     */
    public void setMbxid(int value) {
        this.mbxid = value;
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

}
