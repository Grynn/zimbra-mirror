
package zimbra.generated.accountclient.zm;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for searchSortBy.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="searchSortBy">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="dateDesc"/>
 *     &lt;enumeration value="dateAsc"/>
 *     &lt;enumeration value="subjDesc"/>
 *     &lt;enumeration value="subjAsc"/>
 *     &lt;enumeration value="nameDesc"/>
 *     &lt;enumeration value="nameAsc"/>
 *     &lt;enumeration value="durDesc"/>
 *     &lt;enumeration value="durAsc"/>
 *     &lt;enumeration value="none"/>
 *     &lt;enumeration value="taskDueAsc"/>
 *     &lt;enumeration value="taskDueDesc"/>
 *     &lt;enumeration value="taskStatusAsc"/>
 *     &lt;enumeration value="taskStatusDesc"/>
 *     &lt;enumeration value="taskPercCompletedAsc"/>
 *     &lt;enumeration value="taskPercCompletedDesc"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "searchSortBy")
@XmlEnum
public enum testSearchSortBy {

    @XmlEnumValue("dateDesc")
    DATE_DESC("dateDesc"),
    @XmlEnumValue("dateAsc")
    DATE_ASC("dateAsc"),
    @XmlEnumValue("subjDesc")
    SUBJ_DESC("subjDesc"),
    @XmlEnumValue("subjAsc")
    SUBJ_ASC("subjAsc"),
    @XmlEnumValue("nameDesc")
    NAME_DESC("nameDesc"),
    @XmlEnumValue("nameAsc")
    NAME_ASC("nameAsc"),
    @XmlEnumValue("durDesc")
    DUR_DESC("durDesc"),
    @XmlEnumValue("durAsc")
    DUR_ASC("durAsc"),
    @XmlEnumValue("none")
    NONE("none"),
    @XmlEnumValue("taskDueAsc")
    TASK_DUE_ASC("taskDueAsc"),
    @XmlEnumValue("taskDueDesc")
    TASK_DUE_DESC("taskDueDesc"),
    @XmlEnumValue("taskStatusAsc")
    TASK_STATUS_ASC("taskStatusAsc"),
    @XmlEnumValue("taskStatusDesc")
    TASK_STATUS_DESC("taskStatusDesc"),
    @XmlEnumValue("taskPercCompletedAsc")
    TASK_PERC_COMPLETED_ASC("taskPercCompletedAsc"),
    @XmlEnumValue("taskPercCompletedDesc")
    TASK_PERC_COMPLETED_DESC("taskPercCompletedDesc");
    private final String value;

    testSearchSortBy(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static testSearchSortBy fromValue(String v) {
        for (testSearchSortBy c: testSearchSortBy.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
