
package com.zimbra.soap.admin.wsimport.generated;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for requestAttr.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="requestAttr">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="zimbraMailRecipient"/>
 *     &lt;enumeration value="zimbraAccount"/>
 *     &lt;enumeration value="zimbraAlias"/>
 *     &lt;enumeration value="zimbraDistributionList"/>
 *     &lt;enumeration value="zimbraCOS"/>
 *     &lt;enumeration value="zimbraGlobalConfig"/>
 *     &lt;enumeration value="zimbraDomain"/>
 *     &lt;enumeration value="zimbraSecurityGroup"/>
 *     &lt;enumeration value="zimbraServer"/>
 *     &lt;enumeration value="zimbraMimeEntry"/>
 *     &lt;enumeration value="zimbraObjectEntry"/>
 *     &lt;enumeration value="zimbraTimeZone"/>
 *     &lt;enumeration value="zimbraZimletEntry"/>
 *     &lt;enumeration value="zimbraCalendarResource"/>
 *     &lt;enumeration value="zimbraIdentity"/>
 *     &lt;enumeration value="zimbraDataSource"/>
 *     &lt;enumeration value="zimbraPop3DataSource"/>
 *     &lt;enumeration value="zimbraImapDataSource"/>
 *     &lt;enumeration value="zimbraRssDataSource"/>
 *     &lt;enumeration value="zimbraLiveDataSource"/>
 *     &lt;enumeration value="zimbraGalDataSource"/>
 *     &lt;enumeration value="zimbraSignature"/>
 *     &lt;enumeration value="zimbraXMPPComponent"/>
 *     &lt;enumeration value="zimbraAclTarget"/>
 *     &lt;enumeration value="zimbraGroup"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "requestAttr")
@XmlEnum
public enum RequestAttr {

    @XmlEnumValue("zimbraMailRecipient")
    ZIMBRA_MAIL_RECIPIENT("zimbraMailRecipient"),
    @XmlEnumValue("zimbraAccount")
    ZIMBRA_ACCOUNT("zimbraAccount"),
    @XmlEnumValue("zimbraAlias")
    ZIMBRA_ALIAS("zimbraAlias"),
    @XmlEnumValue("zimbraDistributionList")
    ZIMBRA_DISTRIBUTION_LIST("zimbraDistributionList"),
    @XmlEnumValue("zimbraCOS")
    ZIMBRA_COS("zimbraCOS"),
    @XmlEnumValue("zimbraGlobalConfig")
    ZIMBRA_GLOBAL_CONFIG("zimbraGlobalConfig"),
    @XmlEnumValue("zimbraDomain")
    ZIMBRA_DOMAIN("zimbraDomain"),
    @XmlEnumValue("zimbraSecurityGroup")
    ZIMBRA_SECURITY_GROUP("zimbraSecurityGroup"),
    @XmlEnumValue("zimbraServer")
    ZIMBRA_SERVER("zimbraServer"),
    @XmlEnumValue("zimbraMimeEntry")
    ZIMBRA_MIME_ENTRY("zimbraMimeEntry"),
    @XmlEnumValue("zimbraObjectEntry")
    ZIMBRA_OBJECT_ENTRY("zimbraObjectEntry"),
    @XmlEnumValue("zimbraTimeZone")
    ZIMBRA_TIME_ZONE("zimbraTimeZone"),
    @XmlEnumValue("zimbraZimletEntry")
    ZIMBRA_ZIMLET_ENTRY("zimbraZimletEntry"),
    @XmlEnumValue("zimbraCalendarResource")
    ZIMBRA_CALENDAR_RESOURCE("zimbraCalendarResource"),
    @XmlEnumValue("zimbraIdentity")
    ZIMBRA_IDENTITY("zimbraIdentity"),
    @XmlEnumValue("zimbraDataSource")
    ZIMBRA_DATA_SOURCE("zimbraDataSource"),
    @XmlEnumValue("zimbraPop3DataSource")
    ZIMBRA_POP_3_DATA_SOURCE("zimbraPop3DataSource"),
    @XmlEnumValue("zimbraImapDataSource")
    ZIMBRA_IMAP_DATA_SOURCE("zimbraImapDataSource"),
    @XmlEnumValue("zimbraRssDataSource")
    ZIMBRA_RSS_DATA_SOURCE("zimbraRssDataSource"),
    @XmlEnumValue("zimbraLiveDataSource")
    ZIMBRA_LIVE_DATA_SOURCE("zimbraLiveDataSource"),
    @XmlEnumValue("zimbraGalDataSource")
    ZIMBRA_GAL_DATA_SOURCE("zimbraGalDataSource"),
    @XmlEnumValue("zimbraSignature")
    ZIMBRA_SIGNATURE("zimbraSignature"),
    @XmlEnumValue("zimbraXMPPComponent")
    ZIMBRA_XMPP_COMPONENT("zimbraXMPPComponent"),
    @XmlEnumValue("zimbraAclTarget")
    ZIMBRA_ACL_TARGET("zimbraAclTarget"),
    @XmlEnumValue("zimbraGroup")
    ZIMBRA_GROUP("zimbraGroup");
    private final String value;

    RequestAttr(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static RequestAttr fromValue(String v) {
        for (RequestAttr c: RequestAttr.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
