package CamelBones::AppKit::Constants;

use strict;
use warnings;

require Exporter;

our @ISA = qw(Exporter);

our @EXPORT = qw(
    NSWarningAlertStyle NSInformationalAlertStyle NSCriticalAlertStyle

    NSAnimationEffectDisappearingItemDefault NSAnimationEffectPoof

    NSApplicationDelegateReplySuccess NSApplicationDelegateReplyCancel
    NSApplicationDelegateReplyFailure

    NSTerminateCancel NSTerminateNow NSTerminateLater 

    NSBackingStoreRetained NSBackingStoreNonretained NSBackingStoreBuffered 
    
    NSRoundedBezelStyle NSRegularSquareBezelStyle NSThickSquareBezelStyle
    NSThickerSquareBezelStyle NSDisclosureBezelStyle NSShadowlessSquareBezelStyle
    NSCircularBezelStyle NSTexturedSquareBezelStyle NSHelpButtonBezelStyle

    NSMoveToBezierPathElement NSLineToBezierPathElement NSCurveToBezierPathElement 
    NSClosePathBezierPathElement 

    NSTIFFFileType NSBMPFileType NSGIFFileType NSJPEGFileType NSPNGFileType 
    
    NSNoBorder NSLineBorder NSBezelBorder NSGrooveBorder 

    NSBoxPrimary NSBoxSecondary NSBoxSeparator NSBoxOldStyle 

    NSBrowserNoColumnResizing NSBrowserAutoColumnResizing NSBrowserUserColumnResizing

    NSMomentaryLightButton NSPushOnPushOffButton NSToggleButton NSSwitchButton 
    NSRadioButton NSMomentaryChangeButton NSOnOffButton NSMomentaryPushInButton 
    
    NSCellDisabled NSCellState NSPushInCell NSCellEditable NSChangeGrayCell 
    NSCellHighlighted NSCellLightsByContents NSCellLightsByGray NSChangeBackgroundCell 
    NSCellLightsByBackground NSCellIsBordered NSCellHasOverlappingImage NSCellHasImageHorizontal 
    NSCellHasImageOnLeftOrBottom NSCellChangesContents NSCellIsInsetButton NSCellAllowsMixedState 
    
    NSNoImage NSImageOnly NSImageLeft NSImageRight NSImageBelow NSImageAbove 
    NSImageOverlaps 

    NSMixedState NSOffState NSOnState 

    NSNullCellType NSTextCellType NSImageCellType 

    NSIdentityMappingCharacterCollection NSAdobeCNS1CharacterCollection
    NSAdobeGB1CharacterCollection NSAdobeJapan1CharacterCollection
    NSAdobeJapan2CharacterCollection NSAdobeKorea1CharacterCollection

    NSCompositeClear NSCompositeCopy NSCompositeSourceOver NSCompositeSourceIn 
    NSCompositeSourceOut NSCompositeSourceAtop NSCompositeDestinationOver NSCompositeDestinationIn 
    NSCompositeDestinationOut NSCompositeDestinationAtop NSCompositeXOR NSCompositePlusDarker 
    NSCompositeHighlight NSCompositePlusLighter 

    NSRegularControlSize NSSmallControlSize NSMiniControlSize

    NSDefaultControlTint NSBlueControlTint NSGraphiteControlTint NSClearControlTint

    NSChangeDone NSChangeUndone NSChangeCleared 

    NSDrawerClosedState NSDrawerOpeningState NSDrawerOpenState NSDrawerClosingState 
    
    NSLeftMouseDown NSLeftMouseUp NSRightMouseDown NSRightMouseUp NSMouseMoved 
    NSLeftMouseDragged NSRightMouseDragged NSMouseEntered NSMouseExited NSKeyDown 
    NSKeyUp NSFlagsChanged NSAppKitDefined NSSystemDefined NSApplicationDefined 
    NSPeriodic NSCursorUpdate NSScrollWheel NSOtherMouseDown NSOtherMouseUp NSOtherMouseDragged 
    
    NSFindPanelActionShowFindPanel NSFindPanelActionNext NSFindPanelActionPrevious
    NSFindPanelActionReplaceAll NSFindPanelActionReplace NSFindPanelActionReplaceAndFind
    NSFindPanelActionSetFindString NSFindPanelActionReplaceAllInSelection

    NSFocusRingOnly NSFocusRingBelow NSFocusRingAbove 

    NSFocusRingTypeDefault NSFocusRingTypeNone NSFocusRingTypeExterior

    NSNoFontChangeAction NSViaPanelFontAction NSAddTraitFontAction NSSizeUpFontAction 
    NSSizeDownFontAction NSHeavierFontAction NSLighterFontAction NSRemoveTraitFontAction 
    
    NSGlyphInscribeBase NSGlyphInscribeBelow NSGlyphInscribeAbove NSGlyphInscribeOverstrike 
    NSGlyphInscribeOverBelow 

    NSGlyphLayoutAtAPoint NSGlyphLayoutAgainstAPoint NSGlyphLayoutWithPrevious 
    
    NSGlyphBelow NSGlyphAbove 

    NSGradientNone NSGradientConcaveWeak NSGradientConcaveStrong NSGradientConvexWeak 
    NSGradientConvexStrong 

    NSImageAlignCenter NSImageAlignTop NSImageAlignTopLeft NSImageAlignTopRight 
    NSImageAlignLeft NSImageAlignBottom NSImageAlignBottomLeft NSImageAlignBottomRight 
    NSImageAlignRight 

    NSImageCacheDefault NSImageCacheAlways NSImageCacheBySize NSImageCacheNever

    NSImageFrameNone NSImageFramePhoto NSImageFrameGrayBezel NSImageFrameGroove 
    NSImageFrameButton 

    NSImageInterpolationDefault NSImageInterpolationNone NSImageInterpolationLow 
    NSImageInterpolationHigh 

    NSImageLoadStatusCompleted NSImageLoadStatusCancelled NSImageLoadStatusInvalidData
    NSImageLoadStatusUnexpectedEOF NSImageLoadStatusReadError

    NSImageRepLoadStatusUnknownType NSImageRepLoadStatusReadingHeader
    NSImageRepLoadStatusWillNeedAllData NSImageRepLoadStatusInvalidData
    NSImageRepLoadStatusUnexpectedEOF NSImageRepLoadStatusCompleted

    NSScaleProportionally NSScaleToFit NSScaleNone 

    NSNoInterfaceStyle NSNextStepInterfaceStyle NSWindows95InterfaceStyle 
    NSMacintoshInterfaceStyle 

    NSLayoutLeftToRight NSLayoutRightToLeft 

    NSLayoutNotDone NSLayoutDone NSLayoutCantFit NSLayoutOutOfGlyphs 

    NSLineBreakByWordWrapping NSLineBreakByCharWrapping NSLineBreakByClipping 
    NSLineBreakByTruncatingHead NSLineBreakByTruncatingTail NSLineBreakByTruncatingMiddle 
    
    NSButtLineCapStyle NSRoundLineCapStyle NSSquareLineCapStyle 

    NSMiterLineJoinStyle NSRoundLineJoinStyle NSBevelLineJoinStyle 

    NSLineDoesntMove NSLineMovesLeft NSLineMovesRight NSLineMovesDown NSLineMovesUp 
    
    NSLineSweepLeft NSLineSweepRight NSLineSweepDown NSLineSweepUp

    NSRadioModeMatrix NSHighlightModeMatrix NSListModeMatrix NSTrackModeMatrix 
    
    NSOneByteGlyphPacking NSJapaneseEUCGlyphPacking NSAsciiWithDoubleByteEUCGlyphPacking 
    NSTwoByteGlyphPacking NSFourByteGlyphPacking NSNativeShortGlyphPacking 

    NSOpenGLCPSwapRectangle NSOpenGLCPSwapRectangleEnable NSOpenGLCPRasterizationEnable
    NSOpenGLCPSwapInterval NSOpenGLCPSurfaceOrder NSOpenGLCPSurfaceOpacity
    NSOpenGLCPStateValidation

    NSOpenGLGOFormatCacheSize NSOpenGLGOClearFormatCache NSOpenGLGORetainRenderers 
    NSOpenGLGOResetLibrary 

    NSOpenGLPFAAllRenderers NSOpenGLPFADoubleBuffer NSOpenGLPFAStereo
    NSOpenGLPFAAuxBuffers NSOpenGLPFAColorSize NSOpenGLPFAAlphaSize
    NSOpenGLPFADepthSize NSOpenGLPFAStencilSize NSOpenGLPFAAccumSize
    NSOpenGLPFAMinimumPolicy NSOpenGLPFAMaximumPolicy NSOpenGLPFAOffScreen
    NSOpenGLPFAFullScreen NSOpenGLPFASampleBuffers NSOpenGLPFASamples
    NSOpenGLPFAAuxDepthStencil NSOpenGLPFARendererID NSOpenGLPFASingleRenderer
    NSOpenGLPFANoRecovery NSOpenGLPFAAccelerated NSOpenGLPFAClosestPolicy
    NSOpenGLPFARobust NSOpenGLPFABackingStore NSOpenGLPFAMPSafe NSOpenGLPFAWindow
    NSOpenGLPFAMultiScreen NSOpenGLPFACompliant NSOpenGLPFAScreenMask
    NSOpenGLPFAPixelBuffer NSOpenGLPFAVirtualScreenCount

    NSPopUpNoArrow NSPopUpArrowAtCenter NSPopUpArrowAtBottom 

    NSPrinterTableOK NSPrinterTableNotFound NSPrinterTableError 

    NSPortraitOrientation NSLandscapeOrientation 

    NSDescendingPageOrder NSSpecialPageOrder NSAscendingPageOrder NSUnknownPageOrder 
    
    NSAutoPagination NSFitPagination NSClipPagination 

    NSProgressIndicatorBarStyle NSProgressIndicatorSpinningStyle

    NSProgressIndicatorPreferredThickness NSProgressIndicatorPreferredSmallThickness 
    NSProgressIndicatorPreferredLargeThickness NSProgressIndicatorPreferredAquaThickness 
    
    NSQTMovieNormalPlayback NSQTMovieLoopingPlayback NSQTMovieLoopingBackAndForthPlayback 
    
    NSCriticalRequest NSInformationalRequest 

    NSHorizontalRuler NSVerticalRuler 

    NSSaveOperation NSSaveAsOperation NSSaveToOperation 

    NSScrollerArrowsDefaultSetting NSScrollerArrowsNone 

    NSScrollerIncrementArrow NSScrollerDecrementArrow 

    NSScrollerNoPart NSScrollerDecrementPage NSScrollerKnob NSScrollerIncrementPage 
    NSScrollerDecrementLine NSScrollerIncrementLine NSScrollerKnobSlot 

    NSSegmentSwitchTrackingSelectOne NSSegmentSwitchTrackingSelectAny
    NSSegmentSwitchTrackingMomentary

    NSSelectionAffinityUpstream NSSelectionAffinityDownstream 

    NSDirectSelection NSSelectingNext NSSelectingPrevious 

    NSSelectByCharacter NSSelectByWord NSSelectByParagraph 

    NSLinearSlider NSCircularSlider

    NSSelectedTab NSBackgroundTab NSPressedTab 

    NSTopTabsBezelBorder NSLeftTabsBezelBorder NSBottomTabsBezelBorder NSRightTabsBezelBorder 
    NSNoTabsBezelBorder NSNoTabsLineBorder NSNoTabsNoBorder 

    NSTableViewDropOn NSTableViewDropAbove 

    NSLeftTextAlignment NSRightTextAlignment NSCenterTextAlignment NSJustifiedTextAlignment 
    NSNaturalTextAlignment 

    NSTextFieldSquareBezel NSTextFieldRoundedBezel

    NSLeftTabStopType NSRightTabStopType NSCenterTabStopType NSDecimalTabStopType 
    
    NSTickMarkBelow NSTickMarkAbove NSTickMarkLeft NSTickMarkRight 

    NSTIFFCompressionNone NSTIFFCompressionCCITTFAX3 NSTIFFCompressionCCITTFAX4 
    NSTIFFCompressionLZW NSTIFFCompressionJPEG NSTIFFCompressionNEXT NSTIFFCompressionPackBits 
    NSTIFFCompressionOldJPEG 

    NSNoTitle NSAboveTop NSAtTop NSBelowTop NSAboveBottom NSAtBottom NSBelowBottom 
    
    NSToolbarDisplayModeDefault NSToolbarDisplayModeIconAndLabel NSToolbarDisplayModeIconOnly 
    NSToolbarDisplayModeLabelOnly 

    NSToolbarSizeModeDefault NSToolbarSizeModeRegular NSToolbarSizeModeSmall

    NSTypesetterLatestBehavior NSTypesetterOriginalBehavior NSTypesetterBehavior_10_2_WithCompatibility
    NSTypesetterBehavior_10_2 NSTypesetterBehavior_10_3

    NSNoScrollerParts NSOnlyScrollerArrows NSAllScrollerParts 

    NSNonZeroWindingRule NSEvenOddWindingRule 

    NSWindowCloseButton NSWindowMiniaturizeButton NSWindowZoomButton
    NSWindowToolbarButton NSWindowDocumentIconButton

    NSWindowAbove NSWindowBelow NSWindowOut 

    NSWritingDirectionNatural NSWritingDirectionLeftToRight NSWritingDirectionRightToLeft

    NSAlertFirstButtonReturn NSAlertSecondButtonReturn NSAlertThirdButtonReturn

    NSRunStoppedResponse NSRunAbortedResponse NSRunContinuesResponse 

    NSUnderlinePatternSolid NSUnderlinePatternDot NSUnderlinePatternDash
    NSUnderlinePatternDashDot NSUnderlinePatternDashDotDot

    NSUnderlineStyleNone NSUnderlineStyleSingle NSUnderlineStyleThick
    NSUnderlineStyleDouble

    NSAnyType NSIntType NSPositiveIntType NSFloatType NSPositiveFloatType 
    NSDoubleType NSPositiveDoubleType 

    NSNoCellMask NSContentsCellMask NSPushInCellMask NSChangeGrayCellMask 
    NSChangeBackgroundCellMask 

    NSGrayModeColorPanel NSRGBModeColorPanel NSCMYKModeColorPanel NSHSBModeColorPanel 
    NSCustomPaletteModeColorPanel NSColorListModeColorPanel NSWheelModeColorPanel
    NSCrayonModeColorPanel
    
    NSColorPanelGrayModeMask NSColorPanelRGBModeMask NSColorPanelCMYKModeMask 
    NSColorPanelHSBModeMask NSColorPanelCustomPaletteModeMask NSColorPanelColorListModeMask 
    NSColorPanelWheelModeMask NSColorPanelAllModesMask 

    NSDragOperationNone NSDragOperationCopy NSDragOperationLink NSDragOperationGeneric 
    NSDragOperationPrivate NSDragOperationMove NSDragOperationDelete NSDragOperationEvery 
    
    NSLeftMouseDownMask NSLeftMouseUpMask NSRightMouseDownMask NSRightMouseUpMask 
    NSMouseMovedMask NSLeftMouseDraggedMask NSRightMouseDraggedMask NSMouseEnteredMask 
    NSMouseExitedMask NSKeyDownMask NSKeyUpMask NSFlagsChangedMask NSAppKitDefinedMask 
    NSSystemDefinedMask NSApplicationDefinedMask NSPeriodicMask NSCursorUpdateMask 
    NScrollWheelMask NSOtherMouseDownMask NSOtherMouseUpMask NSOtherMouseDraggedMask 
    NSAnyEventMask 

    NSUpArrowFunctionKey NSDownArrowFunctionKey NSLeftArrowFunctionKey NSRightArrowFunctionKey 
    NSF1FunctionKey NSF2FunctionKey NSF3FunctionKey NSF4FunctionKey NSF5FunctionKey 
    NSF6FunctionKey NSF7FunctionKey NSF8FunctionKey NSF9FunctionKey NSF10FunctionKey 
    NSF11FunctionKey NSF12FunctionKey NSF13FunctionKey NSF14FunctionKey NSF15FunctionKey 
    NSF16FunctionKey NSF17FunctionKey NSF18FunctionKey NSF19FunctionKey NSF20FunctionKey 
    NSF21FunctionKey NSF22FunctionKey NSF23FunctionKey NSF24FunctionKey NSF25FunctionKey 
    NSF26FunctionKey NSF27FunctionKey NSF28FunctionKey NSF29FunctionKey NSF30FunctionKey 
    NSF31FunctionKey NSF32FunctionKey NSF33FunctionKey NSF34FunctionKey NSF35FunctionKey 
    NSInsertFunctionKey NSDeleteFunctionKey NSHomeFunctionKey NSBeginFunctionKey 
    NSEndFunctionKey NSPageUpFunctionKey NSPageDownFunctionKey NSPrintScreenFunctionKey 
    NSScrollLockFunctionKey NSPauseFunctionKey NSSysReqFunctionKey NSBreakFunctionKey 
    NSResetFunctionKey NSStopFunctionKey NSMenuFunctionKey NSUserFunctionKey 
    NSSystemFunctionKey NSPrintFunctionKey NSClearLineFunctionKey NSClearDisplayFunctionKey 
    NSInsertLineFunctionKey NSDeleteLineFunctionKey NSInsertCharFunctionKey NSDeleteCharFunctionKey 
    NSPrevFunctionKey NSNextFunctionKey NSSelectFunctionKey NSExecuteFunctionKey 
    NSUndoFunctionKey NSRedoFunctionKey NSFindFunctionKey NSHelpFunctionKey NSModeSwitchFunctionKey 
    
    NSAlphaShiftKeyMask NSShiftKeyMask NSControlKeyMask NSAlternateKeyMask 
    NSCommandKeyMask NSNumericPadKeyMask NSHelpKeyMask NSFunctionKeyMask 

    NSWindowExposedEventType NSApplicationActivatedEventType NSApplicationDeactivatedEventType 
    NSWindowMovedEventType NSScreenChangedEventType NSAWTEventType 

    NSPowerOffEventType 

    NSItalicFontMask NSBoldFontMask NSUnboldFontMask NSNonStandardCharacterSetFontMask 
    NSNarrowFontMask NSExpandedFontMask NSCondensedFontMask NSSmallCapsFontMask 
    NSPosterFontMask NSCompressedFontMask NSFixedPitchFontMask NSUnitalicFontMask 
    
    NSAlphaEqualToData NSAlphaAlwaysOne 

    NSFontCollectionApplicationOnlyMask

    NSFontPanelFaceModeMask NSFontPanelSizeModeMask NSFontPanelCollectionModeMask
    NSFontPanelStandardModesMask NSFontPanelAllModesMask

    NSControlGlyph NSNullGlyph 

    NSShowControlGlyphs NSShowInvisibleGlyphs NSWantsBidiLevels

    NSImageRepMatchesDevice 

    NSOutlineViewDropOnItemIndex 

    NSAlertDefaultReturn NSAlertAlternateReturn NSAlertOtherReturn NSAlertErrorReturn 
    
    NSOKButton NSCancelButton 

    NSUtilityWindowMask NSDocModalWindowMask NSNonactivatingPanelMask

    NSUpdateWindowsRunLoopOrdering 

    NSDisplayWindowRunLoopOrdering NSResetCursorRectsRunLoopOrdering 

    NSFileHandlingPanelImageButton NSFileHandlingPanelTitleField NSFileHandlingPanelBrowser 
    NSFileHandlingPanelCancelButton NSFileHandlingPanelOKButton NSFileHandlingPanelForm 
    
    NSTableViewGridNone NSTableViewSolidVerticalGridLineMask NSTableViewSolidHorizontalGridLineMask

    NSAttachmentCharacter 

    NSParagraphSeparatorCharacter NSLineSeparatorCharacter NSTabCharacter 
    NSFormFeedCharacter NSNewlineCharacter NSCarriageReturnCharacter NSEnterCharacter 
    NSBackspaceCharacter NSBackTabCharacter NSDeleteCharacter 

    NSIllegalTextMovement NSReturnTextMovement NSTabTextMovement NSBacktabTextMovement
    NSLeftTextMovement NSRightTextMovement NSUpTextMovement NSDownTextMovement
    NSCancelTextMovement NSOtherTextMovement

    NSTextStorageEditedAttributes NSTextStorageEditedCharacters 

    NSViewNotSizable NSViewMinXMargin NSViewWidthSizable NSViewMaxXMargin 
    NSViewMinYMargin NSViewHeightSizable NSViewMaxYMargin 

    NSBorderlessWindowMask NSTitledWindowMask NSClosableWindowMask NSMiniaturizableWindowMask
    NSResizableWindowMask NSTexturedBackgroundWindowMask

    NSWorkspaceLaunchAndPrint NSWorkspaceLaunchInhibitingBackgroundOnly
    NSWorkspaceLaunchWithoutAddingToRecents NSWorkspaceLaunchWithoutActivation
    NSWorkspaceLaunchAsync NSWorkspaceLaunchAllowingClassicStartup
    NSWorkspaceLaunchPreferringClassic NSWorkspaceLaunchNewInstance
    NSWorkspaceLaunchAndHide NSWorkspaceLaunchAndHideOthers NSWorkspaceLaunchDefault

    NSTextReadInapplicableDocumentTypeError NSTextWriteInapplicableDocumentTypeError
    NSTextReadWriteErrorMinimum NSTextReadWriteErrorMaximum
);

