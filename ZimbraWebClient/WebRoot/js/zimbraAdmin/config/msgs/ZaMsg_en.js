/*
 * ***** BEGIN LICENSE BLOCK *****
 * Version: ZPL 1.1
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.1 ("License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.zimbra.com/license
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
 * the License for the specific language governing rights and limitations
 * under the License.
 * 
 * The Original Code is: Zimbra Collaboration Suite.
 * 
 * The Initial Developer of the Original Code is Zimbra, Inc.
 * Portions created by Zimbra are Copyright (C) 2005 Zimbra, Inc.
 * All Rights Reserved.
 * 
 * Contributor(s):
 * 
 * ***** END LICENSE BLOCK *****
 */

function ZaMsg() {
}

ZaMsg.STBM_SEARCHFOR_Account = "Account";
ZaMsg.Of = "of";
ZaMsg.Unlimited = "unlimited";

//Domain configuration labels 
ZaMsg.AuthMech_zimbra="Internal";
ZaMsg.AuthMech_ldap = "External LDAP";
ZaMsg.AuthMech_ad = "External Active Directory";

ZaMsg.GALMode_internal="Internal";
ZaMsg.GALMode_external="External";
ZaMsg.GALMode_both="Both";

ZaMsg.GALServerType_ldap="LDAP";
ZaMsg.GALServerType_ad="Active Directory";

//tab titles
ZaMsg.Domain_Tab_General = "General";
ZaMsg.Domain_Tab_GAL = "GAL";
ZaMsg.Domain_Tab_Authentication = "Authentication";

ZaMsg.Domain_Auth_Config_Complete = "Configuration of authentication mechanism is complete.";
ZaMsg.Domain_Auth_ConfigSummary = "Summary of authentication settings:";
ZaMsg.Domain_AuthProvideLoginPwd = "Please provide username and password to test the authentication settings";
ZaMsg.Domain_AuthTestUserName = "User name";
ZaMsg.Domain_AuthTestPassword = "Password";
ZaMsg.Domain_AuthTestSettings = "Test";
ZaMsg.Domain_AuthTestingInProgress = "Trying to authenticate using the new settings";
ZaMsg.Domain_AuthTestResults = "Test results";
ZaMsg.Domain_AuthTestSuccessful = "Authentication test successful";
ZaMsg.Domain_AuthTestFailed = "Authentication test failed";
ZaMsg.Domain_AuthTestResultCode = "Server message";
ZaMsg.Domain_AuthTestMessage = "Details";
ZaMsg.Domain_AuthComputedBindDn = "Computed bind DN used in the test";

ZaMsg.AuthTest_check_OK = "Test Successful";
ZaMsg.AuthTest_check_UNKNOWN_HOST = "Unable to resolve a hostname";
ZaMsg.AuthTest_check_CONNECTION_REFUSED = "Connection to a port was refused";
ZaMsg.AuthTest_check_SSL_HANDSHAKE_FAILURE = "SSL connect problem, most likely untrusted certificate";
ZaMsg.AuthTest_check_COMMUNICATION_FAILURE = "Generic communication failure";
ZaMsg.AuthTest_check_AUTH_FAILED = "Authentication failed. Invalid credentials (bad dn/password)";
ZaMsg.AuthTest_check_AUTH_NOT_SUPPORTED = "Authentication flavor not supported. LDAP server probably configured to not allow passwords.";
ZaMsg.AuthTest_check_NAME_NOT_FOUND = "Unable to resolve an LDAP name. Most likely invalid search base";
ZaMsg.AuthTest_check_INVALID_SEARCH_FILTER = "Invalid ldap search filter";
ZaMsg.AuthTest_check_FAILURE = "Generic failure";

ZaMsg.Domain_GAL_Config_Complete = "GAL search configuration complete.";
ZaMsg.Domain_GAL_ConfigSummary = "Summary of GAL search settings";
ZaMsg.Domain_GALTestSettings = "Test";
ZaMsg.Domain_GALTestingInProgress = "Please wait while the server is testing new GAL search settings...";
ZaMsg.Domain_GALTestResults = "Test result";
ZaMsg.Domain_GALSearchResult = "Search result";
ZaMsg.Domain_GALTestSuccessful = "Test Successful";
ZaMsg.Domain_GALTestFailed = "GAL search test failed";
ZaMsg.Domain_GALTestResult = "Test result";
ZaMsg.Domain_GALTestMessage = "Details";
ZaMsg.Domain_GALSampleSearchName = "Please provide a search term";  

