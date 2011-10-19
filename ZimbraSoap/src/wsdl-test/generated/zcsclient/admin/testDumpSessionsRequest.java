
package generated.zcsclient.admin;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for dumpSessionsRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="dumpSessionsRequest">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *       &lt;/sequence>
 *       &lt;attribute name="listSessions" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="groupByAccount" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "dumpSessionsRequest")
public class testDumpSessionsRequest {

    @XmlAttribute(name = "listSessions")
    protected Boolean listSessions;
    @XmlAttribute(name = "groupByAccount")
    protected Boolean groupByAccount;

    /**
     * Gets the value of the listSessions property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isListSessions() {
        return listSessions;
    }

    /**
     * Sets the value of the listSessions property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setListSessions(Boolean value) {
        this.listSessions = value;
    }

    /**
     * Gets the value of the groupByAccount property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isGroupByAccount() {
        return groupByAccount;
    }

    /**
     * Sets the value of the groupByAccount property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setGroupByAccount(Boolean value) {
        this.groupByAccount = value;
    }

}
