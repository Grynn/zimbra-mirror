
package generated.zcsclient.account;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;


/**
 * <p>Java class for distributionListSubscribeReq complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="distributionListSubscribeReq">
 *   &lt;simpleContent>
 *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
 *       &lt;attribute name="op" use="required" type="{urn:zimbraAccount}distributionListSubscribeOp" />
 *       &lt;attribute name="bccOwners" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *     &lt;/extension>
 *   &lt;/simpleContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "distributionListSubscribeReq", propOrder = {
    "value"
})
public class testDistributionListSubscribeReq {

    @XmlValue
    protected String value;
    @XmlAttribute(name = "op", required = true)
    protected testDistributionListSubscribeOp op;
    @XmlAttribute(name = "bccOwners", required = true)
    protected boolean bccOwners;

    /**
     * Gets the value of the value property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets the value of the value property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Gets the value of the op property.
     * 
     * @return
     *     possible object is
     *     {@link testDistributionListSubscribeOp }
     *     
     */
    public testDistributionListSubscribeOp getOp() {
        return op;
    }

    /**
     * Sets the value of the op property.
     * 
     * @param value
     *     allowed object is
     *     {@link testDistributionListSubscribeOp }
     *     
     */
    public void setOp(testDistributionListSubscribeOp value) {
        this.op = value;
    }

    /**
     * Gets the value of the bccOwners property.
     * 
     */
    public boolean isBccOwners() {
        return bccOwners;
    }

    /**
     * Sets the value of the bccOwners property.
     * 
     */
    public void setBccOwners(boolean value) {
        this.bccOwners = value;
    }

}