ZaMsg.Domain_Config_Complete = "Configuration of the new domain is complete.<br>Please press 'FINISH' to save the new domain.";
ZaMsg.Domain_DomainName = "Domain name:";
ZaMsg.Domain_GalMode = "GAL mode:";
ZaMsg.Domain_GALServerType = "Server type:";
ZaMsg.Domain_GALServerName = "External server name:";
ZaMsg.Domain_GALServerPort = "External server port:";
ZaMsg.Domain_GALUseSSL = "Use SSL:";
ZaMsg.Domain_UseBindPassword = "Use DN/Password to bind to external server:";
ZaMsg.Domain_GalLdapURL = "LDAP URL";
ZaMsg.Domain_GalLdapSearchBase = "LDAP search base:";
ZaMsg.Domain_GalLdapBindDn = "Bind DN:";
ZaMsg.Domain_GalLdapBindPassword = "Bind password:";
ZaMsg.Domain_GalLdapBindPasswordConfirm = "Confirm bind password:";
ZaMsg.Domain_GalLdapFilter = "LDAP filter:";
ZaMsg.Domain_AuthMech = "Authentication mechanism:";
ZaMsg.Domain_AuthLdapURL = "LDAP URL";
ZaMsg.Domain_AuthLdapUserDn = "LDAP bind DN template:";
ZaMsg.Domain_AuthADServerName = "Active Directory server name:";
ZaMsg.Domain_AuthADDomainName = "Active Directory domain name:";
ZaMsg.Domain_AuthADServerPort = "Active Directory server port";
ZaMsg.Domain_AuthADUseSSL = "Use SSL to connect to Active Directory server:";
ZaMsg.Domain_AuthLDAPServerName = "LDAP server name:";
ZaMsg.Domain_AuthLDAPSearchBase = "LDAP search base:";
ZaMsg.Domain_AuthLDAPServerPort = "LDAP server port:";
ZaMsg.Domain_AuthLDAPUseSSL = "Use SSL to connect to LDAP server:";

ZaMsg.Restore_SelectPath = "Please provide the path to the backup targets.";
ZaMsg.Restore_AccountName = "Please provide the email address of the account that you want to restore.";
ZaMsg.Restore_EmailAddress = "Email Address";
ZaMsg.Restore_SelectServer = "Please select the server for the restored acount.";
ZaMsg.Restore_OriginalServer = "This account was hosted by the server:";
ZaMsg.Restore_TargetServer = "Target server:";
ZaMsg.Restore_TargetPath = "Path to the backup target:";
ZaMsg.Restore_Prefix = "Prefix for the new account name:";
ZaMsg.Restore_method= "Restore method:";
ZaMsg.Restore_SelectLabel = "Please select the backup label to restore from.";
ZaMsg.Restore_Label = "Backup label to restore from:";
ZaMsg.Restore_Restore = "Restore";
ZaMsg.Restore_NoLabelsFound = "Could not find any backup labels in the specified target";
ZaMsg.Restore_LabelsProblem = "Server encountered a problem when looking for backup labels";
ZaMsg.Restore_IncludeIncrementals = "Include incremental backups";
ZaMsg.Restore_LookingForLabels = "Looking for labels";
ZaMsg.Restore_Restoring = "Restoring";
ZaMsg.Restore_RestoreSuccess = "Mailbox succesfully restored.";

ZaMsg.NoAliases = "This account does not have any aliases";
ZaMsg.NoFwd = "No forwarding defined for this account";
ZaMsg.Forward = "Forward";
ZaMsg.NextPage_tt = "Go to next page";
ZaMsg.PrevPage_tt = "Go to previous page"
ZaMsg.Back = "Back";
ZaMsg.appExitWarning = "Doing so will terminate ZimbraAdmin";
// overview panel status
ZaMsg.OVP_accounts = "Accounts";
ZaMsg.OVP_cos = "Class of Service";
ZaMsg.OVP_domains = "Domains";
ZaMsg.OVP_global = "Global Settings";
ZaMsg.OVP_servers = "Servers";
ZaMsg.OVP_status = "Status";
ZaMsg.OVP_statistics = "Statistics";

ZaMsg.LST_ClickToSort_tt = "Sort by ";

ZaMsg.NAD_GlobalStatistics = "System-wide Information";
ZaMsg.NAD_ServerStatistics = "Server:";
ZaMsg.NAD_bytes = "bytes";
ZaMsg.TBB_New = "New";

ZaMsg.TBB_Close = "Close";
ZaMsg.TBB_Save = "Save";
ZaMsg.TBB_Edit = "Edit";
ZaMsg.TBB_EditAliases = "Edit Aliases";
ZaMsg.TBB_Delete = "Delete";
ZaMsg.TBB_Duplicate = "Duplicate";
ZaMsg.TBB_Refresh = "Refresh";
ZaMsg.TBB_ChngPwd = "Change Password";
ZaMsg.TBB_ViewMail = "View Mail";
ZaMsg.TBB_RestoreMailbox = "Restore";
ZaMsg.TBB_Refresh_tt = "Refresh list";

ZaMsg.ALTBB_Save_tt = "Save changes";
ZaMsg.ALTBB_Close_tt = "Close this view";
ZaMsg.ALTBB_New_tt = "New account";
ZaMsg.ALTBB_Edit_tt = "Edit account";
ZaMsg.ALTBB_Edit_Aliases_tt = "Edit account aliases";
ZaMsg.ALTBB_Delete_tt = "Delete account";
ZaMsg.ACTBB_New_tt = "New account";
ZaMsg.ACTBB_Edit_tt = "Edit account"
ZaMsg.ACTBB_Delete_tt = "Delete account";
ZaMsg.ACTBB_ChngPwd_tt = "Change user's password";
ZaMsg.ACTBB_ViewMail_tt = "Login to email application on behalf of this account";
ZaMsg.ACTBB_Restore_tt = "Restore an account";