#
# Typedefs
#

# NSAlertStyle
use constant NSWarningAlertStyle => 0;
use constant NSInformationalAlertStyle => 1;
use constant NSCriticalAlertStyle => 2;

# NSAnimationEffect
use constant NSAnimationEffectDisappearingItemDefault => 0;
use constant NSAnimationEffectPoof => 10;

# NSApplicationDelegateReply
use constant NSApplicationDelegateReplySuccess => 0;
use constant NSApplicationDelegateReplyCancel => 1;
use constant NSApplicationDelegateReplyFailure => 2;

# NSApplicationTerminateReply
use constant NSTerminateCancel => 0;
use constant NSTerminateNow => 1;
use constant NSTerminateLater => 2;

# NSBackingStoreType
use constant NSBackingStoreRetained => 0;
use constant NSBackingStoreNonretained => 1;
use constant NSBackingStoreBuffered => 2;

# NSBezelStyle
use constant NSRoundedBezelStyle => 1;
use constant NSRegularSquareBezelStyle => 2;
use constant NSThickSquareBezelStyle => 3;
use constant NSThickerSquareBezelStyle => 4;
use constant NSDisclosureBezelStyle => 5;
use constant NSShadowlessSquareBezelStyle => 6;
use constant NSCircularBezelStyle => 7;
use constant NSTexturedSquareBezelStyle => 8;
use constant NSHelpButtonBezelStyle => 9;

