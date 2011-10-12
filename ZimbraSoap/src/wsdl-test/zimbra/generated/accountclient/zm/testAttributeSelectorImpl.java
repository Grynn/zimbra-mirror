
package zimbra.generated.accountclient.zm;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import zimbra.generated.accountclient.admin.testGetAccountRequest;
import zimbra.generated.accountclient.admin.testGetCalendarResourceRequest;
import zimbra.generated.accountclient.admin.testGetCosRequest;
import zimbra.generated.accountclient.admin.testGetDomainRequest;
import zimbra.generated.accountclient.admin.testGetServerRequest;
import zimbra.generated.accountclient.admin.testGetXMPPComponentRequest;
import zimbra.generated.accountclient.admin.testGetZimletRequest;
import zimbra.generated.accountclient.admin.testSearchAutoProvDirectoryRequest;
import zimbra.generated.accountclient.admin.testSearchDirectoryRequest;


/**
 * <p>Java class for attributeSelectorImpl complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="attributeSelectorImpl">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *       &lt;/sequence>
 *       &lt;attribute name="attrs" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "attributeSelectorImpl")
@XmlSeeAlso({
    zimbra.generated.accountclient.account.testSearchCalendarResourcesRequest.class,
    testGetCosRequest.class,
    testSearchDirectoryRequest.class,
    testGetServerRequest.class,
    testGetXMPPComponentRequest.class,
    testGetCalendarResourceRequest.class,
    testGetAccountRequest.class,
    testSearchAutoProvDirectoryRequest.class,
    testGetDomainRequest.class,
    testGetZimletRequest.class,
    zimbra.generated.accountclient.admin.testSearchCalendarResourcesRequest.class
})
public abstract class testAttributeSelectorImpl {

    @XmlAttribute(name = "attrs")
    protected String attrs;

    /**
     * Gets the value of the attrs property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAttrs() {
        return attrs;
    }

    /**
     * Sets the value of the attrs property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAttrs(String value) {
        this.attrs = value;
    }

}
