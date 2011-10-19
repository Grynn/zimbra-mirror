
package generated.zcsclient.mail;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for simpleRepeatingRule complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="simpleRepeatingRule">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="until" type="{urn:zimbraMail}dateTimeStringAttr" minOccurs="0"/>
 *         &lt;element name="count" type="{urn:zimbraMail}numAttr" minOccurs="0"/>
 *         &lt;element name="interval" type="{urn:zimbraMail}intervalRule" minOccurs="0"/>
 *         &lt;element name="bysecond" type="{urn:zimbraMail}bySecondRule" minOccurs="0"/>
 *         &lt;element name="byminute" type="{urn:zimbraMail}byMinuteRule" minOccurs="0"/>
 *         &lt;element name="byhour" type="{urn:zimbraMail}byHourRule" minOccurs="0"/>
 *         &lt;element name="byday" type="{urn:zimbraMail}byDayRule" minOccurs="0"/>
 *         &lt;element name="bymonthday" type="{urn:zimbraMail}byMonthDayRule" minOccurs="0"/>
 *         &lt;element name="byyearday" type="{urn:zimbraMail}byYearDayRule" minOccurs="0"/>
 *         &lt;element name="byweekno" type="{urn:zimbraMail}byWeekNoRule" minOccurs="0"/>
 *         &lt;element name="bymonth" type="{urn:zimbraMail}byMonthRule" minOccurs="0"/>
 *         &lt;element name="bysetpos" type="{urn:zimbraMail}bySetPosRule" minOccurs="0"/>
 *         &lt;element name="wkst" type="{urn:zimbraMail}wkstRule" minOccurs="0"/>
 *         &lt;element name="rule-x-name" type="{urn:zimbraMail}xNameRule" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="freq" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "simpleRepeatingRule", propOrder = {
    "until",
    "count",
    "interval",
    "bysecond",
    "byminute",
    "byhour",
    "byday",
    "bymonthday",
    "byyearday",
    "byweekno",
    "bymonth",
    "bysetpos",
    "wkst",
    "ruleXName"
})
public class testSimpleRepeatingRule {

    protected testDateTimeStringAttr until;
    protected testNumAttr count;
    protected testIntervalRule interval;
    protected testBySecondRule bysecond;
    protected testByMinuteRule byminute;
    protected testByHourRule byhour;
    protected testByDayRule byday;
    protected testByMonthDayRule bymonthday;
    protected testByYearDayRule byyearday;
    protected testByWeekNoRule byweekno;
    protected testByMonthRule bymonth;
    protected testBySetPosRule bysetpos;
    protected testWkstRule wkst;
    @XmlElement(name = "rule-x-name")
    protected List<testXNameRule> ruleXName;
    @XmlAttribute(name = "freq", required = true)
    protected String freq;

    /**
     * Gets the value of the until property.
     * 
     * @return
     *     possible object is
     *     {@link testDateTimeStringAttr }
     *     
     */
    public testDateTimeStringAttr getUntil() {
        return until;
    }

    /**
     * Sets the value of the until property.
     * 
     * @param value
     *     allowed object is
     *     {@link testDateTimeStringAttr }
     *     
     */
    public void setUntil(testDateTimeStringAttr value) {
        this.until = value;
    }

    /**
     * Gets the value of the count property.
     * 
     * @return
     *     possible object is
     *     {@link testNumAttr }
     *     
     */
    public testNumAttr getCount() {
        return count;
    }

    /**
     * Sets the value of the count property.
     * 
     * @param value
     *     allowed object is
     *     {@link testNumAttr }
     *     
     */
    public void setCount(testNumAttr value) {
        this.count = value;
    }

    /**
     * Gets the value of the interval property.
     * 
     * @return
     *     possible object is
     *     {@link testIntervalRule }
     *     
     */
    public testIntervalRule getInterval() {
        return interval;
    }

    /**
     * Sets the value of the interval property.
     * 
     * @param value
     *     allowed object is
     *     {@link testIntervalRule }
     *     
     */
    public void setInterval(testIntervalRule value) {
        this.interval = value;
    }

    /**
     * Gets the value of the bysecond property.
     * 
     * @return
     *     possible object is
     *     {@link testBySecondRule }
     *     
     */
    public testBySecondRule getBysecond() {
        return bysecond;
    }

    /**
     * Sets the value of the bysecond property.
     * 
     * @param value
     *     allowed object is
     *     {@link testBySecondRule }
     *     
     */
    public void setBysecond(testBySecondRule value) {
        this.bysecond = value;
    }