# NSBezierPathElement
use constant NSMoveToBezierPathElement => 0;
use constant NSLineToBezierPathElement => 1;
use constant NSCurveToBezierPathElement => 2;
use constant NSClosePathBezierPathElement => 3;

# NSBitmapImageFileType
use constant NSTIFFFileType => 0;
use constant NSBMPFileType => 1;
use constant NSGIFFileType => 2;
use constant NSJPEGFileType => 3;
use constant NSPNGFileType => 4;

# NSBorderType
use constant NSNoBorder => 0;
use constant NSLineBorder => 1;
use constant NSBezelBorder => 2;
use constant NSGrooveBorder => 3;

# NSBoxType
use constant NSBoxPrimary => 0;
use constant NSBoxSecondary => 1;
use constant NSBoxSeparator => 2;
use constant NSBoxOldStyle => 3;

# NSBrowserColumnResizingType
use constant NSBrowserNoColumnResizing => 0;
use constant NSBrowserAutoColumnResizing => 1;
use constant NSBrowserUserColumnResizing => 2;

# NSButtonType
use constant NSMomentaryLightButton => 0;
use constant NSPushOnPushOffButton => 1;
use constant NSToggleButton => 2;
use constant NSSwitchButton => 3;
use constant NSRadioButton => 4;
use constant NSMomentaryChangeButton => 5;
use constant NSOnOffButton => 6;
use constant NSMomentaryPushInButton => 7;

