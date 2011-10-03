#include "CMigration.h"
#pragma once

class MapiMigration: public CMigration
{
public:
    MapiMigration(void);
    ~MapiMigration(void);

    virtual void Connecttoserver();
    virtual void ImportMail();
    virtual void ImportContacts();
    virtual void ImportCalendar();
};
