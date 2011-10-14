
package zimbra.generated.accountclient.admin;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for fileCopierSpec complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="fileCopierSpec">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *       &lt;/sequence>
 *       &lt;attribute name="fcMethod" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="fcIOType" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="fcOIOCopyBufferSize" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="fcAsyncQueueCapacity" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="fcParallelWorkers" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="fcPipes" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="fcPipeBufferSize" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="fcPipeReadersPerPipe" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="fcPipeWritersPerPipe" type="{http://www.w3.org/2001/XMLSchema}int" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "fileCopierSpec")
public class testFileCopierSpec {

    @XmlAttribute(name = "fcMethod")
    protected String fcMethod;
    @XmlAttribute(name = "fcIOType")
    protected String fcIOType;
    @XmlAttribute(name = "fcOIOCopyBufferSize")
    protected Integer fcOIOCopyBufferSize;
    @XmlAttribute(name = "fcAsyncQueueCapacity")
    protected Integer fcAsyncQueueCapacity;
    @XmlAttribute(name = "fcParallelWorkers")
    protected Integer fcParallelWorkers;
    @XmlAttribute(name = "fcPipes")
    protected Integer fcPipes;
    @XmlAttribute(name = "fcPipeBufferSize")
    protected Integer fcPipeBufferSize;
    @XmlAttribute(name = "fcPipeReadersPerPipe")
    protected Integer fcPipeReadersPerPipe;
    @XmlAttribute(name = "fcPipeWritersPerPipe")
    protected Integer fcPipeWritersPerPipe;

    /**
     * Gets the value of the fcMethod property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFcMethod() {
        return fcMethod;
    }

    /**
     * Sets the value of the fcMethod property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFcMethod(String value) {
        this.fcMethod = value;
    }

    /**
     * Gets the value of the fcIOType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFcIOType() {
        return fcIOType;
    }

    /**
     * Sets the value of the fcIOType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFcIOType(String value) {
        this.fcIOType = value;
    }

    /**
     * Gets the value of the fcOIOCopyBufferSize property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getFcOIOCopyBufferSize() {
        return fcOIOCopyBufferSize;
    }

    /**
     * Sets the value of the fcOIOCopyBufferSize property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setFcOIOCopyBufferSize(Integer value) {
        this.fcOIOCopyBufferSize = value;
    }

    /**
     * Gets the value of the fcAsyncQueueCapacity property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getFcAsyncQueueCapacity() {
        return fcAsyncQueueCapacity;
    }

    /**
     * Sets the value of the fcAsyncQueueCapacity property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setFcAsyncQueueCapacity(Integer value) {
        this.fcAsyncQueueCapacity = value;
    }

    /**
     * Gets the value of the fcParallelWorkers property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getFcParallelWorkers() {
        return fcParallelWorkers;
    }

    /**
     * Sets the value of the fcParallelWorkers property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setFcParallelWorkers(Integer value) {
        this.fcParallelWorkers = value;
    }

    /**
     * Gets the value of the fcPipes property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getFcPipes() {
        return fcPipes;
    }

    /**
     * Sets the value of the fcPipes property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setFcPipes(Integer value) {
        this.fcPipes = value;
    }

    /**
     * Gets the value of the fcPipeBufferSize property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getFcPipeBufferSize() {
        return fcPipeBufferSize;
    }

    /**
     * Sets the value of the fcPipeBufferSize property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setFcPipeBufferSize(Integer value) {
        this.fcPipeBufferSize = value;
    }

    /**
     * Gets the value of the fcPipeReadersPerPipe property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getFcPipeReadersPerPipe() {
        return fcPipeReadersPerPipe;
    }

    /**
     * Sets the value of the fcPipeReadersPerPipe property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setFcPipeReadersPerPipe(Integer value) {
        this.fcPipeReadersPerPipe = value;
    }

    /**
     * Gets the value of the fcPipeWritersPerPipe property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getFcPipeWritersPerPipe() {
        return fcPipeWritersPerPipe;
    }

    /**
     * Sets the value of the fcPipeWritersPerPipe property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setFcPipeWritersPerPipe(Integer value) {
        this.fcPipeWritersPerPipe = value;
    }

}
