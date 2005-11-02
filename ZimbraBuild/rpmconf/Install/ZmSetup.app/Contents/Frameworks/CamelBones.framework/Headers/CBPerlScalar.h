//
//  CBPerlScalar.h
//  Camel Bones - a bare-bones Perl bridge for Objective-C
//  Originally written for ShuX
//
//  Copyright (c) 2002 Sherm Pendley. All rights reserved.
//

#import <Foundation/Foundation.h>

@class CBPerlArray;
@class CBPerlHash;
@class CBPerlObject;

@interface CBPerlScalar : NSObject {

}

// Returns an autoreleased handle to a Perl scalar named varName.
// Returns nil of no such scalar exists.
+ (CBPerlScalar *) namedScalar: (NSString *)varName;

// Returns an autoreleased handle to a Perl scalar named varName, creating a
// new scalar if necessary with the default value def. If def is nil, a newly-
// created scalar will be initialized to Perl's undef.
// Returns nil if the named object does not exist, and could not be created
+ (CBPerlScalar *) namedScalar: (NSString *)varName withDefaultString: (NSString *)def;
+ (CBPerlScalar *) namedScalar: (NSString *)varName withDefaultInteger: (long)def;
+ (CBPerlScalar *) namedScalar: (NSString *)varName withDefaultDouble: (double)def;

// Returns an autoreleased handle to a Perl scalar named varName, creating a
// new scalar if necessary, that refers to target. If target is nil, the newly-
// created scalar will be initialized to Perl's undef.
// Returns nil if the named object does not exist, and could not be created
+ (CBPerlScalar *) namedReference: (NSString *)varName toArray: (CBPerlArray *)target;
+ (CBPerlScalar *) namedReference: (NSString *)varName toHash: (CBPerlHash *)target;
+ (CBPerlScalar *) namedReference: (NSString *)varName toObject: (CBPerlObject *)target;
+ (CBPerlScalar *) namedReference: (NSString *)varName toNativeObject: (NSObject *)target;

// Returns a handle to a Perl scalar named varName.
// Returns nil of no such scalar exists.
- (CBPerlScalar *) initNamedScalar: (NSString *)varName;

// Returns a handle to a Perl scalar named varName, creating a
// new scalar if necessary with the default value def. If def is nil, a newly-
// created scalar will be initialized to Perl's undef.
// Returns nil if the named object does not exist, and could not be created
- (CBPerlScalar *) initNamedScalar: (NSString *)varName withDefaultString: (NSString *)def;
- (CBPerlScalar *) initNamedScalar: (NSString *)varName withDefaultInteger: (long)def;
- (CBPerlScalar *) initNamedScalar: (NSString *)varName withDefaultDouble: (double)def;

// Returns a handle to a Perl scalar named varName, creating a
// new scalar if necessary, that refers to target. If target is nil, the newly-
// created scalar will be initialized to Perl's undef.
// Returns nil if the named object does not exist, and could not be created
- (CBPerlScalar *) initNamedReference: (NSString *)varName toArray: (NSArray *)target;
- (CBPerlScalar *) initNamedReference: (NSString *)varName toHash: (NSDictionary *)target;
- (CBPerlScalar *) initNamedReference: (NSString *)varName toObject: (CBPerlObject *)target;
- (CBPerlScalar *) initNamedReference: (NSString *)varName toNativeObject: (NSObject *)target;

// Query the scalar's properties
- (BOOL) isInteger;
- (BOOL) isFloat;
- (BOOL) isString;

- (BOOL) isRef;
- (BOOL) isArrayRef;
- (BOOL) isHashRef;
- (BOOL) isObjectRef;

- (BOOL) isTrue;
- (BOOL) isDefined;

- (int) refType;

// Get/set this variable with NSStrings
- (NSString *) getString;
- (void) setToString: (NSString *)newValue;

// Get/set this variable with ints and floats
- (long) getInt;
- (void) setToInt: (long)newValue;
- (double) getFloat;
- (void) setToFloat: (double)newValue;

// Get/set reference target
- (id) dereference;
- (void) setTargetToArray: (NSArray *)target;
- (void) setTargetToHash: (NSDictionary *)target;
- (void) setTargetToObject: (CBPerlObject *)target;
- (void) setTargetToNativeObject: (NSObject *)target;
- (void) setTargetToPointer: (void *)target;

// String-oriented regex functions
- (void) replace: (NSString *)pattern with:(NSString *)newString;
- (void) replace: (NSString *)pattern with:(NSString *)newString usingFlags:(NSString *)flags;

// Simple boolean test of a pattern match
// Similar to "m/foo/" in Perl.
- (BOOL) matches: (NSString *)pattern;
- (BOOL) matches: (NSString *)pattern usingFlags:(NSString *)flags;

// More advanced pattern match that returns sub-patterns in an array
// Similar to "m/([a-zA-Z]+)\s*([a-zA-Z]+)" in perl.
- (NSArray *) getMatches: (NSString *)pattern;
- (NSArray *) getMatches: (NSString *)pattern usingFlags:(NSString *)flags;

@end
