package CamelBones::Foundation::Constants;

use strict;
use warnings;

require Exporter;

our @ISA = qw(Exporter);

our @EXPORT = qw(
    NS_UnknownByteOrder NS_LittleEndian NS_BigEndian

    NSCalculationNoError NSCalculationLossOfPrecision NSCalculationUnderflow
    NSCalculationOverflow NSCalculationDivideByZero
    
    NSEraCalendarUnit NSYearCalendarUnit NSMonthCalendarUnit NSDayCalendarUnit
    NSHourCalendarUnit NSMinuteCalendarUnit NSSecondCalendarUnit NSWeekCalendarUnit
    NSWeekdayCalendarUnit NSWeekdayOrdinalCalendarUnit

    NSDirectPredicateModifier NSAllPredicateModifier NSAnyPredicateModifier
    NSCaseInsensitivePredicateOption NSDiacriticInsensitivePredicateOption
    NSNotPredicateType NSAndPredicateType NSOrPredicateType

    NSOrderedAscending NSOrderedSame NSOrderedDescending
    
    NSConstantValueExpressionType NSEvaluatedObjectExpressionType NSVariableExpressionType
    NSKeyPathExpressionType NSFunctionExpressionType

    NSHTTPCookieAcceptPolicyAlways NSHTTPCookieAcceptPolicyNever NSHTTPCookieAcceptPolicyOnlyFromMainDocumentDomain

    NSPositionAfter NSPositionBefore NSPositionBeginning NSPositionEnd NSPositionReplace

    NSKeyValueChangeSetting NSKeyValueChangeInsertion NSKeyValueChangeRemoval
    NSKeyValueChangeReplacement

    NSKeyValueUnionSetMutation NSKeyValueMinusSetMutation NSKeyValueIntersectSetMutation
    NSKeyValueSetSetMutation
    
    NSNetServicesUnknownError NSNetServicesCollisionError NSNetServicesNotFoundError
    NSNetServicesActivityInProgressError NSNetServicesBadArgumentError NSNetServicesCancelledError
    NSNetServicesInvalidError NSNetServicesTimeoutError
    
    NSNotificationNoCoalescing NSNotificationCoalescingOnName NSNotificationCoalescingOnSender

    NSNotificationSuspensionBehaviorDrop NSNotificationSuspensionBehaviorCoalesce
    NSNotificationSuspensionBehaviorHold NSNotificationSuspensionBehaviorDeliverImmediately
    
    NSPostWhenIdle NSPostASAP NSPostNow

    NSLessThanPredicateOperatorType NSLessThanOrEqualToPredicateOperatorType
    NSGreaterThanPredicateOperatorType NSGreaterThanOrEqualToPredicateOperatorType
    NSEqualToPredicateOperatorType NSNotEqualToPredicateOperatorType
    NSMatchesPredicateOperatorType NSLikePredicateOperatorType NSBeginsWithPredicateOperatorType
    NSEndsWithPredicateOperatorType NSInPredicateOperatorType NSCustomSelectorPredicateOperatorType

    NSPropertyListOpenStepFormat NSPropertyListXMLFormat_v1_0 NSPropertyListBinaryFormat_v1_0

    NSPropertyListImmutable NSPropertyListMutableContainers NSPropertyListMutableContainersAndLeaves

    NSMinXEdge NSMinYEdge NSMaxXEdge NSMaxYEdge

    NSRelativeAfter NSRelativeBefore
    
    NSRoundPlain NSRoundDown NSRoundUp NSRoundBankers
    
    NSSaveOptionsYes NSSaveOptionsNo NSSaveOptionsAsk

    NSApplicationDirectory NSDemoApplicationDirectory NSDeveloperApplicationDirectory
    NSAdminApplicationDirectory NSLibraryDirectory NSDeveloperDirectory
    NSUserDirectory NSDocumentationDirectory NSDocumentDirectory NSCoreServiceDirectory
    NSDesktopDirectory NSCachesDirectory NSApplicationSupportDirectory
    NSAllApplicationsDirectory NSAllLibrariesDirectory

    NSUserDomainMask NSLocalDomainMask NSNetworkDomainMask NSSystemDomainMask 
    NSAllDomainsMask 
    
    NSStreamEventNone NSStreamEventOpenCompleted NSStreamEventHasBytesAvailable
    NSStreamEventHasSpaceAvailable NSStreamEventErrorOccurred NSStreamEventEndEncountered

    NSStreamStatusNotOpen NSStreamStatusOpening NSStreamStatusOpen NSStreamStatusReading
    NSStreamStatusWriting NSStreamStatusAtEnd NSStreamStatusClosed NSStreamStatusError

    NSEqualToComparison NSLessThanOrEqualToComparison NSLessThanComparison 
    NSGreaterThanOrEqualToComparison NSGreaterThanComparison NSBeginsWithComparison 
    NSEndsWithComparison NSContainsComparison 
    
    NSURLCacheStorageAllowed NSURLCacheStorageAllowedInMemoryOnly NSURLCacheStorageNotAllowed

    NSURLCredentialPersistenceNone NSURLCredentialPersistenceForSession
    NSURLCredentialPersistencePermanent

    NSURLHandleNotLoaded NSURLHandleLoadSucceeded NSURLHandleLoadInProgress 
    NSURLHandleLoadFailed 
    
    NSURLRequestUseProtocolCachePolicy NSURLRequestReloadIgnoringCacheData
    NSURLRequestReturnCacheDataElseLoad NSURLRequestReturnCacheDataDontLoad

    NSIndexSubelement NSEverySubelement NSMiddleSubelement NSRandomSubelement 
    NSNoSubelement 
    
    NSXMLParserInternalError NSXMLParserOutOfMemoryError NSXMLParserDocumentStartError
    NSXMLParserEmptyDocumentError NSXMLParserPrematureDocumentEndError
    NSXMLParserInvalidHexCharacterRefError NSXMLParserInvalidDecimalCharacterRefError
    NSXMLParserInvalidCharacterRefError NSXMLParserInvalidCharacterError
    NSXMLParserCharacterRefAtEOFError NSXMLParserCharacterRefInPrologError
    NSXMLParserCharacterRefInEpilogError NSXMLParserCharacterRefInDTDError
    NSXMLParserEntityRefAtEOFError NSXMLParserEntityRefInPrologError
    NSXMLParserEntityRefInEpilogError NSXMLParserEntityRefInDTDError
    NSXMLParserParsedEntityRefAtEOFError NSXMLParserParsedEntityRefInPrologError
    NSXMLParserParsedEntityRefInEpilogError NSXMLParserParsedEntityRefInInternalSubsetError
    NSXMLParserEntityReferenceWithoutNameError NSXMLParserEntityReferenceMissingSemiError
    NSXMLParserParsedEntityRefNoNameError NSXMLParserParsedEntityRefMissingSemiError
    NSXMLParserUndeclaredEntityError NSXMLParserUnparsedEntityError
    NSXMLParserEntityIsExternalError NSXMLParserEntityIsParameterError
    NSXMLParserUnknownEncodingError NSXMLParserEncodingNotSupportedError
    NSXMLParserStringNotStartedError NSXMLParserStringNotClosedError
    NSXMLParserNamespaceDeclarationError NSXMLParserEntityNotStartedError
    NSXMLParserEntityNotFinishedError NSXMLParserLessThanSymbolInAttributeError
    NSXMLParserAttributeNotStartedError NSXMLParserAttributeNotFinishedError
    NSXMLParserAttributeHasNoValueError NSXMLParserAttributeRedefinedError
    NSXMLParserLiteralNotStartedError NSXMLParserLiteralNotFinishedError
    NSXMLParserCommentNotFinishedError NSXMLParserProcessingInstructionNotStartedError
    NSXMLParserProcessingInstructionNotFinishedError NSXMLParserNotationNotStartedError
    NSXMLParserNotationNotFinishedError NSXMLParserAttributeListNotStartedError
    NSXMLParserAttributeListNotFinishedError NSXMLParserMixedContentDeclNotStartedError
    NSXMLParserMixedContentDeclNotFinishedError NSXMLParserElementContentDeclNotStartedError
    NSXMLParserElementContentDeclNotFinishedError NSXMLParserXMLDeclNotStartedError
    NSXMLParserXMLDeclNotFinishedError NSXMLParserConditionalSectionNotStartedError
    NSXMLParserConditionalSectionNotFinishedError NSXMLParserExternalSubsetNotFinishedError
    NSXMLParserDOCTYPEDeclNotFinishedError NSXMLParserMisplacedCDATAEndStringError
    NSXMLParserCDATANotFinishedError NSXMLParserMisplacedXMLDeclarationError
    NSXMLParserSpaceRequiredError NSXMLParserSeparatorRequiredError
    NSXMLParserNMTOKENRequiredError NSXMLParserNAMERequiredError NSXMLParserPCDATARequiredError
    NSXMLParserURIRequiredError NSXMLParserPublicIdentifierRequiredError
    NSXMLParserLTRequiredError NSXMLParserGTRequiredError NSXMLParserLTSlashRequiredError
    NSXMLParserEqualExpectedError NSXMLParserTagNameMismatchError NSXMLParserUnfinishedTagError
    NSXMLParserStandaloneValueError NSXMLParserInvalidEncodingNameError
    NSXMLParserCommentContainsDoubleHyphenError NSXMLParserInvalidEncodingError
    NSXMLParserExternalStandaloneEntityError NSXMLParserInvalidConditionalSectionError
    NSXMLParserEntityValueRequiredError NSXMLParserNotWellBalancedError
    NSXMLParserExtraContentError NSXMLParserInvalidCharacterInEntityError
    NSXMLParserParsedEntityRefInInternalError NSXMLParserEntityRefLoopError
    NSXMLParserEntityBoundaryError NSXMLParserInvalidURIError NSXMLParserURIFragmentError
    NSXMLParserNoDTDError NSXMLParserDelegateAbortedParseError

    NSFileNoSuchFileError NSFileLockingError NSFileReadUnknownError
    NSFileReadNoPermissionError NSFileReadInvalidFileNameError NSFileReadCorruptFileError
    NSFileReadNoSuchFileError NSFileReadInapplicableStringEncodingError
    NSFileReadUnsupportedSchemeError NSFileWriteUnknownError NSFileWriteNoPermissionError
    NSFileWriteInvalidFileNameError NSFileWriteInapplicableStringEncodingError
    NSFileWriteUnsupportedSchemeError NSFileWriteOutOfSpaceError NSKeyValueValidationError
    NSFormattingError NSUserCancelledError NSFileErrorMinimum NSFileErrorMaximum
    NSValidationErrorMinimum NSValidationErrorMaximum NSFormattingErrorMinimum
    NSFormattingErrorMaximum

    NSKeyValueObservingOptionNew NSKeyValueObservingOptionOld

    NSNotificationDeliverImmediately NSNotificationPostToAllSessions

    NSNotFound 

    NSOpenStepUnicodeReservedBase 

    NSWindowsNTOperatingSystem NSWindows95OperatingSystem NSSolarisOperatingSystem 
    NSHPUXOperatingSystem NSMACHOperatingSystem NSSunOSOperatingSystem NSOSF1OperatingSystem 
    
    NSNoScriptError NSReceiverEvaluationScriptError NSKeySpecifierEvaluationScriptError 
    NSArgumentEvaluationScriptError NSReceiversCantHandleCommandScriptError NSRequiredArgumentsMissingScriptError 
    NSArgumentsWrongScriptError NSUnknownKeyScriptError NSInternalScriptError 
    NSOperationNotSupportedForKeyScriptError NSCannotCreateScriptCommandError 
    
    NSNoSpecifierError NSNoTopLevelContainersSpecifierError NSContainerSpecifierError 
    NSUnknownKeySpecifierError NSInvalidIndexSpecifierError NSInternalSpecifierError 
    NSOperationNotSupportedForKeySpecifierError 

    NSUndoCloseGroupingRunLoopOrdering 

    NSCaseInsensitiveSearch NSLiteralSearch NSBackwardsSearch NSAnchoredSearch 
    
    NSASCIIStringEncoding NSNEXTSTEPStringEncoding NSJapaneseEUCStringEncoding 
    NSUTF8StringEncoding NSISOLatin1StringEncoding NSSymbolStringEncoding NSNonLossyASCIIStringEncoding 
    NSShiftJISStringEncoding NSISOLatin2StringEncoding NSUnicodeStringEncoding 
    NSWindowsCP1251StringEncoding NSWindowsCP1252StringEncoding NSWindowsCP1253StringEncoding 
    NSWindowsCP1254StringEncoding NSWindowsCP1250StringEncoding NSISO2022JPStringEncoding 
    NSMacOSRomanStringEncoding NSProprietaryStringEncoding 

    NSURLErrorUnknown NSURLErrorCancelled NSURLErrorBadURL NSURLErrorTimedOut
    NSURLErrorUnsupportedURL NSURLErrorCannotFindHost NSURLErrorCannotConnectToHost
    NSURLErrorNetworkConnectionLost NSURLErrorDNSLookupFailed NSURLErrorHTTPTooManyRedirects
    NSURLErrorResourceUnavailable NSURLErrorNotConnectedToInternet
    NSURLErrorRedirectToNonExistentLocation NSURLErrorBadServerResponse
    NSURLErrorUserCancelledAuthentication NSURLErrorUserAuthenticationRequired
    NSURLErrorZeroByteResource NSURLErrorFileDoesNotExist NSURLErrorFileIsDirectory
    NSURLErrorNoPermissionsToReadFile NSURLErrorSecureConnectionFailed
    NSURLErrorServerCertificateHasBadDate NSURLErrorServerCertificateUntrusted
    NSURLErrorServerCertificateHasUnknownRoot NSURLErrorServerCertificateNotYetValid
    NSURLErrorClientCertificateRejected NSURLErrorCannotLoadFromNetwork
    NSURLErrorCannotCreateFile NSURLErrorCannotOpenFile NSURLErrorCannotCloseFile
    NSURLErrorCannotWriteToFile NSURLErrorCannotRemoveFile NSURLErrorCannotMoveFile
    NSURLErrorDownloadDecodingFailedMidStream NSURLErrorDownloadDecodingFailedToComplete
);

