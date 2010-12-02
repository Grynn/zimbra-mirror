
package com.zimbra.soap.mail.wsimport.generated;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.zimbra.soap.mail.wsimport.generated package. 
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

    private final static QName _MailDataSource_QNAME = new QName("urn:zimbraMail", "mailDataSource");
    private final static QName _Cn_QNAME = new QName("urn:zimbraMail", "cn");
    private final static QName _Search_QNAME = new QName("urn:zimbraMail", "search");
    private final static QName _GetDataSourcesResponse_QNAME = new QName("urn:zimbraMail", "GetDataSourcesResponse");
    private final static QName _Context_QNAME = new QName("urn:zimbra", "context");
    private final static QName _ExportContactsResponse_QNAME = new QName("urn:zimbraMail", "ExportContactsResponse");
    private final static QName _ImportContactsRequest_QNAME = new QName("urn:zimbraMail", "ImportContactsRequest");
    private final static QName _GetDataSourcesRequest_QNAME = new QName("urn:zimbraMail", "GetDataSourcesRequest");
    private final static QName _Content_QNAME = new QName("urn:zimbraMail", "content");
    private final static QName _Folder_QNAME = new QName("urn:zimbraMail", "folder");
    private final static QName _Link_QNAME = new QName("urn:zimbraMail", "link");
    private final static QName _ImportContactsResponse_QNAME = new QName("urn:zimbraMail", "ImportContactsResponse");
    private final static QName _GetFolderRequest_QNAME = new QName("urn:zimbraMail", "GetFolderRequest");
    private final static QName _GetFolderResponse_QNAME = new QName("urn:zimbraMail", "GetFolderResponse");
    private final static QName _ExportContactsRequest_QNAME = new QName("urn:zimbraMail", "ExportContactsRequest");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.zimbra.soap.mail.wsimport.generated
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Grant }
     * 
     */
    public Grant createGrant() {
        return new Grant();
    }

    /**
     * Create an instance of {@link ImportContactsResponse }
     * 
     */
    public ImportContactsResponse createImportContactsResponse() {
        return new ImportContactsResponse();
    }

    /**
     * Create an instance of {@link Folder }
     * 
     */
    public Folder createFolder() {
        return new Folder();
    }

    /**
     * Create an instance of {@link GetDataSourcesRequest }
     * 
     */
    public GetDataSourcesRequest createGetDataSourcesRequest() {
        return new GetDataSourcesRequest();
    }

    /**
     * Create an instance of {@link ExportContactsRequest }
     * 
     */
    public ExportContactsRequest createExportContactsRequest() {
        return new ExportContactsRequest();
    }

    /**
     * Create an instance of {@link HeaderContext }
     * 
     */
    public HeaderContext createHeaderContext() {
        return new HeaderContext();
    }

    /**
     * Create an instance of {@link GetDataSourcesResponse }
     * 
     */
    public GetDataSourcesResponse createGetDataSourcesResponse() {
        return new GetDataSourcesResponse();
    }

    /**
     * Create an instance of {@link MailCalDataSource }
     * 
     */
    public MailCalDataSource createMailCalDataSource() {
        return new MailCalDataSource();
    }

    /**
     * Create an instance of {@link MailPop3DataSource }
     * 
     */
    public MailPop3DataSource createMailPop3DataSource() {
        return new MailPop3DataSource();
    }

    /**
     * Create an instance of {@link MailRssDataSource }
     * 
     */
    public MailRssDataSource createMailRssDataSource() {
        return new MailRssDataSource();
    }

    /**
     * Create an instance of {@link GetFolderRequest }
     * 
     */
    public GetFolderRequest createGetFolderRequest() {
        return new GetFolderRequest();
    }

    /**
     * Create an instance of {@link Content }
     * 
     */
    public Content createContent() {
        return new Content();
    }

    /**
     * Create an instance of {@link GetFolderResponse }
     * 
     */
    public GetFolderResponse createGetFolderResponse() {
        return new GetFolderResponse();
    }

    /**
     * Create an instance of {@link Folder.Acl }
     * 
     */
    public Folder.Acl createFolderAcl() {
        return new Folder.Acl();
    }

    /**
     * Create an instance of {@link MailImapDataSource }
     * 
     */
    public MailImapDataSource createMailImapDataSource() {
        return new MailImapDataSource();
    }

    /**
     * Create an instance of {@link ImportContact }
     * 
     */
    public ImportContact createImportContact() {
        return new ImportContact();
    }

    /**
     * Create an instance of {@link ImportContactsRequest }
     * 
     */
    public ImportContactsRequest createImportContactsRequest() {
        return new ImportContactsRequest();
    }

    /**
     * Create an instance of {@link SearchFolder }
     * 
     */
    public SearchFolder createSearchFolder() {
        return new SearchFolder();
    }

    /**
     * Create an instance of {@link Mountpoint }
     * 
     */
    public Mountpoint createMountpoint() {
        return new Mountpoint();
    }

    /**
     * Create an instance of {@link ExportContactsResponse }
     * 
     */
    public ExportContactsResponse createExportContactsResponse() {
        return new ExportContactsResponse();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link MailDataSource }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:zimbraMail", name = "mailDataSource")
    public JAXBElement<MailDataSource> createMailDataSource(MailDataSource value) {
        return new JAXBElement<MailDataSource>(_MailDataSource_QNAME, MailDataSource.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ImportContact }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:zimbraMail", name = "cn")
    public JAXBElement<ImportContact> createCn(ImportContact value) {
        return new JAXBElement<ImportContact>(_Cn_QNAME, ImportContact.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SearchFolder }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:zimbraMail", name = "search")
    public JAXBElement<SearchFolder> createSearch(SearchFolder value) {
        return new JAXBElement<SearchFolder>(_Search_QNAME, SearchFolder.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetDataSourcesResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:zimbraMail", name = "GetDataSourcesResponse")
    public JAXBElement<GetDataSourcesResponse> createGetDataSourcesResponse(GetDataSourcesResponse value) {
        return new JAXBElement<GetDataSourcesResponse>(_GetDataSourcesResponse_QNAME, GetDataSourcesResponse.class, null, value);
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
     * Create an instance of {@link JAXBElement }{@code <}{@link ExportContactsResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:zimbraMail", name = "ExportContactsResponse")
    public JAXBElement<ExportContactsResponse> createExportContactsResponse(ExportContactsResponse value) {
        return new JAXBElement<ExportContactsResponse>(_ExportContactsResponse_QNAME, ExportContactsResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ImportContactsRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:zimbraMail", name = "ImportContactsRequest")
    public JAXBElement<ImportContactsRequest> createImportContactsRequest(ImportContactsRequest value) {
        return new JAXBElement<ImportContactsRequest>(_ImportContactsRequest_QNAME, ImportContactsRequest.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetDataSourcesRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:zimbraMail", name = "GetDataSourcesRequest")
    public JAXBElement<GetDataSourcesRequest> createGetDataSourcesRequest(GetDataSourcesRequest value) {
        return new JAXBElement<GetDataSourcesRequest>(_GetDataSourcesRequest_QNAME, GetDataSourcesRequest.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Content }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:zimbraMail", name = "content")
    public JAXBElement<Content> createContent(Content value) {
        return new JAXBElement<Content>(_Content_QNAME, Content.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Folder }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:zimbraMail", name = "folder")
    public JAXBElement<Folder> createFolder(Folder value) {
        return new JAXBElement<Folder>(_Folder_QNAME, Folder.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Mountpoint }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:zimbraMail", name = "link")
    public JAXBElement<Mountpoint> createLink(Mountpoint value) {
        return new JAXBElement<Mountpoint>(_Link_QNAME, Mountpoint.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ImportContactsResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:zimbraMail", name = "ImportContactsResponse")
    public JAXBElement<ImportContactsResponse> createImportContactsResponse(ImportContactsResponse value) {
        return new JAXBElement<ImportContactsResponse>(_ImportContactsResponse_QNAME, ImportContactsResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetFolderRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:zimbraMail", name = "GetFolderRequest")
    public JAXBElement<GetFolderRequest> createGetFolderRequest(GetFolderRequest value) {
        return new JAXBElement<GetFolderRequest>(_GetFolderRequest_QNAME, GetFolderRequest.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetFolderResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:zimbraMail", name = "GetFolderResponse")
    public JAXBElement<GetFolderResponse> createGetFolderResponse(GetFolderResponse value) {
        return new JAXBElement<GetFolderResponse>(_GetFolderResponse_QNAME, GetFolderResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ExportContactsRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:zimbraMail", name = "ExportContactsRequest")
    public JAXBElement<ExportContactsRequest> createExportContactsRequest(ExportContactsRequest value) {
        return new JAXBElement<ExportContactsRequest>(_ExportContactsRequest_QNAME, ExportContactsRequest.class, null, value);
    }

}