# NSCellAttribute
use constant NSCellDisabled => 0;
use constant NSCellState => 1;
use constant NSPushInCell => 2;
use constant NSCellEditable => 3;
use constant NSChangeGrayCell => 4;
use constant NSCellHighlighted => 5;
use constant NSCellLightsByContents => 6;
use constant NSCellLightsByGray => 7;
use constant NSChangeBackgroundCell => 8;
use constant NSCellLightsByBackground => 9;
use constant NSCellIsBordered => 10;
use constant NSCellHasOverlappingImage => 11;
use constant NSCellHasImageHorizontal => 12;
use constant NSCellHasImageOnLeftOrBottom => 13;
use constant NSCellChangesContents => 14;
use constant NSCellIsInsetButton => 15;
use constant NSCellAllowsMixedState => 16;

# NSCellImagePosition
use constant NSNoImage => 0;
use constant NSImageOnly => 1;
use constant NSImageLeft => 2;
use constant NSImageRight => 3;
use constant NSImageBelow => 4;
use constant NSImageAbove => 5;
use constant NSImageOverlaps => 6;

# NSCellStateValue
use constant NSMixedState => -1;
use constant NSOffState => 0;
use constant NSOnState => 1;

# NSCellType
use constant NSNullCellType => 0;
use constant NSTextCellType => 1;
use constant NSImageCellType => 2;

# NSCharacterCollection
use constant NSIdentityMappingCharacterCollection => 0;
use constant NSAdobeCNS1CharacterCollection => 1;
use constant NSAdobeGB1CharacterCollection => 2;
use constant NSAdobeJapan1CharacterCollection => 3;
use constant NSAdobeJapan2CharacterCollection => 4;
use constant NSAdobeKorea1CharacterCollection => 5;

# NSCompositingOperation
use constant NSCompositeClear => 0;
use constant NSCompositeCopy => 1;
use constant NSCompositeSourceOver => 2;
use constant NSCompositeSourceIn => 3;
use constant NSCompositeSourceOut => 4;
use constant NSCompositeSourceAtop => 5;
use constant NSCompositeDestinationOver => 6;
use constant NSCompositeDestinationIn => 7;
use constant NSCompositeDestinationOut => 8;
use constant NSCompositeDestinationAtop => 9;
use constant NSCompositeXOR => 10;
use constant NSCompositePlusDarker => 11;
use constant NSCompositeHighlight => 12;
use constant NSCompositePlusLighter => 13;

# NSControlSize
use constant NSRegularControlSize => 0;
use constant NSSmallControlSize => 1;
use constant NSMiniControlSize => 2;

# NSControlTint
use constant NSDefaultControlTint => 0;
use constant NSBlueControlTint => 1;
use constant NSGraphiteControlTint => 6;
use constant NSClearControlTint => 7;

# NSDocumentChangeType
use constant NSChangeDone => 0;
use constant NSChangeUndone => 1;
use constant NSChangeCleared => 2;

# NSDrawerState
use constant NSDrawerClosedState => 0;
use constant NSDrawerOpeningState => 1;
use constant NSDrawerOpenState => 2;
use constant NSDrawerClosingState => 3;

# NSEventType
use constant NSLeftMouseDown => 1;
use constant NSLeftMouseUp => 2;
use constant NSRightMouseDown => 3;
use constant NSRightMouseUp => 4;
use constant NSMouseMoved => 5;
use constant NSLeftMouseDragged => 6;
use constant NSRightMouseDragged => 7;
use constant NSMouseEntered => 8;
use constant NSMouseExited => 9;
use constant NSKeyDown => 10;
use constant NSKeyUp => 11;
use constant NSFlagsChanged => 12;
use constant NSAppKitDefined => 13;
use constant NSSystemDefined => 14;
use constant NSApplicationDefined => 15;
use constant NSPeriodic => 16;
use constant NSCursorUpdate => 17;
use constant NSScrollWheel => 22;
use constant NSOtherMouseDown => 25;
use constant NSOtherMouseUp => 26;
use constant NSOtherMouseDragged => 27;

# NSFindPanelAction
use constant NSFindPanelActionShowFindPanel => 1;
use constant NSFindPanelActionNext => 2;
use constant NSFindPanelActionPrevious => 3;
use constant NSFindPanelActionReplaceAll => 4;
use constant NSFindPanelActionReplace => 5;
use constant NSFindPanelActionReplaceAndFind => 6;
use constant NSFindPanelActionSetFindString => 7;
use constant NSFindPanelActionReplaceAllInSelection => 8;

# NSFocusRingPlacement
use constant NSFocusRingOnly => 0;
use constant NSFocusRingBelow => 1;
use constant NSFocusRingAbove => 2;

# NSFocusRingType
use constant NSFocusRingTypeDefault => 0;
use constant NSFocusRingTypeNone => 1;
use constant NSFocusRingTypeExterior => 2;

# NSFontAction
use constant NSNoFontChangeAction => 0;
use constant NSViaPanelFontAction => 1;
use constant NSAddTraitFontAction => 2;
use constant NSSizeUpFontAction => 3;
use constant NSSizeDownFontAction => 4;
use constant NSHeavierFontAction => 5;
use constant NSLighterFontAction => 6;
use constant NSRemoveTraitFontAction => 7;

# NSGlyphInscription
use constant NSGlyphInscribeBase => 0;
use constant NSGlyphInscribeBelow => 1;
use constant NSGlyphInscribeAbove => 2;
use constant NSGlyphInscribeOverstrike => 3;
use constant NSGlyphInscribeOverBelow => 4;

# NSGlyphLayoutMode
use constant NSGlyphLayoutAtAPoint => 0;
use constant NSGlyphLayoutAgainstAPoint => 1;
use constant NSGlyphLayoutWithPrevious => 2;

# NSGlyphRelation
use constant NSGlyphBelow => 1;
use constant NSGlyphAbove => 2;

# NSGradientType
use constant NSGradientNone => 0;
use constant NSGradientConcaveWeak => 1;
use constant NSGradientConcaveStrong => 2;
use constant NSGradientConvexWeak => 3;
use constant NSGradientConvexStrong => 4;

