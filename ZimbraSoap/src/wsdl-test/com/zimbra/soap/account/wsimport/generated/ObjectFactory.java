
package com.zimbra.soap.account.wsimport.generated;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.zimbra.soap.account.wsimport.generated package. 
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

    private final static QName _GetInfoRequest_QNAME = new QName("urn:zimbraAccount", "GetInfoRequest");
    private final static QName _GetSignaturesRequest_QNAME = new QName("urn:zimbraAccount", "GetSignaturesRequest");
    private final static QName _Contacts_QNAME = new QName("urn:zimbraAccount", "contacts");
    private final static QName _GetSignaturesResponse_QNAME = new QName("urn:zimbraAccount", "GetSignaturesResponse");
    private final static QName _GetIdentitiesRequest_QNAME = new QName("urn:zimbraAccount", "GetIdentitiesRequest");
    private final static QName _GetInfoResponse_QNAME = new QName("urn:zimbraAccount", "GetInfoResponse");
    private final static QName _GetPrefsRequest_QNAME = new QName("urn:zimbraAccount", "GetPrefsRequest");
    private final static QName _AccountDataSource_QNAME = new QName("urn:zimbraAccount", "accountDataSource");
    private final static QName _GetIdentitiesResponse_QNAME = new QName("urn:zimbraAccount", "GetIdentitiesResponse");
    private final static QName _AuthRequest_QNAME = new QName("urn:zimbraAccount", "AuthRequest");
    private final static QName _GetPrefsResponse_QNAME = new QName("urn:zimbraAccount", "GetPrefsResponse");
    private final static QName _ChangePasswordResponse_QNAME = new QName("urn:zimbraAccount", "ChangePasswordResponse");
    private final static QName _ChangePasswordRequest_QNAME = new QName("urn:zimbraAccount", "ChangePasswordRequest");
    private final static QName _AuthResponse_QNAME = new QName("urn:zimbraAccount", "AuthResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.zimbra.soap.account.wsimport.generated
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link ChangePasswordResponse }
     * 
     */
    public ChangePasswordResponse createChangePasswordResponse() {
        return new ChangePasswordResponse();
    }

    /**
     * Create an instance of {@link GetInfoResponse.Props }
     * 
     */
    public GetInfoResponse.Props createGetInfoResponseProps() {
        return new GetInfoResponse.Props();
    }

    /**
     * Create an instance of {@link GetInfoResponse.Prefs }
     * 
     */
    public GetInfoResponse.Prefs createGetInfoResponsePrefs() {
        return new GetInfoResponse.Prefs();
    }

    /**
     * Create an instance of {@link AccountPop3DataSource }
     * 
     */
    public AccountPop3DataSource createAccountPop3DataSource() {
        return new AccountPop3DataSource();
    }

    /**
     * Create an instance of {@link GetInfoResponse.Identities }
     * 
     */
    public GetInfoResponse.Identities createGetInfoResponseIdentities() {
        return new GetInfoResponse.Identities();
    }

    /**
     * Create an instance of {@link Account }
     * 
     */
    public Account createAccount() {
        return new Account();
    }

    /**
     * Create an instance of {@link AuthResponse }
     * 
     */
    public AuthResponse createAuthResponse() {
        return new AuthResponse();
    }

    /**
     * Create an instance of {@link GetInfoResponse.Signatures }
     * 
     */
    public GetInfoResponse.Signatures createGetInfoResponseSignatures() {
        return new GetInfoResponse.Signatures();
    }

    /**
     * Create an instance of {@link Pref }
     * 
     */
    public Pref createPref() {
        return new Pref();
    }

    /**
     * Create an instance of {@link Prop }
     * 
     */
    public Prop createProp() {
        return new Prop();
    }

    /**
     * Create an instance of {@link AccountRssDataSource }
     * 
     */
    public AccountRssDataSource createAccountRssDataSource() {
        return new AccountRssDataSource();
    }

    /**
     * Create an instance of {@link GetIdentitiesResponse }
     * 
     */
    public GetIdentitiesResponse createGetIdentitiesResponse() {
        return new GetIdentitiesResponse();
    }

    /**
     * Create an instance of {@link ChildAccount }
     * 
     */
    public ChildAccount createChildAccount() {
        return new ChildAccount();
    }

    /**
     * Create an instance of {@link PreAuth }
     * 
     */
    public PreAuth createPreAuth() {
        return new PreAuth();
    }

    /**
     * Create an instance of {@link AccountImapDataSource }
     * 
     */
    public AccountImapDataSource createAccountImapDataSource() {
        return new AccountImapDataSource();
    }

    /**
     * Create an instance of {@link AuthRequest }
     * 
     */
    public AuthRequest createAuthRequest() {
        return new AuthRequest();
    }

    /**
     * Create an instance of {@link Session }
     * 
     */
    public Session createSession() {
        return new Session();
    }

    /**
     * Create an instance of {@link AccountDataSource }
     * 
     */
    public AccountDataSource createAccountDataSource() {
        return new AccountDataSource();
    }

    /**
     * Create an instance of {@link AccountCalDataSource }
     * 
     */
    public AccountCalDataSource createAccountCalDataSource() {
        return new AccountCalDataSource();
    }

    /**
     * Create an instance of {@link GetInfoResponse.DataSources }
     * 
     */
    public GetInfoResponse.DataSources createGetInfoResponseDataSources() {
        return new GetInfoResponse.DataSources();
    }

    /**
     * Create an instance of {@link AuthResponse.Prefs }
     * 
     */
    public AuthResponse.Prefs createAuthResponsePrefs() {
        return new AuthResponse.Prefs();
    }

    /**
     * Create an instance of {@link GetSignaturesResponse }
     * 
     */
    public GetSignaturesResponse createGetSignaturesResponse() {
        return new GetSignaturesResponse();
    }

    /**
     * Create an instance of {@link ChildAccount.Attrs }
     * 
     */
    public ChildAccount.Attrs createChildAccountAttrs() {
        return new ChildAccount.Attrs();
    }

    /**
     * Create an instance of {@link GetInfoResponse.Attrs }
     * 
     */
    public GetInfoResponse.Attrs createGetInfoResponseAttrs() {
        return new GetInfoResponse.Attrs();
    }

    /**
     * Create an instance of {@link GetPrefsRequest }
     * 
     */
    public GetPrefsRequest createGetPrefsRequest() {
        return new GetPrefsRequest();
    }

    /**
     * Create an instance of {@link SignatureContent }
     * 
     */
    public SignatureContent createSignatureContent() {
        return new SignatureContent();
    }

    /**
     * Create an instance of {@link GetSignaturesRequest }
     * 
     */
    public GetSignaturesRequest createGetSignaturesRequest() {
        return new GetSignaturesRequest();
    }

    /**
     * Create an instance of {@link GetIdentitiesRequest }
     * 
     */
    public GetIdentitiesRequest createGetIdentitiesRequest() {
        return new GetIdentitiesRequest();
    }

    /**
     * Create an instance of {@link AuthRequest.Prefs }
     * 
     */
    public AuthRequest.Prefs createAuthRequestPrefs() {
        return new AuthRequest.Prefs();
    }

    /**
     * Create an instance of {@link GetInfoResponse }
     * 
     */
    public GetInfoResponse createGetInfoResponse() {
        return new GetInfoResponse();
    }

    /**
     * Create an instance of {@link Attr }
     * 
     */
    public Attr createAttr() {
        return new Attr();
    }

    /**
     * Create an instance of {@link GetInfoRequest }
     * 
     */
    public GetInfoRequest createGetInfoRequest() {
        return new GetInfoRequest();
    }

    /**
     * Create an instance of {@link GetPrefsResponse }
     * 
     */
    public GetPrefsResponse createGetPrefsResponse() {
        return new GetPrefsResponse();
    }

    /**
     * Create an instance of {@link Signature }
     * 
     */
    public Signature createSignature() {
        return new Signature();
    }

    /**
     * Create an instance of {@link Cos }
     * 
     */
    public Cos createCos() {
        return new Cos();
    }

    /**
     * Create an instance of {@link AuthResponse.Attrs }
     * 
     */
    public AuthResponse.Attrs createAuthResponseAttrs() {
        return new AuthResponse.Attrs();
    }

    /**
     * Create an instance of {@link AuthRequest.Attrs }
     * 
     */
    public AuthRequest.Attrs createAuthRequestAttrs() {
        return new AuthRequest.Attrs();
    }

    /**
     * Create an instance of {@link Identity }
     * 
     */
    public Identity createIdentity() {
        return new Identity();
    }

    /**
     * Create an instance of {@link ChangePasswordRequest }
     * 
     */
    public ChangePasswordRequest createChangePasswordRequest() {
        return new ChangePasswordRequest();
    }

    /**
     * Create an instance of {@link AccountContactsDataSource }
     * 
     */
    public AccountContactsDataSource createAccountContactsDataSource() {
        return new AccountContactsDataSource();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetInfoRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:zimbraAccount", name = "GetInfoRequest")
    public JAXBElement<GetInfoRequest> createGetInfoRequest(GetInfoRequest value) {
        return new JAXBElement<GetInfoRequest>(_GetInfoRequest_QNAME, GetInfoRequest.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetSignaturesRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:zimbraAccount", name = "GetSignaturesRequest")
    public JAXBElement<GetSignaturesRequest> createGetSignaturesRequest(GetSignaturesRequest value) {
        return new JAXBElement<GetSignaturesRequest>(_GetSignaturesRequest_QNAME, GetSignaturesRequest.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AccountContactsDataSource }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:zimbraAccount", name = "contacts")
    public JAXBElement<AccountContactsDataSource> createContacts(AccountContactsDataSource value) {
        return new JAXBElement<AccountContactsDataSource>(_Contacts_QNAME, AccountContactsDataSource.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetSignaturesResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:zimbraAccount", name = "GetSignaturesResponse")
    public JAXBElement<GetSignaturesResponse> createGetSignaturesResponse(GetSignaturesResponse value) {
        return new JAXBElement<GetSignaturesResponse>(_GetSignaturesResponse_QNAME, GetSignaturesResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetIdentitiesRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:zimbraAccount", name = "GetIdentitiesRequest")
    public JAXBElement<GetIdentitiesRequest> createGetIdentitiesRequest(GetIdentitiesRequest value) {
        return new JAXBElement<GetIdentitiesRequest>(_GetIdentitiesRequest_QNAME, GetIdentitiesRequest.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetInfoResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:zimbraAccount", name = "GetInfoResponse")
    public JAXBElement<GetInfoResponse> createGetInfoResponse(GetInfoResponse value) {
        return new JAXBElement<GetInfoResponse>(_GetInfoResponse_QNAME, GetInfoResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetPrefsRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:zimbraAccount", name = "GetPrefsRequest")
    public JAXBElement<GetPrefsRequest> createGetPrefsRequest(GetPrefsRequest value) {
        return new JAXBElement<GetPrefsRequest>(_GetPrefsRequest_QNAME, GetPrefsRequest.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AccountDataSource }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:zimbraAccount", name = "accountDataSource")
    public JAXBElement<AccountDataSource> createAccountDataSource(AccountDataSource value) {
        return new JAXBElement<AccountDataSource>(_AccountDataSource_QNAME, AccountDataSource.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetIdentitiesResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:zimbraAccount", name = "GetIdentitiesResponse")
    public JAXBElement<GetIdentitiesResponse> createGetIdentitiesResponse(GetIdentitiesResponse value) {
        return new JAXBElement<GetIdentitiesResponse>(_GetIdentitiesResponse_QNAME, GetIdentitiesResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AuthRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:zimbraAccount", name = "AuthRequest")
    public JAXBElement<AuthRequest> createAuthRequest(AuthRequest value) {
        return new JAXBElement<AuthRequest>(_AuthRequest_QNAME, AuthRequest.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetPrefsResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:zimbraAccount", name = "GetPrefsResponse")
    public JAXBElement<GetPrefsResponse> createGetPrefsResponse(GetPrefsResponse value) {
        return new JAXBElement<GetPrefsResponse>(_GetPrefsResponse_QNAME, GetPrefsResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ChangePasswordResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:zimbraAccount", name = "ChangePasswordResponse")
    public JAXBElement<ChangePasswordResponse> createChangePasswordResponse(ChangePasswordResponse value) {
        return new JAXBElement<ChangePasswordResponse>(_ChangePasswordResponse_QNAME, ChangePasswordResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ChangePasswordRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:zimbraAccount", name = "ChangePasswordRequest")
    public JAXBElement<ChangePasswordRequest> createChangePasswordRequest(ChangePasswordRequest value) {
        return new JAXBElement<ChangePasswordRequest>(_ChangePasswordRequest_QNAME, ChangePasswordRequest.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AuthResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:zimbraAccount", name = "AuthResponse")
    public JAXBElement<AuthResponse> createAuthResponse(AuthResponse value) {
        return new JAXBElement<AuthResponse>(_AuthResponse_QNAME, AuthResponse.class, null, value);
    }

}
