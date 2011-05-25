
package com.zimbra.soap.mail.wsimport.generated;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for modifyDataSourceRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="modifyDataSourceRequest">
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
@XmlType(name = "modifyDataSourceRequest", propOrder = {
    "imap",
    "pop3",
    "caldav",
    "yab",
    "rss",
    "gal",
    "cal",
    "unknown"
})
public class ModifyDataSourceRequest {

    protected MailImapDataSource imap;
    protected MailPop3DataSource pop3;
    protected MailCaldavDataSource caldav;
    protected MailYabDataSource yab;
    protected MailRssDataSource rss;
    protected MailGalDataSource gal;
    protected MailCalDataSource cal;
    protected MailUnknownDataSource unknown;

    /**
     * Gets the value of the imap property.
     * 
     * @return
     *     possible object is
     *     {@link MailImapDataSource }
     *     
     */
    public MailImapDataSource getImap() {
        return imap;
    }

    /**
     * Sets the value of the imap property.
     * 
     * @param value
     *     allowed object is
     *     {@link MailImapDataSource }
     *     
     */
    public void setImap(MailImapDataSource value) {
        this.imap = value;
    }

    /**
     * Gets the value of the pop3 property.
     * 
     * @return
     *     possible object is
     *     {@link MailPop3DataSource }
     *     
     */
    public MailPop3DataSource getPop3() {
        return pop3;
    }

    /**
     * Sets the value of the pop3 property.
     * 
     * @param value
     *     allowed object is
     *     {@link MailPop3DataSource }
     *     
     */
    public void setPop3(MailPop3DataSource value) {
        this.pop3 = value;
    }

    /**
     * Gets the value of the caldav property.
     * 
     * @return
     *     possible object is
     *     {@link MailCaldavDataSource }
     *     
     */
    public MailCaldavDataSource getCaldav() {
        return caldav;
    }

    /**
     * Sets the value of the caldav property.
     * 
     * @param value
     *     allowed object is
     *     {@link MailCaldavDataSource }
     *     
     */
    public void setCaldav(MailCaldavDataSource value) {
        this.caldav = value;
    }

    /**
     * Gets the value of the yab property.
     * 
     * @return
     *     possible object is
     *     {@link MailYabDataSource }
     *     
     */
    public MailYabDataSource getYab() {
        return yab;
    }

    /**
     * Sets the value of the yab property.
     * 
     * @param value
     *     allowed object is
     *     {@link MailYabDataSource }
     *     
     */
    public void setYab(MailYabDataSource value) {
        this.yab = value;
    }

    /**
     * Gets the value of the rss property.
     * 
     * @return
     *     possible object is
     *     {@link MailRssDataSource }
     *     
     */
    public MailRssDataSource getRss() {
        return rss;
    }

    /**
     * Sets the value of the rss property.
     * 
     * @param value
     *     allowed object is
     *     {@link MailRssDataSource }
     *     
     */
    public void setRss(MailRssDataSource value) {
        this.rss = value;
    }

    /**
     * Gets the value of the gal property.
     * 
     * @return
     *     possible object is
     *     {@link MailGalDataSource }
     *     
     */
    public MailGalDataSource getGal() {
        return gal;
    }

    /**
     * Sets the value of the gal property.
     * 
     * @param value
     *     allowed object is
     *     {@link MailGalDataSource }
     *     
     */
    public void setGal(MailGalDataSource value) {
        this.gal = value;
    }

    /**
     * Gets the value of the cal property.
     * 
     * @return
     *     possible object is
     *     {@link MailCalDataSource }
     *     
     */
    public MailCalDataSource getCal() {
        return cal;
    }

    /**
     * Sets the value of the cal property.
     * 
     * @param value
     *     allowed object is
     *     {@link MailCalDataSource }
     *     
     */
    public void setCal(MailCalDataSource value) {
        this.cal = value;
    }

    /**
     * Gets the value of the unknown property.
     * 
     * @return
     *     possible object is
     *     {@link MailUnknownDataSource }
     *     
     */
    public MailUnknownDataSource getUnknown() {
        return unknown;
    }

    /**
     * Sets the value of the unknown property.
     * 
     * @param value
     *     allowed object is
     *     {@link MailUnknownDataSource }
     *     
     */
    public void setUnknown(MailUnknownDataSource value) {
        this.unknown = value;
    }

}
