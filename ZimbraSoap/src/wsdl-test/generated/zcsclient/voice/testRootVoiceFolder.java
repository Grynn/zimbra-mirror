
package generated.zcsclient.voice;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for rootVoiceFolder complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="rootVoiceFolder">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:zimbraVoice}voiceFolder">
 *       &lt;sequence>
 *         &lt;element name="folder" type="{urn:zimbraVoice}voiceFolder" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "rootVoiceFolder", propOrder = {
    "folder"
})
public class testRootVoiceFolder
    extends testVoiceFolder
{

    protected List<testVoiceFolder> folder;

    /**
     * Gets the value of the folder property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the folder property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getFolder().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link testVoiceFolder }
     * 
     * 
     */
    public List<testVoiceFolder> getFolder() {
        if (folder == null) {
            folder = new ArrayList<testVoiceFolder>();
        }
        return this.folder;
    }

}
