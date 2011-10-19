
package generated.zcsclient.zm;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for sourceLookupOpt.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="sourceLookupOpt">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="ANY"/>
 *     &lt;enumeration value="ALL"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "sourceLookupOpt")
@XmlEnum
public enum testSourceLookupOpt {

    ANY,
    ALL;

    public String value() {
        return name();
    }

    public static testSourceLookupOpt fromValue(String v) {
        return valueOf(v);
    }

}
