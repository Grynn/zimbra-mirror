//
//  Wrappers.h
//  CamelBones
//
//  Copyright (c) 2004 Sherm Pendley. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "PerlImports.h"

// Create a Perl object that "wraps" an Objective-C object
extern void* (*CBCreateWrapperObject)(id obj);
extern void* (*CBCreateWrapperObjectWithClassName)(id obj, NSString* className);

// Create a new Perl object blessed into the specified package
extern void* (*CBCreateObjectOfClass)(NSString *className);

