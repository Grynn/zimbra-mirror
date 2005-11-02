//
//  CBPerlHash.h
//  Camel Bones - a bare-bones Perl bridge for Objective-C
//  Originally written for ShuX
//
//  Copyright (c) 2002 Sherm Pendley. All rights reserved.

#import <Foundation/Foundation.h>


@interface CBPerlHash : NSMutableDictionary {
    void *_myHash;
}

// Required primitive methods
- (unsigned)count;
- (NSEnumerator *)keyEnumerator;
- (id)objectForKey:(id)aKey;

- (void)removeObjectForKey:(id)aKey;
- (void)setObject:(id)anObject forKey:(id)aKey;

// Extended methods

// Convenience creation methods returning autoreleased instances
+ (id) dictionaryNamed: (NSString *)varName isReference: (BOOL)isRef create: (BOOL)shouldCreate;
+ (id) dictionaryNamed: (NSString *)varName isReference: (BOOL)isRef;
+ (id) dictionaryNamed: (NSString *)varName;
+ (id) newDictionaryNamed: (NSString *)varName;
+ (id) dictionaryReferenceNamed: (NSString *)varName;
+ (id) newDictionaryReferenceNamed: (NSString *)varName;

// Designated initializer
- (id) initDictionaryNamed: (NSString *)varName isReference: (BOOL)isRef create: (BOOL)shouldCreate;

// Convenience initializers - these all expand to calls to the designated initializer above
- (id) initDictionaryNamed: (NSString *)varName isReference: (BOOL)isRef;
- (id) initDictionaryNamed: (NSString *)varName;
- (id) initNewDictionaryNamed: (NSString *)varName;
- (id) initDictionaryReferenceNamed: (NSString *)varName;
- (id) initNewDictionaryReferenceNamed: (NSString *)varName;

@end


@interface CBPerlHashKeyEnumerator : NSEnumerator {
    void *_myHash;
}
- (NSArray *)allObjects;
- (id)nextObject;
@end
