
package com.zimbra.soap.admin.wsimport.generated;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for migrateAccountRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="migrateAccountRequest">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="migrate" type="{urn:zimbraAdmin}idAndAction"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "migrateAccountRequest", propOrder = {
    "migrate"
})
public class MigrateAccountRequest {

    @XmlElement(required = true)
    protected IdAndAction migrate;

    /**
     * Gets the value of the migrate property.
     * 
     * @return
     *     possible object is
     *     {@link IdAndAction }
     *     
     */
    public IdAndAction getMigrate() {
        return migrate;
    }

    /**
     * Sets the value of the migrate property.
     * 
     * @param value
     *     allowed object is
     *     {@link IdAndAction }
     *     
     */
    public void setMigrate(IdAndAction value) {
        this.migrate = value;
    }

}
