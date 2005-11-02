#import <Foundation/Foundation.h>

//
// Functions found in Runtime.m
//

// Create Perl wrappers for all registered ObjC classes
extern void REAL_CBWrapRegisteredClasses(void);

// Create Perl wrappers for a list of ObjC classes
extern void REAL_CBWrapNamedClasses(NSArray *names);

// Create a Perl wrapper for a single ObjC class
extern void REAL_CBWrapObjectiveCClass(Class aClass);

// Query class registration
extern BOOL REAL_CBIsClassRegistered(const char *className);

// Register a Perl class with the runtime
extern void REAL_CBRegisterClassWithSuperClass(const char *className, const char *superName);

// Query method registration
extern BOOL REAL_CBIsObjectMethodRegisteredForClass(SEL selector, Class class);
extern BOOL REAL_CBIsClassMethodRegisteredForClass(SEL selector, Class class);

// Perform method registration
extern void REAL_CBRegisterObjectMethodsForClass(const char *package, NSArray *methods, Class class);
extern void REAL_CBRegisterClassMethodsForClass(const char *package, NSArray *methods, Class class);

// Register class handler
extern void REAL_CBRegisterClassHandler(void);

//
// Functions found in NativeMethods.m
//

// Call a native class or object method
extern void* REAL_CBCallNativeMethod(void* target, SEL sel, void*args, BOOL isSuper);




// Globals.m
extern void REAL_CBWrapAllGlobals(void);

// Create Perl wrappers for one global variable of a specific type
extern BOOL REAL_CBWrapString(const char *varName, const char *pkgName);




// PerlMethods.m
// Get information about a Perl object
extern NSString* REAL_CBGetMethodNameForSelector(void* sv, SEL selector);
extern NSString* REAL_CBGetMethodArgumentSignatureForSelector(void* sv, SEL selector);
extern NSString* REAL_CBGetMethodReturnSignatureForSelector(void* sv, SEL selector);

// IMP registered as a native method
extern id REAL_CBPerlIMP(id self, SEL _cmd, ...);




// Wrappers.m
extern void* REAL_CBCreateWrapperObject(id obj);
extern void* REAL_CBCreateWrapperObjectWithClassName(id obj, NSString* className);

// Create a new Perl object blessed into the specified package
extern void* REAL_CBCreateObjectOfClass(NSString *className);

