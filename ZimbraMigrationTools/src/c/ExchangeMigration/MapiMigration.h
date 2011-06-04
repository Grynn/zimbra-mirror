#include "CMigration.h"
#pragma once
class MapiMigration :
	public CMigration
{
public:
	MapiMigration(void);
	~MapiMigration(void);
		virtual void Connecttoserver() ;//do we need separate logon method or can we include it here..TBD
		virtual void ImportMail();
		virtual void ImportContacts();
		virtual void ImportCalendar();
};

