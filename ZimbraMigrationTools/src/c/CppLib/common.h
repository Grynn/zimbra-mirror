/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite, Network Edition.
 * Copyright (C) 2006, 2007, 2009, 2010 Zimbra, Inc.  All Rights Reserved.
 * ***** END LICENSE BLOCK *****
 */
// Precompiled header file.

#pragma once

#ifndef WINVER
#define WINVER          0x0501
#endif
#ifndef _WIN32_WINNT
#define _WIN32_WINNT    WINVER
#endif

#define _WIN32_IE	0x0500

#define _CRT_SECURE_NO_WARNINGS
#define VC_EXTRALEAN
#define WIN32_LEAN_AND_MEAN

#include <windows.h>
#include <stdio.h>
#include <string.h>
#include <tchar.h>
#include <iostream>
#include <string>

#include <initguid.h>
#include "CGuid.h"
#include <atlbase.h>
#include <atlstr.h>

#define __STR2__(x) #x
#define __STR1__(x) __STR2__(x)
#define __LOC__ __FILE__ "("__STR1__(__LINE__)") : "