# NSImageAlignment
use constant NSImageAlignCenter => 0;
use constant NSImageAlignTop => 1;
use constant NSImageAlignTopLeft => 2;
use constant NSImageAlignTopRight => 3;
use constant NSImageAlignLeft => 4;
use constant NSImageAlignBottom => 5;
use constant NSImageAlignBottomLeft => 6;
use constant NSImageAlignBottomRight => 7;
use constant NSImageAlignRight => 8;

# NSImageCacheMode
use constant NSImageCacheDefault => 0;
use constant NSImageCacheAlways => 1;
use constant NSImageCacheBySize => 2;
use constant NSImageCacheNever => 3;

# NSImageFrameStyle
use constant NSImageFrameNone => 0;
use constant NSImageFramePhoto => 1;
use constant NSImageFrameGrayBezel => 2;
use constant NSImageFrameGroove => 3;
use constant NSImageFrameButton => 4;

# NSImageInterpolation
use constant NSImageInterpolationDefault => 0;
use constant NSImageInterpolationNone => 1;
use constant NSImageInterpolationLow => 2;
use constant NSImageInterpolationHigh => 3;

# NSImageLoadStatus
use constant NSImageLoadStatusCompleted => 0;
use constant NSImageLoadStatusCancelled => 1;
use constant NSImageLoadStatusInvalidData => 2;
use constant NSImageLoadStatusUnexpectedEOF => 3;
use constant NSImageLoadStatusReadError => 4;

# NSImageRepLoadStatus
use constant NSImageRepLoadStatusUnknownType => -1;
use constant NSImageRepLoadStatusReadingHeader => -2;
use constant NSImageRepLoadStatusWillNeedAllData => -3;
use constant NSImageRepLoadStatusInvalidData => -4;
use constant NSImageRepLoadStatusUnexpectedEOF => -5;
use constant NSImageRepLoadStatusCompleted => -6;

# NSImageScaling
use constant NSScaleProportionally => 0;
use constant NSScaleToFit => 1;
use constant NSScaleNone => 2;

# NSInterfaceStyle
use constant NSNoInterfaceStyle => 0;
use constant NSNextStepInterfaceStyle => 1;
use constant NSWindows95InterfaceStyle => 2;
use constant NSMacintoshInterfaceStyle => 3;

# NSLayoutDirection
use constant NSLayoutLeftToRight => 0;
use constant NSLayoutRightToLeft => 1;

# NSLayoutStatus
use constant NSLayoutNotDone => 0;
use constant NSLayoutDone => 1;
use constant NSLayoutCantFit => 2;
use constant NSLayoutOutOfGlyphs => 3;

# NSLineBreakMode
use constant NSLineBreakByWordWrapping => 0;
use constant NSLineBreakByCharWrapping => 1;
use constant NSLineBreakByClipping => 2;
use constant NSLineBreakByTruncatingHead => 3;
use constant NSLineBreakByTruncatingTail => 4;
use constant NSLineBreakByTruncatingMiddle => 5;

# NSLineCapStyle
use constant NSButtLineCapStyle => 0;
use constant NSRoundLineCapStyle => 1;
use constant NSSquareLineCapStyle => 2;

# NSLineJoinStyle
use constant NSMiterLineJoinStyle => 0;
use constant NSRoundLineJoinStyle => 1;
use constant NSBevelLineJoinStyle => 2;

# NSLineMovementDirection
use constant NSLineDoesntMove => 0;
use constant NSLineMovesLeft => 1;
use constant NSLineMovesRight => 2;
use constant NSLineMovesDown => 3;
use constant NSLineMovesUp => 4;

# NSLineSweepDirection
use constant NSLineSweepLeft => 0;
use constant NSLineSweepRight => 1;
use constant NSLineSweepDown => 2;
use constant NSLineSweepUp => 3;

# NSMatrixMode
use constant NSRadioModeMatrix => 0;
use constant NSHighlightModeMatrix => 1;
use constant NSListModeMatrix => 2;
use constant NSTrackModeMatrix => 3;

# NSMultibyteGlyphPacking
use constant NSOneByteGlyphPacking => 0;
use constant NSJapaneseEUCGlyphPacking => 1;
use constant NSAsciiWithDoubleByteEUCGlyphPacking => 2;
use constant NSTwoByteGlyphPacking => 3;
use constant NSFourByteGlyphPacking => 4;
use constant NSNativeShortGlyphPacking => 5;

# NSOpenGLContextParameter
use constant NSOpenGLCPSwapRectangle => 200;
use constant NSOpenGLCPSwapRectangleEnable => 201;
use constant NSOpenGLCPRasterizationEnable => 221;
use constant NSOpenGLCPSwapInterval => 222;
use constant NSOpenGLCPSurfaceOrder => 235;
use constant NSOpenGLCPSurfaceOpacity => 236;
use constant NSOpenGLCPStateValidation => 301;

# NSOpenGLGlobalOption
use constant NSOpenGLGOFormatCacheSize => 501;
use constant NSOpenGLGOClearFormatCache => 502;
use constant NSOpenGLGORetainRenderers => 503;
use constant NSOpenGLGOResetLibrary => 504;

# NSOpenGLPixelFormatAttribute
use constant NSOpenGLPFAAllRenderers => 1;
use constant NSOpenGLPFADoubleBuffer => 5;
use constant NSOpenGLPFAStereo => 6;
use constant NSOpenGLPFAAuxBuffers => 7;
use constant NSOpenGLPFAColorSize => 8;
use constant NSOpenGLPFAAlphaSize => 11;
use constant NSOpenGLPFADepthSize => 12;
use constant NSOpenGLPFAStencilSize => 13;
use constant NSOpenGLPFAAccumSize => 14;
use constant NSOpenGLPFAMinimumPolicy => 51;
use constant NSOpenGLPFAMaximumPolicy => 52;
use constant NSOpenGLPFAOffScreen => 53;
use constant NSOpenGLPFAFullScreen => 54;
use constant NSOpenGLPFASampleBuffers => 55;
use constant NSOpenGLPFASamples => 56;
use constant NSOpenGLPFAAuxDepthStencil => 57;
use constant NSOpenGLPFARendererID => 70;
use constant NSOpenGLPFASingleRenderer => 71;
use constant NSOpenGLPFANoRecovery => 72;
use constant NSOpenGLPFAAccelerated => 73;
use constant NSOpenGLPFAClosestPolicy => 74;
use constant NSOpenGLPFARobust => 75;
use constant NSOpenGLPFABackingStore => 76;
use constant NSOpenGLPFAMPSafe => 78;
use constant NSOpenGLPFAWindow => 80;
use constant NSOpenGLPFAMultiScreen => 81;
use constant NSOpenGLPFACompliant => 83;
use constant NSOpenGLPFAScreenMask => 84;
use constant NSOpenGLPFAPixelBuffer => 90;
use constant NSOpenGLPFAVirtualScreenCount => 128;

# NSPopUpArrowPosition
use constant NSPopUpNoArrow => 0;
use constant NSPopUpArrowAtCenter => 1;
use constant NSPopUpArrowAtBottom => 2;

# NSPrinterTableStatus
use constant NSPrinterTableOK => 0;
use constant NSPrinterTableNotFound => 1;
use constant NSPrinterTableError => 2;

# NSPrintingOrientation
use constant NSPortraitOrientation => 0;
use constant NSLandscapeOrientation => 1;

# NSPrintingPageOrder
use constant NSDescendingPageOrder => -1;
use constant NSSpecialPageOrder => 0;
use constant NSAscendingPageOrder => 1;
use constant NSUnknownPageOrder => 2;

