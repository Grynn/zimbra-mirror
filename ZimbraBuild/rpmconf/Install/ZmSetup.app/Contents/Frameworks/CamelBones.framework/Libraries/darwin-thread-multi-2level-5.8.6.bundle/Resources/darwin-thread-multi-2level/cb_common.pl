#!/usr/bin/perl

package CBCommon;

use Config;
use Cwd 'abs_path';

my $CamelBonesPath = '.';

# Look up to four levels above
for (1..4) {
    last if (-d "$CamelBonesPath/CamelBones.framework");
    $CamelBonesPath = "../$CamelBonesPath";
}
my $CamelBones = "$CamelBonesPath/CamelBones.framework";

$CamelBones = abs_path($CamelBones);
$CamelBonesPath = abs_path($CamelBonesPath);

our %opts = (
    VERSION           => '1.0.0',

    PREREQ_PM         => {},

    AUTHOR         => 'Sherm Pendley <camelbones@dot-app.org>',

    XSOPT           => "-typemap $CamelBones/Resources/typemap",

    LIBS              => [ '-lobjc' ],
    INC               => ($ENV{'GNUSTEP_ROOT'} ne '') ?
             "-xobjective-c -Wno-import -I$ENV{'GNUSTEP_SYSTEM_ROOT'}/Library/Headers -I$ENV{'GNUSTEP_LOCAL_ROOT'}/Library/Headers -DGNUSTEP -fconstant-string-class=NSConstantString " :
             "-F$CamelBonesPath -ObjC ",
    dynamic_lib         => {
                        'OTHERLDFLAGS' =>
                            ($ENV{'GNUSTEP_ROOT'} ne '') ?
                            " -L$ENV{'GNUSTEP_SYSTEM_ROOT'}/Library/Libraries -L$ENV{'GNUSTEP_LOCAL_ROOT'}/Library/Libraries -lgnustep-base -lgnustep-gui -lCamelBones " :
                            " -framework Foundation -framework AppKit -framework CamelBones -F$CamelBonesPath -lobjc "
                        },
);

if ($ENV{'CFLAGS'}) {
    $opts{'INC'} .= $ENV{'CFLAGS'};
}
if ($ENV{'LDFLAGS'}) {
    $opts{'dynamic_lib'}->{'OTHERLDFLAGS'} .= $ENV{'LDFLAGS'};
};

1;
