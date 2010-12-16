
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

    private final static QName _Context_QNAME = new QName("urn:zimbra", "context");
    private final static QName _CreateAccountResponse_QNAME = new QName("urn:zimbraAdmin", "CreateAccountResponse");
    private final static QName _GetAccountRequest_QNAME = new QName("urn:zimbraAdmin", "GetAccountRequest");
    private final static QName _AdminAttrsImpl_QNAME = new QName("urn:zimbraAdmin", "adminAttrsImpl");
    private final static QName _GetServerResponse_QNAME = new QName("urn:zimbraAdmin", "GetServerResponse");
    private final static QName _ModifyDomainRequest_QNAME = new QName("urn:zimbraAdmin", "ModifyDomainRequest");
    private final static QName _GetDomainResponse_QNAME = new QName("urn:zimbraAdmin", "GetDomainResponse");
    private final static QName _GetAllServersRequest_QNAME = new QName("urn:zimbraAdmin", "GetAllServersRequest");
    private final static QName _CreateDomainResponse_QNAME = new QName("urn:zimbraAdmin", "CreateDomainResponse");
    private final static QName _ModifyDomainResponse_QNAME = new QName("urn:zimbraAdmin", "ModifyDomainResponse");
    private final static QName _Domain_QNAME = new QName("urn:zimbraAdmin", "domain");
    private final static QName _CreateAccountRequest_QNAME = new QName("urn:zimbraAdmin", "CreateAccountRequest");
    private final static QName _GetServerRequest_QNAME = new QName("urn:zimbraAdmin", "GetServerRequest");
    private final static QName _AuthResponse_QNAME = new QName("urn:zimbraAdmin", "AuthResponse");
    private final static QName _AuthRequest_QNAME = new QName("urn:zimbraAdmin", "AuthRequest");
    private final static QName _DeleteDomainResponse_QNAME = new QName("urn:zimbraAdmin", "DeleteDomainResponse");
    private final static QName _AttributeSelectorImpl_QNAME = new QName("urn:zimbraAdmin", "attributeSelectorImpl");
    private final static QName _CreateDomainRequest_QNAME = new QName("urn:zimbraAdmin", "CreateDomainRequest");
    private final static QName _ModifyServerResponse_QNAME = new QName("urn:zimbraAdmin", "ModifyServerResponse");
    private final static QName _ReloadLocalConfigResponse_QNAME = new QName("urn:zimbraAdmin", "ReloadLocalConfigResponse");
    private final static QName _DeleteServerResponse_QNAME = new QName("urn:zimbraAdmin", "DeleteServerResponse");
    private final static QName _A_QNAME = new QName("urn:zimbraAdmin", "a");
    private final static QName _GetDomainInfoResponse_QNAME = new QName("urn:zimbraAdmin", "GetDomainInfoResponse");
    private final static QName _DeleteDomainRequest_QNAME = new QName("urn:zimbraAdmin", "DeleteDomainRequest");
    private final static QName _GetAccountResponse_QNAME = new QName("urn:zimbraAdmin", "GetAccountResponse");
    private final static QName _CreateServerResponse_QNAME = new QName("urn:zimbraAdmin", "CreateServerResponse");
    private final static QName _ReloadLocalConfigRequest_QNAME = new QName("urn:zimbraAdmin", "ReloadLocalConfigRequest");
    private final static QName _ModifyServerRequest_QNAME = new QName("urn:zimbraAdmin", "ModifyServerRequest");
    private final static QName _GetAllDomainsResponse_QNAME = new QName("urn:zimbraAdmin", "GetAllDomainsResponse");
    private final static QName _CreateServerRequest_QNAME = new QName("urn:zimbraAdmin", "CreateServerRequest");
    private final static QName _GetDomainRequest_QNAME = new QName("urn:zimbraAdmin", "GetDomainRequest");
    private final static QName _DeleteServerRequest_QNAME = new QName("urn:zimbraAdmin", "DeleteServerRequest");
    private final static QName _Account_QNAME = new QName("urn:zimbraAdmin", "account");
    private final static QName _GetAllServersResponse_QNAME = new QName("urn:zimbraAdmin", "GetAllServersResponse");
    private final static QName _Server_QNAME = new QName("urn:zimbraAdmin", "server");
    private final static QName _GetDomainInfoRequest_QNAME = new QName("urn:zimbraAdmin", "GetDomainInfoRequest");
    private final static QName _GetAllDomainsRequest_QNAME = new QName("urn:zimbraAdmin", "GetAllDomainsRequest");

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
     * Create an instance of {@link ServerInfo }
     * 
     */
    public ServerInfo createServerInfo() {
        return new ServerInfo();
    }

    /**
     * Create an instance of {@link ModifyDomainRequest }
     * 
     */
    public ModifyDomainRequest createModifyDomainRequest() {
        return new ModifyDomainRequest();
    }

    /**
     * Create an instance of {@link DeleteDomainResponse }
     * 
     */
    public DeleteDomainResponse createDeleteDomainResponse() {
        return new DeleteDomainResponse();
    }

    /**
     * Create an instance of {@link DeleteServerRequest }
     * 
     */
    public DeleteServerRequest createDeleteServerRequest() {
        return new DeleteServerRequest();
    }

    /**
     * Create an instance of {@link ReloadLocalConfigRequest }
     * 
     */
    public ReloadLocalConfigRequest createReloadLocalConfigRequest() {
        return new ReloadLocalConfigRequest();
    }

    /**
     * Create an instance of {@link Account }
     * 
     */
    public Account createAccount() {
        return new Account();
    }

    /**
     * Create an instance of {@link GetDomainInfoResponse }
     * 
     */
    public GetDomainInfoResponse createGetDomainInfoResponse() {
        return new GetDomainInfoResponse();
    }

    /**
     * Create an instance of {@link GetServerResponse }
     * 
     */
    public GetServerResponse createGetServerResponse() {
        return new GetServerResponse();
    }

    /**
     * Create an instance of {@link GetAllDomainsResponse }
     * 
     */
    public GetAllDomainsResponse createGetAllDomainsResponse() {
        return new GetAllDomainsResponse();
    }

    /**
     * Create an instance of {@link GetAccountResponse }
     * 
     */
    public GetAccountResponse createGetAccountResponse() {
        return new GetAccountResponse();
    }

    /**
     * Create an instance of {@link CreateServerRequest }
     * 
     */
    public CreateServerRequest createCreateServerRequest() {
        return new CreateServerRequest();
    }

    /**
     * Create an instance of {@link ModifyServerRequest }
     * 
     */
    public ModifyServerRequest createModifyServerRequest() {
        return new ModifyServerRequest();
    }

    /**
     * Create an instance of {@link GetDomainResponse }
     * 
     */
    public GetDomainResponse createGetDomainResponse() {
        return new GetDomainResponse();
    }

    /**
     * Create an instance of {@link CreateDomainRequest }
     * 
     */
    public CreateDomainRequest createCreateDomainRequest() {
        return new CreateDomainRequest();
    }

    /**
     * Create an instance of {@link DomainInfo }
     * 
     */
    public DomainInfo createDomainInfo() {
        return new DomainInfo();
    }

    /**
     * Create an instance of {@link CreateAccountRequest }
     * 
     */
    public CreateAccountRequest createCreateAccountRequest() {
        return new CreateAccountRequest();
    }

    /**
     * Create an instance of {@link GetDomainInfoRequest }
     * 
     */
    public GetDomainInfoRequest createGetDomainInfoRequest() {
        return new GetDomainInfoRequest();
    }

    /**
     * Create an instance of {@link GetAccountRequest }
     * 
     */
    public GetAccountRequest createGetAccountRequest() {
        return new GetAccountRequest();
    }

    /**
     * Create an instance of {@link ReloadLocalConfigResponse }
     * 
     */
    public ReloadLocalConfigResponse createReloadLocalConfigResponse() {
        return new ReloadLocalConfigResponse();
    }

    /**
     * Create an instance of {@link ModifyDomainResponse }
     * 
     */
    public ModifyDomainResponse createModifyDomainResponse() {
        return new ModifyDomainResponse();
    }

    /**
     * Create an instance of {@link DeleteDomainRequest }
     * 
     */
    public DeleteDomainRequest createDeleteDomainRequest() {
        return new DeleteDomainRequest();
    }

    /**
     * Create an instance of {@link GetAllDomainsRequest }
     * 
     */
    public GetAllDomainsRequest createGetAllDomainsRequest() {
        return new GetAllDomainsRequest();
    }

    /**
     * Create an instance of {@link GetDomainRequest }
     * 
     */
    public GetDomainRequest createGetDomainRequest() {
        return new GetDomainRequest();
    }

    /**
     * Create an instance of {@link ModifyServerResponse }
     * 
     */
    public ModifyServerResponse createModifyServerResponse() {
        return new ModifyServerResponse();
    }

    /**
     * Create an instance of {@link CreateServerResponse }
     * 
     */
    public CreateServerResponse createCreateServerResponse() {
        return new CreateServerResponse();
    }

    /**
     * Create an instance of {@link GetAllServersRequest }
     * 
     */
    public GetAllServersRequest createGetAllServersRequest() {
        return new GetAllServersRequest();
    }

    /**
     * Create an instance of {@link ServerSelector }
     * 
     */
    public ServerSelector createServerSelector() {
        return new ServerSelector();
    }

    /**
     * Create an instance of {@link GetAllServersResponse }
     * 
     */
    public GetAllServersResponse createGetAllServersResponse() {
        return new GetAllServersResponse();
    }

    /**
     * Create an instance of {@link CreateDomainResponse }
     * 
     */
    public CreateDomainResponse createCreateDomainResponse() {
        return new CreateDomainResponse();
    }

    /**
     * Create an instance of {@link Attr }
     * 
     */
    public Attr createAttr() {
        return new Attr();
    }

    /**
     * Create an instance of {@link HeaderContext }
     * 
     */
    public HeaderContext createHeaderContext() {
        return new HeaderContext();
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
     * Create an instance of {@link CreateAccountResponse }
     * 
     */
    public CreateAccountResponse createCreateAccountResponse() {
        return new CreateAccountResponse();
    }

    /**
     * Create an instance of {@link DeleteServerResponse }
     * 
     */
    public DeleteServerResponse createDeleteServerResponse() {
        return new DeleteServerResponse();
    }

    /**
     * Create an instance of {@link DomainSelector }
     * 
     */
    public DomainSelector createDomainSelector() {
        return new DomainSelector();
    }

    /**
     * Create an instance of {@link GetServerRequest }
     * 
     */
    public GetServerRequest createGetServerRequest() {
        return new GetServerRequest();
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
     * Create an instance of {@link JAXBElement }{@code <}{@link AdminAttrsImpl }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:zimbraAdmin", name = "adminAttrsImpl")
    public JAXBElement<AdminAttrsImpl> createAdminAttrsImpl(AdminAttrsImpl value) {
        return new JAXBElement<AdminAttrsImpl>(_AdminAttrsImpl_QNAME, AdminAttrsImpl.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetServerResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:zimbraAdmin", name = "GetServerResponse")
    public JAXBElement<GetServerResponse> createGetServerResponse(GetServerResponse value) {
        return new JAXBElement<GetServerResponse>(_GetServerResponse_QNAME, GetServerResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ModifyDomainRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:zimbraAdmin", name = "ModifyDomainRequest")
    public JAXBElement<ModifyDomainRequest> createModifyDomainRequest(ModifyDomainRequest value) {
        return new JAXBElement<ModifyDomainRequest>(_ModifyDomainRequest_QNAME, ModifyDomainRequest.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetDomainResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:zimbraAdmin", name = "GetDomainResponse")
    public JAXBElement<GetDomainResponse> createGetDomainResponse(GetDomainResponse value) {
        return new JAXBElement<GetDomainResponse>(_GetDomainResponse_QNAME, GetDomainResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetAllServersRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:zimbraAdmin", name = "GetAllServersRequest")
    public JAXBElement<GetAllServersRequest> createGetAllServersRequest(GetAllServersRequest value) {
        return new JAXBElement<GetAllServersRequest>(_GetAllServersRequest_QNAME, GetAllServersRequest.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CreateDomainResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:zimbraAdmin", name = "CreateDomainResponse")
    public JAXBElement<CreateDomainResponse> createCreateDomainResponse(CreateDomainResponse value) {
        return new JAXBElement<CreateDomainResponse>(_CreateDomainResponse_QNAME, CreateDomainResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ModifyDomainResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:zimbraAdmin", name = "ModifyDomainResponse")
    public JAXBElement<ModifyDomainResponse> createModifyDomainResponse(ModifyDomainResponse value) {
        return new JAXBElement<ModifyDomainResponse>(_ModifyDomainResponse_QNAME, ModifyDomainResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DomainInfo }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:zimbraAdmin", name = "domain")
    public JAXBElement<DomainInfo> createDomain(DomainInfo value) {
        return new JAXBElement<DomainInfo>(_Domain_QNAME, DomainInfo.class, null, value);
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
     * Create an instance of {@link JAXBElement }{@code <}{@link GetServerRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:zimbraAdmin", name = "GetServerRequest")
    public JAXBElement<GetServerRequest> createGetServerRequest(GetServerRequest value) {
        return new JAXBElement<GetServerRequest>(_GetServerRequest_QNAME, GetServerRequest.class, null, value);
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
     * Create an instance of {@link JAXBElement }{@code <}{@link AuthRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:zimbraAdmin", name = "AuthRequest")
    public JAXBElement<AuthRequest> createAuthRequest(AuthRequest value) {
        return new JAXBElement<AuthRequest>(_AuthRequest_QNAME, AuthRequest.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DeleteDomainResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:zimbraAdmin", name = "DeleteDomainResponse")
    public JAXBElement<DeleteDomainResponse> createDeleteDomainResponse(DeleteDomainResponse value) {
        return new JAXBElement<DeleteDomainResponse>(_DeleteDomainResponse_QNAME, DeleteDomainResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AttributeSelectorImpl }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:zimbraAdmin", name = "attributeSelectorImpl")
    public JAXBElement<AttributeSelectorImpl> createAttributeSelectorImpl(AttributeSelectorImpl value) {
        return new JAXBElement<AttributeSelectorImpl>(_AttributeSelectorImpl_QNAME, AttributeSelectorImpl.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CreateDomainRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:zimbraAdmin", name = "CreateDomainRequest")
    public JAXBElement<CreateDomainRequest> createCreateDomainRequest(CreateDomainRequest value) {
        return new JAXBElement<CreateDomainRequest>(_CreateDomainRequest_QNAME, CreateDomainRequest.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ModifyServerResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:zimbraAdmin", name = "ModifyServerResponse")
    public JAXBElement<ModifyServerResponse> createModifyServerResponse(ModifyServerResponse value) {
        return new JAXBElement<ModifyServerResponse>(_ModifyServerResponse_QNAME, ModifyServerResponse.class, null, value);
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
     * Create an instance of {@link JAXBElement }{@code <}{@link DeleteServerResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:zimbraAdmin", name = "DeleteServerResponse")
    public JAXBElement<DeleteServerResponse> createDeleteServerResponse(DeleteServerResponse value) {
        return new JAXBElement<DeleteServerResponse>(_DeleteServerResponse_QNAME, DeleteServerResponse.class, null, value);
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
     * Create an instance of {@link JAXBElement }{@code <}{@link GetDomainInfoResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:zimbraAdmin", name = "GetDomainInfoResponse")
    public JAXBElement<GetDomainInfoResponse> createGetDomainInfoResponse(GetDomainInfoResponse value) {
        return new JAXBElement<GetDomainInfoResponse>(_GetDomainInfoResponse_QNAME, GetDomainInfoResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DeleteDomainRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:zimbraAdmin", name = "DeleteDomainRequest")
    public JAXBElement<DeleteDomainRequest> createDeleteDomainRequest(DeleteDomainRequest value) {
        return new JAXBElement<DeleteDomainRequest>(_DeleteDomainRequest_QNAME, DeleteDomainRequest.class, null, value);
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
     * Create an instance of {@link JAXBElement }{@code <}{@link CreateServerResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:zimbraAdmin", name = "CreateServerResponse")
    public JAXBElement<CreateServerResponse> createCreateServerResponse(CreateServerResponse value) {
        return new JAXBElement<CreateServerResponse>(_CreateServerResponse_QNAME, CreateServerResponse.class, null, value);
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
     * Create an instance of {@link JAXBElement }{@code <}{@link ModifyServerRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:zimbraAdmin", name = "ModifyServerRequest")
    public JAXBElement<ModifyServerRequest> createModifyServerRequest(ModifyServerRequest value) {
        return new JAXBElement<ModifyServerRequest>(_ModifyServerRequest_QNAME, ModifyServerRequest.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetAllDomainsResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:zimbraAdmin", name = "GetAllDomainsResponse")
    public JAXBElement<GetAllDomainsResponse> createGetAllDomainsResponse(GetAllDomainsResponse value) {
        return new JAXBElement<GetAllDomainsResponse>(_GetAllDomainsResponse_QNAME, GetAllDomainsResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CreateServerRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:zimbraAdmin", name = "CreateServerRequest")
    public JAXBElement<CreateServerRequest> createCreateServerRequest(CreateServerRequest value) {
        return new JAXBElement<CreateServerRequest>(_CreateServerRequest_QNAME, CreateServerRequest.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetDomainRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:zimbraAdmin", name = "GetDomainRequest")
    public JAXBElement<GetDomainRequest> createGetDomainRequest(GetDomainRequest value) {
        return new JAXBElement<GetDomainRequest>(_GetDomainRequest_QNAME, GetDomainRequest.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DeleteServerRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:zimbraAdmin", name = "DeleteServerRequest")
    public JAXBElement<DeleteServerRequest> createDeleteServerRequest(DeleteServerRequest value) {
        return new JAXBElement<DeleteServerRequest>(_DeleteServerRequest_QNAME, DeleteServerRequest.class, null, value);
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
     * Create an instance of {@link JAXBElement }{@code <}{@link GetAllServersResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:zimbraAdmin", name = "GetAllServersResponse")
    public JAXBElement<GetAllServersResponse> createGetAllServersResponse(GetAllServersResponse value) {
        return new JAXBElement<GetAllServersResponse>(_GetAllServersResponse_QNAME, GetAllServersResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ServerInfo }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:zimbraAdmin", name = "server")
    public JAXBElement<ServerInfo> createServer(ServerInfo value) {
        return new JAXBElement<ServerInfo>(_Server_QNAME, ServerInfo.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetDomainInfoRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:zimbraAdmin", name = "GetDomainInfoRequest")
    public JAXBElement<GetDomainInfoRequest> createGetDomainInfoRequest(GetDomainInfoRequest value) {
        return new JAXBElement<GetDomainInfoRequest>(_GetDomainInfoRequest_QNAME, GetDomainInfoRequest.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetAllDomainsRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:zimbraAdmin", name = "GetAllDomainsRequest")
    public JAXBElement<GetAllDomainsRequest> createGetAllDomainsRequest(GetAllDomainsRequest value) {
        return new JAXBElement<GetAllDomainsRequest>(_GetAllDomainsRequest_QNAME, GetAllDomainsRequest.class, null, value);
    }

}
