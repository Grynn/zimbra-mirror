
package zimbra.generated.syncclient.sync;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the zimbra.generated.syncclient.sync package. 
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

    private final static QName _SuspendDeviceResponse_QNAME = new QName("urn:zimbraSync", "SuspendDeviceResponse");
    private final static QName _RemoveDeviceResponse_QNAME = new QName("urn:zimbraSync", "RemoveDeviceResponse");
    private final static QName _ResumeDeviceResponse_QNAME = new QName("urn:zimbraSync", "ResumeDeviceResponse");
    private final static QName _GetDeviceStatusResponse_QNAME = new QName("urn:zimbraSync", "GetDeviceStatusResponse");
    private final static QName _GetDeviceStatusRequest_QNAME = new QName("urn:zimbraSync", "GetDeviceStatusRequest");
    private final static QName _RemoteWipeResponse_QNAME = new QName("urn:zimbraSync", "RemoteWipeResponse");
    private final static QName _RemoveDeviceRequest_QNAME = new QName("urn:zimbraSync", "RemoveDeviceRequest");
    private final static QName _ResumeDeviceRequest_QNAME = new QName("urn:zimbraSync", "ResumeDeviceRequest");
    private final static QName _SuspendDeviceRequest_QNAME = new QName("urn:zimbraSync", "SuspendDeviceRequest");
    private final static QName _RemoteWipeRequest_QNAME = new QName("urn:zimbraSync", "RemoteWipeRequest");
    private final static QName _CancelPendingRemoteWipeResponse_QNAME = new QName("urn:zimbraSync", "CancelPendingRemoteWipeResponse");
    private final static QName _CancelPendingRemoteWipeRequest_QNAME = new QName("urn:zimbraSync", "CancelPendingRemoteWipeRequest");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: zimbra.generated.syncclient.sync
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link testResumeDeviceRequest }
     * 
     */
    public testResumeDeviceRequest createtestResumeDeviceRequest() {
        return new testResumeDeviceRequest();
    }

    /**
     * Create an instance of {@link testResumeDeviceResponse }
     * 
     */
    public testResumeDeviceResponse createtestResumeDeviceResponse() {
        return new testResumeDeviceResponse();
    }

    /**
     * Create an instance of {@link testRemoveDeviceResponse }
     * 
     */
    public testRemoveDeviceResponse createtestRemoveDeviceResponse() {
        return new testRemoveDeviceResponse();
    }

    /**
     * Create an instance of {@link testDeviceId }
     * 
     */
    public testDeviceId createtestDeviceId() {
        return new testDeviceId();
    }

    /**
     * Create an instance of {@link testGetDeviceStatusRequest }
     * 
     */
    public testGetDeviceStatusRequest createtestGetDeviceStatusRequest() {
        return new testGetDeviceStatusRequest();
    }

    /**
     * Create an instance of {@link testGetDeviceStatusResponse }
     * 
     */
    public testGetDeviceStatusResponse createtestGetDeviceStatusResponse() {
        return new testGetDeviceStatusResponse();
    }

    /**
     * Create an instance of {@link testSuspendDeviceRequest }
     * 
     */
    public testSuspendDeviceRequest createtestSuspendDeviceRequest() {
        return new testSuspendDeviceRequest();
    }

    /**
     * Create an instance of {@link testRemoteWipeRequest }
     * 
     */
    public testRemoteWipeRequest createtestRemoteWipeRequest() {
        return new testRemoteWipeRequest();
    }

    /**
     * Create an instance of {@link testSuspendDeviceResponse }
     * 
     */
    public testSuspendDeviceResponse createtestSuspendDeviceResponse() {
        return new testSuspendDeviceResponse();
    }

    /**
     * Create an instance of {@link testDeviceStatusInfo }
     * 
     */
    public testDeviceStatusInfo createtestDeviceStatusInfo() {
        return new testDeviceStatusInfo();
    }

    /**
     * Create an instance of {@link testRemoveDeviceRequest }
     * 
     */
    public testRemoveDeviceRequest createtestRemoveDeviceRequest() {
        return new testRemoveDeviceRequest();
    }

    /**
     * Create an instance of {@link testCancelPendingRemoteWipeResponse }
     * 
     */
    public testCancelPendingRemoteWipeResponse createtestCancelPendingRemoteWipeResponse() {
        return new testCancelPendingRemoteWipeResponse();
    }

    /**
     * Create an instance of {@link testRemoteWipeResponse }
     * 
     */
    public testRemoteWipeResponse createtestRemoteWipeResponse() {
        return new testRemoteWipeResponse();
    }

    /**
     * Create an instance of {@link testCancelPendingRemoteWipeRequest }
     * 
     */
    public testCancelPendingRemoteWipeRequest createtestCancelPendingRemoteWipeRequest() {
        return new testCancelPendingRemoteWipeRequest();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link testSuspendDeviceResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:zimbraSync", name = "SuspendDeviceResponse")
    public JAXBElement<testSuspendDeviceResponse> createSuspendDeviceResponse(testSuspendDeviceResponse value) {
        return new JAXBElement<testSuspendDeviceResponse>(_SuspendDeviceResponse_QNAME, testSuspendDeviceResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link testRemoveDeviceResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:zimbraSync", name = "RemoveDeviceResponse")
    public JAXBElement<testRemoveDeviceResponse> createRemoveDeviceResponse(testRemoveDeviceResponse value) {
        return new JAXBElement<testRemoveDeviceResponse>(_RemoveDeviceResponse_QNAME, testRemoveDeviceResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link testResumeDeviceResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:zimbraSync", name = "ResumeDeviceResponse")
    public JAXBElement<testResumeDeviceResponse> createResumeDeviceResponse(testResumeDeviceResponse value) {
        return new JAXBElement<testResumeDeviceResponse>(_ResumeDeviceResponse_QNAME, testResumeDeviceResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link testGetDeviceStatusResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:zimbraSync", name = "GetDeviceStatusResponse")
    public JAXBElement<testGetDeviceStatusResponse> createGetDeviceStatusResponse(testGetDeviceStatusResponse value) {
        return new JAXBElement<testGetDeviceStatusResponse>(_GetDeviceStatusResponse_QNAME, testGetDeviceStatusResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link testGetDeviceStatusRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:zimbraSync", name = "GetDeviceStatusRequest")
    public JAXBElement<testGetDeviceStatusRequest> createGetDeviceStatusRequest(testGetDeviceStatusRequest value) {
        return new JAXBElement<testGetDeviceStatusRequest>(_GetDeviceStatusRequest_QNAME, testGetDeviceStatusRequest.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link testRemoteWipeResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:zimbraSync", name = "RemoteWipeResponse")
    public JAXBElement<testRemoteWipeResponse> createRemoteWipeResponse(testRemoteWipeResponse value) {
        return new JAXBElement<testRemoteWipeResponse>(_RemoteWipeResponse_QNAME, testRemoteWipeResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link testRemoveDeviceRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:zimbraSync", name = "RemoveDeviceRequest")
    public JAXBElement<testRemoveDeviceRequest> createRemoveDeviceRequest(testRemoveDeviceRequest value) {
        return new JAXBElement<testRemoveDeviceRequest>(_RemoveDeviceRequest_QNAME, testRemoveDeviceRequest.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link testResumeDeviceRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:zimbraSync", name = "ResumeDeviceRequest")
    public JAXBElement<testResumeDeviceRequest> createResumeDeviceRequest(testResumeDeviceRequest value) {
        return new JAXBElement<testResumeDeviceRequest>(_ResumeDeviceRequest_QNAME, testResumeDeviceRequest.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link testSuspendDeviceRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:zimbraSync", name = "SuspendDeviceRequest")
    public JAXBElement<testSuspendDeviceRequest> createSuspendDeviceRequest(testSuspendDeviceRequest value) {
        return new JAXBElement<testSuspendDeviceRequest>(_SuspendDeviceRequest_QNAME, testSuspendDeviceRequest.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link testRemoteWipeRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:zimbraSync", name = "RemoteWipeRequest")
    public JAXBElement<testRemoteWipeRequest> createRemoteWipeRequest(testRemoteWipeRequest value) {
        return new JAXBElement<testRemoteWipeRequest>(_RemoteWipeRequest_QNAME, testRemoteWipeRequest.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link testCancelPendingRemoteWipeResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:zimbraSync", name = "CancelPendingRemoteWipeResponse")
    public JAXBElement<testCancelPendingRemoteWipeResponse> createCancelPendingRemoteWipeResponse(testCancelPendingRemoteWipeResponse value) {
        return new JAXBElement<testCancelPendingRemoteWipeResponse>(_CancelPendingRemoteWipeResponse_QNAME, testCancelPendingRemoteWipeResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link testCancelPendingRemoteWipeRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:zimbraSync", name = "CancelPendingRemoteWipeRequest")
    public JAXBElement<testCancelPendingRemoteWipeRequest> createCancelPendingRemoteWipeRequest(testCancelPendingRemoteWipeRequest value) {
        return new JAXBElement<testCancelPendingRemoteWipeRequest>(_CancelPendingRemoteWipeRequest_QNAME, testCancelPendingRemoteWipeRequest.class, null, value);
    }

}