#
# Typedefs
#

# NSByteOrder
use constant NS_UnknownByteOrder => 0;
use constant NS_LittleEndian => 1;
use constant NS_BigEndian => 2;

# NSCalculationError
use constant NSCalculationNoError => 0;
use constant NSCalculationLossOfPrecision => 1;
use constant NSCalculationUnderflow => 2;
use constant NSCalculationOverflow => 3;
use constant NSCalculationDivideByZero => 4;

# NSCalendarUnit
use constant NSEraCalendarUnit => 1 << 1;
use constant NSYearCalendarUnit => 1 << 2;
use constant NSMonthCalendarUnit => 1 << 3;
use constant NSDayCalendarUnit => 1 << 4;
use constant NSHourCalendarUnit => 1 << 5;
use constant NSMinuteCalendarUnit => 1 << 6;
use constant NSSecondCalendarUnit => 1 << 7;
use constant NSWeekCalendarUnit => 1 << 8;
use constant NSWeekdayCalendarUnit => 1 << 9;
use constant NSWeekdayOrdinalCalendarUnit => 1 << 10;

# NSComparisonPredicateModifier
use constant NSDirectPredicateModifier => 0;
use constant NSAllPredicateModifier => 1;
use constant NSAnyPredicateModifier => 2;

# NSComparisonPredicateOptions
use constant NSCaseInsensitivePredicateOption => 0x01;
use constant NSDiacriticInsensitivePredicateOption => 0x02;

