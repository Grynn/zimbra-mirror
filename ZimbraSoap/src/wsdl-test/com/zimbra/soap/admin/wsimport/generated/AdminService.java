
package com.zimbra.soap.admin.wsimport.generated;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.bind.annotation.XmlSeeAlso;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.1.7-hudson-48-
 * Generated source version: 2.1
 * 
 */
@WebService(name = "AdminService", targetNamespace = "urn:zimbraAdmin")
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE)
@XmlSeeAlso({
    ObjectFactory.class
})
public interface AdminService {


    /**
     * 
     * @param parameters
     * @return
     *     returns com.zimbra.soap.admin.wsimport.generated.AuthResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/Auth")
    @WebResult(name = "AuthResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public AuthResponse authRequest(
        @WebParam(name = "AuthRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        AuthRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns com.zimbra.soap.admin.wsimport.generated.CreateAccountResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/CreateAccount")
    @WebResult(name = "CreateAccountResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public CreateAccountResponse createAccountRequest(
        @WebParam(name = "CreateAccountRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        CreateAccountRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns com.zimbra.soap.admin.wsimport.generated.CreateDomainResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/CreateDomain")
    @WebResult(name = "CreateDomainResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public CreateDomainResponse createDomainRequest(
        @WebParam(name = "CreateDomainRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        CreateDomainRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns com.zimbra.soap.admin.wsimport.generated.CreateServerResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/CreateServer")
    @WebResult(name = "CreateServerResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public CreateServerResponse createServerRequest(
        @WebParam(name = "CreateServerRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        CreateServerRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns com.zimbra.soap.admin.wsimport.generated.DeleteDomainResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/DeleteDomain")
    @WebResult(name = "DeleteDomainResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public DeleteDomainResponse deleteDomainRequest(
        @WebParam(name = "DeleteDomainRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        DeleteDomainRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns com.zimbra.soap.admin.wsimport.generated.DeleteServerResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/DeleteServer")
    @WebResult(name = "DeleteServerResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public DeleteServerResponse deleteServerRequest(
        @WebParam(name = "DeleteServerRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        DeleteServerRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns com.zimbra.soap.admin.wsimport.generated.GetAccountResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/GetAccount")
    @WebResult(name = "GetAccountResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public GetAccountResponse getAccountRequest(
        @WebParam(name = "GetAccountRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        GetAccountRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns com.zimbra.soap.admin.wsimport.generated.GetAllDomainsResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/GetAllDomains")
    @WebResult(name = "GetAllDomainsResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public GetAllDomainsResponse getAllDomainsRequest(
        @WebParam(name = "GetAllDomainsRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        GetAllDomainsRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns com.zimbra.soap.admin.wsimport.generated.GetAllServersResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/GetAllServers")
    @WebResult(name = "GetAllServersResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public GetAllServersResponse getAllServersRequest(
        @WebParam(name = "GetAllServersRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        GetAllServersRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns com.zimbra.soap.admin.wsimport.generated.GetDomainInfoResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/GetDomainInfo")
    @WebResult(name = "GetDomainInfoResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public GetDomainInfoResponse getDomainInfoRequest(
        @WebParam(name = "GetDomainInfoRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        GetDomainInfoRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns com.zimbra.soap.admin.wsimport.generated.GetDomainResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/GetDomain")
    @WebResult(name = "GetDomainResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public GetDomainResponse getDomainRequest(
        @WebParam(name = "GetDomainRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        GetDomainRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns com.zimbra.soap.admin.wsimport.generated.GetServerResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/GetServer")
    @WebResult(name = "GetServerResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public GetServerResponse getServerRequest(
        @WebParam(name = "GetServerRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        GetServerRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns com.zimbra.soap.admin.wsimport.generated.ModifyDomainResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/ModifyDomain")
    @WebResult(name = "ModifyDomainResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public ModifyDomainResponse modifyDomainRequest(
        @WebParam(name = "ModifyDomainRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        ModifyDomainRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns com.zimbra.soap.admin.wsimport.generated.ModifyServerResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/ModifyServer")
    @WebResult(name = "ModifyServerResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public ModifyServerResponse modifyServerRequest(
        @WebParam(name = "ModifyServerRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        ModifyServerRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns com.zimbra.soap.admin.wsimport.generated.ReloadLocalConfigResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/ReloadLocalConfig")
    @WebResult(name = "ReloadLocalConfigResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public ReloadLocalConfigResponse reloadLocalConfigRequest(
        @WebParam(name = "ReloadLocalConfigRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        ReloadLocalConfigRequest parameters);

}
