
package generated.zcsclient.mail;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for checkPermissionResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="checkPermissionResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="right" type="{urn:zimbraMail}rightPermission" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="allow" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "checkPermissionResponse", propOrder = {
    "right"
})
public class testCheckPermissionResponse {

    protected List<testRightPermission> right;
    @XmlAttribute(name = "allow", required = true)
    protected boolean allow;

    /**
     * Gets the value of the right property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the right property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRight().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link testRightPermission }
     * 
     * 
     */
    public List<testRightPermission> getRight() {
        if (right == null) {
            right = new ArrayList<testRightPermission>();
        }
        return this.right;
    }

    /**
     * Gets the value of the allow property.
     * 
     */
    public boolean isAllow() {
        return allow;
    }

    /**
     * Sets the value of the allow property.
     * 
     */
    public void setAllow(boolean value) {
        this.allow = value;
    }

}