# NSCompoundPredicateType
use constant NSNotPredicateType => 0;
use constant NSAndPredicateType => 1;
use constant NSOrPredicateType => 2;

# NSComparisonResult
use constant NSOrderedAscending => -1;
use constant NSOrderedSame => 0;
use constant NSOrderedDescending => 1;

# NSExpressionType
use constant NSConstantValueExpressionType => 0;
use constant NSEvaluatedObjectExpressionType => 1;
use constant NSVariableExpressionType => 2;
use constant NSKeyPathExpressionType => 3;
use constant NSFunctionExpressionType => 4;

# NSHTTPCookieAcceptPolicy
use constant NSHTTPCookieAcceptPolicyAlways => 0;
use constant NSHTTPCookieAcceptPolicyNever => 1;
use constant NSHTTPCookieAcceptPolicyOnlyFromMainDocumentDomain => 2;

# NSInsertionPosition
use constant NSPositionAfter => 0;
use constant NSPositionBefore => 1;
use constant NSPositionBeginning => 2;
use constant NSPositionEnd => 3;
use constant NSPositionReplace => 4;

# NSKeyValueChange
use constant NSKeyValueChangeSetting => 1;
use constant NSKeyValueChangeInsertion => 2;
use constant NSKeyValueChangeRemoval => 3;
use constant NSKeyValueChangeReplacement => 4;

