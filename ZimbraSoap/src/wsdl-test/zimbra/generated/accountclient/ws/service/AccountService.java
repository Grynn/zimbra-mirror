
package zimbra.generated.accountclient.ws.service;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.bind.annotation.XmlSeeAlso;
import zimbra.generated.accountclient.account.testAuthRequest;
import zimbra.generated.accountclient.account.testAuthResponse;
import zimbra.generated.accountclient.account.testAutoCompleteGalRequest;
import zimbra.generated.accountclient.account.testAutoCompleteGalResponse;
import zimbra.generated.accountclient.account.testChangePasswordRequest;
import zimbra.generated.accountclient.account.testChangePasswordResponse;
import zimbra.generated.accountclient.account.testCheckLicenseRequest;
import zimbra.generated.accountclient.account.testCheckLicenseResponse;
import zimbra.generated.accountclient.account.testCreateDistributionListRequest;
import zimbra.generated.accountclient.account.testCreateDistributionListResponse;
import zimbra.generated.accountclient.account.testCreateIdentityRequest;
import zimbra.generated.accountclient.account.testCreateIdentityResponse;
import zimbra.generated.accountclient.account.testCreateSignatureRequest;
import zimbra.generated.accountclient.account.testCreateSignatureResponse;
import zimbra.generated.accountclient.account.testDeleteIdentityRequest;
import zimbra.generated.accountclient.account.testDeleteIdentityResponse;
import zimbra.generated.accountclient.account.testDeleteSignatureRequest;
import zimbra.generated.accountclient.account.testDeleteSignatureResponse;
import zimbra.generated.accountclient.account.testDistributionListActionRequest;
import zimbra.generated.accountclient.account.testDistributionListActionResponse;
import zimbra.generated.accountclient.account.testEndSessionRequest;
import zimbra.generated.accountclient.account.testEndSessionResponse;
import zimbra.generated.accountclient.account.testGetAccountInfoRequest;
import zimbra.generated.accountclient.account.testGetAccountInfoResponse;
import zimbra.generated.accountclient.account.testGetAccountMembershipRequest;
import zimbra.generated.accountclient.account.testGetAccountMembershipResponse;
import zimbra.generated.accountclient.account.testGetAllLocalesRequest;
import zimbra.generated.accountclient.account.testGetAllLocalesResponse;
import zimbra.generated.accountclient.account.testGetAvailableCsvFormatsRequest;
import zimbra.generated.accountclient.account.testGetAvailableCsvFormatsResponse;
import zimbra.generated.accountclient.account.testGetAvailableLocalesRequest;
import zimbra.generated.accountclient.account.testGetAvailableLocalesResponse;
import zimbra.generated.accountclient.account.testGetAvailableSkinsRequest;
import zimbra.generated.accountclient.account.testGetAvailableSkinsResponse;
import zimbra.generated.accountclient.account.testGetDistributionListMembersRequest;
import zimbra.generated.accountclient.account.testGetDistributionListMembersResponse;
import zimbra.generated.accountclient.account.testGetDistributionListRequest;
import zimbra.generated.accountclient.account.testGetDistributionListResponse;
import zimbra.generated.accountclient.account.testGetIdentitiesRequest;
import zimbra.generated.accountclient.account.testGetIdentitiesResponse;
import zimbra.generated.accountclient.account.testGetInfoRequest;
import zimbra.generated.accountclient.account.testGetInfoResponse;
import zimbra.generated.accountclient.account.testGetPrefsRequest;
import zimbra.generated.accountclient.account.testGetPrefsResponse;
import zimbra.generated.accountclient.account.testGetSMIMEPublicCertsRequest;
import zimbra.generated.accountclient.account.testGetSMIMEPublicCertsResponse;
import zimbra.generated.accountclient.account.testGetShareInfoRequest;
import zimbra.generated.accountclient.account.testGetShareInfoResponse;
import zimbra.generated.accountclient.account.testGetSignaturesRequest;
import zimbra.generated.accountclient.account.testGetSignaturesResponse;
import zimbra.generated.accountclient.account.testGetVersionInfoRequest;
import zimbra.generated.accountclient.account.testGetVersionInfoResponse;
import zimbra.generated.accountclient.account.testGetWhiteBlackListRequest;
import zimbra.generated.accountclient.account.testGetWhiteBlackListResponse;
import zimbra.generated.accountclient.account.testModifyIdentityRequest;
import zimbra.generated.accountclient.account.testModifyIdentityResponse;
import zimbra.generated.accountclient.account.testModifyPrefsRequest;
import zimbra.generated.accountclient.account.testModifyPrefsResponse;
import zimbra.generated.accountclient.account.testModifyPropertiesRequest;
import zimbra.generated.accountclient.account.testModifyPropertiesResponse;
import zimbra.generated.accountclient.account.testModifySignatureRequest;
import zimbra.generated.accountclient.account.testModifySignatureResponse;
import zimbra.generated.accountclient.account.testModifyWhiteBlackListRequest;
import zimbra.generated.accountclient.account.testModifyWhiteBlackListResponse;
import zimbra.generated.accountclient.account.testModifyZimletPrefsRequest;
import zimbra.generated.accountclient.account.testModifyZimletPrefsResponse;
import zimbra.generated.accountclient.account.testSearchCalendarResourcesRequest;
import zimbra.generated.accountclient.account.testSearchCalendarResourcesResponse;
import zimbra.generated.accountclient.account.testSearchGalRequest;
import zimbra.generated.accountclient.account.testSearchGalResponse;
import zimbra.generated.accountclient.account.testSubscribeDistributionListRequest;
import zimbra.generated.accountclient.account.testSubscribeDistributionListResponse;
import zimbra.generated.accountclient.account.testSyncGalRequest;
import zimbra.generated.accountclient.account.testSyncGalResponse;
import zimbra.generated.accountclient.account.testUpdateProfileRequest;
import zimbra.generated.accountclient.account.testUpdateProfileResponse;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.1.7-hudson-48-
 * Generated source version: 2.1
 * 
 */
