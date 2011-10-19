
package generated.zcsclient.ws.service;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.bind.annotation.XmlSeeAlso;
import generated.zcsclient.account.testAuthRequest;
import generated.zcsclient.account.testAuthResponse;
import generated.zcsclient.account.testAutoCompleteGalRequest;
import generated.zcsclient.account.testAutoCompleteGalResponse;
import generated.zcsclient.account.testChangePasswordRequest;
import generated.zcsclient.account.testChangePasswordResponse;
import generated.zcsclient.account.testCheckLicenseRequest;
import generated.zcsclient.account.testCheckLicenseResponse;
import generated.zcsclient.account.testCreateDistributionListRequest;
import generated.zcsclient.account.testCreateDistributionListResponse;
import generated.zcsclient.account.testCreateIdentityRequest;
import generated.zcsclient.account.testCreateIdentityResponse;
import generated.zcsclient.account.testCreateSignatureRequest;
import generated.zcsclient.account.testCreateSignatureResponse;
import generated.zcsclient.account.testDeleteIdentityRequest;
import generated.zcsclient.account.testDeleteIdentityResponse;
import generated.zcsclient.account.testDeleteSignatureRequest;
import generated.zcsclient.account.testDeleteSignatureResponse;
import generated.zcsclient.account.testDistributionListActionRequest;
import generated.zcsclient.account.testDistributionListActionResponse;
import generated.zcsclient.account.testEndSessionRequest;
import generated.zcsclient.account.testEndSessionResponse;
import generated.zcsclient.account.testGetAccountInfoRequest;
import generated.zcsclient.account.testGetAccountInfoResponse;
import generated.zcsclient.account.testGetAccountMembershipRequest;
import generated.zcsclient.account.testGetAccountMembershipResponse;
import generated.zcsclient.account.testGetAllLocalesRequest;
import generated.zcsclient.account.testGetAllLocalesResponse;
import generated.zcsclient.account.testGetAvailableCsvFormatsRequest;
import generated.zcsclient.account.testGetAvailableCsvFormatsResponse;
import generated.zcsclient.account.testGetAvailableLocalesRequest;
import generated.zcsclient.account.testGetAvailableLocalesResponse;
import generated.zcsclient.account.testGetAvailableSkinsRequest;
import generated.zcsclient.account.testGetAvailableSkinsResponse;
import generated.zcsclient.account.testGetDistributionListMembersRequest;
import generated.zcsclient.account.testGetDistributionListMembersResponse;
import generated.zcsclient.account.testGetDistributionListRequest;
import generated.zcsclient.account.testGetDistributionListResponse;
import generated.zcsclient.account.testGetIdentitiesRequest;
import generated.zcsclient.account.testGetIdentitiesResponse;
import generated.zcsclient.account.testGetInfoRequest;
import generated.zcsclient.account.testGetInfoResponse;
import generated.zcsclient.account.testGetPrefsRequest;
import generated.zcsclient.account.testGetPrefsResponse;
import generated.zcsclient.account.testGetSMIMEPublicCertsRequest;
import generated.zcsclient.account.testGetSMIMEPublicCertsResponse;
import generated.zcsclient.account.testGetShareInfoRequest;
import generated.zcsclient.account.testGetShareInfoResponse;
import generated.zcsclient.account.testGetSignaturesRequest;
import generated.zcsclient.account.testGetSignaturesResponse;
import generated.zcsclient.account.testGetVersionInfoRequest;
import generated.zcsclient.account.testGetVersionInfoResponse;
import generated.zcsclient.account.testGetWhiteBlackListRequest;
import generated.zcsclient.account.testGetWhiteBlackListResponse;
import generated.zcsclient.account.testModifyIdentityRequest;
import generated.zcsclient.account.testModifyIdentityResponse;
import generated.zcsclient.account.testModifyPrefsRequest;
import generated.zcsclient.account.testModifyPrefsResponse;
import generated.zcsclient.account.testModifyPropertiesRequest;
import generated.zcsclient.account.testModifyPropertiesResponse;
import generated.zcsclient.account.testModifySignatureRequest;
import generated.zcsclient.account.testModifySignatureResponse;
import generated.zcsclient.account.testModifyWhiteBlackListRequest;
import generated.zcsclient.account.testModifyWhiteBlackListResponse;
import generated.zcsclient.account.testModifyZimletPrefsRequest;
import generated.zcsclient.account.testModifyZimletPrefsResponse;
import generated.zcsclient.account.testSearchCalendarResourcesRequest;
import generated.zcsclient.account.testSearchCalendarResourcesResponse;
import generated.zcsclient.account.testSearchGalRequest;
import generated.zcsclient.account.testSearchGalResponse;
import generated.zcsclient.account.testSubscribeDistributionListRequest;
import generated.zcsclient.account.testSubscribeDistributionListResponse;
import generated.zcsclient.account.testSyncGalRequest;
import generated.zcsclient.account.testSyncGalResponse;
import generated.zcsclient.account.testUpdateProfileRequest;
import generated.zcsclient.account.testUpdateProfileResponse;
import generated.zcsclient.appblast.testEditDocumentRequest;
import generated.zcsclient.appblast.testEditDocumentResponse;
import generated.zcsclient.appblast.testFinishEditDocumentRequest;
import generated.zcsclient.appblast.testFinishEditDocumentResponse;
import generated.zcsclient.mail.testAddAppointmentInviteRequest;
import generated.zcsclient.mail.testAddAppointmentInviteResponse;
import generated.zcsclient.mail.testAddCommentRequest;
import generated.zcsclient.mail.testAddCommentResponse;
import generated.zcsclient.mail.testAddMsgRequest;
import generated.zcsclient.mail.testAddMsgResponse;
import generated.zcsclient.mail.testAddTaskInviteRequest;
import generated.zcsclient.mail.testAddTaskInviteResponse;
import generated.zcsclient.mail.testAnnounceOrganizerChangeRequest;
import generated.zcsclient.mail.testAnnounceOrganizerChangeResponse;
import generated.zcsclient.mail.testApplyFilterRulesRequest;
import generated.zcsclient.mail.testApplyFilterRulesResponse;
import generated.zcsclient.mail.testApplyOutgoingFilterRulesRequest;
import generated.zcsclient.mail.testApplyOutgoingFilterRulesResponse;
import generated.zcsclient.mail.testAutoCompleteRequest;
import generated.zcsclient.mail.testAutoCompleteResponse;
import generated.zcsclient.mail.testBounceMsgRequest;
import generated.zcsclient.mail.testBounceMsgResponse;
import generated.zcsclient.mail.testBrowseRequest;
import generated.zcsclient.mail.testBrowseResponse;
import generated.zcsclient.mail.testCancelAppointmentRequest;
import generated.zcsclient.mail.testCancelAppointmentResponse;
import generated.zcsclient.mail.testCancelTaskRequest;
import generated.zcsclient.mail.testCancelTaskResponse;
import generated.zcsclient.mail.testCheckDeviceStatusRequest;
import generated.zcsclient.mail.testCheckDeviceStatusResponse;
import generated.zcsclient.mail.testCheckPermissionRequest;
import generated.zcsclient.mail.testCheckPermissionResponse;
import generated.zcsclient.mail.testCheckRecurConflictsRequest;
import generated.zcsclient.mail.testCheckRecurConflictsResponse;
import generated.zcsclient.mail.testCheckSpellingRequest;
import generated.zcsclient.mail.testCheckSpellingResponse;
import generated.zcsclient.mail.testCompleteTaskInstanceRequest;
import generated.zcsclient.mail.testCompleteTaskInstanceResponse;
import generated.zcsclient.mail.testContactActionRequest;
import generated.zcsclient.mail.testContactActionResponse;
import generated.zcsclient.mail.testConvActionRequest;
import generated.zcsclient.mail.testConvActionResponse;
import generated.zcsclient.mail.testCounterAppointmentRequest;
import generated.zcsclient.mail.testCounterAppointmentResponse;
import generated.zcsclient.mail.testCreateAppointmentExceptionRequest;
import generated.zcsclient.mail.testCreateAppointmentExceptionResponse;
import generated.zcsclient.mail.testCreateAppointmentRequest;
import generated.zcsclient.mail.testCreateAppointmentResponse;
import generated.zcsclient.mail.testCreateContactRequest;
import generated.zcsclient.mail.testCreateContactResponse;
import generated.zcsclient.mail.testCreateDataSourceRequest;
import generated.zcsclient.mail.testCreateDataSourceResponse;
import generated.zcsclient.mail.testCreateFolderRequest;
import generated.zcsclient.mail.testCreateFolderResponse;
import generated.zcsclient.mail.testCreateMountpointRequest;
import generated.zcsclient.mail.testCreateMountpointResponse;
import generated.zcsclient.mail.testCreateNoteRequest;
import generated.zcsclient.mail.testCreateNoteResponse;
import generated.zcsclient.mail.testCreateSearchFolderRequest;
import generated.zcsclient.mail.testCreateSearchFolderResponse;
import generated.zcsclient.mail.testCreateTagRequest;
import generated.zcsclient.mail.testCreateTagResponse;
import generated.zcsclient.mail.testCreateTaskExceptionRequest;
import generated.zcsclient.mail.testCreateTaskExceptionResponse;
import generated.zcsclient.mail.testCreateTaskRequest;
import generated.zcsclient.mail.testCreateTaskResponse;
import generated.zcsclient.mail.testCreateWaitSetRequest;
import generated.zcsclient.mail.testCreateWaitSetResponse;
import generated.zcsclient.mail.testDeclineCounterAppointmentRequest;
import generated.zcsclient.mail.testDeclineCounterAppointmentResponse;
import generated.zcsclient.mail.testDeleteDataSourceRequest;
import generated.zcsclient.mail.testDeleteDataSourceResponse;
import generated.zcsclient.mail.testDeleteDeviceRequest;
import generated.zcsclient.mail.testDeleteDeviceResponse;
import generated.zcsclient.mail.testDestroyWaitSetRequest;
import generated.zcsclient.mail.testDestroyWaitSetResponse;
import generated.zcsclient.mail.testDiffDocumentRequest;
import generated.zcsclient.mail.testDiffDocumentResponse;
import generated.zcsclient.mail.testDismissCalendarItemAlarmRequest;
import generated.zcsclient.mail.testDismissCalendarItemAlarmResponse;
import generated.zcsclient.mail.testDocumentActionRequest;
import generated.zcsclient.mail.testDocumentActionResponse;
import generated.zcsclient.mail.testEmptyDumpsterRequest;
import generated.zcsclient.mail.testEmptyDumpsterResponse;
import generated.zcsclient.mail.testEnableSharedReminderRequest;
import generated.zcsclient.mail.testEnableSharedReminderResponse;
import generated.zcsclient.mail.testExpandRecurRequest;
import generated.zcsclient.mail.testExpandRecurResponse;
import generated.zcsclient.mail.testExportContactsRequest;
import generated.zcsclient.mail.testExportContactsResponse;
import generated.zcsclient.mail.testFolderActionRequest;
import generated.zcsclient.mail.testFolderActionResponse;
import generated.zcsclient.mail.testForwardAppointmentInviteRequest;
import generated.zcsclient.mail.testForwardAppointmentInviteResponse;
import generated.zcsclient.mail.testForwardAppointmentRequest;
import generated.zcsclient.mail.testForwardAppointmentResponse;
import generated.zcsclient.mail.testGenerateUUIDRequest;
import generated.zcsclient.mail.testGetActivityStreamRequest;
import generated.zcsclient.mail.testGetActivityStreamResponse;
import generated.zcsclient.mail.testGetAllDevicesRequest;
import generated.zcsclient.mail.testGetAllDevicesResponse;
import generated.zcsclient.mail.testGetAppointmentRequest;
import generated.zcsclient.mail.testGetAppointmentResponse;
import generated.zcsclient.mail.testGetApptSummariesRequest;
import generated.zcsclient.mail.testGetApptSummariesResponse;
import generated.zcsclient.mail.testGetCalendarItemSummariesRequest;
import generated.zcsclient.mail.testGetCalendarItemSummariesResponse;
import generated.zcsclient.mail.testGetCommentsRequest;
import generated.zcsclient.mail.testGetCommentsResponse;
import generated.zcsclient.mail.testGetContactsRequest;
import generated.zcsclient.mail.testGetContactsResponse;
import generated.zcsclient.mail.testGetConvRequest;
import generated.zcsclient.mail.testGetConvResponse;
import generated.zcsclient.mail.testGetCustomMetadataRequest;
import generated.zcsclient.mail.testGetCustomMetadataResponse;
import generated.zcsclient.mail.testGetDataSourcesRequest;
import generated.zcsclient.mail.testGetDataSourcesResponse;
import generated.zcsclient.mail.testGetEffectiveFolderPermsRequest;
import generated.zcsclient.mail.testGetEffectiveFolderPermsResponse;
import generated.zcsclient.mail.testGetFilterRulesRequest;
import generated.zcsclient.mail.testGetFilterRulesResponse;
import generated.zcsclient.mail.testGetFolderRequest;
import generated.zcsclient.mail.testGetFolderResponse;
import generated.zcsclient.mail.testGetFreeBusyRequest;
import generated.zcsclient.mail.testGetFreeBusyResponse;
import generated.zcsclient.mail.testGetICalRequest;
import generated.zcsclient.mail.testGetICalResponse;
import generated.zcsclient.mail.testGetImportStatusRequest;
import generated.zcsclient.mail.testGetImportStatusResponse;
import generated.zcsclient.mail.testGetItemRequest;
import generated.zcsclient.mail.testGetItemResponse;
import generated.zcsclient.mail.testGetMailboxMetadataRequest;
import generated.zcsclient.mail.testGetMailboxMetadataResponse;
import generated.zcsclient.mail.testGetMiniCalRequest;
import generated.zcsclient.mail.testGetMiniCalResponse;
import generated.zcsclient.mail.testGetMsgMetadataRequest;
import generated.zcsclient.mail.testGetMsgMetadataResponse;
import generated.zcsclient.mail.testGetMsgRequest;
import generated.zcsclient.mail.testGetMsgResponse;
import generated.zcsclient.mail.testGetNoteRequest;
import generated.zcsclient.mail.testGetNoteResponse;
import generated.zcsclient.mail.testGetOutgoingFilterRulesRequest;
import generated.zcsclient.mail.testGetOutgoingFilterRulesResponse;
import generated.zcsclient.mail.testGetPermissionRequest;
import generated.zcsclient.mail.testGetPermissionResponse;
import generated.zcsclient.mail.testGetRecurRequest;
import generated.zcsclient.mail.testGetRecurResponse;
import generated.zcsclient.mail.testGetSearchFolderRequest;
import generated.zcsclient.mail.testGetSearchFolderResponse;
import generated.zcsclient.mail.testGetShareNotificationsRequest;
import generated.zcsclient.mail.testGetShareNotificationsResponse;
import generated.zcsclient.mail.testGetSpellDictionariesRequest;
import generated.zcsclient.mail.testGetSpellDictionariesResponse;
import generated.zcsclient.mail.testGetSystemRetentionPolicyRequest;
import generated.zcsclient.mail.testGetSystemRetentionPolicyResponse;
import generated.zcsclient.mail.testGetTagRequest;
import generated.zcsclient.mail.testGetTagResponse;
import generated.zcsclient.mail.testGetTaskRequest;
import generated.zcsclient.mail.testGetTaskResponse;
import generated.zcsclient.mail.testGetTaskSummariesRequest;
import generated.zcsclient.mail.testGetTaskSummariesResponse;
import generated.zcsclient.mail.testGetWatchersRequest;
import generated.zcsclient.mail.testGetWatchersResponse;
import generated.zcsclient.mail.testGetWatchingItemsRequest;
import generated.zcsclient.mail.testGetWatchingItemsResponse;
import generated.zcsclient.mail.testGetWorkingHoursRequest;
import generated.zcsclient.mail.testGetWorkingHoursResponse;
import generated.zcsclient.mail.testGetYahooAuthTokenRequest;
import generated.zcsclient.mail.testGetYahooAuthTokenResponse;
import generated.zcsclient.mail.testGetYahooCookieRequest;
import generated.zcsclient.mail.testGetYahooCookieResponse;
import generated.zcsclient.mail.testGlobalSearchRequest;
import generated.zcsclient.mail.testGlobalSearchResponse;
import generated.zcsclient.mail.testGrantPermissionRequest;
import generated.zcsclient.mail.testGrantPermissionResponse;
import generated.zcsclient.mail.testICalReplyRequest;
import generated.zcsclient.mail.testICalReplyResponse;
import generated.zcsclient.mail.testImportAppointmentsRequest;
import generated.zcsclient.mail.testImportAppointmentsResponse;
import generated.zcsclient.mail.testImportContactsRequest;
import generated.zcsclient.mail.testImportContactsResponse;
import generated.zcsclient.mail.testImportDataRequest;
import generated.zcsclient.mail.testImportDataResponse;
import generated.zcsclient.mail.testInvalidateReminderDeviceRequest;
import generated.zcsclient.mail.testInvalidateReminderDeviceResponse;
import generated.zcsclient.mail.testItemActionRequest;
import generated.zcsclient.mail.testItemActionResponse;
import generated.zcsclient.mail.testListDocumentRevisionsRequest;
import generated.zcsclient.mail.testListDocumentRevisionsResponse;
import generated.zcsclient.mail.testModifyAppointmentRequest;
import generated.zcsclient.mail.testModifyAppointmentResponse;
import generated.zcsclient.mail.testModifyContactRequest;
import generated.zcsclient.mail.testModifyContactResponse;
import generated.zcsclient.mail.testModifyDataSourceRequest;
import generated.zcsclient.mail.testModifyDataSourceResponse;
import generated.zcsclient.mail.testModifyFilterRulesRequest;
import generated.zcsclient.mail.testModifyFilterRulesResponse;
import generated.zcsclient.mail.testModifyMailboxMetadataRequest;
import generated.zcsclient.mail.testModifyMailboxMetadataResponse;
import generated.zcsclient.mail.testModifyOutgoingFilterRulesRequest;
import generated.zcsclient.mail.testModifyOutgoingFilterRulesResponse;
import generated.zcsclient.mail.testModifySearchFolderRequest;
import generated.zcsclient.mail.testModifySearchFolderResponse;
import generated.zcsclient.mail.testModifyTaskRequest;
import generated.zcsclient.mail.testModifyTaskResponse;
import generated.zcsclient.mail.testMsgActionRequest;
import generated.zcsclient.mail.testMsgActionResponse;
import generated.zcsclient.mail.testNoOpRequest;
import generated.zcsclient.mail.testNoOpResponse;
import generated.zcsclient.mail.testNoteActionRequest;
import generated.zcsclient.mail.testNoteActionResponse;
import generated.zcsclient.mail.testPurgeRevisionRequest;
import generated.zcsclient.mail.testPurgeRevisionResponse;
import generated.zcsclient.mail.testRankingActionRequest;
import generated.zcsclient.mail.testRankingActionResponse;
import generated.zcsclient.mail.testRegisterDeviceRequest;
import generated.zcsclient.mail.testRegisterDeviceResponse;
import generated.zcsclient.mail.testRemoveAttachmentsRequest;
import generated.zcsclient.mail.testRemoveAttachmentsResponse;
import generated.zcsclient.mail.testRevokePermissionRequest;
import generated.zcsclient.mail.testRevokePermissionResponse;
import generated.zcsclient.mail.testSaveDocumentRequest;
import generated.zcsclient.mail.testSaveDocumentResponse;
import generated.zcsclient.mail.testSaveDraftRequest;
import generated.zcsclient.mail.testSaveDraftResponse;
import generated.zcsclient.mail.testSearchConvRequest;
import generated.zcsclient.mail.testSearchConvResponse;
import generated.zcsclient.mail.testSearchRequest;
import generated.zcsclient.mail.testSearchResponse;
import generated.zcsclient.mail.testSendDeliveryReportRequest;
import generated.zcsclient.mail.testSendDeliveryReportResponse;
import generated.zcsclient.mail.testSendInviteReplyRequest;
import generated.zcsclient.mail.testSendInviteReplyResponse;
import generated.zcsclient.mail.testSendMsgRequest;
import generated.zcsclient.mail.testSendMsgResponse;
import generated.zcsclient.mail.testSendShareNotificationRequest;
import generated.zcsclient.mail.testSendShareNotificationResponse;
import generated.zcsclient.mail.testSendVerificationCodeRequest;
import generated.zcsclient.mail.testSendVerificationCodeResponse;
import generated.zcsclient.mail.testSetAppointmentRequest;
import generated.zcsclient.mail.testSetAppointmentResponse;
import generated.zcsclient.mail.testSetCustomMetadataRequest;
import generated.zcsclient.mail.testSetCustomMetadataResponse;
import generated.zcsclient.mail.testSetMailboxMetadataRequest;
import generated.zcsclient.mail.testSetMailboxMetadataResponse;
import generated.zcsclient.mail.testSetTaskRequest;
import generated.zcsclient.mail.testSetTaskResponse;
import generated.zcsclient.mail.testSnoozeCalendarItemAlarmRequest;
import generated.zcsclient.mail.testSnoozeCalendarItemAlarmResponse;
import generated.zcsclient.mail.testSyncRequest;
import generated.zcsclient.mail.testSyncResponse;
import generated.zcsclient.mail.testTagActionRequest;
import generated.zcsclient.mail.testTagActionResponse;
import generated.zcsclient.mail.testTestDataSourceRequest;
import generated.zcsclient.mail.testTestDataSourceResponse;
import generated.zcsclient.mail.testUpdateDeviceStatusRequest;
import generated.zcsclient.mail.testUpdateDeviceStatusResponse;
import generated.zcsclient.mail.testVerifyCodeRequest;
import generated.zcsclient.mail.testVerifyCodeResponse;
import generated.zcsclient.mail.testWaitSetRequest;
import generated.zcsclient.mail.testWaitSetResponse;
import generated.zcsclient.mail.testWikiActionRequest;
import generated.zcsclient.mail.testWikiActionResponse;
import generated.zcsclient.replication.testBecomeMasterRequest;
import generated.zcsclient.replication.testBecomeMasterResponse;
import generated.zcsclient.replication.testBringDownServiceIPRequest;
import generated.zcsclient.replication.testBringDownServiceIPResponse;
import generated.zcsclient.replication.testBringUpServiceIPRequest;
import generated.zcsclient.replication.testBringUpServiceIPResponse;
import generated.zcsclient.replication.testReplicationStatusRequest;
import generated.zcsclient.replication.testReplicationStatusResponse;
import generated.zcsclient.replication.testStartCatchupRequest;
import generated.zcsclient.replication.testStartCatchupResponse;
import generated.zcsclient.replication.testStartFailoverClientRequest;
import generated.zcsclient.replication.testStartFailoverClientResponse;
import generated.zcsclient.replication.testStartFailoverDaemonRequest;
import generated.zcsclient.replication.testStartFailoverDaemonResponse;
import generated.zcsclient.replication.testStopFailoverClientRequest;
import generated.zcsclient.replication.testStopFailoverClientResponse;
import generated.zcsclient.replication.testStopFailoverDaemonRequest;
import generated.zcsclient.replication.testStopFailoverDaemonResponse;
import generated.zcsclient.sync.testCancelPendingRemoteWipeRequest;
import generated.zcsclient.sync.testCancelPendingRemoteWipeResponse;
import generated.zcsclient.sync.testGetDeviceStatusRequest;
import generated.zcsclient.sync.testGetDeviceStatusResponse;
import generated.zcsclient.sync.testRemoteWipeRequest;
import generated.zcsclient.sync.testRemoteWipeResponse;
import generated.zcsclient.sync.testRemoveDeviceRequest;
import generated.zcsclient.sync.testRemoveDeviceResponse;
import generated.zcsclient.sync.testResumeDeviceRequest;
import generated.zcsclient.sync.testResumeDeviceResponse;
import generated.zcsclient.sync.testSuspendDeviceRequest;
import generated.zcsclient.sync.testSuspendDeviceResponse;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.1.7-hudson-48-
 * Generated source version: 2.1
 * 
 */