ZaMsg.DTBB_New_tt = "New domain";
ZaMsg.DTBB_Edit_tt = "Edit domain";
ZaMsg.DTBB_Delete_tt = "Delete domain";
ZaMsg.DTBB_Save_tt = "Save changes";
ZaMsg.DTBB_Close_tt = "Close this view";
ZaMsg.DTBB_GAlConfigWiz = "Configure GAL";
ZaMsg.DTBB_GAlConfigWiz_tt = "Open Global Address List Configuration Wizard";
ZaMsg.DTBB_AuthConfigWiz = "Configure Authentication";
ZaMsg.DTBB_AuthConfigWiz_tt = "Open Authentication Mechanism Configuration Wizard";

ZaMsg.COSTBB_New_tt = "New COS";
ZaMsg.COSTBB_Edit_tt = "Edit COS";
ZaMsg.COSTBB_Delete_tt = "Delete COS";
ZaMsg.COSTBB_Duplicate_tt = "Duplicate COS";
ZaMsg.COSTBB_Save_tt = "Save changes";
ZaMsg.COSTBB_Close_tt = "Close this view";

ZaMsg.Alert_ServerDetails = 
	"<b>Note:</b> Settings on an individual server override global settings.";
ZaMsg.Alert_GlobalConfig = 
	"<b>Note:</b> "+
	"Settings only apply to servers that have the appropriate service(s) "+
	"installed and enabled. Server settings override global settings.";
ZaMsg.Alert_ServerRestart = 
	"<b>Note:</b> "+
	"Changes to settings requires server restart in order to take effect.";	

ZaMsg.SERTBB_New_tt = "New server";
ZaMsg.SERTBB_Edit_tt = "Edit server";
ZaMsg.SERTBB_Delete_tt = "Delete server";
ZaMsg.SERTBB_Save_tt = "Save changes";
ZaMsg.SERTBB_Close_tt = "Close this view";

ZaMsg.ALV_Name_col = "EMail Address";
ZaMsg.ALV_FullName_col = "Full Name";
ZaMsg.ALV_DspName_col = "Display Name";
ZaMsg.ALV_Status_col = "Status";
ZaMsg.ALV_Description_col =  "Description";

ZaMsg.CLV_Name_col = "Name";
ZaMsg.CLV_Description_col =  "Description";

ZaMsg.DLV_Name_col = "Name";
ZaMsg.DLV_Description_col =  "Description";

ZaMsg.SLV_Name_col = "Name";
ZaMsg.SLV_ServiceHName_col =  "Service host name:";
ZaMsg.SLV_Description_col =  "Description";

ZaMsg.STV_Server_col = "Server";
ZaMsg.STV_Service_col = "Service";
ZaMsg.STV_Time_col = "Time";
ZaMsg.STV_Status_col = "Status";

ZaMsg.NAD_Tab_General= "General";
ZaMsg.NAD_Tab_Attachments = "Attachments";
ZaMsg.NAD_Tab_MTA = "MTA";
ZaMsg.NAD_Tab_POP = "POP";
ZaMsg.NAD_Tab_IMAP = "IMAP";
ZaMsg.NAD_Tab_AntiSpam = "Anti-Spam";
ZaMsg.NAD_Tab_AntiVirus = "Anti-Virus";
ZaMsg.NAD_Tab_General = "General";
ZaMsg.NAD_Tab_Services = "Services";

ZaMsg.NAD_Service_EnabledServices = "Enabled services:";
ZaMsg.NAD_Service_LDAP = "LDAP";
ZaMsg.NAD_Service_Mailbox = "Mailbox";
ZaMsg.NAD_Service_MTA = "MTA";
ZaMsg.NAD_Service_SNMP = "SNMP";
ZaMsg.NAD_Service_AntiVirus = "Anti-Virus";
ZaMsg.NAD_Service_AntiSpam = "Anti-Spam";

ZaMsg.NAD_Dialog_ShutdownEmailService = "You are about to shutdown the Email service on this server!";
ZaMsg.NAD_Dialog_SaveChanges = "Do you want so save current changes?";

// REVISIT: Account, New Account, and COS forms should be re-organized in
//			order to avoid duplicating these two labels.
ZaMsg.NAD_RemoveAllAttachments = "Disable attachment viewing from web mail UI";
ZaMsg.NAD_AttachmentsViewInHtmlOnly = "Convert attachments to HTML for viewing";

ZaMsg.NAD_Attach_IncomingAttachments = "Attachment settings:";
ZaMsg.NAD_Attach_RemoveAllAttachments = "Disable attachment viewing from web mail UI";
ZaMsg.NAD_Attach_ViewInHtml = "Convert attachments to HTML for viewing";
ZaMsg.NAD_Attach_RemoveAttachmentsByExt = "Reject messages with attachment extensions";
ZaMsg.NAD_Attach_NewExtension = "New extension:";
ZaMsg.NAD_Attach_AddExtension = "Add";

