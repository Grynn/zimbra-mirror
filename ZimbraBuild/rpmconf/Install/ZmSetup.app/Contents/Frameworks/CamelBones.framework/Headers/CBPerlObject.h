//
//  CBPerlObject.h
//  Camel Bones - a bare-bones Perl bridge for Objective-C
//  Originally written for ShuX
//
//  Copyright (c) 2002 Sherm Pendley. All rights reserved.

#import <Foundation/Foundation.h>

@interface CBPerlObject : NSObject {
    NSString *className;

    @private
    void *_mySV;
    void *_myHV;
}

// Returns an autoreleased handle to a Perl object named varName.
// Returns nil of no such object exists.
+ (CBPerlObject *) namedObject: (NSString *)varName;

// Returns an autoreleased handle to a Perl object named varName, creating a
// new object if necessary of the class className.
// Returns nil if the named object does not exist, and could not be created
+ (CBPerlObject *) namedObject: (NSString *)varName ofClass: (NSString *)newClassName;

// Returns a handle to a Perl object named varName.
// Returns nil of no such object exists.
- (CBPerlObject *) initNamedObject: (NSString *)varName;

// Returns a handle to a Perl object named varName, creating a
// new object if necessary of the class className.
// Returns nil if the named object does not exist, and could not be created
- (CBPerlObject *) initNamedObject: (NSString *)varName ofClass: (NSString *)newClassName;

// Query the object for its class name
- (NSString *) perlClassName;

// Check for named properties, and get/set their values
- (BOOL) hasProperty: (NSString *)propName;
- (id) getProperty: (NSString *)propName;
- (void) setProperty: (NSString *)propName toObject: (id)propValue;

@end
