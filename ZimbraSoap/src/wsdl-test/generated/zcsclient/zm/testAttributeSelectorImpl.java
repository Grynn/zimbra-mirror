
package generated.zcsclient.zm;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import generated.zcsclient.admin.testGetAccountRequest;
import generated.zcsclient.admin.testGetCalendarResourceRequest;
import generated.zcsclient.admin.testGetCosRequest;
import generated.zcsclient.admin.testGetDomainRequest;
import generated.zcsclient.admin.testGetServerRequest;
import generated.zcsclient.admin.testGetXMPPComponentRequest;
import generated.zcsclient.admin.testGetZimletRequest;
import generated.zcsclient.admin.testSearchAutoProvDirectoryRequest;
import generated.zcsclient.admin.testSearchDirectoryRequest;


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
    generated.zcsclient.account.testSearchCalendarResourcesRequest.class,
    testGetCosRequest.class,
    testSearchDirectoryRequest.class,
    testGetServerRequest.class,
    testGetXMPPComponentRequest.class,
    testGetCalendarResourceRequest.class,
    testGetAccountRequest.class,
    testSearchAutoProvDirectoryRequest.class,
    testGetDomainRequest.class,
    testGetZimletRequest.class,
    generated.zcsclient.admin.testSearchCalendarResourcesRequest.class
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