ZaMsg.NAD_MTA_Authentication = "Authentication:";
ZaMsg.NAD_MTA_AuthenticationEnabled = "Enabled";
ZaMsg.NAD_MTA_TlsAuthenticationOnly = "TLS authentication only";
ZaMsg.NAD_MTA_WebMailHostname = "Web mail MTA:";
ZaMsg.NAD_MTA_WebMailPort = "Port:";
ZaMsg.NAD_MTA_WebMailTimeout = "Web mail MTA timeout (s):";
ZaMsg.NAD_MTA_RelayHostname = "Relay MTA for external delivery:";
ZaMsg.NAD_MTA_MaxMsgSize = "Maximum message size (kb):";
ZaMsg.NAD_MTA_Options = "Options:";
ZaMsg.NAD_MTA_DnsLookups = "DNS lookups enabled";
ZaMsg.NAD_MTA_ProtocolChecks = "Protocol checks:";
ZaMsg.NAD_MTA_reject_invalid_hostname = "Hostname in greeting violates RFC (reject_invalid_hostname)";
ZaMsg.NAD_MTA_reject_non_fqdn_hostname = "Client must greet with a fully qualified hostname (reject_non_fqdn_hostname)";
ZaMsg.NAD_MTA_reject_non_fqdn_sender = "Sender address must be fully qualified (reject_non_fqdn_sender)";
ZaMsg.NAD_MTA_DnsChecks = "DNS checks:";
ZaMsg.NAD_MTA_reject_unknown_client = "Client's IP address (reject_unknown_client)";
ZaMsg.NAD_MTA_reject_unknown_hostname = "Hostname in greeting (reject_unknown_hostname)";
ZaMsg.NAD_MTA_reject_unknown_sender_domain = "Sender's domain (reject_unknown_sender_domain)";

ZaMsg.NAD_Spam_Checking = "Spam checking:";
ZaMsg.NAD_Spam_CheckingEnabled = "Enabled";
ZaMsg.NAD_Spam_KillPercent = "Kill percent:";
ZaMsg.NAD_Spam_TagPercent = "Tag percent:";
ZaMsg.NAD_Spam_SubjectPrefix = "Subject prefix:";

ZaMsg.NAD_Virus_Checking = "Virus checking:";
ZaMsg.NAD_Virus_CheckingEnabled = "Enabled";
ZaMsg.NAD_Virus_DefUpdateFreq = "Definition update frequency (hrs):";
ZaMsg.NAD_Virus_Options = "Options:";
ZaMsg.NAD_Virus_BlockEncrypted = "Block encrypted archives";
ZaMsg.NAD_Virus_NotifyAdmin = "Send notification to administrator";
ZaMsg.NAD_Virus_NotifyRecipient = "Send notification to recipient";

ZaMsg.NAD_PrefContactsPerPage = "Contacts per page";
ZaMsg.NAD_AuthTokenLifetime = "Session token lifetime";
ZaMsg.NAD_AdminAuthTokenLifetime = "Admin Session Token Lifetime";
ZaMsg.NAD_MailMessageLifetime = "E-mail message lifetime";
ZaMsg.NAD_MailTrashLifetime = "Trashed message lifetime";
ZaMsg.NAD_MailSpamLifetime = "Spam message lifetime";
ZaMsg.NAD_Title = "Create New Account";
ZaMsg.NAD_AccountName = "Account name";
ZaMsg.NAD_Account = "Account";
ZaMsg.NAD_FirstName = "First name";
ZaMsg.NAD_LastName = "Last name";
ZaMsg.NAD_DisplayName = "Display name";
ZaMsg.NAD_Initials = "Middle initial";
ZaMsg.NAD_IsAdmin = "Administrator account";
ZaMsg.NAD_MustChangePwd = "Must change password";
ZaMsg.NAD_Password = "Password";
ZaMsg.NAD_Notes = "Notes";
ZaMsg.NAD_MailQuota = "Account quota (mb)";
ZaMsg.NAD_MailBoxSize = "Mail Box Size";
ZaMsg.NAD_ContactMaxNumEntries = "Address book size limit";
ZaMsg.NAD_AccountStatus = "Account status";
ZaMsg.NAD_ConfirmPassword = "Confirm password";
ZaMsg.NAD_ClassOfService = "Class of service";
ZaMsg.NAD_MailServer = "Mail Server";
ZaMsg.NAD_passMinLength="Minimum password length";
ZaMsg.NAD_passMaxLength="Maximum password length";
ZaMsg.NAD_passMinAge="Minimum password age";
ZaMsg.NAD_passMaxAge="Maximum password age";
ZaMsg.NAD_passEnforceHistory="Enforce password history";
ZaMsg.NAD_prefMailSignature="Mail signature";
ZaMsg.NAD_prefMailSignatureEnabled="Enable mail signature";
ZaMsg.NAD_prefSaveToSent="Save to sent";
ZaMsg.NAD_telephoneNumber="Phone";
ZaMsg.NAD_company = "Company";
ZaMsg.NAD_city ="City";
ZaMsg.NAD_zip ="Postal code";
ZaMsg.NAD_state ="State";
ZaMsg.NAD_country ="Country";
ZaMsg.NAD_office = "Office";
ZaMsg.NAD_orgUnit="Department";
ZaMsg.NAD_postalAddress ="Address";
ZaMsg.NAD_Description = "Description";