# NSPrintingPaginationMode
use constant NSAutoPagination => 0;
use constant NSFitPagination => 1;
use constant NSClipPagination => 2;

# NSProgressIndicatorStyle
use constant NSProgressIndicatorBarStyle => 0;
use constant NSProgressIndicatorSpinningStyle => 1;

# NSProgressIndicatorThickness
use constant NSProgressIndicatorPreferredThickness => 14;
use constant NSProgressIndicatorPreferredSmallThickness => 10;
use constant NSProgressIndicatorPreferredLargeThickness => 18;
use constant NSProgressIndicatorPreferredAquaThickness => 12;

# NSQTMovieLoopMode
use constant NSQTMovieNormalPlayback => 0;
use constant NSQTMovieLoopingPlayback => 1;
use constant NSQTMovieLoopingBackAndForthPlayback => 2;

# NSRequestUserAttentionType
use constant NSCriticalRequest => 0;
use constant NSInformationalRequest => 10;

# NSRulerOrientation
use constant NSHorizontalRuler => 0;
use constant NSVerticalRuler => 1;

# NSSaveOperationType
use constant NSSaveOperation => 0;
use constant NSSaveAsOperation => 1;
use constant NSSaveToOperation => 2;

# NSScrollArrowPosition
use constant NSScrollerArrowsDefaultSetting => 0;
use constant NSScrollerArrowsNone => 2;

# NSScrollerArrow
use constant NSScrollerIncrementArrow => 0;
use constant NSScrollerDecrementArrow => 1;

# NSScrollerPart
use constant NSScrollerNoPart => 0;
use constant NSScrollerDecrementPage => 1;
use constant NSScrollerKnob => 2;
use constant NSScrollerIncrementPage => 3;
use constant NSScrollerDecrementLine => 4;
use constant NSScrollerIncrementLine => 5;
use constant NSScrollerKnobSlot => 6;

# NSSegmentSwitchTracking
use constant NSSegmentSwitchTrackingSelectOne => 0;
use constant NSSegmentSwitchTrackingSelectAny => 1;
use constant NSSegmentSwitchTrackingMomentary => 2;

# NSSelectionAffinity
use constant NSSelectionAffinityUpstream => 0;
use constant NSSelectionAffinityDownstream => 1;

# NSSelectionDirection
use constant NSDirectSelection => 0;
use constant NSSelectingNext => 1;
use constant NSSelectingPrevious => 2;

# NSSelectionGranularity
use constant NSSelectByCharacter => 0;
use constant NSSelectByWord => 1;
use constant NSSelectByParagraph => 2;

# NSSliderType
use constant NSLinearSlider => 0;
use constant NSCircularSlider => 1;

# NSTabState
use constant NSSelectedTab => 0;
use constant NSBackgroundTab => 1;
use constant NSPressedTab => 2;

# NSTabViewType
use constant NSTopTabsBezelBorder => 0;
use constant NSLeftTabsBezelBorder => 1;
use constant NSBottomTabsBezelBorder => 2;
use constant NSRightTabsBezelBorder => 3;
use constant NSNoTabsBezelBorder => 4;
use constant NSNoTabsLineBorder => 5;
use constant NSNoTabsNoBorder => 6;

# NSTableViewDropOperation
use constant NSTableViewDropOn => 0;
use constant NSTableViewDropAbove => 1;

# NSTextAlignment
use constant NSLeftTextAlignment => 0;
use constant NSRightTextAlignment => 1;
use constant NSCenterTextAlignment => 2;
use constant NSJustifiedTextAlignment => 3;
use constant NSNaturalTextAlignment => 4;

# NSTextFieldBezelStyle
use constant NSTextFieldSquareBezel => 0;
use constant NSTextFieldRoundedBezel => 1;

# NSTextTabType
use constant NSLeftTabStopType => 0;
use constant NSRightTabStopType => 1;
use constant NSCenterTabStopType => 2;
use constant NSDecimalTabStopType => 3;

# NSTickMarkPosition
use constant NSTickMarkBelow => 0;
use constant NSTickMarkAbove => 1;
use constant NSTickMarkLeft => NSTickMarkAbove;
use constant NSTickMarkRight => NSTickMarkBelow;

# NSTIFFCompression
use constant NSTIFFCompressionNone => 1;
use constant NSTIFFCompressionCCITTFAX3 => 3;
use constant NSTIFFCompressionCCITTFAX4 => 4;
use constant NSTIFFCompressionLZW => 5;
use constant NSTIFFCompressionJPEG => 6;
use constant NSTIFFCompressionNEXT => 32766;
use constant NSTIFFCompressionPackBits => 32773;
use constant NSTIFFCompressionOldJPEG => 32865;

# NSTitlePosition
use constant NSNoTitle => 0;
use constant NSAboveTop => 1;
use constant NSAtTop => 2;
use constant NSBelowTop => 3;
use constant NSAboveBottom => 4;
use constant NSAtBottom => 5;
use constant NSBelowBottom => 6;

# NSToolbarDisplayMode
use constant NSToolbarDisplayModeDefault => 0;
use constant NSToolbarDisplayModeIconAndLabel => 1;
use constant NSToolbarDisplayModeIconOnly => 2;
use constant NSToolbarDisplayModeLabelOnly => 3;

# NSToolbarSizeMode
use constant NSToolbarSizeModeDefault => 0;
use constant NSToolbarSizeModeRegular => 1;
use constant NSToolbarSizeModeSmall => 2;

# NSTypesetterBehavior
use constant NSTypesetterLatestBehavior => -1;
use constant NSTypesetterOriginalBehavior => 0;
use constant NSTypesetterBehavior_10_2_WithCompatibility => 1;
use constant NSTypesetterBehavior_10_2 => 2;
use constant NSTypesetterBehavior_10_3 => 3;

# NSUsableScrollerParts
use constant NSNoScrollerParts => 0;
use constant NSOnlyScrollerArrows => 1;
use constant NSAllScrollerParts => 2;

# NSWindingRule
use constant NSNonZeroWindingRule => 0;
use constant NSEvenOddWindingRule => 1;

# NSWindowButton
use constant NSWindowCloseButton => 0;
use constant NSWindowMiniaturizeButton => 1;
use constant NSWindowZoomButton => 2;
use constant NSWindowToolbarButton => 3;
use constant NSWindowDocumentIconButton => 4;

# NSWindowOrderingMode
use constant NSWindowAbove => 1;
use constant NSWindowBelow => -1;
use constant NSWindowOut => 0;

# NSWritingDirection
use constant NSWritingDirectionNatural => -1;
use constant NSWritingDirectionLeftToRight => 0;
use constant NSWritingDirectionRightToLeft => 1;

#
# Enums
#

# NSAlertÑButton Return Values
use constant NSAlertFirstButtonReturn => 1000;
use constant NSAlertSecondButtonReturn => 1001;
use constant NSAlertThirdButtonReturn => 1002;

# NSApplication-Modal Session Return Values
use constant NSRunStoppedResponse => -1000;
use constant NSRunAbortedResponse => -1001;
use constant NSRunContinuesResponse => -1002;

# NSAttributedStringÑUnderlining Patterns
use constant NSUnderlinePatternSolid => 0x0000;
use constant NSUnderlinePatternDot => 0x0100;
use constant NSUnderlinePatternDash => 0x0200;
use constant NSUnderlinePatternDashDot => 0x0300;
use constant NSUnderlinePatternDashDotDot => 0x0400;

# NSAttributedStringÑUnderlining Styles
use constant NSUnderlineStyleNone => 0x00;
use constant NSUnderlineStyleSingle => 0x01;
use constant NSUnderlineStyleThick => 0x02;
use constant NSUnderlineStyleDouble => 0x09;

