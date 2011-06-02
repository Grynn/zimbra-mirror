
package com.zimbra.soap.mail.wsimport.generated;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for rankingActionRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="rankingActionRequest">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="action" type="{urn:zimbraMail}rankingActionSpec"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "rankingActionRequest", propOrder = {
    "action"
})
public class RankingActionRequest {

    @XmlElement(required = true)
    protected RankingActionSpec action;

    /**
     * Gets the value of the action property.
     * 
     * @return
     *     possible object is
     *     {@link RankingActionSpec }
     *     
     */
    public RankingActionSpec getAction() {
        return action;
    }

    /**
     * Sets the value of the action property.
     * 
     * @param value
     *     allowed object is
     *     {@link RankingActionSpec }
     *     
     */
    public void setAction(RankingActionSpec value) {
        this.action = value;
    }

}