    /**
     * Gets the value of the byminute property.
     * 
     * @return
     *     possible object is
     *     {@link testByMinuteRule }
     *     
     */
    public testByMinuteRule getByminute() {
        return byminute;
    }

    /**
     * Sets the value of the byminute property.
     * 
     * @param value
     *     allowed object is
     *     {@link testByMinuteRule }
     *     
     */
    public void setByminute(testByMinuteRule value) {
        this.byminute = value;
    }

    /**
     * Gets the value of the byhour property.
     * 
     * @return
     *     possible object is
     *     {@link testByHourRule }
     *     
     */
    public testByHourRule getByhour() {
        return byhour;
    }

    /**
     * Sets the value of the byhour property.
     * 
     * @param value
     *     allowed object is
     *     {@link testByHourRule }
     *     
     */
    public void setByhour(testByHourRule value) {
        this.byhour = value;
    }

    /**
     * Gets the value of the byday property.
     * 
     * @return
     *     possible object is
     *     {@link testByDayRule }
     *     
     */
    public testByDayRule getByday() {
        return byday;
    }

    /**
     * Sets the value of the byday property.
     * 
     * @param value
     *     allowed object is
     *     {@link testByDayRule }
     *     
     */
    public void setByday(testByDayRule value) {
        this.byday = value;
    }

    /**
     * Gets the value of the bymonthday property.
     * 
     * @return
     *     possible object is
     *     {@link testByMonthDayRule }
     *     
     */
    public testByMonthDayRule getBymonthday() {
        return bymonthday;
    }

    /**
     * Sets the value of the bymonthday property.
     * 
     * @param value
     *     allowed object is
     *     {@link testByMonthDayRule }
     *     
     */
    public void setBymonthday(testByMonthDayRule value) {
        this.bymonthday = value;
    }

    /**
     * Gets the value of the byyearday property.
     * 
     * @return
     *     possible object is
     *     {@link testByYearDayRule }
     *     
     */
    public testByYearDayRule getByyearday() {
        return byyearday;
    }

    /**
     * Sets the value of the byyearday property.
     * 
     * @param value
     *     allowed object is
     *     {@link testByYearDayRule }
     *     
     */
    public void setByyearday(testByYearDayRule value) {
        this.byyearday = value;
    }

    /**
     * Gets the value of the byweekno property.
     * 
     * @return
     *     possible object is
     *     {@link testByWeekNoRule }
     *     
     */
    public testByWeekNoRule getByweekno() {
        return byweekno;
    }

    /**
     * Sets the value of the byweekno property.
     * 
     * @param value
     *     allowed object is
     *     {@link testByWeekNoRule }
     *     
     */
    public void setByweekno(testByWeekNoRule value) {
        this.byweekno = value;
    }

    /**
     * Gets the value of the bymonth property.
     * 
     * @return
     *     possible object is
     *     {@link testByMonthRule }
     *     
     */
    public testByMonthRule getBymonth() {
        return bymonth;
    }

    /**
     * Sets the value of the bymonth property.
     * 
     * @param value
     *     allowed object is
     *     {@link testByMonthRule }
     *     
     */
    public void setBymonth(testByMonthRule value) {
        this.bymonth = value;
    }

    /**
     * Gets the value of the bysetpos property.
     * 
     * @return
     *     possible object is
     *     {@link testBySetPosRule }
     *     
     */
    public testBySetPosRule getBysetpos() {
        return bysetpos;
    }

    /**
     * Sets the value of the bysetpos property.
     * 
     * @param value
     *     allowed object is
     *     {@link testBySetPosRule }
     *     
     */
    public void setBysetpos(testBySetPosRule value) {
        this.bysetpos = value;
    }

    /**
     * Gets the value of the wkst property.
     * 
     * @return
     *     possible object is
     *     {@link testWkstRule }
     *     
     */
    public testWkstRule getWkst() {
        return wkst;
    }

    /**
     * Sets the value of the wkst property.
     * 
     * @param value
     *     allowed object is
     *     {@link testWkstRule }
     *     
     */
    public void setWkst(testWkstRule value) {
        this.wkst = value;
    }

    /**
     * Gets the value of the ruleXName property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the ruleXName property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRuleXName().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link testXNameRule }
     * 
     * 
     */
    public List<testXNameRule> getRuleXName() {
        if (ruleXName == null) {
            ruleXName = new ArrayList<testXNameRule>();
        }
        return this.ruleXName;
    }

    /**
     * Gets the value of the freq property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFreq() {
        return freq;
    }

    /**
     * Sets the value of the freq property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFreq(String value) {
        this.freq = value;
    }

}
