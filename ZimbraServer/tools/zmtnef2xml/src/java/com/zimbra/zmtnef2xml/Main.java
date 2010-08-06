/*
 * Diagnostic tool which provides an XML-like view of
 * the properties in a TNEF file
 */

package com.zimbra.zmtnef2xml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.freeutils.tnef.Attachment;
import net.freeutils.tnef.Attr;
import net.freeutils.tnef.GUID;
import net.freeutils.tnef.MAPIProp;
import net.freeutils.tnef.MAPIPropName;
import net.freeutils.tnef.MAPIProps;
import net.freeutils.tnef.MAPIValue;
import net.freeutils.tnef.Message;
import net.freeutils.tnef.TNEFInputStream;
import net.freeutils.tnef.TNEFUtils;

/**
 *
 * @author gren
 */
public class Main {

    private static final Map<Integer, String> propertyIdMap;
    private static final Map<Long, String> PSETID_MeetingByLidMap;
    private static final Map<Long, String> PSETID_AppointmentByLidMap;
    private static final Map<Long, String> PSETID_CommonByLidMap;
    private static final Map<Long, String> PSETID_TaskByLidMap;
    private static final Map<Long, String> PSETID_AddressByLidMap;
    private static final Map<Long, String> PS_PUBLIC_STRINGSByLidMap;