@WebService(name = "zcsPortType", targetNamespace = "http://www.zimbra.com/wsdl/ZimbraService.wsdl")
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE)
@XmlSeeAlso({
    generated.zcsclient.appblast.ObjectFactory.class,
    generated.zcsclient.sync.ObjectFactory.class,
    generated.zcsclient.admin.ObjectFactory.class,
    generated.zcsclient.mail.ObjectFactory.class,
    generated.zcsclient.replication.ObjectFactory.class,
    generated.zcsclient.zm.ObjectFactory.class,
    generated.zcsclient.adminext.ObjectFactory.class,
    generated.zcsclient.account.ObjectFactory.class
})
public interface ZcsPortType {


    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.account.testAuthResponse
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
     *     returns generated.zcsclient.account.testAutoCompleteGalResponse
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
     *     returns generated.zcsclient.account.testChangePasswordResponse
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
     *     returns generated.zcsclient.account.testCheckLicenseResponse
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
     *     returns generated.zcsclient.account.testCreateDistributionListResponse
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
     *     returns generated.zcsclient.account.testCreateIdentityResponse
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
     *     returns generated.zcsclient.account.testCreateSignatureResponse
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
     *     returns generated.zcsclient.account.testDeleteIdentityResponse
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
     *     returns generated.zcsclient.account.testDeleteSignatureResponse
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
     *     returns generated.zcsclient.account.testDistributionListActionResponse
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
     *     returns generated.zcsclient.account.testEndSessionResponse
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
     *     returns generated.zcsclient.account.testGetAccountInfoResponse
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
     *     returns generated.zcsclient.account.testGetAccountMembershipResponse
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
     *     returns generated.zcsclient.account.testGetAllLocalesResponse
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
     *     returns generated.zcsclient.account.testGetAvailableCsvFormatsResponse
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
     *     returns generated.zcsclient.account.testGetAvailableLocalesResponse
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
     *     returns generated.zcsclient.account.testGetAvailableSkinsResponse
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
     *     returns generated.zcsclient.account.testGetDistributionListMembersResponse
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
     *     returns generated.zcsclient.account.testGetDistributionListResponse
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
     *     returns generated.zcsclient.account.testGetIdentitiesResponse
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
     *     returns generated.zcsclient.account.testGetInfoResponse
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
     *     returns generated.zcsclient.account.testGetPrefsResponse
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
     *     returns generated.zcsclient.account.testGetSMIMEPublicCertsResponse
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
     *     returns generated.zcsclient.account.testGetShareInfoResponse
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
     *     returns generated.zcsclient.account.testGetSignaturesResponse
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
     *     returns generated.zcsclient.account.testGetVersionInfoResponse
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
     *     returns generated.zcsclient.account.testGetWhiteBlackListResponse
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
     *     returns generated.zcsclient.account.testModifyIdentityResponse
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
     *     returns generated.zcsclient.account.testModifyPrefsResponse
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
     *     returns generated.zcsclient.account.testModifyPropertiesResponse
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
     *     returns generated.zcsclient.account.testModifySignatureResponse
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
     *     returns generated.zcsclient.account.testModifyWhiteBlackListResponse
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
     *     returns generated.zcsclient.account.testModifyZimletPrefsResponse
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
     *     returns generated.zcsclient.account.testSearchCalendarResourcesResponse
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
     *     returns generated.zcsclient.account.testSearchGalResponse
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
     *     returns generated.zcsclient.account.testSubscribeDistributionListResponse
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
     *     returns generated.zcsclient.account.testSyncGalResponse
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
     *     returns generated.zcsclient.account.testUpdateProfileResponse
     */
    @WebMethod(action = "urn:zimbraAccount/UpdateProfile")
    @WebResult(name = "UpdateProfileResponse", targetNamespace = "urn:zimbraAccount", partName = "parameters")
    public testUpdateProfileResponse updateProfileRequest(
        @WebParam(name = "UpdateProfileRequest", targetNamespace = "urn:zimbraAccount", partName = "parameters")
        testUpdateProfileRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.mail.testAddAppointmentInviteResponse
     */
    @WebMethod(action = "urn:zimbraMail/AddAppointmentInvite")
    @WebResult(name = "AddAppointmentInviteResponse", targetNamespace = "urn:zimbraMail", partName = "parameters")
    public testAddAppointmentInviteResponse addAppointmentInviteRequest(
        @WebParam(name = "AddAppointmentInviteRequest", targetNamespace = "urn:zimbraMail", partName = "parameters")
        testAddAppointmentInviteRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.mail.testAddCommentResponse
     */
    @WebMethod(action = "urn:zimbraMail/AddComment")
    @WebResult(name = "AddCommentResponse", targetNamespace = "urn:zimbraMail", partName = "parameters")
    public testAddCommentResponse addCommentRequest(
        @WebParam(name = "AddCommentRequest", targetNamespace = "urn:zimbraMail", partName = "parameters")
        testAddCommentRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.mail.testAddMsgResponse
     */
    @WebMethod(action = "urn:zimbraMail/AddMsg")
    @WebResult(name = "AddMsgResponse", targetNamespace = "urn:zimbraMail", partName = "parameters")
    public testAddMsgResponse addMsgRequest(
        @WebParam(name = "AddMsgRequest", targetNamespace = "urn:zimbraMail", partName = "parameters")
        testAddMsgRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.mail.testAddTaskInviteResponse
     */
    @WebMethod(action = "urn:zimbraMail/AddTaskInvite")
    @WebResult(name = "AddTaskInviteResponse", targetNamespace = "urn:zimbraMail", partName = "parameters")
    public testAddTaskInviteResponse addTaskInviteRequest(
        @WebParam(name = "AddTaskInviteRequest", targetNamespace = "urn:zimbraMail", partName = "parameters")
        testAddTaskInviteRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.mail.testAnnounceOrganizerChangeResponse
     */
    @WebMethod(action = "urn:zimbraMail/AnnounceOrganizerChange")
    @WebResult(name = "AnnounceOrganizerChangeResponse", targetNamespace = "urn:zimbraMail", partName = "parameters")
    public testAnnounceOrganizerChangeResponse announceOrganizerChangeRequest(
        @WebParam(name = "AnnounceOrganizerChangeRequest", targetNamespace = "urn:zimbraMail", partName = "parameters")
        testAnnounceOrganizerChangeRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.mail.testApplyFilterRulesResponse
     */
    @WebMethod(action = "urn:zimbraMail/ApplyFilterRules")
    @WebResult(name = "ApplyFilterRulesResponse", targetNamespace = "urn:zimbraMail", partName = "parameters")
    public testApplyFilterRulesResponse applyFilterRulesRequest(
        @WebParam(name = "ApplyFilterRulesRequest", targetNamespace = "urn:zimbraMail", partName = "parameters")
        testApplyFilterRulesRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.mail.testApplyOutgoingFilterRulesResponse
     */
    @WebMethod(action = "urn:zimbraMail/ApplyOutgoingFilterRules")
    @WebResult(name = "ApplyOutgoingFilterRulesResponse", targetNamespace = "urn:zimbraMail", partName = "parameters")
    public testApplyOutgoingFilterRulesResponse applyOutgoingFilterRulesRequest(
        @WebParam(name = "ApplyOutgoingFilterRulesRequest", targetNamespace = "urn:zimbraMail", partName = "parameters")
        testApplyOutgoingFilterRulesRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.mail.testAutoCompleteResponse
     */
    @WebMethod(action = "urn:zimbraMail/AutoComplete")
    @WebResult(name = "AutoCompleteResponse", targetNamespace = "urn:zimbraMail", partName = "parameters")
    public testAutoCompleteResponse autoCompleteRequest(
        @WebParam(name = "AutoCompleteRequest", targetNamespace = "urn:zimbraMail", partName = "parameters")
        testAutoCompleteRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.mail.testBounceMsgResponse
     */
    @WebMethod(action = "urn:zimbraMail/BounceMsg")
    @WebResult(name = "BounceMsgResponse", targetNamespace = "urn:zimbraMail", partName = "parameters")
    public testBounceMsgResponse bounceMsgRequest(
        @WebParam(name = "BounceMsgRequest", targetNamespace = "urn:zimbraMail", partName = "parameters")
        testBounceMsgRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.mail.testBrowseResponse
     */
    @WebMethod(action = "urn:zimbraMail/Browse")
    @WebResult(name = "BrowseResponse", targetNamespace = "urn:zimbraMail", partName = "parameters")
    public testBrowseResponse browseRequest(
        @WebParam(name = "BrowseRequest", targetNamespace = "urn:zimbraMail", partName = "parameters")
        testBrowseRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.mail.testCancelAppointmentResponse
     */
    @WebMethod(action = "urn:zimbraMail/CancelAppointment")
    @WebResult(name = "CancelAppointmentResponse", targetNamespace = "urn:zimbraMail", partName = "parameters")
    public testCancelAppointmentResponse cancelAppointmentRequest(
        @WebParam(name = "CancelAppointmentRequest", targetNamespace = "urn:zimbraMail", partName = "parameters")
        testCancelAppointmentRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.mail.testCancelTaskResponse
     */
    @WebMethod(action = "urn:zimbraMail/CancelTask")
    @WebResult(name = "CancelTaskResponse", targetNamespace = "urn:zimbraMail", partName = "parameters")
    public testCancelTaskResponse cancelTaskRequest(
        @WebParam(name = "CancelTaskRequest", targetNamespace = "urn:zimbraMail", partName = "parameters")
        testCancelTaskRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.mail.testCheckDeviceStatusResponse
     */
    @WebMethod(action = "urn:zimbraMail/CheckDeviceStatus")
    @WebResult(name = "CheckDeviceStatusResponse", targetNamespace = "urn:zimbraMail", partName = "parameters")
    public testCheckDeviceStatusResponse checkDeviceStatusRequest(
        @WebParam(name = "CheckDeviceStatusRequest", targetNamespace = "urn:zimbraMail", partName = "parameters")
        testCheckDeviceStatusRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.mail.testCheckPermissionResponse
     */
    @WebMethod(action = "urn:zimbraMail/CheckPermission")
    @WebResult(name = "CheckPermissionResponse", targetNamespace = "urn:zimbraMail", partName = "parameters")
    public testCheckPermissionResponse checkPermissionRequest(
        @WebParam(name = "CheckPermissionRequest", targetNamespace = "urn:zimbraMail", partName = "parameters")
        testCheckPermissionRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.mail.testCheckRecurConflictsResponse
     */
    @WebMethod(action = "urn:zimbraMail/CheckRecurConflicts")
    @WebResult(name = "CheckRecurConflictsResponse", targetNamespace = "urn:zimbraMail", partName = "parameters")
    public testCheckRecurConflictsResponse checkRecurConflictsRequest(
        @WebParam(name = "CheckRecurConflictsRequest", targetNamespace = "urn:zimbraMail", partName = "parameters")
        testCheckRecurConflictsRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.mail.testCheckSpellingResponse
     */
    @WebMethod(action = "urn:zimbraMail/CheckSpelling")
    @WebResult(name = "CheckSpellingResponse", targetNamespace = "urn:zimbraMail", partName = "parameters")
    public testCheckSpellingResponse checkSpellingRequest(
        @WebParam(name = "CheckSpellingRequest", targetNamespace = "urn:zimbraMail", partName = "parameters")
        testCheckSpellingRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.mail.testCompleteTaskInstanceResponse
     */
    @WebMethod(action = "urn:zimbraMail/CompleteTaskInstance")
    @WebResult(name = "CompleteTaskInstanceResponse", targetNamespace = "urn:zimbraMail", partName = "parameters")
    public testCompleteTaskInstanceResponse completeTaskInstanceRequest(
        @WebParam(name = "CompleteTaskInstanceRequest", targetNamespace = "urn:zimbraMail", partName = "parameters")
        testCompleteTaskInstanceRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.mail.testContactActionResponse
     */
    @WebMethod(action = "urn:zimbraMail/ContactAction")
    @WebResult(name = "ContactActionResponse", targetNamespace = "urn:zimbraMail", partName = "parameters")
    public testContactActionResponse contactActionRequest(
        @WebParam(name = "ContactActionRequest", targetNamespace = "urn:zimbraMail", partName = "parameters")
        testContactActionRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.mail.testConvActionResponse
     */
    @WebMethod(action = "urn:zimbraMail/ConvAction")
    @WebResult(name = "ConvActionResponse", targetNamespace = "urn:zimbraMail", partName = "parameters")
    public testConvActionResponse convActionRequest(
        @WebParam(name = "ConvActionRequest", targetNamespace = "urn:zimbraMail", partName = "parameters")
        testConvActionRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.mail.testCounterAppointmentResponse
     */
    @WebMethod(action = "urn:zimbraMail/CounterAppointment")
    @WebResult(name = "CounterAppointmentResponse", targetNamespace = "urn:zimbraMail", partName = "parameters")
    public testCounterAppointmentResponse counterAppointmentRequest(
        @WebParam(name = "CounterAppointmentRequest", targetNamespace = "urn:zimbraMail", partName = "parameters")
        testCounterAppointmentRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.mail.testCreateAppointmentExceptionResponse
     */
    @WebMethod(action = "urn:zimbraMail/CreateAppointmentException")
    @WebResult(name = "CreateAppointmentExceptionResponse", targetNamespace = "urn:zimbraMail", partName = "parameters")
    public testCreateAppointmentExceptionResponse createAppointmentExceptionRequest(
        @WebParam(name = "CreateAppointmentExceptionRequest", targetNamespace = "urn:zimbraMail", partName = "parameters")
        testCreateAppointmentExceptionRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.mail.testCreateAppointmentResponse
     */
    @WebMethod(action = "urn:zimbraMail/CreateAppointment")
    @WebResult(name = "CreateAppointmentResponse", targetNamespace = "urn:zimbraMail", partName = "parameters")
    public testCreateAppointmentResponse createAppointmentRequest(
        @WebParam(name = "CreateAppointmentRequest", targetNamespace = "urn:zimbraMail", partName = "parameters")
        testCreateAppointmentRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.mail.testCreateContactResponse
     */
    @WebMethod(action = "urn:zimbraMail/CreateContact")
    @WebResult(name = "CreateContactResponse", targetNamespace = "urn:zimbraMail", partName = "parameters")
    public testCreateContactResponse createContactRequest(
        @WebParam(name = "CreateContactRequest", targetNamespace = "urn:zimbraMail", partName = "parameters")
        testCreateContactRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.mail.testCreateDataSourceResponse
     */
    @WebMethod(action = "urn:zimbraMail/CreateDataSource")
    @WebResult(name = "CreateDataSourceResponse", targetNamespace = "urn:zimbraMail", partName = "parameters")
    public testCreateDataSourceResponse createDataSourceRequest(
        @WebParam(name = "CreateDataSourceRequest", targetNamespace = "urn:zimbraMail", partName = "parameters")
        testCreateDataSourceRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.mail.testCreateFolderResponse
     */
    @WebMethod(action = "urn:zimbraMail/CreateFolder")
    @WebResult(name = "CreateFolderResponse", targetNamespace = "urn:zimbraMail", partName = "parameters")
    public testCreateFolderResponse createFolderRequest(
        @WebParam(name = "CreateFolderRequest", targetNamespace = "urn:zimbraMail", partName = "parameters")
        testCreateFolderRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.mail.testCreateMountpointResponse
     */
    @WebMethod(action = "urn:zimbraMail/CreateMountpoint")
    @WebResult(name = "CreateMountpointResponse", targetNamespace = "urn:zimbraMail", partName = "parameters")
    public testCreateMountpointResponse createMountpointRequest(
        @WebParam(name = "CreateMountpointRequest", targetNamespace = "urn:zimbraMail", partName = "parameters")
        testCreateMountpointRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.mail.testCreateNoteResponse
     */
    @WebMethod(action = "urn:zimbraMail/CreateNote")
    @WebResult(name = "CreateNoteResponse", targetNamespace = "urn:zimbraMail", partName = "parameters")
    public testCreateNoteResponse createNoteRequest(
        @WebParam(name = "CreateNoteRequest", targetNamespace = "urn:zimbraMail", partName = "parameters")
        testCreateNoteRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.mail.testCreateSearchFolderResponse
     */
    @WebMethod(action = "urn:zimbraMail/CreateSearchFolder")
    @WebResult(name = "CreateSearchFolderResponse", targetNamespace = "urn:zimbraMail", partName = "parameters")
    public testCreateSearchFolderResponse createSearchFolderRequest(
        @WebParam(name = "CreateSearchFolderRequest", targetNamespace = "urn:zimbraMail", partName = "parameters")
        testCreateSearchFolderRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.mail.testCreateTagResponse
     */
    @WebMethod(action = "urn:zimbraMail/CreateTag")
    @WebResult(name = "CreateTagResponse", targetNamespace = "urn:zimbraMail", partName = "parameters")
    public testCreateTagResponse createTagRequest(
        @WebParam(name = "CreateTagRequest", targetNamespace = "urn:zimbraMail", partName = "parameters")
        testCreateTagRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.mail.testCreateTaskExceptionResponse
     */
    @WebMethod(action = "urn:zimbraMail/CreateTaskException")
    @WebResult(name = "CreateTaskExceptionResponse", targetNamespace = "urn:zimbraMail", partName = "parameters")
    public testCreateTaskExceptionResponse createTaskExceptionRequest(
        @WebParam(name = "CreateTaskExceptionRequest", targetNamespace = "urn:zimbraMail", partName = "parameters")
        testCreateTaskExceptionRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.mail.testCreateTaskResponse
     */
    @WebMethod(action = "urn:zimbraMail/CreateTask")
    @WebResult(name = "CreateTaskResponse", targetNamespace = "urn:zimbraMail", partName = "parameters")
    public testCreateTaskResponse createTaskRequest(
        @WebParam(name = "CreateTaskRequest", targetNamespace = "urn:zimbraMail", partName = "parameters")
        testCreateTaskRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.mail.testCreateWaitSetResponse
     */
    @WebMethod(action = "urn:zimbraMail/CreateWaitSet")
    @WebResult(name = "CreateWaitSetResponse", targetNamespace = "urn:zimbraMail", partName = "parameters")
    public testCreateWaitSetResponse createWaitSetRequest(
        @WebParam(name = "CreateWaitSetRequest", targetNamespace = "urn:zimbraMail", partName = "parameters")
        testCreateWaitSetRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.mail.testDeclineCounterAppointmentResponse
     */
    @WebMethod(action = "urn:zimbraMail/DeclineCounterAppointment")
    @WebResult(name = "DeclineCounterAppointmentResponse", targetNamespace = "urn:zimbraMail", partName = "parameters")
    public testDeclineCounterAppointmentResponse declineCounterAppointmentRequest(
        @WebParam(name = "DeclineCounterAppointmentRequest", targetNamespace = "urn:zimbraMail", partName = "parameters")
        testDeclineCounterAppointmentRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.mail.testDeleteDataSourceResponse
     */
    @WebMethod(action = "urn:zimbraMail/DeleteDataSource")
    @WebResult(name = "DeleteDataSourceResponse", targetNamespace = "urn:zimbraMail", partName = "parameters")
    public testDeleteDataSourceResponse deleteDataSourceRequest(
        @WebParam(name = "DeleteDataSourceRequest", targetNamespace = "urn:zimbraMail", partName = "parameters")
        testDeleteDataSourceRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.mail.testDeleteDeviceResponse
     */
    @WebMethod(action = "urn:zimbraMail/DeleteDevice")
    @WebResult(name = "DeleteDeviceResponse", targetNamespace = "urn:zimbraMail", partName = "parameters")
    public testDeleteDeviceResponse deleteDeviceRequest(
        @WebParam(name = "DeleteDeviceRequest", targetNamespace = "urn:zimbraMail", partName = "parameters")
        testDeleteDeviceRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.mail.testDestroyWaitSetResponse
     */
    @WebMethod(action = "urn:zimbraMail/DestroyWaitSet")
    @WebResult(name = "DestroyWaitSetResponse", targetNamespace = "urn:zimbraMail", partName = "parameters")
    public testDestroyWaitSetResponse destroyWaitSetRequest(
        @WebParam(name = "DestroyWaitSetRequest", targetNamespace = "urn:zimbraMail", partName = "parameters")
        testDestroyWaitSetRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.mail.testDiffDocumentResponse
     */
    @WebMethod(action = "urn:zimbraMail/DiffDocument")
    @WebResult(name = "DiffDocumentResponse", targetNamespace = "urn:zimbraMail", partName = "parameters")
    public testDiffDocumentResponse diffDocumentRequest(
        @WebParam(name = "DiffDocumentRequest", targetNamespace = "urn:zimbraMail", partName = "parameters")
        testDiffDocumentRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.mail.testDismissCalendarItemAlarmResponse
     */
    @WebMethod(action = "urn:zimbraMail/DismissCalendarItemAlarm")
    @WebResult(name = "DismissCalendarItemAlarmResponse", targetNamespace = "urn:zimbraMail", partName = "parameters")
    public testDismissCalendarItemAlarmResponse dismissCalendarItemAlarmRequest(
        @WebParam(name = "DismissCalendarItemAlarmRequest", targetNamespace = "urn:zimbraMail", partName = "parameters")
        testDismissCalendarItemAlarmRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.mail.testDocumentActionResponse
     */
    @WebMethod(action = "urn:zimbraMail/DocumentAction")
    @WebResult(name = "DocumentActionResponse", targetNamespace = "urn:zimbraMail", partName = "parameters")
    public testDocumentActionResponse documentActionRequest(
        @WebParam(name = "DocumentActionRequest", targetNamespace = "urn:zimbraMail", partName = "parameters")
        testDocumentActionRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.mail.testEmptyDumpsterResponse
     */
    @WebMethod(action = "urn:zimbraMail/EmptyDumpster")
    @WebResult(name = "EmptyDumpsterResponse", targetNamespace = "urn:zimbraMail", partName = "parameters")
    public testEmptyDumpsterResponse emptyDumpsterRequest(
        @WebParam(name = "EmptyDumpsterRequest", targetNamespace = "urn:zimbraMail", partName = "parameters")
        testEmptyDumpsterRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.mail.testEnableSharedReminderResponse
     */
    @WebMethod(action = "urn:zimbraMail/EnableSharedReminder")
    @WebResult(name = "EnableSharedReminderResponse", targetNamespace = "urn:zimbraMail", partName = "parameters")
    public testEnableSharedReminderResponse enableSharedReminderRequest(
        @WebParam(name = "EnableSharedReminderRequest", targetNamespace = "urn:zimbraMail", partName = "parameters")
        testEnableSharedReminderRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.mail.testExpandRecurResponse
     */
    @WebMethod(action = "urn:zimbraMail/ExpandRecur")
    @WebResult(name = "ExpandRecurResponse", targetNamespace = "urn:zimbraMail", partName = "parameters")
    public testExpandRecurResponse expandRecurRequest(
        @WebParam(name = "ExpandRecurRequest", targetNamespace = "urn:zimbraMail", partName = "parameters")
        testExpandRecurRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.mail.testExportContactsResponse
     */
    @WebMethod(action = "urn:zimbraMail/ExportContacts")
    @WebResult(name = "ExportContactsResponse", targetNamespace = "urn:zimbraMail", partName = "parameters")
    public testExportContactsResponse exportContactsRequest(
        @WebParam(name = "ExportContactsRequest", targetNamespace = "urn:zimbraMail", partName = "parameters")
        testExportContactsRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.mail.testFolderActionResponse
     */
    @WebMethod(action = "urn:zimbraMail/FolderAction")
    @WebResult(name = "FolderActionResponse", targetNamespace = "urn:zimbraMail", partName = "parameters")
    public testFolderActionResponse folderActionRequest(
        @WebParam(name = "FolderActionRequest", targetNamespace = "urn:zimbraMail", partName = "parameters")
        testFolderActionRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.mail.testForwardAppointmentInviteResponse
     */
    @WebMethod(action = "urn:zimbraMail/ForwardAppointmentInvite")
    @WebResult(name = "ForwardAppointmentInviteResponse", targetNamespace = "urn:zimbraMail", partName = "parameters")
    public testForwardAppointmentInviteResponse forwardAppointmentInviteRequest(
        @WebParam(name = "ForwardAppointmentInviteRequest", targetNamespace = "urn:zimbraMail", partName = "parameters")
        testForwardAppointmentInviteRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.mail.testForwardAppointmentResponse
     */
    @WebMethod(action = "urn:zimbraMail/ForwardAppointment")
    @WebResult(name = "ForwardAppointmentResponse", targetNamespace = "urn:zimbraMail", partName = "parameters")
    public testForwardAppointmentResponse forwardAppointmentRequest(
        @WebParam(name = "ForwardAppointmentRequest", targetNamespace = "urn:zimbraMail", partName = "parameters")
        testForwardAppointmentRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns java.lang.String
     */
    @WebMethod(action = "urn:zimbraMail/GenerateUUID")
    @WebResult(name = "GenerateUUIDResponse", targetNamespace = "urn:zimbraMail", partName = "parameters")
    public String generateUUIDRequest(
        @WebParam(name = "GenerateUUIDRequest", targetNamespace = "urn:zimbraMail", partName = "parameters")
        testGenerateUUIDRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.mail.testGetActivityStreamResponse
     */
    @WebMethod(action = "urn:zimbraMail/GetActivityStream")
    @WebResult(name = "GetActivityStreamResponse", targetNamespace = "urn:zimbraMail", partName = "parameters")
    public testGetActivityStreamResponse getActivityStreamRequest(
        @WebParam(name = "GetActivityStreamRequest", targetNamespace = "urn:zimbraMail", partName = "parameters")
        testGetActivityStreamRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.mail.testGetAllDevicesResponse
     */
    @WebMethod(action = "urn:zimbraMail/GetAllDevices")
    @WebResult(name = "GetAllDevicesResponse", targetNamespace = "urn:zimbraMail", partName = "parameters")
    public testGetAllDevicesResponse getAllDevicesRequest(
        @WebParam(name = "GetAllDevicesRequest", targetNamespace = "urn:zimbraMail", partName = "parameters")
        testGetAllDevicesRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.mail.testGetAppointmentResponse
     */
    @WebMethod(action = "urn:zimbraMail/GetAppointment")
    @WebResult(name = "GetAppointmentResponse", targetNamespace = "urn:zimbraMail", partName = "parameters")
    public testGetAppointmentResponse getAppointmentRequest(
        @WebParam(name = "GetAppointmentRequest", targetNamespace = "urn:zimbraMail", partName = "parameters")
        testGetAppointmentRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.mail.testGetApptSummariesResponse
     */
    @WebMethod(action = "urn:zimbraMail/GetApptSummaries")
    @WebResult(name = "GetApptSummariesResponse", targetNamespace = "urn:zimbraMail", partName = "parameters")
    public testGetApptSummariesResponse getApptSummariesRequest(
        @WebParam(name = "GetApptSummariesRequest", targetNamespace = "urn:zimbraMail", partName = "parameters")
        testGetApptSummariesRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.mail.testGetCalendarItemSummariesResponse
     */
    @WebMethod(action = "urn:zimbraMail/GetCalendarItemSummaries")
    @WebResult(name = "GetCalendarItemSummariesResponse", targetNamespace = "urn:zimbraMail", partName = "parameters")
    public testGetCalendarItemSummariesResponse getCalendarItemSummariesRequest(
        @WebParam(name = "GetCalendarItemSummariesRequest", targetNamespace = "urn:zimbraMail", partName = "parameters")
        testGetCalendarItemSummariesRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.mail.testGetCommentsResponse
     */
    @WebMethod(action = "urn:zimbraMail/GetComments")
    @WebResult(name = "GetCommentsResponse", targetNamespace = "urn:zimbraMail", partName = "parameters")
    public testGetCommentsResponse getCommentsRequest(
        @WebParam(name = "GetCommentsRequest", targetNamespace = "urn:zimbraMail", partName = "parameters")
        testGetCommentsRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.mail.testGetContactsResponse
     */
    @WebMethod(action = "urn:zimbraMail/GetContacts")
    @WebResult(name = "GetContactsResponse", targetNamespace = "urn:zimbraMail", partName = "parameters")
    public testGetContactsResponse getContactsRequest(
        @WebParam(name = "GetContactsRequest", targetNamespace = "urn:zimbraMail", partName = "parameters")
        testGetContactsRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.mail.testGetConvResponse
     */
    @WebMethod(action = "urn:zimbraMail/GetConv")
    @WebResult(name = "GetConvResponse", targetNamespace = "urn:zimbraMail", partName = "parameters")
    public testGetConvResponse getConvRequest(
        @WebParam(name = "GetConvRequest", targetNamespace = "urn:zimbraMail", partName = "parameters")
        testGetConvRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.mail.testGetCustomMetadataResponse
     */
    @WebMethod(action = "urn:zimbraMail/GetCustomMetadata")
    @WebResult(name = "GetCustomMetadataResponse", targetNamespace = "urn:zimbraMail", partName = "parameters")
    public testGetCustomMetadataResponse getCustomMetadataRequest(
        @WebParam(name = "GetCustomMetadataRequest", targetNamespace = "urn:zimbraMail", partName = "parameters")
        testGetCustomMetadataRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.mail.testGetDataSourcesResponse
     */
    @WebMethod(action = "urn:zimbraMail/GetDataSources")
    @WebResult(name = "GetDataSourcesResponse", targetNamespace = "urn:zimbraMail", partName = "parameters")
    public testGetDataSourcesResponse getDataSourcesRequest(
        @WebParam(name = "GetDataSourcesRequest", targetNamespace = "urn:zimbraMail", partName = "parameters")
        testGetDataSourcesRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.mail.testGetEffectiveFolderPermsResponse
     */
    @WebMethod(action = "urn:zimbraMail/GetEffectiveFolderPerms")
    @WebResult(name = "GetEffectiveFolderPermsResponse", targetNamespace = "urn:zimbraMail", partName = "parameters")
    public testGetEffectiveFolderPermsResponse getEffectiveFolderPermsRequest(
        @WebParam(name = "GetEffectiveFolderPermsRequest", targetNamespace = "urn:zimbraMail", partName = "parameters")
        testGetEffectiveFolderPermsRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.mail.testGetFilterRulesResponse
     */
    @WebMethod(action = "urn:zimbraMail/GetFilterRules")
    @WebResult(name = "GetFilterRulesResponse", targetNamespace = "urn:zimbraMail", partName = "parameters")
    public testGetFilterRulesResponse getFilterRulesRequest(
        @WebParam(name = "GetFilterRulesRequest", targetNamespace = "urn:zimbraMail", partName = "parameters")
        testGetFilterRulesRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.mail.testGetFolderResponse
     */
    @WebMethod(action = "urn:zimbraMail/GetFolder")
    @WebResult(name = "GetFolderResponse", targetNamespace = "urn:zimbraMail", partName = "parameters")
    public testGetFolderResponse getFolderRequest(
        @WebParam(name = "GetFolderRequest", targetNamespace = "urn:zimbraMail", partName = "parameters")
        testGetFolderRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.mail.testGetFreeBusyResponse
     */
    @WebMethod(action = "urn:zimbraMail/GetFreeBusy")
    @WebResult(name = "GetFreeBusyResponse", targetNamespace = "urn:zimbraMail", partName = "parameters")
    public testGetFreeBusyResponse getFreeBusyRequest(
        @WebParam(name = "GetFreeBusyRequest", targetNamespace = "urn:zimbraMail", partName = "parameters")
        testGetFreeBusyRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.mail.testGetICalResponse
     */
    @WebMethod(action = "urn:zimbraMail/GetICal")
    @WebResult(name = "GetICalResponse", targetNamespace = "urn:zimbraMail", partName = "parameters")
    public testGetICalResponse getICalRequest(
        @WebParam(name = "GetICalRequest", targetNamespace = "urn:zimbraMail", partName = "parameters")
        testGetICalRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.mail.testGetImportStatusResponse
     */
    @WebMethod(action = "urn:zimbraMail/GetImportStatus")
    @WebResult(name = "GetImportStatusResponse", targetNamespace = "urn:zimbraMail", partName = "parameters")
    public testGetImportStatusResponse getImportStatusRequest(
        @WebParam(name = "GetImportStatusRequest", targetNamespace = "urn:zimbraMail", partName = "parameters")
        testGetImportStatusRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.mail.testGetItemResponse
     */
    @WebMethod(action = "urn:zimbraMail/GetItem")
    @WebResult(name = "GetItemResponse", targetNamespace = "urn:zimbraMail", partName = "parameters")
    public testGetItemResponse getItemRequest(
        @WebParam(name = "GetItemRequest", targetNamespace = "urn:zimbraMail", partName = "parameters")
        testGetItemRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.mail.testGetMailboxMetadataResponse
     */
    @WebMethod(action = "urn:zimbraMail/GetMailboxMetadata")
    @WebResult(name = "GetMailboxMetadataResponse", targetNamespace = "urn:zimbraMail", partName = "parameters")
    public testGetMailboxMetadataResponse getMailboxMetadataRequest(
        @WebParam(name = "GetMailboxMetadataRequest", targetNamespace = "urn:zimbraMail", partName = "parameters")
        testGetMailboxMetadataRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.mail.testGetMiniCalResponse
     */
    @WebMethod(action = "urn:zimbraMail/GetMiniCal")
    @WebResult(name = "GetMiniCalResponse", targetNamespace = "urn:zimbraMail", partName = "parameters")
    public testGetMiniCalResponse getMiniCalRequest(
        @WebParam(name = "GetMiniCalRequest", targetNamespace = "urn:zimbraMail", partName = "parameters")
        testGetMiniCalRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.mail.testGetMsgMetadataResponse
     */
    @WebMethod(action = "urn:zimbraMail/GetMsgMetadata")
    @WebResult(name = "GetMsgMetadataResponse", targetNamespace = "urn:zimbraMail", partName = "parameters")
    public testGetMsgMetadataResponse getMsgMetadataRequest(
        @WebParam(name = "GetMsgMetadataRequest", targetNamespace = "urn:zimbraMail", partName = "parameters")
        testGetMsgMetadataRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.mail.testGetMsgResponse
     */
    @WebMethod(action = "urn:zimbraMail/GetMsg")
    @WebResult(name = "GetMsgResponse", targetNamespace = "urn:zimbraMail", partName = "parameters")
    public testGetMsgResponse getMsgRequest(
        @WebParam(name = "GetMsgRequest", targetNamespace = "urn:zimbraMail", partName = "parameters")
        testGetMsgRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.mail.testGetNoteResponse
     */
    @WebMethod(action = "urn:zimbraMail/GetNote")
    @WebResult(name = "GetNoteResponse", targetNamespace = "urn:zimbraMail", partName = "parameters")
    public testGetNoteResponse getNoteRequest(
        @WebParam(name = "GetNoteRequest", targetNamespace = "urn:zimbraMail", partName = "parameters")
        testGetNoteRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.mail.testGetOutgoingFilterRulesResponse
     */
    @WebMethod(action = "urn:zimbraMail/GetOutgoingFilterRules")
    @WebResult(name = "GetOutgoingFilterRulesResponse", targetNamespace = "urn:zimbraMail", partName = "parameters")
    public testGetOutgoingFilterRulesResponse getOutgoingFilterRulesRequest(
        @WebParam(name = "GetOutgoingFilterRulesRequest", targetNamespace = "urn:zimbraMail", partName = "parameters")
        testGetOutgoingFilterRulesRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.mail.testGetPermissionResponse
     */
    @WebMethod(action = "urn:zimbraMail/GetPermission")
    @WebResult(name = "GetPermissionResponse", targetNamespace = "urn:zimbraMail", partName = "parameters")
    public testGetPermissionResponse getPermissionRequest(
        @WebParam(name = "GetPermissionRequest", targetNamespace = "urn:zimbraMail", partName = "parameters")
        testGetPermissionRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.mail.testGetRecurResponse
     */
    @WebMethod(action = "urn:zimbraMail/GetRecur")
    @WebResult(name = "GetRecurResponse", targetNamespace = "urn:zimbraMail", partName = "parameters")
    public testGetRecurResponse getRecurRequest(
        @WebParam(name = "GetRecurRequest", targetNamespace = "urn:zimbraMail", partName = "parameters")
        testGetRecurRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.mail.testGetSearchFolderResponse
     */
    @WebMethod(action = "urn:zimbraMail/GetSearchFolder")
    @WebResult(name = "GetSearchFolderResponse", targetNamespace = "urn:zimbraMail", partName = "parameters")
    public testGetSearchFolderResponse getSearchFolderRequest(
        @WebParam(name = "GetSearchFolderRequest", targetNamespace = "urn:zimbraMail", partName = "parameters")
        testGetSearchFolderRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.mail.testGetShareNotificationsResponse
     */
    @WebMethod(action = "urn:zimbraMail/GetShareNotifications")
    @WebResult(name = "GetShareNotificationsResponse", targetNamespace = "urn:zimbraMail", partName = "parameters")
    public testGetShareNotificationsResponse getShareNotificationsRequest(
        @WebParam(name = "GetShareNotificationsRequest", targetNamespace = "urn:zimbraMail", partName = "parameters")
        testGetShareNotificationsRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.mail.testGetSpellDictionariesResponse
     */
    @WebMethod(action = "urn:zimbraMail/GetSpellDictionaries")
    @WebResult(name = "GetSpellDictionariesResponse", targetNamespace = "urn:zimbraMail", partName = "parameters")
    public testGetSpellDictionariesResponse getSpellDictionariesRequest(
        @WebParam(name = "GetSpellDictionariesRequest", targetNamespace = "urn:zimbraMail", partName = "parameters")
        testGetSpellDictionariesRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.mail.testGetSystemRetentionPolicyResponse
     */
    @WebMethod(action = "urn:zimbraMail/GetSystemRetentionPolicy")
    @WebResult(name = "GetSystemRetentionPolicyResponse", targetNamespace = "urn:zimbraMail", partName = "parameters")
    public testGetSystemRetentionPolicyResponse getSystemRetentionPolicyRequest(
        @WebParam(name = "GetSystemRetentionPolicyRequest", targetNamespace = "urn:zimbraMail", partName = "parameters")
        testGetSystemRetentionPolicyRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.mail.testGetTagResponse
     */
    @WebMethod(action = "urn:zimbraMail/GetTag")
    @WebResult(name = "GetTagResponse", targetNamespace = "urn:zimbraMail", partName = "parameters")
    public testGetTagResponse getTagRequest(
        @WebParam(name = "GetTagRequest", targetNamespace = "urn:zimbraMail", partName = "parameters")
        testGetTagRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.mail.testGetTaskResponse
     */
    @WebMethod(action = "urn:zimbraMail/GetTask")
    @WebResult(name = "GetTaskResponse", targetNamespace = "urn:zimbraMail", partName = "parameters")
    public testGetTaskResponse getTaskRequest(
        @WebParam(name = "GetTaskRequest", targetNamespace = "urn:zimbraMail", partName = "parameters")
        testGetTaskRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.mail.testGetTaskSummariesResponse
     */
    @WebMethod(action = "urn:zimbraMail/GetTaskSummaries")
    @WebResult(name = "GetTaskSummariesResponse", targetNamespace = "urn:zimbraMail", partName = "parameters")
    public testGetTaskSummariesResponse getTaskSummariesRequest(
        @WebParam(name = "GetTaskSummariesRequest", targetNamespace = "urn:zimbraMail", partName = "parameters")
        testGetTaskSummariesRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.mail.testGetWatchersResponse
     */
    @WebMethod(action = "urn:zimbraMail/GetWatchers")
    @WebResult(name = "GetWatchersResponse", targetNamespace = "urn:zimbraMail", partName = "parameters")
    public testGetWatchersResponse getWatchersRequest(
        @WebParam(name = "GetWatchersRequest", targetNamespace = "urn:zimbraMail", partName = "parameters")
        testGetWatchersRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.mail.testGetWatchingItemsResponse
     */
    @WebMethod(action = "urn:zimbraMail/GetWatchingItems")
    @WebResult(name = "GetWatchingItemsResponse", targetNamespace = "urn:zimbraMail", partName = "parameters")
    public testGetWatchingItemsResponse getWatchingItemsRequest(
        @WebParam(name = "GetWatchingItemsRequest", targetNamespace = "urn:zimbraMail", partName = "parameters")
        testGetWatchingItemsRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.mail.testGetWorkingHoursResponse
     */
    @WebMethod(action = "urn:zimbraMail/GetWorkingHours")
    @WebResult(name = "GetWorkingHoursResponse", targetNamespace = "urn:zimbraMail", partName = "parameters")
    public testGetWorkingHoursResponse getWorkingHoursRequest(
        @WebParam(name = "GetWorkingHoursRequest", targetNamespace = "urn:zimbraMail", partName = "parameters")
        testGetWorkingHoursRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.mail.testGetYahooAuthTokenResponse
     */
    @WebMethod(action = "urn:zimbraMail/GetYahooAuthToken")
    @WebResult(name = "GetYahooAuthTokenResponse", targetNamespace = "urn:zimbraMail", partName = "parameters")
    public testGetYahooAuthTokenResponse getYahooAuthTokenRequest(
        @WebParam(name = "GetYahooAuthTokenRequest", targetNamespace = "urn:zimbraMail", partName = "parameters")
        testGetYahooAuthTokenRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.mail.testGetYahooCookieResponse
     */
    @WebMethod(action = "urn:zimbraMail/GetYahooCookie")
    @WebResult(name = "GetYahooCookieResponse", targetNamespace = "urn:zimbraMail", partName = "parameters")
    public testGetYahooCookieResponse getYahooCookieRequest(
        @WebParam(name = "GetYahooCookieRequest", targetNamespace = "urn:zimbraMail", partName = "parameters")
        testGetYahooCookieRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.mail.testGlobalSearchResponse
     */
    @WebMethod(action = "urn:zimbraMail/GlobalSearch")
    @WebResult(name = "GlobalSearchResponse", targetNamespace = "urn:zimbraMail", partName = "parameters")
    public testGlobalSearchResponse globalSearchRequest(
        @WebParam(name = "GlobalSearchRequest", targetNamespace = "urn:zimbraMail", partName = "parameters")
        testGlobalSearchRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.mail.testGrantPermissionResponse
     */
    @WebMethod(action = "urn:zimbraMail/GrantPermission")
    @WebResult(name = "GrantPermissionResponse", targetNamespace = "urn:zimbraMail", partName = "parameters")
    public testGrantPermissionResponse grantPermissionRequest(
        @WebParam(name = "GrantPermissionRequest", targetNamespace = "urn:zimbraMail", partName = "parameters")
        testGrantPermissionRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.mail.testICalReplyResponse
     */
    @WebMethod(action = "urn:zimbraMail/ICalReply")
    @WebResult(name = "ICalReplyResponse", targetNamespace = "urn:zimbraMail", partName = "parameters")
    public testICalReplyResponse iCalReplyRequest(
        @WebParam(name = "ICalReplyRequest", targetNamespace = "urn:zimbraMail", partName = "parameters")
        testICalReplyRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.mail.testImportAppointmentsResponse
     */
    @WebMethod(action = "urn:zimbraMail/ImportAppointments")
    @WebResult(name = "ImportAppointmentsResponse", targetNamespace = "urn:zimbraMail", partName = "parameters")
    public testImportAppointmentsResponse importAppointmentsRequest(
        @WebParam(name = "ImportAppointmentsRequest", targetNamespace = "urn:zimbraMail", partName = "parameters")
        testImportAppointmentsRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.mail.testImportContactsResponse
     */
    @WebMethod(action = "urn:zimbraMail/ImportContacts")
    @WebResult(name = "ImportContactsResponse", targetNamespace = "urn:zimbraMail", partName = "parameters")
    public testImportContactsResponse importContactsRequest(
        @WebParam(name = "ImportContactsRequest", targetNamespace = "urn:zimbraMail", partName = "parameters")
        testImportContactsRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.mail.testImportDataResponse
     */
    @WebMethod(action = "urn:zimbraMail/ImportData")
    @WebResult(name = "ImportDataResponse", targetNamespace = "urn:zimbraMail", partName = "parameters")
    public testImportDataResponse importDataRequest(
        @WebParam(name = "ImportDataRequest", targetNamespace = "urn:zimbraMail", partName = "parameters")
        testImportDataRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.mail.testInvalidateReminderDeviceResponse
     */
    @WebMethod(action = "urn:zimbraMail/InvalidateReminderDevice")
    @WebResult(name = "InvalidateReminderDeviceResponse", targetNamespace = "urn:zimbraMail", partName = "parameters")
    public testInvalidateReminderDeviceResponse invalidateReminderDeviceRequest(
        @WebParam(name = "InvalidateReminderDeviceRequest", targetNamespace = "urn:zimbraMail", partName = "parameters")
        testInvalidateReminderDeviceRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.mail.testItemActionResponse
     */
    @WebMethod(action = "urn:zimbraMail/ItemAction")
    @WebResult(name = "ItemActionResponse", targetNamespace = "urn:zimbraMail", partName = "parameters")
    public testItemActionResponse itemActionRequest(
        @WebParam(name = "ItemActionRequest", targetNamespace = "urn:zimbraMail", partName = "parameters")
        testItemActionRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.mail.testListDocumentRevisionsResponse
     */
    @WebMethod(action = "urn:zimbraMail/ListDocumentRevisions")
    @WebResult(name = "ListDocumentRevisionsResponse", targetNamespace = "urn:zimbraMail", partName = "parameters")
    public testListDocumentRevisionsResponse listDocumentRevisionsRequest(
        @WebParam(name = "ListDocumentRevisionsRequest", targetNamespace = "urn:zimbraMail", partName = "parameters")
        testListDocumentRevisionsRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.mail.testModifyAppointmentResponse
     */
    @WebMethod(action = "urn:zimbraMail/ModifyAppointment")
    @WebResult(name = "ModifyAppointmentResponse", targetNamespace = "urn:zimbraMail", partName = "parameters")
    public testModifyAppointmentResponse modifyAppointmentRequest(
        @WebParam(name = "ModifyAppointmentRequest", targetNamespace = "urn:zimbraMail", partName = "parameters")
        testModifyAppointmentRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.mail.testModifyContactResponse
     */
    @WebMethod(action = "urn:zimbraMail/ModifyContact")
    @WebResult(name = "ModifyContactResponse", targetNamespace = "urn:zimbraMail", partName = "parameters")
    public testModifyContactResponse modifyContactRequest(
        @WebParam(name = "ModifyContactRequest", targetNamespace = "urn:zimbraMail", partName = "parameters")
        testModifyContactRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.mail.testModifyDataSourceResponse
     */
    @WebMethod(action = "urn:zimbraMail/ModifyDataSource")
    @WebResult(name = "ModifyDataSourceResponse", targetNamespace = "urn:zimbraMail", partName = "parameters")
    public testModifyDataSourceResponse modifyDataSourceRequest(
        @WebParam(name = "ModifyDataSourceRequest", targetNamespace = "urn:zimbraMail", partName = "parameters")
        testModifyDataSourceRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.mail.testModifyFilterRulesResponse
     */
    @WebMethod(action = "urn:zimbraMail/ModifyFilterRules")
    @WebResult(name = "ModifyFilterRulesResponse", targetNamespace = "urn:zimbraMail", partName = "parameters")
    public testModifyFilterRulesResponse modifyFilterRulesRequest(
        @WebParam(name = "ModifyFilterRulesRequest", targetNamespace = "urn:zimbraMail", partName = "parameters")
        testModifyFilterRulesRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.mail.testModifyMailboxMetadataResponse
     */
    @WebMethod(action = "urn:zimbraMail/ModifyMailboxMetadata")
    @WebResult(name = "ModifyMailboxMetadataResponse", targetNamespace = "urn:zimbraMail", partName = "parameters")
    public testModifyMailboxMetadataResponse modifyMailboxMetadataRequest(
        @WebParam(name = "ModifyMailboxMetadataRequest", targetNamespace = "urn:zimbraMail", partName = "parameters")
        testModifyMailboxMetadataRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.mail.testModifyOutgoingFilterRulesResponse
     */
    @WebMethod(action = "urn:zimbraMail/ModifyOutgoingFilterRules")
    @WebResult(name = "ModifyOutgoingFilterRulesResponse", targetNamespace = "urn:zimbraMail", partName = "parameters")
    public testModifyOutgoingFilterRulesResponse modifyOutgoingFilterRulesRequest(
        @WebParam(name = "ModifyOutgoingFilterRulesRequest", targetNamespace = "urn:zimbraMail", partName = "parameters")
        testModifyOutgoingFilterRulesRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.mail.testModifySearchFolderResponse
     */
    @WebMethod(action = "urn:zimbraMail/ModifySearchFolder")
    @WebResult(name = "ModifySearchFolderResponse", targetNamespace = "urn:zimbraMail", partName = "parameters")
    public testModifySearchFolderResponse modifySearchFolderRequest(
        @WebParam(name = "ModifySearchFolderRequest", targetNamespace = "urn:zimbraMail", partName = "parameters")
        testModifySearchFolderRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.mail.testModifyTaskResponse
     */
    @WebMethod(action = "urn:zimbraMail/ModifyTask")
    @WebResult(name = "ModifyTaskResponse", targetNamespace = "urn:zimbraMail", partName = "parameters")
    public testModifyTaskResponse modifyTaskRequest(
        @WebParam(name = "ModifyTaskRequest", targetNamespace = "urn:zimbraMail", partName = "parameters")
        testModifyTaskRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.mail.testMsgActionResponse
     */
    @WebMethod(action = "urn:zimbraMail/MsgAction")
    @WebResult(name = "MsgActionResponse", targetNamespace = "urn:zimbraMail", partName = "parameters")
    public testMsgActionResponse msgActionRequest(
        @WebParam(name = "MsgActionRequest", targetNamespace = "urn:zimbraMail", partName = "parameters")
        testMsgActionRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.mail.testNoOpResponse
     */
    @WebMethod(action = "urn:zimbraMail/NoOp")
    @WebResult(name = "NoOpResponse", targetNamespace = "urn:zimbraMail", partName = "parameters")
    public testNoOpResponse noOpRequest(
        @WebParam(name = "NoOpRequest", targetNamespace = "urn:zimbraMail", partName = "parameters")
        testNoOpRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.mail.testNoteActionResponse
     */
    @WebMethod(action = "urn:zimbraMail/NoteAction")
    @WebResult(name = "NoteActionResponse", targetNamespace = "urn:zimbraMail", partName = "parameters")
    public testNoteActionResponse noteActionRequest(
        @WebParam(name = "NoteActionRequest", targetNamespace = "urn:zimbraMail", partName = "parameters")
        testNoteActionRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.mail.testPurgeRevisionResponse
     */
    @WebMethod(action = "urn:zimbraMail/PurgeRevision")
    @WebResult(name = "PurgeRevisionResponse", targetNamespace = "urn:zimbraMail", partName = "parameters")
    public testPurgeRevisionResponse purgeRevisionRequest(
        @WebParam(name = "PurgeRevisionRequest", targetNamespace = "urn:zimbraMail", partName = "parameters")
        testPurgeRevisionRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.mail.testRankingActionResponse
     */
    @WebMethod(action = "urn:zimbraMail/RankingAction")
    @WebResult(name = "RankingActionResponse", targetNamespace = "urn:zimbraMail", partName = "parameters")
    public testRankingActionResponse rankingActionRequest(
        @WebParam(name = "RankingActionRequest", targetNamespace = "urn:zimbraMail", partName = "parameters")
        testRankingActionRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.mail.testRegisterDeviceResponse
     */
    @WebMethod(action = "urn:zimbraMail/RegisterDevice")
    @WebResult(name = "RegisterDeviceResponse", targetNamespace = "urn:zimbraMail", partName = "parameters")
    public testRegisterDeviceResponse registerDeviceRequest(
        @WebParam(name = "RegisterDeviceRequest", targetNamespace = "urn:zimbraMail", partName = "parameters")
        testRegisterDeviceRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.mail.testRemoveAttachmentsResponse
     */
    @WebMethod(action = "urn:zimbraMail/RemoveAttachments")
    @WebResult(name = "RemoveAttachmentsResponse", targetNamespace = "urn:zimbraMail", partName = "parameters")
    public testRemoveAttachmentsResponse removeAttachmentsRequest(
        @WebParam(name = "RemoveAttachmentsRequest", targetNamespace = "urn:zimbraMail", partName = "parameters")
        testRemoveAttachmentsRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.mail.testRevokePermissionResponse
     */
    @WebMethod(action = "urn:zimbraMail/RevokePermission")
    @WebResult(name = "RevokePermissionResponse", targetNamespace = "urn:zimbraMail", partName = "parameters")
    public testRevokePermissionResponse revokePermissionRequest(
        @WebParam(name = "RevokePermissionRequest", targetNamespace = "urn:zimbraMail", partName = "parameters")
        testRevokePermissionRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.mail.testSaveDocumentResponse
     */
    @WebMethod(action = "urn:zimbraMail/SaveDocument")
    @WebResult(name = "SaveDocumentResponse", targetNamespace = "urn:zimbraMail", partName = "parameters")
    public testSaveDocumentResponse saveDocumentRequest(
        @WebParam(name = "SaveDocumentRequest", targetNamespace = "urn:zimbraMail", partName = "parameters")
        testSaveDocumentRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.mail.testSaveDraftResponse
     */
    @WebMethod(action = "urn:zimbraMail/SaveDraft")
    @WebResult(name = "SaveDraftResponse", targetNamespace = "urn:zimbraMail", partName = "parameters")
    public testSaveDraftResponse saveDraftRequest(
        @WebParam(name = "SaveDraftRequest", targetNamespace = "urn:zimbraMail", partName = "parameters")
        testSaveDraftRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.mail.testSearchConvResponse
     */
    @WebMethod(action = "urn:zimbraMail/SearchConv")
    @WebResult(name = "SearchConvResponse", targetNamespace = "urn:zimbraMail", partName = "parameters")
    public testSearchConvResponse searchConvRequest(
        @WebParam(name = "SearchConvRequest", targetNamespace = "urn:zimbraMail", partName = "parameters")
        testSearchConvRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.mail.testSearchResponse
     */
    @WebMethod(action = "urn:zimbraMail/Search")
    @WebResult(name = "SearchResponse", targetNamespace = "urn:zimbraMail", partName = "parameters")
    public testSearchResponse searchRequest(
        @WebParam(name = "SearchRequest", targetNamespace = "urn:zimbraMail", partName = "parameters")
        testSearchRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.mail.testSendDeliveryReportResponse
     */
    @WebMethod(action = "urn:zimbraMail/SendDeliveryReport")
    @WebResult(name = "SendDeliveryReportResponse", targetNamespace = "urn:zimbraMail", partName = "parameters")
    public testSendDeliveryReportResponse sendDeliveryReportRequest(
        @WebParam(name = "SendDeliveryReportRequest", targetNamespace = "urn:zimbraMail", partName = "parameters")
        testSendDeliveryReportRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.mail.testSendInviteReplyResponse
     */
    @WebMethod(action = "urn:zimbraMail/SendInviteReply")
    @WebResult(name = "SendInviteReplyResponse", targetNamespace = "urn:zimbraMail", partName = "parameters")
    public testSendInviteReplyResponse sendInviteReplyRequest(
        @WebParam(name = "SendInviteReplyRequest", targetNamespace = "urn:zimbraMail", partName = "parameters")
        testSendInviteReplyRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.mail.testSendMsgResponse
     */
    @WebMethod(action = "urn:zimbraMail/SendMsg")
    @WebResult(name = "SendMsgResponse", targetNamespace = "urn:zimbraMail", partName = "parameters")
    public testSendMsgResponse sendMsgRequest(
        @WebParam(name = "SendMsgRequest", targetNamespace = "urn:zimbraMail", partName = "parameters")
        testSendMsgRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.mail.testSendShareNotificationResponse
     */
    @WebMethod(action = "urn:zimbraMail/SendShareNotification")
    @WebResult(name = "SendShareNotificationResponse", targetNamespace = "urn:zimbraMail", partName = "parameters")
    public testSendShareNotificationResponse sendShareNotificationRequest(
        @WebParam(name = "SendShareNotificationRequest", targetNamespace = "urn:zimbraMail", partName = "parameters")
        testSendShareNotificationRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.mail.testSendVerificationCodeResponse
     */
    @WebMethod(action = "urn:zimbraMail/SendVerificationCode")
    @WebResult(name = "SendVerificationCodeResponse", targetNamespace = "urn:zimbraMail", partName = "parameters")
    public testSendVerificationCodeResponse sendVerificationCodeRequest(
        @WebParam(name = "SendVerificationCodeRequest", targetNamespace = "urn:zimbraMail", partName = "parameters")
        testSendVerificationCodeRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.mail.testSetAppointmentResponse
     */
    @WebMethod(action = "urn:zimbraMail/SetAppointment")
    @WebResult(name = "SetAppointmentResponse", targetNamespace = "urn:zimbraMail", partName = "parameters")
    public testSetAppointmentResponse setAppointmentRequest(
        @WebParam(name = "SetAppointmentRequest", targetNamespace = "urn:zimbraMail", partName = "parameters")
        testSetAppointmentRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.mail.testSetCustomMetadataResponse
     */
    @WebMethod(action = "urn:zimbraMail/SetCustomMetadata")
    @WebResult(name = "SetCustomMetadataResponse", targetNamespace = "urn:zimbraMail", partName = "parameters")
    public testSetCustomMetadataResponse setCustomMetadataRequest(
        @WebParam(name = "SetCustomMetadataRequest", targetNamespace = "urn:zimbraMail", partName = "parameters")
        testSetCustomMetadataRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.mail.testSetMailboxMetadataResponse
     */
    @WebMethod(action = "urn:zimbraMail/SetMailboxMetadata")
    @WebResult(name = "SetMailboxMetadataResponse", targetNamespace = "urn:zimbraMail", partName = "parameters")
    public testSetMailboxMetadataResponse setMailboxMetadataRequest(
        @WebParam(name = "SetMailboxMetadataRequest", targetNamespace = "urn:zimbraMail", partName = "parameters")
        testSetMailboxMetadataRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.mail.testSetTaskResponse
     */
    @WebMethod(action = "urn:zimbraMail/SetTask")
    @WebResult(name = "SetTaskResponse", targetNamespace = "urn:zimbraMail", partName = "parameters")
    public testSetTaskResponse setTaskRequest(
        @WebParam(name = "SetTaskRequest", targetNamespace = "urn:zimbraMail", partName = "parameters")
        testSetTaskRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.mail.testSnoozeCalendarItemAlarmResponse
     */
    @WebMethod(action = "urn:zimbraMail/SnoozeCalendarItemAlarm")
    @WebResult(name = "SnoozeCalendarItemAlarmResponse", targetNamespace = "urn:zimbraMail", partName = "parameters")
    public testSnoozeCalendarItemAlarmResponse snoozeCalendarItemAlarmRequest(
        @WebParam(name = "SnoozeCalendarItemAlarmRequest", targetNamespace = "urn:zimbraMail", partName = "parameters")
        testSnoozeCalendarItemAlarmRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.mail.testSyncResponse
     */
    @WebMethod(action = "urn:zimbraMail/Sync")
    @WebResult(name = "SyncResponse", targetNamespace = "urn:zimbraMail", partName = "parameters")
    public testSyncResponse syncRequest(
        @WebParam(name = "SyncRequest", targetNamespace = "urn:zimbraMail", partName = "parameters")
        testSyncRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.mail.testTagActionResponse
     */
    @WebMethod(action = "urn:zimbraMail/TagAction")
    @WebResult(name = "TagActionResponse", targetNamespace = "urn:zimbraMail", partName = "parameters")
    public testTagActionResponse tagActionRequest(
        @WebParam(name = "TagActionRequest", targetNamespace = "urn:zimbraMail", partName = "parameters")
        testTagActionRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.mail.testTestDataSourceResponse
     */
    @WebMethod(action = "urn:zimbraMail/TestDataSource")
    @WebResult(name = "TestDataSourceResponse", targetNamespace = "urn:zimbraMail", partName = "parameters")
    public testTestDataSourceResponse testDataSourceRequest(
        @WebParam(name = "TestDataSourceRequest", targetNamespace = "urn:zimbraMail", partName = "parameters")
        testTestDataSourceRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.mail.testUpdateDeviceStatusResponse
     */
    @WebMethod(action = "urn:zimbraMail/UpdateDeviceStatus")
    @WebResult(name = "UpdateDeviceStatusResponse", targetNamespace = "urn:zimbraMail", partName = "parameters")
    public testUpdateDeviceStatusResponse updateDeviceStatusRequest(
        @WebParam(name = "UpdateDeviceStatusRequest", targetNamespace = "urn:zimbraMail", partName = "parameters")
        testUpdateDeviceStatusRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.mail.testVerifyCodeResponse
     */
    @WebMethod(action = "urn:zimbraMail/VerifyCode")
    @WebResult(name = "VerifyCodeResponse", targetNamespace = "urn:zimbraMail", partName = "parameters")
    public testVerifyCodeResponse verifyCodeRequest(
        @WebParam(name = "VerifyCodeRequest", targetNamespace = "urn:zimbraMail", partName = "parameters")
        testVerifyCodeRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.mail.testWaitSetResponse
     */
    @WebMethod(action = "urn:zimbraMail/WaitSet")
    @WebResult(name = "WaitSetResponse", targetNamespace = "urn:zimbraMail", partName = "parameters")
    public testWaitSetResponse waitSetRequest(
        @WebParam(name = "WaitSetRequest", targetNamespace = "urn:zimbraMail", partName = "parameters")
        testWaitSetRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.mail.testWikiActionResponse
     */
    @WebMethod(action = "urn:zimbraMail/WikiAction")
    @WebResult(name = "WikiActionResponse", targetNamespace = "urn:zimbraMail", partName = "parameters")
    public testWikiActionResponse wikiActionRequest(
        @WebParam(name = "WikiActionRequest", targetNamespace = "urn:zimbraMail", partName = "parameters")
        testWikiActionRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.replication.testBecomeMasterResponse
     */
    @WebMethod(action = "urn:zimbraRepl/BecomeMaster")
    @WebResult(name = "BecomeMasterResponse", targetNamespace = "urn:zimbraRepl", partName = "parameters")
    public testBecomeMasterResponse becomeMasterRequest(
        @WebParam(name = "BecomeMasterRequest", targetNamespace = "urn:zimbraRepl", partName = "parameters")
        testBecomeMasterRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.replication.testBringDownServiceIPResponse
     */
    @WebMethod(action = "urn:zimbraRepl/BringDownServiceIP")
    @WebResult(name = "BringDownServiceIPResponse", targetNamespace = "urn:zimbraRepl", partName = "parameters")
    public testBringDownServiceIPResponse bringDownServiceIPRequest(
        @WebParam(name = "BringDownServiceIPRequest", targetNamespace = "urn:zimbraRepl", partName = "parameters")
        testBringDownServiceIPRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.replication.testBringUpServiceIPResponse
     */
    @WebMethod(action = "urn:zimbraRepl/BringUpServiceIP")
    @WebResult(name = "BringUpServiceIPResponse", targetNamespace = "urn:zimbraRepl", partName = "parameters")
    public testBringUpServiceIPResponse bringUpServiceIPRequest(
        @WebParam(name = "BringUpServiceIPRequest", targetNamespace = "urn:zimbraRepl", partName = "parameters")
        testBringUpServiceIPRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.replication.testReplicationStatusResponse
     */
    @WebMethod(action = "urn:zimbraRepl/ReplicationStatus")
    @WebResult(name = "ReplicationStatusResponse", targetNamespace = "urn:zimbraRepl", partName = "parameters")
    public testReplicationStatusResponse replicationStatusRequest(
        @WebParam(name = "ReplicationStatusRequest", targetNamespace = "urn:zimbraRepl", partName = "parameters")
        testReplicationStatusRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.replication.testStartCatchupResponse
     */
    @WebMethod(action = "urn:zimbraRepl/StartCatchup")
    @WebResult(name = "StartCatchupResponse", targetNamespace = "urn:zimbraRepl", partName = "parameters")
    public testStartCatchupResponse startCatchupRequest(
        @WebParam(name = "StartCatchupRequest", targetNamespace = "urn:zimbraRepl", partName = "parameters")
        testStartCatchupRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.replication.testStartFailoverClientResponse
     */
    @WebMethod(action = "urn:zimbraRepl/StartFailoverClient")
    @WebResult(name = "StartFailoverClientResponse", targetNamespace = "urn:zimbraRepl", partName = "parameters")
    public testStartFailoverClientResponse startFailoverClientRequest(
        @WebParam(name = "StartFailoverClientRequest", targetNamespace = "urn:zimbraRepl", partName = "parameters")
        testStartFailoverClientRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.replication.testStartFailoverDaemonResponse
     */
    @WebMethod(action = "urn:zimbraRepl/StartFailoverDaemon")
    @WebResult(name = "StartFailoverDaemonResponse", targetNamespace = "urn:zimbraRepl", partName = "parameters")
    public testStartFailoverDaemonResponse startFailoverDaemonRequest(
        @WebParam(name = "StartFailoverDaemonRequest", targetNamespace = "urn:zimbraRepl", partName = "parameters")
        testStartFailoverDaemonRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.replication.testStopFailoverClientResponse
     */
    @WebMethod(action = "urn:zimbraRepl/StopFailoverClient")
    @WebResult(name = "StopFailoverClientResponse", targetNamespace = "urn:zimbraRepl", partName = "parameters")
    public testStopFailoverClientResponse stopFailoverClientRequest(
        @WebParam(name = "StopFailoverClientRequest", targetNamespace = "urn:zimbraRepl", partName = "parameters")
        testStopFailoverClientRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.replication.testStopFailoverDaemonResponse
     */
    @WebMethod(action = "urn:zimbraRepl/StopFailoverDaemon")
    @WebResult(name = "StopFailoverDaemonResponse", targetNamespace = "urn:zimbraRepl", partName = "parameters")
    public testStopFailoverDaemonResponse stopFailoverDaemonRequest(
        @WebParam(name = "StopFailoverDaemonRequest", targetNamespace = "urn:zimbraRepl", partName = "parameters")
        testStopFailoverDaemonRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.sync.testCancelPendingRemoteWipeResponse
     */
    @WebMethod(action = "urn:zimbraSync/CancelPendingRemoteWipe")
    @WebResult(name = "CancelPendingRemoteWipeResponse", targetNamespace = "urn:zimbraSync", partName = "parameters")
    public testCancelPendingRemoteWipeResponse cancelPendingRemoteWipeRequest(
        @WebParam(name = "CancelPendingRemoteWipeRequest", targetNamespace = "urn:zimbraSync", partName = "parameters")
        testCancelPendingRemoteWipeRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.sync.testGetDeviceStatusResponse
     */
    @WebMethod(action = "urn:zimbraSync/GetDeviceStatus")
    @WebResult(name = "GetDeviceStatusResponse", targetNamespace = "urn:zimbraSync", partName = "parameters")
    public testGetDeviceStatusResponse getDeviceStatusRequest(
        @WebParam(name = "GetDeviceStatusRequest", targetNamespace = "urn:zimbraSync", partName = "parameters")
        testGetDeviceStatusRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.sync.testRemoteWipeResponse
     */
    @WebMethod(action = "urn:zimbraSync/RemoteWipe")
    @WebResult(name = "RemoteWipeResponse", targetNamespace = "urn:zimbraSync", partName = "parameters")
    public testRemoteWipeResponse remoteWipeRequest(
        @WebParam(name = "RemoteWipeRequest", targetNamespace = "urn:zimbraSync", partName = "parameters")
        testRemoteWipeRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.sync.testRemoveDeviceResponse
     */
    @WebMethod(action = "urn:zimbraSync/RemoveDevice")
    @WebResult(name = "RemoveDeviceResponse", targetNamespace = "urn:zimbraSync", partName = "parameters")
    public testRemoveDeviceResponse removeDeviceRequest(
        @WebParam(name = "RemoveDeviceRequest", targetNamespace = "urn:zimbraSync", partName = "parameters")
        testRemoveDeviceRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.sync.testResumeDeviceResponse
     */
    @WebMethod(action = "urn:zimbraSync/ResumeDevice")
    @WebResult(name = "ResumeDeviceResponse", targetNamespace = "urn:zimbraSync", partName = "parameters")
    public testResumeDeviceResponse resumeDeviceRequest(
        @WebParam(name = "ResumeDeviceRequest", targetNamespace = "urn:zimbraSync", partName = "parameters")
        testResumeDeviceRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.sync.testSuspendDeviceResponse
     */
    @WebMethod(action = "urn:zimbraSync/SuspendDevice")
    @WebResult(name = "SuspendDeviceResponse", targetNamespace = "urn:zimbraSync", partName = "parameters")
    public testSuspendDeviceResponse suspendDeviceRequest(
        @WebParam(name = "SuspendDeviceRequest", targetNamespace = "urn:zimbraSync", partName = "parameters")
        testSuspendDeviceRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.appblast.testEditDocumentResponse
     */
    @WebMethod(action = "urn:zimbraAppblast/EditDocument")
    @WebResult(name = "EditDocumentResponse", targetNamespace = "urn:zimbraAppblast", partName = "parameters")
    public testEditDocumentResponse editDocumentRequest(
        @WebParam(name = "EditDocumentRequest", targetNamespace = "urn:zimbraAppblast", partName = "parameters")
        testEditDocumentRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.appblast.testFinishEditDocumentResponse
     */
    @WebMethod(action = "urn:zimbraAppblast/FinishEditDocument")
    @WebResult(name = "FinishEditDocumentResponse", targetNamespace = "urn:zimbraAppblast", partName = "parameters")
    public testFinishEditDocumentResponse finishEditDocumentRequest(
        @WebParam(name = "FinishEditDocumentRequest", targetNamespace = "urn:zimbraAppblast", partName = "parameters")
        testFinishEditDocumentRequest parameters);

}
