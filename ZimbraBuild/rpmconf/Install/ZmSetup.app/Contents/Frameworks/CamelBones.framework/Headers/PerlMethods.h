//
//  PerlMethods.h
//  CamelBones
//
//  Copyright (c) 2004 Sherm Pendley. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "PerlImports.h"

// Get information about a Perl object
extern NSString* (*CBGetMethodNameForSelector)(void* sv, SEL selector);
extern NSString* (*CBGetMethodArgumentSignatureForSelector)(void* sv, SEL selector);
extern NSString* (*CBGetMethodReturnSignatureForSelector)(void* sv, SEL selector);

// IMP registered as a native method
extern id (*CBPerlIMP)(id self, SEL _cmd, ...);