# NSKeyValueSetMutationKind
use constant NSKeyValueUnionSetMutation => 1;
use constant NSKeyValueMinusSetMutation => 2;
use constant NSKeyValueIntersectSetMutation => 3;
use constant NSKeyValueSetSetMutation => 4;

# NSNetServicesError
use constant NSNetServicesUnknownError => -72000;
use constant NSNetServicesCollisionError => -72001;
use constant NSNetServicesNotFoundError => -72002;
use constant NSNetServicesActivityInProgress => -72003;
use constant NSNetServicesBadArgumentError => -72004;
use constant NSNetServicesCancelledError => -72005;
use constant NSNetServicesInvalidError => -72006;
use constant NSNetServicesTimeoutError => -72007;

# NSNotificationCoalescing
use constant NSNotificationNoCoalescing => 0;
use constant NSNotificationCoalescingOnName => 1;
use constant NSNotificationCoalescingOnSender => 2;

# NSNotificationSuspensionBehavior
use constant NSNotificationSuspensionBehaviorDrop => 1;
use constant NSNotificationSuspensionBehaviorCoalesce => 2;
use constant NSNotificationSuspensionBehaviorHold => 3;
use constant NSNotificationSuspensionBehaviorDeliverImmediately => 4;

# NSPostingStyle
use constant NSPostWhenIdle => 1;
use constant NSPostASAP => 2;
use constant NSPostNow => 3;

