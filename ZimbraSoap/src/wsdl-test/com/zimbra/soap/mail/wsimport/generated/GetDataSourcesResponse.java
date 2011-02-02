
package com.zimbra.soap.mail.wsimport.generated;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for getDataSourcesResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getDataSourcesResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;choice maxOccurs="unbounded" minOccurs="0">
 *           &lt;element name="pop3" type="{urn:zimbraMail}mailPop3DataSource"/>
 *           &lt;element name="imap" type="{urn:zimbraMail}mailImapDataSource"/>
 *           &lt;element name="rss" type="{urn:zimbraMail}mailRssDataSource"/>
 *           &lt;element name="cal" type="{urn:zimbraMail}mailCalDataSource"/>
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
@XmlType(name = "getDataSourcesResponse", propOrder = {
    "pop3OrImapOrRss"
})
public class GetDataSourcesResponse {

    @XmlElements({
        @XmlElement(name = "rss", type = MailRssDataSource.class),
        @XmlElement(name = "pop3", type = MailPop3DataSource.class),
        @XmlElement(name = "cal", type = MailCalDataSource.class),
        @XmlElement(name = "imap", type = MailImapDataSource.class)
    })
    protected List<MailDataSource> pop3OrImapOrRss;

    /**
     * Gets the value of the pop3OrImapOrRss property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the pop3OrImapOrRss property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPop3OrImapOrRss().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link MailRssDataSource }
     * {@link MailPop3DataSource }
     * {@link MailCalDataSource }
     * {@link MailImapDataSource }
     * 
     * 
     */
    public List<MailDataSource> getPop3OrImapOrRss() {
        if (pop3OrImapOrRss == null) {
            pop3OrImapOrRss = new ArrayList<MailDataSource>();
        }
        return this.pop3OrImapOrRss;
    }

}
