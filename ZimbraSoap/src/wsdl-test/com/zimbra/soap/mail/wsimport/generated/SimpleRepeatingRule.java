
package com.zimbra.soap.mail.wsimport.generated;

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
public class SimpleRepeatingRule {

    protected DateTimeStringAttr until;
    protected NumAttr count;
    protected IntervalRule interval;
    protected BySecondRule bysecond;
    protected ByMinuteRule byminute;
    protected ByHourRule byhour;
    protected ByDayRule byday;
    protected ByMonthDayRule bymonthday;
    protected ByYearDayRule byyearday;
    protected ByWeekNoRule byweekno;
    protected ByMonthRule bymonth;
    protected BySetPosRule bysetpos;
    protected WkstRule wkst;
    @XmlElement(name = "rule-x-name")
    protected List<XNameRule> ruleXName;
    @XmlAttribute(required = true)
    protected String freq;

    /**
     * Gets the value of the until property.
     * 
     * @return
     *     possible object is
     *     {@link DateTimeStringAttr }
     *     
     */
    public DateTimeStringAttr getUntil() {
        return until;
    }

    /**
     * Sets the value of the until property.
     * 
     * @param value
     *     allowed object is
     *     {@link DateTimeStringAttr }
     *     
     */
    public void setUntil(DateTimeStringAttr value) {
        this.until = value;
    }

    /**
     * Gets the value of the count property.
     * 
     * @return
     *     possible object is
     *     {@link NumAttr }
     *     
     */
    public NumAttr getCount() {
        return count;
    }

    /**
     * Sets the value of the count property.
     * 
     * @param value
     *     allowed object is
     *     {@link NumAttr }
     *     
     */
    public void setCount(NumAttr value) {
        this.count = value;
    }

    /**
     * Gets the value of the interval property.
     * 
     * @return
     *     possible object is
     *     {@link IntervalRule }
     *     
     */
    public IntervalRule getInterval() {
        return interval;
    }

    /**
     * Sets the value of the interval property.
     * 
     * @param value
     *     allowed object is
     *     {@link IntervalRule }
     *     
     */
    public void setInterval(IntervalRule value) {
        this.interval = value;
    }

    /**
     * Gets the value of the bysecond property.
     * 
     * @return
     *     possible object is
     *     {@link BySecondRule }
     *     
     */
    public BySecondRule getBysecond() {
        return bysecond;
    }

    /**
     * Sets the value of the bysecond property.
     * 
     * @param value
     *     allowed object is
     *     {@link BySecondRule }
     *     
     */
    public void setBysecond(BySecondRule value) {
        this.bysecond = value;
    }

    /**
     * Gets the value of the byminute property.
     * 
     * @return
     *     possible object is
     *     {@link ByMinuteRule }
     *     
     */
    public ByMinuteRule getByminute() {
        return byminute;
    }

    /**
     * Sets the value of the byminute property.
     * 
     * @param value
     *     allowed object is
     *     {@link ByMinuteRule }
     *     
     */
    public void setByminute(ByMinuteRule value) {
        this.byminute = value;
    }

    /**
     * Gets the value of the byhour property.
     * 
     * @return
     *     possible object is
     *     {@link ByHourRule }
     *     
     */
    public ByHourRule getByhour() {
        return byhour;
    }

    /**
     * Sets the value of the byhour property.
     * 
     * @param value
     *     allowed object is
     *     {@link ByHourRule }
     *     
     */
    public void setByhour(ByHourRule value) {
        this.byhour = value;
    }

    /**
     * Gets the value of the byday property.
     * 
     * @return
     *     possible object is
     *     {@link ByDayRule }
     *     
     */
    public ByDayRule getByday() {
        return byday;
    }

    /**
     * Sets the value of the byday property.
     * 
     * @param value
     *     allowed object is
     *     {@link ByDayRule }
     *     
     */
    public void setByday(ByDayRule value) {
        this.byday = value;
    }

    /**
     * Gets the value of the bymonthday property.
     * 
     * @return
     *     possible object is
     *     {@link ByMonthDayRule }
     *     
     */
    public ByMonthDayRule getBymonthday() {
        return bymonthday;
    }

    /**
     * Sets the value of the bymonthday property.
     * 
     * @param value
     *     allowed object is
     *     {@link ByMonthDayRule }
     *     
     */
    public void setBymonthday(ByMonthDayRule value) {
        this.bymonthday = value;
    }

    /**
     * Gets the value of the byyearday property.
     * 
     * @return
     *     possible object is
     *     {@link ByYearDayRule }
     *     
     */
    public ByYearDayRule getByyearday() {
        return byyearday;
    }

    /**
     * Sets the value of the byyearday property.
     * 
     * @param value
     *     allowed object is
     *     {@link ByYearDayRule }
     *     
     */
    public void setByyearday(ByYearDayRule value) {
        this.byyearday = value;
    }

    /**
     * Gets the value of the byweekno property.
     * 
     * @return
     *     possible object is
     *     {@link ByWeekNoRule }
     *     
     */
    public ByWeekNoRule getByweekno() {
        return byweekno;
    }

    /**
     * Sets the value of the byweekno property.
     * 
     * @param value
     *     allowed object is
     *     {@link ByWeekNoRule }
     *     
     */
    public void setByweekno(ByWeekNoRule value) {
        this.byweekno = value;
    }

    /**
     * Gets the value of the bymonth property.
     * 
     * @return
     *     possible object is
     *     {@link ByMonthRule }
     *     
     */
    public ByMonthRule getBymonth() {
        return bymonth;
    }

    /**
     * Sets the value of the bymonth property.
     * 
     * @param value
     *     allowed object is
     *     {@link ByMonthRule }
     *     
     */
    public void setBymonth(ByMonthRule value) {
        this.bymonth = value;
    }

    /**
     * Gets the value of the bysetpos property.
     * 
     * @return
     *     possible object is
     *     {@link BySetPosRule }
     *     
     */
    public BySetPosRule getBysetpos() {
        return bysetpos;
    }

    /**
     * Sets the value of the bysetpos property.
     * 
     * @param value
     *     allowed object is
     *     {@link BySetPosRule }
     *     
     */
    public void setBysetpos(BySetPosRule value) {
        this.bysetpos = value;
    }

    /**
     * Gets the value of the wkst property.
     * 
     * @return
     *     possible object is
     *     {@link WkstRule }
     *     
     */
    public WkstRule getWkst() {
        return wkst;
    }

    /**
     * Sets the value of the wkst property.
     * 
     * @param value
     *     allowed object is
     *     {@link WkstRule }
     *     
     */
    public void setWkst(WkstRule value) {
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
     * {@link XNameRule }
     * 
     * 
     */
    public List<XNameRule> getRuleXName() {
        if (ruleXName == null) {
            ruleXName = new ArrayList<XNameRule>();
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
