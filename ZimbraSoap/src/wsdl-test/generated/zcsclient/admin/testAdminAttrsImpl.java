
package generated.zcsclient.admin;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for adminAttrsImpl complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="adminAttrsImpl">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{urn:zimbraAdmin}a" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "adminAttrsImpl", propOrder = {
    "a"
})
@XmlSeeAlso({
    testCreateZimletRequest.class,
    testCreateCalendarResourceRequest.class,
    testModifyDataSourceRequest.class,
    testCreateCosRequest.class,
    testCreateLDAPEntryRequest.class,
    testCheckAuthConfigRequest.class,
    testCreateAccountRequest.class,
    testCreateDistributionListRequest.class,
    testCreateServerRequest.class,
    testModifyCalendarResourceRequest.class,
    testModifyAccountRequest.class,
    testDeleteDataSourceRequest.class,
    testModifyDomainRequest.class,
    testCreateDomainRequest.class,
    testCheckRightRequest.class,
    testModifyCosRequest.class,
    testAddGalSyncDataSourceRequest.class,
    testModifyLDAPEntryRequest.class,
    testGetDataSourcesRequest.class,
    testCheckGalConfigRequest.class,
    testGetAllConfigResponse.class,
    testModifyConfigRequest.class,
    testCreateGalSyncAccountRequest.class,
    testGetConfigResponse.class,
    testModifyDistributionListRequest.class,
    testGetDistributionListRequest.class,
    testModifyServerRequest.class,
    testSmimeConfigModifications.class,
    testArchiveSpec.class,
    testDataSourceSpecifier.class,
    testLdapEntryInfo.class,
    testSearchNode.class,
    testSmimeConfigInfo.class,
    testDataSourceInfo.class,
    testXmppComponentInfo.class,
    testGalContactInfo.class,
    testXmppComponentSpec.class,
    testNetworkInformation.class
})
public class testAdminAttrsImpl {

    protected List<testAttr> a;

    /**
     * Gets the value of the a property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the a property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getA().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link testAttr }
     * 
     * 
     */
    public List<testAttr> getA() {
        if (a == null) {
            a = new ArrayList<testAttr>();
        }
        return this.a;
    }

}
