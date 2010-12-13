
package com.zimbra.soap.admin.wsimport.generated;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.zimbra.soap.admin.wsimport.generated package. 
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

    private final static QName _ReloadLocalConfigResponse_QNAME = new QName("urn:zimbraAdmin", "ReloadLocalConfigResponse");
    private final static QName _Context_QNAME = new QName("urn:zimbra", "context");
    private final static QName _CreateAccountResponse_QNAME = new QName("urn:zimbraAdmin", "CreateAccountResponse");
    private final static QName _GetAccountRequest_QNAME = new QName("urn:zimbraAdmin", "GetAccountRequest");
    private final static QName _A_QNAME = new QName("urn:zimbraAdmin", "a");
    private final static QName _CreateAccountRequest_QNAME = new QName("urn:zimbraAdmin", "CreateAccountRequest");
    private final static QName _Account_QNAME = new QName("urn:zimbraAdmin", "account");
    private final static QName _GetAccountResponse_QNAME = new QName("urn:zimbraAdmin", "GetAccountResponse");
    private final static QName _AuthResponse_QNAME = new QName("urn:zimbraAdmin", "AuthResponse");
    private final static QName _ReloadLocalConfigRequest_QNAME = new QName("urn:zimbraAdmin", "ReloadLocalConfigRequest");
    private final static QName _AuthRequest_QNAME = new QName("urn:zimbraAdmin", "AuthRequest");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.zimbra.soap.admin.wsimport.generated
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link AuthResponse }
     * 
     */
    public AuthResponse createAuthResponse() {
        return new AuthResponse();
    }

    /**
     * Create an instance of {@link GetAccountResponse }
     * 
     */
    public GetAccountResponse createGetAccountResponse() {
        return new GetAccountResponse();
    }

    /**
     * Create an instance of {@link ReloadLocalConfigResponse }
     * 
     */
    public ReloadLocalConfigResponse createReloadLocalConfigResponse() {
        return new ReloadLocalConfigResponse();
    }

    /**
     * Create an instance of {@link AuthRequest }
     * 
     */
    public AuthRequest createAuthRequest() {
        return new AuthRequest();
    }

    /**
     * Create an instance of {@link AccountInfo }
     * 
     */
    public AccountInfo createAccountInfo() {
        return new AccountInfo();
    }

    /**
     * Create an instance of {@link Attr }
     * 
     */
    public Attr createAttr() {
        return new Attr();
    }

    /**
     * Create an instance of {@link CreateAccountResponse }
     * 
     */
    public CreateAccountResponse createCreateAccountResponse() {
        return new CreateAccountResponse();
    }

    /**
     * Create an instance of {@link HeaderContext }
     * 
     */
    public HeaderContext createHeaderContext() {
        return new HeaderContext();
    }

    /**
     * Create an instance of {@link CreateAccountRequest }
     * 
     */
    public CreateAccountRequest createCreateAccountRequest() {
        return new CreateAccountRequest();
    }

    /**
     * Create an instance of {@link GetAccountRequest }
     * 
     */
    public GetAccountRequest createGetAccountRequest() {
        return new GetAccountRequest();
    }

    /**
     * Create an instance of {@link Account }
     * 
     */
    public Account createAccount() {
        return new Account();
    }

    /**
     * Create an instance of {@link ReloadLocalConfigRequest }
     * 
     */
    public ReloadLocalConfigRequest createReloadLocalConfigRequest() {
        return new ReloadLocalConfigRequest();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ReloadLocalConfigResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:zimbraAdmin", name = "ReloadLocalConfigResponse")
    public JAXBElement<ReloadLocalConfigResponse> createReloadLocalConfigResponse(ReloadLocalConfigResponse value) {
        return new JAXBElement<ReloadLocalConfigResponse>(_ReloadLocalConfigResponse_QNAME, ReloadLocalConfigResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link HeaderContext }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:zimbra", name = "context")
    public JAXBElement<HeaderContext> createContext(HeaderContext value) {
        return new JAXBElement<HeaderContext>(_Context_QNAME, HeaderContext.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CreateAccountResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:zimbraAdmin", name = "CreateAccountResponse")
    public JAXBElement<CreateAccountResponse> createCreateAccountResponse(CreateAccountResponse value) {
        return new JAXBElement<CreateAccountResponse>(_CreateAccountResponse_QNAME, CreateAccountResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetAccountRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:zimbraAdmin", name = "GetAccountRequest")
    public JAXBElement<GetAccountRequest> createGetAccountRequest(GetAccountRequest value) {
        return new JAXBElement<GetAccountRequest>(_GetAccountRequest_QNAME, GetAccountRequest.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Attr }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:zimbraAdmin", name = "a")
    public JAXBElement<Attr> createA(Attr value) {
        return new JAXBElement<Attr>(_A_QNAME, Attr.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CreateAccountRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:zimbraAdmin", name = "CreateAccountRequest")
    public JAXBElement<CreateAccountRequest> createCreateAccountRequest(CreateAccountRequest value) {
        return new JAXBElement<CreateAccountRequest>(_CreateAccountRequest_QNAME, CreateAccountRequest.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AccountInfo }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:zimbraAdmin", name = "account")
    public JAXBElement<AccountInfo> createAccount(AccountInfo value) {
        return new JAXBElement<AccountInfo>(_Account_QNAME, AccountInfo.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetAccountResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:zimbraAdmin", name = "GetAccountResponse")
    public JAXBElement<GetAccountResponse> createGetAccountResponse(GetAccountResponse value) {
        return new JAXBElement<GetAccountResponse>(_GetAccountResponse_QNAME, GetAccountResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AuthResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:zimbraAdmin", name = "AuthResponse")
    public JAXBElement<AuthResponse> createAuthResponse(AuthResponse value) {
        return new JAXBElement<AuthResponse>(_AuthResponse_QNAME, AuthResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ReloadLocalConfigRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:zimbraAdmin", name = "ReloadLocalConfigRequest")
    public JAXBElement<ReloadLocalConfigRequest> createReloadLocalConfigRequest(ReloadLocalConfigRequest value) {
        return new JAXBElement<ReloadLocalConfigRequest>(_ReloadLocalConfigRequest_QNAME, ReloadLocalConfigRequest.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AuthRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:zimbraAdmin", name = "AuthRequest")
    public JAXBElement<AuthRequest> createAuthRequest(AuthRequest value) {
        return new JAXBElement<AuthRequest>(_AuthRequest_QNAME, AuthRequest.class, null, value);
    }

}