# NSPredicateOperatorType
use constant NSLessThanPredicateOperatorType => 0;
use constant NSLessThanOrEqualToPredicateOperatorType => 1;
use constant NSGreaterThanPredicateOperatorType => 2;
use constant NSGreaterThanOrEqualToPredicateOperatorType => 3;
use constant NSEqualToPredicateOperatorType => 4;
use constant NSNotEqualToPredicateOperatorType => 5;
use constant NSMatchesPredicateOperatorType => 6;
use constant NSLikePredicateOperatorType => 7;
use constant NSBeginsWithPredicateOperatorType => 8;
use constant NSEndsWithPredicateOperatorType => 9;
use constant NSInPredicateOperatorType => 10;
use constant NSCustomSelectorPredicateOperatorType => 11;

# NSPropertyListFormat
use constant NSPropertyListOpenStepFormat => 1;
use constant NSPropertyListXMLFormat_v1_0 => 100;
use constant NSPropertyListBinaryFormat_v1_0 => 200;

# NSPropertyListMutabilityOptions
use constant NSPropertyListImmutable => 0;
use constant NSPropertyListMutableContainers => 1;
use constant NSPropertyListMutableContainersAndLeaves => 2;

# NSRectEdge
use constant NSMinXEdge => 0;
use constant NSMinYEdge => 1;
use constant NSMaxXEdge => 2;
use constant NSMaxYEdge => 3;

# NSRelativePosition
use constant NSRelativeAfter => 0;
use constant NSRelativeBefore => 1;

# NSRoundingMode
use constant NSRoundPlain => 0;
use constant NSRoundDown => 1;
use constant NSRoundUp => 2;
use constant NSRoundBankers => 3;

# NSSaveOptions
use constant NSSaveOptionsYes => 0;
use constant NSSaveOptionsNo => 1;
use constant NSSaveOptionsAsk => 2;

# NSSearchPathDirectory
use constant NSApplicationDirectory => 1;
use constant NSDemoApplicationDirectory => 2;
use constant NSDeveloperApplicationDirectory => 3;
use constant NSAdminApplicationDirectory => 4;
use constant NSLibraryDirectory => 5;
use constant NSDeveloperDirectory => 6;
use constant NSUserDirectory => 7;
use constant NSDocumentationDirectory => 8;
use constant NSDocumentDirectory => 9;
use constant NSCoreServiceDirectory => 10;
use constant NSDesktopDirectory => 12;
use constant NSCachesDirectory => 13;
use constant NSApplicationSupportDirectory => 14;
use constant NSAllApplicationsDirectory => 100;
use constant NSAllLibrariesDirectory => 101;

# NSSearchPathDomainMask
use constant NSUserDomainMask => 1;
use constant NSLocalDomainMask => 2;
use constant NSNetworkDomainMask => 4;
use constant NSSystemDomainMask => 8;
use constant NSAllDomainsMask => 0x0ffff;

# NSStreamEvent
use constant NSStreamEventNone => 0;
use constant NSStreamEventOpenCompleted => 1 << 0;
use constant NSStreamEventHasBytesAvailable => 1 << 1;
use constant NSStreamEventHasSpaceAvailable => 1 << 2;
use constant NSStreamEventErrorOccurred => 1 << 3;
use constant NSStreamEventEndEncountered => 1 << 4;

# NSStreamStatus
use constant NSStreamStatusNotOpen => 0;
use constant NSStreamStatusOpening => 1;
use constant NSStreamStatusOpen => 2;
use constant NSStreamStatusReading => 3;
use constant NSStreamStatusWriting => 4;
use constant NSStreamStatusAtEnd => 5;
use constant NSStreamStatusClosed => 6;
use constant NSStreamStatusError => 7;

# NSTestComparisonOperation
use constant NSEqualToComparison => 0;
use constant NSLessThanOrEqualToComparison => 1;
use constant NSLessThanComparison => 2;
use constant NSGreaterThanOrEqualToComparison => 3;
use constant NSGreaterThanComparison => 4;
use constant NSBeginsWithComparison => 5;
use constant NSEndsWithComparison => 6;
use constant NSContainsComparison => 7;

# NSURLCacheStoragePolicy
use constant NSURLCacheStorageAllowed => 0;
use constant NSURLCacheStorageAllowedInMemoryOnly => 1;
use constant NSURLCacheStorageNotAllowed => 2;

# NSURLCredentialPersistence
use constant NSURLCredentialPersistenceNone => 0;
use constant NSURLCredentialPersistenceForSession => 1;
use constant NSURLCredentialPersistencePermanent => 2;

