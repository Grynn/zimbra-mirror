
package generated.zcsclient.account;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for operation.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="operation">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="delete"/>
 *     &lt;enumeration value="modify"/>
 *     &lt;enumeration value="rename"/>
 *     &lt;enumeration value="addAlias"/>
 *     &lt;enumeration value="removeAlias"/>
 *     &lt;enumeration value="addOwners"/>
 *     &lt;enumeration value="removeOwners"/>
 *     &lt;enumeration value="setOwners"/>
 *     &lt;enumeration value="grantRights"/>
 *     &lt;enumeration value="revokeRights"/>
 *     &lt;enumeration value="setRights"/>
 *     &lt;enumeration value="addMembers"/>
 *     &lt;enumeration value="removeMembers"/>
 *     &lt;enumeration value="acceptSubsReq"/>
 *     &lt;enumeration value="rejectSubsReq"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "operation")
@XmlEnum
public enum testOperation {

    @XmlEnumValue("delete")
    DELETE("delete"),
    @XmlEnumValue("modify")
    MODIFY("modify"),
    @XmlEnumValue("rename")
    RENAME("rename"),
    @XmlEnumValue("addAlias")
    ADD_ALIAS("addAlias"),
    @XmlEnumValue("removeAlias")
    REMOVE_ALIAS("removeAlias"),
    @XmlEnumValue("addOwners")
    ADD_OWNERS("addOwners"),
    @XmlEnumValue("removeOwners")
    REMOVE_OWNERS("removeOwners"),
    @XmlEnumValue("setOwners")
    SET_OWNERS("setOwners"),
    @XmlEnumValue("grantRights")
    GRANT_RIGHTS("grantRights"),
    @XmlEnumValue("revokeRights")
    REVOKE_RIGHTS("revokeRights"),
    @XmlEnumValue("setRights")
    SET_RIGHTS("setRights"),
    @XmlEnumValue("addMembers")
    ADD_MEMBERS("addMembers"),
    @XmlEnumValue("removeMembers")
    REMOVE_MEMBERS("removeMembers"),
    @XmlEnumValue("acceptSubsReq")
    ACCEPT_SUBS_REQ("acceptSubsReq"),
    @XmlEnumValue("rejectSubsReq")
    REJECT_SUBS_REQ("rejectSubsReq");
    private final String value;

    testOperation(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static testOperation fromValue(String v) {
        for (testOperation c: testOperation.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
