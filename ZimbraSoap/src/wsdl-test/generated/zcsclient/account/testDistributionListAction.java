
package generated.zcsclient.account;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for distributionListAction complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="distributionListAction">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:zimbraAccount}accountKeyValuePairs">
 *       &lt;sequence>
 *         &lt;element name="dlm" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="alias" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="newName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="owner" type="{urn:zimbraAccount}distributionListGranteeSelector" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="right" type="{urn:zimbraAccount}distributionListRightSpec" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{urn:zimbraAccount}subsReq" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="op" use="required" type="{urn:zimbraAccount}operation" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "distributionListAction", propOrder = {
    "dlm",
    "alias",
    "newName",
    "owner",
    "right",
    "subsReq"
})
public class testDistributionListAction
    extends testAccountKeyValuePairs
{

    protected List<String> dlm;
    protected String alias;
    protected String newName;
    protected List<testDistributionListGranteeSelector> owner;
    protected List<testDistributionListRightSpec> right;
    protected testDistributionListSubscribeReq subsReq;
    @XmlAttribute(name = "op", required = true)
    protected testOperation op;

    /**
     * Gets the value of the dlm property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the dlm property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDlm().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getDlm() {
        if (dlm == null) {
            dlm = new ArrayList<String>();
        }
        return this.dlm;
    }

    /**
     * Gets the value of the alias property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAlias() {
        return alias;
    }

    /**
     * Sets the value of the alias property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAlias(String value) {
        this.alias = value;
    }

    /**
     * Gets the value of the newName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNewName() {
        return newName;
    }

    /**
     * Sets the value of the newName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNewName(String value) {
        this.newName = value;
    }

    /**
     * Gets the value of the owner property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the owner property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getOwner().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link testDistributionListGranteeSelector }
     * 
     * 
     */
    public List<testDistributionListGranteeSelector> getOwner() {
        if (owner == null) {
            owner = new ArrayList<testDistributionListGranteeSelector>();
        }
        return this.owner;
    }

    /**
     * Gets the value of the right property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the right property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRight().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link testDistributionListRightSpec }
     * 
     * 
     */
    public List<testDistributionListRightSpec> getRight() {
        if (right == null) {
            right = new ArrayList<testDistributionListRightSpec>();
        }
        return this.right;
    }

    /**
     * Gets the value of the subsReq property.
     * 
     * @return
     *     possible object is
     *     {@link testDistributionListSubscribeReq }
     *     
     */
    public testDistributionListSubscribeReq getSubsReq() {
        return subsReq;
    }

    /**
     * Sets the value of the subsReq property.
     * 
     * @param value
     *     allowed object is
     *     {@link testDistributionListSubscribeReq }
     *     
     */
    public void setSubsReq(testDistributionListSubscribeReq value) {
        this.subsReq = value;
    }

    /**
     * Gets the value of the op property.
     * 
     * @return
     *     possible object is
     *     {@link testOperation }
     *     
     */
    public testOperation getOp() {
        return op;
    }

    /**
     * Sets the value of the op property.
     * 
     * @param value
     *     allowed object is
     *     {@link testOperation }
     *     
     */
    public void setOp(testOperation value) {
        this.op = value;
    }

}
