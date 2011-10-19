
package generated.zcsclient.mail;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for getFolderRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getFolderRequest">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="folder" type="{urn:zimbraMail}getFolderSpec" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="visible" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="needGranteeName" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="view" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="depth" type="{http://www.w3.org/2001/XMLSchema}int" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getFolderRequest", propOrder = {
    "folder"
})
public class testGetFolderRequest {

    protected testGetFolderSpec folder;
    @XmlAttribute(name = "visible")
    protected Boolean visible;
    @XmlAttribute(name = "needGranteeName", required = true)
    protected boolean needGranteeName;
    @XmlAttribute(name = "view")
    protected String view;
    @XmlAttribute(name = "depth")
    protected Integer depth;

    /**
     * Gets the value of the folder property.
     * 
     * @return
     *     possible object is
     *     {@link testGetFolderSpec }
     *     
     */
    public testGetFolderSpec getFolder() {
        return folder;
    }

    /**
     * Sets the value of the folder property.
     * 
     * @param value
     *     allowed object is
     *     {@link testGetFolderSpec }
     *     
     */
    public void setFolder(testGetFolderSpec value) {
        this.folder = value;
    }

    /**
     * Gets the value of the visible property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isVisible() {
        return visible;
    }

    /**
     * Sets the value of the visible property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setVisible(Boolean value) {
        this.visible = value;
    }

    /**
     * Gets the value of the needGranteeName property.
     * 
     */
    public boolean isNeedGranteeName() {
        return needGranteeName;
    }

    /**
     * Sets the value of the needGranteeName property.
     * 
     */
    public void setNeedGranteeName(boolean value) {
        this.needGranteeName = value;
    }

    /**
     * Gets the value of the view property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getView() {
        return view;
    }

    /**
     * Sets the value of the view property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setView(String value) {
        this.view = value;
    }

    /**
     * Gets the value of the depth property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getDepth() {
        return depth;
    }

    /**
     * Sets the value of the depth property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setDepth(Integer value) {
        this.depth = value;
    }

}