# NSURLHandleStatus
use constant NSURLHandleNotLoaded => 0;
use constant NSURLHandleLoadSucceeded => 1;
use constant NSURLHandleLoadInProgress => 2;
use constant NSURLHandleLoadFailed => 3;

# NSURLRequestCachePolicy
use constant NSURLRequestUseProtocolCachePolicy => 0;
use constant NSURLRequestReloadIgnoringCacheData => 1;
use constant NSURLRequestReturnCacheDataElseLoad => 2;
use constant NSURLRequestReturnCacheDataDontLoad => 3;

# NSWhoseSubelementIdentifier
use constant NSIndexSubelement => 0;
use constant NSEverySubelement => 1;
use constant NSMiddleSubelement => 2;
use constant NSRandomSubelement => 3;
use constant NSNoSubelement => 4;

# NSXMLParserError
use constant NSXMLParserInternalError => 1;
use constant NSXMLParserOutOfMemoryError => 2;
use constant NSXMLParserDocumentStartError => 3;
use constant NSXMLParserEmptyDocumentError => 4;
use constant NSXMLParserPrematureDocumentEndError => 5;
use constant NSXMLParserInvalidHexCharacterRefError => 6;
use constant NSXMLParserInvalidDecimalCharacterRefError => 7;
use constant NSXMLParserInvalidCharacterRefError => 8;
use constant NSXMLParserInvalidCharacterError => 9;
use constant NSXMLParserCharacterRefAtEOFError => 10;
use constant NSXMLParserCharacterRefInPrologError => 11;
use constant NSXMLParserCharacterRefInEpilogError => 12;
use constant NSXMLParserCharacterRefInDTDError => 13;
use constant NSXMLParserEntityRefAtEOFError => 14;
use constant NSXMLParserEntityRefInPrologError => 15;
use constant NSXMLParserEntityRefInEpilogError => 16;
use constant NSXMLParserEntityRefInDTDError => 17;
use constant NSXMLParserParsedEntityRefAtEOFError => 18;
use constant NSXMLParserParsedEntityRefInPrologError => 19;
use constant NSXMLParserParsedEntityRefInEpilogError => 20;
use constant NSXMLParserParsedEntityRefInInternalSubsetError => 21;
use constant NSXMLParserEntityReferenceWithoutNameError => 22;
use constant NSXMLParserEntityReferenceMissingSemiError => 23;
use constant NSXMLParserParsedEntityRefNoNameError => 24;
use constant NSXMLParserParsedEntityRefMissingSemiError => 25;
use constant NSXMLParserUndeclaredEntityError => 26;
use constant NSXMLParserUnparsedEntityError => 28;
use constant NSXMLParserEntityIsExternalError => 29;
use constant NSXMLParserEntityIsParameterError => 30;
use constant NSXMLParserUnknownEncodingError => 31;
use constant NSXMLParserEncodingNotSupportedError => 32;
use constant NSXMLParserStringNotStartedError => 33;
use constant NSXMLParserStringNotClosedError => 34;
use constant NSXMLParserNamespaceDeclarationError => 35;
use constant NSXMLParserEntityNotStartedError => 36;
use constant NSXMLParserEntityNotFinishedError => 37;
use constant NSXMLParserLessThanSymbolInAttributeError => 38;
use constant NSXMLParserAttributeNotStartedError => 39;
use constant NSXMLParserAttributeNotFinishedError => 40;
use constant NSXMLParserAttributeHasNoValueError => 41;
use constant NSXMLParserAttributeRedefinedError => 42;
use constant NSXMLParserLiteralNotStartedError => 43;
use constant NSXMLParserLiteralNotFinishedError => 44;
use constant NSXMLParserCommentNotFinishedError => 45;
use constant NSXMLParserProcessingInstructionNotStartedError => 46;
use constant NSXMLParserProcessingInstructionNotFinishedError => 47;
use constant NSXMLParserNotationNotStartedError => 48;
use constant NSXMLParserNotationNotFinishedError => 49;
use constant NSXMLParserAttributeListNotStartedError => 50;
use constant NSXMLParserAttributeListNotFinishedError => 51;
use constant NSXMLParserMixedContentDeclNotStartedError => 52;
use constant NSXMLParserMixedContentDeclNotFinishedError => 53;
use constant NSXMLParserElementContentDeclNotStartedError => 54;
use constant NSXMLParserElementContentDeclNotFinishedError => 55;
use constant NSXMLParserXMLDeclNotStartedError => 56;
use constant NSXMLParserXMLDeclNotFinishedError => 57;
use constant NSXMLParserConditionalSectionNotStartedError => 58;
use constant NSXMLParserConditionalSectionNotFinishedError => 59;
use constant NSXMLParserExternalSubsetNotFinishedError => 60;
use constant NSXMLParserDOCTYPEDeclNotFinishedError => 61;
use constant NSXMLParserMisplacedCDATAEndStringError => 62;
use constant NSXMLParserCDATANotFinishedError => 63;
use constant NSXMLParserMisplacedXMLDeclarationError => 64;
use constant NSXMLParserSpaceRequiredError => 65;
use constant NSXMLParserSeparatorRequiredError => 66;
use constant NSXMLParserNMTOKENRequiredError => 67;
use constant NSXMLParserNAMERequiredError => 68;
use constant NSXMLParserPCDATARequiredError => 69;
use constant NSXMLParserURIRequiredError => 70;
use constant NSXMLParserPublicIdentifierRequiredError => 71;
use constant NSXMLParserLTRequiredError => 72;
use constant NSXMLParserGTRequiredError => 73;
use constant NSXMLParserLTSlashRequiredError => 74;
use constant NSXMLParserEqualExpectedError => 75;
use constant NSXMLParserTagNameMismatchError => 76;
use constant NSXMLParserUnfinishedTagError => 77;
use constant NSXMLParserStandaloneValueError => 78;
use constant NSXMLParserInvalidEncodingNameError => 79;
use constant NSXMLParserCommentContainsDoubleHyphenError => 80;
use constant NSXMLParserInvalidEncodingError => 81;
use constant NSXMLParserExternalStandaloneEntityError => 82;
use constant NSXMLParserInvalidConditionalSectionError => 83;
use constant NSXMLParserEntityValueRequiredError => 84;
use constant NSXMLParserNotWellBalancedError => 85;
use constant NSXMLParserExtraContentError => 86;
use constant NSXMLParserInvalidCharacterInEntityError => 87;
use constant NSXMLParserParsedEntityRefInInternalError => 88;
use constant NSXMLParserEntityRefLoopError => 89;
use constant NSXMLParserEntityBoundaryError => 90;
use constant NSXMLParserInvalidURIError => 91;
use constant NSXMLParserURIFragmentError => 92;
use constant NSXMLParserNoDTDError => 94;
use constant NSXMLParserDelegateAbortedParseError => 512;

