
package generated.zcsclient.admin;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for runUnitTestsResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="runUnitTestsResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="results" type="{urn:zimbraAdmin}testResultInfo"/>
 *       &lt;/sequence>
 *       &lt;attribute name="numExecuted" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="numFailed" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "runUnitTestsResponse", propOrder = {
    "results"
})
public class testRunUnitTestsResponse {

    @XmlElement(required = true)
    protected testTestResultInfo results;
    @XmlAttribute(name = "numExecuted", required = true)
    protected int numExecuted;
    @XmlAttribute(name = "numFailed", required = true)
    protected int numFailed;

    /**
     * Gets the value of the results property.
     * 
     * @return
     *     possible object is
     *     {@link testTestResultInfo }
     *     
     */
    public testTestResultInfo getResults() {
        return results;
    }

    /**
     * Sets the value of the results property.
     * 
     * @param value
     *     allowed object is
     *     {@link testTestResultInfo }
     *     
     */
    public void setResults(testTestResultInfo value) {
        this.results = value;
    }

    /**
     * Gets the value of the numExecuted property.
     * 
     */
    public int getNumExecuted() {
        return numExecuted;
    }

    /**
     * Sets the value of the numExecuted property.
     * 
     */
    public void setNumExecuted(int value) {
        this.numExecuted = value;
    }

    /**
     * Gets the value of the numFailed property.
     * 
     */
    public int getNumFailed() {
        return numFailed;
    }

    /**
     * Sets the value of the numFailed property.
     * 
     */
    public void setNumFailed(int value) {
        this.numFailed = value;
    }

}
