
package com.zimbra.soap.admin.wsimport.generated;

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
    DeleteDataSourceRequest.class,
    ModifyDomainRequest.class,
    CreateDomainRequest.class,
    CheckRightRequest.class,
    CreateCalendarResourceRequest.class,
    ModifyCosRequest.class,
    ModifyDataSourceRequest.class,
    CreateCosRequest.class,
    CheckAuthConfigRequest.class,
    GetDataSourcesRequest.class,
    CheckGalConfigRequest.class,
    CreateAccountRequest.class,
    GetAllConfigResponse.class,
    CreateDistributionListRequest.class,
    ModifyConfigRequest.class,
    CreateGalSyncAccountRequest.class,
    GetConfigResponse.class,
    ModifyDistributionListRequest.class,
    CreateServerRequest.class,
    GetDistributionListRequest.class,
    ModifyCalendarResourceRequest.class,
    ModifyServerRequest.class,
    ModifyAccountRequest.class,
    DataSourceSpecifier.class,
    DataSourceInfo.class,
    GalContactInfo.class,
    NetworkInformation.class
})
public abstract class AdminAttrsImpl {

    protected List<Attr> a;

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
     * {@link Attr }
     * 
     * 
     */
    public List<Attr> getA() {
        if (a == null) {
            a = new ArrayList<Attr>();
        }
        return this.a;
    }

}
