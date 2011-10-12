
package zimbra.generated.adminclient.zm;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import zimbra.generated.adminclient.admin.testGetAccountRequest;
import zimbra.generated.adminclient.admin.testGetCalendarResourceRequest;
import zimbra.generated.adminclient.admin.testGetCosRequest;
import zimbra.generated.adminclient.admin.testGetDomainRequest;
import zimbra.generated.adminclient.admin.testGetServerRequest;
import zimbra.generated.adminclient.admin.testGetXMPPComponentRequest;
import zimbra.generated.adminclient.admin.testGetZimletRequest;
import zimbra.generated.adminclient.admin.testSearchAutoProvDirectoryRequest;
import zimbra.generated.adminclient.admin.testSearchDirectoryRequest;


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
    testGetCosRequest.class,
    testSearchDirectoryRequest.class,
    testGetServerRequest.class,
    testGetXMPPComponentRequest.class,
    testGetCalendarResourceRequest.class,
    testGetAccountRequest.class,
    testSearchAutoProvDirectoryRequest.class,
    testGetDomainRequest.class,
    testGetZimletRequest.class,
    zimbra.generated.adminclient.admin.testSearchCalendarResourcesRequest.class,
    zimbra.generated.adminclient.account.testSearchCalendarResourcesRequest.class
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
