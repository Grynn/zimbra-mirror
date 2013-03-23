/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite CSharp Client
 * Copyright (C) 2011 VMware, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.3 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * 
 * ***** END LICENSE BLOCK *****
 */
#include "common.h"
#include "Exchange.h"
#include "MapiMigration.h"

MapiMigration::MapiMigration(void) {}

MapiMigration::~MapiMigration(void) {}

void MapiMigration::Connecttoserver()
{
    DisplayMessageBox(L"Connectiong to the server \n");
}

void MapiMigration::ImportMail()
{
    DisplayMessageBox(L"importing mails \n");
}

void MapiMigration::ImportContacts()
{
    DisplayMessageBox(L"importing contacts \n");
}

void MapiMigration::ImportCalendar()
{
    DisplayMessageBox(L"importing Calendar \n");
}
