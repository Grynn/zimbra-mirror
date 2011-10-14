
package zimbra.generated.accountclient.mail;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for createDataSourceResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="createDataSourceResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;choice minOccurs="0">
 *           &lt;element name="imap" type="{urn:zimbraMail}imapDataSourceId"/>
 *           &lt;element name="pop3" type="{urn:zimbraMail}pop3DataSourceId"/>
 *           &lt;element name="caldav" type="{urn:zimbraMail}caldavDataSourceId"/>
 *           &lt;element name="yab" type="{urn:zimbraMail}yabDataSourceId"/>
 *           &lt;element name="rss" type="{urn:zimbraMail}rssDataSourceId"/>
 *           &lt;element name="gal" type="{urn:zimbraMail}galDataSourceId"/>
 *           &lt;element name="cal" type="{urn:zimbraMail}calDataSourceId"/>
 *           &lt;element name="unknown" type="{urn:zimbraMail}unknownDataSourceId"/>
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
@XmlType(name = "createDataSourceResponse", propOrder = {
    "imap",
    "pop3",
    "caldav",
    "yab",
    "rss",
    "gal",
    "cal",
    "unknown"
})
public class testCreateDataSourceResponse {

    protected testImapDataSourceId imap;
    protected testPop3DataSourceId pop3;
    protected testCaldavDataSourceId caldav;
    protected testYabDataSourceId yab;
    protected testRssDataSourceId rss;
    protected testGalDataSourceId gal;
    protected testCalDataSourceId cal;
    protected testUnknownDataSourceId unknown;

    /**
     * Gets the value of the imap property.
     * 
     * @return
     *     possible object is
     *     {@link testImapDataSourceId }
     *     
     */
    public testImapDataSourceId getImap() {
        return imap;
    }

    /**
     * Sets the value of the imap property.
     * 
     * @param value
     *     allowed object is
     *     {@link testImapDataSourceId }
     *     
     */
    public void setImap(testImapDataSourceId value) {
        this.imap = value;
    }

    /**
     * Gets the value of the pop3 property.
     * 
     * @return
     *     possible object is
     *     {@link testPop3DataSourceId }
     *     
     */
    public testPop3DataSourceId getPop3() {
        return pop3;
    }

    /**
     * Sets the value of the pop3 property.
     * 
     * @param value
     *     allowed object is
     *     {@link testPop3DataSourceId }
     *     
     */
    public void setPop3(testPop3DataSourceId value) {
        this.pop3 = value;
    }

    /**
     * Gets the value of the caldav property.
     * 
     * @return
     *     possible object is
     *     {@link testCaldavDataSourceId }
     *     
     */
    public testCaldavDataSourceId getCaldav() {
        return caldav;
    }

    /**
     * Sets the value of the caldav property.
     * 
     * @param value
     *     allowed object is
     *     {@link testCaldavDataSourceId }
     *     
     */
    public void setCaldav(testCaldavDataSourceId value) {
        this.caldav = value;
    }

    /**
     * Gets the value of the yab property.
     * 
     * @return
     *     possible object is
     *     {@link testYabDataSourceId }
     *     
     */
    public testYabDataSourceId getYab() {
        return yab;
    }

    /**
     * Sets the value of the yab property.
     * 
     * @param value
     *     allowed object is
     *     {@link testYabDataSourceId }
     *     
     */
    public void setYab(testYabDataSourceId value) {
        this.yab = value;
    }

    /**
     * Gets the value of the rss property.
     * 
     * @return
     *     possible object is
     *     {@link testRssDataSourceId }
     *     
     */
    public testRssDataSourceId getRss() {
        return rss;
    }

    /**
     * Sets the value of the rss property.
     * 
     * @param value
     *     allowed object is
     *     {@link testRssDataSourceId }
     *     
     */
    public void setRss(testRssDataSourceId value) {
        this.rss = value;
    }

    /**
     * Gets the value of the gal property.
     * 
     * @return
     *     possible object is
     *     {@link testGalDataSourceId }
     *     
     */
    public testGalDataSourceId getGal() {
        return gal;
    }

    /**
     * Sets the value of the gal property.
     * 
     * @param value
     *     allowed object is
     *     {@link testGalDataSourceId }
     *     
     */
    public void setGal(testGalDataSourceId value) {
        this.gal = value;
    }

    /**
     * Gets the value of the cal property.
     * 
     * @return
     *     possible object is
     *     {@link testCalDataSourceId }
     *     
     */
    public testCalDataSourceId getCal() {
        return cal;
    }

    /**
     * Sets the value of the cal property.
     * 
     * @param value
     *     allowed object is
     *     {@link testCalDataSourceId }
     *     
     */
    public void setCal(testCalDataSourceId value) {
        this.cal = value;
    }

    /**
     * Gets the value of the unknown property.
     * 
     * @return
     *     possible object is
     *     {@link testUnknownDataSourceId }
     *     
     */
    public testUnknownDataSourceId getUnknown() {
        return unknown;
    }

    /**
     * Sets the value of the unknown property.
     * 
     * @param value
     *     allowed object is
     *     {@link testUnknownDataSourceId }
     *     
     */
    public void setUnknown(testUnknownDataSourceId value) {
        this.unknown = value;
    }

}