ZaMsg.NAD_Domain = "Domain";
ZaMsg.NAD_Aliases = "Aliases";
ZaMsg.NAD_Add = "Add";
ZaMsg.NAD_Remove = "Remove";
ZaMsg.NAD_ForwardTo = "Forward mail to";
ZaMsg.NAD_COSName = "COS Name";
ZaMsg.NAD_PwdLocked = "Password locked";
ZaMsg.NAD_ResetToCOS = "Enforce to COS value";
ZaMsg.NAD_OverrideCOS = "Override COS";
ZaMsg.NAD_ZimbraID = "ID:";
ZaMsg.NAD_new = "new";
ZaMsg.NAD_GalMaxResults = "Most results returned by GAL search:";

ZaMsg.NAD_StatsDataLastDay = "24 hour window";
ZaMsg.NAD_StatsDataLast3Months = "3 months window";
ZaMsg.NAD_StatsDataLast12Months = "12 months window";
ZaMsg.NAD_DefaultDomainName = "Default domain:";
ZaMsg.NAD_MonitorHostServer = "Monitor host server:";

ZaMsg.TABT_StatsDataLastDay = "24 Hours";
ZaMsg.TABT_StatsDataLast3Months = "3 Months";
ZaMsg.TABT_StatsDataLast12Months = "12 Months";

//ZaMsg.NAD_StatsMsgsLastDay = "Messages in last day";
//ZaMsg.NAD_StatsMsgsLast3Months = "Messages in last 3 months";
//ZaMsg.NAD_StatsMsgsLast12Months = "Messages in last 12 months";
ZaMsg.NAD_StatsMsgsLastDay = "24 hour window";
ZaMsg.NAD_StatsMsgsLast3Months = "3 months window";
ZaMsg.NAD_StatsMsgsLast12Months = "12 months window";
ZaMsg.NAD_LastLogonTimestampFrequency = "Last Logon Timestamp Frequency";

ZaMsg.NAD_ServiceConfiguredRole = "Configured Role";
ZaMsg.NAD_ServiceCurrentRole = "Current Role";
ZaMsg.NAD_ServiceHostname = "Service Host Name";
ZaMsg.NAD_Server = "Server: ";
//lmtp
ZaMsg.NAD_LmtpAdvertisedName = "LMTP advertised name:";
ZaMsg.NAD_LmtpBindAddress = "LMTP bind address:";
ZaMsg.NAD_LmtpBindPort = "LMTP bind port:";
ZaMsg.NAD_LmtpNumThreads = "LMTP number of threads:";
//pop3
ZaMsg.NAD_POP_Service = "POP3 service:";
ZaMsg.NAD_POP_Enabled = "Enabled";
ZaMsg.NAD_POP_Address = "Address:";
ZaMsg.NAD_POP_Port = "Port:";
ZaMsg.NAD_POP_SSL = "SSL for POP3 service:";
ZaMsg.NAD_POP_Options = "Options:";
ZaMsg.NAD_POP_CleartextLoginEnabled = "Enable clear text login";
ZaMsg.NAD_POP_AdvertisedName ="Advertised name:";
ZaMsg.NAD_POP_NumThreads = "Number of threads:";
//imap
ZaMsg.NAD_IMAP_Service="IMAP service:";
ZaMsg.NAD_IMAP_Enabled="Enabled";
ZaMsg.NAD_IMAP_Port="Port:";
ZaMsg.NAD_IMAP_SSLService="SSL for IMAP service:";
ZaMsg.NAD_IMAP_Options = "Options:";
ZaMsg.NAD_IMAP_CleartextLoginEnabled="Enable clear text login";

ZaMsg.NAD_MS = "ms"; //milliseconds
ZaMsg.NAD_Sec = "seconds"; //milliseconds

ZaMsg.NAD_RedologFsyncIntervalMS = "FSync interval for redo log";
ZaMsg.NAD_UIFeatures="UI Features:";

