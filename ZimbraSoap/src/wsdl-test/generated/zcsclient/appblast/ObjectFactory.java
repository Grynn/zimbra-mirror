
package generated.zcsclient.appblast;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the generated.zcsclient.appblast package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _FinishEditDocumentRequest_QNAME = new QName("urn:zimbraAppblast", "FinishEditDocumentRequest");
    private final static QName _EditDocumentResponse_QNAME = new QName("urn:zimbraAppblast", "EditDocumentResponse");
    private final static QName _EditDocumentRequest_QNAME = new QName("urn:zimbraAppblast", "EditDocumentRequest");
    private final static QName _FinishEditDocumentResponse_QNAME = new QName("urn:zimbraAppblast", "FinishEditDocumentResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: generated.zcsclient.appblast
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link testEditDocumentRequest }
     * 
     */
    public testEditDocumentRequest createtestEditDocumentRequest() {
        return new testEditDocumentRequest();
    }

    /**
     * Create an instance of {@link testFinishEditDocumentRequest }
     * 
     */
    public testFinishEditDocumentRequest createtestFinishEditDocumentRequest() {
        return new testFinishEditDocumentRequest();
    }

    /**
     * Create an instance of {@link testEditDocumentResponse }
     * 
     */
    public testEditDocumentResponse createtestEditDocumentResponse() {
        return new testEditDocumentResponse();
    }

    /**
     * Create an instance of {@link testFinishEditDocumentResponse }
     * 
     */
    public testFinishEditDocumentResponse createtestFinishEditDocumentResponse() {
        return new testFinishEditDocumentResponse();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link testFinishEditDocumentRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:zimbraAppblast", name = "FinishEditDocumentRequest")
    public JAXBElement<testFinishEditDocumentRequest> createFinishEditDocumentRequest(testFinishEditDocumentRequest value) {
        return new JAXBElement<testFinishEditDocumentRequest>(_FinishEditDocumentRequest_QNAME, testFinishEditDocumentRequest.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link testEditDocumentResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:zimbraAppblast", name = "EditDocumentResponse")
    public JAXBElement<testEditDocumentResponse> createEditDocumentResponse(testEditDocumentResponse value) {
        return new JAXBElement<testEditDocumentResponse>(_EditDocumentResponse_QNAME, testEditDocumentResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link testEditDocumentRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:zimbraAppblast", name = "EditDocumentRequest")
    public JAXBElement<testEditDocumentRequest> createEditDocumentRequest(testEditDocumentRequest value) {
        return new JAXBElement<testEditDocumentRequest>(_EditDocumentRequest_QNAME, testEditDocumentRequest.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link testFinishEditDocumentResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:zimbraAppblast", name = "FinishEditDocumentResponse")
    public JAXBElement<testFinishEditDocumentResponse> createFinishEditDocumentResponse(testFinishEditDocumentResponse value) {
        return new JAXBElement<testFinishEditDocumentResponse>(_FinishEditDocumentResponse_QNAME, testFinishEditDocumentResponse.class, null, value);
    }

}
