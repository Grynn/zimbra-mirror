//
//  AppMain.h
//  CamelBones
//
//  Copyright (c) 2004 Sherm Pendley. All rights reserved.
//

// Default entry point for CamelBones applications, defaults to main.pl
extern int CBApplicationMain(int argc, const char *argv[]);

// Default entry point for CamelBones applications, allowing specification of script name
extern int CBApplicationMain2(const char *scriptName, int argc, const char *argv[]);

// Examine the system to determine Perl arch/version to use
extern char *CBGetPerlArchver();
