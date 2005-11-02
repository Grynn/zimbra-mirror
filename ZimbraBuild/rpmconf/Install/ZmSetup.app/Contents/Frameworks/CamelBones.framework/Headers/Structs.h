//
//  Structs.h
//  CamelBones
//
//  Copyright (c) 2004 Sherm Pendley. All rights reserved.
//

#import <Foundation/Foundation.h>

// Creating NSPoint structs
extern NSPoint (*CBPointFromAV)(void* av);
extern NSPoint (*CBPointFromHV)(void* hv);
extern NSPoint (*CBPointFromSV)(void* sv);

// Converting NSPoint structs to blessed scalar references
extern void* (*CBPointToSV)(NSPoint point);

// Creating NSRect structs
extern NSRect (*CBRectFromAV)(void* av);
extern NSRect (*CBRectFromHV)(void* hv);
extern NSRect (*CBRectFromSV)(void* sv);

// Converting NSRect structs to blessed scalar references
extern void* (*CBRectToSV)(NSRect rect);

// Creating NSRange structs
extern NSRange (*CBRangeFromAV)(void* av);
extern NSRange (*CBRangeFromHV)(void* hv);
extern NSRange (*CBRangeFromSV)(void* sv);

// Converting NSRange structs to blessed scalar references
extern void* (*CBRangeToSV)(NSRange range);

// Creating NSSize structs
extern NSSize (*CBSizeFromAV)(void* av);
extern NSSize (*CBSizeFromHV)(void* hv);
extern NSSize (*CBSizeFromSV)(void* sv);

// Converting NSSize structs to blessed scalar references
extern void* (*CBSizeToSV)(NSSize size);

// The following aren't needed on GNUStep
#ifndef GNUSTEP
// Creating OSType structs
extern OSType (*CBOSTypeFromSV)(void* sv);

// Converting OSType structs to blessed scalar references
extern void* (*CBOSTypeToSV)(OSType type);
#endif
