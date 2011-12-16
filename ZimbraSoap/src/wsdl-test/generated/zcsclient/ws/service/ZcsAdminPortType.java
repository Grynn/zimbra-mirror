
package generated.zcsclient.ws.service;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.bind.annotation.XmlSeeAlso;
import generated.zcsclient.admin.testAbortHsmRequest;
import generated.zcsclient.admin.testAbortHsmResponse;
import generated.zcsclient.admin.testAbortXMbxSearchRequest;
import generated.zcsclient.admin.testAbortXMbxSearchResponse;
import generated.zcsclient.admin.testActivateLicenseRequest;
import generated.zcsclient.admin.testActivateLicenseResponse;
import generated.zcsclient.admin.testAddAccountAliasRequest;
import generated.zcsclient.admin.testAddAccountAliasResponse;
import generated.zcsclient.admin.testAddAccountLoggerRequest;
import generated.zcsclient.admin.testAddAccountLoggerResponse;
import generated.zcsclient.admin.testAddDistributionListAliasRequest;
import generated.zcsclient.admin.testAddDistributionListAliasResponse;
import generated.zcsclient.admin.testAddDistributionListMemberRequest;
import generated.zcsclient.admin.testAddDistributionListMemberResponse;
import generated.zcsclient.admin.testAddGalSyncDataSourceRequest;
import generated.zcsclient.admin.testAddGalSyncDataSourceResponse;
import generated.zcsclient.admin.testAdminCreateWaitSetRequest;
import generated.zcsclient.admin.testAdminCreateWaitSetResponse;
import generated.zcsclient.admin.testAdminDestroyWaitSetRequest;
import generated.zcsclient.admin.testAdminDestroyWaitSetResponse;
import generated.zcsclient.admin.testAdminWaitSetRequest;
import generated.zcsclient.admin.testAdminWaitSetResponse;
import generated.zcsclient.admin.testAuthRequest;
import generated.zcsclient.admin.testAuthResponse;
import generated.zcsclient.admin.testAutoCompleteGalRequest;
import generated.zcsclient.admin.testAutoCompleteGalResponse;
import generated.zcsclient.admin.testAutoProvAccountRequest;
import generated.zcsclient.admin.testAutoProvAccountResponse;
import generated.zcsclient.admin.testBackupAccountQueryRequest;
import generated.zcsclient.admin.testBackupAccountQueryResponse;
import generated.zcsclient.admin.testBackupQueryRequest;
import generated.zcsclient.admin.testBackupQueryResponse;
import generated.zcsclient.admin.testBackupRequest;
import generated.zcsclient.admin.testBackupResponse;
import generated.zcsclient.admin.testCheckAuthConfigRequest;
import generated.zcsclient.admin.testCheckAuthConfigResponse;
import generated.zcsclient.admin.testCheckBlobConsistencyRequest;
import generated.zcsclient.admin.testCheckBlobConsistencyResponse;
import generated.zcsclient.admin.testCheckDirectoryRequest;
import generated.zcsclient.admin.testCheckDirectoryResponse;
import generated.zcsclient.admin.testCheckDomainMXRecordRequest;
import generated.zcsclient.admin.testCheckDomainMXRecordResponse;
import generated.zcsclient.admin.testCheckExchangeAuthRequest;
import generated.zcsclient.admin.testCheckExchangeAuthResponse;
import generated.zcsclient.admin.testCheckGalConfigRequest;
import generated.zcsclient.admin.testCheckGalConfigResponse;
import generated.zcsclient.admin.testCheckHealthRequest;
import generated.zcsclient.admin.testCheckHealthResponse;
import generated.zcsclient.admin.testCheckHostnameResolveRequest;
import generated.zcsclient.admin.testCheckHostnameResolveResponse;
import generated.zcsclient.admin.testCheckPasswordStrengthRequest;
import generated.zcsclient.admin.testCheckPasswordStrengthResponse;
import generated.zcsclient.admin.testCheckRightRequest;
import generated.zcsclient.admin.testCheckRightResponse;
import generated.zcsclient.admin.testConfigureZimletRequest;
import generated.zcsclient.admin.testConfigureZimletResponse;
import generated.zcsclient.admin.testCopyCosRequest;
import generated.zcsclient.admin.testCopyCosResponse;
import generated.zcsclient.admin.testCountAccountRequest;
import generated.zcsclient.admin.testCountAccountResponse;
import generated.zcsclient.admin.testCreateAccountRequest;
import generated.zcsclient.admin.testCreateAccountResponse;
import generated.zcsclient.admin.testCreateArchiveRequest;
import generated.zcsclient.admin.testCreateArchiveResponse;
import generated.zcsclient.admin.testCreateCalendarResourceRequest;
import generated.zcsclient.admin.testCreateCalendarResourceResponse;
import generated.zcsclient.admin.testCreateCosRequest;
import generated.zcsclient.admin.testCreateCosResponse;
import generated.zcsclient.admin.testCreateDataSourceRequest;
import generated.zcsclient.admin.testCreateDataSourceResponse;
import generated.zcsclient.admin.testCreateDistributionListRequest;
import generated.zcsclient.admin.testCreateDistributionListResponse;
import generated.zcsclient.admin.testCreateDomainRequest;
import generated.zcsclient.admin.testCreateDomainResponse;
import generated.zcsclient.admin.testCreateGalSyncAccountRequest;
import generated.zcsclient.admin.testCreateGalSyncAccountResponse;
import generated.zcsclient.admin.testCreateLDAPEntryRequest;
import generated.zcsclient.admin.testCreateLDAPEntryResponse;
import generated.zcsclient.admin.testCreateServerRequest;
import generated.zcsclient.admin.testCreateServerResponse;
import generated.zcsclient.admin.testCreateSystemRetentionPolicyRequest;
import generated.zcsclient.admin.testCreateSystemRetentionPolicyResponse;
import generated.zcsclient.admin.testCreateVolumeRequest;
import generated.zcsclient.admin.testCreateVolumeResponse;
import generated.zcsclient.admin.testCreateXMPPComponentRequest;
import generated.zcsclient.admin.testCreateXMPPComponentResponse;
import generated.zcsclient.admin.testCreateXMbxSearchRequest;
import generated.zcsclient.admin.testCreateXMbxSearchResponse;
import generated.zcsclient.admin.testCreateZimletRequest;
import generated.zcsclient.admin.testCreateZimletResponse;
import generated.zcsclient.admin.testDelegateAuthRequest;
import generated.zcsclient.admin.testDelegateAuthResponse;
import generated.zcsclient.admin.testDeleteAccountRequest;
import generated.zcsclient.admin.testDeleteAccountResponse;
import generated.zcsclient.admin.testDeleteCalendarResourceRequest;
import generated.zcsclient.admin.testDeleteCalendarResourceResponse;
import generated.zcsclient.admin.testDeleteCosRequest;
import generated.zcsclient.admin.testDeleteCosResponse;
import generated.zcsclient.admin.testDeleteDataSourceRequest;
import generated.zcsclient.admin.testDeleteDataSourceResponse;
import generated.zcsclient.admin.testDeleteDistributionListRequest;
import generated.zcsclient.admin.testDeleteDistributionListResponse;
import generated.zcsclient.admin.testDeleteDomainRequest;
import generated.zcsclient.admin.testDeleteDomainResponse;
import generated.zcsclient.admin.testDeleteGalSyncAccountRequest;
import generated.zcsclient.admin.testDeleteGalSyncAccountResponse;
import generated.zcsclient.admin.testDeleteLDAPEntryRequest;
import generated.zcsclient.admin.testDeleteLDAPEntryResponse;
import generated.zcsclient.admin.testDeleteMailboxRequest;
import generated.zcsclient.admin.testDeleteMailboxResponse;
import generated.zcsclient.admin.testDeleteServerRequest;
import generated.zcsclient.admin.testDeleteServerResponse;
import generated.zcsclient.admin.testDeleteSystemRetentionPolicyRequest;
import generated.zcsclient.admin.testDeleteSystemRetentionPolicyResponse;
import generated.zcsclient.admin.testDeleteVolumeRequest;
import generated.zcsclient.admin.testDeleteVolumeResponse;
import generated.zcsclient.admin.testDeleteXMPPComponentRequest;
import generated.zcsclient.admin.testDeleteXMPPComponentResponse;
import generated.zcsclient.admin.testDeleteXMbxSearchRequest;
import generated.zcsclient.admin.testDeleteXMbxSearchResponse;
import generated.zcsclient.admin.testDeleteZimletRequest;
import generated.zcsclient.admin.testDeleteZimletResponse;
import generated.zcsclient.admin.testDeployZimletRequest;
import generated.zcsclient.admin.testDeployZimletResponse;
import generated.zcsclient.admin.testDisableArchiveRequest;
import generated.zcsclient.admin.testDisableArchiveResponse;
import generated.zcsclient.admin.testDumpSessionsRequest;
import generated.zcsclient.admin.testDumpSessionsResponse;
import generated.zcsclient.admin.testEnableArchiveRequest;
import generated.zcsclient.admin.testEnableArchiveResponse;
import generated.zcsclient.admin.testExportAndDeleteItemsRequest;
import generated.zcsclient.admin.testExportAndDeleteItemsResponse;
import generated.zcsclient.admin.testExportMailboxRequest;
import generated.zcsclient.admin.testExportMailboxResponse;
import generated.zcsclient.admin.testFailoverClusterServiceRequest;
import generated.zcsclient.admin.testFailoverClusterServiceResponse;
import generated.zcsclient.admin.testFixCalendarEndTimeRequest;
import generated.zcsclient.admin.testFixCalendarEndTimeResponse;
import generated.zcsclient.admin.testFixCalendarPriorityRequest;
import generated.zcsclient.admin.testFixCalendarPriorityResponse;
import generated.zcsclient.admin.testFixCalendarTZRequest;
import generated.zcsclient.admin.testFixCalendarTZResponse;
import generated.zcsclient.admin.testFlushCacheRequest;
import generated.zcsclient.admin.testFlushCacheResponse;
import generated.zcsclient.admin.testGenCSRRequest;
import generated.zcsclient.admin.testGenCSRResponse;
import generated.zcsclient.admin.testGetAccountInfoRequest;
import generated.zcsclient.admin.testGetAccountInfoResponse;
import generated.zcsclient.admin.testGetAccountLoggersRequest;
import generated.zcsclient.admin.testGetAccountLoggersResponse;
import generated.zcsclient.admin.testGetAccountMembershipRequest;
import generated.zcsclient.admin.testGetAccountMembershipResponse;
import generated.zcsclient.admin.testGetAccountRequest;
import generated.zcsclient.admin.testGetAccountResponse;
import generated.zcsclient.admin.testGetAdminConsoleUICompRequest;
import generated.zcsclient.admin.testGetAdminConsoleUICompResponse;
import generated.zcsclient.admin.testGetAdminExtensionZimletsRequest;
import generated.zcsclient.admin.testGetAdminExtensionZimletsResponse;
import generated.zcsclient.admin.testGetAdminSavedSearchesRequest;
import generated.zcsclient.admin.testGetAdminSavedSearchesResponse;
import generated.zcsclient.admin.testGetAllAccountLoggersRequest;
import generated.zcsclient.admin.testGetAllAccountLoggersResponse;
import generated.zcsclient.admin.testGetAllAccountsRequest;
import generated.zcsclient.admin.testGetAllAccountsResponse;
import generated.zcsclient.admin.testGetAllAdminAccountsRequest;
import generated.zcsclient.admin.testGetAllAdminAccountsResponse;
import generated.zcsclient.admin.testGetAllCalendarResourcesRequest;
import generated.zcsclient.admin.testGetAllCalendarResourcesResponse;
import generated.zcsclient.admin.testGetAllConfigRequest;
import generated.zcsclient.admin.testGetAllConfigResponse;
import generated.zcsclient.admin.testGetAllCosRequest;
import generated.zcsclient.admin.testGetAllCosResponse;
import generated.zcsclient.admin.testGetAllDistributionListsRequest;
import generated.zcsclient.admin.testGetAllDistributionListsResponse;
import generated.zcsclient.admin.testGetAllDomainsRequest;
import generated.zcsclient.admin.testGetAllDomainsResponse;
import generated.zcsclient.admin.testGetAllEffectiveRightsRequest;
import generated.zcsclient.admin.testGetAllEffectiveRightsResponse;
import generated.zcsclient.admin.testGetAllFreeBusyProvidersRequest;
import generated.zcsclient.admin.testGetAllFreeBusyProvidersResponse;
import generated.zcsclient.admin.testGetAllLocalesRequest;
import generated.zcsclient.admin.testGetAllLocalesResponse;
import generated.zcsclient.admin.testGetAllMailboxesRequest;
import generated.zcsclient.admin.testGetAllMailboxesResponse;
import generated.zcsclient.admin.testGetAllRightsRequest;
import generated.zcsclient.admin.testGetAllRightsResponse;
import generated.zcsclient.admin.testGetAllServersRequest;
import generated.zcsclient.admin.testGetAllServersResponse;
import generated.zcsclient.admin.testGetAllVolumesRequest;
import generated.zcsclient.admin.testGetAllVolumesResponse;
import generated.zcsclient.admin.testGetAllXMPPComponentsRequest;
import generated.zcsclient.admin.testGetAllXMPPComponentsResponse;
import generated.zcsclient.admin.testGetAllZimletsRequest;
import generated.zcsclient.admin.testGetAllZimletsResponse;
import generated.zcsclient.admin.testGetApplianceHSMFSRequest;
import generated.zcsclient.admin.testGetApplianceHSMFSResponse;
import generated.zcsclient.admin.testGetAttributeInfoRequest;
import generated.zcsclient.admin.testGetAttributeInfoResponse;
import generated.zcsclient.admin.testGetCSRRequest;
import generated.zcsclient.admin.testGetCSRResponse;
import generated.zcsclient.admin.testGetCalendarResourceRequest;
import generated.zcsclient.admin.testGetCalendarResourceResponse;
import generated.zcsclient.admin.testGetCertRequest;
import generated.zcsclient.admin.testGetCertResponse;
import generated.zcsclient.admin.testGetClusterStatusRequest;
import generated.zcsclient.admin.testGetClusterStatusResponse;
import generated.zcsclient.admin.testGetConfigRequest;
import generated.zcsclient.admin.testGetConfigResponse;
import generated.zcsclient.admin.testGetCosRequest;
import generated.zcsclient.admin.testGetCosResponse;
import generated.zcsclient.admin.testGetCreateObjectAttrsRequest;
import generated.zcsclient.admin.testGetCreateObjectAttrsResponse;
import generated.zcsclient.admin.testGetCurrentVolumesRequest;
import generated.zcsclient.admin.testGetCurrentVolumesResponse;
import generated.zcsclient.admin.testGetDataSourcesRequest;
import generated.zcsclient.admin.testGetDataSourcesResponse;
import generated.zcsclient.admin.testGetDelegatedAdminConstraintsRequest;
import generated.zcsclient.admin.testGetDelegatedAdminConstraintsResponse;
import generated.zcsclient.admin.testGetDevicesCountRequest;
import generated.zcsclient.admin.testGetDevicesCountResponse;
import generated.zcsclient.admin.testGetDevicesCountSinceLastUsedRequest;
import generated.zcsclient.admin.testGetDevicesCountSinceLastUsedResponse;
import generated.zcsclient.admin.testGetDevicesCountUsedTodayRequest;
import generated.zcsclient.admin.testGetDevicesCountUsedTodayResponse;
import generated.zcsclient.admin.testGetDevicesRequest;
import generated.zcsclient.admin.testGetDevicesResponse;
import generated.zcsclient.admin.testGetDistributionListMembershipRequest;
import generated.zcsclient.admin.testGetDistributionListMembershipResponse;
import generated.zcsclient.admin.testGetDistributionListRequest;
import generated.zcsclient.admin.testGetDistributionListResponse;
import generated.zcsclient.admin.testGetDomainInfoRequest;
import generated.zcsclient.admin.testGetDomainInfoResponse;
import generated.zcsclient.admin.testGetDomainRequest;
import generated.zcsclient.admin.testGetDomainResponse;
import generated.zcsclient.admin.testGetEffectiveRightsRequest;
import generated.zcsclient.admin.testGetEffectiveRightsResponse;
import generated.zcsclient.admin.testGetFreeBusyQueueInfoRequest;
import generated.zcsclient.admin.testGetFreeBusyQueueInfoResponse;
import generated.zcsclient.admin.testGetGrantsRequest;
import generated.zcsclient.admin.testGetGrantsResponse;
import generated.zcsclient.admin.testGetHsmStatusRequest;
import generated.zcsclient.admin.testGetHsmStatusResponse;
import generated.zcsclient.admin.testGetLDAPEntriesRequest;
import generated.zcsclient.admin.testGetLDAPEntriesResponse;
import generated.zcsclient.admin.testGetLicenseInfoRequest;
import generated.zcsclient.admin.testGetLicenseInfoResponse;
import generated.zcsclient.admin.testGetLicenseRequest;
import generated.zcsclient.admin.testGetLicenseResponse;
import generated.zcsclient.admin.testGetLoggerStatsRequest;
import generated.zcsclient.admin.testGetLoggerStatsResponse;
import generated.zcsclient.admin.testGetMailQueueInfoRequest;
import generated.zcsclient.admin.testGetMailQueueInfoResponse;
import generated.zcsclient.admin.testGetMailQueueRequest;
import generated.zcsclient.admin.testGetMailQueueResponse;
import generated.zcsclient.admin.testGetMailboxRequest;
import generated.zcsclient.admin.testGetMailboxResponse;
import generated.zcsclient.admin.testGetMailboxStatsRequest;
import generated.zcsclient.admin.testGetMailboxStatsResponse;
import generated.zcsclient.admin.testGetMailboxVersionRequest;
import generated.zcsclient.admin.testGetMailboxVersionResponse;
import generated.zcsclient.admin.testGetMailboxVolumesRequest;
import generated.zcsclient.admin.testGetMailboxVolumesResponse;
import generated.zcsclient.admin.testGetMemcachedClientConfigRequest;
import generated.zcsclient.admin.testGetMemcachedClientConfigResponse;
import generated.zcsclient.admin.testGetQuotaUsageRequest;
import generated.zcsclient.admin.testGetQuotaUsageResponse;
import generated.zcsclient.admin.testGetRightRequest;
import generated.zcsclient.admin.testGetRightResponse;
import generated.zcsclient.admin.testGetRightsDocRequest;
import generated.zcsclient.admin.testGetRightsDocResponse;
import generated.zcsclient.admin.testGetSMIMEConfigRequest;
import generated.zcsclient.admin.testGetSMIMEConfigResponse;
import generated.zcsclient.admin.testGetServerNIfsRequest;
import generated.zcsclient.admin.testGetServerNIfsResponse;
import generated.zcsclient.admin.testGetServerRequest;
import generated.zcsclient.admin.testGetServerResponse;
import generated.zcsclient.admin.testGetServerStatsRequest;
import generated.zcsclient.admin.testGetServerStatsResponse;
import generated.zcsclient.admin.testGetServiceStatusRequest;
import generated.zcsclient.admin.testGetServiceStatusResponse;
import generated.zcsclient.admin.testGetSessionsRequest;
import generated.zcsclient.admin.testGetSessionsResponse;
import generated.zcsclient.admin.testGetShareInfoRequest;
import generated.zcsclient.admin.testGetShareInfoResponse;
import generated.zcsclient.admin.testGetSystemRetentionPolicyRequest;
import generated.zcsclient.admin.testGetSystemRetentionPolicyResponse;
import generated.zcsclient.admin.testGetVersionInfoRequest;
import generated.zcsclient.admin.testGetVersionInfoResponse;
import generated.zcsclient.admin.testGetVolumeRequest;
import generated.zcsclient.admin.testGetVolumeResponse;
import generated.zcsclient.admin.testGetXMPPComponentRequest;
import generated.zcsclient.admin.testGetXMPPComponentResponse;
import generated.zcsclient.admin.testGetXMbxSearchesListRequest;
import generated.zcsclient.admin.testGetXMbxSearchesListResponse;
import generated.zcsclient.admin.testGetZimletRequest;
import generated.zcsclient.admin.testGetZimletResponse;
import generated.zcsclient.admin.testGetZimletStatusRequest;
import generated.zcsclient.admin.testGetZimletStatusResponse;
import generated.zcsclient.admin.testGrantRightRequest;
import generated.zcsclient.admin.testGrantRightResponse;
import generated.zcsclient.admin.testHsmRequest;
import generated.zcsclient.admin.testHsmResponse;
import generated.zcsclient.admin.testInstallCertRequest;
import generated.zcsclient.admin.testInstallCertResponse;
import generated.zcsclient.admin.testInstallLicenseRequest;
import generated.zcsclient.admin.testInstallLicenseResponse;
import generated.zcsclient.admin.testMailQueueActionRequest;
import generated.zcsclient.admin.testMailQueueActionResponse;
import generated.zcsclient.admin.testMailQueueFlushRequest;
import generated.zcsclient.admin.testMailQueueFlushResponse;
import generated.zcsclient.admin.testMigrateAccountRequest;
import generated.zcsclient.admin.testMigrateAccountResponse;
import generated.zcsclient.admin.testModifyAccountRequest;
import generated.zcsclient.admin.testModifyAccountResponse;
import generated.zcsclient.admin.testModifyAdminSavedSearchesRequest;
import generated.zcsclient.admin.testModifyAdminSavedSearchesResponse;
import generated.zcsclient.admin.testModifyCalendarResourceRequest;
import generated.zcsclient.admin.testModifyCalendarResourceResponse;
import generated.zcsclient.admin.testModifyConfigRequest;
import generated.zcsclient.admin.testModifyConfigResponse;
import generated.zcsclient.admin.testModifyCosRequest;
import generated.zcsclient.admin.testModifyCosResponse;
import generated.zcsclient.admin.testModifyDataSourceRequest;
import generated.zcsclient.admin.testModifyDataSourceResponse;
import generated.zcsclient.admin.testModifyDelegatedAdminConstraintsRequest;
import generated.zcsclient.admin.testModifyDelegatedAdminConstraintsResponse;
import generated.zcsclient.admin.testModifyDistributionListRequest;
import generated.zcsclient.admin.testModifyDistributionListResponse;
import generated.zcsclient.admin.testModifyDomainRequest;
import generated.zcsclient.admin.testModifyDomainResponse;
import generated.zcsclient.admin.testModifyLDAPEntryRequest;
import generated.zcsclient.admin.testModifyLDAPEntryResponse;
import generated.zcsclient.admin.testModifySMIMEConfigRequest;
import generated.zcsclient.admin.testModifySMIMEConfigResponse;
import generated.zcsclient.admin.testModifyServerRequest;
import generated.zcsclient.admin.testModifyServerResponse;
import generated.zcsclient.admin.testModifySystemRetentionPolicyRequest;
import generated.zcsclient.admin.testModifySystemRetentionPolicyResponse;
import generated.zcsclient.admin.testModifyVolumeRequest;
import generated.zcsclient.admin.testModifyVolumeResponse;
import generated.zcsclient.admin.testModifyZimletRequest;
import generated.zcsclient.admin.testModifyZimletResponse;
import generated.zcsclient.admin.testMoveBlobsRequest;
import generated.zcsclient.admin.testMoveBlobsResponse;
import generated.zcsclient.admin.testMoveMailboxRequest;
import generated.zcsclient.admin.testMoveMailboxResponse;
import generated.zcsclient.admin.testNoOpRequest;
import generated.zcsclient.admin.testNoOpResponse;
import generated.zcsclient.admin.testPingRequest;
import generated.zcsclient.admin.testPingResponse;
import generated.zcsclient.admin.testPurgeAccountCalendarCacheRequest;
import generated.zcsclient.admin.testPurgeAccountCalendarCacheResponse;
import generated.zcsclient.admin.testPurgeFreeBusyQueueRequest;
import generated.zcsclient.admin.testPurgeFreeBusyQueueResponse;
import generated.zcsclient.admin.testPurgeMessagesRequest;
import generated.zcsclient.admin.testPurgeMessagesResponse;
import generated.zcsclient.admin.testPurgeMovedMailboxRequest;
import generated.zcsclient.admin.testPurgeMovedMailboxResponse;
import generated.zcsclient.admin.testPushFreeBusyRequest;
import generated.zcsclient.admin.testPushFreeBusyResponse;
import generated.zcsclient.admin.testQueryMailboxMoveRequest;
import generated.zcsclient.admin.testQueryMailboxMoveResponse;
import generated.zcsclient.admin.testQueryWaitSetRequest;
import generated.zcsclient.admin.testQueryWaitSetResponse;
import generated.zcsclient.admin.testReIndexRequest;
import generated.zcsclient.admin.testReIndexResponse;
import generated.zcsclient.admin.testRecalculateMailboxCountsRequest;
import generated.zcsclient.admin.testRecalculateMailboxCountsResponse;
import generated.zcsclient.admin.testRegisterMailboxMoveOutRequest;
import generated.zcsclient.admin.testRegisterMailboxMoveOutResponse;
import generated.zcsclient.admin.testReloadAccountRequest;
import generated.zcsclient.admin.testReloadAccountResponse;
import generated.zcsclient.admin.testReloadLocalConfigRequest;
import generated.zcsclient.admin.testReloadLocalConfigResponse;
import generated.zcsclient.admin.testReloadMemcachedClientConfigRequest;
import generated.zcsclient.admin.testReloadMemcachedClientConfigResponse;
import generated.zcsclient.admin.testRemoteWipeRequest;
import generated.zcsclient.admin.testRemoteWipeResponse;
import generated.zcsclient.admin.testRemoveAccountAliasRequest;
import generated.zcsclient.admin.testRemoveAccountAliasResponse;
import generated.zcsclient.admin.testRemoveAccountLoggerRequest;
import generated.zcsclient.admin.testRemoveAccountLoggerResponse;
import generated.zcsclient.admin.testRemoveDistributionListAliasRequest;
import generated.zcsclient.admin.testRemoveDistributionListAliasResponse;
import generated.zcsclient.admin.testRemoveDistributionListMemberRequest;
import generated.zcsclient.admin.testRemoveDistributionListMemberResponse;
import generated.zcsclient.admin.testRenameAccountRequest;
import generated.zcsclient.admin.testRenameAccountResponse;
import generated.zcsclient.admin.testRenameCalendarResourceRequest;
import generated.zcsclient.admin.testRenameCalendarResourceResponse;
import generated.zcsclient.admin.testRenameCosRequest;
import generated.zcsclient.admin.testRenameCosResponse;
import generated.zcsclient.admin.testRenameDistributionListRequest;
import generated.zcsclient.admin.testRenameDistributionListResponse;
import generated.zcsclient.admin.testRenameLDAPEntryRequest;
import generated.zcsclient.admin.testRenameLDAPEntryResponse;
import generated.zcsclient.admin.testResetAllLoggersRequest;
import generated.zcsclient.admin.testResetAllLoggersResponse;
import generated.zcsclient.admin.testRestoreRequest;
import generated.zcsclient.admin.testRestoreResponse;
import generated.zcsclient.admin.testRevokeRightRequest;
import generated.zcsclient.admin.testRevokeRightResponse;
import generated.zcsclient.admin.testRolloverRedoLogRequest;
import generated.zcsclient.admin.testRolloverRedoLogResponse;
import generated.zcsclient.admin.testRunUnitTestsRequest;
import generated.zcsclient.admin.testRunUnitTestsResponse;
import generated.zcsclient.admin.testScheduleBackupsRequest;
import generated.zcsclient.admin.testScheduleBackupsResponse;
import generated.zcsclient.admin.testSearchAccountsRequest;
import generated.zcsclient.admin.testSearchAccountsResponse;
import generated.zcsclient.admin.testSearchAutoProvDirectoryRequest;
import generated.zcsclient.admin.testSearchAutoProvDirectoryResponse;
import generated.zcsclient.admin.testSearchCalendarResourcesRequest;
import generated.zcsclient.admin.testSearchCalendarResourcesResponse;
import generated.zcsclient.admin.testSearchDirectoryRequest;
import generated.zcsclient.admin.testSearchDirectoryResponse;
import generated.zcsclient.admin.testSearchGalRequest;
import generated.zcsclient.admin.testSearchGalResponse;
import generated.zcsclient.admin.testSearchMultiMailboxRequest;
import generated.zcsclient.admin.testSearchMultiMailboxResponse;
import generated.zcsclient.admin.testSetCurrentVolumeRequest;
import generated.zcsclient.admin.testSetCurrentVolumeResponse;
import generated.zcsclient.admin.testSetPasswordRequest;
import generated.zcsclient.admin.testSetPasswordResponse;
import generated.zcsclient.admin.testSyncGalAccountRequest;
import generated.zcsclient.admin.testSyncGalAccountResponse;
import generated.zcsclient.admin.testUndeployZimletRequest;
import generated.zcsclient.admin.testUndeployZimletResponse;
import generated.zcsclient.admin.testUnloadMailboxRequest;
import generated.zcsclient.admin.testUnloadMailboxResponse;
import generated.zcsclient.admin.testUnregisterMailboxMoveOutRequest;
import generated.zcsclient.admin.testUnregisterMailboxMoveOutResponse;
import generated.zcsclient.admin.testUpdateDeviceStatusRequest;
import generated.zcsclient.admin.testUpdateDeviceStatusResponse;
import generated.zcsclient.admin.testUploadDomCertRequest;
import generated.zcsclient.admin.testUploadDomCertResponse;
import generated.zcsclient.admin.testUploadProxyCARequest;
import generated.zcsclient.admin.testUploadProxyCAResponse;
import generated.zcsclient.admin.testVerifyCertKeyRequest;
import generated.zcsclient.admin.testVerifyCertKeyResponse;
import generated.zcsclient.admin.testVerifyIndexRequest;
import generated.zcsclient.admin.testVerifyIndexResponse;
import generated.zcsclient.admin.testVersionCheckRequest;
import generated.zcsclient.admin.testVersionCheckResponse;
import generated.zcsclient.adminext.testBulkIMAPDataImportRequest;
import generated.zcsclient.adminext.testBulkIMAPDataImportResponse;
import generated.zcsclient.adminext.testBulkImportAccountsRequest;
import generated.zcsclient.adminext.testBulkImportAccountsResponse;
import generated.zcsclient.adminext.testGenerateBulkProvisionFileFromLDAPRequest;
import generated.zcsclient.adminext.testGenerateBulkProvisionFileFromLDAPResponse;
import generated.zcsclient.adminext.testGetBulkIMAPImportTaskListRequest;
import generated.zcsclient.adminext.testGetBulkIMAPImportTaskListResponse;
import generated.zcsclient.adminext.testPurgeBulkIMAPImportTasksRequest;
import generated.zcsclient.adminext.testPurgeBulkIMAPImportTasksResponse;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.1.7-hudson-48-
 * Generated source version: 2.1
 * 
 */
