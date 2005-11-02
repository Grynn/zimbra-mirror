use strict;
use warnings;

package CamelBones::Foundation;

require Exporter;
our @ISA = qw(Exporter);
our $VERSION = '1.0.0';
our @EXPORT = ();
our @EXPORT_OK = qw(
    NSLog
    NSClassFromString
    NSFileTypeForHFSTypeCode
    NSHFSTypeCodeFromFileType
    NSHFSTypeOfFile
    NSFullUserName
    NSHomeDirectory
    NSHomeDirectoryForUser
    NSOpenStepRootDirectory
    NSSearchPathForDirectoriesInDomains
    NSTemporaryDirectory
    NSUserName
    NSMakePoint
    NSPointFromString
    NSStringFromPoint
    NSEqualRanges
    NSIntersectionRange
    NSLocationInRange
    NSMakeRange
    NSMaxRange
    NSRangeFromString
    NSStringFromRange
    NSUnionRange
    NSContainsRect
    NSEqualRects
    NSIsEmptyRect
    NSInsetRect
    NSIntegralRect
    NSIntersectionRect
    NSIntersectsRect
    NSMakeRect
    NSMaxX
    NSMaxY
    NSMidX
    NSMidY
    NSMinX
    NSMinY
    NSMouseInRect
    NSOffsetRect
    NSPointInRect
    NSRectFromString
    NSStringFromRect
    NSUnionRect
    NSWidth
    NSEqualSizes
    NSMakeSize
    NSSizeFromString
    NSStringFromSize
    NSLogPageSize
    NSPageSize
    NSRealMemoryAvailable
    NSRoundDownToMultipleOfPageSize
    NSRoundUpToMultipleOfPageSize
);

our %EXPORT_TAGS = (
    'All'		=> [@EXPORT_OK],
);

require XSLoader;
XSLoader::load('CamelBones::Foundation');

1;
