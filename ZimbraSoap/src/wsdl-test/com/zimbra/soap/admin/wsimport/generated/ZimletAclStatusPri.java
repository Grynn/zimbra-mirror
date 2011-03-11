
package com.zimbra.soap.admin.wsimport.generated;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for zimletAclStatusPri complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="zimletAclStatusPri">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="acl" type="{urn:zimbraAdmin}zimletAcl" minOccurs="0"/>
 *         &lt;element name="status" type="{urn:zimbraAdmin}valueAttrib" minOccurs="0"/>
 *         &lt;element name="priority" type="{urn:zimbraAdmin}integerValueAttrib" minOccurs="0"/>
 *       &lt;/all>
 *       &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "zimletAclStatusPri", propOrder = {

})
public class ZimletAclStatusPri {

    protected ZimletAcl acl;
    protected ValueAttrib status;
    protected IntegerValueAttrib priority;
    @XmlAttribute(required = true)
    protected String name;

    /**
     * Gets the value of the acl property.
     * 
     * @return
     *     possible object is
     *     {@link ZimletAcl }
     *     
     */
    public ZimletAcl getAcl() {
        return acl;
    }

    /**
     * Sets the value of the acl property.
     * 
     * @param value
     *     allowed object is
     *     {@link ZimletAcl }
     *     
     */
    public void setAcl(ZimletAcl value) {
        this.acl = value;
    }

    /**
     * Gets the value of the status property.
     * 
     * @return
     *     possible object is
     *     {@link ValueAttrib }
     *     
     */
    public ValueAttrib getStatus() {
        return status;
    }

    /**
     * Sets the value of the status property.
     * 
     * @param value
     *     allowed object is
     *     {@link ValueAttrib }
     *     
     */
    public void setStatus(ValueAttrib value) {
        this.status = value;
    }

    /**
     * Gets the value of the priority property.
     * 
     * @return
     *     possible object is
     *     {@link IntegerValueAttrib }
     *     
     */
    public IntegerValueAttrib getPriority() {
        return priority;
    }

    /**
     * Sets the value of the priority property.
     * 
     * @param value
     *     allowed object is
     *     {@link IntegerValueAttrib }
     *     
     */
    public void setPriority(IntegerValueAttrib value) {
        this.priority = value;
    }

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

}
