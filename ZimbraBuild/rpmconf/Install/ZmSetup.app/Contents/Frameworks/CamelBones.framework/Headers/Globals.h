//
//  Globals.h
//  CamelBones
//
//  Copyright (c) 2001 Sherm Pendley. All rights reserved.
//

#ifdef GNUSTEP
#import <Foundation/Foundation.h>
#else
#import <Cocoa/Cocoa.h>
#endif

// Create Perl wrappers for all global variables
extern void (*CBWrapAllGlobals)(void);

// Create Perl wrappers for one global variable of a specific type
extern BOOL (*CBWrapString)(const char *varName, const char *pkgName);
