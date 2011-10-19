
package generated.zcsclient.admin;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for getAllAccountLoggersResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getAllAccountLoggersResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="accountLogger" type="{urn:zimbraAdmin}accountLoggerInfo" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getAllAccountLoggersResponse", propOrder = {
    "accountLogger"
})
public class testGetAllAccountLoggersResponse {

    protected List<testAccountLoggerInfo> accountLogger;

    /**
     * Gets the value of the accountLogger property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the accountLogger property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAccountLogger().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link testAccountLoggerInfo }
     * 
     * 
     */
    public List<testAccountLoggerInfo> getAccountLogger() {
        if (accountLogger == null) {
            accountLogger = new ArrayList<testAccountLoggerInfo>();
        }
        return this.accountLogger;
    }

}
