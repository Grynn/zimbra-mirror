
package zimbra.generated.accountclient.mail;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
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
 *         &lt;choice minOccurs="0">
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
    "imap",
    "pop3",
    "caldav",
    "yab",
    "rss",
    "gal",
    "cal",
    "unknown"
})
public class testDeleteDataSourceRequest {

    protected testImapDataSourceNameOrId imap;
    protected testPop3DataSourceNameOrId pop3;
    protected testCaldavDataSourceNameOrId caldav;
    protected testYabDataSourceNameOrId yab;
    protected testRssDataSourceNameOrId rss;
    protected testGalDataSourceNameOrId gal;
    protected testCalDataSourceNameOrId cal;
    protected testUnknownDataSourceNameOrId unknown;

    /**
     * Gets the value of the imap property.
     * 
     * @return
     *     possible object is
     *     {@link testImapDataSourceNameOrId }
     *     
     */
    public testImapDataSourceNameOrId getImap() {
        return imap;
    }

    /**
     * Sets the value of the imap property.
     * 
     * @param value
     *     allowed object is
     *     {@link testImapDataSourceNameOrId }
     *     
     */
    public void setImap(testImapDataSourceNameOrId value) {
        this.imap = value;
    }

    /**
     * Gets the value of the pop3 property.
     * 
     * @return
     *     possible object is
     *     {@link testPop3DataSourceNameOrId }
     *     
     */
    public testPop3DataSourceNameOrId getPop3() {
        return pop3;
    }

    /**
     * Sets the value of the pop3 property.
     * 
     * @param value
     *     allowed object is
     *     {@link testPop3DataSourceNameOrId }
     *     
     */
    public void setPop3(testPop3DataSourceNameOrId value) {
        this.pop3 = value;
    }

    /**
     * Gets the value of the caldav property.
     * 
     * @return
     *     possible object is
     *     {@link testCaldavDataSourceNameOrId }
     *     
     */
    public testCaldavDataSourceNameOrId getCaldav() {
        return caldav;
    }

    /**
     * Sets the value of the caldav property.
     * 
     * @param value
     *     allowed object is
     *     {@link testCaldavDataSourceNameOrId }
     *     
     */
    public void setCaldav(testCaldavDataSourceNameOrId value) {
        this.caldav = value;
    }

    /**
     * Gets the value of the yab property.
     * 
     * @return
     *     possible object is
     *     {@link testYabDataSourceNameOrId }
     *     
     */
    public testYabDataSourceNameOrId getYab() {
        return yab;
    }

    /**
     * Sets the value of the yab property.
     * 
     * @param value
     *     allowed object is
     *     {@link testYabDataSourceNameOrId }
     *     
     */
    public void setYab(testYabDataSourceNameOrId value) {
        this.yab = value;
    }

    /**
     * Gets the value of the rss property.
     * 
     * @return
     *     possible object is
     *     {@link testRssDataSourceNameOrId }
     *     
     */
    public testRssDataSourceNameOrId getRss() {
        return rss;
    }

    /**
     * Sets the value of the rss property.
     * 
     * @param value
     *     allowed object is
     *     {@link testRssDataSourceNameOrId }
     *     
     */
    public void setRss(testRssDataSourceNameOrId value) {
        this.rss = value;
    }

    /**
     * Gets the value of the gal property.
     * 
     * @return
     *     possible object is
     *     {@link testGalDataSourceNameOrId }
     *     
     */
    public testGalDataSourceNameOrId getGal() {
        return gal;
    }

    /**
     * Sets the value of the gal property.
     * 
     * @param value
     *     allowed object is
     *     {@link testGalDataSourceNameOrId }
     *     
     */
    public void setGal(testGalDataSourceNameOrId value) {
        this.gal = value;
    }

    /**
     * Gets the value of the cal property.
     * 
     * @return
     *     possible object is
     *     {@link testCalDataSourceNameOrId }
     *     
     */
    public testCalDataSourceNameOrId getCal() {
        return cal;
    }

    /**
     * Sets the value of the cal property.
     * 
     * @param value
     *     allowed object is
     *     {@link testCalDataSourceNameOrId }
     *     
     */
    public void setCal(testCalDataSourceNameOrId value) {
        this.cal = value;
    }

    /**
     * Gets the value of the unknown property.
     * 
     * @return
     *     possible object is
     *     {@link testUnknownDataSourceNameOrId }
     *     
     */
    public testUnknownDataSourceNameOrId getUnknown() {
        return unknown;
    }

    /**
     * Sets the value of the unknown property.
     * 
     * @param value
     *     allowed object is
     *     {@link testUnknownDataSourceNameOrId }
     *     
     */
    public void setUnknown(testUnknownDataSourceNameOrId value) {
        this.unknown = value;
    }

}