    static {
        Map<Integer, String> myMap = new HashMap<Integer, String> ();
        // myMap.put(, "0x39ff");
        myMap.put(0x0001, "PidTagTemplateData");
        myMap.put(0x0002, "PidTagAlternateRecipientAllowed");
        myMap.put(0x0004, "PidTagScriptData");
        myMap.put(0x0005, "PidTagAutoForwarded");
        myMap.put(0x000f, "PidTagDeferredDeliveryTime");
        myMap.put(0x0015, "PidTagExpiryTime");
        myMap.put(0x0017, "PidTagImportance");
        myMap.put(0x001a, "PidTagMessageClass");
        myMap.put(0x0023, "PidTagOriginatorDeliveryReportRequested");
        myMap.put(0x0025, "PidTagParentKey");
        myMap.put(0x0026, "PidTagPriority");
        myMap.put(0x0029, "PidTagReadReceiptRequested");
        myMap.put(0x002b, "PidTagRecipientReassignmentProhibited");
        myMap.put(0x002e, "PidTagOriginalSensitivity");
        myMap.put(0x0030, "PidTagReplyTime");
        myMap.put(0x0031, "PidTagReportTag");
        myMap.put(0x0032, "PidTagReportTime");
        myMap.put(0x0036, "PidTagSensitivity");
        myMap.put(0x0037, "PidTagSubject");
        myMap.put(0x0039, "PidTagClientSubmitTime");
        myMap.put(0x003a, "PidTagReportName");
        myMap.put(0x003b, "PidTagSentRepresentingSearchKey");
        myMap.put(0x003d, "PidTagSubjectPrefix");
        myMap.put(0x003f, "PidTagReceivedByEntryId");
        myMap.put(0x0040, "PidTagReceivedByName");
        myMap.put(0x0041, "PidTagSentRepresentingEntryId");
        myMap.put(0x0042, "PidTagSentRepresentingName");
        myMap.put(0x0043, "PidTagReceivedRepresentingEntryId");
        myMap.put(0x0044, "PidTagReceivedRepresentingName");
        myMap.put(0x0045, "PidTagReportEntryId");
        myMap.put(0x0046, "PidTagReadReceiptEntryId");
        myMap.put(0x0047, "PidTagMessageSubmissionId");
        myMap.put(0x0048, "PidTagProviderSubmitTime");
        myMap.put(0x0049, "PidTagOriginalSubject");
        myMap.put(0x004b, "PidTagOriginalMessageClass");
        myMap.put(0x004d, "PidTagOriginalAuthorName");
        myMap.put(0x004e, "PidTagOriginalSubmitTime");
        myMap.put(0x004f, "PidTagReplyRecipientEntries");
        myMap.put(0x0050, "PidTagReplyRecipientNames");
        myMap.put(0x0051, "PidTagReceivedBySearchKey");
        myMap.put(0x0052, "PidTagReceivedRepresentingSearchKey");
        myMap.put(0x0053, "PidTagReadReceiptSearchKey");
        myMap.put(0x0054, "PidTagReportSearchKey");
        myMap.put(0x0055, "PidTagOriginalDeliveryTime");
        myMap.put(0x0057, "PidTagMessageToMe");
        myMap.put(0x0058, "PidTagMessageCcMe");
        myMap.put(0x0059, "PidTagMessageRecipientMe");
        myMap.put(0x005a, "PidTagOriginalSenderName");
        myMap.put(0x005b, "PidTagOriginalSenderEntryId");
        myMap.put(0x005c, "PidTagOriginalSenderSearchKey");
        myMap.put(0x005d, "PidTagOriginalSentRepresentingName");
        myMap.put(0x005e, "PidTagOriginalSentRepresentingEntryId");
        myMap.put(0x005f, "PidTagOriginalSentRepresentingSearchKey");
        myMap.put(0x0060, "PidTagStartDate");
        myMap.put(0x0061, "PidTagEndDate");
        myMap.put(0x0062, "PidTagOwnerAppointmentId");
        myMap.put(0x0063, "PidTagResponseRequested");
        myMap.put(0x0064, "PidTagSentRepresentingAddressType");
        myMap.put(0x0065, "PidTagSentRepresentingEmailAddress");
        myMap.put(0x0066, "PidTagOriginalSenderAddressType");
        myMap.put(0x0067, "PidTagOriginalSenderEmailAddress");
        myMap.put(0x0068, "PidTagOriginalSentRepresentingAddressType");
        myMap.put(0x0069, "PidTagOriginalSentRepresentingEmailAddress");
        myMap.put(0x0070, "PidTagConversationTopic");
        myMap.put(0x0071, "PidTagConversationIndex");
        myMap.put(0x0072, "PidTagOriginalDisplayBcc");
        myMap.put(0x0073, "PidTagOriginalDisplayCc");
        myMap.put(0x0074, "PidTagOriginalDisplayTo");
        myMap.put(0x0075, "PidTagReceivedByAddressType");
        myMap.put(0x0076, "PidTagReceivedByEmailAddress");
        myMap.put(0x0077, "PidTagReceivedRepresentingAddressType");
        myMap.put(0x0078, "PidTagReceivedRepresentingEmailAddress");
        myMap.put(0x007d, "PidTagTransportMessageHeaders");
        myMap.put(0x007f, "PidTagTnefCorrelationKey");
        myMap.put(0x0470, "PidTagMimeSkeleton");
        myMap.put(0x0807, "PidTagAddressBookRoomCapacity");
        myMap.put(0x0809, "PidTagAddressBookRoomDescription");
        myMap.put(0x0c08, "PidTagOriginatorNonDeliveryReportRequested");
        myMap.put(0x0c15, "PidTagRecipientType");
        myMap.put(0x0c17, "PidTagReplyRequested");
        myMap.put(0x0c19, "PidTagSenderEntryId");
        myMap.put(0x0c1a, "PidTagSenderName");
        myMap.put(0x0c1d, "PidTagSenderSearchKey");
        myMap.put(0x0c1e, "PidTagSenderAddressType");
        myMap.put(0x0c1f, "PidTagSenderEmailAddress");
        myMap.put(0x0c21, "PidTagRemoteMessageTransferAgent");
        myMap.put(0x0e01, "PidTagDeleteAfterSubmit");
        myMap.put(0x0e02, "PidTagDisplayBcc");
        myMap.put(0x0e03, "PidTagDisplayCc");
        myMap.put(0x0e04, "PidTagDisplayTo");
        myMap.put(0x0e06, "PidTagMessageDeliveryTime");
        myMap.put(0x0e07, "PidTagMessageFlags");
        myMap.put(0x0e08, "PidTagMessageSize");
        myMap.put(0x0e09, "PidTagParentEntryId");
        myMap.put(0x0e0a, "PidTagSentMailEntryId");
        myMap.put(0x0e0f, "PidTagResponsibility");
        myMap.put(0x0e12, "PidTagMessageRecipients");
        myMap.put(0x0e13, "PidTagMessageAttachments");
        myMap.put(0x0e14, "PidTagSubmitFlags");
        myMap.put(0x0e17, "PidTagMessageStatus");
        myMap.put(0x0e1b, "PidTagHasAttachments");
        myMap.put(0x0e1d, "PidTagNormalizedSubject");
        myMap.put(0x0e1f, "PidTagRtfInSync");
        myMap.put(0x0e20, "PidTagAttachSize");
        myMap.put(0x0e21, "PidTagAttachNumber");
        myMap.put(0x0e23, "PidTagInternetArticleNumber");
        myMap.put(0x0e27, "PidTagSecurityDescriptor");
        myMap.put(0x0e28, "PidTagPrimarySendAccount");
        myMap.put(0x0e29, "PidTagNextSendAcct");
        myMap.put(0x0e2b, "PidTagToDoItemFlags");
        myMap.put(0x0e2c, "PidTagSwappedToDoStore");
        myMap.put(0x0e2d, "PidTagSwappedToDoData");
        myMap.put(0x0e62, "PidTagUrlCompNameSet");
        myMap.put(0x0e69, "PidTagRead");
        myMap.put(0x0e6a, "PidTagSecurityDescriptorAsXml");
        myMap.put(0x0e79, "PidTagTrustSender");
        myMap.put(0x0e84, "PidTagExchangeNTSecurityDescriptor");
        myMap.put(0x0e99, "PidTagExtendedRuleMessageActions");
        myMap.put(0x0e9a, "PidTagExtendedRuleMessageCondition");
        myMap.put(0x0e9b, "PidTagExtendedRuleSizeLimit");
        myMap.put(0x0ff4, "PidTagAccess");
        myMap.put(0x0ff5, "PidTagRowType");
        myMap.put(0x0ff6, "PidTagInstanceKey");
        myMap.put(0x0ff7, "PidTagAccessLevel");
        myMap.put(0x0ff8, "PidTagMappingSignature");
        myMap.put(0x0ff9, "PidTagRecordKey");
        myMap.put(0x0ffb, "PidTagStoreEntryId");
        myMap.put(0x0ffe, "PidTagObjectType");
        myMap.put(0x0fff, "PidTagEntryId");
        myMap.put(0x1000, "PidTagBody");
        myMap.put(0x1001, "PidTagReportText");
        myMap.put(0x1006, "PidTagRtfSyncBodyCrc");
        myMap.put(0x1007, "PidTagRtfSyncBodyCount");
        myMap.put(0x1008, "PidTagRtfSyncBodyTag");
        myMap.put(0x1009, "PidTagRtfCompressed");
        myMap.put(0x1010, "PidTagRtfSyncPrefixCount");
        myMap.put(0x1011, "PidTagRtfSyncTrailingCount");
        myMap.put(0x1013, "PidTagBodyHtml");
        myMap.put(0x1014, "PidTagBodyContentLocation");
        myMap.put(0x1015, "PidTagBodyContentId");
        myMap.put(0x1016, "PidTagNativeBody");
        myMap.put(0x1035, "PidTagInternetMessageId");
        myMap.put(0x1039, "PidTagInternetReferences");
        myMap.put(0x1042, "PidTagInReplyToId");
        myMap.put(0x1043, "PidTagListHelp");
        myMap.put(0x1044, "PidTagListSubscribe");
        myMap.put(0x1045, "PidTagListUnsubscribe");
        myMap.put(0x1046, "PidTagInternetReturnPath");
        myMap.put(0x1080, "PidTagIconIndex");
        myMap.put(0x1081, "PidTagLastVerbExecuted");
        myMap.put(0x1082, "PidTagLastVerbExecutionTime");
        myMap.put(0x1090, "PidTagFlagStatus");
        myMap.put(0x1091, "PidTagFlagCompleteTime");
        myMap.put(0x1095, "PidTagFollowupIcon");
        myMap.put(0x1096, "PidTagBlockStatus");
        myMap.put(0x1097, "PidTagItemTemporaryflags");
        myMap.put(0x1098, "PidTagConflictItems");
        myMap.put(0x10c3, "PidTagICalendarStartTime");
        myMap.put(0x10c4, "PidTagICalendarEndTime");
        myMap.put(0x10c5, "PidTagCdoRecurrenceid");
        myMap.put(0x10ca, "PidTagICalendarReminderNextTime");
        myMap.put(0x10f0, "PidTagImapCachedMsgsize");
        myMap.put(0x10f3, "PidTagUrlCompName");
        myMap.put(0x10f4, "PidTagAttributeHidden");
        myMap.put(0x10f5, "PidTagAttributeSystem");
        myMap.put(0x10f6, "PidTagAttributeReadOnly");
        myMap.put(0x3000, "PidTagRowid");
        myMap.put(0x3001, "PidTagDisplayName");
        myMap.put(0x3002, "PidTagAddressType");
        myMap.put(0x3003, "PidTagEmailAddress");
        myMap.put(0x3004, "PidTagComment");
        myMap.put(0x3005, "PidTagDepth");
        myMap.put(0x3007, "PidTagCreationTime");
        myMap.put(0x3008, "PidTagLastModificationTime");
        myMap.put(0x300b, "PidTagSearchKey");
        myMap.put(0x3010, "PidTagTargetEntryId");
        myMap.put(0x3013, "PidTagConversationId");
        myMap.put(0x3016, "PidTagConversationIndexTracking");
        myMap.put(0x3018, "PidTagArchiveTag");
        myMap.put(0x3019, "PidTagPolicyTag");
        myMap.put(0x301a, "PidTagRetentionPeriod");
        myMap.put(0x301b, "PidTagStartDateEtc");
        myMap.put(0x301c, "PidTagRetentionDate");
        myMap.put(0x301d, "PidTagRetentionFlags");
        myMap.put(0x301e, "PidTagArchivePeriod");
        myMap.put(0x301f, "PidTagArchiveDate");
        myMap.put(0x340d, "PidTagStoreSupportMask");
        myMap.put(0x340e, "PidTagStoreState");
        myMap.put(0x3600, "PidTagContainerFlags");
        myMap.put(0x3601, "PidTagFolderType");
        myMap.put(0x3602, "PidTagContentCount");
        myMap.put(0x3603, "PidTagContentUnreadCount");
        myMap.put(0x3609, "PidTagSelectable");
        myMap.put(0x360a, "PidTagSubfolders");
        myMap.put(0x360c, "PidTagAnr");
        myMap.put(0x360e, "PidTagContainerHierarchy");
        myMap.put(0x360f, "PidTagContainerContents");
        myMap.put(0x3610, "PidTagFolderAssociatedContents");
        myMap.put(0x3613, "PidTagContainerClass");
        myMap.put(0x36d0, "PidTagIpmAppointmentEntryId");
        myMap.put(0x36d1, "PidTagIpmContactEntryId");
        myMap.put(0x36d2, "PidTagIpmJournalEntryId");
        myMap.put(0x36d3, "PidTagIpmNoteEntryId");
        myMap.put(0x36d4, "PidTagIpmTaskEntryId");
        myMap.put(0x36d5, "PidTagRemindersOnlineEntryId");
        myMap.put(0x36d7, "PidTagIpmDraftsEntryId");
        myMap.put(0x36d8, "PidTagAdditionalRenEntryIds");
        myMap.put(0x36d9, "PidTagAdditionalRenEntryIdsEx");
        myMap.put(0x36da, "PidTagExtendedFolderFlags");
        myMap.put(0x36e2, "PidTagOrdinalMost");
        myMap.put(0x36e4, "PidTagFreeBusyEntryIds");
        myMap.put(0x36e5, "PidTagDefaultPostMessageClass");
        myMap.put(0x3701, "PidTagAttachDataBinary");
        myMap.put(0x3702, "PidTagAttachEncoding");
        myMap.put(0x3703, "PidTagAttachExtension");
        myMap.put(0x3704, "PidTagAttachFilename");
        myMap.put(0x3705, "PidTagAttachMethod");
        myMap.put(0x3707, "PidTagAttachLongFilename");
        myMap.put(0x3708, "PidTagAttachPathname");
        myMap.put(0x3709, "PidTagAttachRendering");
        myMap.put(0x370a, "PidTagAttachTag");
        myMap.put(0x370b, "PidTagRenderingPosition");
        myMap.put(0x370c, "PidTagAttachTransportName");
        myMap.put(0x370d, "PidTagAttachLongPathname");
        myMap.put(0x370e, "PidTagAttachMimeTag");
        myMap.put(0x370f, "PidTagAttachAdditionalInformation");
        myMap.put(0x3711, "PidTagAttachContentBase");
        myMap.put(0x3712, "PidTagAttachContentId");
        myMap.put(0x3713, "PidTagAttachContentLocation");
        myMap.put(0x3714, "PidTagAttachFlags");
        myMap.put(0x3719, "PidTagAttachPayloadProviderGuidString");
        myMap.put(0x371a, "PidTagAttachPayloadClass");
        myMap.put(0x371b, "PidTagTextAttachmentCharset");
        myMap.put(0x3900, "PidTagDisplayType");
        myMap.put(0x3902, "PidTagTemplateid");
        myMap.put(0x3905, "PidTagDisplayTypeEx");
        myMap.put(0x39fe, "PidTagPrimarySmtpAddress");
        myMap.put(0x3a00, "PidTagAccount");
        myMap.put(0x3a02, "PidTagCallbackTelephoneNumber");
        myMap.put(0x3a05, "PidTagGeneration");
        myMap.put(0x3a06, "PidTagGivenName");
        myMap.put(0x3a07, "PidTagGovernmentIdNumber");
        myMap.put(0x3a08, "PidTagBusinessTelephoneNumber");
        myMap.put(0x3a09, "PidTagHomeTelephoneNumber");
        myMap.put(0x3a0a, "PidTagInitials");
        myMap.put(0x3a0b, "PidTagKeyword");
        myMap.put(0x3a0c, "PidTagLanguage");
        myMap.put(0x3a0d, "PidTagLocation");
        myMap.put(0x3a0f, "PidTagMessageHandlingSystemCommonName");
        myMap.put(0x3a10, "PidTagOrganizationalIdNumber");
        myMap.put(0x3a11, "PidTagSurname");
        myMap.put(0x3a12, "PidTagOriginalEntryId");
        myMap.put(0x3a13, "PidTagOriginalDisplayName");
        myMap.put(0x3a14, "PidTagOriginalSearchKey");
        myMap.put(0x3a15, "PidTagPostalAddress");
        myMap.put(0x3a16, "PidTagCompanyName");
        myMap.put(0x3a17, "PidTagTitle");
        myMap.put(0x3a18, "PidTagDepartmentName");
        myMap.put(0x3a19, "PidTagOfficeLocation");
        myMap.put(0x3a1a, "PidTagPrimaryTelephoneNumber");
        myMap.put(0x3a1b, "PidTagBusiness2TelephoneNumber");
        myMap.put(0x3a1c, "PidTagMobileTelephoneNumber");
        myMap.put(0x3a1d, "PidTagRadioTelephoneNumber");
        myMap.put(0x3a1e, "PidTagCarTelephoneNumber");
        myMap.put(0x3a1f, "PidTagOtherTelephoneNumber");
        myMap.put(0x3a20, "PidTagTransmittableDisplayName");
        myMap.put(0x3a21, "PidTagPagerTelephoneNumber");
        myMap.put(0x3a22, "PidTagUserCertificate");
        myMap.put(0x3a23, "PidTagPrimaryFaxNumber");
        myMap.put(0x3a24, "PidTagBusinessFaxNumber");
        myMap.put(0x3a25, "PidTagHomeFaxNumber");
        myMap.put(0x3a26, "PidTagCountry");
        myMap.put(0x3a27, "PidTagLocality");
        myMap.put(0x3a28, "PidTagStateOrProvince");
        myMap.put(0x3a29, "PidTagStreetAddress");
        myMap.put(0x3a2a, "PidTagPostalCode");
        myMap.put(0x3a2b, "PidTagPostOfficeBox");
        myMap.put(0x3a2c, "PidTagTelexNumber");
        myMap.put(0x3a2d, "PidTagIsdnNumber");
        myMap.put(0x3a2e, "PidTagAssistantTelephoneNumber");
        myMap.put(0x3a2f, "PidTagHome2TelephoneNumber");
        myMap.put(0x3a30, "PidTagAssistant");
        myMap.put(0x3a40, "PidTagSendRichInfo");
        myMap.put(0x3a41, "PidTagWeddingAnniversary");
        myMap.put(0x3a42, "PidTagBirthday");
        myMap.put(0x3a43, "PidTagHobbies");
        myMap.put(0x3a44, "PidTagMiddleName");
        myMap.put(0x3a45, "PidTagDisplayNamePrefix");
        myMap.put(0x3a46, "PidTagProfession");
        myMap.put(0x3a47, "PidTagReferredByName");
        myMap.put(0x3a48, "PidTagSpouseName");
        myMap.put(0x3a49, "PidTagComputerNetworkName");
        myMap.put(0x3a4a, "PidTagCustomerId");
        myMap.put(0x3a4b, "PidTagTelecommunicationsDeviceForDeafTelephoneNumber");
        myMap.put(0x3a4c, "PidTagFtpSite");
        myMap.put(0x3a4d, "PidTagGender");
        myMap.put(0x3a4e, "PidTagManagerName");
        myMap.put(0x3a4f, "PidTagNickname");
        myMap.put(0x3a50, "PidTagPersonalHomePage");
        myMap.put(0x3a51, "PidTagBusinessHomePage");
        myMap.put(0x3a57, "PidTagCompanyMainTelephoneNumber");
        myMap.put(0x3a58, "PidTagChildrensNames");
        myMap.put(0x3a59, "PidTagHomeAddressCity");
        myMap.put(0x3a5a, "PidTagHomeAddressCountry");
        myMap.put(0x3a5b, "PidTagHomeAddressPostalCode");
        myMap.put(0x3a5c, "PidTagHomeAddressStateOrProvince");
        myMap.put(0x3a5d, "PidTagHomeAddressStreet");
        myMap.put(0x3a5e, "PidTagHomeAddressPostOfficeBox");
        myMap.put(0x3a5f, "PidTagOtherAddressCity");
        myMap.put(0x3a60, "PidTagOtherAddressCountry");
        myMap.put(0x3a61, "PidTagOtherAddressPostalCode");
        myMap.put(0x3a62, "PidTagOtherAddressStateOrProvince");
        myMap.put(0x3a63, "PidTagOtherAddressStreet");
        myMap.put(0x3a64, "PidTagOtherAddressPostOfficeBox");
        myMap.put(0x3a70, "PidTagUserX509Certificate");
        myMap.put(0x3a71, "PidTagSendInternetEncoding");
        myMap.put(0x3f08, "PidTagInitialDetailsPane");
        myMap.put(0x3f20, "PidTagTemporaryDefaultDocument");
        myMap.put(0x3fde, "PidTagInternetCodepage");
        myMap.put(0x3fdf, "PidTagAutoResponseSuppress");
        myMap.put(0x3fe3, "PidTagDelegatedByRule");
        myMap.put(0x3fe7, "PidTagResolveMethod");
        myMap.put(0x3fea, "PidTagHasDeferredActionMessages");
        myMap.put(0x3feb, "PidTagDeferredSendNumber");
        myMap.put(0x3fec, "PidTagDeferredSendUnits");
        myMap.put(0x3fed, "PidTagExpiryNumber");
        myMap.put(0x3fee, "PidTagExpiryUnits");
        myMap.put(0x3fef, "PidTagDeferredSendTime");
        myMap.put(0x3ff0, "PidTagConflictEntryId");
        myMap.put(0x3ff1, "PidTagMessageLocaleId");
        myMap.put(0x3ff8, "PidTagCreatorName");
        myMap.put(0x3ff9, "PidTagCreatorEntryId");
        myMap.put(0x3ffa, "PidTagLastModifierName");
        myMap.put(0x3ffb, "PidTagLastModifierEntryId");
        myMap.put(0x3ffd, "PidTagMessageCodepage");
        myMap.put(0x4000, "PidTagNewAttach");
        myMap.put(0x4001, "PidTagStartEmbed");
        myMap.put(0x4002, "PidTagEndEmbed");
        myMap.put(0x4003, "PidTagStartRecip");
        myMap.put(0x4004, "PidTagEndToRecip");
        myMap.put(0x4009, "PidTagStartTopFld");
        myMap.put(0x400a, "PidTagStartSubFld");
        myMap.put(0x400b, "PidTagEndFolder");
        myMap.put(0x400c, "PidTagStartMessage");
        myMap.put(0x400d, "PidTagEndMessage");
        myMap.put(0x400e, "PidTagEndAttach");
        myMap.put(0x400f, "PidTagEcWarning");
        myMap.put(0x4010, "PidTagStartFAIMsg");
        myMap.put(0x4011, "PidTagNewFXFolder");
        myMap.put(0x4012, "PidTagIncrSyncChg");
        myMap.put(0x4013, "PidTagIncrSyncDel");
        myMap.put(0x4014, "PidTagIncrSyncEnd");
        myMap.put(0x4015, "PidTagIncrSyncMessage");
        myMap.put(0x4016, "PidTagFXDelProp");
        myMap.put(0x4017, "PidTagIdsetGiven");
        myMap.put(0x4018, "PidTagFXErrorInfo");
        myMap.put(0x4019, "PidTagSenderFlags");
        myMap.put(0x401a, "PidTagSentRepresentingFlags");
        myMap.put(0x401b, "PidTagReceivedByFlags");
        myMap.put(0x401c, "PidTagReceivedRepresentingFlags");
        myMap.put(0x4021, "PidTagIdsetNoLongerInScope");
        myMap.put(0x4029, "PidTagReadReceiptAddressType");
        myMap.put(0x402a, "PidTagReadReceiptEmailAddress");
        myMap.put(0x402b, "PidTagReadReceiptName");
        myMap.put(0x402d, "PidTagIdsetRead");
        myMap.put(0x402e, "PidTagIdsetUnread");
        myMap.put(0x402f, "PidTagIncrSyncRead");
        myMap.put(0x4030, "PidTagSenderSimpleDisplayName");
        myMap.put(0x4031, "PidTagSentRepresentingSimpleDisplayName");
        myMap.put(0x4035, "PidTagReceivedRepresentingSimpleDisplayName");
        myMap.put(0x4038, "PidTagCreatorSimpleDisplayName");
        myMap.put(0x4039, "PidTagLastModifierSimpleDisplayName");
        myMap.put(0x403a, "PidTagIncrSyncStateBegin");
        myMap.put(0x403b, "PidTagIncrSyncStateEnd");
        myMap.put(0x4074, "PidTagIncrSyncProgressMode");
        myMap.put(0x4075, "PidTagIncrSyncProgressPerMsg");
        myMap.put(0x4076, "PidTagContentFilterSpamConfidenceLevel");
        myMap.put(0x4079, "PidTagSenderIdStatus");
        myMap.put(0x407a, "PidTagIncrementalSyncMessagePartial");
        myMap.put(0x407b, "PidTagIncrSyncGroupInfo");
        myMap.put(0x407c, "PidTagIncrSyncGroupId");
        myMap.put(0x407d, "PidTagIncrSyncChgPartial");
        myMap.put(0x4083, "PidTagPurportedSenderDomain");
        myMap.put(0x4084, "PidTagContentFilterPhishingConfidenceLevel");
        myMap.put(0x5902, "PidTagInternetMailOverrideFormat");
        myMap.put(0x5909, "PidTagMessageEditorFormat");
        myMap.put(0x5d01, "PidTagSenderSmtpAddress");
        myMap.put(0x5fde, "PidTagRecipientResourceState");
        myMap.put(0x5fdf, "PidTagRecipientOrder");
        myMap.put(0x5fe1, "PidTagRecipientProposed");
        myMap.put(0x5fe3, "PidTagRecipientProposedStartTime");
        myMap.put(0x5fe4, "PidTagRecipientProposedEndTime");
        myMap.put(0x5fe5, "PidTagSessionInitiationProtocolUri");
        myMap.put(0x5ff6, "PidTagRecipientDisplayName");
        myMap.put(0x5ff7, "PidTagRecipientEntryId");
        myMap.put(0x5ffb, "PidTagRecipientTrackStatusTime");
        myMap.put(0x5ffd, "PidTagRecipientFlags");
        myMap.put(0x5fff, "PidTagRecipientTrackStatus");
        myMap.put(0x6100, "PidTagJunkIncludeContacts");
        myMap.put(0x6101, "PidTagJunkThreshold");
        myMap.put(0x6102, "PidTagJunkPermanentlyDelete");
        myMap.put(0x6103, "PidTagJunkAddRecipientsToSafeSendersList");
        myMap.put(0x6107, "PidTagJunkPhishingEnableLinks");
        myMap.put(0x65c2, "PidTagReplyTemplateId");
        myMap.put(0x65c6, "PidTagSecureSubmitFlags");
        myMap.put(0x65e0, "PidTagSourceKey");
        myMap.put(0x65e1, "PidTagParentSourceKey");
        myMap.put(0x65e2, "PidTagChangeKey");
        myMap.put(0x65e3, "PidTagPredecessorChangeList");
        myMap.put(0x65e9, "PidTagRuleMessageState");
        myMap.put(0x65ea, "PidTagRuleMessageUserFlags");
        myMap.put(0x65eb, "PidTagRuleMessageProvider");
        myMap.put(0x65ec, "PidTagRuleMessageName");
        myMap.put(0x65ed, "PidTagRuleMessageLevel");
        myMap.put(0x65ee, "PidTagRuleMessageProviderData");
        myMap.put(0x65f3, "PidTagRuleMessageSequence");
        myMap.put(0x6619, "PidTagUserEntryId");
        myMap.put(0x661b, "PidTagMailboxOwnerEntryId");
        myMap.put(0x661c, "PidTagMailboxOwnerName");
        myMap.put(0x661d, "PidTagOutOfOfficeState");
        myMap.put(0x6622, "PidTagSchedulePlusFreeBusyEntryId");
        myMap.put(0x6637, "PidTagChangeNotificationGuid");
        myMap.put(0x6639, "PidTagRights");
        myMap.put(0x663a, "PidTagHasRules");
        myMap.put(0x663b, "PidTagAddressBookEntryId");
        myMap.put(0x663e, "PidTagHierarchyChangeNumber");
        myMap.put(0x6645, "PidTagClientActions");
        myMap.put(0x6646, "PidTagDamOriginalEntryId");
        myMap.put(0x6647, "PidTagDamBackPatched");
        myMap.put(0x6648, "PidTagRuleError");
        myMap.put(0x6649, "PidTagRuleActionType");
        myMap.put(0x664a, "PidTagHasNamedProperties");
        myMap.put(0x6650, "PidTagRuleActionNumber");
        myMap.put(0x6651, "PidTagRuleFolderEntryId");
        myMap.put(0x666a, "PidTagProhibitReceiveQuota");
        myMap.put(0x666c, "PidTagInConflict");
        myMap.put(0x666d, "PidTagMaximumSubmitMessageSize");
        myMap.put(0x666e, "PidTagProhibitSendQuota");
        myMap.put(0x6671, "PidTagMemberId");
        myMap.put(0x6672, "PidTagMemberName");
        myMap.put(0x6673, "PidTagMemberRights");
        myMap.put(0x6674, "PidTagRuleId");
        myMap.put(0x6676, "PidTagRuleSequence");
        myMap.put(0x6677, "PidTagRuleState");
        myMap.put(0x6678, "PidTagRuleUserFlags");
        myMap.put(0x6679, "PidTagRuleCondition");
        myMap.put(0x6680, "PidTagRuleActions");
        myMap.put(0x6681, "PidTagRuleProvider");
        myMap.put(0x6682, "PidTagRuleName");
        myMap.put(0x6683, "PidTagRuleLevel");
        myMap.put(0x6684, "PidTagRuleProviderData");
        myMap.put(0x668f, "PidTagDeletedOn");
        myMap.put(0x66a1, "PidTagLocaleId");
        myMap.put(0x66b3, "PidTagNormalMessageSize");
        myMap.put(0x66c3, "PidTagCodePageId");
        myMap.put(0x6704, "PidTagAddressBookManageDistributionList");
        myMap.put(0x6705, "PidTagSortLocaleId");
        myMap.put(0x6707, "PidTagUrlName");
        myMap.put(0x6708, "PidTagSubfolder");
        myMap.put(0x6709, "PidTagLocalCommitTime");
        myMap.put(0x670e, "PidTagFlatUrlName");
        myMap.put(0x671c, "PidTagPublicFolderAdministrativeDescription");
        myMap.put(0x671d, "PidTagPublicFolderProxy");
        myMap.put(0x6740, "PidTagSentMailSvrEID");
        myMap.put(0x6748, "PidTagFolderId");
        myMap.put(0x6749, "PidTagParentFolderId");
        myMap.put(0x674a, "PidTagMid");
        myMap.put(0x674d, "PidTagInstID");
        myMap.put(0x674e, "PidTagInstanceNum");
        myMap.put(0x674f, "PidTagAddressBookMessageId");
        myMap.put(0x6793, "PidTagIdsetExpired");
        myMap.put(0x6796, "PidTagCnsetSeen");
        myMap.put(0x67a4, "PidTagChangeNumber");
        myMap.put(0x67aa, "PidTagAssociated");
        myMap.put(0x67d2, "PidTagCnsetRead");
        myMap.put(0x67da, "PidTagCnsetSeenFAI");
        myMap.put(0x67e5, "PidTagIdsetDeleted");
        myMap.put(0x6800, "PidTagOfflineAddressBookName");
        myMap.put(0x6801, "PidTagOfflineAddressBookSequence");
        myMap.put(0x6802, "PidTagOfflineAddressBookContainerGuid");
        myMap.put(0x6803, "PidTagOfflineAddressBookMessageClass");
        myMap.put(0x6804, "PidTagFaxNumberOfPages");
        myMap.put(0x6805, "PidTagOfflineAddressBookTruncatedProperties");
        myMap.put(0x6806, "PidTagCallId");
        myMap.put(0x6807, "PidTagOfflineAddressBookLanguageId");
        myMap.put(0x6808, "PidTagOfflineAddressBookFileType");
        myMap.put(0x6809, "PidTagOfflineAddressBookCompressedSize");
        myMap.put(0x680a, "PidTagOfflineAddressBookFileSize");
        myMap.put(0x6820, "PidTagReportingMessageTransferAgent");
        myMap.put(0x6834, "PidTagSearchFolderLastUsed");
        myMap.put(0x683a, "PidTagSearchFolderExpiration");
        myMap.put(0x6841, "PidTagScheduleInfoResourceType");
        myMap.put(0x6842, "PidTagScheduleInfoDelegatorWantsCopy");
        myMap.put(0x6843, "PidTagScheduleInfoDontMailDelegates");
        myMap.put(0x6844, "PidTagScheduleInfoDelegateNames");
        myMap.put(0x6845, "PidTagScheduleInfoDelegateEntryIds");
        myMap.put(0x6846, "PidTagGatewayNeedsToRefresh");
        myMap.put(0x6847, "PidTagFreeBusyPublishStart");
        myMap.put(0x6847, "PidTagWlinkSaveStamp");
        myMap.put(0x6848, "PidTagFreeBusyPublishEnd");
        myMap.put(0x6849, "PidTagFreeBusyMessageEmailAddress");
        myMap.put(0x684a, "PidTagScheduleInfoDelegateNamesW");
        myMap.put(0x684b, "PidTagScheduleInfoDelegatorWantsInfo");
        myMap.put(0x684c, "PidTagWlinkEntryId");
        myMap.put(0x684d, "PidTagWlinkRecordKey");
        myMap.put(0x684e, "PidTagWlinkStoreEntryId");
        myMap.put(0x684f, "PidTagScheduleInfoMonthsMerged");
        myMap.put(0x6850, "PidTagScheduleInfoFreeBusyMerged");
        myMap.put(0x6851, "PidTagScheduleInfoMonthsTentative");
        myMap.put(0x6852, "PidTagScheduleInfoFreeBusyTentative");
        myMap.put(0x6853, "PidTagScheduleInfoMonthsBusy");
        myMap.put(0x6854, "PidTagScheduleInfoFreeBusyBusy");
        myMap.put(0x6855, "PidTagScheduleInfoMonthsAway");
        myMap.put(0x6856, "PidTagScheduleInfoFreeBusyAway");
        myMap.put(0x6868, "PidTagFreeBusyRangeTimestamp");
        myMap.put(0x6869, "PidTagFreeBusyCountMonths");
        myMap.put(0x686a, "PidTagScheduleInfoAppointmentTombstone");
        myMap.put(0x686b, "PidTagDelegateFlags");
        myMap.put(0x686c, "PidTagScheduleInfoFreeBusy");
        myMap.put(0x686d, "PidTagScheduleInfoAutoAcceptAppointments");
        myMap.put(0x686e, "PidTagScheduleInfoDisallowRecurringAppts");
        myMap.put(0x686f, "PidTagScheduleInfoDisallowOverlappingAppts");
        myMap.put(0x6890, "PidTagWlinkClientID");
        myMap.put(0x6891, "PidTagWlinkAddressBookStoreEID");
        myMap.put(0x6892, "PidTagWlinkROGroupType");
        myMap.put(0x7001, "PidTagViewDescriptorBinary");
        myMap.put(0x7002, "PidTagViewDescriptorStrings");
        myMap.put(0x7006, "PidTagViewDescriptorName");
        myMap.put(0x7007, "PidTagViewDescriptorVersion");
        myMap.put(0x7c06, "PidTagRoamingDatatypes");
        myMap.put(0x7c07, "PidTagRoamingDictionary");
        myMap.put(0x7c08, "PidTagRoamingXmlStream");
        myMap.put(0x7d01, "PidTagProcessed");
        myMap.put(0x7ff9, "PidTagExceptionReplaceTime");
        myMap.put(0x7ffa, "PidTagAttachmentLinkId");
        myMap.put(0x7ffb, "PidTagExceptionStartTime");
        myMap.put(0x7ffc, "PidTagExceptionEndTime");
        myMap.put(0x7ffd, "PidTagAttachmentFlags");
        myMap.put(0x7ffe, "PidTagAttachmentHidden");
        myMap.put(0x7fff, "PidTagAttachmentContactPhoto");
        myMap.put(0x8004, "PidTagAddressBookFolderPathname");
        myMap.put(0x8005, "PidTagAddressBookManager");
        myMap.put(0x8006, "PidTagAddressBookHomeMessageDatabase");
        myMap.put(0x8008, "PidTagAddressBookIsMemberOfDistributionList");
        myMap.put(0x8009, "PidTagAddressBookMember");
        myMap.put(0x800c, "PidTagAddressBookOwner");
        myMap.put(0x800e, "PidTagAddressBookReports");
        myMap.put(0x800f, "PidTagAddressBookProxyAddresses");
        myMap.put(0x8011, "PidTagAddressBookTargetAddress");
        myMap.put(0x8015, "PidTagAddressBookPublicDelegates");
        myMap.put(0x8024, "PidTagAddressBookOwnerBackLink");
        myMap.put(0x802d, "PidTagAddressBookExtensionAttribute1");
        myMap.put(0x803c, "PidTagAddressBookObjectDistinguishedName");
        myMap.put(0x806a, "PidTagAddressBookDeliveryContentLength");
        myMap.put(0x8073, "PidTagAddressBookDistributionListMemberSubmitAccepted");
        myMap.put(0x8170, "PidTagAddressBookNetworkAddress");
        myMap.put(0x8c61, "PidTagAddressBookExtensionAttribute15");
        myMap.put(0x8c6a, "PidTagAddressBookX509Certificate");
        myMap.put(0x8c6d, "PidTagAddressBookObjectGuid");
        myMap.put(0x8c8e, "PidTagAddressBookPhoneticGivenName");
        myMap.put(0x8c8f, "PidTagAddressBookPhoneticSurname");
        myMap.put(0x8c90, "PidTagAddressBookPhoneticDepartmentName");
        myMap.put(0x8c91, "PidTagAddressBookPhoneticCompanyName");
        myMap.put(0x8c92, "PidTagAddressBookPhoneticDisplayName");
        myMap.put(0x8c93, "PidTagAddressBookDisplayTypeExtended");
        myMap.put(0x8c94, "PidTagAddressBookHierarchicalShowInDepartments");
        myMap.put(0x8c96, "PidTagAddressBookRoomContainers");
        myMap.put(0x8c97, "PidTagAddressBookHierarchicalDepartmentMembers");
        myMap.put(0x8c98, "PidTagAddressBookHierarchicalRootDepartment");
        myMap.put(0x8c99, "PidTagAddressBookHierarchicalParentDepartment");
        myMap.put(0x8c9a, "PidTagAddressBookHierarchicalChildDepartments");
        myMap.put(0x8c9e, "PidTagThumbnailPhoto");
        myMap.put(0x8ca0, "PidTagAddressBookSeniorityIndex");
        myMap.put(0x8ca8, "PidTagAddressBookOrganizationalUnitRootDistinguishedName");
        myMap.put(0x8cac, "PidTagAddressBookSenderHintTranslations");
        myMap.put(0x8cb5, "PidTagAddressBookModerationEnabled");
        myMap.put(0x8cc2, "PidTagSpokenName");
        myMap.put(0x8cd8, "PidTagAddressBookAuthorizedSenders");
        myMap.put(0x8cd9, "PidTagAddressBookUnauthorizedSenders");
        myMap.put(0x8cda, "PidTagAddressBookDistributionListMemberSubmitRejected");
        myMap.put(0x8cdd, "PidTagAddressBookHierarchicalIsHierarchicalGroup");
        myMap.put(0x8ce2, "PidTagAddressBookDistributionListMemberCount");
        myMap.put(0x8ce3, "PidTagAddressBookDistributionListExternalMemberCount");
        myMap.put(0xfffb, "PidTagAddressBookIsMaster");
        myMap.put(0xfffc, "PidTagAddressBookParentEntryId");
        myMap.put(0xfffd, "PidTagAddressBookContainerId");
        propertyIdMap = Collections.unmodifiableMap(myMap);
    }

