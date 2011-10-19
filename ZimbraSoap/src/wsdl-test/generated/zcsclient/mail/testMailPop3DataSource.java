
package generated.zcsclient.mail;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for mailPop3DataSource complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="mailPop3DataSource">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:zimbraMail}mailDataSource">
 *       &lt;sequence>
 *       &lt;/sequence>
 *       &lt;attribute name="leaveOnServer" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "mailPop3DataSource")
public class testMailPop3DataSource
    extends testMailDataSource
{

    @XmlAttribute(name = "leaveOnServer")
    protected Boolean leaveOnServer;

    /**
     * Gets the value of the leaveOnServer property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isLeaveOnServer() {
        return leaveOnServer;
    }

    /**
     * Sets the value of the leaveOnServer property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setLeaveOnServer(Boolean value) {
        this.leaveOnServer = value;
    }

}
