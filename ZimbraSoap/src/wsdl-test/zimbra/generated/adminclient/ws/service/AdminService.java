
package zimbra.generated.adminclient.ws.service;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.bind.annotation.XmlSeeAlso;
import zimbra.generated.adminclient.admin.testAbortHsmRequest;
import zimbra.generated.adminclient.admin.testAbortHsmResponse;
import zimbra.generated.adminclient.admin.testAbortXMbxSearchRequest;
import zimbra.generated.adminclient.admin.testAbortXMbxSearchResponse;
import zimbra.generated.adminclient.admin.testActivateLicenseRequest;
import zimbra.generated.adminclient.admin.testActivateLicenseResponse;
import zimbra.generated.adminclient.admin.testAddAccountAliasRequest;
import zimbra.generated.adminclient.admin.testAddAccountAliasResponse;
import zimbra.generated.adminclient.admin.testAddAccountLoggerRequest;
import zimbra.generated.adminclient.admin.testAddAccountLoggerResponse;
import zimbra.generated.adminclient.admin.testAddDistributionListAliasRequest;
import zimbra.generated.adminclient.admin.testAddDistributionListAliasResponse;
import zimbra.generated.adminclient.admin.testAddDistributionListMemberRequest;
import zimbra.generated.adminclient.admin.testAddDistributionListMemberResponse;
import zimbra.generated.adminclient.admin.testAddGalSyncDataSourceRequest;
import zimbra.generated.adminclient.admin.testAddGalSyncDataSourceResponse;
import zimbra.generated.adminclient.admin.testAdminCreateWaitSetRequest;
import zimbra.generated.adminclient.admin.testAdminCreateWaitSetResponse;
import zimbra.generated.adminclient.admin.testAdminDestroyWaitSetRequest;
import zimbra.generated.adminclient.admin.testAdminDestroyWaitSetResponse;
import zimbra.generated.adminclient.admin.testAdminWaitSetRequest;
import zimbra.generated.adminclient.admin.testAdminWaitSetResponse;
import zimbra.generated.adminclient.admin.testAuthRequest;
import zimbra.generated.adminclient.admin.testAuthResponse;
import zimbra.generated.adminclient.admin.testAutoCompleteGalRequest;
import zimbra.generated.adminclient.admin.testAutoCompleteGalResponse;
import zimbra.generated.adminclient.admin.testAutoProvAccountRequest;
import zimbra.generated.adminclient.admin.testAutoProvAccountResponse;
import zimbra.generated.adminclient.admin.testBackupAccountQueryRequest;
import zimbra.generated.adminclient.admin.testBackupAccountQueryResponse;
import zimbra.generated.adminclient.admin.testBackupQueryRequest;
import zimbra.generated.adminclient.admin.testBackupQueryResponse;
import zimbra.generated.adminclient.admin.testBackupRequest;
import zimbra.generated.adminclient.admin.testBackupResponse;
import zimbra.generated.adminclient.admin.testCheckAuthConfigRequest;
import zimbra.generated.adminclient.admin.testCheckAuthConfigResponse;
import zimbra.generated.adminclient.admin.testCheckBlobConsistencyRequest;
import zimbra.generated.adminclient.admin.testCheckBlobConsistencyResponse;
import zimbra.generated.adminclient.admin.testCheckDirectoryRequest;
import zimbra.generated.adminclient.admin.testCheckDirectoryResponse;
import zimbra.generated.adminclient.admin.testCheckDomainMXRecordRequest;
import zimbra.generated.adminclient.admin.testCheckDomainMXRecordResponse;
import zimbra.generated.adminclient.admin.testCheckExchangeAuthRequest;
import zimbra.generated.adminclient.admin.testCheckExchangeAuthResponse;
import zimbra.generated.adminclient.admin.testCheckGalConfigRequest;
import zimbra.generated.adminclient.admin.testCheckGalConfigResponse;
import zimbra.generated.adminclient.admin.testCheckHealthRequest;
import zimbra.generated.adminclient.admin.testCheckHealthResponse;
import zimbra.generated.adminclient.admin.testCheckHostnameResolveRequest;
import zimbra.generated.adminclient.admin.testCheckHostnameResolveResponse;
import zimbra.generated.adminclient.admin.testCheckPasswordStrengthRequest;
import zimbra.generated.adminclient.admin.testCheckPasswordStrengthResponse;
import zimbra.generated.adminclient.admin.testCheckRightRequest;
import zimbra.generated.adminclient.admin.testCheckRightResponse;
import zimbra.generated.adminclient.admin.testConfigureZimletRequest;
import zimbra.generated.adminclient.admin.testConfigureZimletResponse;
import zimbra.generated.adminclient.admin.testCopyCosRequest;
import zimbra.generated.adminclient.admin.testCopyCosResponse;
import zimbra.generated.adminclient.admin.testCountAccountRequest;
import zimbra.generated.adminclient.admin.testCountAccountResponse;
import zimbra.generated.adminclient.admin.testCreateAccountRequest;
import zimbra.generated.adminclient.admin.testCreateAccountResponse;
import zimbra.generated.adminclient.admin.testCreateArchiveRequest;
import zimbra.generated.adminclient.admin.testCreateArchiveResponse;
import zimbra.generated.adminclient.admin.testCreateCalendarResourceRequest;
import zimbra.generated.adminclient.admin.testCreateCalendarResourceResponse;
import zimbra.generated.adminclient.admin.testCreateCosRequest;
import zimbra.generated.adminclient.admin.testCreateCosResponse;
import zimbra.generated.adminclient.admin.testCreateDataSourceRequest;
import zimbra.generated.adminclient.admin.testCreateDataSourceResponse;
import zimbra.generated.adminclient.admin.testCreateDistributionListRequest;
import zimbra.generated.adminclient.admin.testCreateDistributionListResponse;
import zimbra.generated.adminclient.admin.testCreateDomainRequest;
import zimbra.generated.adminclient.admin.testCreateDomainResponse;
import zimbra.generated.adminclient.admin.testCreateGalSyncAccountRequest;
import zimbra.generated.adminclient.admin.testCreateGalSyncAccountResponse;
import zimbra.generated.adminclient.admin.testCreateLDAPEntryRequest;
import zimbra.generated.adminclient.admin.testCreateLDAPEntryResponse;
import zimbra.generated.adminclient.admin.testCreateServerRequest;
import zimbra.generated.adminclient.admin.testCreateServerResponse;
import zimbra.generated.adminclient.admin.testCreateSystemRetentionPolicyRequest;
import zimbra.generated.adminclient.admin.testCreateSystemRetentionPolicyResponse;
import zimbra.generated.adminclient.admin.testCreateVolumeRequest;
import zimbra.generated.adminclient.admin.testCreateVolumeResponse;
import zimbra.generated.adminclient.admin.testCreateXMPPComponentRequest;
import zimbra.generated.adminclient.admin.testCreateXMPPComponentResponse;
import zimbra.generated.adminclient.admin.testCreateXMbxSearchRequest;
import zimbra.generated.adminclient.admin.testCreateXMbxSearchResponse;
import zimbra.generated.adminclient.admin.testCreateZimletRequest;
import zimbra.generated.adminclient.admin.testCreateZimletResponse;
import zimbra.generated.adminclient.admin.testDelegateAuthRequest;
import zimbra.generated.adminclient.admin.testDelegateAuthResponse;
import zimbra.generated.adminclient.admin.testDeleteAccountRequest;
import zimbra.generated.adminclient.admin.testDeleteAccountResponse;
import zimbra.generated.adminclient.admin.testDeleteCalendarResourceRequest;
import zimbra.generated.adminclient.admin.testDeleteCalendarResourceResponse;
import zimbra.generated.adminclient.admin.testDeleteCosRequest;
import zimbra.generated.adminclient.admin.testDeleteCosResponse;
import zimbra.generated.adminclient.admin.testDeleteDataSourceRequest;
import zimbra.generated.adminclient.admin.testDeleteDataSourceResponse;
import zimbra.generated.adminclient.admin.testDeleteDistributionListRequest;
import zimbra.generated.adminclient.admin.testDeleteDistributionListResponse;
import zimbra.generated.adminclient.admin.testDeleteDomainRequest;
import zimbra.generated.adminclient.admin.testDeleteDomainResponse;
import zimbra.generated.adminclient.admin.testDeleteGalSyncAccountRequest;
import zimbra.generated.adminclient.admin.testDeleteGalSyncAccountResponse;
import zimbra.generated.adminclient.admin.testDeleteLDAPEntryRequest;
import zimbra.generated.adminclient.admin.testDeleteLDAPEntryResponse;
import zimbra.generated.adminclient.admin.testDeleteMailboxRequest;
import zimbra.generated.adminclient.admin.testDeleteMailboxResponse;
import zimbra.generated.adminclient.admin.testDeleteServerRequest;
import zimbra.generated.adminclient.admin.testDeleteServerResponse;
import zimbra.generated.adminclient.admin.testDeleteSystemRetentionPolicyRequest;
import zimbra.generated.adminclient.admin.testDeleteSystemRetentionPolicyResponse;
import zimbra.generated.adminclient.admin.testDeleteVolumeRequest;
import zimbra.generated.adminclient.admin.testDeleteVolumeResponse;
import zimbra.generated.adminclient.admin.testDeleteXMPPComponentRequest;
import zimbra.generated.adminclient.admin.testDeleteXMPPComponentResponse;
import zimbra.generated.adminclient.admin.testDeleteXMbxSearchRequest;
import zimbra.generated.adminclient.admin.testDeleteXMbxSearchResponse;
import zimbra.generated.adminclient.admin.testDeleteZimletRequest;
import zimbra.generated.adminclient.admin.testDeleteZimletResponse;
import zimbra.generated.adminclient.admin.testDeployZimletRequest;
import zimbra.generated.adminclient.admin.testDeployZimletResponse;
import zimbra.generated.adminclient.admin.testDisableArchiveRequest;
import zimbra.generated.adminclient.admin.testDisableArchiveResponse;
import zimbra.generated.adminclient.admin.testDumpSessionsRequest;
import zimbra.generated.adminclient.admin.testDumpSessionsResponse;
import zimbra.generated.adminclient.admin.testEnableArchiveRequest;
import zimbra.generated.adminclient.admin.testEnableArchiveResponse;
import zimbra.generated.adminclient.admin.testExportAndDeleteItemsRequest;
import zimbra.generated.adminclient.admin.testExportAndDeleteItemsResponse;
import zimbra.generated.adminclient.admin.testExportMailboxRequest;
import zimbra.generated.adminclient.admin.testExportMailboxResponse;
import zimbra.generated.adminclient.admin.testFailoverClusterServiceRequest;
import zimbra.generated.adminclient.admin.testFailoverClusterServiceResponse;
import zimbra.generated.adminclient.admin.testFixCalendarEndTimeRequest;
import zimbra.generated.adminclient.admin.testFixCalendarEndTimeResponse;
import zimbra.generated.adminclient.admin.testFixCalendarPriorityRequest;
import zimbra.generated.adminclient.admin.testFixCalendarPriorityResponse;
import zimbra.generated.adminclient.admin.testFixCalendarTZRequest;
import zimbra.generated.adminclient.admin.testFixCalendarTZResponse;
import zimbra.generated.adminclient.admin.testFlushCacheRequest;
import zimbra.generated.adminclient.admin.testFlushCacheResponse;
import zimbra.generated.adminclient.admin.testGenCSRRequest;
import zimbra.generated.adminclient.admin.testGenCSRResponse;
import zimbra.generated.adminclient.admin.testGetAccountInfoRequest;
import zimbra.generated.adminclient.admin.testGetAccountInfoResponse;
import zimbra.generated.adminclient.admin.testGetAccountLoggersRequest;
import zimbra.generated.adminclient.admin.testGetAccountLoggersResponse;
import zimbra.generated.adminclient.admin.testGetAccountMembershipRequest;
import zimbra.generated.adminclient.admin.testGetAccountMembershipResponse;
import zimbra.generated.adminclient.admin.testGetAccountRequest;
import zimbra.generated.adminclient.admin.testGetAccountResponse;
import zimbra.generated.adminclient.admin.testGetAdminConsoleUICompRequest;
import zimbra.generated.adminclient.admin.testGetAdminConsoleUICompResponse;
import zimbra.generated.adminclient.admin.testGetAdminExtensionZimletsRequest;
import zimbra.generated.adminclient.admin.testGetAdminExtensionZimletsResponse;
import zimbra.generated.adminclient.admin.testGetAdminSavedSearchesRequest;
import zimbra.generated.adminclient.admin.testGetAdminSavedSearchesResponse;
import zimbra.generated.adminclient.admin.testGetAllAccountLoggersRequest;
import zimbra.generated.adminclient.admin.testGetAllAccountLoggersResponse;
import zimbra.generated.adminclient.admin.testGetAllAccountsRequest;
import zimbra.generated.adminclient.admin.testGetAllAccountsResponse;
import zimbra.generated.adminclient.admin.testGetAllAdminAccountsRequest;
import zimbra.generated.adminclient.admin.testGetAllAdminAccountsResponse;
import zimbra.generated.adminclient.admin.testGetAllCalendarResourcesRequest;
import zimbra.generated.adminclient.admin.testGetAllCalendarResourcesResponse;
import zimbra.generated.adminclient.admin.testGetAllConfigRequest;
import zimbra.generated.adminclient.admin.testGetAllConfigResponse;
import zimbra.generated.adminclient.admin.testGetAllCosRequest;
import zimbra.generated.adminclient.admin.testGetAllCosResponse;
import zimbra.generated.adminclient.admin.testGetAllDistributionListsRequest;
import zimbra.generated.adminclient.admin.testGetAllDistributionListsResponse;
import zimbra.generated.adminclient.admin.testGetAllDomainsRequest;
import zimbra.generated.adminclient.admin.testGetAllDomainsResponse;
import zimbra.generated.adminclient.admin.testGetAllEffectiveRightsRequest;
import zimbra.generated.adminclient.admin.testGetAllEffectiveRightsResponse;
import zimbra.generated.adminclient.admin.testGetAllFreeBusyProvidersRequest;
import zimbra.generated.adminclient.admin.testGetAllFreeBusyProvidersResponse;
import zimbra.generated.adminclient.admin.testGetAllLocalesRequest;
import zimbra.generated.adminclient.admin.testGetAllLocalesResponse;
import zimbra.generated.adminclient.admin.testGetAllMailboxesRequest;
import zimbra.generated.adminclient.admin.testGetAllMailboxesResponse;
import zimbra.generated.adminclient.admin.testGetAllRightsRequest;
import zimbra.generated.adminclient.admin.testGetAllRightsResponse;
import zimbra.generated.adminclient.admin.testGetAllServersRequest;
import zimbra.generated.adminclient.admin.testGetAllServersResponse;
import zimbra.generated.adminclient.admin.testGetAllVolumesRequest;
import zimbra.generated.adminclient.admin.testGetAllVolumesResponse;
import zimbra.generated.adminclient.admin.testGetAllXMPPComponentsRequest;
import zimbra.generated.adminclient.admin.testGetAllXMPPComponentsResponse;
import zimbra.generated.adminclient.admin.testGetAllZimletsRequest;
import zimbra.generated.adminclient.admin.testGetAllZimletsResponse;
import zimbra.generated.adminclient.admin.testGetApplianceHSMFSRequest;
import zimbra.generated.adminclient.admin.testGetApplianceHSMFSResponse;
import zimbra.generated.adminclient.admin.testGetAttributeInfoRequest;
import zimbra.generated.adminclient.admin.testGetAttributeInfoResponse;
import zimbra.generated.adminclient.admin.testGetCSRRequest;
import zimbra.generated.adminclient.admin.testGetCSRResponse;
import zimbra.generated.adminclient.admin.testGetCalendarResourceRequest;
import zimbra.generated.adminclient.admin.testGetCalendarResourceResponse;
import zimbra.generated.adminclient.admin.testGetCertRequest;
import zimbra.generated.adminclient.admin.testGetCertResponse;
import zimbra.generated.adminclient.admin.testGetClusterStatusRequest;
import zimbra.generated.adminclient.admin.testGetClusterStatusResponse;
import zimbra.generated.adminclient.admin.testGetConfigRequest;
import zimbra.generated.adminclient.admin.testGetConfigResponse;
import zimbra.generated.adminclient.admin.testGetCosRequest;
import zimbra.generated.adminclient.admin.testGetCosResponse;
import zimbra.generated.adminclient.admin.testGetCreateObjectAttrsRequest;
import zimbra.generated.adminclient.admin.testGetCreateObjectAttrsResponse;
import zimbra.generated.adminclient.admin.testGetCurrentVolumesRequest;
import zimbra.generated.adminclient.admin.testGetCurrentVolumesResponse;
import zimbra.generated.adminclient.admin.testGetDataSourcesRequest;
import zimbra.generated.adminclient.admin.testGetDataSourcesResponse;
import zimbra.generated.adminclient.admin.testGetDelegatedAdminConstraintsRequest;
import zimbra.generated.adminclient.admin.testGetDelegatedAdminConstraintsResponse;
import zimbra.generated.adminclient.admin.testGetDevicesCountRequest;
import zimbra.generated.adminclient.admin.testGetDevicesCountResponse;
import zimbra.generated.adminclient.admin.testGetDevicesCountSinceLastUsedRequest;
import zimbra.generated.adminclient.admin.testGetDevicesCountSinceLastUsedResponse;
import zimbra.generated.adminclient.admin.testGetDevicesCountUsedTodayRequest;
import zimbra.generated.adminclient.admin.testGetDevicesCountUsedTodayResponse;
import zimbra.generated.adminclient.admin.testGetDevicesRequest;
import zimbra.generated.adminclient.admin.testGetDevicesResponse;
import zimbra.generated.adminclient.admin.testGetDistributionListMembershipRequest;
import zimbra.generated.adminclient.admin.testGetDistributionListMembershipResponse;
import zimbra.generated.adminclient.admin.testGetDistributionListRequest;
import zimbra.generated.adminclient.admin.testGetDistributionListResponse;
import zimbra.generated.adminclient.admin.testGetDomainInfoRequest;
import zimbra.generated.adminclient.admin.testGetDomainInfoResponse;
import zimbra.generated.adminclient.admin.testGetDomainRequest;
import zimbra.generated.adminclient.admin.testGetDomainResponse;
import zimbra.generated.adminclient.admin.testGetEffectiveRightsRequest;
import zimbra.generated.adminclient.admin.testGetEffectiveRightsResponse;
import zimbra.generated.adminclient.admin.testGetFreeBusyQueueInfoRequest;
import zimbra.generated.adminclient.admin.testGetFreeBusyQueueInfoResponse;
import zimbra.generated.adminclient.admin.testGetGrantsRequest;
import zimbra.generated.adminclient.admin.testGetGrantsResponse;
import zimbra.generated.adminclient.admin.testGetHsmStatusRequest;
import zimbra.generated.adminclient.admin.testGetHsmStatusResponse;
import zimbra.generated.adminclient.admin.testGetLDAPEntriesRequest;
import zimbra.generated.adminclient.admin.testGetLDAPEntriesResponse;
import zimbra.generated.adminclient.admin.testGetLicenseInfoRequest;
import zimbra.generated.adminclient.admin.testGetLicenseInfoResponse;
import zimbra.generated.adminclient.admin.testGetLicenseRequest;
import zimbra.generated.adminclient.admin.testGetLicenseResponse;
import zimbra.generated.adminclient.admin.testGetLoggerStatsRequest;
import zimbra.generated.adminclient.admin.testGetLoggerStatsResponse;
import zimbra.generated.adminclient.admin.testGetMailQueueInfoRequest;
import zimbra.generated.adminclient.admin.testGetMailQueueInfoResponse;
import zimbra.generated.adminclient.admin.testGetMailQueueRequest;
import zimbra.generated.adminclient.admin.testGetMailQueueResponse;
import zimbra.generated.adminclient.admin.testGetMailboxRequest;
import zimbra.generated.adminclient.admin.testGetMailboxResponse;
import zimbra.generated.adminclient.admin.testGetMailboxStatsRequest;
import zimbra.generated.adminclient.admin.testGetMailboxStatsResponse;
import zimbra.generated.adminclient.admin.testGetMailboxVersionRequest;
import zimbra.generated.adminclient.admin.testGetMailboxVersionResponse;
import zimbra.generated.adminclient.admin.testGetMailboxVolumesRequest;
import zimbra.generated.adminclient.admin.testGetMailboxVolumesResponse;
import zimbra.generated.adminclient.admin.testGetMemcachedClientConfigRequest;
import zimbra.generated.adminclient.admin.testGetMemcachedClientConfigResponse;
import zimbra.generated.adminclient.admin.testGetQuotaUsageRequest;
import zimbra.generated.adminclient.admin.testGetQuotaUsageResponse;
import zimbra.generated.adminclient.admin.testGetRightRequest;
import zimbra.generated.adminclient.admin.testGetRightResponse;
import zimbra.generated.adminclient.admin.testGetRightsDocRequest;
import zimbra.generated.adminclient.admin.testGetRightsDocResponse;
import zimbra.generated.adminclient.admin.testGetSMIMEConfigRequest;
import zimbra.generated.adminclient.admin.testGetSMIMEConfigResponse;
import zimbra.generated.adminclient.admin.testGetServerNIfsRequest;
import zimbra.generated.adminclient.admin.testGetServerNIfsResponse;
import zimbra.generated.adminclient.admin.testGetServerRequest;
import zimbra.generated.adminclient.admin.testGetServerResponse;
import zimbra.generated.adminclient.admin.testGetServerStatsRequest;
import zimbra.generated.adminclient.admin.testGetServerStatsResponse;
import zimbra.generated.adminclient.admin.testGetServiceStatusRequest;
import zimbra.generated.adminclient.admin.testGetServiceStatusResponse;
import zimbra.generated.adminclient.admin.testGetSessionsRequest;
import zimbra.generated.adminclient.admin.testGetSessionsResponse;
import zimbra.generated.adminclient.admin.testGetShareInfoRequest;
import zimbra.generated.adminclient.admin.testGetShareInfoResponse;
import zimbra.generated.adminclient.admin.testGetSystemRetentionPolicyRequest;
import zimbra.generated.adminclient.admin.testGetSystemRetentionPolicyResponse;
import zimbra.generated.adminclient.admin.testGetVersionInfoRequest;
import zimbra.generated.adminclient.admin.testGetVersionInfoResponse;
import zimbra.generated.adminclient.admin.testGetVolumeRequest;
import zimbra.generated.adminclient.admin.testGetVolumeResponse;
import zimbra.generated.adminclient.admin.testGetXMPPComponentRequest;
import zimbra.generated.adminclient.admin.testGetXMPPComponentResponse;
import zimbra.generated.adminclient.admin.testGetXMbxSearchRequest;
import zimbra.generated.adminclient.admin.testGetXMbxSearchResponse;
import zimbra.generated.adminclient.admin.testGetXMbxSearchesListRequest;
import zimbra.generated.adminclient.admin.testGetXMbxSearchesListResponse;
import zimbra.generated.adminclient.admin.testGetZimletRequest;
import zimbra.generated.adminclient.admin.testGetZimletResponse;
import zimbra.generated.adminclient.admin.testGetZimletStatusRequest;
import zimbra.generated.adminclient.admin.testGetZimletStatusResponse;
import zimbra.generated.adminclient.admin.testGrantRightRequest;
import zimbra.generated.adminclient.admin.testGrantRightResponse;
import zimbra.generated.adminclient.admin.testHsmRequest;
import zimbra.generated.adminclient.admin.testHsmResponse;
import zimbra.generated.adminclient.admin.testInstallCertRequest;
import zimbra.generated.adminclient.admin.testInstallCertResponse;
import zimbra.generated.adminclient.admin.testInstallLicenseRequest;
import zimbra.generated.adminclient.admin.testInstallLicenseResponse;
import zimbra.generated.adminclient.admin.testMailQueueActionRequest;
import zimbra.generated.adminclient.admin.testMailQueueActionResponse;
import zimbra.generated.adminclient.admin.testMailQueueFlushRequest;
import zimbra.generated.adminclient.admin.testMailQueueFlushResponse;
import zimbra.generated.adminclient.admin.testMigrateAccountRequest;
import zimbra.generated.adminclient.admin.testMigrateAccountResponse;
import zimbra.generated.adminclient.admin.testModifyAccountRequest;
import zimbra.generated.adminclient.admin.testModifyAccountResponse;
import zimbra.generated.adminclient.admin.testModifyAdminSavedSearchesRequest;
import zimbra.generated.adminclient.admin.testModifyAdminSavedSearchesResponse;
import zimbra.generated.adminclient.admin.testModifyCalendarResourceRequest;
import zimbra.generated.adminclient.admin.testModifyCalendarResourceResponse;
import zimbra.generated.adminclient.admin.testModifyConfigRequest;
import zimbra.generated.adminclient.admin.testModifyConfigResponse;
import zimbra.generated.adminclient.admin.testModifyCosRequest;
import zimbra.generated.adminclient.admin.testModifyCosResponse;
import zimbra.generated.adminclient.admin.testModifyDataSourceRequest;
import zimbra.generated.adminclient.admin.testModifyDataSourceResponse;
import zimbra.generated.adminclient.admin.testModifyDelegatedAdminConstraintsRequest;
import zimbra.generated.adminclient.admin.testModifyDelegatedAdminConstraintsResponse;
import zimbra.generated.adminclient.admin.testModifyDistributionListRequest;
import zimbra.generated.adminclient.admin.testModifyDistributionListResponse;
import zimbra.generated.adminclient.admin.testModifyDomainRequest;
import zimbra.generated.adminclient.admin.testModifyDomainResponse;
import zimbra.generated.adminclient.admin.testModifyLDAPEntryRequest;
import zimbra.generated.adminclient.admin.testModifyLDAPEntryResponse;
import zimbra.generated.adminclient.admin.testModifySMIMEConfigRequest;
import zimbra.generated.adminclient.admin.testModifySMIMEConfigResponse;
import zimbra.generated.adminclient.admin.testModifyServerRequest;
import zimbra.generated.adminclient.admin.testModifyServerResponse;
import zimbra.generated.adminclient.admin.testModifySystemRetentionPolicyRequest;
import zimbra.generated.adminclient.admin.testModifySystemRetentionPolicyResponse;
import zimbra.generated.adminclient.admin.testModifyVolumeRequest;
import zimbra.generated.adminclient.admin.testModifyVolumeResponse;
import zimbra.generated.adminclient.admin.testModifyZimletRequest;
import zimbra.generated.adminclient.admin.testModifyZimletResponse;
import zimbra.generated.adminclient.admin.testMoveBlobsRequest;
import zimbra.generated.adminclient.admin.testMoveBlobsResponse;
import zimbra.generated.adminclient.admin.testMoveMailboxRequest;
import zimbra.generated.adminclient.admin.testMoveMailboxResponse;
import zimbra.generated.adminclient.admin.testNoOpRequest;
import zimbra.generated.adminclient.admin.testNoOpResponse;
import zimbra.generated.adminclient.admin.testPingRequest;
import zimbra.generated.adminclient.admin.testPingResponse;
import zimbra.generated.adminclient.admin.testPurgeAccountCalendarCacheRequest;
import zimbra.generated.adminclient.admin.testPurgeAccountCalendarCacheResponse;
import zimbra.generated.adminclient.admin.testPurgeFreeBusyQueueRequest;
import zimbra.generated.adminclient.admin.testPurgeFreeBusyQueueResponse;
import zimbra.generated.adminclient.admin.testPurgeMessagesRequest;
import zimbra.generated.adminclient.admin.testPurgeMessagesResponse;
import zimbra.generated.adminclient.admin.testPurgeMovedMailboxRequest;
import zimbra.generated.adminclient.admin.testPurgeMovedMailboxResponse;
import zimbra.generated.adminclient.admin.testPushFreeBusyRequest;
import zimbra.generated.adminclient.admin.testPushFreeBusyResponse;
import zimbra.generated.adminclient.admin.testQueryMailboxMoveRequest;
import zimbra.generated.adminclient.admin.testQueryMailboxMoveResponse;
import zimbra.generated.adminclient.admin.testQueryWaitSetRequest;
import zimbra.generated.adminclient.admin.testQueryWaitSetResponse;
import zimbra.generated.adminclient.admin.testReIndexRequest;
import zimbra.generated.adminclient.admin.testReIndexResponse;
import zimbra.generated.adminclient.admin.testRecalculateMailboxCountsRequest;
import zimbra.generated.adminclient.admin.testRecalculateMailboxCountsResponse;
import zimbra.generated.adminclient.admin.testRegisterMailboxMoveOutRequest;
import zimbra.generated.adminclient.admin.testRegisterMailboxMoveOutResponse;
import zimbra.generated.adminclient.admin.testReloadAccountRequest;
import zimbra.generated.adminclient.admin.testReloadAccountResponse;
import zimbra.generated.adminclient.admin.testReloadLocalConfigRequest;
import zimbra.generated.adminclient.admin.testReloadLocalConfigResponse;
import zimbra.generated.adminclient.admin.testReloadMemcachedClientConfigRequest;
import zimbra.generated.adminclient.admin.testReloadMemcachedClientConfigResponse;
import zimbra.generated.adminclient.admin.testRemoveAccountAliasRequest;
import zimbra.generated.adminclient.admin.testRemoveAccountAliasResponse;
import zimbra.generated.adminclient.admin.testRemoveAccountLoggerRequest;
import zimbra.generated.adminclient.admin.testRemoveAccountLoggerResponse;
import zimbra.generated.adminclient.admin.testRemoveDistributionListAliasRequest;
import zimbra.generated.adminclient.admin.testRemoveDistributionListAliasResponse;
import zimbra.generated.adminclient.admin.testRemoveDistributionListMemberRequest;
import zimbra.generated.adminclient.admin.testRemoveDistributionListMemberResponse;
import zimbra.generated.adminclient.admin.testRenameAccountRequest;
import zimbra.generated.adminclient.admin.testRenameAccountResponse;
import zimbra.generated.adminclient.admin.testRenameCalendarResourceRequest;
import zimbra.generated.adminclient.admin.testRenameCalendarResourceResponse;
import zimbra.generated.adminclient.admin.testRenameCosRequest;
import zimbra.generated.adminclient.admin.testRenameCosResponse;
import zimbra.generated.adminclient.admin.testRenameDistributionListRequest;
import zimbra.generated.adminclient.admin.testRenameDistributionListResponse;
import zimbra.generated.adminclient.admin.testRenameLDAPEntryRequest;
import zimbra.generated.adminclient.admin.testRenameLDAPEntryResponse;
import zimbra.generated.adminclient.admin.testResetAllLoggersRequest;
import zimbra.generated.adminclient.admin.testResetAllLoggersResponse;
import zimbra.generated.adminclient.admin.testRestoreRequest;
import zimbra.generated.adminclient.admin.testRestoreResponse;
import zimbra.generated.adminclient.admin.testRevokeRightRequest;
import zimbra.generated.adminclient.admin.testRevokeRightResponse;
import zimbra.generated.adminclient.admin.testRolloverRedoLogRequest;
import zimbra.generated.adminclient.admin.testRolloverRedoLogResponse;
import zimbra.generated.adminclient.admin.testRunUnitTestsRequest;
import zimbra.generated.adminclient.admin.testRunUnitTestsResponse;
import zimbra.generated.adminclient.admin.testScheduleBackupsRequest;
import zimbra.generated.adminclient.admin.testScheduleBackupsResponse;
import zimbra.generated.adminclient.admin.testSearchAccountsRequest;
import zimbra.generated.adminclient.admin.testSearchAccountsResponse;
import zimbra.generated.adminclient.admin.testSearchAutoProvDirectoryRequest;
import zimbra.generated.adminclient.admin.testSearchAutoProvDirectoryResponse;
import zimbra.generated.adminclient.admin.testSearchCalendarResourcesRequest;
import zimbra.generated.adminclient.admin.testSearchCalendarResourcesResponse;
import zimbra.generated.adminclient.admin.testSearchDirectoryRequest;
import zimbra.generated.adminclient.admin.testSearchDirectoryResponse;
import zimbra.generated.adminclient.admin.testSearchGalRequest;
import zimbra.generated.adminclient.admin.testSearchGalResponse;
import zimbra.generated.adminclient.admin.testSearchMultiMailboxRequest;
import zimbra.generated.adminclient.admin.testSearchMultiMailboxResponse;
import zimbra.generated.adminclient.admin.testSetCurrentVolumeRequest;
import zimbra.generated.adminclient.admin.testSetCurrentVolumeResponse;
import zimbra.generated.adminclient.admin.testSetPasswordRequest;
import zimbra.generated.adminclient.admin.testSetPasswordResponse;
import zimbra.generated.adminclient.admin.testSyncGalAccountRequest;
import zimbra.generated.adminclient.admin.testSyncGalAccountResponse;
import zimbra.generated.adminclient.admin.testUndeployZimletRequest;
import zimbra.generated.adminclient.admin.testUndeployZimletResponse;
import zimbra.generated.adminclient.admin.testUnloadMailboxRequest;
import zimbra.generated.adminclient.admin.testUnloadMailboxResponse;
import zimbra.generated.adminclient.admin.testUnregisterMailboxMoveOutRequest;
import zimbra.generated.adminclient.admin.testUnregisterMailboxMoveOutResponse;
import zimbra.generated.adminclient.admin.testUpdateDeviceStatusRequest;
import zimbra.generated.adminclient.admin.testUpdateDeviceStatusResponse;
import zimbra.generated.adminclient.admin.testUploadDomCertRequest;
import zimbra.generated.adminclient.admin.testUploadDomCertResponse;
import zimbra.generated.adminclient.admin.testUploadProxyCARequest;
import zimbra.generated.adminclient.admin.testUploadProxyCAResponse;
import zimbra.generated.adminclient.admin.testVerifyCertKeyRequest;
import zimbra.generated.adminclient.admin.testVerifyCertKeyResponse;
import zimbra.generated.adminclient.admin.testVerifyIndexRequest;
import zimbra.generated.adminclient.admin.testVerifyIndexResponse;
import zimbra.generated.adminclient.admin.testVersionCheckRequest;
import zimbra.generated.adminclient.admin.testVersionCheckResponse;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.1.7-hudson-48-
 * Generated source version: 2.1
 * 
 */