# NSCell-Data Entry Types
use constant NSAnyType => 0;
use constant NSIntType => 1;
use constant NSPositiveIntType => 2;
use constant NSFloatType => 3;
use constant NSPositiveFloatType => 4;
use constant NSDoubleType => 6;
use constant NSPositiveDoubleType => 7;

# NSCell-State Masks
use constant NSNoCellMask => 0;
use constant NSContentsCellMask => 1;
use constant NSPushInCellMask => 2;
use constant NSChangeGrayCellMask => 4;
use constant NSChangeBackgroundCellMask => 8;

# NSColorPanelÑModes
use constant NSGrayModeColorPanel => 0;
use constant NSRGBModeColorPanel => 1;
use constant NSCMYKModeColorPanel => 2;
use constant NSHSBModeColorPanel => 3;
use constant NSCustomPaletteModeColorPanel => 4;
use constant NSColorListModeColorPanel => 5;
use constant NSWheelModeColorPanel => 6;
use constant NSCrayonModeColorPanel => 7;

# NSColorPanel-Mode Masks
use constant NSColorPanelGrayModeMask => 0x00000001;
use constant NSColorPanelRGBModeMask => 0x00000002;
use constant NSColorPanelCMYKModeMask => 0x00000004;
use constant NSColorPanelHSBModeMask => 0x00000008;
use constant NSColorPanelCustomPaletteModeMask => 0x00000010;
use constant NSColorPanelColorListModeMask => 0x00000020;
use constant NSColorPanelWheelModeMask => 0x00000040;
use constant NSColorPanelAllModesMask => 0x0000ffff;

# NSDragging-Operations
use constant NSDragOperationNone => 0;
use constant NSDragOperationCopy => 1;
use constant NSDragOperationLink => 2;
use constant NSDragOperationGeneric => 4;
use constant NSDragOperationPrivate => 8;
use constant NSDragOperationMove => 16;
use constant NSDragOperationDelete => 32;
use constant NSDragOperationEvery => 0xffffffff;

# NSEvent-Action Flags
use constant NSLeftMouseDownMask => 0b0000000000000000000010;
use constant NSLeftMouseUpMask => 0b0000000000000000000100;
use constant NSRightMouseDownMask => 0b0000000000000000001000;
use constant NSRightMouseUpMask => 0b0000000000000000010000;
use constant NSMouseMovedMask => 0b0000000000000000100000;
use constant NSLeftMouseDraggedMask => 0b0000000000000001000000;
use constant NSRightMouseDraggedMask => 0b0000000000000010000000;
use constant NSMouseEnteredMask => 0b0000000000000100000000;
use constant NSMouseExitedMask => 0b0000000000001000000000;
use constant NSKeyDownMask => 0b0000000000010000000000;
use constant NSKeyUpMask => 0b0000000000100000000000;
use constant NSFlagsChangedMask => 0b0000000001000000000000;
use constant NSAppKitDefinedMask => 0b0000000010000000000000;
use constant NSSystemDefinedMask => 0b0000000100000000000000;
use constant NSApplicationDefinedMask => 0b0000001000000000000000;
use constant NSPeriodicMask => 0b0000010000000000000000;
use constant NSCursorUpdateMask => 0b0000100000000000000000;
use constant NScrollWheelMask => 0b0001000000000000000000;
use constant NSOtherMouseDownMask => 0b0010000000000000000000;
use constant NSOtherMouseUpMask => 0b0100000000000000000000;
use constant NSOtherMouseDraggedMask => 0b1000000000000000000000;
use constant NSAnyEventMask => 0xffffffff;

# NSEvent-Function-Key Unicodes
use constant NSUpArrowFunctionKey => 0xF700;
use constant NSDownArrowFunctionKey => 0xF701;
use constant NSLeftArrowFunctionKey => 0xF702;
use constant NSRightArrowFunctionKey => 0xF703;
use constant NSF1FunctionKey => 0xF704;
use constant NSF2FunctionKey => 0xF705;
use constant NSF3FunctionKey => 0xF706;
use constant NSF4FunctionKey => 0xF707;
use constant NSF5FunctionKey => 0xF708;
use constant NSF6FunctionKey => 0xF709;
use constant NSF7FunctionKey => 0xF70A;
use constant NSF8FunctionKey => 0xF70B;
use constant NSF9FunctionKey => 0xF70C;
use constant NSF10FunctionKey => 0xF70D;
use constant NSF11FunctionKey => 0xF70E;
use constant NSF12FunctionKey => 0xF70F;
use constant NSF13FunctionKey => 0xF710;
use constant NSF14FunctionKey => 0xF711;
use constant NSF15FunctionKey => 0xF712;
use constant NSF16FunctionKey => 0xF713;
use constant NSF17FunctionKey => 0xF714;
use constant NSF18FunctionKey => 0xF715;
use constant NSF19FunctionKey => 0xF716;
use constant NSF20FunctionKey => 0xF717;
use constant NSF21FunctionKey => 0xF718;
use constant NSF22FunctionKey => 0xF719;
use constant NSF23FunctionKey => 0xF71A;
use constant NSF24FunctionKey => 0xF71B;
use constant NSF25FunctionKey => 0xF71C;
use constant NSF26FunctionKey => 0xF71D;
use constant NSF27FunctionKey => 0xF71E;
use constant NSF28FunctionKey => 0xF71F;
use constant NSF29FunctionKey => 0xF720;
use constant NSF30FunctionKey => 0xF721;
use constant NSF31FunctionKey => 0xF722;
use constant NSF32FunctionKey => 0xF723;
use constant NSF33FunctionKey => 0xF724;
use constant NSF34FunctionKey => 0xF725;
use constant NSF35FunctionKey => 0xF726;
use constant NSInsertFunctionKey => 0xF727;
use constant NSDeleteFunctionKey => 0xF728;
use constant NSHomeFunctionKey => 0xF729;
use constant NSBeginFunctionKey => 0xF72A;
use constant NSEndFunctionKey => 0xF72B;
use constant NSPageUpFunctionKey => 0xF72C;
use constant NSPageDownFunctionKey => 0xF72D;
use constant NSPrintScreenFunctionKey => 0xF72E;
use constant NSScrollLockFunctionKey => 0xF72F;
use constant NSPauseFunctionKey => 0xF730;
use constant NSSysReqFunctionKey => 0xF731;
use constant NSBreakFunctionKey => 0xF732;
use constant NSResetFunctionKey => 0xF733;
use constant NSStopFunctionKey => 0xF734;
use constant NSMenuFunctionKey => 0xF735;
use constant NSUserFunctionKey => 0xF736;
use constant NSSystemFunctionKey => 0xF737;
use constant NSPrintFunctionKey => 0xF738;
use constant NSClearLineFunctionKey => 0xF739;
use constant NSClearDisplayFunctionKey => 0xF73A;
use constant NSInsertLineFunctionKey => 0xF73B;
use constant NSDeleteLineFunctionKey => 0xF73C;
use constant NSInsertCharFunctionKey => 0xF73D;
use constant NSDeleteCharFunctionKey => 0xF73E;
use constant NSPrevFunctionKey => 0xF73F;
use constant NSNextFunctionKey => 0xF740;
use constant NSSelectFunctionKey => 0xF741;
use constant NSExecuteFunctionKey => 0xF742;
use constant NSUndoFunctionKey => 0xF743;
use constant NSRedoFunctionKey => 0xF744;
use constant NSFindFunctionKey => 0xF745;
use constant NSHelpFunctionKey => 0xF746;
use constant NSModeSwitchFunctionKey => 0xF747;

