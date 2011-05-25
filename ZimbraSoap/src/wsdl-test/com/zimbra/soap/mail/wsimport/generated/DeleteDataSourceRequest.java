
package com.zimbra.soap.mail.wsimport.generated;

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
public class DeleteDataSourceRequest {

    protected ImapDataSourceNameOrId imap;
    protected Pop3DataSourceNameOrId pop3;
    protected CaldavDataSourceNameOrId caldav;
    protected YabDataSourceNameOrId yab;
    protected RssDataSourceNameOrId rss;
    protected GalDataSourceNameOrId gal;
    protected CalDataSourceNameOrId cal;
    protected UnknownDataSourceNameOrId unknown;

    /**
     * Gets the value of the imap property.
     * 
     * @return
     *     possible object is
     *     {@link ImapDataSourceNameOrId }
     *     
     */
    public ImapDataSourceNameOrId getImap() {
        return imap;
    }

    /**
     * Sets the value of the imap property.
     * 
     * @param value
     *     allowed object is
     *     {@link ImapDataSourceNameOrId }
     *     
     */
    public void setImap(ImapDataSourceNameOrId value) {
        this.imap = value;
    }

    /**
     * Gets the value of the pop3 property.
     * 
     * @return
     *     possible object is
     *     {@link Pop3DataSourceNameOrId }
     *     
     */
    public Pop3DataSourceNameOrId getPop3() {
        return pop3;
    }

    /**
     * Sets the value of the pop3 property.
     * 
     * @param value
     *     allowed object is
     *     {@link Pop3DataSourceNameOrId }
     *     
     */
    public void setPop3(Pop3DataSourceNameOrId value) {
        this.pop3 = value;
    }

    /**
     * Gets the value of the caldav property.
     * 
     * @return
     *     possible object is
     *     {@link CaldavDataSourceNameOrId }
     *     
     */
    public CaldavDataSourceNameOrId getCaldav() {
        return caldav;
    }

    /**
     * Sets the value of the caldav property.
     * 
     * @param value
     *     allowed object is
     *     {@link CaldavDataSourceNameOrId }
     *     
     */
    public void setCaldav(CaldavDataSourceNameOrId value) {
        this.caldav = value;
    }

    /**
     * Gets the value of the yab property.
     * 
     * @return
     *     possible object is
     *     {@link YabDataSourceNameOrId }
     *     
     */
    public YabDataSourceNameOrId getYab() {
        return yab;
    }

    /**
     * Sets the value of the yab property.
     * 
     * @param value
     *     allowed object is
     *     {@link YabDataSourceNameOrId }
     *     
     */
    public void setYab(YabDataSourceNameOrId value) {
        this.yab = value;
    }

    /**
     * Gets the value of the rss property.
     * 
     * @return
     *     possible object is
     *     {@link RssDataSourceNameOrId }
     *     
     */
    public RssDataSourceNameOrId getRss() {
        return rss;
    }

    /**
     * Sets the value of the rss property.
     * 
     * @param value
     *     allowed object is
     *     {@link RssDataSourceNameOrId }
     *     
     */
    public void setRss(RssDataSourceNameOrId value) {
        this.rss = value;
    }

    /**
     * Gets the value of the gal property.
     * 
     * @return
     *     possible object is
     *     {@link GalDataSourceNameOrId }
     *     
     */
    public GalDataSourceNameOrId getGal() {
        return gal;
    }

    /**
     * Sets the value of the gal property.
     * 
     * @param value
     *     allowed object is
     *     {@link GalDataSourceNameOrId }
     *     
     */
    public void setGal(GalDataSourceNameOrId value) {
        this.gal = value;
    }

    /**
     * Gets the value of the cal property.
     * 
     * @return
     *     possible object is
     *     {@link CalDataSourceNameOrId }
     *     
     */
    public CalDataSourceNameOrId getCal() {
        return cal;
    }

    /**
     * Sets the value of the cal property.
     * 
     * @param value
     *     allowed object is
     *     {@link CalDataSourceNameOrId }
     *     
     */
    public void setCal(CalDataSourceNameOrId value) {
        this.cal = value;
    }

    /**
     * Gets the value of the unknown property.
     * 
     * @return
     *     possible object is
     *     {@link UnknownDataSourceNameOrId }
     *     
     */
    public UnknownDataSourceNameOrId getUnknown() {
        return unknown;
    }

    /**
     * Sets the value of the unknown property.
     * 
     * @param value
     *     allowed object is
     *     {@link UnknownDataSourceNameOrId }
     *     
     */
    public void setUnknown(UnknownDataSourceNameOrId value) {
        this.unknown = value;
    }

}