@WebService(name = "zcsAdminPortType", targetNamespace = "http://www.zimbra.com/wsdl/ZimbraService.wsdl")
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE)
@XmlSeeAlso({
    generated.zcsclient.admin.ObjectFactory.class,
    generated.zcsclient.replication.ObjectFactory.class,
    generated.zcsclient.adminext.ObjectFactory.class,
    generated.zcsclient.account.ObjectFactory.class,
    generated.zcsclient.sync.ObjectFactory.class,
    generated.zcsclient.mail.ObjectFactory.class,
    generated.zcsclient.appblast.ObjectFactory.class,
    generated.zcsclient.zm.ObjectFactory.class
})
public interface ZcsAdminPortType {


    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testAbortHsmResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/AbortHsm")
    @WebResult(name = "AbortHsmResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testAbortHsmResponse abortHsmRequest(
        @WebParam(name = "AbortHsmRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testAbortHsmRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testAbortXMbxSearchResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/AbortXMbxSearch")
    @WebResult(name = "AbortXMbxSearchResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testAbortXMbxSearchResponse abortXMbxSearchRequest(
        @WebParam(name = "AbortXMbxSearchRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testAbortXMbxSearchRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testActivateLicenseResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/ActivateLicense")
    @WebResult(name = "ActivateLicenseResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testActivateLicenseResponse activateLicenseRequest(
        @WebParam(name = "ActivateLicenseRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testActivateLicenseRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testAddAccountAliasResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/AddAccountAlias")
    @WebResult(name = "AddAccountAliasResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testAddAccountAliasResponse addAccountAliasRequest(
        @WebParam(name = "AddAccountAliasRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testAddAccountAliasRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testAddAccountLoggerResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/AddAccountLogger")
    @WebResult(name = "AddAccountLoggerResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testAddAccountLoggerResponse addAccountLoggerRequest(
        @WebParam(name = "AddAccountLoggerRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testAddAccountLoggerRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testAddDistributionListAliasResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/AddDistributionListAlias")
    @WebResult(name = "AddDistributionListAliasResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testAddDistributionListAliasResponse addDistributionListAliasRequest(
        @WebParam(name = "AddDistributionListAliasRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testAddDistributionListAliasRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testAddDistributionListMemberResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/AddDistributionListMember")
    @WebResult(name = "AddDistributionListMemberResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testAddDistributionListMemberResponse addDistributionListMemberRequest(
        @WebParam(name = "AddDistributionListMemberRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testAddDistributionListMemberRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testAddGalSyncDataSourceResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/AddGalSyncDataSource")
    @WebResult(name = "AddGalSyncDataSourceResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testAddGalSyncDataSourceResponse addGalSyncDataSourceRequest(
        @WebParam(name = "AddGalSyncDataSourceRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testAddGalSyncDataSourceRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testAdminCreateWaitSetResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/AdminCreateWaitSet")
    @WebResult(name = "AdminCreateWaitSetResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testAdminCreateWaitSetResponse adminCreateWaitSetRequest(
        @WebParam(name = "AdminCreateWaitSetRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testAdminCreateWaitSetRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testAdminDestroyWaitSetResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/AdminDestroyWaitSet")
    @WebResult(name = "AdminDestroyWaitSetResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testAdminDestroyWaitSetResponse adminDestroyWaitSetRequest(
        @WebParam(name = "AdminDestroyWaitSetRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testAdminDestroyWaitSetRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testAdminWaitSetResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/AdminWaitSet")
    @WebResult(name = "AdminWaitSetResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testAdminWaitSetResponse adminWaitSetRequest(
        @WebParam(name = "AdminWaitSetRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testAdminWaitSetRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testAuthResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/Auth")
    @WebResult(name = "AuthResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testAuthResponse authRequest(
        @WebParam(name = "AuthRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testAuthRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testAutoCompleteGalResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/AutoCompleteGal")
    @WebResult(name = "AutoCompleteGalResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testAutoCompleteGalResponse autoCompleteGalRequest(
        @WebParam(name = "AutoCompleteGalRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testAutoCompleteGalRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testAutoProvAccountResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/AutoProvAccount")
    @WebResult(name = "AutoProvAccountResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testAutoProvAccountResponse autoProvAccountRequest(
        @WebParam(name = "AutoProvAccountRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testAutoProvAccountRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testBackupAccountQueryResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/BackupAccountQuery")
    @WebResult(name = "BackupAccountQueryResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testBackupAccountQueryResponse backupAccountQueryRequest(
        @WebParam(name = "BackupAccountQueryRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testBackupAccountQueryRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testBackupQueryResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/BackupQuery")
    @WebResult(name = "BackupQueryResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testBackupQueryResponse backupQueryRequest(
        @WebParam(name = "BackupQueryRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testBackupQueryRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testBackupResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/Backup")
    @WebResult(name = "BackupResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testBackupResponse backupRequest(
        @WebParam(name = "BackupRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testBackupRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testCheckAuthConfigResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/CheckAuthConfig")
    @WebResult(name = "CheckAuthConfigResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testCheckAuthConfigResponse checkAuthConfigRequest(
        @WebParam(name = "CheckAuthConfigRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testCheckAuthConfigRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testCheckBlobConsistencyResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/CheckBlobConsistency")
    @WebResult(name = "CheckBlobConsistencyResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testCheckBlobConsistencyResponse checkBlobConsistencyRequest(
        @WebParam(name = "CheckBlobConsistencyRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testCheckBlobConsistencyRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testCheckDirectoryResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/CheckDirectory")
    @WebResult(name = "CheckDirectoryResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testCheckDirectoryResponse checkDirectoryRequest(
        @WebParam(name = "CheckDirectoryRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testCheckDirectoryRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testCheckDomainMXRecordResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/CheckDomainMXRecord")
    @WebResult(name = "CheckDomainMXRecordResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testCheckDomainMXRecordResponse checkDomainMXRecordRequest(
        @WebParam(name = "CheckDomainMXRecordRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testCheckDomainMXRecordRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testCheckExchangeAuthResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/CheckExchangeAuth")
    @WebResult(name = "CheckExchangeAuthResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testCheckExchangeAuthResponse checkExchangeAuthRequest(
        @WebParam(name = "CheckExchangeAuthRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testCheckExchangeAuthRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testCheckGalConfigResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/CheckGalConfig")
    @WebResult(name = "CheckGalConfigResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testCheckGalConfigResponse checkGalConfigRequest(
        @WebParam(name = "CheckGalConfigRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testCheckGalConfigRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testCheckHealthResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/CheckHealth")
    @WebResult(name = "CheckHealthResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testCheckHealthResponse checkHealthRequest(
        @WebParam(name = "CheckHealthRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testCheckHealthRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testCheckHostnameResolveResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/CheckHostnameResolve")
    @WebResult(name = "CheckHostnameResolveResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testCheckHostnameResolveResponse checkHostnameResolveRequest(
        @WebParam(name = "CheckHostnameResolveRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testCheckHostnameResolveRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testCheckPasswordStrengthResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/CheckPasswordStrength")
    @WebResult(name = "CheckPasswordStrengthResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testCheckPasswordStrengthResponse checkPasswordStrengthRequest(
        @WebParam(name = "CheckPasswordStrengthRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testCheckPasswordStrengthRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testCheckRightResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/CheckRight")
    @WebResult(name = "CheckRightResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testCheckRightResponse checkRightRequest(
        @WebParam(name = "CheckRightRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testCheckRightRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testConfigureZimletResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/ConfigureZimlet")
    @WebResult(name = "ConfigureZimletResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testConfigureZimletResponse configureZimletRequest(
        @WebParam(name = "ConfigureZimletRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testConfigureZimletRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testCopyCosResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/CopyCos")
    @WebResult(name = "CopyCosResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testCopyCosResponse copyCosRequest(
        @WebParam(name = "CopyCosRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testCopyCosRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testCountAccountResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/CountAccount")
    @WebResult(name = "CountAccountResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testCountAccountResponse countAccountRequest(
        @WebParam(name = "CountAccountRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testCountAccountRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testCreateAccountResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/CreateAccount")
    @WebResult(name = "CreateAccountResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testCreateAccountResponse createAccountRequest(
        @WebParam(name = "CreateAccountRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testCreateAccountRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testCreateArchiveResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/CreateArchive")
    @WebResult(name = "CreateArchiveResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testCreateArchiveResponse createArchiveRequest(
        @WebParam(name = "CreateArchiveRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testCreateArchiveRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testCreateCalendarResourceResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/CreateCalendarResource")
    @WebResult(name = "CreateCalendarResourceResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testCreateCalendarResourceResponse createCalendarResourceRequest(
        @WebParam(name = "CreateCalendarResourceRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testCreateCalendarResourceRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testCreateCosResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/CreateCos")
    @WebResult(name = "CreateCosResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testCreateCosResponse createCosRequest(
        @WebParam(name = "CreateCosRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testCreateCosRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testCreateDataSourceResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/CreateDataSource")
    @WebResult(name = "CreateDataSourceResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testCreateDataSourceResponse createDataSourceRequest(
        @WebParam(name = "CreateDataSourceRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testCreateDataSourceRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testCreateDistributionListResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/CreateDistributionList")
    @WebResult(name = "CreateDistributionListResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testCreateDistributionListResponse createDistributionListRequest(
        @WebParam(name = "CreateDistributionListRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testCreateDistributionListRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testCreateDomainResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/CreateDomain")
    @WebResult(name = "CreateDomainResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testCreateDomainResponse createDomainRequest(
        @WebParam(name = "CreateDomainRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testCreateDomainRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testCreateGalSyncAccountResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/CreateGalSyncAccount")
    @WebResult(name = "CreateGalSyncAccountResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testCreateGalSyncAccountResponse createGalSyncAccountRequest(
        @WebParam(name = "CreateGalSyncAccountRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testCreateGalSyncAccountRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testCreateLDAPEntryResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/CreateLDAPEntry")
    @WebResult(name = "CreateLDAPEntryResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testCreateLDAPEntryResponse createLDAPEntryRequest(
        @WebParam(name = "CreateLDAPEntryRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testCreateLDAPEntryRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testCreateServerResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/CreateServer")
    @WebResult(name = "CreateServerResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testCreateServerResponse createServerRequest(
        @WebParam(name = "CreateServerRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testCreateServerRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testCreateSystemRetentionPolicyResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/CreateSystemRetentionPolicy")
    @WebResult(name = "CreateSystemRetentionPolicyResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testCreateSystemRetentionPolicyResponse createSystemRetentionPolicyRequest(
        @WebParam(name = "CreateSystemRetentionPolicyRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testCreateSystemRetentionPolicyRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testCreateVolumeResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/CreateVolume")
    @WebResult(name = "CreateVolumeResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testCreateVolumeResponse createVolumeRequest(
        @WebParam(name = "CreateVolumeRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testCreateVolumeRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testCreateXMPPComponentResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/CreateXMPPComponent")
    @WebResult(name = "CreateXMPPComponentResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testCreateXMPPComponentResponse createXMPPComponentRequest(
        @WebParam(name = "CreateXMPPComponentRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testCreateXMPPComponentRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testCreateXMbxSearchResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/CreateXMbxSearch")
    @WebResult(name = "CreateXMbxSearchResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testCreateXMbxSearchResponse createXMbxSearchRequest(
        @WebParam(name = "CreateXMbxSearchRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testCreateXMbxSearchRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testCreateZimletResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/CreateZimlet")
    @WebResult(name = "CreateZimletResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testCreateZimletResponse createZimletRequest(
        @WebParam(name = "CreateZimletRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testCreateZimletRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testDelegateAuthResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/DelegateAuth")
    @WebResult(name = "DelegateAuthResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testDelegateAuthResponse delegateAuthRequest(
        @WebParam(name = "DelegateAuthRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testDelegateAuthRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testDeleteAccountResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/DeleteAccount")
    @WebResult(name = "DeleteAccountResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testDeleteAccountResponse deleteAccountRequest(
        @WebParam(name = "DeleteAccountRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testDeleteAccountRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testDeleteCalendarResourceResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/DeleteCalendarResource")
    @WebResult(name = "DeleteCalendarResourceResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testDeleteCalendarResourceResponse deleteCalendarResourceRequest(
        @WebParam(name = "DeleteCalendarResourceRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testDeleteCalendarResourceRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testDeleteCosResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/DeleteCos")
    @WebResult(name = "DeleteCosResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testDeleteCosResponse deleteCosRequest(
        @WebParam(name = "DeleteCosRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testDeleteCosRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testDeleteDataSourceResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/DeleteDataSource")
    @WebResult(name = "DeleteDataSourceResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testDeleteDataSourceResponse deleteDataSourceRequest(
        @WebParam(name = "DeleteDataSourceRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testDeleteDataSourceRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testDeleteDistributionListResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/DeleteDistributionList")
    @WebResult(name = "DeleteDistributionListResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testDeleteDistributionListResponse deleteDistributionListRequest(
        @WebParam(name = "DeleteDistributionListRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testDeleteDistributionListRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testDeleteDomainResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/DeleteDomain")
    @WebResult(name = "DeleteDomainResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testDeleteDomainResponse deleteDomainRequest(
        @WebParam(name = "DeleteDomainRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testDeleteDomainRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testDeleteGalSyncAccountResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/DeleteGalSyncAccount")
    @WebResult(name = "DeleteGalSyncAccountResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testDeleteGalSyncAccountResponse deleteGalSyncAccountRequest(
        @WebParam(name = "DeleteGalSyncAccountRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testDeleteGalSyncAccountRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testDeleteLDAPEntryResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/DeleteLDAPEntry")
    @WebResult(name = "DeleteLDAPEntryResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testDeleteLDAPEntryResponse deleteLDAPEntryRequest(
        @WebParam(name = "DeleteLDAPEntryRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testDeleteLDAPEntryRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testDeleteMailboxResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/DeleteMailbox")
    @WebResult(name = "DeleteMailboxResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testDeleteMailboxResponse deleteMailboxRequest(
        @WebParam(name = "DeleteMailboxRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testDeleteMailboxRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testDeleteServerResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/DeleteServer")
    @WebResult(name = "DeleteServerResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testDeleteServerResponse deleteServerRequest(
        @WebParam(name = "DeleteServerRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testDeleteServerRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testDeleteSystemRetentionPolicyResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/DeleteSystemRetentionPolicy")
    @WebResult(name = "DeleteSystemRetentionPolicyResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testDeleteSystemRetentionPolicyResponse deleteSystemRetentionPolicyRequest(
        @WebParam(name = "DeleteSystemRetentionPolicyRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testDeleteSystemRetentionPolicyRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testDeleteVolumeResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/DeleteVolume")
    @WebResult(name = "DeleteVolumeResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testDeleteVolumeResponse deleteVolumeRequest(
        @WebParam(name = "DeleteVolumeRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testDeleteVolumeRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testDeleteXMPPComponentResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/DeleteXMPPComponent")
    @WebResult(name = "DeleteXMPPComponentResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testDeleteXMPPComponentResponse deleteXMPPComponentRequest(
        @WebParam(name = "DeleteXMPPComponentRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testDeleteXMPPComponentRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testDeleteXMbxSearchResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/DeleteXMbxSearch")
    @WebResult(name = "DeleteXMbxSearchResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testDeleteXMbxSearchResponse deleteXMbxSearchRequest(
        @WebParam(name = "DeleteXMbxSearchRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testDeleteXMbxSearchRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testDeleteZimletResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/DeleteZimlet")
    @WebResult(name = "DeleteZimletResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testDeleteZimletResponse deleteZimletRequest(
        @WebParam(name = "DeleteZimletRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testDeleteZimletRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testDeployZimletResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/DeployZimlet")
    @WebResult(name = "DeployZimletResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testDeployZimletResponse deployZimletRequest(
        @WebParam(name = "DeployZimletRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testDeployZimletRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testDisableArchiveResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/DisableArchive")
    @WebResult(name = "DisableArchiveResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testDisableArchiveResponse disableArchiveRequest(
        @WebParam(name = "DisableArchiveRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testDisableArchiveRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testDumpSessionsResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/DumpSessions")
    @WebResult(name = "DumpSessionsResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testDumpSessionsResponse dumpSessionsRequest(
        @WebParam(name = "DumpSessionsRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testDumpSessionsRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testEnableArchiveResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/EnableArchive")
    @WebResult(name = "EnableArchiveResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testEnableArchiveResponse enableArchiveRequest(
        @WebParam(name = "EnableArchiveRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testEnableArchiveRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testExportAndDeleteItemsResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/ExportAndDeleteItems")
    @WebResult(name = "ExportAndDeleteItemsResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testExportAndDeleteItemsResponse exportAndDeleteItemsRequest(
        @WebParam(name = "ExportAndDeleteItemsRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testExportAndDeleteItemsRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testExportMailboxResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/ExportMailbox")
    @WebResult(name = "ExportMailboxResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testExportMailboxResponse exportMailboxRequest(
        @WebParam(name = "ExportMailboxRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testExportMailboxRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testFailoverClusterServiceResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/FailoverClusterService")
    @WebResult(name = "FailoverClusterServiceResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testFailoverClusterServiceResponse failoverClusterServiceRequest(
        @WebParam(name = "FailoverClusterServiceRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testFailoverClusterServiceRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testFixCalendarEndTimeResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/FixCalendarEndTime")
    @WebResult(name = "FixCalendarEndTimeResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testFixCalendarEndTimeResponse fixCalendarEndTimeRequest(
        @WebParam(name = "FixCalendarEndTimeRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testFixCalendarEndTimeRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testFixCalendarPriorityResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/FixCalendarPriority")
    @WebResult(name = "FixCalendarPriorityResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testFixCalendarPriorityResponse fixCalendarPriorityRequest(
        @WebParam(name = "FixCalendarPriorityRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testFixCalendarPriorityRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testFixCalendarTZResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/FixCalendarTZ")
    @WebResult(name = "FixCalendarTZResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testFixCalendarTZResponse fixCalendarTZRequest(
        @WebParam(name = "FixCalendarTZRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testFixCalendarTZRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testFlushCacheResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/FlushCache")
    @WebResult(name = "FlushCacheResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testFlushCacheResponse flushCacheRequest(
        @WebParam(name = "FlushCacheRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testFlushCacheRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testGenCSRResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/GenCSR")
    @WebResult(name = "GenCSRResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testGenCSRResponse genCSRRequest(
        @WebParam(name = "GenCSRRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testGenCSRRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testGetAccountInfoResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/GetAccountInfo")
    @WebResult(name = "GetAccountInfoResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testGetAccountInfoResponse getAccountInfoRequest(
        @WebParam(name = "GetAccountInfoRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testGetAccountInfoRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testGetAccountLoggersResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/GetAccountLoggers")
    @WebResult(name = "GetAccountLoggersResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testGetAccountLoggersResponse getAccountLoggersRequest(
        @WebParam(name = "GetAccountLoggersRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testGetAccountLoggersRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testGetAccountMembershipResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/GetAccountMembership")
    @WebResult(name = "GetAccountMembershipResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testGetAccountMembershipResponse getAccountMembershipRequest(
        @WebParam(name = "GetAccountMembershipRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testGetAccountMembershipRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testGetAccountResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/GetAccount")
    @WebResult(name = "GetAccountResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testGetAccountResponse getAccountRequest(
        @WebParam(name = "GetAccountRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testGetAccountRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testGetAdminConsoleUICompResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/GetAdminConsoleUIComp")
    @WebResult(name = "GetAdminConsoleUICompResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testGetAdminConsoleUICompResponse getAdminConsoleUICompRequest(
        @WebParam(name = "GetAdminConsoleUICompRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testGetAdminConsoleUICompRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testGetAdminExtensionZimletsResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/GetAdminExtensionZimlets")
    @WebResult(name = "GetAdminExtensionZimletsResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testGetAdminExtensionZimletsResponse getAdminExtensionZimletsRequest(
        @WebParam(name = "GetAdminExtensionZimletsRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testGetAdminExtensionZimletsRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testGetAdminSavedSearchesResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/GetAdminSavedSearches")
    @WebResult(name = "GetAdminSavedSearchesResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testGetAdminSavedSearchesResponse getAdminSavedSearchesRequest(
        @WebParam(name = "GetAdminSavedSearchesRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testGetAdminSavedSearchesRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testGetAllAccountLoggersResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/GetAllAccountLoggers")
    @WebResult(name = "GetAllAccountLoggersResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testGetAllAccountLoggersResponse getAllAccountLoggersRequest(
        @WebParam(name = "GetAllAccountLoggersRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testGetAllAccountLoggersRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testGetAllAccountsResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/GetAllAccounts")
    @WebResult(name = "GetAllAccountsResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testGetAllAccountsResponse getAllAccountsRequest(
        @WebParam(name = "GetAllAccountsRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testGetAllAccountsRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testGetAllAdminAccountsResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/GetAllAdminAccounts")
    @WebResult(name = "GetAllAdminAccountsResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testGetAllAdminAccountsResponse getAllAdminAccountsRequest(
        @WebParam(name = "GetAllAdminAccountsRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testGetAllAdminAccountsRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testGetAllCalendarResourcesResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/GetAllCalendarResources")
    @WebResult(name = "GetAllCalendarResourcesResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testGetAllCalendarResourcesResponse getAllCalendarResourcesRequest(
        @WebParam(name = "GetAllCalendarResourcesRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testGetAllCalendarResourcesRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testGetAllConfigResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/GetAllConfig")
    @WebResult(name = "GetAllConfigResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testGetAllConfigResponse getAllConfigRequest(
        @WebParam(name = "GetAllConfigRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testGetAllConfigRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testGetAllCosResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/GetAllCos")
    @WebResult(name = "GetAllCosResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testGetAllCosResponse getAllCosRequest(
        @WebParam(name = "GetAllCosRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testGetAllCosRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testGetAllDistributionListsResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/GetAllDistributionLists")
    @WebResult(name = "GetAllDistributionListsResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testGetAllDistributionListsResponse getAllDistributionListsRequest(
        @WebParam(name = "GetAllDistributionListsRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testGetAllDistributionListsRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testGetAllDomainsResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/GetAllDomains")
    @WebResult(name = "GetAllDomainsResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testGetAllDomainsResponse getAllDomainsRequest(
        @WebParam(name = "GetAllDomainsRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testGetAllDomainsRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testGetAllEffectiveRightsResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/GetAllEffectiveRights")
    @WebResult(name = "GetAllEffectiveRightsResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testGetAllEffectiveRightsResponse getAllEffectiveRightsRequest(
        @WebParam(name = "GetAllEffectiveRightsRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testGetAllEffectiveRightsRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testGetAllFreeBusyProvidersResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/GetAllFreeBusyProviders")
    @WebResult(name = "GetAllFreeBusyProvidersResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testGetAllFreeBusyProvidersResponse getAllFreeBusyProvidersRequest(
        @WebParam(name = "GetAllFreeBusyProvidersRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testGetAllFreeBusyProvidersRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testGetAllLocalesResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/GetAllLocales")
    @WebResult(name = "GetAllLocalesResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testGetAllLocalesResponse getAllLocalesRequest(
        @WebParam(name = "GetAllLocalesRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testGetAllLocalesRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testGetAllMailboxesResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/GetAllMailboxes")
    @WebResult(name = "GetAllMailboxesResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testGetAllMailboxesResponse getAllMailboxesRequest(
        @WebParam(name = "GetAllMailboxesRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testGetAllMailboxesRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testGetAllRightsResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/GetAllRights")
    @WebResult(name = "GetAllRightsResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testGetAllRightsResponse getAllRightsRequest(
        @WebParam(name = "GetAllRightsRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testGetAllRightsRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testGetAllServersResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/GetAllServers")
    @WebResult(name = "GetAllServersResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testGetAllServersResponse getAllServersRequest(
        @WebParam(name = "GetAllServersRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testGetAllServersRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testGetAllVolumesResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/GetAllVolumes")
    @WebResult(name = "GetAllVolumesResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testGetAllVolumesResponse getAllVolumesRequest(
        @WebParam(name = "GetAllVolumesRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testGetAllVolumesRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testGetAllXMPPComponentsResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/GetAllXMPPComponents")
    @WebResult(name = "GetAllXMPPComponentsResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testGetAllXMPPComponentsResponse getAllXMPPComponentsRequest(
        @WebParam(name = "GetAllXMPPComponentsRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testGetAllXMPPComponentsRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testGetAllZimletsResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/GetAllZimlets")
    @WebResult(name = "GetAllZimletsResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testGetAllZimletsResponse getAllZimletsRequest(
        @WebParam(name = "GetAllZimletsRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testGetAllZimletsRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testGetApplianceHSMFSResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/GetApplianceHSMFS")
    @WebResult(name = "GetApplianceHSMFSResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testGetApplianceHSMFSResponse getApplianceHSMFSRequest(
        @WebParam(name = "GetApplianceHSMFSRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testGetApplianceHSMFSRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testGetAttributeInfoResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/GetAttributeInfo")
    @WebResult(name = "GetAttributeInfoResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testGetAttributeInfoResponse getAttributeInfoRequest(
        @WebParam(name = "GetAttributeInfoRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testGetAttributeInfoRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testGetCSRResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/GetCSR")
    @WebResult(name = "GetCSRResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testGetCSRResponse getCSRRequest(
        @WebParam(name = "GetCSRRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testGetCSRRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testGetCalendarResourceResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/GetCalendarResource")
    @WebResult(name = "GetCalendarResourceResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testGetCalendarResourceResponse getCalendarResourceRequest(
        @WebParam(name = "GetCalendarResourceRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testGetCalendarResourceRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testGetCertResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/GetCert")
    @WebResult(name = "GetCertResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testGetCertResponse getCertRequest(
        @WebParam(name = "GetCertRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testGetCertRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testGetClusterStatusResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/GetClusterStatus")
    @WebResult(name = "GetClusterStatusResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testGetClusterStatusResponse getClusterStatusRequest(
        @WebParam(name = "GetClusterStatusRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testGetClusterStatusRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testGetConfigResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/GetConfig")
    @WebResult(name = "GetConfigResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testGetConfigResponse getConfigRequest(
        @WebParam(name = "GetConfigRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testGetConfigRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testGetCosResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/GetCos")
    @WebResult(name = "GetCosResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testGetCosResponse getCosRequest(
        @WebParam(name = "GetCosRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testGetCosRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testGetCreateObjectAttrsResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/GetCreateObjectAttrs")
    @WebResult(name = "GetCreateObjectAttrsResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testGetCreateObjectAttrsResponse getCreateObjectAttrsRequest(
        @WebParam(name = "GetCreateObjectAttrsRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testGetCreateObjectAttrsRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testGetCurrentVolumesResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/GetCurrentVolumes")
    @WebResult(name = "GetCurrentVolumesResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testGetCurrentVolumesResponse getCurrentVolumesRequest(
        @WebParam(name = "GetCurrentVolumesRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testGetCurrentVolumesRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testGetDataSourcesResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/GetDataSources")
    @WebResult(name = "GetDataSourcesResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testGetDataSourcesResponse getDataSourcesRequest(
        @WebParam(name = "GetDataSourcesRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testGetDataSourcesRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testGetDelegatedAdminConstraintsResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/GetDelegatedAdminConstraints")
    @WebResult(name = "GetDelegatedAdminConstraintsResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testGetDelegatedAdminConstraintsResponse getDelegatedAdminConstraintsRequest(
        @WebParam(name = "GetDelegatedAdminConstraintsRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testGetDelegatedAdminConstraintsRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testGetDevicesCountResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/GetDevicesCount")
    @WebResult(name = "GetDevicesCountResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testGetDevicesCountResponse getDevicesCountRequest(
        @WebParam(name = "GetDevicesCountRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testGetDevicesCountRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testGetDevicesCountSinceLastUsedResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/GetDevicesCountSinceLastUsed")
    @WebResult(name = "GetDevicesCountSinceLastUsedResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testGetDevicesCountSinceLastUsedResponse getDevicesCountSinceLastUsedRequest(
        @WebParam(name = "GetDevicesCountSinceLastUsedRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testGetDevicesCountSinceLastUsedRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testGetDevicesCountUsedTodayResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/GetDevicesCountUsedToday")
    @WebResult(name = "GetDevicesCountUsedTodayResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testGetDevicesCountUsedTodayResponse getDevicesCountUsedTodayRequest(
        @WebParam(name = "GetDevicesCountUsedTodayRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testGetDevicesCountUsedTodayRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testGetDevicesResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/GetDevices")
    @WebResult(name = "GetDevicesResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testGetDevicesResponse getDevicesRequest(
        @WebParam(name = "GetDevicesRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testGetDevicesRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testGetDistributionListMembershipResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/GetDistributionListMembership")
    @WebResult(name = "GetDistributionListMembershipResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testGetDistributionListMembershipResponse getDistributionListMembershipRequest(
        @WebParam(name = "GetDistributionListMembershipRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testGetDistributionListMembershipRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testGetDistributionListResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/GetDistributionList")
    @WebResult(name = "GetDistributionListResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testGetDistributionListResponse getDistributionListRequest(
        @WebParam(name = "GetDistributionListRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testGetDistributionListRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testGetDomainInfoResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/GetDomainInfo")
    @WebResult(name = "GetDomainInfoResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testGetDomainInfoResponse getDomainInfoRequest(
        @WebParam(name = "GetDomainInfoRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testGetDomainInfoRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testGetDomainResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/GetDomain")
    @WebResult(name = "GetDomainResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testGetDomainResponse getDomainRequest(
        @WebParam(name = "GetDomainRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testGetDomainRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testGetEffectiveRightsResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/GetEffectiveRights")
    @WebResult(name = "GetEffectiveRightsResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testGetEffectiveRightsResponse getEffectiveRightsRequest(
        @WebParam(name = "GetEffectiveRightsRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testGetEffectiveRightsRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testGetFreeBusyQueueInfoResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/GetFreeBusyQueueInfo")
    @WebResult(name = "GetFreeBusyQueueInfoResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testGetFreeBusyQueueInfoResponse getFreeBusyQueueInfoRequest(
        @WebParam(name = "GetFreeBusyQueueInfoRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testGetFreeBusyQueueInfoRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testGetGrantsResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/GetGrants")
    @WebResult(name = "GetGrantsResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testGetGrantsResponse getGrantsRequest(
        @WebParam(name = "GetGrantsRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testGetGrantsRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testGetHsmStatusResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/GetHsmStatus")
    @WebResult(name = "GetHsmStatusResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testGetHsmStatusResponse getHsmStatusRequest(
        @WebParam(name = "GetHsmStatusRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testGetHsmStatusRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testGetLDAPEntriesResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/GetLDAPEntries")
    @WebResult(name = "GetLDAPEntriesResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testGetLDAPEntriesResponse getLDAPEntriesRequest(
        @WebParam(name = "GetLDAPEntriesRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testGetLDAPEntriesRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testGetLicenseInfoResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/GetLicenseInfo")
    @WebResult(name = "GetLicenseInfoResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testGetLicenseInfoResponse getLicenseInfoRequest(
        @WebParam(name = "GetLicenseInfoRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testGetLicenseInfoRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testGetLicenseResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/GetLicense")
    @WebResult(name = "GetLicenseResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testGetLicenseResponse getLicenseRequest(
        @WebParam(name = "GetLicenseRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testGetLicenseRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testGetLoggerStatsResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/GetLoggerStats")
    @WebResult(name = "GetLoggerStatsResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testGetLoggerStatsResponse getLoggerStatsRequest(
        @WebParam(name = "GetLoggerStatsRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testGetLoggerStatsRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testGetMailQueueInfoResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/GetMailQueueInfo")
    @WebResult(name = "GetMailQueueInfoResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testGetMailQueueInfoResponse getMailQueueInfoRequest(
        @WebParam(name = "GetMailQueueInfoRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testGetMailQueueInfoRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testGetMailQueueResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/GetMailQueue")
    @WebResult(name = "GetMailQueueResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testGetMailQueueResponse getMailQueueRequest(
        @WebParam(name = "GetMailQueueRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testGetMailQueueRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testGetMailboxResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/GetMailbox")
    @WebResult(name = "GetMailboxResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testGetMailboxResponse getMailboxRequest(
        @WebParam(name = "GetMailboxRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testGetMailboxRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testGetMailboxStatsResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/GetMailboxStats")
    @WebResult(name = "GetMailboxStatsResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testGetMailboxStatsResponse getMailboxStatsRequest(
        @WebParam(name = "GetMailboxStatsRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testGetMailboxStatsRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testGetMailboxVersionResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/GetMailboxVersion")
    @WebResult(name = "GetMailboxVersionResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testGetMailboxVersionResponse getMailboxVersionRequest(
        @WebParam(name = "GetMailboxVersionRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testGetMailboxVersionRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testGetMailboxVolumesResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/GetMailboxVolumes")
    @WebResult(name = "GetMailboxVolumesResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testGetMailboxVolumesResponse getMailboxVolumesRequest(
        @WebParam(name = "GetMailboxVolumesRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testGetMailboxVolumesRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testGetMemcachedClientConfigResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/GetMemcachedClientConfig")
    @WebResult(name = "GetMemcachedClientConfigResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testGetMemcachedClientConfigResponse getMemcachedClientConfigRequest(
        @WebParam(name = "GetMemcachedClientConfigRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testGetMemcachedClientConfigRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testGetQuotaUsageResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/GetQuotaUsage")
    @WebResult(name = "GetQuotaUsageResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testGetQuotaUsageResponse getQuotaUsageRequest(
        @WebParam(name = "GetQuotaUsageRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testGetQuotaUsageRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testGetRightResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/GetRight")
    @WebResult(name = "GetRightResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testGetRightResponse getRightRequest(
        @WebParam(name = "GetRightRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testGetRightRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testGetRightsDocResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/GetRightsDoc")
    @WebResult(name = "GetRightsDocResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testGetRightsDocResponse getRightsDocRequest(
        @WebParam(name = "GetRightsDocRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testGetRightsDocRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testGetSMIMEConfigResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/GetSMIMEConfig")
    @WebResult(name = "GetSMIMEConfigResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testGetSMIMEConfigResponse getSMIMEConfigRequest(
        @WebParam(name = "GetSMIMEConfigRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testGetSMIMEConfigRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testGetServerNIfsResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/GetServerNIfs")
    @WebResult(name = "GetServerNIfsResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testGetServerNIfsResponse getServerNIfsRequest(
        @WebParam(name = "GetServerNIfsRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testGetServerNIfsRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testGetServerResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/GetServer")
    @WebResult(name = "GetServerResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testGetServerResponse getServerRequest(
        @WebParam(name = "GetServerRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testGetServerRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testGetServerStatsResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/GetServerStats")
    @WebResult(name = "GetServerStatsResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testGetServerStatsResponse getServerStatsRequest(
        @WebParam(name = "GetServerStatsRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testGetServerStatsRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testGetServiceStatusResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/GetServiceStatus")
    @WebResult(name = "GetServiceStatusResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testGetServiceStatusResponse getServiceStatusRequest(
        @WebParam(name = "GetServiceStatusRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testGetServiceStatusRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testGetSessionsResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/GetSessions")
    @WebResult(name = "GetSessionsResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testGetSessionsResponse getSessionsRequest(
        @WebParam(name = "GetSessionsRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testGetSessionsRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testGetShareInfoResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/GetShareInfo")
    @WebResult(name = "GetShareInfoResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testGetShareInfoResponse getShareInfoRequest(
        @WebParam(name = "GetShareInfoRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testGetShareInfoRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testGetSystemRetentionPolicyResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/GetSystemRetentionPolicy")
    @WebResult(name = "GetSystemRetentionPolicyResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testGetSystemRetentionPolicyResponse getSystemRetentionPolicyRequest(
        @WebParam(name = "GetSystemRetentionPolicyRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testGetSystemRetentionPolicyRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testGetVersionInfoResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/GetVersionInfo")
    @WebResult(name = "GetVersionInfoResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testGetVersionInfoResponse getVersionInfoRequest(
        @WebParam(name = "GetVersionInfoRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testGetVersionInfoRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testGetVolumeResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/GetVolume")
    @WebResult(name = "GetVolumeResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testGetVolumeResponse getVolumeRequest(
        @WebParam(name = "GetVolumeRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testGetVolumeRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testGetXMPPComponentResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/GetXMPPComponent")
    @WebResult(name = "GetXMPPComponentResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testGetXMPPComponentResponse getXMPPComponentRequest(
        @WebParam(name = "GetXMPPComponentRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testGetXMPPComponentRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testGetXMbxSearchesListResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/GetXMbxSearchesList")
    @WebResult(name = "GetXMbxSearchesListResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testGetXMbxSearchesListResponse getXMbxSearchesListRequest(
        @WebParam(name = "GetXMbxSearchesListRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testGetXMbxSearchesListRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testGetZimletResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/GetZimlet")
    @WebResult(name = "GetZimletResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testGetZimletResponse getZimletRequest(
        @WebParam(name = "GetZimletRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testGetZimletRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testGetZimletStatusResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/GetZimletStatus")
    @WebResult(name = "GetZimletStatusResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testGetZimletStatusResponse getZimletStatusRequest(
        @WebParam(name = "GetZimletStatusRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testGetZimletStatusRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testGrantRightResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/GrantRight")
    @WebResult(name = "GrantRightResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testGrantRightResponse grantRightRequest(
        @WebParam(name = "GrantRightRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testGrantRightRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testHsmResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/Hsm")
    @WebResult(name = "HsmResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testHsmResponse hsmRequest(
        @WebParam(name = "HsmRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testHsmRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testInstallCertResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/InstallCert")
    @WebResult(name = "InstallCertResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testInstallCertResponse installCertRequest(
        @WebParam(name = "InstallCertRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testInstallCertRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testInstallLicenseResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/InstallLicense")
    @WebResult(name = "InstallLicenseResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testInstallLicenseResponse installLicenseRequest(
        @WebParam(name = "InstallLicenseRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testInstallLicenseRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testMailQueueActionResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/MailQueueAction")
    @WebResult(name = "MailQueueActionResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testMailQueueActionResponse mailQueueActionRequest(
        @WebParam(name = "MailQueueActionRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testMailQueueActionRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testMailQueueFlushResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/MailQueueFlush")
    @WebResult(name = "MailQueueFlushResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testMailQueueFlushResponse mailQueueFlushRequest(
        @WebParam(name = "MailQueueFlushRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testMailQueueFlushRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testMigrateAccountResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/MigrateAccount")
    @WebResult(name = "MigrateAccountResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testMigrateAccountResponse migrateAccountRequest(
        @WebParam(name = "MigrateAccountRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testMigrateAccountRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testModifyAccountResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/ModifyAccount")
    @WebResult(name = "ModifyAccountResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testModifyAccountResponse modifyAccountRequest(
        @WebParam(name = "ModifyAccountRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testModifyAccountRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testModifyAdminSavedSearchesResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/ModifyAdminSavedSearches")
    @WebResult(name = "ModifyAdminSavedSearchesResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testModifyAdminSavedSearchesResponse modifyAdminSavedSearchesRequest(
        @WebParam(name = "ModifyAdminSavedSearchesRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testModifyAdminSavedSearchesRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testModifyCalendarResourceResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/ModifyCalendarResource")
    @WebResult(name = "ModifyCalendarResourceResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testModifyCalendarResourceResponse modifyCalendarResourceRequest(
        @WebParam(name = "ModifyCalendarResourceRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testModifyCalendarResourceRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testModifyConfigResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/ModifyConfig")
    @WebResult(name = "ModifyConfigResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testModifyConfigResponse modifyConfigRequest(
        @WebParam(name = "ModifyConfigRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testModifyConfigRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testModifyCosResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/ModifyCos")
    @WebResult(name = "ModifyCosResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testModifyCosResponse modifyCosRequest(
        @WebParam(name = "ModifyCosRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testModifyCosRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testModifyDataSourceResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/ModifyDataSource")
    @WebResult(name = "ModifyDataSourceResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testModifyDataSourceResponse modifyDataSourceRequest(
        @WebParam(name = "ModifyDataSourceRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testModifyDataSourceRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testModifyDelegatedAdminConstraintsResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/ModifyDelegatedAdminConstraints")
    @WebResult(name = "ModifyDelegatedAdminConstraintsResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testModifyDelegatedAdminConstraintsResponse modifyDelegatedAdminConstraintsRequest(
        @WebParam(name = "ModifyDelegatedAdminConstraintsRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testModifyDelegatedAdminConstraintsRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testModifyDistributionListResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/ModifyDistributionList")
    @WebResult(name = "ModifyDistributionListResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testModifyDistributionListResponse modifyDistributionListRequest(
        @WebParam(name = "ModifyDistributionListRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testModifyDistributionListRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testModifyDomainResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/ModifyDomain")
    @WebResult(name = "ModifyDomainResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testModifyDomainResponse modifyDomainRequest(
        @WebParam(name = "ModifyDomainRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testModifyDomainRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testModifyLDAPEntryResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/ModifyLDAPEntry")
    @WebResult(name = "ModifyLDAPEntryResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testModifyLDAPEntryResponse modifyLDAPEntryRequest(
        @WebParam(name = "ModifyLDAPEntryRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testModifyLDAPEntryRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testModifySMIMEConfigResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/ModifySMIMEConfig")
    @WebResult(name = "ModifySMIMEConfigResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testModifySMIMEConfigResponse modifySMIMEConfigRequest(
        @WebParam(name = "ModifySMIMEConfigRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testModifySMIMEConfigRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testModifyServerResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/ModifyServer")
    @WebResult(name = "ModifyServerResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testModifyServerResponse modifyServerRequest(
        @WebParam(name = "ModifyServerRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testModifyServerRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testModifySystemRetentionPolicyResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/ModifySystemRetentionPolicy")
    @WebResult(name = "ModifySystemRetentionPolicyResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testModifySystemRetentionPolicyResponse modifySystemRetentionPolicyRequest(
        @WebParam(name = "ModifySystemRetentionPolicyRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testModifySystemRetentionPolicyRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testModifyVolumeResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/ModifyVolume")
    @WebResult(name = "ModifyVolumeResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testModifyVolumeResponse modifyVolumeRequest(
        @WebParam(name = "ModifyVolumeRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testModifyVolumeRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testModifyZimletResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/ModifyZimlet")
    @WebResult(name = "ModifyZimletResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testModifyZimletResponse modifyZimletRequest(
        @WebParam(name = "ModifyZimletRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testModifyZimletRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testMoveBlobsResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/MoveBlobs")
    @WebResult(name = "MoveBlobsResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testMoveBlobsResponse moveBlobsRequest(
        @WebParam(name = "MoveBlobsRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testMoveBlobsRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testMoveMailboxResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/MoveMailbox")
    @WebResult(name = "MoveMailboxResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testMoveMailboxResponse moveMailboxRequest(
        @WebParam(name = "MoveMailboxRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testMoveMailboxRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testNoOpResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/NoOp")
    @WebResult(name = "NoOpResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testNoOpResponse noOpRequest(
        @WebParam(name = "NoOpRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testNoOpRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testPingResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/Ping")
    @WebResult(name = "PingResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testPingResponse pingRequest(
        @WebParam(name = "PingRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testPingRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testPurgeAccountCalendarCacheResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/PurgeAccountCalendarCache")
    @WebResult(name = "PurgeAccountCalendarCacheResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testPurgeAccountCalendarCacheResponse purgeAccountCalendarCacheRequest(
        @WebParam(name = "PurgeAccountCalendarCacheRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testPurgeAccountCalendarCacheRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testPurgeFreeBusyQueueResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/PurgeFreeBusyQueue")
    @WebResult(name = "PurgeFreeBusyQueueResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testPurgeFreeBusyQueueResponse purgeFreeBusyQueueRequest(
        @WebParam(name = "PurgeFreeBusyQueueRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testPurgeFreeBusyQueueRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testPurgeMessagesResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/PurgeMessages")
    @WebResult(name = "PurgeMessagesResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testPurgeMessagesResponse purgeMessagesRequest(
        @WebParam(name = "PurgeMessagesRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testPurgeMessagesRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testPurgeMovedMailboxResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/PurgeMovedMailbox")
    @WebResult(name = "PurgeMovedMailboxResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testPurgeMovedMailboxResponse purgeMovedMailboxRequest(
        @WebParam(name = "PurgeMovedMailboxRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testPurgeMovedMailboxRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testPushFreeBusyResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/PushFreeBusy")
    @WebResult(name = "PushFreeBusyResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testPushFreeBusyResponse pushFreeBusyRequest(
        @WebParam(name = "PushFreeBusyRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testPushFreeBusyRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testQueryMailboxMoveResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/QueryMailboxMove")
    @WebResult(name = "QueryMailboxMoveResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testQueryMailboxMoveResponse queryMailboxMoveRequest(
        @WebParam(name = "QueryMailboxMoveRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testQueryMailboxMoveRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testQueryWaitSetResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/QueryWaitSet")
    @WebResult(name = "QueryWaitSetResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testQueryWaitSetResponse queryWaitSetRequest(
        @WebParam(name = "QueryWaitSetRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testQueryWaitSetRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testReIndexResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/ReIndex")
    @WebResult(name = "ReIndexResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testReIndexResponse reIndexRequest(
        @WebParam(name = "ReIndexRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testReIndexRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testRecalculateMailboxCountsResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/RecalculateMailboxCounts")
    @WebResult(name = "RecalculateMailboxCountsResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testRecalculateMailboxCountsResponse recalculateMailboxCountsRequest(
        @WebParam(name = "RecalculateMailboxCountsRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testRecalculateMailboxCountsRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testRegisterMailboxMoveOutResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/RegisterMailboxMoveOut")
    @WebResult(name = "RegisterMailboxMoveOutResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testRegisterMailboxMoveOutResponse registerMailboxMoveOutRequest(
        @WebParam(name = "RegisterMailboxMoveOutRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testRegisterMailboxMoveOutRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testReloadAccountResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/ReloadAccount")
    @WebResult(name = "ReloadAccountResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testReloadAccountResponse reloadAccountRequest(
        @WebParam(name = "ReloadAccountRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testReloadAccountRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testReloadLocalConfigResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/ReloadLocalConfig")
    @WebResult(name = "ReloadLocalConfigResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testReloadLocalConfigResponse reloadLocalConfigRequest(
        @WebParam(name = "ReloadLocalConfigRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testReloadLocalConfigRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testReloadMemcachedClientConfigResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/ReloadMemcachedClientConfig")
    @WebResult(name = "ReloadMemcachedClientConfigResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testReloadMemcachedClientConfigResponse reloadMemcachedClientConfigRequest(
        @WebParam(name = "ReloadMemcachedClientConfigRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testReloadMemcachedClientConfigRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testRemoteWipeResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/RemoteWipe")
    @WebResult(name = "RemoteWipeResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testRemoteWipeResponse remoteWipeRequest(
        @WebParam(name = "RemoteWipeRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testRemoteWipeRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testRemoveAccountAliasResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/RemoveAccountAlias")
    @WebResult(name = "RemoveAccountAliasResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testRemoveAccountAliasResponse removeAccountAliasRequest(
        @WebParam(name = "RemoveAccountAliasRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testRemoveAccountAliasRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testRemoveAccountLoggerResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/RemoveAccountLogger")
    @WebResult(name = "RemoveAccountLoggerResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testRemoveAccountLoggerResponse removeAccountLoggerRequest(
        @WebParam(name = "RemoveAccountLoggerRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testRemoveAccountLoggerRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testRemoveDistributionListAliasResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/RemoveDistributionListAlias")
    @WebResult(name = "RemoveDistributionListAliasResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testRemoveDistributionListAliasResponse removeDistributionListAliasRequest(
        @WebParam(name = "RemoveDistributionListAliasRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testRemoveDistributionListAliasRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testRemoveDistributionListMemberResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/RemoveDistributionListMember")
    @WebResult(name = "RemoveDistributionListMemberResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testRemoveDistributionListMemberResponse removeDistributionListMemberRequest(
        @WebParam(name = "RemoveDistributionListMemberRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testRemoveDistributionListMemberRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testRenameAccountResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/RenameAccount")
    @WebResult(name = "RenameAccountResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testRenameAccountResponse renameAccountRequest(
        @WebParam(name = "RenameAccountRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testRenameAccountRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testRenameCalendarResourceResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/RenameCalendarResource")
    @WebResult(name = "RenameCalendarResourceResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testRenameCalendarResourceResponse renameCalendarResourceRequest(
        @WebParam(name = "RenameCalendarResourceRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testRenameCalendarResourceRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testRenameCosResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/RenameCos")
    @WebResult(name = "RenameCosResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testRenameCosResponse renameCosRequest(
        @WebParam(name = "RenameCosRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testRenameCosRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testRenameDistributionListResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/RenameDistributionList")
    @WebResult(name = "RenameDistributionListResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testRenameDistributionListResponse renameDistributionListRequest(
        @WebParam(name = "RenameDistributionListRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testRenameDistributionListRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testRenameLDAPEntryResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/RenameLDAPEntry")
    @WebResult(name = "RenameLDAPEntryResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testRenameLDAPEntryResponse renameLDAPEntryRequest(
        @WebParam(name = "RenameLDAPEntryRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testRenameLDAPEntryRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testResetAllLoggersResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/ResetAllLoggers")
    @WebResult(name = "ResetAllLoggersResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testResetAllLoggersResponse resetAllLoggersRequest(
        @WebParam(name = "ResetAllLoggersRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testResetAllLoggersRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testRestoreResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/Restore")
    @WebResult(name = "RestoreResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testRestoreResponse restoreRequest(
        @WebParam(name = "RestoreRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testRestoreRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testRevokeRightResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/RevokeRight")
    @WebResult(name = "RevokeRightResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testRevokeRightResponse revokeRightRequest(
        @WebParam(name = "RevokeRightRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testRevokeRightRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testRolloverRedoLogResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/RolloverRedoLog")
    @WebResult(name = "RolloverRedoLogResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testRolloverRedoLogResponse rolloverRedoLogRequest(
        @WebParam(name = "RolloverRedoLogRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testRolloverRedoLogRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testRunUnitTestsResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/RunUnitTests")
    @WebResult(name = "RunUnitTestsResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testRunUnitTestsResponse runUnitTestsRequest(
        @WebParam(name = "RunUnitTestsRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testRunUnitTestsRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testScheduleBackupsResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/ScheduleBackups")
    @WebResult(name = "ScheduleBackupsResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testScheduleBackupsResponse scheduleBackupsRequest(
        @WebParam(name = "ScheduleBackupsRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testScheduleBackupsRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testSearchAccountsResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/SearchAccounts")
    @WebResult(name = "SearchAccountsResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testSearchAccountsResponse searchAccountsRequest(
        @WebParam(name = "SearchAccountsRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testSearchAccountsRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testSearchAutoProvDirectoryResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/SearchAutoProvDirectory")
    @WebResult(name = "SearchAutoProvDirectoryResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testSearchAutoProvDirectoryResponse searchAutoProvDirectoryRequest(
        @WebParam(name = "SearchAutoProvDirectoryRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testSearchAutoProvDirectoryRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testSearchCalendarResourcesResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/SearchCalendarResources")
    @WebResult(name = "SearchCalendarResourcesResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testSearchCalendarResourcesResponse searchCalendarResourcesRequest(
        @WebParam(name = "SearchCalendarResourcesRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testSearchCalendarResourcesRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testSearchDirectoryResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/SearchDirectory")
    @WebResult(name = "SearchDirectoryResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testSearchDirectoryResponse searchDirectoryRequest(
        @WebParam(name = "SearchDirectoryRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testSearchDirectoryRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testSearchGalResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/SearchGal")
    @WebResult(name = "SearchGalResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testSearchGalResponse searchGalRequest(
        @WebParam(name = "SearchGalRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testSearchGalRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testSearchMultiMailboxResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/SearchMultiMailbox")
    @WebResult(name = "SearchMultiMailboxResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testSearchMultiMailboxResponse searchMultiMailboxRequest(
        @WebParam(name = "SearchMultiMailboxRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testSearchMultiMailboxRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testSetCurrentVolumeResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/SetCurrentVolume")
    @WebResult(name = "SetCurrentVolumeResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testSetCurrentVolumeResponse setCurrentVolumeRequest(
        @WebParam(name = "SetCurrentVolumeRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testSetCurrentVolumeRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testSetPasswordResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/SetPassword")
    @WebResult(name = "SetPasswordResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testSetPasswordResponse setPasswordRequest(
        @WebParam(name = "SetPasswordRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testSetPasswordRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testSyncGalAccountResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/SyncGalAccount")
    @WebResult(name = "SyncGalAccountResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testSyncGalAccountResponse syncGalAccountRequest(
        @WebParam(name = "SyncGalAccountRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testSyncGalAccountRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testUndeployZimletResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/UndeployZimlet")
    @WebResult(name = "UndeployZimletResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testUndeployZimletResponse undeployZimletRequest(
        @WebParam(name = "UndeployZimletRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testUndeployZimletRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testUnloadMailboxResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/UnloadMailbox")
    @WebResult(name = "UnloadMailboxResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testUnloadMailboxResponse unloadMailboxRequest(
        @WebParam(name = "UnloadMailboxRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testUnloadMailboxRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testUnregisterMailboxMoveOutResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/UnregisterMailboxMoveOut")
    @WebResult(name = "UnregisterMailboxMoveOutResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testUnregisterMailboxMoveOutResponse unregisterMailboxMoveOutRequest(
        @WebParam(name = "UnregisterMailboxMoveOutRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testUnregisterMailboxMoveOutRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testUpdateDeviceStatusResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/UpdateDeviceStatus")
    @WebResult(name = "UpdateDeviceStatusResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testUpdateDeviceStatusResponse updateDeviceStatusRequest(
        @WebParam(name = "UpdateDeviceStatusRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testUpdateDeviceStatusRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testUploadDomCertResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/UploadDomCert")
    @WebResult(name = "UploadDomCertResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testUploadDomCertResponse uploadDomCertRequest(
        @WebParam(name = "UploadDomCertRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testUploadDomCertRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testUploadProxyCAResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/UploadProxyCA")
    @WebResult(name = "UploadProxyCAResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testUploadProxyCAResponse uploadProxyCARequest(
        @WebParam(name = "UploadProxyCARequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testUploadProxyCARequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testVerifyCertKeyResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/VerifyCertKey")
    @WebResult(name = "VerifyCertKeyResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testVerifyCertKeyResponse verifyCertKeyRequest(
        @WebParam(name = "VerifyCertKeyRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testVerifyCertKeyRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testVerifyIndexResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/VerifyIndex")
    @WebResult(name = "VerifyIndexResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testVerifyIndexResponse verifyIndexRequest(
        @WebParam(name = "VerifyIndexRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testVerifyIndexRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.admin.testVersionCheckResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/VersionCheck")
    @WebResult(name = "VersionCheckResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testVersionCheckResponse versionCheckRequest(
        @WebParam(name = "VersionCheckRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testVersionCheckRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.adminext.testBulkIMAPDataImportResponse
     */
    @WebMethod(action = "urn:zimbraAdminExt/BulkIMAPDataImport")
    @WebResult(name = "BulkIMAPDataImportResponse", targetNamespace = "urn:zimbraAdminExt", partName = "parameters")
    public testBulkIMAPDataImportResponse bulkIMAPDataImportRequest(
        @WebParam(name = "BulkIMAPDataImportRequest", targetNamespace = "urn:zimbraAdminExt", partName = "parameters")
        testBulkIMAPDataImportRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.adminext.testBulkImportAccountsResponse
     */
    @WebMethod(action = "urn:zimbraAdminExt/BulkImportAccounts")
    @WebResult(name = "BulkImportAccountsResponse", targetNamespace = "urn:zimbraAdminExt", partName = "parameters")
    public testBulkImportAccountsResponse bulkImportAccountsRequest(
        @WebParam(name = "BulkImportAccountsRequest", targetNamespace = "urn:zimbraAdminExt", partName = "parameters")
        testBulkImportAccountsRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.adminext.testGenerateBulkProvisionFileFromLDAPResponse
     */
    @WebMethod(action = "urn:zimbraAdminExt/GenerateBulkProvisionFileFromLDAP")
    @WebResult(name = "GenerateBulkProvisionFileFromLDAPResponse", targetNamespace = "urn:zimbraAdminExt", partName = "parameters")
    public testGenerateBulkProvisionFileFromLDAPResponse generateBulkProvisionFileFromLDAPRequest(
        @WebParam(name = "GenerateBulkProvisionFileFromLDAPRequest", targetNamespace = "urn:zimbraAdminExt", partName = "parameters")
        testGenerateBulkProvisionFileFromLDAPRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.adminext.testGetBulkIMAPImportTaskListResponse
     */
    @WebMethod(action = "urn:zimbraAdminExt/GetBulkIMAPImportTaskList")
    @WebResult(name = "GetBulkIMAPImportTaskListResponse", targetNamespace = "urn:zimbraAdminExt", partName = "parameters")
    public testGetBulkIMAPImportTaskListResponse getBulkIMAPImportTaskListRequest(
        @WebParam(name = "GetBulkIMAPImportTaskListRequest", targetNamespace = "urn:zimbraAdminExt", partName = "parameters")
        testGetBulkIMAPImportTaskListRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns generated.zcsclient.adminext.testPurgeBulkIMAPImportTasksResponse
     */
    @WebMethod(action = "urn:zimbraAdminExt/PurgeBulkIMAPImportTasks")
    @WebResult(name = "PurgeBulkIMAPImportTasksResponse", targetNamespace = "urn:zimbraAdminExt", partName = "parameters")
    public testPurgeBulkIMAPImportTasksResponse purgeBulkIMAPImportTasksRequest(
        @WebParam(name = "PurgeBulkIMAPImportTasksRequest", targetNamespace = "urn:zimbraAdminExt", partName = "parameters")
        testPurgeBulkIMAPImportTasksRequest parameters);

}
