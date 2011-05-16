#pragma once
class ProfileLauncher
{
public:
	ProfileLauncher(void);
	~ProfileLauncher(void);
	__declspec(dllexport) int DisplayProfiles(char* pszBuffer);
};