#
# Enums
#

# NSError Codes
use constant NSFileNoSuchFileError => 4;
use constant NSFileLockingError => 255;
use constant NSFileReadUnknownError => 256;
use constant NSFileReadNoPermissionError => 257;
use constant NSFileReadInvalidFileNameError => 258;
use constant NSFileReadCorruptFileError => 259;
use constant NSFileReadNoSuchFileError => 260;
use constant NSFileReadInapplicableStringEncodingError => 261;
use constant NSFileReadUnsupportedSchemeError => 262;
use constant NSFileWriteUnknownError => 512;
use constant NSFileWriteNoPermissionError => 513;
use constant NSFileWriteInvalidFileNameError => 514;
use constant NSFileWriteInapplicableStringEncodingError => 517;
use constant NSFileWriteUnsupportedSchemeError => 518;
use constant NSFileWriteOutOfSpaceError => 640;
use constant NSKeyValueValidationError => 1024;
use constant NSFormattingError => 2048;
use constant NSUserCancelledError => 3072;
use constant NSFileErrorMinimum => 0;
use constant NSFileErrorMaximum => 1023;
use constant NSValidationErrorMinimum => 1024;
use constant NSValidationErrorMaximum => 2047;
use constant NSFormattingErrorMinimum => 2048;
use constant NSFormattingErrorMaximum => 2559;

# Key Value Observing Options
use constant NSKeyValueObservingOptionNew => 0x01;
use constant NSKeyValueObservingOptionOld => 0x02;

# NSDistributedNotification Posting Options
use constant NSNotificationDeliverImmediately => 1 << 0;
use constant NSNotificationPostToAllSessions => 1 << 1;

# NSNotFound
use constant NSNotFound => 0x7fffffff;

# NSOpenStepUnicodeReservedBase
use constant NSOpenStepUnicodeReservedBase => 0xF400;

# NSProcessInfo - Operating Systems
use constant NSWindowsNTOperatingSystem => 1;
use constant NSWindows95OperatingSystem => 2;
use constant NSSolarisOperatingSystem => 3;
use constant NSHPUXOperatingSystem => 4;
use constant NSMACHOperatingSystem => 5;
use constant NSSunOSOperatingSystem => 6;
use constant NSOSF1OperatingSystem => 7;

