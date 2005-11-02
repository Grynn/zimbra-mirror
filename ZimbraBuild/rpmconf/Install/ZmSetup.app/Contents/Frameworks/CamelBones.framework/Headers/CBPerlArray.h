//
//  CBPerlArray.h
//  Camel Bones - a bare-bones Perl bridge for Objective-C
//  Originally written for ShuX
//
//  Copyright (c) 2002 Sherm Pendley. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface CBPerlArray : NSMutableArray {
    void *_myArray;
}

// Required primitive methods
#ifdef GNUSTEP
- (id) initWithCapacity: (unsigned int) anInt;
#endif /* GNUSTEP */
- (unsigned)count;
- (id)objectAtIndex:(unsigned)index;

- (void)addObject:(id)anObject;
- (void)insertObject:(id)anObject atIndex:(unsigned)index;
- (void)removeLastObject;
- (void)removeObjectAtIndex:(unsigned)index;
- (void)replaceObjectAtIndex:(unsigned)index withObject:(id)anObject;

// Extended methods

// Convenience creation methods returning autoreleased instances
+ (id) arrayNamed: (NSString *)varName isReference: (BOOL)isRef create: (BOOL)shouldCreate;
+ (id) arrayNamed: (NSString *)varName isReference: (BOOL)isRef;
+ (id) arrayNamed: (NSString *)varName;
+ (id) newArrayNamed: (NSString *)varName;
+ (id) arrayReferenceNamed: (NSString *)varName;
+ (id) newArrayReferenceNamed: (NSString *)varName;

// Designated initializer
- (id) initArrayNamed: (NSString *)varName isReference: (BOOL)isRef create: (BOOL)shouldCreate;

// Convenience initializers - these all expand to calls to the designated initializer above
- (id) initArrayNamed: (NSString *)varName isReference: (BOOL)isRef;
- (id) initArrayNamed: (NSString *)varName;
- (id) initNewArrayNamed: (NSString *)varName;
- (id) initArrayReferenceNamed: (NSString *)varName;
- (id) initNewArrayReferenceNamed: (NSString *)varName;

@end
