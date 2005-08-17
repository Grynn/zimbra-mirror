# Date2Time:
# Pass in an ascii date string and it prints out seconds-since-epoch
# and milliseconds-since-epoch
use Date::Parse;
use strict;

if ($ARGV[0] eq "") {
    print "USAGE: d2t DATE_STRING\n";
    exit(1);
}

my $argStr;
# there must be some extra-special easy perl way to do this...
my $i = 0;
do {
    $argStr = $argStr . $ARGV[$i] . " ";
    $i++;
} while($ARGV[$i] ne "");

my $val = str2time($argStr);
my $back = localtime($val);
my $msval = $val * 1000;
print "$val\n$msval\n$back\n";
