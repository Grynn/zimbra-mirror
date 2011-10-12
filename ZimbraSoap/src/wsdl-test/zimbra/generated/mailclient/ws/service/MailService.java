
package zimbra.generated.mailclient.ws.service;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.bind.annotation.XmlSeeAlso;
import zimbra.generated.mailclient.mail.testAddAppointmentInviteRequest;
import zimbra.generated.mailclient.mail.testAddAppointmentInviteResponse;
import zimbra.generated.mailclient.mail.testAddCommentRequest;
import zimbra.generated.mailclient.mail.testAddCommentResponse;
import zimbra.generated.mailclient.mail.testAddMsgRequest;
import zimbra.generated.mailclient.mail.testAddMsgResponse;
import zimbra.generated.mailclient.mail.testAddTaskInviteRequest;
import zimbra.generated.mailclient.mail.testAddTaskInviteResponse;
import zimbra.generated.mailclient.mail.testAnnounceOrganizerChangeRequest;
import zimbra.generated.mailclient.mail.testAnnounceOrganizerChangeResponse;
import zimbra.generated.mailclient.mail.testApplyFilterRulesRequest;
import zimbra.generated.mailclient.mail.testApplyFilterRulesResponse;
import zimbra.generated.mailclient.mail.testApplyOutgoingFilterRulesRequest;
import zimbra.generated.mailclient.mail.testApplyOutgoingFilterRulesResponse;
import zimbra.generated.mailclient.mail.testAutoCompleteRequest;
import zimbra.generated.mailclient.mail.testAutoCompleteResponse;
import zimbra.generated.mailclient.mail.testBounceMsgRequest;
import zimbra.generated.mailclient.mail.testBounceMsgResponse;
import zimbra.generated.mailclient.mail.testBrowseRequest;
import zimbra.generated.mailclient.mail.testBrowseResponse;
import zimbra.generated.mailclient.mail.testCancelAppointmentRequest;
import zimbra.generated.mailclient.mail.testCancelAppointmentResponse;
import zimbra.generated.mailclient.mail.testCancelTaskRequest;
import zimbra.generated.mailclient.mail.testCancelTaskResponse;
import zimbra.generated.mailclient.mail.testCheckDeviceStatusRequest;
import zimbra.generated.mailclient.mail.testCheckDeviceStatusResponse;
import zimbra.generated.mailclient.mail.testCheckPermissionRequest;
import zimbra.generated.mailclient.mail.testCheckPermissionResponse;
import zimbra.generated.mailclient.mail.testCheckRecurConflictsRequest;
import zimbra.generated.mailclient.mail.testCheckRecurConflictsResponse;
import zimbra.generated.mailclient.mail.testCheckSpellingRequest;
import zimbra.generated.mailclient.mail.testCheckSpellingResponse;
import zimbra.generated.mailclient.mail.testCompleteTaskInstanceRequest;
import zimbra.generated.mailclient.mail.testCompleteTaskInstanceResponse;
import zimbra.generated.mailclient.mail.testContactActionRequest;
import zimbra.generated.mailclient.mail.testContactActionResponse;
import zimbra.generated.mailclient.mail.testConvActionRequest;
import zimbra.generated.mailclient.mail.testConvActionResponse;
import zimbra.generated.mailclient.mail.testCounterAppointmentRequest;
import zimbra.generated.mailclient.mail.testCounterAppointmentResponse;
import zimbra.generated.mailclient.mail.testCreateAppointmentExceptionRequest;
import zimbra.generated.mailclient.mail.testCreateAppointmentExceptionResponse;
import zimbra.generated.mailclient.mail.testCreateAppointmentRequest;
import zimbra.generated.mailclient.mail.testCreateAppointmentResponse;
import zimbra.generated.mailclient.mail.testCreateContactRequest;
import zimbra.generated.mailclient.mail.testCreateContactResponse;
import zimbra.generated.mailclient.mail.testCreateDataSourceRequest;
import zimbra.generated.mailclient.mail.testCreateDataSourceResponse;
import zimbra.generated.mailclient.mail.testCreateFolderRequest;
import zimbra.generated.mailclient.mail.testCreateFolderResponse;
import zimbra.generated.mailclient.mail.testCreateMountpointRequest;
import zimbra.generated.mailclient.mail.testCreateMountpointResponse;
import zimbra.generated.mailclient.mail.testCreateNoteRequest;
import zimbra.generated.mailclient.mail.testCreateNoteResponse;
import zimbra.generated.mailclient.mail.testCreateSearchFolderRequest;
import zimbra.generated.mailclient.mail.testCreateSearchFolderResponse;
import zimbra.generated.mailclient.mail.testCreateTagRequest;
import zimbra.generated.mailclient.mail.testCreateTagResponse;
import zimbra.generated.mailclient.mail.testCreateTaskExceptionRequest;
import zimbra.generated.mailclient.mail.testCreateTaskExceptionResponse;
import zimbra.generated.mailclient.mail.testCreateTaskRequest;
import zimbra.generated.mailclient.mail.testCreateTaskResponse;
import zimbra.generated.mailclient.mail.testCreateWaitSetRequest;
import zimbra.generated.mailclient.mail.testCreateWaitSetResponse;
import zimbra.generated.mailclient.mail.testDeclineCounterAppointmentRequest;
import zimbra.generated.mailclient.mail.testDeclineCounterAppointmentResponse;
import zimbra.generated.mailclient.mail.testDeleteDataSourceRequest;
import zimbra.generated.mailclient.mail.testDeleteDataSourceResponse;
import zimbra.generated.mailclient.mail.testDeleteDeviceRequest;
import zimbra.generated.mailclient.mail.testDeleteDeviceResponse;
import zimbra.generated.mailclient.mail.testDestroyWaitSetRequest;
import zimbra.generated.mailclient.mail.testDestroyWaitSetResponse;
import zimbra.generated.mailclient.mail.testDiffDocumentRequest;
import zimbra.generated.mailclient.mail.testDiffDocumentResponse;
import zimbra.generated.mailclient.mail.testDismissCalendarItemAlarmRequest;
import zimbra.generated.mailclient.mail.testDismissCalendarItemAlarmResponse;
import zimbra.generated.mailclient.mail.testDocumentActionRequest;
import zimbra.generated.mailclient.mail.testDocumentActionResponse;
import zimbra.generated.mailclient.mail.testEmptyDumpsterRequest;
import zimbra.generated.mailclient.mail.testEmptyDumpsterResponse;
import zimbra.generated.mailclient.mail.testEnableSharedReminderRequest;
import zimbra.generated.mailclient.mail.testEnableSharedReminderResponse;
import zimbra.generated.mailclient.mail.testExpandRecurRequest;
import zimbra.generated.mailclient.mail.testExpandRecurResponse;
import zimbra.generated.mailclient.mail.testExportContactsRequest;
import zimbra.generated.mailclient.mail.testExportContactsResponse;
import zimbra.generated.mailclient.mail.testFolderActionRequest;
import zimbra.generated.mailclient.mail.testFolderActionResponse;
import zimbra.generated.mailclient.mail.testForwardAppointmentInviteRequest;
import zimbra.generated.mailclient.mail.testForwardAppointmentInviteResponse;
import zimbra.generated.mailclient.mail.testForwardAppointmentRequest;
import zimbra.generated.mailclient.mail.testForwardAppointmentResponse;
import zimbra.generated.mailclient.mail.testGenerateUUIDRequest;
import zimbra.generated.mailclient.mail.testGetActivityStreamRequest;
import zimbra.generated.mailclient.mail.testGetActivityStreamResponse;
import zimbra.generated.mailclient.mail.testGetAllDevicesRequest;
import zimbra.generated.mailclient.mail.testGetAllDevicesResponse;
import zimbra.generated.mailclient.mail.testGetAppointmentRequest;
import zimbra.generated.mailclient.mail.testGetAppointmentResponse;
import zimbra.generated.mailclient.mail.testGetApptSummariesRequest;
import zimbra.generated.mailclient.mail.testGetApptSummariesResponse;
import zimbra.generated.mailclient.mail.testGetCalendarItemSummariesRequest;
import zimbra.generated.mailclient.mail.testGetCalendarItemSummariesResponse;
import zimbra.generated.mailclient.mail.testGetCommentsRequest;
import zimbra.generated.mailclient.mail.testGetCommentsResponse;
import zimbra.generated.mailclient.mail.testGetContactsRequest;
import zimbra.generated.mailclient.mail.testGetContactsResponse;
import zimbra.generated.mailclient.mail.testGetConvRequest;
import zimbra.generated.mailclient.mail.testGetConvResponse;
import zimbra.generated.mailclient.mail.testGetCustomMetadataRequest;
import zimbra.generated.mailclient.mail.testGetCustomMetadataResponse;
import zimbra.generated.mailclient.mail.testGetDataSourcesRequest;
import zimbra.generated.mailclient.mail.testGetDataSourcesResponse;
import zimbra.generated.mailclient.mail.testGetEffectiveFolderPermsRequest;
import zimbra.generated.mailclient.mail.testGetEffectiveFolderPermsResponse;
import zimbra.generated.mailclient.mail.testGetFilterRulesRequest;
import zimbra.generated.mailclient.mail.testGetFilterRulesResponse;
import zimbra.generated.mailclient.mail.testGetFolderRequest;
import zimbra.generated.mailclient.mail.testGetFolderResponse;
import zimbra.generated.mailclient.mail.testGetFreeBusyRequest;
import zimbra.generated.mailclient.mail.testGetFreeBusyResponse;
import zimbra.generated.mailclient.mail.testGetICalRequest;
import zimbra.generated.mailclient.mail.testGetICalResponse;
import zimbra.generated.mailclient.mail.testGetImportStatusRequest;
import zimbra.generated.mailclient.mail.testGetImportStatusResponse;
import zimbra.generated.mailclient.mail.testGetItemRequest;
import zimbra.generated.mailclient.mail.testGetItemResponse;
import zimbra.generated.mailclient.mail.testGetMailboxMetadataRequest;
import zimbra.generated.mailclient.mail.testGetMailboxMetadataResponse;
import zimbra.generated.mailclient.mail.testGetMiniCalRequest;
import zimbra.generated.mailclient.mail.testGetMiniCalResponse;
import zimbra.generated.mailclient.mail.testGetMsgMetadataRequest;
import zimbra.generated.mailclient.mail.testGetMsgMetadataResponse;
import zimbra.generated.mailclient.mail.testGetMsgRequest;
import zimbra.generated.mailclient.mail.testGetMsgResponse;
import zimbra.generated.mailclient.mail.testGetNoteRequest;
import zimbra.generated.mailclient.mail.testGetNoteResponse;
import zimbra.generated.mailclient.mail.testGetOutgoingFilterRulesRequest;
import zimbra.generated.mailclient.mail.testGetOutgoingFilterRulesResponse;
import zimbra.generated.mailclient.mail.testGetPermissionRequest;
import zimbra.generated.mailclient.mail.testGetPermissionResponse;
import zimbra.generated.mailclient.mail.testGetRecurRequest;
import zimbra.generated.mailclient.mail.testGetRecurResponse;
import zimbra.generated.mailclient.mail.testGetSearchFolderRequest;
import zimbra.generated.mailclient.mail.testGetSearchFolderResponse;
import zimbra.generated.mailclient.mail.testGetShareNotificationsRequest;
import zimbra.generated.mailclient.mail.testGetShareNotificationsResponse;
import zimbra.generated.mailclient.mail.testGetSpellDictionariesRequest;
import zimbra.generated.mailclient.mail.testGetSpellDictionariesResponse;
import zimbra.generated.mailclient.mail.testGetSystemRetentionPolicyRequest;
import zimbra.generated.mailclient.mail.testGetSystemRetentionPolicyResponse;
import zimbra.generated.mailclient.mail.testGetTagRequest;
import zimbra.generated.mailclient.mail.testGetTagResponse;
import zimbra.generated.mailclient.mail.testGetTaskRequest;
import zimbra.generated.mailclient.mail.testGetTaskResponse;
import zimbra.generated.mailclient.mail.testGetTaskSummariesRequest;
import zimbra.generated.mailclient.mail.testGetTaskSummariesResponse;
import zimbra.generated.mailclient.mail.testGetWatchersRequest;
import zimbra.generated.mailclient.mail.testGetWatchersResponse;
import zimbra.generated.mailclient.mail.testGetWatchingItemsRequest;
import zimbra.generated.mailclient.mail.testGetWatchingItemsResponse;
import zimbra.generated.mailclient.mail.testGetWorkingHoursRequest;
import zimbra.generated.mailclient.mail.testGetWorkingHoursResponse;
import zimbra.generated.mailclient.mail.testGetYahooAuthTokenRequest;
import zimbra.generated.mailclient.mail.testGetYahooAuthTokenResponse;
import zimbra.generated.mailclient.mail.testGetYahooCookieRequest;
import zimbra.generated.mailclient.mail.testGetYahooCookieResponse;
import zimbra.generated.mailclient.mail.testGlobalSearchRequest;
import zimbra.generated.mailclient.mail.testGlobalSearchResponse;
import zimbra.generated.mailclient.mail.testGrantPermissionRequest;
import zimbra.generated.mailclient.mail.testGrantPermissionResponse;
import zimbra.generated.mailclient.mail.testICalReplyRequest;
import zimbra.generated.mailclient.mail.testICalReplyResponse;
import zimbra.generated.mailclient.mail.testImportAppointmentsRequest;
import zimbra.generated.mailclient.mail.testImportAppointmentsResponse;
import zimbra.generated.mailclient.mail.testImportContactsRequest;
import zimbra.generated.mailclient.mail.testImportContactsResponse;
import zimbra.generated.mailclient.mail.testImportDataRequest;
import zimbra.generated.mailclient.mail.testImportDataResponse;
import zimbra.generated.mailclient.mail.testInvalidateReminderDeviceRequest;
import zimbra.generated.mailclient.mail.testInvalidateReminderDeviceResponse;
import zimbra.generated.mailclient.mail.testItemActionRequest;
import zimbra.generated.mailclient.mail.testItemActionResponse;
import zimbra.generated.mailclient.mail.testListDocumentRevisionsRequest;
import zimbra.generated.mailclient.mail.testListDocumentRevisionsResponse;
import zimbra.generated.mailclient.mail.testModifyAppointmentRequest;
import zimbra.generated.mailclient.mail.testModifyAppointmentResponse;
import zimbra.generated.mailclient.mail.testModifyContactRequest;
import zimbra.generated.mailclient.mail.testModifyContactResponse;
import zimbra.generated.mailclient.mail.testModifyDataSourceRequest;
import zimbra.generated.mailclient.mail.testModifyDataSourceResponse;
import zimbra.generated.mailclient.mail.testModifyFilterRulesRequest;
import zimbra.generated.mailclient.mail.testModifyFilterRulesResponse;
import zimbra.generated.mailclient.mail.testModifyMailboxMetadataRequest;
import zimbra.generated.mailclient.mail.testModifyMailboxMetadataResponse;
import zimbra.generated.mailclient.mail.testModifyOutgoingFilterRulesRequest;
import zimbra.generated.mailclient.mail.testModifyOutgoingFilterRulesResponse;
import zimbra.generated.mailclient.mail.testModifySearchFolderRequest;
import zimbra.generated.mailclient.mail.testModifySearchFolderResponse;
import zimbra.generated.mailclient.mail.testModifyTaskRequest;
import zimbra.generated.mailclient.mail.testModifyTaskResponse;
import zimbra.generated.mailclient.mail.testMsgActionRequest;
import zimbra.generated.mailclient.mail.testMsgActionResponse;
import zimbra.generated.mailclient.mail.testNoOpRequest;
import zimbra.generated.mailclient.mail.testNoOpResponse;
import zimbra.generated.mailclient.mail.testNoteActionRequest;
import zimbra.generated.mailclient.mail.testNoteActionResponse;
import zimbra.generated.mailclient.mail.testPurgeRevisionRequest;
import zimbra.generated.mailclient.mail.testPurgeRevisionResponse;
import zimbra.generated.mailclient.mail.testRankingActionRequest;
import zimbra.generated.mailclient.mail.testRankingActionResponse;
import zimbra.generated.mailclient.mail.testRegisterDeviceRequest;
import zimbra.generated.mailclient.mail.testRegisterDeviceResponse;
import zimbra.generated.mailclient.mail.testRemoveAttachmentsRequest;
import zimbra.generated.mailclient.mail.testRemoveAttachmentsResponse;
import zimbra.generated.mailclient.mail.testRevokePermissionRequest;
import zimbra.generated.mailclient.mail.testRevokePermissionResponse;
import zimbra.generated.mailclient.mail.testSaveDocumentRequest;
import zimbra.generated.mailclient.mail.testSaveDocumentResponse;
import zimbra.generated.mailclient.mail.testSaveDraftRequest;
import zimbra.generated.mailclient.mail.testSaveDraftResponse;
import zimbra.generated.mailclient.mail.testSearchConvRequest;
import zimbra.generated.mailclient.mail.testSearchConvResponse;
import zimbra.generated.mailclient.mail.testSearchRequest;
import zimbra.generated.mailclient.mail.testSearchResponse;
import zimbra.generated.mailclient.mail.testSendDeliveryReportRequest;
import zimbra.generated.mailclient.mail.testSendDeliveryReportResponse;
import zimbra.generated.mailclient.mail.testSendInviteReplyRequest;
import zimbra.generated.mailclient.mail.testSendInviteReplyResponse;
import zimbra.generated.mailclient.mail.testSendMsgRequest;
import zimbra.generated.mailclient.mail.testSendMsgResponse;
import zimbra.generated.mailclient.mail.testSendShareNotificationRequest;
import zimbra.generated.mailclient.mail.testSendShareNotificationResponse;
import zimbra.generated.mailclient.mail.testSendVerificationCodeRequest;
import zimbra.generated.mailclient.mail.testSendVerificationCodeResponse;
import zimbra.generated.mailclient.mail.testSetAppointmentRequest;
import zimbra.generated.mailclient.mail.testSetAppointmentResponse;
import zimbra.generated.mailclient.mail.testSetCustomMetadataRequest;
import zimbra.generated.mailclient.mail.testSetCustomMetadataResponse;
import zimbra.generated.mailclient.mail.testSetMailboxMetadataRequest;
import zimbra.generated.mailclient.mail.testSetMailboxMetadataResponse;
import zimbra.generated.mailclient.mail.testSetTaskRequest;
import zimbra.generated.mailclient.mail.testSetTaskResponse;
import zimbra.generated.mailclient.mail.testSnoozeCalendarItemAlarmRequest;
import zimbra.generated.mailclient.mail.testSnoozeCalendarItemAlarmResponse;
import zimbra.generated.mailclient.mail.testSyncRequest;
import zimbra.generated.mailclient.mail.testSyncResponse;
import zimbra.generated.mailclient.mail.testTagActionRequest;
import zimbra.generated.mailclient.mail.testTagActionResponse;
import zimbra.generated.mailclient.mail.testTestDataSourceRequest;
import zimbra.generated.mailclient.mail.testTestDataSourceResponse;
import zimbra.generated.mailclient.mail.testUpdateDeviceStatusRequest;
import zimbra.generated.mailclient.mail.testUpdateDeviceStatusResponse;
import zimbra.generated.mailclient.mail.testVerifyCodeRequest;
import zimbra.generated.mailclient.mail.testVerifyCodeResponse;
import zimbra.generated.mailclient.mail.testWaitSetRequest;
import zimbra.generated.mailclient.mail.testWaitSetResponse;
import zimbra.generated.mailclient.mail.testWikiActionRequest;
import zimbra.generated.mailclient.mail.testWikiActionResponse;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.1.7-hudson-48-
 * Generated source version: 2.1
 * 
 */
