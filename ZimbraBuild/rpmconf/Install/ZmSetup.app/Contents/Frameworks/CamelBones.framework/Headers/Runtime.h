//
//  Runtime.h
//  CamelBones
//
//  Copyright (c) 2002 Sherm Pendley. All rights reserved.
//

@class NSArray;

// Functions to help interface with the Objective-C runtime

// Create Perl wrappers for all registered ObjC classes
extern void (*CBWrapRegisteredClasses)(void);

// Create Perl wrappers for a list of ObjC classes
extern void (*CBWrapNamedClasses)(NSArray *names);

// Create a Perl wrapper for a single ObjC class
extern void (*CBWrapObjectiveCClass)(Class aClass);

// Query class registration
extern BOOL (*CBIsClassRegistered)(const char *className);

// Register a Perl class with the runtime
extern void (*CBRegisterClassWithSuperClass)(const char *className, const char *superName);

// Query method registration
extern BOOL (*CBIsObjectMethodRegisteredForClass)(SEL selector, Class class);
extern BOOL (*CBIsClassMethodRegisteredForClass)(SEL selector, Class class);

// Perform method registration
extern void (*CBRegisterObjectMethodsForClass)(const char *package, NSArray *methods, Class class);
extern void (*CBRegisterClassMethodsForClass)(const char *package, NSArray *methods, Class class);

// Class handler registration
extern void (*CBRegisterClassHandler)(void);
