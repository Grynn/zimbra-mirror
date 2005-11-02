use strict;
use warnings;

package CamelBones::Tests;
use CamelBones;

require Exporter;
our @ISA = qw(Exporter);
our $VERSION = '1.0.0';
our @EXPORT = qw(
				 cbt_isNil
				 cbt_char2string
				 cbt_uchar2string
				 cbt_int2string
				 cbt_uint2string
				 cbt_long2string
				 cbt_ulong2string
);
our @EXPORT_OK = (	
                );
our %EXPORT_TAGS = (
    'All'		=> [@EXPORT_OK],
);

require XSLoader;
XSLoader::load('CamelBones::Tests');

1;
