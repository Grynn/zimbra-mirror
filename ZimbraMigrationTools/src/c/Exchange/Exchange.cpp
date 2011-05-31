#include "common.h"
#include "commonMAPI.h"

extern "C" {

__declspec(dllexport) int DisplayProfiles(void) {
	MAPIInitialize(NULL);
    return 0;
}

}