//Features
ZaMsg.NAD_zimbraFeatureHtmlComposeEnabled = "HTML compose";
ZaMsg.NAD_FeatureGalEnabled = "GAL access"
ZaMsg.NAD_FeatureContactsEnabled="Contacts";
ZaMsg.NAD_FeatureCalendarEnabled="Calendar";
ZaMsg.NAD_FeatureTaggingEnabled="Tagging";
ZaMsg.NAD_FeatureAdvancedSearchEnabled="Advanced search";
ZaMsg.NAD_FeatureSavedSearchesEnabled="Saved searches";
ZaMsg.NAD_FeatureConversationsEnabled="Conversations";
ZaMsg.NAD_FeatureChangePasswordEnabled="Change password";
ZaMsg.NAD_FeatureInitialSearchPreferenceEnabled="Initial search preference";
ZaMsg.NAD_FeatureFiltersEnabled="Filters"
ZaMsg.NAD_zimbraAttachmentsIndexingEnabled = "Attachment indexing";
ZaMsg.NAD_zimbraImapEnabled = "IMAP access";
ZaMsg.NAD_zimbraPop3Enabled = "POP3 access";
ZaMsg.NAD_zimbraPrefShowFragments = "Show fragments";
ZaMsg.NAD_zimbraPrefReplyIncludeOriginalText = "When replying, include original text";
ZaMsg.NAD_zimbraPrefForwardIncludeOriginalText = "When forwarding, include original text";
ZaMsg.NAD_zimbraPrefComposeInNewWindow = "Always compose in new window";
ZaMsg.NAD_zimbraPrefForwardReplyInOriginalFormat = "Reply/forward using format of the original message";
ZaMsg.NAD_zimbraPrefAutoAddAddressEnabled = "Enable automatic adding of contacts";
ZaMsg.NAD_zimbraPrefMailItemsPerPage = "Number of items to display per page";
ZaMsg.NAD_zimbraPrefComposeFormat = "Always compose mail using";
ZaMsg.NAD_zimbraPrefGroupMailBy = "Group mail by";
ZaMsg.NAD_zimbraPrefMessageViewHtmlPreferred = "View mail as HTML (when possible)"
ZaMsg.NAD_zimbraPrefNewMailNotificationEnabled = "Enable address for new mail notifications";
ZaMsg.NAD_zimbraPrefNewMailNotificationAddress = "Address for new mail notifications";
ZaMsg.NAD_zimbraPrefOutOfOfficeReplyEnabled = "Away message enabled";
ZaMsg.NAD_zimbraPrefOutOfOfficeReply = "Away message";
ZaMsg.NAD_zimbraPrefMailInitialSearch="Initial mail search";
ZaMsg.NAD_zimbraPrefShowSearchString = "Show search string"
ZaMsg.NAD_zimbraPrefMailSignatureStyle = "Signature style";
ZaMsg.NAD_zimbraPrefUseTimeZoneListInCalendar = "Show timezone list in appointment view";
ZaMsg.NAD_zimbraPrefImapSearchFoldersEnabled = "Show IMAP search folders";

ZaMsg.NAD_UserServicesEnabled = "Email Service";
ZaMsg.NAD_Enabled = "Enabled";
ZaMsg.NAD_Disabled = "Disabled";
ZaMsg.NAD_Enable = "Enable";
ZaMsg.NAD_Disable = "Disable";
ZaMsg.NAD_Auto = "auto";
ZaMsg.NAD_DomainsAuthStr = "expansions for bind DN string:<br>%n = username with @ (or without, if no @ was specified)<br>%u = username with @ removed<br>%d = domain as foo.com<br>%D = domain as dc=foo,dc=com";
ZaMsg.NAD_ZERO_UNLIMETED = "(Note: Use \"0\" to specify \"unlimited\" value)";

ZaMsg.TABT_GeneralPage = "General Information";
ZaMsg.TABT_ContactInfo="Contact Information";
ZaMsg.TABT_Aliases = "Aliases";
ZaMsg.TABT_Forwarding =	"Forwarding";
ZaMsg.TABT_Preferences = "Preferences";
ZaMsg.TABT_Features = "Features";
ZaMsg.TABT_Advanced = "Advanced";
ZaMsg.TABT_InData="Inbound Message Volume";
ZaMsg.TABT_InMsgs="Inbound Message Count";
ZaMsg.TABT_Disk="Disk Usage";
ZaMsg.TABT_ServerPool="Server Pool";
ZaMsg.TABT_DomainConfigComplete="Domain Configuration Complete";
ZaMsg.NAD_AccountAliases = "Define email aliases for the new account. Use '+' / '-' buttons to add/remove aliases.";
ZaMsg.NAD_AccountForwarding = "Forward a copy of email to these addresses. Use '+' / '-' buttons to add/remove addresses.";

ZaMsg.TABT_GALMode = "Global Address List (GAL) Mode";
ZaMsg.TABT_GALonfiguration = "GAL Settings";
ZaMsg.TABT_GALonfigSummary = "GAL Settings Summary";
ZaMsg.TABT_TestGalConfig = "Testing GAL Settings";
ZaMsg.TABT_GalTestResult = "GAL Test Result";
ZaMsg.TABT_AuthMode = "Authentication Mode";
ZaMsg.TABT_AuthSettings = "Authentication Settings";
ZaMsg.TABT_AuthSettingsSummary = "Authentication Settings Summary";
ZaMsg.TABT_TestAuthSettings = "Testing Authentication Settings";
ZaMsg.TABT_AuthTestResult = "Authentication Test Result";

ZaMsg.ACCOUNT_STATUS_ACTIVE = "Active";
ZaMsg.ACCOUNT_STATUS_MAINTENANCE = "Maintanance";
ZaMsg.ACCOUNT_STATUS_LOCKED = "Locked";
ZaMsg.ACCOUNT_STATUS_CLOSED = "Closed";

ZaMsg.NCD_NewAccTitle = "New Account";
ZaMsg.NCD_Title = "Create New Class Of Service";
ZaMsg.NCD_Name = "Name";
ZaMsg.NCD_Description = "Description";
ZaMsg.NCD_MailQuota = "Mail Quota in MB";