    static {
        Map<Long, String> myMap = new HashMap<Long, String> ();
        myMap.put(0x00000001L, "PidLidAttendeeCriticalChange");
        myMap.put(0x00000002L, "PidLidWhere");
        myMap.put(0x00000003L, "PidLidGlobalObjectId");
        myMap.put(0x00000004L, "PidLidIsSilent");
        myMap.put(0x00000005L, "PidLidIsRecurring");
        myMap.put(0x00000006L, "PidLidRequiredAttendees");
        myMap.put(0x00000007L, "PidLidOptionalAttendees");
        myMap.put(0x00000008L, "PidLidResourceAttendees");
        myMap.put(0x00000009L, "PidLidDelegateMail");
        myMap.put(0x0000000aL, "PidLidIsException");
        myMap.put(0x0000000bL, "PidLidSingleInvite");
        myMap.put(0x0000000cL, "PidLidTimeZone");
        myMap.put(0x0000000dL, "PidLidStartRecurrenceDate");
        myMap.put(0x0000000eL, "PidLidStartRecurrenceTime");
        myMap.put(0x0000000fL, "PidLidEndRecurrenceDate");
        myMap.put(0x00000010L, "PidLidEndRecurrenceTime");
        myMap.put(0x00000011L, "PidLidDayInterval");
        myMap.put(0x00000017L, "PidLidMonthOfYearMask");
        myMap.put(0x00000018L, "PidLidOldRecurrenceType");
        myMap.put(0x0000001aL, "PidLidOwnerCriticalChange");
        myMap.put(0x0000001cL, "PidLidCalendarType");
        myMap.put(0x0000001dL, "PidLidAllAttendeesList");
        myMap.put(0x00000023L, "PidLidCleanGlobalObjectId");
        myMap.put(0x00000024L, "PidLidAppointmentMessageClass");
        myMap.put(0x00000026L, "PidLidMeetingType");
        myMap.put(0x00000028L, "PidLidOldLocation");
        myMap.put(0x00000029L, "PidLidOldWhenStartWhole");
        myMap.put(0x0000002aL, "PidLidOldWhenEndWhole");
        PSETID_MeetingByLidMap = Collections.unmodifiableMap(myMap);
    }