@WebService(name = "AccountService", targetNamespace = "http://www.zimbra.com/wsdl/AccountService.wsdl")
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE)
@XmlSeeAlso({
    zimbra.generated.accountclient.mail.ObjectFactory.class,
    zimbra.generated.accountclient.zm.ObjectFactory.class,
    zimbra.generated.accountclient.account.ObjectFactory.class,
    zimbra.generated.accountclient.admin.ObjectFactory.class
})
public interface AccountService {


    /**
     * 
     * @param parameters
     * @return
     *     returns zimbra.generated.accountclient.account.testAuthResponse
     */
    @WebMethod(action = "urn:zimbraAccount/Auth")
    @WebResult(name = "AuthResponse", targetNamespace = "urn:zimbraAccount", partName = "parameters")
    public testAuthResponse authRequest(
        @WebParam(name = "AuthRequest", targetNamespace = "urn:zimbraAccount", partName = "parameters")
        testAuthRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns zimbra.generated.accountclient.account.testAutoCompleteGalResponse
     */
    @WebMethod(action = "urn:zimbraAccount/AutoCompleteGal")
    @WebResult(name = "AutoCompleteGalResponse", targetNamespace = "urn:zimbraAccount", partName = "parameters")
    public testAutoCompleteGalResponse autoCompleteGalRequest(
        @WebParam(name = "AutoCompleteGalRequest", targetNamespace = "urn:zimbraAccount", partName = "parameters")
        testAutoCompleteGalRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns zimbra.generated.accountclient.account.testChangePasswordResponse
     */
    @WebMethod(action = "urn:zimbraAccount/ChangePassword")
    @WebResult(name = "ChangePasswordResponse", targetNamespace = "urn:zimbraAccount", partName = "parameters")
    public testChangePasswordResponse changePasswordRequest(
        @WebParam(name = "ChangePasswordRequest", targetNamespace = "urn:zimbraAccount", partName = "parameters")
        testChangePasswordRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns zimbra.generated.accountclient.account.testCheckLicenseResponse
     */
    @WebMethod(action = "urn:zimbraAccount/CheckLicense")
    @WebResult(name = "CheckLicenseResponse", targetNamespace = "urn:zimbraAccount", partName = "parameters")
    public testCheckLicenseResponse checkLicenseRequest(
        @WebParam(name = "CheckLicenseRequest", targetNamespace = "urn:zimbraAccount", partName = "parameters")
        testCheckLicenseRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns zimbra.generated.accountclient.account.testCreateDistributionListResponse
     */
    @WebMethod(action = "urn:zimbraAccount/CreateDistributionList")
    @WebResult(name = "CreateDistributionListResponse", targetNamespace = "urn:zimbraAccount", partName = "parameters")
    public testCreateDistributionListResponse createDistributionListRequest(
        @WebParam(name = "CreateDistributionListRequest", targetNamespace = "urn:zimbraAccount", partName = "parameters")
        testCreateDistributionListRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns zimbra.generated.accountclient.account.testCreateIdentityResponse
     */
    @WebMethod(action = "urn:zimbraAccount/CreateIdentity")
    @WebResult(name = "CreateIdentityResponse", targetNamespace = "urn:zimbraAccount", partName = "parameters")
    public testCreateIdentityResponse createIdentityRequest(
        @WebParam(name = "CreateIdentityRequest", targetNamespace = "urn:zimbraAccount", partName = "parameters")
        testCreateIdentityRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns zimbra.generated.accountclient.account.testCreateSignatureResponse
     */
    @WebMethod(action = "urn:zimbraAccount/CreateSignature")
    @WebResult(name = "CreateSignatureResponse", targetNamespace = "urn:zimbraAccount", partName = "parameters")
    public testCreateSignatureResponse createSignatureRequest(
        @WebParam(name = "CreateSignatureRequest", targetNamespace = "urn:zimbraAccount", partName = "parameters")
        testCreateSignatureRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns zimbra.generated.accountclient.account.testDeleteIdentityResponse
     */
    @WebMethod(action = "urn:zimbraAccount/DeleteIdentity")
    @WebResult(name = "DeleteIdentityResponse", targetNamespace = "urn:zimbraAccount", partName = "parameters")
    public testDeleteIdentityResponse deleteIdentityRequest(
        @WebParam(name = "DeleteIdentityRequest", targetNamespace = "urn:zimbraAccount", partName = "parameters")
        testDeleteIdentityRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns zimbra.generated.accountclient.account.testDeleteSignatureResponse
     */
    @WebMethod(action = "urn:zimbraAccount/DeleteSignature")
    @WebResult(name = "DeleteSignatureResponse", targetNamespace = "urn:zimbraAccount", partName = "parameters")
    public testDeleteSignatureResponse deleteSignatureRequest(
        @WebParam(name = "DeleteSignatureRequest", targetNamespace = "urn:zimbraAccount", partName = "parameters")
        testDeleteSignatureRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns zimbra.generated.accountclient.account.testDistributionListActionResponse
     */
    @WebMethod(action = "urn:zimbraAccount/DistributionListAction")
    @WebResult(name = "DistributionListActionResponse", targetNamespace = "urn:zimbraAccount", partName = "parameters")
    public testDistributionListActionResponse distributionListActionRequest(
        @WebParam(name = "DistributionListActionRequest", targetNamespace = "urn:zimbraAccount", partName = "parameters")
        testDistributionListActionRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns zimbra.generated.accountclient.account.testEndSessionResponse
     */
    @WebMethod(action = "urn:zimbraAccount/EndSession")
    @WebResult(name = "EndSessionResponse", targetNamespace = "urn:zimbraAccount", partName = "parameters")
    public testEndSessionResponse endSessionRequest(
        @WebParam(name = "EndSessionRequest", targetNamespace = "urn:zimbraAccount", partName = "parameters")
        testEndSessionRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns zimbra.generated.accountclient.account.testGetAccountInfoResponse
     */
    @WebMethod(action = "urn:zimbraAccount/GetAccountInfo")
    @WebResult(name = "GetAccountInfoResponse", targetNamespace = "urn:zimbraAccount", partName = "parameters")
    public testGetAccountInfoResponse getAccountInfoRequest(
        @WebParam(name = "GetAccountInfoRequest", targetNamespace = "urn:zimbraAccount", partName = "parameters")
        testGetAccountInfoRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns zimbra.generated.accountclient.account.testGetAccountMembershipResponse
     */
    @WebMethod(action = "urn:zimbraAccount/GetAccountMembership")
    @WebResult(name = "GetAccountMembershipResponse", targetNamespace = "urn:zimbraAccount", partName = "parameters")
    public testGetAccountMembershipResponse getAccountMembershipRequest(
        @WebParam(name = "GetAccountMembershipRequest", targetNamespace = "urn:zimbraAccount", partName = "parameters")
        testGetAccountMembershipRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns zimbra.generated.accountclient.account.testGetAllLocalesResponse
     */
    @WebMethod(action = "urn:zimbraAccount/GetAllLocales")
    @WebResult(name = "GetAllLocalesResponse", targetNamespace = "urn:zimbraAccount", partName = "parameters")
    public testGetAllLocalesResponse getAllLocalesRequest(
        @WebParam(name = "GetAllLocalesRequest", targetNamespace = "urn:zimbraAccount", partName = "parameters")
        testGetAllLocalesRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns zimbra.generated.accountclient.account.testGetAvailableCsvFormatsResponse
     */
    @WebMethod(action = "urn:zimbraAccount/GetAvailableCsvFormats")
    @WebResult(name = "GetAvailableCsvFormatsResponse", targetNamespace = "urn:zimbraAccount", partName = "parameters")
    public testGetAvailableCsvFormatsResponse getAvailableCsvFormatsRequest(
        @WebParam(name = "GetAvailableCsvFormatsRequest", targetNamespace = "urn:zimbraAccount", partName = "parameters")
        testGetAvailableCsvFormatsRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns zimbra.generated.accountclient.account.testGetAvailableLocalesResponse
     */
    @WebMethod(action = "urn:zimbraAccount/GetAvailableLocales")
    @WebResult(name = "GetAvailableLocalesResponse", targetNamespace = "urn:zimbraAccount", partName = "parameters")
    public testGetAvailableLocalesResponse getAvailableLocalesRequest(
        @WebParam(name = "GetAvailableLocalesRequest", targetNamespace = "urn:zimbraAccount", partName = "parameters")
        testGetAvailableLocalesRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns zimbra.generated.accountclient.account.testGetAvailableSkinsResponse
     */
    @WebMethod(action = "urn:zimbraAccount/GetAvailableSkins")
    @WebResult(name = "GetAvailableSkinsResponse", targetNamespace = "urn:zimbraAccount", partName = "parameters")
    public testGetAvailableSkinsResponse getAvailableSkinsRequest(
        @WebParam(name = "GetAvailableSkinsRequest", targetNamespace = "urn:zimbraAccount", partName = "parameters")
        testGetAvailableSkinsRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns zimbra.generated.accountclient.account.testGetDistributionListMembersResponse
     */
    @WebMethod(action = "urn:zimbraAccount/GetDistributionListMembers")
    @WebResult(name = "GetDistributionListMembersResponse", targetNamespace = "urn:zimbraAccount", partName = "parameters")
    public testGetDistributionListMembersResponse getDistributionListMembersRequest(
        @WebParam(name = "GetDistributionListMembersRequest", targetNamespace = "urn:zimbraAccount", partName = "parameters")
        testGetDistributionListMembersRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns zimbra.generated.accountclient.account.testGetDistributionListResponse
     */
    @WebMethod(action = "urn:zimbraAccount/GetDistributionList")
    @WebResult(name = "GetDistributionListResponse", targetNamespace = "urn:zimbraAccount", partName = "parameters")
    public testGetDistributionListResponse getDistributionListRequest(
        @WebParam(name = "GetDistributionListRequest", targetNamespace = "urn:zimbraAccount", partName = "parameters")
        testGetDistributionListRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns zimbra.generated.accountclient.account.testGetIdentitiesResponse
     */
    @WebMethod(action = "urn:zimbraAccount/GetIdentities")
    @WebResult(name = "GetIdentitiesResponse", targetNamespace = "urn:zimbraAccount", partName = "parameters")
    public testGetIdentitiesResponse getIdentitiesRequest(
        @WebParam(name = "GetIdentitiesRequest", targetNamespace = "urn:zimbraAccount", partName = "parameters")
        testGetIdentitiesRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns zimbra.generated.accountclient.account.testGetInfoResponse
     */
    @WebMethod(action = "urn:zimbraAccount/GetInfo")
    @WebResult(name = "GetInfoResponse", targetNamespace = "urn:zimbraAccount", partName = "parameters")
    public testGetInfoResponse getInfoRequest(
        @WebParam(name = "GetInfoRequest", targetNamespace = "urn:zimbraAccount", partName = "parameters")
        testGetInfoRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns zimbra.generated.accountclient.account.testGetPrefsResponse
     */
    @WebMethod(action = "urn:zimbraAccount/GetPrefs")
    @WebResult(name = "GetPrefsResponse", targetNamespace = "urn:zimbraAccount", partName = "parameters")
    public testGetPrefsResponse getPrefsRequest(
        @WebParam(name = "GetPrefsRequest", targetNamespace = "urn:zimbraAccount", partName = "parameters")
        testGetPrefsRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns zimbra.generated.accountclient.account.testGetSMIMEPublicCertsResponse
     */
    @WebMethod(action = "urn:zimbraAccount/GetSMIMEPublicCerts")
    @WebResult(name = "GetSMIMEPublicCertsResponse", targetNamespace = "urn:zimbraAccount", partName = "parameters")
    public testGetSMIMEPublicCertsResponse getSMIMEPublicCertsRequest(
        @WebParam(name = "GetSMIMEPublicCertsRequest", targetNamespace = "urn:zimbraAccount", partName = "parameters")
        testGetSMIMEPublicCertsRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns zimbra.generated.accountclient.account.testGetShareInfoResponse
     */
    @WebMethod(action = "urn:zimbraAccount/GetShareInfo")
    @WebResult(name = "GetShareInfoResponse", targetNamespace = "urn:zimbraAccount", partName = "parameters")
    public testGetShareInfoResponse getShareInfoRequest(
        @WebParam(name = "GetShareInfoRequest", targetNamespace = "urn:zimbraAccount", partName = "parameters")
        testGetShareInfoRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns zimbra.generated.accountclient.account.testGetSignaturesResponse
     */
    @WebMethod(action = "urn:zimbraAccount/GetSignatures")
    @WebResult(name = "GetSignaturesResponse", targetNamespace = "urn:zimbraAccount", partName = "parameters")
    public testGetSignaturesResponse getSignaturesRequest(
        @WebParam(name = "GetSignaturesRequest", targetNamespace = "urn:zimbraAccount", partName = "parameters")
        testGetSignaturesRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns zimbra.generated.accountclient.account.testGetVersionInfoResponse
     */
    @WebMethod(action = "urn:zimbraAccount/GetVersionInfo")
    @WebResult(name = "GetVersionInfoResponse", targetNamespace = "urn:zimbraAccount", partName = "parameters")
    public testGetVersionInfoResponse getVersionInfoRequest(
        @WebParam(name = "GetVersionInfoRequest", targetNamespace = "urn:zimbraAccount", partName = "parameters")
        testGetVersionInfoRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns zimbra.generated.accountclient.account.testGetWhiteBlackListResponse
     */
    @WebMethod(action = "urn:zimbraAccount/GetWhiteBlackList")
    @WebResult(name = "GetWhiteBlackListResponse", targetNamespace = "urn:zimbraAccount", partName = "parameters")
    public testGetWhiteBlackListResponse getWhiteBlackListRequest(
        @WebParam(name = "GetWhiteBlackListRequest", targetNamespace = "urn:zimbraAccount", partName = "parameters")
        testGetWhiteBlackListRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns zimbra.generated.accountclient.account.testModifyIdentityResponse
     */
    @WebMethod(action = "urn:zimbraAccount/ModifyIdentity")
    @WebResult(name = "ModifyIdentityResponse", targetNamespace = "urn:zimbraAccount", partName = "parameters")
    public testModifyIdentityResponse modifyIdentityRequest(
        @WebParam(name = "ModifyIdentityRequest", targetNamespace = "urn:zimbraAccount", partName = "parameters")
        testModifyIdentityRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns zimbra.generated.accountclient.account.testModifyPrefsResponse
     */
    @WebMethod(action = "urn:zimbraAccount/ModifyPrefs")
    @WebResult(name = "ModifyPrefsResponse", targetNamespace = "urn:zimbraAccount", partName = "parameters")
    public testModifyPrefsResponse modifyPrefsRequest(
        @WebParam(name = "ModifyPrefsRequest", targetNamespace = "urn:zimbraAccount", partName = "parameters")
        testModifyPrefsRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns zimbra.generated.accountclient.account.testModifyPropertiesResponse
     */
    @WebMethod(action = "urn:zimbraAccount/ModifyProperties")
    @WebResult(name = "ModifyPropertiesResponse", targetNamespace = "urn:zimbraAccount", partName = "parameters")
    public testModifyPropertiesResponse modifyPropertiesRequest(
        @WebParam(name = "ModifyPropertiesRequest", targetNamespace = "urn:zimbraAccount", partName = "parameters")
        testModifyPropertiesRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns zimbra.generated.accountclient.account.testModifySignatureResponse
     */
    @WebMethod(action = "urn:zimbraAccount/ModifySignature")
    @WebResult(name = "ModifySignatureResponse", targetNamespace = "urn:zimbraAccount", partName = "parameters")
    public testModifySignatureResponse modifySignatureRequest(
        @WebParam(name = "ModifySignatureRequest", targetNamespace = "urn:zimbraAccount", partName = "parameters")
        testModifySignatureRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns zimbra.generated.accountclient.account.testModifyWhiteBlackListResponse
     */
    @WebMethod(action = "urn:zimbraAccount/ModifyWhiteBlackList")
    @WebResult(name = "ModifyWhiteBlackListResponse", targetNamespace = "urn:zimbraAccount", partName = "parameters")
    public testModifyWhiteBlackListResponse modifyWhiteBlackListRequest(
        @WebParam(name = "ModifyWhiteBlackListRequest", targetNamespace = "urn:zimbraAccount", partName = "parameters")
        testModifyWhiteBlackListRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns zimbra.generated.accountclient.account.testModifyZimletPrefsResponse
     */
    @WebMethod(action = "urn:zimbraAccount/ModifyZimletPrefs")
    @WebResult(name = "ModifyZimletPrefsResponse", targetNamespace = "urn:zimbraAccount", partName = "parameters")
    public testModifyZimletPrefsResponse modifyZimletPrefsRequest(
        @WebParam(name = "ModifyZimletPrefsRequest", targetNamespace = "urn:zimbraAccount", partName = "parameters")
        testModifyZimletPrefsRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns zimbra.generated.accountclient.account.testSearchCalendarResourcesResponse
     */
    @WebMethod(action = "urn:zimbraAccount/SearchCalendarResources")
    @WebResult(name = "SearchCalendarResourcesResponse", targetNamespace = "urn:zimbraAccount", partName = "parameters")
    public testSearchCalendarResourcesResponse searchCalendarResourcesRequest(
        @WebParam(name = "SearchCalendarResourcesRequest", targetNamespace = "urn:zimbraAccount", partName = "parameters")
        testSearchCalendarResourcesRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns zimbra.generated.accountclient.account.testSearchGalResponse
     */
    @WebMethod(action = "urn:zimbraAccount/SearchGal")
    @WebResult(name = "SearchGalResponse", targetNamespace = "urn:zimbraAccount", partName = "parameters")
    public testSearchGalResponse searchGalRequest(
        @WebParam(name = "SearchGalRequest", targetNamespace = "urn:zimbraAccount", partName = "parameters")
        testSearchGalRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns zimbra.generated.accountclient.account.testSubscribeDistributionListResponse
     */
    @WebMethod(action = "urn:zimbraAccount/SubscribeDistributionList")
    @WebResult(name = "SubscribeDistributionListResponse", targetNamespace = "urn:zimbraAccount", partName = "parameters")
    public testSubscribeDistributionListResponse subscribeDistributionListRequest(
        @WebParam(name = "SubscribeDistributionListRequest", targetNamespace = "urn:zimbraAccount", partName = "parameters")
        testSubscribeDistributionListRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns zimbra.generated.accountclient.account.testSyncGalResponse
     */
    @WebMethod(action = "urn:zimbraAccount/SyncGal")
    @WebResult(name = "SyncGalResponse", targetNamespace = "urn:zimbraAccount", partName = "parameters")
    public testSyncGalResponse syncGalRequest(
        @WebParam(name = "SyncGalRequest", targetNamespace = "urn:zimbraAccount", partName = "parameters")
        testSyncGalRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns zimbra.generated.accountclient.account.testUpdateProfileResponse
     */
    @WebMethod(action = "urn:zimbraAccount/UpdateProfile")
    @WebResult(name = "UpdateProfileResponse", targetNamespace = "urn:zimbraAccount", partName = "parameters")
    public testUpdateProfileResponse updateProfileRequest(
        @WebParam(name = "UpdateProfileRequest", targetNamespace = "urn:zimbraAccount", partName = "parameters")
        testUpdateProfileRequest parameters);

}
