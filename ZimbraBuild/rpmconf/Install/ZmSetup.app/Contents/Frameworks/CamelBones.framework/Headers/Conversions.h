//
//  Conversions.h
//  CamelBones
//
//  Copyright (c) 2004 Sherm Pendley. All rights reserved.
//

#import <Foundation/Foundation.h>

extern id (*CBDerefSVtoID)(void* sv);
extern void* (*CBDerefIDtoSV)(id target);

extern Class (*CBClassFromSV)(void* sv);
extern void* (*CBSVFromClass)(Class c);

extern SEL (*CBSelectorFromSV)(void* sv);
extern void* (*CBSVFromSelector)(SEL aSel);

extern void (*CBPoke)(void *address, void *object, unsigned length);