# NSEvent-Modifier Flags
use constant NSAlphaShiftKeyMask => 1 << 16;
use constant NSShiftKeyMask => 1 << 17;
use constant NSControlKeyMask => 1 << 18;
use constant NSAlternateKeyMask => 1 << 19;
use constant NSCommandKeyMask => 1 << 20;
use constant NSNumericPadKeyMask => 1 << 21;
use constant NSHelpKeyMask => 1 << 22;
use constant NSFunctionKeyMask => 1 << 23;

# NSEvent-Types Defined by the Application Kit
use constant NSWindowExposedEventType => 0;
use constant NSApplicationActivatedEventType => 1;
use constant NSApplicationDeactivatedEventType => 2;
use constant NSWindowMovedEventType => 4;
use constant NSScreenChangedEventType => 8;
use constant NSAWTEventType => 16;

# NSEvent-Types Defined by the System
use constant NSPowerOffEventType => 1;

# NSFont-Traits
use constant NSItalicFontMask => 0x00000001;
use constant NSBoldFontMask => 0x00000002;
use constant NSUnboldFontMask => 0x00000004;
use constant NSNonStandardCharacterSetFontMask => 0x00000008;
use constant NSNarrowFontMask => 0x00000010;
use constant NSExpandedFontMask => 0x00000020;
use constant NSCondensedFontMask => 0x00000040;
use constant NSSmallCapsFontMask => 0x00000080;
use constant NSPosterFontMask => 0x00000100;
use constant NSCompressedFontMask => 0x00000200;
use constant NSFixedPitchFontMask => 0x00000400;
use constant NSUnitalicFontMask => 0x01000000;

# NSFontManagerÑFont Collection Mask
use constant NSFontCollectionApplicationOnlyMask => 1 << 0;

# NSFontPanelÑMode Masks
use constant NSFontPanelFaceModeMask => 1 << 0;
use constant NSFontPanelSizeModeMask => 1 << 1;
use constant NSFontPanelCollectionModeMask => 1 << 2;
use constant NSFontPanelStandardModesMask => 0xFFFF;
use constant NSFontPanelAllModesMask => 0xFFFFFFFF;

# NSGraphics - Alpha Values
use constant NSAlphaEqualToData => 1;
use constant NSAlphaAlwaysOne => 2;

# NSGlyph-Reserved Glyph Codes
use constant NSControlGlyph => 0x00FFFFFF;
use constant NSNullGlyph => 0x0;

# NSGlyphStorageÑLayout Options
use constant NSShowControlGlyphs => 1 << 0;
use constant NSShowInvisibleGlyphs => 1 << 1;
use constant NSWantsBidiLevels => 1 << 2;

# NSImageRep-Display Device Matching
use constant NSImageRepMatchesDevice => 0;

# NSOutlineView-Drop On Index
use constant NSOutlineViewDropOnItemIndex => -1;

# NSPanel-Alert Panel Return Values
use constant NSAlertDefaultReturn => 1;
use constant NSAlertAlternateReturn => 0;
use constant NSAlertOtherReturn => -1;
use constant NSAlertErrorReturn => -2;

# NSPanel-Modal Panel Return Values
use constant NSOKButton => 1;
use constant NSCancelButton => 0;

# NSPanel-Style Mask
use constant NSUtilityWindowMask => 1 << 4;
use constant NSDocModalWindowMask => 1 << 6;
use constant NSNonactivatingPanelMask => 1 << 7;

# NSRunLoop-Ordering Modes for NSApplication
use constant NSUpdateWindowsRunLoopOrdering => 0;

# NSRunLoop-Ordering Modes for NSWindow
use constant NSDisplayWindowRunLoopOrdering => 0;
use constant NSResetCursorRectsRunLoopOrdering => 1;

# NSSavePanel-Tags for Subviews
use constant NSFileHandlingPanelImageButton => 150;
use constant NSFileHandlingPanelTitleField => 151;
use constant NSFileHandlingPanelBrowser => 152;
use constant NSFileHandlingPanelCancelButton => NSCancelButton;
use constant NSFileHandlingPanelOKButton => NSOKButton;
use constant NSFileHandlingPanelForm => 155;

# NSTableViewÑGrid Styles
use constant NSTableViewGridNone => 0;
use constant NSTableViewSolidVerticalGridLineMask => 1 << 0;
use constant NSTableViewSolidHorizontalGridLineMask => 1 << 1;

# NSTextAttachment-Attachment Character
use constant NSAttachmentCharacter => 0xfffc;

# NSText-Important Unicodes
use constant NSParagraphSeparatorCharacter => 0x2029;
use constant NSLineSeparatorCharacter => 0x2028;
use constant NSTabCharacter => 0x0009;
use constant NSFormFeedCharacter => 0x000c;
use constant NSNewlineCharacter => 0x000a;
use constant NSCarriageReturnCharacter => 0x000d;
use constant NSEnterCharacter => 0x0003;
use constant NSBackspaceCharacter => 0x0008;
use constant NSBackTabCharacter => 0x0019;
use constant NSDeleteCharacter => 0x007f;

# NSTextÑMovement Codes
use constant NSIllegalTextMovement => 0;
use constant NSReturnTextMovement => 0x10;
use constant NSTabTextMovement => 0x11;
use constant NSBacktabTextMovement => 0x12;
use constant NSLeftTextMovement => 0x13;
use constant NSRightTextMovement => 0x14;
use constant NSUpTextMovement => 0x15;
use constant NSDownTextMovement => 0x16;
use constant NSCancelTextMovement => 0x17;
use constant NSOtherTextMovement => 0;

# NSTextStorage-Editing
use constant NSTextStorageEditedAttributes => 1;
use constant NSTextStorageEditedCharacters => 2;

# NSView-Resizing
use constant NSViewNotSizable => 0;
use constant NSViewMinXMargin => 1;
use constant NSViewWidthSizable => 2;
use constant NSViewMaxXMargin => 4;
use constant NSViewMinYMargin => 8;
use constant NSViewHeightSizable => 16;
use constant NSViewMaxYMargin => 32;

# NSWindowÑBorder Masks
use constant NSBorderlessWindowMask => 0;
use constant NSTitledWindowMask => 1 << 0;
use constant NSClosableWindowMask => 1 << 1;
use constant NSMiniaturizableWindowMask => 1 << 2;
use constant NSResizableWindowMask => 1 << 3;
use constant NSTexturedBackgroundWindowMask => 1 << 8;

# NSWorkspaceÑLaunch Options
use constant NSWorkspaceLaunchAndPrint => 0x00000002;
use constant NSWorkspaceLaunchInhibitingBackgroundOnly => 0x00000080;
use constant NSWorkspaceLaunchWithoutAddingToRecents => 0x00000100;
use constant NSWorkspaceLaunchWithoutActivation => 0x00000200;
use constant NSWorkspaceLaunchAsync => 0x00010000;
use constant NSWorkspaceLaunchAllowingClassicStartup => 0x00020000;
use constant NSWorkspaceLaunchPreferringClassic => 0x00040000;
use constant NSWorkspaceLaunchNewInstance => 0x00080000;
use constant NSWorkspaceLaunchAndHide => 0x00100000;
use constant NSWorkspaceLaunchAndHideOthers => 0x00200000;
use constant NSWorkspaceLaunchDefault => NSWorkspaceLaunchAsync | NSWorkspaceLaunchAllowingClassicStartup;

#
# Errors
#

# Attributed String Errors
use constant NSTextReadInapplicableDocumentTypeError => 65806;
use constant NSTextWriteInapplicableDocumentTypeError => 66062;
use constant NSTextReadWriteErrorMinimum => 65792;
use constant NSTextReadWriteErrorMaximum => 66303;

# Happy perl
1;
