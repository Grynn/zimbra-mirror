
package com.zimbra.soap.mail.wsimport.generated;

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
public class CreateDataSourceResponse {

    protected ImapDataSourceId imap;
    protected Pop3DataSourceId pop3;
    protected CaldavDataSourceId caldav;
    protected YabDataSourceId yab;
    protected RssDataSourceId rss;
    protected GalDataSourceId gal;
    protected CalDataSourceId cal;
    protected UnknownDataSourceId unknown;

    /**
     * Gets the value of the imap property.
     * 
     * @return
     *     possible object is
     *     {@link ImapDataSourceId }
     *     
     */
    public ImapDataSourceId getImap() {
        return imap;
    }

    /**
     * Sets the value of the imap property.
     * 
     * @param value
     *     allowed object is
     *     {@link ImapDataSourceId }
     *     
     */
    public void setImap(ImapDataSourceId value) {
        this.imap = value;
    }

    /**
     * Gets the value of the pop3 property.
     * 
     * @return
     *     possible object is
     *     {@link Pop3DataSourceId }
     *     
     */
    public Pop3DataSourceId getPop3() {
        return pop3;
    }

    /**
     * Sets the value of the pop3 property.
     * 
     * @param value
     *     allowed object is
     *     {@link Pop3DataSourceId }
     *     
     */
    public void setPop3(Pop3DataSourceId value) {
        this.pop3 = value;
    }

    /**
     * Gets the value of the caldav property.
     * 
     * @return
     *     possible object is
     *     {@link CaldavDataSourceId }
     *     
     */
    public CaldavDataSourceId getCaldav() {
        return caldav;
    }

    /**
     * Sets the value of the caldav property.
     * 
     * @param value
     *     allowed object is
     *     {@link CaldavDataSourceId }
     *     
     */
    public void setCaldav(CaldavDataSourceId value) {
        this.caldav = value;
    }

    /**
     * Gets the value of the yab property.
     * 
     * @return
     *     possible object is
     *     {@link YabDataSourceId }
     *     
     */
    public YabDataSourceId getYab() {
        return yab;
    }

    /**
     * Sets the value of the yab property.
     * 
     * @param value
     *     allowed object is
     *     {@link YabDataSourceId }
     *     
     */
    public void setYab(YabDataSourceId value) {
        this.yab = value;
    }

    /**
     * Gets the value of the rss property.
     * 
     * @return
     *     possible object is
     *     {@link RssDataSourceId }
     *     
     */
    public RssDataSourceId getRss() {
        return rss;
    }

    /**
     * Sets the value of the rss property.
     * 
     * @param value
     *     allowed object is
     *     {@link RssDataSourceId }
     *     
     */
    public void setRss(RssDataSourceId value) {
        this.rss = value;
    }

    /**
     * Gets the value of the gal property.
     * 
     * @return
     *     possible object is
     *     {@link GalDataSourceId }
     *     
     */
    public GalDataSourceId getGal() {
        return gal;
    }

    /**
     * Sets the value of the gal property.
     * 
     * @param value
     *     allowed object is
     *     {@link GalDataSourceId }
     *     
     */
    public void setGal(GalDataSourceId value) {
        this.gal = value;
    }

    /**
     * Gets the value of the cal property.
     * 
     * @return
     *     possible object is
     *     {@link CalDataSourceId }
     *     
     */
    public CalDataSourceId getCal() {
        return cal;
    }

    /**
     * Sets the value of the cal property.
     * 
     * @param value
     *     allowed object is
     *     {@link CalDataSourceId }
     *     
     */
    public void setCal(CalDataSourceId value) {
        this.cal = value;
    }

    /**
     * Gets the value of the unknown property.
     * 
     * @return
     *     possible object is
     *     {@link UnknownDataSourceId }
     *     
     */
    public UnknownDataSourceId getUnknown() {
        return unknown;
    }

    /**
     * Sets the value of the unknown property.
     * 
     * @param value
     *     allowed object is
     *     {@link UnknownDataSourceId }
     *     
     */
    public void setUnknown(UnknownDataSourceId value) {
        this.unknown = value;
    }

}
