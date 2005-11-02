//
//  NativeMethods.h
//  CamelBones
//
//  Copyright (c) 2004 Sherm Pendley. All rights reserved.
//

#import <Foundation/Foundation.h>

// Call a native class or object method
extern void* (*CBCallNativeMethod)(void* target, SEL sel, void*args, BOOL isSuper);

