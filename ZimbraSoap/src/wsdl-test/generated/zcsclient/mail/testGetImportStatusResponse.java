
package generated.zcsclient.mail;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for getImportStatusResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getImportStatusResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;choice maxOccurs="unbounded" minOccurs="0">
 *           &lt;element name="imap" type="{urn:zimbraMail}imapImportStatusInfo"/>
 *           &lt;element name="pop3" type="{urn:zimbraMail}pop3ImportStatusInfo"/>
 *           &lt;element name="caldav" type="{urn:zimbraMail}caldavImportStatusInfo"/>
 *           &lt;element name="yab" type="{urn:zimbraMail}yabImportStatusInfo"/>
 *           &lt;element name="rss" type="{urn:zimbraMail}rssImportStatusInfo"/>
 *           &lt;element name="gal" type="{urn:zimbraMail}galImportStatusInfo"/>
 *           &lt;element name="cal" type="{urn:zimbraMail}calImportStatusInfo"/>
 *           &lt;element name="unknown" type="{urn:zimbraMail}unknownImportStatusInfo"/>
 *         &lt;/choice>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getImportStatusResponse", propOrder = {
    "imapOrPop3OrCaldav"
})
public class testGetImportStatusResponse {

    @XmlElements({
        @XmlElement(name = "yab", type = testYabImportStatusInfo.class),
        @XmlElement(name = "caldav", type = testCaldavImportStatusInfo.class),
        @XmlElement(name = "pop3", type = testPop3ImportStatusInfo.class),
        @XmlElement(name = "imap", type = testImapImportStatusInfo.class),
        @XmlElement(name = "cal", type = testCalImportStatusInfo.class),
        @XmlElement(name = "gal", type = testGalImportStatusInfo.class),
        @XmlElement(name = "rss", type = testRssImportStatusInfo.class),
        @XmlElement(name = "unknown", type = testUnknownImportStatusInfo.class)
    })
    protected List<testImportStatusInfo> imapOrPop3OrCaldav;

    /**
     * Gets the value of the imapOrPop3OrCaldav property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the imapOrPop3OrCaldav property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getImapOrPop3OrCaldav().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link testYabImportStatusInfo }
     * {@link testCaldavImportStatusInfo }
     * {@link testPop3ImportStatusInfo }
     * {@link testImapImportStatusInfo }
     * {@link testCalImportStatusInfo }
     * {@link testGalImportStatusInfo }
     * {@link testRssImportStatusInfo }
     * {@link testUnknownImportStatusInfo }
     * 
     * 
     */
    public List<testImportStatusInfo> getImapOrPop3OrCaldav() {
        if (imapOrPop3OrCaldav == null) {
            imapOrPop3OrCaldav = new ArrayList<testImportStatusInfo>();
        }
        return this.imapOrPop3OrCaldav;
    }

}
