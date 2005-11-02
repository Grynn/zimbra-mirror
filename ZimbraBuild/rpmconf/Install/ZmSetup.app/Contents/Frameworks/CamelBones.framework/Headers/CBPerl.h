//
//  CBPerl.h
//  Camel Bones - a bare-bones Perl bridge for Objective-C
//  Originally written for ShuX
//
//  Copyright (c) 2002 Sherm Pendley. All rights reserved.
//

#import <Foundation/Foundation.h>

@class CBPerlScalar;
@class CBPerlArray;
@class CBPerlHash;
@class CBPerlObject;

extern char *perlArchVer;

@interface CBPerl : NSObject {
}

// The two methods used here to access the shared Perl interpreter
// instance differ only in memory management. Both will create the shared
// CBPerl object if necessary - although only init: retains it. Both will
// return nil if a CBPerl object does not exist, and for some reason could
// not be created.

// sharedPerl: will return the shared CBPerl object (without retaining
// it) if it exists. Otherwise it calls init: to create one, autoreleases
// it, and then returns it.

+ (CBPerl *) sharedPerl;

// initXS: A version of sharedPerl suitable for use within XS modules
+ (CBPerl *) sharedPerlXS;

// init: creates the shared CBPerl object if necessary, and then returns it.
- (id) init;

// initXS: A version of init suitable for use within XS modules
- (id) initXS;

- (void) useBundleLib: (NSBundle *)aBundle
		withArch: (NSString *)perlArchName
		forVersion: (NSString *)perlVersion;

// Evaluates a string of Perl code
- (id) eval: (NSString *)perlCode;

// Simple access methods to get/set perl variables of known type and name.

// Perl's built-in automatic variable creation is invoked if the named variable
// does not exist.

// These methods all call the built-in Perl type conversion functions, and
// thus share Perl's type conversion rules.

// Perl variable to/from an Int
- (long) varAsInt: (NSString *)perlVar;
- (void) setVar: (NSString *)perlVar toInt: (long)newValue;

// Perl variable as a float
- (double) varAsFloat: (NSString *)perlVar;
- (void) setVar: (NSString *)perlVar toFloat: (double)newValue;

// Perl variable as a string
- (NSString *) varAsString: (NSString *)perlVar;
- (void) setVar: (NSString *)perlVar toString: (NSString *)newValue;

// Some methods for accessing Perl's "use" pragmas
- (void) useLib: (NSString *)libPath;		// Adds libPath to the library search path
- (void) useModule: (NSString *)moduleName;     // Returns TRUE if the module loaded
- (void) useWarnings;				// Enables warnings
- (void) noWarnings;				// Disables warnings
- (void) useStrict;				// Enables strict mode
- (void) useStrict: (NSString *)options;	// Enables strict mode with options
- (void) noStrict;				// Disables strict mode
- (void) noStrict: (NSString *)options;		// Disables strict mode with options

// Methods to return autoreleased handles referencing named Perl
// variables, cast as the appropriate CamelBones type.
- (CBPerlScalar *) namedScalar: (NSString *)varName;
- (CBPerlArray *) namedArray: (NSString *)varName;
- (CBPerlHash *) namedHash: (NSString *)varName;
- (CBPerlObject *) namedObject: (NSString *)varName;

// Methods to export Objective-C objects into Perl's name space
- (void) exportArray: (NSArray *)array toPerlArray: (NSString *)arrayName;
- (void) exportDictionary: (NSDictionary *)dictionary toPerlHash: (NSString *)hashName;
- (void) exportObject: (id)object toPerlObject: (NSString *)objectName;

// Notification callback method for bundle loading
- (void) bundleDidLoad: (NSNotification *)notification;

// Class methods to initialize the stub framework and perl-specific bundle
+ (void) stubInit: (char*)archver;
+ (void) dylibInit;

@end