    static {
        Map<Long, String> myMap = new HashMap<Long, String> ();
        myMap.put(0x00008200L, "PidLidSendMeetingAsIcal");
        myMap.put(0x00008201L, "PidLidAppointmentSequence");
        myMap.put(0x00008202L, "PidLidAppointmentSequenceTime");
        myMap.put(0x00008203L, "PidLidAppointmentLastSequence");
        myMap.put(0x00008204L, "PidLidChangeHighlight");
        myMap.put(0x00008205L, "PidLidBusyStatus");
        myMap.put(0x00008206L, "PidLidFExceptionalBody");
        myMap.put(0x00008207L, "PidLidAppointmentAuxiliaryFlags");
        myMap.put(0x00008208L, "PidLidLocation");
        myMap.put(0x00008209L, "PidLidMeetingWorkspaceUrl");
        myMap.put(0x0000820aL, "PidLidForwardInstance");
        myMap.put(0x0000820cL, "PidLidLinkedTaskItems");
        myMap.put(0x0000820dL, "PidLidAppointmentStartWhole");
        myMap.put(0x0000820eL, "PidLidAppointmentEndWhole");
        myMap.put(0x0000820fL, "PidLidAppointmentStartTime");
        myMap.put(0x00008210L, "PidLidAppointmentEndTime");
        myMap.put(0x00008211L, "PidLidAppointmentEndDate");
        myMap.put(0x00008212L, "PidLidAppointmentStartDate");
        myMap.put(0x00008213L, "PidLidAppointmentDuration");
        myMap.put(0x00008214L, "PidLidAppointmentColor");
        myMap.put(0x00008215L, "PidLidAppointmentSubType");
        myMap.put(0x00008216L, "PidLidAppointmentRecur");
        myMap.put(0x00008217L, "PidLidAppointmentStateFlags");
        myMap.put(0x00008218L, "PidLidResponseStatus");
        myMap.put(0x00008223L, "PidLidRecurring");
        myMap.put(0x00008224L, "PidLidIntendedBusyStatus");
        myMap.put(0x00008226L, "PidLidAppointmentUpdateTime");
        myMap.put(0x00008228L, "PidLidExceptionReplaceTime");
        myMap.put(0x00008229L, "PidLidFInvited");
        myMap.put(0x0000822bL, "PidLidFExceptionalAttendees");
        myMap.put(0x0000822eL, "PidLidOwnerName");
        myMap.put(0x0000822fL, "PidLidFOthersAppointment");
        myMap.put(0x00008230L, "PidLidAppointmentReplyName");
        myMap.put(0x00008232L, "PidLidRecurrencePattern");
        myMap.put(0x00008233L, "PidLidTimeZoneStruct");
        myMap.put(0x00008234L, "PidLidTimeZoneDescription");
        myMap.put(0x00008235L, "PidLidClipStart");
        myMap.put(0x00008236L, "PidLidClipEnd");
        myMap.put(0x00008237L, "PidLidOriginalStoreEntryId");
        myMap.put(0x00008238L, "PidLidAllAttendeesString");
        myMap.put(0x0000823aL, "PidLidAutoFillLocation");
        myMap.put(0x0000823bL, "PidLidToAttendeesString");
        myMap.put(0x0000823cL, "PidLidCcAttendeesString");
        myMap.put(0x00008240L, "PidLidConferencingCheck");
        myMap.put(0x00008241L, "PidLidConferencingType");
        myMap.put(0x00008242L, "PidLidDirectory");
        myMap.put(0x00008243L, "PidLidOrganizerAlias");
        myMap.put(0x00008244L, "PidLidAutoStartCheck");
        myMap.put(0x00008245L, "PidLidAutoStartWhen");
        myMap.put(0x00008246L, "PidLidAllowExternalCheck");
        myMap.put(0x00008247L, "PidLidCollaborateDoc");
        myMap.put(0x00008248L, "PidLidNetShowUrl");
        myMap.put(0x00008249L, "PidLidOnlinePassword");
        myMap.put(0x00008250L, "PidLidAppointmentProposedStartWhole");
        myMap.put(0x00008251L, "PidLidAppointmentProposedEndWhole");
        myMap.put(0x00008256L, "PidLidAppointmentProposedDuration");
        myMap.put(0x00008257L, "PidLidAppointmentCounterProposal");
        myMap.put(0x00008259L, "PidLidAppointmentProposalNumber");
        myMap.put(0x0000825aL, "PidLidAppointmentNotAllowPropose");
        myMap.put(0x0000825dL, "PidLidAppointmentUnsendableRecipients");
        myMap.put(0x0000825eL, "PidLidAppointmentTimeZoneDefinitionStartDisplay");
        myMap.put(0x0000825fL, "PidLidAppointmentTimeZoneDefinitionEndDisplay");
        myMap.put(0x00008260L, "PidLidAppointmentTimeZoneDefinitionRecur");
        myMap.put(0x0000827aL, "PidLidInboundICalStream");
        myMap.put(0x0000827bL, "PidLidSingleBodyICal");
        PSETID_AppointmentByLidMap = Collections.unmodifiableMap(myMap);
    }


