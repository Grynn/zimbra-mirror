/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2009, 2010 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.3 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.cs.offline.util.ymail;

import java.util.Map;
import java.util.HashMap;

/**
 * Bounce to Drafts if not retriable. (Look at zimbra size stuff).
 */
public enum YMailError {
    NICKNAME_EXPANSION_FAILED("NicknameExpansionFailed"),
    INVALID_STATIONARY_URL("InvalidStationaryURL"),
    ADDRESS_BOOK_FAILURE("AddressBookFailure"),
    PARENTAL_CONTROL_UNAUTHORIZED_RECIPIENTS("ParentalControlUnauthorizedRecipients"),
    USER_SUSPENDED("UserSuspended"),
    VIRUS_DETECTED("VirusDetected"),
    VIRUS_SCAN_FAILED("VirusScanFailed"),
    MESSAGE_TOO_LARGE_SOME_RECIPIENTS("MessageTooLargeSomeRecipients"),
    SEND_MESSAGE_FAILED_SOME_RECIPIENTS("SendMessageFailedSomeRecipients"),
    MESSAGE_TOO_LARGE("MessageTooLarge"),
    SEND_MESSAGE_FAILED("SendMessageFailed"),
    FOLDER_FETCH_FAILED("FolderFetchFailed"),
    USER_ABUSE_DETECTED("UserAbuseDetected"),
    SAVE_MESSAGE_FAILED("SaveMessageFailed"),
    NO_RECIPIENTS_SPECIFIED("NoRecipientsSpecified"),
    INVALID_RECIPIENT_ADDRESS("InvalidRecipientAddress"),
    INVALID_FROM_ADDRESS("InvalidFromAddress"),
    PARENTAL_CONTROL_NO_ATTACHMENTS("ParentalControlNoAttachments"),
    EXCEEDED_ATTACHMENT_LIMIT("ExceededAttachmentLimit"),
    MESSAGE_COMPOSE_FAILED("MessageComposeFailed"),
    SEND_MESSAGE_ATTACHMENT_FAILED("SendMessageAttachmentFailed"),

    // Spam related

    EXCEEDED_MAX_TO("ExceededMaxTo"),
    EXCEEDED_MAX_CC("ExceededMaxCc"),
    EXCEEDED_MAX_BCC("ExceededMaxBcc"),
    EXCEEDED_MAX_RECIPIENTS("ExceededMaxRecipients"),
    EXCEEDED_MAX_HOURLY_MESSAGES("ExceededMaxHourlyMessages"),
    EXCEEDED_MAX_DAILY_MESSAGES("ExceededMaxDailyMessages"),
    CANNOT_SEND_THIS_MESSAGE("CannotSendThisMessage"),
    ACCOUNT_VERIFICATION_REQUIRED("AccountVerificationRequired"),
    ERROR_SENDING_MESSAGE("ErrorSendingMessage"),

    // Captcha related
    
    HUMAN_VERIFICATION_REQUIRED("HumanVerificationRequired"),
    WRONG_INPUT("WrongInput"),
    TIMED_OUT("TimedOut"),
    SERVER_ERROR("ServerError"),
    MAX_ATTEMPTS("MaxAttempts"),
    WRONG_USER("WrongUser"),
    INVALID_YID("InvalidYID"),
    INPUT_TOO_EARLY("InputTooEarly");

    private final String name;

    private YMailError(String name) {
        this.name = name;
    }
    
    private static final Map<String, YMailError> byName = new HashMap<String, YMailError>();

    static {
        for (YMailError ec : values()) {
            byName.put(ec.name.toLowerCase(), ec);
        }
    }

    public static YMailError fromName(String name) {
        return byName.get(name.toLowerCase());
    }

    public static boolean isRetriable(String faultCode) {
        YMailError code = fromFaultCode(faultCode);
        return code != null && code.isRetriable();
    }
    
    public static YMailError fromFaultCode(String faultCode) {
        if (faultCode != null) {
            int i = faultCode.lastIndexOf(".");
            if (i != -1) {
                return fromName(faultCode.substring(i + 1).toLowerCase());
            }
        }
        return null;
    }


    public boolean isRetriable() {
        switch (this) {
        case NICKNAME_EXPANSION_FAILED:
        case ADDRESS_BOOK_FAILURE:
        case VIRUS_SCAN_FAILED:
        case SEND_MESSAGE_FAILED_SOME_RECIPIENTS:
        case SEND_MESSAGE_FAILED:
        case MESSAGE_COMPOSE_FAILED:
        case SEND_MESSAGE_ATTACHMENT_FAILED:
        case ERROR_SENDING_MESSAGE:
        case TIMED_OUT:
        case SERVER_ERROR:
            return true;
        default:
            return false;
        }
    }
}