# NSScriptCommand - General Command Execution Errors
use constant NSNoScriptError => 0;
use constant NSReceiverEvaluationScriptError => 1;
use constant NSKeySpecifierEvaluationScriptError => 2;
use constant NSArgumentEvaluationScriptError => 3;
use constant NSReceiversCantHandleCommandScriptError => 4;
use constant NSRequiredArgumentsMissingScriptError => 5;
use constant NSArgumentsWrongScriptError => 6;
use constant NSUnknownKeyScriptError => 7;
use constant NSInternalScriptError => 8;
use constant NSOperationNotSupportedForKeyScriptError => 9;
use constant NSCannotCreateScriptCommandError => 10;

# NSScriptCommand - Specifier Evaluation Errors
use constant NSNoSpecifierError => 0;
use constant NSNoTopLevelContainersSpecifierError => 1;
use constant NSContainerSpecifierError => 2;
use constant NSUnknownKeySpecifierError => 3;
use constant NSInvalidIndexSpecifierError => 4;
use constant NSInternalSpecifierError => 5;
use constant NSOperationNotSupportedForKeySpecifierError => 6;

# NSUndoCloseGroupingRunLoopOrdering
use constant NSUndoCloseGroupingRunLoopOrdering => 350000;

# Search Types
use constant NSCaseInsensitiveSearch => 1;
use constant NSLiteralSearch => 2;
use constant NSBackwardsSearch => 4;
use constant NSAnchoredSearch => 8;

# String Encodings
use constant NSASCIIStringEncoding => 1;
use constant NSNEXTSTEPStringEncoding => 2;
use constant NSJapaneseEUCStringEncoding => 3;
use constant NSUTF8StringEncoding => 4;
use constant NSISOLatin1StringEncoding => 5;
use constant NSSymbolStringEncoding => 6;
use constant NSNonLossyASCIIStringEncoding => 7;
use constant NSShiftJISStringEncoding => 8;
use constant NSISOLatin2StringEncoding => 9;
use constant NSUnicodeStringEncoding => 10;
use constant NSWindowsCP1251StringEncoding => 11;
use constant NSWindowsCP1252StringEncoding => 12;
use constant NSWindowsCP1253StringEncoding => 13;
use constant NSWindowsCP1254StringEncoding => 14;
use constant NSWindowsCP1250StringEncoding => 15;
use constant NSISO2022JPStringEncoding => 21;
use constant NSMacOSRomanStringEncoding => 30;
use constant NSProprietaryStringEncoding => 65536;

# WebFoundation Error Codes
use constant NSURLErrorUnknown => -1;
use constant NSURLErrorCancelled => -999;
use constant NSURLErrorBadURL => -1000;
use constant NSURLErrorTimedOut => -1001;
use constant NSURLErrorUnsupportedURL => -1002;
use constant NSURLErrorCannotFindHost => -1003;
use constant NSURLErrorCannotConnectToHost => -1004;
use constant NSURLErrorNetworkConnectionLost => -1005;
use constant NSURLErrorDNSLookupFailed => -1006;
use constant NSURLErrorHTTPTooManyRedirects => -1007;
use constant NSURLErrorResourceUnavailable => -1008;
use constant NSURLErrorNotConnectedToInternet => -1009;
use constant NSURLErrorRedirectToNonExistentLocation => -1010;
use constant NSURLErrorBadServerResponse => -1011;
use constant NSURLErrorUserCancelledAuthentication => -1012;
use constant NSURLErrorUserAuthenticationRequired => -1013;
use constant NSURLErrorZeroByteResource => -1014;
use constant NSURLErrorFileDoesNotExist => -1100;
use constant NSURLErrorFileIsDirectory => -1101;
use constant NSURLErrorNoPermissionsToReadFile => -1102;
use constant NSURLErrorSecureConnectionFailed => -1200;
use constant NSURLErrorServerCertificateHasBadDate => -1201;
use constant NSURLErrorServerCertificateUntrusted => -1202;
use constant NSURLErrorServerCertificateHasUnknownRoot => -1203;
use constant NSURLErrorServerCertificateNotYetValid => -1204;
use constant NSURLErrorClientCertificateRejected => -1205;
use constant NSURLErrorCannotLoadFromNetwork => -2000;
use constant NSURLErrorCannotCreateFile => -3000;
use constant NSURLErrorCannotOpenFile => -3001;
use constant NSURLErrorCannotCloseFile => -3002;
use constant NSURLErrorCannotWriteToFile => -3003;
use constant NSURLErrorCannotRemoveFile => -3004;
use constant NSURLErrorCannotMoveFile => -3005;
use constant NSURLErrorDownloadDecodingFailedMidStream => -3006;
use constant NSURLErrorDownloadDecodingFailedToComplete => -3007;

# Happy perl
1;
