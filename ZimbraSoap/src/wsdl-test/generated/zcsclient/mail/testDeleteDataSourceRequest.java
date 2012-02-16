
package generated.zcsclient.mail;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for deleteDataSourceRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="deleteDataSourceRequest">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;choice maxOccurs="unbounded" minOccurs="0">
 *           &lt;element name="imap" type="{urn:zimbraMail}imapDataSourceNameOrId"/>
 *           &lt;element name="pop3" type="{urn:zimbraMail}pop3DataSourceNameOrId"/>
 *           &lt;element name="caldav" type="{urn:zimbraMail}caldavDataSourceNameOrId"/>
 *           &lt;element name="yab" type="{urn:zimbraMail}yabDataSourceNameOrId"/>
 *           &lt;element name="rss" type="{urn:zimbraMail}rssDataSourceNameOrId"/>
 *           &lt;element name="gal" type="{urn:zimbraMail}galDataSourceNameOrId"/>
 *           &lt;element name="cal" type="{urn:zimbraMail}calDataSourceNameOrId"/>
 *           &lt;element name="unknown" type="{urn:zimbraMail}unknownDataSourceNameOrId"/>
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
@XmlType(name = "deleteDataSourceRequest", propOrder = {
    "imapOrPop3OrCaldav"
})
public class testDeleteDataSourceRequest {

    @XmlElements({
        @XmlElement(name = "gal", type = testGalDataSourceNameOrId.class),
        @XmlElement(name = "imap", type = testImapDataSourceNameOrId.class),
        @XmlElement(name = "unknown", type = testUnknownDataSourceNameOrId.class),
        @XmlElement(name = "rss", type = testRssDataSourceNameOrId.class),
        @XmlElement(name = "yab", type = testYabDataSourceNameOrId.class),
        @XmlElement(name = "caldav", type = testCaldavDataSourceNameOrId.class),
        @XmlElement(name = "cal", type = testCalDataSourceNameOrId.class),
        @XmlElement(name = "pop3", type = testPop3DataSourceNameOrId.class)
    })
    protected List<testDataSourceNameOrId> imapOrPop3OrCaldav;

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
     * {@link testGalDataSourceNameOrId }
     * {@link testImapDataSourceNameOrId }
     * {@link testUnknownDataSourceNameOrId }
     * {@link testRssDataSourceNameOrId }
     * {@link testYabDataSourceNameOrId }
     * {@link testCaldavDataSourceNameOrId }
     * {@link testCalDataSourceNameOrId }
     * {@link testPop3DataSourceNameOrId }
     * 
     * 
     */
    public List<testDataSourceNameOrId> getImapOrPop3OrCaldav() {
        if (imapOrPop3OrCaldav == null) {
            imapOrPop3OrCaldav = new ArrayList<testDataSourceNameOrId>();
        }
        return this.imapOrPop3OrCaldav;
    }

}