@WebService(name = "AdminService", targetNamespace = "http://www.zimbra.com/wsdl/AdminService.wsdl")
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE)
@XmlSeeAlso({
    zimbra.generated.adminclient.mail.ObjectFactory.class,
    zimbra.generated.adminclient.zm.ObjectFactory.class,
    zimbra.generated.adminclient.admin.ObjectFactory.class,
    zimbra.generated.adminclient.account.ObjectFactory.class
})
public interface AdminService {


    /**
     * 
     * @param parameters
     * @return
     *     returns zimbra.generated.adminclient.admin.testAbortHsmResponse
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
     *     returns zimbra.generated.adminclient.admin.testAbortXMbxSearchResponse
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
     *     returns zimbra.generated.adminclient.admin.testActivateLicenseResponse
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
     *     returns zimbra.generated.adminclient.admin.testAddAccountAliasResponse
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
     *     returns zimbra.generated.adminclient.admin.testAddAccountLoggerResponse
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
     *     returns zimbra.generated.adminclient.admin.testAddDistributionListAliasResponse
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
     *     returns zimbra.generated.adminclient.admin.testAddDistributionListMemberResponse
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
     *     returns zimbra.generated.adminclient.admin.testAddGalSyncDataSourceResponse
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
     *     returns zimbra.generated.adminclient.admin.testAdminCreateWaitSetResponse
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
     *     returns zimbra.generated.adminclient.admin.testAdminDestroyWaitSetResponse
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
     *     returns zimbra.generated.adminclient.admin.testAdminWaitSetResponse
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
     *     returns zimbra.generated.adminclient.admin.testAuthResponse
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
     *     returns zimbra.generated.adminclient.admin.testAutoCompleteGalResponse
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
     *     returns zimbra.generated.adminclient.admin.testAutoProvAccountResponse
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
     *     returns zimbra.generated.adminclient.admin.testBackupAccountQueryResponse
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
     *     returns zimbra.generated.adminclient.admin.testBackupQueryResponse
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
     *     returns zimbra.generated.adminclient.admin.testBackupResponse
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
     *     returns zimbra.generated.adminclient.admin.testCheckAuthConfigResponse
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
     *     returns zimbra.generated.adminclient.admin.testCheckBlobConsistencyResponse
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
     *     returns zimbra.generated.adminclient.admin.testCheckDirectoryResponse
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
     *     returns zimbra.generated.adminclient.admin.testCheckDomainMXRecordResponse
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
     *     returns zimbra.generated.adminclient.admin.testCheckExchangeAuthResponse
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
     *     returns zimbra.generated.adminclient.admin.testCheckGalConfigResponse
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
     *     returns zimbra.generated.adminclient.admin.testCheckHealthResponse
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
     *     returns zimbra.generated.adminclient.admin.testCheckHostnameResolveResponse
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
     *     returns zimbra.generated.adminclient.admin.testCheckPasswordStrengthResponse
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
     *     returns zimbra.generated.adminclient.admin.testCheckRightResponse
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
     *     returns zimbra.generated.adminclient.admin.testConfigureZimletResponse
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
     *     returns zimbra.generated.adminclient.admin.testCopyCosResponse
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
     *     returns zimbra.generated.adminclient.admin.testCountAccountResponse
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
     *     returns zimbra.generated.adminclient.admin.testCreateAccountResponse
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
     *     returns zimbra.generated.adminclient.admin.testCreateArchiveResponse
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
     *     returns zimbra.generated.adminclient.admin.testCreateCalendarResourceResponse
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
     *     returns zimbra.generated.adminclient.admin.testCreateCosResponse
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
     *     returns zimbra.generated.adminclient.admin.testCreateDataSourceResponse
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
     *     returns zimbra.generated.adminclient.admin.testCreateDistributionListResponse
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
     *     returns zimbra.generated.adminclient.admin.testCreateDomainResponse
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
     *     returns zimbra.generated.adminclient.admin.testCreateGalSyncAccountResponse
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
     *     returns zimbra.generated.adminclient.admin.testCreateLDAPEntryResponse
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
     *     returns zimbra.generated.adminclient.admin.testCreateServerResponse
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
     *     returns zimbra.generated.adminclient.admin.testCreateSystemRetentionPolicyResponse
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
     *     returns zimbra.generated.adminclient.admin.testCreateVolumeResponse
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
     *     returns zimbra.generated.adminclient.admin.testCreateXMPPComponentResponse
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
     *     returns zimbra.generated.adminclient.admin.testCreateXMbxSearchResponse
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
     *     returns zimbra.generated.adminclient.admin.testCreateZimletResponse
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
     *     returns zimbra.generated.adminclient.admin.testDelegateAuthResponse
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
     *     returns zimbra.generated.adminclient.admin.testDeleteAccountResponse
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
     *     returns zimbra.generated.adminclient.admin.testDeleteCalendarResourceResponse
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
     *     returns zimbra.generated.adminclient.admin.testDeleteCosResponse
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
     *     returns zimbra.generated.adminclient.admin.testDeleteDataSourceResponse
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
     *     returns zimbra.generated.adminclient.admin.testDeleteDistributionListResponse
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
     *     returns zimbra.generated.adminclient.admin.testDeleteDomainResponse
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
     *     returns zimbra.generated.adminclient.admin.testDeleteGalSyncAccountResponse
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
     *     returns zimbra.generated.adminclient.admin.testDeleteLDAPEntryResponse
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
     *     returns zimbra.generated.adminclient.admin.testDeleteMailboxResponse
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
     *     returns zimbra.generated.adminclient.admin.testDeleteServerResponse
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
     *     returns zimbra.generated.adminclient.admin.testDeleteSystemRetentionPolicyResponse
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
     *     returns zimbra.generated.adminclient.admin.testDeleteVolumeResponse
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
     *     returns zimbra.generated.adminclient.admin.testDeleteXMPPComponentResponse
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
     *     returns zimbra.generated.adminclient.admin.testDeleteXMbxSearchResponse
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
     *     returns zimbra.generated.adminclient.admin.testDeleteZimletResponse
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
     *     returns zimbra.generated.adminclient.admin.testDeployZimletResponse
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
     *     returns zimbra.generated.adminclient.admin.testDisableArchiveResponse
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
     *     returns zimbra.generated.adminclient.admin.testDumpSessionsResponse
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
     *     returns zimbra.generated.adminclient.admin.testEnableArchiveResponse
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
     *     returns zimbra.generated.adminclient.admin.testExportAndDeleteItemsResponse
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
     *     returns zimbra.generated.adminclient.admin.testExportMailboxResponse
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
     *     returns zimbra.generated.adminclient.admin.testFailoverClusterServiceResponse
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
     *     returns zimbra.generated.adminclient.admin.testFixCalendarEndTimeResponse
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
     *     returns zimbra.generated.adminclient.admin.testFixCalendarPriorityResponse
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
     *     returns zimbra.generated.adminclient.admin.testFixCalendarTZResponse
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
     *     returns zimbra.generated.adminclient.admin.testFlushCacheResponse
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
     *     returns zimbra.generated.adminclient.admin.testGenCSRResponse
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
     *     returns zimbra.generated.adminclient.admin.testGetAccountInfoResponse
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
     *     returns zimbra.generated.adminclient.admin.testGetAccountLoggersResponse
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
     *     returns zimbra.generated.adminclient.admin.testGetAccountMembershipResponse
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
     *     returns zimbra.generated.adminclient.admin.testGetAccountResponse
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
     *     returns zimbra.generated.adminclient.admin.testGetAdminConsoleUICompResponse
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
     *     returns zimbra.generated.adminclient.admin.testGetAdminExtensionZimletsResponse
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
     *     returns zimbra.generated.adminclient.admin.testGetAdminSavedSearchesResponse
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
     *     returns zimbra.generated.adminclient.admin.testGetAllAccountLoggersResponse
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
     *     returns zimbra.generated.adminclient.admin.testGetAllAccountsResponse
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
     *     returns zimbra.generated.adminclient.admin.testGetAllAdminAccountsResponse
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
     *     returns zimbra.generated.adminclient.admin.testGetAllCalendarResourcesResponse
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
     *     returns zimbra.generated.adminclient.admin.testGetAllConfigResponse
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
     *     returns zimbra.generated.adminclient.admin.testGetAllCosResponse
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
     *     returns zimbra.generated.adminclient.admin.testGetAllDistributionListsResponse
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
     *     returns zimbra.generated.adminclient.admin.testGetAllDomainsResponse
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
     *     returns zimbra.generated.adminclient.admin.testGetAllEffectiveRightsResponse
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
     *     returns zimbra.generated.adminclient.admin.testGetAllFreeBusyProvidersResponse
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
     *     returns zimbra.generated.adminclient.admin.testGetAllLocalesResponse
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
     *     returns zimbra.generated.adminclient.admin.testGetAllMailboxesResponse
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
     *     returns zimbra.generated.adminclient.admin.testGetAllRightsResponse
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
     *     returns zimbra.generated.adminclient.admin.testGetAllServersResponse
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
     *     returns zimbra.generated.adminclient.admin.testGetAllVolumesResponse
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
     *     returns zimbra.generated.adminclient.admin.testGetAllXMPPComponentsResponse
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
     *     returns zimbra.generated.adminclient.admin.testGetAllZimletsResponse
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
     *     returns zimbra.generated.adminclient.admin.testGetApplianceHSMFSResponse
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
     *     returns zimbra.generated.adminclient.admin.testGetAttributeInfoResponse
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
     *     returns zimbra.generated.adminclient.admin.testGetCSRResponse
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
     *     returns zimbra.generated.adminclient.admin.testGetCalendarResourceResponse
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
     *     returns zimbra.generated.adminclient.admin.testGetCertResponse
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
     *     returns zimbra.generated.adminclient.admin.testGetClusterStatusResponse
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
     *     returns zimbra.generated.adminclient.admin.testGetConfigResponse
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
     *     returns zimbra.generated.adminclient.admin.testGetCosResponse
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
     *     returns zimbra.generated.adminclient.admin.testGetCreateObjectAttrsResponse
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
     *     returns zimbra.generated.adminclient.admin.testGetCurrentVolumesResponse
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
     *     returns zimbra.generated.adminclient.admin.testGetDataSourcesResponse
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
     *     returns zimbra.generated.adminclient.admin.testGetDelegatedAdminConstraintsResponse
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
     *     returns zimbra.generated.adminclient.admin.testGetDevicesCountResponse
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
     *     returns zimbra.generated.adminclient.admin.testGetDevicesCountSinceLastUsedResponse
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
     *     returns zimbra.generated.adminclient.admin.testGetDevicesCountUsedTodayResponse
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
     *     returns zimbra.generated.adminclient.admin.testGetDevicesResponse
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
     *     returns zimbra.generated.adminclient.admin.testGetDistributionListMembershipResponse
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
     *     returns zimbra.generated.adminclient.admin.testGetDistributionListResponse
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
     *     returns zimbra.generated.adminclient.admin.testGetDomainInfoResponse
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
     *     returns zimbra.generated.adminclient.admin.testGetDomainResponse
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
     *     returns zimbra.generated.adminclient.admin.testGetEffectiveRightsResponse
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
     *     returns zimbra.generated.adminclient.admin.testGetFreeBusyQueueInfoResponse
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
     *     returns zimbra.generated.adminclient.admin.testGetGrantsResponse
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
     *     returns zimbra.generated.adminclient.admin.testGetHsmStatusResponse
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
     *     returns zimbra.generated.adminclient.admin.testGetLDAPEntriesResponse
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
     *     returns zimbra.generated.adminclient.admin.testGetLicenseInfoResponse
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
     *     returns zimbra.generated.adminclient.admin.testGetLicenseResponse
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
     *     returns zimbra.generated.adminclient.admin.testGetLoggerStatsResponse
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
     *     returns zimbra.generated.adminclient.admin.testGetMailQueueInfoResponse
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
     *     returns zimbra.generated.adminclient.admin.testGetMailQueueResponse
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
     *     returns zimbra.generated.adminclient.admin.testGetMailboxResponse
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
     *     returns zimbra.generated.adminclient.admin.testGetMailboxStatsResponse
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
     *     returns zimbra.generated.adminclient.admin.testGetMailboxVersionResponse
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
     *     returns zimbra.generated.adminclient.admin.testGetMailboxVolumesResponse
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
     *     returns zimbra.generated.adminclient.admin.testGetMemcachedClientConfigResponse
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
     *     returns zimbra.generated.adminclient.admin.testGetQuotaUsageResponse
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
     *     returns zimbra.generated.adminclient.admin.testGetRightResponse
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
     *     returns zimbra.generated.adminclient.admin.testGetRightsDocResponse
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
     *     returns zimbra.generated.adminclient.admin.testGetSMIMEConfigResponse
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
     *     returns zimbra.generated.adminclient.admin.testGetServerNIfsResponse
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
     *     returns zimbra.generated.adminclient.admin.testGetServerResponse
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
     *     returns zimbra.generated.adminclient.admin.testGetServerStatsResponse
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
     *     returns zimbra.generated.adminclient.admin.testGetServiceStatusResponse
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
     *     returns zimbra.generated.adminclient.admin.testGetSessionsResponse
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
     *     returns zimbra.generated.adminclient.admin.testGetShareInfoResponse
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
     *     returns zimbra.generated.adminclient.admin.testGetSystemRetentionPolicyResponse
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
     *     returns zimbra.generated.adminclient.admin.testGetVersionInfoResponse
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
     *     returns zimbra.generated.adminclient.admin.testGetVolumeResponse
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
     *     returns zimbra.generated.adminclient.admin.testGetXMPPComponentResponse
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
     *     returns zimbra.generated.adminclient.admin.testGetXMbxSearchResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/GetXMbxSearch")
    @WebResult(name = "GetXMbxSearchResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testGetXMbxSearchResponse getXMbxSearchRequest(
        @WebParam(name = "GetXMbxSearchRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testGetXMbxSearchRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns zimbra.generated.adminclient.admin.testGetXMbxSearchesListResponse
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
     *     returns zimbra.generated.adminclient.admin.testGetZimletResponse
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
     *     returns zimbra.generated.adminclient.admin.testGetZimletStatusResponse
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
     *     returns zimbra.generated.adminclient.admin.testGrantRightResponse
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
     *     returns zimbra.generated.adminclient.admin.testHsmResponse
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
     *     returns zimbra.generated.adminclient.admin.testInstallCertResponse
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
     *     returns zimbra.generated.adminclient.admin.testInstallLicenseResponse
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
     *     returns zimbra.generated.adminclient.admin.testMailQueueActionResponse
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
     *     returns zimbra.generated.adminclient.admin.testMailQueueFlushResponse
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
     *     returns zimbra.generated.adminclient.admin.testMigrateAccountResponse
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
     *     returns zimbra.generated.adminclient.admin.testModifyAccountResponse
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
     *     returns zimbra.generated.adminclient.admin.testModifyAdminSavedSearchesResponse
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
     *     returns zimbra.generated.adminclient.admin.testModifyCalendarResourceResponse
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
     *     returns zimbra.generated.adminclient.admin.testModifyConfigResponse
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
     *     returns zimbra.generated.adminclient.admin.testModifyCosResponse
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
     *     returns zimbra.generated.adminclient.admin.testModifyDataSourceResponse
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
     *     returns zimbra.generated.adminclient.admin.testModifyDelegatedAdminConstraintsResponse
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
     *     returns zimbra.generated.adminclient.admin.testModifyDistributionListResponse
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
     *     returns zimbra.generated.adminclient.admin.testModifyDomainResponse
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
     *     returns zimbra.generated.adminclient.admin.testModifyLDAPEntryResponse
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
     *     returns zimbra.generated.adminclient.admin.testModifySMIMEConfigResponse
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
     *     returns zimbra.generated.adminclient.admin.testModifyServerResponse
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
     *     returns zimbra.generated.adminclient.admin.testModifySystemRetentionPolicyResponse
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
     *     returns zimbra.generated.adminclient.admin.testModifyVolumeResponse
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
     *     returns zimbra.generated.adminclient.admin.testModifyZimletResponse
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
     *     returns zimbra.generated.adminclient.admin.testMoveBlobsResponse
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
     *     returns zimbra.generated.adminclient.admin.testMoveMailboxResponse
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
     *     returns zimbra.generated.adminclient.admin.testNoOpResponse
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
     *     returns zimbra.generated.adminclient.admin.testPingResponse
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
     *     returns zimbra.generated.adminclient.admin.testPurgeAccountCalendarCacheResponse
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
     *     returns zimbra.generated.adminclient.admin.testPurgeFreeBusyQueueResponse
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
     *     returns zimbra.generated.adminclient.admin.testPurgeMessagesResponse
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
     *     returns zimbra.generated.adminclient.admin.testPurgeMovedMailboxResponse
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
     *     returns zimbra.generated.adminclient.admin.testPushFreeBusyResponse
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
     *     returns zimbra.generated.adminclient.admin.testQueryMailboxMoveResponse
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
     *     returns zimbra.generated.adminclient.admin.testQueryWaitSetResponse
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
     *     returns zimbra.generated.adminclient.admin.testReIndexResponse
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
     *     returns zimbra.generated.adminclient.admin.testRecalculateMailboxCountsResponse
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
     *     returns zimbra.generated.adminclient.admin.testRegisterMailboxMoveOutResponse
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
     *     returns zimbra.generated.adminclient.admin.testReloadAccountResponse
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
     *     returns zimbra.generated.adminclient.admin.testReloadLocalConfigResponse
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
     *     returns zimbra.generated.adminclient.admin.testReloadMemcachedClientConfigResponse
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
     *     returns zimbra.generated.adminclient.admin.testRemoveAccountAliasResponse
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
     *     returns zimbra.generated.adminclient.admin.testRemoveAccountLoggerResponse
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
     *     returns zimbra.generated.adminclient.admin.testRemoveDistributionListAliasResponse
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
     *     returns zimbra.generated.adminclient.admin.testRemoveDistributionListMemberResponse
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
     *     returns zimbra.generated.adminclient.admin.testRenameAccountResponse
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
     *     returns zimbra.generated.adminclient.admin.testRenameCalendarResourceResponse
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
     *     returns zimbra.generated.adminclient.admin.testRenameCosResponse
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
     *     returns zimbra.generated.adminclient.admin.testRenameDistributionListResponse
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
     *     returns zimbra.generated.adminclient.admin.testRenameLDAPEntryResponse
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
     *     returns zimbra.generated.adminclient.admin.testResetAllLoggersResponse
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
     *     returns zimbra.generated.adminclient.admin.testRestoreResponse
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
     *     returns zimbra.generated.adminclient.admin.testRevokeRightResponse
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
     *     returns zimbra.generated.adminclient.admin.testRolloverRedoLogResponse
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
     *     returns zimbra.generated.adminclient.admin.testRunUnitTestsResponse
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
     *     returns zimbra.generated.adminclient.admin.testScheduleBackupsResponse
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
     *     returns zimbra.generated.adminclient.admin.testSearchAccountsResponse
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
     *     returns zimbra.generated.adminclient.admin.testSearchAutoProvDirectoryResponse
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
     *     returns zimbra.generated.adminclient.admin.testSearchCalendarResourcesResponse
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
     *     returns zimbra.generated.adminclient.admin.testSearchDirectoryResponse
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
     *     returns zimbra.generated.adminclient.admin.testSearchGalResponse
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
     *     returns zimbra.generated.adminclient.admin.testSearchMultiMailboxResponse
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
     *     returns zimbra.generated.adminclient.admin.testSetCurrentVolumeResponse
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
     *     returns zimbra.generated.adminclient.admin.testSetPasswordResponse
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
     *     returns zimbra.generated.adminclient.admin.testSyncGalAccountResponse
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
     *     returns zimbra.generated.adminclient.admin.testUndeployZimletResponse
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
     *     returns zimbra.generated.adminclient.admin.testUnloadMailboxResponse
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
     *     returns zimbra.generated.adminclient.admin.testUnregisterMailboxMoveOutResponse
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
     *     returns zimbra.generated.adminclient.admin.testUpdateDeviceStatusResponse
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
     *     returns zimbra.generated.adminclient.admin.testUploadDomCertResponse
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
     *     returns zimbra.generated.adminclient.admin.testUploadProxyCAResponse
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
     *     returns zimbra.generated.adminclient.admin.testVerifyCertKeyResponse
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
     *     returns zimbra.generated.adminclient.admin.testVerifyIndexResponse
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
     *     returns zimbra.generated.adminclient.admin.testVersionCheckResponse
     */
    @WebMethod(action = "urn:zimbraAdmin/VersionCheck")
    @WebResult(name = "VersionCheckResponse", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
    public testVersionCheckResponse versionCheckRequest(
        @WebParam(name = "VersionCheckRequest", targetNamespace = "urn:zimbraAdmin", partName = "parameters")
        testVersionCheckRequest parameters);

}
