
package generated.zcsclient.mail;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for createDataSourceRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="createDataSourceRequest">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;choice minOccurs="0">
 *           &lt;element name="imap" type="{urn:zimbraMail}mailImapDataSource"/>
 *           &lt;element name="pop3" type="{urn:zimbraMail}mailPop3DataSource"/>
 *           &lt;element name="caldav" type="{urn:zimbraMail}mailCaldavDataSource"/>
 *           &lt;element name="yab" type="{urn:zimbraMail}mailYabDataSource"/>
 *           &lt;element name="rss" type="{urn:zimbraMail}mailRssDataSource"/>
 *           &lt;element name="gal" type="{urn:zimbraMail}mailGalDataSource"/>
 *           &lt;element name="cal" type="{urn:zimbraMail}mailCalDataSource"/>
 *           &lt;element name="unknown" type="{urn:zimbraMail}mailUnknownDataSource"/>
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
@XmlType(name = "createDataSourceRequest", propOrder = {
    "imap",
    "pop3",
    "caldav",
    "yab",
    "rss",
    "gal",
    "cal",
    "unknown"
})
public class testCreateDataSourceRequest {

    protected testMailImapDataSource imap;
    protected testMailPop3DataSource pop3;
    protected testMailCaldavDataSource caldav;
    protected testMailYabDataSource yab;
    protected testMailRssDataSource rss;
    protected testMailGalDataSource gal;
    protected testMailCalDataSource cal;
    protected testMailUnknownDataSource unknown;

    /**
     * Gets the value of the imap property.
     * 
     * @return
     *     possible object is
     *     {@link testMailImapDataSource }
     *     
     */
    public testMailImapDataSource getImap() {
        return imap;
    }

    /**
     * Sets the value of the imap property.
     * 
     * @param value
     *     allowed object is
     *     {@link testMailImapDataSource }
     *     
     */
    public void setImap(testMailImapDataSource value) {
        this.imap = value;
    }

    /**
     * Gets the value of the pop3 property.
     * 
     * @return
     *     possible object is
     *     {@link testMailPop3DataSource }
     *     
     */
    public testMailPop3DataSource getPop3() {
        return pop3;
    }

    /**
     * Sets the value of the pop3 property.
     * 
     * @param value
     *     allowed object is
     *     {@link testMailPop3DataSource }
     *     
     */
    public void setPop3(testMailPop3DataSource value) {
        this.pop3 = value;
    }

    /**
     * Gets the value of the caldav property.
     * 
     * @return
     *     possible object is
     *     {@link testMailCaldavDataSource }
     *     
     */
    public testMailCaldavDataSource getCaldav() {
        return caldav;
    }

    /**
     * Sets the value of the caldav property.
     * 
     * @param value
     *     allowed object is
     *     {@link testMailCaldavDataSource }
     *     
     */
    public void setCaldav(testMailCaldavDataSource value) {
        this.caldav = value;
    }

    /**
     * Gets the value of the yab property.
     * 
     * @return
     *     possible object is
     *     {@link testMailYabDataSource }
     *     
     */
    public testMailYabDataSource getYab() {
        return yab;
    }

    /**
     * Sets the value of the yab property.
     * 
     * @param value
     *     allowed object is
     *     {@link testMailYabDataSource }
     *     
     */
    public void setYab(testMailYabDataSource value) {
        this.yab = value;
    }

    /**
     * Gets the value of the rss property.
     * 
     * @return
     *     possible object is
     *     {@link testMailRssDataSource }
     *     
     */
    public testMailRssDataSource getRss() {
        return rss;
    }

    /**
     * Sets the value of the rss property.
     * 
     * @param value
     *     allowed object is
     *     {@link testMailRssDataSource }
     *     
     */
    public void setRss(testMailRssDataSource value) {
        this.rss = value;
    }

    /**
     * Gets the value of the gal property.
     * 
     * @return
     *     possible object is
     *     {@link testMailGalDataSource }
     *     
     */
    public testMailGalDataSource getGal() {
        return gal;
    }

    /**
     * Sets the value of the gal property.
     * 
     * @param value
     *     allowed object is
     *     {@link testMailGalDataSource }
     *     
     */
    public void setGal(testMailGalDataSource value) {
        this.gal = value;
    }

    /**
     * Gets the value of the cal property.
     * 
     * @return
     *     possible object is
     *     {@link testMailCalDataSource }
     *     
     */
    public testMailCalDataSource getCal() {
        return cal;
    }

    /**
     * Sets the value of the cal property.
     * 
     * @param value
     *     allowed object is
     *     {@link testMailCalDataSource }
     *     
     */
    public void setCal(testMailCalDataSource value) {
        this.cal = value;
    }

    /**
     * Gets the value of the unknown property.
     * 
     * @return
     *     possible object is
     *     {@link testMailUnknownDataSource }
     *     
     */
    public testMailUnknownDataSource getUnknown() {
        return unknown;
    }

    /**
     * Sets the value of the unknown property.
     * 
     * @param value
     *     allowed object is
     *     {@link testMailUnknownDataSource }
     *     
     */
    public void setUnknown(testMailUnknownDataSource value) {
        this.unknown = value;
    }

}
