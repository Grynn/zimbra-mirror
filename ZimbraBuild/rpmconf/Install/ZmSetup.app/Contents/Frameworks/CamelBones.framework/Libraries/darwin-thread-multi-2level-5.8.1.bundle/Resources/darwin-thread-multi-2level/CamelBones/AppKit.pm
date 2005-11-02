use strict;
use warnings;

package CamelBones::AppKit;

require Exporter;
our @ISA = qw(Exporter);
our $VERSION = '1.0.0';
our @EXPORT = qw(
			  );
our @EXPORT_OK = qw(
					NSShowsServicesMenuItem
					NSSetShowsServicesMenuItem
					NSUpdateDynamicServices
					NSPerformService
					NSRegisterServicesProvider
					NSRectFill
					NSRectFillList
					NSRectFillListWithGrays
					NSRectFillListWithColors
					NSRectFillUsingOperation
					NSRectFillListUsingOperation
					NSRectFillListWithColorsUsingOperation
					NSFrameRect
					NSFrameRectWithWidth
					NSFrameRectWithWidthUsingOperation
					NSRectClip
					NSRectClipList
					NSDrawTiledRects
					NSDrawGrayBezel
					NSDrawGroove
					NSDrawWhiteBezel
					NSDrawButton
					NSEraseRect
					NSReadPixel
					NSDrawBitmap
					NSCopyBits
					NSHighlightRect
					NSBeep
					NSCountWindows
					NSWindowList
					NSCountWindowsForContext
					NSWindowListForContext
					NSDrawColorTiledRects
					NSDrawDarkBezel
					NSDrawLightBezel
					NSDottedFrameRect
					NSDrawWindowBackground
					NSSetFocusRingStyle
					NSInterfaceStyleForKey
					NSRunAlertPanel
					NSRunInformationalAlertPanel
					NSRunCriticalAlertPanel
					NSBeginAlertSheet
					NSBeginInformationalAlertSheet
					NSBeginCriticalAlertSheet
					NSGetAlertPanel
					NSGetInformationalAlertPanel
					NSGetCriticalAlertPanel
					NSReleaseAlertPanel
					NSCreateFilenamePboardType
					NSCreateFileContentsPboardType
					NSGetFileType
					NSGetFileTypes
				   );
our %EXPORT_TAGS = (
    'All'		=> [@EXPORT_OK],
);

require XSLoader;
XSLoader::load('CamelBones::AppKit');

1;
