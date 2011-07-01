
package com.zimbra.soap.admin.wsimport.generated;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for tzFixupRule complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="tzFixupRule">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="match" type="{urn:zimbraAdmin}tzFixupRuleMatch"/>
 *         &lt;element name="touch" type="{urn:zimbraAdmin}simpleElement" minOccurs="0"/>
 *         &lt;element name="replace" type="{urn:zimbraAdmin}tzReplaceInfo" minOccurs="0"/>
 *       &lt;/all>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tzFixupRule", propOrder = {

})
public class TzFixupRule {

    @XmlElement(required = true)
    protected TzFixupRuleMatch match;
    protected SimpleElement touch;
    protected TzReplaceInfo replace;

    /**
     * Gets the value of the match property.
     * 
     * @return
     *     possible object is
     *     {@link TzFixupRuleMatch }
     *     
     */
    public TzFixupRuleMatch getMatch() {
        return match;
    }

    /**
     * Sets the value of the match property.
     * 
     * @param value
     *     allowed object is
     *     {@link TzFixupRuleMatch }
     *     
     */
    public void setMatch(TzFixupRuleMatch value) {
        this.match = value;
    }

    /**
     * Gets the value of the touch property.
     * 
     * @return
     *     possible object is
     *     {@link SimpleElement }
     *     
     */
    public SimpleElement getTouch() {
        return touch;
    }

    /**
     * Sets the value of the touch property.
     * 
     * @param value
     *     allowed object is
     *     {@link SimpleElement }
     *     
     */
    public void setTouch(SimpleElement value) {
        this.touch = value;
    }

    /**
     * Gets the value of the replace property.
     * 
     * @return
     *     possible object is
     *     {@link TzReplaceInfo }
     *     
     */
    public TzReplaceInfo getReplace() {
        return replace;
    }

    /**
     * Sets the value of the replace property.
     * 
     * @param value
     *     allowed object is
     *     {@link TzReplaceInfo }
     *     
     */
    public void setReplace(TzReplaceInfo value) {
        this.replace = value;
    }

}
