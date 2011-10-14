
package zimbra.generated.adminclient.account;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for getWhiteBlackListResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getWhiteBlackListResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="whiteList">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="addr" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="blackList">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="addr" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getWhiteBlackListResponse", propOrder = {
    "whiteList",
    "blackList"
})
public class testGetWhiteBlackListResponse {

    @XmlElement(required = true)
    protected testGetWhiteBlackListResponse.WhiteList whiteList;
    @XmlElement(required = true)
    protected testGetWhiteBlackListResponse.BlackList blackList;

    /**
     * Gets the value of the whiteList property.
     * 
     * @return
     *     possible object is
     *     {@link testGetWhiteBlackListResponse.WhiteList }
     *     
     */
    public testGetWhiteBlackListResponse.WhiteList getWhiteList() {
        return whiteList;
    }

    /**
     * Sets the value of the whiteList property.
     * 
     * @param value
     *     allowed object is
     *     {@link testGetWhiteBlackListResponse.WhiteList }
     *     
     */
    public void setWhiteList(testGetWhiteBlackListResponse.WhiteList value) {
        this.whiteList = value;
    }

    /**
     * Gets the value of the blackList property.
     * 
     * @return
     *     possible object is
     *     {@link testGetWhiteBlackListResponse.BlackList }
     *     
     */
    public testGetWhiteBlackListResponse.BlackList getBlackList() {
        return blackList;
    }

    /**
     * Sets the value of the blackList property.
     * 
     * @param value
     *     allowed object is
     *     {@link testGetWhiteBlackListResponse.BlackList }
     *     
     */
    public void setBlackList(testGetWhiteBlackListResponse.BlackList value) {
        this.blackList = value;
    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="addr" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "addr"
    })
    public static class BlackList {

        protected List<String> addr;

        /**
         * Gets the value of the addr property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the addr property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getAddr().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link String }
         * 
         * 
         */
        public List<String> getAddr() {
            if (addr == null) {
                addr = new ArrayList<String>();
            }
            return this.addr;
        }

    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="addr" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "addr"
    })
    public static class WhiteList {

        protected List<String> addr;

        /**
         * Gets the value of the addr property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the addr property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getAddr().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link String }
         * 
         * 
         */
        public List<String> getAddr() {
            if (addr == null) {
                addr = new ArrayList<String>();
            }
            return this.addr;
        }

    }

}
