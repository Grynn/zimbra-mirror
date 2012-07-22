
package generated.zcsclient.admin;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for searchDirectoryResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="searchDirectoryResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;choice maxOccurs="unbounded" minOccurs="0">
 *           &lt;element name="calresource" type="{urn:zimbraAdmin}calendarResourceInfo"/>
 *           &lt;element ref="{urn:zimbraAdmin}dl"/>
 *           &lt;element ref="{urn:zimbraAdmin}alias"/>
 *           &lt;element name="account" type="{urn:zimbraAdmin}accountInfo"/>
 *           &lt;element ref="{urn:zimbraAdmin}domain"/>
 *           &lt;element name="cos" type="{urn:zimbraAdmin}cosInfo"/>
 *         &lt;/choice>
 *       &lt;/sequence>
 *       &lt;attribute name="num" type="{http://www.w3.org/2001/XMLSchema}long" />
 *       &lt;attribute name="more" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="searchTotal" type="{http://www.w3.org/2001/XMLSchema}long" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "searchDirectoryResponse", propOrder = {
    "calresourceOrDlOrAlias"
})
public class testSearchDirectoryResponse {

    @XmlElements({
        @XmlElement(name = "dl", type = testDistributionListInfo.class),
        @XmlElement(name = "cos", type = testCosInfo.class),
        @XmlElement(name = "domain", type = testDomainInfo.class),
        @XmlElement(name = "calresource", type = testCalendarResourceInfo.class),
        @XmlElement(name = "account", type = testAccountInfo.class),
        @XmlElement(name = "alias", type = testAliasInfo.class)
    })
    protected List<Object> calresourceOrDlOrAlias;
    @XmlAttribute(name = "num")
    protected Long num;
    @XmlAttribute(name = "more")
    protected Boolean more;
    @XmlAttribute(name = "searchTotal")
    protected Long searchTotal;

    /**
     * Gets the value of the calresourceOrDlOrAlias property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the calresourceOrDlOrAlias property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCalresourceOrDlOrAlias().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link testDistributionListInfo }
     * {@link testCosInfo }
     * {@link testDomainInfo }
     * {@link testCalendarResourceInfo }
     * {@link testAccountInfo }
     * {@link testAliasInfo }
     * 
     * 
     */
    public List<Object> getCalresourceOrDlOrAlias() {
        if (calresourceOrDlOrAlias == null) {
            calresourceOrDlOrAlias = new ArrayList<Object>();
        }
        return this.calresourceOrDlOrAlias;
    }

    /**
     * Gets the value of the num property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getNum() {
        return num;
    }

    /**
     * Sets the value of the num property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setNum(Long value) {
        this.num = value;
    }

    /**
     * Gets the value of the more property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isMore() {
        return more;
    }

    /**
     * Sets the value of the more property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setMore(Boolean value) {
        this.more = value;
    }

    /**
     * Gets the value of the searchTotal property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getSearchTotal() {
        return searchTotal;
    }

    /**
     * Sets the value of the searchTotal property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setSearchTotal(Long value) {
        this.searchTotal = value;
    }

}
