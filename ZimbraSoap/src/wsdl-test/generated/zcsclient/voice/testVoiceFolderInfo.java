
package generated.zcsclient.voice;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for voiceFolderInfo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="voiceFolderInfo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="folder" type="{urn:zimbraVoice}rootVoiceFolder"/>
 *       &lt;/sequence>
 *       &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="vm" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "voiceFolderInfo", propOrder = {
    "folder"
})
public class testVoiceFolderInfo {

    @XmlElement(required = true)
    protected testRootVoiceFolder folder;
    @XmlAttribute(name = "name", required = true)
    protected String name;
    @XmlAttribute(name = "vm", required = true)
    protected boolean vm;

    /**
     * Gets the value of the folder property.
     * 
     * @return
     *     possible object is
     *     {@link testRootVoiceFolder }
     *     
     */
    public testRootVoiceFolder getFolder() {
        return folder;
    }

    /**
     * Sets the value of the folder property.
     * 
     * @param value
     *     allowed object is
     *     {@link testRootVoiceFolder }
     *     
     */
    public void setFolder(testRootVoiceFolder value) {
        this.folder = value;
    }

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the vm property.
     * 
     */
    public boolean isVm() {
        return vm;
    }

    /**
     * Sets the value of the vm property.
     * 
     */
    public void setVm(boolean value) {
        this.vm = value;
    }

}