@WebService(name = "MailService", targetNamespace = "http://www.zimbra.com/wsdl/MailService.wsdl")
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE)
@XmlSeeAlso({
    zimbra.generated.mailclient.zm.ObjectFactory.class,
    zimbra.generated.mailclient.mail.ObjectFactory.class
})
public interface MailService {


    /**
     * 
     * @param parameters
     * @return
     *     returns zimbra.generated.mailclient.mail.testAddAppointmentInviteResponse
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
     *     returns zimbra.generated.mailclient.mail.testAddCommentResponse
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
     *     returns zimbra.generated.mailclient.mail.testAddMsgResponse
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
     *     returns zimbra.generated.mailclient.mail.testAddTaskInviteResponse
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
     *     returns zimbra.generated.mailclient.mail.testAnnounceOrganizerChangeResponse
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
     *     returns zimbra.generated.mailclient.mail.testApplyFilterRulesResponse
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
     *     returns zimbra.generated.mailclient.mail.testApplyOutgoingFilterRulesResponse
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
     *     returns zimbra.generated.mailclient.mail.testAutoCompleteResponse
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
     *     returns zimbra.generated.mailclient.mail.testBounceMsgResponse
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
     *     returns zimbra.generated.mailclient.mail.testBrowseResponse
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
     *     returns zimbra.generated.mailclient.mail.testCancelAppointmentResponse
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
     *     returns zimbra.generated.mailclient.mail.testCancelTaskResponse
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
     *     returns zimbra.generated.mailclient.mail.testCheckDeviceStatusResponse
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
     *     returns zimbra.generated.mailclient.mail.testCheckPermissionResponse
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
     *     returns zimbra.generated.mailclient.mail.testCheckRecurConflictsResponse
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
     *     returns zimbra.generated.mailclient.mail.testCheckSpellingResponse
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
     *     returns zimbra.generated.mailclient.mail.testCompleteTaskInstanceResponse
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
     *     returns zimbra.generated.mailclient.mail.testContactActionResponse
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
     *     returns zimbra.generated.mailclient.mail.testConvActionResponse
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
     *     returns zimbra.generated.mailclient.mail.testCounterAppointmentResponse
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
     *     returns zimbra.generated.mailclient.mail.testCreateAppointmentExceptionResponse
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
     *     returns zimbra.generated.mailclient.mail.testCreateAppointmentResponse
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
     *     returns zimbra.generated.mailclient.mail.testCreateContactResponse
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
     *     returns zimbra.generated.mailclient.mail.testCreateDataSourceResponse
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
     *     returns zimbra.generated.mailclient.mail.testCreateFolderResponse
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
     *     returns zimbra.generated.mailclient.mail.testCreateMountpointResponse
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
     *     returns zimbra.generated.mailclient.mail.testCreateNoteResponse
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
     *     returns zimbra.generated.mailclient.mail.testCreateSearchFolderResponse
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
     *     returns zimbra.generated.mailclient.mail.testCreateTagResponse
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
     *     returns zimbra.generated.mailclient.mail.testCreateTaskExceptionResponse
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
     *     returns zimbra.generated.mailclient.mail.testCreateTaskResponse
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
     *     returns zimbra.generated.mailclient.mail.testCreateWaitSetResponse
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
     *     returns zimbra.generated.mailclient.mail.testDeclineCounterAppointmentResponse
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
     *     returns zimbra.generated.mailclient.mail.testDeleteDataSourceResponse
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
     *     returns zimbra.generated.mailclient.mail.testDeleteDeviceResponse
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
     *     returns zimbra.generated.mailclient.mail.testDestroyWaitSetResponse
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
     *     returns zimbra.generated.mailclient.mail.testDiffDocumentResponse
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
     *     returns zimbra.generated.mailclient.mail.testDismissCalendarItemAlarmResponse
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
     *     returns zimbra.generated.mailclient.mail.testDocumentActionResponse
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
     *     returns zimbra.generated.mailclient.mail.testEmptyDumpsterResponse
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
     *     returns zimbra.generated.mailclient.mail.testEnableSharedReminderResponse
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
     *     returns zimbra.generated.mailclient.mail.testExpandRecurResponse
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
     *     returns zimbra.generated.mailclient.mail.testExportContactsResponse
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
     *     returns zimbra.generated.mailclient.mail.testFolderActionResponse
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
     *     returns zimbra.generated.mailclient.mail.testForwardAppointmentInviteResponse
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
     *     returns zimbra.generated.mailclient.mail.testForwardAppointmentResponse
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
     *     returns zimbra.generated.mailclient.mail.testGetActivityStreamResponse
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
     *     returns zimbra.generated.mailclient.mail.testGetAllDevicesResponse
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
     *     returns zimbra.generated.mailclient.mail.testGetAppointmentResponse
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
     *     returns zimbra.generated.mailclient.mail.testGetApptSummariesResponse
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
     *     returns zimbra.generated.mailclient.mail.testGetCalendarItemSummariesResponse
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
     *     returns zimbra.generated.mailclient.mail.testGetCommentsResponse
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
     *     returns zimbra.generated.mailclient.mail.testGetContactsResponse
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
     *     returns zimbra.generated.mailclient.mail.testGetConvResponse
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
     *     returns zimbra.generated.mailclient.mail.testGetCustomMetadataResponse
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
     *     returns zimbra.generated.mailclient.mail.testGetDataSourcesResponse
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
     *     returns zimbra.generated.mailclient.mail.testGetEffectiveFolderPermsResponse
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
     *     returns zimbra.generated.mailclient.mail.testGetFilterRulesResponse
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
     *     returns zimbra.generated.mailclient.mail.testGetFolderResponse
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
     *     returns zimbra.generated.mailclient.mail.testGetFreeBusyResponse
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
     *     returns zimbra.generated.mailclient.mail.testGetICalResponse
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
     *     returns zimbra.generated.mailclient.mail.testGetImportStatusResponse
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
     *     returns zimbra.generated.mailclient.mail.testGetItemResponse
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
     *     returns zimbra.generated.mailclient.mail.testGetMailboxMetadataResponse
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
     *     returns zimbra.generated.mailclient.mail.testGetMiniCalResponse
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
     *     returns zimbra.generated.mailclient.mail.testGetMsgMetadataResponse
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
     *     returns zimbra.generated.mailclient.mail.testGetMsgResponse
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
     *     returns zimbra.generated.mailclient.mail.testGetNoteResponse
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
     *     returns zimbra.generated.mailclient.mail.testGetOutgoingFilterRulesResponse
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
     *     returns zimbra.generated.mailclient.mail.testGetPermissionResponse
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
     *     returns zimbra.generated.mailclient.mail.testGetRecurResponse
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
     *     returns zimbra.generated.mailclient.mail.testGetSearchFolderResponse
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
     *     returns zimbra.generated.mailclient.mail.testGetShareNotificationsResponse
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
     *     returns zimbra.generated.mailclient.mail.testGetSpellDictionariesResponse
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
     *     returns zimbra.generated.mailclient.mail.testGetSystemRetentionPolicyResponse
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
     *     returns zimbra.generated.mailclient.mail.testGetTagResponse
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
     *     returns zimbra.generated.mailclient.mail.testGetTaskResponse
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
     *     returns zimbra.generated.mailclient.mail.testGetTaskSummariesResponse
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
     *     returns zimbra.generated.mailclient.mail.testGetWatchersResponse
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
     *     returns zimbra.generated.mailclient.mail.testGetWatchingItemsResponse
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
     *     returns zimbra.generated.mailclient.mail.testGetWorkingHoursResponse
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
     *     returns zimbra.generated.mailclient.mail.testGetYahooAuthTokenResponse
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
     *     returns zimbra.generated.mailclient.mail.testGetYahooCookieResponse
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
     *     returns zimbra.generated.mailclient.mail.testGlobalSearchResponse
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
     *     returns zimbra.generated.mailclient.mail.testGrantPermissionResponse
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
     *     returns zimbra.generated.mailclient.mail.testICalReplyResponse
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
     *     returns zimbra.generated.mailclient.mail.testImportAppointmentsResponse
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
     *     returns zimbra.generated.mailclient.mail.testImportContactsResponse
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
     *     returns zimbra.generated.mailclient.mail.testImportDataResponse
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
     *     returns zimbra.generated.mailclient.mail.testInvalidateReminderDeviceResponse
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
     *     returns zimbra.generated.mailclient.mail.testItemActionResponse
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
     *     returns zimbra.generated.mailclient.mail.testListDocumentRevisionsResponse
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
     *     returns zimbra.generated.mailclient.mail.testModifyAppointmentResponse
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
     *     returns zimbra.generated.mailclient.mail.testModifyContactResponse
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
     *     returns zimbra.generated.mailclient.mail.testModifyDataSourceResponse
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
     *     returns zimbra.generated.mailclient.mail.testModifyFilterRulesResponse
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
     *     returns zimbra.generated.mailclient.mail.testModifyMailboxMetadataResponse
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
     *     returns zimbra.generated.mailclient.mail.testModifyOutgoingFilterRulesResponse
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
     *     returns zimbra.generated.mailclient.mail.testModifySearchFolderResponse
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
     *     returns zimbra.generated.mailclient.mail.testModifyTaskResponse
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
     *     returns zimbra.generated.mailclient.mail.testMsgActionResponse
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
     *     returns zimbra.generated.mailclient.mail.testNoOpResponse
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
     *     returns zimbra.generated.mailclient.mail.testNoteActionResponse
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
     *     returns zimbra.generated.mailclient.mail.testPurgeRevisionResponse
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
     *     returns zimbra.generated.mailclient.mail.testRankingActionResponse
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
     *     returns zimbra.generated.mailclient.mail.testRegisterDeviceResponse
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
     *     returns zimbra.generated.mailclient.mail.testRemoveAttachmentsResponse
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
     *     returns zimbra.generated.mailclient.mail.testRevokePermissionResponse
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
     *     returns zimbra.generated.mailclient.mail.testSaveDocumentResponse
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
     *     returns zimbra.generated.mailclient.mail.testSaveDraftResponse
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
     *     returns zimbra.generated.mailclient.mail.testSearchConvResponse
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
     *     returns zimbra.generated.mailclient.mail.testSearchResponse
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
     *     returns zimbra.generated.mailclient.mail.testSendDeliveryReportResponse
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
     *     returns zimbra.generated.mailclient.mail.testSendInviteReplyResponse
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
     *     returns zimbra.generated.mailclient.mail.testSendMsgResponse
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
     *     returns zimbra.generated.mailclient.mail.testSendShareNotificationResponse
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
     *     returns zimbra.generated.mailclient.mail.testSendVerificationCodeResponse
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
     *     returns zimbra.generated.mailclient.mail.testSetAppointmentResponse
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
     *     returns zimbra.generated.mailclient.mail.testSetCustomMetadataResponse
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
     *     returns zimbra.generated.mailclient.mail.testSetMailboxMetadataResponse
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
     *     returns zimbra.generated.mailclient.mail.testSetTaskResponse
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
     *     returns zimbra.generated.mailclient.mail.testSnoozeCalendarItemAlarmResponse
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
     *     returns zimbra.generated.mailclient.mail.testSyncResponse
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
     *     returns zimbra.generated.mailclient.mail.testTagActionResponse
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
     *     returns zimbra.generated.mailclient.mail.testTestDataSourceResponse
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
     *     returns zimbra.generated.mailclient.mail.testUpdateDeviceStatusResponse
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
     *     returns zimbra.generated.mailclient.mail.testVerifyCodeResponse
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
     *     returns zimbra.generated.mailclient.mail.testWaitSetResponse
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
     *     returns zimbra.generated.mailclient.mail.testWikiActionResponse
     */
    @WebMethod(action = "urn:zimbraMail/WikiAction")
    @WebResult(name = "WikiActionResponse", targetNamespace = "urn:zimbraMail", partName = "parameters")
    public testWikiActionResponse wikiActionRequest(
        @WebParam(name = "WikiActionRequest", targetNamespace = "urn:zimbraMail", partName = "parameters")
        testWikiActionRequest parameters);

}