ZaMsg.NDD_Title = "Create New Domain";
ZaMsg.NDD_Name = "Name";
ZaMsg.NDD_Description = "Description";

ZaMsg.CHNP_Title = "Change Password";

ZaMsg.NCD_AuthConfigTitle = "Authentication Configuration Wizard";
ZaMsg.NCD_GALConfigTitle = "GAL Configuration Wizard";
ZaMsg.Restore_WizTitle = "Mailbox Restore Wizard";

ZaMsg.ERROR_EMAIL_ADDR_REQUIRED = "Email address is required in order to restore a mailbox!";
ZaMsg.ERROR_SESSION_EXPIRED = "Your Session Has Expired";
ZaMsg.SERVER_ERROR = "Server error encountered";
ZaMsg.SOAP_ERROR = "SOAP error encountered";
ZaMsg.NETWORK_ERROR = "Network error encountered";
ZaMsg.PARSE_ERROR = "Parse error encountered";
ZaMsg.PERMISSION_DENIED = "Error: permission denied"
ZaMsg.JAVASCRIPT_ERROR = "JavaScript error encountered";
ZaMsg.ERROR_AUTH_FAILED = "Authentication Failed";
ZaMsg.ERROR_AUTH_NO_ADMIN_RIGHTS = "User does not have administrator rights";
ZaMsg.ERROR_INVALID_VALUE = "Invalid Value";
ZaMsg.ERROR_MAX_MIN_PWDLENGTH = "Value of \"Minimum Password Length\" cannot be greater than value of \"Maximum Password Length\"";
ZaMsg.ERROR_MAX_MIN_PWDAGE = "Value of \"Minimum Password Age\" cannot be greater than value of \"Maximum Password Age\"";
ZaMsg.ERROR_UNKNOWN = "Unknown error!";
ZaMsg.ERROR_PASSWORD_REQUIRED = "Must specify a password!";
ZaMsg.ERROR_PASSWORD_MISMATCH = "Passwords do not match!";
ZaMsg.ERROR_PASSWORD_TOOLONG = "Password is too long";
ZaMsg.ERROR_PASSWORD_TOOSHORT = "Password is too short";
ZaMsg.ERROR_PASSWORD_INVALID = "Password is invalid";
ZaMsg.ERROR_NAME_REQUIRED = "Must specify a name!";

ZaMsg.ERROR_ACCOUNT_NAME_REQUIRED = "Must specify an account name!";
ZaMsg.ERROR_ACCOUNT_LAST_NAME_REQUIRED = "Must specify a last name!";
ZaMsg.ERROR_ACCOUNT_NAME_INVALID = "The specified account name is invalid.";
ZaMsg.FAILED_ADD_ALIASES = "Failed to add Aliases. ";
ZaMsg.WARNING_ALIAS_EXISTS = "Cannot add alias, because an account with the same name as the specified alias already exists: ";
ZaMsg.WARNING_ALIASES_EXIST = "Cannot add aliases, accounts with the same names as the specified aliases already exist: ";
ZaMsg.ERROR_ACCOUNT_EXISTS = "The specified account already exists. Please choose another name.";
ZaMsg.ERROR_COS_EXISTS = "The specified Class of service already exists. Please choose another name.";
ZaMsg.ERROR_COS_NAME_TOOLONG = "The specified Class of service name is too long.";
ZaMsg.ERROR_DOMAIN_EXISTS = "The specified domain already exists.";
ZaMsg.ERROR_DOMAIN_NAME_TOOLONG = "The specified domain name is too long.";
ZaMsg.ERROR_DOMAIN_NAME_INVALID = "The specified domain name is invalid.";
ZaMsg.ERROR_DOMAIN_NAME_REQUIRED = "A valid domain name is required.";
ZaMsg.ERROR_DOMAIN_NOT_EMPTY = "Cannot delete the domain, because it is not empty. Please remove all the accounts from the domain first.";
ZaMsg.ERROR_WRONG_HOST = "Command sent to the wrong host.";
ZaMsg.ERROR_NO_SUCH_ACCOUNT = "No such account."

ZaMsg.FAILED_RENAME_ACCOUNT_1 = "Failed to rename account. Another account with the specified name already exists. <br>Please choose another name.";
ZaMsg.FAILED_RENAME_ACCOUNT = "Failed to rename account.";
ZaMsg.FAILED_CREATE_ACCOUNT_1 = "Failed to create account. The specified account already exists. <br>Please choose another name.";
ZaMsg.FAILED_CREATE_ACCOUNT = "Failed to create account.";
ZaMsg.FAILED_SAVE_ACCOUNT = "Failed to save the changes to the account.";

ZaMsg.FAILED_RENAME_COS_1 = "Failed to rename COS. Another COS with the specified name already exists. <br>Please choose another name.";
ZaMsg.FAILED_RENAME_COS = "Failed to rename COS.";
ZaMsg.FAILED_CREATE_COS_1 = "Failed to create COS. COS with this name already exists. <br>Please choose another name.";
ZaMsg.FAILED_CREATE_COS = "Failed to create COS.";
ZaMsg.FAILED_SAVE_COS = "Failed to save the changes to the COS.";