    static {
        Map<Long, String> myMap = new HashMap<Long, String> ();
        myMap.put(0x00001001L, "PidLidICalendarDayOfWeekMask");
        myMap.put(0x0000100bL, "PidLidNoEndDateFlag");
        myMap.put(0x00008501L, "PidLidReminderDelta");
        myMap.put(0x00008503L, "PidLidReminderSet");
        myMap.put(0x00008505L, "PidLidReminderTimeDate");
        myMap.put(0x00008506L, "PidLidPrivate");
        myMap.put(0x0000850eL, "PidLidAgingDontAgeMe");
        myMap.put(0x00008510L, "PidLidSideEffects");
        myMap.put(0x00008511L, "PidLidRemoteStatus");
        myMap.put(0x00008514L, "PidLidSmartNoAttach");
        myMap.put(0x00008516L, "PidLidCommonStart");
        myMap.put(0x00008517L, "PidLidCommonEnd");
        myMap.put(0x00008518L, "PidLidTaskMode");
        myMap.put(0x00008519L, "PidLidTaskGlobalId");
        myMap.put(0x0000851aL, "PidLidAutoProcessState");
        myMap.put(0x0000851cL, "PidLidReminderOverride");
        myMap.put(0x0000851dL, "PidLidReminderType");
        myMap.put(0x0000851eL, "PidLidReminderPlaySound");
        myMap.put(0x0000851fL, "PidLidReminderFileParameter");
        myMap.put(0x00008524L, "PidLidVerbResponse");
        myMap.put(0x00008535L, "PidLidBilling");
        myMap.put(0x00008536L, "PidLidNonSendableTo");
        myMap.put(0x00008537L, "PidLidNonSendableCc");
        myMap.put(0x00008539L, "PidLidCompanies");
        myMap.put(0x0000853aL, "PidLidContacts");
        myMap.put(0x00008543L, "PidLidNonSendToTrackStatus");
        myMap.put(0x00008544L, "PidLidNonSendCcTrackStatus");
        myMap.put(0x00008545L, "PidLidNonSendBccTrackStatus");
        myMap.put(0x00008552L, "PidLidCurrentVersion");
        myMap.put(0x00008554L, "PidLidCurrentVersionName");
        myMap.put(0x00008580L, "PidLidInternetAccountName");
        myMap.put(0x00008581L, "PidLidInternetAccountStamp");
        myMap.put(0x00008582L, "PidLidUseTnef");
        myMap.put(0x00008584L, "PidLidContactLinkSearchKey");
        myMap.put(0x00008585L, "PidLidContactLinkEntry");
        myMap.put(0x00008586L, "PidLidContactLinkName");
        myMap.put(0x0000859cL, "PidLidSpamOriginalFolder");
        myMap.put(0x000085a0L, "PidLidToDoOrdinalDate");
        myMap.put(0x000085a1L, "PidLidToDoSubOrdinal");
        myMap.put(0x000085a4L, "PidLidToDoTitle");
        myMap.put(0x000085b1L, "PidLidInstantMessagingAddress");
        myMap.put(0x000085b5L, "PidLidClassified");
        myMap.put(0x000085b6L, "PidLidClassification");
        myMap.put(0x000085b7L, "PidLidClassificationDescription");
        myMap.put(0x000085b8L, "PidLidClassificationGuid");
        myMap.put(0x000085baL, "PidLidClassificationKeep");
        myMap.put(0x000085bdL, "PidLidReferenceEntryId");
        myMap.put(0x000085bfL, "PidLidValidFlagStringProof");
        myMap.put(0x000085c0L, "PidLidFlagString");
        myMap.put(0x000085c6L, "PidLidConversationActionMoveFolderEid");
        myMap.put(0x000085c7L, "PidLidConversationActionMoveStoreEid");
        myMap.put(0x000085c8L, "PidLidConversationActionMaxDeliveryTime");
        myMap.put(0x000085c9L, "PidLidConversationProcessed");
        myMap.put(0x000085caL, "PidLidConversationActionLastAppliedTime");
        myMap.put(0x000085cbL, "PidLidConversationActionVersion");
        PSETID_CommonByLidMap = Collections.unmodifiableMap(myMap);
    }

