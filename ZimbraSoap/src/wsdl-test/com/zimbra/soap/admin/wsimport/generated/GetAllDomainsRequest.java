
package com.zimbra.soap.admin.wsimport.generated;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for getAllDomainsRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getAllDomainsRequest">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *       &lt;/all>
 *       &lt;attribute name="applyConfig" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getAllDomainsRequest", propOrder = {

})
public class GetAllDomainsRequest {

    @XmlAttribute(required = true)
    protected boolean applyConfig;

    /**
     * Gets the value of the applyConfig property.
     * 
     */
    public boolean isApplyConfig() {
        return applyConfig;
    }

    /**
     * Sets the value of the applyConfig property.
     * 
     */
    public void setApplyConfig(boolean value) {
        this.applyConfig = value;
    }

}
