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