    static {
        Map<Long, String> myMap = new HashMap<Long, String> ();
        myMap.put(0x00008101L, "PidLidTaskStatus");
        myMap.put(0x00008102L, "PidLidPercentComplete");
        myMap.put(0x00008103L, "PidLidTeamTask");
        myMap.put(0x00008107L, "PidLidTaskResetReminder");
        myMap.put(0x00008108L, "PidLidTaskAccepted");
        myMap.put(0x00008109L, "PidLidTaskDeadOccurrence");
        myMap.put(0x0000810fL, "PidLidTaskDateCompleted");
        myMap.put(0x00008110L, "PidLidTaskActualEffort");
        myMap.put(0x00008112L, "PidLidTaskVersion");
        myMap.put(0x00008115L, "PidLidTaskLastUpdate");
        myMap.put(0x00008117L, "PidLidTaskAssigners");
        myMap.put(0x00008119L, "PidLidTaskStatusOnComplete");
        myMap.put(0x0000811aL, "PidLidTaskHistory");
        myMap.put(0x0000811bL, "PidLidTaskUpdates");
        myMap.put(0x0000811cL, "PidLidTaskComplete");
        myMap.put(0x0000811eL, "PidLidTaskFCreator");
        myMap.put(0x00008121L, "PidLidTaskAssigner");
        myMap.put(0x00008122L, "PidLidTaskLastUser");
        myMap.put(0x00008123L, "PidLidTaskOrdinal");
        myMap.put(0x00008124L, "PidLidTaskNoCompute");
        myMap.put(0x00008125L, "PidLidTaskLastDelegate");
        myMap.put(0x00008127L, "PidLidTaskRole");
        myMap.put(0x0000812aL, "PidLidTaskAcceptanceState");
        myMap.put(0x00008139L, "PidLidTaskCustomFlags");
        myMap.put(0x0000823eL, "PidLidTrustRecipientHighlights");
        PSETID_TaskByLidMap = Collections.unmodifiableMap(myMap);
    }

