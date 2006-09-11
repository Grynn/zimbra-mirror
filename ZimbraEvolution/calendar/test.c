#include <stdio.h>

const char * blob = "-0700";

int
main()
{
	int hour, min;

	sscanf( blob, "%d %d", &hour, &min );

fprintf( stderr, "hour = %d, min = %d\n", hour, min );
}