ZaMsg.ERROR_RESTORE_1 = "Cannot restorer accounts. Prefix parameter is required when restoring a mailbox into a new account.";
ZaMsg.ERROR_RESTORE_2 = "Cannot restorer accounts, because argument 'accounts' is missing or null.";
ZaMsg.ERROR_RESTORE_3 = "Cannot restorer accounts, because argument 'method' is missing or null.";

ZaMsg.ERROR_BACKUP_1 = "Cannot query backup labels for accounts, because argument 'accounts' is missing or null.";

ZaMsg.Q_DELETE_ACCOUNTS ="Are you sure you want to delete these accounts: ";
ZaMsg.Q_DELETE_ALIASES ="Are you sure you want to delete these aliases:";
ZaMsg.Q_DELETE_DOMAINS ="Are you sure you want to delete these domains:";
ZaMsg.Q_DELETE_COS ="Are you sure you want to delete these classes of service:";
ZaMsg.Q_SAVE_CHANGES="Do you want to save current changes?";
ZaMsg.Q_DELETE_SERVERS="Deleting a server will remove the server entry from LDAP. <br>You should do this only after the server has been removed from your network.<br> Are you sure you want to delete these servers:";

ZaMsg.CORRECT_ERRORS = "Please correct the values in these fields:";

ZaMsg.attrDesc = 
function(name) {
	var desc = ZaMsg.ATTR[name];
	return (desc == null) ? name : desc;
}

/* Translation of  the attribute names to the screen names */
ZaMsg.ATTR = new Object();

	ZaMsg.ATTR[ZaAccount.A_accountName] = "Full Name";
	ZaMsg.ATTR[ZaAccount.A_description] = "Description";
	ZaMsg.ATTR[ZaAccount.A_firstName] = "First Name";
	ZaMsg.ATTR[ZaAccount.A_lastName] =  "Last Name",
	ZaMsg.ATTR[ZaAccount.A_accountStatus] =  "Account Status",
	ZaMsg.ATTR[ZaItem.A_zimbraId] =  "Id",	
	ZaMsg.ATTR[ZaAccount.A_mailHost] =  "Mail Server",
	ZaMsg.ATTR[ZaAccount.A_zimbraMailQuota] =  "Mail Quota",
	ZaMsg.ATTR[ZaAccount.A_notes] =  "Notes"

ZaMsg.accountStatus = 
function(val) {
	var desc = ZaMsg.ACCOUNT_STATUS[val];
	return (desc == null) ? val : desc;
}

/* Translation of Account status values into screen names */
ZaMsg.ACCOUNT_STATUS = new Object ();
	ZaMsg.ACCOUNT_STATUS[ZaAccount.ACCOUNT_STATUS_ACTIVE] = "Active",
	ZaMsg.ACCOUNT_STATUS[ZaAccount.ACCOUNT_STATUS_MAINTENANCE] = "Maintenance",
	ZaMsg.ACCOUNT_STATUS[ZaAccount.ACCOUNT_STATUS_LOCKED] = "Locked",
	ZaMsg.ACCOUNT_STATUS[ZaAccount.ACCOUNT_STATUS_CLOSED] = "Closed"


ZaMsg.mailStatus = 
function(name) {
	var desc = ZaMsg.ACCOUNT_STATUS[name];
	return (desc == null) ? name : desc;
}


ZaMsg.GALModes = new Object();
ZaMsg.GALModes[ZaDomain.GAL_Mode_internal]="Internal";
ZaMsg.GALModes[ZaDomain.GAL_Mode_external]="External";
ZaMsg.GALModes[ZaDomain.GAL_Mode_both]="Both";

ZaMsg.AuthMechs = new Object();
ZaMsg.AuthMechs[ZaDomain.AuthMech_ldap] = "LDAP";
ZaMsg.AuthMechs[ZaDomain.AuthMech_Zimbra]="Zimbra";



ZaMsg.STANDALONE = "Standalone";
ZaMsg.MASTER = "Master";
ZaMsg.SLAVE = "Slave";
ZaMsg.Filter = "Filter";

ZaMsg.adminGuide = "Admin Guide";
ZaMsg.help = "Help";
ZaMsg.about = "About";
ZaMsg.about_title = "Zimbra(TM) Administration Console";
ZaMsg.migrationWiz = "Migration Wizard";
ZaMsg.logOff = "Log Off";
ZaMsg.done = "Done";
ZaMsg.searchForAccounts = "Search for email accounts, distribution lists and email aliases";
ZaMsg.search = "Search";
ZaMsg.queryParseError = "Unable to parse your search query. Please correct any errors and resubmit.";
ZaMsg.zimbraAdminTitle = "Zimbra Administration";
ZaMsg.usedQuota = "Used quota";
ZaMsg.login = " Log On ";
ZaMsg.loginHeader = "Zimbra(TM) Collaboration Suite Administration Console";
ZaMsg.username = "Username";
ZaMsg.password = "Password";
ZaMsg.publicComputer = "Public Computer";
ZaMsg.enterUsername = "Please enter administrator username and password";