    static {
        Map<Long, String> myMap = new HashMap<Long, String> ();
        PSETID_AddressByLidMap = Collections.unmodifiableMap(myMap);
    }

    static {
        Map<Long, String> myMap = new HashMap<Long, String> ();
        myMap.put(0x00009000L, "PidLidCategories");
        PS_PUBLIC_STRINGSByLidMap = Collections.unmodifiableMap(myMap);
    }

    static int xmlIndentLevel = 0;

    private static void usage() {
        // System.err.println("Usage: java com.zimbra.sxtnef2xml.Main [-t <tnef file>]");
        System.err.println("Usage: java -jar sxtnef2xml.jar [-t <tnef file>] | <tnef file>...");
        System.err.println("    \"-t\" option --> Xml-like output for single TNEF file is");
        System.err.println("                      sent to System.out");
        System.err.println("    Otherwise, output is placed in files with the same name");
        System.err.println("    as the TNEF files plus a \".xml\" suffix");
        System.exit(1);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        File tnefFile = null;

        if (args.length == 0) {
            usage();
        } else if ((args.length == 2) && (args[0].equals("-t"))) {
            tnefFile = new File(args[1]);
            writeXmlToStream(tnefFile, System.out);
        } else {
            String xmlFimeName;
            for (int i = 0; i < args.length; ++i) {
                tnefFile = new File(args[i]);
                String xmlFileName = new String(tnefFile + ".xml");
                PrintStream ps = null;
                FileOutputStream wout = null;
                try {
                    wout = new FileOutputStream(xmlFileName);
                    ps = new PrintStream(wout, false);
                    writeXmlToStream(tnefFile, ps);
                    wout.flush();
                } catch (IOException ex) {
                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                } finally {
                    if (wout != null) {
                        try {
                            wout.close();
                        } catch (IOException ex) {
                            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            }
        }
    }

    public static void writeXmlToStream(File tnefFile, PrintStream outStream) {
        if (tnefFile != null) {
            FileInputStream tnefInput = null;
            try {
                tnefInput = new FileInputStream(tnefFile);
                TNEFInputStream tnefStream = null;
                Message tnefView = null;

                tnefStream = new TNEFInputStream(tnefInput);
                tnefView = new Message(tnefStream);
                xmlIndentLevel = 0;
                outStream.println(toXmlStringBuffer(tnefView));
            } catch (IOException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                if (tnefInput != null) {
                    try {
                        tnefInput.close();
                    } catch (IOException ex) {
                        Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
    }

    /**
     * Provide an Xml-like representation of the scheduling
     * view of the TNEF Message
     *
     * @param tnef
     * @return
     * @throws IOException if an I/O error occurs
     */
    public static StringBuffer toXmlStringBuffer(Message tnef) throws IOException {
        StringBuffer s = new StringBuffer();
        appendFormattedInfo(s, "<tnef>\n");
        xmlIndentLevel++;
        List <?> attribute = (List <?>) tnef.getAttributes();
        for (Object thisObj : attribute) {
            if (! (thisObj instanceof Attr)) {
                continue;
            }
            Attr thisAtt = (Attr) thisObj;
            String attrId = TNEFUtils.getConstName(thisAtt.getClass(),"att", thisAtt.getID());
            Object o = thisAtt.getValue();

            if (thisAtt.getID() == (Attr.attMAPIProps)) {
                if (o instanceof MAPIProps) {
                    MAPIProps thisPropset = (MAPIProps) o;
                    appendXmlEquiv(s, thisPropset);
                } else {
                    System.err.println("Parsing issue - attMAPIProps attribute missing MAPIProps value");
                }
            } else if (o instanceof MAPIProps[]) {
                // e.g. Used for attRecipTable
                MAPIProps[] mpArray = (MAPIProps[]) o;
                appendFormattedInfo(s, "<mapi_prop_array tnef_id=\"%s\" len=\"%s\">\n",
                        attrId, new Integer(mpArray.length));
                xmlIndentLevel++;
                for (int mp = 0; mp < mpArray.length; mp++) {
                    appendXmlEquiv(s, mpArray[mp]);
                }
                xmlIndentLevel--;
                appendFormattedInfo(s, "</mapi_prop_array>\n");
            } else {
                appendFormattedInfo(s, "<tnef_attribute id=\"%s\" type=\"%s\" level=\"%s\" length=\"%s\">\n",
                        attrId,
                        TNEFUtils.getConstName(thisAtt.getClass(),"atp",thisAtt.getType()),
                        thisAtt.getLevel(),
                        thisAtt.getLength());
                xmlIndentLevel++;
                appendFormattedInfo(s, "<value>%s</value>\n", thisAtt.getValue());
                xmlIndentLevel--;
                appendFormattedInfo(s, "</tnef_attribute>\n");
            }
        }
        List <?> attaches = (List <?>) tnef.getAttachments();
        for (Object thisObj : attaches) {
            if (! (thisObj instanceof Attachment)) {
                continue;
            }
            Attachment currAttach = (Attachment) thisObj;
            String fnam = currAttach.getFilename();
            xmlIndentLevel++;
            if (fnam == null) {
                appendFormattedInfo(s, "<attachment>\n");
            } else {
                appendFormattedInfo(s, "<attachment filename=\"%s\">\n", fnam);
            }
            List <?> attAttribs = (List <?>) currAttach.getAttributes();
            for (Object attribObj : attAttribs) {
                if (! (attribObj instanceof Attr)) {
                    continue;
                }
                Attr attAttrib = (Attr) attribObj;
                String attAttribId = TNEFUtils.getConstName(attAttrib.getClass(),"att", attAttrib.getID());
                appendFormattedInfo(s, "<tnef_attribute id=\"%s\" type=\"%s\" level=\"%s\" length=\"%s\">\n",
                        attAttribId,
                        TNEFUtils.getConstName(attAttrib.getClass(),"atp",attAttrib.getType()),
                        attAttrib.getLevel(),
                        attAttrib.getLength());
                xmlIndentLevel++;
                appendFormattedInfo(s, "<value>%s</value>\n", attAttrib.getValue());
                xmlIndentLevel--;
                appendFormattedInfo(s, "</tnef_attribute>\n");
            }

            MAPIProps attachMPs = currAttach.getMAPIProps();
            appendXmlEquiv(s, attachMPs);
            xmlIndentLevel--;
            appendFormattedInfo(s, "</attachment>\n");
        }
        xmlIndentLevel--;
        appendFormattedInfo(s, "</tnef>\n");
        return (s);
    }

    private static void appendXmlEquiv(StringBuffer s, MAPIProps thisPropset) throws IOException {
        MAPIProp props[] = thisPropset.getProps();
        if (props.length > 1) {
            appendFormattedInfo(s, "<mapi_prop_list len=\"%s\">\n", new Integer(props.length));
            xmlIndentLevel++;
        }
        for (MAPIProp mp : props) {
            appendXmlEquiv(s, mp);
        }
        if (props.length > 1) {
            xmlIndentLevel--;
            appendFormattedInfo(s, "</mapi_prop_list>\n", new Integer(props.length));
        }
    }

    private static void appendXmlEquiv(StringBuffer s, MAPIProp mp) throws IOException {
        appendFormattedInfo(s, "<mapiprop type=\"%s\">\n",
                TNEFUtils.getConstName(mp.getClass(),"PT_",mp.getType()));
        xmlIndentLevel++;
        MAPIPropName pName = mp.getName();
        if (pName != null) {
            GUID guid = pName.getGUID();
            MSGUID msGuid = new MSGUID(guid);
            String idName = pName.getName();
            StringBuffer nameIdHex = new StringBuffer("0x");
            long lid = pName.getID();
            nameIdHex.append(Long.toHexString(pName.getID()));
            String canonName = null;
            if (idName != null) {
                appendFormattedInfo(s, "<name guid=\"%s\" idname=\"%s\" lid=\"%s\">\n",
                        msGuid, idName, nameIdHex);
            } else {
                if (msGuid.toString().equals(MSGUID.PSETID_Appointment.toString())) {
                    canonName = PSETID_AppointmentByLidMap.get(lid);
                } else if (msGuid.toString().equals(MSGUID.PSETID_Meeting.toString())) {
                    canonName = PSETID_MeetingByLidMap.get(lid);
                } else if (msGuid.toString().equals(MSGUID.PSETID_Common.toString())) {
                    canonName = PSETID_CommonByLidMap.get(lid);
                } else if (msGuid.toString().equals(MSGUID.PSETID_Task.toString())) {
                    canonName = PSETID_TaskByLidMap.get(lid);
                } else if (msGuid.toString().equals(MSGUID.PSETID_Address.toString())) {
                    canonName = PSETID_AddressByLidMap.get(lid);
                } else if (msGuid.toString().equals(MSGUID.PS_PUBLIC_STRINGS.toString())) {
                    canonName = PS_PUBLIC_STRINGSByLidMap.get(lid);
                }
                if (canonName == null) {
                    appendFormattedInfo(s, "<name guid=\"%s\" lid=\"%s\">\n",
                            msGuid, nameIdHex);
                } else {
                    appendFormattedInfo(s, "<name guid=\"%s\" lid=\"%s\" tag=\"%s\">\n",
                            msGuid, nameIdHex, canonName);
                }
            }
        } else {
            int mapiPropId = mp.getID();
            StringBuffer idHex = new StringBuffer("0x");
            idHex.append(Integer.toHexString(mapiPropId));
            String constName = TNEFUtils.getConstName(mp.getClass(),"PR_", mapiPropId);
            String canonName = propertyIdMap.get(mapiPropId);
            if (constName.equals(idHex.toString())) {
                if (canonName == null) {
                    appendFormattedInfo(s, "<id>%s</id>\n", constName);
                } else {
                    appendFormattedInfo(s, "<id tag=\"%s\">%s</id>\n", canonName, idHex);
                }
            } else {
                if (canonName == null) {
                    appendFormattedInfo(s, "<id define=\"%s\">%s</id>\n", constName, idHex);
                } else {
                    appendFormattedInfo(s, "<id define=\"%s\" tag=\"%s\">%s</id>\n", constName, canonName, idHex);
                }
            }
        }
        if (mp.getLength() == 0) {
            appendFormattedInfo(s, "<value></value>\n");
        } else if (mp.getLength() == 1) {
            StringBuffer valHex = null;
            Object theVal = mp.getValues()[0].getValue();
            if (theVal instanceof Integer) {
                Integer intVal = (Integer) theVal;
                if ((intVal > 9) || (intVal < 0)) {
                    valHex = new StringBuffer("0x");
                    valHex.append(Integer.toHexString(intVal));
                }
            } else if (theVal instanceof Long) {
                Long intVal = (Long) theVal;
                if ((intVal > 9) || (intVal < 0)) {
                    valHex = new StringBuffer("0x");
                    valHex.append(Long.toHexString(intVal));
                }
            }
            if (theVal instanceof TNEFInputStream) {
                TNEFInputStream tnefSubStream = (TNEFInputStream) theVal;
                Message subTnefView = new Message(tnefSubStream);
                appendFormattedInfo(s, "<value>\n");
                xmlIndentLevel++;
                s.append(toXmlStringBuffer(subTnefView));
                xmlIndentLevel--;
                appendFormattedInfo(s, "</value>\n");
            } else if (valHex == null) {
                appendFormattedInfo(s, "<value>%s</value>\n", theVal);
            } else {
                appendFormattedInfo(s, "<value hex=\"%s\">%s</value>\n", valHex, theVal);
            }
        } else {
            appendFormattedInfo(s, "<values>\n");
            xmlIndentLevel++;
            for (MAPIValue mapiVal : mp.getValues()) {
                StringBuffer valHex = null;
                Object theVal = mapiVal.getValue();
                if (theVal instanceof Integer) {
                    Integer intVal = (Integer) theVal;
                    if ((intVal > 9) || (intVal < 0)) {
                        valHex = new StringBuffer("0x");
                        valHex.append(Integer.toHexString(intVal));
                    }
                } else if (theVal instanceof Long) {
                    Long intVal = (Long) theVal;
                    if ((intVal > 9) || (intVal < 0)) {
                        valHex = new StringBuffer("0x");
                        valHex.append(Long.toHexString(intVal));
                    }
                }
                if (valHex == null) {
                    appendFormattedInfo(s, "<value>%s</value>\n", theVal);
                } else {
                    appendFormattedInfo(s, "<value hex=\"%s\">%s</value>\n", valHex, theVal);
                }
            }
            xmlIndentLevel--;
            appendFormattedInfo(s, "</values>\n");
        }
        xmlIndentLevel--;
        appendFormattedInfo(s, "</mapiprop>\n");
    }

	/**
	 * @param s
	 * @param format
	 * @param objects
	 */
    private static void appendFormattedInfo(StringBuffer s, String format, Object ... objects) {
        for (int i = 0; i < xmlIndentLevel; ++i) {
            s.append("  ");
        }
        s.append(String.format(format, objects));
    }
}
